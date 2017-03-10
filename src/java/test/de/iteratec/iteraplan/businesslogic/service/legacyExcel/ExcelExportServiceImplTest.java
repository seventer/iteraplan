/*
 * iteraplan is an IT Governance web application developed by iteratec, GmbH
 * Copyright (C) 2004 - 2014 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact iteratec GmbH headquarters at Inselkammerstr. 4
 * 82008 Munich - Unterhaching, Germany, or at email address info@iteratec.de.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "iteraplan" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by iteraplan".
 */
package de.iteratec.iteraplan.businesslogic.service.legacyExcel;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.MockTestDataFactory;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.Sequence;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.dto.ReleaseSuccessorDTO.SuccessionContainer;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 *
 */
public class ExcelExportServiceImplTest extends BaseTransactionalTestSupport {

  private static final Logger          LOGGER                 = Logger.getIteraplanLogger(ExcelExportServiceImplTest.class);
  private final MockTestDataFactory    mtdf                   = MockTestDataFactory.getInstance();
  private final ExcelExportServiceImpl excelExportServiceImpl = new ExcelExportServiceImpl();

  @Autowired
  private BuildingBlockTypeDAO         buildingBlockTypeDAO;
  @Autowired
  private AttributeTypeDAO             attributeTypeDAO;
  @Autowired
  private TestDataHelper2              testDataHelper;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.ExcelExportServiceImpl#getBuildingBlockReport(java.util.Locale, de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelAdditionalQueryData, java.util.List, de.iteratec.iteraplan.model.TypeOfBuildingBlock, java.lang.String, java.io.File)}.
   * More attributes, just one type.
   */
  @Test
  public void testGetBuildingBlockReport() {
    setUpData();

    Locale loc = UserContext.getCurrentLocale();
    List<Product> prodList = new ArrayList<Product>();

    // setting some data to search after later
    Product p1 = mtdf.getProductTestData();
    String description1 = "This is the description for p1.";
    p1.setDescription(description1);
    String user1 = "Captain Jack Sparrow";
    p1.setLastModificationUser(user1);
    String name1 = "Black Pearl";
    p1.setName(name1);

    Product p2 = mtdf.getProductTestData();
    String description2 = "This is the description for p2.";
    p2.setDescription(description2);
    String user2 = "Davy Jones";
    p2.setLastModificationUser(user2);
    String name2 = "Flying Dutchman";
    p2.setName(name2);

    prodList.add(p1);
    prodList.add(p2);

    ExportWorkbook res = excelExportServiceImpl.getBuildingBlockReport(loc, null, prodList, TypeOfBuildingBlock.PRODUCT, "url",
        TemplateType.EXCEL_2003);

    // printWb(res);
    assertTrue("String not found", wbContainsString(res, description1));
    assertTrue("String not found", wbContainsString(res, user1));
    assertTrue("String not found", wbContainsString(res, name1));
    assertTrue("String not found", wbContainsString(res, description2));
    assertTrue("String not found", wbContainsString(res, user2));
    assertTrue("String not found", wbContainsString(res, name2));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.ExcelExportServiceImpl#getBuildingBlockReport(java.util.Locale, de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelAdditionalQueryData, java.util.List, de.iteratec.iteraplan.model.TypeOfBuildingBlock, java.lang.String, java.io.File)}.
   * Goes through all types of BuildingBlock.
   */
  @Test
  public void testGetBuildingBlockReportAllTypes() {
    setUpData();
    TypeOfBuildingBlock tob;
    Locale loc = UserContext.getCurrentLocale();
    TemplateType templateType = TemplateType.EXCEL_2003;

    TypeOfBuildingBlock[] tobs = TypeOfBuildingBlock.values();
    for (int i = 0; i < tobs.length; i++) {
      tob = tobs[i];
      ExportWorkbook res = null;

      //unsupported types
      if (tob.equals(TypeOfBuildingBlock.BUSINESSMAPPING) || tob.equals(TypeOfBuildingBlock.INFORMATIONSYSTEM)
          || tob.equals(TypeOfBuildingBlock.TECHNICALCOMPONENT) || tob.equals(TypeOfBuildingBlock.TRANSPORT)
          || tob.equals(TypeOfBuildingBlock.TCR2IEASSOCIATION) || tob.equals(TypeOfBuildingBlock.ISR2BOASSOCIATION)
          || tob.equals(TypeOfBuildingBlock.DUMMY)) {
        continue;
      }

      // wildcards would be nice but can't be used since we're putting into the collection
      switch (tob) {
        case ARCHITECTURALDOMAIN:
          res = testArchitecturalDomain(loc, tob, templateType);
          break;
        case BUSINESSDOMAIN:
          res = testBusinessDomain(loc, tob, templateType);
          break;
        case BUSINESSFUNCTION:
          res = testBusinessFunction(loc, tob, templateType);
          break;
        case BUSINESSOBJECT:
          res = testBusinessObject(loc, tob, templateType);
          break;
        case BUSINESSPROCESS:
          res = testBusinessProcess(loc, tob, templateType);
          break;
        case BUSINESSUNIT:
          res = testBusinessUnit(loc, tob, templateType);
          break;
        case INFORMATIONSYSTEMDOMAIN:
          res = testInformationSystemDomain(loc, tob, templateType);
          break;
        case INFORMATIONSYSTEMINTERFACE:
          res = testInformationSystemInterface(loc, tob, templateType);
          break;
        case INFORMATIONSYSTEMRELEASE:
          res = testInformationSystemRelease(loc, tob, templateType);
          break;
        case INFRASTRUCTUREELEMENT:
          res = testInfrastructureElement(loc, tob, templateType);
          break;
        case PRODUCT:
          res = testProduct(loc, tob, templateType);
          break;
        case PROJECT:
          res = testProject(loc, tob, templateType);
          break;
        case TECHNICALCOMPONENTRELEASE:
          res = testTechnicalComponentRelease(loc, tob, templateType);
          break;
        default:
          // unsupported type of building block
          throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }

      assertTrue("String not found", wbContainsString(res, "strange vietnamese name starting with nguyen"));
    }
  }

  private ExportWorkbook testTechnicalComponentRelease(Locale loc, TypeOfBuildingBlock tob, TemplateType templateType) {
    List<TechnicalComponentRelease> list = new ArrayList<TechnicalComponentRelease>();
    TechnicalComponentRelease o = new TechnicalComponentRelease();
    String user = "strange vietnamese name starting with nguyen";
    o.setLastModificationUser(user);
    list.add(o);

    return excelExportServiceImpl.getBuildingBlockReport(loc, null, list, tob, "url", templateType);

  }

  private ExportWorkbook testProject(Locale loc, TypeOfBuildingBlock tob, TemplateType templateType) {
    List<Project> list = new ArrayList<Project>();
    Project o = new Project();
    String user = "strange vietnamese name starting with nguyen";
    o.setLastModificationUser(user);
    list.add(o);

    return excelExportServiceImpl.getBuildingBlockReport(loc, null, list, tob, "url", templateType);

  }

  private ExportWorkbook testProduct(Locale loc, TypeOfBuildingBlock tob, TemplateType templateType) {
    List<Product> list = new ArrayList<Product>();
    Product o = new Product();
    String user = "strange vietnamese name starting with nguyen";
    o.setLastModificationUser(user);
    list.add(o);

    return excelExportServiceImpl.getBuildingBlockReport(loc, null, list, tob, "url", templateType);

  }

  private ExportWorkbook testInfrastructureElement(Locale loc, TypeOfBuildingBlock tob, TemplateType templateType) {
    List<InfrastructureElement> list = new ArrayList<InfrastructureElement>();
    InfrastructureElement o = new InfrastructureElement();
    String user = "strange vietnamese name starting with nguyen";
    o.setLastModificationUser(user);
    list.add(o);

    return excelExportServiceImpl.getBuildingBlockReport(loc, null, list, tob, "url", templateType);

  }

  private ExportWorkbook testInformationSystemRelease(Locale loc, TypeOfBuildingBlock tob, TemplateType templateType) {
    List<InformationSystemRelease> list = new ArrayList<InformationSystemRelease>();
    InformationSystemRelease o = new InformationSystemRelease();
    String user = "strange vietnamese name starting with nguyen";
    o.setLastModificationUser(user);
    list.add(o);

    return excelExportServiceImpl.getBuildingBlockReport(loc, null, list, tob, "url", templateType);

  }

  private ExportWorkbook testInformationSystemInterface(Locale loc, TypeOfBuildingBlock tob, TemplateType templateType) {
    List<InformationSystemInterface> list = new ArrayList<InformationSystemInterface>();
    InformationSystemInterface o = new InformationSystemInterface();
    o.connect(new InformationSystemRelease(), new InformationSystemRelease());

    String user = "strange vietnamese name starting with nguyen";
    o.setLastModificationUser(user);
    list.add(o);

    return excelExportServiceImpl.getBuildingBlockReport(loc, null, list, tob, "url", templateType);

  }

  private ExportWorkbook testInformationSystemDomain(Locale loc, TypeOfBuildingBlock tob, TemplateType templateType) {
    List<InformationSystemDomain> list = new ArrayList<InformationSystemDomain>();
    InformationSystemDomain o = new InformationSystemDomain();
    String user = "strange vietnamese name starting with nguyen";
    o.setLastModificationUser(user);
    list.add(o);

    return excelExportServiceImpl.getBuildingBlockReport(loc, null, list, tob, "url", templateType);

  }

  private ExportWorkbook testBusinessUnit(Locale loc, TypeOfBuildingBlock tob, TemplateType templateType) {
    List<BusinessUnit> list = new ArrayList<BusinessUnit>();
    BusinessUnit o = new BusinessUnit();
    String user = "strange vietnamese name starting with nguyen";
    o.setLastModificationUser(user);
    list.add(o);

    return excelExportServiceImpl.getBuildingBlockReport(loc, null, list, tob, "url", templateType);

  }

  private ExportWorkbook testBusinessProcess(Locale loc, TypeOfBuildingBlock tob, TemplateType templateType) {
    List<BusinessProcess> list = new ArrayList<BusinessProcess>();
    BusinessProcess o = new BusinessProcess();
    String user = "strange vietnamese name starting with nguyen";
    o.setLastModificationUser(user);
    list.add(o);

    return excelExportServiceImpl.getBuildingBlockReport(loc, null, list, tob, "url", templateType);

  }

  private ExportWorkbook testBusinessObject(Locale loc, TypeOfBuildingBlock tob, TemplateType templateType) {
    List<BusinessObject> list = new ArrayList<BusinessObject>();
    BusinessObject o = new BusinessObject();
    String user = "strange vietnamese name starting with nguyen";
    o.setLastModificationUser(user);
    list.add(o);

    return excelExportServiceImpl.getBuildingBlockReport(loc, null, list, tob, "url", templateType);

  }

  private ExportWorkbook testBusinessFunction(Locale loc, TypeOfBuildingBlock tob, TemplateType templateType) {
    List<BusinessFunction> list = new ArrayList<BusinessFunction>();
    BusinessFunction o = new BusinessFunction();
    String user = "strange vietnamese name starting with nguyen";
    o.setLastModificationUser(user);
    list.add(o);

    return excelExportServiceImpl.getBuildingBlockReport(loc, null, list, tob, "url", templateType);

  }

  private ExportWorkbook testBusinessDomain(Locale loc, TypeOfBuildingBlock tob, TemplateType templateType) {
    List<BusinessDomain> list = new ArrayList<BusinessDomain>();
    BusinessDomain o = new BusinessDomain();
    String user = "strange vietnamese name starting with nguyen";
    o.setLastModificationUser(user);
    list.add(o);

    return excelExportServiceImpl.getBuildingBlockReport(loc, null, list, tob, "url", templateType);

  }

  private ExportWorkbook testArchitecturalDomain(Locale loc, TypeOfBuildingBlock tob, TemplateType templateType) {
    List<ArchitecturalDomain> list = new ArrayList<ArchitecturalDomain>();
    ArchitecturalDomain o = new ArchitecturalDomain();
    String user = "strange vietnamese name starting with nguyen";
    o.setLastModificationUser(user);
    list.add(o);

    return excelExportServiceImpl.getBuildingBlockReport(loc, null, list, tob, "url", templateType);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.ExcelExportServiceImpl#getReleaseSuccessionReport(java.util.Locale, java.util.List, de.iteratec.iteraplan.model.TypeOfBuildingBlock, java.lang.String)}.
   */
  @Test
  public void testGetReleaseSuccessionReport() {
    setUpData();

    Locale loc = UserContext.getCurrentLocale();
    List<SuccessionContainer<? extends Sequence<?>>> releaseSuccession = Lists.newArrayList();

    // setting some data to search after later
    InformationSystemRelease isr1 = mtdf.getInformationSystemReleaseTestData();
    String description1 = "This is the description for isr1.";
    isr1.setDescription(description1);
    String user1 = "Captain Jack Sparrow";
    isr1.setLastModificationUser(user1);
    SuccessionContainer<InformationSystemRelease> succContainer1 = new SuccessionContainer<InformationSystemRelease>();
    succContainer1.setRelease(isr1);
    succContainer1.setLevel(0);

    InformationSystemRelease isr2 = mtdf.getInformationSystemReleaseTestData();
    String description2 = "This is the description for isr2.";
    isr2.setDescription(description2);
    String user2 = "Captain Barbossa";
    isr2.setLastModificationUser(user2);
    SuccessionContainer<InformationSystemRelease> succContainer2 = new SuccessionContainer<InformationSystemRelease>();
    succContainer2.setRelease(isr2);
    succContainer2.setLevel(1);
    isr1.addSuccessor(isr2);

    InformationSystemRelease isr3 = mtdf.getInformationSystemReleaseTestData();
    String description3 = "This is the description for isr3.";
    isr3.setDescription(description3);
    String user3 = "Commodore Norrington";
    isr3.setLastModificationUser(user3);
    SuccessionContainer<InformationSystemRelease> succContainer3 = new SuccessionContainer<InformationSystemRelease>();
    succContainer3.setRelease(isr3);
    succContainer3.setLevel(2);
    isr2.addSuccessor(isr3);

    releaseSuccession.add(succContainer1);
    releaseSuccession.add(succContainer2);
    releaseSuccession.add(succContainer3);

    ExportWorkbook res = excelExportServiceImpl.getReleaseSuccessionReport(loc, releaseSuccession, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE,
        "url", TemplateType.EXCEL_2003);

    //    printWb(res);
    assertTrue("String not found", wbContainsString(res, description1));
    assertTrue("String not found", wbContainsString(res, user1));
    assertTrue("String not found", wbContainsString(res, description2));
    assertTrue("String not found", wbContainsString(res, user2));
    assertTrue("String not found", wbContainsString(res, description3));
    assertTrue("String not found", wbContainsString(res, user3));
  }

  /**
   * @param wb the Workbook to be scanned
   * @param expctContent the string we search for
   * 
   * @return true if one of the workbook's contains the searched string, false otherwise
   */
  private boolean wbContainsString(ExportWorkbook wb, String expctContent) {

    for (int i = 0; i < wb.getNumberOfSheets(); i++) {
      Sheet sheet = wb.getSheetAt(i);

      for (Iterator<Row> rIt = sheet.rowIterator(); rIt.hasNext();) {
        Row r = rIt.next();

        for (Iterator<Cell> cIt = r.cellIterator(); cIt.hasNext();) {
          Cell c = cIt.next();
          try {
            if (c.getStringCellValue().equals(expctContent)) {
              return true;
            }

          } catch (IllegalStateException e) {
            // if numeric cells are encountered
          }
        }
      }
    }
    return false;
  }

  /**
   * Method to print a workbook's cells-content. Handy if some errors show up.
   */
  @SuppressWarnings("unused")
  private void printWb(ExportWorkbook wb) {
    for (int i = 0; i < wb.getNumberOfSheets(); i++) {
      Sheet sheet = wb.getSheetAt(i);

      for (Iterator<Row> rIt = sheet.rowIterator(); rIt.hasNext();) {
        Row r = rIt.next();

        for (Iterator<Cell> cIt = r.cellIterator(); cIt.hasNext();) {
          Cell c = cIt.next();
          LOGGER.debug(c.toString());
        }
      }
    }
  }

  private void setUpData() {
    excelExportServiceImpl.setAttributeTypeDAO(attributeTypeDAO);
    excelExportServiceImpl.setBuildingBlockTypeDAO(buildingBlockTypeDAO);

  }

}

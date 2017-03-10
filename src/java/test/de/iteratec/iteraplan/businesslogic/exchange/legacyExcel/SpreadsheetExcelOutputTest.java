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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.businesslogic.reports.query.QueryTreeGenerator;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.type.QueryTypeHelper;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.ExportService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;


/**
 * Tests if the excel export of some building blocks with relations and attributes and the following re-import works.
 */
public class SpreadsheetExcelOutputTest extends BaseTransactionalTestSupport {

  private static final String                                           DESCRIPTION_SUFFIX = " description";
  private static final String                                           EXCEL_FILE_NAME    = "src/java/test/excelworkbooktest.xls";
  private List<InformationSystemRelease>                                originalIsrList    = CollectionUtils.arrayList();

  @Autowired
  private InitFormHelperService                                         initFormHelperService;

  @Autowired
  private AttributeTypeService                                          attributeTypeService;

  @Autowired
  private ExportService                                                 exportService;

  @Autowired
  private InformationSystemReleaseService                               informationSystemReleaseService;

  @Autowired
  private TestDataHelper2                                               testDataHelper;

  private TypeOfBuildingBlock                                           tob;
  private DynamicQueryFormData<?>                                       form;
  private List<DynamicQueryFormData<?>>                                 forms;
  private QueryTreeGenerator                                            qtg;
  private Node                                                          node;
  private List<AbstractPostprocessingStrategy<? extends BuildingBlock>> strategies;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Just tests if the export works without exceptions, also creates the excel sheet for the following import tests
   */
  @Test
  public void testExcelExport() {
    setUpData();

    tob = TypeOfBuildingBlock.fromPropertyString(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL);
    form = initFormHelperService.getReportForm((Type<?>) QueryTypeHelper.getTypeObject(tob));
    forms = Lists.newArrayList();
    forms.add(form);
    // Generate the tree of nodes for processing the query.
    qtg = new QueryTreeGenerator(UserContext.getCurrentLocale(), attributeTypeService);

    node = qtg.generateQueryTree(forms);
    strategies = Lists.newArrayList();

    beginTransaction();
    ExportWorkbook excelWorkbook = exportService.getExcelExportByType(node, null, strategies, form, "testURL", TemplateType.EXCEL_2003);

    try {
      OutputStream os = new FileOutputStream(EXCEL_FILE_NAME);
      excelWorkbook.writeTo(os);
      os.flush();
      os.close();
    } catch (FileNotFoundException e) {
      fail(e.getMessage());
    } catch (IOException e) {
      fail(e.getMessage());
    }

    File excelFile = new File(EXCEL_FILE_NAME);
    assertTrue(excelFile.exists());
    assertTrue("Temporary file could not be deleted.", excelFile.delete());
  }

  @Test
  /**
   * Performs an Excel export with an empty template file name (null)
   * The export should work as expected, because the filename will be overwritten with an empty string ("") and the default template should be used
   */
  public void testExcelExportWithNoTemplateFileName() {

    setUpData();

    tob = TypeOfBuildingBlock.fromPropertyString(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL);
    form = initFormHelperService.getReportForm((Type<?>) QueryTypeHelper.getTypeObject(tob));
    forms = Lists.newArrayList();
    forms.add(form);
    // Generate the tree of nodes for processing the query.
    qtg = new QueryTreeGenerator(UserContext.getCurrentLocale(), attributeTypeService);

    node = qtg.generateQueryTree(forms);
    strategies = Lists.newArrayList();

    beginTransaction();
    ExportWorkbook excelWorkbook = exportService.getExcelExportByType(node, null, strategies, form, "testURL", TemplateType.EXCEL_2003, null);

    try {
      OutputStream os = new FileOutputStream(EXCEL_FILE_NAME);
      excelWorkbook.writeTo(os);
      os.flush();
      os.close();
    } catch (FileNotFoundException e) {
      fail(e.getMessage());
    } catch (IOException e) {
      fail(e.getMessage());
    }

    File excelFile = new File(EXCEL_FILE_NAME);
    assertTrue(excelFile.exists());
    assertTrue("Temporary file could not be deleted.", excelFile.delete());

  }

  private void setUpData() {
    originalIsrList.clear();

    Map<AttributeType, List<AttributeValue>> attributes = createAttributeTypesAndValues();

    for (int i = 1; i <= 10; i++) {
      originalIsrList.addAll(createFilledISRs(i, attributes));
    }

    InformationSystemDomain isd = testDataHelper.createInformationSystemDomain("testISD", "testISD description");
    for (InformationSystemRelease isr : originalIsrList) {
      testDataHelper.addIsrToIsd(isr, isd);

      int partnerIndex = (originalIsrList.indexOf(isr) + 5) % originalIsrList.size();
      InformationSystemRelease isr2 = originalIsrList.get(partnerIndex);
      TechnicalComponentRelease tcr;
      if (!isr.getTechnicalComponentReleases().isEmpty()) {
        tcr = isr.getTechnicalComponentReleases().iterator().next();
      }
      else {
        tcr = null;
      }
      String desc = "ISI " + isr.getId() + "-" + isr2.getId() + DESCRIPTION_SUFFIX;

      InformationSystemInterface isi = testDataHelper.createInformationSystemInterface(isr, isr2, tcr, desc);
      assignAttributeValuesToBuildingBlock(isi, partnerIndex, attributes);
    }

    commit();
    beginTransaction();
    originalIsrList = informationSystemReleaseService.loadElementList();
    for (InformationSystemRelease isr : originalIsrList) {
      // touching the collections to load them despite lazy init, making them accessible for later asserts.
      isr.getBusinessObjects().size();
      isr.getBaseComponents().size();
      isr.getPredecessors().size();
      isr.getInformationSystemDomains().size();
      isr.getTechnicalComponentReleases().size();
      isr.getInfrastructureElements().size();
      isr.getProjects().size();
      isr.getAttributeValueAssignments().size();
      isr.getBusinessMappings().size();
    }
    commit();
  }

  private Map<AttributeType, List<AttributeValue>> createAttributeTypesAndValues() {
    Map<AttributeType, List<AttributeValue>> attributes = CollectionUtils.hashMap();

    AttributeTypeGroup defaultGroup = testDataHelper.createAttributeTypeGroup("Default Attribute Type Group",
        "Default Attribute Type Group Description");
    AttributeTypeGroup secondGroup = testDataHelper
        .createAttributeTypeGroup("Second Attribute Type Group", "Second Attribute Type Group Description");

    // Enum AT
    List<AttributeValue> avList = CollectionUtils.arrayList();
    EnumAT enumAT = testDataHelper.createEnumAttributeType("enumAT", "enumAT description", Boolean.TRUE, defaultGroup);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(enumAT);
    for (int i = 1; i < 6; i++) {
      avList.add(testDataHelper.createEnumAV("enumAV" + i, "enumAV" + i + DESCRIPTION_SUFFIX, enumAT));
    }
    attributes.put(enumAT, avList);

    // Number AT
    avList = CollectionUtils.arrayList();
    NumberAT numberAT = testDataHelper.createNumberAttributeType("numberAT", "numberAT description", defaultGroup);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(numberAT);
    attributes.put(numberAT, avList);

    // Date AT
    avList = CollectionUtils.arrayList();
    DateAT dateAT = testDataHelper.createDateAttributeType("dateAT", "dateAT description", defaultGroup);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(dateAT);
    attributes.put(dateAT, avList);

    // Text AT
    avList = CollectionUtils.arrayList();
    TextAT textAT = testDataHelper.createTextAttributeType("textAT", "textAT description", true, secondGroup);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(textAT);
    attributes.put(textAT, avList);

    return attributes;
  }

  private List<InformationSystemRelease> createFilledISRs(int count, Map<AttributeType, List<AttributeValue>> attributes) {
    InformationSystem is = testDataHelper.createInformationSystem("test IS " + count);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, "Test." + count);
    assignAttributeValuesToBuildingBlock(isr, count, attributes);
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is, "Test." + count + ".BETA");
    assignAttributeValuesToBuildingBlock(isr2, count * 2, attributes);

    testDataHelper.addChildToIsr(isr, isr2);
    testDataHelper.addSuccessorToIsr(isr2, isr);
    testDataHelper.addBaseComponentToISRelease(isr, isr2);

    createBusinessMappings(count, Lists.newArrayList(isr, isr2), attributes);

    addBOs(count, Lists.newArrayList(isr, isr2), attributes);

    addProjs(count, Collections.singletonList(isr2), attributes);

    addIEs(count, Collections.singletonList(isr2), attributes);

    addTCRs(count, Collections.singletonList(isr), attributes);

    return Lists.newArrayList(isr, isr2);
  }

  private void createBusinessMappings(int count, List<InformationSystemRelease> isrs, Map<AttributeType, List<AttributeValue>> attributes) {
    BusinessProcess bp = testDataHelper.createBusinessProcess("testBP" + count, "testBP" + count + DESCRIPTION_SUFFIX);
    assignAttributeValuesToBuildingBlock(bp, count, attributes);
    BusinessUnit bu = testDataHelper.createBusinessUnit("testBU" + count, "testBU" + count + DESCRIPTION_SUFFIX);
    assignAttributeValuesToBuildingBlock(bu, count, attributes);
    Product prod = testDataHelper.createProduct("testProd" + count, "testProd" + count + DESCRIPTION_SUFFIX);
    assignAttributeValuesToBuildingBlock(prod, count, attributes);
    BusinessFunction bf = testDataHelper.createBusinessFunction("testBF" + count, "testBF" + count + DESCRIPTION_SUFFIX);
    assignAttributeValuesToBuildingBlock(bf, count, attributes);
    HashSet<BusinessFunction> bfs = new HashSet<BusinessFunction>();
    bfs.add(bf);
    for (InformationSystemRelease isr : isrs) {
      isr.addBusinessFunctions(bfs);
      testDataHelper.createBusinessMapping(isr, bp, bu, prod);
      testDataHelper.createBusinessMapping(isr, bp, bu, prod.getParent());
      testDataHelper.createBusinessMapping(isr, bp, bu.getParent(), prod);
      testDataHelper.createBusinessMapping(isr, bp, bu.getParent(), prod.getParent());
      testDataHelper.createBusinessMapping(isr, bp.getParent(), bu, prod);
      testDataHelper.createBusinessMapping(isr, bp.getParent(), bu, prod.getParent());
      testDataHelper.createBusinessMapping(isr, bp.getParent(), bu.getParent(), prod);
    }
  }

  private void addBOs(int count, List<InformationSystemRelease> isrs, Map<AttributeType, List<AttributeValue>> attributes) {
    for (int i = 1; i <= 3; i++) {
      String name = "testBO" + count + "." + i;
      BusinessObject bo = testDataHelper.createBusinessObject(name, name + DESCRIPTION_SUFFIX);
      assignAttributeValuesToBuildingBlock(bo, count, attributes);
      for (InformationSystemRelease isr : isrs) {
        testDataHelper.addBusinessObjectToInformationSystem(isr, bo);
      }
    }
  }

  private void addProjs(int count, List<InformationSystemRelease> isrs, Map<AttributeType, List<AttributeValue>> attributes) {
    for (int i = 1; i <= 3; i++) {
      String name = "testProj" + count + "." + i;
      Project proj = testDataHelper.createProject(name, name + DESCRIPTION_SUFFIX);
      assignAttributeValuesToBuildingBlock(proj, count, attributes);
      for (InformationSystemRelease isr : isrs) {
        testDataHelper.addIsrToProject(isr, proj);
      }
    }
  }

  private void addIEs(int count, List<InformationSystemRelease> isrs, Map<AttributeType, List<AttributeValue>> attributes) {
    for (int i = 1; i <= 3; i++) {
      String name = "testIE" + count + "." + i;
      InfrastructureElement ie = testDataHelper.createInfrastructureElement(name, name + DESCRIPTION_SUFFIX);
      assignAttributeValuesToBuildingBlock(ie, count, attributes);
      for (InformationSystemRelease isr : isrs) {
        testDataHelper.addIeToIsr(isr, ie);
      }
    }
  }

  private void addTCRs(int count, List<InformationSystemRelease> isrs, Map<AttributeType, List<AttributeValue>> attributes) {
    for (int i = 1; i <= 3; i++) {
      TechnicalComponent tc = testDataHelper.createTechnicalComponent("testTC" + count + "." + i, true, true);
      TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, "1." + i, true);
      assignAttributeValuesToBuildingBlock(tcr, count, attributes);
      for (InformationSystemRelease isr : isrs) {
        testDataHelper.addTcrToIsr(isr, tcr);
      }
    }
  }

  private void assignAttributeValuesToBuildingBlock(BuildingBlock bb, int count, Map<AttributeType, List<AttributeValue>> attributes) {
    for (Map.Entry<AttributeType, List<AttributeValue>> entry : attributes.entrySet()) {
      AttributeType at = entry.getKey();
      List<AttributeValue> avList = entry.getValue();
      AttributeValue av = null;

      if (at instanceof EnumAT || at instanceof ResponsibilityAT) {
        int actualCount = count % avList.size();
        av = avList.get(actualCount);
      }
      else if (at instanceof NumberAT) {
        av = testDataHelper.createNumberAV(new BigDecimal(count * count), (NumberAT) at);
        avList.add(av);
      }
      else if (at instanceof DateAT) {
        av = testDataHelper.createDateAV(new Date(count * 100000000), (DateAT) at);
        avList.add(av);
      }
      else if (at instanceof TextAT) {
        av = testDataHelper.createTextAV("text " + count + "\nnewline", (TextAT) at);
        avList.add(av);
      }

      if (av != null) {
        testDataHelper.createAVA(bb, av);
      }
    }
  }
}

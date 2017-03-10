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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter;

import static org.junit.Assert.assertSame;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Test;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.ExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.TechnicalComponentReleaseExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.sorting.IdentityStringComparator;
import de.iteratec.iteraplan.model.user.User;


/**
 * This class is responsible for testing the content of the excel 
 * export for {@link de.iteratec.iteraplan.model.TechnicalComponentRelease}s.
 * 
 * @see TechnicalComponentReleaseReportTest
 */
public class TechnicalComponentReleaseSheetTest extends ExcelSheetTestBase {

  /**
   * Tests the content of the excel sheet export for {@link TechnicalComponentRelease}s in Excel 2003
   */
  @Test
  public void testTCRSheet2003() {
    ExportWorkbook context = new ExportWorkbook(Locale.GERMAN, TemplateType.EXCEL_2003);

    createAndVerifySheet(context);
  }

  /**
  * Tests the content of the excel sheet export for {@link TechnicalComponentRelease}s in Excel 2007
  */
  @Test
  public void testTCRSheet2007() {
    ExportWorkbook context = new ExportWorkbook(Locale.GERMAN, TemplateType.EXCEL_2007);

    createAndVerifySheet(context);
  }

  /**
   * @param context The Excel-Workbook to be tested
   */
  private void createAndVerifySheet(ExportWorkbook context) {
    ExporterDataUtil exporterDataUtil = ExporterDataUtil.getInstance();

    Set<User> subscribedUsers = exporterDataUtil.createUsers(0, SYSTEM_USER);

    Set<TechnicalComponentRelease> tcrSet = createTestTechnicalComponentReleases(exporterDataUtil, subscribedUsers, "testTCR", 10);

    BuildingBlockType bbt = new BuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);
    bbt.setId(Integer.valueOf(0));

    TechnicalComponentReleaseExcelSheet sheet = new TechnicalComponentReleaseExcelSheet(tcrSet, context, new ArrayList<AttributeType>(), null, bbt,
        TEST_URL);
    sheet.createSheet();

    // is required because of the order of the entities to be compared with the received contents.
    // All entities are sorted before adding them to the result sheet.
    List<TechnicalComponentRelease> sortedTCRs = ExcelHelper.sortEntities(tcrSet, new IdentityStringComparator());

    assertSame(Integer.valueOf(context.getNumberOfSheets()), Integer.valueOf(2));
    Sheet resultSheet = context.getSheetAt(1);

    this.verifyRowContent(this.retrieveNextRow(resultSheet), MessageAccess.getStringOrNull(ExcelReportTestBase.TCR_SHEET_KEY, context.getLocale()));

    List<String> expContent = exporterDataUtil.initExpectedTCRHeaders(context.getLocale());

    this.verifyRowContent(this.retrieveNextRow(resultSheet), expContent.toArray());

    for (TechnicalComponentRelease entity : sortedTCRs) {
      expContent = createTcrExpRowContent(context, entity);
      this.verifyRowContent(this.retrieveNextRow(resultSheet), expContent.toArray());
    }
  }

  private List<String> createTcrExpRowContent(ExportWorkbook context, TechnicalComponentRelease entity) {
    List<String> expContent = new ArrayList<String>();
    expContent.add(String.format("HYPERLINK(\"testURL/show/technicalcomponent/%d\",%d)", entity.getId(), entity.getId()));
    expContent.add(entity.getName());
    expContent.add(entity.getPredecessors().iterator().next().getName());
    expContent.add(entity.getSuccessors().iterator().next().getName());
    expContent.add(ExcelHelper.concatMultipleHierarchicalNames(entity.getBaseComponents(), ExcelSheet.IN_LINE_SEPARATOR));
    expContent.add(ExcelHelper.concatMultipleHierarchicalNames(entity.getParentComponents(), ExcelSheet.IN_LINE_SEPARATOR));
    expContent.add(entity.getDescription());
    SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    formatter.applyPattern(DATE_FORMAT_PATTERN);
    expContent.add(formatter.format(entity.getRuntimePeriod().getStart()));
    expContent.add(formatter.format(entity.getRuntimePeriod().getEnd()));
    expContent.add(MessageAccess.getStringOrNull(entity.getTypeOfStatusAsString(), context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull("global.no", context.getLocale()));
    expContent.add(entity.getLastModificationUser());
    expContent.add(formatter.format(entity.getLastModificationTime()));
    expContent.add(entity.getSubscribedUsers().iterator().next().getIdentityString());
    expContent.add(entity.getArchitecturalDomains().iterator().next().getName());
    expContent.add(entity.getInformationSystemReleases().iterator().next().getName());
    InformationSystemInterface isi = entity.getInformationSystemInterfaces().iterator().next();
    expContent.add(isi.getInformationSystemReleaseA().getName() + " <=> " + isi.getInformationSystemReleaseB().getName());
    expContent.add(entity.getInfrastructureElements().iterator().next().getName());
    return expContent;
  }

  private Set<TechnicalComponentRelease> createTestTechnicalComponentReleases(ExporterDataUtil exporterDataUtil, Set<User> subscribedUsers,
                                                                              String name, int entitiesNumber) {
    Set<TechnicalComponentRelease> entities = new HashSet<TechnicalComponentRelease>();
    for (int i = 0; i < entitiesNumber; i++) {
      Set<TechnicalComponentRelease> tcrs = exporterDataUtil.createTCRs(i + 1, name, 2, 3, 4);
      List<TechnicalComponentRelease> sortedTCRs = ExcelHelper.sortEntities(tcrs, null);
      Iterator<TechnicalComponentRelease> iter = sortedTCRs.iterator();
      TechnicalComponentRelease entity = iter.next();
      entity.addPredecessor(iter.next());
      entity.addSuccessor(iter.next());
      entity.setDescription(TEST_DESCRIPTION);
      entity.setLastModificationUser(TEST_USER);
      entity.setLastModificationTime(this.getLastModDate());
      entity.setSubscribedUsers(subscribedUsers);

      entity.setRuntimePeriod(new RuntimePeriod(this.getLastModDate(), this.getLastModDate()));// a random period
      entity.setTypeOfStatus(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.CURRENT);
      InformationSystemInterface isi = new InformationSystemInterface();
      isi.setId(Integer.valueOf(4));
      isi.setTechnicalComponentReleases(tcrs);
      Set<InformationSystemRelease> isrs = createISRs(exporterDataUtil, name, i, 6, 7);
      Iterator<InformationSystemRelease> isrsIter = isrs.iterator();
      isi.setInformationSystemReleaseA(isrsIter.next());
      isi.setInformationSystemReleaseB(isrsIter.next());
      Set<InformationSystemInterface> isis = new HashSet<InformationSystemInterface>();
      isis.add(isi);
      entity.setInformationSystemInterfaces(isis);

      // it does not matter which base components are used for the test
      entity.setBaseComponents(tcrs);
      entity.setArchitecturalDomains(exporterDataUtil.createADs(i + 1, name + " - ArchitecturalDomain"));
      entity.setInformationSystemReleases(createISRs(exporterDataUtil, name, i, 5));
      entity.setInfrastructureElementAssociations(createIEAssociations(entity, i + 1, name));

      TechnicalComponent tc = new TechnicalComponent();
      tc.setId(Integer.valueOf(i));
      tc.setName(name);
      tc.addRelease(entity);

      entities.add(entity);
    }
    return entities;
  }

  private Set<Tcr2IeAssociation> createIEAssociations(TechnicalComponentRelease entity, int runningNumber, String name) {
    ExporterDataUtil exporterDataUtil = ExporterDataUtil.getInstance();
    Set<InfrastructureElement> infraElements = exporterDataUtil.createIEs(runningNumber, name + " - InfrastructureElements");
    Set<Tcr2IeAssociation> ieAssociations = new HashSet<Tcr2IeAssociation>();
    for (InfrastructureElement ie : infraElements) {
      Tcr2IeAssociation assoc = new Tcr2IeAssociation(entity, ie);
      ieAssociations.add(assoc);
    }
    return ieAssociations;
  }
}

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
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Test;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.ExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.InformationSystemReleaseExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.sorting.IdentityStringComparator;
import de.iteratec.iteraplan.model.user.User;

/**
 * This class is responsible for testing the content of the excel 
 * export for {@link de.iteratec.iteraplan.model.InformationSystemRelease}s.
 * 
 * @see InformationSystemReleaseReportTest
 */
public class InformationSystemReleaseSheetTest extends ExcelSheetTestBase {

  /**
   * Tests the content of the excel sheet export for {@link InformationSystemRelease}s in Excel 2003
   */
  @Test
  public void testISRSheet2003() {
    ExportWorkbook context = new ExportWorkbook(Locale.GERMAN, TemplateType.EXCEL_2003);

    createAndVerifySheet(context);
  }

  /**
  * Tests the content of the excel sheet export for {@link InformationSystemRelease}s in Excel 2007
  */
  @Test
  public void testISRSheet2007() {
    ExportWorkbook context = new ExportWorkbook(Locale.GERMAN, TemplateType.EXCEL_2007);

    createAndVerifySheet(context);
  }

  /**
   * @param context The Excel-Workbook to be tested
   */
  private void createAndVerifySheet(ExportWorkbook context) {
    ExporterDataUtil exporterDataUtil = ExporterDataUtil.getInstance();

    Set<User> subscribedUsers = exporterDataUtil.createUsers(0, SYSTEM_USER);

    String name = "testISR";

    Set<InformationSystemRelease> isrSet = createTestInfoSystemReleases(exporterDataUtil, subscribedUsers, name, 10);

    BuildingBlockType bbt = new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    bbt.setId(Integer.valueOf(0));

    InformationSystemReleaseExcelSheet sheet = new InformationSystemReleaseExcelSheet(isrSet, context, new ArrayList<AttributeType>(), null, bbt,
        TEST_URL);
    sheet.createSheet();

    // is required because of the order of the entities to be compared with the received contents.
    // All entities are sorted before adding them to the result sheet.
    List<InformationSystemRelease> sortedISRs = ExcelHelper.sortEntities(isrSet, new IdentityStringComparator());

    assertSame(Integer.valueOf(context.getNumberOfSheets()), Integer.valueOf(2));
    Sheet resultSheet = context.getSheetAt(1);

    this.verifyRowContent(this.retrieveNextRow(resultSheet), MessageAccess.getStringOrNull(ExcelReportTestBase.ISR_SHEET_KEY, context.getLocale()));
    this.verifyRowContent(this.retrieveNextRow(resultSheet), exporterDataUtil.initExpectedISRHeaders(context.getLocale()).toArray());

    for (InformationSystemRelease entity : sortedISRs) {
      List<String> expContent = createIsrExpRowContent(context, name, entity);
      this.verifyRowContent(this.retrieveNextRow(resultSheet), expContent.toArray());
    }
  }

  private List<String> createIsrExpRowContent(ExportWorkbook context, String name, InformationSystemRelease entity) {
    List<String> expContent = new ArrayList<String>();
    expContent.add(String.format("HYPERLINK(\"testURL/show/informationsystem/%d\",%d)", entity.getId(), entity.getId()));
    expContent.add(entity.getName());
    expContent.add(entity.getHierarchicalName());
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
    expContent.add(entity.getLastModificationUser());
    expContent.add(formatter.format(entity.getLastModificationTime()));
    expContent.add(entity.getSubscribedUsers().iterator().next().getIdentityString());
    expContent.add(MessageAccess.getStringOrNull(entity.getSealState().getValue(), context.getLocale()));
    expContent.add("");
    expContent.add(entity.getBusinessFunctions().iterator().next().getName());
    expContent.add("(" + name + " - BusinessProcess / " + name + " - Product / " + name + " - BusinessUnit)");
    expContent.add(entity.getBusinessObjects().iterator().next().getName());
    expContent.add(entity.getInformationSystemDomains().iterator().next().getName());
    // a predecessor has been taken as connection isr
    expContent.add(entity.getPredecessors().iterator().next().getName());
    expContent.add(entity.getTechnicalComponentReleases().iterator().next().getName());
    expContent.add(entity.getInfrastructureElements().iterator().next().getName());
    expContent.add(entity.getProjects().iterator().next().getName());
    return expContent;
  }

  private Set<InformationSystemRelease> createTestInfoSystemReleases(ExporterDataUtil exporterDataUtil, Set<User> subscribedUsers, String name,
                                                                     int entitiesNumber) {
    Set<InformationSystemRelease> entities = new HashSet<InformationSystemRelease>();
    for (int i = 0; i < entitiesNumber; i++) {
      InformationSystemRelease entity = exporterDataUtil.initISR(name, this.getLastModDate(), i);
      entity.setSubscribedUsers(subscribedUsers);
      entities.add(entity);

    }
    return entities;
  }
}

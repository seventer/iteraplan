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

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.BusinessUnitExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.sorting.IdentityStringComparator;
import de.iteratec.iteraplan.model.user.User;

/**
 * This class is responsible for testing the content of the excel 
 * export for {@link de.iteratec.iteraplan.model.BusinessUnit}s.
 * 
 * @see BusinessUnitReportTest
 */
public class BusinessUnitSheetTest extends ExcelSheetTestBase {
  
  /**
   * Tests the content of the excel sheet export for {@link BusinessUnit}s in Excel 2003
   */
  @Test
  public void testBUSheet2003() {
    ExportWorkbook context = new ExportWorkbook(Locale.GERMAN, TemplateType.EXCEL_2003);

    createAndVerifySheet(context);
  }

  /**
  * Tests the content of the excel sheet export for {@link BusinessUnit}s in Excel 2007
  */
  @Test
  public void testBUSheet2007() {
    ExportWorkbook context = new ExportWorkbook(Locale.GERMAN, TemplateType.EXCEL_2007);

    createAndVerifySheet(context);
  }

  /**
   * @param context The Excel-Workbook to be tested
   */
  private void createAndVerifySheet(ExportWorkbook context) {
    ExporterDataUtil exporterDataUtil = ExporterDataUtil.getInstance();

    Set<User> subscribedUsers = exporterDataUtil.createUsers(0, SYSTEM_USER);

    String name = "testBU";
    String bpName = name + " - BusinessProcess";
    String prodName = name + " - Product";

    Set<BusinessUnit> buSet = createTestBusinessUnits(exporterDataUtil, subscribedUsers, name, bpName, prodName, 10);

    BuildingBlockType bbt = new BuildingBlockType(TypeOfBuildingBlock.BUSINESSUNIT);
    bbt.setId(Integer.valueOf(0));

    BusinessUnitExcelSheet sheet = new BusinessUnitExcelSheet(buSet, context, new ArrayList<AttributeType>(), null, bbt, TEST_URL);
    sheet.createSheet();

    // is required because of the order of the entities to be compared with the received contents.
    // All entities are sorted before adding them to the result sheet.
    List<BusinessUnit> sortedBUs = ExcelHelper.sortEntities(buSet, new IdentityStringComparator());

    assertSame(Integer.valueOf(context.getNumberOfSheets()), Integer.valueOf(2));
    Sheet resultSheet = context.getSheetAt(1);

    this.verifyRowContent(this.retrieveNextRow(resultSheet), MessageAccess.getStringOrNull(ExcelReportTestBase.BU_SHEET_KEY, context.getLocale()));

    List<String> expContent = exporterDataUtil.initCommonExpectedHeaders(context.getLocale(), ExcelReportTestBase.BU_SHEET_KEY);

    expContent.add(MessageAccess.getStringOrNull(BD_PLURAL_MESSAGE_KEY, context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull(BP_PLURAL_MESSAGE_KEY, context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull("global.products", context.getLocale()));

    String process = MessageAccess.getStringOrNull("businessProcess.singular", context.getLocale());
    String product = MessageAccess.getStringOrNull("global.product", context.getLocale());
    String isr = MessageAccess.getStringOrNull("informationSystemRelease.singular", context.getLocale());
    String bm = MessageAccess.getStringOrNull("businessMapping.plural", context.getLocale());
    String businessMappingHeaderNameExtended = getExtBMHeaderName(isr, product, process, bm);
    expContent.add(businessMappingHeaderNameExtended);

    expContent.add(MessageAccess.getStringOrNull("informationSystemRelease.plural", context.getLocale()));

    this.verifyRowContent(this.retrieveNextRow(resultSheet), expContent.toArray());

    for (BusinessUnit entity : sortedBUs) {
      expContent = createBuExpRowContent(name, bpName, prodName, entity);
      this.verifyRowContent(this.retrieveNextRow(resultSheet), expContent.toArray());
    }
  }

  private List<String> createBuExpRowContent(String name, String bpName, String prodName, BusinessUnit entity) {
    List<String> expContent = createBasicHierarchicalEntityContent("HYPERLINK(\"testURL/show/businessunit/%d\",%d)", entity);

    expContent.add(entity.getLastModificationUser());
    SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    formatter.applyPattern(DATE_FORMAT_PATTERN);
    expContent.add(formatter.format(entity.getLastModificationTime()));
    expContent.add(entity.getSubscribedUsers().iterator().next().getIdentityString());
    expContent.add(entity.getBusinessDomains().iterator().next().getName());
    expContent.add(bpName);
    expContent.add(prodName);
    expContent.add("(" + name + " - BusinessProcess / " + name + " - Product / " + name + " - InformationSystemRelease # 3)");
    expContent.add(name + " - InformationSystemRelease # 3");
    return expContent;
  }

  private Set<BusinessUnit> createTestBusinessUnits(ExporterDataUtil exporterDataUtil, Set<User> subscribedUsers, String name, String bpName,
                                                    String prodName, int entitiesNumber) {
    Set<BusinessUnit> entities = new HashSet<BusinessUnit>();
    for (int i = 0; i < entitiesNumber; i++) {
      BusinessUnit entity = new BusinessUnit();
      entity.setId(Integer.valueOf(i));
      entity.setName(name);
      entity.setDescription(TEST_DESCRIPTION);
      entity.setLastModificationUser(TEST_USER);
      entity.setLastModificationTime(this.getLastModDate());
      entity.setSubscribedUsers(subscribedUsers);

      entity.addBusinessDomains(exporterDataUtil.createBDs(i + 1, name + " - BusinessDomain"));
      BusinessMapping bm = new BusinessMapping();
      bm.setBusinessProcess(exporterDataUtil.createBPs(i + 1, bpName).iterator().next());
      bm.setProduct(exporterDataUtil.createProducts(i + 1, prodName).iterator().next());
      bm.setInformationSystemRelease(createISRs(exporterDataUtil, name, i, 3).iterator().next());
      entity.addBusinessMapping(bm);
      entities.add(entity);
    }
    return entities;
  }
}

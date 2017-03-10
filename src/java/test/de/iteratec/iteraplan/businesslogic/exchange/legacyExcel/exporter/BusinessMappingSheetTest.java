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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Test;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.BusinessMappingExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.sorting.IdentityStringComparator;

/**
 * This class is responsible for testing the content of the excel 
 * export for {@link de.iteratec.iteraplan.model.BusinessMapping}s.
 */
public class BusinessMappingSheetTest extends ExcelSheetTestBase {

  /**
   * Tests the content of the excel sheet export for {@link BusinessMapping}s in Excel 2003
   */
  @Test
  public void testBusinessMappingSheet2003() {
    ExportWorkbook context = new ExportWorkbook(Locale.GERMAN, TemplateType.EXCEL_2003);

    createAndVerifySheet(context);
  }

  /**
  * Tests the content of the excel sheet export for {@link BusinessMapping}s in Excel 2007
  */
  @Test
  public void testBusinessMappingSheet2007() {
    ExportWorkbook context = new ExportWorkbook(Locale.GERMAN, TemplateType.EXCEL_2007);

    createAndVerifySheet(context);
  }

  /**
   * @param context The Excel-Workbook to be tested
   */
  private void createAndVerifySheet(ExportWorkbook context) {
    ExporterDataUtil exporterDataUtil = ExporterDataUtil.getInstance();

    Set<BusinessMapping> bmSet = createTestBusinessMappings(exporterDataUtil, "testBusinessMapping", 10);

    BuildingBlockType bbt = new BuildingBlockType(TypeOfBuildingBlock.BUSINESSMAPPING);
    bbt.setId(Integer.valueOf(0));

    BusinessMappingExcelSheet sheet = new BusinessMappingExcelSheet(bmSet, context, new ArrayList<AttributeType>(), null, bbt, TEST_URL);
    sheet.createSheet();

    // is required because of the order of the entities to be compared with the received contents.
    // All entities are sorted before adding them to the result sheet.
    List<BusinessMapping> sortedBMs = ExcelHelper.sortEntities(bmSet, new IdentityStringComparator());

    assertSame(Integer.valueOf(context.getNumberOfSheets()), Integer.valueOf(2));
    Sheet resultSheet = context.getSheetAt(1);

    this.verifyRowContent(this.retrieveNextRow(resultSheet), MessageAccess.getStringOrNull(ExcelReportTestBase.BM_SHEET_KEY, context.getLocale()));

    List<String> expContent = new ArrayList<String>();

    expContent.add(MessageAccess.getStringOrNull("global.id", context.getLocale()) + "(IS)");

    expContent.add(MessageAccess.getStringOrNull(BP_PLURAL_MESSAGE_KEY, context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull("global.products", context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull(BU_PLURAL_MESSAGE_KEY, context.getLocale()));
    expContent.add(MessageAccess.getStringOrNull(ISR_PLURAL_MESSAGE_KEY, context.getLocale()));

    this.verifyRowContent(this.retrieveNextRow(resultSheet), expContent.toArray());

    for (BusinessMapping entity : sortedBMs) {
      expContent = createBmExpRowContent(entity);
      this.verifyRowContent(this.retrieveNextRow(resultSheet), expContent.toArray());
    }
  }

  private List<String> createBmExpRowContent(BusinessMapping entity) {
    List<String> expContent = new ArrayList<String>();
    expContent.add("HYPERLINK(\"testURL/show/informationsystem/" + entity.getInformationSystemRelease().getId() + "\","
        + entity.getInformationSystemRelease().getId() + ")");
    expContent.add(entity.getBusinessProcess().getName());
    expContent.add(entity.getProduct().getName());
    expContent.add(entity.getBusinessUnit().getName());
    expContent.add(entity.getInformationSystemRelease().getName());
    return expContent;
  }

  private Set<BusinessMapping> createTestBusinessMappings(ExporterDataUtil exporterDataUtil, String name, int entitiesNumber) {
    Set<BusinessMapping> entities = new HashSet<BusinessMapping>();
    for (int i = 0; i < entitiesNumber; i++) {
      BusinessMapping entity = new BusinessMapping();
      entity.setId(Integer.valueOf(i));
      entity.setLastModificationUser(TEST_USER);
      entity.setLastModificationTime(this.getLastModDate());
      entity.setBusinessProcess(exporterDataUtil.createBPs(i + 1, name + " - BusinessProcess").iterator().next());
      entity.setProduct(exporterDataUtil.createProducts(i + 1, name + " - Product").iterator().next());
      entity.setBusinessUnit(exporterDataUtil.createBUs(i + 1, name + " - BusinessUnit").iterator().next());
      entity.setInformationSystemRelease(createISRs(exporterDataUtil, name, i, 6).iterator().next());

      entities.add(entity);
    }
    return entities;
  }
}

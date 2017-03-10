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
package de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.exporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.ExcelExportTestUtils;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;


/**
 * Tests the generation of the Timeseries Excel export.
 */
public class TimeseriesExcelExportServiceImplTest extends BaseTransactionalTestSupport {

  /** Sheets in generated timeseries template files are ordered, see {@link TimeseriesExcelExportService#exportTimeseriesDataExcel2003()} */
  private static final String[]        EXPECTED_SHEET_NAMES = { "Introduction", "PROD-enum", "PROD-num", "AD-enum" };

  @Autowired
  private TestDataHelper2              testDataHelper;
  @Autowired
  private TimeseriesExcelExportService timeseriesExportService;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());

    ArchitecturalDomain ad = testDataHelper.createArchitecturalDomain("ad", "ad desc");
    Product prod = testDataHelper.createProduct("prod", "prod desc");

    EnumAT enumAT = testDataHelper.createEnumAttributeType("enum", "enum desc", Boolean.FALSE, testDataHelper.getDefaultATG());
    enumAT.setTimeseries(true);
    enumAT.addBuildingBlockTypeTwoWay(ad.getBuildingBlockType());
    enumAT.addBuildingBlockTypeTwoWay(prod.getBuildingBlockType());

    NumberAT numAT = testDataHelper.createNumberAttributeType("num", "num desc", testDataHelper.getDefaultATG());
    numAT.setTimeseries(true);
    numAT.addBuildingBlockTypeTwoWay(prod.getBuildingBlockType());

    NumberAT nonTimeseriesNumberAT = testDataHelper.createNumberAttributeType("num2", "num2 desc", testDataHelper.getDefaultATG());
    nonTimeseriesNumberAT.addBuildingBlockTypeTwoWay(ad.getBuildingBlockType());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.exporter.TimeseriesExcelExportServiceImpl#generateTemplateExcel2003()}.
   * @throws IOException if an exception occurs
   */
  @Test
  public void testGenerateTemplateExcel2003() throws IOException {
    Workbook workbook = timeseriesExportService.generateTemplateExcel2003();

    assertTemplateSheets(workbook);

    File tempFile = File.createTempFile("iteraplanTimeseriesExcelTemplate", "xls");
    ExcelExportTestUtils.persistWorkbook(workbook, tempFile);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.exporter.TimeseriesExcelExportServiceImpl#generateTemplateExcel2007()}.
   * @throws IOException 
   */
  @Test
  public void testGenerateTemplateExcel2007() throws IOException {
    Workbook workbook = timeseriesExportService.generateTemplateExcel2007();

    assertTemplateSheets(workbook);

    File tempFile = File.createTempFile("iteraplanTimeseriesExcelTemplate", "xlsx");
    ExcelExportTestUtils.persistWorkbook(workbook, tempFile);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.exporter.TimeseriesExcelExportServiceImpl#exportTimeseriesDataExcel2003()}.
   */
  @Test
  @Ignore
  // method to test not yet implemented
  public void testExportTimeseriesDataExcel2003() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.exporter.TimeseriesExcelExportServiceImpl#exportTimeseriesDataExcel2007()}.
   */
  @Test
  @Ignore
  // method to test not yet implemented
  public void testExportTimeseriesDataExcel2007() {
    fail("Not yet implemented");
  }

  private void assertTemplateSheets(Workbook workbook) {
    int actualNumberOfSheets = workbook.getNumberOfSheets();
    assertEquals(4, actualNumberOfSheets);
    for (int i = 0; i < actualNumberOfSheets; i++) {
      Sheet sheet = workbook.getSheetAt(i);
      assertEquals(EXPECTED_SHEET_NAMES[i], sheet.getSheetName());
    }
  }

}

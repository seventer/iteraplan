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
package de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.TimeseriesService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.Timeseries;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesSerializer;


public class TimeseriesExcelImporterTest extends BaseTransactionalTestSupport {
  private static final String         EXCEL_TEST_FILES = "excel/testFiles/timeseriesExcel/";

  @Autowired
  private TestDataHelper2             testDataHelper;
  @Autowired
  private AttributeValueService       avService;
  @Autowired
  private AttributeTypeService        atService;
  @Autowired
  private BuildingBlockServiceLocator bbServiceLocator;
  @Autowired
  private TimeseriesService           timeseriesService;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    timeseriesService.setTimeseriesEnabled(true);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.importer.TimeseriesExcelImporter#importExcel()} with xls-File.
   */
  @Test
  public void testImportExcel2003() throws IOException {

    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "timeseriesImportTest.xls");
    testImportExcel(excel);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.importer.TimeseriesExcelImporter#importExcel()} with xlsx-File.
   */
  @Test
  public void testImportExcel2007() throws IOException {

    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "timeseriesImportTest.xlsx");
    testImportExcel(excel);
  }

  private void testImportExcel(Resource excel) throws IOException {
    TestData testData = setUpData();

    TimeseriesExcelImporter importer = new TimeseriesExcelImporter(atService, bbServiceLocator, timeseriesService);

    assertTrue(importer.importExcel(ExcelUtils.openExcelFile(excel.getInputStream())));
    assertTrue(importer.getErrorMessages().isEmpty());

    List<Integer> bbIds = Lists.newArrayList(testData.isr1.getId(), testData.isr2.getId());
    HashBucketMap<Integer, AttributeValue> isrAVs = avService.getBuildingBlockIdsToConnectedAttributeValues(bbIds, testData.numAT1.getId());

    List<AttributeValue> isr1AVs = isrAVs.get(testData.isr1.getId());
    assertEquals(1, isr1AVs.size());
    assertEquals(getBigDecimal(200.0), isr1AVs.get(0).getValue());
    List<AttributeValue> isr2AVs = isrAVs.get(testData.isr2.getId());
    assertEquals(1, isr2AVs.size());
    assertEquals(getBigDecimal(1000.0), isr2AVs.get(0).getValue());

    bbIds = Lists.newArrayList(testData.bd1.getId());
    HashBucketMap<Integer, AttributeValue> bdAVs = avService.getBuildingBlockIdsToConnectedAttributeValues(bbIds, testData.enumAT.getId());
    List<AttributeValue> bd1AVs = bdAVs.get(testData.bd1.getId());
    assertEquals(1, bd1AVs.size());
    assertEquals("good", bd1AVs.get(0).getValueString());

    Timeseries timeseries1 = timeseriesService.loadTimeseriesByBuildingBlockAndAttributeType(testData.isr1, testData.numAT1);
    assertEquals(2, timeseries1.getEntries().size());
    assertEquals("[{\"date\":\"2012-01-01\",\"value\":\"100.00\"},{\"date\":\"2012-12-10\",\"value\":\"200.00\"}]",
        TimeseriesSerializer.serialize(timeseries1.getEntries()));

    Timeseries timeseries2 = timeseriesService.loadTimeseriesByBuildingBlockAndAttributeType(testData.isr2, testData.numAT1);
    assertEquals(2, timeseries2.getEntries().size());
    assertEquals("[{\"date\":\"2010-02-01\",\"value\":\"400.00\"},{\"date\":\"2013-03-01\",\"value\":\"1000.00\"}]",
        TimeseriesSerializer.serialize(timeseries2.getEntries()));

    Timeseries timeseries3 = timeseriesService.loadTimeseriesByBuildingBlockAndAttributeType(testData.bd1, testData.enumAT);
    assertEquals(2, timeseries3.getEntries().size());
    assertEquals("[{\"date\":\"2012-01-01\",\"value\":\"bad\"},{\"date\":\"2012-12-10\",\"value\":\"good\"}]",
        TimeseriesSerializer.serialize(timeseries3.getEntries()));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.importer.TimeseriesExcelImporter#importExcel()} with xls-File.
   */
  @Test
  public void testImportExcelFail2003() throws IOException {
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "timeseriesImportTestErrors.xls");
    testImportExcelFail(excel);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.importer.TimeseriesExcelImporter#importExcel()} with xlsx-File.
   */
  @Test
  public void testImportExcelFail2007() throws IOException {
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "timeseriesImportTestErrors.xlsx");
    testImportExcelFail(excel);
  }

  private void testImportExcelFail(Resource excel) throws IOException {
    setUpData();

    TimeseriesExcelImporter importer = new TimeseriesExcelImporter(atService, bbServiceLocator, timeseriesService);

    boolean importSuccess = importer.importExcel(ExcelUtils.openExcelFile(excel.getInputStream()));
    assertFalse(importSuccess);

    List<String> actualMessages = importer.getErrorMessages();

    Builder<String> builder = ImmutableList.builder();
    builder.add("Cell 'Tabelle1 A-7': No Building Block named \"non-existing building block\" found.");
    builder.add("Cell 'Tabelle1 C-8': Non-number value for number attribute type found.");
    builder.add("Cell 'Tabelle1 B-9': Date is after today. Only past dates or the present day are allowed for timeseries.");
    builder.add("Cell 'Tabelle1 A-10': No Building Block found.");
    builder.add("Cell 'Tabelle2 B-8': Date is after today. Only past dates or the present day are allowed for timeseries.");
    builder.add("Cell 'Tabelle2 B-9': No date value found.");
    builder.add("Cell 'Tabelle2 C-10': No attribute value found.");
    builder.add("Cell 'Tabelle3 A-3': \"Non-existing building block\" is not a valid building block class name.");
    builder.add("Cell 'Tabelle3 A-4': No attribute type with name \"non-existing attribute type\" found.");
    builder.add("Cell 'Tabelle4 A-4': Attribute \"non-timeseries number AT\" is not a timeseries attribute.");
    List<String> expectedMessages = builder.build();

    assertEquals(expectedMessages.size(), actualMessages.size());
    for (int i = 0; i < expectedMessages.size(); i++) {
      assertEquals(expectedMessages.get(i), actualMessages.get(i));
    }
  }

  private BigDecimal getBigDecimal(double number) {
    BigDecimal result = BigDecimal.valueOf(number);
    return result.setScale(2, BigDecimal.ROUND_DOWN);
  }

  private TestData setUpData() {
    TestData data = new TestData();
    EnumAT enumAT = testDataHelper.createEnumAttributeType("Test Enum", "test desc", Boolean.FALSE, testDataHelper.getDefaultATG());
    testDataHelper.createEnumAV("good", "test desc", enumAT);
    testDataHelper.createEnumAV("bad", "test desc", enumAT);
    enumAT.setTimeseries(true);
    data.enumAT = enumAT;
    NumberAT numAT = testDataHelper.createNumberAttributeType("Test Number", "test desc", testDataHelper.getDefaultATG());
    numAT.setTimeseries(true);
    data.numAT1 = numAT;
    testDataHelper.createNumberAttributeType("non-timeseries number AT", "test desc", testDataHelper.getDefaultATG());

    InformationSystem is1 = testDataHelper.createInformationSystem("IS1");
    InformationSystem is2 = testDataHelper.createInformationSystem("IS2");
    data.isr1 = testDataHelper.createInformationSystemRelease(is1, "1.0");
    data.isr2 = testDataHelper.createInformationSystemRelease(is2, null);

    data.bd1 = testDataHelper.createBusinessDomain("BD1", "test desc");

    testDataHelper.createTimeseries(data.isr2, numAT, new TimeseriesEntry(new LocalDate(2013, 3, 1).toDate(), "1000"));
    commit();

    beginTransaction();
    return data;
  }

  private static class TestData {
    private InformationSystemRelease isr1;
    private InformationSystemRelease isr2;
    private BusinessDomain           bd1;
    private EnumAT                   enumAT;
    private NumberAT                 numAT1;
  }

}

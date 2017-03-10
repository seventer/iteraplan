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
/**
 * 
 */
package de.iteratec.iteraplan.businesslogic.service.legacyExcel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.BuildingBlockHolder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeDataWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog.Level;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.businesslogic.service.TransportService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BuildingBlockUtil;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;


/**
 * Tests various test cases of the {@link ExcelImportService}.
 * 
 * @author agu
 *
 */
public class ImportServiceIntegrationTest extends BaseTransactionalTestSupport {

  private static final String               TEST_ISR_NAME    = "ISR1";
  private static final String               TEST_DESCRIPTION = "testDescription";

  /** Excel import service instance. */
  @Autowired
  private ExcelImportService                excelImportService;
  @Autowired
  private InformationSystemReleaseService   isrService;
  @Autowired
  private TechnicalComponentReleaseService  tcrService;
  @Autowired
  private InformationSystemInterfaceService isiService;
  @Autowired
  private TransportService                  transportService;
  @Autowired
  private TestDataHelper2                   testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    ProcessingLog userLog = new ProcessingLog(Level.DEBUG, logWriter);
    new LandscapeDataWorkbook(userLog);
  }

  @After
  public void onTearDown() {
    super.onTearDown();

    LandscapeDataWorkbook.removeProcessingLog();
  }

  /**
   * Tests the importing of Information system releases with the same name.
   * 
   * @throws Exception if an exception occurs
   */
  @Test
  public void testDuplicateISRBuildingBlocks() throws Exception {
    InformationSystemRelease isr1 = createSampleISR(TEST_ISR_NAME, "1");
    InformationSystemRelease isr2 = createSampleISR(TEST_ISR_NAME, "");

    LandscapeData landscapeData = new LandscapeData();

    BuildingBlockHolder isr1Holder = new BuildingBlockHolder(isr1, null, null);
    BuildingBlockHolder isr2Holder = new BuildingBlockHolder(isr2, null, null);

    isr1Holder.setClone(BuildingBlockUtil.clone(isr1));
    isr2Holder.setClone(BuildingBlockUtil.clone(isr2));

    landscapeData.addBuildingBlock(isr1Holder);
    landscapeData.addBuildingBlock(isr2Holder);

    landscapeData.setLocale(Locale.ENGLISH);
    excelImportService.importLandscapeData(landscapeData);

    assertNotNull(isrService.loadObjectByIdIfExists(isr1.getId()));
    assertNull(isr2.getId());
  }

  private InformationSystemRelease createSampleISR(String name, String version) {
    InformationSystem is1 = BuildingBlockFactory.createInformationSystem();
    InformationSystemRelease isr1 = BuildingBlockFactory.createInformationSystemRelease();
    is1.addRelease(isr1);
    is1.setName(name);
    isr1.setVersion(version);
    isr1.setDescription(name + " # " + version);
    isr1.setTypeOfStatus(TypeOfStatus.CURRENT);
    isr1.setRuntimePeriodNullSafe(new RuntimePeriod(createDate(2005, 1, 1), createDate(2005, 12, 31)));

    return isr1;
  }

  /**
   * Tests the importing of Information system releases with the same name.
   * 
   * @throws Exception if an exception occurs
   */
  @Test
  public void testDuplicateTCRBuildingBlocks() {
    TechnicalComponentRelease tcr1 = createSampleTCR("TCR1", "1");
    TechnicalComponentRelease tcr2 = createSampleTCR("TCR1", "");

    LandscapeData landscapeData = new LandscapeData();
    BuildingBlockHolder tcr1Holder = new BuildingBlockHolder(tcr1, null, null);
    BuildingBlockHolder tcr2Holder = new BuildingBlockHolder(tcr2, null, null);

    tcr1Holder.setClone(BuildingBlockUtil.clone(tcr1));
    tcr2Holder.setClone(BuildingBlockUtil.clone(tcr2));

    landscapeData.addBuildingBlock(tcr1Holder);
    landscapeData.addBuildingBlock(tcr2Holder);

    landscapeData.setLocale(Locale.ENGLISH);
    excelImportService.importLandscapeData(landscapeData);

    assertNotNull(tcrService.loadObjectByIdIfExists(tcr1.getId()));
    assertNull(tcr2.getId());
  }

  private TechnicalComponentRelease createSampleTCR(String name, String version) {
    TechnicalComponent tc = BuildingBlockFactory.createTechnicalComponent();
    TechnicalComponentRelease tcr = BuildingBlockFactory.createTechnicalComponentRelease();
    tc.addRelease(tcr);
    tc.setName(name);
    tc.setAvailableForInterfaces(true);
    tcr.setVersion(version);
    tcr.setDescription(name + " # " + version);
    tcr.setRuntimePeriodNullSafe(new RuntimePeriod(createDate(2005, 1, 1), createDate(2005, 12, 31)));

    return tcr;
  }

  /**
   * Tests what the Importer does, when the ISR blocks have cycles. It is still not clear, how we will
   * fix it.
   */
  @Test
  public void testCycles() {
    InformationSystemRelease isr1 = createSampleISR(TEST_ISR_NAME, "");
    InformationSystemRelease isr2 = createSampleISR("ISR2", "");
    InformationSystemRelease isr3 = createSampleISR("ISR3", "");

    final HSSFWorkbook workbook = new HSSFWorkbook();
    final HSSFSheet sheet = workbook.createSheet();
    final HSSFRow row = sheet.createRow(0);
    final Cell cell1 = row.createCell(0);
    final Cell cell2 = row.createCell(1);
    final Cell cell3 = row.createCell(2);

    cell1.setCellValue("ISR3 : ISR1");
    cell2.setCellValue("ISR1 : ISR2");
    cell3.setCellValue("ISR2 : ISR3");

    final Map<String, Cell> isr1Content = ImmutableMap.of("Information Systems hierarchical", cell1);
    final Map<String, Cell> isr2Content = ImmutableMap.of("Information Systems hierarchical", cell2);
    final Map<String, Cell> isr3Content = ImmutableMap.of("Information Systems hierarchical", cell3);
    final Map<String, Cell> emptyMap = Collections.emptyMap();

    LandscapeData landscapeData = new LandscapeData();
    BuildingBlockHolder isr1Holder = new BuildingBlockHolder(isr1, null, null);
    BuildingBlockHolder isr2Holder = new BuildingBlockHolder(isr2, null, null);
    BuildingBlockHolder isr3Holder = new BuildingBlockHolder(isr3, null, null);

    isr1Holder.setClone(BuildingBlockUtil.clone(isr1));
    isr2Holder.setClone(BuildingBlockUtil.clone(isr2));
    isr3Holder.setClone(BuildingBlockUtil.clone(isr3));

    landscapeData.addBuildingBlock(isr1Holder);
    landscapeData.addBuildingBlock(isr2Holder);
    landscapeData.addBuildingBlock(isr3Holder);
    landscapeData.addRelation(isr1, isr1Content, emptyMap);
    landscapeData.addRelation(isr2, isr2Content, emptyMap);
    landscapeData.addRelation(isr3, isr3Content, emptyMap);

    landscapeData.setLocale(Locale.ENGLISH);
    excelImportService.importLandscapeData(landscapeData);

    isr1 = isrService.loadObjectByIdIfExists(isr1.getId());
    assertEquals(isr3, isr1.getParent());
    isr2 = isrService.loadObjectByIdIfExists(isr2.getId());
    assertEquals(isr1, isr2.getParent());
    isr3 = isrService.loadObjectByIdIfExists(isr3.getId());
    assertNull(isr3.getParent());
  }

  /**
   * Tests the direction of the Business Objects for the imported ISI building block.
   * 
   * @throws Exception if an import exception occurs
   */
  @Test
  public void testISIBusinessObjectsDirection() throws Exception {
    BusinessObject customerBO = testDataHelper.createBusinessObject("Customer", "");
    testDataHelper.createBusinessObject("Rating", "");
    testDataHelper.createBusinessObject("Accounting entry", "");

    InformationSystem isA = testDataHelper.createInformationSystem(TEST_ISR_NAME);
    InformationSystemRelease isrA = testDataHelper.createInformationSystemRelease(isA, "1", TEST_DESCRIPTION, "1.1.2005", "31.12.2005",
        InformationSystemRelease.TypeOfStatus.CURRENT);

    InformationSystem isB = testDataHelper.createInformationSystem("ISR2");
    InformationSystemRelease isrB = testDataHelper.createInformationSystemRelease(isB, "1", TEST_DESCRIPTION, "1.1.2006", "31.12.2006",
        InformationSystemRelease.TypeOfStatus.CURRENT);

    InformationSystemInterface informationSystemInterface = testDataHelper.createInformationSystemInterfaceWithNameDirection("", "-",
        "Interface description", isrA, isrB, null);
    assertNotNull(informationSystemInterface);
    Transport transport = testDataHelper.createTransport(customerBO, informationSystemInterface, Direction.BOTH_DIRECTIONS);
    transportService.saveOrUpdate(transport);

    final Map<String, Cell> content = Maps.newHashMap();

    final HSSFWorkbook workbook = new HSSFWorkbook();
    final HSSFSheet sheet = workbook.createSheet();
    final HSSFRow row = sheet.createRow(0);
    final Cell cell1 = row.createCell(0);
    final Cell cell2 = row.createCell(1);
    final Cell cell3 = row.createCell(2);
    final Cell cell4 = row.createCell(3);

    cell1.setCellValue("ISR1 # 1");
    cell2.setCellValue("ISR2 # 1");
    cell3.setCellValue("Interface description");
    cell4.setCellValue("-> Customer; <- Rating; <-> Accounting entry ");

    content.put("Information System A", cell1);
    content.put("Information System B", cell2);
    content.put("Description", cell3);
    content.put("Transported Business Objects", cell4);

    final Map<String, Cell> emptyMap = Collections.emptyMap();

    LandscapeData landscapeData = new LandscapeData();
    landscapeData.addRelation(informationSystemInterface, content, emptyMap);

    landscapeData.setLocale(Locale.ENGLISH);
    excelImportService.importLandscapeData(landscapeData);

    InformationSystemInterface isiLoaded = isiService.loadObjectByIdIfExists(informationSystemInterface.getId());
    assertNotNull(isiLoaded);

    final Set<Transport> transports = isiLoaded.getTransports();
    assertFalse(transports.isEmpty());
    assertEquals(3, transports.size());

    checkTransportDirection(transports, "Customer", "->");
    checkTransportDirection(transports, "Rating", "<-");
    checkTransportDirection(transports, "Accounting entry", "<->");
  }

  /**
   * Checks the transport direction for the specified {@code boName}.
   * 
   * @param transports the list of available transports
   * @param boName the business object name
   * @param direction the concerned direction
   */
  private void checkTransportDirection(Set<Transport> transports, String boName, String direction) {
    for (Transport transport : transports) {
      String nonHierarchicalName = transport.getBusinessObject().getNonHierarchicalName();
      if (nonHierarchicalName.equals(boName)) {
        assertEquals(direction, transport.getTransportInfo().getTextRepresentation());
        return;
      }
    }

    fail("Concerned Transport not found!");
  }

  /**
   * Tests if the {@link InformationSystemInterface} can import the {@link TechnicalComponentRelease} with
   * the "Available for Interfaces" flag set to {@code false}.
   */
  @Test
  public void testImportInterfacesWithNotAvailableForInterfacesTCR() {
    testDataHelper.createBusinessObject("Accounting entry", "");

    TechnicalComponent tc = testDataHelper.createTechnicalComponent("TC1", true, true);
    testDataHelper.createTCRelease(tc, "1", true);

    InformationSystem isA = testDataHelper.createInformationSystem(TEST_ISR_NAME);
    testDataHelper
        .createInformationSystemRelease(isA, "1", TEST_DESCRIPTION, "1.1.2005", "31.12.2005", InformationSystemRelease.TypeOfStatus.CURRENT);

    InformationSystem isB = testDataHelper.createInformationSystem("ISR2");
    testDataHelper
        .createInformationSystemRelease(isB, "1", TEST_DESCRIPTION, "1.1.2006", "31.12.2006", InformationSystemRelease.TypeOfStatus.CURRENT);

    InformationSystemInterface informationSystemInterface = BuildingBlockFactory.createInformationSystemInterface();

    informationSystemInterface.setName("test_name");
    informationSystemInterface.setDescription("ISI description");

    final HSSFWorkbook workbook = new HSSFWorkbook();
    final HSSFSheet sheet = workbook.createSheet();
    final HSSFRow row = sheet.createRow(0);
    final Cell cell1 = row.createCell(0);
    final Cell cell2 = row.createCell(1);
    final Cell cell3 = row.createCell(2);
    final Cell cell4 = row.createCell(3);

    cell1.setCellValue("ISR1 # 1");
    cell2.setCellValue("ISR2 # 1");
    cell3.setCellValue("TC1 # 1");
    cell4.setCellValue("<-> Accounting entry ");

    final Map<String, Cell> content = Maps.newHashMap();
    content.put("Information System A", cell1);
    content.put("Information System B", cell2);
    content.put("Technical Components", cell3);
    content.put("Transported Business Objects", cell4);

    final Map<String, Cell> emptyMap = Collections.emptyMap();

    LandscapeData landscapeData = new LandscapeData();
    landscapeData.addRelation(informationSystemInterface, content, emptyMap);

    landscapeData.setLocale(Locale.ENGLISH);
    excelImportService.importLandscapeData(landscapeData);

    InformationSystemInterface isiLoaded = isiService.loadObjectByIdIfExists(informationSystemInterface.getId());
    assertNotNull(isiLoaded);
    // assertTrue(isiLoaded.getTechnicalComponentReleases().isEmpty());
    // assertFalse("This test will fail after bug fix :)", isiLoaded.getTechnicalComponentReleases().isEmpty());
  }

  private Date createDate(int year, int month, int day) {
    return new DateTime().withYear(year).withMonthOfYear(month).withDayOfMonth(day).toDate();
  }
}

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
import static org.junit.Assert.assertNotNull;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.BuildingBlockHolder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.CellValueHolder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeData.BuildingBlockAttributes;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeDataWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog.Level;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.BusinessMappingService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BuildingBlockUtil;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;


/**
 * Tests various cases importing Attributes in {@link ExcelImportService}.
 * 
 * @author agu
 *
 */
public class ExcelImportServiceAttributesIntegrationTest extends BaseTransactionalTestSupport {
  private static final String               TEST_DESCRIPTION = "testDescription";
  private static final String               UPDATED_TEXT_AV1 = "Text Value1 updated";

  /** Excel import service instance. */
  @Autowired
  private ExcelImportService                excelImportService;
  @Autowired
  private InformationSystemReleaseService   isrService;
  @Autowired
  private InformationSystemInterfaceService isiService;
  @Autowired
  private BusinessMappingService            businessMappingService;
  @Autowired
  private AttributeTypeService              attributeTypeService;
  @Autowired
  private AttributeTypeGroupDAO             attributeTypeGroupDAO;
  @Autowired
  private TestDataHelper2  testDataHelper;

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
   * Tests if importing Building Block the attribute values will be not deleted. This case is tested with
   * {@link InformationSystemRelease}, but it is actual to all Building blocks.
   * 
   * @throws Exception if an exception occurs
   */
  @Test
  public void testAttributesExistAfterISRImport() {
    InformationSystem is = testDataHelper.createInformationSystem("I");
    TypeOfStatus typeOfStatus = InformationSystemRelease.TypeOfStatus.CURRENT;
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, "i1", TEST_DESCRIPTION, "1.1.2005", "31.12.2005", typeOfStatus);

    TextAV textAV1 = createTextAttribute("1");
    TextAV textAV2 = createTextAttribute("2");
    TextAV textAV3 = createTextAttribute("3");

    testDataHelper.createAVA(isr, textAV1);
    testDataHelper.createAVA(isr, textAV2);
    testDataHelper.createAVA(isr, textAV3);

    final HSSFWorkbook workbook = new HSSFWorkbook();
    final HSSFSheet sheet = workbook.createSheet();
    final HSSFRow row = sheet.createRow(0);
    final Cell nameCell = row.createCell(0);
    final Cell descriptionCell = row.createCell(1);

    BuildingBlockHolder buildingBlockHolder = new BuildingBlockHolder(isr, nameCell, descriptionCell);
    buildingBlockHolder.setClone(BuildingBlockUtil.clone(isr));
    
    LandscapeData landscapeData = new LandscapeData();
    landscapeData.addBuildingBlock(buildingBlockHolder);

    final Map<String, CellValueHolder> attributesMap = Maps.newHashMap();
    final Cell cell = row.createCell(2);
    cell.setCellValue(UPDATED_TEXT_AV1);
    CellValueHolder cellValueHolder = new CellValueHolder(cell);

    attributesMap.put("TextAT1", cellValueHolder);
    landscapeData.getAttributes().add(new BuildingBlockAttributes(isr, attributesMap));

    landscapeData.setLocale(Locale.GERMAN);
    excelImportService.importLandscapeData(landscapeData);

    InformationSystemRelease isrLoadedFromDb = isrService.loadObjectById(isr.getId());
    Set<AttributeValueAssignment> attributeValueAssignments = isrLoadedFromDb.getAttributeValueAssignments();
    assertEquals("Some attribute value assignments were deleted.", 3, attributeValueAssignments.size());

    checkAttributeValues(attributeValueAssignments, textAV1.getAbstractAttributeType(), UPDATED_TEXT_AV1);
    checkAttributeValues(attributeValueAssignments, textAV2.getAbstractAttributeType(), "Text Value2");
    checkAttributeValues(attributeValueAssignments, textAV3.getAbstractAttributeType(), "Text Value3");
  }

  /**
   * Tests if the attributes of the {@link InformationSystemInterface} will be imported and updated.
   * 
   * @throws Exception if an exception occurs
   */
  @Test
  public void testAttributesExistAfterInterfaceImport() {
    InformationSystem isA = testDataHelper.createInformationSystem("I1");
    InformationSystemRelease isrA = testDataHelper.createInformationSystemRelease(isA, "i1", TEST_DESCRIPTION, "1.1.2005", "31.12.2005",
        InformationSystemRelease.TypeOfStatus.CURRENT);

    InformationSystem isB = testDataHelper.createInformationSystem("I2");
    InformationSystemRelease isrB = testDataHelper.createInformationSystemRelease(isB, "i2", TEST_DESCRIPTION, "1.1.2006", "31.12.2006",
        InformationSystemRelease.TypeOfStatus.CURRENT);

    InformationSystemInterface informationSystemInterface = testDataHelper.createInformationSystemInterfaceWithNameDirection("", "-",
        "Interface description", isrA, isrB, null);
    assertNotNull(informationSystemInterface);

    TextAV textAV1 = createTextAttribute("1");
    TextAV textAV2 = createTextAttribute("2");
    TextAV textAV3 = createTextAttribute("3");
    testDataHelper.createAVA(informationSystemInterface, textAV1);
    testDataHelper.createAVA(informationSystemInterface, textAV2);
    testDataHelper.createAVA(informationSystemInterface, textAV3);

    final HSSFWorkbook workbook = new HSSFWorkbook();
    final HSSFSheet sheet = workbook.createSheet();
    final HSSFRow row = sheet.createRow(0);
    final Cell cell1 = row.createCell(0);
    final Cell cell2 = row.createCell(0);
    final Cell cell3 = row.createCell(0);
    cell1.setCellValue(UPDATED_TEXT_AV1);
    cell2.setCellValue(isrA.getNonHierarchicalName());
    cell3.setCellValue(isrB.getNonHierarchicalName());

    final Map<String, Cell> attributesMap = Maps.newHashMap();

    attributesMap.put("TextAT1", cell1);
    final Map<String, Cell> contentMap = Maps.newHashMap();

    contentMap.put("Informationssystem A", cell2);
    contentMap.put("Informationssystem B", cell3);

    LandscapeData landscapeData = new LandscapeData();
    landscapeData.addRelation(informationSystemInterface, contentMap, attributesMap);

    landscapeData.setLocale(Locale.GERMAN);
    excelImportService.importLandscapeData(landscapeData);

    InformationSystemInterface isiLoadedFromDb = isiService.loadObjectById(informationSystemInterface.getId());
    Set<AttributeValueAssignment> attributeValueAssignments = isiLoadedFromDb.getAttributeValueAssignments();
    assertEquals("Some attribute value assignments were deleted.", 3, attributeValueAssignments.size());

    checkAttributeValues(attributeValueAssignments, textAV1.getAbstractAttributeType(), UPDATED_TEXT_AV1);
    checkAttributeValues(attributeValueAssignments, textAV2.getAbstractAttributeType(), "Text Value2");
    checkAttributeValues(attributeValueAssignments, textAV3.getAbstractAttributeType(), "Text Value3");
  }

  /**
   * This test case tests the Bug #2192: Fachliche Zuordnungen mit Merkmalen koennen nicht geloescht werden.
   */@Test
  public void testImportBussinesMappingsWithAttributes() {
    InformationSystem is = testDataHelper.createInformationSystem("I");
    TypeOfStatus typeOfStatus = InformationSystemRelease.TypeOfStatus.CURRENT;
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, "i1", TEST_DESCRIPTION, "1.1.2005", "31.12.2005", typeOfStatus);

    BusinessProcess businessProcess = testDataHelper.createBusinessProcess("BP1", "BP1 description");
    BusinessUnit businessUnit = testDataHelper.createBusinessUnit("BU1", "BU1 description");
    Product product = testDataHelper.createProduct("Product1", "Product1 description");
    BusinessMapping businessMapping = testDataHelper.createBusinessMapping(isr, businessProcess, businessUnit, product);

    TextAV textAV1 = createTextAttribute("1");
    TextAV textAV2 = createTextAttribute("2");
    TextAV textAV3 = createTextAttribute("3");
    testDataHelper.createAVA(businessMapping, textAV1);
    testDataHelper.createAVA(businessMapping, textAV2);
    testDataHelper.createAVA(businessMapping, textAV3);

    InformationSystem isFromExcel = BuildingBlockFactory.createInformationSystem();
    InformationSystemRelease isrFromExcel = BuildingBlockFactory.createInformationSystemRelease();
    isFromExcel.addRelease(isrFromExcel);
    isFromExcel.setName("NewNameFromExcel");
    isrFromExcel.setId(isr.getId());
    isrFromExcel.setVersion("i1");
    isrFromExcel.setDescription(TEST_DESCRIPTION);
    isrFromExcel.setTypeOfStatus(TypeOfStatus.CURRENT);
    isrFromExcel.setRuntimePeriodNullSafe(new RuntimePeriod(createDate(2005, 1, 1), createDate(2005, 12, 31)));

    final HSSFWorkbook workbook = new HSSFWorkbook();
    final HSSFSheet sheet = workbook.createSheet();
    final HSSFRow row = sheet.createRow(0);
    final Cell nameCell = row.createCell(0);
    final Cell descriptionCell = row.createCell(1);
    final Cell cell = row.createCell(2);
    cell.setCellValue("(BP1 / Product1 / BU1)");

    final Map<String, Cell> isrContentMap = Maps.newHashMap();
    isrContentMap.put("Business Mappings(Business Process / Product / Business Unit)", cell);

    BuildingBlockHolder buildingBlockHolder = new BuildingBlockHolder(isrFromExcel, nameCell, descriptionCell);
    buildingBlockHolder.setClone(BuildingBlockUtil.clone(isrFromExcel));
    
    LandscapeData landscapeData = new LandscapeData();
    landscapeData.addBuildingBlock(buildingBlockHolder);
    landscapeData.addRelation(isrFromExcel, isrContentMap, Maps.<String, Cell> newHashMap());

    landscapeData.setLocale(Locale.ENGLISH);
    excelImportService.importLandscapeData(landscapeData);

    InformationSystemRelease isrLoadedFromDb = isrService.loadObjectById(isrFromExcel.getId());
    assertEquals("ISR-Name was not changed to excel value!", "NewNameFromExcel", isrLoadedFromDb.getNameWithoutVersion());

    BusinessMapping bmLoadedFromDb = businessMappingService.getBusinessMappingByRelatedBuildingBlockIds(product.getId(), businessUnit.getId(),
        businessProcess.getId(), isr.getId());
    Set<AttributeValueAssignment> attributeValueAssignments = bmLoadedFromDb.getAttributeValueAssignments();
    assertEquals("Some attribute value assignments were deleted.", 3, attributeValueAssignments.size());

    checkAttributeValues(attributeValueAssignments, textAV1.getAbstractAttributeType(), "Text Value1");
    checkAttributeValues(attributeValueAssignments, textAV2.getAbstractAttributeType(), "Text Value2");
    checkAttributeValues(attributeValueAssignments, textAV3.getAbstractAttributeType(), "Text Value3");
  }

  @Test
  public void testImportDateAttribute() {
    // create attribute type and sample building block
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("myTAG", "");
    DateAT dateType = testDataHelper.createDateAttributeType("MyDateType", "", atg);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(dateType);
    InformationSystem is = testDataHelper.createInformationSystem("myIS");
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, "1.0");

    // create excel cells with import data
    final HSSFWorkbook workbook = new HSSFWorkbook();
    final CellStyle dateStyle = workbook.createCellStyle();
    dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
    final HSSFSheet sheet = workbook.createSheet();
    final HSSFRow row = sheet.createRow(0);
    final Cell cell1 = row.createCell(0);
    cell1.setCellValue(createDate(2010, 12, 31));
    cell1.setCellStyle(dateStyle);  // need to do this so our import routines can parse it back as a date value

    Map<String, Cell> attributes = new HashMap<String, Cell>();
    attributes.put(dateType.getName(), cell1);

    LandscapeData landscapeData = new LandscapeData();
    landscapeData.addAttributes(isr, attributes);

    landscapeData.setLocale(Locale.GERMAN);
    excelImportService.importLandscapeData(landscapeData);

    InformationSystemRelease isrLoadedFromDb = isrService.loadObjectById(isr.getId());
    String loadedAv = isrLoadedFromDb.getAttributeValue(dateType.getName(), Locale.GERMAN);
    assertEquals("31.12.2010", loadedAv);

    // following call failed before ITERAPLAN-170 was fixed
    @SuppressWarnings("unused")
    AttributeType at = attributeTypeService.getAttributeTypeByName(dateType.getName());
  }

  /**
   * Creates a {@link TextAV} attribute with type named as {@code 'TextAT[identifier]'}, belonging to
   * standard attribute group, and value {@code 'Text Value[identifier]'}.
   * 
   * @param identifier the text attribute identifier
   * @return the newly created text attribute
   */
  private TextAV createTextAttribute(String identifier) {
    AttributeTypeGroup atgStandard = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    TextAT textAT1 = testDataHelper.createTextAttributeType("TextAT" + identifier, "TextAT description" + identifier, true, atgStandard);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(textAT1);
    TextAV textAV1 = testDataHelper.createTextAV("Text Value" + identifier, textAT1);

    return textAV1;
  }

  private Date createDate(int year, int month, int day) {
    return new DateTime().withYear(year).withMonthOfYear(month).withDayOfMonth(day).toDate();
  }

  /**
   * Checks if the value of the text attribute equals the specified {@code expectedValue}.
   * 
   * @param avas the set of the attribute value assignments
   * @param type the attribute type, to check the value for
   * @param expectedValue the expected attribute value
   */
  private void checkAttributeValues(Set<AttributeValueAssignment> avas, AttributeType type, String expectedValue) {
    for (AttributeValueAssignment ava : avas) {
      AttributeValue attributeValue = ava.getAttributeValue();

      if (attributeValue.getAbstractAttributeType() == type) {
        assertEquals(expectedValue, attributeValue.getValueString());
      }
    }
  }
}

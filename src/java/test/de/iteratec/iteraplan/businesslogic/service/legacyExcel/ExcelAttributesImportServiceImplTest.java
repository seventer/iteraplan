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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.output.NullOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;


/**
 * Integration test for the {@link ExcelAttributesImportServiceImpl} class.
 */
public class ExcelAttributesImportServiceImplTest extends BaseTransactionalTestSupport {

  private static final String          EXCEL_TEST_FILES = "excel/testFiles/legacyExcel/";
  @Autowired
  private ExcelAttributesImportService excelAttributesImportService;
  @Autowired
  private AttributeTypeService         attributeTypeService;
  @Autowired
  private AttributeTypeGroupService    attributeTypeGroupService;
  @Autowired
  private BuildingBlockTypeService     buildingBlockTypeService;
  @Autowired
  private TestDataHelper2              testDataHelper;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesEnum() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    EnumAT enumAt1 = (EnumAT) attributeTypeService.getAttributeTypeByName("Test1");
    assertNotNull(enumAt1);
    assertEquals("Test description", enumAt1.getDescription());
    assertFalse(enumAt1.isMultiassignmenttype());
    assertTrue(enumAt1.isMandatory());
    Collection<EnumAV> attributeValues1 = enumAt1.getAttributeValues();
    assertEquals(18, attributeValues1.size());

    EnumAT enumAt2 = (EnumAT) attributeTypeService.getAttributeTypeByName("Test2");
    assertNotNull(enumAt2);
    assertEquals("Test description2", enumAt2.getDescription());
    assertTrue(enumAt2.isMultiassignmenttype());
    assertFalse(enumAt2.isMandatory());
    Collection<EnumAV> attributeValues2 = enumAt2.getAttributeValues();
    assertEquals(1, attributeValues2.size());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesResponsibility() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "responsibilityAttributeTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    ResponsibilityAT responsibilityAt1 = (ResponsibilityAT) attributeTypeService.getAttributeTypeByName("ResponsibilityAT1");
    assertNotNull(responsibilityAt1);
    assertEquals("ResponsibilityAt1 description", responsibilityAt1.getDescription());
    assertFalse(responsibilityAt1.isMultiassignmenttype());
    assertTrue(responsibilityAt1.isMandatory());

    ResponsibilityAT responsibilityAt2 = (ResponsibilityAT) attributeTypeService.getAttributeTypeByName("ResponsibilityAT2");
    assertNotNull(responsibilityAt2);
    assertEquals("ResponsibilityAt2 description", responsibilityAt2.getDescription());
    assertTrue(responsibilityAt2.isMultiassignmenttype());
    assertFalse(responsibilityAt2.isMandatory());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @SuppressWarnings("boxing")
  @Test
  public void testImportAttributesNumber() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "numberAttributesTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    NumberAT numberAt1 = (NumberAT) attributeTypeService.getAttributeTypeByName("NumberAt1");
    assertNotNull(numberAt1);
    assertEquals("NumberAt1 description", numberAt1.getDescription());
    assertFalse(numberAt1.isMandatory());
    assertEquals(BigDecimal.TEN, numberAt1.getMinValue());
    assertEquals(new BigDecimal(20), numberAt1.getMaxValue());
    assertEquals("Euro", numberAt1.getUnit());
    assertEquals(true, numberAt1.isRangeUniformyDistributed());

    NumberAT numberAt2 = (NumberAT) attributeTypeService.getAttributeTypeByName("NumberAt2");
    assertNotNull(numberAt2);
    assertEquals("NumberAt2 description", numberAt2.getDescription());
    assertTrue(numberAt2.isMandatory());
    assertNull(numberAt2.getMinValue());
    assertNull(numberAt2.getMaxValue());
    assertEquals("a", numberAt2.getUnit());
    assertEquals(false, numberAt2.isRangeUniformyDistributed());

    NumberAT numberAt3 = (NumberAT) attributeTypeService.getAttributeTypeByName("NumberAt3");
    assertNotNull(numberAt3);
    assertEquals(BigDecimal.TEN, numberAt3.getMinValue());
    assertEquals(BigDecimal.TEN, numberAt3.getMaxValue());

    NumberAT numberAt4 = (NumberAT) attributeTypeService.getAttributeTypeByName("NumberAt4");
    assertNotNull(numberAt4);
    assertNull(numberAt4.getMinValue());
    assertNull(numberAt4.getMaxValue());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesDate() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "dateAttributesTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    DateAT dateAt1 = (DateAT) attributeTypeService.getAttributeTypeByName("DateAt1");
    assertNotNull(dateAt1);
    assertEquals("DateAt1 description", dateAt1.getDescription());
    assertTrue(dateAt1.isMandatory());

    DateAT dateAt2 = (DateAT) attributeTypeService.getAttributeTypeByName("DateAt2");
    assertNotNull(dateAt2);
    assertEquals("DateAt2 description", dateAt2.getDescription());
    assertFalse(dateAt2.isMandatory());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesText() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "textAttributesTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    TextAT textAt1 = (TextAT) attributeTypeService.getAttributeTypeByName("TextAt1");
    assertNotNull(textAt1);
    assertEquals("TextAt1 description", textAt1.getDescription());
    assertTrue(textAt1.isMandatory());
    assertFalse(textAt1.isMultiline());

    TextAT textAt2 = (TextAT) attributeTypeService.getAttributeTypeByName("TextAt2");
    assertNotNull(textAt2);
    assertEquals("TextAt2 description", textAt2.getDescription());
    assertFalse(textAt2.isMandatory());
    assertTrue(textAt2.isMultiline());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesCreate() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesCreateTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    EnumAT enumAt = (EnumAT) attributeTypeService.getAttributeTypeByName("Test");
    assertNotNull(enumAt);
    assertEquals("Test description", enumAt.getDescription());
    assertFalse(enumAt.isMultiassignmenttype());
    assertTrue(enumAt.isMandatory());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesEmptyLines() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesEmptyLinesTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    EnumAT enumAt = (EnumAT) attributeTypeService.getAttributeTypeByName("Test");
    assertNotNull(enumAt);
    assertEquals("Test description", enumAt.getDescription());
    assertFalse(enumAt.isMultiassignmenttype());
    assertTrue(enumAt.isMandatory());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesUpdate() throws IOException {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("testATG", "", Boolean.TRUE);
    EnumAT at = testDataHelper.createEnumAttributeType("Test", "", Boolean.FALSE, atg);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesUpdateTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    assertEquals("Test", at.getName());
    assertEquals("Test description", at.getDescription());
    assertTrue(at.isMultiassignmenttype());
    assertFalse(at.isMandatory());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesUpdateOtherExists() throws IOException {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("testATG", "", Boolean.TRUE);
    ResponsibilityAT at = testDataHelper.createResponsibilityAttributeType("Test", "old Description", Boolean.FALSE, atg);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesUpdateTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    assertEquals("Test", at.getName());
    assertEquals("old Description", at.getDescription());
    assertFalse(at.isMultiassignmenttype());
    assertFalse(at.isMandatory());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesRename() throws IOException {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("testATG", "", Boolean.TRUE);
    EnumAT at = testDataHelper.createEnumAttributeType("Test1", "", Boolean.FALSE, atg);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesRenameTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    assertEquals("Test", at.getName());
    assertEquals("Test description", at.getDescription());
    assertTrue(at.isMultiassignmenttype());
    assertFalse(at.isMandatory());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesRenameOtherExists() throws IOException {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("testATG", "", Boolean.TRUE);
    ResponsibilityAT at = testDataHelper.createResponsibilityAttributeType("Test1", "old Description", Boolean.FALSE, atg);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesRenameTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    assertEquals("Test1", at.getName());
    assertEquals("old Description", at.getDescription());
    assertFalse(at.isMultiassignmenttype());
    assertFalse(at.isMandatory());

    assertNull(attributeTypeService.getAttributeTypeByName("Test"));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesRenameNotExists() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesRenameTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    assertNull(attributeTypeService.getAttributeTypeByName("Test"));
    assertNull(attributeTypeService.getAttributeTypeByName("Test1"));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesRenameToExisting() throws IOException {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("testATG", "", Boolean.TRUE);
    EnumAT atRename = testDataHelper.createEnumAttributeType("Test1", "d1", Boolean.FALSE, atg);
    EnumAT atExisting = testDataHelper.createEnumAttributeType("Test", "d2", Boolean.FALSE, atg);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesRenameTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    assertNotNull(attributeTypeService.getAttributeTypeByName("Test1"));
    assertEquals("d1", atRename.getDescription());
    assertFalse(atRename.isMultiassignmenttype());

    assertNotNull(attributeTypeService.getAttributeTypeByName("Test"));
    assertEquals("d2", atExisting.getDescription());
    assertFalse(atExisting.isMultiassignmenttype());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesRenameToBlank() throws IOException {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("testATG", "", Boolean.TRUE);
    EnumAT at = testDataHelper.createEnumAttributeType("Test", "old Description", Boolean.FALSE, atg);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesRenameToBlankTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    assertNotNull(attributeTypeService.getAttributeTypeByName("Test"));
    assertEquals("old Description", at.getDescription());
    assertNull(attributeTypeService.getAttributeTypeByName(""));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesDouble() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesDoubleTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    EnumAT enumAt1 = (EnumAT) attributeTypeService.getAttributeTypeByName("enumAt1");
    assertNotNull(enumAt1);
    assertEquals("d3", enumAt1.getDescription());
    List<EnumAV> attributeValues1 = enumAt1.getSortedAttributeValues();
    assertEquals(1, attributeValues1.size());
    assertEquals("testvalue3", attributeValues1.get(0).getName());

    EnumAT enumAt2 = (EnumAT) attributeTypeService.getAttributeTypeByName("enumAt2");
    assertNotNull(enumAt2);
    assertEquals("d2", enumAt2.getDescription());
    List<EnumAV> attributeValues2 = enumAt2.getSortedAttributeValues();
    assertEquals(2, attributeValues2.size());
    assertEquals("testvalue1", attributeValues2.get(0).getName());
    assertEquals("testvalue2", attributeValues2.get(1).getName());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesGroup() throws IOException {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("testATG", "", Boolean.TRUE);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesGroupTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    EnumAT enumAt = (EnumAT) attributeTypeService.getAttributeTypeByName("Test");
    assertNotNull(enumAt);
    assertEquals(atg, enumAt.getAttributeTypeGroup());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesGroupNotExists() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesGroupTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    EnumAT enumAt = (EnumAT) attributeTypeService.getAttributeTypeByName("Test");
    assertNotNull(enumAt);
    assertEquals(attributeTypeGroupService.getStandardAttributeTypeGroup(), enumAt.getAttributeTypeGroup());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesActivatedTypes() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesActivatedTypesTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    EnumAT enumAt = (EnumAT) attributeTypeService.getAttributeTypeByName("Test");
    assertNotNull(enumAt);
    Set<BuildingBlockType> bbTypes = enumAt.getBuildingBlockTypes();
    assertEquals(1, bbTypes.size());
    assertTrue(bbTypes.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSPROCESS)));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesActivatedTypesNotAvailable() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesActivatedTypesNotAvailableTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    EnumAT enumAt = (EnumAT) attributeTypeService.getAttributeTypeByName("Test");
    assertNotNull(enumAt);
    Set<BuildingBlockType> bbTypes = enumAt.getBuildingBlockTypes();
    assertEquals(0, bbTypes.size());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesActivatedTypesInvalid() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesActivatedTypesInvalidTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    EnumAT enumAt = (EnumAT) attributeTypeService.getAttributeTypeByName("Test");
    assertNotNull(enumAt);
    Set<BuildingBlockType> bbTypes = enumAt.getBuildingBlockTypes();
    assertEquals(0, bbTypes.size());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesActivatedTypesMulti() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesActivatedTypesMultiTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    EnumAT enumAt = (EnumAT) attributeTypeService.getAttributeTypeByName("Test");
    assertNotNull(enumAt);
    Set<BuildingBlockType> bbTypes = enumAt.getBuildingBlockTypes();
    assertEquals(3, bbTypes.size());
    assertTrue(bbTypes.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE)));
    assertTrue(bbTypes.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE)));
    assertTrue(bbTypes.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE)));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesActivatedTypesMultiWithWhitespaces() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "attributesActivatedTypesMultiWithWhitespacesTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    EnumAT enumAt = (EnumAT) attributeTypeService.getAttributeTypeByName("EnumAt1");
    assertNotNull(enumAt);
    Set<BuildingBlockType> bbTypesAt1 = enumAt.getBuildingBlockTypes();
    assertEquals(3, bbTypesAt1.size());
    assertTrue(bbTypesAt1.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSDOMAIN)));
    assertTrue(bbTypesAt1.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSPROCESS)));
    assertTrue(bbTypesAt1.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.PRODUCT)));

    DateAT dateAt = (DateAT) attributeTypeService.getAttributeTypeByName("DateAt1");
    assertNotNull(dateAt);
    Set<BuildingBlockType> bbTypesAt2 = dateAt.getBuildingBlockTypes();
    assertEquals(3, bbTypesAt2.size());
    assertTrue(bbTypesAt2.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSDOMAIN)));
    assertTrue(bbTypesAt2.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSPROCESS)));
    assertTrue(bbTypesAt2.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.PRODUCT)));

    NumberAT numberAt = (NumberAT) attributeTypeService.getAttributeTypeByName("NumberAt1");
    assertNotNull(numberAt);
    Set<BuildingBlockType> bbTypesAt3 = numberAt.getBuildingBlockTypes();
    assertEquals(3, bbTypesAt3.size());
    assertTrue(bbTypesAt3.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSDOMAIN)));
    assertTrue(bbTypesAt3.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSPROCESS)));
    assertTrue(bbTypesAt3.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.PRODUCT)));

    TextAT textAt = (TextAT) attributeTypeService.getAttributeTypeByName("TextAt1");
    assertNotNull(textAt);
    Set<BuildingBlockType> bbTypesAt4 = textAt.getBuildingBlockTypes();
    assertEquals(3, bbTypesAt4.size());
    assertTrue(bbTypesAt4.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSDOMAIN)));
    assertTrue(bbTypesAt4.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSPROCESS)));
    assertTrue(bbTypesAt4.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.PRODUCT)));

    ResponsibilityAT respAt = (ResponsibilityAT) attributeTypeService.getAttributeTypeByName("ResponsibilityAt1");
    assertNotNull(respAt);
    Set<BuildingBlockType> bbTypesAt5 = respAt.getBuildingBlockTypes();
    assertEquals(3, bbTypesAt5.size());
    assertTrue(bbTypesAt5.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSDOMAIN)));
    assertTrue(bbTypesAt5.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSPROCESS)));
    assertTrue(bbTypesAt5.contains(buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.PRODUCT)));
  }

}

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.output.NullOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.UserService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.RangeValue;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserGroup;


/**
 * Integration test for the {@link ExcelAttributesImportServiceImpl} class.
 */
public class ExcelAttributesImportServiceImplValuesTest extends BaseTransactionalTestSupport {

  private static final String          EXCEL_TEST_FILES = "excel/testFiles/legacyExcel/";
  @Autowired
  private ExcelAttributesImportService excelAttributesImportService;
  @Autowired
  private AttributeTypeService         attributeTypeService;
  @Autowired
  private AttributeTypeGroupService    attributeTypeGroupService;
  @Autowired
  private UserService                  userService;
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
  public void testImportAttributesEnumValuesCreate() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesValuesTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    EnumAT at = (EnumAT) attributeTypeService.getAttributeTypeByName("Test");
    assertNotNull(at);
    List<EnumAV> attributeValues = at.getSortedAttributeValues();
    assertEquals(2, attributeValues.size());
    EnumAV val1 = attributeValues.get(0);
    assertEquals("val1", val1.getName());
    assertEquals("description1", val1.getDescription());
    EnumAV val2 = attributeValues.get(1);
    assertEquals("val2", val2.getName());
    assertEquals("description3", val2.getDescription());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesEnumValuesAdd() throws IOException {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("testATG", "", Boolean.TRUE);
    EnumAT at = testDataHelper.createEnumAttributeType("Test", "", Boolean.FALSE, atg);
    EnumAV av = testDataHelper.createEnumAV("testValue", "old Description", at);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesValuesTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    List<EnumAV> attributeValues = at.getSortedAttributeValues();
    assertEquals(3, attributeValues.size());
    assertEquals(av, attributeValues.get(0));
    EnumAV val1 = attributeValues.get(1);
    assertEquals("val1", val1.getName());
    assertEquals("description1", val1.getDescription());
    EnumAV val2 = attributeValues.get(2);
    assertEquals("val2", val2.getName());
    assertEquals("description3", val2.getDescription());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesEnumValuesEmptyLines() throws IOException {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("testATG", "", Boolean.TRUE);
    EnumAT at = testDataHelper.createEnumAttributeType("Test", "", Boolean.FALSE, atg);
    EnumAV av = testDataHelper.createEnumAV("testValue", "old Description", at);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesValuesEmptyLinesTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    List<EnumAV> attributeValues = at.getSortedAttributeValues();
    assertEquals(3, attributeValues.size());
    assertEquals(av, attributeValues.get(0));
    EnumAV val1 = attributeValues.get(1);
    assertEquals("val1", val1.getName());
    assertEquals("description1", val1.getDescription());
    EnumAV val2 = attributeValues.get(2);
    assertEquals("val2", val2.getName());
    assertEquals("description2", val2.getDescription());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesEnumValuesUpdate() throws IOException {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("testATG", "", Boolean.TRUE);
    EnumAT at = testDataHelper.createEnumAttributeType("Test", "", Boolean.FALSE, atg);
    EnumAV av = testDataHelper.createEnumAV("testValue", "old Description", at);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesValuesUpdateTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    List<EnumAV> attributeValues = at.getSortedAttributeValues();
    assertEquals(1, attributeValues.size());
    assertEquals(av, attributeValues.get(0));
    assertEquals("new Description", av.getDescription());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesEnumValuesRename() throws IOException {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("testATG", "", Boolean.TRUE);
    EnumAT at = testDataHelper.createEnumAttributeType("Test", "", Boolean.FALSE, atg);
    EnumAV av = testDataHelper.createEnumAV("testValue", "old Description", at);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesValuesRenameTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    List<EnumAV> attributeValues = at.getSortedAttributeValues();
    assertEquals(1, attributeValues.size());
    assertEquals(av, attributeValues.get(0));
    assertEquals("value", av.getName());
    assertEquals("new Description", av.getDescription());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesEnumValuesRenameNotExisting() throws IOException {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("testATG", "", Boolean.TRUE);
    EnumAT at = testDataHelper.createEnumAttributeType("Test", "", Boolean.FALSE, atg);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesValuesRenameTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    assertTrue(at.getAttributeValues().isEmpty());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesEnumValuesRenameToExisting() throws IOException {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("testATG", "", Boolean.TRUE);
    EnumAT at = testDataHelper.createEnumAttributeType("Test", "", Boolean.FALSE, atg);
    EnumAV av1 = testDataHelper.createEnumAV("testValue", "d1", at);
    EnumAV av2 = testDataHelper.createEnumAV("value", "d2", at);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesValuesRenameTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    List<EnumAV> attributeValues = at.getSortedAttributeValues();
    assertEquals(2, attributeValues.size());
    assertEquals(av1, attributeValues.get(0));
    assertEquals("testValue", av1.getName());
    assertEquals("d1", av1.getDescription());
    assertEquals(av2, attributeValues.get(1));
    assertEquals("value", av2.getName());
    assertEquals("d2", av2.getDescription());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesEnumValuesDouble() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "enumAttributesValuesDoubleTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    EnumAT enumAt = (EnumAT) attributeTypeService.getAttributeTypeByName("EnumAt");
    assertNotNull(enumAt);
    List<EnumAV> attributeValues = enumAt.getSortedAttributeValues();
    assertEquals(2, attributeValues.size());

    EnumAV testvalue2 = attributeValues.get(0);
    assertEquals("testvalue2", testvalue2.getName());
    assertEquals("d2", testvalue2.getDescription());

    EnumAV testvalue1 = attributeValues.get(1);
    assertEquals("testvalue1", testvalue1.getName());
    assertEquals("d3", testvalue1.getDescription());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesResponsibilityUserValues() throws IOException {
    String aliceStr = "alice";
    User alice = testDataHelper.createUser(aliceStr, "", "", "");
    User bob = testDataHelper.createUser("bob", "", "", "");
    User joe = testDataHelper.createUser("joe", "", "", "");
    User tom = testDataHelper.createUser("tom", "", "", "");
    UserGroup aliceGroup = testDataHelper.createUserGroup(aliceStr + "Group");

    ResponsibilityAT responsibilityAt1 = testDataHelper.createResponsibilityAttributeType("ResponsibilityAT1", "", Boolean.TRUE,
        attributeTypeGroupService.getStandardAttributeTypeGroup());
    testDataHelper.createResponsibilityAV(responsibilityAt1, joe, tom, aliceGroup);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "responsibilityAttributeUserValueTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    List<ResponsibilityAV> responsibilityAV1 = responsibilityAt1.getSortedAttributeValues();
    assertEquals(5, responsibilityAV1.size());
    assertEquals(alice, responsibilityAV1.get(0).getUserEntity());
    assertEquals(aliceGroup, responsibilityAV1.get(1).getUserEntity());
    assertEquals(bob, responsibilityAV1.get(2).getUserEntity());
    assertEquals(joe, responsibilityAV1.get(3).getUserEntity());
    assertEquals(tom, responsibilityAV1.get(4).getUserEntity());

    ResponsibilityAT responsibilityAt2 = (ResponsibilityAT) attributeTypeService.getAttributeTypeByName("ResponsibilityAT2");
    assertNotNull(responsibilityAt2);
    User newUser = userService.getUserByLoginIfExists("newUser");
    assertNotNull(newUser);
    List<ResponsibilityAV> responsibilityAV2 = responsibilityAt2.getSortedAttributeValues();
    assertEquals(1, responsibilityAV2.size());
    assertEquals(newUser, responsibilityAV2.get(0).getUserEntity());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesResponsibilityUserGroupValues() throws IOException {
    String existinggroupStr = "existinggroup";
    UserGroup existinggroup = testDataHelper.createUserGroup(existinggroupStr);
    User existinggroupUser = testDataHelper.createUser(existinggroupStr + "User", "", "", "");

    ResponsibilityAT responsibilityAt1 = testDataHelper.createResponsibilityAttributeType("ResponsibilityAT1", "", Boolean.TRUE,
        attributeTypeGroupService.getStandardAttributeTypeGroup());
    testDataHelper.createResponsibilityAV(responsibilityAt1, existinggroup, existinggroupUser);

    ResponsibilityAT responsibilityAt2 = testDataHelper.createResponsibilityAttributeType("ResponsibilityAT2", "", Boolean.TRUE,
        attributeTypeGroupService.getStandardAttributeTypeGroup());
    testDataHelper.createResponsibilityAV(responsibilityAt2, existinggroupUser);

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "responsibilityAttributeUserGroupValueTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    List<ResponsibilityAV> responsibilityAV1 = responsibilityAt1.getSortedAttributeValues();
    assertEquals(2, responsibilityAV1.size());
    assertEquals(existinggroup, responsibilityAV1.get(0).getUserEntity());
    assertEquals(existinggroupUser, responsibilityAV1.get(1).getUserEntity());

    List<ResponsibilityAV> responsibilityAV2 = responsibilityAt2.getSortedAttributeValues();
    assertEquals(2, responsibilityAV2.size());
    assertEquals(existinggroup, responsibilityAV2.get(0).getUserEntity());
    assertEquals(existinggroupUser, responsibilityAV2.get(1).getUserEntity());

    ResponsibilityAT responsibilityAt3 = (ResponsibilityAT) attributeTypeService.getAttributeTypeByName("ResponsibilityAT3");
    List<ResponsibilityAV> responsibilityAV3 = responsibilityAt3.getSortedAttributeValues();
    assertEquals(1, responsibilityAV3.size());
    assertEquals(existinggroup, responsibilityAV3.get(0).getUserEntity());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesResponsibilityValuesWithWhitespaces() throws IOException {
    User alice = testDataHelper.createUser("alice", "", "", "");
    User bob = testDataHelper.createUser("bob", "", "", "");
    User joe = testDataHelper.createUser("joe", "", "", "");

    UserGroup existinggroup = testDataHelper.createUserGroup("existinggroup");
    UserGroup anotherexistinggroup = testDataHelper.createUserGroup("anotherexistinggroup");

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "responsibilityAttributeValuesWithWhitespacesTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    ResponsibilityAT responsibilityAt1 = (ResponsibilityAT) attributeTypeService.getAttributeTypeByName("ResponsibilityAt1");
    assertNotNull(responsibilityAt1);
    List<ResponsibilityAV> responsibilityAV1 = responsibilityAt1.getSortedAttributeValues();
    assertEquals(3, responsibilityAV1.size());
    assertEquals(alice, responsibilityAV1.get(0).getUserEntity());
    assertEquals(bob, responsibilityAV1.get(1).getUserEntity());
    assertEquals(joe, responsibilityAV1.get(2).getUserEntity());

    ResponsibilityAT responsibilityAt2 = (ResponsibilityAT) attributeTypeService.getAttributeTypeByName("ResponsibilityAt2");
    assertNotNull(responsibilityAt2);
    List<ResponsibilityAV> responsibilityAV2 = responsibilityAt2.getSortedAttributeValues();
    assertEquals(2, responsibilityAV2.size());
    assertEquals(anotherexistinggroup, responsibilityAV2.get(0).getUserEntity());
    assertEquals(existinggroup, responsibilityAV2.get(1).getUserEntity());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesRange() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "numberAttributesRangeTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    NumberAT numberAt1 = (NumberAT) attributeTypeService.getAttributeTypeByName("NumberAt1");
    assertNotNull(numberAt1);
    assertEquals(true, numberAt1.isRangeUniformyDistributed());
    assertTrue(numberAt1.getRangeValues().isEmpty());

    NumberAT numberAt2 = (NumberAT) attributeTypeService.getAttributeTypeByName("NumberAt2");
    assertNotNull(numberAt2);
    assertEquals(false, numberAt2.isRangeUniformyDistributed());
    assertEquals(new BigDecimal("123.00"), numberAt2.getRangeValues().iterator().next().getValue());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesRangeUpdate() throws IOException {
    NumberAT numberAt1 = testDataHelper.createNumberAttributeType("NumberAt1", "", attributeTypeGroupService.getStandardAttributeTypeGroup());
    testDataHelper.createRangeValue(new BigDecimal("123"), numberAt1);
    NumberAT numberAt2 = testDataHelper.createNumberAttributeType("NumberAt2", "", attributeTypeGroupService.getStandardAttributeTypeGroup());
    testDataHelper.createRangeValue(new BigDecimal("123"), numberAt2);
    testDataHelper.createRangeValue(new BigDecimal("999"), numberAt2);

    commit();
    beginTransaction();

    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "numberAttributesRangeTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    NumberAT numberAt1Reloaded = attributeTypeService.loadObjectById(numberAt1.getId(), NumberAT.class);
    NumberAT numberAt2Reloaded = attributeTypeService.loadObjectById(numberAt2.getId(), NumberAT.class);

    assertNotNull(numberAt1Reloaded);
    assertEquals(true, numberAt1Reloaded.isRangeUniformyDistributed());
    assertEquals(1, numberAt1Reloaded.getRangeValues().size());
    assertEquals(new BigDecimal("123"), numberAt1Reloaded.getRangeValues().iterator().next().getValue());

    assertNotNull(numberAt2Reloaded);
    assertEquals(false, numberAt2Reloaded.isRangeUniformyDistributed());
    assertEquals(1, numberAt2Reloaded.getRangeValues().size());
    assertEquals(new BigDecimal("123.00"), numberAt2Reloaded.getRangeValues().iterator().next().getValue());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesRangeInvalid() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "numberAttributesRangeInvalidTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    NumberAT numberAt = (NumberAT) attributeTypeService.getAttributeTypeByName("NumberAt");
    assertNotNull(numberAt);
    assertEquals(false, numberAt.isRangeUniformyDistributed());
    assertTrue(numberAt.getRangeValues().isEmpty());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesRangeToMany() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "numberAttributesRangeToManyTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    NumberAT numberAt = (NumberAT) attributeTypeService.getAttributeTypeByName("NumberAt");
    assertNotNull(numberAt);
    assertEquals(false, numberAt.isRangeUniformyDistributed());

    Set<RangeValue> ranges = numberAt.getRangeValues();
    assertEquals(4, ranges.size());

    Set<BigDecimal> values = Sets.newHashSet();
    for (RangeValue rv : ranges) {
      values.add(rv.getValue());
    }
    assertTrue(values.contains(new BigDecimal("1.00")));
    assertTrue(values.contains(new BigDecimal("2.00")));
    assertTrue(values.contains(new BigDecimal("3.00")));
    assertTrue(values.contains(new BigDecimal("4.00")));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesRangeMulti() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "numberAttributesRangeMultiTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    NumberAT numberAt = (NumberAT) attributeTypeService.getAttributeTypeByName("NumberAt");
    assertNotNull(numberAt);
    assertEquals(false, numberAt.isRangeUniformyDistributed());

    Set<RangeValue> ranges = numberAt.getRangeValues();
    assertEquals(2, ranges.size());

    Set<BigDecimal> values = Sets.newHashSet();
    for (RangeValue rv : ranges) {
      values.add(rv.getValue());
    }
    assertTrue(values.contains(new BigDecimal("1.00")));
    assertTrue(values.contains(new BigDecimal("2.00")));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportServiceImpl#importAttributes(java.io.InputStream, java.io.PrintWriter)}.
   * @throws IOException if the test Excel file will be not found
   */
  @Test
  public void testImportAttributesRangeMultiWithWhitespaces() throws IOException {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    Resource excel = new ClassPathResource(EXCEL_TEST_FILES + "numberAttributesRangeMultiWithWhitespacesTest.xls");

    excelAttributesImportService.importAttributes(excel.getInputStream(), logWriter);
    commit();
    beginTransaction();

    NumberAT numberAt = (NumberAT) attributeTypeService.getAttributeTypeByName("NumberAt");
    assertNotNull(numberAt);
    assertEquals(false, numberAt.isRangeUniformyDistributed());

    Set<RangeValue> ranges = numberAt.getRangeValues();
    assertEquals(3, ranges.size());

    Set<BigDecimal> values = Sets.newHashSet();
    for (RangeValue rv : ranges) {
      values.add(rv.getValue());
    }
    assertTrue(values.contains(new BigDecimal("1.00")));
    assertTrue(values.contains(new BigDecimal("2.00")));
    assertTrue(values.contains(new BigDecimal("3.00")));
  }

}

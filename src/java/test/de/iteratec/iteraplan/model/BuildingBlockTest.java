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
package de.iteratec.iteraplan.model;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;


/**
 * @author mma
 */
@SuppressWarnings("PMD.TooManyMethods")
public class BuildingBlockTest {

  private static final String LANG_BULGARIA   = "Bulgaria";
  private static final String USER_FIRST_NAME = "Pesho";
  private static final String USER_LAST_NAME  = "Sladuk";
  private static final String AT_NAME_1       = "first AT";
  private static final String AT_NAME_2       = "second AT";
  private static final String AV_NAME_1       = "firstAV";
  private static final String AV_NAME_2       = "secAV";

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    classUnderTest = null;
    is = new InformationSystem();
  }

  private BuildingBlock     classUnderTest;
  private InformationSystem is = new InformationSystem();

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#getAttributeStringForSearchIndexing()}. The
   * method tests if the getAttributeStringForSearchIndexing() returns correct string.
   */
  @Test
  public void testGetAttributeStringForSearchIndexing() {
    classUnderTest = new BusinessUnit();

    // AttributeType
    TextAT attributeType = new TextAT();
    attributeType.setId(Integer.valueOf(56));
    attributeType.setName("testType");

    // AttributeTypeGroup
    AttributeTypeGroup attributeTypeGroup = new AttributeTypeGroup();
    attributeTypeGroup.setName("testAttributeTypeGroup");
    attributeType.setAttributeTypeGroup(attributeTypeGroup);

    // AttributeValue
    TextAV attributeValue = new TextAV();
    attributeValue.setId(Integer.valueOf(55));
    attributeValue.setValue("testValue");

    // set for AttributeValues
    Set<AttributeValue> attributeValues = hashSet();
    attributeValues.add(attributeValue);

    // set for AttributeValueAssignment
    Set<AttributeValueAssignment> attributeValueAssignments = hashSet();
    AttributeValueAssignment firstAVA = new AttributeValueAssignment();
    firstAVA.setId(Integer.valueOf(565));
    attributeValueAssignments.add(firstAVA);

    // set the attributes for attributeValue. The both sides of the association.
    attributeValue.setAttributeValueAssignments(attributeValueAssignments);
    attributeValue.setAttributeType(attributeType);
    firstAVA.setAttributeValue(attributeValue);

    classUnderTest.setAttributeValueAssignments(attributeValueAssignments);

    String expected = "testAttributeTypeGroup: testType: testValue^^^";
    String actual = classUnderTest.getAttributeStringForSearchIndexing();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#getActiveDataSource()} The
   * method tests if the getActiveDataSource() writes correct string if the UserContext is NOT null.
   */
  @Test
  public void testGetActiveDataSource() {
    classUnderTest = new BusinessUnit();

    // data needed to initialize UserContext object

    // loginName
    String loginName = "Pesho Mamin Sladuk";

    // Role
    Role role = new Role();
    role.setId(Integer.valueOf(0));
    role.setRoleName(USER_FIRST_NAME);

    Set<Role> roles = hashSet();
    roles.add(role);

    // Locale
    Locale location = new Locale("BG", LANG_BULGARIA);

    // User
    User user = new User();
    user.setFirstName(USER_FIRST_NAME);
    user.setLastName(USER_LAST_NAME);
    user.setLoginName(loginName);

    // UserContext
    UserContext uc = new UserContext(loginName, roles, location, user);
    UserContext.setCurrentUserContext(uc);

    String expected = "MASTER";
    String actual = classUnderTest.getActiveDataSource();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#getActiveDataSource()} The
   * method tests if the getActiveDataSource() writes correct message if the UserContext is null.
   */
  @Test
  public void testGetActiveDataSourceNull() {
    classUnderTest = new BusinessUnit();

    // The class is final. That is why the object have to be set with Null.
    UserContext.setCurrentUserContext(null);
    String expected = "";
    String actual = classUnderTest.getActiveDataSource();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#getDocumentId()}.
   */
  @Test
  public void testGetDocumentIdCaseNull() {
    classUnderTest = new BusinessUnit();

    String expected = null;
    String actual = classUnderTest.getDocumentId();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#getDocumentId()}.
   */
  @Test
  public void testGetDocumentIdCaseNotNull() {
    classUnderTest = new BusinessUnit();
    classUnderTest.setId(Integer.valueOf(35));
    // data needed to initialize UserContext object

    // loginName
    String loginName = "Pesho Mamin Sladuk";

    // Role
    Role role = new Role();
    role.setId(Integer.valueOf(0));
    role.setRoleName(USER_FIRST_NAME);

    Set<Role> roles = hashSet();
    roles.add(role);

    // Locale
    Locale location = new Locale("BG", LANG_BULGARIA);

    // User
    User user = new User();
    user.setFirstName(USER_FIRST_NAME);
    user.setLastName(USER_LAST_NAME);
    user.setLoginName(loginName);

    // UserContext
    UserContext uc = new UserContext(loginName, roles, location, user);
    UserContext.setCurrentUserContext(uc);

    String expected = "MASTER" + "_35";
    String actual = classUnderTest.getDocumentId();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#getOlVersion()}
   * {@link de.iteratec.iteraplan.model.BuildingBlock#setOlVersion(java.lang.Integer)}
   * {@link de.iteratec.iteraplan.model.BuildingBlock#getId()}
   * {@link de.iteratec.iteraplan.model.BuildingBlock#setId(java.lang.Integer)}. The test method has
   * only meaning for the test coverage.
   */
  @Test
  public void testGetOlVersionGetID() {
    classUnderTest = new Transport();
    // OlVersion
    classUnderTest.setOlVersion(Integer.valueOf(65));

    Integer expectedOlVersion = Integer.valueOf(65);
    Integer actualOLVersion = classUnderTest.getOlVersion();

    assertEquals(expectedOlVersion, actualOLVersion);

    // Id
    classUnderTest.setId(Integer.valueOf(5));

    Integer expectedId = Integer.valueOf(5);
    Integer actualId = classUnderTest.getId();

    assertEquals(expectedId, actualId);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#setBuildingBlockType()}
   */
  @Test
  public void testSetBuildingBlockType() {
    classUnderTest = new BusinessObject();

    BuildingBlockType correctBB = new BuildingBlockType(TypeOfBuildingBlock.BUSINESSOBJECT);
    classUnderTest.setBuildingBlockType(correctBB);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#setBuildingBlockType()} with
   * wrong BuildingBlockType.
   */
  @Test(expected = Exception.class)
  public void testSetWrongBuildingBlockType() {
    classUnderTest = new BusinessObject();

    BuildingBlockType wrongBB = new BuildingBlockType(TypeOfBuildingBlock.BUSINESSDOMAIN);
    classUnderTest.setBuildingBlockType(wrongBB);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#getBuildingBlockType()} The
   * method has only meaning for the code coverage.
   */
  @Test
  public void testGetBuildingBlockType() {
    classUnderTest = new Transport();

    BuildingBlockType expected = new BuildingBlockType(TypeOfBuildingBlock.TRANSPORT);
    classUnderTest.setBuildingBlockType(expected);

    BuildingBlockType actual = classUnderTest.getBuildingBlockType();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#getLastModificationUser()}
   * {@link de.iteratec.iteraplan.model.BuildingBlock#getLastModificationTime()}
   * {@link de.iteratec.iteraplan.model.BuildingBlock#setLastModificationTime(java.util.Date)}
   * {@link de.iteratec.iteraplan.model.BuildingBlock#setLastModificationUser(java.lang.String)} The
   * test method has only meaning for the code coverage.
   */
  @Test
  public void testLastModificationUserAndTime() throws ParseException {
    classUnderTest = new Transport();

    // User
    classUnderTest.setLastModificationUser(USER_FIRST_NAME);

    String expectedUser = USER_FIRST_NAME;
    String actualUser = classUnderTest.getLastModificationUser();

    assertEquals(expectedUser, actualUser);

    // Date    
    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern("dd.MM.yyyy");
    Date lastModificationTime = format.parse("06.03.2005");

    classUnderTest.setLastModificationTime(lastModificationTime);

    Date expectedDate = lastModificationTime;
    Date actualDate = classUnderTest.getLastModificationTime();

    assertEquals(expectedDate, actualDate);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#addOwningUserEntity(de.iteratec.iteraplan.model.user.UserEntity)}
   * The method tests if addOwningUserEntity throws correctly an IllegalArgumentException if the
   * user tries to add null in the owningUserEntities.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddOwningUserEntityCaseException() {
    classUnderTest = new Transport();
    classUnderTest.addOwningUserEntity(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#addOwningUserEntity(de.iteratec.iteraplan.model.user.UserEntity)}
   * {@link de.iteratec.iteraplan.model.BuildingBlock#getOwningUserEntities()} The method tests if
   * addOwningUserEntity adds correctly element in the owningUserEntities.
   */
  @Test
  public void testAddOwningUserEntityCaseNoneException() {
    classUnderTest = new Transport();

    Set<UserEntity> expected = hashSet();
    User element = new User();
    expected.add(element);

    classUnderTest.addOwningUserEntity(element);
    Set<UserEntity> actual = classUnderTest.getOwningUserEntities();
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#addOwningUserEntities(java.util.Set)} The
   * method tests if addOwningUserEntities throws correctly an IllegalArgumentException if the user
   * tries to add set of UserEntinies. The set is null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddOwningUserEntitiesCaseException() {
    classUnderTest = new Transport();
    classUnderTest.addOwningUserEntities(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#addOwningUserEntities(java.util.Set)} The
   * method tests if addOwningUserEntities adds correctly a set with UserEntities.
   */
  @Test
  public void testAddOwningUserEntitiesCaseNoneException() {
    classUnderTest = new Transport();

    Set<UserEntity> expected = hashSet();
    User element = new User();
    expected.add(element);

    classUnderTest.addOwningUserEntities(expected);
    Set<UserEntity> actual = classUnderTest.getOwningUserEntities();
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#removeAttributeValueAssignments()}
   * {@link de.iteratec.iteraplan.model.BuildingBlock#getAttributeValueAssignments()} The method
   * tests if the removeAttributeValueAssignments() removes correctly all the elements from the
   * attributeValueAssignments.
   */
  @Test
  public void testRemoveAttributeValueAssignments() {
    classUnderTest = new Transport();

    // set with AttributeValueAssignment.Initialization.
    Set<AttributeValueAssignment> initSet = hashSet();

    // add some elements
    AttributeValueAssignment firstElement = new AttributeValueAssignment();
    firstElement.setId(Integer.valueOf(50));
    initSet.add(firstElement);

    AttributeValueAssignment secondElement = new AttributeValueAssignment();
    secondElement.setId(Integer.valueOf(51));
    initSet.add(secondElement);

    AttributeValueAssignment thirdElement = new AttributeValueAssignment();
    thirdElement.setId(Integer.valueOf(52));
    initSet.add(thirdElement);

    // set the attributeValueAssignments
    classUnderTest.setAttributeValueAssignments(initSet);
    // remove
    classUnderTest.removeAttributeValueAssignments();

    // actual result
    Set<AttributeValueAssignment> actual = classUnderTest.getAttributeValueAssignments();
    // expected empty set after the remove operation
    Set<AttributeValueAssignment> expected = hashSet();

    assertEquals(expected.size(), actual.size());
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#removeAttributeValueAssignmentsForAttributeType(java.lang.Integer)}
   * The method tests if the removeAttributeValueAssignmentsForAttributeType(Integer atId) removes
   * correctly the element from the attributeValueAssignments with the appropriate id.
   */
  @Test
  public void testRemoveAttributeValueAssignmentsForAttributeType() {
    classUnderTest = new Transport();

    // AbstractAttributeType
    TextAT firstTestAT = new TextAT();
    firstTestAT.setId(Integer.valueOf(51));

    // AttributeValue
    TextAV firstTestAV = new TextAV();
    firstTestAV.setId(Integer.valueOf(578));
    firstTestAV.setAttributeType(firstTestAT);

    // AbstractAttributeType
    TextAT secTestAT = new TextAT();
    secTestAT.setId(Integer.valueOf(12));

    // AttributeValue
    TextAV secTestAV = new TextAV();
    secTestAV.setId(Integer.valueOf(57));
    secTestAV.setAttributeType(secTestAT);

    // AbstractAttributeType
    TextAT thirdTestAT = new TextAT();
    thirdTestAT.setId(Integer.valueOf(1));

    // AttributeValue
    TextAV thirdTestAV = new TextAV();
    thirdTestAV.setId(Integer.valueOf(5));
    thirdTestAV.setAttributeType(thirdTestAT);
    // set with AttributeValueAssignment.Initialization.
    Set<AttributeValueAssignment> initSet = hashSet();

    // add some elements
    AttributeValueAssignment firstElement = new AttributeValueAssignment();
    firstElement.setId(Integer.valueOf(50));
    firstElement.setAttributeValue(secTestAV);
    initSet.add(firstElement);

    AttributeValueAssignment secondElement = new AttributeValueAssignment();
    secondElement.setId(Integer.valueOf(51));
    secondElement.setAttributeValue(firstTestAV);
    initSet.add(secondElement);

    AttributeValueAssignment thirdElement = new AttributeValueAssignment();
    thirdElement.setId(Integer.valueOf(52));
    thirdElement.setAttributeValue(thirdTestAV);
    initSet.add(thirdElement);

    // set the attributeValueAssignments
    classUnderTest.setAttributeValueAssignments(initSet);
    // remove
    classUnderTest.removeAttributeValueAssignmentsForAttributeType(Integer.valueOf(51));

    // actual result
    Set<AttributeValueAssignment> actual = classUnderTest.getAttributeValueAssignments();
    // expected empty set after the remove operation
    Set<AttributeValueAssignment> expected = hashSet();
    expected.add(firstElement);
    expected.add(thirdElement);

    assertEquals(expected.size(), actual.size());
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#removeOwningUserEntity(de.iteratec.iteraplan.model.user.UserEntity)}
   * the method tests if the removeOwningUserEntity(UserEntity userEntity) correctly throws an
   * IllegalArgumentException if the user tries to remove null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testRemoveOwningUserEntityCaseException() {
    classUnderTest = new Transport();
    classUnderTest.removeOwningUserEntity(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#removeOwningUserEntity(de.iteratec.iteraplan.model.user.UserEntity)}
   * the method tests if the removeOwningUserEntity(UserEntity userEntity) correctly removes
   * userEntity from the owningUserEntities.
   */
  @Test
  public void testRemoveOwningUserEntityCaseNoneException() {
    classUnderTest = new Transport();

    // set for the initialization of the owningUserEntity
    Set<UserEntity> initSet = hashSet();

    // put some elements
    User first = new User();
    first.setId(Integer.valueOf(45));
    initSet.add(first);

    User sec = new User();
    sec.setId(Integer.valueOf(46));
    initSet.add(sec);

    User third = new User();
    third.setId(Integer.valueOf(47));
    initSet.add(third);

    // initialization
    classUnderTest.setOwningUserEntities(initSet);
    // remove
    classUnderTest.removeOwningUserEntity(sec);

    Set<UserEntity> expected = hashSet();
    expected.add(first);
    expected.add(third);
    Set<UserEntity> actual = classUnderTest.getOwningUserEntities();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#removeOwningUserEntities()}
   * the method tests if the removeOwningUserEntities() correctly removes all the elements from the
   * owningUserEntities set.
   */
  @Test
  public void testRemoveOwningUserEntities() {
    classUnderTest = new Transport();

    // set for the initialization of the owningUserEntity
    Set<UserEntity> initSet = hashSet();

    // put some elements
    User first = new User();
    first.setId(Integer.valueOf(45));
    initSet.add(first);

    User sec = new User();
    sec.setId(Integer.valueOf(46));
    initSet.add(sec);

    User third = new User();
    third.setId(Integer.valueOf(47));
    initSet.add(third);

    // initialization
    classUnderTest.setOwningUserEntities(initSet);
    // remove
    classUnderTest.removeOwningUserEntities();

    Set<UserEntity> expected = hashSet();
    Set<UserEntity> actual = classUnderTest.getOwningUserEntities();
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#getAttributeTypeToAttributeValues()} The
   * method tests if the getAttributeTypeToAttributeValues() returns correct map.
   */
  @Test
  public void testGetAttributeTypeToAttributeValues() {
    classUnderTest = new Transport();

    // AttributeType
    TextAT firstAT = new TextAT();
    firstAT.setId(Integer.valueOf(1));
    firstAT.setName(AT_NAME_1);

    TextAT secAT = new TextAT();
    secAT.setId(Integer.valueOf(2));
    secAT.setName(AT_NAME_2);

    TextAT thirdAT = new TextAT();
    thirdAT.setId(Integer.valueOf(3));
    thirdAT.setName("third AT");

    // AttributeValue
    TextAV firstAV = new TextAV();
    firstAV.setId(Integer.valueOf(11));
    firstAV.setValue(AV_NAME_1);
    firstAV.setAttributeType(firstAT);

    TextAV secAV = new TextAV();
    secAV.setId(Integer.valueOf(12));
    secAV.setValue(AV_NAME_2);
    secAV.setAttributeType(secAT);

    TextAV thirdAV = new TextAV();
    thirdAV.setId(Integer.valueOf(13));
    thirdAV.setValue("thirdAV");
    thirdAV.setAttributeType(thirdAT);

    // AttributeValueAssignment
    Set<AttributeValueAssignment> initSet = hashSet();

    // put some elements
    AttributeValueAssignment firstAVA = new AttributeValueAssignment();
    firstAVA.setId(Integer.valueOf(55));
    firstAVA.setAttributeValue(firstAV);
    initSet.add(firstAVA);

    AttributeValueAssignment secAVA = new AttributeValueAssignment();
    secAVA.setId(Integer.valueOf(56));
    secAVA.setAttributeValue(secAV);
    initSet.add(secAVA);

    AttributeValueAssignment thirdAVA = new AttributeValueAssignment();
    thirdAVA.setId(Integer.valueOf(57));
    thirdAVA.setAttributeValue(thirdAV);
    initSet.add(thirdAVA);

    // initialization
    classUnderTest.setAttributeValueAssignments(initSet);

    HashBucketMap<AttributeType, AttributeValue> expected = new HashBucketMap<AttributeType, AttributeValue>();
    expected.add(firstAT, firstAV);
    expected.add(secAT, secAV);
    expected.add(thirdAT, thirdAV);

    HashBucketMap<AttributeType, AttributeValue> actual = classUnderTest.getAttributeTypeToAttributeValues();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#getAttributeValue(java.lang.String, java.util.Locale)}
   * The method tests if the getAttributeValue(String attributeName, Locale locale) returns correct
   * empty string.
   */
  @Test
  public void testGetAttributeValueCaseEmpty() {
    classUnderTest = new Transport();
    // Locale
    Locale location = new Locale("BG", LANG_BULGARIA);
    // attributeName
    String name = "";

    String actual = classUnderTest.getAttributeValue(name, location);
    String expected = "";
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#getAttributeValue(java.lang.String, java.util.Locale)}
   * The method tests if the getAttributeValue(String attributeName, Locale locale) returns correct
   * string.
   */
  @Test
  public void testGetAttributeValueCaseNotEmpty() {
    classUnderTest = new Transport();

    // AttributeType
    TextAT firstAT = new TextAT();
    firstAT.setId(Integer.valueOf(1));
    firstAT.setName(AT_NAME_1);

    TextAT secAT = new TextAT();
    secAT.setId(Integer.valueOf(2));
    secAT.setName(AT_NAME_2);

    TextAT thirdAT = new TextAT();
    thirdAT.setId(Integer.valueOf(3));
    thirdAT.setName("third AT");

    // AttributeValue
    TextAV firstAV = new TextAV();
    firstAV.setId(Integer.valueOf(11));
    firstAV.setValue(AV_NAME_1);
    firstAV.setAttributeType(firstAT);

    TextAV secAV = new TextAV();
    secAV.setId(Integer.valueOf(12));
    secAV.setValue(AV_NAME_2);
    secAV.setAttributeType(secAT);

    TextAV thirdAV = new TextAV();
    thirdAV.setId(Integer.valueOf(13));
    thirdAV.setValue("thirdAV");
    thirdAV.setAttributeType(thirdAT);

    // AttributeValueAssignment
    Set<AttributeValueAssignment> initSet = hashSet();

    // put some elements
    AttributeValueAssignment firstAVA = new AttributeValueAssignment();
    firstAVA.setId(Integer.valueOf(55));
    firstAVA.setAttributeValue(firstAV);
    initSet.add(firstAVA);

    AttributeValueAssignment secAVA = new AttributeValueAssignment();
    secAVA.setId(Integer.valueOf(56));
    secAVA.setAttributeValue(secAV);
    initSet.add(secAVA);

    AttributeValueAssignment thirdAVA = new AttributeValueAssignment();
    thirdAVA.setId(Integer.valueOf(57));
    thirdAVA.setAttributeValue(thirdAV);
    initSet.add(thirdAVA);

    // initialization
    classUnderTest.setAttributeValueAssignments(initSet);

    // Locale
    Locale location = new Locale("BG", LANG_BULGARIA);
    String actual = classUnderTest.getAttributeValue(AT_NAME_1, location);
    String expected = AV_NAME_1;
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#getConnectedAttributeValues(de.iteratec.iteraplan.model.attribute.AttributeType)}
   * The method test if the getConnectedAttributeValues(AttributeType attributeType) returns correct
   * List with elements. For that test case the log4jrootLogger must be set on DEBUG, stdout. The
   * flag is in the /iteraplan/WebContent/Web-Inf/classes/log4j.properties
   */
  @Test
  public void testGetConnectedAttributeValues() {
    classUnderTest = new Transport();

    // AttributeType
    TextAT firstAT = new TextAT();
    firstAT.setId(Integer.valueOf(1));
    firstAT.setName(AT_NAME_1);

    TextAT secAT = new TextAT();
    secAT.setId(Integer.valueOf(2));
    secAT.setName(AT_NAME_2);

    TextAT thirdAT = new TextAT();
    thirdAT.setId(Integer.valueOf(3));
    thirdAT.setName("third AT");

    // AttributeValue
    TextAV firstAV = new TextAV();
    firstAV.setId(Integer.valueOf(11));
    firstAV.setValue(AV_NAME_1);
    firstAV.setAttributeType(firstAT);

    TextAV secAV = new TextAV();
    secAV.setId(Integer.valueOf(12));
    secAV.setValue(AV_NAME_2);
    secAV.setAttributeType(secAT);

    TextAV thirdAV = new TextAV();
    thirdAV.setId(Integer.valueOf(13));
    thirdAV.setValue("thirdAV");
    thirdAV.setAttributeType(thirdAT);

    // AttributeValueAssignment
    Set<AttributeValueAssignment> initSet = hashSet();
    // put some elements
    AttributeValueAssignment firstAVA = new AttributeValueAssignment();
    firstAVA.setId(Integer.valueOf(55));
    firstAVA.setAttributeValue(firstAV);
    initSet.add(firstAVA);

    AttributeValueAssignment secAVA = new AttributeValueAssignment();
    secAVA.setId(Integer.valueOf(56));
    secAVA.setAttributeValue(secAV);
    initSet.add(secAVA);

    AttributeValueAssignment thirdAVA = new AttributeValueAssignment();
    thirdAVA.setId(Integer.valueOf(57));
    thirdAVA.setAttributeValue(thirdAV);
    initSet.add(thirdAVA);

    // initialization
    classUnderTest.setAttributeValueAssignments(initSet);

    List<AttributeValue> expected = arrayList();
    expected.add(thirdAV);

    List<AttributeValue> actual = classUnderTest.getConnectedAttributeValues(thirdAT);
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#getOwningUserEntityIds()} The
   * method tests if the getOwningUserEntityIds() returns correct Set with elements.
   */
  @Test
  public void testGetOwningUserEntityIds() {
    classUnderTest = new Transport();

    // set for initialization
    Set<UserEntity> initSet = hashSet();

    // put some users
    User firstUser = new User();
    firstUser.setId(Integer.valueOf(65));
    initSet.add(firstUser);

    User secUser = new User();
    secUser.setId(Integer.valueOf(66));
    initSet.add(secUser);

    User thirdUser = new User();
    thirdUser.setId(Integer.valueOf(67));
    initSet.add(thirdUser);

    // initialization
    classUnderTest.setOwningUserEntities(initSet);

    Set<Integer> expected = hashSet();
    expected.add(Integer.valueOf(65));
    expected.add(Integer.valueOf(66));
    expected.add(Integer.valueOf(67));

    Set<Integer> actual = classUnderTest.getOwningUserEntityIds();

    assertEquals(Boolean.valueOf(expected.contains(Integer.valueOf(65))), Boolean.valueOf(actual.contains(Integer.valueOf(65))));

    assertEquals(Boolean.valueOf(expected.contains(Integer.valueOf(66))), Boolean.valueOf(actual.contains(Integer.valueOf(66))));

    assertEquals(Boolean.valueOf(expected.contains(Integer.valueOf(67))), Boolean.valueOf(actual.contains(Integer.valueOf(67))));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#validate()}.
   */
  @Test
  public void testValidate() {
    classUnderTest = new Transport();
    classUnderTest.validate();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo(BuildingBlock o) return negative value if the this object is
   * smaller.
   */
  @Test
  public void testCompareToCaseClassUnderTestBigger() {
    TechnicalComponent firstTechComp = new TechnicalComponent();
    firstTechComp.setName("firstTestTechnComp");
    classUnderTest = firstTechComp;

    TechnicalComponent secTechComp = new TechnicalComponent();
    secTechComp.setName("secTestTechnComp");
    BuildingBlock other = secTechComp;

    int expected = -13;
    int actual = classUnderTest.compareTo(other);
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo(BuildingBlock o) return positive value if the this object is
   * bigger.
   */
  @Test
  public void testCompareToCaseClassUnderTestSmaller() {
    TechnicalComponent firstTechComp = new TechnicalComponent();
    firstTechComp.setName("secTestTechnComp");
    classUnderTest = firstTechComp;

    TechnicalComponent secTechComp = new TechnicalComponent();
    secTechComp.setName("firstTestTechnComp");
    BuildingBlock other = secTechComp;

    int expected = 13;
    int actual = classUnderTest.compareTo(other);
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlock#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo(BuildingBlock o) return 0 if the two object are equal.
   * 
   * @Test public void testCompareToEquals() { TechnicalComponent firstTechComp = new
   *       TechnicalComponent(); firstTechComp.setName("testTechnComp"); BuildingBlock
   *       classUnderTest = firstTechComp; TechnicalComponent secTechComp = new
   *       TechnicalComponent(); secTechComp.setName("testTechnComp"); BuildingBlock other =
   *       secTechComp; int expected = 0; int actual = classUnderTest.compareTo(other);
   *       assertEquals(expected, actual); }
   */

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#toString()}
   * {@link de.iteratec.iteraplan.model.BuildingBlock#getHierarchicalName()}
   * {@link de.iteratec.iteraplan.model.BuildingBlock#getNonHierarchicalName()} The method has only
   * meaning for the code coverage
   */
  @Test
  public void testToStringHierarchicalNameNonHirarchicalName() {
    classUnderTest = new Transport();
    classUnderTest.setId(Integer.valueOf(55000));

    String firstActual = classUnderTest.toString();
    String secondActual = classUnderTest.getHierarchicalName();
    String thirdActual = classUnderTest.getNonHierarchicalName();

    String expected = "55000";

    assertEquals(expected, firstActual);
    assertEquals(expected, secondActual);
    assertEquals(expected, thirdActual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#hashCode()} The method test if
   * the hashCode() returns correct hashCode. The method has meaning for the code coverage.
   */
  @Test
  public void testHashCodeCaseWithoutID() {
    classUnderTest = is;

    int actual = classUnderTest.hashCode();
    int expected = (is).hashCode();

    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#hashCode()} The method test if
   * the hashCode() returns correct hashCode. The method has meaning for the code coverage.
   */
  @Test
  public void testHashCodeCaseWithID() {
    classUnderTest = is;
    is.setId(Integer.valueOf(10));

    InformationSystem is2 = new InformationSystem();
    is2.setId(Integer.valueOf(10));

    int actual = classUnderTest.hashCode();
    int expected = (is2).hashCode();

    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#equals(java.lang.Object)} The
   * method tests if the equals() returns true correctly if the this and the other object are one
   * object.
   */
  @Test
  public void testEqualsFirstCaseTrue() {
    classUnderTest = new InformationSystem();
    classUnderTest.setId(Integer.valueOf(50));

    assertEquals(Boolean.TRUE, Boolean.valueOf(classUnderTest.equals(classUnderTest)));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#equals(java.lang.Object)} The
   * method tests if the equals() returns true correctly if the this and the other object are two
   * object but their ids are equal.
   */
  @Test
  public void testEqualsSecCaseTrue() {
    classUnderTest = new InformationSystem();
    classUnderTest.setId(Integer.valueOf(50));

    BuildingBlock other = new InformationSystem();
    other.setId(Integer.valueOf(50));

    assertEquals(Boolean.TRUE, Boolean.valueOf(classUnderTest.equals(other)));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#equals(java.lang.Object)} The
   * method tests if the equals() returns false correctly if the other object is null.
   */
  @Test
  public void testEqualsFirstCaseFalse() {
    classUnderTest = new InformationSystem();
    classUnderTest.setId(Integer.valueOf(50));

    BuildingBlock other = null;

    assertEquals(Boolean.FALSE, Boolean.valueOf(classUnderTest.equals(other)));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlock#equals(java.lang.Object)} The
   * method tests if the equals() returns false correctly if the other have different types.
   */
  @Test
  public void testEqualsSecCaseFalse() {
    classUnderTest = new InformationSystem();
    classUnderTest.setId(Integer.valueOf(50));

    BuildingBlock other = new TechnicalComponent();

    assertEquals(Boolean.FALSE, Boolean.valueOf(classUnderTest.equals(other)));
  }
}

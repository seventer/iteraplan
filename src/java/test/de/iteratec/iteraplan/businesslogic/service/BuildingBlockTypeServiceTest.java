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
package de.iteratec.iteraplan.businesslogic.service;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;


/**
 * Class for testing the service methods of the {@link BuildingBlockTypeService} interface.
 * 
 * @author mma
 */
public class BuildingBlockTypeServiceTest extends BaseTransactionalTestSupport {

  private static final String      TEST_ATG_NAME         = "testATG";
  private static final String      TEST_DESCRIPTION      = "testDescription";

  private static final String      BBT_MISSING_ERROR_MSG = "Expected Building Block %s missing";

  @Autowired
  private BuildingBlockTypeService classUnderTest        = null;

  @Autowired
  private TestDataHelper2          testDataHelper;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeServiceImpl#getBuildingBlockTypeByType(de.iteratec.iteraplan.model.TypeOfBuildingBlock)}
   * The method tests if the GetBuildingBlockTypeByType() returns the correct BuildingBlockType.
   */
  @Test
  public void testGetBuildingBlockTypeByType() {
    BuildingBlockType expected = new BuildingBlockType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, true);
    BuildingBlockType actual = classUnderTest.getBuildingBlockTypeByType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN);

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeServiceImpl#getAvailableBuildingBlockTypesForAttributeType(java.lang.Integer)}
   * The method tests if the getAvailableBuildingBlockTypesForAttributeType() returns correctly list
   * with all the BuildingBlockTypes that are connected with given id.
   */
  @Test
  public void testGetAvailableBuildingBlockTypesForAttributeTypeCaseEmptyList() {
    Integer id = Integer.valueOf(6569);

    List<BuildingBlockType> expected = arrayList();

    List<BuildingBlockType> actual = classUnderTest.getAvailableBuildingBlockTypesForAttributeType(id);

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeServiceImpl#getAvailableBuildingBlockTypesForAttributeType(java.lang.Integer)}
   * The method tests if the getAvailableBuildingBlockTypesForAttributeType() returns correctly list
   * with all the BuildingBlockTypes that are connected with given id.
   */
  @Test
  public void testGetAvailableBuildingBlockTypesForAttributeTypeFirstCaseNotEmptyList() {
    // create data
    AttributeTypeGroup testATGroup = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TEST_DESCRIPTION);
    TextAT testTextAT = testDataHelper.createTextAttributeType("testTextAT", TEST_DESCRIPTION, true, testATGroup);
    commit();

    // get all the BuildingBlockTypes in association with the given id
    beginTransaction();
    Integer id = testTextAT.getId();
    List<BuildingBlockType> actual = classUnderTest.getAvailableBuildingBlockTypesForAttributeType(id);
    commit();

    List<BuildingBlockType> expected = arrayList();
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.ISR2BOASSOCIATION));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.TCR2IEASSOCIATION));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSMAPPING));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.PROJECT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.PRODUCT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSPROCESS));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSUNIT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSOBJECT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSFUNCTION));

    // the list ordering is not 100% predictable, and not relevant --> just check for presence
    assertEquals(expected.size(), actual.size());
    for (BuildingBlockType buildingBlockType : expected) {
      Assert.assertTrue(String.format(BBT_MISSING_ERROR_MSG, buildingBlockType), actual.contains(buildingBlockType));
    }

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeServiceImpl#getAvailableBuildingBlockTypesForAttributeType(java.lang.Integer)}
   * The method tests if the getAvailableBuildingBlockTypesForAttributeType() returns correctly list
   * with all the BuildingBlockTypes that are connected with given id.
   */
  @Test
  public void testGetAvailableBuildingBlockTypesForAttributeTypeSecCaseNotEmptyList() {
    // create data
    AttributeTypeGroup testATGroup = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TEST_DESCRIPTION);
    DateAT testDateAT = testDataHelper.createDateAttributeType("testDateAT", TEST_DESCRIPTION, testATGroup);
    commit();

    // get all the BuildingBlockTypes in association with the given id
    beginTransaction();
    Integer id = testDateAT.getId();
    List<BuildingBlockType> actual = classUnderTest.getAvailableBuildingBlockTypesForAttributeType(id);
    commit();

    List<BuildingBlockType> expected = arrayList();
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.ISR2BOASSOCIATION));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.TCR2IEASSOCIATION));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSMAPPING));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.PROJECT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.PRODUCT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSPROCESS));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSUNIT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSOBJECT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSFUNCTION));

    // the list ordering is not 100% predictable, and not relevant --> just check for presence
    assertEquals(expected.size(), actual.size());
    for (BuildingBlockType buildingBlockType : expected) {
      Assert.assertTrue(String.format(BBT_MISSING_ERROR_MSG, buildingBlockType), actual.contains(buildingBlockType));
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeServiceImpl#getAvailableBuildingBlockTypesForAttributeType(java.lang.Integer)}
   * The method tests if the getAvailableBuildingBlockTypesForAttributeType() returns correctly list
   * with all the BuildingBlockTypes that are connected with given id.
   */
  @Test
  public void testGetAvailableBuildingBlockTypesForAttributeTypeThirdCaseNotEmptyList() {
    // create data
    AttributeTypeGroup testATGroup = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TEST_DESCRIPTION);
    EnumAT testEnumAT = testDataHelper.createEnumAttributeType("testEnumAT", TEST_DESCRIPTION, Boolean.TRUE, testATGroup);
    commit();

    // get all the BuildingBlockTypes in association with the given id
    beginTransaction();
    Integer id = testEnumAT.getId();
    List<BuildingBlockType> actual = classUnderTest.getAvailableBuildingBlockTypesForAttributeType(id);
    commit();

    List<BuildingBlockType> expected = arrayList();
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.ISR2BOASSOCIATION));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.TCR2IEASSOCIATION));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSMAPPING));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.PROJECT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.PRODUCT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSPROCESS));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSUNIT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSOBJECT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSFUNCTION));

    // the list ordering is not 100% predictable, and not relevant --> just check for presence
    assertEquals(expected.size(), actual.size());
    for (BuildingBlockType buildingBlockType : expected) {
      Assert.assertTrue(String.format(BBT_MISSING_ERROR_MSG, buildingBlockType), actual.contains(buildingBlockType));
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeServiceImpl#getAvailableBuildingBlockTypesForAttributeType(java.lang.Integer)}
   * The method tests if the getAvailableBuildingBlockTypesForAttributeType() returns correctly list
   * with all the BuildingBlockTypes that are connected with given id.
   */
  @Test
  public void testGetAvailableBuildingBlockTypesForAttributeTypeFourthCaseNotEmptyList() {
    // create data
    AttributeTypeGroup testATGroup = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TEST_DESCRIPTION);
    NumberAT testNumberAT = testDataHelper.createNumberAttributeType("testNumberAT", TEST_DESCRIPTION, testATGroup);
    commit();

    // get all the BuildingBlockTypes in association with the given id
    beginTransaction();
    Integer id = testNumberAT.getId();
    List<BuildingBlockType> actual = classUnderTest.getAvailableBuildingBlockTypesForAttributeType(id);
    commit();

    List<BuildingBlockType> expected = arrayList();
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.ISR2BOASSOCIATION));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.TCR2IEASSOCIATION));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSMAPPING));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.PROJECT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.PRODUCT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSPROCESS));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSUNIT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSOBJECT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSFUNCTION));

    // the list ordering is not 100% predictable, and not relevant --> just check for presence
    assertEquals(expected.size(), actual.size());
    for (BuildingBlockType buildingBlockType : expected) {
      Assert.assertTrue(String.format(BBT_MISSING_ERROR_MSG, buildingBlockType), actual.contains(buildingBlockType));
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeServiceImpl#getAvailableBuildingBlockTypesForAttributeType(java.lang.Integer)}
   * The method tests if the getAvailableBuildingBlockTypesForAttributeType() returns correctly list
   * with all the BuildingBlockTypes that are connected with given id.
   */
  @Test
  public void testGetAvailableBuildingBlockTypesForAttributeTypeFifthCaseNotEmptyList() {
    // create data
    AttributeTypeGroup testATGroup = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TEST_DESCRIPTION);
    ResponsibilityAT testNumberAT = testDataHelper.createResponsibilityAttributeType("testResponsibilityAT", TEST_DESCRIPTION, Boolean.TRUE,
        testATGroup);
    commit();

    // get all the BuildingBlockTypes in association with the given id
    beginTransaction();
    Integer id = testNumberAT.getId();
    List<BuildingBlockType> actual = classUnderTest.getAvailableBuildingBlockTypesForAttributeType(id);
    commit();

    List<BuildingBlockType> expected = arrayList();
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.ISR2BOASSOCIATION));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.TCR2IEASSOCIATION));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSMAPPING));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.PROJECT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.PRODUCT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSPROCESS));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSUNIT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSOBJECT));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSFUNCTION));

    // the list ordering is not 100% predictable, and not relevant --> just check for presence
    assertEquals(expected.size(), actual.size());
    for (BuildingBlockType buildingBlockType : expected) {
      Assert.assertTrue(String.format(BBT_MISSING_ERROR_MSG, buildingBlockType), actual.contains(buildingBlockType));
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeServiceImpl#getBuildingBlockTypesEligibleForAttributes()}
   * The method tests if the getBuildingBlockTypesEligibleForAttributes() returns correctly list
   * with all the BuildingBlockTypes that may have user defined attributes.
   */
  @Test
  public void testGetBuildingBlockTypesEligibleForAttributes() {
    List<BuildingBlockType> expected = arrayList();
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.ISR2BOASSOCIATION));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.TCR2IEASSOCIATION));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, true));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSFUNCTION, true));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSDOMAIN, true));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSOBJECT, true));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSPROCESS, true));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSUNIT, true));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, true));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, true));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, true));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, true));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.PROJECT, true));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.PRODUCT, true));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, true));
    expected.add(new BuildingBlockType(TypeOfBuildingBlock.BUSINESSMAPPING, true));
    Collections.sort(expected);

    List<BuildingBlockType> actual = classUnderTest.getBuildingBlockTypesEligibleForAttributes();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeServiceImpl#loadObjectById(Integer)}
   */
  @Test
  public void testLoadObjectById() {
    BuildingBlockType expected = classUnderTest.getBuildingBlockTypeByType(TypeOfBuildingBlock.TCR2IEASSOCIATION);
    assertNotNull(expected);
    assertEquals(TypeOfBuildingBlock.TCR2IEASSOCIATION, expected.getTypeOfBuildingBlock());

    BuildingBlockType actual = classUnderTest.loadObjectById(expected.getId());
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeServiceImpl#subscribe(Integer, boolean) }
   */
  @Test
  public void testSubscribe() {
    BuildingBlockType bbType = classUnderTest.getBuildingBlockTypeByType(TypeOfBuildingBlock.TCR2IEASSOCIATION);
    assertNotNull(bbType);
    assertEquals(TypeOfBuildingBlock.TCR2IEASSOCIATION, bbType.getTypeOfBuildingBlock());

    Integer size = classUnderTest.subscribe(bbType.getId(), true);
    assertEquals(Integer.valueOf(1), size);
    assertTrue(bbType.getSubscribedUsers().contains(UserContext.getCurrentUserContext().getUser()));

    size = classUnderTest.subscribe(bbType.getId(), false);
    assertEquals(Integer.valueOf(0), size);
    assertFalse(bbType.getSubscribedUsers().contains(UserContext.getCurrentUserContext().getUser()));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeServiceImpl#reload(java.util.Collection) }
   */
  @Test
  public void testReloadNull() {
    List<BuildingBlockType> list = classUnderTest.reload(null);
    assertNotNull(list);
    assertTrue(list.isEmpty());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeServiceImpl#reload(java.util.Collection) }
   */
  @Test
  public void testReloadEmpty() {
    List<BuildingBlockType> list = classUnderTest.reload(Lists.<BuildingBlockType> newArrayList());
    assertNotNull(list);
    assertTrue(list.isEmpty());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeServiceImpl#reload(java.util.Collection) }
   */
  @Test
  public void testReload() {
    BuildingBlockType bbType = classUnderTest.getBuildingBlockTypeByType(TypeOfBuildingBlock.TCR2IEASSOCIATION);
    assertNotNull(bbType);
    assertEquals(TypeOfBuildingBlock.TCR2IEASSOCIATION, bbType.getTypeOfBuildingBlock());
    List<BuildingBlockType> expected = Lists.newArrayList(bbType);

    BuildingBlockType newBBType = new BuildingBlockType();
    newBBType.setId(bbType.getId());
    List<BuildingBlockType> actual = Lists.newArrayList(newBBType);

    actual = classUnderTest.reload(actual);
    assertEquals(expected.get(0).getId(), actual.get(0).getId());
    assertEquals(expected.get(0).getName(), actual.get(0).getName());
    assertEquals(expected.get(0).getAttributeTypes(), actual.get(0).getAttributeTypes());
    assertEquals(expected.get(0).getSubscribedUsers(), actual.get(0).getSubscribedUsers());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeServiceImpl#saveOrUpdate(BuildingBlockType) }
   */
  @Test
  public void testSaveOrUpdate() {
    BuildingBlockType newBBType = new BuildingBlockType();
    newBBType = classUnderTest.saveOrUpdate(newBBType);
    assertNotNull(newBBType.getId());
  }
}

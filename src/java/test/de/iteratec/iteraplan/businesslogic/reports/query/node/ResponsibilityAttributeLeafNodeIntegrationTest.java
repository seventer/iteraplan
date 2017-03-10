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
package de.iteratec.iteraplan.businesslogic.reports.query.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * Integration test for {@link ResponsibilityAttributeLeafNode} class.
 *
 */
public class ResponsibilityAttributeLeafNodeIntegrationTest extends AbstractNodeTestBase {

  @Autowired
  private AttributeTypeGroupDAO attributeTypeGroupDAO;
  @Autowired
  private BuildingBlockTypeDAO  buildingBlockTypeDAO;
  @Autowired
  private TestDataHelper2       testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testResponsibilityLikeWithUsers() {
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    ResponsibilityAT respAT = testDataHelper.createResponsibilityAttributeType("ResponsibilityAT", "descr", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(respAT, bbType);
    User user1 = testDataHelper.createUser("user1", "user1", "user1", "MASTER");
    User user2 = testDataHelper.createUser("user2", "user2", "user2", "MASTER");
    User user3 = testDataHelper.createUser("user3", "user3", "user3", "MASTER");

    createISRWithResp(user1, respAT);
    createISRWithResp(user2, respAT);
    createISRWithResp(user3, respAT);
    commit();

    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    ResponsibilityAttributeLeafNode respAttributeLeafNode = ResponsibilityAttributeLeafNode.createNode(type, null, 1, Comparator.LIKE, "user*");

    Set<BuildingBlock> result = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(respAttributeLeafNode);
    assertNotNull(result);
    assertEquals(3, result.size());
  }

  @Test
  public void testResponsibilityLikeWithConcreteUser() {
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    ResponsibilityAT respAT = testDataHelper.createResponsibilityAttributeType("ResponsibilityAT", "descr", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(respAT, bbType);
    User user1 = testDataHelper.createUser("user1", "user1", "user1", "MASTER");
    User user2 = testDataHelper.createUser("user2", "user2", "user2", "MASTER");
    User user3 = testDataHelper.createUser("user3", "user3", "user3", "MASTER");

    InformationSystemRelease release1 = createISRWithResp(user1, respAT);
    createISRWithResp(user2, respAT);
    createISRWithResp(user3, respAT);
    commit();

    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    ResponsibilityAttributeLeafNode respAttributeLeafNode = ResponsibilityAttributeLeafNode.createNode(type, null, 1, Comparator.LIKE, "user1");

    Set<BuildingBlock> result = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(respAttributeLeafNode);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(release1, Iterables.get(result, 0));
  }

  @Test
  public void testResponsibilityLikeWithGroups() {
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    ResponsibilityAT respAT = testDataHelper.createResponsibilityAttributeType("ResponsibilityAT", "descr", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(respAT, bbType);
    User user1 = testDataHelper.createUser("user1", "user1", "user1", "MASTER");
    User user2 = testDataHelper.createUser("user2", "user2", "user2", "MASTER");
    User user3 = testDataHelper.createUser("user3", "user3", "user3", "MASTER");

    UserGroup group1 = testDataHelper.createUserGroup("group1", "descr", Sets.newHashSet((UserEntity) user1), Collections.<BuildingBlock> emptySet());
    UserGroup group2 = testDataHelper.createUserGroup("group2", "descr", Sets.newHashSet((UserEntity) user2), Collections.<BuildingBlock> emptySet());
    UserGroup group3 = testDataHelper.createUserGroup("group3", "descr", Sets.newHashSet((UserEntity) user3), Collections.<BuildingBlock> emptySet());

    createISRWithResp(group1, respAT);
    createISRWithResp(group2, respAT);
    createISRWithResp(group3, respAT);
    commit();

    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    ResponsibilityAttributeLeafNode respAttributeLeafNode1 = ResponsibilityAttributeLeafNode.createNode(type, null, 1, Comparator.LIKE, "user*");

    Set<BuildingBlock> userResult = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(respAttributeLeafNode1);
    assertNotNull(userResult);
    assertEquals(0, userResult.size());

    ResponsibilityAttributeLeafNode respAttributeLeafNode2 = ResponsibilityAttributeLeafNode.createNode(type, null, 1, Comparator.LIKE, "group*");
    Set<BuildingBlock> groupResult = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(respAttributeLeafNode2);
    assertNotNull(groupResult);
    assertEquals(3, groupResult.size());
  }

  @Test
  public void testResponsibilityLikeWithConcreteGroup() {
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    ResponsibilityAT respAT = testDataHelper.createResponsibilityAttributeType("ResponsibilityAT", "descr", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(respAT, bbType);
    User user1 = testDataHelper.createUser("user1", "user1", "user1", "MASTER");
    User user2 = testDataHelper.createUser("user2", "user2", "user2", "MASTER");
    User user3 = testDataHelper.createUser("user3", "user3", "user3", "MASTER");

    UserGroup group1 = testDataHelper.createUserGroup("group1", "descr", Sets.newHashSet((UserEntity) user1), Collections.<BuildingBlock> emptySet());
    UserGroup group2 = testDataHelper.createUserGroup("group2", "descr", Sets.newHashSet((UserEntity) user2), Collections.<BuildingBlock> emptySet());
    UserGroup group3 = testDataHelper.createUserGroup("group3", "descr", Sets.newHashSet((UserEntity) user3), Collections.<BuildingBlock> emptySet());

    InformationSystemRelease release1 = createISRWithResp(group1, respAT);
    createISRWithResp(group2, respAT);
    createISRWithResp(group3, respAT);
    commit();

    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    ResponsibilityAttributeLeafNode respAttributeLeafNode2 = ResponsibilityAttributeLeafNode.createNode(type, null, 1, Comparator.LIKE, "group1");
    Set<BuildingBlock> groupResult = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(respAttributeLeafNode2);
    assertNotNull(groupResult);
    assertEquals(1, groupResult.size());
    assertEquals(release1, Iterables.get(groupResult, 0));
  }

  @Test
  public void testResponsibilityAnyAssigmentWithUser() {
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    ResponsibilityAT respAT = testDataHelper.createResponsibilityAttributeType("ResponsibilityAT", "descr", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(respAT, bbType);
    User user1 = testDataHelper.createUser("user1", "user1", "user1", "MASTER");
    User user2 = testDataHelper.createUser("user2", "user2", "user2", "MASTER");
    User user3 = testDataHelper.createUser("user3", "user3", "user3", "MASTER");

    createISRWithResp(user1, respAT);
    createISRWithResp(user2, respAT);
    createISRWithResp(user3, respAT);
    commit();

    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    ResponsibilityAttributeLeafNode respAttributeLeafNode = ResponsibilityAttributeLeafNode.createNode(type, null, 1, Comparator.ANY_ASSIGNMENT, "");

    Set<BuildingBlock> result = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(respAttributeLeafNode);
    assertNotNull(result);
    assertEquals(3, result.size());
  }

  @Test
  public void testResponsibilityAnyAssigmentWithGroups() {
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    ResponsibilityAT respAT = testDataHelper.createResponsibilityAttributeType("ResponsibilityAT", "descr", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(respAT, bbType);
    User user1 = testDataHelper.createUser("user1", "user1", "user1", "MASTER");
    User user2 = testDataHelper.createUser("user2", "user2", "user2", "MASTER");
    User user3 = testDataHelper.createUser("user3", "user3", "user3", "MASTER");

    UserGroup group1 = testDataHelper.createUserGroup("group1", "descr", Sets.newHashSet((UserEntity) user1), Collections.<BuildingBlock> emptySet());
    UserGroup group2 = testDataHelper.createUserGroup("group2", "descr", Sets.newHashSet((UserEntity) user2), Collections.<BuildingBlock> emptySet());
    UserGroup group3 = testDataHelper.createUserGroup("group3", "descr", Sets.newHashSet((UserEntity) user3), Collections.<BuildingBlock> emptySet());

    createISRWithResp(group1, respAT);
    createISRWithResp(group2, respAT);
    createISRWithResp(group3, respAT);
    commit();

    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    ResponsibilityAttributeLeafNode respAttributeLeafNode2 = ResponsibilityAttributeLeafNode.createNode(type, null, 1, Comparator.ANY_ASSIGNMENT,
        "group*");
    Set<BuildingBlock> groupResult = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(respAttributeLeafNode2);
    assertNotNull(groupResult);
    assertEquals(3, groupResult.size());
  }

  @Test
  public void testResponsibilityNotLikeWithUser() {
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    ResponsibilityAT respAT = testDataHelper.createResponsibilityAttributeType("ResponsibilityAT", "descr", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(respAT, bbType);
    User user1 = testDataHelper.createUser("user1", "user1", "user1", "MASTER");
    User user2 = testDataHelper.createUser("user2", "user2", "user2", "MASTER");
    User user3 = testDataHelper.createUser("user3", "user3", "user3", "MASTER");

    createISRWithResp(user1, respAT);
    InformationSystemRelease release2 = createISRWithResp(user2, respAT);
    InformationSystemRelease release3 = createISRWithResp(user3, respAT);
    commit();

    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    ResponsibilityAttributeLeafNode respAttributeLeafNode = ResponsibilityAttributeLeafNode.createNode(type, null, 1, Comparator.NOT_LIKE, "user1");

    Set<BuildingBlock> result = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(respAttributeLeafNode);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains(release2));
    assertTrue(result.contains(release3));
  }

  @Test
  public void testResponsibilityNotLikeWithGroups() {
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    ResponsibilityAT respAT = testDataHelper.createResponsibilityAttributeType("ResponsibilityAT", "descr", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(respAT, bbType);
    User user1 = testDataHelper.createUser("user1", "user1", "user1", "MASTER");
    User user2 = testDataHelper.createUser("user2", "user2", "user2", "MASTER");
    User user3 = testDataHelper.createUser("user3", "user3", "user3", "MASTER");

    UserGroup group1 = testDataHelper.createUserGroup("group1", "descr", Sets.newHashSet((UserEntity) user1), Collections.<BuildingBlock> emptySet());
    UserGroup group2 = testDataHelper.createUserGroup("group2", "descr", Sets.newHashSet((UserEntity) user2), Collections.<BuildingBlock> emptySet());
    UserGroup group3 = testDataHelper.createUserGroup("group3", "descr", Sets.newHashSet((UserEntity) user3), Collections.<BuildingBlock> emptySet());

    createISRWithResp(group1, respAT);
    InformationSystemRelease release2 = createISRWithResp(group2, respAT);
    InformationSystemRelease release3 = createISRWithResp(group3, respAT);
    commit();

    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    ResponsibilityAttributeLeafNode respAttributeLeafNode2 = ResponsibilityAttributeLeafNode.createNode(type, null, 1, Comparator.NOT_LIKE, "group1");
    Set<BuildingBlock> groupResult = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(respAttributeLeafNode2);
    assertNotNull(groupResult);
    assertEquals(2, groupResult.size());
    assertTrue(groupResult.contains(release2));
    assertTrue(groupResult.contains(release3));
  }

  private InformationSystemRelease createISRWithResp(UserEntity userEntity, ResponsibilityAT respAT) {
    InformationSystem informationSystem = testDataHelper.createInformationSystem(RandomStringUtils.random(5));
    InformationSystemRelease release1 = testDataHelper.createInformationSystemRelease(informationSystem, RandomStringUtils.random(5));

    List<ResponsibilityAV> respAV = testDataHelper.createResponsibilityAV(respAT, userEntity);
    testDataHelper.createAVA(release1, respAV.get(0));
    return release1;
  }
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;


/**
 * Integration test for the {@link UserGroupServiceImpl} class.
 */
public class UserGroupServiceImplTest extends BaseTransactionalTestSupport {

  @Autowired
  private UserGroupService userGroupService;
  @Autowired
  private UserService      userService;
  @Autowired
  private TestDataHelper2  testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserGroupServiceImpl#deleteEntity(de.iteratec.iteraplan.model.user.UserGroup)}.
   */
  @Test
  public void testDeleteEntityUserGroup() {
    UserGroup userGroup = createSampleGroup();

    commit();
    beginTransaction();

    userGroupService.deleteEntity(userGroup);

    assertNull(userGroupService.getUserGroupById(userGroup.getId()));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserGroupServiceImpl#deleteEntity(de.iteratec.iteraplan.model.user.UserGroup)}.
   */
  @Test
  public void testDeleteEntityUserGroupNullId() {
    try {
      userGroupService.deleteEntity(new UserGroup());
      fail("expected exception: " + IllegalArgumentException.class.getSimpleName());
    } catch (IllegalArgumentException e) {
      // OK
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserGroupServiceImpl#deleteEntity(de.iteratec.iteraplan.model.user.UserGroup)}.
   */
  @Test
  public void testDeleteEntityUserGroupReferencedByAt() {
    UserGroup userGroup = createSampleGroup();
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("atg", "");
    ResponsibilityAT at1 = testDataHelper.createResponsibilityAttributeType("at1", "", Boolean.FALSE, atg);
    testDataHelper.createResponsibilityAV(at1, userGroup);
    ResponsibilityAT at2 = testDataHelper.createResponsibilityAttributeType("at2", "", Boolean.FALSE, atg);
    testDataHelper.createResponsibilityAV(at2, userGroup);

    commit();
    beginTransaction();

    try {
      userGroupService.deleteEntity(userGroup);
      fail("expected exception: " + IteraplanBusinessException.class.getSimpleName());
    } catch (IteraplanBusinessException e) {
      // OK
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserGroupServiceImpl#deleteEntity(de.iteratec.iteraplan.model.user.UserGroup)}.
   */
  @Test
  public void testDeleteEntityUserGroupReferencedByOtherGroup() {
    UserGroup userGroup = testDataHelper.createUserGroup("group");
    UserGroup otherGroup = testDataHelper.createUserGroup("otherGroup", userGroup);

    commit();
    beginTransaction();

    userGroupService.deleteEntity(userGroup);
    assertTrue(userGroupService.loadObjectById(otherGroup.getId()).getMembers().isEmpty());
  }

  private UserGroup createSampleGroup() {
    Set<UserEntity> userlist = new HashSet<UserEntity>();
    userlist.add(userService.createUser("user1"));
    userlist.add(userService.createUser("user2"));

    return testDataHelper.createUserGroup("group", "descr", userlist, new HashSet<BuildingBlock>());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserGroupServiceImpl#getAvailableUserGroupsToTransferPermissions(Integer, java.util.List)}.
   */
  @Test
  public void testGetAvailableUserGroupsToTransferPermissions() {
    UserGroup g1 = testDataHelper.createUserGroup("g1");
    UserGroup g2 = testDataHelper.createUserGroup("g2", g1);
    UserGroup g3 = testDataHelper.createUserGroup("g3", g2);

    commit();
    beginTransaction();

    assertEquals(Lists.newArrayList(g1, g2, g3), userGroupService.getAvailableUserGroupsToTransferPermissions(null, Lists.<UserGroup> newArrayList()));
    assertEquals(Lists.newArrayList(g1, g3), userGroupService.getAvailableUserGroupsToTransferPermissions(null, Lists.<UserGroup> newArrayList(g2)));
    assertEquals(Lists.newArrayList(g1), userGroupService.getAvailableUserGroupsToTransferPermissions(g2.getId(), Lists.<UserGroup> newArrayList()));
    assertEquals(Lists.newArrayList(), userGroupService.getAvailableUserGroupsToTransferPermissions(g1.getId(), Lists.<UserGroup> newArrayList()));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserGroupServiceImpl#getUserGroupById(java.lang.Integer)}.
   */
  @Test
  public void testGetUserGroupById() {
    UserGroup userGroup = createSampleGroup();
    commit();
    beginTransaction();

    assertNotNull(userGroupService.getUserGroupById(userGroup.getId()));
  }

  @Test
  public void testSaveOrUpdate() {
    UserGroup userGroup = createSampleGroup();
    commit();
    beginTransaction();

    UserGroup group = userGroupService.getUserGroupById(userGroup.getId());
    assertNotNull(group);
    assertEquals("group", group.getName());
    assertEquals("descr", group.getDescription());
    assertEquals(Sets.newHashSet("user1 user1", "user2 user2"), getMemberNames(group.getMembers()));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserGroupServiceImpl#getUserGroupsFiltered(Integer, java.util.List)}.
   */
  @Test
  public void testGetUserGroupsFiltered() {
    UserGroup g1 = testDataHelper.createUserGroup("g1");
    UserGroup g2 = testDataHelper.createUserGroup("g2");
    UserGroup g3 = testDataHelper.createUserGroup("g3");

    commit();
    beginTransaction();

    assertEquals(Lists.newArrayList(g1, g2, g3), userGroupService.getUserGroupsFiltered(null, Lists.<UserGroup> newArrayList()));
    assertEquals(Lists.newArrayList(g1, g3), userGroupService.getUserGroupsFiltered(null, Lists.<UserGroup> newArrayList(g2)));
    assertEquals(Lists.newArrayList(g2, g3), userGroupService.getUserGroupsFiltered(g2.getId(), Lists.<UserGroup> newArrayList(g1, g2)));
  }

  @Test
  public void testSaveOrUpdateWithOtherGroup() {
    UserGroup userGroup = createSampleGroup();
    UserGroup userGroup2 = testDataHelper.createUserGroup("group2", "descr2", Sets.<UserEntity> newHashSet(userGroup),
        Sets.<BuildingBlock> newHashSet());

    commit();
    beginTransaction();

    UserGroup group = userGroupService.getUserGroupById(userGroup2.getId());
    assertNotNull(group);
    assertEquals("group2", group.getName());
    assertEquals("descr2", group.getDescription());
    assertEquals(Sets.newHashSet("descr"), getMemberNames(group.getMembers()));
  }

  private Set<String> getMemberNames(Set<UserEntity> members) {
    Set<String> result = Sets.newHashSet();
    for (UserEntity userEntity : members) {
      result.add(userEntity.getDescriptiveString());
    }

    return result;
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserGroupServiceImpl#getUserGroupsBySearch(UserGroup)}.
   */
  @Test
  public void testGetUserGroupsBySearch() {
    UserGroup userGroup = testDataHelper.createUserGroup("group");

    commit();
    beginTransaction();

    List<UserGroup> userGroupBySearch = userGroupService.getUserGroupsBySearch(userGroup);
    assertEquals(Lists.newArrayList(userGroup), userGroupBySearch);
  }

}

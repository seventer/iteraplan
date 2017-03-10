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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;
import de.iteratec.iteraplan.persistence.dao.GeneralBuildingBlockDAO;


/**
 * Integration test for the {@link UserServiceImpl} class.
 */
public class UserServiceImplTest extends BaseTransactionalTestSupport {

  @Autowired
  private UserService             userService;
  @Autowired
  private UserGroupService        userGroupService;
  @Autowired
  private GeneralBuildingBlockDAO generalBuildingBlockDAO;
  @Autowired
  private BuildingBlockTypeDAO    buildingBlockTypeDAO;
  @Autowired
  private TestDataHelper2         testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#deleteEntity(de.iteratec.iteraplan.model.user.User)}.
   */
  @Test
  public void testDeleteEntityUser() {
    User user = userService.createUser("user");
    commit();
    beginTransaction();
    assertNotNull(userService.getUserByLoginIfExists("user"));

    userService.deleteEntity(user);
    assertNull(userService.getUserByLoginIfExists("user"));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#deleteEntity(de.iteratec.iteraplan.model.user.User)}.
   */
  @Test
  public void testDeleteEntityUserNull() {
    try {
      userService.deleteEntity(new User());
      fail("expected exception: " + IllegalArgumentException.class.getSimpleName());
    } catch (IllegalArgumentException e) {
      // OK
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#deleteEntity(de.iteratec.iteraplan.model.user.User)}.
   */
  @Test
  public void testDeleteEntityUserCurrent() {
    String login = UserContext.getCurrentUserContext().getLoginName();
    User user = userService.getUserByLoginIfExists(login);
    assertNotNull(user);
    try {
      userService.deleteEntity(user);
      fail("expected exception: " + IteraplanBusinessException.class.getSimpleName());
    } catch (IteraplanBusinessException e) {
      // OK
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#deleteEntity(de.iteratec.iteraplan.model.user.User)}.
   */
  @Test
  public void testDeleteEntityUserReferencedByAt() {
    User user = testDataHelper.createUser("user");
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("atg", "");
    ResponsibilityAT at1 = testDataHelper.createResponsibilityAttributeType("at1", "", Boolean.FALSE, atg);
    testDataHelper.createResponsibilityAV(at1, user);
    ResponsibilityAT at2 = testDataHelper.createResponsibilityAttributeType("at2", "", Boolean.FALSE, atg);
    testDataHelper.createResponsibilityAV(at2, user);

    commit();
    beginTransaction();

    try {
      userService.deleteEntity(user);
      fail("expected exception: " + IteraplanBusinessException.class.getSimpleName());
    } catch (IteraplanBusinessException e) {
      // OK
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#deleteEntity(de.iteratec.iteraplan.model.user.User)}.
   */
  @Test
  public void testDeleteEntityUserReferencedByGroup() {
    User user = testDataHelper.createUser("user");
    UserGroup ug = testDataHelper.createUserGroup("usergroup", user);

    commit();
    beginTransaction();

    userService.deleteEntity(user);
    assertTrue(userGroupService.loadObjectById(ug.getId()).getMembers().isEmpty());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#deleteEntity(de.iteratec.iteraplan.model.user.User)}.
   */
  @Test
  public void testDeleteEntityUserWithBBSubscriptions() {
    BusinessDomain bd = testDataHelper.createBusinessDomain("bd", "desc");
    User user = testDataHelper.createUser("user");
    testDataHelper.addSubscription(user, bd);

    commit();
    beginTransaction();

    userService.deleteEntity(user);
    assertTrue(generalBuildingBlockDAO.loadObjectById(bd.getId()).getSubscribedUsers().isEmpty());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#deleteEntity(de.iteratec.iteraplan.model.user.User)}.
   */
  @Test
  public void testDeleteEntityUserWithBBTSubscriptions() {
    BuildingBlockType bbt = testDataHelper.getBuildingBlockType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN);
    User user = testDataHelper.createUser("user");
    testDataHelper.addSubscription(user, bbt);

    commit();
    beginTransaction();

    userService.deleteEntity(user);
    assertTrue(buildingBlockTypeDAO.loadObjectById(bbt.getId()).getSubscribedUsers().isEmpty());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#getAggregatedOwningUserEntities(java.util.List)}.
   */
  @Test
  public void testGetAggregatedOwningUserEntities() {
    User u1 = testDataHelper.createUser("u1");
    User u2 = testDataHelper.createUser("u2");
    User u3 = testDataHelper.createUser("u3");

    UserGroup g1 = testDataHelper.createUserGroup("g1", "d", u1);
    UserGroup g2 = testDataHelper.createUserGroup("g2", "e", u2, u3);
    UserGroup g3 = testDataHelper.createUserGroup("g3", "f", g1, g2, u3);
    UserGroup g4 = testDataHelper.createUserGroup("g4", "g", g3);

    commit();
    beginTransaction();

    assertEquals(Lists.newArrayList(u1), userService.getAggregatedOwningUserEntities(Lists.<UserEntity> newArrayList(u1)));
    assertEquals(Lists.newArrayList(u1, u3), userService.getAggregatedOwningUserEntities(Lists.<UserEntity> newArrayList(u1, u3)));
    assertEquals(Lists.newArrayList(u1, g1), userService.getAggregatedOwningUserEntities(Lists.<UserEntity> newArrayList(g1)));
    assertEquals(Lists.newArrayList(u2, u3, g2), userService.getAggregatedOwningUserEntities(Lists.<UserEntity> newArrayList(g2)));
    assertEquals(Lists.newArrayList(u1, u2, u3, g2), userService.getAggregatedOwningUserEntities(Lists.<UserEntity> newArrayList(g2, u1)));
    assertEquals(Lists.newArrayList(u1, u2, u3, g1, g2, g3), userService.getAggregatedOwningUserEntities(Lists.<UserEntity> newArrayList(g3)));
    assertEquals(Lists.newArrayList(u1, u2, u3, g1, g2, g3, g4), userService.getAggregatedOwningUserEntities(Lists.<UserEntity> newArrayList(g4)));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#getAllUserEntitiesFiltered(Integer, java.util.Set)}.
   */
  @Test
  public void testGetAllUserEntitiesFiltered() {
    User u1 = testDataHelper.createUser("u1");
    User u2 = testDataHelper.createUser("u2");
    User system = UserContext.getCurrentUserContext().getUser();
    UserGroup g1 = testDataHelper.createUserGroup("g1", "d", u1, u2);

    commit();
    beginTransaction();

    assertEquals(Lists.newArrayList(g1, system, u2), userService.getAllUserEntitiesFiltered(Sets.newHashSet(u1.getId())));
    assertEquals(Lists.newArrayList(g1, system), userService.getAllUserEntitiesFiltered(Sets.newHashSet(u1.getId(), u2.getId())));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#getUserByLoginIfExists(java.lang.String)}.
   */
  @Test
  public void testGetUserByLoginIfExists() {
    userService.createUser("user");
    commit();
    beginTransaction();

    assertNotNull(userService.getUserByLoginIfExists("user"));
    assertNull(userService.getUserByLoginIfExists("nonExistingUser"));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#getUserByLoginIfExists(java.lang.String)}.
   */
  @Test
  public void testGetUserByLoginIfExistsWhenNoUserExist() {
    assertNull(userService.getUserByLoginIfExists("nonExistingUser"));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#getUsersFiltered(Integer, java.util.List)}.
   */
  @Test
  public void testGetUsersFiltered() {
    User u1 = testDataHelper.createUser("u1");
    User u2 = testDataHelper.createUser("u2");
    User system = UserContext.getCurrentUserContext().getUser();

    commit();
    beginTransaction();

    assertEquals(Lists.newArrayList(system, u2), userService.getUsersFiltered(null, Lists.<User> newArrayList(u1)));
    assertEquals(Lists.newArrayList(system), userService.getUsersFiltered(null, Lists.<User> newArrayList(u1, u2)));
    assertEquals(Lists.newArrayList(system, u1), userService.getUsersFiltered(u1.getId(), Lists.<User> newArrayList(u2)));
    assertEquals(Lists.newArrayList(system, u1), userService.getUsersFiltered(u1.getId(), Lists.<User> newArrayList(u1, u2)));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#getUserBySearch(User)}.
   */
  @Test
  public void testGetUserBySearch() {
    User u1 = testDataHelper.createUser("u1");

    commit();
    beginTransaction();

    List<User> userBySearch = userService.getUserBySearch(u1);
    assertEquals(Lists.newArrayList(u1), userBySearch);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#createUser(java.lang.String)}.
   */
  @Test
  public void testCreateNonExistingUser() {
    userService.createUser("user");
    commit();
    beginTransaction();

    assertNotNull(userService.getUserByLoginIfExists("user"));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#createUser(java.lang.String)}.
   */
  @Test
  public void testCreateNonExistingUserTwice() {
    try {
      userService.createUser("user");
      userService.createUser("user");
      fail();
    } catch (IteraplanBusinessException e) {
      //ok
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserServiceImpl#createUser(java.lang.String)}.
   */
  @Test
  public void testCreateNonExistingUserWithEmptyName() {
    try {
      userService.createUser("   ");
      fail();
    } catch (IteraplanBusinessException e) {
      //ok
    }
  }

  @Test
  public void testSaveOrUpdate() {
    User user = new User();
    user.setLoginName("user");
    user.setFirstName("Peter");
    user.setLastName("Muller");
    user.setEmail("peter.muller@gmail.com");
    user.setDataSource("datasource");

    userService.saveOrUpdate(user);

    commit();
    beginTransaction();

    assertNotNull(userService.getUserByLoginIfExists("user"));
    assertEquals("user", user.getLoginName());
    assertEquals("Peter", user.getFirstName());
    assertEquals("Muller", user.getLastName());
    assertEquals("peter.muller@gmail.com", user.getEmail());
    assertEquals("datasource", user.getDataSource());
  }
}

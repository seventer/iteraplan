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
package de.iteratec.iteraplan.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.Role2BbtPermission;
import de.iteratec.iteraplan.model.user.Role2BbtPermission.EditPermissionType;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.persistence.dao.RoleDAO;
import de.iteratec.iteraplan.persistence.dao.UserDAO;


public class UserContextTest extends BaseTransactionalTestSupport {

  private static final Logger LOGGER   = Logger.getIteraplanLogger(UserContextTest.class);
  private static final String TMP_FILE = "userContextTest12345.tmp";
  @Autowired
  private RoleDAO             roleDao;
  @Autowired
  private UserDAO             userDao;
  @Autowired
  private TestDataHelper2     testDataHelper;
  
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testUserHasBbTypeWritePermission1() {
    // User with no roles.
    User user = testDataHelper.getEmptyUser();
    UserContext uc = new UserContext(user.getLoginName(), new HashSet<Role>(), Locale.GERMAN, user);

    List<TypeOfBuildingBlock> allTobb = new ArrayList<TypeOfBuildingBlock>(TypeOfBuildingBlock.ALL);
    for (TypeOfBuildingBlock tobb : allTobb) {
      assertFalse(uc.getPerms().userHasBbTypeUpdatePermission(tobb));
      assertFalse(uc.getPerms().userHasBbTypeCreatePermission(tobb));
      assertFalse(uc.getPerms().userHasBbTypeDeletePermission(tobb));
    }

    // User with one role (permission for information system releases).
    BuildingBlockType bbt = new BuildingBlockType();
    bbt.setTypeOfBuildingBlock(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    Role role = testDataHelper.getEmptyRole();
    new Role2BbtPermission(role, bbt, EditPermissionType.UPDATE).connect();
    new Role2BbtPermission(role, bbt, EditPermissionType.CREATE).connect();
    new Role2BbtPermission(role, bbt, EditPermissionType.DELETE).connect();
    Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    user = testDataHelper.getEmptyUser();
    uc = new UserContext(user.getLoginName(), roles, Locale.GERMAN, user);
    for (TypeOfBuildingBlock tobb : allTobb) {
      if (tobb.equals(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE)) {
        assertTrue(uc.getPerms().userHasBbTypeUpdatePermission(tobb));
        assertTrue(uc.getPerms().userHasBbTypeCreatePermission(tobb));
        assertTrue(uc.getPerms().userHasBbTypeDeletePermission(tobb));
      }
      else {
        assertFalse(uc.getPerms().userHasBbTypeUpdatePermission(tobb));
        assertFalse(uc.getPerms().userHasBbTypeCreatePermission(tobb));
        assertFalse(uc.getPerms().userHasBbTypeDeletePermission(tobb));
      }
    }

    // User with one role (no permissions for any building block type).
    role = testDataHelper.getEmptyRole();
    roles = new HashSet<Role>();
    roles.add(role);
    user = testDataHelper.getEmptyUser();
    uc = new UserContext(user.getLoginName(), roles, Locale.GERMAN, user);
    for (TypeOfBuildingBlock tobb : allTobb) {
      assertFalse(uc.getPerms().userHasBbTypeUpdatePermission(tobb));
      assertFalse(uc.getPerms().userHasBbTypeCreatePermission(tobb));
      assertFalse(uc.getPerms().userHasBbTypeDeletePermission(tobb));
    }

    // Admin user.
    Set<Role> adminRole = new HashSet<Role>();
    adminRole.add(roleDao.getSupervisorRole());
    User admin = testDataHelper.getEmptyUser();
    uc = new UserContext(admin.getLoginName(), adminRole, Locale.GERMAN, user);
    for (TypeOfBuildingBlock tobb : allTobb) {
      assertTrue(uc.getPerms().userHasBbTypeUpdatePermission(tobb));
      assertTrue(uc.getPerms().userHasBbTypeCreatePermission(tobb));
      assertTrue(uc.getPerms().userHasBbTypeDeletePermission(tobb));
    }
  }

  @Test
  public void testUserHasBbTypeUpdatePermission() {
    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE;

    BuildingBlockType bbt = new BuildingBlockType();
    bbt.setTypeOfBuildingBlock(tobb);
    Role role = testDataHelper.getEmptyRole();
    new Role2BbtPermission(role, bbt, EditPermissionType.UPDATE).connect();
    Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    User user = testDataHelper.getEmptyUser();
    UserContext uc = new UserContext(user.getLoginName(), roles, Locale.GERMAN, user);

    assertTrue(uc.getPerms().userHasBbTypeUpdatePermission(tobb));
    assertFalse(uc.getPerms().userHasBbTypeCreatePermission(tobb));
    assertFalse(uc.getPerms().userHasBbTypeDeletePermission(tobb));
  }

  @Test
  public void testUserHasBbTypeCreatePermission() {
    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE;

    BuildingBlockType bbt = new BuildingBlockType();
    bbt.setTypeOfBuildingBlock(tobb);
    Role role = testDataHelper.getEmptyRole();
    new Role2BbtPermission(role, bbt, EditPermissionType.CREATE).connect();
    Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    User user = testDataHelper.getEmptyUser();
    UserContext uc = new UserContext(user.getLoginName(), roles, Locale.GERMAN, user);

    assertFalse(uc.getPerms().userHasBbTypeUpdatePermission(tobb));
    assertTrue(uc.getPerms().userHasBbTypeCreatePermission(tobb));
    assertFalse(uc.getPerms().userHasBbTypeDeletePermission(tobb));
  }

  @Test
  public void testUserHasBbTypeDeletePermission() {
    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE;

    BuildingBlockType bbt = new BuildingBlockType();
    bbt.setTypeOfBuildingBlock(tobb);
    Role role = testDataHelper.getEmptyRole();
    new Role2BbtPermission(role, bbt, EditPermissionType.DELETE).connect();
    Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    User user = testDataHelper.getEmptyUser();
    UserContext uc = new UserContext(user.getLoginName(), roles, Locale.GERMAN, user);

    assertFalse(uc.getPerms().userHasBbTypeUpdatePermission(tobb));
    assertFalse(uc.getPerms().userHasBbTypeCreatePermission(tobb));
    assertTrue(uc.getPerms().userHasBbTypeDeletePermission(tobb));
  }

  @Test
  public void testUserHasFunctionalPermissionBasic() throws IteraplanException {
    // User with no functional permissions.
    User user = testDataHelper.getEmptyUser();
    UserContext uc = new UserContext(user.getLoginName(), new HashSet<Role>(), Locale.GERMAN, user);
    List<TypeOfFunctionalPermission> allTofp = TypeOfFunctionalPermission.ALL;
    for (TypeOfFunctionalPermission tofp : allTofp) {
      assertFalse(uc.getPerms().userHasFunctionalPermission(tofp));
    }

    // User with one role (functional permission for technical components).
    PermissionFunctional pf = new PermissionFunctional();
    pf.setTypeOfFunctionalPermission(TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES);
    Role role = testDataHelper.getEmptyRole();
    role.getPermissionsFunctional().add(pf);
    Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    user = testDataHelper.getEmptyUser();
    uc = new UserContext(user.getLoginName(), roles, Locale.GERMAN, user);
    for (TypeOfFunctionalPermission tofp : allTofp) {
      if (tofp.equals(TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES)) {
        assertTrue(uc.getPerms().userHasFunctionalPermission(tofp));
      }
      else {
        assertFalse(uc.getPerms().userHasFunctionalPermission(tofp));
      }
    }
    // User with one role (no permissions for any building block type).
    role = testDataHelper.getEmptyRole();
    roles.clear();
    roles.add(role);
    user = testDataHelper.getEmptyUser();
    uc = new UserContext(user.getLoginName(), roles, Locale.GERMAN, user);
    for (TypeOfFunctionalPermission tofp : allTofp) {
      assertFalse(uc.getPerms().userHasFunctionalPermission(tofp));
    }

    // Admin user.
    roles.clear();
    roles.add(roleDao.getSupervisorRole());
    User admin = testDataHelper.getEmptyUser();
    uc = new UserContext(admin.getLoginName(), roles, Locale.GERMAN, user);
    for (TypeOfFunctionalPermission tofp : allTofp) {
      assertTrue(uc.getPerms().userHasFunctionalPermission(tofp));
    }
  }

  @Test
  public void testUserHasBbInstanceWritePermissionBasic() throws IteraplanException {
    // User with no owned entities.
    User user = testDataHelper.createUser("login", "", "", Constants.MASTER_DATA_SOURCE);
    LOGGER.info(" ************ {0}", user.getId());
    UserContext uc = new UserContext(user.getLoginName(), new HashSet<Role>(), Locale.GERMAN, userDao.getUserByLoginIfExists(user.getLoginName()));
    InformationSystemRelease ipur = new InformationSystemRelease();
    ipur.setOwningUserEntities(new HashSet<UserEntity>());
    assertTrue(uc.getPerms().userHasBbInstanceWritePermission(ipur));

    // Same with admin user.
    Set<Role> roles = new HashSet<Role>();
    roles.add(roleDao.getSupervisorRole());
    User admin = testDataHelper.getEmptyUser();
    UserContext adminUc = new UserContext(admin.getLoginName(), roles, Locale.GERMAN, user);
    assertTrue(adminUc.getPerms().userHasBbInstanceWritePermission(ipur));

    // User with no owned entities and entity owned by another user.
    UserEntity user2 = testDataHelper.getEmptyUser();
    user2.setId(Integer.valueOf(202));
    ipur.setOwningUserEntities(new HashSet<UserEntity>());
    ipur.getOwningUserEntities().add(user2);
    assertFalse(uc.getPerms().userHasBbInstanceWritePermission(ipur));
    assertTrue(adminUc.getPerms().userHasBbInstanceWritePermission(ipur));

    // User with owned entity.
    ipur.setOwningUserEntities(new HashSet<UserEntity>());
    ipur.getOwningUserEntities().add(user);
    assertTrue(uc.getPerms().userHasBbInstanceWritePermission(ipur));
    assertTrue(adminUc.getPerms().userHasBbInstanceWritePermission(ipur));

    // User with no owned entities and entity owned by another usergroup.
    UserGroup ug = new UserGroup();
    ug.setId(Integer.valueOf(300));
    ipur.setOwningUserEntities(new HashSet<UserEntity>());
    ipur.getOwningUserEntities().add(ug);
    assertFalse(uc.getPerms().userHasBbInstanceWritePermission(ipur));
    assertTrue(adminUc.getPerms().userHasBbInstanceWritePermission(ipur));

    // User in usergroup that owns entity.
    user = testDataHelper.createUser("test2", "", "", Constants.MASTER_DATA_SOURCE);
    ug.addUserEntity(user);
    ug.setParentUserGroups(new HashSet<UserGroup>());
    uc = new UserContext(user.getLoginName(), new HashSet<Role>(), Locale.GERMAN, userDao.getUserByLoginIfExists(user.getLoginName()));
    assertTrue(uc.getPerms().userHasBbInstanceWritePermission(ipur));
    assertTrue(adminUc.getPerms().userHasBbInstanceWritePermission(ipur));
  }

  @Test
  public void testUserHasAttrTypeGroupPermissionBasic() throws IteraplanException {
    // Admin user.
    Set<Role> roles = new HashSet<Role>();
    roles.add(roleDao.getSupervisorRole());
    User admin = testDataHelper.getEmptyUser();
    UserContext adminUc = new UserContext(admin.getLoginName(), roles, Locale.GERMAN, admin);
    AttributeTypeGroup atg = new AttributeTypeGroup();
    atg.setPermissionsRole(new HashSet<PermissionAttrTypeGroup>());
    assertTrue(adminUc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ));
    assertTrue(adminUc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ_WRITE));

    // User with no attribute type group read permissions.
    User user = testDataHelper.getEmptyUser();
    UserContext uc = new UserContext(user.getLoginName(), new HashSet<Role>(), Locale.GERMAN, user);
    assertTrue(adminUc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ));
    assertTrue(adminUc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ_WRITE));

    // User with no roles and group owned by role.
    Role role = testDataHelper.getEmptyRole();
    role.setId(Integer.valueOf(100));
    PermissionAttrTypeGroup patg = new PermissionAttrTypeGroup();
    patg.setRole(role);
    patg.setReadPermission(Boolean.TRUE);
    patg.setWritePermission(Boolean.FALSE);
    atg.getPermissionsRole().add(patg);
    assertFalse(uc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ));
    assertFalse(uc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ_WRITE));
    assertTrue(adminUc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ));
    assertTrue(adminUc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ_WRITE));

    // User with role and role has read permission for attribute type group.
    roles.clear();
    roles.add(role);
    uc = new UserContext(user.getLoginName(), roles, Locale.GERMAN, user);
    assertTrue(uc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ));
    assertFalse(uc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ_WRITE));
    assertTrue(adminUc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ));
    assertTrue(adminUc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ_WRITE));

    // User with role and group owned by two roles.
    Role role2 = testDataHelper.getEmptyRole();
    role2.setId(Integer.valueOf(102));
    PermissionAttrTypeGroup patg2 = new PermissionAttrTypeGroup();
    patg2.setRole(role2);
    patg2.setReadPermission(Boolean.TRUE);
    patg2.setWritePermission(Boolean.TRUE);
    atg.getPermissionsRole().add(patg2);
    assertTrue(uc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ));
    assertFalse(uc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ_WRITE));
    assertTrue(adminUc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ));
    assertTrue(adminUc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ_WRITE));

    // User with two roles and group owned by two roles (one role overlapping).
    Role role3 = testDataHelper.getEmptyRole();
    role3.setId(Integer.valueOf(103));
    PermissionAttrTypeGroup patg3 = new PermissionAttrTypeGroup();
    patg3.setRole(role3);
    patg3.setReadPermission(Boolean.TRUE);
    patg3.setWritePermission(Boolean.TRUE);
    atg.getPermissionsRole().add(patg3);
    roles.clear();
    roles.add(role);
    roles.add(role3);
    user = testDataHelper.getEmptyUser();
    uc = new UserContext(user.getLoginName(), roles, Locale.GERMAN, user);
    assertTrue(uc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ));
    assertTrue(uc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ_WRITE));
    assertTrue(adminUc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ));
    assertTrue(adminUc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ_WRITE));
  }

  @Test
  public void testUserGroupInheritance() {
    UserGroup ug1 = new UserGroup();
    UserGroup ug2 = new UserGroup();
    UserGroup ug3 = new UserGroup();
    UserGroup ug4 = new UserGroup();
    UserGroup ug5 = new UserGroup();
    ug1.setParentUserGroups(new HashSet<UserGroup>());
    ug1.setId(Integer.valueOf(201));
    ug2.setParentUserGroups(new HashSet<UserGroup>());
    ug2.setId(Integer.valueOf(202));
    ug3.setParentUserGroups(new HashSet<UserGroup>());
    ug3.setId(Integer.valueOf(203));
    ug4.setParentUserGroups(new HashSet<UserGroup>());
    ug4.setId(Integer.valueOf(204));
    ug5.setParentUserGroups(new HashSet<UserGroup>());
    ug5.setId(Integer.valueOf(205));

    ug2.getParentUserGroups().add(ug1);
    ug3.getParentUserGroups().add(ug2);
    ug4.getParentUserGroups().add(ug2);

    User user = testDataHelper.createUser("test", "", "", Constants.MASTER_DATA_SOURCE);
    user.setId(Integer.valueOf(1));
    user.getParentUserGroups().add(ug3);
    user.getParentUserGroups().add(ug5);

    UserEntity otherUser = testDataHelper.getEmptyUser();
    user.setId(Integer.valueOf(2));
    otherUser.getParentUserGroups().add(ug1);

    InformationSystem bb1 = new InformationSystem();
    bb1.setId(Integer.valueOf(101));
    InformationSystem bb2 = new InformationSystem();
    bb2.setId(Integer.valueOf(102));
    InformationSystem bb3 = new InformationSystem();
    bb3.setId(Integer.valueOf(103));
    InformationSystem bb4 = new InformationSystem();
    bb4.setId(Integer.valueOf(104));
    InformationSystem bb5 = new InformationSystem();
    bb5.setId(Integer.valueOf(105));
    InformationSystem bb6 = new InformationSystem();
    bb6.setId(Integer.valueOf(106));
    InformationSystem bb7 = new InformationSystem();
    bb7.setId(Integer.valueOf(107));

    ug1.addOwnedBuildingBlock(bb1);
    ug2.addOwnedBuildingBlock(bb2);
    ug3.addOwnedBuildingBlock(bb3);
    ug4.addOwnedBuildingBlock(bb4);
    ug5.addOwnedBuildingBlock(bb5);
    user.addOwnedBuildingBlock(bb6);
    otherUser.addOwnedBuildingBlock(bb7);

    UserContext uc = new UserContext(user.getLoginName(), new HashSet<Role>(), Locale.GERMAN, userDao.getUserByLoginIfExists(user.getLoginName()));
    assertTrue(uc.getPerms().userHasBbInstanceWritePermission(bb1));
    assertTrue(uc.getPerms().userHasBbInstanceWritePermission(bb2));
    assertTrue(uc.getPerms().userHasBbInstanceWritePermission(bb3));
    assertFalse(uc.getPerms().userHasBbInstanceWritePermission(bb4));
    assertTrue(uc.getPerms().userHasBbInstanceWritePermission(bb5));
    assertTrue(uc.getPerms().userHasBbInstanceWritePermission(bb6));
    assertFalse(uc.getPerms().userHasBbInstanceWritePermission(bb7));
  }

  @Test
  public void testRoleInheritance() {
    BuildingBlockType bbt1 = createBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    BuildingBlockType bbt2 = createBuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);
    BuildingBlockType bbt3 = createBuildingBlockType(TypeOfBuildingBlock.PROJECT);
    BuildingBlockType bbt4 = createBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE);
    BuildingBlockType bbt5 = createBuildingBlockType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT);

    PermissionFunctional pf1 = createPermissionFunctional(TypeOfFunctionalPermission.INFORMATIONSYSTEMRELEASE);
    PermissionFunctional pf2 = createPermissionFunctional(TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES);
    PermissionFunctional pf3 = createPermissionFunctional(TypeOfFunctionalPermission.PROJECT);
    PermissionFunctional pf4 = createPermissionFunctional(TypeOfFunctionalPermission.INFORMATIONSYSTEMINTERFACE);
    PermissionFunctional pf5 = createPermissionFunctional(TypeOfFunctionalPermission.INFRASTRUCTUREELEMENT);

    Role role1 = createRole(Integer.valueOf(101), "role1", bbt1, pf1);
    Role role2 = createRole(Integer.valueOf(102), "role2", bbt2, pf2);
    Role role3 = createRole(Integer.valueOf(103), "role3", bbt3, pf3);
    Role role4 = createRole(Integer.valueOf(104), "role4", bbt4, pf4);
    Role role5 = createRole(Integer.valueOf(105), "role5", bbt5, pf5);

    role1.addConsistsOfRoleTwoWay(role2);
    role2.addConsistsOfRoleTwoWay(role3);
    role2.addConsistsOfRoleTwoWay(role4);
    role4.addConsistsOfRoleTwoWay(role5);

    PermissionAttrTypeGroup patg1 = createPermissionAttrTypeGroup(Boolean.TRUE, Boolean.TRUE, role1);
    PermissionAttrTypeGroup patg2 = createPermissionAttrTypeGroup(Boolean.TRUE, Boolean.FALSE, role2);
    PermissionAttrTypeGroup patg3 = createPermissionAttrTypeGroup(Boolean.TRUE, Boolean.FALSE, role2);
    PermissionAttrTypeGroup patg4 = createPermissionAttrTypeGroup(Boolean.TRUE, Boolean.FALSE, role3);
    PermissionAttrTypeGroup patg5 = createPermissionAttrTypeGroup(Boolean.TRUE, Boolean.TRUE, role4);
    PermissionAttrTypeGroup patg6 = createPermissionAttrTypeGroup(Boolean.TRUE, Boolean.TRUE, role5);

    AttributeTypeGroup atg1 = createAttributeTypeGroup(patg1, patg2);
    AttributeTypeGroup atg2 = createAttributeTypeGroup(patg4, patg6);
    AttributeTypeGroup atg3 = createAttributeTypeGroup(patg3, patg5);

    Set<Role> roles = new HashSet<Role>();
    roles.add(role2);

    // Get aggregated roles.
    Set<Role> aggregatedRoles = new HashSet<Role>();
    for (Role role : roles) {
      aggregatedRoles.add(role);
      aggregatedRoles.addAll(role.getConsistsOfRolesAggregated());
    }

    LOGGER.info("Creating usercontext");
    User user = testDataHelper.createUser("test", "", "", Constants.MASTER_DATA_SOURCE);
    UserContext uc = new UserContext(user.getLoginName(), aggregatedRoles, Locale.GERMAN, userDao.getUserByLoginIfExists(user.getLoginName()));

    assertFalse(uc.getPerms().userHasFunctionalPermission(pf1.getTypeOfFunctionalPermission()));
    assertTrue(uc.getPerms().userHasFunctionalPermission(pf2.getTypeOfFunctionalPermission()));
    assertTrue(uc.getPerms().userHasFunctionalPermission(pf3.getTypeOfFunctionalPermission()));
    assertTrue(uc.getPerms().userHasFunctionalPermission(pf4.getTypeOfFunctionalPermission()));
    assertTrue(uc.getPerms().userHasFunctionalPermission(pf5.getTypeOfFunctionalPermission()));

    assertFalse(uc.getPerms().userHasBbTypeUpdatePermission(bbt1.getTypeOfBuildingBlock()));
    assertFalse(uc.getPerms().userHasBbTypeCreatePermission(bbt1.getTypeOfBuildingBlock()));
    assertFalse(uc.getPerms().userHasBbTypeDeletePermission(bbt1.getTypeOfBuildingBlock()));

    assertTrue(uc.getPerms().userHasBbTypeUpdatePermission(bbt2.getTypeOfBuildingBlock()));
    assertTrue(uc.getPerms().userHasBbTypeCreatePermission(bbt2.getTypeOfBuildingBlock()));
    assertTrue(uc.getPerms().userHasBbTypeDeletePermission(bbt2.getTypeOfBuildingBlock()));

    assertTrue(uc.getPerms().userHasBbTypeUpdatePermission(bbt3.getTypeOfBuildingBlock()));
    assertTrue(uc.getPerms().userHasBbTypeCreatePermission(bbt3.getTypeOfBuildingBlock()));
    assertTrue(uc.getPerms().userHasBbTypeDeletePermission(bbt3.getTypeOfBuildingBlock()));

    assertTrue(uc.getPerms().userHasBbTypeUpdatePermission(bbt4.getTypeOfBuildingBlock()));
    assertTrue(uc.getPerms().userHasBbTypeCreatePermission(bbt4.getTypeOfBuildingBlock()));
    assertTrue(uc.getPerms().userHasBbTypeDeletePermission(bbt4.getTypeOfBuildingBlock()));

    assertTrue(uc.getPerms().userHasBbTypeUpdatePermission(bbt5.getTypeOfBuildingBlock()));
    assertTrue(uc.getPerms().userHasBbTypeCreatePermission(bbt5.getTypeOfBuildingBlock()));
    assertTrue(uc.getPerms().userHasBbTypeDeletePermission(bbt5.getTypeOfBuildingBlock()));

    assertTrue(uc.getPerms().userHasAttrTypeGroupPermission(atg1, AttributeTypeGroupPermissionEnum.READ));
    assertFalse(uc.getPerms().userHasAttrTypeGroupPermission(atg1, AttributeTypeGroupPermissionEnum.READ_WRITE));
    assertTrue(uc.getPerms().userHasAttrTypeGroupPermission(atg2, AttributeTypeGroupPermissionEnum.READ));
    assertTrue(uc.getPerms().userHasAttrTypeGroupPermission(atg2, AttributeTypeGroupPermissionEnum.READ_WRITE));
    assertTrue(uc.getPerms().userHasAttrTypeGroupPermission(atg3, AttributeTypeGroupPermissionEnum.READ));
    assertTrue(uc.getPerms().userHasAttrTypeGroupPermission(atg3, AttributeTypeGroupPermissionEnum.READ_WRITE));
  }

  @Test
  public void testSpecialAttributeTypeGroupCase() {

    // special case: when nobody has write permission for an attribute type group, but some have a
    // read permission: this does _not_ imply that everyone has write permissions for that group!
    PermissionAttrTypeGroup patg = new PermissionAttrTypeGroup();
    AttributeTypeGroup atg = new AttributeTypeGroup();
    Role role1 = testDataHelper.getEmptyRole();
    role1.setId(Integer.valueOf(100));
    patg.setRole(role1);
    patg.setReadPermission(Boolean.TRUE);
    patg.setWritePermission(Boolean.FALSE);
    atg.setPermissionsRole(new HashSet<PermissionAttrTypeGroup>());
    atg.getPermissionsRole().add(patg);
    Set<Role> roles = new HashSet<Role>();
    roles.add(role1);
    User user = testDataHelper.getEmptyUser();
    UserContext uc = new UserContext(user.getLoginName(), roles, Locale.GERMAN, user);
    assertTrue(uc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ));
    assertFalse(uc.getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ_WRITE));
  }

  @Test
  public void testSerializing() throws IOException, ClassNotFoundException {
    ObjectInputStream is = null;
    ObjectOutputStream oos = null;
    try {
      try {
        UserContext userContext = testDataHelper.createUserContext();
        oos = new ObjectOutputStream(new FileOutputStream(TMP_FILE));
        oos.writeObject(userContext);
      } finally {
        if (oos != null) {
          oos.close();
        }
      }
      try {
        is = new ObjectInputStream(new FileInputStream(TMP_FILE));
        assertTrue(is.readObject() instanceof UserContext);
      } finally {
        if (is != null) {
          is.close();
        }
      }
    } finally {
      File tmpFile = new File(TMP_FILE);
      if (tmpFile.exists()) {
        tmpFile.delete();
      }
    }
  }

  /**
   * help method for testRoleInheritance()
   * @param tfp
   * @return PermissionFunctional
   */
  private PermissionFunctional createPermissionFunctional(TypeOfFunctionalPermission tfp) {
    PermissionFunctional pf = new PermissionFunctional();
    pf.setTypeOfFunctionalPermission(tfp);

    return pf;
  }

  /**
   * help method for testRoleInheritance()
   * @param tbb
   * @return BuildingBlock
   */
  private BuildingBlockType createBuildingBlockType(TypeOfBuildingBlock tbb) {
    BuildingBlockType bbt = new BuildingBlockType();
    bbt.setTypeOfBuildingBlock(tbb);
    return bbt;
  }

  /**
   * help method for testRoleInheritance()
   * @param roleId
   * @param roleName
   * @param bbt
   * @param pf
   * @return Role
   */
  private Role createRole(Integer roleId, String roleName, BuildingBlockType bbt, PermissionFunctional pf) {
    Role role = testDataHelper.getEmptyRole();
    role.setId(roleId);
    role.setRoleName(roleName);
    new Role2BbtPermission(role, bbt, EditPermissionType.UPDATE).connect();
    new Role2BbtPermission(role, bbt, EditPermissionType.CREATE).connect();
    new Role2BbtPermission(role, bbt, EditPermissionType.DELETE).connect();
    role.addPermissionFunctionalTwoWay(pf);
    return role;
  }

  /**
   * help method for testRoleInheritance()
   * @param readPermission
   * @param writePermission
   * @param role
   * @return PermissionAttrTypeGroup
   */
  private PermissionAttrTypeGroup createPermissionAttrTypeGroup(Boolean readPermission, Boolean writePermission, Role role) {
    PermissionAttrTypeGroup patg = new PermissionAttrTypeGroup();
    patg.setReadPermission(readPermission);
    patg.setWritePermission(writePermission);
    patg.setRole(role);
    return patg;
  }

  /**
   * help method for testRoleInheritance()
   * @param patgs
   * @return AttributeTypeGroup
   */
  private AttributeTypeGroup createAttributeTypeGroup(PermissionAttrTypeGroup... patgs) {
    AttributeTypeGroup atg = new AttributeTypeGroup();
    atg.setPermissionsRole(new HashSet<PermissionAttrTypeGroup>());
    for (PermissionAttrTypeGroup patg : patgs) {
      atg.getPermissionsRole().add(patg);
    }
    return atg;
  }
}

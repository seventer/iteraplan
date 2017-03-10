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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.dto.PermissionAttrTypeGroupDTO;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.Role2BbtPermission.EditPermissionType;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * Integration test for the {@link RoleServiceImpl} class.
 *
 */
public class RoleServiceImplTest extends BaseTransactionalTestSupport {

  @Autowired
  private RoleService          roleService;
  @Autowired
  private BuildingBlockTypeDAO buildingBlockTypeDAO;
  @Autowired
  private TestDataHelper2      testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#saveOrUpdate(de.iteratec.iteraplan.model.user.Role)}.
   */
  @Test
  public void testAddInvisibleWriteableBbTypes() {
    Role role = createSampleRole();

    BuildingBlockType isBbt = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEM);
    BuildingBlockType isrBbt = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    BuildingBlockType tcBbt = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.TECHNICALCOMPONENT);
    BuildingBlockType tcrBbt = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);
    BuildingBlockType isiBbt = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE);
    BuildingBlockType transportBbt = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.TRANSPORT);

    assertThat(role.getBbtForPermissionType(EditPermissionType.UPDATE), JUnitMatchers.hasItems(isBbt, isrBbt, tcBbt, tcrBbt, isiBbt, transportBbt));
    assertThat(role.getBbtForPermissionType(EditPermissionType.CREATE), JUnitMatchers.hasItems(isBbt, isrBbt, tcBbt, tcrBbt, isiBbt, transportBbt));
    assertThat(role.getBbtForPermissionType(EditPermissionType.DELETE), JUnitMatchers.hasItems(isBbt, isrBbt, tcBbt, tcrBbt, isiBbt, transportBbt));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#saveOrUpdate(Role)}.
   */
  @Test
  public void testRemoveInvisibleWriteableBbTypes() {
    List<TypeOfBuildingBlock> typeOfBuildingBlock = Lists.newArrayList(TypeOfBuildingBlock.INFORMATIONSYSTEM, TypeOfBuildingBlock.TECHNICALCOMPONENT,
        TypeOfBuildingBlock.TRANSPORT);
    List<TypeOfFunctionalPermission> typeOfFunctionalPermission = Lists.newArrayList(TypeOfFunctionalPermission.ALL);
    Role role = testDataHelper.createRole("role", typeOfBuildingBlock, typeOfFunctionalPermission);

    roleService.saveOrUpdate(role);

    assertTrue(role.getBbtForPermissionType(EditPermissionType.UPDATE).isEmpty());
    assertTrue(role.getBbtForPermissionType(EditPermissionType.CREATE).isEmpty());
    assertTrue(role.getBbtForPermissionType(EditPermissionType.DELETE).isEmpty());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#deleteEntity(Role)}.
   */
  @Test
  public void testDeleteRole() {
    Role role = createSampleRole();

    roleService.deleteEntity(role);
    assertNull(roleService.loadObjectByIdIfExists(role.getId()));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#deleteEntity(Role)}.
   */
  @Test
  public void testDeleteRoleNullId() {
    try {
      roleService.deleteEntity(new Role());
      fail("expected exception: " + IllegalArgumentException.class.getSimpleName());
    } catch (IllegalArgumentException e) {
      // OK
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#deleteEntity(Role)}.
   */
  @Test
  public void testDeleteRoleSupervisor() {
    try {
      roleService.deleteEntity(roleService.getSupervisorRole());
      fail("expected exception: " + IteraplanBusinessException.class.getSimpleName());
    } catch (IteraplanBusinessException e) {
      // OK
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#saveOrUpdate(Role)}.
   */
  private Role createSampleRole() {
    PermissionAttrTypeGroup patg = new PermissionAttrTypeGroup();
    AttributeTypeGroup attrTypeGroup = testDataHelper.createAttributeTypeGroup("ATG", "");
    patg.setAttrTypeGroup(attrTypeGroup);
    patg.setReadPermission(Boolean.TRUE);
    patg.setWritePermission(Boolean.TRUE);

    commit();

    beginTransaction();

    List<TypeOfBuildingBlock> typeOfBuildingBlock = Lists.newArrayList(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE,
        TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE);
    List<TypeOfFunctionalPermission> typeOfFunctionalPermission = Lists.newArrayList(TypeOfFunctionalPermission.ALL);
    Role role = testDataHelper.createRole("role", typeOfBuildingBlock, typeOfFunctionalPermission);

    role.addPermissionsAttrTypeGroupTwoWay(patg);

    roleService.saveOrUpdate(role);
    return role;
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#getAvailableBuildingBlockTypes(List)}.
   */
  @Test
  public void testGetAvailableBuildingBlockTypes() {
    BuildingBlockType bbt1 = testDataHelper.getBuildingBlockType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN);
    BuildingBlockType bbt2 = testDataHelper.getBuildingBlockType(TypeOfBuildingBlock.BUSINESSMAPPING);
    BuildingBlockType bbt3 = testDataHelper.getBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    List<BuildingBlockType> availableBuildingBlockTypes = roleService.getAvailableBuildingBlockTypes(Lists.newArrayList(bbt1, bbt2, bbt3));
    assertEquals(TypeOfBuildingBlock.DISPLAY.size() - 3, availableBuildingBlockTypes.size());
    assertFalse(availableBuildingBlockTypes.contains(bbt1));
    assertFalse(availableBuildingBlockTypes.contains(bbt2));
    assertFalse(availableBuildingBlockTypes.contains(bbt3));
    assertTrue(availableBuildingBlockTypes.contains(testDataHelper.getBuildingBlockType(TypeOfBuildingBlock.PRODUCT)));
    assertTrue(availableBuildingBlockTypes.contains(testDataHelper.getBuildingBlockType(TypeOfBuildingBlock.PROJECT)));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#getAvailableBuildingBlockTypes(List)}.
   */
  @Test
  public void testGetAvailableBuildingBlockTypesNone() {
    List<BuildingBlockType> availableBuildingBlockTypes = roleService.getAvailableBuildingBlockTypes(Lists.<BuildingBlockType> newArrayList());
    assertEquals(TypeOfBuildingBlock.DISPLAY.size(), availableBuildingBlockTypes.size());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#getAvailableBuildingBlockTypes(List)}.
   */
  @Test
  public void testGetAvailableBuildingBlockTypesAll() {
    List<BuildingBlockType> availableBuildingBlockTypes = roleService.getAvailableBuildingBlockTypes(testDataHelper
        .getAllBuildingBlockTypes(TypeOfBuildingBlock.DISPLAY));
    assertTrue(availableBuildingBlockTypes.isEmpty());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#getAvailableFunctionalPermissions(List)}.
   */
  @Test
  public void testGetAvailableFunctionalPermissionsNone() {
    List<PermissionFunctional> availablePermissionFunctional = roleService.getAvailableFunctionalPermissions(Lists
        .<PermissionFunctional> newArrayList());
    assertEquals(TypeOfFunctionalPermission.ALL.size(), availablePermissionFunctional.size());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#getAvailableRoles(Integer, List)}.
   */
  @Test
  public void testGetAvailableRoles() {
    Role role1 = testDataHelper.createRole("role1");
    Role role2 = testDataHelper.createRole("role2", role1);
    Role role3 = testDataHelper.createRole("role3");

    List<Role> roles = roleService.getAvailableRoles(role1.getId(), Lists.<Role> newArrayList());
    assertFalse(roles.contains(role1));
    assertFalse(roles.contains(role2));
    assertTrue(roles.contains(role3));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#getAvailableRoles(Integer, List)}.
   */
  @Test
  public void testGetAvailableRolesExclude() {
    Role role1 = testDataHelper.createRole("role1");
    Role role2 = testDataHelper.createRole("role2", role1);
    Role role3 = testDataHelper.createRole("role3");
    Role role4 = testDataHelper.createRole("role4");

    List<Role> roles = roleService.getAvailableRoles(role1.getId(), Lists.<Role> newArrayList(role4));
    assertFalse(roles.contains(role1));
    assertFalse(roles.contains(role2));
    assertTrue(roles.contains(role3));
    assertFalse(roles.contains(role4));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#getPermissionsForAttributeTypeGroups(List)}.
   */
  @Test
  public void testGetPermissionsForAttributeTypeGroups() {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("group", "description");
    testDataHelper.createAttrTypeGroupPermission(null, atg, Boolean.TRUE, Boolean.FALSE);

    List<PermissionAttrTypeGroupDTO> list = Lists.newArrayList();
    List<PermissionAttrTypeGroupDTO> res = roleService.getPermissionsForAttributeTypeGroups(list);
    assertEquals(2, res.size());
    assertEquals("[Default Attribute Group]", res.get(0).getPermission().getAttributeTypeGroupName());
    assertEquals("group", res.get(1).getPermission().getAttributeTypeGroupName());
    assertEquals(atg.getId(), res.get(1).getId());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#getPermissionsForAttributeTypeGroups(List)}.
   */
  @Test
  public void testGetPermissionsForAttributeTypeGroupsExclude() {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("group", "description");
    PermissionAttrTypeGroup perm = testDataHelper.createAttrTypeGroupPermission(null, atg, Boolean.TRUE, Boolean.FALSE);
    PermissionAttrTypeGroupDTO permDTO = new PermissionAttrTypeGroupDTO(atg.getId(), perm);

    List<PermissionAttrTypeGroupDTO> list = Lists.newArrayList(permDTO);
    List<PermissionAttrTypeGroupDTO> res = roleService.getPermissionsForAttributeTypeGroups(list);
    assertEquals(1, res.size());
    assertEquals("[Default Attribute Group]", res.get(0).getPermission().getAttributeTypeGroupName());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#getRoleById(Integer)}.
   */
  @Test
  public void testGetRoleById() {
    Role role = testDataHelper.createRole("role");
    assertEquals(role, roleService.getRoleById(role.getId()));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#getRoleByName(String)}.
   */
  @Test
  public void testGetRoleByName() {
    Role role = testDataHelper.createRole("role");
    assertEquals(role, roleService.getRoleByName("role"));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#getAllRolesFiltered()}.
   */
  @Test
  public void testGetAllRolesFiltered() {
    Role role1 = testDataHelper.createRole("role1");
    Role role2 = testDataHelper.createRole("role2");
    Role role3 = testDataHelper.createRole("role3");

    List<Role> res = roleService.getAllRolesFiltered();
    assertTrue(res.contains(role1));
    assertTrue(res.contains(role2));
    assertTrue(res.contains(role3));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#getRolesBySearch(Role)}.
   */
  @Test
  public void testGetRolesBySearch() {
    Role role1 = testDataHelper.createRole("role1");
    List<Role> res = roleService.getRolesBySearch(role1);
    assertEquals(Lists.newArrayList(role1), res);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.RoleServiceImpl#reloadFunctionalPermissions(java.util.Collection)}.
   */
  @Test
  public void testReloadFunctionalPermissions() {
    Role role = createSampleRole();
    commit();
    beginTransaction();

    List<PermissionFunctional> functionalPermissions = roleService.reloadFunctionalPermissions(role.getPermissionsFunctional());

    assertNotNull(functionalPermissions);
    assertEquals(role.getPermissionsFunctional().size(), functionalPermissions.size());
    assertThat(functionalPermissions, JUnitMatchers.hasItems(Iterables.toArray(role.getPermissionsFunctional(), PermissionFunctional.class)));
  }
}

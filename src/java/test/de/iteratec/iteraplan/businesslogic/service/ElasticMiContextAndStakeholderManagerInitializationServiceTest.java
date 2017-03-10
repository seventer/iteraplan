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
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Future;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticmi.M3CRepository;
import de.iteratec.iteraplan.elasticmi.m3c.FutureM3C;
import de.iteratec.iteraplan.elasticmi.m3c.SimpleM3C;
import de.iteratec.iteraplan.elasticmi.m3c.SuccessorM3C;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiAccessLevel;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiFeatureGroupPermission;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiPermission;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiTypePermission;
import de.iteratec.iteraplan.elasticmi.permission.StakeholderManager;
import de.iteratec.iteraplan.elasticmi.permission.impl.StakeholderManagerImpl;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.Role2BbtPermission;
import de.iteratec.iteraplan.model.user.Role2BbtPermission.EditPermissionType;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.User;


public class ElasticMiContextAndStakeholderManagerInitializationServiceTest {

  private ElasticMiContextAndStakeholderManagerInitializationServiceImpl ctxAndStakeholderMgrInitService;

  private AttributeTypeGroupService                                      atgService;
  private ElasticMiService                                               elasticMiService;
  private RoleService                                                    roleService;
  private UserService                                                    userService;
  private M3CRepository                                                  m3cRepository;

  private StakeholderManager                                             stakeholderManager;

  @Before
  public void onSetUp() {
    elasticMiService = EasyMock.createNiceMock(ElasticMiService.class);
    roleService = EasyMock.createMock(RoleService.class);
    userService = EasyMock.createMock(UserService.class);
    atgService = EasyMock.createNiceMock(AttributeTypeGroupService.class);
    stakeholderManager = new StakeholderManagerImpl();
    m3cRepository = EasyMock.createNiceMock(M3CRepository.class);

    this.ctxAndStakeholderMgrInitService = new ElasticMiContextAndStakeholderManagerInitializationServiceImpl(atgService, userService, roleService,
        m3cRepository, stakeholderManager);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testInitializationService() {
    AttributeTypeGroup atg1 = new AttributeTypeGroup();
    atg1.setName("atg1");
    AttributeTypeGroup atg2 = new AttributeTypeGroup();
    atg2.setName("atg2");
    AttributeTypeGroup atg3 = new AttributeTypeGroup();
    atg3.setName("atg3");

    Role role1 = new Role();
    role1.setRoleName("role1");
    role1.addPermissionFunctionalTwoWay(new PermissionFunctional(TypeOfFunctionalPermission.INFORMATIONSYSTEMRELEASE));
    role1.addPermissionFunctionalTwoWay(new PermissionFunctional(TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES));

    Role2BbtPermission updateTCR = new Role2BbtPermission(role1, new BuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE),
        EditPermissionType.UPDATE);
    Role2BbtPermission createTCR = new Role2BbtPermission(role1, new BuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE),
        EditPermissionType.CREATE);
    Role2BbtPermission deleteTCR = new Role2BbtPermission(role1, new BuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE),
        EditPermissionType.DELETE);
    role1.setPermissionsBbt(Sets.newHashSet(updateTCR, createTCR, deleteTCR));

    PermissionAttrTypeGroup permAtg1 = new PermissionAttrTypeGroup(Integer.valueOf(0), atg1);
    permAtg1.setPermissionKey(PermissionAttrTypeGroup.READ_KEY);
    role1.setPermissionsAttrTypeGroup(Sets.newHashSet(permAtg1));

    Role role2 = new Role();
    role2.setRoleName("role2");
    role2.addPermissionFunctionalTwoWay(new PermissionFunctional(TypeOfFunctionalPermission.BUSINESSOBJECT));
    role2.addPermissionFunctionalTwoWay(new PermissionFunctional(TypeOfFunctionalPermission.INFORMATIONSYSTEMRELEASE));

    Role2BbtPermission updateISR = new Role2BbtPermission(role2, new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE),
        EditPermissionType.UPDATE);
    role2.setPermissionsBbt(Sets.newHashSet(updateISR));

    PermissionAttrTypeGroup permAtg2 = new PermissionAttrTypeGroup(Integer.valueOf(1), atg2);
    permAtg2.setPermissionKey(PermissionAttrTypeGroup.READ_WRITE_KEY);
    role2.setPermissionsAttrTypeGroup(Sets.newHashSet(permAtg2));

    Role supervisorRole = new Role();
    supervisorRole.setRoleName(Role.SUPERVISOR_ROLE_NAME);

    User user = new User();
    user.setFirstName("userFirst");
    user.setLastName("userLast");
    user.setLoginName("user");
    user.setEmail("user@email.com");

    user.addRoleTwoWay(role1);
    user.addRoleTwoWay(role2);

    EasyMock.expect(atgService.getAllAttributeTypeGroups()).andReturn(Lists.newArrayList(atg1, atg2, atg3)).times(2);
    EasyMock.replay(atgService);
    EasyMock.expect(roleService.loadElementList()).andReturn(Lists.newArrayList(role1, role2, supervisorRole));
    EasyMock.replay(roleService);
    EasyMock.expect(userService.loadElementList()).andReturn(Lists.newArrayList(user));
    EasyMock.replay(userService);
    EasyMock.replay(elasticMiService);

    Future<SimpleM3C> futureMock = EasyMock.createNiceMock(Future.class);
    EasyMock.expect(m3cRepository.getMetamodelAndModelContainer("MASTER")).andReturn(new SuccessorM3C("MASTER", new FutureM3C("MASTER", futureMock)));
    EasyMock.replay(m3cRepository);

    ctxAndStakeholderMgrInitService.initializeMiContextAndStakeholderManager("user", "MASTER");

    testResults();
  }

  private void testResults() {
    assertEquals(3, stakeholderManager.getRoles().size());
    assertEquals(0, stakeholderManager.getFunctionalPermissions().size());

    //test role1
    de.iteratec.iteraplan.elasticmi.permission.Role elasticRole1 = stakeholderManager.findRoleByPersistentName("role1");
    assertNotNull(elasticRole1);

    assertTrue(elasticRole1.getTypePermissions().contains(new ElasticMiTypePermission("InformationSystem", ElasticMiAccessLevel.READ)));

    assertTrue(elasticRole1.getTypePermissions().contains(new ElasticMiTypePermission("TechnicalComponent", ElasticMiAccessLevel.DELETE)));

    assertTrue(elasticRole1.getFeatureGroupPermissions().contains(new ElasticMiFeatureGroupPermission("atg1", ElasticMiAccessLevel.READ)));
    assertFalse(elasticRole1.getFeatureGroupPermissions().contains(new ElasticMiFeatureGroupPermission("atg1", ElasticMiAccessLevel.UPDATE)));
    assertFalse(elasticRole1.getFeatureGroupPermissions().contains(new ElasticMiFeatureGroupPermission("atg2", ElasticMiAccessLevel.READ)));
    assertFalse(elasticRole1.getFeatureGroupPermissions().contains(new ElasticMiFeatureGroupPermission("atg2", ElasticMiAccessLevel.UPDATE)));
    assertFalse(elasticRole1.getFeatureGroupPermissions().contains(new ElasticMiFeatureGroupPermission("atg3", ElasticMiAccessLevel.READ)));
    assertTrue(elasticRole1.getFeatureGroupPermissions().contains(new ElasticMiFeatureGroupPermission("atg3", ElasticMiAccessLevel.UPDATE)));

    //test role2
    de.iteratec.iteraplan.elasticmi.permission.Role elasticRole2 = stakeholderManager.findRoleByPersistentName("role2");
    assertNotNull(elasticRole2);

    assertTrue(elasticRole2.getTypePermissions().contains(new ElasticMiTypePermission("BusinessObject", ElasticMiAccessLevel.READ)));

    assertFalse(elasticRole2.getTypePermissions().contains(new ElasticMiTypePermission("InformationSystem", ElasticMiAccessLevel.READ)));
    assertFalse(elasticRole2.getTypePermissions().contains(new ElasticMiTypePermission("InformationSystem", ElasticMiAccessLevel.CREATE)));
    assertTrue(elasticRole2.getTypePermissions().contains(new ElasticMiTypePermission("InformationSystem", ElasticMiAccessLevel.UPDATE)));
    assertFalse(elasticRole2.getTypePermissions().contains(new ElasticMiTypePermission("InformationSystem", ElasticMiAccessLevel.DELETE)));

    assertFalse(elasticRole2.getFeatureGroupPermissions().contains(new ElasticMiFeatureGroupPermission("atg1", ElasticMiAccessLevel.READ)));
    assertFalse(elasticRole2.getFeatureGroupPermissions().contains(new ElasticMiFeatureGroupPermission("atg1", ElasticMiAccessLevel.UPDATE)));
    assertFalse(elasticRole2.getFeatureGroupPermissions().contains(new ElasticMiFeatureGroupPermission("atg2", ElasticMiAccessLevel.READ)));
    assertTrue(elasticRole2.getFeatureGroupPermissions().contains(new ElasticMiFeatureGroupPermission("atg2", ElasticMiAccessLevel.UPDATE)));
    assertFalse(elasticRole2.getFeatureGroupPermissions().contains(new ElasticMiFeatureGroupPermission("atg3", ElasticMiAccessLevel.READ)));
    assertTrue(elasticRole2.getFeatureGroupPermissions().contains(new ElasticMiFeatureGroupPermission("atg3", ElasticMiAccessLevel.UPDATE)));

    assertTrue(elasticRole2.getTypePermissions().contains(new ElasticMiTypePermission("Isr2BoAssociation", ElasticMiAccessLevel.DELETE)));

    //test supervisor role
    de.iteratec.iteraplan.elasticmi.permission.Role elasticSupervisorRole = stakeholderManager.findRoleByPersistentName(Role.SUPERVISOR_ROLE_NAME);
    assertNotNull(elasticSupervisorRole);
    for (ElasticMiPermission perm : stakeholderManager.getTypePermissions()) {
      assertTrue(elasticSupervisorRole.getTypePermissions().contains(perm));
    }
    for (ElasticMiPermission perm : stakeholderManager.getFeatureGroupPermissions()) {
      assertTrue(elasticSupervisorRole.getFeatureGroupPermissions().contains(perm));
    }

    assertEquals(1, stakeholderManager.getUsers().size());

    de.iteratec.iteraplan.elasticmi.permission.User elasticUser = stakeholderManager.findUserByPersistentName("user");
    assertNotNull(elasticUser);

    assertEquals(2, elasticUser.getRoles().size());
    assertTrue(elasticUser.getRoles().contains(elasticRole1));
    assertTrue(elasticUser.getRoles().contains(elasticRole2));

    for (ElasticMiTypePermission perm : elasticRole1.getTypePermissions()) {
      assertTrue(elasticUser.getTypePermissions().contains(perm));
    }
    for (ElasticMiTypePermission perm : elasticRole2.getTypePermissions()) {
      assertTrue(elasticUser.getTypePermissions().contains(perm));
    }
    for (ElasticMiFeatureGroupPermission perm : elasticRole1.getFeatureGroupPermissions()) {
      assertTrue(elasticUser.getFeatureGroupPermissions().contains(perm));
    }
    for (ElasticMiFeatureGroupPermission perm : elasticRole2.getFeatureGroupPermissions()) {
      assertTrue(elasticUser.getFeatureGroupPermissions().contains(perm));
    }
  }
}

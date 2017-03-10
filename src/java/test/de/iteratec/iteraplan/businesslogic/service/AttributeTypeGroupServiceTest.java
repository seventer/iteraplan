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

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.TestAsSuperUser;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.dto.PermissionAttrTypeGroupDTO;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.PermissionAttrTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.RoleDAO;


/**
 * Class for testing the service methods of the {@link AttributeTypeGroupService} interface.
 * 
 * @author sip
 */
public class AttributeTypeGroupServiceTest extends TestAsSuperUser {

  private AttributeTypeGroupServiceImpl attributeTypeGroupService;
  private AttributeTypeGroupDAO         attributeTypeGroupDAOMock;
  private PermissionAttrTypeGroupDAO    permissionAttrTypeGroupDAOMock;
  private RoleDAO                       roleDAOMock;
  private AttributeTypeGroup            atg;
  private AttributeTypeGroup            atg2;
  private Role                          role;
  private AttributeType                 at;
  private PermissionAttrTypeGroup       pr;

  @Before
  public void setUp2() {
    this.attributeTypeGroupService = new AttributeTypeGroupServiceImpl();

    attributeTypeGroupDAOMock = createNiceMock(AttributeTypeGroupDAO.class);
    attributeTypeGroupService.setAttributeTypeGroupDAO(attributeTypeGroupDAOMock);

    roleDAOMock = createNiceMock(RoleDAO.class);
    attributeTypeGroupService.setRoleDAO(roleDAOMock);

    permissionAttrTypeGroupDAOMock = createNiceMock(PermissionAttrTypeGroupDAO.class);
    attributeTypeGroupService.setPermissionAttrTypeGroupDAO(permissionAttrTypeGroupDAOMock);

    atg = new AttributeTypeGroup();
    atg.setId(Integer.valueOf(97));
    atg.setName(AttributeTypeGroup.STANDARD_ATG_NAME);
    atg.setPosition(Integer.valueOf(4));

    atg2 = new AttributeTypeGroup();
    atg2.setId(Integer.valueOf(443));
    atg2.setName("Other ATG");
    atg2.setPosition(Integer.valueOf(2));

    at = new TextAT();
    at.setId(Integer.valueOf(9988));
    at.setName("AT1");
    atg2.addAttributeType(at, 12);

    role = new Role();
    role.setId(Integer.valueOf(231));
    role.setRoleName("testRole1");

    pr = new PermissionAttrTypeGroup();
    pr.setId(Integer.valueOf(8332));
    pr.setRole(role);
    pr.setAttrTypeGroup(atg2);
    role.addPermissionsAttrTypeGroupTwoWay(pr);
    atg2.addPermissionTwoWay(pr);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService#getAllAttributeTypeGroups()}.
   */
  @Test
  public void testGetAllAttributeTypeGroups() {
    ArrayList<AttributeTypeGroup> list = new ArrayList<AttributeTypeGroup>(2);
    list.add(atg);
    list.add(new AttributeTypeGroup());
    expect(attributeTypeGroupDAOMock.getAllAttributeTypeGroups()).andReturn(list);
    replay(attributeTypeGroupDAOMock);

    List<AttributeTypeGroup> res = attributeTypeGroupService.getAllAttributeTypeGroups();
    assertEquals(list, res);
    verify(attributeTypeGroupDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService#getMaxATGPositionNumber()}.
   */
  @Test
  public void testGetMaxATGPositionNumber() {
    Integer i = Integer.valueOf(543);
    expect(attributeTypeGroupDAOMock.getMaxATGPositionNumber()).andReturn(i);
    replay(attributeTypeGroupDAOMock);

    assertEquals(i, attributeTypeGroupService.getMaxATGPositionNumber());
    verify(attributeTypeGroupDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService#getPermissionsForRoles(java.lang.Integer, java.util.List)}.
   */
  @Test
  public void testGetPermissionsForRoles() {
    expect(attributeTypeGroupDAOMock.loadObjectById(atg.getId())).andReturn(atg);
    List<Role> roles = new ArrayList<Role>(1);
    roles.add(role);
    expect(roleDAOMock.loadFilteredElementList(null, new HashSet<Integer>())).andReturn(roles);
    replay(attributeTypeGroupDAOMock, roleDAOMock);

    List<PermissionAttrTypeGroupDTO> res = attributeTypeGroupService.getPermissionsForRoles(atg.getId(), new ArrayList<PermissionAttrTypeGroupDTO>());
    assertEquals(1, res.size());
    assertEquals(role.getId(), res.get(0).getId());
    assertEquals(new PermissionAttrTypeGroup(null, atg, role), res.get(0).getPermission());
    verify(attributeTypeGroupDAOMock, roleDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService#getPermissionsForRoles(java.lang.Integer, java.util.List)}.
   */
  @Test
  public void testGetPermissionsForRolesExclude() {
    expect(attributeTypeGroupDAOMock.loadObjectById(atg.getId())).andReturn(atg);
    Set<Integer> set = new HashSet<Integer>();
    set.add(role.getId());
    expect(roleDAOMock.loadFilteredElementList(null, set)).andReturn(new ArrayList<Role>());
    replay(attributeTypeGroupDAOMock, roleDAOMock);

    List<PermissionAttrTypeGroupDTO> list = new ArrayList<PermissionAttrTypeGroupDTO>();
    list.add(new PermissionAttrTypeGroupDTO(role.getId(), new PermissionAttrTypeGroup(null, atg, role)));
    List<PermissionAttrTypeGroupDTO> res = attributeTypeGroupService.getPermissionsForRoles(atg.getId(), list);
    assertEquals(0, res.size());
    verify(attributeTypeGroupDAOMock, roleDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService#getStandardAttributeTypeGroup()}.
   */
  @Test
  public void testGetStandardAttributeTypeGroup() {
    expect(attributeTypeGroupDAOMock.getStandardAttributeTypeGroup()).andReturn(atg);
    replay(attributeTypeGroupDAOMock);

    assertEquals(atg, attributeTypeGroupService.getStandardAttributeTypeGroup());
    verify(attributeTypeGroupDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService#updatePosition(de.iteratec.iteraplan.model.attribute.AttributeTypeGroup, java.lang.Integer)}.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testUpdatePositionNull() {
    attributeTypeGroupService.updatePosition(atg, null);
  }

  @Test
  public void testUpdatePositionMin() {
    expect(attributeTypeGroupDAOMock.getMinATGPositionNumber()).andReturn(Integer.valueOf(-3));
    expect(attributeTypeGroupDAOMock.getMaxATGPositionNumber()).andReturn(Integer.valueOf(5));
    expect(attributeTypeGroupDAOMock.saveOrUpdate(atg)).andReturn(atg);
    replay(attributeTypeGroupDAOMock);

    attributeTypeGroupService.updatePosition(atg, Integer.valueOf(-3));
    assertEquals(Integer.valueOf(-4), atg.getPosition());
    verify(attributeTypeGroupDAOMock);
  }

  @Test
  public void testUpdatePositionMax() {
    expect(attributeTypeGroupDAOMock.getMinATGPositionNumber()).andReturn(Integer.valueOf(-3));
    expect(attributeTypeGroupDAOMock.getMaxATGPositionNumber()).andReturn(Integer.valueOf(5));
    expect(attributeTypeGroupDAOMock.saveOrUpdate(atg)).andReturn(atg);
    replay(attributeTypeGroupDAOMock);

    attributeTypeGroupService.updatePosition(atg, Integer.valueOf(5));
    assertEquals(Integer.valueOf(6), atg.getPosition());
    verify(attributeTypeGroupDAOMock);
  }

  @Test
  public void testUpdatePositionNotTaken() {
    expect(attributeTypeGroupDAOMock.getMinATGPositionNumber()).andReturn(Integer.valueOf(-3));
    expect(attributeTypeGroupDAOMock.getMaxATGPositionNumber()).andReturn(Integer.valueOf(5));
    expect(attributeTypeGroupDAOMock.saveOrUpdate(atg)).andReturn(atg);
    replay(attributeTypeGroupDAOMock);

    attributeTypeGroupService.updatePosition(atg, Integer.valueOf(0));
    assertEquals(Integer.valueOf(0), atg.getPosition());
    verify(attributeTypeGroupDAOMock);
  }

  @Test
  public void testUpdatePositionTaken() {
    expect(attributeTypeGroupDAOMock.getMinATGPositionNumber()).andReturn(Integer.valueOf(-3));
    expect(attributeTypeGroupDAOMock.getMaxATGPositionNumber()).andReturn(Integer.valueOf(5));
    expect(attributeTypeGroupDAOMock.getAttributeTypeGroupByPosition(Integer.valueOf(2))).andReturn(atg2);
    expect(attributeTypeGroupDAOMock.saveOrUpdate(atg)).andReturn(atg);
    replay(attributeTypeGroupDAOMock);

    attributeTypeGroupService.updatePosition(atg, Integer.valueOf(2));
    assertEquals(Integer.valueOf(2), atg.getPosition());
    assertEquals(Integer.valueOf(4), atg2.getPosition());
    verify(attributeTypeGroupDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService#deleteEntity(de.iteratec.iteraplan.model.attribute.AttributeTypeGroup)}.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testDeleteEntityStandardATGName() {
    attributeTypeGroupService.deleteEntity(atg);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService#deleteEntity(de.iteratec.iteraplan.model.attribute.AttributeTypeGroup)}.
   */
  @Test
  public void testDeleteEntity() {
    attributeTypeGroupDAOMock.delete(atg2);
    permissionAttrTypeGroupDAOMock.delete(pr);
    expect(attributeTypeGroupDAOMock.getStandardAttributeTypeGroup()).andReturn(atg);
    replay(attributeTypeGroupDAOMock, permissionAttrTypeGroupDAOMock);

    attributeTypeGroupService.deleteEntity(atg2);
    assertEquals(0, atg2.getAttributeTypes().size());
    assertEquals(1, atg.getAttributeTypes().size());
    assertEquals(at, atg.getAttributeTypes().get(0));
    assertFalse(role.getPermissionsAttrTypeGroup().contains(pr));
    verify(attributeTypeGroupDAOMock, permissionAttrTypeGroupDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService#reloadPermissionAttrTypeGroups(java.util.Collection)}.
   */
  @Test
  public void testReloadPermissionAttrTypeGroupsNull() {
    assertEquals(new ArrayList<PermissionAttrTypeGroup>(), attributeTypeGroupService.reloadPermissionAttrTypeGroups(null));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService#reloadPermissionAttrTypeGroups(java.util.Collection)}.
   */
  @Test
  public void testReloadPermissionAttrTypeGroupsEmpty() {
    assertEquals(new ArrayList<PermissionAttrTypeGroup>(),
        attributeTypeGroupService.reloadPermissionAttrTypeGroups(new ArrayList<PermissionAttrTypeGroup>()));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService#reloadPermissionAttrTypeGroups(java.util.Collection)}.
   */
  @Test
  public void testReloadPermissionAttrTypeGroups() {
    List<PermissionAttrTypeGroup> list = new ArrayList<PermissionAttrTypeGroup>();
    Set<Integer> ids = new HashSet<Integer>();
    list.add(pr);
    ids.add(pr.getId());

    expect(permissionAttrTypeGroupDAOMock.loadElementListWithIds(ids)).andReturn(list);
    replay(permissionAttrTypeGroupDAOMock);

    assertEquals(list, attributeTypeGroupService.reloadPermissionAttrTypeGroups(list));
    verify(permissionAttrTypeGroupDAOMock);
  }

}

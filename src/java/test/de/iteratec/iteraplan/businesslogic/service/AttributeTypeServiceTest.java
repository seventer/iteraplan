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
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.AttributeValueAssignmentDAO;
import de.iteratec.iteraplan.persistence.dao.AttributeValueDAO;
import de.iteratec.iteraplan.persistence.dao.TimeseriesDAO;


/**
 * Class for testing the service methods of the {@link AttributeTypeService} interface.
 * 
 * @author sip
 */
public class AttributeTypeServiceTest {

  private AttributeTypeServiceImpl attributeTypeService;
  private AttributeTypeDAO         attributeTypeDAOMock;
  private AttributeValueDAO        attributeValueDAOMock;
  private TimeseriesDAO            timeseriesDAOMock;

  private AttributeTypeGroup       atg;
  private DateAT                   dateAT;
  private EnumAT                   enumAT;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    attributeTypeService = new AttributeTypeServiceImpl();

    attributeTypeDAOMock = createNiceMock(AttributeTypeDAO.class);
    attributeTypeService.setAttributeTypeDAO(attributeTypeDAOMock);

    attributeValueDAOMock = createNiceMock(AttributeValueDAO.class);
    attributeTypeService.setAttributeValueDAO(attributeValueDAOMock);

    timeseriesDAOMock = createNiceMock(TimeseriesDAO.class);
    attributeTypeService.setTimeseriesDAO(timeseriesDAOMock);

    AttributeValueAssignmentDAO attributeValueAssignmentDAOMock = createNiceMock(AttributeValueAssignmentDAO.class);
    attributeTypeService.setAttributeValueAssignmentDAO(attributeValueAssignmentDAOMock);

    DateIntervalService dateIntervalServiceMock = createNiceMock(DateIntervalService.class);
    attributeTypeService.setDateIntervalService(dateIntervalServiceMock);

    atg = new AttributeTypeGroup();
    atg.setId(Integer.valueOf(97));

    dateAT = new DateAT();
    dateAT.setName("testDateAT");
    dateAT.setId(Integer.valueOf(826));
    dateAT.setAttributeTypeGroup(atg);
    atg.addAttributeType(dateAT, 0);

    enumAT = new EnumAT();
    enumAT.setName("testEnumAT");
    enumAT.setId(Integer.valueOf(135));
    enumAT.setAttributeTypeGroup(atg);
    atg.addAttributeType(enumAT, 1);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#isNumberAT(java.lang.Integer)}.
   */
  @Test
  public void testIsNumberATTrue() {
    NumberAT numberAT = new NumberAT();
    numberAT.setId(Integer.valueOf(2002));
    numberAT.setName("Number AT");

    expect(attributeTypeDAOMock.loadObjectById(numberAT.getId())).andReturn(numberAT);
    replay(attributeTypeDAOMock);
    assertTrue(attributeTypeService.isNumberAT(numberAT.getId()));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#isNumberAT(java.lang.Integer)}.
   */
  @Test
  public void testIsNumberATFalse() {
    DateAT dateAT = new DateAT();
    dateAT.setId(Integer.valueOf(2004));
    dateAT.setName("Date AT");

    expect(attributeTypeDAOMock.loadObjectById(dateAT.getId())).andReturn(dateAT);
    replay(attributeTypeDAOMock);
    assertFalse(attributeTypeService.isNumberAT(dateAT.getId()));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#assureReadPermission(java.util.Set)}.
   */
  @Test
  public void testAssureReadPermissionAsSuperUser() {
    setSuperUserContext();
    expect(attributeTypeDAOMock.loadObjectById(dateAT.getId())).andReturn(dateAT);
    expect(attributeTypeDAOMock.loadObjectById(enumAT.getId())).andReturn(enumAT);
    replay(attributeTypeDAOMock);

    Set<Integer> set = new HashSet<Integer>();
    set.add(dateAT.getId());
    set.add(enumAT.getId());
    attributeTypeService.assureReadPermission(set);
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#assureReadPermission(java.util.Set)}.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAssureReadPermissionWithoutFunctionalPermissions() {
    setUserContextWithPermissions(null, null);
    expect(attributeTypeDAOMock.loadObjectById(dateAT.getId())).andReturn(dateAT);
    expect(attributeTypeDAOMock.loadObjectById(enumAT.getId())).andReturn(enumAT);
    replay(attributeTypeDAOMock);

    Set<Integer> set = new HashSet<Integer>();
    set.add(dateAT.getId());
    set.add(enumAT.getId());
    attributeTypeService.assureReadPermission(set);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#assureReadPermission(java.util.Set)}.
   */
  @Test
  public void testAssureReadPermissionWithFunctionalPermission() {
    setUserContextWithPermissions(
        Arrays.asList(new PermissionFunctional[] { new PermissionFunctional(TypeOfFunctionalPermission.ELEMENT_SPECIFIC_PERMISSION) }), null);
    expect(attributeTypeDAOMock.loadObjectById(dateAT.getId())).andReturn(dateAT);
    expect(attributeTypeDAOMock.loadObjectById(enumAT.getId())).andReturn(enumAT);
    replay(attributeTypeDAOMock);

    Set<Integer> set = new HashSet<Integer>();
    set.add(dateAT.getId());
    set.add(enumAT.getId());
    attributeTypeService.assureReadPermission(set);
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#assureReadPermission(java.util.Set)}.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAssureReadPermissionNotPermitted() {
    Role r = new Role();
    r.setId(Integer.valueOf(5423));
    PermissionAttrTypeGroup patg = new PermissionAttrTypeGroup(Integer.valueOf(22332), atg, r);
    patg.setReadPermission(Boolean.TRUE);
    atg.addPermissionTwoWay(patg);
    setUserContextWithPermissions(Lists.newArrayList(new PermissionFunctional(TypeOfFunctionalPermission.ELEMENT_SPECIFIC_PERMISSION)), null);
    expect(attributeTypeDAOMock.loadObjectById(dateAT.getId())).andReturn(dateAT);
    expect(attributeTypeDAOMock.loadObjectById(enumAT.getId())).andReturn(enumAT);
    replay(attributeTypeDAOMock);

    Set<Integer> set = new HashSet<Integer>();
    set.add(dateAT.getId());
    set.add(enumAT.getId());
    attributeTypeService.assureReadPermission(set);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#assureReadPermission(java.util.Set)}.
   */
  @Test
  public void testAssureReadPermissionPermitted() {
    Role r = new Role();
    r.setId(Integer.valueOf(5423));
    PermissionAttrTypeGroup patg = new PermissionAttrTypeGroup(Integer.valueOf(22332), atg, r);
    patg.setReadPermission(Boolean.TRUE);
    atg.addPermissionTwoWay(patg);
    setUserContextWithPermissions(Lists.newArrayList(new PermissionFunctional(TypeOfFunctionalPermission.ELEMENT_SPECIFIC_PERMISSION)),
        Lists.newArrayList(patg));
    expect(attributeTypeDAOMock.loadObjectById(dateAT.getId())).andReturn(dateAT);
    expect(attributeTypeDAOMock.loadObjectById(enumAT.getId())).andReturn(enumAT);
    replay(attributeTypeDAOMock);

    Set<Integer> set = new HashSet<Integer>();
    set.add(dateAT.getId());
    set.add(enumAT.getId());
    attributeTypeService.assureReadPermission(set);
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#checkForMultipleAssignments(de.iteratec.iteraplan.model.attribute.EnumAT)}.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testCheckForMultipleAssignmentsTrue() {
    expect(Boolean.valueOf(attributeValueDAOMock.checkForBuildingBlocksWithMoreThanOneEnumAVs(enumAT.getId()))).andReturn(Boolean.TRUE);
    replay(attributeValueDAOMock);
    attributeTypeService.checkForMultipleAssignments(enumAT);
    verify(attributeValueDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#checkForMultipleAssignments(de.iteratec.iteraplan.model.attribute.EnumAT)}.
   */
  @Test
  public void testCheckForMultipleAssignmentsFalse() {
    expect(Boolean.valueOf(attributeValueDAOMock.checkForBuildingBlocksWithMoreThanOneEnumAVs(enumAT.getId()))).andReturn(Boolean.FALSE);
    replay(attributeValueDAOMock);
    attributeTypeService.checkForMultipleAssignments(enumAT);
    verify(attributeValueDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#getAttributeTypeByName(java.lang.String)}.
   */
  @Test
  public void testGetAttributeTypeByName() {
    expect(attributeTypeDAOMock.getAttributeTypeByName(dateAT.getName())).andReturn(dateAT);
    replay(attributeTypeDAOMock);
    assertEquals(dateAT, attributeTypeService.getAttributeTypeByName(dateAT.getName()));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#getAttributeTypesFiltered(java.lang.Integer, java.lang.String, java.util.List)}.
   */
  @Test
  public void testGetAttributeTypesFilteredExclude() {
    expect(attributeTypeDAOMock.loadFilteredElementList("name", Sets.newHashSet(enumAT.getId()))).andReturn(
        Lists.<AttributeType> newArrayList(dateAT));
    replay(attributeTypeDAOMock);
    List<AttributeType> list = attributeTypeService.getAttributeTypesFiltered(Lists.<AttributeType> newArrayList(enumAT));
    assertEquals(1, list.size());
    assertTrue(list.contains(dateAT));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#getAttributeTypesForBuildingBlockType(de.iteratec.iteraplan.model.TypeOfBuildingBlock)}.
   */
  @Test
  public void testGetAttributeTypesForBuildingBlockType() {
    expect(attributeTypeDAOMock.getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.INFORMATIONSYSTEM, false)).andReturn(
        Lists.newArrayList(dateAT, enumAT));
    replay(attributeTypeDAOMock);
    List<AttributeType> list = attributeTypeService.getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.INFORMATIONSYSTEM, false);
    assertEquals(2, list.size());
    assertTrue(list.contains(dateAT));
    assertTrue(list.contains(enumAT));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#getAttributeBySearch(de.iteratec.iteraplan.model.attribute.AttributeType)}.
   */
  @Test
  public void testGetAttributeBySearch() {
    List<AttributeType> list = Lists.<AttributeType> newArrayList(dateAT);
    expect(attributeTypeDAOMock.findBySearchTerm(dateAT.getName(), "name")).andReturn(list);
    replay(attributeTypeDAOMock);
    assertEquals(list, attributeTypeService.getAttributeBySearch(dateAT));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#deleteEntity(de.iteratec.iteraplan.model.attribute.AttributeType)}.
   */
  @Test
  public void testDeleteEntityAttributeDate() {
    setSuperUserContext();

    DateAT at = new DateAT();
    at.setId(Integer.valueOf(12478));
    at.setName("Attribute for Test");
    at.setAttributeTypeGroup(atg);
    atg.addAttributeType(at, 1);
    DateAV av = new DateAV(at, new Date());
    av.setAttributeTypeTwoWay(at);
    av.setAttributeValueAssignments(Sets.newHashSet(new AttributeValueAssignment()));

    expect(attributeTypeDAOMock.loadObjectById(at.getId())).andReturn(at);
    attributeTypeDAOMock.delete(at);
    attributeValueDAOMock.deleteAll(at.getAllAttributeValues());
    expect(timeseriesDAOMock.deleteTimeseriesByAttributeTypeId(at.getId())).andReturn(0);
    replay(attributeTypeDAOMock, attributeValueDAOMock, timeseriesDAOMock);

    attributeTypeService.deleteEntity(at);
    assertTrue(av.getAttributeValueAssignments().isEmpty());
    verify(attributeTypeDAOMock, attributeValueDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#deleteEntity(de.iteratec.iteraplan.model.attribute.AttributeType)}.
   */
  @Test
  public void testDeleteEntityAttributeEnum() {
    setSuperUserContext();

    EnumAT at = new EnumAT();
    at.setId(Integer.valueOf(12478));
    at.setName("Attribute for Test");
    at.setAttributeTypeGroup(atg);
    atg.addAttributeType(at, 1);
    EnumAV av = new EnumAV();
    av.setAttributeType(at);
    av.setAttributeTypeTwoWay(at);
    av.setAttributeValueAssignments(Sets.newHashSet(new AttributeValueAssignment()));

    expect(attributeTypeDAOMock.loadObjectById(at.getId())).andReturn(at);
    attributeTypeDAOMock.delete(at);
    attributeValueDAOMock.deleteAll(at.getAllAttributeValues());
    expect(timeseriesDAOMock.deleteTimeseriesByAttributeTypeId(at.getId())).andReturn(0);
    replay(attributeTypeDAOMock, attributeValueDAOMock, timeseriesDAOMock);

    attributeTypeService.deleteEntity(at);
    assertTrue(av.getAttributeValueAssignments().isEmpty());
    verify(attributeTypeDAOMock, attributeValueDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#deleteEntity(de.iteratec.iteraplan.model.attribute.AttributeType)}.
   */
  @Test
  public void testDeleteEntityAttributeNumber() {
    setSuperUserContext();

    NumberAT at = new NumberAT();
    at.setId(Integer.valueOf(12478));
    at.setName("Attribute for Test");
    at.setAttributeTypeGroup(atg);
    atg.addAttributeType(at, 1);
    NumberAV av = new NumberAV(at, BigDecimal.TEN);
    av.setAttributeTypeTwoWay(at);

    expect(attributeTypeDAOMock.loadObjectById(at.getId())).andReturn(at);
    attributeTypeDAOMock.delete(at);
    attributeValueDAOMock.deleteAll(at.getAllAttributeValues());
    expect(timeseriesDAOMock.deleteTimeseriesByAttributeTypeId(at.getId())).andReturn(0);
    replay(attributeTypeDAOMock, attributeValueDAOMock, timeseriesDAOMock);

    attributeTypeService.deleteEntity(at);
    verify(attributeTypeDAOMock, attributeValueDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#deleteEntity(de.iteratec.iteraplan.model.attribute.AttributeType)}.
   */
  @Test
  public void testDeleteEntityAttributeResponsibility() {
    setSuperUserContext();

    ResponsibilityAT at = new ResponsibilityAT();
    at.setId(Integer.valueOf(12478));
    at.setName("Attribute for Test");
    at.setAttributeTypeGroup(atg);
    atg.addAttributeType(at, 1);
    ResponsibilityAV av = new ResponsibilityAV();
    av.setAttributeType(at);
    av.addAttributeTypeTwoWay(at);
    av.setAttributeValueAssignments(Sets.newHashSet(new AttributeValueAssignment()));

    expect(attributeTypeDAOMock.loadObjectById(at.getId())).andReturn(at);
    attributeTypeDAOMock.delete(at);
    attributeValueDAOMock.deleteAll(at.getAllAttributeValues());
    expect(timeseriesDAOMock.deleteTimeseriesByAttributeTypeId(at.getId())).andReturn(0);
    replay(attributeTypeDAOMock, attributeValueDAOMock, timeseriesDAOMock);

    attributeTypeService.deleteEntity(at);
    assertTrue(av.getAttributeValueAssignments().isEmpty());
    verify(attributeTypeDAOMock, attributeValueDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#deleteEntity(de.iteratec.iteraplan.model.attribute.AttributeType)}.
   */
  @Test
  public void testDeleteEntityAttributeText() {
    setSuperUserContext();

    TextAT at = new TextAT();
    at.setId(Integer.valueOf(12478));
    at.setName("Attribute for Test");
    at.setAttributeTypeGroup(atg);
    atg.addAttributeType(at, 1);
    TextAV av = new TextAV(at, "text");
    av.setAttributeTypeTwoWay(at);

    expect(attributeTypeDAOMock.loadObjectById(at.getId())).andReturn(at);
    attributeTypeDAOMock.delete(at);
    attributeValueDAOMock.deleteAll(at.getAllAttributeValues());
    expect(timeseriesDAOMock.deleteTimeseriesByAttributeTypeId(at.getId())).andReturn(0);
    replay(attributeTypeDAOMock, attributeValueDAOMock, timeseriesDAOMock);

    attributeTypeService.deleteEntity(at);
    verify(attributeTypeDAOMock, attributeValueDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#loadObjectById(java.lang.Integer, java.lang.Class)}.
   */
  @Test
  public void testLoadObjectById() {
    expect(attributeTypeDAOMock.loadObjectById(dateAT.getId(), DateAT.class)).andReturn(dateAT);
    replay(attributeTypeDAOMock);
    assertEquals(dateAT, attributeTypeService.loadObjectById(dateAT.getId(), DateAT.class));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#testSaveOrUpdate(de.iteratec.iteraplan.model.attribute.AttributeType)}.
   */
  @Test
  public void testSaveOrUpdateEnum() {
    expect(attributeTypeDAOMock.saveOrUpdate(enumAT)).andReturn(enumAT);
    replay(attributeTypeDAOMock);
    attributeTypeService.saveOrUpdate(enumAT);
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeService#testSaveOrUpdate(de.iteratec.iteraplan.model.attribute.AttributeType)}.
   */
  @Test
  public void testSaveOrUpdateOther() {
    expect(attributeTypeDAOMock.saveOrUpdate(dateAT)).andReturn(dateAT);
    replay(attributeTypeDAOMock);
    attributeTypeService.saveOrUpdate(dateAT);
    verify(attributeTypeDAOMock);
  }

  private void setUserContextWithPermissions(Collection<PermissionFunctional> functionalPermissions, Collection<PermissionAttrTypeGroup> patgs) {
    User user = new User();
    user.setDataSource("MASTER");
    user.setLoginName("user123");

    Role role = new Role();
    role.setRoleName("testRole");

    if (functionalPermissions != null) {
      role.addPermissionFunctionals(functionalPermissions);
    }
    if (patgs != null) {
      role.addPermissionsAttrTypeGroups(patgs);
    }

    Set<Role> roleSet = new HashSet<Role>();
    roleSet.add(role);

    UserContext userContext = new UserContext("system", roleSet, Locale.getDefault(), user);
    UserContext.setCurrentUserContext(userContext);
  }

  private void setSuperUserContext() {
    User user = new User();
    user.setDataSource("MASTER");
    user.setLoginName("system");

    Role superRole = new Role();
    superRole.setRoleName(Role.SUPERVISOR_ROLE_NAME);

    Set<Role> roleSet = new HashSet<Role>();
    roleSet.add(superRole);

    UserContext userContext = new UserContext("system", roleSet, Locale.getDefault(), user);
    UserContext.setCurrentUserContext(userContext);
  }
}

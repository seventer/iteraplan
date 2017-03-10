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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.TestAsSuperUser;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
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
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.AttributeValueDAO;
import de.iteratec.iteraplan.persistence.dao.GeneralBuildingBlockDAO;


/**
 * Class for testing the service methods of the {@link AttributeValueService} interface.
 * 
 * @author sip
 */
public class AttributeValueService1Test extends TestAsSuperUser {

  private AttributeValueServiceImpl attributeValueService;
  private AttributeValueDAO         attributeValueDAOMock;
  private GeneralBuildingBlockDAO   generalBuildingBlockDAOMock;
  private AttributeTypeDAO          attributeTypeDAOMock;

  private AttributeTypeGroup        atg;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    attributeValueService = new AttributeValueServiceImpl();

    attributeValueDAOMock = createNiceMock(AttributeValueDAO.class);
    attributeValueService.setAttributeValueDAO(attributeValueDAOMock);

    attributeTypeDAOMock = createNiceMock(AttributeTypeDAO.class);
    attributeValueService.setAttributeTypeDAO(attributeTypeDAOMock);

    generalBuildingBlockDAOMock = createNiceMock(GeneralBuildingBlockDAO.class);
    attributeValueService.setGeneralBuildingBlockDAO(generalBuildingBlockDAOMock);

    atg = new AttributeTypeGroup();
    atg.setId(Integer.valueOf(1999));
    atg.setName("ATG");
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getAllAVs(java.lang.Integer)}.
   */
  @Test
  public void testGetAllAVsEnum() {
    EnumAT enumAT = new EnumAT();
    enumAT.setId(Integer.valueOf(2001));
    enumAT.setName("Enum AT");
    atg.addAttributeTypeTwoWay(enumAT, 1);
    EnumAV av1 = new EnumAV();
    av1.setName("hello");
    enumAT.addAttribueValueTwoWay(av1);
    EnumAV av2 = new EnumAV();
    av2.setName("world");
    enumAT.addAttribueValueTwoWay(av2);

    expect(attributeTypeDAOMock.loadObjectById(enumAT.getId())).andReturn(enumAT);
    replay(attributeTypeDAOMock);
    List<? extends AttributeValue> list = attributeValueService.getAllAVs(enumAT.getId());
    assertFalse(list.isEmpty());
    assertEquals(enumAT.getAttributeValues().size(), list.size());
    assertTrue(list.containsAll(enumAT.getAttributeValues()));
    assertTrue(enumAT.getAttributeValues().containsAll(list));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getAllAVs(java.lang.Integer)}.
   */
  @Test
  public void testGetAllAVsNumber() {
    NumberAT numberAT = new NumberAT();
    numberAT.setId(Integer.valueOf(2002));
    numberAT.setName("Number AT");
    atg.addAttributeTypeTwoWay(numberAT, 2);
    NumberAV numberAV1 = new NumberAV(numberAT, new BigDecimal(23));
    numberAT.addAttribueValueTwoWay(numberAV1);
    NumberAV numberAV2 = new NumberAV(numberAT, new BigDecimal(2532));
    numberAT.addAttribueValueTwoWay(numberAV2);
    NumberAV av3 = new NumberAV(numberAT, new BigDecimal(5));
    numberAT.addAttribueValueTwoWay(av3);

    expect(attributeTypeDAOMock.loadObjectById(numberAT.getId())).andReturn(numberAT);
    replay(attributeTypeDAOMock);
    List<? extends AttributeValue> list = attributeValueService.getAllAVs(numberAT.getId());
    assertFalse(list.isEmpty());
    assertEquals(numberAT.getAttributeValues().size(), list.size());
    assertTrue(list.containsAll(numberAT.getAttributeValues()));
    assertTrue(numberAT.getAttributeValues().containsAll(list));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getAllAVs(java.lang.Integer)}.
   */
  @Test
  public void testGetAllAVsText() {
    TextAT textAT = new TextAT();
    textAT.setId(Integer.valueOf(2003));
    textAT.setName("Text AT");
    atg.addAttributeTypeTwoWay(textAT, 3);
    TextAV textAV1 = new TextAV(textAT, "hello");
    TextAV textAV2 = new TextAV(textAT, "world");
    TextAV textAV3 = new TextAV(textAT, "!");
    textAT.setAttributeValues(Sets.newHashSet(textAV1, textAV2, textAV3));

    expect(attributeTypeDAOMock.loadObjectById(textAT.getId())).andReturn(textAT);
    replay(attributeTypeDAOMock);
    List<? extends AttributeValue> list = attributeValueService.getAllAVs(textAT.getId());
    assertFalse(list.isEmpty());
    assertEquals(textAT.getAttributeValues().size(), list.size());
    assertTrue(list.containsAll(textAT.getAttributeValues()));
    assertTrue(textAT.getAttributeValues().containsAll(list));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getAllAVs(java.lang.Integer)}.
   */
  @Test
  public void testGetAllAVsDate() {
    DateAT dateAT = new DateAT();
    dateAT.setId(Integer.valueOf(2004));
    dateAT.setName("Date AT");
    atg.addAttributeTypeTwoWay(dateAT, 4);
    DateAV dateAV = new DateAV(dateAT, new Date(0));
    dateAT.addAttributeValue(dateAV);

    expect(attributeTypeDAOMock.loadObjectById(dateAT.getId())).andReturn(dateAT);
    replay(attributeTypeDAOMock);
    List<? extends AttributeValue> list = attributeValueService.getAllAVs(dateAT.getId());
    assertFalse(list.isEmpty());
    assertEquals(dateAT.getAttributeValues().size(), list.size());
    assertTrue(list.containsAll(dateAT.getAttributeValues()));
    assertTrue(dateAT.getAttributeValues().containsAll(list));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getAllAVs(java.lang.Integer)}.
   */
  @Test
  public void testGetAllAVsResponsibility() {
    ResponsibilityAT responsibilityAT = new ResponsibilityAT();
    responsibilityAT.setId(Integer.valueOf(2005));
    responsibilityAT.setName("Responsibility AT");
    atg.addAttributeTypeTwoWay(responsibilityAT, 5);
    responsibilityAT.addAttributeValuesTwoWay(Lists.newArrayList(new ResponsibilityAV(responsibilityAT)));

    expect(attributeTypeDAOMock.loadObjectById(responsibilityAT.getId())).andReturn(responsibilityAT);
    replay(attributeTypeDAOMock);
    List<? extends AttributeValue> list = attributeValueService.getAllAVs(responsibilityAT.getId());
    assertFalse(list.isEmpty());
    assertEquals(responsibilityAT.getAttributeValues().size(), list.size());
    assertTrue(list.containsAll(responsibilityAT.getAttributeValues()));
    assertTrue(responsibilityAT.getAttributeValues().containsAll(list));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getAllAVStrings(java.lang.Integer)}.
   */
  @Test
  public void testGetAllAVStringsEnum() {
    EnumAT enumAT = new EnumAT();
    enumAT.setId(Integer.valueOf(2001));
    enumAT.setName("Enum AT");
    atg.addAttributeTypeTwoWay(enumAT, 1);
    EnumAV av1 = new EnumAV();
    av1.setName("hello");
    enumAT.addAttribueValueTwoWay(av1);
    EnumAV av2 = new EnumAV();
    av2.setName("world");
    enumAT.addAttribueValueTwoWay(av2);

    expect(attributeTypeDAOMock.loadObjectById(enumAT.getId())).andReturn(enumAT);
    expect(attributeTypeDAOMock.loadObjectById(enumAT.getId(), EnumAT.class)).andReturn(enumAT);
    replay(attributeTypeDAOMock);
    List<String> list = attributeValueService.getAllAVStrings(enumAT.getId());
    assertEquals(2, list.size());
    assertEquals(av1.getName(), list.get(0));
    assertEquals(av2.getName(), list.get(1));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getAllAVStrings(java.lang.Integer)}.
   */
  @Test
  public void testGetAllAVStringsNumber() {
    NumberAT numberAT = new NumberAT();
    numberAT.setId(Integer.valueOf(2002));
    numberAT.setName("Number AT");
    atg.addAttributeTypeTwoWay(numberAT, 2);
    NumberAV numberAV1 = new NumberAV(numberAT, new BigDecimal(23));
    numberAT.addAttribueValueTwoWay(numberAV1);
    NumberAV numberAV2 = new NumberAV(numberAT, new BigDecimal(2532));
    numberAT.addAttribueValueTwoWay(numberAV2);
    NumberAV av3 = new NumberAV(numberAT, new BigDecimal(5));
    numberAT.addAttribueValueTwoWay(av3);

    expect(attributeTypeDAOMock.loadObjectById(numberAT.getId())).andReturn(numberAT);
    expect(attributeTypeDAOMock.loadObjectById(numberAT.getId(), NumberAT.class)).andReturn(numberAT);
    replay(attributeTypeDAOMock);
    List<String> list = attributeValueService.getAllAVStrings(numberAT.getId());
    assertEquals(3, list.size());
    assertEquals("23.00", list.get(0));
    assertEquals("2532.00", list.get(1));
    assertEquals("5.00", list.get(2));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getAllAVStrings(java.lang.Integer)}.
   */
  @Test
  public void testGetAllAVStringsText() {
    TextAT textAT = new TextAT();
    textAT.setId(Integer.valueOf(2003));
    textAT.setName("Text AT");
    atg.addAttributeTypeTwoWay(textAT, 3);
    TextAV textAV1 = new TextAV(textAT, "hello");
    TextAV textAV2 = new TextAV(textAT, "world");
    TextAV textAV3 = new TextAV(textAT, "!");
    textAT.setAttributeValues(Sets.newHashSet(textAV1, textAV2, textAV3));

    expect(attributeTypeDAOMock.loadObjectById(textAT.getId())).andReturn(textAT);
    expect(attributeTypeDAOMock.loadObjectById(textAT.getId(), TextAT.class)).andReturn(textAT);
    replay(attributeTypeDAOMock);
    List<String> list = attributeValueService.getAllAVStrings(textAT.getId());
    assertEquals(3, list.size());
    assertTrue(list.contains(textAV1.getValue()));
    assertTrue(list.contains(textAV2.getValue()));
    assertTrue(list.contains(textAV3.getValue()));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getAllAVStrings(java.lang.Integer)}.
   */
  @Test
  public void testGetAllAVStringsDate() {
    DateAT dateAT = new DateAT();
    dateAT.setId(Integer.valueOf(2004));
    dateAT.setName("Date AT");
    atg.addAttributeTypeTwoWay(dateAT, 4);
    DateAV dateAV = new DateAV(dateAT, new Date(0));
    dateAT.addAttributeValue(dateAV);

    expect(attributeTypeDAOMock.loadObjectById(dateAT.getId())).andReturn(dateAT);
    expect(attributeTypeDAOMock.loadObjectById(dateAT.getId(), DateAT.class)).andReturn(dateAT);
    replay(attributeTypeDAOMock);
    List<String> list = attributeValueService.getAllAVStrings(dateAT.getId());
    assertEquals(1, list.size());
    assertEquals("01/01/1970", list.get(0));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getAllAVStrings(java.lang.Integer)}.
   */
  @Test
  public void testGetAllAVStringsResponsibility() {
    ResponsibilityAT responsibilityAT = new ResponsibilityAT();
    responsibilityAT.setId(Integer.valueOf(2005));
    responsibilityAT.setName("Responsibility AT");
    atg.addAttributeTypeTwoWay(responsibilityAT, 5);
    responsibilityAT.addAttributeValuesTwoWay(Lists.newArrayList(new ResponsibilityAV(responsibilityAT)));

    expect(attributeTypeDAOMock.loadObjectById(responsibilityAT.getId())).andReturn(responsibilityAT);
    expect(attributeTypeDAOMock.loadObjectById(responsibilityAT.getId(), ResponsibilityAT.class)).andReturn(responsibilityAT);
    replay(attributeTypeDAOMock);
    List<String> list = attributeValueService.getAllAVStrings(responsibilityAT.getId());
    assertEquals(1, list.size());
    assertNull("Expected null as no user entity was assigned", list.get(0));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getAVStringsForBuildingBlocks(java.lang.Integer, java.util.List)}.
   */
  @Test
  public void testGetAVStringsForBuildingBlocksEnum() {
    EnumAT enumAT = new EnumAT();
    enumAT.setId(Integer.valueOf(2001));
    enumAT.setName("Enum AT");
    atg.addAttributeTypeTwoWay(enumAT, 1);

    EnumAV av1 = new EnumAV();
    av1.setName("hello");
    av1.setId(Integer.valueOf(2334));
    enumAT.addAttribueValueTwoWay(av1);

    EnumAV av2 = new EnumAV();
    av2.setName("world");
    av2.setId(Integer.valueOf(222111));
    enumAT.addAttribueValueTwoWay(av2);

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));
    product.getAttributeValueAssignments().add(new AttributeValueAssignment(product, av1));
    product.getAttributeValueAssignments().add(new AttributeValueAssignment(product, av2));

    expect(attributeTypeDAOMock.loadObjectById(enumAT.getId())).andReturn(enumAT);
    replay(attributeTypeDAOMock);

    List<String> list = attributeValueService.getAVStringsForBuildingBlocks(enumAT.getId(), Lists.<BuildingBlock> newArrayList(product));
    assertEquals(2, list.size());
    assertTrue(list.contains(av1.getName()));
    assertTrue(list.contains(av2.getName()));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getAVStringsForBuildingBlocks(java.lang.Integer, java.util.List)}.
   */
  @Test
  public void testGetAVStringsForBuildingBlocksNumber() {
    NumberAT numberAT = new NumberAT();
    numberAT.setId(Integer.valueOf(2002));
    numberAT.setName("Number AT");
    atg.addAttributeTypeTwoWay(numberAT, 2);

    NumberAV av1 = new NumberAV(numberAT, new BigDecimal(23));
    av1.setId(Integer.valueOf(232239));
    numberAT.addAttribueValueTwoWay(av1);

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));
    product.getAttributeValueAssignments().add(new AttributeValueAssignment(product, av1));

    expect(attributeTypeDAOMock.loadObjectById(numberAT.getId())).andReturn(numberAT);
    replay(attributeTypeDAOMock);

    List<String> list = attributeValueService.getAVStringsForBuildingBlocks(numberAT.getId(), Lists.<BuildingBlock> newArrayList(product));
    assertEquals(1, list.size());
    assertTrue(list.contains(av1.getLocalizedValueString(UserContext.getCurrentLocale())));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getAVStringsForBuildingBlocks(java.lang.Integer, java.util.List)}.
   */
  @Test
  public void testGetAVStringsForBuildingBlocksText() {
    TextAT textAT = new TextAT();
    textAT.setId(Integer.valueOf(2003));
    textAT.setName("Text AT");
    atg.addAttributeTypeTwoWay(textAT, 1);
    TextAV textAV1 = new TextAV(textAT, "hello");
    textAV1.setId(Integer.valueOf(34543));
    TextAV textAV2 = new TextAV(textAT, "world");
    textAV2.setId(Integer.valueOf(43253));
    TextAV textAV3 = new TextAV(textAT, "!");
    textAV3.setId(Integer.valueOf(76543));
    textAT.setAttributeValues(Sets.newHashSet(textAV1, textAV2, textAV3));

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));
    product.getAttributeValueAssignments().add(new AttributeValueAssignment(product, textAV1));
    product.getAttributeValueAssignments().add(new AttributeValueAssignment(product, textAV2));

    Project project = new Project();
    project.setName("Test BB2");
    project.setId(Integer.valueOf(5452));
    project.getAttributeValueAssignments().add(new AttributeValueAssignment(project, textAV2));
    project.getAttributeValueAssignments().add(new AttributeValueAssignment(project, textAV3));

    expect(attributeTypeDAOMock.loadObjectById(textAT.getId())).andReturn(textAT);
    replay(attributeTypeDAOMock);

    List<String> list = attributeValueService.getAVStringsForBuildingBlocks(textAT.getId(), Lists.<BuildingBlock> newArrayList(product, project));
    assertEquals(3, list.size());
    assertEquals(textAV3.getLocalizedValueString(UserContext.getCurrentLocale()), list.get(0));
    assertEquals(textAV1.getLocalizedValueString(UserContext.getCurrentLocale()), list.get(1));
    assertEquals(textAV2.getLocalizedValueString(UserContext.getCurrentLocale()), list.get(2));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getAVStringsForBuildingBlocks(java.lang.Integer, java.util.List)}.
   */
  @Test
  public void testGetAVStringsForBuildingBlocksDate() {
    Date d1 = new Date(200000000);
    Date d2 = new Date(100000000);

    DateAT dateAT = new DateAT();
    dateAT.setId(Integer.valueOf(2003));
    dateAT.setName("Date AT");
    atg.addAttributeTypeTwoWay(dateAT, 1);
    DateAV dateAV1 = new DateAV(dateAT, d1);
    dateAV1.setId(Integer.valueOf(34543));
    DateAV dateAV2 = new DateAV(dateAT, d2);
    dateAV2.setId(Integer.valueOf(34543));
    dateAT.setAttributeValues(Sets.newHashSet(dateAV1, dateAV2));

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));
    product.getAttributeValueAssignments().add(new AttributeValueAssignment(product, dateAV1));
    product.getAttributeValueAssignments().add(new AttributeValueAssignment(product, dateAV2));

    expect(attributeTypeDAOMock.loadObjectById(dateAT.getId())).andReturn(dateAT);
    replay(attributeTypeDAOMock);

    List<String> list = attributeValueService.getAVStringsForBuildingBlocks(dateAT.getId(), Lists.<BuildingBlock> newArrayList(product));
    assertEquals(2, list.size());
    assertEquals(dateAV2.getLocalizedValueString(UserContext.getCurrentLocale()), list.get(0));
    assertEquals(dateAV1.getLocalizedValueString(UserContext.getCurrentLocale()), list.get(1));
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#getBuildingBlockIdsToConnectedAttributeValues(java.util.List, java.lang.Integer)}.
   */
  @Test
  public void testGetBuildingBlockIdsToConnectedAttributeValues() {
    TextAT textAT = new TextAT();
    textAT.setId(Integer.valueOf(2003));
    textAT.setName("Text AT");
    TextAV textAV1 = new TextAV(textAT, "hello");
    TextAV textAV2 = new TextAV(textAT, "world");
    TextAV textAV3 = new TextAV(textAT, "!");
    textAT.setAttributeValues(Sets.newHashSet(textAV1, textAV2, textAV3));

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));
    product.getAttributeValueAssignments().add(new AttributeValueAssignment(product, textAV1));
    product.getAttributeValueAssignments().add(new AttributeValueAssignment(product, textAV2));

    Project project = new Project();
    project.setName("Test BB2");
    project.setId(Integer.valueOf(5452));
    project.getAttributeValueAssignments().add(new AttributeValueAssignment(project, textAV2));
    project.getAttributeValueAssignments().add(new AttributeValueAssignment(project, textAV3));

    expect(attributeTypeDAOMock.loadObjectById(textAT.getId())).andReturn(textAT);
    expect(generalBuildingBlockDAOMock.loadObjectById(product.getId())).andReturn(product);
    expect(generalBuildingBlockDAOMock.loadObjectById(project.getId())).andReturn(project);
    replay(attributeTypeDAOMock, generalBuildingBlockDAOMock);

    HashBucketMap<Integer, AttributeValue> list = attributeValueService.getBuildingBlockIdsToConnectedAttributeValues(
        Lists.newArrayList(product.getId(), project.getId()), textAT.getId());
    assertEquals(2, list.size());
    assertEquals(2, list.getBucketNotNull(product.getId()).size());
    assertTrue(list.getBucketNotNull(product.getId()).contains(textAV1));
    assertTrue(list.getBucketNotNull(product.getId()).contains(textAV2));
    assertEquals(2, list.getBucketNotNull(project.getId()).size());
    assertTrue(list.getBucketNotNull(project.getId()).contains(textAV2));
    assertTrue(list.getBucketNotNull(project.getId()).contains(textAV3));
    verify(attributeTypeDAOMock, generalBuildingBlockDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#saveOrUpdate(de.iteratec.iteraplan.model.attribute.AttributeValue)}.
   */
  @Test
  public void testSaveOrUpdate() {
    TextAT textAT = new TextAT();
    textAT.setId(Integer.valueOf(2003));
    textAT.setName("Text AT");
    TextAV textAV1 = new TextAV(textAT, "bla");

    expect(attributeValueDAOMock.saveOrUpdate(textAV1)).andReturn(textAV1);
    replay(attributeValueDAOMock);
    attributeValueService.saveOrUpdate(textAV1);
    verify(attributeValueDAOMock);
  }

}

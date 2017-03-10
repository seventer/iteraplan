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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.TestAsSuperUser;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.AttributeValueDAO;


/**
 * Class for testing the service methods of the {@link AttributeValueService} interface.
 * 
 * @author sip
 */
public class AttributeValueService2Test extends TestAsSuperUser {

  private AttributeValueServiceImpl attributeValueService;
  private AttributeValueDAO         attributeValueDAOMock;
  private AttributeTypeDAO          attributeTypeDAOMock;

  /**s
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    attributeValueService = new AttributeValueServiceImpl();

    attributeValueDAOMock = createNiceMock(AttributeValueDAO.class);
    attributeValueService.setAttributeValueDAO(attributeValueDAOMock);

    attributeTypeDAOMock = createNiceMock(AttributeTypeDAO.class);
    attributeValueService.setAttributeTypeDAO(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#reload(java.util.Collection)}.
   */
  @Test
  public void testReloadCollection() {
    TextAT textAT = new TextAT();
    textAT.setId(Integer.valueOf(2003));
    textAT.setName("Text AT");

    Integer id1 = Integer.valueOf(99446);
    TextAV av1 = new TextAV(textAT, "text");
    av1.setId(id1);
    TextAV avReloaded1 = new TextAV(textAT, "text2");
    avReloaded1.setId(id1);

    Integer id2 = Integer.valueOf(88446);
    TextAV av2 = new TextAV(textAT, "123");
    av2.setId(id2);
    TextAV avReloaded2 = new TextAV(textAT, "1234");
    avReloaded2.setId(id2);

    expect(attributeValueDAOMock.loadElementListWithIds(Sets.newHashSet(id1, id2))).andReturn(
        Lists.<AttributeValue> newArrayList(avReloaded1, avReloaded2));
    replay(attributeValueDAOMock);

    List<TextAV> res = attributeValueService.reload(Lists.newArrayList(av1, av2));
    assertEquals(2, res.size());
    assertTrue(res.contains(avReloaded1));
    assertEquals(avReloaded1.getValue(), res.get(res.indexOf(avReloaded1)).getValue());
    assertTrue(res.contains(avReloaded2));
    assertEquals(avReloaded2.getValue(), res.get(res.indexOf(avReloaded2)).getValue());
    verify(attributeValueDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#reload(java.util.Collection)}.
   */
  @Test
  public void testReloadCollectionNull() {
    List<DateAV> list = attributeValueService.reload((List<DateAV>) null);
    assertNotNull(list);
    assertTrue(list.isEmpty());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#reload(java.util.Collection)}.
   */
  @Test
  public void testReloadCollectionEmpty() {
    List<DateAV> list = attributeValueService.reload(Lists.<DateAV> newArrayList());
    assertNotNull(list);
    assertTrue(list.isEmpty());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#reload(de.iteratec.iteraplan.model.attribute.AttributeValue)}.
   */
  @Test
  public void testReload() {
    TextAT textAT = new TextAT();
    textAT.setId(Integer.valueOf(2003));
    textAT.setName("AT");

    Integer id1 = Integer.valueOf(99446);
    TextAV av = new TextAV(textAT, "text");
    av.setId(id1);
    TextAV avReloaded = new TextAV(textAT, "text2");
    avReloaded.setId(id1);

    expect(attributeValueDAOMock.loadObjectById(id1, TextAV.class)).andReturn(avReloaded);
    replay(attributeValueDAOMock);

    TextAV res = attributeValueService.reload(av);
    assertEquals(avReloaded.getValue(), res.getValue());
    verify(attributeValueDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#reload(de.iteratec.iteraplan.model.attribute.AttributeValue)}.
   */
  @Test
  public void testReloadNull() {
    DateAV av = attributeValueService.reload((DateAV) null);
    assertNull(av);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#setReferenceValues(de.iteratec.iteraplan.model.BuildingBlock, java.util.Collection, java.lang.Integer)}.
   * Empty list -> delete all assignments
   */
  @Test
  public void testSetReferenceValuesEmptyList() {
    EnumAT enumAT = new EnumAT();
    enumAT.setId(Integer.valueOf(2003));
    enumAT.setName("AT");

    EnumAV enumAV1 = new EnumAV();
    enumAV1.setAttributeType(enumAT);
    enumAV1.setName("hello");
    enumAV1.setId(Integer.valueOf(2342212));
    enumAT.addAttribueValueTwoWay(enumAV1);

    EnumAV enumAV2 = new EnumAV();
    enumAV2.setAttributeType(enumAT);
    enumAV2.setName("world");
    enumAV2.setId(Integer.valueOf(289872));
    enumAT.addAttribueValueTwoWay(enumAV2);

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));
    product.getAttributeValueAssignments().add(new AttributeValueAssignment(product, enumAV1));
    product.getAttributeValueAssignments().add(new AttributeValueAssignment(product, enumAV2));

    attributeValueService.setReferenceValues(product, Lists.<TextAV> newArrayList(), enumAT.getId());
    assertTrue(product.getAssignmentsForId(enumAT.getId()).isEmpty());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#setReferenceValues(de.iteratec.iteraplan.model.BuildingBlock, java.util.Collection, java.lang.Integer)}.
   * Delete an AttributeValue with this method
   */
  @Test
  public void testSetReferenceValuesDeleteAV() {
    EnumAT enumAT = new EnumAT();
    enumAT.setId(Integer.valueOf(2003));
    enumAT.setName("AT");

    EnumAV enumAV1 = new EnumAV();
    enumAV1.setAttributeType(enumAT);
    enumAV1.setName("hello");
    enumAV1.setId(Integer.valueOf(2342212));
    enumAT.addAttribueValueTwoWay(enumAV1);

    EnumAV enumAV2 = new EnumAV();
    enumAV2.setAttributeType(enumAT);
    enumAV2.setName("world");
    enumAV2.setId(Integer.valueOf(289872));
    enumAT.addAttribueValueTwoWay(enumAV2);

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));

    AttributeValueAssignment ava1 = new AttributeValueAssignment(product, enumAV1);
    ava1.setId(Integer.valueOf(23432));
    product.getAttributeValueAssignments().add(ava1);

    AttributeValueAssignment ava2 = new AttributeValueAssignment(product, enumAV2);
    ava2.setId(Integer.valueOf(234323));
    product.getAttributeValueAssignments().add(ava2);

    expect(attributeValueDAOMock.loadObjectById(enumAV1.getId(), EnumAV.class)).andReturn(enumAV1);
    replay(attributeValueDAOMock);

    attributeValueService.setReferenceValues(product, Lists.newArrayList(enumAV1), enumAT.getId());
    assertEquals(1, product.getAssignmentsForId(enumAT.getId()).size());
    assertTrue(product.getAssignmentsForId(enumAT.getId()).contains(ava1));
    verify(attributeValueDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#setReferenceValues(de.iteratec.iteraplan.model.BuildingBlock, java.util.Collection, java.lang.Integer)}.
   * Delete an AttributeValue with this method
   */
  @Test
  public void testSetReferenceValuesReloadNull() {
    EnumAT enumAT = new EnumAT();
    enumAT.setId(Integer.valueOf(2003));
    enumAT.setName("AT");

    EnumAV enumAV1 = new EnumAV();
    enumAV1.setAttributeType(enumAT);
    enumAV1.setName("hello");
    enumAV1.setId(Integer.valueOf(2342212));
    enumAT.addAttribueValueTwoWay(enumAV1);

    EnumAV enumAV2 = new EnumAV();
    enumAV2.setAttributeType(enumAT);
    enumAV2.setName("world");
    enumAV2.setId(Integer.valueOf(289872));
    enumAT.addAttribueValueTwoWay(enumAV2);

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));

    AttributeValueAssignment ava1 = new AttributeValueAssignment(product, enumAV1);
    ava1.setId(Integer.valueOf(23432));
    product.getAttributeValueAssignments().add(ava1);

    AttributeValueAssignment ava2 = new AttributeValueAssignment(product, enumAV2);
    ava2.setId(Integer.valueOf(234323));
    product.getAttributeValueAssignments().add(ava2);

    expect(attributeValueDAOMock.loadObjectById(enumAV1.getId(), EnumAV.class)).andReturn(enumAV1);
    expect(attributeValueDAOMock.loadObjectById(enumAV2.getId(), EnumAV.class)).andReturn(null);
    replay(attributeValueDAOMock);

    attributeValueService.setReferenceValues(product, Lists.newArrayList(enumAV1, enumAV2), enumAT.getId());

    Set<AttributeValueAssignment> set = product.getAssignmentsForId(enumAT.getId());
    assertEquals(1, set.size());
    assertEquals(enumAV1, set.iterator().next().getAttributeValue());
    verify(attributeValueDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#setReferenceValues(de.iteratec.iteraplan.model.BuildingBlock, java.util.Collection, java.lang.Integer)}.
   * Add an AttributeValue with this method
   */
  @Test
  public void testSetReferenceValuesAddAV() {
    EnumAT enumAT = new EnumAT();
    enumAT.setId(Integer.valueOf(2003));
    enumAT.setName("AT");

    EnumAV enumAV = new EnumAV();
    enumAV.setAttributeType(enumAT);
    enumAV.setName("hello");
    enumAV.setId(Integer.valueOf(2342212));
    enumAT.addAttribueValueTwoWay(enumAV);

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));

    expect(attributeValueDAOMock.loadObjectById(enumAV.getId(), EnumAV.class)).andReturn(enumAV);
    replay(attributeValueDAOMock);

    attributeValueService.setReferenceValues(product, Lists.newArrayList(enumAV), enumAT.getId());
    Set<AttributeValueAssignment> set = product.getAssignmentsForId(enumAT.getId());
    assertEquals(1, set.size());
    assertEquals(enumAV, set.iterator().next().getAttributeValue());
    verify(attributeValueDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#setValue(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.attribute.AttributeValue, java.lang.Integer)}.
   */
  @Test
  public void testSetValueNull() {
    TextAT textAT = new TextAT();
    textAT.setId(Integer.valueOf(2003));
    textAT.setName("AT");
    TextAV textAV1 = new TextAV(textAT, "hello");
    textAV1.setId(Integer.valueOf(2342212));
    textAT.setAttributeValues(Sets.newHashSet(textAV1));

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));

    AttributeValueAssignment ava1 = new AttributeValueAssignment(product, textAV1);
    ava1.setId(Integer.valueOf(23432));
    product.getAttributeValueAssignments().add(ava1);

    attributeValueService.setValue(product, null, textAT);
    assertTrue(product.getAssignmentsForId(textAT.getId()).isEmpty());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#setValue(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.attribute.AttributeValue, java.lang.Integer)}.
   */
  @Test
  public void testSetValueNew() {
    TextAT textAT = new TextAT();
    textAT.setId(Integer.valueOf(2003));
    textAT.setName("AT");
    TextAV textAV = new TextAV(textAT, "hello");
    textAV.setId(Integer.valueOf(2342212));
    textAT.setAttributeValues(Sets.newHashSet(textAV));

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));

    attributeValueService.setValue(product, textAV, textAT);
    assertEquals(textAV, product.getAssignmentForId(textAT.getId()).getAttributeValue());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#setValue(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.attribute.AttributeValue, java.lang.Integer)}.
   */
  @Test
  public void testSetValueUpdateNumber() {
    NumberAT numberAT = new NumberAT();
    numberAT.setId(Integer.valueOf(2003));
    numberAT.setName("AT");
    NumberAV av = new NumberAV(numberAT, new BigDecimal(2321));
    av.setId(Integer.valueOf(2342212));
    numberAT.setAttributeValues(Sets.newHashSet(av));

    NumberAV newAv = new NumberAV(numberAT, new BigDecimal(1111));
    newAv.setId(Integer.valueOf(222111));

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));
    AttributeValueAssignment ava = new AttributeValueAssignment(product, av);
    ava.setId(Integer.valueOf(23432));
    product.getAttributeValueAssignments().add(ava);

    attributeValueService.setValue(product, newAv, numberAT);
    assertEquals(newAv.getValue(), ava.getAttributeValue().getValue());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#setValue(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.attribute.AttributeValue, java.lang.Integer)}.
   */
  @Test
  public void testSetValueUpdateDate() {
    DateAT dateAT = new DateAT();
    dateAT.setId(Integer.valueOf(2003));
    dateAT.setName("AT");
    DateAV av = new DateAV(dateAT, new Date(10000000));
    av.setId(Integer.valueOf(2342212));
    dateAT.setAttributeValues(Sets.newHashSet(av));

    DateAV newAv = new DateAV(dateAT, new Date(20000000));
    newAv.setId(Integer.valueOf(222111));

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));
    AttributeValueAssignment ava = new AttributeValueAssignment(product, av);
    ava.setId(Integer.valueOf(23432));
    product.getAttributeValueAssignments().add(ava);

    attributeValueService.setValue(product, newAv, dateAT);
    assertEquals(newAv.getValue(), ava.getAttributeValue().getValue());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#setValue(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.attribute.AttributeValue, java.lang.Integer)}.
   */
  @Test
  public void testSetValueUpdateText() {
    TextAT textAT = new TextAT();
    textAT.setId(Integer.valueOf(2003));
    textAT.setName("AT");
    TextAV av = new TextAV(textAT, "hello");
    av.setId(Integer.valueOf(2342212));
    textAT.setAttributeValues(Sets.newHashSet(av));

    TextAV newAv = new TextAV(textAT, "world");
    newAv.setId(Integer.valueOf(222111));

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));
    AttributeValueAssignment ava = new AttributeValueAssignment(product, av);
    ava.setId(Integer.valueOf(23432));
    product.getAttributeValueAssignments().add(ava);

    attributeValueService.setValue(product, newAv, textAT);
    assertEquals(newAv.getValue(), ava.getAttributeValue().getValue());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#setValue(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.attribute.AttributeValue, java.lang.Integer)}.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSetValueUpdateEnum() {
    EnumAT enumAT = new EnumAT();
    enumAT.setId(Integer.valueOf(2003));
    enumAT.setName("AT");
    EnumAV av = new EnumAV();
    av.setAttributeType(enumAT);
    av.setName("hello");
    av.setId(Integer.valueOf(2342212));
    enumAT.addAttribueValueTwoWay(av);

    EnumAV newAv = new EnumAV();
    newAv.setAttributeType(enumAT);
    newAv.setName("world");
    newAv.setId(Integer.valueOf(222111));

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));
    AttributeValueAssignment ava = new AttributeValueAssignment(product, av);
    ava.setId(Integer.valueOf(23432));
    product.getAttributeValueAssignments().add(ava);

    attributeValueService.setValue(product, newAv, enumAT);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#setValue(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.attribute.AttributeValue, java.lang.Integer)}.
   */
  @Test
  public void testSetValueNoATNumber() {
    NumberAT at = new NumberAT();
    at.setId(Integer.valueOf(2003));
    at.setName("AT");

    NumberAV av = new NumberAV(null, new BigDecimal(1111));
    av.setId(Integer.valueOf(222111));

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));

    replay(attributeTypeDAOMock);

    attributeValueService.setValue(product, av, at);
    assertEquals(at, av.getAttributeType());
    assertEquals(1, at.getAttributeValues().size());
    assertEquals(av, at.getAttributeValues().iterator().next());
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#setValue(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.attribute.AttributeValue, java.lang.Integer)}.
   */
  @Test
  public void testSetValueNoATDate() {
    DateAT at = new DateAT();
    at.setId(Integer.valueOf(2003));
    at.setName("AT");

    DateAV av = new DateAV(null, new Date());
    av.setId(Integer.valueOf(222111));

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));

    replay(attributeTypeDAOMock);

    attributeValueService.setValue(product, av, at);
    assertEquals(at, av.getAttributeType());
    assertEquals(1, at.getAttributeValues().size());
    assertEquals(av, at.getAttributeValues().iterator().next());
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#setValue(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.attribute.AttributeValue, java.lang.Integer)}.
   */
  @Test
  public void testSetValueNoATText() {
    TextAT at = new TextAT();
    at.setId(Integer.valueOf(2003));
    at.setName("AT");

    TextAV av = new TextAV(null, "abc");
    av.setId(Integer.valueOf(222111));

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));

    replay(attributeTypeDAOMock);

    attributeValueService.setValue(product, av, at);
    assertEquals(at, av.getAttributeType());
    assertEquals(1, at.getAttributeValues().size());
    assertEquals(av, at.getAttributeValues().iterator().next());
    verify(attributeTypeDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#setValue(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.attribute.AttributeValue, java.lang.Integer)}.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSetValueNoATEnum() {
    EnumAT at = new EnumAT();
    at.setId(Integer.valueOf(2003));
    at.setName("AT");

    EnumAV av = new EnumAV();
    av.setName("abc");
    av.setId(Integer.valueOf(222111));

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));

    expect(attributeTypeDAOMock.loadObjectById(at.getId(), EnumAT.class)).andReturn(at);
    replay(attributeTypeDAOMock);

    attributeValueService.setValue(product, av, at);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#loadObjectById(java.lang.Integer, java.lang.Class)}.
   */
  @Test
  public void testLoadObjectById() {
    TextAT textAT = new TextAT();
    textAT.setId(Integer.valueOf(2003));
    textAT.setName("Text AT");
    TextAV av = new TextAV(textAT, "text");

    expect(attributeValueDAOMock.loadObjectById(av.getId(), TextAV.class)).andReturn(av);
    replay(attributeValueDAOMock);

    assertEquals(av, attributeValueService.loadObjectById(av.getId(), TextAV.class));
    verify(attributeValueDAOMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueService#saveOrUpdateAttributeValues(de.iteratec.iteraplan.model.BuildingBlock)}.
   */
  @Test
  public void testSaveOrUpdateAttributeValues() {
    TextAT textAT = new TextAT();
    textAT.setId(Integer.valueOf(2003));
    textAT.setName("Text AT");
    TextAV av = new TextAV(textAT, "hello");
    av.setId(Integer.valueOf(2342212));
    textAT.setAttributeValues(Sets.newHashSet(av));

    Product product = new Product();
    product.setName("Test BB1");
    product.setId(Integer.valueOf(5451));
    AttributeValueAssignment ava = new AttributeValueAssignment(product, av);
    ava.setId(Integer.valueOf(23432));
    product.getAttributeValueAssignments().add(ava);

    expect(attributeValueDAOMock.saveOrUpdate(Lists.<AttributeValue> newArrayList(av))).andReturn(Lists.<AttributeValue> newArrayList(av));
    replay(attributeValueDAOMock);

    attributeValueService.saveOrUpdateAttributeValues(product);
    verify(attributeValueDAOMock);
  }
}

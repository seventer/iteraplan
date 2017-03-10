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
package de.iteratec.iteraplan.model.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class TextATTest extends AttributeTypeTestAbstract<TextAT> {

  private static final String NAME        = "tat";
  private static final String DESCRIPTION = "tat description";

  private TextAT              at;

  @Before
  public void setUp() {
    super.setUp();

    // arrange:
    at = new TextAT();
    at.setName(NAME);
    at.setDescription(DESCRIPTION);
    at.setAttributeTypeGroupTwoWay(getAtg());
    at.setMandatory(false);
    at.setMultiline(true);
  }

  @Test
  public void testGetAllAttributeTypes() {
    getHibernateTemplate().save(at);
    commit();

    // 1. TestCase
    AttributeType at1 = getAttributeTypeDAO().getAttributeTypeByName("filter");
    assertNull(at1);

    // 2. TestCase
    at1 = getAttributeTypeDAO().getAttributeTypeByName(NAME);
    assertNotNull(at1);
    assertEquals(NAME, at1.getName());
    assertEquals(DESCRIPTION, at1.getDescription());
    assertEquals(TypeOfAttribute.TEXT, at1.getTypeOfAttribute());
  }

  @Test
  public void testGetAttributeTypesForBuildingBlockType() {
    BuildingBlockType boType = getBuildingBlockTypeDAO().getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSOBJECT);

    // 1. TestCase
    List<AttributeType> list = getAttributeTypeDAO().getAttributeTypesForTypeOfBuildingBlock(boType.getTypeOfBuildingBlock(), false);
    assertEquals(0, list.size());

    // 2. TestCase
    at.setBuildingBlockTypes(new HashSet<BuildingBlockType>());
    at.addBuildingBlockTypeTwoWay(boType);
    getHibernateTemplate().save(at);
    commit();

    list = getAttributeTypeDAO().getAttributeTypesForTypeOfBuildingBlock(boType.getTypeOfBuildingBlock(), false);
    assertEquals(1, list.size());
    at = (TextAT) list.get(0);
    assertNotNull(at);
    assertNotNull(at);
    assertEquals(NAME, at.getName());
    assertEquals(DESCRIPTION, at.getDescription());
  }

  @Test
  public void testgetAttributeTypesForBuildingBlockType() {
    BuildingBlockType boType = getBuildingBlockTypeDAO().getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSOBJECT);
    at.setBuildingBlockTypes(new HashSet<BuildingBlockType>());
    at.addBuildingBlockTypeTwoWay(boType);
    getHibernateTemplate().save(at);
    commit();

    List<AttributeType> list = getAttributeTypeDAO().getAttributeTypesForTypeOfBuildingBlock(boType.getTypeOfBuildingBlock(), false);

    assertEquals(1, list.size());
    at = (TextAT) list.get(0);
    assertNotNull(at);
    assertEquals(NAME, at.getName());
    assertEquals(DESCRIPTION, at.getDescription());
  }

  @Test
  public void testGetAttributeTypeByName() {

    getHibernateTemplate().save(at);
    commit();

    // 1. TestCase
    at = (TextAT) getAttributeTypeDAO().getAttributeTypeByName("not found");
    assertNull(at);

    // 2. TestCase
    at = (TextAT) getAttributeTypeDAO().getAttributeTypeByName(NAME);
    assertNotNull(at);
    assertEquals(NAME, at.getName());
    assertEquals(DESCRIPTION, at.getDescription());
  }

  @Test
  public void testLoadObjectById() {
    getHibernateTemplate().save(at);
    commit();
    assertNotNull(at);
    assertNotNull(at.getId());

    at = (TextAT) getAttributeTypeDAO().loadObjectById(at.getId());
    assertNotNull(at);
    assertEquals(NAME, at.getName());
    assertEquals(DESCRIPTION, at.getDescription());
  }

  @Test
  public void testTextATUpdateFrom() {

    BuildingBlockType adType = getBuildingBlockTypeDAO().getBuildingBlockTypeByType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN);
    at.addBuildingBlockTypeTwoWay(adType);
    getHibernateTemplate().save(at);

    commit();
    beginTransaction();
    // act:

    assert (at != null);
    at.setName("tat new");
    at.setDescription("tat description new");
    at.setAttributeTypeGroup(getAtg2());
    at.setMandatory(false);
    at.setMultiline(false);
    BuildingBlockType boType = getBuildingBlockTypeDAO().getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSOBJECT);
    at.setBuildingBlockTypes(new HashSet<BuildingBlockType>());
    at.addBuildingBlockTypeOneWay(boType);

    rollback();
    beginTransaction();

    attributeTypeService.saveOrUpdate(at);

    commit();

    // assert:
    assertNotNull(at);
    assertEquals("tat new", at.getName());
    assertEquals("tat description new", at.getDescription());
    assertEquals("atg2", at.getAttributeTypeGroup().getName());
    assertFalse(at.isMandatory());
    assertFalse(at.isMultiline());
    assertEquals(1, at.getBuildingBlockTypes().size());
    Set<BuildingBlockType> buildingBlockTypes = at.getBuildingBlockTypes();
    for (BuildingBlockType type : buildingBlockTypes) {
      if (!type.getTypeOfBuildingBlock().equals(TypeOfBuildingBlock.BUSINESSOBJECT)) {
        fail("wrong building block type.");
      }
    }
  }

}

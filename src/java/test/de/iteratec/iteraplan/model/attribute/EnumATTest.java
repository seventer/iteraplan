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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class EnumATTest extends AttributeTypeTestAbstract<EnumAT> {

  private static final String NAME        = "eat";
  private static final String DESCRIPTION = "eat description";

  private EnumAT              at;
  @Autowired
  private TestDataHelper2     testDataHelper;

  @Override
  @Before
  public void setUp() {
    super.setUp();

    at = testDataHelper.createEnumAttributeType(NAME, DESCRIPTION, Boolean.TRUE, getAtg());
  }

  @Test
  public void testEnumATUpdateFrom() {

    BuildingBlockType adType = getBuildingBlockTypeDAO().getBuildingBlockTypeByType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN);
    at.addBuildingBlockTypeTwoWay(adType);

    getHibernateTemplate().save(at);

    EnumAV av1 = new EnumAV();
    av1.setName("av1");
    av1.setDescription("av1 description");
    av1.setAttributeTypeTwoWay(at);
    getHibernateTemplate().save(av1);

    EnumAV av2 = new EnumAV();
    av2.setName("av2");
    av2.setDescription("av2 description");
    av2.setAttributeTypeTwoWay(at);
    getHibernateTemplate().save(av2);

    commit();

    at.setName("eat new");
    at.setDescription("eat description new");
    at.setAttributeTypeGroup(getAtg2());
    at.setMandatory(false);
    at.setMultiassignmenttype(false);
    BuildingBlockType boType = getBuildingBlockTypeDAO().getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSOBJECT);
    at.setBuildingBlockTypes(new HashSet<BuildingBlockType>());
    at.addBuildingBlockTypeOneWay(boType);
    Set<EnumAV> attributeValues = at.getAttributeValues();
    for (EnumAV enumAV : attributeValues) {
      enumAV.setName(enumAV.getName() + " new");
      enumAV.setDescription(enumAV.getDescription() + " new");
    }
    EnumAV av3 = new EnumAV();
    av3.setName("av3 new");
    av3.setDescription("av3 description new");
    at.addAttribueValueTwoWay(av3);

    rollback();
    beginTransaction();
    attributeTypeService.saveOrUpdate(at);
    commit();

    beginTransaction();

    // assert:
    assertNotNull(at);
    assertEquals("eat new", at.getName());
    assertEquals("eat description new", at.getDescription());
    assertEquals("atg2", at.getAttributeTypeGroup().getName());
    assertFalse(at.isMandatory());
    assertFalse(at.isMultiassignmenttype());
    assertEquals(1, at.getBuildingBlockTypes().size());
    Set<BuildingBlockType> buildingBlockTypes = at.getBuildingBlockTypes();
    for (BuildingBlockType type : buildingBlockTypes) {
      if (!type.getTypeOfBuildingBlock().equals(TypeOfBuildingBlock.BUSINESSOBJECT)) {
        fail("wrong building block type.");
      }
    }
    assertEquals(3, at.getAttributeValues().size());
    Set<EnumAV> values = at.getAttributeValues();
    for (EnumAV enumAV : values) {
      assertTrue(enumAV.getName().contains("new"));
      assertTrue(enumAV.getDescription().contains("new"));
    }
  }

  @Test
  public void testEnumATValidate() {
    BuildingBlockType adType = getBuildingBlockTypeDAO().getBuildingBlockTypeByType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN);
    at.addBuildingBlockTypeTwoWay(adType);

    EnumAV av1 = new EnumAV();
    av1.setName("av1");
    av1.setDescription("av1 description");
    av1.setAttributeTypeTwoWay(at);
    getHibernateTemplate().save(av1);

    EnumAV av2 = new EnumAV();
    av2.setName("av2");
    av2.setDescription("av2 description");
    av2.setAttributeTypeTwoWay(at);
    getHibernateTemplate().save(av2);

    ArchitecturalDomain ad = testDataHelper.createArchitecturalDomain("ad", "ad desc");
    testDataHelper.createAVA(ad, av1);
    testDataHelper.createAVA(ad, av2);

    commit();

    // act & assert:
    beginTransaction();

    EnumAT validationAT = (EnumAT) getAttributeTypeDAO().getAttributeTypeByName("eat");

    // check unique constraint for attribute value names
    List<EnumAV> attributeValues = validationAT.getSortedAttributeValues();
    EnumAV firstAV = attributeValues.get(0);
    firstAV.setName("av2");
    try {
      validationAT.validate();
      fail("validation should have failed...");
    } catch (IteraplanBusinessException bex) {
      // expected behaviour
    }
    firstAV.setName("av neu");
    validationAT.validate();

    // check existing bbs with multiple attribute values constraint
    validationAT.setMultiassignmenttype(false);
    try {
      validationAT.validate();
      // check if multivalue setting is ok with attribute
      attributeTypeService.checkForMultipleAssignments(validationAT);
      fail("validation should have failed...");
    } catch (IteraplanBusinessException bex) {
      // expected behaviour
    }
    validationAT.setMultiassignmenttype(true);
    validationAT.validate();
  }
}

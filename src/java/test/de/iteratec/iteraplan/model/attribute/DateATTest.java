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
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class DateATTest extends AttributeTypeTestAbstract<DateAT> {

  private DateAT              at;

  private static final String TEST_NAME = "dat";

  @Before
  public void setUp() {
    super.setUp();
    at = new DateAT();
    at.setName(TEST_NAME);
    at.setDescription("dat description");
    at.setMandatory(true);
    at.setAttributeTypeGroupTwoWay(getAtg());
  }

  @Test
  public void testDateATSave() {
    commit();
    at = (DateAT) getAttributeTypeDAO().getAttributeTypeByName(TEST_NAME);
    assertNotNull(at);
    assertNotNull(at.getId());
    assertEquals(TEST_NAME, at.getName());
    assertEquals("dat description", at.getDescription());
    assertTrue(at.isMandatory());
  }

  @Test
  public void testDateATUpdateFrom() {
    BuildingBlockType adType = getBuildingBlockTypeDAO().getBuildingBlockTypeByType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN);
    at.addBuildingBlockTypeTwoWay(adType);
    getHibernateTemplate().save(at);

    commit();
    beginTransaction();
    // act:

    assertNotNull(at);
    at.setName("dat new");
    at.setDescription("dat description new");
    at.setAttributeTypeGroup(getAtg2());
    at.setMandatory(false);
    BuildingBlockType boType = getBuildingBlockTypeDAO().getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSOBJECT);
    at.setBuildingBlockTypes(new HashSet<BuildingBlockType>());
    at.addBuildingBlockTypeOneWay(boType);

    rollback();
    beginTransaction();

    attributeTypeService.saveOrUpdate(at);

    commit();

    // assert:
    assertNotNull(at);
    assertEquals("dat new", at.getName());
    assertEquals("dat description new", at.getDescription());
    assertEquals("atg2", at.getAttributeTypeGroup().getName());
    assertFalse(at.isMandatory());
    assertEquals(1, at.getBuildingBlockTypes().size());
    Set<BuildingBlockType> buildingBlockTypes = at.getBuildingBlockTypes();
    for (BuildingBlockType type : buildingBlockTypes) {
      if (!type.getTypeOfBuildingBlock().equals(TypeOfBuildingBlock.BUSINESSOBJECT)) {
        fail("wrong building block type.");
      }
    }
  }
}

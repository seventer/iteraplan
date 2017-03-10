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
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class NumberATTest extends AttributeTypeTestAbstract<NumberAT> {

  private static final String NAME        = "nat";
  private static final String DESCRIPTION = "nat description";

  private NumberAT            at;
  @Autowired
  private TestDataHelper2  testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());

    at = testDataHelper.createNumberAttributeType(NAME, DESCRIPTION, getAtg());
  }

  @Test
  public void testNumberATUpdateFrom() {
    at.setMandatory(true);
    at.setMinValue(null);
    at.setMaxValue(null);
    at.setUnit(null);
    BuildingBlockType adType = getBuildingBlockTypeDAO().getBuildingBlockTypeByType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN);
    at.addBuildingBlockTypeTwoWay(adType);
    getHibernateTemplate().save(at);

    commit();

    // act:
    beginTransaction();

    assert (at != null);
    at.setName("nat new");
    at.setDescription("nat description new");
    at.setAttributeTypeGroup(getAtg2());
    at.setMandatory(false);
    at.setMinValue(BigDecimal.ONE);
    at.setMaxValue(new BigDecimal("100"));
    at.setUnit("UNIT");
    BuildingBlockType boType = getBuildingBlockTypeDAO().getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSOBJECT);
    at.setBuildingBlockTypes(new HashSet<BuildingBlockType>());
    at.addBuildingBlockTypeOneWay(boType);

    rollback();
    beginTransaction();

    attributeTypeService.saveOrUpdate(at);
    commit();

    // assert:
    assertNotNull(at);
    assertEquals("nat new", at.getName());
    assertEquals("nat description new", at.getDescription());
    assertEquals("atg2", at.getAttributeTypeGroup().getName());
    assertFalse(at.isMandatory());
    assertEquals(BigDecimal.ONE, at.getMinValue());
    assertEquals(new BigDecimal("100"), at.getMaxValue());
    assertEquals("UNIT", at.getUnit());
    assertEquals(1, at.getBuildingBlockTypes().size());
    Set<BuildingBlockType> buildingBlockTypes = at.getBuildingBlockTypes();
    for (BuildingBlockType type : buildingBlockTypes) {
      if (!type.getTypeOfBuildingBlock().equals(TypeOfBuildingBlock.BUSINESSOBJECT)) {
        fail("wrong building block type.");
      }
    }
  }

  @Test
  public void testNumberATValidate() {
    at.setMinValue(null);
    at.setMaxValue(null);
    getHibernateTemplate().save(at);

    commit();

    // act & assert:
    beginTransaction();
    NumberAT validationAT = (NumberAT) getAttributeTypeDAO().getAttributeTypeByName(NAME);

    // check lower <= upper bound constraint
    validationAT.setMinValue(new BigDecimal("100"));
    validationAT.setMaxValue(BigDecimal.ONE);
    try {
      validationAT.validate();
      fail("validation should have failed...");
    } catch (IteraplanBusinessException bex) {
      // expected behaviour
    }
    validationAT.setMaxValue(null);
    validationAT.validate();

  }

  @Test
  public void testRangeFunctionality() {
    at.setRangeUniformyDistributed(false);

    RangeValue range = new RangeValue(at, BigDecimal.ONE);
    at.addRangeValueTwoWay(range);
    getHibernateTemplate().save(at);

    commit();

    // act & assert:
    beginTransaction();
    NumberAT validationAT = (NumberAT) getAttributeTypeDAO().getAttributeTypeByName(NAME);
    assertFalse(validationAT.isRangeUniformyDistributed());
    Set<RangeValue> ranges = validationAT.getRangeValues();

    RangeValue validationRange = ranges.iterator().next();
    assertEquals(validationRange, range);
  }
}

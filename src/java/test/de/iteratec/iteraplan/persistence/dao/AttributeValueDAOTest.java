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
package de.iteratec.iteraplan.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;


/**
 * Test for the attribute value DAO.
 * 
 * @author dro
 */
public class AttributeValueDAOTest extends BaseTransactionalTestSupport {
  private static final String TEST_DESCRIPTION = "testDescription";
  @Autowired
  private AttributeValueDAO   attributeValueDao;
  @Autowired
  private TestDataHelper2     testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testCheckForBuildingBlocksWithMoreThanOneEnumAVsCaseFalse() {
    Integer enumAttributeTypeId = Integer.valueOf(65);
    assertFalse(attributeValueDao.checkForBuildingBlocksWithMoreThanOneEnumAVs(enumAttributeTypeId));
  }

  @Test
  public void testDeleteOrphanedDateAttributeValues() {
    AttributeTypeGroup group = testDataHelper.createAttributeTypeGroup("testGroup", TEST_DESCRIPTION);
    DateAT at = testDataHelper.createDateAttributeType("name", TEST_DESCRIPTION, group);
    Date value = new Date();
    if (value != null) {
      testDataHelper.createDateAV(value, at);
    }
    commit();

    Integer actual = attributeValueDao.deleteOrphanedDateAttributeValues();
    Integer expected = Integer.valueOf(1);

    assertEquals(expected, actual);
  }

  @Test
  public void testDeleteOrphanedTextAttributeValues() {
    AttributeTypeGroup group = testDataHelper.createAttributeTypeGroup("testGroup", TEST_DESCRIPTION);
    TextAT at = testDataHelper.createTextAttributeType("name", TEST_DESCRIPTION, false, group);
    String value = "test";
    testDataHelper.createTextAV(value, at);
    commit();

    Integer actual = attributeValueDao.deleteOrphanedTextAttributeValues();
    Integer expected = Integer.valueOf(1);

    assertEquals(expected, actual);
  }

  @Test
  public void testDeleteOrphanedNumberAttributeValues() {
    AttributeTypeGroup group = testDataHelper.createAttributeTypeGroup("testGroup", TEST_DESCRIPTION);
    NumberAT at = testDataHelper.createNumberAttributeType("name", TEST_DESCRIPTION, group);
    BigDecimal value = new BigDecimal("567898765");
    testDataHelper.createNumberAV(value, at);
    commit();

    Integer actual = attributeValueDao.deleteOrphanedNumberAttributeValues();
    Integer expected = Integer.valueOf(1);

    assertEquals(expected, actual);
  }

  @Test
  public void testLoadObjectById() {
    TextAT at = createAttributeType(TextAT.class);
    TextAV firstAV = testDataHelper.createTextAV("firstTestAV", at);
    TextAV secondAV = testDataHelper.createTextAV("secondTestAV", at);
    TextAV thirdAV = testDataHelper.createTextAV("thirdTestAV", at);

    assertEquals(firstAV, attributeValueDao.loadObjectById(Integer.valueOf(2), TextAV.class));
    assertEquals(secondAV, attributeValueDao.loadObjectById(Integer.valueOf(3), TextAV.class));
    assertEquals(thirdAV, attributeValueDao.loadObjectById(Integer.valueOf(4), TextAV.class));

    assertNull(attributeValueDao.loadObjectById(null, TextAV.class));
    assertNull(attributeValueDao.loadObjectById(Integer.valueOf(-15), TextAV.class));
  }

  @SuppressWarnings("unchecked")
  private <T extends AttributeType> T createAttributeType(Class<T> clazz) {
    String name = "type";
    String description = "";
    AttributeTypeGroup group = testDataHelper.createAttributeTypeGroup("test", "test");

    if (clazz.isInstance(new DateAT())) {
      return (T) testDataHelper.createDateAttributeType(name, description, group);
    }
    if (clazz.isInstance(new EnumAT())) {
      return (T) testDataHelper.createEnumAttributeType(name, description, Boolean.FALSE, group);
    }
    if (clazz.isInstance(new NumberAT())) {
      return (T) testDataHelper.createNumberAttributeType(name, description, group);
    }
    if (clazz.isInstance(new ResponsibilityAT())) {
      return (T) testDataHelper.createResponsibilityAttributeType(name, description, Boolean.FALSE, group);
    }
    if (clazz.isInstance(new TextAT())) {
      return (T) testDataHelper.createTextAttributeType(name, description, false, group);
    }

    throw new IteraplanTechnicalException();
  }
}

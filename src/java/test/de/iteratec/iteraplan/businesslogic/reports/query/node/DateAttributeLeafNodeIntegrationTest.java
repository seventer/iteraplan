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
/**
 * 
 */
package de.iteratec.iteraplan.businesslogic.reports.query.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Iterables;

import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;

/**
 * Integration test for {@link DateAttributeLeafNode} class.
 */
public class DateAttributeLeafNodeIntegrationTest extends AbstractNodeTestBase {
  @Autowired
  private AttributeTypeGroupDAO attributeTypeGroupDAO;
  @Autowired
  private BuildingBlockTypeDAO  buildingBlockTypeDAO;
  @Autowired
  private TestDataHelper2  testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }
  
  @Test
  public void testDateEquals() {
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    DateAT dateAT = testDataHelper.createDateAttributeType("DateAT", "DateAT descr", group);    
    testDataHelper.assignAttributeTypeToBuildingBlockType(dateAT, bbType);
    
    createISRWithDate(new LocalDate(1982, 1, 25), dateAT);
    InformationSystemRelease release2 = createISRWithDate(new LocalDate(1982, 1, 26), dateAT);
    createISRWithDate(new LocalDate(1982, 1, 27), dateAT);
    
    commit();
    
    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    DateAttributeLeafNode dateAttributeLeafNode = DateAttributeLeafNode.createNode(type, null, 1, Comparator.EQ, "01/26/1982", Locale.ENGLISH);
    
    Set<BuildingBlock> result = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(dateAttributeLeafNode);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(release2, Iterables.getOnlyElement(result));
  }
  
  @Test
  public void testDateGT() {
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    DateAT dateAT = testDataHelper.createDateAttributeType("DateAT", "DateAT descr", group);    
    testDataHelper.assignAttributeTypeToBuildingBlockType(dateAT, bbType);
    
    createISRWithDate(new LocalDate(1982, 1, 25), dateAT);
    createISRWithDate(new LocalDate(1982, 1, 26), dateAT);
    InformationSystemRelease release3 = createISRWithDate(new LocalDate(1982, 1, 27), dateAT);
    
    commit();
    
    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    DateAttributeLeafNode dateAttributeLeafNode = DateAttributeLeafNode.createNode(type, null, 1, Comparator.GT, "01/26/1982", Locale.ENGLISH);
    
    Set<BuildingBlock> result = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(dateAttributeLeafNode);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(release3, Iterables.getOnlyElement(result));
  }
  @Test
  public void testDateLT() {
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    DateAT dateAT = testDataHelper.createDateAttributeType("DateAT", "DateAT descr", group);    
    testDataHelper.assignAttributeTypeToBuildingBlockType(dateAT, bbType);
    
    InformationSystemRelease release1 = createISRWithDate(new LocalDate(1982, 1, 25), dateAT);
    createISRWithDate(new LocalDate(1982, 1, 26), dateAT);
    createISRWithDate(new LocalDate(1982, 1, 27), dateAT);
    
    commit();
    
    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    DateAttributeLeafNode dateAttributeLeafNode = DateAttributeLeafNode.createNode(type, null, 1, Comparator.LT, "01/26/1982", Locale.ENGLISH);
    
    Set<BuildingBlock> result = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(dateAttributeLeafNode);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(release1, Iterables.getOnlyElement(result));
  }
  @Test
  public void testDateAnyAssignment() {
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    DateAT dateAT = testDataHelper.createDateAttributeType("DateAT", "DateAT descr", group);    
    testDataHelper.assignAttributeTypeToBuildingBlockType(dateAT, bbType);
    
    InformationSystemRelease release1 = createISRWithDate(new LocalDate(1982, 1, 25), dateAT);
    InformationSystemRelease release2 = createISRWithDate(new LocalDate(1982, 1, 26), dateAT);
    InformationSystemRelease release3 = createISRWithDate(new LocalDate(1982, 1, 27), dateAT);
    
    commit();
    
    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    DateAttributeLeafNode dateAttributeLeafNode = DateAttributeLeafNode.createNode(type, null, 1, Comparator.ANY_ASSIGNMENT, "01/26/1982", Locale.ENGLISH);
    
    Set<BuildingBlock> result = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(dateAttributeLeafNode);
    assertNotNull(result);
    assertEquals(3, result.size());
    assertTrue(result.contains(release1));
    assertTrue(result.contains(release2));
    assertTrue(result.contains(release3));
  }

  private InformationSystemRelease createISRWithDate(LocalDate date, DateAT dateAT) {
    InformationSystem informationSystem = testDataHelper.createInformationSystem(RandomStringUtils.random(5));
    InformationSystemRelease release1 = testDataHelper.createInformationSystemRelease(informationSystem, RandomStringUtils.random(5));
    
    if (date.toDateMidnight().toDate() != null) {
      DateAV enumAV11 = testDataHelper.createDateAV(date.toDateMidnight().toDate(), dateAT);
      testDataHelper.createAVA(release1, enumAV11);
    }
    return release1;
  }
}

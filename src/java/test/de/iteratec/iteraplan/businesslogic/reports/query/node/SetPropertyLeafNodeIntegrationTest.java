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

import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

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
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * Integration test for {@link SetPropertyLeafNodeIntegration} class.
 *
 */
public class SetPropertyLeafNodeIntegrationTest extends AbstractNodeTestBase {
  @Autowired
  private AttributeTypeGroupDAO attributeTypeGroupDAO;
  @Autowired
  private BuildingBlockTypeDAO  buildingBlockTypeDAO;
  @Autowired
  private TestDataHelper2       testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.reports.query.node.SetPropertyLeafNode#getAlias()}.
   */
  @Test
  public void testAbonementsLike() {
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    ResponsibilityAT respAT = testDataHelper.createResponsibilityAttributeType("ResponsibilityAT", "descr", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(respAT, bbType);
    User user1 = testDataHelper.createUser("user1", "user1", "user1", "MASTER");
    User user2 = testDataHelper.createUser("user2", "user2", "user2", "MASTER");
    User user3 = testDataHelper.createUser("user3", "user3", "user3", "MASTER");

    createISRWithAbonement(user1);
    createISRWithAbonement(user2);
    createISRWithAbonement(user3);
    commit();

    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    SetPropertyLeafNode setPropertyLeafNode = new SetPropertyLeafNode(type, null, "subscribedUsers", Comparator.LIKE, "*user*");

    Set<BuildingBlock> result = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(setPropertyLeafNode);
    assertNotNull(result);
    assertEquals(3, result.size());
  }

  @Test
  public void testAbonementsNotLike() {
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    ResponsibilityAT respAT = testDataHelper.createResponsibilityAttributeType("ResponsibilityAT", "descr", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(respAT, bbType);
    User user1 = testDataHelper.createUser("user1", "user1", "user1", "MASTER");
    User user2 = testDataHelper.createUser("user2", "user2", "user2", "MASTER");
    User user3 = testDataHelper.createUser("user3", "user3", "user3", "MASTER");

    createISRWithAbonement(user1);
    InformationSystemRelease release2 = createISRWithAbonement(user2);
    InformationSystemRelease release3 = createISRWithAbonement(user3);
    commit();

    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    SetPropertyLeafNode setPropertyLeafNode = new SetPropertyLeafNode(type, null, "subscribedUsers", Comparator.NOT_LIKE, "user1");

    Set<BuildingBlock> result = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(setPropertyLeafNode);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains(release2));
    assertTrue(result.contains(release3));
  }

  private InformationSystemRelease createISRWithAbonement(User user) {
    InformationSystem informationSystem = testDataHelper.createInformationSystem(RandomStringUtils.random(5));
    InformationSystemRelease release1 = testDataHelper.createInformationSystemRelease(informationSystem, RandomStringUtils.random(5));
    release1.setSubscribedUsers(Sets.newHashSet(user));

    return release1;
  }
}

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
package de.iteratec.iteraplan.businesslogic.reports.query.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Date;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Iterables;

import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Seal;
import de.iteratec.iteraplan.model.SealState;

/**
 * Integration test for the {@link SealLeafNode} class. 
 */
public class SealLeafNodeTest extends AbstractNodeTestBase {

  /** Seal state field name. */
  private static final String SEAL_STATE = "state";
  @Autowired
  private TestDataHelper2  testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }
  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.reports.query.node.SealLeafNode#isToBeRemoved(java.lang.Object)}.
   */
  @Test
  public void testIsToBeRemoved() {
    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    SealLeafNode leaf = new SealLeafNode(type, null, SEAL_STATE, SealState.INVALID);
    
    assertFalse(leaf.isToBeRemoved(null));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.reports.query.node.SealLeafNode#getWhereCriteria(org.hibernate.criterion.DetachedCriteria)}.
   */
  @Test
  public void testGetWhereCriteriaCaseInvalid() {
    prepareData();
     
    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    SealLeafNode leaf = new SealLeafNode(type, null, SEAL_STATE, SealState.INVALID);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leaf);
    assertEquals(1, results.size());
    assertEquals("I # i1", Iterables.getOnlyElement(results).getNonHierarchicalName());
  }
  
  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.reports.query.node.SealLeafNode#getWhereCriteria(org.hibernate.criterion.DetachedCriteria)}.
   */
  @Test
  public void testGetWhereCriteriaCaseNotAvailable() {
    prepareData();
     
    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    SealLeafNode leaf = new SealLeafNode(type, null, SEAL_STATE, SealState.NOT_AVAILABLE);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leaf);
    assertEquals(1, results.size());
    assertEquals("I # i2", Iterables.getOnlyElement(results).getNonHierarchicalName());
  }
  
  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.reports.query.node.SealLeafNode#getWhereCriteria(org.hibernate.criterion.DetachedCriteria)}.
   */
  @Test
  public void testGetWhereCriteriaCaseValid() {
    prepareData();
     
    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    SealLeafNode leaf = new SealLeafNode(type, null, SEAL_STATE, SealState.VALID);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leaf);
    assertEquals(1, results.size());
    assertEquals("I # i3", Iterables.getOnlyElement(results).getNonHierarchicalName());
  }
  
  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.reports.query.node.SealLeafNode#getWhereCriteria(org.hibernate.criterion.DetachedCriteria)}.
   */
  @Test
  public void testGetWhereCriteriaCaseOutdated() {
    prepareData();
     
    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    SealLeafNode leaf = new SealLeafNode(type, null, SEAL_STATE, SealState.OUTDATED);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leaf);
    assertEquals(1, results.size());
    assertEquals("I # i4", Iterables.getOnlyElement(results).getNonHierarchicalName());
  }

  /**
   * Creates the {@link InformationSystemRelease} entities with {@link SealState} set. A special case for outdated
   * information system is also created and saved.
   */
  private void prepareData() {
    InformationSystem informationSystem = testDataHelper.createInformationSystem("I");
    InformationSystemRelease release1 = testDataHelper.createInformationSystemRelease(informationSystem, "i1");
    release1.setSealState(SealState.INVALID);
    InformationSystemRelease release2 = testDataHelper.createInformationSystemRelease(informationSystem, "i2");
    release2.setSealState(SealState.NOT_AVAILABLE);
    InformationSystemRelease release3 = testDataHelper.createInformationSystemRelease(informationSystem, "i3");
    release3.setSealState(SealState.VALID);
    
    Seal seal = new Seal();
    seal.setUser("user");
    seal.setComment("comment");
    
    int expirationInDays = IteraplanProperties.getIntProperty(IteraplanProperties.SEAL_EXPIRATION_DAYS);
    Date minusDays = new DateTime().minusDays(expirationInDays+1).toDate();
    seal.setDate(minusDays);
    
    InformationSystemRelease release4 = testDataHelper.createInformationSystemRelease(informationSystem, "i4");
    release4.setSealState(SealState.VALID);
    release4.addSeal(seal);

    commit();
  }
}

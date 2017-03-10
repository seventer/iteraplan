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
package de.iteratec.iteraplan.businesslogic.service.History;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.BusinessUnitService;
import de.iteratec.iteraplan.businesslogic.service.HistoryService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.diffs.model.BusinessUnitChangeset;
import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;
import de.iteratec.iteraplan.diffs.model.InformationSystemReleaseChangeset;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.presentation.dialog.History.HistoryResultsPage;

public class HierarchyModificationTest extends BaseTransactionalTestSupport {
  private boolean                wasHistoryEnabledBeforeTest = false;

  @Autowired
  private HistoryService         historyService;
  @Autowired
  private BusinessUnitService    businessUnitService;
  @Autowired
  private InformationSystemReleaseService isrService;
  @Autowired
  private TestDataHelper2        testDataHelper;

  private BusinessUnit bu1;
  private BusinessUnit bu2;

  private BusinessUnit buRoot;

  private InformationSystemRelease isr1;

  private InformationSystemRelease isr2;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    wasHistoryEnabledBeforeTest = historyService.isHistoryEnabled();
    historyService.setHistoryEnabled(true);

    bu1 = testDataHelper.createBusinessUnit("BU1", "");
    bu2 = testDataHelper.createBusinessUnit("BU2", "");

    InformationSystem is = testDataHelper.createInformationSystem("MyIS");
    isr1 = testDataHelper.createInformationSystemRelease(is, "1.0", "descr", "", "", TypeOfStatus.CURRENT);

    InformationSystem is2 = testDataHelper.createInformationSystem("AnotherIS");
    isr2 = testDataHelper.createInformationSystemRelease(is2, "1.2", "descr", "", "", TypeOfStatus.CURRENT);

    buRoot = businessUnitService.getFirstElement();

    commit();
  }

  /**
   * Restores the original state of the auditing mechanism, in oder not to interfere with other tests.
   */
  @Override
  @After
  public void onTearDown() {
    super.onTearDown();
    historyService.setHistoryEnabled(wasHistoryEnabledBeforeTest);
  }

  @Test
  public void testAddParentBU() throws Exception {
    beginTransaction();
    // create modifications
    BusinessUnit bu1Reloaded = businessUnitService.loadObjectById(bu1.getId());
    BusinessUnit bu2Reloaded = businessUnitService.loadObjectById(bu2.getId());

    bu1Reloaded.addParent(bu2Reloaded);

    businessUnitService.saveOrUpdate(bu1Reloaded);
    commit();

    beginTransaction();

    // verify history of BU1
    BusinessUnitChangeset bu1Change = (BusinessUnitChangeset) getLatestChangesetFor(BusinessUnit.class, bu1.getId());
    assertFalse(bu1Change.isAttributesChanged());
    assertFalse(bu1Change.isChildrenChanged());

    assertTrue(bu1Change.isParentChanged());
    assertEquals("name shouldn't have changed", bu1Change.getNameFrom(), bu1Change.getNameTo());

    assertEquals(buRoot, bu1Change.getParentFrom());
    assertEquals("-", bu1Change.getParentFromName());

    assertEquals(bu2, bu1Change.getParentTo());
    assertEquals(bu2.getNonHierarchicalName(), bu1Change.getParentToName());

    // verify history of BU2
    BusinessUnitChangeset bu2Change = (BusinessUnitChangeset) getLatestChangesetFor(BusinessUnit.class, bu2.getId());
    assertFalse(bu2Change.isAttributesChanged());
    assertTrue(bu2Change.isChildrenChanged());
    assertFalse(bu2Change.isParentChanged());

    List<BuildingBlock> childrenAdded = bu2Change.getChildrenAdded();
    assertEquals(1, childrenAdded.size());
    assertEquals(bu1, childrenAdded.get(0));

    List<String> childrenAddedNames = bu2Change.getChildrenAddedNames();
    assertEquals(1, childrenAddedNames.size());
    assertEquals(bu1.getNonHierarchicalName(), childrenAddedNames.get(0));

    assertTrue(bu2Change.getChildrenRemoved().isEmpty());

    // verify history of BU root
    HistoryBBChangeset buRootC = getLatestChangesetFor(BusinessUnit.class, buRoot.getId());
    BusinessUnitChangeset buRootChange = (BusinessUnitChangeset) buRootC;
    assertFalse(buRootChange.isAttributesChanged());
    assertTrue(buRootChange.isChildrenChanged());
    assertFalse(buRootChange.isParentChanged());

    List<BuildingBlock> childrenRemoved = buRootChange.getChildrenRemoved();
    assertEquals(1, childrenRemoved.size());
    assertEquals(bu1, childrenRemoved.get(0));

    List<String> childrenRemovedNames = buRootChange.getChildrenRemovedNames();
    assertEquals(1, childrenRemovedNames.size());
    assertEquals(bu1.getNonHierarchicalName(), childrenRemovedNames.get(0));

    assertTrue(buRootChange.getChildrenAdded().isEmpty());

  }

  @Test
  public void testAddChildBU() throws Exception {
    beginTransaction();
    // create modifications
    BusinessUnit bu1Reloaded = businessUnitService.loadObjectById(bu1.getId());
    BusinessUnit bu2Reloaded = businessUnitService.loadObjectById(bu2.getId());

    bu2Reloaded.addChildren(Sets.newHashSet(bu1Reloaded));

    businessUnitService.saveOrUpdate(bu2Reloaded);
    commit();

    beginTransaction();

    // verify history of BU2
    HistoryBBChangeset bu2c = getLatestChangesetFor(BusinessUnit.class, bu2.getId());
    BusinessUnitChangeset bu2Change = (BusinessUnitChangeset) bu2c;
    assertTrue(bu2Change.isChildrenChanged());
    assertFalse(bu2Change.isParentChanged());
    assertFalse(bu2Change.isAttributesChanged());

    List<BuildingBlock> childrenAdded = bu2Change.getChildrenAdded();
    assertEquals(1, childrenAdded.size());
    assertEquals(bu1, childrenAdded.get(0));

    List<String> childrenAddedNames = bu2Change.getChildrenAddedNames();
    assertEquals(1, childrenAddedNames.size());
    assertEquals(bu1.getNonHierarchicalName(), childrenAddedNames.get(0));

    assertTrue(bu2Change.getChildrenRemoved().isEmpty());

    // verify history of BU1
    BusinessUnitChangeset bu1Changes = (BusinessUnitChangeset) getLatestChangesetFor(BusinessUnit.class, bu1.getId());
    assertFalse(bu1Changes.isAttributesChanged());
    assertFalse(bu1Changes.isChildrenChanged());
    assertTrue(bu1Changes.isParentChanged());

    assertEquals("name shouldn't have changed", bu1Changes.getNameFrom(), bu1Changes.getNameTo());

    assertEquals(buRoot, bu1Changes.getParentFrom());
    assertEquals("-", bu1Changes.getParentFromName());

    assertEquals(bu2, bu1Changes.getParentTo());
    assertEquals(bu2.getNonHierarchicalName(), bu1Changes.getParentToName());

    // verify history of BU root
    BusinessUnitChangeset buRootChange = (BusinessUnitChangeset) getLatestChangesetFor(BusinessUnit.class, buRoot.getId());
    assertFalse(buRootChange.isAttributesChanged());
    assertTrue(buRootChange.isChildrenChanged());
    assertFalse(buRootChange.isParentChanged());

    List<BuildingBlock> childrenRemoved = buRootChange.getChildrenRemoved();
    assertEquals(1, childrenRemoved.size());
    assertEquals(bu1, childrenRemoved.get(0));

    List<String> childrenRemovedNames = buRootChange.getChildrenRemovedNames();
    assertEquals(1, childrenRemovedNames.size());
    assertEquals(bu1.getNonHierarchicalName(), childrenRemovedNames.get(0));

    assertTrue(buRootChange.getChildrenAdded().isEmpty());
  }

  @Test
  public void testChangeParentHierarchyBU() throws Exception {
    beginTransaction();
    // create initial hierarchy: bu1 : bu2 : bu3
    BusinessUnit bu1Reloaded = businessUnitService.loadObjectById(bu1.getId());
    BusinessUnit bu2Reloaded = businessUnitService.loadObjectById(bu2.getId());
    BusinessUnit bu3 = testDataHelper.createBusinessUnit("BU3", "");

    bu2.addParent(bu1Reloaded);
    bu2Reloaded.addChildren(Sets.newHashSet(bu3));

    businessUnitService.saveOrUpdate(bu2Reloaded);
    commit();

    // create modification: switch bu2 to top-level element and check for non-changes in bu3
    beginTransaction();
    bu2Reloaded = businessUnitService.loadObjectById(bu2.getId());
    BusinessUnit buRootReloaded = businessUnitService.loadObjectById(buRoot.getId());
    buRootReloaded.addChildren(Sets.newHashSet(bu2Reloaded));

    commit();

    beginTransaction();

    // verify history of bu3
    BusinessUnitChangeset bu3Changes = (BusinessUnitChangeset) getLatestChangesetFor(BusinessUnit.class, bu3.getId());
    assertFalse(bu3Changes.isAttributesChanged());

    assertFalse(bu3Changes.isParentChanged());
    assertFalse(bu3Changes.isChildrenChanged());
    assertEquals(bu3Changes.getParentFrom(), bu3Changes.getParentTo());
    assertEquals(bu3Changes.getNameFrom(), bu3Changes.getNameTo());

    // verify history of bu2
    BusinessUnitChangeset bu2Changes = (BusinessUnitChangeset) getLatestChangesetFor(BusinessUnit.class, bu2.getId());
    assertFalse(bu2Changes.isChildrenChanged());
    assertTrue(bu2Changes.isParentChanged());

    assertEquals(bu1, bu2Changes.getParentFrom());
    assertEquals(bu1.getNonHierarchicalName(), bu2Changes.getParentFromName());

    assertEquals(buRoot, bu2Changes.getParentTo());
    assertEquals("-", bu2Changes.getParentToName());
  }

  @Test
  public void testAddChildISR() throws Exception {
    beginTransaction();
    InformationSystemRelease isr1Reloaded = isrService.loadObjectById(isr1.getId());
    InformationSystemRelease isr2Reloaded = isrService.loadObjectById(isr2.getId());

    isr1Reloaded.addChild(isr2Reloaded);
    isrService.saveOrUpdate(isr1Reloaded);
    commit();

    beginTransaction();

    // verify history of isr1
    InformationSystemReleaseChangeset isr1Changes = (InformationSystemReleaseChangeset) getLatestChangesetFor(InformationSystemRelease.class, isr1.getId());
    assertFalse(isr1Changes.isParentChanged());
    assertTrue(isr1Changes.isChildrenChanged());
    assertFalse(isr1Changes.isAttributesChanged());

    List<InformationSystemRelease> childrenAdded = isr1Changes.getChildrenAdded();
    assertEquals(1, childrenAdded.size());
    assertEquals(isr2, childrenAdded.get(0));
    assertNull(isr1Changes.getParentFrom());
    assertNull(isr1Changes.getParentTo());

    List<String> childrenAddedNames = isr1Changes.getChildrenAddedNames();
    assertEquals(1, childrenAddedNames.size());
    assertEquals(isr2.getNonHierarchicalName(), childrenAddedNames.get(0));

    assertTrue(isr1Changes.getChildrenRemoved().isEmpty());

    // verify history of isr2
    InformationSystemReleaseChangeset isr2Changes = (InformationSystemReleaseChangeset) getLatestChangesetFor(InformationSystemRelease.class, isr2.getId());
    assertFalse(isr2Changes.isAttributesChanged());
    assertFalse(isr2Changes.isChildrenChanged());
    assertTrue(isr2Changes.isParentChanged());

    assertEquals("name shouldn't have changed", isr2Changes.getNameFrom(), isr2Changes.getNameTo());

    assertNull(isr2Changes.getParentFrom());

    assertEquals(isr1, isr2Changes.getParentTo());
    assertEquals(isr1.getNonHierarchicalName(), isr2Changes.getParentToName());

  }

  @Test
  public void testAddParentISR() throws Exception {
    beginTransaction();
    InformationSystemRelease isr1Reloaded = isrService.loadObjectById(isr1.getId());
    InformationSystemRelease isr2Reloaded = isrService.loadObjectById(isr2.getId());

    isr2Reloaded.addParent(isr1Reloaded);
    isrService.saveOrUpdate(isr2Reloaded);
    commit();

    beginTransaction();

    // verify history of isr1
    InformationSystemReleaseChangeset isr1Changes = (InformationSystemReleaseChangeset) getLatestChangesetFor(InformationSystemRelease.class, isr1.getId());
    assertFalse(isr1Changes.isParentChanged());
    assertTrue(isr1Changes.isChildrenChanged());
    assertFalse(isr1Changes.isAttributesChanged());

    List<InformationSystemRelease> childrenAdded = isr1Changes.getChildrenAdded();
    assertEquals(1, childrenAdded.size());
    assertEquals(isr2, childrenAdded.get(0));
    assertNull(isr1Changes.getParentFrom());
    assertNull(isr1Changes.getParentTo());

    List<String> childrenAddedNames = isr1Changes.getChildrenAddedNames();
    assertEquals(1, childrenAddedNames.size());
    assertEquals(isr2.getNonHierarchicalName(), childrenAddedNames.get(0));

    assertTrue(isr1Changes.getChildrenRemoved().isEmpty());

    // verify history of isr2
    InformationSystemReleaseChangeset isr2Changes = (InformationSystemReleaseChangeset) getLatestChangesetFor(InformationSystemRelease.class, isr2.getId());
    assertFalse(isr2Changes.isAttributesChanged());
    assertFalse(isr2Changes.isChildrenChanged());
    assertTrue(isr2Changes.isParentChanged());

    assertEquals("name shouldn't have changed", isr2Changes.getNameFrom(), isr2Changes.getNameTo());

    assertNull(isr2Changes.getParentFrom());

    assertEquals(isr1, isr2Changes.getParentTo());
    assertEquals(isr1.getNonHierarchicalName(), isr2Changes.getParentToName());

  }

  @SuppressWarnings("boxing")
  private HistoryBBChangeset getLatestChangesetFor(Class<? extends BuildingBlock> bbClass, Integer bbId) {
    HistoryResultsPage page = historyService.getLocalHistoryPage(bbClass, bbId, 0, -1, null, null);
    List<HistoryBBChangeset> changesets = page.getBbChangesets();
    // latest changes appear first, so we need to take element 0
    return changesets.get(0);
  }

}

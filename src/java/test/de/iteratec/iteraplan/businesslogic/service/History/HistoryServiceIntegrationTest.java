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

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.BusinessUnitService;
import de.iteratec.iteraplan.businesslogic.service.HistoryService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.presentation.dialog.History.HistoryResultsPage;


/**
 * Integration Tests of the History Service and DAO
 *
 */
public class HistoryServiceIntegrationTest extends BaseTransactionalTestSupport {
  private boolean             wasHistoryEnabledBeforeTest = false;

  @Autowired
  private HistoryService      historyService;
  @Autowired
  private BusinessUnitService buService;
  @Autowired
  private TestDataHelper2     testDataHelper;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());

    wasHistoryEnabledBeforeTest = historyService.isHistoryEnabled();
    historyService.setHistoryEnabled(true);
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

  /**
   * Test History reading of an object that was modified but never created
   */
  @SuppressWarnings("boxing")
  @Test
  public void testHistoryHoles() {

    historyService.setHistoryEnabled(false);

    ////////////////////////////////////////
    // Create BU, no history

    // Create vA and save
    BusinessUnit bu = testDataHelper.createBusinessUnit("bbTestBU", "A");
    bu = buService.saveOrUpdate(bu);

    commit();
    beginTransaction();

    // Get current
    BusinessUnit buReloaded = buService.loadObjectByIdIfExists(bu.getId());

    ////////////////////////////////////////
    // Update BU, with History, twice

    historyService.setHistoryEnabled(true);

    // Update to vB
    buReloaded.setDescription("B");
    buService.saveOrUpdate(buReloaded);

    commit();
    beginTransaction();
    buReloaded = buService.loadObjectByIdIfExists(bu.getId());

    //Update to vC
    buReloaded.setDescription("C");
    buService.saveOrUpdate(buReloaded);

    commit();
    beginTransaction();

    ////////////////////////////////////////
    // Retrieve history
    ////////////////////////////////////////

    HistoryResultsPage hrp = historyService.getLocalHistoryPage(BusinessUnit.class, buReloaded.getId(), 0, -1, null, null);

    // Only 2, since history was off for 1st rev
    assertEquals(2, hrp.getNumberResults());

    // always sorted with the newest changes first, so initial is last
    HistoryBBChangeset changesetVerOne = hrp.getBbChangesets().get(1);
    HistoryBBChangeset changesetVerTwo = hrp.getBbChangesets().get(0);

    assertEquals(true, changesetVerOne.isInitialChangeset());

    // check the newest changeset was from B to C, and the one before that was init
    // history of changing from A to B was not kept
    assertEquals(false, changesetVerTwo.isInitialChangeset());
    assertEquals(true, changesetVerTwo.isDescriptionChanged());
    assertEquals("B", changesetVerTwo.getDescriptionFrom());
    assertEquals("C", changesetVerTwo.getDescriptionTo());

  }

  @SuppressWarnings("boxing")
  @Test
  public void testHistoryDelRevsIgnored() {
    ////////////////////////////////////////
    // Create history

    // Create and save
    BusinessUnit bu = testDataHelper.createBusinessUnit("ccTestBU", "");
    bu = buService.saveOrUpdate(bu);

    commit();
    beginTransaction();

    // Delete Entity
    Integer buId = bu.getId();
    buService.deleteEntity(bu);

    commit();
    beginTransaction();

    ////////////////////////////////////////
    // Retrieve history
    ////////////////////////////////////////

    HistoryResultsPage hrp = historyService.getLocalHistoryPage(BusinessUnit.class, buId, 0, -1, null, null);

    // Only initial changeset, no history of it having been deleted
    assertEquals(1, hrp.getNumberResults());
  }

  @SuppressWarnings("boxing")
  @Test
  public void testHistoryOneBBTwoRevs() {
    ////////////////////////////////////////
    // Create history

    // Create v1 and save
    BusinessUnit bu = testDataHelper.createBusinessUnit("aaTestBU", "1");
    bu = buService.saveOrUpdate(bu);

    commit();
    try {
      // make sure both revisions have clearly different timestamps
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      // do nothing
    }
    beginTransaction();

    // Get current state from DB
    BusinessUnit buReloaded = buService.loadObjectByIdIfExists(bu.getId());

    // Update business unit to v2
    buReloaded.setDescription("2");
    buService.saveOrUpdate(buReloaded);

    commit();
    beginTransaction();

    ////////////////////////////////////////
    // Retrieve history - full date range, specified
    ////////////////////////////////////////

    // Use date params, infinite, and also dates that actually limit the results
    DateTime beginningOfTime = DateTime.parse("1990-01-01"); // way past

    DateTime now = new DateTime();

    HistoryResultsPage hrp = historyService.getLocalHistoryPage(BusinessUnit.class, buReloaded.getId(), 0, -1, beginningOfTime, now);

    // expect to get v1 (initial creation) and v2 (description update)
    assertEquals(2, hrp.getNumberResults());
    assertEquals(1, hrp.getFirstShownResult());
    assertEquals(2, hrp.getLastShownResult());

    // always sorted with the newest changes first, so initial is last
    HistoryBBChangeset changesetVerOne = hrp.getBbChangesets().get(1);
    HistoryBBChangeset changesetVerTwo = hrp.getBbChangesets().get(0);

    assertEquals("system", changesetVerOne.getAuthor());
    assertEquals(true, changesetVerOne.isInitialChangeset());

    assertEquals("system", changesetVerTwo.getAuthor());
    assertEquals(false, changesetVerTwo.isInitialChangeset());
    assertEquals(false, changesetVerTwo.isNameChanged());
    assertEquals(true, changesetVerTwo.isDescriptionChanged());
    assertEquals("1", changesetVerTwo.getDescriptionFrom());
    assertEquals("2", changesetVerTwo.getDescriptionTo());

    ////////////////////////////////////////
    // Retrieve history - date range limited to date of initial rev
    ////////////////////////////////////////

    DateTime firstRevDate = changesetVerOne.getTimestamp();
    HistoryResultsPage hrpDateLimited = historyService.getLocalHistoryPage(BusinessUnit.class, buReloaded.getId(), 0, -1, beginningOfTime,
        firstRevDate);

    // date constraint should filter out v2
    assertEquals(1, hrpDateLimited.getNumberResults());

    ////////////////////////////////////////
    // Retrieve history - page size = 1, no date limitation
    ////////////////////////////////////////

    HistoryResultsPage hrpSize1 = historyService.getLocalHistoryPage(BusinessUnit.class, buReloaded.getId(), 0, 1, null, null);

    assertEquals(2, hrpSize1.getNumberResults());
    assertEquals(0, hrpSize1.getCurPage());
    assertEquals(1, hrpSize1.getFirstShownResult());
    assertEquals(1, hrpSize1.getLastShownResult());

  }

}

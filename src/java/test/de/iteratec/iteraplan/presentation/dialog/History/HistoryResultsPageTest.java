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
package de.iteratec.iteraplan.presentation.dialog.History;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the HistoryResultsPage class
 */
public class HistoryResultsPageTest {

  @Test
  public void historyResultsPageCalculations160Hits50perPagePage0() {
    // Test 160 results, with 50 per page, so 4 pages
    HistoryResultsPage results = new HistoryResultsPage(null, 160, 50, 0);
    Assert.assertEquals(1, results.getCurPage1BasedIndex());
    Assert.assertEquals(4, results.getPages());
    Assert.assertFalse(results.isLastPage());
    Assert.assertEquals(1, results.getFirstShownResult());
    Assert.assertEquals(50, results.getLastShownResult());
  }

  @Test
  public void historyResultsPageCalculations160Hits50perPagePage1() {
    HistoryResultsPage results = new HistoryResultsPage(null, 160, 50, 1);
    Assert.assertEquals(2, results.getCurPage1BasedIndex());
    Assert.assertEquals(4, results.getPages());
    Assert.assertFalse(results.isLastPage());
    Assert.assertEquals(51, results.getFirstShownResult());
    Assert.assertEquals(100, results.getLastShownResult());
  }

  @Test
  public void historyResultsPageCalculations160Hits50perPagePage2() {
    HistoryResultsPage results = new HistoryResultsPage(null, 160, 50, 2);
    Assert.assertEquals(3, results.getCurPage1BasedIndex());
    Assert.assertEquals(4, results.getPages());
    Assert.assertFalse(results.isLastPage());
    Assert.assertEquals(101, results.getFirstShownResult());
    Assert.assertEquals(150, results.getLastShownResult());

  }

  @Test
  public void historyResultsPageCalculations160Hits50perPagePage3() {
    HistoryResultsPage results = new HistoryResultsPage(null, 160, 50, 3);
    Assert.assertEquals(4, results.getCurPage1BasedIndex());
    Assert.assertEquals(4, results.getPages());
    Assert.assertTrue(results.isLastPage());
    Assert.assertEquals(151, results.getFirstShownResult());
    Assert.assertEquals(160, results.getLastShownResult());
  }

  @Test
  public void historyResultsPageCalculations30Hits50perPagePage0() {
    // Test 30 results, 50 per page
    HistoryResultsPage results = new HistoryResultsPage(null, 30, 50, 0);
    Assert.assertEquals(1, results.getCurPage1BasedIndex());
    Assert.assertEquals(1, results.getPages());
    Assert.assertTrue(results.isLastPage());
    Assert.assertEquals(1, results.getFirstShownResult());
    Assert.assertEquals(30, results.getLastShownResult());
  }

  @Test
  public void historyResultsPageCalculations200HitsOnOnePage() {
    // Test 200 results, show all on one page
    HistoryResultsPage results = new HistoryResultsPage(null, 200, -1, 0);
    Assert.assertEquals(1, results.getCurPage1BasedIndex());
    Assert.assertEquals(1, results.getPages());
    Assert.assertTrue(results.isLastPage());
    Assert.assertEquals(1, results.getFirstShownResult());
    Assert.assertEquals(200, results.getLastShownResult());
  }

}

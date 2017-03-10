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

import java.util.List;

import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;

/**
 * Represents a single page of history results, but also knows how many total results
 * there were, as well as which page it is
 */
public class HistoryResultsPage {

  private List<HistoryBBChangeset> bbChangesets;
  private long                     numberResults;
  private int                      resultsPerPage;
  private int                      curPage;

  /**
   * 
   * @param bbChangesets the changesets of the current page
   * @param numberResults total number of results
   * @param resultsPerPage -1 means show all, 0 not allowed
   * @param curPage 0-based index
   */
  public HistoryResultsPage(List<HistoryBBChangeset> bbChangesets, long numberResults, int resultsPerPage, int curPage) {

    Preconditions.checkArgument(resultsPerPage != 0);
    Preconditions.checkArgument(resultsPerPage >= -1);
    Preconditions.checkArgument(numberResults >= 0);
    Preconditions.checkArgument(curPage >= 0);

    this.bbChangesets = bbChangesets;
    this.numberResults = numberResults;
    this.curPage = curPage;
    this.resultsPerPage = resultsPerPage;
  }

  public List<HistoryBBChangeset> getBbChangesets() {
    return bbChangesets;
  }

  public long getNumberResults() {
    return numberResults;
  }

  /**
   * @return 0-based index
   */
  public int getCurPage() {
    return curPage;
  }

  /**
   * @return 1-based index
   */
  public int getCurPage1BasedIndex() {
    return curPage + 1;
  }

  public int getResultsPerPage() {
    return resultsPerPage;
  }

  public int getPages() {
    // -1 resultsPerPage means all
    if (resultsPerPage == -1) {
      return 1;
    }
    return (int) Math.ceil( (double) numberResults / resultsPerPage);
  }

  public boolean isLastPage() {
    return getCurPage1BasedIndex() == getPages();
  }

  /**
   * @return 1-based index, ready for displaying to user
   */
  public int getFirstShownResult() {
    // -1 resultsPerPage means all
    if (resultsPerPage == -1) {
      return 1;
    }
    return curPage * resultsPerPage + 1;
  }

  /**
   * @return 1-based index, ready for displaying to user
   */
  public long getLastShownResult() {
    // -1 resultsPerPage means all
    if (resultsPerPage == -1) {
      return getNumberResults();
    }
    return Math.min(numberResults, ((long) curPage + 1) * resultsPerPage);
  }

  @Override
  public String toString() {
    return "HistoryResultsPage with " + numberResults + " results, resultsPerPage=" + resultsPerPage
        + ", curPage=" + curPage + ", [bbChangesets=" + bbChangesets+ "]";
  }
}

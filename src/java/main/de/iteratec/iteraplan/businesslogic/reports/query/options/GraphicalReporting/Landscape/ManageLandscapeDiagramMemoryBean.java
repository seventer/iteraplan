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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape;

import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ReportMemBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.common.util.CollectionUtils;


public class ManageLandscapeDiagramMemoryBean extends ReportMemBean {

  private static final long              serialVersionUID = -3280461167656430182L;

  private boolean                        reportUpdated;

  /**
   * Map holding the {@link QueryResult}s of the report
   */
  private final Map<String, QueryResult> queryResults     = CollectionUtils.hashMap();

  /**
   * Holds the query name for the currently active query.
   * Possible values can be queried from the {@link GraphicalExportBaseOptions#getQueryResultNames() OptionsBean}.
   */
  private String                         queryResultName;

  private LandscapeOptionsBean           landscapeOptions = new LandscapeOptionsBean();

  public boolean isReportUpdated() {
    return reportUpdated;
  }

  public void setReportUpdated(boolean reportUpdated) {
    this.reportUpdated = reportUpdated;
  }

  public void setGraphicalOptions(LandscapeOptionsBean landscapeOptions) {
    this.landscapeOptions = landscapeOptions;
  }

  public LandscapeOptionsBean getGraphicalOptions() {
    return landscapeOptions;
  }

  public String getQueryResultName() {
    return queryResultName;
  }

  public void setQueryResultName(String queryResultName) {
    this.queryResultName = queryResultName;
  }

  /**
   * @return the {@link QueryResult} for the currently set {@link #queryResultName}
   */
  public QueryResult getQueryResult() {
    return getQueryResult(getQueryResultName());
  }

  /**
   * @param queryName
   *          String-Key to access a certain {@link QueryResult}, see {@link #queryResultName}
   * @return the {@link QueryResult} for the given queryName
   */
  public QueryResult getQueryResult(String queryName) {
    return queryResults.get(queryName);
  }

  /**
   * Adds the given {@link QueryResult} object to the memory bean, replacing any already existing
   * {@link QueryResult} with the same name.
   * @param queryResult
   *          The {@link QueryResult} to set. Can't be null.
   */
  public void setQueryResult(QueryResult queryResult) {
    queryResults.put(queryResult.getQueryName(), queryResult);
  }

  public Map<String, QueryResult> getQueryResults() {
    return this.queryResults;
  }

}
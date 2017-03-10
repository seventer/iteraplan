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
package de.iteratec.iteraplan.model.xml.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportBeanBase;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;


@XmlType
public class QueryResultXML extends AbstractXMLElement<QueryResult> {

  private String             queryName         = ManageReportBeanBase.MAIN_QUERY;

  /** {@link #getQueryForms()} */
  private List<QueryFormXML> queryForms        = new ArrayList<QueryFormXML>();

  private TimeseriesQueryXML timeseriesQuery   = null;

  /**
   * Takes on a new list of ids of Building Blocks the user selected from the result list. Also used
   * to display on the GUI which Building Blocks are currently selected.
   */
  private List<Integer>      selectedResultIds = new ArrayList<Integer>();

  @XmlAttribute(name = "queryName")
  public String getQueryName() {
    return queryName;
  }

  /**
   * The queryforms of the saved query. queryForms[0] contains the main query form. The remaining
   * forms optionally contain query extensions.
   * 
   * @return The queryForms
   */
  @XmlElementWrapper(name = "queryForms")
  @XmlElement(name = "queryForm")
  public List<QueryFormXML> getQueryForms() {
    return queryForms;
  }

  public void setQueryName(String queryName) {
    this.queryName = queryName;
  }

  public void setQueryForms(List<QueryFormXML> queryForms) {
    this.queryForms = queryForms;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#initFrom(java.lang.Object,
   * java.util.Locale)
   */
  public void initFrom(QueryResult queryElement, Locale locale) {
    this.queryName = queryElement.getQueryName();
    this.queryForms = new ArrayList<QueryFormXML>();
    if (queryElement.getSelectedResults().size() == queryElement.getResults().size()) {
      // set null as flag for "select everything available" rather than a specific set of results
      this.setSelectedResultIds(null);
    }
    else {
      this.setSelectedResultIds(Lists.newArrayList(queryElement.getSelectedResultIds()));
    }
    for (DynamicQueryFormData<?> form : queryElement.getQueryForms()) {
      QueryFormXML queryFormXML = new QueryFormXML();
      queryFormXML.initFrom(form, locale, queryElement.getSelectedPostProcessingStrategies());
      this.queryForms.add(queryFormXML);
    }
    timeseriesQuery = new TimeseriesQueryXML();
    timeseriesQuery.initFrom(queryElement.getTimeseriesQuery(), locale);
  }

  /**
   * The BBs of the query result that have to be selected according to the saved query. Note: only
   * buildingBlocks that still exist will be selected!
   * <p>
   * <b>IMPORTANT</b>: Selected result ids are currently not persisted. Hence when running a saved
   * query always all building blocks matching the query criteria are selected. This feature will be
   * implemented in 2.x
   * 
   * @return The selected BBs
   */
  @XmlElementWrapper(name = "selectedResultIds")
  @XmlElement(name = "id")
  public List<Integer> getSelectedResultIds() {
    return selectedResultIds;
  }

  public void setSelectedResultIds(List<Integer> selectedResultIds) {
    this.selectedResultIds = selectedResultIds;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#update(java.lang.Object,
   * java.util.Locale)
   */
  public void update(QueryResult queryElement, Locale locale) {
    // nothing to do
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#validate(java.util.Locale)
   */
  public void validate(Locale locale) {
    for (QueryFormXML formXML : queryForms) {
      formXML.validate(locale);
    }
  }

  /**
   * @return timeseriesQuery the timeseriesQuery
   */
  public TimeseriesQueryXML getTimeseriesQuery() {
    return timeseriesQuery;
  }

  public void setTimeseriesQuery(TimeseriesQueryXML timeseriesQuery) {
    this.timeseriesQuery = timeseriesQuery;
  }

}

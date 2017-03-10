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
package de.iteratec.iteraplan.businesslogic.reports.query.options;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.FormModificationInfo;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.IQStatusData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QTimespanData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.type.IPresentationExtension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.BuildingBlock;


/**
 * Memory Bean that contains everything needed for creating a report page for a certain
 * BuildingBlockType. This calls contains all the query forms, which contain the query conditions
 * for the BuildingBlock type and associated BuildingBlocks, as well as fields needed for creating
 * the rest of the GUI.
 */
public abstract class ManageReportBeanBase extends ReportMemBean implements Serializable {

  public static final String             MAIN_QUERY              = "mainQuery";

  /** Serialization version. */
  private static final long              serialVersionUID        = 5819176360980566703L;

  private static final Logger            LOGGER                  = Logger.getIteraplanLogger(ManageReportBeanBase.class);

  /**
   * Map holding the {@link QueryResult}s of the report
   */
  private final Map<String, QueryResult> queryResults            = CollectionUtils.hashMap();

  /**
   * Holds the query name for the currently active query.
   * Possible values can be queried from the
   * {@link de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions#getQueryResultNames()
   * OptionsBean}.
   */
  private String                         queryResultName         = MAIN_QUERY;

  /** {@link #getSelectedReportExtension()} */
  private String                         selectedReportExtension = null;

  /** {@link #getReportExtensionToRemove()} */
  private Integer                        reportExtensionToRemove = null;

  /** {@link #getFormModification()} */
  private FormModificationInfo           formModification        = new FormModificationInfo();

  /** {@link #getShowResults()} */
  private Boolean                        showResults             = Boolean.FALSE;

  private ViewConfiguration              viewConfiguration       = new ViewConfiguration(UserContext.getCurrentLocale());

  private Boolean                        checkAllBox             = Boolean.FALSE;

  private Boolean                        massUpdateMode          = Boolean.FALSE;

  /** Column Configuration parameters **/
  private String                         currentColumnName       = null;
  private String                         currentColumnAction     = null;
  private String                         selectedNewColumn       = null;

  public ManageReportBeanBase() {
    queryResults.put(queryResultName, new QueryResult(queryResultName));
  }

  public ManageReportBeanBase(DynamicQueryFormData<?> form, TimeseriesQuery tsQuery) {
    this();
    getQueryResult(queryResultName).getQueryForms().add(form);
    getQueryResult(queryResultName).setTimeseriesQuery(tsQuery);
    getQueryResult(queryResultName).resetAvailableReportExtensions();
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

  /**
   * @param queryForms
   *          The query forms (list of DynamicQueryFormData) to display. This list must not be null
   *          or empty. The first form in the list is used to query the element itself. All other
   *          forms in the list are regarded as extensions.
   * @param availableReportExtensions
   *          Report extensions that are available for this query
   * @see QueryResult#getAvailableReportExtensions()
   */
  public void setQueryForms(List<DynamicQueryFormData<?>> queryForms, Map<String, IPresentationExtension> availableReportExtensions) {
    if (availableReportExtensions == null) {
      throw new IllegalArgumentException("Parameter 'availableReportExtensions' must not be null!");
    }
    if (getQueryResult() != null) {
      getQueryResult().setQueryForms(queryForms);
      getQueryResult().setAvailableReportExtensions(availableReportExtensions);
    }
  }

  /**
   * The report extension to add.
   * 
   * @return The report extension to add to the memBean
   */
  public String getSelectedReportExtension() {
    return selectedReportExtension;
  }

  public void setSelectedReportExtension(String selectedReportExtension) {
    this.selectedReportExtension = selectedReportExtension;
  }

  /**
   * The report extension to remove.
   * 
   * @return The The report extension to remove from the memBean.
   */
  public Integer getReportExtensionToRemove() {
    return reportExtensionToRemove;
  }

  public void setReportExtensionToRemove(Integer reportExtensionToRemove) {
    this.reportExtensionToRemove = reportExtensionToRemove;
  }

  public void setResults(List<? extends BuildingBlock> results) {
    if (results == null) {
      resetResults();
      return;
    }
    getQueryResult().setResults(results);
    this.showResults = Boolean.TRUE;
  }

  /**
   * The result list resulting from the query.
   * 
   * @return The result list.
   */
  public List<? extends BuildingBlock> getResults() {
    return getQueryResult().getResults();
  }

  public void resetResults() {
    if (getQueryResult() != null) {
      getQueryResult().setResults(new ArrayList<BuildingBlock>());
      this.showResults = Boolean.FALSE;
      getQueryResult().setSelectedResultIds(new Integer[0]);
    }
  }

  /**
   * Indicates whether or not the results should be displayed on the gui
   * 
   * @return <code>true</code> if the results should be displayed
   */
  public Boolean getShowResults() {
    return showResults;
  }

  public void setShowResults(Boolean showResults) {
    this.showResults = showResults;
  }

  /**
   * Stores the user input for modifying the current query.
   * 
   * @return The user input for modifying the current query.
   */
  public FormModificationInfo getFormModification() {
    return formModification;
  }

  public void setFormModification(FormModificationInfo formModification) {
    this.formModification = formModification;
  }

  public Type<?> getReportResultType() {
    return getQueryResult().getResultType();
  }

  /**
   * Validates the user input of all active query forms.
   */
  public void validateUserInput(Errors errors) {
    LOGGER.debug("entering: validateUserInput()");

    for (QueryResult queryResult : getQueryResults().values()) {
      for (DynamicQueryFormData<?> form : queryResult.getQueryForms()) {
        LOGGER.debug("- query form for type: " + form.getType());

        // validate the status if applicable - at least one status must be selected
        if (form.getType().isHasStatus()) {
          LOGGER.debug("-- has status");
          IQStatusData statusQueryData = form.getQueryUserInput().getStatusQueryData();
          errors.pushNestedPath("statusQueryData");
          statusQueryData.validate(errors);
          errors.popNestedPath();
        }

        // validate the timespan if applicable
        if (form.getType().isHasTimespan()) {
          LOGGER.debug("-- has timespan");
          QTimespanData timespanQueryData = form.getQueryUserInput().getTimespanQueryData();
          errors.pushNestedPath("timespanQueryData");
          timespanQueryData.validate(errors);
          errors.popNestedPath();
        }
      }
      if (queryResult.getTimeseriesQuery() != null) {
        queryResult.getTimeseriesQuery().getTimespan().validate(errors);
      }
    }

    LOGGER.debug("leaving: validateUserInput()");
  }

  public ViewConfiguration getViewConfiguration() {
    return viewConfiguration;
  }

  public void setViewConfiguration(ViewConfiguration config) {
    this.viewConfiguration = config;
  }

  public String getCurrentColumnName() {
    return currentColumnName;
  }

  public void setCurrentColumnName(String currentColumnName) {
    this.currentColumnName = currentColumnName;
  }

  public String getCurrentColumnAction() {
    return currentColumnAction;
  }

  public void setCurrentColumnAction(String currentAction) {
    this.currentColumnAction = currentAction;
  }

  public String getSelectedNewColumn() {
    return selectedNewColumn;
  }

  public void setSelectedNewColumn(String selectedNewColumn) {
    this.selectedNewColumn = selectedNewColumn;
  }

  public Boolean getCheckAllBox() {
    return checkAllBox;
  }

  public void setCheckAllBox(Boolean checkAllBox) {
    this.checkAllBox = checkAllBox;
  }

  public Boolean getMassUpdateMode() {
    return massUpdateMode;
  }

  public void setMassUpdateMode(Boolean massUpdateMode) {
    this.massUpdateMode = massUpdateMode;

    // when this bean is used for massUpdate, the default for checkAllBox should be false
    if (massUpdateMode.booleanValue()) {
      checkAllBox = Boolean.FALSE;
    }
    if (getQueryResult().getSelectedResults().size() == getResults().size()) {
      checkAllBox = Boolean.TRUE;
    }
  }

  public String getQueryResultName() {
    return queryResultName;
  }

  public void setQueryResultName(String queryResultName) {
    this.queryResultName = queryResultName;
  }

  public Map<String, QueryResult> getQueryResults() {
    return this.queryResults;
  }

}

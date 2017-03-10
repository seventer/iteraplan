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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.OptionConsiderStateAndDate;
import de.iteratec.iteraplan.businesslogic.reports.query.type.IPresentationExtension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.user.PermissionHelper;


/**
 * This class holds a dynamic query, its results and the user selection of the results as well as
 * the available report extensions and post processing strategies.
 */
public class QueryResult implements Serializable {

  /** Serialization version. */
  private static final long                   serialVersionUID          = -4371798398005638619L;

  private String                              queryName;

  /**
   * A list of query forms. Each query form contains query conditions for a certain element type.
   * The first form in the list holds query conditions for the element type that is queried, all
   * other query forms hold query conditions for adjacent elements. These are joined when running
   * the query.
   */
  private List<DynamicQueryFormData<?>>       queryForms                = CollectionUtils.arrayList();

  /** The result of the query. */
  private List<? extends BuildingBlock>       results                   = CollectionUtils.arrayList();

  /**
   * Takes on a new list of ids of Building Blocks the user selected from the result list. Also used
   * to display on the GUI which Building Blocks are currently selected.
   */
  private Integer[]                           selectedResultIds         = new Integer[0];

  /** list of available post processing strategies and their selected status */
  private List<PostProcessingStrategyEntry>   postProcessingStrategies  = CollectionUtils.arrayList();

  /**
   * Report extensions that are available for the BuildingBlock type. A report extension enables the
   * user to define query conditions for associated BuildingBlock types.
   */
  private Map<String, IPresentationExtension> availableReportExtensions = new HashMap<String, IPresentationExtension>();

  /** are there post processing strategies selected? */
  private Boolean                             advancedFunctions         = Boolean.FALSE;

  private TimeseriesQuery                     timeseriesQuery;

  /**
   * An empty, basic constructor
   */
  public QueryResult(String queryName) {
    super();
    this.queryName = queryName;
  }

  /**
   * A copy constructor.
   * 
   * @param queryName
   *          name of this query
   * @param queryForms
   *          List of query forms.
   * @param results
   *          The results
   * @param selectedResultIds
   *          The selected result ids.
   * @param reportTypeString
   *          String representing the {@link de.iteratec.iteraplan.model.queries.ReportType ReportType}
   *          this QueryResult is for, used to initiate the PostProcessingStrategies
   * @throws IteraplanTechnicalException if {@code queryForms} is null or empty
   */
  public QueryResult(String queryName, List<DynamicQueryFormData<?>> queryForms, TimeseriesQuery tsQuery, List<? extends BuildingBlock> results,
      Integer[] selectedResultIds, String reportTypeString) {
    this(queryName);
    if (queryForms == null || queryForms.isEmpty()) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    this.queryForms = queryForms;
    timeseriesQuery = tsQuery;
    this.results = results;
    this.selectedResultIds = (selectedResultIds == null) ? null : selectedResultIds.clone();
    initPostProcessingStrategies(reportTypeString);
    this.availableReportExtensions = new HashMap<String, IPresentationExtension>(getQueryForms().get(0).getType().getExtensionsForPresentation());
  }

  /**
   * Extracts the selected BuildingBlock instances from the result list by using the ids in
   * {@link #selectedResultIds}.
   * 
   * @return The selected BuildingBlock instances.
   */
  private List<? extends BuildingBlock> extractSelectedResults() {
    List<Integer> selResIdList = Arrays.asList(getSelectedResultIds());
    List<BuildingBlock> result = new ArrayList<BuildingBlock>();
    for (Iterator<? extends BuildingBlock> it = getResults().iterator(); it.hasNext();) {
      BuildingBlock el = it.next();
      if (selResIdList.contains(el.getId())) {
        result.add(el);
      }
    }
    return result;
  }

  public String getQueryName() {
    return queryName;
  }

  public void setQueryName(String queryName) {
    this.queryName = queryName;
  }

  /**
   * List of DynamicQueryFormData objects. The first one is the standard query form, the rest are
   * extended query forms.
   * 
   * @return The query forms
   */
  public List<DynamicQueryFormData<?>> getQueryForms() {
    return queryForms;
  }

  public List<? extends BuildingBlock> getResults() {
    return results;
  }

  public void setResults(List<? extends BuildingBlock> results) {
    this.results = results;
  }

  public final Integer[] getSelectedResultIds() {
    Set<Integer> tmp = new HashSet<Integer>(Arrays.asList(selectedResultIds));
    tmp.remove(Integer.valueOf(-1));
    Integer[] ids = new Integer[tmp.size()];
    int i = 0;
    for (Iterator<Integer> it = tmp.iterator(); it.hasNext(); i++) {
      Integer id = it.next();
      ids[i] = id;
    }
    return ids;
  }

  public void setSelectedResultIds(Integer[] selectedResultIds) {
    this.selectedResultIds = (selectedResultIds == null) ? new Integer[0] : selectedResultIds.clone();
  }

  public List<? extends BuildingBlock> getSelectedResults() {
    return extractSelectedResults();
  }

  public Type<?> getResultType() {
    return queryForms.get(0).getType();
  }

  public List<PostProcessingStrategyEntry> getPostProcessingStrategies() {
    return postProcessingStrategies;
  }

  /**
   * @return the available post processing strategies whose {@link PostProcessingStrategyEntry}s have the status selected == true
   */
  public List<AbstractPostprocessingStrategy<? extends BuildingBlock>> getSelectedPostProcessingStrategies() {
    List<AbstractPostprocessingStrategy<? extends BuildingBlock>> selectedPostProcessingStrategies = CollectionUtils.arrayList();

    if (postProcessingStrategies != null) {
      for (PostProcessingStrategyEntry entry : postProcessingStrategies) {
        if (entry.isSelected()) {
          selectedPostProcessingStrategies.add(entry.getStrategy());
        }
      }
    }
    return selectedPostProcessingStrategies;
  }

  public void setSelectedPostprocessingStrategies(List<AbstractPostprocessingStrategy<? extends BuildingBlock>> strategies,
                                                  Map<String, List<String>> strategyWithOptions) {

    if (postProcessingStrategies == null) {
      return;
    }
    for (PostProcessingStrategyEntry entry : postProcessingStrategies) {
      for (AbstractPostprocessingStrategy<? extends BuildingBlock> strategy : strategies) {
        if (entry.getStrategy().getNameKeyForPresentation().equals(strategy.getNameKeyForPresentation())) {
          entry.setSelected(true);

          if (!strategyWithOptions.isEmpty() && strategyWithOptions.containsKey(entry.getStrategy().getNameKeyForPresentation())) {
            List<String> ppsOptionKeys = strategyWithOptions.get(strategy.getNameKeyForPresentation());

            for (OptionConsiderStateAndDate option : entry.getStrategy().getAdditionalOptions()) {
              if (ppsOptionKeys != null && ppsOptionKeys.size() > 0) {
                for (String optionKey : ppsOptionKeys) {
                  if (option.getKey().equals(optionKey)) {
                    option.setSelected(true);
                  }
                  else {
                    option.setSelected(false);
                  }
                }
              }
              else {
                option.setSelected(false);
              }
            }
          }
          this.advancedFunctions = Boolean.TRUE;
        }
      }
    }
  }

  /**
   * Initializes the list of available {@link PostProcessingStrategyEntry}s according to the given result type and report type
   * @param reportResultType
   *          {@link Type} representing the result type of the query
   * @param reportType
   *          String representing the report type of the query
   */
  private void setAvailablePostProcessingStrategies(String reportType) {
    List<AbstractPostprocessingStrategy<? extends BuildingBlock>> ppsType = getResultType().getPostprocessingStrategies();
    if (ppsType == null) {
      postProcessingStrategies = CollectionUtils.arrayList();
      return;
    }
    List<AbstractPostprocessingStrategy<? extends BuildingBlock>> strategies = new ArrayList<AbstractPostprocessingStrategy<? extends BuildingBlock>>(
        ppsType);
    for (Iterator<AbstractPostprocessingStrategy<? extends BuildingBlock>> it = strategies.iterator(); it.hasNext();) {
      AbstractPostprocessingStrategy<? extends BuildingBlock> pps = it.next();
      String[] types = pps.getSupportedReportTypes();
      if (types == null) {
        it.remove();
        continue;
      }
      boolean supported = false;
      for (int i = 0; i < types.length; i++) {
        String type = types[i];
        if (type.equals(reportType)) {
          supported = true;
          break;
        }
      }
      if (!supported) {
        it.remove();
      }
    }
    postProcessingStrategies = CollectionUtils.arrayList();
    for (AbstractPostprocessingStrategy<? extends BuildingBlock> str : strategies) {
      postProcessingStrategies.add(new PostProcessingStrategyEntry(str, false));
    }
  }

  /**
   * Gets the post processing strategies for the given type according to the list of string-keys
   * @param type
   *          {@link Type}
   * @return List of Strategy
   *          String-list of strategies to get from {@code type}
   */
  public static List<AbstractPostprocessingStrategy<? extends BuildingBlock>> getPostProcessingStrategiesByKeys(Type<?> type,
                                                                                                                List<String> selectedPSKeys) {
    List<AbstractPostprocessingStrategy<? extends BuildingBlock>> selectedPostProcessingStrategies = CollectionUtils.arrayList();
    if (!(selectedPSKeys == null || selectedPSKeys.isEmpty())) {
      for (String strategy : selectedPSKeys) {
        AbstractPostprocessingStrategy<? extends BuildingBlock> pps = type.getPostprocessingStrategies().getWithKey(strategy);
        if (pps != null) {
          selectedPostProcessingStrategies.add(pps);
        }
      }
    }
    return selectedPostProcessingStrategies;
  }

  public List<IPresentationExtension> getAvailableReportExtensionsSorted() {
    List<IPresentationExtension> availableReportExtensionsCopy = new ArrayList<IPresentationExtension>(getAvailableReportExtensions().values());

    List<IPresentationExtension> availableReportExtensionsFiltered = new ArrayList<IPresentationExtension>();

    for (IPresentationExtension extension : availableReportExtensionsCopy) {
      if (PermissionHelper.hasPermissionFor(extension.getPermissionKey())) {
        availableReportExtensionsFiltered.add(extension);
      }
    }

    Collections.sort(availableReportExtensionsFiltered, new IPresentationExtension.FunctionalExtensionComparator());
    return availableReportExtensionsFiltered;
  }

  /**
   * Returns the currently available {@link #availableReportExtensions report extensions} 
   * @return The report extensions available fot the QueryType
   */
  public Map<String, IPresentationExtension> getAvailableReportExtensions() {
    return availableReportExtensions;
  }

  /**
   * Sets {@link #availableReportExtensions}.
   * @param availableReportExtensions
   */
  public void setAvailableReportExtensions(Map<String, IPresentationExtension> availableReportExtensions) {
    this.availableReportExtensions = availableReportExtensions;
  }

  public void resetAvailableReportExtensions() {
    this.availableReportExtensions = new HashMap<String, IPresentationExtension>(getQueryForms().get(0).getType().getExtensionsForPresentation());
  }

  /**
   * Sets all post processing strategies to disabled and initializes them according to report type and result type.
   * @param reportType
   *          {@link ReportType}-string
   */
  public final void initPostProcessingStrategies(String reportType) {
    setAvailablePostProcessingStrategies(reportType);
    for (PostProcessingStrategyEntry entry : postProcessingStrategies) {
      entry.setSelected(false);
      for (OptionConsiderStateAndDate option : entry.getStrategy().getAdditionalOptions()) {
        option.setSelected(false);
      }
    }
    this.advancedFunctions = Boolean.FALSE;
  }

  /**
   * @param queryForms
   *          The query forms (list of DynamicQueryFormData) to display. This list must not be null
   *          or empty. The first form in the list is used to query the element itself. All other
   *          forms in the list are regarded as extensions.
   */
  public void setQueryForms(List<DynamicQueryFormData<?>> queryForms) {
    if (queryForms == null || queryForms.isEmpty()) {
      throw new IllegalArgumentException("Parameter 'queryForms' must not be null or empty!");
    }
    this.queryForms = queryForms;
  }

  public Boolean getAdvancedFunctions() {
    return advancedFunctions;
  }

  public boolean isTimeseriesQueryAvailable() {
    return timeseriesQuery != null && timeseriesQuery.getAvailableAttributes().size() > 1;
  }

  /**
   * @return timeseriesQuery the timeseriesQuery
   */
  public TimeseriesQuery getTimeseriesQuery() {
    return timeseriesQuery;
  }

  public void setTimeseriesQuery(TimeseriesQuery timeseriesQuery) {
    this.timeseriesQuery = timeseriesQuery;
  }
}
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.validation.Errors;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.IGraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExportOption;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TabularOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.sorting.ResourceBundleKeyComparator;


/**
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings("UWF_NULL_FIELD")
public class ManageReportMemoryBean extends ManageReportBeanBase {

  /** Serialization version. */
  private static final long           serialVersionUID       = 3914583762696625447L;
  private String                      reportType;
  private List<ExportOption>          availableResultFormats = Lists.newArrayList();

  private String                      selectedBuildingBlock;

  private boolean                     loadedFromSavedQuery;
  private boolean                     reportUpdated          = false;
  private boolean                     visibleColumnsUpdated  = false;

  /** The List of all building block type keys (in plural). */
  private static final List<String>   AVAILABLE_BB_TYPES;
  static {
    AVAILABLE_BB_TYPES = Constants.ALL_TYPES_FOR_DISPLAY;
    Collections.sort(AVAILABLE_BB_TYPES, new ResourceBundleKeyComparator());
  }

  private IGraphicalExportBaseOptions graphicalOptions;

  /** {@link #getTabularOptions()} */
  private TabularOptionsBean          tabularOptions         = new TabularOptionsBean();

  public ManageReportMemoryBean() {
    super();
  }

  public ManageReportMemoryBean(DynamicQueryFormData<?> form, TimeseriesQuery tsQuery) {
    super(form, tsQuery);
  }

  public String getSelectedBuildingBlock() {
    return selectedBuildingBlock;
  }

  public void setSelectedBuildingBlock(String selectedBuildingBlock) {
    this.selectedBuildingBlock = selectedBuildingBlock;
  }

  public TabularOptionsBean getTabularOptions() {
    return tabularOptions;
  }

  public void setTabularOptions(TabularOptionsBean tabularOptions) {
    this.tabularOptions = tabularOptions;
  }

  public List<ExportOption> getAvailableResultFormats() {
    return availableResultFormats;
  }

  public void setAvailableResultFormats(List<ExportOption> availableResultFormats) {
    this.availableResultFormats = availableResultFormats;
  }

  public ReportType getReportType() {
    return ReportType.fromValue(reportType);
  }

  public void setReportType(ReportType reportType) {
    this.reportType = reportType != null ? reportType.getValue() : null;
  }

  /**
   * Sets all post processing strategies of the active query to disabled
   * and initializes them according to report type and result type.
   */
  public void resetPostProcessingStrategies() {
    if (getQueryResult() != null) {
      getQueryResult().initPostProcessingStrategies(reportType);
    }
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  /**
   * Sets the {@link IGraphicalExportBaseOptions}-bean and initializes the memory bean's query result map
   * with the query result names derived from the options bean.
   * @param options
   *          the options bean to set
   */
  public void setGraphicalOptions(IGraphicalExportBaseOptions options) {
    this.graphicalOptions = options;

    if (!options.getQueryResultNames().isEmpty()) {
      for (String queryResultName : options.getQueryResultNames()) {
        QueryResult queryResult = getQueryResult(queryResultName);
        if (queryResult == null) {
          queryResult = new QueryResult(queryResultName);
        }
        setQueryResult(queryResult);
      }
    }
    getQueryResults().keySet().retainAll(options.getQueryResultNames());
  }

  public IGraphicalExportBaseOptions getGraphicalOptions() {
    return graphicalOptions;
  }

  public int getHierarchicalDepth() {
    final List<? extends BuildingBlock> entities = getQueryResult().getSelectedResults();
    int depth = 1;
    for (BuildingBlock bb : entities) {
      depth = Math.max(depth, getHierarchicalLevel(bb));
    }
    return depth;
  }

  private int getHierarchicalLevel(BuildingBlock bb) {
    if (bb instanceof HierarchicalEntity<?>) {
      return ((HierarchicalEntity<?>) bb).getLevel();
    }
    return 1;
  }

  public boolean isReportUpdated() {
    return reportUpdated;
  }

  public void setReportUpdated(boolean reportChanged) {
    this.reportUpdated = reportChanged;
  }

  public boolean isVisibleColumnsUpdated() {
    return visibleColumnsUpdated;
  }

  public void setVisibleColumnsUpdated(boolean visibleColumnsUpdated) {
    this.visibleColumnsUpdated = visibleColumnsUpdated;
  }

  /**
   * If the memBean is used in massupdate mode, the underlying massupdate type is returned
   * 
   * @return The massupdate type
   */
  public MassUpdateType getMassUpdateType() {
    List<DynamicQueryFormData<?>> queryForms = getQueryResult().getQueryForms();
    if (queryForms != null && !queryForms.isEmpty()) {
      return queryForms.get(0).getMassUpdateType();
    }
    return null;
  }

  public List<List<BBAttribute>> getAvailableAttributesForMassUpdate() {
    List<List<BBAttribute>> result = Lists.newArrayList();
    List<DynamicQueryFormData<?>> queryForms = getQueryResult().getQueryForms();

    Predicate<BBAttribute> noTimeseriesPredicate = new Predicate<BBAttribute>() {
      public boolean apply(BBAttribute input) {
        return !input.isTimeseries();
      }
    };

    for (DynamicQueryFormData<?> queryForm : queryForms) {
      List<BBAttribute> allUserDefAttributes = queryForm.getAvailableUserDefinedAttributes();
      result.add(Lists.newArrayList(Collections2.filter(allUserDefAttributes, noTimeseriesPredicate)));
    }
    return result;
  }

  public List<String> getAvailableBbTypes() {
    List<String> permittedBbTypes = new ArrayList<String>();
    for (String bBTypeInList : AVAILABLE_BB_TYPES) {
      if (UserContext.getCurrentPerms().getUserHasBbTypeFunctionalPermission(bBTypeInList)) {
        permittedBbTypes.add(bBTypeInList);
      }
    }

    return permittedBbTypes;
  }

  public List<String> getAvailableBbTypesForMassupdate() {
    List<String> availableBBTypesAndBM = new ArrayList<String>(AVAILABLE_BB_TYPES);
    availableBBTypesAndBM.add(Constants.BB_BUSINESSMAPPING);
    Collections.sort(availableBBTypesAndBM, new ResourceBundleKeyComparator());

    List<String> permittedBbTypes = new ArrayList<String>();
    for (String bBTypeInList : availableBBTypesAndBM) {
      if (UserContext.getCurrentPerms().getUserHasBbTypeUpdatePermission(bBTypeInList)
          && UserContext.getCurrentPerms().getUserHasBbTypeFunctionalPermission(bBTypeInList)) {
        permittedBbTypes.add(bBTypeInList);
      }
    }

    return permittedBbTypes;
  }

  public boolean isLoadedFromSavedQuery() {
    return loadedFromSavedQuery;
  }

  public void setLoadedFromSavedQuery(boolean loadedFromSavedQuery) {
    this.loadedFromSavedQuery = loadedFromSavedQuery;
  }

  public void validate(Errors errors) {
    super.validateUserInput(errors);
  }

}
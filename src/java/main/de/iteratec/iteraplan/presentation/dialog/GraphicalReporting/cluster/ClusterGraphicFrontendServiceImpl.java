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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.cluster;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportBeanBase;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalOptionsGetter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterSecondOrderBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.service.GeneralBuildingBlockService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.sorting.BuildingBlockComparator;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicExportBean;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicalReportBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator;
import de.iteratec.svg.model.Document;


@Service("clusterGraphicFrontendService")
public class ClusterGraphicFrontendServiceImpl extends GraphicalReportBaseFrontendServiceImpl<ManageReportMemoryBean> implements
    ClusterGraphicFrontendService {

  @Autowired
  private GeneralBuildingBlockService generalBuildingBlockService;

  public ManageReportMemoryBean getInitialMemBean() {

    ManageReportMemoryBean memBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL,
        Constants.ALL_TYPES_FOR_DISPLAY);

    ClusterOptionsBean options = new ClusterOptionsBean();
    memBean.setGraphicalOptions(options);
    options.setSelectedBbType(memBean.getSelectedBuildingBlock());

    getInitFormHelperService().initializeClusterSecondOrderBeans(options);
    memBean.setReportType(getReportType());
    memBean.setSavedQueries(getSavedQueryService().getSavedQueriesWithoutContent(getReportType()));
    memBean.resetPostProcessingStrategies();

    return memBean;
  }

  public ManageReportMemoryBean fromInterchange(String bbType, String idList) {

    if (idList == null || bbType == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }

    ManageReportMemoryBean resultMemBean = getInitialMemBean();
    GraphicalOptionsGetter.getClusterOptions(resultMemBean).setSelectedBbType(bbType);
    resultMemBean = getMemBeanForChangedQueryType(resultMemBean);
    getInitFormHelperService().dropRestrictionsFromQueryForm(resultMemBean.getQueryResult().getQueryForms().get(0));

    List<BuildingBlock> allEntities = generalBuildingBlockService.getBuildingBlocksByType(TypeOfBuildingBlock.getTypeOfBuildingBlockByString(bbType));
    removeVirtualElement(allEntities);
    Collections.sort(allEntities, new BuildingBlockComparator());

    List<Integer> idsAsList = parseSelectedIds(idList);
    Integer[] ids = new Integer[idsAsList.size()];
    for (int i = 0; i < idsAsList.size(); i++) {
      ids[i] = idsAsList.get(i);
    }

    List<DynamicQueryFormData<?>> queryForms = resultMemBean.getQueryResult().getQueryForms();
    resultMemBean.setQueryResult(new QueryResult(ManageReportBeanBase.MAIN_QUERY, queryForms, resultMemBean.getQueryResult().getTimeseriesQuery(),
        allEntities, ids, getReportType().getValue()));
    refreshReport(resultMemBean, null, null, false);

    return stepOneToStepTwo(resultMemBean, null, null);
  }

  @Override
  protected ManageReportMemoryBean getMemBeanForChangedQueryType(ManageReportMemoryBean memBean) {
    ManageReportMemoryBean newMemBean = null;
    final ClusterOptionsBean clusterOptions = GraphicalOptionsGetter.getClusterOptions(memBean);
    final String selectedType = clusterOptions.getSelectedBbType();

    if (Constants.REPORTS_EXPORT_CLUSTER_MODE_BB.equals(clusterOptions.getSelectedClusterMode())) {

      newMemBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(selectedType, Constants.ALL_TYPES_FOR_DISPLAY);

      if (newMemBean == null) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }

      final List<SavedQuery> savedQueries = getSavedQueryService().getSavedQueriesWithoutContent(getReportType());

      newMemBean.setSelectedBuildingBlock(selectedType);
      newMemBean.setSavedQueries(savedQueries);
      newMemBean.setReportType(getReportType());
      ClusterOptionsBean newOptions = new ClusterOptionsBean();
      newMemBean.setGraphicalOptions(newOptions);
      newOptions.setSelectedAttributeType(-1);
      newOptions.setSelectedBbType(selectedType);

      getInitFormHelperService().initializeClusterSecondOrderBeans(newOptions);

      return newMemBean;

    }
    else {

      if (clusterOptions.getSelectedAttributeType() == -1) {
        getInitFormHelperService().initializeClusterForAttributeMode(clusterOptions);
        memBean.resetResults();
      }

      clusterOptions.resetSecondOrderBeans();
      getInitFormHelperService().initializeClusterSecondOrderBeans(clusterOptions);

      clusterOptions.getColorOptionsBean().setDimensionAttributeId(Integer.valueOf(clusterOptions.getSelectedAttributeType()));

      getInitFormHelperService().refreshGraphicalExportColorOptions(clusterOptions.getColorOptionsBean(), null);

      clusterOptions.setSelectedAttributeValues(clusterOptions.getColorOptionsBean().getAttributeValues());
      memBean.setCheckAllBox(Boolean.TRUE);

      return memBean;
    }

  }

  @Override
  protected String getFlowId() {
    return Dialog.GRAPHICAL_REPORTING_CLUSTER.getFlowId();
  }

  @SuppressWarnings("boxing")
  public ManageReportMemoryBean stepOneToStepTwo(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    QueryResult queryResult = memBean.getQueryResult();
    List<? extends BuildingBlock> selectedResults = queryResult.getSelectedResults();

    if (selectedResults.size() > IteraplanProperties.getIntProperty(IteraplanProperties.EXPORT_GRAPHICAL_CLUSTER_MAXCOLUMNS)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.CLUSTER_TOO_MANY_COLUMNS, selectedResults.size(),
          IteraplanProperties.getIntProperty(IteraplanProperties.EXPORT_GRAPHICAL_CLUSTER_MAXCOLUMNS));
    }
    ClusterOptionsBean options = GraphicalOptionsGetter.getClusterOptions(memBean);
    String clusterMode = options.getSelectedClusterMode();

    boolean invalidClusterModeBb = Constants.REPORTS_EXPORT_CLUSTER_MODE_BB.equals(clusterMode) && queryResult.getSelectedResultIds().length <= 0;
    boolean invalidClusterModeAttribute = Constants.REPORTS_EXPORT_CLUSTER_MODE_ATTRIBUTE.equals(clusterMode)
        && (options.getSelectedAttributeValues() == null || options.getSelectedAttributeValues().isEmpty());
    if (invalidClusterModeBb || invalidClusterModeAttribute) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.CLUSTER_NO_ELEMENTS);
    }

    options.setDialogStep(2);
    options.setAvailableHierarchicalLevels(Integer.valueOf(memBean.getHierarchicalDepth()));

    return memBean;
  }

  public ManageReportMemoryBean stepTwoToStepOne(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    if (getReportType().equals(memBean.getReportType()) && memBean.getGraphicalOptions() != null) {
      memBean.getGraphicalOptions().setDialogStep(1);
    }

    return memBean;
  }

  @Override
  @SuppressWarnings("boxing")
  public void generateGraphicFileResponse(ManageReportMemoryBean memBean, HttpServletRequest request, HttpServletResponse response) {

    ClusterOptionsBean options = GraphicalOptionsGetter.getClusterOptions(memBean);
    List<? extends BuildingBlock> selectedResults = memBean.getQueryResult().getSelectedResults();

    if (Constants.REPORTS_EXPORT_CLUSTER_MODE_BB.equals(options.getSelectedClusterMode())) {
      if (selectedResults.size() > IteraplanProperties.getIntProperty(IteraplanProperties.EXPORT_GRAPHICAL_CLUSTER_MAXCOLUMNS)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.CLUSTER_TOO_MANY_COLUMNS, selectedResults.size(),
            IteraplanProperties.getIntProperty(IteraplanProperties.EXPORT_GRAPHICAL_CLUSTER_MAXCOLUMNS));
      }

      if (selectedResults.size() < IteraplanProperties.getIntProperty(IteraplanProperties.EXPORT_GRAPHICAL_CLUSTER_MINCOLUMNS)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.CLUSTER_NO_COLUMNS);
      }
    }

    // Retrieve the application url
    String srvAddr = URLBuilder.getApplicationURL(request);
    options.setServerUrl(srvAddr);

    // Generate
    Document doc = getExportService().generateSvgClusterExport(selectedResults, options);
    GraphicExportBean vBean = new GraphicExportBean(doc);

    GraphicsResponseGenerator respGen = getExportService().getResponseGenerator(options.getSelectedGraphicFormat());
    // have the report file written to the HttpServletResponse
    respGen.generateResponse(response, vBean, GraphicsResponseGenerator.GraphicalReport.CLUSTER, memBean.getContent());

  }

  @Override
  protected void refreshGraphicalOptions(ManageReportMemoryBean memBean) {
    // refresh options
    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(memBean.getSelectedBuildingBlock());

    getInitFormHelperService().refreshGraphicalExportColorOptions(memBean.getGraphicalOptions().getColorOptionsBean(), tobb);
    getInitFormHelperService().refreshGraphicalExportLineTypeOptions(memBean.getGraphicalOptions().getLineOptionsBean(), tobb);
    refreshClusterOptions(memBean);
  }

  private void refreshClusterOptions(ManageReportMemoryBean memBean) {
    if (getReportType().equals(memBean.getReportType()) && memBean.getGraphicalOptions() != null) {
      ClusterOptionsBean clusterOptions = GraphicalOptionsGetter.getClusterOptions(memBean);
      clusterOptions.refreshOrder();

      // Refresh cluster second order color dimensions
      for (ClusterSecondOrderBean clusterSecondOrderBean : clusterOptions.getSecondOrderBeans()) {
        TypeOfBuildingBlock bbType = null;
        if (clusterSecondOrderBean.getBeanType().equals(ClusterSecondOrderBean.BUILDING_BLOCK_BEAN)) {
          bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(clusterSecondOrderBean.getRepresentedType());
        }
        getInitFormHelperService().refreshGraphicalExportColorOptions(clusterSecondOrderBean.getColorOptions(), bbType);
      }
    }
  }

  @Override
  protected ReportType getReportType() {
    return ReportType.CLUSTER;
  }

  @Override
  public void updateCheckAllBox(ManageReportMemoryBean memBean) {
    if (Constants.REPORTS_EXPORT_CLUSTER_MODE_BB.equals(GraphicalOptionsGetter.getClusterOptions(memBean).getSelectedClusterMode())) {
      super.updateCheckAllBox(memBean);
    }
  }

  public void setGeneralBuildingBlockService(GeneralBuildingBlockService generalBuildingBlockService) {
    this.generalBuildingBlockService = generalBuildingBlockService;
  }
}

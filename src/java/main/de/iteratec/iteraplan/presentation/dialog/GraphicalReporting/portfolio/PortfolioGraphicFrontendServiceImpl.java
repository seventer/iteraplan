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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.portfolio;

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
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.IGraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.PortfolioOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.service.GeneralBuildingBlockService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.sorting.BuildingBlockComparator;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicExportBean;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicalReportBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator;


@Service("portfolioGraphicFrontendService")
public class PortfolioGraphicFrontendServiceImpl extends GraphicalReportBaseFrontendServiceImpl<ManageReportMemoryBean> implements
    PortfolioGraphicFrontendService {

  @Autowired
  private GeneralBuildingBlockService generalBuildingBlockService;

  public ManageReportMemoryBean getInitialMemBean() {
    ManageReportMemoryBean memBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL,
        Constants.ALL_TYPES_FOR_DISPLAY);

    memBean.setGraphicalOptions(new PortfolioOptionsBean());
    memBean.getGraphicalOptions().setSelectedBbType(memBean.getSelectedBuildingBlock());
    memBean.setReportType(getReportType());
    memBean.setSavedQueries(getSavedQueryService().getSavedQueriesWithoutContent(getReportType()));
    memBean.resetPostProcessingStrategies();

    return memBean;
  }

  @Override
  protected ManageReportMemoryBean getMemBeanForChangedQueryType(ManageReportMemoryBean memBean) {
    ManageReportMemoryBean newMemBean = null;
    IGraphicalExportBaseOptions options = memBean.getGraphicalOptions();
    String selectedType = options.getSelectedBbType();

    newMemBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(selectedType, Constants.ALL_TYPES_FOR_DISPLAY);

    if (newMemBean == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    newMemBean.setSelectedBuildingBlock(selectedType);

    List<SavedQuery> savedQueries = getSavedQueryService().getSavedQueriesWithoutContent(getReportType());
    newMemBean.setSavedQueries(savedQueries);
    newMemBean.setGraphicalOptions(memBean.getGraphicalOptions());
    newMemBean.setReportType(memBean.getReportType());

    return newMemBean;
  }

  public ManageReportMemoryBean fromInterchange(String bbType, String idList) {

    if (idList == null || bbType == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }

    ManageReportMemoryBean resultMemBean = getInitialMemBean();
    GraphicalOptionsGetter.getPortfolioOptions(resultMemBean).setSelectedBbType(bbType);
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
    resultMemBean = refreshReport(resultMemBean, null, null, false);

    return stepOneToStepTwo(resultMemBean, null, null);
  }

  @Override
  protected String getFlowId() {
    return Dialog.GRAPHICAL_REPORTING_CLUSTER.getFlowId();
  }

  public ManageReportMemoryBean stepOneToStepTwo(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    if (getReportType().equals(memBean.getReportType()) && memBean.getGraphicalOptions() != null) {
      memBean.getGraphicalOptions().setDialogStep(2);
    }

    return memBean;
  }

  public ManageReportMemoryBean stepTwoToStepOne(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    if (getReportType().equals(memBean.getReportType()) && memBean.getGraphicalOptions() != null) {
      memBean.getGraphicalOptions().setDialogStep(1);
    }
    return memBean;
  }

  @Override
  public void generateGraphicFileResponse(ManageReportMemoryBean memBean, HttpServletRequest request, HttpServletResponse response) {
    if (memBean.getQueryResult().getSelectedResultIds().length <= 0) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.PORTFOLIO_NO_ELEMENTS);
    }

    PortfolioOptionsBean options = GraphicalOptionsGetter.getPortfolioOptions(memBean);

    GraphicExportBean vBean = null;

    String srvAddr = URLBuilder.getApplicationURL(request);
    options.setServerUrl(srvAddr);
    options.setServerUrl(srvAddr);

    // Generate response according to the chosen file type
    List<? extends BuildingBlock> selectedResults = memBean.getQueryResult().getSelectedResults();
    if (options.getSelectedGraphicFormat().equalsIgnoreCase(Constants.REPORTS_EXPORT_GRAPHICAL_VISIO)) {
      de.iteratec.visio.model.Document doc = getExportService().generateVisioPortfolioExport(selectedResults, options);
      vBean = new GraphicExportBean(doc);
    }
    else {
      de.iteratec.svg.model.Document doc = getExportService().generateSvgPortfolioExport(selectedResults, options);
      vBean = new GraphicExportBean(doc);
    }

    if (vBean != null) {
      GraphicsResponseGenerator respGen = getExportService().getResponseGenerator(options.getSelectedGraphicFormat());
      // have the report file written to the HttpServletResponse
      respGen.generateResponse(response, vBean, GraphicsResponseGenerator.GraphicalReport.PORTFOLIO, memBean.getContent());
    }
  }

  @Override
  protected ReportType getReportType() {
    return ReportType.PORTFOLIO;
  }

  public void setGeneralBuildingBlockService(GeneralBuildingBlockService generalBuildingBlockService) {
    this.generalBuildingBlockService = generalBuildingBlockService;
  }
}

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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.PieBar;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportBeanBase;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalOptionsGetter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.IGraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.DiagramKeyType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.DiagramType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.ValuesType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.SingleBarOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.service.GeneralBuildingBlockService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
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
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator.GraphicalReport;
import de.iteratec.svg.model.Document;


@Service("pieBarGraphicFrontendService")
public class PieBarGraphicFrontendServiceImpl extends GraphicalReportBaseFrontendServiceImpl<ManageReportMemoryBean> implements
    PieBarGraphicFrontendService {

  private static final Logger         LOGGER = Logger.getIteraplanLogger(PieBarGraphicFrontendServiceImpl.class);

  @Autowired
  private GeneralBuildingBlockService generalBuildingBlockService;

  public ManageReportMemoryBean getInitialMemBean() {
    ManageReportMemoryBean memBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL,
        Constants.ALL_TYPES_FOR_DISPLAY);

    memBean.setGraphicalOptions(new PieBarDiagramOptionsBean());
    memBean.getGraphicalOptions().setSelectedBbType(memBean.getSelectedBuildingBlock());

    getInitFormHelperService().initializePieBarOptions(memBean);
    memBean.setReportType(ReportType.PIE);

    final List<SavedQuery> savedQueries = getSavedQueryService().getSavedQueriesWithoutContent(Sets.newHashSet(ReportType.PIE, ReportType.BAR));
    memBean.setSavedQueries(savedQueries);
    memBean.resetPostProcessingStrategies();

    return memBean;
  }

  public ManageReportMemoryBean fromInterchange(String bbType, String idList) {
    if (idList == null || bbType == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }

    ManageReportMemoryBean resultMemBean = getInitialMemBean();
    GraphicalOptionsGetter.getPieBarOptions(resultMemBean).setSelectedBbType(bbType);
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
        allEntities, ids, GraphicalOptionsGetter.getPieBarOptions(resultMemBean).getDiagramType().getValue()));

    return refreshReport(resultMemBean, null, null, false);
  }

  public ManageReportMemoryBean changeDiagramType(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    getInitFormHelperService().initializePieBarOptions(memBean);
    ReportType sqt = ReportType.fromValue(getResultFormat(GraphicalOptionsGetter.getPieBarOptions(memBean).getDiagramType()));
    memBean.setReportType(sqt);
    return memBean;
  }

  @Override
  protected ManageReportMemoryBean getMemBeanForChangedQueryType(ManageReportMemoryBean memBean) {
    ManageReportMemoryBean newMemBean = null;
    final IGraphicalExportBaseOptions pieBarOptions = memBean.getGraphicalOptions();
    final String selectedType = pieBarOptions.getSelectedBbType();

    newMemBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(selectedType, Constants.ALL_TYPES_FOR_DISPLAY);

    if (newMemBean == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    final List<SavedQuery> savedQueries = getSavedQueryService().getSavedQueriesWithoutContent(Sets.newHashSet(ReportType.PIE, ReportType.BAR));
    newMemBean.setSavedQueries(savedQueries);

    newMemBean.setSelectedBuildingBlock(selectedType);
    newMemBean.setReportType(memBean.getReportType());
    newMemBean.setGraphicalOptions(pieBarOptions);
    getInitFormHelperService().initializePieBarOptions(newMemBean);

    return newMemBean;
  }

  public void refreshColorOptions(ManageReportMemoryBean memBean) {
    PieBarDiagramOptionsBean pieBarOptions = GraphicalOptionsGetter.getPieBarOptions(memBean);

    List<ValuesType> availableValuesTypes = Arrays.asList(pieBarOptions.getAvailableDiagramValuesTypes());
    if (!availableValuesTypes.contains(pieBarOptions.getDiagramValuesType())) {
      pieBarOptions.setDiagramValuesType(availableValuesTypes.get(0));
    }

    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(memBean.getSelectedBuildingBlock());

    getInitFormHelperService().refreshGraphicalExportColorOptionsForPieBar(pieBarOptions.getDiagramValuesType(), pieBarOptions,
        pieBarOptions.getColorOptionsBean(), tobb);

    pieBarOptions.refreshSingleBarValueTypes();
    for (SingleBarOptionsBean sbob : pieBarOptions.getBarsMap().values()) {
      getInitFormHelperService().refreshGraphicalExportColorOptionsForPieBar(sbob.getType(), pieBarOptions, sbob.getColorOptions(), tobb);
    }
  }

  public void changeKeyAssociation(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    PieBarDiagramOptionsBean pieBarOptions = GraphicalOptionsGetter.getPieBarOptions(memBean);
    pieBarOptions.setSelectedLevelRange(pieBarOptions.getAvailableTopLevel() + "_" + pieBarOptions.getAvailableBottomLevel());
    refreshColorOptions(memBean);
  }

  public void changeDiagramKeyType(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    refreshColorOptions(memBean);
  }

  @Override
  protected String getFlowId() {
    return Dialog.GRAPHICAL_REPORTING_PIEBAR.getFlowId();
  }

  public ManageReportMemoryBean stepOneToStepTwo(ManageReportMemoryBean memBean) {
    PieBarDiagramOptionsBean pieBarOptions = GraphicalOptionsGetter.getPieBarOptions(memBean);
    boolean isPieBar = ReportType.PIE.equals(memBean.getReportType()) || ReportType.BAR.equals(memBean.getReportType());
    if (isPieBar && pieBarOptions != null) {
      pieBarOptions.setDialogStep(2);
      pieBarOptions.setNumberOfSelectedElements(memBean.getQueryResult().getSelectedResults().size());
      refreshColorOptions(memBean);

      getInitFormHelperService().initPieBarAssociationMetrics(memBean);
    }
    return memBean;
  }

  public ManageReportMemoryBean stepTwoToStepOne(ManageReportMemoryBean memBean) {
    boolean isPieBar = ReportType.PIE.equals(memBean.getReportType()) || ReportType.BAR.equals(memBean.getReportType());
    if (isPieBar && memBean.getGraphicalOptions() != null) {
      PieBarDiagramOptionsBean pieBarOptions = GraphicalOptionsGetter.getPieBarOptions(memBean);
      pieBarOptions.setDialogStep(1);
      pieBarOptions.setDiagramKeyType(DiagramKeyType.ATTRIBUTE_TYPES);
    }
    return memBean;
  }

  @Override
  public void generateGraphicFileResponse(ManageReportMemoryBean memBean, HttpServletRequest request, HttpServletResponse response) {
    PieBarDiagramOptionsBean options = GraphicalOptionsGetter.getPieBarOptions(memBean);

    // Retrieve the application url
    String srvAddr = URLBuilder.getApplicationURL(request);
    options.setServerUrl(srvAddr);

    // refresh color options bean to avoid problems when user changed language settings while configuring the diagram
    refreshColorOptions(memBean);

    // Generate
    Document doc = getExportService().generateSvgPieBarDiagramExport(memBean.getQueryResult().getSelectedResults(), options);
    GraphicExportBean vBean = new GraphicExportBean(doc);

    GraphicsResponseGenerator respGen = getExportService().getResponseGenerator(options.getSelectedGraphicFormat());
    // have the report file written to the HttpServletResponse
    GraphicalReport reportType;
    switch (options.getDiagramType()) {
      case BAR:
        reportType = GraphicsResponseGenerator.GraphicalReport.BAR;
        break;
      case PIE:
        reportType = GraphicsResponseGenerator.GraphicalReport.PIE;
        break;
      default:
        LOGGER.error("Invalid diagram type: {0}", options.getDiagramType().name());
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
    respGen.generateResponse(response, vBean, reportType, memBean.getContent());
  }

  public void requestEntityList(ManageReportMemoryBean memBean) {
    String resultFormat = getResultFormat(GraphicalOptionsGetter.getPieBarOptions(memBean).getDiagramType());
    ReportType sqt = ReportType.fromValue(resultFormat);
    getQueryService().requestEntityList(memBean, sqt);
  }

  private String getResultFormat(DiagramType diagramType) {
    switch (diagramType) {
      case BAR:
        return ReportType.BAR.getValue();
      case PIE:
        return ReportType.PIE.getValue();
      default:
        LOGGER.error("Invalid diagram type: {0}", diagramType.name());
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  @Override
  protected ReportType getReportType() {
    // not used
    return null;
  }

  @Override
  public ManageReportMemoryBean refreshReport(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext,
                                              boolean resetResults) {
    ManageReportMemoryBean newMemBean = super.refreshReport(memBean, context, flowContext, resetResults);

    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(memBean.getSelectedBuildingBlock());
    PieBarDiagramOptionsBean pbOptions = GraphicalOptionsGetter.getPieBarOptions(newMemBean);

    getInitFormHelperService().refreshGraphicalExportColorOptionsForPieBar(pbOptions.getDiagramValuesType(), pbOptions,
        pbOptions.getColorOptionsBean(), tobb);

    for (SingleBarOptionsBean sbob : pbOptions.getBarsMap().values()) {
      getInitFormHelperService().refreshGraphicalExportColorOptionsForPieBar(sbob.getType(), pbOptions, sbob.getColorOptions(), tobb);
    }

    return newMemBean;
  }

  @Override
  public ManageReportMemoryBean loadSavedQuery(ManageReportMemoryBean memBean) {
    SavedQuery query = getSavedQueryService().getSavedQuery(memBean.getSavedQueryId());
    memBean.setReportType(query.getType());
    return super.loadSavedQuery(memBean);
  }

}

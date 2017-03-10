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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.Line;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Line.LineOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.service.FastExportService;
import de.iteratec.iteraplan.businesslogic.service.GeneralBuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.businesslogic.service.TimeseriesService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.Timeseries;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.sorting.BuildingBlockComparator;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicExportBean;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicalReportBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator;
import de.iteratec.visualizationmodel.jfreechart.JFreeChartSymbol;


@Service("lineGraphicFrontendService")
public class LineGraphicFrontendServiceImpl extends GraphicalReportBaseFrontendServiceImpl<ManageReportMemoryBean> implements
    LineGraphicFrontendService {

  @Autowired
  private FastExportService           fastExportService;

  @Autowired
  private GeneralBuildingBlockService generalBuildingBlockService;

  @Autowired
  private AttributeTypeDAO            attributeTypeDao;

  @Autowired
  private TimeseriesService           timeseriesService;

  @Autowired
  private InitFormHelperService       initFormHelperService;

  public ManageReportMemoryBean getInitialMemBean() {
    List<String> bbTypesWithTimeseries = getBbTypesWithTimeseries();
    LineOptionsBean lineOptions = new LineOptionsBean();
    if (bbTypesWithTimeseries.isEmpty()) {
      ManageReportMemoryBean emptyMemBean = new ManageReportMemoryBean();
      lineOptions.setTimeseriesAttributesActive(false);
      emptyMemBean.setGraphicalOptions(lineOptions);
      return emptyMemBean;
    }
    lineOptions.setTimeseriesAttributesActive(true);
    ManageReportMemoryBean memBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL,
        getBbTypesWithTimeseries());

    lineOptions.setAvailableBbTypes(getBbTypesWithTimeseries());
    memBean.setGraphicalOptions(lineOptions);
    memBean.getGraphicalOptions().setSelectedBbType(memBean.getSelectedBuildingBlock());

    lineOptions.setSelectedBbType(memBean.getSelectedBuildingBlock());
    memBean.setReportType(getReportType());
    memBean.setSavedQueries(getSavedQueryService().getSavedQueriesWithoutContent(getReportType()));
    memBean.resetPostProcessingStrategies();

    return memBean;
  }

  @Override
  protected String getFlowId() {
    return Dialog.GRAPHICAL_REPORTING_LINE.getFlowId();
  }

  public ManageReportMemoryBean fromInterchange(String bbType, String idList) {
    if (idList == null || bbType == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }

    ManageReportMemoryBean resultMemBean = getInitialMemBean();
    GraphicalOptionsGetter.getLineOptions(resultMemBean).setSelectedBbType(bbType);
    resultMemBean = getMemBeanForChangedQueryType(resultMemBean);

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

  public ManageReportMemoryBean stepOneToStepTwo(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    if (getReportType().equals(memBean.getReportType()) && memBean.getGraphicalOptions() != null) {
      memBean.getGraphicalOptions().setDialogStep(2);
    }
    getInitFormHelperService().updateLineOptionAvailableAttributeType(memBean);
    return memBean;
  }

  public ManageReportMemoryBean directlyToStepTwo(Integer id, String bbType, String diagramVariant) {
    ManageReportMemoryBean resultMemBean = getInitialMemBean();

    TypeOfBuildingBlock tob = TypeOfBuildingBlock.fromInitialCapString(bbType);
    switch (tob) {
      case INFORMATIONSYSTEMRELEASE:
        resultMemBean.getGraphicalOptions().setSelectedBbType(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL);
        break;
      case PROJECT:
        resultMemBean.getGraphicalOptions().setSelectedBbType(Constants.BB_PROJECT_PLURAL);
        break;
      case BUSINESSPROCESS:
        resultMemBean.getGraphicalOptions().setSelectedBbType(Constants.BB_BUSINESSPROCESS_PLURAL);
        break;
      default:
        throw new IteraplanBusinessException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }
    resultMemBean = getMemBeanForChangedQueryType(resultMemBean);
    resultMemBean.resetPostProcessingStrategies();

    BuildingBlock startElement = fastExportService.getStartElement(id, bbType);
    List<BuildingBlock> listReleases = fastExportService.retrieveBuildingBlockListForMasterplanFastExport(startElement, null, diagramVariant);

    Integer[] selectedResultIds = GeneralHelper.createIdArrayFromIdEntities(listReleases);

    resultMemBean.setResults(listReleases);
    resultMemBean.getQueryResult().setSelectedResultIds(selectedResultIds);
    resultMemBean = stepOneToStepTwo(resultMemBean, null, null);

    return resultMemBean;
  }

  public ManageReportMemoryBean stepTwoToStepOne(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {

    if (getReportType().equals(memBean.getReportType()) && memBean.getGraphicalOptions() != null) {
      memBean.getGraphicalOptions().setDialogStep(1);
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_UNKNOWN_RESULT_TYPE);
    }

    return memBean;
  }

  @Override
  protected ManageReportMemoryBean getMemBeanForChangedQueryType(ManageReportMemoryBean memBean) {
    ManageReportMemoryBean newMemBean = null;
    LineOptionsBean lineOptions = new LineOptionsBean();
    if (memBean.getGraphicalOptions() instanceof LineOptionsBean) {
      lineOptions = (LineOptionsBean) memBean.getGraphicalOptions();
    }
    String selectedType = lineOptions.getSelectedBbType();
    lineOptions.setAvailableBbTypes(getBbTypesWithTimeseries());

    newMemBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(selectedType, getBbTypesWithTimeseries());

    newMemBean.setSelectedBuildingBlock(selectedType);

    List<SavedQuery> savedQueries = getSavedQueryService().getSavedQueriesWithoutContent(getReportType());
    newMemBean.setSavedQueries(savedQueries);

    newMemBean.setGraphicalOptions(lineOptions);

    newMemBean.setReportType(getReportType());

    return newMemBean;
  }

  @Override
  public void generateGraphicFileResponse(ManageReportMemoryBean memBean, HttpServletRequest request, HttpServletResponse response) {

    LineOptionsBean options = GraphicalOptionsGetter.getLineOptions(memBean);
    options.setAvailableBbTypes(getBbTypesWithTimeseries());
    if (DateUtils.parseAsDate(options.getStartDateString(), UserContext.getCurrentLocale()) == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NOT_NULL_RANGE_FIELD_EXCEPTION);
    }
    List<? extends BuildingBlock> selectedResults = memBean.getQueryResult().getSelectedResults();
    AttributeType attributeType = attributeTypeDao.loadObjectById(Integer.valueOf(options.getSelectedKeyAttributeTypeId()));
    List<Timeseries> timeseries = getListOfTimeseries(selectedResults, attributeType);

    JFreeChartLineGraphicCreator jFreeChartLineGraphicCreator = new JFreeChartLineGraphicCreator();
    JFreeChartSymbol jFreeChartSymbol = jFreeChartLineGraphicCreator.generateJFreeChartSymbol(timeseries, attributeType,
        options.getStartDateString(), options.getEndDateString());
    Date fromDate = jFreeChartLineGraphicCreator.getFromDate();
    Date toDate = jFreeChartLineGraphicCreator.getToDate();

    IGraphicalExportBaseOptions graphicalOptions = memBean.getGraphicalOptions();
    float width = jFreeChartSymbol.getWidth();
    float height = jFreeChartSymbol.getHeight();
    float ratio = width / height;
    if (graphicalOptions.getWidth() != null) {
      width = graphicalOptions.getWidth().floatValue();
      height = width / ratio;
    }
    else if (graphicalOptions.getHeight() != null) {
      height = graphicalOptions.getHeight().floatValue();
      width = height * ratio;
    }

    JFreeChartSvgRenderer renderer = new JFreeChartSvgRenderer();
    GraphicExportBean vBean = null;
    try {
      byte[] jFreeChartAsByte = renderer.renderJFreeChart(jFreeChartSymbol.getJFreeChart(), width, height, options.isNakedExport(), fromDate, toDate);
      vBean = new GraphicExportBean(jFreeChartAsByte);
    } catch (IOException e) {
      throw new IteraplanTechnicalException(e);
    }

    // Retrieve the application url
    String srvAddr = URLBuilder.getApplicationURL(request);
    options.setServerUrl(srvAddr);

    if (vBean != null) {
      GraphicsResponseGenerator respGen = getExportService().getResponseGenerator(options.getSelectedGraphicFormat());
      // have the report file written to the HttpServletResponse
      respGen.generateResponse(response, vBean, GraphicsResponseGenerator.GraphicalReport.LINE, memBean.getContent());
    }
  }

  /**
   * @param selectedResults
   * @param attributeType
   * @return list of {@link Timeseries} for the selected {@link BuildingBlock}s
   */
  private List<Timeseries> getListOfTimeseries(List<? extends BuildingBlock> selectedResults, AttributeType attributeType) {
    List<Timeseries> timeseriesList = new ArrayList<Timeseries>();
    for (BuildingBlock bb : selectedResults) {
      Timeseries timeseries = timeseriesService.loadTimeseriesByBuildingBlockAndAttributeType(bb, attributeType);
      if (timeseries != null) {
        timeseriesList.add(timeseries);
      }
    }
    return timeseriesList;
  }

  @Override
  protected ReportType getReportType() {
    return ReportType.LINE;
  }

  @Override
  public ManageReportMemoryBean loadSavedQuery(ManageReportMemoryBean memBean) {
    SavedQuery query = getSavedQueryService().getSavedQuery(memBean.getSavedQueryId());
    memBean.setReportType(query.getType());
    return super.loadSavedQuery(memBean);
  }

  private List<String> getBbTypesWithTimeseries() {
    return initFormHelperService.getBbTypesWithTimeseries();
  }
}

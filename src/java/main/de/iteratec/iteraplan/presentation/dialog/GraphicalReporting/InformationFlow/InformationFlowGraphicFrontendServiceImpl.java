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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.InformationFlow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportBeanBase;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalOptionsGetter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.IGraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExportOption;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessObjectTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemInterfaceTypeQu;
import de.iteratec.iteraplan.businesslogic.service.FastExportService;
import de.iteratec.iteraplan.businesslogic.service.GeneralBuildingBlockService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.sorting.BuildingBlockComparator;
import de.iteratec.iteraplan.model.xml.ReportXML;
import de.iteratec.iteraplan.model.xml.query.QueryFormXML;
import de.iteratec.iteraplan.model.xml.query.QueryResultXML;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicExportBean;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicalReportBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator;


@Service("informationFlowGraphicFrontendService")
public class InformationFlowGraphicFrontendServiceImpl extends GraphicalReportBaseFrontendServiceImpl<ManageReportMemoryBean> implements
    InformationFlowGraphicFrontendService {

  @Autowired
  private GeneralBuildingBlockService generalBuildingBlockService;

  @Autowired
  private FastExportService           fastExportService;

  public ManageReportMemoryBean stepOneToStepTwo(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    if (memBean.getQueryResult().getSelectedResultIds().length <= 0) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INFORMATIONFLOW_NO_ELEMENTS);
    }

    int maxInformationSystems = IteraplanProperties.getIntProperty(IteraplanProperties.PROP_MAX_EXPORT_VISIO_INFORMATIONSTSTEMS);
    if (memBean.getQueryResult().getSelectedResultIds().length > maxInformationSystems) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INFORMATIONFLOW_TOO_MANY_ELEMENTS, Integer.valueOf(maxInformationSystems));
    }
    if (getReportType().equals(memBean.getReportType()) && memBean.getGraphicalOptions() != null) {
      memBean.getGraphicalOptions().setDialogStep(2);
    }

    initializeInterfacesQuery(memBean, context, flowContext);
    initializeBusinessObjectsQuery(memBean, context, flowContext);

    return memBean;
  }

  public ManageReportMemoryBean directlyToStepTwo(Integer id, String bbType) {
    if (id == null || bbType == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }

    BuildingBlock startElement = fastExportService.getStartElement(id, bbType);
    List<InformationSystemRelease> infoSysReleases = fastExportService.getInformationFlowReleases(startElement);

    Integer[] selectedResultIds = GeneralHelper.createIdArrayFromIdEntities(infoSysReleases);

    return fromInterchange(selectedResultIds);
  }

  public ManageReportMemoryBean fromInterchange(String idList) {
    if (idList == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }

    List<Integer> idsAsList = parseSelectedIds(idList);
    Integer[] ids = new Integer[idsAsList.size()];
    for (int i = 0; i < idsAsList.size(); i++) {
      ids[i] = idsAsList.get(i);
    }

    return fromInterchange(ids);
  }

  private ManageReportMemoryBean fromInterchange(Integer[] ids) {
    ManageReportMemoryBean resultMemBean = getInitialMemBean();
    getInitFormHelperService().dropRestrictionsFromQueryForm(resultMemBean.getQueryResult().getQueryForms().get(0));

    List<BuildingBlock> allReleases = generalBuildingBlockService.getBuildingBlocksByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    Collections.sort(allReleases, new BuildingBlockComparator());

    List<DynamicQueryFormData<?>> queryForms = resultMemBean.getQueryResult().getQueryForms();
    resultMemBean.setQueryResult(new QueryResult(ManageReportBeanBase.MAIN_QUERY, queryForms, resultMemBean.getQueryResult().getTimeseriesQuery(),
        allReleases, ids, getReportType().getValue()));
    refreshReport(resultMemBean, null, null, false);
    resultMemBean = stepOneToStepTwo(resultMemBean, null, null);

    return resultMemBean;
  }

  public ManageReportMemoryBean stepTwoToStepOne(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    if (getReportType().equals(memBean.getReportType()) && memBean.getGraphicalOptions() != null) {
      memBean.getGraphicalOptions().setDialogStep(1);
    }
    return memBean;
  }

  @Override
  public void generateGraphicFileResponse(ManageReportMemoryBean memBean, HttpServletRequest request, HttpServletResponse response) {
    InformationFlowOptionsBean options = GraphicalOptionsGetter.getInformationFlowOptions(memBean);

    String templateFileName = options.getSelectedTemplateName();
    if (templateFileName != null && !InformationFlowOptionsBean.DUMMY_TEMPLATE_NAME.equals(templateFileName)) {
      File templateFile = getTemplateLocatorService().getFile(TemplateType.INFOFLOW, templateFileName);
      options.setSelectedTemplateFile(templateFile);
    }
    else {
      options.setSelectedTemplateFile(null);
    }

    // Retrieve the current application url (needed as shape export information)
    String srvAddr = URLBuilder.getApplicationURL(request);
    options.setServerUrl(srvAddr);

    int maxInformationSystems = IteraplanProperties.getIntProperty(IteraplanProperties.PROP_MAX_EXPORT_VISIO_INFORMATIONSTSTEMS);
    if (memBean.getQueryResult().getSelectedResultIds().length > maxInformationSystems) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INFORMATIONFLOW_TOO_MANY_ELEMENTS,
          new Object[] { Integer.valueOf(maxInformationSystems) });
    }

    boolean ppConnectionMerging = ppConnectionMergingSelected(memBean.getQueryResult().getSelectedPostProcessingStrategies());

    if (options.getSelectedGraphicFormat().equalsIgnoreCase(Constants.REPORTS_EXPORT_GRAPHICAL_VISIO)) {
      generateVisioExport(memBean, ppConnectionMerging, options, response);
    }
    else {
      generateSvgBasedExport(memBean, ppConnectionMerging, options, response);
    }
  }

  /**
   * 
   * @param strategies
   * @return Determines whether the post processing strategy concerned with the aggregation of interfaces of hierarchical descendants is selected.
   */
  private boolean ppConnectionMergingSelected(List<AbstractPostprocessingStrategy<? extends BuildingBlock>> strategies) {

    for (AbstractPostprocessingStrategy<? extends BuildingBlock> strategy : strategies) {
      if (strategy.getNameKeyForPresentation().equals(Constants.POSTPROCESSINGSTRATEGY_HIDE_CHILDREN_MERGE)) {
        return true;
      }
    }
    return false;
  }

  public ManageReportMemoryBean getInitialMemBean() {
    ManageReportMemoryBean memBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL,
        ImmutableList.of(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL));

    memBean.setReportType(getReportType());

    InformationSystemInterfaceTypeQu type = InformationSystemInterfaceTypeQu.getInstance();
    List<BBAttribute> lineCaptionAttributes = getInitFormHelperService().getUserdefAttributes(type);

    InformationFlowOptionsBean informationFlowOptions = GraphicalOptionsGetter.getInformationFlowOptions(memBean);
    informationFlowOptions.setIsiAttributes(lineCaptionAttributes);
    informationFlowOptions.setAvailableLayoutTemplates(createTemplateOptionsList(TemplateType.INFOFLOW));

    memBean.setSavedQueries(getSavedQueryService().getSavedQueriesWithoutContent(getReportType()));
    memBean.resetPostProcessingStrategies();

    return memBean;
  }

  @Override
  protected ManageReportMemoryBean initializeMemBeanFromSavedQuery(ManageReportMemoryBean memBean, SavedQuery savedQuery, ReportXML savedReport) {
    //ITERAPLAN-1913, ITERAPLAN-1851 backward-compatibility: explicitly initialize queryforms on interfaces and businessobjects if empty to have the result type of those queries initialized (needed by the timeseries query)
    for (QueryResultXML result : savedReport.getQueryResults()) {
      if (result.getQueryForms().isEmpty()) {
        QueryFormXML form = new QueryFormXML();
        if (result.getQueryName().equals(InformationFlowOptionsBean.INTERFACE_QUERY)) {
          form.setType(InformationSystemInterfaceTypeQu.getInstance());
        }
        else if (result.getQueryName().equals(InformationFlowOptionsBean.BUSINESSOBJECT_QUERY)) {
          form.setType(BusinessObjectTypeQu.getInstance());
        }
        result.getQueryForms().add(form);
      }
    }

    ManageReportMemoryBean newMemBean = super.initializeMemBeanFromSavedQuery(memBean, savedQuery, savedReport);

    InformationFlowOptionsBean options = GraphicalOptionsGetter.getInformationFlowOptions(newMemBean);
    options.setAvailableLayoutTemplates(createTemplateOptionsList(TemplateType.INFOFLOW));

    String savedSelectedTemplateName = savedReport.getInformationFlowOptions().getSelectedTemplateName();
    ExportOption savedSelectedOption = new ExportOption(savedSelectedTemplateName);
    if (options.getAvailableLayoutTemplates().contains(savedSelectedOption)) {
      options.setSelectedTemplateName(savedSelectedTemplateName);
    }

    return newMemBean;
  }

  @Override
  protected ManageReportMemoryBean getMemBeanForChangedQueryType(ManageReportMemoryBean memBean) {
    // never called for information flow diagram
    return null;
  }

  @Override
  protected void refreshGraphicalOptions(ManageReportMemoryBean memBean) {
    IGraphicalExportBaseOptions graphicalOptions = memBean.getGraphicalOptions();
    if (graphicalOptions != null) {
      // refresh options
      TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(memBean.getSelectedBuildingBlock());

      getInitFormHelperService().refreshGraphicalExportColorOptions(graphicalOptions.getColorOptionsBean(), tobb);
      getInitFormHelperService().refreshGraphicalExportLineTypeOptions(graphicalOptions.getLineOptionsBean(),
          TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE);

    }
  }

  @Override
  protected String getFlowId() {
    return Dialog.GRAPHICAL_REPORTING_INFORMATIONFLOW.getFlowId();
  }

  @SuppressWarnings("unchecked")
  private void generateVisioExport(ManageReportMemoryBean memBean, boolean ppConnectionMerging, InformationFlowOptionsBean options,
                                   HttpServletResponse response) {

    List<InformationSystemRelease> selectedIsrResults = (List<InformationSystemRelease>) memBean.getQueryResult().getSelectedResults();

    List<InformationSystemInterface> selectedIsiResults = determineSelectedISIs(memBean);
    List<BusinessObject> selectedBoResults = determineSelectedBOs(memBean);

    de.iteratec.visio.model.Document doc = getExportService().generateVisioInformationFlowDoc(selectedIsrResults, selectedIsiResults,
        selectedBoResults, ppConnectionMerging, options);

    GraphicExportBean vBean = new GraphicExportBean(doc);

    GraphicsResponseGenerator respGen = getExportService().getResponseGenerator(options.getSelectedGraphicFormat());
    // have the report file written to the HttpServletResponse
    respGen.generateResponse(response, vBean, GraphicsResponseGenerator.GraphicalReport.INFORMATIONFLOW, memBean.getContent());

  }

  private List<InformationSystemInterface> determineSelectedISIs(ManageReportMemoryBean memBean) {
    List<InformationSystemInterface> selectedIsiResults = null;
    QueryResult queryResult = memBean.getQueryResult(InformationFlowOptionsBean.INTERFACE_QUERY);
    if (queryResult != null && !queryResult.getQueryForms().isEmpty()) {
      selectedIsiResults = (List<InformationSystemInterface>) queryResult.getSelectedResults();
    }
    return selectedIsiResults;
  }

  private List<BusinessObject> determineSelectedBOs(ManageReportMemoryBean memBean) {
    List<BusinessObject> selectedBoResults = null;
    QueryResult queryResult = memBean.getQueryResult(InformationFlowOptionsBean.BUSINESSOBJECT_QUERY);
    if (queryResult != null && !queryResult.getQueryForms().isEmpty()) {
      selectedBoResults = (List<BusinessObject>) queryResult.getSelectedResults();
    }
    return selectedBoResults;
  }

  @SuppressWarnings("unchecked")
  private void generateSvgBasedExport(ManageReportMemoryBean memBean, boolean ppConnectionMerging, InformationFlowOptionsBean options,
                                      HttpServletResponse response) {

    List<InformationSystemRelease> selectedIsrResults = (List<InformationSystemRelease>) memBean.getQueryResult().getSelectedResults();

    List<InformationSystemInterface> selectedIsiResults = determineSelectedISIs(memBean);
    List<BusinessObject> selectedBoResults = determineSelectedBOs(memBean);

    de.iteratec.svg.model.Document doc = getExportService().generateSvgInformationFlowDoc(selectedIsrResults, selectedIsiResults, selectedBoResults,
        ppConnectionMerging, options);

    GraphicExportBean sBean = new GraphicExportBean(doc);

    GraphicsResponseGenerator respGen = getExportService().getResponseGenerator(options.getSelectedGraphicFormat());
    // have the report file written to the HttpServletResponse
    respGen.generateResponse(response, sBean, GraphicsResponseGenerator.GraphicalReport.INFORMATIONFLOW, memBean.getContent());

  }

  /**
   * {@inheritDoc}
   */
  public ManageReportMemoryBean filterInterfaces(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    getInitFormHelperService().switchQuery(memBean, InformationFlowOptionsBean.INTERFACE_QUERY);
    return memBean;
  }

  private void initializeInterfacesQuery(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    String name = memBean.getQueryResultName();
    memBean.setQueryResultName(InformationFlowOptionsBean.INTERFACE_QUERY);

    if (memBean.getQueryResult().getQueryForms().isEmpty()) {
      InformationSystemInterfaceTypeQu type = InformationSystemInterfaceTypeQu.getInstance();
      DynamicQueryFormData<?> reportForm = getInitFormHelperService().getReportForm(type);
      List<DynamicQueryFormData<?>> queryForms = new ArrayList<DynamicQueryFormData<?>>();
      queryForms.add(reportForm);

      List<? extends BuildingBlock> list = Lists.newArrayList();
      ReportType reportType = ReportType.INFORMATIONSYSTEMINTERFACE;
      QueryResult queryResult = new QueryResult(InformationFlowOptionsBean.INTERFACE_QUERY, queryForms, getInitFormHelperService()
          .getTimeseriesQuery(type), list, new Integer[0], reportType.getValue());
      memBean.setQueryResult(queryResult);
    }

    requestEntityList(memBean, context, flowContext);
    memBean.setQueryResultName(name);
  }

  public ManageReportMemoryBean filterBusinessObjects(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    getInitFormHelperService().switchQuery(memBean, InformationFlowOptionsBean.BUSINESSOBJECT_QUERY);
    return memBean;
  }

  private void initializeBusinessObjectsQuery(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    String name = memBean.getQueryResultName();
    memBean.setQueryResultName(InformationFlowOptionsBean.BUSINESSOBJECT_QUERY);

    if (memBean.getQueryResult().getQueryForms().isEmpty()) {
      BusinessObjectTypeQu type = BusinessObjectTypeQu.getInstance();
      DynamicQueryFormData<?> reportForm = getInitFormHelperService().getReportForm(type);
      List<DynamicQueryFormData<?>> queryForms = new ArrayList<DynamicQueryFormData<?>>();
      queryForms.add(reportForm);

      List<? extends BuildingBlock> list = Lists.newArrayList();
      ReportType reportType = ReportType.BUSINESSOBJECT;
      QueryResult queryResult = new QueryResult(InformationFlowOptionsBean.BUSINESSOBJECT_QUERY, queryForms, getInitFormHelperService()
          .getTimeseriesQuery(type), list, new Integer[0], reportType.getValue());
      memBean.setQueryResult(queryResult);
    }

    requestEntityList(memBean, context, flowContext);
    memBean.setQueryResultName(name);
  }

  /**
   * {@inheritDoc}
   */
  public ManageReportMemoryBean resetInterfacesFilter(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    initializeInterfacesQuery(memBean, context, flowContext);
    return memBean;
  }

  public ManageReportMemoryBean resetBusinessObjectsFilter(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    initializeBusinessObjectsQuery(memBean, context, flowContext);
    return memBean;
  }

  public ManageReportMemoryBean resumeFromFilter(ManageReportMemoryBean memBean) {
    getInitFormHelperService().switchQuery(memBean, ManageReportBeanBase.MAIN_QUERY);
    return memBean;
  }

  public void refreshRelevantInterfaces(ManageReportMemoryBean memBean) {
    InformationFlowOptionsBean options = GraphicalOptionsGetter.getInformationFlowOptions(memBean);

    Set<BuildingBlock> isrs = Sets.newHashSet(memBean.getQueryResult().getSelectedResults());

    QueryResult interfaceQuery = memBean.getQueryResult(InformationFlowOptionsBean.INTERFACE_QUERY);
    if (interfaceQuery == null || interfaceQuery.getQueryForms().isEmpty()) {
      options.setRelevantInterfaces(null);
      return;
    }

    List<InformationSystemInterface> interfaces = Lists.newArrayList();
    for (BuildingBlock bb : interfaceQuery.getSelectedResults()) {
      if (bb instanceof InformationSystemInterface) {
        InformationSystemInterface isi = (InformationSystemInterface) bb;
        if (isrs.contains(isi.getInformationSystemReleaseA()) && isrs.contains(isi.getInformationSystemReleaseB())) {
          interfaces.add(isi);
        }
      }
    }

    options.setRelevantInterfaces(interfaces);
  }

  public void refreshRelevantBusinessObjects(ManageReportMemoryBean memBean) {
    InformationFlowOptionsBean options = GraphicalOptionsGetter.getInformationFlowOptions(memBean);

    QueryResult busniessObjectQuery = memBean.getQueryResult(InformationFlowOptionsBean.BUSINESSOBJECT_QUERY);
    if (busniessObjectQuery == null || busniessObjectQuery.getQueryForms().isEmpty()) {
      options.setRelevantBusinessObjects(null);
      return;
    }

    List<BusinessObject> businessObjects = Lists.newArrayList();
    for (BuildingBlock bb : busniessObjectQuery.getSelectedResults()) {
      if (bb instanceof BusinessObject) {
        BusinessObject bo = (BusinessObject) bb;
        businessObjects.add(bo);
      }
    }

    options.setRelevantBusinessObjects(businessObjects);
  }

  public ManageReportMemoryBean resetReport(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    memBean.resetResults();
    return memBean;
  }

  @Override
  protected ReportType getReportType() {
    return ReportType.INFORMATIONFLOW;
  }
}

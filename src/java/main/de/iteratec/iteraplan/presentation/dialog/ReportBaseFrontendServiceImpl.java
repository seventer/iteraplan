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
package de.iteratec.iteraplan.presentation.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateInfo;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateLocatorService;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.businesslogic.reports.query.QueryTreeGenerator;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry.COLUMN_TYPE;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportBeanBase;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ViewConfiguration;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExportOption;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExtensionFormHelper;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.FormModificationInfo;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QFirstLevel;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.type.IPresentationExtension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateTypeHelper;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.CustomDashboardInstanceService;
import de.iteratec.iteraplan.businesslogic.service.ExportService;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.businesslogic.service.QueryService;
import de.iteratec.iteraplan.businesslogic.service.RefreshHelperService;
import de.iteratec.iteraplan.businesslogic.service.SavedQueryService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.queries.CustomDashboardInstance;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.xml.ReportXML;
import de.iteratec.iteraplan.model.xml.query.ColumnEntryXML;
import de.iteratec.iteraplan.model.xml.query.QueryFormXML;
import de.iteratec.iteraplan.model.xml.query.QueryResultXML;


/**
 * Should be extended by all Frontend-Services that use reports.
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class ReportBaseFrontendServiceImpl<E extends ManageReportMemoryBean> extends CommonFrontendServiceImpl implements
    ReportBaseFrontendService<E> {

  /** Flash scoped attribute name for the attribute, which indicates that saving a report was successful */
  public static final String             FLUSHATTRIBUTE_SAVE_SUCCESSFUL_TRIGGER = "SAVE_SUCCESSFUL_TRIGGER";

  @Autowired
  private QueryService                   queryService;

  @Autowired
  private InitFormHelperService          initFormHelperService;

  @Autowired
  private RefreshHelperService           refreshHelperService;

  @Autowired
  private ExtensionFormHelper            extensionFormHelper;

  @Autowired
  private SavedQueryService              savedQueryService;

  @Autowired
  private CustomDashboardInstanceService customDashboardService;

  @Autowired
  private ExportService                  exportService;
  @Autowired
  private AttributeTypeService           attributeTypeService;

  /** Helps to find the excel template files. */
  @Autowired
  private TemplateLocatorService         templateLocatorService;

  public void setQueryService(QueryService queryService) {
    this.queryService = queryService;
  }

  public void setInitFormHelperService(InitFormHelperService initFormHelperService) {
    this.initFormHelperService = initFormHelperService;
  }

  public void setRefreshHelperService(RefreshHelperService refreshHelperService) {
    this.refreshHelperService = refreshHelperService;
  }

  public void setExtensionFormHelper(ExtensionFormHelper extensionFormHelper) {
    this.extensionFormHelper = extensionFormHelper;
  }

  public void setSavedQueryService(SavedQueryService savedQueryService) {
    this.savedQueryService = savedQueryService;
  }

  public void setExportService(ExportService exportService) {
    this.exportService = exportService;
  }

  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }

  public E changeQueryType(E memBean, RequestContext context, FlowExecutionContext flowContext) {
    E newMemBean = getMemBeanForChangedQueryType(memBean);
    newMemBean.setReportType(memBean.getReportType());
    newMemBean.resetPostProcessingStrategies();
    return newMemBean;
  }

  /**
   * Responsible for creating a newly initialized memory bean after changing the query type of the report.
   * @param memBean
   *          original memory bean
   * @return the new memory bean
   */
  protected abstract E getMemBeanForChangedQueryType(E memBean);

  public boolean requestReport(E memBean, RequestContext context, FlowExecutionContext flowContext) {
    memBean.clearErrors(); // first clear the error list
    HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getNativeRequest();
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();

    boolean result = requestReport(memBean, request, response);

    //check if report type is HTML, because with HTML we don't need any download trigger set
    if (!ReportType.HTML.toString().equals(memBean.getTabularOptions().getResultFormat())) {
      markFinished(context); // this must be done!
    }

    return result;
  }

  public boolean requestReport(E memBean, HttpServletRequest request, HttpServletResponse response) {
    // Generate the tree of nodes for processing the query.
    QueryTreeGenerator qtg = new QueryTreeGenerator(UserContext.getCurrentLocale(), attributeTypeService);
    Node node = qtg.generateQueryTree(memBean.getQueryResult().getQueryForms());

    return createReport(memBean, node, request, response);
  }

  protected boolean createReport(E memBean, Node node, HttpServletRequest request, HttpServletResponse response) {
    List<AbstractPostprocessingStrategy<? extends BuildingBlock>> strategies = getStrategies(memBean);
    List<AbstractPostprocessingStrategy<BuildingBlock>> listWithoutWildcard = getQueryService().disposeOfWildcard(strategies, BuildingBlock.class);

    List<? extends BuildingBlock> results = queryService.evaluateQueryTree(node, memBean.getQueryResult().getTimeseriesQuery(), listWithoutWildcard);

    List<ColumnEntry> visibleColumns = memBean.getViewConfiguration().getVisibleColumns();
    HibernateLazyInitializationHelper.initialize(results, visibleColumns);
    memBean.setResults(results);

    return true;
  }

  protected List<AbstractPostprocessingStrategy<? extends BuildingBlock>> getStrategies(E memBean) {
    Type<?> reportResultType = memBean.getReportResultType();

    List<AbstractPostprocessingStrategy<? extends BuildingBlock>> strategies = memBean.getQueryResult().getSelectedPostProcessingStrategies();

    // Add a post-processing strategy in case an ordered hierarchy has been queried.
    if (reportResultType.isOrderedHierarchy()) {
      // remove virtual top level elements
      strategies.add(reportResultType.getOrderedHierarchyRemoveRootElementStrategy());
    }
    return strategies;
  }

  /**
   * Implement to return the {@link ReportType} this frontend service handles.
   * @return the {@link ReportType}
   */
  protected abstract ReportType getReportType();

  /**
   * Helper method for checking the correct report type. Can be used in an implementation of the
   * abstract method checkResultFormat(memBean). If the mem bean's report type is not equal to the
   * key, a technical exception is thrown.
   * 
   * @param memBean
   *          The backing memory bean.
   * @param key
   *          The corresponding key of the application ressources (e.g.
   *          Constants.REPORTS_EXPORT_GRAPHICAL_LANDSCAPE)
   */
  protected void checkReportTypeHelper(E memBean, String key) {
    {
      String reportType = memBean.getReportType().getValue();
      if (!reportType.equals(key)) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }
    }
  }

  public void requestEntityList(E memBean, RequestContext context, FlowExecutionContext flowContext) {
    ReportType sqt = getReportType();
    getQueryService().requestEntityList(memBean, sqt);
  }

  public void updateCheckAllBox(E memBean) {
    if (memBean.getQueryResult().getSelectedResultIds().length == memBean.getResults().size()) {
      memBean.setCheckAllBox(Boolean.TRUE);
    }
    else {
      memBean.setCheckAllBox(Boolean.FALSE);
    }
  }

  /**
   * {@inheritDoc}
   */
  public E refreshReport(E memBean, RequestContext context, FlowExecutionContext flowContext, boolean resetResults) {
    QueryResult queryResult = memBean.getQueryResult();
    List<DynamicQueryFormData<?>> formList = queryResult.getQueryForms();

    // refresh the forms
    refreshHelperService.refreshAllForms(formList);
    refreshHelperService.refreshTimeseriesQuery(queryResult);

    hideUnsupportedResultFormats(queryResult.getSelectedPostProcessingStrategies(), memBean.getAvailableResultFormats());

    if (resetResults) {
      memBean.resetResults();
    }

    return memBean;
  }

  /**
   * Returns a list of export options for the available templates of the given template type.
   * 
   * @param templateType the {@link TemplateType} the template names should be gathered from.
   * @return List of available template options
   */
  protected List<ExportOption> createTemplateOptionsList(TemplateType templateType) {
    Set<TemplateInfo> templateInfos = getTemplateLocatorService().getTemplateInfos(templateType);
    List<ExportOption> resultFormatTemplates = Lists.newArrayList();
    for (TemplateInfo info : templateInfos) {
      resultFormatTemplates.add(new ExportOption(info.getName()));
    }
    return resultFormatTemplates;
  }

  /**
   * {@inheritDoc}
   */
  public boolean addReportExtension(E memBean, RequestContext context, FlowExecutionContext flowContext) {
    String extension = memBean.getSelectedReportExtension();
    // add to extension and remove from available list.
    QueryResult queryResult = memBean.getQueryResult();
    extensionFormHelper.addReportExtension(extension, queryResult.getQueryForms(), queryResult.getAvailableReportExtensions());

    // the list is emptied when somebody adds a query extension
    List<? extends BuildingBlock> results = new ArrayList<BuildingBlock>();
    memBean.setResults(results);

    return true;
  }

  /**
   * {@inheritDoc}
   */
  public boolean removeReportExtension(E memBean, RequestContext context, FlowExecutionContext flowContext) {
    Integer extensionId = memBean.getReportExtensionToRemove();
    // remove the extension from the form and add it to the available list.
    QueryResult queryResult = memBean.getQueryResult();
    extensionFormHelper.removeExportExtension(queryResult.getQueryForms(), queryResult.getAvailableReportExtensions(), extensionId);

    return true;
  }

  /**
   * {@inheritDoc}
   */
  public E expandFirstLevel(E memBean, RequestContext context, FlowExecutionContext flowContext) {
    FormModificationInfo formModification = memBean.getFormModification();
    Integer queryFormId = formModification.getAffectedQueryFormId();

    if (queryFormId != null) {
      List<DynamicQueryFormData<?>> formList = memBean.getQueryResult().getQueryForms();
      DynamicQueryFormData<?> queryForm = formList.get(queryFormId.intValue());
      Integer secondLevelQueryFormId = formModification.getAffectedSecondLevelQueryFormId();
      // second level query form should be modified
      if (secondLevelQueryFormId != null && secondLevelQueryFormId.intValue() > -1) {
        queryForm = queryForm.getSecondLevelQueryForms().get(secondLevelQueryFormId.intValue());
      }
      queryForm.expandFirstLevel();
    }

    memBean.setFormModification(new FormModificationInfo());
    return memBean;
  }

  /**
   * {@inheritDoc}
   */
  public E expandSecondLevel(E memBean, RequestContext context, FlowExecutionContext flowContext) {
    FormModificationInfo formModification = memBean.getFormModification();
    Integer queryFormId = formModification.getAffectedQueryFormId();
    Integer firstLevelId = formModification.getFirstLevelIdToExpand();

    if (queryFormId != null && firstLevelId != null) {
      List<DynamicQueryFormData<?>> formList = memBean.getQueryResult().getQueryForms();
      DynamicQueryFormData<?> queryForm = formList.get(queryFormId.intValue());
      Integer secondLevelQueryFormId = formModification.getAffectedSecondLevelQueryFormId();
      // second level query form should be modified
      if (secondLevelQueryFormId != null && secondLevelQueryFormId.intValue() > -1) {
        queryForm = queryForm.getSecondLevelQueryForms().get(secondLevelQueryFormId.intValue());
      }
      queryForm.expandSecondLevel(firstLevelId.intValue());
    }

    memBean.setFormModification(new FormModificationInfo());

    return memBean;
  }

  /**
   * {@inheritDoc}
   */
  public E shrinkLevel(E memBean, RequestContext context, FlowExecutionContext flowContext) {
    FormModificationInfo formModification = memBean.getFormModification();
    Integer queryFormId = formModification.getAffectedQueryFormId();
    Integer firstLevelId = formModification.getFirstLevelIdToShrink();
    Integer secondLevelId = formModification.getSecondLevelIdToShrink();

    if (queryFormId != null && firstLevelId != null && secondLevelId != null) {
      List<DynamicQueryFormData<?>> formList = memBean.getQueryResult().getQueryForms();
      DynamicQueryFormData<?> queryForm = formList.get(queryFormId.intValue());
      Integer secondLevelQueryFormId = formModification.getAffectedSecondLevelQueryFormId();
      // second level query form should be modified
      if (secondLevelQueryFormId != null && secondLevelQueryFormId.intValue() > -1) {
        queryForm = queryForm.getSecondLevelQueryForms().get(secondLevelQueryFormId.intValue());
      }
      QFirstLevel firstLevel = queryForm.getQueryUserInput().getQueryFirstLevels().get(firstLevelId.intValue());
      firstLevel.getQuerySecondLevels().remove(secondLevelId.intValue());
      if (firstLevel.getQuerySecondLevels().isEmpty()) {
        queryForm.getQueryUserInput().getQueryFirstLevels().remove(firstLevelId.intValue());
      }
    }

    memBean.setFormModification(new FormModificationInfo());
    return memBean;
  }

  /**
   * {@inheritDoc}
   */
  public void hideUnsupportedResultFormats(List<AbstractPostprocessingStrategy<? extends BuildingBlock>> selectedPps,
                                           List<ExportOption> availableResultFormats) {
    for (ExportOption exOpt : availableResultFormats) {
      exOpt.setVisible(true);
    }
    for (AbstractPostprocessingStrategy<? extends BuildingBlock> ppsElement : selectedPps) {
      String[] supportedExportFormats = ppsElement.getSupportedReportTypes();
      for (ExportOption exOpt : availableResultFormats) {
        boolean setToVisible = false;
        for (String supportedExportFormat : supportedExportFormats) {
          if (exOpt.getPresentationKey().equals(supportedExportFormat)) {
            setToVisible = true;
            break;
          }
        }
        exOpt.setVisible(setToVisible);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public E saveQuery(E memBean, RequestContext requestContext) {
    boolean saveAsOption = memBean.isSaveAs();
    memBean.setSaveAs(false);

    Locale locale = UserContext.getCurrentLocale();
    ReportXML saveReport = new ReportXML();
    saveReport.initFrom(memBean, locale);

    String name;
    if (saveAsOption) {
      name = memBean.getXmlSaveAsQueryName();
    }
    else {
      name = memBean.getXmlQueryName();
    }

    if (name == null || name.equals("")) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.SAVEDQUERY_NAME_IS_NULL);
    }

    List<DynamicQueryFormData<?>> queryForms = memBean.getQueryResult().getQueryForms();
    String reportContentType = queryForms.get(0).getType().getTypeOfBuildingBlock().toString();
    ReportType sqt = ReportType.fromValue(reportContentType);

    if (saveAsOption) {
      checkIfQueryNameIsUsed(sqt, name);
      acceptNewNameAndDescription(memBean);
    }

    savedQueryService.saveQuery(name, memBean.getXmlQueryDescription(), saveReport, sqt);
    memBean.setSavedQueries(savedQueryService.getSavedQueriesWithoutContent(sqt));

    //display success message on screen
    requestContext.getFlashScope().put(FLUSHATTRIBUTE_SAVE_SUCCESSFUL_TRIGGER, "true");

    return memBean;
  }

  /**
   * {@inheritDoc}
   */
  public E saveQueryAs(E memBean, RequestContext requestContext) {
    memBean.setSaveAs(true);
    return saveQuery(memBean, requestContext);
  }

  protected void checkIfQueryNameIsUsed(ReportType savedQueryType, String name) {
    if (savedQueryService.existsQuery(savedQueryType, name)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.SAVEDQUERY_NAME_IN_USE);
    }
  }

  protected void acceptNewNameAndDescription(E memBean) {
    memBean.setXmlQueryName(memBean.getXmlSaveAsQueryName());
    memBean.setXmlQueryDescription(memBean.getXmlSaveAsQueryDescription());
    replaceSaveAsProperties(memBean);
  }

  protected void replaceSaveAsProperties(E memBean) {
    memBean.setXmlSaveAsQueryName("");
    memBean.setXmlSaveAsQueryDescription("");
  }

  /**
   * {@inheritDoc}
   */
  public E deleteSavedQuery(E memBean) {
    if (null == memBean.getDeleteQueryId()) {
      throw new IteraplanTechnicalException();
    }

    memBean.clearErrors();

    SavedQuery sq = savedQueryService.getSavedQuery(memBean.getDeleteQueryId());
    List<CustomDashboardInstance> cdi = customDashboardService.getCustomDashboardBySavedQuery(sq);

    // check if the savedQuery (tabular report) is used by a dashboard
    if (!cdi.isEmpty()) {
      memBean.addError(MessageAccess.getString("customDashboard.deleteSavedQuery.warning"));
    }
    else {
      //no dashboard found => deleting saved query is ok.
      savedQueryService.deleteSavedQuery(memBean.getDeleteQueryId());

      List<DynamicQueryFormData<?>> queryForms = memBean.getQueryResult().getQueryForms();
      String type = queryForms.get(0).getType().getTypeOfBuildingBlock().toString();
      ReportType sqt = ReportType.fromValue(type);
      memBean.setSavedQueries(savedQueryService.getSavedQueriesWithoutContent(sqt));
    }

    return memBean;
  }

  /**
   * {@inheritDoc}
   */
  public E loadSavedQuery(E memBean) {
    if (null == memBean.getSavedQueryId()) {
      throw new IteraplanTechnicalException();
    }

    SavedQuery savedQuery = savedQueryService.getSavedQuery(memBean.getSavedQueryId());
    ReportXML savedReport = savedQueryService.getSavedReport(savedQuery);

    return initializeMemBeanFromSavedQuery(memBean, savedQuery, savedReport);
  }

  protected E initializeMemBeanFromSavedQuery(E memBean, SavedQuery savedQuery, ReportXML savedReport) {
    ManageReportMemoryBean newMembean = getNewMemBeanFromReport(memBean, savedReport);

    //set the report type of the new bean (necessary for the initialization of the available post processing strategies)
    newMembean.setReportType(getReportType());

    if (savedReport.getQueryForms() != null && !savedReport.getQueryForms().isEmpty()) {
      // For compatibility with older saved queries
      loadQueryForms(savedReport, newMembean);
    }
    else {
      loadQueryResults(savedReport, newMembean);
    }
    newMembean.setXmlQueryName(savedQuery.getName());
    newMembean.setXmlQueryDescription(savedQuery.getDescription());
    newMembean.setLoadedFromSavedQuery(true);

    // if previously saved, load visible columns
    List<ColumnEntryXML> visibleColumnsXML = savedReport.getVisibleColumns();
    boolean columnsUpdated = configVisibleColumns(newMembean, visibleColumnsXML);
    newMembean.setVisibleColumnsUpdated(columnsUpdated);

    Type<? extends BuildingBlock> queryType = savedReport.getQueryResultType();
    if (queryType != null && queryType.getTypeOfBuildingBlock() != null) {
      newMembean.setSelectedBuildingBlock(queryType.getTypeOfBuildingBlock().getPluralValue());
    }

    refreshHelperService.refreshAllForms(newMembean.getQueryResult().getQueryForms());

    newMembean.setSavedQueryId(null);

    return (E) newMembean;
  }

  private void loadQueryResults(ReportXML savedReport, ManageReportMemoryBean memBean) {
    if (savedReport.getQueryResults().isEmpty()) {
      return;
    }

    for (QueryResultXML queryResultXML : savedReport.getQueryResults()) {
      QueryResult queryResult = initFormHelperService.getQueryResult(queryResultXML);
      memBean.setQueryResult(queryResult);

      // set the massupdate type for the first form
      DynamicQueryFormData<?> form = queryResult.getQueryForms().get(0);
      if (form != null) {
        form.setMassUpdateType(MassUpdateTypeHelper.getMassUpdateType(TypeOfBuildingBlock.fromPropertyString(memBean.getSelectedBuildingBlock())));
      }

      initFormHelperService.switchQuery(memBean, queryResult.getQueryName());
      initFormHelperService.initSelectedPostProcessingStrategies(memBean, queryResultXML.getQueryForms().get(0));
    }
    initFormHelperService.switchQuery(memBean, ManageReportBeanBase.MAIN_QUERY);
  }

  private void loadQueryForms(ReportXML savedReport, ManageReportMemoryBean newMembean) {
    List<QueryFormXML> queryForms = savedReport.getQueryForms();
    Map<String, IPresentationExtension> availableReportExtensions = new HashMap<String, IPresentationExtension>(queryForms.get(0).getType()
        .getExtensionsForPresentation());

    // get all query forms from the saved XML file and set query extensions. used extensions are
    // removed from availableReportExtensions. the map 'availableReportExtensions' hence after this
    // method call only contains extensions not used yet
    List<DynamicQueryFormData<?>> forms = initFormHelperService.getSavedReportForm(queryForms, availableReportExtensions);

    // set the massupdate type for the first form
    DynamicQueryFormData<?> form = forms.get(0);
    if (form != null) {
      form.setMassUpdateType(MassUpdateTypeHelper.getMassUpdateType(TypeOfBuildingBlock.fromPropertyString(newMembean.getSelectedBuildingBlock())));
    }

    // add forms and (remaining - not selected) report extensions
    newMembean.setQueryForms(forms, availableReportExtensions);

    initFormHelperService.initSelectedPostProcessingStrategies(newMembean, queryForms.get(0));

    List<Integer> savedReportSelectedResults = savedReport.getSelectedResultIds();
    newMembean.getQueryResult().setSelectedResultIds(savedReportSelectedResults.toArray(new Integer[savedReportSelectedResults.size()]));
  }

  private boolean configVisibleColumns(ManageReportMemoryBean newMembean, List<ColumnEntryXML> visibleColumnsXML) {
    //default visible & available columns
    ViewConfiguration configuration = newMembean.getViewConfiguration();
    List<ColumnEntry> visibleColumns = configuration.getVisibleColumns();
    List<ColumnEntry> availableColumns = configuration.getAvailableColumns();

    List<ColumnEntry> visibleColumnsFromXML = Lists.newArrayList();
    for (ColumnEntryXML xmlEntry : visibleColumnsXML) {
      visibleColumnsFromXML.add(new ColumnEntry(xmlEntry.getField(), COLUMN_TYPE.get(xmlEntry.getType()), xmlEntry.getHead()));
    }

    // we need at least one column
    if (!oneSavedColumnStillExists(availableColumns, visibleColumns, visibleColumnsFromXML)) {
      return true;
    }

    // remove not existing columns
    for (ColumnEntry columnEntry : Lists.newArrayList(visibleColumns)) {
      if (!visibleColumnsFromXML.contains(columnEntry)) {
        configuration.deleteColumnEntry(columnEntry.getHead());
      }
      else {
        visibleColumnsFromXML.remove(columnEntry); //done
      }
    }

    // add new columns
    boolean reportUpdated = false;
    for (ColumnEntry columnEntry : visibleColumnsFromXML) {

      // add visible column if the attribute still exists in available list
      if (availableColumns.contains(columnEntry)) {
        configuration.addColumnEntry(columnEntry.getHead());
      }
      else if (!visibleColumns.contains(columnEntry)) {
        reportUpdated = true;
      }
    }

    return reportUpdated;
  }

  /**
   * Gets a freshly initialized query for the BB type that the query returns
   * @param memBean
   *          current memory bean
   * @param savedReport
   *          ReportXML containing the loaded query forms
   * @return new Memory Bean for the BB type that {@code savedReport}'s queries returns
   */
  private ManageReportMemoryBean getNewMemBeanFromReport(E memBean, ReportXML savedReport) {
    TypeOfBuildingBlock typeOfBb = savedReport.getQueryResultType().getTypeOfBuildingBlock();
    ManageReportMemoryBean newMembean = initFormHelperService.getInitializedReportMemBean(typeOfBb.getPluralValue());

    newMembean.setReportUpdated(memBean.isReportUpdated() || savedReport.isReportUpdated());
    memBean.setReportUpdated(memBean.isReportUpdated() || savedReport.isReportUpdated());

    return newMembean;
  }

  /**
   * returns true, if at least one column entry from xml is in one of the availableColumns or visibleColumns contained. Returns false if
   * all columns are not contained in all columns (availableColumns and visibleColumns). Returns also false, if the xmi list is empty.
   * @param availableColumns list of available columns.
   * @param visibleColumns list of visible columns.
   * @param visibleColumnsXML list of visible xml columns.
   * @return see method description.
   */
  private boolean oneSavedColumnStillExists(List<ColumnEntry> availableColumns, List<ColumnEntry> visibleColumns, List<ColumnEntry> visibleColumnsXML) {
    for (ColumnEntry columnEntry : visibleColumnsXML) {
      if (availableColumns.contains(columnEntry) || visibleColumns.contains(columnEntry)) {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public E addColumn(E memBean, RequestContext context, FlowExecutionContext flowContext) {
    ViewConfiguration configuration = memBean.getViewConfiguration();
    configuration.addColumnEntry(memBean.getSelectedNewColumn());

    String resultFormat = memBean.getTabularOptions().getResultFormat();
    if (Constants.REPORTS_EXPORT_HTML.equals(resultFormat)) {
      this.requestReport(memBean, context, flowContext);
    }
    return memBean;
  }

  /**
   * {@inheritDoc}
   */
  public E updateColumn(E memBean, RequestContext context, FlowExecutionContext flowContext) {
    ViewConfiguration configuration = memBean.getViewConfiguration();
    String move = memBean.getCurrentColumnAction();
    if ("moveLeft".equals(move)) {
      configuration.moveColumnEntryLeft(memBean.getCurrentColumnName());
    }
    else if ("moveRight".equals(move)) {
      configuration.moveColumnEntryRight(memBean.getCurrentColumnName());
    }

    String resultFormat = memBean.getTabularOptions().getResultFormat();
    if (Constants.REPORTS_EXPORT_HTML.equals(resultFormat)) {
      this.requestReport(memBean, context, flowContext);
    }
    return memBean;
  }

  /**
   * {@inheritDoc}
   */
  public E removeColumn(E memBean, RequestContext context, FlowExecutionContext flowContext) {
    ViewConfiguration configuration = memBean.getViewConfiguration();
    configuration.deleteColumnEntry(memBean.getCurrentColumnName());

    String resultFormat = memBean.getTabularOptions().getResultFormat();
    if (Constants.REPORTS_EXPORT_HTML.equals(resultFormat)) {
      this.requestReport(memBean, context, flowContext);
    }
    return memBean;
  }

  protected List<Integer> parseSelectedIds(String idList) {
    List<Integer> list = new ArrayList<Integer>();
    String[] parts = idList.substring(1, idList.length() - 1).split(",");

    for (String part : parts) {
      try {
        list.add(Integer.valueOf(part));
      } catch (NumberFormatException e) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
      }
    }

    return list;
  }

  protected void removeVirtualElement(List<BuildingBlock> allEntities) {
    BuildingBlock virtualElement = null;
    for (BuildingBlock bb : allEntities) {
      if (bb.getNonHierarchicalName().equals(AbstractHierarchicalEntity.TOP_LEVEL_NAME)) {
        virtualElement = bb;
      }
    }
    allEntities.remove(virtualElement);
  }

  protected InitFormHelperService getInitFormHelperService() {
    return initFormHelperService;
  }

  protected SavedQueryService getSavedQueryService() {
    return savedQueryService;
  }

  protected ExportService getExportService() {
    return exportService;
  }

  protected ExtensionFormHelper getExtensionFormHelper() {
    return extensionFormHelper;
  }

  protected AttributeTypeService getAttributeTypeService() {
    return attributeTypeService;
  }

  protected QueryService getQueryService() {
    return queryService;
  }

  public TemplateLocatorService getTemplateLocatorService() {
    return templateLocatorService;
  }

  protected RefreshHelperService getRefreshHelperService() {
    return refreshHelperService;
  }

  public E clearErrors(E memBean) {
    memBean.clearErrors();
    return memBean;
  }
}

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
package de.iteratec.iteraplan.presentation.dialog.TabularReporting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.msproject.MsProjectExport;
import de.iteratec.iteraplan.businesslogic.exchange.msproject.MsProjectExporterBase.ExportType;
import de.iteratec.iteraplan.businesslogic.exchange.nettoExport.NettoExporter;
import de.iteratec.iteraplan.businesslogic.exchange.nettoExport.SpreadsheetReportTableStructure;
import de.iteratec.iteraplan.businesslogic.exchange.nettoExport.TableStructure;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ViewConfiguration;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TabularOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.SealState;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.xml.ReportXML;
import de.iteratec.iteraplan.model.xml.query.TabularOptionsXML;
import de.iteratec.iteraplan.presentation.dialog.ReportBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.responsegenerators.CsvResponseGenerator;
import de.iteratec.iteraplan.presentation.responsegenerators.ExcelResponseGenerator;
import de.iteratec.iteraplan.presentation.responsegenerators.MsProjectResponseGenerator;
import de.iteratec.iteraplan.presentation.responsegenerators.XMIResponseGenerator;


@Service("tabularReportingFrontendService")
public class TabularReportingFrontendServiceImpl extends ReportBaseFrontendServiceImpl<ManageReportMemoryBean> implements
    TabularReportingFrontendService {

  @Autowired
  private XMIResponseGenerator xmiResponseGenerator;

  private ManageReportMemoryBean getInitialMemBean() {
    ManageReportMemoryBean memBean = getInitFormHelperService().getInitializedReportMemBeanByDialogPerms(UserContext.getCurrentPerms());
    memBean.setReportType(getReportType());
    memBean.resetPostProcessingStrategies();

    return memBean;
  }

  public ManageReportMemoryBean getInitialMemBean(String tobString) {
    if (tobString == null) {
      return getInitialMemBean();
    }
    else {
      ManageReportMemoryBean memBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(tobString, Constants.ALL_TYPES_FOR_DISPLAY);
      memBean.setReportType(getReportType());
      memBean.resetPostProcessingStrategies();

      return memBean;
    }
  }

  @Override
  protected ManageReportMemoryBean getMemBeanForChangedQueryType(ManageReportMemoryBean memBean) {
    String selectedBuildingBlock = memBean.getSelectedBuildingBlock();

    ManageReportMemoryBean newMemBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(selectedBuildingBlock,
        Constants.ALL_TYPES_FOR_DISPLAY);
    if (newMemBean == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    newMemBean.setSelectedBuildingBlock(selectedBuildingBlock);

    return newMemBean;
  }

  public ManageReportMemoryBean fromInterchange(String bbType, String idList, String statusSelected, String sealSelected, String statusAV,
                                                String sealAV) {
    if (idList == null || bbType == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }

    ManageReportMemoryBean resultMemBean = getInitialMemBean(bbType);

    setSelectedFixedAT(resultMemBean, statusSelected, sealSelected, statusAV, sealAV);

    // cast needed so that the method in ReportBaseFrontendServiceImpl isn't ambiguous
    requestReport(resultMemBean, (HttpServletRequest) null, null);

    //remove results that were not selected previously
    List<Integer> idsAsList = parseSelectedIds(idList);
    List<BuildingBlock> bbsToRemoveFromResult = new ArrayList<BuildingBlock>();
    for (BuildingBlock bb : resultMemBean.getResults()) {
      if (!idsAsList.contains(bb.getId()) || bb.getNonHierarchicalName().equals(AbstractHierarchicalEntity.TOP_LEVEL_NAME)) {
        bbsToRemoveFromResult.add(bb);
      }
    }
    resultMemBean.getResults().removeAll(bbsToRemoveFromResult);

    return resultMemBean;
  }

  @Override
  public ManageReportMemoryBean refreshReport(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext,
                                              boolean resetResults) {
    ManageReportMemoryBean newMemBean = super.refreshReport(memBean, context, flowContext, resetResults);

    String resultFormat = getResultFormat(memBean);
    if (isResultFormatExcelFormat(resultFormat)) {
      TemplateType templateType = getExcelTemplateTypeFromResultFormat(resultFormat);
      newMemBean.getTabularOptions().setAvailableResultFormatTemplates(createTemplateOptionsList(templateType));
    }
    return newMemBean;
  }

  @Override
  protected boolean createReport(ManageReportMemoryBean memBean, Node node, HttpServletRequest request, HttpServletResponse response) {

    String resultFormat = getResultFormat(memBean);
    List<AbstractPostprocessingStrategy<? extends BuildingBlock>> strategies = getStrategies(memBean);

    // handle excel report separately to stay in one database session while
    // getting results and creating report
    if (isResultFormatExcelFormat(resultFormat)) {
      TemplateType templateType = getExcelTemplateTypeFromResultFormat(resultFormat);
      String templateFileName = "";
      if (memBean.getTabularOptions() != null) {
        templateFileName = memBean.getTabularOptions().getResultFormatTemplate();
      }
      createReportExportExcel(memBean, request, response, node, templateType, templateFileName);
    }
    else if (resultFormat.equals(Constants.REPORTS_EXPORT_CSV)) {
      createReportExportCsv(response, node, memBean.getQueryResult().getTimeseriesQuery(), strategies);
    }
    else {
      // Due to the inconsistent usage of generics the list must be processed to dispose off the wildcard.
      List<AbstractPostprocessingStrategy<BuildingBlock>> listWithoutWildcard = getQueryService().disposeOfWildcard(strategies, BuildingBlock.class);

      if (resultFormat.equals(Constants.REPORTS_EXPORT_HTML)) {
        return super.createReport(memBean, node, request, response);
      }
      else if (resultFormat.equals(Constants.REPORTS_EXPORT_XMI)) {
        createReportExportXmi(response, node, memBean.getQueryResult().getTimeseriesQuery(), listWithoutWildcard);
      }
      else {
        ExportType type;
        if (resultFormat.equals(Constants.REPORTS_EXPORT_MSPROJECT_MSPDI)) {
          type = ExportType.XML_WITHOUT_SUBORDINATED_BLOCKS;
        }
        else if (resultFormat.equals(Constants.REPORTS_EXPORT_MSPROJECT_MSPDI_INCLUDING_SUBS)) {
          type = ExportType.XML_WITH_SUBORDINATED_BLOCKS;
        }
        else if (resultFormat.equals(Constants.REPORTS_EXPORT_MSPROJECT_MPX)) {
          type = ExportType.MPX_WITHOUT_SUBORDINATED_BLOCKS;
        }
        else if (resultFormat.equals(Constants.REPORTS_EXPORT_MSPROJECT_MPX_INCLUDING_SUBS)) {
          type = ExportType.MPX_WITH_SUBORDINATED_BLOCKS;
        }
        else {
          throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
        }

        createReportExportMsproject(memBean, request, response, node, listWithoutWildcard, type);
      }
    }
    return true;
  }

  private String getResultFormat(ManageReportMemoryBean memBean) {
    String resultFormat = memBean.getTabularOptions().getResultFormat();
    // if no resultFormat is set, default to HTML
    if (resultFormat == null || BooleanUtils.isTrue(memBean.getMassUpdateMode())) {
      resultFormat = Constants.REPORTS_EXPORT_HTML;
    }
    return resultFormat;
  }

  private void createReportExportXmi(HttpServletResponse response, Node node, TimeseriesQuery tsQuery,
                                     List<AbstractPostprocessingStrategy<BuildingBlock>> listWithoutWildcard) {
    List<? extends BuildingBlock> results = getQueryService().evaluateQueryTree(node, tsQuery, listWithoutWildcard);

    if (!results.isEmpty()) {
      String className = results.iterator().next().getClass().getSimpleName();
      xmiResponseGenerator.generateXmlResponseForTabularReporting(response, results, className + "_ExportForTabReporting.xml");
    }
  }

  private void createReportExportCsv(HttpServletResponse response, Node node, TimeseriesQuery tsQuery,
                                     List<AbstractPostprocessingStrategy<? extends BuildingBlock>> strategies) {
    String csvString = getExportService().getCsvExportNew(node, tsQuery, strategies);

    CsvResponseGenerator csv = new CsvResponseGenerator();
    csv.generateResponse(response, csvString);
  }

  private void createReportExportMsproject(ManageReportMemoryBean memBean, HttpServletRequest request, HttpServletResponse response, Node node,
                                           List<AbstractPostprocessingStrategy<BuildingBlock>> listWithoutWildcard, ExportType type) {

    List<? extends BuildingBlock> results = getQueryService().evaluateQueryTree(node, memBean.getQueryResult().getTimeseriesQuery(),
        listWithoutWildcard);

    DynamicQueryFormData<?> form = memBean.getQueryResult().getQueryForms().get(0);

    MsProjectExport projectExport = getExportService().getMsProjectExport(results, request, form, type);
    MsProjectResponseGenerator msProjectResponseGen = new MsProjectResponseGenerator();
    msProjectResponseGen.generateResponse(response, projectExport, type);
  }

  private void createReportExportExcel(ManageReportMemoryBean memBean, HttpServletRequest request, HttpServletResponse response, Node node,
                                       TemplateType templateType, String templateFileName) {

    ExportWorkbook excelWorkbook = createExportWorkbook(templateType, templateFileName, memBean, request, node, memBean.getQueryResult()
        .getTimeseriesQuery());

    if (excelWorkbook != null) {
      ExcelResponseGenerator excelResponseGen = new ExcelResponseGenerator();
      excelResponseGen.generateResponse(response, excelWorkbook);
    }
  }

  private ExportWorkbook createExportWorkbook(TemplateType templateType, String templateFileName, ManageReportMemoryBean memBean,
                                              HttpServletRequest request, Node node, TimeseriesQuery tsQuery) {
    List<AbstractPostprocessingStrategy<? extends BuildingBlock>> strategies = getStrategies(memBean);

    DynamicQueryFormData<?> form = memBean.getQueryResult().getQueryForms().get(0);
    String serverURL = URLBuilder.getApplicationURL(request);
    return getExportService().getExcelExportByType(node, tsQuery, strategies, form, serverURL, templateType, templateFileName);
  }

  @Override
  protected ManageReportMemoryBean initializeMemBeanFromSavedQuery(ManageReportMemoryBean memBean, SavedQuery savedQuery, ReportXML savedReport) {
    ManageReportMemoryBean newMemBean = super.initializeMemBeanFromSavedQuery(memBean, savedQuery, savedReport);
    TabularOptionsBean tabularOptions = newMemBean.getTabularOptions();

    TabularOptionsXML tabularOptionsXml = savedReport.getTabularOptions();
    // set the result format (excel, csv, etc)
    if (tabularOptionsXml != null) {
      tabularOptions.setResultFormat(tabularOptionsXml.getResultFormat());
      tabularOptions.setResultFormatTemplate(tabularOptionsXml.getResultFormatTemplate());
    }
    String resultFormat = tabularOptions.getResultFormat();
    if (isResultFormatExcelFormat(resultFormat)) {
      TemplateType templateType = getExcelTemplateTypeFromResultFormat(resultFormat);
      tabularOptions.setAvailableResultFormatTemplates(createTemplateOptionsList(templateType));
    }

    return newMemBean;
  }

  private TemplateType getExcelTemplateTypeFromResultFormat(String resultFormat) {
    if (Constants.REPORTS_EXPORT_EXCEL_2007.equals(resultFormat)) {
      return TemplateType.EXCEL_2007;
    }
    else {
      return TemplateType.EXCEL_2003;
    }
  }

  private boolean isResultFormatExcelFormat(String resultFormat) {
    if (Constants.REPORTS_EXPORT_EXCEL_2003.equals(resultFormat) || Constants.REPORTS_EXPORT_EXCEL_2007.equals(resultFormat)) {
      return true;
    }
    return false;
  }

  @Override
  protected String getFlowId() {
    return Dialog.TABULAR_REPORTING.getFlowId();
  }

  @Override
  protected ReportType getReportType() {
    return ReportType.TABVIEW;
  }

  protected void setSelectedFixedAT(ManageReportMemoryBean resultMemBean, String statusSelected, String sealSelected, String statusAV, String sealAV) {

    List<DynamicQueryFormData<?>> queryFormData = resultMemBean.getQueryResult().getQueryForms();

    if (StringUtils.equals(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, resultMemBean.getSelectedBuildingBlock())) {
      for (DynamicQueryFormData<?> queryForm : queryFormData) {
        if (StringUtils.equals(statusSelected, "true")) {
          queryForm.getQueryUserInput().getStatusQueryData().setStatus(statusAV, Boolean.TRUE);
          if (!StringUtils.equals(TypeOfStatus.CURRENT.getValue(), statusAV)) {
            queryForm.getQueryUserInput().getStatusQueryData().setStatus(TypeOfStatus.CURRENT.getValue(), Boolean.FALSE);
          }
        }
        else {
          queryForm.getQueryUserInput().getStatusQueryData().setStatus(TypeOfStatus.INACTIVE.getValue(), Boolean.TRUE);
          queryForm.getQueryUserInput().getStatusQueryData().setStatus(TypeOfStatus.PLANNED.getValue(), Boolean.TRUE);
          queryForm.getQueryUserInput().getStatusQueryData().setStatus(TypeOfStatus.TARGET.getValue(), Boolean.TRUE);
        }

        queryForm.getQueryUserInput().getTimespanQueryData().setStartDateAsString("");
        queryForm.getQueryUserInput().getTimespanQueryData().setEndDateAsString("");

        if (StringUtils.equals(sealSelected, "true")) {
          queryForm.getQueryUserInput().getSealQueryData().setStatus(sealAV, Boolean.TRUE);
        }
        else if (StringUtils.equals(statusSelected, "false")) {
          queryForm.getQueryUserInput().getSealQueryData().setStatus(SealState.INVALID.getValue(), Boolean.TRUE);
          queryForm.getQueryUserInput().getSealQueryData().setStatus(SealState.NOT_AVAILABLE.getValue(), Boolean.TRUE);
          queryForm.getQueryUserInput().getSealQueryData().setStatus(SealState.OUTDATED.getValue(), Boolean.TRUE);
          queryForm.getQueryUserInput().getSealQueryData().setStatus(SealState.VALID.getValue(), Boolean.TRUE);
        }
      }
    }
    else if (StringUtils.equals(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, resultMemBean.getSelectedBuildingBlock())) {
      for (DynamicQueryFormData<?> queryForm : queryFormData) {
        if (StringUtils.equals(statusSelected, "true")) {
          queryForm.getQueryUserInput().getStatusQueryData().setStatus(statusAV, Boolean.TRUE);
          if (!StringUtils.equals(TypeOfStatus.CURRENT.getValue(), statusAV)) {
            queryForm.getQueryUserInput().getStatusQueryData().setStatus(TypeOfStatus.CURRENT.getValue(), Boolean.FALSE);
          }
        }
        else {
          queryForm.getQueryUserInput().getStatusQueryData().setStatus(TypeOfStatus.INACTIVE.getValue(), Boolean.TRUE);
          queryForm.getQueryUserInput().getStatusQueryData().setStatus(TypeOfStatus.PLANNED.getValue(), Boolean.TRUE);
          queryForm.getQueryUserInput().getStatusQueryData().setStatus(TypeOfStatus.TARGET.getValue(), Boolean.TRUE);
        }

        queryForm.getQueryUserInput().getTimespanQueryData().setStartDateAsString("");
        queryForm.getQueryUserInput().getTimespanQueryData().setEndDateAsString("");
      }
    }
    else if (StringUtils.equals(Constants.BB_PROJECT_PLURAL, resultMemBean.getSelectedBuildingBlock())) {
      for (DynamicQueryFormData<?> queryForm : queryFormData) {
        queryForm.getQueryUserInput().getTimespanQueryData().setStartDateAsString("");
        queryForm.getQueryUserInput().getTimespanQueryData().setEndDateAsString("");
      }
    }
  }

  public boolean generateNettoExportResponse(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {

    memBean.clearErrors(); // first clear the error list
    HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getNativeRequest();
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();

    String downloadFormat = request.getParameter("format");
    TypeOfBuildingBlock typeOfBuildingBlock = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(memBean.getSelectedBuildingBlock());

    requestReport(memBean, request, response);
    List<? extends BuildingBlock> results = memBean.getQueryResult().getResults();

    ViewConfiguration viewConfiguration = memBean.getViewConfiguration();
    TableStructure tableStructure = new SpreadsheetReportTableStructure(viewConfiguration);

    NettoExporter exporter = NettoExporter.newInstance(typeOfBuildingBlock, downloadFormat);
    exporter.exportToResponse(results, tableStructure, response);

    context.getExternalContext().recordResponseComplete();

    return true;
  }
}

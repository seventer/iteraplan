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
package de.iteratec.iteraplan.presentation.dialog.ExcelImport;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.emf.EmfExportService;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.emf.EmfExportServiceImpl;
import de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.exporter.TimeseriesExcelExportService;
import de.iteratec.iteraplan.businesslogic.service.ElasticMiService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.elasticmi.io.mapper.xls.XlsModelMapper;
import de.iteratec.iteraplan.elasticmi.io.mapper.xls.XlsModelMapper.ExcelFormat;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQl2Exception;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQlQuery;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.metamodel.partial.BasePartialExportMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.partial.a.APartialExportMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.impl.ModelImpl;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.CommonFrontendServiceImpl;


/**
 * Handles the Excel Import front end
 */
@Service("exportFrontendService")
public class ExportFrontendServiceImpl extends CommonFrontendServiceImpl {

  private static final Logger          LOGGER                       = Logger.getIteraplanLogger(ExportFrontendServiceImpl.class);

  /** MIME Content Type for Excel */
  private static final String          MIME_TYPE_EXCEL_2003         = "application/vnd.ms-excel";
  private static final String          MIME_TYPE_EXCEL_2007         = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  private static final String          MIME_TYPE_XML                = "application/xml";
  private static final String          MIME_TYPE_ZIP                = "application/zip";

  private static final String          ECORE_FILENAME               = "iteraplanMetamodel.ecore";
  private static final String          ZIP_FILENAME                 = "iteraplanXMIExport.zip";

  private static final String          EXCEL_2003_EXTENTSION        = ".xls";
  private static final String          EXCEL_2007_EXTENTSION        = ".xlsx";

  private static final String          METAMODEL_OUTPUT_FILENAME    = "iteraplanExcelTemplate";
  private static final String          FULL_MODEL_OUTPUT_FILENAME   = "iteraplanExcelData";

  private static final String          TIMESERIES_TEMPLATE_FILENAME = "iteraplanTimeseriesTemplate";
  private static final String          TIMESERIES_DATA_FILENAME     = "iteraplanTimeseriesData";

  @Autowired
  private TimeseriesExcelExportService timeseriesExcelExportService;
  @Autowired
  private ElasticMiService             elasticmiService;

  private EmfExportService             emfExportService             = new EmfExportServiceImpl();

  @Override
  protected String getFlowId() {
    return Dialog.IMPORT.getFlowId();
  }

  /** 
   * export the template, i.e. an empty Excel Workbook with all Metamodel information.
   * 
   * @param context 
   * @return true if successful, false if error occurred.
   */
  public MassdataMemBean downloadTemplateExcel2003(RequestContext context, MassdataMemBean memBean) {
    LOGGER.info("in ExcelExport2FrontendServiceImpl.downloadTemplateExcel2003()");
    memBean.resetResultMessages();
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();

    Workbook wb = fillWorkbook(new ModelImpl(), elasticmiService.getRMetamodel(), ExcelFormat.XLS);

    writeExcelToOutputStream(response, wb, METAMODEL_OUTPUT_FILENAME + EXCEL_2003_EXTENTSION, MIME_TYPE_EXCEL_2003);

    markFinished(context);

    return memBean;
  }

  /** 
   * export the template, i.e. an empty Excel Workbook with all Metamodel information.
   * 
   * @param context 
   * @return true if successful, false if error occurred.
   */
  public MassdataMemBean downloadTemplateExcel2007(RequestContext context, MassdataMemBean memBean) {
    LOGGER.info("in ExcelExport2FrontendServiceImpl.downloadTemplateExcel2007()");
    memBean.resetResultMessages();
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();

    Workbook wb = fillWorkbook(new ModelImpl(), elasticmiService.getRMetamodel(), ExcelFormat.XLSX);

    writeExcelToOutputStream(response, wb, METAMODEL_OUTPUT_FILENAME + EXCEL_2007_EXTENTSION, MIME_TYPE_EXCEL_2007);

    markFinished(context);

    return memBean;
  }

  /**
   * export the full Excel Workbook.
   * 
   * @param context
   * @return true if successful, false if error occurred.
   */
  public MassdataMemBean downloadFullModelExcel2003(RequestContext context, MassdataMemBean memBean) {
    LOGGER.info("in ExcelExport2FrontendServiceImpl.downloadFullModelExcel2003()");
    memBean.resetResultMessages();
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();
    Workbook wb = fillWorkbook(elasticmiService.getModel(), elasticmiService.getRMetamodel(), ExcelFormat.XLS);

    writeExcelToOutputStream(response, wb, FULL_MODEL_OUTPUT_FILENAME + EXCEL_2003_EXTENTSION, MIME_TYPE_EXCEL_2003);

    markFinished(context);

    return memBean;
  }

  /**
   * Download metamodel as ecore file
   * @param context
   * @return true, if download succeeds
   */
  public boolean downloadEcore(RequestContext context) {
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();
    setContentTypeAndHeader(response, MIME_TYPE_XML, ECORE_FILENAME);
    try {
      emfExportService.serializeMetamodel(response.getOutputStream());

      markFinished(context);
    } catch (IOException e) {
      LOGGER.error(e);
      return false;
    }
    return true;
  }

  /**
   * Download model as xmi file
   * @param context
   * @return true, if download succeeds
   */
  public boolean downloadBundle(RequestContext context) {
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();
    setContentTypeAndHeader(response, MIME_TYPE_ZIP, ZIP_FILENAME);
    try {
      ServletOutputStream out = response.getOutputStream();
      emfExportService.serializeBundle(out);
      out.flush();
      markFinished(context);
    } catch (IOException e) {
      LOGGER.error(e);
      return false;
    }
    return true;
  }

  /** 
   * export the timeseries template, i.e. an empty Excel Workbook with all (empty) timeseries sheets.
   * 
   * @param context 
   * @return true if successful, false if error occurred.
   */
  public boolean downloadTimeseriesTemplateExcel2003(RequestContext context) {
    LOGGER.info("in downloadTimeseriesTemplateExcel2003()");

    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();

    Workbook wb = timeseriesExcelExportService.generateTemplateExcel2003();
    writeExcelToOutputStream(response, wb, TIMESERIES_TEMPLATE_FILENAME + EXCEL_2003_EXTENTSION, MIME_TYPE_EXCEL_2003);

    markFinished(context);

    return true;
  }

  /** 
   * export the timeseries template, i.e. an empty Excel Workbook with all (empty) timeseries sheets.
   * 
   * @param context 
   * @return true if successful, false if error occurred.
   */
  public boolean downloadTimeseriesTemplateExcel2007(RequestContext context) {
    LOGGER.info("in downloadTimeseriesTemplateExcel2007()");

    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();

    Workbook wb = timeseriesExcelExportService.generateTemplateExcel2007();
    writeExcelToOutputStream(response, wb, TIMESERIES_TEMPLATE_FILENAME + EXCEL_2007_EXTENTSION, MIME_TYPE_EXCEL_2007);

    markFinished(context);

    return true;
  }

  /** 
   * Exports the timeseries data, i.e. an Excel Workbook with all (filled) timeseries sheets.
   * 
   * @param context 
   * @return true if successful, false if error occurred.
   */
  public boolean downloadTimeseriesDataExcel2003(RequestContext context) {
    LOGGER.info("in downloadTimeseriesDataExcel2003()");

    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();

    Workbook wb = timeseriesExcelExportService.exportTimeseriesDataExcel2003();
    writeExcelToOutputStream(response, wb, TIMESERIES_DATA_FILENAME + EXCEL_2003_EXTENTSION, MIME_TYPE_EXCEL_2003);

    markFinished(context);

    return true;
  }

  /** 
   * Exports the timeseries data, i.e. an Excel Workbook with all (filled) timeseries sheets.
   * 
   * @param context 
   * @return true if successful, false if error occurred.
   */
  public boolean downloadTimeseriesDataExcel2007(RequestContext context) {
    LOGGER.info("in downloadTimeseriesDataExcel2007()");

    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();

    Workbook wb = timeseriesExcelExportService.exportTimeseriesDataExcel2007();
    writeExcelToOutputStream(response, wb, TIMESERIES_DATA_FILENAME + EXCEL_2007_EXTENTSION, MIME_TYPE_EXCEL_2007);

    markFinished(context);

    return true;
  }

  private void writeExcelToOutputStream(HttpServletResponse response, Workbook wb, String filename, String mimeType) {
    try {
      setContentTypeAndHeader(response, mimeType, filename);
      OutputStream outputStream = response.getOutputStream();
      wb.write(outputStream);
    } catch (IOException e) {
      LOGGER.error("Excel Export error: ", e);
    }
  }

  private void setContentTypeAndHeader(HttpServletResponse response, String contentType, String filename) {
    response.setContentType(contentType);
    response.setHeader("Content-Disposition", "attachment;filename=" + filename);
  }

  private Workbook fillWorkbook(Model model, RMetamodel metamodel, ExcelFormat format) {
    LOGGER.debug("Starting Excel data export.");
    //Note: Using NOOP message listener here
    return new XlsModelMapper(metamodel, null, MessageListener.NOOP_LISTENER, format, IteraplanProperties.getProperties().getBuildVersion())
        .write(model);
  }

  /*
   * Excel 2007 export
   */

  /** Perform download of Excel 2007 full model. */
  @SuppressWarnings("PMD.SignatureDeclareThrowsException")
  //Suppressed, because the method signature is requested by Spring's MultiAction (JavaDoc).
  public boolean downloadFullModelExcel2007(RequestContext requestContext, MassdataMemBean memBean, MessageContext messageContext) throws Exception {
    LOGGER.info("in ExcelExport2FrontendServiceImpl.downloadFullModelExcel2007()");
    HttpServletResponse response = extractResponse(requestContext);

    if (memBean.isPartialExport()) {
      RStructuredTypeExpression partialType = getCompiledMainExportType(memBean, false);
      return partialType == null ? false : doPartialExportExcel2007(requestContext, memBean, partialType, response);
    }
    else {
      return doExportExcel2007(requestContext, response);
    }
  }

  /** Check conditions for download of Excel 2007 full model. */
  @SuppressWarnings("PMD.SignatureDeclareThrowsException")
  //Suppressed, because the method signature is requested by Spring's MultiAction (JavaDoc).
  public boolean checkFullModelExcel2007(RequestContext requestContext, MassdataMemBean memBean, MessageContext messageContext) throws Exception {
    memBean.resetResultMessages();

    if (memBean.isPartialExport() && getCompiledMainExportType(memBean, true) == null) {
      return false;
    }

    triggerDownloadEvent(requestContext, "downloadFullModelExcel2007");
    return true;
  }

  private boolean doPartialExportExcel2007(RequestContext requestContext, MassdataMemBean memBean, RStructuredTypeExpression partialType,
                                           HttpServletResponse response) {
    BasePartialExportMetamodel partial;
    try {
      partial = new APartialExportMetamodel(elasticmiService.getRMetamodel(), partialType);
    } catch (Exception ex) {
      LOGGER.debug(ex);
      memBean.addErrorMessage("Invalid query, further information can be found in the iteraplan documentation");
      return false;
    }

    Workbook wb = new XlsModelMapper(partial, null, MessageListener.NOOP_LISTENER, IteraplanProperties.getProperties().getBuildVersion())
        .write(elasticmiService.getModel());
    writeExcelToOutputStream(response, wb, FULL_MODEL_OUTPUT_FILENAME + EXCEL_2007_EXTENTSION, MIME_TYPE_EXCEL_2007);
    markFinished(requestContext);
    return true;
  }

  /**
   * Compiles the main export type for a partial export from the information given in the MassdataMemBean.
   * Expects the memBean to hold the necessary information.
   * @param memBean
   *        MassdataMemBean holding the information about the main export type
   * @param generateMessages
   *        flag whether Messages should be generated or not.
   * @return The {@link RStructuredTypeExpression} which serves as the primary export type.
   *         Null if there was an error with compiling the type.
   */
  private RStructuredTypeExpression getCompiledMainExportType(MassdataMemBean memBean, boolean generateMessages) {
    IteraQlQuery queryResult;
    try {
      queryResult = elasticmiService.compile(createFilterString(memBean));
    } catch (IteraQl2Exception ex) {
      LOGGER.debug(ex);
      if (generateMessages) {
        memBean.addErrorMessage(ex.getMessage());
      }
      return null;
    }

    if (queryResult.isRight()) {
      // A Binding-Set is an invalid result
      if (generateMessages) {
        memBean.addErrorMessage("The partial export does not support a Filter with a Binding-Set as result");
      }
      return null;
    }

    return queryResult.getLeft();
  }

  private boolean doExportExcel2007(RequestContext requestContext, HttpServletResponse response) {
    Workbook wb = fillWorkbook(elasticmiService.getModel(), elasticmiService.getRMetamodel(), ExcelFormat.XLSX);
    writeExcelToOutputStream(response, wb, FULL_MODEL_OUTPUT_FILENAME + EXCEL_2007_EXTENTSION, MIME_TYPE_EXCEL_2007);

    markFinished(requestContext);
    return true;
  }

  private HttpServletResponse extractResponse(RequestContext requestContext) {
    try {
      return (HttpServletResponse) requestContext.getExternalContext().getNativeResponse();
    } catch (Exception e) {
      throw new IteraplanTechnicalException(new IllegalStateException("Can't extract HttpServletResponse from RequestContext."));
    }
  }

  private String createFilterString(MassdataMemBean memBean) {
    StringBuilder builder = new StringBuilder();
    builder.append(getElasticMiType(memBean.getFilteredTypeExport()));
    if (!memBean.getExtendedFilterExport().replaceAll(" ", "").isEmpty()) {
      builder.append("[");
      builder.append(memBean.getExtendedFilterExport());
      builder.append("]");
    }
    builder.append(";");
    return builder.toString();
  }

  // Get ElasticMi equivalent for TOBB without trailing "Release" (should concern only ISR and TCR)
  private String getElasticMiType(TypeOfBuildingBlock typeOfBuildingBlock) {
    String classicClassName = typeOfBuildingBlock.getAssociatedClass().getSimpleName();
    return StringUtils.removeEnd(classicClassName, "Release");
  }

}

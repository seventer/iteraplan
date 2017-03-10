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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import de.iteratec.iteraplan.businesslogic.service.ElasticMiService;
import de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelAttributesImportService;
import de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelImportService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.UserContext.Permissions;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.elasticmi.io.mapper.xls.XlsModelMapper;
import de.iteratec.iteraplan.elasticmi.io.mapper.xls.XlsModelMapper.ExcelFormat;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.model.impl.ModelImpl;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.dialog.GuiController;


/**
 * This class is responsible for controlling all user actions made within Excel Importer.
 * 
 * @author ogr
 */
@Controller
public class ExcelImportController extends GuiController {

  private static final Logger          LOGGER                              = Logger.getIteraplanLogger(ExcelImportController.class);

  @Autowired
  private ExcelImportService           excelImportService;
  @Autowired
  private ExcelAttributesImportService excelAttributesImportService;
  @Autowired
  private ElasticMiService             elasticMiService;

  /** MIME Content Type for Excel */
  private static final String          MIME_TYPE_MSEXCEL                   = "application/vnd.ms-excel";
  private static final String          INIT_VIEW                           = "excelimport/init";
  private static final String          DATA_TEMPLATE                       = "ExcelWorkbookTemplate.xls";
  private static final String          ATTRIBUTES_TEMPLATE                 = "ExcelAttributesWorkbookTemplate.xls";
  /** Object related permissions template file name. */
  private static final String          OBJECT_RELATED_PERMISSIONS_TEMPLATE = "ObjectRelatedPermissionsTemplate.xls";

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.presentation.dialog.GuiController#getDialogName()
   */
  @Override
  protected String getDialogName() {
    return Dialog.EXCELIMPORT.getDialogName();
  }

  /**
   * Populates the {@link ModelMap} for the jsp with the {@link ExcelImportDialogMemory}
   * saved in the {@link GuiContext}, or, if not existing yet, additionally creates it
   * and updates the GuiContext accordingly.
   * @return The {@link ExcelImportDialogMemory}
   */
  @ModelAttribute("dialogMemory")
  public ExcelImportDialogMemory getExcelImportDialogMemory() {
    GuiContext guiCtx = GuiContext.getCurrentGuiContext();

    if (guiCtx.hasDialogMemory(this.getDialogName())) {
      LOGGER.debug("Reading ExcelImportDialogMemory from GuiContext.");
      return (ExcelImportDialogMemory) guiCtx.getDialogMemory(this.getDialogName());
    }
    else {
      LOGGER.debug("Creating ExcelImportDialogMemory.");
      ExcelImportDialogMemory dialogMem = new ExcelImportDialogMemory();
      updateGuiContext(dialogMem);
      return dialogMem;
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * de.iteratec.iteraplan.presentation.dialog.GuiController#init(org.springframework.ui.ModelMap,
   * javax.servlet.http.HttpSession, javax.servlet.http.HttpServletRequest)
   */
  @Override
  @RequestMapping(method = RequestMethod.GET)
  protected void init(ModelMap model, HttpSession session, HttpServletRequest request) {
    super.init(model, session, request);

    LOGGER.debug("Checking permissions for excel import...");
    Permissions permissions = UserContext.getCurrentPerms();
    permissions.assureFunctionalPermission(TypeOfFunctionalPermission.EXCELIMPORT);
    LOGGER.debug("Done.");

    ExcelImportDialogMemory dialogMem = new ExcelImportDialogMemory();
    updateGuiContext(dialogMem);
    this.clearErrorFlags();
  }

  /**
   * Differentiates between requested activities and executes all required steps.
   */
  @RequestMapping(method = RequestMethod.POST)
  public String onSubmit(@ModelAttribute("dialogMemory") ExcelImportDialogMemory dialogMem, HttpSession session, MultipartHttpServletRequest req,
                         HttpServletResponse response) {

    Permissions permissions = UserContext.getCurrentPerms();
    permissions.assureFunctionalPermission(TypeOfFunctionalPermission.EXCELIMPORT);

    dialogMem.setImportLog(null);
    this.clearErrorFlags();

    if (dialogMem.getClickedButton().equals("button.excel.data_import")) {
      InputStream isExcel = getInputStreamFromFileIfValid(req.getFile("excelFile"));
      if (isExcel == null) {
        LOGGER.info("Wrong File type...");

        dialogMem.setWrongFileType(true);
        return INIT_VIEW;
      }

      LOGGER.info("Starting import of landscape data...");

      // idea behind using a PrintStream: in a future iteration, push log messages
      // to the client continuously, e.g. with Ajax
      // with a print stream, we get all the data delivered directly to the Controller
      // and don't have to wait for the import processing to complete
      StringWriter writer = new StringWriter();
      PrintWriter logWriter = new PrintWriter(writer);

      excelImportService.importLandscapeData(excelImportService.readLandscapeData(isExcel, logWriter));
      logWriter.flush();

      dialogMem.setImportLog(writer.toString());
      closeInputStream(isExcel);
      LOGGER.info("Finished import of landscape data.");
    }

    if (dialogMem.getClickedButton().equals("button.excel.enum_import")) {
      InputStream isExcel = getInputStreamFromFileIfValid(req.getFile("excelEnumFile"));
      if (isExcel == null) {
        dialogMem.setWrongEnumFileType(true);
        return INIT_VIEW;
      }

      LOGGER.info("Starting import of enumeration attributes...");

      StringWriter writer = new StringWriter();
      PrintWriter logWriter = new PrintWriter(writer);

      excelAttributesImportService.importAttributes(isExcel, logWriter);
      logWriter.flush();
      dialogMem.setImportLog(writer.toString());
      closeInputStream(isExcel);
      LOGGER.info("Finished import of enumeration attributes.");
    }

    if (dialogMem.getClickedButton().equals("button.excel.objrelperm_import")) {
      InputStream isExcel = getInputStreamFromFileIfValid(req.getFile("excelUserFile"));
      if (isExcel == null) {
        dialogMem.setWrongUserFileType(true);
        return INIT_VIEW;
      }

      LOGGER.info("Starting import of object related permissions data...");

      StringWriter writer = new StringWriter();
      PrintWriter logWriter = new PrintWriter(writer);

      excelImportService.importObjectRelatedPermissions(isExcel, logWriter);
      logWriter.flush();

      dialogMem.setImportLog(writer.toString());
      closeInputStream(isExcel);
      LOGGER.info("Finished import of object related permissions data.");
    }

    if (dialogMem.getClickedButton().equals("button.excel.download_data_temp")) {
      this.downloadFile(response, DATA_TEMPLATE);
      LOGGER.info("Provided the template for landscape data.");
    }

    if (dialogMem.getClickedButton().equals("button.excel.download_enum_temp")) {
      this.downloadFile(response, ATTRIBUTES_TEMPLATE);
      LOGGER.info("Provided the template for enumeration attributes.");
    }

    if (dialogMem.getClickedButton().equals("button.excel.download_objrelperm_temp")) {
      this.downloadFile(response, OBJECT_RELATED_PERMISSIONS_TEMPLATE);
      LOGGER.info("Provided the template for user data.");
    }

    experimentalExport(dialogMem, response);

    return INIT_VIEW;
  }

  private void experimentalExport(ExcelImportDialogMemory dialogMem, HttpServletResponse response) {
    //FIXME agu this is experimental feature
    if (dialogMem.getClickedButton().equals("button.excel.download_excel_data_experimental")) {

      //Note: Using NOOP message listener here
      Workbook wb = new XlsModelMapper(elasticMiService.getRMetamodel(), null, MessageListener.NOOP_LISTENER, ExcelFormat.XLS, IteraplanProperties
          .getProperties().getBuildVersion()).write(elasticMiService.getModel());
      try {
        response.setContentType(MIME_TYPE_MSEXCEL);
        response.setHeader("Content-Disposition", "attachment;filename=iteraplanExcelData.xls");
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
      } catch (IOException e) {
        LOGGER.error("Excel Export error: ", e);
      }
    }
    else if (dialogMem.getClickedButton().equals("button.excel.download_excel_template_experimental")) {

      //Node: Using NOOP message listener here
      Workbook wb = new XlsModelMapper(elasticMiService.getRMetamodel(), null, MessageListener.NOOP_LISTENER, ExcelFormat.XLS, IteraplanProperties
          .getProperties().getBuildVersion()).write(new ModelImpl());
      try {
        response.setContentType(MIME_TYPE_MSEXCEL);
        response.setHeader("Content-Disposition", "attachment;filename=iteraplanExcelTemplate.xls");
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
      } catch (IOException e) {
        LOGGER.error("Excel Export error: ", e);
      }
    }
  }

  /**
   * Tries to close the input stream, if an exception occurs it is caught and logged.
   * 
   * @param isExcel
   *          the input stream to close
   */
  private void closeInputStream(InputStream isExcel) {
    try {
      // make sure that the inputStream is definitely closed and resources can be cleaned up
      isExcel.close();
    } catch (IOException ioe) {
      // can't do anything but log the error
      LOGGER.warn("Could not close InputStream on uploaded file. The file might now stay around somewhere", ioe);
    }
  }

  /**
   * Test if file is valid (via checkFile()), and return InputStream if so
   * 
   * @return InputStream from a file if it is valid; null otherwise
   */
  private InputStream getInputStreamFromFileIfValid(MultipartFile file) {
    // Check if Valid
    if (!this.checkFile(file)) {
      return null;
    }

    // Convert File to Input Stream
    InputStream is;
    try {
      is = file.getInputStream();
    } catch (IOException e) {
      LOGGER.error(e);
      return null;
    }
    return is;
  }

  /**
   * Tests if the given <code>file</code> is an excel file in version 2003 or 2007.
   * 
   * @param file
   *          the file to be tested
   * @return <code>true</code> if <code>file</code> is an excel file, <code>false</code> if
   *         <code>file</code> is <code>null</code> or has wrong type.
   */
  private boolean checkFile(MultipartFile file) {
    if (file == null) {
      return false;
    }
    // checks for Office Version 2003; Office 2007 format is not yet supported
    if (file.getOriginalFilename().endsWith(".xls")) {
      return true;
    }
    return false;
  }

  /**
   * Clears the error flags
   */
  private void clearErrorFlags() {
    this.getExcelImportDialogMemory().setWrongFileType(false);
    this.getExcelImportDialogMemory().setWrongEnumFileType(false);
    this.getExcelImportDialogMemory().setWrongUserFileType(false);
  }

  /**
   * Writes the content of the file with the given <code>fileName</code> into the output stream of
   * <code>response</code> and sets all required headers.
   * 
   * @param response
   *          the response to write the file
   * @param fileName
   *          the name of the file to be written
   */
  private void downloadFile(HttpServletResponse response, String fileName) {
    InputStream input = null;
    OutputStream out = null;

    try {
      response.setContentType(MIME_TYPE_MSEXCEL);
      response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

      input = this.getClass().getResourceAsStream("/" + fileName);
      byte[] buffer = new byte[4 * 1024];
      out = response.getOutputStream();
      int bytesRead = input.read(buffer);

      while (bytesRead != -1) {
        out.write(buffer, 0, bytesRead);
        bytesRead = input.read(buffer);
      }

    } catch (SocketException e) {
      // happens if the user cancels the download
      LOGGER.info("Download of data template cancelled by user or network error.", e);
    } catch (IOException e) {
      LOGGER.error("Failed to download the data template.", e);
    } finally {
      IOUtils.closeQuietly(input);
      IOUtils.closeQuietly(out);
    }
  }
}

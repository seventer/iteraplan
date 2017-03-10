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
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.excelimport.ImportService;
import de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.importer.TimeseriesImportService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.presentation.dialog.CommonFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.dialog.ExcelImport.MassdataMemBean.ImportType;


/**
 * Handles the Excel Import front end
 */
@Service("importFrontendService")
public class ImportFrontendServiceImpl extends CommonFrontendServiceImpl {

  private static final Logger     LOGGER            = Logger.getIteraplanLogger(ImportFrontendServiceImpl.class);

  private static final long       MAX_FILESIZE_BYTE = 25 * 1024 * 1024;                                          // 25 Mb

  @Autowired
  private ImportService           importService;
  @Autowired
  private TimeseriesImportService timeseriesImportService;

  @Override
  protected String getFlowId() {
    return Dialog.IMPORT.getFlowId();
  }

  public MassdataMemBean getInitialMemBean() {
    return new MassdataMemBean();
  }

  /**
   * Attempts to upload and import a timeseries from the excel-file data in the given ImportMemBean.
   * @param memBean
   *          The ImportMemBean where the file to import is held
   * @return True if an import was attempted (even if there were errors during the import), false
   *         if there was a problem with the file so that the import wasn't even initiated.
   */
  public boolean uploadTimeseries(MassdataMemBean memBean) {
    LOGGER.info("in ImportFrontendServiceImpl.uploadTimeseries()");

    if (!handleTimeseriesFile(memBean, memBean.getTimeseriesFileToUpload())) {
      // validation of the uploaded file failed
      return false;
    }

    // start the import, handle exceptions gracefully
    try {
      timeseriesImportService.importTimeseries(memBean);

    } catch (IteraplanTechnicalException e) {
      if (IteraplanErrorMessages.GENERAL_IMPORT_ERROR == e.getErrorCode()) {
        LOGGER.error(e);
      }
      else {
        throw new IteraplanBusinessException(e);
      }
    }
    return true;
  }

  private boolean handleTimeseriesFile(MassdataMemBean memBean, MultipartFile file) {
    memBean.resetResultMessages();
    if (file == null || file.getSize() == 0) {
      memBean.addErrorMessage(MessageAccess.getString("import.no.file"));
      return false;
    }

    String originalFilename = file.getOriginalFilename();
    memBean.setFileName(originalFilename);

    if (!hasAcceptableExcelFileExtension(originalFilename)) {
      memBean.setImportType(null);
      memBean.setFileContent(null);
      memBean.addErrorMessage(MessageAccess.getString("import.wrong.type"));
      return false;
    }

    memBean.setImportType(ImportType.EXCEL);
    setFileContentToMemBean(memBean, file);
    return true;
  }

  public boolean upload(MassdataMemBean memBean) {
    LOGGER.info("in ImportFrontendServiceImpl.upload()");

    if (!handleUploadedFile(memBean)) {
      return false;
    }

    return importService.doImport(memBean);
  }

  public boolean compareMetamodel(MassdataMemBean memBean) {
    return importService.compareMetamodel(memBean);
  }

  public boolean mergeMetamodel(MassdataMemBean memBean) {
    return importService.mergeMetamodel(memBean);
  }

  public boolean modelDryrun(MassdataMemBean memBean) {
    return importService.modelDryrun(memBean);
  }

  public boolean writeToDb(MassdataMemBean memBean) {
    return importService.mergeModelToDb(memBean);
  }

  public void removeCurrentImport(MassdataMemBean memBean) {
    importService.removeCurrentImport(memBean);
  }

  /**
   * @param memBean
   * @return true if successful, false if not.
   */
  private boolean handleUploadedFile(MassdataMemBean memBean) {
    memBean.resetResultMessages();
    MultipartFile file = memBean.getFileToUpload();
    if (file == null || file.getSize() == 0) {
      memBean.addErrorMessage(MessageAccess.getString("import.no.file"));
      return false;
    }
    if (memBean.getImportStrategy() == null) {
      memBean.addErrorMessage(MessageAccess.getString("import.no.strategy"));
      return false;
    }
    //TODO use the import strategy

    String originalFilename = file.getOriginalFilename();
    memBean.setFileName(originalFilename);

    if (determineImportType(memBean, originalFilename)) {
      if (file.getSize() > MAX_FILESIZE_BYTE) {
        memBean.addErrorMessage(MessageFormat.format(MessageAccess.getString("import.too.large"), Long.valueOf(MAX_FILESIZE_BYTE)));
        return false;
      }

      setFileContentToMemBean(memBean, file);
      return true;
    }

    return false;
  }

  private void setFileContentToMemBean(MassdataMemBean memBean, MultipartFile file) {
    try {
      memBean.setFileContent(file.getBytes());
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("File length: {0}", Integer.valueOf(file.getBytes().length));
      }
    } catch (IOException e) {
      LOGGER.error("Error reading file \"{0}\"", file.getOriginalFilename());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
    }
  }

  /**
   * Determines whether the file to upload is an xmi or excel file. If neither,
   * according error messages are set on the memory bean.
   * @param memBean
   *          the memory bean
   * @param originalFileName name of the file that was uploaded
   * @return true if an xmi or excel file was recognized, false otherwise.
   */
  private boolean determineImportType(MassdataMemBean memBean, String originalFileName) {
    if (originalFileName.endsWith(".zip")) {
      memBean.setImportType(ImportType.XMI);
      return true;
    }
    if (hasAcceptableExcelFileExtension(originalFileName)) {
      memBean.setImportType(ImportType.EXCEL);
      return true;
    }

    // error case starts here
    memBean.setImportType(null);
    memBean.setFileContent(null);
    memBean.addErrorMessage(MessageAccess.getString("import.wrong.type2"));
    return false;
  }

  /**
   * @param fileName
   * @return {@code true} if the file name extension is either xls, xlsx or xlsm (case-insensitve)
   */
  protected boolean hasAcceptableExcelFileExtension(String fileName) {
    return StringUtils.endsWithIgnoreCase(fileName, ".xls") || StringUtils.endsWithIgnoreCase(fileName, ".xlsx")
        || StringUtils.endsWithIgnoreCase(fileName, ".xlsm");
  }

}

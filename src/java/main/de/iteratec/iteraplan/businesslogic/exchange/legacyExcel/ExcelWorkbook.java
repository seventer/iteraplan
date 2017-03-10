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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateLocatorService;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateLocatorServiceImpl;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


public abstract class ExcelWorkbook {

  private static final Logger    LOGGER                    = Logger.getIteraplanLogger(ExcelWorkbook.class);

  private Workbook               wb;

  public static final String     TEMPLATE_DIRECTORY        = "/templates";

  public static final String     EXCEL_2003_TEMPLATE_FILE  = "ExcelWorkbook.xls";
  public static final String     EXCEL_2007_TEMPLATE_FILE  = "ExcelWorkbook.xlsx";

  /** Specifying the default font name */
  protected static final String  EXCEL_DEFAULT_FONT        = "Arial";

  /**
   * The name of the sheet that import and export use for storing configuration facets, such as the workbook locale
   */
  public static final String     DEFAULT_SHEET_KEY         = "ExcelTemplateSheet";

  /**
   * The Excel file format has an arbitrary limit on the length of worksheet names. This must be considered during import in particular.
   */
  protected static final int     MAXIMUM_SHEET_NAME_LENGTH = 32;

  /** Locale for the Strings in the workbook */
  private Locale                 locale;

  /** TemplateType in this workbook */
  // default is Excel 2007
  private TemplateType           templateType              = TemplateType.EXCEL_2007;

  /** 
   * TemplateLocatorService 
   */
  private TemplateLocatorService templateLocatorService    = new TemplateLocatorServiceImpl(TEMPLATE_DIRECTORY);

  /**
   * Load a workbook from a given template type and template filename
   */
  protected void loadWorkbookFromTemplateFileName(TemplateType templateTypeParameter, String assignedTemplateFileName) {

    LOGGER.debug("Loading Template as Input File [" + assignedTemplateFileName + "]");

    this.templateType = templateTypeParameter;

    String templateFileName;
    if (assignedTemplateFileName.isEmpty()) {
      templateFileName = getDefaultTemplateForTemplateType();
    }
    else {
      templateFileName = assignedTemplateFileName;
    }

    File templateFile = templateLocatorService.getFile(templateType, templateFileName);

    InputStream is = null;

    try {
      is = FileUtils.openInputStream(templateFile);
    } catch (FileNotFoundException e) {
      String msg = String.format("The template file %s could not be found", templateFile);
      LOGGER.error(msg, e);
      throw new IteraplanBusinessException(IteraplanErrorMessages.INVALID_EXCEL_TEMPLATE, msg, e);
    } catch (IOException e) {
      String msg = String.format("The template file %s is a directory or could not be read", templateFile);
      LOGGER.error(msg, e);
      throw new IteraplanBusinessException(IteraplanErrorMessages.INVALID_EXCEL_TEMPLATE, msg, e);
    }

    loadWorkbookFromInputStream(is);
  }

  /**
   * Load a workbook from an InputStream
   */
  protected void loadWorkbookFromInputStream(InputStream excelWorkbookInputStream) {

    if (excelWorkbookInputStream == null) {
      String msg = "The input stream is null.";
      LOGGER.debug(msg);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INVALID_EXCEL_TEMPLATE, msg);
    }
    else {
      LOGGER.debug("Loading Input File");
    }

    try {
      this.wb = WorkbookFactory.create(excelWorkbookInputStream);
    } catch (IOException iex) {
      LOGGER.debug(iex);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INVALID_EXCEL_TEMPLATE, "The Excel file could not be read.", iex);
    } catch (InvalidFormatException fex) {
      LOGGER.debug(fex);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INVALID_EXCEL_TEMPLATE, "Unknown file format.", fex);
    }

    LOGGER.debug("Input file loaded");
  }

  /**
   * Get the default template for a given template type
   */
  private String getDefaultTemplateForTemplateType() {
    if (TemplateType.EXCEL_2003.equals(getTemplateType())) {
      return EXCEL_2003_TEMPLATE_FILE;
    }
    else {
      return EXCEL_2007_TEMPLATE_FILE;
    }
  }

  /**
   * @return Returns the locale.
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * @param locale
   *          The locale to set.
   */
  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  /**
   * @return Returns the Workbook.
   */
  protected Workbook getWb() {
    return wb;
  }

  /**
   * @param wb
   *          The Workbook to set.
   */
  protected void setWb(Workbook wb) {
    this.wb = wb;
  }

  /**
   * @return Returns the templateType.
   */
  public TemplateType getTemplateType() {
    return templateType;
  }

  /**
   * @param templateType
   *          The TemplateType to set.
   */
  protected void setTemplateType(TemplateType templateType) {
    this.templateType = templateType;
  }
}

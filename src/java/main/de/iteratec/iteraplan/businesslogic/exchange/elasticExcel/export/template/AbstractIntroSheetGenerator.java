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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.springframework.core.io.Resource;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.IteraplanProperties;


/**
 *  This class is responsible for the creation of the introduction sheet
 */
public abstract class AbstractIntroSheetGenerator {

  private static final Logger LOGGER           = Logger.getIteraplanLogger(AbstractIntroSheetGenerator.class);

  private Resource            logoImage;

  protected static final int  LOGO_COL         = 0;

  protected static final int  LOGO_ROW         = 0;

  protected static final int  DATE_CELL_ROW    = 1;

  protected static final int  VERSION_CELL_ROW = 2;

  protected static final int  VALUE_CELL_COL   = 2;

  protected static final int  SUMMARY_ROW      = 6;

  protected static final int  SUMMARY_COL      = 0;

  public static final String  INTRO_SHEET_NAME = "Introduction";

  /**
   * Default constructor.
   * @param logoImage 
   */
  public AbstractIntroSheetGenerator(Resource logoImage) {
    this.logoImage = logoImage;
  }

  /**
   * Fills the introduction sheet with content overview, export data and logo
   */
  public void generateIntroduction() {

    insertIntroductionData(getIntroductionSheet());

    createSummary();

    insertIteraplanLogo(LOGO_COL, LOGO_ROW);

  }

  protected Sheet getIntroductionSheet() {
    Sheet introSheet = getWorkbook().getSheet(INTRO_SHEET_NAME);
    if (introSheet == null) {
      introSheet = getWorkbook().createSheet(INTRO_SHEET_NAME);
      getWorkbook().setSheetOrder(INTRO_SHEET_NAME, 0);
    }
    return introSheet;
  }

  protected abstract Workbook getWorkbook();

  protected abstract void createSummary();

  private void insertIteraplanLogo(int colIndex, int rowIndex) {
    CreationHelper helper = getWorkbook().getCreationHelper();

    byte[] bytes;
    try {
      InputStream is = logoImage.getInputStream();
      bytes = IOUtils.toByteArray(is);
    } catch (IOException e) {
      LOGGER.error("Could not read the excel template!", e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INVALID_EXCEL_TEMPLATE);
    }
    int pictureIdx = getWorkbook().addPicture(bytes, Workbook.PICTURE_TYPE_PNG);

    Sheet sheet = getIntroductionSheet();
    Drawing drawing = sheet.createDrawingPatriarch();
    ClientAnchor anchor = helper.createClientAnchor();
    //set top-left corner of the picture
    anchor.setCol1(colIndex);
    anchor.setRow1(rowIndex);
    Picture pict = drawing.createPicture(anchor, pictureIdx);

    pict.resize();
  }

  /**
   * Fills some initial data in the introduction sheet, e.g. date and version
   * 
   * @param workbook
   */
  private void insertIntroductionData(Sheet introSheet) {

    // create  and fill cell for the date
    Row dateRow = introSheet.createRow(DATE_CELL_ROW);
    Cell dateCell = dateRow.createCell(VALUE_CELL_COL);
    dateCell.setCellValue(MessageAccess.getStringOrNull("global.date"));
    Cell dateValueCell = dateRow.createCell(VALUE_CELL_COL + 1);
    dateValueCell.setCellValue(DateUtils.formatAsString(new Date(System.currentTimeMillis()), UserContext.getCurrentLocale()));

    // create and fill cell for the version
    Row versionRow = introSheet.createRow(VERSION_CELL_ROW);
    Cell versionCell = versionRow.createCell(VALUE_CELL_COL);
    versionCell.setCellValue(MessageAccess.getStringOrNull("global.version"));
    Cell versionValueCell = versionRow.createCell(VALUE_CELL_COL + 1);
    versionValueCell.setCellValue(IteraplanProperties.getProperties().getBuildVersion());
  }

  protected void adjustSheetColumnWidths() {
    // it has to be done last because setting the width before inserting the image would stretch it
    getIntroductionSheet().setColumnWidth(SUMMARY_COL, 8000);
    getIntroductionSheet().setColumnWidth(SUMMARY_COL + 1, 16500);

  }
}

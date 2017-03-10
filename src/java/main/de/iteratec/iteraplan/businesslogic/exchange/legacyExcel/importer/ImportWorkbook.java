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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer;

import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.ExcelWorkbook;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * An {@link ExcelWorkbook} extension, containing common functionality for importing
 * Excel files.
 */
public class ImportWorkbook extends ExcelWorkbook {
  
  private static ThreadLocal<ProcessingLog> processingLog = new ThreadLocal<ProcessingLog>();

  public ImportWorkbook(ProcessingLog userLog) {
    processingLog.set(userLog);
  }

  public static ProcessingLog getProcessingLog() {
    return processingLog.get();
  }

  public static void removeProcessingLog() {
    processingLog.remove();
  }

  protected String findExcelSheetNameFor(TypeOfBuildingBlock tob) {
    String fullBbTypeName = MessageAccess.getStringOrNull(tob.getPluralValue(), this.getLocale());
    // truncate it to the maximum allowable Excel Sheet name length
    return fullBbTypeName.substring(0, Math.min(fullBbTypeName.length(), MAXIMUM_SHEET_NAME_LENGTH - 1));
  }

  /**
   * Initially reads the complete Excel workbook and calculates all formulaCells. After that, we can
   * be sure that no cell contains formulas any longer, but they all contain values (or nothing).
   */
  protected void calculateAllFormulas() {
    FormulaEvaluator evaluator = new HSSFFormulaEvaluator((HSSFWorkbook) getWb());

    // iterate over all sheets, their rows, and all their cells
    for (int sheetNum = 0; sheetNum < getWb().getNumberOfSheets(); sheetNum++) {
      Sheet sheet = getWb().getSheetAt(sheetNum);

      for (Row row : sheet) {
        for (Cell cell : row) {

          if ((cell.getCellType() == Cell.CELL_TYPE_FORMULA) && (cell.getCellFormula() != null)) {
            try {
              evaluator.evaluateInCell(cell);
            } catch (Exception e) {
              getProcessingLog().error("Error calculating the formula in cell [" + ExcelImportUtilities.getCellRef(cell)
                  + "] on sheet '"+ sheet.getSheetName() + "', using cached value instead: " + e.getLocalizedMessage());
            }
          }
        }
      }
    }
  }

  protected boolean readInConfigSheet() {
    // Nullify variables that must be read in from this config
    this.setLocale(null);

    Sheet sheetConfig = getWb().getSheet(DEFAULT_SHEET_KEY);
    if (sheetConfig == null) {
      getProcessingLog().error(DEFAULT_SHEET_KEY + " not found.");
      return false;
    }

    // Read in various settings (Config page must have all its variable names in English)
    for (int curRow = 0; curRow <= sheetConfig.getLastRowNum(); curRow++) {

      Row row = sheetConfig.getRow(curRow);
      if (row != null) {
        String key = ExcelImportUtilities.contentAsString(row.getCell(0), getProcessingLog());

        if ("Locale".equals(key)) {
          String content = ExcelImportUtilities.contentAsString(row.getCell(1), getProcessingLog());
          this.setLocale(new Locale(content));
        }
      }
    }

    return this.getLocale() != null;
  }

  /**
   * Returns the content row index of the specified {@code sheet}. It tries to
   * find the head column, with the name of the specified {@code idKey}, if it
   * does, the next row should be content row.
   * 
   * @param sheet the sheet to get content row index for
   * @param headColumnName the localized head column name
   * @param columnNumber the column to look for the key in
   * @return the content row index of the specified {@code sheet} or {@code -1}, if
   *    the content could not be found
   */
  protected int findSheetContentPosition(Sheet sheet, String headColumnName, int columnNumber) {
    int curRow = 0;
    final int lastRow = ExcelImportUtilities.getLastRow(sheet);

    while (curRow <= lastRow) {
      Row row = sheet.getRow(curRow);

      if (row != null) {
        String content = ExcelImportUtilities.contentAsString(row.getCell(columnNumber), getProcessingLog());

        if (content.startsWith(headColumnName)) {
          return curRow + 1;
        }
      }
      curRow++;
    }

    // Failed to find head row
    return -1;
  }
}

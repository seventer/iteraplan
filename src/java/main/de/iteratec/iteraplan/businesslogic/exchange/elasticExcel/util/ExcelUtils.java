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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * Utility methods for dealing with Excel files.
 */
public final class ExcelUtils {

  /** Logger. */
  private static final Logger LOGGER                = Logger.getIteraplanLogger(ExcelUtils.class);

  private static final int    SHEET_NAME_MAX_LENGTH = 31;

  /**
   * Private default constructor. Avoid instantiation.
   */
  private ExcelUtils() {
    // nothing to do.
  }

  /**
   * @param filename
   * 
   * @return the {@link Workbook}.
   * 
   * @throws IteraplanTechnicalException if opening the file failed for any reason.
   */
  public static Workbook openExcelFile(String filename) {
    try {
      InputStream in = new FileInputStream(filename);
      return openExcelFile(in);
    } catch (FileNotFoundException e) {
      String msg = MessageFormat.format("Error opening file {0}: {1}", filename, e.getMessage());
      LOGGER.error(msg);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, msg);
    }
  }

  /**
   * @param in InputStream to read.
   * 
   * @return the {@link Workbook}.
   * 
   * @throws IteraplanTechnicalException if opening the file failed for any reason.
   */
  public static Workbook openExcelFile(InputStream in) {
    Workbook workbook = null;

    try {
      workbook = WorkbookFactory.create(in);
    } catch (InvalidFormatException e) {
      String msg = MessageFormat.format("Error reading excel workbook: InvalidFormatException {0}", e.getMessage());
      LOGGER.error(msg);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, msg);
    } catch (IOException e) {
      String msg = MessageFormat.format("Error reading excel workbook: IOException {0}", e.getMessage());
      LOGGER.error(msg);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, msg);
    } finally {
      IOUtils.closeQuietly(in);
    }

    return workbook;
  }

  /**
   * Returns a CellReference containing the cell's sheet name, as opposed to the standard
   * {@link CellReference#CellReference(Cell)} constructor.
   * @param cell
   *          Cell to create a CellReference from
   * @return The CellReference including the sheet name
   */
  public static CellReference getFullCellReference(Cell cell) {
    return new CellReference(cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex(), true, true);
  }

  /**
   * Returns a CellReference containing the given sheet's sheet name,
   * and row and column of the given cell reference
   * @param sheet
   * @param cellReference
   * @return The CellReference including the sheet name
   */
  public static CellReference getFullCellReference(Sheet sheet, CellReference cellReference) {
    return new CellReference(sheet.getSheetName(), cellReference.getRow(), cellReference.getCol(), true, true);
  }

  /**
   * Returns the full name of the cell, including the sheet name.
   * @param cell
   * @return name of the cell, like "Sheet-1 A-15". Returns <code>"&lt;no-cell&gt;"</code> if cell is null.
   */
  public static String getFullCellName(Cell cell) {
    if (cell == null) {
      return "<no-cell>";
    }

    return getCellRefName(getFullCellReference(cell));
  }

  /**
   * Returns the full name of the cell described by the given cell reference,
   * including the sheet name if the cell reference contains one.
   * @param cellReference
   * @return name of the cell, like "Sheet-1 A-15". Returns <code>"&lt;no-cell&gt;"</code> if cellReference is null.
   */
  public static String getCellRefName(CellReference cellReference) {
    if (cellReference == null) {
      return "<no-cell>";
    }

    String formattedCellReference = cellReference.formatAsString();
    formattedCellReference = formattedCellReference.replace("'", ""); // remove single quote delimiters if existing
    formattedCellReference = formattedCellReference.replace("!$", " ");
    formattedCellReference = formattedCellReference.replace("$", "-");
    return formattedCellReference;
  }

  /**
   * @param cell
   * @return true if cell is empty, false if not.
   */
  @SuppressWarnings({ "PMD.MissingBreakInSwitch" })
  public static boolean isEmptyCell(Cell cell) {
    if (cell == null) {
      return true;
    }
    switch (cell.getCellType()) {
      case Cell.CELL_TYPE_BLANK:
        return true;
      case Cell.CELL_TYPE_BOOLEAN:
        return false;
      case Cell.CELL_TYPE_ERROR:
        return true;
      case Cell.CELL_TYPE_FORMULA:
        return StringUtils.isEmpty(cell.getCellFormula());
      case Cell.CELL_TYPE_NUMERIC:
        return false;
      case Cell.CELL_TYPE_STRING:
        return StringUtils.isEmpty(cell.getStringCellValue());
      default: // impossible.
        return true;
    }
  }

  /**
   * get the attribute from the cell. Do basic type conversions.
   * 
   * @param cell the cell to read
   * @param convertNumberToDate if true, AND if the cell type is numeric, convert the cell value to Date; use the 
   *        numeric value otherwise.
   * 
   * @return the value from the cell, or null if cell is empty.
   */
  @SuppressWarnings({ "PMD.MissingBreakInSwitch", "boxing" })
  public static Object getCellValue(Cell cell, boolean convertNumberToDate) {

    switch (cell.getCellType()) {
      case Cell.CELL_TYPE_BLANK:
        return null;
      case Cell.CELL_TYPE_BOOLEAN:
        return cell.getBooleanCellValue();
      case Cell.CELL_TYPE_NUMERIC:
        if (convertNumberToDate) {
          return cell.getDateCellValue();
        }
        else {
          return cell.getNumericCellValue();
        }
      case Cell.CELL_TYPE_STRING:
        return StringUtils.trim(cell.getStringCellValue());
      case Cell.CELL_TYPE_ERROR:
        LOGGER.error("Error in cell {0}: contains Error", ExcelUtils.getFullCellName(cell));
        return null;
      case Cell.CELL_TYPE_FORMULA:
        return cell.getCellFormula();
      default: // impossible.
        LOGGER.error("Error in cell {0}: contains unknown cell type.", ExcelUtils.getFullCellName(cell));
        return null;
    }
  }

  /**
   * Get the cell's value as string. Non-string-type cell values are converted to string, if possible.
   * @param cell
   *          {@link Cell} to read.
   * @return String representing the cell's content; empty string for empty cells
   */
  public static String getStringCellValue(Cell cell) {
    if (cell == null) {
      return null;
    }
    cell.setCellType(Cell.CELL_TYPE_STRING);
    Object cellValue = getCellValue(cell, false);
    if (cellValue == null) {
      return null;
    }
    else {
      return String.valueOf(cellValue);
    }
  }

  /**
   * @param sheet
   * @param rowNumber
   * @return the row with the given number. Creates the row if it does not exist.
   */
  public static Row getOrCreateRow(Sheet sheet, int rowNumber) {
    Row row = sheet.getRow(rowNumber);
    if (row == null) {
      row = sheet.createRow(rowNumber);
    }
    return row;
  }

  /**
   * @param row
   * @param colNumber
   * @return the cell with the given number. Creates the cell if it does not exist.
   */
  public static Cell getOrCreateCell(Row row, int colNumber) {
    Cell cell = row.getCell(colNumber);
    if (cell == null) {
      cell = row.createCell(colNumber);
    }
    return cell;
  }

  public static String makeSheetNameValid(String originalName, Workbook workbook) {
    String resultName = originalName;
    if (originalName.length() >= SHEET_NAME_MAX_LENGTH) {
      resultName = resultName.substring(0, SHEET_NAME_MAX_LENGTH);
    }

    resultName = resultName.replace('[', '(');
    resultName = resultName.replace(']', ')');
    resultName = resultName.replace('*', '+');
    resultName = resultName.replace('/', '-');
    resultName = resultName.replace('\\', '-');
    resultName = resultName.replace('?', '!');
    resultName = resultName.replace(':', ';');

    if (workbook.getSheet(resultName) == null) {
      return resultName;
    }

    String namePrefix = resultName;
    if (namePrefix.length() > SHEET_NAME_MAX_LENGTH - 4) {
      namePrefix = namePrefix.substring(0, SHEET_NAME_MAX_LENGTH - 4);
    }
    for (int count = 2; count < 1000; count++) {
      resultName = namePrefix + "-" + String.format("%03d", Integer.valueOf(count));
      if (workbook.getSheet(resultName) == null) {
        return resultName;
      }
    }
    // very unlikely, needs more than 999 names starting with the same 31 characters
    throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
  }

}

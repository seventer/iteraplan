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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;


/**
 * Contains the Excel Sheet related information like the {@link Sheet} instance, first data 
 * row and other.
 */
public class SheetContext {

  public static final String    NAME_COLUMN             = "name";
  public static final String    PARENT_COLUMN           = "parent";
  public static final String    FULL_PARENT_NAME_COLUMN = "Full parent name";

  private final Sheet           sheet;
  private final NamedExpression expression;
  private List<ColumnContext>   sheetColumns            = Lists.newArrayList();
  private int                   headerRowNumber;
  private int                   firstDataRowNumber;
  private int                   emptyRowsToAppend;

  /** constructor. */
  public SheetContext(Sheet sheet, NamedExpression ute) {
    this.sheet = sheet;
    this.expression = ute;
  }

  /**
   * Returns the {@link Sheet} instance.
   * @return sheet the {@link Sheet} instance
   */
  public Sheet getSheet() {
    return sheet;
  }

  /**
   * Returns the sheet name.
   * @return the sheet name
   */
  public String getSheetName() {
    return sheet.getSheetName();
  }

  /**
   * 
   * @return headerRowNumber the Header Row number
   */
  public int getHeaderRowNumber() {
    return headerRowNumber;
  }

  public void setHeaderRowNumber(int headerRowNumber) {
    this.headerRowNumber = headerRowNumber;
  }

  /**
   * 
   * @return firstDataRowNumber the first Data Row number
   */
  public int getFirstDataRowNumber() {
    return firstDataRowNumber;
  }

  public void setFirstDataRowNumber(int firstDataRowNumber) {
    this.firstDataRowNumber = firstDataRowNumber;
  }

  /**
   * @return emptyRowsToAppend the emptyRowsToAppend
   */
  public int getEmptyRowsToAppend() {
    return emptyRowsToAppend;
  }

  public void setSetEmptyRowsToAppend(int emptyRowsToAppend) {
    this.emptyRowsToAppend = emptyRowsToAppend;
  }

  /**
   * @return ute the ute
   */
  public NamedExpression getExpression() {
    return expression;
  }

  public List<ColumnContext> getColumns() {
    return sheetColumns;
  }

  public void addColumn(FeatureExpression<?> expr, Cell cell) {
    ColumnContext column = new ColumnContext(expr, cell);
    addColumn(column);
  }

  public void addColumn(ColumnContext col) {
    sheetColumns.add(col);
  }

  /**
   * Returns the total count of the header columns.
   * 
   * @return the total count of the header columns
   */
  public int getColumnCount() {
    return sheetColumns.size();
  }

  public ColumnContext getColumnByPersistentName(String name) {
    for (ColumnContext column : sheetColumns) {
      if (column.getFeatureExpression().getPersistentName().equals(name)) {
        return column;
      }
    }

    return null;
  }

  public short getColumnFullParentNameColumnNumber() {
    Row row = getSheet().getRow(getHeaderRowNumber());
    for (short cellnum = row.getFirstCellNum(); cellnum <= row.getLastCellNum(); cellnum++) {
      Cell cell = row.getCell(cellnum);
      if (SheetContext.FULL_PARENT_NAME_COLUMN.equals(cell.getStringCellValue())) {
        return cellnum;
      }
    }
    return -1;
  }

  /**{@inheritDoc}**/
  @Override
  public String toString() {
    return sheetColumns.toString();
  }

  /**
   * @param dataSetSize
   * @return the last Data row according to the first data row, the given dataSetSize and the empty rows to append
   */
  public int calculateLastDataRow(int dataSetSize) {
    return getFirstDataRowNumber() + dataSetSize + getEmptyRowsToAppend() - 1;
  }

}

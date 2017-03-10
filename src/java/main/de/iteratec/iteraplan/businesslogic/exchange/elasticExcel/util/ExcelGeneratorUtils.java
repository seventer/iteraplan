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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.IteraExcelStyle;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.SheetContextComparator;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.ColumnContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.SheetContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.WorkbookContext;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitivePropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpressionOrder;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpressionOrderFix;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;


/**
 * Utilities required for the Excel template generation.
 */
public final class ExcelGeneratorUtils {

  /** Default constructor. */
  private ExcelGeneratorUtils() {
    // prevent instances of this class
  }

  /**
   * Adds dropdowns to cells in all enumeration value columns. The number of formatted rows is defined by parameter {@code dataSetSize}.
   * Iterates over all EnumerationPropertyExpression that belong to the UniversalTypeExpression represented by the current sheet (context).
   * 
   * @param sheetContext
   *        The Context for the relationship sheet that should be operated upon.
   * @param dataSetSize
   *        The number of rows to be formatted with dropdowns
   */
  public static void addDropdownsToEnumerations(SheetContext sheetContext, int dataSetSize) {
    UniversalTypeExpression ute = (UniversalTypeExpression) sheetContext.getExpression();
    for (PropertyExpression<?> property : ute.getProperties()) {
      if (property instanceof EnumerationPropertyExpression && property.getUpperBound() != Integer.MAX_VALUE) {
        addDropdownToEnumeration((EnumerationPropertyExpression) property, sheetContext, sheetContext.calculateLastDataRow(dataSetSize));
      }
    }
  }

  /**
   * Adds dropdowns to cells in an enumeration value column. The number of formatted rows is defined by parameter {@code lastRow}.
   * 
   * @param enumExpr
   *        The EnumerationPropertyExpression from which all enum literal are extracted for generating the dropdown.
   * @param sheetContext
   *        The Context for the relationship sheet that should be operated upon.
   * @param lastRow
   *        The number of the last row up to which rows are formatted with dropdowns
   */
  private static void addDropdownToEnumeration(EnumerationPropertyExpression enumExpr, SheetContext sheetContext, int lastRow) {
    ColumnContext sheetColumn = sheetContext.getColumnByPersistentName(enumExpr.getPersistentName());
    List<String> values = getListOfLiterals(enumExpr);
    if (values.isEmpty() || StringUtils.join(values, ";").length() >= 255) {
      //do not add dropdown, since it would break export!
      return;
    }

    int columnIndex = sheetColumn.getColumnNumber();
    CellRangeAddressList addressList = new CellRangeAddressList(sheetContext.getFirstDataRowNumber(), lastRow, columnIndex, columnIndex);
    ExcelGeneratorUtils.createDropdownWithValues(sheetContext.getSheet(), addressList, values);
  }

  /**
   * @param enumExpr
   * @return List of strings with the persistent names of the literals
   */
  public static List<String> getListOfLiterals(EnumerationPropertyExpression enumExpr) {
    List<EnumerationLiteralExpression> literals = enumExpr.getType().getLiterals();
    List<String> values = Lists.newArrayList();
    for (EnumerationLiteralExpression enumerationLiteralExpression : literals) {
      String name = enumerationLiteralExpression.getPersistentName();
      values.add(name);
    }
    return values;
  }

  public static Iterable<? extends RelationshipEndExpression> getToOneRelationships(UniversalTypeExpression ute) {
    List<? extends RelationshipEndExpression> relationshipEnds = ute.getRelationshipEnds();
    return Iterables.filter(relationshipEnds, new ToOneRelationFilter());
  }

  /**
   * Adds dropdowns to cells in a relationship column. The number of formatted rows is defined by parameter {@code dataSetSize}.
   * 
   * @param relationshipEndExpression
   *        The RelationshipEndExpression which determines the column that shall be formatted
   * @param sheetContext
   *        The Context for the relationship sheet that should be operated upon.
   * @param dataSetSize
   *        The number of rows to be formatted with dropdown
   */
  public static void addDropdownsToRelationshipColumn(RelationshipEndExpression relationshipEndExpression, SheetContext sheetContext, int dataSetSize) {
    UniversalTypeExpression source = relationshipEndExpression.getType();
    String namesListFormula = ExcelGeneratorUtils.createNameForFormulaAllNames(source);
    ColumnContext column = sheetContext.getColumnByPersistentName(relationshipEndExpression.getPersistentName());
    int columnIndex = column.getColumnNumber();
    CellRangeAddressList addressList = new CellRangeAddressList(sheetContext.getFirstDataRowNumber(), sheetContext.calculateLastDataRow(dataSetSize),
        columnIndex, columnIndex);
    ExcelGeneratorUtils.createDropdown(sheetContext.getSheet(), addressList, namesListFormula);
  }

  public static void initDataCells(WorkbookContext wbContext, SheetContext sheetContext, int dataSetSize) {
    int lastDataRow = sheetContext.calculateLastDataRow(dataSetSize);
    for (int rowIndex = sheetContext.getFirstDataRowNumber(); rowIndex <= lastDataRow; rowIndex++) {
      Row row = ExcelUtils.getOrCreateRow(sheetContext.getSheet(), rowIndex);

      for (ColumnContext sheetColumn : sheetContext.getColumns()) {
        Cell cell = row.createCell(sheetColumn.getColumnNumber());
        CellStyle dataStyle = wbContext.getStyles().get(IteraExcelStyle.DATA);
        cell.setCellStyle(dataStyle);

        FeatureExpression<?> expression = sheetColumn.getFeatureExpression();
        if (expression instanceof PrimitivePropertyExpression) {
          Class<?> type = ((PrimitivePropertyExpression) expression).getType().getEncapsulatedType();
          if (type.isAssignableFrom(Date.class) || (BuiltinPrimitiveType.DURATION.equals(expression.getType()))) {
            CellStyle dateStyle = wbContext.getStyles().get(IteraExcelStyle.DATA_DATE);
            cell.setCellStyle(dateStyle);
          }
        }
      }
    }
  }

  /**
   * Adds formulas to the <i>full parent name</i> column on the current sheet, which derives the fully hierarchical parent name from the
   * non-hierarchical name.
   * @param wbContext
   *        The Context for the currently generated workbook. Used to extract cell style information.
   * @param sheetContext
   *        The Context for the relationship sheet that should be operated upon.
   * @param dataSetSize
   *        The number of rows to be formatted with dropdowns
   */
  public static void addFullParentNameFormula(WorkbookContext wbContext, SheetContext sheetContext, int dataSetSize) {
    short fullParentNameColumnNumber = sheetContext.getColumnFullParentNameColumnNumber();

    if (fullParentNameColumnNumber < 0) {
      // there is no full parent name column --> abort!
      return;
    }

    ColumnContext nameColumn = sheetContext.getColumnByPersistentName(SheetContext.NAME_COLUMN);
    ColumnContext parentColumn = sheetContext.getColumnByPersistentName(SheetContext.PARENT_COLUMN);
    int firstDataRow = sheetContext.getFirstDataRowNumber();
    int lastDataRow = sheetContext.calculateLastDataRow(dataSetSize);

    CellRangeAddress matrix = new CellRangeAddress(firstDataRow, lastDataRow, nameColumn.getColumnNumber(), fullParentNameColumnNumber);
    // for each cell in column, set lookup formula and cell style
    for (int rowIndex = firstDataRow; rowIndex <= lastDataRow; rowIndex++) {
      Row row = ExcelUtils.getOrCreateRow(sheetContext.getSheet(), rowIndex);
      Cell fullParentNameCell = ExcelUtils.getOrCreateCell(row, fullParentNameColumnNumber);
      fullParentNameCell.setCellStyle(wbContext.getStyles().get(IteraExcelStyle.DATA_HIDDEN));
      String lookupFormula = createLookupFormula(parentColumn.getColumnName(), nameColumn.getColumnName(), rowIndex + 1, matrix,
          sheetContext.getSheetName());
      fullParentNameCell.setCellFormula(lookupFormula);
    }
  }

  /**
   * Adds dropdowns to cells in a parent relation column. The number of formatted rows is defined by parameter {@code dataSetSize}.
   * 
   * @param sheetContext
   *        The Context for the relationship sheet that should be operated upon.
   * @param dataSetSize
   *        The number of rows to be formatted with dropdown
   */
  public static void addParentRelationDropDown(SheetContext sheetContext, int dataSetSize) {
    UniversalTypeExpression ute = (UniversalTypeExpression) sheetContext.getExpression();
    ColumnContext parentColumn = sheetContext.getColumnByPersistentName(SheetContext.PARENT_COLUMN);
    int parentCol = parentColumn.getColumnNumber();
    CellRangeAddressList addressList = new CellRangeAddressList(sheetContext.getFirstDataRowNumber(), sheetContext.calculateLastDataRow(dataSetSize),
        parentCol, parentCol);
    String namesListFormulaName = ExcelGeneratorUtils.createNameForFormulaAllNames(ute);
    ExcelGeneratorUtils.createDropdown(sheetContext.getSheet(), addressList, namesListFormulaName);
  }

  @SuppressWarnings("boxing")
  private static String createLookupFormula(String parentColumn, String nameColumn, int currentRow, CellRangeAddress matrix, String sheetName) {

    // jme Alte Version ... so nicht sinnvoll
    // jme: prüfen!

    int columnCount = matrix.getLastColumn() - matrix.getFirstColumn() + 1;
    return String.format("IF(%1$s%2$s=\"\",\"\",VLOOKUP(%1$s%2$s,%4$s,%5$s,FALSE) & \" : \") & %3$s%2$s", parentColumn, currentRow, nameColumn,
        matrix.formatAsString(sheetName, true), columnCount);

    // return String.format("IF(%1$s%2$s=\"\",\"\",%1$s%2$s & \" : \" & %3$s%2$s)", parentColumn, currentRow, nameColumn); NEUE VERSION schreiben
  }

  /**
   * Creates a dropdown box for each cell in the specified {@code addressList}.
   * 
   * @param sheet the sheet to add the dropdown box to
   * @param addressList the cell range to add the dropdown box to
   * @param namesListFormula the formula name of the values to be shown in dropdown box
   */
  public static void createDropdown(Sheet sheet, CellRangeAddressList addressList, String namesListFormula) {
    DataValidationHelper dataValidationHelper = sheet.getDataValidationHelper();
    DataValidationConstraint dvConstraint = dataValidationHelper.createFormulaListConstraint(namesListFormula);
    DataValidation dataValidation = dataValidationHelper.createValidation(dvConstraint, addressList);
    // There is an error in the interpretation of the argument of setSuppressDropDownArrow in POI 3.9
    // for XSSF Workbooks. The arrow is suppressed if the argument is set to 'false' instead of 'true'.
    // In HSSF Workbooks the method works as designed.
    // Luckily in both cases the default behavior is, that the arrow is displayed. So we can skip the explicit setting of this behavior.
    // SKIP THIS: dataValidation.setSuppressDropDownArrow(false);

    sheet.addValidationData(dataValidation);
  }

  /**
   * Creates a dropdown box for each cell in the specified {@code addressList}.
   * 
   * @param sheet the sheet to add the dropdown box to
   * @param addressList the cell range to add the dropdown box to
   * @param values the values to be shown in dropdown box
   */
  public static void createDropdownWithValues(Sheet sheet, CellRangeAddressList addressList, List<String> values) {
    DataValidationHelper dataValidationHelper = sheet.getDataValidationHelper();
    String[] valuesList = values.toArray(new String[values.size()]);
    DataValidationConstraint dvConstraint = dataValidationHelper.createExplicitListConstraint(valuesList);
    DataValidation dataValidation = dataValidationHelper.createValidation(dvConstraint, addressList);
    // There is an error in the interpretation of the argument of setSuppressDropDownArrow in POI 3.9
    // for XSSF Workbooks. The arrow is suppressed if the argument is set to 'false' instead of 'true'.
    // In HSSF Workbooks the method works as designed.
    // Luckily in both cases the default behavior is, that the arrow is displayed. So we can skip the explicit setting of this behavior.
    // SKIP THIS: dataValidation.setSuppressDropDownArrow(false);

    sheet.addValidationData(dataValidation);
  }

  public static String createNameForFormulaAllNames(UniversalTypeExpression type) {
    return String.format("%sAllNames", type.getPersistentName());
  }

  /**
   * Adjusts the column widths for the specified sheet context. The {@code widthsMap} contains the
   * persistent names associated with the actual column width.
   * 
   * @param sheetContext the sheet context
   * @param widthsMap the map containing the column widths
   */
  public static void adjustColumnWidths(SheetContext sheetContext, Map<String, Integer> widthsMap) {
    for (ColumnContext sheetColumn : sheetContext.getColumns()) {
      if (sheetColumn.getFeatureExpression() == null) {
        continue;
      }
      String persistentName = sheetColumn.getFeatureExpression().getPersistentName();

      if (widthsMap.containsKey(persistentName)) {
        Integer width = widthsMap.get(persistentName);
        sheetContext.getSheet().setColumnWidth(sheetColumn.getColumnNumber(), width.intValue());
      }
      else {
        sheetContext.getSheet().autoSizeColumn(sheetColumn.getColumnNumber());
      }
    }
  }

  /**
   * Sorts the sheets in the {@link Workbook}
   * @param wbContext
   */
  public static void sortSheets(WorkbookContext wbContext) {
    Workbook wb = wbContext.getWb();
    Collections.sort(wbContext.getSheetContexts(), new SheetContextComparator<UniversalTypeExpressionOrder>(new UniversalTypeExpressionOrderFix()));

    // the introduction sheet remains the first (0th) sheet
    int i = 1;
    for (SheetContext sheetContext : wbContext.getSheetContexts()) {
      wb.setSheetOrder(sheetContext.getSheetName(), i++);
    }
  }

}

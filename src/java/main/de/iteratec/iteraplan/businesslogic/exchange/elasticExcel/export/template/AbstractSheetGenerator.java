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

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelGeneratorUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.SheetContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.WorkbookContext;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;


/**
 *
 */
public abstract class AbstractSheetGenerator {

  public static final int         TITLE_ROW_NO         = 0;
  public static final int         DESCRIPTION_ROW_NO   = 1;
  public static final int         SHEET_TYPE_ROW_NO    = 2;
  public static final int         FEATURE_TYPE_ROW_NO  = 3;
  public static final int         OPPOSITE_TYPE_ROW_NO = 4;
  public static final int         PRE_HEADER_ROW_NO    = 5;
  public static final int         HEADER_ROW_NO        = 6;
  public static final int         FIRST_DATA_ROW_NO    = 7;

  protected final WorkbookContext wbContext;
  protected final NamedExpression typeExpression;
  protected SheetContext          sheetContext;

  protected Row                   descriptionRow;
  protected Row                   sheetTypeRow;
  protected Row                   featureNameRow;
  protected Row                   oppositeNameRow;
  protected Row                   preHeaderRow;
  protected Row                   headerRow;

  /**
   * Default constructor.
   */
  public AbstractSheetGenerator(WorkbookContext wbContext, NamedExpression typeExpression) {
    this.wbContext = wbContext;
    this.typeExpression = typeExpression;
  }

  // ===== Sheet Area =====

  /**
   * do the generation.
   * 
   * @return the sheet context containing the generated {@link org.apache.poi.ss.usermodel.Sheet} instance and other 
   *    related information like the generated header columns.
   */
  public SheetContext generateSheet() {
    createSheet();

    createSheetHeadline();
    createHeaderRows();

    createSheetHeader();
    createFeatureHeaders();

    formatDataArea();

    return sheetContext;
  }

  /**
   * create the sheet and the sheet context.
   */
  private void createSheet() {
    String sheetName = createSheetName();
    Sheet sheet = wbContext.getWb().createSheet(sheetName);
    this.sheetContext = new SheetContext(sheet, typeExpression);
    sheetContext.setHeaderRowNumber(HEADER_ROW_NO);
    sheetContext.setFirstDataRowNumber(FIRST_DATA_ROW_NO);
    sheetContext.setSetEmptyRowsToAppend(WorkbookContext.DEFAULT_EMPTY_ROW_COUNT);
  }

  // ===== Header Area =====

  /**
   * Creates the header for the specified sheet. 
   */
  private void createSheetHeadline() {
    Sheet sheet = sheetContext.getSheet();
    sheet.createFreezePane(0, FIRST_DATA_ROW_NO);

    Cell sheetHeaderCell = sheet.createRow(0).createCell(0);
    sheetHeaderCell.setCellStyle(wbContext.getStyles().get(IteraExcelStyle.HEADER));
    sheetHeaderCell.setCellValue(createCompleteSheetName());
    sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$D$1")); // headline
  }

  private void createHeaderRows() {
    descriptionRow = sheetContext.getSheet().createRow(DESCRIPTION_ROW_NO);
    sheetTypeRow = sheetContext.getSheet().createRow(SHEET_TYPE_ROW_NO);
    featureNameRow = sheetContext.getSheet().createRow(FEATURE_TYPE_ROW_NO);
    oppositeNameRow = sheetContext.getSheet().createRow(OPPOSITE_TYPE_ROW_NO);
    preHeaderRow = sheetContext.getSheet().createRow(PRE_HEADER_ROW_NO);
    headerRow = sheetContext.getSheet().createRow(HEADER_ROW_NO);

    sheetTypeRow.setHeight((short) 0);
    featureNameRow.setHeight((short) 0);
    oppositeNameRow.setHeight((short) 0);
  }

  /**
   * add type info to the sheet: description (visible) and type definition string (hidden)
   */
  private void createSheetHeader() {
    Cell descrCell = descriptionRow.createCell(0);

    // make one larger cell for description, and set line wrapping.
    // problem: Cell height is not set automatically.
    //    sheetContext.getSheet().addMergedRegion(CellRangeAddress.valueOf("$A$2:$D$2")); // description
    //    CellStyle style = descrCell.getCellStyle();
    //    style.setWrapText(true);
    //    descrCell.setCellStyle(style);

    descrCell.setCellValue(typeExpression.getDescription());

    Cell typeCell = sheetTypeRow.createCell(0);
    typeCell.setCellValue(createTypeString());
    typeCell.setCellStyle(wbContext.getStyles().get(IteraExcelStyle.DATA_HIDDEN));
    String cellRange = String.format("$A$%d:$D$%d", Integer.valueOf((sheetTypeRow.getRowNum() + 1)), Integer.valueOf(sheetTypeRow.getRowNum() + 1));
    sheetContext.getSheet().addMergedRegion(CellRangeAddress.valueOf(cellRange));
  }

  /**
   * @param featureExpression
   */
  protected void addStandardColumnHeader(FeatureExpression<?> featureExpression) {
    int col = sheetContext.getColumnCount();
    String oppositeString = "";
    if (featureExpression instanceof RelationshipEndExpression) {
      RelationshipEndExpression relEndExp = (RelationshipEndExpression) featureExpression;
      oppositeString = createFeatureString(relEndExp.getRelationship().getOppositeEndFor(relEndExp));
    }

    String featureString = createFeatureString(featureExpression);
    Cell featureHeaderCell = featureNameRow.createCell(col);
    featureHeaderCell.setCellValue(featureString);
    featureHeaderCell.setCellStyle(wbContext.getStyles().get(IteraExcelStyle.DATA_HIDDEN));

    Cell oppositeHeaderCell = oppositeNameRow.createCell(col);
    oppositeHeaderCell.setCellValue(oppositeString);
    oppositeHeaderCell.setCellStyle(wbContext.getStyles().get(IteraExcelStyle.DATA_HIDDEN));

    String displayName = featureExpression.getName();
    addColumnHeaderCells(featureExpression, displayName);
  }

  /**
   * creates the cells in header and preheader row.
   * 
   * @param featureExpression
   * @param displayName
   * @return the cell in the PREHEADER row.
   */
  protected Cell addColumnHeaderCells(FeatureExpression<?> featureExpression, String displayName) {
    int columnCount = sheetContext.getColumnCount();

    Cell headerCell = headerRow.createCell(columnCount);
    headerCell.setCellStyle(wbContext.getStyles().get(IteraExcelStyle.HEADER));
    headerCell.setCellValue(displayName);
    addHeaderHyperlink(featureExpression, headerCell);

    sheetContext.getSheet().autoSizeColumn(columnCount);
    sheetContext.addColumn(featureExpression, headerCell);

    Cell preHeaderCell = preHeaderRow.createCell(columnCount);
    preHeaderCell.setCellStyle(wbContext.getStyles().get(IteraExcelStyle.HEADER));

    if (featureExpression instanceof EnumerationPropertyExpression) {
      String joinedLiteralList = StringUtils.join(ExcelGeneratorUtils.getListOfLiterals((EnumerationPropertyExpression) featureExpression), ";");
      String values = StringUtils.abbreviate(joinedLiteralList, 0, 255);
      preHeaderCell.setCellValue(values);
    }

    return preHeaderCell;
  }

  /**
   * Sets a link from the column header to the sheet of the type of the relationship, for better navigation
   */
  protected void addHeaderHyperlink(FeatureExpression<?> featureExpression, Cell headerCell) {
    // override in subclasses when needed
  }

  /**
   * @param relationshipEndExpression
   */
  protected void addDropdownsToRelationshipColumn(RelationshipEndExpression relationshipEndExpression) {
    ExcelGeneratorUtils.addDropdownsToRelationshipColumn(relationshipEndExpression, sheetContext, 0);
  }

  abstract protected void createFeatureHeaders();

  // ===== Data Area =====

  public abstract void addDropdowns();

  protected abstract void formatDataArea();

  protected void initDataCells() {
    ExcelGeneratorUtils.initDataCells(wbContext, sheetContext, 0);
  }

  // ===== Helpers =====

  /**
   * @return String representing the expression.
   */
  private String createTypeString() {
    return String.format("%s{%s}:%s", typeExpression.getPersistentName(), typeExpression.getAbbreviation(), typeExpression.getMetaType()
        .getSimpleName());
  }

  /**
   * @param namedExp
   * @return String representing the expression.
   */
  protected String createFeatureString(FeatureExpression<?> namedExp) {
    String upperBound = (namedExp.getUpperBound() < Integer.MAX_VALUE) ? Integer.toString(namedExp.getUpperBound()) : "*";
    return String.format("%s[%d..%s]:%s", namedExp.getPersistentName(), Integer.valueOf(namedExp.getLowerBound()), upperBound, namedExp.getType()
        .getPersistentName());
  }

  protected NamedExpression getTypeExpression() {
    return typeExpression;
  }

  protected abstract String createCompleteSheetName();

  private String createSheetName() {
    String name = createCompleteSheetName();
    return ExcelUtils.makeSheetNameValid(name, wbContext.getWb());
  }

}
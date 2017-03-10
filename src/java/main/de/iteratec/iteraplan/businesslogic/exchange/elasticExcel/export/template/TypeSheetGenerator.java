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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.util.CellRangeAddress;

import com.google.common.collect.Iterables;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelGeneratorUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.ColumnContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.SheetContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.WorkbookContext;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;


/**
 * generate Excel-Sheets for the {@link UniversalTypeExpression}s.
 */
public class TypeSheetGenerator extends AbstractSheetGenerator {

  /**
   * Generates the new Excel-Sheet for the specified {@link UniversalTypeExpression}.
   * 
   * @param wbContext the instance of the workbook context
   * @param typeExpression the substantial type to generate the sheet for
   */
  public TypeSheetGenerator(WorkbookContext wbContext, UniversalTypeExpression typeExpression) {
    super(wbContext, typeExpression);
  }

  // ===== Header Area =====

  /** add the header cells for each feature, i.e. for each column */
  @Override
  protected void createFeatureHeaders() {

    Iterable<? extends FeatureExpression<?>> allFeatureExpressions = getPrimitiveAndToOneFeatures();

    for (FeatureExpression<?> featureExpression : allFeatureExpressions) {
      if (BuiltinPrimitiveType.DURATION.equals(featureExpression.getType())) {
        addDurationColumnHeaders(featureExpression);
      }
      else {
        addStandardColumnHeader(featureExpression);
      }
    }
  }

  /**
   * @param featureExpression
   * @param col
   */
  private void addDurationColumnHeaders(FeatureExpression<?> featureExpression) {
    int col = sheetContext.getColumnCount();
    Cell runtimeFromCell = addColumnHeaderCells(featureExpression, "rp_from");
    Cell runtimeToCell = addColumnHeaderCells(featureExpression, "rp_to");

    CellRangeAddress merge = new CellRangeAddress(PRE_HEADER_ROW_NO, PRE_HEADER_ROW_NO, col, col + 1);
    sheetContext.getSheet().addMergedRegion(merge);
    runtimeFromCell.setCellValue("Runtime Period");

    String featureString = createFeatureString(featureExpression);
    Cell cell = featureNameRow.createCell(runtimeFromCell.getColumnIndex());
    cell.setCellValue(featureString);
    cell.setCellStyle(wbContext.getStyles().get(IteraExcelStyle.DATA_HIDDEN));

    cell = featureNameRow.createCell(runtimeToCell.getColumnIndex());
    cell.setCellValue(featureString);
    cell.setCellStyle(wbContext.getStyles().get(IteraExcelStyle.DATA_HIDDEN));

    cell = oppositeNameRow.createCell(runtimeFromCell.getColumnIndex());
    cell.setCellStyle(wbContext.getStyles().get(IteraExcelStyle.DATA_HIDDEN));
    cell = oppositeNameRow.createCell(runtimeToCell.getColumnIndex());
    cell.setCellStyle(wbContext.getStyles().get(IteraExcelStyle.DATA_HIDDEN));
  }

  // ===== Data Area =====

  /**
   * apply cell styles and dropdowns (with values of formula-based) to cells in data area.
   */
  protected void formatDataArea() {
    initDataCells();
    createNameListFormula();
  }

  public void addDropdowns() {
    ExcelGeneratorUtils.addDropdownsToEnumerations(sheetContext, 0);

    for (RelationshipEndExpression relationshipEndExpression : ExcelGeneratorUtils.getToOneRelationships(getTypeExpression())) {
      if (StringUtils.equals(SheetContext.PARENT_COLUMN, relationshipEndExpression.getPersistentName())) {

        addParentRelationColumn();

        ExcelGeneratorUtils.addFullParentNameFormula(wbContext, sheetContext, 0);
        ExcelGeneratorUtils.addParentRelationDropDown(sheetContext, 0);
      }
      else {
        addDropdownsToRelationshipColumn(relationshipEndExpression);
      }
    }
  }

  private void addParentRelationColumn() {
    Cell cell = ExcelUtils.getOrCreateCell(headerRow, sheetContext.getColumnCount());
    cell.setCellStyle(wbContext.getStyles().get(IteraExcelStyle.HEADER));
    cell.setCellValue(SheetContext.FULL_PARENT_NAME_COLUMN);
  }

  // ===== Implement/Override methods =====

  @Override
  protected String createCompleteSheetName() {
    String entityName = typeExpression.getName();
    String abbreviation = typeExpression.getAbbreviation();
    return String.format("%s (%s)", entityName, abbreviation);
  }

  @Override
  protected UniversalTypeExpression getTypeExpression() {
    return (UniversalTypeExpression) typeExpression;
  }

  // ===== Helpers =====

  /**
   * returns features in the correct order.
   * 
   * @return primitive features and to-one relationship ends
   */
  private Iterable<? extends FeatureExpression<?>> getPrimitiveAndToOneFeatures() {

    if (getTypeExpression() instanceof SubstantialTypeExpression) {
      Iterable<? extends RelationshipEndExpression> relationships = ExcelGeneratorUtils.getToOneRelationships(getTypeExpression());
      List<PropertyExpression<?>> properties = getTypeExpression().getProperties();
      return Iterables.concat(properties, relationships);
    }
    else {
      ArrayList<FeatureExpression<?>> result = new ArrayList<FeatureExpression<?>>();
      for (FeatureExpression<?> feature : getTypeExpression().getFeatures()) {
        if (!(feature instanceof RelationshipEndExpression) || ((RelationshipEndExpression) feature).getUpperBound() <= 1) {
          result.add(feature);
        }
      }
      return result;
    }

  }

  private void createNameListFormula() {
    ColumnContext nameColumn = sheetContext.getColumnByPersistentName(SheetContext.NAME_COLUMN);
    if (nameColumn != null) {
      Name rangeName = wbContext.getWb().createName();
      rangeName.setNameName(ExcelGeneratorUtils.createNameForFormulaAllNames(getTypeExpression()));
      CellRangeAddress nameRange = CellRangeAddress.valueOf(nameColumn.getHeaderCellReference());
      nameRange.setFirstRow(sheetContext.getFirstDataRowNumber());
      nameRange.setLastRow(60000); // high constant last row number, so that manually added row entries will be considered as well, not only generated rows

      String nameRangeFormulaBase = "OFFSET({0},0,0,SUMPRODUCT(--({0}<>\"\")),1)"; // shows only non-empty entries in the name list
      String nameRangeFormula = MessageFormat.format(nameRangeFormulaBase, nameRange.formatAsString(sheetContext.getSheetName(), true));
      rangeName.setRefersToFormula(nameRangeFormula);
    }
  }
}

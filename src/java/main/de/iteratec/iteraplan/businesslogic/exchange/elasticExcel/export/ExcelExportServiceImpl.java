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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.ExcelTemplateGeneratorService;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelGeneratorUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.ColumnContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.SheetContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.WorkbookContext;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.model.RuntimePeriod;


/**
 * A default {@link ExcelExportService} implementation for exporting the iteraplan data
 * to Excel. The template will be used from the {@link ExcelTemplateGeneratorService}.
 */
public class ExcelExportServiceImpl implements ExcelExportService {

  /** Logger. */
  private static final Logger           LOGGER = Logger.getIteraplanLogger(ExcelExportServiceImpl.class);

  /** The excel template generator service. */
  private ExcelTemplateGeneratorService excelTemplateGeneratorService;

  /**
   * Instantiates a new excel export service impl.
   *
   * @param excelTemplateGeneratorService the excel template generator service
   */
  public ExcelExportServiceImpl(ExcelTemplateGeneratorService excelTemplateGeneratorService) {
    this.excelTemplateGeneratorService = excelTemplateGeneratorService;
  }

  /**{@inheritDoc}**/
  public WorkbookContext exportExcel2003(Model model, Metamodel metamodel) {
    LOGGER.debug("Starting Excel 2003 data export.");
    WorkbookContext wbContext = excelTemplateGeneratorService.generateTemplateExcel2003(metamodel);

    return fillExportWorkbook(model, wbContext);
  }

  /**{@inheritDoc}**/
  public WorkbookContext exportExcel2007(Model model, Metamodel metamodel) {
    LOGGER.debug("Starting Excel 2007 data export.");
    WorkbookContext wbContext = excelTemplateGeneratorService.generateTemplateExcel2007(metamodel);

    return fillExportWorkbook(model, wbContext);
  }

  /**
   * @param model The model used to populate the template
   * @param wbContext The workbook context to be filled
   * @return the workbook context containing a representation of the model
   */
  private WorkbookContext fillExportWorkbook(Model model, WorkbookContext wbContext) {
    List<SheetContext> sheetContexts = wbContext.getSheetContexts();
    for (SheetContext sheetContext : sheetContexts) {
      LOGGER.debug("Exporting data to the sheet: {0}", sheetContext.getSheetName());
      NamedExpression expression = sheetContext.getExpression();
      if (expression instanceof SubstantialTypeExpression || expression instanceof RelationshipTypeExpression) {
        exportTypes(model, wbContext, sheetContext);
      }
      else if (expression instanceof RelationshipExpression) {
        exportRelationships(model, wbContext, sheetContext);
      }
    }
    LOGGER.debug("Excel data was exported.");

    return wbContext;
  }

  @SuppressWarnings("unchecked")
  private void exportTypes(Model model, WorkbookContext wbContext, SheetContext sheetContext) {
    Sheet sheet = sheetContext.getSheet();
    int currentDataRowNum = sheetContext.getFirstDataRowNumber();
    UniversalTypeExpression ute = (UniversalTypeExpression) sheetContext.getExpression();

    Collection<UniversalModelExpression> entities = model.findAll(ute);

    int dataSetSize = entities.size();

    ExcelGeneratorUtils.initDataCells(wbContext, sheetContext, dataSetSize);

    ExcelGeneratorUtils.addDropdownsToEnumerations(sheetContext, dataSetSize);

    for (RelationshipEndExpression relationshipEndExpression : ExcelGeneratorUtils.getToOneRelationships(ute)) {
      if (StringUtils.equals(SheetContext.PARENT_COLUMN, relationshipEndExpression.getPersistentName())) {
        ExcelGeneratorUtils.addFullParentNameFormula(wbContext, sheetContext, dataSetSize);
        ExcelGeneratorUtils.addParentRelationDropDown(sheetContext, dataSetSize);
      }
      else {
        ExcelGeneratorUtils.addDropdownsToRelationshipColumn(relationshipEndExpression, sheetContext, dataSetSize);
      }
    }

    LOGGER.info("Exporting {0}: {1} entities", ute.getPersistentName(), Integer.valueOf(dataSetSize));
    for (UniversalModelExpression modelExpression : entities) {
      Row row = ExcelUtils.getOrCreateRow(sheet, currentDataRowNum++);

      for (int col = 0; col < sheetContext.getColumnCount(); col++) {
        ColumnContext sheetColumn = sheetContext.getColumns().get(col);

        FeatureExpression<?> expression = sheetColumn.getFeatureExpression();
        if (expression instanceof PropertyExpression) {
          PropertyExpression<?> pe = (PropertyExpression<?>) expression;

          Object value = modelExpression.getValue(pe);
          if (pe.getUpperBound() == Integer.MAX_VALUE) {
            value = modelExpression.getValues(pe);
            String valueList;
            if (pe instanceof EnumerationPropertyExpression) {
              valueList = joinEnumLiterals((Collection<EnumerationLiteralExpression>) value);
            }
            else {
              valueList = StringUtils.join((Collection<?>) value, ";");
            }
            setPropertyValue(row, col, valueList);
          }
          else if (BuiltinPrimitiveType.DURATION.equals(pe.getType())) {
            RuntimePeriod rtPeriod = (RuntimePeriod) value;
            if (rtPeriod != null) {
              setPropertyValue(row, col, rtPeriod.getStart());
              setPropertyValue(row, col + 1, rtPeriod.getEnd());
            }
            col++;
          }
          else {
            setPropertyValue(row, col, value);
          }
        }
        else if (expression instanceof RelationshipEndExpression) {
          RelationshipEndExpression re = (RelationshipEndExpression) expression;
          UniversalModelExpression connected = modelExpression.getConnected(re);

          if (connected != null) {
            Object value = connected.getValue(MixinTypeNamed.NAME_PROPERTY);
            Cell cell = ExcelUtils.getOrCreateCell(row, sheetColumn.getColumnNumber());
            cell.setCellValue(String.valueOf(value));
          }
        }

      }
    }
  }

  /**
   * @param literals
   * @return a String with all enum literals, separeted by ';'
   */
  private String joinEnumLiterals(Collection<EnumerationLiteralExpression> literals) {
    List<String> literalNames = new ArrayList<String>();
    for (EnumerationLiteralExpression e : literals) {
      literalNames.add(e.getPersistentName());
    }
    return StringUtils.abbreviate(StringUtils.join(literalNames, ";"), 0, 255);
  }

  /**
   * @param model
   * @param sheetContext
   * @param re
   */
  private void exportRelationships(Model model, WorkbookContext wbContext, SheetContext sheetContext) {

    RelationshipExpression re = (RelationshipExpression) sheetContext.getExpression();

    // these relations are already exported in STE or RTE sheets.
    if (re.getRelationshipEnds().get(0).getUpperBound() == 1 || re.getRelationshipEnds().get(1).getUpperBound() == 1) {
      return;
    }

    BindingSet bindingSet = model.findAll(re.getRelationshipEnds().get(1));

    int estimatedRowCount = 0;
    for (UniversalModelExpression left : bindingSet.getAllFromElements()) {
      estimatedRowCount += bindingSet.getToBindings(left).size();
    }

    ExcelGeneratorUtils.initDataCells(wbContext, sheetContext, estimatedRowCount);

    for (RelationshipEndExpression ree : re.getRelationshipEnds()) {
      ExcelGeneratorUtils.addDropdownsToRelationshipColumn(ree, sheetContext, estimatedRowCount);
    }

    LOGGER.debug("Relation: {0}: From {1} to {2}", re.toString(), bindingSet.getFromType().getPersistentName(), bindingSet.getToType()
        .getPersistentName());

    Sheet sheet = sheetContext.getSheet();
    int currentDataRowNum = sheetContext.getFirstDataRowNumber();

    // we know that left and right are InstanceExpressions of STE.
    for (UniversalModelExpression left : bindingSet.getAllFromElements()) {
      for (UniversalModelExpression right : bindingSet.getToBindings(left)) {

        Row row = ExcelUtils.getOrCreateRow(sheet, currentDataRowNum++);
        Cell fromCell = ExcelUtils.getOrCreateCell(row, 0);
        Cell toCell = ExcelUtils.getOrCreateCell(row, 1);

        String fromName = (String) left.getValue(MixinTypeNamed.NAME_PROPERTY); // left is instance of STE.
        String toName = (String) right.getValue(MixinTypeNamed.NAME_PROPERTY); // right is instance of STE.

        fromCell.setCellValue(fromName);
        toCell.setCellValue(toName);
      }
    }
  }

  private void setPropertyValue(Row row, int colNo, Object value) {
    if (value == null) {
      return;
    }
    Cell cell = ExcelUtils.getOrCreateCell(row, colNo);

    if (value instanceof Number) {
      Number n = (Number) value;
      cell.setCellValue(n.doubleValue());
    }
    else if (value instanceof Date) {
      Date date = (Date) value;
      cell.setCellValue(date);
    }
    else if (value instanceof EnumerationLiteralExpression) {
      EnumerationLiteralExpression literal = (EnumerationLiteralExpression) value;
      cell.setCellValue(String.valueOf(literal.getPersistentName()));
    }
    else {
      cell.setCellValue(String.valueOf(value));
    }
  }


}

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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.excelimport;

import static de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.AbstractSheetGenerator.FIRST_DATA_ROW_NO;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.ColumnContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.SheetContext;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.model.RuntimePeriod;


/**
 * Import FeatureExpressions and add them to the <b>model</b>.
 * 
 * <p>
 * Do so in two passes: first create the instances and import the primitives,
 * and second set the relations given on the STE sheets.
 * (Relation sheets will be handled separately in {@link RelationDataImporter}.)
 * 
 * <p>
 * Use the {@link Pass} value to distinguish between these steps.
 */
public class EntityDataImporter extends AbstractDataImporter {

  /** Logger. */
  private static final Logger LOGGER = Logger.getIteraplanLogger(EntityDataImporter.class);

  /** distinguish between third and fourth import pass. */
  public enum Pass {
    PRIMITIVES, RELATIONS
  }

  private Metamodel                             metamodel;
  private Model                                 model;

  private Pass                                  pass;

  /** map from special key (composed of type and Excel row) to instances. */
  private Map<String, UniversalModelExpression> instanceMap = new HashMap<String, UniversalModelExpression>();

  /**
   * Constructor.
   * 
   * @param metamodel
   * @param model
   */
  public EntityDataImporter(Metamodel metamodel, Model model) {
    this.model = model;
    this.metamodel = metamodel;
  }

  /**
   * do the import of the given sheet.
   * 
   * @param sheetContext
   * @param importPass
   *     PRIMITIVES for third pass, i.e. create instances and import primitives;
   *     RELATIONS for fourth pass, i.e. create relations.
   */
  public void importEntityData(SheetContext sheetContext, Pass importPass) {
    this.pass = importPass;
    LOGGER.info("    == Pass {0}: Sheet {1}", (pass == Pass.PRIMITIVES ? Integer.valueOf(3) : Integer.valueOf(4)), sheetContext.getSheetName());

    if (sheetContext.getExpression() instanceof SubstantialTypeExpression || sheetContext.getExpression() instanceof RelationshipTypeExpression) {
      importEntities(sheetContext);
    }
    else if (sheetContext.getExpression() instanceof EnumerationExpression) {
      return;
    }
    else {
      logError("Sheet {0}: Unexpected sheet type expression: {1}", sheetContext.getSheetName(), sheetContext.getExpression().getClass().getName());
    }
  }

  /**
   * @param sheetContext
   */
  private void importEntities(SheetContext sheetContext) {
    boolean hasMore = true;
    for (int rowNum = FIRST_DATA_ROW_NO; hasMore; rowNum++) {
      Row row = sheetContext.getSheet().getRow(rowNum);
      hasMore = importEntity(sheetContext, row);
    }
  }

  private boolean importEntity(SheetContext sheetContext, Row row) {

    if (!containsData(sheetContext, row)) {
      return false;
    }

    UniversalTypeExpression entityType = (UniversalTypeExpression) sheetContext.getExpression();
    UniversalModelExpression instance = getInstance(entityType, row.getRowNum() - FIRST_DATA_ROW_NO);

    if (instance == null) {
      return false;
    }

    for (int col = 0; col < sheetContext.getColumnCount(); col++) {
      ColumnContext column = sheetContext.getColumns().get(col);
      FeatureExpression<?> featureExpression = column.getFeatureExpression();

      Cell cell = row.getCell(col);
      if (BuiltinPrimitiveType.DURATION.equals(featureExpression.getType())) {
        if (pass == Pass.PRIMITIVES) {
          Cell endCell = row.getCell(col + 1);
          importRuntimePeriodExpression(instance, featureExpression, cell, endCell);
        }
        col++;
      }
      else if (cell != null) { // cell may be null if the value is not set and rows are not created by the template generator.
        importFeatureExpression(instance, featureExpression, cell);
      }
    }

    return true;
  }

  /**
   * @param sheetContext
   * @param row
   * @return true if row contains any data (i.e. at least on cell has a value), and false if it is empty.
   */
  private boolean containsData(SheetContext sheetContext, Row row) {
    if (row != null) {
      for (int i = 0; i < sheetContext.getColumnCount(); i++) {
        if (!ExcelUtils.isEmptyCell(row.getCell(i))) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * RuntimePeriod get special handling, because they need two columns for one primitive type.
   * 
   * @param instance
   * @param featureExpression
   * @param startCell
   * @param endCell
   */
  private void importRuntimePeriodExpression(UniversalModelExpression instance, FeatureExpression<?> featureExpression, Cell startCell, Cell endCell) {

    try {
      Date startDate = (startCell != null) ? startCell.getDateCellValue() : null;
      Date endDate = (endCell != null) ? endCell.getDateCellValue() : null;

      if (startDate == null && endDate == null) {
        return;
      }

      if (startDate != null && endDate != null && startDate.getTime() > endDate.getTime()) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, UserContext.getCurrentLocale());
        logError("Near cell {0}: RuntimePeriod: from {1} until {2} in instance {3}", ExcelUtils.getFullCellName(startCell), df.format(startDate),
            df.format(endDate), instance);
      }
      else {
        RuntimePeriod rt = new RuntimePeriod(startDate, endDate);
        setValue(instance, (PropertyExpression<?>) featureExpression, rt, startCell);
      }
    } catch (Exception e) {
      logError("Near cell {0}: Error importing runtime period in {1}: {2}", ExcelUtils.getFullCellName(startCell), instance, e.getMessage());
    }
  }

  private UniversalModelExpression getInstance(UniversalTypeExpression entityType, int index) {
    String key = entityType.getPersistentName() + "-" + index;
    UniversalModelExpression result = instanceMap.get(key);

    if (result == null) {
      if (entityType instanceof SubstantialTypeExpression) {
        result = model.create((SubstantialTypeExpression) entityType);
      }
      else if (entityType instanceof RelationshipTypeExpression) {
        result = model.create((RelationshipTypeExpression) entityType);
      }
      else {
        logError("findOrCreateInstance: can not create instance of type {0}", entityType.getPersistentName());
      }
      instanceMap.put(key, result);
    }

    return result;
  }

  /**
   * import PropertyExpression and RelationshipEndExpression in pass 3 and 4, respectively.
   * Ignores all other feature expressions.
   */
  private void importFeatureExpression(UniversalModelExpression instance, FeatureExpression<?> featureExpression, Cell cell) {
    if (featureExpression instanceof PropertyExpression && pass == Pass.PRIMITIVES) {
      importPropertyExpression(instance, (PropertyExpression<?>) featureExpression, cell);
    }
    else if (featureExpression instanceof RelationshipEndExpression && pass == Pass.RELATIONS) {
      importRelationshipEndExpression(instance, (RelationshipEndExpression) featureExpression, cell);
    }
  }

  /**
   * do the import of a Property Expression: read the value from the Excel cell, convert the value,
   * and set it in the model.
   * 
   * @param instance
   * @param propertyExpression
   * @param cell
   */
  private void importPropertyExpression(UniversalModelExpression instance, PropertyExpression<?> propertyExpression, Cell cell) {
    Object value = readCellValue(cell, propertyExpression);
    setValue(instance, propertyExpression, value, cell);
  }

  /**
   * @param instance
   * @param relEndExpression
   * @param cell
   */
  private void importRelationshipEndExpression(UniversalModelExpression instance, RelationshipEndExpression relEndExpression, Cell cell) {
    String value = ExcelUtils.getStringCellValue(cell);
    if (value == null || "".equals(value)) {
      return;
    }

    SubstantialTypeExpression targetType = (SubstantialTypeExpression) relEndExpression.getType();
    InstanceExpression target = model.findByName(targetType, value);

    if (target == null) {
      logError("Cell {0}: NOT FOUND: relation {1} from {2}[{3}] to {4}[{5}]", ExcelUtils.getFullCellName(cell), //
          relEndExpression.getPersistentName(), instance.getClass().getSimpleName(), instance.toString(), targetType.getPersistentName(), value);
    }
    else {
      instance.connect(relEndExpression, target);
    }
  }

  /**
   * use the type information from parameter <code>property</code> to convert the value.
   * 
   * @param cellValue
   * @param property
   * @return the converted object. Null if input was null.
   */
  private Object readCellValue(Cell cell, PropertyExpression<?> property) {
    if (isReleaseName(property.getHolder(), property)) {
      return getNormalizedReleaseName(cell);
    }
    else if (ExcelUtils.isEmptyCell(cell)) {
      return null;
    }
    else {
      return readPropertyValue(cell, property);
    }

  }

  private Object readPropertyValue(Cell nonEmptyCell, PropertyExpression<?> nonReleasenameProperty) {
    if (nonReleasenameProperty instanceof EnumerationPropertyExpression) {
      return getEnumPropertyValue(nonEmptyCell, nonReleasenameProperty);
    }
    else if (nonReleasenameProperty.getType().equals(BuiltinPrimitiveType.INTEGER)) {
      Double doubleValue = getCellValueOfType(nonEmptyCell, Double.class);
      return doubleValue == null ? null : BigInteger.valueOf(doubleValue.longValue());
    }
    else if (nonReleasenameProperty.getType().equals(BuiltinPrimitiveType.DECIMAL)) {
      Double doubleValue = getCellValueOfType(nonEmptyCell, Double.class);
      return doubleValue == null ? null : BigDecimal.valueOf(doubleValue.doubleValue());
    }
    else if (nonReleasenameProperty.getType().equals(BuiltinPrimitiveType.DATE)) {
      return getCellValueOfType(nonEmptyCell, Date.class);
    }
    else if (nonReleasenameProperty.getType().equals(BuiltinPrimitiveType.BOOLEAN)) {
      String stringValue = getCellValueOfType(nonEmptyCell, String.class);
      return Boolean.valueOf(Boolean.parseBoolean(stringValue));
    }
    else if (nonReleasenameProperty.getType().equals(BuiltinPrimitiveType.STRING)) {
      return getStringPropertyValue(nonEmptyCell, nonReleasenameProperty);
    }
    else {
      LOGGER.info("Property type for Java enum: {0}", nonReleasenameProperty.getClass().getName());
      return resolveJavaEnum(nonReleasenameProperty.getType().getName(), ExcelUtils.getCellValue(nonEmptyCell, false));
    }
  }

  private Object getEnumPropertyValue(Cell cell, PropertyExpression<?> property) {
    String stringCellValue = ExcelUtils.getStringCellValue(cell);
    if (StringUtils.isEmpty(stringCellValue)) {
      return null;
    }
    else if (property.getUpperBound() > 1) {
      // multi-value attributes may be in one cell, separated by ';'
      List<EnumerationLiteralExpression> list = new ArrayList<EnumerationLiteralExpression>();
      for (String literalName : stringCellValue.split(";")) {
        EnumerationLiteralExpression literal = findEnumLiteral(literalName, property);
        list.add(literal);
      }
      return list;
    }
    else {
      return findEnumLiteral(stringCellValue, property);
    }
  }

  private Object getStringPropertyValue(Cell cell, PropertyExpression<?> property) {
    String stringValue = ExcelUtils.getStringCellValue(cell);
    if (property.getUpperBound() > 1) {
      // multi-value attributes may be in one cell, separated by ';'
      if (stringValue == null || "".equals(stringValue)) {
        return new ArrayList<String>();
      }
      else {
        return Arrays.asList(stringValue.split(";"));
      }
    }
    else {
      return stringValue;
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T getCellValueOfType(Cell cell, Class<T> typeClass) {
    boolean convertNumberToDate = typeClass.equals(Date.class);
    Object cellValue = ExcelUtils.getCellValue(cell, convertNumberToDate);

    if (cellValue == null) {
      return null;
    }

    if (typeClass.isAssignableFrom(cellValue.getClass())) {
      return (T) cellValue;
    }
    else if (String.class.equals(typeClass)) {
      // In some cases, Excel stores text-formatted cells as numeric, if it contains only digits.
      // Number types can't be casted to String, but converted.
      // To avoid errors, handle non-String typed (Excel-/POI-) cells with Metamodel type "String" like this:
      return (T) ExcelUtils.getStringCellValue(cell);
    }
    else {
      logError("Cell {0}: Expected Cell value of type \"{1}\" but got \"{2}\"", ExcelUtils.getFullCellName(cell), typeClass.getSimpleName(),
          cellValue.getClass().getSimpleName());
      return null;
    }
  }

  /**
   * find the enum literal in the metamodel.
   * 
   * @param cellValue
   * @param property
   * @return the enum literal for the given cell.
   */
  private EnumerationLiteralExpression findEnumLiteral(Object cellValue, PropertyExpression<?> property) {
    EnumerationLiteralExpression result = null;
    String persistentName = property.getType().getPersistentName();
    String literalName = cellValue.toString();

    EnumerationExpression enumExpr = (EnumerationExpression) metamodel.findTypeByPersistentName(persistentName);
    if (enumExpr == null) {
      logError("EnumerationExpression {0} not found.", persistentName);
    }
    else {
      result = enumExpr.findLiteralByPersistentName(literalName);
      if (result == null) {
        logError("EnumerationLiteralExpression {0}#{1} not found.", persistentName, literalName);
      }
    }
    return result;
  }

  /**
   * @param typeName
   * @param cellValue
   * @return the enum value for the given typeName and the enum value given as String in the cell value;
   *         or null if the value is not found.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Object resolveJavaEnum(String typeName, Object cellValue) {
    Object result = null;
    try {
      Class<?> clazz = Class.forName(typeName);

      if (clazz.isEnum()) {
        return Enum.valueOf(((Class<Enum>) clazz), cellValue.toString());
      }
      else {
        logError("Type {0} is not an enum. Can not find value for {1}", typeName, cellValue.toString());
      }
    } catch (ClassNotFoundException e) {
      logError("Error setting {0} to {1}: {2} {3}", typeName, cellValue.toString(), e.getClass().getName(), e.getMessage());
    } catch (SecurityException e) {
      logError("Error setting {0} to {1}: {2} {3}", typeName, cellValue.toString(), e.getClass().getName(), e.getMessage());
    } catch (IllegalArgumentException e) {
      logError("Error setting {0} to {1}: {2} {3}", typeName, cellValue.toString(), e.getClass().getName(), e.getMessage());
    }
    return result;
  }

  /**
   * set the given value in the given instance.
   * 
   * @param instance
   * @param propertyExpression
   * @param value
   *    value to set. If null, this method does nothing.
   * @param cell
   *    only needed for error message.
   */
  private void setValue(UniversalModelExpression instance, PropertyExpression<?> propertyExpression, Object value, Cell cell) {

    if (value == null) {
      return;
    }

    try {
      instance.setValue(propertyExpression, value);
    } catch (RuntimeException e) {
      logError("error setting value of cell {0}: {1} ({2})", ExcelUtils.getFullCellName(cell), value.toString(), value.getClass().getName());
    }
  }

  private void logError(String format, Object... params) {
    logError(LOGGER, format, params);
  }
}

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

import static de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ImportWorkbook.getProcessingLog;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.ExcelSheet;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TextAV;


public final class ExcelImportUtilities {
  private static final Logger LOGGER = Logger.getIteraplanLogger(ExcelImportUtilities.class);

  private ExcelImportUtilities() {
    // prevent instantiation
  }

  /**
   * Creates a set (potentially only one value) of AttributeValues matching the given AttributeType <code>at</code>.
   * Offers the possibility to explicitly override cell values via <code>valueOverride</code> for number and responsibility attributes.
   * @param at The attribute type to which the attribute values should be linked.
   * @param valueHolder Cell value holder which contains the relevant attribute value(s). Multiple values are split at semicolons.
   * @param valueOverride an override value for the actual cell content. If this String is non-null/empty and non-whitespace, it will used in the NumberAV, regardless of the cell content in attrValue!
   * @return A set of attribute value objects which contain the value(s) from attrValue or valueOverride, or <code>null</code> as an element if the values could not be parsed
   */
  public static Set<AttributeValue> createAttributeValue(AttributeType at, CellValueHolder valueHolder, String valueOverride)
      throws IllegalArgumentException {
    final Set<AttributeValue> v = Sets.newHashSet();
    switch (at.getTypeOfAttribute()) {
      case NUMBER:
        NumberAV numberV = createNumberAV((NumberAT) at, valueHolder, valueOverride);
        v.add(numberV);
        break;
      case DATE:
        DateAV dateV = createDateAV((DateAT) at, valueHolder);
        v.add(dateV);
        break;
      case TEXT:
        TextAV textV = createTextAV(valueHolder);
        v.add(textV);
        break;
      case RESPONSIBILITY:
        Set<ResponsibilityAV> respV = getResponsibilityAV((ResponsibilityAT) at, valueHolder, valueOverride);
        v.addAll(respV);
        break;
      case ENUM:
        Set<EnumAV> enumV = getEnumAV((EnumAT) at, valueHolder);
        v.addAll(enumV);
        break;
      default:
        LOGGER.error("unknown attribute type: {0}", at.getTypeOfAttribute());
    }
    return v;
  }

  /**
   * Creates a set (potentially only one value) of AttributeValues matching the given AttributeType <code>at</code>.
   * @param at The attribute type to which the attribute values should be linked.
   * @param valueHolder Cell value holder which contains the relevant attribute value(s). Multiple values are split at semicolons.
   * @return A set of attribute value objects which contain the value(s) from attrValue or valueOverride, or <code>null</code> if the values could not be parsed
   */
  public static Set<AttributeValue> createAttributeValue(AttributeType at, CellValueHolder valueHolder) {
    return createAttributeValue(at, valueHolder, null);
  }

  /**
   * Creates a NumberAV object from the attrValue for the given number attribute type.
   * @param at The attribute to which the created number should be linked.
   * @param attrValue Cell value holder which contains the relevant number value
   * @param valueOverride an override value for the actual cell content. If this String is non-null/empty and non-whitespace, it will used in the NumberAV, regardless of the cell content in attrValue!
   * @return A number attribute value object containing the value from attrValue or valueOverride, or <code>null</code> if no number could be parsed
   */
  @SuppressWarnings("boxing")
  private static NumberAV createNumberAV(NumberAT at, CellValueHolder attrValue, String valueOverride) {
    NumberAV numberV = new NumberAV();
    Double cell = null;
    // if valueOverride is null, use attrValue as "default value"
    String value = StringUtils.defaultIfBlank(valueOverride, attrValue.getAttributeValue());

    try {
      cell = contentAsDouble(value);
    } catch (NumberFormatException ex) {
      String warnMessage = "Cell [" + getCellRef(attrValue.getOriginCell()) + "]  Ignoring value " + value + " for attribute type " + at.getName()
          + " due to error:" + ex.getMessage();
      getProcessingLog().warn(warnMessage);
      attrValue.addProblem(ProblemMarker.WARNING, warnMessage);
      throw new IllegalArgumentException("\"" + value + "\" is not a number", ex);
    } catch (IllegalStateException ex) {
      String warnMessage = "Cell [" + getCellRef(attrValue.getOriginCell()) + "]  Ignoring value " + value + " for attribute type " + at.getName()
          + " due to error:" + ex.getMessage();
      getProcessingLog().warn(warnMessage);
      attrValue.addProblem(ProblemMarker.WARNING, warnMessage);
      throw new IllegalArgumentException(ex);
    }

    if (cell != null) {
      try {
        BigDecimal v = BigDecimal.valueOf(cell);
        numberV.setValue(v);

        return numberV;
      } catch (NumberFormatException e) {
        LOGGER.warn("Couldn't translate a Double into a BigDecimal");
      }
    }
    return null;
  }

  private static TextAV createTextAV(CellValueHolder attrValue) {
    String cellContent = attrValue.getAttributeValue();
    if (StringUtils.isEmpty(cellContent)) {
      return null;
    }
    TextAV textValue = new TextAV();
    textValue.setValue(cellContent);
    return textValue;
  }

  private static DateAV createDateAV(DateAT at, CellValueHolder attrValue) {
    DateAV dateV = new DateAV();
    Date dateValue = getDate(attrValue, "attribute " + at.getName());
    if (dateValue != null) {
      dateV.setValue(dateValue);
      return dateV;
    }
    return null;
  }

  public static Date getDate(Cell dateCell, String dateCellCoords, String elementName) {
    Date date = null;
    if (dateCell != null) {
      try {
        date = dateCell.getDateCellValue();
      } catch (IllegalStateException ex) {
        getProcessingLog().info("Cell [{0}]  Date not found, assuming null: {1}", dateCellCoords, elementName);
        LOGGER.info(ex);
      } catch (NumberFormatException ex) {
        getProcessingLog().warn("Cell [{0}]  Date invalid, assuming null: {1}", dateCellCoords, elementName);
        LOGGER.warn(ex);
      }
    }
    return date;
  }

  public static Date getDate(CellValueHolder dateCell, String elementName) {
    Cell originCell = dateCell.getOriginCell();
    return getDate(originCell, dateCell.getCellRef(), elementName);
  }

  private static Double contentAsDouble(String value) {

    Double result = null;
    if (!StringUtils.isEmpty(value)) {
      result = new Double(value);
    }
    return result;
  }

  private static Set<EnumAV> getEnumAV(EnumAT at, CellValueHolder attrValue) {
    Set<EnumAV> enumAVs = new HashSet<EnumAV>();

    String value = attrValue.getAttributeValue();
    String cell = value.trim();

    Set<EnumAV> valuesE = at.getAttributeValues();
    String[] values = getSplittedArray(cell, ExcelSheet.IN_LINE_SEPARATOR.trim());

    if (!at.isMultiassignmenttype() && values.length > 1) {
      String pattern = "Cell [{0}]  Import of value for Attribute Type {1} ignored. The attribute allows only single value assignments, but several attribute values are given.";
      String problemMsg = MessageFormat.format(pattern, getCellRef(attrValue.getOriginCell()), at.getName());
      getProcessingLog().warn(problemMsg);
      attrValue.addProblem(ProblemMarker.WARNING, problemMsg);
      throw new IllegalArgumentException(problemMsg);
    }

    for (String val : values) {
      EnumAV enumValue = findEnumAVfromCollection(valuesE, val);

      if (enumValue != null) {
        enumAVs.add(enumValue);
      }
      else if (val.length() > 0) {
        String pattern = "Cell [{0}]  Value {1} for Attribute Type {2} is not defined as possible enumeration value; ignored";
        String problemMsg = MessageFormat.format(pattern, getCellRef(attrValue.getOriginCell()), val, at.getName());
        getProcessingLog().warn(problemMsg);
        attrValue.addProblem(ProblemMarker.WARNING, problemMsg);
        throw new IllegalArgumentException(problemMsg);
      }
    }

    return enumAVs;
  }

  /**
   * Returns that EnumAV from the list <code>values</code> which has the value/name <code>val</code>.
   * 
   * @param values
   *          a list of EnumAVs
   * @param val
   *          the reference value to search for
   * @return the EnumAV object with the given value, or <code>null</code> if no such object is in
   *         the list.
   */
  private static EnumAV findEnumAVfromCollection(Collection<EnumAV> values, String val) {
    for (EnumAV av : values) {
      if (av.getName().equals(val)) {
        return av;
      }
    }
    return null;
  }

  /**
   * Returns that ResponsibilityAV from the list <code>values</code> which has the value/name
   * <code>respValue</code>.
   * 
   * @param values
   * @param respValue
   *          the reference value to search for
   * @return the ResponsibilityAV object with the given value, or <code>null</code> if no such
   *         object is in the list.
   */
  private static ResponsibilityAV findResponsibilityAVfromCollection(Collection<ResponsibilityAV> values, String respValue) {
    for (ResponsibilityAV av : values) {
      if (av.getName().equals(respValue)) {
        return av;
      }
    }
    return null;
  }

  /**
   * Creates a set of ResponsibilityAV objects from the attrValue for the given responsibility attribute type.
   * @param at The attribute to which the user name should be linked.
   * @param attrValue Cell value holder which contains the relevant responsibility value(s)/ user name(s). User names are split at semicolons
   * @param valueOverride an override value for the actual cell content. If this String is non-null/empty and non-whitespace, it will be used in the ResponsibilityAV, regardless of the cell content in attrValue!
   * @return A set of responsibility attribute value objects containing the value(s) from attrValue or valueOverride, or <code>null</code> if no user name could be identified
   */
  private static Set<ResponsibilityAV> getResponsibilityAV(ResponsibilityAT at, CellValueHolder attrValue, String valueOverride) {
    final Set<ResponsibilityAV> respAVs = Sets.newHashSet();
    final String cell = StringUtils.defaultString(valueOverride, attrValue.getAttributeValue());

    Collection<ResponsibilityAV> valuesR = at.getAttributeValues();
    String[] names = getSplittedArray(cell, ExcelSheet.IN_LINE_SEPARATOR.trim());

    if (!at.isMultiassignmenttype() && names.length > 1) {
      String pattern = "Cell [{0}]  Import of value for Attribute Type {1} ignored. The attribute allows only single value assignments, but several attribute values are given.";
      String problemMsg = MessageFormat.format(pattern, getCellRef(attrValue.getOriginCell()), at.getName());
      getProcessingLog().warn(problemMsg);
      attrValue.addProblem(ProblemMarker.WARNING, problemMsg);
      throw new IllegalArgumentException(problemMsg);
    }

    for (String name : names) {
      ResponsibilityAV responsibilityValue = findResponsibilityAVfromCollection(valuesR, name);
      if (responsibilityValue != null) {
        respAVs.add(responsibilityValue);
      }
      else if (name.length() > 0) {
        String problemMsg = "Cell [" + getCellRef(attrValue.getOriginCell()) + "]  Value " + name + " for responsibility attribute type "
            + at.getName() + " is not defined as possible enumeration value; ignored";
        getProcessingLog().warn(problemMsg);
        attrValue.addProblem(ProblemMarker.WARNING, problemMsg);
        throw new IllegalArgumentException(problemMsg);
      }
    }
    return respAVs;
  }

  /**
   * Splits String around each <code>separator</code> and trims whitespace.
   * 
   * @param str
   *          a string to be split
   * @return an array of Strings, whose elements were separated at the separator string
   */
  public static String[] getSplittedArray(String str, String separator) {
    // escape special regex chars
    String separatorPattern = separator;
    final char[] specialChars = { '\\', '(', ')', '[', ']', '{', '}', '.', '^', '$', '?', '*', '+' };

    for (char special : specialChars) {
      separatorPattern = separatorPattern.replace(String.valueOf(special), "\\" + special);
    }

    String[] result = str.split(separatorPattern);
    for (int i = 0; i < result.length; i++) {
      result[i] = result[i].trim();
    }
    return result;
  }

  /**
   * Reads cell and checks content for being null or "".
   * 
   * @param cell
   *          a cell to check
   * @return true for empty cell
   */
  public static boolean isEmpty(final Cell cell) {
    return (cell == null || StringUtils.isEmpty(cell.toString()));
  }

  /**
   * Returns cell content or cell content being referenced in a formula as String. Also reads
   * numeric cells, but these must not be referenced (a poi formulaCell). A cell being NULL or empty
   * returns "". For formulaCells that are not string Formulas, an empty String is returned, too.
   * Directly using poi's getRichStringCellValue() on a HSSFCell being NULL would throw an
   * exception. Cell in row is specified by the headline map and the key. Warning: Does not support
   * Dates (see currentRowCoreKeyToCell)
   * 
   * @param cell
   *          a cell
   * @return a String from cell or an referenced cell, leading and ending white space removed
   */
  public static String contentAsString(Cell cell, ProcessingLog processingLog) {
    if (cell == null) {
      return "";
    }
    switch (cell.getCellType()) {

      case Cell.CELL_TYPE_STRING: // for richStringsCells and formulaCells
        return cell.getRichStringCellValue().getString().trim();

      case Cell.CELL_TYPE_FORMULA: // for formulaCells / Hyperlinks(id)
        int type = cell.getCachedFormulaResultType();
        String result = "";
        switch (type) {
          case Cell.CELL_TYPE_BOOLEAN:
            result = String.valueOf(cell.getBooleanCellValue());
            break;
          case Cell.CELL_TYPE_NUMERIC:
            result = String.valueOf(cell.getNumericCellValue());
            break;
          case Cell.CELL_TYPE_STRING:
            result = String.valueOf(cell.getRichStringCellValue());
            break;
          default:
            break;
        }
        if (result.isEmpty()) {
          processingLog.warn("Cell [{0}]  Cannot get a String from a numeric cell being referenced by a formula: {1}", getCellRef(cell),
              cell.getCellFormula());
        }
        return result;

      case Cell.CELL_TYPE_NUMERIC:
        return getNumericCellContentAsString(cell, processingLog);

      default:
        return cell.toString().trim();
    }

  }

  private static String getNumericCellContentAsString(Cell cell, ProcessingLog processingLog) {
    // for numeric cells / dates we have to look at the cell format to tell if it's a date cell
    // If so, we retrieve the value as a date and convert it to ISO String notation

    if (HSSFDateUtil.isCellDateFormatted(cell)) {
      // is it a date-formatted number? then return the ISO-formatted date instead of the number
      Date cellDate = contentAsDate(cell);
      final SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
      return dateformatter.format(cellDate);
    }

    Double d = null;
    try {
      d = contentAsDouble(cell);
    } catch (NumberFormatException ex) {
      processingLog.warn("Cell [{0}] {1}; ignoring the value", getCellRef(cell), ex.getMessage());
    } catch (IllegalStateException e) {
      processingLog.warn("Cell [{0}] {1}; ignoring the value", getCellRef(cell), e.getMessage());
    }

    if (d != null) {
      // cut off *.0
      double i = d.doubleValue() - d.intValue();

      if (i == 0) {
        Integer j = Integer.valueOf(d.intValue());
        return j.toString();
      }
      else {
        return d.toString();
      }
    }
    return "";
  }

  /**
   * Returns cell content as Double. A cell being NULL or empty returns NULL. Directly using poi's
   * getNumericCellValue() on a HSSFCell being NULL would throw an exception. An empty cell would be
   * 0. Cell in row is specified by the headline map and the key.
   * 
   * @param row
   *          a poi-HSSFRow
   * @param map
   *          a map to get the index by cell in headline
   * @param key
   *          a key for the map
   * @return a Double
   * @throws NumberFormatException
   *           if the number has a wrong format
   * @throws IllegalStateException
   *           if the content of the given <code>cell</code> is not a number
   */
  @SuppressWarnings("boxing")
  private static Double contentAsDouble(Cell cell) {
    if (cell == null) {
      throw new IllegalArgumentException("Cell must not be null");
    }

    Double result = null;
    if (!ExcelImportUtilities.isEmpty(cell)) {
      result = cell.getNumericCellValue();
    }
    return result;
  }

  /**
   * Returns cell content as Date. A cell being null or empty returns NULL. For Strings an
   * exception is thrown. Directly using poi's getDateCellValue() on a HSSFCell being NULL would
   * throw an exception. Cell in row is specified by the headline map and the key.
   * 
   * @param cell
   *          a cell containing a date
   * @return a Date
   * @throws NumberFormatException
   */
  public static Date contentAsDate(Cell cell) {
    Date date = null;
    if (!ExcelImportUtilities.isEmpty(cell)) {
      date = cell.getDateCellValue();
    }

    return date;
  }

  /**
   * Returns the index of last row on the specified {@code sheet}.
   * 
   * @param sheet the sheet
   * @return the index of last row on the specified {@code sheet}
   */
  public static int getLastRow(Sheet sheet) {
    return sheet.getLastRowNum();
  }

  /**
   * Returns {@code true} if the specified {@code row} is not the last one in the
   * specified {@code sheet}. Otherwise returns {@code false}.
   * 
   * @param sheet the Excel sheet
   * @param row the row to check
   * @return {@code true} if the specified {@code row} is not the last one in the
   *    specified {@code sheet}
   */
  public static boolean hasNextRow(Sheet sheet, int row) {
    int lastRow = getLastRow(sheet);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Current row: {0}; Last row: {1}", Integer.valueOf(row), Integer.valueOf(lastRow));
    }

    return row <= lastRow;
  }

  public static Map<String, Integer> getHeadlineForRange(Row headline, int[] range, ProcessingLog processingLog) {
    Map<String, Integer> map = Maps.newLinkedHashMap();

    for (int columnNo = range[0]; columnNo < range[1]; columnNo++) {
      String cellContent = ExcelImportUtilities.contentAsString(headline.getCell(columnNo), processingLog);
      map.put(cellContent, Integer.valueOf(columnNo));
    }

    if (LOGGER.isDebugEnabled()) {
      String headlines = Joiner.on(" // ").join(map.keySet());
      LOGGER.debug(headlines);
    }

    return map;
  }

  /**
   * Reads the content from the specified {@code row}. The specified {@code headline} defines the
   * head names associated with the column indexes. Only these specified columns will be read
   * and returned.
   * 
   * @param row the row to read the content from
   * @param headline the map containing headline names associated with the headline column indexes
   * @return the map containing headline names associated with the specified {@code row} content
   */
  public static Map<String, Cell> readRow(Row row, Map<String, Integer> headline) {
    final Map<String, Cell> result = new HashMap<String, Cell>();

    for (Map.Entry<String, Integer> headlineEntry : headline.entrySet()) {
      String headlineName = headlineEntry.getKey();
      Integer columnIndex = headlineEntry.getValue();
      Cell curCell = row.getCell(columnIndex.intValue(), Row.CREATE_NULL_AS_BLANK);

      if (curCell == null) {
        // may happen if a single cell was deleted/has no contents --> avoid the mapping to null
        continue;
      }

      LOGGER.debug("  {0}={1}", headlineName, curCell);
      result.put(headlineName, curCell);
    }

    return result;
  }

  /**
   * Returns a {@link Cell}'s coordinates
   * @param cell
   *          the given {@link Cell}
   * @return String representing the cell's coordinates
   */
  public static String getCellRef(Cell cell) {
    if (cell == null) {
      return "undef";
    }
    CellReference cellref = new CellReference(cell.getRowIndex(), cell.getColumnIndex());
    return cellref.formatAsString();
  }

  /**
   * Returns a {@link Cell}'s row number
   * @param cell
   *          the given {@link Cell}
   * @return The cell's row number as String
   */
  public static String getCellRow(Cell cell) {
    if (cell == null) {
      return "undef";
    }
    CellReference cellref = new CellReference(cell.getRowIndex(), cell.getColumnIndex());
    return String.valueOf(cellref.getRow() + 1);
  }

}
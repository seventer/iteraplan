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

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;


/**
 * An {@link ImportWorkbook} extension used to import Enumeration Attributes out of the
 * specified Excel file. This file must contain configuration sheet named as
 * {@code "ExcelTemplateSheet"} and attributes sheets containing the values
 * associated with the attributes
 */
public class AttributeWorkbook extends ImportWorkbook {
  public static final Logger                                  LOGGER                       = Logger.getIteraplanLogger(AttributeWorkbook.class);
  /** The column key for the attribute name. */
  public static final String                                  NAME_COLUMN_KEY              = "attribute.copy.attName";
  /** The column key for the attribute old name. */
  public static final String                                  OLDNAME_COLUMN_KEY           = "attribute.copy.oldAttName";
  /** The column key for the attribute description. */
  public static final String                                  DESCRIPTION_COLUMN_KEY       = "global.description";
  /** The column key for the attribute group. */
  public static final String                                  GROUP_COLUMN_KEY             = "global.attributegroup";
  /** The column key to set a mandatory/non-mandatory attribute. */
  public static final String                                  MANDATORY_COLUMN_KEY         = "manageAttributes.mandatoryattribute";
  /** The column key for the attribute multiplicity. */
  public static final String                                  MULTIPLE_COLUMN_KEY          = "manageAttributes.multiplevalues";
  /** The column key for the building block ids to which the attribute is active for. */
  public static final String                                  ACTIVEFORBB_COLUMN_KEY       = "attribute.copy.bbt";
  /** The column key for the attribute value. */
  public static final String                                  VALUE_COLUMN_KEY             = "attribute.copy.values";
  /** The column key for the attribute old value. */
  public static final String                                  OLDVALUE_COLUMN_KEY          = "attribute.copy.oldValues";
  /** The column key for the attribute old value. */
  public static final String                                  VALUE_DESCRIPTION_COLUMN_KEY = "attribute.copy.valuesDescription";
  /** The column key for the attribute user values in responsibility at. */
  public static final String                                  VALUE_USER_COLUMN_KEY        = "attribute.copy.userValues";
  /** The column key for the attribute user group values in responsibility at. */
  public static final String                                  VALUE_USERGROUP_COLUMN_KEY   = "attribute.copy.userGroupValues";
  /** The column key for the attribute upper bound. */
  public static final String                                  UPPERBOUND_COLUMN_KEY        = "global.upperbound.short";
  /** The column key for the attribute lower bound. */
  public static final String                                  LOWERBOUND_COLUMN_KEY        = "global.lowerbound.short";
  /** The column key for the attribute unit. */
  public static final String                                  UNIT_COLUMN_KEY              = "manageAttributes.numberAT.unit";
  /** The column key for the attribute uniform range. */
  public static final String                                  UNIFORM_COLUMN_KEY           = "manageAttributes.numberAT.uniformrange";
  /** The column key for the attribute multiline. */
  public static final String                                  MULTILINE_COLUMN_KEY         = "manageAttributes.textAT.multiline";
  /** The column key for the attribute user defined ranges. */
  public static final String                                  USERRANGES_COLUMN_KEY        = "manageAttributes.numberAT.manualRanges";

  private static final Map<String, AttributeSheetImporter<?>> SHEET_IMPORTER               = Maps.newHashMap();
  
  private static final Map<String, List<String>>              ATTRIBUTE_TYPE_HEADLINES     = Maps.newHashMap();

  public AttributeWorkbook(ProcessingLog userLog) {
    super(userLog);
  }

  /**
   * Imports the data out of the specified Excel file input stream. If data
   * could not be imported, an {@code empty} list will be returned.
   * 
   * @param is the Excel file input stream
   * @return the list of imported object related permission data objects
   */
  public AttributeData doImport(InputStream is) {
    AttributeData result = new AttributeData();

    loadWorkbookFromInputStream(is);
    initSheetImporters();
    initAttributeTypeHeadlines();

    if (!readInConfigSheet()) {
      getProcessingLog().error("Could not read in configuration sheet. Aborting.");
      return result;
    }

    calculateAllFormulas();
    for (int i = 0; i < getWb().getNumberOfSheets(); i++) {
      Sheet sheet = getWb().getSheetAt(i);
      String sheetName = sheet.getSheetName();

      getProcessingLog().info("Current Sheet: " + StringUtils.defaultIfEmpty(sheetName, "null"));

      if (DEFAULT_SHEET_KEY.equals(sheetName)) {
        continue;
      }

      if (!SHEET_IMPORTER.containsKey(sheetName)) {
        getProcessingLog().error("No import is available for this sheet.");
        continue;
      }

      importSheet(sheet, result);
    }

    return result;
  }

  /**
   * sets new sheet importer for locale.
   */
  private void initSheetImporters() {
    String enumSheetName = MessageAccess.getStringOrNull("attribute.type.enum", getLocale());
    String responsibilitySheetName = MessageAccess.getStringOrNull("attribute.type.responsibility", getLocale());
    String numberSheetName = MessageAccess.getStringOrNull("attribute.type.number", getLocale());
    String dateSheetName = MessageAccess.getStringOrNull("attribute.type.date", getLocale());
    String textSheetName = MessageAccess.getStringOrNull("attribute.type.text", getLocale());

    SHEET_IMPORTER.clear();
    SHEET_IMPORTER.put(enumSheetName, new EnumSheetImporter(getLocale(), getProcessingLog()));
    SHEET_IMPORTER.put(responsibilitySheetName, new ResponsibilitySheetImporter(getLocale(), getProcessingLog()));
    SHEET_IMPORTER.put(numberSheetName, new NumberSheetImporter(getLocale(), getProcessingLog()));
    SHEET_IMPORTER.put(dateSheetName, new DateSheetImporter(getLocale(), getProcessingLog()));
    SHEET_IMPORTER.put(textSheetName, new TextSheetImporter(getLocale(), getProcessingLog()));
  }

  /**
   * Imports the data from the specified {@code sheet}.
   * @param sheet the sheet to import the data from
   * @param attributeData Container for imported Attributes
   */
  private void importSheet(Sheet sheet, AttributeData attributeData) {
    getProcessingLog().debug("Importing Sheet: " + sheet.getSheetName());

    String headColumnName = MessageAccess.getStringOrNull(NAME_COLUMN_KEY, getLocale());
    int contentPosition = findSheetContentPosition(sheet, headColumnName, 0);
    if (contentPosition != -1) {
      Row headRow = sheet.getRow(contentPosition - 1);
      
      List<String> tmp = ATTRIBUTE_TYPE_HEADLINES.get(sheet.getSheetName());
      Map<String, Integer> headline = ExcelImportUtilities.getHeadlineForRange(headRow, new int[] { 0, 12 }, getProcessingLog());
      
      for(String columnName : tmp) {
        if(!headline.containsKey(columnName)) {
          getProcessingLog().error("Attribute column {0} is missing for {1}. Partial import is not possible.", columnName, sheet.getSheetName());
          continue;
        }
      }
      
      List<Row> sheetContentRows = getSheetContentRows(contentPosition, sheet);

      AttributeSheetImporter<?> importer = SHEET_IMPORTER.get(sheet.getSheetName());
      importer.readContentFromSheet(sheetContentRows, headline, attributeData);
    }
    else {
      getProcessingLog().warn("Invalid structure of Sheet '" + sheet.getSheetName() + "', skipping");
    }
  }

  private void initAttributeTypeHeadlines(){
    String enumSheetName = MessageAccess.getStringOrNull("attribute.type.enum", getLocale());
    String responsibilitySheetName = MessageAccess.getStringOrNull("attribute.type.responsibility", getLocale());
    String numberSheetName = MessageAccess.getStringOrNull("attribute.type.number", getLocale());
    String dateSheetName = MessageAccess.getStringOrNull("attribute.type.date", getLocale());
    String textSheetName = MessageAccess.getStringOrNull("attribute.type.text", getLocale());
    
    ATTRIBUTE_TYPE_HEADLINES.put(enumSheetName, getEnumATHeadlines());
    ATTRIBUTE_TYPE_HEADLINES.put(responsibilitySheetName, getResponsibilityATHeadlines());
    ATTRIBUTE_TYPE_HEADLINES.put(numberSheetName, getNumberATHeadlines());
    ATTRIBUTE_TYPE_HEADLINES.put(dateSheetName, getDateATHeadlines());
    ATTRIBUTE_TYPE_HEADLINES.put(textSheetName, getTextATHeadlines());
  }
  
  private List<String> getTextATHeadlines() {
    String multilineColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.MULTILINE_COLUMN_KEY, getLocale());
    
    List<String> headlines = getCommonAttributeHeadlines();
    
    headlines.add(multilineColumnName);
    
    return headlines;
  }
  
  private List<String> getDateATHeadlines() {
    return getCommonAttributeHeadlines();
  }
  
  private List<String> getNumberATHeadlines() {
    String lowerboundColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.LOWERBOUND_COLUMN_KEY, getLocale());
    String upperboundColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.UPPERBOUND_COLUMN_KEY, getLocale());
    String unitColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.UNIT_COLUMN_KEY, getLocale());
    String uniformColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.UNIFORM_COLUMN_KEY, getLocale());
    String userRangesColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.USERRANGES_COLUMN_KEY, getLocale());
    
    List<String> headlines = getCommonAttributeHeadlines();
    
    headlines.add(lowerboundColumnName);
    headlines.add(upperboundColumnName);
    headlines.add(unitColumnName);
    headlines.add(uniformColumnName);
    headlines.add(userRangesColumnName);
    
    return headlines;
  }
  
  private List<String> getEnumATHeadlines() {
    String multipleColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.MULTIPLE_COLUMN_KEY, getLocale());
    String valueColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.VALUE_COLUMN_KEY, getLocale());
    String oldvalueColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.OLDVALUE_COLUMN_KEY, getLocale());
    String valueDescriptionColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.VALUE_DESCRIPTION_COLUMN_KEY, getLocale());
    
    List<String> headlines = getCommonAttributeHeadlines();
    
    headlines.add(multipleColumnName);
    headlines.add(valueColumnName);
    headlines.add(oldvalueColumnName);
    headlines.add(valueDescriptionColumnName);
    
    return headlines;
  }
  
  private List<String> getResponsibilityATHeadlines() {
    String multipleColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.MULTIPLE_COLUMN_KEY, getLocale());
    String usersColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.VALUE_USER_COLUMN_KEY, getLocale());
    String userGroupsColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.VALUE_USERGROUP_COLUMN_KEY, getLocale());
    
    List<String> headlines = getCommonAttributeHeadlines();
    
    headlines.add(multipleColumnName);
    headlines.add(usersColumnName);
    headlines.add(userGroupsColumnName);
    
    return headlines;
  }
  
  private List<String> getCommonAttributeHeadlines() {
    String nameColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.NAME_COLUMN_KEY, getLocale());
    String oldNameColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.OLDNAME_COLUMN_KEY, getLocale());
    String descriptionColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.DESCRIPTION_COLUMN_KEY, getLocale());
    String groupColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.GROUP_COLUMN_KEY, getLocale());
    String mandatoryColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.MANDATORY_COLUMN_KEY, getLocale());
    String activeForBbColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.ACTIVEFORBB_COLUMN_KEY, getLocale());
    
    List<String> headlines = Lists.newArrayList();
    
    headlines.add(nameColumnName);
    headlines.add(oldNameColumnName);
    headlines.add(descriptionColumnName);
    headlines.add(groupColumnName);
    headlines.add(mandatoryColumnName);
    headlines.add(activeForBbColumnName);
    
    return headlines;
  }
  
  /**
   * Returns the sheet content rows from the specified {@code sheet}.
   * 
   * @param contentPosition the first content row index
   * @param sheet the sheet to get the rows from
   * @return the list of all content rows from the specified {@code sheet}
   */
  private List<Row> getSheetContentRows(int contentPosition, Sheet sheet) {
    final List<Row> result = Lists.newArrayList();
    int currentContentRow = contentPosition;

    while (ExcelImportUtilities.hasNextRow(sheet, currentContentRow)) {
      Row row = sheet.getRow(currentContentRow);

      // can happen if a row was deleted (but not removed) in Excel -> return no data
      if (row != null) {
        result.add(row);
      }

      currentContentRow++;
    }

    return result;
  }
}
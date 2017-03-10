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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * An {@link ImportWorkbook} extension used to import object related permissions out of the
 * specified Excel file. This file must contain configuration sheet named as
 * {@code "ExcelTemplateSheet"} and building block sheets containing building block ids
 * associated with the user names, which should become the exclusive read/write permissions
 * on these objects.
 * 
 * @author agu
 * 
 * @see ObjectRelatedPermissionsData
 *
 */
public class ObjectRelatedPermissionsWorkbook extends ImportWorkbook {
  private static final Logger LOGGER          = Logger.getIteraplanLogger(ObjectRelatedPermissionsWorkbook.class);
  /** The column key for id. */
  private static final String ID_COLUMN_KEY   = "global.id";
  /** The column key for name. */
  private static final String NAME_COLUMN_KEY   = "global.name";
  /** The column key for user. */
  private static final String USER_COLUMN_KEY = "global.users";

  public ObjectRelatedPermissionsWorkbook(ProcessingLog userLog) {
    super(userLog);
  }

  /**
   * Imports the data out of the specified Excel file input stream. If data
   * could not be imported, an {@code empty} list will be returned.
   * 
   * @param is the Excel file input stream
   * @return the list of imported object related permission data objects
   */
  public List<ObjectRelatedPermissionsData> doImport(InputStream is) {
    List<ObjectRelatedPermissionsData> result = Lists.newArrayList();

    loadWorkbookFromInputStream(is);

    if (!readInConfigSheet()) {
      getProcessingLog().error("Could not read in configuration sheet. Aborting.");
      return result;
    }

    calculateAllFormulas();
    final Map<String, TypeOfBuildingBlock> supportedSheetNames = getSupportedSheetNames();
    for (int i = 0; i < getWb().getNumberOfSheets(); i++) {
      Sheet sheet = getWb().getSheetAt(i);
      String sheetName = sheet.getSheetName();

      getProcessingLog().debug("Current Sheet: " + StringUtils.defaultIfEmpty(sheetName, "null"));

      if (supportedSheetNames.containsKey(sheetName)) {
        List<ObjectRelatedPermissionsData> sheetObjectPerrmissions = importSheet(sheet, supportedSheetNames.get(sheetName));
        result.addAll(sheetObjectPerrmissions);
      }
      else if (!DEFAULT_SHEET_KEY.equals(sheetName)) {
        getProcessingLog().warn("Unknown Sheet Name '" + sheetName + "'(different Locale?). Skipping.");
      }
    }

    return result;
  }

  /**
   * Returns the map containing supported sheet names associated with the building block
   * types. The names are localized, using the locale specified in Excel configuration sheet.
   * 
   * @return the set containing supported sheet names
   */
  private Map<String, TypeOfBuildingBlock> getSupportedSheetNames() {
    final Map<String, TypeOfBuildingBlock> result = Maps.newHashMap();

    addSupportedSheet(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, result);
    addSupportedSheet(TypeOfBuildingBlock.BUSINESSDOMAIN, result);
    addSupportedSheet(TypeOfBuildingBlock.BUSINESSFUNCTION, result);
    addSupportedSheet(TypeOfBuildingBlock.BUSINESSOBJECT, result);
    addSupportedSheet(TypeOfBuildingBlock.BUSINESSPROCESS, result);
    addSupportedSheet(TypeOfBuildingBlock.BUSINESSUNIT, result);
    addSupportedSheet(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, result);
    addSupportedSheet(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, result);
    addSupportedSheet(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, result);
    addSupportedSheet(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, result);
    addSupportedSheet(TypeOfBuildingBlock.PRODUCT, result);
    addSupportedSheet(TypeOfBuildingBlock.PROJECT, result);
    addSupportedSheet(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, result);

    return result;
  }

  private void addSupportedSheet(TypeOfBuildingBlock type, Map<String, TypeOfBuildingBlock> supportedSheets) {
    String sheetName = findExcelSheetNameFor(type);
    supportedSheets.put(sheetName, type);
  }

  /**
   * Imports the data from the specified {@code sheet}.
   * 
   * @param sheet the sheet to import the data from
   * @param type the type ob building block this sheet represents
   * @return the list of {@link ObjectRelatedPermissionsData} objects containing imported data
   */
  private List<ObjectRelatedPermissionsData> importSheet(Sheet sheet, TypeOfBuildingBlock type) {
    getProcessingLog().debug("Importing Sheet: " + sheet.getSheetName());

    String headColumnName = MessageAccess.getStringOrNull(ID_COLUMN_KEY, getLocale());
    int contentPosition = findSheetContentPosition(sheet, headColumnName, 0);
    if (contentPosition != -1) {
      Row headRow = sheet.getRow(contentPosition - 1);
      Map<String, Integer> headline = ExcelImportUtilities.getHeadlineForRange(headRow, new int[] { 0, 3 }, getProcessingLog());
      List<Row> sheetContentRows = getSheetContentRows(contentPosition, sheet);

      return readContentFromSheet(sheetContentRows, headline, type);
    }
    else {
      getProcessingLog().warn("Invalid structure of Sheet '" + sheet.getSheetName() + "', skipping");
      return Collections.emptyList();
    }
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

  private List<ObjectRelatedPermissionsData> readContentFromSheet(List<Row> sheetContentRows, Map<String, Integer> headline,
                                                                  TypeOfBuildingBlock typeOfBuildingBlock) {
    final List<ObjectRelatedPermissionsData> result = Lists.newArrayList();

    for (Row row : sheetContentRows) {
      LOGGER.debug(" " + row.getSheet().getSheetName() + ": Row " + row.getRowNum());

      Map<String, Cell> rowContent = ExcelImportUtilities.readRow(row, headline);
      ObjectRelatedPermissionsData userPermissions = parseRow(rowContent, typeOfBuildingBlock, row.getRowNum() + 1);

      if (userPermissions != null) {
        result.add(userPermissions);
      }
    }

    return result;
  }

  private ObjectRelatedPermissionsData parseRow(Map<String, Cell> rowContent, TypeOfBuildingBlock typeOfBuildingBlock, int rowIndex) {
    String idColumnName = MessageAccess.getStringOrNull(ID_COLUMN_KEY, getLocale());
    String nameColumnName = MessageAccess.getStringOrNull(NAME_COLUMN_KEY, getLocale());
    String userColumnName = MessageAccess.getStringOrNull(USER_COLUMN_KEY, getLocale());

    CellValueHolder id = new CellValueHolder(rowContent.get(idColumnName));
    CellValueHolder name = new CellValueHolder(rowContent.get(nameColumnName));
    CellValueHolder users = new CellValueHolder(rowContent.get(userColumnName));

    if (!StringUtils.isBlank(id.getAttributeValue()) && parseId(id.getAttributeValue()) == null) {
      getProcessingLog().warn("Could not understand ID \"{0}\" in row {1}", id.getAttributeValue(), Integer.valueOf(rowIndex));
    }

    if (parseId(id.getAttributeValue()) == null && 
        (name.getAttributeValue() == null || StringUtils.isBlank(name.getAttributeValue()))) {
      getProcessingLog().error("Neither id nor name provided in row {0}, skipping", Integer.valueOf(rowIndex));
      return null;
    }

    if (StringUtils.isBlank(users.getAttributeValue())) {
      getProcessingLog().error("The users are not specified for id \"{0}\" in row {1}, skipping", id, Integer.valueOf(rowIndex));
      return null;
    }

    return new ObjectRelatedPermissionsData(id, name, typeOfBuildingBlock, users);
  }

  @SuppressWarnings("boxing")
  private Integer parseId(String content) {
    try {
      return (int) Double.parseDouble(content);

    } catch (NumberFormatException e) {
      LOGGER.debug("Could not understand Id '" + content + "'");
      return null;
    }

  }

}

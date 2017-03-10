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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.sheets;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.util.Assert;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.CellValueHolder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ExcelImportUtilities;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeRowData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProblemMarker;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog;
import de.iteratec.iteraplan.businesslogic.service.EntityService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public abstract class SheetImporter<T extends BuildingBlock> {

  private static final Logger       LOGGER       = Logger.getIteraplanLogger(SheetImporter.class);

  protected static final String     SKIPPING_ROW = "Skipping Import of Row [{0}]";

  private final Locale              locale;
  private final ProcessingLog       processingLog;
  private final TypeOfBuildingBlock typeOfBuildingBlockOnSheet;

  /**
   * Initializes the data and routines common to all worksheets. Takes the
   * {@link de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ImportWorkbook ImportWorkbook}
   * to operate on and the building block type to import from the sheet.
   *
   * @param locale the current locale specified in Excel configuration sheet
   * @param tob
   *          The the building block type that is imported from the worksheet. It's mainly required
   *          to identify the spreadsheet column that includes the building block name.
   * @param processingLog the processing log for saving import messages
   */
  protected SheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
    this.locale = locale;
    this.typeOfBuildingBlockOnSheet = tob;
    this.processingLog = processingLog;
  }

  public void doImport(Sheet sheet, int contentRowOffset, final int headRowIndex, LandscapeData landscapeData) {
    Row headlineRow = sheet.getRow(headRowIndex);
    Map<String, Integer> buildingBlocksHeadline = getBuildingBlocksHeadline(headlineRow);
    Map<String, Integer> attributesHeadline = getAttributeHeadline(headlineRow);
    int currentContentRow = contentRowOffset;

    while (ExcelImportUtilities.hasNextRow(sheet, currentContentRow)) {
      LOGGER.debug(" " + sheet.getSheetName() + ": Row " + currentContentRow);
      Row row = sheet.getRow(currentContentRow);

      // can happen if a row was deleted (but not removed) in Excel -> return no data
      if (row != null) {
        Map<String, Cell> buildingBlocksRow = ExcelImportUtilities.readRow(row, buildingBlocksHeadline);
        Map<String, Cell> attributes = ExcelImportUtilities.readRow(row, attributesHeadline);

        final LandscapeRowData rowData = new LandscapeRowData(buildingBlocksRow, attributes);
        try {
          save(rowData, landscapeData);
        } catch (IteraplanBusinessException e) {
          LOGGER.warn(e);
          getProcessingLog().warn("Error importing row {0}. Skipping.", Integer.valueOf(row.getRowNum() + 1));
        }
      }
      currentContentRow += 1;
    }
  }

  /**
   * Save the read in row to the LandscapeData object, which is later saved to the DAOs in the
   * ExcelImportService
   *
   * @param rowData the landscape row data containing building blocks and attributes of the read row
   * @param landscapeData the landscape data, where parsed building blocks, relations and attributes should be saved
   * @return false if any errors occurred and import of this row was aborted, true otherwise
   */
  private boolean save(LandscapeRowData rowData, LandscapeData landscapeData) {
    if (rowData == null) {
      return false;
    }

    if (isValidRow(rowData.getBuildingBlocks())) {
      return importRowIntoBB(rowData, landscapeData);
    }
    else {
      return false;
    }
  }

  /**
   * Write the row data into the according building block and add it to the LandscapeData object.
   *
   * @param rowData the landscape row data containing building blocks and attributes of the read row
   * @param landscapeData the landscape data, where parsed building blocks, relations and attributes should be saved
   * @return false if any errors occurred and import of this row was aborted, true otherwise
   */
  protected abstract boolean importRowIntoBB(LandscapeRowData rowData, LandscapeData landscapeData);

  protected abstract boolean isValidRow(Map<String, Cell> buildingBlockRowMap);

  /**
   * Checks if an element of the current sheet's type with the given {@code name}
   * already exists under a different id than the given one.
   * If {@code id} is null, checks if an element with the given {@code name} exists at all;
   * @param nameCell
   *          cell with the name to look for
   * @param id
   *          id to check for
   * @return true if an object with the given name and a different than the given id exists
   */
  protected boolean doesEntityExistByNameWithDifferentID(Cell nameCell, Integer id) {
    if (nameCell == null) {
      return false;
    }
    String name = ExcelImportUtilities.contentAsString(nameCell, getProcessingLog());
    boolean objectExists = getService().doesObjectWithDifferentIdExist(id, name);
    if (objectExists) {
      getProcessingLog().warn("Cell [{0}]: An element \"{1}\" of type {2} exists already with a different ID than specified ({3}).",
          ExcelImportUtilities.getCellRef(nameCell), name, getConstant(typeOfBuildingBlockOnSheet.getValue()), id);
      getProcessingLog().warn(SKIPPING_ROW, ExcelImportUtilities.getCellRow(nameCell));
    }
    return objectExists;
  }

  /**
   * Retrieves an appropriate building block with the given ID from database, if the ID is known.
   * If the ID is <code>null</code>, a new building block instance is created. In both cases,
   * the returned object can later be saved back to the database.
   * If a non-null ID is given and the according building block isn't found in the database,
   * null is returned.
   * @param buildingBlockRowData the row data containing the id for the building block to create or load from database.
   * @return A freshly loaded building block object from the database, if one was found for
   * the given ID, or null if not, or a newly created building block object, if ID was null.
   */
  protected T createOrLoad(Map<String, Cell> buildingBlockRowData) {
    T entity;
    Integer id = getId(buildingBlockRowData);
    if (id == null) {
      entity = createBuildingBlockInstance();
    }
    else {
      entity = getService().loadObjectByIdIfExists(id);
    }

    if (entity == null) {
      Cell idCell = buildingBlockRowData.get(getConstant("global.id"));
      getProcessingLog().warn("Cell [{0}]: No Element with the specified ID {1} of type {2} found.", ExcelImportUtilities.getCellRef(idCell), id,
          getConstant(getTypeOfBuildingBlockOnSheet().getValue()));
      getProcessingLog().warn(SKIPPING_ROW, ExcelImportUtilities.getCellRow(idCell));
    }

    return entity;
  }

  /**
   * Creates a new instance of a building block
   * @return building block instance
   */
  protected abstract T createBuildingBlockInstance();

  /**
   * @return the appropriate service for this {@link SheetImporter}'s subclass's building block type
   */
  protected abstract EntityService<T, Integer> getService();

  /**
   * Gets a constant from ApplicationResources in proper Locale
   *
   * @param key
   */
  protected String getConstant(String key) {
    return MessageAccess.getStringOrNull(key, locale);
  }

  /**
   * gets if a BB name is not empty (which is assumed to be valid in the first place)
   *
   * @param name
   * @return true if valid
   */
  protected boolean isNameSet(String name) {
    LOGGER.debug(" Cur BB Name: " + name);
    return StringUtils.isNotEmpty(name);
  }

  /**
   * BB-names must not contain ":" characters
   *
   * @param name
   * @return true if valid
   */
  protected boolean isNameValid(String name) {
    if (name == null) {
      return false;
    }
    if (name.contains(Constants.HIERARCHYSEP.trim())) {
      return false;
    }
    return true;
  }

  /**
   * Gets map of Header-keys to column-index
   *
   * @param sheetName
   */
  private Map<String, Integer> getBuildingBlocksHeadline(Row headline) {
    Assert.notNull(headline, "head line row must not be null");

    int[] range = getBbSpecificRange(headline);
    Map<String, Integer> map = ExcelImportUtilities.getHeadlineForRange(headline, range, getProcessingLog());

    if (map.isEmpty()) {
      throw new IteraplanTechnicalException(new IllegalStateException("The head line row must not be empty"));
    }

    return map;
  }

  private Map<String, Integer> getAttributeHeadline(Row headline) {
    Assert.notNull(headline, "head line row must not be null");

    int[] range = getAttributeRange(headline);
    return ExcelImportUtilities.getHeadlineForRange(headline, range, getProcessingLog());
  }

  /**
   * Divide headline in category groups at each occurrences of '#' BB specific; attributes
   *
   * @param headline
   *          a POI Row
   * @return Range of cells for BuildingBlock specific data. int[0]=first cell, int[1]=last cell
   *         PLUS ONE
   */
  @SuppressWarnings("boxing")
  private int[] getBbSpecificRange(Row headline) {
    int[] range = new int[2];

    List<Integer> indices = getAttributeRanges(headline);
    range[0] = 0;
    range[1] = indices.get(0);

    return range;
  }

  /**
   * Divide headline in category groups at each occurrences of '#' BB specific; attributes
   *
   * @param headline
   *          a POI FRow
   * @return Range of cells for AttributeTypes. int[0]=first cell, int[1]=last cell PLUS ONE
   */
  @SuppressWarnings("boxing")
  private int[] getAttributeRange(Row headline) {
    int[] range = new int[2];

    List<Integer> indices = getAttributeRanges(headline);
    range[0] = indices.get(0) + 1;
    range[1] = indices.get(1);

    return range;
  }

  /**
   * Divide headline in category groups at each occurrences of '#' BB specific; attributes
   *
   * @param headline
   *          a POI Row
   * @return A list of Integers being indices for cells equal '#'
   */
  @SuppressWarnings("boxing")
  private List<Integer> getAttributeRanges(Row headline) {
    List<Integer> indices = new LinkedList<Integer>();

    int size = getRowSize(headline);
    for (int i = 0; i < size; i++) {
      String cell = ExcelImportUtilities.contentAsString(headline.getCell(i), getProcessingLog());
      if ("#".equals(cell)) {
        // TODO define separator char for column intervals
        indices.add(i);
      }
    }

    while (indices.size() < 3) {
      indices.add(size);
    }

    return indices;
  }

  /**
   * Last cells must not contain an empty String
   *
   * @param row
   *          a POI Row
   * @return int number of cells in row
   */
  private int getRowSize(Row row) {
    int size = 0;
    for (int i = row.getLastCellNum(); i >= 0; i--) {
      Cell cell = row.getCell(i - 1);
      if (!ExcelImportUtilities.isEmpty(cell)) {
        size = i;
        break;
      }
    }

    return size;
  }

  protected String getCellRow(Map<String, Cell> buildingBlockRowMap) {
    for (Cell cell : buildingBlockRowMap.values()) {
      if (cell != null) {
        return ExcelImportUtilities.getCellRow(cell);
      }
    }
    return "undef";
  }

  protected String getCellByKey(String key, Map<String, Cell> buildingBlockRowMap) {
    Cell cell = buildingBlockRowMap.get(key);

    return ExcelImportUtilities.contentAsString(cell, getProcessingLog());
  }

  protected String getCellByKeyOrNull(String key, Map<String, Cell> buildingBlockRowMap) {
    Cell cell = buildingBlockRowMap.get(key);

    if (cell == null) {
      return null;
    }
    else {
      return ExcelImportUtilities.contentAsString(cell, getProcessingLog());
    }
  }

  protected Integer getId(Map<String, Cell> buildingBlockRowMap) {
    // Support both the format "11.0" and "11"
    // Integer.valueOf() can't handle decimal places, so trim it
    String id = ExcelImportUtilities.contentAsString(buildingBlockRowMap.get(getConstant("global.id")), getProcessingLog());

    // Trim decimal place, if there is one
    if (id.indexOf('.') >= 0) {
      id = id.substring(0, id.indexOf('.'));
    }

    try {
      return Integer.valueOf(id);
    } catch (NumberFormatException e) {
      LOGGER.debug("Could not understand Id " + id + ". Setting to null...");
      return null;
    }
  }

  protected String getName(Map<String, Cell> buildingBlockRowMap) {
    // Name's header title is not necessarily equal to the sheet name,
    // so we have to explicitly look up the building block type name
    String columnName = getConstant(typeOfBuildingBlockOnSheet.getPluralValue());
    return getCellByKey(columnName, buildingBlockRowMap);
  }

  protected String getDescription(Map<String, Cell> buildingBlockRowMap) {
    String descKey = getConstant(Constants.ATTRIBUTE_DESCRIPTION);
    Cell cell = buildingBlockRowMap.get(descKey);
    if (cell == null) {
      return null;
    }
    return ExcelImportUtilities.contentAsString(cell, getProcessingLog());
  }

  protected Cell getDescriptionCell(Map<String, Cell> buildingBlockRowMap) {
    String descKey = getConstant(Constants.ATTRIBUTE_DESCRIPTION);
    return buildingBlockRowMap.get(descKey);
  }

  /**
   * Creates a new RuntimePeriod from cell data.
   * @param startDateCell container with start date
   * @param endDateCell container with end date
   * @param elementName Name of the building block for which the dates are parsed. Only used for logging purposes.
   * @param previousRuntimePeriod default values, if a cell is null
   * @return new RuntimePeriod
   */
  protected RuntimePeriod getRuntimePeriod(CellValueHolder startDateCell, CellValueHolder endDateCell, String elementName,
                                           RuntimePeriod previousRuntimePeriod) {
    Date start;
    if (startDateCell.getOriginCell() == null) {
      start = previousRuntimePeriod == null ? null : previousRuntimePeriod.getStart();
    }
    else {
      start = ExcelImportUtilities.getDate(startDateCell, elementName);
    }
    Date end;
    if (endDateCell.getOriginCell() == null) {
      end = previousRuntimePeriod == null ? null : previousRuntimePeriod.getEnd();
    }
    else {
      end = ExcelImportUtilities.getDate(endDateCell, elementName);
    }

    if (!isValidRuntimePeriod(start, end)) {
      getProcessingLog().warn("Invalid timespan, end date before start date: {0} --> {1} - {2}, cells [{3}] and [{4}]", elementName, start, end,
          ExcelImportUtilities.getCellRef(startDateCell.getOriginCell()), ExcelImportUtilities.getCellRef(endDateCell.getOriginCell()));
      return null;
    }

    LOGGER.debug("Range " + start + " - " + end);
    return new RuntimePeriod(start, end);
  }

  public static boolean isValidRuntimePeriod(Date start, Date end) {
    boolean result = true;
    if (start != null && end != null && end.before(start)) {
      result = false;
    }
    return result;
  }

  protected ProcessingLog getProcessingLog() {
    return processingLog;
  }

  /**
   * Extracts the date.
   * 
   * @param dateCell the cell containing date
   * @param valueHolder the cell value holder
   */
  protected Date extractDate(Cell dateCell, CellValueHolder valueHolder) {
    if (dateCell != null) {
      try {
        return ExcelImportUtilities.contentAsDate(dateCell);
      } catch (Exception e) {
        LOGGER.warn("The date could not be parsed", e);
        valueHolder.addProblem(ProblemMarker.ERROR, "The date could not be parsed.");
      }
    }

    return null;
  }

  public TypeOfBuildingBlock getTypeOfBuildingBlockOnSheet() {
    return typeOfBuildingBlockOnSheet;
  }
}

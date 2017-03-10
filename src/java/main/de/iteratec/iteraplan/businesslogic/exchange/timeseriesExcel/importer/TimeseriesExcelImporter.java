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
package de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.importer;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.joda.time.LocalDate;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.AbstractIntroSheetGenerator;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.TimeseriesService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.Timeseries;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;
import de.iteratec.iteraplan.model.attribute.TimeseriesType;


/**
 * Imports Timeseries data from an Excel file into the database.
 */
public class TimeseriesExcelImporter {
  private static final Logger         LOGGER            = Logger.getIteraplanLogger(TimeseriesExcelImporter.class);

  public static final int             FIRST_DATA_ROW_NO = 6;
  public static final int             BB_COL_NO         = 0;
  public static final int             DATE_COL_NO       = 1;
  public static final int             VALUE_COL_NO      = 2;
  public static final CellReference   BB_TYPE_CELL_REF  = new CellReference("A3");
  public static final CellReference   AT_CELL_REF       = new CellReference("A4");
  private static final String         BB_PACKAGE_PREFIX = "de.iteratec.iteraplan.model.";

  private AttributeTypeService        atService;
  private BuildingBlockServiceLocator bbServiceLocator;
  private TimeseriesService           timeseriesService;

  private final LocalDate             now;
  private final List<String>          errorMessages     = Lists.newArrayList();

  /**
   * Creates an ExcelTimeseriesExporter for the given workbook.
   */
  public TimeseriesExcelImporter(AttributeTypeService atService, BuildingBlockServiceLocator bbServiceLocator, TimeseriesService timeseriesService) {
    this.atService = atService;
    this.bbServiceLocator = bbServiceLocator;
    this.timeseriesService = timeseriesService;
    this.now = LocalDate.now();
  }

  public List<String> getErrorMessages() {
    return errorMessages;
  }

  /**
   * Imports data from the workbook into the database.
   * @return True if the import was successful, false otherwise.
   */
  public boolean importExcel(Workbook workbook) {
    assert (workbook != null);
    for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
      Sheet sheet = workbook.getSheetAt(sheetIndex);
      if (!AbstractIntroSheetGenerator.INTRO_SHEET_NAME.equals(sheet.getSheetName())) {
        importTimeseriesSheet(sheet);
      }
    }
    return errorMessages.isEmpty();
  }

  private void importTimeseriesSheet(Sheet timeseriesSheet) {
    TypeOfBuildingBlock tobb = getTypeOfBuildingBlockFromSheet(timeseriesSheet);
    AttributeType at = getAttributeTypeFromSheet(timeseriesSheet);

    if (tobb == null || at == null) {
      // something went wrong reading the type of building block or the attribute type
      // => don't import the sheet. More specific errors were already added during the according read-attempts.
      return;
    }

    BuildingBlockService<BuildingBlock, Integer> service = bbServiceLocator.getService(tobb);

    boolean isNumberAt = at instanceof NumberAT;

    Multimap<BuildingBlock, TimeseriesEntry> entriesMap = HashMultimap.create();
    boolean hasMore = true;
    for (int rowNum = FIRST_DATA_ROW_NO; hasMore; rowNum++) {
      Row row = timeseriesSheet.getRow(rowNum);
      hasMore = readRow(row, entriesMap, service, isNumberAt);
    }

    importTimeseriesEntries(entriesMap, at);
  }

  private TypeOfBuildingBlock getTypeOfBuildingBlockFromSheet(Sheet timeseriesSheet) {
    CellReference ref = ExcelUtils.getFullCellReference(timeseriesSheet, BB_TYPE_CELL_REF);

    Row row = timeseriesSheet.getRow(ref.getRow());
    if (row == null) {
      logError(ref, "No Building Block Type name found.");
      return null;
    }
    Cell bbtCell = row.getCell(ref.getCol());
    if (ExcelUtils.isEmptyCell(bbtCell)) {
      logError(ref, "No Building Block Type name found.");
      return null;
    }

    String buildingBlockName = StringUtils.trim(ExcelUtils.getStringCellValue(bbtCell));
    Class<?> buildingBlockClass = null;
    try {
      buildingBlockClass = Class.forName(BB_PACKAGE_PREFIX + buildingBlockName);
    } catch (ClassNotFoundException e) {
      logError(e, ref, "\"{0}\" is not a valid building block class name.", buildingBlockName);
      return null;
    }

    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.typeOfBuildingBlockForClass(buildingBlockClass);
    if (tobb == null) {
      logError(ref, "No Building Block Type for Class \"{0}\" found.", buildingBlockClass);
    }
    return tobb;
  }

  private AttributeType getAttributeTypeFromSheet(Sheet timeseriesSheet) {
    CellReference ref = ExcelUtils.getFullCellReference(timeseriesSheet, AT_CELL_REF);

    Row row = timeseriesSheet.getRow(ref.getRow());
    if (row == null) {
      logError(ref, "No Attribute Type name found.");
      return null;
    }

    Cell atCell = row.getCell(ref.getCol());
    if (ExcelUtils.isEmptyCell(atCell)) {
      logError(ref, "No Attribute Type name found.");
      return null;
    }

    String attributeName = StringUtils.trim(ExcelUtils.getStringCellValue(atCell));
    AttributeType at = atService.getAttributeTypeByName(attributeName);

    if (at == null) {
      logError(ref, "No attribute type with name \"{0}\" found.", attributeName);
      return null;
    }

    if (!(at instanceof TimeseriesType && ((TimeseriesType) at).isTimeseries())) {
      logError(ref, "Attribute \"{0}\" is not a timeseries attribute.", at);
    }
    return at;
  }

  private boolean readRow(Row row, Multimap<BuildingBlock, TimeseriesEntry> entriesMap, BuildingBlockService<BuildingBlock, Integer> bbService,
                          boolean isNumberAt) {
    if (!containsData(row)) {
      return false; // assume there is no more data if the current row is empty
    }

    BuildingBlock bb = getBuildingBlock(row, bbService);
    Date date = getDate(row);
    String value = getValue(row, isNumberAt);

    if (isRowValid(bb, date, value)) {
      entriesMap.put(bb, new TimeseriesEntry(date, value));
    }

    return true; // even if the current row is not valid, read the next rows if they contain data
  }

  private boolean containsData(Row row) {
    if (row != null) {
      for (int i = 0; i < 3; i++) {
        if (!ExcelUtils.isEmptyCell(row.getCell(i))) {
          return true;
        }
      }
    }
    return false;
  }

  private BuildingBlock getBuildingBlock(Row row, BuildingBlockService<BuildingBlock, Integer> bbService) {
    Cell cell = row.getCell(BB_COL_NO);

    if (ExcelUtils.isEmptyCell(cell)) {
      logError(ExcelUtils.getFullCellReference(row.getSheet(), new CellReference(row.getRowNum(), BB_COL_NO)), "No Building Block found.");
      return null;
    }

    String bbName = StringUtils.trim(ExcelUtils.getStringCellValue(cell));
    List<BuildingBlock> bbList = bbService.findByNames(Collections.singleton(bbName));

    if (bbList == null || bbList.isEmpty()) {
      logError(ExcelUtils.getFullCellReference(cell), "No Building Block named \"{0}\" found.", bbName);
      return null;
    }

    if (bbList.size() > 1) {
      LOGGER.error("More than one Building Block named \"{0}\" found.", bbName);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }

    return bbList.get(0);
  }

  private Date getDate(Row row) {
    Cell cell = row.getCell(DATE_COL_NO);

    if (ExcelUtils.isEmptyCell(cell)) {
      logError(ExcelUtils.getFullCellReference(row.getSheet(), new CellReference(row.getRowNum(), DATE_COL_NO)), "No date value found.");
      return null;
    }

    Object dateCellValue = ExcelUtils.getCellValue(cell, true);
    if (!(dateCellValue instanceof Date)) {
      logError(ExcelUtils.getFullCellReference(cell), "No date value found.");
      return null;
    }
    else {
      Date date = (Date) dateCellValue;
      if (now.isBefore(new LocalDate(date))) {
        logError(ExcelUtils.getFullCellReference(cell), "Date is after today. Only past dates or the present day are allowed for timeseries.");
        return null;
      }
      else {
        return date;
      }
    }
  }

  private String getValue(Row row, boolean isNumberAt) {
    Cell valueCell = row.getCell(VALUE_COL_NO);
    if (ExcelUtils.isEmptyCell(valueCell)) {
      logError(ExcelUtils.getFullCellReference(row.getSheet(), new CellReference(row.getRowNum(), VALUE_COL_NO)), "No attribute value found.");
      return null;
    }

    if (isNumberAt) {
      Object value = ExcelUtils.getCellValue(valueCell, false);
      if (!(value instanceof Double)) {
        logError(ExcelUtils.getFullCellReference(valueCell), "Non-number value for number attribute type found.");
        return null;
      }
    }

    return ExcelUtils.getStringCellValue(valueCell);
  }

  private boolean isRowValid(BuildingBlock bb, Date date, String value) {
    return bb != null && date != null && !StringUtils.isEmpty(value);
  }

  private void importTimeseriesEntries(Multimap<BuildingBlock, TimeseriesEntry> entriesMap, AttributeType at) {
    for (BuildingBlock bb : entriesMap.keySet()) {
      Timeseries timeseries = timeseriesService.loadTimeseriesByBuildingBlockAndAttributeType(bb, at);
      if (timeseries == null) {
        timeseries = new Timeseries();
        timeseries.setBuildingBlock(bb);
        timeseries.setAttribute(at);
      }
      for (TimeseriesEntry entry : entriesMap.get(bb)) {
        timeseries.addEntry(entry);
      }
      timeseriesService.saveOrUpdateWithBbUpdate(timeseries);
    }
  }

  private void logError(CellReference ref, String baseMessage, Object... params) {
    logError(null, ref, baseMessage, params);
  }

  private void logError(Exception e, CellReference ref, String baseMessage, Object... params) {
    String message = MessageFormat.format(baseMessage, params);
    if (ref != null) {
      message = "Cell '" + ExcelUtils.getCellRefName(ref) + "': " + message;
    }
    errorMessages.add(message);
    if (e == null) {
      LOGGER.error(message);
    }
    else {
      LOGGER.error(message, e);
    }
  }
}

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
package de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.exporter;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.AbstractIntroSheetGenerator;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.IteraExcelStyle;
import de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.importer.TimeseriesExcelImporter;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.TimeseriesService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.Timeseries;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;


public class TimeseriesExcelExportServiceImpl implements TimeseriesExcelExportService {

  private static final Logger                    LOGGER         = Logger.getIteraplanLogger(TimeseriesExcelExportServiceImpl.class);

  /** 10 characters for date output + 1 to spare. Unit is 1/256th of character width, see {@link Sheet#setColumnWidth(int, int)} */
  private static final int                       DATE_COL_WIDTH = 256 * 11;

  private final TimeseriesExcelTemplateGenerator templateGenerator;

  private BuildingBlockServiceLocator            bbServiceLocator;
  private TimeseriesService                      timeseriesService;

  private Resource                               excel2003Template;
  private Resource                               excel2007Template;

  public TimeseriesExcelExportServiceImpl(Resource logoImage, AttributeTypeService atService) {
    this.templateGenerator = new TimeseriesExcelTemplateGenerator(logoImage, atService);
  }

  public void setExcel2003Template(Resource excel2003Template) {
    this.excel2003Template = excel2003Template;
  }

  public void setExcel2007Template(Resource excel2007Template) {
    this.excel2007Template = excel2007Template;
  }

  public void setBbServiceLocator(BuildingBlockServiceLocator bbServiceLocator) {
    this.bbServiceLocator = bbServiceLocator;
  }

  public void setTimeseriesService(TimeseriesService timeseriesService) {
    this.timeseriesService = timeseriesService;
  }

  /**{@inheritDoc}**/
  public Workbook generateTemplateExcel2003() {
    LOGGER.debug("Starting Excel 2003 Timeseries Template generation.");
    TimeseriesWorkbookContext template = templateGenerator.generateTimeseriesTemplate(excel2003Template);
    adjustColumnWidths(template.getWorkbook());
    return template.getWorkbook();
  }

  /**{@inheritDoc}**/
  public Workbook generateTemplateExcel2007() {
    LOGGER.debug("Starting Excel 2007 Timeseries Template generation.");
    TimeseriesWorkbookContext template = templateGenerator.generateTimeseriesTemplate(excel2007Template);
    adjustColumnWidths(template.getWorkbook());
    return template.getWorkbook();
  }

  /**{@inheritDoc}**/
  public Workbook exportTimeseriesDataExcel2003() {
    LOGGER.debug("Starting Excel 2003 Timeseries data export.");
    TimeseriesWorkbookContext template = templateGenerator.generateTimeseriesTemplate(excel2003Template);
    exportTimeseriesDataToWorkbook(template);
    return template.getWorkbook();
  }

  /**{@inheritDoc}**/
  public Workbook exportTimeseriesDataExcel2007() {
    LOGGER.debug("Starting Excel 2007 Timeseries data export.");
    TimeseriesWorkbookContext template = templateGenerator.generateTimeseriesTemplate(excel2007Template);
    exportTimeseriesDataToWorkbook(template);
    return template.getWorkbook();
  }

  /**
   * Writes timeseries data for attributes and building blocks of the types given in
   * the {@link TimeseriesWorkbookContext}'s sheetContexts to the according sheets. 
   * @param wbContext
   *          workbook context containing the workbook to export to and the {@link TimeseriesSheetContext}s
   *          with building block types and attributes to export
   */
  private void exportTimeseriesDataToWorkbook(TimeseriesWorkbookContext wbContext) {
    LOGGER.debug("Starting Timeseries data export.");

    for (int i = 0; i < wbContext.getWorkbook().getNumberOfSheets(); i++) {
      String sheetName = wbContext.getWorkbook().getSheetName(i);
      TimeseriesSheetContext sheetContext = wbContext.getSheetContexts().get(sheetName);

      if (sheetContext != null) {
        exportDataToSheet(sheetContext, wbContext.getCellStyles());
      }
    }
    adjustColumnWidths(wbContext.getWorkbook());
  }

  private void exportDataToSheet(TimeseriesSheetContext sheetContext, Map<IteraExcelStyle, CellStyle> cellStyles) {
    BuildingBlockService<BuildingBlock, Integer> bbService = bbServiceLocator.getService(sheetContext.getTobb());

    CellStyle dataStyle = cellStyles.get(IteraExcelStyle.DATA);
    CellStyle dateStyle = cellStyles.get(IteraExcelStyle.DATA_DATE);

    boolean isNumber = false;
    if (sheetContext.getAt() instanceof NumberAT) {
      isNumber = true;
    }

    List<BuildingBlock> bbList = bbService.loadElementList();
    int currentRowNum = TimeseriesExcelImporter.FIRST_DATA_ROW_NO;
    for (BuildingBlock bb : bbList) {
      Timeseries timeseries = timeseriesService.loadTimeseriesByBuildingBlockAndAttributeType(bb, sheetContext.getAt());
      if (timeseries != null) {
        for (TimeseriesEntry entry : timeseries.getEntries()) {
          Row row = sheetContext.getSheet().createRow(currentRowNum++);

          Cell bbCell = row.createCell(TimeseriesExcelImporter.BB_COL_NO);
          bbCell.setCellValue(bb.getNonHierarchicalName());
          bbCell.setCellStyle(dataStyle);

          Cell dateCell = row.createCell(TimeseriesExcelImporter.DATE_COL_NO);
          dateCell.setCellValue(entry.getDate());
          dateCell.setCellStyle(dateStyle);

          Cell valueCell = row.createCell(TimeseriesExcelImporter.VALUE_COL_NO);
          if (isNumber) {
            valueCell.setCellValue(Double.parseDouble(entry.getValue()));
          }
          else {
            valueCell.setCellValue(entry.getValue());
          }
          valueCell.setCellStyle(dataStyle);
        }
      }
    }
  }

  private void adjustColumnWidths(Workbook workbook) {
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
      Sheet sheet = workbook.getSheetAt(i);
      if (!AbstractIntroSheetGenerator.INTRO_SHEET_NAME.equals(sheet.getSheetName())) {
        sheet.autoSizeColumn(TimeseriesExcelImporter.BB_COL_NO, false);
        sheet.setColumnWidth(TimeseriesExcelImporter.DATE_COL_NO, DATE_COL_WIDTH);
        sheet.autoSizeColumn(TimeseriesExcelImporter.VALUE_COL_NO, false);
      }
    }
  }

}

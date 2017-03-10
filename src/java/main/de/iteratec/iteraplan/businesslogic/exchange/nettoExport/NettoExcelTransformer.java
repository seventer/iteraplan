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
package de.iteratec.iteraplan.businesslogic.exchange.nettoExport;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.ExcelStylesCreator;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.IteraExcelStyle;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * {@link NettoTransformer} for Excel 2003 and 2007 formats.
 */
public final class NettoExcelTransformer extends AbstractNettoTransformer {

  public static enum ExcelVersion {
    EXCEL_VERSION_2003, EXCEL_VERSION_2007
  }

  private static final int MAX_COLUM_CHAR_WIDTH = 50;

  private ExcelVersion     excelVersion;
  private TableStructure   tableStructure;

  private NettoExcelTransformer() {
    // use static factory
  }

  /**
   * Get new {@link NettoExcelTransformer} instance.
   * @param table  The {@link TableStructure} from the GUI.
   * @param version  The {@link ExcelVersion}.
   * @return  The new instance of {@link NettoExcelTransformer} 
   */
  public static NettoTransformer newInstance(TableStructure table, ExcelVersion version) {
    NettoExcelTransformer excelTransformer = new NettoExcelTransformer();
    excelTransformer.tableStructure = table;
    excelTransformer.excelVersion = version;
    return excelTransformer;
  }

  /**{@inheritDoc}**/
  @Override
  public void transform(List<?> sourceList, OutputStream out, TypeOfBuildingBlock typeOfBuildingBlock) {

    ColumnStructure[] columns = tableStructure.getColumns();

    try {
      Workbook workbook = createWorkbook();

      Sheet sheet = workbook.createSheet();
      configSheetName(sheet, typeOfBuildingBlock);

      Map<IteraExcelStyle, CellStyle> createdStyles = ExcelStylesCreator.createStyles(workbook);
      CellStyle headerStyle = createdStyles.get(IteraExcelStyle.HEADER);
      CellStyle dataStyle = createdStyles.get(IteraExcelStyle.DATA);
      CellStyle dataDateStyle = createdStyles.get(IteraExcelStyle.DATA_DATE);

      // Create cell style for numbers
      CellStyle numCellStyle = workbook.createCellStyle();
      numCellStyle.cloneStyleFrom(dataStyle);
      short numFormatIndex = workbook.createDataFormat().getFormat("0.00");
      numCellStyle.setDataFormat(numFormatIndex);

      Row headerRow = sheet.createRow(0);

      int nextCol = 0;
      for (ColumnStructure columnStructure : columns) {
        Cell headerCell = headerRow.createCell(nextCol);
        headerCell.setCellValue(columnStructure.getColumnHeader());
        headerCell.setCellStyle(headerStyle);
        nextCol++;
      }

      int nextRow = 1;
      for (Object obj : sourceList) {
        if (obj instanceof BuildingBlock) {
          BuildingBlock bb = (BuildingBlock) obj;

          // skip virutal root element
          if (bb instanceof AbstractHierarchicalEntity<?>) {
            AbstractHierarchicalEntity<?> hierarchicalEntity = (AbstractHierarchicalEntity<?>) bb;
            if (hierarchicalEntity.isTopLevelElement()) {
              continue;
            }
          }

          Row row = sheet.createRow(nextRow);

          nextCol = 0;
          for (ColumnStructure columnStructure : columns) {
            Cell cell = row.createCell(nextCol);

            Object resolvedValue = columnStructure.resolveValue(bb);

            if (resolvedValue instanceof Date) {
              cell.setCellStyle(dataDateStyle);
              cell.setCellValue((Date) resolvedValue);
            }
            else if (resolvedValue instanceof Number) {
              cell.setCellStyle(numCellStyle);
              double doubleValue = ((Number) resolvedValue).doubleValue();
              cell.setCellValue(doubleValue);
            }
            else {
              cell.setCellStyle(dataStyle);
              cell.setCellValue(String.valueOf(resolvedValue));
            }

            ++nextCol;
          }

          ++nextRow;
        }
      }

      // auto format
      nextCol = 0;
      for (int col = 0; col < columns.length; col++) {
        sheet.autoSizeColumn(col);
        int columnCharWidth = sheet.getColumnWidth(col) / 256;
        if (columnCharWidth > MAX_COLUM_CHAR_WIDTH) {
          sheet.setColumnWidth(col, MAX_COLUM_CHAR_WIDTH * 256);
        }
      }

      workbook.write(out);
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the POI {@link Workbook} depending on the {@link ExcelVersion}.
   * @return  A POI {@link Workbook}
   */
  private Workbook createWorkbook() {
    switch (excelVersion) {
      case EXCEL_VERSION_2003:
        return new HSSFWorkbook();

      case EXCEL_VERSION_2007:
        return new XSSFWorkbook();

      default:
        return new XSSFWorkbook();
    }
  }
}

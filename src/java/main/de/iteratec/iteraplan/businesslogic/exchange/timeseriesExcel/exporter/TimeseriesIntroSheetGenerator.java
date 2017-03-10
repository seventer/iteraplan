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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.Resource;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.AbstractIntroSheetGenerator;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.IteraExcelStyle;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.common.MessageAccess;


public class TimeseriesIntroSheetGenerator extends AbstractIntroSheetGenerator {

  private final TimeseriesWorkbookContext wbContext;

  public TimeseriesIntroSheetGenerator(TimeseriesWorkbookContext wbContext, Resource logoImage) {
    super(logoImage);
    this.wbContext = wbContext;
  }

  @Override
  protected Workbook getWorkbook() {
    return wbContext.getWorkbook();
  }

  @Override
  protected void createSummary() {
    Sheet introSheet = getIntroductionSheet();

    int rowNum = SUMMARY_ROW;
    Cell headerCell = introSheet.createRow(rowNum++).createCell(SUMMARY_COL);
    headerCell.setCellValue(MessageAccess.getStringOrNull("excel.export.timeseries.intro.description"));
    headerCell.setCellStyle(wbContext.getCellStyles().get(IteraExcelStyle.HEADER));
    introSheet.addMergedRegion(new CellRangeAddress(headerCell.getRowIndex(), headerCell.getRowIndex(), headerCell.getColumnIndex(), headerCell
        .getColumnIndex() + 1));

    CreationHelper createHelper = getWorkbook().getCreationHelper();

    // TODO group the summary by building block type with subheaders
    for (int i = 0; i < getWorkbook().getNumberOfSheets(); i++) {
      Sheet sheet = getWorkbook().getSheetAt(i);
      String sheetName = sheet.getSheetName();
      if (!introSheet.equals(sheet)) {
        Row summaryRow = introSheet.createRow(rowNum++);
        Cell hyperlinkCell = summaryRow.createCell(SUMMARY_COL);
        hyperlinkCell.setCellValue(sheetName);
        summaryRow.createCell(SUMMARY_COL + 1).setCellValue(ExcelUtils.getStringCellValue(sheet.getRow(0).getCell(0)));

        Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
        link.setAddress("'" + sheetName + "'!A1");
        hyperlinkCell.setHyperlink(link);
        hyperlinkCell.setCellStyle(wbContext.getCellStyles().get(IteraExcelStyle.HYPERLINK));
      }
    }

    adjustSheetColumnWidths();
  }
}

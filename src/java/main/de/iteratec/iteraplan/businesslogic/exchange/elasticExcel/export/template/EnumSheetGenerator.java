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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.WorkbookContext;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;


/**
 * Generates the new Excel-Sheet for the specified {@link EnumerationExpression}.
 */
public class EnumSheetGenerator extends AbstractSheetGenerator {

  /** Logger. */
  private static final Logger LOGGER = Logger.getIteraplanLogger(EnumSheetGenerator.class);

  /**
   * @param wbContext the instance of the workbook context
   * @param typeExpression the relationship to generate the sheet for
   */
  public EnumSheetGenerator(WorkbookContext wbContext, EnumerationExpression typeExpression) {
    super(wbContext, typeExpression);
  }

  /** {@inheritDoc} */
  @Override
  protected EnumerationExpression getTypeExpression() {
    return (EnumerationExpression) typeExpression;
  }

  // ===== Header Area =====

  /** add the header cells for each feature, i.e. for each column */
  protected void createFeatureHeaders() {
    addColumn("persistentName");
    addColumn("name");
    addColumn("description");
    addColumn("abbreviation");
  }

  private void addColumn(String colName) {
    int column = sheetContext.getColumnCount();
    Cell cell = headerRow.createCell(column);
    cell.setCellValue(colName);
    cell.setCellStyle(wbContext.getStyles().get(IteraExcelStyle.HEADER));

    sheetContext.addColumn(null, cell);
  }

  // ===== Data Area =====

  /**{@inheritDoc}**/
  @Override
  protected void formatDataArea() {

    int rowNum = FIRST_DATA_ROW_NO;
    Sheet sheet = sheetContext.getSheet();

    for (EnumerationLiteralExpression ele : getTypeExpression().getLiterals()) {
      Row row = sheet.createRow(rowNum++);
      row.createCell(0).setCellValue(ele.getPersistentName());
      row.createCell(1).setCellValue(ele.getName());
      row.createCell(2).setCellValue(ele.getDescription());
      row.createCell(3).setCellValue(ele.getAbbreviation());

      LOGGER.info("ELE: {0}/{1}/{2}/{3}", ele.getPersistentName(), ele.getName(), ele.getDescription(), ele.getAbbreviation());
    }
  }

  public void addDropdowns() {
    // nothing to do on enum sheets
  }

  // ===== Helpers =====

  @Override
  protected String createCompleteSheetName() {
    String enumStd = "de.iteratec.iteraplan.model.attribute.EnumAT.";
    String enumStd2 = "de.iteratec.iteraplan.model.";

    String name = getTypeExpression().getPersistentName();

    if (name.startsWith(enumStd)) {
      name = name.substring(enumStd.length());
    }
    else if (name.startsWith(enumStd2)) {
      name = name.substring(enumStd2.length());
    }

    LOGGER.info("Unprocessed enum sheet name: " + name);
    return name;
  }
}

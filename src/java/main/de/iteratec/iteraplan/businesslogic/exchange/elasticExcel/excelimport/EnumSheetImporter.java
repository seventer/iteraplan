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

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.SheetContext;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;


/**
 * Import {@link EnumerationExpression}s and add them to the <b>meta model</b>.
 */
public class EnumSheetImporter {

  /** Logger. */
  private static final Logger LOGGER = Logger.getIteraplanLogger(EnumSheetImporter.class);

  private EditableMetamodel   metamodel;

  /**
   * Constructor.
   * 
   * @param metamodel metamodel to set up. This metamodel will be changed directly.
   */
  /* package */EnumSheetImporter(EditableMetamodel metamodel) {
    this.metamodel = metamodel;
  }

  /**
   * import the EnumerationExpression. Read first column and create the literals.
   * 
   * @param sheetContext
   */
  /* package*/void importEnumerationExpression(SheetContext sheetContext) {
    Sheet sheet = sheetContext.getSheet();
    EnumerationExpression enumExpression = (EnumerationExpression) sheetContext.getExpression();

    LOGGER.info("importing EnumerationExpression: {0}, {1}", enumExpression.getPersistentName(), enumExpression.getMetaType().getName());

    for (int rowNum = FIRST_DATA_ROW_NO; true; rowNum++) {
      String literalName = getCellString(sheet, rowNum, 0);
      if (StringUtils.isEmpty(literalName)) {
        break;
      }

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("    creating literal: {0}#{1}", enumExpression.getPersistentName(), literalName);
      }

      EnumerationLiteralExpression literal = metamodel.createEnumerationLiteral(enumExpression, literalName, null);
      literal.setName(getCellString(sheet, rowNum, 1));
      literal.setDescription(getCellString(sheet, rowNum, 2));
      literal.setAbbreviation(getCellString(sheet, rowNum, 3));
    }
  }

  /**
   * read the string from the given sheet/row/column
   * 
   * @param sheet
   * @param rowNum
   * @param colNum
   * @return the cell value as String, or null if row or cell are empty, or if cell content is not a String value.
   */
  private String getCellString(Sheet sheet, int rowNum, int colNum) {
    Row row = sheet.getRow(rowNum);
    if (row == null) {
      return null;
    }

    Cell cell = row.getCell(colNum);
    if (cell == null) {
      return null;
    }

    String stringCellValue = ExcelUtils.getStringCellValue(cell);
    if (StringUtils.isEmpty(stringCellValue)) {
      return null;
    }

    return stringCellValue;
  }
}

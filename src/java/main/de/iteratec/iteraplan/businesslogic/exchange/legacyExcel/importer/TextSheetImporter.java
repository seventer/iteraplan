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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.MessageAccess;


/**
 * Concrete sheet importer for text attributes.
 * @author sip
 */
public class TextSheetImporter extends AttributeSheetImporter<TextAttData> {

  /**
   * @param locale the current locale specified in Excel configuration sheet
   * @param processingLog the processing log for saving import messages
   */
  public TextSheetImporter(Locale locale, ProcessingLog processingLog) {
    super(locale, processingLog);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void readContentFromSheet(List<Row> sheetContentRows, Map<String, Integer> headline, AttributeData attributeData) {
    String nameColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.NAME_COLUMN_KEY, getLocale());
    String oldNameColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.OLDNAME_COLUMN_KEY, getLocale());
    String descriptionColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.DESCRIPTION_COLUMN_KEY, getLocale());
    String groupColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.GROUP_COLUMN_KEY, getLocale());
    String mandatoryColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.MANDATORY_COLUMN_KEY, getLocale());
    String multilineColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.MULTILINE_COLUMN_KEY, getLocale());
    String activeforbbColumnName = MessageAccess.getStringOrNull(AttributeWorkbook.ACTIVEFORBB_COLUMN_KEY, getLocale());

    List<TextAttData> result = Lists.newArrayList();
    int currentRow = 0;
    while (currentRow < sheetContentRows.size()) {
      Row initialRow = sheetContentRows.get(currentRow);
      Map<String, Cell> initialRowContent = ExcelImportUtilities.readRow(initialRow, headline);
      Cell nameCell = initialRowContent.get(nameColumnName);
      CellValueHolder name = new CellValueHolder(nameCell);
      if (StringUtils.isBlank(name.getAttributeValue())) {
        getProcessingLog().error("Skipping attribute without name in cell {0}.", ExcelImportUtilities.getCellRef(nameCell));
        currentRow++;
        continue;
      }

      CellValueHolder oldName = new CellValueHolder(initialRowContent.get(oldNameColumnName));
      CellValueHolder description = new CellValueHolder(initialRowContent.get(descriptionColumnName));
      CellValueHolder groupstr = new CellValueHolder(initialRowContent.get(groupColumnName));
      CellValueHolder mandatory = new CellValueHolder(initialRowContent.get(mandatoryColumnName));
      CellValueHolder multiline = new CellValueHolder(initialRowContent.get(multilineColumnName));
      CellValueHolder activeforbb = new CellValueHolder(initialRowContent.get(activeforbbColumnName));

      if(!isBooleanValue(mandatory)) {
        getProcessingLog().warn("[{0}] Mandatory value for attribute {1} doesn\'t match yes/no format.", mandatory.getCellRef(), name.getAttributeValue());
      }
      
      if(!isBooleanValue(multiline)) {
        getProcessingLog().warn("[{0}] Mandatory value for attribute {1} doesn\'t match yes/no format.", multiline.getCellRef(), name.getAttributeValue());
      }
      
      currentRow++;

      result.add(new TextAttData(name, oldName, description, groupstr, mandatory, activeforbb, multiline));
    }

    attributeData.setTextData(result);
  }

}

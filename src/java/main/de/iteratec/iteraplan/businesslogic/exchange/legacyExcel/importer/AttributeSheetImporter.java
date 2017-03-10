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
import org.apache.poi.ss.usermodel.Row;

import de.iteratec.iteraplan.common.MessageAccess;


/**
 * This is the base class for more concrete {@link AttributeSheetImporter}. There should be one importer for all attribute types.
 * @author sip
 * @param <AD> The Attribute Data to import
 */
public abstract class AttributeSheetImporter<AD extends AttData> {

  private final Locale  locale;
  private ProcessingLog processingLog;

  /**
   * @param locale the current locale specified in Excel configuration sheet
   * @param processingLog the processing log for saving import messages
   */
  public AttributeSheetImporter(Locale locale, ProcessingLog processingLog) {
    this.locale = locale;
    this.processingLog = processingLog;
  }

  public ProcessingLog getProcessingLog() {
    return processingLog;
  }

  public Locale getLocale() {
    return locale;
  }

  /**
   * Checks if the value in the current cell holder is a boolean
   * @param cellValueHolder
   */
  protected boolean isBooleanValue(CellValueHolder cellValueHolder) {
    String yesString = MessageAccess.getStringOrNull("global.yes", getLocale());
    String noString  = MessageAccess.getStringOrNull("global.no", getLocale());
    
    if(StringUtils.equalsIgnoreCase(cellValueHolder.getAttributeValue(), yesString) || StringUtils.equalsIgnoreCase(cellValueHolder.getAttributeValue(), noString)) {
      return true;
    }
    
    return false;
  }
  /**
   * Read all data from the sheet, and create AttData Objects.
   * @param sheetContentRows
   * @param headline
   * @param attributeData Container for imported Attributes
   */
  public abstract void readContentFromSheet(List<Row> sheetContentRows, Map<String, Integer> headline, AttributeData attributeData);

}

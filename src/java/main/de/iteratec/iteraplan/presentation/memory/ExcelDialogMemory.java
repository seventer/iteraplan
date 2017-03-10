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
package de.iteratec.iteraplan.presentation.memory;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;


/**
 * ExcelDialogMemory is used when the View should output an Excel spreadsheet
 */
public abstract class ExcelDialogMemory extends DialogMemory {

  private ExportWorkbook workbook;
  private String         workbookFilename;

  /**
   * Sets the ExcelWorkbook that the View will send to the Browser
   * 
   * @param workbook
   */
  public void setExcelWorkbook(ExportWorkbook workbook) {
    this.workbook = workbook;
  }

  public ExportWorkbook getExcelWorkbook() {
    return this.workbook;
  }

  /**
   * Sets the name of the File that will be sent to the user. * e.g. "Report.xls"
   * 
   * @param filename
   *          the name of the Excel Report
   */
  public void setExcelWorkbookFilename(String filename) {
    this.workbookFilename = filename;
  }

  public String getExcelWorkbookFilename() {
    return this.workbookFilename;
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = prime * result + ((workbookFilename == null) ? 0 : workbookFilename.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ExcelDialogMemory other = (ExcelDialogMemory) obj;
    if (workbookFilename == null) {
      if (other.workbookFilename != null) {
        return false;
      }
    }
    else if (!workbookFilename.equals(other.workbookFilename)) {
      return false;
    }

    if (workbook != other.workbook) {
      return false;
    }

    return true;
  }

}

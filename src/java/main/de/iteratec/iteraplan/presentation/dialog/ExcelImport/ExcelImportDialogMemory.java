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
package de.iteratec.iteraplan.presentation.dialog.ExcelImport;

import de.iteratec.iteraplan.presentation.memory.DialogMemory;


/**
 * This class represent the bean for Excel Importer page(s).
 * 
 * @author ogr
 */
public class ExcelImportDialogMemory extends DialogMemory {
  private static final long serialVersionUID = -5713781776278111932L;

  private String            clickedButton;

  private boolean           wrongFileType;

  private boolean           wrongEnumFileType;

  private boolean           wrongUserFileType;

  private long              creationTime;

  private String            selectedImportType;

  private String            importLog;

  public ExcelImportDialogMemory() {
    super();
    this.creationTime = System.currentTimeMillis();
  }

  public String getSelectedImportType() {
    return selectedImportType;
  }

  public void setSelectedImportType(String selectedImportType) {
    this.selectedImportType = selectedImportType;
  }

  public String getImportLog() {
    return importLog;
  }

  /**
   * @return <code>false</code> if the current file has wrong type or is <code>null</code>.
   */
  public boolean isWrongFileType() {
    return wrongFileType;
  }

  public void setWrongFileType(boolean wrongFileType) {
    this.wrongFileType = wrongFileType;
  }

  public void setImportLog(String importLog) {
    this.importLog = importLog;
  }

  public String getClickedButton() {
    return clickedButton;
  }

  public void setClickedButton(String clickedButton) {
    this.clickedButton = clickedButton;
  }

  /**
   * @return the wrongEnumFileType
   */
  public boolean isWrongEnumFileType() {
    return wrongEnumFileType;
  }

  /**
   * @param wrongEnumFileType
   *          the wrongEnumFileType to set
   */
  public void setWrongEnumFileType(boolean wrongEnumFileType) {
    this.wrongEnumFileType = wrongEnumFileType;
  }

  /**
   * @return the wrongUserFileType
   */
  public boolean isWrongUserFileType() {
    return wrongUserFileType;
  }

  /**
   * @param wrongUserFileType
   *          the wrongUserFileType to set
   */
  public void setWrongUserFileType(boolean wrongUserFileType) {
    this.wrongUserFileType = wrongUserFileType;
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    // result = prime * result + ((zipArchive == null) ? 0 : zipArchive.hashCode());
    result = prime * result + Long.valueOf(creationTime).hashCode();
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
    final ExcelImportDialogMemory other = (ExcelImportDialogMemory) obj;
    if (this.hashCode() != other.hashCode()) {
      return false;
    }
    return true;
  }
}

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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.poi.ss.usermodel.Cell;


public class CellValueHolder {
  private String        attributeValue = null;
  private final Cell    originCell;
  private ProblemMarker problemMarker  = ProblemMarker.NONE;
  private StringBuilder problemComment = new StringBuilder();

  public CellValueHolder(Cell originCell) {
    this.originCell = originCell;
  }

  /**
   * Never returns null. Empty or NULL cells yield an empty string.
   * 
   * @return attributeValue
   */
  public String getAttributeValue() {
    if (attributeValue == null) {
      attributeValue = ExcelImportUtilities.contentAsString(originCell, ImportWorkbook.getProcessingLog());
    }
    return attributeValue;
  }

  public Cell getOriginCell() {
    return originCell;
  }
  
  public String getCellRef() {
    return ExcelImportUtilities.getCellRef(originCell);
  }

  public void addProblem(ProblemMarker marker, String currentProblemComment) {
    if (this.problemMarker.compareTo(marker) < 0) {
      // only change the marker if it becomes more severe
      this.problemMarker = marker;
    }
    if (this.problemComment.length() > 0) {
      this.problemComment.append('\n');
    }
    this.problemComment.append(currentProblemComment);
  }

  public ProblemMarker getProblemMarker() {
    return problemMarker;
  }

  public String getProblemComment() {
    return problemComment.toString();
  }

  @Override
  public String toString() {
    return getAttributeValue() + " from [" + getCellRef() + "]";
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(attributeValue).append(originCell).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    final CellValueHolder other = (CellValueHolder) obj;
    final EqualsBuilder equalsBuilder = new EqualsBuilder();
    equalsBuilder.append(attributeValue, other.attributeValue);
    equalsBuilder.append(originCell, other.originCell);

    return equalsBuilder.isEquals();
  }

}
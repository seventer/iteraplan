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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;

import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;


/**
 * Represents one Excel sheet column. Each column 
 * contains the reference to the associated type.
 */
public class ColumnContext {
  private final FeatureExpression<?> featureExpression;
  private final CellReference        headerCellReference;

  public ColumnContext(FeatureExpression<?> featureExpression, Cell cell) {
    this.featureExpression = featureExpression;
    this.headerCellReference = new CellReference(cell);
  }

  /**
   * Returns the associated type expression.
   * @return the type expression
   */
  public FeatureExpression<?> getFeatureExpression() {
    return featureExpression;
  }

  /**
   * Returns the header cell reference. For example:
   * <ul>
   * <li>A1 Cell reference without sheet 
   * <li>Sheet1!A1 Standard sheet name 
   * <li>'O''Brien''s Sales'!A1'  Sheet name with special characters 
   * </ul>
   * 
   * @return the header cell reference
   */
  public String getHeaderCellReference() {
    return headerCellReference.formatAsString();
  }

  /**
   * Returns the string representation of the column. For example "A", "B".
   * @return the string representation of the column
   */
  public String getColumnName() {
    return CellReference.convertNumToColString(headerCellReference.getCol());
  }

  /**
   * Returns the column number.
   * @return the column number
   */
  public int getColumnNumber() {
    return headerCellReference.getCol();
  }

  /**{@inheritDoc}**/
  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    builder.append(featureExpression.getPersistentName());
    builder.append(headerCellReference.formatAsString());

    return builder.toString();
  }
}

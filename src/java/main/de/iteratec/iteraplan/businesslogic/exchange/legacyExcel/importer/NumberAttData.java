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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * Represents imported entry for the number attribute.
 * @author sip
 */
public class NumberAttData extends AttData {
  /** Lower bound for Attribute Values. */
  private CellValueHolder   lowerBound;
  /** Upper bound for Attribute Values. */
  private CellValueHolder   upperBound;
  /** Unit. */
  private CellValueHolder   unit;
  /** Range uniform distributed. */
  private CellValueHolder   uniform;
  /** User defined ranges. */
  private CellValueHolder   userRanges;

  /**
   * @param name
   * @param oldName
   * @param description
   * @param groupName
   * @param mandatory
   * @param active
   * @param lowerBound
   * @param upperBound
   * @param unit
   * @param uniform
   * @param userRanges
   */
  public NumberAttData(CellValueHolder name, CellValueHolder oldName, CellValueHolder description, CellValueHolder groupName,
      CellValueHolder mandatory, CellValueHolder active, CellValueHolder lowerBound, CellValueHolder upperBound, CellValueHolder unit,
      CellValueHolder uniform, CellValueHolder userRanges) {
    super(name, oldName, description, groupName, mandatory, active);
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.unit = unit;
    this.uniform = uniform;
    this.userRanges = userRanges;
  }

  public CellValueHolder getLowerBound() {
    return lowerBound;
  }

  public void setLowerBound(CellValueHolder lowerBound) {
    this.lowerBound = lowerBound;
  }

  public CellValueHolder getUpperBound() {
    return upperBound;
  }

  public void setUpperBound(CellValueHolder upperBound) {
    this.upperBound = upperBound;
  }

  public CellValueHolder getUnit() {
    return unit;
  }

  public void setUnit(CellValueHolder unit) {
    this.unit = unit;
  }

  public CellValueHolder getUniform() {
    return uniform;
  }

  public void setUniform(CellValueHolder uniform) {
    this.uniform = uniform;
  }

  public CellValueHolder getUserRanges() {
    return userRanges;
  }

  public void setUserRanges(CellValueHolder userRanges) {
    this.userRanges = userRanges;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    toStringBuilder.append("name", getName().getAttributeValue());
    toStringBuilder.append("oldName", getOldName().getAttributeValue());
    toStringBuilder.append("description", getDescription().getAttributeValue());
    toStringBuilder.append("groupName", getGroupName().getAttributeValue());
    toStringBuilder.append("mandatory", getMandatory().getAttributeValue());
    toStringBuilder.append("active", getActive().getAttributeValue());
    toStringBuilder.append("lowerBound", getLowerBound().getAttributeValue());
    toStringBuilder.append("upperBound", getUpperBound().getAttributeValue());
    toStringBuilder.append("unit", getUnit().getAttributeValue());
    toStringBuilder.append("uniform", getUniform().getAttributeValue());
    toStringBuilder.append("userRanges", getUserRanges().getAttributeValue());
    return toStringBuilder.toString();
  }
}

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
package de.iteratec.iteraplan.elasticeam.operator.rangify;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.iteratec.iteraplan.elasticeam.exception.ModelException;


public class Range implements Comparable<Range> {

  /**
   * Represents the negative infinity bound of a range and is constrained by the
   * minimal value of the long data type. Attempting to create a range with a 
   * lower bound smaller that this number will cause an exception.
   */
  public static final BigDecimal NEGATIVE_INFINITY = new BigDecimal(Long.MIN_VALUE);

  /**
   * Represents the positive infinity bound of a range and is constrained by the
   * maximal value of the long data type. Attempting to create a range with an
   * upper bound bigger than this number will cause an exception. 
   */
  public static final BigDecimal POSITIVE_INFINITY = new BigDecimal(Long.MAX_VALUE);

  private BigDecimal             lowerBound;
  private BigDecimal             upperBound;
  private String                 name;

  public Range(String name, BigDecimal lowerBound, BigDecimal upperBound) {

    if (lowerBound == null || upperBound == null || lowerBound.compareTo(NEGATIVE_INFINITY) == -1 || upperBound.compareTo(POSITIVE_INFINITY) == 1
        || lowerBound.compareTo(upperBound) == 1) {
      throw new ModelException(ModelException.INCONSISTENT_RANGES,
          "The range you have provided is inconsistent. Are both bounds not null and between neagtive and positive infinity?");
    }
    this.name = name;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  public BigDecimal getLowerBound() {
    return lowerBound;
  }

  public BigDecimal getUpperBound() {
    return upperBound;
  }

  public String getName() {
    return name;
  }

  /**
   * Compares with another range on the basis of their bounds.
   * If the two ranges overlap, 0 is returned. If this range
   * is below the other one, -1 is returned. If this range
   * is above the other one, 1 is returned.
  */
  public int compareTo(Range o) {
    if (this.lowerBound.compareTo(o.getUpperBound()) == 1) {
      return 1;
    }
    else if (this.upperBound.compareTo(o.getLowerBound()) == -1) {
      return -1;
    }
    else {
      return 0;
    }
  }

  public boolean contains(BigDecimal value) {
    boolean result = true;
    if (this.lowerBound != NEGATIVE_INFINITY) {
      result &= this.lowerBound.compareTo(value) == -1;
    }
    if (this.upperBound != POSITIVE_INFINITY) {
      result &= this.upperBound.compareTo(value) == 1;
    }
    return result;
  }

  public boolean equals(Object obj) {
    if (obj == null || !Range.class.isAssignableFrom(obj.getClass())) {
      return false;
    }
    Range r = (Range) obj;
    return name.equals(r.getName()) && lowerBound == r.getLowerBound() && upperBound == r.getUpperBound();
  }

  public int hashCode() {
    return (new HashCodeBuilder()).append(name).append(lowerBound).append(upperBound).hashCode();
  }

  /**{@inheritDoc}**/
  @Override
  public String toString() {
    StringBuffer result = new StringBuffer();
    if (this.lowerBound == NEGATIVE_INFINITY) {
      result.append('(');
    }
    else {
      result.append('[');
      result.append(this.lowerBound);
    }
    result.append(';');
    if (this.upperBound == POSITIVE_INFINITY) {
      result.append(')');
    }
    else {
      result.append(this.upperBound);
      result.append(']');
    }
    return result.toString();
  }
}

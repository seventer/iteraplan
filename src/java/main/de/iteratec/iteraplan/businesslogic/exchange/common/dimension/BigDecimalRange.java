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
package de.iteratec.iteraplan.businesslogic.exchange.common.dimension;

import java.math.BigDecimal;
import java.util.Locale;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;


/**
 * A range class defines a numerical range. There are three types of ranges: Start, end, and middle
 * ranges. Start ranges define a range from minus infinity to a value. End values define a range
 * from a give value to infinity. Middle ranges define areas from start to end. BigDecimalRange
 * implements Comparable and can be sorted.
 */
public class BigDecimalRange implements Comparable<BigDecimalRange> {
  private static final Logger LOGGER             = Logger.getIteraplanLogger(BigDecimalConverter.class);

  public static final String  STRING_FOR_SMALER  = "<";
  public static final String  STRING_FOR_BIGGER  = ">";
  public static final String  STRING_FOR_BETWEEN = "-";
  private static final String STRING_FOR_EMPTY   = " ";
  private static final String STRING_FOR_EQUAL   = "=";

  private BigDecimal          start              = null;
  private BigDecimal          end                = null;
  private TypeOfRange         type;
  private Locale              locale;

  public BigDecimalRange(final BigDecimal start, final BigDecimal end, Locale locale) {

    if (start == null && end == null) {
      LOGGER.error("Es muss mindestens start oder end gefüllt sein!");
      throw new IteraplanBusinessException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    if (start == null && end != null) {
      type = TypeOfRange.START_RANGE;
    }
    else if (end == null) {
      type = TypeOfRange.END_RANGE;
    }
    else {
      type = TypeOfRange.MIDDLE_RANGE;
    }
    this.start = start;
    this.end = end;
    this.locale = locale;
  }

  /**
   * Checks if value is within this range.
   * 
   * @param value
   *          Value to check.
   * @return True if the value is inside this rage. Else false.
   */
  public boolean isInRange(BigDecimal value) {
    boolean match = false;
    if (type == TypeOfRange.START_RANGE) {
      if (value.compareTo(end) <= 0) {
        match = true;
      }
    }
    else if (type == TypeOfRange.END_RANGE) {
      if (value.compareTo(start) > 0) {
        match = true;
      }
    }
    else {
      if (value.compareTo(start) > 0 && (value.compareTo(end) <= 0)) {
        match = true;
      }
    }
    return match;
  }

  public String getResultKey() {
    return this.toString();
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (type == TypeOfRange.START_RANGE) {
      builder.append(STRING_FOR_SMALER).append(STRING_FOR_EQUAL).append(STRING_FOR_EMPTY).append(BigDecimalConverter.format(end, locale));
    }
    else if (type == TypeOfRange.END_RANGE) {
      builder.append(STRING_FOR_BIGGER).append(STRING_FOR_EMPTY).append(BigDecimalConverter.format(start, locale));
    }
    else {
      builder.append(BigDecimalConverter.format(start, locale)).append(STRING_FOR_EMPTY).append(STRING_FOR_BETWEEN).append(STRING_FOR_EMPTY)
          .append(BigDecimalConverter.format(end, locale));
    }
    return builder.toString();
  }

  public TypeOfRange getType() {
    return type;
  }

  private enum TypeOfRange {
    START_RANGE, MIDDLE_RANGE, END_RANGE;
  }

  public BigDecimal getStart() {
    return this.start;
  }

  public int compareTo(BigDecimalRange range) {
    if (range.equals(this)) {
      return 0;
    }
    if (range.getType() == TypeOfRange.START_RANGE && this.getType() != TypeOfRange.START_RANGE) {
      return 1;
    }
    else if (range.getType() == TypeOfRange.END_RANGE && this.getType() != TypeOfRange.END_RANGE) {
      return -1;
    }
    else {
      return this.hashCode() - range.hashCode();
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof BigDecimalRange)) {
      return false;
    }
    BigDecimalRange bd = (BigDecimalRange) obj;
    return bd.hashCode() == this.hashCode();
  }

  @Override
  public int hashCode() {
    int result = 17;
    if (start != null) {
      result = 31 * result + start.intValue();
    }
    if (end != null) {
      result = 31 * result + end.intValue();
    }
    result = 31 * result + type.ordinal();
    return result;
  }

}

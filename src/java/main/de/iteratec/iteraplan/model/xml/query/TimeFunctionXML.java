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
package de.iteratec.iteraplan.model.xml.query;

import java.util.Date;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TimeFunction;


/**
 * Alowes to manage relative time units when saving queries
 * 
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 *
 */
@XmlEnum
public enum TimeFunctionXML {

  ABSOLUTE, NOW, RELATIVE_PAST, RELATIVE_FUTURE;

  /**
   * Calculates the Date to use based on the XML TimeFunction
   * <ul>
   *    <li>{@link #ABSOLUTE} returns the timeUnit that is passed in - can directly be used to generate a Date using new Date(timeUnit)</li>
   *    <li>{@link #NOW} returns the current date/time as long</li>
   *    <li>{@link #RELATIVE_PAST} returns a date lying relativeTimeUnit ms in the past from the current date </li>
   *    <li>{@link #RELATIVE_FUTURE} returns a date lying relativeTimeUnit ms in the future from the current date </li>
   * </ul>
   * 
   * 
   * @param relativeTimeUnit Either the date in ms (if {@link #ABSOLUTE}) or a relative time unit from now 
   * @return The derived date to be used date
   */
  @XmlTransient
  public long getTime(long relativeTimeUnit) {
    if (ABSOLUTE.equals(this)) {
      return relativeTimeUnit;
    }
    else if (RELATIVE_PAST.equals(this)) {
      return new Date().getTime() - relativeTimeUnit;
    }
    else if (RELATIVE_FUTURE.equals(this)) {
      return new Date().getTime() + relativeTimeUnit;
    }
    else {
      return new Date().getTime();
    }
  }

  /**
   * See {@link #getDate(long)}, but returns a Date object instead of its <code>long</code> representation
   * @param relativeTimeUnit See {@link #getDate(long)} 
   * @return The derived date
   */
  @XmlTransient
  public Date getDate(long relativeTimeUnit) {
    if (ABSOLUTE.equals(this)) {
      return new Date(relativeTimeUnit);
    }
    else if (RELATIVE_PAST.equals(this)) {
      if ((new Date().getTime() - relativeTimeUnit) <= 0) {
        return new Date();
      }
      return new Date(new Date().getTime() - relativeTimeUnit);
    }
    else if (RELATIVE_FUTURE.equals(this)) {
      return new Date(new Date().getTime() + relativeTimeUnit);
    }
    else {
      return new Date();
    }
  }

  /**
   * Converts the XML time function to the TimeFunction used internally and for the GUI
   * @return The TimeFunction
   */
  public TimeFunction convert() {
    if (ABSOLUTE.equals(this)) {
      return TimeFunction.ABSOLUTE;
    }
    else if (RELATIVE_PAST.equals(this)) {
      return TimeFunction.RELATIVE_PAST;
    }
    else if (RELATIVE_FUTURE.equals(this)) {
      return TimeFunction.RELATIVE_FUTURE;
    }
    return TimeFunction.NOW;
  }

  /**
   * Converts a given internal TimeFunction to an XML TimeFunction
   * @param timeFunction The TimeFunction
   * @return The XML TimeFunction
   */
  public static TimeFunctionXML convert(TimeFunction timeFunction) {
    if (TimeFunction.NOW.equals(timeFunction)) {
      return NOW;
    }
    if (TimeFunction.RELATIVE_FUTURE.equals(timeFunction)) {
      return RELATIVE_FUTURE;
    }
    if (TimeFunction.RELATIVE_PAST.equals(timeFunction)) {
      return RELATIVE_PAST;
    }
    return ABSOLUTE;
  }
}

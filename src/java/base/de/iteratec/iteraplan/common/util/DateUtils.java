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
package de.iteratec.iteraplan.common.util;

import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.validation.Errors;

import de.iteratec.iteraplan.common.UserContext;


/**
 * Provides static utility methods for operations on dates/time.
 * Delegates all methodcalls to {@link BaseDateUtils}. Uses {@link UserContext#getCurrentLocale()} to resolve the current locale if required. 
 */
public final class DateUtils {
  private DateUtils() {
    // Suppress generation of default constructor, ensuring non-instantiability.
  }

  /**
   * delegates to {@link BaseDateUtils#asInterval(Date, Date)}
   */
  public static Interval asInterval(final Date start, final Date end) {
    return BaseDateUtils.asInterval(start, end);
  }

  /**
   * delegates to {@link BaseDateUtils#asMilliseconds(Date)}
   */
  public static Long asMilliseconds(final Date date) {
    return BaseDateUtils.asMilliseconds(date);
  }

  /**
   * delegates to {@link BaseDateUtils#differenceInDays(Date, Date)}
   */
  public static int differenceInDays(final Date d1, final Date d2) {
    return BaseDateUtils.differenceInDays(d1, d2);
  }

  /**
   * delegates to {@link BaseDateUtils#formatAsString(Date, Locale)}
   */
  public static String formatAsString(final Date date, final Locale locale) {
    return BaseDateUtils.formatAsString(date, locale);
  }

  /**
   * delegates to {@link BaseDateUtils#formatAsStringToDefault(Date, Locale)}
   */
  public static String formatAsStringToDefault(Date date, Locale locale) {
    return BaseDateUtils.formatAsStringToDefault(date, locale);
  }

  /**
   * delegates to {@link BaseDateUtils#formatAsStringNotNull(DateTime, Locale)}
   */
  public static String formatAsStringNotNull(DateTime date, Locale locale) {
    return BaseDateUtils.formatAsStringNotNull(date, locale);
  }

  /**
   * delegates to {@link BaseDateUtils#formatAsStringNotNull(DateTime, Locale)}. Resolves current locale from {@link UserContext}
   */
  public static String formatAsStringNotNull(DateTime date) {
    return BaseDateUtils.formatAsStringNotNull(date, UserContext.getCurrentLocale());
  }

  /**
   * delegates to {@link BaseDateUtils#formatAsStringToLong(Date, Locale)}
   */
  public static String formatAsStringToLong(Date date, Locale locale) {
    return BaseDateUtils.formatAsStringToLong(date, locale);
  }

  /**
   * delegates to {@link BaseDateUtils#parseAsDate(String, Locale)}
   */
  public static Date parseAsDate(final String dateAsString, final Locale locale) {
    return BaseDateUtils.parseAsDate(dateAsString, locale);
  }

  /**
   * delegates to {@link BaseDateUtils#toDateTimeAtStartOfDay(Date)}
   */
  public static DateTime toDateTimeAtStartOfDay(Date d) {
    return BaseDateUtils.toDateTimeAtStartOfDay(d);
  }

  /**
   * delegates to {@link BaseDateUtils#shiftDate(Date, int, Locale)}
   */
  public static Date shiftDate(final Date d, final int days, final Locale locale) {
    return BaseDateUtils.shiftDate(d, days, locale);
  }

  /**
   * delegates to {@link BaseDateUtils#validatePeriod(Date, Date)}
   */
  public static void validatePeriod(final Date start, final Date end) {
    BaseDateUtils.validatePeriod(start, end);
  }

  /**
   * delegates to {@link BaseDateUtils#validatePeriod(Errors, Date, Date)}
   */
  public static void validatePeriod(Errors errors, final Date start, final Date end) {
    BaseDateUtils.validatePeriod(errors, start, end);
  }

  /**
   * delegates to {@link BaseDateUtils#earlier(Date, Date)}
   */
  public static Date earlier(Date date1, Date date2) {
    return BaseDateUtils.earlier(date1, date2);
  }

  /**
   * delegates to {@link BaseDateUtils#later(Date, Date)}
   */
  public static Date later(Date date1, Date date2) {
    return BaseDateUtils.later(date1, date2);
  }
}
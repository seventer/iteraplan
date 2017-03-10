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
package de.iteratec.iteraplan.businesslogic.exchange.common.masterplan;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.IMasterplanOptions;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.IteraplanProperties;


public final class MasterplanCommon {

  private static final double DAYS_PER_MONTH = 30.436875;

  private MasterplanCommon() {
    // do nothing. Just to prevent instantiation
  }

  public static void validateOptions(final IMasterplanOptions optionsToValidate, final Locale currentLocale) {

    int maxMonths = IteraplanProperties.getIntProperty(IteraplanProperties.EXPORT_GRAPHICAL_MASTERPLAN_MAXMONTHS);

    if ("".equals(optionsToValidate.getStartDateString()) || "".equals(optionsToValidate.getEndDateString())) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.TIMESPAN_NOT_CLOSED);
    }

    Date start = DateUtils.parseAsDate(optionsToValidate.getStartDateString(), currentLocale);
    Date end = DateUtils.parseAsDate(optionsToValidate.getEndDateString(), currentLocale);
    DateUtils.validatePeriod(start, end);
    int daysToShow = DateUtils.differenceInDays(end, start);

    if (daysToShow > (maxMonths * DAYS_PER_MONTH)) {

      throw new IteraplanBusinessException(IteraplanErrorMessages.TIMESPAN_TOO_LARGE, new Object[] { Integer.valueOf(maxMonths),
          Integer.valueOf(maxMonths / 12) });
    }
  }

  /**
   * Number of month in between start and end date
   * 
   * @param startDateToCheck
   * @param endDateToCheck
   * @return Number of months
   */
  public static int getTotalMonths(final Date startDateToCheck, final Date endDateToCheck) {
    int calculatedMonths = 0;
    Calendar startDateToCheckC = Calendar.getInstance();
    startDateToCheckC.setTime(startDateToCheck);
    Calendar endDateToCheckC = Calendar.getInstance();
    endDateToCheckC.setTime(endDateToCheck);
    if (startDateToCheckC.get(Calendar.YEAR) != endDateToCheckC.get(Calendar.YEAR)) {
      int fullYears = endDateToCheckC.get(Calendar.YEAR) - startDateToCheckC.get(Calendar.YEAR) - 1;
      int startYearMonthCount = 12 - startDateToCheckC.get(Calendar.MONTH);
      int endYearMonthCount = endDateToCheckC.get(Calendar.MONTH) + 1;
      calculatedMonths = startYearMonthCount + endYearMonthCount + (fullYears * 12);
    }
    else {
      calculatedMonths = endDateToCheckC.get(Calendar.MONTH) - startDateToCheckC.get(Calendar.MONTH) + 1;
    }
    return calculatedMonths;
  }

  /**
   * Provides the total difference in days between the two dates with accuracy taking for example
   * time zones into consideration.
   * 
   * @param startDate
   * @param endDate
   * @return The exact number of days between the two dates.
   */
  public static long getTotalDays(Date startDate, Date endDate) {

    long millisecsPerDay = 86400000;
    Calendar startCalendar = Calendar.getInstance();
    startCalendar.setTime(startDate);
    Calendar endCalender = Calendar.getInstance();
    endCalender.setTime(endDate);

    double endL = endCalender.getTimeInMillis() + endCalender.getTimeZone().getOffset(endCalender.getTimeInMillis());
    double startL = startCalendar.getTimeInMillis() + startCalendar.getTimeZone().getOffset(startCalendar.getTimeInMillis());

    return Math.round((endL - startL) / millisecsPerDay);
  }

  public static String getShortMonthForInt(final Locale locale, int theMonth) {
    String month = "???";
    DateFormatSymbols dfs = new DateFormatSymbols(locale);
    String[] months = dfs.getShortMonths();
    month = months[theMonth % 12];
    return month;
  }

}

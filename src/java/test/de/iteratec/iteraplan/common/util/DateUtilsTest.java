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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;


/**
 * Test cases for the {@link DateUtils} utility class.
 */
public class DateUtilsTest {

  @Test
  public void testAsInterval() {
    Interval i;
    Date d1;
    Date d2;

    d1 = createDate(2008, 7, 1);
    d2 = createDate(2008, 7, 31);
    i = DateUtils.asInterval(d1, d2);
    assertEquals(d1, i.getStart().toDate());
    assertEquals(d2, i.getEnd().toDate());

    d2 = createDate(2008, 7, 31);
    i = DateUtils.asInterval(null, d2);
    assertEquals(BaseDateUtils.MIN_DATE.toDate(), i.getStart().toDate());
    assertEquals(d2, i.getEnd().toDate());

    d1 = createDate(2008, 7, 1);
    i = DateUtils.asInterval(d1, null);
    assertEquals(d1, i.getStart().toDate());
    assertEquals(BaseDateUtils.MAX_DATE.toDate(), i.getEnd().toDate());
  }

  @Test
  public void testDifferenceInDays() {
    assertEquals(0, DateUtils.differenceInDays(createDate(2008, 7, 1), createDate(2008, 7, 1)));
    assertEquals(1, DateUtils.differenceInDays(createDate(2008, 7, 1), createDate(2008, 7, 2)));
    assertEquals(1, DateUtils.differenceInDays(createDate(2008, 7, 2), createDate(2008, 7, 1)));
    assertEquals(62, DateUtils.differenceInDays(createDate(2008, 7, 1), createDate(2008, 9, 1)));
  }

  @Test
  public void testParseAsDate() {
    Date parsed;

    parsed = DateUtils.parseAsDate("01.07.2008", Locale.GERMAN);
    assertEquals(createDate(2008, 7, 1), parsed);

    parsed = DateUtils.parseAsDate("7/1/2008", Locale.ENGLISH);
    assertEquals(createDate(2008, 7, 1), parsed);

    parsed = DateUtils.parseAsDate("", Locale.GERMAN);
    assertNull(parsed);

    parsed = DateUtils.parseAsDate(null, Locale.GERMAN);
    assertNull(parsed);

    try {
      parsed = DateUtils.parseAsDate("01-07-2008", Locale.GERMAN);
    } catch (IteraplanBusinessException e) {
      assertEquals(IteraplanErrorMessages.INCORRECT_DATE_FORMAT, e.getErrorCode());
    }

    try {
      parsed = DateUtils.parseAsDate("01.07.999", Locale.GERMAN);
    } catch (IteraplanBusinessException e) {
      assertEquals(IteraplanErrorMessages.INVALID_DATES, e.getErrorCode());
    }
  }

  @Test
  public void testParseAsString() {
    String parsed;

    parsed = DateUtils.formatAsString(createDate(2008, 7, 1), Locale.GERMAN);
    assertEquals("01.07.2008", parsed);

    parsed = DateUtils.formatAsString(createDate(2008, 7, 1), Locale.ENGLISH);
    assertEquals("07/01/2008", parsed);

    parsed = DateUtils.formatAsString(null, Locale.GERMAN);
    assertNull(parsed);
  }

  @Test
  public void testValidatePeriod() {
    DateUtils.validatePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    DateUtils.validatePeriod(null, createDate(2008, 7, 31));
    DateUtils.validatePeriod(createDate(2008, 7, 1), null);
    DateUtils.validatePeriod(null, null);

    try {
      DateUtils.validatePeriod(createDate(2008, 7, 31), createDate(2008, 7, 1));
    } catch (IteraplanBusinessException e) {
      assertEquals(IteraplanErrorMessages.INVALID_TIMESPAN, e.getErrorCode());
    }

    try {
      DateUtils.validatePeriod(createDate(999, 7, 1), createDate(2008, 7, 31));
    } catch (IteraplanBusinessException e) {
      assertEquals(IteraplanErrorMessages.INVALID_DATES, e.getErrorCode());
    }

    try {
      DateUtils.validatePeriod(createDate(2008, 7, 1), createDate(10000, 7, 31));
    } catch (IteraplanBusinessException e) {
      assertEquals(IteraplanErrorMessages.INVALID_DATES, e.getErrorCode());
    }
  }

  private Date createDate(int year, int monthOfYear, int dayOfMonth) {
    return new DateTime(year, monthOfYear, dayOfMonth, 0, 0, 0, 0).toDate();
  }

  @Test
  public void testFormatDate() {
    Calendar c = Calendar.getInstance();
    c.set(2006, 1, 20);
    assertEquals("20.02.2006", DateUtils.formatAsString(c.getTime(), Locale.GERMAN));
    assertEquals("02/20/2006", DateUtils.formatAsString(c.getTime(), Locale.ENGLISH));
    assertEquals("20/02/2006", DateUtils.formatAsString(c.getTime(), new Locale("es")));
  }

  @Test
  public void testParseDate() {
    String germanDate = "18.10.2006";
    String spanishDate = "18/10/2006";
    Calendar c = Calendar.getInstance();
    c.set(2006, 9, 18, 0, 0, 0);
    try {
      assertEquals(c.getTime().toString(), DateUtils.parseAsDate(germanDate, Locale.GERMAN).toString());
    } catch (IteraplanBusinessException e) {
      fail();
    }
    try {
      DateUtils.parseAsDate(germanDate, Locale.ENGLISH);
      fail();
      DateUtils.parseAsDate(spanishDate, Locale.ENGLISH);
      fail();
    } catch (IteraplanBusinessException e) {
      // ok, wrong date format.
    }
    String englishDate = "10/18/2006";
    try {
      assertEquals(c.getTime().toString(), DateUtils.parseAsDate(englishDate, Locale.ENGLISH).toString());
    } catch (IteraplanBusinessException e) {
      fail();
    }
    try {
      DateUtils.parseAsDate(germanDate, new Locale("es"));
      fail();
    } catch (IteraplanBusinessException e) {
      // ok, wrong date format.
    }
    try {
      assertEquals(c.getTime().toString(), DateUtils.parseAsDate(spanishDate, new Locale("es")).toString());
    } catch (IteraplanBusinessException e) {
      fail();
    }
  }

  @Test
  public void formatAsStringToDefaultWithDateNull() {
    assertEquals("", DateUtils.formatAsStringToDefault(null, Locale.GERMAN));
  }

  @Test
  public void formatAsStringToDefault() {
    DateTime dateTime = new DateTime().withYear(2010).withMonthOfYear(12).withDayOfMonth(1).withHourOfDay(15).withMinuteOfHour(42)
        .withSecondOfMinute(15);
    assertEquals("01.12.2010", DateUtils.formatAsStringToDefault(dateTime.toDate(), Locale.GERMAN));
  }

  @Test
  public void formatAsStringToLong() {
    DateTime dateTime = new DateTime().withYear(2010).withMonthOfYear(12).withDayOfMonth(1).withHourOfDay(15).withMinuteOfHour(42)
        .withSecondOfMinute(15);
    String formattedDate = DateUtils.formatAsStringToLong(dateTime.toDate(), Locale.GERMAN);
    assertEquals("01.12.2010 15:42", formattedDate);
  }

  @Test
  public void formatAsStringToLongNull() {
    String formattedDate = DateUtils.formatAsStringToLong(null, Locale.GERMAN);
    assertNull(formattedDate);
  }
}

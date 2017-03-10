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
package de.iteratec.iteraplan.model.user;

import java.util.Date;

import junit.framework.TestCase;

import org.joda.time.DateTime;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.RuntimePeriod;


/**
 * Test cases for a {@link RuntimePeriod}.
 */
public class RuntimePeriodTest extends TestCase {

  public void testConstructor() {
    RuntimePeriod p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));

    p1 = new RuntimePeriod(null, createDate(2008, 7, 31));
    assertEquals(null, p1.getStart());

    p1 = new RuntimePeriod(createDate(2008, 7, 1), null);
    assertEquals(null, p1.getEnd());

    p1 = new RuntimePeriod(null, null);
    assertEquals(null, p1.getStart());
    assertEquals(null, p1.getEnd());

    try {
      p1 = new RuntimePeriod(createDate(2008, 7, 31), createDate(2008, 7, 1));
    } catch (IteraplanBusinessException e) {
      assertEquals(IteraplanErrorMessages.INVALID_TIMESPAN, e.getErrorCode());
    }

    try {
      p1 = new RuntimePeriod(createDate(999, 7, 1), createDate(2008, 7, 31));
    } catch (IteraplanBusinessException e) {
      assertEquals(IteraplanErrorMessages.INVALID_DATES, e.getErrorCode());
    }

    try {
      p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(10000, 7, 31));
    } catch (IteraplanBusinessException e) {
      assertEquals(IteraplanErrorMessages.INVALID_DATES, e.getErrorCode());
    }
  }

  public void testEquals() {
    RuntimePeriod p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    RuntimePeriod p2 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    assertEquals(true, p1.equals(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    p2 = new RuntimePeriod(createDate(2008, 8, 1), createDate(2008, 8, 31));
    assertEquals(false, p1.equals(p2));
  }

  public void testGapToPeriod() {
    RuntimePeriod p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    RuntimePeriod p2 = new RuntimePeriod(createDate(2008, 8, 1), createDate(2008, 8, 31));
    assertEquals(1, p1.gapToPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    p2 = new RuntimePeriod(createDate(2009, 8, 1), createDate(2009, 8, 31));
    assertEquals(366, p1.gapToPeriod(p2));

    //leap-year!
    p1 = new RuntimePeriod(createDate(2011, 7, 1), createDate(2011, 7, 31));
    p2 = new RuntimePeriod(createDate(2012, 8, 1), createDate(2012, 8, 31));
    assertEquals(367, p1.gapToPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    p2 = new RuntimePeriod(createDate(2008, 7, 31), createDate(2008, 8, 31));
    assertEquals(-1, p1.gapToPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), null);
    p2 = new RuntimePeriod(createDate(2008, 7, 31), createDate(2008, 8, 31));
    assertEquals(-1, p1.gapToPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    p2 = new RuntimePeriod(null, createDate(2008, 8, 31));
    assertEquals(-1, p1.gapToPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), null);
    p2 = new RuntimePeriod(null, createDate(2008, 8, 31));
    assertEquals(-1, p1.gapToPeriod(p2));
  }

  public void testWithinPeriod() {
    RuntimePeriod p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    RuntimePeriod p2 = new RuntimePeriod(createDate(2008, 8, 1), createDate(2008, 8, 31));
    assertFalse(p1.withinPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    p2 = new RuntimePeriod(createDate(2008, 7, 15), createDate(2008, 8, 15));
    assertFalse(p1.withinPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    p2 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    assertTrue(p1.withinPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 2), createDate(2008, 7, 30));
    p2 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    assertTrue(p1.withinPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    p2 = new RuntimePeriod(null, createDate(2008, 8, 15));
    assertTrue(p1.withinPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    p2 = new RuntimePeriod(createDate(2008, 7, 15), null);
    assertFalse(p1.withinPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    p2 = new RuntimePeriod(null, null);
    assertTrue(p1.withinPeriod(p2));
  }

  public void testOverlapsPeriod() {
    RuntimePeriod p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    RuntimePeriod p2 = new RuntimePeriod(createDate(2008, 8, 1), createDate(2008, 8, 31));
    assertFalse(p1.overlapsPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    p2 = new RuntimePeriod(createDate(2008, 7, 15), createDate(2008, 8, 15));
    assertTrue(p1.overlapsPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    p2 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    assertTrue(p1.overlapsPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 2), createDate(2008, 7, 30));
    p2 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    assertTrue(p1.overlapsPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    p2 = new RuntimePeriod(null, createDate(2008, 7, 15));
    assertTrue(p1.overlapsPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    p2 = new RuntimePeriod(createDate(2008, 7, 15), null);
    assertTrue(p1.overlapsPeriod(p2));

    p1 = new RuntimePeriod(createDate(2008, 7, 1), createDate(2008, 7, 31));
    p2 = new RuntimePeriod(null, null);
    assertTrue(p1.overlapsPeriod(p2));
  }

  private Date createDate(int year, int monthOfYear, int dayOfMonth) {
    return new DateTime(year, monthOfYear, dayOfMonth, 0, 0, 0, 0).toDate();
  }

}
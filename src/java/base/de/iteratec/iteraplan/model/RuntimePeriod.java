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
package de.iteratec.iteraplan.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import org.joda.time.Days;
import org.joda.time.Interval;

import de.iteratec.iteraplan.common.util.BaseDateUtils;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.EqualsUtils;


/**
 * Defines a runtime period consisting of a start and an end date.
 * <p>
 * The following invariants hold if at least one of the contained dates is not {@code null}.
 * <ul>
 * <li>The start date must be before the end date.</li>
 * <li>The start date must be after {@link BaseDateUtils#MIN_DATE}.</li>
 * <li>The end date must be before {@link BaseDateUtils#MAX_DATE}.</li>
 * </ul>
 */
@Entity
@Audited
public class RuntimePeriod implements Serializable {

  private static final long serialVersionUID = 2196438075670842433L;
  @Field(store = Store.YES)
  @DateBridge(resolution = Resolution.DAY)
  private Date              start;

  @Field(store = Store.YES)
  @DateBridge(resolution = Resolution.DAY)
  private Date              end;

  public RuntimePeriod() {
    // No-arg constructor.
  }

  /**
   * Constructs a new runtime period. Either argument may be {@code null}.
   * 
   * @throws de.iteratec.iteraplan.common.error.IteraplanBusinessException
   *           If the start is strictly after the end.
   * @throws de.iteratec.iteraplan.common.error.IteraplanBusinessException
   *           If the start is before {@link BaseDateUtils#MIN_DATE} or the end is after
   *           {@link BaseDateUtils#MAX_DATE}.
   */
  public RuntimePeriod(Date start, Date end) {

    DateUtils.validatePeriod(start, end);

    if (start != null) {
      setStart(DateUtils.toDateTimeAtStartOfDay(start).toDate());
    }
    else {
      setStart(null);
    }

    if (end != null) {
      setEnd(DateUtils.toDateTimeAtStartOfDay(end).toDate());
    }
    else {
      setEnd(null);
    }
  }

  public Date getStart() {
    return start;
  }

  public Date getEnd() {
    return end;
  }

  private void setStart(Date start) {
    this.start = start;
  }

  private void setEnd(Date end) {
    this.end = end;
  }

  /**
   * @return Returns the gap to the given period in days or -1 if there is no gap. If the given
   *         period is {@code null} (i.e. {@link BaseDateUtils#MIN_DATE} and {@link BaseDateUtils#MAX_DATE}
   *         is assumed), the test returns -1.
   */
  public int gapToPeriod(RuntimePeriod period) {

    if (period == null) {
      return -1;
    }

    Interval other = DateUtils.asInterval(period.getStart(), period.getEnd());
    Interval gap = asInterval().gap(other);
    return gap == null ? -1 : Days.daysIn(gap).getDays();
  }

  /**
   * @return Returns {@code true} if this period is unbounded, i.e. both start and end are {@code
   *         null}.
   */
  public boolean isUnbounded() {
    return start == null && end == null;
  }

  /**
   * @return Returns {@code true}, if this period is completely contained in the given period
   *         (inclusive at the start and end). If the given period is {@code null} (i.e.
   *         {@link BaseDateUtils#MIN_DATE} and {@link BaseDateUtils#MAX_DATE} is assumed), the test returns
   *         {@code true}.
   */
  public boolean withinPeriod(RuntimePeriod period) {

    if (period == null) {
      return true;
    }

    Interval other = DateUtils.asInterval(period.getStart(), period.getEnd());
    Interval thiz = asInterval();

    Interval overlap = thiz.overlap(other);

    if (overlap == null) {
      return false;
    }

    return overlap.toDuration().equals(thiz.toDuration()) ? true : false;
  }

  /**
   * @return Returns {@code true} if this period overlaps or abuts the given period. If the given
   *         period is {@code null} (i.e. {@link BaseDateUtils#MIN_DATE} and {@link BaseDateUtils#MAX_DATE}
   *         is assumed), the test returns {@code true}.
   */
  public boolean overlapsPeriod(RuntimePeriod period) {

    if (period == null) {
      return true;
    }

    Interval other = DateUtils.asInterval(period.getStart(), period.getEnd());
    return asInterval().gap(other) == null ? true : false;
  }

  @Override
  public int hashCode() {

    int prime = 29;
    int result = 1;

    result = prime * result + ((end == null) ? 0 : end.hashCode());
    result = prime * result + ((start == null) ? 0 : start.hashCode());

    return result;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final RuntimePeriod other = (RuntimePeriod) o;

    return EqualsUtils.areEqual(start, other.start) && EqualsUtils.areEqual(end, other.end);
  }

  @Override
  public String toString() {
    return "The period ranges from " + start + " until " + end;
  }

  private Interval asInterval() {
    return DateUtils.asInterval(start, end);
  }
}
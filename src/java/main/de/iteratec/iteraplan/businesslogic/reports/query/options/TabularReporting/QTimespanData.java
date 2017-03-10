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
package de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TimeFunction;
import de.iteratec.iteraplan.common.util.DateUtils;


/**
 * A JSP backing bean that that holds the GUI data from a textual query about
 * the timespan of a queried type of building block. 
 */
public final class QTimespanData implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID  = 1895451294658788380L;

  /** The locale the timespan's dates should be formatted with. */
  private Locale            locale            = null;

  private String            startDateAsString = null;
  private TimeFunction      startTimeFunction = TimeFunction.ABSOLUTE;

  private String            endDateAsString   = null;
  private TimeFunction      endTimeFunction   = TimeFunction.ABSOLUTE;

  public QTimespanData(Locale locale) {
    this.locale = locale;
    this.startDateAsString = DateUtils.formatAsString(new Date(), locale);
    this.endDateAsString = DateUtils.formatAsString(new Date(), locale);
  }

  public String getStartDateAsString() {
    return startDateAsString;
  }

  public String getEndDateAsString() {
    return endDateAsString;
  }

  public TimeFunction getStartTimeFunction() {
    return startTimeFunction;
  }

  public TimeFunction getEndTimeFunction() {
    return endTimeFunction;
  }

  public void setStartDateAsString(String startDateAsString) {
    this.startDateAsString = startDateAsString;
  }

  public void setEndDateAsString(String endDateAsString) {
    this.endDateAsString = endDateAsString;
  }

  public void setStartTimeFunction(TimeFunction startTimeFunction) {
    this.startTimeFunction = startTimeFunction;
  }

  public void setEndTimeFunction(TimeFunction endTimeFunction) {
    this.endTimeFunction = endTimeFunction;
  }

  public Date getStartDate() {
    return DateUtils.parseAsDate(startDateAsString, locale);
  }

  public Date getEndDate() {
    return DateUtils.parseAsDate(endDateAsString, locale);
  }

  public boolean isStartDateSet() {
    return StringUtils.isNotEmpty(startDateAsString);
  }

  public boolean isEndDateSet() {
    return StringUtils.isNotEmpty(endDateAsString);
  }

  /**
   * Validates the period according to {@link DateUtils#validatePeriod(Date, Date)}.
   */
  public void validate(Errors errors) {
    DateUtils.validatePeriod(errors, getStartDate(), getEndDate());
  }

}
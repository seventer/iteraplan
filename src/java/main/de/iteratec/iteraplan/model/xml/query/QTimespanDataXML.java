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
import java.util.Locale;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TimeFunction;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QTimespanData;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.DateUtils;


/**
 * Represents the Date in a query form. If not date is provided, TimeFunction is set
 * to TimeFunctionXML.ABSOLUTE and the date value to -1
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 *
 */
@XmlType(name = "queryTimespan")
public class QTimespanDataXML extends AbstractXMLElement<QTimespanData> {

  private static final Logger LOGGER            = Logger.getIteraplanLogger(QTimespanDataXML.class);

  /** {@link #getStartDate()} */
  private long                startDate         = -1;
  private long                endDate           = -1;

  private TimeFunctionXML     startTimeFunction = TimeFunctionXML.ABSOLUTE;
  private TimeFunctionXML     endTimeFunction   = TimeFunctionXML.ABSOLUTE;

  /**
   * The start date to use. Takes an absolute date in milliseconds if
   * {@link #getStartTimeFunction()}== {@link TimeFunctionXML#ABSOLUTE},
   * or a relative value (x milliseconds from now) if  {@link TimeFunctionXML#RELATIVE_PAST} or
   * {@link TimeFunctionXML#RELATIVE_FUTURE} is used.
   * Ignored if {@link #getStartTimeFunction()}=={@link TimeFunctionXML#NOW}
   * @return The relative or absolute date (as long) to use
   */
  @XmlElement
  public long getStartDate() {
    return startDate;
  }

  /**
   * The end date to use. Takes an absolute date in milliseconds if
   * {@link #getEndTimeFunction()}== {@link TimeFunctionXML#ABSOLUTE},
   * or a relative value (x milliseconds from now) if  {@link TimeFunctionXML#RELATIVE_PAST} or
   * {@link TimeFunctionXML#RELATIVE_FUTURE} is used.
   * Ignored if {@link #getEndTimeFunction()}=={@link TimeFunctionXML#NOW}
   * @return The relative or absolute date (as long) to use
   */
  @XmlElement
  public long getEndDate() {
    return endDate;
  }

  /**
   * Indicates whether the startdate is an absolute date value, a relative value in the past or the future or
   * needs to be set to the current date.
   * @return The TimeFunctionXML for the startDate
   */
  @XmlElement(required = true)
  public TimeFunctionXML getStartTimeFunction() {
    return startTimeFunction;
  }

  /**
   * Indicates whether the endDate is an absolute date value, a relative value in the past or the future or
   * needs to be set to the current date.
   * @return The TimeFunctionXML for the endDate
   */
  @XmlElement(required = true)
  public TimeFunctionXML getEndTimeFunction() {
    return endTimeFunction;
  }

  public void setStartDate(long startDate) {
    this.startDate = startDate;
  }

  public void setEndDate(long endDate) {
    this.endDate = endDate;
  }

  public void setStartTimeFunction(TimeFunctionXML startTimeFunction) {
    this.startTimeFunction = startTimeFunction;
  }

  public void setEndTimeFunction(TimeFunctionXML endTimeFunction) {
    this.endTimeFunction = endTimeFunction;
  }

  /* (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#initFrom(java.lang.Object, java.util.Locale)
   */
  public void initFrom(QTimespanData queryElement, Locale locale) {
    Date dt = queryElement.getStartDate();
    if (dt != null) {
      this.startDate = dt.getTime();
    }
    dt = queryElement.getEndDate();
    if (dt != null) {
      this.endDate = dt.getTime();
    }
  }

  /* (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#update(java.lang.Object, java.util.Locale)
   */
  public void update(QTimespanData timespanQueryData, Locale locale) {
    if (isStartDateNull()) {
      timespanQueryData.setStartDateAsString("");
      timespanQueryData.setStartTimeFunction(TimeFunction.ABSOLUTE);
    }
    else {
      timespanQueryData.setStartDateAsString(DateUtils.formatAsString(this.calculateStartDate(), locale));
      timespanQueryData.setStartTimeFunction(this.getStartTimeFunction().convert());
    }

    if (isEndDateNull()) {
      timespanQueryData.setEndDateAsString("");
      timespanQueryData.setEndTimeFunction(TimeFunction.ABSOLUTE);
    }
    else {
      timespanQueryData.setEndDateAsString(DateUtils.formatAsString(this.calculateEndDate(), locale));
      timespanQueryData.setEndTimeFunction(this.getEndTimeFunction().convert());
    }
  }

  /**
   * Sets the startDate based on a dateString
   * @param dateString Either the localised date as a string or a relativ number N that indicates
   * a relative date N milliseconds in the future or the past (calculated milliseconds(currentDate) + N)
   * @param locale The current locale
   * @param timeFunction The timefunction that indicates whether an absolute date shall be saved, or the
   * current date (or a value that lies N milliseconds in the future or past) will be returned when
   * reading the XML file
   */
  public void setStartDate(String dateString, Locale locale, TimeFunction timeFunction) {
    if (StringUtils.isEmpty(dateString)) {
      startTimeFunction = TimeFunctionXML.ABSOLUTE;
      startDate = -1;
      return;
    }
    if (timeFunction == null) {
      setAbsoluteDate(dateString, true, locale);
    }
    else {
      this.startTimeFunction = TimeFunctionXML.convert(timeFunction);
      if (TimeFunction.ABSOLUTE.equals(timeFunction)) {
        setAbsoluteDate(dateString, true, locale);
      }
      else if (!TimeFunction.NOW.equals(timeFunction)) {
        try {
          this.startDate = Integer.parseInt(dateString);
        } catch (NumberFormatException e) {
          StringBuffer buffer = new StringBuffer(60);
          buffer.append("Received an illegal number value for a relative time. Time Function: '").append(timeFunction).append("', value: '")
              .append(dateString).append("'. Ignoring the when retrieving the saved query!!");
          LOGGER.error(buffer.toString(), e);
        }
      }
    }
  }

  /**
   * Sets the enddate based on a dateString
   * @param dateString Either the localised date as a string or a relativ number N that indicates
   * a relative date N milliseconds in the future or the past (calculated milliseconds(currentDate) + N)
   * @param locale The current locale
   * @param timeFunction The timefunction that indicates whether an absolute date shall be saved, or the
   * current date (or a value that lies N milliseconds in the future or past) will be returned when
   * reading the XML file
   */
  public void setEndDate(String dateString, Locale locale, TimeFunction timeFunction) {
    if (StringUtils.isEmpty(dateString)) {
      endTimeFunction = TimeFunctionXML.ABSOLUTE;
      endDate = -1;
      return;
    }
    if (timeFunction == null) {
      setAbsoluteDate(dateString, false, locale);
    }
    else {
      this.endTimeFunction = TimeFunctionXML.convert(timeFunction);
      if (TimeFunction.ABSOLUTE.equals(timeFunction)) {
        setAbsoluteDate(dateString, false, locale);
      }
      else {
        try {
          this.endDate = Integer.parseInt(dateString);
        } catch (NumberFormatException e) {
          StringBuffer buffer = new StringBuffer(60);
          buffer.append("Received an illegal number value for a relative time. Time Function: '").append(timeFunction).append("', value: '")
              .append(dateString).append("'. Ignoring the when retrieving the saved query!!");
          LOGGER.error(buffer.toString(), e);
        }
      }
    }
  }

  private void setAbsoluteDate(String dateString, boolean isStartDate, Locale locale) {
    if (locale != null) {
      if (StringUtils.isNotEmpty(dateString)) {
        if (isStartDate) {
          this.startDate = DateUtils.parseAsDate(dateString, locale).getTime();
        }
        else {
          this.endDate = DateUtils.parseAsDate(dateString, locale).getTime();
        }
      }
    }
    else {
      LOGGER.error("Not persisting start- end enddate as the current locale is not set!");
    }
  }

  public long calculateStartTime() {
    return startTimeFunction.getTime(startDate);
  }

  public Date calculateStartDate() {
    return new Date(calculateStartTime());
  }

  public long calculateEndTime() {
    return endTimeFunction.getTime(endDate);
  }

  public Date calculateEndDate() {
    return new Date(calculateEndTime());
  }

  /**
   * {@inheritDoc}
   */
  public void validate(Locale locale) {
    if (!(TimeFunctionXML.NOW.equals(startTimeFunction)) && startDate < 0 && !isStartDateNull()) {
      logError("Negative (relative) date value '" + startDate + "' not allowed as " + startTimeFunction + " start date");
    }
    if (!(TimeFunctionXML.NOW.equals(endTimeFunction)) && endDate < 0 && !isEndDateNull()) {
      logError("Negative (relative) date value '" + endDate + "' not allowed as " + startTimeFunction + " end date");
    }

    // try to convert the date values
    if (!isStartDateNull()) {
      convertDateValue(locale, startDate);
    }
    if (!isEndDateNull()) {
      convertDateValue(locale, endDate);
    }

    validateEndDateAfterStartDate(locale);
  }

  private void validateEndDateAfterStartDate(Locale locale) {
    if (!isStartDateNull() && !isEndDateNull() && calculateEndTime() < calculateStartTime()) {
      String sd = Long.toString(startDate);
      String ed = Long.toString(endDate);
      if (endDate > 0) {
        ed = DateUtils.formatAsString(new Date(endDate), locale);
      }
      if (startDate > 0) {
        sd = DateUtils.formatAsString(new Date(startDate), locale);
      }
      logError("End date '" + ed + "' is bigger than start date '" + sd + "' (calculated values: start: " + calculateStartTime() + ", end: "
          + calculateEndTime() + ")");
    }
  }

  private void convertDateValue(Locale locale, long date) {
    try {
      DateUtils.formatAsString(new Date(date), locale);
    } catch (RuntimeException e) {
      logError("Could not derive a date from '" + date + "'", e);
    }
  }

  /**
   * if function=ABSOLUTE and date=-1, no date must be set
   * @return true if the startDate is null
   */
  private boolean isStartDateNull() {
    return TimeFunctionXML.ABSOLUTE.equals(startTimeFunction) && startDate == -1;
  }

  /**
   * if function=ABSOLUTE and date=-1, no date must be set
   * @return true if the endDate is null
   */
  private boolean isEndDateNull() {
    return TimeFunctionXML.ABSOLUTE.equals(endTimeFunction) && endDate == -1;
  }

}

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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TimeFunction;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.DateUtils;


/**
 * Container for the parameters of the Masterplan diagram.
 */
public class MasterplanOptionsBean extends GraphicalExportBaseOptions implements IMasterplanOptions, Serializable {

  /** Serialization version. */
  private static final long        serialVersionUID        = 1624207673645169725L;
  /** {@link #getStartDateString()} */
  private String                   startDateString;
  /** {@link #getStartTimeFunction()} */
  private TimeFunction             startTimeFunction       = TimeFunction.ABSOLUTE;
  /** End date for the time axis. */
  private String                   endDateString;
  /** {@link #endTimeFunction} */
  private TimeFunction             endTimeFunction         = TimeFunction.ABSOLUTE;

  private boolean                  advancedSettingsToggled = false;

  private Integer                  dateIntervalToRemove;

  private MasterplanRowTypeOptions level0TypeOptions;
  private MasterplanRowTypeOptions level1TypeOptions;
  private MasterplanRowTypeOptions level2TypeOptions;

  private String                   selectedLevel1Relation  = "";
  private String                   selectedLevel2Relation  = "";

  public MasterplanOptionsBean() {
    super();
    setAvailableBbTypes(Constants.ALL_TYPES_FOR_DISPLAY);
    // set start date to today
    Date date = new Date();
    startDateString = DateUtils.formatAsString(date, UserContext.getCurrentLocale());
    // set start date to today plus one year
    Calendar endDateC = Calendar.getInstance();
    endDateC.setTime(date);
    endDateC.roll(Calendar.YEAR, true);
    endDateString = DateUtils.formatAsString(endDateC.getTime(), UserContext.getCurrentLocale());
  }

  public String getSelectedLevel1Relation() {
    return selectedLevel1Relation;
  }

  public void setSelectedLevel1Relation(String newLevel1Relation) {
    this.selectedLevel1Relation = newLevel1Relation;
  }

  public String getSelectedLevel2Relation() {
    return selectedLevel2Relation;
  }

  public void setSelectedLevel2Relation(String newLevel2Relation) {
    this.selectedLevel2Relation = newLevel2Relation;
  }

  public String getEndDateString() {
    return endDateString;
  }

  public void setEndDateString(String endDateString) {
    this.endDateString = endDateString;
  }

  public String getStartDateString() {
    return startDateString;
  }

  public void setStartDateString(String startDateString) {
    this.startDateString = startDateString;
  }

  /**
   * Returns the start time function
   *
   * @return The start time function
   */
  public TimeFunction getStartTimeFunction() {
    return startTimeFunction;
  }

  /**
   * Returns the end time function
   *
   * @return The end time function
   */
  public TimeFunction getEndTimeFunction() {
    return endTimeFunction;
  }

  public void setStartTimeFunction(String id) {
    this.startTimeFunction = TimeFunction.getInstance(id);
  }

  public void setStartTimeFunction(TimeFunction startTimeFunction) {
    this.startTimeFunction = startTimeFunction;
  }

  public void setEndTimeFunction(String id) {
    this.endTimeFunction = TimeFunction.getInstance(id);
  }

  public void setEndTimeFunction(TimeFunction endTimeFunction) {
    this.endTimeFunction = endTimeFunction;
  }

  public boolean isAdvancedSettingsToggled() {
    return advancedSettingsToggled;
  }

  public void setAdvancedSettingsToggled(boolean advancedSettingsToggled) {
    this.advancedSettingsToggled = advancedSettingsToggled;
  }

  public Integer getDateIntervalToRemove() {
    return dateIntervalToRemove;
  }

  public void setDateIntervalToRemove(Integer dateIntervalToRemove) {
    this.dateIntervalToRemove = dateIntervalToRemove;
  }

  public MasterplanRowTypeOptions getLevel0Options() {
    return this.level0TypeOptions;
  }

  public void setLevel0Options(MasterplanRowTypeOptions level0Options) {
    this.level0TypeOptions = level0Options;
  }

  public MasterplanRowTypeOptions getLevel1Options() {
    return this.level1TypeOptions;
  }

  public void setLevel1Options(MasterplanRowTypeOptions level1Options) {
    this.level1TypeOptions = level1Options;
  }

  public MasterplanRowTypeOptions getLevel2Options() {
    return this.level2TypeOptions;
  }

  public void setLevel2Options(MasterplanRowTypeOptions level2Options) {
    this.level2TypeOptions = level2Options;
  }

}

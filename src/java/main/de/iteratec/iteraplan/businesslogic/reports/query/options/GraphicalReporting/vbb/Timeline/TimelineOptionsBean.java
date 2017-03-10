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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.vbb.Timeline;

import java.util.List;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TimeFunction;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.vbb.VbbOptionsBean;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.queries.ReportType;

/**
 *
 */
public class TimelineOptionsBean extends VbbOptionsBean {

  /**
   * Default constructor.
   * @param reportType
   */
  public TimelineOptionsBean(ReportType reportType) {
    super(reportType);
  }

  private static final long serialVersionUID = -2852364215132766637L;
  
  /** {@link #getStartDateString()} */
  private String            startDateString;
  /** {@link #getStartTimeFunction()} */
  private TimeFunction      startTimeFunction                  = TimeFunction.ABSOLUTE;
  /** End date for the time axis. */
  private String            endDateString;
  /** {@link #endTimeFunction} */
  private TimeFunction      endTimeFunction                    = TimeFunction.ABSOLUTE;
  
  private List<BBAttribute>                        availableAttributeTypes;
  
  private int                                      selectedKeyAttributeTypeId      = -1;
  
  /**
   * @return startDateString the startDateString
   */
  public String getStartDateString() {
    return startDateString;
  }

  public void setStartDateString(String startDateString) {
    this.startDateString = startDateString;
  }

  /**
   * @return startTimeFunction the startTimeFunction
   */
  public TimeFunction getStartTimeFunction() {
    return startTimeFunction;
  }

  public void setStartTimeFunction(TimeFunction startTimeFunction) {
    this.startTimeFunction = startTimeFunction;
  }

  /**
   * @return endDateString the endDateString
   */
  public String getEndDateString() {
    return endDateString;
  }

  public void setEndDateString(String endDateString) {
    this.endDateString = endDateString;
  }

  /**
   * @return endTimeFunction the endTimeFunction
   */
  public TimeFunction getEndTimeFunction() {
    return endTimeFunction;
  }

  public void setEndTimeFunction(TimeFunction endTimeFunction) {
    this.endTimeFunction = endTimeFunction;
  }
  
  public void setAvailableAttributeTypes(List<BBAttribute> availableAttributeTypes) {
    this.availableAttributeTypes = availableAttributeTypes;
  }

  public List<BBAttribute> getAvailableAttributeTypes() {
    return availableAttributeTypes;
  }

  public List<BBAttribute> getAvailableKeyAttributeTypes() {
    List<BBAttribute> available = CollectionUtils.arrayList();
    for (BBAttribute att : availableAttributeTypes) {
      if (att.isTimeseries()) {
        available.add(att);
      }
    }
    return available;
  }

  /**
   * @return selectedKeyAttributeTypeId the selectedKeyAttributeTypeId
   */
  public int getSelectedKeyAttributeTypeId() {
    return selectedKeyAttributeTypeId;
  }

  public void setSelectedKeyAttributeTypeId(int selectedKeyAttributeTypeId) {
    this.selectedKeyAttributeTypeId = selectedKeyAttributeTypeId;
  }
}

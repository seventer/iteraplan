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
package de.iteratec.iteraplan.presentation.dialog.common.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.RuntimePeriodDelegate;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * GUI component model for a {@link RuntimePeriod}.
 */
public final class RuntimePeriodComponentModel<T extends RuntimePeriodDelegate> extends AbstractComponentModelBase<T> {

  /** Serialization version. */
  private static final long   serialVersionUID = 518981689718383244L;

  private RuntimePeriod       period;

  private String              startAsString;
  private String              endAsString;

  private final Locale        locale;

  private static final String KEY_LABEL        = "global.productive";

  public RuntimePeriodComponentModel(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
    this.locale = UserContext.getCurrentLocale();
  }

  public void initializeFrom(T source) {
    period = source.getRuntimePeriod();
  }

  public void update() {
    Date start = DateUtils.parseAsDate(startAsString, locale);
    Date end = DateUtils.parseAsDate(endAsString, locale);
    period = new RuntimePeriod(start, end);
  }

  public void configure(T target) {
    target.setRuntimePeriod(period);
  }

  public String getLocalizedLabel() {
    return KEY_LABEL;
  }

  public String getStartAsString() {
    return period == null ? "" : DateUtils.formatAsString(period.getStart(), locale);
  }

  public String getStartDate() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
    return sdf.format(period.getStart());
  }

  public String getEndDate() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
    return sdf.format(period.getEnd());
  }

  public String getEndAsString() {
    return period == null ? "" : DateUtils.formatAsString(period.getEnd(), locale);
  }

  public void setStartAsString(String startAsString) {
    this.startAsString = startAsString;
  }

  public void setEndAsString(String endAsString) {
    this.endAsString = endAsString;
  }

  public void validate(Errors errors) {
    Date start = DateUtils.parseAsDate(startAsString, locale);
    Date end = DateUtils.parseAsDate(endAsString, locale);
    DateUtils.validatePeriod(errors, start, end);
  }

}
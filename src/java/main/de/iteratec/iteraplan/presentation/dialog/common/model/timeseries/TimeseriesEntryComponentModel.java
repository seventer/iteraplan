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
package de.iteratec.iteraplan.presentation.dialog.common.model.timeseries;

import java.util.Date;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


public class TimeseriesEntryComponentModel extends AbstractTimeseriesCMBase<TimeseriesEntry> {
  private static final long                      serialVersionUID = -8439349448658841543L;

  private static final Logger                    LOGGER           = Logger.getIteraplanLogger(TimeseriesEntryComponentModel.class);

  private Type                                   atType;

  private Date                                   date;
  private String                                 dateAsString;
  private TimeseriesAttributeValueComponentModel valueModel;

  protected TimeseriesEntryComponentModel(AttributeType at, ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
    initValueModel(at);
  }

  private void initValueModel(AttributeType at) {
    if (at instanceof EnumAT) {
      valueModel = new TimeseriesEnumSingleValueComponentModel((EnumAT) at, getComponentMode(), getHtmlId() + "_value");
      atType = Type.ENUM;
    }
    else if (at instanceof NumberAT) {
      valueModel = new TimeseriesNumberValueComponentModel(at, getComponentMode(), getHtmlId() + "_value");
      atType = Type.NUMBER;
    }
    else {
      LOGGER.error("Attribute \"{0}\" is of unsupported type \"{1}\".", at, at.getClass().getSimpleName());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
  }

  public void initializeFrom(TimeseriesEntry source) {
    if (source == null) {
      this.date = null;
      this.dateAsString = "";
    }
    else {
      this.date = source.getDate();
      this.dateAsString = DateUtils.formatAsString(date, UserContext.getCurrentLocale());
    }
    this.valueModel.initializeFrom(source);
  }

  public void update() {
    clearErrors();
    dateAsString = dateAsString.trim();
    if (!dateAsString.isEmpty()) {
      date = parseDate();
    }
    else {
      date = null;
    }
    valueModel.update();
  }

  public void configure(TimeseriesEntry target) {
    // nothing to do here, needs to be handled by containing component model during entry instantiation since TimeseriesEntries are immutable
  }

  public void validate(Errors errors) {
    if (dateAsString.isEmpty()) {
      errors.rejectValue("dateAsString", "TIMESERIES_ENTRY_EMPTY_DATE");
    }
    else {
      Date now = new Date();
      Date entryDate = parseDate();
      if (entryDate == null) {
        errors.rejectValue("dateAsString", "INCORRECT_DATE_FORMAT");
      }
      else if (now.before(entryDate)) {
        errors.rejectValue("dateAsString", "TIMESERIES_INVALID_FUTURE_DATE");
      }
    }

    errors.pushNestedPath("valueModel");
    valueModel.validate(errors);
    errors.popNestedPath();
  }

  @Override
  public boolean check() {
    if (date == null) {
      error(IteraplanErrorMessages.TIMESERIES_ENTRY_EMPTY_DATE);
    }
    else if (new Date().before(date)) {
      error(IteraplanErrorMessages.TIMESERIES_INVALID_FUTURE_DATE);
    }
    if (!valueModel.check()) {
      addErrorMessages(valueModel.getErrorMessages());
    }
    return getErrorMessages().isEmpty();
  }

  private Date parseDate() {
    try {
      return DateUtils.parseAsDate(this.dateAsString, UserContext.getCurrentLocale());
    } catch (IteraplanBusinessException ex) {
      error(ex.getLocalizedMessage());
      return null;
    }
  }

  public boolean isEmpty() {
    return dateAsString.isEmpty() && valueModel.isEmpty();
  }

  public Date getDate() {
    return date;
  }

  public String getDateAsString() {
    return dateAsString;
  }

  public void setDateAsString(String dateAsString) {
    this.dateAsString = dateAsString;
  }

  public TimeseriesAttributeValueComponentModel getValueModel() {
    return valueModel;
  }

  public Type getAtType() {
    return atType;
  }

  /**
   * Creates a timeseries entry with the data from this component model.
   * This is necessary because after creation of an timeseries entry, it is immutable, so the
   * usual procedure with component models to create an empty model element and then use the
   * configure-method does not work here.
   * @return new TimeseriesEntry with already set data from this component model
   */
  public TimeseriesEntry createEntry() {
    return new TimeseriesEntry(date, valueModel.getNormalizedValue());
  }

  private enum Type {
    NUMBER, ENUM;
  }
}

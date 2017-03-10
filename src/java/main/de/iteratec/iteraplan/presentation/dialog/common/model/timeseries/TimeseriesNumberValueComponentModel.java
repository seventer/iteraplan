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

import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * Component model for Timeseries NumberAT attribute values.
 */
public class TimeseriesNumberValueComponentModel extends TimeseriesAttributeValueComponentModel {
  private static final long   serialVersionUID = -1451186786842561896L;

  private final AttributeType at;
  private BigDecimal          value;

  protected TimeseriesNumberValueComponentModel(AttributeType at, ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
    this.at = at;
  }

  @Override
  protected void setValueAsStringFromSource(TimeseriesEntry source) {
    setValue(source.getValue(), Locale.ENGLISH);
  }

  private void setValue(String value, Locale locale) {
    if (value == null || value.isEmpty()) {
      this.value = null;
    }
    else {
      BigDecimal numberValue = BigDecimalConverter.parse(value, locale);
      this.value = numberValue;
    }
  }

  private String getValue(Locale locale) {
    if (value == null) {
      return "";
    }
    else {
      return BigDecimalConverter.format(value, locale);
    }
  }

  @Override
  public String getAttributeValueAsString() {
    return getValue(UserContext.getCurrentLocale());
  }

  @Override
  public void setAttributeValueAsString(String value) {
    setValue(value, UserContext.getCurrentLocale());
  }

  public AttributeType getAttributeType() {
    return at;
  }

  /**
   * Only needed by the jsp
   */
  public boolean isOutOfRange() {
    return false;
  }

  @Override
  public void validate(Errors errors) {
    super.validate(errors);
    if (!isEmpty() && !checkNumberFormat()) {
      errors.rejectValue("attributeValueAsString", "INCORRECT_BIGDECIMAL_FORMAT", new Object[] { getAttributeValueAsString() }, "");
    }
  }

  @Override
  public boolean check() {
    if (isEmpty()) {
      error(IteraplanErrorMessages.TIMESERIES_ENTRY_EMPTY_VALUE);
    }
    else {
      checkNumberFormat();
    }
    return getErrorMessages().isEmpty();
  }

  private boolean checkNumberFormat() {
    try {
      BigDecimalConverter.parse(getAttributeValueAsString(), UserContext.getCurrentLocale());
      return true;
    } catch (IteraplanBusinessException ex) {
      error(ex.getLocalizedMessage());
      return false;
    }
  }

  @Override
  String getNormalizedValue() {
    return getValue(Locale.ENGLISH);
  }

}
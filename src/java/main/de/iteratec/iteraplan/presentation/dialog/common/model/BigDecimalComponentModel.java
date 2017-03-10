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

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * Component model for a BigDecimal value. Manages a BigDecimal value for an element of type 'T'. It
 * also provides a label for the BigDecimal value that can be used by the GUI.
 */
public abstract class BigDecimalComponentModel<T> extends AbstractComponentModelBase<T> {
  private static final long serialVersionUID = 5066673601718290318L;

  /** The current value. */
  private BigDecimal        current;

  /** The localized string representation of the current value. */
  private String            currentAsString;

  /** The key of a property to use as label text. */
  private final String      labelKey;

  /**
   * @param componentMode
   * @param htmlId
   * @param labelKey
   *          The key of a property to use as label text for the whole component.
   */
  public BigDecimalComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
    super(componentMode, htmlId);
    this.labelKey = labelKey;
  }

  /**
   * Retrieves the BigDecimal value of the managed property from a model object.
   * 
   * @param source
   *          The model instance whose BigDecimal property should be managed.
   * @return The BigDecimal value to be managed as retrieved from the model object.
   */
  protected abstract BigDecimal getBigDecimalFromElement(T source);

  /**
   * Store the changes into the managed property of the model object.
   * 
   * @param target
   *          The model object to be changed.
   * @param value
   *          The new value for the property.
   */
  protected abstract void setBigDecimalForElement(T target, BigDecimal value);

  public void initializeFrom(T source) throws IteraplanException {
    current = getBigDecimalFromElement(source);
    if (current == null) {
      currentAsString = "";
    }
    else {
      currentAsString = BigDecimalConverter.format(current, true, UserContext.getCurrentLocale());
    }
  }

  public void update() throws IteraplanException {
    if (currentAsString != null && currentAsString.trim().length() > 0) {
      current = BigDecimalConverter.parse(currentAsString, true, UserContext.getCurrentLocale());
    }
    else {
      current = null;
    }
    if (current != null) {
      currentAsString = BigDecimalConverter.format(current, true, UserContext.getCurrentLocale());
    }
    else {
      currentAsString = "";
    }
  }

  public void configure(T target) throws IteraplanException {
    setBigDecimalForElement(target, current);
  }

  public void validate(Errors errors) {
    if (StringUtils.isNotEmpty(currentAsString)) {
      try {
        BigDecimalConverter.parse(currentAsString, UserContext.getCurrentLocale());
      } catch (IteraplanException e) {
        // Could not parse
        errors.rejectValue("currentAsString", "errors.invalidBoundaries",
            new String[] { "<span class=\"errorInline\">" + currentAsString + "</span>" }, "errors.invalidBoundaries");
      }
    }
  }

  public String getCurrentAsString() {
    return currentAsString;
  }

  public void setCurrentAsString(String currentAsString) {
    this.currentAsString = currentAsString;
  }

  public String getLabelKey() {
    return labelKey;
  }

}

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

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.util.StringUtil;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.IteraplanValidationUtils;


/**
 * Manages one String field for the entity 'T'.
 */
public abstract class StringComponentModel<T> extends AbstractComponentModelBase<T> {

  private static final long serialVersionUID = 5700849371483004030L;

  /** The current value. */
  private String            current;

  /** The key of a property to use as a label. */
  private String            labelKey;

  /** Set to true, if an input for this field is mandatory. */
  private boolean           mandatory        = false;

  public StringComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
    super(componentMode, htmlId);
    this.labelKey = labelKey;
  }

  public StringComponentModel(ComponentMode componentMode, String htmlId, String labelKey, boolean mandatory) {
    this(componentMode, htmlId, labelKey);
    this.mandatory = mandatory;
  }

  public void initializeFrom(T element) {
    setCurrent(getStringFromElement(element));
  }

  public void update() throws IteraplanException {
    // nothing to do...
  }

  public void configure(T target) {
    setStringForElement(target, getCurrent());
  }

  public void validate(Errors errors) {
    validate(errors, "current", new String[] { "global.name" });
  }

  /**
   * validation method, that can be used when the standard values for field and errorArgs should be
   * replaced
   */
  public void validate(Errors errors, String field, String[] errorArgs) {
    Object[] params = IteraplanValidationUtils.getLocalizedArgsWithSpanTags(errorArgs, "errorInline");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, "errors.required", params);

    if (getCurrent().length() > Constants.TEXT_SHORT) {
      errors.rejectValue("current", "NAME_TOO_LONG");
    }
  }

  /**
   * validate the description field of the element
   */
  public void validateDescription(Errors errors) {
    if (getCurrent().length() > Constants.TEXT_LONG) {
      errors.rejectValue("current", "TEXT_TOO_LONG");
    }
  }

  public abstract String getStringFromElement(T element);

  public abstract void setStringForElement(T element, String stringToSet);

  public String getCurrent() {
    return current;
  }

  public void setCurrent(String currentValue) {
    this.current = StringUtil.removeIllegalXMLChars(currentValue);
  }

  public String getLabelKey() {
    return labelKey;
  }

  public boolean isMandatory() {
    return mandatory;
  }

}

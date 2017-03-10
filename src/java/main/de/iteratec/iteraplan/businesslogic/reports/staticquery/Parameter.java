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
package de.iteratec.iteraplan.businesslogic.reports.staticquery;

import java.io.Serializable;


/**
 * Abstract base class for a parameter of static queries. It
 * holds the actual value of the concrete parameter object.
 * <p>
 * The class contains the following information:
 * <ul>
 *  <li>The parameter's initial/final value.</li>
 *  <li>The validation logic of the parameter value.</li>
 * </ul>
 */
public abstract class Parameter implements Serializable {

  /**
   * The initial/final value of the parameter object. 
   */
  private String    value;

  /**
   * Defines if the parameter shall be subject to I18N.
   * Defaults to false.
   */
  private Boolean   localized = Boolean.FALSE;

  /**
   * The validator of this parameter object.
   */
  private Validator validator;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Boolean getLocalized() {
    return localized;
  }

  public void setLocalized(Boolean localized) {
    this.localized = localized;
  }

  public Validator getValidator() {
    return validator;
  }

  public void setValidator(Validator validator) {
    this.validator = validator;
  }

  /**
   * This abstract inner class may be used for defining validation
   * code for the parameter encapsulated by the {@link Parameter} 
   * class. It defines a {@link Validator#validate(String)} method, 
   * that may be implemented, in conjunction with the outer class, 
   * as follows:
   * <pre>
   * parameter.setValidator(new Validator() {
   *    public String validate(String value) {
   *        // Validation code goes here.
   *    }
   * });
   * </pre>
   * The calling code might look as follows:
   * <pre>
   * String value = parameter.getValidator().validate(parameter.getValue());
   * </pre> 
   */
  public abstract static class Validator implements Serializable {

    /**
     * This method defines the validation logic for the parameter
     * encapsulated by the enclosing {@link Parameter} class. If the 
     * validation fails, a runtime exception should be thrown from the 
     * body of this method. This is due to the current error handling
     * design of iteraplan, which permits to display an error message
     * to the user, if it is wrapped inside an exception.
     * 
     * @param value
     *    The value encapsulated by the {@link Parameter} object.
     */
    public abstract void validate(String value);
  }

}
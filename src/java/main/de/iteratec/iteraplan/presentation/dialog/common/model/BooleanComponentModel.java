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

import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * Component model for a Boolean value. 
 * 
 * Manages a Boolean value for an element of type 'T'. It also provides
 * a label for the Boolean value that can be used by the GUI.
 */
public abstract class BooleanComponentModel<T> extends AbstractComponentModelBase<T> {

  private static final long serialVersionUID = -4454060093636051303L;

  /** The current value of the boolean choice. */
  private Boolean           current;

  /** The key of a property to use as label for the boolean choice. */
  private final String      labelKey;

  /**
   * @param componentMode
   * @param htmlId
   * @param labelKey The key of a property to use as label text for the whole component.
   */
  public BooleanComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
    super(componentMode, htmlId);
    this.labelKey = labelKey;
  }

  /**
   * Gets the boolean value from the original copy.
   * 
   * @param source The original copy.
   * @return The boolean value of the original copy.
   */
  public abstract Boolean getBooleanFromElement(T source);

  /**
   * Sets the managed boolean value into the working copy.
   * 
   * @param target The working copy
   * @param booleanToSet The managed boolean value that is to be set.
   */
  public abstract void setBooleanForElement(T target, Boolean booleanToSet);

  public void initializeFrom(T source) {
    setCurrent(getBooleanFromElement(source));
  }

  public void update() throws IteraplanException {
    // nothing to do
  }

  public void configure(T target) {
    setBooleanForElement(target, getCurrent());
  }

  public void validate(Errors errors) {
    // do nothing
  }

  public Boolean getCurrent() {
    return current;
  }

  public void setCurrent(Boolean current) {
    this.current = current;
  }

  public String getLabelKey() {
    return labelKey;
  }

}

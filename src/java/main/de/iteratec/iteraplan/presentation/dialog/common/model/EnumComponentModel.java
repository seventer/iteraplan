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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * The Type 'T' represents the entity that has an enum of Type 'E' as member. 
 */
public abstract class EnumComponentModel<T, E extends Enum<E>> extends AbstractComponentModelBase<T> {

  private static final long serialVersionUID = -1030636848411456019L;

  /** The currently assigned enum instance. Never null. */
  private E                 current;

  /** The string value to show on the GUI. */
  private String            currentAsString;

  /** The key of the property to show as label. */
  private String            labelKey         = null;

  /**
   * @param componentMode
   * @param htmlId
   * @param labelKey The key of a property to use as label text for the whole component.
   */
  public EnumComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
    super(componentMode, htmlId);
    this.labelKey = labelKey;
  }

  /**
   * Returns the current enum instance from the given source entity.
   * 
   * @param source The enttiy that has the enum as member. 
   * @return The Enum retrieved from the given source entity. 
   */
  protected abstract E getEnumFromElement(T source);

  /**
   * Sets the given enum instance for the given target entity.
   * 
   * @param target The target entity that has the enum as member.
   * @param currentEnum The current enum instance to set.
   */
  protected abstract void setEnumForElement(T target, E currentEnum);

  /**
   * Maps the string value of an enum to its enum instance. If no 
   * suitable enum can be found, null is returned. 
   * 
   * @param value The string representation of an enum
   * @return The enum instance that matches the string value.
   */
  protected abstract E getEnumByString(String value);

  /**
   * @return Returns the List of all possible values for the enum.
   */
  protected abstract List<E> getValues();

  public void initializeFrom(T source) throws IteraplanException {
    setCurrent(getEnumFromElement(source));
    setCurrentAsString(current.toString());
  }

  public void update() throws IteraplanException {
    if (getComponentMode() != ComponentMode.READ) {
      setCurrent(getEnumByString(currentAsString));
      setCurrentAsString(current.toString());
    }
  }

  public void configure(T target) throws IteraplanException {
    if (getComponentMode() != ComponentMode.READ) {
      setEnumForElement(target, getCurrent());
    }
  }

  public void validate(Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAsString", "errors.required");
  }

  private void setCurrent(E current) {
    assert current != null;
    this.current = current;
  }

  private E getCurrent() {
    return this.current;
  }

  public String getCurrentAsString() {
    return currentAsString;
  }

  public void setCurrentAsString(String currentAsString) {
    this.currentAsString = currentAsString;
  }

  /**
   * @return Returns the key of the message property to use as a label. 
   * May be null.
   */
  public String getLabelKey() {
    return labelKey;
  }

  /**
   * @return Returns a list of all enum keys.
   */
  public List<String> getAvailableEnumKeys() {
    List<String> result = new ArrayList<String>();
    for (Iterator<E> it = getValues().iterator(); it.hasNext();) {
      E element = it.next();
      String toString = element.toString();
      result.add(toString);
    }
    return result;
  }

}

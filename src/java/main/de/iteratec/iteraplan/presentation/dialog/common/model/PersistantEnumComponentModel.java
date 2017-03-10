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

import java.util.Arrays;
import java.util.List;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.util.StringEnumReflectionHelper;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


public abstract class PersistantEnumComponentModel<T, E extends Enum<E>> extends AbstractComponentModelBase<T> {

  private static final long serialVersionUID = 5253395792701024015L;

  /** The currently selected enum instance. Never null, since it is an enum. */
  private E                 current;

  /** The text string to show on the GUI. */
  private String            currentAsString;

  /** The key of the property to show as label. */
  private String            labelKey         = null;

  /**
   * @param componentMode
   * @param htmlId
   * @param labelKey
   *          The key of a property to use as label text for the whole component.
   */
  public PersistantEnumComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
    super(componentMode, htmlId);
    this.labelKey = labelKey;
  }

  public void initializeFrom(T source) throws IteraplanException {
    setCurrent(getEnumFromElement(source));
    setCurrentAsString(current.toString());
  }

  @SuppressWarnings("unchecked")
  public void update() throws IteraplanException {
    String name = StringEnumReflectionHelper.getNameFromValue(current.getClass(), currentAsString);
    setCurrent((E) Enum.valueOf(current.getClass(), name));
    setCurrentAsString(current.toString());
  }

  public void configure(T target) throws IteraplanException {
    setEnumForElement(target, getCurrent());
  }

  protected E getCurrent() {
    return this.current;
  }

  private void setCurrent(E current) {
    this.current = current;
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

  @SuppressWarnings("unchecked")
  public List<String> getAvailableEnumKeys() {
    String[] values = StringEnumReflectionHelper.getStringValues(current.getClass());
    return Arrays.asList(values);
  }

  /**
   * Returns the {@link Enum} instance for the specified {@code source} Entity.
   * 
   * @param source the source entity to get the enum for
   * @return the enum instance 
   */
  protected abstract E getEnumFromElement(T source);

  /**
   * Sets the enum for the {@code target} entity.
   * 
   * @param target the target entity
   * @param currentEnum the enum value to set
   */
  protected abstract void setEnumForElement(T target, E currentEnum);

  public void validate(Errors errors) {
    // nothing to do (yet)
  }
}

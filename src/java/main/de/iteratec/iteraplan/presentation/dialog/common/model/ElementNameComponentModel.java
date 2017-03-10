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
import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.IteraplanValidationUtils;


/**
 * Manages a name field for the entity 'T'. In addition to a {@link StringComponentModel}, it
 * carries a flag to indicate whether it represents a virtual element.
 * <p>
 * Based on that flag, JSPs can modify the UI to offer restricted options on virtual elements.
 */
public abstract class ElementNameComponentModel<T extends IdEntity> extends StringComponentModel<T> {

  private boolean virtualElementSelected = false;
  
  private boolean defaultAttributeGroupSelected = false;

  public ElementNameComponentModel(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId, "global.name");
  }

  public ElementNameComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
    super(componentMode, htmlId, labelKey);
  }

  /**
   * Returns the name of the element. This is a more handy synonym for {@link #getCurrent()}
   * 
   * @return the element Name
   */
  public String getName() {
    return getCurrent();
  }

  /**
   * Sets the name of this element. This is a more handy synonym for {@link #setCurrent(String)}
   */
  public void setName(String name) {
    this.setCurrent(name);
  }

  /**
   * Indicates whether the virtual element of a hierarchical building block has been selected.
   * 
   * @return see method description
   */
  public boolean isVirtualElementSelected() {
    return virtualElementSelected;
  }

  public void setVirtualElementSelected(boolean virtualElementSelected) {
    this.virtualElementSelected = virtualElementSelected;
  }

  public boolean isDefaultAttributeGroupSelected() {
    return defaultAttributeGroupSelected;
  }

  public void setDefaultAttributeGroupSelected(boolean defaultAttributeGroupSelected) {
    this.defaultAttributeGroupSelected = defaultAttributeGroupSelected;
  }

  @Override
  public void validate(Errors errors) {
    Object[] params = IteraplanValidationUtils.getLocalizedArgsWithSpanTags("global.name", "errorInline");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "errors.required", params);

    if (getCurrent().length() > Constants.TEXT_SHORT) {
      errors.rejectValue("name", "NAME_TOO_LONG");
    }
  }

}

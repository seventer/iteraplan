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
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


public abstract class TakeOverMetaComponentModel<T extends BuildingBlock> extends AbstractComponentModelBase<T> {

  private static final long serialVersionUID = -5575507834165573479L;

  private T                 sourceInstance;

  /** The current value of the boolean choice. */
  private boolean           value;

  /** The key of a property to use as label for the boolean choice. */
  private final String      labelKey;

  /**
   * Constructor.
   * 
   * @param componentMode
   *          The state of the component.
   * @param htmlId
   *          The id to use in order to identify the corresponding HTML code.
   * @param labelKey
   *          The i18n key of to describe the component on the GUI
   * @param initallySelected
   *          Iff true, the choice is true by default. Otherwise, it is false.
   */
  public TakeOverMetaComponentModel(ComponentMode componentMode, String htmlId, String labelKey, boolean initallySelected) {
    super(componentMode, htmlId);
    this.labelKey = labelKey;
    value = initallySelected;
  }

  /**
   * Configure the target instance with the values form the initial source instance.
   * 
   * @param target
   *          The instance to configure
   * @param source
   *          The instance to use as a template.
   * @throws IteraplanTechnicalException
   *           If the configuration of the target instance was not successful.
   */
  protected abstract void configure(T target, T source) throws IteraplanTechnicalException;

  public void initializeFrom(T source) throws IteraplanException {
    sourceInstance = source;
  }

  public void update() throws IteraplanException {
    // nothing to do...
  }

  public void configure(T target) throws IteraplanException {
    if (value) {
      configure(target, sourceInstance);
    }
  }

  public boolean isValue() {
    return value;
  }

  public void setValue(boolean value) {
    this.value = value;
  }

  public String getLabelKey() {
    return labelKey;
  }

  public void validate(Errors errors) {
    // nothing to do (yet)
  }
}

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

import java.io.Serializable;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * An interface that should be implemented by all GUI model beans that back JSP pages.
 * It defines methods that should be called at the beginning, during and
 * at the end of business transactions. A component model should 
 * <ul>
 *  <li>Store information that is to be displayed to the user.</li>
 *  <li>Keep track of the changes made by the user during the transaction.</li>
 *  <li>Write the changes made during the transaction into a new instance of the
 *      managed element. This is done at the end of the transaction.</li>
 * </ul>
 * Component models are meant to be nested. The methods defined by this interface
 * should be called for nested component models as needed.
 * <br/>
 * For each component model there should be one or more JSPs that render the data
 * held by the component model as needed by the GUI.
 * @param <T> The element that is managed by the component model.
 */
public interface ComponentModel<T> extends Serializable {

  /**
   * Initialize the ComponentModel with the state of the given source Object.
   *  
   * All Collections necessary for the GUI should be initialized as well.
   * This method should be called at the beginning of a business transaction. 
   * 
   * @param source The object to use in order to initialize the ComponentModel
   */
  void initializeFrom(T source);

  /**
   * Updates the ComponentModel and its GUI Collections within a business transaction.
   */
  void update();

  /**
   * Configure (change) the given target Object with the state of the ComponentModel.
   * 
   * @param target The target object to configure with the ComponentModel state.
   */
  void configure(T target);

  /**
   * Returns the mode of the ComponentModel.
   * Each component model has an associated mode in which it is executed {@link ComponentMode}.
   *  
   * @return The mode of the ComponentModel.
   */
  ComponentMode getComponentMode();

  /**
   * Validates the current component state.
   * Any errors which have been found are stored into the {@link Errors} object.  
   * @param errors The Spring error message context
   */
  void validate(Errors errors);
}

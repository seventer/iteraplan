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

import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;


/**
 * A base class for timeseries component models, providing a basic error reporting functionality
 * since our standard approach cannot easily be used for the modal dialogs the timeseries are
 * to be displayed and edited in 
 */
public abstract class AbstractTimeseriesCMBase<T> extends AbstractComponentModelBase<T> {
  private static final long serialVersionUID = -4048344302454116174L;

  private final Set<String> errorMessages;

  protected AbstractTimeseriesCMBase(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
    errorMessages = Sets.newHashSet();
  }

  protected AbstractTimeseriesCMBase(ComponentMode componentMode) {
    super(componentMode);
    errorMessages = Sets.newHashSet();
  }

  public Set<String> getErrorMessages() {
    return errorMessages;
  }

  public void addErrorMessages(Set<String> messages) {
    this.errorMessages.addAll(messages);
  }

  /**
   * To be called after {@link #update()} to validate the component model.
   * Used within the modal workflow, as opposed to the validate method which will be
   * called when closing the modal dialog (see webflow definitions).
   * Adds error messages if necessary.
   * @return True if the component model is valid, false if not.
   */
  public abstract boolean check();

  /**
   * Adds a localized error message
   * @param errorCode
   *          The error code according to {@link IteraplanErrorMessages}
   * @param params
   *          Optional parameters for the error message
   */
  protected void error(int errorCode, Object... params) {
    errorMessages.add(IteraplanErrorMessages.getErrorMessage(errorCode, params, UserContext.getCurrentLocale()));
  }

  /**
   * Directly adds a message to error messages.
   * @param message
   *          The message to add
   */
  protected void error(String message) {
    errorMessages.add(message);
  }

  /**
   * Clears all error messages
   */
  protected void clearErrors() {
    errorMessages.clear();
  }

}
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
package de.iteratec.iteraplan.presentation.dialog;

import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;


public interface CommonFrontendService {

  /**
   * Builds a Flow label for the given String. If the provided label is null, the localized String
   * for 'global.name.unnamed' will be returned.
   * 
   * @param label
   * @return the Label that will be displayed in the menu
   */
  String getFlowLabel(String label);

  /**
   * Returns the name of the entity this service is responsible for.
   * @return the name of the current entity, equals flowID without the "/start".
   */
  String getEntityName();

  /**
   * Add the active dialog to the list of dialogs currently being edited. This will highlight the
   * entry in the menu.
   * 
   * @param flowContext
   */
  void enterEditMode(FlowExecutionContext flowContext);

  /**
   * Updates the GUI state to reflect that this flow is no longer in edit mode.
   * 
   * @param flowContext
   */
  void leaveEditMode(FlowExecutionContext flowContext);

  /**
   * This method closes all existing Flows of the corresponding FrontendService. The Flows are
   * removed programmatically from the flowRegistry and the Entries are removed from the GuiContext
   * and thus from the Menu.
   * 
   * @param context
   *          the RequestContext
   */
  void closeAllFlows(RequestContext context);
}

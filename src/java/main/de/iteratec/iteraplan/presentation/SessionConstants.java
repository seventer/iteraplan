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
package de.iteratec.iteraplan.presentation;

/**
 * This class contains constants under which objects are stored in the
 * {@link javax.servlet.http.HttpServletRequest} or the {@link javax.servlet.http.HttpSession}.
 */
public final class SessionConstants {

  /** Stores the UserContext. */
  public static final String USER_CONTEXT           = "userContext";

  /** Stores the GuiContext. */
  public static final String GUI_CONTEXT            = "guiContext";

  /** Stores the login name of the currently logged in user as a String . */
  public static final String LOGGED_IN_USER_LOGIN   = "loggedInUserLogin";

  /** Stores the roles of the currently logged in user as a Set of Strings. */
  public static final String LOGGED_IN_USER_ROLES   = "loggedInUserRoles";

  /** Stores the ElasticMiContext */
  public static final String ELASTIC_MI_CONTEXT     = "elasticMiContext";

  /**
   * Stores a Boolean in a dialog memory that defines wether the errors stored under
   * Global.ERROR_KEY are removed or not when calling a command. This is needed for keeping errors
   * that occured when a validation error occurred when updating a memory in the OnPassivate method
   * and that have to be displayed the next time the dialog is displayed.
   */
  /** Stores the flow label into the flow context */
  public static final String FLOW_LABEL_KEY         = "FLOW_LABEL";

  /** Stores the ID of the entity (e.g. a building block) which is loaded in a flow. */
  public static final String ENTITY_ID_KEY          = "id";

  /** HTTP Parameter name that contains the name of a transition that shall be triggered. */
  public static final String FLOW_EVENT_ID          = "_eventId";

  public static final String FLOW_EXECUTION_KEY     = "execution";

  public static final String MVC_SEARCH_RESULT_LIST = "resultList";

  public static final String MVC_BB_TYPE            = "bbt";

  public static final String DIALOG_MEMORY_LABEL    = "dialogMemory";

  public static final String APPLICATION_URL_LABEL  = "iteraplanApplicationUrl";

  public static final String MVC_ERROR_MESSAGES_KEY = "iteraplanMvcErrorMessages";

  /** empty private constructor as all fields are static */
  private SessionConstants() {
    // Hide Constructor as class only provides static fields.
  }

}

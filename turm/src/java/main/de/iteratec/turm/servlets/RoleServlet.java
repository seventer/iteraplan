/*
 * iTURM is a User and Roles Management web application developed by iteratec, GmbH
 * Copyright (C) 2008 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
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
 * You can contact iteratec GmbH headquarters at Inselkammerstraße 4
 * 82008 München - Unterhaching, Germany, or at email address info@iteratec.de.
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
package de.iteratec.turm.servlets;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import de.iteratec.turm.common.Logger;
import de.iteratec.turm.exceptions.DaoException;
import de.iteratec.turm.exceptions.TurmException;
import de.iteratec.turm.exceptions.UniqueConstaintException;
import de.iteratec.turm.exceptions.UserInputException;
import de.iteratec.turm.messages.TurmMessage;
import de.iteratec.turm.model.Role;
import de.iteratec.turm.model.RoleContainer;
import de.iteratec.turm.model.User;


/**
 * This servlet contains functionality to create, update and delete a role.
 * 
 * Depending on the {@link #ACTION} that is passed as a parameter, the servlet executes
 * the corresponding code. Finally, it always forwards to the same JSP which displays
 * the outcome of the action.
 */
public class RoleServlet extends TurmServlet {

  private static final long serialVersionUID = -7658383898633550841L;

  private static final Logger LOGGER              = Logger.getLogger(RoleServlet.class);

  /** The JSP this servlet always forwards to. Can be configured via web.xml. */
  private String              roleJSP             = "/jsp/roles/Roles.jsp";

  // Keys for request or session attributes.

  /** Holds a list of Role */
  private static final String ROLE_LIST           = "roleList";
  /** Holds a list of User */
  private static final String AVAILABLE_USER_LIST = "availableUserList";
  /** Holds a list of User */
  private static final String CONNECTED_USER_LIST = "connectedUserList";
  /** Holds a RoleContainer */
  private static final String CREATE_EDIT_ROLE    = "createEditRole";
  /** Holds a Role */
  private static final String EDITED_ROLE         = "currentlyEditedRole";

  // Keys for request parameters

  /** Must be an Integer */
  private static final String ROLE_ID             = "roleId";
  /** Must be one of ACTION_DELETE, ACTION_CREATE, ACTION_PRE_UPDATE or ACTION_UPDATE */
  private static final String ACTION              = "action";
  /** Possible parameter value for ACTION */
  private static final String ACTION_DELETE       = "deleteRole";
  /** Possible parameter value for ACTION */
  private static final String ACTION_CREATE       = "createRole";
  /** Possible parameter value for ACTION */
  private static final String ACTION_PRE_UPDATE   = "preUpdateRole";
  /** Possible parameter value for ACTION */
  private static final String ACTION_UPDATE       = "updateRole";

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    if (config.getInitParameter("RoleJsp") != null) {
      roleJSP = config.getInitParameter("RoleJsp");
    }
    LOGGER.debug("Using JSP " + roleJSP);
  }

  @Override
  protected void doTurmGet(HttpServletRequest request) throws TurmException {
    setFreshRoleList(request);
    setFreshUserList(request);
  }

  @Override
  protected void doTurmPost(HttpServletRequest request) throws TurmException {
    String action = request.getParameter(ACTION);
    if (ACTION_DELETE.equals(action)) {
      deleteRole(request);
      setFreshRoleList(request);
      setFreshUserList(request);
    }
    else if (ACTION_CREATE.equals(action)) {
      createRole(request);
      setFreshRoleList(request);
      setFreshUserList(request);
    }
    else if (ACTION_PRE_UPDATE.equals(action)) {
      prepareForUpdateRole(request);
    }
    else if (ACTION_UPDATE.equals(action)) {
      updateRole(request);
      setFreshRoleList(request);
      setFreshUserList(request);
    }
  }

  @Override
  protected String getJsp() {
    return roleJSP;
  }

  /**
   * Loads all roles from the database and stores the list in the session.
   * 
   * @param request The current Servlet request.
   * @throws DaoException If there was a problem loading the roles.
   */
  private void setFreshRoleList(HttpServletRequest request) throws DaoException {
    request.getSession().setAttribute(ROLE_LIST, getRoleDao().loadAllRoles());
  }

  /**
   * Loads all users from the database and stores the list in the session.
   * 
   * @param request The current Servlet request.
   * @throws DaoException If there was a problem loading the users.
   */
  private void setFreshUserList(HttpServletRequest request) throws DaoException {
    request.getSession().setAttribute(AVAILABLE_USER_LIST, getUserDao().loadAllUsers());
  }

  /**
   * Creates a role in the database.
   * 
   * The role data is expected to be found in the request parameters.
   * 
   * @param request The current Servlet request.
   * @throws TurmException If the role data could not be fetched from the
   *                       request, the role data is not valid, or the
   *                       role could not be created in the database.
   */
  private void createRole(HttpServletRequest request) throws TurmException {
    RoleContainer newRole = new RoleContainer();
    populate(request, newRole);
    storeUserInputInRequest(request, newRole);
    newRole.validateAll();
    try {
      getRoleDao().createNewRole(newRole);
    } catch (UniqueConstaintException e) {
      throw new UserInputException("error.roleNameExists", newRole.getRoleName());
    }
    saveMessages(new TurmMessage("message.roleCreated", newRole.getRoleName()), request);
    cleanupUserInputInRequest(request);
  }

  /**
   * Prepares the structures that hold the existing role data that will be displayed
   * on the role edit page. For example, the users which do not yet have the role are
   * fetched from the database.
   * 
   * @param request The current Servlet request.
   * @throws DaoException If the role data could not be retrieved from the database.
   */
  private void prepareForUpdateRole(HttpServletRequest request) throws DaoException {
    Integer roleId = Integer.valueOf(request.getParameter(ROLE_ID));
    Role role = getRoleDao().loadRoleById(roleId);
    RoleContainer uc = new RoleContainer(role);
    request.setAttribute(CREATE_EDIT_ROLE, uc);
    List<User> availableUsers = sortOutIdEntities(role.getUsers(), getUserDao().loadAllUsers());
    request.setAttribute(CONNECTED_USER_LIST, role.getUsers());
    request.setAttribute(AVAILABLE_USER_LIST, availableUsers);
    request.getSession().setAttribute(EDITED_ROLE, role);
  }

  /**
   * Update an existing role in the database.
   * 
   * The role data is expected to be found in the request parameters.
   * 
   * @param request The current Servlet request.
   * @throws TurmException If the role data could not be fetched from the request,
   *                       the role data is not valid, or if the role could not be
   *                       updated in the database.
   */
  private void updateRole(HttpServletRequest request) throws TurmException {
    RoleContainer roleToUpdate = new RoleContainer();
    populate(request, roleToUpdate);
    // save input in request, so that it can be displayed again in case of an error.
    storeUserInputInRequest(request, roleToUpdate);
    // set id and ol version
    Role originalRole = (Role) request.getSession().getAttribute(EDITED_ROLE);
    roleToUpdate.setId(originalRole.getId());
    roleToUpdate.setOlVersion(originalRole.getOlVersion());
    // do the update
    roleToUpdate.validateAll();
    try {
      getRoleDao().updateRole(roleToUpdate);
    } catch (UniqueConstaintException e) {
      throw new UserInputException("error.roleNameExists", roleToUpdate.getRoleName());
    }
    saveMessages(new TurmMessage("message.roleUpdated", roleToUpdate.getRoleName()), request);
    // cleanup
    request.getSession().removeAttribute(EDITED_ROLE);
    cleanupUserInputInRequest(request);
  }

  /**
   * Removes temporarily stored role information from the request.
   * 
   * @param request The current Servlet request.
   * @see #prepareForUpdateRole(HttpServletRequest)
   */
  private void cleanupUserInputInRequest(HttpServletRequest request) {
    request.removeAttribute(CREATE_EDIT_ROLE);
    request.removeAttribute(CONNECTED_USER_LIST);
    request.removeAttribute(AVAILABLE_USER_LIST);
  }

  /**
   * Saves role data in the request.
   * 
   * When editing or creating a role, the data the user entered has to be saved in
   * the request, so that when an error occurs, the entered data can be displayed
   * again. Otherwise the user would have to reenter all the data. This method is
   * called at the beginning of an update or create role operation.
   * 
   * @param request The current Servlet request.
   * @param user The Java Bean that holds the role data
   * @throws DaoException If the users could not be loaded from the database.
   */
  private void storeUserInputInRequest(HttpServletRequest request, RoleContainer roleToUpdate) throws DaoException {
    request.setAttribute(CREATE_EDIT_ROLE, roleToUpdate);
    List<User> connectedUsers = null;
    if (roleToUpdate.getUserIds() == null || roleToUpdate.getUserIds().length == 0) {
      connectedUsers = new ArrayList<User>();
    }
    else {
      connectedUsers = getUserDao().loadUsersByIdStrings(roleToUpdate.getUserIds());
    }
    request.setAttribute(CONNECTED_USER_LIST, connectedUsers);
    request.setAttribute(AVAILABLE_USER_LIST, sortOutIdEntities(connectedUsers, getUserDao().loadAllUsers()));
  }

  /**
   * Deletes an existing role from the database.
   * 
   * The id of the role is expected to be found in the request parameters
   * 
   * @param request The current Servlet request.
   * @throws DaoException If the role could not be deleted.
   */
  private void deleteRole(HttpServletRequest request) throws DaoException {
    Integer roleId = Integer.valueOf(request.getParameter(ROLE_ID));
    getRoleDao().deleteRole(roleId);
    saveMessages(new TurmMessage("message.roleDeleted"), request);
  }

}
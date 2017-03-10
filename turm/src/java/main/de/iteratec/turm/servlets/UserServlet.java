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
import de.iteratec.turm.common.TurmProperties;
import de.iteratec.turm.exceptions.DaoException;
import de.iteratec.turm.exceptions.TurmException;
import de.iteratec.turm.exceptions.UniqueConstaintException;
import de.iteratec.turm.exceptions.UserInputException;
import de.iteratec.turm.messages.TurmMessage;
import de.iteratec.turm.model.Role;
import de.iteratec.turm.model.User;
import de.iteratec.turm.model.UserContainer;


/**
 * This servlet contains functionality to create, update and delete a user.
 * 
 * Depending on the {@link #ACTION} that is passed as a parameter, the servlet executes
 * the corresponding code. Finally, it always forwards to the same JSP which displays
 * the outcome of the action.
 */
public class UserServlet extends TurmServlet {

  private static final long serialVersionUID = -7859021701880585838L;

  private static final Logger LOGGER              = Logger.getLogger(UserServlet.class);

  /** The JSP this servlet always forwards to. Can be configured via web.xml. */
  private String              userJSP             = "/jsp/users/Users.jsp";

  // Keys for request or session attributes.

  /** Holds a list of User */
  private static final String USER_LIST           = "userList";
  /** Holds a list of Role */
  private static final String AVAILABLE_ROLE_LIST = "availableRoleList";
  /** Holds a list of Role */
  private static final String CONNECTED_ROLE_LIST = "connectedRoleList";
  /** Holds a UserContainer */
  private static final String CREATE_EDIT_USER    = "createEditUser";
  /** Holds a User */
  private static final String EDITED_USER         = "currentlyEditedUser";
  /** Holds a boolean: enabled or disabled */
  private static final String PASSWORD_MGMT_DISABLED = "isPasswordMgmtDisabled";

  // Keys for request parameters

  /** Must be an Integer */
  private static final String USER_ID             = "userId";
  /** Must be one of ACTION_DELETE, ACTION_CREATE, ACTION_PRE_UPDATE or ACTION_UPDATE */
  private static final String ACTION              = "action";
  /** Possible parameter value for ACTION */
  private static final String ACTION_DELETE       = "deleteUser";
  /** Possible parameter value for ACTION */
  private static final String ACTION_CREATE       = "createUser";
  /** Possible parameter value for ACTION */
  private static final String ACTION_PRE_UPDATE   = "preUpdateUser";
  /** Possible parameter value for ACTION */
  private static final String ACTION_UPDATE       = "updateUser";

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    if (config.getInitParameter("UserJsp") != null) {
      userJSP = config.getInitParameter("UserJsp");
    }
    LOGGER.debug("Using JSP " + userJSP);
  }

  @Override
  protected void doTurmGet(HttpServletRequest request) throws DaoException {
    setPasswordMgmtStatus(request);

    setFreshUserList(request);
    setFreshRoleList(request);
  }

  @Override
  protected void doTurmPost(HttpServletRequest request) throws TurmException {
    setPasswordMgmtStatus(request);

    String action = request.getParameter(ACTION);
    if (ACTION_DELETE.equals(action)) {
      deleteUser(request);
      setFreshRoleList(request);
      setFreshUserList(request);
    }
    else if (ACTION_CREATE.equals(action)) {
      createUser(request);
      setFreshRoleList(request);
      setFreshUserList(request);
    }
    else if (ACTION_PRE_UPDATE.equals(action)) {
      prepareForUpdateUser(request);
    }
    else if (ACTION_UPDATE.equals(action)) {
      updateUser(request);
      setFreshRoleList(request);
      setFreshUserList(request);
    }
  }

  @Override
  protected String getJsp() {
    return userJSP;
  }

  /**
   * Delete a user from the database.
   * 
   * The id of the user is expected to be found in the request parameters.
   * 
   * @param request The current Servlet request.
   * @throws DaoException If the user could not be deleted from the database.
   */
  private void deleteUser(HttpServletRequest request) throws DaoException {
    Integer userId = Integer.valueOf(request.getParameter(USER_ID));
    getUserDao().deleteUser(userId);
    saveMessages(new TurmMessage("message.userDeleted"), request);
  }

  /**
   * Create a user in the database.
   * 
   * The user data is expected to be found in the request parameters.
   * 
   * @param request The current Servlet request.
   * @throws TurmException If the user data could not be fetched from the
   *                          request, the user data is not valid, or the
   *                          user could not be created in the database.
   */
  private void createUser(HttpServletRequest request) throws TurmException {
    UserContainer newUser = new UserContainer();
    populate(request, newUser);
    storeUserInputInRequest(request, newUser);
    newUser.validateAll();
    try {
      getUserDao().createNewUser(newUser);
    } catch (UniqueConstaintException e) {
      throw new UserInputException("error.userLoginExists", newUser.getLoginName());
    }
    saveMessages(new TurmMessage("message.userCreated", newUser.getLoginName()), request);
    cleanupUserInputInRequest(request);
  }

  /**
   * Loads all users from the database and stores the list in the session.
   * 
   * @param request The current Servlet request.
   * @throws DaoException If there was a problem loading the users.
   */
  private void setFreshUserList(HttpServletRequest request) throws DaoException {
    request.getSession().setAttribute(USER_LIST, getUserDao().loadAllUsers());
  }

  /**
   * Loads all roles from the database and stores the list in the session.
   * 
   * @param request The current Servlet request.
   * @throws DaoException If there was a problem loading the roles.
   */
  private void setFreshRoleList(HttpServletRequest request) throws DaoException {
    request.getSession().setAttribute(AVAILABLE_ROLE_LIST, getRoleDao().loadAllRoles());
  }

  /**
   * Loads the value of the property {@link TurmProperties.TURM_PASSWORD_MGMT_DISABLED} and stores
   * it into the session.
   * 
   * @param request
   *          The current Servlet request.
   */
  private void setPasswordMgmtStatus(HttpServletRequest request) {
    String propertyValue = TurmProperties.getProperties().getProperty(TurmProperties.TURM_PASSWORD_MGMT_DISABLED);
    request.getSession().setAttribute(PASSWORD_MGMT_DISABLED, Boolean.parseBoolean(propertyValue));
  }

  /**
   * Prepares the structures that hold the existing user data that will be displayed
   * on the user edit page. For example, the roles the user does not have yet are
   * fetched from the database.
   * 
   * @param request The current Servlet request.
   * @throws DaoException If the user data could not be retrieved from the database.
   */
  private void prepareForUpdateUser(HttpServletRequest request) throws DaoException {
    Integer userId = Integer.valueOf(request.getParameter(USER_ID));
    User user = getUserDao().loadUserById(userId);
    UserContainer uc = new UserContainer(user);
    request.setAttribute(CREATE_EDIT_USER, uc);
    List<Role> availableRoles = sortOutIdEntities(user.getRoles(), getRoleDao().loadAllRoles());
    request.setAttribute(CONNECTED_ROLE_LIST, user.getRoles());
    request.setAttribute(AVAILABLE_ROLE_LIST, availableRoles);
    request.getSession().setAttribute(EDITED_USER, user);
  }

  /**
   * Update an existing user in the database.
   * 
   * The user data is expected to be found in the request parameters.
   * 
   * @param request The current Servlet request.
   * @throws TurmException If the user data could not be fetched from the request,
   *                       the user data is not valid, or if the user could not be
   *                       updated in the database.
   */
  private void updateUser(HttpServletRequest request) throws TurmException {
    UserContainer userToUpdate = new UserContainer();
    populate(request, userToUpdate);
    storeUserInputInRequest(request, userToUpdate);
    // set id and ol version
    User originalUser = (User) request.getSession().getAttribute(EDITED_USER);
    userToUpdate.setId(originalUser.getId());
    userToUpdate.setOlVersion(originalUser.getOlVersion());
    // do the update
    userToUpdate.validateBasicFields();
    try {
      getUserDao().updateUser(userToUpdate);
    } catch (UniqueConstaintException e) {
      throw new UserInputException("error.userLoginExists", userToUpdate.getLoginName());
    }
    saveMessages(new TurmMessage("message.userUpdated", userToUpdate.getLoginName()), request);
    // cleanup
    request.getSession().removeAttribute(EDITED_USER);
    cleanupUserInputInRequest(request);
  }

  /**
   * Saves user data in the request.
   * 
   * When editing or creating a user, the data the user entered has to be saved in
   * the request, so that when an error occurs, the entered data can be displayed
   * again. Otherwise the user would have to reenter all the data. This method is
   * called at the beginning of an update or create user operation.
   * 
   * @param request The current Servlet request.
   * @param user The Java Bean that holds the user data
   * @throws DaoException If the roles could not be loaded from the database.
   */
  private void storeUserInputInRequest(HttpServletRequest request, UserContainer user) throws DaoException {
    request.setAttribute(CREATE_EDIT_USER, user);
    List<Role> connectedRoles = null;
    if (user.getRoleIds() == null || user.getRoleIds().length == 0) {
      connectedRoles = new ArrayList<Role>();
    }
    else {
      connectedRoles = getRoleDao().loadRolesByIdStrings(user.getRoleIds());
    }
    request.setAttribute(CONNECTED_ROLE_LIST, connectedRoles);
    request.setAttribute(AVAILABLE_ROLE_LIST, sortOutIdEntities(connectedRoles, getRoleDao().loadAllRoles()));
  }

  /**
   * Removes temporarily stored user information from the request.
   * 
   * @param request The current Servlet request.
   * @see #storeUserInputInRequest(HttpServletRequest, UserContainer)
   */
  private void cleanupUserInputInRequest(HttpServletRequest request) {
    request.removeAttribute(CREATE_EDIT_USER);
    request.removeAttribute(CONNECTED_ROLE_LIST);
    request.removeAttribute(AVAILABLE_ROLE_LIST);
  }

}

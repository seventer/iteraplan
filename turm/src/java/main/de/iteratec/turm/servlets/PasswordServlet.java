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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import de.iteratec.turm.common.Logger;
import de.iteratec.turm.common.TurmProperties;
import de.iteratec.turm.exceptions.DaoException;
import de.iteratec.turm.exceptions.TurmException;
import de.iteratec.turm.messages.TurmMessage;
import de.iteratec.turm.model.User;
import de.iteratec.turm.model.UserContainer;


/**
 * This servlet contains functionality to change the password of an existing user.
 * 
 * In order to change the password the user has to enter the current password.
 */
public class PasswordServlet extends TurmServlet {

  private static final long serialVersionUID = -8544932294029067085L;

  private static final Logger LOGGER                 = Logger.getLogger(PasswordServlet.class);

  /** The JSP this servlet always forwards to. Can be configured via web.xml. */
  private String              passwordJSP            = "/jsp/password/UserChangePassword.jsp";

  // request parameters

  /** A message key that is used to display the reason for the change (e.g. message.passwordExpired) */
  private static final String PASSWORD_CHANGE_REASON = "passwordChangeReason";
  /** The url this servlet will forward to upon successfully changing the password. (optional) */
  private static final String REFERRER_URL = "referrerUrl";

  /** Possible parameter value for ACTION */
  private static final String ACTION_CHANGE_PASSWORD   = "changePassword";
  /** Must be an Integer */
  private static final String USER_ID                  = "userId";
  /** Holds a UserContainer */
  private static final String USER_CHANGE_PASSWORD     = "userChangePassword";

  /** Holds a UserContainer */
  private static final String CURRENT_LOGIN_NAME       = "currentLoginName";

  /** Holds a Boolean value if the pass has been changed external */
  private static final String PASSWORD_CHANGED_EXTERNAL    = "passwordChangedExternal";

  /** Must be one of ACTION_DELETE, ACTION_CREATE, ACTION_PRE_UPDATE or ACTION_UPDATE */
  private static final String ACTION              = "action";

  // request attributes

  /** A boolean flag that is set to true after the password has been changed. */
  private static final String PASSWORD_WAS_CHANGED   = "passwordWasChanged";
  /** Holds a boolean: enabled or disabled */
  private static final String PASSWORD_MGMT_DISABLED = "isPasswordMgmtDisabled";

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    if (config.getInitParameter("PasswordJsp") != null) {
      passwordJSP = config.getInitParameter("PasswordJsp");
    }
    LOGGER.debug("Using JSP " + passwordJSP);
  }

  @Override
  protected void doTurmGet(HttpServletRequest request) throws TurmException {
    checkPasswordMgmgtIsEnabled(request);

    // preserve (and convert) parameters
    copyFromRequestToSession(PASSWORD_CHANGE_REASON, request);
    String referrerUrl = request.getParameter(REFERRER_URL);
    if (referrerUrl == null) {
      referrerUrl = request.getContextPath() + "/";
    }
    else if (!referrerUrl.startsWith("http://") && !referrerUrl.startsWith("https://")) {
      referrerUrl = request.getContextPath() + referrerUrl;
    }
    request.getSession().setAttribute(REFERRER_URL, referrerUrl);
  }

  private void checkPasswordMgmgtIsEnabled(HttpServletRequest request) throws TurmException {
    boolean isDisabled = Boolean.parseBoolean(TurmProperties.getProperties().getProperty(TurmProperties.TURM_PASSWORD_MGMT_DISABLED));
    if (isDisabled) {
      request.getSession().setAttribute(PASSWORD_MGMT_DISABLED, isDisabled);
      throw new TurmException("error.functionIsDisabled");
    }

  }

  @Override
  protected void doTurmPost(HttpServletRequest request) throws TurmException {
    String action = request.getParameter(ACTION);
    if (ACTION_CHANGE_PASSWORD.equals(action)) {
      prepareForPasswordReset(request);
    }
    else {
      updatePassword(request);
    }
  }

  @Override
  protected String getJsp() {
    return this.passwordJSP;
  }

  private void prepareForPasswordReset(HttpServletRequest request) throws DaoException, TurmException {
    Integer userId = Integer.valueOf(request.getParameter(USER_ID));
    User user = getUserDao().loadUserById(userId);
    UserContainer uc = new UserContainer(user);
    request.setAttribute(USER_CHANGE_PASSWORD, uc.getLoginName());

    UserContainer loggedUserContainer = new UserContainer();
    populate(request, loggedUserContainer);
    request.setAttribute(CURRENT_LOGIN_NAME, request.getRemoteUser());

    request.setAttribute(PASSWORD_CHANGED_EXTERNAL, "false");
  }

  /**
   * Updates the password of an existing user.
   * 
   * The password data is expected to be found in the request parameters.
   * 
   * @param request The current Servlet Request.
   * @throws TurmException If the password data could not be extracted from the
   *                       request parameters, the password is not valid, or the
   *                       password could not be updated in the database.
   */
  private void updatePassword(HttpServletRequest request) throws TurmException {
    UserContainer uc = new UserContainer();
    populate(request, uc);

    if (request.getRemoteUser() == null) {
      // set user name again so that the user doesn't have to type it in case he sees an error
      request.setAttribute(USER_CHANGE_PASSWORD, uc.getLoginName());
      request.setAttribute(CURRENT_LOGIN_NAME, uc.getLoginName());
      uc.validatePassword();

      getUserDao().updateUserPassword(uc.getLoginName(), uc.getOldPasswordEncrypted(), uc.getNewPasswordEncrypted());
      saveMessages(new TurmMessage("message.passwordUpdated"), request);
      request.setAttribute(PASSWORD_WAS_CHANGED, Boolean.TRUE);
      request.removeAttribute(CURRENT_LOGIN_NAME);

    }
    else {
      request.setAttribute(USER_CHANGE_PASSWORD, uc.getLoginName());
      uc.validatePassword();
      getUserDao().resetUserPassword(uc.getLoginName(), uc.getNewPasswordEncrypted());
      saveMessages(new TurmMessage("message.passwordUpdated"), request);
      request.setAttribute(PASSWORD_WAS_CHANGED, Boolean.TRUE);
      request.removeAttribute(USER_CHANGE_PASSWORD);
    }
  }

  /**
   * Copies a value from the request into the session scope.
   * 
   * @param parameter The key of the request parameter to copy.
   * @param request The current Servlet Request.
   */
  private void copyFromRequestToSession(String parameter, HttpServletRequest request) {
    String pwc = request.getParameter(parameter);
    if (pwc != null) {
      request.getSession().setAttribute(parameter, pwc);
    }
  }

}
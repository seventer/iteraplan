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
package de.iteratec.turm.model;

import static de.iteratec.turm.common.TurmProperties.TURM_PASSWORD_LENGTH;
import static de.iteratec.turm.common.TurmProperties.TURM_PASSWORD_MGMT_DISABLED;
import static de.iteratec.turm.common.TurmProperties.TURM_PASSWORD_PATTERN;
import static de.iteratec.turm.common.TurmProperties.getProperties;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import de.iteratec.turm.exceptions.TurmException;
import de.iteratec.turm.exceptions.UserInputException;
import de.iteratec.turm.util.PasswordEncryption;


/**
 * Container for role data.
 * 
 * This class can be automatically populated with the request parameters when creating
 * or updating an existing role. It furthermore can validate the data it holds and
 * throw appropriate exceptions.
 */
public class UserContainer {

  private Number   id;
  private Number   olVersion;
  private String   loginName;
  private String   firstName;
  private String   lastName;
  private String   oldPassword;
  private String   password = "__VOID__";
  private String   passwordRepeat = "__VOID__";
  private String[] roleIds;

  public UserContainer() {
    // nothing to do
  }

  public UserContainer(Number id, Number olVersion, String loginName, String firstName,
                       String lastName, String password, String passwordRepeat, String[] roleIds) {
    super();
    this.id = id;
    this.olVersion = olVersion;
    this.loginName = loginName;
    this.firstName = firstName;
    this.lastName = lastName;
    this.password = password;
    this.passwordRepeat = passwordRepeat;
    this.roleIds = roleIds;
  }

  public UserContainer(User u) {
    this(u.getId(), u.getOlVersion(), u.getLoginName(), u.getFirstName(), u.getLastName(), null,
        null, u.getRoleIdsAsStringArray());
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String newFirstName) {
    this.firstName = newFirstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String newLastName) {
    this.lastName = newLastName;
  }

  public String getLoginName() {
    return loginName;
  }

  public void setLoginName(String newLoginName) {
    this.loginName = newLoginName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String newPassword) {
    this.password = newPassword;
  }

  public String getPasswordRepeat() {
    return passwordRepeat;
  }

  public void setPasswordRepeat(String newPasswordRepeat) {
    this.passwordRepeat = newPasswordRepeat;
  }

  public String[] getRoleIds() {
    return roleIds;
  }

  public void setRoleIds(String[] roleIds) {
    this.roleIds = roleIds;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer("New User:");
    sb.append("\nnewLoginName: ");
    sb.append(loginName);
    sb.append("\nnewFirstName: ");
    sb.append(firstName);
    sb.append("\nnewLastName: ");
    sb.append(lastName);
    sb.append("\nnewPassword: ");
    sb.append(password);
    sb.append("\nnewPasswordRepeat: ");
    sb.append(passwordRepeat);
    sb.append("\nnewRoles: ");
    sb.append(roleIds);
    return sb.toString();
  }

  public String getOldPasswordEncrypted() throws TurmException {
    try {
      return PasswordEncryption.getInstance().getEncryptedPassword(getOldPassword());
    } catch (NoSuchAlgorithmException e) {
      throw new TurmException("error.internalError", e);
    } catch (UnsupportedEncodingException e) {
      throw new TurmException("error.internalError", e);
    }
  }

  public String getNewPasswordEncrypted() throws TurmException {
    try {
      return PasswordEncryption.getInstance().getEncryptedPassword(getPassword());
    } catch (NoSuchAlgorithmException e) {
      throw new TurmException("error.internalError", e);
    } catch (UnsupportedEncodingException e) {
      throw new TurmException("error.internalError", e);
    }
  }

  public void validateAll() throws TurmException {
    validateBasicFields();

    if (!Boolean.parseBoolean(getProperties().getProperty(TURM_PASSWORD_MGMT_DISABLED))) {
      validatePassword();
    }
  }

  public void validatePassword() throws UserInputException {
    Integer passwordLength = Integer.valueOf(getProperties().getProperty(TURM_PASSWORD_LENGTH));
    if (password == null || passwordRepeat == null || password.length() < passwordLength) {
      throw new UserInputException("error.passwordTooShort", new Object[] { passwordLength });
    }
    String passwordPattern = getProperties().getProperty(TURM_PASSWORD_PATTERN);
    if (!Pattern.matches(passwordPattern, password)) {
      throw new UserInputException("error.insecurePassword", new Object[] { passwordLength });
    }
    if (!password.equals(passwordRepeat)) {
      throw new UserInputException("error.passwordsDoNotMatch");
    }
    if (password.equals(oldPassword)) {
      throw new UserInputException("error.passwordIsNotNew");
    }
  }

  public void validateBasicFields() throws UserInputException {
    if (isEmptyString(getLoginName())) {
      throw new UserInputException("error.loginMustNotBeEmpty");
    }

    if (isEmptyString(getLastName())) {
      throw new UserInputException("error.lastNameMustNotBeEmpty");
    }

    if (isEmptyString(getFirstName())) {
      throw new UserInputException("error.firstNameMustNotBeEmpty");
    }

  }

  private static boolean isEmptyString(String str) {
    if (str == null || str.length() <= 0) {
      return true;
    }
    else {
      return false;
    }
  }

  public Number getOlVersion() {
    return olVersion;
  }

  public Number getId() {
    return id;
  }

  public void setOlVersion(Number olVersion) {
    this.olVersion = olVersion;
  }

  public void setId(Number id) {
    this.id = id;
  }

  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

}

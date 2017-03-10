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
package de.iteratec.iteraplan.presentation.dialog.User;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.memory.ColumnDefinition;
import de.iteratec.iteraplan.presentation.memory.SearchDialogMemory;


/**
 * Dialog Memory Class of the User Context. Contains common parameters outside of the Spring Webflow
 * context like search parameters.
 */
public class UserDialogMemory extends SearchDialogMemory {

  private static final long serialVersionUID = 1L;

  /** {@link #getFirstName()} */
  private String            firstName;
  /** {@link #getLastName()} */
  private String            lastName;
  /** {@link #getLoginName()} */
  private String            loginName;
  /** {@link #getEmail()} */
  private String            email;

  @Override
  public List<Criterion> getCriteria() {
    List<Criterion> userCriteria = new ArrayList<Criterion>();
    userCriteria.add(new Criterion("firstName", "manageUser.firstName", "manageUser.search.hint.firstnameField"));
    userCriteria.add(new Criterion("lastName", "manageUser.lastName", "manageUser.search.hint.lastnameField"));

    return userCriteria;
  }

  @Override
  public List<ColumnDefinition> getInitialColumnDefinitions() {
    List<ColumnDefinition> props = new ArrayList<ColumnDefinition>();
    props.add(new ColumnDefinition("manageUser.loginName", "loginName", "", true));
    props.add(new ColumnDefinition("manageUser.firstName", "firstName", "", true));
    props.add(new ColumnDefinition("manageUser.lastName", "lastName", "", true));
    props.add(new ColumnDefinition("manageUser.email", "email", "", true));
    return props;
  }

  /**
   * Gets the search string for the first name.
   * 
   * @return A name part that is being searched for in the first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * {@link #getFirstName()}
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Gets the search string for the last name.
   * 
   * @return A name part that is being searched for in the last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * {@link #getFirstName()}
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Gets the search string for the login name.
   * 
   * @return A name part that is being searched for in the login name
   */
  public String getLoginName() {
    return loginName;
  }

  /**
   * {@link #getLoginName()}
   */
  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  /**
   * Gets the search string for the email.
   * 
   * @return A name part that is being searched for in the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * {@link #getEmail()}
   */
  public void setEmail(String email) {
    this.email = email;
  }

  public User toUser() {
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setLoginName(loginName);
    user.setEmail(email);

    return user;
  }

  /**{@inheritDoc}**/
  @Override
  public String getIconCss() {
    return ""; // no icon for this entity
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
    result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
    result = prime * result + ((loginName == null) ? 0 : loginName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    UserDialogMemory other = (UserDialogMemory) obj;
    EqualsBuilder builder = new EqualsBuilder();
    builder.append(firstName, other.firstName);
    builder.append(lastName, other.lastName);
    builder.append(loginName, other.loginName);
    return builder.isEquals();
  }

}

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * This class can hold User data. It is mainly used for displaying the user data
 * on a JSP.
 * 
 * TODO: check if this can be merged with the {@link UserContainer} class.
 */
public class User implements IdEntity {

  private Number     id;
  private Number     olVersion;
  private String     loginName;
  private String     firstName;
  private String     lastName;
  private List<Role> roles;

  public User() {
    roles = new ArrayList<Role>();
  }

  public User(Number id, Number olVersion, String loginName, String firstName, String lastName,
      List<Role> roles) {
    this.id = id;
    this.olVersion = olVersion;
    this.loginName = loginName;
    this.firstName = firstName;
    this.lastName = lastName;
    this.roles = roles;
  }

  public String getLoginName() {
    return loginName;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public Number getId() {
    return id;
  }

  public Number getOlVersion() {
    return olVersion;
  }

  public String[] getRoleIdsAsStringArray() {
    String[] result = new String[getRoles().size()];
    int index = 0;
    for (Iterator<Role> it = getRoles().iterator(); it.hasNext(); index++) {
      Role role = it.next();
      result[index] = role.getId().toString();
    }
    return result;
  }

}

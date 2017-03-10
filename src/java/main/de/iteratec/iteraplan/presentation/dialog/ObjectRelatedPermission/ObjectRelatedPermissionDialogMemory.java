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
package de.iteratec.iteraplan.presentation.dialog.ObjectRelatedPermission;

import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.presentation.memory.ColumnDefinition;
import de.iteratec.iteraplan.presentation.memory.SearchDialogMemory;


/**
 * Dialog Memory Class of the UserEntity Context. Contains common parameters outside of the Spring
 * Webflow context like search parameters.
 */
public class ObjectRelatedPermissionDialogMemory extends SearchDialogMemory {

  private static final long serialVersionUID = 1L;

  /** {@link #getName()} */
  private String            name;

  @Override
  public List<Criterion> getCriteria() {
    List<Criterion> userEntityCriteria = new ArrayList<Criterion>();
    userEntityCriteria
        .add(new Criterion("name", "objectRelatedPermissions.search.label.nameField", "objectRelatedPermissions.search.hint.nameField"));

    return userEntityCriteria;
  }

  @Override
  public List<ColumnDefinition> getInitialColumnDefinitions() {
    List<ColumnDefinition> props = new ArrayList<ColumnDefinition>();

    props.add(new ColumnDefinition("global.name", "identityString", "", true));

    ColumnDefinition colDef = new ColumnDefinition("objectRelatedPermissions.entityType", "type", "", true);
    colDef.setInternationalized(true);
    props.add(colDef);

    return props;
  }

  /**
   * Gets the search string for the first name.
   * 
   * @return A name part that is being searched for in the first name
   */
  public String getName() {
    return name;
  }

  /**
   * {@link #getName()}
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * User is used internally. User and Usergroup are subclasses of UserEntity.
   * 
   * @return an UserEntity with its name set to the searched name.
   */
  public UserEntity toUserEntity() {
    User userEntity = new User();
    userEntity.setLoginName(name);
    return userEntity;
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
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    ObjectRelatedPermissionDialogMemory other = (ObjectRelatedPermissionDialogMemory) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    }
    else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }

}

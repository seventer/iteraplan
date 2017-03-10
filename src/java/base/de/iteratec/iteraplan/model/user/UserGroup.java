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
package de.iteratec.iteraplan.model.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;


/**
 * This class describes the concept of the 'User Group' in the iteraplan application. It is used
 * amongst others to group {@link UserEntity}s in order to achieve the assignment of instance-
 * specific, exclusive permissions to more than one user.
 * <p>
 * Table name: userentity.
 */
@Entity
public class UserGroup extends UserEntity {

  private static final long serialVersionUID = 578948069391234139L;
  private String            name;
  private String            description;
  private Set<UserEntity>   members          = new HashSet<UserEntity>();

  public UserGroup() {
    super();
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Set<UserEntity> getMembers() {
    return members;
  }

  public void setName(String name) {
    this.name = StringUtils.trim(name);
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setMembers(Set<UserEntity> members) {
    this.members = members;
  }

  /**
   * Adds the given {@link UserEntity} to the set of members of this user group. Updates both sides
   * of the association.
   * 
   * @param userEntity
   *          The user entity to add.
   * @throws IllegalArgumentException
   *           If the passed-in element is {@code null}.
   */
  public void addUserEntity(UserEntity userEntity) {
    if (userEntity == null) {
      throw new IllegalArgumentException("The element to add must no be null.");
    }

    userEntity.getParentUserGroups().add(this);
    members.add(userEntity);
  }

  /**
   * Removes all members of this user group. Updates both sides of the association.
   */
  public void removeMembers() {
    for (UserEntity userEntity : members) {
      userEntity.getParentUserGroups().remove(this);
    }
    members.clear();
  }

  /**
   * @return Returns the set of {@link User}s that are members of this user group.
   */
  public Set<User> findUsersInUserGroup() {
    Set<User> result = new HashSet<User>();
    for (UserEntity userEntity : members) {
      if (userEntity instanceof User) {
        result.add((User) userEntity);
      }
    }

    return result;
  }

  /**
   * @return Returns the set of {@link UserGroup}s that are members of this user group.
   */
  public Set<UserGroup> findUserGroupsInUserGroup() {
    Set<UserGroup> result = new HashSet<UserGroup>();
    for (UserEntity userEntity : members) {
      if (userEntity instanceof UserGroup) {
        result.add((UserGroup) userEntity);
      }
    }

    return result;
  }

  @Override
  public String getDescriptiveString() {
    return getDescription();
  }

  @Override
  public String getType() {
    return "global.usergroup";
  }

  public String getIdentityString() {
    return getName();
  }

  @Override
  public void validate() {
    super.validate();

    if (hasHierarchyCycle()) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.AGGREGATED_USERGROUP_CYCLE);
    }
  }

  /**
   * @return Returns {@code true} if the hierarchy of user entities that this user group is part of
   *         contains a cycle, {@code false} otherwise. A cylce exists, if any parent of this user
   *         group is also a member of this user group.
   */
  private boolean hasHierarchyCycle() {
    Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    for (Integer id : getUserGroupHierarchyAsIDs()) {
      map.put(id, id);
    }

    for (UserEntity userEntity : members) {
      if (map.containsKey(userEntity.getId())) {
        return true;
      }
    }

    return false;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(description).append(name).toHashCode();
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

    UserGroup other = (UserGroup) obj;
    EqualsBuilder builder = new EqualsBuilder();
    builder.append(description, other.description);
    builder.append(name, other.name);

    return builder.isEquals();
  }

}
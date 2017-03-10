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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;


/**
 * This class describes the concept of the 'User' in the iteraplan application.
 * <p>
 * Table name: user
 */
@Entity
public class User extends UserEntity {

  private static final long      serialVersionUID             = -9165774250775413332L;

  private String                 loginName;

  private String                 firstName;

  private String                 lastName;

  private String                 dataSource                   = Constants.MASTER_DATA_SOURCE;

  private String                 email;

  private Set<BuildingBlock>     subscribedBuildingBlocks     = CollectionUtils.hashSet();

  private Set<BuildingBlockType> subscribedBuildingBlockTypes = CollectionUtils.hashSet();

  private Set<Role>              roles = CollectionUtils.hashSet();

  public User() {
    super();
  }

  public String getLoginName() {
    return loginName;
  }

  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getDataSource() {
    return dataSource;
  }

  public Set<BuildingBlock> getSubscribedBuildingBlocks() {
    return subscribedBuildingBlocks;
  }

  public Set<BuildingBlockType> getSubscribedBuildingBlockTypes() {
    return subscribedBuildingBlockTypes;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public void addRoleTwoWay(Role role) {
    if (role != null) {
      this.roles.add(role);
      role.getUsers().add(this);
    }
  }

  public void removeRoleTwoWay(Role role) {
    if (role != null) {
      this.roles.remove(role);
      role.getUsers().remove(this);
    }
  }

  public void clearRoles() {
    for (Role role : new HashSet<Role>(roles)) {
      this.removeRoleTwoWay(role);
    }
  }

  public void setDataSource(String dataSource) {
    this.dataSource = dataSource;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setSubscribedBuildingBlocks(Set<BuildingBlock> subscribedBuildingBlocks) {
    this.subscribedBuildingBlocks = subscribedBuildingBlocks;
  }

  public void setSubscribedBuildingBlockTypes(Set<BuildingBlockType> subscribedBuildingBlockTypes) {
    this.subscribedBuildingBlockTypes = subscribedBuildingBlockTypes;
  }

  /**
   * Removes the given list of {@link BuildingBlock}s from the set of subscribed building blocks of this
   * user entity. Updates both sides of the association.
   * 
   * @param bbsToRemove
   *          The list of {@link BuildingBlock}s to remove.
   * @param bbtsToRemove
   *          The list of {@link BuildingBlockType}s to remove.
   * @throws IllegalArgumentException
   *           If the passed-in list or an element in the list is {@code null}.
   */
  public void removeSubscriptions(List<BuildingBlock> bbsToRemove, List<BuildingBlockType> bbtsToRemove) {
    if (bbsToRemove == null || bbtsToRemove == null) {
      throw new IllegalArgumentException("The parameters are required and must not be null.");
    }

    for (BuildingBlock block : bbsToRemove) {
      if (block == null) {
        throw new IllegalArgumentException("The element to remove must not be null.");
      }
      block.getSubscribedUsers().remove(this);
    }
    subscribedBuildingBlocks.clear();

    for (BuildingBlockType bbt : bbtsToRemove) {
      if (bbt == null) {
        throw new IllegalArgumentException("The element to remove must not be null.");
      }
      bbt.getSubscribedUsers().remove(this);
    }
    subscribedBuildingBlockTypes.clear();
  }

  @Override
  public String getDescriptiveString() {
    return getFirstName() + " " + getLastName();
  }

  @Override
  public String getType() {
    return "global.user";
  }

  public String getIdentityString() {
    return this.getLoginName();
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();
    return builder.appendSuper(super.hashCode()).append(dataSource).append(firstName).append(lastName).append(loginName).toHashCode();
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
    User other = (User) obj;
    EqualsBuilder builder = new EqualsBuilder();
    builder.append(dataSource, other.dataSource);
    builder.append(firstName, other.firstName);
    builder.append(lastName, other.lastName);
    builder.append(loginName, other.loginName);
    return builder.isEquals();
  }

}
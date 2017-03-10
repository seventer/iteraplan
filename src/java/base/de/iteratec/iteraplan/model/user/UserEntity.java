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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.interfaces.Entity;


/**
 * The abstract base class for domain objects of type {@link User} and {@link UserGroup}. It defines
 * common functionality for both concrete classes.
 */
@javax.persistence.Entity
public abstract class UserEntity implements Comparable<UserEntity>, Entity {

  private static final long  serialVersionUID    = -4768968212889328148L;
  private Integer            id;
  private Integer            olVersion;
  private Set<UserGroup>     parentUserGroups    = new HashSet<UserGroup>();
  private Set<BuildingBlock> ownedBuildingBlocks = new HashSet<BuildingBlock>();
  private String             lastModificationUser;
  private Date               lastModificationTime;

  public UserEntity() {
    // nothing to do
  }

  public Integer getId() {
    return id;
  }

  public Integer getOlVersion() {
    return olVersion;
  }

  public Set<UserGroup> getParentUserGroups() {
    return parentUserGroups;
  }

  /**
   * Caution: This method must NOT be called because the current mapping strategy causes problems
   * with MySQL. Calling this method results in a database join over each and every subclass of
   * {@link BuildingBlock}.
   * 
   * @return The set of {@link BuildingBlock}s owned by this user entity.
   */
  public Set<BuildingBlock> getOwnedBuildingBlocks() {
    return ownedBuildingBlocks;
  }

  public String getLastModificationUser() {
    return this.lastModificationUser;
  }

  public Date getLastModificationTime() {
    return lastModificationTime;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setOlVersion(Integer olVersion) {
    this.olVersion = olVersion;
  }

  public void setParentUserGroups(Set<UserGroup> parentUserGroups) {
    this.parentUserGroups = parentUserGroups;
  }

  public void setOwnedBuildingBlocks(Set<BuildingBlock> ownedBuildingBlocks) {
    this.ownedBuildingBlocks = ownedBuildingBlocks;
  }

  public void setLastModificationUser(String user) {
    this.lastModificationUser = user;
  }

  public void setLastModificationTime(Date lastModificationTime) {
    this.lastModificationTime = lastModificationTime;
  }

  /**
   * Adds each element of the given set of {@link UserGroup}s to this user entity. Updates both
   * sides of the association.
   * 
   * @param userGroups
   *          The set of {@link UserGroup}s to add.
   * @throws IllegalArgumentException
   *           If the passed-in set or an element in the set is {@code null}.
   */
  public void addParentUserGroups(Collection<UserGroup> userGroups) {
    if (userGroups == null) {
      throw new IllegalArgumentException("The parameter is required and must no be null.");
    }

    for (UserGroup userGroup : userGroups) {
      if (userGroup == null) {
        throw new IllegalArgumentException("The element to add must be null.");
      }

      userGroup.getMembers().add(this);
      parentUserGroups.add(userGroup);
    }
  }

  /**
   * Adds the given {@link BuildingBlock} to the set of owned building blocks of this user entity.
   * Updates both sides of the association.
   * 
   * @param block
   *          The building block to add.
   * @throws IllegalArgumentException
   *           If the passed-in element is {@code null}.
   */
  public void addOwnedBuildingBlock(BuildingBlock block) {
    if (block == null) {
      throw new IllegalArgumentException("The parameter is required and must no be null.");
    }

    block.getOwningUserEntities().add(this);
    ownedBuildingBlocks.add(block);
  }

  /**
   * Removes all parent user groups from this user entity. Updates both sides of the association.
   */
  public void removeParentUserGroups() {
    for (UserGroup userGroup : parentUserGroups) {
      userGroup.getMembers().remove(this);
    }
    parentUserGroups.clear();
  }

  /**
   * Removes the given list of {@link BuildingBlock}s from the set of owned building blocks of this
   * user entity. Updates both sides of the association.
   * 
   * @param toRemove
   *          The list of {@link BuildingBlock}s to remove.
   * @throws IllegalArgumentException
   *           If the passed-in list or an element in the list is {@code null}.
   */
  public void removeOwnedBuildingBlocks(List<BuildingBlock> toRemove) {
    if (toRemove == null) {
      throw new IllegalArgumentException("The parameter is required and must not be null.");
    }

    for (BuildingBlock block : toRemove) {
      if (block == null) {
        throw new IllegalArgumentException("The element to remove must not be null.");
      }

      block.getOwningUserEntities().remove(this);
    }
    ownedBuildingBlocks.clear();
  }

  /**
   * @return Finds and returns the complete hierarchy of parent user groups of this user entity
   *         (exclusive).
   */
  public Set<UserGroup> getUserGroupHierarchy() {
    Set<UserGroup> result = new HashSet<UserGroup>();
    recursivelyGetUserGroupHierarchy(this, new HashSet<UserEntity>(), result);

    return result;
  }

  /**
   * @return Finds and returns a set of IDs comprised of the IDs of the complete hierarchy of parent
   *         user groups of this user entity (exclusive).
   */
  public Set<Integer> getUserGroupHierarchyAsIDs() {
    Set<UserGroup> userGroups = getUserGroupHierarchy();
    Set<Integer> set = new HashSet<Integer>();
    for (UserGroup userGroup : userGroups) {
      set.add(userGroup.getId());
    }

    return set;
  }

  public abstract String getDescriptiveString();

  public abstract String getType();

  public int compareTo(UserEntity other) {
    return this.getIdentityString().compareToIgnoreCase(other.getIdentityString());
  }

  @Override
  public String toString() {
    return getIdentityString();
  }

  public void validate() {
    // Validation is done in concrete classes.
  }

  /**
   * Recursively finds and stores the complete user group hierarchy of the given user entity in the
   * {@code result} output parameter. The method starts with the given user entity and traverses the
   * hierarchy upwards until no more parents can be found.
   * 
   * @param userEntity
   *          The user entity for which all parents shall be found.
   * @param visited
   *          The previously visited set of user entities.
   * @param result
   *          The output paramater.
   */
  private void recursivelyGetUserGroupHierarchy(UserEntity userEntity, Set<UserEntity> visited, Set<UserGroup> result) {
    if (visited.contains(userEntity)) {
      return;
    }
    visited.add(this);

    for (UserGroup userGroup : userEntity.getParentUserGroups()) {
      result.add(userGroup);
      recursivelyGetUserGroupHierarchy(userGroup, visited, result);
    }
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(id).append(lastModificationUser).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    UserEntity other = (UserEntity) obj;
    EqualsBuilder builder = new EqualsBuilder();
    builder.append(id, other.id);
    builder.append(lastModificationUser, other.lastModificationUser);

    return builder.isEquals();
  }

}
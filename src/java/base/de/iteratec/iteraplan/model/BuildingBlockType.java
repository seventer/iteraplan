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
package de.iteratec.iteraplan.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.iteraplan.model.user.Role2BbtPermission;
import de.iteratec.iteraplan.model.user.User;


@Entity
@Indexed(index = "index.BuildingBlockType")
public class BuildingBlockType implements Comparable<BuildingBlockType>, IdentityEntity, Serializable {

  private static final long       serialVersionUID = 5262508752551376118L;

  @DocumentId
  private Integer                 id;
  private Integer                 olVersion;
  private TypeOfBuildingBlock     typeOfBuildingBlock;
  private Set<AttributeType>      attributeTypes   = new HashSet<AttributeType>();
  private boolean                 availableForAttributes;
  private Set<Role2BbtPermission> rolePermissions  = new HashSet<Role2BbtPermission>();
  /** {@link #getSubscribedUsers()} */
  private Set<User>               subscribedUsers  = new HashSet<User>();

  /**
   * Constructor.
   */
  public BuildingBlockType() {
    // nothing to do
  }

  public BuildingBlockType(TypeOfBuildingBlock typeOfBuildingBlock) {
    this();
    this.typeOfBuildingBlock = typeOfBuildingBlock;
  }

  /**
   * Constructor.
   * 
   * @param typeOfBuildingBlock
   *          The type of building block to use for this building block type.
   * @param availableForAttributes
   *          Indicates, whether the corresponding building block should be able participate in the
   *          attribute system.
   */
  public BuildingBlockType(TypeOfBuildingBlock typeOfBuildingBlock, boolean availableForAttributes) {
    this(typeOfBuildingBlock);
    this.availableForAttributes = availableForAttributes;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;

  }

  public Integer getOlVersion() {
    return olVersion;
  }

  public void setOlVersion(Integer olVersion) {
    this.olVersion = olVersion;
  }

  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return typeOfBuildingBlock;
  }

  public void setTypeOfBuildingBlock(TypeOfBuildingBlock typeOfBuildingBlock) {
    this.typeOfBuildingBlock = typeOfBuildingBlock;
  }

  public Set<AttributeType> getAttributeTypes() {
    return attributeTypes;
  }

  public void setAttributeTypes(Set<AttributeType> attributeTypes) {
    this.attributeTypes = attributeTypes;
  }

  public void addAttributeTypeOneWay(AttributeType at) {
    this.getAttributeTypes().add(at);
  }

  public void addAttributeTypeTwoWay(AttributeType at) {
    this.addAttributeTypeOneWay(at);
    at.addBuildingBlockTypeOneWay(this);
  }

  public void removeAttributeTypeOneWay(AttributeType at) {
    this.getAttributeTypes().remove(at);
  }

  public List<AttributeType> getAttributeTypesAsList() {
    List<AttributeType> types = new ArrayList<AttributeType>(this.getAttributeTypes());
    Collections.sort(types);

    return types;
  }

  public boolean isAvailableForAttributes() {
    return availableForAttributes;
  }

  public void setAvailableForAttributes(boolean availableForAttributes) {
    this.availableForAttributes = availableForAttributes;
  }

  public Set<Role2BbtPermission> getRolePermissions() {
    return rolePermissions;
  }

  public void setRolePermissions(Set<Role2BbtPermission> rolePermissions) {
    this.rolePermissions = rolePermissions;
  }

  public void removeRolePermissions() {
    for (Role2BbtPermission perm : rolePermissions) {
      perm.getRole().getPermissionsBbt().remove(this);
    }
    rolePermissions.clear();
  }

  public String getIdentityString() {
    return getTypeOfBuildingBlock().toString();
  }

  @Field(store = Store.YES, index = Index.UN_TOKENIZED)
  public String getName() {
    return getTypeOfBuildingBlock().toString();
  }

  public int compareTo(BuildingBlockType other) {
    Locale locale = UserContext.getCurrentLocale();
    String a = MessageAccess.getString(typeOfBuildingBlock.toString(), locale);
    String b = MessageAccess.getString(other.getTypeOfBuildingBlock().toString(), locale);

    return a.compareToIgnoreCase(b);
  }

  /**
   * Two building block types are considered equal if their encapsulate the same
   * {@link TypeOfBuildingBlock}.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if ((obj == null) || (obj.getClass() != this.getClass())) {
      return false;
    }

    final BuildingBlockType other = (BuildingBlockType) obj;
    if ((typeOfBuildingBlock == null) ? other.typeOfBuildingBlock == null : !(typeOfBuildingBlock.equals(other.typeOfBuildingBlock))) {
      return false;
    }

    return true;
  }

  @Override
  public String toString() {
    return "building block '" + typeOfBuildingBlock + "'";
  }

  @Override
  public int hashCode() {
    int prime = 29;
    int result = 1;

    result = prime * result + (typeOfBuildingBlock != null ? typeOfBuildingBlock.hashCode() : 0);

    return result;
  }

  public Set<User> getSubscribedUsers() {
    return subscribedUsers;
  }

  public void setSubscribedUsers(Set<User> subscribedUsers) {
    this.subscribedUsers = subscribedUsers;
  }

  /**
   * Returns {@code true} if the currently logged user is already subscribed.
   * 
   * @return {@code true} if the currently logged user is already subscribed, otherwise
   *    returns {@code false}
   */
  public boolean isSubscribed() {
    return this.subscribedUsers.contains(UserContext.getCurrentUserContext().getUser());
  }
}

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

import java.io.Serializable;

import javax.persistence.Entity;

import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;


/**
 * This class represents a Permission for an {@link BuildingBlockType}. The kind of the permission is one type of {@link EditPermissionType}s.
 */
@Entity
public class Role2BbtPermission implements Comparable<Role2BbtPermission>, IdentityEntity, Serializable {

  private static final long serialVersionUID = -6936788067925017902L;

  /**
   * Persistent enumeration class for permission types, used for editing building blocks.
   */
  public enum EditPermissionType {

    UPDATE, CREATE, DELETE;
  }

  private Integer            id;

  private Role               role;

  private BuildingBlockType  bbt;

  private EditPermissionType type;

  /**
   * Default constructor.
   */
  public Role2BbtPermission() {
    super();
  }

  public Role2BbtPermission(Role role, BuildingBlockType bbt, EditPermissionType type) {
    this.role = role;
    this.bbt = bbt;
    this.type = type;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public EditPermissionType getType() {
    return type;
  }

  public void setType(EditPermissionType type) {
    this.type = type;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public BuildingBlockType getBbt() {
    return bbt;
  }

  public void setBbt(BuildingBlockType bbt) {
    this.bbt = bbt;
  }

  /**
   * This method can be called, if both ends of this association are set. On these two
   * ends this permission object will be added.
   */
  public void connect() {
    Preconditions.checkNotNull(getBbt());
    Preconditions.checkNotNull(getRole());
    getBbt().getRolePermissions().add(this);
    getRole().getPermissionsBbt().add(this);
  }

  /**
   * This is the complement method to {@link #connect()}. It will remove itself from the
   * two ends, which are connected.
   */
  public void disconnect() {
    Preconditions.checkNotNull(getBbt());
    Preconditions.checkNotNull(getRole());
    getBbt().getRolePermissions().remove(this);
    getRole().getPermissionsBbt().remove(this);
  }

  /**
   * {@inheritDoc}
   */
  public String getIdentityString() {
    return String.format("%s-%s (%s)", role, bbt, type);
  }

  /**
   * {@inheritDoc}
   */
  public int compareTo(Role2BbtPermission other) {
    int c = this.getRole().compareTo(other.getRole());
    if (c != 0) {
      return c;
    }
    c = this.getBbt().compareTo(other.getBbt());
    if (c != 0) {
      return c;
    }
    return this.getType().compareTo(other.getType());
  }

  /**
   * This method is not compliant with equals().
   * If both id's are null, equals will return true, but the hashCode's are different.
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    if (id == null) {
      result = super.hashCode();
    }
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  /**
   * This method is not compliant with hashCode().
   * If both id's are null, equals will return true, but the hashCode's are different.
   * {@inheritDoc}
   */
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
    Role2BbtPermission other = (Role2BbtPermission) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }
    return this == obj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getIdentityString();
  }

}
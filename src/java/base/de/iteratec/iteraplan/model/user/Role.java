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
import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.interfaces.Entity;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;
import de.iteratec.iteraplan.model.user.Role2BbtPermission.EditPermissionType;


/**
 * This class represents the table "roles" in the database.
 */
@javax.persistence.Entity
@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
public class Role implements Comparable<Role>, Entity {

  private static final long            serialVersionUID         = -5308682670476772326L;

  public static final String           SUPERVISOR_ROLE_NAME     = "iteraplan_Supervisor";

  private Integer                      id;

  private Integer                      olVersion;

  private String                       roleName;

  private String                       description;

  private Set<Role>                    consistsOfRoles          = new HashSet<Role>();

  private Set<Role>                    elementOfRoles           = new HashSet<Role>();

  private Set<PermissionFunctional>    permissionsFunctional    = new HashSet<PermissionFunctional>();

  private Set<PermissionAttrTypeGroup> permissionsAttrTypeGroup = new HashSet<PermissionAttrTypeGroup>();

  private Set<Role2BbtPermission>      permissionsBbt           = new HashSet<Role2BbtPermission>();

  private String                       lastModificationUser;

  private Date                         lastModificationTime;

  private Set<User>                    users                    = new HashSet<User>();

  public Role() {
    // nothing to do
  }

  public Integer getId() {
    return this.id;
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

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getIdentityString() {
    return this.getRoleName();
  }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }

  public void addUserTwoWay(User user) {
    if (user != null) {
      this.users.add(user);
      user.getRoles().add(this);
    }
  }

  public void removeUserTwoWay(User user) {
    if (user != null) {
      this.users.remove(user);
      user.getRoles().remove(this);
    }
  }

  public void clearUsers() {
    for (User user : new HashSet<User>(users)) {
      this.removeUserTwoWay(user);
    }
  }

  public int compareTo(Role other) {
    return this.getRoleName().compareToIgnoreCase(other.getRoleName());
  }

  public Set<Role> getConsistsOfRoles() {
    return consistsOfRoles;
  }

  public void setConsistsOfRoles(Set<Role> consistsOfRoles) {
    this.consistsOfRoles = consistsOfRoles;
  }

  public Set<Role> getConsistsOfRolesAggregated() {
    Set<Role> rolesAggregated = new HashSet<Role>();
    getConsistsOfRolesAggregatedHelper(this, rolesAggregated);
    return rolesAggregated;
  }

  private void getConsistsOfRolesAggregatedHelper(Role role, Set<Role> rolesAggregated) {
    rolesAggregated.addAll(role.getConsistsOfRoles());
    for (Role r : role.getConsistsOfRoles()) {
      getConsistsOfRolesAggregatedHelper(r, rolesAggregated);
    }
  }

  public void addConsistsOfRoleTwoWay(Role consistsOfRole) {
    if (this.getConsistsOfRoles() == null) {
      this.setConsistsOfRoles(new HashSet<Role>());
    }
    this.getConsistsOfRoles().add(consistsOfRole);
    consistsOfRole.addElementOfRoleOneWay(this);
  }

  public void addConsistsOfRoles(Collection<Role> subRoles) {
    for (Role subRole : subRoles) {
      addConsistsOfRoleTwoWay(subRole);
    }
  }

  public void removeConsistsOfRoles() {
    for (Role subRole : consistsOfRoles) {
      subRole.getElementOfRoles().remove(subRole);
    }

    consistsOfRoles.clear();
  }

  private void addElementOfRoleOneWay(Role role) {
    if (this.getElementOfRoles() == null) {
      this.setElementOfRoles(new HashSet<Role>());
    }
    this.getElementOfRoles().add(role);
  }

  public Set<Role> getElementOfRoles() {
    return elementOfRoles;
  }

  public void setElementOfRoles(Set<Role> elementOfRoles) {
    this.elementOfRoles = elementOfRoles;
  }

  public Set<Role> getElementOfRolesAggregated() {
    Set<Role> rolesAggregated = new HashSet<Role>();
    getElementOfRolesAggregatedHelper(this, rolesAggregated);
    return rolesAggregated;
  }

  private void getElementOfRolesAggregatedHelper(Role role, Set<Role> rolesAggregated) {
    rolesAggregated.addAll(role.getElementOfRoles());
    for (Role r : role.getElementOfRoles()) {
      getElementOfRolesAggregatedHelper(r, rolesAggregated);
    }
  }

  public Set<PermissionAttrTypeGroup> getPermissionsAttrTypeGroup() {
    return permissionsAttrTypeGroup;
  }

  public void setPermissionsAttrTypeGroup(Set<PermissionAttrTypeGroup> permissionsAttrTypeGroup) {
    this.permissionsAttrTypeGroup = permissionsAttrTypeGroup;
  }

  public void addPermissionsAttrTypeGroupTwoWay(PermissionAttrTypeGroup patg) {
    this.getPermissionsAttrTypeGroup().add(patg);
    patg.setRole(this);
    patg.getAttrTypeGroup().getPermissionsRole().add(patg);
  }

  public void addPermissionsAttrTypeGroups(Collection<PermissionAttrTypeGroup> patgs) {
    for (PermissionAttrTypeGroup permissionAttrTypeGroup : patgs) {
      addPermissionsAttrTypeGroupTwoWay(permissionAttrTypeGroup);
    }
  }

  /**
   * @return A set of PermissionAttrTypeGroup which are directly assigned to this role as well as
   *         all roles that are part of this role.
   */
  public Set<PermissionAttrTypeGroup> getPermissionsAttrTypeGroupAggregated() {
    Set<PermissionAttrTypeGroup> permissionAtgAggregated = new HashSet<PermissionAttrTypeGroup>();
    getPermissionsAttrTypeGroupAggregatedHelper(this, permissionAtgAggregated);
    return permissionAtgAggregated;
  }

  private void getPermissionsAttrTypeGroupAggregatedHelper(Role role, Set<PermissionAttrTypeGroup> permissionAtgAggregated) {
    permissionAtgAggregated.addAll(role.getPermissionsAttrTypeGroup());
    for (Role r : role.getConsistsOfRoles()) {
      getPermissionsAttrTypeGroupAggregatedHelper(r, permissionAtgAggregated);
    }
  }

  public Set<Role2BbtPermission> getPermissionsBbt() {
    return this.permissionsBbt;
  }

  public void setPermissionsBbt(Set<Role2BbtPermission> permissionsBbt) {
    this.permissionsBbt = permissionsBbt;
  }

  public void removePermissionsBbt() {
    for (Role2BbtPermission perm : permissionsBbt) {
      perm.getBbt().getRolePermissions().remove(this);
    }
    permissionsBbt.clear();
  }

  public Set<BuildingBlockType> getBbtForPermissionType(EditPermissionType type) {
    Set<BuildingBlockType> bbts = Sets.newHashSet();
    for (Role2BbtPermission perm : permissionsBbt) {
      if (perm.getType().equals(type)) {
        bbts.add(perm.getBbt());
      }
    }
    return bbts;
  }

  public Role2BbtPermission getPermissionsBbt(TypeOfBuildingBlock tbb, EditPermissionType type) {
    for (Role2BbtPermission perm : getPermissionsBbt()) {
      if (perm.getBbt().getTypeOfBuildingBlock().equals(tbb) && perm.getType().equals(type)) {
        return perm;
      }
    }
    return null;
  }

  public Set<BuildingBlockType> getBbtForPermissionTypeAggregated(EditPermissionType type) {
    Set<BuildingBlockType> bbTypesAggregated = new HashSet<BuildingBlockType>();
    getBbtForPermissionTypeAggregatedHelper(this, type, bbTypesAggregated);
    return bbTypesAggregated;
  }

  private void getBbtForPermissionTypeAggregatedHelper(Role role, EditPermissionType type, Set<BuildingBlockType> bbTypesAggregated) {
    bbTypesAggregated.addAll(role.getBbtForPermissionType(type));
    for (Role r : role.getConsistsOfRoles()) {
      getBbtForPermissionTypeAggregatedHelper(r, type, bbTypesAggregated);
    }
  }

  public Set<PermissionFunctional> getPermissionsFunctional() {
    return this.permissionsFunctional;
  }

  public void setPermissionsFunctional(Set<PermissionFunctional> permissionsFunctional) {
    this.permissionsFunctional = permissionsFunctional;
  }

  public void removePermissionFunctionalTwoWay(PermissionFunctional pf) {
    if (this.getPermissionsFunctional() != null) {
      this.getPermissionsFunctional().remove(pf);
    }
    pf.getRoles().remove(this);
  }

  public void addPermissionFunctionalTwoWay(PermissionFunctional permFunctionalToAdd) {
    if (this.getPermissionsFunctional() == null) {
      this.setPermissionsFunctional(new HashSet<PermissionFunctional>());
    }
    this.getPermissionsFunctional().add(permFunctionalToAdd);
    permFunctionalToAdd.getRoles().add(this);
  }

  public void addPermissionFunctionals(Collection<PermissionFunctional> functionalPermissions) {
    for (PermissionFunctional permissionFunctional : functionalPermissions) {
      addPermissionFunctionalTwoWay(permissionFunctional);
    }
  }

  public void removePermissionFunctionals() {
    for (PermissionFunctional permissionFunctional : permissionsFunctional) {
      permissionFunctional.getRoles().remove(this);
    }

    permissionsFunctional.clear();
  }

  public Set<PermissionFunctional> getPermissionsFunctionalAggregated() {
    Set<PermissionFunctional> permissionFunctionalAggregated = new HashSet<PermissionFunctional>();
    getPermissionFunctionalAggregatedHelper(this, permissionFunctionalAggregated);
    return permissionFunctionalAggregated;
  }

  private void getPermissionFunctionalAggregatedHelper(Role role, Set<PermissionFunctional> permissionFunctionalAggregated) {
    permissionFunctionalAggregated.addAll(role.getPermissionsFunctional());
    for (Role r : role.getConsistsOfRoles()) {
      getPermissionFunctionalAggregatedHelper(r, permissionFunctionalAggregated);
    }
  }

  public String getLastModificationUser() {
    return this.lastModificationUser;
  }

  public void setLastModificationUser(String user) {
    this.lastModificationUser = user;
  }

  public Date getLastModificationTime() {
    return lastModificationTime;
  }

  public void setLastModificationTime(Date lastModificationTime) {
    this.lastModificationTime = lastModificationTime;
  }

  /**
   * Validates this instance for fullfulling all functional requirements.
   * 
   * @throws de.iteratec.iteraplan.common.error.IteraplanException If there was a validation error.
   */
  public void validate() {
    if (HierarchyHelper.hasAggregationCycleNM(this)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.AGGREGATED_ROLE_CYCLE);
    }
    for (PermissionAttrTypeGroup patg : getPermissionsAttrTypeGroup()) {
      if (!patg.isReadPermission().booleanValue() && patg.isWritePermission().booleanValue()) {
        // PermissionAttributetypegroup must be either read or read-write.
        // Just write is not allowed.
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }
    }
  }

  public void removePermissionsAttrTypeGroup(PermissionAttrTypeGroup patg) {
    patg.getRole().getPermissionsAttrTypeGroup().remove(patg);
    patg.getAttrTypeGroup().getPermissionsRole().remove(patg);

    permissionsAttrTypeGroup.remove(patg);
  }

  public void removePermissionsAttrTypeGroups() {
    for (PermissionAttrTypeGroup permissionAttrTypeGroup : Sets.newHashSet(permissionsAttrTypeGroup)) {
      removePermissionsAttrTypeGroup(permissionAttrTypeGroup);
    }

    permissionsAttrTypeGroup.clear();
  }

  public boolean isSupervisor() {
    return getRoleName().equals(SUPERVISOR_ROLE_NAME);
  }

  @Override
  public String toString() {
    return getRoleName();
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
    if (!(obj instanceof Role)) {
      return false;
    }
    final Role other = (Role) obj;
    if (id == null) {
      if (other.getId() != null) {
        return false;
      }
    }
    else if (!id.equals(other.getId())) {
      return false;
    }
    return true;
  }
}
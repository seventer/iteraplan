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
package de.iteratec.iteraplan.businesslogic.service;

import java.util.Collection;
import java.util.List;

import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.dto.PermissionAttrTypeGroupDTO;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;


/**
 * Service interface for {@link Role}s. 
 */
public interface RoleService extends EntityService<Role, Integer> {

  /**
   * Returns a filtered list of {@link BuildingBlockType}. The result may be 
   * limited to elements not included in the given elementsToExclude list.
   * 
   * @param elementsToExclude Elements which should not be included in the resulting list.
   * @return See method description.
   */
  List<BuildingBlockType> getAvailableBuildingBlockTypes(List<BuildingBlockType> elementsToExclude);

  /**
   * Returns a filtered list of {@link PermissionFunctional}. The result may be 
   * filtered (applied to the internationalized name of the functional permission)
   * and limited to elements not included in the given elementsToExclude list.
   * 
   * @param elementsToExclude Elements which should not be included in the resulting list.
   * @return See method description.
   */
  List<PermissionFunctional> getAvailableFunctionalPermissions(List<PermissionFunctional> elementsToExclude);

  /**
   * Returns a list of available {@link Role}s given the ID of the currently selected 
   * role in order to avoid selecting this role itself. The resulting list may be limited 
   * by an optional filter string and an optional list of roles to exclude.
   * 
   * To avoid creating cycles (that is, assigning the parent role as a child role) the 
   * list of available roles is further filtered to exclude parents of this role.
   * 
   * @param id The ID of the currently selected role.
   * @param elementsToExclude Elements which should not be included in the resulting list.
   * @return See method description.
   */
  List<Role> getAvailableRoles(Integer id, List<Role> elementsToExclude);

  /**
   * Creates a list of {@link PermissionAttrTypeGroupDTO}s which encapsulate a reference to a 
   * {@link de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup}. The result may be 
   * limited to elements not included in the given elementsToExclude list.
   * 
   * @param elementsToExclude The DTOs which encapsulate attribute type groups to ignore.
   * @return See method description.
   */
  List<PermissionAttrTypeGroupDTO> getPermissionsForAttributeTypeGroups(List<PermissionAttrTypeGroupDTO> elementsToExclude);

  /**
   * Returns the {@link Role} specified by the given ID.
   * 
   * @param id The ID of the role to retrieve.
   * @return See method description.
   */
  Role getRoleById(Integer id);

  /**
   * Returns the {@link Role} with the given name.
   * 
   * @param name The role's name.
   * @return See method description.
   */
  Role getRoleByName(String name);

  /**
   * Returns the iteraplan Supervisor {@link Role}.
   * 
   * @return See method description.
   */
  Role getSupervisorRole();

  /**
   * Forwards to {@link de.iteratec.iteraplan.persistence.dao.RoleDAO#getAllRolesFiltered()}
   * 
   * @return A list of all filtered roles
   */
  List<Role> getAllRolesFiltered();

  /**
   * Retrieves all Roles matching the search criteria contained in the given
   * {@link Role}
   * 
   * @param role A partially filled Role, whose properties will be used as search criteria
   * @return a list of user groups which match the specified search criteria.
   */
  List<Role> getRolesBySearch(Role role);

  /**
   * Reloads the specified {@link PermissionFunctional} and associates them with the current Hibernate
   * Session. 
   * 
   * @param permissions the functional permissions to reload
   * @return the reloaded functional permissions
   */
  List<PermissionFunctional> reloadFunctionalPermissions(Collection<PermissionFunctional> permissions);
  
  /**
   * Returns the PermissionFunctional which corresponds to the given type.
   * 
   * @param type The type of the functional permission to be returned.
   * @return the functional permission
   */
  PermissionFunctional getPermissionFunctionalByType(TypeOfFunctionalPermission type);

}
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

import java.util.List;

import de.iteratec.iteraplan.model.user.UserGroup;


/**
 * Service interface for {@link UserGroup}s.
 */
public interface UserGroupService extends EntityService<UserGroup, Integer> {

  /**
   * Returns a list of all {@link UserGroup}s.
   * @param id The userGroup with the id, and userGroups witch grants permissions from, will be excluded.
   *    Set to null if not needed.
   * @param elementsToExcelude The list of user group that should be excluded from the result. 
   *    Set to null if not needed.
   * @return See method description.
   */
  List<UserGroup> getAvailableUserGroupsToTransferPermissions(Integer id, List<UserGroup> elementsToExcelude);

  /**
   * Returns the {@link UserGroup} for the specified {@code id}.
   * 
   * @param id the user group id
   * @return the user group or {@code null}, if the user group with the specified {@code id} does not exist
   */
  UserGroup getUserGroupById(Integer id);

  /**
   * Returns a list of {@link UserGroup}s.
   * <ul>
   *    <li>The list is sorted by the name.</li> 
   *    <li>The list may be limited by an optional filter string which is applied 
   *        to the name.</li>
   *    <li>The user group with the given ID, if specified, is included even if it 
   *        doesn't match the filter condition.</li>
   *    <li>An optional list of user groups may be excluded from the result.</li>
   * </ul>
   *  
   * @param id The ID of the user group that is to be included in the result. Set to null if not needed.
   * @param elementsToExclude The list of user group that should be excluded from the result. Set to null if not needed.
   * @return See method description.
   */
  List<UserGroup> getUserGroupsFiltered(Integer id, List<UserGroup> elementsToExclude);

  /**
   * Retrieves all User groups matching the search criteria contained in the given
   * {@link UserGroup}
   * 
   * @param userGroup A partially filled User instance, whose properties will be used as search criteria
   * @return a list of user groups which match the specified search criteria.
   */
  List<UserGroup> getUserGroupsBySearch(UserGroup userGroup);
}
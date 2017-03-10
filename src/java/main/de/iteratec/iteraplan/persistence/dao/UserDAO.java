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
package de.iteratec.iteraplan.persistence.dao;

import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;


/**
 * DAO interface for {@link User}s.
 */
public interface UserDAO extends DAOTemplate<User, Integer> {

  /**
   * @param currentUserEntityId The id of a user or usergroup. The user or usergroup is always 
   *        included in the result, if it exists.
   * @param userEntityIdsToExclude The users or usergroups with this id are not included in 
   *        the result.
   * @return List of UserEntity. Can be empty, but is not null.
   */
  List<UserEntity> getAllUserEntitiesFiltered(Integer currentUserEntityId, Set<Integer> userEntityIdsToExclude);

  /**
   * @param loginName The login name of the user
   * @return The {@link User}, if exists.
   */
  User getUserByLoginIfExists(String loginName);

  /**
   * Loads and returns a list of building block IDs for which the given user entity ID  
   * has subscriptions.
   * 
   * @param userId user entity ID for which the IDs of owned building blocks shall be returned.
   * @return See method description.
   */
  List<Integer> loadSubscribedBuildingBlockIDs(final Integer userId);

  /**
   * Loads and returns a list of building block type IDs for which the given user entity ID  
   * has subscriptions.
   * 
   * @param userId user entity ID for which the IDs of owned building blocks shall be returned.
   * @return See method description.
   */
  List<Integer> loadSubscribedBuildingBlockTypeIDs(final Integer userId);
}
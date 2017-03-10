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

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.user.UserEntity;


/**
 * Service interface for {@link UserEntity}s.
 */
public interface UserEntityService extends EntityService<UserEntity, Integer> {

  /**
   * Loads and returns a {@code List} of {@link BuildingBlock}s for which the specified
   * {@link UserEntity} (user/user group) has got exclusive read/write permission. An instance
   * permission may be inherited from one or more parent user groups. These are considered as well,
   * if specified via the boolean {@code considerInheritedRights} parameter. If the given user
   * entity has not yet been persisted an empty {@code List} is returned. The {@code List} is sorted
   * by the building block's internationalized type string and their identity string.
   * 
   * @param userEntity The user entity for which owned building blocks shall be returned.
   * @param considerInheritedRights If {@code true}, inherited rights are considered as well.
   * @return See method description.
   * @throws IllegalArgumentException If the user entity is {@code null}.
   */
  List<BuildingBlock> loadOwnedBuildingBlocks(UserEntity userEntity, boolean considerInheritedRights);

  /**
   * Retrieves all User Entities matching the search criteria contained in the given
   * {@link UserEntity}
   * 
   * @param userEntity A partially filled UserEntity instance, whose properties will be used as search criteria
   * @return a list of UserEntities which match the specified search criteria.
   */
  List<UserEntity> getUserEntityBySearch(UserEntity userEntity);
}

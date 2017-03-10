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

import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.dto.PermissionAttrTypeGroupDTO;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;


/**
 * Service interface for {@link AttributeTypeGroup}s.
 */
public interface AttributeTypeGroupService extends EntityService<AttributeTypeGroup, Integer> {

  /**
   * Returns a list of all {@code AttributeTypeGroup}s sorted by their name.
   * 
   * @return all available attribute type groups
   */
  List<AttributeTypeGroup> getAllAttributeTypeGroups();

  /**
   * Returns the maximum position number a {@link AttributeTypeGroup} may have.
   * 
   * @return the maximum position number
   */
  Integer getMaxATGPositionNumber();

  /**
   * Returns a list of {@link PermissionAttrTypeGroupDTO}s which encapsulate a reference 
   * to a {@link de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup}. The result 
   * may be filtered by the name of the contained {@link de.iteratec.iteraplan.model.user.Role} 
   * and limited to elements not included in the given elementsToExclude list.
   * 
   * @param atgId
   *    The id of the {@link AttributeTypeGroup} the permissions are meant for
   * @param elementsToExclude 
   *    The {@link PermissionAttrTypeGroupDTO}s encapsulating roles to ignore.
   * @return 
   *    See description of method.
   */
  List<PermissionAttrTypeGroupDTO> getPermissionsForRoles(Integer atgId, List<PermissionAttrTypeGroupDTO> elementsToExclude);

  /**
   * Returns the default attribute type group.
   * 
   * @return the default attribute type group
   */
  AttributeTypeGroup getStandardAttributeTypeGroup();

  /**
   * Updates the position of the given {@link AttributeTypeGroup} with 
   * the given position. The new position is determined according to the
   * following rules: 
   * 
   * <ol>
   *    <li>If the position is the maximum currently persisted position, 
   *        the new position will be max(position) + 1 
   *    <li>If the position is the minimum currently persisted position, 
   *        the new position will be min(position) - 1
   *    <li>Otherwise the attribute type group will be set to the given 
   *        position and swapped with the attribute type group formerly
   *        occupying that position.
   * </ol>
   * 
   * @param entity the attribute type group, which position should be updated. 
   * @param position the new position of the given attribute type group.
   */
  void updatePosition(AttributeTypeGroup entity, Integer position);

  /**
   * Reloads the specified list of the {@link PermissionAttrTypeGroup} and reloads them by id. The returned
   * entities are associated with the current Hibernate Session.
   * 
   * @param groups the {@link PermissionAttrTypeGroup}s to reload
   * @return the reloaded entities
   */
  List<PermissionAttrTypeGroup> reloadPermissionAttrTypeGroups(Collection<PermissionAttrTypeGroup> groups);

  /**
   * Loads an {@link AttributeTypeGroup} by its name.
   * @param name The name of the {@link AttributeTypeGroup} to load.
   * @return the attribute type group with the given name.
   */
  AttributeTypeGroup getAttributeTypeGroupByName(final String name);

}
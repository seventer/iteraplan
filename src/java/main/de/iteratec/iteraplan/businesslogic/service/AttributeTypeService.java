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
import java.util.Set;

import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.RangeValue;


/**
 * Service methods for {@link AttributeType}s.
 */
public interface AttributeTypeService extends EntityService<AttributeType, Integer> {

  /**
   * Assures that the current user has the permission to read the given set of {@link AttributeType}
   * IDs.
   * 
   * @param ids
   *          The set of IDs for which the READ permission shall be checked.
   * @throws de.iteratec.iteraplan.common.error.IteraplanBusinessException
   *           If the user does not have a read permission for one or more attribute types with the
   *           given IDs.
   */
  void assureReadPermission(Set<Integer> ids);

  /**
   * Checks, if there are building blocks that have been assigned more than one {@code EnumAV} of
   * the given {@link EnumAT}.
   * 
   * @param attributeType
   *          The attribute type to check.
   * @throws de.iteratec.iterplan.common.IteraplanBusinessException
   *           If a building block exists that has been assigned more than one attribute value of
   *           the given attribute type.
   */
  void checkForMultipleAssignments(EnumAT attributeType);

  /**
   * Returns an {@link AttributeType}.
   * 
   * @param name
   *          The name of the attribute type that is to be included in the result.
   * @return See method description.
   */
  AttributeType getAttributeTypeByName(final String name);

  /**
   * Returns a filtered list of {@link AttributeType}s.
   * <ul>
   * <li>The list is sorted by the name.</li>
   * <li>The list may be limited by an optional filter string which is applied to the name.</li>
   * <li>An optional list of attribute types may be excluded from the result.</li>
   * </ul>
   * 
   * @param elementsToExclude The list of attribute types that is to be excluded from the result. 
   *    Set to null if not needed.
   * @return See method description.
   */
  List<AttributeType> getAttributeTypesFiltered(List<AttributeType> elementsToExclude);

  /**
   * Returns an ordered list (by position) of {@link AttributeType}s for the given
   * {@link de.iteratec.iteraplan.model.BuildingBlockType}.
   * 
   * @param type
   *          The type of building block for which the list of activated attribute types shall be
   *          returned.
   * @param enforceReadPermissions If set to true all attribute types are removed from the result
   *            list, which are in attribute type groups for which the currently logged in user has
   *            no read permission.
   * @return See method description.
   */
  List<AttributeType> getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock type, boolean enforceReadPermissions);

  /**
   * Retrieves all attribute types matching the search criteria contained in the given
   * {@link AttributeType}.
   * 
   * @param attributeType
   *          A partially filled AttributeType instance, whose properties will be used as search
   *          criteria
   * @return a list of attribute types which match the specified search criteria.
   */
  List<AttributeType> getAttributeBySearch(AttributeType attributeType);

  /**
   * Loads the {@link AttributeType} for the specified {@code id} and {@code clazz}.
   * 
   * @param <AT> the {@link AttributeType} type 
   * @param id the atributte type id
   * @param clazz the atribute type class
   * @return the loaded attribute type or {@code null}, if the {@code id} equals {@code null} or the
   *    entity could not be found
   */
  <AT extends AttributeType> AT loadObjectById(Integer id, Class<AT> clazz);

  /**
   * Reloads all range values in the given {@code Collection} and returns them as a {@code List}.
   * 
   * @return The list of reloaded range values or an empty list if the argument is {@code null} or
   *         empty.
   */
  List<RangeValue> reloadRangeValues(Collection<RangeValue> identifiers);

  /**
   * Checks if a special attribute type is a {@link NumberAT}
   * 
   * @param attributeTypeId the ID of the attributeType
   * @return {@code true} if type is {@link NumberAT}, otherwise returns {@code false}
   */
  boolean isNumberAT(Integer attributeTypeId);
  
  /**
   * Collect all DateATs
   * 
   * @return {@link DateAT}
   */
  List<DateAT> getAllDateAT();

}
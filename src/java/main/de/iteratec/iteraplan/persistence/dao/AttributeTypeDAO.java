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

import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;


/**
 * DAO interface for {@link AttributeType}s.
 */
public interface AttributeTypeDAO extends DAOTemplate<AttributeType, Integer> {

  /**
   * Returns the List of AttributeTypes which are activated for the BuildingBlockType identified by
   * the given TypeOfBuildingBlock. Optionally all attribute types can be removed for which the
   * currently logged in user has no read permission.
   * 
   * @param typeOfBB The identifying TypeOfBuildingBlock.
   * @param enforceReadPermissions If set to true all attribute types are removed from the result
   *            list, which are in attribute type groups for which the currently logged in user has
   *            no read permission.
   * @return List of AttributeType.
   */
  List<AttributeType> getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock typeOfBB, boolean enforceReadPermissions);

  /**
   * @param name The name of the attribute type.
   * @return The attribute type with the given name.
   */
  AttributeType getAttributeTypeByName(String name);

  /**
   * Returns a list of {@link AttributeType}s that have got an assigned attribute value which
   * itself contains a reference to a {@link de.iteratec.iteraplan.model.user.UserEntity} with 
   * the given ID. This essentially returns attributes of type responsibiliy.
   * 
   * @param id 
   *    The ID of the user entity.
   *    
   * @return 
   *    See method description.
   */
  List<AttributeType> getResponsibilityAttributeTypesReferencingUserEntityID(Integer id);

  /**
   * Returns an AttributeType of a given implementing class
   * 
   * @param attributeId The id of the attribute type
   * @param clazz The class of the attribute type
   * @return The attribute type, or null if it does not exist
   */
  <AT extends AttributeType> AT loadObjectById(Integer attributeId, Class<AT> clazz);

}
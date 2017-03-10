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

import org.springframework.dao.DataAccessException;

import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Service interface for {@link BuildingBlockType}s.
 */
public interface BuildingBlockTypeService extends Service, SubscribeService {

  /**
   * Loads the {@link BuildingBlockType} for the specified {@code id}. If the {@code id} 
   * equals {@code null}, this method returns {@code null}.
   * 
   * @param id the building block type id
   * @return the {@link BuildingBlockType} instance
   * @throws DataAccessException in case the entity does not exist
   */
  BuildingBlockType loadObjectById(Integer id) throws DataAccessException;

  /**
   * Returns an object of type {@link BuildingBlockType} which is of type
   * {@link TypeOfBuildingBlock}.
   * 
   * @param typeOfBuildingBlock the type of the building block.
   * @return the existing {@link BuildingBlockType} instance
   */
  BuildingBlockType getBuildingBlockTypeByType(TypeOfBuildingBlock typeOfBuildingBlock);

  /**
   * Returns a list of all {@code BuildingBlockType}s that can have user defined attributes. The
   * list is sorted by the types' localized name.
   * 
   * @return See method description.
   */
  List<BuildingBlockType> getBuildingBlockTypesEligibleForAttributes();

  /**
   * Returns all {@link BuildingBlockType}s that are available for the attribute type system and are
   * not yet connected to the {@link de.iteratec.iteraplan.model.attribute.AttributeType} with the
   * given id.
   * 
   * @param attributeTypeId the ID of the attribute type for which available building block types 
   *    should be returned
   * @return See method description.
   */
  List<BuildingBlockType> getAvailableBuildingBlockTypesForAttributeType(Integer attributeTypeId);

  /**
   * Reloads the specified list of the {@link BuildingBlockType} instances and associates them with 
   * the Hibernate Session.
   * 
   * @param entities the list of building block types to reload
   * @return the reloaded building block types
   */
  List<BuildingBlockType> reload(Collection<BuildingBlockType> entities);

  /**
   * Saves or updates the given instance of an type with the given session instance.
   * 
   * @param type the instance to save or update.
   * @return the saved or updated entity
   */
  BuildingBlockType saveOrUpdate(BuildingBlockType type);

  List<BuildingBlockType> getAllBuildingBlockTypesForDisplay();
}

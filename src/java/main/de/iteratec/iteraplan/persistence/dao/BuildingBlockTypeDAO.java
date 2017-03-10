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

import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * DAO interface for {@link BuildingBlockType}s.
 */
public interface BuildingBlockTypeDAO extends DAOTemplate<BuildingBlockType, Integer> {

  /**
   * Returns all {@link BuildingBlockType}s that are available for the attribute type system and are
   * not yet connected to the {@link de.iteratec.iteraplan.model.attribute.AttributeType} with the
   * given id.
   * 
   * @param id
   *          The ID of the attribute type for which available building block types should be
   *          returned.
   * @return See method description.
   */
  List<BuildingBlockType> getAvailableBuildingBlockTypesForAttributeType(Integer id);

  /**
   * Returns all {@link BuildingBlockType}s that are connected to the
   * {@link de.iteratec.iteraplan.model.attribute.AttributeType} with the given id.
   * 
   * @param id
   *          The ID of the attribute type for which available building block types should be
   *          returned.
   * @return See method description.
   */
  List<BuildingBlockType> getConnectedBuildingBlockTypesForAttributeType(Integer id);

  /**
   * Returns the persisted {@link BuildingBlockType} for the given {@link TypeOfBuildingBlock}. If
   * no such object is found, an exception is thrown.
   * 
   * @param type
   *          The type of building block for the building block type to load from the database.
   * @throws de.iteratec.iteraplan.common.error.IteraplanTechnicalException
   *           If no building block type for the given type of building block is found.
   */
  BuildingBlockType getBuildingBlockTypeByType(TypeOfBuildingBlock type);

  /**
   * Returns a list of all {@code BuildingBlockType}s that may have user defined attributes.
   * 
   * @return See method description.
   */
  List<BuildingBlockType> getBuildingBlockTypesEligibleForAttributes();

}

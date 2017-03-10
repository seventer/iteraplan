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

import java.util.Collection;
import java.util.List;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * DAO interface for general operations on all kinds of {@link BuildingBlock}s.
 */
public interface GeneralBuildingBlockDAO extends DAOTemplate<BuildingBlock, Integer> {

  /**
   * Loads and returns the {@link BuildingBlock} of the specified class identified by the given ID.
   * The method assumes that an entity exists for the identifier. Otherwise an exception is thrown.
   * 
   * @param id
   *          The ID of the entity to load.
   * @param clazz
   *          The class of the entity.
   * @return See method description.
   * @throws IllegalArgumentException
   *           If any of the parameters are {@code null}.
   */
  BuildingBlock getBuildingBlock(final Integer id, final Class<? extends BuildingBlock> clazz);

  /**
   * Returns all {@link BuildingBlock}s in the database for a given {@link TypeOfBuildingBlock}.
   * 
   * @param type
   *          The type of building block.
   * @return See method description.
   * @throws IllegalArgumentException
   *           If the parameter is {@code null}.
   */
  List<BuildingBlock> getBuildingBlocksByType(TypeOfBuildingBlock type);

  /**
   * Loads and returns a list of {@link BuildingBlock}s that are identified by the given collection
   * of identifiers.
   * 
   * @param identifiers
   *          The collection of building block identifiers.
   * @return A list of {@link BuildingBlock}s or an empty list if the given collection of identifiers
   *         is empty or no matching building blocks could be found.
   * @throws IllegalArgumentException
   *           If any of the parameters are {@code null}.
   * @deprecated do not use this method any more, because it a performance disaster....
   */
  List<BuildingBlock> loadBuildingBlocks(final Collection<Integer> identifiers);

  /**
   * Loads and returns a list of {@link BuildingBlock}s of the specified class that are identified
   * by the given collection of identifiers. Note that this method may return a subset of building
   * blocks if not all of the provided identifiers could be matched to the given class.
   * 
   * @param identifiers
   *          The collection of building block identifiers.
   * @param clazz
   *          The class of the entity.
   * @return A list of {@link BuildingBlock}s or an empty list if the given collection of identifiers
   *         is empty or no matching building blocks could be found.
   * @throws IllegalArgumentException
   *           If any of the parameters are {@code null}.
   */
  List<BuildingBlock> loadBuildingBlocks(final Collection<Integer> identifiers, final Class<? extends BuildingBlock> clazz);

}
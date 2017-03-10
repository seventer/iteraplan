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
import java.util.Map;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.Timeseries;


public interface TimeseriesService extends Service {

  /**
   * Only for tests
   */
  void setTimeseriesEnabled(boolean timeseriesEnabled);

  /**
   * Returns the instance of the {@link Timeseries} with the given identifier.
   * If the {@link Timeseries} does not exist, an exception is thrown.
   * 
   * @param id the ID of the Timeseries to load
   * @return The Timeseries. An exception is thrown if the entity does not exist. If the passed-in ID is
   *         {@code null}, {@code null} is returned.
   */
  Timeseries loadObjectById(Integer id);

  /**
   * Returns the instance of the {@link Timeseries} with the given identifier.
   * If the {@link Timeseries} does not exist, null is returned.
   * 
   * @param id the ID of the Timeseries to load
   * @return The Timeseries. Null if the entity does not exist. If the passed-in ID is
   *         {@code null}, {@code null} is returned.
   */
  Timeseries loadObjectByIdIfExists(Integer id);

  /**
   * Loads the {@link Timeseries} object assigned to the given building block and attribute type.
   * @param bb
   *          The {@link BuildingBlock}
   * @param at
   *          The {@link AttributeType}, does not have to be a timeseries attribute type
   * @return The loaded Timeseries or null, if no Timeseries exists for bb and at.
   */
  Timeseries loadTimeseriesByBuildingBlockAndAttributeType(BuildingBlock bb, AttributeType at);

  /**
   * Deletes the given timeseries.
   * @param toDelete
   *          The timeseries to delete.
   */
  void deleteTimeseries(Timeseries toDelete);

  /**
   * Saves or updates the given instance of a {@link Timeseries} with the given session instance.
   * Also updates the building block of the given timeseries, setting the latest value of the
   * timeseries for the according attribute type.
   * 
   * @param timeseries the instance to save or update.
   * @return the saved or updated Timeseries
   */
  Timeseries saveOrUpdateWithBbUpdate(Timeseries timeseries);

  /**
   * Saves or updates the given instance of a {@link Timeseries} with the given session instance.
   * Does not update the building block of the given timeseries.
   * 
   * 
   * @param timeseries the instance to save or update.
   * @return the saved or updated Timeseries
   */
  Timeseries saveOrUpdateWithoutBbUpdate(Timeseries timeseries);

  /**
   * Updates the building block by setting the current value of the given timeseries
   * for the according attribute type in the building block.
   * If the timeseries does not have any entries, the attribute value assigned to the
   * building block will be "null", thus un-setting the attribute.
   * @param timeseries
   *          The {@link Timeseries} whose building block should be updated.
   * @param buildingBlock
   *          The {@link BuildingBlock} to update
   * @return The updated building block
   */
  BuildingBlock updateBuildingBlockAttribute(Timeseries timeseries, BuildingBlock buildingBlock);

  /**
   * Deletes all Timeseries related to the given attribute type.
   * @param at
   *          The attribute type
   * @return The number of deleted timeseries objects
   */
  Integer deleteTimeseriesByAttributeType(final AttributeType at);

  /**
   * Deletes all Timeseries related to the given building blocks.
   * @param bb
   *          The collection of building blocks whose timeseries should be deleted
   * @return The number of deleted timeseries objects
   */
  Integer deleteTimeseriesByBuildingBlocks(final Collection<? extends BuildingBlock> bb);

  /**
   * Loads the Timeseries objects assigned to the given building blocks and attribute type
   * 
   * @param bbs the {@link BuildingBlock}s
   * @param at the {@link AttributeType}
   * @return a Map mapping the {@link BuildingBlock}s to their Timeseries of the specified {@link AttributeType}
   */
  Map<? extends BuildingBlock, Timeseries> loadTimeseriesForBuildingBlocks(Collection<? extends BuildingBlock> bbs, AttributeType at);
}

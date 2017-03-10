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
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAttributeConfig;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateComponentModel;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateLine;

/**
 * A service for updating and deleting multiple Entities at the same time. 
 */
public interface MassUpdateService {

  /**
   * Initializes a component model in mass update mode. To gain performance, only properties and
   * associations selected by the user to include in the mass update are initialized.
   * 
   * @param <T> Generic parameter for BuildingBlocks
   * @param componentModel The component model that will be initialized
   * @param buildingBlock
   *          The BuildingBlock that will be initialized. The object is detached and has been
   *          retrieved by the standard report functionality. One can hence not guarantee that all
   *          necessary associations are initialized. The service method therefore re-retrieves the
   *          object from the database and initializes the associations (to avoid
   *          LazyLoadingExceptions)
   * @param properties The properties that will be initialized
   * @param associations The associations that will be initialized
   * @return The BuildingBlock having all the necessary associations initialized
   */
  <T extends BuildingBlock> T initComponentModel(MassUpdateComponentModel<T> componentModel, T buildingBlock, List<String> properties,
                                                 List<String> associations);

  /**
   * Initializes the data needed for mass updating attributes
   * 
   * @param selectedAttributeId The attribute ID as coming from the GUI (consisting of the id of the attribute and
   *          the attribute type (enum, string, ...)
   * @param lines Lines containing the BuildingBlocks taking part in the update
   * @param massUpdateAttributeConfig The list to which the GUI configuration for each attribute will be added
   * @param attributeIndex The index of the attribute. Indicates at which position (e.g. which column) the
   *          attribute will be displayed in the update mask
   */
  void initAttributes(String selectedAttributeId, List<MassUpdateLine<? extends BuildingBlock>> lines,
                      List<MassUpdateAttributeConfig> massUpdateAttributeConfig, int attributeIndex);

  /**
   * Delete a single building Block and its successors
   * 
   * @param buildingBlock the building block to be deleted
   */
  void deleteBuildingBlock(BuildingBlock buildingBlock);

  /**
   * Subscribes or unsubscribes a single Building Block.
   * 
   * @param buildingBlock the building block to be subscribed or unsubscribed
   * @param subscribe a flag indicating if the building block must be subscribed ({@code true}) or unsubscribed ({@code false})
   */
  void subscribeBuildingBlock(BuildingBlock buildingBlock, boolean subscribe);

  /**
   * Updates a single line of a mass update
   * 
   * @param <T> The underlying BuildingBlock
   * @param line The line to update
   */
  <T extends BuildingBlock> void updateLine(MassUpdateLine<T> line);

  /**
   * Updates a single line of a mass update for {@code BusinessMapping}s.
   * 
   * @param <T> The underlying BuildingBlock (implicitly bound to ipurelease!)
   * @param line The line to update
   */
  <T extends BuildingBlock> void updateBusinessMappingLine(MassUpdateLine<T> line);

  /**
   * Update the Attributes
   * 
   * @param <T> The underlying BuildingBlock
   * @param line The line to update
   * @return <code>true</code> if at least one Attribute was updated, <code>false</code> else
   */
  <T extends BuildingBlock> boolean updateAttributes(MassUpdateLine<T> line);

}
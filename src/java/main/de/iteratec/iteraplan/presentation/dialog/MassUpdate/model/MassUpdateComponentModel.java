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
package de.iteratec.iteraplan.presentation.dialog.MassUpdate.model;

import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;


/**
 * Interface for component models used in mass update mode
 * 
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 *
 */
public interface MassUpdateComponentModel<T extends BuildingBlock> extends ComponentModel<T> {

  /**
   * Initilizes the component model from a given buildingblock
   * @param properties The properties to be fetched (using the Strings from the respective {@link de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType})
   * @param associations The associations to be fetched (using the Strings from the respective {@link de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType})
   */
  void initializeFrom(T buildingBlock, List<String> properties, List<String> associations);

  /**
   * Returns a HashMap mapping the name property of a {@link de.iteratec.iteraplan.businesslogic.reports.query.type.SimpleAssociation} (Key)
   * to component model access paths (value) - i.e. the getter method that returns the component model for a specific property
   * @return The component model assignment
   */
  Map<String, String> getComponentModelAssignment();

  /**
   * Returns a HashMap, mapping the name property of a {@link de.iteratec.iteraplan.businesslogic.reports.query.type.SimpleAssociation} (Key)
   * to the name of the getter method that returns the component model, which represents the association (value) - i.e. the 
   * getter method that returns the ManyAssociationSetComponentModel that represents the association of a building block to another type of building block.
   * This is name is used to gain access to the connected elements of the association's component model
   * by reflection. See {@link de.iteratec.iteraplan.presentation.dialog.MassUpdate.MassUpdateFrontendServiceImpl}.
   * @return The component model assignment
   */
  Map<String, String> getAssociationGetMethodAssignment();

}

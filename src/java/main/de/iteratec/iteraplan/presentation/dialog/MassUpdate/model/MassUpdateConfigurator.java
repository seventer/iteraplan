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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Property;
import de.iteratec.iteraplan.businesslogic.reports.query.type.SimpleAssociation;
import de.iteratec.iteraplan.model.BuildingBlock;


/**
 * Configures the meta information needed to configure the mass update view for a specific BuildingBlock. Determines
 * which properties the user wants to update and assigns the appropriate path to the componentmodel of each attribute. 
 * 
 * @see MassUpdatePropertyConfig
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 *
 */
public class MassUpdateConfigurator {

  /**
   * Create a {@link MassUpdatePropertyConfig} for each property the user selected. MassUpdatePropertyConfig 
   * links the properties to a component model and holds the I18N key displayed in the GUI.
   * @param type The {@link MassUpdateType} the properties refer to
   * @param properties The properties that were selected by the user
   * @param componentModel The component model containing a hashmap with the mapping from property name to component model method. 
   * @return The initialized property configuration
   */
  public List<MassUpdatePropertyConfig> configureProperties(MassUpdateType type, List<String> properties,
                                                            MassUpdateComponentModel<? extends BuildingBlock> componentModel) {
    List<MassUpdatePropertyConfig> ret = new ArrayList<MassUpdatePropertyConfig>();
    Map<String, String> assignment = componentModel.getComponentModelAssignment();
    for (String propId : properties) {
      Property property = type.getPropertyFromId(propId);
      MassUpdatePropertyConfig config = new MassUpdatePropertyConfig();
      config.setHeaderKey(property.getNamePresentationKey());
      config.setPathToComponentModel(assignment.get(property.getNameDB()));
      config.setPosition(property.getPosition());
      ret.add(config);
    }
    Collections.sort(ret);
    return ret;
  }

  /**
   * Creates a {@link MassUpdateAssociationConfig} for each association the user selected for mass update. A MassUpdateAssociationConfig object
   * links the association to the component model name of the associated building block and holds the I18N key displayed in the GUI.
   * It also holds the name of the getter method that is used to get the component model representing the association (used to invoke the method 
   * by reflection in {@link de.iteratec.iteraplan.presentation.dialog.MassUpdate.MassUpdateFrontendServiceImpl}).
   * 
   * @param type The {@link MassUpdateType} the associations refer to
   * @param associations The associations that were selected by the user
   * @param componentModel The component model containing a hashmap with the mapping from association name to component model method. 
   * @return The initialized association configuration
   */
  public List<MassUpdateAssociationConfig> configureAssociations(MassUpdateType type, List<String> associations,
                                                                 MassUpdateComponentModel<? extends BuildingBlock> componentModel) {
    List<MassUpdateAssociationConfig> ret = new ArrayList<MassUpdateAssociationConfig>();
    Map<String, String> cmAssignment = componentModel.getComponentModelAssignment();
    Map<String, String> associationGetMethodAssignment = componentModel.getAssociationGetMethodAssignment();
    for (String associationId : associations) {
      SimpleAssociation association = type.getSimpleAssociationFromId(associationId);
      MassUpdateAssociationConfig config = new MassUpdateAssociationConfig();
      config.setHeaderKey(association.getNamePresentationKey());
      config.setPathToComponentModel(cmAssignment.get(association.getName()));
      config.setMethodNameOfGetModelOfAssociationMethod(associationGetMethodAssignment.get(association.getName()));
      config.setPosition(association.getPosition());
      config.setCardinality(association.getCardinalityString());
      ret.add(config);
    }
    Collections.sort(ret);
    return ret;
  }
}

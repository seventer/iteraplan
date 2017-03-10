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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.model.BuildingBlock;


/**
 * Holds one BuildingBlock and all mass update related data for the corresponding instance.
 * 
 * When running a mass update, an instance of this class is created for each BuildingBlock
 * that is to be updated. It holds the instance to update, the new values and associations
 * that are set by the user and status information about the update.
 * @version 2.x
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 *
 */
public class MassUpdateLine<T extends BuildingBlock> implements Serializable {

  /** Serialization version. */
  private static final long           serialVersionUID      = 9063015591203729250L;

  /** BuildingBlock for which the property update should be done. */
  private T                           buildingBlockToUpdate;

  /** Data and information about user defined attributes that will be updated */
  private List<MassUpdateAttribute>   attributes            = new ArrayList<MassUpdateAttribute>();

  /** Booleans in the list represent checkboxes in the GUI (MassUpdateLines.jsp) that
   *  enable/disable standard association take over. See MassUpdateFrontendService for
   *  more detailed explanation */
  private List<Boolean>               associations          = new ArrayList<Boolean>();

  private MassUpdateResult            massUpdateResult;

  /** When a mass update is to be executed, this line should be processed. */
  private boolean                     selectedForMassUpdate = true;

  private MassUpdateComponentModel<T> componentModel        = null;

  public T getBuildingBlockToUpdate() {
    return buildingBlockToUpdate;
  }

  public MassUpdateComponentModel<T> getComponentModel() {
    return componentModel;
  }

  public boolean isSelectedForMassUpdate() {
    return selectedForMassUpdate;
  }

  @SuppressWarnings("unchecked")
  public void setBuildingBlockToUpdate(BuildingBlock buildingBlockToUpdate) {
    this.buildingBlockToUpdate = (T) buildingBlockToUpdate;
  }

  public void setComponentModel(MassUpdateComponentModel<T> componentModel) {
    this.componentModel = componentModel;
  }

  public void setSelectedForMassUpdate(boolean selectedForMassUpdate) {
    this.selectedForMassUpdate = selectedForMassUpdate;
  }

  public List<MassUpdateAttribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<MassUpdateAttribute> attributes) {
    this.attributes = attributes;
  }

  public void addAttribute(String attributeId, MassUpdateAttribute attribute) {
    attribute.setSelectedAttributeId(attributeId);
    this.attributes.add(attribute);
  }

  public MassUpdateResult getMassUpdateResult() {
    return massUpdateResult;
  }

  public void setMassUpdateResult(MassUpdateResult result) {
    this.massUpdateResult = result;
  }

  /**
   * Returns the MassUpdateAttribute with a given ID. <code>null</code> if the ID does not exist
   * @param attributeTypeId The id of the attribute to return
   * @return The attribute. <code>null</code> if the given ID cannot be found in the list
   */
  public MassUpdateAttribute getMassUpdateAttribute(Integer attributeTypeId) {
    for (MassUpdateAttribute attribute : attributes) {
      if (attribute.getAttributeTypeId().equals(attributeTypeId)) {
        return attribute;
      }
    }
    return null;
  }

  public void setAssociations(List<Boolean> associations) {
    this.associations = associations;
  }

  public List<Boolean> getAssociations() {
    return associations;
  }
}

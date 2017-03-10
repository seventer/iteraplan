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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.query.type.InfrastructureElementTypeMu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.presentation.dialog.InfrastructureElement.InfrastructureElementComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.NullSafeModel;


public class InfrastructureElementCmMu extends InfrastructureElementComponentModel implements MassUpdateComponentModel<InfrastructureElement> {

  /** Serialization version. */
  private static final long                     serialVersionUID = 3278539367618151652L;

  private ComponentModel<InfrastructureElement> nullSafeModel    = new NullSafeModel<InfrastructureElement>();

  private boolean                               namePropertyAvailable;
  private boolean                               descriptionPropertyAvailable;
  private boolean                               parentAssociationAvailable;
  private boolean                               baseComponentAssociationAvailable;
  private boolean                               parentComponentAssociationAvailable;
  private boolean                               informationSystemReleaseAssociationAvailable;
  private boolean                               technicalComponentReleaseAssociationAvailable;

  public InfrastructureElementCmMu() {
    super(ComponentMode.EDIT);

    String[] header = new String[] { NAME_LABEL };
    String[] fields = new String[] { HIERARCHICAL_NAME };
    super.getBaseComponentsModel().updateHeaderAndFields(header, fields);
    super.getParentComponentsModel().updateHeaderAndFields(header, fields);
    super.getInformationSystemReleaseModel().updateHeaderAndFields(header, fields);
    super.getTechnicalComponentReleaseModel().updateHeaderAndFields(header, fields);
  }

  public Map<String, String> getComponentModelAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();
    assignment.put(InfrastructureElementTypeMu.PROPERTY_NAME, "nameComponentModel");
    assignment.put(InfrastructureElementTypeMu.PROPERTY_DESCRIPTION, "descriptionComponentModel");
    assignment.put(InfrastructureElementTypeMu.ASSOCIATION_PARENT, "parentComponentModel");
    assignment.put(InfrastructureElementTypeMu.ASSOCIATION_BASECOMPONENTS, "baseComponentsComponentModel");
    assignment.put(InfrastructureElementTypeMu.ASSOCIATION_PARENTCOMPONENTS, "parentComponentsComponentModel");
    assignment.put(InfrastructureElementTypeMu.ASSOCIATION_INFORMATIONSYSTEMRELEASES, "informationSystemReleaseComponentModel");
    assignment.put(InfrastructureElementTypeMu.ASSOCIATION_TECHNICALCOMPONENTRELEASE_ASSOCIATIONS, "technicalComponentReleaseComponentModel");

    return assignment;
  }

  /**
   * @return Returns a map of getter names, which are used to gain access to the component model by reflection.
   *         See MassUpdateFrontendService.
   */
  public Map<String, String> getAssociationGetMethodAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();

    assignment.put(InfrastructureElementTypeMu.ASSOCIATION_PARENT, "getParentModel");
    assignment.put(InfrastructureElementTypeMu.ASSOCIATION_BASECOMPONENTS, "getBaseComponentsModel");
    assignment.put(InfrastructureElementTypeMu.ASSOCIATION_PARENTCOMPONENTS, "getParentComponentsModel");
    assignment.put(InfrastructureElementTypeMu.ASSOCIATION_INFORMATIONSYSTEMRELEASES, "getInformationSystemReleaseModel");
    assignment.put(InfrastructureElementTypeMu.ASSOCIATION_TECHNICALCOMPONENTRELEASE_ASSOCIATIONS, "getTechnicalComponentReleaseModel");

    return assignment;
  }

  public void initializeFrom(InfrastructureElement buildingBlock, List<String> properties, List<String> associations) {
    MassUpdateType type = InfrastructureElementTypeMu.getInstance();

    // init the properties that are part of the mass update
    for (String property : properties) {
      if (type.isPropertyIdEqual(property, InfrastructureElementTypeMu.PROPERTY_NAME)) {
        namePropertyAvailable = true;
        getNameModel().initializeFrom(buildingBlock);
      }
      if (type.isPropertyIdEqual(property, InfrastructureElementTypeMu.PROPERTY_DESCRIPTION)) {
        descriptionPropertyAvailable = true;
        getDescriptionModel().initializeFrom(buildingBlock);
      }
    }
    for (String association : associations) {
      if (association.equals(InfrastructureElementTypeMu.ASSOCIATION_INFORMATIONSYSTEMRELEASES)) {
        informationSystemReleaseAssociationAvailable = true;
      }
      if (association.equals(InfrastructureElementTypeMu.ASSOCIATION_PARENT)) {
        parentAssociationAvailable = true;
      }
      if (association.equals(InfrastructureElementTypeMu.ASSOCIATION_BASECOMPONENTS)) {
        baseComponentAssociationAvailable = true;
      }
      if (association.equals(InfrastructureElementTypeMu.ASSOCIATION_PARENTCOMPONENTS)) {
        parentComponentAssociationAvailable = true;
      }
      if (association.equals(InfrastructureElementTypeMu.ASSOCIATION_TECHNICALCOMPONENTRELEASE_ASSOCIATIONS)) {
        technicalComponentReleaseAssociationAvailable = true;
      }
    }
    super.getParentModel().initializeFrom(buildingBlock);
    super.getBaseComponentsModel().initializeFrom(buildingBlock);
    super.getParentComponentsModel().initializeFrom(buildingBlock);

    // TODO in der TCRCmMu wird die init in der for-Schleife gemacht?!
    super.getInformationSystemReleaseModel().initializeFrom(buildingBlock);

    super.getTechnicalComponentReleaseModel().initializeFrom(buildingBlock);
  }

  @Override
  public void update() throws IteraplanException {
    getNameComponentModel().update();
    getDescriptionComponentModel().update();
    getParentComponentModel().update();
    getBaseComponentsComponentModel().update();
    getParentComponentsComponentModel().update();
    getInformationSystemReleaseComponentModel().update();
    getTechnicalComponentReleaseComponentModel().update();
  }

  public ComponentModel<InfrastructureElement> getNameComponentModel() {
    return namePropertyAvailable ? getNameModel() : nullSafeModel;
  }

  public ComponentModel<InfrastructureElement> getDescriptionComponentModel() {
    return descriptionPropertyAvailable ? getDescriptionModel() : nullSafeModel;
  }

  public ComponentModel<InfrastructureElement> getParentComponentModel() {
    return parentAssociationAvailable ? getParentModel() : nullSafeModel;
  }

  public ComponentModel<InfrastructureElement> getBaseComponentsComponentModel() {
    return baseComponentAssociationAvailable ? getBaseComponentsModel() : nullSafeModel;
  }

  public ComponentModel<InfrastructureElement> getParentComponentsComponentModel() {
    return parentComponentAssociationAvailable ? getParentComponentsModel() : nullSafeModel;
  }

  public ComponentModel<InfrastructureElement> getInformationSystemReleaseComponentModel() {
    return informationSystemReleaseAssociationAvailable ? getInformationSystemReleaseModel() : nullSafeModel;
  }

  public ComponentModel<InfrastructureElement> getTechnicalComponentReleaseComponentModel() {
    return technicalComponentReleaseAssociationAvailable ? getTechnicalComponentReleaseModel() : nullSafeModel;
  }

  @Override
  public void configure(InfrastructureElement target) throws IteraplanException {
    getNameComponentModel().configure(target);
    getDescriptionComponentModel().configure(target);
    getParentComponentModel().configure(target);
    getBaseComponentsComponentModel().configure(target);
    getParentComponentsComponentModel().configure(target);
    getInformationSystemReleaseComponentModel().configure(target);
    getTechnicalComponentReleaseComponentModel().configure(target);
  }
}
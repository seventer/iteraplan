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

import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemInterfaceTypeMu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface.InformationSystemInterfaceComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;


/**
 * GUI model for the mass update of {@link InformationSystemInterface}s.
 */
public class InformationSystemInterfaceCmMu extends InformationSystemInterfaceComponentModel implements
    MassUpdateComponentModel<InformationSystemInterface> {

  /** Serialization version. */
  private static final long serialVersionUID              = 9009317742996889764L;
  private boolean           namePropertyAvailable         = false;
  private boolean           descriptionPropertyAvailable  = false;
  private boolean           transportAssociationAvailable = false;
  private boolean           directionAvailable            = false;
  private boolean           tcrAssociationAvailable       = false;

  public InformationSystemInterfaceCmMu() {
    super(ComponentMode.EDIT);

    String[] header = new String[] { NAME_LABEL };

    super.getTechnicalComponentReleaseModel().updateHeaderAndFields(header, new String[] { "hierarchicalName" });
    super.getTransportModel().updateHeaderAndFields(header, new String[] { "hierarchicalName" });
  }

  public ComponentModel<InformationSystemInterface> getNameComponentModel() {
    if (namePropertyAvailable) {
      return super.getNameModel();
    }
    return null;
  }

  public ComponentModel<InformationSystemInterface> getDirectionComponentModel() {
    if (directionAvailable) {
      return super.getTransportInfoModel();
    }
    return null;
  }

  public ComponentModel<InformationSystemInterface> getTcrComponentModel() {
    if (tcrAssociationAvailable) {
      return super.getTechnicalComponentReleaseModel();
    }
    return null;
  }

  public ComponentModel<InformationSystemInterface> getDescriptionComponentModel() {
    if (descriptionPropertyAvailable) {
      return super.getDescriptionModel();
    }
    return null;
  }

  /**
   * Returns the name of the managed class. Needed to access the custom tile for business objects
   * associations.
   */
  public String getManagedClassAsString() {
    return InformationSystemInterface.class.getSimpleName();
  }

  public ComponentModel<InformationSystemInterface> getTransportComponentModel() {
    if (transportAssociationAvailable) {
      return super.getTransportModel();
    }
    return null;
  }

  @Override
  public void configure(InformationSystemInterface target) {

    if (namePropertyAvailable) {
      getNameComponentModel().configure(target);
    }

    if (descriptionPropertyAvailable) {
      getDescriptionComponentModel().configure(target);
    }

    if (directionAvailable) {
      getDirectionComponentModel().configure(target);
    }

    if (transportAssociationAvailable) {
      getTransportModel().configure(target);
    }

    if (tcrAssociationAvailable) {
      getTechnicalComponentReleaseModel().update();
      getTechnicalComponentReleaseModel().configure(target);
    }
  }

  public Map<String, String> getComponentModelAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();

    assignment.put(InformationSystemInterfaceTypeMu.PROPERTY_NAME, "nameComponentModel");
    assignment.put(InformationSystemInterfaceTypeMu.PROPERTY_DESCRIPTION, "descriptionComponentModel");
    assignment.put("direction", "directionComponentModel");

    assignment.put(InformationSystemInterfaceTypeMu.ASSOCIATION_TRANSPORTS, "transportComponentModel");
    assignment.put(InformationSystemInterfaceTypeMu.ASSOCIATION_TECHNICALCOMPONENTRELEASES, "tcrComponentModel");
    return assignment;
  }

  /**
   * @return Returns a map of getter names, which are used to gain access to the component model by reflection.
   *         See MassUpdateFrontendService.
   */
  public Map<String, String> getAssociationGetMethodAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();

    assignment.put(InformationSystemInterfaceTypeMu.ASSOCIATION_TRANSPORTS, "getTransportModel");
    assignment.put(InformationSystemInterfaceTypeMu.ASSOCIATION_TECHNICALCOMPONENTRELEASES, "getTechnicalComponentReleaseModel");

    return assignment;
  }

  public void initializeFrom(InformationSystemInterface buildingBlock, List<String> properties, List<String> associations) {

    MassUpdateType type = InformationSystemInterfaceTypeMu.getInstance();

    //    init the properties that are part of the mass update
    for (String property : properties) {
      if (type.isPropertyIdEqual(property, InformationSystemInterfaceTypeMu.PROPERTY_NAME)) {
        namePropertyAvailable = true;
        super.getNameModel().initializeFrom(buildingBlock);
      }
      if (type.isPropertyIdEqual(property, InformationSystemInterfaceTypeMu.PROPERTY_DESCRIPTION)) {
        descriptionPropertyAvailable = true;
        super.getDescriptionModel().initializeFrom(buildingBlock);
      }
      if (type.isPropertyIdEqual(property, "direction")) {
        directionAvailable = true;
        getDirectionComponentModel().initializeFrom(buildingBlock);
      }
    }

    //  init the associations that will take part in the mass update
    for (String association : associations) {
      if (association.equals(InformationSystemInterfaceTypeMu.ASSOCIATION_TRANSPORTS)) {
        transportAssociationAvailable = true;
        super.getTransportModel().initializeFrom(buildingBlock);
        buildingBlock.setReferenceRelease(buildingBlock.getInformationSystemReleaseA());
      }
      if (association.equals(InformationSystemInterfaceTypeMu.ASSOCIATION_TECHNICALCOMPONENTRELEASES)) {
        tcrAssociationAvailable = true;
        super.getTechnicalComponentReleaseModel().initializeFrom(buildingBlock);
      }
    }
  }

  @Override
  public void update() {

    if (namePropertyAvailable) {
      getNameComponentModel().update();
    }

    if (descriptionPropertyAvailable) {
      getDescriptionComponentModel().update();
    }

    if (directionAvailable) {
      getDirectionComponentModel().update();
    }

    if (transportAssociationAvailable) {
      getTransportModel().update();
    }

    if (tcrAssociationAvailable) {
      getTechnicalComponentReleaseModel().update();
    }
  }
}
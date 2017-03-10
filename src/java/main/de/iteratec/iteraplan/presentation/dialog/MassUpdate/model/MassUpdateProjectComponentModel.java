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

import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProjectMassUpdateType;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.presentation.dialog.Project.ProjectComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;


public class MassUpdateProjectComponentModel extends ProjectComponentModel implements MassUpdateComponentModel<Project> {

  /** Serialization version. */
  private static final long serialVersionUID                             = -1544633648930972800L;
  private boolean           namePropertyAvailable                        = false;
  private boolean           descriptionPropertyAvailable                 = false;
  private boolean           datePropertyAvailable                        = false;
  private boolean           elementOfAssociationAvailable                = false;
  private boolean           informationSystemReleaseAssociationAvailable = false;

  public MassUpdateProjectComponentModel() {
    super(ComponentMode.EDIT);
    String[] header = new String[] { "global.name" };
    String[] fields = new String[] { "hierarchicalName" };
    super.getInformationSystemReleaseModel().updateHeaderAndFields(header, fields);
  }

  public Map<String, String> getComponentModelAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();
    assignment.put(ProjectMassUpdateType.PROPERTY_NAME, "nameComponentModel");
    assignment.put(ProjectMassUpdateType.PROPERTY_DESCRIPTION, "descriptionComponentModel");
    assignment.put(ProjectMassUpdateType.PROPERTY_TIMESPAN, "dateComponentModel");
    assignment.put(ProjectMassUpdateType.ASSOCIATION_PARENT, "elementOfComponentModel");
    assignment.put(ProjectMassUpdateType.ASSOCIATION_INFORMATIONSYSTEMRELEASES, "informationSystemReleaseComponentModel");

    return assignment;
  }

  /**
   * @return Returns a map of getter names, which are used to gain access to the component model by reflection.
   *         See MassUpdateFrontendService.
   */
  public Map<String, String> getAssociationGetMethodAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();

    assignment.put(ProjectMassUpdateType.ASSOCIATION_INFORMATIONSYSTEMRELEASES, "getInformationSystemReleaseModel");
    assignment.put(ProjectMassUpdateType.ASSOCIATION_PARENT, "getParentModel");

    return assignment;
  }

  public void initializeFrom(Project buildingBlock, List<String> properties, List<String> associations) {
    MassUpdateType type = ProjectMassUpdateType.getInstance();

    //    init the properties that are part of the mass update
    for (String property : properties) {
      if (type.isPropertyIdEqual(property, ProjectMassUpdateType.PROPERTY_NAME)) {
        namePropertyAvailable = true;
        getNameModel().initializeFrom(buildingBlock);
      }
      if (type.isPropertyIdEqual(property, ProjectMassUpdateType.PROPERTY_DESCRIPTION)) {
        descriptionPropertyAvailable = true;
        getDescriptionModel().initializeFrom(buildingBlock);
      }
      if (type.isPropertyIdEqual(property, ProjectMassUpdateType.PROPERTY_TIMESPAN)) {
        datePropertyAvailable = true;
        getRuntimePeriodModel().initializeFrom(buildingBlock);
      }
    }

    for (String association : associations) {
      if (association.equals(ProjectMassUpdateType.ASSOCIATION_PARENT)) {
        elementOfAssociationAvailable = true;
        getParentModel().initializeFrom(buildingBlock);
      }
      if (association.equals(ProjectMassUpdateType.ASSOCIATION_INFORMATIONSYSTEMRELEASES)) {
        informationSystemReleaseAssociationAvailable = true;
      }
    }
    getInformationSystemReleaseModel().initializeFrom(buildingBlock);
  }

  @Override
  public void update() throws IteraplanException {
    if (namePropertyAvailable) {
      getNameComponentModel().update();
    }
    if (descriptionPropertyAvailable) {
      getDescriptionComponentModel().update();
    }
    if (datePropertyAvailable) {
      getDateComponentModel().update();
    }
    if (elementOfAssociationAvailable) {
      getElementOfComponentModel().update();
    }
    if (informationSystemReleaseAssociationAvailable) {
      getInformationSystemReleaseComponentModel().update();
    }
  }

  public ComponentModel<Project> getNameComponentModel() {
    if (namePropertyAvailable) {
      return getNameModel();
    }
    return null;
  }

  public ComponentModel<Project> getDescriptionComponentModel() {
    if (descriptionPropertyAvailable) {
      return getDescriptionModel();
    }
    return null;
  }

  public ComponentModel<Project> getDateComponentModel() {
    if (datePropertyAvailable) {
      return getRuntimePeriodModel();
    }
    return null;
  }

  public ComponentModel<Project> getElementOfComponentModel() {
    if (elementOfAssociationAvailable) {
      return super.getParentModel();
    }
    return null;
  }

  public ComponentModel<Project> getInformationSystemReleaseComponentModel() {
    if (informationSystemReleaseAssociationAvailable) {
      return super.getInformationSystemReleaseModel();
    }
    return null;
  }

  @Override
  public void configure(Project target) throws IteraplanException {
    if (namePropertyAvailable) {
      getNameComponentModel().configure(target);
    }
    if (descriptionPropertyAvailable) {
      getDescriptionComponentModel().configure(target);
    }
    if (datePropertyAvailable) {
      getDateComponentModel().update();
      getDateComponentModel().configure(target);
    }
    if (informationSystemReleaseAssociationAvailable) {
      getInformationSystemReleaseComponentModel().configure(target);
    }
    if (elementOfAssociationAvailable) {
      getElementOfComponentModel().configure(target);
    }
  }
}

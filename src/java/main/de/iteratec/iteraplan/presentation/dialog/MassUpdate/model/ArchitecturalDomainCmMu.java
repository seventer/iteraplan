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

import de.iteratec.iteraplan.businesslogic.reports.query.type.ArchitecturalDomainTypeMu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.presentation.dialog.ArchitectualDomain.ArchitecturalDomainComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;


public class ArchitecturalDomainCmMu extends ArchitecturalDomainComponentModel implements MassUpdateComponentModel<ArchitecturalDomain> {

  /** Serialization version. */
  private static final long serialVersionUID             = -7901645276152040472L;
  private boolean           namePropertyAvailable        = false;
  private boolean           descriptionPropertyAvailable = false;
  private boolean           parentAssociationAvailable   = false;
  private boolean           childrenAssociationAvailable = false;
  private boolean           tcrAssociationAvailable      = false;

  public ArchitecturalDomainCmMu() {
    super(ComponentMode.EDIT);
    super.getChildrenModel().updateHeaderAndFields(new String[] { NAME_LABEL }, new String[] { "name" });
    super.getTechnicalComponentReleaseModel().updateHeaderAndFields(new String[] { NAME_LABEL }, new String[] { "releaseName" });
  }

  public Map<String, String> getComponentModelAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();
    assignment.put(ArchitecturalDomainTypeMu.PROPERTY_NAME, "nameComponentModel");
    assignment.put(ArchitecturalDomainTypeMu.PROPERTY_DESCRIPTION, "descriptionComponentModel");
    assignment.put(ArchitecturalDomainTypeMu.ASSOCIATION_PARENT, "parentComponentModel");
    assignment.put(ArchitecturalDomainTypeMu.ASSOCIATION_CHILDREN, "childrenComponentModel");
    assignment.put(ArchitecturalDomainTypeMu.ASSOCIATION_TECHNICALCOMPONENTRELEASES, "technicalComponentReleasesComponentModel");
    return assignment;
  }

  /**
   * @return Returns a map of getter names, which are used to gain access to the component model by reflection.
   *         See MassUpdateFrontendService.
   */
  public Map<String, String> getAssociationGetMethodAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();

    assignment.put(ArchitecturalDomainTypeMu.ASSOCIATION_PARENT, "getParentComponentModel");
    assignment.put(ArchitecturalDomainTypeMu.ASSOCIATION_TECHNICALCOMPONENTRELEASES, "getTechnicalComponentReleaseModel");

    return assignment;
  }

  public void initializeFrom(ArchitecturalDomain buildingBlock, List<String> properties, List<String> associations) {

    MassUpdateType type = ArchitecturalDomainTypeMu.getInstance();

    for (String property : properties) {
      if (type.isPropertyIdEqual(property, ArchitecturalDomainTypeMu.PROPERTY_NAME)) {
        namePropertyAvailable = true;
        getNameModel().initializeFrom(buildingBlock);
      }
      if (type.isPropertyIdEqual(property, ArchitecturalDomainTypeMu.PROPERTY_DESCRIPTION)) {
        descriptionPropertyAvailable = true;
        getDescriptionModel().initializeFrom(buildingBlock);
      }
    }

    for (String association : associations) {
      if (association.equals(ArchitecturalDomainTypeMu.ASSOCIATION_CHILDREN)) {
        childrenAssociationAvailable = true;
        super.getChildrenModel().initializeFrom(buildingBlock);
      }
      if (association.equals(ArchitecturalDomainTypeMu.ASSOCIATION_TECHNICALCOMPONENTRELEASES)) {
        tcrAssociationAvailable = true;
        super.getTechnicalComponentReleaseModel().initializeFrom(buildingBlock);
      }
      if (association.equals(ArchitecturalDomainTypeMu.ASSOCIATION_PARENT)) {
        parentAssociationAvailable = true;
        super.getParentModel().initializeFrom(buildingBlock);
      }
    }
  }

  @Override
  public void update() throws IteraplanException {
    if (namePropertyAvailable) {
      getNameComponentModel().update();
    }
    if (descriptionPropertyAvailable) {
      getDescriptionComponentModel().update();
    }
    if (parentAssociationAvailable) {
      getParentComponentModel().update();
    }
    if (childrenAssociationAvailable) {
      getChildrenComponentModel().update();
    }
    if (tcrAssociationAvailable) {
      getTechnicalComponentReleasesComponentModel().update();
    }
  }

  public ComponentModel<ArchitecturalDomain> getNameComponentModel() {
    if (namePropertyAvailable) {
      return getNameModel();
    }
    return null;
  }

  public ComponentModel<ArchitecturalDomain> getDescriptionComponentModel() {
    if (descriptionPropertyAvailable) {
      return getDescriptionModel();
    }
    return null;
  }

  public ComponentModel<ArchitecturalDomain> getParentComponentModel() {
    if (parentAssociationAvailable) {
      return super.getParentModel();
    }
    return null;
  }

  public ComponentModel<ArchitecturalDomain> getChildrenComponentModel() {
    if (childrenAssociationAvailable) {
      return super.getChildrenModel();
    }
    return null;
  }

  public ComponentModel<ArchitecturalDomain> getTechnicalComponentReleasesComponentModel() {
    if (tcrAssociationAvailable) {
      return super.getTechnicalComponentReleaseModel();
    }
    return null;
  }

  @Override
  public void configure(ArchitecturalDomain workingCopy) {
    if (namePropertyAvailable) {
      getNameComponentModel().configure(workingCopy);
    }
    if (descriptionPropertyAvailable) {
      getDescriptionComponentModel().configure(workingCopy);
    }
    if (parentAssociationAvailable) {
      getParentComponentModel().configure(workingCopy);
    }
    if (childrenAssociationAvailable) {
      getChildrenComponentModel().configure(workingCopy);
    }
    if (tcrAssociationAvailable) {
      getTechnicalComponentReleasesComponentModel().configure(workingCopy);
    }
  }
}

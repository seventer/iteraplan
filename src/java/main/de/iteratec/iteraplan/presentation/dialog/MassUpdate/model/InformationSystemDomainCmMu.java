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

import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemDomainTypeMu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.presentation.dialog.InformationSystemDomain.InformationSystemDomainComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;


/**
 * Component model for mass update operations on {@code InformationSystemDomain}s.
 */
public class InformationSystemDomainCmMu extends InformationSystemDomainComponentModel implements MassUpdateComponentModel<InformationSystemDomain> {

  /** Serialization version. */
  private static final long serialVersionUID             = 4318476548187848016L;
  private boolean           namePropertyAvailable        = false;
  private boolean           descriptionPropertyAvailable = false;
  private boolean           parentAssociationAvailable   = false;
  private boolean           childrenAssociationAvailable = false;
  private boolean           isrAssociationAvailable      = false;

  public InformationSystemDomainCmMu() {
    super(ComponentMode.EDIT);
    super.getInformationSystemReleaseModel().updateHeaderAndFields(new String[] { NAME_LABEL }, new String[] { "hierarchicalName" });
  }

  public Map<String, String> getComponentModelAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();
    assignment.put(InformationSystemDomainTypeMu.PROPERTY_NAME, "nameComponentModel");
    assignment.put(InformationSystemDomainTypeMu.PROPERTY_DESCRIPTION, "descriptionComponentModel");
    assignment.put(InformationSystemDomainTypeMu.ASSOCIATION_INFORMATIONSYSTEMRELEASES, "informationSystemReleaseComponentModel");
    assignment.put(InformationSystemDomainTypeMu.ASSOCIATION_PARENT, "parentComponentModel");
    assignment.put(InformationSystemDomainTypeMu.ASSOCIATION_CHILDREN, "childrenComponentModel");
    return assignment;
  }

  /**
   * @return Returns a map of getter names, which are used to gain access to the component model by reflection.
   *         See MassUpdateFrontendService.
   */
  public Map<String, String> getAssociationGetMethodAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();

    assignment.put(InformationSystemDomainTypeMu.ASSOCIATION_INFORMATIONSYSTEMRELEASES, "getInformationSystemReleaseModel");
    assignment.put(InformationSystemDomainTypeMu.ASSOCIATION_PARENT, "getParentModel");

    return assignment;
  }

  public void initializeFrom(InformationSystemDomain buildingBlock, List<String> properties, List<String> associations) {
    MassUpdateType type = InformationSystemDomainTypeMu.getInstance();

    for (String property : properties) {
      if (type.isPropertyIdEqual(property, InformationSystemDomainTypeMu.PROPERTY_NAME)) {
        namePropertyAvailable = true;
        super.getNameModel().initializeFrom(buildingBlock);
      }
      if (type.isPropertyIdEqual(property, InformationSystemDomainTypeMu.PROPERTY_DESCRIPTION)) {
        descriptionPropertyAvailable = true;
        super.getDescriptionModel().initializeFrom(buildingBlock);
      }
    }

    for (String association : associations) {
      if (association.equals(InformationSystemDomainTypeMu.ASSOCIATION_INFORMATIONSYSTEMRELEASES)) {
        isrAssociationAvailable = true;
        super.getInformationSystemReleaseModel().initializeFrom(buildingBlock);
      }
      if (association.equals(InformationSystemDomainTypeMu.ASSOCIATION_PARENT)) {
        parentAssociationAvailable = true;
        super.getParentModel().initializeFrom(buildingBlock);
      }
      if (association.equals(InformationSystemDomainTypeMu.ASSOCIATION_CHILDREN)) {
        childrenAssociationAvailable = true;
        super.getChildrenModel().initializeFrom(buildingBlock);
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
    if (isrAssociationAvailable) {
      getInformationSystemReleaseComponentModel().update();
    }
  }

  public ComponentModel<InformationSystemDomain> getNameComponentModel() {
    if (namePropertyAvailable) {
      return super.getNameModel();
    }
    return null;
  }

  public ComponentModel<InformationSystemDomain> getDescriptionComponentModel() {
    if (descriptionPropertyAvailable) {
      return super.getDescriptionModel();
    }
    return null;
  }

  public ComponentModel<InformationSystemDomain> getParentComponentModel() {
    if (parentAssociationAvailable) {
      return super.getParentModel();
    }
    return null;
  }

  public ComponentModel<InformationSystemDomain> getChildrenComponentModel() {
    if (childrenAssociationAvailable) {
      return super.getChildrenModel();
    }
    return null;
  }

  public ComponentModel<InformationSystemDomain> getInformationSystemReleaseComponentModel() {
    if (isrAssociationAvailable) {
      return super.getInformationSystemReleaseModel();
    }
    return null;
  }

  @Override
  public void configure(InformationSystemDomain target) throws IteraplanException {
    if (namePropertyAvailable) {
      getNameComponentModel().configure(target);
    }
    if (descriptionPropertyAvailable) {
      getDescriptionComponentModel().configure(target);
    }
    if (parentAssociationAvailable) {
      getParentComponentModel().configure(target);
    }
    if (isrAssociationAvailable) {
      getInformationSystemReleaseComponentModel().configure(target);
    }
  }
}

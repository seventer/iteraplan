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

import static de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessUnitType.ASSOCIATION_BUSINESSDOMAINS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessUnitMassUpdateType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.presentation.dialog.BusinessUnit.BusinessUnitComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;


/**
 * GUI model for the mass update of {@code BusinessUnit}s.
 */
public class MassUpdateBusinessUnitComponentModel extends BusinessUnitComponentModel implements MassUpdateComponentModel<BusinessUnit> {

  /** Serialization version. */
  private static final long serialVersionUID                   = 6949901319418810482L;
  private boolean           namePropertyAvailable              = false;
  private boolean           descriptionPropertyAvailable       = false;
  private boolean           parentAssociationAvailable         = false;
  private boolean           consistsOfAssociationAvailable     = false;
  private boolean           businessDomainAssociationAvailable = false;

  public MassUpdateBusinessUnitComponentModel() {
    super(ComponentMode.EDIT);

    String[] header = new String[] { NAME_LABEL };
    String[] fields = new String[] { "name" };

    super.getConsistsOfModel().updateHeaderAndFields(header, fields);
    super.getBusinessDomainModel().updateHeaderAndFields(header, fields);
  }

  public Map<String, String> getComponentModelAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();
    assignment.put(BusinessUnitMassUpdateType.PROPERTY_NAME, "nameComponentModel");
    assignment.put(BusinessUnitMassUpdateType.PROPERTY_DESCRIPTION, "descriptionComponentModel");
    assignment.put(BusinessUnitMassUpdateType.ASSOCIATION_PARENT, "parentComponentModel");
    assignment.put(BusinessUnitMassUpdateType.ASSOCIATION_CHILDREN, "consistsOfComponentModel");
    assignment.put(BusinessUnitMassUpdateType.ASSOCIATION_BUSINESSDOMAINS, "businessDomainComponentModel");
    return assignment;
  }

  /**
   * @return Returns a map of getter names, which are used to gain access to the component model by reflection.
   *         See MassUpdateFrontendService.
   */
  public Map<String, String> getAssociationGetMethodAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();

    assignment.put(BusinessUnitMassUpdateType.ASSOCIATION_BUSINESSDOMAINS, "getBusinessDomainModel");
    assignment.put(BusinessUnitMassUpdateType.ASSOCIATION_PARENT, "getElementOfModel");

    return assignment;
  }

  public void initializeFrom(BusinessUnit buildingBlock, List<String> properties, List<String> associations) {
    MassUpdateType type = BusinessUnitMassUpdateType.getInstance();

    for (String property : properties) {
      if (type.isPropertyIdEqual(property, BusinessUnitMassUpdateType.PROPERTY_NAME)) {
        namePropertyAvailable = true;
        super.getNameModel().initializeFrom(buildingBlock);
      }
      if (type.isPropertyIdEqual(property, BusinessUnitMassUpdateType.PROPERTY_DESCRIPTION)) {
        descriptionPropertyAvailable = true;
        super.getDescriptionModel().initializeFrom(buildingBlock);
      }
    }

    for (String association : associations) {
      if (association.equals(BusinessUnitMassUpdateType.ASSOCIATION_CHILDREN)) {
        consistsOfAssociationAvailable = true;
        super.getConsistsOfModel().initializeFrom(buildingBlock);
      }

      if (association.equals(BusinessUnitMassUpdateType.ASSOCIATION_PARENT)) {
        parentAssociationAvailable = true;
        super.getElementOfModel().initializeFrom(buildingBlock);
      }

      if (association.equals(ASSOCIATION_BUSINESSDOMAINS)) {
        businessDomainAssociationAvailable = true;
        super.getBusinessDomainModel().initializeFrom(buildingBlock);
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
    if (consistsOfAssociationAvailable) {
      getConsistsOfComponentModel().update();
    }
    if (businessDomainAssociationAvailable) {
      getBusinessDomainComponentModel().update();
    }
  }

  public ComponentModel<BusinessUnit> getNameComponentModel() {
    if (namePropertyAvailable) {
      return super.getNameModel();
    }
    return null;
  }

  public ComponentModel<BusinessUnit> getDescriptionComponentModel() {
    if (descriptionPropertyAvailable) {
      return super.getDescriptionModel();
    }
    return null;
  }

  public ComponentModel<BusinessUnit> getParentComponentModel() {
    if (parentAssociationAvailable) {
      return super.getElementOfModel();
    }
    return null;
  }

  public ComponentModel<BusinessUnit> getConsistsOfComponentModel() {
    if (consistsOfAssociationAvailable) {
      return super.getConsistsOfModel();
    }
    return null;
  }

  public ComponentModel<BusinessUnit> getBusinessDomainComponentModel() {
    if (businessDomainAssociationAvailable) {
      return super.getBusinessDomainModel();
    }
    return null;
  }

  @Override
  public void configure(BusinessUnit target) throws IteraplanException {
    if (namePropertyAvailable) {
      getNameComponentModel().configure(target);
    }
    if (descriptionPropertyAvailable) {
      getDescriptionComponentModel().configure(target);
    }
    if (parentAssociationAvailable) {
      getParentComponentModel().configure(target);
    }
    if (businessDomainAssociationAvailable) {
      getBusinessDomainComponentModel().configure(target);
    }
  }
}

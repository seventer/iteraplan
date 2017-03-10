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

import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessObjectType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessObjectTypeMu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.presentation.dialog.BusinessObject.BusinessObjectComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.NullSafeModel;


/**
 * Component model for mass update operations on {@code BusinessObject}s.
 */
public class BusinessObjectCmMu extends BusinessObjectComponentModel implements MassUpdateComponentModel<BusinessObject> {

  /** Serialization version. */
  private static final long              serialVersionUID                             = 5180311731826630208L;
  private boolean                        namePropertyAvailable                        = false;
  private boolean                        descriptionPropertyAvailable                 = false;
  private boolean                        elementOfAssociationAvailable                = false;
  private boolean                        generalisationAssociationAvailable           = false;
  private boolean                        informationSystemReleaseAssociationAvailable = false;
  private boolean                        businessDomainAssociationAvailable           = false;
  private boolean                        businessFunctionAssociationAvailable         = false;
  private ComponentModel<BusinessObject> nullSafeModel                                = new NullSafeModel<BusinessObject>();

  public BusinessObjectCmMu() {
    super(ComponentMode.EDIT);
    String[] header = new String[] { "global.name" };
    String[] fields = new String[] { "hierarchicalName" };
    super.getInformationSystemReleaseModel().updateHeaderAndFields(header, fields);
    super.getBusinessDomainModel().updateHeaderAndFields(header, fields);
    super.getBusinessFunctionModel().updateHeaderAndFields(header, fields);
  }

  public Map<String, String> getComponentModelAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();
    assignment.put(BusinessObjectTypeMu.PROPERTY_NAME, "nameComponentModel");
    assignment.put(BusinessObjectTypeMu.PROPERTY_DESCRIPTION, "descriptionComponentModel");
    assignment.put(BusinessObjectTypeMu.ASSOCIATION_PARENT, "elementOfComponentModel");
    assignment.put(BusinessObjectType.ASSOCIATION_INFORMATIONSYSTEMRELEASE_ASSOCIATIONS, "informationSystemReleaseComponentModel");
    assignment.put(BusinessObjectType.ASSOCIATION_BUSINESSDOMAIN, "businessDomainComponentModel");
    assignment.put(BusinessObjectType.ASSOCIATION_BUSINESSFUNCTION, "businessFunctionComponentModel");
    assignment.put(BusinessObjectType.ASSOCIATION_GENERALISATION, "generalisationComponentModel");

    return assignment;
  }

  /**
   * @return Returns a map of getter names, which are used to gain access to the componentModel by reflection.
   *         See MassUpdateFrontendService.
   */
  public Map<String, String> getAssociationGetMethodAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();

    assignment.put(BusinessObjectType.ASSOCIATION_INFORMATIONSYSTEMRELEASE_ASSOCIATIONS, "getInformationSystemReleaseModel");
    assignment.put(BusinessObjectType.ASSOCIATION_BUSINESSDOMAIN, "getBusinessDomainModel");
    assignment.put(BusinessObjectType.ASSOCIATION_BUSINESSFUNCTION, "getBusinessFunctionModel");
    assignment.put(BusinessObjectType.ASSOCIATION_PARENT, "getElementOfModel");
    assignment.put(BusinessObjectType.ASSOCIATION_GENERALISATION, "getGeneralisationModel");

    return assignment;
  }

  public void initializeFrom(BusinessObject buildingBlock, List<String> properties, List<String> associations) {
    MassUpdateType type = BusinessObjectTypeMu.getInstance();

    for (String property : properties) {
      if (type.isPropertyIdEqual(property, BusinessObjectTypeMu.PROPERTY_NAME)) {
        namePropertyAvailable = true;
        super.getNameModel().initializeFrom(buildingBlock);
      }
      if (type.isPropertyIdEqual(property, BusinessObjectTypeMu.PROPERTY_DESCRIPTION)) {
        descriptionPropertyAvailable = true;
        super.getDescriptionModel().initializeFrom(buildingBlock);
      }
    }

    for (String association : associations) {
      if (association.equals(BusinessObjectTypeMu.ASSOCIATION_PARENT)) {
        elementOfAssociationAvailable = true;
        super.getParentModel().initializeFrom(buildingBlock);
      }
      if (association.equals(BusinessObjectTypeMu.ASSOCIATION_GENERALISATION)) {
        generalisationAssociationAvailable = true;
        super.getSpecialisationModel().initializeFrom(buildingBlock);
      }
      if (association.equals(BusinessObjectType.ASSOCIATION_INFORMATIONSYSTEMRELEASE_ASSOCIATIONS)) {
        informationSystemReleaseAssociationAvailable = true;
      }
      if (association.equals(BusinessObjectType.ASSOCIATION_BUSINESSDOMAIN)) {
        businessDomainAssociationAvailable = true;
      }
      if (association.equals(BusinessObjectType.ASSOCIATION_BUSINESSFUNCTION)) {
        businessFunctionAssociationAvailable = true;
      }
    }
    super.getBusinessDomainModel().initializeFrom(buildingBlock);
    super.getBusinessFunctionModel().initializeFrom(buildingBlock);
    super.getInformationSystemReleaseModel().initializeFrom(buildingBlock);
  }

  @Override
  public void update() throws IteraplanException {
    if (namePropertyAvailable) {
      getNameComponentModel().update();
    }
    if (descriptionPropertyAvailable) {
      getDescriptionComponentModel().update();
    }
    if (elementOfAssociationAvailable) {
      getElementOfComponentModel().update();
    }
    if (generalisationAssociationAvailable) {
      getGeneralisationComponentModel().update();
    }
    getInformationSystemReleaseModel().update();
    getBusinessDomainModel().update();
    getBusinessFunctionModel().update();
  }

  public ComponentModel<BusinessObject> getNameComponentModel() {
    if (namePropertyAvailable) {
      return super.getNameModel();
    }
    return null;
  }

  public ComponentModel<BusinessObject> getDescriptionComponentModel() {
    if (descriptionPropertyAvailable) {
      return super.getDescriptionModel();
    }
    return null;
  }

  public ComponentModel<BusinessObject> getElementOfComponentModel() {
    if (elementOfAssociationAvailable) {
      return super.getParentModel();
    }
    return null;
  }

  public ComponentModel<BusinessObject> getGeneralisationComponentModel() {
    if (generalisationAssociationAvailable) {
      return super.getSpecialisationModel();
    }
    return null;
  }

  public ComponentModel<BusinessObject> getInformationSystemReleaseComponentModel() {
    return informationSystemReleaseAssociationAvailable ? super.getInformationSystemReleaseModel() : nullSafeModel;
  }

  public ComponentModel<BusinessObject> getBusinessDomainComponentModel() {
    return businessDomainAssociationAvailable ? super.getBusinessDomainModel() : nullSafeModel;
  }

  public ComponentModel<BusinessObject> getBusinessFunctionComponentModel() {
    return businessFunctionAssociationAvailable ? super.getBusinessFunctionModel() : nullSafeModel;
  }

  @Override
  public void configure(BusinessObject target) throws IteraplanException {
    if (namePropertyAvailable) {
      getNameComponentModel().configure(target);
    }
    if (descriptionPropertyAvailable) {
      getDescriptionComponentModel().configure(target);
    }
    if (informationSystemReleaseAssociationAvailable) {
      getInformationSystemReleaseModel().configure(target);
    }
    if (businessDomainAssociationAvailable) {
      getBusinessDomainModel().configure(target);
    }
    if (businessFunctionAssociationAvailable) {
      getBusinessFunctionModel().configure(target);
    }
    if (elementOfAssociationAvailable) {
      getElementOfComponentModel().configure(target);
    }
    if (generalisationAssociationAvailable) {
      getGeneralisationComponentModel().configure(target);
    }
  }
}

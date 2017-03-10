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

import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessDomainMassUpdateType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessDomainType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.presentation.dialog.BusinessDomain.BusinessDomainComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.NullSafeModel;


/**
 * Component model for mass update operations on {@link BusinessDomain}s.
 */
public class MassUpdateBusinessDomainComponentModel extends BusinessDomainComponentModel implements MassUpdateComponentModel<BusinessDomain> {

  /** Serialization version. */
  private static final long              serialVersionUID                     = -4630717885520208772L;

  private ComponentModel<BusinessDomain> nullSafeModel                        = new NullSafeModel<BusinessDomain>();

  private boolean                        namePropertyAvailable                = false;
  private boolean                        descriptionPropertyAvailable         = false;

  private boolean                        businessFunctionAssociationAvailable = false;
  private boolean                        businessProcessAssociationAvailable  = false;
  private boolean                        businessObjectAssociationAvailable   = false;
  private boolean                        businessUnitAssociationAvailable     = false;
  private boolean                        productAssociationAvailable          = false;
  private boolean                        parentAssociationAvailable           = false;
  private boolean                        childrenAssociationAvailable         = false;

  public MassUpdateBusinessDomainComponentModel() {
    super(ComponentMode.EDIT);

    String[] header = new String[] { NAME_LABEL };
    String[] fields = new String[] { NAME };

    super.getChildrenModel().updateHeaderAndFields(header, fields);
    super.getBusinessFunctionModel().updateHeaderAndFields(header, fields);
    super.getBusinessProcessModel().updateHeaderAndFields(header, fields);
    super.getBusinessObjectModel().updateHeaderAndFields(header, fields);
    super.getBusinessUnitModel().updateHeaderAndFields(header, fields);
    super.getProductModel().updateHeaderAndFields(header, fields);
  }

  public ComponentModel<BusinessDomain> getNameComponentModel() {
    return namePropertyAvailable ? super.getNameModel() : nullSafeModel;
  }

  public ComponentModel<BusinessDomain> getDescriptionComponentModel() {
    return descriptionPropertyAvailable ? super.getDescriptionModel() : nullSafeModel;
  }

  public ComponentModel<BusinessDomain> getBusinessFunctionComponentModel() {
    return businessFunctionAssociationAvailable ? super.getBusinessFunctionModel() : nullSafeModel;
  }

  public ComponentModel<BusinessDomain> getBusinessProcessComponentModel() {
    return businessProcessAssociationAvailable ? super.getBusinessProcessModel() : nullSafeModel;
  }

  public ComponentModel<BusinessDomain> getBusinessObjectComponentModel() {
    return businessObjectAssociationAvailable ? super.getBusinessObjectModel() : nullSafeModel;
  }

  public ComponentModel<BusinessDomain> getProductComponentModel() {
    return productAssociationAvailable ? super.getProductModel() : nullSafeModel;
  }

  public ComponentModel<BusinessDomain> getBusinessUnitComponentModel() {
    return businessUnitAssociationAvailable ? super.getBusinessUnitModel() : nullSafeModel;
  }

  public ComponentModel<BusinessDomain> getParentComponentModel() {
    return parentAssociationAvailable ? super.getParentModel() : nullSafeModel;
  }

  public ComponentModel<BusinessDomain> getChildrenComponentModel() {
    return childrenAssociationAvailable ? super.getChildrenModel() : nullSafeModel;
  }

  public Map<String, String> getComponentModelAssignment() {

    Map<String, String> assignment = new HashMap<String, String>();
    assignment.put(BusinessDomainType.PROPERTY_NAME, "nameComponentModel");
    assignment.put(BusinessDomainType.PROPERTY_DESCRIPTION, "descriptionComponentModel");
    assignment.put(BusinessDomainType.ASSOCIATION_PARENT, "parentComponentModel");
    assignment.put(BusinessDomainType.ASSOCIATION_CHILDREN, "childrenComponentModel");
    assignment.put(BusinessDomainType.ASSOCIATION_BUSINESSFUNCTIONS, "businessFunctionComponentModel");
    assignment.put(BusinessDomainType.ASSOCIATION_BUSINESSPROCESSES, "businessProcessComponentModel");
    assignment.put(BusinessDomainType.ASSOCIATION_BUSINESSOBJECTS, "businessObjectComponentModel");
    assignment.put(BusinessDomainType.ASSOCIATION_BUSINESSUNITS, "businessUnitComponentModel");
    assignment.put(BusinessDomainType.ASSOCIATION_PRODUCTS, "productComponentModel");

    return assignment;
  }

  /**
   * @return Returns a map of getter names, which are used to gain access to the component model by reflection.
   *         See MassUpdateFrontendService.
   */
  public Map<String, String> getAssociationGetMethodAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();

    assignment.put(BusinessDomainType.ASSOCIATION_BUSINESSFUNCTIONS, "getBusinessFunctionModel");
    assignment.put(BusinessDomainType.ASSOCIATION_BUSINESSPROCESSES, "getBusinessProcessModel");
    assignment.put(BusinessDomainType.ASSOCIATION_BUSINESSOBJECTS, "getBusinessObjectModel");
    assignment.put(BusinessDomainType.ASSOCIATION_BUSINESSUNITS, "getBusinessUnitModel");
    assignment.put(BusinessDomainType.ASSOCIATION_PRODUCTS, "getProductModel");
    assignment.put(BusinessDomainType.ASSOCIATION_PARENT, "getParentModel");

    return assignment;
  }

  public void initializeFrom(BusinessDomain source, List<String> properties, List<String> associations) {

    MassUpdateType type = BusinessDomainMassUpdateType.getInstance();

    for (String property : properties) {
      if (type.isPropertyIdEqual(property, BusinessDomainType.PROPERTY_NAME)) {
        namePropertyAvailable = true;
        getNameModel().initializeFrom(source);
      }

      if (type.isPropertyIdEqual(property, BusinessDomainType.PROPERTY_DESCRIPTION)) {
        descriptionPropertyAvailable = true;
        getDescriptionModel().initializeFrom(source);
      }
    }

    for (String association : associations) {
      if (association.equals(BusinessDomainType.ASSOCIATION_BUSINESSFUNCTIONS)) {
        businessFunctionAssociationAvailable = true;
        super.getBusinessFunctionModel().initializeFrom(source);
      }

      if (association.equals(BusinessDomainType.ASSOCIATION_BUSINESSPROCESSES)) {
        businessProcessAssociationAvailable = true;
        super.getBusinessProcessModel().initializeFrom(source);
      }

      if (association.equals(BusinessDomainType.ASSOCIATION_BUSINESSOBJECTS)) {
        businessObjectAssociationAvailable = true;
        super.getBusinessObjectModel().initializeFrom(source);
      }

      if (association.equals(BusinessDomainType.ASSOCIATION_BUSINESSUNITS)) {
        businessUnitAssociationAvailable = true;
        super.getBusinessUnitModel().initializeFrom(source);
      }

      if (association.equals(BusinessDomainType.ASSOCIATION_PRODUCTS)) {
        productAssociationAvailable = true;
        super.getProductModel().initializeFrom(source);
      }

      if (association.equals(BusinessDomainType.ASSOCIATION_PARENT)) {
        parentAssociationAvailable = true;
        super.getParentModel().initializeFrom(source);
      }

      if (association.equals(BusinessDomainType.ASSOCIATION_CHILDREN)) {
        childrenAssociationAvailable = true;
        super.getChildrenModel().initializeFrom(source);
      }
    }
  }

  @Override
  public void update() {

    getNameComponentModel().update();
    getDescriptionComponentModel().update();
    getBusinessFunctionComponentModel().update();
    getBusinessProcessComponentModel().update();
    getBusinessObjectComponentModel().update();
    getBusinessUnitComponentModel().update();
    getProductComponentModel().update();
    getParentComponentModel().update();
    getChildrenComponentModel().update();
  }

  @Override
  public void configure(BusinessDomain target) {

    getNameComponentModel().configure(target);
    getDescriptionComponentModel().configure(target);
    getBusinessFunctionComponentModel().configure(target);
    getBusinessProcessComponentModel().configure(target);
    getBusinessObjectComponentModel().configure(target);
    getBusinessUnitComponentModel().configure(target);
    getProductComponentModel().configure(target);
    getParentComponentModel().configure(target);
    getChildrenComponentModel().configure(target);
  }
}
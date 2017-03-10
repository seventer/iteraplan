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

import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessFunctionMassUpdateType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessFunctionType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.presentation.dialog.BusinessFunction.BusinessFunctionComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.NullSafeModel;


/**
 * Component model for mass update operations on {@link BusinessFunction}s.
 */
public class MassUpdateBusinessFunctionComponentModel extends BusinessFunctionComponentModel implements MassUpdateComponentModel<BusinessFunction> {

  /** Serialization version. */
  private static final long                serialVersionUID                   = 7060917732052389426L;

  private ComponentModel<BusinessFunction> nullSafeModel                      = new NullSafeModel<BusinessFunction>();

  private boolean                          namePropertyAvailable              = false;
  private boolean                          descriptionPropertyAvailable       = false;

  private boolean                          informationSystemAssociationAvailable = false;
  private boolean                          businessObjectAssociationAvailable = false;
  private boolean                          businessDomainAssociationAvailable = false;
  private boolean                          parentAssociationAvailable         = false;
  private boolean                          childrenAssociationAvailable       = false;

  public MassUpdateBusinessFunctionComponentModel() {
    super(ComponentMode.EDIT);

    String[] header = new String[] { NAME_LABEL };
    String[] fields = new String[] { NAME_FIELD };

    super.getBusinessObjectModel().updateHeaderAndFields(header, fields);
    super.getBusinessDomainModel().updateHeaderAndFields(header, fields);
    super.getInformationSystemReleaseModel().updateHeaderAndFields(header, fields);
    super.getChildrenModel().updateHeaderAndFields(header, fields);
  }

  public ComponentModel<BusinessFunction> getNameComponentModel() {
    return namePropertyAvailable ? super.getNameModel() : nullSafeModel;
  }

  public ComponentModel<BusinessFunction> getDescriptionComponentModel() {
    return descriptionPropertyAvailable ? super.getDescriptionModel() : nullSafeModel;
  }

  public ComponentModel<BusinessFunction> getBusinessObjectComponentModel() {
    return businessObjectAssociationAvailable ? super.getBusinessObjectModel() : nullSafeModel;
  }

  public ComponentModel<BusinessFunction> getBusinessDomainComponentModel() {
    return businessDomainAssociationAvailable ? super.getBusinessDomainModel() : nullSafeModel;
  }

  public ComponentModel<BusinessFunction> getInformationSystemComponentModel() {
    return informationSystemAssociationAvailable ? super.getInformationSystemReleaseModel() : nullSafeModel;
  }

  public ComponentModel<BusinessFunction> getParentComponentModel() {
    return parentAssociationAvailable ? getParentModel() : nullSafeModel;
  }

  public ComponentModel<BusinessFunction> getChildrenComponentModel() {
    return childrenAssociationAvailable ? getChildrenModel() : nullSafeModel;
  }

  public Map<String, String> getComponentModelAssignment() {

    Map<String, String> assignment = new HashMap<String, String>();
    assignment.put(BusinessFunctionType.PROPERTY_NAME, "nameComponentModel");
    assignment.put(BusinessFunctionType.PROPERTY_DESCRIPTION, "descriptionComponentModel");
    assignment.put(BusinessFunctionType.ASSOCIATION_BUSINESSOBJECTS, "businessObjectComponentModel");
    assignment.put(BusinessFunctionType.ASSOCIATION_BUSINESSDOMAINS, "businessDomainComponentModel");
    assignment.put(BusinessFunctionType.ASSOCIATION_INFORMATIONSYSTEMS, "informationSystemComponentModel");
    assignment.put(BusinessFunctionType.ASSOCIATION_PARENT, "parentComponentModel");
    assignment.put(BusinessFunctionType.ASSOCIATION_CHILDREN, "childrenComponentModel");

    return assignment;
  }

  /**
   * @return Returns a map of getter names, which are used to gain access to the component model by reflection.
   *         See MassUpdateFrontendService.
   */
  public Map<String, String> getAssociationGetMethodAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();

    assignment.put(BusinessFunctionType.ASSOCIATION_BUSINESSOBJECTS, "getBusinessObjectModel");
    assignment.put(BusinessFunctionType.ASSOCIATION_BUSINESSDOMAINS, "getBusinessDomainModel");
    assignment.put(BusinessFunctionType.ASSOCIATION_INFORMATIONSYSTEMS, "getInformationSystemReleaseModel");
    assignment.put(BusinessFunctionType.ASSOCIATION_PARENT, "getParentModel");

    return assignment;
  }

  public void initializeFrom(BusinessFunction source, List<String> properties, List<String> associations) {

    MassUpdateType type = BusinessFunctionMassUpdateType.getInstance();

    for (String property : properties) {
      if (type.isPropertyIdEqual(property, BusinessFunctionType.PROPERTY_NAME)) {
        namePropertyAvailable = true;
        super.getNameModel().initializeFrom(source);
      }

      if (type.isPropertyIdEqual(property, BusinessFunctionType.PROPERTY_DESCRIPTION)) {
        descriptionPropertyAvailable = true;
        super.getDescriptionModel().initializeFrom(source);
      }
    }

    for (String association : associations) {
      if (association.equals(BusinessFunctionType.ASSOCIATION_BUSINESSOBJECTS)) {
        businessObjectAssociationAvailable = true;
        super.getBusinessObjectModel().initializeFrom(source);
      }

      if (association.equals(BusinessFunctionType.ASSOCIATION_BUSINESSDOMAINS)) {
        businessDomainAssociationAvailable = true;
        super.getBusinessDomainModel().initializeFrom(source);
      }

      if (association.equals(BusinessFunctionType.ASSOCIATION_INFORMATIONSYSTEMS)) {
        informationSystemAssociationAvailable = true;
        super.getInformationSystemReleaseModel().initializeFrom(source);
      }

      if (association.equals(BusinessFunctionType.ASSOCIATION_PARENT)) {
        parentAssociationAvailable = true;
        super.getParentModel().initializeFrom(source);
      }

      if (association.equals(BusinessFunctionType.ASSOCIATION_CHILDREN)) {
        childrenAssociationAvailable = true;
        super.getChildrenModel().initializeFrom(source);
      }
    }
  }

  @Override
  public void update() {

    getNameComponentModel().update();
    getDescriptionComponentModel().update();
    getBusinessObjectComponentModel().update();
    getBusinessDomainComponentModel().update();
    getInformationSystemComponentModel().update();
    getParentComponentModel().update();
    getChildrenComponentModel().update();
  }

  @Override
  public void configure(BusinessFunction target) {

    getNameComponentModel().configure(target);
    getDescriptionComponentModel().configure(target);
    getBusinessObjectComponentModel().configure(target);
    getBusinessDomainComponentModel().configure(target);
    getInformationSystemComponentModel().configure(target);
    getParentComponentModel().configure(target);
    getChildrenComponentModel().configure(target);
  }
}
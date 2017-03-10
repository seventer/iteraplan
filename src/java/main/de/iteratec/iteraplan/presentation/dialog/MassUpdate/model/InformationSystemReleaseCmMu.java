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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessMappingType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeMu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.presentation.dialog.InformationSystem.InformationSystemReleaseComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.NullSafeModel;


/**
 * Component model for mass update operations on {@link InformationSystemRelease}s and
 * {@link BusinessMapping}s.
 */
public class InformationSystemReleaseCmMu extends InformationSystemReleaseComponentModel implements
    MassUpdateComponentModel<InformationSystemRelease> {

  /** Serialization version. */
  private static final long                              serialVersionUID                 = -6445684052561988157L;

  private final ComponentModel<InformationSystemRelease> nullSafeModel                    = new NullSafeModel<InformationSystemRelease>();

  private boolean                                        namePropertyAvailable            = false;
  private boolean                                        descriptionPropertyAvailable     = false;
  private boolean                                        timespanPropertyAvailable        = false;
  private boolean                                        statusPropertyAvailable          = false;

  private boolean                                        businessFunctionAvailable        = false;
  private boolean                                        businessObjectAvailable          = false;
  private boolean                                        businessMappingAvailable         = false;
  private boolean                                        informationSystemDomainAvailable = false;
  private boolean                                        infrastructureElementAvailable   = false;
  private boolean                                        projectAvailable                 = false;
  private boolean                                        technicalComponentAvailable      = false;
  private boolean                                        predecessorAvailable             = false;
  private boolean                                        successorAvailable               = false;
  private boolean                                        parentAvailable                  = false;
  private boolean                                        baseComponentsAvailable          = false;
  private boolean                                        parentComponentsAvailable        = false;

  private final List<String>                             pathToSelectedBizMappings        = new ArrayList<String>();

  private final List<BusinessMappingCmMu>                businessMappingComponentModels   = new ArrayList<BusinessMappingCmMu>();

  public InformationSystemReleaseCmMu() {

    super(ComponentMode.EDIT);

    String[] header = new String[] { NAME_LABEL };
    String[] fields = new String[] { "hierarchicalName" };

    super.getBusinessFunctionModel().updateHeaderAndFields(header, fields);
    super.getBusinessObjectModel().updateHeaderAndFields(header, fields);
    super.getInformationSystemDomainModel().updateHeaderAndFields(header, fields);
    super.getInfrastructureElementModel().updateHeaderAndFields(header, fields);
    super.getBaseComponentModel().updateHeaderAndFields(header, fields);
    super.getParentComponentModel().updateHeaderAndFields(header, fields);
    super.getPredecessorModel().updateHeaderAndFields(header, fields);
    super.getSuccessorModel().updateHeaderAndFields(header, fields);
    super.getProjectModel().updateHeaderAndFields(header, fields);
    super.getTechnicalComponentModel().updateHeaderAndFields(header, new String[] { "releaseName" });
  }

  public ComponentModel<InformationSystemRelease> getRuntimePeriodComponentModel() {
    return timespanPropertyAvailable ? super.getRuntimePeriodModel() : nullSafeModel;
  }

  public ComponentModel<InformationSystemRelease> getDescriptionComponentModel() {
    return descriptionPropertyAvailable ? super.getDescriptionModel() : nullSafeModel;
  }

  public ComponentModel<InformationSystemRelease> getNameComponentModel() {
    return namePropertyAvailable ? super.getReleaseNameModel() : nullSafeModel;
  }

  public ComponentModel<InformationSystemRelease> getStatusComponentModel() {
    return statusPropertyAvailable ? super.getStatusModel() : nullSafeModel;
  }

  public ComponentModel<InformationSystemRelease> getBusinessFunctionComponentModel() {
    return businessFunctionAvailable ? super.getBusinessFunctionModel() : nullSafeModel;
  }

  public ComponentModel<InformationSystemRelease> getBusinessObjectComponentModel() {
    return businessObjectAvailable ? super.getBusinessObjectModel() : nullSafeModel;
  }

  public List<BusinessMappingCmMu> getBusinessMappingComponentModels() {
    return this.businessMappingComponentModels;
  }

  public ComponentModel<InformationSystemRelease> getInformationSystemDomainComponentModel() {
    return informationSystemDomainAvailable ? super.getInformationSystemDomainModel() : nullSafeModel;
  }

  public ComponentModel<InformationSystemRelease> getInfrastructureElementComponentModel() {
    return infrastructureElementAvailable ? super.getInfrastructureElementModel() : nullSafeModel;
  }

  public ComponentModel<InformationSystemRelease> getProjectComponentModel() {
    return projectAvailable ? super.getProjectModel() : nullSafeModel;
  }

  public ComponentModel<InformationSystemRelease> getTechnicalComponentComponentModel() {
    return technicalComponentAvailable ? super.getTechnicalComponentModel() : nullSafeModel;
  }

  public ComponentModel<InformationSystemRelease> getParentsModel() {
    return parentAvailable ? super.getParentModel() : nullSafeModel;
  }

  public ComponentModel<InformationSystemRelease> getPredecessorsModel() {
    return predecessorAvailable ? super.getPredecessorModel() : nullSafeModel;
  }

  public ComponentModel<InformationSystemRelease> getSuccessorsModel() {
    return successorAvailable ? super.getSuccessorModel() : nullSafeModel;
  }

  public ComponentModel<InformationSystemRelease> getBaseComponentsModel() {
    return baseComponentsAvailable ? super.getBaseComponentModel() : nullSafeModel;
  }

  public ComponentModel<InformationSystemRelease> getParentComponentsModel() {
    return parentComponentsAvailable ? super.getParentComponentModel() : nullSafeModel;
  }

  public Map<String, String> getComponentModelAssignment() {

    Map<String, String> assignment = new HashMap<String, String>();
    assignment.put(InformationSystemReleaseType.PROPERTY_NAME, "nameComponentModel");
    assignment.put(InformationSystemReleaseType.PROPERTY_DESCRIPTION, "descriptionComponentModel");
    assignment.put(InformationSystemReleaseType.PROPERTY_TYPEOFSTATUS, "statusComponentModel");
    assignment.put(InformationSystemReleaseTypeMu.PROPERTY_TIMESPAN, "runtimePeriodComponentModel");

    assignment.put(InformationSystemReleaseType.ASSOCIATION_BUSINESSFUNCTIONS, "businessFunctionComponentModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_BUSINESSOBJECT_ASSOCIATIONS, "businessObjectComponentModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_INFORMATIONSYSTEMDOMAINS, "informationSystemDomainComponentModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_INFRASTRUCTUREELEMENTS, "infrastructureElementComponentModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_PREDECESSORS, "predecessorModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_SUCCESSORS, "successorModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_PROJECTS, "projectComponentModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_TECHNICALCOMPONENTRELEASES, "technicalComponentComponentModel");

    // This component model is also used for mass updating business mappings.
    assignment.put(BusinessMappingType.ASSOCIATION_BUSINESSPROCESS, "businessMappingComponentModels");
    assignment.put(BusinessMappingType.ASSOCIATION_BUSINESSUNIT, "businessMappingComponentModels");
    assignment.put(BusinessMappingType.ASSOCIATION_PRODUCT, "businessMappingComponentModels");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_BASECOMPONENTS, "baseComponentModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_PARENTCOMPONENTS, "parentComponentModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_PARENT, "parentModel");

    return assignment;
  }

  /**
   * @return Returns a map of getter names, which are used to gain access to the component model by reflection.
   *         See MassUpdateFrontendService.
   */
  public Map<String, String> getAssociationGetMethodAssignment() {

    Map<String, String> assignment = new HashMap<String, String>();

    assignment.put(InformationSystemReleaseType.ASSOCIATION_BUSINESSFUNCTIONS, "getBusinessFunctionModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_BUSINESSOBJECT_ASSOCIATIONS, "getBusinessObjectModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_INFORMATIONSYSTEMDOMAINS, "getInformationSystemDomainModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_INFRASTRUCTUREELEMENTS, "getInfrastructureElementModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_PROJECTS, "getProjectModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_TECHNICALCOMPONENTRELEASES, "getTechnicalComponentModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_BASECOMPONENTS, "getBaseComponentModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_PARENTCOMPONENTS, "getParentComponentModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_PREDECESSORS, "getPredecessorModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_SUCCESSORS, "getSuccessorModel");
    assignment.put(InformationSystemReleaseType.ASSOCIATION_PARENT, "getParentModel");

    return assignment;
  }

  /**
   * @return Returns the paths to the business mapping associations that were not selected as part
   *         of the update procedure.
   */
  public List<String> getComponentModelPathToNotSelectedElements() {

    List<String> ret = new ArrayList<String>();
    addUnselectedPathString(pathToSelectedBizMappings, BusinessMappingCmMu.PATH_TO_BUSINESSPROCESS, ret);
    addUnselectedPathString(pathToSelectedBizMappings, BusinessMappingCmMu.PATH_TO_BUSINESSUNIT, ret);
    addUnselectedPathString(pathToSelectedBizMappings, BusinessMappingCmMu.PATH_TO_PRODUCT, ret);

    return ret;
  }

  /**
   * @return Returns all business mapping associations that were selected to be part of the update
   *         procedure.
   */
  public List<String> getComponentModelPathToSelectedElements() {
    Collections.sort(pathToSelectedBizMappings);
    return pathToSelectedBizMappings;
  }

  public void initializeFrom(InformationSystemRelease source, List<String> properties, List<String> associations) {

    initPropertiesFrom(properties, InformationSystemReleaseTypeMu.getInstance(), source);

    initAssociationsFrom(associations, source);

  }

  /**
   * @param associations
   * @param source
   */
  private void initAssociationsFrom(List<String> associations, InformationSystemRelease source) {
    for (String association : associations) {
      if (association.equals(InformationSystemReleaseType.ASSOCIATION_BUSINESSFUNCTIONS)) {
        businessFunctionAvailable = true;
        super.getBusinessFunctionModel().initializeFrom(source);
      }

      if (association.equals(InformationSystemReleaseType.ASSOCIATION_BUSINESSOBJECT_ASSOCIATIONS)) {
        businessObjectAvailable = true;
        super.getBusinessObjectModel().initializeFrom(source);
      }

      if (association.equals(InformationSystemReleaseType.ASSOCIATION_INFORMATIONSYSTEMDOMAINS)) {
        informationSystemDomainAvailable = true;
        super.getInformationSystemDomainModel().initializeFrom(source);
      }

      if (association.equals(InformationSystemReleaseType.ASSOCIATION_INFRASTRUCTUREELEMENTS)) {
        infrastructureElementAvailable = true;
        super.getInfrastructureElementModel().initializeFrom(source);
      }

      if (association.equals(InformationSystemReleaseType.ASSOCIATION_PROJECTS)) {
        projectAvailable = true;
        super.getProjectModel().initializeFrom(source);
      }

      if (association.equals(InformationSystemReleaseType.ASSOCIATION_TECHNICALCOMPONENTRELEASES)) {
        technicalComponentAvailable = true;
        super.getTechnicalComponentModel().initializeFrom(source);
      }

      if (association.equals(InformationSystemReleaseType.ASSOCIATION_PREDECESSORS)) {
        predecessorAvailable = true;
        super.getPredecessorModel().initializeFrom(source);
      }

      if (association.equals(InformationSystemReleaseType.ASSOCIATION_SUCCESSORS)) {
        successorAvailable = true;
        super.getSuccessorModel().initializeFrom(source);
      }

      if (association.equals(InformationSystemReleaseType.ASSOCIATION_PARENT)) {
        parentAvailable = true;
        super.getParentModel().initializeFrom(source);
      }

      if (association.equals(InformationSystemReleaseType.ASSOCIATION_BASECOMPONENTS)) {
        baseComponentsAvailable = true;
        super.getBaseComponentModel().initializeFrom(source);
      }

      if (association.equals(InformationSystemReleaseType.ASSOCIATION_PARENTCOMPONENTS)) {
        parentComponentsAvailable = true;
        super.getParentComponentModel().initializeFrom(source);
      }

      if (association.equals(BusinessMappingType.ASSOCIATION_BUSINESSPROCESS) || association.equals(BusinessMappingType.ASSOCIATION_BUSINESSUNIT)
          || association.equals(BusinessMappingType.ASSOCIATION_PRODUCT)) {

        if (!businessMappingAvailable) {
          businessMappingAvailable = true;
          initBusinessMappings(source);
        }

        if (association.equals(BusinessMappingType.ASSOCIATION_BUSINESSPROCESS)) {
          pathToSelectedBizMappings.add(BusinessMappingCmMu.PATH_TO_BUSINESSPROCESS);
        }

        if (association.equals(BusinessMappingType.ASSOCIATION_BUSINESSUNIT)) {
          pathToSelectedBizMappings.add(BusinessMappingCmMu.PATH_TO_BUSINESSUNIT);
        }

        if (association.equals(BusinessMappingType.ASSOCIATION_PRODUCT)) {
          pathToSelectedBizMappings.add(BusinessMappingCmMu.PATH_TO_PRODUCT);
        }
      }
    }
  }

  /**
   * @param properties
   * @param source 
   * @param type 
   */
  private void initPropertiesFrom(List<String> properties, MassUpdateType type, InformationSystemRelease source) {
    for (String property : properties) {
      if (type.isPropertyIdEqual(property, InformationSystemReleaseType.PROPERTY_NAME)) {
        namePropertyAvailable = true;
        super.getReleaseNameModel().initializeFrom(source);
      }

      if (type.isPropertyIdEqual(property, InformationSystemReleaseType.PROPERTY_DESCRIPTION)) {
        descriptionPropertyAvailable = true;
        super.getDescriptionModel().initializeFrom(source);
      }

      if (type.isPropertyIdEqual(property, InformationSystemReleaseTypeMu.PROPERTY_TIMESPAN)) {
        timespanPropertyAvailable = true;
        super.getRuntimePeriodModel().initializeFrom(source);
      }

      if (type.isPropertyIdEqual(property, InformationSystemReleaseType.PROPERTY_TYPEOFSTATUS)) {
        statusPropertyAvailable = true;
        super.getStatusModel().initializeFrom(source);
      }
    }

  }

  @Override
  public void update() {

    getNameComponentModel().update();
    getDescriptionComponentModel().update();
    getStatusComponentModel().update();
    getRuntimePeriodComponentModel().update();

    getBusinessFunctionComponentModel().update();
    getBusinessObjectComponentModel().update();
    getInformationSystemDomainComponentModel().update();
    getInfrastructureElementComponentModel().update();
    getProjectComponentModel().update();
    getTechnicalComponentComponentModel().update();
    getParentsModel().update();
    getPredecessorsModel().update();
    getSuccessorsModel().update();
    getBaseComponentsModel().update();
    getParentComponentsModel().update();

    if (businessMappingAvailable) {
      List<BusinessProcess> processes = SpringServiceFactory.getBusinessProcessService().loadElementList();
      List<BusinessUnit> units = SpringServiceFactory.getBusinessUnitService().loadElementList();
      List<Product> products = SpringServiceFactory.getProductService().loadElementList();

      for (BusinessMappingCmMu model : businessMappingComponentModels) {
        model.update(processes, units, products);
      }
    }
  }

  @Override
  public void configure(InformationSystemRelease target) {

    getNameComponentModel().configure(target);
    getDescriptionComponentModel().configure(target);
    getRuntimePeriodComponentModel().configure(target);
    getStatusComponentModel().configure(target);

    getBusinessFunctionComponentModel().configure(target);
    getBusinessObjectComponentModel().configure(target);
    getInformationSystemDomainComponentModel().configure(target);
    getInfrastructureElementComponentModel().configure(target);
    getProjectComponentModel().configure(target);
    getTechnicalComponentComponentModel().configure(target);
    getBaseComponentsModel().configure(target);
    getParentComponentsModel().configure(target);
    getPredecessorsModel().configure(target);
    getSuccessorsModel().configure(target);
    getParentsModel().configure(target);
  }

  /**
   * Adds the path in compareString to the list in result if the path is not contained in list
   * 
   * @param list
   *          The list with the selected paths
   * @param compareString
   *          The path to check for
   * @param result
   *          The result list containing all paths (associations of business mapping) not selected
   *          by the user
   */
  private void addUnselectedPathString(List<String> list, String compareString, List<String> result) {
    for (String item : list) {
      if (item.equals(compareString)) {
        return;
      }
    }
    result.add(compareString);
  }

  private void initBusinessMappings(InformationSystemRelease release) {

    // Load all available business processes, business units and products
    List<BusinessProcess> processes = SpringServiceFactory.getBusinessProcessService().loadElementList();
    List<BusinessUnit> units = SpringServiceFactory.getBusinessUnitService().loadElementList();
    List<Product> products = SpringServiceFactory.getProductService().loadElementList();

    // Only handle those business mappings that contain no reference to any business function.
    // Business functions are handled differently.
    for (BusinessMapping bizMapping : SpringServiceFactory.getBusinessMappingService().getBusinessMappingsWithNoFunctions(release)) {
      BusinessMappingCmMu model;
      model = new BusinessMappingCmMu(bizMapping, ComponentMode.EDIT);
      model.initializeFrom(processes, units, products);
      businessMappingComponentModels.add(model);
    }
  }
}

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
import de.iteratec.iteraplan.businesslogic.reports.query.type.TechnicalComponentReleaseType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.TechnicalComponentReleaseTypeMu;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.presentation.dialog.TechnicalComponent.TechnicalComponentReleaseComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.NullSafeModel;


/**
 * Component model for mass update operations on {@link TechnicalComponentRelease}s.
 */
public class TechnicalComponentReleaseCmMu extends TechnicalComponentReleaseComponentModel implements
    MassUpdateComponentModel<TechnicalComponentRelease> {

  /** Serialization version. */
  private static final long                               serialVersionUID                          = -4396605892320426517L;

  private final ComponentModel<TechnicalComponentRelease> nullSafeModel                             = new NullSafeModel<TechnicalComponentRelease>();

  private boolean                                         namePropertyAvailable                     = false;
  private boolean                                         descriptionPropertyAvailable              = false;
  private boolean                                         timespanPropertyAvailable                 = false;
  private boolean                                         statusPropertyAvailable                   = false;
  private boolean                                         availableForInterfacesPropertyAvailable   = false;

  private boolean                                         architecturalDomainAssociationAvailable   = false;
  private boolean                                         informationSystemAssociationAvailable     = false;
  private boolean                                         infrastructureElementAssociationAvailable = false;
  private boolean                                         predessorsAssociationAvailable            = false;
  private boolean                                         baseComponentsAssociationAvailable        = false;

  public TechnicalComponentReleaseCmMu() {

    super(ComponentMode.EDIT);

    String[] header = new String[] { NAME_LABEL };
    String[] fields = new String[] { HIERARCHICAL_NAME };

    super.getArchitecturalDomainModel().updateHeaderAndFields(header, fields);
    super.getInfrastructureElementModel().updateHeaderAndFields(header, fields);
    super.getPredecessorModel().updateHeaderAndFields(header, fields);
    super.getBaseComponentModel().updateHeaderAndFields(header, fields);
  }

  public ComponentModel<TechnicalComponentRelease> getAvailableForInterfacesComponentModel() {
    return availableForInterfacesPropertyAvailable ? super.getAvailableForInterfacesModel() : nullSafeModel;
  }

  public ComponentModel<TechnicalComponentRelease> getRuntimePeriodComponentModel() {
    return timespanPropertyAvailable ? super.getRuntimePeriodModel() : nullSafeModel;
  }

  public ComponentModel<TechnicalComponentRelease> getDescriptionComponentModel() {
    return descriptionPropertyAvailable ? super.getDescriptionModel() : nullSafeModel;
  }

  public ComponentModel<TechnicalComponentRelease> getNameComponentModel() {
    return namePropertyAvailable ? super.getReleaseNameModel() : nullSafeModel;
  }

  public ComponentModel<TechnicalComponentRelease> getStatusComponentModel() {
    return statusPropertyAvailable ? super.getStatusModel() : nullSafeModel;
  }

  public ComponentModel<TechnicalComponentRelease> getArchitecturalDomainComponentModel() {
    return architecturalDomainAssociationAvailable ? super.getArchitecturalDomainModel() : nullSafeModel;
  }

  public ComponentModel<TechnicalComponentRelease> getInformationSystemReleaseComponentModel() {
    return informationSystemAssociationAvailable ? super.getInformationSystemReleaseModel() : nullSafeModel;
  }

  public ComponentModel<TechnicalComponentRelease> getInfrastructureElementComponentModel() {
    return infrastructureElementAssociationAvailable ? super.getInfrastructureElementModel() : nullSafeModel;
  }

  public ComponentModel<TechnicalComponentRelease> getBaseComponentsComponentModel() {
    return baseComponentsAssociationAvailable ? super.getBaseComponentModel() : nullSafeModel;
  }

  public ComponentModel<TechnicalComponentRelease> getPredecessorComponentModel() {
    return predessorsAssociationAvailable ? super.getPredecessorModel() : nullSafeModel;
  }

  public Map<String, String> getComponentModelAssignment() {

    Map<String, String> assignment = new HashMap<String, String>();
    assignment.put(TechnicalComponentReleaseType.PROPERTY_NAME, "nameComponentModel");
    assignment.put(TechnicalComponentReleaseType.PROPERTY_DESCRIPTION, "descriptionComponentModel");
    assignment.put(TechnicalComponentReleaseTypeMu.PROPERTY_TIMESPAN, "runtimePeriodComponentModel");
    assignment.put(TechnicalComponentReleaseType.PROPERTY_TYPEOFSTATUS, "statusComponentModel");
    assignment.put(TechnicalComponentReleaseType.PROPERTY_AVAILABLEFORINTERFACES, "availableForInterfacesComponentModel");
    assignment.put(TechnicalComponentReleaseType.ASSOCIATION_ARCHITECTURALDOMAINS, "architecturalDomainComponentModel");
    assignment.put(TechnicalComponentReleaseType.ASSOCIATION_INFORMATIONSYSTEMRELEASES, "informationSystemReleaseComponentModel");
    assignment.put(TechnicalComponentReleaseType.ASSOCIATION_INFRASTRUCTUREELEMENT_ASSOCIATIONS, "infrastructureElementComponentModel");
    assignment.put(TechnicalComponentReleaseType.ASSOCIATION_PREDECESSORS, "predecessorComponentModel");
    assignment.put(TechnicalComponentReleaseType.ASSOCIATION_BASECOMPONENTS, "baseComponentsComponentModel");

    return assignment;
  }

  /**
   * @return Returns a map of getter names, which are used to gain access to the component model by reflection.
   *         See MassUpdateFrontendService.
   */
  public Map<String, String> getAssociationGetMethodAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();

    assignment.put(TechnicalComponentReleaseType.ASSOCIATION_ARCHITECTURALDOMAINS, "getArchitecturalDomainModel");
    assignment.put(TechnicalComponentReleaseType.ASSOCIATION_INFORMATIONSYSTEMRELEASES, "getInformationSystemReleaseModel");
    assignment.put(TechnicalComponentReleaseType.ASSOCIATION_INFRASTRUCTUREELEMENT_ASSOCIATIONS, "getInfrastructureElementModel");
    assignment.put(TechnicalComponentReleaseType.ASSOCIATION_PREDECESSORS, "getPredecessorComponentModel");
    assignment.put(TechnicalComponentReleaseType.ASSOCIATION_BASECOMPONENTS, "getBaseComponentsComponentModel");

    return assignment;
  }

  public void initializeFrom(TechnicalComponentRelease source, List<String> properties, List<String> associations) {

    MassUpdateType type = TechnicalComponentReleaseTypeMu.getInstance();

    for (String property : properties) {
      if (type.isPropertyIdEqual(property, TechnicalComponentReleaseType.PROPERTY_NAME)) {
        namePropertyAvailable = true;
        super.getReleaseNameModel().initializeFrom(source);
      }

      if (type.isPropertyIdEqual(property, TechnicalComponentReleaseType.PROPERTY_DESCRIPTION)) {
        descriptionPropertyAvailable = true;
        super.getDescriptionModel().initializeFrom(source);
      }

      if (type.isPropertyIdEqual(property, TechnicalComponentReleaseType.PROPERTY_TYPEOFSTATUS)) {
        statusPropertyAvailable = true;
        super.getStatusModel().initializeFrom(source);
      }

      if (type.isPropertyIdEqual(property, TechnicalComponentReleaseTypeMu.PROPERTY_TIMESPAN)) {
        timespanPropertyAvailable = true;
        super.getRuntimePeriodModel().initializeFrom(source);
      }

      if (type.isPropertyIdEqual(property, TechnicalComponentReleaseType.PROPERTY_AVAILABLEFORINTERFACES)) {
        availableForInterfacesPropertyAvailable = true;
        super.getAvailableForInterfacesModel().initializeFrom(source);
      }
    }

    for (String association : associations) {
      if (association.equals(TechnicalComponentReleaseType.ASSOCIATION_ARCHITECTURALDOMAINS)) {
        architecturalDomainAssociationAvailable = true;
        super.getArchitecturalDomainModel().initializeFrom(source);
      }

      if (association.equals(TechnicalComponentReleaseType.ASSOCIATION_INFORMATIONSYSTEMRELEASES)) {
        informationSystemAssociationAvailable = true;
        super.getInformationSystemReleaseModel().initializeFrom(source);
      }

      if (association.equals(TechnicalComponentReleaseType.ASSOCIATION_INFRASTRUCTUREELEMENT_ASSOCIATIONS)) {
        infrastructureElementAssociationAvailable = true;
        super.getInfrastructureElementModel().initializeFrom(source);
      }

      if (association.equals(TechnicalComponentReleaseType.ASSOCIATION_PREDECESSORS)) {
        predessorsAssociationAvailable = true;
        super.getPredecessorModel().initializeFrom(source);
      }

      if (association.equals(TechnicalComponentReleaseType.ASSOCIATION_BASECOMPONENTS)) {
        baseComponentsAssociationAvailable = true;
        super.getBaseComponentModel().initializeFrom(source);
      }
    }
  }

  @Override
  public void update() {

    getNameComponentModel().update();
    getDescriptionComponentModel().update();
    getStatusComponentModel().update();
    getRuntimePeriodComponentModel().update();
    getAvailableForInterfacesComponentModel().update();
    getArchitecturalDomainComponentModel().update();
    getInformationSystemReleaseComponentModel().update();
    getInfrastructureElementComponentModel().update();
    getPredecessorComponentModel().update();
    getBaseComponentsComponentModel().update();
  }

  @Override
  public void configure(TechnicalComponentRelease target) {

    getNameComponentModel().configure(target);
    getDescriptionComponentModel().configure(target);
    getStatusComponentModel().configure(target);
    getRuntimePeriodComponentModel().configure(target);
    getAvailableForInterfacesComponentModel().configure(target);
    getArchitecturalDomainComponentModel().configure(target);
    getInformationSystemReleaseComponentModel().configure(target);
    getInfrastructureElementComponentModel().configure(target);
    getPredecessorComponentModel().configure(target);
    getBaseComponentsComponentModel().configure(target);
  }
}
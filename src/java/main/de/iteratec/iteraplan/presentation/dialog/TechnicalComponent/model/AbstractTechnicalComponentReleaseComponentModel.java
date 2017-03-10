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
package de.iteratec.iteraplan.presentation.dialog.TechnicalComponent.model;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.BooleanComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.BuildingBlockComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.PersistantEnumComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.RuntimePeriodComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.StringComponentModel;


public abstract class AbstractTechnicalComponentReleaseComponentModel extends BuildingBlockComponentModel<TechnicalComponentRelease> {

  /** Serialization version. */
  private static final long                                                           serialVersionUID  = 5074324322062675949L;
  protected static final String                                                       DESCRIPTION_LABEL = "global.description";
  protected static final String                                                       STATUS_LABEL      = "global.type_of_status";

  private final TechnicalComponentReleaseNameComponentModel                           releaseNameModel;
  private final StringComponentModel<TechnicalComponentRelease>                       descriptionModel;
  private final RuntimePeriodComponentModel<TechnicalComponentRelease>                runtimePeriodModel;
  private final PersistantEnumComponentModel<TechnicalComponentRelease, TypeOfStatus> statusModel;
  private final BooleanComponentModel<TechnicalComponentRelease>                      availableForInterfacesModel;

  public AbstractTechnicalComponentReleaseComponentModel(ComponentMode componentMode) {
    super(componentMode);

    this.releaseNameModel = new TechnicalComponentReleaseNameComponentModel(componentMode, "releaseName");
    this.descriptionModel = new DescriptionStringComponentModel(componentMode, "description", DESCRIPTION_LABEL);
    this.runtimePeriodModel = new RuntimePeriodComponentModel<TechnicalComponentRelease>(componentMode, "period");
    this.statusModel = new StatusPersistantEnumComponentModel(componentMode, "status", STATUS_LABEL);
    this.availableForInterfacesModel = new AvailableForInterfacesBooleanComponentModel(componentMode, " availableForInterfaces",
        "technicalComponentRelease.availableForInterfaces");
  }

  @Override
  public void initializeFrom(TechnicalComponentRelease source) {
    super.initializeFrom(source);
    descriptionModel.initializeFrom(source);
    releaseNameModel.initializeFrom(source);
    runtimePeriodModel.initializeFrom(source);
    statusModel.initializeFrom(source);
    availableForInterfacesModel.initializeFrom(source);
  }

  @Override
  public void update() {
    super.update();
    descriptionModel.update();
    releaseNameModel.update();
    runtimePeriodModel.update();
    statusModel.update();
    availableForInterfacesModel.update();

  }

  @Override
  public void configure(TechnicalComponentRelease target) {
    super.configure(target);
    descriptionModel.configure(target);
    releaseNameModel.configure(target);
    statusModel.configure(target);
    runtimePeriodModel.configure(target);
    availableForInterfacesModel.configure(target);
  }

  public TechnicalComponentReleaseNameComponentModel getReleaseNameModel() {
    return releaseNameModel;
  }

  public StringComponentModel<TechnicalComponentRelease> getDescriptionModel() {
    return descriptionModel;
  }

  public RuntimePeriodComponentModel<TechnicalComponentRelease> getRuntimePeriodModel() {
    return runtimePeriodModel;
  }

  public PersistantEnumComponentModel<TechnicalComponentRelease, TypeOfStatus> getStatusModel() {
    return statusModel;
  }

  public BooleanComponentModel<TechnicalComponentRelease> getAvailableForInterfacesModel() {
    return availableForInterfacesModel;
  }

  public void validate(Errors errors) {
    errors.pushNestedPath("releaseNameModel");
    releaseNameModel.validate(errors);
    errors.popNestedPath();

    errors.pushNestedPath("descriptionModel");
    descriptionModel.validateDescription(errors);
    errors.popNestedPath();

  }

  private static final class DescriptionStringComponentModel extends StringComponentModel<TechnicalComponentRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = -4744523069800397972L;

    public DescriptionStringComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(TechnicalComponentRelease target, String stringToSet) {
      target.setDescription(stringToSet);
    }

    @Override
    public String getStringFromElement(TechnicalComponentRelease source) {
      return source.getDescription();
    }
  }

  private static final class StatusPersistantEnumComponentModel extends PersistantEnumComponentModel<TechnicalComponentRelease, TypeOfStatus> {
    /** Serialization version. */
    private static final long serialVersionUID = 8615233929328093157L;

    public StatusPersistantEnumComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    protected void setEnumForElement(TechnicalComponentRelease target, TypeOfStatus currentEnum) {
      target.setTypeOfStatus(currentEnum);
    }

    @Override
    protected TypeOfStatus getEnumFromElement(TechnicalComponentRelease source) {
      return source.getTypeOfStatus();
    }
  }

  private static final class AvailableForInterfacesBooleanComponentModel extends BooleanComponentModel<TechnicalComponentRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = -1369203402012608046L;

    public AvailableForInterfacesBooleanComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public Boolean getBooleanFromElement(TechnicalComponentRelease source) {
      return Boolean.valueOf(source.getTechnicalComponent().isAvailableForInterfaces());
    }

    @Override
    public void setBooleanForElement(TechnicalComponentRelease target, Boolean booleanToSet) {
      target.getTechnicalComponent().setAvailableForInterfaces(booleanToSet == null ? false : booleanToSet.booleanValue());
    }
  }

}

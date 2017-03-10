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
package de.iteratec.iteraplan.presentation.dialog.InformationSystem.model;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.BuildingBlockComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.PersistantEnumComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.RuntimePeriodComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.StringComponentModel;


public abstract class AbstractInformationSystemReleaseComponentModel extends BuildingBlockComponentModel<InformationSystemRelease> {

  /** Serialization version. */
  private static final long                                                          serialVersionUID   = -781056782531016637L;
  protected static final String                                                      NAME_LABEL         = "global.name";
  protected static final String                                                      RELEASE_NAME_LABEL = "releaseName";
  protected static final String                                                      DESCRIPTION_LABEL  = "global.description";
  protected static final String                                                      STATUS_LABEL       = "global.type_of_status";
  protected static final String                                                      PARENT_LABEL       = "informationSystemRelease.parent";

  private final InformationSystemReleaseNameComponentModel                           releaseNameModel;
  private final StringComponentModel<InformationSystemRelease>                       descriptionModel;
  private final RuntimePeriodComponentModel<InformationSystemRelease>                runtimePeriodModel;
  private final PersistantEnumComponentModel<InformationSystemRelease, TypeOfStatus> statusModel;

  public AbstractInformationSystemReleaseComponentModel(ComponentMode componentMode) {
    super(componentMode);

    this.releaseNameModel = new InformationSystemReleaseNameComponentModel(componentMode, RELEASE_NAME_LABEL);
    this.descriptionModel = new DescriptionStringComponentModel(componentMode, "description", DESCRIPTION_LABEL);
    this.runtimePeriodModel = new RuntimePeriodComponentModel<InformationSystemRelease>(componentMode, "period");
    this.statusModel = new StatusComponentModel(componentMode, "status", STATUS_LABEL);
  }

  public InformationSystemReleaseNameComponentModel getReleaseNameModel() {
    return releaseNameModel;
  }

  public StringComponentModel<InformationSystemRelease> getDescriptionModel() {
    return descriptionModel;
  }

  public RuntimePeriodComponentModel<InformationSystemRelease> getRuntimePeriodModel() {
    return runtimePeriodModel;
  }

  public PersistantEnumComponentModel<InformationSystemRelease, TypeOfStatus> getStatusModel() {
    return statusModel;
  }

  @Override
  public void initializeFrom(InformationSystemRelease source) {
    super.initializeFrom(source);
    descriptionModel.initializeFrom(source);
    releaseNameModel.initializeFrom(source);
    statusModel.initializeFrom(source);
    runtimePeriodModel.initializeFrom(source);
  }

  @Override
  public void update() {
    super.update();
    descriptionModel.update();
    releaseNameModel.update();
    statusModel.update();
    runtimePeriodModel.update();
  }

  @Override
  public void configure(InformationSystemRelease target) {
    super.configure(target);
    descriptionModel.configure(target);
    releaseNameModel.configure(target);
    statusModel.configure(target);
    runtimePeriodModel.configure(target);
  }

  public void validate(Errors errors) {
    errors.pushNestedPath("releaseNameModel");
    releaseNameModel.validate(errors);
    errors.popNestedPath();

    errors.pushNestedPath("descriptionModel");
    descriptionModel.validateDescription(errors);
    errors.popNestedPath();

    errors.pushNestedPath("runtimePeriodModel");
    runtimePeriodModel.validate(errors);
    errors.popNestedPath();
  }

  private static final class DescriptionStringComponentModel extends StringComponentModel<InformationSystemRelease> {

    /** Serialization version. */
    private static final long serialVersionUID = -3274466879929219023L;

    public DescriptionStringComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(InformationSystemRelease target, String stringToSet) {
      target.setDescription(stringToSet);
    }

    @Override
    public String getStringFromElement(InformationSystemRelease source) {
      return source.getDescription();
    }
  }

  private static final class StatusComponentModel extends PersistantEnumComponentModel<InformationSystemRelease, TypeOfStatus> {
    /** Serialization version. */
    private static final long serialVersionUID = -4777561358287705107L;

    public StatusComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    protected void setEnumForElement(InformationSystemRelease target, TypeOfStatus currentEnum) {
      target.setTypeOfStatus(currentEnum);
    }

    @Override
    protected TypeOfStatus getEnumFromElement(InformationSystemRelease source) {
      return source.getTypeOfStatus();
    }
  }

}

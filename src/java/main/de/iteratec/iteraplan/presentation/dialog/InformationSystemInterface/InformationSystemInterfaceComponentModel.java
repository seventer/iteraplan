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
package de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface;

import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemInterfaceTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface.model.SelectInformationSystemInterfaceCm;
import de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface.model.SelectNewInformationSystemInterfaceCm;
import de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface.model.TransportAssociationComponentModel;
import de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface.model.TransportInfoComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.IteraplanValidationUtils;
import de.iteratec.iteraplan.presentation.dialog.common.model.BuildingBlockComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ElementNameComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModelDL;
import de.iteratec.iteraplan.presentation.dialog.common.model.StringComponentModel;


public class InformationSystemInterfaceComponentModel extends BuildingBlockComponentModel<InformationSystemInterface> {

  /** Serialization version. */
  private static final long                                                                             serialVersionUID = 1813947255504277639L;
  protected static final String                                                                         NAME_LABEL       = "global.name";
  protected static final String                                                                         NAME_FIELD       = "name";

  private final ElementNameComponentModel<InformationSystemInterface>                                   nameModel;

  private final SelectInformationSystemInterfaceCm                                                      selectModel;

  private final SelectNewInformationSystemInterfaceCm                                                   selectNewModel;

  private final StringComponentModel<InformationSystemInterface>                                        descriptionModel;

  private final ManyAssociationSetComponentModel<InformationSystemInterface, TechnicalComponentRelease> technicalComponentReleaseModel;

  private final TransportAssociationComponentModel                                                      transportModel;

  private final TransportInfoComponentModel                                                             transportInfoModel;
  private Integer                                                                                       elementId;

  public InformationSystemInterfaceComponentModel(ComponentMode componentMode) {
    super(componentMode);
    this.setHtmlId("isi");

    nameModel = new NameCM(componentMode, NAME_FIELD, NAME_LABEL);
    descriptionModel = new DescriptionCM(componentMode, "description", "");
    selectModel = new SelectInformationSystemInterfaceCm(componentMode, "selectISI");
    selectNewModel = new SelectNewInformationSystemInterfaceCm(componentMode, "selectNewISI");
    technicalComponentReleaseModel = new TechnicalComponentReleaseCM(componentMode, "tcr", "interface.to.technicalComponentReleases", new String[] {
        NAME_LABEL, "global.description" }, new String[] { "releaseName", "description" }, "releaseName", new TechnicalComponentRelease());

    transportModel = new TransportAssociationComponentModel(componentMode);
    transportInfoModel = new TransportInfoComponentModel(componentMode, "isiDirection");
  }

  @Override
  public void initializeFrom(InformationSystemInterface source) {
    super.initializeFrom(source);
    nameModel.initializeFrom(source);
    selectModel.initializeFrom(source);
    selectNewModel.initializeFrom(source);
    descriptionModel.initializeFrom(source);
    technicalComponentReleaseModel.initializeFrom(source);
    transportModel.initializeFrom(source);
    transportInfoModel.initializeFrom(source);
    this.elementId = source.getId();
  }

  @Override
  public void update() {
    super.update();
    nameModel.update();
    selectModel.update();
    selectNewModel.update();
    descriptionModel.update();
    technicalComponentReleaseModel.update();
    transportModel.update();
    transportInfoModel.update();
  }

  @Override
  public void configure(InformationSystemInterface connection) {
    super.configure(connection);
    nameModel.configure(connection);
    selectModel.configure(connection);
    selectNewModel.configure(connection);
    descriptionModel.configure(connection);
    technicalComponentReleaseModel.configure(connection);
    transportModel.configure(connection);
    transportInfoModel.configure(connection);
  }

  public StringComponentModel<InformationSystemInterface> getNameModel() {
    return nameModel;
  }

  public SelectInformationSystemInterfaceCm getSelectModel() {
    return selectModel;
  }

  public StringComponentModel<InformationSystemInterface> getDescriptionModel() {
    return descriptionModel;
  }

  public ManyAssociationSetComponentModel<InformationSystemInterface, TechnicalComponentRelease> getTechnicalComponentReleaseModel() {
    return technicalComponentReleaseModel;
  }

  public ManyAssociationSetComponentModel<InformationSystemInterface, Transport> getTransportModel() {
    return transportModel;
  }

  public TransportInfoComponentModel getTransportInfoModel() {
    return transportInfoModel;
  }

  public SelectNewInformationSystemInterfaceCm getSelectNewModel() {
    return selectNewModel;
  }

  @Override
  public Type<? extends BuildingBlock> getManagedType() {
    return InformationSystemInterfaceTypeQu.getInstance();
  }

  public Integer getElementId() {
    return elementId;
  }

  public void validate(Errors errors) {
    errors.pushNestedPath("descriptionModel");
    descriptionModel.validateDescription(errors);
    errors.popNestedPath();

    if (!isValidHierarchyPartName(nameModel.getName())) {
      errors.rejectValue("nameModel.name", "errors.invalidCharacterInName",
          IteraplanValidationUtils.getLocalizedArgs(getManagedType().getTypeNamePresentationKey()), "Invalid Characters in Name");
    }
  }

  private static final class NameCM extends ElementNameComponentModel<InformationSystemInterface> {
    /**Serialization version. */
    private static final long serialVersionUID = 6027647836898474911L;

    public NameCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(InformationSystemInterface target, String stringToSet) {
      target.setName(stringToSet);
    }

    @Override
    public String getStringFromElement(InformationSystemInterface source) {
      return source.getName();
    }
  }

  private static final class DescriptionCM extends StringComponentModel<InformationSystemInterface> {
    /**Serialization version. */
    private static final long serialVersionUID = 4552510886874327713L;

    public DescriptionCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(InformationSystemInterface element) {
      return element.getDescription();
    }

    @Override
    public void setStringForElement(InformationSystemInterface element, String stringToSet) {
      element.setDescription(stringToSet);
    }
  }

  private static final class TechnicalComponentReleaseCM extends
      ManyAssociationSetComponentModelDL<InformationSystemInterface, TechnicalComponentRelease> {

    /**Serialization version. */
    private static final long serialVersionUID = 8530838060209661037L;

    public TechnicalComponentReleaseCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, TechnicalComponentRelease dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected TechnicalComponentReleaseService getService() {
      return SpringServiceFactory.getTechnicalComponentReleaseService();
    }
    
    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE;
    }

    @Override
    protected Set<TechnicalComponentRelease> getConnectedElements(InformationSystemInterface source) {
      return source.getTechnicalComponentReleases();
    }

    @Override
    protected void setConnectedElements(InformationSystemInterface target, Set<TechnicalComponentRelease> toConnect) {
      if (!target.getTechnicalComponentReleases().equals(toConnect)) {
        List<TechnicalComponentRelease> reloadedEntities = SpringServiceFactory.getTechnicalComponentReleaseService().reload(toConnect);
        target.removeTechnicalComponentReleases();
        target.addTechnicalComponentReleases(reloadedEntities);
      }
    }
  }

}

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
package de.iteratec.iteraplan.presentation.dialog.ArchitectualDomain;

import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.base.Objects;

import de.iteratec.iteraplan.businesslogic.reports.query.type.ArchitecturalDomainTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.ArchitecturalDomainService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.IteraplanValidationUtils;
import de.iteratec.iteraplan.presentation.dialog.common.model.BuildingBlockComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ElementNameComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationListComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationListComponentModelDL;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModelDL;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyToOneComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyToOneComponentModelDL;
import de.iteratec.iteraplan.presentation.dialog.common.model.StringComponentModel;


public class ArchitecturalDomainComponentModel extends BuildingBlockComponentModel<ArchitecturalDomain> {

  /** Serialization version. */
  private static final long                                                                      serialVersionUID = -4983260200941103499L;

  protected static final String                                                                  NAME_LABEL       = "global.name";

  private final ElementNameComponentModel<ArchitecturalDomain>                                   nameModel;
  private final StringComponentModel<ArchitecturalDomain>                                        descriptionModel;
  private final ManyAssociationSetComponentModel<ArchitecturalDomain, TechnicalComponentRelease> technicalComponentReleaseModel;
  private final ManyToOneComponentModel<ArchitecturalDomain, ArchitecturalDomain>                parentModel;
  private final ManyAssociationListComponentModel<ArchitecturalDomain, ArchitecturalDomain>      childrenModel;

  private int                                                                                    subElementCount  = 0;
  private Integer                                                                                elementId;

  public ArchitecturalDomainComponentModel(ComponentMode componentMode) {
    super(componentMode);
    this.setHtmlId("ad");

    nameModel = new NameCM(componentMode, "name", NAME_LABEL);
    descriptionModel = new DescriptionCM(componentMode, "description", "global.description");
    technicalComponentReleaseModel = new TechnicalComponentReleaseCM(componentMode, "tcr", "architecturalDomain.to.technicalComponentReleases",
        new String[] { NAME_LABEL, "global.description" }, new String[] { "releaseName", "description" }, "releaseName",
        new TechnicalComponentRelease());

    parentModel = new ParentCM(componentMode, "parent", "architecturalDomain.parent", false);
    childrenModel = new ChildrenCM(componentMode, "children", "architecturalDomain.children", new String[] { NAME_LABEL, "global.description" },
        new String[] { "name", "description" }, "hierarchicalName", new ArchitecturalDomain());
  }

  public ManyAssociationSetComponentModel<ArchitecturalDomain, TechnicalComponentRelease> getTechnicalComponentReleaseModel() {
    return technicalComponentReleaseModel;
  }

  public ManyAssociationListComponentModel<ArchitecturalDomain, ArchitecturalDomain> getChildrenModel() {
    return childrenModel;
  }

  public StringComponentModel<ArchitecturalDomain> getDescriptionModel() {
    return descriptionModel;
  }

  public StringComponentModel<ArchitecturalDomain> getNameModel() {
    return nameModel;
  }

  public ManyToOneComponentModel<ArchitecturalDomain, ArchitecturalDomain> getParentModel() {
    return parentModel;
  }

  public int getSubElementCount() {
    return subElementCount;
  }

  public Integer getElementId() {
    return elementId;
  }

  @Override
  public void configure(ArchitecturalDomain target) {
    super.configure(target);
    nameModel.configure(target);
    descriptionModel.configure(target);
    technicalComponentReleaseModel.configure(target);
    parentModel.configure(target);
    childrenModel.configure(target);
  }

  @Override
  public void initializeFrom(ArchitecturalDomain source) {
    super.initializeFrom(source);
    nameModel.initializeFrom(source);
    descriptionModel.initializeFrom(source);
    technicalComponentReleaseModel.initializeFrom(source);
    parentModel.initializeFrom(source);
    childrenModel.initializeFrom(source);
    subElementCount = source.getChildren().size();
    this.elementId = source.getId();
  }

  @Override
  public void update() {
    if (getComponentMode() != ComponentMode.READ) {
      super.update();
      nameModel.update();
      descriptionModel.update();
      technicalComponentReleaseModel.update();
      parentModel.update();
      childrenModel.update();
    }
  }

  public void validate(Errors errors) {
    // check for non-empty name
    errors.pushNestedPath("nameModel");
    nameModel.validate(errors);
    errors.popNestedPath();

    errors.pushNestedPath("descriptionModel");
    descriptionModel.validateDescription(errors);
    errors.popNestedPath();

    if (!isValidHierarchyPartName(nameModel.getName())) {
      errors.rejectValue("nameModel.name", "errors.invalidCharacterInName",
          IteraplanValidationUtils.getLocalizedArgs(getManagedType().getTypeNamePresentationKey()), "Invalid Characters in Name");
    }
  }

  @Override
  public Type<? extends BuildingBlock> getManagedType() {
    return ArchitecturalDomainTypeQu.getInstance();
  }

  public void sortEverything() {
    childrenModel.sort();
  }

  private static final class NameCM extends ElementNameComponentModel<ArchitecturalDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = 7540424963687127552L;

    public NameCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(ArchitecturalDomain source) {
      if (ArchitecturalDomain.TOP_LEVEL_NAME.equals(source.getName())) {
        setVirtualElementSelected(true);
      }

      return source.getName();
    }

    @Override
    public void setStringForElement(ArchitecturalDomain target, String stringToSet) {
      target.setName(stringToSet);
    }
  }

  private static final class DescriptionCM extends StringComponentModel<ArchitecturalDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = 1274518812260739263L;

    public DescriptionCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(ArchitecturalDomain source) {
      return source.getDescription();
    }

    @Override
    public void setStringForElement(ArchitecturalDomain target, String stringToSet) {
      target.setDescription(stringToSet);
    }
  }

  private static final class TechnicalComponentReleaseCM extends ManyAssociationSetComponentModelDL<ArchitecturalDomain, TechnicalComponentRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = 7026273701166716598L;

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
    protected Set<TechnicalComponentRelease> getConnectedElements(ArchitecturalDomain source) {
      return source.getTechnicalComponentReleases();
    }

    @Override
    protected void setConnectedElements(ArchitecturalDomain target, Set<TechnicalComponentRelease> toConnect) {
      if (!target.getTechnicalComponentReleases().equals(toConnect)) {
        List<TechnicalComponentRelease> reloadedEntities = SpringServiceFactory.getTechnicalComponentReleaseService().reload(toConnect);
        target.removeTechnicalComponentReleases();
        target.addTechnicalComponentReleases(reloadedEntities);
      }
    }
  }

  private static final class ParentCM extends ManyToOneComponentModelDL<ArchitecturalDomain, ArchitecturalDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = -8149499595993919361L;

    public ParentCM(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
      super(componentMode, htmlId, labelKey, nullable);
    }

    @Override
    protected ArchitecturalDomainService getService() {
      return SpringServiceFactory.getArchitecturalDomainService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.ARCHITECTURALDOMAIN;
    }

    @Override
    protected ArchitecturalDomain getConnectedElement(ArchitecturalDomain source) {
      return source.getParent();
    }

    @Override
    protected void setConnectedElement(ArchitecturalDomain target, ArchitecturalDomain parent) {
      final boolean parentsEqual = Objects.equal(target.getParent(), parent);

      if (!target.isTopLevelElement() && !parentsEqual) {
        ArchitecturalDomain reloadParent = SpringServiceFactory.getArchitecturalDomainService().loadObjectById(parent.getId());
        target.removeParent();
        target.addParent(reloadParent);
      }
    }
  }

  private static final class ChildrenCM extends ManyAssociationListComponentModelDL<ArchitecturalDomain, ArchitecturalDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = 2551790406435440596L;

    public ChildrenCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys, String[] connectedElementsFields,
        String availableElementsLabel, ArchitecturalDomain dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected ArchitecturalDomainService getService() {
      return SpringServiceFactory.getArchitecturalDomainService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.ARCHITECTURALDOMAIN;
    }

    @Override
    protected List<ArchitecturalDomain> getConnectedElements(ArchitecturalDomain source) {
      return source.getChildren();
    }

    @Override
    protected void setConnectedElements(ArchitecturalDomain target, List<ArchitecturalDomain> children) {
      if (!target.getChildren().equals(children)) {
        List<ArchitecturalDomain> reloadedChildren = SpringServiceFactory.getArchitecturalDomainService().reload(children);
        ArchitecturalDomain rootProject = SpringServiceFactory.getArchitecturalDomainService().getFirstElement();

        target.removeChildren(rootProject);
        target.addChildren(reloadedChildren);
      }
    }

    @Override
    protected boolean isElementRemovable() {
      return getSourceElement().getParent() != null;
    }
  }

}

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
package de.iteratec.iteraplan.presentation.dialog.InfrastructureElement;

import static de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory.getInformationSystemReleaseService;
import static de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory.getInfrastructureElementService;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.reports.query.type.InfrastructureElementTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.model.sorting.NonHierarchicalEntityComparator;
import de.iteratec.iteraplan.presentation.dialog.FastExport.FastExportEntryMemBean;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.IteraplanValidationUtils;
import de.iteratec.iteraplan.presentation.dialog.common.model.BuildingBlockComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ElementNameComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationListComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationListComponentModelDL;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetAttributableComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModelDL;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyToOneComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyToOneComponentModelDL;
import de.iteratec.iteraplan.presentation.dialog.common.model.StringComponentModel;


/**
 * GUI model for {@link InfrastructureElement}s.
 */
public class InfrastructureElementComponentModel extends BuildingBlockComponentModel<InfrastructureElement> {

  /** Serialization version. */
  private static final long                                                                                                       serialVersionUID  = 6684349206822421163L;
  protected static final String                                                                                                   NAME_LABEL        = "global.name";
  protected static final String                                                                                                   HIERARCHICAL_NAME = "hierarchicalName";
  private static final String                                                                                                     DESCRIPTION_LABEL = "global.description";
  private static final String                                                                                                     DESCRIPTION       = "description";

  private final ElementNameComponentModel<InfrastructureElement>                                                                  nameModel;
  private final StringComponentModel<InfrastructureElement>                                                                       descriptionModel;
  private final ManyAssociationSetComponentModel<InfrastructureElement, InformationSystemRelease>                                 informationSystemReleaseModel;
  private final ManyAssociationSetAttributableComponentModel<InfrastructureElement, TechnicalComponentRelease, Tcr2IeAssociation> technicalComponentReleaseModel;
  private final ManyToOneComponentModel<InfrastructureElement, InfrastructureElement>                                             parentModel;
  private final ManyAssociationListComponentModel<InfrastructureElement, InfrastructureElement>                                   childrenModel;
  private final ManyAssociationSetComponentModel<InfrastructureElement, InfrastructureElement>                                    baseComponentsModel;
  private final ManyAssociationSetComponentModel<InfrastructureElement, InfrastructureElement>                                    parentComponentsModel;

  private int                                                                                                                     subElementCount   = 0;
  private Integer                                                                                                                 elementId;
  private FastExportEntryMemBean                                                                                                  fastExportBean;

  public InfrastructureElementComponentModel(ComponentMode componentMode) {

    super(componentMode);
    this.setHtmlId("ie");
    setFastExportBean(new FastExportEntryMemBean());

    nameModel = new NameCM(componentMode, "name", NAME_LABEL);
    descriptionModel = new DescriptionCM(componentMode, DESCRIPTION, DESCRIPTION_LABEL);
    informationSystemReleaseModel = new InformationSystemReleaseCM(componentMode, "isr", "infrastructureElement.to.informationSystemReleases",
        new String[] { NAME_LABEL, DESCRIPTION_LABEL }, new String[] { HIERARCHICAL_NAME, DESCRIPTION }, HIERARCHICAL_NAME,
        new InformationSystemRelease());

    technicalComponentReleaseModel = new TechnicalComponentReleaseCM(componentMode, "tcr", "infrastructureElement.to.technicalComponentReleases",
        new String[] { NAME_LABEL, DESCRIPTION_LABEL }, new String[] { HIERARCHICAL_NAME, DESCRIPTION }, HIERARCHICAL_NAME,
        new TechnicalComponentRelease());

    parentModel = new ParentCM(componentMode, "parent", "infrastructureElement.parent", false);
    childrenModel = new ChildrenCM(componentMode, "children", "infrastructureElement.children", new String[] { NAME_LABEL, DESCRIPTION_LABEL },
        new String[] { "name", DESCRIPTION }, HIERARCHICAL_NAME, new InfrastructureElement());

    baseComponentsModel = new BaseComponentsCM(componentMode, "baseComponents", "infrastructureElement.baseComponents", new String[] { NAME_LABEL,
        DESCRIPTION_LABEL }, new String[] { "name", DESCRIPTION }, HIERARCHICAL_NAME, new InfrastructureElement());
    parentComponentsModel = new ParentComponentsCM(componentMode, "baseComponents", "infrastructureElement.parentComponents", new String[] {
        NAME_LABEL, DESCRIPTION_LABEL }, new String[] { "name", DESCRIPTION }, HIERARCHICAL_NAME, new InfrastructureElement());
  }

  @Override
  public void configure(InfrastructureElement target) {
    super.configure(target);
    nameModel.configure(target);
    descriptionModel.configure(target);
    informationSystemReleaseModel.configure(target);
    technicalComponentReleaseModel.configure(target);
    parentModel.configure(target);
    childrenModel.configure(target);
    baseComponentsModel.configure(target);
    parentComponentsModel.configure(target);
  }

  public ManyAssociationListComponentModel<InfrastructureElement, InfrastructureElement> getChildrenModel() {
    return childrenModel;
  }

  public StringComponentModel<InfrastructureElement> getDescriptionModel() {
    return descriptionModel;
  }

  public ManyAssociationSetComponentModel<InfrastructureElement, InformationSystemRelease> getInformationSystemReleaseModel() {
    return informationSystemReleaseModel;
  }

  public ManyAssociationSetComponentModel<InfrastructureElement, TechnicalComponentRelease> getTechnicalComponentReleaseModel() {
    return technicalComponentReleaseModel;
  }

  public StringComponentModel<InfrastructureElement> getNameModel() {
    return nameModel;
  }

  public ManyToOneComponentModel<InfrastructureElement, InfrastructureElement> getParentModel() {
    return parentModel;
  }

  public ManyAssociationSetComponentModel<InfrastructureElement, InfrastructureElement> getBaseComponentsModel() {
    return baseComponentsModel;
  }

  public ManyAssociationSetComponentModel<InfrastructureElement, InfrastructureElement> getParentComponentsModel() {
    return parentComponentsModel;
  }

  public int getSubElementCount() {
    return subElementCount;
  }

  @Override
  public void initializeFrom(InfrastructureElement source) {
    super.initializeFrom(source);
    this.elementId = source.getId();
    nameModel.initializeFrom(source);
    descriptionModel.initializeFrom(source);
    informationSystemReleaseModel.initializeFrom(source);
    technicalComponentReleaseModel.initializeFrom(source);
    parentModel.initializeFrom(source);
    childrenModel.initializeFrom(source);
    baseComponentsModel.initializeFrom(source);
    parentComponentsModel.initializeFrom(source);

    subElementCount = source.getChildren().size();
  }

  @Override
  public void update() {
    if (getComponentMode() != ComponentMode.READ) {
      super.update();
      nameModel.update();
      descriptionModel.update();
      informationSystemReleaseModel.update();
      technicalComponentReleaseModel.update();
      parentModel.update();
      childrenModel.update();
      baseComponentsModel.update();
      parentComponentsModel.update();
    }
  }

  public void sortEverything() {
    childrenModel.sort();
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
    return InfrastructureElementTypeQu.getInstance();
  }

  public void setElementId(Integer elementId) {
    this.elementId = elementId;
  }

  public Integer getElementId() {
    return elementId;
  }

  public final void setFastExportBean(FastExportEntryMemBean fastExportBean) {
    this.fastExportBean = fastExportBean;
  }

  public FastExportEntryMemBean getFastExportBean() {
    return fastExportBean;
  }

  private static final class NameCM extends ElementNameComponentModel<InfrastructureElement> {

    /** Serialization version. */
    private static final long serialVersionUID = -1624324417627116178L;

    public NameCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(InfrastructureElement source) {
      if (InfrastructureElement.TOP_LEVEL_NAME.equals(source.getName())) {
        setVirtualElementSelected(true);
      }

      return source.getName();
    }

    @Override
    public void setStringForElement(InfrastructureElement target, String stringToSet) {
      target.setName(stringToSet);
    }
  }

  private static final class DescriptionCM extends StringComponentModel<InfrastructureElement> {

    /** Serialization version. */
    private static final long serialVersionUID = 4959140181626263083L;

    public DescriptionCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(InfrastructureElement source) {
      return source.getDescription();
    }

    @Override
    public void setStringForElement(InfrastructureElement target, String stringToSet) {
      target.setDescription(stringToSet);
    }
  }

  private static final class InformationSystemReleaseCM extends ManyAssociationSetComponentModelDL<InfrastructureElement, InformationSystemRelease> {

    /** Serialization version. */
    private static final long serialVersionUID = -7470560158508715917L;

    public InformationSystemReleaseCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, InformationSystemRelease dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<InformationSystemRelease> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<InformationSystemRelease>();
    }

    @Override
    protected InformationSystemReleaseService getService() {
      return SpringServiceFactory.getInformationSystemReleaseService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE;
    }

    @Override
    protected Set<InformationSystemRelease> getConnectedElements(InfrastructureElement source) {
      return source.getInformationSystemReleases();
    }

    @Override
    protected void setConnectedElements(InfrastructureElement target, Set<InformationSystemRelease> toConnect) {
      if (!target.getInformationSystemReleases().equals(toConnect)) {
        List<InformationSystemRelease> reloadedIsrs = getInformationSystemReleaseService().reload(toConnect);
        target.removeInformationSystemReleases();
        target.addInformationSystemReleases(Sets.newHashSet(reloadedIsrs));
      }
    }
  }

  private static final class TechnicalComponentReleaseCM extends
      ManyAssociationSetAttributableComponentModel<InfrastructureElement, TechnicalComponentRelease, Tcr2IeAssociation> {

    /** Serialization version. */
    private static final long serialVersionUID = 8843751798155927662L;

    public TechnicalComponentReleaseCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, TechnicalComponentRelease dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<TechnicalComponentRelease> comparatorForSorting() {
      return new NonHierarchicalEntityComparator<TechnicalComponentRelease>();
    }

    @Override
    protected TypeOfBuildingBlock getAssociationType() {
      return TypeOfBuildingBlock.TCR2IEASSOCIATION;
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE;
    }

    @Override
    protected Set<Tcr2IeAssociation> getAssociationsFrom(InfrastructureElement entity) {
      return entity.getTechnicalComponentReleaseAssociations();
    }

    @Override
    protected List<TechnicalComponentRelease> getAvailableElements(Integer id, List<TechnicalComponentRelease> connected) {
      boolean showInactive = UserContext.getCurrentUserContext().isShowInactiveStatus();
      return SpringServiceFactory.getTechnicalComponentReleaseService().filter(connected, showInactive);
    }

    @Override
    protected Set<TechnicalComponentRelease> getConnectedElements(InfrastructureElement source) {
      return source.getTechnicalComponentReleases();
    }

    @Override
    protected void connectAssociation(Tcr2IeAssociation association, InfrastructureElement source, TechnicalComponentRelease target) {
      association.setTechnicalComponentRelease(target);
      association.setInfrastructureElement(source);
      association.connect();
    }

    @Override
    protected TechnicalComponentRelease getTargetFrom(Tcr2IeAssociation association) {
      return association.getTechnicalComponentRelease();
    }

    @Override
    protected Tcr2IeAssociation createNewAssociation() {
      return BuildingBlockFactory.createTcr2IeAssociation();
    }
  }

  private static final class ParentCM extends ManyToOneComponentModelDL<InfrastructureElement, InfrastructureElement> {
    /** Serialization version. */
    private static final long serialVersionUID = -6356181896366177349L;

    public ParentCM(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
      super(componentMode, htmlId, labelKey, nullable);
    }

    @Override
    protected InfrastructureElementService getService() {
      return SpringServiceFactory.getInfrastructureElementService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.INFRASTRUCTUREELEMENT;
    }

    @Override
    protected InfrastructureElement getConnectedElement(InfrastructureElement source) {
      return source.getParent();
    }

    @Override
    protected void setConnectedElement(InfrastructureElement target, InfrastructureElement parent) {
      final boolean parentsEqual = Objects.equal(target.getParent(), parent);

      if (!target.isTopLevelElement() && !parentsEqual) {
        InfrastructureElement reloadParent = getInfrastructureElementService().loadObjectById(parent.getId());
        target.removeParent();
        target.addParent(reloadParent);
      }
    }
  }

  private static final class ChildrenCM extends ManyAssociationListComponentModelDL<InfrastructureElement, InfrastructureElement> {

    /** Serialization version. */
    private static final long serialVersionUID = -983703938152623480L;

    public ChildrenCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys, String[] connectedElementsFields,
        String availableElementsLabel, InfrastructureElement dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected InfrastructureElementService getService() {
      return SpringServiceFactory.getInfrastructureElementService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.INFRASTRUCTUREELEMENT;
    }

    @Override
    protected List<InfrastructureElement> getConnectedElements(InfrastructureElement source) {
      return source.getChildren();
    }

    @Override
    protected boolean isElementRemovable() {
      return getSourceElement().getParent() != null;
    }

    @Override
    protected void setConnectedElements(InfrastructureElement target, List<InfrastructureElement> children) {
      if (!target.getChildren().equals(children)) {
        List<InfrastructureElement> reloadedChildren = getInfrastructureElementService().reload(children);
        InfrastructureElement rootProject = getInfrastructureElementService().getFirstElement();

        target.removeChildren(rootProject);
        target.addChildren(reloadedChildren);
      }
    }
  }

  private static final class BaseComponentsCM extends ManyAssociationSetComponentModelDL<InfrastructureElement, InfrastructureElement> {
    /** Serialization version. */
    private static final long serialVersionUID = -3114123603557040867L;

    public BaseComponentsCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, InfrastructureElement dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<InfrastructureElement> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<InfrastructureElement>();
    }

    @Override
    protected InfrastructureElementService getService() {
      return SpringServiceFactory.getInfrastructureElementService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.INFRASTRUCTUREELEMENT;
    }

    @Override
    protected Set<InfrastructureElement> getConnectedElements(InfrastructureElement source) {
      return source.getBaseComponents();
    }

    @Override
    protected void setConnectedElements(InfrastructureElement target, Set<InfrastructureElement> toConnect) {
      if (!target.getBaseComponents().equals(toConnect)) {
        List<InfrastructureElement> reloadedEntities = getService().reload(toConnect);
        target.removeBaseComponents();
        target.addBaseComponents(reloadedEntities);
      }
    }
  }

  private static final class ParentComponentsCM extends ManyAssociationSetComponentModelDL<InfrastructureElement, InfrastructureElement> {
    /** Serialization version. */
    private static final long serialVersionUID = -184777936617918614L;

    public ParentComponentsCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, InfrastructureElement dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<InfrastructureElement> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<InfrastructureElement>();
    }

    @Override
    protected InfrastructureElementService getService() {
      return SpringServiceFactory.getInfrastructureElementService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.INFRASTRUCTUREELEMENT;
    }

    @Override
    protected Set<InfrastructureElement> getConnectedElements(InfrastructureElement source) {
      return source.getParentComponents();
    }

    @Override
    protected void setConnectedElements(InfrastructureElement target, Set<InfrastructureElement> toConnect) {
      if (!target.getParentComponents().equals(toConnect)) {
        List<InfrastructureElement> reloadedEntities = getService().reload(toConnect);
        target.removeParentComponents();
        target.addParentComponents(reloadedEntities);
      }
    }
  }
}

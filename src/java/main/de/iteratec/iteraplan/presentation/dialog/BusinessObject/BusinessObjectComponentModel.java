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
package de.iteratec.iteraplan.presentation.dialog.BusinessObject;

import static de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory.getBusinessDomainService;
import static de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory.getBusinessFunctionService;
import static de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory.getBusinessObjectService;
import static de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory.getInformationSystemReleaseService;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessObjectTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.BusinessDomainService;
import de.iteratec.iteraplan.businesslogic.service.BusinessFunctionService;
import de.iteratec.iteraplan.businesslogic.service.BusinessObjectService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
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
 * GUI model for the {@code BusinessObject} dialog.
 */
public class BusinessObjectComponentModel extends BuildingBlockComponentModel<BusinessObject> {

  /** Serialization version. */
  private static final long                                                                                         serialVersionUID     = 7591475889584530391L;
  private static final String                                                                                       LABEL_NAME           = "global.name";
  private static final String                                                                                       NAME                 = "name";
  private static final String                                                                                       HIERARCHICAL_NAME    = "hierarchicalName";
  private static final String                                                                                       LABEL_DESCRIPTION    = "global.description";
  private static final String                                                                                       DESCRIPTION          = "description";
  private static final String                                                                                       LABEL_GENERALISATION = "businessObject.generalisation";
  private static final String                                                                                       LABEL_PARENT         = "businessObject.parent";
  private static final String                                                                                       LABEL_CHILDREN       = "businessObject.children";

  private ElementNameComponentModel<BusinessObject>                                                                 nameModel;
  private StringComponentModel<BusinessObject>                                                                      descriptionModel;
  private ManyAssociationSetComponentModel<BusinessObject, BusinessDomain>                                          businessDomainModel;
  private ManyAssociationSetComponentModel<BusinessObject, BusinessFunction>                                        businessFunctionModel;
  private ManyAssociationSetAttributableComponentModel<BusinessObject, InformationSystemRelease, Isr2BoAssociation> informationSystemReleaseModel;
  private JumpToInformationSystemInterfaceCm                                                                        informationSystemInterfaceModel;
  private JumpToInformationSystemReleasesOfIsiCm                                                                    informationSystemReleasesOfIsiModel;
  private ManyToOneComponentModel<BusinessObject, BusinessObject>                                                   parentModel;
  private ManyAssociationListComponentModel<BusinessObject, BusinessObject>                                         childrenModel;
  private ManyToOneComponentModel<BusinessObject, BusinessObject>                                                   specialisationModel;
  private ManyAssociationSetComponentModel<BusinessObject, BusinessObject>                                          generalisationModel;

  private int                                                                                                       subElementCount      = 0;
  private Integer                                                                                                   elementId;

  public BusinessObjectComponentModel(ComponentMode componentMode) {
    super(componentMode);
    this.setHtmlId("bo");

  }

  @Override
  public void configure(BusinessObject target) {
    super.configure(target);
    getNameModel().configure(target);
    getDescriptionModel().configure(target);
    getBusinessDomainModel().configure(target);
    getBusinessFunctionModel().configure(target);
    getParentModel().configure(target);
    getChildrenModel().configure(target);
    getSpecialisationModel().configure(target);
    getGeneralisationModel().configure(target);
    getInformationSystemReleaseModel().configure(target);
  }

  @Override
  public void initializeFrom(BusinessObject source) {
    super.initializeFrom(source);
    getNameModel().initializeFrom(source);
    getDescriptionModel().initializeFrom(source);
    getBusinessDomainModel().initializeFrom(source);
    getBusinessFunctionModel().initializeFrom(source);
    getParentModel().initializeFrom(source);
    getChildrenModel().initializeFrom(source);
    getSpecialisationModel().initializeFrom(source);
    getGeneralisationModel().initializeFrom(source);
    subElementCount = source.getChildren().size();
    this.elementId = source.getId();
    getInformationSystemReleaseModel().initializeFrom(source);
    getInformationSystemInterfaceModel().initializeFrom(source);
    getInformationSystemReleasesOfIsiModel().initializeFrom(source);
  }

  @Override
  public void update() {
    if (getComponentMode() == ComponentMode.READ) {
      getNameModel().update();
    }
    else {
      super.update();
      getNameModel().update();
      getDescriptionModel().update();
      getBusinessDomainModel().update();
      getBusinessFunctionModel().update();
      getParentModel().update();
      getChildrenModel().update();
      getSpecialisationModel().update();
      getGeneralisationModel().update();
      getInformationSystemReleaseModel().update();
      getInformationSystemInterfaceModel().update();
      getInformationSystemReleasesOfIsiModel().update();
    }
  }

  public void validate(Errors errors) {
    // check for non-empty name
    errors.pushNestedPath("nameModel");
    getNameModel().validate(errors);
    errors.popNestedPath();

    errors.pushNestedPath("descriptionModel");
    descriptionModel.validateDescription(errors);
    errors.popNestedPath();

    if (!isValidHierarchyPartName(getNameModel().getName())) {
      errors.rejectValue("nameModel.name", "errors.invalidCharacterInName",
          IteraplanValidationUtils.getLocalizedArgs(getManagedType().getTypeNamePresentationKey()), "Invalid Characters in Name");
    }
  }

  public final ManyAssociationListComponentModel<BusinessObject, BusinessObject> getChildrenModel() {
    if (childrenModel == null) {
      childrenModel = new ChildrenCM(getComponentMode(), "children", LABEL_CHILDREN, new String[] { LABEL_NAME, LABEL_DESCRIPTION }, new String[] {
          NAME, DESCRIPTION }, HIERARCHICAL_NAME, new BusinessObject());
    }
    return childrenModel;
  }

  public final ElementNameComponentModel<BusinessObject> getNameModel() {
    if (nameModel == null) {
      nameModel = new NameCM(getComponentMode(), NAME, LABEL_NAME);
    }
    return nameModel;
  }

  public final StringComponentModel<BusinessObject> getDescriptionModel() {
    if (descriptionModel == null) {
      descriptionModel = new DescriptionCM(getComponentMode(), "desc", LABEL_DESCRIPTION);
    }
    return descriptionModel;
  }

  public final ManyToOneComponentModel<BusinessObject, BusinessObject> getParentModel() {
    if (parentModel == null) {
      parentModel = new ParentCM(getComponentMode(), "parent", LABEL_PARENT, false);
    }
    return parentModel;
  }

  public final ManyToOneComponentModel<BusinessObject, BusinessObject> getSpecialisationModel() {
    if (specialisationModel == null) {
      specialisationModel = new GeneralisationCM(getComponentMode(), "specialisation", LABEL_GENERALISATION, true);
    }
    return specialisationModel;
  }

  public final ManyAssociationSetComponentModel<BusinessObject, BusinessObject> getGeneralisationModel() {
    if (generalisationModel == null) {
      generalisationModel = new SpecialisationCM(getComponentMode(), "generalisation", "businessObject.specialisations", new String[] { LABEL_NAME,
          LABEL_DESCRIPTION }, new String[] { NAME, DESCRIPTION }, HIERARCHICAL_NAME, new BusinessObject());
    }
    return generalisationModel;
  }

  public int getSubElementCount() {
    return subElementCount;
  }

  public Integer getElementId() {
    return elementId;
  }

  public final ManyAssociationSetAttributableComponentModel<BusinessObject, InformationSystemRelease, Isr2BoAssociation> getInformationSystemReleaseModel() {
    if (informationSystemReleaseModel == null) {
      informationSystemReleaseModel = new InformationSystemReleaseCM(getComponentMode(), "isr", "businessObject.to.informationSystemReleases",
          new String[] { LABEL_NAME, LABEL_DESCRIPTION }, new String[] { HIERARCHICAL_NAME, DESCRIPTION }, HIERARCHICAL_NAME,
          new InformationSystemRelease());
    }

    return informationSystemReleaseModel;
  }

  public final ManyAssociationSetComponentModel<BusinessObject, BusinessDomain> getBusinessDomainModel() {
    if (businessDomainModel == null) {
      businessDomainModel = new BusinessDomainCM(getComponentMode(), "businessDomains", "businessObject.to.businessDomains", new String[] {
          LABEL_NAME, LABEL_DESCRIPTION }, new String[] { NAME, DESCRIPTION }, HIERARCHICAL_NAME, new BusinessDomain());
    }
    return businessDomainModel;
  }

  public final ManyAssociationSetComponentModel<BusinessObject, BusinessFunction> getBusinessFunctionModel() {
    if (businessFunctionModel == null) {
      businessFunctionModel = new BusinessFunctionCM(getComponentMode(), "businessFunctions", "businessObject.to.businessFunctions", new String[] {
          LABEL_NAME, LABEL_DESCRIPTION }, new String[] { NAME, DESCRIPTION }, HIERARCHICAL_NAME, new BusinessFunction());
    }
    return businessFunctionModel;
  }

  public final JumpToInformationSystemInterfaceCm getInformationSystemInterfaceModel() {
    if (informationSystemInterfaceModel == null) {
      informationSystemInterfaceModel = new JumpToInformationSystemInterfaceCm(getComponentMode(), "isi");
    }
    return informationSystemInterfaceModel;
  }

  public final JumpToInformationSystemReleasesOfIsiCm getInformationSystemReleasesOfIsiModel() {
    if (informationSystemReleasesOfIsiModel == null) {
      informationSystemReleasesOfIsiModel = new JumpToInformationSystemReleasesOfIsiCm(getComponentMode(), "isrOfIsi");
    }
    return informationSystemReleasesOfIsiModel;
  }

  @Override
  public Type<? extends BuildingBlock> getManagedType() {
    return BusinessObjectTypeQu.getInstance();
  }

  public void sortEverything() {
    childrenModel.sort();
  }

  private static final class ChildrenCM extends ManyAssociationListComponentModelDL<BusinessObject, BusinessObject> {
    /** Serialization version. */
    private static final long serialVersionUID = 6467968796023730903L;

    public ChildrenCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys, String[] connectedElementsFields,
        String availableElementsLabel, BusinessObject dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected BusinessObjectService getService() {
      return SpringServiceFactory.getBusinessObjectService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.BUSINESSOBJECT;
    }

    @Override
    protected List<BusinessObject> getConnectedElements(BusinessObject source) {
      return source.getChildren();
    }

    @Override
    protected boolean isElementRemovable() {
      return getSourceElement().getParent() != null;
    }

    @Override
    protected void setConnectedElements(BusinessObject target, List<BusinessObject> children) {
      if (!target.getChildren().equals(children)) {
        List<BusinessObject> reloadedChildren = getBusinessObjectService().reload(children);
        BusinessObject rootProject = getBusinessObjectService().getFirstElement();

        target.removeChildren(rootProject);
        target.addChildren(reloadedChildren);
      }
    }
  }

  private static final class NameCM extends ElementNameComponentModel<BusinessObject> {
    /** Serialization version. */
    private static final long serialVersionUID = -2089715734196239615L;

    public NameCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(BusinessObject source) {
      if (BusinessObject.TOP_LEVEL_NAME.equals(source.getName())) {
        setVirtualElementSelected(true);
      }

      return source.getName();
    }

    @Override
    public void setStringForElement(BusinessObject target, String stringToSet) {
      target.setName(stringToSet);
    }
  }

  private static final class DescriptionCM extends StringComponentModel<BusinessObject> {
    /** Serialization version. */
    private static final long serialVersionUID = 5461266215956013571L;

    public DescriptionCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(BusinessObject source) {
      return source.getDescription();
    }

    @Override
    public void setStringForElement(BusinessObject target, String stringToSet) {
      target.setDescription(stringToSet);
    }
  }

  private static final class ParentCM extends ManyToOneComponentModelDL<BusinessObject, BusinessObject> {
    /** Serialization version. */
    private static final long serialVersionUID = 5498683737901628215L;

    public ParentCM(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
      super(componentMode, htmlId, labelKey, nullable);
    }

    @Override
    protected BusinessObjectService getService() {
      return SpringServiceFactory.getBusinessObjectService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.BUSINESSOBJECT;
    }

    @Override
    protected BusinessObject getConnectedElement(BusinessObject source) {
      return source.getParent();
    }

    @Override
    protected void setConnectedElement(BusinessObject target, BusinessObject parent) {
      final boolean parentsEqual = Objects.equal(target.getParent(), parent);
      if (!target.isTopLevelElement() && !parentsEqual) {
        BusinessObject reloadParent = getBusinessObjectService().loadObjectById(parent.getId());
        target.removeParent();
        target.addParent(reloadParent);
      }
    }
  }

  private static final class GeneralisationCM extends ManyToOneComponentModelDL<BusinessObject, BusinessObject> {
    /** Serialization version. */
    private static final long serialVersionUID = 8483523586194866457L;

    public GeneralisationCM(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
      super(componentMode, htmlId, labelKey, nullable);
    }

    @Override
    protected BusinessObjectService getService() {
      return SpringServiceFactory.getBusinessObjectService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.BUSINESSOBJECT;
    }

    @Override
    protected BusinessObject getConnectedElement(BusinessObject source) {
      return source.getGeneralisation();
    }

    @Override
    protected void setConnectedElement(BusinessObject target, BusinessObject element) {
      if (!Objects.equal(target.getGeneralisation(), element)) {
        target.removeGeneralisation();
        if (element != null) {
          BusinessObject reloadedElement = getBusinessObjectService().loadObjectById(element.getId());
          target.addGeneralisation(reloadedElement);
        }
      }
    }
  }

  private static final class SpecialisationCM extends ManyAssociationSetComponentModelDL<BusinessObject, BusinessObject> {

    /** Serialization version. */
    private static final long serialVersionUID = 3192714464367640408L;

    public SpecialisationCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, BusinessObject dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<BusinessObject> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<BusinessObject>();
    }

    @Override
    protected BusinessObjectService getService() {
      return SpringServiceFactory.getBusinessObjectService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.BUSINESSOBJECT;
    }

    @Override
    protected Set<BusinessObject> getConnectedElements(BusinessObject source) {
      return source.getSpecialisations();
    }

    @Override
    protected void setConnectedElements(BusinessObject target, Set<BusinessObject> toConnect) {
      if (!target.getSpecialisations().equals(toConnect)) {
        List<BusinessObject> reloadedEntities = getBusinessObjectService().reload(toConnect);
        target.removeSpecialisationRelations();
        target.addSpecialisations(Sets.newHashSet(reloadedEntities));
      }
    }
  }

  private static final class InformationSystemReleaseCM extends
      ManyAssociationSetAttributableComponentModel<BusinessObject, InformationSystemRelease, Isr2BoAssociation> {
    /** Serialization version. */
    private static final long serialVersionUID = -1368193285185994864L;

    public InformationSystemReleaseCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, InformationSystemRelease dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<InformationSystemRelease> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<InformationSystemRelease>();
    }

    @Override
    protected TypeOfBuildingBlock getAssociationType() {
      return TypeOfBuildingBlock.ISR2BOASSOCIATION;
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE;
    }

    @Override
    protected Set<Isr2BoAssociation> getAssociationsFrom(BusinessObject entity) {
      return entity.getInformationSystemReleaseAssociations();
    }

    @Override
    protected List<InformationSystemRelease> getAvailableElements(Integer id, List<InformationSystemRelease> connected) {
      boolean showInactive = UserContext.getCurrentUserContext().isShowInactiveStatus();
      return getInformationSystemReleaseService().getInformationSystemsFiltered(connected, showInactive);
    }

    @Override
    protected Set<InformationSystemRelease> getConnectedElements(BusinessObject source) {
      return source.getInformationSystemReleases();
    }

    @Override
    protected void connectAssociation(Isr2BoAssociation association, BusinessObject source, InformationSystemRelease target) {
      association.setInformationSystemRelease(target);
      association.setBusinessObject(source);
      association.connect();
    }

    @Override
    protected InformationSystemRelease getTargetFrom(Isr2BoAssociation association) {
      return association.getInformationSystemRelease();
    }

    @Override
    protected Isr2BoAssociation createNewAssociation() {
      return BuildingBlockFactory.createIsr2BoAssociation();
    }

  }

  private static final class BusinessDomainCM extends ManyAssociationSetComponentModelDL<BusinessObject, BusinessDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = 564664746200815578L;

    public BusinessDomainCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, BusinessDomain dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<BusinessDomain> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<BusinessDomain>();
    }

    @Override
    protected BusinessDomainService getService() {
      return SpringServiceFactory.getBusinessDomainService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.BUSINESSDOMAIN;
    }

    @Override
    protected Set<BusinessDomain> getConnectedElements(BusinessObject source) {
      return source.getBusinessDomains();
    }

    @Override
    protected void setConnectedElements(BusinessObject target, Set<BusinessDomain> toConnect) {
      if (!target.getBusinessDomains().equals(toConnect)) {
        List<BusinessDomain> reloadedIsrs = getBusinessDomainService().reload(toConnect);
        target.removeBusinessDomainRelations();
        target.addBusinessDomains(reloadedIsrs);
      }
    }
  }

  private static final class BusinessFunctionCM extends ManyAssociationSetComponentModelDL<BusinessObject, BusinessFunction> {

    /** Serialization version. */
    private static final long serialVersionUID = -5480658178067723694L;

    public BusinessFunctionCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, BusinessFunction dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<BusinessFunction> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<BusinessFunction>();
    }

    @Override
    protected BusinessFunctionService getService() {
      return SpringServiceFactory.getBusinessFunctionService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.BUSINESSFUNCTION;
    }

    @Override
    protected Set<BusinessFunction> getConnectedElements(BusinessObject source) {
      return source.getBusinessFunctions();
    }

    @Override
    protected void setConnectedElements(BusinessObject target, Set<BusinessFunction> toConnect) {
      if (!target.getBusinessFunctions().equals(toConnect)) {
        List<BusinessFunction> reloadedIsrs = getBusinessFunctionService().reload(toConnect);
        target.removeBusinessFunctionRelations();
        target.addBusinessFunctions(reloadedIsrs);
      }
    }
  }
}
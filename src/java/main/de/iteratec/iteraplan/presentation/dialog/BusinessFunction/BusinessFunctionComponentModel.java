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
package de.iteratec.iteraplan.presentation.dialog.BusinessFunction;

import static de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory.getBusinessDomainService;
import static de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory.getBusinessFunctionService;
import static de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory.getBusinessObjectService;
import static de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory.getInformationSystemReleaseService;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.base.Objects;

import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessFunctionQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.BusinessDomainService;
import de.iteratec.iteraplan.businesslogic.service.BusinessFunctionService;
import de.iteratec.iteraplan.businesslogic.service.BusinessObjectService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
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


public class BusinessFunctionComponentModel extends BuildingBlockComponentModel<BusinessFunction> {

  /** Serialization version. */
  private static final long                                                                   serialVersionUID  = -5097154299316285558L;
  protected static final String                                                               NAME_LABEL        = "global.name";
  protected static final String                                                               NAME_FIELD        = "name";
  private static final String                                                                 DESCRIPTION_LABEL = "global.description";
  private static final String                                                                 DESCRIPTION_FIELD = "description";
  private static final String                                                                INFORMATION_SYSTEM_RELEASE_MODEL = "businessFunction.to.informationSystemRelease";

  private final ElementNameComponentModel<BusinessFunction>                                   nameModel;
  private final StringComponentModel<BusinessFunction>                                        descriptionModel;
  private final ManyToOneComponentModel<BusinessFunction, BusinessFunction>                   parentModel;
  private final ManyAssociationListComponentModel<BusinessFunction, BusinessFunction>         childrenModel;
  private final ManyAssociationSetComponentModel<BusinessFunction, BusinessDomain>            businessDomainModel;
  private final ManyAssociationSetComponentModel<BusinessFunction, BusinessObject>            businessObjectModel;
  private final ManyAssociationSetComponentModel<BusinessFunction, InformationSystemRelease> informationSystemReleaseModel;

  private int                                                                                 subElementCount   = 0;
  private Integer                                                                             elementId;

  public BusinessFunctionComponentModel() {
    this(ComponentMode.READ);
  }

  public BusinessFunctionComponentModel(ComponentMode componentMode) {
    super(componentMode);
    this.setHtmlId("businessfunction");

    nameModel = new NameCM(componentMode, NAME_FIELD, NAME_LABEL);
    descriptionModel = new DescriptionCM(componentMode, DESCRIPTION_FIELD, DESCRIPTION_LABEL);
    parentModel = new ParentCM(componentMode, "parent", "businessFunction.parent", false);
    childrenModel = new ChildrenCM(componentMode, "children", "businessFunction.children", new String[] { NAME_LABEL, DESCRIPTION_LABEL },
        new String[] { NAME_FIELD, DESCRIPTION_FIELD }, "hierarchicalName", new BusinessFunction());

    businessDomainModel = new BusinessDomainCM(componentMode, "businessfunctions", "businessFunction.to.businessDomains", new String[] { NAME_LABEL,
        DESCRIPTION_LABEL }, new String[] { NAME_FIELD, DESCRIPTION_FIELD }, "hierarchicalName", new BusinessDomain());

    businessObjectModel = new BusinessObjectCM(componentMode, "bo", "businessDomain.to.businessObjects",
        new String[] { NAME_LABEL, DESCRIPTION_LABEL }, new String[] { NAME_FIELD, DESCRIPTION_FIELD }, "hierarchicalName", new BusinessObject());

    informationSystemReleaseModel = new InformationSystemReleaseCM(componentMode, "isr", INFORMATION_SYSTEM_RELEASE_MODEL, new String[] { NAME_LABEL,
        DESCRIPTION_LABEL }, new String[] { NAME_FIELD, DESCRIPTION_FIELD }, "hierarchicalName", new InformationSystemRelease());
  }

  @Override
  public void initializeFrom(BusinessFunction source) {
    super.initializeFrom(source);
    nameModel.initializeFrom(source);
    descriptionModel.initializeFrom(source);
    parentModel.initializeFrom(source);
    childrenModel.initializeFrom(source);
    businessObjectModel.initializeFrom(source);
    businessDomainModel.initializeFrom(source);
    informationSystemReleaseModel.initializeFrom(source);

    subElementCount = source.getChildrenAsSet().size();
    this.elementId = source.getId();
  }

  @Override
  public void update() {
    if (getComponentMode() != ComponentMode.READ) {
      super.update();
      nameModel.update();
      descriptionModel.update();
      parentModel.update();
      childrenModel.update();
      businessObjectModel.update();
      businessDomainModel.update();
      informationSystemReleaseModel.update();
    }
  }

  @Override
  public void configure(BusinessFunction target) {
    super.configure(target);
    nameModel.configure(target);
    descriptionModel.configure(target);
    parentModel.configure(target);
    childrenModel.configure(target);
    businessObjectModel.configure(target);
    businessDomainModel.configure(target);
    informationSystemReleaseModel.configure(target);
  }

  public StringComponentModel<BusinessFunction> getDescriptionModel() {
    return descriptionModel;
  }

  public StringComponentModel<BusinessFunction> getNameModel() {
    return nameModel;
  }

  public ManyToOneComponentModel<BusinessFunction, BusinessFunction> getParentModel() {
    return parentModel;
  }

  public ManyAssociationListComponentModel<BusinessFunction, BusinessFunction> getChildrenModel() {
    return childrenModel;
  }

  public ManyAssociationSetComponentModel<BusinessFunction, BusinessDomain> getBusinessDomainModel() {
    return businessDomainModel;
  }

  public ManyAssociationSetComponentModel<BusinessFunction, BusinessObject> getBusinessObjectModel() {
    return businessObjectModel;
  }

  public ManyAssociationSetComponentModel<BusinessFunction, InformationSystemRelease> getInformationSystemReleaseModel() {
    return informationSystemReleaseModel;
  }

  public int getSubElementCount() {
    return subElementCount;
  }

  public Integer getElementId() {
    return elementId;
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
    return BusinessFunctionQueryType.getInstance();
  }

  public void sortEverything() {
    childrenModel.sort();
  }

  private static final class NameCM extends ElementNameComponentModel<BusinessFunction> {
    /** Serialization version. */
    private static final long serialVersionUID = -6636705090058480880L;

    public NameCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(BusinessFunction target, String stringToSet) {
      target.setName(stringToSet);
    }

    @Override
    public String getStringFromElement(BusinessFunction source) {
      if (BusinessDomain.TOP_LEVEL_NAME.equals(source.getName())) {
        setVirtualElementSelected(true);
      }

      return source.getName();
    }
  }

  private static final class DescriptionCM extends StringComponentModel<BusinessFunction> {
    /** Serialization version. */
    private static final long serialVersionUID = -3831529434103427371L;

    public DescriptionCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(BusinessFunction target, String stringToSet) {
      target.setDescription(stringToSet);
    }

    @Override
    public String getStringFromElement(BusinessFunction source) {
      return source.getDescription();
    }
  }

  private static final class ParentCM extends ManyToOneComponentModelDL<BusinessFunction, BusinessFunction> {
    /** Serialization version. */
    private static final long serialVersionUID = 3938146497260642063L;

    public ParentCM(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
      super(componentMode, htmlId, labelKey, nullable);
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
    protected BusinessFunction getConnectedElement(BusinessFunction source) {
      return source.getParent();
    }

    @Override
    protected void setConnectedElement(BusinessFunction target, BusinessFunction parent) {
      final boolean parentsEqual = Objects.equal(target.getParent(), parent);

      if (!target.isTopLevelElement() && !parentsEqual) {
        BusinessFunction reloadParent = getBusinessFunctionService().loadObjectById(parent.getId());
        target.removeParent();
        target.addParent(reloadParent);
      }
    }
  }

  private static final class ChildrenCM extends ManyAssociationListComponentModelDL<BusinessFunction, BusinessFunction> {
    /** Serialization version. */
    private static final long serialVersionUID = -6340514758796703636L;

    public ChildrenCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys, String[] connectedElementsFields,
                      String availableElementsLabel, BusinessFunction dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
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
    protected List<BusinessFunction> getConnectedElements(BusinessFunction source) {
      return source.getChildren();
    }

    @Override
    protected void setConnectedElements(BusinessFunction target, List<BusinessFunction> children) {
      if (!target.getChildren().equals(children)) {
        List<BusinessFunction> reloadedChildren = getBusinessFunctionService().reload(children);
        BusinessFunction rootBf = getBusinessFunctionService().getFirstElement();

        target.removeChildren(rootBf);
        target.addChildren(reloadedChildren);
      }
    }

    @Override
    protected boolean isElementRemovable() {
      return getSourceElement().getParent() != null;
    }
  }

  private static final class BusinessDomainCM extends ManyAssociationSetComponentModelDL<BusinessFunction, BusinessDomain> {

    /** Serialization version. */
    private static final long serialVersionUID = 516167275943939880L;

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
    protected Set<BusinessDomain> getConnectedElements(BusinessFunction source) {
      return source.getBusinessDomains();
    }

    @Override
    protected void setConnectedElements(BusinessFunction target, Set<BusinessDomain> toConnect) {
      if (!target.getBusinessDomains().equals(toConnect)) {
        List<BusinessDomain> reloadedEntities = getBusinessDomainService().reload(toConnect);
        target.removeBusinessDomains();
        target.addBusinessDomains(reloadedEntities);
      }
    }
  }

  private static final class BusinessObjectCM extends ManyAssociationSetComponentModelDL<BusinessFunction, BusinessObject> {

    /** Serialization version. */
    private static final long serialVersionUID = 369462324929395124L;

    public BusinessObjectCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
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
    protected Set<BusinessObject> getConnectedElements(BusinessFunction source) {
      return source.getBusinessObjects();
    }

    @Override
    protected void setConnectedElements(BusinessFunction target, Set<BusinessObject> toConnect) {
      if (!target.getBusinessObjects().equals(toConnect)) {
        List<BusinessObject> reloadedEntities = getBusinessObjectService().reload(toConnect);
        target.removeBusinessObjects();
        target.addBusinessObjects(reloadedEntities);
      }
    }
  }

  private final static class InformationSystemReleaseCM extends ManyAssociationSetComponentModelDL<BusinessFunction, InformationSystemRelease> {

    private static final long serialVersionUID = 5446846056713898581L;

    public InformationSystemReleaseCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
                                      String[] connectedElementsFields, String availableElementsLabel, InformationSystemRelease dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    /**{@inheritDoc}**/
    @Override
    protected InformationSystemReleaseService getService() {
      return SpringServiceFactory.getInformationSystemReleaseService();
    }

    /**{@inheritDoc}**/
    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE;
    }

    /**{@inheritDoc}**/
    @Override
    protected Set<InformationSystemRelease> getConnectedElements(BusinessFunction source) {
      return source.getInformationSystems();
    }

    /**{@inheritDoc}**/
    @Override
    protected void setConnectedElements(BusinessFunction target, Set<InformationSystemRelease> toConnect) {
      if (!target.getInformationSystems().equals(toConnect)) {
        List<InformationSystemRelease> reloadedEntities = getInformationSystemReleaseService().reload(toConnect);
        target.removeInformationSystems();
        target.addInformationSystems(reloadedEntities);
      }

    }



  }
}

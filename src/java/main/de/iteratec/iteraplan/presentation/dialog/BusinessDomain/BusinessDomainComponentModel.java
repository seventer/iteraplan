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
package de.iteratec.iteraplan.presentation.dialog.BusinessDomain;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.base.Objects;

import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessDomainQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.BusinessDomainService;
import de.iteratec.iteraplan.businesslogic.service.BusinessFunctionService;
import de.iteratec.iteraplan.businesslogic.service.BusinessObjectService;
import de.iteratec.iteraplan.businesslogic.service.BusinessProcessService;
import de.iteratec.iteraplan.businesslogic.service.BusinessUnitService;
import de.iteratec.iteraplan.businesslogic.service.ProductService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.Product;
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


public class BusinessDomainComponentModel extends BuildingBlockComponentModel<BusinessDomain> {

  /** Serialization version. */
  private static final long                                                  serialVersionUID  = 8760669814930078594L;
  protected static final String                                              NAME_LABEL        = "global.name";
  protected static final String                                              NAME              = "name";
  private static final String                                                HIERARCHICAL_NAME = "hierarchicalName";
  private static final String                                                LABEL_DESCRIPTION = "global.description";
  private static final String                                                DESCRIPTION       = "description";

  private ElementNameComponentModel<BusinessDomain>                          nameModel;
  private StringComponentModel<BusinessDomain>                               descriptionModel;
  private ManyAssociationSetComponentModel<BusinessDomain, BusinessFunction> businessFunctionModel;
  private ManyAssociationSetComponentModel<BusinessDomain, BusinessProcess>  businessProcessModel;
  private ManyAssociationSetComponentModel<BusinessDomain, BusinessObject>   businessObjectModel;
  private ManyAssociationSetComponentModel<BusinessDomain, Product>          productModel;
  private ManyAssociationSetComponentModel<BusinessDomain, BusinessUnit>     businessUnitModel;
  private ManyToOneComponentModel<BusinessDomain, BusinessDomain>            parentModel;
  private ManyAssociationListComponentModel<BusinessDomain, BusinessDomain>  childrenModel;

  private int                                                                subElementCount   = 0;
  private Integer                                                            elementId;

  public BusinessDomainComponentModel(ComponentMode componentMode) {
    super(componentMode);
    this.setHtmlId("bd");
  }

  public final ManyAssociationSetComponentModel<BusinessDomain, BusinessFunction> getBusinessFunctionModel() {
    if (businessFunctionModel == null) {
      businessFunctionModel = new BusinessFunctionCM(getComponentMode(), "bf", "businessDomain.to.businessFunctions", new String[] { NAME_LABEL,
          LABEL_DESCRIPTION }, new String[] { NAME, DESCRIPTION }, HIERARCHICAL_NAME, new BusinessFunction());
    }

    return businessFunctionModel;
  }

  public final ManyAssociationSetComponentModel<BusinessDomain, BusinessProcess> getBusinessProcessModel() {
    if (businessProcessModel == null) {
      businessProcessModel = new BusinessProcessCM(getComponentMode(), "bp", "businessDomain.to.businessProcesses", new String[] { NAME_LABEL,
          LABEL_DESCRIPTION }, new String[] { NAME, DESCRIPTION }, HIERARCHICAL_NAME, new BusinessProcess());
    }

    return businessProcessModel;
  }

  public final ManyAssociationSetComponentModel<BusinessDomain, BusinessObject> getBusinessObjectModel() {
    if (businessObjectModel == null) {
      businessObjectModel = new BusinessObjectCM(getComponentMode(), "bo", "businessDomain.to.businessObjects", new String[] { NAME_LABEL,
          LABEL_DESCRIPTION }, new String[] { NAME, DESCRIPTION }, HIERARCHICAL_NAME, new BusinessObject());

    }

    return businessObjectModel;
  }

  public final ManyAssociationSetComponentModel<BusinessDomain, Product> getProductModel() {
    if (productModel == null) {
      productModel = new ProductCM(getComponentMode(), "prod", "businessDomain.to.products", new String[] { NAME_LABEL, LABEL_DESCRIPTION },
          new String[] { NAME, DESCRIPTION }, HIERARCHICAL_NAME, new Product());
    }

    return productModel;
  }

  public final ManyAssociationSetComponentModel<BusinessDomain, BusinessUnit> getBusinessUnitModel() {
    if (businessUnitModel == null) {
      businessUnitModel = new BusinessUnitCM(getComponentMode(), "bu", "businessDomain.to.businessUnits", new String[] { NAME_LABEL,
          LABEL_DESCRIPTION }, new String[] { NAME, DESCRIPTION }, HIERARCHICAL_NAME, new BusinessUnit());
    }

    return businessUnitModel;
  }

  public final ManyAssociationListComponentModel<BusinessDomain, BusinessDomain> getChildrenModel() {
    if (childrenModel == null) {
      childrenModel = new ChildrenCM(getComponentMode(), "children", "businessDomain.children", new String[] { NAME_LABEL, LABEL_DESCRIPTION },
          new String[] { NAME, DESCRIPTION }, HIERARCHICAL_NAME, new BusinessDomain());
    }

    return childrenModel;
  }

  public final StringComponentModel<BusinessDomain> getDescriptionModel() {
    if (descriptionModel == null) {
      descriptionModel = new DescriptionCM(getComponentMode(), "desc", LABEL_DESCRIPTION);

    }

    return descriptionModel;
  }

  public final StringComponentModel<BusinessDomain> getNameModel() {
    if (nameModel == null) {
      nameModel = new NameCM(getComponentMode(), NAME, NAME_LABEL);
    }

    return nameModel;
  }

  public final ManyToOneComponentModel<BusinessDomain, BusinessDomain> getParentModel() {
    if (parentModel == null) {
      parentModel = new ParentCM(getComponentMode(), "parent", "businessDomain.parent", false);
    }

    return parentModel;
  }

  public int getSubElementCount() {
    return subElementCount;
  }

  public Integer getElementId() {
    return elementId;
  }

  @Override
  public void initializeFrom(BusinessDomain source) {
    super.initializeFrom(source);
    getNameModel().initializeFrom(source);
    getDescriptionModel().initializeFrom(source);
    getBusinessFunctionModel().initializeFrom(source);
    getBusinessProcessModel().initializeFrom(source);
    getBusinessObjectModel().initializeFrom(source);
    getProductModel().initializeFrom(source);
    getBusinessUnitModel().initializeFrom(source);
    getParentModel().initializeFrom(source);
    getChildrenModel().initializeFrom(source);

    subElementCount = source.getChildren().size();
    this.elementId = source.getId();
  }

  @Override
  public void update() {
    if (getComponentMode() != ComponentMode.READ) {
      super.update();
      getNameModel().update();
      getDescriptionModel().update();
      getBusinessFunctionModel().update();
      getBusinessProcessModel().update();
      getBusinessObjectModel().update();
      getProductModel().update();
      getBusinessUnitModel().update();
      getParentModel().update();
      getChildrenModel().update();
    }
  }

  @Override
  public void configure(BusinessDomain target) {
    super.configure(target);
    getNameModel().configure(target);
    getDescriptionModel().configure(target);
    getBusinessFunctionModel().configure(target);
    getBusinessProcessModel().configure(target);
    getBusinessObjectModel().configure(target);
    getProductModel().configure(target);
    getBusinessUnitModel().configure(target);
    getParentModel().configure(target);
    getChildrenModel().configure(target);
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
    return BusinessDomainQueryType.getInstance();
  }

  public void sortEverything() {
    childrenModel.sort();
  }

  private static final class BusinessFunctionCM extends ManyAssociationSetComponentModelDL<BusinessDomain, BusinessFunction> {

    /** Serialization version. */
    private static final long serialVersionUID = -430590895342538628L;

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
    protected Set<BusinessFunction> getConnectedElements(BusinessDomain source) {
      return source.getBusinessFunctions();
    }

    @Override
    protected void setConnectedElements(BusinessDomain target, Set<BusinessFunction> toConnect) {
      if (!target.getBusinessFunctions().equals(toConnect)) {
        List<BusinessFunction> reloadedBfs = SpringServiceFactory.getBusinessFunctionService().reload(toConnect);
        target.removeBusinessFunctions();
        target.addBusinessFunctions(reloadedBfs);
      }
    }
  }

  private static final class BusinessProcessCM extends ManyAssociationSetComponentModelDL<BusinessDomain, BusinessProcess> {

    /** Serialization version. */
    private static final long serialVersionUID = -6114103362366111910L;

    public BusinessProcessCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, BusinessProcess dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<BusinessProcess> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<BusinessProcess>();
    }

    @Override
    protected BusinessProcessService getService() {
      return SpringServiceFactory.getBusinessProcessService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.BUSINESSPROCESS;
    }

    @Override
    protected Set<BusinessProcess> getConnectedElements(BusinessDomain source) {
      return source.getBusinessProcesses();
    }

    @Override
    protected void setConnectedElements(BusinessDomain target, Set<BusinessProcess> toConnect) {
      if (!target.getBusinessProcesses().equals(toConnect)) {
        List<BusinessProcess> reloadedBps = SpringServiceFactory.getBusinessProcessService().reload(toConnect);
        target.removeBusinessProcesses();
        target.addBusinessProcesses(reloadedBps);
      }
    }
  }

  private static final class BusinessObjectCM extends ManyAssociationSetComponentModelDL<BusinessDomain, BusinessObject> {

    /** Serialization version. */
    private static final long serialVersionUID = -4964123653280395207L;

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
    protected Set<BusinessObject> getConnectedElements(BusinessDomain source) {
      return source.getBusinessObjects();
    }

    @Override
    protected void setConnectedElements(BusinessDomain target, Set<BusinessObject> toConnect) {
      if (!target.getBusinessObjects().equals(toConnect)) {
        List<BusinessObject> reloadedBps = SpringServiceFactory.getBusinessObjectService().reload(toConnect);
        target.removeBusinessObjects();
        target.addBusinessObjects(reloadedBps);
      }
    }
  }

  private static final class ProductCM extends ManyAssociationSetComponentModelDL<BusinessDomain, Product> {

    /** Serialization version. */
    private static final long serialVersionUID = 720050321110217949L;

    public ProductCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys, String[] connectedElementsFields,
        String availableElementsLabel, Product dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<Product> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<Product>();
    }

    @Override
    protected ProductService getService() {
      return SpringServiceFactory.getProductService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.PRODUCT;
    }

    @Override
    protected Set<Product> getConnectedElements(BusinessDomain source) {
      return source.getProducts();
    }

    @Override
    protected void setConnectedElements(BusinessDomain target, Set<Product> toConnect) {
      if (!target.getProducts().equals(toConnect)) {
        List<Product> reloadedBps = SpringServiceFactory.getProductService().reload(toConnect);
        target.removeProducts();
        target.addProducts(reloadedBps);
      }
    }
  }

  private static final class BusinessUnitCM extends ManyAssociationSetComponentModelDL<BusinessDomain, BusinessUnit> {

    /** Serialization version. */
    private static final long serialVersionUID = -5653125887234563079L;

    public BusinessUnitCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, BusinessUnit dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<BusinessUnit> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<BusinessUnit>();
    }

    @Override
    protected BusinessUnitService getService() {
      return SpringServiceFactory.getBusinessUnitService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.BUSINESSUNIT;
    }

    @Override
    protected Set<BusinessUnit> getConnectedElements(BusinessDomain source) {
      return source.getBusinessUnits();
    }

    @Override
    protected void setConnectedElements(BusinessDomain target, Set<BusinessUnit> toConnect) {
      if (!target.getBusinessUnits().equals(toConnect)) {
        List<BusinessUnit> reloadedBps = SpringServiceFactory.getBusinessUnitService().reload(toConnect);
        target.removeBusinessUnits();
        target.addBusinessUnits(reloadedBps);
      }
    }
  }

  private static final class ChildrenCM extends ManyAssociationListComponentModelDL<BusinessDomain, BusinessDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = -8845562335711247151L;

    public ChildrenCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys, String[] connectedElementsFields,
        String availableElementsLabel, BusinessDomain dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
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
    protected List<BusinessDomain> getConnectedElements(BusinessDomain source) {
      return source.getChildren();
    }

    @Override
    protected void setConnectedElements(BusinessDomain target, List<BusinessDomain> children) {
      if (!target.getChildren().equals(children)) {
        List<BusinessDomain> reloadedChildren = SpringServiceFactory.getBusinessDomainService().reload(children);
        BusinessDomain rootProject = SpringServiceFactory.getBusinessDomainService().getFirstElement();

        target.removeChildren(rootProject);
        target.addChildren(reloadedChildren);
      }
    }

    @Override
    protected boolean isElementRemovable() {
      return getSourceElement().getParent() != null;
    }
  }

  private static final class DescriptionCM extends StringComponentModel<BusinessDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = -8631339445104532033L;

    public DescriptionCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(BusinessDomain source) {
      return source.getDescription();
    }

    @Override
    public void setStringForElement(BusinessDomain target, String stringToSet) {
      target.setDescription(stringToSet);
    }
  }

  private static final class NameCM extends ElementNameComponentModel<BusinessDomain> {

    /** Serialization version. */
    private static final long serialVersionUID = -5196777154921633962L;

    public NameCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(BusinessDomain source) {
      if (BusinessDomain.TOP_LEVEL_NAME.equals(source.getName())) {
        setVirtualElementSelected(true);
      }
      return source.getName();
    }

    @Override
    public void setStringForElement(BusinessDomain target, String stringToSet) {
      target.setName(stringToSet);
    }
  }

  private static final class ParentCM extends ManyToOneComponentModelDL<BusinessDomain, BusinessDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = -4466138787649080961L;

    public ParentCM(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
      super(componentMode, htmlId, labelKey, nullable);
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
    protected BusinessDomain getConnectedElement(BusinessDomain source) {
      return source.getParent();
    }

    @Override
    protected void setConnectedElement(BusinessDomain target, BusinessDomain parent) {
      final boolean parentsEqual = Objects.equal(target.getParent(), parent);

      if (!target.isTopLevelElement() && !parentsEqual) {
        BusinessDomain reloadParent = SpringServiceFactory.getBusinessDomainService().loadObjectById(parent.getId());
        target.removeParent();
        target.addParent(reloadParent);
      }
    }
  }

}

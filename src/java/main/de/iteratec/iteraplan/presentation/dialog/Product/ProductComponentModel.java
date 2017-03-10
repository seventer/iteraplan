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
package de.iteratec.iteraplan.presentation.dialog.Product;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.base.Objects;

import de.iteratec.iteraplan.businesslogic.reports.query.type.ProductQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.BusinessDomainService;
import de.iteratec.iteraplan.businesslogic.service.ProductService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.presentation.dialog.FastExport.FastExportEntryMemBean;
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
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.BusinessMappingsComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.BusinessMappingsComponentModel.ClusterElementRetriever;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.BusinessMappingsComponentModel.DisplayElements;


public class ProductComponentModel extends BuildingBlockComponentModel<Product> {

  /** Serialization version. */
  private static final long                                                       serialVersionUID      = -6031196216914540430L;
  protected static final String                                                   NAME_LABEL            = "global.name";
  private static final String                                                     DESCRIPTION_LABEL     = "global.description";
  private static final String                                                     ELEMENT_OF_LABEL      = "product.parent";
  private static final String                                                     CONSISTS_OF_LABEL     = "product.children";
  private static final String                                                     BUSINESSMAPPING_LABEL = "product.to.businessMappings";

  private final ElementNameComponentModel<Product>                                nameModel;
  private final StringComponentModel<Product>                                     descriptionModel;
  private final ManyAssociationSetComponentModel<Product, BusinessDomain>         businessDomainModel;
  private final ManyToOneComponentModel<Product, Product>                         elementOfModel;
  private final ManyAssociationListComponentModel<Product, Product>               consistsOfModel;
  private final BusinessMappingsComponentModel<Product, InformationSystemRelease> businessMappingModel;

  private int                                                                     subElementCount       = 0;
  private Integer                                                                 elementId;
  private FastExportEntryMemBean                                                  fastExportBean;

  public ProductComponentModel(ComponentMode componentMode) {
    super(componentMode);
    this.setHtmlId("product");
    setFastExportBean(new FastExportEntryMemBean());

    nameModel = new NameCM(componentMode, "name", NAME_LABEL);
    descriptionModel = new DescriptionCM(componentMode, "description", DESCRIPTION_LABEL);
    businessDomainModel = new BusinessDomainCM(componentMode, "products", "product.to.businessDomains", new String[] { NAME_LABEL,
        "global.description" }, new String[] { "name", "description" }, "hierarchicalName", new BusinessDomain());

    elementOfModel = new ParentCM(componentMode, "elementOf", ELEMENT_OF_LABEL, false);
    consistsOfModel = new ChildrenCM(componentMode, "consistsOf", CONSISTS_OF_LABEL, new String[] { NAME_LABEL, DESCRIPTION_LABEL }, new String[] {
        "name", "description" }, "hierarchicalName", new Product());

    // required to instruct the business mapping model, by which building block type it shall
    // cluster the mappings
    ClusterElementRetriever<InformationSystemRelease> isrFromMappingRetriever = new MappingClusterElementRetriever();
    List<DisplayElements> displayOrder = Arrays.asList(new DisplayElements[] { DisplayElements.INFORMATIONSSYSTEMRELEASE,
        DisplayElements.BUSINESSPROCESS, DisplayElements.BUSINESSUNIT });

    businessMappingModel = new BusinessMappingsComponentModel<Product, InformationSystemRelease>(componentMode, isrFromMappingRetriever, "bm",
        BUSINESSMAPPING_LABEL, displayOrder, Product.class);
  }

  @Override
  public void configure(Product target) {
    super.configure(target);
    nameModel.configure(target);
    descriptionModel.configure(target);
    businessDomainModel.configure(target);
    elementOfModel.configure(target);
    consistsOfModel.configure(target);
    businessMappingModel.configure(target);
  }

  public ManyAssociationSetComponentModel<Product, BusinessDomain> getBusinessDomainModel() {
    return businessDomainModel;
  }

  public ManyAssociationListComponentModel<Product, Product> getConsistsOfModel() {
    return consistsOfModel;
  }

  public StringComponentModel<Product> getDescriptionModel() {
    return descriptionModel;
  }

  public ManyToOneComponentModel<Product, Product> getElementOfModel() {
    return elementOfModel;
  }

  public StringComponentModel<Product> getNameModel() {
    return nameModel;
  }

  public BusinessMappingsComponentModel<Product, InformationSystemRelease> getBusinessMappingModel() {
    return businessMappingModel;
  }

  public int getSubElementCount() {
    return subElementCount;
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

  @Override
  public void initializeFrom(Product source) {
    super.initializeFrom(source);
    this.elementId = source.getId();
    nameModel.initializeFrom(source);
    descriptionModel.initializeFrom(source);
    businessDomainModel.initializeFrom(source);
    elementOfModel.initializeFrom(source);
    consistsOfModel.initializeFrom(source);
    businessMappingModel.initializeFrom(source);

    subElementCount = source.getChildren().size();
  }

  @Override
  public void update() {
    if (getComponentMode() != ComponentMode.READ) {
      super.update();
      nameModel.update();
      descriptionModel.update();
      businessDomainModel.update();
      elementOfModel.update();
      consistsOfModel.update();
      businessMappingModel.update();
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
    return ProductQueryType.getInstance();
  }

  public void sortEverything() {
    consistsOfModel.sort();
  }

  private static final class NameCM extends ElementNameComponentModel<Product> {
    /** Serialization version. */
    private static final long serialVersionUID = -6691580083428683749L;

    public NameCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(Product source) {
      if (Product.TOP_LEVEL_NAME.equals(source.getName())) {
        setVirtualElementSelected(true);
      }

      return source.getName();
    }

    @Override
    public void setStringForElement(Product target, String stringToSet) {
      target.setName(stringToSet);
    }
  }

  private static final class DescriptionCM extends StringComponentModel<Product> {
    /** Serialization version. */
    private static final long serialVersionUID = -2833643641702489631L;

    public DescriptionCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(Product source) {
      return source.getDescription();
    }

    @Override
    public void setStringForElement(Product target, String stringToSet) {
      target.setDescription(stringToSet);
    }
  }

  private static final class BusinessDomainCM extends ManyAssociationSetComponentModelDL<Product, BusinessDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = 6916283301480644301L;

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
    protected Set<BusinessDomain> getConnectedElements(Product source) {
      return source.getBusinessDomains();
    }

    @Override
    protected void setConnectedElements(Product target, Set<BusinessDomain> toConnect) {
      if (!target.getBusinessDomains().equals(toConnect)) {
        List<BusinessDomain> reloadedEntities = SpringServiceFactory.getBusinessDomainService().reload(toConnect);
        target.removeBusinessDomainRelations();
        target.addBusinessDomains(reloadedEntities);
      }
    }
  }

  private static final class ParentCM extends ManyToOneComponentModelDL<Product, Product> {
    /** Serialization version. */
    private static final long serialVersionUID = -9152694016980582252L;

    public ParentCM(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
      super(componentMode, htmlId, labelKey, nullable);
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
    protected Product getConnectedElement(Product source) {
      return source.getParent();
    }

    @Override
    protected void setConnectedElement(Product target, Product parent) {
      final boolean parentsEqual = Objects.equal(target.getParent(), parent);

      if (!target.isTopLevelElement() && !parentsEqual) {
        Product reloadParent = SpringServiceFactory.getProductService().loadObjectById(parent.getId());
        target.removeParent();
        target.addParent(reloadParent);
      }
    }
  }

  private static final class ChildrenCM extends ManyAssociationListComponentModelDL<Product, Product> {
    /** Serialization version. */
    private static final long serialVersionUID = -9093365669933287619L;

    public ChildrenCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys, String[] connectedElementsFields,
        String availableElementsLabel, Product dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
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
    protected List<Product> getConnectedElements(Product source) {
      return source.getChildren();
    }

    @Override
    protected boolean isElementRemovable() {
      return getSourceElement().getParent() != null;
    }

    @Override
    protected void setConnectedElements(Product target, List<Product> children) {
      if (!target.getChildren().equals(children)) {
        List<Product> reloadedChildren = SpringServiceFactory.getProductService().reload(children);
        Product rootProject = SpringServiceFactory.getProductService().getFirstElement();

        target.removeChildren(rootProject);
        target.addChildren(reloadedChildren);
      }
    }
  }

  private static final class MappingClusterElementRetriever implements ClusterElementRetriever<InformationSystemRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = -5205109210203606613L;

    public InformationSystemRelease getClusterElementFromMapping(BusinessMapping mapping) {
      return mapping.getInformationSystemRelease();
    }
  }

}

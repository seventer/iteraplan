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
package de.iteratec.iteraplan.presentation.dialog.BusinessUnit;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.base.Objects;

import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessUnitQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.BusinessDomainService;
import de.iteratec.iteraplan.businesslogic.service.BusinessUnitService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;
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


/**
 * GUI model for the {@code BusinessUnit} dialog.
 */
public class BusinessUnitComponentModel extends BuildingBlockComponentModel<BusinessUnit> {

  /** Serialization version. */
  private static final long                                                            serialVersionUID      = 2439557463783629089L;
  protected static final String                                                        NAME_LABEL            = "global.name";
  private static final String                                                          DESCRIPTION_LABEL     = "global.description";
  private static final String                                                          PARENT_LABEL          = "businessUnit.parent";
  private static final String                                                          CHILDREN_LABEL        = "businessUnit.children";
  private static final String                                                          BUSINESSMAPPING_LABEL = "businessUnit.to.businessMappings";

  private final ElementNameComponentModel<BusinessUnit>                                nameModel;
  private final StringComponentModel<BusinessUnit>                                     descriptionModel;
  private final ManyToOneComponentModel<BusinessUnit, BusinessUnit>                    elementOfModel;
  private final ManyAssociationListComponentModel<BusinessUnit, BusinessUnit>          consistsOfModel;
  private final ManyAssociationSetComponentModel<BusinessUnit, BusinessDomain>         businessDomainModel;
  private final BusinessMappingsComponentModel<BusinessUnit, InformationSystemRelease> businessMappingModel;

  private int                                                                          subElementCount       = 0;
  private Integer                                                                      elementId;
  private FastExportEntryMemBean                                                       fastExportBean;

  public BusinessUnitComponentModel(ComponentMode componentMode) {
    super(componentMode);
    this.setHtmlId("BusinessUnit");
    setFastExportBean(new FastExportEntryMemBean());

    nameModel = new NameCM(componentMode, "name", NAME_LABEL);
    descriptionModel = new DescriptionCM(componentMode, "description", DESCRIPTION_LABEL);
    elementOfModel = new ParentCM(componentMode, "elementOf", PARENT_LABEL, false);
    consistsOfModel = new ChildrenCM(componentMode, "consistsOf", CHILDREN_LABEL, new String[] { "global.name", "global.description" }, new String[] {
        "name", "description" }, "hierarchicalName", new BusinessUnit());

    businessDomainModel = new BusinessDomainCM(componentMode, "bd", "businessUnit.to.businessDomains", new String[] { NAME_LABEL,
        "global.description" }, new String[] { "name", "description" }, "hierarchicalName", new BusinessDomain());

    // required to instruct the business mapping model, by which building block type it shall
    // cluster the mappings
    ClusterElementRetriever<InformationSystemRelease> isrFromMappingRetriever = new MappingClusterElementRetriever();
    List<DisplayElements> displayOrder = Arrays.asList(new DisplayElements[] { DisplayElements.INFORMATIONSSYSTEMRELEASE,
        DisplayElements.BUSINESSPROCESS, DisplayElements.PRODUCT });

    businessMappingModel = new BusinessMappingsComponentModel<BusinessUnit, InformationSystemRelease>(componentMode, isrFromMappingRetriever, "bm",
        BUSINESSMAPPING_LABEL, displayOrder, BusinessUnit.class);
  }

  @Override
  public void initializeFrom(BusinessUnit source) {
    super.initializeFrom(source);
    this.elementId = source.getId();
    nameModel.initializeFrom(source);
    descriptionModel.initializeFrom(source);
    elementOfModel.initializeFrom(source);
    consistsOfModel.initializeFrom(source);
    businessDomainModel.initializeFrom(source);
    businessMappingModel.initializeFrom(source);

    subElementCount = source.getChildren().size();
  }

  @Override
  public void update() {
    if (getComponentMode() != ComponentMode.READ) {
      super.update();
      nameModel.update();
      descriptionModel.update();
      elementOfModel.update();
      consistsOfModel.update();
      businessDomainModel.update();
      businessMappingModel.update();
    }
  }

  @Override
  public void configure(BusinessUnit target) {
    super.configure(target);
    nameModel.configure(target);
    descriptionModel.configure(target);
    elementOfModel.configure(target);
    consistsOfModel.configure(target);
    businessDomainModel.configure(target);
    businessMappingModel.configure(target);
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

  public StringComponentModel<BusinessUnit> getNameModel() {
    return nameModel;
  }

  public StringComponentModel<BusinessUnit> getDescriptionModel() {
    return descriptionModel;
  }

  public ManyToOneComponentModel<BusinessUnit, BusinessUnit> getElementOfModel() {
    return elementOfModel;
  }

  public int getSubElementCount() {
    return subElementCount;
  }

  public ManyAssociationListComponentModel<BusinessUnit, BusinessUnit> getConsistsOfModel() {
    return consistsOfModel;
  }

  public ManyAssociationSetComponentModel<BusinessUnit, BusinessDomain> getBusinessDomainModel() {
    return businessDomainModel;
  }

  public BusinessMappingsComponentModel<BusinessUnit, InformationSystemRelease> getBusinessMappingModel() {
    return businessMappingModel;
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
    return BusinessUnitQueryType.getInstance();
  }

  public void sortEverything() {
    consistsOfModel.sort();
  }

  private static final class NameCM extends ElementNameComponentModel<BusinessUnit> {
    /** Serialization version. */
    private static final long serialVersionUID = 4486384978275909580L;

    public NameCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(BusinessUnit target, String stringToSet) {
      target.setName(stringToSet);
    }

    @Override
    public String getStringFromElement(BusinessUnit source) {
      if (BusinessUnit.TOP_LEVEL_NAME.equals(source.getName())) {
        setVirtualElementSelected(true);
      }

      return source.getName();
    }
  }

  private static final class DescriptionCM extends StringComponentModel<BusinessUnit> {
    /** Serialization version. */
    private static final long serialVersionUID = -4882100683746323767L;

    public DescriptionCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(BusinessUnit target, String stringToSet) {
      target.setDescription(stringToSet);
    }

    @Override
    public String getStringFromElement(BusinessUnit source) {
      return source.getDescription();
    }
  }

  private static final class ParentCM extends ManyToOneComponentModelDL<BusinessUnit, BusinessUnit> {
    /** Serialization version. */
    private static final long serialVersionUID = 4616760009924333401L;

    public ParentCM(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
      super(componentMode, htmlId, labelKey, nullable);
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
    protected BusinessUnit getConnectedElement(BusinessUnit source) {
      return source.getParent();
    }

    @Override
    protected void setConnectedElement(BusinessUnit target, BusinessUnit parent) {
      final boolean parentsEqual = Objects.equal(target.getParent(), parent);

      if (!target.isTopLevelElement() && !parentsEqual) {
        BusinessUnit reloadParent = SpringServiceFactory.getBusinessUnitService().loadObjectById(parent.getId());
        target.removeParent();
        target.addParent(reloadParent);
      }
    }
  }

  private static final class ChildrenCM extends ManyAssociationListComponentModelDL<BusinessUnit, BusinessUnit> {
    /** Serialization version. */
    private static final long serialVersionUID = 3095076287116231028L;

    public ChildrenCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys, String[] connectedElementsFields,
        String availableElementsLabel, BusinessUnit dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
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
    protected List<BusinessUnit> getConnectedElements(BusinessUnit source) {
      return source.getChildrenAsList();
    }

    @Override
    protected void setConnectedElements(BusinessUnit target, List<BusinessUnit> children) {
      if (!target.getChildren().equals(children)) {
        List<BusinessUnit> reloadedChildren = SpringServiceFactory.getBusinessUnitService().reload(children);
        BusinessUnit rootBu = SpringServiceFactory.getBusinessUnitService().getFirstElement();

        target.removeChildren(rootBu);
        target.addChildren(reloadedChildren);
      }
    }

    @Override
    protected boolean isElementRemovable() {
      return getSourceElement().getParent() != null;
    }
  }

  private static final class BusinessDomainCM extends ManyAssociationSetComponentModelDL<BusinessUnit, BusinessDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = 3008367404796758212L;

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
    protected Set<BusinessDomain> getConnectedElements(BusinessUnit source) {
      return source.getBusinessDomains();
    }

    @Override
    protected void setConnectedElements(BusinessUnit target, Set<BusinessDomain> toConnect) {
      if (!target.getBusinessDomains().equals(toConnect)) {
        List<BusinessDomain> reloadedEntities = SpringServiceFactory.getBusinessDomainService().reload(toConnect);
        target.removeBusinessDomainRelations();
        target.addBusinessDomains(reloadedEntities);
      }
    }
  }

  private static final class MappingClusterElementRetriever implements ClusterElementRetriever<InformationSystemRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = -1205636652836945710L;

    public InformationSystemRelease getClusterElementFromMapping(BusinessMapping mapping) {
      return mapping.getInformationSystemRelease();
    }
  }

}
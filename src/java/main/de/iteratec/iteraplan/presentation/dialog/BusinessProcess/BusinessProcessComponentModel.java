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
package de.iteratec.iteraplan.presentation.dialog.BusinessProcess;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.base.Objects;

import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessProcessTypeQ;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.BusinessDomainService;
import de.iteratec.iteraplan.businesslogic.service.BusinessProcessService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
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


public class BusinessProcessComponentModel extends BuildingBlockComponentModel<BusinessProcess> {

  /** Serialization version. */
  private static final long                                                               serialVersionUID      = -8108304284772981368L;
  protected static final String                                                           NAME_LABEL            = "global.name";
  protected static final String                                                           DESCRIPTION_LABEL     = "global.description";
  private static final String                                                             ELEMENT_OF_LABEL      = "businessProcess.parent";
  protected static final String                                                           CONSISTS_OF_LABEL     = "businessProcess.children";
  private static final String                                                             BUSINESSMAPPING_LABEL = "businessProcess.to.businessMappings";

  private final ElementNameComponentModel<BusinessProcess>                                nameModel;
  private final StringComponentModel<BusinessProcess>                                     descriptionModel;
  private final ManyAssociationSetComponentModel<BusinessProcess, BusinessDomain>         businessDomainModel;
  private final ManyToOneComponentModel<BusinessProcess, BusinessProcess>                 elementOfModel;
  private final ManyAssociationListComponentModel<BusinessProcess, BusinessProcess>       consistsOfModel;
  private final BusinessMappingsComponentModel<BusinessProcess, InformationSystemRelease> businessMappingModel;

  private int                                                                             subElementCount       = 0;
  private Integer                                                                         elementId;
  private FastExportEntryMemBean                                                          fastExportBean;

  public BusinessProcessComponentModel() {
    this(ComponentMode.READ);
  }

  public BusinessProcessComponentModel(ComponentMode componentMode) {
    super(componentMode);
    this.setHtmlId("bp");
    setFastExportBean(new FastExportEntryMemBean());

    nameModel = new NameCM(componentMode, "name", NAME_LABEL);
    descriptionModel = new DescriptionCM(componentMode, "desc", DESCRIPTION_LABEL);
    businessDomainModel = new BusinessDomainCM(componentMode, "businessfunctions", "businessProcess.to.businessDomains", new String[] { NAME_LABEL,
        "global.description" }, new String[] { "name", "description" }, "hierarchicalName", new BusinessDomain());

    elementOfModel = new ParentCM(componentMode, "parent", ELEMENT_OF_LABEL, false);
    consistsOfModel = new ChildrenCM(componentMode, "children", CONSISTS_OF_LABEL, new String[] { NAME_LABEL, DESCRIPTION_LABEL }, new String[] {
        "name", "description" }, "hierarchicalName", new BusinessProcess());

    // required to instruct the business mapping model, by which building block type it shall
    // cluster the mappings
    ClusterElementRetriever<InformationSystemRelease> isrFromMappingRetriever = new MappingClusterElementRetriever();
    List<DisplayElements> displayOrder = Arrays.asList(new DisplayElements[] { DisplayElements.INFORMATIONSSYSTEMRELEASE,
        DisplayElements.BUSINESSUNIT, DisplayElements.PRODUCT });

    businessMappingModel = new BusinessMappingsComponentModel<BusinessProcess, InformationSystemRelease>(componentMode, isrFromMappingRetriever,
        "bm", BUSINESSMAPPING_LABEL, displayOrder, BusinessProcess.class);
  }

  @Override
  public void configure(BusinessProcess target) {
    super.configure(target);
    nameModel.configure(target);
    descriptionModel.configure(target);
    businessDomainModel.configure(target);
    elementOfModel.configure(target);
    consistsOfModel.configure(target);
    businessMappingModel.configure(target);
  }

  public ManyAssociationSetComponentModel<BusinessProcess, BusinessDomain> getBusinessDomainModel() {
    return businessDomainModel;
  }

  public ManyAssociationListComponentModel<BusinessProcess, BusinessProcess> getConsistsOfModel() {
    return consistsOfModel;
  }

  public StringComponentModel<BusinessProcess> getDescriptionModel() {
    return descriptionModel;
  }

  public ManyToOneComponentModel<BusinessProcess, BusinessProcess> getElementOfModel() {
    return elementOfModel;
  }

  public StringComponentModel<BusinessProcess> getNameModel() {
    return nameModel;
  }

  public BusinessMappingsComponentModel<BusinessProcess, InformationSystemRelease> getBusinessMappingModel() {
    return businessMappingModel;
  }

  public int getSubElementCount() {
    return subElementCount;
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
  public void initializeFrom(BusinessProcess source) {
    super.initializeFrom(source);
    nameModel.initializeFrom(source);
    descriptionModel.initializeFrom(source);
    businessDomainModel.initializeFrom(source);
    elementOfModel.initializeFrom(source);
    consistsOfModel.initializeFrom(source);
    businessMappingModel.initializeFrom(source);

    subElementCount = source.getChildrenAsSet().size();
    this.elementId = source.getId();
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
    return BusinessProcessTypeQ.getInstance();
  }

  public void sortEverything() {
    consistsOfModel.sort();
  }

  private static final class NameCM extends ElementNameComponentModel<BusinessProcess> {
    /** Serialization version. */
    private static final long serialVersionUID = -4393817705898991277L;

    public NameCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(BusinessProcess source) {
      if (BusinessProcess.TOP_LEVEL_NAME.equals(source.getName())) {
        setVirtualElementSelected(true);
      }

      return source.getName();
    }

    @Override
    public void setStringForElement(BusinessProcess target, String stringToSet) {
      target.setName(stringToSet);
    }
  }

  private static final class DescriptionCM extends StringComponentModel<BusinessProcess> {
    /** Serialization version. */
    private static final long serialVersionUID = -2011681436076094279L;

    public DescriptionCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(BusinessProcess source) {
      return source.getDescription();
    }

    @Override
    public void setStringForElement(BusinessProcess target, String stringToSet) {
      target.setDescription(stringToSet);
    }
  }

  private static final class BusinessDomainCM extends ManyAssociationSetComponentModelDL<BusinessProcess, BusinessDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = 1267567783995196140L;

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
    protected Set<BusinessDomain> getConnectedElements(BusinessProcess source) {
      return source.getBusinessDomains();
    }

    @Override
    protected void setConnectedElements(BusinessProcess target, Set<BusinessDomain> toConnect) {
      if (!target.getBusinessDomains().equals(toConnect)) {
        List<BusinessDomain> reloadedBds = SpringServiceFactory.getBusinessDomainService().reload(toConnect);
        target.removeBusinessDomainRelations();
        target.addBusinessDomains(reloadedBds);
      }
    }
  }

  private static final class ParentCM extends ManyToOneComponentModelDL<BusinessProcess, BusinessProcess> {
    /** Serialization version. */
    private static final long serialVersionUID = -247914833422662162L;

    public ParentCM(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
      super(componentMode, htmlId, labelKey, nullable);
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
    protected BusinessProcess getConnectedElement(BusinessProcess source) {
      return source.getParent();
    }

    @Override
    protected void setConnectedElement(BusinessProcess target, BusinessProcess parent) {
      final boolean parentsEqual = Objects.equal(target.getParent(), parent);

      if (!target.isTopLevelElement() && !parentsEqual) {
        BusinessProcess reloadParent = SpringServiceFactory.getBusinessProcessService().loadObjectById(parent.getId());
        target.removeParent();
        target.addParent(reloadParent);
      }
    }
  }

  private static final class ChildrenCM extends ManyAssociationListComponentModelDL<BusinessProcess, BusinessProcess> {
    /** Serialization version. */
    private static final long serialVersionUID = 4183149484159723265L;

    public ChildrenCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys, String[] connectedElementsFields,
        String availableElementsLabel, BusinessProcess dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
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
    protected List<BusinessProcess> getConnectedElements(BusinessProcess source) {
      return source.getChildren();
    }

    @Override
    protected boolean isElementRemovable() {
      return getSourceElement().getParent() != null;
    }

    @Override
    protected void setConnectedElements(BusinessProcess target, List<BusinessProcess> children) {
      if (!target.getChildren().equals(children)) {
        List<BusinessProcess> reloadedChildren = SpringServiceFactory.getBusinessProcessService().reload(children);
        BusinessProcess rootProject = SpringServiceFactory.getBusinessProcessService().getFirstElement();

        target.removeChildren(rootProject);
        target.addChildren(reloadedChildren);
      }
    }
  }

  private static final class MappingClusterElementRetriever implements ClusterElementRetriever<InformationSystemRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = 3098821987847124929L;

    public InformationSystemRelease getClusterElementFromMapping(BusinessMapping mapping) {
      return mapping.getInformationSystemRelease();
    }
  }

}

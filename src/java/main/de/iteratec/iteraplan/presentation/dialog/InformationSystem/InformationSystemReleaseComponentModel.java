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
package de.iteratec.iteraplan.presentation.dialog.InformationSystem;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.BusinessFunctionService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemDomainService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService;
import de.iteratec.iteraplan.businesslogic.service.ProjectService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.Seal;
import de.iteratec.iteraplan.model.SealState;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.presentation.dialog.FastExport.FastExportEntryMemBean;
import de.iteratec.iteraplan.presentation.dialog.InformationSystem.model.AbstractInformationSystemReleaseComponentModel;
import de.iteratec.iteraplan.presentation.dialog.InformationSystem.model.JumpToBusinessObjectComponentModel;
import de.iteratec.iteraplan.presentation.dialog.InformationSystem.model.JumpToInformationSystemInterfaceComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetAttributableComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModelDL;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyToOneComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyToOneComponentModelDL;
import de.iteratec.iteraplan.presentation.dialog.common.model.PersistantEnumComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.BusinessMappingsComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.BusinessMappingsComponentModel.ClusterElementRetriever;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.BusinessMappingsComponentModel.DisplayElements;


@SuppressWarnings("PMD.TooManyMethods")
public class InformationSystemReleaseComponentModel extends AbstractInformationSystemReleaseComponentModel {

  /** Serialization version. */
  private static final long                                                                                         serialVersionUID              = 6577539952187476213L;
  private static final String                                                                                       BUSINESSFUNCTION_LABEL        = "informationSystemRelease.to.businessFunctions";
  private static final String                                                                                       BUSINESSOBJECT_LABEL          = "informationSystemRelease.to.businessObjects";
  private static final String                                                                                       BUSINESSMAPPING_LABEL         = "informationSystemRelease.to.businessMappings";
  private static final String                                                                                       INFORMATIONSYSTEMDOMAIN_LABEL = "informationSystemRelease.to.informationSystemDomains";
  private static final String                                                                                       INFRASTRUCTRURE_LABEL         = "informationSystemRelease.to.infrastructureElements";
  private static final String                                                                                       PROJECT_LABEL                 = "informationSystemRelease.to.projects";
  private static final String                                                                                       TECHNICALCOMPONENT_LABEL      = "informationSystemRelease.to.technicalComponentReleases";
  private static final String                                                                                       CHILDREN_LABEL                = "informationSystemRelease.children";
  private static final String                                                                                       PREDECESSOR_LABEL             = "informationSystemRelease.predecessors";
  private static final String                                                                                       SUCCESSOR_LABEL               = "informationSystemRelease.successors";
  private static final String                                                                                       BASECOMPONENT_LABEL           = "informationSystemRelease.baseComponents";
  private static final String                                                                                       PARENTCOMPONENT_LABEL         = "informationSystemRelease.parentComponents";

  private static final String                                                                                       NON_HIERARCHICAL_NAME_FIELD   = "nonHierarchicalName";
  private static final String                                                                                       HIERARCHICAL_NAME_FIELD       = "hierarchicalName";
  private static final String                                                                                       DESCRIPTION_FIELD             = "description";

  private ManyToOneComponentModel<InformationSystemRelease, InformationSystemRelease>                               parentModel;
  private ManyAssociationSetComponentModel<InformationSystemRelease, InformationSystemRelease>                      childrenModel;

  private ManyAssociationSetComponentModel<InformationSystemRelease, BusinessFunction>                              businessFunctionModel;
  private ManyAssociationSetAttributableComponentModel<InformationSystemRelease, BusinessObject, Isr2BoAssociation> businessObjectModel;

  private BusinessMappingsComponentModel<InformationSystemRelease, BusinessProcess>                                 businessMappingModel;

  private JumpToInformationSystemInterfaceComponentModel                                                            informationSystemInterfaceModel;
  private JumpToBusinessObjectComponentModel                                                                        businessObjectsOfIsiModel;
  private ManyAssociationSetComponentModel<InformationSystemRelease, InfrastructureElement>                         infrastructureElementModel;
  private ManyAssociationSetComponentModel<InformationSystemRelease, InformationSystemDomain>                       informationSystemDomainModel;
  private ManyAssociationSetComponentModel<InformationSystemRelease, InformationSystemRelease>                      predecessorModel;
  private ManyAssociationSetComponentModel<InformationSystemRelease, InformationSystemRelease>                      successorModel;
  private ManyAssociationSetComponentModel<InformationSystemRelease, Project>                                       projectModel;
  private ManyAssociationSetComponentModel<InformationSystemRelease, TechnicalComponentRelease>                     technicalComponentModel;
  private ManyAssociationSetComponentModel<InformationSystemRelease, InformationSystemRelease>                      baseComponentModel;
  private ManyAssociationSetComponentModel<InformationSystemRelease, InformationSystemRelease>                      parentComponentModel;
  private SealComponentModel                                                                                        sealComponentModel;
  private int                                                                                                       subElementCount               = 0;

  // The unique identifier of the corresponding building block
  private Integer                                                                                                   releaseId;

  private FastExportEntryMemBean                                                                                    fastExportBean;

  public InformationSystemReleaseComponentModel(ComponentMode componentMode) {
    super(componentMode);
    this.setHtmlId("release");

    setFastExportBean(new FastExportEntryMemBean());
  }

  @Override
  public void initializeFrom(InformationSystemRelease source) {
    super.initializeFrom(source);
    getParentModel().initializeFrom(source);
    getChildrenModel().initializeFrom(source);
    getBusinessFunctionModel().initializeFrom(source);
    getBusinessObjectModel().initializeFrom(source);
    getBusinessMappingModel().initializeFrom(source);
    getInformationSystemInterfaceModel().initializeFrom(source);
    getBusinessObjectsOfIsiModel().initializeFrom(source);
    getInformationSystemDomainModel().initializeFrom(source);
    getInfrastructureElementModel().initializeFrom(source);
    getPredecessorModel().initializeFrom(source);
    getSuccessorModel().initializeFrom(source);
    getProjectModel().initializeFrom(source);
    getTechnicalComponentModel().initializeFrom(source);
    getBaseComponentModel().initializeFrom(source);
    getParentComponentModel().initializeFrom(source);
    subElementCount = source.getChildren().size();
    getSealModel().initializeFrom(source);
    releaseId = source.getId();
  }

  @Override
  public void update() {
    if (getComponentMode() != ComponentMode.READ) {
      super.update();
      getParentModel().update();
      getChildrenModel().update();
      getBusinessFunctionModel().update();
      getBusinessObjectModel().update();
      getBusinessMappingModel().update();
      getInformationSystemInterfaceModel().update();
      getBusinessObjectsOfIsiModel().update();
      getInformationSystemDomainModel().update();
      getInfrastructureElementModel().update();
      getPredecessorModel().update();
      getSuccessorModel().update();
      getProjectModel().update();
      getTechnicalComponentModel().update();
      getBaseComponentModel().update();
      getParentComponentModel().update();
    }
  }

  @Override
  public void configure(InformationSystemRelease target) {
    // The Model() for connections and children are read-only. Thus, no configuration necessary.
    super.configure(target);
    getParentModel().configure(target);
    getChildrenModel().configure(target);
    getBusinessFunctionModel().configure(target);
    getBusinessObjectModel().configure(target);
    getBusinessMappingModel().configure(target);
    getInformationSystemDomainModel().configure(target);
    getInformationSystemInterfaceModel().configure(target);
    getInfrastructureElementModel().configure(target);
    getPredecessorModel().configure(target);
    getSuccessorModel().configure(target);
    getProjectModel().configure(target);
    getTechnicalComponentModel().configure(target);
    getBaseComponentModel().configure(target);
    getParentComponentModel().configure(target);
  }

  public final SealComponentModel getSealModel() {
    if (sealComponentModel == null) {
      sealComponentModel = new SealComponentModel(getComponentMode(), "seal", "seal");
    }

    return sealComponentModel;
  }

  public final ManyToOneComponentModel<InformationSystemRelease, InformationSystemRelease> getParentModel() {
    if (parentModel == null) {
      parentModel = new ParentComponentModel(getComponentMode(), "parent", PARENT_LABEL, false);
    }

    return parentModel;
  }

  public final ManyAssociationSetComponentModel<InformationSystemRelease, InformationSystemRelease> getChildrenModel() {
    if (childrenModel == null) {
      childrenModel = new ChildrenComponentModel(getComponentMode(), "child", CHILDREN_LABEL, new String[] { NAME_LABEL, DESCRIPTION_LABEL },
          new String[] { NON_HIERARCHICAL_NAME_FIELD, DESCRIPTION_FIELD }, NON_HIERARCHICAL_NAME_FIELD, new InformationSystemRelease());
    }

    return childrenModel;
  }

  public final ManyAssociationSetComponentModel<InformationSystemRelease, BusinessFunction> getBusinessFunctionModel() {
    if (businessFunctionModel == null) {
      businessFunctionModel = new BusinessFunctionCM(getComponentMode(), "businessFunction", BUSINESSFUNCTION_LABEL, new String[] { NAME_LABEL,
          DESCRIPTION_LABEL }, new String[] { HIERARCHICAL_NAME_FIELD, DESCRIPTION_FIELD }, HIERARCHICAL_NAME_FIELD, new BusinessFunction());
    }

    return businessFunctionModel;
  }

  public final ManyAssociationSetAttributableComponentModel<InformationSystemRelease, BusinessObject, Isr2BoAssociation> getBusinessObjectModel() {
    if (businessObjectModel == null) {
      businessObjectModel = new BusinessObjectCM(getComponentMode(), "businessObject", BUSINESSOBJECT_LABEL, new String[] { NAME_LABEL,
          DESCRIPTION_LABEL }, new String[] { HIERARCHICAL_NAME_FIELD, DESCRIPTION_FIELD }, HIERARCHICAL_NAME_FIELD, new BusinessObject());
    }

    return businessObjectModel;
  }

  public final BusinessMappingsComponentModel<InformationSystemRelease, BusinessProcess> getBusinessMappingModel() {
    if (businessMappingModel == null) {
      // required to instruct the business mapping model, by which building block type it shall
      // cluster the mappings
      ClusterElementRetriever<BusinessProcess> processFromMappingRetriever = new BusinessMappingClusterElementRetriever();
      List<DisplayElements> displayOrder = Arrays.asList(new DisplayElements[] { DisplayElements.BUSINESSPROCESS, DisplayElements.BUSINESSUNIT,
          DisplayElements.PRODUCT });

      businessMappingModel = new BusinessMappingsComponentModel<InformationSystemRelease, BusinessProcess>(getComponentMode(),
          processFromMappingRetriever, "bm", BUSINESSMAPPING_LABEL, displayOrder, InformationSystemRelease.class);
    }

    return businessMappingModel;
  }

  public final JumpToInformationSystemInterfaceComponentModel getInformationSystemInterfaceModel() {
    if (informationSystemInterfaceModel == null) {
      informationSystemInterfaceModel = new JumpToInformationSystemInterfaceComponentModel(getComponentMode(), "isi");
    }

    return informationSystemInterfaceModel;
  }

  public final ManyAssociationSetComponentModel<InformationSystemRelease, InformationSystemDomain> getInformationSystemDomainModel() {
    if (informationSystemDomainModel == null) {
      informationSystemDomainModel = new InformationSystemDomainCM(getComponentMode(), "isd", INFORMATIONSYSTEMDOMAIN_LABEL, new String[] {
          NAME_LABEL, DESCRIPTION_LABEL }, new String[] { HIERARCHICAL_NAME_FIELD, DESCRIPTION_FIELD }, HIERARCHICAL_NAME_FIELD,
          new InformationSystemDomain());
    }

    return informationSystemDomainModel;
  }

  public final ManyAssociationSetComponentModel<InformationSystemRelease, InfrastructureElement> getInfrastructureElementModel() {
    if (infrastructureElementModel == null) {
      infrastructureElementModel = new InfrastructureElementCM(getComponentMode(), "infrastructureElement", INFRASTRUCTRURE_LABEL, new String[] {
          NAME_LABEL, DESCRIPTION_LABEL }, new String[] { HIERARCHICAL_NAME_FIELD, DESCRIPTION_FIELD }, HIERARCHICAL_NAME_FIELD,
          new InfrastructureElement());
    }

    return infrastructureElementModel;
  }

  public final ManyAssociationSetComponentModel<InformationSystemRelease, InformationSystemRelease> getPredecessorModel() {
    if (predecessorModel == null) {
      predecessorModel = new PredecessorCM(getComponentMode(), "predecessor", PREDECESSOR_LABEL, new String[] { NAME_LABEL, DESCRIPTION_LABEL },
          new String[] { HIERARCHICAL_NAME_FIELD, DESCRIPTION_FIELD }, HIERARCHICAL_NAME_FIELD, new InformationSystemRelease());
    }

    return predecessorModel;
  }

  public final ManyAssociationSetComponentModel<InformationSystemRelease, InformationSystemRelease> getSuccessorModel() {
    if (successorModel == null) {
      successorModel = new SuccessorCM(getComponentMode(), "successor", SUCCESSOR_LABEL, new String[] { NAME_LABEL, DESCRIPTION_LABEL },
          new String[] { HIERARCHICAL_NAME_FIELD, DESCRIPTION_FIELD }, HIERARCHICAL_NAME_FIELD, new InformationSystemRelease());
    }

    return successorModel;
  }

  public final ManyAssociationSetComponentModel<InformationSystemRelease, Project> getProjectModel() {
    if (projectModel == null) {
      projectModel = new ProjectCM(getComponentMode(), "project", PROJECT_LABEL, new String[] { NAME_LABEL, DESCRIPTION_LABEL }, new String[] {
          HIERARCHICAL_NAME_FIELD, DESCRIPTION_FIELD }, HIERARCHICAL_NAME_FIELD, new Project());
    }

    return projectModel;
  }

  public final ManyAssociationSetComponentModel<InformationSystemRelease, TechnicalComponentRelease> getTechnicalComponentModel() {
    if (technicalComponentModel == null) {
      technicalComponentModel = new TechnicalComponentCM(getComponentMode(), "technicalComponent", TECHNICALCOMPONENT_LABEL, new String[] {
          NAME_LABEL, DESCRIPTION_LABEL }, new String[] { RELEASE_NAME_LABEL, DESCRIPTION_FIELD }, RELEASE_NAME_LABEL,
          new TechnicalComponentRelease());
    }

    return technicalComponentModel;
  }

  public final ManyAssociationSetComponentModel<InformationSystemRelease, InformationSystemRelease> getBaseComponentModel() {
    if (baseComponentModel == null) {
      baseComponentModel = new BaseComponentCM(getComponentMode(), "baseComponents", BASECOMPONENT_LABEL, new String[] { NAME_LABEL,
          DESCRIPTION_LABEL }, new String[] { RELEASE_NAME_LABEL, "description" }, RELEASE_NAME_LABEL, new InformationSystemRelease());
    }

    return baseComponentModel;
  }

  public final ManyAssociationSetComponentModel<InformationSystemRelease, InformationSystemRelease> getParentComponentModel() {
    if (parentComponentModel == null) {
      parentComponentModel = new ParentComponentCM(getComponentMode(), "parentComponents", PARENTCOMPONENT_LABEL, new String[] { NAME_LABEL,
          DESCRIPTION_LABEL }, new String[] { RELEASE_NAME_LABEL, "description" }, RELEASE_NAME_LABEL, new InformationSystemRelease());
    }

    return parentComponentModel;
  }

  public int getSubElementCount() {
    return subElementCount;
  }

  public final JumpToBusinessObjectComponentModel getBusinessObjectsOfIsiModel() {
    if (businessObjectsOfIsiModel == null) {
      businessObjectsOfIsiModel = new JumpToBusinessObjectComponentModel(getComponentMode(), "boOfIsi");
    }

    return businessObjectsOfIsiModel;
  }

  public Integer getReleaseId() {
    return releaseId;
  }

  public void setReleaseId(Integer releaseId) {
    this.releaseId = releaseId;
  }

  @Override
  public Type<InformationSystemRelease> getManagedType() {
    return InformationSystemReleaseTypeQu.getInstance();
  }

  public final void setFastExportBean(FastExportEntryMemBean fastExportBean) {
    this.fastExportBean = fastExportBean;
  }

  public FastExportEntryMemBean getFastExportBean() {
    return fastExportBean;
  }

  private static final class ParentComponentModel extends ManyToOneComponentModelDL<InformationSystemRelease, InformationSystemRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = 8198957772552134610L;

    public ParentComponentModel(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
      super(componentMode, htmlId, labelKey, nullable);
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
    protected InformationSystemRelease getConnectedElement(InformationSystemRelease source) {
      return source.getParent();
    }

    @Override
    protected void setConnectedElement(InformationSystemRelease target, InformationSystemRelease parent) {
      if (parent != null) {
        InformationSystemRelease reloadedParent = SpringServiceFactory.getInformationSystemReleaseService().loadObjectById(parent.getId());
        target.addParent(reloadedParent);
      }
      else {
        target.removeParent();
      }
    }
  }

  private static final class ChildrenComponentModel extends ManyAssociationSetComponentModelDL<InformationSystemRelease, InformationSystemRelease> {

    /** Serialization version. */
    private static final long serialVersionUID = -1958897243540364442L;

    public ChildrenComponentModel(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, InformationSystemRelease dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<InformationSystemRelease> comparatorForSorting() {
      return new InformationSystemReleaseComparator();
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
    protected Set<InformationSystemRelease> getConnectedElements(InformationSystemRelease source) {
      return source.getChildren();
    }

    @Override
    protected void setConnectedElements(InformationSystemRelease target, Set<InformationSystemRelease> children) {
      if (!target.getChildren().equals(children)) {
        List<InformationSystemRelease> reloadedChildren = SpringServiceFactory.getInformationSystemReleaseService().reload(children);
        target.removeChildren();
        target.addChildren(reloadedChildren);
      }
    }

    private static final class InformationSystemReleaseComparator implements Comparator<InformationSystemRelease>, Serializable {
      /** Serialization version. */
      private static final long serialVersionUID = 1269203325765449236L;

      public int compare(InformationSystemRelease o1, InformationSystemRelease o2) {
        return o1.getNonHierarchicalName().compareToIgnoreCase(o2.getNonHierarchicalName());
      }
    }
  }

  private static final class BusinessFunctionCM extends ManyAssociationSetComponentModelDL<InformationSystemRelease, BusinessFunction> {

    /** Serialization version. */
    private static final long serialVersionUID = 590144468550969818L;

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
    protected Set<BusinessFunction> getConnectedElements(InformationSystemRelease source) {
      return source.getBusinessFunctions();
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void setConnectedElements(InformationSystemRelease target, Set<BusinessFunction> toConnect) {
      target.setBusinessFunctions(toConnect);
    }
  }

  private static final class BusinessObjectCM extends
      ManyAssociationSetAttributableComponentModel<InformationSystemRelease, BusinessObject, Isr2BoAssociation> {

    /** Serialization verion. */
    private static final long serialVersionUID = -8182475635979747108L;

    public BusinessObjectCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, BusinessObject dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<BusinessObject> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<BusinessObject>();
    }

    @Override
    protected TypeOfBuildingBlock getAssociationType() {
      return TypeOfBuildingBlock.ISR2BOASSOCIATION;
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.BUSINESSOBJECT;
    }

    @Override
    protected Set<Isr2BoAssociation> getAssociationsFrom(InformationSystemRelease entity) {
      return entity.getBusinessObjectAssociations();
    }

    @Override
    protected List<BusinessObject> getAvailableElements(Integer id, List<BusinessObject> connected) {
      return SpringServiceFactory.getBusinessObjectService().getEntitiesFiltered(connected, false);
    }

    @Override
    protected Set<BusinessObject> getConnectedElements(InformationSystemRelease source) {
      return source.getBusinessObjects();
    }

    @Override
    protected void connectAssociation(Isr2BoAssociation association, InformationSystemRelease source, BusinessObject target) {
      association.setBusinessObject(target);
      association.setInformationSystemRelease(source);
      association.connect();
    }

    @Override
    protected BusinessObject getTargetFrom(Isr2BoAssociation association) {
      return association.getBusinessObject();
    }

    @Override
    protected Isr2BoAssociation createNewAssociation() {
      return BuildingBlockFactory.createIsr2BoAssociation();
    }

  }

  private static final class BusinessMappingClusterElementRetriever implements ClusterElementRetriever<BusinessProcess> {
    /** Serialization version. */
    private static final long serialVersionUID = -3949571251283561465L;

    public BusinessProcess getClusterElementFromMapping(BusinessMapping mapping) {
      return mapping.getBusinessProcess();
    }
  }

  private static final class InformationSystemDomainCM extends ManyAssociationSetComponentModelDL<InformationSystemRelease, InformationSystemDomain>
      implements Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = -4627111529136816632L;

    public InformationSystemDomainCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, InformationSystemDomain dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<InformationSystemDomain> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<InformationSystemDomain>();
    }

    @Override
    protected InformationSystemDomainService getService() {
      return SpringServiceFactory.getInformationSystemDomainService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN;
    }

    @Override
    protected Set<InformationSystemDomain> getConnectedElements(InformationSystemRelease source) {
      return source.getInformationSystemDomains();
    }

    @Override
    protected void setConnectedElements(InformationSystemRelease target, Set<InformationSystemDomain> toConnect) {
      if (!target.getInformationSystemDomains().equals(toConnect)) {
        List<InformationSystemDomain> reloadedEntities = SpringServiceFactory.getInformationSystemDomainService().reload(toConnect);
        target.removeInformationSystemDomains();
        target.addInformationSystemDomains(reloadedEntities);
      }
    }
  }

  private static final class InfrastructureElementCM extends ManyAssociationSetComponentModelDL<InformationSystemRelease, InfrastructureElement>
      implements Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = -9007780606959246861L;

    public InfrastructureElementCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
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
    protected Set<InfrastructureElement> getConnectedElements(InformationSystemRelease source) {
      return source.getInfrastructureElements();
    }

    @Override
    protected void setConnectedElements(InformationSystemRelease target, Set<InfrastructureElement> toConnect) {
      if (!target.getInfrastructureElements().equals(toConnect)) {
        List<InfrastructureElement> reloadedEntities = SpringServiceFactory.getInfrastructureElementService().reload(toConnect);
        target.removeInfrastructureElements();
        target.addInfrastructureElements(reloadedEntities);
      }
    }
  }

  private static final class PredecessorCM extends ManyAssociationSetComponentModelDL<InformationSystemRelease, InformationSystemRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = -9025293556984876244L;

    public PredecessorCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
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
    protected Set<InformationSystemRelease> getConnectedElements(InformationSystemRelease source) {
      return source.getPredecessors();
    }

    @Override
    protected void setConnectedElements(InformationSystemRelease target, Set<InformationSystemRelease> toConnect) {
      if (!target.getPredecessors().equals(toConnect)) {
        List<InformationSystemRelease> reloadedEntities = SpringServiceFactory.getInformationSystemReleaseService().reload(toConnect);
        target.removePredecessors();
        target.addPredecessors(reloadedEntities);
      }
    }
  }

  private static final class SuccessorCM extends ManyAssociationSetComponentModelDL<InformationSystemRelease, InformationSystemRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = -5978590974955190074L;

    public SuccessorCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
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
    protected Set<InformationSystemRelease> getConnectedElements(InformationSystemRelease source) {
      return source.getSuccessors();
    }

    @Override
    protected void setConnectedElements(InformationSystemRelease target, Set<InformationSystemRelease> toConnect) {
      if (!target.getSuccessors().equals(toConnect)) {
        List<InformationSystemRelease> reloadedEntities = SpringServiceFactory.getInformationSystemReleaseService().reload(toConnect);
        target.removeSuccessors();
        target.addSuccessors(reloadedEntities);
      }
    }
  }

  private static final class ProjectCM extends ManyAssociationSetComponentModelDL<InformationSystemRelease, Project> implements Serializable {
    /** Serialization version. */
    private static final long serialVersionUID = 1070949072664440542L;

    public ProjectCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys, String[] connectedElementsFields,
        String availableElementsLabel, Project dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<Project> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<Project>();
    }

    @Override
    protected ProjectService getService() {
      return SpringServiceFactory.getProjectService();
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.PROJECT;
    }

    @Override
    protected Set<Project> getConnectedElements(InformationSystemRelease source) {
      return source.getProjects();
    }

    @Override
    protected void setConnectedElements(InformationSystemRelease target, Set<Project> toConnect) {
      if (!target.getProjects().equals(toConnect)) {
        List<Project> reloadedEntities = SpringServiceFactory.getProjectService().reload(toConnect);
        target.removeProjects();
        target.addProjects(reloadedEntities);
      }
    }
  }

  private static final class TechnicalComponentCM extends ManyAssociationSetComponentModelDL<InformationSystemRelease, TechnicalComponentRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = -7270462005624377548L;

    public TechnicalComponentCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
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
    protected Set<TechnicalComponentRelease> getConnectedElements(InformationSystemRelease source) {
      return source.getTechnicalComponentReleases();
    }

    @Override
    protected void setConnectedElements(InformationSystemRelease target, Set<TechnicalComponentRelease> toConnect) {
      if (!target.getTechnicalComponentReleases().equals(toConnect)) {
        List<TechnicalComponentRelease> reloadedEntities = SpringServiceFactory.getTechnicalComponentReleaseService().reload(toConnect);
        target.removeTechnicalComponentReleases();
        target.addTechnicalComponentReleases(reloadedEntities);
      }
    }
  }

  private static final class BaseComponentCM extends ManyAssociationSetComponentModelDL<InformationSystemRelease, InformationSystemRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = -184777936617918614L;

    public BaseComponentCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
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
    protected Set<InformationSystemRelease> getConnectedElements(InformationSystemRelease source) {
      return source.getBaseComponents();
    }

    @Override
    protected void setConnectedElements(InformationSystemRelease target, Set<InformationSystemRelease> toConnect) {
      if (!target.getBaseComponents().equals(toConnect)) {
        List<InformationSystemRelease> reloadedEntities = SpringServiceFactory.getInformationSystemReleaseService().reload(toConnect);
        target.removeBaseComponents();
        target.addBaseComponents(reloadedEntities);
      }
    }
  }

  private static final class ParentComponentCM extends ManyAssociationSetComponentModelDL<InformationSystemRelease, InformationSystemRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = -184777936617918614L;

    public ParentComponentCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
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
    protected Set<InformationSystemRelease> getConnectedElements(InformationSystemRelease source) {
      return source.getParentComponents();
    }

    @Override
    protected void setConnectedElements(InformationSystemRelease target, Set<InformationSystemRelease> toConnect) {
      if (!target.getParentComponents().equals(toConnect)) {
        List<InformationSystemRelease> reloadedEntities = SpringServiceFactory.getInformationSystemReleaseService().reload(toConnect);
        target.removeParentComponents();
        target.addParentComponents(reloadedEntities);
      }
    }
  }

  /**
   * Component model to show the {@link Seal} state.
   */
  public static final class SealComponentModel extends PersistantEnumComponentModel<InformationSystemRelease, SealState> {
    /** Serialization version. */
    private static final long serialVersionUID = -4777561358287705107L;
    private Seal              lastSeal;
    private List<Seal>        seals;

    public SealComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    /** {@inheritDoc} */
    @Override
    protected void setEnumForElement(InformationSystemRelease target, SealState currentEnum) {
      target.setSealState(currentEnum);
    }

    /** {@inheritDoc} */
    @Override
    protected SealState getEnumFromElement(InformationSystemRelease source) {
      return source.getSealState();
    }

    /** {@inheritDoc} */
    @Override
    public void initializeFrom(InformationSystemRelease source) throws IteraplanException {
      super.initializeFrom(source);
      lastSeal = source.getLastSeal();

      List<Seal> sortedSeals = Ordering.natural().reverse().sortedCopy(source.getSeals());
      seals = Lists.newArrayList(sortedSeals);
    }

    /**
     * Returns the last created seal.
     * 
     * @return the last created seal or {@code null}
     */
    public Seal getLastSeal() {
      return lastSeal;
    }

    /**
     * Returns all created seal sorted by creation date.
     * 
     * @return all created seal sorted by creation date
     */
    public Collection<Seal> getSeals() {
      return seals;
    }
  }
}

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
package de.iteratec.iteraplan.presentation.dialog.TechnicalComponent;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.reports.query.type.TechnicalComponentReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.ArchitecturalDomainService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.presentation.dialog.FastExport.FastExportEntryMemBean;
import de.iteratec.iteraplan.presentation.dialog.TechnicalComponent.model.AbstractTechnicalComponentReleaseComponentModel;
import de.iteratec.iteraplan.presentation.dialog.TechnicalComponent.model.JumpToInformationSystemInterfaceComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetAttributableComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModelDL;


public class TechnicalComponentReleaseComponentModel extends AbstractTechnicalComponentReleaseComponentModel {

  /** Serialization version. */
  private static final long                                                                                                 serialVersionUID  = 2477295721608779290L;
  protected static final String                                                                                             NAME_LABEL        = "global.name";
  protected static final String                                                                                             HIERARCHICAL_NAME = "hierarchicalName";
  private static final String                                                                                               RELEASE_NAME      = "releaseName";
  private static final String                                                                                               DESCRIPTION       = "description";

  private JumpToInformationSystemInterfaceComponentModel                                                                    informationSystemInterfaceModel;
  private ManyAssociationSetComponentModel<TechnicalComponentRelease, ArchitecturalDomain>                                  architecturalDomainModel;
  private ManyAssociationSetComponentModel<TechnicalComponentRelease, InformationSystemRelease>                             informationSystemReleaseModel;
  private ManyAssociationSetAttributableComponentModel<TechnicalComponentRelease, InfrastructureElement, Tcr2IeAssociation> infrastructureElementModel;
  private ManyAssociationSetComponentModel<TechnicalComponentRelease, TechnicalComponentRelease>                            predecessorModel;
  private ManyAssociationSetComponentModel<TechnicalComponentRelease, TechnicalComponentRelease>                            baseComponentModel;
  private ManyAssociationSetComponentModel<TechnicalComponentRelease, TechnicalComponentRelease>                            parentComponentModel;
  private ManyAssociationSetComponentModel<TechnicalComponentRelease, TechnicalComponentRelease>                            successorModel;

  private FastExportEntryMemBean                                                                                            fastExportBean;
  /**
   * The unique identifier of the corresponding building block, needed for visualisation / fast
   * export purposes
   */
  private Integer                                                                                                           releaseId;

  public TechnicalComponentReleaseComponentModel(ComponentMode componentMode) {
    super(componentMode);
    this.setHtmlId("tcr");

    this.fastExportBean = new FastExportEntryMemBean();
  }

  public JumpToInformationSystemInterfaceComponentModel getInformationSystemInterfaceModel() {
    if (informationSystemInterfaceModel == null) {
      informationSystemInterfaceModel = new JumpToInformationSystemInterfaceComponentModel(getComponentMode(), "isi");
    }
    return informationSystemInterfaceModel;
  }

  public ManyAssociationSetComponentModel<TechnicalComponentRelease, ArchitecturalDomain> getArchitecturalDomainModel() {
    if (architecturalDomainModel == null) {
      this.architecturalDomainModel = new ArchitecturalDomainCM(getComponentMode(), "ad", "technicalComponentRelease.to.architecturalDomains",
          new String[] { NAME_LABEL, DESCRIPTION_LABEL }, new String[] { HIERARCHICAL_NAME, DESCRIPTION }, HIERARCHICAL_NAME,
          new ArchitecturalDomain());
    }
    return architecturalDomainModel;
  }

  public ManyAssociationSetComponentModel<TechnicalComponentRelease, InformationSystemRelease> getInformationSystemReleaseModel() {
    if (informationSystemReleaseModel == null) {
      this.informationSystemReleaseModel = new InformationSystemReleaseCM(getComponentMode(), "isr",
          "technicalComponentRelease.to.informationSystemReleases", new String[] { NAME_LABEL, DESCRIPTION_LABEL }, new String[] { HIERARCHICAL_NAME,
              DESCRIPTION }, HIERARCHICAL_NAME, new InformationSystemRelease());
    }
    return informationSystemReleaseModel;
  }

  public ManyAssociationSetComponentModel<TechnicalComponentRelease, InfrastructureElement> getInfrastructureElementModel() {
    if (infrastructureElementModel == null) {
      this.infrastructureElementModel = new InfrastructureElementCM(getComponentMode(), "ie", "technicalComponentRelease.to.infrastructureElements",
          new String[] { NAME_LABEL, DESCRIPTION_LABEL }, new String[] { HIERARCHICAL_NAME, DESCRIPTION }, HIERARCHICAL_NAME,
          new InfrastructureElement());
    }
    return infrastructureElementModel;
  }

  public ManyAssociationSetComponentModel<TechnicalComponentRelease, TechnicalComponentRelease> getPredecessorModel() {
    if (predecessorModel == null) {
      predecessorModel = new PredecessorCM(getComponentMode(), "pred", "technicalComponentRelease.predecessors", new String[] { NAME_LABEL,
          DESCRIPTION_LABEL }, new String[] { RELEASE_NAME, DESCRIPTION }, RELEASE_NAME, new TechnicalComponentRelease());
    }

    return predecessorModel;
  }

  public ManyAssociationSetComponentModel<TechnicalComponentRelease, TechnicalComponentRelease> getSuccessorModel() {
    if (successorModel == null) {
      successorModel = new SuccessorCM(getComponentMode(), "succ", "technicalComponentRelease.successors", new String[] { NAME_LABEL,
          DESCRIPTION_LABEL }, new String[] { RELEASE_NAME, DESCRIPTION }, RELEASE_NAME, new TechnicalComponentRelease());
    }

    return successorModel;
  }

  public ManyAssociationSetComponentModel<TechnicalComponentRelease, TechnicalComponentRelease> getBaseComponentModel() {
    if (baseComponentModel == null) {
      this.baseComponentModel = new BaseComponentCM(getComponentMode(), "baseComponents", "technicalComponentRelease.baseComponents", new String[] {
          NAME_LABEL, DESCRIPTION_LABEL }, new String[] { RELEASE_NAME, DESCRIPTION }, RELEASE_NAME, new TechnicalComponentRelease());
    }
    return baseComponentModel;
  }

  public ManyAssociationSetComponentModel<TechnicalComponentRelease, TechnicalComponentRelease> getParentComponentModel() {
    if (parentComponentModel == null) {
      this.parentComponentModel = new ParentComponentCM(getComponentMode(), "parentComponents", "technicalComponentRelease.parentComponents",
          new String[] { NAME_LABEL, DESCRIPTION_LABEL }, new String[] { RELEASE_NAME, DESCRIPTION }, RELEASE_NAME, new TechnicalComponentRelease());
    }
    return parentComponentModel;
  }

  public Integer getReleaseId() {
    return releaseId;
  }

  public void setReleaseId(Integer releaseId) {
    this.releaseId = releaseId;
  }

  @Override
  public Type<TechnicalComponentRelease> getManagedType() {
    return TechnicalComponentReleaseTypeQu.getInstance();
  }

  @Override
  public void initializeFrom(TechnicalComponentRelease source) {
    super.initializeFrom(source);
    getPredecessorModel().initializeFrom(source);
    getSuccessorModel().initializeFrom(source);
    getArchitecturalDomainModel().initializeFrom(source);
    getBaseComponentModel().initializeFrom(source);
    getParentComponentModel().initializeFrom(source);
    getInformationSystemReleaseModel().initializeFrom(source);
    getInfrastructureElementModel().initializeFrom(source);
    getInformationSystemInterfaceModel().initializeFrom(source);
    releaseId = source.getId();
  }

  @Override
  public void update() {
    if (getComponentMode() != ComponentMode.READ) {
      super.update();
      getPredecessorModel().update();
      getSuccessorModel().update();
      getArchitecturalDomainModel().update();
      getBaseComponentModel().update();
      getParentComponentModel().update();
      getInformationSystemReleaseModel().update();
      getInfrastructureElementModel().update();
      getInformationSystemInterfaceModel().update();
    }
  }

  @Override
  public void configure(TechnicalComponentRelease target) {
    super.configure(target);
    getPredecessorModel().configure(target);
    getSuccessorModel().configure(target);
    getArchitecturalDomainModel().configure(target);
    getBaseComponentModel().configure(target);
    getParentComponentModel().configure(target);
    getInformationSystemReleaseModel().configure(target);
    getInfrastructureElementModel().configure(target);
  }

  public FastExportEntryMemBean getFastExportBean() {
    return fastExportBean;
  }

  private static final class ArchitecturalDomainCM extends ManyAssociationSetComponentModelDL<TechnicalComponentRelease, ArchitecturalDomain> {

    /** Serialization version. */
    private static final long serialVersionUID = 3852378966361777332L;

    public ArchitecturalDomainCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, ArchitecturalDomain dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<ArchitecturalDomain> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<ArchitecturalDomain>();
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
    protected Set<ArchitecturalDomain> getConnectedElements(TechnicalComponentRelease source) {
      return source.getArchitecturalDomains();
    }

    @Override
    protected void setConnectedElements(TechnicalComponentRelease target, Set<ArchitecturalDomain> toConnect) {
      if (!target.getArchitecturalDomains().equals(toConnect)) {
        List<ArchitecturalDomain> reloadedEntities = SpringServiceFactory.getArchitecturalDomainService().reload(toConnect);
        target.removeArchitecturalDomains();
        target.addArchitecturalDomains(reloadedEntities);
      }
    }
  }

  private static final class InformationSystemReleaseCM extends
      ManyAssociationSetComponentModelDL<TechnicalComponentRelease, InformationSystemRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = 2771231639877125183L;

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
    protected Set<InformationSystemRelease> getConnectedElements(TechnicalComponentRelease source) {
      return source.getInformationSystemReleases();
    }

    @Override
    protected void setConnectedElements(TechnicalComponentRelease target, Set<InformationSystemRelease> toConnect) {
      if (!target.getInformationSystemReleases().equals(toConnect)) {
        List<InformationSystemRelease> reloadedEntities = SpringServiceFactory.getInformationSystemReleaseService().reload(toConnect);
        target.removeInformationSystemReleases();
        target.addInformationSystemReleases(reloadedEntities);
      }
    }
  }

  private static final class InfrastructureElementCM extends
      ManyAssociationSetAttributableComponentModel<TechnicalComponentRelease, InfrastructureElement, Tcr2IeAssociation> {
    /** Serialization version. */
    private static final long serialVersionUID = -356029000820278694L;

    public InfrastructureElementCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, InfrastructureElement dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected Comparator<InfrastructureElement> comparatorForSorting() {
      return new HierarchicalEntityCachingComparator<InfrastructureElement>();
    }

    @Override
    protected TypeOfBuildingBlock getAssociationType() {
      return TypeOfBuildingBlock.TCR2IEASSOCIATION;
    }

    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return TypeOfBuildingBlock.INFRASTRUCTUREELEMENT;
    }

    @Override
    protected Set<Tcr2IeAssociation> getAssociationsFrom(TechnicalComponentRelease entity) {
      return entity.getInfrastructureElementAssociations();
    }

    @Override
    protected List<InfrastructureElement> getAvailableElements(Integer id, List<InfrastructureElement> connected) {
      return SpringServiceFactory.getInfrastructureElementService().getEntitiesFiltered(connected, false);
    }

    @Override
    protected Set<InfrastructureElement> getConnectedElements(TechnicalComponentRelease source) {
      return source.getInfrastructureElements();
    }

    @Override
    protected void connectAssociation(Tcr2IeAssociation association, TechnicalComponentRelease source, InfrastructureElement target) {
      association.setInfrastructureElement(target);
      association.setTechnicalComponentRelease(source);
      association.connect();
    }

    @Override
    protected InfrastructureElement getTargetFrom(Tcr2IeAssociation association) {
      return association.getInfrastructureElement();
    }

    @Override
    protected Tcr2IeAssociation createNewAssociation() {
      return BuildingBlockFactory.createTcr2IeAssociation();
    }
  }

  private static final class PredecessorCM extends ManyAssociationSetComponentModelDL<TechnicalComponentRelease, TechnicalComponentRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = -4736340018944462550L;

    public PredecessorCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
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
    protected Set<TechnicalComponentRelease> getConnectedElements(TechnicalComponentRelease source) {
      return source.getPredecessors();
    }

    @Override
    protected void setConnectedElements(TechnicalComponentRelease target, Set<TechnicalComponentRelease> toConnect) {
      if (!target.getPredecessors().equals(toConnect)) {
        List<TechnicalComponentRelease> reloadedEntities = SpringServiceFactory.getTechnicalComponentReleaseService().reload(toConnect);
        target.removePredecessors();
        target.addPredecessors(reloadedEntities);
      }
    }
  }

  private static final class SuccessorCM extends ManyAssociationSetComponentModelDL<TechnicalComponentRelease, TechnicalComponentRelease> {
    private static final long serialVersionUID = 1L;

    public SuccessorCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
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
    protected Set<TechnicalComponentRelease> getConnectedElements(TechnicalComponentRelease source) {
      return source.getSuccessors();
    }

    @Override
    protected void setConnectedElements(TechnicalComponentRelease target, Set<TechnicalComponentRelease> toConnect) {
      if (!target.getBaseComponents().equals(toConnect)) {
        List<TechnicalComponentRelease> reloadedEntities = SpringServiceFactory.getTechnicalComponentReleaseService().reload(toConnect);
        target.removeSuccessors();
        target.addSuccessors(reloadedEntities);
      }
    }

  }

  private static final class BaseComponentCM extends ManyAssociationSetComponentModelDL<TechnicalComponentRelease, TechnicalComponentRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = -3114123603557040867L;

    public BaseComponentCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
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
    protected Set<TechnicalComponentRelease> getConnectedElements(TechnicalComponentRelease source) {
      return source.getBaseComponents();
    }

    @Override
    protected void setConnectedElements(TechnicalComponentRelease target, Set<TechnicalComponentRelease> toConnect) {
      if (!target.getBaseComponents().equals(toConnect)) {
        List<TechnicalComponentRelease> reloadedEntities = SpringServiceFactory.getTechnicalComponentReleaseService().reload(toConnect);
        target.removeBaseComponents();
        target.addBaseComponents(reloadedEntities);
      }
    }
  }

  private static final class ParentComponentCM extends ManyAssociationSetComponentModelDL<TechnicalComponentRelease, TechnicalComponentRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = -3114123603557040867L;

    public ParentComponentCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
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
    protected Set<TechnicalComponentRelease> getConnectedElements(TechnicalComponentRelease source) {
      return source.getParentComponents();
    }

    @Override
    protected void setConnectedElements(TechnicalComponentRelease target, Set<TechnicalComponentRelease> toConnect) {
      if (!target.getBaseComponents().equals(toConnect)) {
        List<TechnicalComponentRelease> reloadedEntities = SpringServiceFactory.getTechnicalComponentReleaseService().reload(toConnect);
        target.removeParentComponents();
        target.addParentComponents(reloadedEntities);
      }
    }
  }
}

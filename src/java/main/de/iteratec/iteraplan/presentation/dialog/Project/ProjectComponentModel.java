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
package de.iteratec.iteraplan.presentation.dialog.Project;

import static de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory.getInformationSystemReleaseService;
import static de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory.getProjectService;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.reports.query.type.ProjectQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.ProjectService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Project;
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
import de.iteratec.iteraplan.presentation.dialog.common.model.RuntimePeriodComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.StringComponentModel;


/**
 * GUI model for the {@code Project} dialog.
 */
public class ProjectComponentModel extends BuildingBlockComponentModel<Project> {

  /** Serialization version. */
  private static final long                                                         serialVersionUID  = 1701690510652625889L;
  private static final String                                                       LABEL_NAME        = "global.name";
  private static final String                                                       LABEL_DESCRIPTION = "global.description";
  private static final String                                                       LABEL_PARENT      = "project.parent";
  private static final String                                                       LABEL_CHILDREN    = "project.children";

  private Integer                                                                   elementId;
  private FastExportEntryMemBean                                                    fastExportBean;

  private final ElementNameComponentModel<Project>                                  nameModel;
  private final StringComponentModel<Project>                                       descriptionModel;
  private final ManyAssociationSetComponentModel<Project, InformationSystemRelease> informationSystemReleaseModel;
  private final ManyToOneComponentModel<Project, Project>                           parentModel;
  private final ManyAssociationListComponentModel<Project, Project>                 childrenModel;
  private final RuntimePeriodComponentModel<Project>                                runtimePeriodModel;

  /**
   * The number of children of the element managed by this component model.
   */
  private int                                                                       subElementCount   = 0;

  public ProjectComponentModel(ComponentMode componentMode) {
    super(componentMode);
    this.setHtmlId("project");

    setFastExportBean(new FastExportEntryMemBean());

    nameModel = new NameElementNameComponentModel(componentMode, "name", LABEL_NAME);
    descriptionModel = new DescriptionStringComponentModel(componentMode, "description", LABEL_DESCRIPTION);
    informationSystemReleaseModel = new InformationSystemReleaseCM(componentMode, "isr", "project.to.informationSystemReleases", new String[] {
        LABEL_NAME, LABEL_DESCRIPTION }, new String[] { "hierarchicalName", "description" }, "hierarchicalName", new InformationSystemRelease());
    parentModel = new ParentCM(componentMode, "parent", LABEL_PARENT, false);
    childrenModel = new ChildrenCM(componentMode, "children", LABEL_CHILDREN, new String[] { LABEL_NAME, LABEL_DESCRIPTION }, new String[] { "name",
        "description" }, "hierarchicalName", new Project());

    this.runtimePeriodModel = new RuntimePeriodComponentModel<Project>(componentMode, "period");
  }

  @Override
  public void initializeFrom(Project source) {
    super.initializeFrom(source);
    this.elementId = source.getId();
    nameModel.initializeFrom(source);
    descriptionModel.initializeFrom(source);
    informationSystemReleaseModel.initializeFrom(source);
    parentModel.initializeFrom(source);
    childrenModel.initializeFrom(source);
    subElementCount = source.getChildren().size();
    runtimePeriodModel.initializeFrom(source);
  }

  @Override
  public void update() {
    if (getComponentMode() != ComponentMode.READ) {
      super.update();
      nameModel.update();
      descriptionModel.update();
      informationSystemReleaseModel.update();
      parentModel.update();
      childrenModel.update();
      runtimePeriodModel.update();
    }
  }

  @Override
  public void configure(Project target) {
    super.configure(target);
    nameModel.configure(target);
    descriptionModel.configure(target);
    informationSystemReleaseModel.configure(target);
    parentModel.configure(target);
    childrenModel.configure(target);
    runtimePeriodModel.configure(target);
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

  public StringComponentModel<Project> getNameModel() {
    return nameModel;
  }

  public StringComponentModel<Project> getDescriptionModel() {
    return descriptionModel;
  }

  public ManyAssociationSetComponentModel<Project, InformationSystemRelease> getInformationSystemReleaseModel() {
    return informationSystemReleaseModel;
  }

  public ManyToOneComponentModel<Project, Project> getParentModel() {
    return parentModel;
  }

  public ManyAssociationListComponentModel<Project, Project> getChildrenModel() {
    return childrenModel;
  }

  public int getSubElementCount() {
    return subElementCount;
  }

  public RuntimePeriodComponentModel<Project> getRuntimePeriodModel() {
    return runtimePeriodModel;
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
  public Type<? extends BuildingBlock> getManagedType() {
    return ProjectQueryType.getInstance();
  }

  public void sortEverything() {
    childrenModel.sort();
  }

  private static final class NameElementNameComponentModel extends ElementNameComponentModel<Project> {
    /** Serialization version. */
    private static final long serialVersionUID = 596596850368533108L;

    public NameElementNameComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(Project target, String stringToSet) {
      target.setName(stringToSet);
    }

    @Override
    public String getStringFromElement(Project source) {
      if (Project.TOP_LEVEL_NAME.equals(source.getName())) {
        setVirtualElementSelected(true);
      }

      return source.getName();
    }
  }

  private static final class DescriptionStringComponentModel extends StringComponentModel<Project> {
    /** Serialization version. */
    private static final long serialVersionUID = -1360231224200729683L;

    public DescriptionStringComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(Project target, String stringToSet) {
      target.setDescription(stringToSet);
    }

    @Override
    public String getStringFromElement(Project source) {
      return source.getDescription();
    }
  }

  private static final class InformationSystemReleaseCM extends ManyAssociationSetComponentModelDL<Project, InformationSystemRelease> {
    /** Serialization version. */
    private static final long serialVersionUID = 5018259834708197836L;

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
    protected Set<InformationSystemRelease> getConnectedElements(Project source) {
      return source.getInformationSystemReleases();
    }

    @Override
    protected void setConnectedElements(Project target, Set<InformationSystemRelease> toConnect) {
      if (!target.getInformationSystemReleases().equals(toConnect)) {
        List<InformationSystemRelease> reloadedIsrs = getInformationSystemReleaseService().reload(toConnect);
        target.removeRelations();
        target.addInformationSystemReleases(Sets.newHashSet(reloadedIsrs));
      }
    }
  }

  private static final class ParentCM extends ManyToOneComponentModelDL<Project, Project> {
    /** Serialization version. */
    private static final long serialVersionUID = 6067371915733390085L;

    public ParentCM(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
      super(componentMode, htmlId, labelKey, nullable);
    }

    @Override
    protected void setConnectedElement(Project target, Project parent) {
      final boolean parentsEqual = Objects.equal(target.getParent(), parent);

      if (!target.isTopLevelElement() && !parentsEqual) {
        Project reloadParent = getProjectService().loadObjectById(parent.getId());
        target.removeParent();
        target.addParent(reloadParent);
      }
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
    protected Project getConnectedElement(Project source) {
      return source.getParent();
    }
  }

  private static final class ChildrenCM extends ManyAssociationListComponentModelDL<Project, Project> {
    /** Serialization version. */
    private static final long serialVersionUID = -1845801565685790999L;

    public ChildrenCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys, String[] connectedElementsFields,
        String availableElementsLabel, Project dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
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
    protected List<Project> getConnectedElements(Project source) {
      return source.getChildren();
    }

    @Override
    protected void setConnectedElements(Project project, List<Project> children) {
      if (!project.getChildren().equals(children)) {
        List<Project> reloadedChildren = getProjectService().reload(children);
        Project rootProject = getProjectService().getFirstElement();
        project.removeChildren(rootProject);
        project.addChildren(reloadedChildren);
      }
    }

    @Override
    protected boolean isElementRemovable() {
      return getSourceElement().getParent() != null;
    }
  }
}
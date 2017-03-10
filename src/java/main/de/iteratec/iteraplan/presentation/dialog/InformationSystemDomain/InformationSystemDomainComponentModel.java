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
package de.iteratec.iteraplan.presentation.dialog.InformationSystemDomain;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.base.Objects;

import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemDomainTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemDomainService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemDomain;
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


public class InformationSystemDomainComponentModel extends BuildingBlockComponentModel<InformationSystemDomain> {

  /** Serialization version. */
  private static final long                                                                         serialVersionUID  = -5243351912770756237L;
  protected static final String                                                                     DESCRIPTION_LABEL = "global.description";
  protected static final String                                                                     NAME_LABEL        = "global.name";

  private final ElementNameComponentModel<InformationSystemDomain>                                  nameModel;
  private final StringComponentModel<InformationSystemDomain>                                       descriptionModel;
  private final ManyAssociationSetComponentModel<InformationSystemDomain, InformationSystemRelease> informationSystemReleaseModel;
  private final ManyToOneComponentModel<InformationSystemDomain, InformationSystemDomain>           parentModel;
  private final ManyAssociationListComponentModel<InformationSystemDomain, InformationSystemDomain> childrenModel;

  private int                                                                                       subElementCount   = 0;

  private Integer                                                                                   elementId;
  private FastExportEntryMemBean                                                                    fastExportBean;

  public InformationSystemDomainComponentModel(ComponentMode componentMode) {
    super(componentMode);
    this.setHtmlId("isd");
    setFastExportBean(new FastExportEntryMemBean());

    nameModel = new NameCM(componentMode, "name", NAME_LABEL);
    descriptionModel = new DescripitionCM(componentMode, "description", DESCRIPTION_LABEL);
    informationSystemReleaseModel = new InformationSystemReleaseCM(componentMode, "isr", "informationSystemDomain.to.informationSystemReleases",
        new String[] { NAME_LABEL, DESCRIPTION_LABEL }, new String[] { "hierarchicalName", "description" }, "hierarchicalName",
        new InformationSystemRelease());

    parentModel = new ParentCM(componentMode, "parent", "informationSystemDomain.parent", false);
    childrenModel = new ChildrenCM(componentMode, "children", "informationSystemDomain.children", new String[] { NAME_LABEL, DESCRIPTION_LABEL },
        new String[] { "name", "description" }, "hierarchicalName", new InformationSystemDomain());
  }

  public ManyAssociationListComponentModel<InformationSystemDomain, InformationSystemDomain> getChildrenModel() {
    return childrenModel;
  }

  public StringComponentModel<InformationSystemDomain> getDescriptionModel() {
    return descriptionModel;
  }

  public ManyAssociationSetComponentModel<InformationSystemDomain, InformationSystemRelease> getInformationSystemReleaseModel() {
    return informationSystemReleaseModel;
  }

  public StringComponentModel<InformationSystemDomain> getNameModel() {
    return nameModel;
  }

  public ManyToOneComponentModel<InformationSystemDomain, InformationSystemDomain> getParentModel() {
    return parentModel;
  }

  public int getSubElementCount() {
    return subElementCount;
  }

  @Override
  public void initializeFrom(InformationSystemDomain source) {
    super.initializeFrom(source);
    this.elementId = source.getId();
    nameModel.initializeFrom(source);
    descriptionModel.initializeFrom(source);
    informationSystemReleaseModel.initializeFrom(source);
    parentModel.initializeFrom(source);
    childrenModel.initializeFrom(source);

    subElementCount = source.getChildren().size();
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
    }
  }

  @Override
  public void configure(InformationSystemDomain target) {
    super.configure(target);
    nameModel.configure(target);
    descriptionModel.configure(target);
    informationSystemReleaseModel.configure(target);
    parentModel.configure(target);
    childrenModel.configure(target);
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
    return InformationSystemDomainTypeQu.getInstance();
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

  public void sortEverything() {
    childrenModel.sort();
  }

  private static final class NameCM extends ElementNameComponentModel<InformationSystemDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = 4441868937916326994L;

    public NameCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(InformationSystemDomain source) {
      if (InformationSystemDomain.TOP_LEVEL_NAME.equals(source.getName())) {
        setVirtualElementSelected(true);
      }

      return source.getName();
    }

    @Override
    public void setStringForElement(InformationSystemDomain target, String stringToSet) {
      target.setName(stringToSet);
    }
  }

  private static final class DescripitionCM extends StringComponentModel<InformationSystemDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = -7433899322552584481L;

    public DescripitionCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(InformationSystemDomain source) {
      return source.getDescription();
    }

    @Override
    public void setStringForElement(InformationSystemDomain target, String stringToSet) {
      target.setDescription(stringToSet);
    }
  }

  private static final class InformationSystemReleaseCM extends ManyAssociationSetComponentModelDL<InformationSystemDomain, InformationSystemRelease> {

    /** Serialization version. */
    private static final long serialVersionUID = 2092282457207747212L;

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
    protected Set<InformationSystemRelease> getConnectedElements(InformationSystemDomain source) {
      return source.getInformationSystemReleases();
    }

    @Override
    protected void setConnectedElements(InformationSystemDomain target, Set<InformationSystemRelease> toConnect) {
      if (!target.getInformationSystemReleases().equals(toConnect)) {
        List<InformationSystemRelease> reloadedEntities = SpringServiceFactory.getInformationSystemReleaseService().reload(toConnect);
        target.removeInformationSystemReleases();
        target.addInformationSystemReleases(reloadedEntities);
      }
    }
  }

  private static final class ParentCM extends ManyToOneComponentModelDL<InformationSystemDomain, InformationSystemDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = -2336047540852356042L;

    public ParentCM(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
      super(componentMode, htmlId, labelKey, nullable);
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
    protected InformationSystemDomain getConnectedElement(InformationSystemDomain source) {
      return source.getParent();
    }

    @Override
    protected void setConnectedElement(InformationSystemDomain target, InformationSystemDomain parent) {
      final boolean parentsEqual = Objects.equal(target.getParent(), parent);

      if (!target.isTopLevelElement() && !parentsEqual) {
        InformationSystemDomain reloadParent = SpringServiceFactory.getInformationSystemDomainService().loadObjectById(parent.getId());
        target.removeParent();
        target.addParent(reloadParent);
      }
    }
  }

  private static final class ChildrenCM extends ManyAssociationListComponentModelDL<InformationSystemDomain, InformationSystemDomain> {
    /** Serialization version. */
    private static final long serialVersionUID = 5364305770342843067L;

    public ChildrenCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys, String[] connectedElementsFields,
        String availableElementsLabel, InformationSystemDomain dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
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
    protected List<InformationSystemDomain> getConnectedElements(InformationSystemDomain source) {
      return source.getChildren();
    }

    @Override
    protected void setConnectedElements(InformationSystemDomain target, List<InformationSystemDomain> children) {
      if (!target.getChildren().equals(children)) {
        List<InformationSystemDomain> reloadedChildren = SpringServiceFactory.getInformationSystemDomainService().reload(children);
        InformationSystemDomain rootProject = SpringServiceFactory.getInformationSystemDomainService().getFirstElement();

        target.removeChildren(rootProject);
        target.addChildren(reloadedChildren);
      }
    }

    @Override
    protected boolean isElementRemovable() {
      return getSourceElement().getParent() != null;
    }
  }
}

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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.presentation.dialog.FastExport.FastExportEntryMemBean;
import de.iteratec.iteraplan.presentation.dialog.InformationSystem.model.JumpToInformationSystemInterfaceComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.CanHaveTimeseriesBaseMemBean;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.BusinessMappingsComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.ClusterComponentModelPart;


public class InformationSystemReleaseMemBean extends CanHaveTimeseriesBaseMemBean<InformationSystemRelease, InformationSystemReleaseComponentModel> {

  /** Serialization version. */
  private static final long                      serialVersionUID  = 7694335920683525749L;

  private InformationSystemReleaseComponentModel componentModel    = new InformationSystemReleaseComponentModel(ComponentMode.READ);

  private List<FastExportEntryMemBean>           fastExportEntries = new ArrayList<FastExportEntryMemBean>();

  public List<FastExportEntryMemBean> getFastExportEntries() {

    return fastExportEntries;
  }

  public void setFastExportEntries(List<FastExportEntryMemBean> fastExportEntries) {

    this.fastExportEntries = fastExportEntries;
  }

  public InformationSystemReleaseComponentModel getComponentModel() {

    return componentModel;
  }

  public void setComponentModel(InformationSystemReleaseComponentModel componentModel) {

    this.componentModel = componentModel;
  }

  /**
   * Perform form validation on component model. Is automatically invoked by Spring Webflow when an
   * event occurs in state 'edit'.
   * 
   * @param errors
   *          Spring error context
   */
  public void validateEdit(Errors errors) {
    super.validateEdit(errors);
    errors.pushNestedPath("componentModel");
    componentModel.validate(errors);
    errors.popNestedPath();
  }

  public void validateCreate(Errors errors) {

    validateEdit(errors);
  }

  public void validateInitCreate(Errors errors) {

    validateEdit(errors);
  }

  public boolean isHasAssociatedBusinessMapping() {

    BusinessMappingsComponentModel<InformationSystemRelease, BusinessProcess> businessMappingModel = this.getComponentModel()
        .getBusinessMappingModel();

    // businessMappingModel is never null
    return !businessMappingModel.getClusterParts().isEmpty();
  }

  public boolean isHasInterfaces() {

    JumpToInformationSystemInterfaceComponentModel interfaceModel = this.getComponentModel().getInformationSystemInterfaceModel();

    List<InformationSystemInterface> interfaces = interfaceModel.getInformationSystemInterfaces();

    return (interfaces != null && !interfaces.isEmpty());
  }

  public boolean isHasAssociatedBusinessFunction() {

    ManyAssociationSetComponentModel<InformationSystemRelease, BusinessFunction> businessFunctionModel = this.getComponentModel()
        .getBusinessFunctionModel();

    // businessFunctionModel is never null
    return !businessFunctionModel.getConnectedElements().isEmpty();
  }

  public boolean isHasAssociatedBusinessProcess() {

    BusinessMappingsComponentModel<InformationSystemRelease, BusinessProcess> businessMappingModel = this.getComponentModel()
        .getBusinessMappingModel();

    if (businessMappingModel != null) {

      List<ClusterComponentModelPart<InformationSystemRelease, BusinessProcess>> cParts = businessMappingModel.getClusterParts();

      // Only returns true of there is at least one non-"-" BusinessProcess in this list
      for (ClusterComponentModelPart<InformationSystemRelease, BusinessProcess> cPart : cParts) {

        if (!cPart.getClusteredByBuildingBlock().getName().equals(AbstractHierarchicalEntity.TOP_LEVEL_NAME)) {

          return true;
        }
      }
    }
    return false;
  }

  public boolean isHasAssociatedProject() {

    ManyAssociationSetComponentModel<InformationSystemRelease, Project> projectModel = this.getComponentModel().getProjectModel();

    // projectModel is never null
    return !projectModel.getConnectedElements().isEmpty();
  }

  public boolean isHasAssociatedTechnicalComponents() {

    ManyAssociationSetComponentModel<InformationSystemRelease, TechnicalComponentRelease> technicalModel = this.getComponentModel()
        .getTechnicalComponentModel();

    // technicalModel is never null
    return !technicalModel.getConnectedElements().isEmpty();
  }

  public boolean isHasAssociatedTechnicalComponentsWithArchitecturalDomains() {

    ManyAssociationSetComponentModel<InformationSystemRelease, TechnicalComponentRelease> technicalModel = this.getComponentModel()
        .getTechnicalComponentModel();

    if (technicalModel != null) {

      List<TechnicalComponentRelease> tcrs = technicalModel.getConnectedElements();
      for (TechnicalComponentRelease tcr : tcrs) {

        // The TCR we have doesn't have its ADs loaded in yet, so we have to get the TCR again
        TechnicalComponentRelease tcrLoaded = SpringServiceFactory.getTechnicalComponentReleaseService().loadObjectById(tcr.getId());

        // True if there are any ADs
        Set<ArchitecturalDomain> tcrADs = tcrLoaded.getArchitecturalDomains();
        if (tcrADs != null && !tcrADs.isEmpty()) {

          return true;
        }
      }
    }
    return false;
  }

}

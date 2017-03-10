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

import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.presentation.dialog.common.CanHaveTimeseriesBaseMemBean;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.BusinessMappingsComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.ClusterComponentModelPart;


public class BusinessProcessMemBean extends CanHaveTimeseriesBaseMemBean<BusinessProcess, BusinessProcessComponentModel> {

  /** Serialization version. */
  private static final long             serialVersionUID = 8238262117680338964L;
  private BusinessProcessComponentModel componentModel   = new BusinessProcessComponentModel(ComponentMode.READ);

  public BusinessProcessComponentModel getComponentModel() {
    return componentModel;
  }

  public void setComponentModel(BusinessProcessComponentModel componentModel) {
    this.componentModel = componentModel;
  }

  /**
   * Perform form validation on component model. Is automatically invoked by Spring Webflow when an
   * event occurs in state 'edit'.
   * 
   * @param errors Spring error context
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

  public boolean isHasBusinessFunction() {

    BusinessMappingsComponentModel<BusinessProcess, InformationSystemRelease> isrModel = this.getComponentModel().getBusinessMappingModel();

    if (isrModel != null) {

      List<ClusterComponentModelPart<BusinessProcess, InformationSystemRelease>> cParts = isrModel.getClusterParts();

      // BP and BF aren't connected directly, so: Loop through all ISRs of the BP
      for (ClusterComponentModelPart<BusinessProcess, InformationSystemRelease> cPart : cParts) {

        InformationSystemRelease curIsr = cPart.getClusteredByBuildingBlock();
        Set<BusinessFunction> businessFunctions = curIsr.getBusinessFunctions();

        // Return true if any of the connected ISRs has BFs
        if (businessFunctions != null && !businessFunctions.isEmpty()) {

          return true;
        }
      }
    }
    return false;
  }

  public boolean isHasBusinessUnit() {

    BusinessMappingsComponentModel<BusinessProcess, InformationSystemRelease> isrModel = this.getComponentModel().getBusinessMappingModel();

    // Get our own id to compare later (Is it possible to just get ourselves?) 
    Integer myId = this.getComponentModel().getElementId();

    if (isrModel != null) {

      List<ClusterComponentModelPart<BusinessProcess, InformationSystemRelease>> cParts = isrModel.getClusterParts();

      // BP and BU are connected through a BMap
      for (ClusterComponentModelPart<BusinessProcess, InformationSystemRelease> cPart : cParts) {

        InformationSystemRelease curIsr = cPart.getClusteredByBuildingBlock();
        // The ISR we had doesn't have its data loaded in yet, so we have to get ISR again
        InformationSystemRelease curIsrLoaded = SpringServiceFactory.getInformationSystemReleaseService().loadObjectById(curIsr.getId());
        Set<BusinessMapping> isrBMs = curIsrLoaded.getBusinessMappings();

        // One of the BMaps might have a BU
        for (BusinessMapping isrBM : isrBMs) {

          BusinessProcess bmBP = isrBM.getBusinessProcess();
          BusinessUnit bmBU = isrBM.getBusinessUnit();

          // Return true if any of the connected ISRs has a BM containing both the original element
          // and any BU (except the Top_Level_Name)
          if (bmBP != null && bmBU != null && !bmBU.getNonHierarchicalName().equals(AbstractHierarchicalEntity.TOP_LEVEL_NAME)) {

            Integer bmBPid = bmBP.getId();

            // Make sure the current BM actually relates this BP (to some BU)
            if (bmBPid.equals(myId)) {

              return true;
            }
          }
        }
      }
    }
    return false;
  }
}
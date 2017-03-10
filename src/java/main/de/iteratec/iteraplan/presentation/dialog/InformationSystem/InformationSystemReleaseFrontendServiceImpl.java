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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.SealService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.interfaces.Entity;
import de.iteratec.iteraplan.presentation.dialog.FastExport.FastExportEntryMemBean;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.timeseries.TimeseriesCMBaseFrontendService;


@Service("informationSystemReleaseFrontendService")
public class InformationSystemReleaseFrontendServiceImpl extends
    TimeseriesCMBaseFrontendService<InformationSystemRelease, InformationSystemReleaseMemBean> implements InformationSystemReleaseFrontendService {

  private static final Logger             LOGGER = Logger.getIteraplanLogger(InformationSystemReleaseFrontendServiceImpl.class);

  @Autowired
  private InformationSystemReleaseService isrService;

  @Autowired
  private SealService                     sealService;

  public void setService(InformationSystemReleaseService service) {
    this.isrService = service;
  }

  @Override
  public InformationSystemReleaseService getService() {
    return isrService;
  }

  @Override
  protected String getFlowId() {
    return Dialog.INFORMATION_SYSTEM.getFlowId();
  }

  public InformationSystemReleaseMemBean getMemBean(Integer currentId) {
    InformationSystemReleaseMemBean memBean = super.getMemBean(currentId);

    // FIXME fast export entries don't seem to be used anywhere
    FastExportEntryMemBean entry = new FastExportEntryMemBean();
    memBean.getFastExportEntries().add(entry);
    return memBean;
  }

  public InformationSystemReleaseMemBean getCreateReleaseMemBean(Integer entityId, FlowExecutionContext flowContext) {
    InformationSystemRelease currentISR = getModelObjectById(entityId);

    InformationSystemReleaseMemBean memBean = super.getCreateMemBeanWithDataFromId(entityId);

    memBean.getComponentModel().getPredecessorModel().getConnectedElements().add(0, currentISR);
    return memBean;
  }

  public InformationSystemReleaseMemBean getCopyReleaseMemBean(Integer entityId, FlowExecutionContext flowContext) {
    return super.getCreateMemBeanWithDataFromId(entityId);
  }

  @Override
  public <L extends Entity> void validateOnSave(L newEntity, BuildingBlockService<InformationSystemRelease, Integer> service) {
    if (newEntity instanceof InformationSystemRelease && service instanceof InformationSystemReleaseService) {
      InformationSystemRelease isr = (InformationSystemRelease) newEntity;

      isrService.validateDuplicate(isr.getInformationSystem(), isr);
    }
    else {
      LOGGER.error("Unexpected call of method with parameters \"{0}\" and \"{1}\" instead of a Information System Release and the fitting service.",
          newEntity, service);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
  }

  public Integer saveNewReleaseComponentModel(InformationSystemReleaseMemBean memBean, Integer entityId, FlowExecutionContext flowContext) {
    InformationSystemReleaseComponentModel cm = memBean.getComponentModel();
    cm.update();

    InformationSystemRelease informationSystemRelease = BuildingBlockFactory.createInformationSystemRelease();
    InformationSystemRelease predecessorRelease = this.getModelObjectById(entityId);
    InformationSystem informationSystem = predecessorRelease.getInformationSystem();
    informationSystem.addRelease(informationSystemRelease);

    // Write the data from GUI to the ComponentModel
    cm.configure(informationSystemRelease);

    isrService.validateDuplicate(informationSystem, informationSystemRelease);

    saveTimeseriesComponentModels(informationSystemRelease, memBean);
    isrService.saveOrUpdate(informationSystemRelease);

    leaveEditMode(flowContext);

    cm.setEntity(informationSystemRelease);

    return informationSystemRelease.getId();
  }

  public Integer saveCopyReleaseComponentModel(InformationSystemReleaseMemBean memBean, FlowExecutionContext flowContext) {
    return super.saveNewComponentModel(memBean, null, flowContext); // We don't need a request context in that implementation of saveNewComponentModel
  }

  /**
   * Loads the {@link InformationSystemRelease} and creates new {@link de.iteratec.iteraplan.model.Seal}. 
   * 
   * @param isrId the information system release id, to crete the seal for
   * @param sealComment the seal comment
   */
  public void createSeal(Integer isrId, String sealComment) {
    InformationSystemRelease isr = isrService.loadObjectById(isrId);
    sealService.createSeal(isr, sealComment);
  }

  @Override
  protected InformationSystemRelease createNewModelObject() {
    // Create new Release
    InformationSystemRelease informationSystemRelease = BuildingBlockFactory.createInformationSystemRelease();

    // Create new Container
    InformationSystem informationSystem = BuildingBlockFactory.createInformationSystem();

    // Make them known to each other
    informationSystem.addRelease(informationSystemRelease);
    return informationSystemRelease;
  }

  @Override
  protected InformationSystemReleaseMemBean createNewMemBean() {
    return new InformationSystemReleaseMemBean();
  }

  @Override
  protected InformationSystemReleaseComponentModel createComponentModel(InformationSystemReleaseMemBean memBean, ComponentMode componentMode) {
    InformationSystemReleaseComponentModel cm = new InformationSystemReleaseComponentModel(componentMode);
    memBean.setComponentModel(cm);
    return cm;
  }

  @Override
  protected void prepareBuildingBlockForCopy(InformationSystemRelease buildingBlock) {
    buildingBlock.removeChildren();

    // hide interfaces, because they will not be copied
    buildingBlock.setInterfacesReleaseA(Sets.<InformationSystemInterface> newHashSet());
    buildingBlock.setInterfacesReleaseB(Sets.<InformationSystemInterface> newHashSet());
  }
}

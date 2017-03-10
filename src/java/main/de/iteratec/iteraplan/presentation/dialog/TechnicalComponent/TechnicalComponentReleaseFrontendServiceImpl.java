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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;

import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.interfaces.Entity;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.timeseries.TimeseriesCMBaseFrontendService;


@Service("technicalComponentReleaseFrontendService")
public class TechnicalComponentReleaseFrontendServiceImpl extends
    TimeseriesCMBaseFrontendService<TechnicalComponentRelease, TechnicalComponentReleaseMemBean> implements TechnicalComponentReleaseFrontendService {

  private static final Logger              LOGGER = Logger.getIteraplanLogger(TechnicalComponentReleaseFrontendServiceImpl.class);

  @Autowired
  private TechnicalComponentReleaseService tcrService;

  public void setService(TechnicalComponentReleaseService service) {
    this.tcrService = service;
  }

  @Override
  public TechnicalComponentReleaseService getService() {
    return tcrService;
  }

  @Override
  protected String getFlowId() {
    return Dialog.TECHNICAL_COMPONENT.getFlowId();
  }

  public TechnicalComponentReleaseMemBean getCreateMemBean(FlowExecutionContext flowContext) {
    TechnicalComponentReleaseMemBean memBean = super.getCreateMemBean(flowContext);

    // Make the new TC appear available for interface by default
    memBean.getComponentModel().getAvailableForInterfacesModel().setCurrent(Boolean.TRUE);
    return memBean;
  }

  public TechnicalComponentReleaseMemBean getCreateReleaseMemBean(Integer entityId, FlowExecutionContext flowContext) {
    TechnicalComponentRelease currentTCR = getModelObjectById(entityId);

    TechnicalComponentReleaseMemBean memBean = super.getCreateMemBeanWithDataFromId(entityId);

    memBean.getComponentModel().getPredecessorModel().getConnectedElements().add(0, currentTCR);
    return memBean;
  }

  public TechnicalComponentReleaseMemBean getCopyReleaseMemBean(Integer entityId, FlowExecutionContext flowContext) {
    return super.getCreateMemBeanWithDataFromId(entityId);
  }

  @Override
  public <L extends Entity> void validateOnSave(L newEntity, BuildingBlockService<TechnicalComponentRelease, Integer> service) {
    if (newEntity instanceof TechnicalComponentRelease && service instanceof TechnicalComponentReleaseService) {
      TechnicalComponentRelease tcr = (TechnicalComponentRelease) newEntity;

      tcrService.validateDuplicate(tcr.getTechnicalComponent(), tcr);
    }
    else {
      LOGGER.error("Unexpected call of method with parameters \"{0}\" and \"{1}\" instead of a Technical Component Release and the fitting service.",
          newEntity, service);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
  }

  public Integer saveNewReleaseComponentModel(TechnicalComponentReleaseMemBean memBean, Integer entityId, FlowExecutionContext flowContext) {
    final TechnicalComponentReleaseComponentModel cm = memBean.getComponentModel();
    cm.update();

    TechnicalComponentRelease technicalComponentRelease = BuildingBlockFactory.createTechnicalComponentRelease();
    TechnicalComponent technicalComponent = this.getModelObjectById(entityId).getTechnicalComponent();
    technicalComponent.addRelease(technicalComponentRelease);

    // Write the data from GUI to the ComponentModel
    cm.configure(technicalComponentRelease);

    tcrService.validateDuplicate(technicalComponent, technicalComponentRelease);

    saveTimeseriesComponentModels(technicalComponentRelease, memBean);
    tcrService.saveOrUpdate(technicalComponentRelease);

    leaveEditMode(flowContext);

    cm.setEntity(technicalComponentRelease);

    return technicalComponentRelease.getId();
  }

  public Integer saveCopyReleaseComponentModel(TechnicalComponentReleaseMemBean memBean, FlowExecutionContext flowContext) {
    return super.saveNewComponentModel(memBean, null, flowContext);
  }

  @Override
  protected TechnicalComponentRelease createNewModelObject() {
    // Create new Release
    TechnicalComponentRelease technicalComponentRelease = BuildingBlockFactory.createTechnicalComponentRelease();

    // Create new Container
    TechnicalComponent technicalComponent = BuildingBlockFactory.createTechnicalComponent();

    // Make them known to each other
    technicalComponent.addRelease(technicalComponentRelease);

    return technicalComponentRelease;
  }

  @Override
  protected TechnicalComponentReleaseMemBean createNewMemBean() {
    return new TechnicalComponentReleaseMemBean();
  }

  @Override
  protected TechnicalComponentReleaseComponentModel createComponentModel(TechnicalComponentReleaseMemBean memBean, ComponentMode componentMode) {
    TechnicalComponentReleaseComponentModel cm = new TechnicalComponentReleaseComponentModel(componentMode);
    memBean.setComponentModel(cm);
    return cm;
  }

  @Override
  protected void prepareBuildingBlockForCopy(TechnicalComponentRelease buildingBlock) {
    buildingBlock.removeInformationSystemInterfaces();
  }
}

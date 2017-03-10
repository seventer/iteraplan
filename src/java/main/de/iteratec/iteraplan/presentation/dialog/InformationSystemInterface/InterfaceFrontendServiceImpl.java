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
package de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.timeseries.TimeseriesCMBaseFrontendService;


@Service("interfaceFrontendService")
public class InterfaceFrontendServiceImpl extends TimeseriesCMBaseFrontendService<InformationSystemInterface, InformationSystemInterfaceMemBean> {

  @Autowired
  private InformationSystemInterfaceService service;

  @Autowired
  private InformationSystemReleaseService   isrService;

  @Override
  protected String getFlowId() {
    return Dialog.INTERFACE.getFlowId();
  }

  /**
   * This method is no longer used. The method getCreateMemBeanWithLeftISId(Integer leftISId) is
   * used instead which sets the left information system release to the specified ID. However it
   * must remain here because the Interface requires it.
   */
  public InformationSystemInterfaceMemBean getCreateMemBean(FlowExecutionContext flowContext) {
    throw new NotImplementedException("This method is not implemented for IS Interfaces.");
  }

  /**
   * @param leftISId
   *          the IS on the left hand side which is set by button from create IS, may be null
   * @return the memBean
   */
  public InformationSystemInterfaceMemBean getCreateMemBeanWithLeftISId(Integer leftISId) {
    InformationSystemInterfaceMemBean memBean = createNewMemBean();
    InformationSystemInterface isi = createNewModelObject();

    if (leftISId != null) {
      isi.setInformationSystemReleaseA(isrService.loadObjectById(leftISId));
    }

    InformationSystemInterfaceComponentModel cm = new InformationSystemInterfaceComponentModel(ComponentMode.CREATE);
    cm.initializeFrom(isi);

    if (leftISId != null) {
      cm.getSelectNewModel().setReleaseAId(leftISId);
      cm.getSelectNewModel().setReleaseBId(null);
    }

    memBean.setComponentModel(cm);

    initializeTimeseriesComponentModels(isi, memBean, ComponentMode.CREATE);
    return memBean;
  }

  // This is called in EDIT Mode, when the Interface already exists (Not in Create Mode)
  @Override
  public boolean saveComponentModel(InformationSystemInterfaceMemBean memBean, Integer entityId, RequestContext context,
                                    FlowExecutionContext flowContext) {
    final InformationSystemInterfaceComponentModel cm = memBean.getComponentModel();
    final InformationSystemInterface isi = service.merge(cm.getEntity());

    cm.update();
    // FIXME combine selectModel and selectNewModel into one component model and then discard this method in favor of super.saveComponentModel
    cm.getSelectModel().setReleaseA(cm.getSelectNewModel().getReleaseA());
    cm.getSelectModel().setReleaseB(cm.getSelectNewModel().getReleaseB());

    cm.configure(isi);
    validateOnSave(isi, service);

    saveTimeseriesComponentModels(isi, memBean);
    service.saveOrUpdate(isi);

    leaveEditMode(flowContext);

    return true;
  }

  @Override
  public Integer saveNewComponentModel(InformationSystemInterfaceMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    InformationSystemInterfaceComponentModel cm = memBean.getComponentModel();
    InformationSystemInterface newIsi = BuildingBlockFactory.createInformationSystemInterface();

    cm.update();
    // FIXME combine selectModel and selectNewModel into one component model and then discard this method in favor of super.saveNewComponentModel
    cm.getSelectModel().setReleaseA(cm.getSelectNewModel().getReleaseA());
    cm.getSelectModel().setReleaseB(cm.getSelectNewModel().getReleaseB());

    cm.configure(newIsi);
    validateOnSave(newIsi, service);

    saveTimeseriesComponentModels(newIsi, memBean);
    newIsi = service.saveOrUpdate(newIsi);

    leaveEditMode(flowContext);

    cm.setEntity(newIsi);

    return newIsi.getId();
  }

  public void setService(InformationSystemInterfaceService isiService) {
    this.service = isiService;
  }

  @Override
  public InformationSystemInterfaceService getService() {
    return service;
  }

  /**
   * @param isiCM interface component model
   * @return flow label displayed for the interface.
   */
  public String getFlowLabel(InformationSystemInterfaceComponentModel isiCM) {
    String label = isiCM.getNameModel().getCurrent();
    if (label == null || label.equals("")) {
      label = MessageAccess.getStringOrNull("global.name.unnamed", UserContext.getCurrentLocale());
    }

    if ((StringUtils.isEmpty(label) || label.equals(MessageAccess.getStringOrNull("global.name.unnamed", UserContext.getCurrentLocale())))
        && (isiCM.getSelectModel().getReleaseA() != null && isiCM.getSelectModel().getReleaseB() != null)) {
      label = isiCM.getSelectModel().getReleaseA().getName();
      label = label + " " + isiCM.getTransportInfoModel().getTransportInfo().getTextRepresentation();
      label = label + " " + isiCM.getSelectModel().getReleaseB().getName();
    }
    return label;
  }

  @Override
  protected InformationSystemInterface getModelObjectById(Integer id) {
    InformationSystemInterface modelObject = super.getModelObjectById(id);
    modelObject.setReferenceRelease(modelObject.getInformationSystemReleaseA());
    return modelObject;
  }

  @Override
  protected InformationSystemInterface createNewModelObject() {
    return BuildingBlockFactory.createInformationSystemInterface();
  }

  @Override
  protected InformationSystemInterfaceMemBean createNewMemBean() {
    return new InformationSystemInterfaceMemBean();
  }

  @Override
  protected InformationSystemInterfaceComponentModel createComponentModel(InformationSystemInterfaceMemBean memBean, ComponentMode componentMode) {
    InformationSystemInterfaceComponentModel cm = new InformationSystemInterfaceComponentModel(componentMode);
    memBean.setComponentModel(cm);
    return cm;
  }

  @Override
  protected void prepareBuildingBlockForCopy(InformationSystemInterface buildingBlock) {
    // nothing to do here
  }

}

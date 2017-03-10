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
package de.iteratec.iteraplan.presentation.dialog.BusinessUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.service.BusinessUnitService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.timeseries.TimeseriesCMBaseFrontendService;


/**
 * This Service Implementation offers actions used by Spring Webflow to create, edit and delete
 * business units.
 */
@Service("businessUnitFrontendService")
public class BusinessUnitFrontendServiceImpl extends TimeseriesCMBaseFrontendService<BusinessUnit, BusinessUnitMemBean> {

  @Autowired
  private BusinessUnitService businessUnitService;

  public void setService(BusinessUnitService businessUnitService) {
    this.businessUnitService = businessUnitService;
  }

  @Override
  public BusinessUnitService getService() {
    return businessUnitService;
  }

  public boolean sortEverythingInComponentModel(BusinessUnitMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    updateComponentModel(memBean, context, flowContext); // This is needed so a selected, but not
    // yet added, BB is also sorted
    memBean.getComponentModel().sortEverything();

    return true;
  }

  @Override
  protected String getFlowId() {
    return Dialog.BUSINESS_UNIT.getFlowId();
  }

  @Override
  protected BusinessUnit createNewModelObject() {
    return BuildingBlockFactory.createBusinessUnit();
  }

  @Override
  protected BusinessUnitMemBean createNewMemBean() {
    return new BusinessUnitMemBean();
  }

  @Override
  protected BusinessUnitComponentModel createComponentModel(BusinessUnitMemBean memBean, ComponentMode componentMode) {
    BusinessUnitComponentModel cm = new BusinessUnitComponentModel(componentMode);
    memBean.setComponentModel(cm);
    return cm;
  }

  @Override
  protected void prepareBuildingBlockForCopy(BusinessUnit buildingBlock) {
    buildingBlock.removeAllChildren();
  }

}

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
package de.iteratec.iteraplan.presentation.dialog.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.service.RoleService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService;
import de.iteratec.iteraplan.presentation.dialog.CMBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * This Service Implementation offers actions used by Spring Webflow to create, edit and delete
 * Roles.
 */
@Service("roleFrontendService")
public class RoleFrontendServiceImpl extends CMBaseFrontendServiceImpl<RoleMemBean, RoleService> implements BuildingBlockFrontendService<RoleMemBean> {

  @Autowired
  private RoleService roleService;

  public void setService(RoleService roleService) {
    this.roleService = roleService;
  }

  public RoleService getService() {
    return roleService;
  }

  protected Role getModelObjectById(Integer entityId) {
    Role role = roleService.loadObjectById(entityId);
    // check if the element that the user switched to still exists.
    // if not, inform him about it.
    if (role == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ENTITY_NOT_FOUND_READ);
    }

    return role;
  }

  public RoleMemBean getMemBean(Integer currentId) {
    RoleComponentModel cm = new RoleComponentModel(ComponentMode.READ);
    RoleMemBean memBean = new RoleMemBean();

    Role entity = getModelObjectById(currentId);

    // create a new component model in read mode and initialize it.
    cm.initializeFrom(entity);
    memBean.setComponentModel(cm);
    return memBean;
  }

  public RoleMemBean getEditMemBean(RoleMemBean memBean, Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    Role role = getModelObjectById(entityId);
    RoleComponentModel cm = new RoleComponentModel(ComponentMode.EDIT);
    cm.setRole(role);

    // create a new component model in read mode and initialize it.
    cm.initializeFrom(role);
    memBean.setComponentModel(cm);

    enterEditMode(flowContext);

    return memBean;
  }

  public RoleMemBean getCreateMemBean(FlowExecutionContext flowContext) {
    RoleMemBean memBean = new RoleMemBean();

    Role role = new Role();

    RoleComponentModel cm = new RoleComponentModel(ComponentMode.CREATE);
    cm.initializeFrom(role);

    memBean.setComponentModel(cm);

    return memBean;
  }

  public RoleMemBean getCreateMemBeanWithDataFromId(Integer currentId) {
    RoleComponentModel cm = new RoleComponentModel(ComponentMode.CREATE);
    RoleMemBean memBean = new RoleMemBean();

    Role entity = getModelObjectById(currentId);

    // create a new component model in read mode and initialize it.
    cm.initializeFrom(entity);
    memBean.setComponentModel(cm);
    return memBean;
  }

  public boolean saveComponentModel(RoleMemBean memBean, Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    RoleComponentModel cm = memBean.getComponentModel();
    Role role = roleService.merge(cm.getRole());

    cm.update();
    cm.configure(role);

    validateOnSave(role, roleService);
    roleService.saveOrUpdate(role);

    leaveEditMode(flowContext);

    return true;
  }

  public Integer saveNewComponentModel(RoleMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    RoleComponentModel cm = memBean.getComponentModel();
    Role newRole = new Role();

    cm.update();
    cm.configure(newRole);

    validateOnSave(newRole, roleService);
    newRole = roleService.saveOrUpdate(newRole);

    leaveEditMode(flowContext);

    return newRole.getId();
  }

  public boolean deleteEntity(Integer currentId, RequestContext context, FlowExecutionContext flowContext) {
    Role role = getModelObjectById(currentId);
    dereferenceUsers(role);
    roleService.deleteEntity(role);

    return true;
  }
  
  private void dereferenceUsers(Role role) {
    role.clearUsers();
  }

  @Override
  protected String getFlowId() {
    return Dialog.ROLE.getFlowId();
  }
}

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
package de.iteratec.iteraplan.presentation.dialog.UserGroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.service.UserGroupService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService;
import de.iteratec.iteraplan.presentation.dialog.CMBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * This Service Implementation offers actions used by Spring Webflow to create, edit and delete user
 * groups.
 */
@Service("userGroupFrontendService")
public class UserGroupFrontendServiceImpl extends CMBaseFrontendServiceImpl<UserGroupMemBean, UserGroupService> implements
    BuildingBlockFrontendService<UserGroupMemBean> {

  @Autowired
  private UserGroupService userGroupService;

  public void setService(UserGroupService service) {
    this.userGroupService = service;
  }

  public UserGroupService getService() {
    return userGroupService;
  }

  protected UserGroup getModelObjectById(Integer entityId) {
    UserGroup userGroup = userGroupService.loadObjectById(entityId);
    // check if the element that the user switched to still exists.
    // if not, inform him about it.
    if (userGroup == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ENTITY_NOT_FOUND_READ);
    }

    return userGroup;
  }

  public boolean saveComponentModel(UserGroupMemBean memBean, Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    UserGroupComponentModel cm = memBean.getComponentModel();
    UserGroup userGroup = userGroupService.merge(cm.getUserGroup());

    cm.update();
    cm.configure(userGroup);

    validateOnSave(userGroup, userGroupService);
    userGroupService.saveOrUpdate(userGroup);

    leaveEditMode(flowContext);

    return true;
  }

  public Integer saveNewComponentModel(UserGroupMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    UserGroupComponentModel cm = memBean.getComponentModel();
    UserGroup newUserGroup = new UserGroup();

    cm.update();
    cm.configure(newUserGroup);

    validateOnSave(newUserGroup, userGroupService);
    newUserGroup = userGroupService.saveOrUpdate(newUserGroup);

    leaveEditMode(flowContext);

    return newUserGroup.getId();
  }

  public UserGroupMemBean getMemBean(Integer currentId) {
    UserGroupComponentModel cm = new UserGroupComponentModel(ComponentMode.READ);
    UserGroupMemBean memBean = new UserGroupMemBean();

    UserGroup entity = getModelObjectById(currentId);

    // create a new component model in read mode and initialize it.
    cm.initializeFrom(entity);
    memBean.setComponentModel(cm);
    return memBean;
  }

  public UserGroupMemBean getCreateMemBean(FlowExecutionContext flowContext) {
    UserGroupMemBean memBean = new UserGroupMemBean();

    UserGroup usergroup = new UserGroup();

    UserGroupComponentModel cm = new UserGroupComponentModel(ComponentMode.CREATE);
    cm.initializeFrom(usergroup);

    memBean.setComponentModel(cm);

    return memBean;
  }

  public UserGroupMemBean getCreateMemBeanWithDataFromId(Integer currentId) {
    UserGroupComponentModel cm = new UserGroupComponentModel(ComponentMode.CREATE);
    UserGroupMemBean memBean = new UserGroupMemBean();

    UserGroup entity = getModelObjectById(currentId);

    // create a new component model in read mode and initialize it.
    cm.initializeFrom(entity);
    memBean.setComponentModel(cm);
    return memBean;
  }

  public UserGroupMemBean getEditMemBean(UserGroupMemBean memBean, Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    UserGroup userGroup = getModelObjectById(entityId);
    UserGroupComponentModel cm = new UserGroupComponentModel(ComponentMode.EDIT);
    cm.setUserGroup(userGroup);

    // create a new component model in read mode and initialize it.
    cm.initializeFrom(userGroup);
    memBean.setComponentModel(cm);

    enterEditMode(flowContext);

    return memBean;
  }

  public boolean deleteEntity(Integer currentId, RequestContext context, FlowExecutionContext flowContext) {
    UserGroup userGroup = getModelObjectById(currentId);
    userGroupService.deleteEntity(userGroup);

    return true;
  }

  @Override
  protected String getFlowId() {
    return Dialog.USER_GROUP.getFlowId();
  }
}

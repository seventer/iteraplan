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
package de.iteratec.iteraplan.presentation.dialog.User;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.service.UserService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.UserContext.Permissions;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService;
import de.iteratec.iteraplan.presentation.dialog.CMBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;



@Service("userFrontendService")
public class UserFrontendServiceImpl extends CMBaseFrontendServiceImpl<UserMemBean, UserService> implements BuildingBlockFrontendService<UserMemBean> {

  @Autowired
  private UserService userService;

  public void setService(UserService service) {
    this.userService = service;
  }

  @Override
  public UserService getService() {
    return userService;
  }

  protected User getModelObjectById(Integer entityId) {
    User user = userService.loadObjectById(entityId);
    // check if the element that the user switched to still exists.
    // if not, inform him about it.
    if (user == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ENTITY_NOT_FOUND_READ);
    }

    return user;
  }

  public boolean saveComponentModel(UserMemBean memBean, Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    UserComponentModel cm = memBean.getComponentModel();
    User user = userService.merge(cm.getUser());

    cm.update();
    cm.configure(user);

    validateOnSave(user, userService);
    userService.saveOrUpdate(user);

    leaveEditMode(flowContext);

    return true;
  }

  public UserMemBean getCreateMemBean(FlowExecutionContext flowContext) {
    
    Permissions perms = UserContext.getCurrentPerms();
    
    if(!perms.getUserHasFuncPermUsers()){
      throw new IteraplanTechnicalException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }
    
    UserMemBean memBean = new UserMemBean();

    User user = new User();

    UserComponentModel cm = new UserComponentModel(ComponentMode.CREATE);
    cm.initializeFrom(user);

    memBean.setComponentModel(cm);

    return memBean;
  }

  public UserMemBean getCreateMemBeanWithDataFromId(Integer currentId) {
    Permissions perms = UserContext.getCurrentPerms();
    
    if(!perms.getUserHasFuncPermUsers()){
      throw new IteraplanTechnicalException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }
    
    UserComponentModel cm = new UserComponentModel(ComponentMode.CREATE);
    UserMemBean memBean = new UserMemBean();

    User entity = getModelObjectById(currentId);

    // create a new component model in read mode and initialize it.
    cm.initializeFrom(entity);
    memBean.setComponentModel(cm);
    return memBean;
  }

  public Integer saveNewComponentModel(UserMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    UserComponentModel cm = memBean.getComponentModel();
    cm.update();

    User newUser = new User();
    cm.configure(newUser);

    validateOnSave(newUser, userService);
    newUser = userService.saveOrUpdate(newUser);

    leaveEditMode(flowContext);

    return newUser.getId();
  }

  public UserMemBean getMemBean(Integer currentId) {
    UserComponentModel cm = new UserComponentModel(ComponentMode.READ);
    UserMemBean memBean = new UserMemBean();

    User entity = getModelObjectById(currentId);

    // create a new component model in read mode and initialize it.
    cm.initializeFrom(entity);
    memBean.setComponentModel(cm);
    return memBean;
  }

  public UserMemBean getEditMemBean(UserMemBean memBean, Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    User user = getModelObjectById(entityId);
    UserComponentModel cm = new UserComponentModel(ComponentMode.EDIT);
    cm.setUser(user);

    // create a new component model in read mode and initialize it.
    cm.initializeFrom(user);
    memBean.setComponentModel(cm);

    // Add active dialog to the list of dialogs currently being edited.
    enterEditMode(flowContext);

    return memBean;
  }

  public boolean deleteEntity(Integer currentId, RequestContext context, FlowExecutionContext flowContext) {
    Permissions perms = UserContext.getCurrentPerms();
    
    if(!perms.getUserHasFuncPermUsers()){
      throw new IteraplanTechnicalException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }
    
    User user = getModelObjectById(currentId);
    dereferenceRoles(user);
    userService.deleteEntity(user);

    return true;
  }
  
  private void dereferenceRoles(User user) {
    user.clearRoles();
  }

  @Override
  protected String getFlowId() {
    return Dialog.USER.getFlowId();
  }

  public void validateOnSave(User user, UserService serv) {
    super.validateOnSave(user, serv);
    validateEmail(user);
  }

  private void validateEmail(User user) {
    String email = user.getEmail();
    if (StringUtils.isNotBlank(email) && !email.matches("[ ]*[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}[ ]*")) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INCORRECT_EMAIL_FORMAT);
    }
  }
}

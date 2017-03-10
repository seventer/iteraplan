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
package de.iteratec.iteraplan.presentation.dialog.AttributeTypeGroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.presentation.dialog.CMBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * This Service Implementation offers actions used by Spring Webflow to create, edit and delete
 * attributeTypeGroups.
 */
@Service("attributeTypeGroupFrontendService")
public class AttributeTypeGroupFrontendServiceImpl extends CMBaseFrontendServiceImpl<AttributeTypeGroupMemBean, AttributeTypeGroupService> implements
    AttributeTypeGroupFrontendService {

  @Autowired
  private AttributeTypeGroupService atgService;

  public void setService(AttributeTypeGroupService atgService) {
    this.atgService = atgService;
  }

  @Override
  public AttributeTypeGroupService getService() {
    return atgService;
  }

  protected AttributeTypeGroup getModelObjectById(Integer id) {
    AttributeTypeGroup attributeTypeGroup = atgService.loadObjectById(id);
    // check if the element that the user switched to still exists.
    // if not, inform him about it.
    if (attributeTypeGroup == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ENTITY_NOT_FOUND_READ);
    }
    return attributeTypeGroup;
  }

  public AttributeTypeGroupMemBean getCreateMemBean() {
    AttributeTypeGroupComponentModel cm = new AttributeTypeGroupComponentModel(ComponentMode.CREATE);

    // initialize the cm with the available attributes and permissions
    cm.initializeFrom(new AttributeTypeGroup());

    AttributeTypeGroupMemBean memBean = new AttributeTypeGroupMemBean();
    memBean.setComponentModel(cm);
    return memBean;
  }

  public AttributeTypeGroupMemBean getEditMemBean(Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    AttributeTypeGroup attributeTypeGroup = getModelObjectById(entityId);
    AttributeTypeGroupComponentModel cm = new AttributeTypeGroupComponentModel(ComponentMode.EDIT);
    cm.setAttributeTypeGroup(attributeTypeGroup);
    cm.initializeFrom(attributeTypeGroup);
    AttributeTypeGroupMemBean memBean = new AttributeTypeGroupMemBean();
    memBean.setComponentModel(cm);

    return memBean;
  }

  public boolean sortEverythingInComponentModel(AttributeTypeGroupMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    updateComponentModel(memBean, context, flowContext); // This is needed so a selected, but not
    // yet added, BB is also sorted
    memBean.getComponentModel().sortEverything();

    return true;
  }

  /**
   * <p>
   * Does essentially the same as
   * {@link #saveNewComponentModelAndReturnId(AttributeTypeGroupMemBean, RequestContext, FlowExecutionContext)}
   * without returning any id. <br/>
   * 
   * </p>
   * 
   * @param memBean
   * @param context
   * @param flowContext
   * @return id of the newly saved attributeTypeGroup
   */
  public boolean saveNewComponentModel(AttributeTypeGroupMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    saveNewComponentModelAndReturnId(memBean, context, flowContext);
    return true;
  }

  /**
   * Additional method for AttributeTypeGroups
   * <p>
   * Saves the ComponentModel and returns the id of the just saved {@code AttributeTypeGroup}. <br/>
   * This is necessary to show the saved ATG when jumping back into the controller view.
   * </p>
   * 
   * @param memBean
   * @param context
   * @param flowContext
   * @return id of the newly saved attributeTypeGroup
   */
  public Integer saveNewComponentModelAndReturnId(AttributeTypeGroupMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    AttributeTypeGroupComponentModel cm = memBean.getComponentModel();
    AttributeTypeGroup attributeTypeGroup = new AttributeTypeGroup();
    cm.update();
    cm.configure(attributeTypeGroup);
    Integer currentMaxPos = atgService.getMaxATGPositionNumber();
    attributeTypeGroup.setPosition(Integer.valueOf(currentMaxPos.intValue() + 1));

    validateOnSave(attributeTypeGroup, atgService);
    AttributeTypeGroup savedAtg = atgService.saveOrUpdate(attributeTypeGroup);
    return savedAtg.getId();
  }

  public boolean saveComponentModel(AttributeTypeGroupMemBean memBean, Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    AttributeTypeGroupComponentModel cm = memBean.getComponentModel();
    AttributeTypeGroup attributeTypeGroup = atgService.merge(cm.getAttributeTypeGroup());
    cm.update();
    cm.configure(attributeTypeGroup);

    validateOnSave(attributeTypeGroup, atgService);
    atgService.saveOrUpdate(attributeTypeGroup);

    leaveEditMode(flowContext);

    return true;
  }

  public boolean deleteEntity(AttributeTypeGroupMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    Integer id = memBean.getComponentModel().getChooseAttributeTypeGroupComponentModel().getCurrentId();
    AttributeTypeGroup currentATG = getModelObjectById(id);
    atgService.deleteEntity(currentATG);

    return true;
  }

  @Override
  protected String getFlowId() {
    return Dialog.ATTRIBUTE_TYPE_GROUP.getFlowId();
  }
}

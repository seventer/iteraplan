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
package de.iteratec.iteraplan.presentation.dialog.AttributeType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TypeOfAttribute;
import de.iteratec.iteraplan.presentation.dialog.CMBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.dialog.AttributeType.model.AttributeTypeComponentModel;
import de.iteratec.iteraplan.presentation.dialog.AttributeType.model.AttributeTypeCopyComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


@Service("attributeTypeFrontendService")
public class AttributeTypeFrontendServiceImpl extends CMBaseFrontendServiceImpl<AttributeTypeMemBean, AttributeTypeService> implements
    AttributeTypeFrontendService {

  @Autowired
  private AttributeTypeService service;

  public void setService(AttributeTypeService service) {
    this.service = service;
  }

  public AttributeTypeService getService() {
    return service;
  }

  protected AttributeType getModelObjectById(Integer id) {
    AttributeType attributeType = service.loadObjectById(id);
    // check if the element that the user switched to still exists.
    // if not, inform him about it.
    if (attributeType == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ENTITY_NOT_FOUND_READ);
    }
    return attributeType;
  }

  public AttributeTypeMemBean getMemBean(Integer currentId) {
    AttributeTypeComponentModel cm = new AttributeTypeComponentModel(ComponentMode.READ);
    AttributeTypeMemBean memBean = new AttributeTypeMemBean();

    AttributeType attributeType = getModelObjectById(currentId);

    cm.initializeFrom(attributeType);
    memBean.setComponentModel(cm);

    return memBean;
  }

  public AttributeTypeMemBean getEditMemBean(AttributeTypeMemBean memBean, Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    AttributeType attributeType = getModelObjectById(entityId);
    AttributeTypeComponentModel cm = new AttributeTypeComponentModel(ComponentMode.EDIT);
    cm.setAttributeType(attributeType);
    cm.initializeFrom(attributeType);
    memBean.setComponentModel(cm);

    enterEditMode(flowContext);

    return memBean;
  }

  public AttributeTypeMemBean getCreateMemBean(AttributeTypeMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    String type = memBean.getAttributeTypeToCreate();
    TypeOfAttribute typeOfAttribute = TypeOfAttribute.getTypeOfAttributeFromString(type);

    AttributeType attributeType = createNewAttributeType(typeOfAttribute);

    AttributeTypeComponentModel cm = new AttributeTypeComponentModel(ComponentMode.CREATE);
    cm.initializeFrom(attributeType);

    memBean.setComponentModel(cm);

    enterEditMode(flowContext);

    return memBean;
  }

  private AttributeType createNewAttributeType(TypeOfAttribute typeOfAttribute) {
    AttributeType attributeType = null;
    switch (typeOfAttribute) {
      case DATE:
        attributeType = new DateAT();
        break;
      case ENUM:
        attributeType = new EnumAT();
        break;
      case NUMBER:
        attributeType = new NumberAT();
        break;
      case RESPONSIBILITY:
        attributeType = new ResponsibilityAT();
        break;
      case TEXT:
        attributeType = new TextAT();
        break;
      default:
        throw new IllegalStateException("Unknown type of attribute type: The requested attribute type cannot be created.");
    }
    return attributeType;
  }

  public AttributeTypeMemBean getChooseMemBean(RequestContext context, FlowExecutionContext flowContext) {
    return new AttributeTypeMemBean();
  }

  public boolean saveComponentModel(AttributeTypeMemBean memBean, Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    AttributeTypeComponentModel cm = memBean.getComponentModel();
    AttributeType attributeType = service.merge(cm.getAttributeType());
    cm.update();
    cm.configure(attributeType);

    validateOnSave(attributeType, service);
    service.saveOrUpdate(attributeType);

    leaveEditMode(flowContext);

    return true;
  }

  public Integer saveNewComponentModel(AttributeTypeMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    AttributeTypeComponentModel cm = memBean.getComponentModel();
    cm.update();

    AttributeType newAT = createNewAttributeType(cm.getManagedTypeOfAttribute());

    cm.configure(newAT);
    validateOnSave(newAT, service);
    newAT = service.saveOrUpdate(newAT);

    leaveEditMode(flowContext);

    return newAT.getId();
  }

  public boolean deleteEntity(Integer currentId, RequestContext context, FlowExecutionContext flowContext) {
    AttributeType currentAT = getModelObjectById(currentId);
    service.deleteEntity(currentAT);

    return true;
  }

  @Override
  protected String getFlowId() {
    return Dialog.ATTRIBUTE_TYPE.getFlowId();
  }

  public AttributeTypeMemBean getCreateMemBean(FlowExecutionContext flowContext) {
    throw new UnsupportedOperationException("Attribute types use a different signature for getCreateMemBean, as they depend on a choose type membean");
  }

  public AttributeTypeMemBean getCopyMemBean(Integer templateId, RequestContext context, FlowExecutionContext flowContext) {
    AttributeType attributeType = getModelObjectById(templateId);
    AttributeTypeComponentModel cm = new AttributeTypeCopyComponentModel();
    cm.initializeFrom(attributeType);

    AttributeTypeMemBean memBean = new AttributeTypeMemBean();
    memBean.setComponentModel(cm);

    return memBean;
  }
}

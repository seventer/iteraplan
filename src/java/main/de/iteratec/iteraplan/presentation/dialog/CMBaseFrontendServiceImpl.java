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
package de.iteratec.iteraplan.presentation.dialog;

import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.service.EntityService;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.interfaces.Entity;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.presentation.dialog.common.MemBean;


/**
 * Should be extended by all Frontend-Services.
 */
public abstract class CMBaseFrontendServiceImpl<T extends MemBean<?, ?>, M extends EntityService<?, ?>> extends CommonFrontendServiceImpl implements
    CMBaseFrontendService<T> {

  /**
   * @return the entity service this frontend service is using
   */
  public abstract M getService();

  /**
   * {@inheritDoc}
   */
  public boolean updateComponentModel(T memBean, RequestContext context, FlowExecutionContext flowContext) {
    memBean.getComponentModel().update();
    return true;
  }

  /**
   * Validation for almost all BuildingBlocks and other Entities (Users, Usergroups, Roles,
   * Attributes and Attributegroups). Overridden for Informationsystems and Technicalcomponents in
   * the respective Frontendservices, because these BB need special handling for
   * Release-Validation.
   * 
   * @param newEntity
   *          the entity that should be validated
   * @param service
   *          the service that is used for validation
   */
  public <L extends Entity> void validateOnSave(L newEntity, M service) {
    String name = null;
    // almost all BuildingBlocks are instances of AbstractHierarchicalEntity
    if (newEntity instanceof AbstractHierarchicalEntity<?>) {
      name = ((AbstractHierarchicalEntity<?>) newEntity).getName();
    }
    // BuildingBlocks that are not instances of AbstractHierarchicalEntity
    else if (newEntity instanceof InformationSystemInterface) {
      newEntity.validate();
      // no further validation is necessary
      return;
    }
    // remaining entities that are not BuildingBlocks
    else if (newEntity instanceof AttributeType) {
      name = ((AttributeType) newEntity).getName();
    }
    else if (newEntity instanceof AttributeTypeGroup) {
      name = ((AttributeTypeGroup) newEntity).getName();
    }
    else if (newEntity instanceof User) {
      name = ((User) newEntity).getLoginName();
    }
    else if (newEntity instanceof UserGroup) {
      name = ((UserGroup) newEntity).getName();
    }
    else if (newEntity instanceof Role) {
      name = ((Role) newEntity).getRoleName();
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }

    // validate the entity itself
    newEntity.validate();
    validateName(newEntity, service, name);
  }

  @SuppressWarnings("unchecked")
  private <L extends Entity, K extends EntityService> void validateName(L newEntity, K service, String name) {
    // name should not be null / empty
    if (name == null || name.equals("")) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NAME_CANNOT_BE_EMPTY);
    }
    // check if an entity with the same name already exists
    if (service.doesObjectWithDifferentIdExist(newEntity.getId(), name)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.DUPLICATE_ENTRY, name);
    }
  }

}

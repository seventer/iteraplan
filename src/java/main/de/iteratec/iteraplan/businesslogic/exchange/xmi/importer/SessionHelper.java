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
/**
 * 
 */
package de.iteratec.iteraplan.businesslogic.exchange.xmi.importer;

import java.sql.Clob;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;

import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.businesslogic.service.CustomDashboardInstanceService;
import de.iteratec.iteraplan.businesslogic.service.CustomDashboardTemplateService;
import de.iteratec.iteraplan.businesslogic.service.DateIntervalService;
import de.iteratec.iteraplan.businesslogic.service.RoleService;
import de.iteratec.iteraplan.businesslogic.service.SavedQueryService;
import de.iteratec.iteraplan.businesslogic.service.UserGroupService;
import de.iteratec.iteraplan.businesslogic.service.UserService;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.model.queries.CustomDashboardInstance;
import de.iteratec.iteraplan.model.queries.CustomDashboardTemplate;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.user.DataSource;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.Role2BbtPermission;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.persistence.dao.PermissionAttrTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.PermissionFunctionalDAO;
import de.iteratec.iteraplan.persistence.dao.Role2BbtPermissionDAO;


/**
 * 
 *
 */
public class SessionHelper {
  private BuildingBlockServiceLocator    bbServiceLocator;
  private SessionFactory                 sessionFactory;
  private UserService                    userService;
  private UserGroupService               userGroupService;
  private PermissionFunctionalDAO        permissionFunctionalDAO;
  private Role2BbtPermissionDAO          role2BbtPermissionDAO;
  private RoleService                    roleService;
  private BuildingBlockTypeService       buildingBlockTypeService;
  private PermissionAttrTypeGroupDAO     permissionAttrTypeGroupDAO;
  private AttributeTypeGroupService      attributeTypeGroupService;
  private AttributeTypeService           attributeTypeService;
  private AttributeValueService          attributeValueService;
  private DateIntervalService            dateIntervalService;
  private SavedQueryService              savedQueryService;
  private CustomDashboardInstanceService customDashboardService;
  private CustomDashboardTemplateService customDashboardTemplateService;

  public SessionHelper() {
    //do nothing
  }

  public IdEntity getExistingInstanceFromDB(Class<? extends IdEntity> objectClass, Integer id) {
    Session session = sessionFactory.getCurrentSession();
    if (id != null) {
      return (IdEntity) session.get(objectClass, id);
    }

    return null;
  }

  public Object save(IdEntity entity) {
    if (entity instanceof BuildingBlock) {
      BuildingBlock bb = (BuildingBlock) entity;
      BuildingBlockService<BuildingBlock, Integer> service = bbServiceLocator.getService(bb.getTypeOfBuildingBlock());

      return service.saveOrUpdate(bb);
    }
    else if (entity instanceof User) {
      return userService.saveOrUpdate((User) entity);
    }
    else if (entity instanceof UserGroup) {
      return userGroupService.saveOrUpdate((UserGroup) entity);
    }
    else if (entity instanceof PermissionFunctional) {
      return permissionFunctionalDAO.saveOrUpdate((PermissionFunctional) entity);
    }
    else if (entity instanceof Role) {
      return roleService.saveOrUpdate((Role) entity);
    }
    else if (entity instanceof Role2BbtPermission) {
      return role2BbtPermissionDAO.saveOrUpdate((Role2BbtPermission) entity);
    }
    else if (entity instanceof BuildingBlockType) {
      return buildingBlockTypeService.saveOrUpdate((BuildingBlockType) entity);
    }
    else if (entity instanceof PermissionAttrTypeGroup) {
      return permissionAttrTypeGroupDAO.saveOrUpdate((PermissionAttrTypeGroup) entity);
    }
    else if (entity instanceof AttributeTypeGroup) {
      return attributeTypeGroupService.saveOrUpdate((AttributeTypeGroup) entity);
    }
    else if (entity instanceof AttributeType) {
      return attributeTypeService.saveOrUpdate((AttributeType) entity);
    }
    else if (entity instanceof AttributeValue) {
      return attributeValueService.saveOrUpdate((AttributeValue) entity);
    }
    else if (entity instanceof DateInterval) {
      return dateIntervalService.saveOrUpdate((DateInterval) entity);
    }
    else if (entity instanceof SavedQuery) {
      return savedQueryService.saveOrUpdate((SavedQuery) entity);
    }
    else if (entity instanceof DataSource) {
      System.out.println("Skipping datasource " + ((DataSource) entity).getKey());
      return null;
    }
    else if (entity instanceof CustomDashboardInstance) {
      return customDashboardService.saveCustomDashboardInstance((CustomDashboardInstance) entity);
    }
    else if (entity instanceof CustomDashboardTemplate){
      return customDashboardTemplateService.saveCustomDashboardTemplate((CustomDashboardTemplate) entity);
    }

    final String msg = String.format("The service for entity %s is not supported", entity);
    throw new IllegalArgumentException(msg);
  }

  /**
   * Creates the new {@link Clob} with the specified string {@code value}.
   * 
   * @param value the string value
   * @return the newly created {@link Clob} instance
   */
  public Clob createClob(String value) {
    Session session = sessionFactory.getCurrentSession();
    return Hibernate.createClob(value, session);
  }

  @Required
  public void setBbServiceLocator(BuildingBlockServiceLocator bbServiceLocator) {
    this.bbServiceLocator = bbServiceLocator;
  }

  @Required
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public void setUserGroupService(UserGroupService userGroupService) {
    this.userGroupService = userGroupService;
  }

  @Required
  public void setPermissionFunctionalDAO(PermissionFunctionalDAO permissionFunctionalDAO) {
    this.permissionFunctionalDAO = permissionFunctionalDAO;
  }

  public void setRole2BbtPermissionDAO(Role2BbtPermissionDAO role2BbtPermissionDAO) {
    this.role2BbtPermissionDAO = role2BbtPermissionDAO;
  }

  @Required
  public void setRoleService(RoleService roleService) {
    this.roleService = roleService;
  }

  @Required
  public void setBuildingBlockTypeService(BuildingBlockTypeService buildingBlockTypeService) {
    this.buildingBlockTypeService = buildingBlockTypeService;
  }

  @Required
  public void setPermissionAttrTypeGroupDAO(PermissionAttrTypeGroupDAO permissionAttrTypeGroupDAO) {
    this.permissionAttrTypeGroupDAO = permissionAttrTypeGroupDAO;
  }

  @Required
  public void setAttributeTypeGroupService(AttributeTypeGroupService attributeTypeGroupService) {
    this.attributeTypeGroupService = attributeTypeGroupService;
  }

  @Required
  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }

  @Required
  public void setAttributeValueService(AttributeValueService attributeValueService) {
    this.attributeValueService = attributeValueService;
  }

  @Required
  public void setDateIntervalService(DateIntervalService dateIntervalService) {
    this.dateIntervalService = dateIntervalService;
  }

  @Required
  public void setSavedQueryService(SavedQueryService savedQueryService) {
    this.savedQueryService = savedQueryService;
  }

  @Required
  public void setCustomDashboardService(CustomDashboardInstanceService customDashboardService) {
    this.customDashboardService = customDashboardService;
  }
  
  @Required
  public void setCustomDashboardTemplateService(CustomDashboardTemplateService customDashboardTemplateService) {
    this.customDashboardTemplateService = customDashboardTemplateService;
  }
}

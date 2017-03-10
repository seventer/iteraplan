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
package de.iteratec.iteraplan.businesslogic.service;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.util.Assert;

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;
import de.iteratec.iteraplan.persistence.dao.UserGroupDAO;


/**
 * Implementation of the service interface {@link UserGroupService}.
 */
public class UserGroupServiceImpl extends AbstractEntityService<UserGroup, Integer> implements UserGroupService {

  private AttributeTypeDAO  attributeTypeDAO;
  private UserGroupDAO      userGroupDAO;
  private UserEntityService userEntityService;

  private static final String NAME = "name";

  /** {@inheritDoc} */
  @Override
  public void deleteEntity(UserGroup userGroupToDelete) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.USERGROUP);

    Integer ugId = userGroupToDelete.getId();
    if (ugId == null) {
      throw new IllegalArgumentException("An ID equal to null is not allowed for deletion.");
    }

    UserGroup userGroup = userGroupDAO.loadObjectById(ugId);

    // Check, if the user group is referenced through an attribute of type responsibility.
    List<AttributeType> list = attributeTypeDAO.getResponsibilityAttributeTypesReferencingUserEntityID(ugId);
    if (list.size() > 0) {
      StringBuilder builder = new StringBuilder();
      for (Iterator<AttributeType> iter = list.iterator(); iter.hasNext();) {
        AttributeType at = iter.next();
        builder.append(at.getName());
        if (iter.hasNext()) {
          builder.append(", ");
        }
      }

      throw new IteraplanBusinessException(IteraplanErrorMessages.USERGROUP_REFERENCED_BY_ATTRIBUTE, userGroup.getName(),
          builder.toString());
    }

    // Check if the userGroup is referenced in another userGroup by a transfer of permissions and delete these references (members)
    List<UserGroup> userGroupList = userGroupDAO.loadElementList(NAME);
    if (userGroupList.size() > 0) {
      for (Iterator<UserGroup> iter = userGroupList.iterator(); iter.hasNext();) {
        UserGroup ug = iter.next();
        Set<UserEntity> membersOfUg = ug.getMembers();
        if (membersOfUg.contains(userGroup)) {
          membersOfUg.remove(userGroup);
          UserGroup ugReloaded = userGroupDAO.loadObjectById(ug.getId());
          ugReloaded.setMembers(membersOfUg);
          userGroupDAO.saveOrUpdate(ugReloaded);
        }
      }
      // Remove references to owned building blocks. This is usually done in the corresponding DAO,
      // but the iteraplan architecture denies accessing services from the DAO layer.
      List<BuildingBlock> owned = userEntityService.loadOwnedBuildingBlocks(userGroup, false);
      userGroup.removeOwnedBuildingBlocks(owned);
      userGroupDAO.delete(userGroup);
    }
  }

  /** {@inheritDoc} */
  public List<UserGroup> getAvailableUserGroupsToTransferPermissions(Integer id, List<UserGroup> elementsToExclude) {
    Set<Integer> set = GeneralHelper.createIdSetFromIdEntities(elementsToExclude);

    if (id != null) {
      set.add(id);
      UserGroup group = userGroupDAO.loadObjectById(id);
      set.addAll(group.getUserGroupHierarchyAsIDs());
    }

    // load available elements
    List<UserGroup> list = userGroupDAO.loadFilteredElementList(NAME, set);

    return list;
  }

  /** {@inheritDoc} */
  public UserGroup getUserGroupById(Integer id) {
    return userGroupDAO.loadObjectByIdIfExists(id);
  }

  /** {@inheritDoc} */
  public List<UserGroup> getUserGroupsFiltered(Integer id, List<UserGroup> elementsToExclude) {
    Set<Integer> set = GeneralHelper.createIdSetFromIdEntities(elementsToExclude);
    List<UserGroup> list = userGroupDAO.loadFilteredElementList(NAME, set);

    // add given user group
    if (id != null) {
      UserGroup group = userGroupDAO.loadObjectById(id);
      if (group != null && !list.contains(group)) {
        list.add(group);
      }
    }

    Collections.sort(list);

    return list;
  }

  public void setAttributeTypeDAO(AttributeTypeDAO attributeTypeDAO) {
    this.attributeTypeDAO = attributeTypeDAO;
  }

  public void setUserGroupDAO(UserGroupDAO userGroupDAO) {
    this.userGroupDAO = userGroupDAO;
  }

  public void setUserEntityService(UserEntityService userEntityService) {
    this.userEntityService = userEntityService;
  }

  /** {@inheritDoc} */
  @Override
  protected DAOTemplate<UserGroup, Integer> getDao() {
    return this.userGroupDAO;
  }

  /** {@inheritDoc} */
  public List<UserGroup> getUserGroupsBySearch(UserGroup userGroup) {
    Assert.notNull(userGroup);
    return this.userGroupDAO.findBySearchTerm(userGroup.getName(), NAME);
  }
}
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.dto.BigInstancePermissionsDTO;
import de.iteratec.iteraplan.model.sorting.BuildingBlockComparator;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.persistence.dao.GeneralBuildingBlockDAO;
import de.iteratec.iteraplan.persistence.dao.UserDAO;
import de.iteratec.iteraplan.persistence.dao.UserEntityDAO;


/**
 * Implementation of the service interface {@link InstancePermissionService}.
 */
public class InstancePermissionServiceImpl implements InstancePermissionService {

  private UserDAO                 userDAO;
  private UserEntityDAO           userEntityDAO;
  private GeneralBuildingBlockDAO generalBuildingBlockDAO;
  private UserEntityService       userEntityService;

  /**
   * {@inheritDoc}
   */
  public void addBuildingBlocks(Integer[] identifiers, BigInstancePermissionsDTO dto) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.ELEMENT_SPECIFIC_PERMISSION);

    List<Integer> identifierList = Arrays.asList(identifiers);
    List<Integer> toAdd = new ArrayList<Integer>(identifierList);

    List<BuildingBlock> associatedBuildingBlocks = dto.getAssociatedBuildingBlocks();
    Set<Integer> associatedIdentifiers = new HashSet<Integer>(10);
    for (BuildingBlock block : associatedBuildingBlocks) {
      associatedIdentifiers.add(block.getId());
    }

    // Add only those building blocks that have not yet been associated.
    toAdd.removeAll(associatedIdentifiers);

    for (BuildingBlock block : generalBuildingBlockDAO.loadBuildingBlocks(toAdd)) {
      associatedBuildingBlocks.add(block);
    }

    Collections.sort(associatedBuildingBlocks, new BuildingBlockComparator());
  }

  /** {@inheritDoc} */
  public BigInstancePermissionsDTO getInitialInstancePermissionDTORead(Integer id) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.ELEMENT_SPECIFIC_PERMISSION);

    Integer clone = id != null ? Integer.valueOf(id.intValue()) : null;

    if (clone == null) {
      User user = userDAO.getFirstElement();

      // No user does yet exist (except the system user). Return an empty DTO.
      if (user == null) {

        BigInstancePermissionsDTO dto = new BigInstancePermissionsDTO();
        dto.setCurrentUserEntity(new UserGroup());
        dto.setAvailableUserEntities(new ArrayList<UserEntity>());
        dto.setAssociatedBuildingBlocks(new ArrayList<BuildingBlock>());
        return dto;
      }
      else {
        clone = user.getId();
      }
    }

    UserEntity ue = userEntityDAO.loadObjectById(clone);

    List<BuildingBlock> list = userEntityService.loadOwnedBuildingBlocks(ue, false);

    // Create DTO.
    BigInstancePermissionsDTO dto = new BigInstancePermissionsDTO();
    dto.setCurrentUserEntity(ue);
    dto.setAvailableUserEntities(userDAO.getAllUserEntitiesFiltered(null, null));
    dto.setAssociatedBuildingBlocks(list);

    return dto;
  }

  /** {@inheritDoc} */
  public BigInstancePermissionsDTO getInitialInstancePermissionDTOWrite(Integer id) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.ELEMENT_SPECIFIC_PERMISSION);

    BigInstancePermissionsDTO dto = getInitialInstancePermissionDTORead(id);
    dto.setAvailableQueryTypes(Type.getAllQueryTypes());

    return dto;
  }

  /** {@inheritDoc} */
  public void removeBuildingBlock(Integer id, BigInstancePermissionsDTO dto) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.ELEMENT_SPECIFIC_PERMISSION);

    for (Iterator<BuildingBlock> it = dto.getAssociatedBuildingBlocks().iterator(); it.hasNext();) {
      BuildingBlock block = it.next();
      if (block.getId().equals(id)) {
        it.remove();
        return;
      }
    }
  }

  /** {@inheritDoc} */
  public void saveInstancePermissions(BigInstancePermissionsDTO dto) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.ELEMENT_SPECIFIC_PERMISSION);

    // Update selected user entity with new instance-specific rights.
    Integer selectedUserEntityID = dto.getCurrentUserEntity().getId();
    UserEntity userEntity = userEntityDAO.loadObjectById(selectedUserEntityID);
    updateOwnedBuildingBlocks(userEntity, dto.getAssociatedBuildingBlocks());
    userEntityDAO.saveOrUpdate(userEntity);
  }

  public void setGeneralBuildingBlockDAO(GeneralBuildingBlockDAO generalBuildingBlockDAO) {
    this.generalBuildingBlockDAO = generalBuildingBlockDAO;
  }

  public void setUserDAO(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  public void setUserEntityDAO(UserEntityDAO userEntityDAO) {
    this.userEntityDAO = userEntityDAO;
  }

  public void setUserEntityService(UserEntityService userEntityService) {
    this.userEntityService = userEntityService;
  }

  private void updateOwnedBuildingBlocks(UserEntity userEntity, List<BuildingBlock> associatedBuildingBlocks) {
    Set<Integer> set = new HashSet<Integer>();
    set.add(userEntity.getId());
    List<Integer> ownedBuildingBlockIDs = userEntityDAO.loadOwnedBuildingBlockIDs(set);

    List<Integer> associatedBuildingBlockIDs = new ArrayList<Integer>();
    for (BuildingBlock block : associatedBuildingBlocks) {
      associatedBuildingBlockIDs.add(block.getId());
    }

    // Update building blocks that were removed.
    List<Integer> toRemove = new ArrayList<Integer>(ownedBuildingBlockIDs);
    toRemove.removeAll(associatedBuildingBlockIDs);
    for (BuildingBlock block : generalBuildingBlockDAO.loadBuildingBlocks(toRemove)) {
      block.removeOwningUserEntity(userEntity);
      generalBuildingBlockDAO.saveOrUpdate(block);
    }

    // Update building blocks that were added.
    List<Integer> toAdd = new ArrayList<Integer>(associatedBuildingBlockIDs);
    toAdd.removeAll(ownedBuildingBlockIDs);
    for (BuildingBlock block : generalBuildingBlockDAO.loadBuildingBlocks(toAdd)) {
      block.addOwningUserEntity(userEntity);
      generalBuildingBlockDAO.saveOrUpdate(block);
    }
  }

}
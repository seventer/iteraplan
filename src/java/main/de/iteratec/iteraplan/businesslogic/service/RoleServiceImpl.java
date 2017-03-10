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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.util.Assert;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.collections.EntityToIdFunction;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.dto.PermissionAttrTypeGroupDTO;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.Role2BbtPermission;
import de.iteratec.iteraplan.model.user.Role2BbtPermission.EditPermissionType;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;
import de.iteratec.iteraplan.persistence.dao.PermissionFunctionalDAO;
import de.iteratec.iteraplan.persistence.dao.RoleDAO;


/**
 * Contains methods for managing iteraplan roles.
 */
public class RoleServiceImpl extends AbstractEntityService<Role, Integer> implements RoleService {

  private RoleDAO                 roleDAO;
  private PermissionFunctionalDAO permissionFunctionalDAO;
  private BuildingBlockTypeDAO    buildingBlockTypeDAO;
  private AttributeTypeGroupDAO   attributeTypeGroupDAO;

  @Override
  /** {@inheritDoc} */
  public void deleteEntity(Role role) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.ROLE);
    Integer roleId = role.getId();

    if (roleId == null) {
      throw new IllegalArgumentException("An ID equal to null is not allowed for deletion.");
    }

    if (roleId.equals(roleDAO.getSupervisorRole().getId())) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.CANNOT_EDIT_SUPERVISOR_ROLE, new Object[] { Role.SUPERVISOR_ROLE_NAME });
    }

    roleDAO.delete(role);
  }

  /** 
   * TODO agu - why is this method here and not in {@link BuildingBlockTypeService}? 
   * {@inheritDoc} 
   */
  public List<BuildingBlockType> getAvailableBuildingBlockTypes(List<BuildingBlockType> elementsToExclude) {
    Set<Integer> set = GeneralHelper.createIdSetFromIdEntities(elementsToExclude);
    List<BuildingBlockType> list = buildingBlockTypeDAO.loadFilteredElementList(null, set);

    for (Iterator<BuildingBlockType> it = list.iterator(); it.hasNext();) {
      BuildingBlockType elem = it.next();
      // remove building blocks that should not be displayed
      if (!TypeOfBuildingBlock.DISPLAY.contains(elem.getTypeOfBuildingBlock())) {
        it.remove();
        continue;
      }
    }

    return list;
  }

  /** 
   * TODO agu - do we really need this method?
   * 
   * {@inheritDoc} 
   */
  public List<PermissionFunctional> getAvailableFunctionalPermissions(List<PermissionFunctional> elementsToExclude) {
    Set<Integer> set = GeneralHelper.createIdSetFromIdEntities(elementsToExclude);
    List<PermissionFunctional> list = permissionFunctionalDAO.loadFilteredElementList(null, set);

    return list;
  }

  /** {@inheritDoc} */
  public List<Role> getAvailableRoles(Integer id, List<Role> elementsToExclude) {
    Set<Integer> set = new HashSet<Integer>();

    // ID is null if and only if a new role is created
    if (id != null) {
      if (elementsToExclude.size() > 0) {
        set = GeneralHelper.createIdSetFromIdEntities(elementsToExclude);
      }

      // add currently selected role
      set.add(id);

      // add parents of currently selected role
      Role role = getRoleById(id);
      set.addAll(GeneralHelper.createIdSetFromIdEntities(role.getElementOfRolesAggregated()));
    }

    // return filtered list
    return roleDAO.loadFilteredElementList("roleName", set);
  }

  /** {@inheritDoc} */
  @Override
  public Role getFirstElement() {
    return roleDAO.getFirstElement();
  }

  /** {@inheritDoc} */
  public List<PermissionAttrTypeGroupDTO> getPermissionsForAttributeTypeGroups(List<PermissionAttrTypeGroupDTO> elementsToExclude) {
    Set<Integer> set = new HashSet<Integer>();
    for (PermissionAttrTypeGroupDTO elem : elementsToExclude) {
      set.add(elem.getPermission().getAttrTypeGroup().getId());
    }
    List<AttributeTypeGroup> list = attributeTypeGroupDAO.loadFilteredElementList(null, set);

    List<PermissionAttrTypeGroupDTO> result = new ArrayList<PermissionAttrTypeGroupDTO>();
    for (AttributeTypeGroup atg : list) {
      // create DTO with unique ID (use ID of attribute type group)
      PermissionAttrTypeGroupDTO dto = new PermissionAttrTypeGroupDTO(atg.getId(), new PermissionAttrTypeGroup(null, atg));
      result.add(dto);
    }
    return result;
  }

  /** {@inheritDoc} */
  public Role getRoleById(Integer id) {
    return roleDAO.loadObjectById(id);
  }

  /** {@inheritDoc} */
  public Role getRoleByName(String roleName) {
    return roleDAO.getRoleByName(roleName);
  }

  /** {@inheritDoc} */
  public Role getSupervisorRole() {
    return roleDAO.getSupervisorRole();
  }

  public void setAttributeTypeGroupDAO(AttributeTypeGroupDAO attributeTypeGroupDAO) {
    this.attributeTypeGroupDAO = attributeTypeGroupDAO;
  }

  public void setBuildingBlockTypeDAO(BuildingBlockTypeDAO buildingBlockTypeDAO) {
    this.buildingBlockTypeDAO = buildingBlockTypeDAO;
  }

  public void setPermissionFunctionalDAO(PermissionFunctionalDAO permissionFunctionalDAO) {
    this.permissionFunctionalDAO = permissionFunctionalDAO;
  }

  public void setRoleDAO(RoleDAO roleDAO) {
    this.roleDAO = roleDAO;
  }

  @Override
  protected DAOTemplate<Role, Integer> getDao() {
    return this.roleDAO;
  }

  /** {@inheritDoc} */
  public List<Role> getAllRolesFiltered() {
    return roleDAO.getAllRolesFiltered();
  }

  /** {@inheritDoc} */
  public List<Role> getRolesBySearch(Role role) {
    Assert.notNull(role);
    return this.roleDAO.findBySearchTerm(role.getRoleName(), "roleName");
  }

  /** {@inheritDoc} */
  public List<PermissionFunctional> reloadFunctionalPermissions(Collection<PermissionFunctional> permissions) {
    EntityToIdFunction<PermissionFunctional, Integer> toIdFunction = new EntityToIdFunction<PermissionFunctional, Integer>();
    Iterable<Integer> transform = Iterables.transform(permissions, toIdFunction);

    return permissionFunctionalDAO.loadElementListWithIds(Sets.newHashSet(transform));
  }

  /** {@inheritDoc} */
  public PermissionFunctional getPermissionFunctionalByType(TypeOfFunctionalPermission type) {
    return roleDAO.getPermissionFunctionalByType(type);
  }

  @Override
  /** {@inheritDoc} */
  public Role saveOrUpdate(Role entity) {
    Role savedRole = super.saveOrUpdate(entity);

    // add all elements again, because hashcode has been changed (the permissions got an id from hibernate)
    HashSet<Role2BbtPermission> permissionsBbt = Sets.newHashSet(savedRole.getPermissionsBbt());
    savedRole.removePermissionsBbt();
    savedRole.getPermissionsBbt().addAll(permissionsBbt);

    addInvisibleWriteableBbTypes(savedRole);
    return savedRole;
  }

  /**
   * Some building blocks are closely related, so if the permission for one is given, it is also
   * given to the other. For example, when the user has the permission to create Ipu, he also has
   * the permission to create ipureleases. This method adds permissions for closely related building
   * blocks.
   * 
   * @param role the currently edited role.
   * @throws de.iteratec.iteraplan.common.error.IteraplanException
   */
  private void addInvisibleWriteableBbTypes(Role entity) {
    for (EditPermissionType type : EditPermissionType.values()) {

      Set<TypeOfBuildingBlock> bbtypeSet = new HashSet<TypeOfBuildingBlock>();
      for (BuildingBlockType bbt : entity.getBbtForPermissionType(type)) {
        bbtypeSet.add(bbt.getTypeOfBuildingBlock());
      }

      if (bbtypeSet.contains(TypeOfBuildingBlock.INFORMATIONSYSTEM) && !bbtypeSet.contains(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE)) {
        entity.getPermissionsBbt(TypeOfBuildingBlock.INFORMATIONSYSTEM, type).disconnect();
      }
      if (!bbtypeSet.contains(TypeOfBuildingBlock.INFORMATIONSYSTEM) && bbtypeSet.contains(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE)) {
        BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEM);
        new Role2BbtPermission(entity, bbType, type).connect();
      }
      if (bbtypeSet.contains(TypeOfBuildingBlock.TECHNICALCOMPONENT) && !bbtypeSet.contains(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE)) {
        entity.getPermissionsBbt(TypeOfBuildingBlock.TECHNICALCOMPONENT, type).disconnect();
      }
      if (!bbtypeSet.contains(TypeOfBuildingBlock.TECHNICALCOMPONENT) && bbtypeSet.contains(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE)) {
        BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.TECHNICALCOMPONENT);
        new Role2BbtPermission(entity, bbType, type).connect();
      }
      if (bbtypeSet.contains(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE) && !bbtypeSet.contains(TypeOfBuildingBlock.TRANSPORT)) {
        BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.TRANSPORT);
        new Role2BbtPermission(entity, bbType, type).connect();
      }
      if (!bbtypeSet.contains(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE) && bbtypeSet.contains(TypeOfBuildingBlock.TRANSPORT)) {
        entity.getPermissionsBbt(TypeOfBuildingBlock.TRANSPORT, type).disconnect();
      }
    }
  }
}

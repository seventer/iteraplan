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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.collections.EntityToIdFunction;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.dto.PermissionAttrTypeGroupDTO;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;
import de.iteratec.iteraplan.persistence.dao.PermissionAttrTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.RoleDAO;


/**
 * Implementation of the service interface {@link AttributeTypeGroupService}.
 */
public class AttributeTypeGroupServiceImpl extends AbstractEntityService<AttributeTypeGroup, Integer> implements AttributeTypeGroupService {

  private AttributeTypeGroupDAO      attributeTypeGroupDAO;
  private PermissionAttrTypeGroupDAO permissionAttrTypeGroupDAO;
  private RoleDAO                    roleDAO;
  private AttributeTypeService       attributeTypeService;

  /** {@inheritDoc} */
  @Override
  public void deleteEntity(AttributeTypeGroup atg) {
    UserContext.getCurrentUserContext().getPerms().assureFunctionalPermission(TypeOfFunctionalPermission.ATTRIBUTETYPEGROUP);

    if (AttributeTypeGroup.STANDARD_ATG_NAME.equals(atg.getName())) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.CANNOT_DELETE_STANDARD_ATG);
    }

    moveAttributeTypesToStandardGroup(atg);
    removeAllPermissionsRolesFromATG(atg);

    attributeTypeGroupDAO.delete(atg);
  }

  /**{@inheritDoc}**/
  @Override
  public AttributeTypeGroup saveOrUpdate(AttributeTypeGroup entity) {
    // update the attributes' position values only if the AttributeTypeGroup has got an ID already, i.e. is managed by Hibernate;
    // otherwise we might run into TransientObjectExceptions
    if (entity.getId() != null && entity.getId().intValue() != 0) {
      // force update on the contained AttributeTypes' position, so that their cached position value is accurate
      List<AttributeType> attributeTypes = entity.getAttributeTypes();

      for (int correctPosition = 0; correctPosition < attributeTypes.size(); correctPosition++) {
        AttributeType attr = attributeTypes.get(correctPosition);
        // update the position only if the AttributeType has got an ID already, i.e. is managed by Hibernate; otherwise we might run into
        // TransientObjectExceptions
        if (attr.getId() == null || attr.getId().intValue() == 0) {
          continue;
        }

        attr.setPosition(correctPosition);
        attributeTypeService.saveOrUpdate(attr);
      }
    }

    return super.saveOrUpdate(entity);
  }

  /**
   * All AttributeTypes of the given AttributeTypeGroup are reorganized in the standard
   * AttributeTypeGroup. This has to be done prior to deletion of an AttributeTypeGroup.
   * 
   * @param atg
   *          The AttributeTypeGroup for which all associated AttributeTypes should become member of
   *          the standard AttributeTypeGroup.
   * @throws de.iteratec.iteraplan.common.error.IteraplanException
   */
  private void moveAttributeTypesToStandardGroup(AttributeTypeGroup atg) {
    if (atg.getAttributeTypes() == null || atg.getAttributeTypes().isEmpty()) {
      return;
    }

    AttributeTypeGroup standardATG = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    for (AttributeType at : Lists.newArrayList(atg.getAttributeTypes())) {
      at.setAttributeTypeGroupTwoWay(standardATG);
    }
    atg.getAttributeTypes().clear();
  }

  private void removeAllPermissionsRolesFromATG(AttributeTypeGroup atg) {
    Set<PermissionAttrTypeGroup> permissionATGs = new HashSet<PermissionAttrTypeGroup>(atg.getPermissionsRole());

    for (PermissionAttrTypeGroup permissionATG : permissionATGs) {

      permissionATG.getRole().removePermissionsAttrTypeGroup(permissionATG);
      permissionAttrTypeGroupDAO.delete(permissionATG);
    }
    atg.getPermissionsRole().clear();

  }

  /** {@inheritDoc} */
  public List<AttributeTypeGroup> getAllAttributeTypeGroups() {
    //TODO agu maybe loadElementList() + Collections.sort(results) are sufficient?
    return attributeTypeGroupDAO.getAllAttributeTypeGroups();
  }

  /** {@inheritDoc} */
  public Integer getMaxATGPositionNumber() {
    return attributeTypeGroupDAO.getMaxATGPositionNumber();
  }

  /** {@inheritDoc} */
  public List<PermissionAttrTypeGroupDTO> getPermissionsForRoles(Integer atgId, List<PermissionAttrTypeGroupDTO> elementsToExclude) {
    AttributeTypeGroup atg = new AttributeTypeGroup();
    if (atgId != null) {
      atg = loadObjectById(atgId);
    }

    // extract role IDs from DTO
    Set<Integer> set = new HashSet<Integer>();
    for (PermissionAttrTypeGroupDTO elem : elementsToExclude) {
      set.add(elem.getPermission().getRole().getId());
    }

    List<Role> list = roleDAO.loadFilteredElementList(null, set);
    List<PermissionAttrTypeGroupDTO> result = Lists.newArrayList();

    for (Role role : list) {
      // create DTO with unique ID (use ID of role)
      // the ID is used to reference the object from the GUI
      result.add(new PermissionAttrTypeGroupDTO(role.getId(), new PermissionAttrTypeGroup(null, atg, role)));
    }

    return result;
  }

  /** {@inheritDoc} */
  public AttributeTypeGroup getStandardAttributeTypeGroup() {
    return attributeTypeGroupDAO.getStandardAttributeTypeGroup();
  }

  /** {@inheritDoc} */
  public void updatePosition(AttributeTypeGroup entity, Integer position) {
    if (position == null) {
      throw new IllegalArgumentException("Argument 'position' must not be null.");
    }

    Integer max = attributeTypeGroupDAO.getMaxATGPositionNumber();
    Integer min = attributeTypeGroupDAO.getMinATGPositionNumber();

    // move entity to the end of the list.
    if (position.equals(max)) {
      entity.setPosition(Integer.valueOf(max.intValue() + 1));
    }
    // move entity to the start of the list.
    else if (position.equals(min)) {
      entity.setPosition(Integer.valueOf(min.intValue() - 1));
    }
    // swap positions with the attribute type group formerly occupying
    // the given position.
    else {
      AttributeTypeGroup group = attributeTypeGroupDAO.getAttributeTypeGroupByPosition(position);

      // If there is an attribute type group occupying that position,
      // move it to the former position of the entity.
      if (group != null) {
        group.setPosition(entity.getPosition());
        attributeTypeGroupDAO.saveOrUpdate(group);
      }

      entity.setPosition(position);
    }

    attributeTypeGroupDAO.saveOrUpdate(entity);
  }

  public AttributeTypeGroup getAttributeTypeGroupByName(final String name) {
    return attributeTypeGroupDAO.getAttributeTypeGroupByName(name);
  }

  public void setAttributeTypeGroupDAO(AttributeTypeGroupDAO attributeTypeGroupDAO) {
    this.attributeTypeGroupDAO = attributeTypeGroupDAO;
  }

  public void setPermissionAttrTypeGroupDAO(PermissionAttrTypeGroupDAO permissionAttrTypeGroupDAO) {
    this.permissionAttrTypeGroupDAO = permissionAttrTypeGroupDAO;
  }

  public void setRoleDAO(RoleDAO roleDAO) {
    this.roleDAO = roleDAO;
  }

  @Override
  protected DAOTemplate<AttributeTypeGroup, Integer> getDao() {
    return attributeTypeGroupDAO;
  }

  /** {@inheritDoc} */
  public List<PermissionAttrTypeGroup> reloadPermissionAttrTypeGroups(Collection<PermissionAttrTypeGroup> groups) {
    if ((groups == null) || groups.isEmpty()) {
      return Lists.newArrayList();
    }

    EntityToIdFunction<PermissionAttrTypeGroup, Integer> toIdFunction = new EntityToIdFunction<PermissionAttrTypeGroup, Integer>();
    Iterable<Integer> transform = Iterables.transform(groups, toIdFunction);

    return permissionAttrTypeGroupDAO.loadElementListWithIds(Sets.newHashSet(transform));
  }

  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }
}

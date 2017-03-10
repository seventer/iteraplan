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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import de.iteratec.hibernate.criterion.IteraplanLikeExpression;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.sorting.BuildingBlockComparator;
import de.iteratec.iteraplan.model.sorting.UserEntityIntComparator;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;
import de.iteratec.iteraplan.persistence.dao.GeneralBuildingBlockDAO;
import de.iteratec.iteraplan.persistence.dao.UserDAO;
import de.iteratec.iteraplan.persistence.util.SqlHqlStringUtils;


/**
 * A {@link UserService} implementation for retrieving, creating and deleting the {@link User} entities.
 */
@Service("userService")
@Repository
public class UserServiceImpl extends AbstractEntityService<User, Integer> implements UserService {

  private AttributeTypeDAO        attributeTypeDAO;
  private UserDAO                 userDAO;
  private GeneralBuildingBlockDAO generalBuildingBlockDAO;
  private BuildingBlockTypeDAO    buildingBlockTypeDAO;
  private UserEntityService       userEntityService;

  /** {@inheritDoc}. */
  @Override
  public void deleteEntity(User userToDelete) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.USER);
    User currentUser = UserContext.getCurrentUserContext().getUser();

    Integer userId = userToDelete.getId();
    if (userId == null) {
      throw new IllegalArgumentException("An ID equal to null is not allowed for deletion.");
    }

    User user = userDAO.loadObjectById(userId);
    // The currently logged in user cannot be deleted.
    if ((currentUser != null) && currentUser.getLoginName().equals(user.getLoginName())) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.CANNOT_DELETE_CURRENTLY_LOGGED_IN_USER);
    }

    // Check, if the user is referenced through an attribute of type responsibility.
    List<AttributeType> list = attributeTypeDAO.getResponsibilityAttributeTypesReferencingUserEntityID(userId);

    if (list.size() > 0) {
      StringBuilder attributes = new StringBuilder();
      for (Iterator<AttributeType> iter = list.iterator(); iter.hasNext();) {
        AttributeType at = iter.next();
        attributes.append(at.getName());
        if (iter.hasNext()) {
          attributes.append(", ");
        }
      }

      throw new IteraplanBusinessException(IteraplanErrorMessages.USER_REFERENCED_BY_ATTRIBUTE, user.getFirstName() + " " + user.getLastName(),
          attributes.toString());
    }

    // Remove references to owned building blocks. This is usually done in the corresponding DAO,
    // but the iteraplan architecture denies accessing services from the DAO layer.
    List<BuildingBlock> owned = userEntityService.loadOwnedBuildingBlocks(user, false);
    user.removeOwnedBuildingBlocks(owned);

    List<BuildingBlock> subscribedBbs = loadSubscribedBuildingBlocks(user);
    List<BuildingBlockType> subscribedBbts = loadSubscribedBuildingBlockTypes(user);
    user.removeSubscriptions(subscribedBbs, subscribedBbts);

    userDAO.delete(user);
  }

  /**
   * Loads and returns a {@code List} of {@link BuildingBlock}s for which the specified
   * {@link User} has a subscription. If the given user has not yet been persisted an
   * empty {@code List} is returned. The {@code List} is sorted
   * by the building block's internationalized type string and their identity string.
   * 
   * @param user
   *          The user for which owned building blocks shall be returned.
   * @return See method description.
   */
  private List<BuildingBlock> loadSubscribedBuildingBlocks(User user) {
    // Load the list of owned building block IDs for the complete hierarchy of user entities.
    List<Integer> identifiers = userDAO.loadSubscribedBuildingBlockIDs(user.getId());

    // Load the building blocks itself. Due to the current ORM strategy in iteraplan the objects
    // MUST be loaded separately by mapped class. If the base class was used, a database join too
    // big for MySQL would be generated.
    List<BuildingBlock> subscribed = generalBuildingBlockDAO.loadBuildingBlocks(identifiers);
    Collections.sort(subscribed, new BuildingBlockComparator());

    return subscribed;
  }

  /**
   * Loads and returns a {@code List} of {@link BuildingBlockType}s for which the specified
   * {@link User} has a subscription. If the given user has not yet been persisted an
   * empty {@code List} is returned. The {@code List} is sorted
   * by the building block type's internationalized type string and their identity string.
   * 
   * @param user
   *          The user for which owned building blocks shall be returned.
   * @return See method description.
   */
  private List<BuildingBlockType> loadSubscribedBuildingBlockTypes(User user) {
    // Load the list of owned building block IDs for the complete hierarchy of user entities.
    List<Integer> identifiers = userDAO.loadSubscribedBuildingBlockTypeIDs(user.getId());

    // Load the building blocks itself. Due to the current ORM strategy in iteraplan the objects
    // MUST be loaded separately by mapped class. If the base class was used, a database join too
    // big for MySQL would be generated.
    List<BuildingBlockType> subscribed = buildingBlockTypeDAO.loadElementListWithIds(new HashSet<Integer>(identifiers));

    return subscribed;
  }

  /** {@inheritDoc}. */
  public List<UserEntity> getAggregatedOwningUserEntities(List<UserEntity> owningUserEntities) {
    Set<UserEntity> aggregatedOwningUserEntitiesSet = new HashSet<UserEntity>();
    findAggregatedOwningUserEntities(new HashSet<UserEntity>(owningUserEntities), aggregatedOwningUserEntitiesSet);
    List<UserEntity> aggregatedOwningUserEntitiesList = new ArrayList<UserEntity>(aggregatedOwningUserEntitiesSet);
    Collections.sort(aggregatedOwningUserEntitiesList, new UserEntityIntComparator());

    return aggregatedOwningUserEntitiesList;
  }

  /** {@inheritDoc}. */
  public List<UserEntity> getAllUserEntitiesFiltered(Set<Integer> userEntityIdsToExclude) {
    return userDAO.getAllUserEntitiesFiltered(null, userEntityIdsToExclude);
  }

  /** {@inheritDoc}. */
  public User getUserByLoginIfExists(String loginName) {
    return userDAO.getUserByLoginIfExists(loginName);
  }

  /** {@inheritDoc}. */
  public List<User> getUsersFiltered(Integer id, List<User> elementsToExclude) {
    Set<Integer> set = GeneralHelper.createIdSetFromIdEntities(elementsToExclude);
    List<User> list = userDAO.loadFilteredElementList("loginName", set);

    // add given user group
    if (id != null) {
      User user = userDAO.loadObjectById(id);
      if ((user != null) && !list.contains(user)) {
        list.add(user);
      }
    }

    Collections.sort(list);

    return list;
  }

  private void findAggregatedOwningUserEntities(Set<UserEntity> owningUserEntities, Set<UserEntity> aggregatedOwningUserEntitiesSet) {
    for (UserEntity ue : owningUserEntities) {
      aggregatedOwningUserEntitiesSet.add(ue);
      if (ue instanceof UserGroup) {
        UserGroup ug = (UserGroup) ue;
        findAggregatedOwningUserEntities(ug.getMembers(), aggregatedOwningUserEntitiesSet);
      }
    }
  }

  /** {@inheritDoc}. */
  @Override
  protected DAOTemplate<User, Integer> getDao() {
    return this.userDAO;
  }

  /** {@inheritDoc}. */
  public List<User> getUserBySearch(User user) {
    //FIXME push this down into DAO and additionally escape T-SQL control characters within like criteria 
    DetachedCriteria crit = DetachedCriteria.forClass(User.class);

    if (user != null) {
      if (!StringUtils.isEmpty(user.getFirstName())) {
        String sqlSearchTerm = SqlHqlStringUtils.processGuiFilterForSql(user.getFirstName());
        crit.add(new IteraplanLikeExpression("firstName", sqlSearchTerm, true));
      }

      if (!StringUtils.isEmpty(user.getLastName())) {
        String sqlSearchTerm = SqlHqlStringUtils.processGuiFilterForSql(user.getLastName());
        crit.add(new IteraplanLikeExpression("lastName", sqlSearchTerm, true));
      }
    }
    crit.addOrder(Order.asc("lastName"));
    // don't add order constraint to count criteria!

    return this.userDAO.findByCriteria(crit);
  }

  /** {@inheritDoc} */
  public User createUser(String loginName) {
    // name should not be null / empty
    if (StringUtils.isBlank(loginName)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NAME_CANNOT_BE_EMPTY);
    }

    User user = new User();
    user.setLoginName(loginName);
    user.setFirstName(loginName);
    user.setLastName(loginName);
    user.validate();

    // check if an entity with the same name already exists
    if (doesObjectWithDifferentIdExist(null, loginName)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.DUPLICATE_ENTRY, loginName);
    }

    return saveOrUpdate(user);
  }

  public void setAttributeTypeDAO(AttributeTypeDAO attributeTypeDAO) {
    this.attributeTypeDAO = attributeTypeDAO;
  }

  public void setUserDAO(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  public void setGeneralBuildingBlockDAO(GeneralBuildingBlockDAO generalBuildingBlockDAO) {
    this.generalBuildingBlockDAO = generalBuildingBlockDAO;
  }

  public void setBuildingBlockTypeDAO(BuildingBlockTypeDAO buildingBlockTypeDAO) {
    this.buildingBlockTypeDAO = buildingBlockTypeDAO;
  }

  public void setUserEntityService(UserEntityService userEntityService) {
    this.userEntityService = userEntityService;
  }
}
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
package de.iteratec.iteraplan.persistence.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import de.iteratec.hibernate.criterion.IteraplanLikeExpression;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;


/**
 * Implementation of the DAO interface {@link UserDAO}.
 */
public class UserDAOImpl extends GenericBaseDAO<User, Integer> implements UserDAO {

  private UserGroupDAO  userGroupDAO;
  private UserEntityDAO userEntityDAO;

  /** {@inheritDoc} */
  public List<UserEntity> getAllUserEntitiesFiltered(Integer currentUserEntityId, Set<Integer> userEntityIdsToExclude) {
    List<UserEntity> result = new ArrayList<UserEntity>();
    result.addAll(loadFilteredElementList("loginName", null));
    result.addAll(userGroupDAO.loadFilteredElementList("name", null));
    UserEntity userEntity = userEntityDAO.loadObjectByIdIfExists(currentUserEntityId);
    if (userEntityIdsToExclude != null) {
      for (Iterator<UserEntity> it = result.iterator(); it.hasNext();) {
        UserEntity ue = it.next();
        if (userEntityIdsToExclude.contains(ue.getId())) {
          it.remove();
        }
      }
    }
    if (userEntity != null) {
      boolean addUserEntity = true;
      for (Iterator<UserEntity> it = result.iterator(); it.hasNext();) {
        UserEntity tmp = it.next();
        if (tmp.getId().equals(userEntity.getId())) {
          addUserEntity = false;
          break;
        }
      }
      if (addUserEntity) {
        result.add(userEntity);
      }
    }
    Collections.sort(result);
    return result;
  }

  /** {@inheritDoc} */
  public User getUserByLoginIfExists(final String loginName) {
    if (loginName == null || loginName.length() == 0) {
      return null;
    }

    HibernateCallback<User> callback = new UserByLoginIfExistsCallback(loginName);
    return getHibernateTemplate().execute(callback);
  }

  public void setUserEntityDAO(UserEntityDAO userEntityDAO) {
    this.userEntityDAO = userEntityDAO;
  }

  public void setUserGroupDAO(UserGroupDAO userGroupDAO) {
    this.userGroupDAO = userGroupDAO;
  }

  /** {@inheritDoc} */
  @Override
  protected String getNameAttribute() {
    return "loginName";
  }

  /** {@inheritDoc} */
  @Override
  protected void onBeforeDelete(User entity) {
    super.onBeforeDelete(entity);

    // The removal of owned building blocks is done in the service.
    entity.removeParentUserGroups();
  }

  private static final class UserByLoginIfExistsCallback implements HibernateCallback<User> {
    private final String loginName;

    public UserByLoginIfExistsCallback(String loginName) {
      this.loginName = loginName;
    }

    public User doInHibernate(Session session) {
      Criteria c = session.createCriteria(User.class);
      //FIXME may we use eq here instead of like? 
      c.add(new IteraplanLikeExpression("loginName", loginName, true));
      return (User) c.uniqueResult();
    }
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public List<Integer> loadSubscribedBuildingBlockIDs(final Integer userId) {
    HibernateCallback<List<Integer>> callback = new SubscribedBuildingBlockIDsCallback(userId);

    return getHibernateTemplate().executeFind(callback);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public List<Integer> loadSubscribedBuildingBlockTypeIDs(final Integer userId) {
    HibernateCallback<List<Integer>> callback = new SubscribedBuildingBlockTypeIDsCallback(userId);

    return getHibernateTemplate().executeFind(callback);
  }

  private static final class SubscribedBuildingBlockIDsCallback implements HibernateCallback<List<Integer>> {
    private final Integer userId;

    public SubscribedBuildingBlockIDsCallback(Integer userId) {
      this.userId = userId;
    }

    @SuppressWarnings("unchecked")
    public List<Integer> doInHibernate(Session session) {
      Query q = session.createQuery("select bb.id from UserEntity ue join ue.subscribedBuildingBlocks bb where ue.id = " + userId);

      return q.list();
    }
  }

  private static final class SubscribedBuildingBlockTypeIDsCallback implements HibernateCallback<List<Integer>> {
    private final Integer userId;

    public SubscribedBuildingBlockTypeIDsCallback(Integer userId) {
      this.userId = userId;
    }

    @SuppressWarnings("unchecked")
    public List<Integer> doInHibernate(Session session) {
      Query q = session.createQuery("select bb.id from UserEntity ue join ue.subscribedBuildingBlockTypes bb where ue.id = " + userId);

      return q.list();
    }
  }

}

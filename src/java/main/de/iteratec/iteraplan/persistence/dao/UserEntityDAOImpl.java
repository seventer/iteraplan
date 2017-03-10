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

import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import de.iteratec.iteraplan.persistence.util.SqlHqlStringUtils;
import de.iteratec.iteraplan.model.user.UserEntity;


/**
 * Implementation of the DAO interface {@link UserEntityDAO}.
 */
public class UserEntityDAOImpl extends GenericBaseDAO<UserEntity, Integer> implements UserEntityDAO {

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<Integer> loadOwnedBuildingBlockIDs(final Set<Integer> identifiers) {
    HibernateCallback<List<Integer>> callback = new OwnedBuildingBlockIDsCallback(identifiers);
    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  @Override
  protected String getNameAttribute() {
    throw new UnsupportedOperationException("This operation is not supported for this type.");
  }

  private static final class OwnedBuildingBlockIDsCallback implements HibernateCallback<List<Integer>> {
    private final Set<Integer> identifiers;

    public OwnedBuildingBlockIDsCallback(Set<Integer> identifiers) {
      this.identifiers = identifiers;
    }

    @SuppressWarnings("unchecked")
    public List<Integer> doInHibernate(Session session) {
      Query q = session.createQuery("select bb.id from UserEntity ue join ue.ownedBuildingBlocks bb where "
          + SqlHqlStringUtils.buildHqlConditionWhereElementsInList("ue.id", identifiers, ","));

      return q.list();
    }
  }

}
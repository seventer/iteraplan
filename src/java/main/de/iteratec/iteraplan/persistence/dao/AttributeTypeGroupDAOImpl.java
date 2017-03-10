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

import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.hibernate.criterion.IteraplanLikeExpression;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.persistence.util.SqlHqlStringUtils;


public class AttributeTypeGroupDAOImpl extends GenericBaseDAO<AttributeTypeGroup, Integer> implements AttributeTypeGroupDAO {

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<AttributeTypeGroup> getAllAttributeTypeGroups() {
    HibernateCallback<List<AttributeTypeGroup>> callback = new AllAttributeTypeGroupsCallback();

    // avoid duplicates and sort:
    List<AttributeTypeGroup> results = Lists.newArrayList(Sets.newHashSet(getHibernateTemplate().executeFind(callback)));
    Collections.sort(results);

    return results;
  }

  /** {@inheritDoc} */
  public AttributeTypeGroup getAttributeTypeGroupByPosition(final Integer position) {
    HibernateCallback<AttributeTypeGroup> callback = new AttributeTypeGroupByPositionCallback(position);
    return getHibernateTemplate().execute(callback);
  }

  /** {@inheritDoc} */
  public Integer getMaxATGPositionNumber() {
    HibernateCallback<Integer> callback = new MaxATGPositionNumberCallback();
    Integer position = getHibernateTemplate().execute(callback);
    if (position == null) {
      position = Integer.valueOf(0);
    }
    return position;
  }

  /** {@inheritDoc} */
  public Integer getMinATGPositionNumber() {
    HibernateCallback<Integer> callback = new MinATGPositionNumberCallback();
    Integer position = getHibernateTemplate().execute(callback);
    if (position == null) {
      position = Integer.valueOf(0);
    }
    return position;
  }

  /** {@inheritDoc} */
  public AttributeTypeGroup getStandardAttributeTypeGroup() {
    HibernateCallback<AttributeTypeGroup> callback = new StandardAttributeTypeGroupCallback();
    AttributeTypeGroup standardATG = getHibernateTemplate().execute(callback);
    if (standardATG == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.DATABASE_NOT_INITIALIZED);
    }
    return standardATG;
  }

  public AttributeTypeGroup getAttributeTypeGroupByName(String name) {
    DetachedCriteria c = DetachedCriteria.forClass(AttributeTypeGroup.class);
    //FIXME may we use eq here instead of like?
    c.add(new IteraplanLikeExpression("name", name, true));

    return findByCriteriaUniqueResult(c);
  }

  /** {@inheritDoc} */
  @Override
  protected String getNameAttribute() {
    return "name";
  }

  private static final class AllAttributeTypeGroupsCallback implements HibernateCallback<List<AttributeTypeGroup>> {
    @SuppressWarnings("unchecked")
    public List<AttributeTypeGroup> doInHibernate(Session session) {
      Criteria c = session.createCriteria(AttributeTypeGroup.class);
      c.addOrder(Order.asc("name"));
      return c.list();
    }
  }

  private static final class AttributeTypeGroupByPositionCallback implements HibernateCallback<AttributeTypeGroup> {
    private final Integer position;

    public AttributeTypeGroupByPositionCallback(Integer position) {
      this.position = position;
    }

    public AttributeTypeGroup doInHibernate(Session session) {
      Criteria c = session.createCriteria(AttributeTypeGroup.class);
      c.add(Restrictions.eq("position", position));
      return (AttributeTypeGroup) c.uniqueResult();
    }
  }

  private static final class MaxATGPositionNumberCallback implements HibernateCallback<Integer> {
    public Integer doInHibernate(Session session) {
      Query q = session.getNamedQuery("getMaxATGPositionNumber");
      return (Integer) q.uniqueResult();
    }
  }

  private static final class MinATGPositionNumberCallback implements HibernateCallback<Integer> {
    public Integer doInHibernate(Session session) {
      Query q = session.getNamedQuery("getMinATGPositionNumber");
      return (Integer) q.uniqueResult();
    }
  }

  private static final class StandardAttributeTypeGroupCallback implements HibernateCallback<AttributeTypeGroup> {
    public AttributeTypeGroup doInHibernate(Session session) {
      Query q = session.getNamedQuery("getAttributeTypeGroupByName");
      q.setString("atgName", SqlHqlStringUtils.escapeSqlLikeOperatorWildcards(AttributeTypeGroup.STANDARD_ATG_NAME, session));
      return (AttributeTypeGroup) q.uniqueResult();
    }
  }

}

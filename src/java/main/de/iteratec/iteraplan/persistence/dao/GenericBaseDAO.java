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

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;

import de.iteratec.hibernate.criterion.IteraplanLikeExpression;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.persistence.util.CriteriaUtil;
import de.iteratec.iteraplan.persistence.util.SqlHqlStringUtils;


/**
 * Hibernate implementation of the DAO interface {@link DAOTemplate}.
 * 
 * @param <E>
 *          The type parameter for the concrete entity.
 * @param <T>
 *          The type parameter for the entity's identifier.
 */
public abstract class GenericBaseDAO<E, T extends Serializable> extends HibernateDaoSupport implements DAOTemplate<E, T> {

  private final Class<E>      persistentClass;

  private static final Logger LOGGER = Logger.getIteraplanLogger(GenericBaseDAO.class);

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public GenericBaseDAO() {
    Type type = getClass().getGenericSuperclass();
    while (!(type instanceof ParameterizedType)) {
      type = ((Class) type).getGenericSuperclass();
    }

    type = ((ParameterizedType) type).getActualTypeArguments()[0];
    this.persistentClass = (Class<E>) type;
  }

  /** {@inheritDoc} */
  public void delete(E entity) {
    Assert.notNull(entity);

    LOGGER.debug("Deleting: {0}", entity.getClass().getName());
    onBeforeDelete(entity);
    getHibernateTemplate().delete(entity);
  }

  /** {@inheritDoc} */
  public void deleteAll(Collection<? extends E> entities) {
    Preconditions.checkNotNull(entities);

    if (!entities.isEmpty()) {
      LOGGER.debug("Deleting: {0} of {1}", entities.size(), entities.iterator().next().getClass().getSimpleName());
      getHibernateTemplate().deleteAll(entities);
    }
  }

  /** {@inheritDoc} */
  public boolean doesObjectWithDifferentIdExist(final T identifier, final String name) {
    Assert.notNull(name);

    HibernateCallback<Long> callback = new HibernateCallback<Long>() {
      public Long doInHibernate(Session session) {
        Criteria c = session.createCriteria(getPersistentClass());
        c.setProjection(Projections.count(getNameAttribute()));
        c.add(Restrictions.eq(getNameAttribute(), name.trim()).ignoreCase());
        if (identifier != null) {
          c.add(Restrictions.ne("id", identifier));
        }
        Object result = c.uniqueResult();

        // if Hibernate returns an Integer, convert it to long
        if (result instanceof Integer) {
          return Long.valueOf(((Integer) result).longValue());
        }
        // otherwise we expect a Long to be returned
        return (Long) result;
      }
    };

    Long count = getHibernateTemplate().execute(callback);

    return count.intValue() > 0 ? true : false;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<E> executeNamedQuery(String namedQuery) {
    return getHibernateTemplate().findByNamedQuery(namedQuery);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<E> executeNamedQuery(String namedQuery, String key, Object value) {
    return getHibernateTemplate().findByNamedQueryAndNamedParam(namedQuery, key, value);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<E> executeNamedQuery(String namedQuery, String[] keys, Object[] values) {
    return getHibernateTemplate().findByNamedQueryAndNamedParam(namedQuery, keys, values);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<E> findByCriteria(DetachedCriteria criteria) {
    return getHibernateTemplate().findByCriteria(criteria);
  }

  /** {@inheritDoc} */
  public E getFirstElement() {
    HibernateCallback<E> callback = new HibernateCallback<E>() {

      @SuppressWarnings("unchecked")
      public E doInHibernate(Session session) {
        Criteria c = session.createCriteria(getPersistentClass());
        c.addOrder(Order.asc(getNameAttribute()));
        c.setMaxResults(1);
        return (E) c.uniqueResult();
      }
    };

    return getHibernateTemplate().execute(callback);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<E> loadElementList(final String orderByProperty) {

    HibernateCallback<List<E>> callback = new HibernateCallback<List<E>>() {
      public List<E> doInHibernate(Session session) {
        Criteria c = session.createCriteria(getPersistentClass());
        if (orderByProperty != null) {
          c.addOrder(Order.asc(orderByProperty));
        }
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return c.list();
      }
    };

    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<E> loadElementListWithIds(final Set<T> identifiers) {
    if ((identifiers == null) || identifiers.isEmpty()) {
      return new ArrayList<E>();
    }

    HibernateCallback<List<E>> callback = new HibernateCallback<List<E>>() {
      public List<E> doInHibernate(Session session) {
        Criteria c = session.createCriteria(getPersistentClass());
        c.add(CriteriaUtil.createInRestrictions("id", identifiers));
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        return c.list();
      }
    };

    return getHibernateTemplate().executeFind(callback);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public List<E> loadFilteredElementList(final String property, final Set<T> excludedIds) {

    HibernateCallback<List<E>> callback = new HibernateCallback<List<E>>() {
      public List<E> doInHibernate(Session session) {
        Criteria c = session.createCriteria(getPersistentClass());
        if ((excludedIds != null) && !excludedIds.isEmpty()) {
          c.add(CriteriaUtil.createNotInRestrictions("id", excludedIds));
        }
        if (property != null) {
          c.addOrder(Order.asc(property));
        }
        // without this for some reason multiple instances may be returned,
        // for example when loading AttributeTypeGroup. Strangely enough, when
        // loading AttributeTypeGroups by the HQL query "from AttributeTypeGroup",
        // only distinct instances are returned ?! Note that the distinct part is
        // done programatically by the Criteria implementation, not by the database!
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return c.list();
      }
    };

    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  public E loadObjectById(T id) {
    if (id == null) {
      return null;
    }

    return getHibernateTemplate().load(getPersistentClass(), id);
  }

  /**
   * {@inheritDoc}
   */
  public E loadObjectById(final T id, final String... associations) {
    Preconditions.checkNotNull(id);

    HibernateCallback<E> callback = new HibernateCallback<E>() {
      @SuppressWarnings("unchecked")
      public E doInHibernate(Session session) {
        Criteria criteria = session.createCriteria(getPersistentClass()).add(Restrictions.idEq(id));
        for (String association : associations) {
          criteria.setFetchMode(association, FetchMode.JOIN);
        }
        return (E) criteria.uniqueResult();
      }
    };

    return getHibernateTemplate().execute(callback);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public E loadObjectByIdIfExists(T id) {
    if (id == null) {
      return null;
    }

    Object entity = getHibernateTemplate().get(getPersistentClass(), id);
    if (entity == null) {
      return null;
    }
    if (!getPersistentClass().isAssignableFrom(entity.getClass())) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ENTITY_NOT_FOUND_READ);
    }

    return (E) entity;
  }

  /** {@inheritDoc} */
  public E saveOrUpdate(E entity) {
    Assert.notNull(entity);

    LOGGER.debug("Updating: {0}", entity.getClass().getName());
    getHibernateTemplate().saveOrUpdate(entity);

    return entity;
  }

  /** {@inheritDoc} */
  public Collection<E> saveOrUpdate(Collection<E> entities) {
    Assert.notNull(entities);

    LOGGER.debug("Updating: {0}", entities);
    getHibernateTemplate().saveOrUpdateAll(entities);

    return entities;
  }

  /**
   * Convenience method to return a single instance that matches the query, or null if the query
   * returns no results. <br>
   * <br>
   * Semantic copied from Hibernate Criteria.uniqueResult()
   * 
   * @param detachedCriteria
   *          The detached Criteria that is forwarded to hibernateTemplate
   * @return The object or null if nothing matches the criteria
   */
  @SuppressWarnings("unchecked")
  protected E findByCriteriaUniqueResult(DetachedCriteria detachedCriteria) {
    List<E> list = getHibernateTemplate().findByCriteria(detachedCriteria);
    int size = list.size();
    if (size == 0) {
      return null;
    }

    // if the criteria is not precisely formed, the result list may contain duplicates
    // --> check that all results are equal, in case we got more than one
    E first = list.get(0);
    for (int i = 1; i < size; i++) {
      if (list.get(i) != first) {
        throw new IncorrectResultSizeDataAccessException(1, size);
      }
    }
    return first;
  }

  /**
   * @return The property name in the {@link #getPersistentClass() managed class} which represents the name of such entities.
   */
  protected abstract String getNameAttribute();

  /**
   * Returns the non-generic entity-class the concrete implementation of the DAO represents
   * 
   * @return The class of the entity that is handled by the DAO
   */
  protected Class<E> getPersistentClass() {
    return persistentClass;
  }

  /**
   * This method is called immediately prior to deleting the given entity. It allows for defining
   * additional operations such as manually deleting associated entities if needed. This method may
   * be overriden by subclasses to define specific behaviour. Note that this method should always be
   * called with a call to super().
   * 
   * @param entity The entity to delete.
   */
  protected void onBeforeDelete(E entity) {
    //do nothing
  }

  /** {@inheritDoc} */
  public List<E> findBySearchTerm(final String searchTerm, final String... attribute) {
    return findBySearchTerm(searchTerm, true, attribute);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<E> findBySearchTerm(final String searchTerm, final boolean showInactive, final String... attribute) {

    List<Object> positionalQueryParams = Lists.newArrayList();

    // Build the Filter Statement if there is something to filter
    StringBuilder whereClause = new StringBuilder();
    if (StringUtils.isNotEmpty(searchTerm)) {
      whereClause.append(" WHERE (");
      for (int i = 0; i < attribute.length; i++) {
        whereClause.append("lower(" + attribute[i] + ") like ?");
        // add replacement value for the question mark placeholder
        positionalQueryParams.add(SqlHqlStringUtils.escapeSqlLikeSquareBrackets(SqlHqlStringUtils.processGuiFilterForSql(searchTerm.toLowerCase()),
            getSession()));
        if (i < attribute.length - 1) {
          whereClause.append(" OR ");
        }
      }
      whereClause.append(")");
    }

    if (!showInactive) {
      whereClause.append(whereClause.length() == 0 ? " WHERE" : " AND");
      whereClause.append(" typeOfStatus != '");
      whereClause.append(InformationSystemRelease.TypeOfStatus.INACTIVE);
      whereClause.append('\'');
    }

    // Build the Order Statement
    StringBuilder orderClause = new StringBuilder(" ORDER BY ");
    for (int i = 0; i < attribute.length; i++) {
      orderClause.append("entity." + attribute[i]);
      if (i < attribute.length - 1) {
        orderClause.append(", ");
      }
    }

    String className = persistentClass.getSimpleName();
    String query = "FROM " + className + " entity" + whereClause + orderClause;

    return getHibernateTemplate().find(query, positionalQueryParams.toArray());
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<E> getSubscribedElements() {
    HibernateCallback<List<E>> callback = new HibernateCallback<List<E>>() {
      public List<E> doInHibernate(Session session) {
        Criteria c = session.createCriteria(getPersistentClass());
        c.createCriteria("subscribedUsers").add(Restrictions.idEq(UserContext.getCurrentUserContext().getUser().getId()));

        return c.list();
      }
    };

    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  public E merge(E entity) {
    return getHibernateTemplate().merge(entity);
  }

  /** {@inheritDoc} */
  public List<E> findByNames(Set<String> names) {
    if (names.isEmpty()) {
      return Collections.emptyList();
    }

    DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
    Disjunction disjunction = Restrictions.disjunction();
    for (String name : names) {
      //FIXME will eq do the trick here too or do we need like?
      //if like is needed we should use the IteraplanLikeExpression
      //      disjunction.add(Restrictions.like(getNameAttribute(), name, MatchMode.EXACT).ignoreCase());
      disjunction.add(new IteraplanLikeExpression(getNameAttribute(), name, true));
    }
    criteria.add(disjunction);

    return findByCriteria(criteria);
  }

  /** {@inheritDoc} */
  public void clearSession() {
    getHibernateTemplate().getSessionFactory().getCurrentSession().clear();
  }
}
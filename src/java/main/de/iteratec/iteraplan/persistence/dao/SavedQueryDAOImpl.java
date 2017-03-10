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

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.queries.CustomDashboardTemplate;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.queries.SavedQueryEntity;


public class SavedQueryDAOImpl extends HibernateDaoSupport implements SavedQueryDAO {

  /** Serialization version. */
  private static final long serialVersionUID = 3026536002012903157L;

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public <T extends SavedQueryEntity> List<T> getSavedQueries(final Class<T> clazz) {
    HibernateCallback<List<T>> callback = new SavedQueriesCallback1<T>(clazz);
    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public <T extends SavedQueryEntity> List<T> getSavedQueries(final Class<T> clazz, Set<ReportType> reportTypes) {
    HibernateCallback<List<T>> callback = new SavedQueriesCallback2<T>(clazz, reportTypes, false);
    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public <T extends SavedQueryEntity> List<T> getSavedQueriesWithoutContent(final Class<T> clazz, Set<ReportType> reportTypes) {
    HibernateCallback<List<T>> callback = new SavedQueriesCallback2<T>(clazz, reportTypes, true);
    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public <T extends SavedQueryEntity> List<T> getSavedQueries(final Class<T> clazz, final Map<String, String> properties) {
    HibernateCallback<List<T>> callback = new SavedQueriesCallback3<T>(properties, clazz);
    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  public SavedQueryEntity saveOrUpdate(SavedQueryEntity savedQuery) {
    if (savedQuery.getName() == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.ILLEGAL_FILE_NAME_EXCEPTION);
    }
    getHibernateTemplate().saveOrUpdate(savedQuery);
    return savedQuery;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("boxing")
  public <T extends SavedQueryEntity> T loadObjectByIdIfExists(Integer id, Class<T> clazz) {
    if (id == null || id <= 0) {
      return null;
    }

    return getHibernateTemplate().get(clazz, id);
  }

  /** {@inheritDoc} */
  public <T extends SavedQueryEntity> void delete(T entity) {
    getHibernateTemplate().delete(entity);
  }

  /**{@inheritDoc}**/
  @SuppressWarnings("unchecked")
  public <T extends SavedQueryEntity> List<T> getSavedQueriesForDashboardTemplates(Set<ReportType> reportTypes) {
    HibernateCallback<List<T>> callback = new SavedQueriesForDashboardTemplatesCallback<T>(reportTypes);
    return getHibernateTemplate().executeFind(callback);
  }

  private static final class SavedQueriesCallback1<T> implements HibernateCallback<List<T>> {
    private final Class<T> clazz;

    public SavedQueriesCallback1(Class<T> clazz) {
      this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    public List<T> doInHibernate(Session session) {
      return session.createCriteria(clazz).addOrder(Order.asc("id")).list();
    }
  }

  private static final class SavedQueriesCallback2<T> implements HibernateCallback<List<T>> {
    private final Class<T>        clazz;
    private final Set<ReportType> reportTypes;
    private final boolean         loadClobs;

    public SavedQueriesCallback2(Class<T> clazz, Set<ReportType> reportTypes, boolean loadClobs) {
      this.clazz = clazz;
      this.reportTypes = reportTypes;
      this.loadClobs = loadClobs;
    }

    @SuppressWarnings("unchecked")
    public List<T> doInHibernate(Session session) {
      Criteria criteria = session.createCriteria(clazz);

      if (loadClobs) {
        ProjectionList properties = Projections.projectionList();
        properties.add(Projections.property("id"), "id");
        properties.add(Projections.property("name"), "name");
        properties.add(Projections.property("description"), "description");
        properties.add(Projections.property("type"), "type");
        properties.add(Projections.property("resultBbType"), "resultBbType");
        criteria.setProjection(properties);

        criteria.setResultTransformer(new AliasToBeanResultTransformer(clazz));
      }

      criteria.add(Restrictions.in("this.type", reportTypes));
      criteria.addOrder(Order.asc("name"));
      return criteria.list();
    }
  }

  private static final class SavedQueriesCallback3<T> implements HibernateCallback<List<T>> {
    private final Map<String, String> properties;
    private final Class<T>            clazz;

    public SavedQueriesCallback3(Map<String, String> properties, Class<T> clazz) {
      this.properties = properties;
      this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    public List<T> doInHibernate(Session session) {
      Criteria crit = session.createCriteria(clazz);
      for (String key : properties.keySet()) {
        crit.add(Restrictions.eq(key, properties.get(key)));
      }
      crit.addOrder(Order.asc("id"));

      return crit.list();
    }
  }

  private static final class SavedQueriesForDashboardTemplatesCallback<T> implements HibernateCallback<List<T>> {
    private final Set<ReportType> reportTypes;

    public SavedQueriesForDashboardTemplatesCallback(Set<ReportType> reportTypes) {
      this.reportTypes = reportTypes;
    }

    /**{@inheritDoc}**/
    @SuppressWarnings("unchecked")
    public List<T> doInHibernate(Session session) throws SQLException {
      Criteria criteria = session.createCriteria(SavedQuery.class);
      criteria.createAlias("this.resultBbType", "bbt");
      
      Criteria dashboardCrit = session.createCriteria(CustomDashboardTemplate.class);
      dashboardCrit.setProjection( Projections.groupProperty("buildingBlockType").as("bbt") );
      
      List<Integer> dashboards = dashboardCrit.list();
      
      Criterion crit = null;
      if (dashboards.size() > 0) {
        crit = Restrictions.and(Restrictions.in("this.type", reportTypes), Restrictions.in("this.resultBbType", dashboards));
      }
      else {
        crit = Restrictions.in("this.type", reportTypes);
      }  
      criteria.add(crit);
      criteria.addOrder(Order.asc("name"));
      return criteria.list();
    }
  }
}

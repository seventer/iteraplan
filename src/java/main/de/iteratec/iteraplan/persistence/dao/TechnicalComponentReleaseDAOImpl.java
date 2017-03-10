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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.persistence.util.CriteriaUtil;


/**
 * Implementation of {@link TechnicalComponentReleaseDAO}.
 */
public class TechnicalComponentReleaseDAOImpl extends GenericBaseDAO<TechnicalComponentRelease, Integer> implements TechnicalComponentReleaseDAO {

  /** {@inheritDoc} */
  @Override
  public boolean doesObjectWithDifferentIdExist(Integer id, String name) {
    String[] nameParts = GeneralHelper.getPartsOfReleaseName(name);
    return doesReleaseWithDifferentIdExist(id, nameParts[0], nameParts[1]);
  }

  /** {@inheritDoc} */
  public boolean doesReleaseExist(final String name, final String version) {
    return doesReleaseWithDifferentIdExist(null, name, version);
  }

  private boolean doesReleaseWithDifferentIdExist(Integer id, final String name, final String version) {
    if (name == null) {
      throw new IllegalArgumentException("Parameters 'name' is required.");
    }

    HibernateCallback<List<TechnicalComponentRelease>> callback = new DoesReleaseExistCallback(name, version);
    List<TechnicalComponentRelease> results = getHibernateTemplate().execute(callback);

    Iterator<TechnicalComponentRelease> resultIter = results.iterator();
    while (resultIter.hasNext()) {
      TechnicalComponentRelease isr = resultIter.next();
      // as the technical component releases from the results list were loaded from the database,
      // we can assume they have an ID != null
      if (isr.getId().equals(id)) {
        resultIter.remove();
      }
    }
    return !results.isEmpty();
  }

  /** {@inheritDoc} */
  public List<TechnicalComponentRelease> eligibleForConnections(final List<TechnicalComponentRelease> elementsToExclude, final boolean showInactive) {
    List<TechnicalComponentRelease> list = filter(elementsToExclude, showInactive);
    for (Iterator<TechnicalComponentRelease> iter = list.iterator(); iter.hasNext();) {
      TechnicalComponentRelease release = iter.next();
      if (!release.getTechnicalComponent().isAvailableForInterfaces()) {
        iter.remove();
      }
    }

    return list;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<TechnicalComponentRelease> filter(final List<TechnicalComponentRelease> elementsToExclude, final boolean showInactive) {
    HibernateCallback<List<TechnicalComponentRelease>> callback = new FilterCallback(showInactive, elementsToExclude);
    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  @Override
  public TechnicalComponentRelease getFirstElement() {
    boolean showInactive = UserContext.getCurrentUserContext().isShowInactiveStatus();
    return getFirstElement(showInactive);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("boxing")
  public boolean isDuplicateTechnicalComponent(final String name, final Integer identifier) {
    if (name == null) {
      throw new IllegalArgumentException("The parameter 'name' is required.");
    }

    HibernateCallback<Long> callback = new IsDuplicateTechnicalComponentCallback(name, identifier);
    Long count = getHibernateTemplate().execute(callback);

    return count > 0 ? true : false;
  }

  private TechnicalComponentRelease getFirstElement(final boolean showInactive) {
    HibernateCallback<TechnicalComponentRelease> callback = new GetFirstElementCallback(showInactive);

    return getHibernateTemplate().execute(callback);
  }

  @SuppressWarnings("unchecked")
  public List<TechnicalComponentRelease> getReleasesWithSuccessors(final boolean showInactive) {
    HibernateCallback<List<TechnicalComponentRelease>> callback = new ReleasesWithSuccessorsCallback(showInactive);

    List<TechnicalComponentRelease> tcrs = getHibernateTemplate().executeFind(callback);
    Collections.sort(tcrs);

    return tcrs;
  }

  /** {@inheritDoc} */
  @Override
  protected String getNameAttribute() {
    throw new UnsupportedOperationException("This operation is not supported for this type.");
  }

  /** {@inheritDoc} */
  @Override
  protected void onBeforeDelete(TechnicalComponentRelease entity) {
    super.onBeforeDelete(entity);

    entity.removeArchitecturalDomains();
    entity.removeInformationSystemReleases();
    entity.removeInformationSystemInterfaces();
    entity.removeSuccessors();
    entity.removePredecessors();
    entity.removeBaseComponents();
    entity.removeParentComponents();
  }

  private static final class DoesReleaseExistCallback implements HibernateCallback<List<TechnicalComponentRelease>> {
    private final String name;
    private final String version;

    public DoesReleaseExistCallback(String name, String version) {
      this.name = name;
      this.version = version;
    }

    @SuppressWarnings("unchecked")
    public List<TechnicalComponentRelease> doInHibernate(Session session) {

      Criteria c = session.createCriteria(TechnicalComponentRelease.class, "tcr");
      if ((version != null) && !version.equals("")) {
        c.add(Restrictions.eq("tcr.version", version).ignoreCase());
      }
      c.createAlias("technicalComponent", "tc");
      c.add(Restrictions.eq("tc.name", name).ignoreCase());
      // Should be uniqueResult, list is used to work with old (inconsistent) data
      return c.list();
    }
  }

  private static final class FilterCallback implements HibernateCallback<List<TechnicalComponentRelease>> {
    private final boolean                         showInactive;
    private final List<TechnicalComponentRelease> elementsToExclude;

    public FilterCallback(boolean showInactive, List<TechnicalComponentRelease> elementsToExclude) {
      this.showInactive = showInactive;
      this.elementsToExclude = elementsToExclude;
    }

    @SuppressWarnings("unchecked")
    public List<TechnicalComponentRelease> doInHibernate(Session session) {

      Criteria c = session.createCriteria(TechnicalComponentRelease.class);
      if ((elementsToExclude != null) && !elementsToExclude.isEmpty()) {
        Set<Integer> set = GeneralHelper.createIdSetFromIdEntities(elementsToExclude);
        c.add(CriteriaUtil.createNotInRestrictions("id", set));
      }

      if (!showInactive) {
        c.add(Restrictions.ne("typeOfStatus", TechnicalComponentRelease.TypeOfStatus.INACTIVE));
      }

      return c.list();
    }
  }

  private static final class IsDuplicateTechnicalComponentCallback implements HibernateCallback<Long> {
    private final String  name;
    private final Integer identifier;

    public IsDuplicateTechnicalComponentCallback(String name, Integer identifier) {
      this.name = name;
      this.identifier = identifier;
    }

    public Long doInHibernate(Session session) {
      String q = "select count(id) from TechnicalComponent where name = :name";
      if (identifier != null) {
        q += " and id != :id";
      }

      Query query = session.createQuery(q);
      query.setParameter("name", name);
      if (identifier != null) {
        query.setParameter("id", identifier);
      }

      return (Long) query.uniqueResult();
    }
  }

  private static final class GetFirstElementCallback implements HibernateCallback<TechnicalComponentRelease> {
    private final boolean showInactive;

    public GetFirstElementCallback(boolean showInactive) {
      this.showInactive = showInactive;
    }

    @SuppressWarnings("unchecked")
    public TechnicalComponentRelease doInHibernate(Session session) throws HibernateException, SQLException {
      Query q = createLoadAllTechnicalComponentReleasesQuery(session);

      List<TechnicalComponentRelease> list = q.setMaxResults(1).list();
      if (!list.isEmpty()) {
        return list.get(0);
      }

      return null;
    }

    private Query createLoadAllTechnicalComponentReleasesQuery(Session session) {
      Query q;
      if (showInactive) {
        q = session.getNamedQuery("getFilteredTechnicalComponentReleasesAll");
      }
      else {
        q = session.getNamedQuery("getFilteredTechnicalComponentReleases");
        q.setParameter("ignoreTypeOfStatus", TechnicalComponentRelease.TypeOfStatus.INACTIVE);
      }
      q.setString("filter", "%");
      q.setString("empty", "");
      return q;

    }
  }

  private static final class ReleasesWithSuccessorsCallback implements HibernateCallback<List<TechnicalComponentRelease>> {
    private final boolean showInactive;

    public ReleasesWithSuccessorsCallback(boolean showInactive) {
      this.showInactive = showInactive;
    }

    @SuppressWarnings("unchecked")
    public List<TechnicalComponentRelease> doInHibernate(Session session) throws HibernateException, SQLException {
      String query = "from TechnicalComponentRelease tcr " + " where ( tcr.predecessors.size > 0 or tcr.successors.size > 0 )";
      if (!showInactive) {
        query += " and tcr.typeOfStatus not like :tos ";
      }
      Query q = session.createQuery(query);
      if (!showInactive) {
        q.setParameter("tos", TechnicalComponentRelease.TypeOfStatus.INACTIVE);
      }
      return q.list();
    }
  }

}
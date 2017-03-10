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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.persistence.util.CriteriaUtil;


/**
 * Implementation of the DAO interface {@link InformationSystemReleaseDAO}.
 */
public class InformationSystemReleaseDAOImpl extends GenericBaseDAO<InformationSystemRelease, Integer> implements InformationSystemReleaseDAO {

  /** 
   * Checks if the {@link InformationSystemRelease} with the specified {@code id} and {@code name} exists. If the 
   * specified {@code id} equals {@code null}, the result of {@link #doesReleaseExist(String, String)} will be returned.
   * 
   * @param id the information system release id
   * @param name the information system release name and version. For example {@code 'ISR#1.0'}
   * @return {@code true} if the information system release exists, {@code false} otherwise
   */
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

    HibernateCallback<List<InformationSystemRelease>> callback = new DoesReleaseExistCallback(name, version);
    List<InformationSystemRelease> results = getHibernateTemplate().execute(callback);

    Iterator<InformationSystemRelease> resultIter = results.iterator();
    while (resultIter.hasNext()) {
      InformationSystemRelease isr = resultIter.next();
      // as the information system releases from the results list were loaded from the database,
      // we can assume they have an ID != null
      if (isr.getId().equals(id)) {
        resultIter.remove();
      }
    }
    return !results.isEmpty();
  }

  /** {@inheritDoc} */
  @Override
  public InformationSystemRelease getFirstElement() {
    boolean showInactive = UserContext.getCurrentUserContext().isShowInactiveStatus();
    HibernateCallback<InformationSystemRelease> callback = new GetFirstElementCallback(showInactive);

    return getHibernateTemplate().execute(callback);
  }

  /** {@inheritDoc} */
  public List<InformationSystemRelease> filter(final List<InformationSystemRelease> elementsToExclude, final boolean showInactive) {
    Set<Integer> set = GeneralHelper.createIdSetFromIdEntities(elementsToExclude);
    return filterWithIds(set, showInactive);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<InformationSystemRelease> filterWithIds(final Set<Integer> elementsToExclude, final boolean showInactive) {
    HibernateCallback<List<InformationSystemRelease>> callback = new FilterWithIdsCallback(elementsToExclude, showInactive);
    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<InformationSystemRelease> getInformationSystemReleasesWithConnections(final boolean showInactive) {
    HibernateCallback<List<InformationSystemRelease>> callback = new InformationSystemReleasesWithConnectionsCallback(showInactive);

    List<InformationSystemRelease> isReleases = getHibernateTemplate().executeFind(callback);
    Collections.sort(isReleases, new HierarchicalEntityCachingComparator<InformationSystemRelease>());

    return isReleases;
  }

  /** {@inheritDoc} */
  public List<InformationSystemRelease> getOutermostInformationSystemReleases() {
    return executeNamedQuery("getOutermostInformationSystemReleases");
  }

  /** {@inheritDoc} */
  @SuppressWarnings("boxing")
  public boolean isDuplicateInformationSystem(final String name, final Integer identifier) {
    if (name == null) {
      throw new IllegalArgumentException("The parameter 'name' is required.");
    }

    HibernateCallback<Long> callback = new IsDuplicateInformationSystemCallback(name, identifier);

    Long count = getHibernateTemplate().execute(callback);

    return count > 0 ? true : false;
  }

  /** {@inheritDoc} */
  @Override
  protected String getNameAttribute() {
    throw new UnsupportedOperationException("This operation is not supported for this type.");
  }

  /** {@inheritDoc} */
  @Override
  protected void onBeforeDelete(InformationSystemRelease entity) {
    // Find all descendants of entity
    Set<InformationSystemRelease> descendants = new HashSet<InformationSystemRelease>(0);
    entity.getDescendants(entity, descendants);

    super.onBeforeDelete(entity);

    // Remove all successors of descendants and descendants themselves.
    for (InformationSystemRelease isr : descendants) {
      cleanIsr(isr);

      getHibernateTemplate().delete(isr);
    }

    cleanIsr(entity);

    // Remove the parent only one-way to avoid problems with cascading deletes for children.
    entity.setParent(null);
  }

  private void cleanIsr(InformationSystemRelease isr) {
    isr.removeInfrastructureElements();
    isr.removeTechnicalComponentReleases();
    isr.removeSuccessors();
    isr.removePredecessors();
    isr.removeProjects();
    isr.removeInformationSystemDomains();
    isr.removeBaseComponents();
    isr.removeParentComponents();
    isr.removeInterfacesReleaseA();
    isr.removeInterfacesReleaseB();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<InformationSystemRelease> getReleasesWithSuccessors(final boolean showInactive) {
    HibernateCallback<List<InformationSystemRelease>> callback = new InformationsystemsWithSuccessorsCallback(showInactive);

    List<InformationSystemRelease> isrs = getHibernateTemplate().executeFind(callback);
    Collections.sort(isrs, new HierarchicalEntityCachingComparator<InformationSystemRelease>());

    return isrs;
  }

  private static final class DoesReleaseExistCallback implements HibernateCallback<List<InformationSystemRelease>> {
    private final String name;
    private final String version;

    public DoesReleaseExistCallback(String name, String version) {
      this.name = name;
      this.version = version;
    }

    @SuppressWarnings("unchecked")
    public List<InformationSystemRelease> doInHibernate(Session session) {
      Criteria c = session.createCriteria(InformationSystemRelease.class, "isr");
      if ((version != null) && !version.equals("")) {
        c.add(Restrictions.eq("isr.version", version).ignoreCase());
      }
      c.createAlias("informationSystem", "is");
      c.add(Restrictions.eq("is.name", name).ignoreCase());
      // Should be uniqueResult, list is used to work with old (inconsistent) data
      return c.list();
    }
  }

  private static final class FilterWithIdsCallback implements HibernateCallback<List<InformationSystemRelease>> {
    private final Set<Integer> elementsToExclude;
    private final boolean      showInactive;

    public FilterWithIdsCallback(Set<Integer> elementsToExclude, boolean showInactive) {
      this.elementsToExclude = elementsToExclude;
      this.showInactive = showInactive;
    }

    @SuppressWarnings("unchecked")
    public List<InformationSystemRelease> doInHibernate(Session session) {
      Criteria c = session.createCriteria(InformationSystemRelease.class);
      if ((elementsToExclude != null) && !elementsToExclude.isEmpty()) {
        c.add(CriteriaUtil.createNotInRestrictions("id", elementsToExclude));
      }

      if (!showInactive) {
        c.add(Restrictions.ne("typeOfStatus", InformationSystemRelease.TypeOfStatus.INACTIVE));
      }

      return c.list();
    }
  }

  private static final class InformationSystemReleasesWithConnectionsCallback implements HibernateCallback<List<InformationSystemRelease>> {
    private final boolean showInactive;

    public InformationSystemReleasesWithConnectionsCallback(boolean showInactive) {
      this.showInactive = showInactive;
    }

    @SuppressWarnings("unchecked")
    public List<InformationSystemRelease> doInHibernate(Session session) {
      Query q;
      if (showInactive) {
        q = session.getNamedQuery("getInformationSystemReleasesWithConnectionsAll");
      }
      else {
        q = session.getNamedQuery("getInformationSystemReleasesWithConnections");
        q.setParameter("ignoreTypeOfStatus", InformationSystemRelease.TypeOfStatus.INACTIVE);
      }
      return q.list();
    }
  }

  private static final class IsDuplicateInformationSystemCallback implements HibernateCallback<Long> {
    private final String  name;
    private final Integer identifier;

    public IsDuplicateInformationSystemCallback(String name, Integer identifier) {
      this.name = name;
      this.identifier = identifier;
    }

    public Long doInHibernate(Session session) {
      String q = "select count(id) from InformationSystem where name = :name";
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

  private static final class GetFirstElementCallback implements HibernateCallback<InformationSystemRelease> {
    private final boolean showInactive;

    public GetFirstElementCallback(boolean showInactive) {
      this.showInactive = showInactive;
    }

    public InformationSystemRelease doInHibernate(Session session) {
      String query = "from InformationSystemRelease isr join fetch isr.informationSystem ins ";
      if (!showInactive) {
        query += "where isr.typeOfStatus not like :tos ";
      }
      query += "order by ins.name, isr.version";
      Query q = session.createQuery(query);
      if (!showInactive) {
        q.setParameter("tos", InformationSystemRelease.TypeOfStatus.INACTIVE);
      }
      q.setMaxResults(1);

      return (InformationSystemRelease) q.uniqueResult();
    }
  }

  private static final class InformationsystemsWithSuccessorsCallback implements HibernateCallback<List<InformationSystemRelease>> {
    private final boolean showInactive;

    public InformationsystemsWithSuccessorsCallback(boolean showInactive) {
      this.showInactive = showInactive;
    }

    @SuppressWarnings("unchecked")
    public List<InformationSystemRelease> doInHibernate(Session session) {
      String query = "from InformationSystemRelease isr " + " where ( isr.predecessors.size > 0 or isr.successors.size > 0 )";
      if (!showInactive) {
        query += " and isr.typeOfStatus not like :tos ";
      }
      Query q = session.createQuery(query);
      if (!showInactive) {
        q.setParameter("tos", InformationSystemRelease.TypeOfStatus.INACTIVE);
      }
      return q.list();
    }
  }

}
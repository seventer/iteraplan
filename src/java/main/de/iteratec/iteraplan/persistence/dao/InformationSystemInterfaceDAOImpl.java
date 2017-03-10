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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.orm.hibernate3.HibernateCallback;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;

import de.iteratec.iteraplan.persistence.util.SqlHqlStringUtils;

/**
 * Implementation of the DAO interface {@link InformationSystemInterfaceDAO}.
 */
public class InformationSystemInterfaceDAOImpl extends GenericBaseDAO<InformationSystemInterface, Integer> implements InformationSystemInterfaceDAO {

  /** {@inheritDoc} */
  public List<InformationSystemInterface> getConnectionsImplementedByTechnicalComponent(TechnicalComponent tc) {
    if (tc == null) {
      return new ArrayList<InformationSystemInterface>();
    }

    return executeNamedQuery("getConnectionsImplementedByTechnicalComponent", "tc", tc);
  }

  /** {@inheritDoc} */
  public List<InformationSystemInterface> getConnectionsImplementedByTechnicalComponentRelease(TechnicalComponentRelease release) {
    if (release == null) {
      return new ArrayList<InformationSystemInterface>();
    }

    return executeNamedQuery("getConnectionsImplementedByTechnicalComponentRelease", "release", release);
  }

  /**
   * Returns the first {@link InformationSystemInterface} of a list of interfaces sorted by the first information system's name and version and then
   * second information system's name and version. The {@link InformationSystemInterface#getReferenceRelease()} of the returned
   * Connection will return the first information system of that connection. If the current user is configured to show only active elements 
   * (see UserContext.getCurrentUserContext().isShowInactiveStatus()), the first interface containing active release A and active release B will 
   * be returned. If no Connection can be found, null is returned.
   * 
   * @return Return the first Connection or null.
   */
  @SuppressWarnings("unchecked")
  @Override
  public InformationSystemInterface getFirstElement() {
    List<InformationSystemInterface> result;
    if (!UserContext.getCurrentUserContext().isShowInactiveStatus()) {
      result = getHibernateTemplate().findByNamedQueryAndNamedParam("getFirstConnection", "ignoreTypeOfStatus", TypeOfStatus.INACTIVE);
    }
    else {
      result = getHibernateTemplate().findByNamedQuery("getFirstConnectionAll");
    }

    if (result.isEmpty()) {
      return null;
    }

    InformationSystemInterface isi = result.get(0);
    isi.setReferenceRelease(isi.getInformationSystemReleaseA());
    return isi;
  }

  /** {@inheritDoc} */
  @Override
  protected String getNameAttribute() {
    return "name";
  }

  /** {@inheritDoc} */
  public PagedListHolder<InformationSystemInterface> getMatchesAndCountForISI(final String[] searchTerm) {
    HibernateCallback<PagedListHolder<InformationSystemInterface>> callback = new MatchesAndCountForISICallback(searchTerm);

    return getHibernateTemplate().execute(callback);
  }

  private static final class MatchesAndCountForISICallback implements HibernateCallback<PagedListHolder<InformationSystemInterface>> {
    private final String[] searchTerm;

    public MatchesAndCountForISICallback(String[] searchTerm) {
      this.searchTerm = searchTerm.clone(); //clone() is used to avoid violation in pmd ("ArrayIsStoredDirectly")
    }

    @SuppressWarnings("unchecked")
    public PagedListHolder<InformationSystemInterface> doInHibernate(Session session) {
      PagedListHolder<InformationSystemInterface> result = new PagedListHolder<InformationSystemInterface>();

      StringBuilder clause = new StringBuilder("FROM InformationSystemInterface isi");

      // Build the Filter Statement if there is something to filter
      if (StringUtils.isNotEmpty(searchTerm[0]) && StringUtils.isNotEmpty(searchTerm[1])) {
        clause.append(" WHERE (");
        clause.append("(lower(isi.informationSystemReleaseA.informationSystem.name) LIKE '"
            + SqlHqlStringUtils.processGuiFilterForSql(searchTerm[0].toLowerCase()) + "' OR lower(isi.informationSystemReleaseA.version) LIKE '"
            + SqlHqlStringUtils.processGuiFilterForSql(searchTerm[0].toLowerCase()) + "')");
        clause.append(" OR ");
        clause.append("(lower(isi.informationSystemReleaseB.informationSystem.name) LIKE '"
            + SqlHqlStringUtils.processGuiFilterForSql(searchTerm[1].toLowerCase()) + "' OR lower(isi.informationSystemReleaseB.version) LIKE '"
            + SqlHqlStringUtils.processGuiFilterForSql(searchTerm[1].toLowerCase()) + "')");
        clause.append(")");
      }

      // Build the Order Statement
      String orderClause = " ORDER BY isi.informationSystemReleaseA.informationSystem.name, isi.informationSystemReleaseA.version, "
          + "isi.informationSystemReleaseB.informationSystem.name, isi.informationSystemReleaseB.version";

      // Build the Query that returns the list
      Query q = session.createQuery(clause + orderClause);

      // Perform the Query and save the matches in the MatchesList
      result.setSource(q.list());

      return result;
    }
  }

  /** {@inheritDoc} */
  public List<InformationSystemInterface> getSelfReferencedInterface(Integer isReleaseId) {
    return executeNamedQuery("getSelfReferencedInterface", "isReleaseId", isReleaseId);
  }

}
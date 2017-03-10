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

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.history.HistoryRevisionEntity;
import de.iteratec.iteraplan.persistence.history.BuildingBlockRevision;
import de.iteratec.iteraplan.persistence.history.HistoryEventListener;


/**
 * Implementation of the DAO interface {@link HistoryDAO}.
 * @author rge
 */
public class HistoryDAOImpl extends GenericBaseDAO<HistoryRevisionEntity, Integer> implements HistoryDAO {

  private final HistoryEventListener historyEventListener;
  private static final String        TIMESTAMP_PROPERTY = "timestamp";

  /**
   * @param historyEventListener
   */
  public HistoryDAOImpl(HistoryEventListener historyEventListener) {
    super();
    this.historyEventListener = historyEventListener;
  }

  /** {@inheritDoc} */
  @Override
  protected String getNameAttribute() {
    throw new UnsupportedOperationException("This operation is not supported for this type.");
  }

  /**
   * Gets a new AuditReader from the current session
   */
  public AuditReader getAuditReader() {
    return AuditReaderFactory.get(this.getSession());
  }

  public <T extends BuildingBlock> int getHistoryLengthFor(Class<T> entityClass, Integer id, DateTime fromDate, DateTime toDate) {
    Preconditions.checkArgument(id != null && id.intValue() >= 0, "Param id is invalid, should be >=0");
    AuditReader auditReader = getAuditReader();
    AuditQuery totalCountQuery = auditReader.createQuery().forRevisionsOfEntity(entityClass, false, false).add(AuditEntity.id().eq(id));

    if (fromDate != null) {
      Long fromDateLong = Long.valueOf(fromDate.getMillis());
      totalCountQuery.add(AuditEntity.revisionProperty(TIMESTAMP_PROPERTY).ge(fromDateLong));
    }
    if (toDate != null) {
      Long toDateLong = Long.valueOf(toDate.getMillis());
      totalCountQuery.add(AuditEntity.revisionProperty(TIMESTAMP_PROPERTY).le(toDateLong));
    }

    // Can't get the total count from the other query because it might be limited to results of the current page
    return totalCountQuery.getResultList().size();
  }

  public <T extends BuildingBlock> T getPreceedingRevisionFor(Class<T> entityClass, Integer bbId, Integer currentRevId) {
    AuditReader auditReader = getAuditReader();

    // Query to get max Rev less than this one (Should exist, since this is a MOD, but might not
    Number prevRev = (Number) auditReader.createQuery().forRevisionsOfEntity(entityClass, true, true)
        .addProjection(AuditEntity.revisionNumber().max()).add(AuditEntity.id().eq(bbId)).add(AuditEntity.revisionNumber().lt(currentRevId))
        .getSingleResult();

    // If History recording was off during the Initial creation of this BB, this MOD rev might be the
    // first known rev
    if (prevRev == null) {
      return null;
    }

    return auditReader.find(entityClass, bbId, prevRev);
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "unchecked", "boxing" })
  public <T extends BuildingBlock> List<BuildingBlockRevision<T>> getRevisionBounded(final Class<T> entityClass, Integer id, Integer curPage,
                                                                                     Integer pageSize, DateTime fromDate, DateTime toDate) {

    Preconditions.checkArgument(id != null && id.intValue() >= 0, "Param id is invalid, should be >=0");
    Preconditions.checkArgument(pageSize >= -1, "Param pageSize is invalid, should be -1 or >0");
    Preconditions.checkArgument(pageSize.intValue() != 0, "Param pageSize is invalid, should be -1 or >0"); //would lead to /0 err

    AuditReader auditReader = getAuditReader();

    // Query retrieves RevisionType in addition to Entity; Revs of type DEL are not retrieved
    // Get date on revisions of this BB
    AuditQuery curPageQuery = auditReader.createQuery().forRevisionsOfEntity(entityClass, false, false).add(AuditEntity.id().eq(id));

    // Limit results by date
    if (fromDate != null) {
      Long fromDateLong = Long.valueOf(fromDate.getMillis());
      curPageQuery.add(AuditEntity.revisionProperty(TIMESTAMP_PROPERTY).ge(fromDateLong));
    }
    if (toDate != null) {
      Long toDateLong = Long.valueOf(toDate.getMillis());
      curPageQuery.add(AuditEntity.revisionProperty(TIMESTAMP_PROPERTY).le(toDateLong));
    }

    int firstResult = curPage * pageSize;

    // Paging (first results, max results), disabled when requesting all results (pageSize=-1)
    if (pageSize > 0) {
      curPageQuery.setFirstResult(firstResult).setMaxResults(pageSize);
    }

    // Object Array[3] contains: T, HistoryRevisionEntity, RevisionType
    List<Object[]> revsList = curPageQuery.addOrder(AuditEntity.revisionNumber().desc()).getResultList();

    return Lists.newArrayList(Lists.transform(revsList, new Function<Object[], BuildingBlockRevision<T>>() {

      @Override
      public BuildingBlockRevision<T> apply(Object[] revObjects) {
        return new BuildingBlockRevision<T>(revObjects, entityClass);
      }

    }));
  }

  /** {@inheritDoc} */
  public boolean isHistoryEnabled() {
    return this.historyEventListener.isHistoryEnabled();
  }

  /** {@inheritDoc} */
  public void setHistoryEnabled(boolean historyEnabledParam) {
    this.historyEventListener.setHistoryEnabled(historyEnabledParam);

  }

}
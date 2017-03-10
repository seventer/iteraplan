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

import org.joda.time.DateTime;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.history.HistoryRevisionEntity;
import de.iteratec.iteraplan.persistence.history.BuildingBlockRevision;


/**
 * DAO containing the main logic for retrieving history, via Hibernate Envers.
 * 
 * @author rge
 */
public interface HistoryDAO extends DAOTemplate<HistoryRevisionEntity, Integer> {

  /**
   * Returns a list with chunks of one building block's revisions, within the specified (optional) date boundaries.
   * Each list element is a three-value {@code Object[]} array with these types:
   * {@code T}, {@link HistoryRevisionEntity}, {@link org.hibernate.envers.RevisionType RevisionType}.
   * 
   * @param entityClass Class of BB
   * @param bbId Id of BB
   * @param curPage 0-based index of the page to retrieve
   * @param pageSize Results per page. -1 for infinite. May not be ==0 or < -1
   * @param fromDate Earliest date for which revisions may be returned. May be null to not limit results in that direction.
   * @param toDate Latest date (inclusive) for which revisions may be returned. May be null to not limit results in that direction.
   * @return a list of building block revisions (BuildingBlock objects in historical states), along with their
   *    HistoryRevisionEntity meta-data and a revision type. The list does not necessarily contain all revisions, but is only complete within the given boundaries.
   *    Further chunks of revisions must be retrieved anew
   */
  <T extends BuildingBlock> List<BuildingBlockRevision<T>> getRevisionBounded(Class<T> entityClass, Integer bbId, Integer curPage, Integer pageSize,
                                                                              DateTime fromDate, DateTime toDate);

  /**
   * Retrieves the total number of one building block's revisions, within the specified (optional) date boundaries.
   * 
   * @param entityClass Class of BB
   * @param bbId Id of BB
   * @param fromDate Earliest date for which revisions may be returned. May be null to not limit results in that direction.
   * @param toDate Latest date (inclusive) for which revisions may be returned. May be null to not limit results in that direction.
   * @return the total number of this building block's revisions, within the given date boundaries.
   */
  <T extends BuildingBlock> int getHistoryLengthFor(Class<T> entityClass, Integer bbId, DateTime fromDate, DateTime toDate);

  /**
   * Retrieves the building blocks revision which comes directly before the specified revision of the building block.
   * @param entityClass Class of BB
   * @param bbId Id of BB
   * @param currentRevId The revision of this building block to compare against.
   * @return The historical building block state that was valid before {@code currentRevId} revision.
   */
  <T extends BuildingBlock> T getPreceedingRevisionFor(Class<T> entityClass, Integer bbId, Integer currentRevId);

  boolean isHistoryEnabled();

  void setHistoryEnabled(boolean historyEnabledParam);

}
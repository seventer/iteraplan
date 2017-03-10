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
package de.iteratec.iteraplan.businesslogic.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import de.iteratec.iteraplan.businesslogic.service.diffs.BBChangesetFactory;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.history.HistoryRevisionEntity;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.persistence.dao.HistoryDAO;
import de.iteratec.iteraplan.persistence.history.BuildingBlockRevision;
import de.iteratec.iteraplan.presentation.dialog.History.HistoryResultsPage;


/**
 * This service provides access to functionality that is related to History via Hibernate Envers.
 * @author rge
 */
public class HistoryServiceImpl implements HistoryService {

  private static final Logger LOGGER = Logger.getIteraplanLogger(HistoryServiceImpl.class);

  private final HistoryDAO    historyDAO;

  private UserService         userService;

  public HistoryServiceImpl(HistoryDAO historyDAO, UserService userService) {
    this.historyDAO = historyDAO;
    this.userService = userService;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("boxing")
  public <T extends BuildingBlock> HistoryResultsPage getLocalHistoryPage(Class<T> entityClass, Integer id, Integer curPage, Integer pageSize,
                                                                          DateTime fromDate, DateTime toDate) {

    LOGGER.debug("Getting changesets for {0} with ID {1}...", entityClass.getSimpleName(), id);
    // Object Array[3] contains: T, HistoryRevisionEntity, RevisionType
    List<BuildingBlockRevision<T>> revsList = historyDAO.getRevisionBounded(entityClass, id, curPage, pageSize, fromDate, toDate);

    List<HistoryBBChangeset> bbChangesetList = new ArrayList<HistoryBBChangeset>();

    // Compare each version to the previous, if there is one, else just note that it's the first
    for (int i = 0; i < revsList.size(); i++) {
      BuildingBlockRevision<T> currentRev = revsList.get(i);

      T curObj = currentRev.getBuildingBlock();
      HistoryRevisionEntity revEntity = currentRev.getRevEntity();

      DateTime curDate = revEntity.getRevisionDateTime();
      String username = revEntity.getUsername();
      User revisionAuthor = userService.getUserByLoginIfExists(username);
      LOGGER.debug("> " + username + " made changes on " + curDate + " [" + revEntity.getId() + "]");

      switch (currentRev.getRevType()) {
        case ADD:
          // This is the actual revision where this BB was added/created
          HistoryBBChangeset changesetAdd = BBChangesetFactory.createChangeset(null, curObj, username, curDate);
          changesetAdd.setRevisionAuthor(revisionAuthor);
          bbChangesetList.add(changesetAdd);
          break;

        case MOD:
          // Changesets require comparing two Revs.
          T prevObj = null;
          // Try getting the previous rev out of our list
          if (i < revsList.size() - 1) {
            prevObj = revsList.get(i + 1).getBuildingBlock();
          }
          else {
            prevObj = historyDAO.getPreceedingRevisionFor(entityClass, id, revEntity.getId());
          }

          HistoryBBChangeset changesetMod = BBChangesetFactory.createChangeset(prevObj, curObj, username, curDate);
          changesetMod.setRevisionAuthor(revisionAuthor);
          bbChangesetList.add(changesetMod);
          break;

        default: // DEL shouldn't happen since the query ignores them
          throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }
    }

    int totalHistorySize = historyDAO.getHistoryLengthFor(entityClass, id, fromDate, toDate);

    return new HistoryResultsPage(bbChangesetList, totalHistorySize, pageSize, curPage);

  }

  /** {@inheritDoc} */
  public boolean isHistoryEnabled() {
    return historyDAO.isHistoryEnabled();
  }

  /** {@inheritDoc} */
  public void setHistoryEnabled(boolean historyEnabledParam) {
    historyDAO.setHistoryEnabled(historyEnabledParam);
  }

}
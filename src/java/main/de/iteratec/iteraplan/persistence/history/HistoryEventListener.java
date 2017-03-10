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
package de.iteratec.iteraplan.persistence.history;

import org.hibernate.envers.event.AuditEventListener;
import org.hibernate.event.PostCollectionRecreateEvent;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PreCollectionRemoveEvent;
import org.hibernate.event.PreCollectionUpdateEvent;

import de.iteratec.iteraplan.common.util.IteraplanProperties;


/**
 * Listens for certain Hibernate Envers events
 * 
 * @author rge
 */
public class HistoryEventListener extends AuditEventListener {

  private static final long    serialVersionUID     = 951312261914799337L;

  // History is only saved if history.enabled is set to "true" in iteraplan.properties
  private static final boolean HISTORY_ENABLED_PROP = IteraplanProperties.getBooleanProperty(IteraplanProperties.HISTORY_ENABLED);
  private boolean              historyEnabled       = HISTORY_ENABLED_PROP;

  @Override
  public void onPostInsert(PostInsertEvent event) {
    if (isHistoryEnabled()) {
      super.onPostInsert(event);
    }
  }

  @Override
  public void onPostUpdate(PostUpdateEvent event) {
    // If this is selectively not versioned, but the Collection listeners still are, the db will be invalid
    if (isHistoryEnabled()) { //&& entityHasHistory(event, event.getEntity())

      //      ensureEntityHasHistory(event, event.getEntity());
      super.onPostUpdate(event);
    }
  }

  @Override
  public void onPostDelete(PostDeleteEvent event) {
    if (isHistoryEnabled()) { //&& entityHasHistory(event, event.getEntity())

      //      ensureEntityHasHistory(event, event.getEntity());
      super.onPostDelete(event);
    }
  }

  @Override
  public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
    if (isHistoryEnabled()) {
      super.onPreUpdateCollection(event);
    }
  }

  @Override
  public void onPreRemoveCollection(PreCollectionRemoveEvent event) {
    if (isHistoryEnabled()) {
      super.onPreRemoveCollection(event);
    }
  }

  @Override
  public void onPostRecreateCollection(PostCollectionRecreateEvent event) {
    if (isHistoryEnabled()) {
      super.onPostRecreateCollection(event);
    }
  }

  /**
   * This only effects saving History. You can always try to retrieve History.
   * @return true if enabled
   */
  public boolean isHistoryEnabled() {
    return historyEnabled;
  }

  /**
   * Only for testing purposes. Should be configured in iteraplan.properties
   */
  public void setHistoryEnabled(boolean historyEnabled) {
    this.historyEnabled = historyEnabled;
  }

  //  /**
  //   * Checks if the entity from an event has previous history.
  //   * Bug: Just calling this function can interfere with data creation in db.createInitialData
  //   *
  //   * @return true if previous history found
  //   */
  //  private boolean entityHasHistory(AbstractEvent event, Object entity) {
  //    AuditReader ar = AuditReaderFactory.get(event.getSession());
  //
  //    if (entity instanceof IdEntity) {
  //      IdEntity idEntity = (IdEntity) entity;
  //      List<Number> revs = ar.getRevisions(idEntity.getClass(), idEntity.getId());
  //      if (!revs.isEmpty()) {
  //        return true;
  //      }
  //      else {
  //        //        logger.warn(idEntity + " has no history; not saving new history.");
  //        return false;
  //      }
  //    }
  //    else {
  //      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
  //    }
  //  }
  //
  //  private void ensureEntityHasHistory(AbstractEvent event, Object entity) {
  //
  //    if (!entityHasHistory(event, entity)) {
  //      // This will happen when creating the initial data, or just before vague crashes that occur when editing/deleting a BB that has no history
  //      logger.warn(entity + " has no history!");
  //    }
  //  }

}
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
package de.iteratec.iteraplan.businesslogic.exchange.common;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * Class to track a process over several steps which have to be worked off in order, one after the other 
 */
public class CheckList<T extends Serializable> implements Serializable {
  /** Serialization version */
  private static final long   serialVersionUID    = -7396419738835477364L;

  private static final Logger LOGGER              = Logger.getIteraplanLogger(CheckList.class);

  private final List<T>       allCheckPoints;
  private int                 lastDoneIndex       = -1;
  private int                 currentPendingIndex = -1;

  public CheckList(List<T> allCheckPoints) {
    this.allCheckPoints = allCheckPoints;
  }

  /**
   * Sets the given check list item as the one currently in work.
   * Requires the check list item directly in front of it in the list of all items to be the last finished item.
   * @param checkPoint
   *          the check list item to be set as pending
   */
  public void pending(T checkPoint) {
    int pendingIndex = allCheckPoints.indexOf(checkPoint);
    if (pendingIndex == -1) {
      LOGGER.error("Check point \"{0}\" not part of this check list.", checkPoint);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
    if (pendingIndex != lastDoneIndex + 1) {
      LOGGER.error("Cannot start working on check point \"{0}\" at this point.", checkPoint);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
    else {
      currentPendingIndex = pendingIndex;
    }
  }

  /**
   * Sets the next check list item after the item finished last as 'pending', if there isn't
   * already another pending check list item and if the checkList isn't already finished.
   * @return true if the status change was successful
   */
  public boolean nextPending() {
    if (currentPendingIndex != -1) {
      LOGGER.warn("There is already a check list item pending: {0}.", getPending());
      return false;
    }
    else if (lastDoneIndex == allCheckPoints.size() - 1) {
      return false;
    }
    else {
      currentPendingIndex = lastDoneIndex + 1;
      return true;
    }
  }

  /**
   * Sets the given check list item as last finished.
   * Requires the check list item to be in pending status.
   * @param checkPoint
   *          the check list item to be set as done
   */
  public void done(T checkPoint) {
    int doneIndex = allCheckPoints.indexOf(checkPoint);
    if (doneIndex == -1) {
      LOGGER.error("Check point \"{0}\" not part of this check list.", checkPoint);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
    if (doneIndex != currentPendingIndex) {
      LOGGER.error("Cannot be done with check point \"{0}\", because it isn't in work right now.", checkPoint);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
    else {
      lastDoneIndex = doneIndex;
      currentPendingIndex = -1;
    }
  }

  /**
   * Sets the currently pending check list item as 'done', if there is one.
   * @return true if the status change was successful
   */
  public boolean currentDone() {
    if (currentPendingIndex == -1) {
      LOGGER.warn("There is no currently pending check list item to be set as done.");
      return false;
    }
    else {
      lastDoneIndex = currentPendingIndex;
      currentPendingIndex = -1;
      return true;
    }
  }

  /**
   * resets the check list
   */
  public void resetCheckList() {
    this.lastDoneIndex = -1;
    this.currentPendingIndex = -1;
  }

  /**
   * Gets all check list items which are already finished.
   * @return List of all finished check list items
   */
  public List<T> getDone() {
    List<T> result = Lists.newArrayList();
    for (int i = 0; i <= lastDoneIndex; i++) {
      result.add(allCheckPoints.get(i));
    }
    return result;
  }

  /**
   * Gets the check list item currently in work.
   * @return the pending check list item
   */
  public T getPending() {
    if (currentPendingIndex == -1) {
      return null;
    }
    return allCheckPoints.get(currentPendingIndex);
  }

  /**
   * Gets the check list item yet to be worked on
   * @return List of all check list items which are neither finished, nor pending
   */
  public List<T> getToDo() {
    List<T> result = Lists.newArrayList();
    for (int i = Math.max(lastDoneIndex, currentPendingIndex) + 1; i < allCheckPoints.size(); i++) {
      result.add(allCheckPoints.get(i));
    }
    return result;
  }
}

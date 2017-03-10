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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.excelimport;

import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.DisposableBean;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.IteraplanProperties;


/**
 * Cleans the import queue periodically
 */

public final class ImportProcessRegistryCleanupTimer implements DisposableBean {

  private static final Logger   LOGGER                               = Logger.getIteraplanLogger(ImportProcessRegistryCleanupTimer.class);

  private static final int      IMPORT_QUEUE_CLEANUP_INTERVAL_IN_HOURS = IteraplanProperties
      .getIntProperty(IteraplanProperties.IMPORT_QUEUE_CLEANUP_INTERVAL);

  private static final int      multiplicationFactorToMs             = 1000 * 60 * 60;

  private ImportProcessRegistry importProcessRegistry;

  private Timer                 timer;

  private int                   startupOffset;
  private int                   period;

  private static final String   TIMER_NAME                            = "PeriodicImportQueueCleanupService";

  public ImportProcessRegistryCleanupTimer() {
    startupOffset = IMPORT_QUEUE_CLEANUP_INTERVAL_IN_HOURS * multiplicationFactorToMs;
    period = startupOffset;
    createTimer();
  }

  public void cleanupImportQueue() {
    if (importProcessRegistry == null) {
      LOGGER.error("Initialisation error: importProcessRegistry is null.");
      return;
    }

    importProcessRegistry.cleanUpOldProcesses();
    LOGGER.debug("Cleaned up the import queue.");
  }

  /**
   * @return importProcessRegistry the importProcessRegistry
   */
  public ImportProcessRegistry getImportProcessRegistry() {
    return importProcessRegistry;
  }

  public void setImportProcessRegistry(ImportProcessRegistry importProcessRegistry) {
    this.importProcessRegistry = importProcessRegistry;
  }

  /**
   * Called on shutdown of the Spring ApplicationContext, this method cancels the Timer, so that the corresponding Thread will stop eventually.
   *
   * {@inheritDoc}
   */
  public void destroy() {
    timer.cancel();
  }

  public void setStartupOffsetAndPeriodInMs(int startupOffsetInMs, int periodInMs) {
    this.startupOffset = startupOffsetInMs;
    this.period = periodInMs;
    timer.cancel();
    createTimer();
  }

  @SuppressWarnings("boxing")
  private void createTimer() {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Starting the timer for the cleanup of the import queue. First run is scheduled at: {0} ({1} ms from now). Period is: {2} ms.",
          LocalDateTime.now().plusMillis(startupOffset).toString(), startupOffset, period);
    }
    timer = new Timer(TIMER_NAME, true);
    timer.schedule(new CleanupTimerTask(), startupOffset, period);
  }

  private class CleanupTimerTask extends TimerTask {
    @Override
    public void run() {
      cleanupImportQueue();
    }
  }

}

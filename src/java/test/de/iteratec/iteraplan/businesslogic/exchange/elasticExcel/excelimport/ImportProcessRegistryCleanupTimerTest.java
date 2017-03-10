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

import java.util.concurrent.CountDownLatch;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.Logger;


public class ImportProcessRegistryCleanupTimerTest extends BaseTransactionalTestSupport {

  @Autowired
  private ImportProcessRegistryCleanupTimer periodicCleanupService;

  private static final Logger               LOGGER = Logger.getIteraplanLogger(ImportProcessRegistryCleanupTimerTest.class);

  private ImportProcessRegistry             importProcessRegistry;

  @Override
  @Before
  public void setUp() {
    importProcessRegistry = EasyMock.createMock(ImportProcessRegistry.class);
  }

  @Test
  public void testCleanupOldProcessesWithTimer() {
    // setting the expected call
    importProcessRegistry.cleanUpOldProcesses();
    EasyMock.replay(importProcessRegistry);

    periodicCleanupService.setImportProcessRegistry(importProcessRegistry);

    LOGGER.info("Changing the interval of the periodicCleanup to allow testing.");
    periodicCleanupService.setStartupOffsetAndPeriodInMs(100, 100000); // hint: cleanUpOldProcesses is called for the first time after the startupOffset (ms) has passed.

    /*
     * We need a helper method to give {@link PeriodicImportQueueCleanupServiceImpl} the time to call cleanUpOldProcesses().
     * Cannot use wait because we are not the owner of this threads monitor.
     */
    sleep(1000);

    EasyMock.verify(importProcessRegistry);
  }

  @SuppressWarnings({ "PMD", "DoNotUseThreads" })
  private void sleep(final int interval) {
    final CountDownLatch latch = new CountDownLatch(1);
    Thread thread = new Thread() {
      @Override
      public void run() {
        try {
          sleep(interval);
        } catch (InterruptedException e) {
          LOGGER.error("The sleep-thread was interrupted.", e);
        }
        latch.countDown();
      }
    };
    thread.start();
    try {
      latch.await();
    } catch (InterruptedException e) {
      LOGGER.error("Error while waiting for the synchronized object.", e);
    }
  }

}

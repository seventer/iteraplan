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
package de.iteratec.iteraplan.common.monitoring;

import de.iteratec.iteraplan.common.Logger;


/**
 * Since AOP is only able to weave its pointcuts if called via the dynamic proxy, inner calls using "this."
 * are unable to get monitored. Thus we go back to using a Monitoring class to monitor elapsed time and used
 * memory. It ist controlled by setting the appropriate log levels to at least info.
 * <p>
 * 
 * @author Lars Orta
 */
public class Monitor {

  private static final Logger LOG_TIMER             = Logger.getIteraplanLogger("monitoring.timer");
  private static final Logger LOG_MEM               = Logger.getIteraplanLogger("monitoring.memory");

  private static final int    BYTE_CONVERSION_VALUE = 1024;

  private String              identifier;
  private long                startTime             = 0L;

  public Monitor() {
    this(null);
  }

  public Monitor(String identifier) {

    if (LOG_TIMER.isInfoEnabled()) {
      this.identifier = identifier;
      startTime = System.currentTimeMillis();
    }

    if (LOG_MEM.isInfoEnabled()) {
      this.identifier = identifier;
    }

  }

  public void end() {

    if (LOG_TIMER.isInfoEnabled()) {
      long endTime = System.currentTimeMillis();
      LOG_TIMER.info("#Timer " + identifier + ": " + (endTime - startTime) + " ms");
    }

    if (LOG_MEM.isInfoEnabled()) {
      long usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      LOG_MEM.info("#Memory " + identifier + ": " + usedMem / BYTE_CONVERSION_VALUE + " [KB]");
    }

  }
}

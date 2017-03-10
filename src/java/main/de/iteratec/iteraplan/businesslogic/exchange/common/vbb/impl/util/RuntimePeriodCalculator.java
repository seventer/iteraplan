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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util;

import java.util.Date;

import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.RuntimePeriod;


/**
 * Helper used to determine the maximum timespan covered by a collection of runtimePeriods.
 */
public class RuntimePeriodCalculator {

  private Date startDate = new Date(Long.MAX_VALUE);
  private Date endDate   = new Date(Long.MIN_VALUE);

  /**
   * Adds the period to the collection of considered runtime periods.
   * @param period the runtime period to consider.
   */
  public void addRuntimePeriod(RuntimePeriod period) {
    if (period != null && period.getStart() != null) {
      this.startDate = DateUtils.earlier(this.startDate, period.getStart());
      this.endDate = DateUtils.later(this.endDate, period.getStart());
    }
    if (period != null && period.getEnd() != null) {
      this.endDate = DateUtils.later(this.endDate, period.getEnd());
      this.startDate = DateUtils.earlier(this.startDate, period.getEnd());
    }
  }

  /**
   * @return the runtime period covering the maximum timespan covered by the collected runtimePeriods.
   */
  public RuntimePeriod getGlobalRuntimePeriod() {
    return new RuntimePeriod(this.startDate, this.endDate);
  }
}

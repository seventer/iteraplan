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
package de.iteratec.iteraplan.model;

import java.util.Date;


/**
 * This interface is intended as a kind of 'mixin'. It does not provide additional functionality as 
 * pure mixins do, but it requires the implementing class to provide it by implementing particular 
 * getter/setter methods. Besides, this interface provides methods that should be delegated to 
 * {@link RuntimePeriod}. 
 */
public interface RuntimePeriodDelegate {

  RuntimePeriod getRuntimePeriod();

  void setRuntimePeriod(RuntimePeriod p);

  /**
   * @return
   *    Never returns {@code null}. If the corresponding member variable is {@code null}, a default 
   *    {@link RuntimePeriod} is returned. Note that this method is owing to shortcomings of Hibernate 
   *    concerning the retrieval of value types. 
   */
  RuntimePeriod getRuntimePeriodNullSafe();

  /**
   * If the given {@link RuntimePeriod} is unbounded on both sides, the corresponding member variable 
   * is set to {@code null}. Note that this method is owing to shortcomings of Hibernate concerning 
   * the retrieval of value types. 
   */
  void setRuntimePeriodNullSafe(RuntimePeriod period);

  /**
   * Convenience method that should delegate to {@link RuntimePeriod#getEnd()}.
   */
  Date runtimeStartsAt();

  /**
   * Convenience method that should delegate to {@link RuntimePeriod#getStart()}.
   */
  Date runtimeEndsAt();

  /**
   * Convenience method that should delegate to {@link RuntimePeriod#withinPeriod(RuntimePeriod)}.
   */
  boolean runtimeWithinPeriod(RuntimePeriod p);

  /**
   * Convenience method that should delegate to {@link RuntimePeriod#overlapsPeriod(RuntimePeriod)}.
   */
  boolean runtimeOverlapsPeriod(RuntimePeriod p);
}
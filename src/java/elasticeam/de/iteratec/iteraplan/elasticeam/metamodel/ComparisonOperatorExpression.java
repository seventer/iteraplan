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
package de.iteratec.iteraplan.elasticeam.metamodel;

import java.util.Set;


/**
 * Captures the concept of a comparison operator available for one or more types.
 */
public interface ComparisonOperatorExpression extends NamedExpression {

  /**
   * Returns true if and only if the provided objects 
   * satisfy the comparison operation in their order as
   * arguments.
   * 
   * <b>Note:</b> Both objects must not be null and must be instances
   * of the same class. Otherwise a metamodel exception will be thrown.
   * 
   * @param o1
   *    The first operand.
   * @param o2
   *    The second operand.
   * @return
   *    <b>true</b> if o1 operation o2 holds and <b>false</b> otherwise.
   */
  boolean compare(Object o1, Object o2);

  /**
   * Retrieves the list of types to which this comparison operator can be applied.
   * @return
   *    The list of available types.
   */
  Set<Class<?>> getSupportedTypes();

  /**
   * Returns true if the given comparison operator supports the given type.
   * @param type
   *    The type.
   * @return
   *    <b>true</b> if and only if the type is supported by the given operator.
   */
  boolean supportsType(Class<?> type);

}

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
package de.iteratec.iteraplan.elasticeam.model;

import java.util.Collection;

import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;


/**
 * Describes the operations for creating, deleting and finding {@link InstanceExpression}s.
 */
public interface InstanceHandler {

  /**
   * Deletes an instance expression.
   * 
   * @param instance
   *    The instance expression to delete.
   */
  void delete(InstanceExpression instance);

  /**
   * Creates a new instance expression of the given substantial type.
   * 
   * @param type
   *    The {@link SubstantialTypeExpression} of the new instance.
   * @return
   *    The new instance expression.
   */
  InstanceExpression create(SubstantialTypeExpression type);

  /**
   * Retrieves all instance expressions of a given substantial type.
   * 
   * @param type
   *    The {@link SubstantialTypeExpression} whose instances are to be fetched.
   * @return
   *    The collection of instances of this type. May be empty, but not <b>null</b>.
   */
  Collection<InstanceExpression> findAll(SubstantialTypeExpression type);

  /**
   * Retrieves whether the handler can operate with a given substantial type expression.
   * 
   * @param type
   *    The {@link SubstantialTypeExpression} to check for.
   * @return
   *    <b>true</b> if and only if this handler supports the given substantial type expression.
   */
  boolean isHandlerFor(SubstantialTypeExpression type);

  /**
   * Retrieves whether the handler can operate with a given instance expression.
   * 
   * @param instance
   *    The {@link InstanceExpression} to check for.
   * @return
   *    <b>true</b> if and only if this handler supports the given instance expression.
   */
  boolean isHandlerFor(InstanceExpression instance);
}

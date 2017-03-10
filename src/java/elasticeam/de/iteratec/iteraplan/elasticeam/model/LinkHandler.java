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

import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;


/**
 * A handler which specifies the operations available for {@link LinkExpression}s.
 */
public interface LinkHandler {

  /**
   * Creates a new link expression of a given relationship type.
   * 
   * @param type
   *    The type of the new link expression.
   * @return
   *    The new link expression.
   */
  LinkExpression create(RelationshipTypeExpression type);

  /**
   * Retrieves all link expressions of a given type.
   * 
   * @param type
   *    The type to whose link expression instances are to be fetched.
   * @return
   *    A collection of link expressions of the given type. May be empty, but not <b>null</b>. 
   */
  Collection<LinkExpression> findAll(RelationshipTypeExpression type);

  /**
   * Deletes a link expression.
   * 
   * @param link
   *    The link expression to delete.
   */
  void delete(LinkExpression link);

  /**
   * Retrieves whether the handler can operate with a given relationship type expression.
   * @param type
   *    The {@link RelationshipTypeExpression} to check for.
   * @return
   *    <b>true</b> if and only if this handler supports the given relationship type expression.
   */
  boolean isHandlerFor(RelationshipTypeExpression type);

  /**
   * Retrieves whether the handler can operate with a given link instance.
   * 
   * @param link
   *    The {@link LinkExpression} to check for.
   * @return
   *    <b>true</b> if and only if this handler supports the given link expression.
   */
  boolean isHandlerFor(LinkExpression link);
}

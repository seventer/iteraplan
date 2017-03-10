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

import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;


/**
 * Describes the operations which a model supports for linking resources.
 */
public interface ConnectHandler {

  /**
   * Creates a relationship between two universal model expressions over a given relationship end expression.
   * 
   * @param from
   *    The instance whose type is the 'holder' of the relationship end, i.e. the one for which the relationship is set.
   * @param via
   *    The {@link RelationshipEndExpression} which represents the kind of relationship to set.
   * @param to
   *    The instance whose type is the 'type' of the relationship, i.e. the one which the relationship leads to.
   */
  void link(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to);

  /**
   * For a given universal model expression this method retrieves a related universal model expression, or a
   * collection of universal model expressions, over a given relationship end.
   * 
   * @param universal
   *    The {@link UniversalModelExpression} from which to search.
   * @param relationshipEnd
   *    The {@link RelationshipEndExpression} over which to search.
   * @return
   *    A single universal model expression, a collection of universal model expression,
   *    or <b>null</b> if not related entities exist.
   */
  Object getValue(UniversalModelExpression universal, RelationshipEndExpression relationshipEnd);

  /**
   * Removes the relationship between two universal model expressions over a given relationship end expression.
   * 
   * @param from
   *    The instance for which the relationship end is outgoing, i.e. the 'holder' of the relations.
   * @param via
   *    The {@link RelationshipEndExpression} which specifies the kind of relationship to remove.
   * @param to
   *    The destination instance, i.e. the one which the relationship leads to.
   */
  void unlink(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to);

  /**
   * Retrieves whether the handler can operate with a given relationship end expression.
   * 
   * @param relationshipEnd
   *    The {@link RelationshipEndExpression} to check for.
   * @return
   *    <b>true</b> if and only if this handler supports the given relation end expression.
   */
  boolean isHandlerFor(RelationshipEndExpression relationshipEnd);

  /**
   * Creates a binding set which contains all instances of the 'holder'
   * of a given relationship end expression as its first component, all
   * instances of the 'type' of the given relationship end expression as its
   * second component and bindings between each two instances of the two
   * {@link UniversalTypeExpression}s which are related over the given
   * relationship end expression.
   * <br/><br/>
   * Note: An instance of the 'holder' or 'type' will not be included
   * if it is not related to any instance of the 'type' or 'holder', respectively. 
   * 
   * @param via
   *    The {@link RelationshipEndExpression} for which to build a binding set.
   * @return
   *    The created binding set. May be empty, but not <b>null</b>.
   */
  BindingSet findAll(RelationshipEndExpression via);
}

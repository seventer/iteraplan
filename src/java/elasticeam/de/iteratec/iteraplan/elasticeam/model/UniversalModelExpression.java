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

import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;


/**
 * A universal model expression a common super type for instances of {@link SubstantialTypeExpression}s and {@link RelationshipTypeExpression}s.
 */
public interface UniversalModelExpression {

  /**
   * Retrieves a value of a property set for this universal model expression.
   * If the property has multiple values, only one of them will be returned.
   * 
   * @param property
   *    The property expression whose value is of interest.
   * @return
   *    One value of the property expression, or <b>null</b> if none is set.
   */
  Object getValue(PropertyExpression<?> property);

  /**
   * Returns all values of a property which are set for this universal model expression.
   * If the property has just one value, the value is added to a collection, which is returned.
   * 
   * @param property
   *    The property expression whose values are of interest.
   * @return
   *    A collection of property values. Never <b>null</b>, but possibly empty.
   */
  Collection<Object> getValues(PropertyExpression<?> property);

  /**
   * Sets the value of a property for this universal model expression.
   * Note: The new value overwrites the old one, even if the old value is a collection.
   * 
   * @param property
   *    The property expression whose value to set.
   * @param value
   *    The value to set.
   */
  void setValue(PropertyExpression<?> property, Object value);

  /**
   * Retrieves one of the universal model expressions connected over a relationship end.
   * 
   * @param relationshipEnd
   *    The {@link RelationshipEndExpression} over which to fetch.
   * @return
   *    One of the related universal model expressions, or <b>null</b> if none are connected.
   */
  UniversalModelExpression getConnected(RelationshipEndExpression relationshipEnd);

  /**
   * Retrieves all universal model expressions related to this one over a relationship end.
   * @param relationshipEnd
   *    The {@link RelationshipEndExpression} over which to fetch.
   * @return
   *    A collection containing all related universal model expressions. Can not be <b>null</b>, but may be empty.
   */
  Collection<UniversalModelExpression> getConnecteds(RelationshipEndExpression relationshipEnd);

  /**
   * Relates this universal model expression over a relationship end with the universal model expression(s)
   * provided.
   * 
   * @param relationshipEnd
   *    The {@link RelationshipEndExpression} to connect through.
   * @param value
   *    Either a single universal model expression, or a collection of universal model expressions.
   */
  void connect(RelationshipEndExpression relationshipEnd, Object value);

  /**
   * Removes the relationship between this universal model expression and another universal model expression,
   * or a collection of universal model expressions, over the given relationship end.
   * 
   * @param relationshipEnd
   *    The {@link RelationshipEndExpression} with regard to which the relationships are to be removed.
   * @param value
   *    A universal model expression or a collection of universal model expressions to disconnect.
   */
  void disconnect(RelationshipEndExpression relationshipEnd, Object value);

}
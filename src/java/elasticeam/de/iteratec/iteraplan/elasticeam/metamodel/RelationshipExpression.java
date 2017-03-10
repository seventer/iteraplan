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

import java.util.List;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;


/**
 * A {@link RelationshipExpression} represents the bidirectional association between
 * two {@link UniversalTypeExpression}s. Each of the two directions of the association
 * is expressed through one of the two attached {@link RelationshipEndExpression}s.
 * <br><br> 
 * A {@link RelationshipEndExpression} should not be confused with a {@link RelationshipTypeExpression}
 * - the latter represents an abstract association, e.g. for representing n-ary relationships in a metamodel.
 */
public interface RelationshipExpression extends NamedExpression {

  /**
   * @return Retrieves the two {@link RelationshipEndExpression}s attached to this relationship.
   */
  List<RelationshipEndExpression> getRelationshipEnds();

  /**
   * Retrieves a {@link RelationshipEndExpression} by its name in the current locale.
   * 
   * @param name
   *    The localized name to search for in the current locale.
   * @return
   *    The {@link RelationshipEndExpression} corresponding to the given name in the current locale, or <b>null</b> if it can not be found.
   */
  RelationshipEndExpression findRelationshipEndByName(String name);

  /**
   * Retrieves a {@link RelationshipEndExpression} by its persistent name.
   * @param name
   *    The persistent name to search for.
   * @return
   *    The corresponding {@link RelationshipEndExpression}.
   */
  RelationshipEndExpression findRelationshipEndByPersistentName(String name);

  /**
   * Retrieves the {@link RelationshipEndExpression} whose type (destination) equals the one provided as an argument.
   * @param type
   *    The type to which the {@link RelationshipEndExpression} should lead.
   * @return
   *    The corresponding {@link RelationshipEndExpression} instance, or <b>null</b> if it can not be found.
   */
  RelationshipEndExpression getEndLeadingTo(UniversalTypeExpression type);

  /**
   * Retrieves the opposite {@link RelationshipEndExpression} for the one provided as an argument.
   * 
   * @param relationshipEnd
   *    The {@link RelationshipEndExpression} to use as reference for determining the opposite.
   * @return
   *    The {@link RelationshipEndExpression} which leads in the opposite direction.
   */
  RelationshipEndExpression getOppositeEndFor(RelationshipEndExpression relationshipEnd);

  /**
   * Whether the relationship is acyclic. A relationship is cyclic, when it is a self-referencing
   * relationship (relates a universal type to itself) and it is allowed that two instances of the
   * universal type are related with each other over the same relationship end in both directions.
   * For example, entity A is parent of entity B and entity B is parent of entity A simultaneously.
   * <br/><rb/>
   * In all cases but the one described above, a relationship is acyclic. In particular, non
   * self-referencing relationships are acyclic.
   * 
   * @return
   *    Whether the relationship is acyclic.
   */
  boolean isAcyclic();

  /**
   * @param ctx
   * @return
   *    Method is deprecated. Use the method with the same name which requires no context parameter instead.
   */
  @Deprecated
  List<RelationshipEndExpression> getRelationshipEnds(ElasticeamContext ctx);

  /**
   * @param ctx
   * @param name
   * @return
   *    Method is deprecated. Use the method with the same name which requires no context parameter instead.
   */
  @Deprecated
  RelationshipEndExpression findRelationshipEndByName(ElasticeamContext ctx, String name);

}

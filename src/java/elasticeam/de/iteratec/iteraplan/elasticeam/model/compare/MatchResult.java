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
package de.iteratec.iteraplan.elasticeam.model.compare;

import java.util.Map;
import java.util.Set;

import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


/**
 * Represents the result of the matching of two models.
 */
public interface MatchResult {

  /**
   * @return
   *    The metamodel with respect to which the matching was made.
   */
  Metamodel getMetamodel();

  /**
   * @return
   *    The left model used for the matching. By convention, this is 
   *    the model which is to be updated.
   */
  Model getLeftModel();

  /**
   * @return
   *    The right model used for the matching. By convention, this is
   *    the model which has been changed and is used to update the left model.
   */
  Model getRightModel();

  /**
   * The set of all matching pairs of this type. May be empty but not null.
   * @param type
   *    The type of the matching pairs.
   * @return
   *    The set of matching pairs.
   */
  Set<MatchingPair> getMatchesForType(UniversalTypeExpression type);

  /**
   * @return
   *  The mapping from universal type to matching pairs of this type.
   */
  Map<UniversalTypeExpression, Set<MatchingPair>> getMatches();

  /**
   * The set of expressions of the 'left' model which could not be matched to
   * any expression of the right model.
   * @param type
   *    The type of the instances.
   * @return
   *    The set of expressions.
   */
  Set<UniversalModelExpression> getLeftUnmatchedForType(UniversalTypeExpression type);

  /**
   * The set of expressions of the 'right' model which could not be matched to
   * any expression of the left model.
   * @param type
   *    The type of the instances.
   * @return
   *    The set of expressions.
   */
  Set<UniversalModelExpression> getRightUnmatchedForType(UniversalTypeExpression type);

  /**
   * @return
   *    A mapping from universal type to the set of all expressions of this type
   *    from the 'left' model which could not be matched.
   */
  Map<UniversalTypeExpression, Set<UniversalModelExpression>> getLeftUnmatched();

  /**
   * @return
   *    A mapping from universal type to the set of all expressions of this type
   *    from the 'right' model which could not be matched.
   */
  Map<UniversalTypeExpression, Set<UniversalModelExpression>> getRightUnmatched();

}

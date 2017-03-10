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
package de.iteratec.iteraplan.elasticeam.model.compare.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.MatchResult;
import de.iteratec.iteraplan.elasticeam.model.compare.MatchingPair;


public class MatchResultImpl implements MatchResult {

  private final Metamodel                                                   metamodel;

  private final Model                                                       leftModel;
  private final Model                                                       rightModel;

  private final Map<UniversalTypeExpression, Set<MatchingPair>>             matches;

  private final Map<UniversalTypeExpression, Set<UniversalModelExpression>> leftUnmatched;
  private final Map<UniversalTypeExpression, Set<UniversalModelExpression>> rightUnmatched;

  protected MatchResultImpl(Metamodel metamodel, Model leftModel, Model rightModel) {
    this.metamodel = metamodel;
    this.leftModel = leftModel;
    this.rightModel = rightModel;
    this.matches = new HashMap<UniversalTypeExpression, Set<MatchingPair>>();
    this.leftUnmatched = new HashMap<UniversalTypeExpression, Set<UniversalModelExpression>>();
    this.rightUnmatched = new HashMap<UniversalTypeExpression, Set<UniversalModelExpression>>();
  }

  protected void addMatch(UniversalTypeExpression type, UniversalModelExpression leftExpression, UniversalModelExpression rightExpression) {
    UniversalTypeExpression actualType = metamodel.findUniversalTypeByPersistentName(type.getPersistentName());
    if (actualType == null) {
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR, "The universal type " + type.getPersistentName()
          + " does not belong to the metamodel of this matcher.");
    }

    if (matches.get(actualType) == null) {
      matches.put(actualType, new HashSet<MatchingPair>());
    }

    matches.get(actualType).add(new MatchingPairImpl(this, actualType, leftExpression, rightExpression));
  }

  protected void addLeftUnmatched(UniversalTypeExpression type, UniversalModelExpression expression) {
    UniversalTypeExpression actualType = metamodel.findUniversalTypeByPersistentName(type.getPersistentName());
    if (actualType == null) {
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR, "The universal type " + type.getPersistentName()
          + " does not belong to the metamodel of this matcher.");
    }

    if (leftUnmatched.get(actualType) == null) {
      leftUnmatched.put(actualType, new HashSet<UniversalModelExpression>());
    }

    leftUnmatched.get(actualType).add(expression);
  }

  protected void addRightUnmatched(UniversalTypeExpression type, UniversalModelExpression expression) {
    UniversalTypeExpression actualType = metamodel.findUniversalTypeByPersistentName(type.getPersistentName());
    if (actualType == null) {
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR, "The universal type " + type.getPersistentName()
          + " does not belong to the metamodel of this matcher.");
    }

    if (rightUnmatched.get(actualType) == null) {
      rightUnmatched.put(actualType, new HashSet<UniversalModelExpression>());
    }

    rightUnmatched.get(actualType).add(expression);
  }

  /**{@inheritDoc}**/
  public Set<MatchingPair> getMatchesForType(UniversalTypeExpression type) {
    Set<MatchingPair> result = matches.get(type);
    if (result != null) {
      return result;
    }
    return new HashSet<MatchingPair>();
  }

  /**{@inheritDoc}**/
  public Map<UniversalTypeExpression, Set<MatchingPair>> getMatches() {
    return matches;
  }

  /**{@inheritDoc}**/
  public Metamodel getMetamodel() {
    return this.metamodel;
  }

  /**{@inheritDoc}**/
  public Model getLeftModel() {
    return this.leftModel;
  }

  /**{@inheritDoc}**/
  public Model getRightModel() {
    return this.rightModel;
  }

  /**{@inheritDoc}**/
  public Set<UniversalModelExpression> getLeftUnmatchedForType(UniversalTypeExpression type) {
    if (leftUnmatched.get(type) != null) {
      return leftUnmatched.get(type);
    }
    return new HashSet<UniversalModelExpression>();
  }

  /**{@inheritDoc}**/
  public Set<UniversalModelExpression> getRightUnmatchedForType(UniversalTypeExpression type) {
    if (rightUnmatched.get(type) != null) {
      return rightUnmatched.get(type);
    }
    return new HashSet<UniversalModelExpression>();
  }

  /**{@inheritDoc}**/
  public Map<UniversalTypeExpression, Set<UniversalModelExpression>> getLeftUnmatched() {
    return leftUnmatched;
  }

  /**{@inheritDoc}**/
  public Map<UniversalTypeExpression, Set<UniversalModelExpression>> getRightUnmatched() {
    return rightUnmatched;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(" --- MatchResult --- \n");
    builder.append("- metamodel: ");
    builder.append(metamodel);
    builder.append("\n- left model:");
    builder.append("");
    builder.append(leftModel);
    builder.append("\n- right model:");
    builder.append(rightModel);
    builder.append("\n- left unmatched expressions: ");
    for (Entry<UniversalTypeExpression, Set<UniversalModelExpression>> entry : leftUnmatched.entrySet()) {
      builder.append("-- type: ");
      builder.append(entry.getKey().getPersistentName());
      builder.append("\n--- expressions: ");
      for (UniversalModelExpression expression : entry.getValue()) {
        builder.append("  leftUnmatched(");
        builder.append(expression.getValue(UniversalTypeExpression.ID_PROPERTY));
        builder.append("), \n");
      }
    }
    builder.append("\n- right unmatched expressions:");
    for (Entry<UniversalTypeExpression, Set<UniversalModelExpression>> entry : rightUnmatched.entrySet()) {
      builder.append("-- type: ");
      builder.append(entry.getKey().getPersistentName());
      builder.append("\n--- expressions: ");
      for (UniversalModelExpression expression : entry.getValue()) {
        builder.append("  rightUnmatched(");
        builder.append(expression.getValue(UniversalTypeExpression.ID_PROPERTY));
        builder.append("), \n");
      }
    }
    builder.append("\n matched:");
    for (Entry<UniversalTypeExpression, Set<MatchingPair>> entry : matches.entrySet()) {
      builder.append("-- type: ");
      builder.append(entry.getKey().getPersistentName());
      builder.append("\n--- expressions: ");
      for (MatchingPair pair : entry.getValue()) {
        builder.append("  matched(");
        builder.append(pair.getLeftExpression().getValue(UniversalTypeExpression.ID_PROPERTY));
        builder.append(",");
        builder.append(pair.getRightExpression().getValue(UniversalTypeExpression.ID_PROPERTY));
        builder.append("), \n");
      }
    }
    return builder.toString();
  }

}

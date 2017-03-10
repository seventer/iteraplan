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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.MatchResult;
import de.iteratec.iteraplan.elasticeam.model.compare.Matcher;


/**
 * Matches two models.
 */
public class MatcherImpl implements Matcher {

  /**
   * Compares the universal model expressions based on their IDs. If null-values occur both left and right side of the comparison,
   * you should really think of using a different comparator!
   */
  public static final Comparator<UniversalModelExpression> IDCOMPARATOR   = new ModelIdComparator();
  /**
   * Compares the universal model expressions based on their Names. RelationshipTypes should be compared based on the names of their
   * related elements. still incomplete
   */
  public static final Comparator<UniversalModelExpression> NAMECOMPARATOR = new ModelIdComparator();

  private final Metamodel                                  metamodel;
  private final Comparator<UniversalModelExpression>       comparator;

  public MatcherImpl(Metamodel metamodel, Comparator<UniversalModelExpression> comparator) {
    this.metamodel = metamodel;
    this.comparator = comparator;
  }

  /**{@inheritDoc}**/
  public MatchResult match(Model leftModel, Model rightModel) {
    MatchResultImpl matchResult = new MatchResultImpl(metamodel, leftModel, rightModel);
    for (UniversalTypeExpression universalType : this.metamodel.getUniversalTypes()) {
      match(Lists.newArrayList(leftModel.findAll(universalType)), Lists.newArrayList(rightModel.findAll(universalType)), universalType, matchResult);
    }
    return matchResult;
  }

  private void match(List<UniversalModelExpression> leftExpressions, List<UniversalModelExpression> rightExpressions, UniversalTypeExpression type,
                     MatchResultImpl result) {
    Collections.sort(leftExpressions, this.comparator);
    Collections.sort(rightExpressions, this.comparator);
    int leftIndex = 0;
    int rightIndex = 0;
    while (leftIndex < leftExpressions.size()) {
      UniversalModelExpression leftExpression = leftExpressions.get(leftIndex);
      UniversalModelExpression rightExpression = (rightIndex < rightExpressions.size()) ? rightExpressions.get(rightIndex) : null;
      while (rightExpression != null && this.comparator.compare(leftExpression, rightExpression) > 0) {
        result.addRightUnmatched(type, rightExpression);
        rightIndex++;
        rightExpression = (rightIndex < rightExpressions.size()) ? rightExpressions.get(rightIndex) : null;
      }
      if (rightExpression != null && this.comparator.compare(leftExpression, rightExpression) == 0) {
        result.addMatch(type, leftExpression, rightExpression);
        rightIndex++;
      }
      else {
        result.addLeftUnmatched(type, leftExpression);
      }
      leftIndex++;
    }
    while (rightIndex < rightExpressions.size()) {
      result.addRightUnmatched(type, rightExpressions.get(rightIndex++));
    }
  }

  private static class ModelIdComparator implements Comparator<UniversalModelExpression>, Serializable {

    /** Serialization version */
    private static final long serialVersionUID = 5317705210586233947L;

    public int compare(UniversalModelExpression o1, UniversalModelExpression o2) {
      BigInteger id1 = (BigInteger) o1.getValue(UniversalTypeExpression.ID_PROPERTY);
      BigInteger id2 = (BigInteger) o2.getValue(UniversalTypeExpression.ID_PROPERTY);

      if (id1 == null) {
        return id2 == null ? 0 : -1;
      }
      else {
        return id2 == null ? 1 : id1.compareTo(id2);
      }
    }

  }

  /**
   * TODO still incomplete
   */
  private static class ModelNameComparator implements Comparator<UniversalModelExpression>, Serializable {

    /** Serialization version */
    private static final long serialVersionUID = 804517920405758972L;

    public int compare(UniversalModelExpression o1, UniversalModelExpression o2) {
      if (o1 instanceof InstanceExpression) {
        if (o2 instanceof InstanceExpression) {
          String name1 = (String) o1.getValue(MixinTypeNamed.NAME_PROPERTY);
          String name2 = (String) o2.getValue(MixinTypeNamed.NAME_PROPERTY);
          return name1.compareTo(name2);
        }
        else {
          return 1;
        }
      }
      else {
        if (o2 instanceof InstanceExpression) {
          return -1;
        }
        else {
          // TODO compare of two link expressions
          return 0;
        }
      }
    }

  }
}

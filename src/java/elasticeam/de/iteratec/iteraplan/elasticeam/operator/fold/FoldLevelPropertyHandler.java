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
package de.iteratec.iteraplan.elasticeam.operator.fold;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.PropertyHandler;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.ADerivedHandler;


public class FoldLevelPropertyHandler extends ADerivedHandler implements PropertyHandler {

  public FoldLevelPropertyHandler(Model model) {
    super(model);
  }

  /**{@inheritDoc}**/
  public Object getValue(UniversalModelExpression expression, PropertyExpression<?> property) {
    Set<UniversalModelExpression> collected = new HashSet<UniversalModelExpression>();
    collected.add(expression);

    long depth = computeDistance(collected, new HashSet<UniversalModelExpression>(), ((FoldLevelProperty) property).getRelationshipEnd());
    return BigInteger.valueOf(depth);
  }

  private static long computeDistance(Set<UniversalModelExpression> toTraverse, Set<UniversalModelExpression> alreadyCheked,
                                      RelationshipEndExpression relEnd) {
    Set<UniversalModelExpression> nextStepExpressions = new HashSet<UniversalModelExpression>();
    for (UniversalModelExpression expr : toTraverse) {
      Collection<UniversalModelExpression> related = expr.getConnecteds(relEnd);
      if (related.size() == 0) {
        return 0;
      }
      for (UniversalModelExpression relatedExpr : related) {
        if (!alreadyCheked.contains(relatedExpr)) {
          nextStepExpressions.add(relatedExpr);
        }
      }
      alreadyCheked.add(expr);
    }

    return 1 + computeDistance(nextStepExpressions, alreadyCheked, relEnd);
  }

  /**{@inheritDoc}**/
  public void setValue(UniversalModelExpression expression, PropertyExpression<?> property, Object value) {
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  public boolean isHandlerFor(PropertyExpression<?> property) {
    return FoldLevelProperty.class.isInstance(property);
  }
}

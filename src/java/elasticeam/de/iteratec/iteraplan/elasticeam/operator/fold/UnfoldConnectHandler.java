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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.ConnectHandler;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.ADerivedHandler;


public class UnfoldConnectHandler extends ADerivedHandler implements ConnectHandler {

  public UnfoldConnectHandler(Model model) {
    super(model);
  }

  /**{@inheritDoc}**/
  public Object getValue(UniversalModelExpression universal, RelationshipEndExpression relationshipEnd) {
    Set<UniversalModelExpression> startFrom = new HashSet<UniversalModelExpression>();
    startFrom.add(universal);

    Set<UniversalModelExpression> result = collect(startFrom, startFrom, ((UnfoldRelationshipEnd) relationshipEnd).getBaseRelationshipEnd());

    if (result.size() == 0) {
      return null;
    }
    else if (result.size() == 1) {
      return result.iterator().next();
    }
    return result;
  }

  /**{@inheritDoc}**/
  public BindingSet findAll(RelationshipEndExpression via) {
    UnfoldRelationshipEnd undfoldRelEnd = (UnfoldRelationshipEnd) via;

    BindingSet result = new BindingSet();
    result.setFromType(undfoldRelEnd.getBaseRelationshipEnd().getOrigin());
    result.setToType(undfoldRelEnd.getBaseRelationshipEnd().getType());

    for (UniversalModelExpression expression : getModel().findAll(undfoldRelEnd.getBaseRelationshipEnd().getHolder())) {
      Set<UniversalModelExpression> startFrom = new HashSet<UniversalModelExpression>();
      startFrom.add(expression);

      Set<UniversalModelExpression> reachable = collect(startFrom, startFrom, undfoldRelEnd.getBaseRelationshipEnd());
      for (UniversalModelExpression reachableExpression : reachable) {
        result.addBinding(expression, reachableExpression);
      }
    }

    return result;
  }

  private static Set<UniversalModelExpression> collect(Set<UniversalModelExpression> fromExpressions, Set<UniversalModelExpression> alreadyChecked,
                                                       RelationshipEndExpression overRelationship) {
    Set<UniversalModelExpression> collectedExpressions = new HashSet<UniversalModelExpression>();
    Set<UniversalModelExpression> nextStepExpressions = new HashSet<UniversalModelExpression>();

    for (UniversalModelExpression start : fromExpressions) {
      alreadyChecked.add(start);
      Collection<UniversalModelExpression> reachable = start.getConnecteds(overRelationship);
      for (UniversalModelExpression reachableExpression : reachable) {
        if (!alreadyChecked.contains(reachableExpression)) {
          nextStepExpressions.add(reachableExpression);
        }
        collectedExpressions.add(reachableExpression);
      }
    }
    if (nextStepExpressions.size() > 0) {
      collectedExpressions.addAll(collect(nextStepExpressions, alreadyChecked, overRelationship));
    }
    return collectedExpressions;
  }

  /**{@inheritDoc}**/
  public boolean isHandlerFor(RelationshipEndExpression relationshipEnd) {
    return UnfoldRelationshipEnd.class.isInstance(relationshipEnd);
  }

  /**{@inheritDoc}**/
  public void link(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to) {
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  public void unlink(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to) {
    throw new UnsupportedOperationException();
  }

}

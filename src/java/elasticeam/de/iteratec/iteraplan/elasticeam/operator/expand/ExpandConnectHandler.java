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
package de.iteratec.iteraplan.elasticeam.operator.expand;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.ConnectHandler;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.ADerivedHandler;


public class ExpandConnectHandler extends ADerivedHandler implements ConnectHandler {

  public ExpandConnectHandler(Model model) {
    super(model);
  }

  private Collection<InstanceExpression> reachableTo(UniversalModelExpression fromExpression, ToExpandedRelationshipEnd relEnd) {
    Collection<InstanceExpression> result = Sets.newHashSet();
    result.add((InstanceExpression) fromExpression);
    for (UniversalModelExpression expr : fromExpression.getConnecteds(relEnd.getBaseRelationshipEnd())) {
      result.add((InstanceExpression) expr);
    }

    return result;
  }

  private Collection<InstanceExpression> reachableFrom(InstanceExpression fromExpandedInstance, FromExpandedRelationshipEnd relEnd) {
    Collection<UniversalModelExpression> candidates = getModel().findAll(relEnd.getType());
    Set<InstanceExpression> result = Sets.newHashSet();

    if (candidates.contains(fromExpandedInstance)) {
      result.add(fromExpandedInstance);
    }

    for (UniversalModelExpression expr : fromExpandedInstance.getConnecteds(relEnd.getBaseRelationshipEnd())) {
      if (candidates.contains(expr)) {
        result.add((InstanceExpression) expr);
      }
    }

    return result;
  }

  /**{@inheritDoc}**/
  public Object getValue(UniversalModelExpression universal, RelationshipEndExpression relationshipEnd) {
    Collection<InstanceExpression> result = Sets.newHashSet();
    if (ToExpandedRelationshipEnd.class.isInstance(relationshipEnd)) {
      result.addAll(reachableTo(universal, (ToExpandedRelationshipEnd) relationshipEnd));
    }
    else {
      result.addAll(reachableFrom((InstanceExpression) universal, (FromExpandedRelationshipEnd) relationshipEnd));
    }

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
    BindingSet result = new BindingSet();
    result.setFromType(via.getHolder());
    result.setToType(via.getType());
    if (ToExpandedRelationshipEnd.class.isInstance(via)) {
      for (UniversalModelExpression fromExpr : getModel().findAll(via.getHolder())) {
        for (InstanceExpression toExpr : reachableTo(fromExpr, (ToExpandedRelationshipEnd) via)) {
          result.addBinding(fromExpr, toExpr);
        }
      }
    }
    else {
      for (UniversalModelExpression fromExpandedExpr : getModel().findAll(via.getHolder())) {
        for (InstanceExpression toExpr : reachableFrom((InstanceExpression) fromExpandedExpr, (FromExpandedRelationshipEnd) via)) {
          result.addBinding(fromExpandedExpr, toExpr);
        }
      }
    }

    return result;
  }

  /**{@inheritDoc}**/
  public void link(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to) {
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  public void unlink(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to) {
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  public boolean isHandlerFor(RelationshipEndExpression relationshipEnd) {
    return FromExpandedRelationshipEnd.class.isInstance(relationshipEnd) || ToExpandedRelationshipEnd.class.isInstance(relationshipEnd);
  }

}

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
package de.iteratec.iteraplan.elasticeam.operator.nullify;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.ConnectHandler;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.ADerivedHandler;


public class NullifyConnectHandler extends ADerivedHandler implements ConnectHandler {

  public NullifyConnectHandler(Model model) {
    super(model);
  }

  /**{@inheritDoc}**/
  public Object getValue(UniversalModelExpression universal, RelationshipEndExpression relationshipEnd) {
    Collection<UniversalModelExpression> reachable = Lists.newLinkedList();//getReachableExpressions(universal, (NullifiedRelationshipEnd) relationshipEnd);

    if (FromNullifiedRelationshipEnd.class.isInstance(relationshipEnd)) {
      reachable.addAll(getReachable(universal, (FromNullifiedRelationshipEnd) relationshipEnd));
    }
    else {
      Collection<UniversalModelExpression> reachableToElements = universal.getConnecteds(((ToNullifiedRelationshipEnd) relationshipEnd)
          .getBaseRelationshipEnd());
      if (reachableToElements.size() == 0) {
        reachable.add(getNullifiedToInstance((ToNullifiedRelationshipEnd) relationshipEnd));
      }
      else {
        reachable = reachableToElements;
      }
    }

    if (reachable.isEmpty()) {
      return null;
    }
    else if (reachable.size() == 1) {
      return reachable.iterator().next();
    }
    return reachable;

  }

  /**{@inheritDoc}**/
  public BindingSet findAll(RelationshipEndExpression via) {
    BindingSet result = new BindingSet();
    result.setFromType(via.getHolder());
    result.setToType(via.getType());

    if (FromNullifiedRelationshipEnd.class.isInstance(via)) {
      for (UniversalModelExpression from : getModel().findAll(via.getHolder())) {
        for (UniversalModelExpression to : getReachable(from, (FromNullifiedRelationshipEnd) via)) {
          result.addBinding(from, to);
        }
      }
    }
    else {
      List<UniversalModelExpression> unreachableFromElements = Lists.newArrayList();
      for (UniversalModelExpression from : getModel().findAll(via.getHolder())) {
        Collection<UniversalModelExpression> reachableToElements = from.getConnecteds(((ToNullifiedRelationshipEnd) via).getBaseRelationshipEnd());
        if (reachableToElements.size() == 0) {
          unreachableFromElements.add(from);
        }
        else {
          for (UniversalModelExpression toElement : reachableToElements) {
            result.addBinding(from, toElement);
          }
        }
      }
      if (unreachableFromElements.size() > 0) {
        UniversalModelExpression nullToElement = getNullifiedToInstance((ToNullifiedRelationshipEnd) via);
        for (UniversalModelExpression fromElement : unreachableFromElements) {
          result.addBinding(fromElement, nullToElement);
        }
      }
    }

    return result;
  }

  private UniversalModelExpression getNullifiedToInstance(ToNullifiedRelationshipEnd via) {
    if (SubstantialTypeExpression.class.isInstance(via.getType())) {
      return new NullifiedInstance(getModel(), (NullifiedSubstantialType) via.getType());
    }
    return new NullifiedLink(getModel(), (NullifiedRelationshipType) via.getType());
  }

  private Collection<UniversalModelExpression> getReachable(UniversalModelExpression fromInstance, FromNullifiedRelationshipEnd via) {
    List<UniversalModelExpression> result = Lists.newArrayList();

    if (NullifiedModelExpression.class.isInstance(fromInstance)) {
      RelationshipEndExpression baseEnd = via.getBaseRelationshipEnd();

      for (UniversalModelExpression candidate : getModel().findAll(baseEnd.getType())) {
        if (candidate.getConnecteds(baseEnd.getRelationship().getOppositeEndFor(baseEnd)).size() == 0) {
          result.add(candidate);
        }
      }
    }
    else {
      result.addAll(fromInstance.getConnecteds(via.getBaseRelationshipEnd()));
    }

    return result;
  }

  /**{@inheritDoc}**/
  public boolean isHandlerFor(RelationshipEndExpression relationshipEnd) {
    return relationshipEnd instanceof ToNullifiedRelationshipEnd || relationshipEnd instanceof FromNullifiedRelationshipEnd;
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

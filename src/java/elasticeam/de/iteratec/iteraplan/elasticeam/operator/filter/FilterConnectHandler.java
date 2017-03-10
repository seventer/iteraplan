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
package de.iteratec.iteraplan.elasticeam.operator.filter;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.ConnectHandler;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.ADerivedHandler;


/**
 *
 */
public class FilterConnectHandler extends ADerivedHandler implements ConnectHandler {

  public FilterConnectHandler(Model model) {
    super(model);
  }

  /**{@inheritDoc}**/
  public void link(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to) {
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  @SuppressWarnings("unchecked")
  public Object getValue(UniversalModelExpression universal, RelationshipEndExpression relationshipEnd) {
    if (relationshipEnd instanceof FromFilteredRelationshipEnd) {
      return getModel().getValue(universal, ((FromFilteredRelationshipEnd) relationshipEnd).getBaseRelationshipEnd());
    }
    else {
      ToFilteredRelationshipEnd re = ((ToFilteredRelationshipEnd) relationshipEnd);
      Object val = getModel().getValue(universal, re.getBaseRelationshipEnd());
      if (val instanceof Collection) {
        return Collections2.filter((Collection<UniversalModelExpression>) val, re.getPredicate());
      }
      else if (val != null && re.getPredicate().apply((UniversalModelExpression) val)) {
        return val;
      }
      return null;
    }
  }

  /**{@inheritDoc}**/
  public void unlink(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to) {
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  public boolean isHandlerFor(RelationshipEndExpression relationshipEnd) {
    return relationshipEnd instanceof FromFilteredRelationshipEnd || relationshipEnd instanceof ToFilteredRelationshipEnd;
  }

  /**{@inheritDoc}**/
  public BindingSet findAll(RelationshipEndExpression via) {
    Predicate<UniversalModelExpression> alwaysTrue = Predicates.alwaysTrue();
    if (via instanceof FromFilteredRelationshipEnd) {
      FromFilteredRelationshipEnd fVia = (FromFilteredRelationshipEnd) via;
      return getModel().findAll(fVia.getBaseRelationshipEnd()).filterBindingSet(fVia.getPredicate(), alwaysTrue);
    }
    else {
      ToFilteredRelationshipEnd fVia = (ToFilteredRelationshipEnd) via;
      return getModel().findAll(fVia.getBaseRelationshipEnd()).filterBindingSet(alwaysTrue, fVia.getPredicate());
    }
  }
}

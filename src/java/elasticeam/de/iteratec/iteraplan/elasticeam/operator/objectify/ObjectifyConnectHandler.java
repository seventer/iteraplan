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
package de.iteratec.iteraplan.elasticeam.operator.objectify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.ConnectHandler;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.ADerivedHandler;


/**
 * Connect handler for relationship ends derived through the Objectify Operator.
 */
public class ObjectifyConnectHandler extends ADerivedHandler implements ConnectHandler {

  public ObjectifyConnectHandler(Model model) {
    super(model);
  }

  /**{@inheritDoc}**/
  public Object getValue(UniversalModelExpression universal, RelationshipEndExpression relationshipEnd) {
    List<Object> result = new ArrayList<Object>();

    if (ObjectifiedInstanceExpression.class.isInstance(universal)) {
      //re.source is objectified -> 
      Collection<UniversalModelExpression> candidates = getModel().findAll(relationshipEnd.getType());
      for (UniversalModelExpression candidate : candidates) {
        Collection<Object> vals = candidate.getValues(((ObjectifiedInstanceExpression) universal).getOwningType().getObjectifiedProperty());
        for (Object val : vals) {
          if (val.equals(((ObjectifiedInstanceExpression) universal).getWrappedPropertyValue())) {
            result.add(candidate);
          }

        }
      }
    }
    else {
      // the universal is an instance of a canonic type
      ObjectifyingSubstantialType oSubstantialType = (ObjectifyingSubstantialType) ((ToObjectifiedRelationshipEnd) relationshipEnd).getType();
      Collection<Object> values = universal.getValues(oSubstantialType.getObjectifiedProperty());
      for (Object val : values) {
        result.add(new ObjectifiedInstanceExpression(getModel(), oSubstantialType, val));
      }
    }
    if (result.size() == 0) {
      return null;
    }
    else if (result.size() == 1) {
      return result.get(0);
    }
    else {
      return result;
    }
  }

  /**{@inheritDoc}**/
  public BindingSet findAll(RelationshipEndExpression via) {
    Collection<UniversalModelExpression> sources = getModel().findAll(via.getOrigin());

    BindingSet result = new BindingSet();
    result.setFromType(via.getOrigin());
    result.setToType(via.getType());
    for (UniversalModelExpression source : sources) {
      Collection<UniversalModelExpression> destinations = source.getConnecteds(via);
      for (UniversalModelExpression destination : destinations) {
        result.addBinding(source, destination);
      }
    }

    return result;
  }

  /**{@inheritDoc}**/
  public boolean isHandlerFor(RelationshipEndExpression relationshipEnd) {
    return relationshipEnd instanceof ToObjectifiedRelationshipEnd || relationshipEnd instanceof FromObjectifiedRelationshipEnd;
  }

  /**{@inheritDoc}**/
  public void unlink(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to) {
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  public void link(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to) {
    throw new UnsupportedOperationException();
  }
}

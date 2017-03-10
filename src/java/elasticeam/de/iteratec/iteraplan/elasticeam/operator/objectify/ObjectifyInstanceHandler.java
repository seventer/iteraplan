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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.InstanceHandler;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.ADerivedHandler;


/**
 * Instance handler for instances derived through the Objectify Operator.
 */
public class ObjectifyInstanceHandler extends ADerivedHandler implements InstanceHandler {

  public ObjectifyInstanceHandler(Model model) {
    super(model);
  }

  public Collection<InstanceExpression> findAll(SubstantialTypeExpression type) {
    ObjectifyingSubstantialType derivedExpression = (ObjectifyingSubstantialType) type;
    PropertyExpression<?> property = derivedExpression.getObjectifiedProperty();
    Collection<UniversalModelExpression> originalInstances = getModel().findAll(property.getHolder());

    Map<Object, InstanceExpression> result = new HashMap<Object, InstanceExpression>();

    for (UniversalModelExpression expr : originalInstances) {
      Collection<Object> values = expr.getValues(property);
      for (Object val : values) {
        if (!result.containsKey(val)) {
          result.put(val, new ObjectifiedInstanceExpression(getModel(), derivedExpression, val));
        }
      }
    }

    return result.values();
  }

  public boolean isHandlerFor(SubstantialTypeExpression type) {
    return type instanceof ObjectifyingSubstantialType;
  }

  public boolean isHandlerFor(InstanceExpression instance) {
    return instance instanceof ObjectifiedInstanceExpression;
  }

  /**{@inheritDoc}**/
  public void delete(InstanceExpression instance) {
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  public InstanceExpression create(SubstantialTypeExpression type) {
    throw new UnsupportedOperationException();
  }

}

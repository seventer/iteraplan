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
package de.iteratec.iteraplan.elasticeam.operator.power;

import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.ConnectHandler;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.ADerivedHandler;


public class PowerConnectHandler extends ADerivedHandler implements ConnectHandler {

  public PowerConnectHandler(Model model) {
    super(model);
  }

  /**{@inheritDoc}**/
  public Object getValue(UniversalModelExpression universal, RelationshipEndExpression relationshipEnd) {
    if (relationshipEnd.getOrigin() instanceof PowerSubstantialType) {
      return universal.getConnecteds(relationshipEnd);
    }
    else {
      return new PowerInstanceExpression(getModel(), (PowerSubstantialType) relationshipEnd.getType());
    }
  }

  /**{@inheritDoc}**/
  public boolean isHandlerFor(RelationshipEndExpression relationshipEnd) {
    return relationshipEnd instanceof ToPowerRelationshipEnd || relationshipEnd instanceof FromPowerRelationshipEnd;
  }

  /**{@inheritDoc}**/
  public BindingSet findAll(RelationshipEndExpression via) {
    BindingSet result = new BindingSet();
    result.setFromType(via.getOrigin());
    result.setToType(via.getType());

    if (via.getOrigin() instanceof PowerSubstantialType) {
      UniversalModelExpression powerInstance = new PowerInstanceExpression(getModel(), (PowerSubstantialType) via.getOrigin());
      for (UniversalModelExpression destination : powerInstance.getConnecteds(via)) {
        result.addBinding(powerInstance, destination);
      }
    }
    else {
      UniversalModelExpression powerInstance = new PowerInstanceExpression(getModel(), (PowerSubstantialType) via.getType());
      for (UniversalModelExpression from : getModel().findAll(via.getOrigin())) {
        result.addBinding(from, powerInstance);
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

}

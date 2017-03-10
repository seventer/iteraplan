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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.ADerivedExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.DerivedInstanceExpression;


public class PowerInstanceExpression extends ADerivedExpression implements DerivedInstanceExpression {

  private String               powerInstanceName;
  private PowerSubstantialType powerType;

  protected PowerInstanceExpression(Model model, PowerSubstantialType powerType) {
    super(model);
    this.powerType = powerType;
    this.powerInstanceName = "PowerInstanceExpression(" + powerType.getPersistentName() + ")";
  }

  /**{@inheritDoc}**/
  public Object getValue(PropertyExpression<?> property) {
    if (!powerType.getProperties().contains(property)) {
      return null;
    }
    if (SubstantialTypeExpression.ID_PROPERTY.getPersistentName().equals(property.getPersistentName())) {
      return BigInteger.valueOf(powerInstanceName.hashCode());
    }
    return powerInstanceName;
  }

  /**{@inheritDoc}**/
  public Collection<Object> getValues(PropertyExpression<?> property) {
    Object val = getValue(property);
    if (val == null) {
      return Collections.emptyList();
    }
    return Collections.singleton(val);
  }

  /**{@inheritDoc}**/
  public void setValue(PropertyExpression<?> property, Object value) {
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  public UniversalModelExpression getConnected(RelationshipEndExpression relationshipEnd) {
    Collection<UniversalModelExpression> connected = getConnecteds(relationshipEnd);

    if (connected.size() == 0) {
      return null;
    }
    return connected.iterator().next();
  }

  /**{@inheritDoc}**/
  public Collection<UniversalModelExpression> getConnecteds(RelationshipEndExpression relationshipEnd) {
    if (!powerType.equals(relationshipEnd.getOrigin())) {
      throw new ModelException(ModelException.GENERAL_ERROR, "Can not evaluate the relationship end " + relationshipEnd + " on the power type "
          + powerType);
    }

    List<UniversalModelExpression> result = new ArrayList<UniversalModelExpression>();
    if (SubstantialTypeExpression.class.isInstance(relationshipEnd.getType())) {
      result.addAll(getModel().findAll((SubstantialTypeExpression) relationshipEnd.getType()));
    }
    else {
      result.addAll(getModel().findAll((RelationshipTypeExpression) relationshipEnd.getType()));
    }

    return result;
  }

  /**{@inheritDoc}**/
  public void connect(RelationshipEndExpression relationshipEnd, Object value) {
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  public void disconnect(RelationshipEndExpression relationshipEnd, Object value) {
    throw new UnsupportedOperationException();
  }

}

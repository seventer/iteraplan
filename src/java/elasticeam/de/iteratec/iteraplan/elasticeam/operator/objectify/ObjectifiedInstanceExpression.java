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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.ADerivedExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.DerivedInstanceExpression;


public final class ObjectifiedInstanceExpression extends ADerivedExpression implements DerivedInstanceExpression {

  private final ObjectifyingSubstantialType owningType;
  private final Object                      wrappsPropertyValue;

  protected ObjectifiedInstanceExpression(Model model, ObjectifyingSubstantialType owningType, Object wrappsPropertyValue) {
    super(model);
    this.owningType = owningType;
    this.wrappsPropertyValue = wrappsPropertyValue;
  }

  protected ObjectifyingSubstantialType getOwningType() {
    return owningType;
  }

  protected Object getWrappedPropertyValue() {
    return wrappsPropertyValue;
  }

  /**{@inheritDoc}**/
  public Object getValue(PropertyExpression<?> property) {
    if (!owningType.getProperties().contains(property)) {
      return null;
    }

    if (property.getName().equals(SubstantialTypeExpression.ID_PROPERTY.getPersistentName())) {
      return BigInteger.valueOf(wrappsPropertyValue.hashCode());
    }
    else {
      if (wrappsPropertyValue instanceof EnumerationLiteralExpression) {
        return ((EnumerationLiteralExpression) wrappsPropertyValue).getPersistentName();
      }
      else {
        return wrappsPropertyValue == null ? null : wrappsPropertyValue.toString();
      }
    }
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
    Collection<UniversalModelExpression> allConnected = getConnecteds(relationshipEnd);
    if (allConnected.size() == 0) {
      return null;
    }
    else {
      return allConnected.iterator().next();
    }
  }

  /**{@inheritDoc}**/
  public Collection<UniversalModelExpression> getConnecteds(RelationshipEndExpression relationshipEnd) {
    if (!owningType.equals(relationshipEnd.getOrigin())) {
      throw new ModelException(ModelException.GENERAL_ERROR, "Can not evaluate relationship end " + relationshipEnd + " on the objectified type "
          + owningType);
    }
    List<UniversalModelExpression> result = new ArrayList<UniversalModelExpression>();

    Collection<UniversalModelExpression> candidates = new ArrayList<UniversalModelExpression>();
    if (SubstantialTypeExpression.class.isInstance(relationshipEnd.getType())) {
      candidates.addAll(getModel().findAll((SubstantialTypeExpression) relationshipEnd.getType()));
    }
    else if (RelationshipTypeExpression.class.isInstance(relationshipEnd.getType())) {
      candidates.addAll(getModel().findAll((RelationshipTypeExpression) relationshipEnd.getType()));
    }
    else {
      throw new ModelException(ModelException.GENERAL_ERROR,
          "Inconsistent metamodel: A relationship end should only have a substatntial or a relationship type at its end.");
    }

    for (UniversalModelExpression ure : candidates) {
      Collection<Object> values = ure.getValues(getOwningType().getObjectifiedProperty());
      for (Object val : values) {
        if (val.equals(wrappsPropertyValue)) {
          result.add(ure);
        }
      }
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

  public String toString() {
    return "ObjectifiedInstanceExpression(" + wrappsPropertyValue + ")";
  }

  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!ObjectifiedInstanceExpression.class.equals(obj.getClass())) {
      return false;
    }
    return ((ObjectifiedInstanceExpression) obj).getOwningType().equals(owningType)
        && ((ObjectifiedInstanceExpression) obj).getWrappedPropertyValue().equals(wrappsPropertyValue);
  }

  public int hashCode() {
    return owningType.hashCode() ^ wrappsPropertyValue.hashCode();
  }

}

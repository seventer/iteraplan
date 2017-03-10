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
package de.iteratec.iteraplan.elasticeam.model.diff;

import java.math.BigInteger;

import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


/**
 *
 */
public class PropertyValueChange extends AbstractModelElementChange {

  public static final TypeOfModelElementChange HANDLED_TYPE = TypeOfModelElementChange.UPDATE_PROPERTY_VALUE;

  private final UniversalModelExpression       affectedInstance;
  private final UniversalTypeExpression        typeExpression;
  private final PropertyExpression<?>          propertyExpression;
  private final Object                         oldValue;
  private final Object                         newValue;

  protected PropertyValueChange(Model currentModel, Model modifiedModel, Metamodel metamodel, UniversalModelExpression affectedInstance,
      UniversalTypeExpression typeExpression, PropertyExpression<?> propertyExpression, Object oldValue, Object newValue) {
    super(currentModel, modifiedModel, metamodel);
    this.affectedInstance = affectedInstance;
    this.typeExpression = typeExpression;
    this.propertyExpression = propertyExpression;
    this.oldValue = oldValue;
    this.newValue = checkLiteral(newValue);
  }

  /**{@inheritDoc}**/
  @Override
  public TypeOfModelElementChange getTypeOfModelDifference() {
    return HANDLED_TYPE;
  }

  /**{@inheritDoc}**/
  @Override
  public boolean isApplicable() {
    return (affectedInstance != null && propertyExpression != null && (newValue != null || propertyExpression.getLowerBound() < 1));
  }

  /**{@inheritDoc}**/
  @Override
  public boolean isActualChange() {
    return ((oldValue == null && newValue != null) || (oldValue != null && !oldValue.equals(newValue))) && isApplicable();
  }

  public PropertyExpression<?> getPropertyExpression() {
    return propertyExpression;
  }

  public UniversalModelExpression getModelExpression() {
    return affectedInstance;
  }

  public UniversalModelExpression getModelExpressionInCurrentModel() {
    return getCurrentModel().findById(typeExpression, (BigInteger) affectedInstance.getValue(UniversalTypeExpression.ID_PROPERTY));
  }

  public Object getOldValue() {
    return oldValue;
  }

  public Object getNewValue() {
    return newValue;
  }

  public UniversalTypeExpression getTypeExpression() {
    return typeExpression;
  }

  /**{@inheritDoc}**/
  @Override
  public boolean apply() {
    if (isApplicable() && !isApplied()) {
      getCurrentModel().setValue(getModelExpressionInCurrentModel(), propertyExpression, newValue);
      setApplied(true);
      return true;
    }
    throw new ModelException(ModelException.GENERAL_ERROR, "Cannot apply property value change " + this);
  }

  @Override
  public String toString() {
    return super.toString() + ": Value of " + affectedInstance + "." + propertyExpression + " changed from " + oldValue + " to " + newValue;
  }

  private Object checkLiteral(Object value) {
    Object result = value;
    if (value != null && propertyExpression.getType() instanceof EnumerationExpression) {
      EnumerationLiteralExpression fromOtherMetamodel = (EnumerationLiteralExpression) value;
      result = ((EnumerationExpression) propertyExpression.getType()).findLiteralByPersistentName(fromOtherMetamodel.getPersistentName());
    }
    return result;
  }

}

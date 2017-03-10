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
package de.iteratec.iteraplan.persistence.elasticeam.model.diff;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.compare.diff.metamodel.UpdateAttribute;

import com.google.common.collect.BiMap;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.diff.PropertyValueChange;


/**
 *
 */
public class EMFPropertyValueChange extends PropertyValueChange {

  private static final Logger                           LOGGER = Logger.getIteraplanLogger(EMFPropertyValueChange.class);

  private final IteraplanMapping                        mapping;
  private final BiMap<Object, UniversalModelExpression> instanceMapping;
  private final UpdateAttribute                         updateAttribute;

  protected EMFPropertyValueChange(IteraplanMapping mapping, Model model, BiMap<Object, UniversalModelExpression> instanceMapping,
      Model modifiedModel, UniversalModelExpression affectedInstance, UniversalTypeExpression typeExpression,
      PropertyExpression<?> propertyExpression, Object oldValue, Object newValue, UpdateAttribute updateAttribute) {
    super(model, modifiedModel, mapping.getMetamodel(), affectedInstance, typeExpression, propertyExpression, oldValue, newValue);
    this.mapping = mapping;
    this.instanceMapping = instanceMapping;
    //can be null, in case of initiation of backlog changes
    this.updateAttribute = updateAttribute;
  }

  public UpdateAttribute getUpdateAttribute() {
    return updateAttribute;
  }

  /**{@inheritDoc}**/
  @Override
  public boolean apply() {
    Object persistedInstance = instanceMapping.inverse().get(getModelExpressionInCurrentModel());
    if (super.apply()) {
      try {
        persist(persistedInstance);
      } catch (IllegalArgumentException e) {
        LOGGER.error(e);
        throw new ModelException(ModelException.UNKNOWN_ERROR, "could not persit changes " + this + "(" + e + ")");
      } catch (IllegalAccessException e) {
        LOGGER.error(e);
        throw new ModelException(ModelException.UNKNOWN_ERROR, "could not persit changes " + this + "(" + e + ")");
      } catch (InvocationTargetException e) {
        LOGGER.error(e);
        throw new ModelException(ModelException.UNKNOWN_ERROR, "could not persit changes " + this + "(" + e + ")");
      }

      return true;
    }
    throw new ModelException(ModelException.GENERAL_ERROR, "Cannot apply property value change " + this);
  }

  /**
   * Write changes to iteraplan (hibernate) object
   * 
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  protected void persist(Object persistedInstance) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

    PropertyExpression<?> propertyExpression = getPropertyExpression();
    HbMappedClass hbClass = HbClassHelper.getHbClass(mapping, getTypeExpression());
    Object newValue = getNewValue();

    IteraplanInstanceHelper.setPropertyValue(newValue, propertyExpression, persistedInstance, hbClass, mapping);
  }
}

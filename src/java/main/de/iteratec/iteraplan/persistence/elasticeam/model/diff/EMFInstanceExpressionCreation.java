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
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeLeftTarget;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.google.common.collect.BiMap;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.common.DefaultSpringApplicationContext;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.diff.InstanceExpressionCreation;
import de.iteratec.iteraplan.elasticeam.model.diff.LinkChange;
import de.iteratec.iteraplan.model.BuildingBlock;


/**
 *
 */
public class EMFInstanceExpressionCreation extends InstanceExpressionCreation {

  private static final Logger                           LOGGER = Logger.getIteraplanLogger(EMFInstanceExpressionCreation.class);

  private final IteraplanMapping                        mapping;
  private final BiMap<Object, UniversalModelExpression> instanceMapping;
  private final ModelElementChangeLeftTarget            newElementChange;
  private BuildingBlock                                 buildingBlock;
  private final HbMappedClass                           hbClass;

  protected EMFInstanceExpressionCreation(IteraplanMapping mapping, Model model, BiMap<Object, UniversalModelExpression> instanceMapping,
      Model modifiedModel, InstanceExpression newModelExpression, SubstantialTypeExpression newModelExpressionsType,
      ModelElementChangeLeftTarget newElementChange) {
    super(model, modifiedModel, mapping.getMetamodel(), newModelExpression, newModelExpressionsType);
    this.newElementChange = newElementChange;
    this.hbClass = HbClassHelper.getHbClass(mapping, getUniversalTypeExpression());
    this.instanceMapping = instanceMapping;
    this.mapping = mapping;
  }

  public ModelElementChangeLeftTarget getNewElementChange() {
    return newElementChange;
  }

  /**{@inheritDoc}**/
  @Override
  public boolean apply() {
    if (super.apply()) {
      persist();
      return true;
    }
    throw new ModelException(ModelException.GENERAL_ERROR, "Could not apply instance creation: " + this);
  }

  private void persist() {
    if (isApplied()) {
      buildingBlock = IteraplanInstanceHelper.createIteraplanInstance(hbClass, getSourceModelExpression());
      instanceMapping.put(buildingBlock, getNewCreatedModelExpression());
      try {
        setBasicFeatures();
        save();
        propagateIteraplanId();
        setAdditionalAttributes();
      } catch (IllegalArgumentException e) {
        LOGGER.error(e);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
      } catch (IllegalAccessException e) {
        LOGGER.error(e);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
      } catch (InvocationTargetException e) {
        LOGGER.error(e);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
      } catch (SecurityException e) {
        LOGGER.error(e);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
      }
    }
    else {
      throw new ModelException(ModelException.GENERAL_ERROR, "TODO");
    }
  }

  private void setBasicFeatures() throws IllegalAccessException, InvocationTargetException {

    if (isApplied()) {
      Map<PropertyExpression<?>, Method> properties = mapping.getBuiltInProperties().get(getUniversalTypeExpression());
      for (Entry<PropertyExpression<?>, Method> entry : properties.entrySet()) {
        PropertyExpression<?> property = entry.getKey();
        if (!UniversalTypeExpression.ID_PROPERTY.equals(property)) {
          Object newValue = getSourceModelExpression().getValue(property);

          IteraplanInstanceHelper.setPropertyValue(newValue, property, buildingBlock, hbClass, mapping);
        }
      }
    }
  }

  private void save() throws IllegalAccessException, InvocationTargetException {
    SessionFactory sessionFactory = (SessionFactory) DefaultSpringApplicationContext.getSpringApplicationContext().getBean("sessionFactory");
    Session session = sessionFactory.getCurrentSession();
    if (hbClass.isReleaseClass()) {
      Object base = hbClass.getReleaseBaseProperty().getGetMethod().invoke(buildingBlock);
      session.save(base);
    }
    session.save(buildingBlock);
  }

  private void propagateIteraplanId() {
    BigInteger id = BigInteger.valueOf(buildingBlock.getId().longValue());
    getNewCreatedModelExpression().setValue(UniversalTypeExpression.ID_PROPERTY, id);
    getSourceModelExpression().setValue(UniversalTypeExpression.ID_PROPERTY, id);
  }

  private void setAdditionalAttributes() throws IllegalAccessException, InvocationTargetException {
    if (isApplied()) {

      for (PropertyExpression<?> property : getUniversalTypeExpression().getProperties()) {
        if (mapping.getAdditionalPropertyExpressions().containsKey(property)) {
          Object value = getSourceModelExpression().getValue(property);

          IteraplanInstanceHelper.setPropertyValue(value, property, buildingBlock, hbClass, mapping);
        }
      }
    }
  }

  @Override
  protected LinkChange createLinkChange(RelationshipEndExpression relationshipEnd, UniversalModelExpression newTarget) {
    return new EMFLinkChange(mapping, getCurrentModel(), instanceMapping, getModifiedModel(), getNewCreatedModelExpression(), relationshipEnd, null,
        newTarget, null);
  }

}

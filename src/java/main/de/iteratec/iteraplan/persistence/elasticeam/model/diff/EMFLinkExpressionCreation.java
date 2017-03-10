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
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedProperty;
import de.iteratec.iteraplan.elasticeam.model.LinkExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.diff.LinkExpressionCreation;
import de.iteratec.iteraplan.model.BuildingBlock;


/**
 *
 */
public class EMFLinkExpressionCreation extends LinkExpressionCreation {

  private static final Logger                           LOGGER = Logger.getIteraplanLogger(EMFLinkExpressionCreation.class);

  private final IteraplanMapping                        mapping;
  private final BiMap<Object, UniversalModelExpression> instanceMapping;

  private BuildingBlock                                 buildingBlock;
  private final ModelElementChangeLeftTarget            newElementChange;
  private final HbMappedClass                           hbClass;

  protected EMFLinkExpressionCreation(IteraplanMapping mapping, Model model, BiMap<Object, UniversalModelExpression> instanceMapping,
      Model modifiedModel, LinkExpression newModelExpression, RelationshipTypeExpression newModelExpressionsType,
      ModelElementChangeLeftTarget newElementChange) {
    super(model, modifiedModel, mapping.getMetamodel(), newModelExpression, newModelExpressionsType);
    this.instanceMapping = instanceMapping;
    this.newElementChange = newElementChange;
    this.hbClass = HbClassHelper.getHbClass(mapping, getUniversalTypeExpression());
    this.mapping = mapping;
  }

  public ModelElementChangeLeftTarget getNewElementChange() {
    return newElementChange;
  }

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
        setRelations();
        save();
        propagateIteraplanId();
        setAdditionalAttributes();
      } catch (IllegalAccessException e) {
        LOGGER.error(e);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
      } catch (InvocationTargetException e) {
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

  private void setRelations() throws IllegalAccessException, InvocationTargetException {
    for (RelationshipEndExpression relationshipEnd : getUniversalTypeExpression().getRelationshipEnds()) {
      UniversalModelExpression newTargetExpression = getNewCreatedModelExpression().getConnected(relationshipEnd);
      Object newTarget = instanceMapping.inverse().get(newTargetExpression);

      HbMappedProperty property = hbClass.getProperty(relationshipEnd.getPersistentName());

      // Assumes Relationship objects only have 1:1 relations to other objects
      property.getSetMethod().invoke(buildingBlock, newTarget);
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
}

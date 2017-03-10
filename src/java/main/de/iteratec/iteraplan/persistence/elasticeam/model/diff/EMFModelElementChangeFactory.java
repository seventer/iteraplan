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

import java.math.BigInteger;

import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeLeftTarget;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget;
import org.eclipse.emf.compare.diff.metamodel.ReferenceChange;
import org.eclipse.emf.compare.diff.metamodel.ReferenceOrderChange;
import org.eclipse.emf.compare.diff.metamodel.UpdateAttribute;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import com.google.common.collect.BiMap;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.LinkExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.diff.AbstractModelElementChange;


/**
 *
 */
public final class EMFModelElementChangeFactory {

  private static final Logger               LOGGER   = Logger.getIteraplanLogger(EMFModelElementChangeFactory.class);

  static final EMFModelElementChangeFactory INSTANCE = new EMFModelElementChangeFactory();

  private EMFModelElementChangeFactory() {
    //hide constructor
  }

  public AbstractModelElementChange createModelElementChange(DiffElement diff, IteraplanMapping mapping, Model model,
                                                             BiMap<Object, UniversalModelExpression> instanceMapping, Model modifiedModel) {
    if (diff instanceof ReferenceOrderChange) {
      return createTechnicalEMFModelElementChange(diff, mapping, model, modifiedModel);
    }
    if (diff instanceof ModelElementChangeLeftTarget) {
      return createEMFModelExpressionCreation(diff, mapping, instanceMapping, model, modifiedModel);
    }
    if (diff instanceof UpdateAttribute) {
      return createEMFPropertyValueChange(diff, mapping, instanceMapping, model, modifiedModel);
    }
    if (diff instanceof ReferenceChange) {
      return createEMFLinkChange(diff, mapping, instanceMapping, model, modifiedModel);
    }
    if (diff instanceof ModelElementChangeRightTarget) {
      return createEMFInstanceDeletion(diff, mapping.getMetamodel(), model, modifiedModel);
    }

    throw new ModelException(ModelException.GENERAL_ERROR, "Could not create " + AbstractModelElementChange.class.getSimpleName() + " instance for "
        + diff);
  }

  private TechnicalEMFModelElementChange createTechnicalEMFModelElementChange(DiffElement diff, IteraplanMapping mapping, Model model,
                                                                              Model modifiedModel) {
    LOGGER.info("Creating TechnicalEMFModelElementChange...");
    TechnicalEMFModelElementChange change = new TechnicalEMFModelElementChange(mapping, model, modifiedModel, (ReferenceOrderChange) diff);
    LOGGER.info("\"{0}\" created.", change);
    return change;
  }

  private AbstractModelElementChange createEMFModelExpressionCreation(DiffElement diff, IteraplanMapping mapping,
                                                                      BiMap<Object, UniversalModelExpression> instanceMapping, Model model,
                                                                      Model modifiedModel) {
    LOGGER.info("Creating EMFModelExpressionCreation...");
    ModelElementChangeLeftTarget instanceCreation = (ModelElementChangeLeftTarget) diff;
    EObject newEInstance = instanceCreation.getLeftElement();
    EClass eClass = newEInstance.eClass();
    UniversalTypeExpression type = (UniversalTypeExpression) mapping.getMetamodel().findTypeByPersistentName(eClass.getName());
    UniversalModelExpression newModelExpression = findModelExpression(modifiedModel, newEInstance, type);

    AbstractModelElementChange change = null;
    if (type instanceof SubstantialTypeExpression) {
      InstanceExpression instanceExpression = (InstanceExpression) newModelExpression;
      SubstantialTypeExpression substantialType = (SubstantialTypeExpression) type;
      change = new EMFInstanceExpressionCreation(mapping, model, instanceMapping, modifiedModel, instanceExpression, substantialType,
          instanceCreation);
    }
    else {
      LinkExpression linkExpression = (LinkExpression) newModelExpression;
      RelationshipTypeExpression relationshipType = (RelationshipTypeExpression) type;
      change = new EMFLinkExpressionCreation(mapping, model, instanceMapping, modifiedModel, linkExpression, relationshipType, instanceCreation);
    }

    LOGGER.info("\"{0}\" created.", change);
    return change;
  }

  private AbstractModelElementChange createEMFPropertyValueChange(DiffElement diff, IteraplanMapping mapping,
                                                                  BiMap<Object, UniversalModelExpression> instanceMapping, Model model,
                                                                  Model modifiedModel) {
    LOGGER.info("Creating EMFPropertyValueChange...");
    UpdateAttribute updateAttribute = (UpdateAttribute) diff;
    EAttribute eAttribute = updateAttribute.getAttribute();
    EObject affectedEInstance = updateAttribute.getLeftElement();
    EClass eClass = affectedEInstance.eClass();
    UniversalTypeExpression typeExpression = (UniversalTypeExpression) mapping.getMetamodel().findTypeByPersistentName(eClass.getName());
    UniversalModelExpression affectedModelExpression = findModelExpression(model, affectedEInstance, typeExpression);
    UniversalModelExpression updatedModelExpression = findModelExpression(modifiedModel, affectedEInstance, typeExpression);
    PropertyExpression<?> property = typeExpression.findPropertyByPersistentName(eAttribute.getName());
    Object oldValue = model.getValue(affectedModelExpression, property);
    Object newValue = modifiedModel.getValue(updatedModelExpression, property);

    EMFPropertyValueChange change = new EMFPropertyValueChange(mapping, model, instanceMapping, modifiedModel, affectedModelExpression,
        typeExpression, property, oldValue, newValue, updateAttribute);
    LOGGER.info("\"{0}\" created.", change);
    return change;
  }

  private AbstractModelElementChange createEMFLinkChange(DiffElement diff, IteraplanMapping mapping,
                                                         BiMap<Object, UniversalModelExpression> instanceMapping, Model model, Model modifiedModel) {
    LOGGER.info("Creating EMFLinkChange...");
    ReferenceChange referenceChange = (ReferenceChange) diff;
    EReference reference = referenceChange.getReference();
    EObject sourceEInstance = referenceChange.getLeftElement();
    EClass sourceEClass = sourceEInstance.eClass();
    UniversalTypeExpression type = (UniversalTypeExpression) mapping.getMetamodel().findTypeByPersistentName(sourceEClass.getName());
    UniversalModelExpression source = findModelExpression(model, sourceEInstance, type);
    UniversalModelExpression sourceSibling = findModelExpression(modifiedModel, referenceChange.getRightElement(), type);
    RelationshipEndExpression relationshipEnd = type.findRelationshipEndByPersistentName(reference.getName());
    UniversalModelExpression oldTarget = source.getConnected(relationshipEnd);
    UniversalModelExpression newTarget = sourceSibling.getConnected(relationshipEnd);

    EMFLinkChange change = new EMFLinkChange(mapping, model, instanceMapping, modifiedModel, source, relationshipEnd, oldTarget, newTarget,
        referenceChange);
    LOGGER.info("\"{0}\" created.", change);
    return change;
  }

  private AbstractModelElementChange createEMFInstanceDeletion(DiffElement diff, Metamodel metamodel, Model model, Model modifiedModel) {
    LOGGER.info("Creating EMFInstanceDeletion...");
    ModelElementChangeRightTarget instanceDeletion = (ModelElementChangeRightTarget) diff;
    EObject eInstanceToDelete = instanceDeletion.getRightElement();
    EClass eClass = eInstanceToDelete.eClass();
    UniversalTypeExpression type = (UniversalTypeExpression) metamodel.findTypeByPersistentName(eClass.getName());
    UniversalModelExpression instanceToDelete = findModelExpression(model, eInstanceToDelete, type);

    EMFInstanceDeletion change = new EMFInstanceDeletion(metamodel, model, modifiedModel, instanceToDelete, type, instanceDeletion);
    LOGGER.info("\"{0}\" created.", change);
    return change;
  }

  private static UniversalModelExpression findModelExpression(Model model, EObject eObject, UniversalTypeExpression type) {
    EAttribute idAttribute = (EAttribute) eObject.eClass().getEStructuralFeature(UniversalTypeExpression.ID_PROPERTY.getPersistentName());
    BigInteger id = (BigInteger) eObject.eGet(idAttribute);
    UniversalModelExpression modelExpression = model.findById(type, id);
    if (modelExpression == null) {
      LOGGER.warn("Could not find corresponding model expression for " + eObject);
    }
    return modelExpression;
  }
}

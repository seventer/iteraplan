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


/**
 *
 */
public final class ModelElementChangeFactory {

  public static final ModelElementChangeFactory INSTANCE = new ModelElementChangeFactory();

  private ModelElementChangeFactory() {
    //hide constructor
  }

  public AbstractModelElementChange createModelElementChange(DiffElement diff, Model currentModel, Model modifiedModel, Metamodel metamodel) {
    if (diff instanceof ReferenceOrderChange) {
      return new TechnicalDiff(currentModel, modifiedModel, metamodel, null, null);
    }
    if (diff instanceof ModelElementChangeLeftTarget) {
      return createModelExpressionCreation(diff, currentModel, modifiedModel, metamodel);
    }
    if (diff instanceof UpdateAttribute) {
      return createPropertyValueChange(diff, currentModel, modifiedModel, metamodel);
    }
    if (diff instanceof ReferenceChange) {
      return createLinkChange(diff, currentModel, modifiedModel, metamodel);
    }
    if (diff instanceof ModelElementChangeRightTarget) {
      return createModelElementDeletion(diff, currentModel, modifiedModel, metamodel);
    }
    throw new ModelException(ModelException.GENERAL_ERROR, "Could not create " + AbstractModelElementChange.class.getSimpleName() + " instance for "
        + diff);
  }

  private AbstractModelElementChange createModelExpressionCreation(DiffElement diff, Model currentModel, Model modifiedModel, Metamodel metamodel) {
    ModelElementChangeLeftTarget instanceCreation = (ModelElementChangeLeftTarget) diff;
    EObject newEInstance = instanceCreation.getLeftElement();
    EClass eClass = newEInstance.eClass();
    UniversalTypeExpression type = (UniversalTypeExpression) metamodel.findTypeByName(eClass.getName());
    UniversalModelExpression newModelExpression = findModelExpression(modifiedModel, newEInstance, type);

    if (type instanceof SubstantialTypeExpression) {
      InstanceExpression instanceExpression = (InstanceExpression) newModelExpression;
      SubstantialTypeExpression substantialType = (SubstantialTypeExpression) type;
      return new InstanceExpressionCreation(currentModel, modifiedModel, metamodel, instanceExpression, substantialType);
    }
    else {
      LinkExpression linkExpression = (LinkExpression) newModelExpression;
      RelationshipTypeExpression relationshipType = (RelationshipTypeExpression) type;
      return new LinkExpressionCreation(currentModel, modifiedModel, metamodel, linkExpression, relationshipType);
    }
  }

  private AbstractModelElementChange createPropertyValueChange(DiffElement diff, Model currentModel, Model modifiedModel, Metamodel metamodel) {
    UpdateAttribute updateAttribute = (UpdateAttribute) diff;
    EAttribute eAttribute = updateAttribute.getAttribute();
    EObject affectedEInstance = updateAttribute.getLeftElement();
    EClass eClass = affectedEInstance.eClass();
    UniversalTypeExpression typeExpression = (UniversalTypeExpression) metamodel.findTypeByName(eClass.getName());
    UniversalModelExpression affectedModelExpression = findModelExpression(currentModel, affectedEInstance, typeExpression);
    UniversalModelExpression updatedModelExpression = findModelExpression(modifiedModel, affectedEInstance, typeExpression);
    PropertyExpression<?> property = typeExpression.findPropertyByName(eAttribute.getName());
    Object oldValue = currentModel.getValue(affectedModelExpression, property);
    Object newValue = modifiedModel.getValue(updatedModelExpression, property);
    return new PropertyValueChange(currentModel, modifiedModel, metamodel, affectedModelExpression, typeExpression, property, oldValue, newValue);
  }

  private AbstractModelElementChange createLinkChange(DiffElement diff, Model currentModel, Model modifiedModel, Metamodel metamodel) {
    ReferenceChange referenceChange = (ReferenceChange) diff;
    EReference reference = referenceChange.getReference();
    EObject sourceEInstance = referenceChange.getLeftElement();
    EClass sourceEClass = sourceEInstance.eClass();
    UniversalTypeExpression type = (UniversalTypeExpression) metamodel.findTypeByName(sourceEClass.getName());
    UniversalModelExpression source = findModelExpression(currentModel, sourceEInstance, type);
    UniversalModelExpression sourceSibling = findModelExpression(modifiedModel, referenceChange.getRightElement(), type);
    RelationshipEndExpression relationshipEnd = type.findRelationshipEndByName(reference.getName());
    UniversalModelExpression oldTarget = source.getConnected(relationshipEnd);
    UniversalModelExpression newTarget = sourceSibling.getConnected(relationshipEnd);
    return new LinkChange(currentModel, modifiedModel, metamodel, source, relationshipEnd, oldTarget, newTarget);
  }

  private AbstractModelElementChange createModelElementDeletion(DiffElement diff, Model currentModel, Model modifiedModel, Metamodel metamodel) {
    ModelElementChangeRightTarget instanceDeletion = (ModelElementChangeRightTarget) diff;
    EObject eInstanceToDelete = instanceDeletion.getRightElement();
    EClass eClass = eInstanceToDelete.eClass();
    UniversalTypeExpression type = (UniversalTypeExpression) metamodel.findTypeByName(eClass.getName());
    UniversalModelExpression instanceToDelete = findModelExpression(currentModel, eInstanceToDelete, type);
    return new ModelElementDeletion(currentModel, modifiedModel, metamodel, instanceToDelete, type);
  }

  private static UniversalModelExpression findModelExpression(Model model, EObject eObject, UniversalTypeExpression type) {
    EAttribute idAttribute = (EAttribute) eObject.eClass().getEStructuralFeature(UniversalTypeExpression.ID_PROPERTY.getName());
    BigInteger id = (BigInteger) eObject.eGet(idAttribute);
    return model.findById(type, id);
  }
}

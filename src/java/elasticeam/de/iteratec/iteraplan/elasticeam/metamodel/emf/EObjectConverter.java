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
package de.iteratec.iteraplan.elasticeam.metamodel.emf;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.emfimpl.EMFMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.TypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.LinkExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


public final class EObjectConverter {

  private EObjectConverter() {
    //Nothing here
  }

  public static Collection<EObject> export(Metamodel metamodel, Model model, boolean useLocalNames) {
    return new ExportRun(metamodel, model, useLocalNames).getEObjects();
  }

  public static List<EObject> export(Mapping<Metamodel> mapping, Model model) {
    return new ExportRun(mapping, model).getEObjects();
  }

  public static void importData(Mapping<EMFMetamodel> mapping, Model model, Collection<EObject> data) {
    new ImportRun(mapping, model, data);
  }

  private static class ImportRun {

    private static final Logger                      LOGGER = Logger.getIteraplanLogger(ImportRun.class);

    private Mapping<EMFMetamodel>                    mapping;
    private BiMap<EObject, UniversalModelExpression> expressions;
    private Model                                    model;

    ImportRun(Mapping<EMFMetamodel> mapping, Model model, Collection<EObject> data) {
      this.mapping = mapping;
      this.expressions = HashBiMap.create();
      this.model = model;
      Collection<EObject> links = Lists.newLinkedList();
      for (EObject eObject : data) {
        if (!createInstanceExpression(eObject)) {
          links.add(eObject);
        }
      }
      for (EObject eObject : links) {
        if (!createRelatorExpression(eObject)) {
          LOGGER.error("Could not instantiate RelatorExpression for EObject " + eObject);
        }
      }
      for (Entry<EObject, UniversalModelExpression> entry : expressions.entrySet()) {
        linkExpressions(entry.getKey(), entry.getValue());
      }
    }

    /**
     * Actually linking the {@link ObjectExpression}s by setting all relationships
     * 
     * @param eObject
     *      the {@link EObject} holding all information about connected instances
     * @param expression
     *      the {@link ObjectExpression} being linked
     */
    private void linkExpressions(EObject eObject, UniversalModelExpression expression) {
      EClass eClass = eObject.eClass();
      UniversalTypeExpression typeExpression = this.mapping.getUniversalType(eClass);

      for (RelationshipEndExpression relEnd : typeExpression.getRelationshipEnds()) {
        EReference eReference = this.mapping.getEReference(typeExpression, relEnd);
        Object value = eObject.eGet(eReference);
        if (value instanceof Collection) {
          @SuppressWarnings("unchecked")
          Collection<EObject> connectedEObjects = (Collection<EObject>) value;
          for (EObject connectedEObject : connectedEObjects) {
            UniversalModelExpression connectedModelExpression = expressions.get(connectedEObject);
            model.link(expression, relEnd, connectedModelExpression);
          }
        }
        else if (value != null) {
          EObject connectedEObject = (EObject) value;
          UniversalModelExpression connectedModelExpression = expressions.get(connectedEObject);
          model.link(expression, relEnd, connectedModelExpression);
        }
      }
    }

    /**
     * Creates {@link IndividualExpression}s for a given {@link EObject}
     * 
     * @param eObject
     *      the {@link EObject} for which an {@link IndividualExpression} will be instantiated
     * @return
     *      true, if the {@link EObject} is representing an {@link IndividualExpression}
     */
    private boolean createInstanceExpression(EObject eObject) {
      SubstantialTypeExpression substantialType = this.mapping.getSubstantialType(eObject.eClass());
      if (substantialType != null) {
        InstanceExpression result = model.create(substantialType);
        setProperties(eObject, result, substantialType);
        this.expressions.put(eObject, result);
        return true;
      }
      return false;
    }

    /**
     * Creates {@link RelatorExpression}s for a given {@link EObject}
     * 
     * @param eObject
     *      the {@link EObject} for which a {@link RelatorExpression} will be instantiated
     * @return true, if a {@link RelatorExpression} was created, false otherwise
     */
    private boolean createRelatorExpression(EObject eObject) {
      RelationshipTypeExpression relationshipType = this.mapping.getRelationshipType(eObject.eClass());
      if (relationshipType != null) {
        LinkExpression result = model.create(relationshipType);
        setProperties(eObject, result, relationshipType);
        this.expressions.put(eObject, result);
        return true;
      }
      return false;
    }

    @SuppressWarnings("unchecked")
    private final void setProperties(EObject eObject, UniversalModelExpression expression, UniversalTypeExpression typeExpression) {

      for (PropertyExpression<?> property : typeExpression.getProperties()) {
        EAttribute eAttribute = (EAttribute) eObject.eClass().getEStructuralFeature(property.getName());
        if (eAttribute.getEType() instanceof EEnum) {
          Object value = eObject.eGet(eAttribute);
          if (value instanceof Collection) {
            Collection<EnumerationLiteralExpression> literals = Lists.newLinkedList();
            for (EEnumLiteral literal : (Collection<EEnumLiteral>) value) {
              literals.add(this.mapping.getLiteral((EEnum) eAttribute.getEType(), literal));
            }
            model.setValue(expression, property, literals);
          }
          else {
            model.setValue(expression, property, this.mapping.getLiteral((EEnum) eAttribute.getEType(), (EEnumLiteral) value));
          }
        }
        else {
          model.setValue(expression, property, eObject.eGet(eAttribute));
        }
      }
    }
  }

  private static class ExportRun {
    private Mapping<Metamodel>                       mapping;
    private BiMap<UniversalModelExpression, EObject> eObjects = HashBiMap.create();

    ExportRun(Mapping<Metamodel> mapping, Model model) {
      this.mapping = mapping;
      run(mapping.getMetamodel(), model);
    }

    ExportRun(Metamodel metamodel, Model model, boolean useLocalNames) {
      this.mapping = EPackageConverter.deriveMapping(metamodel, useLocalNames);
      run(metamodel, model);
    }

    private void run(Metamodel metamodel, Model model) {
      for (TypeExpression type : metamodel.getTypes()) {
        if (type instanceof SubstantialTypeExpression) {
          SubstantialTypeExpression substantialType = (SubstantialTypeExpression) type;
          for (InstanceExpression instance : model.findAll(substantialType)) {
            convert(substantialType, instance);
          }
        }
        if (type instanceof RelationshipTypeExpression) {
          RelationshipTypeExpression relationshipType = (RelationshipTypeExpression) type;
          for (LinkExpression link : model.findAll(relationshipType)) {
            convert(relationshipType, link);
          }
        }
      }

      for (RelationshipExpression relationship : metamodel.getRelationships()) {
        RelationshipEndExpression end0 = relationship.getRelationshipEnds().get(0);
        BindingSet connections = model.findAll(end0);
        for (UniversalModelExpression left : connections.getAllFromElements()) {
          for (UniversalModelExpression right : connections.getToBindings(left)) {
            connect(this.eObjects.get(left), this.mapping.getEReference(end0.getHolder(), end0), this.eObjects.get(right));
          }
        }
      }
    }

    @SuppressWarnings("unchecked")
    private static void connect(EObject leftEObject, EReference left2right, EObject rightEObject) {
      if (leftEObject == null || rightEObject == null) {
        return;
      }
      if (left2right.isMany()) {
        List<EObject> values = Lists.newArrayList();
        Object value = leftEObject.eGet(left2right);
        if (value instanceof Collection) {
          values.addAll((Collection<EObject>) value);
        }
        values.add(rightEObject);
        leftEObject.eSet(left2right, values);
      }
      else {
        leftEObject.eSet(left2right, rightEObject);
      }
    }

    private void convert(UniversalTypeExpression universalType, UniversalModelExpression instance) {
      EObject eObject = EcoreUtil.create(this.mapping.getEClass(universalType));
      this.eObjects.put(instance, eObject);
      for (PropertyExpression<?> property : universalType.getProperties()) {
        EAttribute eAttribute = this.mapping.getEAttribute(universalType, property);
        if (eAttribute.isMany()) {
          set(eObject, eAttribute, instance.getValues(property));
        }
        else {
          set(eObject, eAttribute, instance.getValue(property));
        }
      }
    }

    private void set(EObject eObject, EAttribute eAttribute, Object value) {
      if (value == null || eAttribute == null) {
        return;
      }
      if (eAttribute.getEAttributeType() instanceof EEnum) {
        EEnum eEnum = (EEnum) eAttribute.getEAttributeType();
        if (eAttribute.isMany()) {
          Collection<EEnumLiteral> literals = Lists.newLinkedList();
          for (Object val : (Collection<?>) value) {
            literals.add(getELiteral(this.mapping.getEnumeration(eEnum), (EnumerationLiteralExpression) val));
          }
          eObject.eSet(eAttribute, literals);
        }
        else {
          eObject.eSet(eAttribute, getELiteral(this.mapping.getEnumeration(eEnum), (EnumerationLiteralExpression) value));
        }
      }
      else {
        eObject.eSet(eAttribute, value);
      }
    }

    private EEnumLiteral getELiteral(EnumerationExpression enumeration, EnumerationLiteralExpression ele) {
      return this.mapping.getEEnum(enumeration).getEEnumLiteral(ele.getPersistentName());
    }

    final List<EObject> getEObjects() {
      return Lists.newArrayList(this.eObjects.values());
    }
  }
}
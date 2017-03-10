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
package de.iteratec.iteraplan.elasticeam.emfimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.derived.AbstractInstanceStore;
import de.iteratec.iteraplan.elasticeam.derived.QueryableModel;
import de.iteratec.iteraplan.elasticeam.emfimpl.EMFInstanceStore.EMFModelInstance;
import de.iteratec.iteraplan.elasticeam.emfimpl.EMFInstanceStore.EMFModelLink;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveProperty;
import de.iteratec.iteraplan.elasticeam.model.AModelElement;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.LinkExpression;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


/**
 *
 */
@SuppressWarnings("PMD.TooManyMethods")
public class EMFInstanceStore extends
    AbstractInstanceStore<EMFModelInstance, EMFSubstantialType, EMFModelLink, EMFRelationshipType, EMFProperty<?>, EMFRelationshipEnd> {

  private Resource                        resource;
  private BiMap<EObject, EMFModelElement> elements;

  public EMFInstanceStore(Metamodel metamodel) {
    super(metamodel);
    this.elements = HashBiMap.create();
    this.resource = new XMLResourceImpl();
    this.resource.setTrackingModification(false);
  }

  protected class EMFModelElement extends AModelElement {

    private EObject        wrapped;
    private QueryableModel queryableModel;

    /**
     * Default constructor.
     */
    EMFModelElement(EObject wrapped) {
      this.wrapped = wrapped;
    }

    /**{@inheritDoc}**/
    @Override
    protected final QueryableModel getQueryableModel() {
      if (queryableModel == null) {
        this.queryableModel = new QueryableModel(EMFInstanceStore.this);
      }
      return queryableModel;
    }

    /**{@inheritDoc}**/
    @Override
    protected final void setQueryableModel(QueryableModel queryableModel) {
      this.queryableModel = queryableModel;
    }

    protected final EObject getWrapped() {
      return this.wrapped;
    }

    @Override
    public String toString() {
      if (getWrapped() == null) {
        return "null";
      }
      if (getWrapped().eClass().getEIDAttribute() == null) {
        return ((Object) this).toString();
      }
      return this.getWrapped().eClass().getName() + " " + getWrapped().eGet(getWrapped().eClass().getEIDAttribute());
    }
  }

  public final class EMFModelInstance extends EMFModelElement implements InstanceExpression {
    EMFModelInstance(EObject wrapped) {
      super(wrapped);
    }
  }

  public final class EMFModelLink extends EMFModelElement implements LinkExpression {
    EMFModelLink(EObject wrapped) {
      super(wrapped);
    }
  }

  /**{@inheritDoc}**/
  @SuppressWarnings("unchecked")
  @Override
  protected final void deleteInstance(EMFModelInstance instance) {
    if (canDelete(instance)) {
      List<EMFModelLink> toDelete = new ArrayList<EMFModelLink>();

      EObject content = instance.getWrapped();
      for (EReference eReference : content.eClass().getEReferences()) {
        Object value = content.eGet(eReference);
        if (value instanceof Collection) {
          List<EObject> vals = Lists.newLinkedList((Collection<EObject>) value);
          for (EObject val : vals) {
            if (EMFMetamodel.isRelationshipType(val.eClass())) {
              Object fromOpposite = val.eGet(eReference.getEOpposite());
              if ((fromOpposite instanceof Collection)) {
                List<EObject> oppositeVals = Lists.newLinkedList((Collection<EObject>) fromOpposite);
                if (eReference.getEOpposite().getLowerBound() > (oppositeVals.size() - 1)) {
                  toDelete.add((EMFModelLink) encapsulate(val));
                }
              }
              else if (fromOpposite != null) {
                toDelete.add((EMFModelLink) encapsulate(val));
              }
            }
          }
        }
        else if (value != null && EMFMetamodel.isRelationshipType(((EObject) value).eClass()) && eReference.getEOpposite().getLowerBound() > 0) {
          toDelete.add((EMFModelLink) encapsulate((EObject) value));
        }
      }

      for (EMFModelLink link : toDelete) {
        deleteLink(link);
      }

      this.resource.getContents().remove(instance.getWrapped());
      this.elements.remove(instance.getWrapped());
    }
    else {
      throw new ModelException(ModelException.ILLEGAL_ACCESS, "Insufficient rights to delete: " + instance);
    }
  }

  /**{@inheritDoc}**/
  @Override
  protected final InstanceExpression createInstance(EMFSubstantialType type) {
    if (canCreate(type)) {
      EObject eObject = EcoreUtil.create(type.getWrapped());
      this.resource.getContents().add(eObject);
      return (InstanceExpression) encapsulate(eObject);
    }
    throw new ModelException(ModelException.ILLEGAL_ACCESS, "Insufficient rights for creating instances of " + type);
  }

  /**{@inheritDoc}**/
  @Override
  protected final Collection<InstanceExpression> findAllInstances(ElasticeamContext ctx, EMFSubstantialType type) {
    List<InstanceExpression> result = Lists.newLinkedList();
    int index = this.getMetamodel().getSubstantialTypes().indexOf(type);
    if (index >= 0) {
      EMFSubstantialType sti = (EMFSubstantialType) this.getMetamodel().getSubstantialTypes().get(index);
      Collection<EObject> instances = EcoreUtil.getObjectsByType(this.resource.getContents(), sti.getWrapped());
      for (EObject instance : instances) {
        result.add((InstanceExpression) encapsulate(instance));
      }
    }
    return result;
  }

  /**{@inheritDoc}**/
  @Override
  protected final LinkExpression createLink(EMFRelationshipType type) {
    if (canCreate(type)) {
      EObject eObject = EcoreUtil.create(type.getWrapped());
      this.resource.getContents().add(eObject);
      return (LinkExpression) encapsulate(eObject);
    }
    throw new ModelException(ModelException.ILLEGAL_ACCESS, "Insufficient rights for creating instances of " + type);
  }

  /**{@inheritDoc}**/
  @Override
  protected final void deleteLink(EMFModelLink link) {
    if (canDelete(link)) {
      EObject via = link.getWrapped();
      for (EReference eReference : via.eClass().getEReferences()) {
        via.eSet(eReference, null);
      }

      this.resource.getContents().remove(link.getWrapped());
      this.elements.remove(link.getWrapped());
    }
    else {
      throw new ModelException(ModelException.ILLEGAL_ACCESS, "Insufficient rights for deleting " + link);
    }
  }

  /**{@inheritDoc}**/
  @Override
  protected final Collection<LinkExpression> findAllLinks(EMFRelationshipType type) {
    List<LinkExpression> result = Lists.newLinkedList();
    Collection<EObject> instances = EcoreUtil.getObjectsByType(this.resource.getContents(), type.getWrapped());
    for (EObject instance : instances) {
      result.add((LinkExpression) encapsulate(instance));
    }
    return result;
  }

  /**{@inheritDoc}**/
  @SuppressWarnings("unchecked")
  protected final void link(ElasticeamContext ctx, UniversalModelExpression from, EMFRelationshipEnd via, UniversalModelExpression to) {
    if (canEdit(from, to, via.getRelationship(ctx))) {
      EObject fromObject = unwrap(from);
      EObject toObject = unwrap(to);
      Object value = fromObject.eGet(via.getWrapped());
      if (value instanceof Collection) {
        List<EObject> newValues = Lists.newLinkedList((Collection<EObject>) value);
        newValues.add(toObject);
        fromObject.eSet(via.getWrapped(), newValues);
      }
      else {
        fromObject.eSet(via.getWrapped(), toObject);
      }
    }
    else {
      throw new ModelException(ModelException.ILLEGAL_ACCESS, "Insufficient rights for linking " + from + " and " + to + " via " + via);
    }
  }

  @Override
  protected final void link(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to) {
    if (EMFRelationshipEnd.class.isInstance(via)) {
      link(getContext(), from, (EMFRelationshipEnd) via, to);
    }
    else {
      super.link(from, via, to);
    }
  }

  @SuppressWarnings("rawtypes")
  protected final BindingSet findAll(ElasticeamContext ctx, EMFRelationshipEnd via) {
    BindingSet result = new BindingSet();
    result.setFromType(via.getSource());
    result.setToType(via.getType());

    Collection startEntities = null;
    if (SubstantialTypeExpression.class.isInstance(result.getFromType())) {
      startEntities = findAllInstances(ctx, (EMFSubstantialType) result.getFromType());
    }
    else {
      startEntities = findAllLinks((EMFRelationshipType) result.getFromType());
    }

    for (Object obj : startEntities) {
      Object val = getValue(ctx, (UniversalModelExpression) obj, via);
      if (Collection.class.isInstance(val)) {
        for (Object dest : (Collection) val) {
          result.addBinding((UniversalModelExpression) obj, (UniversalModelExpression) dest);
        }
      }
      else if (val != null) {
        result.addBinding((UniversalModelExpression) obj, (UniversalModelExpression) val);
      }
    }

    return result;
  }

  protected final Object getValue(ElasticeamContext ctx, UniversalModelExpression universal, EMFRelationshipEnd relationshipEnd) {
    EObject fromObject = unwrap(universal);
    return encapsulate(fromObject.eGet(((EMFMetamodel) getMetamodel()).unwrap(relationshipEnd, fromObject.eClass())), relationshipEnd);
  }

  protected final Object getValue(ElasticeamContext ctx, UniversalModelExpression expression, EMFProperty<?> property) {
    EObject fromObject = unwrap(expression);
    return encapsulate(fromObject.eGet(((EMFMetamodel) getMetamodel()).unwrap(property, fromObject.eClass())), property);
  }

  protected final Object getValue(UniversalModelExpression expression, BuiltinPrimitiveProperty property) {
    EObject fromObject = unwrap(expression);
    EMFProperty<?> propertyInstance = mapToPropertyInstance(fromObject, property);
    return encapsulate(fromObject.eGet(propertyInstance.getWrapped()), propertyInstance);
  }

  protected final void setValue(ElasticeamContext ctx, UniversalModelExpression expression, EMFProperty<?> property, Object value) {
    if (canEdit(expression, property)) {
      EObject fromObject = unwrap(expression);
      fromObject.eSet(property.getWrapped(), unwrap(value, property));
    }
    else {
      throw new ModelException(ModelException.ILLEGAL_ACCESS, "Insufficient rights for setting values for " + expression + "." + property);
    }

  }

  protected final void setValue(ElasticeamContext ctx, UniversalModelExpression expression, BuiltinPrimitiveProperty property, Object value) {
    if (canEdit(expression, property)) {
      EObject fromObject = unwrap(expression);
      EMFProperty<?> propertyInstance = mapToPropertyInstance(fromObject, property);
      fromObject.eSet(propertyInstance.getWrapped(), unwrap(value, propertyInstance));
    }
    else {
      throw new ModelException(ModelException.ILLEGAL_ACCESS, "Insufficient rights for setting values for " + expression + "." + property);
    }

  }

  private EMFProperty<?> mapToPropertyInstance(EObject fromObject, BuiltinPrimitiveProperty builtinProperty) {
    EClass stClass = fromObject.eClass();
    UniversalTypeExpression uType = ((EMFMetamodel) getMetamodel()).encapsulate(stClass);
    EMFProperty<?> result = (EMFProperty<?>) uType.findPropertyByPersistentName(builtinProperty.getPersistentName());
    if (result == null) {
      throw new ModelException(ModelException.GENERAL_ERROR, "The builtin property " + builtinProperty + " is not permitted for the type with name "
          + fromObject.eClass().getName());
    }
    return result;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Object encapsulate(Object value, EMFFeature feature) {
    if (value == null
        || (feature instanceof EMFEnumerationProperty && value instanceof EEnumLiteral && EMFEnumerationLiteral.NOT_SPECIFIED
            .equals(((EEnumLiteral) value).getLiteral()))) {
      return null;
    }
    Object resultCandidate = value instanceof Collection ? encapsulateAll((Collection<Object>) value, feature) : encapsulateSingle(value, feature);
    if (feature.getUpperBound() == FeatureExpression.UNLIMITED) {
      if (resultCandidate instanceof Collection) {
        return resultCandidate;
      }
      else {
        return Collections.singleton(resultCandidate);
      }
    }
    else {
      if (resultCandidate instanceof Collection) {
        throw new ModelException(ModelException.GENERAL_ERROR, "Cannot set multiple values to single-valued feature.");
      }
      else {
        return resultCandidate;
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private Object encapsulateSingle(Object value, EMFFeature feature) {
    if (feature instanceof EMFRelationshipEnd) {
      return encapsulate((EObject) value);
    }
    else if (feature instanceof EMFEnumerationProperty) {
      return feature.getMetamodelImpl().encapsulate((EEnumLiteral) value);
    }
    else {
      return value;
    }
  }

  @SuppressWarnings("rawtypes")
  private Object encapsulateAll(Collection<Object> values, EMFFeature feature) {
    List<Object> result = Lists.newLinkedList();
    for (Object value : values) {
      result.add(encapsulateSingle(value, feature));
    }
    return result;
  }

  private EMFModelElement encapsulate(EObject eObject) {
    if (!this.elements.containsKey(eObject)) {
      if (EMFMetamodel.isSubstantialType(eObject.eClass())) {
        this.elements.put(eObject, new EMFModelInstance(eObject));
      }
      if (EMFMetamodel.isRelationshipType(eObject.eClass())) {
        this.elements.put(eObject, new EMFModelLink(eObject));
      }
    }
    return this.elements.get(eObject);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Object unwrap(Object value, EMFFeature feature) {
    if (value == null) {
      if (feature instanceof EMFEnumerationProperty) {
        EAttribute eAtt = (EAttribute) feature.getWrapped();
        EEnum eEnum = (EEnum) eAtt.getEType();
        return eEnum.getELiterals().get(0);
      }
      else {
        return null;
      }
    }

    Object resultCandidate = value instanceof Collection ? unwrapAll((Collection<Object>) value, feature) : unwrapSingle(value, feature);
    if (feature.getUpperBound() == FeatureExpression.UNLIMITED) {
      if (resultCandidate instanceof Collection) {
        return resultCandidate;
      }
      else {
        return Collections.singletonList(resultCandidate);
      }
    }
    else {
      if (resultCandidate instanceof Collection) {
        throw new ModelException(ModelException.GENERAL_ERROR, "Cannot get multiple values from single-valued feature.");
      }
      else {
        return resultCandidate;
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private Object unwrapSingle(Object value, EMFFeature feature) {
    if (feature instanceof EMFRelationshipEnd) {
      if (value instanceof EObject) {
        return value;
      }
      else {
        return unwrap((UniversalModelExpression) value);
      }

    }
    else if (feature instanceof EMFEnumerationProperty) {
      return ((EMFEnumerationLiteral) ((EMFEnumerationProperty) feature).getType().findLiteralByPersistentName(
          ((EMFEnumerationLiteral) value).getPersistentName())).getWrapped();
    }
    else {
      return value;
    }
  }

  @SuppressWarnings("rawtypes")
  private Object unwrapAll(Collection<Object> values, EMFFeature feature) {
    List<Object> result = Lists.newLinkedList();
    for (Object value : values) {
      result.add(unwrapSingle(value, feature));
    }
    return result;
  }

  private EObject unwrap(UniversalModelExpression expression) {
    if (expression instanceof EMFModelElement) {
      return ((EMFModelElement) expression).getWrapped();
    }
    throw new ModelException(ModelException.GENERAL_ERROR, "Cannot process non-canonic instances or links.");
  }

  /**{@inheritDoc}**/
  protected final boolean canCreate(UniversalTypeExpression universalType) {
    if (universalType == null || getContext() == null) {
      return false;
    }
    return true;
  }

  /**{@inheritDoc}**/
  protected final boolean canDelete(LinkExpression linkExpression) {
    if (!(linkExpression instanceof EMFModelLink) || getContext() == null) {
      return false;
    }
    return true;
  }

  /**{@inheritDoc}**/
  protected final boolean canDelete(UniversalTypeExpression typeExpression) {
    if (typeExpression == null || getContext() == null) {
      return false;
    }
    return true;
  }

  protected final boolean canDelete(InstanceExpression instance) {
    if (instance == null || getContext() == null) {
      return false;
    }
    return canDelete(((EMFMetamodel) getMetamodel()).encapsulate(unwrap(instance).eClass())) && true;
  }

  /**{@inheritDoc}**/
  protected final boolean canEdit(RelationshipEndExpression relationshipEndExpression) {
    if (relationshipEndExpression == null || getContext() == null) {
      return false;
    }
    return true;
  }

  /**{@inheritDoc}**/
  protected final boolean canEdit(UniversalModelExpression expression, PropertyExpression<?> property) {
    if (expression == null || property == null || getContext() == null) {
      return false;
    }
    return true;
  }

  /**{@inheritDoc}**/
  protected final boolean canEdit(UniversalModelExpression from, UniversalModelExpression to, RelationshipExpression relationship) {
    if (from == null || to == null || relationship == null || getContext() == null) {
      return false;
    }
    return true;
  }

  /**{@inheritDoc}**/
  protected final boolean canDelete(UniversalModelExpression instance) {
    if (instance == null || getContext() == null) {
      return false;
    }
    return true;
  }

  /**{@inheritDoc}**/
  protected final Object getValue(ElasticeamContext ctx, UniversalModelExpression expression, PropertyExpression<?> property) {
    return getValue(ctx, expression, (EMFProperty<?>) property);
  }

  /**{@inheritDoc}**/
  protected final void setValue(ElasticeamContext ctx, UniversalModelExpression expression, PropertyExpression<?> property, Object value) {
    setValue(ctx, expression, (EMFProperty<?>) property, value);
  }

  /**{@inheritDoc}**/
  protected final Object getValue(ElasticeamContext ctx, UniversalModelExpression expression, RelationshipEndExpression relationshipEnd) {
    if (EMFRelationshipEnd.class.isInstance(relationshipEnd)) {
      return getValue(ctx, expression, (EMFRelationshipEnd) relationshipEnd);
    }
    throw new ModelException(ModelException.GENERAL_ERROR, "Cannot process non-canonic instances or links.");
  }

  final void setValue(UniversalModelExpression expression, BuiltinPrimitiveProperty property, Object value) {
    setValue(getContext(), expression, property, value);
  }

  protected Object getValue(ElasticeamContext ctx, UniversalModelExpression expression, BuiltinPrimitiveProperty property) {
    return getValue(expression, property);
  }

  /**{@inheritDoc}**/
  @Override
  protected void unlink(ElasticeamContext ctx, UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to) {
    if (canEdit(from, to, via.getRelationship()) && via instanceof EMFRelationshipEnd) {
      EMFRelationshipEnd emfVia = (EMFRelationshipEnd) via;
      EObject fromObject = unwrap(from);
      EObject toObject = unwrap(to);
      Object value = unwrap(fromObject.eGet(emfVia.getWrapped()), emfVia);
      if (value instanceof Collection) {
        @SuppressWarnings("unchecked")
        List<EObject> newValues = Lists.newLinkedList((Collection<EObject>) value);
        newValues.remove(toObject);
        fromObject.eSet(emfVia.getWrapped(), newValues);
      }
      else if (toObject.equals(value)) {
        fromObject.eUnset(emfVia.getWrapped());
      }
    }
    else {
      throw new ModelException(ModelException.ILLEGAL_ACCESS, "Insufficient rights for unlinking " + from + " and " + to + " via " + via);
    }
  }

}

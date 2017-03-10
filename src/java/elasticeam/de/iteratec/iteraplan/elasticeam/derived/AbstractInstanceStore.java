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
package de.iteratec.iteraplan.elasticeam.derived;

import java.math.BigInteger;
import java.util.Collection;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.emfimpl.EMFMetamodel;
import de.iteratec.iteraplan.elasticeam.emfimpl.EMFProperty;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinFeature;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveProperty;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.LinkExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.util.ElasticeamContextUtil;


/**
 * Contains instances, by providing similar methods as defined by {@link Model} interface without exposing them;
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class AbstractInstanceStore<I extends InstanceExpression, S extends SubstantialTypeExpression, L extends LinkExpression, R extends RelationshipTypeExpression, P extends PropertyExpression<?>, E extends RelationshipEndExpression> {

  private Metamodel metamodel = new EMFMetamodel("empty metamodel");

  protected AbstractInstanceStore(Metamodel metamodel) {
    this.metamodel = metamodel;
  }

  protected Metamodel getMetamodel() {
    return this.metamodel;
  }

  @SuppressWarnings("unchecked")
  private S map(SubstantialTypeExpression type) {
    UniversalTypeExpression myType = this.metamodel.findUniversalTypeByPersistentName(type.getPersistentName());
    if (myType == null) {
      throw new ModelException(ModelException.GENERAL_ERROR, "Could not find type - incompatible metamodels !?");
    }
    return myType instanceof SubstantialTypeExpression ? (S) myType : null;
  }

  @SuppressWarnings("unchecked")
  private R map(RelationshipTypeExpression type) {
    UniversalTypeExpression myType = this.metamodel.findUniversalTypeByPersistentName(type.getPersistentName());
    if (myType == null) {
      throw new ModelException(ModelException.GENERAL_ERROR, "Could not find type - incompatible metamodels !?");
    }
    return myType instanceof RelationshipTypeExpression ? (R) myType : null;
  }

  private RelationshipEndExpression map(RelationshipEndExpression end) {
    if (end instanceof BuiltinFeature) {
      return end;
    }
    return this.metamodel.findUniversalTypeByPersistentName(end.getHolder().getPersistentName()).findRelationshipEndByPersistentName(
        end.getPersistentName());
  }

  private PropertyExpression<?> map(PropertyExpression<?> property) {
    if (property instanceof BuiltinFeature) {
      return property;
    }
    return this.metamodel.findUniversalTypeByPersistentName(property.getHolder().getPersistentName()).findPropertyByPersistentName(
        property.getPersistentName());
  }

  final InstanceExpression create(SubstantialTypeExpression type) {
    if (canCreate(map(type))) {
      return createInstance(map(type));
    }
    throw new ModelException(ModelException.ILLEGAL_ACCESS, "Failed to create instance expression of type: " + type);
  }

  final LinkExpression create(RelationshipTypeExpression type) {
    if (canCreate(map(type))) {
      return createLink(map(type));
    }
    throw new ModelException(ModelException.ILLEGAL_ACCESS, "Failed to create link expression for relationship type: " + type);
  }

  @SuppressWarnings("unchecked")
  final void delete(InstanceExpression instance) {
    if (canDelete(instance)) {
      deleteInstance((I) instance);
    }
    else {
      throw new ModelException(ModelException.ILLEGAL_ACCESS, "Failed to delete instance expression: " + instance);
    }
  }

  @SuppressWarnings("unchecked")
  final void delete(LinkExpression link) {
    if (canDelete(link)) {
      deleteLink((L) link);
    }
    else {
      throw new ModelException(ModelException.ILLEGAL_ACCESS, "Failed to delete link expression: " + link);
    }
  }

  @SuppressWarnings("unchecked")
  protected void link(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to) {
    RelationshipEndExpression queryvia = map(via);
    if (canEdit(from, to, queryvia.getRelationship()) && canEdit(queryvia)) {
      link(getContext(), from, (E) queryvia, to);
    }
    else {
      throw new ModelException(ModelException.ILLEGAL_ACCESS, "Failed to create link expression from: " + from + " to: " + to + " via: " + queryvia);
    }

  }

  protected final void unlink(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to) {
    RelationshipEndExpression queryvia = map(via);
    if (canEdit(from, to, queryvia.getRelationship()) && canEdit(queryvia)) {
      unlink(getContext(), from, queryvia, to);
    }
    else {
      throw new ModelException(ModelException.ILLEGAL_ACCESS, "Failed to unlink connection between " + from + " and " + to + " via " + queryvia);
    }
  }

  final Collection<InstanceExpression> findAll(SubstantialTypeExpression type) {
    return findAllInstances(getContext(), map(type));
  }

  final Collection<LinkExpression> findAll(RelationshipTypeExpression type) {
    return findAllLinks(map(type));
  }

  @SuppressWarnings("unchecked")
  final BindingSet findAll(RelationshipEndExpression via) {
    return findAll(getContext(), (E) map(via));
  }

  final InstanceExpression findById(SubstantialTypeExpression type, BigInteger id) {
    for (InstanceExpression instance : findAll(type)) {
      if (getValue(instance, UniversalTypeExpression.ID_PROPERTY).equals(id)) {
        return instance;
      }
    }
    return null;
  }

  final LinkExpression findById(RelationshipTypeExpression type, BigInteger id) {
    for (LinkExpression linkExpression : findAll(type)) {
      if (getValue(linkExpression, UniversalTypeExpression.ID_PROPERTY).equals(id)) {
        return linkExpression;
      }
    }
    return null;
  }

  final InstanceExpression findByName(SubstantialTypeExpression type, String name) {
    for (InstanceExpression instance : findAll(type)) {
      if (getValue(instance, MixinTypeNamed.NAME_PROPERTY).equals(name)) {
        return instance;
      }
    }
    return null;
  }

  final Object getValue(UniversalModelExpression expression, PropertyExpression<?> property) {
    PropertyExpression<?> queryproperty = map(property);
    if (BuiltinPrimitiveProperty.class.isInstance(queryproperty)) {
      return getValue(getContext(), expression, (BuiltinPrimitiveProperty) queryproperty);
    }
    if (!(queryproperty instanceof EMFProperty)) {
      throw new ModelException(ModelException.GENERAL_ERROR, "The property of class " + queryproperty.getClass() + " is not canonic.");
    }
    return getValue(getContext(), expression, queryproperty);
  }

  final Object getValue(UniversalModelExpression expression, RelationshipEndExpression relationshipEnd) {
    return getValue(getContext(), expression, map(relationshipEnd));
  }

  final void setValue(UniversalModelExpression expression, PropertyExpression<?> property, Object value) {
    PropertyExpression<?> queryproperty = map(property);
    if (canEdit(expression, queryproperty)) {
      if (BuiltinPrimitiveProperty.class.isInstance(queryproperty)) {
        setValue(getContext(), expression, (BuiltinPrimitiveProperty) queryproperty, value);
      }
      else {
        setValue(getContext(), expression, queryproperty, value);
      }
    }
    else {
      throw new ModelException(ModelException.ILLEGAL_ACCESS, "Insufficient rights for setting value of " + queryproperty);
    }
  }

  protected abstract boolean canCreate(UniversalTypeExpression universalType);

  protected abstract boolean canDelete(LinkExpression linkExpression);

  protected abstract boolean canDelete(UniversalTypeExpression typeExpression);

  protected abstract boolean canDelete(UniversalModelExpression instance);

  protected abstract boolean canEdit(RelationshipEndExpression relationshipEnd);

  protected abstract boolean canEdit(UniversalModelExpression expression, PropertyExpression<?> property);

  protected abstract boolean canEdit(UniversalModelExpression from, UniversalModelExpression to, RelationshipExpression relationship);

  protected abstract InstanceExpression createInstance(S type);

  protected abstract LinkExpression createLink(R type);

  protected abstract void deleteInstance(I instance);

  protected abstract void deleteLink(L link);

  protected abstract void link(ElasticeamContext ctx, UniversalModelExpression from, E via, UniversalModelExpression to);

  protected abstract void unlink(ElasticeamContext ctx, UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to);

  protected abstract Collection<InstanceExpression> findAllInstances(ElasticeamContext context, S type);

  protected abstract Collection<LinkExpression> findAllLinks(R type);

  protected abstract BindingSet findAll(ElasticeamContext ctx, E via);

  protected abstract Object getValue(ElasticeamContext ctx, UniversalModelExpression expression, BuiltinPrimitiveProperty property);

  protected abstract Object getValue(ElasticeamContext ctx, UniversalModelExpression expression, PropertyExpression<?> property);

  protected abstract Object getValue(ElasticeamContext ctx, UniversalModelExpression expression, RelationshipEndExpression relationshipEnd);

  protected abstract void setValue(ElasticeamContext ctx, UniversalModelExpression expression, BuiltinPrimitiveProperty property, Object value);

  protected abstract void setValue(ElasticeamContext ctx, UniversalModelExpression expression, PropertyExpression<?> property, Object value);

  protected static ElasticeamContext getContext() {
    //TODO if the context is removed from all signatures in this class the entire class structure of the subclasses is no longer ok.
    return ElasticeamContextUtil.getCurrentContext();
  }
}

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
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.ConnectHandler;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.InstanceHandler;
import de.iteratec.iteraplan.elasticeam.model.LinkExpression;
import de.iteratec.iteraplan.elasticeam.model.LinkHandler;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.PropertyHandler;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.operator.count.CountPropertyHandler;
import de.iteratec.iteraplan.elasticeam.operator.expand.ExpandConnectHandler;
import de.iteratec.iteraplan.elasticeam.operator.expand.ExpandInstanceHandler;
import de.iteratec.iteraplan.elasticeam.operator.expand.ExpandPropertyHandler;
import de.iteratec.iteraplan.elasticeam.operator.filter.FilterConnectHandler;
import de.iteratec.iteraplan.elasticeam.operator.filter.FilterInstanceHandler;
import de.iteratec.iteraplan.elasticeam.operator.filter.FilterLinkHandler;
import de.iteratec.iteraplan.elasticeam.operator.filter.FilterPropertyHandler;
import de.iteratec.iteraplan.elasticeam.operator.fold.FoldLevelPropertyHandler;
import de.iteratec.iteraplan.elasticeam.operator.fold.UnfoldConnectHandler;
import de.iteratec.iteraplan.elasticeam.operator.join.JoinConnectHandler;
import de.iteratec.iteraplan.elasticeam.operator.move.MovePropertyHandler;
import de.iteratec.iteraplan.elasticeam.operator.nullify.NullifyConnectHandler;
import de.iteratec.iteraplan.elasticeam.operator.nullify.NullifyInstanceHandler;
import de.iteratec.iteraplan.elasticeam.operator.nullify.NullifyLinkHandler;
import de.iteratec.iteraplan.elasticeam.operator.nullify.NullifyPropertyHandler;
import de.iteratec.iteraplan.elasticeam.operator.objectify.ObjectifyConnectHandler;
import de.iteratec.iteraplan.elasticeam.operator.objectify.ObjectifyInstanceHandler;
import de.iteratec.iteraplan.elasticeam.operator.objectify.ObjectifyPropertyHandler;
import de.iteratec.iteraplan.elasticeam.operator.power.PowerConnectHandler;
import de.iteratec.iteraplan.elasticeam.operator.power.PowerInstanceHandler;
import de.iteratec.iteraplan.elasticeam.operator.rangify.RangifyPropertyHandler;


/**
 * A {@link QueryableModel} holds instance data ({@link AbstractInstanceStore}) and can handle 
 * create, delete, read (find) and edit operations for qualified instances
 */
public class QueryableModel implements Model {

  private final AbstractInstanceStore<?, ?, ?, ?, ?, ?> instanceStore;

  private final Set<PropertyHandler>                    propertyHandlers = new HashSet<PropertyHandler>();
  private final Set<InstanceHandler>                    instanceHandlers = new HashSet<InstanceHandler>();
  private final Set<ConnectHandler>                     connectHandlers  = new HashSet<ConnectHandler>();
  private final Set<LinkHandler>                        linkHandlers     = new HashSet<LinkHandler>();

  public QueryableModel(AbstractInstanceStore<?, ?, ?, ?, ?, ?> instanceStore) {
    //objectify handlers
    instanceHandlers.add(new ObjectifyInstanceHandler(this));
    connectHandlers.add(new ObjectifyConnectHandler(this));
    propertyHandlers.add(new ObjectifyPropertyHandler(this));

    //join handlers
    connectHandlers.add(new JoinConnectHandler(this));

    //filter handlers
    instanceHandlers.add(new FilterInstanceHandler(this));
    linkHandlers.add(new FilterLinkHandler(this));
    connectHandlers.add(new FilterConnectHandler(this));
    propertyHandlers.add(new FilterPropertyHandler(this));

    //rangify handlers
    propertyHandlers.add(new RangifyPropertyHandler(this));

    //move handlers
    propertyHandlers.add(new MovePropertyHandler(this));

    //power handlers
    instanceHandlers.add(new PowerInstanceHandler(this));
    connectHandlers.add(new PowerConnectHandler(this));

    //expand handlers
    instanceHandlers.add(new ExpandInstanceHandler(this));
    connectHandlers.add(new ExpandConnectHandler(this));
    propertyHandlers.add(new ExpandPropertyHandler(this));

    //fold handlers
    connectHandlers.add(new UnfoldConnectHandler(this));
    propertyHandlers.add(new FoldLevelPropertyHandler(this));

    //count handlers
    propertyHandlers.add(new CountPropertyHandler(this));

    //nullify handlers
    instanceHandlers.add(new NullifyInstanceHandler(this));
    linkHandlers.add(new NullifyLinkHandler(this));
    connectHandlers.add(new NullifyConnectHandler(this));
    propertyHandlers.add(new NullifyPropertyHandler(this));

    this.instanceStore = instanceStore;
  }

  /**{@inheritDoc}**/
  public final Object getValue(UniversalModelExpression element, RelationshipEndExpression relationshipEnd) {
    ConnectHandler connectHandler = getConnectHandler(unmap(relationshipEnd));
    if (connectHandler == null) {
      return this.instanceStore.getValue(element, unmap(relationshipEnd));
    }
    else {
      return connectHandler.getValue(element, unmap(relationshipEnd));
    }
  }

  /**{@inheritDoc}**/
  public final void unlink(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to) {
    ConnectHandler connectHandler = getConnectHandler(unmap(via));
    if (connectHandler == null) {
      instanceStore.unlink(from, unmap(via), to);
    }
    else {
      connectHandler.unlink(from, unmap(via), to);
    }
  }

  /**{@inheritDoc}**/
  public final Object getValue(UniversalModelExpression expression, PropertyExpression<?> property) {
    PropertyHandler propertyHandler = getPropertyHandler(unmap(property));
    if (propertyHandler == null) {
      return instanceStore.getValue(expression, unmap(property));
    }
    else {
      return propertyHandler.getValue(expression, unmap(property));
    }
  }

  /**{@inheritDoc}**/
  public final void setValue(UniversalModelExpression expression, PropertyExpression<?> property, Object value) {
    PropertyHandler propertyHandler = getPropertyHandler(unmap(property));
    if (propertyHandler == null) {
      instanceStore.setValue(expression, unmap(property), value);
    }
    else {
      propertyHandler.setValue(expression, unmap(property), value);
    }

  }

  /**{@inheritDoc}**/
  public final InstanceExpression create(SubstantialTypeExpression type) {
    InstanceHandler instanceHandler = getInstanceHandler(unmap(type));
    if (instanceHandler == null) {
      return instanceStore.create(unmap(type));
    }
    else {
      return instanceHandler.create(unmap(type));
    }
  }

  /**{@inheritDoc}**/
  public final Collection<InstanceExpression> findAll(SubstantialTypeExpression type) {
    InstanceHandler instanceHandler = getInstanceHandler(unmap(type));
    if (instanceHandler == null) {
      return this.instanceStore.findAll(unmap(type));
    }
    else {
      return instanceHandler.findAll(unmap(type));
    }
  }

  /**{@inheritDoc}**/
  public InstanceExpression findByName(SubstantialTypeExpression type, String name) {
    //FIXME Instance Handler must support findByName
    return instanceStore.findByName(unmap(type), name);
  }

  /**{@inheritDoc}**/
  public InstanceExpression findById(SubstantialTypeExpression type, BigInteger id) {
    //FIXME Instance Handler must support findById
    return instanceStore.findById(unmap(type), id);
  }

  /**{@inheritDoc}**/
  public final void delete(InstanceExpression instance) {
    InstanceHandler instanceHandler = getInstanceHandler(instance);
    if (instanceHandler == null) {
      this.instanceStore.delete(instance);
    }
    else {
      instanceHandler.delete(instance);
    }
  }

  /**{@inheritDoc}**/
  public final void link(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to) {
    ConnectHandler connectHandler = getConnectHandler(unmap(via));
    if (connectHandler == null) {
      instanceStore.link(from, unmap(via), to);
    }
    else {
      connectHandler.link(from, unmap(via), to);
    }
  }

  /**{@inheritDoc}**/
  public final BindingSet findAll(RelationshipEndExpression via) {
    ConnectHandler connectHandler = getConnectHandler(unmap(via));
    if (connectHandler == null) {
      return this.instanceStore.findAll(unmap(via));
    }
    else {
      return connectHandler.findAll(unmap(via));
    }
  }

  /**{@inheritDoc}**/
  public final LinkExpression create(RelationshipTypeExpression type) {
    LinkHandler linkHandler = getLinkHandler(unmap(type));
    if (linkHandler == null) {
      return instanceStore.create(unmap(type));
    }
    else {
      return linkHandler.create(unmap(type));
    }
  }

  /**{@inheritDoc}**/
  public final Collection<LinkExpression> findAll(RelationshipTypeExpression type) {
    LinkHandler linkHandler = getLinkHandler(unmap(type));
    if (linkHandler == null) {
      return instanceStore.findAll(unmap(type));
    }
    else {
      return linkHandler.findAll(unmap(type));
    }
  }

  /**{@inheritDoc}**/
  public LinkExpression findById(RelationshipTypeExpression type, BigInteger id) {
    //FIXME Link Handler must support findById
    return instanceStore.findById(unmap(type), id);
  }

  /**{@inheritDoc}**/
  public final void delete(LinkExpression link) {
    LinkHandler linkHandler = getLinkHandler(link);
    if (linkHandler == null) {
      instanceStore.delete(link);
    }
    else {
      linkHandler.delete(link);
    }
    instanceStore.delete(link);
  }

  /**{@inheritDoc}**/
  public boolean canCreate(UniversalTypeExpression universalType) {
    //FIXME Instance Handler and Link Handler must support canCreate
    return this.instanceStore.canCreate(unmap(universalType));
  }

  /**{@inheritDoc}**/
  public boolean canDelete(UniversalTypeExpression typeExpression) {
    //FIXME Instance Handler and Link Handler must support canDelete
    return instanceStore.canDelete(unmap(typeExpression));
  }

  /**{@inheritDoc}**/
  public boolean canDelete(InstanceExpression instance) {
    //FIXME Instance Handler must support canDelete    
    return instanceStore.canDelete(instance);
  }

  /**{@inheritDoc}**/
  public boolean canDelete(LinkExpression linkExpression) {
    //FIXME Link Handler must support canDelete
    return instanceStore.canDelete(linkExpression);
  }

  /**{@inheritDoc}**/
  public boolean canEdit(RelationshipEndExpression relationshipEndExpression) {
    //FIXME Connect Handler must support canEdit
    return instanceStore.canEdit(unmap(relationshipEndExpression));
  }

  /**{@inheritDoc}**/
  public boolean canEdit(UniversalModelExpression expression, PropertyExpression<?> property) {
    //FIXME Property Handler must support canEdit
    return instanceStore.canEdit(expression, unmap(property));
  }

  /**{@inheritDoc}**/
  public boolean canEdit(UniversalModelExpression from, UniversalModelExpression to, RelationshipExpression relationship) {
    //FIXME Connect Handler must support canEdit
    return instanceStore.canEdit(from, to, unmap(relationship));
  }

  /**{@inheritDoc}**/
  public final Collection<UniversalModelExpression> findAll(UniversalTypeExpression type) {
    Set<UniversalModelExpression> result = Sets.newHashSet();
    if (SubstantialTypeExpression.class.isInstance(unmap(type))) {
      result.addAll(findAll((SubstantialTypeExpression) unmap(type)));
    }
    else if (RelationshipTypeExpression.class.isInstance(unmap(type))) {
      result.addAll(findAll((RelationshipTypeExpression) unmap(type)));
    }
    return result;
  }

  /**{@inheritDoc}**/
  public final UniversalModelExpression findById(UniversalTypeExpression type, BigInteger id) {
    if (SubstantialTypeExpression.class.isInstance(unmap(type))) {
      return findById((SubstantialTypeExpression) unmap(type), id);
    }
    else if (RelationshipTypeExpression.class.isInstance(unmap(type))) {
      return findById((RelationshipTypeExpression) unmap(type), id);
    }
    return null;
  }

  private InstanceHandler getInstanceHandler(SubstantialTypeExpression type) {
    for (InstanceHandler candidate : this.instanceHandlers) {
      if (candidate.isHandlerFor(type)) {
        return candidate;
      }
    }
    return null;
  }

  private InstanceHandler getInstanceHandler(InstanceExpression instance) {
    for (InstanceHandler candidate : this.instanceHandlers) {
      if (candidate.isHandlerFor(instance)) {
        return candidate;
      }
    }
    return null;
  }

  private PropertyHandler getPropertyHandler(PropertyExpression<?> property) {
    for (PropertyHandler candidate : this.propertyHandlers) {
      if (candidate.isHandlerFor(property)) {
        return candidate;
      }
    }
    return null;
  }

  private ConnectHandler getConnectHandler(RelationshipEndExpression relationshipEnd) {
    for (ConnectHandler candidate : this.connectHandlers) {
      if (candidate.isHandlerFor(relationshipEnd)) {
        return candidate;
      }
    }
    return null;
  }

  private LinkHandler getLinkHandler(RelationshipTypeExpression type) {
    for (LinkHandler candidate : this.linkHandlers) {
      if (candidate.isHandlerFor(type)) {
        return candidate;
      }
    }
    return null;
  }

  private LinkHandler getLinkHandler(LinkExpression link) {
    for (LinkHandler candidate : this.linkHandlers) {
      if (candidate.isHandlerFor(link)) {
        return candidate;
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private static <N extends NamedExpression> N unmap(N input) {
    return input instanceof DecoratedNamedElement ? ((DecoratedNamedElement<N>) input).getWrapped() : input;
  }
}

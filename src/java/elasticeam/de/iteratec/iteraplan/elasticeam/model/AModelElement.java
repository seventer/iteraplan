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
package de.iteratec.iteraplan.elasticeam.model;

import java.util.Collection;
import java.util.Collections;

import de.iteratec.iteraplan.elasticeam.derived.QueryableModel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;


/**
 * Defines the 'has properties' feature for model expressions.
 */
public abstract class AModelElement implements UniversalModelExpression {

  protected abstract QueryableModel getQueryableModel();
  protected abstract void setQueryableModel(QueryableModel queryableModel);
  
  @Deprecated
  public final Integer getId() {
    return null;
  }

  /**{@inheritDoc}**/
  @SuppressWarnings("rawtypes")
  public final Object getValue(PropertyExpression property) {
    Object value = getQueryableModel().getValue(this, property);
    if (value instanceof Collection) {
      if (((Collection) value).isEmpty()) {
        return null;
      }
      else {
        return ((Collection) value).iterator().next();
      }
    }
    else {
      return value;
    }
  }

  /**{@inheritDoc}**/
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public final Collection<Object> getValues(PropertyExpression property) {
    Object value = getQueryableModel().getValue(this, property);
    if (value instanceof Collection) {
      return (Collection) value;
    }
    else if (value == null) {
      return Collections.emptyList();
    }
    else {
      return Collections.singleton(value);
    }
  }

  /**{@inheritDoc}**/
  public final void setValue(PropertyExpression<?> property, Object value) {
    getQueryableModel().setValue(this, property, value);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public final UniversalModelExpression getConnected(RelationshipEndExpression relationshipEnd) {
    Object value = getQueryableModel().getValue(this, relationshipEnd);
    if (value instanceof Collection) {
      if (((Collection) value).isEmpty()) {
        return null;
      }
      else {
        return ((Collection<UniversalModelExpression>) value).iterator().next();
      }
    }
    else {
      return (UniversalModelExpression) value;
    }
  }

  @SuppressWarnings("unchecked")
  public final Collection<UniversalModelExpression> getConnecteds(RelationshipEndExpression relationshipEnd) {
    Object value = getQueryableModel().getValue(this, relationshipEnd);
    if (value instanceof Collection) {
      return (Collection<UniversalModelExpression>) value;
    }
    else if (value == null) {
      return Collections.emptyList();
    }
    else {
      return Collections.singleton((UniversalModelExpression) value);
    }
  }

  /**{@inheritDoc}**/
  @SuppressWarnings("unchecked")
  public final void connect(RelationshipEndExpression relationshipEnd, Object value) {
    if (value instanceof Collection) {
      for (UniversalModelExpression to : (Collection<UniversalModelExpression>) value) {
        getQueryableModel().link(this, relationshipEnd, to);
      }
    }
    else if (value instanceof UniversalModelExpression) {
      getQueryableModel().link(this, relationshipEnd, (UniversalModelExpression) value);
    }
  }

  /**{@inheritDoc}**/
  @SuppressWarnings("unchecked")
  public final void disconnect(RelationshipEndExpression relationshipEnd, Object value) {
    if (value instanceof Collection) {
      for (UniversalModelExpression to : (Collection<UniversalModelExpression>) value) {
        getQueryableModel().unlink(this, relationshipEnd, to);
      }
    }
    else if (value instanceof UniversalModelExpression) {
      getQueryableModel().unlink(this, relationshipEnd, (UniversalModelExpression) value);
    }
  }
 
}

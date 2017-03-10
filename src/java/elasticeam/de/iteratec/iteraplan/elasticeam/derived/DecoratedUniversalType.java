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

import java.util.List;
import java.util.Observable;

import org.apache.commons.collections.ListUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.util.CompareUtil;


@edu.umd.cs.findbugs.annotations.SuppressWarnings({ "EQ_COMPARETO_USE_OBJECT_EQUALS" })
abstract class DecoratedUniversalType<U extends UniversalTypeExpression> extends DecoratedNamedElement<U> implements UniversalTypeExpression {

  private final AddReplaceList<PropertyExpression<?>>                      properties       = new AddReplaceList<PropertyExpression<?>>() {
                                                                                              @Override
                                                                                              protected List<PropertyExpression<?>> rawGet() {
                                                                                                return DecoratedUniversalType.this.getWrapped()
                                                                                                    .getProperties();
                                                                                              }

                                                                                              @Override
                                                                                              protected PropertyExpression<?> rawFindByPersistentname(String persistentName) {
                                                                                                return DecoratedUniversalType.this.getWrapped()
                                                                                                    .findPropertyByPersistentName(persistentName);
                                                                                              }

                                                                                              @Override
                                                                                              protected PropertyExpression<?> rawFindByName(String name) {
                                                                                                return DecoratedUniversalType.this.getWrapped()
                                                                                                    .findPropertyByName(name);
                                                                                              }

                                                                                              @Override
                                                                                              protected List<? extends PropertyExpression<?>> getAdditionalElements() {
                                                                                                return DecoratedUniversalType.this.additionalProperties;
                                                                                              }
                                                                                            };
  private final AddReplaceList<RelationshipEndExpression>                  relationshipEnds = new AddReplaceList<RelationshipEndExpression>(
                                                                                                getRelationshipEndReplacer()) {
                                                                                              @Override
                                                                                              protected List<RelationshipEndExpression> rawGet() {
                                                                                                return DecoratedUniversalType.this.getWrapped()
                                                                                                    .getRelationshipEnds();
                                                                                              }

                                                                                              @Override
                                                                                              protected RelationshipEndExpression rawFindByPersistentname(String persistentName) {
                                                                                                return DecoratedUniversalType.this.getWrapped()
                                                                                                    .findRelationshipEndByPersistentName(
                                                                                                        persistentName);
                                                                                              }

                                                                                              @Override
                                                                                              protected RelationshipEndExpression rawFindByName(String name) {
                                                                                                return DecoratedUniversalType.this.getWrapped()
                                                                                                    .findRelationshipEndByName(name);
                                                                                              }

                                                                                              @Override
                                                                                              protected List<? extends RelationshipEndExpression> getAdditionalElements() {
                                                                                                return DecoratedUniversalType.this.additionalReferences;
                                                                                              }
                                                                                            };

  private final List<PropertyExpression<?>>                                additionalProperties;
  private final List<RelationshipEndExpression>                            additionalReferences;
  private final Function<UniversalTypeExpression, UniversalTypeExpression> replacer;

  DecoratedUniversalType(U wrapped, Function<UniversalTypeExpression, UniversalTypeExpression> replacer) {
    super(wrapped);
    this.additionalProperties = Lists.newLinkedList();
    this.additionalReferences = Lists.newLinkedList();
    this.replacer = replacer;
  }

  final Function<RelationshipEndExpression, RelationshipEndExpression> getRelationshipEndReplacer() {
    return new Function<RelationshipEndExpression, RelationshipEndExpression>() {
      public RelationshipEndExpression apply(RelationshipEndExpression input) {
        if (input == null) {
          return input;
        }
        return new DecoratedRelationshipEnd(input, replacer);
      }
    };
  }

  final void addFeature(FeatureExpression<?> feature) {
    if (feature instanceof PropertyExpression) {
      this.additionalProperties.add((PropertyExpression<?>) feature);
    }
    else if (feature instanceof RelationshipEndExpression) {
      this.additionalReferences.add((RelationshipEndExpression) feature);
    }
  }

  /**{@inheritDoc}**/
  public final List<PropertyExpression<?>> getProperties() {
    return this.properties.get();
  }

  /**{@inheritDoc}**/
  public final PropertyExpression<?> findPropertyByPersistentName(String persistentName) {
    return this.properties.findByPersistentName(persistentName);
  }

  /**{@inheritDoc}**/
  public final PropertyExpression<?> findPropertyByName(String name) {
    return this.properties.findByName(name);
  }

  /**{@inheritDoc}**/
  public final List<RelationshipEndExpression> getRelationshipEnds() {
    return this.relationshipEnds.get();
  }

  /**{@inheritDoc}**/
  public final RelationshipEndExpression findRelationshipEndByPersistentName(String persistentName) {
    return this.relationshipEnds.findByPersistentName(persistentName);
  }

  /**{@inheritDoc}**/
  public final RelationshipEndExpression findRelationshipEndByName(String name) {
    return this.relationshipEnds.findByName(name);
  }

  /**{@inheritDoc}**/
  @SuppressWarnings("unchecked")
  public final List<FeatureExpression<?>> getFeatures() {
    return ListUtils.sum(this.properties.get(), this.relationshipEnds.get());
  }

  /**{@inheritDoc}**/
  public final FeatureExpression<?> findFeatureByPersistentName(String persistentName) {
    FeatureExpression<?> result = this.properties.findByPersistentName(persistentName);
    if (result == null) {
      result = this.relationshipEnds.findByPersistentName(persistentName);
    }
    return result;
  }

  /**{@inheritDoc}**/
  public final FeatureExpression<?> findFeatureByName(String name) {
    FeatureExpression<?> result = this.properties.findByName(name);
    if (result == null) {
      result = this.relationshipEnds.findByName(name);
    }
    return result;
  }

  /**{@inheritDoc}**/
  public final int compareTo(UniversalTypeExpression o) {
    return CompareUtil.compareTo(this, o);
  }

  /**{@inheritDoc}**/
  public final void update(Observable arg0, Object arg1) {
    getWrapped().update(arg0, arg1);
  }

  /**{@inheritDoc}**/
  public final List<PropertyExpression<?>> getProperties(ElasticeamContext ctx) {
    return getProperties();
  }

  /**{@inheritDoc}**/
  public final PropertyExpression<?> findPropertyByName(ElasticeamContext ctx, String name) {
    return findPropertyByName(name);
  }

  /**{@inheritDoc}**/
  public final List<RelationshipEndExpression> getRelationshipEnds(ElasticeamContext ctx) {
    return getRelationshipEnds();
  }

  /**{@inheritDoc}**/
  public final RelationshipEndExpression findRelationshipEndByName(ElasticeamContext ctx, String name) {
    return findRelationshipEndByName(name);
  }
}
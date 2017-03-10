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
package de.iteratec.iteraplan.elasticeam.metamodel.impl;

import java.util.List;
import java.util.Map;
import java.util.Observable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.util.CompareUtil;
import de.iteratec.iteraplan.elasticeam.util.ElasticeamContextUtil;
import de.iteratec.iteraplan.elasticeam.util.I18nMap;


/**
 *
 */
public abstract class UniversalTypeImpl extends NamedExpressionImpl implements UniversalTypeExpression {

  private final I18nMap<FeatureExpression<?>>     features;
  private final Map<String, FeatureExpression<?>> featuresByPersistentName;
  private final String                            persistentName;

  /**
   * Default constructor.
   */
  public UniversalTypeImpl(String persistentName) {
    this.features = I18nMap.create();
    this.featuresByPersistentName = Maps.newHashMap();
    this.persistentName = persistentName;
    super.setAbbreviation(persistentName);
    super.setDescription(persistentName);
    super.setName(persistentName);
  }

  protected void init() {
    //TODO Init local names;
    for (FeatureExpression<?> feature : getFeatures()) {
      this.featuresByPersistentName.put(feature.getPersistentName(), feature);
    }
  }

  /**{@inheritDoc}**/
  public final String getPersistentName() {
    return this.persistentName;
  }

  /**{@inheritDoc}**/
  public final void update(Observable o, Object arg) {
    //Nothing here
  }

  /**{@inheritDoc}**/
  public final int compareTo(UniversalTypeExpression o) {
    return CompareUtil.compareTo(this, o);
  }

  /**{@inheritDoc}**/
  public final List<PropertyExpression<?>> getProperties() {
    List<PropertyExpression<?>> result = Lists.newLinkedList();
    for (FeatureExpression<?> feature : getFeatures()) {
      if (feature instanceof PropertyExpression) {
        result.add((PropertyExpression<?>) feature);
      }
    }
    return result;
  }

  /**{@inheritDoc}**/
  public final PropertyExpression<?> findPropertyByName(String name) {
    FeatureExpression<?> feature = findFeatureByName(name);
    if (feature instanceof PropertyExpression) {
      return (PropertyExpression<?>) feature;
    }
    else {
      return null;
    }
  }

  /**{@inheritDoc}**/
  public final List<RelationshipEndExpression> getRelationshipEnds() {
    List<RelationshipEndExpression> result = Lists.newLinkedList();
    for (FeatureExpression<?> feature : getFeatures()) {
      if (feature instanceof RelationshipEndExpression) {
        result.add((RelationshipEndExpression) feature);
      }
    }
    return result;
  }

  /**{@inheritDoc}**/
  public final RelationshipEndExpression findRelationshipEndByName(String name) {
    FeatureExpression<?> feature = findFeatureByName(name);
    if (feature instanceof RelationshipEndExpression) {
      return (RelationshipEndExpression) feature;
    }
    else {
      return null;
    }
  }

  /**{@inheritDoc}**/
  public final PropertyExpression<?> findPropertyByPersistentName(String pName) {
    FeatureExpression<?> feature = findFeatureByPersistentName(pName);
    if (feature instanceof PropertyExpression) {
      return (PropertyExpression<?>) feature;
    }
    else {
      return null;
    }
  }

  /**{@inheritDoc}**/
  public final RelationshipEndExpression findRelationshipEndByPersistentName(String pName) {
    FeatureExpression<?> feature = findFeatureByPersistentName(pName);
    if (feature instanceof RelationshipEndExpression) {
      return (RelationshipEndExpression) feature;
    }
    else {
      return null;
    }
  }

  /**{@inheritDoc}**/
  public final FeatureExpression<?> findFeatureByName(String name) {
    return this.features.get(ElasticeamContextUtil.getCurrentContext().getLocale(), name);
  }

  /**{@inheritDoc}**/
  public final FeatureExpression<?> findFeatureByPersistentName(String pName) {
    return this.featuresByPersistentName.get(pName);
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

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
package de.iteratec.iteraplan.elasticeam.metamodel.builtin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.MixinTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.FinalNamedExpressionImpl;
import de.iteratec.iteraplan.elasticeam.util.CompareUtil;


/**
 * Implementations of this class represent predefined universal types.
 */
public abstract class BuiltinUniversalType extends FinalNamedExpressionImpl implements MixinTypeExpression {

  private String                          persistentName;
  private List<PropertyExpression<?>>     properties;
  private List<RelationshipEndExpression> relationshipEnds;

  protected BuiltinUniversalType(String persistentName) {
    this.persistentName = persistentName;
    this.properties = Lists.newLinkedList();
    this.relationshipEnds = Lists.newLinkedList();
  }

  protected void add(PropertyExpression<?> pe) {
    this.properties.add(pe);
  }

  protected void add(RelationshipEndExpression re) {
    this.relationshipEnds.add(re);
  }

  /**{@inheritDoc}**/
  public final List<PropertyExpression<?>> getProperties() {
    return Collections.unmodifiableList(this.properties);
  }

  /**{@inheritDoc}**/
  public final List<RelationshipEndExpression> getRelationshipEnds() {
    return Collections.unmodifiableList(this.relationshipEnds);
  }

  /**{@inheritDoc}**/
  public final String getPersistentName() {
    return this.persistentName;
  }

  /**{@inheritDoc}**/
  public final PropertyExpression<?> findPropertyByName(String name) {
    for (PropertyExpression<?> pe : this.properties) {
      if (pe.getPersistentName().equals(name)) {
        return pe;
      }
    }
    return null;
  }

  /**{@inheritDoc}**/
  public final RelationshipEndExpression findRelationshipEndByName(String name) {
    for (RelationshipEndExpression re : this.relationshipEnds) {
      if (re.getPersistentName().equals(name)) {
        return re;
      }
    }
    return null;
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

  /**{@inheritDoc}**/
  public final int compareTo(UniversalTypeExpression o) {
    return CompareUtil.compareTo(this, o);
  }

  /**{@inheritDoc}**/
  public PropertyExpression<?> findPropertyByPersistentName(String pName) {
    return findPropertyByName(pName);
  }

  /**{@inheritDoc}**/
  public RelationshipEndExpression findRelationshipEndByPersistentName(String pName) {
    return findRelationshipEndByName(pName);
  }

  /**{@inheritDoc}**/
  public FeatureExpression<?> findFeatureByName(String name) {
    RelationshipEndExpression relEnd = findRelationshipEndByName(name);
    if (!(relEnd == null)) {
      return relEnd;
    }
    return findPropertyByName(name);
  }

  /**{@inheritDoc}**/
  public FeatureExpression<?> findFeatureByPersistentName(String pName) {
    return findFeatureByName(pName);
  }

  /**{@inheritDoc}**/
  public List<FeatureExpression<?>> getFeatures() {
    List<FeatureExpression<?>> features = new ArrayList<FeatureExpression<?>>();
    features.addAll(this.relationshipEnds);
    features.addAll(this.properties);
    return features;
  }
}

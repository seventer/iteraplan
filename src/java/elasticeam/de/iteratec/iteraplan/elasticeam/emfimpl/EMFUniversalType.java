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

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.util.ElasticeamContextUtil;
import de.iteratec.iteraplan.elasticeam.util.I18nMap;


/**
 *
 */
public abstract class EMFUniversalType extends EMFNamedElement<EClass> implements UniversalTypeExpression, Observer {

  private final I18nMap<FeatureExpression<?>> features;

  protected EMFUniversalType(EClass wrapped, EMFMetamodel metamodel) {
    super(wrapped, metamodel);
    this.features = I18nMap.create();
  }

  protected final void removeFeature(FeatureExpression<?> feature) {
    features.remove(feature);
  }

  /**{@inheritDoc}**/
  public final List<PropertyExpression<?>> getProperties() {
    List<PropertyExpression<?>> result = Lists.newArrayList();
    for (EStructuralFeature eFeature : getWrapped().getEStructuralFeatures()) {
      if (eFeature instanceof EAttribute) {
        PropertyExpression<?> property = getMetamodelImpl().encapsulate((EAttribute) eFeature);
        if (property != null) {
          result.add(property);
        }
      }
    }
    return result;
  }

  /**{@inheritDoc}**/
  public final List<PropertyExpression<?>> getProperties(ElasticeamContext ctx) {
    return getProperties();
  }

  /**{@inheritDoc}**/
  public final PropertyExpression<?> findPropertyByName(String name) {
    FeatureExpression<?> candidate = findFeatureByName(name);
    if (candidate instanceof PropertyExpression) {
      return (PropertyExpression<?>) candidate;
    }
    return null;
  }

  /**{@inheritDoc}**/
  public final PropertyExpression<?> findPropertyByName(ElasticeamContext ctx, String name) {
    return findPropertyByName(name);
  }

  /**{@inheritDoc}**/
  public final List<RelationshipEndExpression> getRelationshipEnds() {
    List<RelationshipEndExpression> result = Lists.newArrayList();
    for (EStructuralFeature eFeature : getWrapped().getEStructuralFeatures()) {
      if (eFeature instanceof EReference) {
        RelationshipEndExpression relEnd = getMetamodelImpl().encapsulate((EReference) eFeature);
        if (relEnd != null) {
          result.add(relEnd);
        }
      }
    }
    return result;
  }

  /**{@inheritDoc}**/
  public final List<RelationshipEndExpression> getRelationshipEnds(ElasticeamContext ctx) {
    return getRelationshipEnds();
  }

  /**{@inheritDoc}**/
  public final RelationshipEndExpression findRelationshipEndByName(String name) {
    FeatureExpression<?> candidate = findFeatureByName(name);
    if (candidate instanceof RelationshipEndExpression) {
      return (RelationshipEndExpression) candidate;
    }
    return null;
  }

  /**{@inheritDoc}**/
  public final RelationshipEndExpression findRelationshipEndByName(ElasticeamContext ctx, String name) {
    return findRelationshipEndByName(name);
  }

  /**{@inheritDoc}**/
  public PropertyExpression<?> findPropertyByPersistentName(String persistentName) {
    FeatureExpression<?> feature = findFeatureByPersistentName(persistentName);
    if (PropertyExpression.class.isInstance(feature)) {
      return (PropertyExpression<?>) feature;
    }
    return null;
  }

  /**{@inheritDoc}**/
  public RelationshipEndExpression findRelationshipEndByPersistentName(String persistentName) {
    FeatureExpression<?> feature = findFeatureByPersistentName(persistentName);
    if (RelationshipEndExpression.class.isInstance(feature)) {
      return (RelationshipEndExpression) feature;
    }
    return null;
  }

  /**{@inheritDoc}**/
  public FeatureExpression<?> findFeatureByName(String name) {
    return getMetamodelImpl().filterFeature(this.features.get(ElasticeamContextUtil.getCurrentContext().getLocale(), name));
  }

  /**{@inheritDoc}**/
  public FeatureExpression<?> findFeatureByPersistentName(String persistentName) {
    EStructuralFeature structuralFeature = getWrapped().getEStructuralFeature(persistentName);
    if (structuralFeature == null) {
      return null;
    }
    return getMetamodelImpl().encapsulate(structuralFeature);
  }

  /**{@inheritDoc}**/
  public List<FeatureExpression<?>> getFeatures() {
    List<FeatureExpression<?>> result = Lists.newArrayList();
    for (EStructuralFeature eFeature : getWrapped().getEStructuralFeatures()) {
      FeatureExpression<?> feature = getMetamodelImpl().encapsulate(eFeature);
      if (feature != null) {
        result.add(feature);
      }
    }
    return result;
  }

  /**{@inheritDoc}**/
  public final void update(Observable o, Object arg) {
    if (o instanceof FeatureExpression && arg instanceof NameChangeEvent) {
      this.features.set(((NameChangeEvent) arg).getLocale(), ((NameChangeEvent) arg).getName(), (FeatureExpression<?>) o);
    }
  }
}

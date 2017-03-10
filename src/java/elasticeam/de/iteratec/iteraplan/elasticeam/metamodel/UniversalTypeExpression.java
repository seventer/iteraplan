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
package de.iteratec.iteraplan.elasticeam.metamodel;

import java.util.List;
import java.util.Observer;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveProperty;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;


/**
 * A {@link TypeExpression} with a predefined ID property and the ability to have other properties
 * associated with it.
 */
public interface UniversalTypeExpression extends TypeExpression, Observer, Comparable<UniversalTypeExpression> {

  /**
   * The built-in ID property available for all universal types.
   */
  PrimitivePropertyExpression ID_PROPERTY = new BuiltinPrimitiveProperty("id", 1, 1, BuiltinPrimitiveType.INTEGER, null);

  /**
   * Retrieves the list of all properties associated with this universal type.
   * @return
   *    A list of property expressions. May be empty, but not <b>null</b>.
   */
  List<PropertyExpression<?>> getProperties();

  /**
   * Retrieves a property by its name in the current locale.
   * 
   * @param name
   *    The localized property name.
   * @return
   *    The corresponding property expression, or <b>null</b> if it can not be found.
   */
  PropertyExpression<?> findPropertyByName(String name);

  /**
   * Retrieves the list of relationship ends associated with this universal type.
   * 
   * @return
   *    A list of relationship ends. May be empty, but not <b>null</b>.
   */
  List<RelationshipEndExpression> getRelationshipEnds();

  /**
   * Retrieves a relationship end by its name in the current locale.
   * 
   * @param name
   *    The localized name of the relationship end.
   * @return
   *    The corresponding relationship end, or <b>null</b> if it can not be found.
   */
  RelationshipEndExpression findRelationshipEndByName(String name);

  /**
   * Retrieves a property associated with this universal type by its persistent name.
   * 
   * @param persistentName
   *    The persistent name of the property.
   * @return
   *    The corresponding property expression, or <b>null</b> if it can not be found.
   */
  PropertyExpression<?> findPropertyByPersistentName(String persistentName);

  /**
   * Retrieves an associated relationship end by its persistent name.
   * 
   * @param persistentName
   *    The persistent name of the relationship end.
   * @return
   *    The corresponding relationship end expression, or <b>null</b> if it can not be found.
   */
  RelationshipEndExpression findRelationshipEndByPersistentName(String persistentName);

  /**
   * Retrieves all features (relationship ends and properties) associated with this universal type.
   * 
   * @return
   *    The list of all associated features. May be empty, by not <b>null</b>.
   */
  List<FeatureExpression<?>> getFeatures();

  /**
   * Retrieves a feature (a relationship end or a property) by its name in the current locale.
   * 
   * @param name
   *    The localized name of the feature.
   * @return
   *    The corresponding feature expression, or <b>null</b> if it can not be found.
   */
  FeatureExpression<?> findFeatureByName(String name);

  /**
   * Retrieves a feature (a relationship end or a property) by its persistent name.
   * 
   * @param persistentName
   *    The persistent name of the feature.
   * @return
   *    The corresponding feature expression, or <b>null</b> if it can not be found.
   */
  FeatureExpression<?> findFeatureByPersistentName(String persistentName);

  /**
   * @param ctx
   * @return Method is deprecated. Use corresponding method which has no {@link ElasticeamContext} argument instead.
   */
  @Deprecated
  List<PropertyExpression<?>> getProperties(ElasticeamContext ctx);

  /**
   * @param ctx
   * @param name
   * @return Method is deprecated. Use corresponding method which has no {@link ElasticeamContext} argument instead.
   */
  @Deprecated
  PropertyExpression<?> findPropertyByName(ElasticeamContext ctx, String name);

  /**
   * @param ctx
   * @return Method is deprecated. Use corresponding method which has no {@link ElasticeamContext} argument instead.
   */
  @Deprecated
  List<RelationshipEndExpression> getRelationshipEnds(ElasticeamContext ctx);

  /**
   * @param ctx
   * @param name
   * @return Method is deprecated. Use corresponding method which has no {@link ElasticeamContext} argument instead.
   */
  @Deprecated
  RelationshipEndExpression findRelationshipEndByName(ElasticeamContext ctx, String name);
}

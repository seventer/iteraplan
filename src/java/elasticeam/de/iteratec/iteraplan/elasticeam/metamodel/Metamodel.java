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

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;


/**
 * Represents a metamodel consisting of different substantial types, relationship types, enumerations and primitive data types.
 */
public interface Metamodel {

  /**
   * @return 
   *    The name of the metamodel instance
   */
  String getName();

  /**
   * Retrieves all types in the metamodel. These include all substantial and relationship types as well as
   * all data types - primitive types and enumeration types.
   * 
   * @return 
   *    The list of all accessible substantial types and enumerations. May be empty, if no substantial types and enumerations exist or if none is accessible.
   */
  List<TypeExpression> getTypes();

  /**
   * Retrieves all substantial types accessible to the current user.
   * 
   * @return
   *    The list of all accessible substantial types. May be empty, if no substantial types exist or if none is accessible.
   */
  List<SubstantialTypeExpression> getSubstantialTypes();

  /**
   * Retrieves all relationship types accessible to the current user.
   * 
   * @return
   *    The list of all accessible relationship types. May be empty, if no relationship types exist or if none is accessible.
   */
  List<RelationshipTypeExpression> getRelationshipTypes();

  /**
   * Retrieves all enumerations accessible to the current user.
   * 
   * @return 
   *    The list of all accessible enumerations. May be empty, if no enumerations exist or if none is accessible.
   */
  List<EnumerationExpression> getEnumerationTypes();

  /**
   * Retrieves all primitive types from the metamodel.
   * 
   * @return 
   *    The list of all accessible primitive data types.
   */
  List<PrimitiveTypeExpression> getPrimitiveTypes();

  /**
   * Retrieves all data types (primitive types and enumeration types) from the metamodel.
   *
   * @return 
   *    The list of all accessible data types.
   */
  List<DataTypeExpression> getDataTypes();

  /**
   * Retrieves all universal types (substantial types and relationship types) from the metamodel.
   * 
   * @return 
   *    The list of all accessible universal types.
   */
  List<UniversalTypeExpression> getUniversalTypes();

  /**
   * Retrieves all relationships of the metamodel which are visible for the current user.
   * 
   * @return
   *    The list of all accessible relationships. May be empty, if no relationships exist or if none is accessible.
   */
  List<RelationshipExpression> getRelationships();

  /**
   * Retrieves a type expression (universal type or data type) by its name in the current locale.
   * 
   * @param name
   *    The localized name of the type expression.
   * @return
   *    The corresponding type expression, or <b>null</b> if it can not be found in the metamodel.
   */
  TypeExpression findTypeByName(String name);

  /**
   * Retrieves a type expression (universal type or data type) by its persistent name.
   * 
   * @param persistentName
   *    The persistent name of the corresponding type. For primitive data types, the persistent
   *    name is the fully qualified name of the represented class.
   * @return
   *    The corresponding type expression, or <b>null</b> if it can not be found in the metamodel.
   */
  TypeExpression findTypeByPersistentName(String persistentName);

  /**
   * Retrieves a primitive data type or an enumeration type by its name in the locale.
   * @param name
   *    The localized name of the data type.
   * @return
   *    The corresponding data type, or <b>null</b> if it can not be found in the metamodel.
   */
  DataTypeExpression findDataTypeByName(String name);

  /**
   * Retrieves a primitive data type of an enumeration type by its persistent name.
   * For primitive types, the persistent name is the fully qualified class name of the
   * represented class.
   * 
   * @param persistentName
   *    The persistent name of the data type.
   * @return
   *    The corresponding data type, or <b>null</b> if it can not be found in the metamodel.
   */
  DataTypeExpression findDataTypeByPersistentName(String persistentName);

  /**
   * Retrieves a universal type (substantial type or relationship type) by its name in the current locale.
   * 
   * @param name
   *    The localized name of the universal type.
   * @return
   *    The corresponding universal type, or <b>null</b> if it can not be found in the metamodel.
   */
  UniversalTypeExpression findUniversalTypeByName(String name);

  /**
   * Retrieves a universal type (sunstantial type or relationship type) by its persistent name.
   * 
   * @param persistentName
   *    The persistent name of the universal type.
   * @return
   *    The corresponding universal type, or <b>null</b> if it can not be found in the metamodel.
   */
  UniversalTypeExpression findUniversalTypeByPersistentName(String persistentName);

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * Retrieves all types in the metamodel. These include all substantial and relationship types as well as
   * all data types - primitive types and enumeration types.
   * @param ctx the context specifying access rights
   * @return the list of all accessible substantial types and enumerations. may be empty, if no substantial types and enumerations exist or if none is accessible.
   */
  @Deprecated
  List<TypeExpression> getTypes(ElasticeamContext ctx);

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * Retrieves all substantial types accessible in the given context.
   * @param ctx the context specifying access rights
   * @return the list of all accessible substantial types. may be empty, if no substantial types exist or if none is accessible.
   */
  @Deprecated
  List<SubstantialTypeExpression> getSubstantialTypes(ElasticeamContext ctx);

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * Retrieves all relationship types accessible in the given context.
   * @param ctx the context specifying access rights
   * @return the list of all accessible relationship types. may be empty, if no relationship types exist or if none is accessible.
   */
  @Deprecated
  List<RelationshipTypeExpression> getRelationshipTypes(ElasticeamContext ctx);

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * Retrieves all enumerations accessible in the given context.
   * @param ctx the context specifying access rights
   * @return the list of all accessible enumerations. may be empty, if no enumerations exist or if none is accessible.
   */
  @Deprecated
  List<EnumerationExpression> getEnumerationTypes(ElasticeamContext ctx);

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * Retrieves all primitive types from the metamodel.
   * @param ctx the context specifying access rights
   * @return the list of all accessible primitive data types.
   */
  @Deprecated
  List<PrimitiveTypeExpression> getPrimitiveTypes(ElasticeamContext ctx);

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * Retrieves all data types (primitive types and enumeration types) from the metamodel.
   * @param ctx the context specifying access rights
   * @return the list of all accessible data types.
   */
  @Deprecated
  List<DataTypeExpression> getDataTypes(ElasticeamContext ctx);

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * Retrieves all universal types (substantial types and relationship types) from the metamodel.
   * @param ctx the context specifying access rights
   * @return the list of all accessible universal types.
   */
  @Deprecated
  List<UniversalTypeExpression> getUniversalTypes(ElasticeamContext ctx);

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * Retrieves a substantial type or an enumeration by name, but <b>not</b> a
   * relationship type. This is because relationship types are not constrained
   * to have a unique name and the method makes no sense for them. For the
   * relationship types, use the corresponding method.
   * 
   * @param ctx
   * @param name
   * @return The substantial type or enumeration.
   */
  @Deprecated
  TypeExpression findTypeByName(ElasticeamContext ctx, String name);

  /**
   * Method is deprecated. Use the method with the same name, which takes no {@link ElasticeamContext} as an argument.
   * Retrieves all relationships accessible in the given context.
   * @param ctx the context specifying access rights
   * @return the list of all accessible relationships. may be empty, if no relationships exist or if none is accessible.
   */
  @Deprecated
  List<RelationshipExpression> getRelationships(ElasticeamContext ctx);
}

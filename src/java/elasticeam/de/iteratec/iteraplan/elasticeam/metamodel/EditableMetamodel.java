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

import java.awt.Color;
import java.util.Set;

import de.iteratec.iteraplan.model.user.Role;


/**
 * Interface representing a metamodel that can be edited, i.e. to which new substantial types, relationship types,
 * enumerations, enumeration literals, or properties can be added, can be deleted.
 */
public interface EditableMetamodel extends Metamodel {

  /**
   * Creates a new enumeration with the given persistent name.
   * @param persistentName the enumeration's persistent name
   * @return the newly created enumeration
   */
  EnumerationExpression createEnumeration(String persistentName);

  /**
   * Deletes an enumeration and all related enumeration properties.
   * @param enumeration the enumeration to delete
   */
  void deleteEnumeration(EnumerationExpression enumeration);

  /**
  * Creates a new enumeration literal with the given persistent name owned by the given enumeration.
   * @param enumeration the owning enumeration
   * @param persistentName the literal's persistent name
   * @param defaultLiteralColor the default color for this literal
   * @return the newly created enumeration literal
   */
  EnumerationLiteralExpression createEnumerationLiteral(EnumerationExpression enumeration, String persistentName, Color defaultLiteralColor);

  /**
   * Deletes an enumeration literal.
   * @param literal the enumeration literal to delete
   */
  void deleteEnumerationLiteral(EnumerationLiteralExpression literal);

  /**
   * Creates a new substantial type with the given persistent name.
   * @param persistentName the substantial type's persistent name
   * @return the newly created substantial type
   */
  SubstantialTypeExpression createSubstantialType(String persistentName);

  /**
   * Deletes a substantial type, all owned properties, and all targeting relationship ends.
   * @param substantialType the substantial type to delete
   */
  void deleteSubstantialType(SubstantialTypeExpression substantialType);

  /**
   * Adds a mixin to the supplied substantial type, which adopts all mixin properties and relationships.
   * @param substantialType the substantial type to be extended
   * @param mixin the mixin for the extension
   */
  void addMixin(SubstantialTypeExpression substantialType, MixinTypeExpression mixin);

  /**
   * Removes a mixin to the supplied substantial type, which looses all mixin properties and relationships.
   * @param substantialType the substantial type to be narrowed
   * @param mixin the mixin to be removed
   */
  void removeMixin(SubstantialTypeExpression substantialType, MixinTypeExpression mixin);

  /**
   * Creates a new relationship type with the given persistent name.
   * @param persistentName the relationship type's persistent name
   * @return the newly created relationship type
   */
  RelationshipTypeExpression createRelationshipType(String persistentName);

  /**
   * Deletes a relationship type, all owned properties, and all originating relationship ends.
   * @param relationshipType the relationship type to delete
   */
  void deleteRelationshipType(RelationshipTypeExpression relationshipType);

  /**
   * Creates a new primitive property with the given persistent name and given properties.
   * @param owner the owning universal type
   * @param persistentName the property's persistent name
   * @param lowerBound the lower bound of cardinality of property's values
   * @param upperBound the upper bound of cardinality of property's values
   * @param type the primitive type of the property
   * @return the newly created primitive property
   */
  PrimitivePropertyExpression createProperty(UniversalTypeExpression owner, String persistentName, int lowerBound, int upperBound,
                                             PrimitiveTypeExpression type);

  /**
   * Creates a new enumeration property with the given persistent name and given properties.
   * @param owner the owning universal type
   * @param persistentName the property's persistent name
   * @param lowerBound the lower bound of cardinality of property's values
   * @param upperBound the upper bound of cardinality of property's values
   * @param type the enumeration of the property
   * @return the newly created enumeration property
   */
  EnumerationPropertyExpression createProperty(UniversalTypeExpression owner, String persistentName, int lowerBound, int upperBound,
                                               EnumerationExpression type);

  /**
   * Creates a new property with the given persistent name and given features.
   * @param owner the owning universal type
   * @param persistentName the property's persistent name
   * @param lowerBound the lower bound of cardinality of property's values
   * @param upperBound the upper bound of cardinality of property's values
   * @param type the (enumeration or primitive) type of the property
   * @return the newly created enumeration property
   */
  PropertyExpression<?> createProperty(UniversalTypeExpression owner, String persistentName, int lowerBound, int upperBound, DataTypeExpression type);

  /**
   * Creates a mixed OR property expressio to be used by the VBBs in template metamodels.
   * <b>Not to be used in canonic metamodels!</b>
   * @param owner
   *    The owner universal type of the property.
   * @param persistentName
   *    The persistent name of the property.
   * @param lowerBound
   *    The lower bound of the cardinality of the property values.
   * @param upperBound
   *    The upper bound of the cardinality of the property values.
   * @param admissibleDataTypes
   *    The set of admissible primitive types for the property. Should not be empty. The first admissible
   *    primitive type us returned by property.getType().
   * @param matchEnumerations
   *    Whether enumeration expressions should also be matched by the property.
   * @return
   *    The new mixed OR property expression.
   */
  MixedOrPropertyExpression createMixedOrProperty(UniversalTypeExpression owner, String persistentName, int lowerBound, int upperBound,
                                                  Set<PrimitiveTypeExpression> admissibleDataTypes, boolean matchEnumerations);

  /**
   * Deletes a property.
   * @param property the property to delete
   */
  void deleteProperty(PropertyExpression<?> property);

  /**
   * Creates a relationship which associates two {@link UniversalTypeExpression}s,
   * as long as at least one is a {@link SubstantialTypeExpression}, i.e. no
   * associations between two {@link RelationshipTypeExpression}s are allowed.
   * 
   * @param persistentName 
   *    The persistent name to use for the relationship.
   * @param end0
   *    The first {@link UniversalTypeExpression} to associate.
   * @param end0Name
   *    The persistent name for the association ({@link RelationshipEndExpression}) from the first to the second {@link UniversalTypeExpression}.
   * @param end0lower
   *    The minimal number of associations in the direction from first to second.
   * @param end0upper
   *    The maximal number of associations in the direction from first to second.
   * @param end1
   *    The second {@link UniversalTypeExpression} to associate.
   * @param end1Name
   *    The persistent name for the association ({@link RelationshipEndExpression}) from the second to the first {@link UniversalTypeExpression}.
   * @param end1lower
   *    The minimal number of associations in the direction from second to first.
   * @param end1upper
   *    The maximal number of associations in the direction from second to first.
   * @return
   *    The created relationship expression instance.
   */
  RelationshipExpression createRelationship(String persistentName, UniversalTypeExpression end0, String end0Name, int end0lower, int end0upper,
                                            UniversalTypeExpression end1, String end1Name, int end1lower, int end1upper);

  /**
   * Creates a self-referencing relationship, which optionally can be set to be cyclic.
   * 
   * @param persistentName
   *    The persistent name to use for the relationship.
   * @param type
   *    The {@link UniversalTypeExpression} which associates with this relationship.
   * @param end0Name
   *    The persistent name of the first relationship end.
   * @param end0lower
   *    The lower bound of the first relationship end.
   * @param end0upper
   *    The upper bound of the first relationship end.
   * @param end1Name
   *    The persistent name of the second relationship end.
   * @param end1lower
   *    The lower bound of the second relationship end.
   * @param end1Upper
   *    The upper bound of the second relationship end.
   * @param acycic
   *    Whether the relationship is acyclic or not. A cyclic relationship would, for example, allow for entity A to be
   *    a parent of entity B and for entity B to be a parent of entity A simultaneously. Default is true. 
   * @return
   *    The created relationship expression instance.
   */
  RelationshipExpression createRelationship(String persistentName, UniversalTypeExpression type, String end0Name, int end0lower, int end0upper,
                                            String end1Name, int end1lower, int end1Upper, boolean acycic);

  /**
   * Deletes a given relationship from the metamodel.
   * @param relationship
   *    The relationship to delete.
   */
  void deleteRelationship(RelationshipExpression relationship);

  /**
   * Grants a permission on a given universal type.
   * 
   * @param on
   *    The universal type regarded by the permission.
   * @param to
   *    The role to which the permission is granted.
   * @param type
   *    The type of permission.
   */
  void grantPermission(UniversalTypeExpression on, Role to, UniversalTypePermissions type);

  /**
   * Grants a permission to a given property expression.
   * 
   * @param on
   *    The property expression regarded by the permission.
   * @param to
   *    The role to which the permission is granted.
   * @param type
   *    The type of permission.
   */
  void grantPermission(PropertyExpression<?> on, Role to, FeaturePermissions type);
}

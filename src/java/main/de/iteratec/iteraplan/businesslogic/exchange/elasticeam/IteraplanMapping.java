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
package de.iteratec.iteraplan.businesslogic.exchange.elasticeam;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.criteria.ParameterExpression;

import org.eclipse.emf.ecore.EEnum;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.MetamodelExport.TypeOfExchange;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.elasticeam.emfimpl.EMFMetamodel;
import de.iteratec.iteraplan.elasticeam.exception.MetamodelException;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitiveTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.TypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedProperty;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;


/**
 * Abstract class, representing the mapping information of a {@link MetamodelExport}.
 * Holds information about the iteraplan sources ({@link HbMappedClass}es, {@link HbMappedProperty}s, {@link AttributeType}s and {@link EnumAV}s) for all 
 *  {@link SubstantialTypeExpression}s
 *  {@link EnumerationExpression}s
 *  {@link PropertyExpression}s
 *  {@link RelationshipTypeExpression}s
 */
public abstract class IteraplanMapping {

  public static final Set<String>                                 DEFAULT_ATTRIBUTES               = initDefaultAttributeNames();

  private EMFMetamodel                                            loadedMetamodel;
  private TypeOfExchange                                          exchangeType;
  private Map<SubstantialTypeExpression, HbMappedClass>           substantialTypes                 = Maps.newHashMap();
  private Map<EnumerationExpression, Class<? extends Enum<?>>>    enumerationExpressions           = Maps.newHashMap();
  private Map<EnumerationLiteralExpression, Enum<?>>              enumerationLiterals              = Maps.newHashMap();
  private Map<TypeExpression, Map<PropertyExpression<?>, Method>> builtInProperties                = Maps.newHashMap();
  private Map<RelationshipTypeExpression, HbMappedClass>          associationClasses               = Maps.newHashMap();
  private Map<PropertyExpression<?>, AttributeType>               additionalProperties             = Maps.newHashMap();
  private Map<EnumerationExpression, EnumAT>                      additionalEnumerationExpressions = Maps.newHashMap();
  private Map<EnumerationLiteralExpression, EnumAV>               additionalEnumerationLiterals    = Maps.newHashMap();

  /**
   * 
   * Default constructor.
   * @param toe
   * @param metamodel2BeFilled
   */
  public IteraplanMapping(TypeOfExchange toe, EMFMetamodel metamodel2BeFilled) {
    this.exchangeType = Preconditions.checkNotNull(toe);
    this.loadedMetamodel = Preconditions.checkNotNull(metamodel2BeFilled);
  }

  public IteraplanMapping(IteraplanMapping loadedSource) {
    this.exchangeType = loadedSource.exchangeType;
    this.loadedMetamodel = loadedSource.loadedMetamodel;
    this.substantialTypes = loadedSource.substantialTypes;
    this.enumerationExpressions = loadedSource.enumerationExpressions;
    this.enumerationLiterals = loadedSource.enumerationLiterals;
    this.builtInProperties = loadedSource.builtInProperties;
    this.associationClasses = loadedSource.associationClasses;
    this.additionalProperties = loadedSource.additionalProperties;
    this.additionalEnumerationExpressions = loadedSource.additionalEnumerationExpressions;
    this.additionalEnumerationLiterals = loadedSource.additionalEnumerationLiterals;
  }

  /**
   * Public getter for the resulting {@link Metamodel}
   * 
   * @return the resulting {@link Metamodel}
   */
  public Metamodel getMetamodel() {
    return loadedMetamodel;
  }

  /**
   * Public getter for {@link #enumerationExpressions}
   * 
   * @return {@link #enumerationExpressions}
   */
  public Map<EnumerationExpression, Class<? extends Enum<?>>> getEnumerationExpressions() {
    return enumerationExpressions;
  }

  /**
   * Public getter for {@link #substantialTypes}
   * 
   * @return {@link #substantialTypes}
   */
  public Map<SubstantialTypeExpression, HbMappedClass> getSubstantialTypes() {
    return substantialTypes;
  }

  /**
   * Public getter for {@link #associationClasses}
   * 
   * @return {@link #associationClasses}
   */
  public Map<RelationshipTypeExpression, HbMappedClass> getRelationshipTypes() {
    return associationClasses;
  }

  /**
   * Public getter for {@link #enumerationLiterals}
   * 
   * @return {@link #enumerationLiterals}
   */
  public Map<EnumerationLiteralExpression, Enum<?>> getEnumerationLiterals() {
    return enumerationLiterals;
  }

  /**
   * Public getter for {@link #builtInProperties}
   * 
   * @return {@link #builtInProperties}
   */
  public Map<TypeExpression, Map<PropertyExpression<?>, Method>> getBuiltInProperties() {
    return builtInProperties;
  }

  /**
   * Public getter for {@link #additionalProperties}
   * 
   * @return {@link #additionalProperties}
   */
  public Map<PropertyExpression<?>, AttributeType> getAdditionalPropertyExpressions() {
    return additionalProperties;
  }

  public boolean isDerivedFromAT(PropertyExpression<?> property) {
    return additionalProperties.containsKey(property);
  }

  /**
   * Public getter for {@link #additionalEnumerationExpressions}
   * 
   * @return {@link #additionalEnumerationExpressions}
   */
  public Map<EnumerationExpression, EnumAT> getAdditionalEnumerationExpressions() {
    return additionalEnumerationExpressions;
  }

  /**
   * Public getter for {@link #additionalEnumerationLiterals}
   * 
   * @return {@link #additionalEnumerationLiterals}
   */
  public Map<EnumerationLiteralExpression, EnumAV> getAdditionalEnumerationLiterals() {
    return additionalEnumerationLiterals;
  }

  /**
   * Getter for the {@link Metamodel}'s {@link EMFMetamodel}
   * 
   * @return {@link #loadedMetamodel}
   */
  protected EMFMetamodel getEMFMetamodel() {
    return loadedMetamodel;
  }

  /**
   * Getter for {@link #exchangeType}
   * 
   * @return {@link #exchangeType}
   */
  protected final TypeOfExchange getExchangeType() {
    return exchangeType;
  }

  /**
   * Collect mapping information for a created {@link SubstantialTypeExpression} 
   * and its corresponding {@link HbMappedClass} (=source)
   * 
   * @param ste the created {@link SubstantialTypeExpression}
   * @param source the {@link HbMappedClass} which has been used to create the {@link SubstantialTypeExpression}
   */
  protected final void add(SubstantialTypeExpression ste, HbMappedClass source) {
    if (source == null || ste == null) {
      throw new MetamodelException(MetamodelException.UNSUPPORTED_OPERATION, "Cannot map NULL");
    }
    substantialTypes.put(ste, source);
  }

  /**
   * Collect mapping information for a created {@link EnumerationExpression}
   * and its corresponding {@link Enum} (=source)
   *  
   * @param ee the created {@link EnumerationExpression}
   * @param enumm the {@link Enum} which has been used to create the {@link EnumerationExpression}
   */
  protected final void add(EnumerationExpression ee, Class<? extends Enum<?>> enumm) {
    if (ee == null || enumm == null) {
      throw new MetamodelException(MetamodelException.UNSUPPORTED_OPERATION, "Cannot map NULL");
    }
    enumerationExpressions.put(ee, enumm);
  }

  /**
   * Collect mapping information for a created {@link RelationshipTypeExpression}
   * and its corresponding {@link HbMappedClass} (=source, like BusinessMapping or subclasses of AbstractAssociation)
   * 
   * @param rte the created {@link RelationshipTypeExpression}
   * @param assocHbClass the {@link HbMappedClass} which has been used to create the {@link RelationshipTypeExpression}
   */
  protected final void add(RelationshipTypeExpression rte, HbMappedClass assocHbClass) {
    if (rte == null || assocHbClass == null) {
      throw new MetamodelException(MetamodelException.UNSUPPORTED_OPERATION, "Cannot map NULL");
    }
    associationClasses.put(rte, assocHbClass);
  }

  /**
   * Collect mapping information for a created {@link EnumerationExpression}
   * and its corresponding {@link EnumAT} (=source)
   * 
   * @param ee the created {@link EnumerationExpression}
   * @param enumAT the {@link EnumAT} which has been used to create the {@link EnumerationExpression}
   */
  protected final void add(EnumerationExpression ee, EnumAT enumAT) {
    if (ee == null || enumAT == null) {
      throw new MetamodelException(MetamodelException.UNSUPPORTED_OPERATION, "Cannot map NULL");
    }
    additionalEnumerationExpressions.put(ee, enumAT);
  }

  /**
   * Collect mapping information for a created {@link EnumerationLiteralExpression}
   * and its corresponding {@link Enum}-literal
   * 
   * @param ele the created {@link EnumerationLiteralExpression}
   * @param literal the {@link Enum} literal which has been used to create the {@link EnumerationLiteralExpression}
   */
  protected final void add(EnumerationLiteralExpression ele, Enum<?> literal) {
    if (ele == null || literal == null) {
      throw new MetamodelException(MetamodelException.UNSUPPORTED_OPERATION, "Cannot map NULL");
    }
    enumerationLiterals.put(ele, literal);
  }

  /**
   * Collect mapping information for a created {@link EnumerationLiteralExpression}
   * and its corresponding {@link EnumAV}
   * 
   * @param ele the created {@link EnumerationLiteralExpression}
   * @param enumAV the {@link EnumAV} which has been used to create the {@link EnumerationLiteralExpression}
   */
  protected final void add(EnumerationLiteralExpression ele, EnumAV enumAV) {
    if (ele == null || enumAV == null) {
      throw new MetamodelException(MetamodelException.UNSUPPORTED_OPERATION, "Cannot map NULL");
    }
    additionalEnumerationLiterals.put(ele, enumAV);
  }

  /**
   * Get an existing {@link SubstantialTypeExpression} for a {@link HbMappedClass}
   * if such a {@link SubstantialTypeExpression} already exists
   * 
   * @param hbClass the {@link HbMappedClass} to search for
   * @return an existing {@link SubstantialTypeExpression} or null, if no such {@link SubstantialTypeExpression} exists
   */
  protected final SubstantialTypeExpression getSubstantialTypeExpression(HbMappedClass hbClass) {
    if (hbClass == null) {
      return null;
    }
    for (Entry<SubstantialTypeExpression, HbMappedClass> e : substantialTypes.entrySet()) {
      if (hbClass == e.getValue()) {
        return e.getKey();
      }
    }
    return null;
  }
  
  /**
   * Get an existing {@link SubstantialTypeExpression} for a persistent name {@link String}
   * if such a {@link SubstantialTypeExpression} already exists
   * 
   * @param persistentName the {@link String} to search for
   * @return an existing {@link SubstantialTypeExpression} or null, if no such {@link SubstantialTypeExpression} exists
   */
  protected final SubstantialTypeExpression getSubstantialTypeExpression(String persistentName) {
    if (persistentName == null) {
      return null;
    }
    for (SubstantialTypeExpression ste : substantialTypes.keySet()) {
      if (persistentName.equals(ste.getPersistentName())) {
        return ste;
      }
    }
    return null;
  }

  /**
   * Get an existing {@link RelationshipTypeExpression} for a {@link HbMappedClass} representing a relationship
   * like BusinessMapping or subclasses of AbstractAssociation
   * 
   * @param assocHbClass the {@link HbMappedClass} representing the association class
   * @return an existing {@link RelationshipTypeExpression} or null, if no such {@link RelationshipTypeExpression} exists
   */
  protected final RelationshipTypeExpression getRelationshipTypeExpression(HbMappedClass assocHbClass) {
    if (assocHbClass == null) {
      return null;
    }
    for (Entry<RelationshipTypeExpression, HbMappedClass> e : associationClasses.entrySet()) {
      if (assocHbClass == e.getValue()) {
        return e.getKey();
      }
    }
    return null;
  }
  
  /**
   * Get an existing {@link RelationshipTypeExpression} for a persistentName String
   * like BusinessMapping or subclasses of AbstractAssociation
   * 
   * @param persistentName the name of the RelationshipTypeExpression
   * @return an existing {@link RelationshipTypeExpression} or null, if no such {@link RelationshipTypeExpression} exists
   */
  protected final RelationshipTypeExpression getRelationshipTypeExpression(String persistentName) {
    if (persistentName == null) {
      return null;
    }
    for (Entry<RelationshipTypeExpression, HbMappedClass> e : associationClasses.entrySet()) {
      if (persistentName.equals(e.getKey().getPersistentName())) {
        return e.getKey();
      }
    }
    return null;
  }

  /**
   * Get an existing {@link UniversalTypeExpression} for a {@link HbMappedClass}
   * @see #getRelationshipTypeExpression(HbMappedClass)
   * @see #getSubstantialTypeExpression(HbMappedClass)
   * @param hbClass the {@link HbMappedClass} to search for
   * @return an existing {@link UniversalTypeExpression} or null, if no such {@link UniversalTypeExpression} exists
   */
  protected final UniversalTypeExpression resolve(HbMappedClass hbClass) {
    UniversalTypeExpression result = getRelationshipTypeExpression(hbClass);
    if (result == null) {
      result = getSubstantialTypeExpression(hbClass);
    }
    return result;

  }

  /**
   * Collect mapping information for a created {@link PropertyExpression}
   * and its corresponding property's getter {@link Method}
   * 
   * @param propertyExpression the created {@link PropertyExpression}
   * @param method the corresponding getter {@link Method}
   */
  protected final void add(TypeExpression containingType, PropertyExpression<?> propertyExpression, Method method) {
    if (containingType == null || propertyExpression == null) {
      throw new ModelException(ModelException.GENERAL_ERROR, "Type and Property must not be null");
    }
    if (!this.builtInProperties.containsKey(containingType)) {
      Map<PropertyExpression<?>, Method> map = Maps.newHashMap();
      this.builtInProperties.put(containingType, map);
    }
    this.builtInProperties.get(containingType).put(propertyExpression, method);
  }

  /**
   * Collect mapping information for a created {@link PropertyExpression}
   * and its corresponding {@link AttributeType} (=source)
   * 
   * @param propertyExpression the created {@link PropertyExpression}
   * @param attributeType the {@link AttributeType} which has been used to create the {@link ParameterExpression}
   */
  protected final void add(PropertyExpression<?> propertyExpression, AttributeType attributeType) {
    this.additionalProperties.put(propertyExpression, attributeType);
  }

  /**
   * Get an existing {@link EnumerationExpression} for an {@link Enum}
   * 
   * @param enumm the {@link Enum} to search for
   * @return an existing {@link EnumerationExpression} or null, if no such {@link EnumerationExpression} exists
   */
  protected EnumerationExpression resolve(Class<? extends Enum<?>> enumm) {
    if (enumm == null) {
      return null;
    }
    for (Entry<EnumerationExpression, Class<? extends Enum<?>>> e : enumerationExpressions.entrySet()) {
      if (enumm == e.getValue()) {
        return e.getKey();
      }
    }
    return null;
  }

  protected PrimitiveTypeExpression resolvePT(Class<?> clazz) {
    if (clazz == null) {
      return null;
    }
    // prevent usage of primitive type boolean
    if (boolean.class.equals(clazz)) {
      return BuiltinPrimitiveType.BOOLEAN;
    }
    for (PrimitiveTypeExpression primitiveType : BuiltinPrimitiveType.BUILTIN_PRIMITIVE_TYPES) {
      if (primitiveType.getEncapsulatedType().equals(clazz)) {
        return primitiveType;
      }
    }
    return null;
  }

  /**
   * Get an existing {@link EnumerationExpression} for an {@link EnumAT}
   * 
   * @param enumAT the {@link EnumAT} to search for
   * @return an existing {@link EnumerationExpression} or null, if no such {@link EnumerationExpression} exists
   */
  protected EnumerationExpression resolve(EnumAT enumAT) {
    if (enumAT == null) {
      return null;
    }
    for (Entry<EnumerationExpression, EnumAT> e : additionalEnumerationExpressions.entrySet()) {
      if (enumAT == e.getValue()) {
        return e.getKey();
      }
    }
    return null;
  }

  /**
   * Get an existing {@link EnumerationLiteralExpression} for an {@link Enum} literal
   * 
   * @param literal the {@link Enum}-literal to search for
   * @return an existing {@link EnumerationLiteralExpression} or null, if no such {@link EnumerationLiteralExpression} exists
   */
  public EnumerationLiteralExpression resolve(Enum<?> literal) {
    if (literal == null) {
      return null;
    }
    for (Entry<EnumerationLiteralExpression, Enum<?>> e : enumerationLiterals.entrySet()) {
      if (literal == e.getValue()) {
        return e.getKey();
      }
    }
    return null;
  }

  public static boolean isDerivedFromEnumAT(EEnum eEnum) {
    return eEnum.getName().contains(EnumAT.class.getCanonicalName());
  }

  public EnumerationLiteralExpression resolve(PropertyExpression<?> pe, String literalValue) {
    if (pe instanceof EnumerationPropertyExpression) {
      for (EnumerationLiteralExpression ele : ((EnumerationPropertyExpression) pe).getType().getLiterals()) {
        if (ele.getPersistentName().equals(literalValue)) {
          return ele;
        }
      }
    }
    return null;
  }

  /**
   * Get an existing {@link EnumerationLiteralExpression} for an {@link EnumAV}
   * 
   * @param enumAV the {@link EnumAV} to search for
   * @return an existing {@link EnumerationLiteralExpression} or null, if no such {@link EnumerationLiteralExpression} exists 
   */
  public EnumerationLiteralExpression resolve(EnumAV enumAV) {
    if (enumAV == null) {
      return null;
    }
    for (Entry<EnumerationLiteralExpression, EnumAV> e : additionalEnumerationLiterals.entrySet()) {
      if (enumAV == e.getValue()) {
        return e.getKey();
      }
    }
    return null;
  }

  /**
   * Get the getter Method for a {@link PropertyExpression}
   * 
   * @param property the {@link PropertyExpression} to search for
   * @return the getter {@link Method} for the given {@link PropertyExpression} or null, if no such {@link PropertyExpression} exists
   */
  public Method resolveBuiltInProperty(TypeExpression containingType, PropertyExpression<?> property) {
    if (!this.builtInProperties.containsKey(containingType)) {
      return null;
    }
    return this.builtInProperties.get(containingType).get(property);
  }

  /**
   * Get the {@link AttributeType} for an "additional" (sourced from an {@link AttributeType}) {@link PropertyExpression}
   * 
   * @param property the {@link PropertyExpression} to search for
   * @return the {@link AttributeType} instance which is represented by the {@link PropertyExpression} or null, if no such {@link PropertyExpression} exists
   */
  public AttributeType resolveAdditionalProperty(PropertyExpression<?> property) {
    return this.additionalProperties.get(property);
  }

  /**
   * 
   * @return a {@link String}-{@link Set} containing the names of all standard properties that are 
   * contained in each {@link SubstantialTypeExpression} by default
   */
  public static final Set<String> initDefaultAttributeNames() {
    Set<String> set = Sets.newHashSet();
    set.add(SubstantialTypeExpression.ID_PROPERTY.getName());
    set.add(MixinTypeNamed.NAME_PROPERTY.getName());
    set.add(MixinTypeNamed.DESCRIPTION_PROPERTY.getName());
    return ImmutableSet.copyOf(set);
  }

  /**
   * static helper method to lowercase the first letter of a given {@link String}
   * 
   * @param s the {@link String} to be lowercased 
   * @return a lowercased version of the given {@link String}
   */
  public static String decapitalize(String s) {
    return (s.substring(0, 1).toLowerCase() + s.substring(1));
  }
}

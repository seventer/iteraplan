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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EEnum;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.elasticmi.exception.MetamodelException;
import de.iteratec.iteraplan.elasticmi.exception.ModelException;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.AtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WEnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMixinTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WRelationshipTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WSortalTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WUniversalTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WValueTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.impl.WMetamodelImpl;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;


/**
 * Abstract class, representing the mapping information of a {@link WMetamodelExport}.
 * Holds information about the iteraplan sources ({@link HbMappedClass}es, {@link HbMappedProperty}s, {@link AttributeType}s and {@link EnumAV}s) for all 
 *  {@link WSortalTypeExpression}s
 *  {@link WNominalEnumerationExpression}s
 *  {@link WPropertyExpression}s
 *  {@link WRelationshipTypeExpression}s
 */
public abstract class ElasticMiIteraplanMapping {

  public static final Set<String>                                      DEFAULT_ATTRIBUTES               = initDefaultAttributeNames();

  private WMetamodelImpl                                               loadedMetamodel;
  private Map<WSortalTypeExpression, HbMappedClass>                    sortalTypes                      = Maps.newHashMap();
  private Map<WNominalEnumerationExpression, Class<? extends Enum<?>>> enumerationExpressions           = Maps.newHashMap();
  private Map<WEnumerationLiteralExpression, Enum<?>>                  enumerationLiterals              = Maps.newHashMap();
  private Map<WTypeExpression, Map<WPropertyExpression, Method>>       builtInProperties                = Maps.newHashMap();
  private Map<WRelationshipTypeExpression, HbMappedClass>              associationClasses               = Maps.newHashMap();
  private Map<String, AttributeType>                                   additionalProperties             = Maps.newHashMap();
  private Map<WNominalEnumerationExpression, EnumAT>                   additionalEnumerationExpressions = Maps.newHashMap();
  private Map<WEnumerationLiteralExpression, EnumAV>                   additionalEnumerationLiterals    = Maps.newHashMap();

  /**
   * 
   * Default constructor.
   * @param metamodel2BeFilled
   */
  public ElasticMiIteraplanMapping(WMetamodelImpl metamodel2BeFilled) {
    this.loadedMetamodel = Preconditions.checkNotNull(metamodel2BeFilled);
  }

  /**
   * Public getter for the resulting {@link WMetamodel}
   * 
   * @return the resulting {@link WMetamodel}
   */
  public WMetamodel getMetamodel() {
    return loadedMetamodel;
  }

  /**
   * Public getter for {@link #enumerationExpressions}
   * 
   * @return {@link #enumerationExpressions}
   */
  public Map<WNominalEnumerationExpression, Class<? extends Enum<?>>> getEnumerationExpressions() {
    return enumerationExpressions;
  }

  /**
   * Public getter for {@link #sortalTypes}
   * 
   * @return {@link #sortalTypes}
   */
  public Map<WSortalTypeExpression, HbMappedClass> getSubstantialTypes() {
    return sortalTypes;
  }

  /**
   * Public getter for {@link #associationClasses}
   * 
   * @return {@link #associationClasses}
   */
  public Map<WRelationshipTypeExpression, HbMappedClass> getRelationshipTypes() {
    return associationClasses;
  }

  /**
   * Public getter for {@link #enumerationLiterals}
   * 
   * @return {@link #enumerationLiterals}
   */
  public Map<WEnumerationLiteralExpression, Enum<?>> getEnumerationLiterals() {
    return enumerationLiterals;
  }

  /**
   * Public getter for {@link #builtInProperties}
   * 
   * @return {@link #builtInProperties}
   */
  public Map<WTypeExpression, Map<WPropertyExpression, Method>> getBuiltInProperties() {
    return builtInProperties;
  }

  /**
   * Public getter for {@link #additionalProperties}
   * 
   * @return {@link #additionalProperties}
   */
  public Map<String, AttributeType> getAdditionalPropertyExpressions() {
    return additionalProperties;
  }

  public boolean isDerivedFromAT(WPropertyExpression property) {
    return additionalProperties.containsKey(property.getPersistentName());
  }

  /**
   * Public getter for {@link #additionalEnumerationExpressions}
   * 
   * @return {@link #additionalEnumerationExpressions}
   */
  public Map<WNominalEnumerationExpression, EnumAT> getAdditionalEnumerationExpressions() {
    return additionalEnumerationExpressions;
  }

  /**
   * Public getter for {@link #additionalEnumerationLiterals}
   * 
   * @return {@link #additionalEnumerationLiterals}
   */
  public Map<WEnumerationLiteralExpression, EnumAV> getAdditionalEnumerationLiterals() {
    return additionalEnumerationLiterals;
  }

  /**
   * Getter for the {@link WMetamodel}'s {@link WMetamodelImpl}
   * 
   * @return {@link #loadedMetamodel}
   */
  protected WMetamodelImpl getMetamodelImpl() {
    return loadedMetamodel;
  }

  /**
   * Collect mapping information for a created {@link WSortalTypeExpression} 
   * and its corresponding {@link HbMappedClass} (=source)
   * 
   * @param ste the created {@link WSortalTypeExpression}
   * @param source the {@link HbMappedClass} which has been used to create the {@link WSortalTypeExpression}
   */
  protected final void add(WSortalTypeExpression ste, HbMappedClass source) {
    if (source == null || ste == null) {
      throw new MetamodelException(MetamodelException.UNSUPPORTED_OPERATION, "Cannot map NULL");
    }
    sortalTypes.put(ste, source);
  }

  /**
   * Collect mapping information for a created {@link WNominalEnumerationExpression}
   * and its corresponding {@link Enum} (=source)
   *  
   * @param ee the created {@link WNominalEnumerationExpression}
   * @param enumm the {@link Enum} which has been used to create the {@link WNominalEnumerationExpression}
   */
  protected final void add(WNominalEnumerationExpression ee, Class<? extends Enum<?>> enumm) {
    if (ee == null || enumm == null) {
      throw new MetamodelException(MetamodelException.UNSUPPORTED_OPERATION, "Cannot map NULL");
    }
    enumerationExpressions.put(ee, enumm);
  }

  /**
   * Collect mapping information for a created {@link WRelationshipTypeExpression}
   * and its corresponding {@link HbMappedClass} (=source, like BusinessMapping or subclasses of AbstractAssociation)
   * 
   * @param rte the created {@link WRelationshipTypeExpression}
   * @param assocHbClass the {@link HbMappedClass} which has been used to create the {@link WRelationshipTypeExpression}
   */
  protected final void add(WRelationshipTypeExpression rte, HbMappedClass assocHbClass) {
    if (rte == null || assocHbClass == null) {
      throw new MetamodelException(MetamodelException.UNSUPPORTED_OPERATION, "Cannot map NULL");
    }
    associationClasses.put(rte, assocHbClass);
  }

  /**
   * Collect mapping information for a created {@link WNominalEnumerationExpression}
   * and its corresponding {@link EnumAT} (=source)
   * 
   * @param ee the created {@link WNominalEnumerationExpression}
   * @param enumAT the {@link EnumAT} which has been used to create the {@link WNominalEnumerationExpression}
   */
  protected final void add(WNominalEnumerationExpression ee, EnumAT enumAT) {
    if (ee == null || enumAT == null) {
      throw new MetamodelException(MetamodelException.UNSUPPORTED_OPERATION, "Cannot map NULL");
    }
    additionalEnumerationExpressions.put(ee, enumAT);
  }

  /**
   * Collect mapping information for a created {@link WEnumerationLiteralExpression}
   * and its corresponding {@link Enum}-literal
   * 
   * @param ele the created {@link WEnumerationLiteralExpression}
   * @param literal the {@link Enum} literal which has been used to create the {@link WEnumerationLiteralExpression}
   */
  protected final void add(WEnumerationLiteralExpression ele, Enum<?> literal) {
    if (ele == null || literal == null) {
      throw new MetamodelException(MetamodelException.UNSUPPORTED_OPERATION, "Cannot map NULL");
    }
    enumerationLiterals.put(ele, literal);
  }

  /**
   * Collect mapping information for a created {@link WEnumerationLiteralExpression}
   * and its corresponding {@link EnumAV}
   * 
   * @param ele the created {@link WEnumerationLiteralExpression}
   * @param enumAV the {@link EnumAV} which has been used to create the {@link WEnumerationLiteralExpression}
   */
  protected final void add(WEnumerationLiteralExpression ele, EnumAV enumAV) {
    if (ele == null || enumAV == null) {
      throw new MetamodelException(MetamodelException.UNSUPPORTED_OPERATION, "Cannot map NULL");
    }
    additionalEnumerationLiterals.put(ele, enumAV);
  }

  /**
   * Get an existing {@link WSortalTypeExpression} for a {@link HbMappedClass}
   * if such a {@link WSortalTypeExpression} already exists
   * 
   * @param hbClass the {@link HbMappedClass} to search for
   * @return an existing {@link WSortalTypeExpression} or null, if no such {@link WSortalTypeExpression} exists
   */
  protected final WSortalTypeExpression getSortalTypeExpression(HbMappedClass hbClass) {
    if (hbClass == null) {
      return null;
    }
    for (Entry<WSortalTypeExpression, HbMappedClass> e : sortalTypes.entrySet()) {
      if (hbClass == e.getValue()) {
        return e.getKey();
      }
    }
    return null;
  }

  /**
   * Get an existing {@link WSortalTypeExpression} for a persistent name {@link String}
   * if such a {@link WSortalTypeExpression} already exists
   * 
   * @param persistentName the {@link String} to search for
   * @return an existing {@link WSortalTypeExpression} or null, if no such {@link WSortalTypeExpression} exists
   */
  protected final WSortalTypeExpression getSortalTypeExpression(String persistentName) {
    if (persistentName == null) {
      return null;
    }
    for (WSortalTypeExpression ste : sortalTypes.keySet()) {
      if (persistentName.equals(ste.getPersistentName())) {
        return ste;
      }
    }
    return null;
  }

  /**
   * Get an existing {@link WRelationshipTypeExpression} for a {@link HbMappedClass} representing a relationship
   * like BusinessMapping or subclasses of AbstractAssociation
   * 
   * @param assocHbClass the {@link HbMappedClass} representing the association class
   * @return an existing {@link WRelationshipTypeExpression} or null, if no such {@link WRelationshipTypeExpression} exists
   */
  protected final WRelationshipTypeExpression getRelationshipTypeExpression(HbMappedClass assocHbClass) {
    if (assocHbClass == null) {
      return null;
    }
    for (Entry<WRelationshipTypeExpression, HbMappedClass> e : associationClasses.entrySet()) {
      if (assocHbClass == e.getValue()) {
        return e.getKey();
      }
    }
    return null;
  }

  /**
   * Get an existing {@link WRelationshipTypeExpression} for a persistentName String
   * like BusinessMapping or subclasses of AbstractAssociation
   * 
   * @param persistentName the name of the RelationshipTypeExpression
   * @return an existing {@link WRelationshipTypeExpression} or null, if no such {@link WRelationshipTypeExpression} exists
   */
  protected final WRelationshipTypeExpression getRelationshipTypeExpression(String persistentName) {
    if (persistentName == null) {
      return null;
    }
    for (Entry<WRelationshipTypeExpression, HbMappedClass> e : associationClasses.entrySet()) {
      if (persistentName.equals(e.getKey().getPersistentName())) {
        return e.getKey();
      }
    }
    return null;
  }

  /**
   * Get an existing {@link WUniversalTypeExpression} for a {@link HbMappedClass}
   * @see #getRelationshipTypeExpression(HbMappedClass)
   * @see #getSortalTypeExpression(HbMappedClass)
   * @param hbClass the {@link HbMappedClass} to search for
   * @return an existing {@link WUniversalTypeExpression} or null, if no such {@link WUniversalTypeExpression} exists
   */
  protected final WUniversalTypeExpression resolve(HbMappedClass hbClass) {
    WUniversalTypeExpression result = getRelationshipTypeExpression(hbClass);
    if (result == null) {
      result = getSortalTypeExpression(hbClass);
    }
    return result;

  }

  protected final WMixinTypeExpression resolve(AttributeTypeGroup group) {
    return getMetamodel().findMixinByPersistentName(group.getName());
  }

  /**
   * Collect mapping information for a created {@link WPropertyExpression}
   * and its corresponding property's getter {@link Method}
   * 
   * @param propertyExpression the created {@link WPropertyExpression}
   * @param method the corresponding getter {@link Method}
   */
  protected final void add(WTypeExpression containingType, WPropertyExpression propertyExpression, Method method) {
    if (containingType == null || propertyExpression == null) {
      throw new ModelException(ModelException.GENERAL_ERROR, "Type and Property must not be null");
    }
    if (!this.builtInProperties.containsKey(containingType)) {
      Map<WPropertyExpression, Method> map = Maps.newHashMap();
      this.builtInProperties.put(containingType, map);
    }
    this.builtInProperties.get(containingType).put(propertyExpression, method);
  }

  /**
   * Collect mapping information for a created {@link WPropertyExpression}
   * and its corresponding {@link AttributeType} (=source)
   * 
   * @param propertyExpression the created {@link WPropertyExpression}
   * @param attributeType the {@link AttributeType} which has been used to create the {@link WPropertyExpression}
   */
  protected final void add(WPropertyExpression propertyExpression, AttributeType attributeType) {
    this.additionalProperties.put(propertyExpression.getPersistentName(), attributeType);
  }

  /**
   * Get an existing {@link WNominalEnumerationExpression} for an {@link Enum}
   * 
   * @param enumm the {@link Enum} to search for
   * @return an existing {@link WNominalEnumerationExpression} or null, if no such {@link WNominalEnumerationExpression} exists
   */
  protected WNominalEnumerationExpression resolve(Class<? extends Enum<?>> enumm) {
    if (enumm == null) {
      return null;
    }
    for (Entry<WNominalEnumerationExpression, Class<? extends Enum<?>>> e : enumerationExpressions.entrySet()) {
      if (enumm == e.getValue()) {
        return e.getKey();
      }
    }
    return null;
  }

  protected WValueTypeExpression<?> resolveVT(Class<?> clazz) {
    if (clazz == null) {
      return null;
    }
    // prevent usage of primitive type boolean
    if (boolean.class.equals(clazz)) {
      return AtomicDataType.BOOLEAN.type();
    }
    if (RuntimePeriod.class.equals(clazz)) {
      return AtomicDataType.DURATION.type();
    }
    for (AtomicDataType primitiveType : AtomicDataType.values()) {
      if (primitiveType.type().getEncapsulatedType().equals(clazz)) {
        return primitiveType.type();
      }
    }
    return null;
  }

  /**
   * Get an existing {@link WNominalEnumerationExpression} for an {@link EnumAT}
   * 
   * @param enumAT the {@link EnumAT} to search for
   * @return an existing {@link WNominalEnumerationExpression} or null, if no such {@link WNominalEnumerationExpression} exists
   */
  protected WNominalEnumerationExpression resolve(EnumAT enumAT) {
    if (enumAT == null) {
      return null;
    }
    for (Entry<WNominalEnumerationExpression, EnumAT> e : additionalEnumerationExpressions.entrySet()) {
      if (enumAT == e.getValue()) {
        return e.getKey();
      }
    }
    return null;
  }

  /**
   * Get an existing {@link WEnumerationLiteralExpression} for an {@link Enum} literal
   * 
   * @param literal the {@link Enum}-literal to search for
   * @return an existing {@link WEnumerationLiteralExpression} or null, if no such {@link WEnumerationLiteralExpression} exists
   */
  public WEnumerationLiteralExpression resolve(Enum<?> literal) {
    if (literal == null) {
      return null;
    }
    for (Entry<WEnumerationLiteralExpression, Enum<?>> e : enumerationLiterals.entrySet()) {
      if (literal == e.getValue()) {
        return e.getKey();
      }
    }
    return null;
  }

  public static boolean isDerivedFromEnumAT(EEnum eEnum) {
    return eEnum.getName().contains(EnumAT.class.getCanonicalName());
  }

  public WEnumerationLiteralExpression resolve(WPropertyExpression pe, String literalValue) {
    if (pe.getType() instanceof WNominalEnumerationExpression) {
      for (WEnumerationLiteralExpression ele : ((WNominalEnumerationExpression) pe.getType()).getLiterals()) {
        if (ele.getPersistentName().equals(literalValue)) {
          return ele;
        }
      }
    }
    return null;
  }

  /**
   * Get an existing {@link WEnumerationLiteralExpression} for an {@link EnumAV}
   * 
   * @param enumAV the {@link EnumAV} to search for
   * @return an existing {@link WEnumerationLiteralExpression} or null, if no such {@link WEnumerationLiteralExpression} exists 
   */
  public WEnumerationLiteralExpression resolve(EnumAV enumAV) {
    if (enumAV == null) {
      return null;
    }
    for (Entry<WEnumerationLiteralExpression, EnumAV> e : additionalEnumerationLiterals.entrySet()) {
      if (enumAV == e.getValue()) {
        return e.getKey();
      }
    }
    return null;
  }

  /**
   * Get the getter Method for a {@link WPropertyExpression}
   * 
   * @param property the {@link WPropertyExpression} to search for
   * @return the getter {@link Method} for the given {@link WPropertyExpression} or null, if no such {@link WPropertyExpression} exists
   */
  public Method resolveBuiltInProperty(WTypeExpression containingType, WPropertyExpression property) {
    if (!this.builtInProperties.containsKey(containingType)) {
      return null;
    }
    return this.builtInProperties.get(containingType).get(property);
  }

  /**
   * Get the {@link AttributeType} for an "additional" (sourced from an {@link AttributeType}) {@link WPropertyExpression}
   * 
   * @param property the {@link WPropertyExpression} to search for
   * @return the {@link AttributeType} instance which is represented by the {@link WPropertyExpression} or null, if no such {@link WPropertyExpression} exists
   */
  public AttributeType resolveAdditionalProperty(WPropertyExpression property) {
    return this.additionalProperties.get(property.getPersistentName());
  }

  /**
   * 
   * @return a {@link String}-{@link Set} containing the names of all standard properties that are 
   * contained in each {@link WSortalTypeExpression} by default
   */
  public static final Set<String> initDefaultAttributeNames() {
    //FIXME bra is this correct?
    Set<String> set = Sets.newHashSet();
    set.add(ElasticMiConstants.PERSISTENT_NAME_ID);
    set.add(ElasticMiConstants.PERSISTENT_NAME_NAME);
    set.add(ElasticMiConstants.PERSISTENT_NAME_DESCRIPTION);
    set.add(ElasticMiConstants.PERSISTENT_NAME_LAST_MODIFICATION_TIME);
    set.add(ElasticMiConstants.PERSISTENT_NAME_LAST_MODIFICATION_USER);
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

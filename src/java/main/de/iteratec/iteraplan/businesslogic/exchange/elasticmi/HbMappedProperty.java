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
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.RuntimePeriod;


/**
 * This class provides all necessary data which is used for generating PropertyExpressions;
 * There should be one HbMappedProperty for each Property/Table-Column of a iteraplan entity class
 */
public class HbMappedProperty {

  private static final Logger         LOGGER          = Logger.getIteraplanLogger(HbMappedProperty.class);

  private static final List<Class<?>> PRIMITIVE_TYPES = initPrimitiveTypes();

  private Property                    property;
  private Column                      column;
  private HbMappedClass               containingClass;

  /**
   * Default constructor.
   * 
   * @param containingClass
   *    {@link HbMappedClass} that contains the {@link HbMappedProperty} instance
   * @param property
   *    The corresponding {@link Property} of the {@link HbMappedProperty} instance 
   * @param column
   *    The corresponding {@link Table} of the {@link HbMappedProperty} instance
   */
  public HbMappedProperty(HbMappedClass containingClass, Property property, Column column) {
    this.containingClass = Preconditions.checkNotNull(containingClass);
    this.property = property;
    this.column = column;
  }

  /**
   * 
   * @return the {@link HbMappedClass} that holds this {@link HbMappedProperty} instance
   */
  public HbMappedClass getContainingClass() {
    return containingClass;
  }

  /**
   * 
   * @return the name of the property as defined in the entity class
   */
  public String getName() {
    if (property == null) {
      return column.getName();
    }
    else {
      return property.getName();
    }
  }

  /**
   * 
   * @return true if the attribute's associated column has a unique constraint
   */
  public boolean isUnique() {
    if (column == null) {
      return false;
    }
    return (column.isUnique());
  }

  /**
   * 
   * @return true, if the property's value must not be null
   */
  public boolean isOptional() {
    if (property == null) {
      return column.isNullable();
    }
    else {
      return property.isOptional();
    }

  }

  /**
   * 
   * @return true if the property's upper bound > 1
   */
  public boolean isMany() {
    if (property == null) {
      return false;
    }
    else {
      return property.getType().isCollectionType();
    }

  }

  /**
   * @return true if the property's type is an Enumeration
   */
  public boolean isEnum() {
    return getType().isEnum();
  }

  /**
   * Get the type of the property
   * 
   * @return the {@link Type}'s returned {@link Class} 
   * (Note: for parameterized Types like Collection<T> this method returns the class of T, not Collection 
   */
  public Class<?> getType() {
    Method m = getGetMethod();
    if (m == null) {
      //try to get type via column
      if (column.getValue() != null && column.getValue().getType() != null) {
        return column.getValue().getType().getReturnedClass();
      }

      LOGGER.debug("Could not find puplic getMethod for HbmMappedProperty " + getName());
      return null;
    }
    Type returnType = m.getGenericReturnType();
    if (returnType instanceof ParameterizedType) {
      ParameterizedType type = (ParameterizedType) returnType;
      Type[] typeArguments = type.getActualTypeArguments();
      if (typeArguments.length != 1) {
        LOGGER.warn(m.getName() + "has 0 or more than one TypeArguments: " + typeArguments.length);
        return m.getReturnType();
      }
      else {
        Type returnedType = typeArguments[0];
        //do not use 'instanceof', as it would lead to an import of a sun-class (=> PMD error)
        if (returnedType.getClass().getSimpleName().equals("TypeVariableImpl")) {
          if (getName().equals("children")) {
            return containingClass.getMappedClass();
          }
          else {
            return Object.class;
          }

        }
        return (Class<?>) returnedType;
      }

    }
    Class<?> retType = m.getReturnType();
    if (retType != null && retType.getName().contains("Hierarchical")) {
      LOGGER.debug("Found hierarchical entity; returning " + containingClass.getMappedClass());
      retType = containingClass.getMappedClass();
    }

    return retType;
  }

  /**
   * @return the {@link HbMappedClass} that is representing the {@link #getType()}'s {@link Class} or null, if the instance does not represent a relationship
   */
  public HbMappedClass getHbType() {
    if (isPrimitive() || isEnum()) {
      return null;
    }
    return containingClass.getAllHbMappedClasses().get(getType().getCanonicalName());
  }

  /**
   * Get the getter {@link Method} for the represented property
   * 
   * @return the property's getter {@link Method}
   */
  public Method getGetMethod() {
    if (property != null && containingClass != null && containingClass.getMappedClass() != null
        && property.getGetter(containingClass.getMappedClass()) != null) {
      Method m = property.getGetter(containingClass.getMappedClass()).getMethod();
      if (Modifier.isPublic(m.getModifiers())) {
        return m;
      }
    }
    return null;
  }

  /**
   * Get the setter {@link Method} for the represented property
   */
  public Method getSetMethod() {
    if (property != null && containingClass != null && containingClass.getMappedClass() != null
        && property.getSetter(containingClass.getMappedClass()) != null) {
      Method m = property.getSetter(containingClass.getMappedClass()).getMethod();
      if (Modifier.isPublic(m.getModifiers())) {
        return m;
      }
    }
    return null;
  }

  /**
   * 
   * @return true if the property's type is a primitive type
   */
  public boolean isPrimitive() {
    return PRIMITIVE_TYPES.contains(getType());
  }

  /**
   * This {@link ImmutableSet} should contain all primitive data types that are used by the iteraplan entity classes.
   * Note: A {@link HbMappedProperty} is considered "primitive" ({@link #isPrimitive()}) if its type is contained in this {@link List}!
   */
  public static ImmutableList<Class<?>> initPrimitiveTypes() {
    ArrayList<Class<?>> types = Lists.newArrayList();

    types.add(int.class);
    types.add(Integer.class);
    types.add(float.class);
    types.add(BigDecimal.class);
    types.add(boolean.class);
    types.add(Boolean.class);
    types.add(String.class);
    types.add(Date.class);
    types.add(long.class);
    types.add(RuntimePeriod.class);

    return ImmutableList.copyOf(types);
  }

  /**
   * Find the opposite of an instance that is representing a relationship
   * {@link HbMappedClass}.{@link #getOpposite()}
   * 
   * @return the {@link HbMappedProperty} which is representing the instance's back-ref
   */
  public HbMappedProperty getOpposite() {
    return containingClass.getOpposite(this);
  }

  /**
   * Providing a nicer string representation as the default one
   */
  public String toString() {
    String primitive = "";
    String opposite = "";
    if (isPrimitive()) {
      primitive = "Primitive ";
    }
    else if (!isEnum() && getOpposite() != null) {
      opposite = ", opposite: " + getOpposite().getName();
    }
    return (primitive + "Property " + containingClass.getClassName() + "." + getName() + " (of type: " + getType() + ")" + opposite);
  }

  /**
   * Providing a {@link Comparator} that can be used to sort the {@link HbMappedProperty}s of a {@link HbMappedClass}
   */
  @SuppressWarnings("rawtypes")
  public static Comparator getComparator() {
    return new Comparator<HbMappedProperty>() {

      public int compare(HbMappedProperty o1, HbMappedProperty o2) {
        if ((o1.isPrimitive() && !o2.isPrimitive()) || (o1.isEnum() && !o2.isPrimitive() && !o2.isEnum())) {
          return -1;
        }
        else if ((o2.isPrimitive() && !o1.isPrimitive()) || (o2.isEnum() && !o1.isPrimitive() && !o1.isEnum())) {
          return 1;
        }
        return o1.getName().compareTo(o2.getName());
      }

    };
  }

}

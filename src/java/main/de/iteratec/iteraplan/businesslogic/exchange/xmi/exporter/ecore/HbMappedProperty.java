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
package de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.Preconditions;


/**
 * This class is responsible for the generation of EStructuralFeatures for the iteraplan entity classes.
 * Each instance is representing one mapped property (and its corresponding database column).
 */
public class HbMappedProperty {

  private static final Logger LOGGER      = Logger.getIteraplanLogger(HbMappedProperty.class);

  private Property            property;
  private Column              column;
  private HbMappedClass      containingClass;

  public HbMappedProperty(HbMappedClass containingClass, Property property, Column column) {
    this.containingClass = Preconditions.checkNotNull(containingClass);
    this.property = Preconditions.checkNotNull(property);
    this.column = column;
  }

  /**
   * Checks whether the property is the identifier property
   * 
   * @return true if the property represents the entity's id attribute
   */
  public boolean isId() {
    return containingClass.getIdentifierPropertyName().equals(getName());
  }

  /**
   * 
   * @return the name of the property as defined in the entity class
   */
  public String getName() {
    return property.getName();
  }

  /**
   * 
   * @return true if the attribute's associated column has a unique constraint
   */
  public boolean isUnique() {
    if (column == null) {
      return false;
    }
    return (isId() || column.isUnique());
  }

  /**
   * 
   * @return true, if the property's value must not be null
   */
  public boolean isOptional() {
    return property.isOptional();
  }

  /**
   * 
   * @return true if the property's upper bound &gt; 1
   */
  public boolean isMany() {
    return property.getType().isCollectionType();
  }

  /**
   * 
   * return true if the property's upper bound &gt; 1
   */
  public boolean isOrdered() {
    return isMany();
  }

  /**
   * Get the type of the property
   * 
   * @return the {@link Type}'s returned {@link Class}
   * (Note: for parameterized Types like Collection&lt;T&gt; this method returns the class of T, not Collection
   */
  public Class<?> getType() {
    if (isId()) {
      return String.class;
    }
    Method m = getGetMethod();
    if (m == null) {
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
            return containingClass.getEntityClass();
          }
          else {
            return Object.class;
          }

        }
        return (Class<?>) returnedType;
      }

    }
    Class<?> retType = m.getReturnType();

    return retType;
  }

  /**
   * Get the getter {@link Method} for the represented property
   * 
   * @return the property's getter {@link Method}
   */
  private Method getGetMethod() {
    try {
      Method m = property.getGetter(containingClass.getEntityClass()).getMethod();
      if (Modifier.isPublic(m.getModifiers())) {
        return m;
      }
      return null;
    } catch (SecurityException e) {
      LOGGER.error(e);
      return null;
    }
  }

}

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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.MappingException;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.metadata.ClassMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.Preconditions;


/**
 * This class provides the information which is used while generating the EClasses for the
 * iteraplan entity classes.
 * Each instance represents an iteraplan entity class
 */
public class HbMappedClass {

  private static final Logger LOGGER                    = Logger.getIteraplanLogger(HbMappedClass.class);

  private ClassMetadata                 metaData;
  private PersistentClass               persistentClass;
  private Map<String, HbMappedProperty> properties;
  private List<String>                  propertyNames;
  private Map<String, HbMappedClass>    allHbMappedClasses;
  private Set<String>                   superPropNames;

  public HbMappedClass(Map<String, HbMappedClass>allHbMappedClasses, ClassMetadata metaData, PersistentClass persistentClass) {

    this.allHbMappedClasses = Preconditions.checkNotNull(allHbMappedClasses);
    this.metaData = Preconditions.checkNotNull(metaData);
    this.persistentClass = Preconditions.checkNotNull(persistentClass);
    this.propertyNames = Arrays.asList(this.metaData.getPropertyNames());
    initProperties();
  }

  /**
   * Get the {@link HbMappedProperty} which is contained in the {@link HbMappedClass}
   * 
   * @param propertyName the name of the property
   * 
   * @return the {@link HbMappedProperty} with the given propertyName; null, if no proerty with
   * the propertyName exists
   */
  public HbMappedProperty getProperty(String propertyName) {
    return properties.get(propertyName);
  }

  /**
   * Get the name of the identifier property
   * 
   * @return the name of the identifier property
   */
  public String getIdentifierPropertyName() {
    return metaData.getIdentifierPropertyName();
  }

  /**
   * Get all {@link HbMappedProperty}s of the {@link HbMappedClass}
   * 
   * @return all {@link HbMappedProperty}s; all properties which are defined in the entity class'
   * super classes are being ignored
   */
  public Collection<HbMappedProperty> getProperties() {
    return properties.values();
  }

  /**
   * Get the names of all properties which are defined in the represented entity class
   * 
   * @return a {@link List} of names; (Note: names of properties which are defined in super classes are being ignored)
   */
  public List<String> getPropertyNames() {
    List<String> result = Arrays.asList(metaData.getPropertyNames());
    result.removeAll(superPropNames);
    return result;
  }

  /**
   * Get the represented entity class
   * 
   * @return the {@link Class} which is represented by this {@link HbMappedClass} instance.
   */
  public Class<?> getEntityClass() {
    return persistentClass.getMappedClass();
  }

  /**
   * Delete all (HbmMapped)Properties from this {@link HbMappedClass} which are
   * defined in the super class of the represented entity class.
   */
  public void cleanUpSuperProperties() {
    superPropNames.clear();
    Class<?> superClass = getSuperClass(null);
    HbMappedClass hbMappedSuperClass = getHbmMappedSuperClass();
    if (superClass == null) {
      return;
    }
    List<String> props = getPropertyNames();
    List<String> superProps = Lists.newArrayList();
    while (superClass != null) {
      if (hbMappedSuperClass != null && !hbMappedSuperClass.getPropertyNames().isEmpty()) {
        superProps.addAll(hbMappedSuperClass.getPropertyNames());
      }
      superClass = getSuperClass(superClass);
      hbMappedSuperClass = null;
      if (superClass != null) {
        hbMappedSuperClass = allHbMappedClasses.get(superClass.getName());
      }
    }
    for (String propName : superProps) {
      if (props.contains(propName)) {
        properties.remove(propName);
        superPropNames.add(propName);
      }
    }
  }

  /**
   * Initialize {@link HbMappedProperty}s for all properties as
   * defined by {@link ClassMetadata}.getPropertyNames()
   */
  private void initProperties() {
    properties = Maps.newHashMap();
    superPropNames = Sets.newHashSet();
    if (persistentClass.getMappedClass().getSuperclass().equals(Object.class)) {
      Property idProp = persistentClass.getIdentifierProperty();
      Column idCol = getColumn(idProp);
      HbMappedProperty idHbmProp = new HbMappedProperty(this, idProp, idCol);
      properties.put(metaData.getIdentifierPropertyName(), idHbmProp);
    }

    for (String propertyName : metaData.getPropertyNames()) {
      createProperty(propertyName);
    }
  }

  /**
   * create an {@link HbMappedProperty}
   * 
   */
  private void createProperty(String propName) {
    Property property = getPropertyFromPersistentClass(propName);
    if (property != null) {
      Column column = getColumn(property);
      HbMappedProperty newHbmProp = new HbMappedProperty(this, property, column);
      properties.put(propName, newHbmProp);
    }
  }

  /**
   * Get a {@link Property} for a propertyName
   * 
   * @param propertyName the property to search for
   * 
   * @return the {@link Property} with the given propertyName
   */
  private Property getPropertyFromPersistentClass(String propertyName) {
    try {
      if (propertyNames.contains(propertyName)) {
        return persistentClass.getProperty(propertyName);
      }
      else {
        return null;
      }
    } catch (MappingException e) {
      LOGGER.debug(e.getMessage(), e);
      return null;
    }

  }

  /**
   * Get a {@link Column} for a {@link Property}
   * 
   * @param property
   * @return the {@link Column};
   */
  private Column getColumn(Property property) {
    if (property == null || !property.getColumnIterator().hasNext()) {
      return null;
    }

    return (Column) property.getColumnIterator().next();
  }

  /**
   * 
   * @return the super class of the represented entity class (if it is different from {@link Object}.class)
   */
  private Class<?> getSuperClass(Class<?> clazz) {
    Class<?> c = clazz;
    if (c == null) {
      c = getEntityClass();
    }
    if (c == null || c.getSuperclass() == null || c.getSuperclass().equals(Object.class)) {
      return null;
    }
    return c.getSuperclass();
  }

  /**
   * 
   * @return the {@link HbMappedClass} representing the entity classes super class
   * (if such a super class exists - and is different from Object.class; null otherwise)
   */
  private HbMappedClass getHbmMappedSuperClass() {
    Class<?> superClass = getSuperClass(null);
    if (superClass == null) {
      return null;
    }
    return allHbMappedClasses.get(superClass.getName());
  }
}

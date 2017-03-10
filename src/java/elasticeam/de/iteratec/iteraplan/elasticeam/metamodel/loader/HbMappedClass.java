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
package de.iteratec.iteraplan.elasticeam.metamodel.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.MappingException;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.metadata.ClassMetadata;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;


/**
 * This class provides all necessary data which is used for generating 
 * SubstantialTypeExpressions and RelationshipTypeExpressions;
 * There should be one HbMappedClass for each entity class / table of the iteraplan data model
 */
public class HbMappedClass {

  private static final Logger             LOGGER            = Logger.getIteraplanLogger(HbMappedClass.class);
  private static final String             RELEASE           = "Release";
  private static final String             REV               = "REV";
  public static final Map<String, String> OPPOSITE_NAMES    = initOppositeNames();
  public static final Set<String>         IGNORED_OPPOSITES = initIgnoreProperties();

  private ClassMetadata                   metaData;
  private Table                           table;
  private PersistentClass                 persistentClass;
  private Map<String, HbMappedProperty>   properties;
  private Set<HbMappedClass>              subClasses;

  private Map<String, HbMappedClass>      allHbMappedClasses;

  /**
   * Use this constructor for abstract classes only!
   * (e.g. {@link AbstractHierarchicalEntity})
   * 
   * @param allHbMappedClasses holds the references to all other initialized {@link HbMappedClass}es
   * @param persistentClass the representing entity class
   */
  public HbMappedClass(Map<String, HbMappedClass> allHbMappedClasses, PersistentClass persistentClass) {
    this.allHbMappedClasses = Preconditions.checkNotNull(allHbMappedClasses);
    this.metaData = null;
    this.persistentClass = Preconditions.checkNotNull(persistentClass);
    this.table = persistentClass.getIdentityTable();
    initProperties();
  }

  /**
   * Standard Constructor
   * 
   * @param allHbMappedClasses holds the references to all other initialized HbMappedClasses
   * @param metaData contains all necessary hibernate mapping information
   * @param persistentClass the representing entity class ({@link PersistentClass})
   */
  public HbMappedClass(Map<String, HbMappedClass> allHbMappedClasses, ClassMetadata metaData, PersistentClass persistentClass) {

    this.allHbMappedClasses = Preconditions.checkNotNull(allHbMappedClasses);
    this.metaData = Preconditions.checkNotNull(metaData);
    this.persistentClass = Preconditions.checkNotNull(persistentClass);
    this.table = persistentClass.getIdentityTable();
    initProperties();
  }

  /**
   * Returns the represented class' name
   * @return
   *    the canonical name of the class if the instance is representing an automatically generated table (without a corresponding entity class, like HIST* tables)
   *    the simple name of the represented entity class
   */
  public String getClassName() {
    if (getMappedClass() == null) {
      return persistentClass.getEntityName();
    }
    else {
      return getMappedClass().getSimpleName();
    }
  }

  /**
   * Used from @see {@link HbMappedProperty}.{@link #getOpposite(HbMappedProperty)}
   */
  public Map<String, HbMappedClass> getAllHbMappedClasses() {
    return allHbMappedClasses;
  }

  /**
   * Searches for a corresponding back-ref
   * @see #OPPOSITE_NAMES
   * @see #IGNORED_OPPOSITES
   * 
   * @param prop the {@link HbMappedProperty}
   * @return the opposite (=back-ref) of the {@link HbMappedProperty}
   */
  HbMappedProperty getOpposite(HbMappedProperty prop) {
    if (!this.equals(prop.getContainingClass()) || !properties.containsKey(prop.getName())) {
      throw (new IteraplanTechnicalException());
    }
    String propName = prop.getName();
    if (REV.equals(propName) || IGNORED_OPPOSITES.contains(propName)) {
      // HistoryRevisionEntity does not contain a back-reference
      // some relationships don't have opposites (@see initIgnoreOpposites())
      return null;
    }
    Class<?> type = prop.getType();
    if (type.isEnum()) {
      LOGGER.warn("Opposite for enum requested!");
      return null;
    }
    HbMappedClass targetClass = prop.getHbType();
    if (targetClass == null) {
      LOGGER.warn("Could not find HbMappedClass for " + type.getCanonicalName());
      targetClass = this;
    }

    for (Entry<String, String> e : OPPOSITE_NAMES.entrySet()) {

      if (propName.equals(e.getKey())) {
        if (getProperty(e.getValue()) != null) {
          targetClass = this;
        }
        return targetClass.getProperty(e.getValue());
      }
      if (propName.equals(e.getValue())) {
        if (getProperty(e.getKey()) != null) {
          targetClass = this;
        }
        return targetClass.getProperty(e.getKey());
      }

    }

    ArrayList<HbMappedProperty> candidates = new ArrayList<HbMappedProperty>();
    for (HbMappedProperty hbProp : targetClass.getAllRelations()) {
      Class<?> propType = hbProp.getType();
      if (propType != null) {
        HbMappedClass propTypeHbClass = hbProp.getHbType();
        if (propTypeHbClass != null && propTypeHbClass.equals(this)) {
          candidates.add(hbProp);
        }
      }
    }
    if (candidates.size() == 1) {
      return candidates.get(0);
    }
    LOGGER.debug("Could not determine opposite for HbMappedProperty " + getClassName() + "." + propName + ":");
    if (candidates.size() < 1) {
      LOGGER.debug("\tFound 0 candidates");
    }
    else {
      for (HbMappedProperty candidate : candidates) {
        LOGGER.debug("tFound " + candidate.getContainingClass().getClassName() + "." + candidate.getName());
      }
    }

    return null;
  }

  /**
   * Finds all {@link HbMappedClass}es representing subclasses of the 
   * class that is represented by this {@link HbMappedClass} instance
   * 
   * @return a {@link Set} of {@link HbMappedClass}es that are representing subclasses of this instance
   */
  public Set<HbMappedClass> getSubClasses() {
    if (!hasSubClasses()) {
      //just to make sure that subClasses has been initialized correctly
      return subClasses;
    }
    return subClasses;
  }

  /**
   * Decides whether the represented class is a "release"-class
   * (currently: InformationSystemRelease, TechnicalComponentRelease)
   * 
   * @return true, if {@link #getClassName()} contains "Release"
   */
  public boolean isReleaseClass() {
    return (getMappedClass() != null && getClassName().contains(RELEASE));
  }

  /**
   * Find the base class for "release"-classes
   * @see #isReleaseClass()
   * 
   * @return the base class for "release"-classes (e.g. InformationSystem for InformationSystemRelease, TechnicalComponent for TechnicalComponentRelease)
   */
  public HbMappedClass getReleaseBase() {
    if (!isReleaseClass() || getMappedClass() == null) {
      return null;
    }
    return allHbMappedClasses.get(getMappedClass().getCanonicalName().replace(RELEASE, ""));
  }

  public HbMappedProperty getReleaseBaseProperty() {
    if (!isReleaseClass()) {
      return null;
    }
    return properties.get(decapitalize(getReleaseBase().getClassName()));
  }

  /**
   * Necessary to ensure the correct merging of e.g. InformationSystemRelease + InformationSystem => InformationSystem+
   * 
   * @return true if there are {@link HbMappedClass}es 
   */
  public boolean hasReleaseClass() {
    return getReleaseClass() != null;
  }

  public HbMappedClass getReleaseClass() {
    if (getMappedClass() == null) {
      return null;
    }
    return allHbMappedClasses.get(getMappedClass().getCanonicalName() + RELEASE);
  }

  /**
   * Get the {@link HbMappedProperty} which is contained in the {@link HbMappedClass} 
   * 
   * @param propertyName the name of the property
   * 
   * @return the {@link HbMappedProperty} with the given propertyName; null, if no property with 
   * the propertyName exists 
   */
  public HbMappedProperty getProperty(String propertyName) {
    return properties.get(propertyName);
  }

  /**
   * 
   * @return true if the instance is representing an {@link Enum}
   */
  public boolean isEnum() {
    if (getMappedClass() != null && getMappedClass().isEnum()) {
      return true;
    }
    return false;
  }

  /**
   * 
   * @return the iteraplan entity class which is represented or null if the instance is representing an automatically generated table (like HIST*)
   */
  public final Class<?> getMappedClass() {
    return persistentClass.getMappedClass();
  }

  /**
   * Get all {@link HbMappedProperty}s of the {@link HbMappedClass}
   * 
   * @return all {@link HbMappedProperty}s; all properties which are defined in the entity class' 
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
    if (metaData == null) {
      return Lists.newArrayList();
    }

    return Arrays.asList(metaData.getPropertyNames());
  }

  /**
   * @return true if the instance is representing a class that serves as a super class of another entity class (like {@link BuildingBlock}, {@link AttributeType}, ...)
   */
  public boolean hasSubClasses() {
    if (subClasses == null) {
      findSubclasses();
    }
    return !subClasses.isEmpty();
  }

  /**
   * Initialize {@link HbMappedProperty}s for all properties as 
   * defined by {@link ClassMetadata#getPropertyNames()}
   */
  @SuppressWarnings("unchecked")
  private final void initProperties() {
    properties = Maps.newHashMap();
    if (getMappedClass() != null && getMappedClass().getSuperclass().equals(Object.class) && metaData != null) {
      Property idProp = persistentClass.getIdentifierProperty();
      Column idCol = getColumn(idProp);
      HbMappedProperty idHbmProp = new HbMappedProperty(this, idProp, idCol);
      properties.put(metaData.getIdentifierPropertyName(), idHbmProp);
    }

    if (getMappedClass() == null) {
      //handle history revision entity
      Iterator<Column> colIterator = table.getColumnIterator();
      while (colIterator.hasNext()) {
        Column column = colIterator.next();
        createProperty(column);
      }
    }
    else if (metaData != null) {
      for (String propertyName : metaData.getPropertyNames()) {
        createProperty(propertyName);
      }
    }
  }

  /**
   * create a {@link HbMappedProperty} for tables that do not have a corresponding entity class
   * (e.g. HIST*)
   * Uses meta information of the {@link Column} instead.
   * 
   */
  private void createProperty(Column column) {
    HbMappedProperty newHbmProp = new HbMappedProperty(this, null, column);
    properties.put(column.getName(), newHbmProp);
  }

  /**
   * create a {@link HbMappedProperty} for a standard property 
   * 
   * @param propName the name of the property 
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
      return persistentClass.getProperty(propertyName);
    } catch (MappingException e) {
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
      c = getMappedClass();
    }
    if (c == null || c.getSuperclass() == null || c.getSuperclass().equals(Object.class)) {
      return null;
    }
    return c.getSuperclass();
  }

  /**
   * 
   * @return the subset of {@link #getAllHbMappedProperties()} representing primitive properties (no relationships or enums)
   */
  public Collection<HbMappedProperty> getAllProperties() {
    Set<HbMappedProperty> hbProps = Sets.newHashSet();
    for (HbMappedProperty hbProp : getAllHbMappedProperties()) {
      if (hbProp.isPrimitive()) {
        hbProps.add(hbProp);
      }
    }
    return hbProps;
  }

  /**
   * 
   * @return the subset of {@link #getAllHbMappedProperties()} representing enumeration properties (no relationships or primitive properties)
   */
  public Collection<HbMappedProperty> getAllEnums() {
    Set<HbMappedProperty> hbProps = Sets.newHashSet();
    for (HbMappedProperty hbProp : getAllHbMappedProperties()) {
      if (hbProp.isEnum()) {
        hbProps.add(hbProp);
      }
    }
    return hbProps;
  }

  /**
   * 
   * @return the subset of {@link #getAllHbMappedProperties()} which represents relationships (no attributes or enums)
   */
  public Collection<HbMappedProperty> getAllRelations() {
    Set<HbMappedProperty> hbRels = Sets.newHashSet();
    for (HbMappedProperty hbRel : getAllHbMappedProperties()) {
      if (!hbRel.isPrimitive() && !hbRel.isEnum()) {
        hbRels.add(hbRel);
      }
    }
    return hbRels;
  }

  /**
   * Returns a {@link Collection} of all {@link HbMappedProperty}s that are defined for this class, the class' super class
   * (and properties that are defined in {@link #getReleaseBase()}) 
   */
  private Collection<HbMappedProperty> getAllHbMappedProperties() {
    Map<String, HbMappedProperty> allProps = Maps.newHashMap();
    HbMappedClass tmp = this;

    while (tmp != null) {
      for (HbMappedProperty hbProp : tmp.getProperties()) {
        if (!allProps.containsKey(hbProp.getName())) {
          allProps.put(hbProp.getName(), hbProp);
        }
      }
      tmp = tmp.getHbSuperClass();
    }

    if (hasReleaseClass()) {
      String className = getMappedClass().getCanonicalName() + RELEASE;

      HbMappedClass releaseClass = getReleaseClass();
      if (releaseClass == null) {
        LOGGER.error("Could not find release class " + className);
      }
      else {
        for (HbMappedProperty hbProp : releaseClass.getProperties()) {
          if (!allProps.containsKey(hbProp.getName())) {
            allProps.put(hbProp.getName(), hbProp);
          }
        }
      }
    }

    return allProps.values();
  }

  /**
   * 
   * @return the HbMappedClass that is representing the next available (i.e. not abstract) super class of the represented entity class
   */
  public HbMappedClass getHbSuperClass() {
    Class<?> superClass = getSuperClass(null);
    while (superClass != null && allHbMappedClasses.get(superClass.getName()) == null) {
      superClass = getSuperClass(superClass);
    }
    if (superClass == null || Object.class.equals(superClass)) {
      return null;
    }
    else {
      return allHbMappedClasses.get(superClass.getName());
    }
  }

  /**
   * @return the super class of the represented entity class
   */
  public Class<?> getSuperClass() {
    if (getMappedClass() == null) {
      return Object.class;
    }
    else {
      return getMappedClass().getSuperclass();
    }
  }

  /**
   * @return a nicer String representation than the default one
   */
  @Override
  public String toString() {
    String superName = "";
    if (getHbSuperClass() != null) {
      superName = " <- " + getHbSuperClass().getClassName();
    }
    if (subClasses == null) {
      findSubclasses();
    }

    return "Metadata for class " + getClassName() + superName + " (" + subClasses.size() + ")";
  }

  /**
   * Specifies special name pairs for Relationships among the iteraplan model. This is to make sure that RelationshipEndExpressions 
   * can be associated to the right RelationshipTypeExpressions.
   */
  private static ImmutableMap<String, String> initOppositeNames() {
    Map<String, String> map = Maps.newHashMap();
    map.put("parent", "children");
    map.put("predecessors", "successors");
    map.put("generalisation", "specialisations");
    map.put("informationSystemReleaseA", "interfacesReleaseA");
    map.put("informationSystemReleaseB", "interfacesReleaseB");
    map.put("elementOfRoles", "consistsOfRoles");
    map.put("baseComponents", "parentComponents");

    return ImmutableMap.copyOf(map);
  }

  /**
   * Some of the iteraplan references do not have a corresponding back-reference. The names of these relationships should be contained within this {@link ImmutableSet}.
   * By that, the method @see getOpposite() will return null before searching for a back-reference
   */
  private static ImmutableSet<String> initIgnoreProperties() {

    Set<String> set = Sets.newHashSet();
    set.add("buildingBlockType");
    set.add("runtimePeriod");
    set.add("resultBbType");

    return ImmutableSet.copyOf(set);
  }

  /**
   * Private helper method which initializes {@link #subClasses}, a {@link Set} of {@link HbMappedClass}es containing all instances representing subclasses of this instance
   */
  private void findSubclasses() {
    subClasses = Sets.newHashSet();
    if (getMappedClass() != null) {
      for (HbMappedClass candidate : allHbMappedClasses.values()) {
        if (candidate.getMappedClass() != null && this.equals(candidate.getHbSuperClass())) {
          subClasses.add(candidate);
        }
      }
    }
  }

  private String decapitalize(String s) {
    return s.substring(0, 1).toLowerCase() + s.substring(1);
  }
}

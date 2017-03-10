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
package de.iteratec.iteraplan.businesslogic.reports.query.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.common.util.VBStyleCollection;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.user.PermissionHelper;


/**
 * Base class for all types.
 * <p>
 * Types represent a meta model for the building blocks and relationships used in this application.
 */
public abstract class Type<T extends BuildingBlock> implements Serializable {

  private static final long                                                                        serialVersionUID                = 701084814835980368L;
  private String                                                                                   typeNameDB;
  private String                                                                                   typeNameDBShort;
  private String                                                                                   typeNamePresentationKey;
  private String                                                                                   typeNamePluralPresentationKey;

  /** Map from key (String) to String array. */
  private final Map<String, String[]>                                                              specialPropertyHQLStrings       = new HashMap<String, String[]>();

  /** List of {@link Property}. */
  private final List<Property>                                                                     properties                      = new ArrayList<Property>();

  /**
   * Map of name (String) to {@link Extension}. These are the query extensions that are available
   * for the dynamic queries
   */
  private final Map<String, IPresentationExtension>                                                extensions                      = new HashMap<String, IPresentationExtension>();

  /**
   * Map of name (String) to {@link Extension}. This is only used for the landscape export. These
   * extensions are only a subset of the {@link #extensions} map and hold (slightly) different
   * Extension objects
   */
  private final Map<String, Extension>                                                             relations                       = new HashMap<String, Extension>();

  /**
   * Map of name (String) to {@link TypeWithJoinProperty}. Note that this has to be initialized
   * lazily since otherwise references to the singletons of subclasses of Type could be created
   * before the singletons are actually initialized.
   */
  private final Map<String, TypeWithJoinProperty>                                                  associations                    = new HashMap<String, TypeWithJoinProperty>();

  private final Set<SimpleAssociation>                                                             massUpdateAssociations          = Sets
                                                                                                                                       .newHashSet();

  /** List of {@link AbstractPostprocessingStrategy}. */
  private final VBStyleCollection<String, AbstractPostprocessingStrategy<? extends BuildingBlock>> postprocessingStrategies        = new VBStyleCollection<String, AbstractPostprocessingStrategy<? extends BuildingBlock>>();

  public static final String                                                                       PROPERTY_LAST_USER              = "lastModificationUser";
  public static final String                                                                       PROPERTY_LAST_MODIFICATION_DATE = "lastModificationTime";
  public static final String                                                                       PROPERTY_SUBSCRIBED_USERS       = "subscribedUsers";

  /**
   * @param typeNameDB
   *          The class name of the model this type stands for. Used within HQL queries.
   * @param typeNameDBShort
   *          Some short name for the class name that must be unique.
   * @param typeNamePresentationKey
   *          The key to use for i18n property files.
   */
  Type(String typeNameDB, String typeNameDBShort, String typeNamePresentationKey, String typeNamePluralPresentationKey) {
    this.typeNameDB = typeNameDB;
    this.typeNameDBShort = typeNameDBShort;
    this.typeNamePresentationKey = typeNamePresentationKey;
    this.typeNamePluralPresentationKey = typeNamePluralPresentationKey;
    initProperties();
    addDefaultProperties();
    initPostprocessingStrategies();
  }

  public Type(String typeNamePresentationKey, String typeNamePluralPresentationKey) {
    this(null, null, typeNamePresentationKey, typeNamePluralPresentationKey);
  }

  /**
   * Initialize the properties for this type. Status and timespan are NOT included in these
   * properties.
   */
  abstract void initProperties();

  private void addDefaultProperties() {
    if (!(this instanceof MassUpdateType)) {
      properties.add(new Property(PROPERTY_LAST_USER, Constants.ATTRIBUTE_LAST_USER, 100));
      properties.add(new Property(PROPERTY_LAST_MODIFICATION_DATE, Constants.ATTRIBUTE_LAST_MODIFICATION_DATE, 101));
      properties.add(new Property(PROPERTY_SUBSCRIBED_USERS, Constants.ATTRIBUTE_SUBSCRIBED_USERS, 102));
    }
  }

  /**
   * Initialize the query extensions for this type.
   */
  protected void initExtensions() {
    // implementable by subclasses
  }

  /**
   * Initialize the associations of this type.
   */
  protected void initAssociations() {
    // implementable by subclasses
  }

  /**
   * Initialize the available postprocessing strategies for this type. The strategies have to be
   * added to the type by calling the
   * {@link #addPostProcessingStrategy(AbstractPostprocessingStrategy)} method. Since more than one
   * post processing strategy can be executed, the order number that is passed to the post
   * processing strategies is extremely important.
   */
  abstract void initPostprocessingStrategies();

  /**
   * @return Returns the typeName.
   */
  public String getTypeNameDB() {
    return typeNameDB;
  }

  /**
   * @return Returns the typeNameDBShort.
   */
  public String getTypeNameDBShort() {
    return typeNameDBShort;
  }

  /**
   * @return Returns the specialPropertyHQLStrings.
   */
  public Map<String, String[]> getSpecialPropertyHQLStrings() {
    return ImmutableMap.copyOf(specialPropertyHQLStrings);
  }

  /**
   * Add the special HQL Strings necessary to express queries. The value[] contains two String, e.g.
   * "is" and "null"
   * 
   * @param key
   * @param value
   */
  void addSpecialPropertyHQLString(String key, String[] value) {
    specialPropertyHQLStrings.put(key, value);
  }

  /**
   * This method is called to see if the property that is being queried is inheritable or special
   * and if it is, how the query must be extended to include instances that possibly inherit the
   * property value. In order to extend the query it is necessary to know the operand and the
   * undefined value for this property. <br>
   * Example: For an inheritable date field the operand would be "is" and the undefined value would
   * be "null".
   * 
   * @param property
   *          The property of this Type that is possibly inheritable or somehow special.
   * @return If the property is not special, this method returns a zero length immutable array. 
   *         Otherwise a String array of size 2 is returned that contains the operand and the 
   *         undefined value for this property.
   */
  public String[] getSpecialPropertyHQL(String property) {
    if (specialPropertyHQLStrings.containsKey(property)) {
      return specialPropertyHQLStrings.get(property);
    }
    return ArrayUtils.EMPTY_STRING_ARRAY;
  }

  /**
   * Checks whether the given property is part of the inheritance mechanism or needs some other
   * special handling.
   * 
   * @param property
   *          The property to check. If null, false is returned.
   * @return true if and only if property is part of the inheritance mechanism or somehow special.
   */
  public boolean isSpecialProperty(String property) {
    if (property == null) {
      return false;
    }
    if (specialPropertyHQLStrings.containsKey(property)) {
      return true;
    }
    return false;
  }

  /**
   * Returns the properties map for this type.
   * 
   * @return Map of Property.
   */
  public List<Property> getProperties() {
    return properties;
  }

  public Property getProperty(String nameDB) {
    for (Property property : properties) {
      if (property.getNameDB().equals(nameDB)) {
        return property;
      }
    }
    return null;
  }

  public Property getPropertyFromId(String propertyId) {
    for (Property property : properties) {
      if (property.getNameAsID().equals(propertyId)) {
        return property;
      }
    }
    return null;
  }

  public SimpleAssociation getSimpleAssociationFromId(String associationId) {
    for (SimpleAssociation association : massUpdateAssociations) {
      if (association.getName().equals(associationId)) {
        return association;
      }
    }
    return null;
  }

  /**
   * Adds a property to the Set of properties.
   * 
   * @param property
   */
  void addProperty(Property property) {
    properties.add(property);
  }

  public Set<SimpleAssociation> getMassUpdateAssociations() {
    if (massUpdateAssociations.isEmpty()) {
      initAssociations();
    }
    return massUpdateAssociations;
  }

  void addMassUpdateAssociation(SimpleAssociation association) {
    this.massUpdateAssociations.add(association);
  }

  /**
   * Returns the Map of possible query extensions for this type. The extension name serves as key.
   * Since the extensions contain types that contain extensions to other types, they can't be
   * initialized in the type constructor.
   * 
   * @return Map of Extension.
   */
  public Map<String, IPresentationExtension> getExtensions() {
    return getExtensions(true);
  }

  /**
   * Returns the Map of possible query extensions for this type. The extension name serves as key.
   * Since the extensions contain types that contain extensions to other types, they can't be
   * initialized in the type constructor. <br>
   * Note: This method does not check user permissions for the extensions and must only be used in
   * cases where this is not necessary.
   * 
   * @return Map of Extension.
   */
  Map<String, IPresentationExtension> getExtensionsWithoutPermissionCheck() {
    return getExtensions(false);
  }

  Map<String, IPresentationExtension> getExtensions(boolean checkPermissions) {
    if ((extensions == null) || extensions.isEmpty()) {
      initExtensions();
    }

    Map<String, IPresentationExtension> results = new HashMap<String, IPresentationExtension>();

    if (extensions != null) {
      for (String key : extensions.keySet()) {
        IPresentationExtension extension = extensions.get(key);

        if (checkPermissions) {
          IPresentationExtension clonedExtension = checkAndCloneExtension(extension);
          if (clonedExtension != null) {
            results.put(extension.getName(), clonedExtension);
          }
        }
        else {
          results.put(extension.getName(), extension);
        }
      }
    }
    return results;
  }

  private IPresentationExtension checkAndCloneExtension(IPresentationExtension extension) {
    boolean userHasPermission = PermissionHelper.hasPermissionFor(extension.getPermissionKey());

    if (userHasPermission) {
      IPresentationExtension clonedExtension;
      try {
        clonedExtension = extension.clone();
      } catch (CloneNotSupportedException e) {
        // should never happen
        throw new IteraplanTechnicalException(e);
      }
      clonedExtension.checkDeepPermission();
      return clonedExtension;
    }
    else {
      return null;
    }

  }

  /**
   * Can be overwritten if several extensions have to be combined in a combined extension for the
   * presentation layer.
   */
  public Map<String, IPresentationExtension> getExtensionsForPresentation() {
    return getExtensions();
  }

  /**
   * Returns the Extension for the given extension name. If no extension name is found,
   * <code>null</code> is returned.
   */
  public Extension getExtension(String extensionName) {
    return (Extension) getExtensions().get(extensionName);
  }

  /**
   * Returns the Extension for the given extension name without a check for permissions. If no
   * extension name is found, <code>null</code> is returned.
   */
  public Extension getExtensionWithoutPermissionCheck(String extensionName) {
    return (Extension) getExtensionsWithoutPermissionCheck().get(extensionName);
  }

  /**
   * Add a possible query extension to this type.
   */
  void addExtension(Extension extension) {
    extensions.put(extension.getName(), extension);
  }

  /**
   * Returns the Map of Relations to other types for this type. This is only a subset of the values
   * of {@link #getExtensions()}, and holds subtlely different Extensions. It was developed for the
   * purposes of the landscape diagram.
   * <p>
   * The extension name serves as key. Since the extensions contain types that contain extensions to
   * other types, they can't be initialized in the type constructor.
   * </p>
   * 
   * @return Map of Extensions.
   */
  public Map<String, Extension> getRelations() {
    if ((relations == null) || relations.isEmpty()) {
      initRelations();
    }

    Map<String, Extension> results = new HashMap<String, Extension>();

    if (relations != null) {
      for (Map.Entry<String, Extension> relEntry : relations.entrySet()) {
        Extension extension = relEntry.getValue();
        boolean userHasPermission = PermissionHelper.hasPermissionFor(extension.getPermissionKey());

        if (userHasPermission) {
          results.put(extension.getName(), extension);
        }
      }
    }

    return results;
  }

  /**
   * Add a building block relation to this type. This is stored in a Map to allow later retrieval in
   * {@link #getRelations()}. It should be used by all subtypes implementing
   * {@link #initRelations()}.
   */
  void addRelation(Extension extension) {
    relations.put(extension.getName(), extension);
  }

  /**
   * Initialize the relations for this type. Should be overwritten by all types that can appear in
   * the middle of a landscape diagram.
   */
  protected void initRelations() {
    // nothing to do in the default case
  }

  /**
   * Adds an association to this type. This is stored in a Map to allow later retrieval in
   * {@link #getAssociation(String)}. It should be used by subtypes in {@link #initAssociations()}.
   * 
   * @param association
   *          An association to add.
   */
  void addAssociation(TypeWithJoinProperty association) {
    associations.put(association.getAssociationName(), association);
  }

  /**
   * Returns the association object for the given association name. If no association name is found,
   * <code>null</code> is returned.
   * 
   * @param associationName
   *          The name of the association to return.
   */
  TypeWithJoinProperty getAssociation(String associationName) {
    return getAssociations().get(associationName);
  }

  /**
   * Returns the map of all associations for this type. The association name serves as key.
   * 
   * @return A Map<String,TypeWithJoinProperty> a from association names to association objects.
   */
  Map<String, TypeWithJoinProperty> getAssociations() {
    // do not check for permission here, as associations are something internal in this package,
    // and will not be given to other classes
    // This is in contrast to extensions, which _are_ public and undergo a permission check for that
    // reason
    if (associations.isEmpty()) {
      initAssociations();
    }
    return associations;
  }

  /**
   * Return the post-processing strategies as a {@link VBStyleCollection}.
   * 
   * @return See method description.
   */
  public VBStyleCollection<String, AbstractPostprocessingStrategy<? extends BuildingBlock>> getPostprocessingStrategies() {
    return postprocessingStrategies;
  }

  void addPostProcessingStrategy(AbstractPostprocessingStrategy<? extends BuildingBlock> strat) {
    postprocessingStrategies.addWithKey(strat, strat.getNameKeyForPresentation());
  }

  public String getTypeNamePresentationKey() {
    return typeNamePresentationKey;
  }

  /**
   * Returns {@code true} if the {@link Type} implementation class
   * has status property. Otherwise returns {@code false}.
   * 
   * @return the flag indicating if the {@link Type} has status property
   */
  public boolean isHasStatus() {
    return false;
  }

  /**
   * Returns {@code true} if the {@link Type} implementation class
   * has seal property. Otherwise returns {@code false}.
   * 
   * @return the flag indicating if the {@link Type} has seal property
   */
  public boolean isHasSeal() {
    return false;
  }

  /**
   * Returns {@code true} if the {@link Type} implementation class
   * has time span property. Otherwise returns {@code false}.
   * 
   * @return the flag indicating if the {@link Type} has time span property
   */
  public boolean isHasTimespan() {
    return false;
  }

  public String getTimespanPresentationKey() {
    return null;
  }

  /**
   * Indicates if a type is an association type like e.g. {@code BusinessMapping} or {@code
   * Transport}. This information is used on the GUI and by the query engine. Please override in
   * subclasses if appropriate.
   * 
   * @return Defaults to false.
   */
  public boolean isAssociationType() {
    return false;
  }

  /**
   * Returns true if this is hierarchically ordered according to a user-set ordering using a virtual
   * top-level element.
   * <p>
   * 
   * @return See method description.
   */
  public boolean isOrderedHierarchy() {
    return false;
  }

  /**
   * Returns the name of the virtual root element.
   * <p>
   * For this method to work properly the method {@link #isOrderedHierarchy()} must be overridden in
   * the concrete type to return {@code true} for ordered hierarchies.
   * 
   * @return The name of the virtual root element.
   */
  public final String getOrderedHierarchyRootElementName() {
    if (isOrderedHierarchy()) {
      return AbstractHierarchicalEntity.TOP_LEVEL_NAME;
    }
    else {
      return null;
    }
  }

  /**
   * Returns the post-processing strategy to remove the virtual element. The default implementation
   * returns {@code null}. If concrete types wish to provide such a strategy they must override this
   * method.
   * 
   * @return An {@link AbstractPostprocessingStrategy} or {@code null}.
   */
  public AbstractPostprocessingStrategy<T> getOrderedHierarchyRemoveRootElementStrategy() {
    return null;
  }

  /**
   * Indicates if a type does have its own simple properties or attributes. This information is used
   * on the GUI in order to hide user input form parts for types that do not have any attributes or
   * properties attached to them. Please override if appropriate.
   * 
   * @return true
   */
  public boolean isAnnotatedWithAttributesOrProperties() {
    return true;
  }

  public static List<Type<?>> getAllQueryTypes() {
    List<Type<?>> result = CollectionUtils.arrayList();
    result.add(BusinessDomainQueryType.getInstance());
    result.add(BusinessProcessTypeQ.getInstance());
    result.add(BusinessFunctionQueryType.getInstance());
    result.add(ProductQueryType.getInstance());
    result.add(BusinessUnitQueryType.getInstance());
    result.add(BusinessObjectTypeQu.getInstance());
    result.add(InformationSystemDomainTypeQu.getInstance());
    result.add(InformationSystemReleaseTypeQu.getInstance());
    result.add(InformationSystemInterfaceTypeQu.getInstance());
    result.add(ArchitecturalDomainTypeQu.getInstance());
    result.add(TechnicalComponentReleaseTypeQu.getInstance());
    result.add(InfrastructureElementTypeQu.getInstance());
    result.add(ProjectQueryType.getInstance());
    return result;
  }

  public static Type<?> getTypeByTypeNameDB(String typeNameDB) {
    List<Type<?>> allQueryTypes = getAllQueryTypes();
    for (Type<?> type : allQueryTypes) {
      if (type.getTypeNameDB().equals(typeNameDB)) {
        return type;
      }
    }
    return null;
  }

  public String getTypeNamePluralPresentationKey() {
    return typeNamePluralPresentationKey;
  }

  /**
   * @return The TypeOfBuildingBlock that is represented by this type.
   */
  public abstract TypeOfBuildingBlock getTypeOfBuildingBlock();

  public boolean isPropertyIdEqual(String propertyId, String nameDB) {
    for (Property property : getProperties()) {
      if (property.getNameAsID().equals(propertyId) && property.getNameDB().equals(nameDB)) {
        return true;
      }
    }
    return false;
  }

}
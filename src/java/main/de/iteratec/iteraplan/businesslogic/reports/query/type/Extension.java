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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.user.PermissionHelper;


/**
 * This class represents an extension for a type. An extension models a path from one type to
 * another. Types represent the nodes of the path and the property that leads to the next type on
 * the path represent the edges. The path is represented by the
 * {@link #getTypesWithJoinProperties()} list. <br>
 * <br>
 * An extension can also split up and go to different types. This is for example the case with
 * {@code BusinessMapping}, which connects four different BuildingBlocks. In this case, the path in
 * this extension leads to the type where the path splits up. The paths going out from that end
 * point are described as further extensions and are contained in the
 * {@link #getSecondStepExtensions()} list.
 */
public class Extension implements IPresentationExtension, Cloneable {

  /** Serialization version. */
  private static final long          serialVersionUID        = 7400556689753141941L;

  private static final Logger        LOGGER                  = Logger.getIteraplanLogger(Extension.class);

  private String                     name;

  private String                     nameKeyForPresentation;

  private String                     permissionKey;

  /**
   * List of TypeWithJoinProperty instances that describe a path from one type to another. The first
   * entry in the list is the first edge and node on the path to the target type, the second entry
   * the second edge and node, etc.
   */
  private List<TypeWithJoinProperty> typesWithJoinProperties = new ArrayList<TypeWithJoinProperty>();

  /**
   * Describes several paths going out from the type that is reached by the path described in
   * {@link #typesWithJoinProperties}. When a path splits up and reaches several types, the paths
   * are described in this list. This list is only filled for complex paths and is usually empty.
   */
  private List<Extension>            secondStepExtensions    = new ArrayList<Extension>();

  /**
   * Constructor creating a complex Extension for a single TypeWithJoinProperty instance and a list
   * of second step Extension instances.
   * 
   * @param name
   *          A unique name for the Extension. Only used internally.
   * @param nameKeyForPresentation
   *          The I18N String that can be used by the GUI to display this extension.
   * @param typeWithJoinProperty
   *          The simple one edge path from one type to another.
   * @param secondStepExtensions
   *          The paths going out from type contained in typeWithJoinProperty
   */
  public Extension(String name, String nameKeyForPresentation, TypeWithJoinProperty typeWithJoinProperty, List<Extension> secondStepExtensions) {
    this(name, nameKeyForPresentation, typeWithJoinProperty);
    this.secondStepExtensions = secondStepExtensions;
  }

  /**
   * Constructor creating a complex Extension for list of TypeWithJoinProperty instance and a list
   * of second step Extension instances.
   * 
   * @param name
   *          A unique name for the Extension. Only used internally.
   * @param nameKeyForPresentation
   *          The I18N String that can be used by the GUI to display this extension.
   * @param typesWithJoinProperties
   *          The path from one type to another.
   * @param secondStepExtensions
   *          The paths going out from type contained in typeWithJoinProperty
   */
  public Extension(String name, String nameKeyForPresentation, List<TypeWithJoinProperty> typesWithJoinProperties,
      List<Extension> secondStepExtensions) {
    this(name, nameKeyForPresentation, typesWithJoinProperties);
    this.secondStepExtensions = secondStepExtensions;
  }

  /**
   * Constructor creating a simple Extension for a list of TypeWithJoinProperty instances.
   * 
   * @param name
   *          A unique name for the Extension. Only used internally.
   * @param nameKeyForPresentation
   *          The I18N String that can be used by the GUI to display this extension.
   * @param typesWithJoinProperties
   *          The path from one type to another.
   */
  public Extension(String name, String nameKeyForPresentation, List<TypeWithJoinProperty> typesWithJoinProperties) {

    this(name, nameKeyForPresentation, nameKeyForPresentation, typesWithJoinProperties);
  }

  /**
   * Constructor creating a simple Extension for a list of TypeWithJoinProperty instances.
   * 
   * @param name
   *          A unique name for the Extension. Only used internally.
   * @param nameKeyForPresentation
   *          The I18N String that can be used by the GUI to display this extension.
   * @param permissionKey
   *          String used to check permissions for this extension, in case it's different from {@code nameKeyForPresentation}
   * @param typesWithJoinProperties
   *          The path from one type to another.
   */
  public Extension(String name, String nameKeyForPresentation, String permissionKey, List<TypeWithJoinProperty> typesWithJoinProperties) {

    Preconditions.checkContentsNotNull(typesWithJoinProperties, "parameter must not be null/ list must not contain null elements");
    this.name = name;
    this.nameKeyForPresentation = nameKeyForPresentation;
    this.permissionKey = permissionKey;
    this.typesWithJoinProperties = typesWithJoinProperties;
  }

  /**
   * Constructor creating a simple Extension for a single TypeWithJoinProperty instance.
   * 
   * @param name
   *          A unique name for the Extension. Only used internally.
   * @param nameKeyForPresentation
   *          The I18N String that can be used by the GUI to display this extension.
   * @param permissionKey
   *          String used to check permissions for this extension, in case it's different from {@code nameKeyForPresentation}
   * @param typeWithJoinProperty
   *          The simple one edge path from one type to another.
   */
  public Extension(String name, String nameKeyForPresentation, String permissionKey, TypeWithJoinProperty typeWithJoinProperty) {
    this(name, nameKeyForPresentation, permissionKey, Arrays.asList(typeWithJoinProperty));
  }

  /**
   * Constructor creating a simple Extension for a single TypeWithJoinProperty instance.
   * 
   * @param name
   *          A unique name for the Extension. Only used internally.
   * @param nameKeyForPresentation
   *          The I18N String that can be used by the GUI to display this extension.
   * @param typeWithJoinProperty
   *          The simple one edge path from one type to another.
   */
  public Extension(String name, String nameKeyForPresentation, TypeWithJoinProperty typeWithJoinProperty) {
    this(name, nameKeyForPresentation, nameKeyForPresentation, Arrays.asList(typeWithJoinProperty));
  }

  public String getName() {
    return name;
  }

  public String getNameKeyForPresentation() {
    return nameKeyForPresentation;
  }

  public String getPermissionKey() {
    return permissionKey;
  }

  /**
   * @return A path between two types.
   */
  public List<TypeWithJoinProperty> getTypesWithJoinProperties() {
    return typesWithJoinProperties;
  }

  void setTypesWithJoinProperties(List<TypeWithJoinProperty> properties) {
    this.typesWithJoinProperties = properties;
  }

  /**
   * @return the number of associations to be followed. This does not include the distances of the
   *         extensions in {@link #secondStepExtensions}.
   */
  public int getLeafDistance() {
    return typesWithJoinProperties.size();
  }

  /**
   * @return The association name which represents the last edge in the path described in
   *         {@link #typesWithJoinProperties}.
   */
  public String getLeafTypeJoinProperty() {
    return getTypeWithJoinProperty(getLeafDistance() - 1).getAssociationName();
  }

  /**
   * Returns an intermediary Type on the path described in {@link #typesWithJoinProperties}.
   * 
   * @param index
   *          The index of the type on the path.
   * @return The type that has the given index on the path.
   */
  public Type<? extends BuildingBlock> getIntermediaryType(int index) {
    return getTypeWithJoinProperty(index).getType();
  }

  /**
   * Returns an intermediary edge on the path described in {@link #typesWithJoinProperties}. The
   * edge is represented by an association name leading to the next type.
   * 
   * @param index
   *          The index of the edge on the path.
   * @return an association name on the way from result type to leaf type.
   */
  public String getIntermediaryTypeJoinProperty(int index) {
    return getTypeWithJoinProperty(index).getAssociationName();
  }

  /**
   * @param index
   *          The index of the Type on the path.
   * @return true, iff intermediary TypeWithJoinProperty is linked using a multi-end association.
   */
  public boolean isIntermediaryTypeMultiEnd(int index) {
    return getTypeWithJoinProperty(index).returnsMultipleEntities();
  }

  /**
   * Returns the TypeWithJoinProperty for the given index.
   * 
   * @param index
   *          The index of the type with property.
   * @return The respective TypeWithJoinProperty.
   */
  public TypeWithJoinProperty getTypeWithJoinProperty(int index) {
    return typesWithJoinProperties.get(index);
  }

  /**
   * Starting from a given element, the elements which are reached by following the path described
   * in {@link #typesWithJoinProperties} are returned. Intermediary elements are not returned, which
   * means that the returned elements are of the type as described by {@link #getRequestedType()}.
   * 
   * @param element
   *          The building block element for which associated elements.
   * @return The elements associated over the described path.
   */
  public Set<BuildingBlock> getConditionElements(Object element) {
    Set<BuildingBlock> conditionElements;
    if (getTypeWithJoinProperty(0) != null) {
      String joinProperty = getTypeWithJoinProperty(0).getAssociationName();
      conditionElements = getNeighborElements(element, joinProperty);
      for (int i = 1; i < getLeafDistance(); i++) {
        Set<BuildingBlock> allNeighborElements = new HashSet<BuildingBlock>();
        joinProperty = getTypeWithJoinProperty(i).getAssociationName();
        for (BuildingBlock neighbor : conditionElements) {
          Set<BuildingBlock> neighborElements = getNeighborElements(neighbor, joinProperty);
          allNeighborElements.addAll(neighborElements);
        }
        conditionElements = allNeighborElements;
      }
      return conditionElements;
    }
    else {
      return new HashSet<BuildingBlock>();
    }
  }

  /**
   * Retrieves the set of associated elements by calling a given getter method.
   * 
   * @param element
   *          A building block element.
   * @param joinProperty
   *          The name of the property of the element which holds the neighbors. The String will be
   *          converted into a corresponding getter method which will then be called.
   * @throws IteraplanTechnicalException
   *           if the joinProperty could not be evaluated, typically a Getter could not be called
   *           reflectively
   */
  @SuppressWarnings("unchecked")
  private Set<BuildingBlock> getNeighborElements(Object element, String joinProperty) {
    Set<BuildingBlock> neighborSet = new HashSet<BuildingBlock>();
    String getterName = createGetterFromJoinProperty(joinProperty);
    try {
      Class<?> elementClass = element.getClass();
      Method neighborGetter = elementClass.getMethod(getterName);
      Object neighbor = neighborGetter.invoke(element);
      if (neighbor instanceof Set<?>) {
        neighborSet = (Set<BuildingBlock>) neighbor;
      }
      else {
        neighborSet.add((BuildingBlock) neighbor);
      }
    } catch (NoSuchMethodException ex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    } catch (IllegalArgumentException ex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    } catch (IllegalAccessException ex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    } catch (InvocationTargetException ex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    }
    return neighborSet;
  }

  public Type<? extends BuildingBlock> getRequestedType() {
    if (typesWithJoinProperties == null || typesWithJoinProperties.isEmpty()) {
      return null;
    }
    TypeWithJoinProperty twjp = getTypeWithJoinProperty(getLeafDistance() - 1);
    return twjp.getType();
  }

  /**
   * Returns the name of the getter method for a given field name.
   * 
   * @param joinProperty
   *          The name of the field.
   * @return The name of the getter method of the given
   */
  protected String createGetterFromJoinProperty(String joinProperty) {
    StringBuilder buf = new StringBuilder();
    buf.append("get");
    buf.append(joinProperty.substring(0, 1).toUpperCase());
    buf.append(joinProperty.substring(1));
    return buf.toString();
  }

  /**
   * An association type is a type that merely connects types. This method finds the last type in
   * the path described by this extension.
   * 
   * @return -1 if no association types are contained in the extension. Otherwise the last index of
   *         the association type in the extension is returned.
   * @see Type#isAssociationType()
   */
  public int getLastAssociationTypeIndex() {
    int index = -1;
    int count = 0;
    for (Iterator<TypeWithJoinProperty> it = typesWithJoinProperties.iterator(); it.hasNext(); count++) {
      TypeWithJoinProperty twjp = it.next();
      if (twjp.getType().isAssociationType()) {
        index = count;
      }
    }
    return index;
  }

  public boolean isWithAssociationType() {
    if (getLastAssociationTypeIndex() >= 0) {
      LOGGER.debug("Association type found in query extension. " + "At least one type within the extension is marked as an association type.");
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    for (Iterator<TypeWithJoinProperty> it = typesWithJoinProperties.iterator(); it.hasNext();) {
      TypeWithJoinProperty twjp = it.next();
      s.append(twjp.getAssociationName());
      s.append(' ');
      s.append(twjp.getType().getTypeNameDB());
      if (it.hasNext()) {
        s.append(' ');
      }
    }
    return s.toString();
  }

  public List<Extension> getSecondStepExtensions() {
    return secondStepExtensions;
  }

  void setSecondStepExtensions(List<Extension> extensions) {
    this.secondStepExtensions = extensions;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNameKeyForPresentation(String nameKeyForPresentation) {
    this.nameKeyForPresentation = nameKeyForPresentation;
  }

  public void checkDeepPermission() {
    Iterator<Extension> it = getSecondStepExtensions().iterator();

    while (it.hasNext()) {
      Extension extension = it.next();
      if (!(PermissionHelper.hasPermissionFor(extension.getPermissionKey()))) {
        it.remove();
      }
      else {
        extension.checkDeepPermission();
      }
    }
  }

  @Override
  public IPresentationExtension clone() throws CloneNotSupportedException {
    Object clonedObject = super.clone();

    if (clonedObject instanceof Extension) {
      Extension clonedExtension = (Extension) clonedObject;
      clonedExtension.setSecondStepExtensions(new ArrayList<Extension>());

      if (getSecondStepExtensions() != null) {
        for (Extension secondExtensionToClone : getSecondStepExtensions()) {
          Extension extensionClone = (Extension) secondExtensionToClone.clone();
          clonedExtension.getSecondStepExtensions().add(extensionClone);
        }
      }

      clonedExtension.setTypesWithJoinProperties(new ArrayList<TypeWithJoinProperty>());
      for (TypeWithJoinProperty propertyToClone : getTypesWithJoinProperties()) {
        TypeWithJoinProperty propertyClone = new TypeWithJoinProperty(propertyToClone.getAssociationName(), propertyToClone.getType(),
            propertyToClone.returnsMultipleEntities());
        clonedExtension.getTypesWithJoinProperties().add(propertyClone);
      }

      return clonedExtension;
    }
    else {
      // should not happen
      throw new CloneNotSupportedException();
    }
  }
}
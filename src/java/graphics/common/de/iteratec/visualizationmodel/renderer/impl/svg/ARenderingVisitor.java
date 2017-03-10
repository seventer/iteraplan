/*
 * Copyright 2011 Christian M. Schweda & iteratec
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iteratec.visualizationmodel.renderer.impl.svg;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.visualizationmodel.ASymbol;
import de.iteratec.visualizationmodel.renderer.AVisualizationObjectVisitor;


/**
 * Base class for all rendering visitors. Subclasses must implement the
 * <code>setAttribute</code> appropriately.
 * 
 * @param <S> The type of the symbols created by the visitor.
 *  
 * @author Christian M. Schweda
 * 
 * @version 6.0
 */
public abstract class ARenderingVisitor<S, OUTPUT> extends AVisualizationObjectVisitor {

  private static final Logger                  LOGGER = Logger.getLogger(ARenderingVisitor.class);

  private Map<PropertyDescriptor, ISerializer> attributeSerializerMap;

  protected ARenderingVisitor() {
    this.attributeSerializerMap = new HashMap<PropertyDescriptor, ISerializer>();
  }

  /**
   * Method for accessing the result of the rendering.
   * 
   * @return The result of the rendering.
   */
  public abstract S getResult();

  /**
   * Add a serializer for the specified attribute.
   * 
   * @param attributeDescriptor The name of the attribute.
   * @param serializer The serializer to be used.
   */
  public void addAttributeSerializer(PropertyDescriptor attributeDescriptor, ISerializer serializer) {
    this.attributeSerializerMap.put(attributeDescriptor, serializer);
  }

  private ISerializer findAttributeSerializer(PropertyDescriptor attributeDescriptor) {
    try {
      if (attributeSerializerMap.containsKey(attributeDescriptor)) {
        return attributeSerializerMap.get(attributeDescriptor);
      }
      else {
        Class<?> clazz = attributeDescriptor.getReadMethod().getDeclaringClass();
        // If we are not already at the root level ... (superclass of Object is null)
        if (clazz.getSuperclass() != null) {
          // Search for a serializer registered for a superclass
          return findAttributeSerializer(new PropertyDescriptor(attributeDescriptor.getName(), clazz.getSuperclass()));
        }
      }
    } catch (IntrospectionException ignored) { /* Ignore */
    }
    return null;
  }

  /**
   * Returns the serialized representation of the given source data.
   * 
   * @param attributeDescriptor The attribute name.
   * @param value The value of the attribute.
   * 
   * @return the transformed object.
   */
  private Object serializeAttribute(PropertyDescriptor attributeDescriptor, Object value) throws SerializationException {
    ISerializer serializer = findAttributeSerializer(attributeDescriptor);
    if (serializer != null) {
      return serializer.transform(value);
    }
    else {
      return value;
    }
  }

  /**
   * Iterates over all graphical attributes of a symbol and calls setAttribute with
   * the container, the attribute name and the attribute value.
   * 
   * @param sourceObject The object, of which the attributes should be iterated.
   * @param serializedObject The serialized object to set the attributes at.
   */
  protected void setAllAttributes(ASymbol sourceObject, OUTPUT serializedObject) {
    String errorMessage = "Error invoking a bean-method.";

    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(sourceObject.getClass());
      PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
      if (descriptors != null) {
        for (PropertyDescriptor descriptor : descriptors) {
          // Check if Property Methods stick to the Bean Specification (get...(), set...())
          if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null) {
            Object propertyValue = descriptor.getReadMethod().invoke(sourceObject);
            Object serializedValue = serializeAttribute(descriptor, propertyValue);
            //Object serializedValue = serializeAttribute(sourceObject.getClass(), descriptor.getName(), propertyValue);
            setAttribute(serializedObject, descriptor.getName(), serializedValue);
          }
        }
      }
    } catch (IntrospectionException e) {
      LOGGER.warn(errorMessage, e);
    } catch (IllegalArgumentException e) {
      LOGGER.warn(errorMessage, e);
    } catch (IllegalAccessException e) {
      LOGGER.warn(errorMessage, e);
    } catch (InvocationTargetException e) {
      LOGGER.warn(errorMessage, e);
    } catch (SerializationException e) {
      LOGGER.warn("Error while serializing an attribute.", e);
    }
  }

  /**
   * Set the attribute called attributeName on serializedObject with value
   * value. Is called from setAllAttributes.
   * 
   * @param serializedObject The object to set the attribute on.
   * @param attributeName The name of the attribute to set.
   * @param value The new value to set the attribute to.
   */
  protected abstract void setAttribute(OUTPUT serializedObject, String attributeName, Object value);
}
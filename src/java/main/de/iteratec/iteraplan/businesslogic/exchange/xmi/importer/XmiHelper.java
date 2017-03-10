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
package de.iteratec.iteraplan.businesslogic.exchange.xmi.importer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.Diagnostician;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.interfaces.IdEntity;


/**
 * A helper class providing the methods needed for XMI import.
 */
public final class XmiHelper {
  private static final Logger LOGGER           = Logger.getIteraplanLogger(XmiHelper.class);
  private static final String IGNORE_CASE_AVAS = "getAttributeValueAssignments";
  private static final String ID_SEPARATOR     = "_";

  private XmiHelper() {
    //prevent instances of this class
  }

  /**
   * Returns the entity id out of the specified {@code eObject}. The id must have
   * the following pattern - {@code 'type_id'}. For example {@code 'Transport_330'}.
   * 
   * @param eObject the eObject instance to get the id from
   * @return the id or {@code null}, if the id cannot be parsed
   */
  public static Integer getEId(EObject eObject) {
    String eIdString = getEIdString(eObject);
    if (StringUtils.isEmpty(eIdString)) {
      return null;
    }

    try {
      if (eIdString.contains(ID_SEPARATOR)) {
        String numberValue = StringUtils.substringAfter(eIdString, ID_SEPARATOR);
        return Integer.valueOf(numberValue);
      }
      return Integer.valueOf(eIdString);
    } catch (NumberFormatException e) {
      LOGGER.error("Cannot resolve id: " + eIdString, e);
      return null;
    }
  }

  private static String getEIdString(EObject eInstance) {
    EAttribute eIdAttribute = eInstance.eClass().getEIDAttribute();
    if (eIdAttribute == null || eInstance.eGet(eIdAttribute) == null || eInstance.eGet(eIdAttribute).toString().length() < 1) {
      return null;
    }

    return eInstance.eGet(eIdAttribute).toString();
  }

  public static Object invokeReadMethod(IdEntity instance, EStructuralFeature esf) {
    Method m = getReadMethod(esf, instance.getClass());
    if (m == null || (instance.getClass().equals(AttributeValue.class) && m.getName().equals(IGNORE_CASE_AVAS))) {
      return null;
    }
    try {
      return m.invoke(instance);
    } catch (IllegalArgumentException e) {
      LOGGER.error(e);
    } catch (IllegalAccessException e) {
      LOGGER.error(e);
    } catch (InvocationTargetException e) {
      LOGGER.error(e);
    }

    return null;
  }

  /**
   * Searches the ReadMethod for the given EStructuralFeature
   * 
   * @param esf the given EStructuralFeature
   * @param entityClass the entity class to get the method from
   * @return the corresponding ReadMethod
   */
  private static Method getReadMethod(EStructuralFeature esf, Class<?> entityClass) {
    try {
      String prefix = "get";
      if ((esf instanceof EAttribute) && esf.getEType().getName().startsWith("EBoolean")) {
        prefix = "is";
      }
      String methodName = prefix + StringUtils.capitalize(esf.getName());

      return entityClass.getMethod(methodName);
    } catch (SecurityException e) {
      LOGGER.error(e);
    } catch (NoSuchMethodException e) {
      LOGGER.error(e);
    }

    LOGGER.error("No getMethod for:" + esf.getName());
    return null;
  }

  /**
   * Invokes the setMethod for the given EStructuralFeature
   * 
   * @param instance the IdEntity instance on which the method is invoked
   * @param value the given value which is set
   * @param esf the EStructuralFeature corresponding to the Method
   * @param sessionHelper the helper for creating the {@link Clob} if needed
   */
  public static void invokeWriteMethod(IdEntity instance, Object value, EStructuralFeature esf, SessionHelper sessionHelper) {
    Method m = getWriteMethod(esf, instance.getClass());
    if (m == null || value == null) {
      return;
    }
    try {
      Object argument = value;
      if (m.getParameterTypes()[0].equals(Clob.class)) {
        argument = sessionHelper.createClob(value.toString());
      }

      m.invoke(instance, argument);
    } catch (IllegalArgumentException e) {
      LOGGER.error(e);
    } catch (IllegalAccessException e) {
      LOGGER.error(e);
    } catch (InvocationTargetException e) {
      LOGGER.error(e);
    }
  }

  /**
   * Returns the WriteMethod for the given EStructuralFeature
   * 
   * @param esf the given EStructuralFeature
   * @param entityClass the entity class to get the method from
   * @return the corresponding WriteMethod
   */
  public static Method getWriteMethod(EStructuralFeature esf, Class<? extends IdEntity> entityClass) {
    try {
      String methodName = "set" + StringUtils.capitalize(esf.getName());
      Class<?> firstMethodParameterType = getReadMethod(esf, entityClass).getReturnType();

      return entityClass.getMethod(methodName, firstMethodParameterType);
    } catch (NoSuchMethodException e) {
      LOGGER.error(e);
    } catch (SecurityException e) {
      LOGGER.error(e);
    }

    LOGGER.error("No setMethod for:" + esf.getName());

    return null;
  }

  /**
   * Creates an object for the specified entity class.
   * 
   * @param entityClass the entity class
   * @return a new entity-instance of the corresponding Class
   */
  public static IdEntity getNewObject(Class<? extends IdEntity> entityClass) {
    try {
      return entityClass.getConstructor().newInstance();
    } catch (IllegalArgumentException e) {
      LOGGER.error(e);
    } catch (SecurityException e) {
      LOGGER.error(e);
    } catch (InstantiationException e) {
      LOGGER.error(e);
    } catch (IllegalAccessException e) {
      LOGGER.error(e);
    } catch (InvocationTargetException e) {
      LOGGER.error(e);
    } catch (NoSuchMethodException e) {
      LOGGER.error(e);
    }
    LOGGER.error("Could not create a new instance for " + entityClass);

    return null;
  }

  public static List<String> validateObject(EObject eObject) {
    List<String> errors = Lists.newArrayList();
    Diagnostic diagnostic = Diagnostician.INSTANCE.validate(eObject);

    if (diagnostic.getSeverity() == Diagnostic.ERROR || diagnostic.getSeverity() == Diagnostic.WARNING) {
      List<Diagnostic> children = diagnostic.getChildren();
      for (Diagnostic childDiagnostic : children) {
        if (childDiagnostic.getSeverity() == Diagnostic.ERROR || childDiagnostic.getSeverity() == Diagnostic.WARNING) {
          errors.add(childDiagnostic.getMessage());
        }
      }
    }

    return errors;
  }

  @SuppressWarnings("unchecked")
  public static <T> List<T> castList(List<?> entities) {
    List<T> comparableList = Lists.newArrayList();

    for (Object object : entities) {
      comparableList.add((T) object);
    }

    return comparableList;
  }
}

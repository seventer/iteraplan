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
package de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore.EntityBeanIntrospectionServiceBean;
import de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore.Iteraplan2EMFHelper;
import de.iteratec.iteraplan.common.Logger;


public class EObjectCreatorImpl implements EObjectCreator {
  private static final Logger                      LOGGER               = Logger.getIteraplanLogger(EObjectCreatorImpl.class);
  private static final Set<String>                 SYSTEM_PACKAGE_NAMES = Sets.newHashSet("java.lang", "java.util", "java.math");

  private final SessionFactory                     sessionFactory;
  private final EntityBeanIntrospectionServiceBean introspectionBean;

  private final Map<Object, EObject>               objectToEObject      = Maps.newHashMap();

  public EObjectCreatorImpl(SessionFactory sessionFactory, EntityBeanIntrospectionServiceBean introspectionBean) {
    this.sessionFactory = sessionFactory;
    this.introspectionBean = introspectionBean;
  }

  /**
   * {@inheritDoc}
   */
  public EObject getXmiExportObject() {
    objectToEObject.clear();

    Map<String, EClass> eClasses = collectEClasses();
    Collection<EObject> allEInstances = collectAllEInstances(eClasses);

    EClass eClass = eClasses.get(Iteraplan2EMFHelper.CLASS_NAME);
    EPackage ePackage = eClass.getEPackage();
    EObject eContainer = ePackage.getEFactoryInstance().create(eClass);
    EStructuralFeature eStructuralFeature = eContainer.eClass().getEStructuralFeature(Iteraplan2EMFHelper.EREFERENCE_NAME);
    eContainer.eSet(eStructuralFeature, allEInstances);
    objectToEObject.clear();
    return eContainer;
  }

  private Map<String, EClass> collectEClasses() {
    Map<String, EClass> eClasses = Maps.newLinkedHashMap();

    for (EPackage ePackage : introspectionBean.getEPackages()) {
      for (EClassifier eClassifier : ePackage.getEClassifiers()) {
        if (eClassifier instanceof EClass) {
          eClasses.put(eClassifier.getName(), (EClass) eClassifier);
        }
      }
    }

    return eClasses;
  }

  /**
   * Method which creates EObject instances for all iteraplan-entity-instances
   */
  private Collection<EObject> collectAllEInstances(Map<String, EClass> eClasses) {
    Map<EObject, Object> allEObjects = Maps.newLinkedHashMap();
    Session session = sessionFactory.getCurrentSession();

    for (Entry<String, EClass> entry : eClasses.entrySet()) {
      Map<EObject, Object> eObjects = createInstancesForEClass(entry.getValue(), session);
      allEObjects.putAll(eObjects);
    }

    List<EObject> components = Lists.newArrayList();
    for (Entry<EObject, Object> entry : allEObjects.entrySet()) {
      Collection<EObject> referenceComponents = setEReferences(entry.getKey(), entry.getValue(), eClasses);
      components.addAll(referenceComponents);
    }

    return Lists.newArrayList(Iterables.concat(allEObjects.keySet(), components));
  }

  @SuppressWarnings("unchecked")
  private Map<EObject, Object> createInstancesForEClass(EClass eClassifier, Session session) {
    Map<EObject, Object> result = Maps.newLinkedHashMap();

    String name = eClassifier.getName();
    boolean canBeExported = !(eClassifier.isAbstract() || Iteraplan2EMFHelper.CLASS_NAME.equals(name) || "RuntimePeriod".equals(name));

    if (canBeExported) {
      final Query query = session.createQuery("select xx from " + name + " xx");
      Iterator<Object> iterator = query.iterate();
      while (iterator.hasNext()) {
        Object instance = iterator.next();
        EObject eObject = createEObject(eClassifier, instance);
        result.put(eObject, instance);
        objectToEObject.put(instance, eObject);
      }
    }

    return result;
  }

  /**
   * Method to create an EObject for the actual instance and sets all EAttributes of the
   * corresponding EClass
   * 
   * @param eClass
   *          The corresponding {@link EClass} of the instanceClass
   * @param instanceClass
   *          The {@link Class} of the instance
   * @param instance
   *          The corresponding {@link Object} of the created EObject
   */
  private EObject createEObject(EClass eClass, Object instance) {
    Class<?> instanceClass = instance.getClass();
    EFactory eFactory = eClass.getEPackage().getEFactoryInstance();
    EObject eInstance = eFactory.create(eClass);

    for (EAttribute eAttribute : eClass.getEAllAttributes()) {
      try {
        String prefix = "get";
        if (eAttribute.getEType().getName().startsWith("EBoolean")) {
          prefix = "is";
        }

        final Method method = instanceClass.getMethod(prefix + capitalize(eAttribute.getName()));

        if (eAttribute.getEType() instanceof EEnum) {
          ensureEEnum(eAttribute);
        }
        if (eAttribute.isID()) {
          eInstance.eSet(eAttribute, eClass.getName() + "_" + method.invoke(instance));
        }
        else {
          Object eObjectFor = eObjectFor(method.invoke(instance));
          eInstance.eSet(eAttribute, eObjectFor);
        }

      } catch (IllegalArgumentException e) {
        LOGGER.error("IllegalArgumentException thrown by gettingMethod for " + eClass.getName() + "." + eAttribute.getName());
      } catch (SecurityException e) {
        LOGGER.error("SecurityException caused by " + eClass.getName() + "." + eAttribute.getName());
      } catch (NoSuchMethodException e) {
        LOGGER.error("No getter-method for " + eClass.getName() + "." + eAttribute.getName() + " found!");
      } catch (IllegalAccessException e) {
        LOGGER.error("IllegalAccessException caused by " + eClass.getName() + "." + eAttribute.getName());
      } catch (InvocationTargetException e) {
        LOGGER.error("InocationTargetException caused by " + eClass.getName() + "." + eAttribute.getName());
      }
    }

    return eInstance;
  }

  /**
   * Method to set all eReferences of the actual eInstance
   * 
   * @param eClass
   *          The corresponding {@link EClass} of the instanceClass
   * @param eInstance
   *          The eObject instance which will be edited
   * @param instanceClass
   *          The {@link Class} of the instance
   * @param instance
   *          The corresponding {@link Object} of the edited EObject
   */
  private Collection<EObject> setEReferences(EObject eInstance, Object instance, Map<String, EClass> eClasses) {
    Collection<EObject> result = Lists.newArrayList();
    EClass eClass = eInstance.eClass();
    Class<?> instanceClass = instance.getClass();

    for (EReference eReference : eClass.getEAllReferences()) {
      String referenceName = eReference.getName();
      try {
        Method method = instanceClass.getMethod("get" + capitalize(referenceName));
        if ((!StringUtils.equals(eClass.getName(), "AttributeValue") && !StringUtils.equals(referenceName, "attributeValueAssignments"))) {
          if (isComponent(eReference)) {
            Object invoke = method.invoke(instance);

            if (invoke != null) {
              EObject componentEObject = createEObjectForComponent(invoke, eClasses);
              result.add(componentEObject);
              eInstance.eSet(eReference, componentEObject);
            }
          }
          else {
            eInstance.eSet(eReference, eObjectFor(method.invoke(instance)));
          }
        }
      } catch (NoSuchMethodException e) {
        LOGGER.error("no getter-Method for Reference " + eClass.getName() + "." + referenceName + " found!");
      } catch (IllegalArgumentException e) {
        LOGGER.error("getter-Method for Reference " + eClass.getName() + "." + referenceName + " used with IllegalArgument!");
      } catch (IllegalAccessException e) {
        LOGGER.error("IllegalAccessException caused by " + eClass.getName() + "." + referenceName);
      } catch (InvocationTargetException e) {
        LOGGER.error("InvocationTargetException caused by getter-Method for " + eClass.getName() + "." + referenceName);
      }
    }

    return result;
  }

  private boolean isComponent(EReference eReference) {
    return capitalize(eReference.getName()).equals("RuntimePeriod");
  }

  /**
   * Creates an {@link EObject} for a component-entity (at the moment only for
   * {@link de.iteratec.iteraplan.model.RuntimePeriod RuntimePeriod}-instances
   * 
   * @param component
   *          The Component instance
   * @return The corresponding EObjectInstance
   */
  private EObject createEObjectForComponent(Object component, Map<String, EClass> eClasses) {
    String simpleName = component.getClass().getSimpleName();
    EPackage ePackage = eClasses.get(simpleName).getEPackage();
    EFactory eFactoryInstance = ePackage.getEFactoryInstance();
    EClass eClass = eClasses.get(simpleName);
    EObject result = eFactoryInstance.create(eClass);

    for (EStructuralFeature esf : eClasses.get(component.getClass().getSimpleName()).getEAllStructuralFeatures()) {
      if (StringUtils.equals(esf.getName(), "id")) {
        result.eSet(esf, EcoreUtil.generateUUID());
        continue;
      }

      try {
        Method componentMethod = component.getClass().getMethod("get" + capitalize(esf.getName()));
        Object value = componentMethod.invoke(component);
        result.eSet(esf, value);
      } catch (IllegalArgumentException e) {
        LOGGER.error("getting-Method for Reference " + component.getClass().getSimpleName() + "." + esf.getName() + " used with IllegalArgument!");
      } catch (SecurityException e) {
        LOGGER.error("SecurityException caused by " + component.getClass().getSimpleName() + "." + esf.getName());
      } catch (NoSuchMethodException e) {
        LOGGER.error("no getting-Method for Reference " + component.getClass().getSimpleName() + "." + esf.getName() + " found!");
      } catch (IllegalAccessException e) {
        LOGGER.error("IllegalAccessException caused by " + component.getClass().getSimpleName() + "." + esf.getName());
      } catch (InvocationTargetException e) {
        LOGGER.error("InvocationTargetException caused by " + component.getClass().getSimpleName() + "." + esf.getName());
      }
    }

    return result;
  }

  /**
   * Method which returns the emf-value for the given Object
   * 
   * @return The emf-value for the given object
   */
  private Object eObjectFor(Object object) {
    Object value = object;
    if (value == null) {
      return null;
    }

    if (value instanceof Iterable) {
      EList<Object> result = new UniqueEList<Object>();
      for (Object entry : (Iterable<?>) value) {
        if (entry != null) {
          result.add(eObjectFor(entry));
        }
      }
      return result;
    }

    if (value instanceof Clob) {
      return getClobContent((Clob) value);
    }

    String packageName = value.getClass().getPackage().getName();
    if (SYSTEM_PACKAGE_NAMES.contains(packageName)) {
      return value;
    }

    // the Date stored in the DateAV-instances retrieved from the database is of type java.sql.Date
    // and is therefore skipped when looking for java.util in SYSTEM_PACKAGE_NAMES 
    if (value instanceof java.sql.Date) {
      return new java.util.Date(((java.sql.Date) value).getTime());
    }

    if (value.getClass().equals(Timestamp.class)) {
      return value;
    }

    if (value.getClass().isEnum()) {
      value = value.toString();
    }

    return objectToEObject.get(value);
  }

  /**
   * Returns the content of the specified {@code clob} and returns it. 
   * 
   * @param clob the clob object
   * @return the content or {@code null}, if the content cannot be read
   */
  private Object getClobContent(Clob clob) {
    try {
      Reader characterStream = clob.getCharacterStream();
      return IOUtils.toString(characterStream);
    } catch (IOException e) {
      LOGGER.error("An unexpected error occurred reading the clob", e);
    } catch (SQLException e) {
      LOGGER.error("An unexpected error occurred reading the clob", e);
    }

    return null;
  }

  /**
   * Method which ensures that the eObject for the given enumeration-value is available
   * 
   * @param eAttribute
   *          The {@link EAttribute }
   */
  @SuppressWarnings("cast")
  private void ensureEEnum(EAttribute eAttribute) {
    if (!objectToEObject.containsKey((EEnum) (eAttribute.getEType()))) {
      EEnum eEnum = (EEnum) eAttribute.getEType();
      objectToEObject.put(eEnum.getName(), eEnum);

      for (EEnumLiteral eLiteral : eEnum.getELiterals()) {
        objectToEObject.put(eLiteral.getLiteral(), eLiteral);
      }
    }
  }

  /**
   * Returns the captialized String for the given attributeName used to concatenate method-names
   * (e.g. "get" + capitalize(attributeName))
   * 
   * @param attributeName
   *          a string
   * @return the capitalized String
   */
  private String capitalize(String attributeName) {
    return StringUtils.capitalize(attributeName);
  }
}

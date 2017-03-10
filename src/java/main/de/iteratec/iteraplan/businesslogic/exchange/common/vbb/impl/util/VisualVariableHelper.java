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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util;

import java.beans.PropertyDescriptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.hibernate.mapping.Collection;
import org.springframework.core.annotation.AnnotationUtils;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.VBB;
import de.iteratec.iteraplan.common.Logger;


/**
 * Helper class for setting and getting visual variables to beanMaps.
 */
public final class VisualVariableHelper {

  private static final Logger LOGGER = Logger.getIteraplanLogger(VisualVariableHelper.class);

  private VisualVariableHelper() {
    //Do not instantiate
  }

  /**
   * Sets the value of a given visual variable to a beanMap. If no such variable exists or the variable is null, then no value is set.
   * @param target the beanMap to which the variable should be set.
   * @param source the instance holding the values of the visual variables.
   * @param vv the visual variable to set to the beanMap.
   */
  @SuppressWarnings("unchecked")
  public static void setVisualVariableValue(BeanMap target, EObject source, EAttribute vv) {
    if (source.eIsSet(vv) && source.eGet(vv) != null && target.containsKey(vv.getName())) {
      Object value = source.eGet(vv);
      //TODO handle multivalued attributes here
      if (vv.getEAttributeType() instanceof EEnum) {
        Class<Enum<?>> enumType = (Class<Enum<?>>) ((EEnum) vv.getEAttributeType()).getInstanceClass();
        for (Enum<?> ec : enumType.getEnumConstants()) {
          if (ec.name().equals(((EEnumLiteral) value).getName())) {
            value = ec;
          }
        }
      }
      target.put(vv.getName(), value);
    }
  }

  /**
   * Derives all visual variables from a given class and adds the variables as EAttributes to the supplied EClass.
   * @param vbbClass the class that defines the visual variables via annotated methods {@link VisualVariable}
   * @param eClass the EClass to which the introspected variables should be added as EAttributes
   */
  public static void addAllVisualVariables(Class<? extends VBB> vbbClass, EClass eClass) {
    for (PropertyDescriptor vvCandidate : PropertyUtils.getPropertyDescriptors(vbbClass)) {
      VisualVariable vvAnnotation = getVVAnnotation(vvCandidate);
      if (vvAnnotation != null) {
        EAttribute att = EcoreFactory.eINSTANCE.createEAttribute();
        att.setName(vvCandidate.getName());
        att.setLowerBound(vvAnnotation.mandatory() ? 1 : 0);
        setMultiplicityAndDataType(eClass.getEPackage(), att, vvCandidate);
        if (att.getEType() != null) {
          eClass.getEStructuralFeatures().add(att);
        }
        else {
          LOGGER.warn("Could not introspect type for field " + vvCandidate.getName() + ".");
        }
      }
    }
  }

  private static VisualVariable getVVAnnotation(PropertyDescriptor vvCandidate) {
    VisualVariable vvAnnotation = null;
    if (vvCandidate.getReadMethod() != null) {
      vvAnnotation = AnnotationUtils.findAnnotation(vvCandidate.getReadMethod(), VisualVariable.class);
    }
    if (vvAnnotation == null && vvCandidate.getWriteMethod() != null) {
      vvAnnotation = AnnotationUtils.findAnnotation(vvCandidate.getWriteMethod(), VisualVariable.class);
    }
    return vvAnnotation;
  }

  @SuppressWarnings("unchecked")
  private static void setMultiplicityAndDataType(EPackage vvEPackage, EAttribute att, Type type) {
    if (type instanceof ParameterizedType) {
      setMultiplicityAndDataType(vvEPackage, att, ((ParameterizedType) type).getActualTypeArguments()[0]);
    }
    else if (type instanceof GenericArrayType) {
      setMultiplicityAndDataType(vvEPackage, att, ((GenericArrayType) type).getGenericComponentType());
    }
    else if (type instanceof Class) {
      Class<?> clazz = (Class<?>) type;
      if (clazz.isEnum()) {
        att.setEType(getEEnum(vvEPackage, (Class<? extends Enum<?>>) clazz));
      }
      else {
        att.setEType(getEDataType(vvEPackage, clazz));
      }
    }
  }

  private static EClassifier getEEnum(EPackage vvEPackage, Class<? extends Enum<?>> enumClass) {
    if (vvEPackage.getEClassifier(enumClass.getSimpleName()) == null) {
      EEnum result = EcoreFactory.eINSTANCE.createEEnum();
      result.setName(enumClass.getSimpleName());
      result.setInstanceClass(enumClass);
      for (Enum<?> enumValue : enumClass.getEnumConstants()) {
        EEnumLiteral lit = EcoreFactory.eINSTANCE.createEEnumLiteral();
        lit.setValue(enumValue.ordinal());
        lit.setName(enumValue.name());
        result.getELiterals().add(lit);
      }
      vvEPackage.getEClassifiers().add(result);
    }
    return vvEPackage.getEClassifier(enumClass.getSimpleName());
  }

  private static void setMultiplicityAndDataType(EPackage vvEPackage, EAttribute att, PropertyDescriptor pd) {
    if (Collection.class.isAssignableFrom(pd.getPropertyType()) || pd.getPropertyType().isArray()) {
      att.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
      setMultiplicityAndDataType(vvEPackage, att, pd.getReadMethod().getGenericReturnType());
    }
    else {
      att.setUpperBound(1);
      setMultiplicityAndDataType(vvEPackage, att, pd.getPropertyType());
    }
  }

  private static EClassifier getEDataType(EPackage vvEPackage, Class<?> clazz) {
    for (EClassifier eClassifier : EcorePackage.eINSTANCE.getEClassifiers()) {
      if (eClassifier instanceof EDataType && eClassifier.getInstanceClass().equals(clazz)) {
        return eClassifier;
      }
    }
    if (vvEPackage.getEClassifier(clazz.getSimpleName()) == null) {
      try {
        if (clazz.getConstructor(String.class) != null) {
          EDataType dataType = EcoreFactory.eINSTANCE.createEDataType();
          dataType.setName(clazz.getSimpleName());
          dataType.setInstanceClass(clazz);
          vvEPackage.getEClassifiers().add(dataType);
        }
      } catch (SecurityException e) {
        LOGGER.error("Could not convert class to EDataType " + clazz.getName() + ".", e);
      } catch (NoSuchMethodException e) {
        LOGGER.error("Could not convert for class to EDataType " + clazz.getName() + ".", e);
      }
    }
    return vvEPackage.getEClassifier(clazz.getSimpleName());
  }

  /**
   * Annotation for designating that a method is accessor for a visual variable that should be introspected.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ ElementType.METHOD })
  public static @interface VisualVariable {
    boolean mandatory() default true;
  }

  /**
   * Creates an empty package for EClasses specifying visual variables. Sets the EFactory of the EPackage
   * to an introspective factory that uses string-based constructors and toString()-methods for serializing
   * and deserializing values of EDataTypes.
   *  
   * @param viewpointName the name of the viewpoint for which the EPackage is used.
   * @return the empty package for EClass specifying visual variables.
   */
  public static EPackage createVisualVariableEPackage(String viewpointName) {
    EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
    pkg.setName("vv4" + viewpointName);
    pkg.setNsPrefix("vv");
    pkg.setNsURI("urn:vv4" + viewpointName);
    pkg.setEFactoryInstance(new IntrospectiveEFactoryImpl());
    return pkg;
  }

  private static class IntrospectiveEFactoryImpl extends EFactoryImpl {

    /**{@inheritDoc}**/
    @Override
    public Object createFromString(EDataType eDataType, String stringValue) {
      try {
        return eDataType.getInstanceClass().getConstructor(String.class).newInstance(stringValue);
      } catch (IllegalArgumentException e) {
        LOGGER.error(e.getMessage(), e);
        return null;
      } catch (SecurityException e) {
        LOGGER.error(e.getMessage(), e);
        return null;
      } catch (InstantiationException e) {
        LOGGER.error(e.getMessage(), e);
        return null;
      } catch (IllegalAccessException e) {
        LOGGER.error(e.getMessage(), e);
        return null;
      } catch (InvocationTargetException e) {
        LOGGER.error(e.getMessage(), e);
        return null;
      } catch (NoSuchMethodException e) {
        LOGGER.error(e.getMessage(), e);
        return null;
      }
    }

    /**{@inheritDoc}**/
    @Override
    public String convertToString(EDataType eDataType, Object objectValue) {
      return objectValue.toString();
    }
  }
}

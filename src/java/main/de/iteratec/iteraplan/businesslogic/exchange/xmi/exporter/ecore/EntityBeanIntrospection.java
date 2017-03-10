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
/*
# * iteraplan is an IT Governance web application developed by iteratec, GmbH
 * Copyright (C) 2008 iteratec, GmbH
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
 * You can contact iteratec GmbH headquarters at Inselkammerstraße 4
 * 82008 München - Unterhaching, Germany, or at email address info@iteratec.de.
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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.RuntimePeriod;


/**
 * Responsible for the creation of {@link EPackage}s and their {@link EClassifier}s
 * for the iteraplan entity classes
 */
class EntityBeanIntrospection {

  public static final String              PACKAGE_NAME   = "model";
  public static final Map<String, String> OPPOSITE_NAMES = initOppositeNames();

  private Map<String, HbMappedClass>      hbClassData;
  private Map<Class<?>, EClassifier>      classifiers    = Maps.newHashMap();
  private Map<String, EPackage>           packages       = Maps.newLinkedHashMap();
  
  /** ensures that createEPackages() is called only once */
  private boolean packagesCreated = false;

  public EntityBeanIntrospection(Map<String, HbMappedClass> hbClassData) {
    this.hbClassData = Preconditions.checkNotNull(hbClassData);
  }

  /**
   * 
   * @return the {@link EPackage} instances for the {@link Package}s that hold the iteraplan model classes
   */
  Collection<EPackage> getEPackages() {
    if (packages.isEmpty()) {
      createEPackages();
    }
    
    return packages.values();
  }

  /**
   * Create the EPackage instances for the iteraplan model classes
   * (NOTE: This method mustn't be called more than once!)
   */
  private Map<String, EPackage> createEPackages() {
    if (packagesCreated) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    
    for (Class<?> clazz : EntityClassStore.getEntityClasses()) {
      eClassifierFor(clazz);
    }

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
    ResourceSet resourceSet = new ResourceSetImpl();

    for (EPackage ePackage : packages.values()) {
      if (ePackage.getName().equals(PACKAGE_NAME)) {
        ePackage.getEClassifiers().add(Iteraplan2EMFHelper.getEContainerClass());
      }
      resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
    }
    packagesCreated = true;
    
    return packages;
  }

  /**
   * Method to create an {@link EClassifier} from a given {@link Class}
   * 
   * @param clazz the given Class
   * @return The created EClassifier
   */
  private EClassifier eClassifierFor(Class<?> clazz) {
    EClassifier cachedClassifier = this.classifiers.get(clazz);
    if (cachedClassifier != null) {
      return cachedClassifier;
    }

    for (EClassifier candidate : EcorePackage.eINSTANCE.getEClassifiers()) {
      if (clazz.equals(candidate.getInstanceClass())) {
        return candidate;
      }
    }

    if (clazz.isEnum()) {
      EEnum eenum = eEEnumFor(clazz);
      ePackageFor(clazz).getEClassifiers().add(eenum);
      this.classifiers.put(clazz, eenum);

      return eenum;
    }
    else {
      if (clazz.getSimpleName().equals("Clob")) {
        return EcorePackage.eINSTANCE.getEClassifier("EString");
      }
      else {
        return eEClassFor(clazz);
      }
    }
  }

  /**
   * Method to create an {@link EEnum} from a given {@link Class}
   * 
   * @param enumm the given Class
   * @return The created EEnum
   */
  @SuppressWarnings("unchecked")
  private EEnum eEEnumFor(Class<?> enumm) {
    // create the enum type
    EEnum result = EcoreFactory.eINSTANCE.createEEnum();
    if (enumm.getEnclosingClass() != null) {
      result.setName(enumm.getEnclosingClass().getSimpleName() + enumm.getSimpleName());
    }
    else {
      result.setName(enumm.getSimpleName());
    }

    // create the enum values
    for (Enum<?> literal : ((Class<? extends Enum<?>>) enumm).getEnumConstants()) {
      EEnumLiteral eLiteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
      eLiteral.setName(literal.name());
      eLiteral.setValue(literal.ordinal());
      eLiteral.setLiteral(literal.toString());
      result.getELiterals().add(eLiteral);
    }

    return result;
  }

  /**
   * Method to create an {@link EClass} for a given {@link Class}
   * 
   * @param clazz
   *          The given Class
   * @return The created EClass
   */
  private EClass eEClassFor(Class<?> clazz) {
    HbMappedClass hbmClass = hbClassData.get(clazz.getName());
    EClass result = EcoreFactory.eINSTANCE.createEClass();
    result.setName(clazz.getSimpleName());
    this.classifiers.put(clazz, result);

    Class<?> superClass = getSuperClass(clazz);
    if (superClass != null) {
      EClassifier superClassifier = null;
      if (this.classifiers.containsKey(superClass)) {
        superClassifier = this.classifiers.get(superClass);
      }
      else {
        superClassifier = eEClassFor(superClass);
      }

      if (superClassifier instanceof EClass) {
        result.getESuperTypes().add((EClass) superClassifier);
      }
    }

    result.setAbstract((clazz.getModifiers() & Modifier.ABSTRACT) > 0);

    if (hbmClass != null && hbmClass.getProperties() != null) {
      for (HbMappedProperty hbmProp : hbmClass.getProperties()) {
        if (hbmProp != null && hbmProp.getType() != null) {
          EClassifier eType = eClassifierFor(hbmProp);
          EStructuralFeature esf = createEStructuralFeature(hbmProp, eType);
          if (esf != null) {
            result.getEStructuralFeatures().add(esf);
            initEStructuralFeature(esf);
          }
        }
      }
    }

    addIdToRuntimePeriod(clazz, result);
    ePackageFor(clazz).getEClassifiers().add(result);

    return result;
  }

  /**
   * Due to the special behavior of {@link RuntimePeriod}s (there is a class definition, but the 
   * {@link RuntimePeriod}'s attributes start and end are mapped in the InformationSystemRelease's 
   * and TechnicalComponentRelease's tables), this method is necessary to create the {@link EAttribute}s 
   * manually
   * 
   * @param clazz which is checked for equality to {@link RuntimePeriod}.class
   * @param result the {@link EClass} that represents the clazz
   */
  private void addIdToRuntimePeriod(Class<?> clazz, EClass result) {
    if (RuntimePeriod.class.isAssignableFrom(clazz)) {
      EAttribute idAttribute = EcoreFactory.eINSTANCE.createEAttribute();
      idAttribute.setName("id");
      idAttribute.setID(true);
      idAttribute.setEType(EcorePackage.eINSTANCE.getEString());
      result.getEStructuralFeatures().add(idAttribute);

      EAttribute startAttribute = EcoreFactory.eINSTANCE.createEAttribute();
      startAttribute.setName("start");
      startAttribute.setEType(EcorePackage.eINSTANCE.getEDate());
      result.getEStructuralFeatures().add(startAttribute);

      EAttribute endAttribute = EcoreFactory.eINSTANCE.createEAttribute();
      endAttribute.setName("end");
      endAttribute.setEType(EcorePackage.eINSTANCE.getEDate());
      result.getEStructuralFeatures().add(endAttribute);
    }
  }

  /**
   * Get the {@link EPackage} that represents the {@link Package} of the given {@link Class}.
   * If no {@link EPackage} exists in the first place, it will be created.
   *  
   * @param clazz {@link Class}
   * @return {@link EPackage}
   */
  private EPackage ePackageFor(Class<?> clazz) {
    final String packageName = clazz.getPackage().getName();

    if (!this.packages.containsKey(packageName)) {
      EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
      String simplePackageName = packageName.substring(packageName.lastIndexOf('.') + 1);

      ePackage.setName(simplePackageName);
      ePackage.setNsPrefix(simplePackageName);
      ePackage.setNsURI("http://www.iteraplan.de/" + ePackage.getNsPrefix() + ".ecore");
      this.packages.put(packageName, ePackage);
    }

    return this.packages.get(packageName);
  }

  /**
   * Method to create an {@link EClassifier} for a given {@link HbMappedProperty}
   * 
   * @param hbmProp the given {@link HbMappedProperty}
   * @return The created EClassifier
   */
  private EClassifier eClassifierFor(HbMappedProperty hbmProp) {
    if (hbmProp.isId()) {
      return EcorePackage.eINSTANCE.getEClassifier("EString");
    }

    return eClassifierFor(hbmProp.getType());
  }

  /**
   * Method to create an {@link EStructuralFeature} of a specific type, 
   * for a given {@link HbMappedProperty} 
   * 
   * @param hbmProp the {@link HbMappedProperty}
   * @param type the {@link EClassifier} object representing the resulting {@link EStructuralFeature}'s type
   * 
   * @return The created EStructuralFeature
   */
  private EStructuralFeature createEStructuralFeature(HbMappedProperty hbmProp, EClassifier type) {
    if (type instanceof EDataType) {
      EAttribute att = EcoreFactory.eINSTANCE.createEAttribute();
      att.setName(hbmProp.getName());
      att.setLowerBound(hbmProp.isOptional() ? 0 : 1);
      att.setID(hbmProp.isId());
      att.setEType(type);
      att.setUnique(hbmProp.isUnique());
      att.setOrdered(hbmProp.isOrdered());
      att.setUpperBound(hbmProp.isMany() ? EStructuralFeature.UNBOUNDED_MULTIPLICITY : 1);
      return att;
    }
    else if (type instanceof EClass) {
      EReference ref = EcoreFactory.eINSTANCE.createEReference();
      ref.setName(hbmProp.getName());
      ref.setEType(type);
      ref.setLowerBound(hbmProp.isOptional() ? 0 : 1);
      ref.setUnique(hbmProp.isUnique());
      ref.setOrdered(hbmProp.isOrdered());
      ref.setUpperBound(hbmProp.isMany() ? EStructuralFeature.UNBOUNDED_MULTIPLICITY : 1);
      return ref;
    }

    return null;
  }

  /**
   * Method to specify additional properties of {@link EReference}
   * 
   * @param esf the {@link EStructuralFeature} which represents the {@link HbMappedProperty}
   */
  private void initEStructuralFeature(EStructuralFeature esf) {
    if (!(esf instanceof EReference)) {
      return;
    }

    EReference ref = (EReference) esf;
    if (ref.getUpperBound() != 1) {
      ref.setUnique(true);
    }

    EStructuralFeature opposite = findEOpposite(ref);
    if (opposite instanceof EReference) {
      ref.setEOpposite((EReference) opposite);
      ((EReference) opposite).setEOpposite(ref);

      if (ref.getUpperBound() == 1 && ref.getEOpposite().getUpperBound() != 1) {
        ref.getEOpposite().setDerived(true);
        ref.getEOpposite().setTransient(true);
      }
    }
  }

  /**
   * Get the super class of an entity class if it is different from {@link Object}.class
   * 
   * @param clazz the entity {@link Class}
   * @return the super class if it is different from {@link Object}.class
   */
  private Class<?> getSuperClass(Class<?> clazz) {
    if (clazz == null || clazz.getSuperclass() == null || clazz.getSuperclass().equals(Object.class)) {
      return null;
    }
    else {
      return clazz.getSuperclass();
    }
  }

  /**
   * Connect a given {@link EReference} with its opposite if possible
   * 
   * @param reference the {@link EReference}
   * @return the {@link EReference} which should be the EOpposite of reference
   */
  private EStructuralFeature findEOpposite(EReference reference) {

    EClass source = reference.getEContainingClass();
    EClassifier targetClassifier = reference.getEType();

    if (targetClassifier instanceof EClass) {
      EClass target = (EClass) targetClassifier;
      for (Entry<String, String> e : OPPOSITE_NAMES.entrySet()) {
        if (reference.getName().equals(e.getKey())) {
          if (source.getEStructuralFeature(e.getValue()) != null) {
            target = source;
          }
          return target.getEStructuralFeature(e.getValue());
        }
        if (reference.getName().equals(e.getValue())) {
          if (source.getEStructuralFeature(e.getKey()) != null) {
            target = source;
          }
          return target.getEStructuralFeature(e.getKey());
        }
      }

      ArrayList<EReference> candidates = new ArrayList<EReference>();
      for (EStructuralFeature esf : target.getEAllStructuralFeatures()) {
        if (esf instanceof EReference) {
          EReference candidate = (EReference) esf;
          if (candidate.getEType().equals(source) && !candidate.equals(reference)) {
            candidates.add(candidate);
          }
        }
      }
      if (candidates.size() == 1) {
        return candidates.get(0);
      }
    }
    return null;
  }

  /**
   * specify special name pairs of {@link EReference}s to make sure that their EOpposite 
   * attribute can be set correctly; (needed if {@link EClass}es A and B have more than one 
   * {@link EReference}s between them; NOTE: A == B can be true) 
   */
  private static ImmutableMap<String, String> initOppositeNames() {
    Map<String, String> map = Maps.newHashMap();
    map.put("parent", "children");
    map.put("predecessors", "successors");
    map.put("generalisation", "specialisations");
    map.put("informationSystemReleaseA", "interfacesReleaseA");
    map.put("informationSystemReleaseB", "interfacesReleaseB");
    return ImmutableMap.copyOf(map);
  }
}

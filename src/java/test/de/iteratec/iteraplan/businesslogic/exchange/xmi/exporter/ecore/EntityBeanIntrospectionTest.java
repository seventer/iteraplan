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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.Logger;


/**
 * Tests the entity bean introspection of {@link EntityBeanIntrospectionServiceBean} by comparing the resulting {@link EPackage}s
 * with a static ecore file ({@link #ECORE_FILE}).
 * NOTE: The current configuration implies the failing of the test whenever the entity bean introspection changes. If the changes 
 * are desired, you will have to update the ecore file that is used for the comparison!
 */
public class EntityBeanIntrospectionTest extends BaseTransactionalTestSupport {

  private static final Logger                LOGGER         = Logger.getIteraplanLogger(EntityBeanIntrospectionTest.class);
  private static final String                ECORE_FILE     = "xmi/iteraplanModel.ecore";
  private static final Map<String, String>   OPPOSITE_NAMES = EntityBeanIntrospection.OPPOSITE_NAMES;

  @Autowired
  private EntityBeanIntrospectionServiceBean entityIntrospectionBean;

  /**
   * Compare all {@link EPackage}s, their {@link EClass}es and {@link EEnum}s to the saved reference ecore file 
   * and verify their similarity;
   * NOTE: The method's purpose is mainly to generate warnings in case of any differences and only produces errors in case 
   * of major differences!
   */
  @Test
  public void testEcoreCompleteness() {
    Map<String, EPackage> loadedEPackages = loadEPackagesFromFile();

    assertNotNull(entityIntrospectionBean);
    Collection<EPackage> generatedEPackages = entityIntrospectionBean.getEPackages();
    assertNotNull(generatedEPackages);
    assertEquals(loadedEPackages.size(), generatedEPackages.size()); //[mode, attribute, user, query, interfaces]

    for (EPackage generatedPackage : generatedEPackages) {

      EPackage loaded = loadedEPackages.get(generatedPackage.getName());

      assertNotNull(loaded);
      assertEquals(loaded.getName(), generatedPackage.getName());
      assertEquals(loaded.getNsPrefix(), generatedPackage.getNsPrefix());
      assertEquals(loaded.getNsURI(), generatedPackage.getNsURI());
      assertEquals(loaded.getESuperPackage(), generatedPackage.getESuperPackage());

      Map<String, EClassifier> loadedEClassifiers = Maps.newHashMap();
      for (EClassifier ec : loaded.getEClassifiers()) {
        loadedEClassifiers.put(ec.getName(), ec);
        if (generatedPackage.getEClassifier(ec.getName()) == null) {
          LOGGER.warn("EClassifier '" + ec.getName()
              + "' is defined in reference ecore file but is not generated by the current Ecore generation any more");
        }
      }

      for (EClassifier ec : generatedPackage.getEClassifiers()) {
        checkEClassifierCompleteness(ec, loadedEClassifiers);
      }
    }
  }

  /**
   * Tests special EStructuralFeature properties which are specific to the iteraplen ecore export
   */
  @Test
  public void testEStructuralFeatures() {
    assertNotNull(entityIntrospectionBean);

    Collection<EPackage> ePackages = entityIntrospectionBean.getEPackages();
    assertNotNull(ePackages);
    assertFalse(ePackages.isEmpty());

    for (EPackage ePackage : ePackages) {
      Collection<EClassifier> eClassifiers = ePackage.getEClassifiers();
      assertNotNull(eClassifiers);
      assertFalse(eClassifiers.isEmpty());

      for (EClassifier eClassifier : eClassifiers) {
        if (eClassifier instanceof EClass) {
          EClass eClass = (EClass) eClassifier;
          checkEIDAttribute(eClass);
          checkEReferences(eClass);
        }
      }
    }
  }

  /**
   * Checks whether the generated EClassifier is contained within the EClassifier definitions 
   * of the reference ecore file
   * 
   * @param generatedEClassifier
   *    the {@link EClassifier} that has been generated by the entity bean introspection
   * @param loadedEClassifiers
   *    the EClassifiers of the reference ecore file
   */
  private void checkEClassifierCompleteness(EClassifier generatedEClassifier, Map<String, EClassifier> loadedEClassifiers) {

    assertNotNull(generatedEClassifier);
    assertNotNull(loadedEClassifiers);

    EClassifier ec = loadedEClassifiers.get(generatedEClassifier.getName());
    if (ec == null) {
      LOGGER.warn("EClassifier '" + generatedEClassifier.getName()
          + "' is generated by the current EPackage generation but is not contained in the reference ecore model");
    }
    else if (generatedEClassifier instanceof EClass) {
      EClass generatedEClass = (EClass) generatedEClassifier;

      if (ec instanceof EClass) {
        EClass loadedEClass = (EClass) ec;
        checkEClassCompleteness(generatedEClass, loadedEClass);
      }
      else {
        fail("Expected EClass but got an EEnum");
      }
    }
    else if (generatedEClassifier instanceof EEnum) {
      EEnum generatedEEnum = (EEnum) generatedEClassifier;

      if (ec instanceof EEnum) {
        EEnum loadedEEnum = (EEnum) ec;
        checkEEnumCompleteness(generatedEEnum, loadedEEnum);
      }
      else {
        fail("Expected EEnum but gon an EClass");
      }
    }
    else {
      fail("Expected EClass or EEnum but got a " + generatedEClassifier.getClass().getName());
    }
  }

  /**
   * Checks the similarity of a generated and a loaded {@link EClass}
   * 
   * @param generatedEClass
   *    generated by entity bean introspection (runtime)
   * @param loadedEClass
   *    loaded from reference ecore file
   */
  private void checkEClassCompleteness(EClass generatedEClass, EClass loadedEClass) {
    assertNotNull(generatedEClass);
    assertNotNull(loadedEClass);
    assertEquals(generatedEClass.getName(), loadedEClass.getName());

    if ((generatedEClass.getESuperTypes().isEmpty() && !loadedEClass.getESuperTypes().isEmpty())
        || (!generatedEClass.getESuperTypes().isEmpty() && loadedEClass.getESuperTypes().isEmpty())) {
      LOGGER.warn("Please check inheritence of EClass '" + generatedEClass.getName() + "'");
    }
    for (EStructuralFeature loadedESF : loadedEClass.getEStructuralFeatures()) {
      if (generatedEClass.getEStructuralFeature(loadedESF.getName()) == null) {
        LOGGER.warn("EStructuralFeature '" + loadedEClass.getName() + "." + loadedESF.getName()
            + "as defined by reference ecore file could not be found in current EPackage export");
      }
    }

    for (EStructuralFeature generatedEsf : generatedEClass.getEStructuralFeatures()) {
      EStructuralFeature loadedEsf = loadedEClass.getEStructuralFeature(generatedEsf.getName());
      if (loadedEsf == null) {
        LOGGER.warn("EStructuralFeature '" + generatedEClass.getName() + "." + generatedEsf.getName() + "' cannot be found in refrence ecore file");
      }
      else {
        if (generatedEsf.getLowerBound() != loadedEsf.getLowerBound()) {
          LOGGER.warn("EStructuralFeature '" + generatedEClass.getName() + "." + generatedEsf.getName()
              + "' has a differnt multiplicity as defined in refrence ecore file");
        }
        if (generatedEsf.getLowerBound() != loadedEsf.getLowerBound() || generatedEsf.getUpperBound() != loadedEsf.getUpperBound()
            || generatedEsf.isRequired() != loadedEsf.isRequired() || generatedEsf.isUnique() != loadedEsf.isUnique()) {
          LOGGER.warn("EStructuralFeature '" + generatedEClass.getName() + "." + generatedEsf.getName()
              + "' has a differnt multiplicity as defined in refrence ecore file");
        }

        if (loadedEsf.getEType().eIsProxy()) {
          if (!loadedEsf.getEType().toString().contains(generatedEsf.getEType().getName())) {
            LOGGER.warn("Found type missmatch of EstructuralFeature '" + generatedEClass.getName() + "." + generatedEsf.getName() + "' ");
          }
        }
        else if (!generatedEsf.getEType().getName().equals(loadedEsf.getEType().getName())) {
          LOGGER.warn("Found type missmatch of EstructuralFeature '" + generatedEClass.getName() + "." + generatedEsf.getName() + "' ");
        }
      }
    }
  }

  /**
   * Checks the similarity of a generated and a loaded {@link EEnum}
   * 
   * @param generatedEEnum
   *    generated by entity bean introspection (runtime)
   * @param loadedEEnum
   *    loaded from reference ecore file
   */
  private void checkEEnumCompleteness(EEnum generatedEEnum, EEnum loadedEEnum) {

    assertNotNull(generatedEEnum);
    assertNotNull(loadedEEnum);

    for (EEnumLiteral loadedELit : loadedEEnum.getELiterals()) {
      if (generatedEEnum.getEEnumLiteral(loadedELit.getName()) == null) {
        LOGGER.warn("EEnumLiteral '" + generatedEEnum.getName() + "." + loadedELit.getName()
            + "' as defined in reference ecore file could not be found in current EPackge generation");
      }
    }

    for (EEnumLiteral generatedELit : generatedEEnum.getELiterals()) {

      EEnumLiteral loadedELit = loadedEEnum.getEEnumLiteral(generatedELit.getName());
      if (loadedELit == null) {
        LOGGER.warn("EEnumLiteral '" + generatedEEnum.getName() + "." + generatedELit.getName() + "' could not be found in reference ecore file");
      }
    }
  }

  /**
   * Checks the id specific settings for the eIDAttribute of a {@link EClass}
   */
  private void checkEIDAttribute(EClass eClass) {

    assertNotNull(eClass);

    EAttribute eIdAttribute = eClass.getEIDAttribute();

    if (eIdAttribute == null) {
      LOGGER.warn("Could not find any EIDAttribute in EClass '" + eClass.getName() + "'");
    }
    else {
      assertTrue(eIdAttribute.isUnique());
      assertEquals("id", eIdAttribute.getName());
      assertEquals(EcorePackage.eINSTANCE.getEClassifier("EString"), eIdAttribute.getEAttributeType());
    }
  }

  /**
   * checks the correctness of an {@link EClass}es' EReferences
   * @param eClass the {@link EClass} to check
   */
  private void checkEReferences(EClass eClass) {

    assertNotNull(eClass);
    Collection<EStructuralFeature> eStructuralFeatures = eClass.getEStructuralFeatures();
    assertNotNull(eStructuralFeatures);
    assertTrue(!eStructuralFeatures.isEmpty() || eClass.isAbstract());

    for (EStructuralFeature eStructuralFeature : eStructuralFeatures) {
      if (eStructuralFeature instanceof EReference) {
        EReference eReference = (EReference) eStructuralFeature;
        EReference eOpposite = eReference.getEOpposite();
        if (eOpposite == null) {
          LOGGER.warn("EReference '" + eClass.getName() + "." + eReference.getName() + "' does not have an eOpposite");
        }
        else {
          if (OPPOSITE_NAMES.containsKey(eReference.getName())) {
            assertEquals(OPPOSITE_NAMES.get(eReference.getName()), eOpposite.getName());
          }
          else if (OPPOSITE_NAMES.containsValue(eReference.getName())) {
            assertEquals(OPPOSITE_NAMES.get(eOpposite.getName()), eReference.getName());
          }
        }
      }
    }
  }

  /**
   * Load contents of ecore file and returns a map of {@link EPackage}s
   */
  private Map<String, EPackage> loadEPackagesFromFile() {

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
    ResourceSet rs = new ResourceSetImpl();
    Map<String, EPackage> ePackages = Maps.newHashMap();

    try {
      ClassPathResource importFile = new ClassPathResource(ECORE_FILE);
      URI uri = URI.createURI(importFile.getURI().getRawPath());
      Resource file = rs.createResource(uri);
      file.load(null);
      for (Object o : file.getContents()) {
        if (o instanceof EPackage) {
          EPackage e = (EPackage) o;
          ePackages.put(e.getName(), e);
        }
        else {
          fail("Tried to load EPackages but found a " + o.getClass().getName());
        }
      }
    } catch (IOException e) {
      fail("Failed to load ecore resource at " + ECORE_FILE + "\n" + e.getMessage());
    }
    assertTrue(ePackages.size() > 0);
    return ePackages;
  }
}

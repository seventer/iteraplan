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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.Logger;


/**
 * This class is responsible for testing the content of the IteraplanModelForTabularReporting.ecore file. This 
 * serves as a base for the generated {@link EPackage}. This is why the content of this file needs to be up to date.
 * Therefore, the content of the file is compared to the current {@link EPackage} 'model' that is generated (dynamically) 
 * by the iteraplan ecore export.
 * Each time, the test identifies a missing/deprecated {@link EStructuralFeature}, a corresponding warning message will
 * be displayed (so the test won't fail)!
 */
public class EcoreForTabularReportingTest extends BaseTransactionalTestSupport {

  private static final Logger                LOGGER           = Logger.getIteraplanLogger(EcoreForTabularReportingTest.class);
  private static final Set<String>           IGNORED_WARNINGS = getIgnoredEsfNames();

  @Autowired
  private EntityBeanIntrospectionServiceBean entityIntrospectionService;

  /**
   * This method seaches for {@link EStructuralFeature}s that are defined by the saved
   * ecore file but no longer contained in the iteraplan entity classes
   */
  @Test
  public void testForRemovedFeatures() {
    EPackage savedEcoreModel = getSavedEcoreModel();
    EPackage generatedEPackage = getGeneratedEPackage();

    for (EClassifier ec : savedEcoreModel.getEClassifiers()) {
      if (ec instanceof EClass) {
        EClass savedEClass = (EClass) ec;
        EClass generatedEClass = (EClass) generatedEPackage.getEClassifier(savedEClass.getName());

        assertNotNull(generatedEClass);

        List<String> generatedEsfNames = getAllEsfNames(generatedEClass);

        for (EStructuralFeature esf : savedEClass.getEAllStructuralFeatures()) {
          if (!generatedEsfNames.contains(esf.getName()) && !IGNORED_WARNINGS.contains(esf.getName())) {
            LOGGER.warn("Could not find EStructuralFeature '" + esf.getEContainingClass().getName() + "." + esf.getName() + "' in generated EClass");
          }
        }
      }
    }
  }

  /**
   * This method searches for recently added Attributes/References in the iteraplan model
   * classes that have not been added to the ecore file for tabular reporting.
   */
  @Test
  public void testForNewFeatures() {
    EPackage savedEcoreModel = getSavedEcoreModel();
    EPackage generatedEPackage = getGeneratedEPackage();

    for (EClassifier ec : savedEcoreModel.getEClassifiers()) {
      if (ec instanceof EClass) {
        EClass savedEClass = (EClass) ec;
        EClassifier ecl = generatedEPackage.getEClassifier(savedEClass.getName());
        assertNotNull(ecl);
        EClass generatedEClass = (EClass) ecl;
        for (String esfName : getAllEsfNames(generatedEClass)) {
          if (savedEClass.getEStructuralFeature(esfName) == null && !IGNORED_WARNINGS.contains(esfName)) {
            LOGGER.warn("Could not find EStructuralFeature '" + savedEClass.getName() + "." + esfName + "' in saved ecore file");
          }
        }
      }
    }
  }

  /**
   * Loads the contents of the static ecore file that contains the {@link EPackage} that serves as a base for 
   * the ecore export for tabular reporting
   */
  private EPackage getSavedEcoreModel() {

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
    ResourceSet rs = new ResourceSetImpl();

    try {
      ClassPathResource importFile = new ClassPathResource("IteraplanModelForTabularReporting.ecore");
      URI uri = URI.createURI(importFile.getURI().getRawPath());
      Resource file = rs.createResource(uri);
      file.load(null);
      for (Object o : file.getContents()) {
        if (o instanceof EPackage) {
          EPackage e = (EPackage) o;
          if (e.getName().equals(EntityBeanIntrospection.PACKAGE_NAME)) {
            return e;
          }
        }
      }
    } catch (IOException e) {
      fail("Failed to load ecore resource at \n" + e.getMessage());
    }
    fail("Error loading EPackage 'model' from file");
    return null;//to avoid compiler error
  }

  /**
   * Create the {@link EPackage}s for the standard ecore export and return
   * the single EPackage that has its representation in the ecore model for tabular reporting
   * @see {@link EntityBeanIntrospection#PACKAGE_NAME}
   */
  private EPackage getGeneratedEPackage() {

    assertNotNull(entityIntrospectionService);
    Collection<EPackage> ePackages = entityIntrospectionService.getEPackages();
    for (EPackage ePackage : ePackages) {
      if (ePackage.getName().equals(EntityBeanIntrospection.PACKAGE_NAME)) {
        return ePackage;
      }
    }
    fail("Error generating EPackages");
    return null;
  }

  /**
   * Returns the names of all {@link EStructuralFeature}s that should be contained
   * in the passed {@link EClass}' tabular reporting representation
   * @see {@link #findAllESFs(EClass)} 
   */
  private List<String> getAllEsfNames(EClass eClass) {
    List<EStructuralFeature> esfs = findAllESFs(eClass);
    List<String> esfNames = Lists.newArrayList();
    for (EStructuralFeature esf : esfs) {
      esfNames.add(esf.getName());
    }
    return esfNames;
  }

  /**
   * Searches for all {@link EStructuralFeature}s in a {@link EClass} of the iteraplan ecore
   * export.
   * CAUTION: As there are no super classes in the {@link EClass}es of the ecore model for tabular reporting,
   * we don't have to care about these, but as ISRs an ISs (same as for TCRs an TCs) are merged for the tabular reporting,
   * this method returns the Release's properties, concated with the parent class' properties.
   */
  private List<EStructuralFeature> findAllESFs(EClass eClass) {

    assertNotNull(eClass);

    List<EStructuralFeature> eSFs = Lists.newArrayList();
    eSFs.addAll(eClass.getEAllStructuralFeatures());

    if (eClass.getName().endsWith("Release")) {
      String superName = eClass.getName().replace("Release", "");
      EClassifier eSuperClassifier = eClass.getEPackage().getEClassifier(superName);
      if (eSuperClassifier instanceof EClass) {
        EClass eSuperClass = (EClass) eSuperClassifier;
        eSFs.addAll(eSuperClass.getEAllStructuralFeatures());
      }
      else {
        fail("expected to find EClass '" + superName + "' in EPackge " + eClass.getEPackage().getName());
      }
    }
    return eSFs;
  }

  /**
   * Sets up a static set with names of {@link EStructuralFeature}s that are either contained in the
   * standard ecore export OR the saved ecore model for tabular reporting. Any "known differences" should 
   * be manaed in this method!
   */
  private static Set<String> getIgnoredEsfNames() {
    Set<String> set = Sets.newHashSet();
    set.add("position"); //TODO why do we need this?
    set.add("direction"); //simple name difference
    set.add("interfaceDirection"); // -"-
    set.add("buildingBlockType"); //not necessary!
    set.add("olVersion"); //not necessary!
    set.add("subscribedUsers"); //not necessary!
    set.add("seals"); //not necessary!
    set.add("attributeValueAssignments"); //not necessary!

    //other esfs that can be ignored:
    //BF.isr_s
    //ISR.bf_s
    //ISR.is
    //IS.releases
    //TRC.tc
    //TR.releases
    //RP.id

    return ImmutableSet.copyOf(set);
  }
}

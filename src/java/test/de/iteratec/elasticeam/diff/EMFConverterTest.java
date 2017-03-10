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
package de.iteratec.elasticeam.diff;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;

import de.iteratec.elasticeam.MetamodelCreator;
import de.iteratec.elasticeam.ModelCreator;
import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMetamodelLoaderImpl;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.ModelLoader;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.emf.EObjectConverter;
import de.iteratec.iteraplan.elasticeam.metamodel.emf.EPackageConverter;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.ModelFactory;


/**
 *
 */
public class EMFConverterTest extends BaseTransactionalTestSupport {

  private static final Logger LOGGER           = Logger.getIteraplanLogger(EMFConverterTest.class);

  private MetamodelCreator    metamodelCreator = new MetamodelCreator();
  private Metamodel           metamodel;
  private Model               model;
  private ResourceSet         resourceSet;

  @Autowired
  private IteraplanMetamodelLoaderImpl metamodelLoader;
  @Autowired
  private ModelLoader         modelLoader;
  @Autowired
  private TestDataHelper2     testDataHelper;

  @Before
  public void init() {
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new EcoreResourceFactoryImpl());
    resourceSet = new ResourceSetImpl();
  }

//  @Test
//  public void testEPackageConverter() {
//    loadTestData();
//    EPackage ePackage = EPackageConverter.convert(metamodel, false);
//    ePackage.setNsPrefix("metamodel");
//    ePackage.setNsURI("http://iteraplan.de/metamodel");
//    assertNotNull(ePackage);
//    assertFalse(ePackage.getEClassifiers().isEmpty());
//    Resource ecore = resourceSet.createResource(URI.createURI("ePackage.ecore"));
//    resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
//    ecore.getContents().add(ePackage);
//    //    ecore.save(Maps.newHashMap());
//  }

  @Test
  public void testEPackageConverter2() {

    IteraplanMapping mapping = loadIteraplanData();
    EPackage ePackage = EPackageConverter.convert(mapping.getMetamodel(), false);

    ePackage.setNsPrefix("metamodel");
    ePackage.setNsURI("http://iteraplan.de/metamodel");
    assertNotNull(ePackage);
    assertFalse(ePackage.getEClassifiers().isEmpty());
    Resource ecore = resourceSet.createResource(URI.createURI("ePackage2.ecore"));
    resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
    ecore.getContents().add(ePackage);
  }

  @Test
  public void testEObjectConverter() {
    loadTestData();

    EPackage ePackage = EPackageConverter.convert(metamodel, false);
    ePackage.setNsURI("http://iteraplan.de/metamodel");
    ePackage.setNsPrefix("metamodel");

    Collection<EObject> eObjects = EObjectConverter.export(metamodel, model, false);
    assertNotNull(eObjects);
    assertFalse(eObjects.isEmpty());
    Resource xmi = getXMIResource("./xmi/eObjects1.xmi", ePackage, eObjects);
    assertNotNull(xmi);
    assertFalse(xmi.getContents().isEmpty());
    LOGGER.info("EObjectConverter created " + eObjects.size() + "EObjects");
  }

  @Test
  public void testEObjectConverter2() {
    IteraplanMapping mapping = loadIteraplanData();
    EPackage ePackage = EPackageConverter.convert(mapping.getMetamodel(), false);
    assertNotNull(ePackage);
    assertFalse(ePackage.getEClassifiers().isEmpty());
    ePackage.setNsURI("http://iteraplan.de/metamodel");
    ePackage.setNsPrefix("metamodel");

    Collection<EObject> eObjects = EObjectConverter.export(mapping.getMetamodel(), model, false);
    Resource xmi = getXMIResource("./xmi/eObjects2.xmi", ePackage, eObjects);
    assertNotNull(xmi);
    assertFalse(xmi.getContents().isEmpty());
    LOGGER.info("EObjectConverter created " + eObjects.size() + "EObjects");
  }

  @Test
  public void testEPackageConverter3() throws InterruptedException {
    IteraplanMapping mapping1 = loadIteraplanData();
    EPackage ePackage1 = EPackageConverter.convert(mapping1.getMetamodel(), false);
    assertFalse(ePackage1.getEClassifiers().isEmpty());
    ePackage1.setNsURI("http://iteraplan.de/metamodel");
    ePackage1.setNsPrefix("metamodel");

    IteraplanMapping mapping2 = loadIteraplanData();
    EPackage ePackage2 = EPackageConverter.convert(mapping2.getMetamodel(), false);
    assertFalse(ePackage2.getEClassifiers().isEmpty());
    ePackage2.setNsURI("http://iteraplan.de/metamodel");
    ePackage2.setNsPrefix("metamodel");

    Map<String, Object> options = Maps.newHashMap();

    MatchModel matchModel = MatchService.doMatch(ePackage2, ePackage1, options);
    assertFalse(matchModel.getMatchedElements().isEmpty());
    assertTrue(matchModel.getUnmatchedElements().isEmpty());

  }

//  @Test
//  public void testEObjectConverter3() throws InterruptedException {
//    IteraplanMapping mapping1 = loadIteraplanData();
//    de.iteratec.iteraplan.elasticeam.metamodel.emf.Mapping<Metamodel> metamodelMapping = EPackageConverter.deriveMapping(mapping1.getMetamodel(),
//        false);
//
//    Collection<EObject> eObjects1 = EObjectConverter.export(metamodelMapping, model);
//
//    loadIteraplanData();
//    Collection<EObject> eObjects2 = EObjectConverter.export(metamodelMapping, model);
//
//    Resource r1 = getXMIResource("uri1.xmi", null, eObjects1);
//    Resource r2 = getXMIResource("uri2.xmi", null, eObjects2);
//    Map<String, Object> options = Maps.newHashMap();
//    MatchModel matchModel = MatchService.doResourceMatch(r2, r1, options);
//    assertTrue(matchModel.getUnmatchedElements().isEmpty());
//    assertFalse(matchModel.getMatchedElements().isEmpty());
//  }

  private void loadTestData() {
    metamodel = metamodelCreator.getMetamodel();
    model = ModelCreator.createModel(metamodel);
    assertNotNull(metamodel);
    assertFalse(metamodel.getTypes().isEmpty());
  }

  private IteraplanMapping loadIteraplanData() {
    IteraplanMapping mapping = metamodelLoader.loadConceptualMetamodelMapping();
    assertNotNull(mapping);
    assertNotNull(mapping.getMetamodel());
    model = ModelFactory.INSTANCE.createModel(mapping.getMetamodel());
    assertNotNull(model);
    modelLoader.load(model, mapping);
    return mapping;
  }

  private Resource getXMIResource(String uri, EPackage ePackage, Collection<EObject> eObjects) {
    Resource xmi = resourceSet.createResource(URI.createURI(uri));
    if (ePackage != null) {
      xmi.getContents().add(ePackage);
    }

    xmi.getContents().addAll(eObjects);
    return xmi;
  }
}

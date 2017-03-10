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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Method;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.Test;

import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.interfaces.IdEntity;

/**
 * JUnit test for the {@link XmiHelper} class.
 *
 */
public class XmiHelperTest {
  private static final Integer ID15 = Integer.valueOf(15);

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.XmiHelper#getEId(org.eclipse.emf.ecore.EObject)}.
   */
  @Test
  public void testGetEId() {
    EObject eObject = createSimpleEObject("Project_" + ID15);
    
    assertEquals(ID15, XmiHelper.getEId(eObject));
  }
  
  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.XmiHelper#getEId(org.eclipse.emf.ecore.EObject)}.
   */
  @Test
  public void testGetEIdWithoutString() {
    EObject eObject = createSimpleEObject(ID15.toString());
    
    assertEquals(ID15, XmiHelper.getEId(eObject));
  }
  
  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.XmiHelper#getEId(org.eclipse.emf.ecore.EObject)}.
   */
  @Test
  public void testGetEIdWhenInvalidId() {
    EObject eObject = createSimpleEObject("wrongId");
    
    assertNull(XmiHelper.getEId(eObject));
  }

  private EObject createSimpleEObject(Object idValue) {
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName("Project");
    
    EAttribute idAttr = EcoreFactory.eINSTANCE.createEAttribute();
    idAttr.setID(true);
    idAttr.setEType(EcorePackage.eINSTANCE.getEString());
    idAttr.setName("id");
    eClass.getEStructuralFeatures().add(idAttr);
    
    EAttribute topLevelAttr = EcoreFactory.eINSTANCE.createEAttribute();
    topLevelAttr.setEType(EcorePackage.eINSTANCE.getEBoolean());
    topLevelAttr.setName("topLevelElement");
    eClass.getEStructuralFeatures().add(topLevelAttr);
    
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.getEClassifiers().add(eClass);
    EFactory eFactory = ePackage.getEFactoryInstance();
    EObject eObject = eFactory.create(eClass);
    eObject.eSet(idAttr, idValue);
    
    return eObject;
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.XmiHelper#getId()}.
   */
  @Test
  public void testGetId() {
    Project proj = new Project();
    proj.setId(ID15);
    
    assertEquals(ID15, proj.getId());
  }
  
  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.XmiHelper#getId()}.
   */
  @Test
  public void testGetIdWhenIdNull() {
    Project proj = new Project();
    
    assertNull(proj.getId());
  }
  
  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.XmiHelper#invokeWriteMethod(de.iteratec.iteraplan.model.interfaces.IdEntity, java.lang.Object, org.eclipse.emf.ecore.EStructuralFeature, de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.SessionHelper)}.
   */
  @Test
  public void testInvokeWriteMethod() {
    Project proj = new Project();
    EObject eObject = createSimpleEObject("Project_" + ID15);
    EAttribute eidAttribute = eObject.eClass().getEIDAttribute();
    
    XmiHelper.invokeWriteMethod(proj, ID15, eidAttribute, null);
    assertEquals(ID15, proj.getId());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.XmiHelper#getWriteMethod(org.eclipse.emf.ecore.EStructuralFeature)}.
   */
  @Test
  public void testGetWriteMethod() {
    EObject eObject = createSimpleEObject("Project_" + ID15);
    EAttribute eidAttribute = eObject.eClass().getEIDAttribute();
    
    Method writeMethod = XmiHelper.getWriteMethod(eidAttribute, Project.class);
    assertNotNull(writeMethod);
    assertEquals("setId", writeMethod.getName());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.XmiHelper#getNewObject(org.eclipse.emf.ecore.EObject)}.
   */
  @Test
  public void testGetNewObject() {
    IdEntity entity = XmiHelper.getNewObject(Project.class);
    assertNotNull(entity);
  }
  
  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.XmiHelper#invokeReadMethod(de.iteratec.iteraplan.model.interfaces.IdEntity, org.eclipse.emf.ecore.EStructuralFeature)}.
   */
  @Test
  public void testInvokeReadMethod() {
    Project proj = new Project();
    proj.setId(ID15);
    EObject eObject = createSimpleEObject("Project_" + ID15);
    EAttribute eidAttribute = eObject.eClass().getEIDAttribute();
    
    Object value = XmiHelper.invokeReadMethod(proj, eidAttribute);
    assertEquals(ID15, value);
  }
  
  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.XmiHelper#invokeReadMethod(de.iteratec.iteraplan.model.interfaces.IdEntity, org.eclipse.emf.ecore.EStructuralFeature)}.
   */
  @Test
  public void testInvokeReadMethodBoolean() {
    Project proj = new Project();
    proj.setName(AbstractHierarchicalEntity.TOP_LEVEL_NAME);
    EObject eObject = createSimpleEObject("Project_" + ID15);
    EStructuralFeature eidAttribute = eObject.eClass().getEStructuralFeature("topLevelElement");
    
    Object value = XmiHelper.invokeReadMethod(proj, eidAttribute);
    assertEquals(Boolean.TRUE, value);
  }

}

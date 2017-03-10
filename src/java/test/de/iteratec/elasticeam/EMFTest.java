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
package de.iteratec.elasticeam;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.elasticeam.metamodel.emf.EPackageConverter;


public class EMFTest {

  private EPackage pkg;

  @Before
  public void setUp() {
    pkg = EPackageConverter.convert(new MetamodelCreator().getMetamodel(), true);
  }

  private static String getEscapedName(ENamedElement eNamedElement) {
    return EcoreUtil.getAnnotation(eNamedElement, ExtendedMetaData.ANNOTATION_URI, "name");
  }

  private static String escape(String name) {
    StringBuffer escapedName = new StringBuffer();
    boolean isFirst = true;
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
        escapedName.append(c);
      }
      else if (!isFirst && (c == '-' || c == '.' || ('0' <= c && c <= '9'))) {
        escapedName.append(c);
      }
      else {
        escapedName.append('_');
        escapedName.append((int) c);
      }
      isFirst = false;
    }
    return escapedName.toString();
  }

  @Test
  public void testEEnumerations() {
    for (String enumerationName : MetamodelCreator.getEnumerationNames()) {
      Assert.assertTrue(pkg.getEClassifier(enumerationName) instanceof EEnum);
      Assert.assertEquals(escape(enumerationName), getEscapedName(pkg.getEClassifier(enumerationName)));
    }
  }

  @Test
  public void testEClasses() {
    for (String substantialTypeName : MetamodelCreator.getSubstantialTypeNames()) {
      Assert.assertTrue(pkg.getEClassifier(substantialTypeName) instanceof EClass);
      Assert.assertEquals(escape(substantialTypeName), getEscapedName(pkg.getEClassifier(substantialTypeName)));
    }
  }

  //  @Test
  //  public void testBasicAttribute() {
  //    for (String substantialTypeName : MetamodelCreator.getSubstantialTypeNames()) {
  //      EClass eClass = (EClass) pkg.getEClassifier(substantialTypeName);
  //      Assert.assertTrue(eClass.getEStructuralFeature("id") instanceof EAttribute);
  //      EAttribute idAttribute = (EAttribute) eClass.getEStructuralFeature("id");
  //      Assert.assertEquals("id", getEscapedName(idAttribute));
  //      Assert.assertEquals(1, idAttribute.getLowerBound());
  //      Assert.assertEquals(1, idAttribute.getUpperBound());
  //      Assert.assertEquals(EcorePackage.eINSTANCE.getEIntegerObject(), idAttribute.getEAttributeType());
  //      Assert.assertTrue(idAttribute.isID());
  //
  //      Assert.assertTrue(eClass.getEStructuralFeature("name") instanceof EAttribute);
  //      EAttribute nameAttribute = (EAttribute) eClass.getEStructuralFeature("name");
  //      Assert.assertEquals("name", getEscapedName(nameAttribute));
  //      Assert.assertEquals(1, nameAttribute.getLowerBound());
  //      Assert.assertEquals(1, nameAttribute.getUpperBound());
  //      Assert.assertEquals(EcorePackage.eINSTANCE.getEString(), nameAttribute.getEAttributeType());
  //      Assert.assertFalse(nameAttribute.isID());
  //
  //      Assert.assertTrue(eClass.getEStructuralFeature("description") instanceof EAttribute);
  //      EAttribute descriptionAttribute = (EAttribute) eClass.getEStructuralFeature("description");
  //      Assert.assertEquals("description", getEscapedName(descriptionAttribute));
  //      Assert.assertEquals(0, descriptionAttribute.getLowerBound());
  //      Assert.assertEquals(1, descriptionAttribute.getUpperBound());
  //      Assert.assertEquals(EcorePackage.eINSTANCE.getEString(), descriptionAttribute.getEAttributeType());
  //      Assert.assertFalse(descriptionAttribute.isID());
  //    }
  //  }

  @Test
  public void writeEPackage() {
    Resource r = new EcoreResourceFactoryImpl().createResource(URI.createURI(""));
    r.getContents().add(pkg);
    try {
      File tempFile = File.createTempFile("test", "ecore");
      FileOutputStream fos = new FileOutputStream(tempFile);
      r.save(fos, Collections.emptyMap());
      fos.close();
    } catch (Exception e) {
      fail();
    }
  }
}

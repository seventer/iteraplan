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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.elasticeam.emfimpl.EMFMetamodel;
import de.iteratec.iteraplan.elasticeam.exception.MetamodelException;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitiveTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;


public class MetamodelUpdateTest {

  private MetamodelCreator metamodelCreator;

  @Before
  public void setUp() {
    this.metamodelCreator = new MetamodelCreator();
  }

  private EMFMetamodel getMetamodelImpl() {
    return this.metamodelCreator.getMetamodelImpl();
  }

  @Test(expected = MetamodelException.class)
  public void testIDPropertyRemoval() {
    getMetamodelImpl().deleteProperty(UniversalTypeExpression.ID_PROPERTY);
  }

  @Test(expected = MetamodelException.class)
  public void testNamePropertyRemovalFromSubstantialType() {
    getMetamodelImpl().deleteProperty(MixinTypeNamed.NAME_PROPERTY);
  }

  @Test(expected = MetamodelException.class)
  public void testDescriptionPropertyRemovalFromSubstantialType() {
    getMetamodelImpl().deleteProperty(MixinTypeNamed.DESCRIPTION_PROPERTY);
  }

  @Test(expected = MetamodelException.class)
  public void testDuplicateProperty() {
    assertNotNull(typeIS().findPropertyByName("version"));
    getMetamodelImpl().createProperty(typeIS(), "version", 1, FeatureExpression.UNLIMITED, getDecimalPrimitiveType());
  }

  @Test
  public void testBuiltinPrimitiveTypeExists() {
    assertNotNull(getMetamodelImpl().findTypeByPersistentName(BigDecimal.class.getName()));
  }

  @Test
  public void testCustomPrimitiveTypeExists() {
    assertNull(getMetamodelImpl().findTypeByPersistentName(Integer.class.getName()));
  }

  @Test
  public void testNonReplacingDuplicateProperty() {
    PropertyExpression<?> version = typeIS().findPropertyByName("version");
    try {
      getMetamodelImpl().createProperty(typeIS(), "version", 1, FeatureExpression.UNLIMITED, getDecimalPrimitiveType());
    } catch (MetamodelException expected) {
      //Nothing
    }
    assertSame(version, typeIS().findPropertyByName("version"));
  }

  @Test(expected = MetamodelException.class)
  public void testShadowingBuiltInProperty() {
    assertNotNull(typeIS().findPropertyByName("id"));
    PrimitiveTypeExpression integerType = (PrimitiveTypeExpression) getMetamodelImpl().findTypeByPersistentName(Integer.class.getName());
    getMetamodelImpl().createProperty(typeIS(), "id", 0, 1, integerType);
  }

  @Test
  public void testRemoveEnumerations() {
    for (String enumerationName : MetamodelCreator.getEnumerationNames()) {
      getMetamodelImpl().deleteEnumeration((EnumerationExpression) getMetamodelImpl().findTypeByName(TestContext.INSTANCE, enumerationName));
    }
    assertNull(typeIS().findPropertyByName("responsibility"));
    assertNull(typeIS().findPropertyByName("health"));
    assertNull(typeBP().findPropertyByName("responsibility"));
    assertNull(typeBU().findPropertyByName("responsibility"));
    assertNull(typeBM().findPropertyByName("responsibility"));
  }

  @Test
  public void removeProperty() {
    getMetamodelImpl().deleteProperty(typeIS().findPropertyByName("responsibility"));
    assertNull(typeIS().findPropertyByName("responsibility"));
    assertNotNull(getMetamodelImpl().findTypeByName(TestContext.INSTANCE, "responsibilityE"));
  }

  @Test
  public void removeRelationshipType() {
    getMetamodelImpl().deleteRelationshipType(typeBM());
    assertNull(getMetamodelImpl().findTypeByName("BM"));
    assertEquals(2, typeIS().getRelationshipEnds().size());
    assertEquals(2, typeBP().getRelationshipEnds().size());
    assertEquals(2, typeBU().getRelationshipEnds().size());
  }

  @Test
  public void removeSubstantialType() {
    getMetamodelImpl().deleteSubstantialType(typeIS());
    assertNull(getMetamodelImpl().findTypeByName("IS"));
  }

  private PrimitiveTypeExpression getDecimalPrimitiveType() {
    return (PrimitiveTypeExpression) getMetamodelImpl().findTypeByName(BigDecimal.class.getName());
  }

  private SubstantialTypeExpression typeIS() {
    return (SubstantialTypeExpression) getMetamodelImpl().findTypeByName(TestContext.INSTANCE, "IS");
  }

  private SubstantialTypeExpression typeBP() {
    return (SubstantialTypeExpression) getMetamodelImpl().findTypeByName(TestContext.INSTANCE, "BP");
  }

  private SubstantialTypeExpression typeBU() {
    return (SubstantialTypeExpression) getMetamodelImpl().findTypeByName(TestContext.INSTANCE, "BU");
  }

  private RelationshipTypeExpression typeBM() {
    return (RelationshipTypeExpression) getMetamodelImpl().findTypeByName(TestContext.INSTANCE, "BM");
  }
}

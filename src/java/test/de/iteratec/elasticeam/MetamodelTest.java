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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;


public class MetamodelTest {
  private MetamodelCreator metamodelCreator;

  @Before
  public void setUp() {
    this.metamodelCreator = new MetamodelCreator();
  }

  public Metamodel getMetamodel() {
    return this.metamodelCreator.getMetamodel();
  }

  @Test
  public void testEnumerations() {
    for (String enumerationName : MetamodelCreator.getEnumerationNames()) {
      assertTrue(getMetamodel().findTypeByName(enumerationName) instanceof EnumerationExpression);
    }
    assertEquals(2, getMetamodel().getEnumerationTypes().size());
  }

  @Test
  public void testSubstantialTypes() {
    for (String substantialTypeName : MetamodelCreator.getSubstantialTypeNames()) {
      assertTrue(getMetamodel().findTypeByName(substantialTypeName) instanceof SubstantialTypeExpression);
      SubstantialTypeExpression type = (SubstantialTypeExpression) getMetamodel().findTypeByName(substantialTypeName);
      assertNotNull(type.findPropertyByName("id"));
      assertNotNull(type.findPropertyByName("name"));
      assertNotNull(type.findPropertyByName("description"));
    }
    assertEquals(3, getMetamodel().getSubstantialTypes().size());
  }

  @Test
  public void testRelationshipTypes() {
    assertEquals(1, getMetamodel().getRelationshipTypes().size());
  }

  @Test
  public void testPropertyExistence() {
    SubstantialTypeExpression isExpr = (SubstantialTypeExpression) getMetamodel().findTypeByName("IS");
    assertNotNull(isExpr.findPropertyByName("costs"));
    assertNotNull(isExpr.findPropertyByName("responsibility"));
    assertNotNull(isExpr.findPropertyByName("version"));
    assertNotNull(isExpr.findPropertyByName("startDate"));
    assertNotNull(isExpr.findPropertyByName("health"));
    assertEquals(8, isExpr.getProperties().size());

    SubstantialTypeExpression bpExpr = (SubstantialTypeExpression) getMetamodel().findTypeByName("BP");
    assertNotNull(bpExpr.findPropertyByName("costs"));
    assertNotNull(bpExpr.findPropertyByName("responsibility"));
    assertNull(bpExpr.findPropertyByName("version"));
    assertNull(bpExpr.findPropertyByName("startDate"));
    assertNull(bpExpr.findPropertyByName("health"));
    assertEquals(5, bpExpr.getProperties().size());

    SubstantialTypeExpression buExpr = (SubstantialTypeExpression) getMetamodel().findTypeByName("BU");
    assertNotNull(buExpr.findPropertyByName("costs"));
    assertNotNull(buExpr.findPropertyByName("responsibility"));
    assertNull(buExpr.findPropertyByName("version"));
    assertNull(buExpr.findPropertyByName("startDate"));
    assertNull(buExpr.findPropertyByName("health"));
    assertEquals(5, buExpr.getProperties().size());
  }

  @Test
  public void testHiearchyExistence() {
    for (SubstantialTypeExpression type : getMetamodel().getSubstantialTypes()) {
      RelationshipEndExpression parentEnd = type.findRelationshipEndByName("parent");
      RelationshipEndExpression childEnd = type.findRelationshipEndByName("children");

      assertNotNull(childEnd);
      assertEquals(0, childEnd.getLowerBound());
      assertEquals(FeatureExpression.UNLIMITED, childEnd.getUpperBound());

      assertNotNull(parentEnd);
      assertEquals(0, parentEnd.getLowerBound());
      assertEquals(1, parentEnd.getUpperBound());

      assertEquals(type, childEnd.getType());
      assertEquals(type, parentEnd.getType());

      assertNotNull(childEnd.getRelationship());
      assertNotNull(parentEnd.getRelationship());
      assertEquals(childEnd.getRelationship(), parentEnd.getRelationship());

      RelationshipExpression rel = childEnd.getRelationship();
      assertEquals(parentEnd, rel.findRelationshipEndByName("parent"));
      assertEquals(childEnd, rel.findRelationshipEndByName("children"));

    }
  }

  @Test
  public void testRelationshipTypeExistence() {
    assertNotNull(getMetamodel().findTypeByName("BM"));
    RelationshipTypeExpression bmExpr = (RelationshipTypeExpression) getMetamodel().findTypeByName("BM");
    SubstantialTypeExpression isExpr = (SubstantialTypeExpression) getMetamodel().findTypeByName("IS");
    SubstantialTypeExpression bpExpr = (SubstantialTypeExpression) getMetamodel().findTypeByName("BP");
    SubstantialTypeExpression buExpr = (SubstantialTypeExpression) getMetamodel().findTypeByName("BU");

    assertNotNull(bmExpr.findPropertyByName("id"));
    assertNotNull(bmExpr.findPropertyByName("name"));
    assertNull(bmExpr.findPropertyByName("description"));
    assertNull(bmExpr.findPropertyByName("costs"));
    assertNotNull(bmExpr.findPropertyByName("responsibility"));
    assertNotNull(bmExpr.findPropertyByName("version"));
    assertNull(bmExpr.findPropertyByName("startDate"));
    assertNull(bmExpr.findPropertyByName("health"));

    assertEquals(3, bmExpr.getRelationshipEnds().size());
    assertNotNull(bmExpr.findRelationshipEndByName("information system"));
    assertNotNull(bmExpr.findRelationshipEndByName("business process"));
    assertNotNull(bmExpr.findRelationshipEndByName("business unit"));

    RelationshipEndExpression isBM = bmExpr.findRelationshipEndByName("information system");
    RelationshipEndExpression bpBM = bmExpr.findRelationshipEndByName("business process");
    RelationshipEndExpression buBM = bmExpr.findRelationshipEndByName("business unit");

    assertEquals(isExpr, isBM.getType());
    assertEquals(bmExpr, isBM.getRelationship().findRelationshipEndByName("business mapping").getType());
    assertEquals(isBM, isExpr.findRelationshipEndByName("business mapping").getRelationship().findRelationshipEndByName("information system"));

    assertEquals(bpExpr, bpBM.getType());
    assertEquals(bmExpr, bpBM.getRelationship().findRelationshipEndByName("business mapping").getType());
    assertEquals(bpBM, bpExpr.findRelationshipEndByName("business mapping").getRelationship().findRelationshipEndByName("business process"));

    assertEquals(buExpr, buBM.getType());
    assertEquals(bmExpr, buBM.getRelationship().findRelationshipEndByName("business mapping").getType());
    assertEquals(buBM, buExpr.findRelationshipEndByName("business mapping").getRelationship().findRelationshipEndByName("business unit"));
  }

}

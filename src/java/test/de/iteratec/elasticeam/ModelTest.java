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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.LinkExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


public class ModelTest {

  private MetamodelCreator metamodelCreator;

  @Before
  public void setUp() {
    this.metamodelCreator = new MetamodelCreator();
  }

  private Metamodel getMetamodel() {
    return this.metamodelCreator.getMetamodel();
  }

  private Model getModel() {
    return ModelCreator.createModel(getMetamodel());
  }

  @Test
  public void testCreateModel() {
    Model model = ModelCreator.createModel(getMetamodel());
    assertNotNull(model);
  }

  @Test
  public void testReadInstances() {
    Model model = getModel();

    for (SubstantialTypeExpression expr : getMetamodel().getSubstantialTypes()) {
      Collection<InstanceExpression> instances = model.findAll(expr);
      assertEquals(10, instances.size());
    }
  }

  @Test
  public void testReadPropertyValues() {
    Model model = getModel();

    for (SubstantialTypeExpression expr : getMetamodel().getSubstantialTypes()) {
      Collection<InstanceExpression> instances = model.findAll(expr);

      for (InstanceExpression instance : instances) {
        int instanceCountId = getInstanceCountId(instance);
        // check name
        assertEquals(instance, model.findByName(expr, expr.getName().toLowerCase() + instanceCountId));

        // check id is not null
        assertNotNull(instance.getValue(UniversalTypeExpression.ID_PROPERTY));

        // check description
        assertEquals(expr.getName().toLowerCase() + instanceCountId + "descr",
            instance.getValue(expr.findPropertyByName(MixinTypeNamed.DESCRIPTION_PROPERTY.getName())));

        // check costs
        BigDecimal cost = (BigDecimal) instance.getValue(expr.findPropertyByName("costs"));
        assertEquals(ModelCreator.costValues[instanceCountId - 1].compareTo(cost), 0);

        //check responsibility
        Collection<Object> respVals = instance.getValues(expr.findPropertyByPersistentName("responsibility"));

        assertNotNull(respVals);

        EnumerationExpression enumE = (EnumerationExpression) getMetamodel().findTypeByPersistentName("responsibilityE");
        assertTrue(respVals.contains(enumE.getLiterals().get(instanceCountId % 3)));
        assertTrue(respVals.contains(enumE.getLiterals().get((instanceCountId + 1) % 3)));

        if (expr.getName().equals("IS")) {
          // check version
          BigInteger version = (BigInteger) instance.getValue(expr.findPropertyByPersistentName("version"));
          assertEquals(instanceCountId, version.intValue());

          // check start date (existence)
          assertNotNull(instance.getValue(expr.findPropertyByPersistentName("startDate")));

          // check health
          EnumerationExpression enum2E = (EnumerationExpression) getMetamodel().findTypeByPersistentName("healthE");
          EnumerationLiteralExpression literal = enum2E.getLiterals().get(instanceCountId % 3);
          assertEquals(literal, instance.getValue(expr.findPropertyByPersistentName("health")));
        }
      }
    }
  }

  @Test
  public void testHierarchyLinks() {
    Model model = getModel();

    // hierarchy between is1 (parent) and is2, is3 (children), top down
    SubstantialTypeExpression expr = (SubstantialTypeExpression) getMetamodel().findTypeByName("IS");
    assertNotNull(expr);
    InstanceExpression is1 = model.findByName(expr, "is1");
    assertNotNull(is1);
    assertEquals(2, is1.getConnecteds(expr.findRelationshipEndByPersistentName("children")).size());
    assertEquals(3, is1.getConnecteds(expr.findRelationshipEndByPersistentName("business mapping")).size());

    List<UniversalModelExpression> hChildren = new ArrayList<UniversalModelExpression>();
    RelationshipEndExpression relEnd = expr.findRelationshipEndByPersistentName("children");
    for (UniversalModelExpression otherInstance : is1.getConnecteds(relEnd)) {
      assertNotNull(otherInstance);
      hChildren.add(otherInstance);
    }
    assertEquals(2, hChildren.size());
    assertTrue(hChildren.contains(model.findByName((SubstantialTypeExpression) getMetamodel().findTypeByPersistentName("IS"), "is2")));
    assertTrue(hChildren.contains(model.findByName((SubstantialTypeExpression) getMetamodel().findTypeByPersistentName("IS"), "is2")));

    // hierarchy between is9 (parent) and is8 (child)
    InstanceExpression is9 = model.findByName(expr, "is9");
    UniversalModelExpression is9parentE = is9.getConnected(expr.findRelationshipEndByPersistentName("parent"));
    assertNotNull(is9parentE);
    assertEquals(model.findByName((SubstantialTypeExpression) getMetamodel().findTypeByPersistentName("IS"), "is8"), is9parentE);
  }

  @Test
  public void testBMLinks() {
    Model model = getModel();

    // substantial types
    SubstantialTypeExpression isType = (SubstantialTypeExpression) getMetamodel().findTypeByPersistentName("IS");
    SubstantialTypeExpression bpType = (SubstantialTypeExpression) getMetamodel().findTypeByPersistentName("BP");
    SubstantialTypeExpression buType = (SubstantialTypeExpression) getMetamodel().findTypeByPersistentName("BU");
    // Note: a collection of BM types can also be fetched from the
    // getMetamodel().
    RelationshipTypeExpression bmType = (RelationshipTypeExpression) isType.findRelationshipEndByPersistentName("business mapping").getType();

    // prepare instances
    InstanceExpression is1 = model.findByName(isType, "is1");
    InstanceExpression is2 = model.findByName(isType, "is2");
    InstanceExpression is3 = model.findByName(isType, "is3");

    InstanceExpression bp1 = model.findByName(bpType, "bp1");
    InstanceExpression bp2 = model.findByName(bpType, "bp2");

    InstanceExpression bu1 = model.findByName(buType, "bu1");
    InstanceExpression bu2 = model.findByName(buType, "bu2");
    InstanceExpression bu3 = model.findByName(buType, "bu3");
    InstanceExpression bu4 = model.findByName(buType, "bu4");

    // check the bms of is1
    Collection<UniversalModelExpression> links = is1.getConnecteds(isType.findRelationshipEndByPersistentName("business mapping"));
    List<UniversalModelExpression> reachableBp = new ArrayList<UniversalModelExpression>();
    List<UniversalModelExpression> reachableBu = new ArrayList<UniversalModelExpression>();
    for (UniversalModelExpression link : links) {
      assertNotNull(link);
      assertNotNull(link.getValue(bmType.findPropertyByPersistentName("responsibility")));
      assertNotNull(link.getValue(bmType.findPropertyByPersistentName("version")));
      assertNotNull(link.getValue(UniversalTypeExpression.ID_PROPERTY));

      assertNotNull(link.getConnected(bmType.findRelationshipEndByPersistentName("business process")));
      reachableBp.add(link.getConnected(bmType.findRelationshipEndByPersistentName("business process")));
      assertNotNull(link.getConnected(bmType.findRelationshipEndByPersistentName("business unit")));
      reachableBu.add(link.getConnected(bmType.findRelationshipEndByPersistentName("business unit")));
    }
    assertTrue(reachableBp.contains(bp1));
    assertTrue(reachableBp.contains(bp2));
    assertTrue(reachableBu.contains(bu1));
    assertTrue(reachableBu.contains(bu2));

    // check the bms of bu3
    links = bu3.getConnecteds(buType.findRelationshipEndByName("business mapping"));
    reachableBp = new ArrayList<UniversalModelExpression>();
    List<UniversalModelExpression> reachableIs = new ArrayList<UniversalModelExpression>();
    for (UniversalModelExpression link : links) {
      assertNotNull(link.getValue(UniversalTypeExpression.ID_PROPERTY));
      assertNotNull(link.getValue(bmType.findPropertyByPersistentName("responsibility")));
      assertNotNull(link.getValue(bmType.findPropertyByPersistentName("version")));

      assertNotNull(link.getConnected(bmType.findRelationshipEndByPersistentName("information system")));
      reachableIs.add(link.getConnected(bmType.findRelationshipEndByPersistentName("information system")));
      assertNotNull(link.getConnected(bmType.findRelationshipEndByPersistentName("business process")));
      reachableBp.add(link.getConnected(bmType.findRelationshipEndByPersistentName("business process")));
    }
    assertTrue(reachableBp.contains(bp1));
    assertTrue(reachableIs.contains(is2));

    // check the bms of bp2
    links = bp2.getConnecteds(bpType.findRelationshipEndByPersistentName("business mapping"));
    reachableBu = new ArrayList<UniversalModelExpression>();
    reachableIs = new ArrayList<UniversalModelExpression>();
    for (UniversalModelExpression link : links) {
      assertNotNull(link.getValue(UniversalTypeExpression.ID_PROPERTY));
      assertNotNull(link.getValue(bmType.findPropertyByPersistentName("responsibility")));
      assertNotNull(link.getValue(bmType.findPropertyByPersistentName("version")));

      assertNotNull(link.getConnected(bmType.findRelationshipEndByPersistentName("information system")));
      reachableIs.add(link.getConnected(bmType.findRelationshipEndByPersistentName("information system")));
      assertNotNull(link.getConnected(bmType.findRelationshipEndByPersistentName("business unit")));
      reachableBu.add(link.getConnected(bmType.findRelationshipEndByPersistentName("business unit")));
    }
    assertTrue(reachableBu.contains(bu4));
    assertTrue(reachableIs.contains(is3));

  }

  @Test
  public void testDeleteInstance() {
    Model model = getModel();

    SubstantialTypeExpression bpType = (SubstantialTypeExpression) getMetamodel().findTypeByName("BP");
    SubstantialTypeExpression isType = (SubstantialTypeExpression) getMetamodel().findTypeByName("IS");
    RelationshipEndExpression isBMtype = isType.findRelationshipEndByName("business mapping");

    assertEquals(10, model.findAll(bpType).size());
    InstanceExpression is1 = model.findByName(isType, "is1");
    assertEquals(3, is1.getConnecteds(isBMtype).size());

    InstanceExpression bp1 = model.findByName(bpType, "bp1");
    model.delete(bp1);

    assertEquals(9, model.findAll(bpType).size());
    //so far ok...
    is1 = model.findByName(isType, "is1");
    assertEquals(1, is1.getConnecteds(isBMtype).size());
  }

  @Test
  public void testDeleteLink() {
    Model model = getModel();

    SubstantialTypeExpression isType = (SubstantialTypeExpression) getMetamodel().findTypeByName("IS");
    RelationshipEndExpression isBM = isType.findRelationshipEndByName("business mapping");
    RelationshipTypeExpression bmType = (RelationshipTypeExpression) isBM.getType();

    InstanceExpression is1 = model.findByName(isType, "is1");
    UniversalModelExpression bmLink = null;
    PropertyExpression<?> name = bmType.findPropertyByName("name");
    for (UniversalModelExpression link : is1.getConnecteds(isBM)) {
      if (link.getValue(name).equals("bm1")) {
        bmLink = link;
      }
    }
    assertNotNull(bmLink);

    model.delete((LinkExpression) bmLink);

    //the is1 link end was deleted:
    is1 = model.findByName(isType, "is1");
    bmLink = null;
    for (UniversalModelExpression link : is1.getConnecteds(isBM)) {
      if (link.getValue(name).equals("bm1")) {
        bmLink = link;
      }
    }
    assertNull(bmLink);

    //the bp1 link end was deleted:
    SubstantialTypeExpression bpType = (SubstantialTypeExpression) getMetamodel().findTypeByName("BP");
    RelationshipEndExpression bpBM = bpType.findRelationshipEndByName("business mapping");
    InstanceExpression bp1 = model.findByName(bpType, "bp1");
    bmLink = null;
    for (UniversalModelExpression link : bp1.getConnecteds(bpBM)) {
      if (link.getValue(name).equals("bm1")) {
        bmLink = link;
      }
    }
    assertNull(bmLink);

    //the bu1 link end was deleted:
    SubstantialTypeExpression buType = (SubstantialTypeExpression) getMetamodel().findTypeByName("BU");
    RelationshipEndExpression buBM = buType.findRelationshipEndByName("business mapping");
    InstanceExpression bu1 = model.findByName(buType, "bu1");
    bmLink = null;
    for (UniversalModelExpression link : bu1.getConnecteds(buBM)) {
      if (link.getValue(name).equals("bm1")) {
        bmLink = link;
      }
    }
    assertNull(bmLink);

  }

  @Test
  public void testDeletePropertyValue() {
    Model model = getModel();

    SubstantialTypeExpression isType = (SubstantialTypeExpression) getMetamodel().findTypeByName("IS");
    InstanceExpression is1 = model.findByName(isType, "is1");
    model.setValue(is1, isType.findPropertyByName("version"), null);

    assertNull(model.findByName(isType, "is1").getValue(isType.findPropertyByName("version")));
  }

  @Test
  public void testUpdatePropertyValue() {
    Model model = getModel();

    SubstantialTypeExpression isType = (SubstantialTypeExpression) getMetamodel().findTypeByName("IS");
    InstanceExpression is1 = model.findByName(isType, "is1");
    model.setValue(is1, MixinTypeNamed.NAME_PROPERTY, "newIs1");

    assertNull(model.findByName(isType, "is1"));
    assertNotNull(model.findByName(isType, "newIs1"));
  }

  @SuppressWarnings("PMD")
  private int getInstanceCountId(InstanceExpression instance) {
    String name = (String) instance.getValue(MixinTypeNamed.NAME_PROPERTY);
    int val = Integer.valueOf(String.valueOf(name.charAt(name.length() - 1))).intValue();
    if (val == 0) {
      val = 10;
    }
    return val;
  }
}

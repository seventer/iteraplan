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
package de.iteratec.elasticeam.operator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.elasticeam.MetamodelCreator;
import de.iteratec.elasticeam.ModelCreator;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.operator.objectify.ObjectifyingSubstantialType;


/**
 *
 */
public class ObjectifyOperatorTest {

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

  private SubstantialTypeExpression typeIS() {
    return (SubstantialTypeExpression) getMetamodel().findTypeByName("IS");
  }

  private SubstantialTypeExpression getObectifiedSubstantialType() {
    SubstantialTypeExpression isType = typeIS();
    PropertyExpression<?> health = isType.findPropertyByPersistentName("health");

    return new ObjectifyingSubstantialType(health);
  }

  @Test
  public void testCreateObjectifiedST() {
    getObectifiedSubstantialType();
  }

  @Test
  public void testObjectifiedProperty() {
    SubstantialTypeExpression ost = getObectifiedSubstantialType();
    assertEquals(3, ost.getProperties().size());
    assertNotNull(ost.findPropertyByPersistentName(MixinTypeNamed.NAME_PROPERTY.getPersistentName()));
    assertNotNull(ost.findPropertyByPersistentName(MixinTypeNamed.DESCRIPTION_PROPERTY.getPersistentName()));
    assertNotNull(ost.findPropertyByPersistentName(SubstantialTypeExpression.ID_PROPERTY.getPersistentName()));
  }

  @Test
  public void testObjectifiedRelationship() {
    SubstantialTypeExpression ost = getObectifiedSubstantialType();
    assertEquals(1, ost.getRelationshipEnds().size());
    assertNotNull(ost.findRelationshipEndByPersistentName("isValueOf"));

    RelationshipEndExpression implicitEnd = ost.findRelationshipEndByPersistentName("isValueOf");
    assertEquals(typeIS(), implicitEnd.getType());
    assertEquals(ost, implicitEnd.getHolder());

    assertNotNull(implicitEnd.getRelationship());
    RelationshipExpression relationship = implicitEnd.getRelationship();

    assertNotNull(relationship.findRelationshipEndByName("isValueOf"));
    assertNotNull(relationship.findRelationshipEndByName("valueOf(health)"));

    RelationshipEndExpression secondEnd = relationship.findRelationshipEndByPersistentName("valueOf(health)");
    assertEquals(typeIS(), secondEnd.getHolder());
    assertEquals(ost, secondEnd.getType());
  }

  @SuppressWarnings("cast")
  @Test
  public void testObjectifiedInstances() {
    SubstantialTypeExpression ost = getObectifiedSubstantialType();
    assertEquals(3, getModel().findAll(ost).size());

    PropertyExpression<?> nameProperty = ost.findPropertyByPersistentName("name");

    boolean goodExists = false;
    boolean mediumExists = false;
    boolean badExists = false;
    for (InstanceExpression e : (Collection<InstanceExpression>) getModel().findAll(ost)) {
      String name = e.getValue(nameProperty).toString();
      assertNotNull(name);
      System.out.println(name);
      if ("good".equals(name)) {
        goodExists = true;
      }
      else if ("medium".equals(name)) {
        mediumExists = true;
      }
      else if ("bad".equals(name)) {
        badExists = true;
      }
    }
    assertTrue(goodExists && mediumExists && badExists);

    //test with bindings
    RelationshipEndExpression relEnd = ost.findRelationshipEndByPersistentName("isValueOf");
    BindingSet mappings = getModel().findAll(relEnd);
    assertEquals(3, mappings.getAllFromElements().size());
    assertEquals(10, mappings.getAllToElements().size());

    UniversalModelExpression goodInstance = null;
    UniversalModelExpression mediumInstance = null;
    UniversalModelExpression badInstance = null;

    Set<UniversalModelExpression> allIS = mappings.getAllToElements();

    for (UniversalModelExpression e : mappings.getAllFromElements()) {
      if (e.getValue(nameProperty).toString().equals("good")) {
        assertEquals(3, mappings.getToBindings(e).size());
        goodInstance = e;
      }
      else if (e.getValue(nameProperty).toString().equals("medium")) {
        assertEquals(4, mappings.getToBindings(e).size());
        mediumInstance = e;
      }
      else {
        assertEquals(3, mappings.getToBindings(e).size());
        badInstance = e;
      }
    }

    //test with forward retrieval (from the objectified type)
    assertEquals(3, goodInstance.getConnecteds(relEnd).size());
    assertEquals(4, mediumInstance.getConnecteds(relEnd).size());
    assertEquals(3, badInstance.getConnecteds(relEnd).size());

    //test with backward retrieval (from the canonic ste)
    RelationshipEndExpression otherRelEnd = null;
    for (RelationshipEndExpression e : relEnd.getRelationship().getRelationshipEnds()) {
      if (!e.equals(relEnd)) {
        otherRelEnd = e;
      }
    }
    assertNotNull(otherRelEnd);
    for (UniversalModelExpression isInstance : allIS) {
      assertEquals(1, isInstance.getConnecteds(otherRelEnd).size());
    }

  }
}

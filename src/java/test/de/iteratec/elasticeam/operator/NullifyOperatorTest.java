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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.elasticeam.MetamodelCreator;
import de.iteratec.elasticeam.ModelCreator;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.operator.nullify.FromNullifiedRelationshipEnd;
import de.iteratec.iteraplan.elasticeam.operator.nullify.NullifiedModelExpression;
import de.iteratec.iteraplan.elasticeam.operator.nullify.NullifiedSubstantialType;
import de.iteratec.iteraplan.elasticeam.operator.nullify.ToNullifiedRelationshipEnd;


public class NullifyOperatorTest {

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

  private SubstantialTypeExpression createNullifiedST() {
    return new NullifiedSubstantialType(typeIS());
  }

  private RelationshipEndExpression createNullifiedRE() {
    RelationshipEndExpression baseRelEnd = typeIS().findRelationshipEndByName("business mapping");
    return new ToNullifiedRelationshipEnd(baseRelEnd);
  }

  @Test
  public void testCreateNullifiedSubstantialType() {
    createNullifiedST();
  }

  @Test
  public void testCreateNullifiedRelationshipEnd() {
    createNullifiedRE();
  }

  @Test
  public void testListNullifiedInstances() {
    SubstantialTypeExpression nullifiedIs = createNullifiedST();
    Collection<InstanceExpression> nullifiedInstances = getModel().findAll(nullifiedIs);
    assertEquals(11, nullifiedInstances.size());
    int realInstancesCount = 0;
    int nullifiedInstancesCount = 0;
    for (InstanceExpression expr : nullifiedInstances) {
      if (NullifiedModelExpression.class.isInstance(expr)) {
        nullifiedInstancesCount++;
      }
      else {
        realInstancesCount++;
      }
    }
    assertEquals(10, realInstancesCount);
    assertEquals(1, nullifiedInstancesCount);
  }

  @Test
  public void testEvaluateNullifiedUT() {
    SubstantialTypeExpression nullifiedIs = createNullifiedST();

    //test with hierarchy
    RelationshipEndExpression nullifiedParentRelEnd = nullifiedIs.findRelationshipEndByPersistentName("parent");
    assertEquals(FromNullifiedRelationshipEnd.class, nullifiedParentRelEnd.getClass());

    //is1 has no parent
    InstanceExpression is1 = getModel().findByName(typeIS(), "is1");
    Collection<UniversalModelExpression> nonPaprents = is1.getConnecteds(nullifiedParentRelEnd);
    assertEquals(0, nonPaprents.size());

    //binds the null is to all information systems that have no child
    BindingSet mapping = getModel().findAll(nullifiedParentRelEnd);

    UniversalModelExpression nullIs = null;
    for (UniversalModelExpression expr : mapping.getAllFromElements()) {
      if (NullifiedModelExpression.class.isInstance(expr)) {
        nullIs = expr;
      }
    }
    assertEquals(5, mapping.getToBindings(nullIs).size());
    assertEquals(5, nullIs.getConnecteds(nullifiedParentRelEnd).size());

    //test with business mapping
    RelationshipEndExpression nullifiedBmRelEnd = nullifiedIs.findRelationshipEndByPersistentName("business mapping");
    mapping = getModel().findAll(nullifiedBmRelEnd);

    // since all business mappings have an information system attached to them,
    // this binding set should contain no null information system
    nullIs = null;
    for (UniversalModelExpression expr : mapping.getAllFromElements()) {
      if (NullifiedModelExpression.class.isInstance(expr)) {
        nullIs = expr;
      }
    }
    assertNull(nullIs);
  }

  @Test
  public void testEvaluateNullifiedRE() {
    RelationshipEndExpression nullifiedBM = createNullifiedRE();
    assertEquals(ToNullifiedRelationshipEnd.class, nullifiedBM.getClass());

    //the null element is a 'to' element (right outer)
    BindingSet mapping = getModel().findAll(nullifiedBM);

    for (UniversalModelExpression expr : mapping.getAllToElements()) {
      if (NullifiedModelExpression.class.isInstance(expr)) {
        assertEquals(7, mapping.getFromBindings(expr).size());
      }
    }

    InstanceExpression is1 = getModel().findByName(typeIS(), "is1");
    UniversalModelExpression bm = is1.getConnected(nullifiedBM);
    assertFalse(NullifiedModelExpression.class.isInstance(bm));

    InstanceExpression is10 = getModel().findByName(typeIS(), "is10");
    bm = is10.getConnected(nullifiedBM);
    assertTrue(NullifiedModelExpression.class.isInstance(bm));
  }

  @Test
  public void testRelationship() {
    SubstantialTypeExpression nullifiedIS = createNullifiedST();

    RelationshipEndExpression parentFromNRE = nullifiedIS.findRelationshipEndByPersistentName("parent");
    assertEquals(FromNullifiedRelationshipEnd.class, parentFromNRE.getClass());

    RelationshipExpression relationship = parentFromNRE.getRelationship();
    RelationshipEndExpression parentToNRE = relationship.getOppositeEndFor(parentFromNRE);
    assertEquals(ToNullifiedRelationshipEnd.class, parentToNRE.getClass());

    BindingSet mapping1 = getModel().findAll(parentToNRE);

    BindingSet mapping2 = getModel().findAll(new ToNullifiedRelationshipEnd(typeIS().findRelationshipEndByName("children")));

    assertEquals(mapping1.getAllFromElements().size(), mapping2.getAllFromElements().size());
    assertEquals(mapping1.getAllToElements().size(), mapping2.getAllToElements().size());

  }

}

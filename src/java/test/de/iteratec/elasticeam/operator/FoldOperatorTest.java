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

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.elasticeam.MetamodelCreator;
import de.iteratec.elasticeam.ModelCreator;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.operator.fold.FoldLevelProperty;
import de.iteratec.iteraplan.elasticeam.operator.fold.UnfoldRelationshipEnd;


public class FoldOperatorTest {

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

  private PropertyExpression<?> getFoldLevelProperty() {
    return new FoldLevelProperty(typeIS().findRelationshipEndByName("children"));
  }

  private RelationshipEndExpression getUnfoldRelationshipEnd() {
    return new UnfoldRelationshipEnd(typeIS().findRelationshipEndByName("children"));
  }

  @Test
  public void testCreateFoldLevelProperty() {
    getFoldLevelProperty();
  }

  @Test
  public void testCreateUnfoldRelationshipEnd() {
    getUnfoldRelationshipEnd();
  }

  @Test
  public void testEvaluateFoldLevelProperty() {
    PropertyExpression<?> foldLevelProp = getFoldLevelProperty();
    PropertyExpression<?> nameProp = typeIS().findPropertyByName("name");

    for (InstanceExpression is : getModel().findAll(typeIS())) {
      if (is.getValue(nameProp).equals("is1")) {
        assertEquals(BigInteger.valueOf(2), is.getValue(foldLevelProp));
      }
      else if (is.getValue(nameProp).equals("is2")) {
        assertEquals(BigInteger.valueOf(1), is.getValue(foldLevelProp));
      }
      else if (is.getValue(nameProp).equals("is3")) {
        assertEquals(BigInteger.valueOf(1), is.getValue(foldLevelProp));
      }
      else if (is.getValue(nameProp).equals("is4")) {
        assertEquals(BigInteger.valueOf(0), is.getValue(foldLevelProp));
      }
      else if (is.getValue(nameProp).equals("is5")) {
        assertEquals(BigInteger.valueOf(0), is.getValue(foldLevelProp));
      }
      else if (is.getValue(nameProp).equals("is6")) {
        assertEquals(BigInteger.valueOf(0), is.getValue(foldLevelProp));
      }
      else if (is.getValue(nameProp).equals("is7")) {
        assertEquals(BigInteger.valueOf(2), is.getValue(foldLevelProp));
      }
      else if (is.getValue(nameProp).equals("is8")) {
        assertEquals(BigInteger.valueOf(1), is.getValue(foldLevelProp));
      }
      else if (is.getValue(nameProp).equals("is9")) {
        assertEquals(BigInteger.valueOf(0), is.getValue(foldLevelProp));
      }
      else if (is.getValue(nameProp).equals("is10")) {
        assertEquals(BigInteger.valueOf(0), is.getValue(foldLevelProp));
      }
    }
  }

  @Test
  public void testEvaluateUnfoldRelationshipEnd() {
    RelationshipEndExpression unfoldEnd = getUnfoldRelationshipEnd();
    PropertyExpression<?> nameProp = typeIS().findPropertyByName("name");

    for (InstanceExpression is : getModel().findAll(typeIS())) {
      if (is.getValue(nameProp).equals("is1")) {
        assertEquals(5, is.getConnecteds(unfoldEnd).size());
      }
      else if (is.getValue(nameProp).equals("is2")) {
        assertEquals(2, is.getConnecteds(unfoldEnd).size());
      }
      else if (is.getValue(nameProp).equals("is3")) {
        assertEquals(1, is.getConnecteds(unfoldEnd).size());
      }
      else if (is.getValue(nameProp).equals("is4")) {
        assertEquals(0, is.getConnecteds(unfoldEnd).size());
      }
      else if (is.getValue(nameProp).equals("is5")) {
        assertEquals(0, is.getConnecteds(unfoldEnd).size());
      }
      else if (is.getValue(nameProp).equals("is6")) {
        assertEquals(0, is.getConnecteds(unfoldEnd).size());
      }
      else if (is.getValue(nameProp).equals("is7")) {
        assertEquals(2, is.getConnecteds(unfoldEnd).size());
      }
      else if (is.getValue(nameProp).equals("is8")) {
        assertEquals(1, is.getConnecteds(unfoldEnd).size());
      }
      else if (is.getValue(nameProp).equals("is9")) {
        assertEquals(0, is.getConnecteds(unfoldEnd).size());
      }
      else if (is.getValue(nameProp).equals("is10")) {
        assertEquals(0, is.getConnecteds(unfoldEnd).size());
      }
    }
  }

  @Test
  public void testUnfoldBindingSet() {
    RelationshipEndExpression unfolfEnd = getUnfoldRelationshipEnd();
    PropertyExpression<?> nameProp = typeIS().findPropertyByName("name");

    BindingSet mapping = getModel().findAll(unfolfEnd);
    assertEquals(5, mapping.getAllFromElements().size());
    assertEquals(7, mapping.getAllToElements().size());

    for (UniversalModelExpression is : mapping.getAllFromElements()) {
      if (is.getValue(nameProp).equals("is1")) {
        assertEquals(5, mapping.getToBindings(is).size());
      }
      else if (is.getValue(nameProp).equals("is2")) {
        assertEquals(2, mapping.getToBindings(is).size());
      }
      else if (is.getValue(nameProp).equals("is3")) {
        assertEquals(1, mapping.getToBindings(is).size());
      }
      else if (is.getValue(nameProp).equals("is7")) {
        assertEquals(2, mapping.getToBindings(is).size());
      }
      else if (is.getValue(nameProp).equals("is8")) {
        assertEquals(1, mapping.getToBindings(is).size());
      }
    }

    //inverse relationship
    RelationshipEndExpression inverseUnfoldRelEnd = null;
    for (RelationshipEndExpression relEnd : unfolfEnd.getRelationship().getRelationshipEnds()) {
      if (!relEnd.equals(unfolfEnd)) {
        inverseUnfoldRelEnd = relEnd;
        break;
      }
    }

    BindingSet inverseMapping = getModel().findAll(inverseUnfoldRelEnd);
    assertEquals(7, inverseMapping.getAllFromElements().size());
    assertEquals(5, inverseMapping.getAllToElements().size());

    for (UniversalModelExpression is : inverseMapping.getAllFromElements()) {
      if (is.getValue(nameProp).equals("is2")) {
        assertEquals(1, inverseMapping.getToBindings(is).size());
      }
      else if (is.getValue(nameProp).equals("is3")) {
        assertEquals(1, inverseMapping.getToBindings(is).size());
      }
      else if (is.getValue(nameProp).equals("is4")) {
        assertEquals(2, inverseMapping.getToBindings(is).size());
      }
      else if (is.getValue(nameProp).equals("is5")) {
        assertEquals(2, inverseMapping.getToBindings(is).size());
      }
      else if (is.getValue(nameProp).equals("is6")) {
        assertEquals(2, inverseMapping.getToBindings(is).size());
      }
      else if (is.getValue(nameProp).equals("is8")) {
        assertEquals(1, inverseMapping.getToBindings(is).size());
      }
      else if (is.getValue(nameProp).equals("is9")) {
        assertEquals(2, inverseMapping.getToBindings(is).size());
      }
    }
  }

  //FIXME enable when validation of cyclic paths is on
  //  @Test(expected = ModelException.class)
  //  public void testNonCyclicProperty() {
  //    new FoldLevelProperty(typeIS().findRelationshipEndByName("business mapping"));
  //  }

  //FIXME enable when validation of cyclic paths is on
  //  @Test(expected = ModelException.class)
  //  public void testNonCyclicRelationshipEnd() {
  //    new UnfoldRelationshipEnd(typeIS().findRelationshipEndByName("business mapping"));
  //  }

}

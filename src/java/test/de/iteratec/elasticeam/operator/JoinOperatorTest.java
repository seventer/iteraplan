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

import org.junit.Before;
import org.junit.Test;

import de.iteratec.elasticeam.MetamodelCreator;
import de.iteratec.elasticeam.ModelCreator;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.operator.join.JoinedRelationshipEnd;


/**
 *
 */
public class JoinOperatorTest {

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

  private RelationshipEndExpression getJoinedRelationshipEnd() {
    RelationshipEndExpression firstRelEnd = typeIS().findRelationshipEndByPersistentName("business mapping");
    RelationshipEndExpression secondRelEnd = firstRelEnd.getType().findRelationshipEndByPersistentName("business process");

    return new JoinedRelationshipEnd(firstRelEnd, secondRelEnd);
  }

  private RelationshipEndExpression getOpposite(RelationshipEndExpression forRelationshipEnd) {
    for (RelationshipEndExpression relEnd : forRelationshipEnd.getRelationship().getRelationshipEnds()) {
      if (!relEnd.equals(forRelationshipEnd)) {
        return relEnd;
      }
    }
    return null;
  }

  @Test
  public void testCreateJoinedRelationshipEnd() {
    getJoinedRelationshipEnd();
  }

  @Test
  public void testImplicitRelationship() {
    RelationshipEndExpression jRelEnd = getJoinedRelationshipEnd();
    assertNotNull(jRelEnd);
    assertNotNull(jRelEnd.getRelationship());

    RelationshipExpression rel = jRelEnd.getRelationship();
    assertEquals(2, rel.getRelationshipEnds().size());

    RelationshipEndExpression oppositeEnd = getOpposite(jRelEnd);
    assertNotNull(oppositeEnd);
    assertEquals(jRelEnd.getHolder(), oppositeEnd.getType());
    assertEquals(jRelEnd.getType(), oppositeEnd.getHolder());
  }

  @Test
  public void testInstanceRetrieval() {
    RelationshipEndExpression jRelEnd = getJoinedRelationshipEnd();

    BindingSet bindings = getModel().findAll(jRelEnd);
    // Correct evaluation should produce the following
    //    From: IS (SubstantialTypeImpl)
    //    To:BP (SubstantialTypeImpl)
    //    Unique first components: 3
    //    Unique second components: 2
    //    Total bindings: 4
    //   Bindings:  { 
    //    (IS 80 ,BP 91), 
    //    (IS 80 ,BP 90), 
    //    (IS 81 ,BP 90), 
    //    (IS 82 ,BP 91), 
    //   }
    assertEquals(3, bindings.getAllFromElements().size());
    assertEquals(2, bindings.getAllToElements().size());

    int bindingsCount = 0;
    for (UniversalModelExpression first : bindings.getAllFromElements()) {
      bindingsCount = bindingsCount + bindings.getToBindings(first).size();
    }
    assertEquals(4, bindingsCount);

    //test inverse relationship end
    BindingSet inverseBindings = getModel().findAll(getOpposite(jRelEnd));
    // Correct evaluation should produce the following
    //    From: BP (SubstantialTypeImpl)
    //    To:IS (SubstantialTypeImpl)
    //    Unique first components: 2
    //    Unique second components: 3
    //    Total bindings: 4
    //   Bindings:  { 
    //    (BP 91 ,IS 80), 
    //    (BP 91 ,IS 82), 
    //    (BP 90 ,IS 80), 
    //    (BP 90 ,IS 81), 
    //   }
    assertEquals(2, inverseBindings.getAllFromElements().size());
    assertEquals(3, inverseBindings.getAllToElements().size());

    bindingsCount = 0;
    for (UniversalModelExpression first : inverseBindings.getAllFromElements()) {
      bindingsCount = bindingsCount + inverseBindings.getToBindings(first).size();
    }
    assertEquals(4, bindingsCount);

    //test correctness for each single entity (use inverse bindings)
    //    for (UniversalModelExpression bp : inverseBindings.getAllFromElements()) {
    //      assertEquals(2, bp.getConnecteds(getOpposite(jRelEnd)).size());
    //    }

  }
}

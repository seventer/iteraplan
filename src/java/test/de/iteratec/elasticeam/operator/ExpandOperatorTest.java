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

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;

import de.iteratec.elasticeam.MetamodelCreator;
import de.iteratec.elasticeam.ModelCreator;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.ComparisonOperators;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.operator.expand.ExpandedSubstantialType;
import de.iteratec.iteraplan.elasticeam.operator.expand.ToExpandedRelationshipEnd;
import de.iteratec.iteraplan.elasticeam.operator.filter.FilteredSubstantialType;
import de.iteratec.iteraplan.elasticeam.operator.filter.predicate.FilterPredicates;


public class ExpandOperatorTest {

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

  private SubstantialTypeExpression getIS1() {
    PropertyExpression<?> nameProperty = typeIS().findPropertyByName("name");
    Predicate<UniversalModelExpression> nameFilterPredicate = FilterPredicates.buildStringPredicate(ComparisonOperators.EQUALS, nameProperty, "is1");
    return new FilteredSubstantialType(typeIS(), nameFilterPredicate);
  }

  private SubstantialTypeExpression getExpandedType() {
    SubstantialTypeExpression filteredIS = getIS1();
    return new ExpandedSubstantialType(filteredIS.findRelationshipEndByPersistentName("children"));
  }

  @Test
  public void testCreateExpandedType() {
    getExpandedType();
  }

  @Test
  public void testEvaluateExpandedType() {
    SubstantialTypeExpression is1Filter = getIS1();
    assertEquals(1, getModel().findAll(is1Filter).size());

    SubstantialTypeExpression extendedType = getExpandedType();
    assertEquals(3, getModel().findAll(extendedType).size());
  }

  //FIXME enable when universal type compatibility is implemented.
  //  @Test(expected = ModelException.class)
  //  public void testNonCyclicRelationship() {
  //    SubstantialTypeExpression filteredIS = getIS1();
  //    new ExpandedSubstantialType(filteredIS.findRelationshipEndByPersistentName("business mapping"));
  //  }

  @Test
  public void testRelationshipEnds() {
    SubstantialTypeExpression filteredIS = getIS1();
    RelationshipEndExpression toExpandedEnd = new ToExpandedRelationshipEnd(filteredIS.findRelationshipEndByPersistentName("children"));

    InstanceExpression is1 = getModel().findAll(filteredIS).iterator().next();
    assertEquals(3, is1.getConnecteds(toExpandedEnd).size());

    RelationshipEndExpression fromExpandedType = toExpandedEnd.getRelationship().getOppositeEndFor(toExpandedEnd);
    assertEquals(1, is1.getConnecteds(fromExpandedType).size());

  }

}

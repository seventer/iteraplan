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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.elasticeam.MetamodelCreator;
import de.iteratec.elasticeam.ModelCreator;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitivePropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.operator.rangify.Range;
import de.iteratec.iteraplan.elasticeam.operator.rangify.RangifiedProperty;


public class RangifyOperatorTest {

  private MetamodelCreator         metamodelCreator;

  private static final List<Range> overlappingRanges = new ArrayList<Range>();
  private static final List<Range> normalRanges      = new ArrayList<Range>();

  static {
    normalRanges.add(new Range("range1", BigDecimal.valueOf(-2), BigDecimal.valueOf(7)));
    normalRanges.add(new Range("range2", BigDecimal.valueOf(7.2), BigDecimal.valueOf(14)));
    normalRanges.add(new Range("range3", BigDecimal.valueOf(14.2), BigDecimal.valueOf(15.5)));
    normalRanges.add(new Range("range4", BigDecimal.valueOf(17), BigDecimal.valueOf(19)));
    normalRanges.add(new Range("range5", BigDecimal.valueOf(23), BigDecimal.valueOf(37)));
    normalRanges.add(new Range("range6", BigDecimal.valueOf(100), BigDecimal.valueOf(120)));
    normalRanges.add(new Range("range7", BigDecimal.valueOf(1000), BigDecimal.valueOf(10000)));

    overlappingRanges.add(new Range("range1", BigDecimal.valueOf(-2), BigDecimal.valueOf(2)));
    overlappingRanges.add(new Range("range2", BigDecimal.valueOf(1), BigDecimal.valueOf(5)));
  }

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

  private RangifiedProperty getRangifiedProperty() {
    PropertyExpression<?> baseProperty = typeIS().findPropertyByPersistentName("costs");
    return new RangifiedProperty((PrimitivePropertyExpression) baseProperty, normalRanges);
  }

  @Test
  public void testCreateValidProperty() {
    getRangifiedProperty();
  }

  @Test(expected = ModelException.class)
  public void testOverlappingRanges() {
    PropertyExpression<?> baseProperty = typeIS().findPropertyByPersistentName("costs");
    new RangifiedProperty((PrimitivePropertyExpression) baseProperty, overlappingRanges);
  }

  @Test(expected = ModelException.class)
  public void testNonNumericDataType() {
    PropertyExpression<?> baseProperty = typeIS().findPropertyByPersistentName("startDate");
    RangifiedProperty prop = new RangifiedProperty((PrimitivePropertyExpression) baseProperty, normalRanges);
    getModel().findAll(typeIS()).iterator().next().getValue(prop);
  }

  @Test
  public void testEvaluateProperty() {
    RangifiedProperty property = getRangifiedProperty();
    Collection<InstanceExpression> isInstances = getModel().findAll(typeIS());
    PropertyExpression<?> costs = typeIS().findPropertyByPersistentName("costs");

    for (InstanceExpression instance : isInstances) {
      assertEquals(1, instance.getValues(property).size());
      if (((BigDecimal) instance.getValue(costs)).compareTo(BigDecimal.valueOf(7.1)) == -1) {
        assertEquals("[-2;7]", ((EnumerationLiteralExpression) instance.getValue(property)).getName());
      }
      else {
        assertEquals("[7.2;14]", ((EnumerationLiteralExpression) instance.getValue(property)).getName());
      }
    }
  }
}

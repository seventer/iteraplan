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
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;

import de.iteratec.elasticeam.MetamodelCreator;
import de.iteratec.elasticeam.ModelCreator;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.ComparisonOperators;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.operator.filter.FilteredSubstantialType;
import de.iteratec.iteraplan.elasticeam.operator.filter.predicate.FilterPredicates;


public class FilterOperatorTest {

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

  @Test
  public void testIntegerFilter() {
    Model model = getModel();
    SubstantialTypeExpression typeIS = typeIS();
    InstanceExpression is = model.findAll(typeIS).iterator().next();
    PropertyExpression<?> idProperty = typeIS.findPropertyByName("id");

    Predicate<UniversalModelExpression> predicate = FilterPredicates.buildIntegerPredicate(ComparisonOperators.EQUALS, idProperty,
        (BigInteger) is.getValue(idProperty));

    FilteredSubstantialType filteredTypeIS = new FilteredSubstantialType(typeIS, predicate);
    assertEquals(1, model.findAll(filteredTypeIS).size());
  }

  @Test
  public void testDecimalFilter() {
    Model model = getModel();
    SubstantialTypeExpression typeIS = typeIS();
    PropertyExpression<?> costsProperty = typeIS.findPropertyByName("costs");

    Predicate<UniversalModelExpression> predGreater = FilterPredicates.buildDoublePredicate(ComparisonOperators.GREATER, costsProperty,
        BigDecimal.valueOf(7.5));
    Predicate<UniversalModelExpression> predGrEq = FilterPredicates.buildDoublePredicate(ComparisonOperators.GREATER_EQUALS, costsProperty,
        BigDecimal.valueOf(7.5));

    FilteredSubstantialType isGreater = new FilteredSubstantialType(typeIS, predGreater);
    FilteredSubstantialType isGrEq = new FilteredSubstantialType(typeIS, predGrEq);

    assertEquals(4, model.findAll(isGreater).size());
    assertEquals(5, model.findAll(isGrEq).size());
  }

  @Test
  public void testDateFilter() {
    Model model = getModel();
    SubstantialTypeExpression typeIS = typeIS();
    PropertyExpression<?> startDate = typeIS.findPropertyByName("startDate");

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    Predicate<UniversalModelExpression> olderIs = null;
    Predicate<UniversalModelExpression> newerIs = null;
    Predicate<UniversalModelExpression> onIs = null;

    try {
      olderIs = FilterPredicates.buildDatePredicate(ComparisonOperators.LESS, startDate, format.parse(ModelCreator.startDates[4]));
      newerIs = FilterPredicates.buildDatePredicate(ComparisonOperators.GREATER, startDate, format.parse(ModelCreator.startDates[4]));
      onIs = FilterPredicates.buildDatePredicate(ComparisonOperators.EQUALS, startDate, format.parse(ModelCreator.startDates[4]));
    } catch (ParseException e) {
      throw new ModelException(ModelException.GENERAL_ERROR, "Failed to parse dates.");
    }

    FilteredSubstantialType isBefore = new FilteredSubstantialType(typeIS, olderIs);
    FilteredSubstantialType isAfter = new FilteredSubstantialType(typeIS, newerIs);
    FilteredSubstantialType isOn = new FilteredSubstantialType(typeIS, onIs);

    assertEquals(4, model.findAll(isBefore).size());
    assertEquals(5, model.findAll(isAfter).size());
    assertEquals(1, model.findAll(isOn).size());
  }

}

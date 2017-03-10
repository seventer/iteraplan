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
package de.iteratec.iteraplan.elasticeam.iteraql2.qt.predicate;

import de.iteratec.iteraplan.elasticeam.iteraql2.IteraQl2Exception;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompilationUnit;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompileUtil;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.PredicateCompilationUnit;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.QueryCompilationContext;
import de.iteratec.iteraplan.elasticeam.metamodel.ComparisonOperatorExpression;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.operator.filter.predicate.FilterPredicates;


/**
 * Captures a simple predicate consisting of a property, a comparison operator and
 * a reference value (which in return may contain another property).
 */
public class PredicateLeaf extends Predicate {

  //hides either a basic prop, or a derived one (e.g. foldLevel)
  private PredicateLeafProperty        onProperty;

  //holds the operation to apply, e.g. < or beginsWith
  private ComparisonOperatorExpression operator;

  //hides the reference value, which is either a value or another property
  private PredicateLeafReferenceValue  referenceValue;

  public PredicateLeaf(PredicateLeafProperty onProperty, ComparisonOperatorExpression operator, PredicateLeafReferenceValue referenceValue) {
    this.onProperty = onProperty;
    this.operator = operator;
    this.referenceValue = referenceValue;
  }

  public String toString() {
    return onProperty.toString() + operator.getPersistentName() + referenceValue.toString();
  }

  /**{@inheritDoc}**/
  public CompilationUnit compile(QueryCompilationContext context) {
    CompilationUnit contextCopilationUnit = context.getContextCompilationUnit();
    CompilationUnit onPropertyCompilation = onProperty.compile(context);
    context.setContextCompilationUnit(contextCopilationUnit);
    CompilationUnit referenceValueCompilationUnit = referenceValue.compile(context);

    com.google.common.base.Predicate<UniversalModelExpression> filterPredicate = null;
    if (CompileUtil.isPropertyCompilationUnit(referenceValueCompilationUnit)) {
      filterPredicate = FilterPredicates.buildLeafPredicate(operator, CompileUtil.asPropertyCompilationUnit(onPropertyCompilation)
          .getCompilationResult(), CompileUtil.asPropertyCompilationUnit(referenceValueCompilationUnit).getCompilationResult());
    }
    else if (CompileUtil.isValueCompilationUnit(referenceValueCompilationUnit)) {
      filterPredicate = FilterPredicates.buildLeafPredicate(operator, CompileUtil.asPropertyCompilationUnit(onPropertyCompilation)
          .getCompilationResult(), CompileUtil.asValueCompilationUnit(referenceValueCompilationUnit).getCompilationResult());
    }
    else {
      throw new IteraQl2Exception(IteraQl2Exception.INCOMPATIBLE_COMPILATION_UNITS,
          "A leaf predicate can only have a value or a reference property, but not the following compilation unit: " + referenceValueCompilationUnit);
    }

    return new PredicateCompilationUnit(filterPredicate);
  }
}

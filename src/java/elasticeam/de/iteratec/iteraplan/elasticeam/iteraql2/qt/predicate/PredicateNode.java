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

import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompilationUnit;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompileUtil;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.PredicateCompilationUnit;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.QueryCompilationContext;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.operator.filter.predicate.CompositePredicateOperation;
import de.iteratec.iteraplan.elasticeam.operator.filter.predicate.FilterPredicates;


/**
 * A query tree element which represents a composition of one or more predicates.
 */
public class PredicateNode extends Predicate {

  private Predicate                   subPredicate1;
  private Predicate                   subPredicate2;
  private CompositePredicateOperation operation;

  public PredicateNode(Predicate subPredicate1, Predicate subPredicate2, CompositePredicateOperation operation) {
    //Note: in case of a NOT the subPred2 is null!!!
    this.subPredicate1 = subPredicate1;
    this.subPredicate2 = subPredicate2;
    this.operation = operation;
  }

  public String toString() {
    String result = subPredicate1.toString() + operation.toString();
    if (subPredicate2 != null) {
      result = result + subPredicate2.toString();
    }
    return result;
  }

  /**{@inheritDoc}**/
  public CompilationUnit compile(QueryCompilationContext context) {
    CompilationUnit contextCompilationUnit = context.getContextCompilationUnit();
    CompilationUnit firstSubPredicateCompilation = subPredicate1.compile(context);
    if (CompositePredicateOperation.ENCLOSE.equals(operation)) {
      return firstSubPredicateCompilation;
    }
    List<com.google.common.base.Predicate<UniversalModelExpression>> subPredicates = Lists.newArrayList();
    subPredicates.add(CompileUtil.asPredicateCompilationUnit(firstSubPredicateCompilation).getCompilationResult());

    if (subPredicate2 != null) {
      context.setContextCompilationUnit(contextCompilationUnit);
      CompilationUnit secondSubCompilationUnit = subPredicate2.compile(context);
      subPredicates.add(CompileUtil.asPredicateCompilationUnit(secondSubCompilationUnit).getCompilationResult());
    }

    com.google.common.base.Predicate<UniversalModelExpression> filterPredicate = FilterPredicates.buildCompositePredicate(operation, subPredicates);
    return new PredicateCompilationUnit(filterPredicate);
  }
}

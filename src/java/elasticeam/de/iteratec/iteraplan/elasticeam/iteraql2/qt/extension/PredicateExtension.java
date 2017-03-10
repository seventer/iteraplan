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
package de.iteratec.iteraplan.elasticeam.iteraql2.qt.extension;

import de.iteratec.iteraplan.elasticeam.iteraql2.IteraQl2Exception;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompilationUnit;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompileUtil;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.QueryCompilationContext;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.RelationshipEndCompilationUnit;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.UniversalTypeCompilationUnit;
import de.iteratec.iteraplan.elasticeam.iteraql2.qt.predicate.Predicate;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.operator.filter.FilteredRelationshipType;
import de.iteratec.iteraplan.elasticeam.operator.filter.FilteredSubstantialType;
import de.iteratec.iteraplan.elasticeam.operator.filter.ToFilteredRelationshipEnd;


/**
 * A query tree element which represents the filter operator.
 */
public class PredicateExtension implements Extension {

  private Predicate predicate;

  public PredicateExtension(Predicate predicate) {
    this.predicate = predicate;
  }

  public String toString() {
    return "[" + predicate.toString() + "]";
  }

  /**{@inheritDoc}**/
  public CompilationUnit compile(QueryCompilationContext context) {
    CompilationUnit contextCompilationUnit = context.getContextCompilationUnit();
    CompilationUnit predicateCompilation = predicate.compile(context);

    com.google.common.base.Predicate<UniversalModelExpression> filterPredicate = CompileUtil.asPredicateCompilationUnit(predicateCompilation)
        .getCompilationResult();

    if (CompileUtil.isUniversalTypeCompilationUnit(contextCompilationUnit)) {
      UniversalTypeExpression baseType = CompileUtil.asUniversalTypeCompilationUnit(contextCompilationUnit).getCompilationResult();
      UniversalTypeExpression filteredType = null;
      if (baseType instanceof SubstantialTypeExpression) {
        filteredType = new FilteredSubstantialType((SubstantialTypeExpression) baseType, filterPredicate);
      }
      else {
        filteredType = new FilteredRelationshipType((RelationshipTypeExpression) baseType, filterPredicate);
      }
      return new UniversalTypeCompilationUnit(filteredType);
    }
    else if (CompileUtil.isRelationshipEndCompilationUnit(contextCompilationUnit)) {
      RelationshipEndExpression baseRelationshipEnd = CompileUtil.asRelationshipEndCompilationUnit(contextCompilationUnit).getCompilationResult();
      return new RelationshipEndCompilationUnit(new ToFilteredRelationshipEnd(baseRelationshipEnd, filterPredicate));
    }
    else {
      throw new IteraQl2Exception(IteraQl2Exception.INCOMPATIBLE_COMPILATION_UNITS,
          "A filter can only be applied to universal type and relationship end compilation units, but not the following compilation unit: "
              + contextCompilationUnit);
    }
  }

}

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
import de.iteratec.iteraplan.elasticeam.iteraql2.qt.predicate.PredicateLeafProperty;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.operator.join.JoinedRelationshipEnd;
import de.iteratec.iteraplan.elasticeam.operator.objectify.ObjectifyingSubstantialType;
import de.iteratec.iteraplan.elasticeam.operator.objectify.ToObjectifiedRelationshipEnd;


/**
 * A query tree element which represents the objectify operator.
 */
public class ObjectifyExtension implements Extension {

  private PredicateLeafProperty property;

  public ObjectifyExtension(PredicateLeafProperty property) {
    this.property = property;
  }

  public String toString() {
    return ".objectify(" + property.toString() + ")";
  }

  /**{@inheritDoc}**/
  public CompilationUnit compile(QueryCompilationContext context) {
    CompilationUnit contextCompilationUnit = context.getContextCompilationUnit();
    if (contextCompilationUnit == null) {
      throw new IteraQl2Exception(IteraQl2Exception.EXTENSION_MISSING_CONTEXT, "No context compilation unit was provided for the extension.");
    }
    CompilationUnit propertyCompilationUnit = property.compile(context);

    if (CompileUtil.isUniversalTypeCompilationUnit(contextCompilationUnit)) {
      if (!(CompileUtil.asUniversalTypeCompilationUnit(contextCompilationUnit).getCompilationResult() instanceof SubstantialTypeExpression)) {
        throw new IteraQl2Exception(IteraQl2Exception.EXTENSION_WRONG_CONTEXT, "The objectify extension can only be used for substantial types.");
      }
      SubstantialTypeExpression objectified = new ObjectifyingSubstantialType(CompileUtil.asPropertyCompilationUnit(propertyCompilationUnit)
          .getCompilationResult());
      return new UniversalTypeCompilationUnit(objectified);
    }
    else if (CompileUtil.isRelationshipEndCompilationUnit(contextCompilationUnit)) {
      if (!(CompileUtil.asRelationshipEndCompilationUnit(contextCompilationUnit).getCompilationResult().getType() instanceof SubstantialTypeExpression)) {
        throw new IteraQl2Exception(IteraQl2Exception.EXTENSION_WRONG_CONTEXT, "The objectify extension can only be used for substantial types.");
      }
      RelationshipEndExpression objectified = new ToObjectifiedRelationshipEnd(CompileUtil.asPropertyCompilationUnit(propertyCompilationUnit)
          .getCompilationResult());
      RelationshipEndExpression relEnd = new JoinedRelationshipEnd(CompileUtil.asRelationshipEndCompilationUnit(contextCompilationUnit)
          .getCompilationResult(), objectified);
      return new RelationshipEndCompilationUnit(relEnd);
    }
    throw new IteraQl2Exception(IteraQl2Exception.EXTENSION_WRONG_CONTEXT, "The nullify extension can not be used in the current context: "
        + context.getContextCompilationUnit());
  }

}

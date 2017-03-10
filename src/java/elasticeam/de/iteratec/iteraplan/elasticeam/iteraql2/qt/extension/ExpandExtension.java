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
import de.iteratec.iteraplan.elasticeam.iteraql2.qt.RelationshipEnd;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.operator.expand.ExpandedSubstantialType;
import de.iteratec.iteraplan.elasticeam.operator.expand.ToExpandedRelationshipEnd;
import de.iteratec.iteraplan.elasticeam.operator.join.JoinedRelationshipEnd;


/**
 * A query tree element which represents the expand operator.
 */
public class ExpandExtension implements Extension {

  private RelationshipEnd relationshipEnd;

  public ExpandExtension(RelationshipEnd relationshipEnd) {
    this.relationshipEnd = relationshipEnd;
  }

  public String toString() {
    return ".expand(" + relationshipEnd.toString() + ")";
  }

  /**{@inheritDoc}**/
  public CompilationUnit compile(QueryCompilationContext context) {
    if (context.getContextCompilationUnit() == null) {
      throw new IteraQl2Exception(IteraQl2Exception.EXTENSION_MISSING_CONTEXT, "No context compilation unit was provided for the extension.");
    }
    CompilationUnit contextCompilationUnit = context.getContextCompilationUnit();
    CompilationUnit expandingRelEnd = relationshipEnd.compile(context);

    if (CompileUtil.isUniversalTypeCompilationUnit(contextCompilationUnit)) {
      UniversalTypeExpression baseType = CompileUtil.asUniversalTypeCompilationUnit(contextCompilationUnit).getCompilationResult();
      if (!(baseType instanceof SubstantialTypeExpression)) {
        throw new IteraQl2Exception(IteraQl2Exception.EXTENSION_WRONG_CONTEXT, "The expand extension can only be used for substantial types.");
      }

      return new UniversalTypeCompilationUnit(new ExpandedSubstantialType(CompileUtil.asRelationshipEndCompilationUnit(expandingRelEnd)
          .getCompilationResult()));
    }
    else if (CompileUtil.isRelationshipEndCompilationUnit(contextCompilationUnit)) {
      RelationshipEndExpression joinEnd0 = CompileUtil.asRelationshipEndCompilationUnit(contextCompilationUnit).getCompilationResult();
      if (!(joinEnd0.getType() instanceof SubstantialTypeExpression)) {
        throw new IteraQl2Exception(IteraQl2Exception.EXTENSION_WRONG_CONTEXT, "The expand extension can only be used for substantial types.");
      }
      ToExpandedRelationshipEnd joinEnd1 = new ToExpandedRelationshipEnd(CompileUtil.asRelationshipEndCompilationUnit(expandingRelEnd)
          .getCompilationResult());

      return new RelationshipEndCompilationUnit(new JoinedRelationshipEnd(joinEnd0, joinEnd1));
    }
    throw new IteraQl2Exception(IteraQl2Exception.EXTENSION_WRONG_CONTEXT, "The expand extension can not be used in the current context: "
        + context.getContextCompilationUnit());
  }
}

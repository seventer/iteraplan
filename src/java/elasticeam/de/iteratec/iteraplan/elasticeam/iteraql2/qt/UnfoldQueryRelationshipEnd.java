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
package de.iteratec.iteraplan.elasticeam.iteraql2.qt;

import java.util.List;

import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompilationUnit;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompileUtil;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.QueryCompilationContext;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.RelationshipEndCompilationUnit;
import de.iteratec.iteraplan.elasticeam.iteraql2.qt.extension.Extension;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.operator.fold.UnfoldRelationshipEnd;
import de.iteratec.iteraplan.elasticeam.operator.join.JoinedRelationshipEnd;


/**
 * A relationship end query tree element corresponds to the unfold operator.
 */
public class UnfoldQueryRelationshipEnd extends RelationshipEnd {

  private RelationshipEnd subRelationshipEnd;

  public UnfoldQueryRelationshipEnd(RelationshipEnd subRelationshipEnd, List<Extension> extensions, RelationshipEnd next) {
    super(next, extensions);
    this.subRelationshipEnd = subRelationshipEnd;
  }

  public String toString() {
    String result = "unfold(" + subRelationshipEnd + ")";
    if (next != null) {
      result = result + next.toString();
    }
    return result;
  }

  /**{@inheritDoc}**/
  public CompilationUnit compile(QueryCompilationContext context) {
    CompilationUnit subUnfoldCompilation = subRelationshipEnd.compile(context);
    RelationshipEndExpression subUnfoldPath = CompileUtil.asRelationshipEndCompilationUnit(subUnfoldCompilation).getCompilationResult();
    //FIXME enable again, when compatibility relationship between universal types is defined.
    //    if (!subUnfoldPath.getOrigin().equals(subUnfoldPath.getType())) {
    //      throw new IteraQl2Exception(IteraQl2Exception.FOLD_PATH_NOT_CYCLIC, "An unfold operator can only be applied to a cyclic path.");
    //    }

    RelationshipEndExpression unfoldRelEnd = new UnfoldRelationshipEnd(subUnfoldPath);
    CompilationUnit contextCompilationUnit = new RelationshipEndCompilationUnit(unfoldRelEnd);

    if (extensions != null && !extensions.isEmpty()) {
      for (Extension extension : extensions) {
        context.setContextCompilationUnit(contextCompilationUnit);
        contextCompilationUnit = extension.compile(context);
        unfoldRelEnd = CompileUtil.asRelationshipEndCompilationUnit(contextCompilationUnit).getCompilationResult();
      }
    }

    if (next == null) {
      return new RelationshipEndCompilationUnit(unfoldRelEnd);
    }

    context.setContextCompilationUnit(new RelationshipEndCompilationUnit(unfoldRelEnd));
    CompilationUnit postUnfoldCompilation = next.compile(context);

    RelationshipEndExpression finalRelEnd = new JoinedRelationshipEnd(unfoldRelEnd, CompileUtil.asRelationshipEndCompilationUnit(
        postUnfoldCompilation).getCompilationResult());

    return new RelationshipEndCompilationUnit(finalRelEnd);
  }
}

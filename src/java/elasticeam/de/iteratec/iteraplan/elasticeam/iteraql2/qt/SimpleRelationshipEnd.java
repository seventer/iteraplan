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

import de.iteratec.iteraplan.elasticeam.iteraql2.IteraQl2Exception;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompilationUnit;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompileUtil;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.QueryCompilationContext;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.RelationshipEndCompilationUnit;
import de.iteratec.iteraplan.elasticeam.iteraql2.qt.extension.Extension;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.operator.join.JoinedRelationshipEnd;


/**
 * A relationship end query tree element which represents a relationship end expression,
 * possibly with extensions and possibly followed by another relationship end.
 */
public class SimpleRelationshipEnd extends RelationshipEnd {

  private String identifier;

  public SimpleRelationshipEnd(String identifier, List<Extension> extensions, RelationshipEnd next) {
    super(next, extensions);
    this.identifier = identifier;
  }

  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append(identifier);
    for (Extension ext : extensions) {
      result.append(ext.toString());
    }

    if (next != null) {
      result.append(next.toString());
    }
    return result.toString();
  }

  /**{@inheritDoc}**/
  public CompilationUnit compile(QueryCompilationContext context) {
    //locate base relationship end
    UniversalTypeExpression contextType = CompileUtil.getContextType(context);
    RelationshipEndExpression relEnd = CompileUtil.findRelationshipEnd(identifier, contextType);

    if (relEnd == null) {
      throw new IteraQl2Exception(IteraQl2Exception.NO_SUCH_ELEMENT, "No such relationship end: " + identifier + " for universal type " + contextType);
    }
    CompilationUnit contextCompilationUnit = new RelationshipEndCompilationUnit(relEnd);

    // manage extensions
    if (extensions != null && !extensions.isEmpty()) {
      for (Extension extension : extensions) {
        context.setContextCompilationUnit(contextCompilationUnit);
        contextCompilationUnit = extension.compile(context);
        relEnd = CompileUtil.asRelationshipEndCompilationUnit(contextCompilationUnit).getCompilationResult();
      }
    }

    //manage next relationship end
    if (next != null) {
      context.setContextCompilationUnit(contextCompilationUnit);
      contextCompilationUnit = next.compile(context);
      RelationshipEndExpression nextRelationshipEnd = CompileUtil.asRelationshipEndCompilationUnit(contextCompilationUnit).getCompilationResult();
      RelationshipEndExpression finalRelEnd = new JoinedRelationshipEnd(relEnd, nextRelationshipEnd);
      contextCompilationUnit = new RelationshipEndCompilationUnit(finalRelEnd);
    }

    return contextCompilationUnit;
  }

}

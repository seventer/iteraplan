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
package de.iteratec.iteraplan.elasticeam.iteraql2.compile;

import de.iteratec.iteraplan.elasticeam.iteraql2.IteraQl2Exception;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;


/**
 * A utility with common functions used for the compilation of queries.
 */
public final class CompileUtil {

  private CompileUtil() {
    //Nothing here
  }

  public static boolean isUniversalTypeCompilationUnit(CompilationUnit unit) {
    return (unit instanceof UniversalTypeCompilationUnit);
  }

  public static boolean isRelationshipEndCompilationUnit(CompilationUnit unit) {
    return (unit instanceof RelationshipEndCompilationUnit);
  }

  public static boolean isPropertyCompilationUnit(CompilationUnit unit) {
    return (unit instanceof PropertyCompilationUnit);
  }

  public static boolean isValueCompilationUnit(CompilationUnit unit) {
    return (unit instanceof ValueCompilationUnit);
  }

  public static boolean isPredicateCompilationUnit(CompilationUnit unit) {
    return (unit instanceof PredicateCompilationUnit);
  }

  public static UniversalTypeCompilationUnit asUniversalTypeCompilationUnit(CompilationUnit unit) {
    if (!isUniversalTypeCompilationUnit(unit)) {
      throw new IteraQl2Exception(IteraQl2Exception.INCOMPATIBLE_COMPILATION_UNITS, "The compilation unit " + unit
          + " can not be transformed to a universal type compilation unit.");
    }
    return (UniversalTypeCompilationUnit) unit;
  }

  public static RelationshipEndCompilationUnit asRelationshipEndCompilationUnit(CompilationUnit unit) {
    if (!isRelationshipEndCompilationUnit(unit)) {
      throw new IteraQl2Exception(IteraQl2Exception.INCOMPATIBLE_COMPILATION_UNITS, "The compilation unit " + unit
          + " can not be transformed to a relationship end compilation unit.");
    }
    return (RelationshipEndCompilationUnit) unit;
  }

  public static PropertyCompilationUnit asPropertyCompilationUnit(CompilationUnit unit) {
    if (!isPropertyCompilationUnit(unit)) {
      throw new IteraQl2Exception(IteraQl2Exception.INCOMPATIBLE_COMPILATION_UNITS, "The compilation unit " + unit
          + " can not be transformed to a property compilation unit.");
    }
    return (PropertyCompilationUnit) unit;
  }

  public static ValueCompilationUnit asValueCompilationUnit(CompilationUnit unit) {
    if (!isValueCompilationUnit(unit)) {
      throw new IteraQl2Exception(IteraQl2Exception.INCOMPATIBLE_COMPILATION_UNITS, "The compilation unit " + unit
          + " can not be transformed to a value compilation unit.");
    }
    return (ValueCompilationUnit) unit;
  }

  public static PredicateCompilationUnit asPredicateCompilationUnit(CompilationUnit unit) {
    if (!isPredicateCompilationUnit(unit)) {
      throw new IteraQl2Exception(IteraQl2Exception.INCOMPATIBLE_COMPILATION_UNITS, "The compilation unit " + unit
          + " can not be transformed to a predicate compilation unit.");
    }
    return (PredicateCompilationUnit) unit;
  }

  public static UniversalTypeExpression findUniversalType(String identifier, Metamodel inMetamodel) {
    UniversalTypeExpression result = null;
    //findByAbbreviation still to come
    result = inMetamodel.findUniversalTypeByName(identifier);
    if (result != null) {
      return result;
    }
    return inMetamodel.findUniversalTypeByPersistentName(identifier);
  }

  public static RelationshipEndExpression findRelationshipEnd(String identifier, UniversalTypeExpression contextType) {
    RelationshipEndExpression result = null;
    //findByAbbreviation still to come
    result = contextType.findRelationshipEndByName(identifier);
    if (result != null) {
      return result;
    }
    return contextType.findRelationshipEndByPersistentName(identifier);
  }

  public static PropertyExpression<?> findProperty(String identifier, UniversalTypeExpression contextType) {
    PropertyExpression<?> result = null;
    result = contextType.findPropertyByName(identifier);
    if (result != null) {
      return result;
    }
    return contextType.findPropertyByPersistentName(identifier);
  }

  public static UniversalTypeExpression getContextType(QueryCompilationContext context) {
    CompilationUnit contextCompilationUnit = context.getContextCompilationUnit();
    if (contextCompilationUnit == null) {
      throw new IteraQl2Exception(IteraQl2Exception.EXTENSION_MISSING_CONTEXT, "No context compilation unit was provided for the extension.");
    }
    if (CompileUtil.isUniversalTypeCompilationUnit(contextCompilationUnit)) {
      return CompileUtil.asUniversalTypeCompilationUnit(contextCompilationUnit).getCompilationResult();
    }
    if (CompileUtil.isRelationshipEndCompilationUnit(contextCompilationUnit)) {
      return CompileUtil.asRelationshipEndCompilationUnit(contextCompilationUnit).getCompilationResult().getType();
    }
    throw new IteraQl2Exception(IteraQl2Exception.RELATIONSHIP_END_INCOMPATIBLE_CONTEXT,
        "Compilation is only possible in the context of a universal type or a relationthip end. Current context is: " + contextCompilationUnit);
  }
}

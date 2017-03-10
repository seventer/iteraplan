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
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.PropertyCompilationUnit;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.QueryCompilationContext;
import de.iteratec.iteraplan.elasticeam.iteraql2.qt.Feature;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.operator.count.CountProperty;


/**
 * A query tree element which represents a count operator property.
 */
public class CountDerivedLeafProperty extends DerivedLeafProperty {

  private Feature feature;

  public CountDerivedLeafProperty(Feature feature) {
    this.feature = feature;
  }

  public String toString() {
    return "count(" + feature.toString() + ")";
  }

  /**{@inheritDoc}**/
  public CompilationUnit compile(QueryCompilationContext context) {
    CompilationUnit subCompilation = feature.compile(context);
    FeatureExpression<?> metamodelFeature = null;

    if (CompileUtil.isPropertyCompilationUnit(subCompilation)) {
      metamodelFeature = CompileUtil.asPropertyCompilationUnit(subCompilation).getCompilationResult();
    }
    else if (CompileUtil.isRelationshipEndCompilationUnit(subCompilation)) {
      metamodelFeature = CompileUtil.asRelationshipEndCompilationUnit(subCompilation).getCompilationResult();
    }
    else {
      throw new IteraQl2Exception(IteraQl2Exception.INCOMPATIBLE_COMPILATION_UNITS,
          "A count extension can only be applied to a proeprty or to a relationship end compilation, but not to compilation unit: " + subCompilation);
    }

    return new PropertyCompilationUnit(new CountProperty(metamodelFeature));
  }

}

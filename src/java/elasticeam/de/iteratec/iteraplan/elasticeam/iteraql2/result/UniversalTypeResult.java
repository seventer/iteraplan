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
package de.iteratec.iteraplan.elasticeam.iteraql2.result;

import java.util.Collection;

import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompiledQuery;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


/**
 * Contains the result of a query which evaluates to a universal type.
 */
public class UniversalTypeResult implements QueryResult {

  private CompiledQuery                        sourceQuery;
  private UniversalTypeExpression              universalType;
  private Collection<UniversalModelExpression> results;

  public UniversalTypeResult(CompiledQuery sourceQuery, UniversalTypeExpression universalType, Collection<UniversalModelExpression> results) {
    this.sourceQuery = sourceQuery;
    this.universalType = universalType;
    this.results = results;
  }

  public UniversalTypeExpression getUniversalType() {
    return universalType;
  }

  /**
   * @return
   *    The collection of universal model expressions, which are instances of the universal type of this query.
   */
  public Collection<UniversalModelExpression> getResults() {
    return results;
  }

  /**{@inheritDoc}**/
  public QueryResultType getType() {
    return QueryResultType.UNIVERSAL_TYPE;
  }

  /**{@inheritDoc}**/
  public CompiledQuery getSourceQuery() {
    return sourceQuery;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("UniversalTypeResult \n");
    builder.append("universalType: ");
    builder.append(universalType);
    builder.append("\n\n");
    builder.append("Instances: size=");
    builder.append(results.size());
    builder.append("\n");
    for (UniversalModelExpression expr : results) {
      builder.append(expr);
      builder.append("\n");
    }

    return builder.toString();
  }
}

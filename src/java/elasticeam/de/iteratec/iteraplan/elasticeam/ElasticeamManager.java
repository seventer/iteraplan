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
package de.iteratec.iteraplan.elasticeam;

import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompiledQuery;
import de.iteratec.iteraplan.elasticeam.iteraql2.qt.Query;
import de.iteratec.iteraplan.elasticeam.iteraql2.result.QueryResult;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.model.Model;


public interface ElasticeamManager {

  /**
   * Triggers an initalization or a reinitialization of the iteraQl metamodel and model data.
   */
  void reInitialize();

  /**
   * Retrieves the metamodel which is currently in use.
   * @return
   *    The current metamodel.
   */
  Metamodel getMetamodel();

  /**
   * Retrieves the model which is currently in use.
   * @return
   *    The current model.
   */
  Model getModel();

  /**
   * Parses, compiles and executes the given query.
   * 
   * @param queryString
   *    The query to execute.
   * @return
   *    The result of the query.
   */
  QueryResult executeQuery(String queryString);

  /**
   * Compiles and executes the given query.
   * 
   * @param query
   *    The query to execute.
   * @return
   *    The result of the query.
   */
  QueryResult executeQuery(Query query);

  /**
   * Executes the given query.
   * 
   * @param compiledQuery
   *    The query to execute.
   * @return
   *    The result of the query.
   */
  QueryResult executeQuery(CompiledQuery compiledQuery);

  /**
   * Parses the query string to a (fully initialized) {@link Query} and compiles this
   * object to a {@link CompiledQuery}.
   * @param queryString
   *    The query to parse and compile.
   * @return
   *    The parsed and initialized query object.
   */
  CompiledQuery compile(String queryString);

  /**
   * Compiles this query object to a {@link CompiledQuery}.
   * 
   * @param query
   *    The query to compile.
   * @return
   *    The parsed and initialized query object.
   */
  CompiledQuery compile(Query query);

  /**
   * Retrieves whether the query framework is currently initialized. This is the case when both
   * metamodel and model have been loaded.
   * @return
   *    The current state of the query framework.
   */
  boolean isInitialized();

}

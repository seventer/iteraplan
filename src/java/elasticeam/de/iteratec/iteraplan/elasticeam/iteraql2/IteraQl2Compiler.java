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
package de.iteratec.iteraplan.elasticeam.iteraql2;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;

import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompiledQuery;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.QueryCompilationContext;
import de.iteratec.iteraplan.elasticeam.iteraql2.qt.Query;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;


/**
 * Compiles a query into some canonic or derived metamodel element.
 */
public class IteraQl2Compiler {

  private Metamodel metamodel;

  /**
   * Creates a new query compiler.
   * 
   * @param metamodel
   *    The {@link Metamodel} against which the compilation of queries is done.
   */
  public IteraQl2Compiler(Metamodel metamodel) {
    this.metamodel = metamodel;
  }

  /**
   * Compiles a query from string against the metamodel of this compiler.
   * @param queryString
   *    The query string to compile.
   * @return
   *    The compiled query.
   */
  public CompiledQuery compile(String queryString) {
    return compile(metamodel, queryString);
  }

  /**
   * Compiles a query object against the metamodel of this compiler.
   * @param queryObject
   *    The query object to compile.
   * @return
   *    The compiled query.
   */
  public CompiledQuery compile(Query queryObject) {
    return compile(metamodel, queryObject);
  }

  /**
   * Compiles the given query string against the provided metamodel.
   * 
   * @param metamodel
   *    The metamodel against which this query is compiled.
   * @param queryString
   *    The query to compile.
   * @return
   *    The compiled query.
   */
  public static CompiledQuery compile(Metamodel metamodel, String queryString) {
    Query queryObject = null;
    CharStream charStream = new ANTLRStringStream(queryString);
    IteraQl2Lexer lexer = new IteraQl2Lexer(charStream);
    TokenStream tokenStram = new CommonTokenStream(lexer);
    IteraQl2Parser parser = new IteraQl2Parser(tokenStram);

    try {
      queryObject = parser.query();
    } catch (RecognitionException e) {
      throw new IteraQl2Exception(IteraQl2Exception.PARSER_INVALID_QUERY_STRING, "Syntax error. Cause: " + e.getMessage(), e);
    }

    return compile(metamodel, queryObject);
  }

  /**
   * Compiles the given query object against the provided metamodel.
   * 
   * @param metamodel
   *    The metamodel against which this query is compiled.
   * @param queryObject
   *    The query to compile.
   * @return
   *    The compiled query.
   */
  public static CompiledQuery compile(Metamodel metamodel, Query queryObject) {
    return queryObject.compile(new QueryCompilationContext(metamodel));
  }

}

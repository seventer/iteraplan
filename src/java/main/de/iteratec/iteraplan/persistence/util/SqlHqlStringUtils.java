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
package de.iteratec.iteraplan.persistence.util;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.engine.SessionFactoryImplementor;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.interfaces.IdEntity;


/**
 *
 */
public final class SqlHqlStringUtils {

  /** Logger. */
  private static final Logger LOGGER       = Logger.getIteraplanLogger(SqlHqlStringUtils.class);

  private static final String WILD_PERCENT = "%";

  /**
   * Utility class, do not instantiate.
   */
  private SqlHqlStringUtils() {
  }

  /**
   * Escapes the characters '%' and '_' in a string and replaces the wildcard characters '*' and '?'
   * in the filter String with the sql-specific wildcards. The returned String always contains the %
   * wildcard at its first and last position. Use this method for GUI filter fields.
   * 
   * @param filter The filter String to process
   * @return A SQL conform filter String.
   */
  public static String processGuiFilterForSql(String filter) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("processGuiFilterForSql called with filter string '" + filter + "'");
    }
    String res;
    if (filter == null) {
      res = WILD_PERCENT;
    }
    else {
      res = WILD_PERCENT + processFilterForSql(filter) + WILD_PERCENT;
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("processGuiFilterForSql resulted in filter string '" + res + "'");
    }
    return res;
  }

  /**
   * Escapes the characters '%' and '_' in a string and replaces the wildcard characters '*' and '?'
   * in the filter String with the sql-specific wildcards.
   * 
   * @param filter The filter String to process
   * @return A SQL conform filter String.
   */

  public static String processFilterForSql(String filter) {
    // using | as escape sequence:
    // replace | with ||
    String res = filter;
    Character escapeChar = Constants.SQL_LIKE_OPERATOR_ESCAPE_CHAR;
    res = res.replaceAll("\\" + escapeChar, "\\" + escapeChar + "\\" + escapeChar);
    // replace % with |%
    res = res.replaceAll("%", escapeChar + "%");
    // replace _ with |_
    res = res.replaceAll("_", escapeChar + "_");
    // replace * with %
    res = res.replaceAll("\\*", "%");
    // replace ? with _
    res = res.replaceAll("\\?", "_");
    return res;
  }

  public static String escapeSqlLikeOperatorWildcards(String filter, Session session) {
    // using | as escape sequence:
    // replace | with ||
    String res = filter;
    Character escapeChar = Constants.SQL_LIKE_OPERATOR_ESCAPE_CHAR;
    res = res.replaceAll("\\" + escapeChar, "\\" + escapeChar + "\\" + escapeChar);
    // replace % with |%
    res = res.replaceAll("%", escapeChar + "%");
    // replace _ with |_
    res = res.replaceAll("_", escapeChar + "_");

    if (session != null && session.getSessionFactory() instanceof SessionFactoryImplementor) {
      res = escapeSqlLikeSquareBrackets(res, ((SessionFactoryImplementor) session.getSessionFactory()).getDialect());
    }
    return res;
  }

  /**
   * unlike other DBMS, SQL Server's LIKE operator treats '[', ']' as special characters.
   * 
   * @param session current hibernate session (used to check if we query an mssql server)
   * @param filter the filter string to escape [ and ] within
   * @return the filter with [ and ] escaped
   */
  public static String escapeSqlLikeSquareBrackets(String filter, Session session) {
    String res = filter;

    if (session != null && session.getSessionFactory() instanceof SessionFactoryImplementor) {
      res = escapeSqlLikeSquareBrackets(res, ((SessionFactoryImplementor) session.getSessionFactory()).getDialect());
    }

    return res;
  }

  public static String escapeSqlLikeSquareBrackets(String filter, Dialect dialect) {
    String result = filter;
    // unlike other DBMS, SQL Server's LIKE operator treats '[', ']' as special characters.
    if (isMsSqlServer(dialect)) {
      result = result.replaceAll("\\[", Constants.SQL_LIKE_OPERATOR_ESCAPE_CHAR + "[");
      result = result.replaceAll("\\]", Constants.SQL_LIKE_OPERATOR_ESCAPE_CHAR + "]");
    }
    return result;
  }

  private static boolean isMsSqlServer(Dialect dialect) {
    return dialect != null && (dialect instanceof SQLServerDialect);
  }

  /**
   * Produces a HQL WHERE condition fragment that checks if a property value is contained in a list
   * of elements.
   * <p>
   * The element list is rendered to a concatenated {@code String} representation of the elements in
   * the specified collection. If the contained elements implement the {@link IdEntity} interface,
   * their getId() method is invoked to obtain the string, otherwise by calling the
   * {@code toString()} method on the contained elements. Each {@code String}, except the last one,
   * is followed by the given separator {@code String}. For example the collection of objects of
   * type {@code Integer} [1 2 3 4 5] produces the following result:
   * <code>prop.id in (1,2,3,4,5)</code>.
   * <p>
   * If the element list is longer than 950 items, it is split up into two (or more) lists. This is
   * because Oracle refuses to check against lists with more than 1000 elements. In such a case, the
   * result will be
   * <tt>prop.id in (<b>less than 1000 elements</b>) or prop.id in (<b>remaining elements</b>)</tt>.
   * Thus the list-items are split up into several in-clauses. This will make the query a bit
   * slower, the only other alternative would be to use a subquery, which seems not to be possible
   * here.
   * 
   * @param propertyName
   *          The property name that should be used for the is-contained-in check.
   * @param coll
   *          The collection of elements to check against.
   * @param separator
   *          The separator.
   * @return See method description. If either the collection or the separator is {@code null} or
   *         the propertyName is empty, an empty {@code String} is returned.
   */
  public static String buildHqlConditionWhereElementsInList(String propertyName, Collection<?> coll, String separator) {
    if (StringUtils.isEmpty(propertyName) || (coll == null) || (separator == null)) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    sb.append(' ');
    sb.append(propertyName);
    sb.append(" in (");
    int elementCount = 0;

    for (Iterator<?> it = coll.iterator(); it.hasNext();) {
      Object o = it.next();

      if (o instanceof IdEntity) {
        IdEntity idEntity = (IdEntity) o;
        sb.append(idEntity.getId());
      }
      else {
        sb.append(o.toString());
      }
      if (it.hasNext()) {
        if (elementCount++ > 950) {
          sb.append(") or ");
          sb.append(propertyName);
          sb.append(" in (");
          elementCount = 0;
        }
        else {
          sb.append(separator);
        }
      }
    }
    sb.append(") ");

    LOGGER.debug("Produced string: {0}", sb.toString());

    return sb.toString();
  }

}

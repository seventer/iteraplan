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
package de.iteratec.iteraplan.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.Logger;


public final class SqlScriptExecutor {

  private static final Logger LOGGER = Logger.getIteraplanLogger(SqlScriptExecutor.class);

  private SqlScriptExecutor() {
    // prevent instantiation
  }

  public static List<String> readSqlScript(InputStream in) {
    InputStream setupSqlInputStream = in;
    String sqlString = null;
    try {
      sqlString = IOUtils.toString(setupSqlInputStream);
    } catch (IOException fnfe) {
      LOGGER.error("unable to read data.");
    } finally {
      if (setupSqlInputStream != null) {
        try {
          setupSqlInputStream.close();
        } catch (IOException e) {
          LOGGER.error("Cannot close stream for setupSqlFile", e);
        }
      }
    }
    List<String> sqlStatements = Lists.newArrayList();

    StringTokenizer tokenizer = new StringTokenizer(sqlString, ";");
    while (tokenizer.hasMoreTokens()) {
      String nextToken = tokenizer.nextToken();
      sqlStatements.add(nextToken);
    }

    return sqlStatements;
  }

  public static void executeSqlStatements(final List<String> sqlStrings, HibernateTemplate hibernateTemplate) {

    final Work work = new Work() {

      public void execute(Connection connection) throws SQLException {
        executeSqlStatements(sqlStrings, connection);
      }
    };

    HibernateCallback<Object> callback = new HibernateCallback<Object>() {

      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        session.doWork(work);
        return null;
      }
    };

    hibernateTemplate.execute(callback);
  }

  public static void executeSqlStatements(final List<String> sqlStrings, Connection connection) throws SQLException {
    for (String statement : sqlStrings) {
      PreparedStatement stmt = null;
      try {
        stmt = connection.prepareStatement(statement);
        stmt.executeUpdate();
      } catch (SQLException se) {
        // ignore alter table errors because these might be ok, if tables do not exist
        if (!statement.trim().startsWith("alter table")) {
          LOGGER.error("database error when running db statement  '" + statement + "'.");
          throw se;
        }
      } finally {
        if (stmt != null) {
          stmt.close();
        }
      }
    }

    connection.commit();
  }

  public static void executeSqlScriptFile(Connection connection, String fileName) {
    try {
      FileInputStream inputStream = new FileInputStream(fileName);
      executeSqlStatements(readSqlScript(inputStream), connection);
    } catch (SQLException e) {
      LOGGER.error("Error while executing the script file '" + fileName + "'.", e);
    } catch (FileNotFoundException e) {
      LOGGER.error("Could not read file '" + fileName + "'.", e);
    }
  }

  public static void executeSqlScriptFile(HibernateTemplate hibernateTemplate, String fileName) {
    try {
      FileInputStream inputStream = new FileInputStream(fileName);
      executeSqlStatements(readSqlScript(inputStream), hibernateTemplate);
    } catch (FileNotFoundException e) {
      LOGGER.error("Could not read file '" + fileName + "'.", e);
    }
  }

}

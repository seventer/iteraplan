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
package de.iteratec.iteraplan.presentation.problemreports;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;

import de.iteratec.iteraplan.common.DefaultSpringApplicationContext;


/**
 * {@link ProblemReportPart} for information about the database (schema) and the JDBC Connection.
 */
final class DatabaseProblemReportPart extends AbstractProblemReportPart {

  private static final int TIMEOUT_IN_SECONDS = 4;

  private DatabaseProblemReportPart(String filename) {
    super(filename);
  }

  static ProblemReportPart generateDatabaseReport(String filename, HttpServletRequest request) {
    DatabaseProblemReportPart reportPart = new DatabaseProblemReportPart(filename);
    PrintWriter dbWriter = reportPart.getWriter();

    ApplicationContext context = DefaultSpringApplicationContext.getSpringApplicationContext();
    Object sessionFactoryObject = context.getBean("sessionFactory");

    if (sessionFactoryObject instanceof SessionFactory) {
      SessionFactory sessionFactory = (SessionFactory) sessionFactoryObject;
      Session currentSession = sessionFactory.getCurrentSession();
      Map<String, ClassMetadata> allClassMetadata = sessionFactory.getAllClassMetadata();
      final Set<String> tableNames = Sets.newHashSet();
      for (ClassMetadata cm : allClassMetadata.values()) {
        if (cm instanceof AbstractEntityPersister) {
          AbstractEntityPersister aep = (AbstractEntityPersister) cm;
          tableNames.add(aep.getTableName());
        }
      }

      ByteArrayOutputStream dbInfoBuffer = new ByteArrayOutputStream();
      final PrintWriter dbInfoWriter = new PrintWriter(dbInfoBuffer);
      Work work = new Work() {

        @Override
        public void execute(Connection connection) throws SQLException {

          try {
            DatabaseMetaData metaData = connection.getMetaData();
            dbInfoWriter.println("Database Name: " + metaData.getDatabaseProductName() + "  " + metaData.getDatabaseMajorVersion() + "."
                + metaData.getDatabaseMinorVersion());
            dbInfoWriter.println("Database Product Version: " + metaData.getDatabaseProductVersion());
            dbInfoWriter.println("JDBC URL: " + metaData.getURL());
            dbInfoWriter.println("JDBC API: " + metaData.getJDBCMajorVersion() + "." + metaData.getJDBCMinorVersion());
            dbInfoWriter.println("JDBC-Driver Name: " + metaData.getDriverName() + "  " + metaData.getDriverMajorVersion() + "."
                + metaData.getDriverMinorVersion());
            dbInfoWriter.println("JDBC-Driver Version: " + metaData.getDriverVersion());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      };

      try {
        TimeLimiter timeLimiter = new SimpleTimeLimiter();
        Session sessionProxy = timeLimiter.newProxy(currentSession, Session.class, TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        sessionProxy.doWork(work);
      } catch (UncheckedTimeoutException e) {
        dbInfoWriter.println("Couldn't gather database information from conncetion within " + TIMEOUT_IN_SECONDS + " seconds.");
      } catch (Exception e) {
        dbInfoWriter.println("Couldn't gather database information from connection.");
      }

      dbInfoWriter.close();
      dbWriter.print(dbInfoBuffer);
    }

    dbWriter.close();
    return reportPart;
  }

  /**{@inheritDoc}**/
  @Override
  public String getReportPartIdentifier() {
    return "database";
  }

}

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
package de.iteratec.iteraplan.xmi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.db.DataTypeFactoryHelper;
import de.iteratec.iteraplan.db.SqlScriptExecutor;
import de.iteratec.iteraplan.xmi.XmiImport.HistoryInitialization;


public final class InitialDataDumper {

  private static final Logger LOGGER                = Logger.getIteraplanLogger(InitialDataDumper.class);

  public static final String  INITIAL_DATA_XML_FILE = "src/java/test/initialData.xml";

  private InitialDataDumper() {
    // nothing happening
  }

  /**
   * @param args (schema script, jdbc driver name, jdbc url, login, password)
   * @throws IOException if the {@code iteraplanData.xmi} file will be not found
   * @throws ClassNotFoundException if the jdbc driver is not found.
   * @throws SQLException if the connection to the database could not be established
   * @throws DataSetException if writing the dump was impossible
   */
  public static void main(String[] args) throws IOException, SQLException {
    if (args.length < 4) {
      LOGGER.error("Invalid method call! Database schema script, database driver, database url, login and optional password are needed!");
    }

    String schemaExportFile = args[0];
    String jdbcDriver = args[1];
    String jdbcUrl = args[2];
    String login = args[3];
    String password = (args.length < 5) ? "" : args[4];

    Connection jdbcConnection = getJdbcConnection(jdbcDriver, jdbcUrl, login, password);

    createSchema(schemaExportFile, jdbcConnection);

    importInitialData();

    createXmlDump(jdbcConnection);
  }

  private static void createXmlDump(Connection jdbcConnection) throws SQLException, IOException {

    try {
      IDataSet fullDataSet = extractDataSet(jdbcConnection);
      FlatXmlDataSet.write(fullDataSet, new FileOutputStream(INITIAL_DATA_XML_FILE));
    } catch (DataSetException e) {
      LOGGER.error("Dataset corrupt, could not be written.", e);
      throw new IllegalStateException(e);
    } catch (DatabaseUnitException e) {
      LOGGER.error(e);
      throw new IllegalStateException(e);
    }
  }

  private static IDataSet extractDataSet(Connection jdbcConnection) throws DatabaseUnitException, SQLException {
    IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
    IDataTypeFactory dataTypeFactory = DataTypeFactoryHelper.determineDataTypeFactory(jdbcConnection);

    DatabaseConfig config = connection.getConfig();
    config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);

    ITableFilter filter = new DatabaseSequenceFilter(connection);

    return new FilteredDataSet(filter, connection.createDataSet());
  }

  private static Connection getJdbcConnection(String jdbcDriver, String jdbcUrl, String login, String password) throws SQLException {
    // to load the driver class
    try {
      Class.forName(jdbcDriver);
    } catch (ClassNotFoundException e) {
      LOGGER.error("Driver '" + jdbcDriver + "' not found.", e);
    }

    return DriverManager.getConnection(jdbcUrl, login, password);
  }

  private static void importInitialData() throws IOException {
    XmiImport xmiImport = new XmiImport();
    xmiImport.importInitialData(false, HistoryInitialization.FORCE_INITIALIZE);
  }

  private static void createSchema(String schemaExportFile, Connection jdbcConnection) {
    SqlScriptExecutor.executeSqlScriptFile(jdbcConnection, schemaExportFile);
  }

}

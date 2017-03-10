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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.OracleDataTypeFactory;

import de.iteratec.iteraplan.common.Logger;


public final class DataTypeFactoryHelper {

  private static final Logger LOGGER = Logger.getIteraplanLogger(DataTypeFactoryHelper.class);

  private DataTypeFactoryHelper() {
    // nothing to do
  }

  /**
   * Returns a {@link IDataTypeFactory} which fits the database the given connection uses.
   * @param connection
   * @return the fitting {@link IDataTypeFactory} if existing, {@link DefaultDataTypeFactory} otherwise.
   */
  public static IDataTypeFactory determineDataTypeFactory(Connection connection) {
    String productName = "";
    try {
      DatabaseMetaData metaData = connection.getMetaData();
      productName = metaData.getDatabaseProductName();
    } catch (SQLException e) {
      LOGGER.error("Error reading the connection's meta data", e);
      throw new IllegalStateException(e);
    }
    DefaultDataTypeFactory dataTypeFactory = new HsqldbDataTypeFactory();
    if (isAmongValidProducts(productName, dataTypeFactory)) {
      return dataTypeFactory;
    }
    dataTypeFactory = new MySqlDataTypeFactory();
    if (isAmongValidProducts(productName, dataTypeFactory)) {
      return dataTypeFactory;
    }
    dataTypeFactory = new OracleDataTypeFactory();
    if (isAmongValidProducts(productName, dataTypeFactory)) {
      return dataTypeFactory;
    }
    dataTypeFactory = new MsSqlDataTypeFactory();
    if (isAmongValidProducts(productName, dataTypeFactory)) {
      return dataTypeFactory;
    }
    else {
      LOGGER.debug("Using \"{0}\" for database \"{1}\".", DefaultDataTypeFactory.class.getName(), productName);
      return new DefaultDataTypeFactory();
    }
  }

  private static boolean isAmongValidProducts(String productName, DefaultDataTypeFactory dataTypeFactory) {
    String lowerCaseProductName = productName.toLowerCase();

    for (Object dbProduct : dataTypeFactory.getValidDbProducts()) {
      if (lowerCaseProductName.contains(dbProduct.toString().toLowerCase())) {
        LOGGER.debug("Using \"{0}\" for database \"{1}\".", dataTypeFactory.getClass().getName(), productName);
        return true;
      }
    }
    return false;
  }
}

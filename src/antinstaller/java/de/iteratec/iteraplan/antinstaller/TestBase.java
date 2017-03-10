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
package de.iteratec.iteraplan.antinstaller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tp23.antinstaller.InstallerContext;
import org.tp23.antinstaller.input.Validator;

/**
 * Validator base class with shared functions.
 */

abstract class TestBase implements Validator
{ 
  
  private static final Logger LOGGER = Logger.getLogger(TestBase.class);
  
  protected abstract String getDBName();
  
  public String getErrorMessage( final Throwable a_oThrowable, final Locale a_oLocale )
  {
    if( a_oThrowable instanceof ValidationException)
    {
      return a_oThrowable.getLocalizedMessage();
    }
    LOGGER.debug(a_oThrowable + " not instance of ValidationException; unexpected error: '" + a_oThrowable.getLocalizedMessage() + "'");
    return "unexpected error: '" + a_oThrowable.getLocalizedMessage() + "'";
  }
  
  protected void doValidate( final String a_strPropertyPrefix, final InstallerContext a_oInstallerContext )
    throws Exception
  {
    if (System.getProperties().containsKey("skip.connection.validation")) {
      LOGGER.info("Skip database connection validation.");
      return;
    }
    
    final Map oMapProperties = TestUtil.getProperties( a_oInstallerContext );
    final String strDBVendor = TestUtil.getProperty( oMapProperties, "database.rdbmsName", "Database vendor" ); // Add prefix here when allowing different DB-Dialects
    final String strDBAddress = TestUtil.getProperty( oMapProperties, 
        a_strPropertyPrefix + "database.serverAddress", "Address of the DB server" );
    if( "mytest".equals( strDBAddress ) )
    { // allow skipping test for debugging
      return;
    }
    final String strDBPort = TestUtil.getProperty( oMapProperties, 
        a_strPropertyPrefix + "database.serverPort", "Port of the DB server" );
    final String strDBName = TestUtil.getProperty( oMapProperties, 
        a_strPropertyPrefix + "database.name", "Name of the DB" );
    final String strDBUser = TestUtil.getProperty( oMapProperties, 
        a_strPropertyPrefix + "database.login", "User name for the db" );
    final String strDBPassword = TestUtil.getProperty( oMapProperties, 
        a_strPropertyPrefix + "database.password", "Password for the db" );
    try
    {
      final Connection oConnection = getConnectionPerDriverManager( strDBVendor, strDBAddress,
          strDBPort, strDBName, strDBUser, strDBPassword );
      oConnection.close();
    }
    catch( final SQLException a_oSQLException )
    {
      LOGGER.error("Error while accessing " + getDBName() + " database", a_oSQLException);
      throw new ValidationException( "Error while accessing " + getDBName() + " database('" + a_oSQLException.getLocalizedMessage() + "')!" );
    }
  }  

  /**
   * @param a_strClass
   *            String with name of class to in URL to instance
   * @return Instance of given class type if found else null
   */  
  protected Object createInstance4Class( final String a_strClass )
  {
     try
     {
        return Class.forName( a_strClass ).newInstance();
     }
     catch( final Exception a_oException )
     {
       LOGGER.error("TestIteraplanDatabase:validate(): cannot create new instance of class '" + a_strClass, a_oException);
     }
     return null;
  }
  
  /**
   * @param a_strDriverClassName
   *            String with name of class to be used for connection
   * @param a_strURL
   *            String with connection URL
   * @param a_strUserID
   *            String with user name
   * @param a_strPassword
   *            String with user password
   * @return Connection gotten from DriverManager else NULL
   * @throws Exception 
   */  
  protected Connection getConnectionPerDriverManager( final String a_strDBVendor,
        final String a_strDBAddress, final String a_strDBPort,  final String a_strDBName,  
        final String a_strUserID, final String a_strPassword )
     throws Exception
  {
    
    String strDriverClassName = "";
    final String strURL;
    if( "mysql5".equals( a_strDBVendor ) )
    {
      strDriverClassName = "com.mysql.jdbc.Driver";
      strURL = "jdbc:mysql://" + a_strDBAddress + ":" + a_strDBPort + "/" + a_strDBName;
    }
    else if( "oracle".equals( a_strDBVendor ) )
    {
      strDriverClassName = "oracle.jdbc.OracleDriver";
      strURL = "jdbc:oracle:thin:@" + a_strDBAddress + ":" + a_strDBPort + ":" + a_strDBName;
    }  
    else if( "sqlserver".equals( a_strDBVendor ) )
    {
      strDriverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
      strURL = "jdbc:sqlserver://" + a_strDBAddress + ":" + a_strDBPort + ";databaseName=" + a_strDBName;
    }  
    else
    {
      LOGGER.error("TestIteraplanDatabase:validate(): invalid database vendor '" + a_strDBVendor + "'!");
      throw new Exception( "TestIteraplanDatabase:validate(): invalid database vendor '" + a_strDBVendor + "'!" );
    }
    if( null == createInstance4Class( strDriverClassName ) )
    {
      LOGGER.error("TestIteraplanDatabase:validate(): cannot instanciate JDBC driver!");
      throw new Exception( "TestIteraplanDatabase:validate(): cannot instanciate JDBC driver!" );
    }
    return DriverManager.getConnection( strURL, a_strUserID, a_strPassword );
  }
}

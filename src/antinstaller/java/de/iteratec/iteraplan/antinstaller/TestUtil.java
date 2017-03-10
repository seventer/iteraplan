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

import java.util.Map;

import org.apache.log4j.Logger;
import org.tp23.antinstaller.InstallerContext;

public class TestUtil {
  
  private static final Logger LOGGER = Logger.getLogger(TestUtil.class);
  
  /**
   * @return Map of type <String, String> with all known properties
   * @throws Exception 
   */  
  public static Map getProperties( final InstallerContext a_oInstallerContext )
    throws Exception
  {
    try
    {
      final Map oMapProperties = a_oInstallerContext.getInstaller().getResultContainer().getAllProperties();
      if( null == oMapProperties )
      {
        LOGGER.error("empty properties");
        throw new Exception( "empty properties" );
      }
      return oMapProperties;
    }
    catch( final Exception a_oException )
    {
      LOGGER.error("Can't access properties map!", a_oException);
      throw new Exception( "Can't access properties map!" );
    }
  }
  
  /**
   * @param a_oMapProperties
   *            Map of type <String, String> with all known properties
   * @param a_strPropertyKey
   *            String with name of key for property
   * @param a_strPropertyname
   *            String with official name of element in GUI
   * @return String with value of property if available else throws an exception
   */  
  public static String getProperty( final Map a_oMapProperties, final String a_strPropertyKey,
                                final String a_strPropertyname )
    throws Exception
  {
    final Object oObject = a_oMapProperties.get( a_strPropertyKey );
    if( ( null == oObject ) || ( false == oObject instanceof String ) ||
        ( ( (String)oObject ).trim().length() < 1 ) )
    {
      LOGGER.error("Field '" + a_strPropertyname + "' is empty");
      throw new ValidationException( "Field '" + a_strPropertyname + "' is empty. Please insert a correct value before you continue." );
    }
    return (String)oObject;
  }

}

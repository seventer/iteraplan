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
package de.iteratec.iteraplan.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * Provides access to the log4j.properties file. This is used by iteraplan
 * have access to the audit log file path for debugging purposes and for 
 * letting the user download the audit log file.   
 */
public final class Log4jProperties {

  private static final Logger    LOGGER                = Logger.getIteraplanLogger(Log4jProperties.class);
  /** The name of the log4j properties file. */
  private static final String    LOG4J_PROPERTIES_NAME = "/log4j.properties";

  /** The key for the audit log file path in the log4j properties. */
  public static final String     PROP_AUDIT_LOG_FILE   = "log4j.appender.auditlogfile.File";

  private static Log4jProperties instance              = null;
  private Properties             bundle                = null;

  private Log4jProperties() {
    // nothing to do
  }

  /**
   * Returns the current Log4jProperties instance.
   * 
   * @return current Log4jProperties instance.
   * @throws IteraplanTechnicalException
   */
  @SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
  // synchronized needed here for static access
  public static synchronized Log4jProperties getProperties() throws IteraplanTechnicalException {
    if (instance == null) {
      try {
        instance = new Log4jProperties();
        instance.init();
      } catch (IOException e) {
        instance = null;
        LOGGER.error("Could not access resource + " + LOG4J_PROPERTIES_NAME);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
      }
    }
    return instance;
  }

  /**
   * Returns the value of a property with the given key.
   * 
   * @param propertyName The key of the property.
   * @return The value of the property.
   * @throws IteraplanTechnicalException If the property does not exist in the property file.
   */
  public String getProperty(String propertyName) throws IteraplanTechnicalException {
    String property = bundle.getProperty(propertyName);
    if (property == null) {
      LOGGER.error("unable to extract property '" + propertyName + "'");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
    return property;
  }

  /**
   * Reads the LOG4J_PROPERTIES_NAME file and loads the properties.
   * Called only once.
   * 
   * @throws IOException If the LOG4J_PROPERTIES_NAME could not be loaded.
   */
  private void init() throws IOException {
    bundle = new Properties();
    InputStream iteraplanPropertyStream = null;
    try {
      iteraplanPropertyStream = this.getClass().getResourceAsStream(LOG4J_PROPERTIES_NAME);
      bundle.load(iteraplanPropertyStream);
    } finally {
      if (iteraplanPropertyStream != null) {
        iteraplanPropertyStream.close();
      }
    }
  }

}
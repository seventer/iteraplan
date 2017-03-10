
/*
 * iTURM is a User and Roles Management web application developed by iteratec, GmbH
 * Copyright (C) 2008 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
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
 * You can contact iteratec GmbH headquarters at Inselkammerstraße 4
 * 82008 München - Unterhaching, Germany, or at email address info@iteratec.de.
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
package de.iteratec.turm.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Provides access to the turm.properties file.
 */
public final class TurmProperties {

  private static final Logger   logger                      = Logger
                                                                .getLogger(TurmProperties.class);

  private static final String   TURM_PROPERTIES_NAME        = "/turm.properties";
  public static final String    TURM_DATASOURCE             = "turm.datasource";
  public static final String    TURM_PASSWORD_PATTERN       = "turm.passwordPattern";
  public static final String    TURM_PASSWORD_LENGTH        = "turm.passwordLength";
  public static final String    TURM_PASSWORD_MGMT_DISABLED = "turm.passwordmgmt.disable";

  private static TurmProperties instance                    = null;
  private Properties            bundle                      = null;

  private TurmProperties() {
    // nothing to do
  }

  /**
   * @return The singleton.
   */
  public static synchronized TurmProperties getProperties() {
    if (instance == null) {
      try {
        instance = new TurmProperties();
        instance.init();
      } catch (IOException e) {
        instance = null;
        logger.error("Cannot access resource + " + TURM_PROPERTIES_NAME, e);
      }
    }
    return instance;
  }

  /**
   * @param propertyName The key of the property.
   * @return The value of the property.
   */
  public String getProperty(String propertyName) {
    String property = bundle.getProperty(propertyName);
    if (property == null) {
      logger.error("Property " + propertyName + " not found.");
    }
    return property;
  }

  /**
   * Load the iTURM properties.
   * 
   * @throws IOException
   */
  private void init() throws IOException {
    bundle = new Properties();
    InputStream turmPropertyStream = null;
    try {
      turmPropertyStream = this.getClass().getResourceAsStream(TURM_PROPERTIES_NAME);
      if (turmPropertyStream == null) {
        throw new FileNotFoundException("Could not find properties file: '" + TURM_PROPERTIES_NAME
            + "' in classpath.");
      }
      bundle.load(turmPropertyStream);
    } finally {
      if (turmPropertyStream != null) {
        turmPropertyStream.close();
      }
    }
  }

}
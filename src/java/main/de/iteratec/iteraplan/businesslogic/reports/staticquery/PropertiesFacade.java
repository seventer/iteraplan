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
package de.iteratec.iteraplan.businesslogic.reports.staticquery;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * This class provides access to the .properties file which configures the 
 * available static queries.
 * <p>
 * This class has been primarily introduced to source out the configuration
 * of static queries with regard to the open source version of iteraplan. 
 * That way, it is easily possible to configure a subset of static queries.
 */
public final class PropertiesFacade {

  private static final Logger        LOGGER     = Logger.getIteraplanLogger(PropertiesFacade.class);

  private static final String        RESOURCE   = "/staticquery.properties";

  private static PropertiesFacade    instance   = null;

  private static Map<String, String> properties = new HashMap<String, String>();

  private static List<String>        keys       = new ArrayList<String>();

  private PropertiesFacade() {
    // nothing to do.
  }

  /**
   * Returns the singelton instance of this class. On first access, the instance 
   * is created and the properties list is read from the resource specified in the 
   * field {@link PropertiesFacade#RESOURCE}
   * 
   * @return
   *    See method description.
   *    
   * @throws IteraplanTechnicalException
   *    If the resource could not be successfully accessed.
   */
  @SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
  public static synchronized PropertiesFacade getInstance() {

    if (instance == null) {
      try {
        instance = new PropertiesFacade();
        instance.init();
      } catch (FileNotFoundException ex) {
        instance = null;
        LOGGER.error("Cannot access resource + " + RESOURCE);

        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
      }
    }

    return instance;
  }

  /**
   * Reads the properties list from the resource specified in the
   * field {@link PropertiesFacade#RESOURCE}.
   * 
   * @throws FileNotFoundException
   *    If the properties file could not be found
   */
  private void init() throws FileNotFoundException {
    BufferedReader br = null;
    InputStreamReader inReader = null;

    final InputStream stream = this.getClass().getResourceAsStream(RESOURCE);
    if (stream == null) {
      throw new FileNotFoundException("Properties file '" + RESOURCE + "' not found in classpath.");
    }

    try {
      inReader = new InputStreamReader(stream);
      br = new BufferedReader(inReader);

      String line = br.readLine();
      while (line != null) {
        if (!(line.length() == 0 || line.charAt(0) == '#')) {
          String[] tokens = line.split("=");
          keys.add(tokens[0]);
          properties.put(tokens[0], tokens[1]);
        }
        line = br.readLine();
      }
    } catch (IOException e) {
      LOGGER.error("Cannot access resource + " + RESOURCE, e);
    } finally {
      IOUtils.closeQuietly(br);
      IOUtils.closeQuietly(inReader);
    }
  }

  /**
   * Returns the value of a property with the given key from the properties 
   * list specified in the field {@link PropertiesFacade#RESOURCE}.
   * 
   * @param key 
   *    The key of the property.
   * 
   * @return 
   *    See method description.
   */
  public String getProperty(String key) {
    return properties.get(key);
  }

  /**
   * Returns a list of all keys in the properties list specified in the
   * field {@link PropertiesFacade#RESOURCE}.
   * 
   * @return
   *    See method description.
   */
  public List<String> getAllProperties() {
    return keys;
  }
}
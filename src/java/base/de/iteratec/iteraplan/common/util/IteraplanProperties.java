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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * Provides access to the iteraplan.properties file. The keys in the properties are listed as
 * constants in this class.
 */
public final class IteraplanProperties {

  private static final Logger        LOGGER                                                 = Logger.getIteraplanLogger(IteraplanProperties.class);

  /** The name of the iteraplan properties file. This file must be on the class path. */
  private static final String        ITERAPLAN_PROPERTIES_NAME                              = "/iteraplan.properties";
  private static final String        ITERAPLAN_LOCAL_PROPERTIES_NAME                        = "/iteraplan_local.properties";
  private static final String        ITERAPLAN_DB_PROPERTIES_NAME                           = "/iteraplan-db.properties";
  private static final String        ITERAPLAN_AUTH_PROPERTIES_NAME                         = "/iteraplan-auth.properties";

  /** The key for the build id of this iteraplan release */
  public static final String         PROP_BUILD_ID_TAG                                      = "build.id";

  public static final String         PROP_BUILD_VERSION_TAG                                 = "build.version";

  /** The key for the audit logging setting. Value can be true or false. */
  public static final String         PROP_AUDIT_LOGGING_ENABLED                             = "audit.logging.enabled";

  /** The key for the error reporting setting. Value can be true or false. */
  public static final String         PROP_PROBLEM_REPORTS_ENABLED                           = "problem.reports.enabled";

  /** This "secret" property should only be used by developers! Not in production mode. */
  public static final String         PROP_PROBLEM_REPORTS_DISPLAY_IN_GUI                    = "problem.reports.display.gui";

  /** The key for the last modification logging setting. Value can be true or false. */
  public static final String         PROP_LASTMODIFICATION_LOGGING_ENABLED                  = "lastmodification.logging.enabled";

  /** The version of iteraplan. Either 'oss' or 'enterprise' */
  public static final String         PROP_VERSION                                           = "iteraplan.version";

  /** The key for the preference property, how many search results shall be displayed at once */
  public static final String         PROP_SEARCH_RESULT_COUNT                               = "searchresults.default.count";

  /**
   * The key for the property for choosing among the options how many search results shall be
   * displayed at once
   */
  public static final String         PROP_SEARCH_OPTIONS_COUNT                              = "searchresults.options.count";

  private static final String        ENTERPRISE                                             = "enterprise";

  /**The key for the property that defines the default value of the option to show elements with the "inactive" status.*/
  public static final String         CONFIG_SHOW_INACTIVE                                   = "configuration.show.inactive";

  /** The key for the csv export: description. Value can be true or false. */
  public static final String         PROP_CSV_EXPORT_DESCRIPTION                            = "csv.export.description";

  /** The key for the csv export: timespan. Value can be true or false. */
  public static final String         PROP_CSV_EXPORT_TIMESPAN                               = "csv.export.timespan";

  /** The key for the csv export: status. Value can be true or false. */
  public static final String         PROP_CSV_EXPORT_STATUS                                 = "csv.export.status";

  /** Prefix for csv export keys that point to attributes that are to be exported. */
  public static final String         PREFIX_CSV_ATTR                                        = "csv.attr.";

  /** The key for the separator to use in the generated CSV file. */
  public static final String         CSV_SEPARATOR                                          = "csv.separator";

  /** The key for the separator replacement to use in the generated CSV file. */
  public static final String         CSV_SEPARATOR_REPLACEMENT                              = "csv.separator.replacement";

  /** The key for the value separator to use in the generated CSV file. */
  public static final String         CSV_SEPARATOR_SUB                                      = "csv.separator.sub";

  /** The key for the validation query database connection pool property */
  public static final String         DATABASE_VALIDATIONQUERY                               = "database.validationquery";

  /** The key for the maximum number of data sources to be held by the RoutingDataSource */
  public static final String         MAXIMUM_SIMULTANEOUS_DATASOURCES                       = "maximum.simultaneous.datasources";

  public static final String         PROP_MAX_SIMULTANEOUS_MASSUPDATE_PROPERTIES            = "maximum.simultaneous.massupdate.properties";

  public static final String         PROP_MAX_SIMULTANEOUS_MASSUPDATE_BUILDINGBLOCKS        = "maximum.simultaneous.massupdate.buildingblocks";

  public static final String         PROP_MAX_EXPORT_VISIO_INFORMATIONSTSTEMS               = "maximum.export.visio.informationsystems";

  public static final String         GRAPHICAL_EXPORT_MAX_ELEMENTS                          = "export.graphical.maxElementNumber";

  public static final String         EXPORT_GRAPHICAL_MASTERPLAN_MAXMONTHS                  = "export.graphical.masterplan.maxmonths";
  public static final String         EXPORT_GRAPHICAL_MASTERPLAN_MAXCUSTOMCOLUMNS           = "export.graphical.masterplan.maxcustomcolumns";
  public static final String         EXPORT_GRAPHICAL_CLUSTER_MAXCOLUMNS                    = "export.graphical.cluster.maxcolumns";
  public static final String         EXPORT_GRAPHICAL_CLUSTER_MAXROWS                       = "export.graphical.cluster.maxrows";
  public static final String         EXPORT_GRAPHICAL_CLUSTER_MINCOLUMNS                    = "export.graphical.cluster.mincolumns";
  public static final String         EXPORT_GRAPHICAL_CLUSTER_MINROWS                       = "export.graphical.cluster.minrows";
  public static final String         EXPORT_GRAPHICAL_LANDSCAPE_SCALE_DOWN_GRAPHIC_ELEMENTS = "export.graphical.landscape.scaleDownGraphicElements";

  public static final String         EXCEL_TEMPLATE_NAME                                    = "excel.template.name";
  public static final String         EXCEL_EXPORT_COLUMN_WIDTH_NARROW                       = "excelReport.columnWidth.narrow";
  public static final String         EXCEL_EXPORT_COLUMN_WIDTH_MIDDLE                       = "excelReport.columnWidth.middle";
  public static final String         EXCEL_EXPORT_COLUMN_WIDTH_WIDE                         = "excelReport.columnWidth.wide";
  public static final String         EXCEL_EXPORT_ID_COLUMN_WIDTH                           = "excelReport.columnWidth.id";

  public static final String         NOTIFICATION_ACTIVATED                                 = "notification.activated";
  public static final String         NOTIFICATION_SMTP_SERVER                               = "notification.smtpserver";
  public static final String         NOTIFICATION_PORT                                      = "notification.port";
  public static final String         NOTIFICATION_EMAIL_FROM                                = "notification.email.from";
  public static final String         NOTIFICATION_SSL                                       = "notification.ssl.enable";
  public static final String         NOTIFICATION_STARTTLS                                  = "notification.starttls.enable";
  public static final String         NOTIFICATION_USERNAME                                  = "notification.username";
  public static final String         NOTIFICATION_PASSWORD                                  = "notification.password";

  public static final String         WIKI_SYNTAX                                            = "wiki.syntax";

  public static final String         HISTORY_ENABLED                                        = "history.enabled";
  public static final String         APPLICATION_ADDRESS_FROM_PROPERTIES                    = "urlbuilder.application.address";
  public static final String         SEAL_EXPIRATION_DAYS                                   = "seal.expiration.days";

  public static final String         TIMESERIES_ENABLED                                     = "timeseries.enabled";

  public static final String         TREE_VIEW_PARTIAL_ACTIVATION_THRESHOLD                 = "treeview.partial.activationThreshold";
  public static final String         TREE_VIEW_PARTIAL_INITIAL_ELEMENTS                     = "treeview.partial.initialElements";

  public static final String         ADMIN_EMAIL                                            = "admin.email";

  public static final String         IMPORT_QUEUE_CLEANUP_INTERVAL                          = "import.queue.cleanup.interval.hours";
  public static final String         IMPORT_TIMEOUT                                         = "import.timeout.hours";

  public static final String         LDAP_FIELDNAME_FIRSTNAME                               = "ldap.attribute.firstname";
  public static final String         LDAP_FIELDNAME_LASTNAME                                = "ldap.attribute.lastname";
  public static final String         LDAP_FIELDNAME_EMAIL                                   = "ldap.attribute.email";

  private static IteraplanProperties instance                                               = null;
  private Properties                 bundle                                                 = null;
  private static final Object        LOCK                                                   = new Object();

  private IteraplanProperties() {
    // nothing to do; Singleton
  }

  /**
   * Return IteraplanProperties instance.
   * 
   * @return the current IteraplanProperties instance.
   * @throws IteraplanTechnicalException
   */
  public static IteraplanProperties getProperties() throws IteraplanTechnicalException {
    synchronized (LOCK) {

      if (instance != null) {
        return instance;
      }

      try {
        instance = new IteraplanProperties();
        instance.init();
      } catch (IOException e) {
        instance = null;
        LOGGER.error("Could not access resource: " + ITERAPLAN_PROPERTIES_NAME);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
      }
      return instance;
    }
  }

  /**
   * Returns the value of the build id contained in the iteraplan.properties file.
   * 
   * @return the iteraplan build id.
   */
  public String getBuildId() {
    String buildId = bundle.getProperty(PROP_BUILD_ID_TAG);
    if (buildId == null) {
      return "Build.Id is undefined";
    }
    return buildId;
  }

  public String getBuildVersion() {
    String buildVersion = bundle.getProperty(PROP_BUILD_VERSION_TAG);
    if (buildVersion == null) {
      return "Build.Version is undefined";
    }
    return buildVersion;
  }

  /**
   * Returns the value of a property with the given key.
   * 
   * @param propertyName
   *          The key of the property.
   * @return The value of the property.
   * @throws IteraplanTechnicalException
   *           If the property does not exist in the property file.
   */
  public String getProperty(String propertyName) throws IteraplanTechnicalException {
    String property = bundle.getProperty(propertyName);
    if (property == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.PROPERTY_MISSING, propertyName);
    }
    return property;
  }

  /**
   * Checks if a property with the given key has the value 'true'.
   * 
   * @param propertyName
   *          The key of the property.
   * @return true, if the property exists and it has a value of true. False, if the property does
   *         not exist, is not set to true, or if the iteraplan.properties could not be found.
   */
  public boolean propertyIsSetToTrue(String propertyName) {
    String propertyValue = "";
    try {
      propertyValue = getProperty(propertyName);
    } catch (IteraplanTechnicalException e) {
      return false;
    }
    propertyValue = propertyValue.trim();
    return propertyValue.equalsIgnoreCase("true");
  }

  /**
   * Retrieves an integer value from the property with the given <code>propertyName</code>.
   * 
   * @param propertyName
   *          A valid property key
   * @return an integer value, identified by the given property name
   * @throws NumberFormatException
   *           if the property contains a string which is not a valid integer
   * @throws IteraplanTechnicalException
   *           if no property with the given name exists
   */
  public static int getIntProperty(String propertyName) {
    try {
      return Integer.parseInt(IteraplanProperties.getProperties().getProperty(propertyName));
    } catch (NumberFormatException nfe) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INCORRECT_INTEGER_FORMAT, nfe);
    }
  }

  public static boolean getBooleanProperty(String propertyName) {
    return Boolean.parseBoolean(IteraplanProperties.getProperties().getProperty(propertyName));
  }

  /**
   * Return the keys of all properties in the iteraplan.properties file.
   * 
   * @return a set of keys.
   */
  public Collection<Object> getAllPropertyKeys() {
    return Collections.unmodifiableCollection(bundle.keySet());
  }

  /**
   * Reads the iteraplan.properties file and loads the properties. Called only once.
   * 
   * @throws IOException
   *           If the iteraplan.properties could not be loaded.
   */
  private void init() throws IOException {
    bundle = new Properties();
    InputStream iteraplanPropertyStream = null;
    try {
      iteraplanPropertyStream = this.getClass().getResourceAsStream(ITERAPLAN_PROPERTIES_NAME);
      // This file MUST be present -> fail otherwise
      if (iteraplanPropertyStream == null) {
        throw new FileNotFoundException("Could not find properties file: '" + ITERAPLAN_PROPERTIES_NAME + "' in classpath.");
      }
      bundle.load(iteraplanPropertyStream);
    } finally {
      if (iteraplanPropertyStream != null) {
        iteraplanPropertyStream.close();
      }
    }

    List<String> optionalPropertyFiles = new ArrayList<String>(Arrays.asList(new String[] { ITERAPLAN_LOCAL_PROPERTIES_NAME,
        ITERAPLAN_DB_PROPERTIES_NAME, ITERAPLAN_AUTH_PROPERTIES_NAME }));

    for (String propertyFile : optionalPropertyFiles) {
      try {
        iteraplanPropertyStream = this.getClass().getResourceAsStream(propertyFile);
        // this file is optional -> skip if it doesn't exist
        if (iteraplanPropertyStream != null) {
          bundle.load(iteraplanPropertyStream);
        }
      } finally {
        if (iteraplanPropertyStream != null) {
          iteraplanPropertyStream.close();
        }
      }
    }

  }

  /**
   * Checks if iteraplan is running in oss or enterprise mode
   * 
   * @return <code>true</code> if iterplan runs in enterprise mode, <code>false</code> else
   */
  public Boolean isEnterpriseVersion() {
    String propertyValue;
    try {
      propertyValue = getProperty(PROP_VERSION);
    } catch (IteraplanTechnicalException e) {
      return Boolean.FALSE;
    }
    return Boolean.valueOf(ENTERPRISE.equals(propertyValue));
  }

  /**
   * Checks if a property is set.
   * 
   * @param key
   *          Key of the property
   * @return TRUE if property with key exists, else false
   */
  public boolean containsKey(String key) {
    boolean containsKey = false;
    if (bundle.containsKey(key)) {
      containsKey = true;
    }
    return containsKey;
  }

}
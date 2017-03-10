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
package de.iteratec.iteraplan.presentation;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.fop.util.DecimalFormatCache;
import org.hibernate.type.StringType;
import org.springframework.context.ApplicationContext;

import de.iteratec.iteraplan.common.DefaultSpringApplicationContext;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.common.util.Log4jProperties;
//import org.apache.fop.util.DecimalFormatCache;


/**
 * The primary iteraplan servlet listener This class is called by the servlet container when the
 * application is started or stopped. It does initialization and tear down work and logs relevant
 * information on info level.
 */
public final class ContextListener implements ServletContextListener {

  private static final Logger LOGGER = Logger.getIteraplanLogger(ContextListener.class);

  public void contextInitialized(ServletContextEvent event) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Initializing the iteraplan context.");
    }
    logApplicationInfo();
    checkAuditLoggingSettings();
    checkLastModificationSettings();
    checkHibernatePatch();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Done initializing the iteraplan context.");
    }

    ApplicationContext context = DefaultSpringApplicationContext.getSpringApplicationContext();
    if (context == null) {
      LOGGER.fatal("Spring Application Context not available. Without that infrastructure, iteraplan does not work! Check your configuration for syntax errors.");
      return;
    }
  }

  public void contextDestroyed(ServletContextEvent event) {
    LOGGER.debug("The iteraplan context has been removed. Cleaning up ...");

    saveCoberturaReport();
    deregisterJdbcDrivers();

    // Clear the cache of the fop pdf transcoder.
    // Note that this method is additionally implemented in the patched version
    // and not available in the original distribution.
    // Also, while error messages are still reported when unloading iteraplan,
    // there is no actual memory leak after this method is called.
    DecimalFormatCache.destroyCache();

    LOGGER.debug("Done cleaning up the iteraplan context.");
  }

  /**
   * JDBC drivers are implicitly referenced by the DriverManager which is on the container class
   * loader. If JDBC drivers are not unloaded explicitly, they will prevent the application from
   * undeploying properly.
   */
  @SuppressWarnings("PMD.UseProperClassLoader")
  private void deregisterJdbcDrivers() {
    LOGGER.debug("Deregistering JDBC drivers from this webapp");
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    ArrayList<Driver> driversToUnload = new ArrayList<Driver>();

    ClassLoader myClassLoader = getClass().getClassLoader();
    while (drivers.hasMoreElements()) {
      Driver driver = drivers.nextElement();
      ClassLoader driverClassLoader = driver.getClass().getClassLoader();

      // make sure to unload only drivers which have been loaded inside this
      // servlet context's class loader; contrary to the suppressed PMD warning, we do *not* want
      // the context classloader in this case
      if (driverClassLoader != null && driverClassLoader.equals(myClassLoader)) {
        driversToUnload.add(driver);
      }
    }
    for (Driver driver : driversToUnload) {
      try {
        DriverManager.deregisterDriver(driver);
        LOGGER.debug("Unloaded driver " + driver.getClass().getName());
      } catch (SQLException e) {
        LOGGER.debug("JDBC driver manager refused to deregister a driver. Giving up on it.", e);
      }
    }
  }

  /**
   * If a test coverage report is to be generated, a Cobertura method has to be called when
   * destroying the context.
   */
  private void saveCoberturaReport() {
    // We need to save the Cobertura report if we are coverage testing. Since we don't want
    // a dependency on Cobertura in a normal environment, we just call it by reflection if
    // we find the relevant Cobertura class. This way the report is written if Cobertura
    // is on the classpath
    String coberturaReflectionError = "Could not save Cobertura project data through reflection";
    try {
      Class<?> projectDataClass = Class.forName("net.sourceforge.cobertura.coveragedata.ProjectData");
      Method method = projectDataClass.getMethod("saveGlobalProjectData");
      method.invoke(null);
    } catch (ClassNotFoundException e) {
      // we just do nothing
      // From here: We just log, we can't do anything -- the reporting will fail.
    } catch (SecurityException e) {
      LOGGER.error(coberturaReflectionError, e);
    } catch (NoSuchMethodException e) {
      LOGGER.error(coberturaReflectionError, e);
    } catch (IllegalArgumentException e) {
      LOGGER.error(coberturaReflectionError, e);
    } catch (IllegalAccessException e) {
      LOGGER.error(coberturaReflectionError, e);
    } catch (InvocationTargetException e) {
      LOGGER.error(coberturaReflectionError, e);
    }
  }

  /**
   * Check if Hibernate is patched correctly.
   */
  private void checkHibernatePatch() {
    try {
      StringType.isPatched();
    } catch (NoSuchMethodError e) {
      LOGGER
      .error(
          "Hibernate StringType class is not patched for Oracle use! Running the application on Oracle will result in incorrect behaviour. Make sure that the classpath is set correctly.",
          e);
    }
  }

  /**
   * Check if audit logging is activated. If it is, check if the audit logging path is correct.
   */
  private void checkAuditLoggingSettings() {

    try {
      boolean enabled = IteraplanProperties.getProperties().propertyIsSetToTrue(IteraplanProperties.PROP_AUDIT_LOGGING_ENABLED);
      if (enabled) {
        String auditlogFile = Log4jProperties.getProperties().getProperty(Log4jProperties.PROP_AUDIT_LOG_FILE);
        File f = new File(auditlogFile);
        if (!f.canWrite()) {
          LOGGER.error("Audit logging is activated, but path to audit log file is not accessible: " + auditlogFile);
        }
        else {
          LOGGER.info("Audit logging is activated. Audit log file path is: " + auditlogFile);
        }
      }
      else {
        LOGGER.info("Audit logging is deactivated.");
      }
    } catch (IteraplanTechnicalException ex) {
      // cannot throw an exception here
      LOGGER.error("Could not read iteraplan property file.", ex);
    }
  }

  /**
   * Check if the last modification setting is turned on.
   */
  private void checkLastModificationSettings() {
    try {
      boolean enabled = IteraplanProperties.getProperties().propertyIsSetToTrue(IteraplanProperties.PROP_LASTMODIFICATION_LOGGING_ENABLED);
      if (enabled) {
        LOGGER.info("Last modification logging is activated.");
      }
      else {
        LOGGER.info("Last modification logging is deactivated.");
      }
    } catch (IteraplanTechnicalException e) {
      // cannot throw an exception here
      LOGGER.error("Could not read iteraplan property file.", e);
    }
  }

  /**
   * Print the build id.
   */
  private void logApplicationInfo() {
    try {
      LOGGER.info("Build ID is '" + IteraplanProperties.getProperties().getBuildId() + "'");
    } catch (IteraplanTechnicalException e) {
      // cannot throw an exception here.
      LOGGER.error("Could not read iteraplan property file.", e);
    }
  }

}
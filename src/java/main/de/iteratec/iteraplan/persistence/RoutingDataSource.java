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
package de.iteratec.iteraplan.persistence;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.IteraplanProperties;


/**
 * This class provides functionality to dynamically add a {@link javax.sql.DataSource} at
 * runtime to route different users to different databases. The code's majority is based on
 * Spring's {@link org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource}.
 * <p>
 * Note that this class permits adding any type of object as a data source, but the default
 * implementation supports {@link javax.sql.DataSource}s and String objects which will be
 * resolved by means of a {@link #setDataSourceLookup(DataSourceLookup) DataSourceLookup}.
 * <p>
 * Spring's implementation is limited to reading and storing bean definitions of data
 * sources injected at container startup into a map of possible target data sources.
 * <p>
 * To avoid storing unlimited numbers of data sources, the current implementation provides
 * a method to add data sources of type {@link CachableBasicDataSource}. These carry an
 * access identifier to decide which data source to remove from the map of target data
 * sources.
 * <p>
 * See also: http://blog.interface21.com/main/2007/01/23/dynamic-datasource-routing/
 */
public class RoutingDataSource extends AbstractDataSource implements InitializingBean {

  private static final Logger LOGGER = Logger.getIteraplanLogger(RoutingDataSource.class);

  /** {@link #setTargetDataSources()} */
  private Map<Object, DataSource> targetDataSources;

  private Map<Object, DataSource> resolvedDataSources;
  private static final Object     RESOLVED_DATA_SOURCES_LOCK = new Object();

  /** {@link #setDataSourceLookup(DataSourceLookup)} */
  private DataSourceLookup        dataSourceLookup           = new JndiDataSourceLookup();

  /** {@link #setCloseDataSources(String) } */
  private Map<String, String>     closeDataSources;

  // time stamp when the last log entry about pool utilization was made; the timestamp is used to throttle logging
  private long previousLoggingTimestamp;

  /**
   * Adds a data source of type {@link CachableBasicDataSource} to the map of
   * {@link #setTargetDataSources(Map) target data sources}.
   * 
   * @param key
   *    The lookup key the data source is mapped to.
   * @param value
   *    The data source.
   */
  public void addCachableBasicDataSource(Object key, DataSource value) {

    if (!(value instanceof CachableBasicDataSource)) {
      throw new IllegalArgumentException("The parameter 'value' must be of type 'CachableDataSource'.");
    }

    int maximumCacheSize = IteraplanProperties.getIntProperty(IteraplanProperties.MAXIMUM_SIMULTANEOUS_DATASOURCES);

    if (maximumCacheSize <= 0) {
      throw new IllegalArgumentException("The cache size property must have a value greater than zero.");
    }

    // This code block needs to be synchronized because only ONE thread at a time must be allowed
    // to change the shared map of target data sources. Otherwise race conditions could occur.
    // All other accesses to targetDataSources can remain without explicit synchronization, because they
    // operate on ConcurrentHashMap.
    synchronized (this) {
      if (targetDataSources.size() >= maximumCacheSize) {
        logger.info("Cache exceeded: Trying to remove least recently accessed data source.");

        long timestampToRemove = System.currentTimeMillis();
        Object lookupKeyToRemove = null;
        DataSource dataSourceToRemove = null;

        for (Map.Entry<Object, DataSource> entry : this.targetDataSources.entrySet()) {
          Object lookupKey = resolveSpecifiedLookupKey(entry.getKey());
          DataSource dataSource = resolveSpecifiedDataSource(entry.getValue());

          // The MASTER data source must not be removed from the map.
          if (lookupKey.equals(Constants.MASTER_DATA_SOURCE)) {
            continue;
          }

          if (!(dataSource instanceof CachableBasicDataSource)) {
            throw new IllegalStateException("Target data source is of type '" + dataSource.getClass().getSimpleName()
                + "', but must be of type 'CachableDataSource'");
          }

          long timestamp = ((CachableBasicDataSource) dataSource).getTimestamp();
          if (timestamp < timestampToRemove) {
            timestampToRemove = timestamp;
            lookupKeyToRemove = lookupKey;
            dataSourceToRemove = dataSource;
            logger.trace("The currently oldest timestamp is " + timestampToRemove + " for the data source with the lookup key " + lookupKeyToRemove);
          }
        }

        logger.info("Removing data source '" + lookupKeyToRemove + "' with timestamp " + timestampToRemove);

        if (lookupKeyToRemove != null) {
          targetDataSources.remove(lookupKeyToRemove);
        }
        if (dataSourceToRemove != null) {
          destroyDataSource(dataSourceToRemove);
        }
      }
    }
    logger.info("Adding key '" + key + "' to the map of data sources.");

    targetDataSources.put(key, value);

    updateDataSourceRouter();
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  public void afterPropertiesSet() {
    if (targetDataSources == null) {
      throw new IllegalArgumentException("The property 'targetDataSources' must not be null.");
    }
    updateDataSourceRouter();
  }

  /**
   * Callback method to hook into the Spring Bean lifecylce.
   * <p>
   * This implementation closes all data sources managed by this bean.
   * <p>
   * @see #setCloseDataSources(Map)
   */
  public void destroy() {

    for (Object dataSource : resolvedDataSources.values()) {
      destroyDataSource(dataSource);
    }
  }

  /*
   * (non-Javadoc)
   * @see javax.sql.DataSource#getConnection()
   */
  public Connection getConnection() throws SQLException {
    return determineTargetDataSource().getConnection();
  }

  /*
   * (non-Javadoc)
   * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
   */
  public Connection getConnection(String username, String password) throws SQLException {
    return determineTargetDataSource().getConnection(username, password);
  }

  /**
   * Checks if the given lookup key is already contained in the data source router.
   * 
   * @param key
   *    The key to look for in the data source router.
   * 
   * @return
   *    {@code True}, if the key is contained. {@code False}, otherwise.
   */
  public boolean isKeyContained(String key) {
    return resolvedDataSources.containsKey(key);
  }

  /**
   * In order to support different implementations of {@link javax.sql.DataSource}s, a map may be
   * specified which maps the simple class names of theses implementations to their respective close
   * methods, i.e. the methods that free up any resources occupied by the data source. These methods
   * are called via {@link #destroy() reflection}. If some implementation does not provide a close
   * method, nothing will be done before the Spring container shuts down.
   * <p>
   * Note that supported close methods do not support parameters.
   *
   * @param closeDataSources
   *    See method description.
   */
  public void setCloseDataSources(Map<String, String> closeDataSources) {
    this.closeDataSources = closeDataSources;
  }

  /**
   * Specify the {@link DataSourceLookup} implementation to use for resolving data source name
   * strings in the {@link #setTargetDataSources targetDataSources} map.
   * <p>
   * The property defaults to a {@link JndiDataSourceLookup}, allowing the JNDI names of application
   * server data sources to be specified directly.
   */
  public void setDataSourceLookup(DataSourceLookup dataSourceLookup) {
    this.dataSourceLookup = (dataSourceLookup != null ? dataSourceLookup : new JndiDataSourceLookup());
  }

  /**
   * Specify a map of available target data sources. Data sources are mapped by a lookup key. The value
   * may contain one of the following:
   * <ul>
   *    <li>An instance of {@link javax.sql.DataSource}.</li>
   *    <li>A data source name string (to be resolved via a {@link #setDataSourceLookup DataSourceLookup}.</li>
   * <ul>
   * The key may be of arbitrary type; this class implements the generic lookup process only. The
   * concrete key representation will be handled by {@link #resolveSpecifiedLookupKey(Object)} and
   * {@link #determineCurrentLookupKey()}.
   */
  public void setTargetDataSources(Map<Object, DataSource> targetDataSources) {
    this.targetDataSources = new ConcurrentHashMap<Object, DataSource>(targetDataSources);
  }

  /**
   * Reflectively calls a method specified in the {@link #closeDataSources} map that closes all
   * resources occupied by the given data source. The matching between this object and the close
   * method is done via the simple class name of the passed-in object. Note that for now this
   * implementation only supports close methods without parameters.
   * 
   * @param dataSource
   *    The data source object to close.
   */
  private void destroyDataSource(Object dataSource) {

    String simpleName = dataSource.getClass().getSimpleName();
    if (closeDataSources.containsKey(simpleName)) {
      String closeMethod = closeDataSources.get(simpleName);

      // Close data sources via reflection.
      try {
        Method method = dataSource.getClass().getMethod(closeMethod);
        method.invoke(dataSource);
        logger.info("Data source of type " + simpleName + " closed.");
      } catch (NoSuchMethodException e) {
        logger.error("Exception: Destroying data source '" + dataSource + "'.", e);
      } catch (IllegalArgumentException e) {
        logger.error("Exception: Destroying data source '" + dataSource + "'.", e);
      } catch (IllegalAccessException e) {
        logger.error("Exception: Destroying data source '" + dataSource + "'.", e);
      } catch (InvocationTargetException e) {
        logger.error("Exception: Destroying data source '" + dataSource + "'.", e);
      }
    }
  }

  /**
   * Updates the data source router in case new data sources have been added.
   */
  private void updateDataSourceRouter() {

    synchronized (RESOLVED_DATA_SOURCES_LOCK) {
      // no need to synchronize access on targetDataSources, as it is a ConcurrentHashMap
      if (resolvedDataSources == null) {
        resolvedDataSources = new ConcurrentHashMap<Object, DataSource>(targetDataSources.size());
      }

      resolvedDataSources.clear();
      for (Map.Entry<Object, DataSource> entry : this.targetDataSources.entrySet()) {
        Object lookupKey = resolveSpecifiedLookupKey(entry.getKey());
        DataSource dataSource = resolveSpecifiedDataSource(entry.getValue());
        resolvedDataSources.put(lookupKey, dataSource);
      }
    }

  }

  /**
   * The current lookup key is determined via the thread-bound {@link UserContext} object of
   * iteraplan. This object holds a {@link UserContext#getDataSource() dataSource} property
   * which contains the lookup key to the data source to be used.
   * <p>
   * Allows for arbitrary keys. The returned key needs to match the stored lookup key type, as
   * resolved by the {@link #resolveSpecifiedLookupKey} method.
   */
  protected Object determineCurrentLookupKey() {

    UserContext uc = UserContext.getCurrentUserContext();
    if (uc != null) {
      String lookupKey = uc.getDataSource();
      logger.debug("Data source: " + lookupKey);
      return lookupKey;
    }
    else {
      logger.debug("Data source: MASTER");
      return Constants.MASTER_DATA_SOURCE;
    }
  }

  /**
   * Retrieve the current target data source.
   * <ul>
   *    <li>Updates the data source router in case new data sources have been added.</li>
   *    <li>Determines the {@link #determineCurrentLookupKey() current lookup key}</li>
   *    <li>Performs a lookup in the {@link #setTargetDataSources targetDataSources} map</li>
   * 
   * @see #determineCurrentLookupKey()
   */
  protected DataSource determineTargetDataSource() {

    if (resolvedDataSources == null) {
      throw new IllegalStateException("The data source router has not been initialized.");
    }

    // Retrieve the current target data source.
    Object lookupKey = determineCurrentLookupKey();
    DataSource dataSource = resolvedDataSources.get(lookupKey);

    if (dataSource == null) {
      throw new IllegalStateException("Target data source for lookup key [" + lookupKey + "] cannot be determined.");
    }
    
    if (LOGGER.isDebugEnabled()) {
      logPoolUtilization(dataSource, lookupKey.toString());
    }

    return dataSource;
  }
  
  /**
   * Write current utilization values for the passed data source source to the log (on DEBUG level). Log entries are written in intervals of at least
   * five seconds. This is intended so that the log is not flooded.
   * 
   * @param ds
   *          The DataSource to inspect. Right now, only {@link BasicDataSource} is supported, other implementations will be silently skipped.
   * @param dbname
   *          The name of the DataSource, as it is registered in the RoutingDataSource.
   */
  @SuppressWarnings("boxing")
  protected void logPoolUtilization(DataSource ds, String dbname) {
    if (! (ds instanceof BasicDataSource)) {
      // cannot retrieve values from other types, so skip
      return;
    }
    
    long currentTime = System.currentTimeMillis();
    // log at most every five seconds
    if (currentTime - previousLoggingTimestamp < 5000) {
      return;
    }

    previousLoggingTimestamp = currentTime;
    BasicDataSource bds = (BasicDataSource) ds;
    int bdsNumActive = bds.getNumActive();
    int bdsMaxActive = bds.getMaxActive();
    int bdsNumIdle = bds.getNumIdle();

    LOGGER.debug("DS {0}, {1} active, {2} idle, {3} max total", dbname, bdsNumActive, bdsNumIdle, bdsMaxActive);
  }

  /**
   * Resolve the specified object into a {@link javax.sql.DataSource} instance.
   * <p>
   * The default implementation handles:
   * <ul>
   *    <li>Instances of {@link javax.sql.DataSource}</li>
   *    <li>Data source names (to be resolved via a {@link #setDataSourceLookup DataSourceLookup}).</li>
   * 
   * @param dataSource
   *    The data source object as specified in the {@link #setTargetDataSources targetDataSources} map.
   * 
   * @return
   *    The resolved {@link javax.sql.DataSource} (Never <code>null</code>).
   * 
   * @throws IllegalArgumentException
   *    If the given object is of an unsupported type.
   */
  protected DataSource resolveSpecifiedDataSource(Object dataSource) {

    if (dataSource instanceof DataSource) {
      return (DataSource) dataSource;
    }
    else if (dataSource instanceof String) {
      return dataSourceLookup.getDataSource((String) dataSource);
    }
    else {
      throw new IllegalArgumentException("Illegal data source type - only [javax.sql.DataSource] and String supported: " + dataSource);
    }
  }

  /**
   * Resolves the given lookup key, as specified in the {@link #setTargetDataSources
   * targetDataSources} map, into the actual lookup key to be used for matching with
   * the {@link #determineCurrentLookupKey() current lookup key}.
   * <p>
   * The default implementation simply returns the given key as-is.
   * 
   * @param lookupKey
   *    The lookup key object as specified by the user.
   * 
   * @return
   *    The lookup key as needed for matching
   */
  protected Object resolveSpecifiedLookupKey(Object lookupKey) {
    return lookupKey;
  }

  public java.util.logging.Logger getParentLogger() {
    return null;
  }
}
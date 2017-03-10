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
package de.iteratec.iteraplan.businesslogic.service;

import static de.iteratec.iteraplan.common.error.IteraplanErrorMessages.LOGIN_DB_DATASOURCE_EXPIRED;
import static de.iteratec.iteraplan.common.error.IteraplanErrorMessages.LOGIN_DB_DATASOURCE_INCONSISTENT;
import static de.iteratec.iteraplan.common.error.IteraplanErrorMessages.LOGIN_DB_DATASOURCE_NOT_AVAILABLE;

import java.util.Date;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.user.DataSource;
import de.iteratec.iteraplan.persistence.CachableBasicDataSource;
import de.iteratec.iteraplan.persistence.RoutingDataSource;
import de.iteratec.iteraplan.persistence.dao.DataSourceDAO;


/**
 * Implementation of the service interface {@link DataSourceService}.
 */
public class DataSourceServiceImpl implements DataSourceService {

  private static final Logger LOGGER = Logger.getIteraplanLogger(DataSourceServiceImpl.class);

  private DataSourceDAO       dataSourceDAO;
  private RoutingDataSource   routingDataSource;

  public void setDataSourceDAO(DataSourceDAO dataSourceDAO) {
    this.dataSourceDAO = dataSourceDAO;
  }

  public void setRoutingDataSource(RoutingDataSource routingDataSource) {
    this.routingDataSource = routingDataSource;
  }

  /** {@inheritDoc} */
  public List<DataSource> loadDataSources() {
    return dataSourceDAO.loadDataSources();
  }

  /** {@inheritDoc} */
  public DataSource validateKey(String key) {
    LOGGER.info("Validating data source with the key '" + key + "'.");

    // A lookup key must be assigned to the user.
    if (StringUtils.isEmpty(key)) {
      throw new IteraplanTechnicalException(LOGIN_DB_DATASOURCE_INCONSISTENT);
    }

    // If the user has been assigned to the master data source, no additional data source must be
    // created.
    if (key.equals(Constants.MASTER_DATA_SOURCE)) {
      return null;
    }

    // Try to find the data source stored for the lookup key assigned to the user. Any exceptions
    // are wrapped in an iteraplan exception and thrown back to the caller.
    DataSource dataSource;
    try {
      dataSource = getDataSourceByKey(key);
    } catch (RuntimeException ex) {
      throw new IteraplanTechnicalException(LOGIN_DB_DATASOURCE_INCONSISTENT, ex);
    }

    // If the data source could not be found.
    if (dataSource == null) {
      throw new IteraplanTechnicalException(LOGIN_DB_DATASOURCE_NOT_AVAILABLE);
    }

    // If an expiry date is present and the access to the data source has expired.
    if (dataSource.getExpiryDate() != null && dataSource.getExpiryDate().before(new Date())) {
      throw new IteraplanTechnicalException(LOGIN_DB_DATASOURCE_EXPIRED);
    }

    return dataSource;
  }

  /** {@inheritDoc} */
  public void initializeDBwithKey(String key) {
    DataSource dataSource = validateKey(key);
    if (dataSource == null) {
      // The key points to the MASTER data source. Nothing todo.
      return;
    }

    // Validation succeeded.
    // Add the data source only if it is not contained already.
    if (!routingDataSource.isKeyContained(key)) {

      // Create a basic data source.
      BasicDataSource ds = new CachableBasicDataSource();
      ds.setDriverClassName(dataSource.getDriver());
      ds.setUrl(dataSource.getUrl());
      ds.setUsername(dataSource.getUser());
      ds.setPassword(dataSource.getPassword());
      ds.setDefaultAutoCommit(false);

      String validationQuery = IteraplanProperties.getProperties().getProperty(IteraplanProperties.DATABASE_VALIDATIONQUERY);
      ds.setValidationQuery(validationQuery);
      ds.setTestOnBorrow(true);
      // basicDataSource.setMaxActive(5);
      // basicDataSource.setMinIdle(1);

      routingDataSource.addCachableBasicDataSource(key, ds);
    }
  }

  /**
   * Returns the {@link DataSource} stored under the given key.
   * 
   * @param key
   *          The lookup key to identify the data source.
   * @return The data source stored under the given key or {@code null} if no such data source could
   *         be found.
   * @throws IllegalArgumentException
   *           If the given string key is null or empty.
   * @throws IllegalStateException
   *           If more than one data source was found for the given key.
   */
  private DataSource getDataSourceByKey(String key) {
    if (StringUtils.isEmpty(key)) {
      throw new IllegalArgumentException("The lookup key must not be null or the empty string.");
    }

    LOGGER.info("Retrieving data source for the lookup key '" + key + "'");
    List<DataSource> dataSources = dataSourceDAO.getDataSourceByKey(key);

    if (dataSources.size() == 0) {
      LOGGER.warn("No data source found for the lookup key '" + key + "'");
      return null;
    }

    // Even though the lookup key is mapped with a unique constraint, check if more than
    // one data source was found for the given key.
    if (dataSources.size() > 1) {
      throw new IllegalStateException("Inconsistent data: More than one data source found for the lookup key '" + key + "'");
    }

    return dataSources.get(0);
  }

}
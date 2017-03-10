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
package de.iteratec.turm.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import de.iteratec.turm.common.Logger;
import de.iteratec.turm.dao.common.OlVersionCheck;
import de.iteratec.turm.dao.common.UpdateStatement;
import de.iteratec.turm.exceptions.DaoException;
import de.iteratec.turm.exceptions.UniqueConstaintException;
import de.iteratec.turm.model.IdEntity;


/**
 * A base class for DAOs.
 * 
 * Provides functionality to execute select, update and insert statements within
 * one transaction.
 */
public class BaseDao {

  private static final Logger LOGGER = Logger.getLogger(BaseDao.class);

  private JdbcTemplate        jdbcTemplate;

  /**
   * Configures and stores the Data source for DAO operations.
   * @param ds The configured data source.
   */
  public void setDataSource(DataSource ds) {
    this.jdbcTemplate = new JdbcTemplate(ds);
  }

  /**
   * Execute one select query.
   * 
   * @param query The query to execute.
   * @param resultSetExtractor A Spring ResultSetExtractor that turns the selected rows into objects
   * @param params The parameters that are to be set into the prepared statement.
   * @return A List of Objects, as produced by the called-provided ResultSetExtractor
   * @throws DaoException
   */
  protected <T extends IdEntity> List<T> executeQuery(String query, ResultSetExtractor<List<T>> resultSetExtractor, Object... params) {
    LOGGER.debug(" **** Running query: " + query);
    return jdbcTemplate.query(query, resultSetExtractor, params);
  }

  /**
   * Performs a number of insert or update statements within one transaction.
   * 
   * @param statements The update or insert statements to execute.
   * @param olVersionCheck A check for the optimistic locking version.
   *                       If it fails, no statements will be executed. Can be null.
   * @return The number of affected rows. Returns -1 when the olVersionCheck failed.
   * @throws DaoException
   */
  protected int executeUpdates(List<UpdateStatement> statements, OlVersionCheck olVersionCheck) throws DaoException {
    LOGGER.debug("start executeUpdates");
    int rowsAffected = 0;
    try {
      if (olVersionCheck != null) {
        int olVersionInDb = jdbcTemplate.queryForInt(olVersionCheck.getQuery(), olVersionCheck.getId());

        if (olVersionInDb != olVersionCheck.getOlVersionToCheck().intValue()) {
          throw new OptimisticLockingFailureException("The object in the database has been changed concurrently. Aborting the current update operation.");
        }
      }

      for (UpdateStatement query : statements) {
        LOGGER.debug(" **** Sending statement: " + query.getQuery());
        rowsAffected += jdbcTemplate.update(query.getQuery(), query.getParams());
      }
    } catch (DuplicateKeyException dupEx) {
      throw new UniqueConstaintException("Unique key constraint violation", dupEx);
    }

    LOGGER.debug("finish executeUpdates");
    return rowsAffected;
  }

  /**
   * Determines the next valid id.
   * 
   * Since iTURM has to work with different databases, the key generation is done
   * explicitly by storing the next valid id in a database table. This method
   * fetches the next valid id and increases it by one.
   * 
   * @return The next valid id.
   * @throws DaoException If the next id could not be read or it could not be
   *                      incremented.
   */
  public Number getNextId() throws DaoException {
    Number nextId = jdbcTemplate.queryForInt("select max(nextId) as nextId from im_id_table");

    List<UpdateStatement> statement = new ArrayList<UpdateStatement>();
    statement.add(new UpdateStatement("update im_id_table set nextId=(nextId+1) where nextId = ?", nextId));
    int affectedRows = executeUpdates(statement, null);
    if (affectedRows == 0) {
      throw new DaoException("error.concurrentError");
    }
    LOGGER.debug("returning id: " + nextId);
    return nextId;
  }
}

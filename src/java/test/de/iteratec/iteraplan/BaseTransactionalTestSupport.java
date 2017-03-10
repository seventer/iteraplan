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
package de.iteratec.iteraplan;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.db.DataTypeFactoryHelper;
import de.iteratec.iteraplan.db.SqlScriptExecutor;
import de.iteratec.iteraplan.xmi.InitialDataDumper;


/**
 * A base class used for integration tests.
 */
@ContextConfiguration({ "/applicationContext.xml", "/applicationContext-dao.xml", "/applicationContext-datasource.xml",
    "/applicationContext-ext.xml", "/applicationContext-gui.xml", "/applicationContext-staticquery.xml", "/applicationContext-test.xml",
    "/applicationContext-rest.xml" })
@ActiveProfiles("de.iteratec.iteraplan.testing")
public abstract class BaseTransactionalTestSupport extends AbstractJUnit4SpringContextTests {
  private static final Logger        LOGGER                = Logger.getIteraplanLogger(BaseTransactionalTestSupport.class);

  private static final String        SETUP_SQL_FILE        = "/schema-export.sql";
  private static final List<String>  SQL_STATEMENTS        = SqlScriptExecutor.readSqlScript(BaseTransactionalTestSupport.class
                                                               .getResourceAsStream(SETUP_SQL_FILE));
  private static final IDataSet      INITIAL_DATA          = initDataSet();

  @Autowired
  private SessionFactory             sessionFactory;

  /** The transaction manager to use */
  @Autowired
  private PlatformTransactionManager transactionManager;

  /** TransactionStatus for this test. Typical subclasses won't need to use it. */
  private TransactionStatus          transactionStatus;
  /**
   * Transaction definition used by this test class: by default, a plain
   * DefaultTransactionDefinition. Subclasses can change this to cause
   * different behavior.
   */
  private TransactionDefinition      transactionDefinition = new DefaultTransactionDefinition();
  /** Should we commit the current transaction? */
  private boolean                    complete              = false;
  /** Number of transactions started */
  private int                        transactionsStarted   = 0;
  /** Should we roll back by default? */
  private boolean                    defaultRollback       = true;

  private boolean                    insertData            = true;

  /**
   * Inits the data set.
   *
   * @return the data set
   */
  private static IDataSet initDataSet() {
    FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
    try {
      return builder.build(new FileInputStream(InitialDataDumper.INITIAL_DATA_XML_FILE));
    } catch (DataSetException e) {
      throw new IllegalStateException(e);
    } catch (FileNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  public BaseTransactionalTestSupport() {
    super();
  }

  @Before
  public void setUp() {
    beginTransaction();
    SqlScriptExecutor.executeSqlStatements(SQL_STATEMENTS, getHibernateTemplate());

    if (insertData) {
      final Work work = new InitialDataImportWork();
      sessionFactory.getCurrentSession().doWork(work);
    }
  }

  @After
  public void onTearDown() {
    if (this.transactionStatus != null && !this.transactionStatus.isCompleted()) {
      endTransaction();
    }
  }

  protected HibernateTemplate getHibernateTemplate() {
    return new HibernateTemplate((SessionFactory) applicationContext.getBean("sessionFactory"));
  }

  /**
   * A {@link Work} for importing initial data.
   */
  private static final class InitialDataImportWork implements Work {
    public void execute(Connection jdbcConnection) throws SQLException {
      try {
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
        IDataTypeFactory dataTypeFactory = DataTypeFactoryHelper.determineDataTypeFactory(jdbcConnection);

        DatabaseConfig config = connection.getConfig();
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);

        DatabaseOperation.CLEAN_INSERT.execute(connection, INITIAL_DATA);
      } catch (DatabaseUnitException e) {
        LOGGER.error("Could not restore initial data", e);
        throw new IllegalStateException(e);
      }
    }
  }

  public void beginTransaction() {
    startNewTransaction();
  }

  public void commit() {
    setComplete();
    endTransaction();
  }

  public void rollback() {
    endTransaction();
  }

  private void startNewTransaction() {
    if (this.transactionStatus != null) {
      throw new IllegalStateException("Cannot start new transaction without ending existing transaction: "
          + "Invoke endTransaction() before startNewTransaction()");
    }
    if (this.transactionManager == null) {
      throw new IllegalStateException("No transaction manager set");
    }

    this.transactionStatus = this.transactionManager.getTransaction(this.transactionDefinition);
    ++this.transactionsStarted;
    this.complete = !this.isRollback();

    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Began transaction (" + this.transactionsStarted + "): transaction manager [" + this.transactionManager + "]; rollback ["
          + this.isRollback() + "].");
    }
  }

  /**
   * Determines whether or not to rollback transactions for the current test.
   * <p>The default implementation delegates to {@link #isDefaultRollback()}.
   * Subclasses can override as necessary.
   */
  protected boolean isRollback() {
    return this.defaultRollback;
  }

  /**
   * Cause the transaction to commit for this test method, even if the test
   * method is configured to {@link #isRollback() rollback}.
   * @throws IllegalStateException if the operation cannot be set to complete
   * as no transaction manager was provided
   */
  private void setComplete() {
    if (this.transactionManager == null) {
      throw new IllegalStateException("No transaction manager set");
    }
    this.complete = true;
  }

  /**
   * Immediately force a commit or rollback of the transaction, according to
   * the <code>complete</code> and {@link #isRollback() rollback} flags.
   * <p>Can be used to explicitly let the transaction end early, for example to
   * check whether lazy associations of persistent objects work outside of a
   * transaction (that is, have been initialized properly).
   * @see #setComplete()
   */
  private void endTransaction() {
    final boolean commit = this.complete || !isRollback();
    if (this.transactionStatus != null) {
      try {
        if (commit) {
          this.transactionManager.commit(this.transactionStatus);
          this.logger.debug("Committed transaction after execution of test.");
        }
        else {
          this.transactionManager.rollback(this.transactionStatus);
          this.logger.debug("Rolled back transaction after execution of test.");
        }
      } finally {
        this.transactionStatus = null;
      }
    }
  }

  public void setInsertDataOnSetUp(boolean insertData) {
    this.insertData = insertData;
  }
}
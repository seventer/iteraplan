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
package de.iteratec.iteraplan.setup;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.search.event.FullTextIndexEventListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import de.iteratec.iteraplan.common.Logger;


/**
 * Creates the spring application context based on the XML files returned by
 * {@link #getSpringConfigFiles()}. By default it enables the Spring profile
 * {@code de.iteratec.iteraplan.testing}.
 * <p>
 * Furthermore allows to manually set transaction boundaries, using {@link #startTransaction()} to
 * start a transaction, {@link #setCommitting()} to change the default behaviour from "rollback" to
 * "commit" (i.e. the transaction will be commited) and {@link #commitOrRollbackTransaction()} to
 * end transaction (default: rollback, commit resp. if previously set)</p>
 * 
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 */
public final class LocalApplicationContextUtil {

  private static final Logger                   LOGGER                = Logger.getIteraplanLogger(LocalApplicationContextUtil.class);

  private static PlatformTransactionManager     transactionManager;
  private static boolean                        defaultRollback       = true;
  private static boolean                        complete              = false;
  private static int                            transactionsStarted   = 0;
  private static TransactionDefinition          transactionDefinition = new DefaultTransactionDefinition();
  private static TransactionStatus              transactionStatus;
  private static GenericXmlApplicationContext   applicationContext    = null;
  private static HibernateTemplate              hibernateTemplate     = null;

  @SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
  public static synchronized ConfigurableApplicationContext getApplicationContext() {
    if (applicationContext == null) {
      applicationContext = new GenericXmlApplicationContext();

      // use default profile instead of active profile so that callers are still able to override the active profile, e.g. by command line sysproperty
      applicationContext.getEnvironment().setDefaultProfiles("de.iteratec.iteraplan.testing");

      applicationContext.load(getSpringConfigFiles());
      applicationContext.refresh();
    }
    if (transactionManager == null) {
      transactionManager = applicationContext.getBean(PlatformTransactionManager.class);
    }

    FullTextIndexEventListener searchlistener = applicationContext.getBean(FullTextIndexEventListener.class);

    // initialize the searchlistener with an empty configuration, thereby disabling indexing for the
    // data-creation tasks
    AnnotationConfiguration hibernateConfiguration = new AnnotationConfiguration();
    searchlistener.initialize(hibernateConfiguration);

    return applicationContext;
  }

  public static String[] getSpringConfigFiles() {
    return new String[] { "applicationContext.xml", "applicationContext-dao.xml", "applicationContext-datasource.xml", "applicationContext-ext.xml",
        "applicationContext-gui.xml", "applicationContext-staticquery.xml" };
  }

  private LocalApplicationContextUtil() {
    // only static methods
  }

  public static void startTransaction() throws TransactionException {
    complete = !defaultRollback;

    if (transactionManager == null) {
      LOGGER.info("No transaction manager set: test will NOT run within a transaction");
    }
    else if (transactionDefinition == null) {
      LOGGER.info("No transaction definition set: test will NOT run within a transaction");
    }
    else {
      try {
        startNewTransaction();
      } catch (TransactionException ex) {
        endTransaction();
        throw ex;
      }
    }
  }

  /**
   * Start a new transaction. Only call this method if {@link #endTransaction()} has been called.
   * {@link #setCommitting()} can be used again in the new transaction. The fate of the new
   * transaction, by default, will be the usual rollback.
   * 
   * @throws TransactionException
   *           if starting the transaction failed
   */
  private static void startNewTransaction() throws TransactionException {
    if (transactionStatus != null) {
      throw new IllegalStateException("Cannot start new transaction without ending existing transaction: "
          + "Invoke endTransaction() before startNewTransaction()");
    }
    if (transactionManager == null) {
      throw new IllegalStateException("No transaction manager set");
    }

    transactionStatus = transactionManager.getTransaction(transactionDefinition);
    ++transactionsStarted;
    complete = !defaultRollback;

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Began transaction (" + transactionsStarted + "): transaction manager [" + transactionManager + "]; default rollback = "
          + defaultRollback);
    }
  }

  /**
   * Immediately force a commit or rollback of the transaction, according to the complete flag.
   * <p>
   * Can be used to explicitly let the transaction end early, for example to check whether lazy
   * associations of persistent objects work outside of a transaction (that is, have been
   * initialized properly).
   * 
   * @see #setCommitting()
   */
  @SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
  public static synchronized void endTransaction() {
    if (transactionStatus != null) {
      try {
        if (!complete) {
          transactionManager.rollback(transactionStatus);
          LOGGER.info("Rolled back transaction after test execution");
        }
        else {
          transactionManager.commit(transactionStatus);
          LOGGER.info("Committed transaction after test execution");
        }
      } finally {
        transactionStatus = null;
      }
    }
  }

  /**
   * This implementation ends the transaction after test execution.
   * 
   * @throws Exception
   *           simply let any exception propagate to assure rollback
   */
  public static void commitOrRollbackTransaction() {
    // Call onTearDownInTransaction and end transaction if the transaction is still active.
    if (transactionStatus != null && !transactionStatus.isCompleted()) {
      endTransaction();

    }
  }

  /**
   * Cause the transaction to commit for this test method, even if default is set to rollback.
   * 
   * @throws IllegalStateException
   *           if the operation cannot be set to complete as no transaction manager was provided
   */
  public static void setCommitting() {
    if (transactionManager == null) {
      throw new IllegalStateException("No transaction manager set");
    }
    complete = true;
  }

  /**
   * Calls both {@link #setCommitting()} and {@link #startNewTransaction()}
   */
  public static void startCommittingTransaction() {
    startNewTransaction();
    setCommitting();

  }

  /**
   * Returns a HibernateTemplate bound to the current sessionfactory
   * 
   * @return The session factory
   */
  @SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
  public static synchronized HibernateTemplate getHibernateTemplate() {
    if (hibernateTemplate == null) {
      hibernateTemplate = new HibernateTemplate(getApplicationContext().getBean(SessionFactory.class));
    }
    return hibernateTemplate;
  }
}

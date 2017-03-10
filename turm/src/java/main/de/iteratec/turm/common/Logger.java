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

/**
 * Standard logger used by every class which want to log something.
 * 
 * Methods to log messages in different log levels and to check if the actual
 * log level is at least debug or info. log levels are defined in the log4j.properties.
 */

public class Logger {

  private org.apache.log4j.Logger log4jLogger;

  private Logger(Class<?> clazz) {
    log4jLogger = org.apache.log4j.Logger.getLogger(clazz);
  }

  public static Logger getLogger(Class<?> clazz) {
    return new Logger(clazz);
  }

  /**
   * Checks if the log level for the actual package is at least 'debug'. log levels are defined in
   * the log4j.properties.
   * 
   * @return true if log level for the actual package is at least 'debug'.
   */
  public boolean isDebugEnabled() {
    return this.log4jLogger.isDebugEnabled();
  }

  /**
   * Checks if the log level for the actual package is at least 'info'. log levels are defined in
   * the log4j.properties.
   * 
   * @return true if log level for the actual package is at least 'info'.
   */
  public boolean isInfoEnabled() {
    return this.log4jLogger.isInfoEnabled();
  }

  /**
   * Logs a message with the severity level 'debug'.
   * 
   * @param msg the message to log
   */
  public void debug(String msg) {
    this.log4jLogger.debug(msg);
  }

  /**
   * Logs a message with the severity level 'info'.
   * 
   * @param msg the message to log
   */
  public void info(String msg) {
    this.log4jLogger.info(msg);
  }

  /**
   * Logs a message with the severity level 'info'.
   * 
   * @param msg the message to log
   * @param t The error that was thrown.
   */
  public void info(String msg, Throwable t) {
    this.log4jLogger.info(msg, t);
  }

  /**
   * Logs a message with the severity level 'warn'.
   * 
   * @param msg the message to log
   */
  public void warn(String msg) {
    this.log4jLogger.warn(msg);
  }

  /**
   * Logs a message with the severity level 'error'.
   * 
   * @param msg the message to log
   */
  public void error(String msg) {
    this.log4jLogger.error(msg);
  }

  /**
   * @param msg the message to log
   * @param e the exception to log
   */
  public void error(String msg, Throwable e) {
    this.log4jLogger.error(msg, e);
  }

  /**
   * Logs a message with the severity level 'fatal'.
   * 
   * @param msg the message to log
   */
  public void fatal(String msg) {
    this.log4jLogger.fatal(msg);
  }

  /**
   * Logs an Exception with the severity level 'debug'.
   * 
   * @param e the Exception to log
   */
  public void debug(Throwable e) {
    this.log4jLogger.debug(e.getMessage(), e);
  }

  /**
   * Logs an Exception with the severity level 'info'.
   * 
   * @param e the Exception to log
   */
  public void info(Throwable e) {
    this.log4jLogger.info(e.getMessage(), e);
  }

  /**
   * Logs an Exception with the severity level 'warn'.
   * 
   * @param e the Exception to log
   */
  public void warn(Throwable e) {
    this.log4jLogger.warn(e.getMessage(), e);
  }

  /**
   * Logs an Exception with the severity level 'error'.
   * 
   * @param e the Exception to log
   */
  public void error(Throwable e) {
    this.log4jLogger.error(e.getMessage(), e);
  }

  /**
   * Logs an Exception with the severity level 'fatal'.
   * 
   * @param e the Exception to log
   */
  public void fatal(Throwable e) {
    this.log4jLogger.fatal(e.getMessage(), e);
  }

  public void log(int category, String msg) {
    if (category == 5) {
      debug(msg);
    }
    else if (category == 4) {
      info(msg);
    }
    else if (category == 3) {
      warn(msg);
    }
    else if (category == 2) {
      error(msg);
    }
    else if (category == 1) {
      fatal(msg);
    }
  }

  public void log(int category, Throwable throwable) {
    if (category == 5) {
      debug(throwable);
    }
    else if (category == 4) {
      info(throwable);
    }
    else if (category == 3) {
      warn(throwable);
    }
    else if (category == 2) {
      error(throwable);
    }
    else if (category == 1) {
      fatal(throwable);
    }
  }

}
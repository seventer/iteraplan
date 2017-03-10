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
package de.iteratec.iteraplan.presentation.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.LoggingCategory;


/**
 * Provides basic logging capabilities in the form of a jsp log tag.
 * 
 * @author afe
 */
public class IteratecLogTag extends TagSupport {

  private static Logger   logger           = Logger.getIteraplanLogger("iteratecLogTag");

  private String          message;

  private Throwable       exception;

  private boolean         logStacktrace    = false;

  private boolean         logRootException = false;

  private LoggingCategory category         = LoggingCategory.ERROR;

  public void setLogStacktrace(boolean logStacktrace) {
    this.logStacktrace = logStacktrace;
  }

  public void setLogRootException(boolean logRootException) {
    this.logRootException = logRootException;
  }

  public void setCategory(String category) {
    LoggingCategory loggingCat = LoggingCategory.getCategoryByString(category);
    if (loggingCat == null) {
      logger.info("No such category defined: " + category + " using error instead!");
    }
    else {
      this.category = loggingCat;
    }
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setException(Throwable exception) {
    this.exception = exception;
  }

  /**
   * Process the start tag.
   * 
   * @exception JspException
   *              if a JSP exception has occurred
   */
  @Override
  public int doStartTag() throws JspException {

    // first we output the message if it is set
    if (message != null) {
      logger.log(category, message);
    }

    // then the exception
    if (exception != null) {
      logException();
    }

    // Continue processing this page
    return SKIP_BODY;
  }

  /**
   * Logs the exception to the iteraplan logger, printing the stacktrace and the root exception if
   * configured
   * 
   * @param exception
   *          the exception object, must nut be null
   */
  private void logException() {
    logger.log(category, exception.toString());

    if (logStacktrace) {
      logger.log(category, "Stacktrace: ");
      StackTraceElement[] stack = exception.getStackTrace();
      for (int i = 0; i < stack.length; i++) {
        logger.log(category, stack[i].toString());
      }
    }

    if (logRootException) {
      logger.log(category, "Getting Root Cause of this exception: ");

      // recursively get the cause of this exception until the root exception is reached
      while (exception != null && !exception.equals(exception.getCause())) {
        // log each exception
        logger.log(category, exception.toString());
        exception = exception.getCause();
      }
    }
  }
}

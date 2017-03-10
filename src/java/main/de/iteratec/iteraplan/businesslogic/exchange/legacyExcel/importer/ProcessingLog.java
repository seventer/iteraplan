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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;


/**
 * Collects processing events that are of interest to the user. This class is meant to aggregate
 * event messages that give feedback to the user.
 */
public class ProcessingLog {

  public static enum Level {
    DEBUG, INFO, WARN, ERROR, FATAL
  }

  private static final Logger LOGGER = Logger.getIteraplanLogger(ProcessingLog.class);

  private final Date          startTime;
  private Level               level  = Level.DEBUG;
  private PrintWriter         logStream;

  public ProcessingLog() {
    startTime = new Date();
  }

  public ProcessingLog(Level level, PrintWriter logTarget) {
    this();
    this.level = level;
    this.logStream = logTarget;
  }

  public CharSequence getLogContents() {
    StringBuilder sb = new StringBuilder();
    sb.append("Processing started at ");
    DateFormat dateFormatter = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT, UserContext.getCurrentLocale());
    sb.append(dateFormatter.format(startTime));
    sb.append('\n');

    return sb;
  }

  public void error(String message, Object... params) {
    if (level.compareTo(Level.ERROR) > 0) {
      return;
    }
    LOGGER.error(message, params); // Also display instantly
    buildUserMessage(" [ERROR] ", message, params);
  }

  public void warn(String message, Object... params) {
    if (level.compareTo(Level.WARN) > 0) {
      return;
    }
    LOGGER.warn(message, params); // Also display instantly
    buildUserMessage(" [WARN ] ", message, params);
  }

  public void info(String message, Object... params) {
    if (level.compareTo(Level.WARN) > 0) {
      return;
    }
    LOGGER.info(message, params); // Also display instantly
    buildUserMessage(" [INFO ] ", message, params);
  }

  public void debug(String message, Object... params) {
    if (level.compareTo(Level.DEBUG) > 0) {
      return;
    }
    LOGGER.debug(message, params); // Also display instantly
    buildUserMessage(" [DEBUG] ", message, params);
  }

  public void insertDummyRow(String message, Object... params){
    buildUserMessage("", message, params);
  }
  
  private void buildUserMessage(String messagePrefix, String message, Object... params) {
    logStream.append(messagePrefix);

    for (Object param : params) {
      if (param instanceof Exception) {
        Exception e = (Exception) param;
        LOGGER.error("An exception occured during processing: " + message, e);
        break;
      }
    }
    MessageFormat mf = new MessageFormat(message);
    String completeMessage = mf.format(params);

    logStream.append(completeMessage);
    logStream.append('\n');
  }
}

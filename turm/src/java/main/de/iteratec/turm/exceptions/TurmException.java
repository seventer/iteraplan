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
package de.iteratec.turm.exceptions;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * General Turm exception.
 * 
 * The message has to be a ressource key which can be looked up for internationalisation.
 * In future implementations this should be replaced with a proper override of the
 * {@link Throwable#getLocalizedMessage()} method.
 */
public class TurmException extends Exception {

  private static final long serialVersionUID = 7882802481802648852L;

  /** Up to four optional parameters that are filled in the placeholders in the message. */
  Object[] messageParams = new Object[0];

  public TurmException() {
    // empty
  }

  public TurmException(String message, Exception e) {
    super(message, e);
  }

  public TurmException(String message) {
    super(message);
  }

  public TurmException(String message, Object... messageParams) {
    super(message);
    this.messageParams = messageParams;
  }

  public String getStackTracePrint() {
    StringWriter sw = null;
    PrintWriter pw = null;
    String result = null;
    try {
      sw = new StringWriter();
      pw = new PrintWriter(sw, true);
      printStackTrace(pw);
      pw.flush();
      sw.flush();
      result = sw.toString();
    } finally {
      try {
        if (sw != null) {
          sw.close();
        }
      } catch (IOException e) {
        // ignore
      }
      if (pw != null) {
        pw.close();
      }
    }
    return result;
  }

  public Object[] getMessageParams() {
    return messageParams;
  }
}

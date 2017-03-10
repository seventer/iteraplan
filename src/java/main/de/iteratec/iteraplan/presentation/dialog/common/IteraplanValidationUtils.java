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
package de.iteratec.iteraplan.presentation.dialog.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;


public final class IteraplanValidationUtils {

  private IteraplanValidationUtils() {
    // private constructor, to avoid instantiation of a static methods-only class
  }

  /**
   * Replace keys with localized messages
   * 
   * @param errorArgs
   * @return Object[] containing Strings with localized messages, or input key if no message for the
   *         key was found
   */
  public static Object[] getLocalizedArgs(String[] errorArgs) {
    Locale locale = UserContext.getCurrentLocale();

    List<String> localizedArgs = new ArrayList<String>();
    for (String arg : Arrays.asList(errorArgs)) {
      String localizedMessage = MessageAccess.getStringOrNull(arg, locale);
      // if no localized message was found, add the argument itself
      if (localizedMessage == null) {
        localizedArgs.add(arg);
      }
      else {
        localizedArgs.add(localizedMessage);
      }
    }
    return localizedArgs.toArray();
  }

  /**
   * Replace the errorArg with the localized message
   * 
   * @param errorArg
   * @return Object[] containing Strings with localized messages, or input key if no message for the
   *         key was found
   */
  public static Object[] getLocalizedArgs(String errorArg) {
    return getLocalizedArgs(new String[] { errorArg });
  }

  public static Object[] getLocalizedArgsWithSpanTags(String[] errorArgs, String spanClass) {
    Object[] res = getLocalizedArgs(errorArgs);
    for (int i = 0; i<res.length; i++) {
      res[i] = "\"<span class=\"" + spanClass + "\">" + res[i] + "</span>";
    }
    
    return res;
  }

  public static Object[] getLocalizedArgsWithSpanTags(String errorArg, String spanClass) {
    return getLocalizedArgsWithSpanTags(new String[] { errorArg }, spanClass);
  }

}

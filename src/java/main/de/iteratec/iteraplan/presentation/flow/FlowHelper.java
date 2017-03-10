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
package de.iteratec.iteraplan.presentation.flow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.iteratec.iteraplan.presentation.SessionConstants;


public final class FlowHelper {

  public static final Pattern FLOWEXKEY_PATTERN = Pattern.compile("/" + SessionConstants.FLOW_EXECUTION_KEY + "/([^/]*)/?");

  private FlowHelper() {
    // private constructor, to avoid instantiation of a static methods-only class
  }

  /**
   * <p>Will return the value for the key, encoded within the request-URI</p>
   * <p>
   * e.g. for key=foo
   * <ul>
   *    <li>/flow/foo/bar -> bar</li>
   *    <li>/flow/foo/bar/ -> bar</li>
   *    <li>/flow/foo/bar/baz -> bar</li>
   * </ul>
   * @param requestURI
   * @return the value of the given key, or null if none could be found
   */
  public static String retrieveRestValueFromRequestURI(String requestURI, String key) {
    Pattern pattern = Pattern.compile("/" + key + "/([^/]*)/?");
    Matcher matcher = pattern.matcher(requestURI);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  /**
   * <p>
   * extracts the requestURI from the request and then removes the key-value pair from it
   * </p>
   * <p>
   * replaces the found "/key/123" with "/"
   * <ul>
   * <li>foo/key/123/ -> foo/</li>
   * <li>foo/key/123 -> foo/</li>
   * <li>foo/key/123/bar -> foo/bar</li>
   * <li>foo/key/123/bar/baz/ -> foo/bar/baz/</li>
   * </ul>
   * 
   * @param requestURI
   * @return the requestURI of the request without the key and value
   */
  public static String removeKeyValueFromRequestURI(String requestURI, String key) {
    Pattern pattern = Pattern.compile("/" + key + "/([^/]*)/?");
    Matcher matcher = pattern.matcher(requestURI);
    if (matcher.find()) {
      /** replaces the found "/execution/123" with "/"
       * foo/execution/123/ -> foo/
       * foo/execution/123 -> foo/
       * foo/execution/123/bar -> foo/bar
       * foo/execution/123/bar/baz/ -> foo/bar/baz/ 
       */
      return matcher.replaceAll("/");
    }
    return requestURI;
  }

  /**
   * returns the first number within the pathInfo string (REST). If none is found returns null
   * 
   * @param pathInfo the pathInfo String obtained from request.getPathInfo
   * @return the first number within the pathInfo string or null, if none is present
   */
  public static String getId(String pathInfo) {
    if (pathInfo != null) {
      Pattern pattern = Pattern.compile("(\\d+)");
      Matcher matcher = pattern.matcher(pathInfo);
      if (matcher.find()) {
        return matcher.group();
      }
    }
    return null;
  }

  /**
   * Return the flowId from the pathInfo
   * <p>
   * Expected examples of PathInfo are: 
   * <ul>
   * <li>"/informationsystem/20/"</li>
   * <li>"/graphicalreporting/landscapediagram/"</li>
   * </ul>
   * Illegal examples are:
   * <ul>
   * <li>"/informationsystem/foo/20"</li>
   * <li>"/graphicalreporting/landscapediagram/foo"</li>
   * </ul>
   * </p>
   * <p>
   * They must either contain the flowID-basename (without start) followed by a number or consist of
   * only the flowID-basename. Also see examples above.
   * </p>
   * 
   * @param pathInfo
   * @return the id of the flow
   */
  public static String getFlowId(String pathInfo) {
    // define the end of the string part that is relevant for the flowId
    int end = pathInfo.length();

    // if the string contains an id, the flowid is the part before this id
    String id = getId(pathInfo);
    if (id != null) {
      end = pathInfo.indexOf(id);
    }

    // if pathInfo starts with "/", start at index 1 to skip it
    int start = pathInfo.charAt(0) == '/' ? 1 : 0;
    String idWithoutStart = pathInfo.substring(start, end);
    if (!idWithoutStart.endsWith("/")) {
      idWithoutStart += "/";
    }
    return idWithoutStart + "start";
  }

}

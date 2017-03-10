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
package de.iteratec.iteraplan.presentation;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.iteratec.iteraplan.common.Logger;


/**
 * This interceptor is responsible for logging parameters on incoming requests.
 * It is intended for debugging purposes and will do nothing if its log level is
 * lower than DEBUG.
 *
 */
@Service
public class LoggingInterceptor extends HandlerInterceptorAdapter {
  private static final Logger LOGGER = Logger.getIteraplanLogger(LoggingInterceptor.class);

  /**
   * Logs the parameters that are contained in the current request, with a readable output.
   * 
   * @param req
   *          The current request
   */
  @Override
  @SuppressWarnings({ "unchecked" })
  public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws ServletException {

    if (!LOGGER.isDebugEnabled()) {
      return true;
    }

    Map<String, String[]> map = req.getParameterMap();
    LOGGER.debug("*********** Parameters in request *****************");
    for (Iterator<Entry<String, String[]>> it = map.entrySet().iterator(); it.hasNext();) {
      Entry<String, String[]> entry = it.next();
      StringBuffer buf = new StringBuffer();
      buf.append(entry.getKey()).append(" = ");
      String[] values = entry.getValue();

      for (int i = 0; i < values.length; i++) {
        if (i > 0) {
          buf.append(", ");
        }
        buf.append(values[i]);
      }
      LOGGER.debug(buf.toString());
    }
    LOGGER.debug("*********** Headers in request *****************");
    Enumeration<String> headerNames = req.getHeaderNames();
    for (String headerName = headerNames.nextElement(); headerNames.hasMoreElements() ; headerName = headerNames.nextElement()) {
      String headerValue = req.getHeader(headerName);
      LOGGER.debug("{0} = {1}", headerName, headerValue);
    }
    LOGGER.debug("***************************************************");

    return true;
  }

}

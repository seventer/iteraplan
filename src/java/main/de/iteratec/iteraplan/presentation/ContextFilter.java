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

import static de.iteratec.iteraplan.presentation.SessionConstants.ELASTIC_MI_CONTEXT;
import static de.iteratec.iteraplan.presentation.SessionConstants.GUI_CONTEXT;
import static de.iteratec.iteraplan.presentation.SessionConstants.USER_CONTEXT;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;


/**
 * This Filter is responsible for creating various contexts on the current thread
 * of execution. After request processing has completed, it will remove those contexts
 * from the Thread again in order to avoid memory leaks.<br>
 * This includes:
 * <ul>
 * <li>setting gui context.</li>
 * <li>setting user context.</li>
 * </ul>
 */
public final class ContextFilter implements Filter {

  private final Logger logger = Logger.getIteraplanLogger(getClass());

  public void init(FilterConfig filterConfig) throws ServletException {
    // nothing to do, but required by the interface
  }

  public void destroy() {
    // nothing to do, but required by the interface
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    if (request instanceof HttpServletRequest) {
      HttpServletRequest req = (HttpServletRequest) request;
      HttpSession session = req.getSession();

      // Prepare the user's context. The locale cannot be (easily) set here,
      // because we're still outside of Spring MVC
      UserContext userContext = getUserContext(session);
      if (userContext != null) {
        userContext.setSessionId(session.getId());
        UserContext.setCurrentUserContext(userContext);
        logger.debug("Set user context from session to current thread.");
      }

      // Prepare the GUI context.
      GuiContext guiContext = getGuiContext(session);
      if (guiContext != null) {
        GuiContext.setCurrentRequest(req);
        GuiContext.setCurrentGuiContext(guiContext);
        logger.debug("Set GUI context from session to current thread.");
      }

      ElasticMiContext elasticMiContext = getElasticMiContext(session);
      if (elasticMiContext != null) {
        ElasticMiContext.setCurrentContext(elasticMiContext);
        logger.debug("Set elasticMI Context from session to current thread.");
      }

    }

    try {
      // proceed along the chain
      chain.doFilter(request, response);
      // request processing has completed
    } finally {

      // cleanup the contexts, in order to avoid memory leaks in the ThreadLocals
      GuiContext.detachCurrentGuiContext();
      GuiContext.detachCurrentRequest();
      UserContext.detachCurrentUserContext();
      ElasticMiContext.detachCurrentContext();
    }
  }

  private UserContext getUserContext(final HttpSession session) {
    return (UserContext) session.getAttribute(USER_CONTEXT);
  }

  /**
   * Gets the current instance of {@link GuiContext} from the session.
   * 
   * @param session
   *          The current session.
   */
  private GuiContext getGuiContext(final HttpSession session) {
    return (GuiContext) session.getAttribute(GUI_CONTEXT);
  }

  private ElasticMiContext getElasticMiContext(final HttpSession session) {
    return (ElasticMiContext) session.getAttribute(ELASTIC_MI_CONTEXT);
  }
}
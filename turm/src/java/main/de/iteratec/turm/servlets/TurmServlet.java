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
package de.iteratec.turm.servlets;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import de.iteratec.turm.common.Logger;
import de.iteratec.turm.dao.RoleDao;
import de.iteratec.turm.dao.UserDao;
import de.iteratec.turm.exceptions.DaoException;
import de.iteratec.turm.exceptions.TurmException;
import de.iteratec.turm.messages.TurmMessage;
import de.iteratec.turm.model.IdEntity;


/**
 * Base class for all Turm servlets.
 * 
 * It provides the following functionality:
 * <ul>
 *  <li>The locale of the user is determined and stored for future access.
 *  <li>Errors are logged, stored in the request and converted to {@link TurmException}s if necessary.
 *  <li>After a get or a post request, the request is always forwarded to the JSP defined in {@link #getJsp()}
 *  <li>It provides several common methods for subclasses.
 * </ul>
 */
public abstract class TurmServlet extends HttpServlet {

  private static final long serialVersionUID = -6366990616826366380L;

  private static final Logger LOGGER             = Logger.getLogger(TurmServlet.class);

  /** TurmExceptions that occurred will be saved as an request attribute under this key. */
  private static final String ERROR_PARAM_KEY    = "turmExceptions";

  /** TurmMessages that are to be displayed will be saved as an request attribute under this key. */
  private static final String MESSAGE_PARAM_KEY  = "turmMessages";

  /** The current Locale will be saved in the session under this key. */
  private static final String CURRENT_LOCALE_KEY = "turmLocale";

  /** The URI of the servlet will be stored as a request attribute under this key.
   *  This is used by common JSP tiles that post data to the currently active servlet. */
  private static final String SERVLET_URI        = "servletURI";

  /** The current locale. */
  private Locale              currentLocale;

  @Override
  protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    LOGGER.debug("============================== GET REQUEST ==============================");
    logRequestParameters(request);
    setAndStoreCurrentLocale(request);
    storeServletPath(request);
    try {
      doTurmGet(request);
    } catch (TurmException e) {
      saveErrors(e, request);
    } catch (Exception e) {
      saveErrors(new TurmException("error.internalError", e), request);
    }
    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(getJsp());
    dispatcher.forward(request, response);
  }

  @Override
  protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    LOGGER.debug("============================== POST REQUEST ==============================");
    logRequestParameters(request);
    setAndStoreCurrentLocale(request);
    storeServletPath(request);
    try {
      doTurmPost(request);
    } catch (TurmException e) {
      saveErrors(e, request);
    } catch (Exception e) {
      saveErrors(new TurmException("error.internalError", e), request);
    }
    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(getJsp());
    dispatcher.forward(request, response);
  }

  /**
   * Process the current GET request and execute the necessary actions.
   * 
   * @param request The current request.
   * @throws TurmException If there occurred an error while processing the request.
   */
  protected abstract void doTurmGet(HttpServletRequest request) throws TurmException;

  /**
   * Process the current POST request and execute the necessary actions
   * 
   * @param request The current request.
   * @throws TurmException If there occurred an error while processing the request.
   */
  protected abstract void doTurmPost(HttpServletRequest request) throws TurmException;

  /**
   * @return The JSP the Servlet will forward to when it's done, no matter if an error
   *         occured or not.
   */
  protected abstract String getJsp();

  /**
   * Store the URI of this servlet in a request attribute, so that JSPs have
   * access to it.
   * 
   * @param request The current Serlvet Request
   */
  private void storeServletPath(HttpServletRequest request) {
    request.setAttribute(SERVLET_URI, request.getRequestURI());
  }

  /**
   * Log the parameters contained in the given request for
   * debugging purposes.
   * 
   * @param request The current Serlvet Request
   */
  @SuppressWarnings("unchecked")
  private void logRequestParameters(HttpServletRequest request) {
    if (!LOGGER.isDebugEnabled()) {
      return;
    }
    LOGGER.debug("-------------- Parameters in request --------------");
    Map<String, String[]> parameterMap = request.getParameterMap();
    for (Iterator<Map.Entry<String, String[]>> it = parameterMap.entrySet().iterator(); it
        .hasNext();) {
      Map.Entry<String, String[]> entry = it.next();
      LOGGER.debug(entry.getKey() + ": ");
      String[] value = entry.getValue();
      String parametersValues = "\t";
      for (int i = 0; i < value.length; i++) {
        if (i > 0) {
          parametersValues += ", ";
        }
        parametersValues += value[i];
      }
      LOGGER.debug(parametersValues);
    }
    LOGGER.debug("---------------------------------------------------");
  }

  /**
   * Store a TurmException in a request attribute, so that it can be rendered
   * by a JSP.
   * 
   * @param e The TurmException to store.
   * @param request The current Serlvet Request
   */
  @SuppressWarnings("unchecked")
  protected final void saveErrors(TurmException e, HttpServletRequest request) {
    LOGGER.info("An error occurred", e);
    List<TurmException> errors = (List<TurmException>) request.getAttribute(ERROR_PARAM_KEY);
    if (errors == null) {
      errors = new ArrayList<TurmException>();
    }
    errors.add(e);
    request.setAttribute(ERROR_PARAM_KEY, errors);
  }

  /**
   * Store a TurmMessage in a request attribute, so that it can be rendered
   * by a JSP.
   * 
   * @param message The TurmMessage to store.
   * @param request The current Serlvet Request
   */
  @SuppressWarnings("unchecked")
  protected final void saveMessages(TurmMessage message, HttpServletRequest request) {
    LOGGER.info("Message to be displayed: " + message.getMessageKey());
    List<TurmMessage> messages = (List<TurmMessage>) request.getAttribute(MESSAGE_PARAM_KEY);
    if (messages == null) {
      messages = new ArrayList<TurmMessage>();
    }
    messages.add(message);
    request.setAttribute(MESSAGE_PARAM_KEY, messages);
  }

  /**
   * Parameters that are contained in the current request are used to fill
   * a given Java Bean. This is similar to the way Struts fills its ActionForms.
   * 
   * @param request The current Serlvet Request
   * @param bean A JavaBean that is to be populated.
   * @throws TurmException If the JavaBean could not be populated.
   */
  @SuppressWarnings("unchecked")
  protected final void populate(HttpServletRequest request, Object bean) throws TurmException {
    HashMap<String, String[]> map = new HashMap<String, String[]>();
    Enumeration<String> names = request.getParameterNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement();
      map.put(name, request.getParameterValues(name));
    }
    try {
      BeanUtils.populate(bean, map);
    } catch (IllegalAccessException e) {
      throw new TurmException("error.internal", e);
    } catch (InvocationTargetException e) {
      throw new TurmException("error.internal", e);
    }
  }

  /**
   * Determines the current Locale, stores it in the session and returns it.
   * 
   * It first checks, if a request parameter contains the current locale. This
   * means the user has selected a different locale. Otherwise, it tries to
   * retrieve the locale from the session. If that doesn't exist, the current
   * locale is taken from the request itself.
   * 
   * @param request The current Serlvet Request
   * @return The current Locale as set by the user, or as stored in the session.
   */
  private Locale setAndStoreCurrentLocale(HttpServletRequest request) {
    HttpSession session = request.getSession();
    Locale currentLocaleToSet;
    if (request.getParameter(CURRENT_LOCALE_KEY) != null) {
      String parameterLocale = request.getParameter(CURRENT_LOCALE_KEY);
      if (parameterLocale.equals("de")) {
        currentLocaleToSet = Locale.GERMAN;
      }
      else {
        currentLocaleToSet = Locale.ENGLISH;
      }
      LOGGER.debug("Locale created from HTTP parameter: " + currentLocaleToSet);
    }
    else if (session.getAttribute(CURRENT_LOCALE_KEY) != null) {
      currentLocaleToSet = (Locale) session.getAttribute(CURRENT_LOCALE_KEY);
      LOGGER.debug("Locale loaded from session: " + currentLocaleToSet);
    }
    else {
      currentLocaleToSet = request.getLocale();
      if (currentLocaleToSet.getLanguage().toUpperCase().matches(".*DE.*")) {
        currentLocaleToSet = Locale.GERMAN;
      }
      else {
        currentLocaleToSet = Locale.ENGLISH;
      }
      LOGGER.debug("Locale loaded from HTTP request header. Language is: " + currentLocaleToSet.getLanguage());
    }
    this.currentLocale = currentLocaleToSet;
    session.setAttribute(CURRENT_LOCALE_KEY, currentLocale);
    return currentLocale;
  }

  /**
   * @return The current Locale
   */
  protected final Locale getCurrentLocale() {
    return this.currentLocale;
  }

  /**
   * Generic method to remove a list of IdEntity from another list of IdEntity.
   * 
   * @param <T> The type must extend IdEntity
   * @param entitiesToSortOut The IdEntity list to remove.
   * @param allEntities The IdEntity list from which elements are to be removed.
   * @return The allEntities list minus the entitiesToSortOut list.
   */
  protected final <T extends IdEntity> List<T> sortOutIdEntities(List<T> entitiesToSortOut,
      List<T> allEntities) {
    Set<Number> ids = new HashSet<Number>();
    for (Iterator<T> it = entitiesToSortOut.iterator(); it.hasNext();) {
      T entity = it.next();
      ids.add(entity.getId());
    }
    List<T> result = new ArrayList<T>(allEntities);
    for (Iterator<T> it = result.iterator(); it.hasNext();) {
      T entity = it.next();
      if (ids.contains(entity.getId())) {
        it.remove();
      }
    }
    return result;
  }

  /**
   * @return
   * @throws DaoException
   */
  protected UserDao getUserDao() throws DaoException {
    WebApplicationContext springCtx = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
    return springCtx.getBean(UserDao.class);
  }

  /**
   * @return
   * @throws DaoException
   */
  protected RoleDao getRoleDao() throws DaoException {
    WebApplicationContext springCtx = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
    return springCtx.getBean(RoleDao.class);
  }

}

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
package de.iteratec.iteraplan.presentation.dialog;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.ui.context.Theme;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.SessionConstants;
import de.iteratec.iteraplan.presentation.memory.DialogMemory;
import de.iteratec.iteraplan.presentation.problemreports.IteraplanProblemReport;


public abstract class GuiController {

  private static final Logger LOGGER = Logger.getIteraplanLogger(GuiController.class);

  protected abstract String getDialogName();

  protected void init(ModelMap model, HttpSession session, HttpServletRequest request) {
    model.addAttribute("componentMode", "READ");

    // Store application URL in model map
    String applicationURL = URLBuilder.getApplicationURL(request);
    if (applicationURL != null) {
      model.addAttribute(SessionConstants.APPLICATION_URL_LABEL, StringUtils.removeEnd(applicationURL, "/"));
    }
  }

  /**
   * Updates the GUI context, setting the currently active dialog name and storing the dialog memory
   * to the GUI context.
   * <p>
   * Shall be invoked by <b>every</b> request handling method, so that the GUI context always
   * reflects the current state.
   * </p>
   * 
   * @param dialogMemory
   *          the dialog-specific implementation of the dialog memory or null, if none is used.
   */
  protected void updateGuiContext(DialogMemory dialogMemory) {
    GuiContext guiContext = GuiContext.getCurrentGuiContext();
    guiContext.setActiveDialogName(getDialogName());
    guiContext.setActiveDialog(getDialogName());
    guiContext.setDialogMemory(getDialogName(), dialogMemory);
  }

  @ExceptionHandler(Throwable.class)
  public ModelAndView handleIteraplanException(Throwable ex, HttpServletRequest req, HttpServletResponse resp) {

    ModelAndView mav = new ModelAndView("errorOutsideFlow");
    if (ex instanceof IteraplanTechnicalException) {
      LOGGER.error("Caught an unexpected exception: " + ex.getMessage(), ex);
    }
    else if (ex instanceof IteraplanBusinessException) {
      LOGGER.info("Handled an (expected) business exception: " + ex.getMessage(), ex);
    }
    else {
      LOGGER.error("Last resort catching an unhandled Exception", ex);
    }

    IteraplanProblemReport.createFromController(ex, req);

    mav.addObject(Constants.JSP_ATTRIBUTE_EXCEPTION_MESSAGE, ex.getLocalizedMessage());
    return mav;
  }

  /**
   * Add a new error message in any request of the redirect-chain.
   * Call populateErrorMessages() after the last possible error message
   * to make the messages available in "ErrorMessagesMVC.jsp"
   * 
   * Concerning the usage of FlashMap/-Attributes, please refer to comment in
   * "springmvc-servlet.xml", bean "DefaultAnnotationHandlerMapping".
   * 
   * @param request  The {@link HttpServletRequest}
   * @param message  A new error message
   */
  @SuppressWarnings("unchecked")
  protected void addErrorMessage(HttpServletRequest request, String message) {
    FlashMap outputFlashMap = RequestContextUtils.getOutputFlashMap(request);
    List<String> errorMessages;
    if (outputFlashMap.containsKey(SessionConstants.MVC_ERROR_MESSAGES_KEY)) {
      errorMessages = (List<String>) outputFlashMap.get(SessionConstants.MVC_ERROR_MESSAGES_KEY);
    }
    else {
      errorMessages = Lists.newArrayList();
      outputFlashMap.put(SessionConstants.MVC_ERROR_MESSAGES_KEY, errorMessages);
    }
    errorMessages.add(message);
  }

  /**
   * Store all error messages from the complete redirect-chain into the request-scope attribute 'MVC_ERROR_MESSAGES_KEY'.
   * Must be called at least once, most suitable at the very end of {@link Theme} controller's method(s).
   * Can be called several times without harm.
   * 
   * Use in JSP like this:  ${requestScope['iteraplanMvcErrorMessages']}
   * 
   * Concerning the usage of FlashMap/-Attributes, please refer to comment in
   * "springmvc-servlet.xml", bean "DefaultAnnotationHandlerMapping".
   * 
   * @param request  The {@link HttpServletRequest}
   */
  @SuppressWarnings("unchecked")
  protected void storeErrorMessagesInRequestScope(HttpServletRequest request) {
    List<String> errorMessages = Lists.newArrayList();

    // Add error messages from previous requests of the redirect-chain
    Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
    if (inputFlashMap != null && inputFlashMap.containsKey(SessionConstants.MVC_ERROR_MESSAGES_KEY)) {
      errorMessages.addAll((List<String>) inputFlashMap.get(SessionConstants.MVC_ERROR_MESSAGES_KEY));
    }

    // Add error messages from the current (and last!) request of the redirect-chain
    FlashMap outputFlashMap = RequestContextUtils.getOutputFlashMap(request);
    if (outputFlashMap.containsKey(SessionConstants.MVC_ERROR_MESSAGES_KEY)) {
      errorMessages.addAll((List<String>) outputFlashMap.get(SessionConstants.MVC_ERROR_MESSAGES_KEY));
    }

    // Use same key for request attribute like for FlashMap key.
    ImmutableList<String> unqiueErrorMessages = ImmutableSortedSet.copyOf(errorMessages).asList();
    request.setAttribute(SessionConstants.MVC_ERROR_MESSAGES_KEY, unqiueErrorMessages);
  }
}

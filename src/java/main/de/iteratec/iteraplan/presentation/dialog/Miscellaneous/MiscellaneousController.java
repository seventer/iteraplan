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
package de.iteratec.iteraplan.presentation.dialog.Miscellaneous;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.presentation.dialog.GuiController;
import de.iteratec.iteraplan.presentation.problemreports.IteraplanProblemReport;
import de.iteratec.iteraplan.presentation.views.AuditLogFileView;
import de.iteratec.iteraplan.presentation.views.ProblemReportingView;


@Controller
public class MiscellaneousController extends GuiController {

  private static final Logger LOGGER = Logger.getIteraplanLogger(MiscellaneousController.class);

  @Override
  @RequestMapping(method = RequestMethod.GET)
  public void init(ModelMap model, HttpSession session, HttpServletRequest req) {
    super.init(model, session, req);

    LOGGER.debug("MiscellaneousController#init");

    if (!UserContext.getCurrentUserContext().getPerms().getUserHasDialogPermission(getDialogName())) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }
    UserContext.getCurrentUserContext().getPerms().assureFunctionalPermission(TypeOfFunctionalPermission.DOWNLOAD_AUDIT_LOG);

    updateGuiContext(null);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView requestAuditLogFile() {
    if (IteraplanProperties.getProperties().propertyIsSetToTrue(IteraplanProperties.PROP_AUDIT_LOGGING_ENABLED)) {
      return new ModelAndView(new AuditLogFileView());
    }

    return new ModelAndView();
  }

  /**
   * Ajax call to store the {@link IteraplanProblemReport} into the servlet container's application context {@link ServletContext}
   * and to return the mailto-link for all users.
   * @param reportKey   A unique key for the current problem report.
   * @param request  The {@link HttpServletRequest} object
   * @return  The mailto-link to generate a mail which includes the link to the (restricted) problem report.
   */
  @RequestMapping(method = RequestMethod.GET)
  public @ResponseBody
  ResponseEntity<String> generateProblemReportLink(@RequestParam(value = "reportKey", required = true) String reportKey, HttpServletRequest request) {

    IteraplanProblemReport problemReport = IteraplanProblemReport.readFromSessionScope(reportKey, request);
    try {
      problemReport.storeInApplicationScope(request);
      return new ResponseEntity<String>(problemReport.getMailtoLink(), HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<String>("", HttpStatus.GONE);
    }
  }

  /**
   * Download problem report as ZIP
   * @param reportKey   A unique key for the current problem report.
   * @param request  The {@link HttpServletRequest} object
   * @param response  The {@link HttpServletResponse} object
   * @return  The view for the zipped problem report
   */
  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView requestProblemReport(@RequestParam(value = "reportKey", required = true) String reportKey, HttpServletRequest request,
                                           HttpServletResponse response) {
    if (!UserContext.getCurrentPerms().isUserIsAdministrator()) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return new ModelAndView();
    }

    // lookup in application scope
    IteraplanProblemReport problemReport = IteraplanProblemReport.readFromApplicationScope(reportKey, request);

    if (problemReport == null) {
      // try to find it in session scope
      problemReport = IteraplanProblemReport.readFromSessionScope(reportKey, request);
    }

    return new ModelAndView(new ProblemReportingView(problemReport));
  }

  @Override
  protected String getDialogName() {
    return Dialog.MISCELLANEOUS.getDialogName();
  }

}

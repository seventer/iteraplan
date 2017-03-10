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
package de.iteratec.iteraplan.presentation.dialog.CustomDashboards;

import java.text.MessageFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.iteratec.iteraplan.businesslogic.service.CustomDashboardInstanceService;
import de.iteratec.iteraplan.businesslogic.service.CustomDashboardTemplateService;
import de.iteratec.iteraplan.businesslogic.service.SavedQueryService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.queries.CustomDashboardInstance;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.dialog.GuiController;


@Controller
@SessionAttributes({ "customDashboardDialogMemory" })
public class CustomDashboardsController extends GuiController {

  public static final String             INIT_VIEW = "customdashboards/init";

  @Autowired
  private SavedQueryService              savedQueryService;

  @Autowired
  private CustomDashboardInstanceService dashboardService;

  @Autowired
  private CustomDashboardTemplateService templateService;

  /**{@inheritDoc}**/
  @Override
  @RequestMapping(method = RequestMethod.GET)
  protected void init(ModelMap model, HttpSession session, HttpServletRequest request) {
    super.init(model, session, request);

    GuiContext context = GuiContext.getCurrentGuiContext();

    CustomDashboardsDialogMemory dashboardDialogMemory;
    if (context.hasDialogMemory(getDialogName())) {
      dashboardDialogMemory = (CustomDashboardsDialogMemory) context.getDialogMemory(getDialogName());
    }
    else {
      dashboardDialogMemory = new CustomDashboardsDialogMemory();
    }

    refillDialogMemory(dashboardDialogMemory);

    model.addAttribute("customDashboardDialogMemory", dashboardDialogMemory);
    //set dialogue memory to model and update GUI context
    updateGuiContext(dashboardDialogMemory);
  }

  @RequestMapping(method = RequestMethod.POST)
  public String onSubmit(@ModelAttribute("customDashboardDialogMemory") CustomDashboardsDialogMemory dialogMem,
                         ModelMap model, HttpSession session, HttpServletRequest req, HttpServletResponse response) {

    String action = dialogMem.getAction();

    if ("loadQuery".equals(action)) {
      CustomDashboardInstance dashboardInstance = new CustomDashboardInstance();
      dashboardInstance.setQuery(savedQueryService.getSavedQuery(dialogMem.getSavedQueryId()));
      dialogMem.setCustomDashboardInstance(dashboardInstance);
    }
    else if ("loadTemplate".equals(action)) {
      CustomDashboardInstance dashboardInstance = dialogMem.getCustomDashboardInstance();

      if (dashboardInstance == null) {
        dashboardInstance = new CustomDashboardInstance();
      }

      dashboardInstance.setTemplate(templateService.findById(dialogMem.getTemplateId()));
      dialogMem.setCustomDashboardInstance(dashboardInstance);
    }
    else if ("saveDashboardInstance".equals(action)) {
      CustomDashboardInstance customDashboardInstance = dialogMem.getCustomDashboardInstance();

      if (customDashboardInstance.getName() == null || customDashboardInstance.getName().isEmpty()) {
        String message = MessageAccess.getString("errors.required");
        dialogMem.addError(MessageFormat.format(message, MessageAccess.getString("global.name")));
      }
      else {
        String userName = UserContext.getCurrentUserContext().getLoginName();
        customDashboardInstance.setAuthor(userName);
        customDashboardInstance.setLastAccessUser(userName);
        Date now = DateTime.now().toDate();
        customDashboardInstance.setCreationTime(now);
        customDashboardInstance.setLastAccessTime(now);
        dashboardService.saveCustomDashboardInstance(customDashboardInstance);
        clearDialogMemory(dialogMem);
      }
    }
    else if ("rollbackDashboardInstance".equals(action)) {
      clearDialogMemory(dialogMem);
    }
    else if ("loadCustomDashboardInstance".equals(action)) {
      dialogMem.setCreateDashboard(true);
      CustomDashboardInstance instance = dashboardService.findById(dialogMem.getCustomDashboardInstanceId());
      dialogMem.setCustomDashboardInstance(instance);
      dialogMem.setTemplateId(instance.getTemplate().getId());
      dialogMem.setSavedQueryId(instance.getQuery().getId());
    }
    else if ("deleteDashboardInstance".equals(action)) {
      CustomDashboardInstance instance = dashboardService.findById(dialogMem.getCustomDashboardInstanceId());
      dashboardService.deleteCustomDashboardInstance(instance);
      clearDialogMemory(dialogMem);
    }
    else if ("deleteSavedQuery".equals(action)) {
      dialogMem.setCustomDashboardInstance(null);
      dialogMem.setSavedQueryId(null);
      dialogMem.setTemplateId(null);
      dialogMem.clearErrors();
    }
    else if ("deleteDashboardTemplate".equals(action)) {
      dialogMem.getCustomDashboardInstance().setTemplate(null);
      dialogMem.setTemplateId(null);
      dialogMem.clearErrors();
    }
    else if ("showNewDashboardInstanceOptions".equals(action)) {
      dialogMem.setCreateDashboard(true);
    }
    else if ("hideNewDashboardInstanceOptions".equals(action)) {
      clearDialogMemory(dialogMem);
    }

    refillDialogMemory(dialogMem);

    updateGuiContext(dialogMem);

    return INIT_VIEW;
  }

  /**
   * @param dialogMem
   */
  private void refillDialogMemory(CustomDashboardsDialogMemory dialogMem) {
    if (dialogMem.getSavedQueryId() != null) {
      dialogMem.setTemplates(templateService.getCustomDashboardTemplateByBbt(dialogMem.getCustomDashboardInstance().getQuery().getResultBbType()));
    }
    dialogMem.setDashboards(dashboardService.getCustomDashboardInstances());
    dialogMem.setSavedQueries(savedQueryService.getAllTabularReportsForDashboardTemplates());
  }

  private void clearDialogMemory(CustomDashboardsDialogMemory dialogMem) {
    dialogMem.setSavedQueryId(null);
    dialogMem.setCustomDashboardInstance(null);
    dialogMem.setCustomDashboardInstanceId(null);
    dialogMem.setCreateDashboard(false);
    dialogMem.setTemplateId(null);
    dialogMem.clearErrors();
  }

  /**{@inheritDoc}**/
  @Override
  protected String getDialogName() {
    return Dialog.CUSTOM_DASHBOARD_INSTANCES_OVERVIEW.getDialogName();
  }
}

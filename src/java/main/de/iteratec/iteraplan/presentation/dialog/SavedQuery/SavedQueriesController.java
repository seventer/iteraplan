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
package de.iteratec.iteraplan.presentation.dialog.SavedQuery;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import de.iteratec.iteraplan.businesslogic.service.CustomDashboardInstanceService;
import de.iteratec.iteraplan.businesslogic.service.SavedQueryService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.UserContext.Permissions;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.queries.CustomDashboardInstance;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.presentation.SessionConstants;
import de.iteratec.iteraplan.presentation.dialog.GuiController;


/**
 * Spring MVC Controller to show saved diagram reports. 
 */
@Controller
public class SavedQueriesController extends GuiController {

  private static final Logger            LOGGER        = Logger.getIteraplanLogger(SavedQueriesController.class);

  private static Ordering<SavedQuery>    queryOrdering = Ordering.natural().onResultOf(new Function<SavedQuery, String>() {
                                                         @Override
                                                         public String apply(SavedQuery input) {
                                                           return input.getName();
                                                         }
                                                       });

  @Autowired
  private SavedQueryService              savedQueryService;

  @Autowired
  private CustomDashboardInstanceService customDashboardService;

  @Override
  @RequestMapping(method = RequestMethod.GET)
  public void init(ModelMap model, HttpSession session, HttpServletRequest request) {
    super.init(model, session, request);
    SavedQueriesDialogMemory savedQueryDialogMemory = new SavedQueriesDialogMemory();
    List<SavedQuery> allSavedQueries = savedQueryService.getAllSavedQueries();

    // Filter out queries according to permissions
    Iterable<SavedQuery> filteredQueries = Iterables.filter(allSavedQueries, new ReportTypePredicate(UserContext.getCurrentPerms()));
    // Sort queries by name
    List<SavedQuery> sortedQueries = queryOrdering.sortedCopy(filteredQueries);

    savedQueryDialogMemory.setSavedQueries(Lists.newArrayList(sortedQueries));

    model.addAttribute(SessionConstants.DIALOG_MEMORY_LABEL, savedQueryDialogMemory);
    storeErrorMessagesInRequestScope(request);
    updateGuiContext(savedQueryDialogMemory);
  }

  @RequestMapping
  public String mvcAction(@RequestParam(value = "deleteQueryId", required = false) Integer deleteQueryId, ModelMap model, HttpServletRequest request) {

    // Delete-action:
    if (deleteQueryId != null) {

      SavedQuery savedQuery = savedQueryService.getSavedQuery(deleteQueryId);
      if (savedQuery == null) {
        throw new IteraplanBusinessException(new IllegalStateException("Could not find query with ID " + deleteQueryId));
      }

      ReportType type = savedQuery.getType();
      if (type != null && type.isGraphicalReport()) {
        UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHREPORT_FULL);
      }
      else if (type != null && type.isTabularReport()) {
        UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.TABREPORT_FULL);
      }
      else {
        LOGGER.warn("Saved query is not of graphical or tabular reporting type.");
        throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
      }

      List<CustomDashboardInstance> cdi = customDashboardService.getCustomDashboardBySavedQuery(savedQuery);

      // check if the savedQuery (tabular report) is used by a dashboard
      if (!cdi.isEmpty()) {
        addErrorMessage(request, MessageAccess.getString("customDashboard.deleteSavedQuery.warning"));
      }
      else {
        try {
          savedQueryService.deleteSavedQuery(savedQuery.getId());
        } catch (RuntimeException e) {

          LOGGER.warn("Could not delete Saved Query with ID " + deleteQueryId);
          LOGGER.warn(e.getMessage());
        }
      }
    }

    return "redirect:/savedqueries/init.do";
  }

  /**{@inheritDoc}**/
  @Override
  protected String getDialogName() {
    return Dialog.SAVED_QUERIES.getDialogName();
  }

  /** {@link Predicate} helper type to filter out saved queries by permission  */
  private static class ReportTypePredicate implements Predicate<SavedQuery> {

    private final boolean tabularPermitted;
    private final boolean graphicalPermitted;

    /**
     * Default constructor.
     */
    public ReportTypePredicate(Permissions perms) {
      this.graphicalPermitted = perms.getUserHasFuncPermGraphReporting();
      this.tabularPermitted = perms.getUserHasFuncPermTabReporting();
    }

    /**{@inheritDoc}**/
    @Override
    public boolean apply(SavedQuery input) {
      ReportType reportType = input.getType();
      boolean graphical = (ReportType.GRAPHICAL_REPORTING_TYPES.contains(reportType) && graphicalPermitted);
      boolean tabular = (ReportType.TABLUAR_REPORTING_TYPES.contains(reportType) && tabularPermitted);
      return graphical || tabular;
    }
  }
}

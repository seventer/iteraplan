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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.iteratec.iteraplan.businesslogic.reports.query.QueryTreeGenerator;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.CustomDashboardInstanceService;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.businesslogic.service.QueryService;
import de.iteratec.iteraplan.businesslogic.service.SavedQueryService;
import de.iteratec.iteraplan.businesslogic.service.UserService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.queries.CustomDashboardInstance;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.xml.ReportXML;
import de.iteratec.iteraplan.model.xml.query.PostProcessingAdditionalOptionsXML;
import de.iteratec.iteraplan.model.xml.query.PostProcessingStrategiesXML;
import de.iteratec.iteraplan.model.xml.query.PostProcessingStrategyXML;
import de.iteratec.iteraplan.model.xml.query.QueryFormXML;
import de.iteratec.iteraplan.model.xml.query.QueryResultXML;
import de.iteratec.iteraplan.presentation.dialog.GuiController;


@Controller
public class CustomDashboardInstanceController extends GuiController {
  @Autowired
  private CustomDashboardInstanceService dashboardService;

  @Autowired
  private UserService                    userService;

  @Autowired
  private SavedQueryService              savedQueryService;

  @Autowired
  private InitFormHelperService          initFormHelperService;
  @Autowired
  private AttributeTypeService           attributeTypeService;

  @Autowired
  private QueryService                   queryService;

  @RequestMapping(value = "show.do", method = RequestMethod.GET)
  public void showDashboard(@RequestParam("id")
  Integer id, ModelMap model, HttpSession session, HttpServletRequest request) {
    super.init(model, session, request);

    CustomDashboardInstanceDialogMemory dashboardDialogMemory = new CustomDashboardInstanceDialogMemory();

    if (UserContext.getCurrentPerms().userHasFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING)) {
      CustomDashboardInstance instance = dashboardService.findById(id);

      dashboardDialogMemory.setCustomDashboardInstance(instance);
      dashboardDialogMemory.setAuthor(userService.getUserByLoginIfExists(instance.getAuthor()));
      dashboardDialogMemory.setQueryResults(loadQueryResult(instance.getQuery()));

      //manually set the attributes required for breadcrumb construction, as we are in a dialog not directly reachable from the navigation
      model.addAttribute("active_title", "global.customDashboardInstances");
      model.addAttribute("active_url", "/customdashboards/init.do");
      model.addAttribute("active_sub_title", instance.getName());
      model.addAttribute("active_sub_url", request.getRequestURL() + "?" + request.getQueryString());
    }

    model.addAttribute("dialogMemory", dashboardDialogMemory);
    //set dialogue memory to model and update GUI context
    updateGuiContext(dashboardDialogMemory);
  }

  /**
   * @param savedQuery
   * 
   * @return the resulting list of buildingblocks of the specified saved query
   */
  private List<? extends BuildingBlock> loadQueryResult(SavedQuery savedQuery) {
    ReportXML savedReport = savedQueryService.getSavedReport(savedQuery);

    List<QueryResultXML> queryResults = savedReport.getQueryResults();
    QueryResult queryResult = initFormHelperService.getQueryResult(queryResults.get(0));

    // handle postprocessing strategies similar to InitFormHelperServiceImpl, but do not use a membean 
    // normally the membean is used to initialize the queryresults according to the saved query but there is no membean available for MVC Controllers.
    QueryFormXML queryFormXml = queryResults.get(0).getQueryForms().get(0);
    queryResult.initPostProcessingStrategies(Constants.REPORTS_EXPORT_TABVIEW);
    PostProcessingStrategiesXML xmlStrategies = queryFormXml.getPostProcessingStrategies();
    if (xmlStrategies != null && xmlStrategies.getPostProcessingStrategy() != null && !xmlStrategies.getPostProcessingStrategy().isEmpty()) {

      List<String> selectedPPStrategyKeys = new ArrayList<String>();
      Map<String, List<String>> strategyWithOptions = new HashMap<String, List<String>>();
      List<PostProcessingStrategyXML> listOfStrategies = xmlStrategies.getPostProcessingStrategy();

      for (PostProcessingStrategyXML strategy : listOfStrategies) {
        selectedPPStrategyKeys.add(strategy.getName());
        List<PostProcessingAdditionalOptionsXML> additionalOptions = strategy.getAdditionalOptions();
        List<String> selectedPPSOptionKeys = new ArrayList<String>();

        for (PostProcessingAdditionalOptionsXML option : additionalOptions) {
          selectedPPSOptionKeys.add(option.getAdditionalOption());
        }

        strategyWithOptions.put(strategy.getName(), selectedPPSOptionKeys);
      }

      queryResult.setSelectedPostprocessingStrategies(QueryResult.getPostProcessingStrategiesByKeys(queryFormXml.getType(), selectedPPStrategyKeys),
          strategyWithOptions);
    }

    List<AbstractPostprocessingStrategy<BuildingBlock>> listWithoutWildcard = queryService.disposeOfWildcard(
        queryResult.getSelectedPostProcessingStrategies(), BuildingBlock.class);

    QueryTreeGenerator qtg = new QueryTreeGenerator(UserContext.getCurrentLocale(), attributeTypeService);
    Node node = qtg.generateQueryTree(queryResult.getQueryForms());

    return queryService.evaluateQueryTree(node, queryResult.getTimeseriesQuery(), listWithoutWildcard);
  }

  /**{@inheritDoc}**/
  @Override
  protected String getDialogName() {
    return Dialog.CUSTOM_DASHBOARD_INSTANCE.getDialogName();
  }
}

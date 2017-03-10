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
package de.iteratec.iteraplan.presentation.dialog.ConsistencyCheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.iteratec.iteraplan.businesslogic.reports.staticquery.Configuration;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Domain;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.PropertiesFacade;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Result;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.StaticQuery;
import de.iteratec.iteraplan.businesslogic.service.ConsistencyCheckService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.dialog.GuiController;


@Controller
@SessionAttributes("dialogMemory")
public class ConsistencyCheckController extends GuiController {

  @Autowired
  private ConsistencyCheckService consistencyCheckService;

  @Override
  @RequestMapping
  public void init(ModelMap model, HttpSession session, HttpServletRequest request) {

    super.init(model, session, request);

    GuiContext context = GuiContext.getCurrentGuiContext();

    ConsistencyCheckDialogMemory dialogMemory;
    if (context.hasDialogMemory(getDialogName())) {
      // if the page was already accessed once, use the existing dialogMemory
      dialogMemory = (ConsistencyCheckDialogMemory) context.getDialogMemory(getDialogName());
    }
    else {
      // else prepare dialogMemory for initial call
      // Retrieve all currently available consistency checks.
      List<StaticQuery> checks = consistencyCheckService.getAllConsistencyChecks();

      // Create a map data structure mapping domains to a list of consistency check
      // configurations in order to display them grouped accordingly in the GUI.
      HashBucketMap<Domain, Configuration> map = new HashBucketMap<Domain, Configuration>();

      for (StaticQuery check : checks) {
        // Initialize the check's configuration.
        Configuration configuration = check.initializeConfiguration();
        map.add(configuration.getDomain(), configuration);
      }

      // Create a list of domains and sort them according to the order in which they are
      // specified. Used for displaying the domains in the GUI in the correct order.
      List<Domain> domains = new ArrayList<Domain>(map.keySet());
      Collections.sort(domains);

      // Create new dialogMemory fill it with the information and store it in the model
      dialogMemory = new ConsistencyCheckDialogMemory();
      dialogMemory.setDomain(domains);
      dialogMemory.setConfigurations(map);
    }
    model.addAttribute("dialogMemory", dialogMemory);
    updateGuiContext(dialogMemory);
  }

  @RequestMapping
  public void requestSingleReport(@ModelAttribute("dialogMemory") ConsistencyCheckDialogMemory dialogMemory, HttpSession session,
                                  HttpServletRequest request) {

    // Get the consistency check.
    String key = dialogMemory.getSelectedConsistencyCheck();
    String value = PropertiesFacade.getInstance().getProperty(key);
    StaticQuery check = consistencyCheckService.getConsistencyCheck(value);

    // Execute the consistency check.
    Result result = check.execute(check.getConfiguration().getParameters());

    // Set the consistency check´s number to display it on the GUI.
    result.setNumber(dialogMemory.getSelectedConsistencyCheckNumber());

    // Create a map data structure mapping domains to a list of consistency check
    // results in order to display them grouped accordingly in the GUI.
    HashBucketMap<Domain, Result> map = new HashBucketMap<Domain, Result>();
    map.add(result.getDomain(), result);

    // Create a list of domains and sort them according to the order in which they are
    // specified. Used for displaying the domains in the GUI in the correct order.
    List<Domain> domains = new ArrayList<Domain>(map.keySet());
    Collections.sort(domains);

    // Store the result in the dialogMemory
    dialogMemory.setDomainsResult(domains);
    dialogMemory.setResults(map);
    // Store the dialogMemory in the GuiContext
    updateGuiContext(dialogMemory);
  }

  @RequestMapping
  public void requestFullDomainReport(@ModelAttribute("dialogMemory") ConsistencyCheckDialogMemory dialogMemory, ModelMap model, HttpSession session,
                                      HttpServletRequest request) {

    // Get the selected Domain
    Domain domain = Domain.getDomainByString(dialogMemory.getSelectedConsistencyCheck());

    // Get all consistency checks.
    List<StaticQuery> list = consistencyCheckService.getAllConsistencyChecks();

    // Create a map data structure mapping domains to a list of consistency check
    // results in order to display them grouped accordingly in the GUI.
    HashBucketMap<Domain, Result> map = new HashBucketMap<Domain, Result>();

    for (StaticQuery check : list) {
      if (check.getConfiguration().getDomain() == domain) {
        // Execute the consistency check.
        Result result = check.execute(check.getConfiguration().getParameters());
        map.add(result.getDomain(), result);
      }
    }

    // Sort the domains in the order they are specified.
    List<Domain> domains = new ArrayList<Domain>(map.keySet());
    Collections.sort(domains);

    // Store the result in the dialogMemory
    dialogMemory.setDomainsResult(domains);
    dialogMemory.setResults(map);
    // Store the dialogMemory in the GuiContext
    updateGuiContext(dialogMemory);
  }

  @RequestMapping
  public void requestFullReport(@ModelAttribute("dialogMemory") ConsistencyCheckDialogMemory dialogMemory, ModelMap model, HttpSession session,
                                HttpServletRequest request) {

    // Get all consistency checks.
    List<StaticQuery> list = consistencyCheckService.getAllConsistencyChecks();

    // Create a map data structure mapping domains to a list of consistency check
    // results in order to display them grouped accordingly in the GUI.
    HashBucketMap<Domain, Result> map = new HashBucketMap<Domain, Result>();

    for (StaticQuery check : list) {
      // Execute the consistency check.
      Result result = check.execute(check.getConfiguration().getParameters());
      map.add(result.getDomain(), result);
    }

    // Sort the domains in the order they are specified.
    List<Domain> domains = new ArrayList<Domain>(map.keySet());
    Collections.sort(domains);

    // Store the result in the dialogMemory
    dialogMemory.setDomainsResult(domains);
    dialogMemory.setResults(map);
    // Store the dialogMemory in the GuiContext
    updateGuiContext(dialogMemory);
  }

  @Override
  protected String getDialogName() {
    return Dialog.CONSISTENCY_CHECK.getDialogName();
  }

}

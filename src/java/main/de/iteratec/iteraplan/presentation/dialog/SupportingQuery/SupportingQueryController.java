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
package de.iteratec.iteraplan.presentation.dialog.SupportingQuery;

import java.util.ArrayList;
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
import de.iteratec.iteraplan.businesslogic.reports.staticquery.PropertiesFacade;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Result;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.StaticQuery;
import de.iteratec.iteraplan.businesslogic.service.PermissionQueryService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.dialog.GuiController;


@Controller
@SessionAttributes("dialogMemory")
public class SupportingQueryController extends GuiController {

  @Autowired
  private PermissionQueryService permissionQueryService;

  @Autowired
  public SupportingQueryController(PermissionQueryService permissionQueryService) {
    super();
    this.permissionQueryService = permissionQueryService;
  }

  @Override
  @RequestMapping
  public void init(ModelMap model, HttpSession session, HttpServletRequest request) {

    super.init(model, session, request);

    GuiContext context = GuiContext.getCurrentGuiContext();
    PermissionQueriesDialogMemory dialogMemory;

    if (context.hasDialogMemory(getDialogName())) {
      // if the page was already accessed once, use the existing MemoryBean
      dialogMemory = (PermissionQueriesDialogMemory) context.getDialogMemory(getDialogName());

    }
    else {
      // else prepare dialogMemory for initial call
      // Retrieve all currently available permission queries.
      List<StaticQuery> queries = permissionQueryService.getAllPermissionQueries();
      List<Configuration> configurations = new ArrayList<Configuration>();

      for (StaticQuery query : queries) {

        Configuration configuration = query.initializeConfiguration();
        configurations.add(configuration);
      }

      // Create new dialogMemory and store it in the GuiContext.
      dialogMemory = new PermissionQueriesDialogMemory();
      dialogMemory.setConfigurations(configurations);
    }
    model.addAttribute("dialogMemory", dialogMemory);
    this.updateGuiContext(dialogMemory);
  }

  @RequestMapping
  public void requestSingleReport(@ModelAttribute("dialogMemory") PermissionQueriesDialogMemory dialogMemory, HttpSession session,
                                  HttpServletRequest request) {

    // Retrieve the selected query.
    String key = dialogMemory.getSelectedPermissionQuery();
    String value = PropertiesFacade.getInstance().getProperty(key);
    StaticQuery query = permissionQueryService.getPermissionQuery(value);

    // Execute the query.
    Result result = query.execute(query.getConfiguration().getParameters());

    // Set the supporting query´s number to display it on the GUI.
    result.setNumber(dialogMemory.getSelectedPermissionQueryNumber());

    // Store the result in the dialogMemory
    List<Result> results = new ArrayList<Result>();
    results.add(result);
    dialogMemory.setResults(results);
    this.updateGuiContext(dialogMemory);

  }

  @Override
  protected String getDialogName() {
    return Dialog.SUPPORTING_QUERY.getDialogName();
  }

}

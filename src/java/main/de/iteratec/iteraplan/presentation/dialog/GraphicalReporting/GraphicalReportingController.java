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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ReportMemBean;
import de.iteratec.iteraplan.businesslogic.service.SavedQueryService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.presentation.dialog.GuiController;


/**
 * A very simple controller for the diagram type selection page. Its only purpose is to supply
 * information to the menu highlighting functionality, i.e. update GUI context.
 */
@Controller
public class GraphicalReportingController extends GuiController {

  private MappingJacksonHttpMessageConverter converter    = new MappingJacksonHttpMessageConverter();

  /** The saved query types, which will be show. */
  private static final Set<ReportType>       REPORT_TYPES = Sets.newHashSet(ReportType.LANDSCAPE, ReportType.LINE, ReportType.CLUSTER, ReportType.INFORMATIONFLOW,
                                                              ReportType.PORTFOLIO, ReportType.MASTERPLAN, ReportType.PIE, ReportType.BAR,
                                                              ReportType.COMPOSITE, ReportType.VBBCLUSTER, ReportType.TIMELINE, ReportType.MATRIX);

  @Autowired
  private SavedQueryService                  savedQueryService;

  @Override
  protected String getDialogName() {
    return Dialog.GRAPHICAL_REPORTING.getDialogName();
  }

  @Override
  @RequestMapping
  public void init(ModelMap model, HttpSession session, HttpServletRequest request) {
    super.init(model, session, request);
    updateGuiContext(null);
    ReportMemBean memBean = new ManageReportMemoryBean();

    List<SavedQuery> savedQueries = Lists.newArrayList(savedQueryService.getSavedQueriesWithoutContent(REPORT_TYPES));
    memBean.setSavedQueries(savedQueries);
    model.addAttribute("memBean", memBean);
  }

  @RequestMapping
  public void loadSavedQueries(@RequestParam(value = "type", required = true) String reportType, HttpServletResponse response) throws IOException {
    List<SavedQuery> savedQueries = savedQueryService.getSavedQueriesWithoutContent(ReportType.fromValue(reportType));
    List<Map<String, String>> result = Lists.newArrayList();
    for (SavedQuery sq : savedQueries) {
      Map<String, String> struct = Maps.newHashMap();
      struct.put("id", sq.getId().toString());
      struct.put("name", sq.getName());
      result.add(struct);
    }
    converter.write(result, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
  }
}

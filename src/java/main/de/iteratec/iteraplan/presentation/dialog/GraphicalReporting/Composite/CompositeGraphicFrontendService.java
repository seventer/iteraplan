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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.Composite;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.reports.query.options.CompositeReportMemoryBean;
import de.iteratec.iteraplan.presentation.dialog.CommonFrontendService;


public interface CompositeGraphicFrontendService extends CommonFrontendService {

  CompositeReportMemoryBean getInitialMemBean();

  /**
   * Refreshes the list of available graphical reports to add as part of the composite diagram
   * @param memBean
   *          {@link CompositeReportMemoryBean}
   * @param context
   * @param flowContext
   * @return the updated {@link CompositeReportMemoryBean}
   */
  CompositeReportMemoryBean refreshAvailablePartQueries(CompositeReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  /**
   * Updates the ordering of the part report membeans
   * @param memBean
   * @param context
   * @param flowContext
   * @return the updated {@link CompositeReportMemoryBean}
   */
  CompositeReportMemoryBean refreshOrderOfParts(CompositeReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  /**
   * Saves the currently configured reporting options as a saved report.
   * 
   * @param memBean
   * @param requestContext TODO
   */
  CompositeReportMemoryBean saveQuery(CompositeReportMemoryBean memBean, RequestContext requestContext);
  
  /**
   * Saves the currently configured reporting options as a saved report under a new name.
   * 
   * @param memBean
   * @param requestContext TODO
   */
  CompositeReportMemoryBean saveQueryAs(CompositeReportMemoryBean memBean, RequestContext requestContext);

  CompositeReportMemoryBean loadSavedQuery(CompositeReportMemoryBean memBean);

  CompositeReportMemoryBean deleteSavedQuery(CompositeReportMemoryBean memBean);

  void generateGraphicFileResponse(CompositeReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  void generateGraphicFileResponse(CompositeReportMemoryBean memBean, HttpServletRequest request, HttpServletResponse response);
}

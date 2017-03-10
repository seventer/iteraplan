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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.InformationFlow;

import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicalReportBaseFrontendService;


public interface InformationFlowGraphicFrontendService extends GraphicalReportBaseFrontendService<ManageReportMemoryBean> {

  ManageReportMemoryBean stepOneToStepTwo(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  ManageReportMemoryBean stepTwoToStepOne(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  /**
   * Initializes the configuration dialog for a context diagram of the specified Building Block
   * @param id Building Block ID
   * @param bbType Building Block type
   * @return a freshly initialized memBean for a context diagram
   */
  ManageReportMemoryBean directlyToStepTwo(Integer id, String bbType);

  ManageReportMemoryBean fromInterchange(String idList);

  /**
   * Returns a new memBean for filtering interfaces. It contains a {@link de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult} for Interfaces. If the old memBean does not
   * contain a {@link de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult} for filtering interfaces, a new one will be created. This one will also be added to the old memBean.
   * @param memBean the old memBean
   * @return see method description.
   */
  ManageReportMemoryBean filterInterfaces(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  ManageReportMemoryBean filterBusinessObjects(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  /**
   * Removes the current {@link de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult} for filtering interfaces from the memBean.
   */
  ManageReportMemoryBean resetInterfacesFilter(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  ManageReportMemoryBean resetBusinessObjectsFilter(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);
  
  ManageReportMemoryBean resetReport(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  /**
   * Refreshes the list of all interfaces, selected by the interface-filter, where both ends selected by the main-query.
   * @param memBean
   */
  void refreshRelevantInterfaces(ManageReportMemoryBean memBean);
  
  void refreshRelevantBusinessObjects(ManageReportMemoryBean memBean);
}

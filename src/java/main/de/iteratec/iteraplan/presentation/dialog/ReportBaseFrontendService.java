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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ReportMemBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExportOption;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.model.BuildingBlock;


public interface ReportBaseFrontendService<E extends ReportMemBean> {

  boolean requestReport(E memBean, RequestContext context, FlowExecutionContext flowContext);

  boolean requestReport(E memBean, HttpServletRequest request, HttpServletResponse response);

  E refreshReport(E memBean, RequestContext context, FlowExecutionContext flowContext, boolean resetReport);

  boolean addReportExtension(E memBean, RequestContext context, FlowExecutionContext flowContext);

  boolean removeReportExtension(E memBean, RequestContext context, FlowExecutionContext flowContext);

  E expandFirstLevel(E memBean, RequestContext context, FlowExecutionContext flowContext);

  E expandSecondLevel(E memBean, RequestContext context, FlowExecutionContext flowContext);

  E shrinkLevel(E memBean, RequestContext context, FlowExecutionContext flowContext);

  E changeQueryType(E memBean, RequestContext context, FlowExecutionContext flowContext);

  /**
   * Hides the export options which are not supported by the selected post processing strategy. <br/>
   * For example, some post processing strategies are only meaningful for graphical exports. When
   * they are selected, an Excel export would not be available.
   * 
   * @param selectedPps
   *          The selected post processing strategies.
   * @param availableResultFormats
   *          All available result formats.
   */
  void hideUnsupportedResultFormats(List<AbstractPostprocessingStrategy<? extends BuildingBlock>> selectedPps,
                                    List<ExportOption> availableResultFormats);

  /**
   * Saves the currently configured reporting options as a saved report.
   * 
   * @param memBean
   * @param requestContext
   */
  E saveQuery(E memBean, RequestContext requestContext);
  
  /**
   * Saves the currently configured reporting options as a saved report under a new name.
   * 
   * @param memBean
   * @param requestContext
   */
  E saveQueryAs(E memBean, RequestContext requestContext);

  E loadSavedQuery(E memBean);

  E deleteSavedQuery(E memBean);

  E addColumn(E memBean, RequestContext context, FlowExecutionContext flowContext);

  E updateColumn(E memBean, RequestContext context, FlowExecutionContext flowContext);

  E removeColumn(E memBean, RequestContext context, FlowExecutionContext flowContext);

}

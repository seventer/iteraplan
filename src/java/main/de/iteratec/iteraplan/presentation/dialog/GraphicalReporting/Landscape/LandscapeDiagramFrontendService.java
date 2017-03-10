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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.Landscape;

import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ManageLandscapeDiagramMemoryBean;


public interface LandscapeDiagramFrontendService extends LandscapeDiagramBaseFrontendService {

  ManageLandscapeDiagramMemoryBean getInitialMemBean();

  /**
   * Initializes the configuration dialog for a context diagram of the specified Building Block
   * @param id Building Block ID
   * @param bbType Building Block type
   * @param diagramVariant one of the String codes specified in {@link de.iteratec.iteraplan.businesslogic.service.FastExportService.DiagramVariant}
   * @return a freshly initialized memBean for a context diagram
   */
  ManageLandscapeDiagramMemoryBean directlyToStepTwo(Integer id, String bbType, String diagramVariant);

  ManageLandscapeDiagramMemoryBean selectRowType(ManageLandscapeDiagramMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  ManageLandscapeDiagramMemoryBean selectColumnType(ManageLandscapeDiagramMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  ManageLandscapeDiagramMemoryBean changeContentType(ManageLandscapeDiagramMemoryBean memBean, RequestContext context,
                                                     FlowExecutionContext flowContext);

  ManageLandscapeDiagramMemoryBean selectContentType(ManageLandscapeDiagramMemoryBean memBean, RequestContext context,
                                                     FlowExecutionContext flowContext);

  ManageLandscapeDiagramMemoryBean updateColorAttribute(ManageLandscapeDiagramMemoryBean memBean, RequestContext context,
                                                        FlowExecutionContext flowContext);

  ManageLandscapeDiagramMemoryBean updateLineTypeAttribute(ManageLandscapeDiagramMemoryBean memBean, RequestContext context,
                                                           FlowExecutionContext flowContext);

  ManageLandscapeDiagramMemoryBean changeColumnType(ManageLandscapeDiagramMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  ManageLandscapeDiagramMemoryBean changeRowType(ManageLandscapeDiagramMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  ManageReportMemoryBean filterRowResults(ManageLandscapeDiagramMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  ManageReportMemoryBean filterColumnResults(ManageLandscapeDiagramMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  ManageReportMemoryBean filterContentResults(ManageLandscapeDiagramMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  ManageReportMemoryBean resetReport(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

  ManageLandscapeDiagramMemoryBean resumeFromFilter(ManageReportMemoryBean reportMemBean, ManageLandscapeDiagramMemoryBean memBean,
                                                    RequestContext context, FlowExecutionContext flowContext);

  ManageLandscapeDiagramMemoryBean loadSavedQuery(ManageLandscapeDiagramMemoryBean memBean);
  
  /**
   * Load the SavedQuery.<br>
   * If the queryRef (Reference to an SavedQuery for tabular reporting) is not null,
   * the QueryResult from the SavedQuery is changed to the QueryResult from queryRef.
   * 
   * @param memBean
   * @param queryRef (Reference to an SavedQuery for tabular reporting)
   * @return The MemoryBean with the QueryResult
   */
  ManageLandscapeDiagramMemoryBean loadSavedQuery(ManageLandscapeDiagramMemoryBean memBean, Integer queryRef);

  ManageLandscapeDiagramMemoryBean saveQuery(ManageLandscapeDiagramMemoryBean memBean, RequestContext requestContext);
  
  ManageLandscapeDiagramMemoryBean saveQueryAs(ManageLandscapeDiagramMemoryBean memBean, RequestContext requestContext);

  ManageLandscapeDiagramMemoryBean deleteSavedQuery(ManageLandscapeDiagramMemoryBean memBean);

  void generateGraphicFileResponse(ManageLandscapeDiagramMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext);

}

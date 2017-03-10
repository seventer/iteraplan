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
/*
l * iteraplan is an IT Governance web application developed by iteratec, GmbH
 * Copyright (C) 2008 iteratec, GmbH
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
 * You can contact iteratec GmbH headquarters at Inselkammerstraße 4
 * 82008 München - Unterhaching, Germany, or at email address info@iteratec.de.
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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ManageLandscapeDiagramMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExportOption;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.ReportBaseFrontendServiceImpl;


public abstract class LandscapeDiagramBaseFrontendServiceImpl extends ReportBaseFrontendServiceImpl<ManageReportMemoryBean> implements
    LandscapeDiagramBaseFrontendService {

  @Override
  public boolean addReportExtension(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    String extension = memBean.getSelectedReportExtension();
    // add to extension and remove from available list.
    QueryResult queryResult = memBean.getQueryResult();
    getExtensionFormHelper().addReportExtension(extension, queryResult.getQueryForms(), queryResult.getAvailableReportExtensions());

    return true;
  }

  @Override
  public void hideUnsupportedResultFormats(List<AbstractPostprocessingStrategy<? extends BuildingBlock>> selectedPps,
                                           List<ExportOption> availableResultFormats) {
    for (ExportOption exOpt : availableResultFormats) {
      exOpt.setVisible(true);
    }
    for (AbstractPostprocessingStrategy<? extends BuildingBlock> ppsElement : selectedPps) {
      String[] supportedExportFormats = ppsElement.getSupportedReportTypes();
      for (ExportOption exOpt : availableResultFormats) {
        boolean setToVisible = false;
        for (int i = 0; i < supportedExportFormats.length; i++) {
          if (exOpt.getPresentationKey().equals(supportedExportFormats[i])) {
            setToVisible = true;
            break;
          }
        }
        exOpt.setVisible(setToVisible);
      }
    }
  }

  @Override
  public boolean removeReportExtension(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    Integer extensionId = memBean.getReportExtensionToRemove();
    // remove the extension from the form and add it to the available list.
    QueryResult queryResult = memBean.getQueryResult();
    getExtensionFormHelper().removeExportExtension(queryResult.getQueryForms(), queryResult.getAvailableReportExtensions(), extensionId);

    return true;
  }

  @Override
  protected String getFlowId() {
    return Dialog.GRAPHICAL_REPORTING_LANDSCAPE.getFlowId();
  }

  /**
   * Needs to be implemented by subclasses
   */
  public abstract void generateGraphicFileResponse(ManageLandscapeDiagramMemoryBean memBean, HttpServletRequest request, HttpServletResponse response);

  /**
   * Retrieves the necessary objects from the context and delegates to
   * {@link #generateGraphicFileResponse(ManageLandscapeDiagramMemoryBean, HttpServletRequest, HttpServletResponse)}
   * 
   * @param memBean
   * @param context
   * @param flowContext
   */
  public void generateGraphicFileResponse(ManageLandscapeDiagramMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getNativeRequest();
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();
    generateGraphicFileResponse(memBean, request, response);

    context.getExternalContext().recordResponseComplete();
  }

}

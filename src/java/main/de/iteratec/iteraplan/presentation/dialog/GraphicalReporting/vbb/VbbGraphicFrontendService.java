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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.vbb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.BaseVBB;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalOptionsGetter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.vbb.VbbOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.VbbGraphicsService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicExportBean;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicalReportBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator.GraphicalReport;


public abstract class VbbGraphicFrontendService extends GraphicalReportBaseFrontendServiceImpl<ManageReportMemoryBean> {

  @Autowired
  private VbbGraphicsService vbbGraphicsService;

  public ManageReportMemoryBean getInitialMemBean() {
    ManageReportMemoryBean memBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL,
        Constants.ALL_TYPES_FOR_DISPLAY);

    VbbOptionsBean vbbOptions = new VbbOptionsBean(getReportType());
    memBean.setGraphicalOptions(vbbOptions);

    //TODO setEnabled is relevant?
    vbbOptions.setIteraQlEnabled(true);
    vbbOptions.setSelectedBbType(memBean.getSelectedBuildingBlock());
    memBean.setReportType(getReportType());
    memBean.setSavedQueries(getSavedQueryService().getSavedQueriesWithoutContent(getReportType()));
    memBean.resetPostProcessingStrategies();

    return memBean;
  }

  @Override
  public ManageReportMemoryBean loadSavedQuery(ManageReportMemoryBean memBean) {
    ManageReportMemoryBean newBean = super.loadSavedQuery(memBean);
    //TODO setEnabled is relevant?
    GraphicalOptionsGetter.getVbbOptions(newBean).setIteraQlEnabled(true);
    return newBean;
  }

  @Override
  public void generateGraphicFileResponse(ManageReportMemoryBean memBean, HttpServletRequest request, HttpServletResponse response) {
    applyFilters(memBean);

    memBean.getGraphicalOptions().setServerUrl(URLBuilder.getApplicationURL(request));

    byte[] result = this.vbbGraphicsService.createVbbDiagram(memBean, getVbbDiagramName());
    GraphicExportBean vBean = new GraphicExportBean(result);

    GraphicsResponseGenerator respGen = getExportService().getResponseGenerator(memBean.getGraphicalOptions().getSelectedGraphicFormat());
    // have the report file written to the HttpServletResponse
    respGen.generateResponse(response, vBean, getGraphicalReport(), memBean.getContent());
  }

  /**{@inheritDoc}**/
  @Override
  protected ManageReportMemoryBean getMemBeanForChangedQueryType(ManageReportMemoryBean memBean) {
    // not needed
    return memBean;
  }

  protected abstract GraphicalReport getGraphicalReport();

  protected abstract String getVbbDiagramName();

  /**{@inheritDoc}**/
  @Override
  protected abstract ReportType getReportType();

  /**{@inheritDoc}**/
  @Override
  protected abstract String getFlowId();

  private void applyFilters(ManageReportMemoryBean memBean) {
    VbbOptionsBean result = GraphicalOptionsGetter.getVbbOptions(memBean);

    for (QueryResult qr : memBean.getQueryResults().values()) {
      result.getViewpointConfigMap().put(qr.getQueryName() + BaseVBB.FILTER_SUFFIX,
          constructFilteredTypeString(result.getViewpointConfigMap().get(qr.getQueryName()), qr.getSelectedResultIds()));
    }
  }

  private String constructFilteredTypeString(String baseTypeString, Integer[] ids) {
    if (!baseTypeString.contains(".objectify(")) {
      StringBuilder result = new StringBuilder();
      for (int i = 0; i < ids.length; i++) {
        result.append(ids[i]);
        if (i < ids.length - 1) {
          result.append(",");
        }
      }
      return result.toString();
    }
    else {
      return "";
    }
  }
}

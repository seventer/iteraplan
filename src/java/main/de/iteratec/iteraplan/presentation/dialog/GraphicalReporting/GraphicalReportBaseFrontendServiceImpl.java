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

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalOptionsGetter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.IGraphicalExportBaseOptions;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.queries.SavedQueryEntity;
import de.iteratec.iteraplan.model.queries.SavedQueryEntityInfo;
import de.iteratec.iteraplan.model.xml.ReportXML;
import de.iteratec.iteraplan.presentation.dialog.ReportBaseFrontendServiceImpl;


public abstract class GraphicalReportBaseFrontendServiceImpl<E extends ManageReportMemoryBean> extends ReportBaseFrontendServiceImpl<E> implements
    GraphicalReportBaseFrontendService<E> {

  private static final Logger LOGGER = Logger.getIteraplanLogger(GraphicalReportBaseFrontendServiceImpl.class);

  @Override
  public E loadSavedQuery(E memBean) {
    if (null == memBean.getSavedQueryId()) {
      throw new IteraplanTechnicalException();
    }

    SavedQuery savedQuery = getSavedQueryService().getSavedQuery(memBean.getSavedQueryId());

    if (!memBean.getReportType().equals(savedQuery.getType())) {
      LOGGER.error("requested QueryType ('{0}') does not fit the required QueryType ('{1}')", memBean.getReportType(), savedQuery.getType());
      throw new IteraplanBusinessException(IteraplanErrorMessages.INVALID_REQUEST_PARAMETER, "savedQueryType");
    }

    ReportXML savedReport = getSavedQueryService().getSavedReport(savedQuery);

    return initializeMemBeanFromSavedQuery(memBean, savedQuery, savedReport);
  }

  @Override
  protected E initializeMemBeanFromSavedQuery(E memBean, SavedQuery savedQuery, ReportXML savedReport) {
    return getInitFormHelperService().initMemBeanFromSavedGraphicalReport(memBean, savedQuery, savedReport);
  }

  @Override
  public E deleteSavedQuery(E memBean) {
    if (null == memBean.getDeleteQueryId()) {
      throw new IteraplanTechnicalException();
    }

    getSavedQueryService().deleteSavedQuery(memBean.getDeleteQueryId());
    loadStoredXMLQueries(memBean);

    return memBean;
  }

  @Override
  public E saveQuery(E memBean, RequestContext requestContext) {
    boolean saveAsOption = memBean.isSaveAs();
    memBean.setSaveAs(false);

    Locale locale = UserContext.getCurrentLocale();
    ReportXML saveReport = new ReportXML();
    saveReport.initFrom(memBean, locale);

    IGraphicalExportBaseOptions options = memBean.getGraphicalOptions();
    if (ReportType.CLUSTER.equals(memBean.getReportType())
        && Constants.REPORTS_EXPORT_CLUSTER_MODE_ATTRIBUTE.equals(GraphicalOptionsGetter.getClusterOptions(memBean).getSelectedClusterMode())) {
      saveReport.setSelectedResultIds(null);
    }
    if (options != null) {
      options.validate();
    }

    String name;
    if (saveAsOption) {
      name = memBean.getXmlSaveAsQueryName();
    }
    else {
      name = memBean.getXmlQueryName();
    }

    if (name == null || name.equals("")) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.SAVEDQUERY_NAME_IS_NULL);
    }

    if (saveAsOption) {
      checkIfQueryNameIsUsed(memBean.getReportType(), name);
      acceptNewNameAndDescription(memBean);
    }

    if (memBean.getCheckAllBox().booleanValue()) {
      saveReport.setSelectedResultIds(null);
    }

    SavedQueryEntity saveQuery = getSavedQueryService().saveQuery(name, memBean.getXmlQueryDescription(), saveReport, memBean.getReportType());
    options.setSavedQueryInfo(new SavedQueryEntityInfo(saveQuery, memBean.getReportType()));

    loadStoredXMLQueries(memBean);

    //display success message on screen
    requestContext.getFlashScope().put(FLUSHATTRIBUTE_SAVE_SUCCESSFUL_TRIGGER, "true");

    return memBean;
  }

  @Override
  public E saveQueryAs(E memBean, RequestContext requestContext) {
    memBean.setSaveAs(true);
    return saveQuery(memBean, requestContext);
  }

  private void loadStoredXMLQueries(E memBean) {
    if (ReportType.PIE.equals(memBean.getReportType()) || ReportType.BAR.equals(memBean.getReportType())) {
      List<SavedQuery> savedQueries = getSavedQueryService().getSavedQueriesWithoutContent(Sets.newHashSet(ReportType.PIE, ReportType.BAR));
      memBean.setSavedQueries(savedQueries);
    }
    else {
      memBean.setSavedQueries(getSavedQueryService().getSavedQueriesWithoutContent(memBean.getReportType()));
    }
  }

  /**
   * Needs to be implemented by subclasses
   */
  public abstract void generateGraphicFileResponse(E memBean, HttpServletRequest request, HttpServletResponse response);

  /**
   * Retrieves the necessary objects from the context and delegates to
   * {@link #generateGraphicFileResponse(ManageReportMemoryBean, HttpServletRequest, HttpServletResponse)}
   * 
   * @param memBean
   * @param context
   * @param flowContext
   */
  public void generateGraphicFileResponse(E memBean, RequestContext context, FlowExecutionContext flowContext) {
    memBean.getGraphicalOptions().validate();

    HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getNativeRequest();
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();
    generateGraphicFileResponse(memBean, request, response);

    context.getExternalContext().recordResponseComplete();
  }

  @Override
  public E refreshReport(E memBean, RequestContext context, FlowExecutionContext flowContext, boolean resetResults) {
    refreshGraphicalOptions(memBean);
    return super.refreshReport(memBean, context, flowContext, resetResults);
  }

  protected void refreshGraphicalOptions(E memBean) {
    // refresh options
    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(memBean.getSelectedBuildingBlock());

    getInitFormHelperService().refreshGraphicalExportColorOptions(memBean.getGraphicalOptions().getColorOptionsBean(), tobb);
    getInitFormHelperService().refreshGraphicalExportLineTypeOptions(memBean.getGraphicalOptions().getLineOptionsBean(), tobb);
  }

}

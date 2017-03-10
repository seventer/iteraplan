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

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.reports.query.options.CompositeReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Composite.CompositeDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.ExportService;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.businesslogic.service.SavedQueryService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.queries.SavedQueryEntity;
import de.iteratec.iteraplan.model.queries.SavedQueryEntityInfo;
import de.iteratec.iteraplan.model.xml.CompositeDiagramXML;
import de.iteratec.iteraplan.model.xml.ReportXML;
import de.iteratec.iteraplan.presentation.dialog.CommonFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.dialog.ReportBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicExportBean;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator;
import de.iteratec.svg.model.Document;


@Service("compositeGraphicFrontendService")
public class CompositeGraphicFrontendServiceImpl extends CommonFrontendServiceImpl implements CompositeGraphicFrontendService {

  @Autowired
  private InitFormHelperService initFormHelperService;

  @Autowired
  private ExportService         exportService;

  @Autowired
  private SavedQueryService     savedQueryService;

  public CompositeReportMemoryBean getInitialMemBean() {
    CompositeReportMemoryBean memBean = new CompositeReportMemoryBean();
    memBean.setSavedQueries(savedQueryService.getSavedQueriesWithoutContent(ReportType.COMPOSITE));

    final List<SavedQuery> partQueries = savedQueryService.getSavedQueriesWithoutContent(Sets.newHashSet(ReportType.PIE, ReportType.BAR));
    memBean.setAvailablePartQueries(partQueries);

    return memBean;
  }

  private void loadPartReportMemBean(CompositeReportMemoryBean memBean, Integer partReportId) {
    ManageReportMemoryBean partMemBean = initFormHelperService.getInitializedReportMemBeanByDialogPerms(UserContext.getCurrentPerms());
    partMemBean.setSavedQueryId(partReportId);

    SavedQuery savedQuery = savedQueryService.getSavedQuery(partReportId);
    partMemBean.setReportType(savedQuery.getType());
    ReportXML savedReport = savedQueryService.getSavedReport(savedQuery);

    partMemBean = initFormHelperService.initMemBeanFromSavedGraphicalReport(partMemBean, savedQuery, savedReport);

    memBean.addGraphicPartMemBean(partReportId, partMemBean);
  }

  public CompositeReportMemoryBean refreshAvailablePartQueries(CompositeReportMemoryBean memBean, RequestContext context,
                                                               FlowExecutionContext flowContext) {
    memBean.setAvailablePartQueries(savedQueryService.getSavedQueriesWithoutContent(Sets.newHashSet(ReportType.PIE, ReportType.BAR)));
    memBean.validateSelectedPartQueryIds();
    return memBean;
  }

  public void generateGraphicFileResponse(CompositeReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {

    HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getNativeRequest();
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();

    generateGraphicFileResponse(memBean, request, response);

    context.getExternalContext().recordResponseComplete();
  }

  public void generateGraphicFileResponse(CompositeReportMemoryBean memBean, HttpServletRequest request, HttpServletResponse response) {
    CompositeDiagramOptionsBean options = memBean.getCompositeOptions();

    if (options.getSelectedSavedQueryIds().isEmpty()) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.CLUSTER_NO_ROWS);
    }

    // Retrieve the application url
    String srvAddr = URLBuilder.getApplicationURL(request);
    options.setServerUrl(srvAddr);

    if (options.isSelectionChanged()) {
      memBean.getGraphicPartMemBeans().clear();
      // load part reports
      for (Integer savedQueryId : options.getSelectedSavedQueryIds()) {
        loadPartReportMemBean(memBean, savedQueryId);
      }
      options.setSelectionChanged(false);
    }

    // Generate
    Document doc = exportService.generateSvgCompositeDiagramExport(memBean.getGraphicPartMemBeans(), options);
    GraphicExportBean vBean = new GraphicExportBean(doc);

    GraphicsResponseGenerator respGen = exportService.getResponseGenerator(options.getSelectedGraphicFormat());
    // have the report file written to the HttpServletResponse
    respGen.generateResponse(response, vBean, GraphicsResponseGenerator.GraphicalReport.COMPOSITE, memBean.getContent());
  }

  public CompositeReportMemoryBean saveQuery(CompositeReportMemoryBean memBean, RequestContext requestContext) {
    boolean saveAsOption = memBean.isSaveAs();
    memBean.setSaveAs(false);

    Locale locale = UserContext.getCurrentLocale();
    CompositeDiagramXML saveReport = new CompositeDiagramXML();
    saveReport.initFrom(memBean, locale);

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
      checkIfQueryNameIsUsed(ReportType.COMPOSITE, name);
      acceptNewNameAndDescription(memBean);
    }

    SavedQueryEntity savedQuery = savedQueryService.saveCompositeDiagram(name, memBean.getXmlQueryDescription(), saveReport);

    memBean.getCompositeOptions().setSavedQueryInfo(new SavedQueryEntityInfo(savedQuery, ReportType.COMPOSITE));
    memBean.setSavedQueries(savedQueryService.getSavedQueriesWithoutContent(ReportType.COMPOSITE));

    //display success message on screen
    requestContext.getFlashScope().put(ReportBaseFrontendServiceImpl.FLUSHATTRIBUTE_SAVE_SUCCESSFUL_TRIGGER, "true");

    return memBean;
  }

  /**
   * {@inheritDoc}
   */
  public CompositeReportMemoryBean saveQueryAs(CompositeReportMemoryBean memBean, RequestContext requestContext) {
    memBean.setSaveAs(true);
    return saveQuery(memBean, requestContext);
  }

  private void checkIfQueryNameIsUsed(ReportType savedQueryType, String name) {
    if (savedQueryService.existsQuery(savedQueryType, name)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.SAVEDQUERY_NAME_IN_USE);
    }
  }

  protected void acceptNewNameAndDescription(CompositeReportMemoryBean memBean) {
    memBean.setXmlQueryName(memBean.getXmlSaveAsQueryName());
    memBean.setXmlQueryDescription(memBean.getXmlSaveAsQueryDescription());
    replaceSaveAsProperties(memBean);
  }

  private void replaceSaveAsProperties(CompositeReportMemoryBean memBean) {
    memBean.setXmlSaveAsQueryName("");
    memBean.setXmlSaveAsQueryDescription("");
  }

  public CompositeReportMemoryBean deleteSavedQuery(CompositeReportMemoryBean memBean) {
    if (null == memBean.getDeleteQueryId()) {
      throw new IteraplanTechnicalException();
    }

    savedQueryService.deleteSavedQuery(memBean.getDeleteQueryId());

    memBean.setSavedQueries(savedQueryService.getSavedQueriesWithoutContent(ReportType.COMPOSITE));

    return memBean;
  }

  public CompositeReportMemoryBean loadSavedQuery(CompositeReportMemoryBean memBean) {
    if (null == memBean.getSavedQueryId()) {
      throw new IteraplanTechnicalException();
    }
    CompositeDiagramOptionsBean compositeOptions = memBean.getCompositeOptions();

    SavedQuery savedQuery = savedQueryService.getSavedQuery(memBean.getSavedQueryId());
    CompositeDiagramXML savedReport = savedQueryService.getSavedCompositeDiagram(savedQuery);

    memBean.clearPartReports();
    compositeOptions.setSelectedSavedQueryIds(savedReport.getPartReports());
    refreshAvailablePartQueries(memBean, null, null);

    compositeOptions.setSelectedGraphicFormat(savedReport.getGraphicFormat());
    compositeOptions.setShowSavedQueryInfo(savedReport.isShowSavedQueryInfo());
    memBean.setXmlQueryName(savedQuery.getName());
    memBean.setXmlQueryDescription(savedQuery.getDescription());

    compositeOptions.setSavedQueryInfo(new SavedQueryEntityInfo(savedQuery, ReportType.COMPOSITE));
    memBean.setSavedQueryId(null);

    return memBean;
  }

  @Override
  protected String getFlowId() {
    return Dialog.GRAPHICAL_REPORTING_COMPOSITE.getFlowId();
  }

  public CompositeReportMemoryBean refreshOrderOfParts(CompositeReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    memBean.getCompositeOptions().refreshOrder();
    return memBean;
  }

}
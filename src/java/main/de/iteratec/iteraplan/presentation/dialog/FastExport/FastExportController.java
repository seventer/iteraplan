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
package de.iteratec.iteraplan.presentation.dialog.FastExport;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.reports.query.options.CompositeReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.IGraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.LandscapeOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ManageLandscapeDiagramMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.ExportService;
import de.iteratec.iteraplan.businesslogic.service.FastExportService;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.businesslogic.service.QueryService;
import de.iteratec.iteraplan.businesslogic.service.SavedQueryService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.xml.ReportXML;
import de.iteratec.iteraplan.presentation.PresentationHelper;
import de.iteratec.iteraplan.presentation.SessionConstants;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicExportBean;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicalReportBaseFrontendService;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.Composite.CompositeGraphicFrontendService;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.InformationFlow.InformationFlowGraphicFrontendService;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.Landscape.LandscapeDiagramFrontendService;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.Line.LineGraphicFrontendService;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.PieBar.PieBarGraphicFrontendService;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.cluster.ClusterGraphicFrontendService;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.masterplan.MasterplanGraphicFrontendService;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.portfolio.PortfolioGraphicFrontendService;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.vbb.VbbGraphicFrontendService;
import de.iteratec.iteraplan.presentation.dialog.TabularReporting.TabularReportingFrontendService;
import de.iteratec.iteraplan.presentation.problemreports.IteraplanProblemReport;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator.Content;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator.GraphicalReport;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator.ResultFormat;
import de.iteratec.iteraplan.presentation.responsegenerators.TabularResponseGenerator.TabularResultFormat;


@Controller
public class FastExportController {

  private static final Logger                   LOGGER         = Logger.getIteraplanLogger(FastExportController.class);

  @Autowired
  private InitFormHelperService                 initFormHelperService;

  @Autowired
  private QueryService                          queryService;

  @Autowired
  private ExportService                         exportService;

  @Autowired
  private FastExportService                     fastExportService;

  @Autowired
  private InformationFlowGraphicFrontendService infoflowFrontendService;

  @Autowired
  private PortfolioGraphicFrontendService       portfolioFrontService;

  @Autowired
  private ClusterGraphicFrontendService         clusterFrontendService;

  @Autowired
  private LineGraphicFrontendService            lineGraphicFrontendService;

  @Autowired
  private MasterplanGraphicFrontendService      masterplanFrontendService;

  @Autowired
  private LandscapeDiagramFrontendService       landscapeFrontendService;

  @Autowired
  private PieBarGraphicFrontendService          piebarFrontendService;

  @Autowired
  private CompositeGraphicFrontendService       compositeGraphicFrontendService;

  @Autowired
  @Qualifier("vbbClusterGraphicFrontendService")
  private VbbGraphicFrontendService             vbbClusterGraphicFrontendService;

  @Autowired
  @Qualifier("timelineGraphicFrontendService")
  private VbbGraphicFrontendService             timelineGraphicFrontendService;

  @Autowired
  @Qualifier("matrixGraphicFrontendService")
  private VbbGraphicFrontendService             matrixGraphicFrontendService;

  @Autowired
  private TabularReportingFrontendService       tabularReportFrontendService;

  @Autowired
  private SavedQueryService                     savedQueryService;

  private static final String                   ID             = "id";
  private static final String                   QUERYREFID     = "queryReferenceId";
  private static final String                   BBT            = "buildingBlockType";
  private static final String                   SAVEDQUERYTYPE = "savedQueryType";
  private static final String                   RESULTFORMAT   = "resultFormat";
  private static final String                   OUTPUTMODE     = "outputMode";
  private static final String                   DIAGRAMVARIANT = "diagramVariant";
  private static final String                   NAKEDEXPORT    = "nakedExport";
  private static final String                   WIDTH          = "width";
  private static final String                   HEIGHT         = "height";

  @RequestMapping
  public void generate(@RequestParam(value = ID, required = true) String bbeId, @RequestParam(value = BBT, required = true) String bbType,
                       @RequestParam(value = SAVEDQUERYTYPE, required = true) String savedQueryType,
                       @RequestParam(value = RESULTFORMAT, required = true) String resultFormat,
                       @RequestParam(value = OUTPUTMODE, required = true) String outputMode,
                       @RequestParam(value = DIAGRAMVARIANT, required = false) String diagramVariant, HttpServletRequest request,
                       HttpServletResponse response) {

    String serverUrl = URLBuilder.getApplicationURL(request);
    Integer id = PresentationHelper.parseId(bbeId);

    GraphicalReport graphicalReport = GraphicalReport.fromTypeString(savedQueryType);

    BuildingBlock startElement = fastExportService.getStartElement(id, bbType);

    // The actual graphic generation happens only inside the following
    GraphicExportBean vBean = generateExportBean(startElement, graphicalReport, resultFormat, serverUrl, diagramVariant);

    Content content = Content.fromValue(outputMode);
    if (Content.UNKNOWN.equals(content)) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }

    GraphicsResponseGenerator responseGen = getResponseGenerator(resultFormat);
    responseGen.generateResponse(response, vBean, graphicalReport, content);

  }

  /**
   * @param resultFormat
   *          The result format passed as parameter in the url.
   * @return The according graphical response generator, never <code>null</code>
   * @throws IteraplanTechnicalException if the response generator could not be instantiated or if the passed resultFormat is unknown.
   */
  private GraphicsResponseGenerator getResponseGenerator(String resultFormat) {

    GraphicsResponseGenerator responseGen;

    ResultFormat format = ResultFormat.fromString(resultFormat);

    if (!ResultFormat.UNKNOWN.equals(format)) {
      try {
        responseGen = format.getGeneratorClass().newInstance();

        return responseGen;

      } catch (InstantiationException e) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
      } catch (IllegalAccessException e) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
      }
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_UNKNOWN_RESULT_TYPE);
    }
  }

  public GraphicExportBean generateExportBean(BuildingBlock startElement, GraphicalReport graphicalReport, String resultFormat, String serverUrl,
                                              String diagramVariant) {

    ResultFormat format = ResultFormat.fromString(resultFormat);

    if (ResultFormat.UNKNOWN.equals(format)) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_UNKNOWN_RESULT_TYPE);
    }

    switch (graphicalReport) {
      case CLUSTER:
        // mind for Visio, because it is currently not supported in cluster graphics!
        if (ResultFormat.VISIO.equals(format)) {
          throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_UNKNOWN_RESULT_TYPE);
        }
        break;
      case INFORMATIONFLOW:
        return generateInformationFlowExportBean(startElement, format, serverUrl);
      case MASTERPLAN:
        return generateMasterplanExportBean(startElement, format, serverUrl, diagramVariant);
      case LANDSCAPE:
        return generateLandscapeExportBean(startElement, format, serverUrl, diagramVariant);
      case PORTFOLIO:
        break;
      case NEIGHBORHOOD:
        return generateNeighborhoodDiagramExportBean(startElement, serverUrl, format);
      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
    return null;

  }

  @SuppressWarnings("PMD.MissingBreakInSwitch")
  // PMD seems to dislike intentional fall-through in switch blocks
  @ExceptionHandler(Throwable.class)
  public ModelAndView handleIteraplanException(Throwable ex, HttpServletRequest req, HttpServletResponse resp) {
    ModelAndView mav = new ModelAndView("errorOutsideFlow");
    if (ex instanceof IteraplanException) {
      IteraplanException itex = (IteraplanException) ex;
      switch (itex.getErrorCode()) {
        case IteraplanErrorMessages.LANDSCAPE_NO_ELEMENTS:
        case IteraplanErrorMessages.MASTERPLAN_NO_ELEMENTS:
        case IteraplanErrorMessages.FLOW_NO_ELEMENTS: // this error and the next are exactly the same?
        case IteraplanErrorMessages.INFORMATIONFLOW_NO_ELEMENTS:
        case IteraplanErrorMessages.PORTFOLIO_NO_ELEMENTS:
        case IteraplanErrorMessages.LINE_NO_ELEMENTS:
        case IteraplanErrorMessages.CLUSTER_NO_ELEMENTS:
          LOGGER.debug("Handling a business exception that indicates an empty diagram; setting status code to 204");
          resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // status code 204
          return mav;

        default:
          LOGGER.error("During context graphic creation, an error occurred", ex);
      }
    }
    else {
      LOGGER.error("Last resort catching an unhandled Exception", ex);
    }

    IteraplanProblemReport.createFromController(ex, req);

    mav.addObject(Constants.JSP_ATTRIBUTE_EXCEPTION_MESSAGE, ex.getLocalizedMessage());
    return mav;
  }

  private GraphicExportBean generateMasterplanExportBean(BuildingBlock startElement, ResultFormat resultFormat, String serverUrl,
                                                         String diagramVariant) {

    MasterplanOptionsBean options = fastExportService.initMasterplanOptionsForFastExport(serverUrl);

    List<BuildingBlock> elementsDisplayed = fastExportService.retrieveBuildingBlockListForMasterplanFastExport(startElement, options, diagramVariant);

    RuntimePeriod runtimePeriod = fastExportService.getEncompassingRuntimePeriod(elementsDisplayed);

    fastExportService.configureMasterplanOptionsForFastExport(options, runtimePeriod);
    options.setLegend(false);

    switch (resultFormat) {
      case VISIO:
        de.iteratec.visio.model.Document visioDoc = exportService.generateVisioMasterplanExport(elementsDisplayed, options);
        return new GraphicExportBean(visioDoc);
      case UNKNOWN:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_UNKNOWN_RESULT_TYPE);
      default:
        de.iteratec.svg.model.Document doc = exportService.generateSvgMasterplanExport(elementsDisplayed, options);
        return new GraphicExportBean(doc);
    }

  }

  private GraphicExportBean generateInformationFlowExportBean(BuildingBlock startElement, ResultFormat resultFormat, String serverUrl) {

    List<InformationSystemRelease> releases = fastExportService.getInformationFlowReleases(startElement);

    if (releases.size() < 1) { // Must have at least one, otherwise there is no picture at all, and
      // Configure will complain later

      throw new IteraplanBusinessException(IteraplanErrorMessages.INFORMATIONFLOW_NO_ELEMENTS);
    }

    InformationFlowOptionsBean options = fastExportService.retrieveInformationFlowOptionsForFastExport();

    options.setServerUrl(serverUrl);
    options.setNakedExport(true); // No title
    options.setLegend(false); // No legend

    switch (resultFormat) {
      case VISIO:
        de.iteratec.visio.model.Document visioDoc = exportService.generateVisioInformationFlowDoc(releases, null, null, false, options);
        return new GraphicExportBean(visioDoc);
      case UNKNOWN:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_UNKNOWN_RESULT_TYPE);
      default:
        de.iteratec.svg.model.Document doc = exportService.generateSvgInformationFlowDoc(releases, null, null, false, options);
        return new GraphicExportBean(doc);
    }

  }

  private GraphicExportBean generateLandscapeExportBean(BuildingBlock startElement, ResultFormat resultFormat, String serverUrl, String diagramVariant) {

    ServiceHelper.init(initFormHelperService, queryService);

    LandscapeDiagramHelper<? extends BuildingBlock> landscapeHelper = fastExportService.retrieveLandscapeHelper(diagramVariant, startElement);

    LandscapeOptionsBean options = fastExportService.retrieveLandscapeOptionsForFastExport();

    options.setServerUrl(serverUrl);
    options.setNakedExport(true);
    options.setLegend(false);

    switch (resultFormat) {
      case VISIO:
        de.iteratec.visio.model.Document visioDoc = exportService.generateVisioLandscapeDiagram(landscapeHelper.getConfig(), options);
        return new GraphicExportBean(visioDoc);
      case UNKNOWN:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_UNKNOWN_RESULT_TYPE);
      default:
        de.iteratec.svg.model.Document doc = exportService.generateSvgLandscapeDiagram(landscapeHelper.getConfig(), options);
        return new GraphicExportBean(doc);
    }
  }

  /**
   * @param startElement
   * @param serverUrl 
   * @param format
   * @param serverUrl
   * @return
   *    A bean containing the generated neighborhood graphic.
   */
  private GraphicExportBean generateNeighborhoodDiagramExportBean(BuildingBlock startElement, String serverUrl, ResultFormat format) {

    switch (format) {
      case VISIO:
        de.iteratec.visio.model.Document visioDoc = exportService.generateVisioNeighborhoodDiagram(startElement);
        return new GraphicExportBean(visioDoc);
      case UNKNOWN:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_UNKNOWN_RESULT_TYPE);
      default:
        de.iteratec.svg.model.Document doc = exportService.generateSvgNeighborhoodDiagram(startElement, serverUrl);
        return new GraphicExportBean(doc);
    }

  }

  @RequestMapping
  public void generateSavedQuery(@RequestParam(value = ID, required = true) String queryId,
                                 @RequestParam(value = QUERYREFID, required = false) String queryRefID,
                                 @RequestParam(value = SAVEDQUERYTYPE, required = true) String savedQueryType,
                                 @RequestParam(value = OUTPUTMODE, required = true) String outputMode,
                                 @RequestParam(value = RESULTFORMAT, required = false) String resultFormat,
                                 @RequestParam(value = NAKEDEXPORT, required = false) String nakedExport,
                                 @RequestParam(value = WIDTH, required = false) String widthParam,
                                 @RequestParam(value = HEIGHT, required = false) String heightParam, HttpServletRequest request,
                                 HttpServletResponse response) {

    Integer id = PresentationHelper.parseId(queryId);
    Integer queryIdRef = PresentationHelper.parseId(queryRefID);
    Boolean nakedExp = Boolean.valueOf(nakedExport);
    Double width = null;
    Double height = null;

    //validate width parameter
    try {
      if (widthParam != null) {
        width = Double.valueOf(widthParam);
      }
    } catch (Exception e) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INVALID_REQUEST_PARAMETER, WIDTH);
    }

    //validate height parameter
    try {
      if (heightParam != null) {
        height = Double.valueOf(heightParam);
      }
    } catch (Exception e) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INVALID_REQUEST_PARAMETER, HEIGHT);
    }

    // validate outputMode parameter
    Content content = Content.fromValue(outputMode);
    if (content.equals(Content.UNKNOWN)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INVALID_REQUEST_PARAMETER, OUTPUTMODE);
    }

    // if this is a graphical report
    GraphicalReport graphicalReport = GraphicalReport.fromTypeOrValueString(savedQueryType);
    if (!graphicalReport.equals(GraphicalReport.UNKNOWN)) {
      generateExport(id, queryIdRef, content, resultFormat, graphicalReport, nakedExp, width, height, request, response);
    }
    // if this is a tabular report
    else if (TypeOfBuildingBlock.getTypeOfBuildingBlockByString(savedQueryType) != null) {
      doTabularExport(id, content, resultFormat, TypeOfBuildingBlock.getTypeOfBuildingBlockByString(savedQueryType).getPluralValue(), request,
          response);
    }
    else {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INVALID_REQUEST_PARAMETER, SAVEDQUERYTYPE);
    }
  }

  private void doTabularExport(Integer id, Content content, String resultFormat, String buildingBlockType, HttpServletRequest request,
                               HttpServletResponse response) {
    ManageReportMemoryBean mBean = initFormHelperService.getInitializedReportMemBeanByViewPerms(buildingBlockType, Constants.ALL_TYPES_FOR_DISPLAY);
    mBean.setSavedQueryId(id);
    mBean = tabularReportFrontendService.loadSavedQuery(mBean);
    mBean.setContent(content);

    // validate and set resultFormat
    if (resultFormat != null) {
      TabularResultFormat format = TabularResultFormat.fromString(resultFormat);

      if (format.equals(TabularResultFormat.UNKNOWN)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.INVALID_REQUEST_PARAMETER, RESULTFORMAT);
      }
      else {
        mBean.getTabularOptions().setResultFormat(format.getResultFormat());
      }
    }

    // do HTML export if the result format is not defined at all (default to HTML) or explicitly set
    // to HTML
    if ((mBean.getTabularOptions().getResultFormat() == null)
        || TabularResultFormat.HTML.getResultFormat().equals(mBean.getTabularOptions().getResultFormat())) {
      try {
        response.sendRedirect("../tabularreporting?" + ID + "=" + id + "&" + SessionConstants.FLOW_EVENT_ID + "=directRequest");
      } catch (IOException e) {
        LOGGER.error("Failed to redirect to tabular reporting page!", e);
      }
    }
    tabularReportFrontendService.requestReport(mBean, request, response);
  }

  /**
   * Finds the needed service to generate the export based on the provided graphicalReport and then
   * loads the saved query of the provided id and executes the generation logic.
   * 
   * If there is specified an reference ID (queryRefID), the saved Query (diagram) is loaded
   * with the result from the referenced saved Query (from a tabular reporting). 
   * 
   * @param id
   * @param queryRefId
   * @param graphicalReport
   * @param content
   * @param request
   * @param response
   */
  private void generateExport(Integer id, Integer queryRefId, Content content, String resultFormat, GraphicalReport graphicalReport,
                              Boolean nakedExport, Double width, Double height, HttpServletRequest request, HttpServletResponse response) {
    ResultFormat format = ResultFormat.UNKNOWN;
    if (resultFormat != null) {
      format = ResultFormat.fromString(resultFormat);
      if (format.equals(ResultFormat.UNKNOWN)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.INVALID_REQUEST_PARAMETER, RESULTFORMAT);
      }
    }

    switch (graphicalReport) {
      case CLUSTER:
        doExport(id, queryRefId, content, format, nakedExport, width, height, request, response, clusterFrontendService);
        break;
      case INFORMATIONFLOW:
        doExport(id, queryRefId, content, format, nakedExport, width, height, request, response, infoflowFrontendService);
        break;
      case MASTERPLAN:
        doExport(id, queryRefId, content, format, nakedExport, width, height, request, response, masterplanFrontendService);
        break;
      case LANDSCAPE:
        doExport(id, queryRefId, content, format, nakedExport, width, height, request, response, landscapeFrontendService);
        break;
      case PORTFOLIO:
        doExport(id, queryRefId, content, format, nakedExport, width, height, request, response, portfolioFrontService);
        break;
      case PIE:
      case BAR:
        doExport(id, queryRefId, content, format, nakedExport, width, height, request, response, piebarFrontendService);
        break;
      case LINE:
        doExport(id, queryRefId, content, format, nakedExport, width, height, request, response, lineGraphicFrontendService);
        break;
      case COMPOSITE:
        doExport(id, content, format, width, height, request, response, compositeGraphicFrontendService);
        break;
      case VBBCLUSTER:
        // it is not possible to change the query result for vbbcluster -> currently they have no filter option
        doExport(id, null, content, format, nakedExport, width, height, request, response, vbbClusterGraphicFrontendService);
        break;
      case TIMELINE:
        doExport(id, queryRefId, content, format, nakedExport, width, height, request, response, timelineGraphicFrontendService);
        break;
      case MATRIX:
        doExport(id, queryRefId, content, format, nakedExport, width, height, request, response, matrixGraphicFrontendService);
        break;
      default:
        throw new IteraplanBusinessException(IteraplanErrorMessages.INVALID_REQUEST_PARAMETER, SAVEDQUERYTYPE);
    }
  }

  /**
   * Loads the saved query referenced by id and generates the export with the provided service.
   * If there is specified an reference ID, the saved Query (diagram) is loaded with the result
   * from the referenced saved Query (from a tabular reporting). 
   * 
   * @param id
   * @param queryRefId
   * @param content
   * @param request
   * @param response
   * @param service
   */
  private void doExport(Integer id, Integer queryRefId, Content content, ResultFormat format, Boolean nakedExport, Double width, Double height,
                        HttpServletRequest request, HttpServletResponse response, GraphicalReportBaseFrontendService<ManageReportMemoryBean> service) {
    ManageReportMemoryBean mBean = service.getInitialMemBean();
    mBean.setSavedQueryId(id);
    mBean = service.loadSavedQuery(mBean);
    mBean.setContent(content);

    // this if condition changed the query result, if a queryRefId is specified
    if (queryRefId != null && queryRefId.intValue() != -1) {
      SavedQuery savedQuery = savedQueryService.getSavedQuery(queryRefId);
      if (savedQuery.getResultBbType().getName().equals(mBean.getSelectedBuildingBlock())) {
        ReportXML reportXML = savedQueryService.getSavedReport(savedQuery);
        initFormHelperService.loadQueryResults(mBean, reportXML, mBean.getReportType());
      }
      else {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.QUERIES_NOT_COMPATIBLE);
      }
    }

    IGraphicalExportBaseOptions graphicalOptions = mBean.getGraphicalOptions();

    if (!ResultFormat.UNKNOWN.equals(format)) {
      graphicalOptions.setSelectedGraphicFormat(format.getGraphicFormat());
    }
    graphicalOptions.setNakedExport(nakedExport.booleanValue());
    graphicalOptions.setHeight(height);
    graphicalOptions.setWidth(width);

    if (nakedExport.booleanValue()) {
      graphicalOptions.setSavedQueryInfo(null);
    }

    service.generateGraphicFileResponse(mBean, request, response);
  }

  private void doExport(Integer id, Content content, ResultFormat format, Double width, Double height, HttpServletRequest request,
                        HttpServletResponse response, CompositeGraphicFrontendService service) {
    CompositeReportMemoryBean mBean = service.getInitialMemBean();
    mBean.setSavedQueryId(id);
    mBean = service.loadSavedQuery(mBean);
    mBean.setContent(content);

    if (!ResultFormat.UNKNOWN.equals(format)) {
      mBean.getCompositeOptions().setSelectedGraphicFormat(format.getGraphicFormat());
    }

    mBean.getCompositeOptions().setWidth(width);
    mBean.getCompositeOptions().setHeight(height);

    service.generateGraphicFileResponse(mBean, request, response);
  }

  /**
   * Loads the saved query referenced by id and generates the export with the provided service.
   * If there is specified an reference ID, the saved Query (diagram) is loaded with the result
   * from the referenced saved Query (from a tabular reporting). 
   * 
   * @param id
   * @param queryRefId
   * @param content
   * @param request
   * @param response
   * @param service
   */
  private void doExport(Integer id, Integer queryRefId, Content content, ResultFormat format, Boolean nakedExport, Double width, Double height,
                        HttpServletRequest request, HttpServletResponse response, LandscapeDiagramFrontendService service) {
    ManageLandscapeDiagramMemoryBean mBean = service.getInitialMemBean();
    mBean.setSavedQueryId(id);
    mBean = service.loadSavedQuery(mBean, queryRefId);
    mBean.setContent(content);

    LandscapeOptionsBean optionsBean = mBean.getGraphicalOptions();

    if (!ResultFormat.UNKNOWN.equals(format)) {
      optionsBean.setSelectedGraphicFormat(format.getGraphicFormat());
    }
    optionsBean.setNakedExport(nakedExport.booleanValue());
    if (nakedExport.booleanValue()) {
      optionsBean.setSavedQueryInfo(null);
    }
    optionsBean.setHeight(height);
    optionsBean.setWidth(width);

    service.generateGraphicFileResponse(mBean, request, response);
  }
}

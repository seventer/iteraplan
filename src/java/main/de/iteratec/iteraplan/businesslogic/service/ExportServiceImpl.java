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
package de.iteratec.iteraplan.businesslogic.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.LandscapeDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelAdditionalQueryData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.msproject.MsProjectExport;
import de.iteratec.iteraplan.businesslogic.exchange.msproject.MsProjectExporterBase.ExportType;
import de.iteratec.iteraplan.businesslogic.exchange.msproject.MsProjectExporterFactory;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.businesslogic.exchange.visio.landscape.VisioLandscapeDiagramExport;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Composite.CompositeDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ILandscapeOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.IMasterplanOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.IPortfolioOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.IQStatusData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QTimespanData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.legacyExcel.ExcelExportService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.Sequence;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.dto.LandscapeDiagramConfigDTO;
import de.iteratec.iteraplan.model.dto.ReleaseSuccessorDTO.SuccessionContainer;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator;
import de.iteratec.iteraplan.presentation.responsegenerators.JPEGResponseGenerator;
import de.iteratec.iteraplan.presentation.responsegenerators.PDFResponseGenerator;
import de.iteratec.iteraplan.presentation.responsegenerators.PNGResponseGenerator;
import de.iteratec.iteraplan.presentation.responsegenerators.SVGResponseGenerator;
import de.iteratec.iteraplan.presentation.responsegenerators.VisioResponseGenerator;
import de.iteratec.visio.model.Document;


/**
 * This is the facade for Visio, Excel and other export formats. It provides methods for generating
 * report results for different graphical and textual export formats.
 */
public class ExportServiceImpl implements ExportService {
  private QueryService                                                          queryService;
  private ExcelExportService                                                    excelExportService;
  private SvgExportService                                                      svgExportService;
  private VisioExportService                                                    visioExportService;
  private AttributeTypeDAO                                                      attributeTypeDAO;
  private GeneralBuildingBlockService                                           generalBuildingBlockService;
  private AttributeTypeService                                                  attributeTypeService;
  private AttributeValueService                                                 attributeValueService;
  private CsvExportService                                                      csvExportService;

  /**
   * Maps TypeOfBuildingBlocks you can create queries for to their class
   * used in {@link #getExcelExportByType(Node, List, DynamicQueryFormData, String)}
   */
  private static final Map<TypeOfBuildingBlock, Class<? extends BuildingBlock>> TOBB_TO_CLASS = CollectionUtils.hashMap();
  static {
    TOBB_TO_CLASS.put(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, InformationSystemRelease.class);
    TOBB_TO_CLASS.put(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, TechnicalComponentRelease.class);
    TOBB_TO_CLASS.put(TypeOfBuildingBlock.PROJECT, Project.class);
    TOBB_TO_CLASS.put(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, InformationSystemInterface.class);
    TOBB_TO_CLASS.put(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, InfrastructureElement.class);
    TOBB_TO_CLASS.put(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, InformationSystemDomain.class);
    TOBB_TO_CLASS.put(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, ArchitecturalDomain.class);
    TOBB_TO_CLASS.put(TypeOfBuildingBlock.BUSINESSOBJECT, BusinessObject.class);
    TOBB_TO_CLASS.put(TypeOfBuildingBlock.BUSINESSDOMAIN, BusinessDomain.class);
    TOBB_TO_CLASS.put(TypeOfBuildingBlock.BUSINESSPROCESS, BusinessProcess.class);
    TOBB_TO_CLASS.put(TypeOfBuildingBlock.BUSINESSFUNCTION, BusinessFunction.class);
    TOBB_TO_CLASS.put(TypeOfBuildingBlock.PRODUCT, Product.class);
    TOBB_TO_CLASS.put(TypeOfBuildingBlock.BUSINESSUNIT, BusinessUnit.class);
  }

  /**
   * {@inheritDoc}
   */
  public int[] determineLevels(final List<? extends BuildingBlock> elements) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);

    return queryService.determineLevels(elements);
  }

  /**
   * {@inheritDoc}
   */
  public Document generateVisioInformationFlowDoc(final List<InformationSystemRelease> isReleases, final List<InformationSystemInterface> interfaces,
                                                  final List<BusinessObject> businessObjects, final boolean ppConnectionMerging,
                                                  final InformationFlowOptionsBean options) {

    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);

    return visioExportService.generateVisioInformationFlowExport(isReleases, interfaces, businessObjects, ppConnectionMerging, options);
  }

  /**
   * {@inheritDoc}
   */
  public de.iteratec.svg.model.Document generateSvgInformationFlowDoc(final List<InformationSystemRelease> isReleases,
                                                                      final List<InformationSystemInterface> interfaces,
                                                                      final List<BusinessObject> businessObjects, final boolean ppConnectionMerging,
                                                                      final InformationFlowOptionsBean informationFlowOptions) {

    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);

    return svgExportService.generateSvgInformationFlowExport(isReleases, interfaces, businessObjects, ppConnectionMerging, informationFlowOptions);
  }

  /**
   * {@inheritDoc}
   */
  public Document generateVisioMasterplanExport(final List<? extends BuildingBlock> buildingBlocks, final IMasterplanOptions options) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);

    return visioExportService.generateVisioMasterplanExport(buildingBlocks, options);
  }

  /**
   * {@inheritDoc}
   */
  public de.iteratec.svg.model.Document generateSvgMasterplanExport(final List<? extends BuildingBlock> buildingBlocks,
                                                                    final IMasterplanOptions options) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);

    return svgExportService.generateSvgMasterplanExport(buildingBlocks, options);
  }

  /**
  * {@inheritDoc}
  */
  public Document generateVisioLandscapeDiagram(final LandscapeDiagramConfigDTO config, final ILandscapeOptions options) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);
    LandscapeDiagramCreator mc = new LandscapeDiagramCreator(config, attributeTypeDAO, generalBuildingBlockService);
    VisioLandscapeDiagramExport me = new VisioLandscapeDiagramExport(mc.createLandscapeDiagram(), options, attributeTypeService,
        attributeValueService);

    return me.createDiagram();
  }

  /**
   * {@inheritDoc}
   */
  public de.iteratec.svg.model.Document generateSvgLandscapeDiagram(final LandscapeDiagramConfigDTO config, ILandscapeOptions options) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);

    return svgExportService.generateSvgLandscapeDiagramExport(config, options);
  }

  /**
   * {@inheritDoc}
   */
  public Document generateVisioPortfolioExport(final List<? extends BuildingBlock> buildingBlocks, final IPortfolioOptions portfolioOptions) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);

    return visioExportService.generateVisioPortfolioExport(buildingBlocks, portfolioOptions);
  }

  /**
   * {@inheritDoc}
   */
  public de.iteratec.svg.model.Document generateSvgPortfolioExport(final List<? extends BuildingBlock> buildingBlocks,
                                                                   final IPortfolioOptions portfolioOptions) {

    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);

    return svgExportService.generateSvgPortfolioExport(buildingBlocks, portfolioOptions);
  }

  /**
   * {@inheritDoc}
   */
  public de.iteratec.svg.model.Document generateSvgClusterExport(final List<? extends BuildingBlock> buildingBlocks, final ClusterOptionsBean options) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);

    return svgExportService.generateSvgClusterExport(buildingBlocks, options);
  }

  /**
   * {@inheritDoc}
   */
  public de.iteratec.svg.model.Document generateSvgPieBarDiagramExport(final List<? extends BuildingBlock> buildingBlocks,
                                                                       final PieBarDiagramOptionsBean options) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);

    return svgExportService.generateSvgPieBarDiagramExport(buildingBlocks, options);
  }

  /**
   * {@inheritDoc}
   */
  public de.iteratec.svg.model.Document generateSvgCompositeDiagramExport(final List<ManageReportMemoryBean> memBeans,
                                                                          final CompositeDiagramOptionsBean options) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);

    return svgExportService.generateSvgCompositeDiagramExport(memBeans, options);
  }

  /**{@inheritDoc}**/
  public Document generateVisioNeighborhoodDiagram(BuildingBlock objectOfInterest) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);
    return visioExportService.generateVisioNeighborhoodDiagramExport(objectOfInterest);
  }

  /**{@inheritDoc}**/
  public de.iteratec.svg.model.Document generateSvgNeighborhoodDiagram(BuildingBlock objectOfInterest, String serverUrl) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);
    return svgExportService.generateSvgNeighborhoodDiagramExport(objectOfInterest, serverUrl);
  }

  /** {@inheritDoc} */
  public String getCsvExportNew(final Node node, TimeseriesQuery tsQuery,
                                final List<AbstractPostprocessingStrategy<? extends BuildingBlock>> postProcessingStrategies) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.TABULAR_REPORTING);
    List<AbstractPostprocessingStrategy<InformationSystemRelease>> disposeOfWildcard = queryService.disposeOfWildcard(postProcessingStrategies,
        InformationSystemRelease.class);
    List<InformationSystemRelease> isReleases = queryService.evaluateQueryTree(node, tsQuery, disposeOfWildcard);

    return csvExportService.createCsvExport(isReleases);
  }

  /**
   * {@inheritDoc}
   */
  public ExportWorkbook getReleaseSuccessorExcelExport(List<SuccessionContainer<? extends Sequence<?>>> releaseSuccession,
                                                       TypeOfBuildingBlock releaseType, String serverURL, TemplateType templateType) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.SUCCESSORREPORT);
    UserContext.getCurrentPerms().assureFunctionalPermission(releaseType);

    return excelExportService.getReleaseSuccessionReport(UserContext.getCurrentLocale(), releaseSuccession, releaseType, serverURL, templateType);
  }

  /**
   * {@inheritDoc}
   */
  public <T extends BuildingBlock> ExportWorkbook getExcelExportByType(Node node, TimeseriesQuery tsQuery,
                                                                       List<AbstractPostprocessingStrategy<? extends BuildingBlock>> ppStrategies,
                                                                       DynamicQueryFormData<?> form, String serverURL, TemplateType templateType) {
    return getExcelExportByType(node, tsQuery, ppStrategies, form, serverURL, templateType, "");
  }

  /**
   * {@inheritDoc}
   */
  public <T extends BuildingBlock> ExportWorkbook getExcelExportByType(Node node, TimeseriesQuery tsQuery,
                                                                       List<AbstractPostprocessingStrategy<? extends BuildingBlock>> ppStrategies,
                                                                       DynamicQueryFormData<?> form, String serverURL, TemplateType templateType,
                                                                       String templateFileName) {
    //nullpointer check for templateFileName  
    String validatedTemplateFileName = templateFileName == null ? "" : templateFileName;

    TypeOfBuildingBlock tobb = ((Type<?>) form.getType()).getTypeOfBuildingBlock();
    if (!TOBB_TO_CLASS.containsKey(tobb)) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    ExcelAdditionalQueryData queryData = null;
    if (TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.equals(tobb) || TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE.equals(tobb)) {
      queryData = getQueryDataForReleases(form);
    }
    else if (TypeOfBuildingBlock.PROJECT.equals(tobb)) {
      QTimespanData timespanQueryData = form.getQueryUserInput().getTimespanQueryData();

      // set the data that should be exported to the excel sheet
      queryData = new ExcelAdditionalQueryData();
      queryData.setStartDate(timespanQueryData.getStartDateAsString());
      queryData.setEndDate(timespanQueryData.getEndDateAsString());
    }

    List<? extends BuildingBlock> results = queryService.evaluateQueryTree(node, tsQuery,
        queryService.disposeOfWildcard(ppStrategies, TOBB_TO_CLASS.get(tobb)));

    return this.excelExportService.getBuildingBlockReport(UserContext.getCurrentLocale(), queryData, results, tobb, serverURL, templateType,
        validatedTemplateFileName);
  }

  private ExcelAdditionalQueryData getQueryDataForReleases(DynamicQueryFormData<?> form) {
    IQStatusData statusQueryData = form.getQueryUserInput().getStatusQueryData();
    QTimespanData timespanQueryData = form.getQueryUserInput().getTimespanQueryData();

    // set the data that should be exported to the excel sheet
    ExcelAdditionalQueryData queryData = new ExcelAdditionalQueryData();
    queryData.setStatusList(statusQueryData.exportCheckedStatusList());
    queryData.setStartDate(timespanQueryData.getStartDateAsString());
    queryData.setEndDate(timespanQueryData.getEndDateAsString());
    return queryData;
  }

  public MsProjectExport getMsProjectExport(List<? extends BuildingBlock> results, HttpServletRequest request, DynamicQueryFormData<?> form,
                                            ExportType exportType) {

    Type<?> type = form.getType();
    return MsProjectExporterFactory.getExport(results, request, type, exportType);
  }

  public GraphicsResponseGenerator getResponseGenerator(String selectedExportFormat) {
    GraphicsResponseGenerator respGen = null;

    if (selectedExportFormat.equalsIgnoreCase(Constants.REPORTS_EXPORT_GRAPHICAL_VISIO)) {
      respGen = new VisioResponseGenerator();
    }
    else if (selectedExportFormat.equalsIgnoreCase(Constants.REPORTS_EXPORT_GRAPHICAL_SVG)) {
      respGen = new SVGResponseGenerator();
    }
    else if (selectedExportFormat.equalsIgnoreCase(Constants.REPORTS_EXPORT_GRAPHICAL_JPEG)) {
      respGen = new JPEGResponseGenerator();
    }
    else if (selectedExportFormat.equalsIgnoreCase(Constants.REPORTS_EXPORT_GRAPHICAL_PNG)) {
      respGen = new PNGResponseGenerator();
    }
    else if (selectedExportFormat.equalsIgnoreCase(Constants.REPORTS_EXPORT_GRAPHICAL_PDF)) {
      respGen = new PDFResponseGenerator();
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    return respGen;
  }

  public void setQueryService(QueryService queryService) {
    this.queryService = queryService;
  }

  public void setExcelExportService(ExcelExportService excelExportService) {
    this.excelExportService = excelExportService;
  }

  public void setSvgExportService(SvgExportService svgExportService) {
    this.svgExportService = svgExportService;
  }

  public void setAttributeTypeDAO(AttributeTypeDAO attributeTypeDAO) {
    this.attributeTypeDAO = attributeTypeDAO;
  }

  public void setGeneralBuildingBlockService(GeneralBuildingBlockService generalBuildingBlockService) {
    this.generalBuildingBlockService = generalBuildingBlockService;
  }

  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }

  public void setAttributeValueService(AttributeValueService attributeValueService) {
    this.attributeValueService = attributeValueService;
  }

  public void setVisioExportService(VisioExportService visioExportService) {
    this.visioExportService = visioExportService;
  }

  public void setCsvExportService(CsvExportService csvExportService) {
    this.csvExportService = csvExportService;
  }

}
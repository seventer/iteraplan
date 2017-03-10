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

import javax.servlet.http.HttpServletRequest;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.msproject.MsProjectExport;
import de.iteratec.iteraplan.businesslogic.exchange.msproject.MsProjectExporterBase.ExportType;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
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
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.HideSubInformationSystemReleasesWithConnectionMergingStrategy;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Sequence;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.dto.LandscapeDiagramConfigDTO;
import de.iteratec.iteraplan.model.dto.ReleaseSuccessorDTO.SuccessionContainer;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator;
import de.iteratec.visio.model.Document;


/**
 * This is the facade for Visio, Excel and other export formats. It provides methods for
 * generating report results for different graphical and textual export formats.
 * 
 * @author Karsten Voges - iteratec GmbH
 */
public interface ExportService {

  /**
   * Checks permissions and returns a generated Visio document for the given List of IS releases.
   * 
   * @param isReleases the list of IS releases to show in the diagram.
   * @param interfaces a list of interfaces, which should be shown on the diagram (as edges). If this is null, all interfaces will be shown.
   * @param ppConnectionMerging true if the post processing strategy {@link HideSubInformationSystemReleasesWithConnectionMergingStrategy}
   *          is to be applied.
   * @param informationFlowOptions the configuration options (dimension) for the informationFlow diagram.
   * @return A generated Visio document instance that can write to an OutputStream.
   */
  de.iteratec.visio.model.Document generateVisioInformationFlowDoc(final List<InformationSystemRelease> isReleases,
                                                                   final List<InformationSystemInterface> interfaces,
                                                                   final List<BusinessObject> businessObjects, final boolean ppConnectionMerging,
                                                                   final InformationFlowOptionsBean informationFlowOptions);

  /**
   * Checks permissions and returns a generated SVG document for the given List of IS releases.
   * 
   * @param isReleases List of IS releases to show in the diagram.
   * @param interfaces a list of interfaces, which should be shown on the diagram (as edges). If this is null, all interfaces will be shown.
   * @param ppConnectionMerging true iff the post processing strategy {@link HideSubInformationSystemReleasesWithConnectionMergingStrategy}
   *          is to be applied.
   * @param informationFlowOptions The configuration options (dimension) for the informationFlow diagram.
   * @return A generated SVG document instance that can write to an OutputStream.
   */
  de.iteratec.svg.model.Document generateSvgInformationFlowDoc(final List<InformationSystemRelease> isReleases,
                                                               final List<InformationSystemInterface> interfaces,
                                                               final List<BusinessObject> businessObjects, final boolean ppConnectionMerging,
                                                               final InformationFlowOptionsBean informationFlowOptions);

  /**
   * Checks permissions and generates a Visio document instance for the given List of building
   * blocks and diagram options.
   * 
   * @param buildingBlocks the building blocks to include in the diagram.
   * @param portfolioOptions the configuration options (dimensions) for the portfolio diagram.
   * @return A generated Visio document instance that can write to a given OutputStream.
   */
  de.iteratec.visio.model.Document generateVisioPortfolioExport(final List<? extends BuildingBlock> buildingBlocks,
                                                                final IPortfolioOptions portfolioOptions);

  /**
   * Checks permissions and returns a generated Visio document for the given configuration.
   * 
   * @param config the landscape diagram configuration.
   * @return A generated Visio document instance that can write to an OutputStream.
   */
  de.iteratec.visio.model.Document generateVisioLandscapeDiagram(final LandscapeDiagramConfigDTO config, ILandscapeOptions options);

  /**
   * Checks permissions and returns a generated SVG Document for the given configuration.
   * 
   * @param config the landscape diagram configuration.
   * @param options the options that standardize the diagram with the dimension export. This would be an
   *          override for the {@link de.iteratec.iteraplan.businesslogic.exchange.visio.landscape.ShapeConfigurator}s
   *          that are used with the Visio generation and would provide compatibility with the
   *          {@link de.iteratec.iteraplan.businesslogic.exchange.visio.VisioDimensionExport}.
   * @return A generated SVG document instance that can write to an OutputStream.
   */
  de.iteratec.svg.model.Document generateSvgLandscapeDiagram(final LandscapeDiagramConfigDTO config, ILandscapeOptions options);

  /**
   * @param elements the elements for which to determine the levels
   * @return An int array that contains:
   *         <ul>
   *         <li>The highest level of the given elements</li>
   *         <li>The lowest level of the given elements</li>
   *         </ul>
   */
  int[] determineLevels(final List<? extends BuildingBlock> elements);

  /**
   * Creates an Excel workbook from the given list of releases (parameter <code>releaseSuccession</code>)
   * and all their associated building blocks.
   * 
   * @param releaseSuccession A List of <code>SuccessionContainer</code>s which shall be exported. At the moment, on the release from a container is used,
   *          and the level information is ignored. The releases will always be exported in alphabetical order, regardless of the order in this list.
   * @param releaseType Building block type of the releases that shall be exported. This required to put them on the appropriate worksheets.
   * @param serverURL the URL to be used for hyperlinks within excel report
   * @return excel workbook
   */
  ExportWorkbook getReleaseSuccessorExcelExport(List<SuccessionContainer<? extends Sequence<?>>> releaseSuccession, TypeOfBuildingBlock releaseType,
                                                String serverURL, TemplateType templateType);

  /**
   * @param node rootNode of the query that is used to find appropriate results
   * @param tsQuery the {@link TimeseriesQuery} to execute after processing the rootNode
   * @param postProcessingStrategies for postprocessing the results
   * @return CSV Export as String
   */
  String getCsvExportNew(final Node node, TimeseriesQuery tsQuery,
                         final List<AbstractPostprocessingStrategy<? extends BuildingBlock>> postProcessingStrategies);

  /**
   * Checks permissions and generates a Visio document for the given list of building blocks and
   * masterplan options.
   * 
   * @param buildingBlocks The building blocks to show in the diagram.
   * @param options The configuration options for the masterplan export.
   * @return A generated Visio document instance that can write to a OutputStream.
   */
  de.iteratec.visio.model.Document generateVisioMasterplanExport(final List<? extends BuildingBlock> buildingBlocks, final IMasterplanOptions options);

  /**
   * Checks permissions and generates an SVG document for the given list of building blocks and
   * masterplan options.
   * 
   * @param buildingBlocks The building blocks to show in the diagram.
   * @param options The configuration options for the masterplan export.
   * @return A generated SVG document instance that can write to a OutputStream.
   */
  de.iteratec.svg.model.Document generateSvgMasterplanExport(final List<? extends BuildingBlock> buildingBlocks, final IMasterplanOptions options);

  /**
   * Checks permissions and generates a SVG document instance for the given List of building blocks
   * and diagram options.
   * 
   * @param buildingBlocks The building blocks to include in the diagram.
   * @param portfolioOptions The configuration options (dimensions) for the portfolio diagram.
   * @return A generated SVG document instance that can write to a given OutputStream.
   */
  de.iteratec.svg.model.Document generateSvgPortfolioExport(final List<? extends BuildingBlock> buildingBlocks,
                                                            final IPortfolioOptions portfolioOptions);

  /**
   * Checks permissions and generates an SVG document for the given list of building blocks and
   * cluster options.
   * 
   * @param buildingBlocks The building blocks to show in the diagram.
   * @param options The configuration options for the cluster export.
   * @return A generated SVG document instance that can write to a OutputStream.
   */
  de.iteratec.svg.model.Document generateSvgClusterExport(final List<? extends BuildingBlock> buildingBlocks, final ClusterOptionsBean options);

  /**
   * Checks permissions and generates an SVG document for the given list of building blocks and
   * bar diagram options.
   * 
   * @param buildingBlocks The building blocks to show in the diagram.
   * @param options The configuration options for the bar diagram export.
   * @return A generated SVG document instance that can write to a OutputStream.
   */
  de.iteratec.svg.model.Document generateSvgPieBarDiagramExport(final List<? extends BuildingBlock> buildingBlocks,
                                                                final PieBarDiagramOptionsBean options);

  /**
   * Checks permissions and generates an SVG document for the given list of memory beans.
   * 
   * @param memBeans The memory beans diagram export.
   * @param options The configuration options
   * @return A generated SVG document instance that can write to a OutputStream.
   */
  de.iteratec.svg.model.Document generateSvgCompositeDiagramExport(final List<ManageReportMemoryBean> memBeans, CompositeDiagramOptionsBean options);

  /**
   * Generates a Visio Neighborhood Diagram for the given objectOfInterest
   * @param objectOfInterest
   * @return A Viso Neighborhood Diagram document
   */
  Document generateVisioNeighborhoodDiagram(BuildingBlock objectOfInterest);

  /**
   * Generates a SVG Neighborhood Diagram for the given objectOfInterest
   * @param objectOfInterest
   * @param serverUrl 
   * @return A SVG Neighborhood Diagram document
   */
  de.iteratec.svg.model.Document generateSvgNeighborhoodDiagram(BuildingBlock objectOfInterest, String serverUrl);

  /**
  * creates a ExportWorkbook with the default template from a given template type
  * @param node {@link Node} to parse.
  * @param tsQuery the {@link TimeseriesQuery} to execute after processing the node
  * @param postProcessingStrategies for postprocessing the results
  * @param form The query form for a building block.
  * @param serverURL the server url to be used for creation of links within the current report
  * @param templateType the template type of the report
  * @return ExcelWorkbook created with the default template
  */
  <T extends BuildingBlock> ExportWorkbook getExcelExportByType(Node node,
                                                                TimeseriesQuery tsQuery,
                                                                List<AbstractPostprocessingStrategy<? extends BuildingBlock>> postProcessingStrategies,
                                                                DynamicQueryFormData<?> form, String serverURL, TemplateType templateType);

  /**
   * creates a ExportWorkbook of a given template type, using a given template file
   * @param node {@link Node} to parse.
   * @param tsQuery the {@link TimeseriesQuery} to execute after processing the node
   * @param postProcessingStrategies for postprocessing the results
   * @param form The query form for a building block.
   * @param serverURL the server url to be used for creation of links within the current report
   * @param templateType the template type of the report
   * @return ExcelWorkbook, created from the given template file name
   */
  <T extends BuildingBlock> ExportWorkbook getExcelExportByType(Node node,
                                                                TimeseriesQuery tsQuery,
                                                                List<AbstractPostprocessingStrategy<? extends BuildingBlock>> postProcessingStrategies,
                                                                DynamicQueryFormData<?> form, String serverURL, TemplateType templateType,
                                                                String templateFileName);

  MsProjectExport getMsProjectExport(List<? extends BuildingBlock> results, HttpServletRequest request, DynamicQueryFormData<?> form,
                                     ExportType exportType);

  GraphicsResponseGenerator getResponseGenerator(String selectedExportFormat);


}
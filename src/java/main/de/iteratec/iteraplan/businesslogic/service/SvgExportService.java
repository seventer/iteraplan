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

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Composite.CompositeDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ILandscapeOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.IMasterplanOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.IPortfolioOptions;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.dto.LandscapeDiagramConfigDTO;
import de.iteratec.svg.model.Document;


/**
 * Provides service methods for InformationFlow, Portfolio and Masterplan SVG Export. The Landscape
 * export is treated differently.
 */
public interface SvgExportService {

  /**
   * Generates a SVG document for the given IS Releases.
   * 
   * @param isReleases List of information system releases to show in the diagram.
   * @param interfaces a list of interfaces, which should be shown on the diagram (as connection) if this is null, all interfaces, will be shown.
   * @param ppConnectionMerging true if the post processing strategie HideSubIpureleasesWithConnectionMergingStrategy
   *          is to be applied.
   * @return The generated SVG document instance that can write to an OutputStream.
   */
  Document generateSvgInformationFlowExport(List<InformationSystemRelease> isReleases, List<InformationSystemInterface> interfaces,
                                            List<BusinessObject> businessObjects, boolean ppConnectionMerging, final InformationFlowOptionsBean option);

  /**
   * Generates a SVG document for the given building blocks and diagram options.
   * 
   * @param buildingBlocks The building blocks to include in the diagram.
   * @param portfolioOptions The configuration options (dimensions) for the portfolio diagram.
   * @return The generated SVG document instance that can write to an OutputStream.
   */
  Document generateSvgPortfolioExport(List<? extends BuildingBlock> buildingBlocks, IPortfolioOptions portfolioOptions);

  /**
   * Generates an SVG document for the given parameters
   * 
   * @param buildingBlocks The building blocks to show in the diagram.
   * @param options The {@link MasterplanOptionsBean} for the Masterplan export.
   * @return The generated SVG document instance that can write to an OutputStream.
   */
  Document generateSvgMasterplanExport(List<? extends BuildingBlock> buildingBlocks, IMasterplanOptions options);

  /**
   * Generates an SVG document for the given parameters
   * 
   * @param buildingBlocks The building blocks to show in the diagram.
   * @param options The {@link ClusterOptionsBean} for the Cluster export.
   * @return The generated SVG document instance that can write to an OutputStream.
   */

  Document generateSvgClusterExport(List<? extends BuildingBlock> buildingBlocks, ClusterOptionsBean options);

  /**
   * Generates an SVG document for the given parameters
   * 
   * @param buildingBlocks The building blocks to show in the diagram.
   * @param options The {@link PieBarDiagramOptionsBean} for the Bar export.
   * @return The generated SVG document instance that can write to an OutputStream.
   */
  Document generateSvgPieBarDiagramExport(List<? extends BuildingBlock> buildingBlocks, PieBarDiagramOptionsBean options);

  /**
   * Generates an SVG document for the given parameters
   * 
   * @param memBeans The memory beans for the diagram reports.
   * @param options The {@link CompositeDiagramOptionsBean} for the diagram export.
   * @return The generated SVG document instance that can write to an OutputStream.
   */
  Document generateSvgCompositeDiagramExport(List<ManageReportMemoryBean> memBeans, CompositeDiagramOptionsBean options);

  /**
   * Generates an SVG document for the given parameters
   * 
   * @param config The {@link LandscapeDiagramConfigDTO} that contains the abstract configuration information.
   * @param options The {@link ILandscapeOptions} for the Landscape Diagram.
   * @return The generated SVG document instance that can write to an OutputStream.
   */
  Document generateSvgLandscapeDiagramExport(final LandscapeDiagramConfigDTO config, ILandscapeOptions options);

  /**
   * Generates a Neighborhood Diagram Document for the given Building Block
   * @param objectOfInterest The building block in the center of the graphic
   * @param serverUrl 
   * @return The generated SVG document instance that can write to an OutputStream
   */
  de.iteratec.svg.model.Document generateSvgNeighborhoodDiagramExport(BuildingBlock objectOfInterest, String serverUrl);

}

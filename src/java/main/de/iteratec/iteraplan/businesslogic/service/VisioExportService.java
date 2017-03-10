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

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.IMasterplanOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.IPortfolioOptions;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.visio.model.Document;


/**
 * Provides service methods for InformationFlow, Portfolio and Masterplan Visio Export. The
 * Landscape export is treated differently.
 */
public interface VisioExportService {

  /**
   * Generates a Visio document for the given IS Releases.
   * 
   * @param isReleases List of Information System Releases to show in the diagram.
   * @param interfaces a list of interfaces, which should be shown on the diagram (as edges). If this is null, all interfaces will be shown.
   * @param ppConnectionMerging true if the post processing strategy
   *    {@link de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.HideSubInformationSystemReleasesWithConnectionMergingStrategy}
   *          is to be applied.
   * @param informationFlowOptions The information flow options bean.
   * @return The generated Visio document instance that can write to an OutputStream.
   */
  Document generateVisioInformationFlowExport(List<InformationSystemRelease> isReleases, List<InformationSystemInterface> interfaces,
                                              List<BusinessObject> businessObjects,
                                              boolean ppConnectionMerging, final InformationFlowOptionsBean informationFlowOptions);

  /**
   * Generates a Visio document for the given building blocks and diagram options.
   * 
   * @param buildingBlocks The building blocks to include in the diagram.
   * @param portfolioOptions The configuration options (dimensions) for the portfolio diagram.
   * @return The generated Visio document instance that can write to an OutputStream.
   */
  Document generateVisioPortfolioExport(List<? extends BuildingBlock> buildingBlocks, IPortfolioOptions portfolioOptions);

  /**
   * Generates a Visio document for the given list of building blocks.
   * 
   * @param buildingBlocks The building blocks to show in the diagram.
   * @param options The configuration options for the masterplan export.
   * @return A generated Visio document instance that can write to an OutputStream.
   */
  Document generateVisioMasterplanExport(List<? extends BuildingBlock> buildingBlocks, IMasterplanOptions options);

  /**
   * Generates a Neighborhood Diagram Document for the given Building Block
   * @param objectOfInterest The building block in the center of the graphic
   * @return The generated Visio document instance that can write to an OutputStream
   */
  Document generateVisioNeighborhoodDiagramExport(BuildingBlock objectOfInterest);

}
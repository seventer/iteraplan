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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.common.neighbor.NeighborhoodDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.visio.VisioBubbleExport;
import de.iteratec.iteraplan.businesslogic.exchange.visio.VisioDimensionExport;
import de.iteratec.iteraplan.businesslogic.exchange.visio.VisioMasterplanExport;
import de.iteratec.iteraplan.businesslogic.exchange.visio.informationflow.VisioInformationFlowExport;
import de.iteratec.iteraplan.businesslogic.exchange.visio.neighborhood.VisioNeighborhoodDiagramExport;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.IGraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.IMasterplanOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.IPortfolioOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.HideSubInformationSystemReleasesWithConnectionMergingStrategy;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.visio.model.Document;


/**
 * Provides service methods for InformationFlow, Portfolio and Masterplan Visio Export. The
 * Landscape export is treated differently.
 */
public final class VisioExportServiceImpl implements VisioExportService {
  private GeneralBuildingBlockService generalBuildingBlockService;
  private AttributeTypeService        attributeTypeService;
  private AttributeValueService       attributeValueService;
  private BuildingBlockServiceLocator buildingBlockServiceLocator;

  public void setGeneralBuildingBlockService(GeneralBuildingBlockService generalBuildingBlockService) {
    this.generalBuildingBlockService = generalBuildingBlockService;
  }

  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }

  public void setAttributeValueService(AttributeValueService attributeValueService) {
    this.attributeValueService = attributeValueService;
  }

  public void setBuildingBlockServiceLocator(BuildingBlockServiceLocator buildingBlockServiceLocator) {
    this.buildingBlockServiceLocator = buildingBlockServiceLocator;
  }

  /**
   * Wraps the graphic generation together with the error handling for BusinessExceptions with
   * regard to the state of the dimension options.
   * 
   * @param export
   *          An initialized export to be created.
   * @param options
   *          The options of the export whose dimensions are to be switched.
   * @return The generated document.
   */
  private Document generateDiagram(VisioDimensionExport export, IGraphicalExportBaseOptions options) {
    try {
      options.switchDimensionOptionsToGenerationMode();
      return export.createDiagram();
    } finally {
      options.switchDimensionOptionsToPresentationMode();
    }
  }

  /** {@inheritDoc} */
  public Document generateVisioInformationFlowExport(List<InformationSystemRelease> isReleases,
                                                     List<InformationSystemInterface> interfaces,
                                                     List<BusinessObject> businessObjects,
                                                     boolean ppConnectionMerging, final InformationFlowOptionsBean informationFlowOptions) {

    if (isReleases.size() > IteraplanProperties.getIntProperty(IteraplanProperties.GRAPHICAL_EXPORT_MAX_ELEMENTS)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.EXPORT_GRAPHICAL_MAX_SIZE_CONSTRAINT);
    }
    List<BuildingBlock> refreshedIsrs = generalBuildingBlockService.refreshBuildingBlocks(isReleases);
    Set<InformationSystemRelease> refreshedIsrSet = Sets.newHashSet();
    for (BuildingBlock bb : refreshedIsrs) {
      refreshedIsrSet.add((InformationSystemRelease) bb);
    }

    if (ppConnectionMerging) {
      HideSubInformationSystemReleasesWithConnectionMergingStrategy s = new HideSubInformationSystemReleasesWithConnectionMergingStrategy(null);
      refreshedIsrSet = s.process(refreshedIsrSet, null);
    }

    Set<InformationSystemInterface> refreshedIsiSet = null;
    if (interfaces != null) {
      List<BuildingBlock> refreshedIsis = generalBuildingBlockService.refreshBuildingBlocks(interfaces);
      refreshedIsiSet = Sets.newHashSet();
      for (BuildingBlock bb : refreshedIsis) {
        refreshedIsiSet.add((InformationSystemInterface) bb);
      }
    }

    Map<String, BusinessObject> refreshedBoMap = null;
    if (businessObjects != null) {
      List<BuildingBlock> refreshedBos = generalBuildingBlockService.refreshBuildingBlocks(businessObjects);
      refreshedBoMap = Maps.newHashMap();
      for (BuildingBlock bb : refreshedBos) {
        refreshedBoMap.put(bb.getNonHierarchicalName(), (BusinessObject) bb);
      }
    }

    InformationSystemReleaseService isrService = buildingBlockServiceLocator.getIsrService();
    VisioInformationFlowExport export = new VisioInformationFlowExport(new ArrayList<InformationSystemRelease>(refreshedIsrSet), refreshedIsiSet,
        refreshedBoMap, UserContext.getCurrentLocale(), informationFlowOptions, attributeTypeService, attributeValueService, isrService);

    return generateDiagram(export, informationFlowOptions);
  }

  /** {@inheritDoc} */
  public Document generateVisioPortfolioExport(List<? extends BuildingBlock> buildingBlocks, IPortfolioOptions portfolioOptions) {
    if (buildingBlocks.size() > IteraplanProperties.getIntProperty(IteraplanProperties.GRAPHICAL_EXPORT_MAX_ELEMENTS)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.EXPORT_GRAPHICAL_MAX_SIZE_CONSTRAINT);
    }
    List<BuildingBlock> refreshedList = generalBuildingBlockService.refreshBuildingBlocks(buildingBlocks);
    VisioBubbleExport visioExport = new VisioBubbleExport(refreshedList, portfolioOptions, attributeTypeService, attributeValueService);

    return generateDiagram(visioExport, portfolioOptions);
  }

  /** {@inheritDoc} */
  public Document generateVisioMasterplanExport(List<? extends BuildingBlock> buildingBlocks, IMasterplanOptions options) {
    if (buildingBlocks.size() > IteraplanProperties.getIntProperty(IteraplanProperties.GRAPHICAL_EXPORT_MAX_ELEMENTS)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.EXPORT_GRAPHICAL_MAX_SIZE_CONSTRAINT);
    }

    List<BuildingBlock> refreshedList = generalBuildingBlockService.refreshBuildingBlocks(buildingBlocks);
    MasterplanDiagramCreator masterplanCreator2 = new MasterplanDiagramCreator(options, buildingBlockServiceLocator, attributeTypeService, refreshedList);
    MasterplanDiagram masterplanDiagram2 = masterplanCreator2.createMasterplanDiagram();
    VisioMasterplanExport masterplanExport = new VisioMasterplanExport(masterplanDiagram2, options, attributeTypeService, attributeValueService);
    
    return generateDiagram(masterplanExport, options);
  }

  /**{@inheritDoc}**/
  public Document generateVisioNeighborhoodDiagramExport(BuildingBlock objectOfInterest) {
    NeighborhoodDiagramCreator cvDiagramCreator = new NeighborhoodDiagramCreator((InformationSystemRelease) objectOfInterest);
    VisioNeighborhoodDiagramExport neighborhoodDiagramExport = new VisioNeighborhoodDiagramExport(cvDiagramCreator.createNeighborhoodDiagram(),
        attributeTypeService, attributeValueService);
    return neighborhoodDiagramExport.createDiagram();
  }

}
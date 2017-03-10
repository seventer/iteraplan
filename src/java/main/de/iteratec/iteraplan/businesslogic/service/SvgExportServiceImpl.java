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
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.common.cluster.ClusterDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.cluster.ClusterDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.common.informationflow.InformationFlowGraphConverter;
import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.LandscapeDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.common.neighbor.NeighborhoodDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.common.piebar.AbstractPieBarDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.common.piebar.BarDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.common.piebar.PieDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.svg.SvgBarDiagramExport;
import de.iteratec.iteraplan.businesslogic.exchange.svg.SvgBubbleExport;
import de.iteratec.iteraplan.businesslogic.exchange.svg.SvgClusterExport;
import de.iteratec.iteraplan.businesslogic.exchange.svg.SvgCompositeDiagramExport;
import de.iteratec.iteraplan.businesslogic.exchange.svg.SvgNeighborhoodDiagramExport;
import de.iteratec.iteraplan.businesslogic.exchange.svg.SvgExport;
import de.iteratec.iteraplan.businesslogic.exchange.svg.SvgInformationFlowExport;
import de.iteratec.iteraplan.businesslogic.exchange.svg.SvgLandscapeDiagramExport;
import de.iteratec.iteraplan.businesslogic.exchange.svg.SvgMasterplanExport;
import de.iteratec.iteraplan.businesslogic.exchange.svg.SvgPieDiagramExport;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalOptionsGetter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.IGraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Composite.CompositeDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ILandscapeOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.IMasterplanOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.IPortfolioOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.HideSubInformationSystemReleasesWithConnectionMergingStrategy;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.dto.LandscapeDiagramConfigDTO;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.GeneralBuildingBlockDAO;
import de.iteratec.svg.model.Document;


/**
 * Provides service methods for SVG Export.
 */
public final class SvgExportServiceImpl implements SvgExportService {

  private static final Logger         LOGGER = Logger.getIteraplanLogger(SvgExportServiceImpl.class);

  private AttributeTypeDAO            attributeTypeDAO;
  private GeneralBuildingBlockService generalBuildingBlockService;
  private GeneralBuildingBlockDAO     generalBuildingBlockDAO;
  private AttributeTypeService        attributeTypeService;
  private AttributeValueService       attributeValueService;
  private BuildingBlockServiceLocator buildingBlockServiceLocator;

  public void setAttributeTypeDAO(AttributeTypeDAO attributeTypeDAO) {
    this.attributeTypeDAO = attributeTypeDAO;
  }

  public void setGeneralBuildingBlockService(GeneralBuildingBlockService generalBuildingBlockService) {
    this.generalBuildingBlockService = generalBuildingBlockService;
  }

  public void setGeneralBuildingBlockDAO(GeneralBuildingBlockDAO generalBuildingBlockDAO) {
    this.generalBuildingBlockDAO = generalBuildingBlockDAO;
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
  private Document generateDiagram(SvgExport export, IGraphicalExportBaseOptions options) {
    try {
      options.switchDimensionOptionsToGenerationMode();
      return export.createDiagram();
    } finally {
      options.switchDimensionOptionsToPresentationMode();
    }
  }

  /** {@inheritDoc} */
  public Document generateSvgInformationFlowExport(List<InformationSystemRelease> isReleases, List<InformationSystemInterface> interfaces,
                                                   List<BusinessObject> businessObjects, boolean ppConnectionMerging,
                                                   final InformationFlowOptionsBean informationFlowOptions) {

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

    InformationFlowGraphConverter informationFlowConverter = new InformationFlowGraphConverter(refreshedIsrSet, refreshedIsiSet, refreshedBoMap,
        attributeTypeService, informationFlowOptions);
    informationFlowConverter.convertToGraph();

    SvgInformationFlowExport export = new SvgInformationFlowExport(UserContext.getCurrentLocale(), informationFlowConverter.getConvertedGraph(),
        informationFlowOptions, attributeTypeService, attributeValueService);

    return generateDiagram(export, informationFlowOptions);
  }

  /** {@inheritDoc} */
  public Document generateSvgPortfolioExport(List<? extends BuildingBlock> buildingBlocks, IPortfolioOptions portfolioOptions) {
    if (buildingBlocks.size() > IteraplanProperties.getIntProperty(IteraplanProperties.GRAPHICAL_EXPORT_MAX_ELEMENTS)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.EXPORT_GRAPHICAL_MAX_SIZE_CONSTRAINT);
    }

    List<BuildingBlock> refreshedList = generalBuildingBlockService.refreshBuildingBlocks(buildingBlocks);
    SvgBubbleExport export = new SvgBubbleExport(refreshedList, portfolioOptions, attributeTypeService, attributeValueService);

    return generateDiagram(export, portfolioOptions);
  }

  /** {@inheritDoc} */
  public Document generateSvgLandscapeDiagramExport(final LandscapeDiagramConfigDTO config, ILandscapeOptions options) {
    LandscapeDiagramCreator mc = new LandscapeDiagramCreator(config, attributeTypeDAO, generalBuildingBlockService);
    SvgLandscapeDiagramExport me = new SvgLandscapeDiagramExport(mc.createLandscapeDiagram(), options, attributeTypeService, attributeValueService);

    return generateDiagram(me, options);
  }

  /** {@inheritDoc} */
  public Document generateSvgMasterplanExport(List<? extends BuildingBlock> buildingBlocks, IMasterplanOptions options) {
    if (buildingBlocks.size() > IteraplanProperties.getIntProperty(IteraplanProperties.GRAPHICAL_EXPORT_MAX_ELEMENTS)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.EXPORT_GRAPHICAL_MAX_SIZE_CONSTRAINT);
    }

    List<BuildingBlock> refreshedList = generalBuildingBlockService.refreshBuildingBlocks(buildingBlocks);

    MasterplanDiagramCreator masterplanCreator = new MasterplanDiagramCreator(options, buildingBlockServiceLocator, attributeTypeService,
        refreshedList);
    MasterplanDiagram masterplanDiagram = masterplanCreator.createMasterplanDiagram();

    SvgMasterplanExport masterplanExport = new SvgMasterplanExport(masterplanDiagram, options, attributeTypeService, attributeValueService);

    return generateDiagram(masterplanExport, options);
  }

  /** {@inheritDoc} */
  public Document generateSvgClusterExport(List<? extends BuildingBlock> buildingBlocks, ClusterOptionsBean options) {
    if (buildingBlocks.size() > IteraplanProperties.getIntProperty(IteraplanProperties.GRAPHICAL_EXPORT_MAX_ELEMENTS)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.EXPORT_GRAPHICAL_MAX_SIZE_CONSTRAINT);
    }

    List<BuildingBlock> refreshedList = generalBuildingBlockService.refreshBuildingBlocks(buildingBlocks);

    if (Constants.REPORTS_EXPORT_CLUSTER_MODE_BB.equals(options.getSelectedClusterMode()) && refreshedList.isEmpty()) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.CLUSTER_NO_ELEMENTS);
    }

    // Note: We can't use the generic method for switching to generation mode, as the
    // ClusterDiagramCreator also needs the color dimension to be in generation mode when the
    // graphic is in attribute mode.
    try {
      options.switchDimensionOptionsToGenerationMode();

      ClusterDiagramCreator creator = new ClusterDiagramCreator(options, refreshedList, generalBuildingBlockDAO, attributeTypeService,
          attributeValueService);
      ClusterDiagram clusterDiagram = creator.createClusterDiagram();
      SvgClusterExport export = new SvgClusterExport(clusterDiagram, options, attributeTypeService, attributeValueService);

      return export.createDiagram();
    } finally {
      options.switchDimensionOptionsToPresentationMode();
    }
  }

  /** {@inheritDoc} */
  public Document generateSvgPieBarDiagramExport(List<? extends BuildingBlock> buildingBlocks, PieBarDiagramOptionsBean options) {
    List<BuildingBlock> refreshedList = generalBuildingBlockService.refreshBuildingBlocks(buildingBlocks);

    try {
      options.switchDimensionOptionsToGenerationMode();

      //explicitly set the number of selected elements to the size of the list, because it has been initialized with the number of selected elements of the underlying query. This is wrong in the case of the graphics being used in a custom dashboard instance, where the underlying query is replaced by the result of another saved tabular report.
      options.setNumberOfSelectedElements(buildingBlocks.size());

      switch (options.getDiagramType()) {
        case PIE:
          return generateSvgPieDiagram(options, refreshedList);
        case BAR:
          return generateSvgBarDiagram(options, refreshedList);
        default:
          LOGGER.error("Invalid DiagramType for SvgPieBarDiagramExport: {0}", options.getDiagramType().name());
          throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
      }
    } finally {
      options.switchDimensionOptionsToPresentationMode();
    }
  }

  private Document generateSvgPieDiagram(PieBarDiagramOptionsBean options, List<BuildingBlock> refreshedList) {
    PieDiagramCreator creator = new PieDiagramCreator(options, refreshedList, attributeTypeService, attributeValueService);
    SvgPieDiagramExport export = new SvgPieDiagramExport(creator.createDiagram(), options, attributeTypeService, attributeValueService);
    return export.createDiagram();
  }

  private Document generateSvgBarDiagram(PieBarDiagramOptionsBean options, List<BuildingBlock> refreshedList) {
    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(options.getSelectedKeyAssociation());
    BuildingBlockService<BuildingBlock, Integer> bbService = buildingBlockServiceLocator.getService(tobb);

    BarDiagramCreator creator = new BarDiagramCreator(options, refreshedList, attributeTypeService, attributeValueService, bbService);
    SvgBarDiagramExport export = new SvgBarDiagramExport(creator.createDiagram(), options, attributeTypeService, attributeValueService);
    return export.createDiagram();
  }

  /** {@inheritDoc} */
  public Document generateSvgCompositeDiagramExport(List<ManageReportMemoryBean> memBeans, CompositeDiagramOptionsBean compositeOptions) {
    List<AbstractPieBarDiagramCreator<?>> creators = Lists.newArrayList();
    List<PieBarDiagramOptionsBean> optionsList = Lists.newArrayList();
    for (ManageReportMemoryBean memBean : memBeans) {
      List<? extends BuildingBlock> buildingBlocks = memBean.getQueryResult().getSelectedResults();

      List<BuildingBlock> refreshedList = generalBuildingBlockService.refreshBuildingBlocks(buildingBlocks);

      if (ReportType.PIE.equals(memBean.getReportType()) || ReportType.BAR.equals(memBean.getReportType())) {
        PieBarDiagramOptionsBean options = GraphicalOptionsGetter.getPieBarOptions(memBean);
        optionsList.add(options);
        options.setSelectedGraphicFormat(compositeOptions.getSelectedGraphicFormat());
        options.setShowSavedQueryInfo(compositeOptions.isShowSavedQueryInfo());
        options.setServerUrl(compositeOptions.getServerUrl());

        options.switchDimensionOptionsToGenerationMode();

        switch (options.getDiagramType()) {
          case PIE:
            creators.add(new PieDiagramCreator(options, refreshedList, attributeTypeService, attributeValueService));
            break;
          case BAR:
            TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(options.getSelectedKeyAssociation());
            BuildingBlockService<BuildingBlock, Integer> bbService = buildingBlockServiceLocator.getService(tobb);
            creators.add(new BarDiagramCreator(options, refreshedList, attributeTypeService, attributeValueService, bbService));
            break;
          default:
            LOGGER.error("Invalid DiagramType for SvgPieBarDiagramExport: {0}", options.getDiagramType().name());
            throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
        }
      }
      else {
        LOGGER.error("Invalid report type for SvgCompositeDiagramExport: {0}", memBean.getReportType());
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
      }
    }

    SvgCompositeDiagramExport export = new SvgCompositeDiagramExport(compositeOptions, creators, optionsList, attributeTypeService,
        attributeValueService);
    return export.createDiagram();
  }

  /**{@inheritDoc}**/
  public Document generateSvgNeighborhoodDiagramExport(BuildingBlock objectOfInterest, String serverUrl) {
    NeighborhoodDiagramCreator cvDiagramCreator = new NeighborhoodDiagramCreator((InformationSystemRelease) objectOfInterest);
    SvgNeighborhoodDiagramExport neighborhoodDiagramExport = new SvgNeighborhoodDiagramExport(cvDiagramCreator.createNeighborhoodDiagram(),
        attributeTypeService, attributeValueService, serverUrl);
    return neighborhoodDiagramExport.createDiagram();
  }

}

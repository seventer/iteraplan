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
package de.iteratec.iteraplan.businesslogic.exchange.visio.neighborhood;

import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.INamesLegend.LegendMode;
import de.iteratec.iteraplan.businesslogic.exchange.common.neighbor.NeighborhoodDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.neighbor.SpatialInfromationSystemWrapper;
import de.iteratec.iteraplan.businesslogic.exchange.visio.VisioDimensionExport;
import de.iteratec.iteraplan.businesslogic.exchange.visio.legend.VisioNamesLegend;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.InchConverter;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.visio.model.Document;
import de.iteratec.visio.model.Shape;
import de.iteratec.visio.model.exceptions.MasterNotFoundException;

public class VisioNeighborhoodDiagramExport extends VisioDimensionExport {

  private static final Logger       LOGGER              = Logger.getIteraplanLogger(VisioNeighborhoodDiagramExport.class);

  private static final String       VISIO_TEMPLATE      = "/VisioNeighborhoodDiagramTemplate.vdx";
  private static final String       BACKGROUND_SHAPE    = "Background";
  private static final String       LEGEND_COLOR_SHAPE  = "legend-color";
  private static final String       LABEL_SHAPE         = "Label";
  private static final String       CONNECTION_SHAPE    = "Connection";

  private static final double       TEXT_SIZE_10_PTS    = 0.139;
  private static final double       COLOR_LEGEND_HEIGHT = 280.0;

  private Shape                     objectOfInterest;
  private final NeighborhoodDiagram neighborhoodDiagram;

  public VisioNeighborhoodDiagramExport(NeighborhoodDiagram neighborhoodDiagram, AttributeTypeService attributeTypeService,
      AttributeValueService attributeValueService) {
    super(attributeTypeService, attributeValueService);
    this.neighborhoodDiagram = neighborhoodDiagram;
  }

  @Override
  public Document createDiagram() {
    LOGGER.debug("generating Visio-Neighborhood-Diagram");
    try {
      init(VISIO_TEMPLATE, MessageAccess.getString(NeighborhoodDiagram.TITLE, UserContext.getCurrentLocale()));

      setVisioNamesLegend(new VisioNamesLegend(this.getTargetPage()));
      fillLegendEntries();

      double sideLength = pixelToInches(neighborhoodDiagram.getSideLength());
      double nameLegendHeight = pixelToInches(getVisioNamesLegend().getNamesLegendHeight() * NeighborhoodDiagram.LEGEND_ENTRY_HEIGHT);
      double nameLegendWidth = getVisioNamesLegend().getLegendWidth();
      double colorLegendSideLength = pixelToInches(COLOR_LEGEND_HEIGHT);
      double additionalWidth = Math.max(colorLegendSideLength, nameLegendWidth) + pixelToInches(NeighborhoodDiagram.SIMPLE_MARGIN);

      if ((nameLegendHeight + colorLegendSideLength) > sideLength) {
        getTargetPage().setSize(sideLength + additionalWidth + pixelToInches(NeighborhoodDiagram.ROOT_MARGIN),
            nameLegendHeight + colorLegendSideLength + pixelToInches(NeighborhoodDiagram.ROOT_MARGIN));
      }
      else {
        getTargetPage().setSize(sideLength + additionalWidth + pixelToInches(NeighborhoodDiagram.ROOT_MARGIN),
            sideLength + pixelToInches(NeighborhoodDiagram.ROOT_MARGIN));
      }

      createNeighborhoodDiagramContent();

      double yPlacementNameLegend = getTargetPage().getHeight() - pixelToInches(NeighborhoodDiagram.SIMPLE_MARGIN) - nameLegendHeight;
      double xPlacementLegends = sideLength + pixelToInches(NeighborhoodDiagram.ROOT_MARGIN);

      getVisioNamesLegend().setLegendMode(LegendMode.IN_PAGE);
      createNamesLegend(xPlacementLegends, yPlacementNameLegend, 0, 0, false, "informationsystem");
      if (getVisioNamesLegend().getNamesLegendHeight() > 1) {
        yPlacementNameLegend = yPlacementNameLegend - pixelToInches(NeighborhoodDiagram.DEFAULT_LEGEND_MARGIN);
      }
      else {
        yPlacementNameLegend = getTargetPage().getHeight() - NeighborhoodDiagram.SIMPLE_MARGIN;
      }

      ColorDimension colorDimension = createColorDimension(generateOptionsBean(), TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
      Coordinates colorLegendCordinates = new Coordinates(xPlacementLegends, yPlacementNameLegend);
      createColorLegend(colorDimension, getTargetPage(), colorLegendCordinates, LEGEND_COLOR_SHAPE,
          MessageAccess.getString(NeighborhoodDiagram.MESSAGE_TYPE_OF_STATUS), TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);

      setTitlePosAndSize(createDiagramTitle(MessageAccess.getString(NeighborhoodDiagram.TITLE, UserContext.getCurrentLocale())),
          pixelToInches(NeighborhoodDiagram.SIMPLE_MARGIN), getTargetPage().getHeight(), pixelToInches(NeighborhoodDiagram.DEFAUL_CONTENT_SIZE));


      LOGGER.debug("Finished Visio-Neighborhood-Diagram");
    } catch (Exception ex) {
      LOGGER.error("VisioExportException while trying to create a VisioNeighborhoodDiagramExport:\n" + ex.getMessage());
      throw new IteraplanTechnicalException(ex);
    }
    return getVisioDocument();
  }

  private void createNeighborhoodDiagramContent() throws MasterNotFoundException {
    createNeighborhoodBackground();
    generateNeighborhoodEntries();
  }

  private void generateNeighborhoodEntries() throws MasterNotFoundException {
    generateNeighborhoodEntry(neighborhoodDiagram.getObjectOfInterest(), true);
    for (SpatialInfromationSystemWrapper connectedIsr : neighborhoodDiagram.getConnectedInformationSystems()) {
      /**
       * The Rectangle which represents a Information System
       */
      generateNeighborhoodEntry(connectedIsr, false);
      /**
       * The connection Between the Object of Interest and the InformationSystem
       */
    }
  }

  private void generateNeighborhoodEntry(SpatialInfromationSystemWrapper connectedIsr, boolean isObjectOfInterest)
      throws MasterNotFoundException {
    Shape connectedIS = this.getTargetPage().createNewShape(LABEL_SHAPE);
    connectedIS.setPosition(pixelToInches(connectedIsr.getCoordinate().getX() + NeighborhoodDiagram.BLOCK_WIDTH / 3),
        pixelToInches(findYVisioCoordinate(connectedIsr.getCoordinate().getY() - NeighborhoodDiagram.BLOCK_HEIGHT * 2)));
    connectedIS.setCharSize(TEXT_SIZE_10_PTS);
    connectedIS.setSize(pixelToInches(NeighborhoodDiagram.BLOCK_WIDTH), pixelToInches(NeighborhoodDiagram.BLOCK_HEIGHT));
    connectedIS.setShapeText(connectedIsr.getNameAbbreviation());

    connectedIS.setFillForegroundColor(connectedIsr.getColorForStatus());
    if (isObjectOfInterest) {
      objectOfInterest = connectedIS;
      objectOfInterest.setLineWeight(pixelToInches(NeighborhoodDiagram.OOI_LINE_WIDTH));

    }
    else {
      this.getTargetPage().createNewConnector(CONNECTION_SHAPE, connectedIS, objectOfInterest);
    }
  }

  private void createNeighborhoodBackground() throws MasterNotFoundException {
    Shape background = this.getTargetPage().createNewShape(BACKGROUND_SHAPE);
    background.setSize(pixelToInches(neighborhoodDiagram.getSideLength()), pixelToInches(neighborhoodDiagram.getSideLength()));
    background.setPosition(getTargetPage().getHeight() - (pixelToInches(NeighborhoodDiagram.ROOT_MARGIN + neighborhoodDiagram.getSideLength())) / 2,
        (pixelToInches(neighborhoodDiagram.getSideLength() + NeighborhoodDiagram.ROOT_MARGIN)) / 2);
  }

  private static double pixelToInches(double pixel) {
    return InchConverter.pixelToInches(pixel, DEFAULT_SYSTEM_DPI);
  }

  private double findYVisioCoordinate(double y) {
    return neighborhoodDiagram.getObjectOfInterest().getCoordinate().getY() + (neighborhoodDiagram.getObjectOfInterest().getCoordinate().getY() - y);
  }

  /**
   * Generates a ColorDimensionOptionsBean for TypeOfStatus
   * @return the bean
   */
  private ColorDimensionOptionsBean generateOptionsBean() {
    ColorDimensionOptionsBean bean = new ColorDimensionOptionsBean();
    bean.setDimensionAttributeId(GraphicalExportBaseOptions.STATUS_SELECTED);
    List<String> colors = new ArrayList<String>();
    colors.add(neighborhoodDiagram.toHexString(NeighborhoodDiagram.STATUS_CURRENT_COLOR, false));
    colors.add(neighborhoodDiagram.toHexString(NeighborhoodDiagram.STATUS_PLANNED_COLOR, false));
    colors.add(neighborhoodDiagram.toHexString(NeighborhoodDiagram.STATUS_TARGET_COLOR, false));
    colors.add(neighborhoodDiagram.toHexString(NeighborhoodDiagram.STATUS_INACTIVE_COLOR, false));
    bean.setAvailableColors(colors);

    List<String> attributeValues = new ArrayList<String>();
    attributeValues.add(TypeOfStatus.CURRENT.name());
    attributeValues.add(TypeOfStatus.PLANNED.name());
    attributeValues.add(TypeOfStatus.TARGET.name());
    attributeValues.add(TypeOfStatus.INACTIVE.name());
    bean.refresh(attributeValues);

    bean.setSelectedColor(TypeOfStatus.CURRENT.name(), neighborhoodDiagram.toHexString(NeighborhoodDiagram.STATUS_CURRENT_COLOR, false));
    bean.setSelectedColor(TypeOfStatus.PLANNED.name(), neighborhoodDiagram.toHexString(NeighborhoodDiagram.STATUS_PLANNED_COLOR, false));
    bean.setSelectedColor(TypeOfStatus.TARGET.name(), neighborhoodDiagram.toHexString(NeighborhoodDiagram.STATUS_TARGET_COLOR, false));
    bean.setSelectedColor(TypeOfStatus.INACTIVE.name(), neighborhoodDiagram.toHexString(NeighborhoodDiagram.STATUS_INACTIVE_COLOR, false));

    return bean;
  }

  /**
   * method must be called before any other content is generated, to estimate the height of the visio diagram
   */
  private void fillLegendEntries() {
    for (SpatialInfromationSystemWrapper sIsw : neighborhoodDiagram.getConnectedInformationSystems()) {
      sIsw.setNameAbbreviation(addLegendEntry(sIsw.getInformationSystemRelease(), pixelToInches(NeighborhoodDiagram.BLOCK_WIDTH)));
    }
    neighborhoodDiagram.getObjectOfInterest().setNameAbbreviation(
        addLegendEntry(neighborhoodDiagram.getObjectOfInterest().getInformationSystemRelease(), pixelToInches(NeighborhoodDiagram.BLOCK_WIDTH)));
  }

  private String addLegendEntry(InformationSystemRelease isr, double width) {
    return getVisioNamesLegend().addLegendEntry(isr.getNonHierarchicalName(), isr.getName(),
        MessageAccess.getString(isr.getTypeOfBuildingBlock().getValue()), width, Double.parseDouble(NeighborhoodDiagram.DEFAULT_FONT_SIZE), null);
  }

}

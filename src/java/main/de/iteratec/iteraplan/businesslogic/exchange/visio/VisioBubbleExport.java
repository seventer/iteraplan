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
package de.iteratec.iteraplan.businesslogic.exchange.visio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.AttributeAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.AttributeRangeAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.Dimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.DimensionAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.PositionDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.SizeDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.portfolio.BubbleSpace;
import de.iteratec.iteraplan.businesslogic.exchange.common.portfolio.PositionInTile;
import de.iteratec.iteraplan.businesslogic.exchange.common.portfolio.Tile;
import de.iteratec.iteraplan.businesslogic.exchange.common.portfolio.Weight;
import de.iteratec.iteraplan.businesslogic.exchange.visio.legend.VisioAttributeLegend;
import de.iteratec.iteraplan.businesslogic.exchange.visio.legend.VisioNamesLegend;
import de.iteratec.iteraplan.businesslogic.exchange.visio.legend.VisioSizeLegend;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.IPortfolioOptions;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.InchConverter;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.visio.model.Document;
import de.iteratec.visio.model.Shape;
import de.iteratec.visio.model.exceptions.MasterNotFoundException;


/**
 * Contains the builder algorithm to generate the Visio portfolio diagram. This class does not use
 * the gxl2visio functionality, but directly uses the Visio element wrappers of the gxl2visio
 * library to generate a {@link Document}.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class VisioBubbleExport extends VisioDimensionExport {

  private static final String       VISIO_TEMPLATE_FILE            = "/VisioBubbleTemplate.vdx";
  private static final String       VISIO_DIAGRAM_TITLE            = "iteraplan portfolio";

  private static final String       VISIO_PROPERTY_DIMENSION       = "Dimension";

  private static final String       VISIO_SHAPE_NAME_COSY          = "CoordinateSystem";
  private static final String       VISIO_SHAPE_NAME_XAXIS         = "X-Axis";
  private static final String       VISIO_SHAPE_NAME_YAXIS         = "Y-Axis";
  private static final String       VISIO_SHAPE_NAME_BUBBLE        = "Bubble";
  private static final String       VISIO_SHAPE_NAME_RECT          = "Rectangle";

  private static final String       VISIO_SHAPE_NAME_COLOR_BUBBLE  = "Color-Index-Bubble";
  private static final String       VISIO_SHAPE_NAME_SIZE_ENTITY   = "SizeEntity";
  private static final String       VISIO_SHAPE_NAME_DIMHEADER     = "Legend-Dimension-Header";
  private static final String       VISIO_SHAPE_NAME_DIMCONTENT    = "Legend-Dimension-Content";
  private static final String       VISIO_SHAPE_NAME_ATTRHEADER    = "Legend-Attribute-Header";
  private static final String       VISIO_SHAPE_NAME_ATTRCONTENT   = "Legend-Attribute-Content";

  private static final String       SHAPE_LINE                     = "Line";
  private static final String       VALUE_LINESTYLE_SOLID          = "solid";

  // the maximum and minimum values are the boundaries adequate to the coordinate system shape (in
  // inches)
  private static final double       MINIMUM_X                      = 1.57480315;                                        // 40mm
  private static final double       MAXIMUM_X                      = 12.7952756;                                        // 325mm
  private static final double       MINIMUM_Y                      = 1.57480315;                                        // 40mm
  private static final double       MAXIMUM_Y                      = 12.7952756;                                        // 325mm

  private static final double       LEGEND_MARGIN_CM               = 1.3;

  // the absolute distance of axis values to the real coordinate system (in inches)
  private static final double       AXIS_VALUES_FROM_COSY_DISTANCE = 0.9;

  private static final double       COSY_SIDE                      = MAXIMUM_X - MINIMUM_X;

  // Visio operates in radians and counterclockwise so this is 90 degrees clockwise
  private static final double       VISIO_VERTICAL_ANGLE           = -Math.PI * 1.5;
  private static final double       VISIO_HORIZONTAL_ANGLE         = 0;

  // Scaling attributes
  private double                    scaleFactorX                   = 1;                                                 // Default
  // X
  // scale
  // factor
  private double                    scaleFactorY                   = 1;                                                 // Default
  // Y
  // scale
  // factor
  // The offsets at which the graphic should begin, in relation to the (0,0) point
  private double                    offsetX;
  private double                    offsetY;
  // The initial borders of the frame that encapsulates all the objects to be drawn in the
  // coordinate system
  private double                    locMinX                        = Double.MAX_VALUE;
  private double                    locMinY                        = Double.MAX_VALUE;

  private double                    legendFrameBaseX;
  private double                    legendFrameWidth;
  private double                    namesLegendFrameMaxY;
  private double                    scaling                        = 1;

  private final List<BuildingBlock> buildingBlocks;

  private final IPortfolioOptions   portfolioOptions;

  private BubbleSpace               bubbleSpace;

  private static final Logger       LOGGER                         = Logger.getIteraplanLogger(VisioBubbleExport.class);

  public VisioBubbleExport(List<? extends BuildingBlock> blocks, IPortfolioOptions portfolioOptions, AttributeTypeService attributeTypeService,
      AttributeValueService attributeValueService) {
    super(attributeTypeService, attributeValueService);
    this.buildingBlocks = new ArrayList<BuildingBlock>(blocks);
    this.portfolioOptions = portfolioOptions;
  }

  /*
   * (non-Javadoc) This method initiates the drawing of the diagram.
   */
  @Override
  public Document createDiagram() {

    // Prepare a Document and a Page for writing
    init(VISIO_TEMPLATE_FILE, VISIO_DIAGRAM_TITLE);

    // Names legend
    if (portfolioOptions.isUseNamesLegend()) {
      setVisioNamesLegend(new VisioNamesLegend(getTargetPage()));
    }

    // Initialise the dimensions (X,Y,Colour,Size)
    bubbleSpace = createBubbleSpace(portfolioOptions, buildingBlocks);
    revertYAxesForVisio();
    try {
      // create the coordinatesystem
      createCoordinateSystem();

      // create labels for the axes
      String xLabel = bubbleSpace.getXDimension().getName();
      String yLabel = bubbleSpace.getYDimension().getName();
      createCoordinateLabels(xLabel, yLabel);

      Shape title = createDiagramTitle(getTitle(portfolioOptions.getSelectedBbType()));
      setTitlePosAndSize(title, 1, getTargetPage().getHeight(), null);

      createGeneratedInformation(getTargetPage().getWidth());
      createLogos(0, 0, getTargetPage().getWidth(), getTargetPage().getHeight());

      // Create the textual legend
      Shape legend;
      if (isOneDimensionNumber()) {
        legend = createLegend(true);
      }
      else {
        legend = createLegend(false);
      }

      createQueryInfo(legend);

      double colorLegendHeightInch = createColorLegend();

      // Add the bubbles to the diagram
      addResultNodes();

      double sizeLegendHeightInch = createSizeLegend();

      // Create the grid labels
      if (IPortfolioOptions.TYPE_XY.equals(portfolioOptions.getPortfolioType())) {
        createGridLabels(bubbleSpace.getXDimension(), false);
        createGridLabels(bubbleSpace.getYDimension(), true);
      }

      if (portfolioOptions.isUseNamesLegend()) {
        namesLegendFrameMaxY = namesLegendFrameMaxY - Math.max(colorLegendHeightInch, sizeLegendHeightInch)
            - InchConverter.cmToInches(LEGEND_MARGIN_CM);

        double namesLegendBaseY = InchConverter.cmToInches(2 * LEGEND_MARGIN_CM);
        createNamesLegend(legendFrameBaseX, namesLegendBaseY, legendFrameWidth, namesLegendFrameMaxY - namesLegendBaseY,
            portfolioOptions.isNakedExport(), getTitle(portfolioOptions.getSelectedBbType()));
      }

    } catch (MasterNotFoundException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }

    return getVisioDocument();
  }

  private Map<String, Object> addLabels(BuildingBlock block) {

    Map<String, Object> props = new HashMap<String, Object>();
    String label = "";
    int count = 1;

    Dimension<?> dim = bubbleSpace.getXDimension();
    String attrName = dim.getName();
    if (!StringUtils.isEmpty(attrName)) {
      label = attrName + "=" + block.getAttributeValue(attrName, getLocale());
      props.put(VISIO_PROPERTY_DIMENSION + count++, label);
    }

    dim = bubbleSpace.getYDimension();
    attrName = dim.getName();
    if (!StringUtils.isEmpty(attrName)) {
      label = attrName + "=" + block.getAttributeValue(attrName, getLocale());
      props.put(VISIO_PROPERTY_DIMENSION + count++, label);
    }

    dim = bubbleSpace.getSizeDimension();
    attrName = dim.getName();
    if (!StringUtils.isEmpty(attrName)) {
      label = attrName + "=" + block.getAttributeValue(attrName, getLocale());
      props.put(VISIO_PROPERTY_DIMENSION + count++, label);
    }

    dim = bubbleSpace.getColorDimension();
    attrName = dim.getName();
    if (!StringUtils.isEmpty(attrName)) {
      label = attrName + "=" + block.getAttributeValue(attrName, getLocale());
      props.put(VISIO_PROPERTY_DIMENSION + count++, label);
    }

    return props;
  }

  /**
   * Adds all necessary result nodes to the graph for the list of BuildingBlocks.
   *
   * @throws MasterNotFoundException
   */
  private void addResultNodes() throws MasterNotFoundException {
    LOGGER.debug("entering addResultNodes...");

    /*
     * If scaling is enabled, additional check is necessary, to estimate the size of the frame, in
     * which all to be drawn entities can be enclosed. The scaling factors for the X and Y axis are
     * then calculated respecting this frame.
     */
    if (portfolioOptions.isScalingEnabled()) {
      double tmp;
      double frameSizeX;
      double frameSizeY;
      double locMaxX = Double.MIN_VALUE;
      double locMaxY = Double.MIN_VALUE;

      for (BuildingBlock block : buildingBlocks) {
        // Determine min and max X
        tmp = bubbleSpace.getXDimension().getValue(block).doubleValue();
        if (tmp > locMaxX && tmp != -1) {
          locMaxX = tmp;
        }
        if (tmp < locMinX && tmp != -1) {
          locMinX = tmp;
        }
        // Determine min and max Y
        tmp = bubbleSpace.getYDimension().getValue(block).doubleValue();
        if (tmp > locMaxY && tmp != -1) {
          locMaxY = tmp;
        }
        if (tmp < locMinY && tmp != -1) {
          locMinY = tmp;
        }
      }
      // Estimate frame size, offsets and scaleFactor
      frameSizeX = locMaxX - locMinX;
      frameSizeY = locMaxY - locMinY;

      // Test whether all objects have the same x or y coordinate
      // If this is the case, scaling is being disabled in the corresponding dimension
      // If this check is not made, an infinity scale factor can occur
      if (frameSizeX == 0) {
        frameSizeX = 1;
      }
      if (frameSizeY == 0) {
        frameSizeY = 1;
      }

      scaleFactorX = (MAXIMUM_X - MINIMUM_X) / (frameSizeX * (MAXIMUM_X - MINIMUM_X));
      scaleFactorY = (MAXIMUM_Y - MINIMUM_Y) / (frameSizeY * (MAXIMUM_Y - MINIMUM_Y));

      offsetX = MINIMUM_X;
      offsetY = MINIMUM_Y;
    }

    // the oneDimensionNotSet is relevant only for testing purposes
    if (isOneDimensionNumber() || oneDimensionNotSet()) {
      for (BuildingBlock buildingBlock : buildingBlocks) {
        createBubbleShape(buildingBlock);
      }
    }
    else {
      createBubbles();
    }
    // Call the shape creation method for each shape

    LOGGER.debug("leaving addResultNodes...");
  }

  private double calculateMediumValueOfDiametersInTile(Map<BuildingBlock, Double> mappedDiameterToBuildingBlock, List<BuildingBlock> bBList) {
    double sum = 0;
    for (BuildingBlock block : bBList) {
      double diameterForBlock = mappedDiameterToBuildingBlock.get(block).doubleValue();
      sum = sum + diameterForBlock;
    }
    return sum / bBList.size();
  }

  /**
   * @param vertical
   *          true for a vertical rectangle, false means horizontal rectangle
   */
  private void createAxisValueRect(String text, boolean vertical, double weight, double dim, boolean dimIsNumber) throws MasterNotFoundException {
    double angle;
    Coordinates coords = null;
    if (vertical) {
      angle = VISIO_VERTICAL_ANGLE;
      if (dimIsNumber) {
        coords = new Coordinates(MINIMUM_X - AXIS_VALUES_FROM_COSY_DISTANCE, (weight * (MAXIMUM_Y - MINIMUM_Y)) + MINIMUM_Y);
      }
      else {
        if (weight == 0.0) {
          coords = new Coordinates(MINIMUM_X - AXIS_VALUES_FROM_COSY_DISTANCE, (weight * (MAXIMUM_Y - MINIMUM_Y)) + MINIMUM_Y + dim / 2);
        }
        else {
          if (weight == 1.0) {
            coords = new Coordinates(MINIMUM_X - AXIS_VALUES_FROM_COSY_DISTANCE, (weight * (MAXIMUM_Y - MINIMUM_Y)) + MINIMUM_Y - dim / 2);
          }
          else {
            coords = new Coordinates(MINIMUM_X - AXIS_VALUES_FROM_COSY_DISTANCE, (weight * (MAXIMUM_Y - MINIMUM_Y)) + MINIMUM_Y);
          }
        }
      }
    }
    else {
      angle = VISIO_HORIZONTAL_ANGLE;
      if (dimIsNumber) {
        coords = new Coordinates((weight * (MAXIMUM_X - MINIMUM_X)) + MINIMUM_X, MINIMUM_Y - AXIS_VALUES_FROM_COSY_DISTANCE);
      }
      else {
        if (weight == 0.0) {
          coords = new Coordinates((weight * (MAXIMUM_X - MINIMUM_X)) + dim / 2 + MINIMUM_X, MINIMUM_Y - AXIS_VALUES_FROM_COSY_DISTANCE);
        }
        else {
          if (weight == 1.0) {
            coords = new Coordinates((weight * (MAXIMUM_X - MINIMUM_X)) - dim / 2 + MINIMUM_X, MINIMUM_Y - AXIS_VALUES_FROM_COSY_DISTANCE);
          }
          else {
            coords = new Coordinates((weight * (MAXIMUM_X - MINIMUM_X)) + MINIMUM_X, MINIMUM_Y - AXIS_VALUES_FROM_COSY_DISTANCE);
          }
        }
      }
    }
    createRectStructure(text, angle, coords);
  }

  private void createBubbles() {

    List<Tile> tiles = createTiles();
    Map<BuildingBlock, Double> mapBuildingBlocksToDiameter = this.mapBuildingBlockToDiameter();

    scalingNeeded(mapBuildingBlocksToDiameter, tiles);

    //    double queryInfoHeight = createQueryInfo();

    if (scaling < 1) {
      doScaling(mapBuildingBlocksToDiameter);
    }

    for (Tile tile : tiles) {
      List<PositionInTile> possiblePositionsInTile;
      Map<BuildingBlock, Double> mapBuildingBlocksInTile = getMapBuildingBlocksInTile(mapBuildingBlocksToDiameter, tile);
      Map<BuildingBlock, Double> sortedBuildingBlocksInTile = sortMap(mapBuildingBlocksInTile);
      double biggestDiameterInTile = findBiggestDiameterInTile(sortedBuildingBlocksInTile, tile.getBuildingBlocks());
      if (portfolioOptions.getSizeAttributeId().intValue() > 0) {
        possiblePositionsInTile = possiblePositionsInTileWithScaling(sortedBuildingBlocksInTile, tile, biggestDiameterInTile);
      }
      else {
        possiblePositionsInTile = possiblePositionsInTile(tile, biggestDiameterInTile);
      }

      int index = 0;

      // Create shapes
      for (Map.Entry<BuildingBlock, Double> entry : sortedBuildingBlocksInTile.entrySet()) {
        BuildingBlock block = entry.getKey();
        double diameter = mapBuildingBlocksToDiameter.get(block).doubleValue();
        double radius = diameter * 0.5;
        String hierarchicalBubbleName = getBuildingBlockHierarchicalName(block);
        String nonhierarchicalBubbleName = getBuildingBlockNonHierarchicalName(block);
        String screenName = getScreenName(portfolioOptions, nonhierarchicalBubbleName, hierarchicalBubbleName, "", diameter, 8, "");

        PositionInTile position = possiblePositionsInTile.get(index);
        if (index < possiblePositionsInTile.size() - 1) {
          index++;
        }

        double x = position.getX().doubleValue();
        double y = position.getY().doubleValue();

        try {
          setBubbleShape(x, y, screenName, block, diameter, radius, false);
        } catch (MasterNotFoundException e) {
          e.printStackTrace();
        }

      }
    }
  }

  private void createBubbleShape(BuildingBlock block) throws MasterNotFoundException {

    // calculate relative radius of bubble
    // ***********************************
    double weight = bubbleSpace.getSizeDimension().getValue(block).doubleValue();
    boolean missing = false;

    // if size isn't set then create a medium size bubble and set the
    // "missing" property (which currently leads to a hatched bubble)
    if (doubleEqual(weight, -1)) {
      weight = 0.4;

      // if no attribute is chosen for size then don't hatch the bubbles
      if (!"".equals(bubbleSpace.getSizeDimension().getName())) {
        missing = true;
      }
    }

    // the visio template specifies the diameter of a bubble as: 30mm * radius.
    // Calculation is done here since weight gets changed later
    double diameter = InchConverter.mmToInches(30) * (weight * 0.6 + 0.5);
    double radius = diameter * 0.5;

    // calculate x- and y-position
    // ***************************
    double x;
    double y;

    // Estimate x position:
    weight = bubbleSpace.getXDimension().getValue(block).doubleValue();

    // If undefined:
    if (doubleEqual(weight, -1)) {
      weight = 1.14;
    }

    // If there is no scaling:
    x = weight * (MAXIMUM_X - MINIMUM_X) + MINIMUM_X;

    // If there is scaling
    if (portfolioOptions.isScalingEnabled()) {
      x = offsetX + weight * (MAXIMUM_X - MINIMUM_X) * scaleFactorX - locMinX * scaleFactorX * (MAXIMUM_X - MINIMUM_X);
    }

    // Explicitly set coordinate for undefined values:
    if (doubleEqual(weight, 1.14)) {
      x = weight * (MAXIMUM_X - MINIMUM_X) + MINIMUM_X;
    }

    // Estimate y position
    weight = bubbleSpace.getYDimension().getValue(block).doubleValue();

    // If undefined:
    if (doubleEqual(weight, -1)) {
      weight = 1.14;
    }

    // If there is no scaling:
    y = weight * (MAXIMUM_Y - MINIMUM_Y) + MINIMUM_Y;

    // If there is scaling
    if (portfolioOptions.isScalingEnabled()) {
      y = offsetY + weight * (MAXIMUM_Y - MINIMUM_Y) * scaleFactorY - locMinY * scaleFactorY * (MAXIMUM_Y - MINIMUM_Y);
    }

    // Explicitly set coordinate for undefined values:
    if (doubleEqual(weight, 1.14)) {
      y = weight * (MAXIMUM_Y - MINIMUM_Y) + MINIMUM_Y;
    }

    // Get bubbles' name
    String hierarchicalBubbleName = getBuildingBlockHierarchicalName(block);
    String nonhierarchicalBubbleName = getBuildingBlockNonHierarchicalName(block);
    String screenName = getScreenName(portfolioOptions, nonhierarchicalBubbleName, hierarchicalBubbleName, "", diameter, 8, "");

    // create shape, size and position it
    setBubbleShape(x, y, screenName, block, diameter, radius, missing);

  }

  private double createColorLegend() throws MasterNotFoundException {
    double colorLegendHeightInch = 0;
    if (!"".equals(bubbleSpace.getColorDimension().getName())) {
      double colorLegendX = getTargetPage().getDocument().getMaster(VISIO_SHAPE_NAME_COLOR_BUBBLE).getShapes()[0].getPinX();
      Coordinates position = new Coordinates(colorLegendX, namesLegendFrameMaxY);
      colorLegendHeightInch = createColorLegend(portfolioOptions.getColorOptionsBean(), this.getTargetPage(), position,
          VISIO_SHAPE_NAME_COLOR_BUBBLE, bubbleSpace.getColorDimension().getName(),
          TypeOfBuildingBlock.getTypeOfBuildingBlockByString(portfolioOptions.getSelectedBbType()));
    }
    return colorLegendHeightInch;
  }

  private void createCoordinateLabels(String xLabel, String yLabel) throws MasterNotFoundException {

    Shape shape = this.getTargetPage().createNewShape(VISIO_SHAPE_NAME_XAXIS);
    shape.setFieldValue(xLabel);

    shape = this.getTargetPage().createNewShape(VISIO_SHAPE_NAME_YAXIS);
    shape.setFieldValue(yLabel);
    shape.setAngle(VISIO_VERTICAL_ANGLE);
  }

  private void createCoordinateSystem() throws MasterNotFoundException {
    this.getTargetPage().createNewShape(VISIO_SHAPE_NAME_COSY);
    createGridLines(bubbleSpace.getXDimension(), true);
    createGridLines(bubbleSpace.getYDimension(), false);
  }

  /**
   * Creates Labels for the grid of an axis of the portfolio matrix
   */
  private void createGridLabels(Dimension<Double> dim, boolean vertical) throws MasterNotFoundException {
    DimensionAdapter<?> adapter = dim.getAdapter();
    boolean isNumberAt = adapter instanceof AttributeAdapter;

    double heightTile = COSY_SIDE / bubbleSpace.getYDimension().getMapping().size();
    double widthTile = COSY_SIDE / bubbleSpace.getXDimension().getMapping().size();

    if (isNumberAt) {
      isNumberAt = (((AttributeAdapter) adapter).getAttributeType() instanceof NumberAT) && !(adapter instanceof AttributeRangeAdapter);

    }
    Map<String, Double> mapping = dim.getMapping();
    List<String> values = new ArrayList<String>(mapping.keySet()); // dim.getValues();
    if (isNumberAt) {
      AttributeType attrType = ((AttributeAdapter) adapter).getAttributeType();
      NumberAT numberAt = (NumberAT) attrType;
      values = getNumberValuesForLegend(numberAt, dim.getValues(), portfolioOptions);
      if (!values.isEmpty()) {
        if (values.size() == 1) {
          createAxisValueRect(values.get(0), vertical, 0.5, 0.0, isNumberAt);
        }
        else {
          int size = values.size();
          double stepFraction = 1.0 / (size - 1.0);
          for (int i = 0; i < size; i++) {
            createAxisValueRect(values.get(i), vertical, i * stepFraction, 0.0, isNumberAt);
          }
        }
      }
    }
    else {
      for (int i = 0; i < values.size(); i++) {
        String value = values.get(i);
        if (vertical) {
          createAxisValueRect(value, vertical, mapping.get(value).doubleValue(), heightTile, isNumberAt);
        }
        else {
          createAxisValueRect(value, vertical, mapping.get(value).doubleValue(), widthTile, isNumberAt);
        }
      }
    }
    createAxisValueRect(MessageAccess.getStringOrNull(DimensionOptionsBean.DEFAULT_VALUE, getLocale()), vertical, 1.14, 0.0, isNumberAt);
  }

  private void createGridLines(Dimension<Double> dim, boolean vertical) throws MasterNotFoundException {
    DimensionAdapter<?> adapter = dim.getAdapter();
    boolean isNumberAt = adapter instanceof AttributeAdapter;

    if (isNumberAt) {
      isNumberAt = (((AttributeAdapter) adapter).getAttributeType() instanceof NumberAT) && !(adapter instanceof AttributeRangeAdapter);
    }
    Map<String, Double> mapping = dim.getMapping();
    List<String> values = new ArrayList<String>(mapping.keySet()); // dim.getValues();
    if (isNumberAt) {
      AttributeType attrType = ((AttributeAdapter) adapter).getAttributeType();
      NumberAT numberAt = (NumberAT) attrType;
      values = getNumberValuesForLegend(numberAt, dim.getValues(), portfolioOptions);

      if (!values.isEmpty() && values.size() > 1) {
        int size = values.size();
        for (int i = 0; i < size - 1; i++) {
          if (vertical) {
            double widthTile = COSY_SIDE / (size - 1);
            createLine(MINIMUM_X + i * widthTile, MINIMUM_Y, MINIMUM_Y + i * widthTile, MAXIMUM_Y, VALUE_LINESTYLE_SOLID);
          }
          else {
            double heightTile = COSY_SIDE / (size - 1);
            createLine(MINIMUM_X, MINIMUM_Y + i * heightTile, MAXIMUM_X, MINIMUM_Y + i * heightTile, VALUE_LINESTYLE_SOLID);
          }
        }
      }
    }
    else {
      for (int i = 1; i < values.size(); i++) {
        if (vertical) {
          double widthTile = COSY_SIDE / values.size();
          createLine(MINIMUM_X + i * widthTile, MINIMUM_Y, MINIMUM_Y + i * widthTile, MAXIMUM_Y, VALUE_LINESTYLE_SOLID);
        }
        else {
          double heightTile = COSY_SIDE / values.size();
          createLine(MINIMUM_X, MINIMUM_Y + i * heightTile, MAXIMUM_X, MINIMUM_Y + i * heightTile, VALUE_LINESTYLE_SOLID);
        }
      }
    }
  }

  private Shape createLegend(boolean realPosition) throws MasterNotFoundException {

    Shape visioLegendContainer = this.getTargetPage().createNewShape(VisioAttributeLegend.VISIO_SHAPE_NAME_LEGEND_GROUP_CONTAINER);

    double legendHeight;
    double legendBaseY;

    // create dimension column
    // ***********************
    Shape shape = visioLegendContainer.createNewInnerShape(VISIO_SHAPE_NAME_DIMHEADER);
    shape.setFieldValue(MessageAccess.getStringOrNull("graphicalReport.headline", getLocale()));

    // Note that here we initialize some of the variables necessary to determine the frame for the
    // names legend.
    legendFrameBaseX = shape.getPinX();
    legendFrameWidth = shape.getWidth();
    legendHeight = 6 * shape.getHeight();
    legendBaseY = shape.getPinY() - legendHeight + shape.getHeight();

    shape.setPosition(0, legendHeight - shape.getHeight());

    shape = visioLegendContainer.createNewInnerShape(VISIO_SHAPE_NAME_DIMCONTENT);
    shape.setFieldValue(MessageAccess.getStringOrNull("reports.xaxis", getLocale()));
    double pinY = legendHeight - 2 * shape.getHeight();
    double height = shape.getHeight();
    shape.setPosition(0, pinY);

    shape = visioLegendContainer.createNewInnerShape(VISIO_SHAPE_NAME_DIMCONTENT);
    shape.setFieldValue(MessageAccess.getStringOrNull("reports.yaxis", getLocale()));
    shape.setPosition(0, pinY - height);

    shape = visioLegendContainer.createNewInnerShape(VISIO_SHAPE_NAME_DIMCONTENT);
    shape.setFieldValue(MessageAccess.getStringOrNull("reports.size", getLocale()));
    shape.setPosition(0, pinY - 2 * height);

    shape = visioLegendContainer.createNewInnerShape(VISIO_SHAPE_NAME_DIMCONTENT);
    shape.setFieldValue(MessageAccess.getStringOrNull("reports.color", getLocale()));
    shape.setPosition(0, pinY - 3 * height);

    shape = visioLegendContainer.createNewInnerShape(VISIO_SHAPE_NAME_DIMCONTENT);
    shape.setFieldValue(MessageAccess.getStringOrNull("reports.position", getLocale()));
    shape.setPosition(0, pinY - 4 * height);

    double secondColX = shape.getWidth();

    // create attribute column
    // ***********************
    shape = visioLegendContainer.createNewInnerShape(VISIO_SHAPE_NAME_ATTRHEADER);
    shape.setFieldValue(MessageAccess.getStringOrNull("global.attribute", getLocale()));
    shape.setPosition(secondColX, legendHeight - shape.getHeight());

    legendFrameWidth = legendFrameWidth + shape.getWidth();

    shape = visioLegendContainer.createNewInnerShape(VISIO_SHAPE_NAME_ATTRCONTENT);
    shape.setFieldValue(getFieldValueFromDimension(bubbleSpace.getXDimension()));
    shape.setPosition(secondColX, pinY);

    shape = visioLegendContainer.createNewInnerShape(VISIO_SHAPE_NAME_ATTRCONTENT);
    shape.setFieldValue(getFieldValueFromDimension(bubbleSpace.getYDimension()));
    shape.setPosition(secondColX, pinY - height);

    shape = visioLegendContainer.createNewInnerShape(VISIO_SHAPE_NAME_ATTRCONTENT);
    shape.setFieldValue(getFieldValueFromDimension(bubbleSpace.getSizeDimension()));
    shape.setPosition(secondColX, pinY - 2 * height);

    shape = visioLegendContainer.createNewInnerShape(VISIO_SHAPE_NAME_ATTRCONTENT);
    shape.setFieldValue(getFieldValueFromDimension(bubbleSpace.getColorDimension()));
    shape.setPosition(secondColX, pinY - 3 * height);

    shape = visioLegendContainer.createNewInnerShape(VISIO_SHAPE_NAME_ATTRCONTENT);
    if (realPosition) {
      shape.setFieldValue(MessageAccess.getStringOrNull("reports.position.description", getLocale()));
    }
    else {
      String positionText = MessageAccess.getStringOrNull("reports.position.description.discrete", getLocale());
      //When the SVG Version of this diagramm is generated the last blank in this text is left out; an extra blank is added before
      //the final dot, blank which has to be taken out fpr the Visio version.
      String text = positionText.substring(0, positionText.length() - 2);
      text = text.concat(positionText.substring(positionText.length() - 1));
      shape.setFieldValue(text);
    }
    shape.setPosition(secondColX, pinY - 4 * height);

    visioLegendContainer.setSize(legendFrameWidth, legendHeight);
    visioLegendContainer.setPosition(legendFrameBaseX, legendBaseY);

    namesLegendFrameMaxY = legendBaseY - InchConverter.cmToInches(LEGEND_MARGIN_CM);

    return visioLegendContainer;
  }

  /**
   * Create a line.
   * 
   * @param startX
   * @param startY
   * @param endX
   * @param endY
   * @param style
   * @throws MasterNotFoundException
   */
  private void createLine(double startX, double startY, double endX, double endY, String style) throws MasterNotFoundException {

    Shape shape = this.getTargetPage().createNewShape(SHAPE_LINE);
    shape.setBeginPosition(startX, startY);
    shape.setEndPosition(endX, endY);

    // set shape's geometry
    // note: the index parameter is known from the visio template.
    shape.setFirstVertexOfGeometry(startX, startY);
    shape.setLineEnd(2, startX, startY);

    // format the line depending on the style
    if ("dottedFine".equals(style)) {
      shape.setLinePattern(10);
      shape.setLineWeight(InchConverter.ptToInches(0.54, 72));
    }
    else if ("dottedCoarse".equals(style)) {
      shape.setLinePattern(16);
      shape.setLineWeight(InchConverter.ptToInches(0.54, 72));
    }
    // solid
    else {
      shape.setLinePattern(1);
      shape.setLineWeight(InchConverter.ptToInches(0.72, 72));
    }
  }

  private Map<BuildingBlock, Weight> createMapBBToWeightXY() {
    Map<BuildingBlock, Weight> mappedWeightXYToBB = Maps.newHashMap();
    for (BuildingBlock block : buildingBlocks) {
      Double weightX = bubbleSpace.getXDimension().getValue(block);
      Double weightY = bubbleSpace.getYDimension().getValue(block);
      mappedWeightXYToBB.put(block, Weight.of(weightX, weightY));
    }
    return mappedWeightXYToBB;
  }

  private void createQueryInfo(Shape legend) throws MasterNotFoundException {
    List<Shape> queryInfo = null;
    if (portfolioOptions.isShowSavedQueryInfo()) {
      double width = InchConverter.inchesToCm(legendFrameWidth);
      queryInfo = createSavedQueryInfo(new Coordinates(0, 0), width, 11, portfolioOptions.getSavedQueryInfo(), portfolioOptions.getServerUrl());
    }
    double queryInfoHeight = 0;
    if (queryInfo != null && !queryInfo.isEmpty()) {
      queryInfoHeight = getQueryInfoHeight(queryInfo);
      setQueryInfoPos(queryInfo, legendFrameBaseX, getTargetPage().getHeight() - queryInfoHeight - DISTANCE_TO_MARGIN_INCHES * 2);
      legend.setPosition(legend.getPinX(), legend.getPinY() - queryInfoHeight);
      namesLegendFrameMaxY -= queryInfoHeight;
    }
  }

  private void createRectStructure(String text, double angle, Coordinates coords) throws MasterNotFoundException {

    Shape shape = this.getTargetPage().createNewShape(VISIO_SHAPE_NAME_RECT);
    shape.setPosition(coords.getX(), coords.getY());
    shape.setAngle(angle);
    shape.setFieldValue(text);
  }

  private double createSizeLegend() throws MasterNotFoundException {
    // Create size legend
    double sizeLegendHeightInch = 0;
    if (!"".equals(bubbleSpace.getSizeDimension().getName())) {
      SizeDimension sizeDimension = bubbleSpace.getSizeDimension();
      SizeDimension newSizeDimension;
      if (scaling < 1) {
        newSizeDimension = resize(sizeDimension);
      }
      else {
        newSizeDimension = sizeDimension;
      }
      VisioAttributeLegend<Double> sizeLegend = createSizeLegend(newSizeDimension);
      sizeLegendHeightInch = sizeLegend.getLegendHeightInInch();
      Coordinates pos = sizeLegend.getPosition();
      pos.setY(namesLegendFrameMaxY);
      sizeLegend.setPosition(pos);
    }
    return sizeLegendHeightInch;
  }

  /**
   * Creates the visio index structures for the size dimension.
   *
   * @param dimension
   *          The size dimension for which the index structures should be created.
   * @param pos
   *          position to draw the legend
   * @return The legend height in inch.
   * @throws MasterNotFoundException
   */
  private VisioAttributeLegend<Double> createSizeLegend(Dimension<Double> dimension) throws MasterNotFoundException {

    VisioAttributeLegend<Double> legend = new VisioSizeLegend();
    legend.initializeLegend(this.getTargetPage(), dimension, dimension.getName(), getLocale());
    legend.createLegendEntries(VISIO_SHAPE_NAME_SIZE_ENTITY);
    return legend;
  }

  private List<Tile> createTiles() {
    List<Tile> tiles = new ArrayList<Tile>();
    Map<BuildingBlock, Weight> mappedBBToWeightXY = createMapBBToWeightXY();
    int nrTilesX = bubbleSpace.getXDimension().getMapping().size();
    int nrTilesY = bubbleSpace.getYDimension().getMapping().size();

    double heightTile = COSY_SIDE / nrTilesY;
    double widthTile = COSY_SIDE / nrTilesX;

    for (BuildingBlock block : buildingBlocks) {
      Tile tile = new Tile();
      Weight pairWeightBB = mappedBBToWeightXY.get(block);
      double xWeightBB = pairWeightBB.getX().doubleValue();
      double yWeightBB = pairWeightBB.getY().doubleValue();
      double xCorLeftCornerTile;
      double yCorLeftCornerTile;
      if (xWeightBB < 0) {
        xCorLeftCornerTile = COSY_SIDE;
      }
      else {
        xCorLeftCornerTile = widthTile * Math.round(xWeightBB * (nrTilesX - 1));
      }
      if (yWeightBB < 0) {
        yCorLeftCornerTile = COSY_SIDE - heightTile / 2;
      }
      else {
        yCorLeftCornerTile = heightTile * Math.round(yWeightBB * (nrTilesY - 1));
      }
      boolean foundTile = false;
      for (Tile t : tiles) {
        if (Double.compare(t.getxPos(), xCorLeftCornerTile) == 0 && Double.compare(t.getyPos(), yCorLeftCornerTile) == 0) {
          tile = t;
          tile.getBuildingBlocks().add(block);
          foundTile = true;
          break;
        }
      }
      if (!foundTile) {
        tile.setxPos(xCorLeftCornerTile);
        tile.setyPos(yCorLeftCornerTile);
        tile.setWidth(widthTile);
        tile.setHeight(heightTile);
        List<BuildingBlock> bBL = new LinkedList<BuildingBlock>();
        bBL.add(block);
        tile.setBuildingBlocks(bBL);
        tiles.add(tile);
      }
    }
    return tiles;
  }

  private void doScaling(Map<BuildingBlock, Double> mapBuildingBlocksToDiameter) {
    for (Map.Entry<BuildingBlock, Double> entryBBDiameter : mapBuildingBlocksToDiameter.entrySet()) {
      double initialDiameter = entryBBDiameter.getValue().doubleValue();
      entryBBDiameter.setValue(Double.valueOf(initialDiameter * scaling));
    }
  }

  private boolean doubleEqual(double first, double second) {
    return Math.abs(first - second) < 0.000001;
  }

  private double findBiggestDiameterInTile(Map<BuildingBlock, Double> mappedDiameterToBuildingBlock, List<BuildingBlock> bBList) {
    double diameter = 0;
    for (BuildingBlock block : bBList) {
      double diameterForBlock = mappedDiameterToBuildingBlock.get(block).doubleValue();
      if (diameterForBlock > diameter) {
        diameter = diameterForBlock;
      }
    }
    return diameter;
  }

  private Map<BuildingBlock, Double> getMapBuildingBlocksInTile(Map<BuildingBlock, Double> mapBuildingBlocksToDiameter, Tile tile) {
    List<BuildingBlock> bbInTile = tile.getBuildingBlocks();
    Map<BuildingBlock, Double> mapBBInTile = new HashMap<BuildingBlock, Double>();
    for (Map.Entry<BuildingBlock, Double> entry : mapBuildingBlocksToDiameter.entrySet()) {
      if (bbInTile.contains(entry.getKey())) {
        mapBBInTile.put(entry.getKey(), entry.getValue());
      }
    }
    return mapBBInTile;
  }

  private String getTitle(String bbType) {
    StringBuilder title = new StringBuilder();
    title.append(MessageAccess.getStringOrNull("graphicalExport.portfolio.title", getLocale())).append(" - ");
    title.append(MessageAccess.getStringOrNull("graphicalExport.reportDate", getLocale())).append(" ");
    title.append(DateUtils.formatAsStringToLong(new Date(), getLocale())).append("\n");
    title.append(MessageAccess.getStringOrNull("graphicalExport.title.content", getLocale())).append(": ");
    title.append(MessageAccess.getStringOrNull(bbType, getLocale()));

    return title.toString();
  }

  private boolean isOneDimensionNumber() {
    DimensionAdapter<?> adapterX = bubbleSpace.getXDimension().getAdapter();
    boolean isNumberAtX = adapterX instanceof AttributeAdapter;
    if (isNumberAtX) {
      isNumberAtX = (((AttributeAdapter) adapterX).getAttributeType() instanceof NumberAT) && !(adapterX instanceof AttributeRangeAdapter);
    }
    DimensionAdapter<?> adapterY = bubbleSpace.getYDimension().getAdapter();
    boolean isNumberAtY = adapterY instanceof AttributeAdapter;
    if (isNumberAtY) {
      isNumberAtY = (((AttributeAdapter) adapterY).getAttributeType() instanceof NumberAT) && !(adapterY instanceof AttributeRangeAdapter);
    }
    return (isNumberAtX || isNumberAtY);
  }

  private Map<BuildingBlock, Double> mapBuildingBlockToDiameter() {
    Map<BuildingBlock, Double> mappedBuildingBlockToDiameter = Maps.newHashMap();
    for (BuildingBlock block : buildingBlocks) {
      double weight = bubbleSpace.getSizeDimension().getValue(block).doubleValue();
      if (doubleEqual(weight, -1)) {
        weight = 0.4;
      }
      double diameter = InchConverter.mmToInches(30) * (weight * 0.6 + 0.5);
      mappedBuildingBlockToDiameter.put(block, Double.valueOf(diameter));
    }
    return mappedBuildingBlockToDiameter;
  }

  private boolean oneDimensionNotSet() {
    return bubbleSpace.getXDimension().getMapping().isEmpty() || bubbleSpace.getYDimension().getMapping().isEmpty();
  }

  private List<PositionInTile> possiblePositionsInTile(Tile tile, double diameter) {
    List<PositionInTile> coorPairs = Lists.newArrayList();
    double spacing = 3;
    double spacingInInches = InchConverter.ptToInches(spacing, DEFAULT_SYSTEM_DPI);
    double radius = diameter / 2;
    double height = tile.getHeight();
    double width = tile.getWidth();
    double nextXPos = MINIMUM_X + tile.getxPos() + radius + spacingInInches;
    double nextYPos = MINIMUM_Y + tile.getyPos() + height - (radius + spacingInInches);
    double maxX = MINIMUM_X + tile.getxPos() + width - radius - spacingInInches;
    double maxY = MINIMUM_Y + tile.getyPos() + (radius + spacingInInches);
    while (nextXPos < maxX) {
      while (nextYPos > maxY) {
        coorPairs.add(PositionInTile.of(Double.valueOf(nextXPos), Double.valueOf(nextYPos)));
        nextYPos -= diameter + spacingInInches;
      }
      nextXPos += diameter + spacingInInches;
      nextYPos = MINIMUM_Y + tile.getyPos() + height - (radius + spacingInInches);
    }

    return coorPairs;
  }

  private List<PositionInTile> possiblePositionsInTileWithScaling(Map<BuildingBlock, Double> mapBuildingBlocksToDiameter, Tile tile, double diameter) {
    List<PositionInTile> coorPairs = Lists.newArrayList();
    double spacing = 3;
    double spacingInInches = InchConverter.ptToInches(spacing, DEFAULT_SYSTEM_DPI);
    double radius = diameter / 2;
    double height = tile.getHeight();
    double width = tile.getWidth();
    double nextXPos = MINIMUM_X + tile.getxPos() + radius + spacingInInches;
    double nextYPos = MINIMUM_Y + tile.getyPos() + height - (radius + spacingInInches);
    double baseMaxX = MINIMUM_X + tile.getxPos() + width - spacingInInches;
    double baseMaxY = MINIMUM_Y + tile.getyPos() + spacingInInches;
    double maxX = MINIMUM_X + tile.getxPos() + width - radius - spacingInInches;
    double maxY = MINIMUM_Y + tile.getyPos() + (radius + spacingInInches);
    Set<Entry<BuildingBlock, Double>> bBSet = mapBuildingBlocksToDiameter.entrySet();
    Iterator<Entry<BuildingBlock, Double>> it = bBSet.iterator();
    while (nextXPos < maxX) {
      double biggestDiameterCol = 0;
      double smallestDiameterCol = diameter;
      while (nextYPos > maxY) {
        coorPairs.add(PositionInTile.of(Double.valueOf(nextXPos), Double.valueOf(nextYPos)));
        if (it.hasNext()) {
          Entry<BuildingBlock, Double> next = it.next();
          double findNewDiameter = next.getValue().doubleValue();
          biggestDiameterCol = findNewDiameter > biggestDiameterCol ? findNewDiameter : biggestDiameterCol;
          smallestDiameterCol = findNewDiameter < smallestDiameterCol ? findNewDiameter : smallestDiameterCol;
          nextYPos -= findNewDiameter + spacingInInches;
          maxY = baseMaxY + smallestDiameterCol / 2;
        }
        else {
          nextYPos -= diameter + spacingInInches;
          maxY = baseMaxY + radius;
        }
      }
      if (it.hasNext()) {
        nextXPos += (biggestDiameterCol + smallestDiameterCol) / 2 + spacingInInches;
        maxX = baseMaxX - (biggestDiameterCol + smallestDiameterCol) / 4;
        if (nextXPos > maxX && nextXPos - maxX < smallestDiameterCol) {
          maxX = baseMaxX;
        }
      }
      else {
        nextXPos += diameter + spacingInInches;
        maxX = baseMaxX - radius;
      }
      nextYPos = MINIMUM_Y + tile.getyPos() + height - (radius + spacingInInches);
    }

    return coorPairs;
  }

  /**
   * @param sizeDimension
   */
  private SizeDimension resize(SizeDimension sizeDimension) {
    SizeDimension sizeDim = new SizeDimension(sizeDimension.getAdapter());
    Map<String, Double> sizedimMap = new HashMap<String, Double>();
    Map<String, Double> sizeDimensionMap = sizeDimension.getMapping();
    for (Map.Entry<String, Double> entry : sizeDimensionMap.entrySet()) {
      double currentValue = entry.getValue().doubleValue();
      double newValue = currentValue * scaling;
      sizedimMap.put(entry.getKey(), Double.valueOf(newValue));
    }
    sizeDim.setMapping(sizedimMap);
    return sizeDim;
  }

  private void scalingNeeded(Map<BuildingBlock, Double> mapBuildingBlocksToDiameter, List<Tile> tiles) {
    int spacing = 6;
    double spacingInInches = InchConverter.ptToInches(spacing, DEFAULT_SYSTEM_DPI);
    for (Tile tile : tiles) {
      Map<BuildingBlock, Double> mapBuildingBlocksInTile = getMapBuildingBlocksInTile(mapBuildingBlocksToDiameter, tile);
      double middleValueOfDiametersInTile = calculateMediumValueOfDiametersInTile(mapBuildingBlocksInTile, tile.getBuildingBlocks());
      int nrBBTile = tile.getBuildingBlocks().size();
      long nrBBInRow = Math.round(Math.sqrt(nrBBTile) + 1);
      double neededDim = nrBBInRow * (middleValueOfDiametersInTile + spacingInInches);
      if (tile.getWidth() < neededDim || tile.getHeight() < neededDim) {
        double scalingWidth = tile.getWidth() / neededDim;
        double scalingHeight = tile.getHeight() / neededDim;
        double possibleScaling = scalingWidth < scalingHeight ? scalingWidth : scalingHeight;
        if (scaling > possibleScaling) {
          scaling = possibleScaling;
        }
      }
    }
  }

  private Map<BuildingBlock, Double> sortMap(Map<BuildingBlock, Double> mapBuildingBlocksInTile) {
    List<Entry<BuildingBlock, Double>> list = new LinkedList<Entry<BuildingBlock, Double>>(mapBuildingBlocksInTile.entrySet());
    Collections.sort(list, new Comparator<Map.Entry<BuildingBlock, Double>>() {
      public int compare(Entry<BuildingBlock, Double> o1, Entry<BuildingBlock, Double> o2) {
        return -((Comparable<Double>) (o1).getValue()).compareTo((o2).getValue());
      }
    });

    Map<BuildingBlock, Double> result = new LinkedHashMap<BuildingBlock, Double>();
    for (Iterator<Entry<BuildingBlock, Double>> it = list.iterator(); it.hasNext();) {
      Map.Entry<BuildingBlock, Double> entry = it.next();
      result.put(entry.getKey(), entry.getValue());
    }
    return result;
  }

  private void setBubbleShape(double x, double y, String bubbleName, BuildingBlock block, double diameter, double radius, boolean missing)
      throws MasterNotFoundException {
    Shape shape = this.getTargetPage().createNewShape(VISIO_SHAPE_NAME_BUBBLE);
    shape.setPosition(x, y);
    shape.setFillForegroundColor(getColorStr(bubbleSpace.getColorDimension().getValue(block)), 0);

    // display bubble's text.
    shape.setShapeText(bubbleName);

    // adjust bubble's width and height to correct size.
    //
    // note for visio viewer:
    // it does not work to set xform/width and xform/height in the document. the bubble has to
    // be "modeled" via a first vertex and two arcs, that is, two half-circles in the case of
    // bubbles.
    // additionally, to behave correctly the center of rotation has to be set to the center of the
    // bubble. the formulas for calculating the values have been taken form the visio template.
    //
    // note:
    // the index parameters 2 and 3 for the setEllipticalArc() method are known values (by
    // inspecting
    // the resulting visio document). this is unfortunately a quick-n-dirty solution to get at the
    // correct elements.
    shape.setSize(diameter, diameter);
    shape.setLocPin(radius, radius);
    shape.setFirstVertexOfGeometry(0, radius);
    shape.setEllipticalArc(2, diameter, radius, radius, diameter, 0, 1);
    shape.setEllipticalArc(3, 0, radius, radius, 0, 0, 1);
    if (missing) {
      shape.setLinePattern(9);
    }

    // transform textfield to match bubble's position and size.
    //
    // note: the formulas for calculating the values have been taken from the visio template.
    // 20mm calcualtes to 0.787401575 inches.

    // We keep this as an example for the usage of the textXForm although it is no longer needed
    // after having introduced a names legend
    // double txtWidth = Math.max(radius * 0.875, 0.787401575);
    // double txtHeight = diameter * 0.75;
    // shape.setTextXForm(radius, radius, txtWidth, txtHeight, txtWidth * 0.5, txtHeight * 0.5, 0);

    // set calculated dimension properties to be able to access them in the visio document
    Map<String, Object> props = addLabels(block);
    shape.setCustomProperties(props);
  }

  private void revertYAxesForVisio() {
    if (!getAttributeTypeService().isNumberAT(portfolioOptions.getYAxisAttributeId())) {
      PositionDimension yDimension = bubbleSpace.getYDimension();
      Map<String, Double> mapping = yDimension.getMapping();
      Map<String, Double> newMapping = new HashMap<String, Double>();
      for (Entry<String, Double> entry : mapping.entrySet()) {
        double value = entry.getValue().doubleValue();
        newMapping.put(entry.getKey(), Double.valueOf(1.0 - value));
      }
      yDimension.setMapping(newMapping);
      bubbleSpace.setYDimension(yDimension);
    }
  }
}

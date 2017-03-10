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
package de.iteratec.iteraplan.businesslogic.exchange.svg;

import java.awt.Color;
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

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.AttributeAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.AttributeRangeAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.Dimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.DimensionAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.portfolio.BubbleSpace;
import de.iteratec.iteraplan.businesslogic.exchange.common.portfolio.PositionInTile;
import de.iteratec.iteraplan.businesslogic.exchange.common.portfolio.Tile;
import de.iteratec.iteraplan.businesslogic.exchange.common.portfolio.Weight;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.IPortfolioOptions;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.svg.model.BasicShape;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.Shape;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.styling.SvgBaseStyling;


@SuppressWarnings("PMD.TooManyMethods")
public class SvgBubbleExport extends SvgExport {

  private static final String       SVG_TEMPLATE_FILE                    = "/SVGBubbleTemplate.svg";

  /**
   * Portfolio-specific master shapes
   */
  private static final String       SVG_BUBBLE                           = "BubbleRoot";
  private static final String       SVG_COORDINATE_SYSTEM                = "CoordinateSystemRoot";
  private static final String       SVG_SIZE_ENTITY                      = "Size-EntityRoot";
  private static final String       SVG_DIM_LABEL_BOX                    = "LegendDimensionLabelRectangleRoot";
  private static final String       SVG_DIM_ATTRIBUTE_BOX                = "LegendDimensionAttributeRectangleRoot";
  private static final String       MASTER_LINE_LEGEND_BOX               = "LineLegendBoxRoot";

  /*
   * Note. We work with units, not with centimeters or inches. Only the fonts are set in points.
   */

  private static final double       PAGE_WIDTH                           = 2376;
  private static final double       PAGE_HEIGHT                          = 1680;

  private static final double       COSY_BASE_X                          = 160;
  private static final double       COSY_BASE_Y                          = 380;
  private static final double       COSY_SIDE                            = 1140;

  private static final double       LEGEND_BASE_X                        = 1700;
  private static final double       DIM_LEGEND_BASE_Y                    = 90;
  private static final double       COLOR_LEGEND_BASE_Y                  = 390;
  private static final double       SIZE_LEGEND_BASE_X                   = 2000;

  private static final double       BUBBLE_WEIGHT_TO_SCREEN_UNITS_FACTOR = 80;

  private static final String       BUBBLE_LINE_PATTERN_MISSING          = "bubbleMissingLineStyle";
  private static final String       BUBBLE_LINE_PATTERN_DEFAULT          = "bubbleDefaultLineStyle";
  private static final double       BUBBLE_TEXT_SIZE_PT                  = 8;

  private static final String       UNSPECIFIED_VALUE                    = "unspecified";

  private static final double       NAME_LEGEND_FIT_IN_PAGE_WIDTH        = 560;
  private static final double       PAGE_MARGIN                          = 68;

  private double                    dimLegendBoxWidth;
  private double                    dimAttributeBoxWidth;

  // Scaling properties
  private double                    scaleFactorX                         = 1;
  private double                    scaleFactorY                         = 1;

  private double                    smallestXValue;
  private double                    smallestYValue;

  // Others
  private final List<BuildingBlock> buildingBlocks;

  private final IPortfolioOptions   portfolioOptions;

  private BubbleSpace               bubbleSpace;

  public SvgBubbleExport(List<? extends BuildingBlock> blocks, IPortfolioOptions portfolioOptions, AttributeTypeService attributeTypeService,
      AttributeValueService attributeValueService) {

    super(attributeTypeService, attributeValueService);
    this.buildingBlocks = new ArrayList<BuildingBlock>(blocks);
    this.portfolioOptions = portfolioOptions;
  }

  @Override
  public Document createDiagram() {

    init();

    if (portfolioOptions.isUseNamesLegend()) {
      this.setSvgNamesLegend(new SvgNamesLegend(getSvgDocument()));
    }

    try {

      bubbleSpace = createBubbleSpace(portfolioOptions, buildingBlocks);
      setColorDimension(createColorDimension(portfolioOptions.getColorOptionsBean(),
          TypeOfBuildingBlock.getTypeOfBuildingBlockByString(portfolioOptions.getSelectedBbType())));

      // Generate the CSS classes for the bubble colors
      generateCssColorStyles(getColorDimension());
      createLinePatternCssClasses();

      // Globally disable CSS for shape styling
      getSvgDocument().setShapeCSSStylingEnabled(false);

      // Create shapes
      setPageProperties();
      createCoordinateSystem();

      if (!portfolioOptions.isNakedExport()) {
        createLogos(0, 0, PAGE_WIDTH, PAGE_HEIGHT);
        createDiagramTitle(getTitleString(), 0, 0, COSY_BASE_X);
        createGeneratedInformation(PAGE_WIDTH, PAGE_HEIGHT);
      }

      double queryInfoHeight = createQueryInfo();

      if (!(portfolioOptions.getColorOptionsBean().getDimensionAttributeId().intValue() == GraphicalExportBaseOptions.NOTHING_SELECTED)) {
        createColorLegend(getColorDimension(), new Coordinates(LEGEND_BASE_X, COLOR_LEGEND_BASE_Y + queryInfoHeight), MASTER_COLOR_LEGEND_FIELD,
            getColorDimension().getName(), null);
      }

      createAxes();
      createGridLabels();
      if (isOneDimensionNumber() || oneDimensionNotSet()) {
        createBubblesNumberDim();
        createDimLegend(queryInfoHeight, true);
      }
      else {
        createBubbles();
        createDimLegend(queryInfoHeight, false);
      }

      if (portfolioOptions.isUseNamesLegend()) {
        createNamesLegend(queryInfoHeight);
      }

      setCustomSize(portfolioOptions.getWidth(), portfolioOptions.getHeight());

      getSvgDocument().finalizeDocument();

    } catch (SvgExportException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (IteraplanException ex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    }

    return getSvgDocument();
  }

  private boolean oneDimensionNotSet() {
    return bubbleSpace.getXDimension().getMapping().isEmpty() || bubbleSpace.getYDimension().getMapping().isEmpty();
  }

  private double calculateMediumValueOfDiametersInTile(Map<BuildingBlock, Double> mappedDiameterToBuildingBlock, List<BuildingBlock> bBList) {
    double sum = 0;
    for (BuildingBlock block : bBList) {
      double diameterForBlock = mappedDiameterToBuildingBlock.get(block).doubleValue();
      sum = sum + diameterForBlock;
    }
    return Math.round(sum / bBList.size() + 1);
  }

  private double calculateXCor(BuildingBlock block, double diameter) {
    double xCor;
    double shiftX;
    double weightX = bubbleSpace.getXDimension().getValue(block).doubleValue();

    // Unspecified
    if (weightX == -1) {
      xCor = COSY_BASE_X + COSY_SIDE * 1.14 - diameter / 2;
    }
    else {
      xCor = COSY_BASE_X + weightX * COSY_SIDE * scaleFactorX - diameter / 2;
      if (portfolioOptions.isScalingEnabled()) {
        shiftX = smallestXValue * COSY_SIDE * scaleFactorX;
        xCor = xCor - shiftX;
      }
    }
    return xCor;
  }

  private double calculateYCor(BuildingBlock block, double diameter, boolean isYNumberAt) {
    double coordinateSystemToPageBottomMargin = 160;
    double yCor;
    double shiftY;
    double weightY;
    if (isYNumberAt) {
      weightY = bubbleSpace.getYDimension().getValue(block).doubleValue();
    }
    else {
      weightY = 1 - bubbleSpace.getYDimension().getValue(block).doubleValue();
    }

    // Unspecified
    if (weightY == -1) {
      yCor = COSY_BASE_Y - 0.14 * COSY_SIDE - diameter / 2;
    }
    else {
      yCor = PAGE_HEIGHT - COSY_SIDE * weightY * scaleFactorY - coordinateSystemToPageBottomMargin - diameter / 2;

      if (portfolioOptions.isScalingEnabled()) {
        shiftY = smallestYValue * COSY_SIDE * scaleFactorY;
        yCor = yCor + shiftY;
      }
    }
    return yCor;
  }

  private void createAxes() throws SvgExportException {
    // Create axis labels
    Shape shape = createNewShape(MASTER_TITLE);
    shape.setTextFieldValue(bubbleSpace.getXDimension().getName());
    shape.setPosition(COSY_BASE_X + 0.5 * COSY_SIDE, PAGE_HEIGHT - MARGIN);
    shape.addTextCSSClass("axisTitle");

    shape = createNewShape(MASTER_TITLE);
    shape.setTextFieldValue(bubbleSpace.getYDimension().getName());
    shape.setPosition(MARGIN, COSY_BASE_Y + 0.5 * COSY_SIDE);
    shape.setAngle(270);
    shape.addTextCSSClass("axisTitle");

  }

  private void createBubbles() throws SvgExportException {

    if (portfolioOptions.isScalingEnabled()) {
      // Estimate scale factors
      estimateScaleFactors(bubbleSpace.getXDimension(), bubbleSpace.getYDimension());
    }

    List<Tile> tiles = createTiles();
    Map<BuildingBlock, Shape> mappedBuildingBlockToShape = this.mapBuildingBlockToShape();
    Map<BuildingBlock, Double> mapBuildingBlocksToDiameter = this.mapBuildingBlockToDiameter(mappedBuildingBlockToShape);

    double scaling = scalingNeeded(mapBuildingBlocksToDiameter, tiles);

    double queryInfoHeight = createQueryInfo();

    if (scaling < 1) {
      doScaling(scaling, mappedBuildingBlockToShape, mapBuildingBlocksToDiameter);
      createSizeLegend(queryInfoHeight, scaling);
    }
    else {
      createSizeLegend(queryInfoHeight, 1.00);
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
        List<Color> colorsForShape = bubbleSpace.getColorDimension().getMultipleValues(block);
        Shape bubble = mappedBuildingBlockToShape.get(block);
        if (colorsForShape.size() > 1) {
          bubble.addCSSClass("colorFieldTransparentStyle");
        }

        // Naming
        bubble.setTextFieldValue(estimateBubbleName(biggestDiameterInTile, block));

        PositionInTile position = possiblePositionsInTile.get(index);
        if (index < possiblePositionsInTile.size() - 1) {
          index++;
        }

        //Position
        bubble.setPosition(position.getX().doubleValue(), position.getY().doubleValue());
        bubble.setTextPosition(biggestDiameterInTile / 2, biggestDiameterInTile / 2);

        // If there is more than one value assignment for the color attribute draw segments in the
        //circle
        if (colorsForShape.size() > 1) {
          createMultipleColorAssignments(bubble, colorsForShape);
        }
        // Add XLink URL
        bubble.setXLink(retrieveXLinkUrlForIdentityEntity(block, portfolioOptions.getServerUrl()));
      }
    }
  }

  private void createBubblesNumberDim() throws SvgExportException {

    if (portfolioOptions.isScalingEnabled()) {
      // Estimate scale factors
      estimateScaleFactors(bubbleSpace.getXDimension(), bubbleSpace.getYDimension());
    }

    createSizeLegend(createQueryInfo(), 0.75);

    // Create shapes
    for (BuildingBlock block : buildingBlocks) {
      List<Color> colorsForShape = bubbleSpace.getColorDimension().getMultipleValues(block);
      Shape bubble = initializeBubbleShape(block);
      if (colorsForShape.size() > 1) {
        bubble.addCSSClass("colorFieldTransparentStyle");
      }

      // Size
      double diameter = determineBubbleSizeAndLinePattern(bubble, block);
      // Naming
      bubble.setTextFieldValue(estimateBubbleName(diameter, block));
      // Position
      double xCor = calculateXCor(block, diameter);
      double yCor = calculateYCor(block, diameter, this.getAttributeTypeService().isNumberAT(portfolioOptions.getYAxisAttributeId()));
      bubble.setPosition(xCor, yCor);
      bubble.setTextPosition(diameter / 2, diameter / 2);

      // If there is more than one value assignment for the color attribute draw segments in the
      // circle
      if (colorsForShape.size() > 1) {
        createMultipleColorAssignments(bubble, colorsForShape);
      }
      // Add XLink URL
      bubble.setXLink(retrieveXLinkUrlForIdentityEntity(block, portfolioOptions.getServerUrl()));
    }
  }

  private void createCoordinateSystem() throws SvgExportException {
    Shape coordinateSystem = createNewShape(SVG_COORDINATE_SYSTEM);
    coordinateSystem.setPosition(COSY_BASE_X, COSY_BASE_Y);
    createGridLines(bubbleSpace.getXDimension(), true);
    createGridLines(bubbleSpace.getYDimension(), false);
  }

  private void createDimLegend(double deltaY, boolean realPosition) throws SvgExportException {

    // Labels
    List<String> dimLabels = new ArrayList<String>();
    String dimLabelsHeader = MessageAccess.getStringOrNull("graphicalReport.headline", getLocale());
    dimLabels.add(MessageAccess.getStringOrNull("reports.xaxis", getLocale()));
    dimLabels.add(MessageAccess.getStringOrNull("reports.yaxis", getLocale()));
    dimLabels.add(MessageAccess.getStringOrNull("reports.size", getLocale()));
    dimLabels.add(MessageAccess.getStringOrNull("reports.color", getLocale()));
    dimLabels.add(MessageAccess.getStringOrNull("reports.position", getLocale()));

    List<BasicShape> entries = createBaseLegend(SVG_DIM_LABEL_BOX, new Coordinates(0, 0), new Coordinates(LEGEND_BASE_X, DIM_LEGEND_BASE_Y + deltaY),
        dimLabels, true, dimLabelsHeader, new Coordinates(0, 0), null, null);

    boolean first = true;
    for (BasicShape shape : entries) {
      if (first) {
        first = false;
        Shape aShape = (Shape) shape;
        aShape.getShapeTextCSSClassNames().remove(CSS_LEGEND_HEADER_TEXT);
      }
      else {
        shape.setShapeCSSEnabled(false);
      }
    }

    // Attributes
    String dimAttrHeader = MessageAccess.getStringOrNull("global.attribute", getLocale());
    List<String> dimAttrValues = new ArrayList<String>();
    List<String> dimAttrUrls = getDimensionUrls();

    dimAttrValues.add(getFullFieldValueFromDimension(bubbleSpace.getXDimension()));
    dimAttrValues.add(getFullFieldValueFromDimension(bubbleSpace.getYDimension()));
    dimAttrValues.add(getFullFieldValueFromDimension(bubbleSpace.getSizeDimension()));
    dimAttrValues.add(getFullFieldValueFromDimension(bubbleSpace.getColorDimension()));
    if (realPosition) {
      dimAttrValues.add(MessageAccess.getStringOrNull("reports.position.description", getLocale()));
    }
    else {
      dimAttrValues.add(MessageAccess.getStringOrNull("reports.position.description.discrete", getLocale()));
    }

    entries = createBaseLegend(SVG_DIM_ATTRIBUTE_BOX, new Coordinates(0, 0), new Coordinates(LEGEND_BASE_X + dimLegendBoxWidth, DIM_LEGEND_BASE_Y
        + deltaY), dimAttrValues, true, dimAttrHeader, new Coordinates(0, 0), dimAttrUrls, null);

    first = true;
    for (BasicShape entry : entries) {
      if (first) {
        first = false;
      }
      else {
        Shape shape = (Shape) entry;
        shape.setBoundTextInShape(true);
        shape.getShapeTextCSSClassNames().remove(CSS_LEGEND_CONTENT_TEXT);
      }
    }
  }

  private void createGridLabels() throws SvgExportException {
    createGridLabels(bubbleSpace.getXDimension(), false);
    createGridLabels(bubbleSpace.getYDimension(), true);
  }

  /**
   * Creates Labels for the grid of an axis of the portfolio matrix
   */
  private void createGridLabels(Dimension<Double> dim, boolean vertical) throws IteraplanException, SvgExportException {
    DimensionAdapter<?> adapter = dim.getAdapter();
    boolean isNumberAt = false;
    if (adapter instanceof AttributeAdapter && ((AttributeAdapter) adapter).getAttributeType() instanceof NumberAT) {
      isNumberAt = true;
    }

    double heightTile = COSY_SIDE / bubbleSpace.getYDimension().getMapping().size();
    double widthTile = COSY_SIDE / bubbleSpace.getXDimension().getMapping().size();

    if (isNumberAt) {
      isNumberAt = !(adapter instanceof AttributeRangeAdapter);

    }
    Map<String, Double> mapping = dim.getMapping();
    List<String> values = new ArrayList<String>(mapping.keySet()); // dim.getValues();
    if (isNumberAt) {
      AttributeType attrType = ((AttributeAdapter) adapter).getAttributeType();
      NumberAT numberAt = (NumberAT) attrType;
      values = getNumberValuesForLegend(numberAt, dim.getValues(), portfolioOptions);
      if (!values.isEmpty()) {
        if (values.size() == 1) {
          createGridValue(values.get(0), vertical, 0.5, 0.0, isNumberAt);
        }
        else {
          int size = values.size();
          double stepFraction = 1.0 / (size - 1.0);
          for (int i = 0; i < size; i++) {
            createGridValue(values.get(i), vertical, i * stepFraction, 0.0, isNumberAt);
          }
        }
      }
    }
    else {
      for (int i = 0; i < values.size(); i++) {
        String value = values.get(i);
        if (vertical) {
          createGridValue(value, vertical, mapping.get(value).doubleValue(), heightTile, isNumberAt);
        }
        else {
          createGridValue(value, vertical, mapping.get(value).doubleValue(), widthTile, isNumberAt);
        }
      }
    }
    createGridValue(MessageAccess.getStringOrNull(DimensionOptionsBean.DEFAULT_VALUE, getLocale()), vertical, 1.14, 0.0, isNumberAt);
  }

  private void createGridLines(Dimension<Double> dim, boolean vertical) throws SvgExportException {
    DimensionAdapter<?> adapter = dim.getAdapter();
    boolean isNumberAt = false;
    if (adapter instanceof AttributeAdapter && ((AttributeAdapter) adapter).getAttributeType() instanceof NumberAT) {
      isNumberAt = true;
    }

    if (isNumberAt) {
      isNumberAt = !(adapter instanceof AttributeRangeAdapter);
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
            createVerticalLine(new Coordinates(COSY_BASE_X + i * widthTile, COSY_BASE_Y), COSY_SIDE, CSS_LINE_SOLID);
          }
          else {
            double heightTile = COSY_SIDE / (size - 1);
            createHorizontalLine(new Coordinates(COSY_BASE_X, COSY_BASE_Y + i * heightTile), COSY_SIDE, CSS_LINE_SOLID);
          }
        }
      }
    }
    else {
      for (int i = 1; i < values.size(); i++) {
        if (vertical) {
          double widthTile = COSY_SIDE / values.size();
          createVerticalLine(new Coordinates(COSY_BASE_X + i * widthTile, COSY_BASE_Y), COSY_SIDE, CSS_LINE_SOLID);
        }
        else {
          double heightTile = COSY_SIDE / values.size();
          createHorizontalLine(new Coordinates(COSY_BASE_X, COSY_BASE_Y + i * heightTile), COSY_SIDE, CSS_LINE_SOLID);
        }
      }
    }
  }

  private void createGridValue(String textValue, boolean vertical, double weight, double dim, boolean dimIsNumber) throws SvgExportException {

    double xGridYPosition = 1612;
    double yGridXPosition = 68;

    Shape shape = createNewShape(MASTER_TITLE);
    if (textValue == null) {
      shape.setTextFieldValue(UNSPECIFIED_VALUE);
    }
    else {
      shape.setTextFieldValue(textValue);
    }
    shape.addTextCSSClass("gridValue");
    if (vertical) {
      shape.setAngle(270);
      if (dimIsNumber) {
        shape.setPosition(yGridXPosition, COSY_BASE_Y + COSY_SIDE - COSY_SIDE * weight);
      }
      else {
        if (!textValue.equals(MessageAccess.getStringOrNull(DimensionOptionsBean.DEFAULT_VALUE, getLocale()))) {
          if (weight == 0.0) {
            shape.setPosition(yGridXPosition, COSY_BASE_Y + COSY_SIDE * weight + dim / 2);
          }
          else {
            if (weight == 1.0) {
              shape.setPosition(yGridXPosition, COSY_BASE_Y + COSY_SIDE * weight - dim / 2);
            }
            else {
              shape.setPosition(yGridXPosition, COSY_BASE_Y + COSY_SIDE * weight);
            }
          }
        }
        else {
          shape.setPosition(yGridXPosition, COSY_BASE_Y + COSY_SIDE - COSY_SIDE * weight);
        }
      }
    }
    else {
      if (dimIsNumber) {
        shape.setPosition(COSY_BASE_X + COSY_SIDE * weight, xGridYPosition);
      }
      else {
        if (weight == 0.0) {
          shape.setPosition(COSY_BASE_X + COSY_SIDE * weight + dim / 2, xGridYPosition);
        }
        else {
          if (weight == 1.0) {
            shape.setPosition(COSY_BASE_X + COSY_SIDE * weight - dim / 2, xGridYPosition);
          }
          else {
            shape.setPosition(COSY_BASE_X + COSY_SIDE * weight, xGridYPosition);
          }
        }
      }
    }
  }

  private void createLinePatternCssClasses() throws SvgExportException {
    // Create CSS Style for the line patterns
    Map<String, String> linePatternMap = new HashMap<String, String>();
    linePatternMap.put(SvgBaseStyling.STROKE_LINE_PATTERN, "10");
    linePatternMap.put(SvgBaseStyling.STROKE_WITDH, "1");
    linePatternMap.put(SvgBaseStyling.STROKE_COLOR, "#000000");
    getSvgDocument().createNewCSSClass(BUBBLE_LINE_PATTERN_MISSING, linePatternMap);

    linePatternMap = new HashMap<String, String>();
    linePatternMap.put(SvgBaseStyling.STROKE_LINE_PATTERN, "none");
    linePatternMap.put(SvgBaseStyling.STROKE_WITDH, "1");
    linePatternMap.put(SvgBaseStyling.STROKE_COLOR, "#000000");
    getSvgDocument().createNewCSSClass(BUBBLE_LINE_PATTERN_DEFAULT, linePatternMap);
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

  private void createMultipleColorAssignments(Shape bubble, List<Color> colors) throws SvgExportException {

    double radius = bubble.getWidth() / 2;
    double centerX = bubble.getPinX() + radius;
    double centerY = bubble.getPinY() + radius;

    double segmentCentralAngleRadians = 2 * Math.PI / colors.size();

    for (int i = 0; i < colors.size(); i++) {
      BasicShape segment = createSegmentShape(bubble, centerX, centerY, radius, segmentCentralAngleRadians, i * segmentCentralAngleRadians + Math.PI
          / 2);
      segment.setPosition(centerX, centerY);
      segment.addCSSClass("colorSectorBaseStyle");
      segment.addCSSClass(getColorToColorClassMap().get(getColorStr(colors.get(i))));
    }
  }

  private void createNamesLegend(double deltaY) throws SvgExportException {

    int numberOfColors = getColorDimension().getValues().size();
    int numberOfSizes = bubbleSpace.getSizeDimension().getValues().size();

    if (getColorDimension().hasUnspecificValue()) {
      numberOfColors++;
    }
    if (bubbleSpace.getSizeDimension().hasUnspecificValue()) {
      numberOfSizes++;
    }

    double nameLegendColumnBaseY = COLOR_LEGEND_BASE_Y + (Math.max(numberOfColors, numberOfSizes) + 2) * LEGEND_BOX_HEIGHT + deltaY;

    double availableSpace = PAGE_HEIGHT - PAGE_MARGIN - nameLegendColumnBaseY;

    String title = getTitleString();

    super.createNamesLegend(LEGEND_BASE_X, nameLegendColumnBaseY, NAME_LEGEND_FIT_IN_PAGE_WIDTH, availableSpace, portfolioOptions.isNakedExport(),
        title);
  }

  private double createQueryInfo() throws SvgExportException {
    double queryInfoHeight = 0;
    if (portfolioOptions.isShowSavedQueryInfo()) {
      Coordinates pos = new Coordinates(LEGEND_BASE_X, DIM_LEGEND_BASE_Y);
      double width = dimLegendBoxWidth + dimAttributeBoxWidth;
      queryInfoHeight = createSavedQueryInfo(pos, width, portfolioOptions.getSavedQueryInfo(), portfolioOptions.getServerUrl()) - DIM_LEGEND_BASE_Y;
    }
    return queryInfoHeight;
  }

  private void createSizeLegend(double deltaY, double scaling) throws SvgExportException {
    if ("".equals(bubbleSpace.getSizeDimension().getName())) {
      return;
    }
    Dimension<Double> sizeDimension = bubbleSpace.getSizeDimension();

    List<String> textValues = new ArrayList<String>();
    textValues.addAll(sizeDimension.getValues());

    boolean hasUnspecified = sizeDimension.hasUnspecificValue();

    if (hasUnspecified) {
      textValues.add(MessageAccess.getStringOrNull(DimensionOptionsBean.DEFAULT_VALUE, getLocale()));
    }

    double weight;
    String headerString = sizeDimension.getName();
    List<BasicShape> entries = createBaseLegend(MASTER_LINE_LEGEND_BOX, new Coordinates(0, 0), new Coordinates(SIZE_LEGEND_BASE_X,
        COLOR_LEGEND_BASE_Y + deltaY), textValues, true, headerString, new Coordinates(0, 0), null, null);

    for (int count = 1; count < entries.size(); count++) {
      Shape shape = (Shape) entries.get(count);
      shape.setShapeCSSEnabled(false);

      if (!textValues.get(count - 1).equals(MessageAccess.getStringOrNull(DimensionOptionsBean.DEFAULT_VALUE, getLocale()))) {
        weight = getScreenLengthForBubbleWeight(sizeDimension.getValue(textValues.get(count - 1)).doubleValue());
      }
      else {
        weight = getScreenLengthForBubbleWeight(0.4);
      }

      Shape sizeEntity = shape.createNewInnerShape(SVG_SIZE_ENTITY);

      sizeEntity.setSize(weight * scaling, shape.getHeight() / 2);
      sizeEntity.setPosition(0.3 * shape.getWidth() - weight / 2, 0.25 * shape.getHeight());

      if (shape.getTextFieldValue().equals(MessageAccess.getStringOrNull(DimensionOptionsBean.DEFAULT_VALUE, getLocale()))) {
        sizeEntity.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_LINE_PATTERN, "8");
        sizeEntity.setPosition(0.3 * shape.getWidth() - weight / 2, 0.25 * shape.getHeight());
        sizeEntity.setSize(weight, shape.getHeight() / 2);
      }

    }

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
        yCorLeftCornerTile = -COSY_BASE_Y / 2;
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
        if (xWeightBB < 0) {
          tile.setWidth(widthTile / 2);
        }
        else {
          tile.setWidth(widthTile);
        }
        if (yWeightBB < 0) {
          tile.setHeight(heightTile / 2);
        }
        else {
          tile.setHeight(heightTile);
        }
        List<BuildingBlock> bBL = new LinkedList<BuildingBlock>();
        bBL.add(block);
        tile.setBuildingBlocks(bBL);
        tiles.add(tile);
      }
    }
    return tiles;
  }

  private double determineBubbleSizeAndLinePattern(Shape bubble, BuildingBlock block) throws SvgExportException {
    double diameter;
    double weightSize = bubbleSpace.getSizeDimension().getValue(block).doubleValue();
    if (weightSize == -1) {
      diameter = getScreenLengthForBubbleWeight(0.4);
      // Missing
      if (!"".equals(bubbleSpace.getSizeDimension().getName())) {
        bubble.addCSSClass(BUBBLE_LINE_PATTERN_MISSING);
      }
    }
    else {
      diameter = getScreenLengthForBubbleWeight(bubbleSpace.getSizeDimension().getValue(block).doubleValue());
    }
    if (!bubble.getShapeCSSClassNames().contains(BUBBLE_LINE_PATTERN_MISSING)) {
      bubble.addCSSClass(BUBBLE_LINE_PATTERN_DEFAULT);
    }

    bubble.setSize(diameter, diameter);
    return diameter;
  }

  /**
   * @param scaling
   * @param mappedBuildingBlockToShape
   * @param mapBuildingBlocksToDiameter
   */
  private void doScaling(double scaling, Map<BuildingBlock, Shape> mappedBuildingBlockToShape, Map<BuildingBlock, Double> mapBuildingBlocksToDiameter) {
    for (Map.Entry<BuildingBlock, Shape> entryBBShape : mappedBuildingBlockToShape.entrySet()) {
      Shape bubble = entryBBShape.getValue();
      double diameter = mapBuildingBlocksToDiameter.get(entryBBShape.getKey()).doubleValue();
      bubble.setSize(diameter * scaling, diameter * scaling);
    }
    for (Map.Entry<BuildingBlock, Double> entryBBDiameter : mapBuildingBlocksToDiameter.entrySet()) {
      double initialDiameter = entryBBDiameter.getValue().doubleValue();
      entryBBDiameter.setValue(Double.valueOf(initialDiameter * scaling));
    }
  }

  private String estimateBubbleName(double bubbleDiameter, BuildingBlock bubbleBlock) {

    String originalName = getBuildingBlockHierarchicalName(bubbleBlock);
    String ownName = getBuildingBlockNonHierarchicalName(bubbleBlock);

    String url = retrieveXLinkUrlForIdentityEntity(bubbleBlock, portfolioOptions.getServerUrl());
    return getScreenName(portfolioOptions, ownName, originalName, "", bubbleDiameter, BUBBLE_TEXT_SIZE_PT, url);
  }

  private void estimateScaleFactors(Dimension<Double> xDimension, Dimension<Double> yDimension) {

    // We use the coordinates of the first building block as initial coordinates. We can do this,
    // since the nonemptiness of the list of building blocks has been verified in the frontend
    // service.
    double initialX = xDimension.getValue(buildingBlocks.get(0)).doubleValue();
    double initialY = yDimension.getValue(buildingBlocks.get(0)).doubleValue();

    double minXValue = initialX;
    double maxXValue = initialX;
    double minYValue = initialY;
    double maxYValue = initialY;
    double tmpX;
    double tmpY;

    for (BuildingBlock block : buildingBlocks) {

      tmpX = xDimension.getValue(block).doubleValue();

      if (tmpX > maxXValue) {
        maxXValue = tmpX;
      }
      if ((tmpX < minXValue || minXValue < 0) && tmpX >= 0) {
        minXValue = tmpX;
      }

      tmpY = yDimension.getValue(block).doubleValue();

      if (tmpY > maxYValue) {
        maxYValue = tmpY;
      }
      if ((tmpY < minYValue || minYValue < 0) && tmpY >= 0) {
        minYValue = tmpY;
      }
    }
    if (xDimension.getAdapter() instanceof AttributeAdapter) {
      smallestXValue = minXValue;
      double frameSizeX = maxXValue - minXValue;
      if (frameSizeX == 0) {
        frameSizeX = 1;
      }
      scaleFactorX = 1 / frameSizeX;
    }

    if (yDimension.getAdapter() instanceof AttributeAdapter) {
      smallestYValue = minYValue;
      double frameSizeY = maxYValue - minYValue;
      if (frameSizeY == 0) {
        frameSizeY = 1;
      }
      scaleFactorY = 1 / frameSizeY;
    }
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

  private List<String> getDimensionUrls() {
    List<String> dimUrls = new ArrayList<String>();

    if (portfolioOptions.getXAxisAttributeId().intValue() > 0) {
      AttributeType xAxisAt = getAttributeTypeService().loadObjectById(portfolioOptions.getXAxisAttributeId());
      String xAxisUrl = retrieveXLinkUrlForIdentityEntity(xAxisAt, portfolioOptions.getServerUrl());
      dimUrls.add(xAxisUrl);
    }
    else {
      dimUrls.add("");
    }

    if (portfolioOptions.getYAxisAttributeId().intValue() > 0) {
      AttributeType yAxisAt = getAttributeTypeService().loadObjectById(portfolioOptions.getYAxisAttributeId());
      String yAxisUrl = retrieveXLinkUrlForIdentityEntity(yAxisAt, portfolioOptions.getServerUrl());
      dimUrls.add(yAxisUrl);
    }
    else {
      dimUrls.add("");
    }

    if (portfolioOptions.getSizeAttributeId().intValue() > 0) {
      AttributeType sizeAt = getAttributeTypeService().loadObjectById(portfolioOptions.getSizeAttributeId());
      String sizeUrl = retrieveXLinkUrlForIdentityEntity(sizeAt, portfolioOptions.getServerUrl());
      dimUrls.add(sizeUrl);
    }
    else {
      dimUrls.add("");
    }

    if (portfolioOptions.getColorOptionsBean().getDimensionAttributeId().intValue() > 0) {
      AttributeType colorAt = getAttributeTypeService().loadObjectById(portfolioOptions.getColorOptionsBean().getDimensionAttributeId());
      String colorUrl = retrieveXLinkUrlForIdentityEntity(colorAt, portfolioOptions.getServerUrl());
      dimUrls.add(colorUrl);
    }
    else {
      dimUrls.add("");
    }

    return dimUrls;
  }

  private Map<BuildingBlock, Double> getMapBuildingBlocksInTile(Map<BuildingBlock, Double> mapBuildingBlocksToDiameter, Tile tile) {
    List<BuildingBlock> bbInTile = tile.getBuildingBlocks();
    Map<BuildingBlock, Double> mapBBInTile = new HashMap<BuildingBlock, Double>();

    for (BuildingBlock bb : bbInTile) {
      mapBBInTile.put(bb, mapBuildingBlocksToDiameter.get(bb));
    }

    return mapBBInTile;
  }

  private double getScreenLengthForBubbleWeight(double bubbleWeight) {
    return (bubbleWeight + 0.7) * BUBBLE_WEIGHT_TO_SCREEN_UNITS_FACTOR;
  }

  private String getTitleString() {
    StringBuilder title = new StringBuilder();

    title.append(MessageAccess.getStringOrNull("graphicalExport.portfolio.title", getLocale())).append(" - ");
    title.append(MessageAccess.getStringOrNull("graphicalExport.reportDate", getLocale())).append(" ");
    title.append(DateUtils.formatAsStringToLong(new Date(), getLocale())).append("\n");

    title.append(MessageAccess.getStringOrNull("graphicalExport.title.content", getLocale())).append(": ");
    title.append(MessageAccess.getStringOrNull(portfolioOptions.getSelectedBbType(), getLocale()));

    return title.toString();
  }

  private void init() {
    loadSvgDocumentFromTemplate(SVG_TEMPLATE_FILE, "Portfolio");
    dimLegendBoxWidth = getSvgDocument().getMasterShapeProperties(SVG_DIM_LABEL_BOX).getShapeWidth();
    dimAttributeBoxWidth = getSvgDocument().getMasterShapeProperties(SVG_DIM_ATTRIBUTE_BOX).getShapeWidth();
  }

  private Shape initializeBubbleShape(BuildingBlock block) throws SvgExportException {
    Shape bubble = createNewShape(SVG_BUBBLE);
    bubble.addTextCSSClass("bubbleText");
    bubble.setBoundTextInShape(true);
    bubble.setResizeTextWithShape(false);
    bubble.setLineSpacing(BUBBLE_TEXT_SIZE_PT);
    bubble.setShapeCSSEnabled(true);

    bubble.addCSSClass(getColorToColorClassMap().get(getColorStr(bubbleSpace.getColorDimension().getValue(block))));
    return bubble;
  }

  private boolean isOneDimensionNumber() {
    DimensionAdapter<?> adapterX = bubbleSpace.getXDimension().getAdapter();

    boolean isNumberAtX = false;
    if (adapterX instanceof AttributeAdapter && ((AttributeAdapter) adapterX).getAttributeType() instanceof NumberAT) {
      isNumberAtX = true;
    }
    if (isNumberAtX) {
      isNumberAtX = !(adapterX instanceof AttributeRangeAdapter);
    }
    DimensionAdapter<?> adapterY = bubbleSpace.getYDimension().getAdapter();
    boolean isNumberAtY = false;
    if (adapterY instanceof AttributeAdapter && ((AttributeAdapter) adapterY).getAttributeType() instanceof NumberAT) {
      isNumberAtY = true;
    }
    if (isNumberAtY) {
      isNumberAtY = !(adapterY instanceof AttributeRangeAdapter);
    }
    return (isNumberAtX || isNumberAtY);
  }

  private Map<BuildingBlock, Double> mapBuildingBlockToDiameter(Map<BuildingBlock, Shape> mappedShapeToBuildingBlock) throws SvgExportException {
    Map<BuildingBlock, Double> mappedBuildingBlockToDiameter = Maps.newHashMap();
    for (Map.Entry<BuildingBlock, Shape> entry : mappedShapeToBuildingBlock.entrySet()) {
      Shape bubble = entry.getValue();
      BuildingBlock block = entry.getKey();
      double diameter = determineBubbleSizeAndLinePattern(bubble, block);
      mappedBuildingBlockToDiameter.put(block, Double.valueOf(diameter));
    }
    return mappedBuildingBlockToDiameter;
  }

  private Map<BuildingBlock, Shape> mapBuildingBlockToShape() throws SvgExportException {
    Map<BuildingBlock, Shape> mappedBuildingBlockToShape = Maps.newHashMap();
    for (BuildingBlock block : buildingBlocks) {
      Shape bubble = initializeBubbleShape(block);
      mappedBuildingBlockToShape.put(block, bubble);
    }
    return mappedBuildingBlockToShape;
  }

  private List<PositionInTile> possiblePositionsInTile(Tile tile, double diameter) {
    List<PositionInTile> coorPairs = new ArrayList<PositionInTile>();
    double spacing = 5;
    double nextXPos = COSY_BASE_X + tile.getxPos() + spacing;
    double nextYPos = COSY_BASE_Y + tile.getyPos() + spacing;
    double maxX = COSY_BASE_X + tile.getxPos() + tile.getWidth() - diameter - spacing;
    double maxY = COSY_BASE_Y + tile.getyPos() + tile.getHeight() - diameter - spacing;
    while (nextXPos < maxX) {
      while (nextYPos < maxY) {
        coorPairs.add(PositionInTile.of(Double.valueOf(nextXPos), Double.valueOf(nextYPos)));
        nextYPos += diameter + spacing;
      }
      nextXPos += diameter + spacing;
      nextYPos = COSY_BASE_Y + tile.getyPos() + spacing;
    }

    return coorPairs;
  }

  private List<PositionInTile> possiblePositionsInTileWithScaling(Map<BuildingBlock, Double> mapBuildingBlocksToDiameter, Tile tile, double diameter) {
    List<PositionInTile> coorPairs = new ArrayList<PositionInTile>();
    double spacing = 5;
    double nextXPos = COSY_BASE_X + tile.getxPos() + spacing;
    double nextYPos = COSY_BASE_Y + tile.getyPos() + spacing;
    double baseMaxX = COSY_BASE_X + tile.getxPos() + tile.getWidth() - spacing;
    double baseMaxY = COSY_BASE_Y + tile.getyPos() + tile.getHeight() - spacing;
    double maxX = COSY_BASE_X + tile.getxPos() + tile.getWidth() - diameter - spacing;
    double maxY = COSY_BASE_Y + tile.getyPos() + tile.getHeight() - diameter - spacing;
    Set<Entry<BuildingBlock, Double>> bBSet = mapBuildingBlocksToDiameter.entrySet();
    Iterator<Entry<BuildingBlock, Double>> it = bBSet.iterator();
    while (nextXPos < maxX) {
      double biggestDiameterCol = 0;
      double smallestDiameterCol = diameter;
      while (nextYPos < maxY) {
        coorPairs.add(PositionInTile.of(Double.valueOf(nextXPos), Double.valueOf(nextYPos)));
        if (it.hasNext()) {
          Entry<BuildingBlock, Double> next = it.next();
          double findNewDiameter = next.getValue().doubleValue();
          biggestDiameterCol = findNewDiameter > biggestDiameterCol ? findNewDiameter : biggestDiameterCol;
          smallestDiameterCol = findNewDiameter < smallestDiameterCol ? findNewDiameter : smallestDiameterCol;
          nextYPos += findNewDiameter + spacing;
          maxY = baseMaxY - biggestDiameterCol;
        }
        else {
          nextYPos += diameter + spacing;
          maxY = baseMaxY - diameter;
        }
      }
      if (it.hasNext()) {
        nextXPos += biggestDiameterCol + spacing;
        maxX = baseMaxX - smallestDiameterCol;
        if (nextXPos > maxX && nextXPos - maxX < smallestDiameterCol) {
          maxX = baseMaxX;
        }
      }
      else {
        nextXPos += diameter + spacing;
        maxX = baseMaxX - diameter;
      }
      nextYPos = COSY_BASE_Y + tile.getyPos() + spacing;
    }

    return coorPairs;
  }

  private double scalingNeeded(Map<BuildingBlock, Double> mapBuildingBlocksToDiameter, List<Tile> tiles) {
    double scaling = 1.0;
    for (Tile tile : tiles) {
      Map<BuildingBlock, Double> mapBuildingBlocksInTile = getMapBuildingBlocksInTile(mapBuildingBlocksToDiameter, tile);
      double middleValueOfDiametersInTile = calculateMediumValueOfDiametersInTile(mapBuildingBlocksInTile, tile.getBuildingBlocks());
      int nrBBTile = tile.getBuildingBlocks().size();
      long nrBBInRow = Math.round(Math.sqrt(nrBBTile) + 1);
      int spacing = 10;
      double neededDim = nrBBInRow * (middleValueOfDiametersInTile + spacing);
      if (tile.getWidth() < neededDim || tile.getHeight() < neededDim) {
        double scalingWidth = tile.getWidth() / neededDim;
        double scalingHeight = tile.getHeight() / neededDim;
        double possibleScaling = scalingWidth < scalingHeight ? scalingWidth : scalingHeight;
        if (scaling > possibleScaling) {
          scaling = possibleScaling;
        }
      }
    }
    return scaling;
  }

  private void setPageProperties() {
    getSvgDocument().setPageSize(PAGE_WIDTH, PAGE_HEIGHT);
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
}

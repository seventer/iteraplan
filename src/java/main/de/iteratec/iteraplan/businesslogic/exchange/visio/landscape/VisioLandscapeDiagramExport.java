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
package de.iteratec.iteraplan.businesslogic.exchange.visio.landscape;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorRangeDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.Dimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.LineDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.AxisElement;
import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.ContentElement;
import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.LandscapeDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.INamesLegend.LegendMode;
import de.iteratec.iteraplan.businesslogic.exchange.visio.VisioDimensionExport;
import de.iteratec.iteraplan.businesslogic.exchange.visio.legend.VisioAttributeLegend;
import de.iteratec.iteraplan.businesslogic.exchange.visio.legend.VisioNamesLegend;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ILandscapeOptions;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.common.util.InchConverter;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.visio.model.Document;
import de.iteratec.visio.model.DocumentFactory;
import de.iteratec.visio.model.Shape;
import de.iteratec.visio.model.exceptions.MasterNotFoundException;
import de.iteratec.visio.model.exceptions.NoSuchElementException;


/**
 * Export a diagram in matrix form, used in combination with the Visio template
 * 'VisioLandscapeTemplate'. This class does not use the gxl2visio functionality, but directly uses
 * the Visio element wrappers of the gxl2visio library to generate a {@link Document}.
 */
public class VisioLandscapeDiagramExport extends VisioDimensionExport {

  /**
   * The name and location of the Visio document to use as a template. This is searched on the
   * classpath.
   */
  private static final String                VISIO_TEMPLATE_FILE              = "/VisioLandscapeTemplate.vdx";

  /**
   * The width of a cell of the matrix body.
   */
  private static final double                CELL_WIDTH                       = 5.3;

  /**
   * The height of a cell of the matrix body.
   */
  private static final double                CELL_HEIGHT                      = 2.0;

  /**
   * The padding used inside the cells of the matrix body. This applies both vertically and
   * horizontally.
   */
  private static final double                CONTENT_PADDING                  = 0.10;

  /**
   * The height of a line in the top axis.
   */
  private static final double                TOP_AXIS_ELEMENT_HEIGHT          = 0.85;

  /**
   * The width of a line in the side axis.
   */
  private static final double                SIDE_AXIS_ELEMENT_WIDTH          = 1.4;

  /**
   * The height (top axis) or width (side axis) of an axis element of the deepest sub-level, i.e.
   * one that can accommodate three rows of text.
   */
  private static final double                BOTTOM_LEVEL_AXIS_ELEMENT_HEIGHT = 1.525;

  /**
   * The padding between shapes in a header. This applies both horizontally and vertically.
   */
  private static final double                AXIS_ELEMENT_PADDING             = 0.10;

  /**
   * 90 degrees in radians to be used for turning the vertical shapes.
   */
  private static final double                VERTICAL_ANGLE                   = Math.PI / 2;

  /**
   * The height of a legend shape.
   */
  private static final double                LEGEND_SHAPE_HEIGHT              = 0.58;

  /**
   * The width of a legend shape.
   */
  private static final double                LEGEND_SHAPE_WIDTH               = 2.58;

  /**
   * The height of the title.
   */
  private static final double                TITLE_HEIGHT                     = 0.2;

  /**
   * The distance from the left edge to the start of the diagram (title, left axis).
   */
  private static final double                MARGIN                           = 1.0;

  /**
   * The line pattern used for dotted lines. This is an ID as used by Visio (visible as number in
   * the line formatting dialog).
   */
  private static final int                   LINE_PATTERN_ID_DOTTED           = 3;

  /**
   * The weight of a dotted line. This can be used to additionally change the appearance of a dotted
   * line through a different weight. Measured in inches (72pt is one inch).
   */
  private static final double                LINE_WEIGHT_DOTTED               = 0.35 / 72;

  private static final double                DIN_A1_PAGE_WIDTH_INCH           = 33.1;
  private static final double                DIN_A1_PAGE_HEIGHT_INCH          = 23.4;

  private static final double                MAX_SCALE_FACTOR                 = 1.0;
  private static final double                MIN_AXIS_DOWNSCALE_FACTOR        = 0.6;

  /**
   * Maps certain classes of building blocks onto special Visio shapes. This is a Map<Class,String>,
   * which maps all objects of that particular class to a Visio Master of the given name, when it is
   * to be placed on the top axis.
   */
  private static final Map<Class<?>, String> TOP_AXIS_SHAPE_NAMES             = new HashMap<Class<?>, String>();
  {
    TOP_AXIS_SHAPE_NAMES.put(BusinessProcess.class, "Process");
  }

  /**
   * Maps certain classes of building blocks onto special Visio shapes. This is a Map<Class,String>,
   * which maps all objects of that particular class to a Visio Master of the given name, when it is
   * to be placed on the side axis.
   */
  private static final Map<Class<?>, String> SIDE_AXIS_SHAPE_NAMES            = new HashMap<Class<?>, String>();
  {
    SIDE_AXIS_SHAPE_NAMES.put(BusinessProcess.class, "Process_vertical");
  }

  /**
   * The name of the default Visio Master for shapes on the axes. This is used then there is no
   * specific entry in {@link #TOP_AXIS_SHAPE_NAMES} or {@link #SIDE_AXIS_SHAPE_NAMES}. TODO Im
   * Master und hier zum Zweck des besseren Verständnisses den Namen ändern.
   */
  private static final String                DEFAULT_AXIS_SHAPE_NAME          = "AxisElement";

  // legend shapes
  private static final String                LEGEND_TITLE_SHAPE_NAME          = "LegendTitle";
  private static final String                LEGEND_ITEM_SHAPE_NAME           = "LegendItem";
  private static final String                LEGEND_COLOR_SAMPLE_SHAPE_NAME   = "ColorSample";
  private static final String                LEGEND_LINE_SAMPLE_SHAPE_NAME    = "LineSample";

  // content and other shapes
  private static final String                CONTENT_SHAPE_NAME               = "Rectangle";
  private static final String                LABEL_SHAPE_NAME                 = "Label";
  private static final String                LINE_SHAPE_NAME                  = "Line";

  private static final int                   TOP_AXIS_FONT_SIZE_PT            = 12;
  private static final int                   SIDE_AXIS_FONT_SIZE_PT           = 12;

  private final LandscapeDiagram             landscapeDiagram;
  private final ILandscapeOptions            landscapeOptions;

  // Scaling attributes
  private double                             contentScaleFactorX              = 1;
  private double                             contentScaleFactorY              = 1;

  private double                             topAxisScaleY                    = 1;
  private double                             sideAxisScaleX                   = 1;

  private double                             topAxisToContentFrameOffsetX;
  private double                             sideAxisToContentFrameOffsetY;

  private double                             contentFrameWidth;
  private double                             contentFrameHeight;
  private double                             contentFrameBaseX;
  private double                             contentFrameBaseY;

  private final List<ShapeConfigurator<?>>   shapeConfigurators               = CollectionUtils.arrayList();

  private final Set<ShapeConfigurator<?>>    legendsToMove                    = new HashSet<ShapeConfigurator<?>>();

  private static final Logger                LOGGER                           = Logger.getIteraplanLogger(VisioLandscapeDiagramExport.class);

  public VisioLandscapeDiagramExport(LandscapeDiagram landscapeDiagram, ILandscapeOptions landscapeOptions,
      AttributeTypeService attributeTypeService, AttributeValueService attributeValueService) {
    super(attributeTypeService, attributeValueService);
    this.landscapeDiagram = landscapeDiagram;
    this.landscapeOptions = landscapeOptions;
    try {
      InputStream is = null;
      try {
        is = this.getClass().getResourceAsStream(VISIO_TEMPLATE_FILE);
        this.setVisioDocument(DocumentFactory.getInstance().loadDocument(is));
      } finally {
        if (is != null) {
          is.close();
        }
      }
      this.setTargetPage(this.getVisioDocument().getPage(0));
    } catch (NoSuchElementException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (IOException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (ParserConfigurationException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (SAXException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
  }

  /**
   * Runs the export algorithm. After execution, the generated visio XML can be written to an
   * OutputStream using write(OutputStream).
   * 
   * @return Returns the generated Visio document.
   * @throws MasterNotFoundException
   *           If a master shape could not be found on the diagram template.
   * @throws IteraplanException
   *           Iff a general exception occurs during the run.
   */
  @Override
  public Document createDiagram() throws IteraplanException {

    try {
      createShapeConfigurators();

      if (landscapeDiagram.isUseNamesLegend()) {
        setVisioNamesLegend(new VisioNamesLegend(this.getTargetPage()));
        getVisioNamesLegend().setLegendMode(LegendMode.NEW_PAGE);
      }

      initPage();
      Shape title = createDiagramTitle(landscapeDiagram.getTitle());
      double titleHeight = title.getHeight();

      List<Shape> queryInfos = createQueryInfo();
      titleHeight += getQueryInfoHeight(queryInfos) + MARGIN;

      initContentFrame(titleHeight);
      setTitlePosAndSize(title, MARGIN, getTargetPage().getHeight(), Double.valueOf(getTargetPage().getWidth() - MARGIN));
      setQueryInfoPos(queryInfos, MARGIN, getTargetPage().getHeight() - titleHeight + 0.9 * DISTANCE_TO_MARGIN_INCHES);

      createGeneratedInformation(getTargetPage().getWidth());

      createTopAxisElements();
      createSideAxisElements();
      createContentElements();
      createLogos(0, 0, getTargetPage().getWidth(), getTargetPage().getHeight());
      createLabels();
      createLines();
      createLegends();

    } catch (MasterNotFoundException e) {
      LOGGER.error("The master shape could not be found in the diagram template.");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
    return getVisioDocument();
  }

  /**
   * Creates the list of shape configurators from the options set in the memory bean and sets them
   * into the configuration object. Same logic as dimensions in all other graphics. Should be
   * generalized.
   */
  private void createShapeConfigurators() {
    ShapeConfiguratorLinePattern lineConfig = getLinePatternConfigurator();
    if (lineConfig != null) {
      shapeConfigurators.add(lineConfig);
    }
    ShapeConfiguratorColor colorConfig = getColorConfigurator();
    if (colorConfig != null) {
      shapeConfigurators.add(colorConfig);
    }
  }

  private ShapeConfiguratorColor getColorConfigurator() {

    ShapeConfiguratorColor sc = null;
    ColorDimensionOptionsBean colorOptions = landscapeOptions.getColorOptionsBean();
    Integer selectedColorAttribute = colorOptions.getDimensionAttributeId();

    if (selectedColorAttribute.intValue() != GraphicalExportBaseOptions.NOTHING_SELECTED) {
      ColorDimension dimension = createColorDimension(colorOptions,
          TypeOfBuildingBlock.getTypeOfBuildingBlockByString(landscapeOptions.getSelectedBbType()));
      // Transfer the selected default value to the color dimension
      if (colorOptions.getAttributeValues().size() > 0) {
        dimension.setDefaultValue(colorOptions.getSelectedColors().get(colorOptions.getSelectedColors().size() - 1));
      }
      sc = new ShapeConfiguratorColor(dimension);
    }
    return sc;
  }

  private ShapeConfiguratorLinePattern getLinePatternConfigurator() {
    ShapeConfiguratorLinePattern sc = null;
    LineDimensionOptionsBean lineOptions = landscapeOptions.getLineOptionsBean();
    Integer selectedLineTypeAttributeId = lineOptions.getDimensionAttributeId();

    if (selectedLineTypeAttributeId.intValue() != GraphicalExportBaseOptions.NOTHING_SELECTED) {
      LineDimension dimension = createLineDimension(selectedLineTypeAttributeId, getIntegerList(lineOptions.getSelectedLineTypes()),
          TypeOfBuildingBlock.getTypeOfBuildingBlockByString(landscapeOptions.getSelectedBbType()));
      // Transfer the selected default value to the line dimension
      if (lineOptions.getAttributeValues().size() > 0) {
        dimension.setDefaultValue(Integer.valueOf(lineOptions.getSelectedLineTypes().get(lineOptions.getSelectedLineTypes().size() - 1)));
      }
      sc = new ShapeConfiguratorLinePattern(dimension);
    }
    return sc;
  }

  private List<Integer> getIntegerList(List<String> input) {
    List<Integer> result = new ArrayList<Integer>();
    for (String value : input) {
      result.add(Integer.valueOf(value));
    }
    return result;
  }

  private List<Shape> createQueryInfo() throws MasterNotFoundException {
    List<Shape> queryInfos = null;
    if (landscapeOptions.isShowSavedQueryInfo()) {
      Coordinates pos = new Coordinates(MARGIN, 0);
      double width = InchConverter.inchesToCm(getTargetPage().getWidth() / 2) - 2 * MARGIN;
      queryInfos = createSavedQueryInfo(pos, width, 8, landscapeOptions.getSavedQueryInfo(), landscapeOptions.getServerUrl());
    }
    return queryInfos;
  }

  private void createLegends() throws MasterNotFoundException, IteraplanException {
    List<ShapeConfigurator<?>> shapeConfigs = this.shapeConfigurators;
    if (!shapeConfigs.isEmpty()) {
      double xPos, yPos;

      int movedLegendsCount = 0;
      int unmovedLegendsCount = 0;

      for (ShapeConfigurator<?> sc : shapeConfigs) {

        //Override the coordinates of the legend if necessary, i.e. if the legend is to be placed in a new page
        if (legendsToMove.contains(sc)) {
          xPos = InchConverter.inchesToCm(getTargetPage().getWidth()) + 2.5 + (LEGEND_SHAPE_WIDTH + MARGIN) * movedLegendsCount;
          yPos = InchConverter.inchesToCm(getTargetPage().getHeight()) - 3;
          getVisioNamesLegend().addOffset(LEGEND_SHAPE_WIDTH + MARGIN);
          movedLegendsCount++;
        }
        else {
          //Recover coordinates for the standard case
          xPos = contentFrameBaseX + contentFrameWidth - LEGEND_SHAPE_WIDTH * shapeConfigs.size() + LEGEND_SHAPE_WIDTH * unmovedLegendsCount;
          yPos = contentFrameBaseY - MARGIN * 0.5;
          unmovedLegendsCount++;
        }

        Shape visioLegendContainer = this.getTargetPage().createNewShape(VisioAttributeLegend.VISIO_SHAPE_NAME_LEGEND_GROUP_CONTAINER);

        double legendHeight = sc.getValues().size() + 1;
        if (sc.hasUnspecificValue()) {
          legendHeight++;
        }
        legendHeight = legendHeight * LEGEND_SHAPE_HEIGHT;
        double legendWidth = LEGEND_SHAPE_WIDTH;

        visioLegendContainer.setPosition(InchConverter.cmToInches(xPos), InchConverter.cmToInches(yPos - legendHeight));
        visioLegendContainer.setSize(InchConverter.cmToInches(legendWidth), InchConverter.cmToInches(legendHeight));

        double innerYCor = legendHeight;

        createLegendTitle(visioLegendContainer, 0, innerYCor, sc.getLabel());
        innerYCor = innerYCor - LEGEND_SHAPE_HEIGHT;
        List<String> values = sc.getValues();

        for (String val : values) {
          String prefix = getPrefix(sc.getDimension(), val);
          createLegendItem(visioLegendContainer, 0, innerYCor, prefix + val);
          createSample(visioLegendContainer, 0, innerYCor, sc, val);
          innerYCor = innerYCor - LEGEND_SHAPE_HEIGHT;
        }

        if (sc.hasUnspecificValue()) {
          createLegendItem(visioLegendContainer, 0, innerYCor, null);
          createSample(visioLegendContainer, 0, innerYCor, sc, null);
        }
      }
    }

    // Create the names legend
    if (landscapeDiagram.isUseNamesLegend()) {
      createNamesLegend(0, 0, 0, 0, false, landscapeDiagram.getTitle());
    }

  }

  private String getPrefix(Dimension<?> dim, String val) {
    String prefix = "";
    if (dim instanceof ColorRangeDimension) {
      prefix = MessageAccess.getStringOrNull(((ColorRangeDimension) dim).getLegendPrefixKeyFor(val), getLocale());
      prefix += ": ";
    }
    return prefix;
  }

  private void createLegendTitle(Shape visioLegendContainer, double x, double y, String label) throws MasterNotFoundException {

    double widthInch = InchConverter.cmToInches(LEGEND_SHAPE_WIDTH);
    double heightInch = InchConverter.cmToInches(LEGEND_SHAPE_HEIGHT);

    Shape shape = visioLegendContainer.createNewInnerShape(LEGEND_TITLE_SHAPE_NAME);
    shape.setPosition(InchConverter.cmToInches(x), InchConverter.cmToInches(y));
    shape.setCharSize(InchConverter.ptToInches(7, DEFAULT_SYSTEM_DPI));
    shape.setSize(widthInch, heightInch);
    shape.setLocPin(0, heightInch);

    // geometry has to be set explicitly for correct display in visio viewer
    // note: the index parameters are known from the visio template.
    shape.setFirstVertexOfGeometry(0, 0);
    shape.setLineEnd(2, widthInch, 0);
    shape.setLineEnd(3, widthInch, heightInch);
    shape.setLineEnd(4, 0, heightInch);
    shape.setLineEnd(5, 0, 0);
    shape.setCharSize(InchConverter.ptToInches(7, DEFAULT_SYSTEM_DPI));
    shape.setFieldValue(label);
  }

  private void createLegendItem(Shape visioLegendContainer, double x, double y, String valueString) throws MasterNotFoundException {

    String label;

    if (valueString != null) {
      label = valueString;
    }
    else {
      label = MessageAccess.getStringOrNull("graphicalExport.landscape.legend.undefined", UserContext.getCurrentLocale());
    }

    double widthInch = InchConverter.cmToInches(LEGEND_SHAPE_WIDTH);
    double heightInch = InchConverter.cmToInches(LEGEND_SHAPE_HEIGHT);

    Shape shape = visioLegendContainer.createNewInnerShape(LEGEND_ITEM_SHAPE_NAME);
    shape.setPosition(InchConverter.cmToInches(x), InchConverter.cmToInches(y));
    shape.setCharSize(InchConverter.ptToInches(6, DEFAULT_SYSTEM_DPI));
    shape.setSize(widthInch, heightInch);
    shape.setLocPin(0, heightInch);
    shape.setTextBlockLeftMargin(heightInch + InchConverter.ptToInches(2, DEFAULT_SYSTEM_DPI));

    // geometry has to be set explicitly for correct display in visio viewer
    // note: the index parameters are known from the visio template.
    shape.setFirstVertexOfGeometry(heightInch, 0);
    shape.setLineEnd(2, 0, 0);
    shape.setLineEnd(3, 0, heightInch);
    shape.setLineEnd(4, widthInch, heightInch);
    shape.setLineEnd(5, widthInch, 0);
    shape.setLineEnd(6, heightInch, 0);
    shape.setLineEnd(7, heightInch, heightInch);
    shape.setCharSize(InchConverter.ptToInches(6, DEFAULT_SYSTEM_DPI));
    shape.setFieldValue(label);
  }

  private void createSample(Shape visioLegendContainer, double x, double y, ShapeConfigurator<?> sc, String val) throws MasterNotFoundException {

    Shape shape;

    if (sc.affectsLine()) {
      shape = visioLegendContainer.createNewInnerShape(LEGEND_LINE_SAMPLE_SHAPE_NAME);

      double sideInch = InchConverter.cmToInches(LEGEND_SHAPE_HEIGHT);
      shape.setPosition(InchConverter.cmToInches(x), InchConverter.cmToInches(y));
      shape.setSize(sideInch, sideInch);
      shape.setLocPin(0, sideInch);

      double width = shape.getWidth();
      double height = shape.getHeight();
      shape.setFirstVertexOfGeometry(width * 0.1, height * 0.5);
      shape.setLineEnd(2, width * 0.9, height * 0.5);
    }
    else {
      shape = visioLegendContainer.createNewInnerShape(LEGEND_COLOR_SAMPLE_SHAPE_NAME);

      double sideInch = InchConverter.cmToInches(LEGEND_SHAPE_HEIGHT);
      shape.setPosition(InchConverter.cmToInches(x), InchConverter.cmToInches(y));
      // samples are square using height as side length
      shape.setSize(sideInch, sideInch);
      shape.setLocPin(0, sideInch);

      // geometry has to be set explicitly for correct display in visio viewer
      // note: the index parameters are known from the visio template.
      shape.setFirstVertexOfGeometry(0, 0);
      shape.setLineEnd(2, sideInch, 0);
      shape.setLineEnd(3, sideInch, sideInch);
      shape.setLineEnd(4, 0, sideInch);
      shape.setLineEnd(5, 0, 0);
    }

    // sc.configureShapeByAttributeValue(shape, val);
    sc.configureShape(shape, val);
  }

  private void createLines() throws MasterNotFoundException {

    // top line
    double startHorX = contentFrameBaseX + topAxisToContentFrameOffsetX;
    double startHorY = contentFrameBaseY + contentFrameHeight - sideAxisToContentFrameOffsetY;
    double endHorX = contentFrameBaseX + contentFrameWidth;
    double endHorY = startHorY;
    createLine(startHorX, startHorY, endHorX, endHorY, false);

    // side line
    double startVerX = startHorX;
    double startVerY = startHorY;
    double endVerX = startHorX;
    double endVerY = contentFrameBaseY;
    createLine(startVerX, startVerY, endVerX, endVerY, false);

    // vertical grid lines
    int totalCount = 0;
    List<AxisElement<?>> edgeElements = landscapeDiagram.getTopAxis().getElements();
    for (int elCount = 0; elCount < edgeElements.size(); elCount++) {
      AxisElement<?> currentEl = edgeElements.get(elCount);
      double displayLength = currentEl.getDisplayLength() - currentEl.getChildrenDisplayLength();
      for (int i = 0; i < displayLength; i++) {
        if (totalCount > 0) {
          double startGridX = startVerX + totalCount * CELL_WIDTH * contentScaleFactorX;
          double startGridY = startVerY;
          double endGridX = startGridX;
          double endGridY = endVerY;
          boolean dotted = true;
          if (landscapeDiagram.isContentScalesVertically() && i == 0) {
            dotted = false;
          }
          createLine(startGridX, startGridY, endGridX, endGridY, dotted);
        }
        totalCount++;
      }
    }
    // final line to frame the matrix
    double finalX = startVerX + totalCount * CELL_WIDTH * contentScaleFactorX;
    createLine(finalX, startVerY, finalX, endVerY, false);

    // horizontal grid lines
    totalCount = 0;
    edgeElements = landscapeDiagram.getSideAxis().getElements();
    for (int elCount = 0; elCount < edgeElements.size(); elCount++) {
      AxisElement<?> currentEl = edgeElements.get(elCount);
      double displayLength = currentEl.getDisplayLength() - currentEl.getChildrenDisplayLength();
      for (int i = 0; i < displayLength; i++) {
        if (totalCount > 0) {
          double startGridX = startHorX;
          double startGridY = startHorY - totalCount * CELL_HEIGHT * contentScaleFactorY;
          double endGridX = endHorX;
          double endGridY = startGridY;
          boolean dotted = true;
          if (!landscapeDiagram.isContentScalesVertically() && i == 0) {
            dotted = false;
          }
          createLine(startGridX, startGridY, endGridX, endGridY, dotted);
        }
        totalCount++;
      }
    }
    // final line to frame the matrix
    double finalY = startHorY - totalCount * CELL_HEIGHT * contentScaleFactorY;
    createLine(startHorX, finalY, endHorX, finalY, false);
  }

  private void createLabels() throws MasterNotFoundException {

    double sideAxisMiddle = contentFrameBaseY + contentFrameHeight / 2;
    createLabel(landscapeDiagram.getSideAxis().getName(), true, contentFrameBaseX / 2, sideAxisMiddle);

    double topAxisMiddle = contentFrameBaseX + contentFrameWidth / 2;
    createLabel(landscapeDiagram.getTopAxis().getName(), false, topAxisMiddle, contentFrameBaseY + contentFrameHeight + 2 * TITLE_HEIGHT);
  }

  private void createContentElements() throws MasterNotFoundException {
    for (ContentElement<?> el : landscapeDiagram.getContent().getContentElements().values()) {
      createContent(el, landscapeDiagram.isContentScalesVertically());
    }
  }

  private void createSideAxisElements() throws MasterNotFoundException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("sideAxisOffsetX: " + contentFrameBaseX + " sideAxisOffsetY: " + contentFrameBaseY + contentFrameHeight
          + sideAxisToContentFrameOffsetY);
      LOGGER.debug("total side axis lenght: " + landscapeDiagram.getSideAxis().getTotalDisplayLength());
    }

    for (AxisElement<?> axisElement : landscapeDiagram.getSideAxis().getElements()) {
      createSideAxisShape(axisElement);
    }

  }

  private void createTopAxisElements() throws MasterNotFoundException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("topAxisOffsetX: " + contentFrameBaseX + topAxisToContentFrameOffsetX + " topAxisOffsetY: " + contentFrameBaseY
          + contentFrameHeight);
    }
    for (AxisElement<?> element : landscapeDiagram.getTopAxis().getElements()) {
      createTopAxisShape(element);
    }
  }

  private void createTopAxisShape(AxisElement<?> axisElement) throws MasterNotFoundException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Creating process shape: " + axisElement.toString());
    }

    double xPos = topAxisToContentFrameOffsetX + contentFrameBaseX + (axisElement.getDisplayStartPosition() * CELL_WIDTH) * contentScaleFactorX;
    double yPos = contentFrameBaseY + contentFrameHeight - (axisElement.getLevel() * TOP_AXIS_ELEMENT_HEIGHT) * topAxisScaleY;
    double width = axisElement.getDisplayLength() * CELL_WIDTH * contentScaleFactorX;
    double height = TOP_AXIS_ELEMENT_HEIGHT * topAxisScaleY;

    double widthInch = InchConverter.cmToInches(width);
    double heightInch = InchConverter.cmToInches(height);

    String master = getShapeMasterName(axisElement, true);
    Shape shape = this.getTargetPage().createNewShape(master);

    if (axisElement.getLevel() == landscapeDiagram.getTopAxis().getMaxLevel()) {
      //If the shape is in the last sublevel, use a different size and position.
      shape.setSize(widthInch, InchConverter.cmToInches(BOTTOM_LEVEL_AXIS_ELEMENT_HEIGHT * topAxisScaleY));
      shape.setPosition(InchConverter.cmToInches(xPos),
          InchConverter.cmToInches(yPos - (BOTTOM_LEVEL_AXIS_ELEMENT_HEIGHT - TOP_AXIS_ELEMENT_HEIGHT) * topAxisScaleY));
    }
    else {
      shape.setSize(widthInch, heightInch);
      shape.setPosition(InchConverter.cmToInches(xPos), InchConverter.cmToInches(yPos));
    }
    String name = getScreenName(axisElement, widthInch, TOP_AXIS_FONT_SIZE_PT);
    shape.setCharSize(InchConverter.ptToInches(TOP_AXIS_FONT_SIZE_PT, DEFAULT_SYSTEM_DPI));
    shape.setShapeText(name);

    // TODO Namen über Parameter steuern
    if ("Process".equals(master)) {

      // explicitly set geometry
      double offset = InchConverter.cmToInches(0.15) * contentScaleFactorX;
      shape.setFirstVertexOfGeometry(-offset, 0);
      shape.setLineEnd(2, (widthInch - offset), 0);
      shape.setLineEnd(3, (widthInch + offset), heightInch * 0.5);
      shape.setLineEnd(4, (widthInch - offset), heightInch);
      shape.setLineEnd(5, -offset, heightInch);
      shape.setLineEnd(6, offset, heightInch * 0.5);
      shape.setLineEnd(7, -offset, 0);
    }
    else if ("AxisElement".equals(master)) {
      manipulateMasterShape(widthInch, heightInch, shape);
    }

    if (!axisElement.isInResultSet()) {
      markShapeForElementNotInResultSet(shape);
    }
  }

  private void createSideAxisShape(AxisElement<?> elem) throws MasterNotFoundException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Creating side shape: " + elem.toString());
    }

    double xpos = contentFrameBaseX + (elem.getLevel() - 1) * SIDE_AXIS_ELEMENT_WIDTH * sideAxisScaleX;
    double ypos = contentFrameBaseY + contentFrameHeight - sideAxisToContentFrameOffsetY - (elem.getDisplayStartPosition() + elem.getDisplayLength())
        * CELL_HEIGHT * contentScaleFactorY;
    double width = SIDE_AXIS_ELEMENT_WIDTH * sideAxisScaleX;
    double height = elem.getDisplayLength() * CELL_HEIGHT * contentScaleFactorY;

    String master = getShapeMasterName(elem, false);

    Shape shape = this.getTargetPage().createNewShape(master);
    double widthInch = InchConverter.cmToInches(width);
    double heightInch = InchConverter.cmToInches(height);
    shape.setPosition(InchConverter.cmToInches(xpos), InchConverter.cmToInches(ypos));

    if (elem.getLevel() == landscapeDiagram.getSideAxis().getMaxLevel()) {
      //Use a different width for the shapes of the last sublevel.
      shape.setSize(InchConverter.cmToInches(BOTTOM_LEVEL_AXIS_ELEMENT_HEIGHT * sideAxisScaleX), heightInch);
    }
    else {
      shape.setSize(widthInch, heightInch);
    }

    // The following has to be added so that the graphic can be correctly displayed with VisioViewer
    shape.setFirstVertexOfGeometry(0, 0);
    shape.setLineEnd(2, heightInch, 0);
    shape.setLineEnd(3, heightInch, widthInch);
    shape.setLineEnd(4, 0, widthInch);
    shape.setLineEnd(5, 0, 0);

    String name = getScreenName(elem, heightInch, SIDE_AXIS_FONT_SIZE_PT);
    shape.setCharSize(InchConverter.ptToInches(SIDE_AXIS_FONT_SIZE_PT, DEFAULT_SYSTEM_DPI));
    shape.setShapeText(name);
    // turn text
    shape.setTextXForm(0, 0, heightInch, widthInch, 0, widthInch, 1.5707963267949);
    // Align the text to the mittle of the content shape (the 1 corresponds to a middle alignment)
    shape.setTextBlockVertAlign(1);

    if ("Process_vertical".equals(master)) {
      // explicitly set geometry

      double offset = InchConverter.cmToInches(0.15) * sideAxisScaleX;
      shape.setFirstVertexOfGeometry(0, heightInch + offset);
      shape.setLineEnd(2, 0, offset);
      shape.setLineEnd(3, widthInch * 0.5, -offset);
      shape.setLineEnd(4, widthInch, offset);
      shape.setLineEnd(5, widthInch, heightInch + offset);
      shape.setLineEnd(6, widthInch * 0.5, heightInch - offset);
      shape.setLineEnd(7, 0, heightInch + offset);
    }

    if (!elem.isInResultSet()) {
      markShapeForElementNotInResultSet(shape);
    }
  }

  /**
   * Extra create the shape again for compatibility with Visio Viewer.
   * 
   * @param widthInch
   * @param heightInch
   * @param shape
   */
  private void manipulateMasterShape(double widthInch, double heightInch, Shape shape) {
    // explicitly set geometry
    shape.setFirstVertexOfGeometry(0, 0);
    shape.setLineEnd(2, widthInch, 0);
    shape.setLineEnd(3, widthInch, heightInch);
    shape.setLineEnd(4, 0, heightInch);
    shape.setLineEnd(5, 0, 0);

    // transform textfield to match element's position and size.
    // the formulas are known from the visio template
    shape.setTextXForm(0, 0, widthInch, heightInch, 0, 0, 0);
  }

  private String getShapeMasterName(AxisElement<?> element, boolean forTopAxis) {

    String name = null;
    if (forTopAxis) {
      name = TOP_AXIS_SHAPE_NAMES.get(element.getElement().getClass());
    }
    else {
      name = SIDE_AXIS_SHAPE_NAMES.get(element.getElement().getClass());
    }
    if (name != null) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Shape for " + element.getElement().getClass() + " is: " + name);
      }
      return name;
    }
    else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Shape for " + element.getElement().getClass() + " is: " + DEFAULT_AXIS_SHAPE_NAME);
      }
      return DEFAULT_AXIS_SHAPE_NAME;
    }
  }

  private String getNonHierarchicalName(AxisElement<?> element) {
    String name;
    if (element.getElement() instanceof HierarchicalEntity<?>) {
      name = ((HierarchicalEntity<?>) element.getElement()).getNonHierarchicalName();
    }
    else {
      name = element.getElement().getIdentityString();
    }
    return name;
  }

  private void markShapeForElementNotInResultSet(Shape shape) {
    shape.setLinePattern(10);
  }

  @SuppressWarnings("unchecked")
  private void createContent(ContentElement<?> elem, boolean scalesVertically) throws MasterNotFoundException {

    double xpos = contentFrameBaseX + topAxisToContentFrameOffsetX + (CELL_WIDTH * elem.getTopAxisRef().getTailStart() + CONTENT_PADDING / 2)
        * contentScaleFactorX;
    double ypos = contentFrameBaseY + contentFrameHeight - sideAxisToContentFrameOffsetY
        - ((elem.getSideAxisRef().getTailStart() + 1) * CELL_HEIGHT - CONTENT_PADDING / 2) * contentScaleFactorY;

    double width = (CELL_WIDTH - CONTENT_PADDING);
    double height = (CELL_HEIGHT - CONTENT_PADDING);

    if (scalesVertically) {
      // adjust height based on merged cells
      height += (elem.getSize() - 1) * CELL_HEIGHT;

      // Adjust the y-coordinate as Visio draws with Cartesian coordinates and we need to "move" the
      // starting point to the lower left corner of the shape.
      ypos = ypos - (elem.getSize() - 1) * CELL_HEIGHT * contentScaleFactorY;

      if (landscapeDiagram.isScaleDownContentElements()) {
        // Case: scale down content elements to fit in a single cell
        width = width / elem.getTopAxisRef().getUniqueContentElementCount(elem);
        xpos = xpos + (elem.getInternalLevel() - 1) * width * contentScaleFactorX;
      }
      else {
        // Case: each content element has a cell of its own
        xpos = xpos + (elem.getInternalLevel() - 1) * (width + CONTENT_PADDING) * contentScaleFactorX;
      }
    }
    else {
      // adjust width based on merged cells
      width += (elem.getSize() - 1) * CELL_WIDTH;

      if (landscapeDiagram.isScaleDownContentElements()) {
        // Case: scale down the content elements to fit in a single cell
        height = height / elem.getSideAxisRef().getUniqueContentElementCount(elem);

        // Adjust the y-Coordinate with one cell height in the case of a single element in the cell.
        if (elem.getSideAxisRef().getUniqueContentElementCount(elem) == 1) {
          ypos = ypos - (elem.getInternalLevel() - 1) * height;
        }
        else {
          ypos = ypos + (elem.getSideAxisRef().getUniqueContentElementCount(elem) - elem.getInternalLevel()) * height * contentScaleFactorY;
        }
      }
      else {
        // Case: just a single content element per cell - just adjust the coordinate of the element
        ypos = ypos - (elem.getInternalLevel() - 1) * CELL_HEIGHT * contentScaleFactorY;
      }
    }

    double yPosInch = InchConverter.cmToInches(ypos);
    double xPosInch = InchConverter.cmToInches(xpos);
    double widthInch = InchConverter.cmToInches(width * contentScaleFactorX);
    double heightInch = InchConverter.cmToInches(height * contentScaleFactorY);

    createContentShape(elem, scalesVertically, xPosInch, yPosInch, widthInch, heightInch);
  }

  private void createContentShape(ContentElement<?> elem, boolean scalesVertically, double xPos, double yPos, double width, double height)
      throws MasterNotFoundException {

    Shape shape = this.getTargetPage().createNewShape(CONTENT_SHAPE_NAME);

    shape.setPosition(xPos, yPos);
    shape.setSize(width, height);

    // Adjust text size respecting the height and width of the content shape
    double charSize = 10;
    while (InchConverter.ptToInches(charSize, DEFAULT_SYSTEM_DPI) >= 0.7 * height) {
      charSize -= 0.1;
    }

    // Set the text
    shape.setShapeText(elem.getName());

    // explicitly set geometry (visio viewer compatibility)
    shape.setFirstVertexOfGeometry(0, 0);
    shape.setLineEnd(2, width, 0);
    shape.setLineEnd(3, width, height);
    shape.setLineEnd(4, 0, height);
    shape.setLineEnd(5, 0, 0);

    configureShape(shape, elem.getElement());

    if (scalesVertically) {

      // turn text
      shape.setTextXForm(0, 0, height, width, 0, width, 1.5707963267949);

      // Align the text to the top of the content shape (the 0 corresponds to a top alignment)
      shape.setTextBlockVertAlign(0);

      // Adjust the text size respecting the width of the content shape
      double charSizeY = 10;
      while (InchConverter.ptToInches(charSizeY * elem.getName().trim().length() / 1.75, DEFAULT_SYSTEM_DPI) >= height) {
        charSizeY -= 0.1;
      }
      shape.setCharSize(InchConverter.ptToInches(Math.min(charSize, charSizeY), DEFAULT_SYSTEM_DPI));
    }
    else {
      // Fit the text into a single line
      double charSizeY = 10;
      while (InchConverter.ptToInches(charSizeY * elem.getName().trim().length() / 1.75, DEFAULT_SYSTEM_DPI) >= width) {
        charSizeY -= 0.1;
      }
      shape.setCharSize(InchConverter.ptToInches(Math.min(charSize, charSizeY), DEFAULT_SYSTEM_DPI));
    }
  }

  private void configureShape(Shape shape, Object data) {
    for (ShapeConfigurator<?> configurator : shapeConfigurators) {
      configurator.configureShape(shape, (BuildingBlock) data);
    }
  }

  private void createLabel(String text, boolean vertical, double offsetX, double offsetY) throws MasterNotFoundException {

    // position shape
    Shape shape = this.getTargetPage().createNewShape(LABEL_SHAPE_NAME);
    shape.setPosition((InchConverter.cmToInches(offsetX)), (InchConverter.cmToInches(offsetY)));

    // set shape's text
    shape.setCharSize(InchConverter.ptToInches(10, DEFAULT_SYSTEM_DPI));
    shape.setFieldValue(text);

    if (vertical) {
      shape.setAngle(VERTICAL_ANGLE);
    }
  }

  private void createLine(double beginX, double beginY, double endX, double endY, boolean dotted) throws MasterNotFoundException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Creating line: xs: " + beginX + " ys: " + beginY + " xe: " + endX + " ye:" + endY + " dotted: " + dotted);
    }

    double myBeginX = InchConverter.cmToInches(beginX);
    double myBeginY = InchConverter.cmToInches(beginY);
    double myEndX = InchConverter.cmToInches(endX);
    double myEndY = InchConverter.cmToInches(endY);

    // position shape
    Shape shape = this.getTargetPage().createNewShape(LINE_SHAPE_NAME);
    shape.setBeginPosition(myBeginX, myBeginY);
    shape.setEndPosition(myEndX, myEndY);

    // set shape's geometry
    // note: the index parameter is known from the visio template.
    shape.setFirstVertexOfGeometry(myBeginX, myBeginY);
    shape.setLineEnd(2, myEndX, myEndY);

    if (dotted) {
      shape.setLinePattern(LINE_PATTERN_ID_DOTTED);
      shape.setLineWeight(LINE_WEIGHT_DOTTED);
    }
  }

  private void initPage() {

    double dinA1PaperRatio = 1.4145;

    double sideAxisWidth = landscapeDiagram.getSideAxis().getTotalDisplayLength() * CELL_HEIGHT;
    double topAxisWidth = landscapeDiagram.getTopAxis().getTotalDisplayLength() * CELL_WIDTH;

    double contentRatio = topAxisWidth / sideAxisWidth;

    if (contentRatio <= dinA1PaperRatio) {
      // Portrait
      getTargetPage().setSize(DIN_A1_PAGE_HEIGHT_INCH, DIN_A1_PAGE_WIDTH_INCH);
    }
    else {
      // Landscape
      getTargetPage().setSize(DIN_A1_PAGE_WIDTH_INCH, DIN_A1_PAGE_HEIGHT_INCH);
    }
  }

  /**
   * Calculate the size of the frame that contains the graphic (axes and content elements). This
   * is done by estimating the size of the legend and all margins and adjusting, so that we fit
   * into a DIN A1 page. Alternately, if scaling has been disabled, this method calculates the 
   * size of the page, so that the entire graphic can be fitted.
   */
  private void initContentFrame(double titleHeight) {
    double legendHeight = estimateLegendHeight() * LEGEND_SHAPE_HEIGHT;

    contentFrameBaseX = MARGIN;
    contentFrameBaseY = 2 * MARGIN + DISTANCE_TO_MARGIN_INCHES * 1.3 + legendHeight;

    double pageHeightCm = InchConverter.inchesToCm(getTargetPage().getHeight());
    double pageWidthCm = InchConverter.inchesToCm(getTargetPage().getWidth());

    contentFrameHeight = pageHeightCm - legendHeight - 5 * MARGIN - titleHeight;
    contentFrameWidth = pageWidthCm - 2 * MARGIN;
    initScaleFactorsAndOffsets(pageWidthCm, pageHeightCm);
  }

  private void initScaleFactorsAndOffsets(double pageWidthCm, double pageHeightCm) {

    double sideAxisHeight = (landscapeDiagram.getSideAxis().getMaxLevel() - 1) * SIDE_AXIS_ELEMENT_WIDTH + BOTTOM_LEVEL_AXIS_ELEMENT_HEIGHT;
    double sideAxisWidth = landscapeDiagram.getSideAxis().getTotalDisplayLength() * CELL_HEIGHT;
    double topAxisHeight = (landscapeDiagram.getTopAxis().getMaxLevel() - 1) * TOP_AXIS_ELEMENT_HEIGHT + BOTTOM_LEVEL_AXIS_ELEMENT_HEIGHT;
    double topAxisWidth = landscapeDiagram.getTopAxis().getTotalDisplayLength() * CELL_WIDTH;

    double contentAbsoluteWidth = topAxisWidth + sideAxisHeight + AXIS_ELEMENT_PADDING;
    double contentAbsoluteHeight = topAxisHeight + sideAxisWidth + AXIS_ELEMENT_PADDING;

    double pageWidthNew = pageWidthCm;
    double pageHeightNew = pageHeightCm;

    //handle width
    if ((contentAbsoluteWidth < contentFrameWidth) || !landscapeOptions.isGlobalScalingEnabled()) {
      contentFrameWidth = contentAbsoluteWidth;
      pageWidthNew = contentFrameWidth + 2 * MARGIN;
    }

    //handle height
    if (contentAbsoluteHeight < contentFrameHeight || !landscapeOptions.isGlobalScalingEnabled()) {
      double heightDifference = contentFrameHeight - contentAbsoluteHeight;
      pageHeightNew = pageHeightNew - heightDifference;
      contentFrameHeight = contentAbsoluteHeight;
    }

    getTargetPage().setSize(InchConverter.cmToInches(pageWidthNew), InchConverter.cmToInches(pageHeightNew));

    if (landscapeOptions.isGlobalScalingEnabled()) {
      contentScaleFactorX = Math.min(MAX_SCALE_FACTOR, contentFrameWidth / contentAbsoluteWidth);
      contentScaleFactorY = Math.min(MAX_SCALE_FACTOR, contentFrameHeight / contentAbsoluteHeight);
    }

    /*
     * It can happen in the case of excessively many content elements (mostly when the downscale of
     * the content elements is off), that the axis elements become too small. Therefore we
     * distinguish between two scale factors - one for the axis elements and one for the content
     * elements. If the axis elements don't get too small, we use the same scale for both.
     * Otherwise, axis and content scales are determined separately to establish a best fit.
     */
    if (contentScaleFactorY < MIN_AXIS_DOWNSCALE_FACTOR) {
      topAxisScaleY = MIN_AXIS_DOWNSCALE_FACTOR;
      contentScaleFactorY = (contentFrameHeight - topAxisHeight * MIN_AXIS_DOWNSCALE_FACTOR) / (sideAxisWidth + AXIS_ELEMENT_PADDING);
    }
    else {
      topAxisScaleY = contentScaleFactorY;
    }

    if (contentScaleFactorX < MIN_AXIS_DOWNSCALE_FACTOR) {
      sideAxisScaleX = MIN_AXIS_DOWNSCALE_FACTOR;
      contentScaleFactorX = Math.min(MAX_SCALE_FACTOR,
          ((contentFrameWidth - sideAxisHeight * MIN_AXIS_DOWNSCALE_FACTOR) / (topAxisWidth + AXIS_ELEMENT_PADDING)));
    }
    else {
      sideAxisScaleX = contentScaleFactorX;
    }

    topAxisToContentFrameOffsetX = (sideAxisHeight + AXIS_ELEMENT_PADDING) * sideAxisScaleX;

    sideAxisToContentFrameOffsetY = (topAxisHeight + AXIS_ELEMENT_PADDING) * topAxisScaleY;

  }

  private String getScreenName(AxisElement<?> axisElement, double fieldWidth, double textSizePt) {

    String nhName = getNonHierarchicalName(axisElement);
    if (!landscapeDiagram.isUseNamesLegend()) {
      return nhName;
    }
    return getVisioNamesLegend().addLegendEntry(nhName, null, "", fieldWidth, textSizePt, "");
  }

  private int estimateLegendHeight() {
    int legendHeight = 0;

    for (ShapeConfigurator<?> sc : shapeConfigurators) {
      int tmpHeight = 0;
      tmpHeight = sc.getValues().size();
      if (sc.hasUnspecificValue()) {
        tmpHeight++;
      }

      if (tmpHeight > 8) {
        legendsToMove.add(sc);
      }
      else {
        legendHeight = Math.max(legendHeight, tmpHeight);
      }
    }

    if (legendHeight > 0) {
      return legendHeight + 1;
    }
    else {
      return legendHeight;
    }
  }
}
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

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.LineDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.informationflow.InformationFlowGeneralHelper;
import de.iteratec.iteraplan.businesslogic.exchange.common.informationflow.InformationFlowGraphConverter;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.INamesLegend.LegendMode;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.layoutgraph.DockingPoint;
import de.iteratec.layoutgraph.LayoutEdge;
import de.iteratec.layoutgraph.LayoutEdge.Direction;
import de.iteratec.layoutgraph.LayoutEdgeCoordinate;
import de.iteratec.layoutgraph.LayoutGraph;
import de.iteratec.layoutgraph.LayoutNode;
import de.iteratec.layoutgraph.LayoutNode.NodeSide;
import de.iteratec.layoutgraph.layout.AbstractNodeLayout;
import de.iteratec.layoutgraph.layout.CircleNodeLayout;
import de.iteratec.layoutgraph.layout.DirectEdgeLayout;
import de.iteratec.layoutgraph.layout.KKNodeLayout;
import de.iteratec.layoutgraph.layout.OrthogonalEdgeLayout;
import de.iteratec.layoutgraph.layout.StandardSpringForceLayout;
import de.iteratec.svg.model.BasicShape;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.Shape;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.model.impl.AdvancedTextHelper;
import de.iteratec.svg.model.impl.PathShape;
import de.iteratec.svg.model.impl.RectangleShape;
import de.iteratec.svg.model.impl.TextShape;
import de.iteratec.svg.styling.SvgBaseStyling;


/**
 * This class contains the svg-related logic of the iteraplan information flow export.
 */
public class SvgInformationFlowExport extends SvgExport {

  private static final String              IS_CATEGORY_NAME                    = MessageAccess
                                                                                   .getStringOrNull(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL);
  private static final String              BO_CATEGORY_NAME                    = MessageAccess.getStringOrNull(Constants.BB_BUSINESSOBJECT_PLURAL);

  private static final String              SVG_TEMPLATE_FILE                   = "/SvgInformationFlowTemplate.svg";

  private final LayoutGraph                layoutGraph;
  private final InformationFlowOptionsBean informationFlowOptions;

  private Dimension                        nodeLayoutSize;
  private LineDimension                    lineDimension;

  private final Map<Integer, Shape>        representedIdToShapeMap;

  // Masters
  private static final String              SVG_SHAPE_NAME_APPLICATION          = "applicationRoot";
  private static final String              SVG_SHAPE_NAME_EDGE_ARROW           = "arrowSmallRoot";

  private static final String              NODE_CSS_BUSINESS_OBJECTS           = "applicationBusinessObjects";
  private static final String              NODE_CSS_BASE_COMPONENTS            = "applicationBaseComponents";
  private static final String              NODE_CSS_OBJECTS                    = "applicationObjectsBase";
  private static final String              NODE_CSS_OUTLINE                    = "applicationOutline";
  private static final String              NODE_CSS_TITLE_TEXT                 = "applicationTitleText";

  private static final String              EDGE_CSS_BASE_STYLE                 = "edgeBaseStyle";
  private static final String              EDGE_LABEL_TEXT_CSS                 = "edgeTextStyle";
  private static final String              EDGE_LABEL_BACKGROUND_STYLE         = "edgeTextBackgroundStyle";

  private static final double              LEGEND_BOX_WIDTH                    = 340;
  private static final double              CONTENT_MARGIN                      = 100;

  private static final double              DESCR_LEGEND_BOX_HEIGHT             = 45;
  private static final double              DESCR_LEGEND_BOX_ID_WIDTH           = 180;
  private static final double              DESCR_LEGEND_CONTENT_WIDTH          = 320;

  private static final double              MAX_INNER_BOX_HEIGHT                = 30;
  private static final double              MAX_INNER_BOX_HEIGHT_TO_WIDTH_RATIO = 0.3;
  private static final double              INNER_BOX_MARGIN                    = 2.25;
  private static final double              MIN_NODE_HEIGHT                     = 40;

  private static final double              PADDING_TOP                         = 10;
  private static final double              PADDING_BOTTOM                      = 10;
  private static final double              PADDING_LEFT                        = 10;
  private static final double              PADDING_RIGHT                       = 10;
  private static final double              PADDING_MIDDLE                      = 12;

  private static final double              APPLICATION_TITLE_TEXT_SIZE         = 6;

  private static final double              IS_TITLE_HEIGHT                     = 10;

  private static final int                 EDGE_LAYER                          = 4;
  private static final int                 NODE_LAYER                          = 6;
  private static final int                 EDGE_TEXT_INTERNAL_LAYER            = 6;
  private static final int                 EDGE_TEXT_BACKGROUND_INTERNAL_LAYER = 4;

  private static final int                 POSITION_GRID_GRANULARITY           = 10;

  private static final int                 STEP_SIZE                           = 10;

  private static final double              APPLICATION_MASTER_WIDTH            = 80;
  private static final double              APPLICATION_MASTER_HEIGHT           = 25;

  private static final Logger              LOGGER                              = Logger.getIteraplanLogger(SvgInformationFlowExport.class);

  public SvgInformationFlowExport(Locale locale, LayoutGraph layoutGraph, InformationFlowOptionsBean flowOptions,
      AttributeTypeService attributeTypeService, AttributeValueService attributeValueService) {

    super(attributeTypeService, attributeValueService);
    setLocale(locale);
    this.layoutGraph = layoutGraph;
    this.informationFlowOptions = flowOptions;
    this.representedIdToShapeMap = new HashMap<Integer, Shape>();

    loadSvgDocumentFromTemplate(SVG_TEMPLATE_FILE, "InformationFlow");
  }

  @Override
  public Document createDiagram() {
    try {

      setColorDimension(createColorDimension(informationFlowOptions.getColorOptionsBean(), TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
      generateCssColorStyles(getColorDimension());

      lineDimension = createLineDimension(informationFlowOptions.getLineOptionsBean(), TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
      generateCssLineStyles(lineDimension);

      if (informationFlowOptions.isUseNamesLegend()) {
        setSvgNamesLegend(new SvgNamesLegend(getSvgDocument()));
        if (!informationFlowOptions.isLegend()) {
          getSvgNamesLegend().setLegendMode(LegendMode.NEW_PAGE);
        }
      }

      createNodeShapes();
      applyNodeLayout();
      createEdges();

      estimatePageSize();

      double titleHeight = 0;
      if (!informationFlowOptions.isNakedExport()) {
        createLogos(0, 0, getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
        titleHeight = createDiagramTitle(MessageAccess.getStringOrNull("graphicalExport.informationflow.title", getLocale()), 0, 0, MARGIN);
        createGeneratedInformation(getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
      }

      if (informationFlowOptions.isShowSavedQueryInfo()) {
        Coordinates pos = new Coordinates(MARGIN, MARGIN_TOP + titleHeight);
        double width = nodeLayoutSize.getWidth() + 2 * CONTENT_MARGIN;
        createSavedQueryInfo(pos, width, informationFlowOptions.getSavedQueryInfo(), informationFlowOptions.getServerUrl());
      }

      createLegends();

      setCustomSize(informationFlowOptions.getWidth(), informationFlowOptions.getHeight());

      getSvgDocument().finalizeDocument();
    } catch (SvgExportException ex) {
      LOGGER.error("SvgExportException while trying to create a SvgInformationFlowExport-Diagram:\n" + ex.getMessage());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    }
    return getSvgDocument();
  }

  private void createLegends() throws SvgExportException {

    int colorLegendEntryCount = 0;
    int lineLegendEntryCount = 0;

    if (informationFlowOptions.isLegend()) {
      createDescriptionLegend();
      colorLegendEntryCount = createColorLegend();
      lineLegendEntryCount = createLineLegend(colorLegendEntryCount);
    }

    double frameX = getSvgDocument().getPageWidth() - 2 * MARGIN;
    if (informationFlowOptions.isUseNamesLegend()) {
      frameX = frameX - getSvgNamesLegend().getLegendWidth();
    }
    double frameY = 4 * MARGIN + 4 * DESCR_LEGEND_BOX_HEIGHT;

    if (colorLegendEntryCount > 0) {
      frameY = frameY + colorLegendEntryCount * LEGEND_BOX_HEIGHT + MARGIN;
    }

    if (lineLegendEntryCount > 0) {
      frameY = frameY + (lineLegendEntryCount + 1) * LEGEND_BOX_HEIGHT + MARGIN;
    }

    final double frameWidth = DESCR_LEGEND_BOX_ID_WIDTH + DESCR_LEGEND_CONTENT_WIDTH;
    final double frameHeight = getSvgDocument().getPageHeight() - 2 * MARGIN - frameY;

    if (informationFlowOptions.isUseNamesLegend()) {
      createNamesLegend(frameX, frameY, frameWidth, frameHeight, informationFlowOptions.isNakedExport(),
          MessageAccess.getStringOrNull("graphicalExport.informationflow.title", getLocale()));
    }

  }

  private void createDescriptionLegend() throws SvgExportException {

    final double nameLegendColumnBaseX = getSvgDocument().getPageWidth() - 2 * MARGIN - DESCR_LEGEND_BOX_ID_WIDTH - DESCR_LEGEND_CONTENT_WIDTH;
    final double nameLegendColumnBaseY = 3 * MARGIN;

    Coordinates boxSize = new Coordinates(DESCR_LEGEND_BOX_ID_WIDTH, DESCR_LEGEND_BOX_HEIGHT);
    Coordinates position = new Coordinates(nameLegendColumnBaseX, nameLegendColumnBaseY);
    Coordinates textShift = new Coordinates(10, DESCR_LEGEND_BOX_HEIGHT * 0.2);
    String headerString = MessageAccess.getStringOrNull("graphicalReport.headline", getLocale());

    createBaseLegend(BasicShape.SVG_BASIC_SHAPES.RECTANGLE, boxSize, position, createNameLegendHeaders(), true, headerString, textShift, null, null);

    boxSize = new Coordinates(DESCR_LEGEND_CONTENT_WIDTH, DESCR_LEGEND_BOX_HEIGHT);
    position = new Coordinates(nameLegendColumnBaseX + DESCR_LEGEND_BOX_ID_WIDTH, nameLegendColumnBaseY);
    headerString = InformationFlowGeneralHelper.headerAttributeOrBuilding(getLocale());

    createBaseLegend(BasicShape.SVG_BASIC_SHAPES.RECTANGLE, boxSize, position, createNameLegendContent(), true, headerString, textShift, null, null);

  }

  private List<String> createNameLegendHeaders() {
    final List<String> nameLegendHeaders = new ArrayList<String>();
    nameLegendHeaders.add(MessageAccess.getStringOrNull("reports.color", getLocale()));
    nameLegendHeaders.add(MessageAccess.getStringOrNull("reports.lineType", getLocale()));
    nameLegendHeaders.add(MessageAccess.getStringOrNull("reports.lineCaption", getLocale()));
    return nameLegendHeaders;
  }

  private List<String> createNameLegendContent() {
    final List<String> nameLegendContent = new ArrayList<String>();
    // add chosen color attribute name, if available
    nameLegendContent.add(InformationFlowGeneralHelper.replaceBlank(getFieldValueFromDimension(getColorDimension())));
    // add chosen line attribute name, if available
    nameLegendContent.add(InformationFlowGeneralHelper.replaceBlank(getFieldValueFromDimension(lineDimension)));

    // add name of building block / attribute chosen for the line description:

    final int[] lineCaptionSelected = informationFlowOptions.getSelectionType();

    nameLegendContent.add(InformationFlowGeneralHelper.getDescriptionTypeName(getAttributeTypeService(), lineCaptionSelected,
        informationFlowOptions.getLineCaptionSelectedAttributeId(), informationFlowOptions, getLocale()));

    return nameLegendContent;

  }

  /**
   * Applies a selected layout to the nodes of the graph. Note that the dimensions of the nodes must
   * be known at this point, as they play a role in the configuration of the layouting algorithm.
   */
  private void applyNodeLayout() {

    final String selectedLayout = informationFlowOptions.getSelectedNodeLayout();

    AbstractNodeLayout layout = null;

    if (selectedLayout.equals(Constants.REPORTS_EXPORT_INFORMATIONFLOW_LAYOUT_STANDARD)) {
      layout = new StandardSpringForceLayout(layoutGraph);
    }
    else if (selectedLayout.equals(Constants.REPORTS_EXPORT_INFORMATIONFLOW_LAYOUT_KK)) {
      layout = new KKNodeLayout(layoutGraph);
    }
    else if (selectedLayout.equals(Constants.REPORTS_EXPORT_INFORMATIONFLOW_LAYOUT_CIRCLE)) {
      layout = new CircleNodeLayout(layoutGraph);
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    layout.initialize();
    layout.doLayout();

    nodeLayoutSize = layoutGraph.getGraphDimension();

    // Apply node layout positions
    for (LayoutNode node : layoutGraph.getNodes()) {
      if (node.getParent() == null) {
        node.setX(Math.round(node.getX() / STEP_SIZE) * STEP_SIZE);
        node.setY(Math.round(node.getY() / STEP_SIZE) * STEP_SIZE);
        node.setX(node.getX() + CONTENT_MARGIN);
        node.setY(node.getY() + CONTENT_MARGIN);
        representedIdToShapeMap.get(node.getRepresentedId()).setPosition(node.getX(), node.getY());
        validateLayoutGraphInnerNodePositions(node);
      }
    }
  }

  private void createNodeShapes() throws SvgExportException {

    for (LayoutNode node : layoutGraph.getNodes()) {
      createNode(node, node.getParent());
    }
  }

  private Dimension2D createNode(LayoutNode node, Object parentShape) throws SvgExportException {

    final Shape shape = addNodeShape(parentShape);

    representedIdToShapeMap.put(node.getRepresentedId(), shape);

    // If the node is a top level node we link it to the building block.
    if (node.getParent() == null) {
      shape.setXLink(retrieveXLinkUrlForIdentityEntity(node.getNodeElement(), informationFlowOptions.getServerUrl()));
    }

    if (node.getChildren() != null && node.getChildren().size() > 0) {
      double maxWidth = 0;
      double maxHeight = 0;
      final List<Shape> children = new ArrayList<Shape>();

      // handle children
      for (LayoutNode child : node.getChildren()) {
        final Dimension2D size = createNode(child, shape);
        children.add(representedIdToShapeMap.get(child.getRepresentedId()));
        maxWidth = Math.max(maxWidth, size.getWidth());
        maxHeight = Math.max(maxHeight, size.getHeight());
      }

      // find the smallest n x n or n x (n-1) matrix that can hold all children
      final int arrayWidth = (int) Math.ceil(Math.sqrt(node.getChildren().size()));
      final int arrayHeight = (int) Math.ceil(node.getChildren().size() / (double) arrayWidth);

      setChildrenPositions(maxWidth, maxHeight, children, arrayWidth, arrayHeight);

      shape.setScaleInnerShapesWithShape(false);
      shape.setSize(PADDING_LEFT + arrayWidth * (maxWidth + PADDING_MIDDLE) - PADDING_MIDDLE + PADDING_RIGHT, PADDING_TOP + arrayHeight
          * (maxHeight + PADDING_MIDDLE) + PADDING_BOTTOM);
    }

    final double width = shape.getWidth();
    final double height = adjustShapeHeight(node, shape);

    // Apply the dimensions to the nodes of the graph as they might be needed in the layouting.
    node.setWidth(width);
    node.setHeight(height);

    manageShapeFormatting(node, shape);

    return new DoubleDimension(width, height);
  }

  private double adjustShapeHeight(LayoutNode node, Shape shape) {
    double newHeight = shape.getHeight();
    final double spaceForInnerBoxHeight = Math.min(MAX_INNER_BOX_HEIGHT + 2 * INNER_BOX_MARGIN, shape.getWidth()
        * MAX_INNER_BOX_HEIGHT_TO_WIDTH_RATIO);

    // Check for Business Objects and make space for them
    if (node.getCustomProperty(InformationFlowGraphConverter.APPLICATION_HAS_INFORMATION_OBJECTS).equals(
        InformationFlowGraphConverter.APPLICATION_HAS_INFORMATION_OBJECTS)) {
      newHeight += spaceForInnerBoxHeight;
    }
    // Check for uses-relations and make space for them
    if (node.getCustomProperty(InformationFlowGraphConverter.APPLICATION_HAS_BASE_COMPONENTS).equals(
        InformationFlowGraphConverter.APPLICATION_HAS_BASE_COMPONENTS)) {
      newHeight += spaceForInnerBoxHeight;
    }
    shape.setScaleInnerShapesWithShape(false);
    shape.setSize(shape.getWidth(), Math.max(MIN_NODE_HEIGHT, newHeight));
    return shape.getHeight();
  }

  private Shape addNodeShape(Object parentShape) throws SvgExportException {
    if (parentShape != null) {
      return ((Shape) parentShape).createNewInnerShape(SVG_SHAPE_NAME_APPLICATION);
    }
    else {
      return createNewShape(SVG_SHAPE_NAME_APPLICATION, NODE_LAYER);
    }
  }

  private void setChildrenPositions(double maxWidth, double maxHeight, List<Shape> children, int arrayWidth, int arrayHeight) {
    for (int j = 0; j < children.size(); j++) {
      final Shape childShape = children.get(j);
      final int col = j % arrayWidth;
      final int row = arrayHeight - 1 - (j / arrayWidth);

      // Determine positions for the shape
      int childShapeX = (int) Math.round(PADDING_LEFT + maxWidth / 2 + (maxWidth + PADDING_MIDDLE) * col - childShape.getWidth() / 2);
      int childShapeY = (int) Math.round(PADDING_TOP + PADDING_BOTTOM + maxHeight / 2 + (maxHeight + PADDING_MIDDLE) * row - childShape.getHeight()
          / 2);

      // Fix the position to be a multiple of POSITION_GRID_GRANULARITY
      int fixValue = childShapeX % POSITION_GRID_GRANULARITY;
      childShapeX = childShapeX - fixValue;
      fixValue = childShapeY % POSITION_GRID_GRANULARITY;
      childShapeY = childShapeY - fixValue;

      childShape.setPosition(childShapeX, childShapeY);
    }
  }

  private void manageShapeFormatting(LayoutNode node, Shape shape) throws SvgExportException {

    createShapeTitle(node, shape);

    // Manage base components
    boolean hasBaseComponents = false;
    if (node.getCustomProperty(InformationFlowGraphConverter.APPLICATION_HAS_BASE_COMPONENTS).equals(
        InformationFlowGraphConverter.APPLICATION_HAS_BASE_COMPONENTS)) {

      hasBaseComponents = true;
      final RectangleShape bcField = createField(shape, 0, false);
      bcField.addCSSClass(NODE_CSS_BASE_COMPONENTS);

      createText(node, shape, bcField, InformationFlowGraphConverter.APPLICATION_BASE_COMPONENTS, IS_CATEGORY_NAME);
    }

    // Manage business objects
    if (node.getCustomProperty(InformationFlowGraphConverter.APPLICATION_HAS_INFORMATION_OBJECTS).equals(
        InformationFlowGraphConverter.APPLICATION_HAS_INFORMATION_OBJECTS)) {

      final RectangleShape boField = createField(shape, 5, hasBaseComponents);
      boField.addCSSClass(NODE_CSS_BUSINESS_OBJECTS);

      createText(node, shape, boField, InformationFlowGraphConverter.APPLICATION_INFORMATION_OBJECTS, BO_CATEGORY_NAME);
    }

    manageColoringAndOutline(node, shape);
  }

  private void createShapeTitle(LayoutNode node, Shape shape) throws SvgExportException {
    // Manage texts
    final String applicationName = node.getCustomProperty(InformationFlowGraphConverter.APPLICATION_NAME);
    final String applicationVersion = node.getCustomProperty(InformationFlowGraphConverter.APPLICATION_VERSION);

    String shapeText = applicationName;
    if (applicationVersion != null && !applicationVersion.matches("//s*")) {
      shapeText = shapeText + Constants.VERSIONSEP + applicationVersion;
    }

    // Creating text shape
    final TextShape shapeTitle = (TextShape) shape.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.TEXT);
    shapeTitle.setPosition(shape.getWidth() / 2, IS_TITLE_HEIGHT);
    shapeTitle.addCSSClass(NODE_CSS_TITLE_TEXT);

    shapeTitle.setTextValue(getScreenName(informationFlowOptions, shapeText, null, IS_CATEGORY_NAME, shape.getWidth(), APPLICATION_TITLE_TEXT_SIZE,
        shape.getXLink()));
  }

  private void manageColoringAndOutline(LayoutNode node, Shape shape) throws SvgExportException {
    shape.setShapeCSSEnabled(false);

    final List<String> colorStrings = getColorStrings(getColorDimension().getMultipleValues(node.getNodeElement()));
    final String fillPatternId = createHorizontalLinesPattern(colorStrings, APPLICATION_MASTER_WIDTH, APPLICATION_MASTER_HEIGHT);

    shape.getDefaultStyle().setAttribute(SvgBaseStyling.FILL_COLOR, fillPatternId);
    shape.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_WITDH, "0");

    // Manage transparency
    if (node.getChildren() != null && node.getChildren().size() > 0) {
      shape.getDefaultStyle().setAttribute(SvgBaseStyling.FILL_OPACITY, "0.6");
    }

    // create outline
    final BasicShape outline = shape.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
    outline.setSize(shape.getWidth(), shape.getHeight());
    outline.addCSSClass(NODE_CSS_OUTLINE);
  }

  private RectangleShape createField(Shape parentShape, int cornerRounding, boolean isAboveAnotherField) throws SvgExportException {

    final RectangleShape field = (RectangleShape) parentShape.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
    final double innerBoxWidth = parentShape.getWidth() - 2 * INNER_BOX_MARGIN;

    field.setSize(innerBoxWidth, Math.min(MAX_INNER_BOX_HEIGHT, innerBoxWidth * MAX_INNER_BOX_HEIGHT_TO_WIDTH_RATIO));

    final double xPos = parentShape.getPinX() + (parentShape.getWidth() - field.getWidth()) / 2;
    final double yPos = parentShape.getHeight() - (INNER_BOX_MARGIN + field.getHeight()) * (isAboveAnotherField ? 2 : 1);

    field.setPosition(xPos, yPos);
    field.addCSSClass(NODE_CSS_OBJECTS);
    field.setCornerRounding(cornerRounding, cornerRounding);

    return field;
  }

  private void createText(LayoutNode node, Shape parentShape, RectangleShape textField, String textPropertyKey, String entryCategoryName)
      throws SvgExportException {
    final TextShape text = (TextShape) textField.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.TEXT);
    text.setPosition(textField.getWidth() / 2, textField.getHeight() / 2);
    text.addCSSClass(NODE_CSS_TITLE_TEXT);

    final String textStr = node.getCustomProperty(textPropertyKey);
    //TODO the business object text is sometimes too long, when the names legend is disabled...
    text.setTextValue(getScreenName(informationFlowOptions, textStr, null, entryCategoryName, textField.getWidth(), APPLICATION_TITLE_TEXT_SIZE,
        parentShape.getXLink()));
  }

  private void createEdges() throws SvgExportException {
    if (useDirectLayout()) {
      final DirectEdgeLayout edgeLayout = new DirectEdgeLayout(layoutGraph);
      edgeLayout.initialize();
      edgeLayout.doLayout();
    }
    else {
      final OrthogonalEdgeLayout edgeLayout = new OrthogonalEdgeLayout(layoutGraph);
      edgeLayout.initialize();
      edgeLayout.doLayout();
    }

    for (LayoutEdge edge : layoutGraph.getEdges()) {
      createEdge(edge);
    }

  }

  private void createEdge(LayoutEdge edge) throws SvgExportException {

    final List<LayoutEdgeCoordinate> pathCors = edge.getEdgePath();

    if (pathCors.size() > 0) {
      // Initialize the svg shape
      final PathShape edgeShape = (PathShape) createNewBasicShape(BasicShape.SVG_BASIC_SHAPES.PATH);
      edgeShape.setShapeLayer(EDGE_LAYER);

      // Line styling
      final Integer key = lineDimension.getValue(edge.getEdgeElement());
      edgeShape.addCSSClass(getLineTypeToCSSStyleMap().get(String.valueOf(key.intValue())));
      edgeShape.addCSSClass(EDGE_CSS_BASE_STYLE);

      // Create the path
      edgeShape.moveTo(pathCors.get(0).getX(), pathCors.get(0).getY());
      for (int i = 1; i < pathCors.size(); i++) {
        edgeShape.lineTo(pathCors.get(i).getX(), pathCors.get(i).getY());
      }

      // Add direction arrows if any
      createEdgeDirections(edge, edgeShape);

      // Add text
      addEdgeLabel(edge, edgeShape);
    }

  }

  private void createEdgeDirections(LayoutEdge edge, PathShape edgeShape) throws SvgExportException {

    if (edge.getEdgeDirection().equals(Direction.START_TO_END)) {
      // Draw an arrow at the end point
      final double angle = getRotationAngleForEdge(edge, false);
      addArrow(edgeShape, edge.getEdgePath().get(edge.getEdgePath().size() - 1).getX() - edgeShape.getPinX(),
          edge.getEdgePath().get(edge.getEdgePath().size() - 1).getY() - edgeShape.getPinY(), angle);
    }
    else if (edge.getEdgeDirection().equals(Direction.END_TO_START)) {
      // Draw an arrow at the start point
      final double angle = getRotationAngleForEdge(edge, true);
      addArrow(edgeShape, edge.getEdgePath().get(0).getX() - edgeShape.getPinX(), edge.getEdgePath().get(0).getY() - edgeShape.getPinY(), angle);
    }
    else if (edge.getEdgeDirection().equals(Direction.BIDIRECTIONAL)) {
      // Draw arrows at both ends
      final double startAngle = getRotationAngleForEdge(edge, true);
      final double endAngle = getRotationAngleForEdge(edge, false);
      addArrow(edgeShape, edge.getEdgePath().get(0).getX() - edgeShape.getPinX(), edge.getEdgePath().get(0).getY() - edgeShape.getPinY(), startAngle);
      addArrow(edgeShape, edge.getEdgePath().get(edge.getEdgePath().size() - 1).getX() - edgeShape.getPinX(),
          edge.getEdgePath().get(edge.getEdgePath().size() - 1).getY() - edgeShape.getPinY(), endAngle);
    }
  }

  private void addArrow(PathShape edgeShape, double xCor, double yCor, double angle) throws SvgExportException {

    final Shape arrow = edgeShape.createNewInnerShape(SVG_SHAPE_NAME_EDGE_ARROW, EDGE_LAYER);
    arrow.setPosition(xCor, yCor);
    arrow.setAngle(angle);
    arrow.setShapeCSSEnabled(false);

  }

  private void addEdgeLabel(LayoutEdge edge, PathShape edgeShape) throws SvgExportException {
    final TextShape edgeLabel = (TextShape) edgeShape.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.TEXT, EDGE_TEXT_INTERNAL_LAYER);
    edgeLabel.setTextValue(edge.getEdgeLabel());
    edgeLabel.addCSSClass(EDGE_LABEL_TEXT_CSS);

    final double[] edgeLabelPosition = findLabelPosition(edge);
    edgeLabel.setPosition(edgeLabelPosition[0], edgeLabelPosition[1] + 5);
    edgeLabel.setAngle(90 * edgeLabelPosition[2]);

    // Create background for the text
    final RectangleShape edgeLabelBackground = (RectangleShape) edgeShape.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE,
        EDGE_TEXT_BACKGROUND_INTERNAL_LAYER);
    edgeLabelBackground.addCSSClass(EDGE_LABEL_BACKGROUND_STYLE);
    edgeLabelBackground.setAngle(edgeLabel.getAngle());
    double labelWidth = edgeLabel.getTextValue().length() * 10 * AdvancedTextHelper.POINT_TO_UNIT_CONSTANT;
    edgeLabelBackground.setSize(labelWidth, 10);
    if (edgeLabelBackground.getAngle() % 180 == 0) {
      edgeLabelBackground.setPosition(edgeLabel.getPinX() - labelWidth / 2, edgeLabel.getPinY() - 10);
    }
    else {
      edgeLabelBackground.setPosition(edgeLabel.getPinX() + 10, edgeLabel.getPinY() - labelWidth / 2);
    }
  }

  private double[] findLabelPosition(LayoutEdge edge) {
    if (edge.getLinePoints().size() > 0) {
      final int middle = Math.round(edge.getLinePoints().size() / 2.0f);
      final LayoutEdgeCoordinate labelPosition = edge.getLinePoints().get(middle);
      final double[] output = new double[3];
      output[0] = labelPosition.getCoord(OrthogonalEdgeLayout.HORIZONTAL) - edge.getStartDock().getCoord(OrthogonalEdgeLayout.HORIZONTAL);
      output[1] = labelPosition.getCoord(OrthogonalEdgeLayout.VERTICAL) - edge.getStartDock().getCoord(OrthogonalEdgeLayout.VERTICAL);
      output[2] = edge.getLinePoints().get(middle).getX() - edge.getLinePoints().get(middle - 1).getX() == 0 ? OrthogonalEdgeLayout.VERTICAL
          : OrthogonalEdgeLayout.HORIZONTAL;
      return output;
    }
    else {
      return new double[] { 0, 0, 0 };
    }
  }

  /**
   * Retrieves the rotation angle for a given edge.
   * 
   * @param edge
   *          The edge
   * @param startPoint
   *          If true, the angle for the start point will be determined, otherwise for the end
   *          point.
   * @return The rotation angle in degrees: 0, 90, 180 or 270
   */
  private double getRotationAngleForEdge(LayoutEdge edge, boolean startPoint) {

    DockingPoint dock;

    if (startPoint) {
      dock = edge.getStartDock();
    }
    else {
      dock = edge.getEndDock();
    }

    if (dock == null) {
      return 0;
    }

    final NodeSide side = dock.getSide();

    if (side == null) {
      return 0;
    }

    if (side.equals(NodeSide.NORTH)) {
      return 90;
    }
    if (side.equals(NodeSide.EAST)) {
      return 180;
    }
    if (side.equals(NodeSide.SOUTH)) {
      return 270;
    }
    if (side.equals(NodeSide.WEST)) {
      return 0;
    }

    return 0;
  }

  private int createColorLegend() throws SvgExportException {

    final ColorDimensionOptionsBean colorOptions = informationFlowOptions.getColorOptionsBean();

    if (colorOptions.getDimensionAttributeId().intValue() != -1) {
      final Coordinates position = new Coordinates(getSvgDocument().getPageWidth() - 2 * MARGIN - LEGEND_BOX_WIDTH,
          4 * (MARGIN + DESCR_LEGEND_BOX_HEIGHT));
      final String header = getFieldValueFromDimension(getColorDimension());
      String headerUrl = null;
      if (colorOptions.getDimensionAttributeId().intValue() > 0) {
        final AttributeType type = getAttributeTypeService().loadObjectById(colorOptions.getDimensionAttributeId());
        headerUrl = retrieveXLinkUrlForIdentityEntity(type, informationFlowOptions.getServerUrl());
      }
      return createColorLegend(getColorDimension(), position, MASTER_COLOR_LEGEND_FIELD, header, headerUrl);
    }
    return 0;
  }

  private int createLineLegend(int numberOfColorLegendEntries) throws SvgExportException {

    final LineDimensionOptionsBean lineTypeOptions = informationFlowOptions.getLineOptionsBean();

    if (lineTypeOptions.getDimensionAttributeId().intValue() != -1) {
      final String header = getFieldValueFromDimension(lineDimension);
      final String headerUrl = getUrlForAttributeId(lineTypeOptions.getDimensionAttributeId(), informationFlowOptions.getServerUrl());
      final double lineLegendX = getSvgDocument().getPageWidth() - 2 * MARGIN - LEGEND_BOX_WIDTH;
      double lineLegendY = 4 * (DESCR_LEGEND_BOX_HEIGHT + MARGIN);
      if (numberOfColorLegendEntries > 0) {
        lineLegendY = lineLegendY + numberOfColorLegendEntries * LEGEND_BOX_HEIGHT + MARGIN;
      }
      return createLineLegend(lineDimension, header, headerUrl, lineLegendX, lineLegendY, LEGEND_BOX_WIDTH, LEGEND_BOX_HEIGHT);
    }
    return 0;
  }

  private void estimatePageSize() {

    double docWidth = nodeLayoutSize.getWidth() + 2 * MARGIN;
    double docHeight = docWidth / Math.sqrt(2);

    if (informationFlowOptions.isLegend()) {
      docWidth = docWidth + 2 * CONTENT_MARGIN + DESCR_LEGEND_BOX_ID_WIDTH + DESCR_LEGEND_CONTENT_WIDTH;
      docHeight = docWidth / Math.sqrt(2);

      int lineDimensionHeight = 0;
      if (lineDimension != null && !(informationFlowOptions.getLineOptionsBean().getDimensionAttributeId().intValue() == -1)) {
        lineDimensionHeight = lineDimension.getValues().size() + 1;
        if (lineDimension.hasUnspecificValue()) {
          lineDimensionHeight++;
        }
      }

      int colorDimensionHeight = 0;
      if (getColorDimension() != null && informationFlowOptions.getColorOptionsBean().getDimensionAttributeId().intValue() != -1) {
        colorDimensionHeight = getColorDimension().getValues().size() + 1;
        if (getColorDimension().hasUnspecificValue()) {
          colorDimensionHeight++;
        }
      }

      if (docHeight < 5 * MARGIN + (colorDimensionHeight + lineDimensionHeight + 1) * LEGEND_BOX_HEIGHT + 4 * DESCR_LEGEND_BOX_HEIGHT) {
        docHeight = 2 * CONTENT_MARGIN + (colorDimensionHeight + lineDimensionHeight + 1) * LEGEND_BOX_HEIGHT + 4 * DESCR_LEGEND_BOX_HEIGHT + MARGIN;
        docWidth = docHeight * Math.sqrt(2);
      }
    }

    // Additionally, check if the document is higher than the page.
    if (docHeight < nodeLayoutSize.getHeight() + 4 * MARGIN) {
      docHeight = nodeLayoutSize.getHeight() + 4 * MARGIN;
      docWidth = docHeight * Math.sqrt(2);
    }

    getSvgDocument().setPageSize(docWidth, docHeight);
  }

  private void validateLayoutGraphInnerNodePositions(LayoutNode node) {
    for (LayoutNode child : node.getChildren()) {
      child.setX(representedIdToShapeMap.get(child.getRepresentedId()).getPinX());
      child.setY(representedIdToShapeMap.get(child.getRepresentedId()).getPinY());
      validateLayoutGraphInnerNodePositions(child);
    }
  }

  /**
   * This method determines whether the {@link DirectEdgeLayout} or the {@link OrthogonalEdgeLayout} is to
   * be used for the current graphic generation. Currently, two aspects of the configuration are taken into
   * account when making the decision: <br><br>
   * 1. If there are intersecting nodes after the application of the node layout (i.e. nodes share a side or surface),
   * then the direct layout is used. <br><br>
   * 2. If for any top-level (root) node the number of edges accumulated over all sub-nodes and the node itself is
   * greater than the one specified through the <i>maximum.export.svg.interfaces.for.informationsystem</i> property,
   * the direct edge layout is used. Also, for all sub-level shapes a maximal number of edges is determined as one
   * third of the number specified in the properties (since for sub-level shapes only one shape side is used for
   * the docking of edges). Should the edge count of any sub-shape exceed this number, the direct layout is used.<br><br>
   * If none of the above cases occurs, the orthogonal edge layout is to be used. Note that the conditions do not
   * guarantee the success of the orthogonal edge layout, i.e. it might still fail, but this should happen in
   * very few cases.
   * 
   * @return
   *    <b>true</b> if a direct layout is to be used and
   *    <b>false</b> if an orthogonal edge layout is to be used.
   */
  private boolean useDirectLayout() {

    //Check for intersecting nodes (which also have edges)
    for (LayoutNode node1 : layoutGraph.getNodes()) {
      for (LayoutNode node2 : layoutGraph.getNodes()) {
        if (!node1.equals(node2) && getAccumulatedNumberOfEdges(node1) > 0 && getAccumulatedNumberOfEdges(node2) > 0
            && doNodesIntersect(node1, node2)) {
          return true;
        }
      }
    }

    //Check for nodes with too many edges
    double globalMaxCount = getRelativeCount(IteraplanProperties.getIntProperty("maximum.export.svg.interfaces.for.informationsystem"), true);
    for (LayoutNode node : layoutGraph.getNodes()) {
      if (nodeHasTooManyEdges(node, globalMaxCount)) {
        return true;
      }
    }

    return false;
  }

  /**
   * (Recursively) Checks whether the given node (or a sublevel node) has more edges
   * that allowed in the iteraplan properties.
   * @param node
   *    The node to be checked.
   * @param maxEdgeCount
   *    The maximal global edge count specified in the iteraplan properties.
   * @return true if the node, or a sublevel node have more than the allowed number of edges.
   */
  private static boolean nodeHasTooManyEdges(LayoutNode node, double maxEdgeCount) {
    boolean isRootNode = true;
    if (node.getParent() != null) {
      isRootNode = false;
    }

    for (LayoutNode child : node.getChildren()) {
      if (nodeHasTooManyEdges(child, maxEdgeCount)) {
        return true;
      }
    }

    if (getRelativeCount(getAccumulatedNumberOfEdges(node), isRootNode) > maxEdgeCount) {
      return true;
    }

    return false;
  }

  /**
   * Determines whether two nodes intersect, i.e. have common area or border.
   * @param node1
   * @param node2
   * @return true if the nodes intersect, false otherwise.
   */
  private static boolean doNodesIntersect(LayoutNode node1, LayoutNode node2) {
    double[][] node1Corners = new double[4][2];
    node1Corners[0][0] = node1.getX();
    node1Corners[0][1] = node1.getY();
    node1Corners[1][0] = node1.getX() + node1.getWidth();
    node1Corners[1][1] = node1Corners[0][1];
    node1Corners[2][0] = node1Corners[1][0];
    node1Corners[2][1] = node1.getY() + node1.getHeight();
    node1Corners[3][0] = node1Corners[0][0];
    node1Corners[3][1] = node1Corners[2][1];

    for (int i = 0; i < 4; i++) {
      if (((node1Corners[i][0] >= node2.getX() && node1Corners[i][0] <= node2.getX() + node2.getWidth()) && (node1Corners[i][1] >= node2.getY() && node1Corners[i][1] <= node2
          .getY() + node2.getHeight()))) {
        return true;
      }
    }

    return false;
  }

  /**
   * Estimates the accumulated number of edges over a given node and all of its sub-nodes.
   * @param node
   *    The node.
   * @return
   *    The number of edges of this node and all of its (direct and indirect) children.
   *    Note that edges going e.g. from one child to another child are counted once for
   *    each child and, thus, twice in the result.
   */
  private static int getAccumulatedNumberOfEdges(LayoutNode node) {
    int result = node.getEdges().size();
    for (LayoutNode child : node.getChildren()) {
      result = result + getAccumulatedNumberOfEdges(child);
    }
    return result;
  }

  /**
   * Determines a relative count for a given node depending on the actual
   * number of edges and whether the node is root node or a sublevel node.
   * @param edgeCount
   *    The actual number of edges of this node.
   * @param isRootNode
   *    Whether the node is a root or a sublevel node.
   * @return
   *    The determined relative count.
   */
  private static double getRelativeCount(int actualCount, boolean isRootNode) {
    double baseCount = actualCount;
    if (!isRootNode) {
      //Since non-root nodes only use one side to attach edges, their ratio increases three times.
      baseCount = baseCount * 3;
    }
    return baseCount;
  }

  /**
   * A small helper class to manage the sizes calculated for the nodes.
   */
  public static class DoubleDimension extends Dimension2D {
    private double width;
    private double height;

    public DoubleDimension(double width, double height) {
      super();
      this.width = width;
      this.height = height;
    }

    @Override
    public double getWidth() {
      return this.width;
    }

    @Override
    public double getHeight() {
      return this.height;
    }

    @Override
    public void setSize(double width, double height) {
      this.width = width;
      this.height = height;
    }
  }

}

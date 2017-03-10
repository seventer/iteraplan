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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.LineDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.AxisElement;
import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.ContentElement;
import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.LandscapeDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.INamesLegend.LegendMode;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ILandscapeOptions;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.svg.model.BasicShape;
import de.iteratec.svg.model.BasicShape.SVG_BASIC_SHAPES;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.Shape;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.model.impl.AbstractBasicShapeImpl;
import de.iteratec.svg.model.impl.AdvancedTextHelper;
import de.iteratec.svg.model.impl.PathShape;
import de.iteratec.svg.model.impl.RectangleShape;
import de.iteratec.svg.styling.SvgBaseStyling;


public class SvgLandscapeDiagramExport extends SvgExport {

  private final LandscapeDiagram  landscapeDiagram;
  private final ILandscapeOptions landscapeOptions;

  private LineDimension           lineDimension;

  private double                  legendBaseX;
  private double                  legendBaseY;

  private static final double     MAX_SCALE_FACTOR             = 1;
  private static final double     MAX_AXIS_DOWNSCALE_FACTOR    = 0.4;
  private double                  scaleFactorContentX          = 1;
  private double                  scaleFactorContentY          = 1;
  private double                  scaleFactorTopAxisY          = 1;
  private double                  scaleFactorSideAxisX         = 1;

  private double                  contentFrameWidth;
  private double                  contentFrameHeight;
  private double                  contentFrameBaseX;
  private double                  contentFrameBaseY;

  private double                  topAxisToContentFrameOffsetX;
  private double                  sideAxisToContentFrameOffsetY;

  // Name of the SVG template file to be used
  private static final String     SVG_TEMPLATE_FILE            = "/SVGProcessTemplate.svg";

  // Master shape names to be found in the template
  private static final String     PROCESS_HORIZONTAL           = "ProcessHorizontal-Root";
  private static final String     PROCESS_VERTICAL             = "ProcessVertical-Root";
  private static final String     CONTENT_ELEMENT              = "Content-Root";
  private static final String     AXIS_ELEMENT                 = "AxisElement-Root";

  // Page size
  private static final double     LANDSCAPE_DIN_A1_PAGE_WIDTH  = 3364;
  private static final double     LANDSCAPE_DIN_A1_PAGE_HEIGHT = 2376;

  // Margins and stuff...
  private static final double     LEFT_MARGIN                  = 2 * MARGIN;
  private static final double     AXIS_ELEMENT_PADDING         = 10;

  private static final double     CONTENT_PADDING              = 12;

  private static final double     TOP_AXIS_ELEMENT_HEIGHT      = 88;
  private static final double     SIDE_AXIS_ELEMENT_WIDTH      = 144;
  private static final double     CELL_HEIGHT                  = 204;
  private static final double     CELL_WIDTH                   = 540;

  private static final double     AXIS_TITLE_TO_AXIS_MARGIN    = 60;

  private static final double     LEGEND_BOX_WIDTH             = 260;

  private static final double     TOP_AXIS_TEXT_SIZE_PT        = 18;
  private static final double     SIDE_AXIS_TEXT_SIZE_PT       = 14;

  private static final String     GRID_LINE_SOLID              = "horizontalGridLineStyle";
  private static final String     GRID_LINE_DOTTED             = "verticalGridLineStyle";

  private boolean                 moveColorLegendToNewPage     = false;
  private boolean                 moveLineLegendToNewPage      = false;

  public SvgLandscapeDiagramExport(LandscapeDiagram landscapeDiagram, ILandscapeOptions landscapeOptions, AttributeTypeService attributeTypeService,
      AttributeValueService attributeValueService) {

    super(attributeTypeService, attributeValueService);
    this.landscapeDiagram = landscapeDiagram;
    this.landscapeOptions = landscapeOptions;

    loadSvgDocumentFromTemplate(SVG_TEMPLATE_FILE, "Landscape");
  }

  @Override
  public Document createDiagram() {

    if (landscapeDiagram.isUseNamesLegend()) {
      setSvgNamesLegend(new SvgNamesLegend(getSvgDocument()));
      getSvgNamesLegend().setLegendMode(LegendMode.NEW_PAGE);
    }

    try {

      TypeOfBuildingBlock bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(landscapeOptions.getSelectedBbType());

      setColorDimension(createColorDimension(landscapeOptions.getColorOptionsBean(), bbType));

      generateCssColorStyles(getColorDimension());

      lineDimension = createLineDimension(landscapeOptions.getLineOptionsBean(), bbType);
      generateCssLineStyles(lineDimension);

      initPage();

      double titleHeight = createTitleAndQueryInfo();

      initContentFrame(titleHeight);
      initScaleFactorsAndOffsets();

      if (!landscapeOptions.isNakedExport()) {
        createLogos(0, 0, getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
        createGeneratedInformation(getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
      }

      if (landscapeOptions.isLegend()) {
        generateLegends();
      }
      createContent();

      if (landscapeDiagram.isUseNamesLegend()) {
        super.createNamesLegend(0, 0, 0, 0, landscapeOptions.isNakedExport(), landscapeDiagram.getTitle());
      }

      setCustomSize(landscapeOptions.getWidth(), landscapeOptions.getHeight());

      if (!landscapeDiagram.isUseNamesLegend() || getSvgNamesLegend().isEmpty()) {
        Document svgDocument = getSvgDocument();
        // not only check if the legend has been moved to the right of the diagram but also check if there is a color legend
        if (moveColorLegendToNewPage && landscapeOptions.getColorOptionsBean().getDimensionAttributeId().intValue() != -1) {
          svgDocument.setPageSize(svgDocument.getPageWidth() + LEGEND_BOX_WIDTH + 3 * MARGIN, svgDocument.getPageHeight());
        }
        // not only check if the legend has been moved to the right of the diagram but also check if there is a line legend
        if (moveLineLegendToNewPage && landscapeOptions.getLineOptionsBean().getDimensionAttributeId().intValue() != -1) {
          svgDocument.setPageSize(svgDocument.getPageWidth() + LEGEND_BOX_WIDTH + 3 * MARGIN, svgDocument.getPageHeight());
        }
      }

      getSvgDocument().finalizeDocument();

    } catch (SvgExportException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (IteraplanTechnicalException ex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    }

    return getSvgDocument();

  }

  private double createTitleAndQueryInfo() throws SvgExportException {
    double titleHeight = 0;
    if (!landscapeOptions.isNakedExport()) {
      titleHeight = createDiagramTitle(landscapeDiagram.getTitle(), 0, 0, LEFT_MARGIN);
    }
    if (landscapeOptions.isShowSavedQueryInfo()) {
      Coordinates pos = new Coordinates(LEFT_MARGIN, MARGIN_TOP + titleHeight);
      double width = getSvgDocument().getPageWidth() - 4 * MARGIN;
      titleHeight = createSavedQueryInfo(pos, width, landscapeOptions.getSavedQueryInfo(), landscapeOptions.getServerUrl()) - MARGIN_TOP;
    }
    return titleHeight;
  }

  private void initPage() {

    double dinA1PaperRatio = 1.4145;

    double sideAxisWidth = landscapeDiagram.getSideAxis().getTotalDisplayLength() * CELL_HEIGHT;
    double topAxisWidth = landscapeDiagram.getTopAxis().getTotalDisplayLength() * CELL_WIDTH;

    double contentRatio = topAxisWidth / sideAxisWidth;

    if (contentRatio <= dinA1PaperRatio) {
      // Portrait
      getSvgDocument().setPageSize(LANDSCAPE_DIN_A1_PAGE_HEIGHT, LANDSCAPE_DIN_A1_PAGE_WIDTH);
    }
    else {
      // Landscape
      getSvgDocument().setPageSize(LANDSCAPE_DIN_A1_PAGE_WIDTH, LANDSCAPE_DIN_A1_PAGE_HEIGHT);
    }
  }

  /**
   * Calculate the size of the frame that contains the graphic (axes and content elements). This
   * is done by estimating the size of the legend and all margins and adjusting, so that we fit
   * into a DIN A1 page.
   */
  private void initContentFrame(double deltaY) {

    double colorLegendRowCount = getColorLegendRowCount();
    double lineLegendRowCount = getLineLegendRowCount();
    double legendRowCount = Math.max(colorLegendRowCount, lineLegendRowCount);

    moveLegendsToNewPage();

    if (moveColorLegendToNewPage) {
      legendRowCount = lineLegendRowCount;
    }
    if (moveLineLegendToNewPage) {
      legendRowCount = colorLegendRowCount;
    }
    if (moveColorLegendToNewPage && moveLineLegendToNewPage) {
      legendRowCount = 0;
    }

    double legendHeight = legendRowCount * LEGEND_BOX_HEIGHT;
    contentFrameHeight = getSvgDocument().getPageHeight() - legendHeight - 6 * MARGIN - deltaY;
    contentFrameWidth = getSvgDocument().getPageWidth() - 4 * MARGIN;
    contentFrameBaseX = LEFT_MARGIN;
    contentFrameBaseY = 3 * MARGIN + deltaY;

    legendBaseX = getSvgDocument().getPageWidth() - 2 * MARGIN - 2 * LEGEND_BOX_WIDTH;
    legendBaseY = contentFrameBaseY + contentFrameHeight + MARGIN;

  }

  private boolean moveLegendsToNewPage() {

    double lineVals = 0;
    double colorVals = 0;

    int colorAttributeId = landscapeOptions.getColorOptionsBean().getDimensionAttributeId().intValue();
    int lineAttributeId = landscapeOptions.getLineOptionsBean().getDimensionAttributeId().intValue();

    if (lineAttributeId != -1) {
      lineVals = createIntegerList(landscapeOptions.getLineOptionsBean().getSelectedLineTypes()).size();
    }
    if (colorAttributeId != -1) {
      colorVals = landscapeOptions.getColorOptionsBean().getSelectedColors().size();
    }

    if (lineVals > 8) {
      this.moveLineLegendToNewPage = true;
    }
    if (colorVals > 8) {
      this.moveColorLegendToNewPage = true;
    }
    if (lineVals > 8 && colorVals > 8) {
      return true;
    }
    return false;
  }

  private double getColorLegendRowCount() {
    double colorVals = getColorDimension().getValues().size();
    double legendHeight = colorVals + 2;

    // In case nothing was selected for both color and line legend:
    if (!landscapeOptions.isLegend()) {
      legendHeight = 0;
    }
    return legendHeight;
  }

  private double getLineLegendRowCount() {
    double lineVals = lineDimension.getValues().size();
    double legendHeight = lineVals + 2;

    // In case nothing was selected for both color and line legend:
    if (!landscapeOptions.isLegend()) {
      legendHeight = 0;
    }
    return legendHeight;
  }

  private void initScaleFactorsAndOffsets() {

    double sideAxisHeight = landscapeDiagram.getSideAxis().getMaxLevel() * SIDE_AXIS_ELEMENT_WIDTH;
    double sideAxisWidth = landscapeDiagram.getSideAxis().getTotalDisplayLength() * CELL_HEIGHT;
    double topAxisHeight = (landscapeDiagram.getTopAxis().getMaxLevel() + 1) * TOP_AXIS_ELEMENT_HEIGHT;
    double topAxisWidth = landscapeDiagram.getTopAxis().getTotalDisplayLength() * CELL_WIDTH;

    double contentAbsoluteWidth = topAxisWidth + sideAxisHeight + AXIS_ELEMENT_PADDING;
    double contentAbsoluteHeight = topAxisHeight + sideAxisWidth + AXIS_ELEMENT_PADDING;

    double pageWidthNew = getSvgDocument().getPageWidth();
    double pageHeightNew = getSvgDocument().getPageHeight();

    if (contentAbsoluteWidth < contentFrameWidth || !landscapeOptions.isGlobalScalingEnabled()) {
      contentFrameWidth = contentAbsoluteWidth;
      pageWidthNew = contentFrameWidth + 4 * MARGIN;
      legendBaseX = pageWidthNew - 2 * MARGIN - 2 * LEGEND_BOX_WIDTH;
    }
    if (contentAbsoluteHeight < contentFrameHeight || !landscapeOptions.isGlobalScalingEnabled()) {
      double heightDifference = contentFrameHeight - contentAbsoluteHeight;
      pageHeightNew = pageHeightNew - heightDifference;
      legendBaseY = legendBaseY - heightDifference;
      contentFrameHeight = contentAbsoluteHeight;
    }

    getSvgDocument().setPageSize(pageWidthNew, pageHeightNew);

    if (landscapeOptions.isGlobalScalingEnabled()) {
      scaleFactorContentX = Math.min(MAX_SCALE_FACTOR, contentFrameWidth / contentAbsoluteWidth);
      scaleFactorContentY = Math.min(MAX_SCALE_FACTOR, contentFrameHeight / contentAbsoluteHeight);
    }

    /*
     * It can happen in the case of excessively many content elements (mostly when the downscale of
     * the content elements is off), that the axis elements become too small. Therefore we
     * distinguish between two scale factors - one for the axis elements and one for the content
     * elements. If the axis elements don't get too small, we use the same scale for both.
     * Otherwise, axis and content scales are determined separately to establish a best fit.
     */
    if (scaleFactorContentY < MAX_AXIS_DOWNSCALE_FACTOR) {
      scaleFactorTopAxisY = MAX_AXIS_DOWNSCALE_FACTOR;
      scaleFactorContentY = (contentFrameHeight - topAxisHeight * MAX_AXIS_DOWNSCALE_FACTOR) / (sideAxisWidth + AXIS_ELEMENT_PADDING);
    }
    else {
      scaleFactorTopAxisY = scaleFactorContentY;
    }

    if (scaleFactorContentX < MAX_AXIS_DOWNSCALE_FACTOR) {
      scaleFactorSideAxisX = MAX_AXIS_DOWNSCALE_FACTOR;
      scaleFactorContentX = Math.min(MAX_SCALE_FACTOR,
          ((contentFrameWidth - sideAxisHeight * MAX_AXIS_DOWNSCALE_FACTOR) / (topAxisWidth + AXIS_ELEMENT_PADDING)));
    }
    else {
      scaleFactorSideAxisX = scaleFactorContentX;
    }

    topAxisToContentFrameOffsetX = (sideAxisHeight + AXIS_ELEMENT_PADDING) * scaleFactorSideAxisX;
    sideAxisToContentFrameOffsetY = (topAxisHeight + AXIS_ELEMENT_PADDING) * scaleFactorTopAxisY;

  }

  private void generateLegends() throws SvgExportException {

    // Create the color legend
    ColorDimensionOptionsBean colorOptions = landscapeOptions.getColorOptionsBean();
    if (colorOptions.getDimensionAttributeId().intValue() != -1) {
      Coordinates position = null;
      if (!moveColorLegendToNewPage) {
        position = new Coordinates(legendBaseX + LEGEND_BOX_WIDTH, legendBaseY);
      }
      else {
        position = new Coordinates(getSvgDocument().getPageWidth() + 2 * MARGIN, 6 * MARGIN);
        addNamesLegendOffset(LEGEND_BOX_WIDTH + 2 * MARGIN);
      }
      String header = getFieldValueFromDimension(getColorDimension());
      String headerUrl = getUrlForAttributeId(colorOptions.getDimensionAttributeId(), landscapeOptions.getServerUrl());
      createColorLegend(getColorDimension(), position, MASTER_COLOR_LEGEND_FIELD, header, headerUrl);
    }

    // Create the line legend
    LineDimensionOptionsBean lineTypeOptions = landscapeOptions.getLineOptionsBean();
    if (lineTypeOptions.getDimensionAttributeId().intValue() != -1) {
      String header = getFieldValueFromDimension(lineDimension);
      String headerUrl = getUrlForAttributeId(lineTypeOptions.getDimensionAttributeId(), landscapeOptions.getServerUrl());
      double legendX = legendBaseX;
      double legendY = legendBaseY;
      if (moveLineLegendToNewPage) {
        addNamesLegendOffset(LEGEND_BOX_WIDTH + 2 * MARGIN);
        legendX = getSvgDocument().getPageWidth() + 2 * MARGIN;
        legendY = 6 * MARGIN;
        if (moveColorLegendToNewPage) {
          legendX = legendX + LEGEND_BOX_WIDTH + MARGIN;
        }
      }
      createLineLegend(lineDimension, header, headerUrl, legendX, legendY, LEGEND_BOX_WIDTH, LEGEND_BOX_HEIGHT);
    }
  }

  private void addNamesLegendOffset(double offsetAdd) {
    if (landscapeDiagram.isUseNamesLegend()) {
      getSvgNamesLegend().addOffset(offsetAdd);
    }
  }

  private void createContent() throws SvgExportException {
    createTopAxis();
    createSideAxis();
    createAxisLabels();
    createLines();
    createContentElements();
  }

  private void createTopAxis() throws SvgExportException {

    for (AxisElement<?> axisElement : landscapeDiagram.getTopAxis().getElements()) {
      createTopAxisElement(axisElement);
    }
  }

  private void createTopAxisElement(AxisElement<?> axisElement) throws SvgExportException {
    String master = getShapeMasterName(axisElement, true);
    double xPos = topAxisToContentFrameOffsetX + axisElement.getDisplayStartPosition() * CELL_WIDTH * scaleFactorContentX + contentFrameBaseX;
    double yPos = (axisElement.getLevel() * TOP_AXIS_ELEMENT_HEIGHT) * scaleFactorTopAxisY + contentFrameBaseY - 3 * AXIS_ELEMENT_PADDING
        * scaleFactorTopAxisY;
    double width = axisElement.getDisplayLength() * CELL_WIDTH * scaleFactorContentX;
    double height = TOP_AXIS_ELEMENT_HEIGHT * scaleFactorTopAxisY;

    Shape element = createNewShape(master);
    element.setPosition(xPos, yPos);
    element.setSize(width, height);
    element.setTextFieldValue(estimateElementName(width, axisElement, true));
    element.addTextCSSClass("axisElementText");
    element.setResizeTextWithShape(false);

    element.setXLink(getAxisElementXLink(axisElement));

    if (master.equals(PROCESS_HORIZONTAL)) {
      PathShape process = createProcessForShape(element, xPos, yPos, width, height, false);
      process.addCSSClass("axisElementBaseStyle");

      // We re-adjust the text to put it in the middle of the shape by adding 16 units (this is
      // exactly as much as the arrow of a process stretches to the inside of the box)
      element.setTextPosition(xPos + width / 2 + 16, yPos + height * 0.6);
    }
    else {
      RectangleShape innerRect = (RectangleShape) element.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
      innerRect.setSize(element.getWidth(), element.getHeight());
      innerRect.setPosition(0, 0);
      innerRect.addCSSClass("axisElementBaseStyle");

      element.setTextPosition(xPos + width / 2, yPos + height * 0.6);
    }
  }

  private void createSideAxis() throws SvgExportException {

    for (AxisElement<?> axisElement : landscapeDiagram.getSideAxis().getElements()) {
      createSideAxisElement(axisElement);
    }
  }

  private void createSideAxisElement(AxisElement<?> element) throws SvgExportException {

    String master = getShapeMasterName(element, false);
    double xPos = ((element.getLevel() - 1) * SIDE_AXIS_ELEMENT_WIDTH) * scaleFactorSideAxisX + contentFrameBaseX;
    double yPos = contentFrameBaseY + sideAxisToContentFrameOffsetY + (element.getDisplayStartPosition() * CELL_HEIGHT) * scaleFactorContentY;
    double width = SIDE_AXIS_ELEMENT_WIDTH * scaleFactorSideAxisX;
    double height = element.getDisplayLength() * CELL_HEIGHT * scaleFactorContentY;

    Shape shape = createNewShape(master);
    shape.setPosition(xPos, yPos);
    shape.setSize(width, height);
    shape.setTextFieldValue(estimateElementName(height, element, true));
    shape.addTextCSSClass("sideAxisElementText");
    shape.setTextAngle(270);
    shape.setResizeTextWithShape(false);
    shape.setXLink(getAxisElementXLink(element));

    AbstractBasicShapeImpl formShape = null;

    if (master.equals(PROCESS_VERTICAL)) {
      formShape = createProcessForShape(shape, xPos, yPos, width, height, true);
      shape.setTextPosition(xPos + width * 0.6, yPos + height / 2 + 16);
    }
    else {
      formShape = (RectangleShape) shape.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
      formShape.setSize(shape.getWidth(), shape.getHeight());
      formShape.setPosition(0, 0);
      shape.setTextPosition(xPos + width * 0.6, yPos + height / 2);
    }

    formShape.addCSSClass("axisElementBaseStyle");
  }

  private void createAxisLabels() throws SvgExportException {
    // Top axis label
    Shape topAxisLabel = createNewShape(MASTER_TITLE);
    topAxisLabel.addTextCSSClass("axisLabelText");
    topAxisLabel.setPosition(contentFrameBaseX + topAxisToContentFrameOffsetX + (contentFrameWidth - topAxisToContentFrameOffsetX) / 2,
        contentFrameBaseY - AXIS_TITLE_TO_AXIS_MARGIN / 2);
    topAxisLabel.setTextFieldValue(landscapeDiagram.getTopAxis().getName());

    // Side axis label
    Shape sideAxisLabel = createNewShape(MASTER_TITLE);
    sideAxisLabel.addTextCSSClass("axisLabelText");
    sideAxisLabel.setPosition(contentFrameBaseX - AXIS_TITLE_TO_AXIS_MARGIN / 2, contentFrameBaseY + sideAxisToContentFrameOffsetY
        + (contentFrameHeight - sideAxisToContentFrameOffsetY) / 2);
    sideAxisLabel.setTextFieldValue(landscapeDiagram.getSideAxis().getName());
    sideAxisLabel.setAngle(270);
  }

  private void createLines() throws SvgExportException {
    createGridLineClasses();

    // The top line
    double startX = contentFrameBaseX + topAxisToContentFrameOffsetX;
    double startY = contentFrameBaseY + sideAxisToContentFrameOffsetY;
    double endX = contentFrameBaseX + topAxisToContentFrameOffsetX + landscapeDiagram.getTopAxis().getTotalDisplayLength() * CELL_WIDTH
        * scaleFactorContentX;
    createHorizontalLine(new Coordinates(startX, startY), endX - startX, GRID_LINE_SOLID);

    // The left side line
    endX = startX;
    double endY = contentFrameBaseY + sideAxisToContentFrameOffsetY + landscapeDiagram.getSideAxis().getTotalDisplayLength() * CELL_HEIGHT
        * scaleFactorContentY;
    createVerticalLine(new Coordinates(startX, startY), endY - startY, GRID_LINE_SOLID);

    // vertical grid lines
    createVerticalGridLines(startX, startY, endY);

    // Right framing line
    startX = contentFrameBaseX + topAxisToContentFrameOffsetX + landscapeDiagram.getTopAxis().getTotalDisplayLength() * CELL_WIDTH
        * scaleFactorContentX;
    endX = startX;
    startY = contentFrameBaseY + sideAxisToContentFrameOffsetY;
    endY = contentFrameBaseY + sideAxisToContentFrameOffsetY + landscapeDiagram.getSideAxis().getTotalDisplayLength() * CELL_HEIGHT
        * scaleFactorContentY;
    createVerticalLine(new Coordinates(startX, startY), endY - startY, GRID_LINE_SOLID);

    // Bottom framing line
    startX = contentFrameBaseX + topAxisToContentFrameOffsetX;
    startY = contentFrameBaseY + sideAxisToContentFrameOffsetY + landscapeDiagram.getSideAxis().getTotalDisplayLength() * CELL_HEIGHT
        * scaleFactorContentY;
    endX = contentFrameBaseX + topAxisToContentFrameOffsetX + landscapeDiagram.getTopAxis().getTotalDisplayLength() * CELL_WIDTH
        * scaleFactorContentX;
    createHorizontalLine(new Coordinates(startX, startY), endX - startX, GRID_LINE_SOLID);

    // Horizontal lines
    createHorizontalGridLines();

  }

  private void createVerticalGridLines(double startX, double startY, double endY) throws SvgExportException {

    int totalCount = 0;
    List<AxisElement<?>> edgeElements = landscapeDiagram.getTopAxis().getElements();

    for (int elCount = 0; elCount < edgeElements.size(); elCount++) {
      AxisElement<?> currentEl = edgeElements.get(elCount);
      double displayLength = currentEl.getDisplayLength() - currentEl.getChildrenDisplayLength();
      for (int i = 0; i < displayLength; i++) {
        if (totalCount > 0) {
          double startGridX = startX + totalCount * CELL_WIDTH * scaleFactorContentX;

          if (landscapeDiagram.isContentScalesVertically() && i == 0) {
            createVerticalLine(new Coordinates(startGridX, startY), endY - startY, GRID_LINE_SOLID);
          }
          else {
            createVerticalLine(new Coordinates(startGridX, startY), endY - startY, GRID_LINE_DOTTED);
          }
        }
        totalCount++;
      }
    }
  }

  private void createHorizontalGridLines() throws SvgExportException {
    double startX = contentFrameBaseX + topAxisToContentFrameOffsetX;
    double endX = contentFrameBaseX + topAxisToContentFrameOffsetX + landscapeDiagram.getTopAxis().getTotalDisplayLength() * CELL_WIDTH
        * scaleFactorContentX;
    double startY = contentFrameBaseY + sideAxisToContentFrameOffsetY;
    int totalCount = 0;
    List<AxisElement<?>> edgeElements = landscapeDiagram.getSideAxis().getElements();

    for (int elCount = 0; elCount < edgeElements.size(); elCount++) {
      AxisElement<?> currentEl = edgeElements.get(elCount);
      double displayLength = currentEl.getDisplayLength() - currentEl.getChildrenDisplayLength();
      for (int i = 0; i < displayLength; i++) {
        if (totalCount > 0) {
          double tempY = startY + totalCount * CELL_HEIGHT * scaleFactorContentY;
          if (landscapeDiagram.isContentScalesVertically() && i == 0) {
            createHorizontalLine(new Coordinates(startX, tempY), endX - startX, GRID_LINE_DOTTED);
          }
          else {
            createHorizontalLine(new Coordinates(startX, tempY), endX - startX, GRID_LINE_SOLID);
          }
        }
        totalCount++;
      }
    }
  }

  private void createContentElements() throws SvgExportException {

    double baseX = contentFrameBaseX + topAxisToContentFrameOffsetX;
    double baseY = contentFrameBaseY + sideAxisToContentFrameOffsetY;

    for (ContentElement<?> element : landscapeDiagram.getContent().getContentElements().values()) {
      createContentElement(element, landscapeDiagram.isContentScalesVertically(), baseX, baseY);
    }
  }

  @SuppressWarnings("unchecked")
  private void createContentElement(ContentElement<?> element, boolean vertical, double baseX, double baseY) throws SvgExportException {

    // Determine initial coordinates
    double xPos = baseX + (element.getTopAxisRef().getTailStart() * CELL_WIDTH + CONTENT_PADDING / 2) * scaleFactorContentX;
    double yPos = baseY + (element.getSideAxisRef().getTailStart() * CELL_HEIGHT + CONTENT_PADDING / 2) * scaleFactorContentY;

    double width = (CELL_WIDTH - CONTENT_PADDING);
    double height = (CELL_HEIGHT - CONTENT_PADDING);

    if (vertical) {
      height = height + (element.getSize() - 1) * CELL_HEIGHT;
      if (landscapeDiagram.isScaleDownContentElements()) {
        // Case: scale down elements to fit in a single cell
        width = width / element.getTopAxisRef().getUniqueContentElementCount(element);
        xPos = xPos + (element.getInternalLevel() - 1) * width * scaleFactorContentX;
      }
      else {
        // Case - each element is in a single cell
        xPos = xPos + (element.getInternalLevel() - 1) * (width + CONTENT_PADDING) * scaleFactorContentX;
      }
    }
    else {

      width = width + (element.getSize() - 1) * CELL_WIDTH;

      if (landscapeDiagram.isScaleDownContentElements()) {
        // Case: scale down elements to fit in a single cell
        height = height / element.getSideAxisRef().getUniqueContentElementCount(element);
        yPos = yPos + (element.getInternalLevel() - 1) * height * scaleFactorContentY;
      }
      else {
        // Case: single element per cell - just correct the coordinate
        yPos = yPos + (element.getInternalLevel() - 1) * (height + CONTENT_PADDING) * scaleFactorContentY;
      }
    }

    width = width * scaleFactorContentX;
    height = height * scaleFactorContentY;

    // Create the shape
    Shape contentShape = createNewShape(CONTENT_ELEMENT);
    contentShape.setSize(width, height);
    contentShape.setPosition(xPos, yPos);
    contentShape.setXLink(retrieveXLinkUrlForIdentityEntity(element.getElement(), landscapeOptions.getServerUrl()));

    // Contour:
    RectangleShape contentInner = createRectangle(contentShape, width, height);
    contentInner.addCSSClass("contentElementBaseStyle");

    Integer key = lineDimension.getValue(element.getElement());
    contentInner.addCSSClass(getLineTypeToCSSStyleMap().get(String.valueOf(key.intValue())));

    // Apply coloring
    applyContentElementColoring(contentInner, element, vertical);

    contentShape.setResizeTextWithShape(false);
    contentShape.setTextCSSEnabled(false);
    contentShape.setTextFieldValue(element.getName());
    contentShape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FILL_COLOR, "000000");
    contentShape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FILL_OPACITY, "1");
    contentShape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_FAMILY, "Arial");
    contentShape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_WEIGHT, "normal");
    contentShape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_ALIGN, "start");
    contentShape.addTextCSSClass("contentElementText");

    if (vertical) {
      contentShape.setTextAngle(270);
      double fontSize = estimateFontSize(contentShape.getHeight(), contentShape.getWidth(), contentShape.getTextFieldValue().length());
      contentShape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_SIZE, Double.toString(fontSize));
      contentShape.setTextPosition((xPos + (width + fontSize * AdvancedTextHelper.POINT_TO_UNIT_CONSTANT) * 0.5), yPos + height - CELL_HEIGHT
          * scaleFactorContentY * 0.1);
    }
    else {
      double fontSize = estimateFontSize(contentShape.getWidth(), contentShape.getHeight(), contentShape.getTextFieldValue().length());
      contentShape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_SIZE, Double.toString(fontSize));
      contentShape.setTextPosition(xPos + CELL_WIDTH * 0.1 * scaleFactorContentX, yPos
          + (height + fontSize * AdvancedTextHelper.POINT_TO_UNIT_CONSTANT) * 0.5);
    }
  }

  private void applyContentElementColoring(RectangleShape contentColorFrame, ContentElement<?> element, boolean vertical) throws SvgExportException {

    // Apply default color class
    contentColorFrame.addCSSClass(getColorToColorClassMap().get(getColorStr(getColorDimension().getValue(element.getElement()))));

    List<Color> colorsForElement = getColorDimension().getMultipleValues(element.getElement());
    if (colorsForElement.size() > 1) {
      contentColorFrame.addCSSClass("colorFieldTransparentStyle");
      if (vertical) {
        double innerFieldWidth = contentColorFrame.getWidth() / colorsForElement.size();
        for (int i = 0; i < colorsForElement.size(); i++) {
          RectangleShape colorField = (RectangleShape) contentColorFrame.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
          colorField.setSize(innerFieldWidth, contentColorFrame.getHeight());
          colorField.setPosition(i * innerFieldWidth, 0);
          colorField.addCSSClass("contentElementBaseStyle");
          colorField.addCSSClass(getColorToColorClassMap().get(getColorStr(colorsForElement.get(i))));
        }
      }
      else {
        double innerColorFieldHeight = contentColorFrame.getHeight() / colorsForElement.size();
        for (int i = 0; i < colorsForElement.size(); i++) {

          RectangleShape colorField = (RectangleShape) contentColorFrame.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
          colorField.setSize(contentColorFrame.getWidth(), innerColorFieldHeight);
          colorField.setPosition(0, i * innerColorFieldHeight);
          colorField.addCSSClass("contentElementBaseStyle");
          colorField.addCSSClass(getColorToColorClassMap().get(getColorStr(colorsForElement.get(i))));
        }
      }
    }
  }

  private String getShapeMasterName(AxisElement<?> element, boolean forTopAxis) {
    return BusinessProcess.class.equals(element.getElement().getClass()) ? (forTopAxis ? PROCESS_HORIZONTAL : PROCESS_VERTICAL) : AXIS_ELEMENT;
  }

  private PathShape createProcessForShape(BasicShape shape, double xPos, double yPos, double width, double height, boolean vertical)
      throws SvgExportException {

    PathShape process = (PathShape) shape.createNewBasicInnerShape(SVG_BASIC_SHAPES.PATH);
    process.moveTo(xPos, yPos);

    if (vertical) {

      process.lineTo(xPos, yPos + height);
      process.lineTo(xPos + width * 0.5, yPos + height + 16);
      process.lineTo(xPos + width, yPos + height);
      process.lineTo(xPos + width, yPos);
      process.lineTo(xPos + width * 0.5, yPos + 16);
      process.setClosePath(true);
    }
    else {

      process.lineTo(xPos + width, yPos);
      process.lineTo(xPos + width + 16, yPos + height * 0.5);
      process.lineTo(xPos + width, yPos + height);
      process.lineTo(xPos, yPos + height);
      process.lineTo(xPos + 16, yPos + height * 0.5);
      process.setClosePath(true);
    }

    return process;
  }

  private RectangleShape createRectangle(BasicShape parentShape, double width, double height) throws SvgExportException {

    RectangleShape rect = (RectangleShape) parentShape.createNewBasicInnerShape(SVG_BASIC_SHAPES.RECTANGLE);
    rect.setPosition(0, 0);
    rect.setSize(width, height);

    return rect;
  }

  private void createGridLineClasses() throws SvgExportException {
    Map<String, String> lineMap = new HashMap<String, String>();
    lineMap.put(SvgBaseStyling.STROKE_COLOR, "#000000");
    lineMap.put(SvgBaseStyling.STROKE_WITDH, "1");
    lineMap.put(SvgBaseStyling.STROKE_LINE_PATTERN, "none");
    getSvgDocument().createNewCSSClass(GRID_LINE_SOLID, lineMap);

    lineMap = new HashMap<String, String>();
    lineMap.put(SvgBaseStyling.STROKE_COLOR, "#000000");
    lineMap.put(SvgBaseStyling.STROKE_WITDH, "1");
    lineMap.put(SvgBaseStyling.STROKE_LINE_PATTERN, "4");
    getSvgDocument().createNewCSSClass(GRID_LINE_DOTTED, lineMap);
  }

  private String getAxisElementXLink(AxisElement<?> axisElement) {
    IdentityEntity element = axisElement.getElement();
    if (element instanceof AttributeValue && !Integer.valueOf(0).equals(((AttributeValue) element).getAbstractAttributeType().getId())) {
      AttributeType aType = ((AttributeValue) element).getAbstractAttributeType();
      return retrieveXLinkUrlForIdentityEntity(aType, landscapeOptions.getServerUrl());
    }
    else {
      return retrieveXLinkUrlForIdentityEntity(element, landscapeOptions.getServerUrl());
    }
  }

  private String estimateElementName(double shapeWidth, AxisElement<?> axisElement, boolean elementFromTopAxis) {

    String ownName = null;
    if (axisElement.getElement() instanceof HierarchicalEntity<?>) {
      ownName = getBuildingBlockNonHierarchicalName((BuildingBlock) axisElement.getElement());
    }
    else {
      ownName = axisElement.getElement().getIdentityString();
    }

    if (!landscapeDiagram.isUseNamesLegend()) {
      return ownName;
    }

    double elementTextSizePt = 0;
    if (elementFromTopAxis) {
      elementTextSizePt = TOP_AXIS_TEXT_SIZE_PT;
    }
    else {
      elementTextSizePt = SIDE_AXIS_TEXT_SIZE_PT;
    }

    String url = getAxisElementXLink(axisElement);
    String screenName = getSvgNamesLegend().addLegendEntry(ownName, null, "", shapeWidth, elementTextSizePt, url);

    return screenName;
  }
}

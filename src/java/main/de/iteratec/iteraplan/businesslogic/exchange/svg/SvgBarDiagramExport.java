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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.piebar.BarDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.piebar.PieBar;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.ValuesType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.SingleBarOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.svg.model.BasicShape;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.Shape;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.model.impl.AdvancedTextHelper;
import de.iteratec.svg.model.impl.RectangleShape;


public class SvgBarDiagramExport extends AbstractSvgPieBarExport {

  private static final Logger               LOGGER                      = Logger.getIteraplanLogger(SvgBarDiagramExport.class);
  private static final String               SVG_TEMPLATE_FILE           = "/SVGBarTemplate.svg";
  private static final String               CSS_RIGHT_TEXT              = "anchorEnd";
  private static final String               CSS_CENTER_TEXT             = "anchorMiddle";
  private static final String               CSS_NUM_LABEL_TEXT          = "numLabelText";
  private static final String               CSS_LINE_GREY               = "greyLine";

  private static final double               BARS_AREA_WIDTH             = 1000;
  /** distance between two bars as fraction of the bars' width */
  private static final double               DISTANCE_BETWEEN_BARS_RATIO = 0.3;
  private static final double               BARS_AREA_PADDING           = MARGIN / 2;
  private static final double               BARS_WIDTH                  = 50;
  private static final Coordinates          CONTENT_AREA_POS            = new Coordinates(MARGIN * 2, MARGIN_TOP + MARGIN);
  private double                            elementCountLabelWidth      = 0;

  private final BarDiagram                  diagram;
  /** maps an attribute name to the corresponding ColorDimension */
  private final Map<String, ColorDimension> singleBarColorDimensions    = CollectionUtils.hashMap();
  private boolean                           hasMultipleColorLegends     = false;

  private static final String               SINGLE_USED_VALUES_KEY      = "singleAttributeUsedValuesForSingleColorLegend";
  private final Map<String, Set<String>>    usedAttributeValues         = new LinkedHashMap<String, Set<String>>();
  private final Map<String, ValuesType>     singleBarValuesType         = CollectionUtils.hashMap();

  /** Ratio between the displayed pixelHeight of a bar and the corresponding {@link PieBar#getTotalSize()} */
  private final List<String>                orderedBarsLabels           = CollectionUtils.arrayList();
  private double                            barsLengthRatio             = 1;
  private double                            distanceBetweenBars         = 0;
  private double                            barsAreaHeight;
  private double                            barsLabelMaxWidth           = 0;

  private final Coordinates                 documentDimension           = new Coordinates(CONTENT_AREA_POS);

  public SvgBarDiagramExport(BarDiagram barDiagram, PieBarDiagramOptionsBean pieBarOptions, AttributeTypeService attributeTypeService,
      AttributeValueService attributeValueService) {
    super(attributeTypeService, attributeValueService, pieBarOptions);
    this.diagram = barDiagram;

    loadSvgDocumentFromTemplate(SVG_TEMPLATE_FILE, "Bar");
    if (isAttributeTypesDiagram()) {
      distanceBetweenBars = BARS_WIDTH * DISTANCE_BETWEEN_BARS_RATIO;
    }

    documentDimension.incX(BARS_AREA_WIDTH);
  }

  @Override
  public Document createDiagram() {
    LOGGER.info("creating SVG-document for bar diagram");

    try {
      initializeColors();

      calculateBarMetrics();
      estimateBarsLabelsAreaWidth();

      double queryInfoHeight = createQueryInfo();

      createBarAreaBackgroundAndHeader(queryInfoHeight);
      createBars(queryInfoHeight);
      createColorLegends(queryInfoHeight);

      createMultiValueAssignmentWarning();

      setDocumentDimensions();

      createDiagramTitle(diagram.getDiagramTitle(), 0, 0, MARGIN);
      if (!getOptions().isNakedExport()) {
        createGeneratedInformation(getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
        createLogos(0, 0, getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
      }
      
      setCustomSize(getOptions().getWidth(), getOptions().getHeight());

      if (!isEmbeddedDiagram()) {
        getSvgDocument().finalizeDocument();
      }
    } catch (SvgExportException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }

    return getSvgDocument();
  }

  private double createQueryInfo() throws SvgExportException {
    double queryInfoHeight = 0;
    if (getOptions().isShowSavedQueryInfo()) {
      Coordinates pos = new Coordinates(CONTENT_AREA_POS.getX(), CONTENT_AREA_POS.getY() - MARGIN);
      double width = barsLabelMaxWidth + elementCountLabelWidth + BARS_AREA_WIDTH;
      queryInfoHeight = createSavedQueryInfo(pos, width, getOptions().getSavedQueryInfo(), getOptions().getServerUrl()) - CONTENT_AREA_POS.getY()
          + MARGIN;
      documentDimension.incY(queryInfoHeight);
    }
    return queryInfoHeight;
  }

  private void initializeColors() throws SvgExportException {
    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(getOptions().getSelectedBbType());

    if (isAttributeTypesDiagram() && PieBarDiagramOptionsBean.ValuesType.VALUES.equals(getOptions().getDiagramValuesType())) {

      hasMultipleColorLegends = true;
      for (SingleBarOptionsBean sbob : getOptions().getBarsMap().values()) {
        if (sbob.isSelected()) {
          ColorDimension colorDim = createColorDimension(sbob.getColorOptions(), tobb);
          singleBarColorDimensions.put(sbob.getLabel(), colorDim);
          singleBarValuesType.put(sbob.getLabel(), sbob.getType());
          generateCssColorStyles(colorDim);
        }
      }
    }
    else {
      ColorDimensionOptionsBean colorOptions = getOptions().getColorOptionsBean();
      setColorDimension(createColorDimension(colorOptions, tobb));
      if (!diagram.isColorsSet()) {
        // use more pretty color than the "unspecified"-one
        getColorDimension().setDefaultValue(colorOptions.getAvailableColors().get(0));
      }
      generateCssColorStyles(getColorDimension());
    }
  }

  private boolean isAttributeTypesDiagram() {
    return PieBarDiagramOptionsBean.DiagramKeyType.ATTRIBUTE_TYPES.equals(getOptions().getDiagramKeyType());
  }

  private void calculateBarMetrics() {
    // barsLengthRatio
    final double innerBarsAreaHeight = BARS_AREA_WIDTH - 2 * BARS_AREA_PADDING;
    int barMaxSize = 0;
    for (PieBar bar : getUsedBars()) {
      barMaxSize = Math.max(barMaxSize, bar.getTotalSize());
    }
    barsLengthRatio = innerBarsAreaHeight / barMaxSize;

    // barsAreaHeight
    int usedBarsNumber = getUsedBars().size();
    barsAreaHeight = usedBarsNumber * BARS_WIDTH + (usedBarsNumber - 1) * distanceBetweenBars + 2 * BARS_AREA_PADDING;

    documentDimension.incY(barsAreaHeight);
  }

  private void estimateBarsLabelsAreaWidth() {
    for (PieBar bar : getUsedBars()) {
      barsLabelMaxWidth = Math.max(AdvancedTextHelper.getTextWidth(bar.getLabel().length(), 11, AdvancedTextHelper.POINT_TO_UNIT_CONSTANT),
          barsLabelMaxWidth);
    }
    // using 2 pt larger fontsize to account for bold font
    barsLabelMaxWidth = Math.max(
        AdvancedTextHelper.getTextWidth(diagram.getHorizontalAxisLabel().length(), 13, AdvancedTextHelper.POINT_TO_UNIT_CONSTANT), barsLabelMaxWidth);

    if (getOptions().isShowBarSizeLabels()) {
      // using 2 pt larger fontsize to account for bold font
      elementCountLabelWidth = AdvancedTextHelper.getTextWidth(getSelectedBbLabel().length(), 13, AdvancedTextHelper.POINT_TO_UNIT_CONSTANT)
          + BARS_AREA_PADDING;
    }

    documentDimension.incX(barsLabelMaxWidth + elementCountLabelWidth);
  }

  private String getSelectedBbLabel() {
    return MessageAccess.getStringOrNull(getOptions().getSelectedBbType(), getLocale());
  }

  private List<PieBar> getUsedBars() {
    List<PieBar> usedBars = CollectionUtils.arrayList();
    if (getOptions().isShowEmptyBars()) {
      return diagram.getBars();
    }
    else {
      for (PieBar bar : diagram.getBars()) {
        if (bar.getTotalSize() != 0) {
          usedBars.add(bar);
        }
      }
      return usedBars;
    }
  }

  private void createBarAreaBackgroundAndHeader(double deltaY) throws SvgExportException {
    long maxValue = Math.round((BARS_AREA_WIDTH - 2 * BARS_AREA_PADDING) / barsLengthRatio);
    long step = 1;
    while (maxValue > step * 10) {
      step *= 10;
    }
    if (maxValue <= step * 2) {
      step = Math.max(step / 2, 1);
    }

    int labelValue = 0;
    double xPos = getBarsStartXPos();
    double yPos = CONTENT_AREA_POS.getY() + deltaY;
    labelValue += step;
    xPos += step * barsLengthRatio;
    while (labelValue <= maxValue) {
      createTextLabel(MASTER_LABEL_LARGE, new Coordinates(xPos, yPos), String.valueOf(labelValue), CSS_NUM_LABEL_TEXT, null);
      createVerticalLine(new Coordinates(xPos, yPos), barsAreaHeight, CSS_LINE_GREY);
      labelValue += step;
      xPos += step * barsLengthRatio;
    }

    Coordinates pos = new Coordinates(CONTENT_AREA_POS.getX() + barsLabelMaxWidth, yPos);
    createVerticalLine(new Coordinates(pos.getX() + BARS_AREA_PADDING, yPos - MARGIN / 2), barsAreaHeight + MARGIN / 2, CSS_LINE_SOLID);
    Shape textLabel = createTextLabel(MASTER_LABEL_LARGE, pos, diagram.getHorizontalAxisLabel(), CSS_TEXT_11PT_BOLD, null);
    textLabel.addTextCSSClass(CSS_RIGHT_TEXT);

    if (elementCountLabelWidth != 0) {
      pos = new Coordinates(CONTENT_AREA_POS.getX() + barsLabelMaxWidth + elementCountLabelWidth, yPos);
      createVerticalLine(new Coordinates(pos.getX() + BARS_AREA_PADDING, yPos - MARGIN / 2), barsAreaHeight + MARGIN / 2, CSS_LINE_SOLID);
      textLabel = createTextLabel(MASTER_LABEL_LARGE, pos, getSelectedBbLabel(), CSS_TEXT_11PT_BOLD, null);
      textLabel.addTextCSSClass(CSS_RIGHT_TEXT);
    }
  }

  private void createBars(double deltaY) throws SvgExportException {
    final double barPosX = getBarsStartXPos();
    double barPosY = CONTENT_AREA_POS.getY() + BARS_AREA_PADDING + deltaY;

    if (!hasMultipleColorLegends) {
      /* init used attribute values list for diagrams with only one color legend.
       * initialisation for multi color legends happen at "getUsedValuesListForBar" at "createBar" */
      usedAttributeValues.put(SINGLE_USED_VALUES_KEY, new HashSet<String>());
    }

    final double centerOfBarLabelHeader = CONTENT_AREA_POS.getX() + barsLabelMaxWidth / 2;
    for (PieBar bar : getUsedBars()) {
      orderedBarsLabels.add(bar.getLabel());
      createBar(barPosX, barPosY, barsLengthRatio, bar);
      createBarLabel(centerOfBarLabelHeader, barPosY, bar);
      barPosY += BARS_WIDTH + distanceBetweenBars;
    }
  }

  private double getBarsStartXPos() {
    return CONTENT_AREA_POS.getX() + barsLabelMaxWidth + elementCountLabelWidth + BARS_AREA_PADDING;
  }

  private void createBar(final double barPosX, final double barPosY, final double lengthRatio, final PieBar bar) throws SvgExportException {
    Set<String> usedValues = getUsedValuesListForBar(bar);

    double currentBarPosX = barPosX;
    List<BarSegmentLabel> labels = CollectionUtils.arrayList();
    for (Map.Entry<String, Integer> entry : bar.getValuesToSizeMap().entrySet()) {
      Integer sizeValue = entry.getValue();
      if (sizeValue != null && !sizeValue.equals(Integer.valueOf(0))) {
        double length = entry.getValue().doubleValue() * lengthRatio;
        RectangleShape barSegment = (RectangleShape) createNewBasicShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
        barSegment.setPosition(currentBarPosX, barPosY);
        barSegment.setSize(length, BARS_WIDTH);

        String colorCss = getColorClassForBarSegment(entry, bar.getLabel());
        barSegment.addCSSClass(colorCss);

        if (getOptions().isShowSegmentLabels()) {
          labels.add(new BarSegmentLabel(createSegmentLabelText(entry, bar), (currentBarPosX + length / 2)));
        }

        currentBarPosX += length;
      }
      if (sizeValue != null && (getOptions().isShowEmptySegments() || !sizeValue.equals(Integer.valueOf(0)))) {
        usedValues.add(entry.getKey());
      }
    }

    drawBarSegmentLabels(barPosY, labels);

    //create outline
    double length = bar.getTotalSize() * lengthRatio;
    RectangleShape barShape = (RectangleShape) createNewBasicShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
    barShape.setPosition(barPosX, barPosY);
    barShape.setSize(length, BARS_WIDTH);
    barShape.addCSSClass(CSS_LINE_SOLID);
    barShape.addCSSClass(CSS_FILL_TRANSPARENT_STYLE);
  }

  private void drawBarSegmentLabels(final double barPosY, final List<BarSegmentLabel> labels) throws SvgExportException {
    if (!labels.isEmpty()) {
      Iterator<BarSegmentLabel> labelIt = labels.iterator();
      BarSegmentLabel current = labelIt.next();
      BarSegmentLabel next = null;
      double[] yPos = { barPosY + BARS_WIDTH - 6, barPosY + BARS_WIDTH / 2, barPosY + 17 };
      int midPosY = 1;
      int upperPosY = 2;
      int lowerPosY = 0;
      int labelPosY = midPosY;
      while (current != null) {
        boolean collision = false;
        if (labelIt.hasNext()) {
          next = labelIt.next();
          double distance = next.getPos() - current.getPos();
          if ((current.getEstimatedWidth() + next.getEstimatedWidth()) / 2 > distance) {
            collision = true;
          }
        }
        else {
          next = null;
        }

        if (labelPosY == midPosY && collision) {
          labelPosY = lowerPosY;
        }
        else if (labelPosY == lowerPosY) {
          labelPosY = upperPosY;
        }
        else if (labelPosY == upperPosY) {
          labelPosY = lowerPosY;
        }
        createTextLabel(MASTER_LABEL_LARGE, new Coordinates(current.getPos(), yPos[labelPosY]), current.getLabelText(), CSS_CENTER_TEXT, null);
        if (!collision) {
          labelPosY = midPosY;
        }
        current = next;
      }
    }
  }

  private Set<String> getUsedValuesListForBar(final PieBar bar) {
    Set<String> usedValues = CollectionUtils.hashSet();
    if (hasMultipleColorLegends) {
      usedAttributeValues.put(bar.getLabel(), usedValues);
    }
    else {
      usedValues = usedAttributeValues.get(SINGLE_USED_VALUES_KEY);
    }
    return usedValues;
  }

  private void createBarLabel(final double centerOfBarLabelHeader, final double barPosY, final PieBar bar) throws SvgExportException {
    Coordinates labelPos = new Coordinates(CONTENT_AREA_POS.getX() + barsLabelMaxWidth, barPosY + BARS_WIDTH / 2);
    String anchorCSS = CSS_RIGHT_TEXT;

    if (PieBarDiagramOptionsBean.DiagramKeyType.ATTRIBUTE_VALUES.equals(getOptions().getDiagramKeyType()) && elementCountLabelWidth == 0) {
      int keyAttrId = getOptions().getSelectedKeyAttributeTypeId();
      if (keyAttrId > 0) {
        AttributeType at = getAttributeTypeService().loadObjectById(Integer.valueOf(keyAttrId));
        if (at instanceof NumberAT) {
          anchorCSS = CSS_CENTER_TEXT;
          labelPos.setX(centerOfBarLabelHeader);
        }
      }
    }
    StringBuilder labelText = new StringBuilder();
    labelText.append(bar.getLabel());
    if (bar.isMultiValueATBar()) {
      labelText.append(" (*)");
    }
    Shape textLabel = createTextLabel(MASTER_LABEL_LARGE, labelPos, labelText.toString(), CSS_TEXT_NORMAL, null);
    textLabel.addTextCSSClass(anchorCSS);

    if (elementCountLabelWidth != 0) {
      labelText = new StringBuilder();
      labelText.append(String.valueOf(bar.getRealSize()));
      labelText.append(" (").append(createPercentageLabelText(bar.getRealSize(), getOptions().getNumberOfSelectedElements())).append(")");
      labelPos.setX(CONTENT_AREA_POS.getX() + barsLabelMaxWidth + elementCountLabelWidth);
      textLabel = createTextLabel(MASTER_LABEL_LARGE, labelPos, labelText.toString(), CSS_TEXT_NORMAL, null);
      textLabel.addTextCSSClass(CSS_RIGHT_TEXT);
    }
  }

  private String getColorClassForBarSegment(final Map.Entry<String, Integer> entry, final String attributeName) {
    Color color;
    ColorDimension colorDim;

    if (hasMultipleColorLegends) {
      colorDim = singleBarColorDimensions.get(attributeName);
    }
    else {
      colorDim = getColorDimension();
    }

    if (DimensionOptionsBean.DEFAULT_VALUE.equals(entry.getKey())) {
      color = colorDim.getDefaultValue();
    }
    else {
      color = colorDim.getValue(entry.getKey());
    }
    return getColorToColorClassMap().get(getColorStr(color));
  }

  private void createColorLegends(double deltaY) throws SvgExportException {
    Coordinates pos = new Coordinates(CONTENT_AREA_POS);
    boolean displayLegendRightFromBars = isEmbeddedDiagram() && !isAttributeTypesDiagram();

    if (!displayLegendRightFromBars) {
      pos.incY(barsAreaHeight + MARGIN + deltaY);
    }
    else {
      pos.incX(barsLabelMaxWidth + elementCountLabelWidth + BARS_AREA_WIDTH + MARGIN);
    }

    if (hasMultipleColorLegends) {
      double legendAreaStartPosY = pos.getY();
      double legendWidth = getSvgDocument().getMasterShapeProperties(MASTER_LEGEND_BOX).getShapeWidth();
      double maxX = CONTENT_AREA_POS.getX() + BARS_AREA_WIDTH + barsLabelMaxWidth + elementCountLabelWidth;

      for (String barLabel : orderedBarsLabels) {
        ColorDimension currentColorDim = singleBarColorDimensions.get(barLabel);
        if (!currentColorDim.getValues().isEmpty()) {
          if (pos.getX() + legendWidth > maxX) {
            // start new row with color legends
            pos.setX(CONTENT_AREA_POS.getX());
            pos.incY(getMaxLegendHeight() + MARGIN);
            resetMaxLegendHeight();
          }
          createPieBarColorLegend(singleBarValuesType.get(barLabel), currentColorDim, pos, usedAttributeValues.get(barLabel), true);
          pos.incX(legendWidth + MARGIN);
        }
      }

      documentDimension.incY(pos.getY() + getMaxLegendHeight() - legendAreaStartPosY + MARGIN);
    }
    else {
      boolean headerEnabled = true;
      if (isAttributeTypesDiagram()) {
        headerEnabled = false;
      }
      if ((!getColorDimension().getValues().isEmpty() && diagram.isColorsSet()) || isAttributeTypesDiagram()) {
        double legendBoxHeight = getSvgDocument().getMasterShapeProperties(MASTER_LEGEND_BOX).getShapeHeight();
        Set<String> usedValues = usedAttributeValues.get(SINGLE_USED_VALUES_KEY);
        int legendSize = createPieBarColorLegend(getOptions().getDiagramValuesType(), getColorDimension(), pos, usedValues, headerEnabled);
        if (!displayLegendRightFromBars) {
          documentDimension.incY(legendBoxHeight * legendSize + MARGIN);
        }
        else {
          double legendBoxWidth = getSvgDocument().getMasterShapeProperties(MASTER_LEGEND_BOX).getShapeWidth();
          documentDimension.incX(legendBoxWidth + MARGIN);
          documentDimension.incY(Math.max(0, legendBoxHeight * legendSize - barsAreaHeight));
        }
      }
    }
  }

  private void createMultiValueAssignmentWarning() throws SvgExportException {
    if (diagram.isShowMultiValueWarning()) {
      Coordinates pos = new Coordinates(CONTENT_AREA_POS.getX(), documentDimension.getY() + MARGIN);
      Shape label = createTextLabel(MASTER_LABEL_LARGE, pos, MessageAccess.getStringOrNull("graphicalExport.pieBar.bar.multiAVWarning", getLocale()),
          CSS_TEXT_NORMAL, null);
      documentDimension.incY(label.getHeight() + MARGIN);
    }
  }

  private void setDocumentDimensions() {
    double calculatedWidth = documentDimension.getX() + 2 * MARGIN;
    double calculatedHeight = documentDimension.getY() + 2 * MARGIN;
    getSvgDocument().setPageSize(calculatedWidth, calculatedHeight);

    if (isEmbeddedDiagram()) {
      getDiagramContainer().setSize(getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
    }
  }

  @Override
  protected String getNameForAdapter(final Integer attributeId) {
    switch (getOptions().getValuesSource()) {
      case ATTRIBUTE:
        String name = BBAttribute.getAttributeNameById(attributeId);
        if (attributeId.intValue() <= 0) {
          name = MessageAccess.getStringOrNull(name, UserContext.getCurrentLocale());
        }
        return name;
      case ASSOCIATION:
        return MessageAccess.getStringOrNull(getOptions().getSelectedAssociation(), UserContext.getCurrentLocale());
      default:
        LOGGER.error("Invalid ValuesSource: {0}", getOptions().getValuesSource().name());
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
  }

  @Override
  protected List<String> getAttributeValuesForAdapter(final Integer attributeId) {
    if (!diagram.isColorsSet()) {
      return new ArrayList<String>();
    }
    else if (hasMultipleColorLegends) {
      return getOptions().getBarsMap().get(attributeId).getColorOptions().getAttributeValues();
    }
    else {
      return getOptions().getColorOptionsBean().getAttributeValues();
    }
  }

  private class BarSegmentLabel extends SegmentLabel {

    public BarSegmentLabel(String labelText, double pos) {
      super(labelText, pos);
      if ((pos - getEstimatedWidth() / 2) < getBarsStartXPos()) {
        setPos(getBarsStartXPos() + getEstimatedWidth() / 2);
      }
    }

  }

}

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.cluster.ClusterDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.cluster.ContentElement;
import de.iteratec.iteraplan.businesslogic.exchange.common.cluster.MainAxisElement;
import de.iteratec.iteraplan.businesslogic.exchange.common.cluster.SecondaryAxisElement;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.INamesLegend;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.INamesLegend.LegendMode;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterSecondOrderBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.Shape;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.model.impl.AdvancedTextHelper;


public class SvgClusterExport extends SvgExport {

  private static final String         SVG_TEMPLATE_FILE        = "/SVGClusterTemplate.svg";

  private static final double         MARGIN_BIG               = 40;
  private static final double         MARGIN_LITTLE            = 20;
  private static final double         MARGIN_MINI              = 10;
  private static final double         MARGIN_PAGE              = 80;
  private static final double         LEGENDBOX_LABEL_HEIGHT   = 52;
  private static final double         LEGENDBOX_LABEL_WIDTH    = 240;

  private static final double         COLUMN_HEIGHT            = 30;
  private static final double         ELEMENT_HEIGHT           = 50;
  private static final double         ELEMENT_MARGIN           = 10;

  private static final double         TITLE_LABEL_TEXT_PT      = 11;

  private static final double         COLUMN_WIDTH             = 300;

  private static final int            COLOR_LEGEND_TARGET_SIZE = 7;
  private static final String         MAIN_AXIS_KEY            = "mainAxis";

  private final ClusterDiagram        clusterDiagram;
  private final ClusterOptionsBean    clusterOptions;

  private Map<String, ColorDimension> secondOrderColorDimensions;
  private Map<String, Set<String>>    usedAttributeValues;

  private double                      globalContentHeight;
  private double                      globalContentWidth;

  public SvgClusterExport(ClusterDiagram clusterDiagram, ClusterOptionsBean clusterOptions, AttributeTypeService attributeTypeService,
      AttributeValueService attributeValueService) {

    super(attributeTypeService, attributeValueService);
    this.clusterDiagram = clusterDiagram;
    this.clusterOptions = clusterOptions;

    loadSvgDocumentFromTemplate(SVG_TEMPLATE_FILE, "Cluster");
  }

  @Override
  public Document createDiagram() {
    if (clusterOptions.isUseNamesLegend()) {
      setSvgNamesLegend(new SvgNamesLegend(getSvgDocument()));
      getSvgNamesLegend().setLegendMode(LegendMode.AUTO);
    }

    try {
      initalizeColorDimensions();
      generateCssColorStyles();

      double yPointer = createQueryInfo();
      double mainAxisY = yPointer + COLUMN_HEIGHT;

      yPointer = createMainAxis(yPointer);
      yPointer += MARGIN_MINI;
      createSecondaryAxisAndLines(yPointer);
      createContentElements();

      setDocumentDimensions(yPointer);

      if (clusterOptions.isUseNamesLegend()) {
        createNamesLegend(mainAxisY);
      }

      if(clusterOptions.isLegend()){
        createColorLegends(yPointer);
      }
      if (!clusterOptions.isNakedExport()) {
        createDiagramTitle(clusterDiagram.getDiagramTitle(), 0, 0, MARGIN_PAGE + MARGIN_LITTLE);
        createGeneratedInformation(getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
        createLogos(0, 0, getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
      }

      setCustomSize(clusterOptions.getWidth(), clusterOptions.getHeight());

      getSvgDocument().finalizeDocument();

    } catch (SvgExportException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }

    return getSvgDocument();
  }

  private double createQueryInfo() throws SvgExportException {
    double yPointer = MARGIN_LITTLE + MARGIN_PAGE;
    if (clusterOptions.isShowSavedQueryInfo()) {
      Coordinates pos = new Coordinates(MARGIN_PAGE + MARGIN_LITTLE, yPointer);
      double width = clusterDiagram.getMainAxis().getMainAxisLength() * COLUMN_WIDTH + MARGIN_PAGE + MARGIN_LITTLE;
      yPointer = createSavedQueryInfo(pos, width, clusterOptions.getSavedQueryInfo(), clusterOptions.getServerUrl());
    }
    return yPointer;
  }

  private void initalizeColorDimensions() {
    usedAttributeValues = new HashMap<String, Set<String>>();

    // Main color dimension
    setColorDimension(createColorDimension(clusterOptions.getColorOptionsBean(),
        TypeOfBuildingBlock.getTypeOfBuildingBlockByString(clusterOptions.getSelectedBbType())));
    usedAttributeValues.put(MAIN_AXIS_KEY, new HashSet<String>());

    // Second order color dimensions
    secondOrderColorDimensions = new HashMap<String, ColorDimension>();
    TypeOfBuildingBlock typeOfBb;
    for (ClusterSecondOrderBean secondOrderBean : clusterOptions.getSecondOrderBeans()) {
      if (secondOrderBean.isSelected()) {
        if (secondOrderBean.getBeanType().equals(ClusterSecondOrderBean.BUILDING_BLOCK_BEAN)) {
          typeOfBb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(secondOrderBean.getRepresentedType());
        }
        else {
          typeOfBb = null;
        }
        ColorDimension dim = createColorDimension(secondOrderBean.getColorOptions(), typeOfBb);
        secondOrderColorDimensions.put(secondOrderBean.getName(), dim);
        usedAttributeValues.put(secondOrderBean.getName(), new HashSet<String>());
      }
    }
  }

  /**
   * Generates the needed CSS color classes. The method replaces the common color class generation,
   * as the cluster graphic has multiple color legends.
   * 
   * @throws SvgExportException
   *           if some error occurs when generating the colors classes.
   */
  private void generateCssColorStyles() throws SvgExportException {
    // Generate the common color styles
    generateCssColorStyles(getColorDimension());

    for (Entry<String, ColorDimension> entry : secondOrderColorDimensions.entrySet()) {
      generateCssColorStyles(entry.getValue());
    }
  }

  private double createMainAxis(double yPointer) throws SvgExportException {
    double mainAxisBaseX = MARGIN_PAGE + MARGIN_LITTLE;
    Coordinates pointer = new Coordinates(mainAxisBaseX + clusterDiagram.getMainAxis().getMainAxisLength() * COLUMN_WIDTH * 0.5, yPointer);

    // Create axis title
    createTextLabel(MASTER_TITLE, pointer, clusterDiagram.getMainAxis().getTitle(), CSS_TEXT_CENTER_11PT, null);

    // Create axis headers
    Set<String> attrValues = usedAttributeValues.get(MAIN_AXIS_KEY);
    for (MainAxisElement<?> element : clusterDiagram.getMainAxis().getAllElements()) {

      List<String> colorsHex = new ArrayList<String>();
      String url = null;

      if (clusterOptions.getSelectedClusterMode().equals(Constants.REPORTS_EXPORT_CLUSTER_MODE_BB)) {

        List<Color> colors = new ArrayList<Color>();
        colors.add(getColorDimension().getValue(element.getElement()));
        attrValues.add(getUsedAttributeValueRange(getColorDimension(), element.getElement()));

        List<Color> allColors = getColorDimension().getMultipleValues(element.getElement());
        if (allColors.size() > 1) {
          colors = allColors;
        }
        colorsHex = getColorStrings(allColors);

        url = retrieveXLinkUrlForIdentityEntity((BuildingBlock) element.getElement(), clusterOptions.getServerUrl());
      }
      else {
        if (element.getName().equals(DimensionOptionsBean.DEFAULT_VALUE)) {
          colorsHex.add(getColorStr(getColorDimension().getDefaultValue()));
          element.setName(MessageAccess.getStringOrNull(element.getName(), getLocale()));
        }
        else {
          colorsHex.add(getColorStr(getColorDimension().getValue(element.getName())));
        }
        AttributeType aType = getAttributeTypeService().loadObjectById(Integer.valueOf(clusterOptions.getSelectedAttributeType()));
        url = retrieveXLinkUrlForIdentityEntity(aType, clusterOptions.getServerUrl());
      }

      pointer = new Coordinates(mainAxisBaseX + element.getStartPosition() * COLUMN_WIDTH, yPointer + element.getDepth() * COLUMN_HEIGHT);
      double width = element.getLength() * COLUMN_WIDTH;

      Shape shapeElement = createCustomRectangle(MASTER_LABEL_LARGE, pointer, getScreenName(element, width, TITLE_LABEL_TEXT_PT), colorsHex, width,
          TITLE_LABEL_TEXT_PT, true, true);

      shapeElement.setXLink(url);

      // Set the x coordinate of the axis element, so that we can locate the cell for the content
      // elements.
      element.setReferencedCoordinate(pointer.getX());

    }

    globalContentHeight = clusterDiagram.getMainAxis().getMainAxisDepth() * COLUMN_HEIGHT;
    globalContentWidth = clusterDiagram.getMainAxis().getMainAxisLength() * COLUMN_WIDTH;

    return yPointer + globalContentHeight + MARGIN_LITTLE;
  }

  private void createSecondaryAxisAndLines(double yPointer) throws SvgExportException {
    double contentWidth = clusterDiagram.getMainAxis().getMainAxisLength() * COLUMN_WIDTH;
    double contentStartX = MARGIN_PAGE + MARGIN_LITTLE;
    Coordinates pointer;

    // Create labels and horizontal lines
    double accumulatedHeight = 0;
    double rowHeight;

    for (SecondaryAxisElement element : clusterDiagram.getSecondaryAxisElements()) {

      rowHeight = Math.max(getStringWidth(element.getTitle(), 10), element.getHeight() * ELEMENT_HEIGHT);

      // Create line
      pointer = new Coordinates(contentStartX, yPointer + accumulatedHeight);
      createHorizontalLine(pointer, contentWidth, CSS_LINE_DASHED_NORMAL);

      // Create label
      pointer = new Coordinates(contentStartX - MARGIN_LITTLE, yPointer + accumulatedHeight + rowHeight * 0.5);
      createTextLabel(MASTER_TITLE, pointer, element.getTitle(), CSS_TEXT_CENTER_8PT, null, 270);

      // Set the y coordinate of the axis element, so that we can locate the cell for the content
      // elements.
      element.setReferencedCoordinate(pointer.getY() - rowHeight * 0.5);

      accumulatedHeight = accumulatedHeight + rowHeight;

    }
    // Add closing line at the bottom
    pointer = new Coordinates(contentStartX, yPointer + accumulatedHeight);
    createHorizontalLine(pointer, contentWidth, CSS_LINE_SOLID);

    // Create vertical lines
    for (int i = 0; i <= clusterDiagram.getMainAxis().getMainAxisLength(); i++) {
      pointer = new Coordinates(contentStartX + i * COLUMN_WIDTH, yPointer);
      createVerticalLine(pointer, accumulatedHeight, CSS_LINE_SOLID);
    }

    globalContentHeight = globalContentHeight + accumulatedHeight;

  }

  private void createContentElements() throws SvgExportException {
    for (SecondaryAxisElement secondaryAxisElement : clusterDiagram.getSecondaryAxisElements()) {
      for (MainAxisElement<?> mainAxisElement : clusterDiagram.getMainAxis().getAllElements()) {
        for (ContentElement<?> element : secondaryAxisElement.getRelatedContentElements()) {
          if (element.getMainAxisReference().equals(mainAxisElement)) {
            createContent(element);
          }
        }
      }
    }
  }

  private void createContent(ContentElement<?> element) throws SvgExportException {
    Coordinates pointer = new Coordinates(element.getMainAxisReference().getReferencedCoordinate() + ELEMENT_MARGIN, element
        .getSecondaryAxisReference().getReferencedCoordinate() + element.getPositionInCell() * ELEMENT_HEIGHT + ELEMENT_MARGIN);

    ClusterSecondOrderBean secondOrderOptions = clusterOptions.getSecondOrderBean(element.getSecondaryAxisReference().getType());

    List<String> colorStrings;
    String url = "";

    ColorDimension colorDim = secondOrderColorDimensions.get(secondOrderOptions.getName());
    if (secondOrderOptions.getBeanType().equals(ClusterSecondOrderBean.BUILDING_BLOCK_BEAN)) {

      BuildingBlock bb = (BuildingBlock) element.getElement();
      List<Color> colors = new ArrayList<Color>();

      colors.add(colorDim.getValue(bb));
      List<Color> allColors = colorDim.getMultipleValues(bb);

      if (allColors.size() > 1) {
        colors = allColors;
      }
      colorStrings = getColorStrings(colors);

      url = retrieveXLinkUrlForIdentityEntity(bb, clusterOptions.getServerUrl());
    }
    else {
      colorStrings = new ArrayList<String>();
      colorStrings.add(getColorStr(colorDim.getValue(((AttributeValue) element.getElement()).getLocalizedValueString(getLocale()))));
    }

    Set<String> attrValues = usedAttributeValues.get(element.getSecondaryAxisReference().getType());
    attrValues.add(getUsedAttributeValueRange(colorDim, element.getElement()));

    Shape elementShape = null;
    double shapeWidth = COLUMN_WIDTH - 2 * ELEMENT_MARGIN;

    if (Constants.REPORTS_EXPORT_CLUSTER_BB_SHAPE_ARROW.equals(secondOrderOptions.getSelectedBbShape())) {

      elementShape = createProcessShape(MASTER_LABEL_LARGE, pointer, getScreenName(element, shapeWidth, 11), colorStrings, shapeWidth, 11, false,
          true);
    }
    else {
      elementShape = createCustomLabel(MASTER_LABEL_LARGE, pointer, getScreenName(element, shapeWidth, 11), colorStrings, shapeWidth, 11, false,
          true, Constants.REPORTS_EXPORT_CLUSTER_BB_SHAPE_ROUNDED.equals(secondOrderOptions.getSelectedBbShape()));
    }
    elementShape.setXLink(url);
  }

  private void createNamesLegend(double yPointer) throws SvgExportException {
    double legendX = MARGIN_PAGE + clusterDiagram.getMainAxis().getMainAxisLength() * COLUMN_WIDTH + MARGIN_PAGE;

    double frameWidth = getSvgNamesLegend().getLegendWidth();
    double frameHeight = getSvgDocument().getPageHeight() - MARGIN_TOP - MARGIN;
    super.createNamesLegend(legendX, yPointer, frameWidth, frameHeight, clusterOptions.isNakedExport(), clusterDiagram.getDiagramTitle());

    if (INamesLegend.LegendMode.IN_PAGE.equals(getSvgNamesLegend().getLegendMode())) {
      globalContentWidth += frameWidth;
      getSvgDocument().setPageSize(getSvgDocument().getPageWidth() + frameWidth, getSvgDocument().getPageHeight());
    }
  }

  private void setDocumentDimensions(double yPointer) {
    int maxLegendSize;
    double legendsWidth;
    if (!clusterOptions.isLegend()){
      maxLegendSize = 0;
      legendsWidth = 0;
    }else{
      int currentLegendSize = getColorDimension().getValues().size();
      if (currentLegendSize > COLOR_LEGEND_TARGET_SIZE) {
        currentLegendSize = usedAttributeValues.get(MAIN_AXIS_KEY).size();
      }
      maxLegendSize = Constants.REPORTS_EXPORT_CLUSTER_MODE_BB.equals(clusterOptions.getSelectedClusterMode()) ? currentLegendSize : 0;
      legendsWidth = (clusterOptions.getColorOptionsBean().getDimensionAttributeId().intValue() == -1 && Constants.REPORTS_EXPORT_CLUSTER_MODE_BB
          .equals(clusterOptions.getSelectedClusterMode())) ? 0 : LEGENDBOX_LABEL_WIDTH;
  
      for (ClusterSecondOrderBean dimension : clusterOptions.getSecondOrderBeans()) {
        if (dimension.isSelected() && dimension.getColorOptions().getDimensionAttributeId().intValue() != -1) {
          currentLegendSize = secondOrderColorDimensions.get(dimension.getName()).getValues().size();
          if (currentLegendSize > COLOR_LEGEND_TARGET_SIZE) {
            currentLegendSize = usedAttributeValues.get(dimension.getName()).size();
          }
          maxLegendSize = Math.max(maxLegendSize, currentLegendSize);
          legendsWidth += LEGENDBOX_LABEL_WIDTH + MARGIN_LITTLE;
        }
      }
      if (maxLegendSize > 0) {
        maxLegendSize += 2;
      }
    }
    // Verify that the resulting diagram is not too big. Note that the last check for maximal
    // diagram size was only concerned with the number of content entities. Now the legend height is
    // also taken into consideration. This is necessary, as a huge page size can cause errors
    // with the SVG-based transcoders, or memory issues.
    if (clusterDiagram.getSecondaryAxisHeight() + maxLegendSize > IteraplanProperties
        .getIntProperty(IteraplanProperties.GRAPHICAL_EXPORT_MAX_ELEMENTS)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.EXPORT_GRAPHICAL_MAX_SIZE_CONSTRAINT);
    }

    double legendBoxesHeight = maxLegendSize * LEGENDBOX_LABEL_HEIGHT;

    double pageHeight = 2 * MARGIN_PAGE + yPointer + globalContentHeight + legendBoxesHeight;

    double pageWidth = 2 * MARGIN_PAGE + Math.max(legendsWidth, globalContentWidth);

    getSvgDocument().setPageSize(pageWidth, pageHeight);
  }

  private void createColorLegendBox(Coordinates position, String url, String typeName, String dimensionName) throws SvgExportException {
    Shape shape = createCustomRectangle(MASTER_LEGEND_BOX, position, " ", COLOR_WHITE_HEX, LEGENDBOX_LABEL_WIDTH, 0, false, false);
    if (url != null) {
      shape.setXLink(url);
    }

    shape = createTextLabel(MASTER_TITLE, new Coordinates(position.getX() + LEGENDBOX_LABEL_WIDTH / 2, position.getY() + MARGIN_LITTLE), typeName,
        CSS_TEXT_CENTER_11PT, null);
    if (url != null) {
      shape.setXLink(url);
    }

    shape = createTextLabel(MASTER_TITLE, new Coordinates(position.getX() + LEGENDBOX_LABEL_WIDTH / 2, position.getY() + MARGIN_BIG), dimensionName,
        CSS_TEXT_CENTER_11PT, null);
    if (url != null) {
      shape.setXLink(url);
    }
  }

  private void createColorLegends(double yPointer) throws SvgExportException {
    double x = MARGIN_PAGE + MARGIN_LITTLE;
    double y = yPointer + MARGIN_BIG + MARGIN_MINI + globalContentHeight;

    int colorAttrId = clusterOptions.getColorOptionsBean().getDimensionAttributeId().intValue();

    if (colorAttrId > 0 && Constants.REPORTS_EXPORT_CLUSTER_MODE_BB.equals(clusterOptions.getSelectedClusterMode())) {
      AttributeType colorAt = getAttributeTypeService().loadObjectById(clusterOptions.getColorOptionsBean().getDimensionAttributeId());
      String headerUrl = retrieveXLinkUrlForIdentityEntity(colorAt, clusterOptions.getServerUrl());

      if (getColorDimension().getValues().size() > COLOR_LEGEND_TARGET_SIZE) {
        createColorLegendForUsedAttrValuesOnly(getColorDimension(), usedAttributeValues.get(MAIN_AXIS_KEY), new Coordinates(x, y
            + LEGENDBOX_LABEL_HEIGHT), MASTER_COLOR_LEGEND_FIELD, null, false, null);
      }
      else {
        createColorLegend(getColorDimension(), new Coordinates(x, y + LEGENDBOX_LABEL_HEIGHT), MASTER_COLOR_LEGEND_FIELD, null, false, null);
      }

      createColorLegendBox(new Coordinates(x, y), headerUrl, clusterDiagram.getMainAxis().getTitle(), getFieldValueFromDimension(getColorDimension()));

      x = x + LEGENDBOX_LABEL_WIDTH + MARGIN_LITTLE;
    }
    else if (colorAttrId == GraphicalExportBaseOptions.STATUS_SELECTED || colorAttrId == GraphicalExportBaseOptions.SEAL_SELECTED) {
      if (getColorDimension().getValues().size() > COLOR_LEGEND_TARGET_SIZE) {
        createColorLegendForUsedAttrValuesOnly(getColorDimension(), usedAttributeValues.get(MAIN_AXIS_KEY), new Coordinates(x, y
            + LEGENDBOX_LABEL_HEIGHT), MASTER_COLOR_LEGEND_FIELD, null, false, null);
      }
      else {
        createColorLegend(getColorDimension(), new Coordinates(x, y + LEGENDBOX_LABEL_HEIGHT), MASTER_COLOR_LEGEND_FIELD, null, false, null);
      }

      createColorLegendBox(new Coordinates(x, y), null, clusterDiagram.getMainAxis().getTitle(), getFieldValueFromDimension(getColorDimension()));

      x = x + LEGENDBOX_LABEL_WIDTH + MARGIN_LITTLE;
    }

    for (SecondaryAxisElement secondaryAxis : clusterDiagram.getSecondaryAxisElements()) {
      ClusterSecondOrderBean secondOrderBean = clusterOptions.getSecondOrderBean(secondaryAxis.getType());
      ColorDimensionOptionsBean colorOptions = secondOrderBean.getColorOptions();
      int colorDimensionAttrId = colorOptions.getDimensionAttributeId().intValue();
      if (colorDimensionAttrId != -1 && colorOptions.getAttributeValues().size() > 0) {
        ColorDimension colorDim = secondOrderColorDimensions.get(secondOrderBean.getName());

        String typeName;
        if (secondOrderBean.getBeanType().equals(ClusterSecondOrderBean.BUILDING_BLOCK_BEAN)) {
          typeName = MessageAccess.getString(secondOrderBean.getName(), getLocale());
        }
        else {
          typeName = clusterDiagram.getMainAxis().getTitle();
        }

        String dimensionName;
        String dimensionUrl;

        if (colorDimensionAttrId == GraphicalExportBaseOptions.STATUS_SELECTED || colorDimensionAttrId == GraphicalExportBaseOptions.SEAL_SELECTED) {
          dimensionName = getFieldValueFromDimension(colorDim);
          dimensionUrl = "";
        }
        else {
          AttributeType attributeType = getAttributeTypeService().loadObjectById(colorOptions.getDimensionAttributeId());
          dimensionName = attributeType.getName();
          dimensionUrl = retrieveXLinkUrlForIdentityEntity(attributeType, clusterOptions.getServerUrl());
        }

        if (colorDim.getValues().size() > COLOR_LEGEND_TARGET_SIZE) {
          createColorLegendForUsedAttrValuesOnly(colorDim, usedAttributeValues.get(secondOrderBean.getName()), new Coordinates(x, y
              + LEGENDBOX_LABEL_HEIGHT), MASTER_COLOR_LEGEND_FIELD, null, false, null);
        }
        else {
          createColorLegend(colorDim, new Coordinates(x, y + LEGENDBOX_LABEL_HEIGHT), MASTER_COLOR_LEGEND_FIELD, null, false, null);
        }

        createColorLegendBox(new Coordinates(x, y), dimensionUrl, typeName, dimensionName);

        x = x + LEGENDBOX_LABEL_WIDTH + MARGIN_LITTLE;
      }
    }
  }

  private String getScreenName(MainAxisElement<?> element, double shapeWidth, double textSizePt) {
    if (element.getElement() instanceof BuildingBlock) {
      BuildingBlock block = (BuildingBlock) element.getElement();
      return getBbScreenName(block, shapeWidth, textSizePt);
    }
    else {
      return getScreenNameForString(element.getName(), shapeWidth, textSizePt);
    }
  }

  @SuppressWarnings("rawtypes")
  private String getScreenName(ContentElement element, double shapeWidth, double textSizePt) {
    if (element.getElement() instanceof BuildingBlock) {
      BuildingBlock block = (BuildingBlock) element.getElement();
      return getBbScreenName(block, shapeWidth, textSizePt);
    }
    else {
      return getScreenNameForString(element.getElement().getIdentityString(), shapeWidth, textSizePt);
    }
  }

  private String getBbScreenName(BuildingBlock buildingBlock, double shapeWidth, double textSizePt) {
    String ownName = getBuildingBlockNonHierarchicalName(buildingBlock);
    String hierarchicalName = getBuildingBlockHierarchicalName(buildingBlock);

    String url = retrieveXLinkUrlForIdentityEntity(buildingBlock, clusterOptions.getServerUrl());
    return getScreenName(clusterOptions, ownName, hierarchicalName, "", shapeWidth, textSizePt, url);
  }

  private String getScreenNameForString(String originalName, double shapeWidth, double textSizePt) {
    return getScreenName(clusterOptions, originalName, null, "", shapeWidth, textSizePt, "");
  }

  private double getStringWidth(String string, double sizePt) {
    return string.length() * sizePt * AdvancedTextHelper.POINT_TO_UNIT_CONSTANT + MARGIN_LITTLE;
  }

}

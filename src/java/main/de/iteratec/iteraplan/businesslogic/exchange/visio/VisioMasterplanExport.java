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

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.INamesLegend.LegendMode;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanBuildingBlockRow;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanCommon;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanTimespanRow;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanTimespanYear;
import de.iteratec.iteraplan.businesslogic.exchange.visio.legend.VisioNamesLegend;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.IMasterplanOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.InchConverter;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;
import de.iteratec.visio.model.Document;
import de.iteratec.visio.model.Shape;
import de.iteratec.visio.model.exceptions.MasterNotFoundException;


/**
 * Contains the builder algorithm to create a Visio masterplan diagram. This class does not use the
 * gxl2visio functionality, but directly uses the Visio element wrappers of the gxl2visio library to
 * generate a {@link Document}.
 */
public class VisioMasterplanExport extends VisioDimensionExport {

  private static final Logger                           LOGGER                       = Logger
.getIteraplanLogger(VisioMasterplanExport.class);

  private static final String                           VISIO_TEMPLATE_FILE          = "/VisioMasterplanTemplate.vdx";
  private static final String                           VISIO_DIAGRAM_TITLE          = "iteraplan masterplan";
  private static final String                           DEFAULT_COLOR                = SpringGuiFactory.getInstance().getDefaultColor();

  private static final Color                            COLOR_GREY_LIGHT             = new Color(233, 231, 212);
  private static final Color                            COLOR_GREY_DARK              = new Color(211, 207, 209);
  private static final Color                            COLOR_WHITE                  = new Color(255, 255, 255);
  private static final double                           DISTANCE_TO_MARGIN           = 1.0;

  private static final double                           BUILDING_BLOCK_WIDTH         = 8.4;
  private static final double                           DATE_AND_STATUS_WIDTH        = 1.8;
  private double                                        contentBaseY;
  private double                                        contentBaseX                 = BUILDING_BLOCK_WIDTH + DISTANCE_TO_MARGIN
                                                                                         + DATE_AND_STATUS_WIDTH * 3.0;

  private static final double                           MONTH_WIDTH                  = 0.6;
  private static final double                           ROW_HEIGHT                   = 0.6;
  private static final double                           TOP_ROW_HEIGHT_SMALL         = 0.3;
  private static final double                           BAR_HEIGHT                   = 0.35;
  private static final double                           BAR_OFFSET                   = 0.125;
  private static final double                           LEGEND_BOX_HEIGHT_CM         = 1.3;

  private static final String                           SHAPE_LINE                   = "Line";
  private static final String                           VALUE_LINESTYLE_SOLID        = "solid";
  private static final String                           VALUE_LINESTYLE_DOTTEDFINE   = "dottedFine";
  private static final String                           VALUE_LINESTYLE_DOTTEDCOARSE = "dottedCoarse";

  private static final String                           SHAPE_LABEL                  = "Label";
  private static final String                           SHAPE_LABEL_SMALL            = "LabelSmall";
  private static final String                           VALUE_TEXTSTYLE_TITLE        = "title";
  private static final String                           VALUE_TEXTSTYLE_NORMAL       = "normal";
  private static final String                           VALUE_TEXTSTYLE_SMALL        = "small";
  private static final String                           SHAPE_BAR                    = "Bar";
  private static final String                           SHAPE_LEGEND_COLOR           = "legend-color";

  private boolean                                       skipStatusColumn;

  private final IMasterplanOptions                      masterplanOptions;
  private final MasterplanDiagram                       masterplanDiagram;

  private Map<ColumnEntry, Double>                      customColumnWidths;
  private static final double                           LEGENDBOX_LABEL_WIDTH        = 2.5;
  private static final double                           MARGIN_PART                  = 0.5;
  private Map<MasterplanRowTypeOptions, ColorDimension> colorDimensions;

  public VisioMasterplanExport(MasterplanDiagram masterplanDiagram, IMasterplanOptions options, AttributeTypeService attributeTypeService,
      AttributeValueService attributeValueService) {

    super(attributeTypeService, attributeValueService);

    MasterplanCommon.validateOptions(options, getLocale());

    this.masterplanDiagram = masterplanDiagram;
    this.masterplanOptions = options;
  }

  @Override
  public Document createDiagram() {

    init(VISIO_TEMPLATE_FILE, VISIO_DIAGRAM_TITLE);

    if (masterplanOptions.isUseNamesLegend()) {
      setVisioNamesLegend(new VisioNamesLegend(getTargetPage()));
      getVisioNamesLegend().setLegendMode(LegendMode.IN_PAGE);
    }

    try {
      createColorDimensionsAndEstimateBaseY();

      //skipStatusColumn = MasterplanCommon.ckeckSkipStatusColumn(masterplanOptions);

      skipStatusColumn = masterplanDiagram.getStatusHeader().isEmpty();
      estimateCustomColumnWidths();

      createHeaderRowAndVerticalGrid();
      createContentRowsAndHorizontalGrid();

      double savedQueryInfoHeight = createQueryInfo();

      createColorLegends();

      createNamesLegendAndSetPageSize(savedQueryInfoHeight);
      // external title
      Shape title = createDiagramTitle(getTitleString());
      setTitlePosAndSize(title, /*1*/0.35, this.getTargetPage().getHeight(), null);
      createGeneratedInformation(this.getTargetPage().getWidth());
      // corners
      createLogos(0, 0, this.getTargetPage().getWidth(), this.getTargetPage().getHeight());

    } catch (MasterNotFoundException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
    return getVisioDocument();
  }

  private void createColorDimensionsAndEstimateBaseY() {
    //content elements (timelines) colors
    this.colorDimensions = Maps.newHashMap();

    contentBaseY = DISTANCE_TO_MARGIN;
    int legendHeight = 0;

    MasterplanRowTypeOptions level0Opts = masterplanOptions.getLevel0Options();
    MasterplanRowTypeOptions level1Opts = masterplanOptions.getLevel1Options();
    MasterplanRowTypeOptions level2Opts = masterplanOptions.getLevel2Options();

    ColorDimension currentColorDimension = createColorDimension(level0Opts.getColorOptions(), level0Opts.getTypeOfBuildingBlock());
    this.colorDimensions.put(level0Opts, currentColorDimension);
    legendHeight = Math.max(legendHeight, calculateColorLegendHeight(currentColorDimension));
    if (level1Opts != null) {
      currentColorDimension = createColorDimension(level1Opts.getColorOptions(), level1Opts.getTypeOfBuildingBlock());
      this.colorDimensions.put(level1Opts, currentColorDimension);
      legendHeight = Math.max(legendHeight, calculateColorLegendHeight(currentColorDimension));
      if (level2Opts != null) {
        currentColorDimension = createColorDimension(level2Opts.getColorOptions(), level2Opts.getTypeOfBuildingBlock());
        this.colorDimensions.put(level2Opts, currentColorDimension);
        legendHeight = Math.max(legendHeight, calculateColorLegendHeight(currentColorDimension));
      }
    }

    if (legendHeight > 0) {
      contentBaseY = contentBaseY + (legendHeight + 1) * LEGEND_BOX_HEIGHT_CM + DISTANCE_TO_MARGIN;
    }
  }

  private int calculateColorLegendHeight(ColorDimension colorDimension) {
    int height = colorDimension.getValues().size();
    if (colorDimension.hasUnspecificValue()) {
      height++;
    }
    return height;
  }

  private void createColorLegends() throws MasterNotFoundException {
    if (!masterplanOptions.isNakedExport()) {
      LOGGER.info("Drawing color legends on an VISIO masterplan diagram");

      int offset = 0;
      offset = createColorLegend(masterplanOptions.getLevel0Options(), offset);
      offset = createColorLegend(masterplanOptions.getLevel1Options(), offset);
      createColorLegend(masterplanOptions.getLevel2Options(), offset);
    }
  }

  private int createColorLegend(MasterplanRowTypeOptions rowType, int offset) throws MasterNotFoundException {
    if (rowType == null || rowType.getColorOptions().getDimensionAttributeId().intValue() == -1) {
      return offset;
    }

    String header = masterplanDiagram.getHeader(rowType.getLevel()) + " - " + getFieldValueFromDimension(colorDimensions.get(rowType));
    Coordinates position = getColorLegendPosition();

    if (offset > 0) {
      position.incX(offset * (LEGENDBOX_LABEL_WIDTH + MARGIN_PART));
    }

    createColorLegend(colorDimensions.get(rowType), this.getTargetPage(), position, SHAPE_LEGEND_COLOR, header, rowType.getTypeOfBuildingBlock());

    return offset + 1;
  }

  private double createQueryInfo() throws MasterNotFoundException {
    double savedQueryInfoHeight = 0;
    if (masterplanOptions.isShowSavedQueryInfo()) {
      Coordinates pos = new Coordinates(DISTANCE_TO_MARGIN, getHeaderRowYPos() + ROW_HEIGHT * 1.5);
      double namesLegendWidth = getVisioNamesLegend() != null ? getVisioNamesLegend().getLegendWidth() : 0;
      double width = contentBaseX + masterplanDiagram.getTimenspanLength() * MONTH_WIDTH + namesLegendWidth;
      List<Shape> queryInfos = createSavedQueryInfo(pos, width, 9, masterplanOptions.getSavedQueryInfo(), masterplanOptions.getServerUrl());
      savedQueryInfoHeight = getQueryInfoHeight(queryInfos);
    }
    return savedQueryInfoHeight;
  }

  private String getTitleString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append(MessageAccess.getStringOrNull("graphicalExport.masterplan.title", getLocale()));
    buffer.append('\n');
    buffer.append(MessageAccess.getStringOrNull("graphicalExport.title.content", getLocale()));
    buffer.append(": ");
    buffer.append(masterplanDiagram.getLevel0Header());

    if (masterplanOptions.getLevel1Options() != null) {
      buffer.append(' ');
      buffer.append(MasterplanDiagram.REL_TYPE_PREFIX);
      buffer.append(masterplanDiagram.getLevel1Header());
    }
    if (masterplanOptions.getLevel2Options() != null) {
      buffer.append(' ');
      buffer.append(MasterplanDiagram.REL_TYPE_PREFIX);
      buffer.append(masterplanDiagram.getLevel2Header());
    }

    return buffer.toString();
  }

  private boolean diagramHasTimelineFeatures() {
    if (masterplanOptions.getLevel0Options().getTimelineFeatures().size() > 0) {
      return true;
    }
    else if (masterplanOptions.getLevel1Options() != null && masterplanOptions.getLevel1Options().getTimelineFeatures().size() > 0) {
      return true;
    }
    else if (masterplanOptions.getLevel2Options() != null && masterplanOptions.getLevel2Options().getTimelineFeatures().size() > 0) {
      return true;
    }
    return false;
  }

  private double calculateFrameHeight() {
    int timelineRowCount = 0;
    for (MasterplanBuildingBlockRow row : masterplanDiagram.getBuildingBlockRows()) {
      timelineRowCount = timelineRowCount + row.getLogicalHeight();
    }
    int headerOffset = getHeaderRowHeight();
    return (timelineRowCount + headerOffset) * InchConverter.cmToInches(ROW_HEIGHT);
  }

  /**
   * Creates the names legend and resize the page for it afterwards
   * @throws MasterNotFoundException
   */
  private void createNamesLegendAndSetPageSize(double savedQueryInfoHeight) throws MasterNotFoundException {

    double namesLegendBaseX = InchConverter.cmToInches(contentBaseX + masterplanDiagram.getTimenspanLength() * MONTH_WIDTH + DISTANCE_TO_MARGIN);
    double frameHeight = calculateFrameHeight();
    double pageSizeX = namesLegendBaseX;

    if (masterplanOptions.isUseNamesLegend()) {
      createNamesLegend(namesLegendBaseX, InchConverter.cmToInches(contentBaseY + ROW_HEIGHT), getVisioNamesLegend().getLegendWidth(), frameHeight,
          masterplanOptions.isNakedExport(), "");
      pageSizeX = pageSizeX + getVisioNamesLegend().getLegendWidth() + DISTANCE_TO_MARGIN_INCHES;
    }

    double pageSizeY = InchConverter.cmToInches(contentBaseY) + frameHeight + DISTANCE_TO_MARGIN + DISTANCE_TO_MARGIN_INCHES + savedQueryInfoHeight;
    this.getTargetPage().setSize(pageSizeX, pageSizeY);
  }

  private void createHeaderRowAndVerticalGrid() throws MasterNotFoundException {

    Coordinates pointer = new Coordinates(DISTANCE_TO_MARGIN, getHeaderRowYPos());
    double secondHeaderRowHeight = 0;

    // Building blocks header
    createLabel(pointer.getX(), pointer.getY(), masterplanDiagram.getLevel0Header(), VALUE_TEXTSTYLE_TITLE, BUILDING_BLOCK_WIDTH, ROW_HEIGHT,
        COLOR_GREY_DARK);
    if (!masterplanDiagram.getLevel1Header().isEmpty()) {
      secondHeaderRowHeight = ROW_HEIGHT;
      createLabel(pointer.getX(), pointer.getY() - secondHeaderRowHeight, MasterplanDiagram.REL_TYPE_PREFIX + masterplanDiagram.getLevel1Header(),
          VALUE_TEXTSTYLE_TITLE, BUILDING_BLOCK_WIDTH, ROW_HEIGHT, COLOR_GREY_LIGHT);
    }
    double thirdHeaderRowHeight = secondHeaderRowHeight;
    if (!masterplanDiagram.getLevel2Header().isEmpty()) {
      thirdHeaderRowHeight = thirdHeaderRowHeight + ROW_HEIGHT;
      createLabel(pointer.getX(), pointer.getY() - thirdHeaderRowHeight, MasterplanDiagram.REL_TYPE_PREFIX + MasterplanDiagram.REL_TYPE_PREFIX
          + masterplanDiagram.getLevel2Header(), VALUE_TEXTSTYLE_TITLE, BUILDING_BLOCK_WIDTH, ROW_HEIGHT, COLOR_WHITE);
    }

    pointer.incX(BUILDING_BLOCK_WIDTH);

    // Status header if selected
    if (!skipStatusColumn) {
      createLabel(pointer.getX(), pointer.getY(), masterplanDiagram.getStatusHeader(), VALUE_TEXTSTYLE_TITLE, DATE_AND_STATUS_WIDTH, ROW_HEIGHT,
          COLOR_GREY_DARK);
      if (masterplanOptions.getLevel1Options() != null) {
        createLabel(pointer.getX(), pointer.getY() - secondHeaderRowHeight, "", VALUE_TEXTSTYLE_TITLE, DATE_AND_STATUS_WIDTH, ROW_HEIGHT,
            COLOR_GREY_LIGHT);
      }
      if (masterplanOptions.getLevel2Options() != null) {
        createLabel(pointer.getX(), pointer.getY() - thirdHeaderRowHeight, "", VALUE_TEXTSTYLE_TITLE, DATE_AND_STATUS_WIDTH, ROW_HEIGHT, COLOR_WHITE);
      }
      pointer.incX(DATE_AND_STATUS_WIDTH);
    }
    else {
      contentBaseX = contentBaseX - DATE_AND_STATUS_WIDTH;
    }

    // Create the custom labels
    for (ColumnEntry customColumn : masterplanDiagram.getCustomColumns()) {
      String customHeader = masterplanDiagram.getCustomColumnHeader(customColumn);
      String screenName = getScreenName(masterplanOptions, customHeader, null, "", customColumnWidths.get(customColumn).doubleValue(), 11, "");

      double colWidth = customColumnWidths.get(customColumn).doubleValue();
      createLabel(pointer.getX(), pointer.getY(), screenName, VALUE_TEXTSTYLE_TITLE, colWidth, ROW_HEIGHT, COLOR_GREY_DARK);

      if (masterplanOptions.getLevel1Options() != null) {
        createLabel(pointer.getX(), pointer.getY() - secondHeaderRowHeight, "", VALUE_TEXTSTYLE_TITLE, colWidth, ROW_HEIGHT, COLOR_GREY_LIGHT);
      }
      if (masterplanOptions.getLevel2Options() != null) {
        createLabel(pointer.getX(), pointer.getY() - thirdHeaderRowHeight, "", VALUE_TEXTSTYLE_TITLE, colWidth, ROW_HEIGHT, COLOR_WHITE);
      }
      pointer.incX(colWidth);
      contentBaseX += colWidth;
    }

    if (diagramHasTimelineFeatures()) {
      // Create start date header
      createLabel(pointer.getX(), pointer.getY(), masterplanDiagram.getBeginHeader(), VALUE_TEXTSTYLE_TITLE, DATE_AND_STATUS_WIDTH, ROW_HEIGHT,
          COLOR_GREY_DARK);
      if (masterplanOptions.getLevel1Options() != null) {
        createLabel(pointer.getX(), pointer.getY() - secondHeaderRowHeight, "", VALUE_TEXTSTYLE_TITLE, DATE_AND_STATUS_WIDTH, ROW_HEIGHT,
            COLOR_GREY_LIGHT);
      }
      if (masterplanOptions.getLevel2Options() != null) {
        createLabel(pointer.getX(), pointer.getY() - thirdHeaderRowHeight, "", VALUE_TEXTSTYLE_TITLE, DATE_AND_STATUS_WIDTH, ROW_HEIGHT, COLOR_WHITE);
      }
      pointer.incX(DATE_AND_STATUS_WIDTH);

      // Create end date header
      createLabel(pointer.getX(), pointer.getY(), masterplanDiagram.getEndHeader(), VALUE_TEXTSTYLE_TITLE, DATE_AND_STATUS_WIDTH, ROW_HEIGHT,
          COLOR_GREY_DARK);
      if (masterplanOptions.getLevel1Options() != null) {
        createLabel(pointer.getX(), pointer.getY() - secondHeaderRowHeight, "", VALUE_TEXTSTYLE_TITLE, DATE_AND_STATUS_WIDTH, ROW_HEIGHT,
            COLOR_GREY_LIGHT);
      }
      if (masterplanOptions.getLevel2Options() != null) {
        createLabel(pointer.getX(), pointer.getY() - thirdHeaderRowHeight, "", VALUE_TEXTSTYLE_TITLE, DATE_AND_STATUS_WIDTH, ROW_HEIGHT, COLOR_WHITE);
      }
      pointer.incX(DATE_AND_STATUS_WIDTH);
    }
    else {
      contentBaseX = contentBaseX - (2 * DATE_AND_STATUS_WIDTH);
    }

    createDatesAndLines(pointer);
  }

  /**
   * @param pointer 
   * @param relType 
   * @param headerRowCount 
   * @throws MasterNotFoundException 
   * 
   */
  private void createDatesAndLines(Coordinates pointer) throws MasterNotFoundException {
    double tempX = contentBaseX;
    double tempY = pointer.getY();
    double lineHeight = (masterplanDiagram.getLogicalHeight() - 1 + getHeaderRowHeight()) * ROW_HEIGHT;

    for (int yearCount = 0; yearCount < masterplanDiagram.getTimespan().size(); yearCount++) {

      MasterplanTimespanYear year = masterplanDiagram.getTimespan().get(yearCount);

      // Create year label
      int months = year.getMonths().size();
      double width = MONTH_WIDTH * months;
      // expand it a little bit if too short
      if (months < 2) {
        width += 0.1;
      }

      createLabel(tempX, tempY + TOP_ROW_HEIGHT_SMALL, year.getYearString(), VALUE_TEXTSTYLE_SMALL, width, TOP_ROW_HEIGHT_SMALL, COLOR_GREY_DARK);
      if (/*relType*/masterplanOptions.getLevel1Options() != null) {
        createLabel(tempX, tempY - ROW_HEIGHT, "", VALUE_TEXTSTYLE_NORMAL, width, ROW_HEIGHT, COLOR_GREY_LIGHT);
      }
      if (masterplanOptions.getLevel2Options() != null) {
        createLabel(tempX, tempY - (ROW_HEIGHT * 2), "", VALUE_TEXTSTYLE_NORMAL, width, ROW_HEIGHT, COLOR_WHITE);
      }

      // Create month labels
      for (int index = 0; index < year.getMonths().size(); index++) {

        createLabel(tempX + index * MONTH_WIDTH, tempY, year.getMonths().get(index), VALUE_TEXTSTYLE_SMALL, MONTH_WIDTH, TOP_ROW_HEIGHT_SMALL,
            COLOR_GREY_LIGHT);

        if (index > 0) {
          createLine(tempX + index * MONTH_WIDTH, tempY, tempX + index * MONTH_WIDTH, tempY - lineHeight, VALUE_LINESTYLE_DOTTEDFINE);
        }
      }

      tempX = tempX + year.getMonths().size() * MONTH_WIDTH;
      if (!(yearCount == masterplanDiagram.getTimespan().size() - 1)) {
        // Year dividing line
        createLine(tempX, tempY, tempX, tempY - lineHeight, VALUE_LINESTYLE_DOTTEDCOARSE);
      }
      else {
        // Closing border line
        createLine(tempX, tempY, tempX, tempY - lineHeight, VALUE_LINESTYLE_SOLID);
      }
    }
  }

  private int getHeaderRowHeight() {
    int headerRowCount = 1;
    if (!masterplanDiagram.getLevel1Header().isEmpty()) {
      headerRowCount = 2;
    }
    if (!masterplanDiagram.getLevel2Header().isEmpty()) {
      headerRowCount = 3;
    }
    return headerRowCount;
  }

  private double getHeaderRowYPos() {
    int headerRowCount = getHeaderRowHeight();
    return contentBaseY + (masterplanDiagram.getLogicalHeight() + headerRowCount) * ROW_HEIGHT;
  }

  private void createContentRowsAndHorizontalGrid() throws MasterNotFoundException /*throws SvgExportException*/{
    int timelineRowCount = 0;
    for (MasterplanBuildingBlockRow row : masterplanDiagram.getBuildingBlockRows()) {
      createBbRow(row, timelineRowCount);
      timelineRowCount += row.getLogicalHeight();
    }
  }

  private void createBbRow(MasterplanBuildingBlockRow row, int timelineRowCount) throws MasterNotFoundException /*throws SvgExportException*/{
    Coordinates pointer = new Coordinates(DISTANCE_TO_MARGIN, contentBaseY
        + (masterplanDiagram.getLogicalHeight() - timelineRowCount - row.getLogicalHeight() + 1) * ROW_HEIGHT);
    double bbLabelHeight = row.getLogicalHeight() * ROW_HEIGHT;

    // row color, name prefix
    Color rowColor = null;
    if (masterplanOptions.getLevel1Options() == null) {
      rowColor = COLOR_GREY_LIGHT;
    }
    else {
      rowColor = COLOR_GREY_DARK;
    }
    String namePrefix = "";
    if (row.getRowType().getLevel() == 1) {
      rowColor = COLOR_GREY_LIGHT;
      namePrefix = MasterplanDiagram.REL_TYPE_PREFIX;
    }
    else if (row.getRowType().getLevel() == 2) {
      rowColor = COLOR_WHITE;
      namePrefix = MasterplanDiagram.REL_TYPE_PREFIX + MasterplanDiagram.REL_TYPE_PREFIX;
    }

    // Create name field
    String screenName = getScreenName(masterplanOptions, row.getRowName(), null, "", InchConverter.cmToInches(BUILDING_BLOCK_WIDTH), 8, "");
    createLabel(pointer.getX(), pointer.getY(), namePrefix + screenName, VALUE_TEXTSTYLE_NORMAL, BUILDING_BLOCK_WIDTH, bbLabelHeight, rowColor);
    pointer.incX(BUILDING_BLOCK_WIDTH);

    // Status header if selected
    if (!skipStatusColumn) {
      createLabel(pointer.getX(), pointer.getY(), row.getStatusValue(), VALUE_TEXTSTYLE_NORMAL, DATE_AND_STATUS_WIDTH, bbLabelHeight, rowColor);
      pointer.incX(DATE_AND_STATUS_WIDTH);
    }

    // Append custom columns' content {
    for (ColumnEntry customColumn : masterplanDiagram.getCustomColumns()) {
      String customContent = row.getCustomColumnValues().get(customColumn);
      double colWidth = customColumnWidths.get(customColumn).doubleValue();
      if (customContent == null) {
        screenName = "";
      }
      else {
        screenName = getScreenName(masterplanOptions, customContent, null, "", InchConverter.cmToInches(colWidth), 8, "");
      }
      createLabel(pointer.getX(), pointer.getY(), screenName, VALUE_TEXTSTYLE_NORMAL, colWidth, bbLabelHeight, rowColor);
      pointer.incX(colWidth);
    }

    // timeline rows
    if (row.getTimespanRows().size() == 0) {
      createTimelineStartAndEndDates(pointer, MasterplanDiagram.DATE_UNSPECIFIED, MasterplanDiagram.DATE_UNSPECIFIED, rowColor);
    }
    else {
      int offset = 1;
      for (MasterplanTimespanRow timelineRow : row.getTimespanRows()) {

        double yRow = pointer.getY() + ROW_HEIGHT * (row.getTimespanRows().size() - offset);
        createTimelineRow(row, timelineRow, new Coordinates(pointer.getX(), yRow), rowColor);

        // Create the horizontal lines
        if (offset < row.getTimespanRows().size()) {
          createLine(contentBaseX, yRow, contentBaseX + MONTH_WIDTH * masterplanDiagram.getTimenspanLength(), yRow, VALUE_LINESTYLE_DOTTEDFINE);
        }

        offset++;
      }
    }

    // solid horizontal lines
    createLine(contentBaseX, pointer.getY(), contentBaseX + MONTH_WIDTH * masterplanDiagram.getTimenspanLength(), pointer.getY(),
        VALUE_LINESTYLE_SOLID);
  }

  private void createTimelineStartAndEndDates(Coordinates pointer, String fromDate, String toDate, Color rowColor) throws MasterNotFoundException {
    if (diagramHasTimelineFeatures()) {
      // Start date title
      createLabel(pointer.getX(), pointer.getY(), fromDate, VALUE_TEXTSTYLE_NORMAL, DATE_AND_STATUS_WIDTH, ROW_HEIGHT, rowColor);
      pointer.incX(DATE_AND_STATUS_WIDTH);

      // End date title
      createLabel(pointer.getX(), pointer.getY(), toDate, VALUE_TEXTSTYLE_NORMAL, DATE_AND_STATUS_WIDTH, ROW_HEIGHT, rowColor);
      pointer.incX(DATE_AND_STATUS_WIDTH);
    }
  }

  private void createTimelineRow(MasterplanBuildingBlockRow bbRow, MasterplanTimespanRow timelineRow, Coordinates pointer, Color rowColor)
      throws MasterNotFoundException {

    createTimelineStartAndEndDates(pointer, timelineRow.getFromDate(), timelineRow.getToDate(), rowColor);

    // Create bar
    if (!timelineRow.isOutOfTimespan()) {

      double timeStartX = contentBaseX + MONTH_WIDTH * masterplanDiagram.getTimenspanLength() * timelineRow.getRowFieldStart();
      double timeLength = MONTH_WIDTH * masterplanDiagram.getTimenspanLength() * timelineRow.getRowFieldLength();

      Color color = Color.decode("#" + DEFAULT_COLOR);

      if (bbRow.getRowType().isUseDefaultColoring() && !timelineRow.getTimelineFeature().isRuntimePeriod()) {
        color = Color.decode(timelineRow.getTimelineFeature().getDefaultColorHex());
      }
      else if (bbRow.getRowType().isUseDefaultColoring() && timelineRow.getTimelineFeature().isRuntimePeriod()) {
        color = this.colorDimensions.get(bbRow.getRowType()).getDefaultValue();
      }
      else {
        color = this.colorDimensions.get(bbRow.getRowType()).getValue(bbRow.getBuildingBlock());
      }

      createBar(timeStartX, pointer.getY() + BAR_OFFSET, timeLength, timelineRow.getTimespanCaption(), color);

    }
    else {
      // create a text label only
      String label = timelineRow.getTimespanCaption();
      Shape shape = this.getTargetPage().createNewShape(VISIO_SHAPE_NAME_TITLE);
      shape
          .setSize(label.length() * /*InchConverter.ptToInches(4, 72) / 1.75*/0.1, InchConverter.cmToInches(ROW_HEIGHT)/*InchConverter.ptToInches(4, 72)*/);
      shape.setPosition(InchConverter.cmToInches(pointer.getX()), InchConverter.cmToInches(pointer.getY()));
      shape.setFieldValue(label);
    }
  }

  /**
   * Create a bar.
   * 
   * @param xPosParam
   * @param yPosParam
   * @param lengthParam
   * @param planningState
   * @param rowName
   * @throws MasterNotFoundException
   */
  private void createBar(double xPosParam, double yPosParam, double lengthParam, String rowName, Color color) throws MasterNotFoundException {

    double length = InchConverter.cmToInches(lengthParam);

    Shape shape = this.getTargetPage().createNewShape(SHAPE_BAR);
    shape.setPosition(InchConverter.cmToInches(xPosParam), InchConverter.cmToInches(yPosParam));
    shape.setSize(length, InchConverter.cmToInches(BAR_HEIGHT));

    String screenName = getScreenName(masterplanOptions, rowName, null, "", length, 10, "");

    // Only add the text if it can fit into the shape (for example entries with a length of 1 day in
    // a timespan of 2 years are too short to be used as background for even as little text as the
    // number to identify them in the names legend.)
    if (length > VisioNamesLegend.getTextWidth(screenName.length(), 10, DEFAULT_SYSTEM_DPI)) {
      shape.setShapeText(screenName);
      shape.setCharSize(InchConverter.ptToInches(10, DEFAULT_SYSTEM_DPI));
    }
    else if (length > VisioNamesLegend.getTextWidth(screenName.length(), 7, DEFAULT_SYSTEM_DPI)) {
      shape.setShapeText(screenName);
      shape.setCharSize(InchConverter.ptToInches(7, DEFAULT_SYSTEM_DPI));
    }
    else {
      String superShortName = "";
      Pattern pattern = Pattern.compile("\\[[0-9]+]$");
      Matcher matcher = pattern.matcher(screenName);
      while (matcher.find()) {
        superShortName = matcher.group();
      }
      if (length > VisioNamesLegend.getTextWidth(superShortName.length(), 7, DEFAULT_SYSTEM_DPI)) {
        shape.setShapeText(superShortName);
      }
      else {
        shape.setShapeText("");
      }
      shape.setCharSize(InchConverter.ptToInches(7, DEFAULT_SYSTEM_DPI));
    }

    // set shape's geometry
    double geomX1 = 0;
    double geomY1 = 0;
    double geomX2 = length;
    double geomY2 = 0;
    double geomX3 = length;
    double geomY3 = InchConverter.cmToInches(BAR_HEIGHT);
    double geomX4 = 0;
    double geomY4 = InchConverter.cmToInches(BAR_HEIGHT);
    double geomX5 = 0;
    double geomY5 = 0;

    shape.setFirstVertexOfGeometry(geomX1, geomY1);
    shape.setLineEnd(2, geomX2, geomY2);
    shape.setLineEnd(3, geomX3, geomY3);
    shape.setLineEnd(4, geomX4, geomY4);
    shape.setLineEnd(5, geomX5, geomY5);

    shape.setFillForegroundColor(color);

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

    double myStartX = InchConverter.cmToInches(startX);
    double myStartY = InchConverter.cmToInches(startY);
    double myEndX = InchConverter.cmToInches(endX);
    double myEndY = InchConverter.cmToInches(endY);

    Shape shape = this.getTargetPage().createNewShape(SHAPE_LINE);
    shape.setBeginPosition(myStartX, myStartY);
    shape.setEndPosition(myEndX, myEndY);

    // set shape's geometry
    // note: the index parameter is known from the visio template.
    shape.setFirstVertexOfGeometry(myStartX, myStartY);
    shape.setLineEnd(2, myEndX, myEndY);

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

  /**
   * Create a label.
   * 
   * @param posX
   * @param posY
   * @param text
   * @param style
   * @param lengthParam
   * @throws MasterNotFoundException
   */
  private void createLabel(double posX, double posY, String text, String style, double lengthParam, double heightParam, Color fgColor)
      throws MasterNotFoundException {

    double length = InchConverter.cmToInches(lengthParam);

    Shape shape = null;
    if (VALUE_TEXTSTYLE_SMALL.equals(style)) {
      shape = this.getTargetPage().createNewShape(SHAPE_LABEL_SMALL);
    }
    else {
      shape = this.getTargetPage().createNewShape(SHAPE_LABEL);
    }
    shape.setPosition(InchConverter.cmToInches(posX), InchConverter.cmToInches(posY));
    shape.setSize(length, InchConverter.cmToInches(heightParam));
    shape.setFillForegroundColor(fgColor);
    // set shape's text
    shape.setShapeText(text);

    // format shape's text depending on the style
    if (VALUE_TEXTSTYLE_TITLE.equals(style)) {
      shape.setCharSize(InchConverter.ptToInches(10, 72));
      shape.setCharStyle(1);
    }
    else if (VALUE_TEXTSTYLE_SMALL.equals(style)) {
      shape.setCharSize(InchConverter.ptToInches(6, 72));
      shape.setCharStyle(0);
      shape.setParaIndFirst(InchConverter.mmToInches(0.8));
    }
    // style "normal"
    else {
      shape.setCharSize(InchConverter.ptToInches(8, 72));
      shape.setCharStyle(0);
    }
  }

  private Coordinates getColorLegendPosition() {

    double xPos = DISTANCE_TO_MARGIN;
    double yPos = contentBaseY - DISTANCE_TO_MARGIN;
    return new Coordinates(InchConverter.cmToInches(xPos), InchConverter.cmToInches(yPos));
  }

  private void estimateCustomColumnWidths() {
    if (masterplanDiagram.getCustomColumns().size() == 0) {
      return;
    }

    customColumnWidths = new HashMap<ColumnEntry, Double>();
    double currentWidth;

    for (ColumnEntry customColumn : masterplanDiagram.getCustomColumns()) {
      String header = masterplanDiagram.getCustomColumnHeader(customColumn);
      currentWidth = VisioNamesLegend.getTextWidth(header.length(), 10, DEFAULT_SYSTEM_DPI);

      for (MasterplanBuildingBlockRow row : masterplanDiagram.getBuildingBlockRows()) {
        String rowString = row.getCustomColumnValues().get(customColumn);
        if (rowString == null) {
          continue;
        }
        currentWidth = Math.max(currentWidth, VisioNamesLegend.getTextWidth(rowString.length(), 8, DEFAULT_SYSTEM_DPI));
      }
      if (InchConverter.inchesToCm(currentWidth) > BUILDING_BLOCK_WIDTH) {
        customColumnWidths.put(customColumn, Double.valueOf(BUILDING_BLOCK_WIDTH));
      }
      else {
        customColumnWidths.put(customColumn, Double.valueOf(InchConverter.inchesToCm(currentWidth) + 1));
      }
    }
  }
}

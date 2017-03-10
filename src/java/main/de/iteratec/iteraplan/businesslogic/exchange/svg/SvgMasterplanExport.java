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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.INamesLegend.LegendMode;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanBuildingBlockRow;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanCommon;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanTimespanRow;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanTimespanYear;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.IMasterplanOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.TimelineFeature;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.Shape;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.model.impl.AdvancedTextHelper;
import de.iteratec.svg.styling.SvgBaseStyling;


/**
 * This class is used to generate a Masterplan graphic in SVG format.
 */
public class SvgMasterplanExport extends SvgExport {

  private static final Logger                           LOGGER                 = Logger.getIteraplanLogger(SvgMasterplanExport.class);

  private static final String                           SVG_TEMPLATE_FILE      = "/SVGMasterplanTemplate.svg";

  private static final double                           MARGIN_PART            = 40;
  private static final double                           MARGIN_PAGE            = 120;
  private static final double                           LEGENDBOX_LABEL_HEIGHT = 52;
  private static final double                           LEGENDBOX_LABEL_WIDTH  = 240;
  private static final double                           CELL                   = 30;
  private static final double                           YEAR_HEIGHT            = 15;
  private static final double                           BAR_SPACER             = 5;
  private static final double                           ROW_HEIGHT             = 30;
  private static final double                           TITLE_LABEL_TEXT_PT    = 11;
  private static final double                           TITLE_LABEL_WIDTH      = 210;
  private static final double                           COLUMN_LABEL_WIDTH     = 90;

  private final double                                  labelSmallWidth;

  private double                                        legendBoxHeight        = 0;
  private double                                        tableHeaderWidth       = 0;

  private boolean                                       skipStatusColumn;

  private double                                        contentBaseX;
  private double                                        contentBaseY;

  private final IMasterplanOptions                      masterplanOptions;

  private MasterplanDiagram                             masterplanDiagram;
  private Map<ColumnEntry, Double>                      customColumnWidths;
  private Map<MasterplanRowTypeOptions, ColorDimension> colorDimensions;
  private Map<MasterplanRowTypeOptions, String>         rowColors;
  private Map<MasterplanRowTypeOptions, String>         rowCssColorClasses;

  public SvgMasterplanExport(MasterplanDiagram masterplanDiagram, IMasterplanOptions options, AttributeTypeService attributeTypeService,
      AttributeValueService attributeValueService) {
    super(attributeTypeService, attributeValueService);
    MasterplanCommon.validateOptions(options, getLocale());
    this.masterplanDiagram = masterplanDiagram;
    this.masterplanOptions = options;

    loadSvgDocumentFromTemplate(SVG_TEMPLATE_FILE, "Masterplan");
    labelSmallWidth = getSvgDocument().getMasterShapeProperties(MASTER_LABEL_SMALL).getShapeWidth();
  }

  /**
   * This is the main method which draws the diagram.
   * @throws IteraplanTechnicalException
   *           is thrown if some error occurs when drawing the diagram
   */
  public Document createDiagram() {
    if (masterplanOptions.isUseNamesLegend()) {
      setSvgNamesLegend(new SvgNamesLegend(getSvgDocument()));
      getSvgNamesLegend().setLegendMode(LegendMode.IN_PAGE);
    }

    try {
      LOGGER.debug("Generating an SVG masterplan diagram...");

      createColorDimensionsAndCssClasses();
      skipStatusColumn = masterplanDiagram.getStatusHeader().isEmpty();
      estimateCustomColumnWidths();

      double yPointer = MARGIN_TOP;
      if (!masterplanOptions.isNakedExport()) {
        yPointer += createDiagramTitle(getTitleString(), 0, 0, MARGIN_PAGE - 20);
      }

      yPointer = createQueryInfo(yPointer);

      createHeaderRowAndVerticalGrid(yPointer);
      createContentRowsAndHorizontalGrid();

      createColorLegends();

      if (masterplanOptions.isUseNamesLegend()) {
        createNamesLegend(MARGIN_PAGE + MARGIN_PART + tableHeaderWidth, yPointer, 0, 0, false, "");
      }

      setPageDimensions(yPointer);

      if (!masterplanOptions.isNakedExport()) {
        createGeneratedInformation(getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
        createLogos(0, 0, getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
      }

      setCustomSize(masterplanOptions.getWidth(), masterplanOptions.getHeight());

      getSvgDocument().finalizeDocument();

    } catch (SvgExportException ex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    }
    return getSvgDocument();
  }

  private void createColorDimensionsAndCssClasses() throws SvgExportException {
    //content elements (timelines) colors
    this.colorDimensions = Maps.newHashMap();
    MasterplanRowTypeOptions level0Opts = masterplanOptions.getLevel0Options();
    MasterplanRowTypeOptions level1Opts = masterplanOptions.getLevel1Options();
    MasterplanRowTypeOptions level2Opts = masterplanOptions.getLevel2Options();
    createColorDimensionForRowType(level0Opts);
    if (level1Opts != null) {
      createColorDimensionForRowType(level1Opts);
      if (level2Opts != null) {
        createColorDimensionForRowType(level2Opts);
      }
    }

    //row colors
    this.rowColors = Maps.newHashMap();
    this.rowCssColorClasses = Maps.newHashMap();
    if (level1Opts == null) {
      rowColors.put(level0Opts, COLOR_BEIGE_HEX);
      rowCssColorClasses.put(level0Opts, CSS_COLOR_BEIGE);
    }
    else {
      rowColors.put(level0Opts, COLOR_GRAY_HEX);
      rowCssColorClasses.put(level0Opts, CSS_COLOR_GRAY);
      rowColors.put(level1Opts, COLOR_BEIGE_HEX);
      rowCssColorClasses.put(level1Opts, CSS_COLOR_BEIGE);
      if (level2Opts != null) {
        rowColors.put(level2Opts, COLOR_WHITE_HEX);
        rowCssColorClasses.put(level2Opts, CSS_COLOR_WHITE);
      }
    }
  }

  private void createColorDimensionForRowType(MasterplanRowTypeOptions rowType) throws SvgExportException {
    if (rowType.isUseDefaultColoring()) {
      for (TimelineFeature timeline : rowType.getTimelineFeatures()) {
        generateCssColorClassForColorHex(timeline.getDefaultColorHex());
      }
    }
    //this should be initialized with -1 if default coloring is selected
    ColorDimension currentColorDimension = createColorDimension(rowType.getColorOptions(), rowType.getTypeOfBuildingBlock());
    this.colorDimensions.put(rowType, currentColorDimension);
    generateCssColorStyles(currentColorDimension);
  }

  private void estimateCustomColumnWidths() {
    if (masterplanDiagram.getCustomColumns().size() == 0) {
      return;
    }

    customColumnWidths = new HashMap<ColumnEntry, Double>();
    double currentWidth;

    for (ColumnEntry customColumn : masterplanDiagram.getCustomColumns()) {
      String header = masterplanDiagram.getCustomColumnHeader(customColumn);
      currentWidth = AdvancedTextHelper.getTextWidth(header.length(), 14, AdvancedTextHelper.POINT_TO_UNIT_CONSTANT) + 20;

      for (MasterplanBuildingBlockRow row : masterplanDiagram.getBuildingBlockRows()) {
        String rowString = row.getCustomColumnValues().get(customColumn);
        if (rowString == null) {
          continue;
        }
        currentWidth = Math
            .max(currentWidth, AdvancedTextHelper.getTextWidth(rowString.length(), 10, AdvancedTextHelper.POINT_TO_UNIT_CONSTANT) + 20);
      }
      if (currentWidth > TITLE_LABEL_WIDTH) {
        currentWidth = TITLE_LABEL_WIDTH;
      }
      customColumnWidths.put(customColumn, Double.valueOf(currentWidth));
    }
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

  private double createHeaderRowAndVerticalGrid(double yPointer) throws SvgExportException {
    // Create content name column header
    Coordinates pointer = new Coordinates(MARGIN_PAGE, yPointer);
    double secondHeaderRowHeight = 0;
    Shape label = createCustomRectangle(MASTER_LABEL_LARGE, pointer, masterplanDiagram.getLevel0Header(), COLOR_GRAY_HEX, TITLE_LABEL_WIDTH, 11,
        true, false);
    if (!masterplanDiagram.getLevel1Header().isEmpty()) {
      secondHeaderRowHeight = ROW_HEIGHT;
      label = createCustomRectangle(MASTER_LABEL_LARGE, new Coordinates(pointer.getX(), pointer.getY() + secondHeaderRowHeight),
          MasterplanDiagram.REL_TYPE_PREFIX + masterplanDiagram.getLevel1Header(), rowColors.get(masterplanOptions.getLevel1Options()),
          TITLE_LABEL_WIDTH, 11, true, false);
    }
    double thirdHeaderRowHeight = secondHeaderRowHeight;
    if (!masterplanDiagram.getLevel2Header().isEmpty()) {
      thirdHeaderRowHeight = thirdHeaderRowHeight + ROW_HEIGHT;
      label = createCustomRectangle(MASTER_LABEL_LARGE, new Coordinates(pointer.getX(), pointer.getY() + thirdHeaderRowHeight),
          MasterplanDiagram.REL_TYPE_PREFIX + MasterplanDiagram.REL_TYPE_PREFIX + masterplanDiagram.getLevel2Header(),
          rowColors.get(masterplanOptions.getLevel2Options()), TITLE_LABEL_WIDTH, 11, true, false);
    }

    pointer.incX(label.getWidth());
    tableHeaderWidth = TITLE_LABEL_WIDTH;

    // Create status header
    if (!skipStatusColumn) {
      if (masterplanOptions.getLevel1Options() != null) {
        createTextLabel(MASTER_LABEL_SMALL, new Coordinates(pointer.getX(), pointer.getY() + secondHeaderRowHeight), "", CSS_TEXT_11PT_BOLD,
            rowCssColorClasses.get(masterplanOptions.getLevel1Options()));
      }
      if (masterplanOptions.getLevel2Options() != null) {
        createTextLabel(MASTER_LABEL_SMALL, new Coordinates(pointer.getX(), pointer.getY() + thirdHeaderRowHeight), "", CSS_TEXT_11PT_BOLD,
            rowCssColorClasses.get(masterplanOptions.getLevel2Options()));
      }
      label = createTextLabel(MASTER_LABEL_SMALL, pointer, masterplanDiagram.getStatusHeader(), CSS_TEXT_11PT_BOLD, CSS_COLOR_GRAY);
      pointer.incX(label.getWidth());
      tableHeaderWidth = tableHeaderWidth + label.getWidth();
    }

    // Create the custom labels
    for (ColumnEntry customColumn : masterplanDiagram.getCustomColumns()) {
      String header = masterplanDiagram.getCustomColumnHeader(customColumn);
      String screenName = getScreenName(masterplanOptions, header, null, "", customColumnWidths.get(customColumn).doubleValue(), 11, "");

      //create the box for the second header row
      if (masterplanOptions.getLevel1Options() != null) {
        createCustomRectangle(MASTER_LABEL_LARGE, new Coordinates(pointer.getX(), pointer.getY() + secondHeaderRowHeight), "",
            rowColors.get(masterplanOptions.getLevel1Options()), TITLE_LABEL_WIDTH, 11, true, false);
      }
      if (masterplanOptions.getLevel2Options() != null) {
        createCustomRectangle(MASTER_LABEL_LARGE, new Coordinates(pointer.getX(), pointer.getY() + thirdHeaderRowHeight), "",
            rowColors.get(masterplanOptions.getLevel2Options()), TITLE_LABEL_WIDTH, 11, true, false);
      }
      //create the actual label
      label = createCustomRectangle(MASTER_LABEL_LARGE, pointer, screenName, COLOR_GRAY_HEX, customColumnWidths.get(customColumn).doubleValue(), 11,
          true, false);
      pointer.incX(label.getWidth());
      tableHeaderWidth = tableHeaderWidth + label.getWidth();
    }

    if (diagramHasTimelineFeatures()) {
      // Create start date header
      if (masterplanOptions.getLevel1Options() != null) {
        createTextLabel(MASTER_LABEL_SMALL, new Coordinates(pointer.getX(), pointer.getY() + secondHeaderRowHeight), "", CSS_TEXT_11PT_BOLD,
            rowCssColorClasses.get(masterplanOptions.getLevel1Options()));
      }
      if (masterplanOptions.getLevel2Options() != null) {
        createTextLabel(MASTER_LABEL_SMALL, new Coordinates(pointer.getX(), pointer.getY() + thirdHeaderRowHeight), "", CSS_TEXT_11PT_BOLD,
            rowCssColorClasses.get(masterplanOptions.getLevel2Options()));
      }
      label = createTextLabel(MASTER_LABEL_SMALL, pointer, masterplanDiagram.getBeginHeader(), CSS_TEXT_11PT_BOLD, CSS_COLOR_GRAY);
      pointer.incX(label.getWidth());
      tableHeaderWidth = tableHeaderWidth + label.getWidth();

      // Create end date header
      if (masterplanOptions.getLevel1Options() != null) {
        createTextLabel(MASTER_LABEL_SMALL, new Coordinates(pointer.getX(), pointer.getY() + secondHeaderRowHeight), "", CSS_TEXT_11PT_BOLD,
            rowCssColorClasses.get(masterplanOptions.getLevel1Options()));
      }
      if (masterplanOptions.getLevel2Options() != null) {
        createTextLabel(MASTER_LABEL_SMALL, new Coordinates(pointer.getX(), pointer.getY() + thirdHeaderRowHeight), "", CSS_TEXT_11PT_BOLD,
            rowCssColorClasses.get(masterplanOptions.getLevel2Options()));
      }
      label = createTextLabel(MASTER_LABEL_SMALL, pointer, masterplanDiagram.getEndHeader(), CSS_TEXT_11PT_BOLD, CSS_COLOR_GRAY);
      pointer.incX(label.getWidth());
      tableHeaderWidth = tableHeaderWidth + label.getWidth();
    }
    // Fix the content base coordinates
    contentBaseX = pointer.getX();
    contentBaseY = pointer.getY() + label.getHeight() + thirdHeaderRowHeight;

    createTimelineHeader(pointer, secondHeaderRowHeight, thirdHeaderRowHeight);

    return contentBaseY;
  }

  private void createTimelineHeader(Coordinates pointer, double bbHeaderRowsShift1, double bbHeaderRowsShift2) throws SvgExportException {
    // Create the year and month labels
    double tempX = contentBaseX;
    double tempY = pointer.getY();
    double lineHeight = masterplanDiagram.getLogicalHeight() * ROW_HEIGHT + bbHeaderRowsShift2;

    for (int yearCount = 0; yearCount < masterplanDiagram.getTimespan().size(); yearCount++) {

      MasterplanTimespanYear year = masterplanDiagram.getTimespan().get(yearCount);

      // Create year label
      double width = CELL * year.getMonths().size();
      if (masterplanOptions.getLevel1Options() != null) {
        createCustomRectangle(MASTER_LABEL_LARGE, new Coordinates(tempX, tempY + bbHeaderRowsShift1), "",
            rowColors.get(masterplanOptions.getLevel1Options()), width, 11, true, false);
      }
      if (masterplanOptions.getLevel2Options() != null) {
        createCustomRectangle(MASTER_LABEL_LARGE, new Coordinates(tempX, tempY + bbHeaderRowsShift2), "",
            rowColors.get(masterplanOptions.getLevel2Options()), width, 11, true, false);
      }
      createCustomRectangle(MASTER_YEAR_LABEL, new Coordinates(tempX, tempY), year.getYearString(), COLOR_GRAY_HEX, width, 7, true, false);

      // Create month labels
      for (int index = 0; index < year.getMonths().size(); index++) {
        createTextLabel(MASTER_MINI_LABEL, new Coordinates(tempX + index * CELL, tempY + YEAR_HEIGHT), year.getMonths().get(index), CSS_TEXT_7PT,
            CSS_COLOR_BEIGE);
        if (index > 0) {
          // Vertical grid line
          createVerticalLine(new Coordinates(tempX + index * CELL, contentBaseY - bbHeaderRowsShift2), lineHeight, CSS_LINE_DASHED_NORMAL);
        }
      }

      tempX = tempX + year.getMonths().size() * CELL;
      if (!(yearCount == masterplanDiagram.getTimespan().size() - 1)) {
        // Year dividing line
        createVerticalLine(new Coordinates(tempX, contentBaseY - bbHeaderRowsShift2), lineHeight, CSS_LINE_DASHED_15);
      }
      else {
        // Closing border line
        createVerticalLine(new Coordinates(tempX, contentBaseY - bbHeaderRowsShift2), lineHeight, CSS_LINE_SOLID);
      }
    }

    tableHeaderWidth = tableHeaderWidth + tempX - contentBaseX;
  }

  private void createContentRowsAndHorizontalGrid() throws SvgExportException {
    int timelineRowCount = 0;
    for (MasterplanBuildingBlockRow row : masterplanDiagram.getBuildingBlockRows()) {
      createBbRow(row, timelineRowCount);
      timelineRowCount = timelineRowCount + row.getLogicalHeight();
    }
  }

  private void createBbRow(MasterplanBuildingBlockRow row, int timelineRowCount) throws SvgExportException {
    Coordinates pointer = new Coordinates(MARGIN_PAGE, contentBaseY + timelineRowCount * ROW_HEIGHT);
    double bbLabelHeight = row.getLogicalHeight() * ROW_HEIGHT;
    String entityUrl = retrieveXLinkUrlForIdentityEntity(row.getBuildingBlock(), masterplanOptions.getServerUrl());

    String color = rowColors.get(row.getRowType());
    String colorClass = rowCssColorClasses.get(row.getRowType());
    List<String> colorStrings = Lists.newArrayList();
    colorStrings.add(color);

    // Create name field
    String screenName = getScreenName(masterplanOptions, row.getRowName(), null, "", TITLE_LABEL_WIDTH, TITLE_LABEL_TEXT_PT, entityUrl);
    Shape label = createCustomRectangle(MASTER_BAR, pointer, addLevelToScreenName(row.getRowType(), screenName), colorStrings, TITLE_LABEL_WIDTH,
        bbLabelHeight, TITLE_LABEL_TEXT_PT, false, false);
    label.setXLink(entityUrl);
    pointer.incX(label.getWidth());

    // Create status (if not project simple)
    if (!skipStatusColumn) {
      label = createCustomRectangle(MASTER_BAR, pointer, row.getStatusValue(), colorStrings, COLUMN_LABEL_WIDTH, bbLabelHeight, TITLE_LABEL_TEXT_PT,
          false, false);
      pointer.incX(label.getWidth());
    }
    //Append custom columns' content
    for (ColumnEntry customColumn : masterplanDiagram.getCustomColumns()) {
      String bbValue = row.getCustomColumnValues().get(customColumn);
      double columnWidth = customColumnWidths.get(customColumn).doubleValue();
      if (bbValue == null) {
        screenName = "";
      }
      else {
        screenName = getScreenName(masterplanOptions, bbValue, null, "", columnWidth, 9, "");
      }
      label = createCustomRectangle(MASTER_BAR, pointer, screenName, colorStrings, columnWidth, bbLabelHeight, TITLE_LABEL_TEXT_PT, false, false);
      pointer.incX(label.getWidth());
    }

    //timeline rows
    int timelinesCount = 1;
    if (row.getTimespanRows().size() == 0 && diagramHasTimelineFeatures()) {
      createTimelineStartAndEndDates(pointer, MasterplanDiagram.DATE_UNSPECIFIED, MasterplanDiagram.DATE_UNSPECIFIED, colorClass);
    }
    else {
      for (MasterplanTimespanRow timelineRow : row.getTimespanRows()) {
        createTimelineRow(row, timelineRow, new Coordinates(pointer.getX(), pointer.getY() + ROW_HEIGHT * (timelinesCount - 1)), colorClass,
            entityUrl);
        if (timelinesCount < row.getTimespanRows().size()) {
          createHorizontalLine(new Coordinates(contentBaseX, pointer.getY() + timelinesCount * ROW_HEIGHT),
              CELL * masterplanDiagram.getTimenspanLength(), CSS_LINE_DASHED_NORMAL);
        }
        timelinesCount++;
      }
    }

    createHorizontalLine(new Coordinates(contentBaseX, pointer.getY() + row.getLogicalHeight() * ROW_HEIGHT),
        CELL * masterplanDiagram.getTimenspanLength(), CSS_LINE_SOLID);
  }

  private void createTimelineStartAndEndDates(Coordinates pointer, String fromDate, String toDate, String colorClass) throws SvgExportException {
    // Create start date
    Shape label = createTextLabel(MASTER_LABEL_SMALL, pointer, fromDate, CSS_TEXT_9PT, colorClass);
    pointer.incX(label.getWidth());

    // Create end date
    label = createTextLabel(MASTER_LABEL_SMALL, pointer, toDate, CSS_TEXT_9PT, colorClass);
    pointer.incX(label.getWidth());
  }

  private void createTimelineRow(MasterplanBuildingBlockRow bbRow, MasterplanTimespanRow timelineRow, Coordinates pointer, String colorClass,
                                 String entityUrl) throws SvgExportException {

    if (diagramHasTimelineFeatures()) {
      createTimelineStartAndEndDates(pointer, timelineRow.getFromDate(), timelineRow.getToDate(), colorClass);
    }

    // Create bar
    if (!timelineRow.isOutOfTimespan()) {
      createTimeBar(bbRow, timelineRow, pointer.getY() + BAR_SPACER, entityUrl);
    }
    else {
      //create a text label only
      Shape shape = createNewShape(MASTER_TITLE);
      shape.setPosition(contentBaseX, pointer.getY() + ROW_HEIGHT - 2 * BAR_SPACER);
      shape.setTextCSSEnabled(false);
      shape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FILL_COLOR, "000000");
      shape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FILL_OPACITY, "1");
      shape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_FAMILY, "Arial");
      shape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_WEIGHT, "normal");
      shape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_ALIGN, "start");
      shape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_SIZE, String.valueOf(TITLE_LABEL_TEXT_PT));
      shape.setTextFieldValue(timelineRow.getTimespanCaption());
    }
  }

  private void createTimeBar(MasterplanBuildingBlockRow bbRow, MasterplanTimespanRow timespanRow, double baseY, String barUrl)
      throws SvgExportException {
    double timeStartX = contentBaseX + CELL * masterplanDiagram.getTimenspanLength() * timespanRow.getRowFieldStart();
    double timeLength = CELL * masterplanDiagram.getTimenspanLength() * timespanRow.getRowFieldLength();

    List<String> barColorStrings = CollectionUtils.arrayList();
    if (bbRow.getRowType().isUseDefaultColoring() && !timespanRow.getTimelineFeature().isRuntimePeriod()) {
      barColorStrings.add(timespanRow.getTimelineFeature().getDefaultColorHex());
    }
    else if (bbRow.getRowType().isUseDefaultColoring() && timespanRow.getTimelineFeature().isRuntimePeriod()) {
      barColorStrings.add((getColorStr(this.colorDimensions.get(bbRow.getRowType()).getDefaultValue())));
    }
    else {
      barColorStrings = getColorStrings(this.colorDimensions.get(bbRow.getRowType()).getMultipleValues(bbRow.getBuildingBlock()));
    }

    String screenName = getScreenName(masterplanOptions, timespanRow.getTimespanCaption(), null, "", timeLength, TITLE_LABEL_TEXT_PT, barUrl);

    double charSize = Math.min(TITLE_LABEL_TEXT_PT, timeLength / screenName.length());

    Shape bar = createCustomRectangle(MASTER_BAR, new Coordinates(timeStartX, baseY), screenName, barColorStrings, timeLength, charSize, false, true);
    bar.setXLink(barUrl);
  }

  private void createColorLegends() throws SvgExportException {
    if (masterplanOptions.isLegend()) {
      LOGGER.info("Drawing color legends on an SVG masterplan diagram");

      int offset = 0;
      offset = createColorLegend(masterplanOptions.getLevel0Options(), offset);
      offset = createColorLegend(masterplanOptions.getLevel1Options(), offset);
      createColorLegend(masterplanOptions.getLevel2Options(), offset);
    }
  }

  private int createColorLegend(MasterplanRowTypeOptions rowType, int offset) throws SvgExportException {
    if (rowType == null || rowType.getColorOptions().getDimensionAttributeId().intValue() == -1) {
      return offset;
    }

    this.legendBoxHeight = Math.max(legendBoxHeight, (colorDimensions.get(rowType).getValues().size() + 2) * LEGENDBOX_LABEL_HEIGHT);

    String header = masterplanDiagram.getHeader(rowType.getLevel()) + " - " + getFieldValueFromDimension(colorDimensions.get(rowType));
    String headerUrl = null;
    if (rowType.getColorOptions().getDimensionAttributeId().intValue() > 0) {
      AttributeType type = getAttributeTypeService().loadObjectById(rowType.getColorOptions().getDimensionAttributeId());
      headerUrl = retrieveXLinkUrlForIdentityEntity(type, masterplanOptions.getServerUrl());
    }
    createColorLegend(colorDimensions.get(rowType), new Coordinates(MARGIN_PAGE + offset * (LEGENDBOX_LABEL_WIDTH + MARGIN_PART), contentBaseY
        + masterplanDiagram.getLogicalHeight() * ROW_HEIGHT + MARGIN_PART), MASTER_COLOR_LEGEND_FIELD, header, headerUrl);

    return offset + 1;
  }

  private void setPageDimensions(double yPointer) {
    double namesLegendHeight = 0;
    if (masterplanOptions.isUseNamesLegend()) {
      namesLegendHeight = getSvgNamesLegend().getNamesLegendHeight();
    }
    double pageHeight = yPointer + MARGIN_PAGE + CELL * Math.max(masterplanDiagram.getLogicalHeight() + 1, namesLegendHeight);
    double pageWidth = 2 * MARGIN_PAGE + MARGIN_PART + tableHeaderWidth;

    if (masterplanOptions.isLegend()) {
      pageHeight = pageHeight + legendBoxHeight;
    }
    if (masterplanOptions.isUseNamesLegend()) {
      pageWidth = pageWidth + getSvgNamesLegend().getLegendWidth();
    }

    getSvgDocument().setPageSize(pageWidth, pageHeight);
  }

  private double createQueryInfo(double yPointer) throws SvgExportException {
    double y = yPointer;
    if (masterplanOptions.isShowSavedQueryInfo()) {
      Coordinates pos = new Coordinates(MARGIN_PAGE, yPointer);
      double tableWidth = calculateTableWidth();
      y = createSavedQueryInfo(pos, tableWidth, masterplanOptions.getSavedQueryInfo(), masterplanOptions.getServerUrl());
    }
    return y;
  }

  private static String addLevelToScreenName(MasterplanRowTypeOptions rowType, String screenName) {
    if (rowType.getLevel() == 0) {
      return screenName;
    }
    else if (rowType.getLevel() == 1) {
      return MasterplanDiagram.REL_TYPE_PREFIX + screenName;
    }
    return MasterplanDiagram.REL_TYPE_PREFIX + MasterplanDiagram.REL_TYPE_PREFIX + screenName;
  }

  private double calculateTableWidth() {
    double width = 0;
    if (customColumnWidths != null) {
      for (Double val : customColumnWidths.values()) {
        width = width + val.doubleValue();
      }
    }
    if (!skipStatusColumn) {
      width = width + labelSmallWidth;
    }
    // name, begin and end columns
    width += TITLE_LABEL_WIDTH + labelSmallWidth * 2;
    // time span
    width += CELL * masterplanDiagram.getTimenspanLength();

    return width;
  }
}

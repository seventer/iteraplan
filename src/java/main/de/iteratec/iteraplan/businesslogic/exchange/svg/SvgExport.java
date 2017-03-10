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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.exchange.BitmapCodeGenerator;
import de.iteratec.iteraplan.businesslogic.exchange.DimensionExport;
import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.AttributeRangeAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorRangeDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.LineDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.EmptyNamesLegend;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.INamesLegend;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.LogicalPage;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.IGraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.iteraplan.model.queries.SavedQueryEntityInfo;
import de.iteratec.svg.model.BasicShape;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.DocumentFactory;
import de.iteratec.svg.model.Shape;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.model.impl.AdvancedTextHelper;
import de.iteratec.svg.model.impl.CircleShape;
import de.iteratec.svg.model.impl.PathShape;
import de.iteratec.svg.model.impl.PatternShape;
import de.iteratec.svg.model.impl.RectangleShape;
import de.iteratec.svg.styling.SvgBaseStyling;
import de.iteratec.svg.styling.SvgCssStyling;


@SuppressWarnings("PMD.TooManyMethods")
public abstract class SvgExport extends DimensionExport {

  private Document                  svgDocument;

  private final IteraplanProperties properties                        = IteraplanProperties.getProperties();
  private SvgNamesLegend            svgNamesLegend;

  private static final Logger       LOGGER                            = Logger.getIteraplanLogger(SvgExport.class);

  /**
   * Predefined master shape names common for some of the graphics.
   */
  protected static final String     MASTER_LEGEND_BOX                 = "LegendBoxRoot";
  protected static final String     MASTER_COLOR_LEGEND_FIELD         = "ColorLegendFieldRoot";
  protected static final String     MASTER_TITLE                      = "TitleRoot";
  protected static final String     MASTER_LOGO_UPPER_RIGHT           = "Logo-UpperRightCornerRoot";
  protected static final String     MASTER_LOGO_LOWER_LEFT            = "Logo-LowerLeftCornerRoot";
  protected static final String     MASTER_LABEL_LARGE                = "LabelLargeRoot";
  protected static final String     MASTER_LABEL_SMALL                = "LabelSmallRoot";
  protected static final String     MASTER_IMAGE                      = "ImageRoot";
  protected static final String     MASTER_MINI_LABEL                 = "MiniLabelRoot";
  protected static final String     MASTER_YEAR_LABEL                 = "YearLabelRoot";
  protected static final String     MASTER_BAR                        = "BarRoot";

  protected static final double     LEGEND_BOX_HEIGHT                 = 52;

  /**
   * Predefined css class names used in more than one graphic.
   */
  protected static final String     CSS_TITLE                         = "title";

  protected static final String     CSS_LEGEND_HEADER                 = "legendHeaderStyle";
  protected static final String     CSS_LEGEND_CONTENT                = "legendContentStyle";
  protected static final String     CSS_LEGEND_HEADER_TEXT            = "legendHeaderText";
  protected static final String     CSS_LEGEND_CONTENT_TEXT           = "legendContentText";

  protected static final String     CSS_LINE_SOLID                    = "solidLine";
  protected static final String     CSS_LINE_DASHED_NORMAL            = "dashedLine";
  protected static final String     CSS_LINE_DASHED_15                = "longDashedLine";
  protected static final String     CSS_TEXT_NORMAL                   = "normalText";
  protected static final String     CSS_TEXT_CENTER_11PT              = "centerText";
  protected static final String     CSS_TEXT_CENTER_8PT               = "centerLittleText";
  protected static final String     CSS_TEXT_11PT_BOLD                = "boldText";
  protected static final String     CSS_TEXT_9PT                      = "dateText";
  protected static final String     CSS_TEXT_7PT                      = "miniText";
  protected static final String     CSS_GENERATED_INFORMATION         = "generatedInformation";

  protected static final String     CSS_COLOR_GRAY                    = "colorGray";
  protected static final String     CSS_COLOR_BEIGE                   = "colorBeige";
  protected static final String     CSS_COLOR_WHITE                   = "colorWhite";

  protected static final String     COLOR_GRAY_HEX                    = "d3cfd1";
  protected static final String     COLOR_BEIGE_HEX                   = "e8e6d3";
  protected static final String     COLOR_BLACK_HEX                   = "000000";
  protected static final String     COLOR_WHITE_HEX                   = "ffffff";

  protected static final String     FONT_WEIGHT_BOLD                  = "bold";
  protected static final String     FONT_ALIGN_MIDDLE                 = "middle";

  protected static final double     EDGE_ROUNDING                     = 10;
  protected static final double     MARGIN                            = 40;
  protected static final double     MARGIN_TOP                        = 2 * MARGIN;
  protected static final double     LINE_PADDING                      = 20;
  protected static final double     LINE_WIDTH                        = 40;

  private static final double       COLOR_LEGEND_COLOR_FIELD_DIAMETER = 26;

  //  private double                    titleHeight                       = 1;

  private final Map<String, String> colorToColorClassMap              = new HashMap<String, String>();
  private Map<String, String>       lineTypeToCSSStyleMap;

  public SvgExport(AttributeTypeService attributeTypeService, AttributeValueService attributeValueService) {
    super(attributeTypeService, attributeValueService);
  }

  public abstract Document createDiagram();

  /**
   * Loads the content of the template into the svg document. In case that the method has been
   * successful, the variable svgDocument stores the resulting document.
   *
   * @param svgTemplateFile
   *          The template file to load. Should not be null.
   * @param savedQueryType
   *          Textual representation of the diagram type (e.g. portfolio, cluster). Used for logging
   *          purposes.
   */
  protected void loadSvgDocumentFromTemplate(String svgTemplateFile, String savedQueryType) {

    // pre logging
    LOGGER.debug("entering SVG {0} init...", savedQueryType);

    // surrounding try/catch for IOException (because of opening/closing stream)
    try {
      InputStream templateStream = null;

      // nested try/catch for all other exceptions
      try {
        templateStream = SvgExport.class.getResourceAsStream(svgTemplateFile);
        this.svgDocument = DocumentFactory.getInstance().loadDocument(templateStream);
      } catch (ParserConfigurationException e) {
        LOGGER.error("ParserConfigurationException while trying to open the SVG template for {0} :\n {1}", savedQueryType, e.getMessage());
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
      } catch (SAXException e) {
        LOGGER.error("SAXException while trying to open the SVG template for {0} :\n {1}", savedQueryType, e.getMessage());
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
      } catch (SvgExportException e) {
        LOGGER.error("SVGException while trying to open the SVG template for {0} :\n {1}", savedQueryType, e.getMessage());
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
      } finally {
        // try to close stream. IOException may still occur here.
        if (templateStream != null) {
          templateStream.close();
        }
      }

      // surrounding catch of IO exception
    } catch (IOException e) {
      LOGGER.error("IOException while trying to open the SVG template for {0} :\n {1}", savedQueryType, e.getMessage());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }

    // post logging
    LOGGER.debug("leaving SVG {0} init...", savedQueryType);

  }

  /**
   * Creates a basic text legend. The css properties of the legend boxes and texts are not to be
   * edited.
   *
   * @param masterName
   * @param boxSize
   * @param position
   * @param values
   * @param headerEnabled
   * @param headerString
   * @param textShift
   * @param urls
   *          The list with urls to be added to the shapes. Can be null.
   * @param headerUrl
   *          The url of the header shape. Can be null.
   * @return A list with the generated legend boxes.
   * @throws SvgExportException
   */
  protected List<BasicShape> createBaseLegend(String masterName, Coordinates boxSize, Coordinates position, List<String> values,
                                              boolean headerEnabled, String headerString, Coordinates textShift, List<String> urls, String headerUrl)
      throws SvgExportException {

    boolean basicShapeLegend = false;
    if (masterName == null || masterName.equals(BasicShape.SVG_BASIC_SHAPES.PATH) || masterName.equals(BasicShape.SVG_BASIC_SHAPES.TEXT)) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    if (masterName.equals(BasicShape.SVG_BASIC_SHAPES.CIRCLE) || masterName.equals(BasicShape.SVG_BASIC_SHAPES.RECTANGLE)) {
      basicShapeLegend = true;
    }

    List<BasicShape> generatedLegendEntries = new LinkedList<BasicShape>();
    Coordinates currentBoxPointer = new Coordinates(position.getX(), position.getY());

    // create header
    if (headerEnabled) {
      BasicShape header = createLegendHeader(basicShapeLegend, masterName, headerString, boxSize, currentBoxPointer);
      setEntryUrl(headerUrl, header);
      generatedLegendEntries.add(header);
    }

    // create legend entries
    int count = 0;
    for (String value : values) {
      BasicShape entry = createLegendEntry(basicShapeLegend, masterName, boxSize, textShift, currentBoxPointer, value);
      //Second check needed for the extra entry regarding the position in  a potfolio diagram where the description doesn't hav an url.
      if (urls != null && count < urls.size()) {
        setEntryUrl(urls.get(count), entry);
      }
      generatedLegendEntries.add(entry);
      count++;
    }
    return generatedLegendEntries;
  }

  private BasicShape createLegendHeader(boolean basicShapeLegend, String masterName, String headerString, Coordinates boxSize,
                                        Coordinates currentBoxPointer) throws SvgExportException {
    BasicShape header;
    if (!basicShapeLegend) {
      header = createNewShape(masterName);
      ((Shape) header).addTextCSSClass(CSS_LEGEND_HEADER_TEXT);
      ((Shape) header).setTextFieldValue(headerString);
      ((Shape) header).setLineSpacing(3);
    }
    else {
      header = createNewBasicShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
      header.setSize(boxSize.getX(), boxSize.getY());
      header.addCSSClass(CSS_LEGEND_HEADER);

      Shape text = header.createNewInnerShape(MASTER_LABEL_LARGE);
      text.addTextCSSClass(CSS_LEGEND_HEADER_TEXT);
      text.setTextFieldValue(headerString);
      text.setPosition(boxSize.getX() * 0.5, boxSize.getY() * 0.5);
      makeTextShapeAdjust(text, 11, boxSize.getX() * 1.2);
    }
    header.addCSSClass(CSS_LEGEND_HEADER);
    header.setPosition(currentBoxPointer.getX(), currentBoxPointer.getY());
    currentBoxPointer.incY(header.getHeight());
    return header;
  }

  private BasicShape createLegendEntry(boolean basicShapeLegend, String masterName, Coordinates boxSize, Coordinates textShift,
                                       Coordinates currentBoxPointer, String value) throws SvgExportException {
    BasicShape entry;
    if (!basicShapeLegend) {
      entry = createNewShape(masterName);
      ((Shape) entry).addTextCSSClass(CSS_LEGEND_CONTENT_TEXT);
      ((Shape) entry).setTextFieldValue(value);
      ((Shape) entry).setBoundTextInShape(true);
      ((Shape) entry).setLineSpacing(3);
    }
    else {
      entry = createNewBasicShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
      entry.setSize(boxSize.getX(), boxSize.getY());

      Shape text = entry.createNewInnerShape(MASTER_LABEL_LARGE);
      text.addTextCSSClass(CSS_LEGEND_CONTENT_TEXT);
      text.setTextFieldValue(value);
      text.setPosition(textShift.getX(), textShift.getY());
      makeTextShapeAdjust(text, 11, boxSize.getX() * 1.2);
    }
    entry.addCSSClass(CSS_LEGEND_CONTENT);
    entry.setPosition(currentBoxPointer.getX(), currentBoxPointer.getY());
    currentBoxPointer.incY(entry.getHeight());
    return entry;
  }

  private void setEntryUrl(String url, BasicShape entry) {
    if (url != null && !url.matches("\\s*")) {
      entry.setXLink(url);
    }
  }

  /**
   * Generates CSS styles.
   *
   * @param colorDim
   *          The dimension holding the color configuration.
   * @throws SvgExportException
   *           If the CSS class generation fails.
   */
  protected void generateCssColorStyles(ColorDimension colorDim) throws SvgExportException {

    LOGGER.info("Generating CSS color styles for SVG Export...");

    createColorClassesFromDimension(colorDim);

    // Add the unspecified value
    generateCssColorClassForColorHex(getColorStr(colorDim.getDefaultValue()));
  }

  /**
   * Generates a css color class in the current document for the
   * provided hexadecimal color value. The value should be provided without
   * the # symbol.
   * @param colorHex
   *    The hexadecimal color for which a css color class is to be created.
   * @throws SvgExportException
   */
  protected void generateCssColorClassForColorHex(String colorHex) throws SvgExportException {
    String colorClassName = "ColorClass" + colorHex;
    Map<String, String> cssClassProperties = Maps.newHashMap();

    if (!getColorToColorClassMap().containsKey(colorHex)) {
      cssClassProperties.put(SvgBaseStyling.FILL_COLOR, "#" + colorHex);
      getColorToColorClassMap().put(colorHex, colorClassName);
      svgDocument.createNewCSSClass(colorClassName, cssClassProperties);
    }
  }

  /**
   * Retrieve the colors of the dimension and create the color classes
   * @param colorDim
   *          Color-Dimension holding information about the colors to use
   * @throws SvgExportException
   */
  private void createColorClassesFromDimension(ColorDimension colorDim) throws SvgExportException {

    final Set<String> colorNames = colorDim.getMapping().keySet();
    for (String colorName : colorNames) {
      final Map<String, String> classProperties = CollectionUtils.hashMap();
      final String colorStr = getColorStr(colorDim.getValue(colorName));

      String colorClassName = "ColorClass" + colorStr;

      if (!getColorToColorClassMap().containsKey(colorStr)) {
        getColorToColorClassMap().put(colorStr, colorClassName);
        classProperties.put(SvgBaseStyling.FILL_COLOR, "#" + colorStr);
        svgDocument.createNewCSSClass(colorClassName, classProperties);
      }

    }
  }

  protected void generateCssLineStyles(LineDimension lineDimension) throws SvgExportException {

    lineTypeToCSSStyleMap = new HashMap<String, String>();
    Map<String, String> classProperties;
    Integer line;
    String lineClassName;
    String linePatternValue;
    int count = 0;

    // Create the classes for the line types
    for (String lineId : lineDimension.getValues()) {
      classProperties = new HashMap<String, String>();
      line = lineDimension.getValue(lineId);
      linePatternValue = getSvgLinePattern(line.intValue());
      classProperties.put(SvgBaseStyling.STROKE_LINE_PATTERN, linePatternValue);
      classProperties.put(SvgBaseStyling.STROKE_WITDH, "1");
      classProperties.put(SvgBaseStyling.STROKE_COLOR, "#000000");
      lineClassName = "lineClass" + count;
      count++;

      getLineTypeToCSSStyleMap().put(String.valueOf(line), lineClassName);

      svgDocument.createNewCSSClass(lineClassName, classProperties);
    }

    // Create the default value
    classProperties = new HashMap<String, String>();
    line = lineDimension.getDefaultValue();
    if (line.intValue() == 0) {
      linePatternValue = "none";
    }
    else {
      linePatternValue = String.valueOf(line.intValue() * 4);
    }
    classProperties.put(SvgBaseStyling.STROKE_LINE_PATTERN, linePatternValue);
    classProperties.put(SvgBaseStyling.STROKE_WITDH, "1");
    classProperties.put(SvgBaseStyling.STROKE_COLOR, "#000000");
    lineClassName = "lineClassDefault";

    getLineTypeToCSSStyleMap().put(String.valueOf(line), lineClassName);

    svgDocument.createNewCSSClass(lineClassName, classProperties);
  }

  /**
   * Creates a color legend with the specified parameters.
   *
   * @param colorDim
   *          ColorDimension containing information about the legend's entries
   * @param position
   *          The coorinates of the legend.
   * @param colorShapeMasterName
   *          The master name of the shape to be used for the color fields.
   * @param headline
   *          The title of the legend.
   * @param headerUrl
   *          An optional URL to which the legend header can link.
   * @return The number of entries in the legend, includuing the header and unspecified value if
   *         those exist. Can be used to determine the height of the legend.
   * @throws SvgExportException
   */
  protected int createColorLegend(ColorDimension colorDim, Coordinates position, String colorShapeMasterName, String headline, String headerUrl)
      throws SvgExportException {
    return createColorLegend(colorDim, position, colorShapeMasterName, headline, true, headerUrl);
  }

  /**
   * Creates a color legend with the specified parameters.
   *
   * @param colorDim
   *          The {@link ColorDimension} holding the color data.
   * @param position
   *          The coordinates of the legend.
   * @param colorShapeMasterName
   *          The name of the master shape to be used for the color field.
   * @param headline
   *          The text to be sued as a legend header.
   * @param headerEnabled
   *          Whether the legend should have a header.
   * @param headerUrl
   *          The optional URL to which the header can link.
   * @return The number of entries in the legend, includuing the header and unspecified value if
   *         those exist. Can be used to determine the height of the legend.
   * @throws SvgExportException
   */
  protected int createColorLegend(ColorDimension colorDim, Coordinates position, String colorShapeMasterName, String headline, boolean headerEnabled,
                                  String headerUrl) throws SvgExportException {
    final List<String> valuesT = colorDim.getValues();
    final List<String> values = new LinkedList<String>();
    final List<String> colors = new LinkedList<String>();

    for (String val : valuesT) {
      colors.add(getColorStr(colorDim.getValue(val)));
      values.add(val);
    }

    if (colorDim instanceof ColorRangeDimension) {
      modifyValues((ColorRangeDimension) colorDim, values);
    }

    if (colorDim.hasUnspecificValue()) {
      values.add(MessageAccess.getStringOrNull(DimensionOptionsBean.DEFAULT_VALUE, getLocale()));
      colors.add(getColorStr(colorDim.getDefaultValue()));
    }

    return createColorLegend(colorShapeMasterName, position, headline, values, colors, headerEnabled, headerUrl);
  }

  /**
   * Creates a color legend with the specified parameters.
   *
   * @param colorShapeMasterName
   *          The name of the master shape to be used for the color field.
   * @param position
   *          The coordinates of the legend.
   * @param headline
   *          The text to be used as a legend header.
   * @param values
   *          The text values to be put next to each color field.
   * @param colors
   *          The list of colors (in hexadecimal format) to be used for the color fields.
   * @param headerEnabled
   *          Whether the legend will have a header entry.
   * @param headerUrl
   *          The optional URL to which the legend header can link.
   * @return The number of entries in the legend, includuing the header and unspecified value if
   *         those exist. Can be used to determine the height of the legend.
   * @throws SvgExportException
   */
  protected int createColorLegend(String colorShapeMasterName, Coordinates position, String headline, List<String> values, List<String> colors,
                                  boolean headerEnabled, String headerUrl) throws SvgExportException {

    Coordinates boxSize = new Coordinates(0, 0);
    Coordinates textShift = new Coordinates(0.6, 0.6);
    final List<BasicShape> legendShapes = createBaseLegend(MASTER_LEGEND_BOX, boxSize, position, values, headerEnabled, headline, textShift, null,
        headerUrl);

    final int numberOfEntries = legendShapes.size();

    int count = 0;
    if (headerEnabled) {
      count = 1;
    }

    while (count < legendShapes.size()) {

      final BasicShape entry = legendShapes.get(count);

      final BasicShape colorField = entry.createNewInnerShape(colorShapeMasterName);
      colorField.setShapeCSSEnabled(true);
      if (headerEnabled) {
        colorField.addCSSClass(getColorToColorClassMap().get(colors.get(count - 1)));
      }
      else {
        colorField.addCSSClass(getColorToColorClassMap().get(colors.get(count)));
      }
      colorField.addCSSClass("colorFieldBaseStyle");
      colorField.setSize(COLOR_LEGEND_COLOR_FIELD_DIAMETER, COLOR_LEGEND_COLOR_FIELD_DIAMETER);
      colorField.setPosition(COLOR_LEGEND_COLOR_FIELD_DIAMETER / 2, COLOR_LEGEND_COLOR_FIELD_DIAMETER / 2);
      count++;
    }

    return numberOfEntries;
  }

  /**
   * This is used to draw a customized single shape on the document.
   *
   * @param masterShape
   *          the master-shape this shape is based on.
   * @param drawPointer
   *          the position of the shape.
   * @param text
   *          the text contained in the shape.
   * @param colorStrings
   *          a list with the hexadecimal colors for the possibly serifed coloring of the shape.
   * @param width
   *          the width of the shape.
   * @param charSize
   *          the size of the contained text.
   * @param bold
   *          set it true, if you want the text will be written with a bold style.
   * @param center
   *          set it true, if you want the text to be centered.
   * @param rounded
   *          set it true, if you want the rectangle's edges to be rounded.
   * @return the drawn shape.
   * @throws SvgExportException
   *           is thrown if some error occurs when drawing the shape.
   */

  protected Shape createCustomLabel(String masterShape, Coordinates drawPointer, String text, List<String> colorStrings, double width,
                                    double charSize, boolean bold, boolean center, boolean rounded) throws SvgExportException {
    final Shape label = createTextLabel(masterShape, drawPointer, text, null, null);
    label.setSize(width, label.getHeight());

    label.setResizeTextWithShape(false);
    label.setTextCSSEnabled(false);
    label.setTextPosition(label.getPinX() + (center ? (label.getWidth() / 2) : 4), label.getPinY() + (label.getHeight() + charSize) / 2);
    label.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_SIZE, Double.toString(charSize));
    if (bold) {
      label.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_WEIGHT, FONT_WEIGHT_BOLD);
    }
    if (center) {
      label.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_ALIGN, FONT_ALIGN_MIDDLE);
    }

    final RectangleShape background = createShapeBackgroundAndOutline(label, colorStrings, COLOR_BLACK_HEX);

    if (rounded) {
      background.setCornerRounding(EDGE_ROUNDING, EDGE_ROUNDING);
    }

    return label;
  }

  protected Shape createCustomLabel(String masterShape, Coordinates drawPointer, String text, List<String> colorStrings, double width, double height,
                                    double charSize, boolean bold, boolean center, boolean rounded) throws SvgExportException {
    final Shape label = createTextLabel(masterShape, drawPointer, text, null, null);
    label.setSize(width, height);

    label.setResizeTextWithShape(false);
    label.setTextCSSEnabled(false);
    label.setTextPosition(label.getPinX() + (center ? (label.getWidth() / 2) : 4), label.getPinY() + (label.getHeight() + charSize) / 2);
    label.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_SIZE, Double.toString(charSize));
    if (bold) {
      label.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_WEIGHT, FONT_WEIGHT_BOLD);
    }
    if (center) {
      label.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_ALIGN, FONT_ALIGN_MIDDLE);
    }

    final RectangleShape background = createShapeBackgroundAndOutline(label, colorStrings, COLOR_BLACK_HEX);

    if (rounded) {
      background.setCornerRounding(EDGE_ROUNDING, EDGE_ROUNDING);
    }

    return label;
  }

  /**
   * This is used to draw a single shape with a Rectangle form on the document.
   *
   * @param masterShape
   *          the master-shape this shape is based on.
   * @param drawPointer
   *          the position of the shape.
   * @param text
   *          the text contained in the shape.
   * @param colors
   *          the hexadecimal code of the shape's color.
   * @param width
   *          the width of the shape.
   * @param charSize
   *          the size of the contained text.
   * @param bold
   *          set it true, if you want the text will be written with a bold style.
   * @param center
   *          set it true, if you want the text to be centered.
   * @return the drawn shape.
   * @throws SvgExportException
   *           is thrown if some error occurs when drawing the shape.
   */

  protected Shape createCustomRectangle(String masterShape, Coordinates drawPointer, String text, List<String> colors, double width, double charSize,
                                        boolean bold, boolean center) throws SvgExportException {
    return createCustomLabel(masterShape, drawPointer, text, colors, width, charSize, bold, center, false);
  }

  protected Shape createCustomRectangle(String masterShape, Coordinates drawPointer, String text, List<String> colors, double width, double height,
                                        double charSize, boolean bold, boolean center) throws SvgExportException {
    return createCustomLabel(masterShape, drawPointer, text, colors, width, height, charSize, bold, center, false);
  }

  /**
   * This is used to draw a single shape with a Rectangle form on the document.
   *
   * @param masterShape
   *          the master-shape this shape is based on.
   * @param drawPointer
   *          the position of the shape.
   * @param text
   *          the text contained in the shape.
   * @param colorHex
   *          the hexadecimal code of the shape's color.
   * @param width
   *          the width of the shape.
   * @param charSize
   *          the size of the contained text.
   * @param bold
   *          set it true, if you want the text will be written with a bold style.
   * @param center
   *          set it true, if you want the text to be centered.
   * @return the drawn shape.
   * @throws SvgExportException
   *           is thrown if some error occurs when drawing the shape.
   */

  protected Shape createCustomRectangle(String masterShape, Coordinates drawPointer, String text, String colorHex, double width, double charSize,
                                        boolean bold, boolean center) throws SvgExportException {
    final List<String> colors = new LinkedList<String>();
    colors.add(colorHex);
    return createCustomLabel(masterShape, drawPointer, text, colors, width, charSize, bold, center, false);
  }

  /**
   * This is used to draw a single shape with an arrow form on the document.
   *
   * @param masterShape
   *          the master-shape this shape is based on.
   * @param drawPointer
   *          the position of the shape.
   * @param text
   *          the text contained in the shape.
   * @param colorStrings
   *          a list with the hexadecimal colors for the possibly serifed coloring of the shape.
   * @param width
   *          the width of the shape.
   * @param charSize
   *          the size of the contained text.
   * @param bold
   *          set it true, if you want the text will be written with a bold style.
   * @param center
   *          set it true, if you want the text to be centered.
   * @return the drawn shape.
   * @throws SvgExportException
   *           is thrown if some error occurs when drawing the shape.
   */
  protected Shape createProcessShape(String masterShape, Coordinates drawPointer, String text, List<String> colorStrings, double width,
                                     double charSize, boolean bold, boolean center) throws SvgExportException {
    final double arrowMarge = Math.min(width / 20, 50);
    final Shape label = createTextLabel(masterShape, drawPointer, text, null, null);
    label.setSize(width, label.getHeight());

    final PathShape path = (PathShape) label.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.PATH);
    path.setSize(width, label.getHeight());

    final double x = drawPointer.getX();
    final double y = drawPointer.getY();

    // Begin drawing the arrow...
    // TODO Pathshape should be use relative Coordinates!
    path.moveTo(x, y);
    path.lineTo(x + width - arrowMarge, y);
    path.lineTo(x + width, y + label.getHeight() / 2);
    path.lineTo(x + width - arrowMarge, y + label.getHeight());
    path.lineTo(x, y + label.getHeight());
    path.lineTo(x + arrowMarge, y + label.getHeight() / 2);
    path.setClosePath(true);

    label.setResizeTextWithShape(false);
    label.setTextCSSEnabled(false);
    label.setTextPosition(label.getPinX() + (center ? (label.getWidth() / 2) : 4), label.getPinY() + (label.getHeight() + charSize) / 2);
    label.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_SIZE, Double.toString(charSize));
    if (bold) {
      label.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_WEIGHT, FONT_WEIGHT_BOLD);
    }
    if (center) {
      label.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_ALIGN, FONT_ALIGN_MIDDLE);
    }

    final String patternUrl = createHorizontalLinesPattern(colorStrings, width, label.getHeight());

    path.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_COLOR, COLOR_BLACK_HEX);
    path.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_WITDH, "1");
    path.getDefaultStyle().setAttribute(SvgBaseStyling.FILL_COLOR, patternUrl);
    path.setShapeCSSEnabled(false);
    path.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_LINE_PATTERN, "");
    return label;
  }

  /**
   * This is used to draw a single shape on the document. The size of the shape and the text are
   * inherited from the master-shape and are not customizable. The color and the text style can be
   * customized thought CSS classes which can be defined in SVG-template.
   *
   * @param masterShape
   *          the master-shape this shape is based on.
   * @param drawPointer
   *          the position of the shape.
   * @param text
   *          the text contained in the shape.
   * @param textClass
   *          the name of the CSS class defining the text style.
   * @param colorClass
   *          the name of the CSS class defining the color of the shape.
   * @return the drawn shape.
   * @throws SvgExportException
   *           is thrown if some error occurs when drawing the shape.
   */
  protected Shape createTextLabel(String masterShape, Coordinates drawPointer, String text, String textClass, String colorClass)
      throws SvgExportException {
    final Shape label = createNewShape(masterShape);
    label.setPosition(drawPointer.getX(), drawPointer.getY());
    label.setTextFieldValue(text);
    if (textClass != null) {
      label.addTextCSSClass(textClass);
    }
    if (colorClass != null) {
      label.addCSSClass(colorClass);
    }
    return label;
  }

  /**
   * Creates a new {@link Shape}
   * @param masterShape
   *          name of the master-shape for the new shape
   * @return the {@link Shape}
   * @throws SvgExportException
   */
  protected Shape createNewShape(String masterShape) throws SvgExportException {
    return svgDocument.createNewShape(masterShape);
  }

  /**
   * Creates a new {@link Shape}, see also {@link Document#createNewShape(String, int)}
   * @param masterShape
   * @param layer
   * @return the {@link Shape}
   * @throws SvgExportException
   */
  protected Shape createNewShape(String masterShape, int layer) throws SvgExportException {
    return svgDocument.createNewShape(masterShape, layer);
  }

  /**
   * This is used to draw a single shape on the document. The size of the shape and the text are
   * inherited from the master-shape and are not customizable. The color and the text style can be
   * customized thought CSS classes which can be defined in SVG-template.
   *
   * @param masterShape
   *          the master-shape this shape is based on.
   * @param drawPointer
   *          the position of the shape.
   * @param text
   *          the text contained in the shape.
   * @param textClass
   *          the name of the CSS class defining the text style.
   * @param colorClass
   *          the name of the CSS class defining the color of the shape.
   * @param angle
   *          the shape will be rotated this angle to the right.
   * @return the drawn shape.
   * @throws SvgExportException
   *           is thrown if some error occurs when drawing the shape.
   */
  protected Shape createTextLabel(String masterShape, Coordinates drawPointer, String text, String textClass, String colorClass, double angle)
      throws SvgExportException {
    final Shape label = createTextLabel(masterShape, drawPointer, text, textClass, colorClass);
    label.setAngle(angle);
    return label;
  }

  /**
   * Draws a simple text on the document. If you specify a maximum width, the text will wrap
   * accordingly and the text-shape's height will adjust. The resulting shape will have
   * text CSS styling enabled.
   * @param pointer
   *          {@link Coordinates}: upper left corner of the text-area
   * @param text
   *          String to display
   * @param textSize
   *          font-size in pt, if 0 or smaller, a default value will be used
   * @param maxWidth
   *          maximum width of the text area. if 0 or smaller, no text-wrapping will be applied
   * @param textStyles
   *          optional CSS classes to be applied to the text
   * @return the text shape
   * @throws SvgExportException
   *           is thrown if some error occurs when drawing the shape.
   */
  protected Shape createAdjustingTextShape(Coordinates pointer, String text, int textSize, double maxWidth, String... textStyles)
      throws SvgExportException {
    final Shape textShape = createNewShape(MASTER_LABEL_LARGE);
    textShape.setTextFieldValue(text);
    textShape.setPosition(pointer.getX(), pointer.getY());
    if (textStyles != null) {
      for (String style : textStyles) {
        textShape.addTextCSSClass(style);
      }
    }

    makeTextShapeAdjust(textShape, textSize, maxWidth);
    return textShape;
  }

  /**
   * If you specify a maximum width, the text in the given text shape will wrap
   * accordingly and the text-shape's height will adjust. The shape will have
   * text CSS styling enabled.
   * @param textShape
   *          Shape for the text
   * @param textSize
   *          font-size in pt, if 0 or smaller, a default value will be used
   * @param maxWidth
   *          maximum width of the text area. if 0 or smaller, no text-wrapping will be applied
   * @throws SvgExportException
   *           is thrown if some error occurs when drawing the shape.
   */
  protected void makeTextShapeAdjust(final Shape textShape, int textSize, double maxWidth) throws SvgExportException {

    int fontSize = 11;
    if (textSize > 0) {
      fontSize = textSize;
    }
    textShape.addTextCSSClass(createFontSizeCssClass(fontSize));
    textShape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_SIZE, String.valueOf(fontSize));

    double textWidth = AdvancedTextHelper.getTextWidth(textShape.getTextFieldValue().length(), fontSize, AdvancedTextHelper.POINT_TO_UNIT_CONSTANT);
    textShape.setResizeTextWithShape(false);
    textShape.setBoundTextInShape(true);
    if (maxWidth > 0) {
      double adjustedMaxWidth = maxWidth * AdvancedTextHelper.TEXT_BOX_WIDTH_TO_SHAPE_WIDTH_FACTOR;
      int rows = (int) Math.ceil(textWidth / adjustedMaxWidth);
      textShape.setSize(maxWidth, rows * fontSize * 1.4);
    }
    else {
      textShape.setSize(textWidth, fontSize * 1.4);
    }
    textShape.setLineSpacing(fontSize * 0.4);
    textShape.setTextXPlacement(0);

    textShape.setTextCSSEnabled(true);
  }

  private String createFontSizeCssClass(int fontSize) throws SvgExportException {
    String cssClassName = "fontsize" + fontSize + "pt";
    List<SvgCssStyling> cssClasses = getSvgDocument().getCSSStyleClasses();
    boolean exists = false;
    for (SvgCssStyling css : cssClasses) {
      if (css.getCssClassName().equals(cssClassName)) {
        exists = true;
        continue;
      }
    }
    if (!exists) {
      Map<String, String> cssClass = CollectionUtils.hashMap();
      cssClass.put("font-size", String.valueOf(fontSize));
      getSvgDocument().createNewCSSClass(cssClassName, cssClass);
    }
    return cssClassName;
  }

  /**
   * This method creates a vertical line into the document.
   *
   * @param drawPointer
   *          The start point of the line.
   * @param length
   *          The length of the line.
   * @param lineClass
   *          A css class name for the class to be added to the shape's classes.
   * @throws SvgExportException
   *           Thrown if some error occurs when creating the line the line.
   * @return The generated PathShape.
   */

  protected PathShape createVerticalLine(Coordinates drawPointer, double length, String lineClass) throws SvgExportException {

    if (drawPointer == null || lineClass == null) {
      LOGGER.error("A line with null coordinates or null list of css classes cannot be created.");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    final PathShape innerLine = (PathShape) createNewBasicShape(BasicShape.SVG_BASIC_SHAPES.PATH);

    innerLine.moveTo(drawPointer.getX(), drawPointer.getY());
    innerLine.lineTo(drawPointer.getX(), drawPointer.getY() + length);

    innerLine.addCSSClass(lineClass);

    return innerLine;
  }

  /**
   * Creates a new {@link BasicShape}
   * @param masterShapeName
   *          name of the master-shape for the new shape, see also {@link Document#createNewBasicShape(String)}
   * @return the {@link BasicShape}
   * @throws SvgExportException
   */
  protected BasicShape createNewBasicShape(String masterShapeName) throws SvgExportException {
    return svgDocument.createNewBasicShape(masterShapeName);
  }

  /**
   * This method creates a horizontal line into the document.
   *
   * @param drawPointer
   *          The start position of the line.
   * @param length
   *          The length of the line.
   * @param lineClass
   *          A css class name for the class to be added to the shape's classes.
   * @throws SvgExportException
   *           If some error occurs while creating the line.
   * @return The generated PathShape.
   */
  protected PathShape createHorizontalLine(Coordinates drawPointer, double length, String lineClass) throws SvgExportException {

    if (drawPointer == null || lineClass == null) {
      LOGGER.error("A line with null coordinates or null list of css classes cannot be created.");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    final PathShape innerLine = (PathShape) createNewBasicShape(BasicShape.SVG_BASIC_SHAPES.PATH);

    innerLine.moveTo(drawPointer.getX(), drawPointer.getY());
    innerLine.lineTo(drawPointer.getX() + length, drawPointer.getY());

    innerLine.addCSSClass(lineClass);

    return innerLine;
  }

  /**
   * This method creates the generated information. Note that it is possible
   * to divide the document into different logical pages and create generated information
   * for every one of them.
   *
   * @param pageEndX
   *          The global x coordinate that is to be taken as end coordinate of the logical page.
   * @param pageEndY
   *          The global y coordinate that is to be taken as an end coordinate of the logical page.
   * @throws SvgExportException
   *           If there is an error with the creation of the SVG shapes.
   */
  protected void createGeneratedInformation(double pageEndX, double pageEndY) throws SvgExportException {

    // Create generated information
    final Shape shape = createNewShape(MASTER_TITLE);
    shape.setPosition(pageEndX - MARGIN, pageEndY - MARGIN);
    shape.addTextCSSClass(CSS_GENERATED_INFORMATION);
    shape.setTextFieldValue("Generated " + DateUtils.formatAsStringToLong(new Date(), getLocale()) + " by "
        + MessageAccess.getStringOrNull("global.applicationname", getLocale()) + " " + properties.getBuildId());
  }

  /**
   * This method creates the diagram title. Note that it is possible to divide the document
   * into different logical pages and create title for every one of them.
   *
   * @param titleStr
   *          The string that is to be used as a title. Can contain "\n" as line break to create multi-row titles.
   * @param pageStartX
   *          The global x coordinate that is to be taken as a page start. Use zero for the original
   *          page start.
   * @param pageStartY
   *          The global y coordinate that is to be taken as a page start. Use zero for the original
   *          page start.
   * @param leftMargin
   *          The distance from {@code pageStartX} at which the title should be displayed
   * @return Height of the title
   * @throws SvgExportException
   *           If there is an error with the creation of the SVG shapes.
   */
  protected double createDiagramTitle(String titleStr, double pageStartX, double pageStartY, double leftMargin) throws SvgExportException {
    // Create diagram title
    final List<String> titleRows = Arrays.asList(titleStr.split("\n"));
    double height = 0;
    for (String row : titleRows) {
      final Shape titleShape = createNewShape(MASTER_TITLE);
      titleShape.setTextFieldValue(row);
      titleShape.setPosition(pageStartX + leftMargin, pageStartY + MARGIN_TOP + height);
      titleShape.addTextCSSClass(CSS_TITLE);
      height += titleShape.getHeight();
    }
    return height;
  }

  protected double createSavedQueryInfo(Coordinates pos, double maxWidth, SavedQueryEntityInfo queryInfo, String serverUrl) throws SvgExportException {

    double yPos = pos.getY();
    if (queryInfo != null) {
      String url = GeneralHelper.createFastExportUrl(serverUrl, queryInfo.getReportType(), queryInfo.getId());
      String pngString = BitmapCodeGenerator.generateFastExportUrlCode(serverUrl, queryInfo.getId(), queryInfo.getReportType());
      int imageBorderWidth = BitmapCodeGenerator.URL_CODE_HEIGHT / 5;

      final Shape qrCodeShape = createNewShape(MASTER_IMAGE);
      qrCodeShape.setPosition(pos.getX(), pos.getY());
      qrCodeShape.setImageXLink("data:image/png;base64," + pngString);
      qrCodeShape.setImageSize(BitmapCodeGenerator.URL_CODE_WIDTH, BitmapCodeGenerator.URL_CODE_HEIGHT);
      pos.incX(qrCodeShape.getImageWidth());
      pos.incY(imageBorderWidth);

      double width = maxWidth - qrCodeShape.getImageWidth() - MARGIN;

      final Shape queryInfoHeaderShape = createAdjustingTextShape(pos, MessageAccess.getStringOrNull("graphicalExport.basedOn", getLocale()), 0,
          width);
      pos.incY(queryInfoHeaderShape.getHeight());
      queryInfoHeaderShape.setXLink(url);

      final Shape queryInfoTitleShape = createAdjustingTextShape(pos, queryInfo.getName(), 0, width);
      pos.incY(queryInfoTitleShape.getHeight());
      queryInfoTitleShape.setXLink(url);

      if (!queryInfo.getDescription().isEmpty()) {
        final Shape queryInfoDescShape = createAdjustingTextShape(pos, queryInfo.getDescription(), 0, width);
        pos.incY(queryInfoDescShape.getHeight());
        queryInfoDescShape.setXLink(url);
      }
      yPos = Math.max(pos.getY() + imageBorderWidth, qrCodeShape.getPinY() + qrCodeShape.getImageHeight());
    }

    return yPos;
  }

  /**
   * This method draws the iteraplan logos according to the given logical dimensions of the page.
   * Note that it is possible to divide the document into different logical pages and create logos
   * separately for every one of them.
   *
   * @param pageStartX
   *          The x coordinate of the logical page start.
   * @param pageStartY
   *          The y coordinate of the logical page end.
   * @param pageEndX
   *          The x coordinate of the logical page end.
   * @param pageEndY
   *          The y coordinate of the logical page end.
   */
  protected void createLogos(double pageStartX, double pageStartY, double pageEndX, double pageEndY) throws SvgExportException {
    final Shape logoUp = createNewShape(MASTER_LOGO_UPPER_RIGHT);
    logoUp.setPosition(pageEndX - 2 * logoUp.getWidth(), pageStartY + logoUp.getHeight());
    logoUp.setShapeCSSEnabled(false);

    final Shape logoDown = createNewShape(MASTER_LOGO_LOWER_LEFT);
    logoDown.setPosition(pageStartX + logoDown.getWidth(), pageEndY - 2 * logoDown.getHeight());
    logoDown.setShapeCSSEnabled(false);
  }

  /**
   * Transform a color to a hexadecimal string.
   *
   * @param color
   *          The input color.
   * @return The resulting RGB representation.
   */
  protected String getColorStr(Color color) {
    String rgb = Integer.toHexString(color.getRGB());
    rgb = rgb.substring(2, rgb.length());
    return rgb;
  }

  protected List<String> getColorStrings(List<Color> colors) {
    final List<String> colorStrings = new LinkedList<String>();
    for (Color color : colors) {
      colorStrings.add(getColorStr(color));
    }
    return colorStrings;
  }

  /**
   * Retrieves the iteraplan-URL of an attribute with the specified ID.
   *
   * @param attributeId
   *          The attribute Id.
   * @return The generated link.
   */
  protected String getUrlForAttributeId(Integer attributeId, String serverUrl) {
    if (attributeId == null || attributeId.intValue() <= 0) {
      return " ";
    }
    return retrieveXLinkUrlForIdentityEntity(getAttributeTypeService().loadObjectById(attributeId), serverUrl);
  }

  /**
   * Creates a line legend with the specified parameters.
   *
   * @param dimension
   *          The LineDimension that holds relevant information.
   * @param legendTitle
   *          The text to be used as a legend header.
   * @param headerUrl
   *          The optional URL of the legend header.
   * @param legendBaseX
   *          The base x-coordinate of the legend (left border).
   * @param legendBaseY
   *          The base y-coordinate of the legend (the top side border).
   * @param legendBoxWidth
   *          The width of a legend box (a single entry in the legend).
   * @param legendBoxHeight
   *          The height of a legend box (a single entry in the legend).
   * @throws SvgExportException
   */
  protected int createLineLegend(LineDimension dimension, String legendTitle, String headerUrl, double legendBaseX, double legendBaseY,
                                 double legendBoxWidth, double legendBoxHeight) throws SvgExportException {

    final List<String> values = new ArrayList<String>(dimension.getValues());

    final String unspecified = MessageAccess.getStringOrNull(DimensionOptionsBean.DEFAULT_VALUE, getLocale());
    if (dimension.hasUnspecificValue()) {
      values.add(unspecified);
    }

    if (values.size() > 0) {

      Coordinates boxSize = new Coordinates(legendBoxWidth, 0);
      Coordinates textShift = new Coordinates(0, 0);
      final List<BasicShape> legendBoxes = createBaseLegend(MASTER_LEGEND_BOX, boxSize, new Coordinates(legendBaseX, legendBaseY), values, true,
          legendTitle, textShift, null, headerUrl);

      int count = 1;
      Integer lineType;
      for (String key : values) {
        if ((key == null) || key.equals(unspecified)) {
          lineType = dimension.getDefaultValue();
        }
        else {
          lineType = dimension.getValue(key);
        }
        createHorizontalLine(new Coordinates(legendBaseX + LINE_PADDING, legendBoxes.get(count).getPinY() + legendBoxHeight / 2), LINE_WIDTH,
            getLineTypeToCSSStyleMap().get(lineType.toString()));
        count++;
      }
    }

    return values.size();

  }

  /**
   * Triggers the creation of a names legend.
   *
   * @param frameX
   *          The x-coordinate of the names legend frame on the existing page. Relevant only if the
   *          names legend mode is AUTO or IN_PAGE.
   * @param frameY
   *          The y-coordinate of the names legend frame on the existing page. Relevant only if the
   *          names legend mode is AUTO or IN_PAGE.
   * @param frameWidth
   *          The width of the names legend frame on the existing page. Relevant only if the names
   *          legend mode is AUTO.
   * @param frameHeight
   *          The height of the names legend frame on the existing page. Relevant only if the names
   *          legend mode is AUTO.
   * @param nakedExport
   *          Whether naked export is on or off. If naked export is off, logos, title and generated
   *          information will be added to every logical page being generated. Relevant for the
   *          modes AUTO and NEW_PAGE.
   * @param titleString
   *          The title of the graphic to be inserted into every new page if naked export is off.
   *          Relevant for the modes AUTO and NEW_PAGE.
   * @throws SvgExportException
   */
  protected void createNamesLegend(double frameX, double frameY, double frameWidth, double frameHeight, boolean nakedExport, String titleString)
      throws SvgExportException {

    if (getSvgNamesLegend().displayNamesLegend() && !getSvgNamesLegend().isEmpty()) {

      final String attachmentStr = MessageAccess.getStringOrNull("reports.nameLegendAttachment", getLocale());
      final String legendPageTitleStr = titleString + "\n" + attachmentStr;

      final int titleRows = legendPageTitleStr.split("\n").length;
      getSvgNamesLegend().setTopMargin(titleRows * MARGIN + MARGIN_TOP);

      getSvgNamesLegend().setFrameSize(frameWidth, frameHeight, frameX, frameY);
      getSvgNamesLegend().setPageSize(svgDocument.getPageWidth(), svgDocument.getPageHeight());

      final List<LogicalPage> logicalPages = getSvgNamesLegend().createLegend();

      // Add logos and texts for the newly generated logical pages
      for (LogicalPage page : logicalPages) {
        if (!nakedExport) {
          createLogos(page.getBeginX(), 0, page.getBeginX() + page.getWidth(), svgDocument.getPageHeight());
          createDiagramTitle(legendPageTitleStr, page.getBeginX(), 0, SvgNamesLegend.LEGEND_MARGIN);
          createGeneratedInformation(page.getBeginX() + page.getWidth(), svgDocument.getPageHeight());
        }
      }
    }
  }

  /**
   * Calculates the size (in pt) of a text with a given length, so that it fits as a text line into
   * a box with the specified dimensions.
   *
   * @param fieldWidth
   *          The width of the field in svg screen units.
   * @param fieldHeight
   *          The height of the field in svg screen units.
   * @param textLength
   *          The number of charachters in the text string.
   * @return The text size in pt so that it fits in the field.
   */
  protected double estimateFontSize(double fieldWidth, double fieldHeight, int textLength) {
    final double fontSize = Math.min(10, (fieldWidth * 0.8) / (textLength * AdvancedTextHelper.POINT_TO_UNIT_CONSTANT));
    return Math.min(fontSize, (fieldHeight * 0.8 * AdvancedTextHelper.POINT_TO_UNIT_CONSTANT));
  }

  /**
   * Creates an iteraplan-URL for the given entity.
   *
   * @param entity
   *          The entity whose link to build.
   * @param serverUrl
   *          The url of the server.
   * @return The generated URL.
   */
  protected String retrieveXLinkUrlForIdentityEntity(IdentityEntity entity, String serverUrl) {
    return URLBuilder.getEntityURL(entity, serverUrl);
  }

  /**
   * Creates a RectangleShape as background of {@code parentShape} with horizontally striped coloring and the given {@code outlineColor}
   * 
   * @param parentShape
   *          shape that gets this background
   * @param backgroundColorStrings
   *          list of color-strings in hex format providing the colors for background coloring
   * @param outlineColor
   *          color of the outline
   * @return the background shape for further adjustments, if necessary (like transparency, edge rounding, ...)
   * @throws SvgExportException
   */
  protected RectangleShape createShapeBackgroundAndOutline(Shape parentShape, List<String> backgroundColorStrings, String outlineColor)
      throws SvgExportException {
    final double width = parentShape.getWidth();
    final double height = parentShape.getHeight();

    final String fillPatternId = createHorizontalLinesPattern(backgroundColorStrings, width, height);

    final RectangleShape background = (RectangleShape) parentShape.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
    background.setSize(width, height);
    background.setShapeCSSEnabled(false);

    background.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_COLOR, outlineColor);
    background.getDefaultStyle().setAttribute(SvgBaseStyling.FILL_COLOR, fillPatternId);
    background.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_WITDH, "1");
    background.setInternalLayer(4);

    return background;
  }

  /**
   * Creates a PatternShape basic shape with a horizontal line pattern, where each line is coloured
   * in one of the colors in the provided list.
   *
   * @param colorStrings
   *          The list of hexadecimal strings coding the colors for the pattern.
   * @param width
   *          The width of the pattern shape. Note that sometimes the pattern might need to be wider
   *          than the actual shape as to be able to cover it completely.
   * @param height
   *          The height of the pattern.
   * @return The pattern id together with its url, as it is to be referenced by the attributes of
   *         the shapes. For example 'url(#myPatternBasic2)'.
   * @throws SvgExportException
   *           If an error occurs while creating the basic shape.
   */
  protected String createHorizontalLinesPattern(List<String> colorStrings, double width, double height) throws SvgExportException {

    final PatternShape pattern = (PatternShape) createNewBasicShape(BasicShape.SVG_BASIC_SHAPES.PATTERN);

    int count = 0;
    final double entryHeight = height / colorStrings.size();

    for (String colorHex : colorStrings) {
      final RectangleShape rect = (RectangleShape) pattern.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
      rect.setSize(width, entryHeight);
      rect.setPosition(0, count * entryHeight);
      rect.setInternalLayer(4);

      rect.getDefaultStyle().setAttribute(SvgBaseStyling.FILL_COLOR, colorHex);
      rect.setShapeCSSEnabled(false);

      count++;
    }

    return "url(#" + pattern.getID() + ")";
  }

  /**
   * This method transforms the internal encoding for a line pattern to the chosen line pattern to
   * be used in the line legends (and shapes accordingly) of the svg exports.
   *
   * @param internalValue
   *          The iteraplan line pattern value.
   * @return The string specifying the stroke-dasharray attribute of the css class for this line
   *         pattern.
   */
  public static String getSvgLinePattern(int internalValue) {
    if (internalValue % 4 == 0) {
      return "none";
    }
    else if (internalValue % 4 == 1) {
      return "2,4";
    }
    else if (internalValue % 4 == 2) {
      return "10,4,2,4";
    }
    else {
      return "12";
    }
  }

  protected Document getSvgDocument() {
    return svgDocument;
  }

  protected Map<String, String> getColorToColorClassMap() {
    return colorToColorClassMap;
  }

  protected void setSvgNamesLegend(SvgNamesLegend svgNamesLegend) {
    this.svgNamesLegend = svgNamesLegend;
  }

  protected INamesLegend getSvgNamesLegend() {
    if (svgNamesLegend != null) {
      return svgNamesLegend;
    }
    else {
      // to avoid NullPointerExceptions when overlooking something in the SvgExport-implementations
      return EmptyNamesLegend.getInstance();
    }
  }

  protected Map<String, String> getLineTypeToCSSStyleMap() {
    return lineTypeToCSSStyleMap;
  }

  protected String getScreenName(IGraphicalExportBaseOptions options, String originalName, String fullName, String entryCategory, double fieldWidth,
                                 double textSizePt, String elementUrl) {
    if (!options.isUseNamesLegend()) {
      return originalName;
    }
    return getSvgNamesLegend().addLegendEntry(originalName, fullName, entryCategory, fieldWidth, textSizePt, elementUrl);
  }

  protected BasicShape createSegmentShape(Shape parent, double centerX, double centerY, double radius, double centralAngleRadians,
                                          double startingAngleRadians) throws SvgExportException {

    double fullCircle = 2 * Math.PI;
    /*
     * Using a small margin to avoid errors due to the possibility of the full circle angle double value
     * being slightly different depending on how it's calculated (rounding errors).
     */
    if (Math.abs(centralAngleRadians - fullCircle) < 0.0000001) {
      CircleShape fullSegment = (CircleShape) parent.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.CIRCLE);
      fullSegment.setRadius(radius);
      return fullSegment;
    }
    else {
      PathShape segment = (PathShape) parent.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.PATH);

      int largeArc = 0;
      if (centralAngleRadians > Math.PI) {
        largeArc = 1;
      }

      segment.moveTo(centerX, centerY);
      segment.lineTo(centerX + radius * Math.cos(startingAngleRadians), centerY + radius * Math.sin(startingAngleRadians));
      segment.ellipticalArcTo(radius, radius, 0, largeArc, 1, centerX + radius * Math.cos(startingAngleRadians + centralAngleRadians), centerY
          + radius * Math.sin(startingAngleRadians + centralAngleRadians));
      segment.setClosePath(true);

      return segment;
    }
  }

  protected String getUsedAttributeValueRange(ColorDimension colorDim, Object element) {
    String usedValue = "";
    if (element instanceof BuildingBlock) {
      usedValue = colorDim.getAdapter().getResultForValue(element);
    }
    else if (element instanceof AttributeValue) {
      usedValue = ((AttributeValue) element).getLocalizedValueString(getLocale());
    }
    else {
      return "ERROR";
    }

    if (colorDim.getAdapter() instanceof AttributeRangeAdapter) {
      usedValue = ((AttributeRangeAdapter) colorDim.getAdapter()).getResultForValue(usedValue);
    }

    if (usedValue == null || "".equals(usedValue)) {
      usedValue = DimensionOptionsBean.DEFAULT_VALUE;
    }
    return usedValue;
  }

  /**
   * Uses {@code colorDim} and {@code usedValues} to calculate the valuesList to be shown in the legend.
   * Then calls {@link #createColorLegend(String, Coordinates, String, List, List, boolean, String)}.
   * @param colorDim
   *          {@link ColorDimension} holding information about values and colors
   * @param usedValues
   *          String-list of values that should be displayed
   * @param position
   * @param colorShapeMasterName
   * @param headline
   * @param headerEnabled
   * @param headerUrl
   * @return The number of entries in the legend, includuing the header and unspecified value if those exist.
   *         Can be used to determine the height of the legend.
   * @throws SvgExportException
   */
  protected int createColorLegendForUsedAttrValuesOnly(ColorDimension colorDim, Set<String> usedValues, Coordinates position,
                                                       String colorShapeMasterName, String headline, boolean headerEnabled, String headerUrl)
      throws SvgExportException {
    List<String> values = new ArrayList<String>();
    List<String> colors = new ArrayList<String>();
    for (String attrValue : colorDim.getValues()) {
      if (usedValues.contains(attrValue)) {
        values.add(attrValue);
        colors.add(getColorStr(colorDim.getValue(attrValue)));
      }
    }
    if (colorDim instanceof ColorRangeDimension) {
      modifyValues((ColorRangeDimension) colorDim, values);
    }
    if (colorDim.hasUnspecificValue() && usedValues.contains(DimensionOptionsBean.DEFAULT_VALUE)) {
      values.add(MessageAccess.getStringOrNull(DimensionOptionsBean.DEFAULT_VALUE, getLocale()));
      colors.add(getColorStr(colorDim.getDefaultValue()));
    }

    return createColorLegend(colorShapeMasterName, position, headline, values, colors, headerEnabled, headerUrl);
  }

  private void modifyValues(ColorRangeDimension colorRangeDim, final List<String> values) {
    for (int i = 0; i < values.size(); i++) {
      String prefix = MessageAccess.getStringOrNull(colorRangeDim.getLegendPrefixKeyFor(values.get(i)));
      values.set(i, prefix + ": " + values.get(i));
    }
  }

  /**
   * Set a custom size of the view.
   * 
   * Only one of this parameters are considered.
   * Are both parameters are null, nothing is done. The graphic is created in the original size.
   * If width or height is set - the parameter who is not defined, is set automatically into the right ratio.
   * If both parameter are set - the value for the height is ignored and is set automatically into the right ratio to the width.
   * 
   * @param width
   * @param height
   */
  protected void setCustomSize(Double width, Double height) {
    if (width != null) {
      double changeInPercent = (width.doubleValue() * 100) / getSvgDocument().getPageWidth();
      double newHeight = (getSvgDocument().getPageHeight() * changeInPercent) / 100;

      getSvgDocument().setViewBox(0, 0, getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
      getSvgDocument().setPageSize(width.doubleValue(), newHeight);
    }
    else if (height != null) {
      double changeInPercent = (height.doubleValue() * 100) / getSvgDocument().getPageHeight();
      double newWidth = (getSvgDocument().getPageWidth() * changeInPercent) / 100;

      getSvgDocument().setViewBox(0, 0, getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
      getSvgDocument().setPageSize(newWidth, height.doubleValue());
    }
  }

}

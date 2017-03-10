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
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.exchange.DimensionExport;
import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.LineDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.EmptyNamesLegend;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.INamesLegend;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.LogicalPage;
import de.iteratec.iteraplan.businesslogic.exchange.visio.legend.VisioAttributeLegend;
import de.iteratec.iteraplan.businesslogic.exchange.visio.legend.VisioColorLegend;
import de.iteratec.iteraplan.businesslogic.exchange.visio.legend.VisioLineTypeLegend;
import de.iteratec.iteraplan.businesslogic.exchange.visio.legend.VisioNamesLegend;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.IGraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.InchConverter;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.iteraplan.model.queries.SavedQueryEntityInfo;
import de.iteratec.visio.model.Document;
import de.iteratec.visio.model.DocumentFactory;
import de.iteratec.visio.model.Page;
import de.iteratec.visio.model.Shape;
import de.iteratec.visio.model.exceptions.MasterNotFoundException;
import de.iteratec.visio.model.exceptions.NoSuchElementException;


public abstract class VisioDimensionExport extends DimensionExport {

  private Document                  visioDocument;
  private Page                      targetPage;

  private VisioNamesLegend          visioNamesLegend;

  private final IteraplanProperties properties                             = IteraplanProperties.getProperties();

  protected static final int        DEFAULT_SYSTEM_DPI                     = 72;

  protected static final double     DISTANCE_TO_MARGIN_INCHES              = InchConverter.cmToInches(1.0);

  public static final String        VISIO_SHAPE_NAME_LINE_FIELD            = "Line-Index-Field";

  protected static final String     VISIO_SHAPE_NAME_LOGO_LEFT             = "LogoLowerLeftCorner";
  protected static final String     VISIO_SHAPE_NAME_LOGO_RIGHT            = "LogoUpperRightCorner";
  protected static final String     VISIO_SHAPE_NAME_GENERATED_INFORMATION = "GenerationInformation";

  protected static final String     VISIO_SHAPE_NAME_TITLE                 = "Title";

  // 11 * 0.0139 -> ptToInch
  protected static final double     TXT_SIZE_11_PTS                        = 0.1529;

  private static final Logger       LOGGER                                 = Logger.getIteraplanLogger(VisioDimensionExport.class);
  public static final String        PROP_XREF_URL                          = "Url";

  public VisioDimensionExport(AttributeTypeService attributeTypeService, AttributeValueService attributeValueService) {
    super(attributeTypeService, attributeValueService);
  }

  /**
   * Creates a new visio document from a given template file with the given title.
   * 
   * @param visioTemplateFile
   *          name of the visio template file for the respective diagram type.
   *          <strong>Contract:</strong> caller must ensure that the file name is not null.
   * @param visioDiagramTitle
   *          title of the diagram (to be displayed)
   */
  protected void init(String visioTemplateFile, String visioDiagramTitle) {
    LOGGER.debug("entering init...");
    try {
      InputStream is = null;
      try {
        is = VisioDimensionExport.class.getResourceAsStream(visioTemplateFile);
                this.setVisioDocument(DocumentFactory.getInstance().loadDocument(is));
      } finally {
        if (is != null) {
          is.close();
        }
      }
      this.getVisioDocument().setTitle(visioDiagramTitle);
      this.setTargetPage(this.getVisioDocument().getPage(0));
    } catch (IOException iex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, iex);
    } catch (NoSuchElementException nseex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, nseex);
    } catch (ParserConfigurationException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (SAXException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
    LOGGER.debug("leaving init...");
  }

  /**
   * Returns a hexadecimal RGB string from a Color.class Object. The return value has a fixed size
   * with 6 numbers and leading '#'
   *
   * @param color
   *          Color object.
   * @return RGB string.
   */
  protected String getColorStr(Color color) {
    String rgb = Integer.toHexString(color.getRGB());
    rgb = "#" + rgb.substring(2, rgb.length());
    return rgb;
  }

  /**
   * Paints a new color legend for a given configuration and page.
   *
   * @param colorOptions
   *          Specific configurations
   * @param page
   *          Page object where we add the new legend
   * @param position
   *          Left bound where to position the color legend. Can be null.
   * @param colorShapeName
   *          Name of the symbol shape in the template
   * @return legend height in inch
   * @throws MasterNotFoundException
   */
  protected double createColorLegend(ColorDimensionOptionsBean colorOptions, Page page, final Coordinates position, String colorShapeName,
                                     String headline, TypeOfBuildingBlock bbType) throws MasterNotFoundException {
    ColorDimension colorDimension = createColorDimension(colorOptions, bbType);
    VisioAttributeLegend<Color> legend = new VisioColorLegend();
    legend.initializeLegend(page, colorDimension, headline, getLocale());
    legend.createLegendEntries(colorShapeName);
    legend.setPosition(position);
    return legend.getLegendHeightInInch();
  }

  protected double createColorLegend(ColorDimension colorDimension, Page page, final Coordinates position, String colorShapeName, String headline,
                                     TypeOfBuildingBlock bbType) throws MasterNotFoundException {
    VisioAttributeLegend<Color> legend = new VisioColorLegend();
    legend.initializeLegend(page, colorDimension, headline, getLocale());
    legend.createLegendEntries(colorShapeName);
    legend.setPosition(position);
    return legend.getLegendHeightInInch();
  }

  /**
   * Paints a new line type legend for a given configuration and page.
   *
   * @param lineOptions
   *          Specific configurations
   * @param page
   *          Page object where we add the new legend
   * @param position
   *          Left bound where to position the color legend. Can be null.
   * @param lineShapeName
   *          Name of the symbol shape in the template
   * @return legend height in inch
   * @throws MasterNotFoundException
   */
  protected double createLineTypeLegend(LineDimensionOptionsBean lineOptions, Page page, final Coordinates position, String lineShapeName,
                                        String headline, TypeOfBuildingBlock bbType) throws MasterNotFoundException {
    LineDimension lineDimension = createLineDimension(lineOptions, bbType);
    VisioAttributeLegend<Integer> legend = new VisioLineTypeLegend();
    legend.initializeLegend(page, lineDimension, headline, getLocale());
    legend.createLegendEntries(lineShapeName);
    legend.setPosition(position);
    return legend.getLegendHeightInInch();
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
   * @throws MasterNotFoundException
   */
  protected void createNamesLegend(double frameX, double frameY, double frameWidth, double frameHeight, boolean nakedExport, String titleString)
      throws MasterNotFoundException {

    if (getVisioNamesLegend().isEmpty()) {
      return;
    }

    final String attachmentStr = MessageAccess.getStringOrNull("reports.nameLegendAttachment", getLocale());
    final String legendPageTitleStr = titleString + "\n" + attachmentStr;

    final int titleRows = legendPageTitleStr.split("\n").length;
    getVisioNamesLegend().setTopMargin((titleRows + 1) * DISTANCE_TO_MARGIN_INCHES);

    getVisioNamesLegend().setFrameSize(frameWidth, frameHeight, frameX, frameY);
    getVisioNamesLegend().setPageSize(getTargetPage().getWidth(), getTargetPage().getHeight());

    List<LogicalPage> logicalPages = getVisioNamesLegend().createLegend();

    // Add logos and texts for the newly generated logical pages
    for (LogicalPage page : logicalPages) {
      if (!nakedExport) {
        createLogos(page.getBeginX(), 0, page.getBeginX() + page.getWidth(), getTargetPage().getHeight());
        Shape title = createDiagramTitle(legendPageTitleStr);

        setTitlePosAndSize(title, page.getBeginX() + 1, getTargetPage().getHeight(), Double.valueOf(page.getWidth() - 1));

        createGeneratedInformation(page.getBeginX() + page.getWidth());
      }
    }

    getTargetPage().setNumberOfPrintPages(logicalPages.size() + 1, 1);
  }

  protected void createLogos(double pageStartX, double pageStartY, double pageEndX, double pageEndY) throws MasterNotFoundException {

    Shape logoLeft = getTargetPage().createNewShape(VISIO_SHAPE_NAME_LOGO_LEFT);
    logoLeft.setPosition(pageStartX + DISTANCE_TO_MARGIN_INCHES, DISTANCE_TO_MARGIN_INCHES);

    Shape logoRight = getTargetPage().createNewShape(VISIO_SHAPE_NAME_LOGO_RIGHT);
    logoRight.setPosition(pageEndX - DISTANCE_TO_MARGIN_INCHES, pageEndY - DISTANCE_TO_MARGIN_INCHES);
  }

  protected void createGeneratedInformation(double pageEndX) throws MasterNotFoundException {

    // Generated information
    String generatedInfoStr = "Generated " + DateUtils.formatAsStringToLong(new Date(), Locale.ENGLISH) + " by "
        + MessageAccess.getStringOrNull("global.applicationname", getLocale()) + " " + properties.getBuildId();

    Shape shapeInfo = this.getTargetPage().createNewShape(VISIO_SHAPE_NAME_TITLE);
    shapeInfo.setCharSize(InchConverter.ptToInches(4, 72));
    shapeInfo.setSize(generatedInfoStr.length() * InchConverter.ptToInches(4, 72) / 1.75, InchConverter.ptToInches(4, 72));
    shapeInfo.setPosition(pageEndX - shapeInfo.getWidth() - InchConverter.cmToInches(1), InchConverter.cmToInches(1));
    shapeInfo.setFieldValue(generatedInfoStr);
  }

  protected Shape createDiagramTitle(String titleStr) throws MasterNotFoundException {
    Shape shape = this.getTargetPage().createNewShape(VISIO_SHAPE_NAME_TITLE);
    shape.setFieldValue(titleStr);
    return shape;
  }

  protected List<Shape> createSavedQueryInfo(Coordinates pos, double maxWidth, double fontSizePt, SavedQueryEntityInfo queryInfo, String serverUrl)
      throws MasterNotFoundException {
    List<Shape> infoShapes = CollectionUtils.arrayList();
    if (queryInfo != null) {
      double deltaY = 0;
      double posXInch = InchConverter.cmToInches(pos.getX());
      double posYInch = InchConverter.cmToInches(pos.getY());
      double widthInch = InchConverter.cmToInches(maxWidth);

      double fontSize = InchConverter.ptToInches(fontSizePt, 72);
      if (!queryInfo.getDescription().isEmpty()) {
        final Shape queryInfoDescShape = this.getTargetPage().createNewShape(VISIO_SHAPE_NAME_TITLE);
        queryInfoDescShape.setCharSize(fontSize);
        queryInfoDescShape.setSize(widthInch, fontSize * 2);
        queryInfoDescShape.setFieldValue(queryInfo.getDescription());
        queryInfoDescShape.setPosition(posXInch, posYInch + deltaY);
        deltaY += queryInfoDescShape.getHeight();
        infoShapes.add(queryInfoDescShape);
      }

      final Shape queryInfoTitleShape = this.getTargetPage().createNewShape(VISIO_SHAPE_NAME_TITLE);
      queryInfoTitleShape.setCharSize(fontSize);
      queryInfoTitleShape.setSize(widthInch, fontSize);
      queryInfoTitleShape.setFieldValue(queryInfo.getName());
      queryInfoTitleShape.setPosition(posXInch, posYInch + deltaY);
      deltaY += queryInfoTitleShape.getHeight();
      infoShapes.add(queryInfoTitleShape);

      final Shape queryInfoHeaderShape = this.getTargetPage().createNewShape(VISIO_SHAPE_NAME_TITLE);
      queryInfoHeaderShape.setCharSize(fontSize);
      queryInfoHeaderShape.setSize(widthInch, fontSize);
      queryInfoHeaderShape.setFieldValue(MessageAccess.getStringOrNull("graphicalExport.basedOn", getLocale()));
      queryInfoHeaderShape.setPosition(posXInch, posYInch + deltaY);
      infoShapes.add(queryInfoHeaderShape);
    }
    return infoShapes;
  }

  protected void setTitlePosAndSize(Shape title, double posX, double pageEndY, Double titleWidth) {
    if (titleWidth != null) {
      title.setSize(titleWidth.doubleValue(), title.getHeight());
    }
    title.setPosition(posX, pageEndY - DISTANCE_TO_MARGIN_INCHES * 1.3 - title.getHeight());
  }

  /**
   * Maps the internal line pattern values, as used in the business logic to the predefined line
   * patterns used by Visio. This is required to accomplish homogeneous line styles for the Visio
   * and Svg graphics as well as the masks of the application. Note that unknown internal patterns
   * are mapped to the value 1 which is the coding for a continuous line.
   *
   * @param internalLinePattern
   *          The line pattern as used internally in the application.
   * @return The id of the corresponding predefined pattern of Visio.
   */
  public static int getVisioLinePattern(int internalLinePattern) {
    switch (internalLinePattern) {
      case 1:
        return 3;
      case 2:
        return 9;
      case 3:
        return 2;

      default:
        // Covers the default value as well as unspecified values (like 4 for example)
        return 1;
    }
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

  public Document createDiagram() {
    LOGGER.error("VisioDimensionExport cannot be used to create Diagrams. Only as base class intented.");
    throw new UnsupportedOperationException();
  }

  public void setVisioDocument(Document visioDocument) {
    this.visioDocument = visioDocument;
  }

  public Document getVisioDocument() {
    return visioDocument;
  }

  public void setTargetPage(Page targetPage) {
    this.targetPage = targetPage;
  }

  public Page getTargetPage() {
    return targetPage;
  }

  public void setVisioNamesLegend(VisioNamesLegend visioNamesLegend) {
    this.visioNamesLegend = visioNamesLegend;
  }

  public INamesLegend getVisioNamesLegend() {
    if (visioNamesLegend != null) {
      return visioNamesLegend;
    }
    else {
      // to avoid NullPointerExceptions when overlooking something in the VisioDimensionExport-implementations
      return EmptyNamesLegend.getInstance();
    }
  }

  protected String getScreenName(IGraphicalExportBaseOptions options, String originalName, String fullName, String entryCategory, double fieldWidth,
                                 double textSizePt, String elementUrl) {
    if (!options.isUseNamesLegend()) {
      return originalName;
    }
    return getVisioNamesLegend().addLegendEntry(originalName, fullName, entryCategory, fieldWidth, textSizePt, elementUrl);
  }

  protected void setQueryInfoPos(List<Shape> queryInfos, double xPos, double yPos) {
    double y = yPos;
    if (queryInfos != null && !queryInfos.isEmpty()) {
      for (Shape info : queryInfos) {
        info.setPosition(xPos, y);
        y += info.getHeight();
      }
    }
  }

  protected double getQueryInfoHeight(List<Shape> queryInfo) {
    double infoHeight = 0;
    if (queryInfo != null) {
      for (Shape info : queryInfo) {
        infoHeight += info.getHeight();
      }
    }
    return infoHeight;
  }

}

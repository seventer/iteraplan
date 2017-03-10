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
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.INamesLegend.LegendMode;
import de.iteratec.iteraplan.businesslogic.exchange.common.neighbor.NeighborhoodDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.neighbor.SpatialInfromationSystemWrapper;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.svg.model.BasicShape;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.Shape;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.model.impl.PathShape;
import de.iteratec.svg.model.impl.RectangleShape;
import de.iteratec.svg.model.impl.TextShape;
import de.iteratec.svg.styling.SvgBaseStyling;


public class SvgNeighborhoodDiagramExport extends SvgExport {

  private NeighborhoodDiagram neighborhoodDiagram;
  private static final Logger LOGGER              = Logger.getIteraplanLogger(SvgNeighborhoodDiagramExport.class);
  private static final String SVG_TEMPLATE_FILE   = "/SVGNeighborhoodDiagramTemplate.svg";
  private static final String BACKGROUND_SHAPE    = "BackgroundShapeRoot";
  private static final String NEIGHBORHOOD_ENTRY  = "NeighborhoodEntryRoot";
  private static final String COLOR_LEGEND        = "ColorLegendFieldRoot";
  private static final String SVG_CSS_CURRENT     = "colorCurrent";
  private static final String SVG_CSS_PLANNED     = "colorPlanned";
  private static final String SVG_CSS_TARGET      = "colorTarget";
  private static final String SVG_CSS_INACTIVE    = "colorInactive";

  private static final String MESSAGE_CURRENT     = "typeOfStatus_current";
  private static final String MESSAGE_PLANNED     = "typeOfStatus_planned";
  private static final String MESSAGE_TARGET      = "typeOfStatus_target";
  private static final String MESSAGE_INACTIVE    = "typeOfStatus_inactive";

  private static final String LINE_PATTERN_STROKE = "none";

  private List<String>        colors              = new ArrayList<String>();
  private List<String>        values              = new ArrayList<String>();

  private static final int    BACKGROUND_LAYER    = 6;
  private static final int    CONNECTION_LAYER    = 7;
  private static final int    ENTITY_LAYER        = 8;
  private static final int    TEXT_LAYER          = 9;

  private Shape               rootShape;
  /**
   * Colors
   */
  private static final String BACKGROUND_COLOR    = "#BFBFBF";
  private static final String COLOR_BLACK         = "#000000";

  private final String        serverUrl;

  /**
   * Default constructor.
   * @param attributeTypeService
   * @param attributeValueService
   * @param serverUrl 
   */
  public SvgNeighborhoodDiagramExport(NeighborhoodDiagram neighborhoodDiagram, AttributeTypeService attributeTypeService,
      AttributeValueService attributeValueService, String serverUrl) {
    super(attributeTypeService, attributeValueService);
    this.neighborhoodDiagram = neighborhoodDiagram;
    this.serverUrl = serverUrl;
    loadSvgDocumentFromTemplate(SVG_TEMPLATE_FILE, "NeighborhoodDiagram");
    try {
      rootShape = createNewShape(BACKGROUND_SHAPE);
    } catch (SvgExportException ex) {
      LOGGER.debug("Error creating root shape in Neighborhood-Diagram");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    }
  }

  /**{@inheritDoc}**/
  @Override
  public Document createDiagram() {
    try {
      LOGGER.debug("Generating Svg-Neighborhood-Diagram ...");

      setSvgNamesLegend(new SvgNamesLegend(getSvgDocument()));

      createContextVisualizationContent();

      createDiagramTitle(MessageAccess.getStringOrNull(NeighborhoodDiagram.TITLE, UserContext.getCurrentLocale()), NeighborhoodDiagram.SIMPLE_MARGIN,
          0, 0);

      getSvgNamesLegend().setLegendMode(LegendMode.IN_PAGE);

      createNamesLegend(getSvgDocument().getPageWidth(), NeighborhoodDiagram.SIMPLE_MARGIN, 0, 0, false, neighborhoodDiagram.getObjectOfInterest()
          .getInformationSystemRelease().getTypeOfBuildingBlock().getValue());
      addColorLegendEntrys();

      if (getSvgNamesLegend().getNamesLegendHeight() < 2) {
        drawColorLegend(getSvgDocument().getPageWidth(), NeighborhoodDiagram.SIMPLE_MARGIN);
      }
      else {
        drawColorLegend(getSvgDocument().getPageWidth(), getSvgNamesLegend().getNamesLegendHeight() * NeighborhoodDiagram.LEGEND_ENTRY_HEIGHT
            + NeighborhoodDiagram.SIMPLE_MARGIN + NeighborhoodDiagram.DEFAULT_LEGEND_MARGIN);
      }
      getSvgDocument().setPageSize(
          getSvgDocument().getPageWidth()
              + Math.max(NeighborhoodDiagram.ROOT_MARGIN, getSvgNamesLegend().getLegendWidth() + NeighborhoodDiagram.ROOT_MARGIN),
          getSvgDocument().getPageHeight());

      getSvgDocument().finalizeDocument();

      LOGGER.debug("Finished Svg-Neighborhood-Diagram");

    } catch (SvgExportException ex) {
      LOGGER.error("SvgExportException while trying to create a SvgNeighborhoodDiagramExport:\n" + ex.getMessage());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    }
    return getSvgDocument();
  }

  /**
   * @throws SvgExportException 
   * 
   */
  private void createContextVisualizationContent() throws SvgExportException {
    BasicShape contextContent = generateVisualizationBackground();
    generateContextEntries(contextContent);
    if (contextContent.getWidth() > NeighborhoodDiagram.DEFAUL_CONTENT_SIZE || contextContent.getHeight() > NeighborhoodDiagram.DEFAUL_CONTENT_SIZE) {
      getSvgDocument().setPageSize(contextContent.getWidth() + NeighborhoodDiagram.ROOT_MARGIN,
          contextContent.getHeight() + NeighborhoodDiagram.ROOT_MARGIN);
    }
  }

  private BasicShape generateVisualizationBackground() throws SvgExportException {

    final RectangleShape background = (RectangleShape) rootShape.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE, BACKGROUND_LAYER);
    background.setSize(neighborhoodDiagram.getSideLength(), neighborhoodDiagram.getSideLength());

    /**
     * CSS
     */
    background.setShapeCSSEnabled(false);
    background.getDefaultStyle().setAttribute(SvgBaseStyling.FILL_COLOR, BACKGROUND_COLOR);
    background.getDefaultStyle().setAttribute(SvgBaseStyling.FILL_OPACITY, "1");
    background.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_LINE_PATTERN, LINE_PATTERN_STROKE);
    background.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_COLOR, neighborhoodDiagram.toHexString(Color.BLACK, true));

    return background;
  }

  /**
   * Fills the Background with the necessary content to display
   * @param background
   * @throws SvgExportException
   */
  private void generateContextEntries(BasicShape background) throws SvgExportException {
    for (SpatialInfromationSystemWrapper connectedIsr : neighborhoodDiagram.getConnectedInformationSystems()) {
      /**
       * The Rectangle which represents a Information System
       */
      generateContextEntry(background, connectedIsr, false);
      /**
       * The connection Between the Object of Interest and the InformationSystem
       */
      PathShape connection = (PathShape) background.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.PATH, CONNECTION_LAYER);

      connection.setPosition(connectedIsr.getCoordinate().getX(), connectedIsr.getCoordinate().getY());
      connection.moveTo(connectedIsr.getCoordinate().getX(), connectedIsr.getCoordinate().getY());
      connection.lineTo(neighborhoodDiagram.getObjectOfInterest().getCoordinate().getX(), neighborhoodDiagram.getObjectOfInterest().getCoordinate()
          .getY());
      /**
       * CSS
       */
      connection.setShapeCSSEnabled(false);
      connection.getDefaultStyle().setAttribute(SvgBaseStyling.FILL_COLOR, neighborhoodDiagram.toHexString(Color.BLACK, true));
      connection.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_COLOR, neighborhoodDiagram.toHexString(Color.BLACK, true));
      connection.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_LINE_PATTERN, LINE_PATTERN_STROKE);
    }
    /**
     * Object of Interest
     */
    generateContextEntry(background, neighborhoodDiagram.getObjectOfInterest(), true);
  }

  /**
   * @param background
   * @param connectedIsr
   * @throws SvgExportException        
   */
  private void generateContextEntry(BasicShape background, SpatialInfromationSystemWrapper connectedIsr, boolean isObjectOfInterest)
      throws SvgExportException {
    Shape connectedIS = background.createNewInnerShape(NEIGHBORHOOD_ENTRY, ENTITY_LAYER);
    connectedIS.setPosition(getCVElementXPosition(connectedIsr), getCVElementYPosition(connectedIsr));
    /**
     * Work around, cause the SvgCreator creates a new line if the text is almost as long as the widht of the shape 
     */
    TextShape ts = (TextShape) connectedIS.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.TEXT, TEXT_LAYER);
    ts.setSize(NeighborhoodDiagram.BLOCK_WIDTH + 20, NeighborhoodDiagram.BLOCK_HEIGHT + 20);
    ts.setPosition(NeighborhoodDiagram.BLOCK_WIDTH / 2, NeighborhoodDiagram.BLOCK_HEIGHT / 2 + 5);

    /**
     * CSS
     */
    ts.setShapeCSSEnabled(false);
    ts.setTextValue(addLegendEntry(connectedIsr.getInformationSystemRelease(), NeighborhoodDiagram.BLOCK_WIDTH));
    ts.getDefaultStyle().setAttribute(SvgBaseStyling.FONT_SIZE, NeighborhoodDiagram.DEFAULT_FONT_SIZE);
    ts.getDefaultStyle().setAttribute(SvgBaseStyling.FONT_ALIGN, "middle");
    ts.getDefaultStyle().setAttribute(SvgBaseStyling.FONT_FAMILY, NeighborhoodDiagram.DEFAULT_FONT);
    ts.getDefaultStyle().setAttribute(SvgBaseStyling.FILL_COLOR, COLOR_BLACK);
    ts.setXLink(URLBuilder.getEntityURL(connectedIsr.getInformationSystemRelease(), serverUrl));
    connectedIS.setShapeCSSEnabled(false);
    connectedIS.setTextCSSEnabled(false);
    connectedIS.getDefaultStyle().setAttribute(SvgBaseStyling.FILL_COLOR, neighborhoodDiagram.toHexString(connectedIsr.getColorForStatus(), true));
    connectedIS.getDefaultStyle().setAttribute(SvgBaseStyling.FILL_OPACITY, "1");
    connectedIS.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_COLOR, COLOR_BLACK);

    if (isObjectOfInterest) {
      connectedIS.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_LINE_PATTERN, LINE_PATTERN_STROKE);
      connectedIS.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_WITDH, String.valueOf(NeighborhoodDiagram.OOI_LINE_WIDTH));
    }
    else {
      connectedIS.getDefaultStyle().setAttribute(SvgBaseStyling.STROKE_WITDH, "1");
    }
  }

  private String addLegendEntry(InformationSystemRelease isr, double width) {
    return getSvgNamesLegend().addLegendEntry(isr.getNonHierarchicalName(), isr.getName(),
        MessageAccess.getString(isr.getTypeOfBuildingBlock().getValue()), width, Double.parseDouble(NeighborhoodDiagram.DEFAULT_FONT_SIZE), null);
  }

  /**
   * Add the color legend entries to the diagram
   * @throws SvgExportException
   */
  private void addColorLegendEntrys() throws SvgExportException {
    legendHelperCss(MessageAccess.getString(MESSAGE_CURRENT), SVG_CSS_CURRENT, NeighborhoodDiagram.STATUS_CURRENT_COLOR);
    legendHelperCss(MessageAccess.getString(MESSAGE_PLANNED), SVG_CSS_PLANNED, NeighborhoodDiagram.STATUS_PLANNED_COLOR);
    legendHelperCss(MessageAccess.getString(MESSAGE_TARGET), SVG_CSS_TARGET, NeighborhoodDiagram.STATUS_TARGET_COLOR);
    legendHelperCss(MessageAccess.getString(MESSAGE_INACTIVE), SVG_CSS_INACTIVE, NeighborhoodDiagram.STATUS_INACTIVE_COLOR);
  }

  private void legendHelperCss(String name, String cssClass, Color color) throws SvgExportException {
    final Map<String, String> classProperties = CollectionUtils.hashMap();
    final String colorStr = neighborhoodDiagram.toHexString(color, true);
    if (!getColorToColorClassMap().containsKey(cssClass)) {
      getColorToColorClassMap().put(cssClass, cssClass);
      classProperties.put(SvgBaseStyling.FILL_COLOR, colorStr);
      getSvgDocument().createNewCSSClass(cssClass, classProperties);
    }
    colors.add(cssClass);
    values.add(name);
  }

  private void drawColorLegend(double x, double y) throws SvgExportException {
    this.createColorLegend(COLOR_LEGEND, new Coordinates(x, y), MessageAccess.getString(NeighborhoodDiagram.MESSAGE_TYPE_OF_STATUS), values,
        colors, true, null);
  }

  private double getCVElementXPosition(SpatialInfromationSystemWrapper sInformatioSystemRelease) {
    return sInformatioSystemRelease.getCoordinate().getX() - (NeighborhoodDiagram.BLOCK_WIDTH / 2);
  }

  private double getCVElementYPosition(SpatialInfromationSystemWrapper sInformatioSystemRelease) {
    return sInformatioSystemRelease.getCoordinate().getY() - (NeighborhoodDiagram.BLOCK_HEIGHT / 2);
  }
}

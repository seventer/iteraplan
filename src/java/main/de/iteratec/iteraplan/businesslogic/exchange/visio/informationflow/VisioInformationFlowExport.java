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
package de.iteratec.iteraplan.businesslogic.exchange.visio.informationflow;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.gxl.GXLDocument;
import net.sourceforge.gxl.GXLEdge;
import net.sourceforge.gxl.GXLGraph;
import net.sourceforge.gxl.GXLGraphElement;
import net.sourceforge.gxl.GXLNode;
import net.sourceforge.gxl.GXLString;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.graph.layout.CenterPositionLayout;
import de.iteratec.graph.layout.LayoutOperation;
import de.iteratec.graph.layout.RadialComponentLayout;
import de.iteratec.graph.layout.SpringForceLayout;
import de.iteratec.gxl.GXLUtil;
import de.iteratec.gxl2visio.GxlToVisioConverter;
import de.iteratec.gxl2visio.exceptions.GraphStructureException;
import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.LineDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.informationflow.InformationFlowGeneralHelper;
import de.iteratec.iteraplan.businesslogic.exchange.common.informationflow.VisioInformationFlowTemplateParser;
import de.iteratec.iteraplan.businesslogic.exchange.visio.VisioDimensionExport;
import de.iteratec.iteraplan.businesslogic.exchange.visio.legend.VisioAttributeLegend;
import de.iteratec.iteraplan.businesslogic.exchange.visio.legend.VisioNamesLegend;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.IInformationFlowOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.VisioExportServiceImpl;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.InchConverter;
import de.iteratec.iteraplan.common.util.StringUtil;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TransportInfo;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.visio.model.Document;
import de.iteratec.visio.model.Page;
import de.iteratec.visio.model.Shape;
import de.iteratec.visio.model.exceptions.MasterNotFoundException;
import de.iteratec.visio.model.exceptions.NoSuchElementException;


/**
 * Contains the builder algorithm for the creation of a Visio information flow diagram. This class
 * generates a GXL graph which is then converted to a {@link Document} using the gxl2visio library.
 */
@SuppressWarnings("PMD.ExcessiveImports")
public class VisioInformationFlowExport extends VisioDimensionExport {

  private static final String                   BO_CATEGORY_NAME                        = MessageAccess
                                                                                            .getStringOrNull(Constants.BB_BUSINESSOBJECT_PLURAL);
  private static final String                   IS_CATEGORY_NAME                        = MessageAccess
                                                                                            .getStringOrNull(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL);

  private static final String                   GXL_SCHEMA_URI                          = "http://www.iteratec.de/iteraplan/1/nofile#";

  public static final String                    PROP_ID                                 = "Id";
  public static final String                    PROP_APP_VERSION                        = "ApplicationVersion";
  public static final String                    PROP_APP_NAME                           = "ApplicationName";
  public static final String                    PROP_APP_NAME_FULL                      = "ApplicationNameFull";
  public static final String                    PROP_APP_INFORMATION_OBJECTS            = "InformationObjects";
  public static final String                    PROP_APP_INFORMATION_OBJECTS_FULL       = "InformationObjectsFull";
  public static final String                    PROP_STATUS                             = "PlanningState";
  public static final String                    PROP_DESCRIPTION                        = "Description";
  public static final String                    PROP_START_DATE                         = "StartDate";
  public static final String                    PROP_END_DATE                           = "EndDate";
  public static final String                    PROP_COLOR_RED                          = "Red";
  public static final String                    PROP_COLOR_GREEN                        = "Green";
  public static final String                    PROP_COLOR_BLUE                         = "Blue";
  public static final String                    PROP_APP_BASE_COMPONENTS                = "BaseComponents";
  public static final String                    PROP_APP_INS_DOMAIN                     = "ApplicationDomain";
  public static final String                    PROP_ATTRIBUTES                         = "ApplicationAttributes";

  public static final String                    PROP_LINE_TYPE                          = "LineStyle";
  public static final String                    PROP_FLOW_INFORMATION_OBJECTS           = "InformationObjects";

  private static final String                   VISIO_SHAPE_NAME_APPLICATION_CP_S_BO_BC = "Application-cp-s-Bo-Bc";
  private static final String                   VISIO_SHAPE_NAME_APPLICATION_CP_S_BO    = "Application-cp-s-Bo";
  private static final String                   VISIO_SHAPE_NAME_APPLICATION_CP_S_BC    = "Application-cp-s-Bc";
  private static final String                   VISIO_SHAPE_NAME_APPLICATION_CP_S_SMALL = "Application-cp-s-Small";
  private static final String                   VISIO_SHAPE_NAME_APPLICATION_CP_M_BO_BC = "Application-cp-m-Bo-Bc";
  private static final String                   VISIO_SHAPE_NAME_APPLICATION_CP_M_BO    = "Application-cp-m-Bo";
  private static final String                   VISIO_SHAPE_NAME_APPLICATION_CP_M_BC    = "Application-cp-m-Bc";
  private static final String                   VISIO_SHAPE_NAME_APPLICATION_CP_M_SMALL = "Application-cp-m-Small";
  private static final String                   VISIO_SHAPE_NAME_APPLICATION_CP_L_BO_BC = "Application-cp-l-Bo-Bc";
  private static final String                   VISIO_SHAPE_NAME_APPLICATION_CP_L_BO    = "Application-cp-l-Bo";
  private static final String                   VISIO_SHAPE_NAME_APPLICATION_CP_L_BC    = "Application-cp-l-Bc";
  private static final String                   VISIO_SHAPE_NAME_APPLICATION_CP_L_SMALL = "Application-cp-l-Small";
  private static final String                   VISIO_SHAPE_NAME_COLOR_INDEX_SQUARE     = "Color-Index-Square";
  private static final String                   VISIO_SHAPE_NAME_FLOW                   = "InformationFlow";
  private static final String                   VISIO_SHAPE_NAME_FLOW_BIDIRECTIONAL     = "InformationFlowBidirectional";
  private static final String                   VISIO_SHAPE_NAME_FLOW_NODIRECTION       = "InformationFlowNoDirection";
  private static final String                   VISIO_SHAPE_NAME_DIMCONTENT             = "Legend-Dimension-Content";
  private static final String                   VISIO_SHAPE_NAME_DIMHEADER              = "Legend-Dimension-Header";
  private static final String                   VISIO_SHAPE_NAME_ATTRHEADER             = "Legend-Attribute-Header";
  private static final String                   VISIO_SHAPE_NAME_ATTRCONTENT            = "Legend-Attribute-Content";

  private static final String                   VISIO_TEMPLATE_FILE                     = "/VisioInformationFlowTemplate.vdx";
  private static final String                   VISIO_DIAGRAM_TITLE                     = "iteraplan information flow diagram";

  private static final double                   LEGEND_DESCRIPTION_WIDTH_CM             = 14.5;
  private static final double                   LEGEND_STANDARD_CELL_WIDTH_CM           = 6.5;

  private static final double                   MARGIN_IN                               = InchConverter.cmToInches(1.5);

  private static final double                   APPLICATION_WIDTH                       = InchConverter.cmToInches(2);
  private static final double                   APPLICATION_PADDING_SIDE                = InchConverter.cmToInches(0.4);

  private static final double                   APPLICATION_CP_S_LIMIT                  = 25;
  private static final double                   APPLICATION_CP_M_LIMIT                  = 60;

  private static final Pattern                  IS_ONLY_WHITESPACE_PATTERN              = Pattern.compile("\\s*");

  private final InformationSystemReleaseService isrService;

  private GxlToVisioConverter                   visioDocumentCreator;
  private GXLGraph                              graph;

  private final Map<String, NodeWithProperties> isIdToGxlNode;

  private final List<InformationSystemRelease>  isReleases;
  private final List<Integer>                   isReleaseIds;
  private final Set<InformationSystemInterface> isInterfaces;
  private final Map<String, BusinessObject>     businessObjects;

  private LineDimension                         lineDimension;
  private int[]                                 lineCaptionSelected;
  private Integer                               lineCaptionAttributeId;

  private static final Logger                   LOGGER                                  = Logger.getIteraplanLogger(VisioInformationFlowExport.class);

  private static final boolean                  IS_LEFT_END_SEARCHED_BB                 = false;

  private final IInformationFlowOptions         informationFlowOptions;

  /**
   * Constructor.
   * @param isReleases List of IS release.
   * @param isInterfaces a set of interfaces, which should be shown on the diagram (as edges). If this is null, all interfaces will be shown.
   * @param currentLocale The current Locale.
   */
  public VisioInformationFlowExport(List<InformationSystemRelease> isReleases, Set<InformationSystemInterface> isInterfaces,
      Map<String, BusinessObject> businessObjects, Locale currentLocale, IInformationFlowOptions informationFlowOptions,
      AttributeTypeService attributeTypeService, AttributeValueService attributeValueService, InformationSystemReleaseService isrService) {

    super(attributeTypeService, attributeValueService);
    this.isrService = isrService;
    // copy List of IS releases:
    this.isReleases = Lists.newArrayList();
    this.isReleaseIds = Lists.newArrayList();
    this.isIdToGxlNode = Maps.newHashMap();
    for (InformationSystemRelease isRelease : isReleases) {
      this.isReleases.add(isRelease);
      this.isReleaseIds.add(isRelease.getId());
    }
    this.isInterfaces = isInterfaces;
    setLocale(currentLocale);
    this.informationFlowOptions = informationFlowOptions;
    this.businessObjects = businessObjects;
  }

  private void init() {
    LOGGER.debug("entering init...");

    InputStream visioTemplateStream = null;
    try {
      // initialize VisioDocumentCreator
      visioTemplateStream = VisioExportServiceImpl.class.getResourceAsStream(VISIO_TEMPLATE_FILE);
      visioDocumentCreator = new GxlToVisioConverter(visioTemplateStream, VISIO_DIAGRAM_TITLE);

      // initialize GraphDocument and add GraphType:
      graph = new GXLGraph("informationFlow");
      GXLDocument graphDocument = new GXLDocument();
      graphDocument.getDocumentElement().add(graph);

      this.setTargetPage(visioDocumentCreator.getDocument().getPage(0));

    } catch (IOException iex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, iex);
    } catch (NoSuchElementException nsex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, nsex);
    } catch (ParserConfigurationException pce) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, pce);
    } catch (SAXException saxe) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, saxe);
    } finally {
      if (visioTemplateStream != null) {
        try {
          visioTemplateStream.close();
        } catch (IOException e) {
          LOGGER.error("Failed to close the visio template stream.");
        }
      }
    }

    LOGGER.debug("leaving init...");
  }

  @Override
  public Document createDiagram() {

    init();

    if (informationFlowOptions.isUseNamesLegend()) {
      setVisioNamesLegend(new VisioNamesLegend(this.getTargetPage()));
    }

    setColorDimension(createColorDimension(informationFlowOptions.getColorOptionsBean(), TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));

    lineDimension = createLineDimension(informationFlowOptions.getLineOptionsBean(), TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);

    lineCaptionSelected = informationFlowOptions.getSelectionType();
    lineCaptionAttributeId = informationFlowOptions.getLineCaptionSelectedAttributeId();

    addMissingParentNodes();
    addResultNodes();
    groupNodes();

    for (GXLNode node : GXLUtil.getNodes(graph)) {
      setIsrNodeTexts(node);
    }

    addEdges();

    try {
      LOGGER.debug("trying to add graph to gxl2visio converter");

      List<LayoutOperation> layoutOperations = determineLayoutOperations();

      Rectangle2D graphAreaBounds = visioDocumentCreator.addGraph(graph, layoutOperations);
      // correct the position of the bounding box, since it's returned incorrectly by the "addGraph"-method
      graphAreaBounds.setRect(0, 0, graphAreaBounds.getWidth(), graphAreaBounds.getHeight());

      Shape title = createDiagramTitle(MessageAccess.getStringOrNull("graphicalExport.informationflow.title", getLocale()));
      List<Shape> queryInfo = createQueryInfo(graphAreaBounds);

      Rectangle2D legendsBox = createLegends(graphAreaBounds);

      setTitlePos(graphAreaBounds, title, queryInfo);
      setQueryInfoPos(queryInfo, title.getPinX(), title.getPinY() - getQueryInfoHeight(queryInfo));

      Point2D adjustment = adjustPage(graphAreaBounds, title, queryInfo, legendsBox);
      legendsBox = new Rectangle2D.Double(legendsBox.getX() + adjustment.getX(), legendsBox.getY() + adjustment.getY(), legendsBox.getWidth(),
          legendsBox.getHeight());

      createGeneratedInformation(this.getTargetPage().getWidth());
      createLogos(0, 0, this.getTargetPage().getWidth(), this.getTargetPage().getHeight());

      if (informationFlowOptions.isUseNamesLegend()) {
        createNamesLegend(legendsBox);
      }

    } catch (GraphStructureException gex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, gex);
    } catch (MasterNotFoundException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
    return visioDocumentCreator.getDocument();
  }

  private void createNamesLegend(Rectangle2D legendsBox) throws MasterNotFoundException {
    double frameX = legendsBox.getX();
    double frameY = legendsBox.getY() - getTargetPage().getHeight() + MARGIN_IN;
    double frameWidth = legendsBox.getWidth();
    double frameHeight = getTargetPage().getHeight() - legendsBox.getHeight() - DISTANCE_TO_MARGIN_INCHES;
    createNamesLegend(frameX, frameY, frameWidth, frameHeight, informationFlowOptions.isNakedExport(),
        MessageAccess.getStringOrNull("graphicalExport.informationflow.title", getLocale()));
  }

  private void setTitlePos(Rectangle2D graphAreaBounds, Shape title, List<Shape> queryInfo) {
    double titleTopY = graphAreaBounds.getY() + graphAreaBounds.getHeight() + DISTANCE_TO_MARGIN_INCHES * 2.8 + title.getHeight()
        + getQueryInfoHeight(queryInfo);
    setTitlePosAndSize(title, graphAreaBounds.getX(), titleTopY, null);
  }

  /**
   * Adjusts the page size to the containing shapes, with a margin, and also moves all shapes on the page
   * so their positions are actually within the page bounds. 
   */
  private Point2D adjustPage(Rectangle2D graphAreaBounds, Shape title, List<Shape> queryInfo, Rectangle2D legendsBox) {
    double pageWidth = graphAreaBounds.getWidth() + 3 * MARGIN_IN + legendsBox.getWidth();
    double graphAreaPlusTitleHeight = graphAreaBounds.getHeight() + title.getHeight() + getQueryInfoHeight(queryInfo) + MARGIN_IN;
    double pageHeight = Math.max(graphAreaPlusTitleHeight, legendsBox.getHeight()) + 2 * MARGIN_IN;
    getTargetPage().setSize(pageWidth, pageHeight);

    double deltaY = pageHeight - graphAreaPlusTitleHeight - MARGIN_IN;
    for (Shape shape : getTargetPage().getShapes()) {
      shape.setPosition(shape.getPinX() + MARGIN_IN, shape.getPinY() + deltaY);
    }

    return new Point2D.Double(MARGIN_IN, deltaY);
  }

  private Rectangle2D createLegends(Rectangle2D bounds) throws MasterNotFoundException {
    Page page = getTargetPage();
    double legendsBlockLeftX = bounds.getWidth() + MARGIN_IN;
    double legendsBlockTopY = bounds.getY() + bounds.getHeight() + MARGIN_IN;

    Coordinates position = new Coordinates(legendsBlockLeftX, legendsBlockTopY);

    Shape descriptionLegend = createDescriptionLegend(informationFlowOptions, page, position);
    double legendsBlockWidth = descriptionLegend.getWidth();
    double legendsBlockHeight = descriptionLegend.getHeight();

    position.incX(InchConverter.cmToInches(LEGEND_DESCRIPTION_WIDTH_CM - LEGEND_STANDARD_CELL_WIDTH_CM));
    position.incY(-(MARGIN_IN + descriptionLegend.getHeight()));
    legendsBlockHeight += MARGIN_IN;

    ColorDimensionOptionsBean colorsBean = informationFlowOptions.getColorOptionsBean();
    if (GraphicalExportBaseOptions.NOTHING_SELECTED != colorsBean.getDimensionAttributeId().intValue()) {
      double colorLegendHeight = createColorLegend(colorsBean, page, position, VISIO_SHAPE_NAME_COLOR_INDEX_SQUARE,
          getFieldValueFromDimension(getColorDimension()), TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
      position.incY(-(MARGIN_IN + colorLegendHeight));
      legendsBlockHeight += colorLegendHeight + MARGIN_IN;
    }

    LineDimensionOptionsBean lineTypeBean = informationFlowOptions.getLineOptionsBean();
    if (GraphicalExportBaseOptions.NOTHING_SELECTED != lineTypeBean.getDimensionAttributeId().intValue()) {
      legendsBlockHeight += createLineTypeLegend(lineTypeBean, page, position, VISIO_SHAPE_NAME_LINE_FIELD,
          getFieldValueFromDimension(lineDimension), TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    }
    return new Rectangle2D.Double(legendsBlockLeftX, legendsBlockTopY, legendsBlockWidth, legendsBlockHeight);
  }

  private Shape createDescriptionLegend(IInformationFlowOptions infoFlowOptions, Page page, final Coordinates position)
      throws MasterNotFoundException {

    String descriptionTypeName = InformationFlowGeneralHelper.getDescriptionTypeName(getAttributeTypeService(), lineCaptionSelected,
        lineCaptionAttributeId, informationFlowOptions, getLocale());

    // Create container
    Shape visioLegendContainter = page.createNewShape(VisioAttributeLegend.VISIO_SHAPE_NAME_LEGEND_GROUP_CONTAINER);

    double legendHeight;
    double legendWidth;
    int rowCount = 4;

    // create dimension column
    // ***********************
    Shape shape = visioLegendContainter.createNewInnerShape(VISIO_SHAPE_NAME_DIMHEADER);

    shape.setFieldValue(MessageAccess.getStringOrNull("graphicalReport.headline", getLocale()));

    legendHeight = rowCount * shape.getHeight();

    double pinYHead = legendHeight - shape.getHeight();
    double pinXHead = 0;
    double heightHead = shape.getHeight();

    shape.setPosition(pinXHead, pinYHead);

    shape = visioLegendContainter.createNewInnerShape(VISIO_SHAPE_NAME_DIMCONTENT);
    shape.setFieldValue(MessageAccess.getStringOrNull("reports.color", getLocale()));

    shape.setPosition(pinXHead, pinYHead - heightHead);

    double widthFirstRow = shape.getWidth();
    double pinY = shape.getPinY();
    double height = shape.getHeight();
    legendWidth = widthFirstRow;

    shape = visioLegendContainter.createNewInnerShape(VISIO_SHAPE_NAME_DIMCONTENT);
    shape.setFieldValue(MessageAccess.getStringOrNull("reports.lineType", getLocale()));
    shape.setPosition(pinXHead, pinY - height);

    shape = visioLegendContainter.createNewInnerShape(VISIO_SHAPE_NAME_DIMCONTENT);
    shape.setFieldValue(MessageAccess.getStringOrNull("reports.lineCaption", getLocale()));
    shape.setPosition(pinXHead, pinY - 2 * height);

    // create attribute column
    // ***********************
    shape = visioLegendContainter.createNewInnerShape(VISIO_SHAPE_NAME_ATTRHEADER);
    shape.setFieldValue(InformationFlowGeneralHelper.headerAttributeOrBuilding(getLocale()));

    double pinXWithOffset = pinXHead + widthFirstRow;

    shape.setPosition(pinXWithOffset, pinYHead);
    double pinY2 = shape.getPinY();
    double height2 = shape.getHeight();

    shape = visioLegendContainter.createNewInnerShape(VISIO_SHAPE_NAME_ATTRCONTENT);

    String colorAttributeName = InformationFlowGeneralHelper.replaceBlank(getFieldValueFromDimension(createColorDimension(
        infoFlowOptions.getColorOptionsBean(), TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE)));
    shape.setFieldValue(colorAttributeName);
    shape.setPosition(pinXWithOffset, pinY2 - height2);

    shape = visioLegendContainter.createNewInnerShape(VISIO_SHAPE_NAME_ATTRCONTENT);
    String lineAttributeName = InformationFlowGeneralHelper.replaceBlank(getFieldValueFromDimension(createLineDimension(
        infoFlowOptions.getLineOptionsBean(), TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE)));
    shape.setFieldValue(lineAttributeName);
    shape.setPosition(pinXWithOffset, pinY2 - 2 * height2);

    shape = visioLegendContainter.createNewInnerShape(VISIO_SHAPE_NAME_ATTRCONTENT);
    shape.setFieldValue(descriptionTypeName);
    shape.setPosition(pinXWithOffset, pinY2 - 3 * height2);

    legendWidth = legendWidth + shape.getWidth();

    visioLegendContainter.setSize(legendWidth, legendHeight);
    visioLegendContainter.setPosition(position.getX(), position.getY() - legendHeight);

    return visioLegendContainter;
  }

  private List<LayoutOperation> determineLayoutOperations() {
    List<LayoutOperation> layoutOps = Lists.newArrayList();

    if (informationFlowOptions.getSelectedTemplateFile() != null) {
      VisioInformationFlowTemplateParser parser = new VisioInformationFlowTemplateParser(informationFlowOptions.getSelectedTemplateFile());
      parser.parseTemplate();
      layoutOps.add(new CopyFromTemplateLayout(parser.getNodeMap()));
      layoutOps.add(new CenterPositionLayout());
    }
    else {
      layoutOps.add(new RadialComponentLayout());
    }

    layoutOps.add(new SpringForceLayout(SpringForceLayout.DEFAULT_DIN_A_CONFIG));
    return layoutOps;
  }

  private List<Shape> createQueryInfo(Rectangle2D bounds) throws MasterNotFoundException {
    List<Shape> queryInfo = null;
    if (informationFlowOptions.isShowSavedQueryInfo()) {
      Coordinates pos = new Coordinates(0, 0);
      double widthInCm = InchConverter.inchesToCm(bounds.getWidth());
      queryInfo = createSavedQueryInfo(pos, widthInCm, 10, informationFlowOptions.getSavedQueryInfo(), informationFlowOptions.getServerUrl());
    }
    return queryInfo;
  }

  /**
   * Adds necessary nodes to the graph for all parent IS releases that have a child IS release in
   * the List of IS release Ids, but are not contained in the List itself.
   */
  public void addMissingParentNodes() {
    LOGGER.debug("entering addMissingParentNodes...");

    List<Integer> missingIsReleaseIds = Lists.newArrayList();

    // gather Ids of IS release parent elements not contained in the List of
    // IS release Ids:
    for (InformationSystemRelease release : isReleases) {

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("adding parent nodes for IS release: " + release.getNonHierarchicalName());
      }

      InformationSystemRelease parent = release.getParent();

      while (parent != null) {

        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("current parent: " + parent.getNonHierarchicalName());
        }

        Integer parentId = parent.getId();

        if (!isReleaseIds.contains(parentId) && !missingIsReleaseIds.contains(parentId)) {
          missingIsReleaseIds.add(parentId);
        }
        parent = parent.getParent();
      }
    }

    LOGGER.debug("finished iterating through parents...");

    for (Integer id : missingIsReleaseIds) {
      InformationSystemRelease isRelease = isrService.loadObjectById(id);
      addIsrNode(isRelease);
    }

    LOGGER.debug("leaving addMissingParentNodes...");
  }

  /**
   * Adds all necessary result nodes to the graph for the List of IS releases.
   */
  public void addResultNodes() {
    LOGGER.debug("entering addResultNodes...");

    for (InformationSystemRelease isRelease : isReleases) {
      addIsrNode(isRelease);
    }

    LOGGER.debug("leaving addResultNodes...");
  }

  /**
   * Adds a given InformationSystemRelease to the resulting graph.
   * @param isRelease The InformationSystemRelease to add.
   */
  private void addIsrNode(InformationSystemRelease isRelease) {
    // Get the list of child business objects/isr2BoAssociations  filtered by a given Map of BusinessObjects 
    Set<Isr2BoAssociation> isr2BoAssociationSet = GeneralHelper.filterAbstractAssociationsByBusinessObjects(isRelease, businessObjects,
        IS_LEFT_END_SEARCHED_BB);
    // Get the list of child base components
    Set<InformationSystemRelease> bcSet = isRelease.getBaseComponents();

    String nodeId = String.valueOf(isRelease.getId());
    GXLNode node = new GXLNode(nodeId);

    // distinguish between IS releases with and without child business objects and base components
    // and create the graph node (shape) accordingly. We set the standard node type here. Later on
    // the node type might get changed (see method adjustNodeMasterShapes()) if the number of
    // interfaces exceeds certain limits.
    if ((!informationFlowOptions.isShowIsBusinessObjects() || CollectionUtils.isEmpty(isr2BoAssociationSet))
        && (!informationFlowOptions.isShowIsBaseComponents() || CollectionUtils.isEmpty(bcSet))) {
      // neither business objects nor base components
      node.setType(createGxlTypeURI(VISIO_SHAPE_NAME_APPLICATION_CP_S_SMALL));
    }
    else if (!informationFlowOptions.isShowIsBusinessObjects() || CollectionUtils.isEmpty(isr2BoAssociationSet)) {
      // no business objects (only base components)
      node.setType(createGxlTypeURI(VISIO_SHAPE_NAME_APPLICATION_CP_S_BC));
    }
    else if (!informationFlowOptions.isShowIsBaseComponents() || CollectionUtils.isEmpty(bcSet)) {
      // no base components (only business objects)
      node.setType(createGxlTypeURI(VISIO_SHAPE_NAME_APPLICATION_CP_S_BO));
    }
    else {
      // both
      node.setType(createGxlTypeURI(VISIO_SHAPE_NAME_APPLICATION_CP_S_BO_BC));
    }

    // The attributes of the node which are relevant for the name legend are set later on, when the
    // width of the shape can be estimated.
    String isName = isRelease.getInformationSystem().getName();
    if (!isOnlyWhitespace(isRelease.getVersion())) {
      isName += " # " + isRelease.getVersion();
    }

    NodeWithProperties nodeWrapper = new NodeWithProperties(node, StringUtil.encodeVisioXml(isName),
        GeneralHelper.makeConcatenatedNameStringForAssociationCollection(isr2BoAssociationSet, false, true, false),
        GeneralHelper.makeConcatenatedNameStringForBbCollection(isRelease.getBaseComponents()));

    isIdToGxlNode.put(nodeId, nodeWrapper);

    addShapePropertiesToInsNode(isRelease, node, isName);

    graph.add(node);
  }

  /**
   * Escapes the passed text to make it appropriate for Visio, and sets it as a String attribute to the node or edge.
   */
  private void setTextPropertyToNode(GXLGraphElement nodeOrEdge, String property, String text) {
    String escapedText = StringUtil.encodeVisioXml(text);
    nodeOrEdge.setAttr(property, new GXLString(escapedText));
  }

  /**
   * Adds a defined set of custom properties to the GLXNode.
   * These properties will be exported to the Visio shapes as custom properties.
   * 
   * @param isRelease An InformationSystemRelease containing the attributes to add to the GLXNode.
   * @param node The corresponding graph node for the isRelease.
   * @param isName The formated name of the isRelease.
   */
  private void addShapePropertiesToInsNode(InformationSystemRelease isRelease, GXLNode node, String isName) {

    setTextPropertyToNode(node, PROP_ID, isRelease.getId().toString());
    setTextPropertyToNode(node, PROP_APP_NAME_FULL, isName);
    setTextPropertyToNode(node, PROP_APP_INFORMATION_OBJECTS_FULL,
        GeneralHelper.makeConcatenatedNameStringForBbCollection(isRelease.getBusinessObjects()));
    String urlToEntity = this.retrieveXLinkUrlForIdentityEntity(isRelease, this.informationFlowOptions.getServerUrl());
    setTextPropertyToNode(node, PROP_XREF_URL, urlToEntity);

    // create a string containing all INS Domains, separated by a semicolon
    StringBuilder isdAttribute = new StringBuilder();
    // check if the user has the permission to read the INS Domains:
    if (UserContext.getCurrentPerms().userHasFunctionalPermission(TypeOfFunctionalPermission.INFORMATIONSYSTEMDOMAIN)) {
      for (InformationSystemDomain isd : isRelease.getInformationSystemDomains()) {
        isdAttribute.append(isd.getName() + "(" + isd.getId() + ");");
      }
    }
    setTextPropertyToNode(node, PROP_APP_INS_DOMAIN, isdAttribute.toString());

    // create a string containing all assigned attributes, separated by a semicolon
    StringBuilder attributesString = new StringBuilder();
    Set<AttributeValueAssignment> avas = isRelease.getAttributeValueAssignments();
    for (AttributeValueAssignment ava : avas) {
      AttributeValue av = ava.getAttributeValue();
      AttributeType at = av.getAbstractAttributeType();
      AttributeTypeGroup atg = at.getAttributeTypeGroup();
      // check if the user has the permission to read the attributeGroup:
      if (UserContext.getCurrentPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ)) {
        attributesString.append("(" + at.getName() + ")=" + av.getLocalizedValueString(getLocale()) + ";");
      }
    }
    setTextPropertyToNode(node, PROP_ATTRIBUTES, attributesString.toString());

    setTextPropertyToNode(node, PROP_APP_VERSION, isRelease.getVersion());
    setTextPropertyToNode(node, PROP_STATUS, mapTypeOfStatusVisioExport(isRelease.getTypeOfStatus()));
    setTextPropertyToNode(node, PROP_DESCRIPTION, isRelease.getDescription());
    setTextPropertyToNode(node, PROP_START_DATE, DateUtils.formatAsStringToDefault(isRelease.runtimeStartsAt(), getLocale()));
    setTextPropertyToNode(node, PROP_END_DATE, DateUtils.formatAsStringToDefault(isRelease.runtimeEndsAt(), getLocale()));

    Color color = getColorDimension().getValue(isRelease);
    LOGGER.debug("Color for node: {0}", color);

    setTextPropertyToNode(node, PROP_COLOR_RED, String.valueOf(color.getRed()));
    setTextPropertyToNode(node, PROP_COLOR_GREEN, String.valueOf(color.getGreen()));
    setTextPropertyToNode(node, PROP_COLOR_BLUE, String.valueOf(color.getBlue()));
  }

  /**
   * Maps the Typeofstatus to Visio export Strings
   *
   * @param typeOfStatus
   *          The TypeofStatus to map.
   * @return The Visio String representation.
   */
  private String mapTypeOfStatusVisioExport(TypeOfStatus typeOfStatus) {
    String status = typeOfStatus.toString();
    int dotIndex = status.indexOf('_');
    return status.substring(dotIndex + 1);
  }

  /**
   * Adds all edges to the graph.
   */
  public void addEdges() {
    LOGGER.debug("entering addEdges()");

    List<InformationSystemInterface> interfaces = Lists.newArrayList();
    List<Integer> interfacesIds = Lists.newArrayList();

    // The map that stores the edge count (greater or equal than the interface count) for every
    // information system.
    // We use a one-value integer array in this map instead of an Integer object,
    // because Integer objects are immutable and would cost performance.
    Map<Integer, int[]> releaseIdToEdgeCount = Maps.newHashMap();

    for (InformationSystemRelease isRelease : isReleases) {
      releaseIdToEdgeCount.put(isRelease.getId(), new int[] { 0 });

      // filter interfaces
      Set<InformationSystemInterface> filteredInterfaces = isRelease.getAllConnections();
      if (isInterfaces != null) {
        filteredInterfaces = Sets.intersection(filteredInterfaces, isInterfaces);
      }

      // retrieves all connections of the isRelease and stores them in a List
      for (InformationSystemInterface iface : filteredInterfaces) {
        if (isReleaseIds.contains(iface.getInformationSystemReleaseA().getId())
            && isReleaseIds.contains(iface.getInformationSystemReleaseB().getId()) && !interfacesIds.contains(iface.getId())) {
          interfaces.add(iface);
          interfacesIds.add(iface.getId());
        }
      }
    }

    // add edges for all found interfaces:
    for (InformationSystemInterface iface : interfaces) {
      // Edges are only added if both "from" and "to" IS release are in the List of IS release Ids.
      Integer isReleaseAId = iface.getInformationSystemReleaseA().getId();
      Integer isReleaseBId = iface.getInformationSystemReleaseB().getId();
      if (isReleaseIds.contains(isReleaseAId) && isReleaseIds.contains(isReleaseBId)) {
        int newEdgesCount = addEdgesForInterfaces(iface);
        releaseIdToEdgeCount.get(isReleaseAId)[0] += newEdgesCount;
        releaseIdToEdgeCount.get(isReleaseAId)[0] += newEdgesCount;
      }
    }

    // releaseId to edge count now contains the number of edges per node
    adjustNodeMasterShapes(releaseIdToEdgeCount);

    LOGGER.debug("leaving addEdges()");
  }

  private void adjustNodeMasterShapes(Map<Integer, int[]> releaseIdToEdgeCount) {
    for (Entry<Integer, int[]> entry : releaseIdToEdgeCount.entrySet()) {
      Integer releaseId = entry.getKey();
      String releaseIdStr = releaseId.toString();
      NodeWithProperties infoSysNodeProperties = isIdToGxlNode.get(releaseIdStr);
      GXLNode node = infoSysNodeProperties.getNode();
      // a single-value array
      int[] edgeCount = entry.getValue();

      if (edgeCount[0] > APPLICATION_CP_M_LIMIT) {
        // set type to large in accordance with the BO string
        if (isOnlyWhitespace(infoSysNodeProperties.getBusinessObjects()) && isOnlyWhitespace(infoSysNodeProperties.getBaseComponents())) {
          // neither business objects nor base components
          node.setType(createGxlTypeURI(VISIO_SHAPE_NAME_APPLICATION_CP_L_SMALL));
        }
        else if (!isOnlyWhitespace(infoSysNodeProperties.getBaseComponents())) {
          // no business objects (only base components)
          node.setType(createGxlTypeURI(VISIO_SHAPE_NAME_APPLICATION_CP_L_BC));
        }
        else if (!isOnlyWhitespace(infoSysNodeProperties.getBusinessObjects())) {
          // no base components (only business objects)
          node.setType(createGxlTypeURI(VISIO_SHAPE_NAME_APPLICATION_CP_L_BO));
        }
        else {
          // both
          node.setType(createGxlTypeURI(VISIO_SHAPE_NAME_APPLICATION_CP_L_BO_BC));
        }
      }
      if ((edgeCount[0] > APPLICATION_CP_S_LIMIT) && (edgeCount[0] <= APPLICATION_CP_M_LIMIT)) {
        // set the type to medium
        if (isOnlyWhitespace(infoSysNodeProperties.getBusinessObjects()) && isOnlyWhitespace(infoSysNodeProperties.getBaseComponents())) {
          // no business objects and base components
          node.setType(createGxlTypeURI(VISIO_SHAPE_NAME_APPLICATION_CP_M_SMALL));
        }
        else if (!isOnlyWhitespace(infoSysNodeProperties.getBaseComponents())) {
          // no business objects (only base components)
          node.setType(createGxlTypeURI(VISIO_SHAPE_NAME_APPLICATION_CP_M_BC));
        }
        else if (!isOnlyWhitespace(infoSysNodeProperties.getBusinessObjects())) {
          // no base components (only business objects)
          node.setType(createGxlTypeURI(VISIO_SHAPE_NAME_APPLICATION_CP_M_BO));
        }
        else {
          // both
          node.setType(createGxlTypeURI(VISIO_SHAPE_NAME_APPLICATION_CP_M_BO_BC));
        }
      }
    }
  }

  private static URI createGxlTypeURI(String typeIdentifier) {
    try {
      return new URI(GXL_SCHEMA_URI + typeIdentifier);
    } catch (URISyntaxException e) {
      // should never happen
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  private static boolean isOnlyWhitespace(String str) {
    return IS_ONLY_WHITESPACE_PATTERN.matcher(str).matches();
  }

  /**
   * Adds edges to the graph for all transports of a given Connection.
   * @param iface The connection to assign to the graph.
   * @return The count of edges.
   */
  @SuppressWarnings("boxing")
  private int addEdgesForInterfaces(InformationSystemInterface iface) {

    List<String> bidirectionalBOs = Lists.newArrayList();
    List<String> firstToSecondBOs = Lists.newArrayList();
    List<String> secondToFirstBOs = Lists.newArrayList();
    List<String> noDirectionBOs = Lists.newArrayList();

    addTransportedBusinessObjects(iface, bidirectionalBOs, firstToSecondBOs, secondToFirstBOs, noDirectionBOs);

    Map<String, String> edgeProps = Maps.newHashMap();

    edgeProps.put(PROP_ID, iface.getId().toString());
    edgeProps.put(PROP_XREF_URL, this.retrieveXLinkUrlForIdentityEntity(iface, this.informationFlowOptions.getServerUrl()));

    // TODO currently there is no status information implemented for interfaces:
    edgeProps.put(PROP_STATUS, "current");

    edgeProps.put(PROP_DESCRIPTION, iface.getDescription());

    StringBuilder attributesString = initAttributeString(iface.getAttributeValueAssignments());
    edgeProps.put(PROP_ATTRIBUTES, attributesString.toString());
    edgeProps.put(PROP_LINE_TYPE, String.valueOf(VisioDimensionExport.getVisioLinePattern(lineDimension.getValue(iface))));

    return calculateEdgeCount(edgeProps, iface, bidirectionalBOs, firstToSecondBOs, secondToFirstBOs, noDirectionBOs);
  }

  /**
   * @param edgeProps
   * @param noDirectionBOs
   * @param secondToFirstBOs
   * @param firstToSecondBOs
   * @param bidirectionalBOs
   * @param iface
   * 
   * @return the edge count
   */
  private int calculateEdgeCount(Map<String, String> edgeProps, InformationSystemInterface iface, List<String> bidirectionalBOs,
                                 List<String> firstToSecondBOs, List<String> secondToFirstBOs, List<String> noDirectionBOs) {
    List<Direction> directions = Lists.newArrayList();
    List<String> transportsLabels = Lists.newArrayList();

    if (ArrayUtils.contains(lineCaptionSelected, InformationFlowOptionsBean.LINE_DESCR_BUSINESS_OBJECTS)) {
      addTransportLabelAndDirection(transportsLabels, directions, bidirectionalBOs, Direction.BOTH_DIRECTIONS);
      addTransportLabelAndDirection(transportsLabels, directions, noDirectionBOs, Direction.NO_DIRECTION);
      addTransportLabelAndDirection(transportsLabels, directions, firstToSecondBOs, Direction.FIRST_TO_SECOND);
      addTransportLabelAndDirection(transportsLabels, directions, secondToFirstBOs, Direction.SECOND_TO_FIRST);
      if (transportsLabels.isEmpty() && directions.isEmpty()) {
        transportsLabels.add("");
        directions.add(Direction.NO_DIRECTION);
      }
    }
    else {
      transportsLabels.add("");
      directions.add(iface.getInterfaceDirection());
    }

    int edgeCount = transportsLabels.size();

    List<String> commonTransportLabels = determineCommonLabels(iface);

    // add edges
    for (int i = 0; i < edgeCount; i++) {
      List<String> allEdgeLabels = Lists.newArrayList(commonTransportLabels);
      allEdgeLabels.add(transportsLabels.get(i));
      allEdgeLabels.removeAll(Lists.newArrayList(""));

      edgeProps.put(PROP_FLOW_INFORMATION_OBJECTS, StringUtils.join(allEdgeLabels, "; "));

      addEdgeWithDirection(iface, directions.get(i), edgeProps);
    }

    return edgeCount;
  }

  private List<String> determineCommonLabels(InformationSystemInterface iface) {
    List<String> commonTransportLabels = Lists.newArrayList();
    for (int lineCaption : this.lineCaptionSelected) {
      String prefix = "";
      switch (lineCaption) {

        case InformationFlowOptionsBean.LINE_DESCR_TECHNICAL_COMPONENTS:
          List<String> referencedTcrs = InformationFlowGeneralHelper.getReferencedTcReleaseNames(iface);
          if (!referencedTcrs.isEmpty()) {
            prefix = MessageAccess.getStringOrNull("graphicalExport.informationflow.technicalComponent.abbreviation") + ": ";
            commonTransportLabels.add(prefix + GeneralHelper.makeConcatenatedStringWithSeparator(referencedTcrs, Constants.BUILDINGBLOCKSEP));
          }
          break;

        case InformationFlowOptionsBean.LINE_DESCR_ATTRIBUTES:
          List<String> resultValues = InformationFlowGeneralHelper.getLabelDescrForAttribute(getAttributeTypeService(), iface,
              this.lineCaptionAttributeId);
          prefix = MessageAccess.getStringOrNull("graphicalExport.informationflow.attribute.abbreviation") + ": ";
          commonTransportLabels.add(prefix + GeneralHelper.makeConcatenatedStringWithSeparator(resultValues, Constants.BUILDINGBLOCKSEP));
          break;

        case InformationFlowOptionsBean.LINE_DESCR_DESCRIPTION:
          prefix = MessageAccess.getStringOrNull("graphicalExport.informationflow.description.abbreviation") + ": ";
          commonTransportLabels.add(prefix + iface.getDescription());
          break;

        case InformationFlowOptionsBean.LINE_DESCR_NAME:
          if (!iface.getName().isEmpty()) {
            prefix = MessageAccess.getStringOrNull("graphicalExport.informationflow.name.abbreviation") + ": ";
            commonTransportLabels.add(prefix + iface.getName());
          }
          break;

        default: // Business Objects handles separately
      }
    }
    return commonTransportLabels;
  }

  private void addTransportLabelAndDirection(List<String> businessObjectsLabels, List<Direction> directions, List<String> listOfObjects,
                                             Direction direction) {
    if (!listOfObjects.isEmpty()) {
      String prefix = MessageAccess.getStringOrNull("graphicalExport.informationflow.businessObject.abbreviation") + ": ";
      businessObjectsLabels.add(prefix + GeneralHelper.makeConcatenatedStringWithSeparator(listOfObjects, Constants.BUILDINGBLOCKSEP));
      directions.add(direction);
    }
  }

  /**
   * Prepares a string from the AttributeValueAssignment
   * 
   * @return StringBuilder for the string
   */
  private StringBuilder initAttributeString(Set<AttributeValueAssignment> avas) {
    StringBuilder attributesString = new StringBuilder();

    for (AttributeValueAssignment ava : avas) {
      AttributeValue av = ava.getAttributeValue();
      AttributeType at = av.getAbstractAttributeType();
      AttributeTypeGroup atg = at.getAttributeTypeGroup();

      // check if the user has the permission to read the attributeGroup:
      if (UserContext.getCurrentPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ)) {
        attributesString.append('(');
        attributesString.append(at.getName());
        attributesString.append(")=");
        attributesString.append(av.getLocalizedValueString(getLocale()));
        attributesString.append(';');
      }
    }

    return attributesString;
  }

  /**
   * Adds edges to the graph with the given direction
   * @param iface
   * @param direction
   * @param edgeProps
   */
  private void addEdgeWithDirection(InformationSystemInterface iface, Direction direction, Map<String, String> edgeProps) {

    // The default is a shape with no direction.
    EdgeParameters edgeParams = new EdgeParameters(VISIO_SHAPE_NAME_FLOW_NODIRECTION, iface, edgeProps);

    if (Direction.BOTH_DIRECTIONS.equals(direction)) {
      edgeParams = new EdgeParameters(VISIO_SHAPE_NAME_FLOW_BIDIRECTIONAL, iface, edgeProps);
    }
    else if (Direction.FIRST_TO_SECOND.equals(direction)) {
      edgeParams = new EdgeParameters(VISIO_SHAPE_NAME_FLOW, iface, true, edgeProps);
    }
    else if (Direction.SECOND_TO_FIRST.equals(direction)) {
      edgeParams = new EdgeParameters(VISIO_SHAPE_NAME_FLOW, iface, false, edgeProps);
    }

    addEdge(edgeParams.masterFlow, edgeParams.releaseAId, edgeParams.releaseBId, edgeParams.edgeProps);
  }

  /**
   * 
   * @param connection
   * @param bidirectionalBOs
   * @param firstToSecondBOs
   * @param secondToFirstBOs
   * @param noDirectionBOs
   */
  private void addTransportedBusinessObjects(InformationSystemInterface connection, List<String> bidirectionalBOs, List<String> firstToSecondBOs,
                                             List<String> secondToFirstBOs, List<String> noDirectionBOs) {
    for (Transport transport : Lists.newArrayList(connection.getTransports())) {

      String transportedBusinessObject = transport.getBusinessObject().getName();
      TransportInfo transportInfo = transport.getTransportInfo();

      switch (transportInfo) {
        case BOTH_DIRECTIONS:
          bidirectionalBOs.add(transportedBusinessObject);
          break;
        case FIRST_TO_SECOND:
          firstToSecondBOs.add(transportedBusinessObject);
          break;
        case SECOND_TO_FIRST:
          secondToFirstBOs.add(transportedBusinessObject);
          break;
        default: // No Direction
          noDirectionBOs.add(transportedBusinessObject);
      }
    }
  }

  /**
   * Group the graph nodes in a hierarchical fashion (if necessary). For every node in the nodes[]
   * it is checked whether the corresponding IS release has a parent. If yes, the node is copied to
   * the list of children and removed from the graph later on.
   */
  public void groupNodes() {
    LOGGER.debug("entering groupNodes...");

    Set<GXLNode> nodes = GXLUtil.getNodes(graph);

    // nodesMap maps nodeIds to nodes:
    Map<Integer, GXLNode> nodesMap = Maps.newHashMap();
    for (GXLNode node : nodes) {
      Integer nodeId = Integer.valueOf(node.getID());
      nodesMap.put(nodeId, node);
    }

    // check for all nodes if their corresponding IS releases have parents:
    for (GXLNode node : nodes) {
      Integer nodeId = Integer.valueOf(node.getID());
      InformationSystemRelease isRelease = isrService.loadObjectById(nodeId);
      if (isRelease.getParent() != null) {
        Integer parentNodeId = isRelease.getParent().getId();
        GXLNode parentNode = nodesMap.get(parentNodeId);

        GXLGraph innerGraph;
        if (parentNode.getGraphCount() == 0) {
          innerGraph = new GXLGraph("subsystems" + parentNodeId);
          parentNode.add(innerGraph);
        }
        else {
          innerGraph = parentNode.getGraphAt(0);
        }
        // move node into outer node
        graph.remove(node);
        innerGraph.add(node);
        nodesMap.put(nodeId, node);
      }
    }
    LOGGER.debug("leaving groupNodes...");
  }

  /**
   * Adds a new connection edge with properties to the resulting graph.
   * @param shapeName
   * @param fromId
   * @param toId
   * @param edgeProps
   */
  private void addEdge(String shapeName, Integer fromId, Integer toId, Map<String, String> edgeProps) {
    GXLEdge edge = new GXLEdge(String.valueOf(fromId), String.valueOf(toId));

    edge.setType(createGxlTypeURI(shapeName));

    for (Map.Entry<String, String> edgeProperty : edgeProps.entrySet()) {
      String prop = "";
      if (edgeProperty.getValue() != null) {
        prop = edgeProperty.getValue();
      }
      setTextPropertyToNode(edge, edgeProperty.getKey(), prop);
    }

    graph.add(edge);
  }

  /**
   * 
   * @param node
   * @return The resulting node width.
   */
  private double setIsrNodeTexts(GXLNode node) {

    double nodeWidth = 0;

    if (node.getGraphCount() == 0) {
      nodeWidth = APPLICATION_WIDTH;
    }
    else {
      for (GXLNode child : GXLUtil.getNodes(node.getGraphAt(0))) {
        setIsrNodeTexts(child);
      }
      nodeWidth = (int) Math.ceil(Math.sqrt(GXLUtil.getNodes(node.getGraphAt(0)).size())) * (APPLICATION_WIDTH + APPLICATION_PADDING_SIDE)
          + APPLICATION_PADDING_SIDE;
    }

    NodeWithProperties nodeWithProperties = isIdToGxlNode.get(node.getID());
    String appName = nodeWithProperties.getAppName();
    String nodeBusinessObjects = nodeWithProperties.getBusinessObjects();
    String nodeBaseComponents = nodeWithProperties.getBaseComponents();

    String screenName = getScreenName(informationFlowOptions, appName, null, IS_CATEGORY_NAME, nodeWidth, 8, "");
    setTextPropertyToNode(node, PROP_APP_NAME, screenName);

    String screenBos = getScreenName(informationFlowOptions, nodeBusinessObjects, null, BO_CATEGORY_NAME, nodeWidth, 6, "");
    setTextPropertyToNode(node, PROP_APP_INFORMATION_OBJECTS, screenBos);

    String screenBcs = getScreenName(informationFlowOptions, nodeBaseComponents, null, IS_CATEGORY_NAME, nodeWidth, 6, "");
    setTextPropertyToNode(node, PROP_APP_BASE_COMPONENTS, screenBcs);

    return nodeWidth;
  }

  /**
   * Parameter class for set of edge parameter values and common computations, e.g. releases A and B
   * of the interface.
   */
  private static class EdgeParameters {
    private final String              masterFlow;
    private Integer                   releaseAId;
    private Integer                   releaseBId;

    private final Map<String, String> edgeProps;

    protected EdgeParameters(String masterFlow, InformationSystemInterface connection, Map<String, String> edgeProps) {
      this.masterFlow = masterFlow;
      this.releaseAId = connection.getInformationSystemReleaseA().getId();
      this.releaseBId = connection.getInformationSystemReleaseB().getId();
      this.edgeProps = edgeProps;
    }

    /**
     * Constructor for directed, yet not bidirectional edges.
     *
     * @param masterFlow
     *          The master shape name
     * @param connection
     *          The connection object holding information for the edge
     * @param relAToRelB
     *          Determines whether the edge should be directed from A to B (true), or from B to A
     *          (false).
     * @param edgeProps
     *          The (visio-relevant) shape properties for the edge.
     */
    protected EdgeParameters(String masterFlow, InformationSystemInterface connection, boolean relAToRelB, Map<String, String> edgeProps) {
      this.masterFlow = masterFlow;
      if (relAToRelB) {
        this.releaseAId = connection.getInformationSystemReleaseA().getId();
        this.releaseBId = connection.getInformationSystemReleaseB().getId();
      }
      else {
        this.releaseAId = connection.getInformationSystemReleaseB().getId();
        this.releaseBId = connection.getInformationSystemReleaseA().getId();
      }
      this.edgeProps = edgeProps;
    }

  }

  private static class NodeWithProperties {
    private final GXLNode node;
    private final String  appName;
    private final String  businessObjects;
    private final String  baseComponents;

    public NodeWithProperties(GXLNode node, String appName, String businessObjects, String baseComponents) {
      super();
      this.node = node;
      this.appName = appName;
      this.businessObjects = businessObjects;
      this.baseComponents = baseComponents;
    }

    public GXLNode getNode() {
      return node;
    }

    public String getAppName() {
      return appName;
    }

    public String getBusinessObjects() {
      return businessObjects;
    }

    public String getBaseComponents() {
      return baseComponents;
    }

  }
}

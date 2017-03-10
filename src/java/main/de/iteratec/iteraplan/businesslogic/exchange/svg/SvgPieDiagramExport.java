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
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.piebar.PieBar;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.svg.model.BasicShape;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.Shape;
import de.iteratec.svg.model.SvgExportException;


public class SvgPieDiagramExport extends AbstractSvgPieBarExport {

  private static final Logger      LOGGER                  = Logger.getIteraplanLogger(SvgPieDiagramExport.class);
  private static final String      SVG_TEMPLATE_FILE       = "/SVGPieTemplate.svg";

  /**
   * Pie-specific master shapes
   */
  private static final String      SVG_PIE_MASTER          = "PieRoot";

  private static final String      COLOR_SECTOR_BASE_STYLE = "colorSectorBaseStyle";

  private static final Coordinates PIE_CENTER              = new Coordinates(400, 400);
  private static final Coordinates COLOR_LEGEND_POS        = new Coordinates(690, MARGIN_TOP);
  private static final double      DOCUMENT_WIDTH          = 1010;
  private static final double      DOCUMENT_MIN_HEIGHT     = 700;

  private final PieBar             pie;
  private double                   pieRadius;

  private final Set<String>        usedAttributeValues     = CollectionUtils.hashSet();

  public SvgPieDiagramExport(PieBar pie, PieBarDiagramOptionsBean pieBarOptions, AttributeTypeService attributeTypeService,
      AttributeValueService attributeValueService) {
    super(attributeTypeService, attributeValueService, pieBarOptions);
    this.pie = pie;

    loadSvgDocumentFromTemplate(SVG_TEMPLATE_FILE, "Pie");
  }

  @Override
  public Document createDiagram() {
    LOGGER.info("creating SVG-document for pie diagram");

    try {
      initializeColors();

      createPie();
      createPieBarColorLegend(getOptions().getDiagramValuesType(), getColorDimension(), COLOR_LEGEND_POS, usedAttributeValues, false);

      double queryInfoEnd = createQueryInfo();

      setDocumentDimensions(queryInfoEnd);

      createDiagramTitle(createTitleString(), 0, 0, MARGIN);
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
    double queryInfoEnd = 0;
    if (getOptions().isShowSavedQueryInfo()) {
      Coordinates pos = new Coordinates(2 * MARGIN, DOCUMENT_MIN_HEIGHT - MARGIN);
      double width = COLOR_LEGEND_POS.getX() - 2 * MARGIN;
      queryInfoEnd = createSavedQueryInfo(pos, width, getOptions().getSavedQueryInfo(), getOptions().getServerUrl()) + MARGIN;
    }
    return queryInfoEnd;
  }

  private String createTitleString() {
    StringBuilder title = new StringBuilder();
    title.append(getOptions().getNumberOfSelectedElements()).append(' ');
    title.append(MessageAccess.getStringOrNull(getOptions().getSelectedBbType(), getLocale()));
    title.append('\n').append(pie.getLabel());
    return title.toString();
  }

  private void initializeColors() throws SvgExportException {
    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(getOptions().getSelectedBbType());
    setColorDimension(createColorDimension(getOptions().getColorOptionsBean(), tobb));

    generateCssColorStyles(getColorDimension());
  }

  private Shape createPie() throws SvgExportException {
    Shape pieShape = initializePieShape();

    double radius = pieShape.getWidth() / 2;
    double centerX = pieShape.getPinX() + radius;
    double centerY = pieShape.getPinY() + radius;

    List<PieSegmentLabel> labels = CollectionUtils.arrayList();
    double startAngle = -Math.PI / 2;
    for (Map.Entry<String, Integer> entry : pie.getValuesToSizeMap().entrySet()) {
      Integer sizeValue = entry.getValue();
      if (sizeValue != null && sizeValue.intValue() != 0) {
        double angleRad = createColoredPieSegment(pieShape, radius, centerX, centerY, startAngle, entry);

        if (getOptions().isShowSegmentLabels()) {
          boolean outside = false;
          if (angleRad < (2 * Math.PI) / 36) {
            outside = true;
          }
          labels.add(new PieSegmentLabel(createSegmentLabelText(entry, pie), (startAngle + angleRad / 2) % (2 * Math.PI), outside));
        }

        startAngle += angleRad;
      }
      if (sizeValue != null && (getOptions().isShowEmptySegments() || sizeValue.intValue() != 0)) {
        usedAttributeValues.add(entry.getKey());
      }
    }

    drawSegmentLabels(labels);

    return pieShape;
  }

  /**
   * Creates a colored segment for the pie chart according to the given parameters and returns
   * the angle this segment occupies.
   * @param pieShape
   * @param radius
   * @param centerX
   * @param centerY
   * @param startAngle
   * @param valueToSizeEntry
   * @return the angle of the created segment in rad
   * @throws SvgExportException
   */
  private double createColoredPieSegment(Shape pieShape, double radius, double centerX, double centerY, double startAngle,
                                         Map.Entry<String, Integer> valueToSizeEntry) throws SvgExportException {

    double angleRad = 2 * Math.PI * valueToSizeEntry.getValue().intValue() / pie.getTotalSize();

    BasicShape segment = createSegmentShape(pieShape, centerX, centerY, radius, angleRad, startAngle);

    segment.setPosition(centerX, centerY);
    segment.addCSSClass(COLOR_SECTOR_BASE_STYLE);

    Color color;
    if (DimensionOptionsBean.DEFAULT_VALUE.equals(valueToSizeEntry.getKey())) {
      color = getColorDimension().getDefaultValue();
    }
    else {
      color = getColorDimension().getValue(valueToSizeEntry.getKey());
    }
    String colorCss = getColorToColorClassMap().get(getColorStr(color));
    segment.addCSSClass(colorCss);
    return angleRad;
  }

  private Shape initializePieShape() throws SvgExportException {
    Shape pieShape = createNewShape(SVG_PIE_MASTER);
    pieRadius = pieShape.getWidth() / 2;
    pieShape.setPosition(PIE_CENTER.getX() - pieRadius, PIE_CENTER.getY() - pieRadius);

    pieShape.addCSSClass(getColorToColorClassMap().get(getColorStr(getColorDimension().getDefaultValue())));
    pieShape.addCSSClass(CSS_FILL_TRANSPARENT_STYLE);
    return pieShape;
  }

  private void drawSegmentLabels(List<PieSegmentLabel> labels) throws SvgExportException {
    double segmentLabelPadding = 15;

    boolean possibleCollision = false;
    for (PieSegmentLabel label : labels) {
      double distFromCenter = 0;
      if (!label.isOutside()) {
        distFromCenter = pieRadius - calculateNeededSpaceForTextLabel(label) - segmentLabelPadding;
        possibleCollision = false;
      }
      else {
        distFromCenter = pieRadius + calculateNeededSpaceForTextLabel(label) + segmentLabelPadding;
        if (possibleCollision && labelAboveOrBelowPie(label.getPos())) {
          distFromCenter -= segmentLabelPadding;
          possibleCollision = false;
        }
        else {
          possibleCollision = true;
        }
      }

      Coordinates pos = new Coordinates(PIE_CENTER);
      pos.incX(distFromCenter * Math.cos(label.getPos()));
      pos.incY(distFromCenter * Math.sin(label.getPos()));
      createTextLabel(MASTER_LABEL_LARGE, pos, label.getLabelText(), CSS_TEXT_CENTER_11PT, null);
    }
  }

  private boolean labelAboveOrBelowPie(double pos) {
    double normPos = pos;
    while (normPos < 0) {
      normPos += 2 * Math.PI;
    }
    normPos = normPos % (2 * Math.PI);
    return ((normPos > Math.PI * 1 / 4 && normPos < Math.PI * 3 / 4) || (normPos > Math.PI * 5 / 4 && normPos < Math.PI * 7 / 4));
  }

  private double calculateNeededSpaceForTextLabel(PieSegmentLabel label) {
    double majRadius = label.getEstimatedWidth() / 2;
    double minRadius = majRadius / 2;
    double angle = label.getPos();

    return (minRadius * majRadius)
        / Math.sqrt(minRadius * minRadius * Math.cos(angle) * Math.cos(angle) + majRadius * majRadius * Math.sin(angle) * Math.sin(angle));
  }

  private void setDocumentDimensions(double queryInfoEnd) {
    double necessaryHeightForLegend = getMaxLegendHeight() + MARGIN_TOP * 2;
    getSvgDocument().setPageSize(DOCUMENT_WIDTH, Math.max(Math.max(necessaryHeightForLegend, DOCUMENT_MIN_HEIGHT), queryInfoEnd));
    if (isEmbeddedDiagram()) {
      getDiagramContainer().setSize(getSvgDocument().getPageWidth(), getSvgDocument().getPageHeight());
    }
  }

  @Override
  protected String getNameForAdapter(Integer attributeId) {
    return pie.getLabel();
  }

  @Override
  protected List<String> getAttributeValuesForAdapter(Integer attributeId) {
    return getOptions().getColorOptionsBean().getAttributeValues();
  }

  private class PieSegmentLabel extends SegmentLabel {
    private boolean outside = false;

    public PieSegmentLabel(String labelText, double pos, boolean outside) {
      super(labelText, pos);
      this.outside = outside;
    }

    public boolean isOutside() {
      return outside;
    }
  }

}

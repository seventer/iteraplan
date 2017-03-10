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

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.CustomAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.DimensionAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.EmptyAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.piebar.PieBar;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.ValuesType;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.svg.model.BasicShape;
import de.iteratec.svg.model.Shape;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.model.impl.AdvancedTextHelper;


public abstract class AbstractSvgPieBarExport extends SvgExport {

  private static final Logger            LOGGER                     = Logger.getIteraplanLogger(AbstractSvgPieBarExport.class);

  protected static final String          SPECIFIED_PROP_KEY         = "graphicalReport.specified";

  private static final String            LEGEND_BOX_MASTER          = "ColorLegendFieldRoot";
  private static final String            CONTAINER_SHAPE_MASTER     = "ContainerRoot";

  protected static final String          CSS_FILL_TRANSPARENT_STYLE = "fillTransparentStyle";

  private final PieBarDiagramOptionsBean options;

  private double                         maxLegendHeight            = 0;

  private boolean                        embeddedDiagram            = false;
  private Shape                          containerShape;

  public AbstractSvgPieBarExport(AttributeTypeService attributeTypeService, AttributeValueService attributeValueService,
      PieBarDiagramOptionsBean pieBarOptions) {
    super(attributeTypeService, attributeValueService);
    this.options = pieBarOptions;
  }

  public PieBarDiagramOptionsBean getOptions() {
    return options;
  }

  /**
   * Used for composite diagram creation. To be set before calling {@code createDiagram}.
   * @param count
   *          value to set the shapeIdCount to
   */
  public void setStartShapeIdCount(int count) {
    getSvgDocument().setShapeIdCount(count);
  }

  /**
   * Takes into account the different value-types the color dimensions
   * of pie or bar diagram exports can have to construct the corresponding values, before calling the
   * common createColorLegend-Methods of {@link SvgExport}.
   * @param colorDim
   * @param position
   * @param usedAttributeValues
   * @param headerEnabled
   * @return The number of entries in the legend, includuing the header and unspecified value if those exist.
   *         Can be used to determine the height of the legend.
   * @throws SvgExportException
   */
  protected int createPieBarColorLegend(ValuesType valuesType, ColorDimension colorDim, Coordinates position, Set<String> usedAttributeValues,
                                        boolean headerEnabled) throws SvgExportException {
    int legendSize = 0;

    String name = colorDim.getName();
    List<String> values;
    List<String> colors;

    switch (valuesType) {
      case COUNT:
      case VALUES:
        legendSize = createColorLegendForUsedAttrValuesOnly(colorDim, usedAttributeValues, position, LEGEND_BOX_MASTER, name, headerEnabled, "");
        break;
      case MAINTAINED:
        values = ImmutableList.of(MessageAccess.getStringOrNull(SPECIFIED_PROP_KEY, getLocale()),
            MessageAccess.getStringOrNull(DimensionOptionsBean.DEFAULT_VALUE, getLocale()));
        colors = ImmutableList.of(getColorStr(colorDim.getValue(SPECIFIED_PROP_KEY)), getColorStr(colorDim.getDefaultValue()));
        legendSize = createColorLegend(LEGEND_BOX_MASTER, position, name, values, colors, headerEnabled, "");
        break;
      default:
        LOGGER.error("Invalid values type: {0}", getOptions().getDiagramValuesType().name());
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }

    double legendBoxHeight = getSvgDocument().getMasterShapeProperties(MASTER_LEGEND_BOX).getShapeHeight();
    maxLegendHeight = Math.max(maxLegendHeight, legendSize * legendBoxHeight);
    return legendSize;
  }

  /**
   * @return maximum height of all created PieBarColorLegends
   * since last reset of {@link #maxLegendHeight}.
   */
  public double getMaxLegendHeight() {
    return maxLegendHeight;
  }

  /**
   * resets {@link #maxLegendHeight}
   */
  public void resetMaxLegendHeight() {
    maxLegendHeight = 0;
  }

  /**
   * Implementing classes have to provide an appropriate name for the adapter created
   * in {@link AbstractSvgPieBarExport#createAdapter(Integer, TypeOfBuildingBlock)}
   * @param attributeId
   *          relevant attributeId for the colorDimension. may be -1 if the dimension isn't dependent on an attribute
   * @return a name as String
   */
  protected abstract String getNameForAdapter(Integer attributeId);

  /**
   * Implementing classes have to provide the values for the adapter created
   * in {@link AbstractSvgPieBarExport#createAdapter(Integer, TypeOfBuildingBlock)}
   * @param attributeId
   *          relevant attributeId for the colorDimension. may be -1 if the dimension isn't dependent on an attribute
   * @return List of String-values
   */
  protected abstract List<String> getAttributeValuesForAdapter(Integer attributeId);

  @Override
  protected DimensionAdapter<?> createAdapter(Integer selection, TypeOfBuildingBlock bbType) {
    List<String> values = getAttributeValuesForAdapter(selection);
    if (values.isEmpty() && Integer.valueOf(-1).equals(selection)) {
      return new EmptyAdapter(getLocale());
    }
    else if (selection.intValue() < -1) {
      return new CustomAdapter(getLocale(), values, getNameForAdapter(selection));
    }
    else {
      switch (getOptions().getDiagramValuesType()) {
        case VALUES:
          return super.createAdapter(selection, bbType);
        case COUNT:
          return new CustomAdapter(getLocale(), values, getNameForAdapter(selection));
        case MAINTAINED:
          return new CustomAdapter(getLocale(), values, getNameForAdapter(selection));
        default:
          LOGGER.error("Invalid values type: {0}", getOptions().getDiagramValuesType().name());
          throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
      }
    }
  }

  @Override
  protected Shape createNewShape(String masterShape) throws SvgExportException {
    if (!embeddedDiagram) {
      return super.createNewShape(masterShape);
    }
    else {
      return containerShape.createNewInnerShape(masterShape);
    }
  }

  @Override
  protected Shape createNewShape(String masterShape, int layer) throws SvgExportException {
    if (!embeddedDiagram) {
      return super.createNewShape(masterShape, layer);
    }
    else {
      return containerShape.createNewInnerShape(masterShape, layer);
    }
  }

  @Override
  protected BasicShape createNewBasicShape(String masterShapeName) throws SvgExportException {
    if (!embeddedDiagram) {
      return super.createNewBasicShape(masterShapeName);
    }
    else {
      return containerShape.createNewBasicInnerShape(masterShapeName);
    }
  }

  /**
   * Sets whether the diagram should be created on its own or is to be embedded in
   * an composite diagram.
   * @param embeddedDiagram
   *          true if diagram is to be embedded
   * @throws SvgExportException
   */
  public void setEmbeddedDiagram(boolean embeddedDiagram) throws SvgExportException {
    this.embeddedDiagram = embeddedDiagram;
    if (embeddedDiagram) {
      containerShape = getSvgDocument().createNewShape(CONTAINER_SHAPE_MASTER);
      containerShape.setScaleInnerShapesWithShape(false);
    }
    else {
      containerShape = null;
    }
  }

  public boolean isEmbeddedDiagram() {
    return embeddedDiagram;
  }

  public Shape getDiagramContainer() {
    return containerShape;
  }

  protected String createSegmentLabelText(Entry<String, Integer> entry, PieBar bar) {
    StringBuilder label = new StringBuilder();
    label.append(entry.getValue().toString());
    label.append(" (").append(createPercentageLabelText(entry.getValue().floatValue(), bar.getRealSize())).append(")");
    return label.toString();
  }

  protected String createPercentageLabelText(float part, int total) {
    float ratio = part / total;
    int percentage = Math.round(ratio * 100);
    return percentage + "%";
  }

  protected class SegmentLabel {
    private final String labelText;
    private double       pos;
    private final double estimatedWidth;

    public SegmentLabel(String labelText, double pos) {
      this.labelText = labelText;
      this.estimatedWidth = AdvancedTextHelper.getTextWidth(labelText.length(), 11, AdvancedTextHelper.POINT_TO_UNIT_CONSTANT);
      this.pos = pos;
    }

    public String getLabelText() {
      return labelText;
    }

    public double getPos() {
      return pos;
    }

    protected void setPos(double pos) {
      this.pos = pos;
    }

    public double getEstimatedWidth() {
      return estimatedWidth;
    }

  }

}

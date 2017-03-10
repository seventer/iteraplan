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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb.legend;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.CreateLabeledPlanarSymbol;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.CreatePlanarSymbol;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.visualizationmodel.ALabeledVisualizationObject;
import de.iteratec.visualizationmodel.APlanarSymbol;
import de.iteratec.visualizationmodel.ASymbol;
import de.iteratec.visualizationmodel.Color;
import de.iteratec.visualizationmodel.CompositeSymbol;
import de.iteratec.visualizationmodel.HorizontalAlignment;
import de.iteratec.visualizationmodel.LineStyle;
import de.iteratec.visualizationmodel.Placement;
import de.iteratec.visualizationmodel.Rectangle;
import de.iteratec.visualizationmodel.TextHelper;
import de.iteratec.visualizationmodel.VerticalAlignment;


/**
 * Class to create color legends
 */
public class LegendCreator {

  public static final String                                     NAMES_LEGEND_NAME         = "__Name__";
  public static final float                                      FONTSIZE_TO_HEIGHT_FACTOR = 3f;
  private String                                                 fontName                  = "Arial";

  private Color                                                  defaultFillColor          = Color.WHITE;
  private Color                                                  defaultBorderColor        = Color.BLACK;
  private float                                                  defaultBorderWidth        = 1f;
  private LineStyle                                              defaultLineStyle          = LineStyle.SOLID;
  private float                                                  defaultWidth              = 150f;
  private float                                                  defaultHeight             = 30f;
  private float                                                  defaultHeightSymbol       = 35f;
  private float                                                  deafaultWidthSymbol       = 35f;

  private CreatePlanarSymbol<APlanarSymbol>                      borderCreator;
  private CreateLabeledPlanarSymbol<ALabeledVisualizationObject> labelCreator;
  private CreatePlanarSymbol<APlanarSymbol>                      symbolCreator;

  private LegendCreatorConfiguration                             configuration             = new LegendCreatorConfiguration();
  private Rectangle2D.Float                                      diagramBoundingBox;
  private List<ColorLegend>                                      legends                   = new ArrayList<ColorLegend>();

  public LegendCreator() {
    labelCreator = new CreateLabeledPlanarSymbol<ALabeledVisualizationObject>();
    labelCreator.setVObjectClass(Rectangle.class);
    labelCreator.setHeight((configuration.getFontSize() * FONTSIZE_TO_HEIGHT_FACTOR));
    labelCreator.setFillColor(Color.WHITE);
    labelCreator.setBorderColor(Color.WHITE);

    borderCreator = new CreatePlanarSymbol<APlanarSymbol>();
    borderCreator.setVObjectClass(Rectangle.class);
    borderCreator.setFillColor(configuration.getLegendBackgroundColor());
    borderCreator.setBorderColor(configuration.getLegendBorderColor());

    symbolCreator = new CreatePlanarSymbol<APlanarSymbol>();
    symbolCreator.setVObjectClass(Rectangle.class);
    symbolCreator.setHeight(defaultHeightSymbol);
    symbolCreator.setWidth(deafaultWidthSymbol);
  }

  public LegendCreatorConfiguration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(LegendCreatorConfiguration configuration) {
    this.configuration = configuration;
  }

  public void setDiagramBoundingBox(Rectangle2D.Float diagramBoundingBox) {
    this.diagramBoundingBox = diagramBoundingBox;
  }

  public CompositeSymbol transform(Model model) {
    initLegendSymbols();
    int entriesPerColumn = getNumberOfEntriesPerColumn() + 1;
    List<CompositeSymbol> legendColumns = createLegendColumns(entriesPerColumn);

    float xpos = 0;
    for (CompositeSymbol legendColumn : legendColumns) {
      switch (configuration.getVerticalAlignment()) {
        case TOP:
          legendColumn.setYpos(legendColumn.getHeight() / 2);
          break;
        case BOTTOM:
          legendColumn.setYpos(legendColumn.getHeight() / -2);
          break;
        default: //nothing
      }
      legendColumn.setXpos(xpos + legendColumn.getWidth() / 2);
      xpos += legendColumn.getWidth() + configuration.getInterLegendMargin();
    }

    CompositeSymbol legendContainer = new CompositeSymbol();
    legendContainer.getChildren().addAll(legendColumns);
    adjustLegendsContainerPosition(legendContainer);
    return legendContainer;
  }

  private int getNumberOfEntriesPerColumn() {
    int numberOfEntries = 0;
    float legendEntryMaxWidth = 0;
    for (ColorLegend legend : legends) {
      Font font = new Font(fontName, 0, 10);
      numberOfEntries += legend.getLegendEntries().size() + 1; // +1 for header
      for (ColorLegendEntry legendEntry : legend.getLegendEntries()) {
        double textWidth = TextHelper.getTextBounds(legendEntry.getEntryLabel(), font, TextHelper.DEFAULT_FONT_RENDER_CONTEXT).getWidth();
        double legendEntryWidth = legendEntry.getEntrySymbol().getWidth() + textWidth + configuration.getInnerLegendPadding() * 3;
        legendEntryMaxWidth = (float) Math.max(legendEntryMaxWidth, legendEntryWidth);
      }
    }
    return calculateNumberOfEntriesPerColumn(numberOfEntries, legendEntryMaxWidth);
  }

  private int calculateNumberOfEntriesPerColumn(int numberOfEntries, float legendEntryMaxWidth) {
    int entriesPerColumn = numberOfEntries;
    if (this.diagramBoundingBox.height != 0 && (configuration.getPlacement() == Placement.LEFT || configuration.getPlacement() == Placement.RIGHT)) {
      float entryHeight = (configuration.getFontSize() * FONTSIZE_TO_HEIGHT_FACTOR + configuration.getInnerLegendPadding());
      entriesPerColumn = (int) Math.floor(this.diagramBoundingBox.height / entryHeight);
    }
    else if (this.diagramBoundingBox.width != 0
        && (configuration.getPlacement() == Placement.TOP || configuration.getPlacement() == Placement.BOTTOM)) {
      int numberOfColumns = (int) Math.floor(this.diagramBoundingBox.width / legendEntryMaxWidth);
      entriesPerColumn = (int) Math.ceil(((double) numberOfEntries) / numberOfColumns);
    }
    return entriesPerColumn;
  }

  private List<CompositeSymbol> createLegendColumns(int entriesPerColumn) {
    int currentRow = 1;
    List<CompositeSymbol> legendColumns = Lists.newArrayList();
    List<CompositeSymbol> legendSymbols = Lists.newArrayList();
    for (ColorLegend legend : legends) {
      CompositeSymbol legendSymbol = new CompositeSymbol();
      CompositeSymbol iconsColumn = new CompositeSymbol();
      CompositeSymbol labelsColumn = new CompositeSymbol();

      CompositeSymbol header = createLegendHeader(legend.getLegendInfo());
      legendSymbol.add(header);
      currentRow++;

      float yPos = header.getHeight() + configuration.getInnerLegendPadding() * 2;
      boolean firstElement = true;

      for (Iterator<ColorLegendEntry> entriesIterator = legend.getLegendEntries().iterator(); entriesIterator.hasNext();) {
        ColorLegendEntry legendEntry = entriesIterator.next();
        ASymbol entrySymbol = legendEntry.getEntrySymbol();
        entrySymbol.setXpos(0);
        entrySymbol.setYpos(yPos);

        ALabeledVisualizationObject label = createLegendEntryLabel(yPos, legendEntry);

        if (entrySymbol.getHeight() > label.getHeight() && firstElement) {
          yPos += entrySymbol.getHeight() / 2;
          entrySymbol.setYpos(yPos);
          label.setYpos(yPos);
          firstElement = false;
        }

        iconsColumn.getChildren().add(entrySymbol);
        labelsColumn.getChildren().add(label);

        yPos += Math.max(entrySymbol.getHeight(), label.getHeight()) + configuration.getInnerLegendPadding();
        currentRow++;

        if (currentRow > entriesPerColumn
            && (legend.getLegendEntries().size() > configuration.getMinConnectedEntries() || !entriesIterator.hasNext())) {
          currentRow = 1;
          assembleLegend(legendSymbol, iconsColumn, labelsColumn);
          legendSymbols.add(legendSymbol);
          legendColumns.add(assembleLegendColumn(legendSymbols));
          legendSymbols.clear();
          if (entriesIterator.hasNext()) {
            legendSymbol = new CompositeSymbol();
            iconsColumn = new CompositeSymbol();
            labelsColumn = new CompositeSymbol();
          }
        }
        else if (!entriesIterator.hasNext()) {
          assembleLegend(legendSymbol, iconsColumn, labelsColumn);
          legendSymbols.add(legendSymbol);
        }
      }
    }
    if (!legendSymbols.isEmpty()) {
      legendColumns.add(assembleLegendColumn(legendSymbols));
    }
    return legendColumns;
  }

  private CompositeSymbol assembleLegendColumn(List<CompositeSymbol> legendsList) {
    float ypos = 0;
    for (CompositeSymbol legend : legendsList) {
      switch (configuration.getHorizontalAlignment()) {
        case LEFT:
          legend.setXpos(legend.getWidth() / 2);
          break;
        case RIGHT:
          legend.setXpos(legend.getWidth() / -2);
          break;
        default: //nothing
      }
      legend.setYpos(ypos + legend.getHeight() / 2);
      ypos += legend.getHeight() + configuration.getInterLegendMargin();
    }
    CompositeSymbol legendColumn = new CompositeSymbol();
    legendColumn.addAll(legendsList);
    return legendColumn;
  }

  private void assembleLegend(CompositeSymbol legendContainer, CompositeSymbol iconsColumn, CompositeSymbol labelsColumn) {
    iconsColumn.setXpos(iconsColumn.getWidth() / 2);
    labelsColumn.setXpos(iconsColumn.getWidth() + labelsColumn.getWidth() / 2 + configuration.getInnerLegendPadding());

    legendContainer.add(iconsColumn);
    legendContainer.add(labelsColumn);

    APlanarSymbol border = createLegendBorder(legendContainer);
    legendContainer.getChildren().add(0, border);
  }

  private CompositeSymbol createLegendHeader(ColorLegendInfo legendInfo) {
    CompositeSymbol header = new CompositeSymbol();
    ALabeledVisualizationObject headerTypeName = labelCreator.create();
    String typeName = legendInfo.getTypeName();
    String attributeName = legendInfo.getAttributeName();
    String connectCaracter = " - ";
    if (typeName != null) {
      headerTypeName.getText().setText(typeName);
    }
    else {
      headerTypeName.getText().setText("");
      connectCaracter = "";
    }
    headerTypeName.getText().setTextSize(configuration.getFontSize());
    headerTypeName.getText().setHorizontalAlignment(HorizontalAlignment.CENTER);
    headerTypeName.getText().setVerticalAlignment(VerticalAlignment.MIDDLE);
    headerTypeName.setFillColor(defaultFillColor);
    headerTypeName.setWidth(defaultWidth);
    headerTypeName.setHeight(defaultHeight / 2);
    headerTypeName.setLineStyle(defaultLineStyle);
    headerTypeName.adjustSizeToText(0, 0);
    headerTypeName.setXpos(headerTypeName.getWidth() / 2);
    header.getChildren().add(0, headerTypeName);

    ALabeledVisualizationObject headerAttributeName = labelCreator.create();
    if (attributeName != null) {
      headerAttributeName.getText().setText(connectCaracter + attributeName);
    }
    else {
      headerAttributeName.getText().setText("");
    }
    headerAttributeName.getText().setTextSize(configuration.getFontSize());
    headerAttributeName.getText().setHorizontalAlignment(HorizontalAlignment.CENTER);
    headerAttributeName.getText().setVerticalAlignment(VerticalAlignment.MIDDLE);
    headerAttributeName.setFillColor(defaultFillColor);
    headerAttributeName.setWidth(defaultWidth);
    headerAttributeName.setHeight(defaultHeight / 2);
    headerAttributeName.setLineStyle(defaultLineStyle);
    headerAttributeName.adjustSizeToText(0, 0);
    headerAttributeName.setXpos(headerAttributeName.getWidth() / 2);
    headerAttributeName.setYpos(headerTypeName.getHeight());
    header.getChildren().add(1, headerAttributeName);
    return header;
  }

  private ALabeledVisualizationObject createLegendEntryLabel(float yPos, ColorLegendEntry legendEntry) {
    ALabeledVisualizationObject label = labelCreator.create();
    label.getText().setText(legendEntry.getEntryLabel());
    label.getText().setTextSize(configuration.getFontSize());
    label.getText().setHorizontalAlignment(HorizontalAlignment.LEFT);
    label.getText().setVerticalAlignment(VerticalAlignment.MIDDLE);
    label.setFillColor(defaultFillColor);
    label.setWidth(defaultWidth);
    label.setHeight(defaultHeight);
    label.setLineStyle(defaultLineStyle);
    label.adjustSizeToText(0, 0);
    label.setXpos(label.getWidth() / 2);
    label.setYpos(yPos);
    return label;
  }

  private APlanarSymbol createLegendBorder(CompositeSymbol legendSymbol) {
    APlanarSymbol border = borderCreator.create();
    border.setWidth(legendSymbol.getWidth() + configuration.getInnerLegendPadding() * 2);
    border.setHeight(legendSymbol.getHeight() + configuration.getInnerLegendPadding() * 2);
    border.setXpos(legendSymbol.getXpos());
    border.setYpos(legendSymbol.getYpos());
    border.setBorderColor(defaultBorderColor);
    border.setBorderWidth(defaultBorderWidth);
    border.setFillColor(defaultFillColor);
    return border;
  }

  private void initLegendSymbols() {
    for (ColorLegend legend : legends) {
      for (ColorLegendEntry colorLegendEntry : legend.getLegendEntries()) {
        APlanarSymbol symbol = symbolCreator.create();
        symbol.setWidth(deafaultWidthSymbol);
        symbol.setHeight(defaultHeightSymbol);
        symbol.setBorderColor(Color.BLACK);
        symbol.setFillColor((Color) colorLegendEntry.getPropertyValue());
        colorLegendEntry.setEntrySymbol(symbol);
      }
    }
  }

  private void adjustLegendsContainerPosition(CompositeSymbol legendContainer) {
    switch (configuration.getPlacement()) {
      case LEFT:
        legendContainer.setXpos((float) this.diagramBoundingBox.getMinX() - legendContainer.getWidth() / 2 - configuration.getMarginToDiagram());
        adjustVerticalAlignment(legendContainer);
        break;
      case RIGHT:
        legendContainer.setXpos((float) this.diagramBoundingBox.getMaxX() + legendContainer.getWidth() / 2 + configuration.getMarginToDiagram());
        adjustVerticalAlignment(legendContainer);
        break;
      case BOTTOM:
        legendContainer.setYpos((float) this.diagramBoundingBox.getMaxY() + legendContainer.getHeight() / 2 + configuration.getMarginToDiagram());
        adjustHorizontalAlignment(legendContainer);
        break;
      case TOP:
        legendContainer.setYpos((float) this.diagramBoundingBox.getMinY() - legendContainer.getHeight() / 2 - configuration.getMarginToDiagram());
        adjustHorizontalAlignment(legendContainer);
        break;
      default:
        // nothing
    }

    legendContainer.setXpos(legendContainer.getXpos() + configuration.getxDelta());
    legendContainer.setYpos(legendContainer.getYpos() + configuration.getyDelta());
  }

  private void adjustVerticalAlignment(CompositeSymbol legendContainer) {
    switch (configuration.getVerticalAlignment()) {
      case BOTTOM:
        legendContainer.setYpos((float) this.diagramBoundingBox.getMaxY() - legendContainer.getHeight() / 2);
        break;
      case TOP:
        legendContainer.setYpos((float) this.diagramBoundingBox.getMinY() + legendContainer.getHeight() / 2);
        break;
      case MIDDLE:
        legendContainer.setYpos((float) this.diagramBoundingBox.getCenterY());
        break;
      default:
        // nothing
    }
  }

  private void adjustHorizontalAlignment(CompositeSymbol legendContainer) {
    switch (configuration.getHorizontalAlignment()) {
      case RIGHT:
        legendContainer.setXpos((float) this.diagramBoundingBox.getMaxX() - legendContainer.getWidth() / 2);
        break;
      case LEFT:
        legendContainer.setXpos((float) this.diagramBoundingBox.getMinX() + legendContainer.getWidth() / 2);
        break;
      case CENTER:
        legendContainer.setXpos((float) this.diagramBoundingBox.getCenterX());
        break;
      default:
        // nothing
    }
  }

  public List<ColorLegend> getLegends() {
    return legends;
  }

  public void setLegends(List<ColorLegend> legends) {
    this.legends = legends;
  }

  public static final class LegendCreatorConfiguration {
    /** Position of the legends compared to the diagram */
    private Placement           placement             = Placement.RIGHT;
    /** Horizontal alignment of the legends */
    private HorizontalAlignment horizontalAlignment   = HorizontalAlignment.LEFT;
    /** Vertical alignment of the legends */
    private VerticalAlignment   verticalAlignment     = VerticalAlignment.TOP;
    /** Padding between the elements of a legend */
    private float               innerLegendPadding    = 5f;
    /** Margin between the legends */
    private float               interLegendMargin     = 10f;
    /** Distance between the legends and the diagram area */
    private float               marginToDiagram       = 50f;
    /** Legends with a number of up to this value will always be displayed without splitting them */
    private int                 minConnectedEntries   = 7;
    /** Distance the legends will be shifted down after taking placement and alignment into account (negative values for up shifting) */
    private float               yDelta                = 0;
    /** Distance the legends will be shifted to the right after taking placement and alignment into account  (negative values for left shifting) */
    private float               xDelta                = 0;
    /** Font size of legend texts */
    private int                 fontSize              = 10;
    /** Background color of the single legends */
    private Color               legendBackgroundColor = Color.WHITE;
    /** Color of the legends' borders */
    private Color               legendBorderColor     = Color.BLACK;
    /**
     * Mapping from a type name (for example "outer" or "inner" in a nesting cluster diagram) to the actual name
     * to be displayed in legend headers. 
     */
    private Map<String, String> typeNameMap           = Maps.newHashMap();

    public LegendCreatorConfiguration() {
      typeNameMap.put(LegendCreator.NAMES_LEGEND_NAME, MessageAccess.getStringOrNull("reports.nameLegendContent"));
    }

    public Color getLegendBackgroundColor() {
      return legendBackgroundColor;
    }

    public void setLegendBackgroundColor(Color legendBackgroundColor) {
      this.legendBackgroundColor = legendBackgroundColor;
    }

    public Color getLegendBorderColor() {
      return legendBorderColor;
    }

    public void setLegendBorderColor(Color legendBorderColor) {
      this.legendBorderColor = legendBorderColor;
    }

    public Placement getPlacement() {
      return placement;
    }

    public void setPlacement(Placement placement) {
      this.placement = placement;
    }

    public HorizontalAlignment getHorizontalAlignment() {
      return horizontalAlignment;
    }

    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
      this.horizontalAlignment = horizontalAlignment;
    }

    public VerticalAlignment getVerticalAlignment() {
      return verticalAlignment;
    }

    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
      this.verticalAlignment = verticalAlignment;
    }

    public float getInnerLegendPadding() {
      return innerLegendPadding;
    }

    public void setInnerLegendPadding(float innerLegendPadding) {
      this.innerLegendPadding = innerLegendPadding;
    }

    public float getInterLegendMargin() {
      return interLegendMargin;
    }

    public void setInterLegendMargin(float interLegendMargin) {
      this.interLegendMargin = interLegendMargin;
    }

    public float getMarginToDiagram() {
      return marginToDiagram;
    }

    public void setMarginToDiagram(float marginToDiagram) {
      this.marginToDiagram = marginToDiagram;
    }

    public int getMinConnectedEntries() {
      return minConnectedEntries;
    }

    public void setMinConnectedEntries(int minConnectedEntries) {
      this.minConnectedEntries = minConnectedEntries;
    }

    public float getyDelta() {
      return yDelta;
    }

    public void setyDelta(float yDelta) {
      this.yDelta = yDelta;
    }

    public float getxDelta() {
      return xDelta;
    }

    public void setxDelta(float xDelta) {
      this.xDelta = xDelta;
    }

    public int getFontSize() {
      return fontSize;
    }

    public void setFontSize(int fontSize) {
      this.fontSize = fontSize;
    }

    public Map<String, String> getTypeNameMap() {
      return typeNameMap;
    }

    public void setTypeNameMap(Map<String, String> typeNameMap) {
      this.typeNameMap = typeNameMap;
    }
  }
}

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

import de.iteratec.iteraplan.businesslogic.exchange.common.legend.LogicalPage;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.NamesLegend;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.NamesLegendEntry;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.svg.model.BasicShape;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.model.impl.AdvancedTextHelper;
import de.iteratec.svg.model.impl.RectangleShape;
import de.iteratec.svg.model.impl.TextShape;


public class SvgNamesLegend extends NamesLegend {

  private final Document      svgDocument;

  private static final double INITIAL_LEGEND_WIDTH = 100;
  private static final double LEGEND_ID_BOX_WIDTH  = 70;
  private static final double LEGEND_ENTRY_HEIGHT  = 30;
  private static final double LEGEND_TEXT_SIZE_PT  = 12;

  public static final double  LEGEND_MARGIN        = 80;

  private static final String LEGEND_HEADER        = "legendHeaderStyle";
  private static final String LEGEND_CONTENT       = "legendContentStyle";
  private static final String LEGEND_HEADER_TEXT   = "legendHeaderText";
  private static final String LEGEND_CONTENT_TEXT  = "legendContentText";

  private double              legendBlockX;
  private double              legendBlockY;

  public SvgNamesLegend(Document svgDocument) {
    this.svgDocument = svgDocument;
    this.setLegendContentWidth(INITIAL_LEGEND_WIDTH);
    this.setLegendIdColumnWidth(LEGEND_ID_BOX_WIDTH);
    this.setLegendEntryHeight(LEGEND_ENTRY_HEIGHT);
    this.setLegendTextSizePt(LEGEND_TEXT_SIZE_PT);
    this.setTopMargin(LEGEND_MARGIN);
  }

  @Override
  protected double getNextRowY(double currentY) {
    return currentY + getLegendEntryHeight();
  }

  @Override
  protected double getRelativeLegendHeaderY(int entryNumber) {
    return 0;
  }

  public double getTextWidth(int textLength, double textSizePt) {
    // using 2 pt larger fontsize to account for bold font
    return AdvancedTextHelper.getTextWidth(textLength, textSizePt + 2, AdvancedTextHelper.POINT_TO_UNIT_CONSTANT);
  }

  @Override
  protected double initLegendBlock(double x, double y, double relativeY, int entryNumber, double containerHeight) {
    legendBlockX = x;
    legendBlockY = y + relativeY;
    return y + relativeY + entryNumber * getLegendEntryHeight();
  }

  @Override
  protected void estimateMaxCharacterCount() {
    // estimate the maximal number of characters that fit into a content shape
    setMaxCharacterCount((int) Math.floor(getLegendContentWidth() / ((getLegendTextSizePt()) * AdvancedTextHelper.POINT_TO_UNIT_CONSTANT)));
  }

  @Override
  protected double addNewLogicalPage(List<LogicalPage> resultingLogicalPages) {
    svgDocument.setPageSize(svgDocument.getPageWidth() + getPageWidth(), getPageHeight());
    double pageXnull = svgDocument.getPageWidth() - getPageWidth();
    resultingLogicalPages.add(new LogicalPage(pageXnull, getPageWidth()));
    return pageXnull;
  }

  @Override
  protected void createLegendEntryShape(double deltaY, NamesLegendEntry namesLegendEntry) {

    try {

      // Create the id field
      RectangleShape idRect = (RectangleShape) svgDocument.createNewBasicShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
      idRect.setPosition(legendBlockX, legendBlockY + deltaY);
      idRect.setSize(getLegendIdColumnWidth(), getLegendEntryHeight());
      idRect.addCSSClass(LEGEND_CONTENT);

      TextShape idText = (TextShape) idRect.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.TEXT);
      idText.setTextValue(String.valueOf(namesLegendEntry.getEntryId()));
      idText.addCSSClass(LEGEND_HEADER_TEXT);
      idText.setPosition(idRect.getWidth() / 2, idRect.getHeight() * 0.7);

      // Create the content field
      RectangleShape nameRect = (RectangleShape) svgDocument.createNewBasicShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
      nameRect.setPosition(legendBlockX + getLegendIdColumnWidth(), legendBlockY + deltaY);
      nameRect.setSize(getLegendContentWidth(), getLegendEntryHeight());
      nameRect.addCSSClass(LEGEND_CONTENT);

      TextShape nameText = (TextShape) nameRect.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.TEXT);
      nameText.setTextValue(getTrimmedEntryText(namesLegendEntry.getEntryText()));
      nameText.addCSSClass(LEGEND_CONTENT_TEXT);
      nameText.setXLink(namesLegendEntry.getEntryUrl());
      nameText.setPosition(10, nameRect.getHeight() * 0.7);

    } catch (SvgExportException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }

  }

  @Override
  protected void createLegendHeader(double relativeY, String entryCategoryName) {

    try {

      // Create the id field
      RectangleShape idRect = (RectangleShape) svgDocument.createNewBasicShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
      idRect.setPosition(legendBlockX, legendBlockY + relativeY);
      idRect.setSize(getLegendIdColumnWidth(), getLegendEntryHeight());
      idRect.addCSSClass(LEGEND_HEADER);

      TextShape idText = (TextShape) idRect.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.TEXT);
      idText.setTextValue(getHeaderId());
      idText.addCSSClass(LEGEND_HEADER_TEXT);
      idText.setPosition(idRect.getWidth() / 2, idRect.getHeight() * 0.7);

      // Create the content field
      RectangleShape nameRect = (RectangleShape) svgDocument.createNewBasicShape(BasicShape.SVG_BASIC_SHAPES.RECTANGLE);
      nameRect.setPosition(legendBlockX + getLegendIdColumnWidth(), legendBlockY + relativeY);
      nameRect.setSize(getLegendContentWidth(), getLegendEntryHeight());
      nameRect.addCSSClass(LEGEND_HEADER);

      TextShape nameText = (TextShape) nameRect.createNewBasicInnerShape(BasicShape.SVG_BASIC_SHAPES.TEXT);
      nameText.setTextValue(getHeaderName(entryCategoryName));
      nameText.addCSSClass(LEGEND_HEADER_TEXT);
      nameText.setPosition(nameRect.getWidth() / 2, nameRect.getHeight() * 0.7);

    } catch (SvgExportException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
  }

  @Override
  protected double getLegendMargin() {
    return LEGEND_MARGIN;
  }

  @Override
  protected double getLegendInitialWidth() {
    return INITIAL_LEGEND_WIDTH;
  }

}

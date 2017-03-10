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
package de.iteratec.iteraplan.businesslogic.exchange.visio.legend;

import java.util.List;

import de.iteratec.iteraplan.businesslogic.exchange.common.legend.LogicalPage;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.NamesLegend;
import de.iteratec.iteraplan.businesslogic.exchange.common.legend.NamesLegendEntry;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.InchConverter;
import de.iteratec.visio.model.Page;
import de.iteratec.visio.model.Shape;
import de.iteratec.visio.model.exceptions.MasterNotFoundException;


/**
 * The Visio Name Legend implementation. Note: We implicitly assume that all units being given to
 * this class are inches for coordinates and lengths and points for text sizes.
 */
public class VisioNamesLegend extends NamesLegend {

  private static final String MASTER_LEGEND_HEADER    = "Names-Legend-Header";
  private static final String MASTER_LEGEND_CONTENT   = "Names-Legend-Content";
  private static final String MASTER_LEGEND_GROUP     = "Visio-Legend";

  private final Page          targetPage;
  private Shape               legendGroup;

  private static final double INITIAL_LEGEND_WIDTH_IN = InchConverter.cmToInches(4);
  private static final double LEGEND_ID_BOX_WIDTH_IN  = InchConverter.cmToInches(2);
  private static final double LEGEND_ENTRY_HEIGHT_IN  = InchConverter.cmToInches(0.8);
  private static final double LEGEND_TEXT_SIZE_PT     = 12;

  private static final double LEGEND_MARGIN_IN        = InchConverter.cmToInches(2.5);

  public VisioNamesLegend(Page targetPage) {
    this.targetPage = targetPage;
    this.setLegendContentWidth(INITIAL_LEGEND_WIDTH_IN);
    this.setLegendIdColumnWidth(LEGEND_ID_BOX_WIDTH_IN);
    this.setLegendEntryHeight(LEGEND_ENTRY_HEIGHT_IN);
    this.setLegendTextSizePt(LEGEND_TEXT_SIZE_PT);
    this.setTopMargin(LEGEND_MARGIN_IN);
  }

  public double getTextWidth(int textLength, double textSizePt) {
    return getTextWidth(textLength, textSizePt, 72);
  }

  public static double getTextWidth(int textLength, double textSizePt, int systemDpiSetting) {
    return textLength * InchConverter.ptToInches(textSizePt, systemDpiSetting) / 1.75;
  }

  @Override
  protected double getNextRowY(double currentY) {
    return currentY - getLegendEntryHeight();
  }

  @Override
  protected double getRelativeLegendHeaderY(int entryNumber) {
    return entryNumber * getLegendEntryHeight();
  }

  @Override
  protected double initLegendBlock(double x, double y, double relativeY, int entryNumber, double containerHeight) {
    try {
      legendGroup = this.targetPage.createNewShape(MASTER_LEGEND_GROUP);
      legendGroup.setSize(getLegendWidth(), Math.min((entryNumber + 1) * getLegendEntryHeight(), containerHeight));
      double yPos = containerHeight + y - relativeY - legendGroup.getHeight();
      legendGroup.setPosition(x, yPos);
      return yPos;
    } catch (MasterNotFoundException ex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    }
  }

  @Override
  protected void estimateMaxCharacterCount() {
    // estimate the maximal number of characters that fit into a content shape
    setMaxCharacterCount((int) Math.floor((getLegendContentWidth() * 2) / InchConverter.ptToInches(getLegendTextSizePt(), 72)));
  }

  @Override
  protected double addNewLogicalPage(List<LogicalPage> resultingLogicalPages) {
    double pageXnull = targetPage.getWidth();
    targetPage.setSize(targetPage.getWidth() + getPageWidth(), getPageHeight());
    resultingLogicalPages.add(new LogicalPage(pageXnull, getPageWidth()));
    return pageXnull;
  }

  @Override
  protected void createLegendHeader(double relativeY, String entryCategory) {
    try {
      Shape idHeader = legendGroup.createNewInnerShape(MASTER_LEGEND_HEADER);
      idHeader.setPosition(0, relativeY);
      idHeader.setFieldValue(getHeaderId());

      Shape nameHeader = legendGroup.createNewInnerShape(MASTER_LEGEND_HEADER);
      nameHeader.setPosition(LEGEND_ID_BOX_WIDTH_IN, relativeY);
      nameHeader.setSize(getLegendContentWidth(), LEGEND_ENTRY_HEIGHT_IN);
      nameHeader.setFieldValue(getHeaderName(entryCategory));
    } catch (MasterNotFoundException ex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    }

  }

  @Override
  protected void createLegendEntryShape(double relativeY, NamesLegendEntry namesLegendEntry) {
    try {
      Shape idHeader = legendGroup.createNewInnerShape(MASTER_LEGEND_CONTENT);
      idHeader.setPosition(0, relativeY);
      idHeader.setFieldValue(String.valueOf(namesLegendEntry.getEntryId()));

      Shape nameHeader = legendGroup.createNewInnerShape(MASTER_LEGEND_CONTENT);
      nameHeader.setPosition(LEGEND_ID_BOX_WIDTH_IN, relativeY);
      nameHeader.setSize(getLegendContentWidth(), getLegendEntryHeight());
      nameHeader.setFieldValue(getTrimmedEntryText(namesLegendEntry.getEntryText()));
    } catch (MasterNotFoundException ex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    }

  }

  @Override
  protected double getLegendMargin() {
    return LEGEND_MARGIN_IN;
  }

  @Override
  protected double getLegendInitialWidth() {
    return INITIAL_LEGEND_WIDTH_IN;
  }

}

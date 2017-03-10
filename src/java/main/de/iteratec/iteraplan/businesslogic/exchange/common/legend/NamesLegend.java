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
package de.iteratec.iteraplan.businesslogic.exchange.common.legend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.MessageAccess;


/**
 * This class makes the foundation of all name legends used in svg and visio graphical exports 
 */
public abstract class NamesLegend implements INamesLegend {

  private static final int                          MIN_SCREEN_NAME_LENGTH = 4;

  private int                                       nextEntryId            = 1;
  private final Map<String, List<NamesLegendEntry>> legendCategories       = Maps.newHashMap();

  private LegendMode                                legendMode             = LegendMode.AUTO;

  private double                                    legendEntryHeight      = 0;
  private double                                    legendContentWidth;
  private double                                    legendIdColumnWidth;
  private double                                    legendTextSizePt;

  private int                                       numberOfEntriesPerColumn;
  private int                                       numberOfColumnsPerPage;
  private int                                       maxCharacterCount;

  private final String                              headerId               = MessageAccess.getStringOrNull("reports.nameLegendId");
  private final String                              headerName             = MessageAccess.getStringOrNull("reports.nameLegendContent");

  private double                                    frameWidth             = 0;
  private double                                    frameHeight            = 0;
  private double                                    frameX                 = 0;
  private double                                    frameY                 = 0;
  private double                                    pageWidth              = 0;
  private double                                    pageHeight             = 0;

  private double                                    offset                 = 0;
  private int                                       columnsInducedByOffset = 0;

  /**
   * top margin may differ depending on how many rows the diagram title has
   */
  private double                                    topMargin              = 0;

  public List<LogicalPage> createLegend() {
    if (nextEntryId <= 1) {
      return new LinkedList<LogicalPage>();
    }

    if (legendMode.equals(LegendMode.IN_PAGE)) {
      return createLegendInPage();
    }
    else if (legendMode.equals(LegendMode.NEW_PAGE)) {
      return createLegendNewPage();
    }
    else {
      // Decide whether the legend fits into the provided frame.
      if ((legendEntryHeight * getNamesLegendHeight() < getFrameHeight()) && getLegendContentWidth() < getFrameWidth()) {
        legendMode = LegendMode.IN_PAGE;
        return createLegendInPage();
      }
      else {
        legendMode = LegendMode.NEW_PAGE;
        return createLegendNewPage();
      }
    }
  }

  public boolean displayNamesLegend() {
    return true;
  }

  /**
   * Creates a legend in the page. This is called when the {@link INamesLegend.LegendMode LegendMode}
   * is IN_PAGE or AUTO and the legend fits.
   * 
   * @return An empty list, as the legend is drawn in the existing page and no new logical pages are
   *         created.
   */
  protected List<LogicalPage> createLegendInPage() {
    List<String> keys = getSortedKeys();

    double legendBlockDeltaY = 0;
    for (String currentCategory : keys) {
      List<NamesLegendEntry> categoryEntries = getLegendCategories().get(currentCategory);

      initLegendBlock(getFrameX(), getFrameY(), legendBlockDeltaY, categoryEntries.size(), getFrameHeight());
      double currentY = getRelativeLegendHeaderY(categoryEntries.size());
      createLegendHeader(currentY, currentCategory);
      currentY = getNextRowY(currentY);

      for (NamesLegendEntry legendEntry : categoryEntries) {
        createLegendEntryShape(currentY, legendEntry);
        currentY = getNextRowY(currentY);
      }
      // accounting for header and an empty row between blocks
      legendBlockDeltaY += (categoryEntries.size() + 2) * getLegendEntryHeight();
    }

    // In this case we have generated no logical pages, so we can return an empty list.
    return new LinkedList<LogicalPage>();
  }

  /**
   * returns the y position of the next legend entry row, based on the current position
   * @param currentY
   *          the current y position
   * @return the next y position
   */
  protected abstract double getNextRowY(double currentY);

  /**
   * Returns which Y position the legend header has relative to the legend position initialized
   * in {@link #initLegendBlock(double, double, double, int, double)}.
   * @param entryNumber
   *          Number of legend entries in the current legend block
   * @return relative header position
   */
  protected abstract double getRelativeLegendHeaderY(int entryNumber);

  /**
   * Initializes the current block of legend entries to be shown within a certain area
   * defined by the surrounding element (like frame or page)
   * @param x
   *          X position of the relevant surrounding element
   * @param y
   *          Y position of the relevant surrounding element
   * @param relativeY
   *          Distance of the legend from the top of the surrounding element
   * @param numberOfEntries
   *          Number of entries for this legend block
   * @param containerHeight
   *          Height of the relevant surrounding element
   * @return the Y coordinate of the end of the legend block
   */
  protected abstract double initLegendBlock(double x, double y, double relativeY, int numberOfEntries, double containerHeight);

  protected abstract void createLegendHeader(double relativeY, String entryCategoryName);

  protected abstract void createLegendEntryShape(double relativeY, NamesLegendEntry namesLegendEntry);

  /**
   * Creates the legend in a new page (or pages if necessary). This is called if the
   * {@link INamesLegend.LegendMode LegendMode} is NEW_PAGE or AUTO and the legend doesn't fit into the frame provided.
   * 
   * @return A list of {@link LogicalPage}s, specifying the coordinates of the created logical
   *         pages.
   */
  protected List<LogicalPage> createLegendNewPage() {
    // estimate the number of pages to be created
    initMultipageLegend();
    estimateMaxCharacterCount();

    return createLegendPages(getSortedKeys().iterator());
  }

  private List<LogicalPage> createLegendPages(Iterator<String> categoryNameIterator) {
    List<LogicalPage> resultingLogicalPages = new LinkedList<LogicalPage>();

    boolean done = false;
    int entryCounter = 0;
    String currentCategory = categoryNameIterator.next();
    List<NamesLegendEntry> categoryEntries = getLegendCategories().get(currentCategory);

    //Make space for the offset - skip a number of columns on the first page...
    int currentColumnPreProcess = this.columnsInducedByOffset;
    while (!done) {
      double pageXnull = addNewLogicalPage(resultingLogicalPages);

      for (int currentColumn = currentColumnPreProcess; currentColumn < getNumberOfColumnsPerPage(); currentColumn++) {
        if (done) {
          break;
        }

        double nameLegendColumnBaseX = calculateLegendColumnX(pageXnull, currentColumn);
        int numberOfEntries = Math.min(categoryEntries.size() - entryCounter, getNumberOfEntriesPerColumn());
        double currentY = getRelativeLegendHeaderY(numberOfEntries);
        double legendBlockDeltaY = 0;

        initLegendBlock(nameLegendColumnBaseX, 0, getTopMargin() + legendBlockDeltaY, numberOfEntries, getPageHeight());
        createLegendHeader(currentY, currentCategory);

        for (int currentRow = 1; currentRow < getNumberOfEntriesPerColumn(); currentRow++) {
          if (done) {
            break;
          }

          currentY = getNextRowY(currentY);

          if (entryCounter < 0) {
            // create header only if there is at least space for one more entry in the same column
            if (currentRow < getNumberOfEntriesPerColumn() - 1) {
              legendBlockDeltaY += (numberOfEntries + 2) * getLegendEntryHeight();

              numberOfEntries = Math.min(categoryEntries.size(), getNumberOfEntriesPerColumn() - currentRow);
              initLegendBlock(nameLegendColumnBaseX, 0, getTopMargin() + legendBlockDeltaY, numberOfEntries, getPageHeight());
              currentY = getRelativeLegendHeaderY(numberOfEntries);
              createLegendHeader(currentY, currentCategory);
              entryCounter++;
            }
            // else start new column
            else {
              break;
            }
          }
          else if (categoryEntries.size() > entryCounter) {
            // draw row at the specified coordinates
            createLegendEntryShape(currentY, categoryEntries.get(entryCounter));
            entryCounter++;
          }
          else if (categoryNameIterator.hasNext()) {
            currentCategory = categoryNameIterator.next();
            categoryEntries = getLegendCategories().get(currentCategory);
            entryCounter = -1; // equals header of the new category is to be created next.
          }
          if (categoryEntries.size() <= entryCounter && !categoryNameIterator.hasNext()) {
            done = true;
          }
        }
      }
      currentColumnPreProcess = 0; // no column skipping for following pages
    }
    return resultingLogicalPages;
  }

  /**
   * Calculates the X position of the legend column based on the page X position and the column number
   * @param pageXnull
   * @param currentColumn
   * @return the X position of the legend column
   */
  private double calculateLegendColumnX(double pageXnull, int currentColumn) {
    double legendMargin = getLegendMargin();
    return pageXnull + legendMargin + currentColumn * (getLegendWidth() + legendMargin);
  }

  protected abstract double addNewLogicalPage(List<LogicalPage> resultingLogicalPages);

  protected abstract double getLegendMargin();

  protected abstract double getLegendInitialWidth();

  /**
   * Updates the column width of the legend in accordance with the width of the newly added entry.
   * 
   * @param newEntryWidth
   *          The width of the new entry in the units of the implementation.
   */
  protected void updateLegendWidth(double newEntryWidth) {
    setLegendContentWidth(Math.max(getLegendContentWidth(), newEntryWidth));
  }

  /**
   * Estimates the screen name for an element.
   * 
   * @param ownName
   *          The own (non-hierarchical) name of the element represented by an entry.
   * @param completeName
   *          The full (hierarchical) name of the element to be represented. Can be <b>null</b> if
   *          the element only has an own name.
   * @param fieldWidth
   *          The width of the field in which the screen name should fit in the units of the
   *          implementation.
   * @param fieldTextSizePt
   *          The size of the text in the target object(shape) in pt.
   * @param entryId
   *          The ID the corresponding entry in the names legend has or will have.
   * @return The shortened name for the entry, whole name if it didn't need to be shortened.
   */
  public String getScreenName(String ownName, String completeName, double fieldWidth, double fieldTextSizePt, int entryId) {

    String screenName = getEntryText(ownName, completeName);

    int splitIndex = findSplitIndex(screenName.length(), screenName.length(), fieldWidth * 0.9, fieldTextSizePt);

    if (splitIndex == screenName.length()) {
      return screenName;
    }
    else {
      return screenName.substring(0, splitIndex) + " [" + entryId + ']';
    }
  }

  /**
   * 
   * @param strLength
   *    The length of the original string.
   * @param splitIndex
   *    The current index at which the string is supposed to be split.
   * @param fieldWidth
   *    The width of the field the text should fit into, in units of the implementation.
   * @param fieldTextSizePt
   *    The text size in pt.
   * @return
   *    The index at which the string should be split. Note that this can also be the index of
   *    the last character if the string fits into the field.
   */
  protected int findSplitIndex(int strLength, int splitIndex, double fieldWidth, double fieldTextSizePt) {

    //check if the minimal allowed number of characters in the string has been reached
    if (splitIndex <= MIN_SCREEN_NAME_LENGTH) {
      return splitIndex;
    }

    if (getTextWidth(splitIndex, fieldTextSizePt) >= fieldWidth) {
      return findSplitIndex(strLength, (int) Math.floor(splitIndex * 0.5), fieldWidth, fieldTextSizePt);
    }
    else if (getTextWidth(splitIndex, fieldTextSizePt) > 0.9 * fieldWidth) {
      return splitIndex;
    }
    else {
      if (splitIndex + 1 > strLength) {
        return strLength;
      }
      return findSplitIndex(strLength, splitIndex + 1, fieldWidth, fieldTextSizePt);
    }
  }

  public double getLegendWidth() {
    return getLegendIdColumnWidth() + getLegendContentWidth();
  }

  /**
   * {@inheritDoc}
   */
  public int getNamesLegendHeight() {
    // legend entries + one row per legend category header + one empty row between each two categories
    return nextEntryId - 1 + legendCategories.size() * 2 - 1;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isEmpty() {
    if (legendCategories == null || legendCategories.isEmpty()) {
      return true;
    }

    for (List<NamesLegendEntry> entry : legendCategories.values()) {
      if (!entry.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public Map<String, List<NamesLegendEntry>> getLegendCategories() {
    return legendCategories;
  }

  public LegendMode getLegendMode() {
    return legendMode;
  }

  public void setLegendMode(LegendMode legendMode) {
    this.legendMode = legendMode;
  }

  public void addOffset(double offsetAdd) {
    this.offset = offsetAdd;
  }

  public void setTopMargin(double topMargin) {
    this.topMargin = topMargin;
  }

  public double getLegendOffsetCount() {
    return this.offset;
  }

  public String addLegendEntry(String ownName, String completeName, String entryCategory, double fieldWidth, double fieldTextSizePt, String entryUrl) {
    List<NamesLegendEntry> legendEntryList = getCategoryEntries(entryCategory);

    String entryText = getEntryText(ownName, completeName);
    NamesLegendEntry newEntry = new NamesLegendEntry(nextEntryId, entryText, entryUrl);
    if (legendEntryList.contains(newEntry)) {
      for (NamesLegendEntry entry : legendEntryList) {
        if (newEntry.equals(entry)) {
          return getScreenName(ownName, completeName, fieldWidth, fieldTextSizePt, entry.getEntryId());
        }
      }
    }
    else {
      String screenName = getScreenName(ownName, completeName, fieldWidth, fieldTextSizePt, newEntry.getEntryId());
      if (!entryText.equals(screenName)) {
        double entryWidth = getTextWidth(entryText.length(), getLegendTextSizePt());
        updateLegendWidth(entryWidth);

        legendEntryList.add(newEntry);
        nextEntryId++;
      }
      return screenName;
    }
    return "";
  }

  private List<NamesLegendEntry> getCategoryEntries(String entryCategory) {
    if (!legendCategories.containsKey(entryCategory)) {
      legendCategories.put(entryCategory, new ArrayList<NamesLegendEntry>());
    }
    return legendCategories.get(entryCategory);
  }

  private String getEntryText(String ownName, String completeName) {
    String entryText;
    if (completeName != null) {
      entryText = completeName;
    }
    else {
      entryText = ownName;
    }
    return entryText;
  }

  /**
   * In the case when the legend is created on a new page(s), this method calculates the number of
   * columns etc. needed to present all entries.
   */
  private void initMultipageLegend() {

    double pageMargin = getLegendMargin();
    double initialLegendContentWidth = getLegendInitialWidth();

    // Make sure that no entry is wider than the page as otherwise the algorithm tries to create
    // infinitely many pages.
    setLegendContentWidth(Math.min(getLegendContentWidth(), getPageWidth() - 3 * pageMargin - getLegendIdColumnWidth()));
    if (getLegendContentWidth() < initialLegendContentWidth) {
      setLegendContentWidth(initialLegendContentWidth);
      this.pageWidth = getLegendContentWidth() + getLegendIdColumnWidth() + 3 * pageMargin;
    }

    this.numberOfEntriesPerColumn = (int) Math.floor((getPageHeight() - pageMargin - getTopMargin()) / (legendEntryHeight));

    //add a number of columns for the offset
    this.columnsInducedByOffset = calculateColumnsInducedByOffset();

    this.numberOfColumnsPerPage = (int) Math.floor((getPageWidth() - pageMargin) / (getLegendContentWidth() + getLegendIdColumnWidth() + pageMargin));
  }

  private int calculateColumnsInducedByOffset() {
    double columnWidth = getLegendContentWidth() + getLegendIdColumnWidth();

    return (int) Math.ceil(offset / columnWidth);
  }

  private double getTopMargin() {
    return topMargin;
  }

  /**
   * Trims a text to fit into the legend box.
   * 
   * @param originalEntryText
   *          The original text.
   * @return If shorter than the box, the original text is returned. Otherwise, a trimmed text is
   *         returned.
   */
  protected String getTrimmedEntryText(String originalEntryText) {

    if (legendMode.equals(LegendMode.NEW_PAGE) && getMaxCharacterCount() < originalEntryText.length()) {
      return originalEntryText.substring(0, getMaxCharacterCount() - 3) + "...";
    }
    return originalEntryText;
  }

  public void setFrameSize(double frameWidth, double frameHeight, double frameX, double frameY) {
    this.frameHeight = frameHeight;
    this.frameWidth = frameWidth;
    this.frameX = frameX;
    this.frameY = frameY;
  }

  public void setPageSize(double pageWidth, double pageHeight) {
    this.pageHeight = pageHeight;
    this.pageWidth = pageWidth;
  }

  public void setLegendEntryHeight(double legendEntryHeight) {
    this.legendEntryHeight = legendEntryHeight;
  }

  protected double getLegendEntryHeight() {
    return legendEntryHeight;
  }

  protected void setLegendContentWidth(double legendContentWidth) {
    this.legendContentWidth = legendContentWidth;
  }

  protected double getLegendContentWidth() {
    return legendContentWidth;
  }

  protected void setLegendIdColumnWidth(double legendIdColumnWidth) {
    this.legendIdColumnWidth = legendIdColumnWidth;
  }

  protected double getLegendIdColumnWidth() {
    return legendIdColumnWidth;
  }

  protected void setLegendTextSizePt(double legendTextSizePt) {
    this.legendTextSizePt = legendTextSizePt;
  }

  protected double getLegendTextSizePt() {
    return legendTextSizePt;
  }

  protected double getFrameY() {
    return frameY;
  }

  protected double getFrameX() {
    return frameX;
  }

  protected int getMaxCharacterCount() {
    return maxCharacterCount;
  }

  protected void setMaxCharacterCount(int maxCharacterCount) {
    this.maxCharacterCount = maxCharacterCount;
  }

  protected abstract void estimateMaxCharacterCount();

  protected double getPageHeight() {
    return pageHeight;
  }

  protected double getPageWidth() {
    return pageWidth;
  }

  protected int getNumberOfColumnsPerPage() {
    return numberOfColumnsPerPage;
  }

  protected int getNumberOfEntriesPerColumn() {
    return numberOfEntriesPerColumn;
  }

  protected String getHeaderId() {
    return headerId;
  }

  protected String getHeaderName(String entryCategoryName) {
    if (entryCategoryName == null || entryCategoryName.isEmpty()) {
      return headerName;
    }
    else {
      return entryCategoryName;
    }
  }

  protected List<String> getSortedKeys() {
    List<String> keys = Lists.newArrayList(legendCategories.keySet());
    Collections.sort(keys);
    return keys;
  }

  protected double getFrameHeight() {
    return frameHeight;
  }

  protected double getFrameWidth() {
    return frameWidth;
  }
}

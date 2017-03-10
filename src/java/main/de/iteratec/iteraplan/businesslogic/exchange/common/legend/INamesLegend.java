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

import java.util.List;
import java.util.Map;


/**
 *
 */
public interface INamesLegend {

  /**
   * @return true if the names legend should be displayed
   */
  boolean displayNamesLegend();

  /**
   * After all parameters have been set, this method should be called to trigger the calculation of
   * the legends' columns, pages etc. The implementations should take care of the specific features
   * for SVG and Visio.
   * 
   * @return A list of {@link LogicalPage}s. Each of those encapsulates the beginning position and
   *         the width of a logical page created for the names legend. The original page is not
   *         included. This is needed for the creation of further shapes on the created logical
   *         pages.
   */
  List<LogicalPage> createLegend();

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
  String getScreenName(String ownName, String completeName, double fieldWidth, double fieldTextSizePt, int entryId);

  /**
   * Estimates the (implementation-specific) width of the given text.
   * 
   * @param textLength
   *          The text length in number of characters.
   * @param textSizePt
   *          The size of the text in pt.
   * @return The width in the units of the implementation.
   */
  double getTextWidth(int textLength, double textSizePt);

  /**
   * Retrieves the width of the legend in the units of the implementation.
   * 
   * @return The legend width.
   */
  double getLegendWidth();

  /**
   * Returns the height of the name legend as number of legend entries, including
   * space for header and between legend parts as appropriate.
   * @return a number representing the names legend height as multiple of a names legend entry height
   */
  int getNamesLegendHeight();

  /**
   * Returns whether the names legend contains entries or not
   * @return true when the names legend doesn't contain entries
   */
  boolean isEmpty();

  Map<String, List<NamesLegendEntry>> getLegendCategories();

  LegendMode getLegendMode();

  void setLegendMode(LegendMode legendMode);

  void addOffset(double offsetAdd);

  void setTopMargin(double topMargin);

  double getLegendOffsetCount();

  /**
   * Adds an entry to the {@link NamesLegend}, but only if the element's name doesn't fit into the
   * text field.
   * @param ownName
   *          The own (non-hierarchical) name of the element represented by an entry.
   * @param completeName
   *          The full (hierarchical) name of the element to be represented. Can be <b>null</b> if
   *          the element only has an own name.
   * @param entryCategory
   *          Name of the category of the names legend the entry should be added to. Allows to divide
   *          the names legend into logical blocks.
   * @param fieldWidth
   *          The width of the field in which the screen name should fit in the units of the
   *          implementation.
   * @param fieldTextSizePt
   *          The size of the text in the target object(shape) in pt.
   * @param entryUrl
   *          The URL assigned to the entry's element
   * @return The estimated screen name, as in {@link #getScreenName(String, String, double, double, int)}.
   */
  String addLegendEntry(String ownName, String completeName, String entryCategory, double fieldWidth, double fieldTextSizePt, String entryUrl);

  /**
   * Sets the size of the frame on the main page in which the names legend can be created. This is
   * to be set before creating the legend structure as the logic needs this parameters to decide
   * whether the legend is to be created in the main page or an extra page(s) is to be created.
   * 
   * @param frameWidth
   *          The height in units of the implementation.
   * @param frameHeight
   *          The width in units of the implementation.
   * @param frameX
   *          The x-coordinate of the frame in units of the implementation.
   * @param frameY
   *          The y-coordinate of the frame in the units of the implementation.
   */
  void setFrameSize(double frameWidth, double frameHeight, double frameX, double frameY);

  /**
   * Sets the size of the page of the document. This should be set before creating the legend
   * structure as it is needed so that the legend can be correctly distributed over the new pages
   * (if these are to be created).
   * 
   * @param pageWidth
   *          The page height in units of the implementation.
   * @param pageHeight
   *          The page width in units of the implementation.
   */
  void setPageSize(double pageWidth, double pageHeight);

  /**
   * Sets the (implementation-specific) height of a single row of the names legend.
   * 
   * @param legendEntryHeight
   *          The height in units of the implementation.
   */
  void setLegendEntryHeight(double legendEntryHeight);

  /**
   * A small enumeration representing the three different modes in which a names legend can be
   * created. Those are: <br>
   * <b>AUTO:</b> The legend checks whether it fits into the provided frame (see implementations)
   * and if so the entries are created there. If the legend wouldn't fit, it is created on a new
   * (logical) page to the right of the graphic. <br>
   * <b>IN_PAGE:</b> In this mode the legend is created in the provided frame. If the legend is
   * higher than the frame, the page is made accordingly higher. <br>
   * <b>NEW_PAGE:</b> The legend is explicitly created on a new (logical) page to the right of the
   * existing page(s).
   */
  public enum LegendMode {

    AUTO(0), IN_PAGE(1), NEW_PAGE(2);

    private final int legendMode;

    private LegendMode(int mode) {
      this.legendMode = mode;
    }

    public int getValue() {
      return legendMode;
    }
  }

}

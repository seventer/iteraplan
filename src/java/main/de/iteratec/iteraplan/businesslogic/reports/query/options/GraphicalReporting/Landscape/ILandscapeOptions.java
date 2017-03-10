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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.IGraphicalExportBaseOptions;


public interface ILandscapeOptions extends IGraphicalExportBaseOptions {

  void setStrictRelations(boolean strictRelations);

  boolean isStrictRelations();

  void setShowUnspecifiedRelations(boolean showUnspecifiedRelations);

  boolean isShowUnspecifiedRelations();

  /**
   * If global scaling is enabled, the entire graphic (excluding pages containing the names legend) will be scaled to
   * fit a fixed page size (currently DIN A1), the exact dimensions of which are specific for SVG and Visio and can be
   * found in the corresponding diagram export classes. If global scaling is not enabled, the size of the graphic
   * elements is fixed and the page is resized appropriately.
   * @return
   *  The selected value for the global scaling. Default is <b>true</b>.
   */
  boolean isGlobalScalingEnabled();

  /**
   * If true, a content element which occupies neighouring cells is spanned over all those cells, i.e. in this case
   * a connected component of content elements over a number of cells is built (the traditional landscape diagram).
   * If disabled, each cell is independent of all other cells in its row/column. Thus, content element sizes and positions
   * are determined on the basis of the specific cell and not on the basis of the entire connected component of cells.
   * @return
   *    The current setting.
   */
  boolean isSpanContentBetweenCells();
}

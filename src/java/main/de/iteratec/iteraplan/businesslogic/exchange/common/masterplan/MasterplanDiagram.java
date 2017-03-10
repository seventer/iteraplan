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
package de.iteratec.iteraplan.businesslogic.exchange.common.masterplan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.common.MessageAccess;


/**
 * A class to capture all data needed to fill a masterplan diagram.
 */
public class MasterplanDiagram {

  public static final String                     REL_TYPE_PREFIX        = " - ";

  static final String                            MASTERPLAN_LABEL_BEGIN = "graphicalExport.masterplan.labels.begin";
  static final String                            MASTERPLAN_LABEL_END   = "graphicalExport.masterplan.labels.end";

  public static final String                     DATE_UNSPECIFIED       = "-";

  private String                                 level0Header           = "";
  private String                                 level1Header           = "";
  private String                                 level2Header           = "";

  private String                                 statusHeader           = "";

  private final Map<ColumnEntry, String>         customColumnHeaders;
  private final BiMap<ColumnEntry, Integer>      customColumnPositions;

  private final List<MasterplanBuildingBlockRow> bbRows;

  private int                                    timespanLength         = 0;
  private List<MasterplanTimespanYear>           timespan               = new ArrayList<MasterplanTimespanYear>();

  public MasterplanDiagram() {
    this.bbRows = Lists.newArrayList();
    this.customColumnHeaders = Maps.newHashMap();
    this.customColumnPositions = HashBiMap.create();
  }

  /**
   * Adds a building block row to the masterplan diagram.
   * @param bbRow
   *    The building block row to add.
   */
  public void addBuildingBlockRow(MasterplanBuildingBlockRow bbRow) {
    this.bbRows.add(bbRow);
  }

  /**
   * @return
   *    The (ordered implicitly on insertion) list of building block rows.
   */
  public List<MasterplanBuildingBlockRow> getBuildingBlockRows() {
    return Lists.newArrayList(this.bbRows);
  }

  /**
   * @return
   *    The height of the masterplan diagram, measured in the number of
   *    timeline rows over all building block rows. Note that this measurement neglects
   *    the height of the header, which has to be estimated separately.
   */
  public int getLogicalHeight() {
    int height = 0;
    for (MasterplanBuildingBlockRow bbRow : bbRows) {
      height = height + bbRow.getLogicalHeight();
    }
    return height;
  }

  public String getHeader(int level) {
    if (level == 0) {
      return getLevel0Header();
    }
    else if (level == 1) {
      return getLevel1Header();
    }
    return getLevel2Header();
  }

  /**
   * @return
   *    The header for the level 0 (base) building block. Can not be empty or null.
   */
  public String getLevel0Header() {
    return level0Header;
  }

  /**
   * Sets the level 0 (base) building block header of the masterplan diagram.
   * @param level0Header
   *    The level 0 header which must not be empty or null.
   */
  public void setLevel0Header(String level0Header) {
    this.level0Header = level0Header == null ? "" : level0Header;
  }

  /**
   * @return
   *  The header for the level 1 (first related) building block. May be empty, but not null.
   */
  public String getLevel1Header() {
    return level1Header;
  }

  /**
   * Sets the level 1 (first related) building block header of the masterplan diagram.
   * @param level1Header
   *    The level 1 header. May be empty, but not null.
   */
  public void setLevel1Header(String level1Header) {
    this.level1Header = level1Header == null ? "" : level1Header;
  }

  /**
   * @return
   *    The header for the level 2 (second related) building block. May be empty, but not null.
   */
  public String getLevel2Header() {
    return level2Header;
  }

  /**
   * Sets the level 2 (second related) building block header of the masterplan diagram.
   * @param level2Header
   *    The level 2 header. May be empty, but not null.
   */
  public void setLevel2Header(String level2Header) {
    this.level2Header = level2Header == null ? "" : level2Header;
  }

  /**
   * @return
   *    The header for the status column of the masterplan diagram. If the header is empty, no status column
   *    should be added to the diagram. May not be null.
   */
  public String getStatusHeader() {
    return statusHeader;
  }

  /**
   * Sets the status header of the masterplan diagram. If no status column should be added to the
   * diagram, the status header should be set to an empty string.
   * @param statusHeader
   *    The status header. May be empty, but not null.
   */
  public void setStatusHeader(String statusHeader) {
    this.statusHeader = statusHeader == null ? "" : statusHeader;
  }

  /**
   * @return
   *    The localized header for the start date column of the diagram.
   */
  public String getBeginHeader() {
    return MessageAccess.getString(MASTERPLAN_LABEL_BEGIN);
  }

  /**
   * @return
   *    The localized header for the end date column of the diagram.
   */
  public String getEndHeader() {
    return MessageAccess.getString(MASTERPLAN_LABEL_END);
  }

  /**
   * Adds a custom column header to the masterplan diagram.
   * Creates an internal ordering of the custom columns.
   * @param column
   *    The custom column to add.
   * @param headerValue
   *    The value for the header.
   */
  public void addCustomColumn(ColumnEntry column, String headerValue, MasterplanRowTypeOptions supportedRowType) {
    if (customColumnHeaders.get(column) == null) {
      this.customColumnHeaders.put(column, headerValue);
      this.customColumnPositions.put(column, Integer.valueOf(this.customColumnPositions.entrySet().size()));
    }
  }

  /**
   * @return
   *    All custom columns added to the diagram.
   */
  public List<ColumnEntry> getCustomColumns() {
    List<Integer> sortedColumPositions = Lists.newArrayList(this.customColumnPositions.values());
    Collections.sort(sortedColumPositions);
    List<ColumnEntry> orderedCustomColumns = Lists.newArrayList();
    for (Integer colPosition : sortedColumPositions) {
      orderedCustomColumns.add(this.customColumnPositions.inverse().get(colPosition));
    }
    return orderedCustomColumns;
  }

  /**
   * Retrieves the presentation header for a custom column.
   * @param customColumn
   *    The custom column whose presentation header is required.
   * @return
   *    The presentation header.
   */
  public String getCustomColumnHeader(ColumnEntry customColumn) {
    return this.customColumnHeaders.get(customColumn);
  }

  /**
   * Retrieves the logical position of a custom column within the set of custom
   * columns of this masterplan diagram.
   * @param customColumn
   *    The custom column whose logical position (within the set of custom columns)
   *    is required.
   * @return
   *    The position of the custom column.
   */
  public Integer getCustomColumnLogicalPosition(ColumnEntry customColumn) {
    return this.customColumnPositions.get(customColumn);
  }

  public void setTimespan(List<MasterplanTimespanYear> timespan) {
    this.timespan = timespan;
    this.timespanLength = 0;
    for (MasterplanTimespanYear year : timespan) {
      this.timespanLength = this.timespanLength + year.getMonths().size();
    }
  }

  public void addYearToTimespan(MasterplanTimespanYear year) {
    this.timespan.add(year);
    this.timespanLength = timespanLength + year.getMonths().size();
  }

  public List<MasterplanTimespanYear> getTimespan() {
    return timespan;
  }

  /**
   * The length of the timespan in number of months.
   * 
   * @return The total number of months in the selected timespan.
   */
  public int getTimenspanLength() {
    return timespanLength;
  }

}

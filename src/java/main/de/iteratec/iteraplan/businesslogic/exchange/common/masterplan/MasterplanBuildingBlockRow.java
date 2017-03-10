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

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.interfaces.StatusEntity;


/**
 * Represents a composite row in a masterplan diagram.
 * The row contains exactly one building block, but may
 * have multiple timeline features, which result in a number of rows.
 * Each of these timeline rows provides the timeline (runtime period or date interval) information
 * for the building block represented by this row.
 */
public class MasterplanBuildingBlockRow {

  private final MasterplanRowTypeOptions    rowType;
  private final BuildingBlock               buildingBlock;
  private final Map<ColumnEntry, String>    customColumnValues;
  private final List<MasterplanTimespanRow> timspanRows;

  public MasterplanBuildingBlockRow(MasterplanRowTypeOptions rowType, BuildingBlock buildingBlock) {
    this.buildingBlock = buildingBlock;
    this.rowType = rowType;
    this.customColumnValues = Maps.newHashMap();
    this.timspanRows = Lists.newArrayList();
  }

  /**
   * @return
   *  The height measured as the number of timeline feature rows.
   */
  public int getLogicalHeight() {
    return Math.max(1, this.rowType.getTimelineFeatures().size());
  }

  /**
   * @return
   *  The row type of this building block row.
   */
  public MasterplanRowTypeOptions getRowType() {
    return this.rowType;
  }

  /**
   * @return
   *    The building block represented by this row.
   */
  public BuildingBlock getBuildingBlock() {
    return this.buildingBlock;
  }

  /**
   * @return
   *    The name of the building block for presentation,
   *    in accordance with the configuration of the diagram.
   */
  public String getRowName() {
    if (buildingBlock instanceof HierarchicalEntity<?>) {
      if (this.rowType.isHierarchicalSort()) {
        return buildingBlock.getHierarchicalName();
      }
      else {
        return buildingBlock.getNonHierarchicalName();
      }
    }
    else {
      return buildingBlock.getNonHierarchicalName();
    }
  }

  /**
   * @return
   *    A mapping from custom columns to their respective values for
   *    the building block represented by this row.
   */
  public Map<ColumnEntry, String> getCustomColumnValues() {
    return Maps.newHashMap(this.customColumnValues);
  }

  /**
   * Adds the value of a custom columns to the value mapping for
   * this row.
   * @param columnEntry
   *    The column entry representing the custom column attribute.
   * @param value
   *    The value for the building block represented by this row.
   *    By convention, the value should not be null, but rather
   *    an empty string, if no actual value exists.
   */
  public void addCustomColumnValue(ColumnEntry columnEntry, String value) {
    this.customColumnValues.put(columnEntry, value);
  }

  /**
   * @return
   *    Retrieves the status value for this building block. The value is never
   *    null, but can be empty, if the building block has no status set,
   *    or the building block type doesen't support status.
   */
  public String getStatusValue() {
    if (this.rowType.hasStatus()) {
      return MessageAccess.getString(((StatusEntity) buildingBlock).getTypeOfStatusAsString());
    }
    return "";
  }

  /**
   * Adds a timespan row to this building block row.
   * @param timespanRow
   *    The timespan row to add.
   */
  public void addTimespanRow(MasterplanTimespanRow timespanRow) {
    this.timspanRows.add(timespanRow);
  }

  /**
   * @return
   *    The list of all timespan rows for this building block rows.
   *    The ordering is determined by the order of creation.
   */
  public List<MasterplanTimespanRow> getTimespanRows() {
    return Lists.newArrayList(this.timspanRows);
  }

}

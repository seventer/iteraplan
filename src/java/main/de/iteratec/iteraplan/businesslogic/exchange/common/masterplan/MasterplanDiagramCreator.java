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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry.COLUMN_TYPE;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.IMasterplanOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.TimelineFeature;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.BuildingBlock;


public class MasterplanDiagramCreator {

  private static final String               MASTERPLAN_LABEL_STATUS = "graphicalExport.masterplan.labels.status";

  private final BuildingBlockServiceLocator buildingBlockServiceLocator;
  private final AttributeTypeService        attributeTypeService;
  private final Locale                      locale;

  private final List<BuildingBlock>         selectedLevel0BuildingBlocks;
  private final IMasterplanOptions          masterplanOptions;

  private MasterplanDiagram                 masterplanDiagram;
  private Date                              globalStartDate;
  private Date                              globalEndDate;
  private double                            totalDays;

  public MasterplanDiagramCreator(IMasterplanOptions masterplanOptions, BuildingBlockServiceLocator buildingBlockServiceLocator,
      AttributeTypeService attributeTypeService, List<BuildingBlock> selectedLevel0BuildingBlocks) {
    this.masterplanOptions = masterplanOptions;
    this.buildingBlockServiceLocator = buildingBlockServiceLocator;
    this.attributeTypeService = attributeTypeService;
    this.selectedLevel0BuildingBlocks = Lists.newArrayList(selectedLevel0BuildingBlocks);

    MasterplanRowTypeOptions.sortEntities(this.selectedLevel0BuildingBlocks, this.masterplanOptions.getLevel0Options().isHierarchicalSort());
    this.locale = UserContext.getCurrentLocale();
  }

  public MasterplanDiagram createMasterplanDiagram() {
    this.masterplanDiagram = new MasterplanDiagram();

    createHeaders();
    createHeaderTimespan();
    createRows();

    return this.masterplanDiagram;
  }

  private void createHeaders() {
    this.masterplanDiagram.setLevel0Header(getBbHeader(masterplanOptions.getLevel0Options()));
    this.masterplanDiagram.setLevel1Header(getBbHeader(masterplanOptions.getLevel1Options()));
    this.masterplanDiagram.setLevel2Header(getBbHeader(masterplanOptions.getLevel2Options()));

    if (hasStatusColumn()) {
      this.masterplanDiagram.setStatusHeader(MessageAccess.getStringOrNull(MASTERPLAN_LABEL_STATUS, locale));
    }

    createCustomColumnHeaders();
  }

  private void createCustomColumnHeaders() {
    createCustomColumnHeadersForRowType(masterplanOptions.getLevel0Options());
    createCustomColumnHeadersForRowType(masterplanOptions.getLevel1Options());
    createCustomColumnHeadersForRowType(masterplanOptions.getLevel2Options());
  }

  private void createCustomColumnHeadersForRowType(MasterplanRowTypeOptions rowType) {
    if (rowType == null || rowType.getSelectedCustomColumns().isEmpty()) {
      return;
    }
    for (ColumnEntry entry : rowType.getSelectedCustomColumns()) {
      if (entry.getEnumType().equals(COLUMN_TYPE.ATTRIBUTE)) {
        masterplanDiagram.addCustomColumn(entry, entry.getHead(), rowType);
      }
      else {
        String headerName = MessageAccess.getStringOrNull(entry.getHead(), locale);
        masterplanDiagram.addCustomColumn(entry, headerName, rowType);
      }
    }
  }

  private boolean hasStatusColumn() {
    return masterplanOptions.getLevel0Options().hasStatus()
        || (masterplanOptions.getLevel1Options() != null && masterplanOptions.getLevel1Options().hasStatus())
        || (masterplanOptions.getLevel2Options() != null && masterplanOptions.getLevel2Options().hasStatus());
  }

  /**
   * Retrieves the header of the column containing the names of the building blocks.
   *
   * @return The header as string.
   */
  private String getBbHeader(MasterplanRowTypeOptions rowTypeOptions) {
    if (rowTypeOptions == null) {
      return "";
    }
    else if (rowTypeOptions.getLevel() == 0) {
      return MessageAccess.getStringOrNull(rowTypeOptions.getTypeOfBuildingBlock().getValue(), locale);
    }
    return MessageAccess.getStringOrNull(rowTypeOptions.getRelationToBbType(), locale);
  }

  private void createRows() {
    MasterplanRowTypeOptions level0Options = masterplanOptions.getLevel0Options();
    MasterplanRowTypeOptions level1Options = masterplanOptions.getLevel1Options();
    MasterplanRowTypeOptions level2Options = masterplanOptions.getLevel2Options();

    boolean hasLevel0CustomColumns = level0Options.getSelectedCustomColumns().size() > 0;
    boolean hasLevel1CustomColumns = level1Options != null && level1Options.getSelectedCustomColumns().size() > 0;
    boolean hasLevel2CustomColumns = level2Options != null && level2Options.getSelectedCustomColumns().size() > 0;

    Map<ColumnEntry, Map<BuildingBlock, String>> level0CustomColumnValues = null;
    Map<ColumnEntry, Map<BuildingBlock, String>> level1CustomColumnValues = null;
    Map<ColumnEntry, Map<BuildingBlock, String>> level2CustomColumnValues = null;

    if (hasLevel0CustomColumns) {
      MasterplanCustomColumnHelper helper = new MasterplanCustomColumnHelper(Sets.newHashSet(this.selectedLevel0BuildingBlocks),
          level0Options.getSelectedCustomColumns(), attributeTypeService, locale);
      level0CustomColumnValues = helper.projectColums();
    }

    for (BuildingBlock l0Bb : this.selectedLevel0BuildingBlocks) {
      MasterplanBuildingBlockRow bbRow = createRow(level0Options, l0Bb, level0CustomColumnValues);
      this.masterplanDiagram.addBuildingBlockRow(bbRow);

      if (level1Options != null) {
        List<BuildingBlock> relatedLevel1Bbs = level1Options.getRelatedBuildingBlocks(l0Bb, buildingBlockServiceLocator);

        //create level 1 custom column mappings
        if (hasLevel1CustomColumns) {
          MasterplanCustomColumnHelper helper = new MasterplanCustomColumnHelper(Sets.newHashSet(relatedLevel1Bbs),
              level1Options.getSelectedCustomColumns(), attributeTypeService, locale);
          level1CustomColumnValues = helper.projectColums();
        }

        //create level 1 rows
        for (BuildingBlock l1Bb : relatedLevel1Bbs) {
          MasterplanBuildingBlockRow l1BbRow = createRow(level1Options, l1Bb, level1CustomColumnValues);
          this.masterplanDiagram.addBuildingBlockRow(l1BbRow);

          if (level2Options != null) {
            List<BuildingBlock> relatedLevel2Bbs = level2Options.getRelatedBuildingBlocks(l1Bb, buildingBlockServiceLocator);

            //create level 2 custom column mappings
            if (hasLevel2CustomColumns) {
              MasterplanCustomColumnHelper helper = new MasterplanCustomColumnHelper(Sets.newHashSet(relatedLevel2Bbs),
                  level2Options.getSelectedCustomColumns(), attributeTypeService, locale);
              level2CustomColumnValues = helper.projectColums();
            }

            //create level 2 rows
            for (BuildingBlock l2Bb : relatedLevel2Bbs) {
              MasterplanBuildingBlockRow l2BbRow = createRow(level2Options, l2Bb, level2CustomColumnValues);
              this.masterplanDiagram.addBuildingBlockRow(l2BbRow);
            }
          }
        }
      }
    }
  }

  private MasterplanBuildingBlockRow createRow(MasterplanRowTypeOptions rowType, BuildingBlock buildingBlock,
                                               Map<ColumnEntry, Map<BuildingBlock, String>> customColumnValues) {
    MasterplanBuildingBlockRow row = new MasterplanBuildingBlockRow(rowType, buildingBlock);

    if (customColumnValues != null) {
      for (Entry<ColumnEntry, Map<BuildingBlock, String>> entry : customColumnValues.entrySet()) {
        String value = entry.getValue().get(buildingBlock);
        if (value == null) {
          value = "";
        }
        row.addCustomColumnValue(entry.getKey(), value);
      }
    }

    for (TimelineFeature timelineFeature : rowType.getTimelineFeatures()) {
      createTimespanRow(row, timelineFeature);
    }
    return row;
  }

  private void createTimespanRow(MasterplanBuildingBlockRow row, TimelineFeature timelineFeature) {
    Date bbStartDate = timelineFeature.getFrom(row.getBuildingBlock());
    Date bbEndDate = timelineFeature.getTo(row.getBuildingBlock());

    MasterplanTimespanRow timespanRow = new MasterplanTimespanRow(timelineFeature);
    timespanRow.setTimespanCaption(timelineFeature.getCaption(row.getBuildingBlock()));

    if (bbStartDate == null) {
      bbStartDate = GeneralHelper.MIN_DATE;
      timespanRow.setFromDate(MasterplanDiagram.DATE_UNSPECIFIED);
    }
    else {
      timespanRow.setFromDate(DateUtils.formatAsString(bbStartDate, locale));
    }

    if (bbEndDate == null) {
      bbEndDate = GeneralHelper.MAX_DATE;
      timespanRow.setToDate(MasterplanDiagram.DATE_UNSPECIFIED);
    }
    else {
      timespanRow.setToDate(DateUtils.formatAsString(bbEndDate, locale));
    }

    if (bbEndDate.before(globalStartDate) || bbStartDate.after(globalEndDate) || bbEndDate.before(bbStartDate)) {
      timespanRow.setOutOfTimespan(true);
    }
    else {
      if (bbStartDate.before(globalStartDate)) {
        // Field starts at the global start date
        timespanRow.setRowFieldStart(0);

        if (bbEndDate.after(globalEndDate)) {
          // Complete field to be spanned
          timespanRow.setRowFieldLength(1);
        }
        else {
          // Length is from globalStart to localEnd
          double localDays = MasterplanCommon.getTotalDays(globalStartDate, bbEndDate);
          double length = localDays / totalDays;
          timespanRow.setRowFieldLength(length);
        }
      }
      else {
        if (bbEndDate.after(globalEndDate)) {
          // localStart to globalEnd as length, localStart as beginning
          double globalToLocalStart = MasterplanCommon.getTotalDays(globalStartDate, bbStartDate);
          double beginning = globalToLocalStart / totalDays;
          timespanRow.setRowFieldStart(beginning);

          double localStartToGlobalEnd = MasterplanCommon.getTotalDays(bbStartDate, globalEndDate);
          double length = localStartToGlobalEnd / totalDays;
          timespanRow.setRowFieldLength(length);
        }
        else {
          // Start @ localStart, end @ localEnd, length is diff between localStart and localEnd
          double globalToLocalStart = MasterplanCommon.getTotalDays(globalStartDate, bbStartDate);
          double beginning = globalToLocalStart / totalDays;
          timespanRow.setRowFieldStart(beginning);

          double localStartToLocalEnd = MasterplanCommon.getTotalDays(bbStartDate, bbEndDate);
          double length = localStartToLocalEnd / totalDays;
          timespanRow.setRowFieldLength(length);
        }
      }
    }
    row.addTimespanRow(timespanRow);
  }

  private void createHeaderTimespan() {
    // We span the time interval to the first (last) day of the first (last) month in the selection
    // as the masterplan diagram has month granularity
    Calendar calendar = Calendar.getInstance();

    globalEndDate = DateUtils.parseAsDate(masterplanOptions.getEndDateString(), locale);
    if (globalEndDate == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.TIMESPAN_NOT_CLOSED);
    }
    calendar.setTime(globalEndDate);
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    globalEndDate = calendar.getTime();

    globalStartDate = DateUtils.parseAsDate(masterplanOptions.getStartDateString(), locale);
    if (globalStartDate == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.TIMESPAN_NOT_CLOSED);
    }
    calendar.setTime(globalStartDate);
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
    globalStartDate = calendar.getTime();

    if (globalEndDate.before(globalStartDate)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INVALID_TIMESPAN);
    }

    totalDays = MasterplanCommon.getTotalDays(globalStartDate, globalEndDate);
    int totalMonths = MasterplanCommon.getTotalMonths(globalStartDate, globalEndDate);

    int startMonth = calendar.get(Calendar.MONTH);
    int lastMonth = startMonth + totalMonths;
    int year = calendar.get(Calendar.YEAR);
    int positionCount = 0;

    List<String> months = new ArrayList<String>();
    List<MasterplanTimespanYear> timespanYears = new ArrayList<MasterplanTimespanYear>();

    for (int monthCount = startMonth; monthCount < lastMonth; monthCount++) {

      months.add(MasterplanCommon.getShortMonthForInt(locale, monthCount));

      if ((monthCount + 1) % 12 == 0 || monthCount == (lastMonth - 1)) {
        MasterplanTimespanYear tYear = new MasterplanTimespanYear();
        tYear.setMonths(months);
        tYear.setLength(months.size());
        tYear.setYearString(Integer.toString(year));
        tYear.setStartPosition(positionCount - months.size() + 1);
        timespanYears.add(tYear);
        year++;
        months = new ArrayList<String>();
      }

      positionCount++;
    }

    masterplanDiagram.setTimespan(timespanYears);
  }

}

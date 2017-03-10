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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.IMasterplanRelatedElementsGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedADGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedBDGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedBFGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedBMGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedBOGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedBPGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedBUGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedBaseComponentsGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedChildrenGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedIEGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedISDGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedISIGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedISRGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedParentComponentsGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedParentGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedPredecessorsGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedProductsGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedProjectsGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedSuccessorsGetter;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters.RelatedTCRGetter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.RuntimePeriodDelegate;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.iteraplan.model.sorting.BuildingBlockSortHelper;


public class MasterplanRowTypeOptions implements Serializable {

  private static final long                                   serialVersionUID         = -4230251415475789568L;

  private static final List<IMasterplanRelatedElementsGetter> RELATED_ELEMENTS_GETTERS = getRelatedElementsGetters();

  private static List<IMasterplanRelatedElementsGetter> getRelatedElementsGetters() {
    List<IMasterplanRelatedElementsGetter> getters = Lists.newArrayList();
    getters.add(RelatedADGetter.getInstance());
    getters.add(RelatedBaseComponentsGetter.getInstance());
    getters.add(RelatedBDGetter.getInstance());
    getters.add(RelatedBFGetter.getInstance());
    getters.add(RelatedBMGetter.getInstance());
    getters.add(RelatedBOGetter.getInstance());
    getters.add(RelatedBPGetter.getInstance());
    getters.add(RelatedBUGetter.getInstance());
    getters.add(RelatedChildrenGetter.getInstance());
    getters.add(RelatedIEGetter.getInstance());
    getters.add(RelatedISDGetter.getInstance());
    getters.add(RelatedISIGetter.getInstance());
    getters.add(RelatedISRGetter.getInstance());
    getters.add(RelatedParentComponentsGetter.getInstance());
    getters.add(RelatedParentGetter.getInstance());
    getters.add(RelatedPredecessorsGetter.getInstance());
    getters.add(RelatedProductsGetter.getInstance());
    getters.add(RelatedProjectsGetter.getInstance());
    getters.add(RelatedSuccessorsGetter.getInstance());
    getters.add(RelatedTCRGetter.getInstance());

    return getters;
  }

  private final String                selectedBbType;
  private final TypeOfBuildingBlock   typeOfBuildingBlock;

  private final String                relationToBbType;

  private final int                   level;

  private final List<BBAttribute>     availableColorAttributes;
  private final List<TimelineFeature> availableTimeLines;
  private final List<TimelineFeature> selectedTimeLines;

  private boolean                     useDefaultColoring             = false;
  private ColorDimensionOptionsBean   colorOptions                   = new ColorDimensionOptionsBean();

  private List<String>                availableRelatedTypes          = new ArrayList<String>();
  private boolean                     hierarchicalSort               = false;

  private boolean                     canBuildClosure                = false;
  private boolean                     buildClosure                   = false;

  private final List<ColumnEntry>     availableCustomColumns         = new ArrayList<ColumnEntry>();
  private List<ColumnEntry>           selectedCustomColumns          = new ArrayList<ColumnEntry>();
  private boolean                     additionalCustomColumnsAllowed = true;

  private Integer                     currentDateInterval;
  private String                      currentCustomColumn;
  private String                      columnToRemove;

  public MasterplanRowTypeOptions(String relationToBbType, String selectedBbType, int level, List<DateInterval> availableDateIntervals,
      List<BBAttribute> availableColorAttributes, List<ColumnEntry> availableCustomColumns) {
    this.relationToBbType = relationToBbType;
    this.selectedBbType = selectedBbType;
    this.typeOfBuildingBlock = TypeOfBuildingBlock.fromPropertyString(selectedBbType);
    this.level = level;
    availableTimeLines = Lists.newArrayList();
    selectedTimeLines = Lists.newArrayList();
    if (this.typeOfBuildingBlock.hasRuntimePeriod()) {
      RuntimePeriodTimeline runtimePeriodTimeline = new RuntimePeriodTimeline(0, this);
      selectedTimeLines.add(runtimePeriodTimeline);
    }
    for (DateInterval dateInterval : availableDateIntervals) {
      int position = availableTimeLines.size() + 1;
      this.availableTimeLines.add(new DateIntervalTimeline(position, this, dateInterval));
    }
    this.availableColorAttributes = availableColorAttributes;
    this.availableCustomColumns.addAll(availableCustomColumns);
  }

  public List<BBAttribute> getAvailableColorAttributes() {
    return this.availableColorAttributes;
  }

  public void setColorOptions(ColorDimensionOptionsBean colorOptions) {
    this.colorOptions = colorOptions;
  }

  public ColorDimensionOptionsBean getColorOptions() {
    return this.colorOptions;
  }

  public boolean isUseDefaultColoring() {
    return useDefaultColoring;
  }

  public void setUseDefaultColoring(boolean useDefaultColoring) {
    this.useDefaultColoring = useDefaultColoring;
  }

  public int getLevel() {
    return this.level;
  }

  public String getSelectedBbType() {
    return this.selectedBbType;
  }

  public boolean isHierarchicalSort() {
    return hierarchicalSort;
  }

  public void setHierarchicalSort(boolean hierarchicalSorting) {
    this.hierarchicalSort = hierarchicalSorting;
  }

  public boolean hasStatus() {
    return this.typeOfBuildingBlock.hasStatusAttribute();
  }

  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return this.typeOfBuildingBlock;
  }

  public String getRelationToBbType() {
    return this.relationToBbType;
  }

  public List<BuildingBlock> getRelatedBuildingBlocks(BuildingBlock forBuildingBlock, BuildingBlockServiceLocator buildingBlockServiceLocator) {
    if (relationToBbType == null || relationToBbType.isEmpty()) {
      List<BuildingBlock> result = Lists.newArrayList();
      result.add(forBuildingBlock);
      return result;
    }

    List<BuildingBlock> relatedEntities = filterEntities(getRelatedElements(forBuildingBlock, buildingBlockServiceLocator, relationToBbType,
        buildClosure, hierarchicalSort));
    sortEntities(relatedEntities, hierarchicalSort);
    return relatedEntities;
  }

  public List<TimelineFeature> getAvailableTimeLines() {
    return availableTimeLines;
  }

  public void addTimeline(TimelineFeature timelineFeature) {
    availableTimeLines.remove(timelineFeature);
    selectedTimeLines.add(timelineFeature);
  }

  public void removeTimeLineByPosition(int position) {
    for (TimelineFeature timelineFeature : selectedTimeLines) {
      if (position == timelineFeature.getPosition()) {
        selectedTimeLines.remove(timelineFeature);
        availableTimeLines.add(timelineFeature);
        break;
      }
    }
  }

  public List<TimelineFeature> getTimelineFeatures() {
    return selectedTimeLines;
  }

  public boolean isBuildClosure() {
    return buildClosure;
  }

  public void setBuildClosure(boolean buildClosure) {
    this.buildClosure = buildClosure;
  }

  public boolean isCanBuildClosure() {
    return canBuildClosure;
  }

  public void setCanBuildClosure(boolean canBuildClosure) {
    this.canBuildClosure = canBuildClosure;
  }

  public List<ColumnEntry> getAvailableCustomColumns() {
    return availableCustomColumns;
  }

  public List<ColumnEntry> getSelectedCustomColumns() {
    return selectedCustomColumns;
  }

  public void setSelectedCustomColumns(List<ColumnEntry> customColumns) {
    this.selectedCustomColumns = customColumns;
  }

  public String getCurrentCustomColumn() {
    return currentCustomColumn;
  }

  public void setCurrentCustomColumn(String addedCustomColumn) {
    this.currentCustomColumn = addedCustomColumn;
  }

  public void setColumnToRemove(String columnToRemove) {
    this.columnToRemove = columnToRemove;
  }

  public String getColumnToRemove() {
    return this.columnToRemove;
  }

  public boolean isAdditionalCustomColumnsAllowed() {
    return additionalCustomColumnsAllowed;
  }

  public void setAdditionalCustomColumnsAllowed(boolean additionalCustomColumnsAllowed) {
    this.additionalCustomColumnsAllowed = additionalCustomColumnsAllowed;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof MasterplanRowTypeOptions)) {
      return false;
    }
    return hashCode() == obj.hashCode();
  }

  public int hashCode() {
    return relationToBbType.hashCode() ^ selectedBbType.hashCode() ^ level;
  }

  private static class RuntimePeriodTimeline extends TimelineFeature {

    private static final long serialVersionUID = -6251993592308691511L;

    RuntimePeriodTimeline(int position, MasterplanRowTypeOptions owner) {
      super(position, owner);
    }

    /**
     * The method is null safe, relying on the null safeness of the RuntimePeriodDelegate.
     * @param buildingBlock
     * @return
     *  A non-null runtime period.
     */
    private RuntimePeriod retrieveRuntimePeriod(BuildingBlock buildingBlock) {
      if (!(buildingBlock instanceof RuntimePeriodDelegate)) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }
      RuntimePeriodDelegate delegate = (RuntimePeriodDelegate) buildingBlock;
      return delegate.getRuntimePeriodNullSafe();
    }

    /**{@inheritDoc}**/
    @Override
    public Date getFrom(BuildingBlock buildingBlock) {
      return retrieveRuntimePeriod(buildingBlock).getStart();
    }

    /**{@inheritDoc}**/
    @Override
    public Date getTo(BuildingBlock buildingBlock) {
      return retrieveRuntimePeriod(buildingBlock).getEnd();
    }

    /**{@inheritDoc}**/
    @Override
    public String getName() {
      return MessageAccess.getString("massUpdates.timespan");
    }

    /**{@inheritDoc}**/
    @Override
    public String getCaption(BuildingBlock buildingBlock) {
      return getName();
    }

    /**{@inheritDoc}**/
    @Override
    public boolean isRuntimePeriod() {
      return true;
    }

    /**{@inheritDoc}**/
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof RuntimePeriodTimeline)) {
        return false;
      }
      RuntimePeriodTimeline other = (RuntimePeriodTimeline) obj;
      return getPosition() == other.getPosition() && getOwner().equals(other.getOwner());
    }

    /**{@inheritDoc}**/
    @Override
    public int hashCode() {
      return getOwner().hashCode() ^ RuntimePeriodTimeline.class.hashCode() ^ getPosition();
    }

    /**{@inheritDoc}**/
    @Override
    public String getDefaultColorHex() {
      return Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR;
    }
  }

  private static class DateIntervalTimeline extends TimelineFeature {

    private static final long  serialVersionUID = 142937757887196505L;

    private final DateInterval dateInterval;

    DateIntervalTimeline(int position, MasterplanRowTypeOptions owner, DateInterval dateInterval) {
      super(position, owner);
      this.dateInterval = dateInterval;
    }

    private List<AttributeValue> getAttributeValues(BuildingBlock buildingBlock, DateAT dateAt) {
      return buildingBlock.getAttributeTypeToAttributeValues().getBucketNotNull(dateAt);
    }

    private Date getValue(List<AttributeValue> attributeValues) {
      if (attributeValues.isEmpty()) {
        return null;
      }
      return ((DateAV) attributeValues.iterator().next()).getValue();
    }

    /**{@inheritDoc}**/
    @Override
    public Date getFrom(BuildingBlock buildingBlock) {
      return getValue(getAttributeValues(buildingBlock, dateInterval.getStartDate()));
    }

    /**{@inheritDoc}**/
    @Override
    public Date getTo(BuildingBlock buildingBlock) {
      return getValue(getAttributeValues(buildingBlock, dateInterval.getEndDate()));
    }

    /**{@inheritDoc}**/
    @Override
    public String getName() {
      return dateInterval.getName();
    }

    /**{@inheritDoc}**/
    @Override
    public String getCaption(BuildingBlock buildingBlock) {
      return getName();
    }

    /**{@inheritDoc}**/
    @Override
    public boolean isRuntimePeriod() {
      return false;
    }

    /**{@inheritDoc}**/
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof DateIntervalTimeline)) {
        return false;
      }
      DateIntervalTimeline other = (DateIntervalTimeline) obj;
      return getPosition() == other.getPosition() && dateInterval.equals(other.dateInterval) && getOwner().equals(other.getOwner());
    }

    /**{@inheritDoc}**/
    @Override
    public int hashCode() {
      return getOwner().hashCode() ^ DateIntervalTimeline.class.hashCode() ^ dateInterval.hashCode() ^ getPosition();
    }

    /**{@inheritDoc}**/
    @Override
    public String getDefaultColorHex() {
      if (getOwner().isUseDefaultColoring()) {
        return dateInterval.getDefaultColorHex();
      }
      else {
        return Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR;
      }
    }
  }

  private static Set<BuildingBlock> getRelatedElements(BuildingBlock bb, BuildingBlockServiceLocator buildingBlockServiceLocator, String association,
                                                       boolean buildClosure, boolean hierachical) {
    Set<BuildingBlock> relatedElements = Sets.newHashSet();
    String selectedRelatedType = association;

    for (IMasterplanRelatedElementsGetter getter : RELATED_ELEMENTS_GETTERS) {
      if (getter.supports(selectedRelatedType)) {
        relatedElements.addAll(getter.getRelatedElements(bb, selectedRelatedType));
        break;
      }
    }

    if (buildClosure) {
      Set<BuildingBlock> relatedElementsRecursive = CollectionUtils.hashSet();
      for (BuildingBlock relatedBb : relatedElements) {
        BuildingBlock reloadedBb = reloadBb(relatedBb, buildingBlockServiceLocator, association);
        relatedElementsRecursive.addAll(getRelatedElements(reloadedBb, buildingBlockServiceLocator, association, buildClosure, hierachical));
      }
      relatedElements.addAll(relatedElementsRecursive);
    }

    return relatedElements;
  }

  private static BuildingBlock reloadBb(BuildingBlock bb, BuildingBlockServiceLocator buildingBlockServiceLocator, String... associationsToLoad) {
    BuildingBlockService<? extends BuildingBlock, Integer> service = buildingBlockServiceLocator.getService(bb.getTypeOfBuildingBlock());
    return service.loadObjectById(bb.getId(), associationsToLoad);
  }

  /**
   * Sorts the entities alphabetically.
   */
  public static void sortEntities(List<? extends BuildingBlock> entities, boolean hierarchical) {
    if (hierarchical) {
      BuildingBlockSortHelper.sortByHierarchicalName(entities);
    }
    else {
      BuildingBlockSortHelper.sortByNonHierarchicalName(entities);
    }
  }

  /**
   * Removes the virtual element from a list of building blocks.
   * @param sourceEntities
   *    The list to filter.
   * @return
   *    The filtered list, i.e. with the virtual element removed.
   */
  public static <T extends BuildingBlock> List<T> filterEntities(Collection<T> sourceEntities) {
    List<T> filteredEntities = Lists.newArrayList();
    for (T bb : sourceEntities) {
      if (!bb.getHierarchicalName().trim().equals(AbstractHierarchicalEntity.TOP_LEVEL_NAME)) {
        filteredEntities.add(bb);
      }
    }
    return filteredEntities;
  }

  public List<String> getAvailableRelatedTypes() {
    return availableRelatedTypes;
  }

  public void setAvailableRelatedTypes(List<String> availableRelatedTypes) {
    this.availableRelatedTypes = availableRelatedTypes;
  }

  public Integer getCurrentDateInterval() {
    return currentDateInterval;
  }

  public void setCurrentDateInterval(Integer currentDateInterval) {
    this.currentDateInterval = currentDateInterval;
  }

}

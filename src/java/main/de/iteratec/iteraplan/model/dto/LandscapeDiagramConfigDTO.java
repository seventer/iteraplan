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
package de.iteratec.iteraplan.model.dto;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.LandscapeElementLevels;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.LandscapeOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ManageLandscapeDiagramMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;


public class LandscapeDiagramConfigDTO {

  private static final Logger           LOGGER                   = Logger.getIteraplanLogger(LandscapeDiagramConfigDTO.class);

  /** The list of user selected elements for the top axis. Not filled when columnAttributeId is set. */
  private List<? extends BuildingBlock> topAxisBbs;

  /** The list of user selected elements for the side axis. not filled when rowAttributeId is set. */
  private List<? extends BuildingBlock> sideAxisBbs;

  /** The list of user selected content elements. Always filled */
  private List<? extends BuildingBlock> contentBbs;

  /**
   * The Extension that represents the relation between content building blocks to building blocks
   * in topAxisBb list. Not set when columnAttributeId is set.
   */
  private Extension                     columnExtension;

  /**
   * The id of the attribute for which the values will be displayed on the column side. Not set when
   * topAxisBbs and columnExtension is set.
   */
  private Integer                       columnAttributeId;

  private boolean                       filterEmptyColumns;

  /**
   * The Extension that represents the relation between content building blocks to building blocks
   * in sideAxisBbs list. Not set when rowAttributeId is set.
   */
  private Extension                     rowExtension;

  /**
   * The id of the attribute for which the values will be displayed on the row side. Not set when
   * sideAxisBbs and rowExtension is set.
   */
  private Integer                       rowAttributeId;

  private boolean                       filterEmptyRows;

  /** The type of building blocks in the contentBbs list. Always filled. */
  private Type<?>                       contentType;

  /**
   * If true, the column axis elements will scale with the contained elements. Otherwise the row
   * axis elements will do that.
   */
  private boolean                       columnAxisScalesWithContent;

  /**
   * If true, content elements that represent the same element and lie next to each other are merged
   * together to one bigger content element.
   */
  private boolean                       mergeContent             = true;

  /**
   * Specifies from which level on the hierarchy on the top axis is displayed. All elements in
   * topAxisBbs, which have a level lower than topAxisTopLevel will be ignored.
   */
  private int                           topAxisTopLevel          = 1;

  /**
   * Specifies until which level the hierarchy on the top axis is displayed. All elements in
   * topAxisBbs, which have a level higher than topAxisBottomLevel will not be shown. Their
   * associated content elements however will be merged with their parent.
   */
  private int                           topAxisBottomLevel       = 100;

  /** This level is for the side axis. See {@link #topAxisTopLevel}. */
  private int                           sideAxisTopLevel         = 1;

  /** This level is for the side axis. See {@link #topAxisBottomLevel}. */
  private int                           sideAxisBottomLevel      = 100;

  /** This level is for the content. See {@link #topAxisTopLevel}. */
  private int                           contentTopLevel          = 1;

  /** This level is for the content. See {@link #topAxisBottomLevel}. */
  private int                           contentBottomLevel       = 100;

  private boolean                       hideChildrenElements;

  /**
   * Specifies whether the content elements are to be scaled down to fit in a cell (default, true),
   * or the cell should be enlarged, so that all content elements fit in.
   */
  private boolean                       scaleDownGraphicElements = true;

  /**
   * Specifies whether a names legend is to be used for the names of entities, in case those names
   * don't fit into the resulting graphic elements.
   */
  private boolean                       useNamesLegend           = true;

  /**
   * treat the business mappings of an information system release separately
   */
  private boolean                       strictRelations          = true;

  /**
   * show content elements with missing relations in extra row/column
   */
  private boolean                       showUnspecifiedRelations = false;

  /**
   * span content elements between neighbouring cells
   */
  private boolean                       spanContentBetweenCells  = true;

  public LandscapeDiagramConfigDTO() {
    super();
  }

  public void initFromMemBean(ManageLandscapeDiagramMemoryBean memBean) {
    final LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    LOGGER.debug("Content type: {0}", landscapeOptions.getCurrentContentType().getTypeNameDB());
    setContentType(landscapeOptions.getCurrentContentType());

    QueryResult contentQuery = memBean.getQueryResult(LandscapeOptionsBean.CONTENT_QUERY);
    int size = contentQuery.getSelectedResults().size();
    LOGGER.debug("Number of content elements: {0}", Integer.valueOf(size));
    setContentBbs(contentQuery.getSelectedResults());

    setHideChildrenElements(isHideChildrenStrategySelected(contentQuery.getSelectedPostProcessingStrategies()));

    setColumnConfiguration(memBean);
    setRowConfiguration(memBean);

    setStrictRelations(landscapeOptions.isStrictRelations());
    setShowUnspecifiedRelations(landscapeOptions.isShowUnspecifiedRelations());
    setScaleDownGraphicElements(landscapeOptions.isScaleDownContentElements());
    setSpanContentBetweenCells(landscapeOptions.isSpanContentBetweenCells());
    setUseNamesLegend(landscapeOptions.isUseNamesLegend());

    setColumnAxisScalesWithContent(landscapeOptions.isColumnAxisScalesWithContent());

    final LandscapeElementLevels levels = new LandscapeElementLevels();
    levels.setTopAxisTopLevel(landscapeOptions.getSelectedTopLevelForTopAxis());
    levels.setTopAxisBottomLevel(landscapeOptions.getSelectedBottomLevelForTopAxis());
    levels.setSideAxisTopLevel(landscapeOptions.getSelectedTopLevelForSideAxis());
    levels.setSideAxisBottomLevel(landscapeOptions.getSelectedBottomLevelForSideAxis());
    levels.setContentTopLevel(landscapeOptions.getSelectedTopLevelForContent());
    levels.setContentBottomLevel(landscapeOptions.getSelectedBottomLevelForContent());
    setAxisLevels(levels);

    setFilterEmptyColumns(landscapeOptions.isFilterEmptyColumns());
    setFilterEmptyRows(landscapeOptions.isFilterEmptyRows());
  }

  private boolean isHideChildrenStrategySelected(List<AbstractPostprocessingStrategy<? extends BuildingBlock>> strategies) {
    for (AbstractPostprocessingStrategy<? extends BuildingBlock> strategy : strategies) {
      if (strategy.getNameKeyForPresentation().equals(Constants.POSTPROCESSINGSTRATEGY_HIDE_CHILDREN)) {
        return true;
      }
    }
    return false;
  }

  private void setColumnConfiguration(ManageLandscapeDiagramMemoryBean memBean) {
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();
    int selectedColumnOption = landscapeOptions.getSelectedColumnOption();

    if (selectedColumnOption == 1) {
      List<? extends BuildingBlock> selectedResults = memBean.getQueryResult(LandscapeOptionsBean.COLUMN_QUERY).getSelectedResults();
      LOGGER.debug("Number of column elements: {0}", Integer.valueOf(selectedResults.size()));
      setTopAxisBbs(selectedResults);

      LOGGER.debug("Column relation: {0}", landscapeOptions.getCurrentColumnRelation().getNameKeyForPresentation());
      setColumnExtension(landscapeOptions.getCurrentColumnRelation());
    }
    else if (selectedColumnOption == 2) {
      Integer selectedColumnAttributeId = landscapeOptions.getSelectedColumnAttributeId();
      LOGGER.debug("Column attribute id: {0}", selectedColumnAttributeId);
      setColumnAttributeId(selectedColumnAttributeId);
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  private void setRowConfiguration(ManageLandscapeDiagramMemoryBean memBean) {
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    if (landscapeOptions.getSelectedRowOption() == 1) {
      List<? extends BuildingBlock> selectedResults = memBean.getQueryResult(LandscapeOptionsBean.ROW_QUERY).getSelectedResults();
      LOGGER.debug("Number of row elements: {0}", Integer.valueOf(selectedResults.size()));
      setSideAxisBbs(selectedResults);

      LOGGER.debug("Row relation: {0}", landscapeOptions.getCurrentRowRelation().getNameKeyForPresentation());
      setRowExtension(landscapeOptions.getCurrentRowRelation());
    }
    else if (landscapeOptions.getSelectedRowOption() == 2) {
      LOGGER.debug("Row attribute id: {0}", landscapeOptions.getSelectedRowAttributeId());
      setRowAttributeId(landscapeOptions.getSelectedRowAttributeId());
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  public void setContentTopLevel(int contentTopLevel) {
    this.contentTopLevel = contentTopLevel;
  }

  public int getContentTopLevel() {
    return contentTopLevel;
  }

  public void setContentBottomLevel(int contentBottomLevel) {
    this.contentBottomLevel = contentBottomLevel;
  }

  public int getContentBottomLevel() {
    return contentBottomLevel;
  }

  public List<? extends BuildingBlock> getContentBbs() {
    return contentBbs;
  }

  public void setContentBbs(List<? extends BuildingBlock> contentBbs) {
    this.contentBbs = contentBbs;
  }

  public boolean isColumnAxisScalesWithContent() {
    return columnAxisScalesWithContent;
  }

  public void setColumnAxisScalesWithContent(boolean columnAxisScalesWithContent) {
    this.columnAxisScalesWithContent = columnAxisScalesWithContent;
  }

  public boolean isMergeContent() {
    return mergeContent;
  }

  public void setMergeContent(boolean mergeContent) {
    this.mergeContent = mergeContent;
  }

  public List<? extends BuildingBlock> getSideAxisBbs() {
    return sideAxisBbs;
  }

  public void setSideAxisBbs(List<? extends BuildingBlock> sideAxisBbs) {
    this.sideAxisBbs = sideAxisBbs;
  }

  public int getSideAxisBottomLevel() {
    return sideAxisBottomLevel;
  }

  public int getSideAxisTopLevel() {
    return sideAxisTopLevel;
  }

  public List<? extends BuildingBlock> getTopAxisBbs() {
    return topAxisBbs;
  }

  public void setTopAxisBbs(List<? extends BuildingBlock> topAxisBbs) {
    this.topAxisBbs = topAxisBbs;
  }

  public int getTopAxisBottomLevel() {
    return topAxisBottomLevel;
  }

  public int getTopAxisTopLevel() {
    return topAxisTopLevel;
  }

  /**
   * Sets the levels that are to be displayed for each side. If the elements on an axis are not
   * hierarchical, then set those levels to 1.
   * 
   * @param levels
   *          as in {@link LandscapeOptionsBean#levels}
   * @throws IteraplanTechnicalException
   */
  public void setAxisLevels(LandscapeElementLevels levels) throws IteraplanTechnicalException {
    if (levels != null) {
      final boolean topAxisLevelsError = levels.getTopAxisTopLevel() < 1 || levels.getTopAxisTopLevel() > levels.getTopAxisBottomLevel();
      final boolean sideAxisLevelsError = levels.getSideAxisTopLevel() < 1 || levels.getSideAxisTopLevel() > levels.getSideAxisBottomLevel();
      final boolean contentLevelsError = levels.getContentTopLevel() < 1 || levels.getContentTopLevel() > levels.getContentBottomLevel();
      if (topAxisLevelsError || sideAxisLevelsError || contentLevelsError) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }

      this.topAxisTopLevel = levels.getTopAxisTopLevel();
      this.topAxisBottomLevel = levels.getTopAxisBottomLevel();
      this.sideAxisTopLevel = levels.getSideAxisTopLevel();
      this.sideAxisBottomLevel = levels.getSideAxisBottomLevel();
      this.contentTopLevel = levels.getContentTopLevel();
      this.contentBottomLevel = levels.getContentBottomLevel();
    }
  }

  public Extension getColumnExtension() {
    return columnExtension;
  }

  public void setColumnExtension(Extension columnExtension) {
    this.columnExtension = columnExtension;
  }

  public Type<?> getContentType() {
    return contentType;
  }

  public void setContentType(Type<?> contentType) {
    this.contentType = contentType;
  }

  public Extension getRowExtension() {
    return rowExtension;
  }

  public void setRowExtension(Extension rowExtension) {
    this.rowExtension = rowExtension;
  }

  public Integer getColumnAttributeId() {
    return columnAttributeId;
  }

  public void setColumnAttributeId(Integer columnAttributeId) {
    this.columnAttributeId = columnAttributeId;
  }

  public Integer getRowAttributeId() {
    return rowAttributeId;
  }

  public void setRowAttributeId(Integer rowAttributeId) {
    this.rowAttributeId = rowAttributeId;
  }

  public boolean isHideChildrenElements() {
    return hideChildrenElements;
  }

  public void setHideChildrenElements(boolean hideChildrenElements) {
    this.hideChildrenElements = hideChildrenElements;
  }

  public boolean isScaleDownGraphicElements() {
    return scaleDownGraphicElements;
  }

  public void setScaleDownGraphicElements(boolean scaleDownGrahicElements) {
    this.scaleDownGraphicElements = scaleDownGrahicElements;
  }

  public boolean isFilterEmptyColumns() {
    return filterEmptyColumns;
  }

  public void setFilterEmptyColumns(boolean filterEmptyColumns) {
    this.filterEmptyColumns = filterEmptyColumns;
  }

  public boolean isFilterEmptyRows() {
    return filterEmptyRows;
  }

  public void setFilterEmptyRows(boolean filterEmptyRows) {
    this.filterEmptyRows = filterEmptyRows;
  }

  public void setStrictRelations(boolean strictRelations) {
    this.strictRelations = strictRelations;
  }

  public boolean isStrictRelations() {
    return strictRelations;
  }

  public void setShowUnspecifiedRelations(boolean showUnspecifiedRelations) {
    this.showUnspecifiedRelations = showUnspecifiedRelations;
  }

  public boolean isShowUnspecifiedRelations() {
    return showUnspecifiedRelations;
  }

  public boolean isUseNamesLegend() {
    return useNamesLegend;
  }

  public void setUseNamesLegend(boolean useNamesLegend) {
    this.useNamesLegend = useNamesLegend;
  }

  public boolean isSpanContentBetweenCells() {
    return spanContentBetweenCells;
  }

  public void setSpanContentBetweenCells(boolean spanContentBetweenCells) {
    this.spanContentBetweenCells = spanContentBetweenCells;
  }

  @Override
  public String toString() {
    ToStringBuilder.setDefaultStyle(ToStringStyle.MULTI_LINE_STYLE);
    return ToStringBuilder.reflectionToString(this);
  }
}

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableList;

import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.LandscapeDiagram;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.IPresentationExtension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.TechnicalComponentReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


public class LandscapeOptionsBean extends GraphicalExportBaseOptions implements ILandscapeOptions, Serializable {

  /** Serialization version. */
  private static final long                  serialVersionUID                  = -1023703960213981171L;

  public static final String                 CONTENT_QUERY                     = "contentQuery";
  public static final String                 ROW_QUERY                         = "rowQuery";
  public static final String                 COLUMN_QUERY                      = "columnQuery";

  /** {@link #getQueryResultNames()} */
  private static final ImmutableList<String> DIAGRAM_QUERIES                   = ImmutableList.of(CONTENT_QUERY, COLUMN_QUERY, ROW_QUERY);

  /**
   * split symbol for axis levels (between top level and bottom level)
   */
  private static final String                RANGE_SPLIT                       = "_";
  private static final int                   TOP_LEVEL                         = 0;
  private static final int                   BOTTOM_LEVEL                      = 1;

  public static final int                    ROW_COLUMN_OPTION_NONE            = 0;
  public static final int                    ROW_COLUMN_OPTION_RELATION        = 1;
  public static final int                    ROW_COLUMN_OPTION_ATTRIBUTE       = 2;

  private static final Set<String>           BM_ELEMENTS                       = CollectionUtils.hashSet(
      InformationSystemReleaseTypeQu.EXTENSION_BM_BUSINESSPROCESS,
      InformationSystemReleaseTypeQu.EXTENSION_BM_BUSINESSUNIT,
      InformationSystemReleaseTypeQu.EXTENSION_BM_PRODUCT);

  /** If true: one cell per axis element. Otherwise: one cell per content element. */
  private boolean                            scaleDownContentElements;

  /**
   * determines whether relations should be aggregated (false) across business mappings (within one building block)
   * and/or across building blocks (in case of reducing the visible hierarchical levels) or not (true).
   */
  private boolean                            strictRelations                   = true;

  /** show content elements with missing relations in extra row/column */
  private boolean                            showUnspecifiedRelations;

  private boolean                            globalScalingEnabled              = true;

  private boolean                            spanContentBetweenCells           = false;

  private List<BBAttribute>                  availableAttributes               = CollectionUtils.arrayList();

  /**
   * {@link LandscapeElementLevels}
   */
  private LandscapeElementLevels             levels                            = new LandscapeElementLevels();

  /**
   * Contains the user selection of the levels that are to be displayed in the content (only if
   * applicable). The format is for example: 1_3
   */
  private String                             selectedLevelRangeContent;

  /*
   * Fields pertaining to the column element selection:
   */

  private boolean                            filterEmptyColumns;

  /**
   * If set to 1, the user selected a relation for the columns. If set to 2, the user selected an
   * attribute for the columns.
   */
  private int                                selectedColumnOption;

  /** Holds the user selection for the column relation. This is also a resource key. */
  private String                             selectedColumnRelation;

  /**
   * Holds the id of the selected column attribute. Should only be filled when
   * selectedColumnRelation is null.
   */
  private Integer                            selectedColumnAttributeId;

  /** List of {@link String}s that represent the attribute values of the selected column attribute. */
  private List<String>                       attrValsOfSelectedColumnAttribute = CollectionUtils.arrayList();

  /**
   * If true, the column axis elements will scale with the contained elements. Otherwise the row
   * axis elements will do that.
   */
  private boolean                            columnAxisScalesWithContent;

  /**
   * Contains the user selection of the levels that are to be displayed in the column axis (only if
   * applicable). The format is for example: 1_3
   */
  private String                             selectedLevelRangeColumnAxis;

  /*
   * Fields pertaining to the row element selection:
   */

  /**
   * If set to 1, the user selected a relation for the rows. If set to 2, the user selected an
   * attribute for the rows.
   */
  private int                                selectedRowOption;

  /** Holds the user selection for the row relation. This is also a resource key. */
  private String                             selectedRowRelation;

  private boolean                            filterEmptyRows;

  /**
   * Holds the id of the selected row attribute. Should only be filled when selectedRowRelation is
   * null.
   */
  private Integer                            selectedRowAttributeId;

  /** List of {@link String}s that represent the attribute values of the selected row attribute. */
  private List<String>                       attrValsOfSelectedRowAttribute    = CollectionUtils.arrayList();

  /**
   * Contains the user selection of the levels that are to be displayed in the row axis (only if
   * applicable). The format is for example: 1_3
   */
  private String                             selectedLevelRangeRowAxis;

  /**
   * Constructor initializing {@link ColorDimensionOptionsBean} and {@link LineDimensionOptionsBean}
   * as well as {@link #scaleDownContentElements}
   */
  public LandscapeOptionsBean() {
    super();
    getColorOptionsBean().setAvailableColors(SpringGuiFactory.getInstance().getLandscapeColors());
    getLineOptionsBean().setAvailableLineStyles(SpringGuiFactory.getInstance().getAvailableLineTypes());
    this.scaleDownContentElements = LandscapeDiagram.isScaleDownLandscapeContentElements();
    setAvailableBbTypes(ImmutableList.of(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL));
    setSelectedBbType(null);
  }

  /**
   * Validates whether the user completed the necessary settings for creating a diagram
   */
  public void validate(ManageLandscapeDiagramMemoryBean memBean) {
    if (!isColumnSelectionOk(memBean.getQueryResult(LandscapeOptionsBean.COLUMN_QUERY))) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.LANDSCAPE_NO_ELEMENTS);
    }
    if (!isRowSelectionOk(memBean.getQueryResult(LandscapeOptionsBean.ROW_QUERY))) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.LANDSCAPE_NO_ELEMENTS);
    }
    if (!isContentSelectionOk(memBean.getQueryResult(LandscapeOptionsBean.CONTENT_QUERY))) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.LANDSCAPE_NO_ELEMENTS);
    }

  }

  /**
   * Checks if the column selection is valid and the user can proceed to the next step. At least one
   * column building block must be selected or one column attribute value must be selected.
   * 
   * @return true, if the column selection is ok.
   */
  private boolean isColumnSelectionOk(QueryResult columnQuery) {
    final boolean columnBbSelectionOk = selectedColumnOption == 1 && columnQuery != null && columnQuery.getSelectedResultIds().length > 0;
    final boolean columnAttributeselectionOk = selectedColumnOption == 2 && !attrValsOfSelectedColumnAttribute.isEmpty();
    return columnBbSelectionOk || columnAttributeselectionOk;
  }

  /**
   * Checks if the row selection is valid and the user can proceed to the next step. At least one
   * row building block must be selected or one row attribute value must be selected.
   * 
   * @return true, if the column selection is ok.
   */
  private boolean isRowSelectionOk(QueryResult rowQuery) {
    final boolean rowBbSelectionOk = selectedRowOption == 1 && rowQuery != null && rowQuery.getSelectedResultIds().length > 0;
    final boolean rowAttributeSelectionOk = selectedRowOption == 2 && !attrValsOfSelectedRowAttribute.isEmpty();
    return rowBbSelectionOk || rowAttributeSelectionOk;
  }

  /**
   * Checks if the content selection is valid and the user can proceed to the next step. At least
   * one content building block must be selected to return true.
   * 
   * @return true, if the content selection is ok.
   */
  private boolean isContentSelectionOk(QueryResult contentQuery) {
    return contentQuery != null && contentQuery.getSelectedResultIds().length > 0;
  }

  /**
   * Resets all fields that are associated with the configuration step.
   */
  public void resetConfiguration() {
    getColorOptionsBean().setDimensionAttributeId(Integer.valueOf(-1));
    getColorOptionsBean().resetValueToColorMap();
    getLineOptionsBean().setDimensionAttributeId(Integer.valueOf(-1));
    getLineOptionsBean().resetValueToLineTypeMap();
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

  public void setScaleDownContentElements(boolean scaleDownContentElements) {
    this.scaleDownContentElements = scaleDownContentElements;
  }

  public boolean isScaleDownContentElements() {
    return scaleDownContentElements;
  }

  /* (non-Javadoc)
   * @see de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ILandscapeOptions#isGlobalScalingEnabled()
   */
  public boolean isGlobalScalingEnabled() {
    return globalScalingEnabled;
  }

  public void setGlobalScalingEnabled(boolean globalScalingEnabled) {
    this.globalScalingEnabled = globalScalingEnabled;
  }

  public boolean isSpanContentBetweenCells() {
    return spanContentBetweenCells;
  }

  public void setSpanContentBetweenCells(boolean spanContentBetweenCells) {
    this.spanContentBetweenCells = spanContentBetweenCells;
  }

  @Override
  public List<String> getQueryResultNames() {
    return DIAGRAM_QUERIES;
  }

  public void setAvailableAttributes(List<BBAttribute> availableAttributes) {
    this.availableAttributes = availableAttributes;
  }

  public List<BBAttribute> getAvailableAttributes() {
    return availableAttributes;
  }

  /**
   * Retrieves the {@link Type} the user selected for the content.
   * 
   * @return The content {@link Type}. Can be either an {@link InformationSystemReleaseTypeQu} or a
   *         {@link TechnicalComponentReleaseTypeQu}.
   * @throws IteraplanTechnicalException
   *           If the selected content type is neither an Information System Release nor a Technical
   *           Component Release.
   */
  public Type<?> getCurrentContentType() {
    if (Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL.equals(getSelectedBbType())) {
      return InformationSystemReleaseTypeQu.getInstance();
    }
    else if (Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL.equals(getSelectedBbType())) {
      return TechnicalComponentReleaseTypeQu.getInstance();
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  public boolean isBusinessMappingsBasedDiagram() {
    return BM_ELEMENTS.contains(selectedColumnRelation) && BM_ELEMENTS.contains(selectedRowRelation);
  }

  /**
   * @return Returns a list of {@link Extension}s that are configured for the current content type
   *         of the diagram. The list represents the valid relations of the respective type.
   */
  public List<Extension> getAvailableRelations() {
    final Type<?> currentContentType = getCurrentContentType();
    if (currentContentType == null) {
      return new ArrayList<Extension>();
    }

    final Collection<Extension> contentTypeRelations = currentContentType.getRelations().values();
    final List<Extension> availableRelations = new ArrayList<Extension>(contentTypeRelations);
    Collections.sort(availableRelations, new IPresentationExtension.PresentationKeyComparator());

    return availableRelations;

  }

  public LandscapeElementLevels getLevels() {
    if (levels == null) {
      levels = new LandscapeElementLevels();
    }
    return levels;
  }

  public void setLevels(LandscapeElementLevels levels) {
    this.levels = new LandscapeElementLevels(levels);
  }

  public int getSelectedTopLevelForContent() {
    return getSelectedLevel(this.selectedLevelRangeContent, TOP_LEVEL);
  }

  public int getSelectedBottomLevelForContent() {
    return getSelectedLevel(this.selectedLevelRangeContent, BOTTOM_LEVEL);
  }

  public void setSelectedLevelRangeContent(String selectedLevelRangeContent) {
    this.selectedLevelRangeContent = selectedLevelRangeContent;
  }

  public String getSelectedLevelRangeContent() {
    return selectedLevelRangeContent;
  }

  /**
   * @param axis
   *          either row (side) or column (top) axis
   * @param whichLevel
   *          either TOP_LEVEL or BOTTOM_LEVEL
   * @return the respective level value
   */
  private int getSelectedLevel(String axis, int whichLevel) {
    if (StringUtils.isEmpty(axis)) {
      return 1;
    }
    final String[] split = axis.split(RANGE_SPLIT);
    return Integer.parseInt(split[whichLevel]);
  }

  public boolean isFilterEmptyColumns() {
    return filterEmptyColumns;
  }

  public void setFilterEmptyColumns(boolean filterEmptyColumns) {
    this.filterEmptyColumns = filterEmptyColumns;
  }

  /**
   * @return The column option. The coding is following: 0 stands for none, 1 for relation, and 2
   *         for attribute.
   */
  public int getSelectedColumnOption() {
    return selectedColumnOption;
  }

  /**
   * @param selectedColumnOption
   *          The column option. The value 0 is ignored. Values 1 and 2 are valid.
   * @throws IllegalArgumentException
   *           When the argument is not 0, 1 or 2.
   */
  public void setSelectedColumnOption(int selectedColumnOption) {
    if (selectedColumnOption == LandscapeOptionsBean.ROW_COLUMN_OPTION_NONE) {
      return;
    }
    if (selectedColumnOption == LandscapeOptionsBean.ROW_COLUMN_OPTION_ATTRIBUTE
        || selectedColumnOption == LandscapeOptionsBean.ROW_COLUMN_OPTION_RELATION) {
      this.selectedColumnOption = selectedColumnOption;
    }
    else {
      throw new IllegalArgumentException();
    }
  }

  public void resetColumnOption() {
    this.selectedColumnOption = 0; // setter does not allow 0
  }

  public String getSelectedColumnRelation() {
    return selectedColumnRelation;
  }

  public void setSelectedColumnRelation(String selectedColumnRelation) {
    this.selectedColumnRelation = selectedColumnRelation;
  }

  public Integer getSelectedColumnAttributeId() {
    return selectedColumnAttributeId;
  }

  public void setSelectedColumnAttributeId(Integer selectedColumnAttributeId) {
    this.selectedColumnAttributeId = selectedColumnAttributeId;
  }

  public List<String> getAttrValsOfSelectedColumnAttribute() {
    return attrValsOfSelectedColumnAttribute;
  }

  /**
   * @param attrValsOfSelectedColumnAttribute
   *          The list to set. If null, an empty list is set.
   */
  public void setAttrValsOfSelectedColumnAttribute(List<String> attrValsOfSelectedColumnAttribute) {
    if (attrValsOfSelectedColumnAttribute == null) {
      this.attrValsOfSelectedColumnAttribute = new ArrayList<String>();
    }
    else {
      this.attrValsOfSelectedColumnAttribute = attrValsOfSelectedColumnAttribute;
    }
  }

  public Extension getCurrentColumnRelation() {
    return getCurrentContentType().getRelations().get(selectedColumnRelation);
  }

  public boolean isColumnAxisScalesWithContent() {
    return columnAxisScalesWithContent;
  }

  public void setColumnAxisScalesWithContent(boolean columnAxisScalesWithContent) {
    this.columnAxisScalesWithContent = columnAxisScalesWithContent;
  }

  public String getSelectedLevelRangeColumnAxis() {
    return selectedLevelRangeColumnAxis;
  }

  public void setSelectedLevelRangeColumnAxis(String selectedLevelRangeColumnAxis) {
    this.selectedLevelRangeColumnAxis = selectedLevelRangeColumnAxis;
  }

  public int getSelectedTopLevelForTopAxis() {
    return getSelectedLevel(this.selectedLevelRangeColumnAxis, TOP_LEVEL);
  }

  public int getSelectedBottomLevelForTopAxis() {
    return getSelectedLevel(this.selectedLevelRangeColumnAxis, BOTTOM_LEVEL);
  }

  public int getSelectedRowOption() {
    return selectedRowOption;
  }

  /**
   * @param selectedRowOption
   *          The row option. The value 0 is ignored. Values 1 and 2 are set.
   * @throws IllegalArgumentException
   *           When the argument is not 0, 1 or 2.
   */
  public void setSelectedRowOption(int selectedRowOption) {
    if (selectedRowOption == LandscapeOptionsBean.ROW_COLUMN_OPTION_NONE) {
      return;
    }
    if (selectedRowOption == LandscapeOptionsBean.ROW_COLUMN_OPTION_ATTRIBUTE || selectedRowOption == LandscapeOptionsBean.ROW_COLUMN_OPTION_RELATION) {
      this.selectedRowOption = selectedRowOption;
    }
    else {
      throw new IllegalArgumentException();
    }
  }

  public void resetRowOption() {
    this.selectedRowOption = 0; // setter does not allow 0
  }

  public String getSelectedRowRelation() {
    return selectedRowRelation;
  }

  public void setSelectedRowRelation(String selectedRowRelation) {
    this.selectedRowRelation = selectedRowRelation;
  }

  public Extension getCurrentRowRelation() {
    return getCurrentContentType().getRelations().get(selectedRowRelation);
  }

  public boolean isFilterEmptyRows() {
    return filterEmptyRows;
  }

  public void setFilterEmptyRows(boolean filterEmptyRows) {
    this.filterEmptyRows = filterEmptyRows;
  }

  public Integer getSelectedRowAttributeId() {
    return selectedRowAttributeId;
  }

  public void setSelectedRowAttributeId(Integer selectedRowAttributeId) {
    this.selectedRowAttributeId = selectedRowAttributeId;
  }

  public List<String> getAttrValsOfSelectedRowAttribute() {
    return attrValsOfSelectedRowAttribute;
  }

  /**
   * @param attrValsOfSelectedRowAttribute
   *          The list to set. If null, an empty list is set.
   */
  public void setAttrValsOfSelectedRowAttribute(List<String> attrValsOfSelectedRowAttribute) {
    if (attrValsOfSelectedRowAttribute == null) {
      this.attrValsOfSelectedRowAttribute = new ArrayList<String>();
    }
    else {
      this.attrValsOfSelectedRowAttribute = attrValsOfSelectedRowAttribute;
    }
  }

  public String getSelectedLevelRangeRowAxis() {
    return selectedLevelRangeRowAxis;
  }

  public void setSelectedLevelRangeRowAxis(String selectedLevelRangeRowAxis) {
    this.selectedLevelRangeRowAxis = selectedLevelRangeRowAxis;
  }

  public int getSelectedTopLevelForSideAxis() {
    return getSelectedLevel(this.selectedLevelRangeRowAxis, TOP_LEVEL);
  }

  public int getSelectedBottomLevelForSideAxis() {
    return getSelectedLevel(this.selectedLevelRangeRowAxis, BOTTOM_LEVEL);
  }

}

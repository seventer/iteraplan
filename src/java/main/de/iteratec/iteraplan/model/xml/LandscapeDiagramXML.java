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
package de.iteratec.iteraplan.model.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.LandscapeDiagram;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.LandscapeElementLevels;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.LandscapeOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ManageLandscapeDiagramMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.TechnicalComponentReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.xml.query.EnumAttributeXML;
import de.iteratec.iteraplan.model.xml.query.QueryResultXML;


/**
 * The Java to XML DTO for marshalling the memBean of landscape diagrams.
 */
@XmlRootElement
@XmlType(name = "landscapeDiagramXML", propOrder = { "color", "columnAxisScalesWithContent", "columnQuery", "contentQuery", "filterEmptyColumns",
    "filterEmptyRows", "lineType", "rowQuery", "selectedColumnAttributeId", "selectedColumnOption", "selectedColumnRelation",
    "selectedRowAttributeId", "selectedGraphicsFormat", "selectedResultIds", "selectedRowOption", "selectedRowRelation", "strictRelations",
    "showUnspecifiedRelations", "scaleDownContentElements", "globalScalingEnabled", "spanContentBetweenCells", "useColorRange", "useNamesLegend",
    "showSavedQueryInfo", "sideAxisBottomLevel", "sideAxisTopLevel", "topAxisBottomLevel", "topAxisTopLevel", "contentBottomLevel",
    "contentTopLevel", "version" })
public class LandscapeDiagramXML implements SerializedQuery<ManageLandscapeDiagramMemoryBean> {
  private static final Logger LOGGER = Logger.getIteraplanLogger(LandscapeDiagramXML.class);

  /**
   * Enumeration for the content type of landscape diagrams.
   */
  @XmlEnum(String.class)
  public enum LandscapeDiagramContentTypeXML {

    INFORMATIONSYSTEMRELEASE, TECHNICALCOMPONENTRELEASE;

    public Type<?> getQueryType() {
      if (INFORMATIONSYSTEMRELEASE.equals(this)) {
        return InformationSystemReleaseTypeQu.getInstance();
      }
      else if (TECHNICALCOMPONENTRELEASE.equals(this)) {
        return TechnicalComponentReleaseTypeQu.getInstance();
      }
      throw new IteraplanTechnicalException();
    }
  }

  /**
   * Needed to determine whether an attribute or buildingblocks should be displayed in columns or
   * rows
   * 
   * @author Gunnar Giesinger, iteratec GmbH, 2007
   */
  @XmlEnum(String.class)
  public enum ContentOption {
    BUILDING_BLOCK, ATTRIBUTE;

    public int getContentOptionAsInt() {
      if (BUILDING_BLOCK.equals(this)) {
        return 1;
      }
      else if (ATTRIBUTE.equals(this)) {
        return 2;
      }
      throw new IteraplanTechnicalException();
    }

    public static ContentOption getContentOption(int id) {
      if (id == 1) {
        return BUILDING_BLOCK;
      }
      else if (id == 2) {
        return ATTRIBUTE;
      }
      throw new IteraplanTechnicalException();
    }
  }

  /** {@link #getDialogStep()} */
  private int                            dialogStep;
  /** {@link #getContentType()} */
  private LandscapeDiagramContentTypeXML contentType;
  private boolean                        filterEmptyColumns;
  private boolean                        filterEmptyRows;
  private String                         selectedGraphicsFormat;
  /** {@link #getContentQuery()} */
  private QueryResultXML                 contentQuery              = new QueryResultXML();
  /** {@link #getSelectedColumnOption()} */
  private ContentOption                  selectedColumnOption      = ContentOption.BUILDING_BLOCK;
  /** {@link #getSelectedColumnRelation()} */
  private String                         selectedColumnRelation;
  /** {@link #getColumnQuery()} */
  private QueryResultXML                 columnQuery               = new QueryResultXML();
  /** {@link #getSelectedRowOption()} */
  private ContentOption                  selectedRowOption         = ContentOption.BUILDING_BLOCK;
  /** {@link #getSelectedRowRelation()} */
  private String                         selectedRowRelation;
  /** {@link #isStrictRelations()} */
  private boolean                        strictRelations           = true;
  /** {@link #isShowUnspecifiedRelations()} */
  private boolean                        showUnspecifiedRelations;
  /** {@link #isScaleDownContentElements()} */
  private boolean                        scaleDownContentElements  = LandscapeDiagram.isScaleDownLandscapeContentElements();
  private boolean                        globalScalingEnabled      = true;
  private boolean                        spanContentBetweenCells   = false;
  /** {@link #isUseNameLegend()} */
  private boolean                        useNamesLegend            = true;
  private boolean                        showSavedQueryInfo;
  /** {@link #getRowQuery()} */
  private QueryResultXML                 rowQuery                  = new QueryResultXML();
  /** {@link #getTopAxisTopLevel()} */
  private int                            topAxisTopLevel           = 1;
  /** {@link #getTopAxisBottomLevel()} */
  private int                            topAxisBottomLevel        = 3;
  /** {@link #getSideAxisTopLevel()} */
  private int                            sideAxisTopLevel          = 1;
  /** {@link #getSideAxisBottomLevel()} */
  private int                            sideAxisBottomLevel       = 3;
  /** {@link #getContentTopLevel()} */
  private int                            contentTopLevel           = 1;
  /** {@link #getContentBottomLevel()} */
  private int                            contentBottomLevel        = 3;
  /** {@link #getColor()} */
  private EnumAttributeXML               color;
  /** {@link #getLineType()} */
  private EnumAttributeXML               lineType;
  /** {@link #isColumnAxisScalesWithContent()} */
  private boolean                        columnAxisScalesWithContent;

  private boolean                        reportUpdated;
  /** {@link #getSelectedResultIds()} */
  private List<Integer>                  selectedResultIds         = new ArrayList<Integer>();

  /** TRANSIENT attributes (not marshalled) * */

  /** {@link #getSelectedColumnAttributeId()} */
  private Integer                        selectedColumnAttributeId = Integer.valueOf(-1);
  /** {@link #getSelectedRowAttributeId()} */
  private Integer                        selectedRowAttributeId    = Integer.valueOf(-1);

  /** {@link #isUseColorRange()} */
  private boolean                        useColorRange             = false;

  /**
   * The type of building block of the content
   * 
   * @return The buildingblock type of the content
   */
  @XmlAttribute(name = "contentType", required = true)
  public LandscapeDiagramContentTypeXML getContentType() {
    return contentType;
  }

  @XmlElement
  public boolean isFilterEmptyColumns() {
    return filterEmptyColumns;
  }

  @XmlElement
  public boolean isFilterEmptyRows() {
    return filterEmptyRows;
  }

  /**
   * Specifies from which level on the hierarchy on the top axis is displayed. All elements in
   * topAxisBbs, which have a level lower than topAxisTopLevel will be ignored.
   * 
   * @return The topAxisTopLevel
   */
  @XmlElement
  public int getTopAxisTopLevel() {
    return topAxisTopLevel;
  }

  /**
   * Specifies until which level the hierarchy on the top axis is displayed. All elements in
   * topAxisBbs, which have a level higher than topAxisBottomLevel will not be shown. Their
   * associated content elements however will be merged with their parent.
   * 
   * @return The topAxisBottomLevel
   */
  @XmlElement
  public int getTopAxisBottomLevel() {
    return topAxisBottomLevel;
  }

  /**
   * Same as {@link #getTopAxisTopLevel()} but for the for the side axis.
   * 
   * @return The sideAxisTopLevel
   */
  @XmlElement
  public int getSideAxisTopLevel() {
    return sideAxisTopLevel;
  }

  /**
   * Same as {@link #getTopAxisBottomLevel()} but for the for the side axis.
   * 
   * @return The sideAxisBottomLevel
   */
  @XmlElement
  public int getSideAxisBottomLevel() {
    return sideAxisBottomLevel;
  }

  /**
   * Same as {@link #getTopAxisTopLevel()} but for the for the content.
   * 
   * @return The contentTopLevel
   */
  @XmlElement
  public int getContentTopLevel() {
    return contentTopLevel;
  }

  /**
   * Same as {@link #getTopAxisBottomLevel()} but for the for the content.
   * 
   * @return The contentBottomLevel
   */
  @XmlElement
  public int getContentBottomLevel() {
    return contentBottomLevel;
  }

  /**
   * The step in the configuration dialog of a grafical query that is reflected by this DTO.
   * 
   * @return The step in the dialog
   */
  @XmlAttribute(required = true)
  public int getDialogStep() {
    return dialogStep;
  }

  /**
   * Holds the query for the content
   * 
   * @return The content query
   */
  @XmlElement
  public QueryResultXML getContentQuery() {
    return contentQuery;
  }

  /**
   * Display BuildingBlocks or Attributes in columns?
   * 
   * @return {@link ContentOption#BUILDING_BLOCK} or {@link ContentOption#ATTRIBUTE}
   */
  @XmlElement(required = true)
  public ContentOption getSelectedColumnOption() {
    return selectedColumnOption;
  }

  /**
   * Holds the user selection for the column relation. This is also a ressource key.
   * 
   * @return The user selection for the column relation
   */
  @XmlElement
  public String getSelectedColumnRelation() {
    return selectedColumnRelation;
  }

  /**
   * The content of the column query (if {@link #getSelectedColumnOption()} is set to
   * {@link ContentOption#BUILDING_BLOCK})
   * 
   * @return The content of the column query
   */
  @XmlElement
  public QueryResultXML getColumnQuery() {
    return columnQuery;
  }

  /**
   * Display BuildingBlocks or Attributes in rows?
   * 
   * @return {@link ContentOption#BUILDING_BLOCK} or {@link ContentOption#ATTRIBUTE}
   */
  @XmlElement(required = true)
  public ContentOption getSelectedRowOption() {
    return selectedRowOption;
  }

  /**
   * Holds the user selection for the row relation. This is also a ressource key.
   * 
   * @return The user selection for the row relation
   */
  @XmlElement
  public String getSelectedRowRelation() {
    return selectedRowRelation;
  }

  @XmlElement
  public boolean isShowUnspecifiedRelations() {
    return showUnspecifiedRelations;
  }

  @XmlElement
  public boolean isScaleDownContentElements() {
    return scaleDownContentElements;
  }

  @XmlElement
  public boolean isGlobalScalingEnabled() {
    return globalScalingEnabled;
  }

  @XmlElement
  public boolean isSpanContentBetweenCells() {
    return spanContentBetweenCells;
  }

  @XmlElement
  public boolean isUseNamesLegend() {
    return useNamesLegend;
  }

  public void setShowSavedQueryInfo(boolean showSavedQueryInfo) {
    this.showSavedQueryInfo = showSavedQueryInfo;
  }

  @XmlElement
  public boolean isShowSavedQueryInfo() {
    return showSavedQueryInfo;
  }

  @XmlElement
  public boolean isStrictRelations() {
    return strictRelations;
  }

  /**
   * The content of the row query (if {@link #getSelectedRowOption()} is set to
   * {@link ContentOption#BUILDING_BLOCK})
   * 
   * @return The content of the row query
   */
  @XmlElement
  public QueryResultXML getRowQuery() {
    return rowQuery;
  }

  @XmlElement
  public String getVersion() {
    return IteraplanProperties.getProperties().getBuildVersion();
  }

  /**
   * Returns the configuration of the color attribute (if set)
   * 
   * @return The color attribute
   */
  @XmlElement
  public EnumAttributeXML getColor() {
    return color;
  }

  /**
   * Returns the configuration of the linetype attribute (if set)
   * 
   * @return The linetype attribute
   */
  @XmlElement
  public EnumAttributeXML getLineType() {
    return lineType;
  }

  /**
   * If true, the column axis elements will scale with the contained elements. Otherwise the row
   * axis elements will do that.
   * 
   * @return <code>true</code> if the column scales
   */
  @XmlElement(required = true)
  public boolean isColumnAxisScalesWithContent() {
    return columnAxisScalesWithContent;
  }

  public void setLineType(EnumAttributeXML lineType) {
    this.lineType = lineType;
  }

  @XmlElement
  public String getSelectedGraphicsFormat() {
    return selectedGraphicsFormat;
  }

  @XmlElement
  public Integer getSelectedColumnAttributeId() {
    return selectedColumnAttributeId;
  }

  @XmlElement
  public Integer getSelectedRowAttributeId() {
    return selectedRowAttributeId;
  }

  /**
   * The BBs of the query result that have to be selected according to the saved query. Note: only
   * buildingBlocks that still exist will be selected!
   * <p>
   * <b>IMPORTANT</b>: Selected result ids are currently not persisted. Hence when running a saved
   * query always all building blocks matching the query criteria are selected. This feature will be
   * implemented in 2.x
   * 
   * @return The selected BBs
   */
  @XmlElementWrapper(name = "selectedResultIds")
  @XmlElement(name = "id")
  public List<Integer> getSelectedResultIds() {
    // TODO 2.x: implement to retrieve selected results from an XML file
    return selectedResultIds;
  }

  public void setFilterEmptyColumns(boolean filterEmptyColumns) {
    this.filterEmptyColumns = filterEmptyColumns;
  }

  public void setFilterEmptyRows(boolean filterEmptyRows) {
    this.filterEmptyRows = filterEmptyRows;
  }

  public void setSelectedGraphicsFormat(String selectedGraphicsFormat) {
    this.selectedGraphicsFormat = selectedGraphicsFormat;
  }

  public void setSelectedColumnAttributeId(Integer selectedColumnAttributeId) {
    this.selectedColumnAttributeId = selectedColumnAttributeId;
  }

  public void setSelectedRowAttributeId(Integer selectedRowAttributeId) {
    this.selectedRowAttributeId = selectedRowAttributeId;
  }

  public void setDialogStep(int step) {
    this.dialogStep = step;
  }

  public void setContentType(LandscapeDiagramContentTypeXML contentType) {
    this.contentType = contentType;
  }

  public void setContentQuery(QueryResultXML contentQuery) {
    this.contentQuery = contentQuery;
  }

  public void setSelectedColumnOption(ContentOption selectedColumnOption) {
    this.selectedColumnOption = selectedColumnOption;
  }

  public void setSelectedColumnRelation(String selectedColumnRelation) {
    this.selectedColumnRelation = selectedColumnRelation;
  }

  public void setColumnQuery(QueryResultXML columnQuery) {
    this.columnQuery = columnQuery;
  }

  public void setSelectedRowOption(ContentOption selectedRowOption) {
    this.selectedRowOption = selectedRowOption;
  }

  public void setSelectedRowRelation(String selectedRowRelation) {
    this.selectedRowRelation = selectedRowRelation;
  }

  public void setStrictRelations(boolean strictRelations) {
    this.strictRelations = strictRelations;
  }

  public void setShowUnspecifiedRelations(boolean showUnspecifiedRelations) {
    this.showUnspecifiedRelations = showUnspecifiedRelations;
  }

  public void setScaleDownContentElements(boolean scaleDownContentElements) {
    this.scaleDownContentElements = scaleDownContentElements;
  }

  public void setGlobalScalingEnabled(boolean globalScalingEnabled) {
    this.globalScalingEnabled = globalScalingEnabled;
  }

  public void setSpanContentBetweenCells(boolean spanContentBetweenCells) {
    this.spanContentBetweenCells = spanContentBetweenCells;
  }

  public void setUseNamesLegend(boolean useNamesLegend) {
    this.useNamesLegend = useNamesLegend;
  }

  public void setColor(EnumAttributeXML color) {
    this.color = color;
  }

  public void setRowQuery(QueryResultXML rowQuery) {
    this.rowQuery = rowQuery;
  }

  public void setTopAxisTopLevel(int topAxisTopLevel) {
    this.topAxisTopLevel = topAxisTopLevel;
  }

  public void setTopAxisBottomLevel(int topAxisBottomLevel) {
    this.topAxisBottomLevel = topAxisBottomLevel;
  }

  public void setSideAxisTopLevel(int sideAxisTopLevel) {
    this.sideAxisTopLevel = sideAxisTopLevel;
  }

  public void setSideAxisBottomLevel(int sideAxisBottomLevel) {
    this.sideAxisBottomLevel = sideAxisBottomLevel;
  }

  public void setContentTopLevel(int contentTopLevel) {
    this.contentTopLevel = contentTopLevel;
  }

  public void setContentBottomLevel(int contentBottomLevel) {
    this.contentBottomLevel = contentBottomLevel;
  }

  public void setColumnAxisScalesWithContent(boolean columnAxisScalesWithContent) {
    this.columnAxisScalesWithContent = columnAxisScalesWithContent;
  }

  public void setSelectedResultIds(List<Integer> selectedResultIds) {
    this.selectedResultIds = selectedResultIds;
  }

  @XmlTransient
  public boolean isReportUpdated() {
    return reportUpdated;
  }

  public void setReportUpdated(boolean reportUpdated) {
    this.reportUpdated = reportUpdated;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.SerializedQuery#validate(java.util.Locale)
   */
  public void validate(Locale locale) {
    // the contentType does not have to be validated as enums are validated against the schema
    if (contentQuery != null) {
      contentQuery.validate(locale);
    }
    if (ContentOption.BUILDING_BLOCK.equals(selectedColumnOption)
        && (columnQuery == null || columnQuery.getQueryForms() == null || columnQuery.getQueryForms().isEmpty())) {
      logError("Content of column is set to BUILDINGBLOCK but no query configuration could be found");
    }
    else {
      columnQuery.validate(locale);
    }

    if (ContentOption.BUILDING_BLOCK.equals(selectedRowOption)
        && (rowQuery == null || rowQuery.getQueryForms() == null || rowQuery.getQueryForms().isEmpty())) {
      logError("Content of row is set to BUILDINGBLOCK but no query configuration could be found");
    }
    else {
      rowQuery.validate(locale);
    }

    String[] typesEnumResp = new String[] { BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, BBAttribute.USERDEF_RESPONSIBILITY_ATTRIBUTE_TYPE,
        BBAttribute.BLANK_ATTRIBUTE_TYPE, BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE, BBAttribute.FIXED_ATTRIBUTE_TYPE };

    if (color == null || color.getAttributeId() == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.ATTRIBUTE_NOT_FOUND);
    }
    if (lineType != null && StringUtils.isNotEmpty(lineType.getAttributeName())) {
      validateAttributeType(lineType.getAttributeName(), typesEnumResp);
    }

    // validate the relations
    Type<?> currentContentType = contentType.getQueryType();
    if (currentContentType != null) {
      List<Extension> availableRelations = new ArrayList<Extension>(currentContentType.getRelations().values());
      if (StringUtils.isNotEmpty(selectedColumnRelation)) {
        validateRelation(selectedColumnRelation, availableRelations);
      }
      if (StringUtils.isNotEmpty(selectedRowRelation)) {
        validateRelation(selectedRowRelation, availableRelations);
      }
    }
    if (selectedColumnAttributeId == null) {
      selectedColumnAttributeId = Integer.valueOf(-1);
    }
    if (selectedRowAttributeId == null) {
      selectedRowAttributeId = Integer.valueOf(-1);
    }
  }

  /**
   * Checks if a given relation is part of a given sets of validations derived from a specific
   * buildingblock type
   * 
   * @param relation
   *          The relation
   * @param availableRelations
   *          All relations of a buildingblock type
   */
  private void validateRelation(String relation, List<Extension> availableRelations) {
    for (Extension ex : availableRelations) {
      if (ex.getName().equals(relation)) {
        return;
      }
    }
    StringBuilder buffer = new StringBuilder().append("Invalid relation: '").append(relation).append("'. Relation has to be in: ( ");
    for (Extension ex : availableRelations) {
      buffer.append(ex.getName()).append(' ');
    }
    buffer.append(')');
    logError(buffer.toString());
  }

  /**
   * Checks if the type of a given attribute string id is part of given types
   * 
   * @param attributeName
   *          The attribute string id (e.g. userdefEnum_null_Complexity), see
   *          {@link BBAttribute#getStringIdName()}
   * @param types
   *          The allowed types (e.g. {@link BBAttribute#USERDEF_ENUM_ATTRIBUTE_TYPE}, ...)
   */
  private void validateAttributeType(String attributeName, String[] types) {

    try {
      if (!BBAttribute.isTypeIn(attributeName, types)) {
        StringBuilder buffer = new StringBuilder().append("Invalid attribute type: '").append(attributeName).append("'. Type has to be in: ( ");
        for (String type : types) {
          buffer.append(type).append(' ');
        }
        buffer.append(')');
        logError(buffer.toString());
      }
    } catch (RuntimeException e) {
      // thrown when the attributeName does not have the correct form
      logError("'" + attributeName + "' is not a valid attribute name", e);
    }
  }

  public void initFrom(ManageLandscapeDiagramMemoryBean memBean, Locale locale) {
    // called before saving an XML file
    final LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    this.dialogStep = landscapeOptions.getDialogStep();
    Type<?> resultType = memBean.getQueryResult(LandscapeOptionsBean.CONTENT_QUERY).getResultType();
    if (resultType instanceof InformationSystemReleaseTypeQu) {
      this.contentType = LandscapeDiagramContentTypeXML.INFORMATIONSYSTEMRELEASE;
    }
    else if (resultType instanceof TechnicalComponentReleaseTypeQu) {
      this.contentType = LandscapeDiagramContentTypeXML.TECHNICALCOMPONENTRELEASE;
    }
    else {
      throw new IteraplanTechnicalException();
    }

    initContent(memBean, locale);
    initColumn(memBean, locale);
    initRow(memBean, locale);

    this.strictRelations = landscapeOptions.isStrictRelations();
    this.showUnspecifiedRelations = landscapeOptions.isShowUnspecifiedRelations();
    this.scaleDownContentElements = landscapeOptions.isScaleDownContentElements();
    this.globalScalingEnabled = landscapeOptions.isGlobalScalingEnabled();
    this.spanContentBetweenCells = landscapeOptions.isSpanContentBetweenCells();
    this.useNamesLegend = landscapeOptions.isUseNamesLegend();
    this.showSavedQueryInfo = landscapeOptions.isShowSavedQueryInfo();

    initLevels(landscapeOptions);

    initColorOptions(memBean, landscapeOptions);
    initLineOptions(memBean, landscapeOptions);

    this.columnAxisScalesWithContent = landscapeOptions.isColumnAxisScalesWithContent();

    this.selectedGraphicsFormat = landscapeOptions.getSelectedGraphicFormat();
    this.filterEmptyColumns = landscapeOptions.isFilterEmptyColumns();
    this.filterEmptyRows = landscapeOptions.isFilterEmptyRows();
  }

  private void initLineOptions(ManageLandscapeDiagramMemoryBean memBean, final LandscapeOptionsBean landscapeOptions) {
    // set the line type attribute
    final LineDimensionOptionsBean lineOptions = landscapeOptions.getLineOptionsBean();
    if (lineOptions.getDimensionAttributeId() != null) {
      for (BBAttribute attr : memBean.getQueryResult(LandscapeOptionsBean.CONTENT_QUERY).getQueryForms().get(0).getDimensionAttributes()) {
        if (attr.getId().equals(lineOptions.getDimensionAttributeId())) {
          lineType = new EnumAttributeXML();
          lineType.initFrom(attr, lineOptions.getAttributeValues(), lineOptions.getSelectedLineTypes());
          break;
        }
      }
    }
  }

  private void initColorOptions(ManageLandscapeDiagramMemoryBean memBean, final LandscapeOptionsBean landscapeOptions) {
    // set the color attribute
    final ColorDimensionOptionsBean colorOptions = landscapeOptions.getColorOptionsBean();
    if (colorOptions.getDimensionAttributeId() != null) {
      for (BBAttribute attr : memBean.getQueryResult(LandscapeOptionsBean.CONTENT_QUERY).getQueryForms().get(0).getDimensionAttributes()) {
        if (attr.getId().equals(colorOptions.getDimensionAttributeId())) {
          color = new EnumAttributeXML();
          color.initFrom(attr, colorOptions.getAttributeValues(), colorOptions.getSelectedColors());
          useColorRange = colorOptions.isUseColorRange();
          break;
        }
      }
    }
  }

  private void initLevels(LandscapeOptionsBean landscapeOptions) {
    if (landscapeOptions.getLevels() != null) {
      topAxisTopLevel = landscapeOptions.getSelectedTopLevelForTopAxis();
      topAxisBottomLevel = landscapeOptions.getSelectedBottomLevelForTopAxis();
      sideAxisTopLevel = landscapeOptions.getSelectedTopLevelForSideAxis();
      sideAxisBottomLevel = landscapeOptions.getSelectedBottomLevelForSideAxis();
      contentTopLevel = landscapeOptions.getSelectedTopLevelForContent();
      contentBottomLevel = landscapeOptions.getSelectedBottomLevelForContent();
    }
  }

  private void initRow(ManageLandscapeDiagramMemoryBean memBean, Locale locale) {
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    // init the row part
    this.selectedRowOption = ContentOption.getContentOption(landscapeOptions.getSelectedRowOption());
    QueryResult rowQueryResult = memBean.getQueryResult(LandscapeOptionsBean.ROW_QUERY);
    if (rowQueryResult != null) {
      rowQuery.initFrom(rowQueryResult, locale);
      this.selectedRowRelation = landscapeOptions.getSelectedRowRelation();
    }
    if (landscapeOptions.getSelectedRowAttributeId() != null) {
      this.selectedRowAttributeId = landscapeOptions.getSelectedRowAttributeId();
    }
  }

  private void initColumn(ManageLandscapeDiagramMemoryBean memBean, Locale locale) {
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();
    // init the column part
    this.selectedColumnOption = ContentOption.getContentOption(landscapeOptions.getSelectedColumnOption());
    QueryResult columnQueryResult = memBean.getQueryResult(LandscapeOptionsBean.COLUMN_QUERY);
    if (columnQueryResult != null) {
      columnQuery.initFrom(columnQueryResult, locale);
      this.selectedColumnRelation = landscapeOptions.getSelectedColumnRelation();
    }
    if (landscapeOptions.getSelectedColumnAttributeId() != null) {
      this.selectedColumnAttributeId = landscapeOptions.getSelectedColumnAttributeId();
    }
  }

  private void initContent(ManageLandscapeDiagramMemoryBean memBean, Locale locale) {
    // init the content part
    QueryResult contentQueryResult = memBean.getQueryResult(LandscapeOptionsBean.CONTENT_QUERY);
    if (contentQueryResult != null) {
      contentQuery.initFrom(contentQueryResult, locale);
      if (contentQueryResult.getSelectedResults().size() == contentQueryResult.getResults().size()) {
        contentQuery.setSelectedResultIds(null);
      }
    }

  }

  /**
   * Updates basic data of the memBean. Attributes, ... have to be updated after the content query
   * is set! See {@link #updateAttributes(ManageLandscapeDiagramMemoryBean, Locale)}
   * 
   * @param memBean
   *          The memBean
   * @param locale
   *          The current user's locale
   */
  public void update(ManageLandscapeDiagramMemoryBean memBean, Locale locale) {
    final LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();
    landscapeOptions.setDialogStep(dialogStep);

    if (LandscapeDiagramContentTypeXML.INFORMATIONSYSTEMRELEASE.equals(contentType)) {
      landscapeOptions.setSelectedBbType(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL);
    }
    else {
      landscapeOptions.setSelectedBbType(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL);
    }

    if (ContentOption.BUILDING_BLOCK.equals(selectedColumnOption)) {
      landscapeOptions.setSelectedColumnOption(1);
    }
    else if (ContentOption.ATTRIBUTE.equals(selectedColumnOption)) {
      landscapeOptions.setSelectedColumnOption(2);
    }

    if (ContentOption.BUILDING_BLOCK.equals(selectedRowOption)) {
      landscapeOptions.setSelectedRowOption(1);
    }
    else if (ContentOption.ATTRIBUTE.equals(selectedRowOption)) {
      landscapeOptions.setSelectedRowOption(2);
    }
    // initialise the axis levels. Levels retrieved from the XML file have to be set
    // later on when the contents of column and row queries are known (necessary to
    // determine the actually possible levels)
    landscapeOptions.setLevels(new LandscapeElementLevels());
    landscapeOptions.setColumnAxisScalesWithContent(columnAxisScalesWithContent);
  }

  /**
   * Sets the hierarchical levels to display on columns, rows and content
   * 
   * @param memBean
   *          The landscape membean
   */
  public void updateLevels(ManageLandscapeDiagramMemoryBean memBean) {
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    final LandscapeElementLevels levels = landscapeOptions.getLevels();
    if (LandscapeDiagramXML.ContentOption.BUILDING_BLOCK.equals(selectedColumnOption)) {
      determineColumnAxisLevelRange(memBean, levels);
    }
    if (LandscapeDiagramXML.ContentOption.BUILDING_BLOCK.equals(selectedRowOption)) {
      determineRowAxisLevelRange(memBean, levels);
    }
    if (LandscapeDiagramContentTypeXML.INFORMATIONSYSTEMRELEASE.equals(contentType)) {
      determineContentLevelRange(memBean, levels);
    }
  }

  private void determineContentLevelRange(ManageLandscapeDiagramMemoryBean memBean, final LandscapeElementLevels levels) {
    if (contentTopLevel > levels.getContentBottomLevel()) {
      contentTopLevel = levels.getContentBottomLevel();
    }
    if (contentBottomLevel > levels.getContentBottomLevel() || contentBottomLevel < contentTopLevel) {
      contentBottomLevel = levels.getContentBottomLevel();
    }
    String selectedLevelRangeContent = Integer.toString(contentTopLevel) + "_" + Integer.toString(contentBottomLevel);
    memBean.getGraphicalOptions().setSelectedLevelRangeContent(selectedLevelRangeContent);
  }

  private void determineRowAxisLevelRange(ManageLandscapeDiagramMemoryBean memBean, final LandscapeElementLevels levels) {
    if (sideAxisTopLevel > levels.getSideAxisBottomLevel()) {
      sideAxisTopLevel = levels.getSideAxisBottomLevel();
    }
    if (sideAxisBottomLevel > levels.getSideAxisBottomLevel() || sideAxisBottomLevel < sideAxisTopLevel) {
      sideAxisBottomLevel = levels.getSideAxisBottomLevel();
    }
    String selectedLevelRangeRowAxis = Integer.toString(sideAxisTopLevel) + "_" + Integer.toString(sideAxisBottomLevel);
    memBean.getGraphicalOptions().setSelectedLevelRangeRowAxis(selectedLevelRangeRowAxis);
  }

  private void determineColumnAxisLevelRange(ManageLandscapeDiagramMemoryBean memBean, final LandscapeElementLevels levels) {
    if (topAxisTopLevel > levels.getTopAxisBottomLevel()) {
      topAxisTopLevel = levels.getTopAxisBottomLevel();
    }
    if (topAxisBottomLevel > levels.getTopAxisBottomLevel() || topAxisBottomLevel < topAxisTopLevel) {
      topAxisBottomLevel = levels.getTopAxisBottomLevel();
    }
    String selectedLevelRangeColumnAxis = Integer.toString(topAxisTopLevel) + "_" + Integer.toString(topAxisBottomLevel);
    memBean.getGraphicalOptions().setSelectedLevelRangeColumnAxis(selectedLevelRangeColumnAxis);
  }

  /**
   * Update the attributes of the memBean. Important: Updates and content query have to be
   * initialised via service method before calling this method See
   * {@link de.iteratec.iteraplan.businesslogic.service.InitFormHelperService#initLandscapeDiagram(java.util.List, java.util.List, LandscapeDiagramXML)}
   * 
   * @param memBean
   *          The memBean that will be update by this XML DTO
   * @param currentLocale
   *          The current user's locale
   */
  public void updateAttributes(ManageLandscapeDiagramMemoryBean memBean, Locale currentLocale) {
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    if (dialogStep > 1 && ContentOption.ATTRIBUTE.equals(selectedColumnOption) && selectedColumnAttributeId != null) {
      landscapeOptions.setSelectedColumnAttributeId(selectedColumnAttributeId);
    }
    if (dialogStep > 1) {
      if (ContentOption.ATTRIBUTE.equals(selectedRowOption) && selectedRowAttributeId != null) {
        landscapeOptions.setSelectedRowAttributeId(selectedRowAttributeId);
      }
      if (color != null && color.getAttributeId() != null) {
        landscapeOptions.getColorOptionsBean().setDimensionAttributeId(color.getAttributeId());
      }
      if (lineType != null && lineType.getAttributeId() != null) {
        landscapeOptions.getLineOptionsBean().setDimensionAttributeId(lineType.getAttributeId());
      }
    }
  }

  private void logError(String message) {
    LOGGER.error("Validation error in {0} during validation: {1}", LandscapeDiagramXML.class.getName(), message);
    throw new IteraplanTechnicalException(IteraplanErrorMessages.ILLEGAL_XML_FILE_DATA);
  }

  private void logError(String message, Throwable e) {
    LOGGER.error("Validation error in {0} during validation: {1}", LandscapeDiagramXML.class.getName(), message);
    throw new IteraplanTechnicalException(IteraplanErrorMessages.ILLEGAL_XML_FILE_DATA, e);
  }

  @XmlElement
  public boolean isUseColorRange() {
    return useColorRange;
  }

  public void setUseColorRange(boolean useColorRange) {
    this.useColorRange = useColorRange;
  }

}

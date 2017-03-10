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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.Landscape;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.reports.interchange.InterchangeBean;
import de.iteratec.iteraplan.businesslogic.reports.query.QueryTreeGenerator;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.LandscapeElementLevels;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.LandscapeOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ManageLandscapeDiagramMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QFirstLevel;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QPart;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.IPresentationExtension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.FastExportService;
import de.iteratec.iteraplan.businesslogic.service.GeneralBuildingBlockService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.common.util.StringEnumReflectionHelper;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.dto.LandscapeDiagramConfigDTO;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.queries.SavedQueryEntity;
import de.iteratec.iteraplan.model.queries.SavedQueryEntityInfo;
import de.iteratec.iteraplan.model.sorting.BuildingBlockComparator;
import de.iteratec.iteraplan.model.xml.LandscapeDiagramXML;
import de.iteratec.iteraplan.model.xml.ReportXML;
import de.iteratec.iteraplan.model.xml.query.EnumAttributeXML;
import de.iteratec.iteraplan.model.xml.query.PostProcessingAdditionalOptionsXML;
import de.iteratec.iteraplan.model.xml.query.PostProcessingStrategiesXML;
import de.iteratec.iteraplan.model.xml.query.PostProcessingStrategyXML;
import de.iteratec.iteraplan.model.xml.query.QueryFormXML;
import de.iteratec.iteraplan.model.xml.query.QueryResultXML;
import de.iteratec.iteraplan.presentation.PresentationHelper;
import de.iteratec.iteraplan.presentation.dialog.FastExport.LandscapeDiagramHelper;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicExportBean;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator;
import de.iteratec.iteraplan.presentation.responsegenerators.VisioResponseGenerator;


@SuppressWarnings("PMD.TooManyMethods")
@Service("landscapeDiagramFrontendService")
public class LandscapeDiagramFrontendServiceImpl extends LandscapeDiagramBaseFrontendServiceImpl implements LandscapeDiagramFrontendService {

  static final Color                  DEFAULT_COLOR = Color.decode("#" + Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR);

  private static final Logger         LOGGER        = Logger.getIteraplanLogger(LandscapeDiagramFrontendServiceImpl.class);

  @Autowired
  private FastExportService           fastExportService;

  @Autowired
  private GeneralBuildingBlockService generalBuildingBlockService;

  public ManageLandscapeDiagramMemoryBean getInitialMemBean() {
    ManageLandscapeDiagramMemoryBean memBean = new ManageLandscapeDiagramMemoryBean();
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    memBean.setSavedQueries(getSavedQueryService().getSavedQueriesWithoutContent(getReportType()));

    landscapeOptions.setDialogStep(1);
    return memBean;
  }

  public ManageLandscapeDiagramMemoryBean directlyToStepTwo(Integer id, String bbType, String diagramVariant) {
    ManageLandscapeDiagramMemoryBean resultMemBean = getInitialMemBean();

    if (id == null || bbType == null || diagramVariant == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }
    BuildingBlock startElement = fastExportService.getStartElement(id, bbType);
    LandscapeDiagramHelper<? extends BuildingBlock> landscapeHelper = fastExportService.retrieveLandscapeHelper(diagramVariant, startElement);

    LandscapeOptionsBean landscapeOptions = resultMemBean.getGraphicalOptions();

    // Select Content Type
    switch (landscapeHelper.getContentTypeOfBb()) {
      case INFORMATIONSYSTEMRELEASE:
        landscapeOptions.setSelectedBbType(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL);
        break;
      case TECHNICALCOMPONENTRELEASE:
        landscapeOptions.setSelectedBbType(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL);
        break;
      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    resultMemBean = selectContentType(resultMemBean, null, null);
    setQueryResultToMemBean(resultMemBean, landscapeHelper.getContentElements(), LandscapeOptionsBean.CONTENT_QUERY);
    // set the result of the content query to all BBs of the corresponding type
    resultMemBean.getQueryResult(LandscapeOptionsBean.CONTENT_QUERY).setResults(
        generalBuildingBlockService.getBuildingBlocksByType(landscapeHelper.getContentTypeOfBb()));

    // Column Type
    landscapeOptions.setSelectedColumnOption(LandscapeOptionsBean.ROW_COLUMN_OPTION_RELATION);
    // By Element Type (Not by Attribute)
    landscapeOptions.setSelectedColumnRelation(landscapeHelper.getColumnExtension());
    resultMemBean = selectColumnType(resultMemBean, null, null); // CRASH!?
    // Inject Column List
    setQueryResultToMemBean(resultMemBean, landscapeHelper.getFilteredColumnElements(), LandscapeOptionsBean.COLUMN_QUERY);

    // Row Type
    landscapeOptions.setSelectedRowOption(LandscapeOptionsBean.ROW_COLUMN_OPTION_RELATION);
    landscapeOptions.setSelectedRowRelation(landscapeHelper.getRowExtension());
    resultMemBean = selectRowType(resultMemBean, null, null);
    // Inject Row List
    setQueryResultToMemBean(resultMemBean, landscapeHelper.getFilteredRowElements(), LandscapeOptionsBean.ROW_QUERY);

    return resultMemBean;
  }

  private void setQueryResultToMemBean(ManageLandscapeDiagramMemoryBean resultMemBean, List<? extends BuildingBlock> bbList, String queryName) {
    Integer[] bbIds = GeneralHelper.createIdArrayFromIdEntities(bbList);
    List<DynamicQueryFormData<?>> queryForms = resultMemBean.getQueryResult(queryName).getQueryForms();
    getInitFormHelperService().dropRestrictionsFromQueryForm(queryForms.get(0));
    resultMemBean.getQueryResult(queryName).setSelectedResultIds(bbIds);
  }

  public ManageLandscapeDiagramMemoryBean fromInterchange(String bbType, String idList, String diagramType) {

    if (idList == null || bbType == null || diagramType == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }

    TypeOfBuildingBlock contentType;
    if (InterchangeBean.IS_LANDSCAPE_CONTENT.equals(diagramType) || InterchangeBean.IS_LANDSCAPE_X_AXIS.equals(diagramType)
        || InterchangeBean.IS_LANDSCAPE_Y_AXIS.equals(diagramType)) {
      contentType = TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE;
    }
    else {
      contentType = TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE;
    }

    ManageLandscapeDiagramMemoryBean resultMemBean = getInitialMemBean();
    resultMemBean.getGraphicalOptions().setSelectedBbType(contentType.getPluralValue());
    resultMemBean = selectContentType(resultMemBean, null, null);
    QueryResult contentResult = resultMemBean.getQueryResult(LandscapeOptionsBean.CONTENT_QUERY);
    getInitFormHelperService().dropRestrictionsFromQueryForm(contentResult.getQueryForms().get(0));

    List<BuildingBlock> allContentEntities = generalBuildingBlockService.getBuildingBlocksByType(contentType);
    Collections.sort(allContentEntities, new BuildingBlockComparator());

    Integer[] contentIds;
    if (InterchangeBean.IS_LANDSCAPE_CONTENT.equals(diagramType) || InterchangeBean.TC_LANDSCAPE_CONTENT.equals(diagramType)) {
      List<Integer> contentIdsAsList = parseSelectedIds(idList);
      contentIds = new Integer[contentIdsAsList.size()];
      for (int i = 0; i < contentIdsAsList.size(); i++) {
        contentIds[i] = contentIdsAsList.get(i);
      }
    }
    else {
      contentIds = new Integer[allContentEntities.size()];
      for (int i = 0; i < allContentEntities.size(); i++) {
        contentIds[i] = allContentEntities.get(i).getId();
      }
    }

    List<DynamicQueryFormData<?>> queryForms = contentResult.getQueryForms();
    QueryResult qrContent = new QueryResult(LandscapeOptionsBean.CONTENT_QUERY, queryForms, contentResult.getTimeseriesQuery(), allContentEntities,
        contentIds, getReportType().getValue());
    resultMemBean.setQueryResult(qrContent);

    if (InterchangeBean.IS_LANDSCAPE_CONTENT.equals(diagramType) || InterchangeBean.TC_LANDSCAPE_CONTENT.equals(diagramType)) {
      setLevels(resultMemBean);
      return resultMemBean;
    }

    TypeOfBuildingBlock axisType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(bbType);

    List<BuildingBlock> allAxisEntities = generalBuildingBlockService.getBuildingBlocksByType(axisType);
    Collections.sort(allAxisEntities, new BuildingBlockComparator());

    List<Integer> axisIdsAsList = parseSelectedIds(idList);
    Integer[] axisIds = new Integer[axisIdsAsList.size()];
    for (int i = 0; i < axisIdsAsList.size(); i++) {
      axisIds[i] = axisIdsAsList.get(i);
    }

    LandscapeOptionsBean landscapeOptions = resultMemBean.getGraphicalOptions();
    if (InterchangeBean.IS_LANDSCAPE_X_AXIS.equals(diagramType) || InterchangeBean.TC_LANDSCAPE_X_AXIS.equals(diagramType)) {
      landscapeOptions.setSelectedColumnOption(LandscapeOptionsBean.ROW_COLUMN_OPTION_RELATION);
      landscapeOptions.setSelectedColumnRelation(getExtensionKeyByPresentationKey(resultMemBean, axisType.getPluralValue()));
      selectColumnType(resultMemBean, null, null);
      QueryResult columnResult = resultMemBean.getQueryResult(LandscapeOptionsBean.COLUMN_QUERY);
      getInitFormHelperService().dropRestrictionsFromQueryForm(columnResult.getQueryForms().get(0));
      queryForms = columnResult.getQueryForms();
      qrContent = new QueryResult(LandscapeOptionsBean.COLUMN_QUERY, queryForms, columnResult.getTimeseriesQuery(), allAxisEntities, axisIds,
          getReportType().getValue());
      resultMemBean.setQueryResult(qrContent);

    }
    else {
      landscapeOptions.setSelectedRowOption(LandscapeOptionsBean.ROW_COLUMN_OPTION_RELATION);
      landscapeOptions.setSelectedRowRelation(getExtensionKeyByPresentationKey(resultMemBean, axisType.getPluralValue()));
      selectRowType(resultMemBean, null, null);
      QueryResult rowResult = resultMemBean.getQueryResult(LandscapeOptionsBean.ROW_QUERY);
      getInitFormHelperService().dropRestrictionsFromQueryForm(rowResult.getQueryForms().get(0));
      queryForms = rowResult.getQueryForms();
      qrContent = new QueryResult(LandscapeOptionsBean.ROW_QUERY, queryForms, rowResult.getTimeseriesQuery(), allAxisEntities, axisIds,
          getReportType().getValue());
      resultMemBean.setQueryResult(qrContent);
    }
    setLevels(resultMemBean);

    return resultMemBean;
  }

  private String getExtensionKeyByPresentationKey(ManageLandscapeDiagramMemoryBean memBean, String presentationKey) {
    for (Entry<String, Extension> entry : memBean.getGraphicalOptions().getCurrentContentType().getRelations().entrySet()) {
      if (entry.getValue().getNameKeyForPresentation().equals(presentationKey)) {
        return entry.getKey();
      }
    }
    throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
  }

  /**
   * Determines the minimum and maximum levels of the selected axis elements and information system content elements
   * and sets them into the memory bean for the diagram configuration.
   * 
   * @param memBean
   *          The memory that acts as in-out parameter
   */
  protected void setLevels(ManageLandscapeDiagramMemoryBean memBean) {
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    final LandscapeElementLevels levels = new LandscapeElementLevels();
    if (landscapeOptions.getSelectedColumnOption() == 1) {
      final int[] columnLevels = getExportService().determineLevels(memBean.getQueryResult(LandscapeOptionsBean.COLUMN_QUERY).getSelectedResults());
      levels.setTopAxisTopLevel(columnLevels[0]);
      levels.setTopAxisBottomLevel(columnLevels[1]);
    }
    if (landscapeOptions.getSelectedRowOption() == 1) {
      final int[] rowLevels = getExportService().determineLevels(memBean.getQueryResult(LandscapeOptionsBean.ROW_QUERY).getSelectedResults());
      levels.setSideAxisTopLevel(rowLevels[0]);
      levels.setSideAxisBottomLevel(rowLevels[1]);
    }
    if (Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL.equals(landscapeOptions.getSelectedBbType())) {
      final int[] contentLevels = getExportService().determineLevels(memBean.getQueryResult(LandscapeOptionsBean.CONTENT_QUERY).getSelectedResults());
      levels.setContentTopLevel(contentLevels[0]);
      levels.setContentBottomLevel(contentLevels[1]);
    }

    landscapeOptions.setLevels(levels);

    //If there is already selected option then do nothing
    if (landscapeOptions.getSelectedLevelRangeColumnAxis() == null) {
      landscapeOptions.setSelectedLevelRangeColumnAxis(levels.getTopAxisTopLevel() + "_" + levels.getTopAxisBottomLevel());
    }

    if (landscapeOptions.getSelectedLevelRangeRowAxis() == null) {
      landscapeOptions.setSelectedLevelRangeRowAxis(levels.getSideAxisTopLevel() + "_" + levels.getSideAxisBottomLevel());
    }

    if (landscapeOptions.getSelectedLevelRangeContent() == null) {
      landscapeOptions.setSelectedLevelRangeContent(levels.getContentTopLevel() + "_" + levels.getContentBottomLevel());
    }

  }

  public ManageLandscapeDiagramMemoryBean selectContentType(ManageLandscapeDiagramMemoryBean memBean, RequestContext context,
                                                            FlowExecutionContext flowContext) {

    Type<?> currentContentType = memBean.getGraphicalOptions().getCurrentContentType();
    DynamicQueryFormData<?> reportForm = getInitFormHelperService().getReportForm(currentContentType);
    TimeseriesQuery tsQuery = getInitFormHelperService().getTimeseriesQuery(currentContentType);
    QueryTreeGenerator qtg = new QueryTreeGenerator(UserContext.getCurrentLocale(), getAttributeTypeService());
    List<DynamicQueryFormData<?>> queryForms = new ArrayList<DynamicQueryFormData<?>>();
    queryForms.add(reportForm);

    // initialise the query form behind the content
    Node node = qtg.generateQueryTree(queryForms);
    List<BuildingBlock> results = getQueryService().evaluateQueryTree(node, tsQuery, new ArrayList<AbstractPostprocessingStrategy<BuildingBlock>>());
    Integer[] ids = GeneralHelper.createIdArrayFromIdEntities(results);
    QueryResult contentQuery = new QueryResult(LandscapeOptionsBean.CONTENT_QUERY, queryForms, tsQuery, results, ids, getReportType().getValue());
    memBean.setQueryResult(contentQuery);

    // go to the second step of the configuration dialog
    setLevels(memBean);
    memBean.getGraphicalOptions().setDialogStep(2);
    return memBean;
  }

  @Override
  protected String getFlowId() {
    return Dialog.GRAPHICAL_REPORTING_LANDSCAPE.getFlowId();
  }

  public ManageLandscapeDiagramMemoryBean selectColumnType(ManageLandscapeDiagramMemoryBean memBean, RequestContext context,
                                                           FlowExecutionContext flowContext) {

    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();
    int columnOption = landscapeOptions.getSelectedColumnOption();

    switch (columnOption) {
      case LandscapeOptionsBean.ROW_COLUMN_OPTION_RELATION:

        Extension columnRelation = landscapeOptions.getCurrentContentType().getRelations().get(landscapeOptions.getSelectedColumnRelation());
        memBean.setQueryResult(createNewQueryResultForRelation(columnRelation, LandscapeOptionsBean.COLUMN_QUERY));
        break;

      case LandscapeOptionsBean.ROW_COLUMN_OPTION_ATTRIBUTE:

        List<String> attrVals = getAttrValuesForSelectedAttribute(memBean, getAttributeById(memBean, landscapeOptions.getSelectedColumnAttributeId()));
        landscapeOptions.setAttrValsOfSelectedColumnAttribute(attrVals);
        break;

      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);

    }

    if (isValidRowOrColumnOption(columnOption)) {
      setLevels(memBean);
      landscapeOptions.setDialogStep(2);
    }

    return memBean;
  }

  /**
   * Retrieves the attribute of the current content type that has the given id. In order to retrieve
   * the attribute, the list of attributes stored in the current content type query is used.
   * 
   * @param memBean
   *          the {@link ManageLandscapeDiagramMemoryBean} for the report
   * @param id
   *          The id of the attribute.
   * @return The {@link BBAttribute} with the given id. Null if not found.
   */
  private BBAttribute getAttributeById(ManageLandscapeDiagramMemoryBean memBean, Integer id) {
    if (id != null && id.intValue() == 0) {
      return new BBAttribute(Integer.valueOf(0), BBAttribute.FIXED_ATTRIBUTE_TYPE, Constants.ATTRIBUTE_TYPEOFSTATUS, Constants.ATTRIBUTE_TYPEOFSTATUS);
    }
    else {
      for (BBAttribute attr : memBean.getQueryResult(LandscapeOptionsBean.CONTENT_QUERY).getQueryForms().get(0).getAvailableAttributes()) {
        if (attr.getId().equals(id)) {
          return attr;
        }
      }
    }
    return null;
  }

  private List<String> getAttrValuesForSelectedAttribute(ManageLandscapeDiagramMemoryBean memBean, BBAttribute selectedAttribute) {
    List<String> attrVals;
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    if (selectedAttribute.getId().intValue() == 0) {
      Locale locale = UserContext.getCurrentLocale();
      attrVals = getInitFormHelperService().getDimensionAttributeValues(selectedAttribute.getId(), locale,
          TypeOfBuildingBlock.getTypeOfBuildingBlockByString(landscapeOptions.getSelectedBbType()));
    }
    else {
      attrVals = getQueryService().getAttributeValuesForAttribute(landscapeOptions.getCurrentContentType(), selectedAttribute);
    }
    return attrVals;
  }

  @SuppressWarnings("unchecked")
  private QueryResult createNewQueryResultForRelation(Extension relation, String queryName) {
    Type<?> requestedType = relation.getRequestedType();
    DynamicQueryFormData<?> reportForm = getInitFormHelperService().getReportForm(requestedType);

    QueryTreeGenerator qtg = new QueryTreeGenerator(UserContext.getCurrentLocale(), getAttributeTypeService());
    List<DynamicQueryFormData<?>> queryForms = new ArrayList<DynamicQueryFormData<?>>();
    queryForms.add(reportForm);
    Node node = qtg.generateQueryTree(queryForms);

    List postProcessingStrategies = Lists.newArrayList();
    if (requestedType.isOrderedHierarchy()) {
      postProcessingStrategies.add(requestedType.getOrderedHierarchyRemoveRootElementStrategy());
    }
    TimeseriesQuery tsQuery = getInitFormHelperService().getTimeseriesQuery(requestedType);
    List<? extends BuildingBlock> results = getQueryService().evaluateQueryTree(node, tsQuery, postProcessingStrategies);

    Integer[] ids = GeneralHelper.createIdArrayFromIdEntities(results);
    return new QueryResult(queryName, queryForms, tsQuery, results, ids, getReportType().getValue());
  }

  public ManageLandscapeDiagramMemoryBean changeContentType(ManageLandscapeDiagramMemoryBean memBean, RequestContext context,
                                                            FlowExecutionContext flowContext) {
    resetContentTypeSelection(memBean);
    resetColumnTypeSelection(memBean);
    resetRowTypeSelection(memBean);
    memBean.getGraphicalOptions().resetConfiguration();
    memBean.getGraphicalOptions().setDialogStep(1);

    return memBean;
  }

  /**
   * Resets all fields that are associated with the content type selection step.
   */
  private void resetContentTypeSelection(ManageLandscapeDiagramMemoryBean memBean) {
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    memBean.setQueryResult(new QueryResult(LandscapeOptionsBean.CONTENT_QUERY));
    landscapeOptions.setSelectedBbType(null);
    landscapeOptions.getLevels().setContentBottomLevel(1);
    landscapeOptions.getLevels().setContentTopLevel(1);
  }

  /**
   * Resets all fields that are associated with the column type selection step.
   */
  private void resetColumnTypeSelection(ManageLandscapeDiagramMemoryBean memBean) {
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    landscapeOptions.resetColumnOption();
    landscapeOptions.setSelectedColumnRelation(null);
    landscapeOptions.setSelectedColumnAttributeId(null);
    landscapeOptions.setAttrValsOfSelectedColumnAttribute(new ArrayList<String>());
    memBean.setQueryResult(new QueryResult(LandscapeOptionsBean.COLUMN_QUERY));
  }

  /**
   * Resets all fields that are associated with the row type selection step.
   */
  private void resetRowTypeSelection(ManageLandscapeDiagramMemoryBean memBean) {
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    landscapeOptions.resetRowOption();
    landscapeOptions.setSelectedRowRelation(null);
    landscapeOptions.setSelectedRowAttributeId(null);
    landscapeOptions.setAttrValsOfSelectedRowAttribute(null);
    memBean.setQueryResult(new QueryResult(LandscapeOptionsBean.ROW_QUERY));
  }

  public ManageLandscapeDiagramMemoryBean selectRowType(ManageLandscapeDiagramMemoryBean memBean, RequestContext context,
                                                        FlowExecutionContext flowContext) {

    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    int rowOption = landscapeOptions.getSelectedRowOption();
    switch (rowOption) {
      case LandscapeOptionsBean.ROW_COLUMN_OPTION_RELATION:

        Extension rowRelation = landscapeOptions.getCurrentContentType().getRelations().get(landscapeOptions.getSelectedRowRelation());
        memBean.setQueryResult(createNewQueryResultForRelation(rowRelation, LandscapeOptionsBean.ROW_QUERY));
        break;

      case LandscapeOptionsBean.ROW_COLUMN_OPTION_ATTRIBUTE:

        List<String> attrVals = getAttrValuesForSelectedAttribute(memBean, getAttributeById(memBean, landscapeOptions.getSelectedRowAttributeId()));
        landscapeOptions.setAttrValsOfSelectedRowAttribute(attrVals);
        break;

      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    if (isValidRowOrColumnOption(rowOption)) {
      setLevels(memBean);
      landscapeOptions.setDialogStep(2);
    }
    return memBean;
  }

  private boolean isValidRowOrColumnOption(int rowOrColumn) {
    return (rowOrColumn > LandscapeOptionsBean.ROW_COLUMN_OPTION_NONE && rowOrColumn <= LandscapeOptionsBean.ROW_COLUMN_OPTION_ATTRIBUTE);
  }

  public ManageLandscapeDiagramMemoryBean updateColorAttribute(ManageLandscapeDiagramMemoryBean memBean, RequestContext context,
                                                               FlowExecutionContext flowContext) {

    ColorDimensionOptionsBean colorOptions = memBean.getGraphicalOptions().getColorOptionsBean();
    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(memBean.getGraphicalOptions().getSelectedBbType());

    getInitFormHelperService().refreshGraphicalExportColorOptions(colorOptions, tobb);

    return memBean;
  }

  public ManageLandscapeDiagramMemoryBean updateLineTypeAttribute(ManageLandscapeDiagramMemoryBean memBean, RequestContext context,
                                                                  FlowExecutionContext flowContext) {

    LineDimensionOptionsBean lineOptions = memBean.getGraphicalOptions().getLineOptionsBean();
    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(memBean.getGraphicalOptions().getSelectedBbType());

    getInitFormHelperService().refreshGraphicalExportLineTypeOptions(lineOptions, tobb);

    return memBean;
  }

  public ManageLandscapeDiagramMemoryBean changeColumnType(ManageLandscapeDiagramMemoryBean memBean, RequestContext context,
                                                           FlowExecutionContext flowContext) {
    resetColumnTypeSelection(memBean);
    memBean.getGraphicalOptions().setDialogStep(2);
    return memBean;
  }

  public ManageLandscapeDiagramMemoryBean changeRowType(ManageLandscapeDiagramMemoryBean memBean, RequestContext context,
                                                        FlowExecutionContext flowContext) {
    resetRowTypeSelection(memBean);
    memBean.getGraphicalOptions().setDialogStep(2);

    return memBean;
  }

  @Override
  public void generateGraphicFileResponse(ManageLandscapeDiagramMemoryBean memBean, HttpServletRequest request, HttpServletResponse response) {

    memBean.getGraphicalOptions().validate(memBean);

    final LandscapeDiagramConfigDTO config = new LandscapeDiagramConfigDTO();
    config.initFromMemBean(memBean);

    if (memBean.getGraphicalOptions().getSelectedGraphicFormat().equalsIgnoreCase(Constants.REPORTS_EXPORT_GRAPHICAL_VISIO)) {
      // Visio-specific export procedure
      createVisioExport(config, memBean, request, response);
    }
    else {
      createSVGBasedExport(memBean, config, request, response);
    }

  }

  private void createVisioExport(LandscapeDiagramConfigDTO config, ManageLandscapeDiagramMemoryBean memBean, HttpServletRequest request,
                                 HttpServletResponse response) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Configuration: " + config.toString());
    }

    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    String srvAddr = URLBuilder.getApplicationURL(request);
    landscapeOptions.setServerUrl(srvAddr);

    de.iteratec.visio.model.Document doc = getExportService().generateVisioLandscapeDiagram(config, landscapeOptions);
    // VisioXMLExportBean vBean = new VisioXMLExportBean(doc);

    GraphicsResponseGenerator respGen = new VisioResponseGenerator();
    if (!landscapeOptions.getSelectedGraphicFormat().equalsIgnoreCase(Constants.REPORTS_EXPORT_GRAPHICAL_VISIO)) {
      LOGGER.error("Wrong export format: Visio expected.");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }

    GraphicExportBean vBean = new GraphicExportBean(doc);

    // have the report file written to the HttpServletResponse
    respGen.generateResponse(response, vBean, GraphicsResponseGenerator.GraphicalReport.LANDSCAPE, memBean.getContent());

  }

  private void createSVGBasedExport(ManageLandscapeDiagramMemoryBean memBean, LandscapeDiagramConfigDTO config, HttpServletRequest request,
                                    HttpServletResponse response) {

    LandscapeOptionsBean options = memBean.getGraphicalOptions();

    // Retrieve the application url
    String srvAddr = URLBuilder.getApplicationURL(request);
    options.setServerUrl(srvAddr);

    de.iteratec.svg.model.Document doc = getExportService().generateSvgLandscapeDiagram(config, options);

    GraphicsResponseGenerator respGen = getExportService().getResponseGenerator(options.getSelectedGraphicFormat());

    GraphicExportBean vBean = new GraphicExportBean(doc);

    // have the report file written to the HttpServletResponse
    respGen.generateResponse(response, vBean, GraphicsResponseGenerator.GraphicalReport.LANDSCAPE, memBean.getContent());
  }

  public ManageLandscapeDiagramMemoryBean loadSavedQuery(ManageLandscapeDiagramMemoryBean memBean) {
    return loadSavedQuery(memBean, null);
  }

  public ManageLandscapeDiagramMemoryBean loadSavedQuery(ManageLandscapeDiagramMemoryBean memBean, Integer queryRef) {
    if (memBean.getSavedQueryId() == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    memBean.setReportUpdated(false);

    // get the (already validated) config data of the saved query
    SavedQuery savedQueryfile = getSavedQueryService().getSavedQuery(memBean.getSavedQueryId());
    // LandscapeDiagramXML savedReport = savedQueryService.getSavedReport(savedQueryfile);
    LandscapeDiagramXML savedReport = getSavedQueryService().getSavedLandscapeDiagram(savedQueryfile);

    if (savedReport.isReportUpdated()) {
      memBean.setReportUpdated(true);
    }

    // the if condition check, if the content query is to replace with an other query
    if (queryRef != null && queryRef.intValue() != -1) {
      SavedQuery savedQuery = getSavedQueryService().getSavedQuery(queryRef);
      // checks if the BBType is the same
      if (savedQuery.getResultBbType().getName().equals(savedQueryfile.getResultBbType().getName())) {
        ReportXML reportXML = getSavedQueryService().getSavedReport(savedQuery);
        for (QueryResultXML queryResultXML : reportXML.getQueryResults()) {
          // replace the mainQuery with the new one
          savedReport.setContentQuery(queryResultXML);
        }
      }
      else {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.QUERIES_NOT_COMPATIBLE);
      }
    }

    loadMemoryBeanFromXML(memBean, savedReport);

    memBean.getGraphicalOptions().setSavedQueryInfo(new SavedQueryEntityInfo(savedQueryfile, getReportType()));

    memBean.setXmlQueryName(savedQueryfile.getName());
    memBean.setXmlQueryDescription(savedQueryfile.getDescription());

    return memBean;
  }

  public void loadMemoryBeanFromXML(ManageLandscapeDiagramMemoryBean memBean, LandscapeDiagramXML savedReport) {
    // copy the data from the XML DTO to the memBean
    savedReport.update(memBean, UserContext.getCurrentLocale());

    if (memBean.getGraphicalOptions().getDialogStep() > 1) {
      // load the content query from the XML DTO
      setContentQuery(memBean, savedReport);
      // after the content query has been set, handle attributes that are part of the saved query
      // i.e. check if they still exist and derive their current database id
      DynamicQueryFormData<?> contentFormData = memBean.getQueryResult(LandscapeOptionsBean.CONTENT_QUERY).getQueryForms().get(0);
      getInitFormHelperService()
          .initLandscapeDiagram(contentFormData.getDimensionAttributes(), contentFormData.getAvailableAttributes(), savedReport);
      // set the derived attribute ids on the memBean
      savedReport.updateAttributes(memBean, UserContext.getCurrentLocale());

      // load the column query
      setColumnQuery(memBean, savedReport);

      // load the row query and attributes for color and linetype
      setRowQuery(memBean, savedReport);
      setConfiguration(memBean, savedReport);
    }
    savedReport.updateLevels(memBean);
    memBean.setSavedQueryId(null);

  }

  /**
   * Configure the content area of the memBean
   * 
   * @param memBean
   *          The ManageLandscapeDiagramMemoryBean
   * @param savedReport
   *          The data retrieved from the saved query
   */
  private void setContentQuery(ManageLandscapeDiagramMemoryBean memBean, LandscapeDiagramXML savedReport) {
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    // set the query configuration for the CONTENT query
    if (savedReport.getContentQuery() == null || savedReport.getContentQuery().getQueryForms() == null
        || savedReport.getContentQuery().getQueryForms().isEmpty()) {
      return;
    }
    final QueryResult queryResult = getQueryResult(savedReport.getContentQuery());

    // might not be set in older saved queries, so adding this here for compatibility
    queryResult.setQueryName(LandscapeOptionsBean.CONTENT_QUERY);

    memBean.setQueryResult(queryResult);

    final LandscapeElementLevels levels = landscapeOptions.getLevels();
    final int[] contentLevels = getExportService().determineLevels(memBean.getQueryResult(LandscapeOptionsBean.CONTENT_QUERY).getSelectedResults());
    levels.setContentTopLevel(contentLevels[0]);
    levels.setContentBottomLevel(contentLevels[1]);
    landscapeOptions.setSelectedLevelRangeRowAxis(contentLevels[0] + "_" + contentLevels[1]);
  }

  /**
   * Configure the column area of the memBean
   * 
   * @param mem
   *          The dialog memory
   * @param memBean
   *          The ManageLandscapeDiagramMemoryBean
   * @param savedReport
   *          The data retrieved from the saved query
   */
  private void setColumnQuery(ManageLandscapeDiagramMemoryBean memBean, LandscapeDiagramXML savedReport) {
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();
    landscapeOptions.setSelectedColumnOption(savedReport.getSelectedColumnOption().getContentOptionAsInt());

    // Column contatins buildingblocks
    if (LandscapeDiagramXML.ContentOption.BUILDING_BLOCK.equals(savedReport.getSelectedColumnOption())) {
      final String selectedColumnRelation = savedReport.getSelectedColumnRelation();
      // tell the memBean to display the BuildingBlock Type in colums
      landscapeOptions.setSelectedColumnRelation(selectedColumnRelation);
      landscapeOptions.setSelectedColumnAttributeId(null);

      final QueryResult queryResult = getQueryResult(savedReport.getColumnQuery());

      // might not be set in older saved queries, so adding this here for compatibility
      queryResult.setQueryName(LandscapeOptionsBean.COLUMN_QUERY);

      memBean.setQueryResult(queryResult);

      final LandscapeElementLevels levels = landscapeOptions.getLevels();
      final int[] columnLevels = getExportService().determineLevels(memBean.getQueryResult(LandscapeOptionsBean.COLUMN_QUERY).getSelectedResults());
      levels.setTopAxisTopLevel(columnLevels[0]);
      levels.setTopAxisBottomLevel(columnLevels[1]);
      landscapeOptions.setSelectedLevelRangeColumnAxis(columnLevels[0] + "_" + columnLevels[1]);
    }
    else if (LandscapeDiagramXML.ContentOption.ATTRIBUTE.equals(savedReport.getSelectedColumnOption())) {

      BBAttribute selectedAttribute = getAttributeById(memBean, landscapeOptions.getSelectedColumnAttributeId());
      Integer savedAttributeId = savedReport.getSelectedColumnAttributeId();

      List<String> attrVals = getAttrValuesForSavedAttribute(savedReport, selectedAttribute, savedAttributeId);

      landscapeOptions.setAttrValsOfSelectedColumnAttribute(attrVals);
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  private List<String> getAttrValuesForSavedAttribute(LandscapeDiagramXML savedReport, BBAttribute selectedAttribute, Integer savedAttributeId) {
    List<String> attrVals;
    final Locale locale = UserContext.getCurrentLocale();
    if (savedAttributeId.intValue() == 0) {
      if ("INFORMATIONSYSTEMRELEASE".equals(savedReport.getContentType().toString())) {
        attrVals = StringEnumReflectionHelper.getLanguageSpecificEnumValues(de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus.class,
            locale);
      }
      else {
        attrVals = StringEnumReflectionHelper.getLanguageSpecificEnumValues(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.class,
            locale);
      }
    }
    else {
      attrVals = getQueryService().getAttributeValuesForAttribute(savedReport.getContentType().getQueryType(), selectedAttribute);
    }
    return attrVals;
  }

  /**
   * Configure the row area of the memBean
   * 
   * @param mem
   *          The dialog memory
   * @param memBean
   *          The ManageLandscapeDiagramMemoryBean
   * @param savedReport
   *          The data retrieved from the saved query
   */
  private void setRowQuery(ManageLandscapeDiagramMemoryBean memBean, LandscapeDiagramXML savedReport) {
    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    landscapeOptions.setSelectedRowOption(savedReport.getSelectedRowOption().getContentOptionAsInt());
    // Row contains building blocks
    if (LandscapeDiagramXML.ContentOption.BUILDING_BLOCK.equals(savedReport.getSelectedRowOption())) {
      final String selectedRowRelation = savedReport.getSelectedRowRelation();
      // tell the memBean to display the BuildingBlock Type in row
      landscapeOptions.setSelectedRowRelation(selectedRowRelation);
      landscapeOptions.setSelectedRowAttributeId(null);

      final QueryResult queryResult = getQueryResult(savedReport.getRowQuery());

      // might not be set in older saved queries, so adding this here for compatibility
      queryResult.setQueryName(LandscapeOptionsBean.ROW_QUERY);

      memBean.setQueryResult(queryResult);

      final LandscapeElementLevels levels = landscapeOptions.getLevels();
      final int[] rowLevels = getExportService().determineLevels(memBean.getQueryResult(LandscapeOptionsBean.ROW_QUERY).getSelectedResults());
      levels.setSideAxisTopLevel(rowLevels[0]);
      levels.setSideAxisBottomLevel(rowLevels[1]);
      landscapeOptions.setSelectedLevelRangeRowAxis(rowLevels[0] + "_" + rowLevels[1]);
    }
    else if (LandscapeDiagramXML.ContentOption.ATTRIBUTE.equals(savedReport.getSelectedRowOption())) {

      BBAttribute selectedAttribute = getAttributeById(memBean, landscapeOptions.getSelectedRowAttributeId());
      Integer savedAttributeId = savedReport.getSelectedRowAttributeId();

      List<String> attrVals = getAttrValuesForSavedAttribute(savedReport, selectedAttribute, savedAttributeId);

      landscapeOptions.setAttrValsOfSelectedRowAttribute(attrVals);
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  /**
   * Returns the queryResult based on the query configuration of a saved query
   * 
   * @param queryResultXML
   *          The XML query result
   * @return The initialised query result
   */
  @SuppressWarnings("unchecked")
  private QueryResult getQueryResult(QueryResultXML queryResultXML) {
    Type<?> requestedType = queryResultXML.getQueryForms().get(0).getType();
    // get all report extensions
    HashMap<String, IPresentationExtension> availableReportExtensions = new HashMap<String, IPresentationExtension>(
        requestedType.getExtensionsForPresentation());

    // get all query forms from the saved XML file and set query extensions. used extensions are
    // removed from availableReportExtensions. the map 'availableReportExtensions' hence after this
    // method call only contains extensions not used yet
    List<DynamicQueryFormData<?>> forms = getInitFormHelperService().getSavedReportForm(queryResultXML.getQueryForms(), availableReportExtensions);

    QueryTreeGenerator qtg = new QueryTreeGenerator(UserContext.getCurrentLocale(), getAttributeTypeService());
    Node node = qtg.generateQueryTree(forms);

    List<String> processingInstructions = new ArrayList<String>();
    List<QueryFormXML> queryForms = queryResultXML.getQueryForms();
    Map<String, List<String>> strategyWithOptions = new HashMap<String, List<String>>();

    getSelectedPostProcessingStrategiesFromXML(queryForms, processingInstructions, strategyWithOptions);

    List postProcessingStrategies = Lists.newArrayList();
    if (requestedType.isOrderedHierarchy()) {
      postProcessingStrategies.add(requestedType.getOrderedHierarchyRemoveRootElementStrategy());
    }
    else {
      postProcessingStrategies = QueryResult.getPostProcessingStrategiesByKeys(requestedType, processingInstructions);
    }

    TimeseriesQuery timeseriesQuery = getInitFormHelperService().getTimeseriesQuery(requestedType);

    // backward-compatibility: keep empty timeseries query if saved query does not contain a timeseries query
    if (queryResultXML.getTimeseriesQuery() != null) {
      queryResultXML.getTimeseriesQuery().update(timeseriesQuery, UserContext.getCurrentLocale());
    }

    List<? extends BuildingBlock> results = getQueryService().evaluateQueryTree(node, timeseriesQuery, postProcessingStrategies);

    List<Integer> selectedResultIds = queryResultXML.getSelectedResultIds();
    Integer[] ids = GeneralHelper.createIdArrayFromIdEntities(results);
    if (selectedResultIds != null && selectedResultIds.size() > 0) {
      selectedResultIds.retainAll(Lists.newArrayList(ids));
      ids = selectedResultIds.toArray(new Integer[selectedResultIds.size()]);
    }
    QueryResult qr = new QueryResult(queryResultXML.getQueryName(), forms, timeseriesQuery, results, ids, getReportType().getValue());
    qr.setSelectedPostprocessingStrategies(postProcessingStrategies, strategyWithOptions);

    getRefreshHelperService().refreshTimeseriesQuery(qr);

    return qr;
  }

  /**
   * Initializes the selected post processing strategies, and their options, from the query form xml objects
   * @param queryForms
   *          List of {@link QueryFormXML} objects of the saved query to load
   * @param selectedStrategyNames
   *          OUT-parameter: String-list with the names of the selected post processing strategies
   * @param strategyWithOptions
   *          OUT-parameter: Mapping the name of a post processing strategy to a list of options
   */
  private void getSelectedPostProcessingStrategiesFromXML(List<QueryFormXML> queryForms, List<String> selectedStrategyNames,
                                                          Map<String, List<String>> strategyWithOptions) {
    if (!CollectionUtils.isEmpty(queryForms)) {
      QueryFormXML parent = queryForms.get(0);

      PostProcessingStrategiesXML strategies = parent.getPostProcessingStrategies();
      List<String> selectedPPStrategyKeys = new ArrayList<String>();
      List<PostProcessingStrategyXML> listOfStrategies = strategies.getPostProcessingStrategy();

      if (strategies.getPostProcessingStrategy() != null && !CollectionUtils.isEmpty(strategies.getPostProcessingStrategy())) {
        List<PostProcessingStrategyXML> strategy = strategies.getPostProcessingStrategy();

        for (PostProcessingStrategyXML pps : strategy) {
          selectedStrategyNames.add(pps.getName());
        }
        for (PostProcessingStrategyXML tmp : listOfStrategies) {
          selectedPPStrategyKeys.add(tmp.getName());
          List<PostProcessingAdditionalOptionsXML> additionalOptions = tmp.getAdditionalOptions();
          List<String> selectedPPSOptionKeys = new ArrayList<String>();

          for (PostProcessingAdditionalOptionsXML option : additionalOptions) {
            selectedPPSOptionKeys.add(option.getAdditionalOption());
          }

          strategyWithOptions.put(tmp.getName(), selectedPPSOptionKeys);
        }
      }
    }
  }

  /**
   * Set the colors and lineTypes according to the data saved in the XML file. First get the
   * currently available attribute values, check if they exist in the XML DTO. If so, set the
   * associated color / lineType
   * 
   * @param memBean
   *          The memory bean
   * @param xmlDto
   *          The XML DTO of the saved landscape diagram
   */
  private void setConfiguration(ManageLandscapeDiagramMemoryBean memBean, LandscapeDiagramXML xmlDto) {

    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    setColorConfiguration(memBean, xmlDto);
    setLineTypeConfiguration(memBean, xmlDto);

    landscapeOptions.setStrictRelations(xmlDto.isStrictRelations());
    landscapeOptions.setShowUnspecifiedRelations(xmlDto.isShowUnspecifiedRelations());
    landscapeOptions.setScaleDownContentElements(xmlDto.isScaleDownContentElements());
    landscapeOptions.setGlobalScalingEnabled(xmlDto.isGlobalScalingEnabled());
    landscapeOptions.setSpanContentBetweenCells(xmlDto.isSpanContentBetweenCells());
    landscapeOptions.setUseNamesLegend(xmlDto.isUseNamesLegend());
    landscapeOptions.setShowSavedQueryInfo(xmlDto.isShowSavedQueryInfo());
    landscapeOptions.setSelectedGraphicFormat(xmlDto.getSelectedGraphicsFormat());
    landscapeOptions.setFilterEmptyColumns(xmlDto.isFilterEmptyColumns());
    landscapeOptions.setFilterEmptyRows(xmlDto.isFilterEmptyRows());
  }

  @SuppressWarnings("boxing")
  private void setLineTypeConfiguration(ManageLandscapeDiagramMemoryBean memBean, LandscapeDiagramXML xmlDto) {
    LineDimensionOptionsBean lineOptions = memBean.getGraphicalOptions().getLineOptionsBean();
    TypeOfBuildingBlock bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(memBean.getGraphicalOptions().getSelectedBbType());

    EnumAttributeXML lineType = xmlDto.getLineType();
    if (lineType != null && lineType.getAttributeId() != null && lineType.getAttributeId().intValue() != GraphicalExportBaseOptions.NOTHING_SELECTED) {
      List<String> values = getInitFormHelperService().getDimensionAttributeValues(lineType.getAttributeId(), UserContext.getCurrentLocale(), bbType);

      lineOptions.refreshDimensionOptions(values);
      lineOptions.matchValuesFromSavedQuery(lineType.getAttributeValues(), lineType.getSelectedStyles());
    }
    else {
      lineOptions.resetValueToLineTypeMap();
      lineOptions.setDimensionAttributeId(GraphicalExportBaseOptions.NOTHING_SELECTED);
    }
    getInitFormHelperService().refreshGraphicalExportLineTypeOptions(lineOptions, bbType);
  }

  @SuppressWarnings("boxing")
  private void setColorConfiguration(ManageLandscapeDiagramMemoryBean memBean, LandscapeDiagramXML xmlDto) {
    ColorDimensionOptionsBean colorOptions = memBean.getGraphicalOptions().getColorOptionsBean();
    TypeOfBuildingBlock bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(memBean.getGraphicalOptions().getSelectedBbType());

    EnumAttributeXML color = xmlDto.getColor();
    if (color != null && color.getAttributeId() != null && color.getAttributeId().intValue() != GraphicalExportBaseOptions.NOTHING_SELECTED) {
      List<String> values = getInitFormHelperService().getDimensionAttributeValues(color.getAttributeId(), UserContext.getCurrentLocale(), bbType);
      colorOptions.setColorRangeAvailable(getAttributeTypeService().isNumberAT(color.getAttributeId()));
      colorOptions.refreshDimensionOptions(values);
      colorOptions.matchValuesFromSavedQuery(color.getAttributeValues(), color.getSelectedStyles());
    }
    else {
      colorOptions.resetValueToColorMap();
      colorOptions.setDimensionAttributeId(GraphicalExportBaseOptions.NOTHING_SELECTED);
    }
    colorOptions.setUseColorRange(xmlDto.isUseColorRange());
    getInitFormHelperService().refreshGraphicalExportColorOptions(colorOptions, bbType);
  }

  public ManageLandscapeDiagramMemoryBean saveQuery(ManageLandscapeDiagramMemoryBean memBean, RequestContext requestContext) {
    boolean saveAsOption = memBean.isSaveAs();
    memBean.setSaveAs(false);

    memBean.getGraphicalOptions().validate(memBean);

    Locale locale = UserContext.getCurrentLocale();
    LandscapeDiagramXML saveReport = new LandscapeDiagramXML();

    saveReport.initFrom(memBean, locale);

    String name;
    if (saveAsOption) {
      name = memBean.getXmlSaveAsQueryName();
    }
    else {
      name = memBean.getXmlQueryName();
    }

    if (name == null || "".equals(name)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.SAVEDQUERY_NAME_IS_NULL);
    }

    if (saveAsOption) {
      checkIfQueryNameIsUsed(getReportType(), name);
      acceptNewNameAndDescription(memBean);
    }

    SavedQueryEntity savedQuery = getSavedQueryService().saveLandscapeDiagram(name, memBean.getXmlQueryDescription(), saveReport);
    memBean.getGraphicalOptions().setSavedQueryInfo(new SavedQueryEntityInfo(savedQuery, getReportType()));

    // Load stored XML Queries
    memBean.setSavedQueries(getSavedQueryService().getSavedQueriesWithoutContent(getReportType()));

    //display success message on screen
    requestContext.getFlashScope().put(FLUSHATTRIBUTE_SAVE_SUCCESSFUL_TRIGGER, "true");

    return memBean;
  }

  /**
   * {@inheritDoc}
   */
  public ManageLandscapeDiagramMemoryBean saveQueryAs(ManageLandscapeDiagramMemoryBean memBean, RequestContext requestContext) {
    memBean.setSaveAs(true);
    return saveQuery(memBean, requestContext);
  }

  private void acceptNewNameAndDescription(ManageLandscapeDiagramMemoryBean memBean) {
    memBean.setXmlQueryName(memBean.getXmlSaveAsQueryName());
    memBean.setXmlQueryDescription(memBean.getXmlSaveAsQueryDescription());
    replaceSaveAsProperties(memBean);
  }

  private void replaceSaveAsProperties(ManageLandscapeDiagramMemoryBean memBean) {
    memBean.setXmlSaveAsQueryName("");
    memBean.setXmlSaveAsQueryDescription("");
  }

  public ManageLandscapeDiagramMemoryBean deleteSavedQuery(ManageLandscapeDiagramMemoryBean memBean) {
    if (null == memBean.getDeleteQueryId()) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    getSavedQueryService().deleteSavedQuery(memBean.getDeleteQueryId());

    memBean.setSavedQueries(getSavedQueryService().getSavedQueriesWithoutContent(getReportType()));

    return memBean;
  }

  /**
   * Creates a {@link ManageReportMemoryBean} that this configured according to the passed
   * QueryResult.
   * 
   * @param dimensionQuery
   *          the query configuration that is to be fed into the ReportMemBean
   * @return A pre-configured query configuration
   */
  private ManageReportMemoryBean filterElements(QueryResult dimensionQuery) {
    if (dimensionQuery == null || dimensionQuery.getQueryForms().isEmpty()) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    ManageReportMemoryBean memBean = new ManageReportMemoryBean();
    memBean.setReportType(getReportType());

    memBean.setQueryResult(dimensionQuery);

    for (DynamicQueryFormData<?> queryForm : dimensionQuery.getQueryForms()) {
      Map<String, List<NamedId>> availableAttributeValues = Maps.newHashMap();
      for (QFirstLevel qfl : queryForm.getQueryUserInput().getQueryFirstLevels()) {
        for (QPart qp : qfl.getQuerySecondLevels()) {
          String attrId = qp.getChosenAttributeStringId();
          BBAttribute attribute = queryForm.getBBAttributeByStringId(attrId);
          if (attribute == null) {
            continue;
          }

          if (!availableAttributeValues.containsKey(attribute.getStringId())) {
            List<String> newAttrValList = getQueryService().getAttributeValuesForAttribute(queryForm.getType(), attribute);
            List<NamedId> newAttrValIdList = PresentationHelper.convertStringsToNamedIds(newAttrValList);
            availableAttributeValues.put(attribute.getStringId(), newAttrValIdList);
            if (newAttrValIdList.isEmpty() || (qp.getFreeTextCriteria() != null && !qp.getFreeTextCriteria().isEmpty())) {
              qp.setFreeTextCriteriaSelected(Boolean.TRUE);
            }
            else {
              qp.setFreeTextCriteriaSelected(Boolean.FALSE);
            }
          }
        }
      }
      queryForm.setAvailableAttributeValues(availableAttributeValues);
    }

    getInitFormHelperService().switchQuery(memBean, dimensionQuery.getQueryName());

    return memBean;
  }

  public ManageReportMemoryBean filterColumnResults(ManageLandscapeDiagramMemoryBean landscapeMemBean, RequestContext context,
                                                    FlowExecutionContext flowContext) {
    landscapeMemBean.setQueryResultName(LandscapeOptionsBean.COLUMN_QUERY);
    return filterElements(landscapeMemBean.getQueryResult());
  }

  public ManageReportMemoryBean filterContentResults(ManageLandscapeDiagramMemoryBean landscapeMemBean, RequestContext context,
                                                     FlowExecutionContext flowContext) {
    landscapeMemBean.setQueryResultName(LandscapeOptionsBean.CONTENT_QUERY);
    return filterElements(landscapeMemBean.getQueryResult());
  }

  public ManageReportMemoryBean filterRowResults(ManageLandscapeDiagramMemoryBean landscapeMemBean, RequestContext context,
                                                 FlowExecutionContext flowContext) {
    landscapeMemBean.setQueryResultName(LandscapeOptionsBean.ROW_QUERY);
    return filterElements(landscapeMemBean.getQueryResult());
  }

  public ManageLandscapeDiagramMemoryBean resumeFromFilter(ManageReportMemoryBean reportMemBean, ManageLandscapeDiagramMemoryBean memBean,
                                                           RequestContext context, FlowExecutionContext flowContext) {

    QueryResult queryResult = reportMemBean.getQueryResult();
    String queryName = memBean.getQueryResultName();

    if (memBean.getGraphicalOptions().getQueryResultNames().contains(queryName)) {
      memBean.setQueryResult(queryResult);
      setLevels(memBean);
    }
    return memBean;
  }

  public ManageReportMemoryBean resetReport(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    memBean.resetResults();
    return memBean;
  }

  /**
   * Inherited from ReportBaseFrontendService. At the moment, this is only a dummy implementation
   * which does nothing. The original input parameter <strong>memBean</strong> is returned.
   */
  @Override
  protected ManageReportMemoryBean getMemBeanForChangedQueryType(ManageReportMemoryBean memBean) {
    // return the original mem bean
    return memBean;
  }

  @Override
  protected ReportType getReportType() {
    return ReportType.LANDSCAPE;
  }

}

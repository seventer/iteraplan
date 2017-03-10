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
package de.iteratec.iteraplan.businesslogic.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableBoolean;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.queries.SavedQueryEntity;
import de.iteratec.iteraplan.model.xml.CompositeDiagramXML;
import de.iteratec.iteraplan.model.xml.LandscapeDiagramXML;
import de.iteratec.iteraplan.model.xml.ReportXML;
import de.iteratec.iteraplan.model.xml.query.ColorOptionsXML;
import de.iteratec.iteraplan.model.xml.query.QFirstLevelXML;
import de.iteratec.iteraplan.model.xml.query.QPartXML;
import de.iteratec.iteraplan.model.xml.query.QueryFormXML;
import de.iteratec.iteraplan.model.xml.query.QueryResultXML;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.SavedQueryDAO;


/**
 * Implements {@link SavedQueryService}
 */
@SuppressWarnings("boxing")
public class SavedQueryServiceImpl implements SavedQueryService {

  private static final Logger          LOGGER                    = Logger.getIteraplanLogger(SavedQueryServiceImpl.class);
  private static final int             NON_EXISTING_ATTRIBUTE_ID = -1;

  private SavedQueryDAO                savedQueryDAO;
  private AttributeTypeDAO             attributeTypeDAO;
  private BuildingBlockTypeService     buildingBlockTypeService;

  private static final Set<ReportType> types                     = Sets.newHashSet();
  private static final Set<ReportType> DIAGRAMTYPES              = Sets.newHashSet();

  static {
    for (TypeOfBuildingBlock tobb : TypeOfBuildingBlock.DISPLAY) {
      types.add(ReportType.fromValue(tobb.getValue()));
    }

    for (ReportType dashboardTypes : ReportType.GRAPHICAL_REPORTING_TYPES) {
      DIAGRAMTYPES.add(dashboardTypes);
    }
  }

  public void setSavedQueryDAO(SavedQueryDAO savedQueryDAO) {
    this.savedQueryDAO = savedQueryDAO;
  }

  public void setAttributeTypeDAO(AttributeTypeDAO attributeTypeDAO) {
    this.attributeTypeDAO = attributeTypeDAO;
  }

  public void setBuildingBlockTypeService(BuildingBlockTypeService buildingBlockTypeService) {
    this.buildingBlockTypeService = buildingBlockTypeService;
  }

  /** {@inheritDoc} */
  public LandscapeDiagramXML getSavedLandscapeDiagram(SavedQuery savedQuery) {
    LandscapeDiagramXML diagram = SavedQueryXmlHelper.loadQuery(LandscapeDiagramXML.class, Constants.SCHEMA_GRAPHICAL_LANDSCAPE, savedQuery);

    // Prints the content of currently loaded saved query
    if (LOGGER.isDebugEnabled()) {
      String queryContent = SavedQueryXmlHelper.writeQueryToXMLString(Constants.SCHEMA_GRAPHICAL_LANDSCAPE, diagram);
      LOGGER.debug(queryContent);
    }

    diagram = checkLandscapeDiagramAttributeTypeNotNull(diagram);

    // validate the xml data internally
    diagram.validate(UserContext.getCurrentLocale());
    // LandscapeDiagram column, row, color and linetype attributes are validated
    // during the load process in InitFormHelperServiceImpl#initLandscapeDiagram
    // Attributes of query configurations are validated in the standard query
    // loading mechanism
    // See: {@link InitFormHelperServiceImpl#getSavedReportForm(List, Map)}
    return diagram;
  }

  /** {@inheritDoc} */
  public CompositeDiagramXML getSavedCompositeDiagram(Integer id) {
    SavedQuery savedQuery = getSavedQuery(id);

    return getSavedCompositeDiagram(savedQuery);
  }

  /** {@inheritDoc} */
  public CompositeDiagramXML getSavedCompositeDiagram(SavedQuery savedQuery) {
    CompositeDiagramXML diagram = SavedQueryXmlHelper.loadQuery(CompositeDiagramXML.class, Constants.SCHEMA_COMPOSITE_DIAGRAM, savedQuery);

    // Prints the content of currently loaded saved query
    if (LOGGER.isDebugEnabled()) {
      String queryContent = SavedQueryXmlHelper.writeQueryToXMLString(Constants.SCHEMA_COMPOSITE_DIAGRAM, diagram);
      LOGGER.debug(queryContent);
    }

    diagram = checkCompositeDiagramPartReportsNotNull(diagram);

    // validate the xml data internally
    diagram.validate(UserContext.getCurrentLocale());
    // LandscapeDiagram column, row, color and linetype attributes are validated
    // during the load process in InitFormHelperServiceImpl#initLandscapeDiagram
    // Attributes of query configurations are validated in the standard query
    // loading mechanism
    // See: {@link InitFormHelperServiceImpl#getSavedReportForm(List, Map)}
    return diagram;
  }

  private CompositeDiagramXML checkCompositeDiagramPartReportsNotNull(CompositeDiagramXML diagram) {
    List<Integer> existingIds = CollectionUtils.arrayList();
    for (Integer id : diagram.getPartReports()) {
      SavedQuery partQuery = savedQueryDAO.loadObjectByIdIfExists(id, SavedQuery.class);
      if (partQuery != null) {
        existingIds.add(id);
      }
      else {
        diagram.setReportUpdated(true);
      }
    }
    diagram.setPartReports(existingIds);
    return diagram;
  }

  /** {@inheritDoc} */
  public ReportXML getSavedReport(SavedQuery savedQuery) {
    ReportXML report = SavedQueryXmlHelper.loadQuery(ReportXML.class, Constants.SCHEMA_QUERY, savedQuery);

    // Prints the content of currently loaded saved query
    if (LOGGER.isDebugEnabled()) {
      String queryContent = SavedQueryXmlHelper.writeQueryToXMLString(Constants.SCHEMA_QUERY, report);
      LOGGER.debug(queryContent);
    }

    if (ReportType.CLUSTER.equals(savedQuery.getType())) {
      report = checkClusterDiagramAttributeTypeNotNullOrChanged(report);
    }
    else if (ReportType.INFORMATIONFLOW.equals(savedQuery.getType())) {
      report = checkInformationFlowDiagramAttributeTypeNotNull(report);
    }
    else if (ReportType.PORTFOLIO.equals(savedQuery.getType())) {
      report = checkPortfolioDiagramAttributeTypeNotNull(report);
    }
    else if (ReportType.MASTERPLAN.equals(savedQuery.getType())) {
      report = checkMasterplanDiagramAttributeTypeNotNull(report);
    }
    else if (ReportType.PIE.equals(savedQuery.getType()) || ReportType.BAR.equals(savedQuery.getType())) {
      report = checkPieBarDiagramAttributeTypeNotNull(report);
    }
    else if (ReportType.LINE.equals(savedQuery.getType())) {
      report = checkLineDiagramAttributeTypeNotNull(report);
    }
    else {
      report.setReportUpdated(false);
      report = getReportWithCheckedQueryForms(report);
    }

    // validate the xml data internally and localize dates and numbers
    report.validate(UserContext.getCurrentLocale());
    // Attributes of query configurations are validated in the standard query
    // loading mechanism
    // See: {@link InitFormHelperServiceImpl#getSavedReportForm(List, Map)}
    return report;
  }

  /** {@inheritDoc} */
  public String deleteSavedQuery(Integer id) {
    SavedQuery savedQuery = savedQueryDAO.loadObjectByIdIfExists(id, SavedQuery.class);
    if (savedQuery == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.SAVEDQUERY_NOT_FOUND);
    }
    String name = savedQuery.getName();

    savedQueryDAO.delete(savedQuery);
    return name;
  }

  /** {@inheritDoc} */
  public SavedQueryEntity saveLandscapeDiagram(String name, String description, LandscapeDiagramXML landscapeXML) {
    ReportType type = ReportType.LANDSCAPE;
    String schema = SavedQuery.getSchemaMapping().get(type);
    // get the query result type from the query forms and set it to the saved query
    Type<?> queryResultType = landscapeXML.getContentQuery().getQueryForms().get(0).getType();

    SavedQuery queryInDatabase = getInitializedOrExistingQuery(name, schema, description, type, queryResultType,
        SavedQueryXmlHelper.writeQueryToXMLString(schema, landscapeXML));

    return saveOrUpdate(queryInDatabase);
  }

  /** {@inheritDoc} */
  public SavedQueryEntity saveQuery(String name, String description, ReportXML reportXML, ReportType savedQueryType) {
    String schema = SavedQuery.getSchemaMapping().get(savedQueryType);
    // get the query result type from the query forms and set it to the saved query
    Type<?> queryResultType = reportXML.getQueryResultType();

    String queryXml = SavedQueryXmlHelper.writeQueryToXMLString(schema, reportXML);
    SavedQuery queryInDatabase = getInitializedOrExistingQuery(name, schema, description, savedQueryType, queryResultType, queryXml);

    return saveOrUpdate(queryInDatabase);
  }

  /** {@inheritDoc} */
  public SavedQueryEntity saveCompositeDiagram(String name, String description, CompositeDiagramXML compositeXML) {
    ReportType type = ReportType.COMPOSITE;
    String schema = SavedQuery.getSchemaMapping().get(type);

    SavedQuery queryInDatabase = getNewOrExistingQuery(name, schema, description, type,
        SavedQueryXmlHelper.writeQueryToXMLString(schema, compositeXML));

    return saveOrUpdate(queryInDatabase);
  }

  /**
   * Checks database for existing SavedQuery object with a given name. If no entry exists, a new one
   * is created and initialized.
   * 
   * @param name
   *          Name of the saved query.
   * @param schema
   *          Schema file to use.
   * @param description
   *          Description of the saved query.
   * @param savedQueryType
   *          Type of diagram.
   * @param queryResultType
   *          A {@link Type} object that describes the query result type. This information will
   *          later be used to group saved queries by their result type.
   * @return A newly initialized or existing SavedQuery object.
   */
  private SavedQuery getInitializedOrExistingQuery(String name, String schema, String description, ReportType savedQueryType,
                                                   Type<?> queryResultType, String xmlString) {

    SavedQuery queryInDatabase = getNewOrExistingQuery(name, schema, description, savedQueryType, xmlString);

    if (queryResultType != null) {
      // this block is executed for new queries and queries from older version
      // without that attribute, when they are re-saved
      BuildingBlockType resultBbt = buildingBlockTypeService.getBuildingBlockTypeByType(queryResultType.getTypeOfBuildingBlock());
      queryInDatabase.setResultBbType(resultBbt);
    }

    return queryInDatabase;
  }

  /**
   * Checks database for existing SavedQuery object with a given name. If no entry exists, a new one
   * is created.
   *
   * @param name
   *          Name of the saved query.
   * @param schema
   *          Schema file to use.
   * @param description
   *          Description of the saved query.
   * @param savedQueryType
   *          Type of diagram.
   * @param xmlString
   * @return A newly initialized or existing SavedQuery object.
   */
  private SavedQuery getNewOrExistingQuery(String name, String schema, String description, ReportType savedQueryType, String xmlString) {
    SavedQuery queryInDatabase = getSavedQueryByName(savedQueryType, name);

    if (queryInDatabase != null) {
      queryInDatabase.setDescription(description);
      queryInDatabase.setContent(xmlString);
    }
    else {
      queryInDatabase = new SavedQuery();
      queryInDatabase.setSchemaFile(schema);
      queryInDatabase.setName(name);
      queryInDatabase.setDescription(description);
      queryInDatabase.setType(savedQueryType);
      queryInDatabase.setContent(xmlString);
    }
    return queryInDatabase;
  }

  private SavedQuery getSavedQueryByName(ReportType queryKey, String name) {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put("type", queryKey.getValue());
    properties.put("name", name);
    List<SavedQuery> result = savedQueryDAO.getSavedQueries(SavedQuery.class, properties);
    if (result.size() > 0) {
      return result.get(0);
    }
    else {
      return null;
    }
  }
  
  public boolean existsQuery(ReportType savedQueryType, String name){
    return getSavedQueryByName(savedQueryType, name) != null;
  }

  /** {@inheritDoc} */
  public SavedQuery getSavedQuery(Integer id) {
    SavedQuery savedQuery = savedQueryDAO.loadObjectByIdIfExists(id, SavedQuery.class);
    if (savedQuery == null) {
      LOGGER.error("No query found for query with key " + id);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.SAVEDQUERY_NOT_FOUND);
    }

    return savedQuery;
  }

  /** {@inheritDoc} */
  public List<SavedQuery> getSavedQueries(ReportType queryKey) {
    checkSchemaMapping(queryKey);

    return savedQueryDAO.getSavedQueries(SavedQuery.class, Sets.newHashSet(queryKey));
  }

  /** {@inheritDoc} */
  public List<SavedQuery> getSavedQueries(Set<ReportType> reportTypes) {
    for (ReportType reportType : reportTypes) {
      checkSchemaMapping(reportType);
    }

    return savedQueryDAO.getSavedQueries(SavedQuery.class, reportTypes);
  }

  /** {@inheritDoc} */
  public List<SavedQuery> getSavedQueriesWithoutContent(ReportType reportType) {
    checkSchemaMapping(reportType);

    return savedQueryDAO.getSavedQueriesWithoutContent(SavedQuery.class, Sets.newHashSet(reportType));
  }

  private void checkSchemaMapping(ReportType reportType) {
    if (!SavedQuery.getSchemaMapping().containsKey(reportType)) {
      LOGGER.error("No query type found for key " + reportType);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  /** {@inheritDoc} */
  public List<SavedQuery> getSavedQueriesWithoutContent(Set<ReportType> reportTypes) {
    for (ReportType reportType : reportTypes) {
      checkSchemaMapping(reportType);
    }

    return savedQueryDAO.getSavedQueriesWithoutContent(SavedQuery.class, reportTypes);
  }

  private LandscapeDiagramXML checkLandscapeDiagramAttributeTypeNotNull(LandscapeDiagramXML report) {
    report.setReportUpdated(false);

    checkColorAttributeNotNull(report);
    checkSelectedRowAttributeNotNull(report);
    checkSelectedColumnAttributeNotNull(report);
    checkLineTypeAttributeNotNull(report);

    MutableBoolean tmp = new MutableBoolean(report.isReportUpdated());
    checkQueryFormsAttributeTypeNotNull(report.getColumnQuery().getQueryForms(), tmp);
    checkQueryFormsAttributeTypeNotNull(report.getContentQuery().getQueryForms(), tmp);
    checkQueryFormsAttributeTypeNotNull(report.getRowQuery().getQueryForms(), tmp);

    report.setReportUpdated(tmp.booleanValue());

    return report;
  }

  private void checkLineTypeAttributeNotNull(LandscapeDiagramXML report) {
    Integer lineTypeAttributeId = report.getLineType().getAttributeId();

    if (!isAT(lineTypeAttributeId)) {
      return;
    }

    if (attributeTypeDAO.loadObjectByIdIfExists(lineTypeAttributeId) == null) {
      report.getLineType().setAttributeId(NON_EXISTING_ATTRIBUTE_ID);
      report.setReportUpdated(true);
    }
  }

  /**
   * Returns whether the attribute with the given ID is an attribute type or not
   */
  private boolean isAT(Integer attributeId) {
    return attributeId > 0;
  }

  private void checkSelectedColumnAttributeNotNull(LandscapeDiagramXML report) {
    Integer selectedColumnAttributeId = report.getSelectedColumnAttributeId();

    if (attributeTypeDAO.loadObjectByIdIfExists(selectedColumnAttributeId) == null && isAT(selectedColumnAttributeId)) {
      report.setSelectedColumnAttributeId(NON_EXISTING_ATTRIBUTE_ID);
      report.setReportUpdated(true);
    }
  }

  private void checkSelectedRowAttributeNotNull(LandscapeDiagramXML report) {
    Integer selectedRowAttributeId = report.getSelectedRowAttributeId();

    if (attributeTypeDAO.loadObjectByIdIfExists(selectedRowAttributeId) == null && isAT(selectedRowAttributeId)) {
      report.setSelectedRowAttributeId(NON_EXISTING_ATTRIBUTE_ID);
      report.setReportUpdated(true);
    }
  }

  private void checkColorAttributeNotNull(LandscapeDiagramXML report) {
    Integer colorAttributeId = report.getColor().getAttributeId();

    if (attributeTypeDAO.loadObjectByIdIfExists(colorAttributeId) == null && isAT(colorAttributeId)) {
      report.getColor().setAttributeId(NON_EXISTING_ATTRIBUTE_ID);
      report.getColor().setAttributeValues(null);
      report.setReportUpdated(true);
    }
  }

  private ReportXML checkClusterDiagramAttributeTypeNotNullOrChanged(ReportXML report) {
    report.setReportUpdated(false);

    int colorAttributeId = report.getClusterOptions().getColorAttribute();
    if (attributeTypeDAO.loadObjectByIdIfExists(colorAttributeId) == null && isAT(colorAttributeId)) {
      report.getClusterOptions().setColorAttribute(NON_EXISTING_ATTRIBUTE_ID);
      report.getClusterOptions().setColorAttributeValues(null);
      report.setReportUpdated(true);
    }

    List<ColorOptionsXML> colorOptions = new ArrayList<ColorOptionsXML>();
    for (ColorOptionsXML option : report.getClusterOptions().getColorOptions()) {
      if (isAT(option.getColorAttribute())) {

        AttributeType colorAttributeFromDb = attributeTypeDAO.loadObjectByIdIfExists(option.getColorAttribute());
        if (colorAttributeFromDb == null) {
          option.setColorAttribute(NON_EXISTING_ATTRIBUTE_ID);
          option.setChildrenColorAttributeValues(null);
          option.setChildrenSelectedColors(null);
          if ("attributeBean".equals(option.getDimensionType())) {
            report.getClusterOptions().getTypeOrder().remove(option.getDimensionKey());
          }
          report.setReportUpdated(true);
        }
        else if ("attributeBean".equals(option.getDimensionType()) && !colorAttributeFromDb.getName().equals(option.getDimensionKey())) {
          List<String> typeOrder = report.getClusterOptions().getTypeOrder();

          typeOrder.remove(option.getDimensionKey());
          option.setDimensionKey(colorAttributeFromDb.getName());

          int insertAt = 0;
          if (!option.isSelected()) {
            insertAt = typeOrder.size();
          }
          typeOrder.add(insertAt, option.getDimensionKey());
          report.setReportUpdated(true);
        }
      }
      colorOptions.add(option);
    }
    report.getClusterOptions().setColorOptions(colorOptions);

    return getReportWithCheckedQueryForms(report);
  }

  private ReportXML checkInformationFlowDiagramAttributeTypeNotNull(ReportXML report) {
    report.setReportUpdated(false);

    int edgeTypeAttributeId = report.getInformationFlowOptions().getEdgeAttributeId();
    Integer colorAttributeId = report.getInformationFlowOptions().getColorAttributeId();
    Integer lineTypeAttributeId = report.getInformationFlowOptions().getLineTypeAttributeId();

    if (attributeTypeDAO.loadObjectByIdIfExists(colorAttributeId) == null && isAT(colorAttributeId)) {
      report.getInformationFlowOptions().setColorAttributeId(NON_EXISTING_ATTRIBUTE_ID);
      report.setReportUpdated(true);
    }
    if (attributeTypeDAO.loadObjectByIdIfExists(lineTypeAttributeId) == null && isAT(lineTypeAttributeId)) {
      report.getInformationFlowOptions().setLineTypeAttributeId(NON_EXISTING_ATTRIBUTE_ID);
      report.setReportUpdated(true);
    }
    if (attributeTypeDAO.loadObjectByIdIfExists(edgeTypeAttributeId) == null && isAT(edgeTypeAttributeId)) {
      report.getInformationFlowOptions().setEdgeAttributeId(NON_EXISTING_ATTRIBUTE_ID);
      report.setReportUpdated(true);
    }

    return getReportWithCheckedQueryForms(report);
  }

  private ReportXML checkPortfolioDiagramAttributeTypeNotNull(ReportXML report) {
    report.setReportUpdated(false);

    Integer colorAttributeId = report.getPortfolioOptions().getColorAttributeId();
    Integer sizeAttributeId = report.getPortfolioOptions().getSizeAttributeId();
    Integer xAxisAttributeId = report.getPortfolioOptions().getXAxisAttributeId();
    Integer yAxisAttributeId = report.getPortfolioOptions().getYAxisAttributeId();

    if (attributeTypeDAO.loadObjectByIdIfExists(colorAttributeId) == null && isAT(colorAttributeId)) {
      report.getPortfolioOptions().setColorAttributeId(NON_EXISTING_ATTRIBUTE_ID);
      report.setReportUpdated(true);
    }
    if (attributeTypeDAO.loadObjectByIdIfExists(sizeAttributeId) == null && isAT(sizeAttributeId)) {
      report.getPortfolioOptions().setSizeAttributeId(NON_EXISTING_ATTRIBUTE_ID);
      report.setReportUpdated(true);
    }
    if (attributeTypeDAO.loadObjectByIdIfExists(xAxisAttributeId) == null && isAT(xAxisAttributeId)) {
      report.getPortfolioOptions().setXAxisAttributeId(NON_EXISTING_ATTRIBUTE_ID);
      report.setReportUpdated(true);
    }
    if (attributeTypeDAO.loadObjectByIdIfExists(yAxisAttributeId) == null && isAT(yAxisAttributeId)) {
      report.getPortfolioOptions().setYAxisAttributeId(NON_EXISTING_ATTRIBUTE_ID);
      report.setReportUpdated(true);
    }

    return getReportWithCheckedQueryForms(report);
  }

  private ReportXML checkMasterplanDiagramAttributeTypeNotNull(ReportXML report) {
    report.setReportUpdated(false);
    Integer colorAttributeId = report.getMasterplanOptions().getColorAttributeId();

    if (attributeTypeDAO.loadObjectByIdIfExists(colorAttributeId) == null && isAT(colorAttributeId)) {
      report.getMasterplanOptions().setColorAttributeId(NON_EXISTING_ATTRIBUTE_ID);
      report.setReportUpdated(true);
    }

    return getReportWithCheckedQueryForms(report);
  }

  private ReportXML checkPieBarDiagramAttributeTypeNotNull(ReportXML report) {
    report.setReportUpdated(false);
    Integer colorAttributeId = report.getPieBarOptions().getColorAttributeId();

    if (attributeTypeDAO.loadObjectByIdIfExists(colorAttributeId) == null && isAT(colorAttributeId)) {
      report.getPieBarOptions().setColorAttributeId(NON_EXISTING_ATTRIBUTE_ID);
      report.setReportUpdated(true);
    }

    return getReportWithCheckedQueryForms(report);
  }

  private ReportXML checkLineDiagramAttributeTypeNotNull(ReportXML report) {
    report.setReportUpdated(true);
    return getReportWithCheckedQueryForms(report);
  }

  private ReportXML getReportWithCheckedQueryForms(ReportXML report) {
    MutableBoolean temp = new MutableBoolean(report.isReportUpdated());
    if (report.getQueryResults() != null && !report.getQueryResults().isEmpty()) {
      for (QueryResultXML queryResultXML : report.getQueryResults()) {
        checkQueryFormsAttributeTypeNotNull(queryResultXML.getQueryForms(), temp);
      }
    }
    else if (report.getQueryForms() != null && !report.getQueryForms().isEmpty()) {
      // for compatibility to older saved queries
      checkQueryFormsAttributeTypeNotNull(report.getQueryForms(), temp);
    }
    report.setReportUpdated(temp.booleanValue());

    return report;
  }

  private void checkQueryFormsAttributeTypeNotNull(List<QueryFormXML> queryForms, MutableBoolean tmp) {
    for (QueryFormXML queryFormXML : queryForms) {
      for (QFirstLevelXML qFirstLevelXML : queryFormXML.getQueryUserInput().getQueryFirstLevels()) {
        ArrayList<QPartXML> qPartsList = new ArrayList<QPartXML>();

        for (QPartXML qPart : qFirstLevelXML.getQuerySecondLevels()) {
          if (qPart.getChosenAttributeStringId() != null) {
            Integer id = BBAttribute.getIdByStringId(qPart.getChosenAttributeStringId());
            if (attributeTypeDAO.loadObjectByIdIfExists(id) == null && isAT(id)) {
              String attributeType = BBAttribute.getTypeByStringId(qPart.getChosenAttributeStringId());
              StringBuffer attributeStringBuffer = new StringBuffer(qPart.getChosenAttributeStringId().replace(id.toString(), "-1"));
              attributeStringBuffer.replace(0, attributeType.length(), BBAttribute.BLANK_ATTRIBUTE_TYPE);
              qPart.setChosenAttributeStringId(attributeStringBuffer.toString());
              tmp.setValue(true);
            }
          }
          qPartsList.add(qPart);
        }
        qFirstLevelXML.setQuerySecondLevels(qPartsList);
      }
    }
  }

  /** {@inheritDoc} */
  public List<SavedQuery> getAllSavedQueries() {
    return savedQueryDAO.getSavedQueries(SavedQuery.class);
  }

  /** {@inheritDoc} */
  public SavedQueryEntity saveOrUpdate(SavedQueryEntity entity) {
    return savedQueryDAO.saveOrUpdate(entity);
  }

  /**{@inheritDoc}**/
  public List<SavedQuery> getAllTabularReportsForDashboardTemplates() {
    return savedQueryDAO.getSavedQueriesForDashboardTemplates(types);
  }

  /**{@inheritDoc}**/
  public List<SavedQuery> getAllSavedQueryForDashboards(BuildingBlockType bbt) {
    List<SavedQuery> searchedQuery = new ArrayList<SavedQuery>();

    List<SavedQuery> savedQuerys = getSavedQueries(DIAGRAMTYPES);
    for (SavedQuery sq : savedQuerys) {
      if (sq.getResultBbType() != null && sq.getResultBbType().getName().equals(bbt.getName())) {
        searchedQuery.add(sq);
      }
    }
    return searchedQuery;
  }

}

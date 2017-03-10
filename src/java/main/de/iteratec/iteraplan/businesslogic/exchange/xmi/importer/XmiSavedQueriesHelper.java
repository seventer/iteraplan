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
package de.iteratec.iteraplan.businesslogic.exchange.xmi.importer;

import static de.iteratec.iteraplan.businesslogic.service.wiki.IteraplanSyntaxParserServiceImpl.END_TAG_D;
import static de.iteratec.iteraplan.businesslogic.service.wiki.IteraplanSyntaxParserServiceImpl.START_TAG_D;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry.COLUMN_TYPE;
import de.iteratec.iteraplan.businesslogic.service.CustomDashboardTemplateService;
import de.iteratec.iteraplan.businesslogic.service.SavedQueryService;
import de.iteratec.iteraplan.businesslogic.service.SavedQueryXmlHelper;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.model.queries.CustomDashboardTemplate;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.xml.CompositeDiagramXML;
import de.iteratec.iteraplan.model.xml.LandscapeDiagramXML;
import de.iteratec.iteraplan.model.xml.ReportXML;
import de.iteratec.iteraplan.model.xml.query.ColorOptionsXML;
import de.iteratec.iteraplan.model.xml.query.ColumnEntryXML;
import de.iteratec.iteraplan.model.xml.query.MasterplanRowTypeOptionsXML;
import de.iteratec.iteraplan.model.xml.query.QFirstLevelXML;
import de.iteratec.iteraplan.model.xml.query.QPartXML;
import de.iteratec.iteraplan.model.xml.query.QueryFormXML;
import de.iteratec.iteraplan.model.xml.query.QueryResultXML;


/**
 * A helper class for updating the {@link SavedQuery} attribute type IDs. During import the entity IDs
 * can change, so this class re-maps the required entity IDs.
 *
 */
public class XmiSavedQueriesHelper {

  private static final Logger                  LOGGER = Logger.getIteraplanLogger(XmiSavedQueriesHelper.class);

  private final SavedQueryService              savedQueryService;
  private final CustomDashboardTemplateService templateService;

  public XmiSavedQueriesHelper(SavedQueryService savedQueryService, CustomDashboardTemplateService templateService) {
    this.savedQueryService = savedQueryService;
    this.templateService = templateService;
  }

  /**
   * Updates the entity IDs for all already saved {@link SavedQuery} entities.
   * 
   * @param objectToEObject the map containing the saved {@link IdEntity} objects associated with the 
   *    {@link EObject} instances from XMI import. Will be used to track the changed IDs
   */
  public void update(Map<IdEntity, EObject> objectToEObject) {
    Map<Integer, Integer> atIdMappings = getChangedIdMappings(objectToEObject, AttributeType.class);
    Map<Integer, Integer> bbIdMappings = getChangedIdMappings(objectToEObject, BuildingBlock.class);

    updateSavedQueries(objectToEObject, atIdMappings, bbIdMappings);
    updateDashboardTemplates(objectToEObject);
  }

  private void updateSavedQueries(Map<IdEntity, EObject> objectToEObject, Map<Integer, Integer> atIdMappings, Map<Integer, Integer> bbIdMappings) {
    List<SavedQuery> loadDbInstances = savedQueryService.getAllSavedQueries();
    for (SavedQuery savedQuery : loadDbInstances) {
      if (ReportType.LANDSCAPE.equals(savedQuery.getType())) {
        updateLandscapeDiagram(atIdMappings, bbIdMappings, savedQuery);
      }
      else if (ReportType.CLUSTER.equals(savedQuery.getType())) {
        updateClusterDiagram(atIdMappings, bbIdMappings, savedQuery);
      }
      else if (ReportType.INFORMATIONFLOW.equals(savedQuery.getType())) {
        updateInformationFlowDiagram(atIdMappings, bbIdMappings, savedQuery);
      }
      else if (ReportType.PORTFOLIO.equals(savedQuery.getType())) {
        updatePortfolioDiagram(atIdMappings, bbIdMappings, savedQuery);
      }
      else if (ReportType.MASTERPLAN.equals(savedQuery.getType())) {
        updateMasterplanDiagram(atIdMappings, bbIdMappings, savedQuery);
      }
      else if (ReportType.PIE.equals(savedQuery.getType()) || ReportType.BAR.equals(savedQuery.getType())) {
        updatePieBarDiagram(atIdMappings, bbIdMappings, savedQuery);
      }
      else if (ReportType.COMPOSITE.equals(savedQuery.getType())) {
        updateCompositeDiagram(objectToEObject, savedQuery);
      }
      else {
        updateQueryForms(atIdMappings, savedQuery);
      }
    }
  }

  private void updateDashboardTemplates(Map<IdEntity, EObject> objectToEObject) {
    LOGGER.info("Updating dashboard templates...");
    Map<Integer, Integer> changedIdMappings = getChangedIdMappings(objectToEObject, SavedQuery.class);
    LOGGER.debug("Changed savedQuery-IDs: {0}", changedIdMappings);
    List<CustomDashboardTemplate> templateList = templateService.getCustomDashboardTemplate();
    LOGGER.debug("Templates: {0}", templateList);

    for (CustomDashboardTemplate template : templateList) {
      LOGGER.info("Handling {0}...", template);
      String dashboardTemplate = template.getContent();

      String diagramRegex = START_TAG_D + "(.*?)>([0-9]+?)" + END_TAG_D;
      Pattern diagramPattern = Pattern.compile(diagramRegex);
      Matcher matcher = diagramPattern.matcher(dashboardTemplate);
      List<Integer> diagramIds = Lists.newArrayList();
      while (matcher.find()) {
        LOGGER.debug("Found diagram reference: {0}", matcher.group());
        diagramIds.add(Integer.valueOf(Integer.parseInt(matcher.group(2))));
      }
      LOGGER.info("Old diagram-IDs referenced in the template: {0}", diagramIds);

      // to avoid unwanted results when some new IDs are equal to existing old IDs
      dashboardTemplate = matcher.replaceAll(START_TAG_D + "$1>_$2_" + END_TAG_D);
      //      LOGGER.debug("Intermediary dashboardTemplate:\n{0}", dashboardTemplate);

      for (Integer oldId : diagramIds) {
        if (changedIdMappings.containsKey(oldId)) {
          Integer newId = changedIdMappings.get(oldId);
          LOGGER.debug("Replacing diagram-ID {0} with {1}", oldId, newId);
          dashboardTemplate = dashboardTemplate.replace(">_" + oldId.toString() + "_" + END_TAG_D, ">" + newId.toString() + END_TAG_D);
        }
        else {
          LOGGER.debug("No need to replace diagram-ID {0}", oldId);
          dashboardTemplate = dashboardTemplate.replace(">_" + oldId.toString() + "_" + END_TAG_D, ">" + oldId.toString() + END_TAG_D);
        }
      }
      //      LOGGER.debug("Final transformed dashboardTemplate:\n{0}", dashboardTemplate);
      template.setContent(dashboardTemplate);
    }
  }

  /**
   * Updates the entity IDs for landscape diagram.
   * 
   * @param atIdMappings the {@link EObject} id associated with the {@link IdEntity} id. Is 
   *    used to track the changed attribute type IDs
   * @param bbIdMappings the {@link EObject} id associated with the {@link IdEntity} id. Is 
   *    used to track the changed building block IDs
   * @param savedQuery the saved query to update
   */
  private void updateLandscapeDiagram(Map<Integer, Integer> atIdMappings, Map<Integer, Integer> bbIdMappings, SavedQuery savedQuery) {
    LandscapeDiagramXML landscapeDiagram = SavedQueryXmlHelper.loadQuery(LandscapeDiagramXML.class, Constants.SCHEMA_GRAPHICAL_LANDSCAPE, savedQuery);
    updateQueryFormParts(atIdMappings, landscapeDiagram.getContentQuery().getQueryForms());
    updateQueryFormParts(atIdMappings, landscapeDiagram.getColumnQuery().getQueryForms());
    updateQueryFormParts(atIdMappings, landscapeDiagram.getRowQuery().getQueryForms());
    List<Integer> existingIds = getChangedResultIds(bbIdMappings, landscapeDiagram.getSelectedResultIds());
    landscapeDiagram.setSelectedResultIds(existingIds);

    Integer colorAttributeId = landscapeDiagram.getColor().getAttributeId();
    if (atIdMappings.containsKey(colorAttributeId)) {
      Integer newAtId = atIdMappings.get(colorAttributeId);
      landscapeDiagram.getColor().setAttributeId(newAtId);
    }

    Integer lineAttributeId = landscapeDiagram.getLineType().getAttributeId();
    if (atIdMappings.containsKey(lineAttributeId)) {
      Integer newAtId = atIdMappings.get(lineAttributeId);
      landscapeDiagram.getLineType().setAttributeId(newAtId);
    }

    savedQueryService.saveLandscapeDiagram(savedQuery.getName(), savedQuery.getDescription(), landscapeDiagram);
  }

  /**
   * Updates the entity IDs for cluster diagram.
   * 
   * @param changedIdMappings the {@link EObject} id associated with the {@link IdEntity} id. Is 
   *    used to track the changed attribute type IDs
   * @param savedQuery the saved query to update
   */
  private void updateClusterDiagram(Map<Integer, Integer> changedIdMappings, Map<Integer, Integer> bbIdMappings, SavedQuery savedQuery) {
    ReportXML report = SavedQueryXmlHelper.loadQuery(ReportXML.class, Constants.SCHEMA_QUERY, savedQuery);
    updateQueryResults(changedIdMappings, bbIdMappings, report);

    Integer colorAttributeId = Integer.valueOf(report.getClusterOptions().getColorAttribute());
    if (changedIdMappings.containsKey(colorAttributeId)) {
      Integer newAtId = changedIdMappings.get(colorAttributeId);
      report.getClusterOptions().setColorAttribute(newAtId.intValue());
    }

    Integer selectedAttributeId = Integer.valueOf(report.getClusterOptions().getSelectedAttributeType());
    if (changedIdMappings.containsKey(selectedAttributeId)) {
      Integer newAtId = changedIdMappings.get(selectedAttributeId);
      report.getClusterOptions().setSelectedAttributeType(newAtId.intValue());
    }

    for (ColorOptionsXML option : report.getClusterOptions().getColorOptions()) {
      Integer optionColorAttributeId = Integer.valueOf(option.getColorAttribute());
      if (changedIdMappings.containsKey(optionColorAttributeId)) {
        Integer newAtId = changedIdMappings.get(optionColorAttributeId);
        option.setColorAttribute(newAtId.intValue());
      }
    }

    savedQueryService.saveQuery(savedQuery.getName(), savedQuery.getDescription(), report, savedQuery.getType());
  }

  /**
   * Updates the entity IDs for Information Flow diagram.
   * 
   * @param changedIdMappings the {@link EObject} id associated with the {@link IdEntity} id. Is 
   *    used to track the changed attribute type IDs
   * @param savedQuery the saved query to update
   */
  private void updateInformationFlowDiagram(Map<Integer, Integer> changedIdMappings, Map<Integer, Integer> bbIdMappings, SavedQuery savedQuery) {
    ReportXML report = SavedQueryXmlHelper.loadQuery(ReportXML.class, Constants.SCHEMA_QUERY, savedQuery);
    updateQueryResults(changedIdMappings, bbIdMappings, report);

    Integer edgeTypeAttributeId = Integer.valueOf(report.getInformationFlowOptions().getEdgeAttributeId());
    if (changedIdMappings.containsKey(edgeTypeAttributeId)) {
      Integer newAtId = changedIdMappings.get(edgeTypeAttributeId);
      report.getInformationFlowOptions().setEdgeAttributeId(newAtId.intValue());
    }

    Integer colorAttributeId = report.getInformationFlowOptions().getColorAttributeId();
    if (changedIdMappings.containsKey(colorAttributeId)) {
      Integer newAtId = changedIdMappings.get(colorAttributeId);
      report.getInformationFlowOptions().setColorAttributeId(newAtId);
    }

    Integer lineTypeAttributeId = report.getInformationFlowOptions().getLineTypeAttributeId();
    if (changedIdMappings.containsKey(lineTypeAttributeId)) {
      Integer newAtId = changedIdMappings.get(lineTypeAttributeId);
      report.getInformationFlowOptions().setLineTypeAttributeId(newAtId);
    }

    savedQueryService.saveQuery(savedQuery.getName(), savedQuery.getDescription(), report, savedQuery.getType());
  }

  /**
   * Updates the entity IDs for Portfolio diagram.
   * 
   * @param changedIdMappings the {@link EObject} id associated with the {@link IdEntity} id. Is 
   *    used to track the changed attribute type IDs
   * @param savedQuery the saved query to update
   */
  private void updatePortfolioDiagram(Map<Integer, Integer> changedIdMappings, Map<Integer, Integer> bbIdMappings, SavedQuery savedQuery) {
    ReportXML report = SavedQueryXmlHelper.loadQuery(ReportXML.class, Constants.SCHEMA_QUERY, savedQuery);
    updateQueryResults(changedIdMappings, bbIdMappings, report);

    Integer colorAttributeId = report.getPortfolioOptions().getColorAttributeId();
    if (changedIdMappings.containsKey(colorAttributeId)) {
      Integer newAtId = changedIdMappings.get(colorAttributeId);
      report.getPortfolioOptions().setColorAttributeId(newAtId);
    }

    Integer sizeAttributeId = report.getPortfolioOptions().getSizeAttributeId();
    if (changedIdMappings.containsKey(sizeAttributeId)) {
      Integer newAtId = changedIdMappings.get(sizeAttributeId);
      report.getPortfolioOptions().setSizeAttributeId(newAtId);
    }

    Integer xAxisAttributeId = report.getPortfolioOptions().getXAxisAttributeId();
    if (changedIdMappings.containsKey(xAxisAttributeId)) {
      Integer newAtId = changedIdMappings.get(xAxisAttributeId);
      report.getPortfolioOptions().setXAxisAttributeId(newAtId);
    }

    Integer yAxisAttributeId = report.getPortfolioOptions().getYAxisAttributeId();
    if (changedIdMappings.containsKey(yAxisAttributeId)) {
      Integer newAtId = changedIdMappings.get(yAxisAttributeId);
      report.getPortfolioOptions().setYAxisAttributeId(newAtId);
    }

    savedQueryService.saveQuery(savedQuery.getName(), savedQuery.getDescription(), report, savedQuery.getType());
  }

  /**
   * Updates the entity IDs for Masterplan diagram.
   * 
   * @param changedIdMappings the {@link EObject} id associated with the {@link IdEntity} id. Is 
   *    used to track the changed attribute type IDs
   * @param savedQuery the saved query to update
   */
  private void updateMasterplanDiagram(Map<Integer, Integer> changedIdMappings, Map<Integer, Integer> bbIdMappings, SavedQuery savedQuery) {
    ReportXML report = SavedQueryXmlHelper.loadQuery(ReportXML.class, Constants.SCHEMA_QUERY, savedQuery);
    updateQueryResults(changedIdMappings, bbIdMappings, report);

    Integer colorAttributeId = report.getMasterplanOptions().getColorAttributeId();
    if (changedIdMappings.containsKey(colorAttributeId)) {
      Integer newAtId = changedIdMappings.get(colorAttributeId);
      if (newAtId != null) {
        report.getMasterplanOptions().setColorAttributeId(newAtId);
      }
    }

    List<MasterplanRowTypeOptionsXML> rowTypeOptions = Lists.newArrayList();
    rowTypeOptions.add(report.getMasterplanOptions().getLevel0Options());
    rowTypeOptions.add(report.getMasterplanOptions().getLevel1Options());
    rowTypeOptions.add(report.getMasterplanOptions().getLevel2Options());
    for (MasterplanRowTypeOptionsXML rowTypeOption : rowTypeOptions) {
      if (rowTypeOption != null) {
        Integer newAtId = changedIdMappings.get(rowTypeOption.getColorAttributeId());
        if (newAtId != null) {
          rowTypeOption.setColorAttributeId(newAtId);
        }
      }
    }

    savedQueryService.saveQuery(savedQuery.getName(), savedQuery.getDescription(), report, savedQuery.getType());
  }

  /**
   * Updates the part report IDs for the composite diagrams.
   * 
   * @param objectToEObject objectToEObject the map containing the saved {@link IdEntity} objects associated with the 
   *    {@link EObject} instances from XMI import. Will be used to track the changed IDs
   * @param savedQuery the saved query to update
   */
  private void updateCompositeDiagram(Map<IdEntity, EObject> objectToEObject, SavedQuery savedQuery) {
    CompositeDiagramXML report = SavedQueryXmlHelper.loadQuery(CompositeDiagramXML.class, Constants.SCHEMA_COMPOSITE_DIAGRAM, savedQuery);
    Map<Integer, Integer> changedIdMappings = getChangedIdMappings(objectToEObject, SavedQuery.class);

    List<Integer> existingIds = Lists.newArrayList();
    for (Integer id : report.getPartReports()) {
      if (changedIdMappings.containsKey(id)) {
        Integer newAtId = changedIdMappings.get(id);
        existingIds.add(newAtId);
      }
      else {
        existingIds.add(id);
      }
    }
    report.setPartReports(existingIds);

    savedQueryService.saveCompositeDiagram(savedQuery.getName(), savedQuery.getDescription(), report);
  }

  /**
   * Updates the entity IDs for Pie and Bar diagrams.
   * 
   * @param changedIdMappings the {@link EObject} id associated with the {@link IdEntity} id. Is 
   *    used to track the changed attribute type IDs
   * @param savedQuery the saved query to update
   */
  private void updatePieBarDiagram(Map<Integer, Integer> changedIdMappings, Map<Integer, Integer> bbIdMappings, SavedQuery savedQuery) {
    ReportXML report = SavedQueryXmlHelper.loadQuery(ReportXML.class, Constants.SCHEMA_QUERY, savedQuery);
    updateQueryResults(changedIdMappings, bbIdMappings, report);

    Integer colorAttributeId = Integer.valueOf(report.getPieBarOptions().getColorAttributeId());
    if (changedIdMappings.containsKey(colorAttributeId)) {
      Integer newAtId = changedIdMappings.get(colorAttributeId);
      report.getPieBarOptions().setColorAttributeId(newAtId.intValue());
    }

    Integer keyAttributeId = Integer.valueOf(report.getPieBarOptions().getSelectedKeyAttributeType());
    if (changedIdMappings.containsKey(keyAttributeId)) {
      Integer newAtId = changedIdMappings.get(keyAttributeId);
      report.getPieBarOptions().setSelectedKeyAttributeType(newAtId.intValue());
    }

    savedQueryService.saveQuery(savedQuery.getName(), savedQuery.getDescription(), report, savedQuery.getType());
  }

  private void updateQueryForms(Map<Integer, Integer> changedIdMappings, SavedQuery savedQuery) {
    ReportXML report = SavedQueryXmlHelper.loadQuery(ReportXML.class, Constants.SCHEMA_QUERY, savedQuery);

    // selected results don't matter here, so use an empty bbId change map
    updateQueryResults(changedIdMappings, new HashMap<Integer, Integer>(), report);

    // fix "visible column" attribute id references
    for (ColumnEntryXML visibleColumn : report.getVisibleColumns()) {
      LOGGER.debug("Checking visible column: {0}", visibleColumn);
      if (COLUMN_TYPE.ATTRIBUTE.toString().equals(visibleColumn.getType())) {
        LOGGER.debug("User-def attribute found: {0}", visibleColumn.getHead());
        Integer oldAttrId = Integer.valueOf(Integer.parseInt(visibleColumn.getField()));
        if (changedIdMappings.containsKey(oldAttrId)) {
          Integer newAttrId = changedIdMappings.get(oldAttrId);
          LOGGER.debug("Changing ID-Reference from {0} to {1}", oldAttrId, newAttrId);
          visibleColumn.setField(changedIdMappings.get(oldAttrId).toString());
        }
      }
    }

    savedQueryService.saveQuery(savedQuery.getName(), savedQuery.getDescription(), report, savedQuery.getType());
  }

  private void updateQueryResults(Map<Integer, Integer> changedIdMappings, Map<Integer, Integer> bbIdMappings, ReportXML report) {
    if (report.getQueryResults() != null && !report.getQueryResults().isEmpty()) {
      for (QueryResultXML queryResultXML : report.getQueryResults()) {
        updateQueryFormParts(changedIdMappings, queryResultXML.getQueryForms());
        List<Integer> existingIds = getChangedResultIds(bbIdMappings, queryResultXML.getSelectedResultIds());
        queryResultXML.setSelectedResultIds(existingIds);
      }
    }
    else if (report.getQueryForms() != null && !report.getQueryForms().isEmpty()) {
      // for compatibility with older saved queries
      updateQueryFormParts(changedIdMappings, report.getQueryForms());
      List<Integer> existingIds = getChangedResultIds(bbIdMappings, report.getSelectedResultIds());
      report.setSelectedResultIds(existingIds);
    }
  }

  private void updateQueryFormParts(Map<Integer, Integer> changedIdMappings, Collection<QueryFormXML> queryForms) {
    for (QueryFormXML queryFormXML : queryForms) {
      for (QFirstLevelXML qFirstLevelXML : queryFormXML.getQueryUserInput().getQueryFirstLevels()) {
        for (QPartXML qPart : qFirstLevelXML.getQuerySecondLevels()) {
          if (qPart.getChosenAttributeStringId() != null) {
            Integer attributeId = BBAttribute.getIdByStringId(qPart.getChosenAttributeStringId());
            if (changedIdMappings.containsKey(attributeId)) {
              Integer newAtId = changedIdMappings.get(attributeId);

              StringBuffer attributeStringBuffer = new StringBuffer(qPart.getChosenAttributeStringId().replace(attributeId.toString(),
                  newAtId.toString()));
              qPart.setChosenAttributeStringId(attributeStringBuffer.toString());
            }
          }
        }
      }
    }
  }

  /**
   * Collects the entity changed IDs. Only changed IDs will be returned.
   * 
   * @param objectToEObject the map containing the {@link IdEntity} objects associated with the {@link EObject} instances
   * @param entityClass the class type to collect the IDs for
   * @return the map containing the {@link EObject} id associated with the {@link IdEntity} id
   */
  private Map<Integer, Integer> getChangedIdMappings(Map<IdEntity, EObject> objectToEObject, Class<?> entityClass) {
    Map<Integer, Integer> changedIdMappings = Maps.newHashMap();
    LOGGER.debug("Build mapping of changed IDs for {0}", entityClass.getSimpleName());
    for (Entry<IdEntity, EObject> entry : objectToEObject.entrySet()) {
      IdEntity entity = entry.getKey();
      EObject eObject = entry.getValue();

      if (entityClass.isAssignableFrom(entity.getClass())) {
        Integer eId = XmiHelper.getEId(eObject);

        if (eId != null && !eId.equals(entity.getId())) {
          changedIdMappings.put(eId, entity.getId());
          LOGGER.debug("mapping added: E-ID {0} => ID {1}", eId, entity.getId());
        }
      }
    }
    return changedIdMappings;
  }

  /**
   * Returns the list containing the new result {@link BuildingBlock} IDs. 
   * 
   * @param bbIdMappings the map containing the old {@link BuildingBlock} id associated with the new id
   * @param selectedResultIds the old {@link BuildingBlock} IDs
   * @return the new {@link BuildingBlock} IDs, changed after the import
   */
  private List<Integer> getChangedResultIds(Map<Integer, Integer> bbIdMappings, List<Integer> selectedResultIds) {
    List<Integer> existingIds = Lists.newArrayList();
    for (Integer id : selectedResultIds) {
      if (bbIdMappings.containsKey(id)) {
        Integer newAtId = bbIdMappings.get(id);
        existingIds.add(newAtId);
      }
      else {
        existingIds.add(id);
      }
    }
    return existingIds;
  }

}

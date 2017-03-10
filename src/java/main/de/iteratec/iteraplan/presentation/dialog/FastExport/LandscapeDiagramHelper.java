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
package de.iteratec.iteraplan.presentation.dialog.FastExport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.LandscapeDiagram;
import de.iteratec.iteraplan.businesslogic.reports.query.QueryTreeGenerator;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.LandscapeOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.QueryTypeHelper;
import de.iteratec.iteraplan.businesslogic.reports.query.type.TechnicalComponentReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.businesslogic.service.QueryService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessMappingEntity;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.dto.LandscapeDiagramConfigDTO;
import de.iteratec.iteraplan.model.queries.ReportType;


/**
 * Abstract base class for creating landscape diagrams for fast export. Implements the basic
 * functionality, and expects subclasses to do the specific filtering of content, row, and column
 * elements: offers Template Methods for this purpose.
 * 
 * @author est
 * @param <T>
 *          The type of the start element, e.g. the information system release from which the
 *          diagram generation is triggered through the fast export mechanism. Note that it can
 *          differ from the actual content type. The start element serves as context for diagram
 *          creation, especially in the filtering steps where it is used for generating a specific,
 *          customised view - revolving around this element.
 */
public abstract class LandscapeDiagramHelper<T extends BuildingBlock> {

  private final InitFormHelperService     initFormHelperService;
  private final QueryService              queryService;

  private final LandscapeDiagramConfigDTO config;

  private final T                         startElement;

  private Type<? extends BuildingBlock>   contentType;

  private List<BuildingBlock>             contentElements;

  private List<? extends BuildingBlock>   columnElements;
  private List<? extends BuildingBlock>   rowElements;

  private List<? extends BuildingBlock>   filteredColumnElements;
  private List<? extends BuildingBlock>   filteredRowElements;

  private final TypeOfBuildingBlock       contentTypeOfBb;

  private final TypeOfBuildingBlock       columnTypeOfBb;
  private final TypeOfBuildingBlock       rowTypeOfBb;

  /**
   * template method to filter column elements: to be implemented in a suitable manner in subclasses
   */
  protected abstract List<BuildingBlock> filterContentElements();

  /**
   * template method to filter column elements: to be implemented in a suitable manner in subclasses
   */
  protected abstract List<? extends BuildingBlock> filterColumnElements();

  /**
   * template method to filter row elements: to be implemented in a suitable manner in subclasses
   */
  protected abstract List<? extends BuildingBlock> filterRowElements();

  /**
   * Configures a landscape diagram.
   * 
   * @param startElement
   *          The building block from which the diagram generator is called (through the fast export
   *          mechanism). The start element serves as context for diagram creation, especially in
   *          the filtering steps where it is used for generating a specific, customised view -
   *          revolving around this element. Thus, for its central role, it is referred to as start
   *          element.
   * @param contentType
   *          the building block type of the content
   * @param columnType
   *          the building block type of the columns
   * @param rowType
   *          the building block type of the rows
   */
  public LandscapeDiagramHelper(T startElement, TypeOfBuildingBlock contentType, TypeOfBuildingBlock columnType, TypeOfBuildingBlock rowType) {

    this.contentTypeOfBb = contentType;
    this.columnTypeOfBb = columnType;
    this.rowTypeOfBb = rowType;

    this.initFormHelperService = ServiceHelper.getInitFormHelperService();
    this.queryService = ServiceHelper.getQueryService();

    config = new LandscapeDiagramConfigDTO();
    config.setHideChildrenElements(false);
    config.setScaleDownGraphicElements(LandscapeDiagram.isScaleDownLandscapeContentElements());

    this.startElement = startElement;
    setContent();

    configureColumns();
    configureRows();

  }

  public LandscapeDiagramConfigDTO getConfig() {
    if (!isProducesValidGraph()) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.LANDSCAPE_NO_ELEMENTS);
    }
    return this.config;
  }

  public String getRowExtension() {
    return getExtensionFor(contentTypeOfBb, rowTypeOfBb);
  }

  public String getColumnExtension() {
    return getExtensionFor(contentTypeOfBb, columnTypeOfBb);
  }

  /**
   * sets the content elements of the landscape diagram
   */
  @SuppressWarnings("unchecked")
  private void setContent() {
    contentType = (Type<? extends BuildingBlock>) QueryTypeHelper.getQueryType(contentTypeOfBb);
    config.setContentType(contentType);

    QueryResult contentQuery = getQueryResult(LandscapeOptionsBean.CONTENT_QUERY, contentType);

    contentElements = (List<BuildingBlock>) contentQuery.getSelectedResults();
    List<BuildingBlock> filteredContent = filterContentElements();

    config.setContentBbs(filteredContent);
  }

  /**
   * General helper for retrieving all elements of a certain building block type
   * 
   * @param selectedType
   * @return See method description
   */
  private QueryResult getQueryResult(String queryName, Type<? extends BuildingBlock> selectedType) {
    DynamicQueryFormData<?> reportForm = initFormHelperService.getReportForm(selectedType);

    QueryTreeGenerator qtg = new QueryTreeGenerator(UserContext.getCurrentLocale(), SpringServiceFactory.getAttributeTypeService());

    List<DynamicQueryFormData<?>> queryForms = new ArrayList<DynamicQueryFormData<?>>();
    queryForms.add(reportForm);

    // initialise the query form behind the content
    Node node = qtg.generateQueryTree(queryForms);
    TimeseriesQuery tsQuery = initFormHelperService.getTimeseriesQuery(selectedType);
    List<BuildingBlock> results = queryService.evaluateQueryTree(node, tsQuery, new ArrayList<AbstractPostprocessingStrategy<BuildingBlock>>());

    Integer[] ids = GeneralHelper.createIdArrayFromIdEntities(results);

    QueryResult result = new QueryResult(queryName, queryForms, tsQuery, results, ids, ReportType.LANDSCAPE.getValue());
    result.setTimeseriesQuery(tsQuery);
    return result;

  }

  /**
   * filter which selects all column elements having a business mapping to the start element
   * (content).
   */
  protected List<BuildingBlock> filterColumnElementsByBusinessMapping() {

    // Here, the filtered column elements are collected
    List<BuildingBlock> filteredList = new ArrayList<BuildingBlock>();

    for (BuildingBlock columnElement : columnElements) {
      Set<BusinessMapping> mappings = getBusinessMappings(columnElement);

      // add the current axis element to the filter list if it has a connection to the start element
      if (mappingsIncludeStartElement(mappings)) {
        filteredList.add(columnElement);
      }
    }
    return filteredList;
  }

  /**
   * Returns true if the given mappings include the start element, as well as having something for
   * both the rowType and the columnType
   */
  protected boolean mappingsIncludeStartElement(Set<BusinessMapping> mappings) {

    if (mappings != null) {

      TypeOfBuildingBlock tob = startElement.getBuildingBlockType().getTypeOfBuildingBlock();

      if (startElementIsBusinessMappingEntity()) {

        // go through all business mappings
        for (BusinessMapping mapping : mappings) {

          BuildingBlock content = mapping.getAssociatedElement(tob);

          // the current mapping has the start element in it
          if (startElement.equals(content)) {

            BuildingBlock contentofRowType = mapping.getAssociatedElement(rowTypeOfBb);
            BuildingBlock contentofColumnType = mapping.getAssociatedElement(columnTypeOfBb);

            // The Business Mapping must have something in both the slot for the row and column
            // type, unless that type isn't in a business mapping (in which case null is returned)
            if (!(contentofRowType != null && contentofRowType.getHierarchicalName().equals(AbstractHierarchicalEntity.TOP_LEVEL_NAME))
                && !(contentofColumnType != null && contentofColumnType.getHierarchicalName().equals(AbstractHierarchicalEntity.TOP_LEVEL_NAME))) {

              return true;
            }
          }
        } // for
      }
      else { // start element is NOT a Business Mapping Entity
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
      }

    }

    return false;

  }

  private boolean startElementIsBusinessMappingEntity() {
    return (startElement instanceof BusinessMappingEntity);
  }

  /**
   * ; retrieves the business mappings of a building block element, if present
   * 
   * @param contentElement
   *          the building block element
   * @return a set of business mappings associated to this element (or null if there are none)
   */
  protected Set<BusinessMapping> getBusinessMappings(BuildingBlock contentElement) {

    if (contentElement instanceof BusinessMappingEntity) {

      BusinessMappingEntity bmEntity = (BusinessMappingEntity) contentElement;
      return bmEntity.getBusinessMappings();

    }
    else if (contentElement instanceof InformationSystemRelease) {

      InformationSystemRelease isr = (InformationSystemRelease) contentElement;
      return isr.getBusinessMappings();

    }
    else {
      return null;
    }
  }

  /**
   * Elaborate check if element list is empty (if there is just one element inside, and that element
   * is the special "-", then consider it empty also)
   * 
   * @param filteredList
   * @return False if the List is Empty, else True
   */
  private boolean elementListIsNotEmpty(List<? extends BuildingBlock> elementList) {
    return (elementList != null && elementList.size() > 0 && !(elementList.size() == 1 && elementList.get(0).getHierarchicalName()
        .equals(AbstractHierarchicalEntity.TOP_LEVEL_NAME)));
  }

  /**
   * configures the columns of the landscape diagram. Calls template method to filter column
   * elements.
   */
  @SuppressWarnings("unchecked")
  private void configureColumns() {

    Type<? extends BuildingBlock> columnType = (Type<? extends BuildingBlock>) QueryTypeHelper.getQueryType(columnTypeOfBb);
    QueryResult columnQuery = getQueryResult(LandscapeOptionsBean.COLUMN_QUERY, columnType);

    columnElements = columnQuery.getSelectedResults();

    // call filter method implementation from concrete subclasses
    filteredColumnElements = filterColumnElements();

    // Allow setting this to an empty list here, since we'll check if the whole graph is valid later
    // with isProducesValidGraph()
    config.setTopAxisBbs(filteredColumnElements);

    config.setColumnAxisScalesWithContent(false);

    Extension colExtension = contentType.getRelations().get(getExtensionFor(contentTypeOfBb, columnTypeOfBb));
    config.setColumnExtension(colExtension);

    config.setColumnAttributeId(Integer.valueOf(-1));
  }

  /**
   * configures the rows of the landscape diagram. Calls template method to filter row elements.
   */
  @SuppressWarnings("unchecked")
  private void configureRows() {

    Type<? extends BuildingBlock> rowType = (Type<? extends BuildingBlock>) QueryTypeHelper.getQueryType(rowTypeOfBb);
    QueryResult rowQuery = getQueryResult(LandscapeOptionsBean.ROW_QUERY, rowType);

    rowElements = rowQuery.getSelectedResults();

    // call filter method implementation from concrete subclasses
    filteredRowElements = filterRowElements();

    // Allow setting this to an empty list here, since we'll check if the whole graph is valid later
    // with isProducesValidGraph()
    config.setSideAxisBbs(filteredRowElements);

    Extension rowExtension = contentType.getRelations().get(getExtensionFor(contentTypeOfBb, rowTypeOfBb));
    config.setRowExtension(rowExtension);

    config.setRowAttributeId(Integer.valueOf(-1));

  }

  /**
   * Helper method which retrieves the suitable extension string for the pair of content type and
   * extension.
   * 
   * @param contentTypeBb
   *          the source
   * @param extensionType
   *          the destination
   * @return the String name of this extension
   */
  // no breaks required, as all branches end in a return statement
  @SuppressWarnings("PMD.MissingBreakInSwitch")
  private String getExtensionFor(TypeOfBuildingBlock contentTypeBb, TypeOfBuildingBlock extensionType) {
    switch (contentTypeBb) {
      case INFORMATIONSYSTEMRELEASE:
        switch (extensionType) {
          case BUSINESSPROCESS:
            return InformationSystemReleaseTypeQu.EXTENSION_BM_BUSINESSPROCESS;
          case BUSINESSUNIT:
            return InformationSystemReleaseTypeQu.EXTENSION_BM_BUSINESSUNIT;
          case PRODUCT:
            return InformationSystemReleaseTypeQu.EXTENSION_BM_PRODUCT;
          case PROJECT:
            return InformationSystemReleaseTypeQu.EXTENSION_PROJECTS;
          case BUSINESSFUNCTION:
            return InformationSystemReleaseTypeQu.EXTENSION_BUSINESSFUNCTIONS;
          default:
            throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
        }
      case TECHNICALCOMPONENTRELEASE:
        switch (extensionType) {
          case INFORMATIONSYSTEMRELEASE:
            return TechnicalComponentReleaseTypeQu.EXTENSION_INFORMATIONSYSTEMRELEASES;
          case ARCHITECTURALDOMAIN:
            return TechnicalComponentReleaseTypeQu.EXTENSION_ARCHITECTURALDOMAINS;
          case INFRASTRUCTUREELEMENT:
            return TechnicalComponentReleaseTypeQu.EXTENSION_INFRASTRUCTUREELEMENTS;
          default:
            throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
        }
      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
  }

  public List<BuildingBlock> getContentElements() {
    return contentElements;
  }

  // Calling this removes the "-" from lists. This means no hierarchy is shown, but it also prevents
  // the Report from crashing, and makes both look the same
  public List<? extends BuildingBlock> trimTopLevelFromElementList(List<? extends BuildingBlock> elementList) {
    if (elementList.size() > 0 && elementList.get(0).getHierarchicalName().equals(AbstractHierarchicalEntity.TOP_LEVEL_NAME)) {
      elementList.remove(0);
    }
    return elementList;
  }

  public List<? extends BuildingBlock> getFilteredColumnElements() {
    return trimTopLevelFromElementList(filteredColumnElements);
  }

  public List<? extends BuildingBlock> getFilteredRowElements() {
    return trimTopLevelFromElementList(filteredRowElements);
  }

  public TypeOfBuildingBlock getContentTypeOfBb() {
    return contentTypeOfBb;
  }

  // returns false if either row or column is empty, in which case trying to generate a graph from
  // this would fail
  public boolean isProducesValidGraph() {
    return (elementListIsNotEmpty(getFilteredColumnElements()) && elementListIsNotEmpty(getFilteredRowElements()));
  }

  protected T getStartElement() {
    return startElement;
  }

  protected TypeOfBuildingBlock getRowTypeOfBb() {
    return rowTypeOfBb;
  }

  protected List<? extends BuildingBlock> getRowElements() {
    return rowElements;
  }

  protected TypeOfBuildingBlock getColumnTypeOfBb() {
    return columnTypeOfBb;
  }

  protected List<? extends BuildingBlock> getColumnElements() {
    return columnElements;
  }

}

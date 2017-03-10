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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.ValuesType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.PortfolioOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.type.IPresentationExtension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.UserContext.Permissions;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.xml.LandscapeDiagramXML;
import de.iteratec.iteraplan.model.xml.ReportXML;
import de.iteratec.iteraplan.model.xml.query.ClusterOptionsXML;
import de.iteratec.iteraplan.model.xml.query.InformationFlowOptionsXML;
import de.iteratec.iteraplan.model.xml.query.MasterplanOptionsXML;
import de.iteratec.iteraplan.model.xml.query.PieBarOptionsXML;
import de.iteratec.iteraplan.model.xml.query.PortfolioOptionsXML;
import de.iteratec.iteraplan.model.xml.query.QueryFormXML;
import de.iteratec.iteraplan.model.xml.query.QueryResultXML;


/**
 * Initializes {@link DynamicQueryFormData} and new {@link ManageReportMemoryBean}s for different
 * BuildingBlock types.
 */
public interface InitFormHelperService {

  /**
   * Creates a list of all user-defined attributes for a given {@link Type}. Attributes for which
   * the user does not have the given attribute type group permission are not included in the list.
   * 
   * @param bbType
   *          the Type of the building block
   * @param permission
   *          a AttributeTypeGroupPermission defining the requested permission
   * @return a List of userdefined BBAttribute objects, may be empty, but never null. The first
   *         entry in the list is always a blank attribute for GUI purposes.
   */
  List<BBAttribute> getAvailableUserDefAttributes(Type<?> bbType, AttributeTypeGroupPermissionEnum permission);

  List<BBAttribute> getUserdefAttributes(Type<?> type);

  void setViewConfiguration(ManageReportMemoryBean memBean, TypeOfBuildingBlock type);

  /**
   * Drops all constraints, like timespan or status information, from a query form.
   * The resulting query form represents the selection of all building blocks of
   * a given type.
   * <br><br>
   * Used when interchanging from the iteraQl query console to
   * another report type. In this case it is necessary to remove all
   * constraints from the query form, so that reloading the query does not
   * drop any of the query results.
   * 
   * @param queryForm
   *    The query form to modify.
   */
  void dropRestrictionsFromQueryForm(DynamicQueryFormData<?> queryForm);

  /**
   * Returns an initialized ReportMemBean for the requested BuildingBlock-type if the user has the
   * dialog-permissions for it. Otherwise uses the first BuildingBlock-type the user has the
   * dialog-permissions for.
   * 
   * @param requestedTobString
   *          String-representation of the requested BuildingBlock-type
   * @param possibleTypes
   *          List of string-representations of BuildingBlock-types to choose from for the initialized memory bean
   * @return initialized ManageReportMemoryBean
   */
  ManageReportMemoryBean getInitializedReportMemBeanByViewPerms(String requestedTobString, List<String> possibleTypes);

  /**
   * Returns an initialized ReportMemBean for the BuildingBlock-type represented by {@code tobString}.
   * 
   * @param tobString
   *          String-representation of the requested BuildingBlock-type
   * @return initialized ManageReportMemoryBean
   */
  ManageReportMemoryBean getInitializedReportMemBean(String tobString);

  /**
   * Returns an initialized ReportMemBean for the first BuildingBlock the user has the
   * dialog-permissions for.
   * 
   * @param currentPerms
   * @return initialized ManageReportMemoryBean
   */
  ManageReportMemoryBean getInitializedReportMemBeanByDialogPerms(Permissions currentPerms);

  /**
   * Returns an initialized ReportMemBean for the first BuildingBlock the user has the
   * write-permissions for.
   * 
   * @param currentPerms
   * @return initialized ManageReportMemoryBean
   */
  ManageReportMemoryBean getInitializedReportMemBeanByWritePerms(Permissions currentPerms);

  /**
   * Creates a new {@link DynamicQueryFormData} instance for the given
   * {@link IPresentationExtension} instance.
   * 
   * @param complexExtension
   * @return A new DynamicQueryFormData instance.
   */
  DynamicQueryFormData<?> getReportForm(IPresentationExtension complexExtension);

  /**
   * Creates a new {@link DynamicQueryFormData} instance for the given {@link Type} instance. With
   * the type, the associated fixed and user defined attributes can be determined. With this
   * information, an appropriate DynamicQueryFormData can be created which contains all information
   * for the JSP to render correctly.
   * 
   * @param type
   *          The type for which the DynamicQueryFormData is to be created.
   * @return A new DynamicQueryFormData instance.
   */
  <T extends BuildingBlock> DynamicQueryFormData<T> getReportForm(Type<T> type);

  TimeseriesQuery getTimeseriesQuery(Type<?> type);

  /**
   * Get all query forms from QueryFormXMLs retrieved from a saved XML file and set the query
   * extensions. Already used extensions are removed from availableExtensions, i.e. after this
   * method call the map 'availableExtensions' contains only extensions not used yet
   * 
   * @param queryForms
   *          The queryForms retrieved from the XML file
   * @param availableExtensions
   *          Pass in all availableReportExtension for the underlying type. After the method call,
   *          this map contains only those extensions that are not used yet by the saved query
   * @return The DynamicQueryFormData created based on the saved report (i.e. the report
   *         configuration retrieved from the XML file)
   */
  List<DynamicQueryFormData<?>> getSavedReportForm(List<QueryFormXML> queryForms, Map<String, IPresentationExtension> availableExtensions);

  /**
   * Init the attributes of a saved landscape diagram. Check if the attributes still exist and
   * retrieve their current ids (which might potentially have changed) from the db.
   * 
   * @param availableDimensionAttributes
   *          The available attributes of the dimensions (color, linetype) of the landscape diagram.
   * @param availableAttributes
   *          The available attributes of the content query of the landscape diagram
   * @param landscapeDiagramXML
   *          The saved report
   */
  void initLandscapeDiagram(List<BBAttribute> availableDimensionAttributes, List<BBAttribute> availableAttributes,
                            LandscapeDiagramXML landscapeDiagramXML);

  /**
   * Init the attributes of a saved portfolio diagram.
   * @param portfolioOptions
   *          The saved report options.
   * 
   * @return Frontend options for a portfolio diagram.
   */
  PortfolioOptionsBean initPortfolioDiagramForm(PortfolioOptionsXML portfolioOptions);

  /**
   * Init the attributes of a saved informationFlow diagram.
   * @param informationFlowOptions
   *          The saved report options.
   * 
   * @return Frontend options for a informationFlow diagram.
   */
  InformationFlowOptionsBean initInformationFlowDiagramForm(InformationFlowOptionsXML informationFlowOptions);

  /**
   * Init the attributes of a saved masterplan diagram.
   * @param masterplanOptions
   *          The saved report options.
   * 
   * @return Frontend options for a masterplan diagram.
   */
  MasterplanOptionsBean initMasterplanDiagramForm(MasterplanOptionsXML masterplanOptions);

  /**
   * Init the attributes of a saved cluster diagram.
   * 
   * @param clusterOptions
   *          The saved report options.
   * @return Frontend options for a cluster diagram.
   */
  ClusterOptionsBean initClusterDiagramForm(ClusterOptionsXML clusterOptions);

  /**
   * Init the attributes of a saved pie or bar diagram.
   * 
   * @param memBean
   *          Memory bean of the piebar report
   * @param xmlOptions
   *          The saved report options.
   */
  void initPieBarDiagramForm(ManageReportMemoryBean memBean, PieBarOptionsXML xmlOptions);

  /**
   * Updates the available attribute types option fpr the line chart
   * 
   * @param memBean
   *          Memory bean of the line chart
   */
  void updateLineOptionAvailableAttributeType(ManageReportMemoryBean memBean);

  /**
   * Takes the selected color and fills all corresponding values for the user form. Input options
   * will be manipulated.
   * 
   * @param colorOptions
   *          Current Options. Will be manipulated.
   * @param bbType
   *          The type of the building blocks for which the options are to be refreshed. Only
   *          relevant for the status.
   */
  void refreshGraphicalExportColorOptions(ColorDimensionOptionsBean colorOptions, TypeOfBuildingBlock bbType);

  /**
   * Takes the selected color and fills all corresponding values for the user form. Input options
   * will be manipulated.
   * 
   * @param valuesType
   *          {@link ValuesType} the colors should be refreshed for
   * @param pieBarOptions
   *          Options to define DiagramType and DiagramValuesType
   * @param colorOptions
   *          Current Options. Will be manipulated.
   * @param bbType
   *          The type of the building blocks for which the options are to be refreshed. Only
   *          relevant for the status.
   */
  void refreshGraphicalExportColorOptionsForPieBar(ValuesType valuesType, PieBarDiagramOptionsBean pieBarOptions,
                                                   ColorDimensionOptionsBean colorOptions, TypeOfBuildingBlock bbType);

  /**
   * Takes the selected line types and fills all corresponding values for the user form. Input
   * options will be manipulated.
   * 
   * @param lineOptions
   *          Current Options. Will be manipulated.
   * @param bbType
   *          The type of the building blocks for which the options are to be refreshed. Only
   *          relevant for the status.
   */
  void refreshGraphicalExportLineTypeOptions(LineDimensionOptionsBean lineOptions, TypeOfBuildingBlock bbType);

  /**
   * Returns a list of attribute for a given attribute
   * 
   * @param selectedValue
   *          ID of the selected attribute
   * @param locale
   *          Current locale
   * @param bbType
   *          The type of the building block for which the attribute is taken. Only relevant for the
   *          status.
   * @return List of attribute values
   */
  List<String> getDimensionAttributeValues(Integer selectedValue, Locale locale, TypeOfBuildingBlock bbType);

  /**
   * Fetch needed data for the second order dimensions of the cluster graphic.
   * 
   * @param clusterOptions
   *          The options where the data is to be stored.
   */
  void initializeClusterSecondOrderBeans(ClusterOptionsBean clusterOptions);

  /**
   * Retrieves the list of attributes available for the presentation of the provided type
   * of building block. Also checks permissions.
   * @param bbType
   *    The type of building block
   * @return
   *    The list of available attributes.
   */
  List<BBAttribute> getAttributesForTypeOfBuildingBlock(TypeOfBuildingBlock bbType);

  /**
   * Initializes a masterplan row type with the given parameters.
   * @param relationToBbType
   *    The relationship to the bbType of this row type.
   * @param selectedBBType
   *    The bbType of this row type.
   * @param level
   *    The level of the row type.
   * @return
   *    An initialized masterplan row type.
   */
  MasterplanRowTypeOptions createMasterplanRowType(TypeOfBuildingBlock fromType, String relationToBbType, String selectedBBType, int level);

  /**
   * Initializes the lists of available attributes and associations for the given options.
   * 
   * @param memBean
   *          Memory bean of the piebar report containing the {@link PieBarDiagramOptionsBean}
   */
  void initializePieBarOptions(ManageReportMemoryBean memBean);

  /**
   * Initializes the {@link PieBarDiagramOptionsBean}'s maps denoting the current maximum number of assignments
   * as well as the maps denoting the available hierarchical top and bottom levels for each relation of the
   * currently selected building blocks given by {@code memBean}.
   * @param memBean
   *          Memory-Bean containing the list of the selected building blocks
   */
  void initPieBarAssociationMetrics(ManageReportMemoryBean memBean);

  /**
   * Loads the available attribute types into the cluster options.
   * 
   * @param clusterOptions
   *          The options bean.
   */
  void initializeClusterForAttributeMode(ClusterOptionsBean clusterOptions);

  /**
   * Retrieves a list of the presentation keys of the relations to other types for a given 
   * type of building block.
   * 
   * @param bbType
   *          The presentation key for the plural form of a building block type.
   * @return The list of allowed relations, including the relationships to all
   * connected building block types and all self-relationships visible to the user.
   */
  List<String> getRelationshipsToOtherBbTypes(String bbType);

  /**
   * Retrieves the type of building block to which a given relationship to building block type leads
   * to (see {@link #getRelationshipsToOtherBbTypes(String)}). For relationships between different
   * building block types, the relationship is the plural form of the bulding block type. For self
   * relationships, special keys are used.
   * @return
   *    The type of building block string to which the provided relationship leads.
   */
  String getRelatedTypeOfBuildingBlock(String relationToBbType);

  <E extends ManageReportMemoryBean> E initMemBeanFromSavedGraphicalReport(E memBean, SavedQuery savedQuery, ReportXML savedReport);

  /**
   * Initialize all selected post processing strategies for the current bean
   * 
   * @param memBean
   *          The backing memory bean.
   * @param queryFormXml
   *          The query form as saved in xml
   */
  void initSelectedPostProcessingStrategies(ManageReportMemoryBean memBean, QueryFormXML queryFormXml);

  /**
   * Returns the queryResult based on the query configuration of a saved query
   * @param queryResultXML The XML query result
   * @return The initialised query result
   */
  QueryResult getQueryResult(QueryResultXML queryResultXML);

  /**
   * Sets the {@link QueryResult} with the given name as active in the given memory bean.
   * @param memBean
   *         memory bean for which the active query should be switched
   * @param queryNameToSwitchTo
   *         Name of the {@link QueryResult} to set as active
   */
  <E extends ManageReportMemoryBean> void switchQuery(E memBean, String queryNameToSwitchTo);

  /**
   * Load the SavedResult.<br>
   *
   * @param memBean
   * @param savedReport
   * @param type
   */
  <E extends ManageReportMemoryBean> void loadQueryResults(E memBean, ReportXML savedReport, ReportType type);

  /**
   * @return List of BBTypes which have attributes with active timeseries
   */
  List<String> getBbTypesWithTimeseries();
}

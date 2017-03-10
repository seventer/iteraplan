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
import java.util.Set;

import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.queries.SavedQueryEntity;
import de.iteratec.iteraplan.model.xml.CompositeDiagramXML;
import de.iteratec.iteraplan.model.xml.LandscapeDiagramXML;
import de.iteratec.iteraplan.model.xml.ReportXML;


/**
 * Service for managing saved queries
 */
public interface SavedQueryService {

  /**
   * Returns a saved landscape diagram
   * 
   * @param savedQuery The metadata object of the saved diagram file
   * @return The saved query configuration
   */
  LandscapeDiagramXML getSavedLandscapeDiagram(SavedQuery savedQuery);

  /**
   * Returns a saved landscape diagram
   * 
   * @param id The id of the saved diagram file
   * @return The saved query configuration
   */
  CompositeDiagramXML getSavedCompositeDiagram(Integer id);

  /**
   * Returns a saved landscape diagram
   * 
   * @param savedQuery The metadata object of the saved diagram file
   * @return The saved query configuration
   */
  CompositeDiagramXML getSavedCompositeDiagram(SavedQuery savedQuery);

  /**
   * REturns a saved report
   * 
   * @param savedQuery The metadata object of the saved report file
   * @return The saved report query configuration
   */
  ReportXML getSavedReport(SavedQuery savedQuery);

  /**
   * Returns a list of saved queries of a specific type
   * 
   * @param reportType the report type
   * @return the existing queries for the specified type
   */
  List<SavedQuery> getSavedQueries(ReportType reportType);
  
  /**
   * Returns a list of saved queries of a list of specific types
   * 
   * @param reportTypes - Set of report types
   * @return the existing queries for the specified type
   */
  List<SavedQuery> getSavedQueries(Set<ReportType> reportTypes);

  /**
   * Returns a list of all saved tabular reports that do not have a dashboard instance defined
   * 
   * @return a list of all saved tabular reports
   */
  List<SavedQuery> getAllTabularReportsForDashboardTemplates();

  /**
   * Returns a list of all diagrams
   * 
   * @return a list of all diagrams
   */
  List<SavedQuery> getAllSavedQueryForDashboards(BuildingBlockType bbt);
  
  /**
   * Loads the same {@link SavedQuery} instances as in {@link #getSavedQueries(ReportType)}, 
   * but the returned queries do not contain the content set (see {@link SavedQuery#getContent()}).
   * 
   * @param reportType the report type 
   * @return the existing queries for the specified type
   */
  List<SavedQuery> getSavedQueriesWithoutContent(ReportType reportType);

  /**
   * Loads all {@link SavedQuery} instances containing the {@link ReportType} specified in 
   * {@code reportTypes}. The content of returned queries will be not set. 
   * 
   * @param reportTypes the set of report types
   * @return all {@link SavedQuery} instances containing the {@link ReportType}
   */
  List<SavedQuery> getSavedQueriesWithoutContent(Set<ReportType> reportTypes);

  /**
   * Returns the meta information about a saved file
   * 
   * @param id The id of the file
   * @return The information about the file.
   */
  SavedQuery getSavedQuery(Integer id);

  /**
   * SAves a given Query to a XML file
   * 
   * @param name Name for saved Query.
   * @param description Details for saved query. Will be displayed in list.
   * @param reportXML Query configuration
   * @param savedQueryType The type of the diagram.
   */
  SavedQueryEntity saveQuery(String name, String description, ReportXML reportXML, ReportType savedQueryType);

  /**
   * Saves a composite diagram to DB
   * 
   * @param name Name for saved Query.
   * @param description Details for saved query. Will be displayed in list.
   * @param compositeXML Query configuration
   */
  SavedQueryEntity saveCompositeDiagram(String name, String description, CompositeDiagramXML compositeXML);

  /**
   * Saves a landscape Diagram to DB
   * 
   * @param name Name for saved Query.
   * @param description Details for saved query. Will be displayed in list.
   * @param landscapeXML Configuration.
   */
  SavedQueryEntity saveLandscapeDiagram(String name, String description, LandscapeDiagramXML landscapeXML);

  /**
   * Delete a saved query. First step: delete file, second step: delete db entry.
   * 
   * @param id The id of the saved report file
   * @return Name of the file
   */
  String deleteSavedQuery(Integer id);

  /**
   * Returns all {@link SavedQuery} instances.
   * 
   * @return all {@link SavedQuery} instances
   */
  List<SavedQuery> getAllSavedQueries();

  /**
   * Saved or update the specified {@link SavedQueryEntity}. 
   * 
   * @param entity the saved query entity to save or update
   * @return the saved entity
   */
  SavedQueryEntity saveOrUpdate(SavedQueryEntity entity);
  
  /**
   * Checks if an query with the given name and from the given type already exists.
   * 
   * @param savedQueryType
   * @param name
   * @return <b>true</b> if any query from the given type already exists, otherwise <b>false</b>
   */
  boolean existsQuery(ReportType savedQueryType, String name);

}

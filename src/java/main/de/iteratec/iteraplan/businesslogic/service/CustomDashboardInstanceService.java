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

import de.iteratec.iteraplan.model.queries.CustomDashboardInstance;
import de.iteratec.iteraplan.model.queries.CustomDashboardTemplate;
import de.iteratec.iteraplan.model.queries.SavedQuery;


/**
 * Service for managing custom dashboards
 */
public interface CustomDashboardInstanceService {
  /**
   * save the specified {@link CustomDashboardInstance} into the database
   * 
   * @param instance the {@link CustomDashboardInstance} to save
   */
  CustomDashboardInstance saveCustomDashboardInstance(CustomDashboardInstance instance);

  /**
   * Get all defined {@link CustomDashboardInstance}s
   * 
   * @return all defined {@link CustomDashboardInstance}s
   */
  List<CustomDashboardInstance> getCustomDashboardInstances();

  /**
   * Delete the specified {@link CustomDashboardInstance}
   * 
   * @param instance
   */
  void deleteCustomDashboardInstance(CustomDashboardInstance instance);

  /**
   * Find a specific {@link CustomDashboardInstance} by its id
   * 
   * @param id the id of the {@link CustomDashboardInstance} to load
   * @return the {@link CustomDashboardInstance} with the specified id
   */
  CustomDashboardInstance findById(Integer id);
  
  /**
   * Find a specific {@link CustomDashboardInstance} by its used savedQuery
   * 
   * @param savedQuery
   * @return list of {@link CustomDashboardInstance} which use the savedQuery
   */
  List<CustomDashboardInstance> getCustomDashboardBySavedQuery(SavedQuery savedQuery);
  
  /**
   * Find a specific {@link CustomDashboardInstance} by its used template
   * 
   * @param template
   * @return list of {@link CustomDashboardInstance} which use the CustomDashboardTemplate
   */
  List<CustomDashboardInstance> getCustomDashboardByDashboardTemplate(CustomDashboardTemplate template);
}

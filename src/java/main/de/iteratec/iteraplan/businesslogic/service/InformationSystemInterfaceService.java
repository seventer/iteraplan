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

import org.springframework.beans.support.PagedListHolder;

import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 * Service interface for {@link InformationSystemInterface}s.
 */
public interface InformationSystemInterfaceService extends BuildingBlockService<InformationSystemInterface, Integer> {

  /**
   * Returns a list of {@link InformationSystemRelease}s that have connections to other information
   * system releases.
   * <ul>
   * <li>The resulting list may limited by an optional filter string.</li>
   * <li>The release with the given ID is always included in the list even if it doesn't match the
   * filter condition.</li>
   * <li>It may be specified if inactive releases should be included in the resulting list.</li>
   * </ul>
   * 
   * @param id the ID of the release that is to be included in the result.
   * @param showInactive if false, all releases with status INACTIVE will be filtered out.
   * @return See method description.
   */
  List<InformationSystemRelease> getInformationSystemReleasesWithConnections(Integer id, boolean showInactive);

  /**
   * Fetches all connections for the information system release with the given ID and returns a list
   * of {@link NamedId}s where the ID field corresponds to the ID of each connection and the name
   * field corresponds to the hierarchical name of the release this connection connects to. In order
   * to specify which of the two connected releases is used for the name, a reference release is
   * passed.
   * 
   * @param id
   *          The ID of the reference release.
   * @return See method description.
   */
  List<NamedId> getNamedIdsForConnectionsOfInformationSystemRelease(Integer id);

  /**
   * Retrieves all interfaces matching the search criteria contained in the given
   * {@link InformationSystemInterface}
   * 
   * @param informationSystemInterface A partially filled interface instance, whose properties will be used as search criteria
   * @param page Together with maxResults, this defines the index of the first returned result item in the entire result list.
   *    Typically used for pagination.
   * @param maxResults the maximum number of results to return in the list.
   * @return a list of interfaces which match the specified search criteria.
   */
  PagedListHolder<InformationSystemInterface> getInformationSystemInterfacesBySearch(InformationSystemInterface informationSystemInterface, int page, int maxResults);
}
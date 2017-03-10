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

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.dto.SearchDTO;
import de.iteratec.iteraplan.presentation.dialog.Search.SearchDialogMemory;


/**
 * Service interface for search service.
 */
public interface SearchService {

  /**
   * Refreshes the entire Index from the database
   * 
   * @param purge if set to true, the whole index will be deleted first, removing all entries for all datasources!
   */
  void createIndex(PurgeMode purge);

  /**
   * Populates a new SearchDTO object by executing the search and storing the results into the
   * SearchDTO
   * 
   * @param searchDialogMemory contains details in terms of what needs to be searched for
   * @return SearchDTO
   */
  SearchDTO getSearchDTO(final SearchDialogMemory searchDialogMemory);

  /**
   * Collection of building blocks filtered by type and search query
   * 
   * @param buildingBlockType filtering
   * @param searchField query
   * @return collection of building blocks
   */
  List<BuildingBlock> getSearchBuildingBlocks(TypeOfBuildingBlock buildingBlockType, String searchField);

  public enum PurgeMode {
    /** Requests that all index entries are removed before the index is created. */
    PURGE,
    /** Request that the existing index is not cleared, but only existing entries are updates and new ones added. */
    NO_PURGE;
  }
}

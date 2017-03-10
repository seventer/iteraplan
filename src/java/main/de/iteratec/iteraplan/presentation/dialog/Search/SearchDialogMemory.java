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
package de.iteratec.iteraplan.presentation.dialog.Search;

import java.io.IOException;
import java.io.ObjectInputStream;

import de.iteratec.iteraplan.model.dto.SearchDTO;
import de.iteratec.iteraplan.presentation.memory.DialogMemory;


/**
 * This class represents the memory bean for the search.
 */
public class SearchDialogMemory extends DialogMemory {
  private static final long serialVersionUID = 1L;

  private String            searchField;
  private String            buildingBlockTypeFilter;
  private int               numberOfResults;

  private String            requestType;

  public SearchDialogMemory() {
    super();
  }

  // the DTO containing search data for the HTML result format
  private transient SearchDTO searchDTO = null;

  public SearchDialogMemory(SearchDTO dto) {
    this.searchDTO = dto;
  }

  public final SearchDTO getSearchDTO() {
    return searchDTO;
  }

  public void setSearchDTO(SearchDTO dto) {
    this.searchDTO = dto;
  }

  public String getSearchField() {
    return searchField;
  }

  public void setSearchField(String searchField) {
    this.searchField = searchField;
  }

  public String getBuildingBlockTypeFilter() {
    return buildingBlockTypeFilter;
  }

  public void setBuildingBlockTypeFilter(String buildingBlockTypeFilter) {
    this.buildingBlockTypeFilter = buildingBlockTypeFilter;
  }

  /**
   * Implement readObject() to allow the deserialisation to init the transient field.
   * 
   * @throws ClassNotFoundException
   * @throws IOException
   */
  private void readObject(final ObjectInputStream is) throws IOException, ClassNotFoundException {
    if (is != null) {
      is.defaultReadObject();
    }
    searchDTO = new SearchDTO();
  }

  public void setNumberOfResults(int numberOfResults) {
    this.numberOfResults = numberOfResults;
  }

  public int getNumberOfResults() {
    return numberOfResults;
  }

  public String getRequestType() {
    return requestType;
  }

  public void setRequestType(String requestType) {
    this.requestType = requestType;
  }
}

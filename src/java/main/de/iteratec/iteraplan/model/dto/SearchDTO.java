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
package de.iteratec.iteraplan.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


/**
 * This DTO saves the Result-Rows of the search and provides methods for convenient output
 * rendering.
 */
public class SearchDTO {

  /**
   * Flag to indicate whether this DTO is returned as the result of a query.
   */
  private boolean noQueryExecuted = false;

  private int     numberOfAlternativeResults;

  private String  alternativeQueryString;

  public boolean isNoQueryExecuted() {
    return noQueryExecuted;
  }

  public void setNoQueryExecuted(boolean noQueryExcuted) {
    this.noQueryExecuted = noQueryExcuted;
  }

  /**
   * Multimap of SeachRowDTOs.
   */
  private Multimap<String, SearchRowDTO> searchMultiMap = ArrayListMultimap.create();

  public Multimap<String, SearchRowDTO> getSearchMultiMap() {
    return searchMultiMap;
  }

  public void setSearchMultiMap(Multimap<String, SearchRowDTO> searchMultiMap) {
    this.searchMultiMap = searchMultiMap;
  }

  /**
   * Returns a Map presentation of the Multimap. For each BuildingBlockElement an iterable
   * Collection of SearchRowDTOs will be returned.
   * 
   * @return map containing a collection of results grouped by the BBE
   */
  public Map<String, Collection<SearchRowDTO>> getSearchMap() {
    return searchMultiMap.asMap();
  }

  /**
   * @return sorted List containing Strings representing the BBE for which a result was found.
   */
  public List<String> getAvailableBBE() {
    List<String> availableBBE = new ArrayList<String>(searchMultiMap.keySet());
    Collections.sort(availableBBE);
    return availableBBE;
  }

  public int getNumberOfAlternativeResults() {
    return numberOfAlternativeResults;
  }

  public void setNumberOfAlternativeResults(int numberOfAlternativeResults) {
    this.numberOfAlternativeResults = numberOfAlternativeResults;
  }

  public String getAlternativeQueryString() {
    return alternativeQueryString;
  }

  public void setAlternativeQueryString(String alternativeQuerystring) {
    this.alternativeQueryString = alternativeQuerystring;
  }
}
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
package de.iteratec.iteraplan.model;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;


/**
 * Contains lists with the relevant building blocks for every type, which is available for the user, to show in the overview page.
 * @author sip
 */
public class OverviewElementLists implements Serializable {

  private static final long          serialVersionUID = -3042717531407983310L;

  /** All lists, which schould be shown on the overview page. */
  private final List<ElementList<?>> lists            = Lists.newArrayList();

  /**
   * Adds a new {@link ElementList}, to this object.
   */
  public <T extends BuildingBlock> void addElementList(ElementList<T> elementList) {
    lists.add(elementList);
  }

  public List<ElementList<?>> getLists() {
    return lists;
  }

  /**
   * Holds the header, iconUrl and some data of {@link BuildingBlock}s which belongs to this list.
   * @param <T>
   */
  public static class ElementList<T extends BuildingBlock> implements Serializable {

    private static final long serialVersionUID = 4539483450059807146L;

    private final String      htmlId;
    private final String      headerKey;
    private final int         totalNumberOfElements;
    private final List<T>     elements;

    /**
     * @param htmlId for this type, this is used for creating a link to the specific building block site.
     * @param headerKey The header for this list.
     * @param totalNumberOfElements The total number of elements in this list.
     * @param elements Some relevant elements for the content of this list.
     */
    public ElementList(String htmlId, String headerKey, int totalNumberOfElements, List<T> elements) {
      this.htmlId = htmlId;
      this.headerKey = headerKey;
      this.totalNumberOfElements = totalNumberOfElements;
      this.elements = elements;
    }

    public String getHtmlId() {
      return htmlId;
    }

    public String getHeaderKey() {
      return headerKey;
    }

    public int getTotalNumberOfElements() {
      return totalNumberOfElements;
    }

    public List<T> getElements() {
      return elements;
    }

  }

}

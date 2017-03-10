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
package de.iteratec.iteraplan.businesslogic.reports.staticquery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * This class defines a row in the result table of a static query.
 */
public class ResultRow implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 8003600352720316845L;
  /**
   * The list of {@link Object}s that make up a row in the result table of a static query.
   */
  private List<Object>      elements         = new ArrayList<Object>();

  public List<Object> getElements() {
    return elements;
  }

  public void setElements(List<Object> elements) {
    this.elements = elements;
  }

  public boolean hasSameContentAs(ResultRow other) {

    if (other != null && elements != null && !elements.isEmpty()) {

      List<Object> elementsOther = other.getElements();

      // both lists 1) not empty and 2) possess the same length

      if (elementsOther != null && !elementsOther.isEmpty() && elements.size() == elementsOther.size()) {

        for (int i = 0; i < elements.size(); i++) {

          // all content must be equal

          if (elements.get(i) == null) {
            if (other.getElements().get(i) != null) {
              return false;
            }
          }
          else if (!elements.get(i).equals(other.getElements().get(i))) {
            return false;
          }
        }
        return true;
      }
    }
    // umbrella for all else branches
    return false;
  }

}
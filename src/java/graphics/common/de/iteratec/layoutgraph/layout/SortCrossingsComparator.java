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
package de.iteratec.layoutgraph.layout;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.iteratec.layoutgraph.LayoutEdge;
import de.iteratec.layoutgraph.LayoutEdgeCoordinate;


/**
 * The class offers a possibility to compare two LayoutEdge. That will be helpful when we have to
 * sort a list of LayoutEdges. That class is useful for the OrthogonalEdgeLayout. That is why that
 * class is private for the package. The class is similar to IsLongerComparator. However, the two
 * classes use different characteristics to compare.
 */
class SortCrossingsComparator implements Comparator<LayoutEdge>, Serializable {

  private Map<LayoutEdgeCoordinate, Integer> linePoints = new HashMap<LayoutEdgeCoordinate, Integer>();

  /**
   * The constructor assigns linePoints. That object will be needed later for the comparison.
   * 
   * @param linePoints
   */
  public SortCrossingsComparator(Map<LayoutEdgeCoordinate, Integer> linePoints) {
    this.linePoints = linePoints;
  }

  /**
   * Compares two LayoutEdges.
   */
  public int compare(LayoutEdge edge1, LayoutEdge edge2) {
    int size1 = edge1.getLinePoints().size();
    int size2 = edge2.getLinePoints().size();
    int crossings1 = 0;
    int crossings2 = 0;
    for (LayoutEdgeCoordinate point : edge1.getLinePoints()) {
      if (linePoints.containsKey(point)) {
        crossings1++;
      }
    }
    for (LayoutEdgeCoordinate point : edge2.getLinePoints()) {
      if (linePoints.containsKey(point)) {
        crossings2++;
      }
    }
    if (crossings1 < crossings2) {
      return -1;
    }
    else if (crossings2 < crossings1) {
      return 1;
    }
    else if (size1 < size2) {
      return -1;
    }
    else if (size2 < size1) {
      return 1;
    }
    else {
      return 0;
    }
  }

}

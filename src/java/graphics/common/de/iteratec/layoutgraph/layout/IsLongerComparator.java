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

import de.iteratec.layoutgraph.LayoutEdge;
import de.iteratec.layoutgraph.LayoutGraph;
import de.iteratec.layoutgraph.LayoutNode;


/**
 * The class offers a possibility to compare two LayoutEdge. That will be helpful when we have to
 * sort a list of LayoutEdges. That class is useful for the OrthogonalEdgeLayout. That is why that
 * class is private for the package.
 * 
 * @author mma
 */

class IsLongerComparator implements Comparator<LayoutEdge>, Serializable {
  private LayoutGraph layoutGraph;

  /**
   * The constructor assigns layoutGraph. That object will be needed later for the comparison.
   * 
   * @param layoutGraph
   */
  public IsLongerComparator(LayoutGraph layoutGraph) {
    this.layoutGraph = layoutGraph;
  }

  /**
   * Initializes the internal nodes
   * 
   * @param edge1StartX
   * @param edge1EndX
   * @param edge1StartY
   * @param edge1EndY
   * @param edge2StartX
   * @param edge2EndX
   * @param edge2StartY
   * @param edge2EndY
   * @return int[]
   */
  private int[] initInternalNodes(double edge1StartX, double edge1EndX, double edge1StartY, double edge1EndY, double edge2StartX, double edge2EndX,
                                  double edge2StartY, double edge2EndY) {
    int[] innerNodes = new int[2];
    for (LayoutNode tempNode : layoutGraph.getNodes()) {

      double tempX = tempNode.getAbsoluteX();
      double tempY = tempNode.getAbsoluteY();

      if (isBetween(tempX, edge1StartX, edge1EndX) && isBetween(tempY, edge1StartY, edge1EndY)) {
        innerNodes[0]++;
      }

      if (isBetween(tempX, edge2StartX, edge2EndX) && isBetween(tempY, edge2StartY, edge2EndY)) {
        innerNodes[1]++;
      }
    }
    return innerNodes;
  }

  private boolean isBetween(double valueToCheck, double rangeStart, double rangeEnd) {
    return (valueToCheck > rangeStart && valueToCheck < rangeEnd);
  }

  /**
   * Compares internal nodes
   * 
   * @param innerNodes1
   * @param innerNodes2
   * @param edge1
   * @param edge2
   * @return int
   */
  private int compareInternalNodes(int innerNodes1, int innerNodes2, LayoutEdge edge1, LayoutEdge edge2) {
    if (innerNodes1 < innerNodes2) {
      return -1;
    }
    else if (innerNodes2 < innerNodes1) {
      return 1;
    }
    else {
      double length1 = Math.abs(edge1.getStartNode().getAbsoluteX() - edge1.getEndNode().getAbsoluteX())
          + Math.abs(edge1.getStartNode().getAbsoluteY() - edge1.getEndNode().getAbsoluteY());
      double length2 = Math.abs(edge2.getStartNode().getAbsoluteX() - edge2.getEndNode().getAbsoluteX())
          + Math.abs(edge2.getStartNode().getAbsoluteY() - edge2.getEndNode().getAbsoluteY());
      if (length1 < length2) {
        return -1;
      }
      else if (length2 < length1) {
        return 1;
      }
      else {
        return 0;
      }
    }
  }

  /**
   * Compares two LayoutEdges
   */
  public int compare(LayoutEdge edge1, LayoutEdge edge2) {

    int[] innerNodes = initInternalNodes(getStartX(edge1), getEndX(edge1), getStartY(edge1), getEndY(edge1), getStartX(edge2), getEndX(edge2),
        getStartY(edge2), getEndY(edge2));

    return compareInternalNodes(innerNodes[0], innerNodes[1], edge1, edge2);
  }

  private double getStartX(LayoutEdge edge) {
    return Math.min(edge.getStartNode().getAbsoluteX(), edge.getEndNode().getAbsoluteX());
  }

  private double getEndX(LayoutEdge edge) {
    return Math.max(edge.getStartNode().getAbsoluteX(), edge.getEndNode().getAbsoluteX());
  }

  private double getStartY(LayoutEdge edge) {
    return Math.min(edge.getStartNode().getAbsoluteY(), edge.getEndNode().getAbsoluteY());
  }

  private double getEndY(LayoutEdge edge) {
    return Math.max(edge.getStartNode().getAbsoluteX(), edge.getEndNode().getAbsoluteY());
  }

}

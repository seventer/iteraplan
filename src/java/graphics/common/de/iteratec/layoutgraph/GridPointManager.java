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
package de.iteratec.layoutgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *  Class to manage a grid of Layout Graph points
 *  
 *  - Enables to keep track of blocked points , points used as LinePoints (and therefore
 *   may not be used for crossings) and to keep track of already visited points
 *  
 *  - can be used to discretize planar objects
 *  
 *  
 * 
 */
public class GridPointManager {

  public static final int                    GLOBAL_STEP_SIZE = 10;
  private static final int                   VISIT_INCREMENT  = 1;
  private static final int                   SAMPLE_GRIDSIZE  = 5;

  private Map<LayoutEdgeCoordinate, Integer> linePoints;

  /**
   *  Map of non-crossable, i.e. blocked points
   */
  private Map<LayoutEdgeCoordinate, Integer> forbiddenMap;

  /**
   * List of non-crossable, i.e. blocked points
   */
  private List<LayoutEdgeCoordinate>         forbidden;

  private Map<LayoutEdgeCoordinate, Integer> visitedPoints;

  private double                             widthLimit;
  private double                             heightLimit;

  public GridPointManager(double widthLimit, double heightLimit) {
    this.widthLimit = widthLimit;
    this.heightLimit = heightLimit;
    linePoints = new HashMap<LayoutEdgeCoordinate, Integer>();
    forbidden = new ArrayList<LayoutEdgeCoordinate>();
    forbiddenMap = new HashMap<LayoutEdgeCoordinate, Integer>();
    visitedPoints = new HashMap<LayoutEdgeCoordinate, Integer>();
  }

  /**
   * Discretizes a the points of a list of nodes and adds it to the list of forbidden points.
   * 
   * @param nodes whose points are to be forbidden
   */
  public void addForbiddenPointsFromNodes(Set<LayoutNode> nodes) {

    double nodeHeight;
    double nodeWidth;
    double nodeXPos;
    double nodeyPos;

    /*  every graph node gets discretized by a grid with origin in the node's
     *  top left corner and a gridpoint-distance of SAMPLE_GRIDSIZE
     */
    for (LayoutNode currentLayoutNode : nodes) {

      nodeXPos = currentLayoutNode.getAbsoluteX();
      nodeyPos = currentLayoutNode.getAbsoluteY();
      nodeHeight = currentLayoutNode.getHeight();
      nodeWidth = currentLayoutNode.getWidth();
      for (int j = 0; j <= nodeWidth; j += SAMPLE_GRIDSIZE) {
        for (int k = 0; k <= nodeHeight; k += SAMPLE_GRIDSIZE) {
          LayoutEdgeCoordinate currentCoords = new LayoutEdgeCoordinate(nodeXPos + j, nodeyPos + k);
          forbiddenMap.put(currentCoords, Integer.valueOf(forbidden.size() - 1));
          forbidden.add(currentCoords);
        }
      }
    }

  }

  public void addForbiddenPoints(Collection<LayoutEdge> edges) {
    for (LayoutEdge tempEdge : edges) {
      addForbiddenPoints(tempEdge);
    }
  }

  public void addForbiddenPoints(LayoutEdge layoutEdgeTemp) {
    for (LayoutEdgeCoordinate point : layoutEdgeTemp.getEdgePath()) {
      LayoutEdgeCoordinate pointLoc = point.clone();
      addToForbidden(pointLoc);
    }
  }

  public void addLinePoints(LayoutEdge edge) {
    for (LayoutEdgeCoordinate point : edge.getLinePoints()) {
      LayoutEdgeCoordinate pointLoc = point.clone();
      linePoints.put(pointLoc, Integer.valueOf(((linePoints.size() - 1))));
    }
  }

  /**
   *  add a point to the list and map of forbidden (blocked) coordinates
   * @param forbiddenPoint  blocked point
   */
  public void addToForbidden(LayoutEdgeCoordinate forbiddenPoint) {
    forbidden.add(forbiddenPoint);
    forbiddenMap.put(forbiddenPoint, Integer.valueOf(forbidden.size() - 1));
  }

  public boolean isAlreadyLinePoint(LayoutEdgeCoordinate point) {
    return linePoints.containsKey(point);
  }

  public double getWidthLimit() {
    return widthLimit;
  }

  public double getHeightLimit() {
    return heightLimit;
  }

  public List<LayoutEdgeCoordinate> getForbiddenPoints() {
    return forbidden;
  }

  public int getNumberOfVisits(LayoutEdgeCoordinate point) {
    if (!visitedPoints.containsKey(point)) {
      return 0;
    }
    return visitedPoints.get(point).intValue();
  }

  public void increaseVisits(LayoutEdgeCoordinate visitedPoint) {

    Integer numberOfVisits = visitedPoints.get(visitedPoint);
    if (numberOfVisits != null) {
      visitedPoints.put(visitedPoint.clone(), Integer.valueOf(numberOfVisits.intValue() + VISIT_INCREMENT));
    }
    else {
      visitedPoints.put(visitedPoint.clone(), Integer.valueOf(VISIT_INCREMENT));
    }
  }

  public void clearVisitedPoints() {
    visitedPoints.clear();
  }

  /**
   * Counts the number of mapped points in a horizontal or vertical segment
   * 
   * if segment is not vertical or horizontal it returns 0
   * 
   * @param firstPoint
   * @param secondPoint
   * @return number of mapped points in segment / -1 if segment not axes-parallel
   */
  public int countMappedPointsInSegment(LayoutEdgeCoordinate firstPoint, LayoutEdgeCoordinate secondPoint) {
    if ((firstPoint.getCoord(LayoutEdgeCoordinate.HORIZONTAL) == secondPoint.getCoord(LayoutEdgeCoordinate.HORIZONTAL))) {
      return countMappedPointsInSegment(firstPoint, secondPoint, LayoutEdgeCoordinate.VERTICAL);
    }
    else if ((firstPoint.getCoord(LayoutEdgeCoordinate.VERTICAL) == secondPoint.getCoord(LayoutEdgeCoordinate.VERTICAL))) {
      return countMappedPointsInSegment(firstPoint, secondPoint, LayoutEdgeCoordinate.HORIZONTAL);
    }
    return -1;
  }

  /**
   *  Counts the number of mapped points in a horizontal or vertical segment.
   *  
   * 
   * @param startLoc
   *          The segment startPoint.
   * @param end
   *          The segment's endPoint (only segmentDimension is considered!)
   * @param segmentDimension
   *          dimension (horizontal/vertical) in which the searching takes place.
   * @return crossings found between startLoc and end
   */
  private int countMappedPointsInSegment(LayoutEdgeCoordinate startLoc, LayoutEdgeCoordinate end, int segmentDimension) {
    LayoutEdgeCoordinate start = startLoc.clone();
    int result = 0;
    if (start.getCoord(segmentDimension) < end.getCoord(segmentDimension)) {
      for (start.setCoord(segmentDimension, start.getCoord(segmentDimension) + GLOBAL_STEP_SIZE); start.getCoord(segmentDimension) < end
          .getCoord(segmentDimension); start.setCoord(segmentDimension, start.getCoord(segmentDimension) + GLOBAL_STEP_SIZE)) {
        if (forbiddenMap.containsKey(start)) {
          result++;
        }
      }
    }
    else {
      for (start.setCoord(segmentDimension, start.getCoord(segmentDimension) - GLOBAL_STEP_SIZE); start.getCoord(segmentDimension) > end
          .getCoord(segmentDimension); start.setCoord(segmentDimension, start.getCoord(segmentDimension) - GLOBAL_STEP_SIZE)) {
        if (forbiddenMap.containsKey(start)) {
          result++;
        }
      }
    }
    return result;
  }

  /**
   *  Discretizes the edges path, 
   *  i.e. each of the edge path segments is divided into its grid points of distance GLOBAL_STEP_SIZE which are written into
   *  the edge's linePoint list
   *  and then adds the information about non-crossable points and line points to the corresponding Collections.
   * 
   * 
   * @param edge
   *          The Edge object to be discretized.
   */
  public void discretizeEdgePathToLinePoints(LayoutEdge edge) {
    int n = edge.getEdgePath().size();
    // reinitialize edge's linePoints
    edge.setLinePoints(new ArrayList<LayoutEdgeCoordinate>());
    int dimension;
    for (int i = 0; i < n - 1; i++) {
      dimension = edge.getEdgePath().get(i).getX() - edge.getEdgePath().get(i + 1).getX() == 0 ? LayoutEdgeCoordinate.VERTICAL
          : LayoutEdgeCoordinate.HORIZONTAL;
      discretizeLineSegment(edge.getEdgePath().get(i), edge.getEdgePath().get(i + 1), edge.getLinePoints(), dimension);
    }

    addLinePoints(edge);
    addForbiddenPoints(edge);

  }

  /**
   * Discretizes a line segment into points with distance GLOBAL_STEP_SIZE
   * and adds the grid-points to a given list
   * 
   * start first the first gridpoint ( i.e. active dimension % GLOBAL_STEP_SIZE =  0)
   * on the line towards the endPoint and stops at the last gridPoint BEFORE the endPoint
   * 
   * @param startPoint
   *          Start Point
   * @param endPoint
   *          End Point
   * @param containerList
   *          The List to be written in
   * @param dim
   *          The dimension (horizontal/vertical) of the line segment
   */
  public void discretizeLineSegment(LayoutEdgeCoordinate startPoint, LayoutEdgeCoordinate endPoint, List<LayoutEdgeCoordinate> containerList, int dim) {
    LayoutEdgeCoordinate currentPointToBeAdded = startPoint.clone();
    /*
     *  direction of segment ( +1: ascending dim-value, -1: descending dim-value)
     */
    double segmentDirection = Math.signum(endPoint.getCoord(dim) - startPoint.getCoord(dim));

    // determine first grid point on line segment:
    if (segmentDirection > 0) {
      currentPointToBeAdded.setCoord(dim, Math.floor((currentPointToBeAdded.getCoord(dim) + GLOBAL_STEP_SIZE) / GLOBAL_STEP_SIZE) * GLOBAL_STEP_SIZE);
    }
    else {
      currentPointToBeAdded.setCoord(dim, Math.ceil((currentPointToBeAdded.getCoord(dim) - GLOBAL_STEP_SIZE) / GLOBAL_STEP_SIZE) * GLOBAL_STEP_SIZE);
    }

    // go one step further each step until endPoint reached and add Points
    while (segmentDirection * (endPoint.getCoord(dim) - currentPointToBeAdded.getCoord(dim)) >= 0) {
      containerList.add(currentPointToBeAdded.clone());
      currentPointToBeAdded.setCoord(dim, currentPointToBeAdded.getCoord(dim) + segmentDirection * GLOBAL_STEP_SIZE);
    }
  }

  /**
   * Discretizes a line segment into points with distance GLOBAL_STEP_SIZE
   * and adds the grid-points to a given map
   * 
   * 
   * start first the first gridpoint ( i.e. active dimension % GLOBAL_STEP_SIZE =  0)
   * on the line towards the endPoint and stops at the last gridPoint BEFORE the endPoint
   * 
   * 
   * @param startPoint
   *          The start
   * @param endPoint
   *          The end
   * @param containerMap
   *          The Map to be written in
   * @param dim
   *          The dimension (horizontal/vertical) in which the setting takes place.
   */
  public void discretizeLineSegment(LayoutEdgeCoordinate startPoint, LayoutEdgeCoordinate endPoint, Map<LayoutEdgeCoordinate, Integer> containerMap,
                                    int dim) {
    LayoutEdgeCoordinate currentPointToBeAdded = startPoint.clone();

    /*
     *  direction of segment ( +1: ascending dim-value, -1: descending dim-value)
     */
    double segmentDirection = Math.signum(endPoint.getCoord(dim) - startPoint.getCoord(dim));

    // determine first grid point on line segment:
    if (segmentDirection > 0) {
      currentPointToBeAdded.setCoord(dim, Math.floor((currentPointToBeAdded.getCoord(dim) + GLOBAL_STEP_SIZE) / GLOBAL_STEP_SIZE) * GLOBAL_STEP_SIZE);
    }
    else {
      currentPointToBeAdded.setCoord(dim, Math.ceil((currentPointToBeAdded.getCoord(dim) - GLOBAL_STEP_SIZE) / GLOBAL_STEP_SIZE) * GLOBAL_STEP_SIZE);
    }

    // go one step further each step until endPoint reached and add Points
    while (segmentDirection * (endPoint.getCoord(dim) - currentPointToBeAdded.getCoord(dim)) >= 0) {
      containerMap.put(currentPointToBeAdded.clone(), Integer.valueOf(((containerMap.size() - 1))));
      currentPointToBeAdded.setCoord(dim, currentPointToBeAdded.getCoord(dim) + segmentDirection * GLOBAL_STEP_SIZE);
    }

  }

  /**
   * adds a discretization of a vertical line segment (startY, endY] to the forbidden points
   *  rounds up (down) the segment's upper (lower) bound (in accordance with grid size) and
   *  returns the new upper (lower) bound if the end coordinate is greater (lower) than the start one.
   * 
   *  note that the starting point is NOT added to the list of forbidden points.
   *  
   * @param startY
   * @param endY
   * @param posX
   * @return  see method description
   */
  public double forbidVerticalSegment(double startY, double endY, double posX) {

    double newEndY = endY;
    int segmentDirection = -1;
    // round up or down according to position of endY
    if (endY > startY) {
      segmentDirection = 1;
      newEndY += GLOBAL_STEP_SIZE;
    }
    newEndY -= Math.IEEEremainder(endY, GLOBAL_STEP_SIZE);

    double currentPosY = startY + segmentDirection * SAMPLE_GRIDSIZE;
    while (segmentDirection * (newEndY - currentPosY) > 0) {
      LayoutEdgeCoordinate currentForbiddenPoint = new LayoutEdgeCoordinate(posX, currentPosY);
      addToForbidden(currentForbiddenPoint);
      currentPosY += segmentDirection * SAMPLE_GRIDSIZE;
    }

    return newEndY;
  }

  /**
   * adds a discretization of a horizontal line segment (startX, endX] to the forbidden points
   *  rounds up (down) the segment's upper (lower) bound (in accordance with grid size) and
   *  returns the new upper (lower) bound if the end coordinate is greater (lower) than the start one.
   * 
   *  note that the starting point is NOT added to the list of forbidden points.
   *  
   * @param startX
   * @param endX
   * @param posY
   * @return  see method description
   */
  public double forbidHorizontalSegment(double startX, double endX, double posY) {

    double newEndX = endX;
    int segmentDirection = -1;
    // round up or down according to position of endY
    if (endX > startX) {
      segmentDirection = 1;
      newEndX += GLOBAL_STEP_SIZE;
    }
    newEndX -= Math.IEEEremainder(endX, GLOBAL_STEP_SIZE);

    double currentPosX = startX + segmentDirection * SAMPLE_GRIDSIZE;
    while (segmentDirection * (newEndX - currentPosX) > 0) {
      LayoutEdgeCoordinate currentForbiddenPoint = new LayoutEdgeCoordinate(currentPosX, posY);
      addToForbidden(currentForbiddenPoint);
      currentPosX += segmentDirection * SAMPLE_GRIDSIZE;
    }

    return newEndX;
  }



}

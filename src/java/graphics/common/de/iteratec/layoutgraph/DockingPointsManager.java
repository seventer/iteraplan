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

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.List;

import de.iteratec.layoutgraph.LayoutNode.NodeSide;


/**
 *  Class to manage docking points in an orthogonal-edge-layout algorithm.
 * 
 *  - Initializes docking points of edges, sets their first segments
 *  - enables a postprocessing (tries to find shortcuts, better docking points)
 * 
 */
public class DockingPointsManager {

  private static final int DOCKDISTANCETOLERANCE = 80;

  private GridPointManager gridPointManager;

  public DockingPointsManager(GridPointManager gridPointManager) {
    this.gridPointManager = gridPointManager;
  }

  /**
   *  applies a shortcut of the given edge by setting the new given end-dockingPoint
   *  and deleting all points after the given preLastIndex
   * 
   * @param edge
   * @param preLastIndex
   * @param dockingPoint
   */
  private void applyEndDockShortcut(LayoutEdge edge, int preLastIndex, DockingPoint dockingPoint) {
    for (int j = edge.getEdgePath().size() - 1; j > preLastIndex; j--) {
      edge.getEdgePath().remove(edge.getEdgePath().size() - 1);
    }
    edge.addEdgePathPoint(dockingPoint.getDock());
    edge.setEndDock(dockingPoint);
  }

  /**
   * Finds end docking point if it's not already set.
   * 
   * @param layoutEdgeTemp
   * @param endNode
   * @param startDock
   */
  private void initEndDock(LayoutEdge layoutEdgeTemp, LayoutNode endNode, DockingPoint startDock) {
    if (layoutEdgeTemp.getEndDock() == null) {
      DockingPoint dockingPoint = endNode.findDockingPoint(startDock.getX(), startDock.getY());
      layoutEdgeTemp.setEndDock(dockingPoint);
    }
  }

  /**
   *  Searches for a possible shortcut of the edge by trying
   *   all edge points starting from startSearchIndex to the edge's next to the last point
   *   and determining their corresponding docking points.
   * 
   *   If the distance of edgePoint and found dockingPoint is < maxDistance
   *   and the segment [edgePoint, dockingPoint] is not blocked, the shortcut is applied,
   *   i.e. the edge points inbetween are deleted and the segment added.
   * 
   * 
   * @param edge
   * @param maxDistance
   * @param startSearchIndex
   */
  private void findEndDockShortcut(LayoutEdge edge, int maxDistance, int startSearchIndex) {

    boolean shortCutFound = false;
    for (int edgePointCandidateIndex = startSearchIndex; (edgePointCandidateIndex < edge.getEdgePath().size() - 2) && !shortCutFound; edgePointCandidateIndex++) {
      LayoutEdgeCoordinate currentEdgePointCandidate = edge.getEdgePath().get(edgePointCandidateIndex);
      DockingPoint dockingPoint = findDockingPoint(edge.getEndNode(), currentEdgePointCandidate);
      boolean dockingPointDistanceAdmissible = (LayoutEdgeCoordinate.uniformDistance(currentEdgePointCandidate, dockingPoint.getDock()) < maxDistance);
      if (dockingPointDistanceAdmissible) {
        boolean segmentAvailable = (gridPointManager.countMappedPointsInSegment(currentEdgePointCandidate, dockingPoint.getDock()) == 0);
        if (segmentAvailable) {
          applyEndDockShortcut(edge, edgePointCandidateIndex, dockingPoint);
          shortCutFound = true;
        }
      }

    }

  }

  /**
   * Finds start docking point if it's not already set.
   * 
   * @param layoutEdgeTemp
   * @param startNode
   * @param endNode
   */
  private void initStartDock(LayoutEdge layoutEdgeTemp, LayoutNode startNode, LayoutNode endNode) {
    if (layoutEdgeTemp.getStartDock() == null) {
      double absoluteX = endNode.getAbsoluteX();
      double absoluteY = endNode.getAbsoluteY();
      DockingPoint dockingPoint = startNode.findDockingPoint(absoluteX, absoluteY);
      layoutEdgeTemp.setStartDock(dockingPoint);
    }
  }

  /**
   *  applies a shortcut of the given edge by setting the new given start-dockingPoint
   *  and deleting all points up to the given secondPointIndex
   * 
   * @param edge
   * @param secondPointIndex
   * @param dockingPoint
   */
  private void applyStartDockShortcut(LayoutEdge edge, int secondPointIndex, DockingPoint dockingPoint) {
    for (int j = 0; j < secondPointIndex; j++) {
      edge.getEdgePath().remove(0);
    }
    edge.addEdgePathPoint(dockingPoint.getDock(), 0);
    edge.setStartDock(dockingPoint);
  }

  /**
   *   Searches for a possible shortcut  of the edge by trying
   *   all edge points starting from startSearchIndex to the edge's next to the first point
   *   and determining their corresponding docking points.
   * 
   *   If the distance of edgePoint and found dockingPoint is < maxDistance
   *   and the segment [edgePoint, dockingPoint] is not blocked, the shortcut is applied,
   *   i.e. the edge points inbetween are deleted and the segment added.
   * 
   * @param edge
   * @param maxDistance
   * @param startSearchIndex
   */
  private void findStartDockShortcut(LayoutEdge edge, int maxDistance, int startSearchIndex) {

    boolean shortCutFound = false;
    for (int edgePointCandidateIndex = startSearchIndex; (edgePointCandidateIndex > 1) && !shortCutFound; edgePointCandidateIndex--) {
      LayoutEdgeCoordinate currentEdgePointCandidate = edge.getEdgePath().get(edgePointCandidateIndex);
      DockingPoint dockingPoint = findDockingPoint(edge.getStartNode(), currentEdgePointCandidate);

      boolean dockingPointDistanceAdmissible = (LayoutEdgeCoordinate.uniformDistance(currentEdgePointCandidate, dockingPoint.getDock()) < maxDistance);
      if (dockingPointDistanceAdmissible) {
        boolean segmentAvailable = (gridPointManager.countMappedPointsInSegment(currentEdgePointCandidate, dockingPoint.getDock()) == 0);
        if (segmentAvailable) {
          applyStartDockShortcut(edge, edgePointCandidateIndex, dockingPoint);
          shortCutFound = true;
        }
      }
    }

  }

  /**
   *  Determines the coordinates where edge routing starts by defining
   *   the minimum distance of the first edge-corner from the docking point.
   * 
   * 
   *  Explanation:
   *  If several edges are docked at the same side of a node, they must bend at different distances
   *  to the nodes boundary.
   *  This distance is determined by the number of so far docked edges at the node and a first
   *  edge path coordinate is set.
   * 
   *  Example:
   *     ********
   *     *******o-----|
   *     ********     |
   *     *******o--|  |
   *     ********  |  |
   * 
   * 
   * @param dock
   * @param dockedEdges
   * @return   docking point coordinates
   */
  private Double determineRoutingStartCoordinates(DockingPoint dock, int dockedEdges) {

    // determine the next free position
    double[] direction = dock.getDirection();
    double dockX = dock.getX();
    double freePosX = dockX + direction[LayoutEdgeCoordinate.HORIZONTAL] * GridPointManager.GLOBAL_STEP_SIZE * dockedEdges;
    double dockY = dock.getY();
    double freePosY = dockY + direction[LayoutEdgeCoordinate.VERTICAL] * GridPointManager.GLOBAL_STEP_SIZE * dockedEdges;

    // adjust position to grid and forbid preceding segment
    if (direction[LayoutEdgeCoordinate.HORIZONTAL] != 0) {
      freePosX = gridPointManager.forbidHorizontalSegment(dockX, freePosX, freePosY);
    }
    else if (direction[LayoutEdgeCoordinate.VERTICAL] != 0) {
      freePosY = gridPointManager.forbidVerticalSegment(dockY, freePosY, freePosX);
    }

    return new Point2D.Double(freePosX, freePosY);
  }

  /**
   * Determine the docking point of each edge and set the first path segments in a way
   * several edges docked to the same node have the first bending at different distances from the boundary.
   * Therefore determines the first path segments of the edges from both of their docking points.
   * 
   */
  public void initializeDockingPoints(List<LayoutEdge> edges) {

    // first set docking points of child nodes
    findChildrenDockingPoints(edges);

    for (LayoutEdge currentEdge : edges) {

      LayoutNode startNode = currentEdge.getStartNode();
      LayoutNode endNode = currentEdge.getEndNode();

      /*
       *  find docking point, set distance of first edge corner to node big enough
       *  by determining the routeStartCoords (from there the layout algorithm can start routing)
       *
       */

      initStartDock(currentEdge, startNode, endNode);
      DockingPoint startDock = currentEdge.getStartDock();
      int numberOfStartDockedEdges = getNumberOfDockedEdges(startDock.getSide(), startNode);
      Point2D.Double routeStartCoords = determineRoutingStartCoordinates(startDock, numberOfStartDockedEdges);
      currentEdge.addEdgePathPoint(startDock.getX(), startDock.getY());
      currentEdge.addEdgePathPoint(routeStartCoords.getX(), routeStartCoords.getY());

      initEndDock(currentEdge, endNode, startDock);
      DockingPoint endDock = currentEdge.getEndDock();
      int numberOfEndDockedEdges = getNumberOfDockedEdges(endDock.getSide(), endNode);
      Point2D.Double coordEnd = determineRoutingStartCoordinates(endDock, numberOfEndDockedEdges);
      currentEdge.addEdgePathPoint(coordEnd.getX(), coordEnd.getY());
      currentEdge.addEdgePathPoint(endDock.getX(), endDock.getY());

    }
  }

  /**
   * Docking points of child nodes are set by finding docking points at their root nodes that
   * are closest to their centers.
   * 
   */
  private void findChildrenDockingPoints(List<LayoutEdge> edges) {

    int n = edges.size();
    for (int i = 0; i < n; i++) {
      LayoutEdge currentLayoutEdge = edges.get(i);

      LayoutNode edgeStartNode = currentLayoutEdge.getStartNode();
      LayoutNode edgeEndNode = currentLayoutEdge.getEndNode();

      // for both start- and end-node determine if it has a parent, if so find the root node
      // and  find a docking point at it

      if (edgeStartNode.getParent() != null) {
        double startNodeCenterX = edgeStartNode.getAbsoluteX() + edgeStartNode.getWidth() / 2;
        double startNodeCenterY = edgeStartNode.getAbsoluteY() + edgeStartNode.getHeight() / 2;
        LayoutNode rootNode = currentLayoutEdge.getStartNode();
        while (rootNode.getParent() != null) {
          rootNode = rootNode.getParent();
        }
        currentLayoutEdge.setStartDock(rootNode.findDockingPoint(startNodeCenterX, startNodeCenterY));
        currentLayoutEdge.setFurtherStartDock(edgeStartNode.findDockingPoint(currentLayoutEdge.getStartDock().getX(), currentLayoutEdge
            .getStartDock().getY()));
      }

      if (edgeEndNode.getParent() != null) {
        double endNodeCenterX = edgeEndNode.getAbsoluteX() + edgeEndNode.getWidth() / 2;
        double endNodeCenterY = edgeEndNode.getAbsoluteY() + edgeEndNode.getHeight() / 2;
        LayoutNode rootNode = currentLayoutEdge.getEndNode();
        while (rootNode.getParent() != null) {
          rootNode = rootNode.getParent();
        }
        currentLayoutEdge.setEndDock(rootNode.findDockingPoint(endNodeCenterX, endNodeCenterY));
        currentLayoutEdge.setFurtherEndDock(edgeEndNode
            .findDockingPoint(currentLayoutEdge.getEndDock().getX(), currentLayoutEdge.getEndDock().getY()));
      }
    }
  }

  /**
   *  Returns the docking point on a node that corresponds to a specified targetPoint
   *   (in fact the projection of the targetPoint on the node)
   * @param node
   * @param targetPoint
   * @return docking point
   */
  private DockingPoint findDockingPoint(LayoutNode node, LayoutEdgeCoordinate targetPoint) {
    NodeSide side = node.getRelativePosition(targetPoint);
    return node.findDockingPoint(targetPoint.getCoord(LayoutEdgeCoordinate.HORIZONTAL), targetPoint.getCoord(LayoutEdgeCoordinate.VERTICAL), side);
  }

  /**
   * Postprocessing for the dockings:
   * 
   *   - Extends the edge in case of nested nodes to furtherDockings
   *   - optimize Dockings via shortcuts (see method optimizeDockings)
   * 
   * 
   * @param edges
   *          The LayoutEdge Object to be optimized.
   */
  public void postprocessDockings(List<LayoutEdge> edges) {

    for (LayoutEdge layoutEdgeTemp : edges) {
      layoutEdgeTemp.applyFurtherDocks();
      optimizeDockings(layoutEdgeTemp);
    }
  }

  /**
   * Tries to find new start (end) docking points by
   *   trying shortcuts from the first (last) edge path points.
   * 
   *   A shortcut is applied when the distance of the edge path point
   *   is smaller than TOLERANCE and there are no forbidden points between
   *   the docking point candidate and the edge path point.
   * 
   * 
   * @param edge
   */
  private void optimizeDockings(LayoutEdge edge) {

    int maxStartPosition = edge.getEdgePath().size() - 2;
    int desiredStartPosition = Math.min(maxStartPosition, 4);

    // try to find new dock only for non-nested nodes
    if (edge.getStartNode().getParent() == null) {
      findStartDockShortcut(edge, DOCKDISTANCETOLERANCE, desiredStartPosition);
    }

    desiredStartPosition = Math.max(1, edge.getEdgePath().size() - 5);

    // try to find new dock only for non-nested nodes
    if (edge.getEndNode().getParent() == null) {
      findEndDockShortcut(edge, DOCKDISTANCETOLERANCE, desiredStartPosition);
    }
  }

  /**
   * 
   * @param side
   * @param node
   * @return number of docked edges at specified side of given node
   */
  private int getNumberOfDockedEdges(NodeSide side, LayoutNode node) {
    switch (side) {
      case NORTH:
        return node.getDockedNorth();
      case EAST:
        return node.getDockedEast();
      case SOUTH:
        return node.getDockedSouth();
      case WEST:
        return node.getDockedWest();
      default:
        return 1;
    }
  }

}

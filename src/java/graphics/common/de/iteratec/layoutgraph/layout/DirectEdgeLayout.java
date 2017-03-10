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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.iteratec.layoutgraph.DockingPoint;
import de.iteratec.layoutgraph.LayoutEdge;
import de.iteratec.layoutgraph.LayoutEdgeCoordinate;
import de.iteratec.layoutgraph.LayoutGraph;
import de.iteratec.layoutgraph.LayoutNode;
import de.iteratec.layoutgraph.LayoutNode.NodeSide;


public class DirectEdgeLayout extends AbstractEdgeLayout {

  private Map<LayoutNode, Map<NodeSide, List<LayoutEdge>>> startNodeToNodeSideToEdgesMap;
  private Map<LayoutNode, Map<NodeSide, List<LayoutEdge>>> endNodeToNodeSideToEdgesMap;

  public DirectEdgeLayout(LayoutGraph graph) {
    super(graph);
    startNodeToNodeSideToEdgesMap = new HashMap<LayoutNode, Map<NodeSide, List<LayoutEdge>>>();
    endNodeToNodeSideToEdgesMap = new HashMap<LayoutNode, Map<NodeSide, List<LayoutEdge>>>();
  }

  @Override
  public void initialize() {

    // Initialize docks
    for (LayoutNode node : getLayoutGraph().getNodes()) {
      initDocksForNode(node);
    }
  }

  private void initDocksForNode(LayoutNode node) {

    Map<NodeSide, List<LayoutEdge>> map = new HashMap<LayoutNode.NodeSide, List<LayoutEdge>>();
    startNodeToNodeSideToEdgesMap.put(node, map);
    map.put(NodeSide.NORTH, new LinkedList<LayoutEdge>());
    map.put(NodeSide.SOUTH, new LinkedList<LayoutEdge>());
    map.put(NodeSide.EAST, new LinkedList<LayoutEdge>());
    map.put(NodeSide.WEST, new LinkedList<LayoutEdge>());

    map = new HashMap<LayoutNode.NodeSide, List<LayoutEdge>>();
    endNodeToNodeSideToEdgesMap.put(node, map);
    map.put(NodeSide.NORTH, new LinkedList<LayoutEdge>());
    map.put(NodeSide.SOUTH, new LinkedList<LayoutEdge>());
    map.put(NodeSide.EAST, new LinkedList<LayoutEdge>());
    map.put(NodeSide.WEST, new LinkedList<LayoutEdge>());

    for (LayoutNode child : node.getChildren()) {
      initDocksForNode(child);
    }
  }

  @Override
  public void doLayout() {

    // Determine the node sides of the start and end nodes for every edge
    for (LayoutEdge edge : getLayoutGraph().getEdges()) {
      NodeSide[] nodeSides = retrieveNodeSides(edge);
      startNodeToNodeSideToEdgesMap.get(edge.getStartNode()).get(nodeSides[0]).add(edge);
      endNodeToNodeSideToEdgesMap.get(edge.getEndNode()).get(nodeSides[1]).add(edge);
    }

    // Distribute the resulting docking points onto the sides of the nodes.
    for (LayoutNode node : getLayoutGraph().getNodes()) {
      distributeDockingPoints(node);
    }

    // Add path points
    createPaths();

    // Add labels
    createLabels();

  }

  private void distributeDockingPoints(LayoutNode node) {

    distributeDockingPointsForSide(node, NodeSide.NORTH);
    distributeDockingPointsForSide(node, NodeSide.EAST);
    distributeDockingPointsForSide(node, NodeSide.SOUTH);
    distributeDockingPointsForSide(node, NodeSide.WEST);

    for (LayoutNode child : node.getChildren()) {
      distributeDockingPoints(child);
    }

  }

  private void distributeDockingPointsForSide(LayoutNode node, NodeSide side) {

    if (startNodeToNodeSideToEdgesMap.get(node).get(side).size() + endNodeToNodeSideToEdgesMap.get(node).get(side).size() == 0) {
      return;
    }

    double distanceBetweenDocks = getDistanceBetweenDocks(node, side);
    int count = 1;

    for (LayoutEdge edge : startNodeToNodeSideToEdgesMap.get(node).get(side)) {
      LayoutEdgeCoordinate dockPos = getCoordinatesForDock(side, edge.getStartNode().getAbsoluteX(), edge.getStartNode().getAbsoluteY(), edge
          .getStartNode().getWidth(), edge.getStartNode().getHeight(), distanceBetweenDocks * count);
      DockingPoint edgeStartDock = new DockingPoint(dockPos.getX(), dockPos.getY(), side);
      edge.setStartDock(edgeStartDock);
      count++;
    }
    for (LayoutEdge edge : endNodeToNodeSideToEdgesMap.get(node).get(side)) {
      LayoutEdgeCoordinate dockPos = getCoordinatesForDock(side, edge.getEndNode().getAbsoluteX(), edge.getEndNode().getAbsoluteY(), edge
          .getEndNode().getWidth(), edge.getEndNode().getHeight(), distanceBetweenDocks * count);
      DockingPoint edgeEndDock = new DockingPoint(dockPos.getX(), dockPos.getY(), side);
      edge.setEndDock(edgeEndDock);
      count++;
    }

  }

  private double getDistanceBetweenDocks(LayoutNode node, NodeSide nodeSide) {

    int numberOfEdges = startNodeToNodeSideToEdgesMap.get(node).get(nodeSide).size() + endNodeToNodeSideToEdgesMap.get(node).get(nodeSide).size();

    if (nodeSide.equals(NodeSide.NORTH) || nodeSide.equals(NodeSide.SOUTH)) {
      return node.getWidth() / numberOfEdges;
    }
    else {
      return node.getHeight() / numberOfEdges;
    }
  }

  private LayoutEdgeCoordinate getCoordinatesForDock(NodeSide side, double nodeX, double nodeY, double nodeWidth, double nodeHeight, double edgeOffset) {
    LayoutEdgeCoordinate result = new LayoutEdgeCoordinate(nodeX, nodeY);

    if (side.equals(NodeSide.NORTH) || side.equals(NodeSide.SOUTH)) {
      result.setX(result.getX() + edgeOffset);
      if (side.equals(NodeSide.SOUTH)) {
        result.setY(result.getY() + nodeHeight);
      }
    }
    else {
      result.setY(result.getY() + edgeOffset);
      if (side.equals(NodeSide.EAST)) {
        result.setX(result.getX() + nodeWidth);
      }
    }

    return result;
  }

  private NodeSide[] retrieveNodeSides(LayoutEdge edge) {

    // id.0 -> start node, id.1 -> end node
    NodeSide[] sides = new NodeSide[2];

    double startNodeX = edge.getStartNode().getAbsoluteX();
    double startNodeY = edge.getStartNode().getAbsoluteY();

    double endNodeX = edge.getEndNode().getAbsoluteX();
    double endNodeY = edge.getEndNode().getAbsoluteY();

    if (Math.abs(startNodeX - endNodeX) >= Math.abs(startNodeY - endNodeY)) {
      // Attach into east-west direction
      if (startNodeX <= endNodeX) {
        // startNode: east side; endNode: west side
        sides[0] = NodeSide.EAST;
        sides[1] = NodeSide.WEST;
      }
      else {
        // startNode: west; endNode: east
        sides[0] = NodeSide.WEST;
        sides[1] = NodeSide.EAST;
      }
    }
    else {
      // Attach into north-south direction
      if (startNodeY <= endNodeY) {
        // startNode: south; endNode: north
        sides[0] = NodeSide.SOUTH;
        sides[1] = NodeSide.NORTH;
      }
      else {
        // startNode: north; endNode: south
        sides[0] = NodeSide.NORTH;
        sides[1] = NodeSide.SOUTH;
      }
    }
    return sides;
  }

  private void createPaths() {

    for (LayoutEdge edge : getLayoutGraph().getEdges()) {

      // Add start point
      edge.addEdgePathPoint(edge.getStartDock().getX(), edge.getStartDock().getY());

      // Create two intermediate points, so that the edges hit the shape surface orthogonally.
      LayoutEdgeCoordinate orthogonalStart = getCoordinatesForConnectionPoint(edge.getStartDock().getX(), edge.getStartDock().getY(), edge
          .getStartDock().getSide());
      edge.addEdgePathPoint(orthogonalStart);

      LayoutEdgeCoordinate orthogonalEnd = getCoordinatesForConnectionPoint(edge.getEndDock().getX(), edge.getEndDock().getY(), edge.getEndDock()
          .getSide());

      edge.addEdgePathPoint(orthogonalEnd);

      // Add end point
      edge.addEdgePathPoint(edge.getEndDock().getX(), edge.getEndDock().getY());
    }
  }

  private LayoutEdgeCoordinate getCoordinatesForConnectionPoint(double hitPointX, double hitPointY, NodeSide nodeSide) {

    LayoutEdgeCoordinate result = new LayoutEdgeCoordinate(hitPointX, hitPointY);

    // Create a random disposition between 10 and 30 units.
    double randomDisposition = 20 + Math.random() * 40;

    if (nodeSide.equals(NodeSide.NORTH)) {
      result.setY(result.getY() - randomDisposition);
    }
    else if (nodeSide.equals(NodeSide.EAST)) {
      result.setX(result.getX() + randomDisposition);
    }
    else if (nodeSide.equals(NodeSide.SOUTH)) {
      result.setY(result.getY() + randomDisposition);
    }
    else {
      result.setX(result.getX() - randomDisposition);
    }
    return result;
  }

  private void createLabels() {
    for (LayoutEdge edge : getLayoutGraph().getEdges()) {
      edge.setLinePoints(edge.getEdgePath());
    }
  }
}

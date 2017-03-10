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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.layoutgraph.layout.OrthogonalEdgeLayout;


/**
 * This class is a wrapper of a vertex for an arbitrary object to make it possible to extend the
 * Jung framework with needed additional properties.
 */

public class LayoutNode {

  private IdentityEntity                 nodeElement;

  private LayoutNode                     parent;
  private Set<LayoutNode>                children;

  private List<DockingPoint>             dockingPoints;
  private List<DockingPoint>             dockedDockingPoints;

  private Set<LayoutEdge>                edges;
  private Map<NodeSide, Set<LayoutEdge>> nodeSideToEdgesMap;

  private Integer                        representedId;

  private Map<String, String>            customProperties;

  private double                         xCor;
  private double                         yCor;

  private double                         width;
  private double                         height;

  private int                            dockedCount;
  private int                            dockedNorth;
  private int                            dockedEast;
  private int                            dockedSouth;
  private int                            dockedWest;

  public int getDockedNorth() {
    return dockedNorth;
  }

  public int getDockedEast() {
    return dockedEast;
  }

  public int getDockedSouth() {
    return dockedSouth;
  }

  public int getDockedWest() {
    return dockedWest;
  }

  /**
   * Sets dockingPoints used for the Orthogonal Edge Routing onto the boundary of the node.
   */
  public void initializeDockingPoints() {
    double horizontalEnd = this.getAbsoluteX() + this.width;
    double verticalEnd = this.getAbsoluteY() + this.height;
    for (double i = this.getAbsoluteX(); i <= horizontalEnd; i += GridPointManager.GLOBAL_STEP_SIZE) {
      dockingPoints.add(new DockingPoint(i, this.getAbsoluteY(), NodeSide.NORTH));
      dockingPoints.add(new DockingPoint(i, this.getAbsoluteY() + this.height, NodeSide.SOUTH));
    }
    for (double i = this.getAbsoluteY(); i <= verticalEnd; i += GridPointManager.GLOBAL_STEP_SIZE) {
      dockingPoints.add(new DockingPoint(this.getAbsoluteX(), i, NodeSide.WEST));
      dockingPoints.add(new DockingPoint(this.getAbsoluteX() + this.width, i, NodeSide.EAST));
    }
  }

  /**
   * Sets the input dockingPoint as docked on this node.
   * @param point The input dockingPoint
   */
  public void dock(DockingPoint point) {
    this.dockedDockingPoints.add(point);
    point.setDocked(true);
    switch (point.getSide()) {
      case NORTH:
        dockedNorth++;
        break;
      case EAST:
        dockedEast++;
        break;
      case SOUTH:
        dockedSouth++;
        break;
      case WEST:
        dockedWest++;
        break;
      default:
    }
    dockedCount++;
  }

  /**
   * Removes the input dockingPoint from the list of docked point on this node.
   * @param point The input dockingPoint
   */
  public void unDock(DockingPoint point) {
    this.dockedDockingPoints.remove(point);
    point.setDocked(false);
    switch (point.getSide()) {
      case NORTH:
        dockedNorth--;
        break;
      case EAST:
        dockedEast--;
        break;
      case SOUTH:
        dockedSouth--;
        break;
      case WEST:
        dockedWest--;
        break;
      default:
    }
    dockedCount--;
  }

  /**
   * Finds the nearest dockingPoint relative to the input coordinates.
   * 
   * @param x
   *          The x Coordinate.
   * @param y
   *          The y Coordinate.
   * @return The nearest dockingPoint found on this node.
   */
  public DockingPoint findDockingPoint(double x, double y) {
    if (this.dockingPoints.size() == 0) {
      this.initializeDockingPoints();
    }
    NodeSide side = this.getRelativePosition(new LayoutEdgeCoordinate(x, y));
    if (side != null) {
      return findDockingPoint(x, y, side);
    }
    int docks = dockingPoints.size();
    double distance = Double.MAX_VALUE;
    double tempDist;
    int nearestIndex = 0;
    for (int i = 0; i < docks; i++) {
      tempDist = Math.abs(x - dockingPoints.get(i).getX()) + Math.abs(y - dockingPoints.get(i).getY());
      if ((tempDist < distance) && !dockingPoints.get(i).isDocked()) {
        nearestIndex = i;
        distance = tempDist;
      }
    }
    return dockingPoints.get(nearestIndex);
  }

  /**
   * Finds the nearest dockingPoint relative to the input coordinates with restriction to a specific
   * side.
   * 
   * @param x
   *          The x Coordinate.
   * @param y
   *          The y Coordinate.
   * @param side
   *          The side restriction
   * @return The nearest dockingPoint found on this node.
   */
  public DockingPoint findDockingPoint(double x, double y, NodeSide side) {
    if (this.dockingPoints.size() == 0) {
      this.initializeDockingPoints();
    }

    int docks = dockingPoints.size();
    double distance = Double.MAX_VALUE;
    double tempDist;
    int nearestIndex = 0;
    for (int i = 0; i < docks; i++) {
      tempDist = Math.abs(x - dockingPoints.get(i).getX()) + Math.abs(y - dockingPoints.get(i).getY());
      if ((tempDist < distance) && !dockingPoints.get(i).isDocked() && dockingPoints.get(i).getSide() == side) {
        nearestIndex = i;
        distance = tempDist;
      }
    }
    return dockingPoints.get(nearestIndex);
  }

  public LayoutNode() {
    this.representedId = null;
    this.initializeNode();
  }

  public LayoutNode(Integer representedId) {
    this.representedId = representedId;
    this.initializeNode();
  }

  private void initializeNode() {
    dockingPoints = new ArrayList<DockingPoint>();
    dockedDockingPoints = new ArrayList<DockingPoint>();
    this.parent = null;
    this.children = new HashSet<LayoutNode>();
    this.customProperties = new HashMap<String, String>();
    this.xCor = 0;
    this.yCor = 0;
    this.width = 0;
    this.height = 0;
    this.edges = new HashSet<LayoutEdge>();
    this.nodeSideToEdgesMap = new HashMap<NodeSide, Set<LayoutEdge>>();
    for (NodeSide side : NodeSide.values()) {
      nodeSideToEdgesMap.put(side, new HashSet<LayoutEdge>());
    }
  }

  public int getDockedCount() {
    return dockedCount;
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  public double[] getDimensions() {
    return new double[] { width, height };
  }

  public double getX() {
    return xCor;
  }

  public void setX(double cor) {
    xCor = cor;
  }

  public double getY() {
    return yCor;
  }

  public double[] getAbsoluteArray() {
    return new double[] { this.getAbsoluteX(), this.getAbsoluteY() };
  }

  public void setY(double cor) {
    yCor = cor;
  }

  public LayoutNode getParent() {
    return parent;
  }

  public void setParent(LayoutNode parent) {
    this.parent = parent;
  }

  public Set<LayoutNode> getChildren() {
    return children;
  }

  public void setChildren(Set<LayoutNode> children) {
    this.children = children;
  }

  public Integer getRepresentedId() {
    return representedId;
  }

  public void setRepresentedId(Integer representedId) {
    this.representedId = representedId;
  }

  public LayoutNode getRootParent() {
    LayoutNode tmpNode = this;
    while (tmpNode.getParent() != null) {
      tmpNode = tmpNode.getParent();
    }
    return tmpNode;
  }

  public double getAbsoluteX() {
    double absX = xCor;
    LayoutNode tmpNode = this;
    while (tmpNode.getParent() != null) {
      tmpNode = tmpNode.getParent();
      absX += tmpNode.getX();
    }
    return absX;
  }

  public double getAbsoluteY() {
    double absY = yCor;
    LayoutNode tmpNode = this;
    while (tmpNode.getParent() != null) {
      tmpNode = tmpNode.getParent();
      absY += tmpNode.getY();
    }
    return absY;
  }

  public void addCustomProperty(String propertyIdentifier, String propertyValue) {
    this.customProperties.put(propertyIdentifier, propertyValue);
  }

  public String getCustomProperty(String propertyIdentifier) {
    return this.customProperties.get(propertyIdentifier);
  }

  public IdentityEntity getNodeElement() {
    return nodeElement;
  }

  public void setNodeElement(IdentityEntity element) {
    this.nodeElement = element;
  }

  public void addChild(LayoutNode childNode) {
    this.children.add(childNode);
  }

  public Set<LayoutEdge> getEdges() {
    return edges;
  }

  public void setEdges(Set<LayoutEdge> edges) {
    this.edges = edges;
  }

  public void addEdge(LayoutEdge edge) {
    this.edges.add(edge);
  }

  public Set<LayoutEdge> getEdgesForSide(NodeSide side) {
    return nodeSideToEdgesMap.get(side);
  }

  public void addEdgeForSide(LayoutEdge edge, NodeSide side) {
    this.nodeSideToEdgesMap.get(side).add(edge);
  }

  /**
   * Returns the nearest side of the node relative to the input point.
   * 
   * @param pOI
   *          The input point
   * @return NORTH, EAST, SOUTH, WEST: The nearest NodeSide. null if the point does not lie between
   *         the borders of one side.
   */
  public NodeSide getRelativePosition(LayoutEdgeCoordinate pOI) {
    if ((pOI.getCoord(OrthogonalEdgeLayout.HORIZONTAL) <= this.getAbsoluteX() + this.getWidth())
        && (pOI.getCoord(OrthogonalEdgeLayout.HORIZONTAL) >= this.getAbsoluteX())) {
      if (pOI.getCoord(OrthogonalEdgeLayout.VERTICAL) <= this.getAbsoluteY()) {
        return NodeSide.NORTH;
      }
      else if (pOI.getCoord(OrthogonalEdgeLayout.VERTICAL) >= this.getAbsoluteY() + this.getHeight()) {
        return NodeSide.SOUTH;
      }
    }
    else if (pOI.getCoord(OrthogonalEdgeLayout.VERTICAL) <= this.getAbsoluteY() + this.getHeight()
        && (pOI.getCoord(OrthogonalEdgeLayout.VERTICAL) >= this.getAbsoluteY())) {
      if (pOI.getCoord(OrthogonalEdgeLayout.HORIZONTAL) <= this.getAbsoluteX()) {
        return NodeSide.WEST;
      }
      else if (pOI.getCoord(OrthogonalEdgeLayout.HORIZONTAL) >= this.getAbsoluteX() + this.getWidth()) {
        return NodeSide.EAST;
      }
    }
    return null;
  }

  /**
   * Specifies the sides of a node, at which edges can be attached. We suppose that nodes are
   * rectangular.
   */
  public enum NodeSide {
    NORTH, EAST, SOUTH, WEST;
  }

}

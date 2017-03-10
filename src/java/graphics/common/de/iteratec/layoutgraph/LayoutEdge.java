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
import java.util.List;

import de.iteratec.iteraplan.model.interfaces.IdentityEntity;


/**
 * This class is a wrapper of an edge for an arbitrary Object to make it possible to extend the Jung
 * framework with needed additional properties, without changing the iteraplan model.
 */
public class LayoutEdge {

  private LayoutNode                 startNode;
  private LayoutNode                 endNode;

  private Direction                  edgeDirection;
  private String                     edgeLabel;

  private DockingPoint               startDock;
  private DockingPoint               endDock;
  private DockingPoint               furtherStartDock;
  private DockingPoint               furtherEndDock;
  private IdentityEntity             edgeElement;

  private List<LayoutEdgeCoordinate> edgePath;
  private List<LayoutEdgeCoordinate> linePoints;

  public void setLinePoints(List<LayoutEdgeCoordinate> linePoints) {
    this.linePoints = linePoints;
  }

  public List<LayoutEdgeCoordinate> getLinePoints() {
    return this.linePoints;
  }

  public void setStartDock(DockingPoint startDock) {
    if (this.startDock != null) {
      this.startNode.unDock(this.startDock);
    }
    this.startDock = startDock;
    this.startNode.dock(startDock);
  }

  public void setEndDock(DockingPoint endDock) {
    if (this.endDock != null) {
      this.endNode.unDock(this.endDock);
    }
    this.endDock = endDock;
    this.endNode.dock(endDock);
  }

  public DockingPoint getStartDock() {
    return startDock;
  }

  public DockingPoint getEndDock() {
    return endDock;
  }

  public LayoutEdge() {
    this.edgePath = new ArrayList<LayoutEdgeCoordinate>();
    this.linePoints = new ArrayList<LayoutEdgeCoordinate>();
  }

  public IdentityEntity getEdgeElement() {
    return edgeElement;
  }

  public void setEdgeElement(IdentityEntity element) {
    this.edgeElement = element;
  }

  public List<LayoutEdgeCoordinate> getEdgePath() {
    return edgePath;
  }

  public void setEdgePath(List<LayoutEdgeCoordinate> edgePath) {
    this.edgePath = edgePath;
  }

  public void addEdgePathPoint(LayoutEdgeCoordinate point) {
    this.edgePath.add(point.clone());
  }

  public void addEdgePathPoint(double x, double y) {
    this.addEdgePathPoint(new LayoutEdgeCoordinate(x, y));
  }

  public void addEdgePathPoint(LayoutEdgeCoordinate point, int position) {
    this.edgePath.add(position, point.clone());
  }

  public LayoutNode getStartNode() {
    return startNode;
  }

  public void setStartNode(LayoutNode startNode) {
    this.startNode = startNode;
  }

  public void setStartNode(double x, double y) {
    this.startNode.setX(x);
    this.startNode.setY(y);
  }

  public LayoutNode getEndNode() {
    return endNode;
  }

  public void setEndNode(LayoutNode endNode) {
    this.endNode = endNode;
  }

  public void setEndNode(double x, double y) {
    this.endNode.setX(x);
    this.endNode.setY(y);
  }

  public Direction getEdgeDirection() {
    return edgeDirection;
  }

  public void setEdgeDirection(Direction edgeDirection) {
    this.edgeDirection = edgeDirection;
  }

  public String getEdgeLabel() {
    return edgeLabel;
  }

  public void setEdgeLabel(String edgeLabel) {
    this.edgeLabel = edgeLabel;
  }

  public DockingPoint getFurtherStartDock() {
    return furtherStartDock;
  }

  public void setFurtherStartDock(DockingPoint furtherStartDock) {
    this.furtherStartDock = furtherStartDock;
  }

  public DockingPoint getFurtherEndDock() {
    return furtherEndDock;
  }

  public void setFurtherEndDock(DockingPoint furtherEndDock) {
    this.furtherEndDock = furtherEndDock;
  }
 
  
  /**
   *   Extends the edges if further docking points (i.e. docking points of nested symbols) exist
   */
  public void applyFurtherDocks() {
    if (this.furtherStartDock != null) {
      this.startDock = this.furtherStartDock;
      this.addEdgePathPoint(this.startDock.getDock(), 0);
    }
    if (this.furtherEndDock != null) {
      this.endDock = this.furtherEndDock;
      this.addEdgePathPoint(this.endDock.getDock());
    }
  }

  /**
   * An enumeration representing the possible directions of an edge.
   */
  public enum Direction {
    NO_DIRECTION, START_TO_END, END_TO_START, BIDIRECTIONAL;
  }
}

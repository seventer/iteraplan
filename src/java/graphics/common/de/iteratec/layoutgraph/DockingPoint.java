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

import de.iteratec.layoutgraph.LayoutNode.NodeSide;


/**
 * A dockingPoint Object represents a point on the boundary of a LayoutNode used for the Orthogonal Edge Routing.
 * 
 * @author Andreas Umbreit, iteratec GmbH
 */
public class DockingPoint {

  public DockingPoint(double xCor, double yCor, NodeSide side) {
    this.dock = new LayoutEdgeCoordinate(xCor, yCor);
    this.docked = false;
    this.side = side;
  }

  public DockingPoint(LayoutEdgeCoordinate cor, NodeSide side) {
    this.dock = cor.clone();
    this.docked = false;
    this.side = side;
  }

  private LayoutEdgeCoordinate dock;
  private boolean              docked;
  private NodeSide             side;

  public NodeSide getSide() {
    return side;
  }

  public double getX() {
    return this.dock.getX();
  }

  public double getY() {
    return this.dock.getY();
  }

  public void setDocked(boolean docked) {
    this.docked = docked;
  }

  public boolean isDocked() {
    return docked;
  }

  public double getCoord(int dimension) {
    if (dimension == 0) {
      return this.dock.getX();
    }
    else {
      return this.dock.getY();
    }
  }

  public LayoutEdgeCoordinate getDock() {
    return this.dock;
  }

  public double[] getDirection() {
    switch (this.side) {
      case NORTH:
        return new double[] { 0, -1 };
      case EAST:
        return new double[] { 1, 0 };
      case SOUTH:
        return new double[] { 0, 1 };
      case WEST:
        return new double[] { -1, 0 };
      default:
        return null;
    }
  }

}

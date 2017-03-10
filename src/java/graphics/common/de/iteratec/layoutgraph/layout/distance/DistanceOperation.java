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
package de.iteratec.layoutgraph.layout.distance;

import de.iteratec.layoutgraph.LayoutNode;


/**
 * Implements the calculation of the distance between two nodes. The return value has three parts:
 * the distance according to whatever metric is implemented, plus the horizontal and vertical
 * distances. All three can be different depending on implementation.
 */
public interface DistanceOperation {
  /**
   * Groups the three parts of the return value.
   */
  class DistResult {
    private double dist;
    private double dx;
    private double dy;

    public void setDx(double dx) {
      this.dx = dx;
    }

    public double getDx() {
      return dx;
    }

    public void setDy(double dy) {
      this.dy = dy;
    }

    public double getDy() {
      return dy;
    }

    public void setDist(double dist) {
      this.dist = dist;
    }

    public double getDist() {
      return dist;
    }
  }

  /**
   * Calculates the distances between the two nodes.
   */
  DistanceOperation.DistResult distance(LayoutNode node1, LayoutNode node2);

  /**
   * Returns a name of the operation used for user interfaces and debugging purposes.
   */
  String getName();
}

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

/**
 * A container class used for the representation of points by an x and a y coordinate.
 */
public class LayoutEdgeCoordinate implements Cloneable {

  private double           x;
  private double           y;
  public static final int HORIZONTAL = 0;
  public static final int VERTICAL   = 1;

  public LayoutEdgeCoordinate() {
    this.x = 0;
    this.y = 0;
  }

  public LayoutEdgeCoordinate(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public LayoutEdgeCoordinate(double[] coordinate) {
    double[] coordinateLocal = coordinate.clone();
    this.x = coordinateLocal[HORIZONTAL];
    this.y = coordinateLocal[VERTICAL];
  }

  public double getCoord(int dimension) {
    if (dimension == HORIZONTAL) {
      return this.x;
    }
    else {
      return this.y;
    }
  }

  public void setCoord(int dimension, double cor) {
    if (dimension == HORIZONTAL) {
      this.x = cor;
    }
    else {
      this.y = cor;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    LayoutEdgeCoordinate other = (LayoutEdgeCoordinate) obj;
    if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
      return false;
    }
    if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  @SuppressWarnings("PMD")
  public LayoutEdgeCoordinate clone() {
    try {
      // no need to care for simple member variables, since these are handled by Object#clone
      return (LayoutEdgeCoordinate) super.clone();
    } catch (CloneNotSupportedException e) {
      // this shouldn't happen, since we are Cloneable
      throw new InternalError();
    }
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getX() {
    return x;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getY() {
    return y;
  }

  /**
  * 
  *  Distance of two points induced by uniform norm,
  *   i.e.
  *    d(a,b) = max( |a.x - b.x|, |a.y - b.y|, 
  * 
  * @param a
  *          The one point
  * @param b
  *          The other point
  * @return distance induced by uniform norm
  */
  public static double uniformDistance(LayoutEdgeCoordinate a, LayoutEdgeCoordinate b) {
    return Math.max(Math.abs(a.getCoord(HORIZONTAL) - b.getCoord(HORIZONTAL)), Math.abs(a.getCoord(VERTICAL) - b.getCoord(VERTICAL)));
  }

}

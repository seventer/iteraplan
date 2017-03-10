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
package de.iteratec.iteraplan.businesslogic.exchange.common;

import java.io.Serializable;


/**
 * represents the coordinates in a 2-dimensional coordinate system
 * 
 * @author Niko Baehr (iteratec GmbH)
 * @version $Id:$
 */
public class Coordinates implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 4376333738375485093L;
  private double            x;
  private double            y;

  public Coordinates(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public Coordinates(Coordinates pointer) {
    this.x = pointer.x;
    this.y = pointer.y;
  }

  public Coordinates(Coordinates pointer, double margeX, double margeY) {
    this.x = pointer.x + margeX;
    this.y = pointer.y + margeY;
  }

  /**
   * @return Returns the x.
   */
  public double getX() {
    return x;
  }

  /**
   * @param x
   *          The x to set.
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * @return Returns the y.
   */
  public double getY() {
    return y;
  }

  /**
   * @param y
   *          The y to set.
   */
  public void setY(double y) {
    this.y = y;
  }

  public void incX(double margeX) {
    this.x += margeX;
  }

  public void incY(double margeY) {
    this.y += margeY;
  }

  public void moveTo(double xPos, double yPos) {
    this.x = xPos;
    this.y = yPos;
  }
}

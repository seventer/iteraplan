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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting;

import java.io.Serializable;


/**
 * Provides functionality for "ordering buttons" like at the ClusterDiagram's Dimensions table
 */
public abstract class GraphicalExportOptionsWithOrderedList extends GraphicalExportBaseOptions implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 4547827598769076365L;

  /**
   * Specifies the direction in which a chosen element should be moved.
   */
  private Movement          move             = Movement.HOLD_POSITION;
  /**
   * Specifies the element to be moved. (specifics depend on implementation of {@link #refreshOrder()})
   */
  private int               movedItem        = -1;

  public GraphicalExportOptionsWithOrderedList() {
    super();
  }

  public abstract void refreshOrder();

  public Movement getMoveType() {
    return move;
  }

  public int getMove() {
    return move.toInteger();
  }

  public void setMove(int move) {
    this.move = Movement.getMovement(move);
  }

  public void setMovedItem(int movedItem) {
    this.movedItem = movedItem;
  }

  public int getMovedItem() {
    return movedItem;
  }

  protected enum Movement {
    TOP(1), UP(2), DOWN(3), BOTTOM(4), HOLD_POSITION(-1);
    private final int value;

    private Movement(int value) {
      this.value = value;
    }

    public int toInteger() {
      return value;
    }

    private static Movement getMovement(int intValue) {
      switch (intValue) {
        case 1:
          return TOP;
        case 2:
          return UP;
        case 3:
          return DOWN;
        case 4:
          return BOTTOM;
        default:
          return HOLD_POSITION;
      }
    }
  }

}
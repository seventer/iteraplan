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
package de.iteratec.iteraplan.model;

/**
 * Represents the direction.
 */
public enum Direction {
  NO_DIRECTION("-"), FIRST_TO_SECOND("->"), SECOND_TO_FIRST("<-"), BOTH_DIRECTIONS("<->");

  /**The string representation of the direction. */
  private String value;

  private Direction(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return this.getValue();
  }

  /**
   * @return <code>true</code> if the direction is from the first to the second element
   */
  public boolean isFirstToSecond() {
    return FIRST_TO_SECOND.equals(this) || BOTH_DIRECTIONS.equals(this);
  }

  /**
   * @return <code>true</code> if the direction is from the second to the first element
   */
  public boolean isSecondToFirst() {
    return SECOND_TO_FIRST.equals(this) || BOTH_DIRECTIONS.equals(this);
  }
  
  /**
   * Returns the direction for the specified string representation. If no 
   * {@link Direction} can be found, this method returns {@code null}.
   * 
   * @param directionValue the direction string representation
   * @return the {@link Direction} instance or {@code null}, If no {@link Direction} can be found
   */
  public static Direction getDirectionForValue(String directionValue) {
    Direction[] values = values();
    for (Direction direction : values) {
      if (direction.getValue().equals(directionValue)) {
        return direction;
      }
    }
    
    return null;
  }
}

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
package de.iteratec.iteraplan.businesslogic.reports.query.node;

/**
 * Enumeration for comparison literals used in dynamic queries.
 */
public enum Comparator {

  IS("is"), LIKE("like"), NOT_LIKE("not like"), LT("<"), LEQ("<="), EQ("="), NEQ("!="), GT(">"), GEQ(">="), ANY_ASSIGNMENT("any assignment"), NO_ASSIGNMENT(
      "no assignment");

  private final String comparatorType;

  private Comparator(String comparatorType) {
    this.comparatorType = comparatorType;
  }

  /**
   * Returns the current string value stored in the Enum.
   *  
   * @return
   *   See method description.
   */
  public String toString() {
    return comparatorType;
  }

  /**
   * Returns the comparator instance for the specified {@code typeName}. If the {@link Comparator} for the specified
   * name will not be found, {@code null} will be returned.
   * 
   * @param typeName the comparator type name
   * @return the comparator instance or {@code null}
   */
  public static Comparator fromString(String typeName) {
    if (typeName != null) {
      for (Comparator b : Comparator.values()) {
        if (typeName.equalsIgnoreCase(b.toString())) {
          return b;
        }
      }
    }
    
    return null;
  }
}
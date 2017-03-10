/*
 * iTURM is a User and Roles Management web application developed by iteratec, GmbH
 * Copyright (C) 2008 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
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
 * You can contact iteratec GmbH headquarters at Inselkammerstraße 4
 * 82008 München - Unterhaching, Germany, or at email address info@iteratec.de.
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
package de.iteratec.turm.dao.common;

/**
 * Holds data to construct an optimistic locking version check.
 * 
 * This class holds all necessary data to check wether the OL version of an instance
 * in the database is still valid.
 */
public class OlVersionCheck {

  /** A query with a id parameter that returns exactly one column with name 'olVersion'.*/
  private String query;

  /** The id of the entity to check. This is filled into the query when creating a
   * prepared statement with the query string. */
  private Number id;
  
  /** The value the query is expected to return. */
  private Number    olVersionToCheck;


  public OlVersionCheck(String query, Number id, Number olVersionToCheck) {
    this.query = query;
    this.id = id;
    this.olVersionToCheck = olVersionToCheck;
  }

  public Number getOlVersionToCheck() {
    return olVersionToCheck;
  }

  public String getQuery() {
    return query;
  }

  public Number getId() {
    return id;
  }

}

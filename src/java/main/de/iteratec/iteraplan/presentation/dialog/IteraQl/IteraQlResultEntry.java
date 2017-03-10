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
package de.iteratec.iteraplan.presentation.dialog.IteraQl;

public class IteraQlResultEntry {

  private String id1          = "";
  private String name1        = "";
  private String description1 = "";

  private String id2          = "";
  private String name2        = "";
  private String description2 = "";

  public IteraQlResultEntry(String id1, String name1, String description1, String id2, String name2, String description2) {
    if (id1 != null) {
      this.id1 = id1;
    }
    if (name1 != null) {
      this.name1 = name1;
    }
    if (description1 != null) {
      this.description1 = description1;
    }
    if (id2 != null) {
      this.id2 = id2;
    }
    if (name2 != null) {
      this.name2 = name2;
    }
    if (description2 != null) {
      this.description2 = description2;
    }
  }

  public String getId1() {
    return id1;
  }

  public String getName1() {
    return name1;
  }

  public String getDescription1() {
    return description1;
  }

  public String getId2() {
    return id2;
  }

  public String getName2() {
    return name2;
  }

  public String getDescription2() {
    return description2;
  }
}

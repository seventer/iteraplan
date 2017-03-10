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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape;

import java.io.Serializable;


/**
 * Contains six values: column axis top level, column axis bottom level, row axis top level,
 * row axis bottom level, content top level and content bottom level. If the selected axis
 * elements are not hierarchical, the default value is 1.
 * Note: higher level means lower number.
 */
public class LandscapeElementLevels implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID    = 1820483788859846852L;
  private int               topAxisTopLevel     = 1;
  private int               topAxisBottomLevel  = 1;
  private int               sideAxisTopLevel    = 1;
  private int               sideAxisBottomLevel = 1;
  private int               contentTopLevel     = 1;
  private int               contentBottomLevel  = 1;

  public LandscapeElementLevels() {
    // empty
  }

  public LandscapeElementLevels(LandscapeElementLevels source) {
    if (source != null) {
      this.topAxisTopLevel = source.getTopAxisTopLevel();
      this.topAxisBottomLevel = source.getTopAxisBottomLevel();
      this.sideAxisTopLevel = source.getSideAxisTopLevel();
      this.sideAxisBottomLevel = source.getSideAxisBottomLevel();
      this.contentTopLevel = source.getContentTopLevel();
      this.contentBottomLevel = source.getContentBottomLevel();
    }
  }

  public void setTopAxisTopLevel(int topAxisTopLevel) {
    this.topAxisTopLevel = topAxisTopLevel;
  }

  public int getTopAxisTopLevel() {
    return topAxisTopLevel;
  }

  public void setTopAxisBottomLevel(int topAxisBottomLevel) {
    this.topAxisBottomLevel = topAxisBottomLevel;
  }

  public int getTopAxisBottomLevel() {
    return topAxisBottomLevel;
  }

  public void setSideAxisTopLevel(int sideAxisTopLevel) {
    this.sideAxisTopLevel = sideAxisTopLevel;
  }

  public int getSideAxisTopLevel() {
    return sideAxisTopLevel;
  }

  public void setSideAxisBottomLevel(int sideAxisBottomLevel) {
    this.sideAxisBottomLevel = sideAxisBottomLevel;
  }

  public int getSideAxisBottomLevel() {
    return sideAxisBottomLevel;
  }

  public void setContentTopLevel(int contentTopLevel) {
    this.contentTopLevel = contentTopLevel;
  }

  public int getContentTopLevel() {
    return contentTopLevel;
  }

  public void setContentBottomLevel(int contentBottomLevel) {
    this.contentBottomLevel = contentBottomLevel;
  }

  public int getContentBottomLevel() {
    return contentBottomLevel;
  }

}

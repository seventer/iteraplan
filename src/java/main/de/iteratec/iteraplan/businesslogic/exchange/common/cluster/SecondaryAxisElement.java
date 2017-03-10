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
package de.iteratec.iteraplan.businesslogic.exchange.common.cluster;

import java.util.ArrayList;
import java.util.List;


public class SecondaryAxisElement {

  private String                  type;

  private String                  title;

  private List<ContentElement<?>> contentElements;

  private int                     height;

  /**
   * The coordinate (on the X or Y axis, depending on selection) of this element in the
   * implementation of a cluster export. It us supposed to help when locating a cell of content
   * elements by their main and secondary axis references.
   */
  private double                  referencedCoordinate;

  public SecondaryAxisElement(String type) {
    this.type = type;
    this.contentElements = new ArrayList<ContentElement<?>>();
  }

  public List<ContentElement<?>> getRelatedContentElements() {
    return contentElements;
  }

  public void addContentElement(ContentElement<?> element) {
    this.contentElements.add(element);
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public String getType() {
    return type;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getHeight() {
    return height;
  }

  public double getReferencedCoordinate() {
    return referencedCoordinate;
  }

  public void setReferencedCoordinate(double referencedCoordinate) {
    this.referencedCoordinate = referencedCoordinate;
  }

}

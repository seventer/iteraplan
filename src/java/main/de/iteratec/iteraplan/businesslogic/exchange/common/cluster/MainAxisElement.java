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

import de.iteratec.iteraplan.model.interfaces.IdentityStringEntity;


/**
 * Hold a BuildingBlock or an AttributeType respectively.
 * 
 * @param <A>
 */
public class MainAxisElement<A> {

  /**
   * The domain object being held by this element.
   */
  private A                        element;

  /**
   * The parent element. Should be directly corresponding to the business model.
   */
  private MainAxisElement<A>       parent;

  /**
   * A list of child elements. Those should correspond to the selected business elements in their
   * hierarchy.
   */
  private List<MainAxisElement<A>> children;

  /**
   * The start position in units from the logical begin of the axis.
   */
  private int                      startPosition;

  /**
   * The length of the element. Depends on the number and hierarchy of the children.
   */
  private int                      length = 1;

  /**
   * The distance of this element from the hierarchical root. If the element is a root element, its
   * depth is one.
   */
  private int                      depth  = 1;

  /**
   * The list of all content element attached to this element of the main axis.
   */
  private List<ContentElement<?>>  contentElements;

  private String                   name   = "name";

  /**
   * The base coordinate of this main axis element when creating a specific graphic. Can be used by
   * the implementation to store the location of this element on the X or Y axis (depending on the
   * user selection).
   */
  private double                   referencedCoordinate;

  private MainAxis                 mainAxis;

  public MainAxisElement(A element, MainAxis mainAxis) {
    this.element = element;
    this.parent = null;
    this.children = new ArrayList<MainAxisElement<A>>();
    this.contentElements = new ArrayList<ContentElement<?>>();
    this.mainAxis = mainAxis;
    if (element instanceof IdentityStringEntity) {
      IdentityStringEntity entity = (IdentityStringEntity) element;
      this.name = entity.getIdentityString();
    }
  }

  public List<ContentElement<?>> getRelatedContentElements() {
    return this.contentElements;
  }

  public void addContentElement(ContentElement<?> contentElement) {
    this.contentElements.add(contentElement);
  }

  public A getElement() {
    return element;
  }

  public MainAxisElement<A> getParent() {
    return parent;
  }

  public void setParent(MainAxisElement<A> parent) {
    this.parent = parent;
  }

  public List<MainAxisElement<A>> getChildren() {
    return children;
  }

  public void setChildren(List<MainAxisElement<A>> children) {
    this.children = children;
    for (MainAxisElement<?> child : children) {
      if (!mainAxis.getAllElements().contains(child)) {
        mainAxis.getAllElements().add(child);
      }
    }
  }

  public void addChild(MainAxisElement<A> child) {
    this.children.add(child);
    if (!mainAxis.getAllElements().contains(child)) {
      mainAxis.getAllElements().add(child);
    }
  }

  public int getStartPosition() {
    return startPosition;
  }

  public void setStartPosition(int startPosition) {
    this.startPosition = startPosition;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  public int getDepth() {
    return this.depth;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getReferencedCoordinate() {
    return referencedCoordinate;
  }

  public void setReferencedCoordinate(double referencedCoordinate) {
    this.referencedCoordinate = referencedCoordinate;
  }

}

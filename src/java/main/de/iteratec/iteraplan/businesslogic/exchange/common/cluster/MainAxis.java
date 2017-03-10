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
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Holds references to the top level elements of the hierarchical structure of elements of the main
 * axis.
 */
public class MainAxis {

  private String                   title;
  private Set<MainAxisElement<?>>  allMainAxisElements;

  private List<MainAxisElement<?>> axisElements;

  private int                      mainAxisLength;

  public MainAxis() {
    this.axisElements = new ArrayList<MainAxisElement<?>>();
    this.allMainAxisElements = new HashSet<MainAxisElement<?>>();
  }

  public void addElement(MainAxisElement<?> element) {
    this.axisElements.add(element);
    this.allMainAxisElements.add(element);
    addChildrenToList(element);
    removeParents(element);
  }

  /**
   * Retrieves the top level elements of the main axis.
   * 
   * @return A sorted list of the top level elements of the main axis.
   */
  public List<MainAxisElement<?>> getElements() {
    return this.axisElements;
  }

  /**
   * Retrieves all elements, not only the top level ones.
   * 
   * @return A set of all elements from all levels.
   */
  public Set<MainAxisElement<?>> getAllElements() {
    return this.allMainAxisElements;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return this.title;
  }

  public void setMainAxisLength(int length) {
    this.mainAxisLength = length;
  }

  public int getMainAxisLength() {
    return mainAxisLength;
  }

  public int getMainAxisDepth() {
    int mainAxisDepth = 0;
    for (MainAxisElement<?> element : allMainAxisElements) {
      mainAxisDepth = Math.max(mainAxisDepth, element.getDepth());
    }
    return mainAxisDepth;
  }

  /**
   * This method is to be called after all elements have been added to the main axis. The method
   * corrects the depths of the axis elements if a hierarchical selection has been made and the top
   * level is not the first one.
   */
  public void adjustAxisDepths() {
    for (MainAxisElement<?> element : axisElements) {
      adjustAxisDepthsIntern(element, 1);
    }
  }

  private void adjustAxisDepthsIntern(MainAxisElement<?> element, int level) {
    element.setDepth(level);
    for (MainAxisElement<?> child : element.getChildren()) {
      adjustAxisDepthsIntern(child, level + 1);
    }
  }

  /**
   * When adding a new root element to the main axis, all the children of this element should be
   * inserted to the flattened list of elements.
   * 
   * @param element
   *          The element whose children are to be added.
   */
  private void addChildrenToList(MainAxisElement<?> element) {
    for (MainAxisElement<?> child : element.getChildren()) {
      if (!allMainAxisElements.contains(child)) {
        allMainAxisElements.add(child);
      }
      addChildrenToList(child);
    }
  }

  /**
   * We remove the parents as adding an element directly to the main axis implicitly makes the
   * element top level. This means that the element must not have parents.
   * 
   * @param element
   *          The element whose parents are to be removed.
   */
  private void removeParents(MainAxisElement<?> element) {
    Set<MainAxisElement<?>> aggregatedParents = aggregateParents(element);

    for (MainAxisElement<?> parent : aggregatedParents) {
      for (MainAxisElement<?> child : parent.getChildren()) {
        child.setParent(null);
      }
      allMainAxisElements.remove(parent);
    }
  }

  /**
   * Collects all hierarchical parents.
   * 
   * @param element
   *          The element to start the search from.
   * @return The set of all aggregated parents.
   */
  private Set<MainAxisElement<?>> aggregateParents(MainAxisElement<?> element) {

    Set<MainAxisElement<?>> parents = new HashSet<MainAxisElement<?>>();
    MainAxisElement<?> parent = element.getParent();

    while (parent != null) {
      parents.add(parent);
      parent = parent.getParent();
    }
    return parents;
  }

}

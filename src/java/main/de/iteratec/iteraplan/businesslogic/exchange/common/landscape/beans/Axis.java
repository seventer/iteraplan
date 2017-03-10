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
package de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans;

import java.util.Collections;
import java.util.List;

import de.iteratec.iteraplan.common.util.CollectionUtils;


/**
 * Data for labelling an axis of the diagram.
 * 
 * This class stores all data relevant to fill a row or column header, including the
 * hierarchy and sorting order of the shapes.
 */
public class Axis extends Domain {
  /**
   * The toplevel of the data used for the axis.
   * 
   * This is a structure continued through the
   * {@link de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.AxisElement#getChildren()}
   * method. Each part is a tree, but there can be multiple roots. The list contains all roots
   * for the structure.
   */
  private List<AxisElement<?>> toplevelElements;

  /**
   * The maximum level of the hierarchy as calculated during creation in AxisHelper.
   */
  private int               maxLevel;

  /**
   * Creates a new axis domain.
   * 
   * @param label             The name to use for display purposes.
   * @param toplevelElements  The list of toplevel elements, which can spawn a tree using 
   *                          {@link de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.AxisElement#getChildren()}
   * @param maxLevel          The deepest level of the hierarchy.
   */
  public Axis(String label, List<AxisElement<?>> toplevelElements, int maxLevel) {
    super(label);
    this.toplevelElements = toplevelElements;
    this.maxLevel = maxLevel;
  }

  /**
   * @return All elements on the axis in their natural or specified order. 
   *         If the axis is hierarchical, the hierarchy is flattened by
   *         a deep search.
   */
  public List<AxisElement<?>> getElements() {
    final List<AxisElement<?>> list = CollectionUtils.arrayList();
    for (AxisElement<?> element : toplevelElements) {
      deepSearch(element, list);
    }
    return Collections.unmodifiableList(list);
  }

  /**
   * Collects all elements found during a depth-first search in the list.
   * 
   * This method adds successors of the element depth-first, by using the
   * getChildren() method.
   * 
   * @param element  The element to start with. It will be added to the list last.
   * @param list     IN/OUT. The list to fill.
   */
  private void deepSearch(AxisElement<?> element, List<AxisElement<?>> list) {
    if (element.getChildren() != null) {
      for (AxisElement<?> child : element.getChildren()) {
        deepSearch(child, list);
      }
    }
    list.add(element);
  }

  /**
   * The depth of the hierarchy.
   * 
   * @return The depth of the hierarchy.
   */
  public int getMaxLevel() {
    return maxLevel;
  }

  /**
   * Calculates the length over all elements.
   * 
   * @return  The length over all elements.
   */
  public double getTotalDisplayLength() {
    if (toplevelElements.isEmpty()) {
      return 0;
    }
    final AxisElement<?> el = toplevelElements.get(toplevelElements.size() - 1);
    return el.getDisplayStartPosition() + el.getDisplayLength();
  }
}

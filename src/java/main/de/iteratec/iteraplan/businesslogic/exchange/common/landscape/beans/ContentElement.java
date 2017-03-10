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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.common.util.HashBucketMatrix;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.iteraplan.model.sorting.BuildingBlockSortHelper;


public class ContentElement<E extends IdentityEntity & Comparable<E>> extends Element {

  private AxisElement<?> topAxisRef;

  private AxisElement<?> sideAxisRef;

  /**
   * Used when merging content elements. Depending on alignment, this value represents length or
   * height.
   */
  private double         size = 1;

  private E              element;

  private int            internalLevel;

  public ContentElement(AxisElement<?> topAxisRef, AxisElement<?> sideAxisRef, E element) {
    super(element.getIdentityString());
    this.topAxisRef = topAxisRef;
    this.sideAxisRef = sideAxisRef;
    this.element = element;
  }

  public E getElement() {
    return element;
  }

  public AxisElement getSideAxisRef() {
    return sideAxisRef;
  }

  public AxisElement getTopAxisRef() {
    return topAxisRef;
  }

  /**
   * For each content BuildingBlock, there exist one or more ContentElements. This method returns a
   * list of lists of ContentElements. The first list groups the ContentElements by their respective
   * BuildingBlock and is sorted accordingly. The second list contains ContentElements that
   * represent the same BuildingBlock.
   * 
   * @param axisElement
   * @return A list of lists of ContentElements.
   */
  public List<List<ContentElement<E>>> getContentElementsGrouped(AxisElement<ContentElement<E>> axisElement) {
    HashBucketMap<E, ContentElement<E>> map = new HashBucketMap<E, ContentElement<E>>();
    for (Iterator<ContentElement<E>> it = axisElement.getContentElements().iterator(); it.hasNext();) {
      ContentElement<E> ce = it.next();
      map.add(ce.getElement(), ce);
    }
    return getGroupedContentElements(map);
  }

  private List<List<ContentElement<E>>> getGroupedContentElements(HashBucketMap<E, ContentElement<E>> map) {
    List<E> keys = new ArrayList<E>(map.keySet());
    sort(keys);
    List<List<ContentElement<E>>> result = new ArrayList<List<ContentElement<E>>>();
    for (Iterator<E> it = keys.iterator(); it.hasNext();) {
      result.add(map.get(it.next()));
    }
    return result;
  }

  /**
   * @param keys
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void sort(List<E> keys) {
    if (keys.isEmpty()) {
      return;
    }

    if (BuildingBlock.class.isAssignableFrom(keys.get(0).getClass())) {
      BuildingBlockSortHelper.sortByDefault((List) keys);
    }
    else {
      Collections.sort(keys);
    }

  }

  /**
   * For each content BuildingBlock, there exist one or more ContentElements. This method returns a
   * list of lists of ContentElements. The first list groups the ContentElements by their respective
   * BuildingBlock and is sorted accordingly. The second list contains ContentElements that
   * represent the same BuildingBlock. Note that this method depends on the contentElementGroups
   * bucket which is only fully initialized after the method
   * AxisElement.getUniqueElementCount(Element, boolean) has been called for all ContentElements of
   * this AxisElement.
   * 
   * @param axisElement
   *          The axis element which defines the set of groups to choose from.
   * @return A list of lists of content elements.
   */
  @SuppressWarnings("unchecked")
  public List<List<ContentElement<E>>> getContentElementGroupForElement(AxisElement<ContentElement<E>> axisElement) {

    HashBucketMap<E, ContentElement<E>> map = new HashBucketMap<E, ContentElement<E>>();

    HashBucketMatrix<AxisElement<?>, AxisElement<?>, List<ContentElement<?>>> groups = axisElement.getContentElementGroups();
    List<List<ContentElement<?>>> contentElements = groups.getBucket(this.getSideAxisRef(), getTopAxisRef());

    for (Iterator<ContentElement<?>> it = contentElements.get(0).iterator(); it.hasNext();) {
      ContentElement<E> ce = (ContentElement<E>) it.next();
      map.add(ce.getElement(), ce);
    }
    return getGroupedContentElements(map);
  }

  public double getSize() {
    return size;
  }

  public void increaseSize() {
    this.size++;
  }

  public void setSideAxisRef(AxisElement<?> sideAxisRef) {
    this.sideAxisRef = sideAxisRef;
  }

  public void setTopAxisRef(AxisElement<?> topAxisRef) {
    this.topAxisRef = topAxisRef;
  }

  /**
   * @return The level (order) within one cell (if there are more than one building blocks).
   */
  public int getInternalLevel() {
    return internalLevel;
  }

  public void setInternalLevel(int internalLevel) {
    this.internalLevel = internalLevel;
  }
}

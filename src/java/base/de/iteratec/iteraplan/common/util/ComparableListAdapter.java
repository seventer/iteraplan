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
package de.iteratec.iteraplan.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 *  Utility Wrapper/Adapter class for java.util.List, to adapt List to Comparable.
 *  Useful for sorting of multivalue fields.
 */
public class ComparableListAdapter<T extends Comparable<? super T>> implements List<T>, Comparable<ComparableListAdapter<T>> {

  public static final int SORT_WRAPPED_LIST    = 1;
  public static final int NO_SORT_WRAPPED_LIST = 0;

  private final List<T>   source;

  public ComparableListAdapter() {
    this.source = new ArrayList<T>();
  }

  public ComparableListAdapter(List<T> source, int sort) {
    if (sort == SORT_WRAPPED_LIST) {
      Collections.sort(source);
    }

    this.source = source;
  }

  // WRAPPER METHODS:

  public int size() {
    return source.size();
  }

  public boolean isEmpty() {
    return source.isEmpty();
  }

  public boolean contains(Object o) {
    return source.contains(o);
  }

  public Iterator<T> iterator() {
    return source.iterator();
  }

  public Object[] toArray() {
    return source.toArray();
  }

  public <E> E[] toArray(E[] a) {
    return source.toArray(a);
  }

  public boolean add(T e) {
    return source.add(e);
  }

  public boolean remove(Object o) {
    return source.remove(o);
  }

  public boolean containsAll(Collection<?> c) {
    return source.containsAll(c);
  }

  public boolean addAll(Collection<? extends T> c) {
    return source.addAll(c);
  }

  public boolean addAll(int index, Collection<? extends T> c) {
    return source.addAll(index, c);
  }

  public boolean removeAll(Collection<?> c) {
    return source.removeAll(c);
  }

  public boolean retainAll(Collection<?> c) {
    return source.retainAll(c);
  }

  public void clear() {
    source.clear();
  }

  @Override
  public boolean equals(Object o) {
    return source.equals(o);
  }

  @Override
  public int hashCode() {
    return source.hashCode();
  }

  public T get(int index) {
    return source.get(index);
  }

  public T set(int index, T element) {
    return source.set(index, element);
  }

  public void add(int index, T element) {
    source.add(index, element);
  }

  public T remove(int index) {
    return source.remove(index);
  }

  public int indexOf(Object o) {
    return source.indexOf(o);
  }

  public int lastIndexOf(Object o) {
    return source.lastIndexOf(o);
  }

  public ListIterator<T> listIterator() {
    return source.listIterator();
  }

  public ListIterator<T> listIterator(int index) {
    return source.listIterator(index);
  }

  public List<T> subList(int fromIndex, int toIndex) {
    return source.subList(fromIndex, toIndex);
  }

  
  /**
   *  Compares the Lists element by element, starting from index 0 to min(this.size(), other.size())+1.
   *  Null value is treated as the lowest possible value. 
   */
  public int compareTo(ComparableListAdapter<T> other) {
    Iterator<T> thisIterator = this.iterator();
    Iterator<T> otherIterator = other.iterator();

    while (thisIterator.hasNext() || otherIterator.hasNext()) {
      if (thisIterator.hasNext() ^ otherIterator.hasNext()) {
        return thisIterator.hasNext() ? 1 : -1;
      }
      else {
        T thisElem = thisIterator.next();
        T otherElem = otherIterator.next();

        if (thisElem.compareTo(otherElem) != 0) {
          return thisElem.compareTo(otherElem);
        }
      }
    }

    return 0;
  }

}

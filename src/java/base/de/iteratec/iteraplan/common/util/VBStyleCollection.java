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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * This class is basically an {@link ArrayList}, but allows the lookup of individual entries by a
 * key of type K. <br>
 * <br>
 * VBStyle stands for Visual Basic style.
 */
public class VBStyleCollection<K, E> extends ArrayList<E> implements Serializable {

  private static final long serialVersionUID = -6550982955702948057L;

  /**
   * Maps from a key to an index (Integer), which specifies the position of the object with that key
   * in the ArrayList.
   */
  private Map<K, Integer>   map              = new HashMap<K, Integer>();

  /**
   * Contains all keys in the same order as the respective objects in the ArrayList.
   */
  private List<K>           lstKeys          = new ArrayList<K>();

  /**
   * Default constructor.
   */
  public VBStyleCollection() {
    super();
  }

  /**
   * A copy constructor
   * 
   * @param c
   *          The VBStyleCollection to copy.
   */
  @SuppressWarnings("unchecked")
  // JDK classes do not use covariance on clone()
  public VBStyleCollection(VBStyleCollection<K, E> c) {
    super();
    if (c == null) {
      return;
    }
    addAllPrivate(c);
    this.lstKeys = new ArrayList<K>(c.lstKeys);
    this.map = (HashMap<K, Integer>) ((HashMap<K, Integer>) c.map).clone();
  }

  /**
   * Unsupported operation. Will throw an {@link UnsupportedOperationException}.
   */
  @Override
  public boolean add(E element) {
    throw new UnsupportedOperationException();
  }

  /**
   * Add an entry with a key.
   * 
   * @param element
   *          the element to add
   * @param key
   *          the key of the element
   */
  public void addWithKey(E element, K key) {
    if (getWithKey(key) != null) {
      removeWithKey(key);
    }
    map.put(key, Integer.valueOf(this.size()));
    super.add(element);
    lstKeys.add(key);
  }

  /**
   * Unsupported operation. Will throw an {@link UnsupportedOperationException}.
   */
  @Override
  public void add(int index, E element) {
    throw new UnsupportedOperationException();
  }

  /**
   * Removes an entry with the given key.
   * 
   * @param key
   *          the key used for removal.
   */
  public void removeWithKey(K key) {
    if (getWithKey(key) == null) {
      return;
    }
    int index = map.get(key).intValue();
    addToListIndex(index + 1, -1);
    super.remove(index);
    lstKeys.remove(index);
    map.remove(key);
  }

  @Override
  public E remove(int index) {
    if (index > (size() - 1)) {
      throw new IndexOutOfBoundsException();
    }
    addToListIndex(index + 1, -1);
    Object obj = lstKeys.get(index);
    if (obj != null) {
      map.remove(obj);
    }
    lstKeys.remove(index);
    return super.remove(index);
  }

  /**
   * Fetch an entry by its key.
   * 
   * @param key
   *          the key of the entry.
   * @return the entry with the given key.
   */
  public E getWithKey(K key) {
    Integer index = map.get(key);
    if (index == null) {
      return null;
    }
    return super.get(index.intValue());
  }

  /**
   * Checks if a key is contained in this VBStyleCollection.
   * 
   * @param key
   *          the key to check for
   * @return true iff the key is contained in this collection.
   */
  public boolean containsKey(K key) {
    return map.containsKey(key);
  }

  @Override
  public void clear() {
    map.clear();
    lstKeys.clear();
    super.clear();
  }

  private void addAllPrivate(Collection<E> list) {
    super.addAll(list);
  }

  /**
   * @return Returns the map.
   */
  public Map<K, Integer> getMap() {
    return map;
  }

  /**
   * @return Returns the lstKeys.
   */
  public List<K> getLstKeys() {
    return lstKeys;
  }

  /**
   * Sorts the collection. The collection elements must implement the Comparable interface.
   */
  public void sort() {
    sort(null);
  }

  /**
   * Sorts the collection. Optionally, a Comparator can be provided.
   * 
   * @param c
   *          The {@link Comparator} that is used for sorting. If Comparator is null, it is assumed
   *          that the elements implement the Comparable interface. TODO the non-comparator version
   *          makes the assumption that the elements are comparable, which is not safe TO DO it might
   *          be a lot easier (and generally better) to implement
   *          {@link java.util.ListIterator#set(Object)} properly -- currently this is still a way
   *          to break the invariants of this class anyway
   */
  @SuppressWarnings({ "unchecked" })
  @edu.umd.cs.findbugs.annotations.SuppressWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
  // suppress a warning for a line where the context ensures that it will return null
  // assuming List<Comparable> below
  public void sort(Comparator<? super E> c) {
    HashMap<E, List<K>> backup = new HashMap<E, List<K>>();
    for (Entry<K, Integer> entry : map.entrySet()) {
      K key = entry.getKey();
      Integer index = entry.getValue();
      E o = super.get(index.intValue());
      List<K> keyList = backup.get(o);
      if (keyList == null) {
        keyList = new ArrayList<K>();
        backup.put(o, keyList);
      }
      keyList.add(key);
      backup.remove(o);
      backup.put(o, keyList);
    }
    if (c != null) {
      Collections.sort(this, c);
    }
    else {
      Collections.sort((List<Comparable>) this);
    }
    map = new HashMap<K, Integer>();
    lstKeys = new ArrayList<K>();
    VBStyleCollection<K, E> sortedCollection = new VBStyleCollection<K, E>();

    for (E o : this) {
      List<K> keyList = backup.get(o);
      K key = keyList.get(keyList.size() - 1);
      keyList.remove(keyList.size() - 1);
      if (!keyList.isEmpty()) {
        backup.remove(o);
        backup.put(o, keyList);
      }
      else {
        backup.remove(o);
      }
      sortedCollection.addWithKey(o, key);
    }
    this.map = sortedCollection.getMap();
    this.lstKeys = sortedCollection.getLstKeys();
  }

  /**
   * Manages the indices of objects when adding or removing from the list.
   * 
   * @param index
   *          The index
   * @param diff
   */
  private void addToListIndex(int index, int diff) {
    for (int i = lstKeys.size() - 1; i >= index; i--) {
      K key = lstKeys.get(i);
      if (key != null) {
        map.put(key, Integer.valueOf((i + diff)));
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(180);
    sb.append("VBStyleCollection content: \n\t");
    sb.append(super.size()).append(" Objects contained in list: \n");
    for (E obj : this) {
      sb.append("\t\tObject: " + obj + "\n");
    }
    sb.append("\t" + map.entrySet().size() + " Objects contained in map: \n");
    for (Entry<K, Integer> entry : map.entrySet()) {
      sb.append("\t\tKey: " + entry.getKey() + " Value: " + entry.getValue() + "\n");
    }
    sb.append("\t" + lstKeys.size() + " Objects contained in lstKeys: \n");
    for (K lstKey : lstKeys) {
      sb.append("\t\tObject: " + lstKey + "\n");
    }
    sb.append('\n');
    return sb.toString();
  }

  /**
   * Unsupported operation. Will throw an {@link UnsupportedOperationException}.
   */
  @Override
  public boolean addAll(Collection<? extends E> arg0) {
    throw new UnsupportedOperationException();
  }

  /**
   * Unsupported operation. Will throw an {@link UnsupportedOperationException}.
   */
  @Override
  public boolean addAll(int arg0, Collection<? extends E> arg1) {
    throw new UnsupportedOperationException();
  }

  /**
   * Unsupported operation. Will throw an {@link UnsupportedOperationException}.
   */
  @Override
  public boolean remove(Object arg0) {
    throw new UnsupportedOperationException();
  }

  /**
   * Unsupported operation. Will throw an {@link UnsupportedOperationException}.
   */
  @Override
  public boolean removeAll(Collection<?> arg0) {
    throw new UnsupportedOperationException();
  }

  /**
   * Unsupported operation. Will throw an {@link UnsupportedOperationException}.
   */
  @Override
  protected void removeRange(int arg0, int arg1) {
    throw new UnsupportedOperationException();
  }

  /**
   * Unsupported operation. Will throw an {@link UnsupportedOperationException}.
   */
  @Override
  public boolean retainAll(Collection<?> arg0) {
    throw new UnsupportedOperationException();
  }

  /**
   * Unsupported operation. Will throw an {@link UnsupportedOperationException}.
   */
  @Override
  public List<E> subList(int arg0, int arg1) {
    throw new UnsupportedOperationException();
  }
}

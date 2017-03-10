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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;


/**
 * Based on a {@link HashBucketMap} this class allows to store a list of values by two keys. It can
 * be used to store multiple values for discrete values in a coordinate system.
 */
public class HashBucketMatrix<K1, K2, V> implements Serializable {
  private static final long             serialVersionUID = -3944317476525130041L;

  /**
   * (Object firstDimensionKey, HashBucketMap(Object secondDimensionKey, ArrayList values))
   */
  private Map<K1, HashBucketMap<K2, V>> firstDimension   = new HashMap<K1, HashBucketMap<K2, V>>();

  /**
   * Returns the list that is stored in the Matrix for the specified keys
   * 
   * @param first
   *          key for the first dimension of the matrix
   * @param second
   *          key for the second dimension of the matrix
   * @return value for the specified key (maybe null)
   */
  public List<V> getBucket(K1 first, K2 second) {
    HashBucketMap<K2, V> bucketMap = this.firstDimension.get(first);
    if (bucketMap == null) {
      return null;
    }
    return bucketMap.get(second);
  }

  /**
   * Returns the list (or an empty list) that is stored in the Matrix for the specified keys
   * 
   * @param first
   *          key for the first dimension of the matrix
   * @param second
   *          key for the second dimension of the matrix
   * @return value for the specified key (maybe an empty list but not null)
   */
  public List<V> getBucketNotNull(K1 first, K2 second) {
    HashBucketMap<K2, V> bucketMap = this.firstDimension.get(first);
    if (bucketMap == null) {
      return new ArrayList<V>();
    }
    return bucketMap.getBucketNotNull(second);
  }

  /**
   * adds a value to the specified bucket in the matrix (defined by first and second key)
   * 
   * @param first
   *          first dimension key
   * @param second
   *          second dimension key
   * @param value
   *          the value to add to the bucket
   */
  public void add(K1 first, K2 second, V value) {
    HashBucketMap<K2, V> bucketMap = this.firstDimension.get(first);
    if (bucketMap == null) {
      bucketMap = new HashBucketMap<K2, V>();
      this.firstDimension.put(first, bucketMap);
    }
    bucketMap.add(second, value);
  }

  /**
   * Return all values contained in this map.
   * 
   * @return all values contained in this map
   */
  public Collection<V> values() {
    List<V> l = new ArrayList<V>();
    for (HashBucketMap<K2, V> map : firstDimension.values()) {
      for (List<V> contents : map.values()) {
        l.addAll(contents);
      }
    }
    return l;
  }

  /**
   * Removes all entries with the given key1.
   * 
   * @param key1
   */
  public void removeKey1(K1 key1) {
    firstDimension.remove(key1);
  }

  /**
   * Removes all entries with the given key2.
   * 
   * @param key2
   */
  public void removeKey2(K2 key2) {
    for (Iterator<Map.Entry<K1, HashBucketMap<K2, V>>> it1 = firstDimension.entrySet().iterator(); it1.hasNext();) {
      Map.Entry<K1, HashBucketMap<K2, V>> entry = it1.next();
      HashBucketMap<K2, V> secondDimension = entry.getValue();
      for (Iterator<K2> it2 = secondDimension.keySet().iterator(); it2.hasNext();) {
        K2 tmpKey = it2.next();
        if (tmpKey.equals(key2)) {
          it2.remove();
        }
      }
      if (secondDimension.keySet().isEmpty()) {
        it1.remove();
      }
    }
  }

  /**
   * Removes all values stored under the given keys from the map.
   * 
   * @param key1
   * @param key2
   */
  public void remove(K1 key1, K2 key2) {
    HashBucketMap<K2, V> bucketMap = this.firstDimension.get(key1);
    if (bucketMap == null) {
      return;
    }
    bucketMap.remove(key2);
  }

  /**
   * Returns a set that contains HashBucketMap.Entry objects that hold a key pair and a value each.
   * This method is similar to the entrySet method of a normal HashMap. The returned Set has no
   * references to the map, so removing and adding elements will have no effect.
   * 
   * @return a collection view of the map
   */
  public Set<Entry<K1, K2, V>> entrySet() {
    Set<Entry<K1, K2, V>> entrySet = new HashSet<Entry<K1, K2, V>>();

    for (K1 key1 : firstDimension.keySet()) {
      HashBucketMap<K2, V> bucketMap = firstDimension.get(key1);

      for (java.util.Map.Entry<K2, List<V>> e : bucketMap.entrySet()) {
        for (V val : e.getValue()) {
          Entry<K1, K2, V> entry = new Entry<K1, K2, V>(key1, e.getKey(), val);
          entrySet.add(entry);
        }
      }
    }
    return entrySet;
  }

  /**
   * String representation of the HashBucketMatrix. for each entry it shows the key and all of its
   * values. the toString method of each object is used for that.
   * 
   * @return the string representation
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(30);

    // get keys
    HashSet<K2> secondDimensionKeys = new HashSet<K2>();
    for (HashBucketMap<K2, V> bucketMap : this.firstDimension.values()) {
      for (K2 secondKey : bucketMap.keySet()) {
        secondDimensionKeys.add(secondKey);
      }
    }

    sb.append("\n          || ");
    for (K1 key : this.firstDimension.keySet()) {
      if (key == null) {
        sb.append(StringUtils.rightPad("null", 10));
      }
      else {
        sb.append(StringUtils.rightPad(key.toString(), 10));
      }
      sb.append("| ");
    }
    sb.append('\n');

    for (K2 secondKey : secondDimensionKeys) {
      if (secondKey == null) {
        sb.append(StringUtils.rightPad("null", 10));
      }
      else {
        sb.append(StringUtils.rightPad(secondKey.toString(), 10));
      }
      sb.append("|| ");
      for (K1 firstKey : this.firstDimension.keySet()) {
        List<V> bucket = this.getBucketNotNull(firstKey, secondKey);
        StringBuffer bucketBuffer = new StringBuffer();
        for (V value : bucket) {
          bucketBuffer.append(value);
          bucketBuffer.append(';');
        }
        if (bucketBuffer.length() > 0) {
          bucketBuffer.deleteCharAt(bucketBuffer.length() - 1);
        }
        sb.append(StringUtils.rightPad(bucketBuffer.toString(), 10));
        sb.append("| ");
      }
      sb.append('\n');
    }

    return sb.toString();
  }

  // The HashBucketMatrix class maps two keys to a _list_ of values of type V.
  // The entries represented by the following class map two keys to a _single_
  // value of type V.
  /**
   * Class that is used to represent individual entries in this map.
   */
  public static class Entry<K1, K2, V> {
    private final K1 key1;
    private final K2 key2;
    private final V  value;

    /**
     * Constructor
     * 
     * @param key1
     * @param key2
     * @param value
     */
    public Entry(K1 key1, K2 key2, V value) {
      super();
      this.key1 = key1;
      this.key2 = key2;
      this.value = value;
    }

    public K1 getKey1() {
      return key1;
    }

    public K2 getKey2() {
      return key2;
    }

    public V getValue() {
      return value;
    }

    public String toString() {
      return String.format("[%s, %s, %s]", key1, key2, value);
    }
  }

}

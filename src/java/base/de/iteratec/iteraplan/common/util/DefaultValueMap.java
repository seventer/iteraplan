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

import java.util.Collection;
import java.util.Set;


/**
 *  Wraps a java.util.Map instance and returns a default value instead of null, if the key is not contained.
 *  It's against the specification!!
 */
public class DefaultValueMap<K, V> implements java.util.Map<K, V> {

  private final java.util.Map<K, V> map;
  private V                         defaultValue;

  public DefaultValueMap(java.util.Map<K, V> map, V defaultValue) {
    this.map = map;
    this.defaultValue = defaultValue;
  }

  public int size() {
    return map.size();
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  /**
   * Returns a default value, if the key is not present.
   */
  public V get(Object key) {
    return map.containsKey(key) ? map.get(key) : defaultValue;
  }

  public V put(K key, V value) {
    return map.put(key, value);
  }

  public V remove(Object key) {
    return map.remove(key);
  }

  public void putAll(java.util.Map<? extends K, ? extends V> t) {
    map.putAll(t);
  }

  public void clear() {
    map.clear();
  }

  public Set<K> keySet() {
    return map.keySet();
  }

  public Collection<V> values() {
    return map.values();
  }

  public java.util.Set<java.util.Map.Entry<K, V>> entrySet() {
    return map.entrySet();
  }

  public V getDefault() {
    return defaultValue;
  }

  public void setDefault(V defaultValue) {
    this.defaultValue = defaultValue;
  }
}

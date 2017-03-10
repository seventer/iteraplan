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
import java.util.HashMap;
import java.util.List;


/**
 * This class decorates the {@link HashMap} class in order to map keys to a list of 
 * values. It provides two convenience features:
 * <ul>
 *  <li>Methods that are similar to the methods {@link List#add(int, Object)} and 
 *      {@link List#addAll(int, Collection)} in the {@link List} interface may be 
 *      called directly on the map: {@code add(K,V)} and {@code addAll(K,Collection<V>)}. 
 *      This adds the object of type V or the collection of objects of type V direcly 
 *      to the mapped list.</li>
 *  <li>The mapped lists are created implicitly if required.</li>
 * </ul>
 * The remaining methods, e.g. get() or put(), operate as described in the API pages 
 * for the {@link java.util.Map} interface.
 * <p>
 * The concrete implementation of the {@link List} interface that is to be mapped 
 * may be returned via the {@link #getConcreteListImplementation()} method which 
 * must be overridden in subclasses. 
 * 
 * @param <K>
 *    The type of keys maintained by this map.
 * @param <V>
 *    The type of mapped values.
 */
public abstract class AbstractMultiValueMap<K, V> extends HashMap<K, List<V>> {

  private static final long serialVersionUID = -4289532376384862538L;

  /**
    * Returns the value to which the specified key is mapped, or a freshly 
    * created, empty {@link List} if this map contains no mapping for the key.
    * 
    * @param key 
    *    The key whose associated value is to be returned.
    *    
    * @return
    *    See method description.
    */
  public List<V> getBucketNotNull(K key) {

    List<V> bucket = super.get(key);
    if (bucket == null) {
      bucket = getConcreteListImplementation();
    }

    return bucket;
  }

  /**
   * Adds the specified value to a {@link List} and stores that list in the 
   * map under the specified key. If no list has been created for the key yet, 
   * it will be created beforehande.
   * 
   * @param key 
   *    Key with which the specified value is to be associated.
   * @param value
   *    Value to be associated with the specified key.
   */
  public void add(K key, V value) {

    List<V> bucket = this.get(key);
    if (bucket == null) {
      bucket = getConcreteListImplementation();
      super.put(key, bucket);
    }
    bucket.add(value);
  }

  /**
   * Adds all the values specified in the given {@link Collection} to the 
   * existing values stored in the map under the specified key. If no list 
   * has been created for the key yet, it will be created beforehande.
   * 
   * @param key 
   *    Key with which the specified value is to be associated.
   * @param values 
   *    Value to be associated with the specified key.
   */
  public void addAll(K key, Collection<V> values) {

    List<V> bucket = this.get(key);
    if (bucket == null) {
      bucket = getConcreteListImplementation();
      super.put(key, bucket);
    }
    bucket.addAll(values);
  }

  /**
   * Returns a string representation of the map. For each entry it 
   * shows the key and all of its values. The {@code toString} method 
   * of each object is used for that purpose.
   * 
   * @return
   *    See method description.
   */
  public String toString() {

    StringBuffer sb = new StringBuffer(25);
    for (K key : super.keySet()) {
      sb.append("key = ").append(key).append(" - value = ");
      List<V> value = this.getBucketNotNull(key);
      for (V element : value) {
        sb.append(element).append(", ");
      }
      sb.append('\n');
    }

    return sb.toString();
  }

  protected abstract List<V> getConcreteListImplementation();
}

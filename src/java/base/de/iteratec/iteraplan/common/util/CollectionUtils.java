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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Provides static utility methods for operations on all sorts of collections.
 */
public final class CollectionUtils {

  private CollectionUtils() {
    // Suppress generation of default constructor, ensuring non-instantiability.
  }

  /**
   * Creates an array of type {@code T} containing the given elements.
   * 
   * @param ts The elements that the array should contain.
   * @return A newly-created array containing the given elements.
   */
  public static <T> T[] array(T... ts) {
    return ts;
  }

  /**
   * Creates an empty {@code ArrayList} instance.
   * <p>
   * <b>Note:</b> If you need an <i>immutable</i> empty List, use
   * {@link java.util.Collections#emptyList}. instead.
   * 
   * @return A newly-created, initially-empty {@code ArrayList}.
   */
  public static <E> List<E> arrayList() {
    return new ArrayList<E>();
  }

  /**
   * Creates a {@code HashMap} instance.
   * <p>
   * <b>Note:</b> If {@code K} is an {@code enum} type, use an {@link java.util.EnumMap} instead.
   * <p>
   * <b>Note:</b> If you need an <i>immutable</i> empty List, use
   * {@link java.util.Collections#emptyMap}.
   * 
   * @return A newly-created, initially-empty {@code HashMap}.
   */
  public static <K, V> Map<K, V> hashMap() {
    return new HashMap<K, V>();
  }

  /**
   * Creates an empty {@code HashSet} instance.
   * <p>
   * <b>Note:</b> If {@code E} is an {@link Enum} type, use {@link java.util.EnumSet#noneOf}
   * instead.
   * <p>
   * <b>Note:</b> If you need an <i>immutable</i> empty List, use
   * {@link java.util.Collections#emptySet}.
   * 
   * @return A newly-created, initially-empty {@code HashSet}.
   */
  public static <T> Set<T> hashSet() {
    return new HashSet<T>();
  }

  /**
   * Creates a {@code HashSet} instance containing the given elements.
   * 
   * @param ts The elements that the set should contain.
   * @return A newly-created {@code HashSet} containing the given elements (minus duplicates).
   */
  public static <T> Set<T> hashSet(T... ts) {
    return new HashSet<T>(Arrays.asList(ts));
  }

}

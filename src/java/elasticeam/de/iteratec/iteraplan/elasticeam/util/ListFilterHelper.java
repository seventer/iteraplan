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
package de.iteratec.iteraplan.elasticeam.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;
import de.iteratec.iteraplan.elasticeam.exception.MetamodelException;


/**
 * Helper class for filtering collections to instances of a given type.
 */
public final class ListFilterHelper {

  private ListFilterHelper() {
    //Hide constructor of utility class
  }

  /**
   * Filters a given collection to instances of a given type. if the type is comparable, the resulting list is sorted.
   * @param input the input collection
   * @param type the type of elements to be filtered
   * @return the (ordered) list of instances of the given type
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <I, O> List<O> filter(Collection<I> input, Class<O> type) {
    if (Comparable.class.isAssignableFrom(type)) {
      return (List<O>) filterAndSort(input, (Class<? extends Comparable>) type);
    }
    else {
      List<O> result = Lists.newLinkedList();
      collect(input, type, result);
      return result;
    }
  }

  private static <O, I> void collect(Collection<I> input, Class<O> type, List<O> result) {
    for (I inputElement : input) {
      if (type.isInstance(inputElement)) {
        result.add(type.cast(inputElement));
      }
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static <O extends Comparable, I> List<O> filterAndSort(Collection<I> input, Class<O> type) {
    List<O> result = Lists.newArrayList();
    collect(input, type, result);
    Collections.sort(result);
    return result;
  }

  /**
   * Finds the single element in the collection that matches the given type.
   * 
   * @param input the collection of instances
   * @param type the type to be found
   * @return the single instance of the given type or null, if no instance exists. Throws an exception, if filtering is not unique.
   */
  public static <I, O> O filterSingle(Collection<I> input, Class<O> type) {
    List<O> tmp = Lists.newLinkedList();
    collect(input, type, tmp);
    if (tmp.size() == 0) {
      return null;
    }
    else if (tmp.size() == 1) {
      return tmp.get(0);
    }
    else {
      throw new MetamodelException(ElasticeamException.GENERAL_ERROR, "Non-unique filtering");
    }
  }
}
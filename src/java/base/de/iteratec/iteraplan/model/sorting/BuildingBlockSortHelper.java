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
package de.iteratec.iteraplan.model.sorting;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.interfaces.IdentityStringEntity;


/**
 * This class provides methods which helps to sort a list of {@link BuildingBlock}'s.
 * Sometimes, in generic methods, the concrete type of a {@link BuildingBlock} is not
 * available. Sorting with the compare() method could be slow, because of hierarchical
 * orders. In this case, there could be a caching comparator returned.
 * 
 * @author sip
 */
public final class BuildingBlockSortHelper {

  private BuildingBlockSortHelper() {
    // no instance needed. Only providing static methods
  }

  /**
   * Returns a new {@link Comparator} instance which sorts entities of the given type.
   * The {@link Comparator} will keep the same order as the compare()-method of the given type.
   * @param cls the type of entity which should be sorted.
   * @return see method description.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <T extends BuildingBlock> Comparator<? super T> getDefaultComparator(Class<? extends T> cls) {
    if (InformationSystemRelease.class.isAssignableFrom(cls)) {
      return new HierarchicalEntityCachingComparator();
    }
    else if (HierarchicalEntity.class.isAssignableFrom(cls)) {
      return new OrderedHierarchicalEntityCachingComparator();
    }
    else {
      return new IdentityStringComparator();
    }
  }

  /**
   * Returns a new {@link Comparator} instance which sorts entities of the given type.
   * The {@link Comparator} will sorts entities by hierarchical name, if the
   * type is an {@link HierarchicalEntity}. Otherwise, it will return the default
   * comparator(see {@link #getDefaultComparator(Class)}).
   * @param cls the type of entity which should be sorted.
   * @return see method description.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <T extends BuildingBlock> Comparator<? super T> getHierarchicalNameComparator(Class<? extends T> cls) {
    if (HierarchicalEntity.class.isAssignableFrom(cls)) {
      return new HierarchicalEntityCachingComparator();
    }
    else {
      return getDefaultComparator(cls);
    }
  }

  /**
   * Returns a new {@link Comparator} instance which sorts entities of the given type.
   * The {@link Comparator} will sorts entities by non hierarchical name, if the
   * type is an {@link HierarchicalEntity}. Otherwise, it will return the default
   * comparator(see {@link #getDefaultComparator(Class)}).
   * @param cls the type of entity which should be sorted.
   * @return see method description.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <T extends BuildingBlock> Comparator<? super T> getNonHierarchicalNameComparator(Class<? extends T> cls) {
    if (HierarchicalEntity.class.isAssignableFrom(cls)) {
      return new NonHierarchicalEntityComparator();
    }
    else {
      return getDefaultComparator(cls);
    }
  }

  /**
   * Returns a new {@link Comparator} instance which sorts entities of the given type.
   * The {@link Comparator} will sorts entities by identity string. For {@link HierarchicalEntity}.
   * this name would be the hierarchical name.
   * @param cls the type of entity which should be sorted.
   * @return see method description.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static <T extends IdentityStringEntity> Comparator<? super T> getIdentityStringComparator(Class<? extends T> cls) {
    if (HierarchicalEntity.class.isAssignableFrom(cls)) {
      return new HierarchicalEntityCachingComparator();
    }
    else {
      return new IdentityStringComparator();
    }
  }

  /**
   * Sorts the given list with an {@link Comparator}.
   * See {@link #getDefaultComparator(Class)}.
   * @param list the list which should be sorted.
   */
  public static <T extends BuildingBlock> void sortByDefault(List<T> list) {
    if (list.isEmpty()) {
      return;
    }
    else {
      Class<? extends BuildingBlock> cls = list.get(0).getClass();
      Comparator<? super BuildingBlock> comp = getDefaultComparator(cls);
      Collections.sort(list, comp);
    }
  }

  /**
   * Sorts the given list with an {@link Comparator}.
   * See {@link #getHierarchicalNameComparator(Class)}.
   * @param list the list which should be sorted.
   */
  public static <T extends BuildingBlock> void sortByHierarchicalName(List<T> list) {
    if (list.isEmpty()) {
      return;
    }
    else {
      Class<? extends BuildingBlock> cls = list.get(0).getClass();
      Comparator<? super BuildingBlock> comp = getHierarchicalNameComparator(cls);
      Collections.sort(list, comp);
    }
  }

  /**
   * Sorts the given list with an {@link Comparator}.
   * See {@link #getNonHierarchicalNameComparator(Class)}.
   * @param list the list which should be sorted.
   */
  public static <T extends BuildingBlock> void sortByNonHierarchicalName(List<T> list) {
    if (list.isEmpty()) {
      return;
    }
    else {
      Class<? extends BuildingBlock> cls = list.get(0).getClass();
      Comparator<? super BuildingBlock> comp = getNonHierarchicalNameComparator(cls);
      Collections.sort(list, comp);
    }
  }

  /**
   * Sorts the given list with an {@link Comparator}.
   * See {@link #getIdentityStringComparator(Class)}.
   * @param list the list which should be sorted.
   */
  public static <T extends IdentityStringEntity> void sortByIdentityString(List<T> list) {
    if (list.isEmpty()) {
      return;
    }
    else {
      Class<? extends IdentityStringEntity> cls = list.get(0).getClass();
      Comparator<? super IdentityStringEntity> comp = getIdentityStringComparator(cls);
      Collections.sort(list, comp);
    }
  }

}

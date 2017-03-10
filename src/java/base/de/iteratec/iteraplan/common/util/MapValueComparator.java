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
import java.util.Comparator;
import java.util.Map;


/**
 * <p>
 * Comparator used for ordering Maps by their value instead of their key. 
 * If multiple keys with identical values exists, the order of these will be undetermined!
 * </p>
 * Note: this comparator imposes orderings that are inconsistent with equals. Thus, using .get on Maps is not possible. 
 * Only add items and iterate over the sorted map.
 */
@SuppressWarnings("rawtypes")
public class MapValueComparator<T extends Comparable> implements Comparator, Serializable {
  private static final long serialVersionUID = 1L;
  private Map<?, T> mapData = null;
  
  public MapValueComparator(Map<?, T> data) {
    mapData = data;
  }

  @SuppressWarnings("unchecked")
  public int compare(Object o1, Object o2) {
    // when both are equal, return -1, to keep both keys in the map!
    // this is inconsistent with equals!
    if (mapData.get(o1).compareTo(mapData.get(o2)) == 0) {
      return -1;
    }
    // sort descending
    return mapData.get(o2).compareTo(mapData.get(o1));
  }
}

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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import de.iteratec.iteraplan.common.Logger;


public class MapValueComparatorTest {

  private static final Logger LOGGER = Logger.getIteraplanLogger(MapValueComparatorTest.class);

  @Test
  @SuppressWarnings({ "unchecked", "boxing" })
  public void testAddSameValues() {
    Map<String, Integer> map = new HashMap<String, Integer>();
    // add more then one entry with the same value
    map.put("Test1", Integer.valueOf(1));
    map.put("Test1Again", Integer.valueOf(1));
    map.put("Test2", Integer.valueOf(2));
    map.put("Test3", Integer.valueOf(3));

    SortedMap<String, Integer> sortedMap = new TreeMap<String, Integer>(new MapValueComparator<Integer>(map));
    sortedMap.putAll(map);
    LOGGER.info(sortedMap.toString());
    // Note: Can't use sortedMap.get() here, because the comparator is inconsistent with equals
    assertSame(sortedMap.size(), 4);

    // iterate over all keys and remove them from the original map, to assure the keys are equal
    for (String key : sortedMap.keySet()) {
      map.remove(key);
    }
    assertTrue(map.isEmpty());
  }
}

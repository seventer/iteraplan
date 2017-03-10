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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;


public class ViewpointConfigurationTest {

  @Test
  public void testViewpointConfiguration() {
    //setup
    Map<String, String> testMap = Maps.newHashMap();
    testMap.put("root", "aa");
    testMap.put("root.", "nmds");
    testMap.put("root.x", "x");
    testMap.put("root.x.1", "x1");
    testMap.put("root.x.2", "x2");
    testMap.put("root.y", "y");
    testMap.put("root.y.1", "y1");
    testMap.put("root.y.2", "y2");

    //object under test
    ViewpointConfiguration vpConfig = new ViewpointConfiguration.Root(testMap);

    //assertions
    assertNull(vpConfig.get("nonexistent"));
    assertEquals("aa", vpConfig.get("root"));
    assertEquals("x", vpConfig.get("root.x"));
    assertEquals("x1", vpConfig.get("root.x.1"));
    assertEquals("x2", vpConfig.get("root.x.2"));
    assertEquals("y", vpConfig.get("root.y"));
    assertEquals("y1", vpConfig.get("root.y.1"));
    assertEquals("y2", vpConfig.get("root.y.2"));

    vpConfig = vpConfig.withPrefix("root");
    assertNull(vpConfig.get("aa"));
    assertEquals("nmds", vpConfig.get(""));
    assertEquals("x", vpConfig.get("x"));
    assertEquals("x1", vpConfig.get("x.1"));
    assertEquals("x2", vpConfig.get("x.2"));
    assertEquals("y", vpConfig.get("y"));
    assertEquals("y1", vpConfig.get("y.1"));
    assertEquals("y2", vpConfig.get("y.2"));

    vpConfig = vpConfig.withPrefix("x");
    assertNull(vpConfig.get("x"));
    assertEquals("x1", vpConfig.get("1"));
    assertEquals("x2", vpConfig.get("2"));

  }

}

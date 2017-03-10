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

import java.util.Iterator;

import junit.framework.TestCase;
import de.iteratec.iteraplan.common.util.HashBucketMatrix.Entry;


public class HashBucketMatrixTest extends TestCase {

  public void testGetBucket() {
    HashBucketMatrix<String, String, String> m = new HashBucketMatrix<String, String, String>();
    m.add("1", "1", "A");
    assertEquals(m.getBucket("1", "1").size(), 1);
    m.add("1", "1", "B");
    assertEquals(m.getBucket("1", "1").size(), 2);
    m.add("1", "2", "C");
    assertEquals(m.getBucket("1", "1").size(), 2);
    assertEquals(m.getBucket("1", "2").size(), 1);
    m.add("1", "2", "D");
    assertEquals(m.getBucket("1", "1").size(), 2);
    assertEquals(m.getBucket("1", "2").size(), 2);
    m.add("2", "1", "E");
    assertEquals(m.getBucket("1", "1").size(), 2);
    assertEquals(m.getBucket("1", "2").size(), 2);
    assertEquals(m.getBucket("2", "1").size(), 1);
    m.add("2", "1", "F");
    assertEquals(m.getBucket("1", "1").size(), 2);
    assertEquals(m.getBucket("1", "2").size(), 2);
    assertEquals(m.getBucket("2", "1").size(), 2);
  }

  public void testGetBucketNotNull() {
    HashBucketMatrix<String, String, String> m = new HashBucketMatrix<String, String, String>();
    assertNotNull(m.getBucketNotNull("1", "2"));
    assertEquals(m.getBucketNotNull("1", "2").size(), 0);
  }

  public void testValues() {
    HashBucketMatrix<String, String, String> m = new HashBucketMatrix<String, String, String>();
    m.add("1", "1", "A");
    m.add("1", "1", "B");
    m.add("1", "2", "C");
    m.add("2", "1", "D");
    m.add("2", "2", "E");
    m.add("2", "3", "E");
    assertEquals(m.values().size(), 6);
    for (Iterator<String> it = m.values().iterator(); it.hasNext();) {
      String v = it.next();
      assertTrue("A".equals(v) || "B".equals(v) || "C".equals(v) || "D".equals(v) || "E".equals(v));
    }
  }

  public void testEntrySet() {
    HashBucketMatrix<String, Integer, Boolean> m = new HashBucketMatrix<String, Integer, Boolean>();
    m.add("1", Integer.valueOf(1), Boolean.TRUE);
    m.add("1", Integer.valueOf(1), Boolean.TRUE);
    m.add("1", Integer.valueOf(1), Boolean.TRUE);
    m.add("2", Integer.valueOf(1), Boolean.TRUE);
    m.add("2", Integer.valueOf(2), Boolean.TRUE);
    m.add("2", Integer.valueOf(3), Boolean.TRUE);
    assertEquals(m.entrySet().size(), 6);
  }

  public void testRemoveKey1() {
    HashBucketMatrix<String, String, String> m = new HashBucketMatrix<String, String, String>();
    m.add("1", "1", "X");
    m.add("1", "1", "X");
    m.add("1", "2", "X");
    m.add("1", "3", "X");
    m.add("2", "1", "X");
    m.add("2", "1", "X");
    m.add("2", "5", "X");
    m.add("2", "6", "X");
    m.removeKey1("1");
    assertEquals(m.entrySet().size(), 4);
    for (Iterator<HashBucketMatrix.Entry<String, String, String>> it = m.entrySet().iterator(); it.hasNext();) {
      HashBucketMatrix.Entry<String, String, String> entry = it.next();
      assertEquals(entry.getKey1(), "2");
      assertTrue(entry.getKey2().equals("1") || entry.getKey2().equals("5") || entry.getKey2().equals("6"));
    }
  }

  public void testRemoveKey2() {
    HashBucketMatrix<String, String, String> m = new HashBucketMatrix<String, String, String>();
    m.add("1", "1", "X");
    m.add("1", "1", "X");
    m.add("1", "4", "X");
    m.add("1", "5", "X");
    m.add("2", "1", "X");
    m.add("2", "4", "X");
    m.add("2", "5", "X");
    m.add("3", "1", "X");
    m.removeKey2("1");
    assertEquals(m.entrySet().size(), 4);
    for (Iterator<HashBucketMatrix.Entry<String, String, String>> it = m.entrySet().iterator(); it.hasNext();) {
      Entry<String, String, String> entry = it.next();
      assertTrue(entry.getKey1().equals("1") || entry.getKey1().equals("2"));
      assertTrue(entry.getKey2().equals("4") || entry.getKey2().equals("5"));
    }
    assertNotNull(m.getBucket("1", "4"));
    assertNotNull(m.getBucket("1", "5"));
    assertNotNull(m.getBucket("2", "4"));
    assertNotNull(m.getBucket("2", "5"));
    assertNull(m.getBucket("3", "1"));
  }

  public void testRemove() {
    HashBucketMatrix<String, String, String> m = new HashBucketMatrix<String, String, String>();
    m.add("1", "1", "X");
    m.add("1", "1", "X");
    m.add("1", "4", "X");
    m.add("1", "5", "X");
    m.add("2", "1", "X");
    assertEquals(m.entrySet().size(), 5);
    m.remove("1", "1");
    assertEquals(m.entrySet().size(), 3);
    m.remove("1", "4");
    assertEquals(m.entrySet().size(), 2);
    m.remove("1", "5");
    assertEquals(m.entrySet().size(), 1);
    m.remove("2", "1");
    assertEquals(m.entrySet().size(), 0);
  }
}

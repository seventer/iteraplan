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
package de.iteratec.iteraplan.model.ApplicationLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 * @author rfe
 * @author mma
 */
public class InformationSystemTest {

  private static final String TEST_NAME = "testName";

  @Before
  public void setUp() throws Exception {
    // not yet used
  }

  @After
  public void tearDown() throws Exception {
    // not yet used
  }

  private InformationSystem classUnderTest = null;

  /**
   * Test method for (getter and setter)
   * {@link de.iteratec.iteraplan.model.InformationSystem#getName()},
   * {@link de.iteratec.iteraplan.model.InformationSystem#setName(java.lang.String)} and
   * {@link de.iteratec.iteraplan.model.InformationSystem#getIdentityString()}.
   */
  @Test
  public void testName() {
    classUnderTest = new InformationSystem();

    classUnderTest.setName(TEST_NAME);
    String actual = classUnderTest.getName();

    assertEquals(TEST_NAME, actual);

    classUnderTest.setName("testName\n");
    actual = classUnderTest.getName();

    assertEquals(TEST_NAME, actual);

    classUnderTest.setName("\r");
    actual = classUnderTest.getName();

    assertEquals("", actual);

    classUnderTest.setName("\ttestName\n");
    actual = classUnderTest.getName();

    assertEquals(TEST_NAME, actual);

    classUnderTest.setName(null);
    actual = classUnderTest.getName();

    assertEquals(null, actual);

    classUnderTest.setName("\r\t\n testName \r\t\n");
    actual = classUnderTest.getIdentityString();

    assertEquals(TEST_NAME, actual);
  }

  /**
   * Test method for (getter, setter and remove)
   * {@link de.iteratec.iteraplan.model.InformationSystem#setReleases()},
   * {@link de.iteratec.iteraplan.model.InformationSystem#getReleases()}
   * {@link de.iteratec.iteraplan.model.InformationSystem#addRelease()} and
   * {@link de.iteratec.iteraplan.model.InformationSystem#removeRelease(de.iteratec.iteraplan.model.InformationSystemRelease)}
   */
  @Test
  public void testReleases() {
    classUnderTest = new InformationSystem();
    classUnderTest.setName("testInfoSys");
    assertNotNull(classUnderTest.getReleases());

    // setReleases and getReleases
    Set<InformationSystemRelease> expected = hashSet();
    Set<InformationSystemRelease> actual;
    InformationSystemRelease firstISR = new InformationSystemRelease();

    expected.add(firstISR);
    classUnderTest.setReleases(expected);
    actual = classUnderTest.getReleases();

    assertEquals(expected, actual);

    // addRelease and getReleases
    classUnderTest.removeRelease(firstISR);
    InformationSystemRelease secISR = new InformationSystemRelease();
    classUnderTest.addRelease(secISR);
    actual = classUnderTest.getReleases();

    // addRelease : test the first side of the association
    assertEquals(expected, actual);

    // addRelease : test the second side of the association
    for (InformationSystemRelease isr : actual) {
      InformationSystem actualIS = isr.getInformationSystem();
      assertEquals("Fail to update both sides of the association", classUnderTest, actualIS);
    }

    // removeRelease
    classUnderTest.removeRelease(secISR);
    actual = hashSet();
    assertEquals(expected, actual);

    // removeRelease
    classUnderTest.removeRelease(firstISR);
    actual = hashSet();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystem#getTypeOfBuildingBlock()}
   * The method tests the getI18NKey() method. The test method has only meaning for the code
   * coverage.
   */
  @Test
  public void testGetTypeOfBuildingBlock() {
    classUnderTest = new InformationSystem();
    assertEquals("informationSystem.singular", classUnderTest.getTypeOfBuildingBlock().toString());
  }

}

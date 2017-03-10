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
package de.iteratec.iteraplan.model.TechnicalLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * @author rfe
 */
public class TechnicalComponentTest {

  @Before
  public void setUp() throws Exception {
    // not yet used
  }

  @After
  public void tearDown() throws Exception {
    // not yet used
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.TechnicalComponent#setName()},
   * {@link de.iteratec.iteraplan.model.TechnicalComponent#getName()} and
   * {@link de.iteratec.iteraplan.model.TechnicalComponent#getIdentityString()}. The test method has
   * meaning for the code coverage.
   */
  @Test
  public void testName() {
    TechnicalComponent classUnderTest = new TechnicalComponent();
    assertNull(classUnderTest.getName());
    String expectedName = "testName TC";

    classUnderTest.setName(expectedName);
    assertEquals(expectedName, classUnderTest.getName());
    assertEquals(expectedName, classUnderTest.getIdentityString());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponent#isAvailableForInterfaces()} and
   * {@link de.iteratec.iteraplan.model.TechnicalComponent#setAvailableForInterfaces(boolean)}
   * The test method has meaning for the code coverage.
   */
  @Test
  public void testAvailableForInterfaces() {
    TechnicalComponent classUnderTest = new TechnicalComponent();
    classUnderTest.setAvailableForInterfaces(true);
    assertTrue(classUnderTest.isAvailableForInterfaces());
    classUnderTest.setAvailableForInterfaces(false);
    assertFalse(classUnderTest.isAvailableForInterfaces());
  }

  /**
   * Test method for (setter and getter): The attribute releases must be at the beginning empty but
   * not null. Then two sets are created. The fist one is empty and the second one is null. At the
   * end the two sets must be equals.
   * {@link de.iteratec.iteraplan.model.TechnicalComponent#getReleases()} and
   * {@link de.iteratec.iteraplan.model.TechnicalComponent#setReleases(java.util.Set)}.
   */
  @Test
  public void testReleases() {
    TechnicalComponent classUnderTest = new TechnicalComponent();
    Assert.assertNotNull(classUnderTest.getReleases());

    Set<TechnicalComponentRelease> expected = new HashSet<TechnicalComponentRelease>();
    Set<TechnicalComponentRelease> actual;

    expected.add(new TechnicalComponentRelease());

    classUnderTest.setReleases(expected);
    actual = classUnderTest.getReleases();

    Assert.assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponent#addRelease(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * The method tests if the addRelease adds properly one TechnicalComponentRelease
   */
  @Test
  public void testAddReleasesCaseNotNull() {
    TechnicalComponent classUnderTest = new TechnicalComponent();
    Set<TechnicalComponentRelease> expectedTCRs = hashSet();
    TechnicalComponentRelease expected = new TechnicalComponentRelease();
    expectedTCRs.add(expected);
    classUnderTest.addRelease(expected);
    assertEquals(classUnderTest.getReleases(), expectedTCRs);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponent#addRelease(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * The method tests if the addRelease properly adds one TechnicalComponentRelease
   */
  @Test(expected = IllegalArgumentException.class)
  public void testtestAddReleasesCaseNull() {
    TechnicalComponent classUnderTest = new TechnicalComponent();
    Set<TechnicalComponentRelease> expectedTCRs = hashSet();
    expectedTCRs.add(null);
    classUnderTest.addRelease(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponent#removeRelease(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * The method tests if the removeRelease properly removes one TechnicalComponentRelease
   */
  @Test
  public void testRemoveRelease() {
    TechnicalComponent classUnderTest = new TechnicalComponent();

    Set<TechnicalComponentRelease> expectedTCRs = hashSet();

    TechnicalComponentRelease someTcr = new TechnicalComponentRelease();
    classUnderTest.addRelease(someTcr);
    classUnderTest.removeRelease(someTcr);

    assertEquals(classUnderTest.getReleases(), expectedTCRs);
  }

}

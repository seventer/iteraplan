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
package de.iteratec.iteraplan.model;

import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.Test;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;


public class InfrastructureElementTest {

  private static final String UPDATE_BOTH_SIDES_ERROR_MSG = "Fail to update both sides of the association";

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#addBaseComponents(java.util.Set)}
   * The method tests if the addBaseComponents throws IllegalArgumentException if the user tries to add
   * null in the set baseComponents.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testBaseComponentsAddNull() {
    InfrastructureElement classUnderTest = new InfrastructureElement();
    Set<InfrastructureElement> setISD = hashSet();
    setISD.add(null);

    classUnderTest.addBaseComponents(setISD);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#addBaseComponent(de.iteratec.iteraplan.model.InfrastructureElement)}
   * The method tests if the addBaseComponent throws IllegalArgumentException if the user tries to add
   * null as element.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testBaseComponentAddNull() {
    InfrastructureElement classUnderTest = new InfrastructureElement();

    classUnderTest.addBaseComponent(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#getBaseComponents()}
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#setBaseComponents(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#addBaseComponent(de.iteratec.iteraplan.model.InfrastructureElement)}
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#addBaseComponents(java.util.Set)}.
   * The method tests if the addBaseComponents, addBaseComponent and setBaseComponents add correctly
   * a BaseComponents/BaseComponent in the set of the baseComponents.
   */
  @Test
  public void testBaseComponents() {
    InfrastructureElement classUnderTest = new InfrastructureElement();
    assertNotNull(classUnderTest.getBaseComponents());

    // set for the second side of the association
    Set<InfrastructureElement> expectedPCs = hashSet();
    expectedPCs.add(classUnderTest);

    // initialization set
    Set<InfrastructureElement> expected = hashSet();

    // add some elements to test addBaseComponents
    InfrastructureElement firstEl = new InfrastructureElement();
    expected.add(firstEl);
    InfrastructureElement secEl = new InfrastructureElement();
    expected.add(secEl);
    InfrastructureElement thirdEl = new InfrastructureElement();
    expected.add(thirdEl);

    classUnderTest.addBaseComponents(expected);
    Set<InfrastructureElement> actual = classUnderTest.getBaseComponents();

    // addBasedComponents : test the first side of the association
    assertEquals(expected, actual);

    // addBasedComponents : test the second side of the association
    for (InfrastructureElement bc : actual) {
      Set<InfrastructureElement> actualPCs = bc.getParentComponents();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPCs.size(), actualPCs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPCs, actualPCs);
    }

    // add fourth element in the set to test addBaseComponent
    InfrastructureElement fourthEl = new InfrastructureElement();
    classUnderTest.addBaseComponent(fourthEl);
    expected.add(fourthEl);
    actual = classUnderTest.getBaseComponents();

    // addBasedComponent : test the first side of the association
    assertEquals(expected, actual);

    // addBasedComponent : test the second side of the association
    for (InfrastructureElement bc : actual) {
      Set<InfrastructureElement> actualPCs = bc.getParentComponents();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPCs.size(), actualPCs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPCs, actualPCs);
    }

    // add fifth element in the set to test setBaseComponents
    InfrastructureElement fifthEl = new InfrastructureElement();
    expected.add(fifthEl);
    classUnderTest.setBaseComponents(expected);
    actual = classUnderTest.getBaseComponents();

    // setBaseComponents : test the first side of the association
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#addBaseComponent(de.iteratec.iteraplan.model.InfrastructureElement)}
   * Tests if it is possible to generate a base component cycle with the aforementioned method.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddBaseComponentCycle() {
    InfrastructureElement classUnderTest = new InfrastructureElement();
    classUnderTest.setId(Integer.valueOf(1));

    InfrastructureElement base = new InfrastructureElement();
    base.setId(Integer.valueOf(2));
    classUnderTest.addBaseComponent(base);

    base.addBaseComponent(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#addBaseComponent(de.iteratec.iteraplan.model.InfrastructureElement)}
   * Tests if it is possible to generate a base component cycle with the aforementioned method.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddBaseComponentTransientCycle() {
    InfrastructureElement classUnderTest = new InfrastructureElement();
    classUnderTest.setId(Integer.valueOf(1));

    InfrastructureElement base = new InfrastructureElement();
    base.setId(Integer.valueOf(2));

    InfrastructureElement base2 = new InfrastructureElement();
    base2.setId(Integer.valueOf(3));

    // create baseComponentHierarchy
    base.addBaseComponent(base2);
    base2.addBaseComponent(classUnderTest);

    // this will fail
    classUnderTest.addBaseComponent(base);
  }

}

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
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 * JUnit test for the {@link InformationSystemRelease} class. 
 * Other JUnit tests are implemented in {@link InformationSystemReleaseTest} class.
 */
public class InformationSystemRelease4BMTest {

  private static final String UPDATE_BOTH_SIDES_ERROR_MSG = "Fail to update both sides of the association";

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMappings(java.util.Set)}
   * The method tests if the addBusinessMappings throws IllegalArgumentException if the user tries to
   * add set with null in it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testBusinessMappingsFirstCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    Set<BusinessMapping> expected = hashSet();
    expected.add(null);

    classUnderTest.addBusinessMappings(expected);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMapping(java.util.Set)}
   * The method tests if the addBusinessMapping throws IllegalArgumentException if the user tries to add
   * null in the businessMappings.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testBusinessMappingsSecCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    classUnderTest.addBusinessMapping(null);
  }

  /**
   * Test method for (add and getter)
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMapping(de.iteratec.iteraplan.model.BusinessMapping)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMappings(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessMappings()} The
   * attribute businessMapping must be at the beginning empty but not null. At the beginning the set
   * should be empty but not null. Then two sets are created. At the end the two sets should be
   * equal.No BusinessUnit, BusinessProcess or BusinessFunction are set for the BusinessMapping.
   */
  @Test
  public void testBusinessMappingsCaseNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getBusinessMappings());

    Set<BusinessMapping> expected = hashSet();
    Set<BusinessMapping> actual;

    // TestBusinessMapping, TestBusinessUnit, TestBusinessProcess, TestBusinessFunction
    BusinessMapping businessMapping = new BusinessMapping();

    businessMapping.setBusinessUnit(null);
    businessMapping.setBusinessProcess(null);

    expected.add(businessMapping);

    classUnderTest.addBusinessMappings(expected);
    classUnderTest.addBusinessMapping(businessMapping);

    actual = classUnderTest.getBusinessMappings();

    assertEquals(expected, actual);

    // test for the other side of the association
    for (BusinessMapping bm : classUnderTest.getBusinessMappings()) {
      assertNull(bm.getBusinessUnit());
      assertNull(bm.getBusinessProcess());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, bm.getInformationSystemRelease(), classUnderTest);
    }
  }

  /**
   * Test method for (add and getter)
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMapping(de.iteratec.iteraplan.model.BusinessMapping)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMappings(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessMappings()} The
   * attribute businessMapping must be at the beginning empty but not null. At the beginning the set
   * should be empty but not null. Then two sets are created. At the end the two sets should be
   * equal.Only BusinessProcess is set for the BusinessMapping.
   */
  @Test
  public void testBusinessMappingsCaseTwo() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getBusinessMappings());

    Set<BusinessMapping> expectedBMForBP = hashSet();
    Set<BusinessMapping> expected = hashSet();
    Set<BusinessMapping> actual;

    // TestBusinessMapping, TestBusinessUnit, TestBusinessProcess, TestBusinessFunction
    BusinessMapping businessMapping = new BusinessMapping();
    BusinessProcess businessProcess = new BusinessProcess();

    businessMapping.setBusinessUnit(null);
    businessMapping.addBusinessProcess(businessProcess);

    expectedBMForBP.add(businessMapping);
    expected.add(businessMapping);

    classUnderTest.addBusinessMappings(expected);
    classUnderTest.addBusinessMapping(businessMapping);

    actual = classUnderTest.getBusinessMappings();

    assertEquals(expected, actual);
    assertEquals(expectedBMForBP, businessProcess.getBusinessMappings());

    // test for the other side of the association
    for (BusinessMapping bm : classUnderTest.getBusinessMappings()) {
      assertNull(bm.getBusinessUnit());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, businessProcess, bm.getBusinessProcess());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, bm.getInformationSystemRelease(), classUnderTest);
    }
  }

  /**
   * Test method for (add and getter)
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMapping(de.iteratec.iteraplan.model.BusinessMapping)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMappings(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessMappings()} The
   * attribute businessMapping must be at the beginning empty but not null. At the beginning the set
   * should be empty but not null. Then two sets are created. At the end the two sets should be
   * equal.Only BusinessUnit is set for the BusinessMapping.
   */
  @Test
  public void testBusinessMappingsCaseThree() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getBusinessMappings());

    Set<BusinessMapping> expectedBMForBU = hashSet();
    Set<BusinessMapping> expected = hashSet();
    Set<BusinessMapping> actual;

    // TestBusinessMapping, TestBusinessUnit, TestBusinessProcess, TestBusinessFunction
    BusinessMapping businessMapping = new BusinessMapping();
    BusinessUnit businessUnit = new BusinessUnit();

    businessMapping.setBusinessProcess(null);
    businessMapping.addBusinessUnit(businessUnit);

    expectedBMForBU.add(businessMapping);
    expected.add(businessMapping);

    classUnderTest.addBusinessMappings(expected);
    classUnderTest.addBusinessMapping(businessMapping);

    actual = classUnderTest.getBusinessMappings();

    assertEquals(expected, actual);
    assertEquals(expectedBMForBU, businessUnit.getBusinessMappings());

    // test for the other side of the association
    for (BusinessMapping bm : classUnderTest.getBusinessMappings()) {
      assertNull(bm.getBusinessProcess());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, businessUnit, bm.getBusinessUnit());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, bm.getInformationSystemRelease(), classUnderTest);
    }
  }

  /**
   * Test method for (add and getter)
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMapping(de.iteratec.iteraplan.model.BusinessMapping)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMappings(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessMappings()} The
   * attribute businessMapping must be at the beginning empty but not null. At the beginning the set
   * should be empty but not null. Then two sets are created. At the end the two sets should be
   * equal.Only BusinessFunction and BusinessProcess are set for the BusinessMapping.
   */
  @Test
  public void testBusinessMappingsCaseFour() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getBusinessMappings());

    Set<BusinessMapping> expectedBMForBFAndBP = hashSet();

    Set<BusinessMapping> expected = hashSet();
    Set<BusinessMapping> actual;

    // TestBusinessMapping, TestBusinessUnit, TestBusinessProcess, TestBusinessFunction
    BusinessMapping businessMapping = new BusinessMapping();
    BusinessProcess businessProcess = new BusinessProcess();

    businessMapping.setBusinessUnit(null);
    businessMapping.addBusinessProcess(businessProcess);

    expectedBMForBFAndBP.add(businessMapping);
    expected.add(businessMapping);

    classUnderTest.addBusinessMappings(expected);
    classUnderTest.addBusinessMapping(businessMapping);

    actual = classUnderTest.getBusinessMappings();

    assertEquals(expected, actual);
    assertEquals(expectedBMForBFAndBP, businessProcess.getBusinessMappings());

    // test for the other side of the association
    for (BusinessMapping bm : classUnderTest.getBusinessMappings()) {
      assertNull(bm.getBusinessUnit());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, businessProcess, bm.getBusinessProcess());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, bm.getInformationSystemRelease(), classUnderTest);
    }
  }

  /**
   * Test method for (add and getter)
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMapping(de.iteratec.iteraplan.model.BusinessMapping)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMappings(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessMappings()} The
   * attribute businessMapping must be at the beginning empty but not null. At the beginning the set
   * should be empty but not null. Then two sets are created. At the end the two sets should be
   * equal.Only BusinessFunction and BusinessUnit are set for the BusinessMapping.
   */
  @Test
  public void testBusinessMappingsCaseFive() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getBusinessMappings());

    Set<BusinessMapping> expectedBMForBFAndBU = hashSet();

    Set<BusinessMapping> expected = hashSet();
    Set<BusinessMapping> actual;

    // TestBusinessMapping, TestBusinessUnit, TestBusinessProcess, TestBusinessFunction
    BusinessMapping businessMapping = new BusinessMapping();
    BusinessUnit businessUnit = new BusinessUnit();

    businessMapping.setBusinessProcess(null);
    businessMapping.addBusinessUnit(businessUnit);

    expectedBMForBFAndBU.add(businessMapping);
    expected.add(businessMapping);

    classUnderTest.addBusinessMappings(expected);
    classUnderTest.addBusinessMapping(businessMapping);

    actual = classUnderTest.getBusinessMappings();

    assertEquals(expected, actual);
    assertEquals(expectedBMForBFAndBU, businessUnit.getBusinessMappings());

    // test for the other side of the association
    for (BusinessMapping bm : classUnderTest.getBusinessMappings()) {
      assertNull(bm.getBusinessProcess());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, businessUnit, bm.getBusinessUnit());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, bm.getInformationSystemRelease(), classUnderTest);
    }
  }

  /**
   * Test method for (add and getter)
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMapping(de.iteratec.iteraplan.model.BusinessMapping)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMappings(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessMappings()} The
   * attribute businessMapping must be at the beginning empty but not null. At the beginning the set
   * should be empty but not null. Then two sets are created. At the end the two sets should be
   * equal.Only BusinessUnit and BusinessProcess are set for the BusinessMapping.
   */
  @Test
  public void testBusinessMappingsCaseSix() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getBusinessMappings());

    Set<BusinessMapping> expectedBMForBUAndBP = hashSet();

    Set<BusinessMapping> expected = hashSet();
    Set<BusinessMapping> actual;

    // TestBusinessMapping, TestBusinessUnit, TestBusinessProcess, TestBusinessFunction
    BusinessMapping businessMapping = new BusinessMapping();
    BusinessUnit businessUnit = new BusinessUnit();
    BusinessProcess businessProcess = new BusinessProcess();

    businessMapping.addBusinessProcess(businessProcess);
    businessMapping.addBusinessUnit(businessUnit);

    expectedBMForBUAndBP.add(businessMapping);
    expected.add(businessMapping);

    classUnderTest.addBusinessMappings(expected);
    classUnderTest.addBusinessMapping(businessMapping);

    actual = classUnderTest.getBusinessMappings();

    assertEquals(expected, actual);
    assertEquals(expectedBMForBUAndBP, businessUnit.getBusinessMappings());
    assertEquals(expectedBMForBUAndBP, businessProcess.getBusinessMappings());

    // test for the other side of the association
    for (BusinessMapping bm : classUnderTest.getBusinessMappings()) {
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, businessProcess, bm.getBusinessProcess());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, businessUnit, bm.getBusinessUnit());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, bm.getInformationSystemRelease(), classUnderTest);
    }
  }

  /**
   * Test method for (add and getter)
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMapping(de.iteratec.iteraplan.model.BusinessMapping)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessMappings(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessMappings()} The
   * attribute businessMapping must be at the beginning empty but not null. At the beginning the set
   * should be empty but not null. Then two sets are created. At the end the two sets should be
   * equal.BusinessFunction, BusinessProcess and BusinessUnit are set for the BusinessMapping.
   */
  @Test
  public void testBusinessMappingsCaseSeven() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getBusinessMappings());

    Set<BusinessMapping> expectedBMForBFAndBPAndBU = hashSet();

    Set<BusinessMapping> expected = hashSet();
    Set<BusinessMapping> actual;

    // TestBusinessMapping, TestBusinessUnit, TestBusinessProcess, TestBusinessFunction
    BusinessMapping businessMapping = new BusinessMapping();
    BusinessProcess businessProcess = new BusinessProcess();
    BusinessUnit businessUnit = new BusinessUnit();

    businessMapping.addBusinessUnit(businessUnit);
    businessMapping.addBusinessProcess(businessProcess);

    expectedBMForBFAndBPAndBU.add(businessMapping);
    expected.add(businessMapping);

    classUnderTest.addBusinessMappings(expected);
    classUnderTest.addBusinessMapping(businessMapping);

    actual = classUnderTest.getBusinessMappings();

    assertEquals(expected, actual);
    assertEquals(expectedBMForBFAndBPAndBU, businessProcess.getBusinessMappings());
    assertEquals(expectedBMForBFAndBPAndBU, businessUnit.getBusinessMappings());

    // test for the other side of the association
    for (BusinessMapping bm : classUnderTest.getBusinessMappings()) {
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, businessUnit, bm.getBusinessUnit());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, businessProcess, bm.getBusinessProcess());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, bm.getInformationSystemRelease(), classUnderTest);
    }
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessMapping(java.lang.Integer)}
   * The test method has only meaning for the code coverage.
   */
  @Test
  public void testGetBusinessMappingNotEmpty() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getBusinessMappings());

    BusinessMapping actual;

    BusinessMapping expected = new BusinessMapping();
    expected.setId(Integer.valueOf(1));

    classUnderTest.addBusinessMapping(expected);

    actual = classUnderTest.getBusinessMapping(Integer.valueOf(1));

    assertEquals(expected, actual);
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessMapping(java.lang.Integer)}
   * The test method has only meaning for the code coverage.
   */
  @Test
  public void testGetBusinessMappingEmpty() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getBusinessMappings());

    BusinessMapping actual = null;
    BusinessMapping expected = null;

    actual = classUnderTest.getBusinessMapping(Integer.valueOf(1));
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#removeBusinessMappings()} The
   * method tests if the removeBusinessMappings() successfully removes all the BusinessMappings with
   * their InformationSystemReleases.
   */
  @Test
  public void testRemoveBusinessMappings() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    Set<BusinessMapping> expected = hashSet();
    List<BusinessMapping> bms = createSimpleBusinessMappings(classUnderTest);

    classUnderTest.removeBusinessMappings();
    Set<BusinessMapping> actual = classUnderTest.getBusinessMappings();
    assertEquals(expected, actual);

    InformationSystemRelease expectedEmpty = null;
    InformationSystemRelease actualISR = bms.get(0).getInformationSystemRelease();

    // Test for the other side of the association
    assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedEmpty, actualISR);
  }

  private List<BusinessMapping> createSimpleBusinessMappings(InformationSystemRelease classUnderTest) {
    List<BusinessMapping> bms = new ArrayList<BusinessMapping>();

    // add some elements
    BusinessMapping firstEl = new BusinessMapping();
    firstEl.setId(Integer.valueOf(500));
    firstEl.addInformationSystemRelease(classUnderTest);
    classUnderTest.addBusinessMapping(firstEl);
    bms.add(firstEl);

    BusinessMapping secEl = new BusinessMapping();
    secEl.setId(Integer.valueOf(500));
    bms.add(secEl);

    BusinessMapping thirdEl = new BusinessMapping();
    bms.add(thirdEl);

    BusinessMapping fourthEl = new BusinessMapping();
    fourthEl.setId(Integer.valueOf(50));
    bms.add(fourthEl);
    return bms;
  }

}

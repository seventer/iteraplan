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
package de.iteratec.iteraplan.model.BusinessLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.Product;


/**
 * @author rfe
 */
@SuppressWarnings({ "boxing", "PMD.TooManyMethods" })
public class BusinessDomainTest {

  private static final String UPDATE_BOTH_SIDES_ERROR_MSG = "Fail to update both sides of the association";

  private static final String TEST_NAME_1                 = "first";
  private static final String TEST_NAME_2                 = "sec";

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    // not yet used
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    // not yet used
  }

  private BusinessDomain classUnderTest = null;

  /**
   * Test method for (getter)
   * {@link de.iteratec.iteraplan.model.BusinessDomain#getTypeOfBuildingBlock()} The method has only
   * meaning for the code coverage.
   */
  @Test
  public void testgetTypeOfBuildingBlock() {
    classUnderTest = new BusinessDomain();

    assertEquals("businessDomain.singular", classUnderTest.getTypeOfBuildingBlock().toString());
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessDomain#setBusinessFunctions(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.BusinessDomain#getBusinessFunctions()} The attribute
   * businessFunctions must be at the beginning empty but not null. Then two sets are created. The
   * fist one is empty and the second one is null. At the end the two sets must be equals. The
   * method has meaning only for the code coverage.
   */
  @Test
  public void testBusinessFunctions() {
    classUnderTest = new BusinessDomain();
    assertNotNull(classUnderTest.getBusinessFunctions());

    Set<BusinessFunction> expected = hashSet();
    expected.add(new BusinessFunction());

    classUnderTest.setBusinessFunctions(expected);
    Set<BusinessFunction> actual = classUnderTest.getBusinessFunctions();

    assertEquals(expected, actual);
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessDomain#setBusinessProcesses(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.BusinessDomain#getBusinessProcesses()} The attribute
   * businessProcesses must be at the beginning empty but not null. Then two sets are created. The
   * fist one is empty and the second one is null. At the end the two sets must be equals. The
   * method has meaning only for the code coverage.
   */
  @Test
  public void testBusinessProcesses() {
    classUnderTest = new BusinessDomain();
    assertNotNull(classUnderTest.getBusinessProcesses());

    Set<BusinessProcess> expected = hashSet();
    expected.add(new BusinessProcess());

    classUnderTest.setBusinessProcesses(expected);
    Set<BusinessProcess> actual = classUnderTest.getBusinessProcesses();

    assertEquals(expected, actual);
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessDomain#setBusinessObjects(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.BusinessDomain#getBusinessObjects()} The attribute
   * businessObjects must be at the beginning empty but not null. Then two sets are created. The
   * fist one is empty and the second one is null. At the end the two sets must be equals. The
   * method has meaning only for the code coverage.
   */
  @Test
  public void testBusinessObjects() {
    classUnderTest = new BusinessDomain();
    assertNotNull(classUnderTest.getBusinessObjects());

    Set<BusinessObject> expected = hashSet();
    expected.add(new BusinessObject());

    classUnderTest.setBusinessObjects(expected);
    Set<BusinessObject> actual = classUnderTest.getBusinessObjects();

    assertEquals(expected, actual);
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessDomain#setProducts(java.util.Set)}
   *{@link de.iteratec.iteraplan.model.BusinessDomain#getProducts()} The attribute products must be
   * at the beginning empty but not null. Then two sets are created. The fist one is empty and the
   * second one is null. At the end the two sets must be equals. The method has meaning only for the
   * code coverage.
   */
  @Test
  public void testProducts() {
    classUnderTest = new BusinessDomain();
    assertNotNull(classUnderTest.getProducts());

    Set<Product> expected = hashSet();
    expected.add(new Product());

    classUnderTest.setProducts(expected);
    Set<Product> actual = classUnderTest.getProducts();

    assertEquals(expected, actual);
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessDomain#setBusinessUnits(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.BusinessDomain#getBusinessUnits()} The attribute
   * businessUnits must be at the beginning empty but not null. Then two sets are created. The fist
   * one is empty and the second one is null. At the end the two sets must be equals. The method has
   * meaning only for the code coverage.
   */
  @Test
  public void testBusinessUnits() {
    classUnderTest = new BusinessDomain();
    assertNotNull(classUnderTest.getBusinessUnits());

    Set<BusinessUnit> expected = hashSet();
    expected.add(new BusinessUnit());

    classUnderTest.setBusinessUnits(expected);
    Set<BusinessUnit> actual = classUnderTest.getBusinessUnits();

    assertEquals(expected, actual);
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessDomain#setChildren(java.util.List)}
   * {@link de.iteratec.iteraplan.model.BusinessDomain#getChildren()} The attribute children must be
   * at the beginning empty but not null. Then two sets are created. The fist one is empty and the
   * second one is null. At the end the two sets must be equals. The method has meaning only for the
   * code coverage.
   */
  @Test
  public void testGetChildren() {
    classUnderTest = new BusinessDomain();
    assertNotNull(classUnderTest.getChildren());

    List<BusinessDomain> expected = arrayList();
    expected.add(new BusinessDomain());

    classUnderTest.setChildren(expected);
    List<BusinessDomain> actual = classUnderTest.getChildren();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addBusinessFunctions(java.util.Set)} The
   * method tests if the addBusinessFunctions adds properly an empty set with BusinessFunctions.
   */
  @Test
  public void testAddBusinessFunctionsCaseEmptySet() {
    classUnderTest = new BusinessDomain();
    Set<BusinessFunction> expectedBFunctions = hashSet();

    classUnderTest.addBusinessFunctions(expectedBFunctions);
    assertEquals(classUnderTest.getBusinessFunctions(), expectedBFunctions);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addBusinessFunctions(java.util.Set)} The
   * method tests if the addBusinessFunctions adds properly set with BusinessFunctions. The both
   * sides of the association should be properly set.
   */
  @Test
  public void testAddBusinessFunctionsFirstCaseNotEmptySet() {
    classUnderTest = new BusinessDomain();

    // set to test the second side of the association
    Set<BusinessDomain> expectedBDs = hashSet();
    expectedBDs.add(classUnderTest);

    // initialization set
    Set<BusinessFunction> expectedBFs = hashSet();
    // add some elements
    BusinessFunction firstBF = new BusinessFunction();
    firstBF.setId(Integer.valueOf(1));
    expectedBFs.add(firstBF);

    BusinessFunction secBF = new BusinessFunction();
    secBF.setId(Integer.valueOf(2));
    expectedBFs.add(secBF);

    classUnderTest.addBusinessFunctions(expectedBFs);
    Set<BusinessFunction> actualBFs = classUnderTest.getBusinessFunctions();
    assertEquals(expectedBFs, actualBFs);

    // test the second side of the association
    for (BusinessFunction bf : actualBFs) {
      Set<BusinessDomain> actualBDs = bf.getBusinessDomains();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBDs, actualBDs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addBusinessFunctions(java.util.Set)} The
   * method tests if the addBusinessFunctions adds properly set with BusinessFunctions. The both
   * sides of the association should be properly set.
   */
  @Test
  public void testAddBusinessFunctionsSecCaseNotEmptySet() {
    classUnderTest = new BusinessDomain();

    // set to test the second side of the association
    Set<BusinessDomain> expectedBDs = hashSet();
    expectedBDs.add(classUnderTest);

    // initialization set
    Set<BusinessFunction> expectedBFs = hashSet();
    // add some elements
    BusinessFunction firstBF = new BusinessFunction();
    expectedBFs.add(firstBF);
    BusinessFunction secBF = new BusinessFunction();
    expectedBFs.add(secBF);

    classUnderTest.addBusinessFunctions(expectedBFs);
    Set<BusinessFunction> actualBFs = classUnderTest.getBusinessFunctions();
    assertEquals(expectedBFs, actualBFs);

    // test the second side of the association
    for (BusinessFunction bf : actualBFs) {
      Set<BusinessDomain> actualBDs = bf.getBusinessDomains();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBDs, actualBDs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addBusinessProcesses(java.util.Set)} The
   * method tests if the addBusinessFunctions adds properly an empty set with BusinessFunctions. The
   * both sides of the association should be properly set.
   */
  @Test
  public void testAddBusinessProcessesCaseEmptySet() {
    classUnderTest = new BusinessDomain();
    Set<BusinessProcess> expectedBProcesses = hashSet();

    classUnderTest.addBusinessProcesses(expectedBProcesses);
    assertEquals(classUnderTest.getBusinessProcesses(), expectedBProcesses);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addBusinessProcesses(java.util.Set)} The
   * method tests if the addBusinessFunctions adds properly set with BusinessFunctions.
   */
  @Test
  public void testAddBusinessProcessesFirstCaseNotEmptySet() {
    classUnderTest = new BusinessDomain();

    // set to test the second side of the association
    Set<BusinessDomain> expectedBDs = hashSet();
    expectedBDs.add(classUnderTest);

    // initialization set
    Set<BusinessProcess> expectedBPs = hashSet();
    // put some elements
    BusinessProcess firstBP = new BusinessProcess();
    firstBP.setId(Integer.valueOf(1));
    firstBP.setName(TEST_NAME_1);
    expectedBPs.add(firstBP);

    BusinessProcess secBP = new BusinessProcess();
    secBP.setId(Integer.valueOf(2));
    secBP.setName(TEST_NAME_2);
    expectedBPs.add(secBP);

    classUnderTest.addBusinessProcesses(expectedBPs);

    Set<BusinessProcess> actualBPs = classUnderTest.getBusinessProcesses();
    assertEquals(expectedBPs, actualBPs);

    // test the second side of the association
    for (BusinessProcess bp : actualBPs) {
      Set<BusinessDomain> actualBDs = bp.getBusinessDomains();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBDs, actualBDs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addBusinessProcesses(java.util.Set)} The
   * method tests if the addBusinessProcesses adds properly set with BusinessDomains. The both sides
   * of the association should be properly set.
   */
  @Test
  public void testAddBusinessProcessesSecNotEmptySet() {
    classUnderTest = new BusinessDomain();

    // set to test the second side of the association
    Set<BusinessDomain> expectedBDs = hashSet();
    expectedBDs.add(classUnderTest);

    // initialization set
    Set<BusinessProcess> expectedBPs = hashSet();
    BusinessProcess firstBP = new BusinessProcess();
    expectedBPs.add(firstBP);
    BusinessProcess secBP = new BusinessProcess();
    expectedBPs.add(secBP);

    classUnderTest.addBusinessProcesses(expectedBPs);

    Set<BusinessProcess> actualBPs = classUnderTest.getBusinessProcesses();
    assertEquals(expectedBPs, actualBPs);

    // test the second side of the association
    for (BusinessProcess bp : actualBPs) {
      Set<BusinessDomain> actualBDs = bp.getBusinessDomains();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBDs, actualBDs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addBusinessObjects(java.util.Set)} The method
   * tests if the addBusinessObjects adds properly set with BusinessObjects.
   */
  @Test
  public void testAddBusinessObjectsCaseEmptySet() {
    classUnderTest = new BusinessDomain();
    Set<BusinessObject> expectedBObjects = hashSet();

    classUnderTest.addBusinessObjects(expectedBObjects);
    assertEquals(classUnderTest.getBusinessObjects(), expectedBObjects);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addBusinessObjects(java.util.Set)} The method
   * tests if the addBusinessObjects adds properly set with BusinessObjects. The both sides of the
   * association should be properly set.
   */
  @Test
  public void testAddBusinessObjectsFirstCaseNotEmptySet() {
    classUnderTest = new BusinessDomain();

    // set to test the second side of the association
    Set<BusinessDomain> expectedBDs = hashSet();
    expectedBDs.add(classUnderTest);

    // initialization set
    Set<BusinessObject> expectedBOs = hashSet();
    // set some elements
    BusinessObject firstBO = new BusinessObject();
    firstBO.setId(Integer.valueOf(1));
    firstBO.setName(TEST_NAME_1);
    expectedBOs.add(firstBO);

    BusinessObject secBO = new BusinessObject();
    secBO.setId(Integer.valueOf(2));
    secBO.setName(TEST_NAME_2);
    expectedBOs.add(secBO);

    classUnderTest.addBusinessObjects(expectedBOs);

    Set<BusinessObject> actualBOs = classUnderTest.getBusinessObjects();
    assertEquals(expectedBOs, actualBOs);

    // test the second side of the association
    for (BusinessObject bo : actualBOs) {
      Set<BusinessDomain> actualBDs = bo.getBusinessDomains();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBDs, actualBDs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addBusinessObjects(java.util.Set)} The method
   * tests if the addBusinessObjects adds properly set with BusinessObjects. The both sides of the
   * association should be properly set.
   */
  @Test
  public void testAddBusinessObjectsSecCaseNotEmptySet() {
    classUnderTest = new BusinessDomain();

    // set to test the second side of the association
    Set<BusinessDomain> expectedBDs = hashSet();
    expectedBDs.add(classUnderTest);

    // initialization set
    Set<BusinessObject> expectedBOs = hashSet();
    // set some elements
    BusinessObject firstBO = new BusinessObject();
    expectedBOs.add(firstBO);
    BusinessObject secBO = new BusinessObject();
    expectedBOs.add(secBO);

    classUnderTest.addBusinessObjects(expectedBOs);
    Set<BusinessObject> actualBOs = classUnderTest.getBusinessObjects();
    assertEquals(expectedBOs, actualBOs);

    // test the second side of the association
    for (BusinessObject bo : actualBOs) {
      Set<BusinessDomain> actualBDs = bo.getBusinessDomains();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBDs, actualBDs);
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessDomain#addProducts(java.util.Set)}
   * The method tests if the addProducts adds properly set with BusinessProducts.
   */
  @Test
  public void testAddProductsCaseEmptyList() {
    classUnderTest = new BusinessDomain();
    Set<Product> expectedProducts = hashSet();

    classUnderTest.addProducts(expectedProducts);
    assertEquals(classUnderTest.getBusinessObjects(), expectedProducts);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessDomain#addProducts(java.util.Set)}
   * The method tests if the addProducts adds properly set with BusinessProducts. The both sides of
   * the association should be properly set.
   */
  @Test
  public void testAddProductsFirstCaseNotEmptyList() {
    classUnderTest = new BusinessDomain();

    // set to test the second side of the association
    Set<BusinessDomain> expectedBDs = hashSet();
    expectedBDs.add(classUnderTest);

    // initialization set
    Set<Product> expectedPs = hashSet();
    // add some elements
    Product firstP = new Product();
    firstP.setId(Integer.valueOf(1));
    firstP.setName(TEST_NAME_1);
    expectedPs.add(firstP);

    Product secP = new Product();
    secP.setId(Integer.valueOf(2));
    secP.setName(TEST_NAME_2);
    expectedPs.add(secP);

    Product thirdP = new Product();
    thirdP.setId(Integer.valueOf(2));
    thirdP.setName("third");
    expectedPs.add(thirdP);

    classUnderTest.addProducts(expectedPs);

    Set<Product> actualPs = classUnderTest.getProducts();
    assertEquals(expectedPs, actualPs);

    // test the second side of the association
    for (Product p : actualPs) {
      Set<BusinessDomain> actualBDs = p.getBusinessDomains();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBDs, actualBDs);
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessDomain#addProducts(java.util.Set)}
   * The method tests if the addProducts adds properly set with BusinessProducts. The both sides of
   * the association should be properly set.
   */
  @Test
  public void testAddProductsSecCaseNotEmptyList() {
    classUnderTest = new BusinessDomain();

    // set to test the second side of the association
    Set<BusinessDomain> expectedBDs = hashSet();
    expectedBDs.add(classUnderTest);

    // initialization set
    Set<Product> expectedPs = hashSet();
    // add some elements
    Product firstP = new Product();
    expectedPs.add(firstP);

    Product secP = new Product();
    expectedPs.add(secP);

    Product thirdP = new Product();
    expectedPs.add(thirdP);

    classUnderTest.addProducts(expectedPs);

    Set<Product> actualPs = classUnderTest.getProducts();
    assertEquals(expectedPs, actualPs);

    // test the second side of the association
    for (Product p : actualPs) {
      Set<BusinessDomain> actualBDs = p.getBusinessDomains();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBDs, actualBDs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addBusinessUnits(java.util.Set)} The method
   * tests if the addBusinessUnits adds properly set with BusinessUnits.
   */
  @Test
  public void testAddBusinessUnitsCaseEmptySet() {
    classUnderTest = new BusinessDomain();
    Set<BusinessUnit> expectedBusinessUnits = hashSet();

    classUnderTest.addBusinessUnits(expectedBusinessUnits);
    assertEquals(classUnderTest.getBusinessUnits(), expectedBusinessUnits);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addBusinessUnits(java.util.Set)} The method
   * tests if the addBusinessUnits adds properly set with BusinessUnits. The both sides of the
   * association should be properly set.
   */
  @Test
  public void testAddBusinessUnitsFirstCaseNotEmptySet() {
    classUnderTest = new BusinessDomain();

    // set to test the second side of the association
    Set<BusinessDomain> expectedBDs = hashSet();
    expectedBDs.add(classUnderTest);

    // initialization set
    Set<BusinessUnit> expectedBUs = hashSet();
    // add some elements
    BusinessUnit firstBU = new BusinessUnit();
    firstBU.setId(Integer.valueOf(1));
    firstBU.setName(TEST_NAME_1);
    expectedBUs.add(firstBU);

    BusinessUnit secBU = new BusinessUnit();
    secBU.setId(Integer.valueOf(2));
    secBU.setName(TEST_NAME_2);
    expectedBUs.add(secBU);

    BusinessUnit thirdBU = new BusinessUnit();
    thirdBU.setId(Integer.valueOf(2));
    thirdBU.setName("third");
    expectedBUs.add(thirdBU);

    classUnderTest.addBusinessUnits(expectedBUs);

    Set<BusinessUnit> actualBUs = classUnderTest.getBusinessUnits();
    assertEquals(expectedBUs, actualBUs);

    // test the second side of the association
    for (BusinessUnit bu : actualBUs) {
      Set<BusinessDomain> actualBDs = bu.getBusinessDomains();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBDs, actualBDs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addBusinessUnits(java.util.Set)} The method
   * tests if the addBusinessUnits adds properly set with BusinessUnits. The both sides of the
   * association should be properly set.
   */
  @Test
  public void testAddBusinessUnitsSecCaseNotEmptySet() {
    classUnderTest = new BusinessDomain();

    // set to test the second side of the association
    Set<BusinessDomain> expectedBDs = hashSet();
    expectedBDs.add(classUnderTest);

    // initialization set
    Set<BusinessUnit> expectedBUs = hashSet();
    // add some elements
    BusinessUnit firstBU = new BusinessUnit();
    expectedBUs.add(firstBU);

    BusinessUnit secBU = new BusinessUnit();
    expectedBUs.add(secBU);

    BusinessUnit thirdBU = new BusinessUnit();

    expectedBUs.add(thirdBU);

    classUnderTest.addBusinessUnits(expectedBUs);

    Set<BusinessUnit> actualBUs = classUnderTest.getBusinessUnits();
    assertEquals(expectedBUs, actualBUs);

    // test the second side of the association
    for (BusinessUnit bu : actualBUs) {
      Set<BusinessDomain> actualBDs = bu.getBusinessDomains();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBDs, actualBDs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addParent(de.iteratec.iteraplan.model.BusinessDomain)}
   * The method tests if the addParent() really throws IllegalArgumentException when the user tries to
   * set the parent with null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddParentCaseAddNull() {
    classUnderTest = new BusinessDomain();
    classUnderTest.addParent(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addParent(de.iteratec.iteraplan.model.BusinessDomain)}
   * The method tests if the method sets itself as parent. An element can not be parent of itself,
   * that is why we expect IteraplanBusinessException.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentCaseAddItself() {
    classUnderTest = new BusinessDomain();
    classUnderTest.setId(Integer.valueOf(1));
    classUnderTest.addParent(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addParent(de.iteratec.iteraplan.model.BusinessDomain)}
   * The method tests if the method detects cycles. An element A can not have an element B as its
   * parent if B already has A as its parent.
   */
  @Test
  public void testAddParentCaseCycle() {
    classUnderTest = new BusinessDomain();
    classUnderTest.setId(Integer.valueOf(1));
    BusinessDomain parent1 = new BusinessDomain();
    parent1.setId(Integer.valueOf(2));
    parent1.addParent(classUnderTest);
    try {
      classUnderTest.addParent(parent1);
      fail("addParent() should have thrown an exception!");
    } catch (IteraplanBusinessException e) {
      assertEquals(null, classUnderTest.getParent());
      // make sure that the names can still be displayed (no cycle exists).
      classUnderTest.getHierarchicalName();
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#addParent(de.iteratec.iteraplan.model.BusinessDomain)}
   * The method tests if the AddParent method sets correctly another object as parent.
   */
  @Test
  public void testAddParentCaseAddOtherBD() {
    BusinessDomain parent = new BusinessDomain();
    parent.setId(Integer.valueOf(1));
    classUnderTest = new BusinessDomain();
    classUnderTest.addParent(parent);

    assertEquals(classUnderTest.getParent(), parent);

    // check that object is removed from old parent's child list
    BusinessDomain newParent = new BusinessDomain();
    newParent.setId(3);
    assertEquals(0, newParent.getChildren().size());
    assertEquals(1, parent.getChildren().size());
    classUnderTest.addParent(newParent);
    assertEquals(1, newParent.getChildren().size());
    assertEquals(0, parent.getChildren().size());

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessDomain#removeParent()} The method
   * tests if the removeParent method removes the parent object correctly. The attributes of the
   * parent object save same information.
   */
  @Test
  public void testRemoveParentCaseNotNull() {
    classUnderTest = new BusinessDomain();

    BusinessDomain parent = new BusinessDomain();
    parent.setId(Integer.valueOf(12345));

    classUnderTest.addParent(parent);
    classUnderTest.removeParent();

    BusinessDomain expectedParent = null;
    BusinessDomain actualParent = classUnderTest.getParent();

    assertEquals(expectedParent, actualParent);

    List<BusinessDomain> expectedChildren = arrayList();
    List<BusinessDomain> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessDomain#removeParent()} The method
   * tests if the method removes the parent object. The attributes of the parent object saves no
   * information.
   */
  @Test
  public void testRemoveParentCaseNull() {
    classUnderTest = new BusinessDomain();
    classUnderTest.removeParent();

    BusinessDomain expectedParent = null;
    BusinessDomain actualParent = classUnderTest.getParent();

    assertEquals(expectedParent, actualParent);

    List<BusinessDomain> expectedChildren = arrayList();
    List<BusinessDomain> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessDomain#removeRelations()} The method
   * tests if the removeRelations method removes all the domain relations.
   */
  @Test
  public void testRemoveRelations() {
    classUnderTest = new BusinessDomain();

    // BusinessFunctions
    Set<BusinessFunction> expectedBFunctions = hashSet();
    BusinessFunction firstBF = new BusinessFunction();
    BusinessFunction secBF = new BusinessFunction();

    firstBF.setId(Integer.valueOf(1));
    secBF.setId(Integer.valueOf(2));

    expectedBFunctions.add(firstBF);
    expectedBFunctions.add(secBF);

    // BusinessProcess
    Set<BusinessProcess> expectedBProcesses = hashSet();
    BusinessProcess firstBP = new BusinessProcess();
    BusinessProcess secBP = new BusinessProcess();

    expectedBProcesses.add(firstBP);
    expectedBProcesses.add(secBP);

    // BusinessObjects
    Set<BusinessObject> expectedBObjects = hashSet();

    BusinessObject firstBO = new BusinessObject();
    BusinessObject secBO = new BusinessObject();

    firstBO.setId(Integer.valueOf(1));
    secBO.setId(Integer.valueOf(2));

    expectedBObjects.add(firstBO);
    expectedBObjects.add(secBO);

    // Product
    Set<Product> expectedProducts = hashSet();

    Product firstP = new Product();
    Product secP = new Product();

    firstP.setId(Integer.valueOf(1));
    secP.setId(Integer.valueOf(2));

    expectedProducts.add(firstP);
    expectedProducts.add(secP);

    // BusinessUnit
    Set<BusinessUnit> expectedBUnits = hashSet();

    BusinessUnit firstBU = new BusinessUnit();
    BusinessUnit secBU = new BusinessUnit();

    firstBU.setId(Integer.valueOf(1));
    secBU.setId(Integer.valueOf(2));

    expectedBUnits.add(firstBU);
    expectedBUnits.add(secBU);

    classUnderTest.addBusinessFunctions(expectedBFunctions);
    classUnderTest.addBusinessProcesses(expectedBProcesses);
    classUnderTest.addBusinessObjects(expectedBObjects);
    classUnderTest.addProducts(expectedProducts);
    classUnderTest.addBusinessUnits(expectedBUnits);

    // Method Under Test
    classUnderTest.removeRelations();

    assertEquals(new HashSet<BusinessFunction>(), classUnderTest.getBusinessFunctions());
    assertEquals(new HashSet<BusinessProcess>(), classUnderTest.getBusinessProcesses());
    assertEquals(new HashSet<BusinessObject>(), classUnderTest.getBusinessObjects());
    assertEquals(new HashSet<Product>(), classUnderTest.getProducts());
    assertEquals(new HashSet<BusinessUnit>(), classUnderTest.getBusinessUnits());
  }

  /**
   * Test method for
   *{@link de.iteratec.iteraplan.model.BusinessDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * this object is bigger.
   */
  @Test
  public void testCompareToCaseThisBigger() {
    BusinessDomain first = new BusinessDomain();
    BusinessDomain second = new BusinessDomain();
    second.setName("second");
    first.setParent(second);
    assertEquals(1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are empty and equal.
   */
  @Test
  public void testCompareToCaseEmptyObjects() {
    BusinessDomain first = new BusinessDomain();
    BusinessDomain second = new BusinessDomain();
    assertEquals(0, first.compareTo(second));
  }

  /**
   * Test method for
   *{@link de.iteratec.iteraplan.model.BusinessDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * second object is bigger.
   */
  @Test
  public void testCompareToCaseOtherBigger() {
    BusinessDomain first = new BusinessDomain();
    first.setName(TEST_NAME_1);
    BusinessDomain second = new BusinessDomain();

    assertEquals(-1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are equal. .
   */
  @Test
  public void testCompareToCaseEqualsObjects() {
    BusinessDomain first = new BusinessDomain();
    BusinessDomain second = new BusinessDomain();
    assertEquals(0, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is.
   */
  @Test
  public void testCompareToCaseParentEquals() {
    BusinessDomain parent = new BusinessDomain();
    parent.setId(Integer.valueOf(55));
    parent.setName("parent");
    parent.setDescription("test");

    BusinessDomain first = new BusinessDomain();
    first.setParent(parent);
    first.setId(Integer.valueOf(1));

    BusinessDomain second = new BusinessDomain();
    second.setParent(parent);
    second.setId(Integer.valueOf(2));

    List<BusinessDomain> children = arrayList();
    children.add(first);
    children.add(second);

    parent.setChildren(children);

    assertEquals(1, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsFirst() {
    // Initialize
    // firstParent
    BusinessDomain firstParent = new BusinessDomain();
    firstParent.setId(Integer.valueOf(55));
    firstParent.setName("firstParent");

    // children
    BusinessDomain first = new BusinessDomain();
    first.setId(Integer.valueOf(167));
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);

    // secondParent
    BusinessDomain secParent = new BusinessDomain();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");

    // children
    BusinessDomain second = new BusinessDomain();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessDomain> firstChildren = arrayList();
    firstChildren.add(first);
    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessDomain> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    BusinessDomain root = new BusinessDomain();
    root.setName("root");
    root.setId(Integer.valueOf(1000));
    ArrayList<BusinessDomain> rootChildren = new ArrayList<BusinessDomain>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    assertEquals(-1, firstParent.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsSec() {
    // Initialize
    BusinessDomain top = new BusinessDomain();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    BusinessDomain root = new BusinessDomain();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    BusinessDomain firstParent = new BusinessDomain();
    firstParent.setId(Integer.valueOf(55));

    // children
    BusinessDomain first = new BusinessDomain();
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    BusinessDomain secParent = new BusinessDomain();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription("test");

    // children
    BusinessDomain second = new BusinessDomain();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessDomain> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessDomain> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<BusinessDomain> rootChildren = new ArrayList<BusinessDomain>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<BusinessDomain> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(-1, root.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The second will be with the root compared. The
   * result should be +1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsThird() {
    // Initialize
    BusinessDomain top = new BusinessDomain();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    BusinessDomain root = new BusinessDomain();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    BusinessDomain firstParent = new BusinessDomain();
    firstParent.setId(Integer.valueOf(55));

    // children
    BusinessDomain first = new BusinessDomain();
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    BusinessDomain secParent = new BusinessDomain();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription("test");

    // children
    BusinessDomain second = new BusinessDomain();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessDomain> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessDomain> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<BusinessDomain> rootChildren = new ArrayList<BusinessDomain>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<BusinessDomain> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(1, second.compareTo(root));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessDomain#getChildrenAsList()} The
   * method tests if the getChildrenAsList() correctly returns the list with the children as a list.
   */
  @Test
  public void testGetChildrenAsList() {
    classUnderTest = new BusinessDomain();
    assertNotNull(classUnderTest.getChildrenAsList());

    // Add some children:
    List<BusinessDomain> expected = arrayList();

    // child 1
    BusinessDomain firstChild = new BusinessDomain();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("child1");
    expected.add(firstChild);

    // child 2 = child 3
    BusinessDomain secChild = new BusinessDomain();
    expected.add(secChild);
    expected.add(secChild);

    classUnderTest.setChildren(expected);

    List<BusinessDomain> actual = classUnderTest.getChildrenAsList();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessDomain#getChildrenAsSet()} The
   * method tests if the getChildrenAsSet() correctly returns the list with the children as a set.
   */
  @Test
  public void testGetChildrenAsSet() {
    classUnderTest = new BusinessDomain();
    assertNotNull(classUnderTest.getChildrenAsSet());

    // Add some children:
    List<BusinessDomain> expected = arrayList();
    Set<BusinessDomain> actual = hashSet();

    // child 1
    BusinessDomain firstChild = new BusinessDomain();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("child1");
    expected.add(firstChild);

    // child 2 = child 3
    BusinessDomain secChild = new BusinessDomain();
    expected.add(secChild);
    expected.add(secChild);

    classUnderTest.setChildren(expected);
    actual = classUnderTest.getChildrenAsSet();

    assertNotNull(actual);
    assertTrue(actual.contains(firstChild));
    assertTrue(actual.contains(secChild));

    // the expected size of the set should be with one smaller than the size of the list.
    int expectedSize = expected.size() - 1;
    int actualSize = actual.size();
    assertEquals(expectedSize, actualSize);
  }

}

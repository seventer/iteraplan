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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;


/**
 * @author mma
 */
public class ProductTest {

  private static final String TEST_NAME_1 = "first";
  private static final String TEST_NAME_2 = "sec";

  @Before
  public void setUp() throws Exception {
    // not yet used
  }

  @After
  public void tearDown() throws Exception {
    // not yet used
  }

  private Product classUnderTest = null;

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Product#getTypeOfBuildingBlock()} The method
   * tests the getTypeOfBuildingBlock() method. The test method has only meaning for the code
   * coverage.
   */
  @Test
  public void testGetTypeOfBuildingBlock() {
    assertEquals("global.product", new Product().getTypeOfBuildingBlock().toString());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Product#addBusinessDomains(java.util.Set)}
   * The method tests if the AddBusinessDomains() throws correctly IllegalArgumentException if the user
   * tries to initialize businessDomains with set with null element in it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddBusinessDomainsCaseNull() {
    classUnderTest = new Product();
    assertNotNull(classUnderTest.getBusinessDomains());

    Set<BusinessDomain> expected = hashSet();

    // set with null element
    expected.add(null);
    classUnderTest.addBusinessDomains(expected);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Product#addInformationSystems(java.util.Set)} The method
   * tests if the setInformationSystemRelease() throws correctly IllegalArgumentException if the user tries to
   * initialize information systems with set with null element in it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddInformationSystemsCaseNull() {

    classUnderTest = new Product();
    Set<BusinessMapping> businessMappings = classUnderTest.getBusinessMappings();
    assertNotNull(businessMappings);

    for (BusinessMapping bm : businessMappings) {
      assertNotNull(bm.getInformationSystemRelease());
    }

    BusinessMapping firstBusinessMapping = new BusinessMapping();
    firstBusinessMapping.setInformationSystemRelease(null);

    Set<BusinessMapping> expected = hashSet();
    Set<BusinessMapping> actual = classUnderTest.getBusinessMappings();

    expected.add(firstBusinessMapping);
    expected.add(null);
    classUnderTest.addBusinessMappings(expected);
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Product#addBusinessDomains(java.util.Set)}
   * The method tests if the AddBusinessDomains() adds correctly set of BusinessDomains. The both
   * sides of the association should be set.
   */
  @Test
  public void testAddBusinessDomainsFirstCaseNotNull() {
    classUnderTest = new Product();
    assertNotNull(classUnderTest.getBusinessDomains());

    // set to test the other side of the association
    Set<Product> expectedPs = hashSet();
    expectedPs.add(classUnderTest);

    // initialization set
    Set<BusinessDomain> expectedBDs = hashSet();
    // add some elements
    BusinessDomain firstBD = new BusinessDomain();
    firstBD.setId(Integer.valueOf(1));
    firstBD.setName(TEST_NAME_1);
    expectedBDs.add(firstBD);

    BusinessDomain secBD = new BusinessDomain();
    secBD.setId(Integer.valueOf(2));
    secBD.setName(TEST_NAME_2);
    expectedBDs.add(secBD);

    BusinessDomain thirdBD = new BusinessDomain();
    thirdBD.setId(Integer.valueOf(2));
    thirdBD.setName("third");
    expectedBDs.add(thirdBD);

    classUnderTest.addBusinessDomains(expectedBDs);

    Set<BusinessDomain> actualBDs = classUnderTest.getBusinessDomains();
    assertEquals(expectedBDs, actualBDs);

    // test the second side of the association
    for (BusinessDomain bd : actualBDs) {
      Set<Product> actualPs = bd.getProducts();
      assertEquals("Fail to update both sides of the association", expectedPs, actualPs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Product#addInformationSystems(java.util.Set)} The method
   * tests if the setInformationSystemRelease() adds properly a set with information systems. Both sides of the
   * association should be set.
   */
  @Test
  public void testAddInformationSystemsCaseNotNull() {

    classUnderTest = new Product();

    Set<Product> expectedProducts = hashSet();
    expectedProducts.add(classUnderTest);
    Set<BusinessMapping> expectedBMs = hashSet();

    BusinessMapping firstBM = new BusinessMapping();
    firstBM.setId(Integer.valueOf(10));

    InformationSystemRelease firstISR = new InformationSystemRelease();
    firstISR.setId(Integer.valueOf(1));
    firstBM.addInformationSystemRelease(firstISR);

    InformationSystemRelease secondISR = new InformationSystemRelease();
    secondISR.setId(Integer.valueOf(2));
    firstBM.addInformationSystemRelease(secondISR);
    expectedBMs.add(firstBM);

    classUnderTest.addBusinessMappings(expectedBMs);
    Set<BusinessMapping> actualBMs = classUnderTest.getBusinessMappings();
    assertEquals(expectedBMs, actualBMs);

    for (BusinessMapping bm : actualBMs) {
      Product actualProduct = bm.getProduct();
      assertEquals("Fail to update both sides of the association", classUnderTest, actualProduct);
    }

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Product#addBusinessDomains(java.util.Set)}
   * The method tests if the AddBusinessDomains() adds correctly set of BusinessDomains. The both
   * sides of the association should be set.
   */
  @Test
  public void testAddBusinessDomainsSecCaseNotNull() {
    classUnderTest = new Product();
    assertNotNull(classUnderTest.getBusinessDomains());

    // set to test the other side of the association
    Set<Product> expectedPs = hashSet();
    expectedPs.add(classUnderTest);

    // initialization set
    Set<BusinessDomain> expectedBDs = hashSet();
    // add some elements
    BusinessDomain firstBD = new BusinessDomain();
    expectedBDs.add(firstBD);

    BusinessDomain secBD = new BusinessDomain();
    expectedBDs.add(secBD);

    BusinessDomain thirdBD = new BusinessDomain();
    expectedBDs.add(thirdBD);

    classUnderTest.addBusinessDomains(expectedBDs);

    Set<BusinessDomain> actualBDs = classUnderTest.getBusinessDomains();
    assertEquals(expectedBDs, actualBDs);

    // test the second side of the association
    for (BusinessDomain bd : actualBDs) {
      Set<Product> actualPs = bd.getProducts();
      assertEquals("Fail to update both sides of the association", expectedPs, actualPs);
    }
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.Product#addBusinessDomains(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.Product#setBusinessDomains(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.Product#getBusinessDomains()} The attribute businessDomain
   * must be at the beginning empty but not null. Then two sets are created. The fist one is empty
   * and the second one is null. At the end the two sets must be equals.
   */
  @Test
  public void testBusinessDomains() {
    classUnderTest = new Product();
    assertNotNull(classUnderTest.getBusinessDomains());

    Set<BusinessDomain> expected = hashSet();
    Set<BusinessDomain> actual;

    // initialize the businessDomains
    BusinessDomain firstBD = new BusinessDomain();
    expected.add(firstBD);

    // Set of Products for the BusinessDomain
    Set<Product> expectedProducts = hashSet();
    expectedProducts.add(classUnderTest);
    firstBD.addProducts(expectedProducts);

    classUnderTest.setBusinessDomains(expected);

    // check if the other side of the association is set.
    for (BusinessDomain bd : classUnderTest.getBusinessDomains()) {
      Set<Product> actualProducts = bd.getProducts();
      assertEquals(expectedProducts, actualProducts);
    }

    // put second element in businessDomains with the add Method
    BusinessDomain secBD = new BusinessDomain();
    expected.add(secBD);
    classUnderTest.addBusinessDomains(expected);

    actual = classUnderTest.getBusinessDomains();

    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Parent#setParent()}
   * {@link de.iteratec.iteraplan.model.Parent#getParent()}
   * {@link de.iteratec.iteraplan.model.Product#getParentElement()} The parent must be at the
   * beginning null. The method has only meaning for the code coverage.
   */
  @Test
  public void testParent() {
    classUnderTest = new Product();
    assertNull(classUnderTest.getParent());

    // Add a parent:
    Product expected = new Product();
    Product actual;
    expected.setId(Integer.valueOf(1));

    classUnderTest.setParent(expected);
    actual = classUnderTest.getParent();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Product#addParent(de.iteratec.iteraplan.model.Product)} The
   * method tests if the addParent() really throws IllegalArgumentException when we try to set the
   * parent to null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddParentCaseNull() {
    classUnderTest = new Product();
    classUnderTest.addParent(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Product#addParent(de.iteratec.iteraplan.model.Product)} The
   * method tests if the method sets itself as parent. An element can not be parent of itself, that
   * is why we expect IteraplanBusinessException.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentCaseAddItself() {
    classUnderTest = new Product();
    classUnderTest.setId(Integer.valueOf(1));
    classUnderTest.addParent(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Product#addParent(de.iteratec.iteraplan.model.Product)} The
   * method tests if the method detects cycles. An element A can not have an element B as its parent
   * if B already has A as its parent.
   */
  @Test
  public void testAddParentCaseCycle() {
    classUnderTest = new Product();
    classUnderTest.setId(Integer.valueOf(1));
    Product parent1 = new Product();
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
   * {@link de.iteratec.iteraplan.model.Product#addParent(de.iteratec.iteraplan.model.Product)} The
   * method tests if the AddParent method sets correctly another object as parent.
   */
  @Test
  public void testAddParentCaseAddOtherP() {
    Product parent = new Product();
    parent.setId(Integer.valueOf(1));
    classUnderTest = new Product();
    classUnderTest.addParent(parent);

    assertEquals(classUnderTest.getParent(), parent);

    // check that object is removed from old parent's child list
    Product newParent = new Product();
    newParent.setId(Integer.valueOf(3));
    assertEquals(0, newParent.getChildren().size());
    assertEquals(1, parent.getChildren().size());
    classUnderTest.addParent(newParent);
    assertEquals(1, newParent.getChildren().size());
    assertEquals(0, parent.getChildren().size());

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Product#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * this object is bigger.
   */
  @Test
  public void testCompareToCaseThisBigger() {
    Product first = new Product();
    Product second = new Product();
    second.setName("second");
    first.setParent(second);
    assertEquals(1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Product#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are empty and equal.
   */
  @Test
  public void testCompareToCaseEmptyObjects() {
    Product first = new Product();
    Product second = new Product();
    assertEquals(0, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Product#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * second object is bigger.
   */
  @Test
  public void testCompareToCaseOtherBigger() {
    Product first = new Product();
    first.setName(TEST_NAME_1);
    Product second = new Product();

    assertEquals(-1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Product#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are equal. .
   */
  @Test
  public void testCompareToCaseEqualsObjects() {
    Product first = new Product();
    Product second = new Product();
    assertEquals(0, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Product#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is.
   */
  @Test
  public void testCompareToCaseParentEquals() {
    Product parent = new Product();
    parent.setId(Integer.valueOf(55));
    parent.setName("parent");
    parent.setDescription("test");

    Product first = new Product();
    first.setParent(parent);
    first.setId(Integer.valueOf(1));

    Product second = new Product();
    second.setParent(parent);
    second.setId(Integer.valueOf(2));

    List<Product> children = arrayList();
    children.add(first);
    children.add(second);

    parent.setChildren(children);

    assertEquals(1, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Product#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsFirst() {
    // Initialize
    // firstParent
    Product firstParent = new Product();
    firstParent.setId(Integer.valueOf(55));
    firstParent.setName("firstParent");

    // children
    Product first = new Product();
    first.setId(Integer.valueOf(167));
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);

    // secondParent
    Product secParent = new Product();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");

    // children
    Product second = new Product();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<Product> firstChildren = arrayList();
    firstChildren.add(first);
    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<Product> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    Product root = new Product();
    root.setName("root");
    root.setId(Integer.valueOf(1000));
    List<Product> rootChildren = arrayList();
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
   * {@link de.iteratec.iteraplan.model.Product#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsSec() {
    // Initialize
    Product top = new Product();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    Product root = new Product();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    Product firstParent = new Product();
    firstParent.setId(Integer.valueOf(55));

    // children
    Product first = new Product();
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    Product secParent = new Product();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription("test");

    // children
    Product second = new Product();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<Product> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<Product> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    List<Product> rootChildren = arrayList();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<Product> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(-1, root.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Product#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The second will be with the root compared. The
   * result should be +1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsThird() {
    // Initialize
    Product top = new Product();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    Product root = new Product();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    Product firstParent = new Product();
    firstParent.setId(Integer.valueOf(55));

    // children
    Product first = new Product();
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    Product secParent = new Product();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription("test");

    // children
    Product second = new Product();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<Product> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<Product> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    List<Product> rootChildren = arrayList();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<Product> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(1, second.compareTo(root));
  }

  /**
   * The sorting of Products should result in an in-order ordering. The implementation of compareTo
   * is tested with this method.
   */
  @Test
  public void testCompareToImplementation() {
    // arrange:
    Product p1 = new Product();
    p1.setName("p1");
    Product p2 = new Product();
    p2.setName("p2");
    Product p3 = new Product();
    p3.setName("p3");
    Product p4 = new Product();
    p4.setName("p4");
    Product p5 = new Product();
    p5.setName("p5");
    Product p6 = new Product();
    p6.setName("p6");
    Product p7 = new Product();
    p7.setName("p7");
    Product p8 = new Product();
    p8.setName("p8");

    // act
    p2.addParent(p1);
    p3.addParent(p2);
    p4.addParent(p2);
    p5.addParent(p1);
    p6.addParent(p5);
    p7.addParent(p5);
    p8.addParent(p7);

    List<Product> l = new ArrayList<Product>();
    l.add(p4);
    l.add(p3);
    l.add(p8);
    l.add(p1);
    l.add(p2);
    l.add(p6);
    l.add(p5);
    l.add(p7);
    Collections.sort(l);

    // asserts for l
    for (int i = 0; i < l.size(); i++) {
      assertEquals("p" + (i + 1), l.get(i).getName());
    }

    List<Product> m = new ArrayList<Product>();
    m.add(p5);
    m.add(p7);
    m.add(p1);
    m.add(p3);
    m.add(p8);
    m.add(p2);
    m.add(p6);
    m.add(p4);
    Collections.sort(m);

    // asserts for m
    for (int i = 0; i < m.size(); i++) {
      assertEquals("p" + (i + 1), m.get(i).getName());
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Product#getChildrenAsList()} The method
   * tests if the getChildrenAsList() correctly takes the list with the children.
   */
  @Test
  public void testGetChildrenAsList() {
    classUnderTest = new Product();
    assertNotNull(classUnderTest.getChildrenAsList());

    // Add some children:
    List<Product> expected = arrayList();
    List<Product> actual;
    Product child = new Product();
    child.setId(Integer.valueOf(1));
    child.setName("child1");
    expected.add(child);
    classUnderTest.setChildren(expected);

    actual = classUnderTest.getChildrenAsList();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Product#getChildrenAsSet()} The method tests
   * if the GetChildrenAsSet() correctly takes the list with the children as a set.
   */
  @Test
  public void testGetChildrenAsSet() {
    classUnderTest = new Product();
    assertNotNull(classUnderTest.getChildrenAsSet());

    // Add some children:
    List<Product> expected = arrayList();
    Set<Product> actual;

    // child 1
    Product firstChild = new Product();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("child1");
    expected.add(firstChild);

    // child 2 = child 3
    Product secChild = new Product();
    expected.add(secChild);
    expected.add(secChild);

    classUnderTest.setChildren(expected);
    actual = classUnderTest.getChildrenAsSet();

    assertNotNull(actual);
    assertTrue(actual.contains(firstChild));
    assertTrue(actual.contains(secChild));
    assertEquals(expected.size() - 1, actual.size());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Product#getI18NKey()} The method tests the
   * getI18NKey() method. The test method has only meaning for the code coverage.
   */
  @Test
  public void testGetI18NKey() {
    classUnderTest = new Product();
    assertEquals("product.virtualElement", classUnderTest.getI18NKey());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Product#removeBusinessDomainRelations()} The
   * method tests if the RemoveBusinessDomainRelations() correctly removes all the businessDomains
   * and their Products.
   */
  @Test
  public void testRemoveBusinessDomainRelations() {
    classUnderTest = new Product();

    Set<BusinessDomain> businessDomains = hashSet();
    BusinessDomain firstBD = new BusinessDomain();
    businessDomains.add(firstBD);

    classUnderTest.addBusinessDomains(businessDomains);

    classUnderTest.removeBusinessDomainRelations();

    Set<BusinessDomain> expectedBusinessDomains = hashSet();
    Set<BusinessDomain> actualBusinessDomains = classUnderTest.getBusinessDomains();

    assertEquals(expectedBusinessDomains, actualBusinessDomains);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Product#removeInformationSystemReleaseRelations()}
   */
  @Test
  public void testRemoveInformationSystemReleaseRelations() {

    classUnderTest = new Product();
    Set<BusinessMapping> expectedBMs = hashSet();

    BusinessMapping firstBM = new BusinessMapping();
    BusinessMapping secondBM = new BusinessMapping();
    InformationSystemRelease firstISR = new InformationSystemRelease();
    InformationSystemRelease secondISR = new InformationSystemRelease();

    firstBM.addInformationSystemRelease(firstISR);
    firstBM.addInformationSystemRelease(secondISR);
    secondBM.addInformationSystemRelease(firstISR);

    classUnderTest.addBusinessMappings(expectedBMs);
    classUnderTest.removeBusinessMappings();

    Set<BusinessMapping> expected = new BusinessProcess().getBusinessMappings();
    Set<BusinessMapping> actual = classUnderTest.getBusinessMappings();
    assertEquals(expected, actual);

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Product#removeParent()}
   * {@link de.iteratec.iteraplan.model.Product#setChildren(java.util.List)}
   * {@link de.iteratec.iteraplan.model.Product#getChildren()} The method tests if the removeParent
   * method removes the parent object correctly. The attributes of the parent object save same
   * information.
   */
  @Test
  public void testRemoveParentCaseNotNull() {
    classUnderTest = new Product();

    Product parent = new Product();
    parent.setId(Integer.valueOf(65));
    parent.setName("parent");

    Product child = new Product();
    List<Product> children = arrayList();
    children.add(child);
    parent.setChildren(children);

    classUnderTest.addParent(parent);
    classUnderTest.removeParent();

    Product expectedParent = null;
    Product actualParent = classUnderTest.getParent();

    assertEquals(expectedParent, actualParent);

    List<Product> expectedChildren = arrayList();
    List<Product> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Product#removeParent()} The method tests if
   * the method removes the parent object. The attributes of the parent object saves no information.
   */
  @Test
  public void testRemoveParentCaseNull() {
    classUnderTest = new Product();
    classUnderTest.removeParent();

    Product expectedParent = null;
    Product actualParent = classUnderTest.getParent();

    assertEquals(expectedParent, actualParent);

    List<Product> expectedChildren = arrayList();
    List<Product> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Product#removeAllChildren()} The method
   * tests if the removeAllChildren method correctly removes all the children.
   */
  @Test
  public void testRemoveAllChildren() {
    classUnderTest = new Product();

    // List with children
    List<Product> children = arrayList();

    // add children
    children.add(new Product());
    children.add(new Product());
    children.add(new Product());
    children.add(new Product());
    children.add(new Product());

    classUnderTest.setChildren(children);
    classUnderTest.removeAllChildren();

    List<Product> expectedChildren = arrayList();
    List<Product> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedChildren, actualChildren);
  }
}

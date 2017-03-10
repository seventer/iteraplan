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

import junit.framework.Assert;

import org.junit.Test;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;


/**
 * Test for the {@link BusinessFunction}.
 */
@SuppressWarnings({"PMD.TooManyMethods" })
public class BusinessFunctionTest {

  private static final String TEST_NAME_1                 = "first";
  private static final String TEST_NAME_2                 = "sec";

  private static final String UPDATE_BOTH_SIDES_ERROR_MSG = "Fail to update both sides of the association";

  private BusinessFunction classUnderTest = null;

  /**
   * Test method for (getter)
   * {@link de.iteratec.iteraplan.model.BusinessFunction#getTypeOfBuildingBlock()} The method has
   * only meaning for the code coverage.
   */
  @Test
  public void testgetTypeOfBuildingBlock() {
    classUnderTest = new BusinessFunction();

    assertEquals("global.business_function", classUnderTest.getTypeOfBuildingBlock().toString());
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessFunction#setBusinessObjects(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.BusinessFunction#getBusinessObjects()} The attribute
   * businessObjects must be at the beginning empty but not null. Then two sets are created. The
   * fist one is empty and the second one is null. At the end the two sets must be equals. The
   * method has only meaning for the code coverage.
   */
  @Test
  public void testBusinessObjects() {
    classUnderTest = new BusinessFunction();
    Assert.assertNotNull(classUnderTest.getBusinessObjects());

    Set<BusinessObject> expected = hashSet();
    expected.add(new BusinessObject());

    classUnderTest.setBusinessObjects(expected);
    Set<BusinessObject> actual = classUnderTest.getBusinessObjects();

    assertEquals(expected, actual);
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessFunction#setBusinessDomains(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.BusinessFunction#getBusinessDomains()} The attribute
   * businessDomains must be at the beginning empty but not null. Then two sets are created. The
   * fist one is empty and the second one is null. At the end the two sets must be equals. The
   * method has only meaning for the code coverage.
   */
  @Test
  public void testBusinessDomains() {
    classUnderTest = new BusinessFunction();
    assertNotNull(classUnderTest.getBusinessObjects());

    Set<BusinessDomain> expected = hashSet();
    expected.add(new BusinessDomain());

    classUnderTest.setBusinessDomains(expected);
    Set<BusinessDomain> actual = classUnderTest.getBusinessDomains();

    assertEquals(expected, actual);
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessFunction#setChildren(java.util.List)}
   * {@link de.iteratec.iteraplan.model.BusinessFunction#getChildren()} The attribute children must
   * be at the beginning empty but not null. Then two sets are created. The fist one is empty and
   * the second one is null. At the end the two sets must be equals. The method has only meaning for
   * the code coverage.
   */
  @Test
  public void testGetChildren() {
    classUnderTest = new BusinessFunction();
    assertNotNull(classUnderTest.getBusinessObjects());

    List<BusinessFunction> expected = arrayList();
    expected.add(new BusinessFunction());

    classUnderTest.setChildren(expected);
    List<BusinessFunction> actual = classUnderTest.getChildren();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#addBusinessObjects(java.util.Set)} The
   * method tests if the addBusinessObject adds properly set with BusinessObjects.
   */
  @Test
  public void testAddBusinessObjectsCaseEmptySet() {
    classUnderTest = new BusinessFunction();
    Set<BusinessObject> expectedBObjects = hashSet();

    classUnderTest.addBusinessObjects(expectedBObjects);
    assertEquals(classUnderTest.getBusinessObjects(), expectedBObjects);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#addBusinessObjects(java.util.Set)} The
   * method tests if the addBusinessObject adds properly set with BusinessObjects. The both side of
   * the association should be set.
   */
  @Test
  public void testAddBusinessObjectsFirstCaseNotEmptySet() {
    classUnderTest = new BusinessFunction();

    // set to test the second side of the association
    Set<BusinessFunction> expectedBFs = hashSet();
    expectedBFs.add(classUnderTest);

    // initialization set
    Set<BusinessObject> expectedBOs = hashSet();
    // add some elements
    BusinessObject firstBO = new BusinessObject();
    firstBO.setId(Integer.valueOf(1));
    firstBO.setName(TEST_NAME_1);
    expectedBOs.add(firstBO);

    BusinessObject secBO = new BusinessObject();
    secBO.setId(Integer.valueOf(2));
    secBO.setName(TEST_NAME_2);
    expectedBOs.add(secBO);

    BusinessObject thirdBO = new BusinessObject();
    thirdBO.setId(Integer.valueOf(2));
    thirdBO.setName("third");
    expectedBOs.add(thirdBO);

    classUnderTest.addBusinessObjects(expectedBOs);

    Set<BusinessObject> actualBOs = classUnderTest.getBusinessObjects();
    assertEquals(expectedBOs, actualBOs);

    // test the second side of the association
    for (BusinessObject bo : actualBOs) {
      Set<BusinessFunction> actualBFs = bo.getBusinessFunctions();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBFs, actualBFs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#addBusinessObjects(java.util.Set)} The
   * method tests if the addBusinessObject adds properly set with BusinessObjects. The both side of
   * the association should be set.
   */
  @Test
  public void testAddBusinessObjectsSecCaseNotEmptySet() {
    classUnderTest = new BusinessFunction();

    // set to test the second side of the association
    Set<BusinessFunction> expectedBFs = hashSet();
    expectedBFs.add(classUnderTest);

    // initialization set
    Set<BusinessObject> expectedBOs = hashSet();
    // add some elements
    BusinessObject firstBO = new BusinessObject();
    expectedBOs.add(firstBO);

    BusinessObject secBO = new BusinessObject();
    expectedBOs.add(secBO);

    BusinessObject thirdBO = new BusinessObject();
    expectedBOs.add(thirdBO);

    classUnderTest.addBusinessObjects(expectedBOs);

    Set<BusinessObject> actualBOs = classUnderTest.getBusinessObjects();
    assertEquals(expectedBOs, actualBOs);

    // test the second side of the association
    for (BusinessObject bo : actualBOs) {
      Set<BusinessFunction> actualBFs = bo.getBusinessFunctions();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBFs, actualBFs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#addBusinessDomains(java.util.Set)} The
   * method tests if the addBusinessDomains adds properly set with BusinessDomains.
   */
  @Test
  public void testAddBusinessDomainsCaseEmptySet() {
    classUnderTest = new BusinessFunction();

    // set to test the second side of the association
    Set<BusinessFunction> expectedBFs = hashSet();
    expectedBFs.add(classUnderTest);

    // initialization set
    Set<BusinessDomain> expectedBDomains = hashSet();

    classUnderTest.addBusinessDomains(expectedBDomains);
    assertEquals(classUnderTest.getBusinessObjects(), expectedBDomains);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#addBusinessDomains(java.util.Set)} The
   * method tests if the addBusinessDomains adds properly set with BusinessDomains. The both side of
   * the association should be set.
   */
  @Test
  public void testAddBusinessDomainsFirstCaseNotEmptySet() {
    classUnderTest = new BusinessFunction();

    // set to test the second side of the association
    Set<BusinessFunction> expectedBFs = hashSet();
    expectedBFs.add(classUnderTest);

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
      Set<BusinessFunction> actualBFs = bd.getBusinessFunctions();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBFs, actualBFs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#addBusinessDomains(java.util.Set)} The
   * method tests if the addBusinessDomains adds properly set with BusinessDomains.The both side of
   * the association should be set.
   */
  @Test
  public void testAddBusinessDomainsSecCaseNotEmptySet() {
    classUnderTest = new BusinessFunction();

    // set to test the second side of the association
    Set<BusinessFunction> expectedBFs = hashSet();
    expectedBFs.add(classUnderTest);

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
      Set<BusinessFunction> actualBFs = bd.getBusinessFunctions();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBFs, actualBFs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#addParent(de.iteratec.iteraplan.model.BusinessDomain)}
   * The method tests if the addParent() really throws IllegalArgumentException when the user tries to
   * set the parent with null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddParentCaseAddNull() {
    classUnderTest = new BusinessFunction();
    classUnderTest.addParent(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#addParent(de.iteratec.iteraplan.model.BusinessFunction)}
   * The method tests if the method sets itself as parent. An element can not be parent of itself,
   * that is why we expect IteraplanBusinessException.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentCaseAddItself() {
    classUnderTest = new BusinessFunction();
    classUnderTest.setId(Integer.valueOf(1));
    classUnderTest.addParent(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#addParent(de.iteratec.iteraplan.model.BusinessFunction)}
   * The method tests if the method detects cycles. An element A can not have an element B as its
   * parent if B already has A as its parent.
   */
  @Test
  public void testAddParentCaseCycle() {
    classUnderTest = new BusinessFunction();
    classUnderTest.setId(Integer.valueOf(1));
    BusinessFunction parent1 = new BusinessFunction();
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
   * {@link de.iteratec.iteraplan.model.BusinessFunction#addParent(de.iteratec.iteraplan.model.BusinessFunction)}
   * The method tests if the AddParent method sets correctly another object as parent.
   */
  @Test
  public void testAddParentCaseAddOtherBF() {
    classUnderTest = new BusinessFunction();

    BusinessFunction parent = new BusinessFunction();
    parent.setId(Integer.valueOf(1));

    classUnderTest.addParent(parent);

    assertEquals(classUnderTest.getParent(), parent);

    // check that object is removed from old parent's child list
    BusinessFunction newParent = new BusinessFunction();
    newParent.setId(Integer.valueOf(3));
    assertEquals(0, newParent.getChildren().size());
    assertEquals(1, parent.getChildren().size());
    classUnderTest.addParent(newParent);
    assertEquals(1, newParent.getChildren().size());
    assertEquals(0, parent.getChildren().size());

  }

  /**
   * Test method for Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#removeParent()} The method tests if the
   * removeParent method removes the parent object correctly. The attributes of the parent object
   * save same information.
   */
  @Test
  public void testRemoveParentCaseNotNull() {
    classUnderTest = new BusinessFunction();

    BusinessFunction parent = new BusinessFunction();
    parent.setId(Integer.valueOf(5678));
    classUnderTest.addParent(parent);
    classUnderTest.removeParent();

    BusinessFunction expectedParent = null;
    BusinessFunction actualParent = classUnderTest.getParent();
    List<BusinessFunction> expectedChildren = arrayList();
    List<BusinessFunction> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedParent, actualParent);
    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#removeParent()} The method tests if the
   * method removes the parent object. The attributes of the parent object saves no information.
   */
  @Test
  public void testRemoveParentCaseNull() {
    classUnderTest = new BusinessFunction();
    classUnderTest.removeParent();

    BusinessFunction expectedParent = null;
    BusinessFunction actualParent = classUnderTest.getParent();
    List<BusinessFunction> expectedChildren = arrayList();
    List<BusinessFunction> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedParent, actualParent);
    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessFunction#removeRelations()} The
   * method tests if the removeRelations method removes all the function relations.
   */
  @Test
  public void testRemoveRelations() {
    classUnderTest = new BusinessFunction();

    // BusinessObjects
    Set<BusinessObject> expectedBObjects = hashSet();

    BusinessObject firstBO = new BusinessObject();
    BusinessObject secBO = new BusinessObject();

    firstBO.setId(Integer.valueOf(1));
    secBO.setId(Integer.valueOf(2));

    expectedBObjects.add(firstBO);
    expectedBObjects.add(secBO);

    // BusinessDomains
    Set<BusinessDomain> expectedBDomains = hashSet();
    BusinessDomain firstBD = new BusinessDomain();
    BusinessDomain secBD = new BusinessDomain();

    expectedBDomains.add(firstBD);
    expectedBDomains.add(secBD);

    classUnderTest.addBusinessDomains(expectedBDomains);
    classUnderTest.addBusinessObjects(expectedBObjects);

    // Method Under Test
    classUnderTest.removeRelations();

    assertEquals(new HashSet<BusinessObject>(), classUnderTest.getBusinessObjects());
    assertEquals(new HashSet<BusinessDomain>(), classUnderTest.getBusinessDomains());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessFunction#removeAllChildren()} The
   * method tests if the removeAllChildren method correctly removes all the children associations.
   */
  @Test
  public void testRemoveAllChildren() {
    classUnderTest = new BusinessFunction();

    List<BusinessFunction> children = arrayList();

    children.add(new BusinessFunction());
    children.add(new BusinessFunction());
    children.add(new BusinessFunction());
    children.add(new BusinessFunction());
    children.add(new BusinessFunction());

    classUnderTest.setChildren(children);
    classUnderTest.removeAllChildren();

    List<BusinessFunction> expectedChildren = new BusinessFunction().getChildren();
    List<BusinessFunction> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for
   *{@link de.iteratec.iteraplan.model.BusinessFunction#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * this object is bigger.
   */
  @Test
  public void testCompareToCaseThisBigger() {
    BusinessFunction first = new BusinessFunction();
    BusinessFunction second = new BusinessFunction();
    second.setName("second");
    first.setParent(second);
    assertEquals(1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are empty and equal.
   */
  @Test
  public void testCompareToCaseEmptyObjects() {
    BusinessFunction first = new BusinessFunction();
    BusinessFunction second = new BusinessFunction();
    assertEquals(0, first.compareTo(second));
  }

  /**
   * Test method for
   *{@link de.iteratec.iteraplan.model.BusinessFunction#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * second object is bigger.
   */
  @Test
  public void testCompareToCaseOtherBigger() {
    BusinessFunction first = new BusinessFunction();
    first.setName(TEST_NAME_1);
    BusinessFunction second = new BusinessFunction();

    assertEquals(-1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are equal. .
   */
  @Test
  public void testCompareToCaseEqualsObjects() {
    BusinessFunction first = new BusinessFunction();
    BusinessFunction second = new BusinessFunction();
    assertEquals(0, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is.
   */
  @Test
  public void testCompareToCaseParentEquals() {
    BusinessFunction parent = new BusinessFunction();
    parent.setId(Integer.valueOf(55));
    parent.setName("parent");
    parent.setDescription("test");

    BusinessFunction first = new BusinessFunction();
    first.setParent(parent);
    first.setId(Integer.valueOf(1));

    BusinessFunction second = new BusinessFunction();
    second.setParent(parent);
    second.setId(Integer.valueOf(2));

    List<BusinessFunction> children = arrayList();
    children.add(first);
    children.add(second);

    parent.setChildren(children);

    assertEquals(1, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsFirst() {
    // Initialize
    // firstParent
    BusinessFunction firstParent = new BusinessFunction();
    firstParent.setId(Integer.valueOf(55));
    firstParent.setName("firstParent");

    // children
    BusinessFunction first = new BusinessFunction();
    first.setId(Integer.valueOf(167));
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);

    // secondParent
    BusinessFunction secParent = new BusinessFunction();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");

    // children
    BusinessFunction second = new BusinessFunction();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessFunction> firstChildren = arrayList();
    firstChildren.add(first);
    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessFunction> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    BusinessFunction root = new BusinessFunction();
    root.setName("root");
    root.setId(Integer.valueOf(1000));
    ArrayList<BusinessFunction> rootChildren = new ArrayList<BusinessFunction>();
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
   * {@link de.iteratec.iteraplan.model.BusinessFunction#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsSec() {
    // Initialize
    BusinessFunction top = new BusinessFunction();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    BusinessFunction root = new BusinessFunction();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    BusinessFunction firstParent = new BusinessFunction();
    firstParent.setId(Integer.valueOf(55));

    // children
    BusinessFunction first = new BusinessFunction();
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    BusinessFunction secParent = new BusinessFunction();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription("test");

    // children
    BusinessFunction second = new BusinessFunction();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessFunction> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessFunction> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<BusinessFunction> rootChildren = new ArrayList<BusinessFunction>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<BusinessFunction> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(-1, root.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessFunction#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The second will be with the root compared. The
   * result should be +1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsThird() {
    // Initialize
    BusinessFunction top = new BusinessFunction();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    BusinessFunction root = new BusinessFunction();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    BusinessFunction firstParent = new BusinessFunction();
    firstParent.setId(Integer.valueOf(55));

    // children
    BusinessFunction first = new BusinessFunction();
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    BusinessFunction secParent = new BusinessFunction();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription("test");

    // children
    BusinessFunction second = new BusinessFunction();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessFunction> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessFunction> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<BusinessFunction> rootChildren = new ArrayList<BusinessFunction>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<BusinessFunction> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(1, second.compareTo(root));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessFunction#getChildrenAsList()} The
   * method tests if the getChildrenAsList() correctly returns the list with the children as a list.
   */
  @Test
  public void testGetChildrenAsList() {
    classUnderTest = new BusinessFunction();
    assertNotNull(classUnderTest.getChildrenAsList());

    // Add some children:
    List<BusinessFunction> expected = arrayList();

    // child 1
    BusinessFunction firstChild = new BusinessFunction();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("child1");
    expected.add(firstChild);

    // child 2 = child 3
    BusinessFunction secChild = new BusinessFunction();
    expected.add(secChild);
    expected.add(secChild);

    classUnderTest.setChildren(expected);

    List<BusinessFunction> actual = classUnderTest.getChildrenAsList();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessFunction#getI18NKey()} The method
   * has only meaning for the code coverage..
   */
  @Test
  public void testGetI18NKey() {
    classUnderTest = new BusinessFunction();
    assertEquals("businessFunction.virtualElement.description", classUnderTest.getI18NKey());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessFunction#getChildrenAsSet()} The
   * method tests if the getChildrenAsList() correctly returns the list with the children as a set.
   */
  @Test
  public void testGetChildrenAsSet() {
    classUnderTest = new BusinessFunction();
    assertNotNull(classUnderTest.getChildrenAsSet());

    // Add some children:
    List<BusinessFunction> expected = arrayList();
    Set<BusinessFunction> actual = hashSet();

    // child 1
    BusinessFunction firstChild = new BusinessFunction();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("child1");
    expected.add(firstChild);

    // child 2 = child 3
    BusinessFunction secChild = new BusinessFunction();
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

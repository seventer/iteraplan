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
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 * @author rfe
 * @author mma
 */
@SuppressWarnings("boxing")
public class BusinessUnitTest {

  private static final String TEST_NAME_1   = "first";
  private static final String TEST_NAME_2   = "sec";
  private static final String PARENT_NAME_1 = "firstParent";
  private static final String PARENT_NAME_2 = "secParent";

  private BusinessUnit businessUnit = null;

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessUnit#setBusinessDomains()}
   * {@link de.iteratec.iteraplan.model.BusinessUnit#getBusinessDomains()} The attribute
   * businessDomain should be at the beginning empty but not null. Then two sets are created. At the
   * end the two sets should be equal. The method has only meaning for the code coverage.
   */
  @Test
  public void testBusinessDomains() {
    businessUnit = new BusinessUnit();
    Assert.assertNotNull(businessUnit.getBusinessDomains());

    Set<BusinessDomain> expected = hashSet();
    Set<BusinessDomain> actual;

    expected.add(new BusinessDomain());

    businessUnit.setBusinessDomains(expected);
    actual = businessUnit.getBusinessDomains();

    assertEquals(expected, actual);
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessUnit#setParent()}
   * {@link de.iteratec.iteraplan.model.BusinessUnit#getParent()} The parent should be at the
   * beginning null. At the end two objects will be compared and they should be equal. The method
   * has only meaning for the code coverage.
   */
  @Test
  public void testParent() {
    businessUnit = new BusinessUnit();
    Assert.assertNull(businessUnit.getParent());

    // Add a parent:
    BusinessUnit expected = new BusinessUnit();
    BusinessUnit actual;
    expected.setId(Integer.valueOf(1));

    businessUnit.setParent(expected);
    actual = businessUnit.getParent();

    assertEquals(expected, actual);
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessUnit#setChildren()}
   * {@link de.iteratec.iteraplan.model.BusinessUnit#getChildren()} The list with the children
   * should be at the beginning not null. Then two other lists are created. At the end the two lists
   * should be equal. The method has only meaning for the code coverage.
   */
  @Test
  public void testChildrenCaseParentChildNotEqual() {
    businessUnit = new BusinessUnit();
    assertNotNull(businessUnit.getChildren());

    // Add some children:
    List<BusinessUnit> expected = arrayList();
    List<BusinessUnit> actual;

    BusinessUnit child = new BusinessUnit();
    child.setId(Integer.valueOf(1));
    child.setName("child1");

    expected.add(child);

    businessUnit.setChildren(expected);
    actual = businessUnit.getChildren();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#addBusinessDomains(java.util.Set)} The method
   * tests if the addBusinessDomain adds properly an empty list with BusinessDomains.
   */
  @Test
  public void testAddBusinessDomainsCaseEmptyList() {
    businessUnit = new BusinessUnit();

    Set<BusinessDomain> expectedBDomains = hashSet();
    Set<BusinessDomain> actualBDomains = businessUnit.getBusinessDomains();

    businessUnit.addBusinessDomains(expectedBDomains);
    assertEquals(expectedBDomains, actualBDomains);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#addInformationSystems(java.util.Set)} The method
   * tests if the setInformationSystemRelease() adds properly an empty list with information systems.
   */
  @Test
  public void testAddInformationSystemsCaseNull() {

    businessUnit = new BusinessUnit();
    Set<BusinessMapping> businessMappings = businessUnit.getBusinessMappings();
    assertNotNull(businessMappings);

    for (BusinessMapping bm : businessMappings) {
      assertNotNull(bm.getInformationSystemRelease());
    }

    BusinessMapping firstBusinessMapping = new BusinessMapping();
    firstBusinessMapping.setInformationSystemRelease(null);

    Set<BusinessMapping> expected = hashSet();
    Set<BusinessMapping> actual = businessUnit.getBusinessMappings();

    expected.add(firstBusinessMapping);
    businessUnit.addBusinessMappings(expected);
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#addBusinessDomains(java.util.Set)} The method
   * tests if the addBusinessDomain adds properly a list with BusinessDomains. The both sides of the
   * association should be properly set.
   */
  @Test
  public void testAddBusinessDomainsCaseNotEmptyList() {
    businessUnit = new BusinessUnit();

    // set with BUs : for the second side of the association
    Set<BusinessUnit> expectedBUs = hashSet();
    expectedBUs.add(businessUnit);

    // set with BDs : first side of the association
    Set<BusinessDomain> expectedBDs = hashSet();

    // add some businessDomains in the set
    BusinessDomain firstBD = new BusinessDomain();
    firstBD.setId(Integer.valueOf(1));
    firstBD.setName("first BD");
    expectedBDs.add(firstBD);

    BusinessDomain secondBD = new BusinessDomain();
    secondBD.setId(Integer.valueOf(2));
    secondBD.setName("sec BD");
    expectedBDs.add(secondBD);

    // initialize the businessDomains
    businessUnit.addBusinessDomains(expectedBDs);
    Set<BusinessDomain> actualBDs = businessUnit.getBusinessDomains();

    // test the first side of the association
    assertEquals(expectedBDs, actualBDs);

    // test the second side of the association
    for (BusinessDomain bd : actualBDs) {
      Set<BusinessUnit> actualBUs = bd.getBusinessUnits();
      assertEquals("Fail to update both sides of the association", expectedBUs, actualBUs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#addInformationSystems(java.util.Set)} The method
   * tests if the setInformationSystemRelease() adds properly list with information systems.
   * Both sides of the association should be set.
   */
  @Test
  public void testAddInformationSystemsCaseNotNull() {

    businessUnit = new BusinessUnit();

    // set to test the second side of the association
    Set<BusinessUnit> expectedBUs = hashSet();
    expectedBUs.add(businessUnit);
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

    businessUnit.addBusinessMappings(expectedBMs);
    Set<BusinessMapping> actualBMs = businessUnit.getBusinessMappings();
    assertEquals(expectedBMs, actualBMs);

    for (BusinessMapping bm : actualBMs) {
      BusinessUnit actualBU = bm.getBusinessUnit();
      assertEquals("Fail to update both sides of the association", businessUnit, actualBU);
    }

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#addParent(de.iteratec.iteraplan.model.BusinessUnit)}
   * The method tests if the addParent() really throws IllegalArgumentException when the user tries to
   * set the parent with null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddParentCaseNull() {
    businessUnit = new BusinessUnit();
    businessUnit.addParent(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#addParent(de.iteratec.iteraplan.model.BusinessUnit)}
   * The method tests if the method sets itself as parent. An element can not be parent of itself,
   * that is why we expect IteraplanBusinessException.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentCaseAddItself() {
    businessUnit = new BusinessUnit();
    businessUnit.setId(Integer.valueOf(1));
    businessUnit.addParent(businessUnit);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#addParent(de.iteratec.iteraplan.model.BusinessUnit)}
   * The method tests if the method detects cycles. An element A can not have an element B as its
   * parent if B already has A as its parent.
   */
  @Test
  public void testAddParentCaseCycle() {
    businessUnit = new BusinessUnit();
    businessUnit.setId(Integer.valueOf(1));
    BusinessUnit parent1 = new BusinessUnit();
    parent1.setId(Integer.valueOf(2));
    parent1.addParent(businessUnit);
    try {
      businessUnit.addParent(parent1);
      fail("addParent() should have thrown an exception!");
    } catch (IteraplanBusinessException e) {
      assertEquals(null, businessUnit.getParent());
      // make sure that the names can still be displayed (no cycle exists).
      businessUnit.getHierarchicalName();
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#addParent(de.iteratec.iteraplan.model.BusinessUnit)}
   * The method tests if the AddParent method sets correctly another object as parent.
   */
  @Test
  public void testAddParentCaseAddOtherBU() {
    businessUnit = new BusinessUnit();

    BusinessUnit parent = new BusinessUnit();
    parent.setId(1);

    businessUnit.addParent(parent);

    assertEquals(parent, businessUnit.getParent());

    // check that object is removed from old parent's child list
    BusinessUnit newParent = new BusinessUnit();
    newParent.setId(3);
    assertEquals(0, newParent.getChildren().size());
    assertEquals(1, parent.getChildren().size());
    businessUnit.addParent(newParent);
    assertEquals(1, newParent.getChildren().size());
    assertEquals(0, parent.getChildren().size());

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessUnit#removeParent()} The method
   * tests if the removeParent method removes the parent object correctly. The attributes of the
   * parent object save same information.
   */
  @Test
  public void testRemoveParentCaseNotNull() {
    businessUnit = new BusinessUnit();

    BusinessUnit parent = new BusinessUnit();
    parent.setId(Integer.valueOf(65));
    parent.setName("parent");

    BusinessUnit child = new BusinessUnit();
    List<BusinessUnit> children = arrayList();
    children.add(child);
    parent.setChildren(children);

    businessUnit.addParent(parent);
    businessUnit.removeParent();

    BusinessUnit expectedParent = null;
    BusinessUnit actualParent = businessUnit.getParent();

    assertEquals(expectedParent, actualParent);

    List<BusinessUnit> expectedChildren = arrayList();
    List<BusinessUnit> actualChildren = businessUnit.getChildren();

    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessUnit#removeParent()} The method
   * tests if the method removes the parent object. The attributes of the parent object saves no
   * information.
   */
  @Test
  public void testRemoveParentCaseNull() {
    businessUnit = new BusinessUnit();
    businessUnit.removeParent();

    BusinessUnit expectedParent = null;
    BusinessUnit actualParent = businessUnit.getParent();

    assertEquals(expectedParent, actualParent);

    List<BusinessUnit> expectedChildren = arrayList();
    List<BusinessUnit> actualChildren = businessUnit.getChildren();

    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#removeBusinessDomainRelations()} The method
   * tests if the removeBusinessDomainRelations method removes all the domain relations correctly.
   */
  @Test
  public void testRemoveBusinessDomainRelations() {
    businessUnit = new BusinessUnit();

    Set<BusinessDomain> expectedBDs = hashSet();

    BusinessDomain firstBD = new BusinessDomain();
    BusinessDomain secondBD = new BusinessDomain();

    expectedBDs.add(firstBD);
    expectedBDs.add(secondBD);

    businessUnit.addBusinessDomains(expectedBDs);
    businessUnit.removeBusinessDomainRelations();

    Set<BusinessDomain> expected = new BusinessUnit().getBusinessDomains();
    Set<BusinessDomain> actual = businessUnit.getBusinessDomains();
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#removeInformationSystemReleaseRelations()}
   */
  @Test
  public void testRemoveInformationSystemReleaseRelations() {

    businessUnit = new BusinessUnit();
    Set<BusinessMapping> expectedBMs = hashSet();

    BusinessMapping firstBM = new BusinessMapping();
    BusinessMapping secondBM = new BusinessMapping();
    InformationSystemRelease firstISR = new InformationSystemRelease();
    InformationSystemRelease secondISR = new InformationSystemRelease();

    firstBM.addInformationSystemRelease(firstISR);
    firstBM.addInformationSystemRelease(secondISR);
    secondBM.addInformationSystemRelease(firstISR);

    businessUnit.addBusinessMappings(expectedBMs);
    businessUnit.removeBusinessMappings();

    Set<BusinessMapping> expected = new BusinessProcess().getBusinessMappings();
    Set<BusinessMapping> actual = businessUnit.getBusinessMappings();
    assertEquals(expected, actual);

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessUnit#removeAllChildren()} The method
   * tests if the removeAllChildren method correctly removes all the children.
   */
  @Test
  public void testRemoveAllChildren() {
    businessUnit = new BusinessUnit();

    // List with children
    List<BusinessUnit> children = new ArrayList<BusinessUnit>();

    // add children
    children.add(new BusinessUnit());
    children.add(new BusinessUnit());
    children.add(new BusinessUnit());
    children.add(new BusinessUnit());
    children.add(new BusinessUnit());

    businessUnit.setChildren(children);
    businessUnit.removeAllChildren();

    List<BusinessUnit> expectedChildren = arrayList();
    List<BusinessUnit> actualChildren = businessUnit.getChildren();

    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * this object is bigger.
   */
  @Test
  public void testCompareToCaseThisBigger() {
    BusinessUnit first = new BusinessUnit();
    BusinessUnit second = new BusinessUnit();
    second.setName("second");
    first.setParent(second);
    assertEquals(1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are empty and equal.
   */
  @Test
  public void testCompareToCaseEmptyObjects() {
    BusinessUnit first = new BusinessUnit();
    BusinessUnit second = new BusinessUnit();
    assertEquals(0, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * second object is bigger.
   */
  @Test
  public void testCompareToCaseOtherBigger() {
    BusinessUnit first = new BusinessUnit();
    first.setName(TEST_NAME_1);
    BusinessUnit second = new BusinessUnit();

    assertEquals(-1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are equal. .
   */
  @Test
  public void testCompareToCaseEqualsObjects() {
    BusinessUnit first = new BusinessUnit();
    BusinessUnit second = new BusinessUnit();
    assertEquals(0, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is.
   */
  @Test
  public void testCompareToCaseParentEquals() {
    BusinessUnit parent = new BusinessUnit();
    parent.setId(Integer.valueOf(55));
    parent.setName("parent");
    parent.setDescription("test");

    BusinessUnit first = new BusinessUnit();
    first.setParent(parent);
    first.setId(Integer.valueOf(1));

    BusinessUnit second = new BusinessUnit();
    second.setParent(parent);
    second.setId(Integer.valueOf(2));

    List<BusinessUnit> children = arrayList();
    children.add(first);
    children.add(second);

    parent.setChildren(children);

    assertEquals(1, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsFirst() {
    // Initialize
    // firstParent
    BusinessUnit firstParent = new BusinessUnit();
    firstParent.setId(Integer.valueOf(55));
    firstParent.setName(PARENT_NAME_1);

    // children
    BusinessUnit first = new BusinessUnit();
    first.setId(Integer.valueOf(167));
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);

    // secondParent
    BusinessUnit secParent = new BusinessUnit();
    secParent.setId(Integer.valueOf(60));
    secParent.setName(PARENT_NAME_2);

    // children
    BusinessUnit second = new BusinessUnit();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessUnit> firstChildren = arrayList();
    firstChildren.add(first);
    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessUnit> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    BusinessUnit root = new BusinessUnit();
    root.setName("root");
    root.setId(Integer.valueOf(1000));
    ArrayList<BusinessUnit> rootChildren = new ArrayList<BusinessUnit>();
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
   * {@link de.iteratec.iteraplan.model.BusinessUnit#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsSec() {
    // Initialize
    BusinessUnit top = new BusinessUnit();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    BusinessUnit root = new BusinessUnit();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    BusinessUnit firstParent = new BusinessUnit();
    firstParent.setId(Integer.valueOf(55));

    // children
    BusinessUnit first = new BusinessUnit();
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    BusinessUnit secParent = new BusinessUnit();
    secParent.setId(Integer.valueOf(60));
    secParent.setName(PARENT_NAME_2);
    secParent.setDescription("test");

    // children
    BusinessUnit second = new BusinessUnit();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessUnit> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessUnit> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<BusinessUnit> rootChildren = new ArrayList<BusinessUnit>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<BusinessUnit> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(-1, root.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessUnit#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The second will be with the root compared. The
   * result should be +1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsThird() {
    // Initialize
    BusinessUnit top = new BusinessUnit();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    BusinessUnit root = new BusinessUnit();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    BusinessUnit firstParent = new BusinessUnit();
    firstParent.setId(Integer.valueOf(55));

    // children
    BusinessUnit first = new BusinessUnit();
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    BusinessUnit secParent = new BusinessUnit();
    secParent.setId(Integer.valueOf(60));
    secParent.setName(PARENT_NAME_2);
    secParent.setDescription("test");

    // children
    BusinessUnit second = new BusinessUnit();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessUnit> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessUnit> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<BusinessUnit> rootChildren = new ArrayList<BusinessUnit>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<BusinessUnit> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(1, second.compareTo(root));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessUnit#getI18NKey()} The method tests
   * the getI18NKey() method. The test method has only meaning for the code coverage.
   */
  @Test
  public void testGetI18NKey() {
    businessUnit = new BusinessUnit();
    assertEquals("businessUnit.virtualElement", businessUnit.getI18NKey());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessUnit#getChildrenAsList()} The method
   * tests if the getChildrenAsList() correctly returns the list with the children as a list.
   */
  @Test
  public void testGetChildrenAsList() {
    businessUnit = new BusinessUnit();
    assertNotNull(businessUnit.getChildrenAsList());

    // Add some children:
    List<BusinessUnit> expected = arrayList();

    // child 1
    BusinessUnit firstChild = new BusinessUnit();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("child1");
    expected.add(firstChild);

    // child 2 = child 3
    BusinessUnit secChild = new BusinessUnit();
    expected.add(secChild);
    expected.add(secChild);

    businessUnit.setChildren(expected);

    List<BusinessUnit> actual = businessUnit.getChildrenAsList();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessUnit#getChildrenAsSet()} The method
   * tests if the GetChildrenAsSet() correctly returns the list with the children as a set.
   */
  @Test
  public void testGetChildrenAsSet() {
    businessUnit = new BusinessUnit();
    assertNotNull(businessUnit.getChildrenAsSet());

    // Add some children:
    List<BusinessUnit> expected = arrayList();
    Set<BusinessUnit> actual = hashSet();

    // child 1
    BusinessUnit firstChild = new BusinessUnit();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("child1");
    expected.add(firstChild);

    // child 2 = child 3
    BusinessUnit secChild = new BusinessUnit();
    expected.add(secChild);
    expected.add(secChild);

    businessUnit.setChildren(expected);
    actual = businessUnit.getChildrenAsSet();

    assertNotNull(actual);
    assertTrue(actual.contains(firstChild));
    assertTrue(actual.contains(secChild));

    // the expected size of the set should be with one smaller than the size of the list.
    int expectedSize = expected.size() - 1;
    int actualSize = actual.size();
    assertEquals(expectedSize, actualSize);
  }
}

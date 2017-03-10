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
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * @author rfe
 * @author mma
 */
@SuppressWarnings("boxing")
public class BusinessProcessTest {

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

  private BusinessProcess classUnderTest = null;

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessProcess#getTypeOfBuildingBlock()}
   */
  @Test
  public void testGetTypeOfBuildingBlock() {
    assertEquals(TypeOfBuildingBlock.BUSINESSPROCESS, new BusinessProcess().getTypeOfBuildingBlock());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#addBusinessDomains(java.util.Set)} The method
   * tests if the AddBusinessDomains() throws correctly IllegalArgumentException if the user tries to
   * initialize businessDomains with set with null element in it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddBusinessDomainsCaseNull() {
    classUnderTest = new BusinessProcess();
    assertNotNull(classUnderTest.getBusinessDomains());

    Set<BusinessDomain> expected = hashSet();

    // set with null element
    expected.add(null);
    classUnderTest.addBusinessDomains(expected);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#addInformationSystems(java.util.Set)} The method
   * tests if the setInformationSystemRelease() throws correctly IllegalArgumentException if the user tries to
   * initialize information systems with set with null element in it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddInformationSystemsCaseNull() {

    classUnderTest = new BusinessProcess();
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
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#addBusinessDomains(java.util.Set)} The method
   * tests if the addBusinessDomain adds properly set with BusinessDomains. The both sides of the
   * association should be set.
   */
  @Test
  public void testAddBusinessDomainsFirstCaseNotNull() {
    classUnderTest = new BusinessProcess();

    // set to test the second side of the association
    Set<BusinessProcess> expectedBPs = hashSet();
    expectedBPs.add(classUnderTest);

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
      Set<BusinessProcess> actualBPs = bd.getBusinessProcesses();
      assertEquals("Fail to update both sides of the association", expectedBPs, actualBPs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#addInformationSystems(java.util.Set)} The method
   * tests if the setInformationSystemRelease() adds properly a set with information systems. Both sides of the
   * association should be set.
   */
  @Test
  public void testAddInformationSystemsCaseNotNull() {

    classUnderTest = new BusinessProcess();

    Set<BusinessProcess> expectedBPs = hashSet();
    expectedBPs.add(classUnderTest);
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
      BusinessProcess actualBP = bm.getBusinessProcess();
      assertEquals("Fail to update both sides of the association", classUnderTest, actualBP);
    }

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#addBusinessDomains(java.util.Set)} The
   * method tests if the addBusinessDomain adds properly set with BusinessDomains. The both sides of
   * the association should be set.
   */
  @Test
  public void testAddBusinessDomainsSecCaseNotNull() {
    classUnderTest = new BusinessProcess();

    // set to test the second side of the association
    Set<BusinessProcess> expectedBPs = hashSet();
    expectedBPs.add(classUnderTest);

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
      Set<BusinessProcess> actualBPs = bd.getBusinessProcesses();
      assertEquals("Fail to update both sides of the association", expectedBPs, actualBPs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#addParent(de.iteratec.iteraplan.model.BusinessProcess)}
   * The method tests if the method sets itself as parent. An element cannot be parent of itself,
   * that is why we expect IteraplanBusinessException.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentCaseAddItself() {
    classUnderTest = new BusinessProcess();
    classUnderTest.setId(1);
    classUnderTest.addParent(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#addParent(de.iteratec.iteraplan.model.BusinessProcess)}
   * The method tests if the method detects cycles. An element A can not have an element B as its
   * parent if B already has A as its parent.
   */
  @Test
  public void testAddParentCaseCycle() {
    classUnderTest = new BusinessProcess();
    classUnderTest.setId(Integer.valueOf(1));
    BusinessProcess parent1 = new BusinessProcess();
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
   * {@link de.iteratec.iteraplan.model.BusinessProcess#addParent(de.iteratec.iteraplan.model.BusinessProcess)}
   * The method tests if the AddParent method sets correctly another object as parent.
   */
  @Test
  public void testAddParentCaseAddOtherBP() {
    classUnderTest = new BusinessProcess();
    classUnderTest.setId(2);

    BusinessProcess parent = new BusinessProcess();
    parent.setId(1);
    classUnderTest.addParent(parent);

    assertEquals(classUnderTest.getParent(), parent);

    // check that object is removed from old parent's child list
    BusinessProcess newParent = new BusinessProcess();
    newParent.setId(3);
    assertEquals(0, newParent.getChildren().size());
    assertEquals(1, parent.getChildren().size());
    classUnderTest.addParent(newParent);
    assertEquals(1, newParent.getChildren().size());
    assertEquals(0, parent.getChildren().size());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * this object is bigger.
   */
  @Test
  public void testCompareToCaseThisBigger() {
    BusinessProcess first = new BusinessProcess();
    BusinessProcess second = new BusinessProcess();
    second.setName("second");
    first.setParent(second);
    assertEquals(1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are empty and equal.
   */
  @Test
  public void testCompareToCaseEmptyObjects() {
    BusinessProcess first = new BusinessProcess();
    BusinessProcess second = new BusinessProcess();
    assertEquals(0, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * second object is bigger.
   */
  @Test
  public void testCompareToCaseOtherBigger() {
    BusinessProcess first = new BusinessProcess();
    first.setName(TEST_NAME_1);
    BusinessProcess second = new BusinessProcess();

    assertEquals(-1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are equal. .
   */
  @Test
  public void testCompareToCaseEqualsObjects() {
    BusinessProcess first = new BusinessProcess();
    BusinessProcess second = new BusinessProcess();
    assertEquals(0, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is.
   */
  @Test
  public void testCompareToCaseParentEquals() {
    BusinessProcess parent = new BusinessProcess();
    parent.setId(Integer.valueOf(55));
    parent.setName("parent");
    parent.setDescription("test");

    BusinessProcess first = new BusinessProcess();
    first.setParent(parent);
    first.setId(Integer.valueOf(1));

    BusinessProcess second = new BusinessProcess();
    second.setParent(parent);
    second.setId(Integer.valueOf(2));

    List<BusinessProcess> children = arrayList();
    children.add(first);
    children.add(second);

    parent.setChildren(children);

    assertEquals(1, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsFirst() {
    // Initialize
    // firstParent
    BusinessProcess firstParent = new BusinessProcess();
    firstParent.setId(Integer.valueOf(55));
    firstParent.setName("firstParent");

    // children
    BusinessProcess first = new BusinessProcess();
    first.setId(Integer.valueOf(167));
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);

    // secondParent
    BusinessProcess secParent = new BusinessProcess();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");

    // children
    BusinessProcess second = new BusinessProcess();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessProcess> firstChildren = arrayList();
    firstChildren.add(first);
    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessProcess> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    BusinessProcess root = new BusinessProcess();
    root.setName("root");
    root.setId(Integer.valueOf(1000));
    ArrayList<BusinessProcess> rootChildren = new ArrayList<BusinessProcess>();
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
   * {@link de.iteratec.iteraplan.model.BusinessProcess#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsSec() {
    // Initialize
    BusinessProcess top = new BusinessProcess();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    BusinessProcess root = new BusinessProcess();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    BusinessProcess firstParent = new BusinessProcess();
    firstParent.setId(Integer.valueOf(55));

    // children
    BusinessProcess first = new BusinessProcess();
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    BusinessProcess secParent = new BusinessProcess();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription("test");

    // children
    BusinessProcess second = new BusinessProcess();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessProcess> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessProcess> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<BusinessProcess> rootChildren = new ArrayList<BusinessProcess>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<BusinessProcess> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(-1, root.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The second will be with the root compared. The
   * result should be +1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsThird() {
    // Initialize
    BusinessProcess top = new BusinessProcess();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    BusinessProcess root = new BusinessProcess();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    BusinessProcess firstParent = new BusinessProcess();
    firstParent.setId(Integer.valueOf(55));

    // children
    BusinessProcess first = new BusinessProcess();
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    BusinessProcess secParent = new BusinessProcess();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription("test");

    // children
    BusinessProcess second = new BusinessProcess();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessProcess> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessProcess> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<BusinessProcess> rootChildren = new ArrayList<BusinessProcess>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<BusinessProcess> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(1, second.compareTo(root));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessProcess#getChildrenAsSet()} The
   * method tests if the getChildrenAsSet() correctly returns the list of the children as aset.
   */
  @Test
  public void testGetChildrenAsSet() {
    classUnderTest = new BusinessProcess();
    assertNotNull(classUnderTest.getChildrenAsSet());

    Set<BusinessProcess> expected = hashSet();

    // initialization list
    List<BusinessProcess> initList = arrayList();

    // add some elements
    BusinessProcess firstChild = new BusinessProcess();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("first Child");
    initList.add(firstChild);
    expected.add(firstChild);

    BusinessProcess thirdChild = new BusinessProcess();
    thirdChild.setId(Integer.valueOf(3));
    thirdChild.setName("third Child");
    initList.add(thirdChild);
    expected.add(thirdChild);

    BusinessProcess secChild = new BusinessProcess();
    secChild.setId(Integer.valueOf(2));
    secChild.setName("sec Child");
    initList.add(secChild);
    expected.add(secChild);

    classUnderTest.setChildren(initList);
    Set<BusinessProcess> actual = classUnderTest.getChildrenAsSet();

    assertEquals(Boolean.valueOf(expected.contains(firstChild)), Boolean.valueOf(actual.contains(firstChild)));
    assertEquals(Boolean.valueOf(expected.contains(secChild)), Boolean.valueOf(actual.contains(secChild)));
    assertEquals(Boolean.valueOf(expected.contains(thirdChild)), Boolean.valueOf(actual.contains(thirdChild)));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#removeBusinessDomainRelations()}
   */
  @Test
  public void testRemoveBusinessDomainRelations() {
    classUnderTest = new BusinessProcess();
    Set<BusinessDomain> expectedBDomains = hashSet();

    BusinessDomain firstBD = new BusinessDomain();
    BusinessDomain secondBD = new BusinessDomain();
    BusinessDomain thirdBD = new BusinessDomain();
    BusinessDomain fourthBD = new BusinessDomain();

    expectedBDomains.add(firstBD);
    expectedBDomains.add(secondBD);
    expectedBDomains.add(thirdBD);
    expectedBDomains.add(fourthBD);

    classUnderTest.addBusinessDomains(expectedBDomains);
    classUnderTest.removeBusinessDomainRelations();

    Set<BusinessDomain> expected = new BusinessProcess().getBusinessDomains();
    Set<BusinessDomain> actual = classUnderTest.getBusinessDomains();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#removeInformationSystemReleaseRelations()}
   */
  @Test
  public void testRemoveInformationSystemReleaseRelations() {

    classUnderTest = new BusinessProcess();
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
   * Test method for {@link de.iteratec.iteraplan.model.BusinessProcess#removeParent()} The method
   * tests if the removeParent method removes the parent object correctly. The attributes of the
   * parent object save same information.
   */
  @Test
  public void testRemoveParentCaseNotNull() {
    classUnderTest = new BusinessProcess();
    classUnderTest.setId(1);

    BusinessProcess parent = new BusinessProcess();
    parent.setId(Integer.valueOf(2));
    classUnderTest.addParent(parent);
    classUnderTest.removeParent();

    BusinessProcess expected = new BusinessProcess().getParent();
    BusinessProcess actual = classUnderTest.getParent();

    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessProcess#removeParent()} The method
   * tests if the method removes the parent object. The attributes of the parent object saves no
   * information.
   */
  @Test
  public void testRemoveParentCaseNull() {
    classUnderTest = new BusinessProcess();
    classUnderTest.removeParent();

    BusinessProcess expected = new BusinessProcess().getParent();
    BusinessProcess actual = classUnderTest.getParent();

    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessProcess#removeAllChildren()} The
   * method tests the removeAllChildren() if it removes correctly all the Children relations.
   */
  @Test
  public void testRemoveAllChildren() {
    classUnderTest = new BusinessProcess();

    // set the children relations
    BusinessProcess firstBP = new BusinessProcess();
    BusinessProcess secondBP = new BusinessProcess();
    BusinessProcess thirdBP = new BusinessProcess();

    List<BusinessProcess> children = arrayList();
    children.add(firstBP);
    children.add(secondBP);
    children.add(thirdBP);

    classUnderTest.setChildren(children);
    classUnderTest.removeAllChildren();

    List<BusinessProcess> expected = new BusinessProcess().getChildren();
    List<BusinessProcess> actual = classUnderTest.getChildren();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessProcess#setBusinessDomains(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.BusinessProcess#getBusinessDomains()} The method tests if
   * the addBusinessDomain adds properly an empty list with BusinessDomains.
   */
  @Test
  public void testBusinessDomains() {
    classUnderTest = new BusinessProcess();
    assertNotNull(classUnderTest.getBusinessDomains());

    Set<BusinessDomain> expected = new HashSet<BusinessDomain>();
    Set<BusinessDomain> actual;

    expected.add(new BusinessDomain());

    classUnderTest.setBusinessDomains(expected);
    actual = classUnderTest.getBusinessDomains();

    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessProcess#getI18NKey()} The method has
   * only meaning for the code coverage.
   */
  @Test
  public void testGetI18NKey() {
    classUnderTest = new BusinessProcess();

    assertEquals("businessProcess.virtualElement", classUnderTest.getI18NKey());
  }
}

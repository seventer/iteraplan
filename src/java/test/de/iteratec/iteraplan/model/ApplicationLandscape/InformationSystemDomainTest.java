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

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 * @author rfe
 * @author mma
 */
public class InformationSystemDomainTest {

  private static final String TEST_ISD_NAME               = "first";
  private static final String CHILD_NAME_1                = "child1";
  private static final String UPDATE_BOTH_SIDES_ERROR_MSG = "Fail to update both sides of the association";

  private InformationSystemDomain isd;

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#getTypeOfBuildingBlock()} The method
   * has only meaning for the code coverage.
   */
  @Test
  public void testGetTypeOfBuildingBlock() {
    assertEquals(new InformationSystemDomain().getTypeOfBuildingBlock().toString(), "informationSystemDomain.singular");
  }

  /**
   * Test method for (getter and setter):
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#getInformationSystemReleases()} and
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#setInformationSystemReleases(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method has only meaning for the code coverage.
   */
  @Test
  public void testInformationSystemReleases() {
    isd = new InformationSystemDomain();
    assertNotNull(isd.getInformationSystemReleases());

    Set<InformationSystemRelease> expected = new HashSet<InformationSystemRelease>();
    Set<InformationSystemRelease> actual;

    expected.add(new InformationSystemRelease());

    isd.setInformationSystemReleases(expected);
    actual = isd.getInformationSystemReleases();

    assertEquals(expected, actual);
  }

  /**
   * Test method for (getter and setter):
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#getParent()} and
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#setParent(de.iteratec.iteraplan.model.InformationSystemDomain)}
   * The method has only meaning for the code coverage.
   */
  @Test
  public void testParent() {
    isd = new InformationSystemDomain();
    assertNull(isd.getParent());

    // Add a parent:
    InformationSystemDomain expected = new InformationSystemDomain();
    InformationSystemDomain actual;
    expected.setId(Integer.valueOf(1));
    expected.setName("parent1");

    isd.setParent(expected);
    actual = isd.getParent();

    assertNotNull(actual);
    assertEquals(expected, actual);
  }

  /**
   * Test method for (getter and setter):
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#getChildren()} and
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#setChildren(java.util.List)}
   */
  @Test
  public void testGetChildren() {
    isd = new InformationSystemDomain();
    assertNotNull(isd.getInformationSystemReleases());

    // Add some children:
    List<InformationSystemDomain> expected = new ArrayList<InformationSystemDomain>();
    List<InformationSystemDomain> actual;
    InformationSystemDomain child = new InformationSystemDomain();
    child.setId(Integer.valueOf(1));
    child.setName(CHILD_NAME_1);

    expected.add(child);

    isd.setChildren(expected);
    actual = isd.getChildren();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#addInformationSystemReleases(java.util.Set)}
   * The method tests if the addInformationSystemReleases(Set<InformationSystemRelease> set) throws
   * correctly IllegalArgumentException if the user tries to add set of InformationSystemReleases with a
   * null as an element in it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddInformationSystemReleasesAddNull() {
    isd = new InformationSystemDomain();
    assertNotNull(isd.getInformationSystemReleases());

    Set<InformationSystemRelease> expected = new HashSet<InformationSystemRelease>();

    InformationSystemRelease isr = new InformationSystemRelease();
    expected.add(isr);
    expected.add(null);

    isd.addInformationSystemReleases(expected);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#addInformationSystemReleases(java.util.Set)}
   * The method tests if the addInformationSystemReleases(Set<InformationSystemRelease> set) adds
   * correctly a set of InformationSystemReleases. The both sides of the association should be set
   * correctly.
   */
  @Test
  public void testAddInformationSystemReleases() {
    isd = new InformationSystemDomain();
    assertNotNull(isd.getInformationSystemReleases());

    Set<InformationSystemRelease> expected = new HashSet<InformationSystemRelease>();
    Set<InformationSystemRelease> actual;

    InformationSystemRelease isr = new InformationSystemRelease();
    expected.add(isr);

    isd.addInformationSystemReleases(expected);
    actual = isd.getInformationSystemReleases();

    assertEquals(expected, actual);
    assertTrue(UPDATE_BOTH_SIDES_ERROR_MSG, isr.getInformationSystemDomains().contains(isd));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#addParent(de.iteratec.iteraplan.model.InformationSystemDomain)}
   * The method tests if the method sets itself as parent. An element can not be parent of itself,
   * that is why we expect IteraplanBusinessException.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentCaseAddItself() {
    isd = new InformationSystemDomain();
    isd.setId(Integer.valueOf(1));
    isd.addParent(isd);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#addParent(de.iteratec.iteraplan.model.InformationSystemDomain)}
   * The method tests if the method detects cycles. An element A can not have an element B as its
   * parent if B already has A as its parent.
   */
  @Test
  public void testAddParentCaseCycle() {
    isd = new InformationSystemDomain();
    isd.setId(Integer.valueOf(1));
    InformationSystemDomain parent1 = new InformationSystemDomain();
    parent1.setId(Integer.valueOf(2));
    parent1.addParent(isd);
    try {
      isd.addParent(parent1);
      fail("addParent() should have thrown an exception!");
    } catch (IteraplanBusinessException e) {
      assertEquals(null, isd.getParent());
      // make sure that the names can still be displayed (no cycle exists).
      isd.getHierarchicalName();
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#addParent(de.iteratec.iteraplan.model.InformationSystemDomain)}
   * The method tests if the AddParent method sets correctly another InformationSystemRelease as
   * parent. Here once again the both sides of the association should be set.
   */
  @Test
  public void testAddParentCaseAddOtherISD() {
    isd = new InformationSystemDomain();

    InformationSystemDomain expectedParent = new InformationSystemDomain();
    expectedParent.setId(Integer.valueOf(1));

    assertFalse(expectedParent.getChildren().contains(isd));

    isd.addParent(expectedParent);
    InformationSystemDomain actualParent = isd.getParent();

    assertEquals(expectedParent, actualParent);
    assertTrue(UPDATE_BOTH_SIDES_ERROR_MSG, expectedParent.getChildren().contains(isd));

    // check that object is removed from old parent's child list
    InformationSystemDomain newParent = new InformationSystemDomain();
    newParent.setId(Integer.valueOf(3));
    assertEquals(0, newParent.getChildren().size());
    assertEquals(1, expectedParent.getChildren().size());
    isd.addParent(newParent);
    assertEquals(1, newParent.getChildren().size());
    assertEquals(0, expectedParent.getChildren().size());

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#removeInformationSystemReleases()}
   * The method tests if the removeInformationSystemReleases() removes correctly all the
   * InformationSystemReleases.
   */
  @Test
  public void testRemoveInformationReleases() {
    isd = new InformationSystemDomain();

    // initialization set
    Set<InformationSystemRelease> expectedISRs = hashSet();

    // put some elements into the set
    InformationSystemRelease firstISR = new InformationSystemRelease();
    InformationSystemRelease secondISR = new InformationSystemRelease();
    InformationSystemRelease thirdISR = new InformationSystemRelease();
    firstISR.setId(Integer.valueOf(1));
    secondISR.setId(Integer.valueOf(2));
    thirdISR.setId(Integer.valueOf(3));
    expectedISRs.add(firstISR);
    expectedISRs.add(secondISR);
    expectedISRs.add(thirdISR);

    isd.addInformationSystemReleases(expectedISRs);
    // test the other side of the association is also correctly set
    assertTrue(secondISR.getInformationSystemDomains().contains(isd));

    // remove
    isd.removeInformationSystemReleases();

    // test the both sides of the association after the remove
    Set<InformationSystemRelease> expected = new InformationSystemDomain().getInformationSystemReleases();
    Set<InformationSystemRelease> actual = isd.getInformationSystemReleases();
    assertEquals(expected, actual);

    assertFalse(UPDATE_BOTH_SIDES_ERROR_MSG, secondISR.getInformationSystemDomains().contains(isd));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemDomain#removeParent()} The
   * method tests if the removeParent() correctly removes the parent association.
   */
  @Test
  public void testRemoveParent() {
    isd = new InformationSystemDomain();
    assertNull(isd.getParent());

    // Add a parent:
    InformationSystemDomain expectedParent = new InformationSystemDomain();
    expectedParent.setId(Integer.valueOf(1));
    expectedParent.setName("parent1");

    isd.addParent(expectedParent);
    assertTrue(expectedParent.getChildren().contains(isd));

    // remove
    isd.removeParent();

    // test the both sides of the association after the remove
    InformationSystemDomain expected = null;
    InformationSystemDomain actual = isd.getParent();

    assertEquals(expected, actual);
    assertFalse(UPDATE_BOTH_SIDES_ERROR_MSG, expectedParent.getChildren().contains(isd));

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemDomain#removeAllChildren()}
   * The method tests if the removeAllChildren() removes correctly all the children associations.
   */
  @Test
  public void testRemoveAllChildren() {
    isd = new InformationSystemDomain();
    assertNotNull(isd.getChildrenAsList());

    // Add some children:
    List<InformationSystemDomain> expected = new ArrayList<InformationSystemDomain>();
    InformationSystemDomain child = new InformationSystemDomain();
    child.setId(Integer.valueOf(1));
    child.setName(CHILD_NAME_1);
    expected.add(child);
    isd.setChildren(expected);

    List<InformationSystemDomain> actual = isd.getChildren();
    assertEquals(1, actual.size());
    assertEquals(expected, actual);

    isd.removeAllChildren();
    actual = isd.getChildren();
    assertNotNull(actual);
    assertEquals(0, actual.size());
    assertNotSame(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * this object is bigger.
   */
  @Test
  public void testCompareToCaseThisBigger() {
    InformationSystemDomain first = new InformationSystemDomain();
    InformationSystemDomain second = new InformationSystemDomain();
    second.setName("second");
    first.setParent(second);
    assertEquals(1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are empty and equal.
   */
  @Test
  public void testCompareToCaseEmptyObjects() {
    InformationSystemDomain first = new InformationSystemDomain();
    InformationSystemDomain second = new InformationSystemDomain();
    assertEquals(0, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * second object is bigger.
   */
  @Test
  public void testCompareToCaseOtherBigger() {
    InformationSystemDomain first = new InformationSystemDomain();
    first.setName(TEST_ISD_NAME);
    InformationSystemDomain second = new InformationSystemDomain();

    assertEquals(-1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are equal. .
   */
  @Test
  public void testCompareToCaseEqualsObjects() {
    InformationSystemDomain first = new InformationSystemDomain();
    InformationSystemDomain second = new InformationSystemDomain();
    assertEquals(0, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is.
   */
  @Test
  public void testCompareToCaseParentEquals() {
    InformationSystemDomain parent = new InformationSystemDomain();
    parent.setId(Integer.valueOf(55));
    parent.setName("parent");
    parent.setDescription("test");

    InformationSystemDomain first = new InformationSystemDomain();
    first.setParent(parent);
    first.setId(Integer.valueOf(1));

    InformationSystemDomain second = new InformationSystemDomain();
    second.setParent(parent);
    second.setId(Integer.valueOf(2));

    List<InformationSystemDomain> children = arrayList();
    children.add(first);
    children.add(second);

    parent.setChildren(children);

    assertEquals(1, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsFirst() {
    // Initialize
    // firstParent
    InformationSystemDomain firstParent = new InformationSystemDomain();
    firstParent.setId(Integer.valueOf(55));
    firstParent.setName("firstParent");

    // children
    InformationSystemDomain first = new InformationSystemDomain();
    first.setId(Integer.valueOf(167));
    first.setName(TEST_ISD_NAME);
    first.setParent(firstParent);

    // secondParent
    InformationSystemDomain secParent = new InformationSystemDomain();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");

    // children
    InformationSystemDomain second = new InformationSystemDomain();
    second.setName("sec");
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<InformationSystemDomain> firstChildren = arrayList();
    firstChildren.add(first);
    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<InformationSystemDomain> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    InformationSystemDomain root = new InformationSystemDomain();
    root.setName("root");
    root.setId(Integer.valueOf(1000));
    ArrayList<InformationSystemDomain> rootChildren = new ArrayList<InformationSystemDomain>();
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
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsSec() {
    // Initialize
    InformationSystemDomain top = new InformationSystemDomain();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    InformationSystemDomain root = new InformationSystemDomain();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    InformationSystemDomain firstParent = new InformationSystemDomain();
    firstParent.setId(Integer.valueOf(55));

    // children
    InformationSystemDomain first = new InformationSystemDomain();
    first.setName(TEST_ISD_NAME);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    InformationSystemDomain secParent = new InformationSystemDomain();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription("test");

    // children
    InformationSystemDomain second = new InformationSystemDomain();
    second.setName("sec");
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<InformationSystemDomain> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<InformationSystemDomain> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<InformationSystemDomain> rootChildren = new ArrayList<InformationSystemDomain>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<InformationSystemDomain> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(-1, root.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The second will be with the root compared. The
   * result should be +1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsThird() {
    // Initialize
    InformationSystemDomain top = new InformationSystemDomain();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    InformationSystemDomain root = new InformationSystemDomain();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    InformationSystemDomain firstParent = new InformationSystemDomain();
    firstParent.setId(Integer.valueOf(55));

    // children
    InformationSystemDomain first = new InformationSystemDomain();
    first.setName(TEST_ISD_NAME);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    InformationSystemDomain secParent = new InformationSystemDomain();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription("test");

    // children
    InformationSystemDomain second = new InformationSystemDomain();
    second.setName("sec");
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<InformationSystemDomain> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<InformationSystemDomain> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<InformationSystemDomain> rootChildren = new ArrayList<InformationSystemDomain>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<InformationSystemDomain> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(1, second.compareTo(root));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemDomain#getI18NKey()} The
   * test has only meaning for the code coverage.
   */
  @Test
  public void testGetI18NKey() {
    isd = new InformationSystemDomain();
    assertEquals(isd.getI18NKey(), "informationSystemDomain.virtualElement");
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemDomain#getChildrenAsList()}
   * The method tests if the getChildrenAsList() returns correctly the set of the children as a
   * list.
   */
  @Test
  public void testGetChildrenAsList() {
    isd = new InformationSystemDomain();
    assertNotNull(isd.getChildrenAsList());

    // Add some children:
    List<InformationSystemDomain> expected = new ArrayList<InformationSystemDomain>();

    InformationSystemDomain firstChild = new InformationSystemDomain();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName(CHILD_NAME_1);
    expected.add(firstChild);

    InformationSystemDomain thirdChild = new InformationSystemDomain();
    thirdChild.setId(Integer.valueOf(3));
    thirdChild.setName("child3");
    expected.add(thirdChild);

    InformationSystemDomain secChild = new InformationSystemDomain();
    secChild.setId(Integer.valueOf(2));
    secChild.setName("child2");
    expected.add(secChild);

    isd.setChildren(expected);

    List<InformationSystemDomain> actual;
    actual = isd.getChildrenAsList();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemDomain#getChildrenAsSet()}
   * The method tests if the getChildrenAsSet() returns correctly the set of the children as a set.
   */
  @Test
  public void testGetChildrenAsSet() {
    isd = new InformationSystemDomain();
    assertNotNull(isd.getChildrenAsSet());

    // Add some children:
    List<InformationSystemDomain> expected = new ArrayList<InformationSystemDomain>();

    InformationSystemDomain firstChild = new InformationSystemDomain();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName(CHILD_NAME_1);
    expected.add(firstChild);

    InformationSystemDomain thirdChild = new InformationSystemDomain();
    thirdChild.setId(Integer.valueOf(3));
    thirdChild.setName("child3");
    expected.add(thirdChild);

    InformationSystemDomain secChild = new InformationSystemDomain();
    secChild.setId(Integer.valueOf(2));
    secChild.setName("child2");
    expected.add(secChild);

    isd.setChildren(expected);
    Set<InformationSystemDomain> actual = isd.getChildrenAsSet();

    assertNotNull(actual);
    assertTrue(actual.contains(firstChild));
    assertTrue(actual.contains(secChild));
    assertTrue(actual.contains(thirdChild));
    assertEquals(expected.size(), actual.size());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemDomain#getParentElement()}
   * The method has only meaning for the code coverage.
   */
  @Test
  public void testGetParentElement() {
    isd = new InformationSystemDomain();
    assertNull(isd.getParentElement());

    InformationSystemDomain expected = new InformationSystemDomain();
    InformationSystemDomain actual;
    expected.setId(Integer.valueOf(1));
    expected.setName("parent");

    isd.setParent(expected);
    actual = isd.getParent();

    assertEquals(expected, actual);
  }
}

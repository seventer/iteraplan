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
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * @author rfe
 * @author mma
 */
public class ArchitecturalDomainTest {

  private static final String TEST_NAME_1      = "first";
  private static final String TEST_DESCRIPTION = "testDescription";

  @Before
  public void setUp() throws Exception {
    // not yet used
  }

  @After
  public void tearDown() throws Exception {
    // not yet used
  }

  private ArchitecturalDomain classUnderTest = null;

  /**
   * Test method for (getter)
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#getTypeOfBuildingBlock()} The method has
   * only meaning for the code coverage.
   */
  @Test
  public void testgetTypeOfBuildingBlock() {
    classUnderTest = new ArchitecturalDomain();

    assertEquals("architecturalDomain.singular", classUnderTest.getTypeOfBuildingBlock().toString());
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#setTechnicalComponentReleases()}
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#getTechnicalComponentReleases()} The
   * attribute technicalComponentReleases must be at the beginning empty but not null. Then two sets
   * are created. The fist one is empty and the second one is null. At the end the two sets must be
   * equals. The method has only meaning for the code coverage.
   */
  @Test
  public void testTechnicalComponentReleases() {
    classUnderTest = new ArchitecturalDomain();
    assertNotNull(classUnderTest.getTechnicalComponentReleases());

    Set<TechnicalComponentRelease> expected = new HashSet<TechnicalComponentRelease>();
    Set<TechnicalComponentRelease> actual;

    expected.add(new TechnicalComponentRelease());

    classUnderTest.setTechnicalComponentReleases(expected);
    actual = classUnderTest.getTechnicalComponentReleases();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#addTechnicalComponentRelease(java.util.Set)}
   * The method tests if the addTechnicalComponentReleases adds properly set with
   * TechnicalComponentReleases.
   */
  @Test
  public void testAddTechnicalComponentReleasesCaseEmptySet() {
    classUnderTest = new ArchitecturalDomain();

    Set<TechnicalComponentRelease> expectedTCRs = hashSet();

    classUnderTest.addTechnicalComponentReleases(expectedTCRs);
    Set<TechnicalComponentRelease> actualTCRs = classUnderTest.getTechnicalComponentReleases();

    assertEquals(expectedTCRs, actualTCRs);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#addTechnicalComponentRelease(java.util.Set)}
   * The method tests if the addTechnicalComponentReleases adds properly set with
   * TechnicalComponentReleases. The both sides of the association should be set.
   */
  @Test
  public void testAddBusinessDomainsFirstCaseNotEmptySet() {
    classUnderTest = new ArchitecturalDomain();

    // initialization set
    Set<TechnicalComponentRelease> expectedTCRs = hashSet();
    // set to test the second side of the association
    Set<ArchitecturalDomain> expectedADs = hashSet();
    expectedADs.add(classUnderTest);

    TechnicalComponentRelease firstTCR = new TechnicalComponentRelease();
    TechnicalComponentRelease secTCR = new TechnicalComponentRelease();

    firstTCR.setId(Integer.valueOf(1));
    secTCR.setId(Integer.valueOf(2));

    expectedTCRs.add(firstTCR);
    expectedTCRs.add(secTCR);

    classUnderTest.addTechnicalComponentReleases(expectedTCRs);
    Set<TechnicalComponentRelease> actualTCRs = classUnderTest.getTechnicalComponentReleases();
    assertEquals(expectedTCRs, actualTCRs);

    // test the second side of the association
    for (TechnicalComponentRelease tcr : actualTCRs) {
      Set<ArchitecturalDomain> actualADs = tcr.getArchitecturalDomains();
      assertEquals("Fail to update both sides of the association", expectedADs, actualADs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#addTechnicalComponentRelease(java.util.Set)}
   * The method tests if the addTechnicalComponentReleases adds properly set with
   * TechnicalComponentReleases. The both sides of the association should be set.
   */
  @Test
  public void testAddBusinessDomainsSecCaseNotEmptySet() {
    classUnderTest = new ArchitecturalDomain();

    // initialization set
    Set<TechnicalComponentRelease> expectedTCRs = hashSet();
    // set to test the second side of the association
    Set<ArchitecturalDomain> expectedADs = hashSet();
    expectedADs.add(classUnderTest);

    TechnicalComponentRelease firstTCR = new TechnicalComponentRelease();
    TechnicalComponentRelease secTCR = new TechnicalComponentRelease();

    expectedTCRs.add(firstTCR);
    expectedTCRs.add(secTCR);

    classUnderTest.addTechnicalComponentReleases(expectedTCRs);

    Set<TechnicalComponentRelease> actualTCRs = classUnderTest.getTechnicalComponentReleases();
    assertEquals(expectedTCRs, actualTCRs);

    // test the second side of the association
    for (TechnicalComponentRelease tcr : actualTCRs) {
      Set<ArchitecturalDomain> actualADs = tcr.getArchitecturalDomains();
      assertEquals("Fail to update both sides of the association", expectedADs, actualADs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#addParent(de.iteratec.iteraplan.model.ArchitecturalDomain)}
   * The method tests if the addParent() really throws IllegalArgumentException when the user tries to
   * set the parent with null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddParentCaseAddNull() {
    classUnderTest = new ArchitecturalDomain();
    classUnderTest.addParent(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#addParent(de.iteratec.iteraplan.model.ArchitecturalDomain)}
   * The method tests if the method sets itself as parent. An element can not be parent of itself,
   * that is why we expect IteraplanBusinessException.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentCaseAddItself() {
    classUnderTest = new ArchitecturalDomain();
    classUnderTest.setId(Integer.valueOf(1));
    classUnderTest.addParent(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#addParent(de.iteratec.iteraplan.model.ArchitecturalDomain)}
   * The method tests if the method detects cycles. An element A can not have an element B as its
   * parent if B already has A as its parent.
   */
  @Test
  public void testAddParentCaseCycle() {
    classUnderTest = new ArchitecturalDomain();
    classUnderTest.setId(Integer.valueOf(1));
    ArchitecturalDomain parent1 = new ArchitecturalDomain();
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
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#addParent(de.iteratec.iteraplan.model.ArchitecturalDomain)}
   * The method tests if the AddParent method sets correctly another object as parent.
   */
  @Test
  public void testAddParentCaseAddOtherAD() {
    classUnderTest = new ArchitecturalDomain();
    ArchitecturalDomain parent = new ArchitecturalDomain();
    parent.setId(Integer.valueOf(1));
    classUnderTest.addParent(parent);

    assertEquals(parent, classUnderTest.getParent());

    // check that object is removed from old parent's child list
    ArchitecturalDomain newParent = new ArchitecturalDomain();
    newParent.setId(Integer.valueOf(3));
    assertEquals(0, newParent.getChildren().size());
    assertEquals(1, parent.getChildren().size());
    classUnderTest.addParent(newParent);
    assertEquals(1, newParent.getChildren().size());
    assertEquals(0, parent.getChildren().size());

  }

  /**
   * Test method for Test method for
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#removeTechnicalComponentReleases()} The
   * method tests if the removeTechnicalComponentReleases method removes all the
   * TechnicalComponentReleases correctly.
   */
  @Test
  public void testRemoveTechnicalComponentReleases() {
    classUnderTest = new ArchitecturalDomain();
    Set<TechnicalComponentRelease> tcrs = hashSet();

    TechnicalComponentRelease firstTCR = new TechnicalComponentRelease();
    tcrs.add(firstTCR);
    TechnicalComponentRelease secTCR = new TechnicalComponentRelease();
    tcrs.add(secTCR);
    TechnicalComponentRelease thirdTCR = new TechnicalComponentRelease();
    tcrs.add(thirdTCR);

    classUnderTest.addTechnicalComponentReleases(tcrs);
    classUnderTest.removeTechnicalComponentReleases();

    Set<TechnicalComponentRelease> expectedTCRs = hashSet();
    Set<TechnicalComponentRelease> actualTCRs = classUnderTest.getTechnicalComponentReleases();

    assertEquals(expectedTCRs, actualTCRs);
  }

  /**
   * Test method for Test method for
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#removeParent()} The method tests if the
   * removeParent method removes the parent object correctly. The attributes of the parent object
   * save same information.
   */
  @Test
  public void testRemoveParentCaseNotNull() {
    classUnderTest = new ArchitecturalDomain();
    ArchitecturalDomain parent = new ArchitecturalDomain();
    parent.setName("parent");
    classUnderTest.addParent(parent);
    classUnderTest.removeParent();

    ArchitecturalDomain expectedParent = null;
    ArchitecturalDomain actualParent = classUnderTest.getParent();

    assertEquals(expectedParent, actualParent);

    List<ArchitecturalDomain> expectedChildren = arrayList();
    List<ArchitecturalDomain> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for Test method for
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#removeParent()} The method tests if the
   * method removes the parent object. The attributes of the parent object saves no information.
   */
  @Test
  public void testRemoveParentCaseNull() {
    classUnderTest = new ArchitecturalDomain();
    classUnderTest.removeParent();

    ArchitecturalDomain expectedParent = null;
    ArchitecturalDomain actualParent = classUnderTest.getParent();

    assertEquals(expectedParent, actualParent);

    List<ArchitecturalDomain> expectedChildren = arrayList();
    List<ArchitecturalDomain> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.ArchitecturalDomain#removeAllChildren()} The
   * method tests if the removeAllChildren() correctly removes all the children associations.
   */
  @Test
  public void testRemoveAllChildren() {
    classUnderTest = new ArchitecturalDomain();
    List<ArchitecturalDomain> children = new ArrayList<ArchitecturalDomain>();

    children.add(new ArchitecturalDomain());
    children.add(new ArchitecturalDomain());
    children.add(new ArchitecturalDomain());
    children.add(new ArchitecturalDomain());
    children.add(new ArchitecturalDomain());

    classUnderTest.setChildren(children);
    classUnderTest.removeAllChildren();

    List<ArchitecturalDomain> expectedChildren = arrayList();
    List<ArchitecturalDomain> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for
   *{@link de.iteratec.iteraplan.model.ArchitecturalDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * this object is bigger.
   */
  @Test
  public void testCompareToCaseThisBigger() {
    ArchitecturalDomain first = new ArchitecturalDomain();
    ArchitecturalDomain second = new ArchitecturalDomain();
    second.setName("second");
    first.setParent(second);
    assertEquals(1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are empty and equal.
   */
  @Test
  public void testCompareToCaseEmptyObjects() {
    ArchitecturalDomain first = new ArchitecturalDomain();
    ArchitecturalDomain second = new ArchitecturalDomain();
    assertEquals(0, first.compareTo(second));
  }

  /**
   * Test method for
   *{@link de.iteratec.iteraplan.model.ArchitecturalDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * second object is bigger.
   */
  @Test
  public void testCompareToCaseOtherBigger() {
    ArchitecturalDomain first = new ArchitecturalDomain();
    first.setName(TEST_NAME_1);
    ArchitecturalDomain second = new ArchitecturalDomain();

    assertEquals(-1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are equal.
   */
  @Test
  public void testCompareToCaseEqualsObjects() {
    ArchitecturalDomain first = new ArchitecturalDomain();
    ArchitecturalDomain second = new ArchitecturalDomain();
    assertEquals(0, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is.
   */
  @Test
  public void testCompareToCaseParentEquals() {
    ArchitecturalDomain parent = new ArchitecturalDomain();
    parent.setId(Integer.valueOf(55));
    parent.setName("parent");
    parent.setDescription(TEST_DESCRIPTION);

    ArchitecturalDomain first = new ArchitecturalDomain();
    first.setParent(parent);
    first.setId(Integer.valueOf(1));

    ArchitecturalDomain second = new ArchitecturalDomain();
    second.setParent(parent);
    second.setId(Integer.valueOf(2));

    List<ArchitecturalDomain> children = arrayList();
    children.add(first);
    children.add(second);

    parent.setChildren(children);

    assertEquals(1, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsFirst() {
    // Initialize
    // firstParent
    ArchitecturalDomain firstParent = new ArchitecturalDomain();
    firstParent.setId(Integer.valueOf(55));
    firstParent.setName("firstParent");

    // children
    ArchitecturalDomain first = new ArchitecturalDomain();
    first.setId(Integer.valueOf(167));
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);

    // secondParent
    ArchitecturalDomain secParent = new ArchitecturalDomain();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");

    // children
    ArchitecturalDomain second = new ArchitecturalDomain();
    second.setName("sec");
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<ArchitecturalDomain> firstChildren = arrayList();
    firstChildren.add(first);
    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<ArchitecturalDomain> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArchitecturalDomain root = new ArchitecturalDomain();
    root.setName("root");
    root.setId(Integer.valueOf(1000));
    ArrayList<ArchitecturalDomain> rootChildren = new ArrayList<ArchitecturalDomain>();
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
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsSec() {
    // Initialize
    ArchitecturalDomain top = new ArchitecturalDomain();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    ArchitecturalDomain root = new ArchitecturalDomain();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    ArchitecturalDomain firstParent = new ArchitecturalDomain();
    firstParent.setId(Integer.valueOf(55));

    // children
    ArchitecturalDomain first = new ArchitecturalDomain();
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    ArchitecturalDomain secParent = new ArchitecturalDomain();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription(TEST_DESCRIPTION);

    // children
    ArchitecturalDomain second = new ArchitecturalDomain();
    second.setName("sec");
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<ArchitecturalDomain> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<ArchitecturalDomain> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<ArchitecturalDomain> rootChildren = new ArrayList<ArchitecturalDomain>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<ArchitecturalDomain> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(-1, root.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.ArchitecturalDomain#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The second will be with the root compared. The
   * result should be +1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsThird() {
    // Initialize
    ArchitecturalDomain top = new ArchitecturalDomain();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    ArchitecturalDomain root = new ArchitecturalDomain();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    ArchitecturalDomain firstParent = new ArchitecturalDomain();
    firstParent.setId(Integer.valueOf(55));

    // children
    ArchitecturalDomain first = new ArchitecturalDomain();
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    ArchitecturalDomain secParent = new ArchitecturalDomain();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription(TEST_DESCRIPTION);

    // children
    ArchitecturalDomain second = new ArchitecturalDomain();
    second.setName("sec");
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<ArchitecturalDomain> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<ArchitecturalDomain> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<ArchitecturalDomain> rootChildren = new ArrayList<ArchitecturalDomain>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<ArchitecturalDomain> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(1, second.compareTo(root));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.ArchitecturalDomain#getI18NKey()} The method
   * tests the getI18NKey() method. The test method has only meaning for the code coverage.
   */
  @Test
  public void testGetI18NKey() {
    classUnderTest = new ArchitecturalDomain();
    assertEquals("architecturalDomain.virtualElement", classUnderTest.getI18NKey());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.ArchitecturalDomain#getChildrenAsList()} The
   * method tests if the GetChildrenAsList() correctly returns the list with the children as a list.
   */
  @Test
  public void testGetChildrenAsList() {
    classUnderTest = new ArchitecturalDomain();

    // Add some children:
    List<ArchitecturalDomain> expected = arrayList();

    ArchitecturalDomain thirdChild = new ArchitecturalDomain();
    thirdChild.setId(Integer.valueOf(3));
    thirdChild.setName("child3");
    expected.add(thirdChild);

    ArchitecturalDomain secChild = new ArchitecturalDomain();
    secChild.setId(Integer.valueOf(2));
    secChild.setName("child2");
    expected.add(secChild);

    ArchitecturalDomain firstChild = new ArchitecturalDomain();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("child1");
    expected.add(firstChild);

    classUnderTest.setChildren(expected);

    List<ArchitecturalDomain> actual = classUnderTest.getChildrenAsList();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.ArchitecturalDomain#getChildrenAsSet()} The
   * method tests if the GetChildrenAsSet() correctly returns the list with the children as a set.
   */
  @Test
  public void testGetChildrenAsSet() {
    classUnderTest = new ArchitecturalDomain();
    // Add some children:
    List<ArchitecturalDomain> expected = arrayList();
    Set<ArchitecturalDomain> actual;

    // child 1
    ArchitecturalDomain firstChild = new ArchitecturalDomain();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("child1");
    expected.add(firstChild);

    // child 2 = child 3
    ArchitecturalDomain secChild = new ArchitecturalDomain();
    expected.add(secChild);
    expected.add(secChild);

    classUnderTest.setChildren(expected);
    actual = classUnderTest.getChildrenAsSet();

    assertNotNull(actual);
    assertTrue(actual.contains(firstChild));
    assertTrue(actual.contains(secChild));
    assertEquals(expected.size() - 1, actual.size());
  }
}

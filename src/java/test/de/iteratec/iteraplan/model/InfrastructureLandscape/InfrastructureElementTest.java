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
package de.iteratec.iteraplan.model.InfrastructureLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * @author rfe
 * @author mma
 */
@SuppressWarnings("boxing")
public class InfrastructureElementTest {

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

  private InfrastructureElement classUnderTest = null;

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#getTypeOfBuildingBlock()} The method
   * has only meaning for the code coverage.
   */
  @Test
  public void testGgetTypeOfBuildingBlock() {
    classUnderTest = new InfrastructureElement();

    assertEquals("infrastructureElement.singular", classUnderTest.getTypeOfBuildingBlock().toString());
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#addInformationSystemReleases(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#getInformationSystemReleases()} The
   * method tests if the addInformationSystemReleases adds properly an empty list with
   * InformationSystemReleases.
   */
  @Test
  public void testAddInformationSystemReleasesCaseEmptyList() {
    classUnderTest = new InfrastructureElement();

    Set<InformationSystemRelease> expectedISRs = hashSet();
    classUnderTest.addInformationSystemReleases(expectedISRs);
    assertEquals(classUnderTest.getInformationSystemReleases(), expectedISRs);
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#addInformationSystemReleases(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#getInformationSystemReleases()} The
   * method tests if the addInformationSystemReleases adds properly an empty list with
   * InformationSystemReleases.
   */
  @Test
  public void testAddInformationSystemReleasesCaseNotEmptyList() {
    classUnderTest = new InfrastructureElement();

    // set to test the second side of the association
    Set<InfrastructureElement> expectedIEs = hashSet();
    expectedIEs.add(classUnderTest);

    // initialization set
    Set<InformationSystemRelease> expectedISRs = hashSet();

    InformationSystemRelease firstISR = new InformationSystemRelease();
    InformationSystemRelease secondISR = new InformationSystemRelease();
    InformationSystemRelease thirdISR = new InformationSystemRelease();
    InformationSystemRelease fourthISR = new InformationSystemRelease();
    InformationSystemRelease fifthISR = new InformationSystemRelease();

    expectedISRs.add(firstISR);
    expectedISRs.add(secondISR);
    expectedISRs.add(thirdISR);
    expectedISRs.add(fourthISR);
    expectedISRs.add(fifthISR);

    classUnderTest.addInformationSystemReleases(expectedISRs);
    Set<InformationSystemRelease> actualISRs = classUnderTest.getInformationSystemReleases();

    // test the first side of the association
    assertEquals(expectedISRs, actualISRs);

    // test the second side of the association
    for (InformationSystemRelease isr : actualISRs) {
      Set<InfrastructureElement> actualIEs = isr.getInfrastructureElements();
      assertEquals("Fail to update both sides of the association", expectedIEs, actualIEs);
    }
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#addTechnicalComponentReleases(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#getTechnicalComponentReleases()} The
   * method tests if the addTechnicalComponentReleases adds properly an empty list with
   * TechnicalComponentReleases.
   */
  @Test
  public void testAddTechnicalComponentReleasesCaseNotEmptyList() {
    classUnderTest = new InfrastructureElement();

    // set to test the second side of the association
    Set<InfrastructureElement> expectedIEs = hashSet();
    expectedIEs.add(classUnderTest);

    // initialization set
    Set<TechnicalComponentRelease> expectedTCRs = hashSet();

    TechnicalComponentRelease firstTCR = new TechnicalComponentRelease();
    TechnicalComponentRelease secondTCR = new TechnicalComponentRelease();
    TechnicalComponentRelease thirdTCR = new TechnicalComponentRelease();
    TechnicalComponentRelease fourthTCR = new TechnicalComponentRelease();
    TechnicalComponentRelease fifthTCR = new TechnicalComponentRelease();

    expectedTCRs.add(firstTCR);
    expectedTCRs.add(secondTCR);
    expectedTCRs.add(thirdTCR);
    expectedTCRs.add(fourthTCR);
    expectedTCRs.add(fifthTCR);
    for (TechnicalComponentRelease expTcr : expectedTCRs) {
      new Tcr2IeAssociation(expTcr, classUnderTest).connect();
    }

    Set<TechnicalComponentRelease> actualTCRs = classUnderTest.getTechnicalComponentReleases();

    // test the first side of the association
    assertEquals(expectedTCRs, actualTCRs);

    // test the second side of the association
    for (TechnicalComponentRelease isr : actualTCRs) {
      Set<InfrastructureElement> actualIEs = isr.getInfrastructureElements();
      assertEquals("Fail to update both sides of the association", expectedIEs, actualIEs);
    }
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#addTechnicalComponentRelease(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#getTechnicalComponentReleases()} The
   * method tests if the addTechnicalComponentReleases adds properly an empty list with
   * TechnicalComponentReleases.
   */
  @Test
  public void testAddTechnicalComponentRelease() {
    classUnderTest = new InfrastructureElement();
    assertNotNull(classUnderTest.getTechnicalComponentReleases());

    // set to test the second side of the association
    Set<InfrastructureElement> expectedIEs = hashSet();
    expectedIEs.add(classUnderTest);

    // initialization set
    Set<TechnicalComponentRelease> expectedTCR = hashSet();
    TechnicalComponentRelease tcr = new TechnicalComponentRelease();
    expectedTCR.add(tcr);

    new Tcr2IeAssociation(tcr, classUnderTest).connect();
    Set<TechnicalComponentRelease> actualTCR = classUnderTest.getTechnicalComponentReleases();

    // test the first side of the association
    assertEquals(expectedTCR, actualTCR);

    // test the second side of the association
    for (TechnicalComponentRelease isr : actualTCR) {
      Set<InfrastructureElement> actualIEs = isr.getInfrastructureElements();
      assertEquals("Fail to update both sides of the association", expectedIEs, actualIEs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#addParent(de.iteratec.iteraplan.model.InfrastructureElement)}
   * The method tests if the addParent() really throws IllegalArgumentException when the user tries to
   * set the parent with null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddParentCaseNull() {
    classUnderTest = new InfrastructureElement();
    classUnderTest.addParent(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#addParent(de.iteratec.iteraplan.model.InfrastructureElement)}
   * The method tests if the method sets itself as parent. An element can not be parent of itself,
   * that is why we expect IteraplanBusinessException.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentCaseAddItself() {
    classUnderTest = new InfrastructureElement();
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
    classUnderTest = new InfrastructureElement();
    classUnderTest.setId(Integer.valueOf(1));
    InfrastructureElement parent1 = new InfrastructureElement();
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
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#addParent(de.iteratec.iteraplan.model.InfrastructureElement)}
   * The method tests if the AddParent method sets correctly another object as parent.
   */
  @Test
  public void testAddParentCaseAddOtherIE() {
    classUnderTest = new InfrastructureElement();
    InfrastructureElement parent = new InfrastructureElement();
    parent.setId(Integer.valueOf(1));
    classUnderTest.addParent(parent);

    assertEquals(parent, classUnderTest.getParent());

    // check that object is removed from old parent's child list
    InfrastructureElement newParent = new InfrastructureElement();
    newParent.setId(3);
    assertEquals(0, newParent.getChildren().size());
    assertEquals(1, parent.getChildren().size());
    classUnderTest.addParent(newParent);
    assertEquals(1, newParent.getChildren().size());
    assertEquals(0, parent.getChildren().size());

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * this object is bigger.
   */
  @Test
  public void testCompareToCaseThisBigger() {
    InfrastructureElement first = new InfrastructureElement();
    InfrastructureElement second = new InfrastructureElement();
    second.setName("second");
    first.setParent(second);
    assertEquals(1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are empty and equal.
   */
  @Test
  public void testCompareToCaseEmptyObjects() {
    InfrastructureElement first = new InfrastructureElement();
    InfrastructureElement second = new InfrastructureElement();
    assertEquals(0, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * second object is bigger.
   */
  @Test
  public void testCompareToCaseOtherBigger() {
    InfrastructureElement first = new InfrastructureElement();
    first.setName(TEST_NAME_1);
    InfrastructureElement second = new InfrastructureElement();

    assertEquals(-1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are equal. .
   */
  @Test
  public void testCompareToCaseEqualsObjects() {
    InfrastructureElement first = new InfrastructureElement();
    InfrastructureElement second = new InfrastructureElement();
    assertEquals(0, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is.
   */
  @Test
  public void testCompareToCaseParentEquals() {
    InfrastructureElement parent = new InfrastructureElement();
    parent.setId(Integer.valueOf(55));
    parent.setName("parent");
    parent.setDescription("test");

    InfrastructureElement first = new InfrastructureElement();
    first.setParent(parent);
    first.setId(Integer.valueOf(1));

    InfrastructureElement second = new InfrastructureElement();
    second.setParent(parent);
    second.setId(Integer.valueOf(2));

    List<InfrastructureElement> children = arrayList();
    children.add(first);
    children.add(second);

    parent.setChildren(children);

    assertEquals(1, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsFirst() {
    // Initialize
    // firstParent
    InfrastructureElement firstParent = new InfrastructureElement();
    firstParent.setId(Integer.valueOf(55));
    firstParent.setName("firstParent");

    // children
    InfrastructureElement first = new InfrastructureElement();
    first.setId(Integer.valueOf(167));
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);

    // secondParent
    InfrastructureElement secParent = new InfrastructureElement();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");

    // children
    InfrastructureElement second = new InfrastructureElement();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<InfrastructureElement> firstChildren = arrayList();
    firstChildren.add(first);
    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<InfrastructureElement> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    InfrastructureElement root = new InfrastructureElement();
    root.setName("root");
    root.setId(Integer.valueOf(1000));
    ArrayList<InfrastructureElement> rootChildren = new ArrayList<InfrastructureElement>();
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
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsSec() {
    // Initialize
    InfrastructureElement top = new InfrastructureElement();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    InfrastructureElement root = new InfrastructureElement();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    InfrastructureElement firstParent = new InfrastructureElement();
    firstParent.setId(Integer.valueOf(55));

    // children
    InfrastructureElement first = new InfrastructureElement();
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    InfrastructureElement secParent = new InfrastructureElement();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription("test");

    // children
    InfrastructureElement second = new InfrastructureElement();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<InfrastructureElement> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<InfrastructureElement> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<InfrastructureElement> rootChildren = new ArrayList<InfrastructureElement>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<InfrastructureElement> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(-1, root.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The second will be with the root compared. The
   * result should be +1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsThird() {
    // Initialize
    InfrastructureElement top = new InfrastructureElement();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    InfrastructureElement root = new InfrastructureElement();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    InfrastructureElement firstParent = new InfrastructureElement();
    firstParent.setId(Integer.valueOf(55));

    // children
    InfrastructureElement first = new InfrastructureElement();
    first.setName(TEST_NAME_1);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    InfrastructureElement secParent = new InfrastructureElement();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription("test");

    // children
    InfrastructureElement second = new InfrastructureElement();
    second.setName(TEST_NAME_2);
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<InfrastructureElement> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<InfrastructureElement> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<InfrastructureElement> rootChildren = new ArrayList<InfrastructureElement>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<InfrastructureElement> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(1, second.compareTo(root));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InfrastructureElement#getChildrenAsList()}
   * The method tests if the getChildrenAsList() correctly returns the list with the children as a
   * list.
   */
  @Test
  public void testGetChildrenAsList() {
    classUnderTest = new InfrastructureElement();
    assertNotNull(classUnderTest.getChildrenAsList());

    // Add some children:
    List<InfrastructureElement> expected = arrayList();

    // child 1
    InfrastructureElement firstChild = new InfrastructureElement();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("child1");
    expected.add(firstChild);

    // child 2 = child 3
    InfrastructureElement secChild = new InfrastructureElement();
    expected.add(secChild);
    expected.add(secChild);

    classUnderTest.setChildren(expected);

    List<InfrastructureElement> actual = classUnderTest.getChildrenAsList();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InfrastructureElement#getChildrenAsSet()}
   * The method tests if the GetChildrenAsSet() correctly returns the list with the children as a
   * set.
   */
  @Test
  public void testGetChildrenAsSet() {
    classUnderTest = new InfrastructureElement();
    assertNotNull(classUnderTest.getChildrenAsSet());

    // Add some children:
    List<InfrastructureElement> expected = arrayList();
    Set<InfrastructureElement> actual = hashSet();

    // child 1
    InfrastructureElement firstChild = new InfrastructureElement();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("child1");
    expected.add(firstChild);

    // child 2 = child 3
    InfrastructureElement secChild = new InfrastructureElement();
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

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InfrastructureElement#getI18NKey()} The
   * method has only meaning for the test coverage.
   */
  @Test
  public void testGetI18NKey() {
    classUnderTest = new InfrastructureElement();
    assertNotNull("infrastructureElement.virtualElement", classUnderTest.getI18NKey());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#removeInformationSystemReleases()}
   */
  @Test
  public void testRemoveInformationSystemReleases() {
    classUnderTest = new InfrastructureElement();

    // initialization set
    Set<InformationSystemRelease> initISRs = hashSet();
    InformationSystemRelease firstISR = new InformationSystemRelease();
    InformationSystemRelease secondISR = new InformationSystemRelease();
    InformationSystemRelease thirdISR = new InformationSystemRelease();
    InformationSystemRelease fourthISR = new InformationSystemRelease();
    InformationSystemRelease fifthISR = new InformationSystemRelease();

    initISRs.add(firstISR);
    initISRs.add(secondISR);
    initISRs.add(thirdISR);
    initISRs.add(fourthISR);
    initISRs.add(fifthISR);

    classUnderTest.addInformationSystemReleases(initISRs);
    classUnderTest.removeInformationSystemReleases();

    Set<InformationSystemRelease> expectedISRs = hashSet();
    Set<InformationSystemRelease> actualISRs = classUnderTest.getInformationSystemReleases();
    assertEquals(expectedISRs, actualISRs);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#removeTechnicalComponentsReleaseAssociations()}
   */
  @Test
  public void testRemoveTechnicalComponentsReleases() {
    classUnderTest = new InfrastructureElement();
    Set<TechnicalComponentRelease> expectedTcrs = hashSet();

    TechnicalComponentRelease firstTCR = new TechnicalComponentRelease();
    TechnicalComponentRelease secondTCR = new TechnicalComponentRelease();
    TechnicalComponentRelease thirdTCR = new TechnicalComponentRelease();
    TechnicalComponentRelease fourthTCR = new TechnicalComponentRelease();
    TechnicalComponentRelease fifthTCR = new TechnicalComponentRelease();

    expectedTcrs.add(firstTCR);
    expectedTcrs.add(secondTCR);
    expectedTcrs.add(thirdTCR);
    expectedTcrs.add(fourthTCR);
    expectedTcrs.add(fifthTCR);
    for (TechnicalComponentRelease expTcr : expectedTcrs) {
      new Tcr2IeAssociation(expTcr, classUnderTest).connect();
    }

    classUnderTest.removeTechnicalComponentsReleaseAssociations();
    assertEquals(new InfrastructureElement().getTechnicalComponentReleases(), classUnderTest.getTechnicalComponentReleases());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InfrastructureElement#removeParent()} The
   * method tests if the removeParent method removes the parent object correctly. The attributes of
   * the parent object save same information.
   */
  @Test
  public void testRemoveParentCaseNotNull() {
    classUnderTest = new InfrastructureElement();

    InfrastructureElement parent = new InfrastructureElement();
    parent.setId(Integer.valueOf(56));
    classUnderTest.addParent(parent);
    classUnderTest.removeParent();

    InfrastructureElement expectedParent = null;
    InfrastructureElement actualParent = classUnderTest.getParent();

    List<InfrastructureElement> expectedChildren = arrayList();
    List<InfrastructureElement> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedParent, actualParent);
    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InfrastructureElement#removeParent()}
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#getChildren()} The method tests if the
   * method removes the parent object. The attributes of the parent object saves no information.
   */
  @Test
  public void testRemoveParentCaseNull() {
    classUnderTest = new InfrastructureElement();
    classUnderTest.removeParent();

    InfrastructureElement expectedParent = null;
    InfrastructureElement actualParent = classUnderTest.getParent();

    List<InfrastructureElement> expectedChildren = arrayList();
    List<InfrastructureElement> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedParent, actualParent);
    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InfrastructureElement#removeAllChildren()}
   * The method tests if the removeAllChildren method correctly removes all the children.
   */
  @Test
  public void testRemoveAllChildren() {
    classUnderTest = new InfrastructureElement();

    List<InfrastructureElement> children = arrayList();
    children.add(new InfrastructureElement());
    children.add(new InfrastructureElement());
    children.add(new InfrastructureElement());
    children.add(new InfrastructureElement());
    children.add(new InfrastructureElement());

    classUnderTest.setChildren(children);
    classUnderTest.removeAllChildren();

    List<InfrastructureElement> expectedChildren = new InfrastructureElement().getChildren();
    List<InfrastructureElement> actualChildren = classUnderTest.getChildren();
    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#setInformationSystemReleases(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#setTechnicalComponentReleases(java.util.Set)}
   * The method has only meaning for the code coverage.
   */
  @Test
  public void testSetTCRsAndSetISRs() {
    classUnderTest = new InfrastructureElement();

    Set<TechnicalComponentRelease> expectedTCRElements = hashSet();
    expectedTCRElements.add(new TechnicalComponentRelease());
    expectedTCRElements.add(new TechnicalComponentRelease());
    expectedTCRElements.add(new TechnicalComponentRelease());
    expectedTCRElements.add(new TechnicalComponentRelease());
    expectedTCRElements.add(new TechnicalComponentRelease());

    classUnderTest.setTechnicalComponentReleases(expectedTCRElements);
    Set<TechnicalComponentRelease> actualTCRElements = classUnderTest.getTechnicalComponentReleases();
    assertEquals(expectedTCRElements, actualTCRElements);

    Set<InformationSystemRelease> expectedISRElements = hashSet();
    expectedISRElements.add(new InformationSystemRelease());
    expectedISRElements.add(new InformationSystemRelease());
    expectedISRElements.add(new InformationSystemRelease());
    expectedISRElements.add(new InformationSystemRelease());
    expectedISRElements.add(new InformationSystemRelease());

    classUnderTest.setInformationSystemReleases(expectedISRElements);

    Set<InformationSystemRelease> actualISRElements = classUnderTest.getInformationSystemReleases();
    assertEquals(expectedISRElements, actualISRElements);
  }
}

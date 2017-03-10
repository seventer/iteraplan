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
package de.iteratec.iteraplan.model.ProjectPortfolio;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.RuntimePeriod;


/**
 * @author rfe
 */
@SuppressWarnings({ "boxing", "PMD.TooManyMethods" })
public class ProjectTest {

  private static final String TEST_PROJ_NAME           = "first";
  private static final String TEST_DESCRIPTION         = "test";

  private static final String DATE_PATTERN             = "dd.MM.yyyy";
  private static final String STANDARD_START_DATE_2005 = "6.3.2005";
  private static final String STANDARD_END_DATE_2009   = "6.3.2009";

  @Before
  public void setUp() throws Exception {
    // not yet used
  }

  @After
  public void tearDown() throws Exception {
    // not yet used
  }

  private Project classUnderTest = null;

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.Project#getInformationSystemReleases()}
   * {@link de.iteratec.iteraplan.model.Project#addInformationSystemRelease(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The test method has only meaning for the code coverage.
   */
  @Test
  public void testInformationSystemRelease() {
    classUnderTest = new Project();
    assertNotNull(classUnderTest.getInformationSystemReleases());

    Set<InformationSystemRelease> expectedISRs = hashSet();

    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setId(Integer.valueOf(45));
    isr.setVersion("5");

    expectedISRs.add(isr);

    classUnderTest.addInformationSystemRelease(isr);
    assertEquals(expectedISRs, classUnderTest.getInformationSystemReleases());
  }

  /**
   * Test method for{@link de.iteratec.iteraplan.model.Project#getTypeOfBuildingBlock()} The method
   * tests the getTypeOfBuildingBlock() method. The test method has only meaning for the code
   * coverage.
   */
  @Test
  public void testGetTypeOfBuildingBlock() {
    assertEquals("project.singular", new Project().getTypeOfBuildingBlock().toString());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Product#addBusinessDomains(java.util.Set)}
   * The method tests if the AddInformationSystemReleases() throws correctly IllegalArgumentException if
   * the user tries to initialize informationSystemReleases with set with null element in it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddInformationSystemReleasesCaseNull() {
    classUnderTest = new Project();
    assertNotNull(classUnderTest.getInformationSystemReleases());

    Set<InformationSystemRelease> expected = hashSet();

    // set with null element
    expected.add(null);
    classUnderTest.addInformationSystemReleases(expected);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Project#getInformationSystemReleases()}
   * {@link de.iteratec.iteraplan.model.Project#addInformationSystemReleases(java.util.Set)} The
   * method tests if the addInformationSystemReleases() adds correctly set with
   * InformationSystemReleases. The both sides of the connection should be set.
   */
  @Test
  public void testAddInformationSystemReleasesFirstCaseNotNull() {
    classUnderTest = new Project();
    assertNotNull(classUnderTest.getInformationSystemReleases());

    // set to test the other side of the association
    Set<Project> expectedPs = hashSet();
    expectedPs.add(classUnderTest);

    // initialization set
    Set<InformationSystemRelease> expectedISRs = hashSet();

    InformationSystemRelease firstISR = new InformationSystemRelease();
    firstISR.setId(Integer.valueOf(45));

    expectedISRs.add(firstISR);

    classUnderTest.addInformationSystemReleases(expectedISRs);
    Set<InformationSystemRelease> actualISRs = classUnderTest.getInformationSystemReleases();
    assertEquals(expectedISRs, actualISRs);

    // test the other side of the association
    for (InformationSystemRelease isr : actualISRs) {
      Set<Project> actualPs = isr.getProjects();
      assertEquals("Fail to update both sides of the association", expectedPs, actualPs);
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Project#getInformationSystemReleases()}
   * {@link de.iteratec.iteraplan.model.Project#addInformationSystemReleases(java.util.Set)} The
   * method tests if the addInformationSystemReleases() adds correctly set with
   * InformationSystemReleases. The both sides of the connection should be set.
   */
  @Test
  public void testAddInformationSystemReleasesSecCaseNotNull() {
    classUnderTest = new Project();
    assertNotNull(classUnderTest.getInformationSystemReleases());

    // set to test the other side of the association
    Set<Project> expectedPs = hashSet();
    expectedPs.add(classUnderTest);

    // initialization set
    Set<InformationSystemRelease> expectedISRs = hashSet();
    InformationSystemRelease firstISR = new InformationSystemRelease();
    expectedISRs.add(firstISR);

    classUnderTest.addInformationSystemReleases(expectedISRs);
    Set<InformationSystemRelease> actualISRs = classUnderTest.getInformationSystemReleases();
    assertEquals(expectedISRs, actualISRs);

    // test the other side of the association
    for (InformationSystemRelease isr : actualISRs) {
      Set<Project> actualPs = isr.getProjects();
      assertEquals("Fail to update both sides of the association", expectedPs, actualPs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#addParent(de.iteratec.iteraplan.model.Project)} The
   * method tests if the addParent() really throws IllegalArgumentException when we try to set the
   * parent to null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddParentCaseNull() {
    classUnderTest = new Project();
    classUnderTest.addParent(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#addParent(de.iteratec.iteraplan.model.Project)} The
   * method tests if the method sets itself as parent. An element can not be parent of itself, that
   * is why we expect IteraplanBusinessException.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentCaseAddItself() {
    classUnderTest = new Project();
    classUnderTest.setId(Integer.valueOf(1));
    classUnderTest.addParent(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#addParent(de.iteratec.iteraplan.model.Project)} The
   * method tests if the method detects cycles. An element A can not have an element B as its parent
   * if B already has A as its parent.
   */
  @Test
  public void testAddParentCaseCycle() {
    classUnderTest = new Project();
    classUnderTest.setId(Integer.valueOf(1));
    Project parent1 = new Project();
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
   * {@link de.iteratec.iteraplan.model.Project#addParent(de.iteratec.iteraplan.model.Project)} The
   * method tests if the AddParent method sets correctly another object as parent.
   */
  @Test
  public void testAddParentCaseAddOtherP() {
    Project parent = new Project();
    parent.setId(Integer.valueOf(1));
    classUnderTest = new Project();
    classUnderTest.addParent(parent);

    assertEquals(classUnderTest.getParent(), parent);

    // check that object is removed from old parent's child list
    Project newParent = new Project();
    newParent.setId(3);
    assertEquals(0, newParent.getChildren().size());
    assertEquals(1, parent.getChildren().size());
    classUnderTest.addParent(newParent);
    assertEquals(1, newParent.getChildren().size());
    assertEquals(0, parent.getChildren().size());

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * this object is bigger.
   */
  @Test
  public void testCompareToCaseThisBigger() {
    Project first = new Project();
    Project second = new Project();
    second.setName("second");
    first.setParent(second);
    assertEquals(1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are empty and equal.
   */
  @Test
  public void testCompareToCaseEmptyObjects() {
    Project first = new Project();
    Project second = new Project();
    assertEquals(0, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * second object is bigger.
   */
  @Test
  public void testCompareToCaseOtherBigger() {
    Project first = new Project();
    first.setName(TEST_PROJ_NAME);
    Project second = new Project();

    assertEquals(-1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are equal. .
   */
  @Test
  public void testCompareToCaseEqualsObjects() {
    Project first = new Project();
    Project second = new Project();
    assertEquals(0, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is.
   */
  @Test
  public void testCompareToCaseParentEquals() {
    Project parent = new Project();
    parent.setId(Integer.valueOf(55));
    parent.setName("parent");
    parent.setDescription(TEST_DESCRIPTION);

    Project first = new Project();
    first.setParent(parent);
    first.setId(Integer.valueOf(1));

    Project second = new Project();
    second.setParent(parent);
    second.setId(Integer.valueOf(2));

    List<Project> children = arrayList();
    children.add(first);
    children.add(second);

    parent.setChildren(children);

    assertEquals(1, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsFirst() {
    // Initialize
    // firstParent
    Project firstParent = new Project();
    firstParent.setId(Integer.valueOf(55));
    firstParent.setName("firstParent");

    // children
    Project first = new Project();
    first.setId(Integer.valueOf(167));
    first.setName(TEST_PROJ_NAME);
    first.setParent(firstParent);

    // secondParent
    Project secParent = new Project();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");

    // children
    Project second = new Project();
    second.setName("sec");
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<Project> firstChildren = arrayList();
    firstChildren.add(first);
    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<Project> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    Project root = new Project();
    root.setName("root");
    root.setId(Integer.valueOf(1000));
    List<Project> rootChildren = arrayList();
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
   * {@link de.iteratec.iteraplan.model.Project#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsSec() {
    // Initialize
    Project top = new Project();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    Project root = new Project();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    Project firstParent = new Project();
    firstParent.setId(Integer.valueOf(55));

    // children
    Project first = new Project();
    first.setName(TEST_PROJ_NAME);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    Project secParent = new Project();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription(TEST_DESCRIPTION);

    // children
    Project second = new Project();
    second.setName("sec");
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<Project> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<Project> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    List<Project> rootChildren = arrayList();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<Project> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(-1, root.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The second will be with the root compared. The
   * result should be +1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsThird() {
    // Initialize
    Project top = new Project();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    Project root = new Project();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    Project firstParent = new Project();
    firstParent.setId(Integer.valueOf(55));

    // children
    Project first = new Project();
    first.setName(TEST_PROJ_NAME);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    Project secParent = new Project();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription(TEST_DESCRIPTION);

    // children
    Project second = new Project();
    second.setName("sec");
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<Project> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<Project> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    List<Project> rootChildren = arrayList();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<Project> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(1, second.compareTo(root));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Project#getChildrenAsSet()} The method tests
   * if the GetChildrenAsSet() correctly takes the list with the children as a set.
   */
  @Test
  public void testGetChildrenAsSet() {
    classUnderTest = new Project();
    assertNotNull(classUnderTest.getChildrenAsSet());

    // Add some children:
    List<Project> expected = arrayList();
    Set<Project> actual;

    // child 1
    Project firstChild = new Project();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("child1");
    expected.add(firstChild);

    // child 2 = child 3
    Project secChild = new Project();
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
   * Test method for {@link de.iteratec.iteraplan.model.Project#getI18NKey()} The test method has
   * only meaning for the code coverage.
   */
  @Test
  public void testGetI18NKey() {
    classUnderTest = new Project();
    assertEquals("project.virtualElement", classUnderTest.getI18NKey());
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.Project#setRuntimePeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * and {@link de.iteratec.iteraplan.model.Project#getRuntimePeriod()}. The test method has only
   * meaning for the code coverage.
   */
  @Test
  public void testGetRuntimePeriod() throws ParseException {
    classUnderTest = new Project();

    // set a time period
    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse(STANDARD_START_DATE_2005);
    Date end = format.parse(STANDARD_END_DATE_2009);
    RuntimePeriod rtp = new RuntimePeriod(start, end);
    classUnderTest.setRuntimePeriod(rtp);

    assertEquals(rtp, classUnderTest.getRuntimePeriod());
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.Project#getRuntimePeriodNullSafe()}
   * {@link de.iteratec.iteraplan.model.Project#setRuntimePeriodNullSafe()} The test method has only
   * meaning for the code coverage.
   */
  @Test
  public void testGetRuntimePeriodNullSafeFirstCase() {
    classUnderTest = new Project();
    RuntimePeriod rtp = new RuntimePeriod(null, null);
    classUnderTest.setRuntimePeriodNullSafe(rtp);
    assertEquals(new RuntimePeriod(null, null), classUnderTest.getRuntimePeriodNullSafe());
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.Project#getRuntimePeriodNullSafe()}
   * {@link de.iteratec.iteraplan.model.Project#setRuntimePeriodNullSafe()} The test method has only
   * meaning for the code coverage.
   */
  @Test
  public void testGetRuntimePeriodNullSafeSecCase() throws ParseException {
    classUnderTest = new Project();

    // set a time period
    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse(STANDARD_START_DATE_2005);
    Date end = format.parse(STANDARD_END_DATE_2009);
    RuntimePeriod rtp = new RuntimePeriod(start, end);
    classUnderTest.setRuntimePeriodNullSafe(rtp);

    assertEquals(rtp, classUnderTest.getRuntimePeriodNullSafe());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Project#removeParent()} The method tests if
   * the removeParent method removes the parent object correctly. The attributes of the parent
   * object save same information.
   */
  @Test
  public void testRemoveParentCaseNotNull() {
    Project parent = new Project();
    parent.setId(Integer.valueOf(65));
    parent.setName("parent");
    classUnderTest = new Project();
    classUnderTest.addParent(parent);
    classUnderTest.removeParent();

    Project expectedParent = null;
    Project actualParent = classUnderTest.getParent();

    assertEquals(expectedParent, actualParent);

    List<Project> expectedChildren = arrayList();
    List<Project> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Project#removeParent()} The method tests if
   * the method removes the parent object. The attributes of the parent object saves no information.
   */
  @Test
  public void testRemoveParentCaseNull() {
    classUnderTest = new Project();
    classUnderTest.removeParent();

    Project expectedParent = null;
    Project actualParent = classUnderTest.getParent();

    assertEquals(expectedParent, actualParent);

    List<Project> expectedChildren = arrayList();
    List<Project> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Project#removeRelations()} The method tests
   * if the removeRelations() removes all the InformationSystemReleases successfully.
   */
  @Test
  public void testRemoveRelations() {
    classUnderTest = new Project();

    Set<InformationSystemRelease> isrs = hashSet();
    InformationSystemRelease first = new InformationSystemRelease();
    first.setId(Integer.valueOf(1));
    first.setDescription(TEST_DESCRIPTION);
    isrs.add(first);

    InformationSystemRelease sec = new InformationSystemRelease();
    sec.setId(Integer.valueOf(2));
    sec.setDescription(TEST_DESCRIPTION);
    isrs.add(sec);

    InformationSystemRelease third = new InformationSystemRelease();
    third.setId(Integer.valueOf(3));
    third.setDescription(TEST_DESCRIPTION);
    isrs.add(third);

    // isrs.add(null);

    classUnderTest.setInformationSystemReleases(isrs);
    classUnderTest.removeRelations();
    assertEquals(new Project().getInformationSystemReleases(), classUnderTest.getInformationSystemReleases());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Project#runtimeEndsAt()}
   */
  @Test
  public void testRuntimeEndsAtFirstCase() {
    classUnderTest = new Project();

    // set a time period
    RuntimePeriod rtp = new RuntimePeriod(null, null);
    classUnderTest.setRuntimePeriodNullSafe(rtp);

    assertEquals(null, classUnderTest.runtimeEndsAt());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Project#runtimeEndsAt()}
   */
  @Test
  public void testRuntimeEndsAtSecCase() throws ParseException {
    classUnderTest = new Project();

    // set a time period
    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse(STANDARD_START_DATE_2005);
    Date end = format.parse(STANDARD_END_DATE_2009);
    RuntimePeriod rtp = new RuntimePeriod(start, end);
    classUnderTest.setRuntimePeriodNullSafe(rtp);

    assertEquals(rtp.getEnd(), classUnderTest.runtimeEndsAt());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#runtimeOverlapsPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * .
   */
  @Test
  public void testRuntimeOverlapsPeriodFirstCase() {
    classUnderTest = new Project();
    assertTrue(classUnderTest.runtimeOverlapsPeriod(null));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#runtimeOverlapsPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   *The test method tests the case if there is gap between two different intervals.
   */
  @Test
  public void testRuntimeOverlapsPeriodSecCase() throws ParseException {
    classUnderTest = new Project();

    // set a time period
    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse(STANDARD_START_DATE_2005);
    Date end = format.parse(STANDARD_END_DATE_2009);

    Date startOther = format.parse("15.03.2009");
    Date endOther = format.parse("01.02.2010");

    RuntimePeriod rtp = new RuntimePeriod(start, end);
    RuntimePeriod rtpOther = new RuntimePeriod(startOther, endOther);
    classUnderTest.setRuntimePeriod(rtp);

    assertFalse(classUnderTest.runtimeOverlapsPeriod(rtpOther));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#runtimeOverlapsPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   *The test method tests the case if there is no gap between two different intervals.
   */
  @Test
  public void testRuntimeOverlapsPeriodThirdCase() throws ParseException {
    classUnderTest = new Project();

    // set a time period
    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse(STANDARD_START_DATE_2005);
    Date end = format.parse(STANDARD_END_DATE_2009);

    Date startOther = format.parse(STANDARD_END_DATE_2009);
    Date endOther = format.parse("1.02.2010");

    RuntimePeriod rtp = new RuntimePeriod(start, end);
    RuntimePeriod rtpOther = new RuntimePeriod(startOther, endOther);
    classUnderTest.setRuntimePeriod(rtp);

    assertTrue(classUnderTest.runtimeOverlapsPeriod(rtpOther));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#runtimeOverlapsPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   */
  @Test
  public void testRuntimeOverlapsPeriodFourthCase() {
    classUnderTest = new Project();
    RuntimePeriod rtp = new RuntimePeriod(null, null);
    classUnderTest.setRuntimePeriod(rtp);
    assertTrue(classUnderTest.runtimeOverlapsPeriod(null));
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.Project#runtimeStartsAt()} The test method has meaning only
   * for the code coverage.
   */
  @Test
  public void testRuntimeStartsAtCaseNotNull() throws ParseException {
    classUnderTest = new Project();

    // set a time period
    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse(STANDARD_START_DATE_2005);
    Date end = format.parse(STANDARD_END_DATE_2009);

    RuntimePeriod rtp = new RuntimePeriod(start, end);
    classUnderTest.setRuntimePeriod(rtp);

    assertEquals(start, classUnderTest.runtimeStartsAt());
  }

  /**
   * Test method for (getter and setter)
   * {@link de.iteratec.iteraplan.model.Project#runtimeStartsAt()} The test method has meaning only
   * for the code coverage.
   */
  @Test
  public void testRuntimeStartsAtCaseNull() {
    classUnderTest = new Project();
    assertEquals(null, classUnderTest.runtimeStartsAt());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The test method tests the case when runTimePeriod is null. Than the runtimeWithinPeriod must
   * return true.
   */
  @Test
  public void testRuntimeWithinPeriodRTPNull() {
    classUnderTest = new Project();
    assertNull(classUnderTest.getRuntimePeriod());
    assertTrue(classUnderTest.runtimeWithinPeriod(null));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The test method tests a possible variant for the withinPeriod(RuntimePeriod period) in the
   * RuntimePeriod.java.
   */
  @Test
  public void testRuntimeWithinPeriodCaseNull() throws ParseException {
    classUnderTest = new Project();

    // set a time period
    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse(STANDARD_START_DATE_2005);
    Date end = format.parse(STANDARD_END_DATE_2009);

    RuntimePeriod rtp = new RuntimePeriod(start, end);

    classUnderTest.setRuntimePeriod(rtp);

    assertTrue(classUnderTest.runtimeWithinPeriod(null));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The test method tests a possible variant for the withinPeriod(RuntimePeriod period) in the
   * RuntimePeriod.java.
   */
  @Test
  public void testRuntimeWithinPeriodCaseNotNull() throws ParseException {
    classUnderTest = new Project();

    // set a time period
    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse(STANDARD_START_DATE_2005);
    Date end = format.parse(STANDARD_END_DATE_2009);

    RuntimePeriod rtp = new RuntimePeriod(start, end);

    classUnderTest.setRuntimePeriod(rtp);

    assertTrue(classUnderTest.runtimeWithinPeriod(rtp));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The test method tests a possible variant for the withinPeriod(RuntimePeriod period) in the
   * RuntimePeriod.java.
   */
  @Test
  public void testRuntimeWithinPeriodCaseOverlapNull() throws ParseException {
    classUnderTest = new Project();

    // set a time period
    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse(STANDARD_START_DATE_2005);
    Date end = format.parse(STANDARD_END_DATE_2009);

    Date startOther = format.parse("16.03.2009");
    Date endOther = format.parse("6.03.2010");

    RuntimePeriod rtp = new RuntimePeriod(start, end);
    RuntimePeriod rtpOther = new RuntimePeriod(startOther, endOther);

    classUnderTest.setRuntimePeriod(rtp);

    assertFalse(classUnderTest.runtimeWithinPeriod(rtpOther));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.Project#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The test method tests a possible variant for the withinPeriod(RuntimePeriod period) in the
   * RuntimePeriod.java.
   */
  @Test
  public void testRuntimeWithinPeriodCaseOverlapNotNull() throws ParseException {
    classUnderTest = new Project();

    // set a time period
    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse(STANDARD_START_DATE_2005);
    Date end = format.parse(STANDARD_END_DATE_2009);

    Date startOther = format.parse("16.03.2008");
    Date endOther = format.parse("6.03.2010");

    RuntimePeriod rtp = new RuntimePeriod(start, end);
    RuntimePeriod rtpOther = new RuntimePeriod(startOther, endOther);

    classUnderTest.setRuntimePeriod(rtp);

    assertFalse(classUnderTest.runtimeWithinPeriod(rtpOther));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.Project#removeAllChildren()} The method
   * tests if the removeAllChildren method correctly removes all the children.
   */
  @Test
  public void testRemoveAllChildren() {
    List<Project> children = arrayList();
    children.add(new Project());
    children.add(new Project());
    children.add(new Project());
    children.add(new Project());
    children.add(new Project());

    classUnderTest = new Project();
    classUnderTest.setChildren(children);
    classUnderTest.removeAllChildren();

    assertEquals(new Project().getChildren(), classUnderTest.getChildren());
  }

}

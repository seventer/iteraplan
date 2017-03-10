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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TestDataCreationHelper;
import de.iteratec.iteraplan.model.Transport;


/**
 * JUnit tests for the {@link InformationSystemRelease} class.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class InformationSystemReleaseTest {

  private static final String UPDATE_BOTH_SIDES_ERROR_MSG = "Fail to update both sides of the association";

  private static final String VERSION_1_0                 = "1.0";
  private static final String VERSION_2_0                 = "2.0";
  private static final String TEST_ID                     = "test Id";

  /**
   * Test method for (add and getter)
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessObject(de.iteratec.iteraplan.model.BusinessObject)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBusinessObjects(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessObjects()} The attribute
   * businessObjects should be at the beginning empty but not null. At the beginning the set should
   * be empty but not null. Then two other sets are created. The fist one is empty and the second
   * one is null. At the end the two sets must be equal.
   */
  @Test
  public void testBusinessObjects() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getBusinessObjects());

    // set needed to test the second side of the association
    Set<InformationSystemRelease> actualSetOtherSide = hashSet();
    actualSetOtherSide.add(classUnderTest);

    Set<Isr2BoAssociation> expected = hashSet();
    Set<Isr2BoAssociation> actual;

    BusinessObject secBO = new BusinessObject();

    Isr2BoAssociation association1 = new Isr2BoAssociation(classUnderTest, new BusinessObject());
    Isr2BoAssociation association2 = new Isr2BoAssociation(classUnderTest, secBO);

    association1.connect();
    association2.connect();

    expected.add(association1);
    expected.add(association2);

    actual = classUnderTest.getBusinessObjectAssociations();
    assertEquals(expected, actual);

    // test for the other side of the association
    for (BusinessObject bo : classUnderTest.getBusinessObjects()) {
      assertEquals(actualSetOtherSide.size(), bo.getInformationSystemReleases().size());
      assertEquals(bo.getInformationSystemReleases(), actualSetOtherSide);
    }
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#setBusinessObjects(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessObjects(java.util.Set)}
   * The method tests if the setBusinessObjects() sets correctly the businessObjects. The attribute
   * businessObjects should be at the beginning empty but not null. At the beginning the set should
   * be empty but not null. Then two other sets are created. The fist one is empty and the second
   * one is null. At the end the two sets should be equal.
   */
  @Test
  public void testSetBusinessObjects() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getBusinessObjects());

    Set<BusinessObject> expected = hashSet();
    Set<BusinessObject> actual;

    expected.add(new BusinessObject());

    classUnderTest.setBusinessObjects(expected);
    actual = classUnderTest.getBusinessObjects();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addChild(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method tests if the addChild throws IllegalArgumentException if the user tries to add null as a
   * child.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testChildCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    classUnderTest.addChild(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addChild(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getChildren()}.
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#setChildren(java.util.Set)}.The
   * method tests if the addChildren and addChild add correctly a children/child in the set of the
   * children.
   */
  @Test
  public void testChildren() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getChildren());

    Set<InformationSystemRelease> children = hashSet();
    InformationSystemRelease firstChild = new InformationSystemRelease();
    children.add(firstChild);

    InformationSystemRelease secChild = new InformationSystemRelease();
    secChild.setId(Integer.valueOf(1));

    classUnderTest.setChildren(children);
    classUnderTest.addChild(secChild);
    children.add(secChild);
    Set<InformationSystemRelease> actual = classUnderTest.getChildren();

    assertEquals(children, actual);

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addInformationSystemDomain(de.iteratec.iteraplan.model.InformationSystemDomain)}
   * The method tests if the addInformationSystemDomains throws IllegalArgumentException if the user
   * tries to add a set with null in it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInformationSystemDomainsFirstCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    Set<InformationSystemDomain> setISD = hashSet();
    setISD.add(null);

    classUnderTest.addInformationSystemDomains(setISD);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addInformationSystemDomains(java.util.Set)}
   * The method tests if the addInformationSystemDomain throws IllegalArgumentException if the user
   * tries to add null in the set InformationSystemDomains.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInformationSystemDomainsSecCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    classUnderTest.addInformationSystemDomain(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addInformationSystemDomain(de.iteratec.iteraplan.model.InformationSystemDomain)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getInformationSystemDomains()}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addInformationSystemDomains(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#setInformationSystemDomains(java.util.Set)}
   * The method tests if the addInformationSystemDomains, addInformationSystemDomain and
   * setInformationSystemDomains add correctly a InformationSystemDomains/InformationSystemDomains
   * in the set of the InformationSystemDomains.
   */
  @Test
  public void testInformationSystemDomains() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getInformationSystemDomains());

    // set for the second side of the association
    Set<InformationSystemRelease> expectedRs = hashSet();
    expectedRs.add(classUnderTest);

    Set<InformationSystemDomain> expected = hashSet();
    Set<InformationSystemDomain> actual;
    InformationSystemDomain firstEl = new InformationSystemDomain();
    firstEl.setInformationSystemReleases(expectedRs);
    expected.add(firstEl);

    InformationSystemDomain secEl = new InformationSystemDomain();
    InformationSystemDomain thirdEl = new InformationSystemDomain();

    classUnderTest.setInformationSystemDomains(expected);

    classUnderTest.addInformationSystemDomain(secEl);
    expected.add(secEl);
    expected.add(thirdEl);

    classUnderTest.addInformationSystemDomains(expected);
    actual = classUnderTest.getInformationSystemDomains();

    assertEquals(expected, actual);

    // test for the other side of the association
    for (InformationSystemDomain isd : classUnderTest.getInformationSystemDomains()) {
      Set<InformationSystemRelease> actualRs = isd.getInformationSystemReleases();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedRs.size(), actualRs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedRs, actualRs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addInfrastructureElement(de.iteratec.iteraplan.model.InfrastructureElement)}
   * The method tests if the addInfrastructureElements throws IllegalArgumentException if the user tries
   * to add a set with null in it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInfrastructureElementFirstCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    Set<InfrastructureElement> setIEs = hashSet();
    setIEs.add(null);

    classUnderTest.addInfrastructureElements(setIEs);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addInfrastructureElements(de.iteratec.iteraplan.model.InfrastructureElement)}
   * The method tests if the addInfrastructureElement throws IllegalArgumentException if the user tries
   * to add null in the set InfrastructureElements.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInfrastructureElementsSecCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    classUnderTest.addInfrastructureElement(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addInfrastructureElement(de.iteratec.iteraplan.model.InformationSystemDomain)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getInfrastructureElements()}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addInfrastructureElements(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#setInfrastructureElements(java.util.Set)}
   * The method tests if the addInformationSystemDomains, addInformationSystemDomain and
   * setInformationSystemDomain add correctly a InformationSystemDomains/InformationSystemDomains in
   * the set of the InformationSystemDomains.
   */
  @Test
  public void testInfrastructureElements() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getInformationSystemDomains());

    // set for the second side of the association
    Set<InformationSystemRelease> expectedRs = hashSet();
    expectedRs.add(classUnderTest);

    Set<InfrastructureElement> expected = hashSet();
    Set<InfrastructureElement> actual;
    InfrastructureElement firstEl = new InfrastructureElement();
    expected.add(firstEl);

    InfrastructureElement secEl = new InfrastructureElement();
    InfrastructureElement thirdEl = new InfrastructureElement();

    classUnderTest.setInfrastructureElements(expected);
    classUnderTest.addInfrastructureElement(secEl);
    expected.add(secEl);
    expected.add(thirdEl);

    classUnderTest.addInfrastructureElements(expected);

    actual = classUnderTest.getInfrastructureElements();

    assertEquals(expected, actual);

    // test for the other side of the association
    for (InfrastructureElement ie : classUnderTest.getInfrastructureElements()) {
      Set<InformationSystemRelease> actualRs = ie.getInformationSystemReleases();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedRs.size(), actualRs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedRs, actualRs);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addParent(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method tests if the method sets itself as parent. An element can not be parent of itself,
   * that is why we expect IteraplanBusinessException.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentCaseAddItself() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setId(Integer.valueOf(1));
    classUnderTest.addParent(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addParent(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method tests if the method detects cycles. An element A can not have an element B as its
   * parent if B already has A as its parent.
   */
  @Test
  public void testAddParentCaseCycle() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setId(Integer.valueOf(1));

    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(Integer.valueOf(2));
    isrA.addParent(classUnderTest);

    InformationSystemRelease isrB = new InformationSystemRelease();
    isrB.setId(Integer.valueOf(3));
    isrB.addParent(isrA);

    try {
      classUnderTest.addParent(isrB);
      fail("addParent() should have thrown an exception!");
    } catch (IteraplanBusinessException e) {
      assertEquals(null, classUnderTest.getParent());
      // make sure that the names can still be displayed (no cycle exists).
      classUnderTest.getHierarchicalName();
      isrA.getHierarchicalName();
      isrB.getHierarchicalName();
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addParent(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method tests if the AddParent method sets correctly another object as parent.
   */
  @SuppressWarnings("boxing")
  @Test
  public void testAddParentCaseAddOtherISR() {
    InformationSystemRelease parent = new InformationSystemRelease();
    parent.setId(Integer.valueOf(1));
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.addParent(parent);

    assertEquals(classUnderTest.getParent(), parent);

    // check that object is removed from old parent's child list
    InformationSystemRelease newParent = new InformationSystemRelease();
    newParent.setId(3);
    assertEquals(0, newParent.getChildren().size());
    assertEquals(1, parent.getChildren().size());
    classUnderTest.addParent(newParent);
    assertEquals(1, newParent.getChildren().size());
    assertEquals(0, parent.getChildren().size());

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addPredecessors(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method tests if the addPredecessors throws IllegalArgumentException if the user tries to add a
   * set with null in it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPredecessorsFirstCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    Set<InformationSystemRelease> setIEs = hashSet();
    setIEs.add(null);

    classUnderTest.addPredecessors(setIEs);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addPredecessor(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method tests if the addPredecessor throws IllegalArgumentException if the user tries to add
   * null in the set Predecessors.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPredecessorSecCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    classUnderTest.addPredecessor(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addPredecessor(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addPredecessors(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getPredecessor(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#setPredecessor(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method tests if the addPredecessors, addPredecessor and setPredecessor add correctly a
   * Predecessors/Predecessor in the set of the Predecessors.
   */
  @Test
  public void testPredecessors() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getPredecessors());

    Set<InformationSystemRelease> expected = hashSet();
    Set<InformationSystemRelease> actual;
    InformationSystemRelease firstEl = new InformationSystemRelease();
    expected.add(firstEl);

    InformationSystemRelease secEl = new InformationSystemRelease();
    InformationSystemRelease thirdEl = new InformationSystemRelease();

    classUnderTest.setPredecessors(expected);
    classUnderTest.addPredecessor(secEl);
    expected.add(secEl);
    classUnderTest.addPredecessors(expected);
    expected.add(thirdEl);
    actual = classUnderTest.getPredecessors();

    assertEquals(expected, actual);
  }

  /**
   * Tests if it is possible to generate an predecessor cycle
   */
  @Test
  public void testAddPredecessorCycle() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setId(Integer.valueOf(1));

    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(Integer.valueOf(2));
    classUnderTest.addPredecessor(isrA);

    InformationSystemRelease isrB = new InformationSystemRelease();
    isrB.setId(Integer.valueOf(3));
    isrA.addPredecessor(isrB);

    try {
      isrB.addPredecessor(classUnderTest);
      fail("addPredecessor() should have thrown an exception!");
    } catch (IteraplanBusinessException e) {
      assertEquals(new HashSet<InformationSystemRelease>(), isrB.getPredecessors());
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addProjects(java.util.Set)} The
   * method tests if the addProjects throws IllegalArgumentException if the user tries to add a set with
   * null in it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testProjectFirstCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    Set<Project> setPs = hashSet();
    setPs.add(null);

    classUnderTest.addProjects(setPs);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addProject(de.iteratec.iteraplan.model.Project)}
   * The method tests if the addProject throws IllegalArgumentException if the user tries to add null in
   * the set Projects.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testProjectSecCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    classUnderTest.addProject(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addProject(de.iteratec.iteraplan.model.Project)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addProjects(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#setProjects(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getProjects()} The method tests if
   * the addProjects, addProject and setProjects add correctly a Projects/Project in the set of the
   * Projects.
   */
  @Test
  public void testProjects() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getProjects());

    Set<Project> expected = hashSet();
    Set<Project> actual;
    Project firstEl = new Project();
    expected.add(firstEl);

    Project secEl = new Project();
    Project thirdEl = new Project();

    classUnderTest.setProjects(expected);
    classUnderTest.addProject(secEl);
    expected.add(secEl);
    classUnderTest.addProjects(expected);
    expected.add(thirdEl);
    actual = classUnderTest.getProjects();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addSuccessors(java.util.Set)} The
   * method tests if the addSuccessors throws IllegalArgumentException if the user tries to add a set
   * with null in it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSuccessorsFirstCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    Set<InformationSystemRelease> setIEs = hashSet();
    setIEs.add(null);

    classUnderTest.addSuccessors(setIEs);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addSuccessor(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method tests if the addSuccessor throws IllegalArgumentException if the user tries to add null
   * in the set Successors.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSuccessorSecCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    classUnderTest.addSuccessor(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addSuccessor(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addSuccessors(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getSuccessors()}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#setSuccessors(java.util.Set)} The
   * method tests if the addSuccessors, addSuccessor and setSuccessor add correctly a
   * Successors/Successor in the set of the Successors.
   */
  @Test
  public void testSuccessors() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getSuccessors());

    Set<InformationSystemRelease> expected = hashSet();
    Set<InformationSystemRelease> actual;
    InformationSystemRelease firstEl = new InformationSystemRelease();
    expected.add(firstEl);

    InformationSystemRelease secEl = new InformationSystemRelease();
    InformationSystemRelease thirdEl = new InformationSystemRelease();

    classUnderTest.setSuccessors(expected);
    classUnderTest.addSuccessor(secEl);
    expected.add(secEl);
    classUnderTest.addSuccessors(expected);
    expected.add(thirdEl);
    actual = classUnderTest.getSuccessors();

    assertEquals(expected, actual);
  }

  /**
   * Tests if it is possible to generate an predecessor cycle
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddSuccessorCycle() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setId(Integer.valueOf(1));

    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(Integer.valueOf(2));
    classUnderTest.addSuccessor(isrA);

    InformationSystemRelease isrB = new InformationSystemRelease();
    isrB.setId(Integer.valueOf(3));
    isrA.addSuccessor(isrB);

    isrB.addSuccessor(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addTechnicalComponentReleases(java.util.Set)}
   * The method tests if the addTechnicalComponentReleases throws IllegalArgumentException if the user
   * tries to add a set with null in it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testTechnicalComponentReleasesFirstCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    Set<TechnicalComponentRelease> setIEs = hashSet();
    setIEs.add(null);

    classUnderTest.addTechnicalComponentReleases(setIEs);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addTechnicalComponentRelease(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * The method tests if the addTechnicalComponentRelease throws IllegalArgumentException if the user
   * tries to add null in the set TechnicalComponentReleases.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testTechnicalComponentReleaseSecCaseAddNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    classUnderTest.addTechnicalComponentRelease(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addTechnicalComponentRelease(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addTechnicalComponentReleases(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getTechnicalComponentReleases()}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#setTechnicalComponentReleases(java.util.Set)}
   * The method tests if the addTechnicalComponentReleases, addTechnicalComponentRelease and
   * setTechnicalComponentRelease add correctly a
   * TechnicalComponentReleases/TechnicalComponentRelease in the set of the
   * TechnicalComponentReleases.
   */
  @Test
  public void testTechnicalComponentReleases() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getTechnicalComponentReleases());

    Set<TechnicalComponentRelease> expected = hashSet();
    Set<TechnicalComponentRelease> actual;
    TechnicalComponentRelease firstEl = new TechnicalComponentRelease();
    expected.add(firstEl);

    TechnicalComponentRelease secEl = new TechnicalComponentRelease();
    TechnicalComponentRelease thirdEl = new TechnicalComponentRelease();

    classUnderTest.setTechnicalComponentReleases(expected);
    classUnderTest.addTechnicalComponentRelease(secEl);
    expected.add(secEl);
    classUnderTest.addTechnicalComponentReleases(expected);
    expected.add(thirdEl);
    actual = classUnderTest.getTechnicalComponentReleases();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#createVersionWithSuffix(java.lang.String)}
   * The method tests if the createVersionWithSuffix(String identifier) build correctly the needed
   * string.
   */
  @Test
  public void testCreateVersionWithSuffixEmptyIdVersion() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    String actual = classUnderTest.createVersionWithSuffix(null);
    String expected = "-";
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#createVersionWithSuffix(java.lang.String)}
   * The method tests if the createVersionWithSuffix(String identifier) build correctly the needed
   * string.
   */
  @Test
  public void testCreateVersionWithSuffixNotEmptyIdVersion() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setVersion(VERSION_1_0);
    String actual = classUnderTest.createVersionWithSuffix(TEST_ID);
    String expected = VERSION_1_0 + " - " + TEST_ID;
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getAllConnections()} The method
   * tests if the getAllConnections() returns correct set with all the InformationSystemInterfacesA
   * and all the InformationSystemInterfaceB.
   */
  @Test
  public void testGetAllConnectionsNotEmpty() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    Set<InformationSystemInterface> expected = hashSet();

    Set<InformationSystemInterface> setA = hashSet();
    InformationSystemInterface firstElA = new InformationSystemInterface();
    setA.add(firstElA);
    InformationSystemInterface secElA = new InformationSystemInterface();
    setA.add(secElA);
    setA.add(null);

    Set<InformationSystemInterface> setB = hashSet();
    InformationSystemInterface firstElB = new InformationSystemInterface();
    setB.add(firstElB);
    InformationSystemInterface secElB = new InformationSystemInterface();
    setB.add(secElB);
    setB.add(null);

    expected.add(firstElA);
    expected.add(secElA);
    expected.add(null);
    expected.add(firstElB);
    expected.add(secElB);
    expected.add(null);

    classUnderTest.setInterfacesReleaseA(setA);
    classUnderTest.setInterfacesReleaseB(setB);

    Set<InformationSystemInterface> actual = classUnderTest.getAllConnections();
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getAllConnectionsSorted()} The
   * method tests if the getAllConnectionsSorted() returns correct sorted list with all connections.
   */
  @Test
  public void testGetAllConnectionsSorted() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    // InformationSystems and InformationSystemReleases
    // A
    InformationSystem isA = new InformationSystem();
    isA.setName("testInfoSysA");

    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(Integer.valueOf(150));
    isrA.setVersion("55");
    isA.addRelease(isrA);

    // B
    InformationSystem isB = new InformationSystem();
    isB.setName("testInfoSysB");

    InformationSystemRelease isrB = new InformationSystemRelease();
    isrB.setId(Integer.valueOf(151));
    isrB.setVersion("56");
    isB.addRelease(isrB);

    // referenceRelease
    InformationSystem ref = new InformationSystem();
    ref.setName("testInfoSysRef");

    InformationSystemRelease reference = new InformationSystemRelease();
    reference.setId(Integer.valueOf(150));
    reference.setVersion(VERSION_1_0);
    ref.addRelease(reference);

    TestDataCreationHelper.createTestTransportsForBusinessObjects(TestDataCreationHelper.createTestBusinessObjects());

    List<InformationSystemInterface> expected = arrayList();

    Set<InformationSystemInterface> setA = hashSet();

    InformationSystemInterface firstElA = new InformationSystemInterface();
    firstElA.setInformationSystemReleaseA(isrA);
    firstElA.setInformationSystemReleaseB(isrB);
    firstElA.setReferenceRelease(reference);
    setA.add(firstElA);

    InformationSystemInterface secElA = new InformationSystemInterface();
    secElA.setInformationSystemReleaseA(isrA);
    secElA.setInformationSystemReleaseB(isrB);
    secElA.setReferenceRelease(reference);
    setA.add(secElA);
    // setA.add(null);

    Set<InformationSystemInterface> setB = hashSet();
    InformationSystemInterface firstElB = new InformationSystemInterface();
    firstElB.setInformationSystemReleaseA(isrA);
    firstElB.setInformationSystemReleaseB(isrB);
    firstElB.setReferenceRelease(isrB);
    setB.add(firstElB);

    InformationSystemInterface secElB = new InformationSystemInterface();
    secElB.setInformationSystemReleaseA(isrA);
    secElB.setInformationSystemReleaseB(isrB);
    secElB.setReferenceRelease(isrB);
    setB.add(secElB);

    expected.add(firstElA);
    expected.add(firstElB);
    expected.add(secElA);
    expected.add(secElB);

    classUnderTest.setInterfacesReleaseA(setA);
    classUnderTest.setInterfacesReleaseB(setB);

    List<InformationSystemInterface> actual = classUnderTest.getAllConnectionsSorted();
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getChildrenAsList()} The method
   * tests if the getChildrenAsList() correctly returns the list with the children as a list.
   */
  @Test
  public void testGetChildrenAsList() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getChildrenAsList());

    // Add some children:
    Set<InformationSystemRelease> set = hashSet();
    List<InformationSystemRelease> actual;

    InformationSystemRelease child = new InformationSystemRelease();
    child.setId(Integer.valueOf(1));
    child.setVersion(VERSION_1_0);

    InformationSystem is = new InformationSystem();
    is.setName("test InfoSys");
    is.addRelease(child);
    child.setInformationSystem(is);

    set.add(child);
    classUnderTest.setChildren(set);

    List<InformationSystemRelease> expected = new ArrayList<InformationSystemRelease>(set);

    actual = classUnderTest.getChildrenAsList();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemRelease#getChildrenAsSet()}
   * . The method tests if the GetChildrenAsSet() correctly returns the list with the children as a
   * set.
   */
  @Test
  public void testGetChildrenAsSet() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getChildrenAsSet());

    // Add some children:
    Set<InformationSystemRelease> expected = hashSet();
    Set<InformationSystemRelease> actual;

    // child 1
    InformationSystemRelease firstChild = new InformationSystemRelease();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setVersion(VERSION_1_0);
    expected.add(firstChild);

    // child 2 = child 3
    InformationSystemRelease secChild = new InformationSystemRelease();
    expected.add(secChild);
    expected.add(secChild);

    classUnderTest.setChildren(expected);
    actual = classUnderTest.getChildrenAsSet();

    assertNotNull(actual);
    assertTrue(actual.contains(firstChild));
    assertTrue(actual.contains(secChild));
    assertEquals(expected.size(), actual.size());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getDescendants(de.iteratec.iteraplan.model.InformationSystemRelease, java.util.Set)}
   * The method tests if the getDescendants (InformationSystemRelease entity,
   * Set<InformationSystemRelease> set) correctly copies all the children of the entity in to a set.
   */
  @Test
  public void testGetDescendants() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setId(Integer.valueOf(1500));
    isr.setVersion("test fifty");
    assertNotNull(isr.getChildrenAsSet());

    // Add some children:
    Set<InformationSystemRelease> expected = hashSet();
    Set<InformationSystemRelease> actual = hashSet();

    // child 1
    InformationSystemRelease firstChild = new InformationSystemRelease();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setVersion(VERSION_1_0);
    expected.add(firstChild);

    // child 2 = child 3
    InformationSystemRelease secChild = new InformationSystemRelease();
    expected.add(secChild);
    expected.add(secChild);

    isr.setChildren(expected);
    classUnderTest.getDescendants(isr, actual);

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getDirectAndIndirectChildren()} The
   * method tests if the getDirectAndIndirectChildren() correctly copies all the children of a
   * InformationSystemRelease into a list.
   */
  @Test
  public void testGetDirectAndIndirectChildren() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    List<InformationSystemRelease> expected = arrayList();
    List<InformationSystemRelease> actual = arrayList();

    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setId(Integer.valueOf(1500));
    isr.setVersion("test fifty");
    assertNotNull(isr.getChildren());

    // Add some children:
    Set<InformationSystemRelease> classUnderTestChildren = hashSet();
    Set<InformationSystemRelease> isrChildren = hashSet();
    Set<InformationSystemRelease> firstChildChildren = hashSet();

    // child 1
    InformationSystemRelease firstChild = new InformationSystemRelease();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setVersion(VERSION_1_0);
    assertNotNull(firstChild.getChildren());

    isrChildren.add(firstChild);

    // child 2 = child 3
    InformationSystemRelease secChild = new InformationSystemRelease();
    secChild.setId(Integer.valueOf(2));
    secChild.setVersion(VERSION_2_0);
    assertNotNull(secChild.getChildren());

    firstChildChildren.add(secChild);
    isrChildren.add(firstChild);
    classUnderTestChildren.add(isr);

    firstChild.setChildren(firstChildChildren);
    isr.setChildren(isrChildren);
    classUnderTest.setChildren(classUnderTestChildren);

    expected.add(isr);
    expected.add(firstChild);
    expected.add(secChild);
    expected.add(0, classUnderTest);

    actual = classUnderTest.getDirectAndIndirectChildren();

    assertEquals(expected, actual);

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getHierarchicalNameIfDifferent()}
   * The method tests if the getHierarchicalNameIfDifferent() returns correct name as string.
   */
  @Test
  public void testGetHierarchicalNameIfDifferentNotEmpty() {
    InformationSystem is = new InformationSystem();
    is.setName("test InfoSys");

    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setId(Integer.valueOf(50));
    classUnderTest.setVersion("2.7 Beta");

    InformationSystemRelease parent = new InformationSystemRelease();
    parent.setId(Integer.valueOf(500));
    parent.setVersion("10.0");

    is.addRelease(parent);
    parent.setInformationSystem(is);

    is.addRelease(classUnderTest);
    classUnderTest.setInformationSystem(is);

    classUnderTest.setParent(parent);
    Set<InformationSystemRelease> children = hashSet();
    children.add(classUnderTest);
    parent.setChildren(children);

    String firstActual = classUnderTest.getHierarchicalNameIfDifferent();
    String expected = "test InfoSys # 10.0 : test InfoSys # 2.7 Beta";
    assertEquals(expected, firstActual);

    String secActual = classUnderTest.getIdentityString();
    assertEquals(expected, secActual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getHierarchicalNameIfDifferent()}.
   * The method tests if the getHierarchicalNameIfDifferent() returns correct name as string.
   */
  @Test
  public void testGetHierarchicalNameIfDifferentEmpty() {

    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    String actual = classUnderTest.getHierarchicalNameIfDifferent();
    String expected = "";
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getInterfacedInformationSystemReleases()}
   * The method tests if the getInterfacedInformationSystemReleases() gets all the interfaces and
   * their InformationSystemReleases and returns all the InformationSystemReleases as a sorted list
   * of neighbors.
   */
  @Test
  public void testGetInterfacedInformationSystemReleases() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    // InformationSystems and InformationSystemReleases
    // A
    InformationSystem isA = new InformationSystem();
    isA.setId(Integer.valueOf(501));
    isA.setName("testInfoSysA");

    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(Integer.valueOf(150));
    isrA.setVersion("55");
    isA.addRelease(isrA);

    // B
    InformationSystem isB = new InformationSystem();
    isB.setId(Integer.valueOf(502));
    isB.setName("testInfoSysB");

    InformationSystemRelease isrB = new InformationSystemRelease();
    isrB.setId(Integer.valueOf(151));
    isrB.setVersion("56");
    isB.addRelease(isrB);

    Set<InformationSystemInterface> setA = hashSet();
    InformationSystemInterface firstElA = new InformationSystemInterface();
    firstElA.setId(Integer.valueOf(701));
    firstElA.setInformationSystemReleaseA(classUnderTest);
    firstElA.setInformationSystemReleaseB(isrB);
    setA.add(firstElA);

    InformationSystemInterface secElA = new InformationSystemInterface();
    secElA.setId(Integer.valueOf(702));
    secElA.setInformationSystemReleaseA(isrA);
    secElA.setInformationSystemReleaseB(classUnderTest);
    setA.add(secElA);

    Set<InformationSystemInterface> setB = hashSet();
    InformationSystemInterface firstElB = new InformationSystemInterface();
    firstElB.setId(Integer.valueOf(703));
    firstElB.setInformationSystemReleaseA(isrA);
    firstElB.setInformationSystemReleaseB(isrB);
    setB.add(firstElB);

    InformationSystemInterface secElB = new InformationSystemInterface();
    secElB.setId(Integer.valueOf(704));
    secElB.setInformationSystemReleaseA(isrA);
    secElB.setInformationSystemReleaseB(classUnderTest);
    setB.add(secElB);

    classUnderTest.setInterfacesReleaseA(setA);
    classUnderTest.setInterfacesReleaseB(setB);

    List<InformationSystemRelease> expected = arrayList();
    expected.add(isrA);
    expected.add(isrA);
    expected.add(isrA);
    expected.add(isrB);

    List<InformationSystemRelease> actual = classUnderTest.getInterfacedInformationSystemReleases();
    assertEquals(expected, actual);
  }

  private List<BusinessObject> createTestBusinessObjectsWithTransportsAndInterfaces(InformationSystemRelease classUnderTest) {
    List<BusinessObject> bos = TestDataCreationHelper.createTestBusinessObjects();

    Set<Transport> ts = TestDataCreationHelper.createTestTransportsForBusinessObjects(bos);

    Set<InformationSystemInterface> isis = hashSet();

    Set<InformationSystemInterface> setA = hashSet();
    InformationSystemInterface firstElA = new InformationSystemInterface();
    firstElA.setTransports(ts);
    setA.add(firstElA);

    InformationSystemInterface secElA = new InformationSystemInterface();
    secElA.setTransports(ts);
    setA.add(secElA);

    Set<InformationSystemInterface> setB = hashSet();
    InformationSystemInterface firstElB = new InformationSystemInterface();
    firstElB.setTransports(ts);
    setB.add(firstElB);

    InformationSystemInterface secElB = new InformationSystemInterface();
    secElB.setTransports(ts);
    setB.add(secElB);

    isis.add(firstElA);
    isis.add(secElA);
    isis.add(firstElB);
    isis.add(secElB);

    classUnderTest.setInterfacesReleaseA(setA);
    classUnderTest.setInterfacesReleaseB(setB);
    return bos;
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessObjectsTransportedByIsisSorted()}
   * The method tests if the getBusinessObjectsTransportedByIsisSorted() gets all the interfaces and
   * their transports and returns all the BusinessObjects as a sorted list.
   */
  @Test
  public void testGetBusinessObjectsTransportedByIsisSorted() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    List<BusinessObject> bos = createTestBusinessObjectsWithTransportsAndInterfaces(classUnderTest);

    List<BusinessObject> expected = arrayList();
    expected.add(bos.get(2));
    expected.add(bos.get(1));
    expected.add(bos.get(3));
    expected.add(bos.get(0));

    List<BusinessObject> actual = classUnderTest.getBusinessObjectsTransportedByIsisSorted();

    for (BusinessObject bo : bos) {
      assertEquals(Boolean.valueOf(expected.contains(bo)), Boolean.valueOf(actual.contains(bo)));
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessObjectsTransportedByIsis()}
   * The method tests if the getBusinessObjectsTransportedByIsis() gets all the interfaces and their
   * transports and returns all the BusinessObjects as a set.
   */
  @Test
  public void testGetBusinessObjectsTransportedByIsis() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    List<BusinessObject> bos = createTestBusinessObjectsWithTransportsAndInterfaces(classUnderTest);

    Set<BusinessObject> expected = hashSet();
    for (BusinessObject bo : bos) {
      expected.add(bo);
    }

    Set<BusinessObject> actual = classUnderTest.getBusinessObjectsTransportedByIsis();
    for (BusinessObject bo : bos) {
      assertEquals(Boolean.valueOf(expected.contains(bo)), Boolean.valueOf(actual.contains(bo)));
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemRelease#getLevel()} The
   * method tests if the getLevel () returns correct level of the object.
   */
  @Test
  public void testGetLevelCaseWithParent() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setId(Integer.valueOf(1));

    InformationSystemRelease parent = new InformationSystemRelease();
    parent.setId(Integer.valueOf(2));
    Set<InformationSystemRelease> children = hashSet();

    classUnderTest.setParent(parent);
    children.add(classUnderTest);
    parent.setChildren(children);

    Integer expected = Integer.valueOf(2);
    int actual = classUnderTest.getLevel();

    assertEquals(expected, Integer.valueOf(actual));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemRelease#getLevel()} The
   * method tests if the getLevel () returns correct level of the object.
   */
  @Test
  public void testGetLevelCaseWithoutParent() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    Integer expected = Integer.valueOf(1);
    int actual = classUnderTest.getLevel();

    assertEquals(expected, Integer.valueOf(actual));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemRelease#getPrimeFather()}
   * The method tests if the getPrimeFather() correctly returns the primeFather.
   */
  @Test
  public void testGetPrimeFather() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setId(Integer.valueOf(1));

    // Parents
    InformationSystemRelease primeFather = new InformationSystemRelease();
    primeFather.setId(Integer.valueOf(99999));
    InformationSystemRelease top = new InformationSystemRelease();
    top.setId(Integer.valueOf(1000));
    InformationSystemRelease parent = new InformationSystemRelease();
    parent.setId(Integer.valueOf(100));

    classUnderTest.setParent(parent);
    parent.setParent(top);
    top.setParent(primeFather);

    InformationSystemRelease expected = primeFather;
    InformationSystemRelease actual = classUnderTest.getPrimeFather();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#removeBusinessMappings()} The
   * method tests if the removeBusinessObjects() successfully removes all the BusinessObjects with
   * their InformationSystemReleases.
   */
  @Test
  public void testRemoveBusinessObjects() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    Set<BusinessObject> expected = hashSet();
    Set<BusinessObject> bms = hashSet();

    // add some elements
    BusinessObject firstEl = new BusinessObject();
    firstEl.setId(Integer.valueOf(500));

    Isr2BoAssociation assoc = new Isr2BoAssociation(classUnderTest, firstEl);
    assoc.connect();
    bms.add(firstEl);

    BusinessObject secEl = new BusinessObject();
    secEl.setId(Integer.valueOf(500));
    bms.add(secEl);

    BusinessObject thirdEl = new BusinessObject();
    bms.add(thirdEl);

    BusinessObject fourthEl = new BusinessObject();
    fourthEl.setId(Integer.valueOf(50));
    bms.add(fourthEl);

    classUnderTest.removeBusinessObjectAssociations();
    Set<BusinessObject> actual = classUnderTest.getBusinessObjects();
    assertEquals(expected, actual);

    // Test for the other side of the association
    assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, new HashSet<InformationSystemRelease>(), firstEl.getInformationSystemReleases());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#removeInformationSystemDomains()}
   * The method tests if the removeInformationSystemDomains() successfully removes all the
   * InformationSystemDomains with their InformationSystemReleases.
   */
  @Test
  public void testRemoveInformationSystemDomains() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    Set<InformationSystemDomain> expected = hashSet();
    Set<InformationSystemDomain> isels = hashSet();

    // set that we need for the other side of the connection
    Set<InformationSystemRelease> isrs = hashSet();
    isrs.add(classUnderTest);

    // add some elements
    InformationSystemDomain firstEl = new InformationSystemDomain();
    firstEl.setId(Integer.valueOf(500));
    firstEl.addInformationSystemReleases(isrs);
    classUnderTest.addInformationSystemDomain(firstEl);
    isels.add(firstEl);

    InformationSystemDomain secEl = new InformationSystemDomain();
    secEl.setId(Integer.valueOf(500));
    isels.add(secEl);

    InformationSystemDomain thirdEl = new InformationSystemDomain();
    isels.add(thirdEl);

    InformationSystemDomain fourthEl = new InformationSystemDomain();
    fourthEl.setId(Integer.valueOf(50));
    isels.add(fourthEl);

    classUnderTest.removeInformationSystemDomains();
    Set<InformationSystemDomain> actual = classUnderTest.getInformationSystemDomains();
    assertEquals(expected, actual);

    // Test for the other side of the association
    assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, new HashSet<InformationSystemRelease>(), firstEl.getInformationSystemReleases());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#removeInfrastructureElements()} The
   * method tests if the removeInfrastructureElements() successfully removes all the
   * InfrastructureElements with their InformationSystemReleases.
   */
  @Test
  public void testRemoveInfrastructureElements() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    Set<InfrastructureElement> expected = hashSet();
    Set<InfrastructureElement> isels = hashSet();

    // set that we need for the other side of the connection
    Set<InformationSystemRelease> isrs = hashSet();
    isrs.add(classUnderTest);

    // add some elements
    InfrastructureElement firstEl = new InfrastructureElement();
    firstEl.setId(Integer.valueOf(500));
    firstEl.addInformationSystemReleases(isrs);
    classUnderTest.addInfrastructureElement(firstEl);
    isels.add(firstEl);

    InfrastructureElement secEl = new InfrastructureElement();
    secEl.setId(Integer.valueOf(500));
    isels.add(secEl);

    InfrastructureElement thirdEl = new InfrastructureElement();
    isels.add(thirdEl);

    InfrastructureElement fourthEl = new InfrastructureElement();
    fourthEl.setId(Integer.valueOf(50));
    isels.add(fourthEl);

    classUnderTest.removeInfrastructureElements();
    Set<InfrastructureElement> actual = classUnderTest.getInfrastructureElements();
    assertEquals(expected, actual);

    // Test for the other side of the association
    assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, new HashSet<InformationSystemRelease>(), firstEl.getInformationSystemReleases());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#removePredecessors()} The method
   * tests if the removeSuccessors() successfully removes all the Predecessors with their
   * Successors.
   */
  @Test
  public void testRemovePredecessors() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    Set<InformationSystemRelease> expected = hashSet();
    Set<InformationSystemRelease> successors = hashSet();

    // add some elements
    InformationSystemRelease firstEl = new InformationSystemRelease();
    firstEl.setId(Integer.valueOf(500));
    firstEl.addSuccessor(classUnderTest);
    classUnderTest.addPredecessor(firstEl);
    successors.add(firstEl);

    InformationSystemRelease secEl = new InformationSystemRelease();
    secEl.setId(Integer.valueOf(500));
    successors.add(secEl);

    InformationSystemRelease thirdEl = new InformationSystemRelease();
    successors.add(thirdEl);

    InformationSystemRelease fourthEl = new InformationSystemRelease();
    fourthEl.setId(Integer.valueOf(50));
    successors.add(fourthEl);

    classUnderTest.removePredecessors();
    Set<InformationSystemRelease> actual = classUnderTest.getPredecessors();
    assertEquals(expected, actual);

    // Test for the other side of the association
    assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, new HashSet<InformationSystemRelease>(), firstEl.getSuccessors());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemRelease#removeProjects()}
   * The method tests if the removeProjects() successfully removes all the Projects with their
   * InformationSystemReleases.
   */
  @Test
  public void testRemoveProjects() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    Set<Project> expected = hashSet();
    Set<Project> ps = hashSet();

    // add some elements
    Project firstEl = new Project();
    firstEl.setId(Integer.valueOf(500));
    firstEl.addInformationSystemRelease(classUnderTest);
    classUnderTest.addProject(firstEl);
    ps.add(firstEl);

    Project secEl = new Project();
    secEl.setId(Integer.valueOf(500));
    ps.add(secEl);

    Project thirdEl = new Project();
    ps.add(thirdEl);

    Project fourthEl = new Project();
    fourthEl.setId(Integer.valueOf(50));
    ps.add(fourthEl);

    classUnderTest.removeProjects();
    Set<Project> actual = classUnderTest.getProjects();
    assertEquals(expected, actual);

    // Test for the other side of the association
    assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, new HashSet<InformationSystemRelease>(), firstEl.getInformationSystemReleases());

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemRelease#removeSuccessors()}
   * The method tests if the removeSuccessors() successfully removes all the Successors with their
   * Predecessors.
   */
  @Test
  public void testRemoveSuccessors() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    Set<InformationSystemRelease> expected = hashSet();
    Set<InformationSystemRelease> predecessors = hashSet();

    // add some elements
    InformationSystemRelease firstEl = new InformationSystemRelease();
    firstEl.setId(Integer.valueOf(1));
    classUnderTest.addSuccessor(firstEl);
    predecessors.add(firstEl);

    InformationSystemRelease secEl = new InformationSystemRelease();
    secEl.setId(Integer.valueOf(500));
    predecessors.add(secEl);

    InformationSystemRelease thirdEl = new InformationSystemRelease();
    predecessors.add(thirdEl);

    InformationSystemRelease fourthEl = new InformationSystemRelease();
    fourthEl.setId(Integer.valueOf(50));
    predecessors.add(fourthEl);

    classUnderTest.removeSuccessors();
    Set<InformationSystemRelease> actual = classUnderTest.getSuccessors();
    assertEquals(expected, actual);

    // Test for the other side of the association
    assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, new HashSet<InformationSystemRelease>(), firstEl.getPredecessors());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#removeTechnicalComponentReleases()}
   * The method tests if the removeTechnicalComponentReleases() successfully removes all the
   * TechnicalComponentReleases and their InformationSystemReleases.
   */
  @Test
  public void testRemoveTechnicalComponentReleases() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    Set<TechnicalComponentRelease> expected = hashSet();
    Set<TechnicalComponentRelease> tcrs = hashSet();

    // Set for the other side of the connection
    Set<InformationSystemRelease> firstELISRs = hashSet();
    firstELISRs.add(classUnderTest);

    // add some elements
    TechnicalComponentRelease firstEl = new TechnicalComponentRelease();
    firstEl.setId(Integer.valueOf(500));
    firstEl.setInformationSystemReleases(firstELISRs);
    classUnderTest.addTechnicalComponentRelease(firstEl);
    tcrs.add(firstEl);

    TechnicalComponentRelease secEl = new TechnicalComponentRelease();
    tcrs.add(secEl);

    TechnicalComponentRelease thirdEl = new TechnicalComponentRelease();
    tcrs.add(thirdEl);

    TechnicalComponentRelease fourthEl = new TechnicalComponentRelease();
    tcrs.add(fourthEl);

    classUnderTest.removeTechnicalComponentReleases();
    Set<TechnicalComponentRelease> actual = classUnderTest.getTechnicalComponentReleases();
    assertEquals(expected, actual);

    // Test for the other side of the association
    assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, new HashSet<InformationSystemRelease>(), firstEl.getInformationSystemReleases());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#setTypeOfStatus(de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus)}
   * The method tests the TypeOfStatus class.
   */
  @Test
  public void testSetTypeOfStatus() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.CURRENT);

    // test for getTypeOfStatus()
    InformationSystemRelease.TypeOfStatus expectedTypeOfStatus = InformationSystemRelease.TypeOfStatus.CURRENT;
    InformationSystemRelease.TypeOfStatus actualTypeOfStatus = classUnderTest.getTypeOfStatus();
    assertEquals(expectedTypeOfStatus, actualTypeOfStatus);

    // tests for getValue()
    TypeOfStatus firstObject = TypeOfStatus.CURRENT;
    TypeOfStatus secObject = TypeOfStatus.INACTIVE;
    TypeOfStatus thirdObject = TypeOfStatus.PLANNED;
    TypeOfStatus fourthObject = TypeOfStatus.TARGET;

    assertEquals("typeOfStatus_current", firstObject.getValue());
    assertEquals("typeOfStatus_inactive", secObject.getValue());
    assertEquals("typeOfStatus_planned", thirdObject.getValue());
    assertEquals("typeOfStatus_target", fourthObject.getValue());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemRelease#validate()} The
   * method tests if the validate method throws exception correctly.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateFirstCaseException() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    InformationSystem is = new InformationSystem();
    is.setName("");
    is.addRelease(classUnderTest);
    classUnderTest.setInformationSystem(is);
    classUnderTest.validate();
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemRelease#validate()} The
   * method tests if the validate method throws exception correctly.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateSecCaseException() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    InformationSystem is = new InformationSystem();
    is.addRelease(classUnderTest);
    classUnderTest.setInformationSystem(is);
    classUnderTest.validate();
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemRelease#validate()} The
   * method tests if the validate method throws exception correctly.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateThirdCaseException() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setId(Integer.valueOf(15));

    InformationSystem is = new InformationSystem();
    is.setName("testInfoSys");
    is.addRelease(classUnderTest);
    classUnderTest.setInformationSystem(is);

    classUnderTest.setParent(classUnderTest);
    classUnderTest.validate();

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#findChildPos(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * .
   */
  @Test
  public void testFindChildPos() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    InformationSystemRelease c = new InformationSystemRelease();
    int expected = 0;
    int actual = classUnderTest.findChildPos(c);

    assertEquals(expected, actual);
  }

  /**
   *Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBaseComponents()}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#setBaseComponents(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBaseComponent(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBaseComponents(java.util.Set)}.
   * The method tests if the addBaseComponents, addBaseComponent and setBaseComponents add correctly
   * a BaseComponents/BaseComponent in the set of the baseComponents.
   */
  @Test
  public void testBaseComponents() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getBaseComponents());

    // set for the second side of the association
    Set<InformationSystemRelease> expectedPCs = hashSet();
    expectedPCs.add(classUnderTest);

    // initialization set
    Set<InformationSystemRelease> expected = hashSet();

    // add some elements to test addBaseComponents
    InformationSystemRelease firstEl = new InformationSystemRelease();
    expected.add(firstEl);
    InformationSystemRelease secEl = new InformationSystemRelease();
    expected.add(secEl);
    InformationSystemRelease thirdEl = new InformationSystemRelease();
    expected.add(thirdEl);

    classUnderTest.addBaseComponents(expected);
    Set<InformationSystemRelease> actual = classUnderTest.getBaseComponents();

    // addBasedComponents : test the first side of the association
    assertEquals(expected, actual);

    // addBasedComponents : test the second side of the association
    for (InformationSystemRelease bc : actual) {
      Set<InformationSystemRelease> actualPCs = bc.getParentComponents();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPCs.size(), actualPCs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPCs, actualPCs);
    }

    // add fourth element in the set to test addBaseComponent
    InformationSystemRelease fourthEl = new InformationSystemRelease();
    classUnderTest.addBaseComponent(fourthEl);
    expected.add(fourthEl);
    actual = classUnderTest.getBaseComponents();

    // addBasedComponent : test the first side of the association
    assertEquals(expected, actual);

    // addBasedComponent : test the second side of the association
    for (InformationSystemRelease bc : actual) {
      Set<InformationSystemRelease> actualPCs = bc.getParentComponents();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPCs.size(), actualPCs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPCs, actualPCs);
    }

    // add fifth element in the set to test setBaseComponents
    InformationSystemRelease fifthEl = new InformationSystemRelease();
    expected.add(fifthEl);
    classUnderTest.setBaseComponents(expected);
    actual = classUnderTest.getBaseComponents();

    // setBaseComponents : test the first side of the association
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBaseComponent(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * Tests if it is possible to generate a base component cycle with the aforementioned method.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddBaseComponentCycle() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setId(Integer.valueOf(1));

    InformationSystemRelease base = new InformationSystemRelease();
    base.setId(Integer.valueOf(2));
    classUnderTest.addBaseComponent(base);

    base.addBaseComponent(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addBaseComponent(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * Tests if it is possible to generate a base component cycle with the aforementioned method.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddBaseComponentTransientCycle() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setId(Integer.valueOf(1));

    InformationSystemRelease base = new InformationSystemRelease();
    base.setId(Integer.valueOf(2));

    InformationSystemRelease base2 = new InformationSystemRelease();
    base2.setId(Integer.valueOf(3));

    // create baseComponentHierarchy
    base.addBaseComponent(base2);
    base2.addBaseComponent(classUnderTest);

    // this will fail
    classUnderTest.addBaseComponent(base);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addParentComponent(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * Tests if it is possible to generate a base component cycle with the aforementioned method.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentComponentCycle() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setId(Integer.valueOf(1));

    InformationSystemRelease parent = new InformationSystemRelease();
    parent.setId(Integer.valueOf(2));
    classUnderTest.addParentComponent(parent);

    parent.addParentComponent(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addParentComponent(de.iteratec.iteraplan.model.InformationSystemRelease)
   * InformationSystemRelease#addParentComponent(InformationSystemRelease)}
   * Tests if it is possible to generate a base component cycle with the aforementioned method.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentComponentTransientCycle() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setId(Integer.valueOf(1));

    InformationSystemRelease parent = new InformationSystemRelease();
    parent.setId(Integer.valueOf(2));

    InformationSystemRelease parent2 = new InformationSystemRelease();
    parent2.setId(Integer.valueOf(3));

    // create parentComponentHierarchy
    parent.addParentComponent(parent2);
    parent2.addParentComponent(classUnderTest);

    // this should fail
    classUnderTest.addParentComponent(parent);
  }

  /**
   *Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#addParentComponent(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#setParentComponents(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getParentComponents()} The method
   * tests if the addParentComponents, addParentComponent and setParentComponents add correctly a
   * InformationSystemReleases/InformationSystemRelease in the set of the parentComponents.
   */
  @Test
  public void testParentComponents() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    assertNotNull(classUnderTest.getParentComponents());

    // set for the second side of the association
    Set<InformationSystemRelease> expectedBCs = hashSet();
    expectedBCs.add(classUnderTest);

    // initialization set
    Set<InformationSystemRelease> expected = hashSet();

    // add some elements to test addParentComponents
    InformationSystemRelease firstEl = new InformationSystemRelease();
    expected.add(firstEl);
    InformationSystemRelease secEl = new InformationSystemRelease();
    expected.add(secEl);
    InformationSystemRelease thirdEl = new InformationSystemRelease();
    expected.add(thirdEl);

    // addParentComponent : test the first side of the association
    classUnderTest.addParentComponent(firstEl);
    classUnderTest.addParentComponent(secEl);
    classUnderTest.addParentComponent(thirdEl);

    Set<InformationSystemRelease> actual = classUnderTest.getParentComponents();

    // addParentComponent : test the first side of the association
    assertEquals(expected, actual);

    // addParentComponent : test the second side of the association
    for (InformationSystemRelease tcr : actual) {
      Set<InformationSystemRelease> actualBCs = tcr.getBaseComponents();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBCs.size(), actualBCs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBCs, actualBCs);
    }

    // add fourth element in the set to test setParentComponents
    InformationSystemRelease fourthEl = new InformationSystemRelease();
    expected.add(fourthEl);
    classUnderTest.setParentComponents(expected);
    actual = classUnderTest.getParentComponents();

    // setParentComponents : test the first side of the association
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   *{@link de.iteratec.iteraplan.model.InformationSystemRelease#removeBaseComponents()} The method
   * tests if the testRemoveBaseComponents() successfully removes all the InformationSystemReleases
   * at the baseCoponents and their parentComponents.
   */
  @Test
  public void testRemoveBaseComponents() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    // expected empty set after the remove operation
    Set<InformationSystemRelease> expected = hashSet();
    Set<InformationSystemRelease> bcs = hashSet();

    // Set for the ParentComponents for the BaseComponents
    Set<InformationSystemRelease> baseParentComponents = hashSet();
    baseParentComponents.add(classUnderTest);

    // create some baseComponents and add some parents for them
    InformationSystemRelease baseComponent = new InformationSystemRelease();
    baseComponent.setId(Integer.valueOf(500));
    baseComponent.setParentComponents(baseParentComponents);
    bcs.add(baseComponent);

    // add some baseComponents
    classUnderTest.addBaseComponents(bcs);

    classUnderTest.removeBaseComponents();
    Set<InformationSystemRelease> actual = classUnderTest.getBaseComponents();
    assertEquals(expected, actual);

    // Test for the side of the connection
    assertEquals(new HashSet<InformationSystemRelease>(), baseComponent.getParentComponents());
  }

  /**
   * Test method for
   *{@link de.iteratec.iteraplan.model.InformationSystemRelease#removeParentComponents()} The
   * method tests if the testRemoveParentComponents() successfully removes all the ParentComponents
   * and their BaseComponents.
   */
  @Test
  public void testRemoveParentComponents() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    // expected empty set after the remove operation
    Set<InformationSystemRelease> expected = hashSet();
    Set<InformationSystemRelease> ies = hashSet();

    // Set for the BaseComponents for the ParentElements
    Set<InformationSystemRelease> parentBaseComponents = hashSet();
    parentBaseComponents.add(classUnderTest);

    // create some ParentComponents and add some BaseComponents for them
    InformationSystemRelease parentComponent = new InformationSystemRelease();
    parentComponent.setId(Integer.valueOf(500));
    parentComponent.addBaseComponents(parentBaseComponents);
    ies.add(parentComponent);

    // add some ParentComponents
    classUnderTest.setParentComponents(ies);

    classUnderTest.removeParentComponents();
    Set<InformationSystemRelease> actual = classUnderTest.getParentComponents();
    assertEquals(expected, actual);

    // Test for the side of the connection
    assertEquals(new HashSet<InformationSystemRelease>(), parentComponent.getBaseComponents());
  }

}

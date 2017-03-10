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
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TestDataCreationHelper;
import de.iteratec.iteraplan.model.Transport;


/**
 * @author mma
 */
@SuppressWarnings("PMD.TooManyMethods")
public class InformationSystemInterfaceTest {

  private static final String UPDATE_BOTH_SIDES_ERROR_MSG = "Fail to update both sides of the association";

  private static final String TEST_IS_NAME_A              = "testInfoSysA";
  private static final String TEST_IS_NAME_B              = "testInfoSysB";

  @Before
  public void setUp() throws Exception {
    // not yet used
  }

  @After
  public void tearDown() throws Exception {
    // not yet used
  }

  private InformationSystemInterface classUnderTest = null;

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getTypeOfBuildingBlock()}
   */
  @Test
  public void testGetTypeOfBuildingBlock() {
    assertEquals(new InformationSystemInterface().getTypeOfBuildingBlock().toString(), "interface.singular");
  }

  /**
   * Test method for (getter and setter)
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getDescription()},
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#setDescription(java.lang.String)}
   */
  @Test
  public void testDescription() {
    classUnderTest = new InformationSystemInterface();

    classUnderTest.setDescription("testDescription");
    String actual = classUnderTest.getDescription();

    assertEquals("testDescription", actual);

    classUnderTest.setDescription("testDescription\n");
    actual = classUnderTest.getDescription();

    assertEquals("testDescription\n", actual);

    classUnderTest.setDescription("\r");
    actual = classUnderTest.getDescription();

    assertEquals("\r", actual);

    classUnderTest.setDescription("\t");
    actual = classUnderTest.getDescription();

    assertEquals("\t", actual);

    classUnderTest.setDescription(null);
    actual = classUnderTest.getDescription();

    assertEquals(null, actual);
  }

  /**
   * Test method for (getter and setter):
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getInformationSystemReleaseA()}
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#setInformationSystemReleaseA()}
   */
  @Test
  public void testInformationSystemReleaseA() {
    classUnderTest = new InformationSystemInterface();
    assertNull(classUnderTest.getInformationSystemReleaseA());

    // Set an Information System Release A
    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setId(Integer.valueOf(1));

    InformationSystemInterface expected = new InformationSystemInterface();
    expected.setInformationSystemReleaseA(isr);

    classUnderTest.setInformationSystemReleaseA(isr);
    InformationSystemRelease actual = classUnderTest.getInformationSystemReleaseA();

    assertEquals(expected.getInformationSystemReleaseA(), actual);
  }

  /**
   * Test method for (getter and setter):
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getInformationSystemReleaseB()}
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#setInformationSystemReleaseB()}
   */
  @Test
  public void testInformationSystemReleaseB() {
    classUnderTest = new InformationSystemInterface();
    assertNull(classUnderTest.getInformationSystemReleaseB());

    // Set an Information System Release B
    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setId(Integer.valueOf(1));

    InformationSystemInterface expected = new InformationSystemInterface();
    expected.setInformationSystemReleaseB(isr);

    classUnderTest.setInformationSystemReleaseB(isr);
    InformationSystemRelease actual = classUnderTest.getInformationSystemReleaseB();

    assertEquals(expected.getInformationSystemReleaseB(), actual);
  }

  /**
   * Test method for (getter and setter)
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getTechnicalComponentReleases(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getTechnicalComponentReleases(java.util.Set)}
   * .
   */
  @Test
  public void testTechnicalComponentReleases() {
    classUnderTest = new InformationSystemInterface();
    assertNotNull(classUnderTest.getTechnicalComponentReleases());
    assertEquals(hashSet(), classUnderTest.getTechnicalComponentReleases());

    Set<TechnicalComponentRelease> expected = hashSet();
    Set<TechnicalComponentRelease> actual;

    expected.add(new TechnicalComponentRelease());

    classUnderTest.setTechnicalComponentReleases(expected);
    actual = classUnderTest.getTechnicalComponentReleases();

    assertEquals(expected, actual);
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#setTransports(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getTransports()}
   */
  @Test
  public void testTransports() {
    classUnderTest = new InformationSystemInterface();
    assertNotNull(classUnderTest.getTransports());

    Set<Transport> expected = hashSet();
    Set<Transport> actual;

    expected.add(new Transport());

    classUnderTest.setTransports(expected);
    actual = classUnderTest.getTransports();

    assertEquals(expected, actual);
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getReferenceRelease()} and
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#setReferenceRelease(de.iteratec.iteraplan.model.InformationSystemRelease)}
   */
  @Test
  public void testReferenceRelease() {
    classUnderTest = new InformationSystemInterface();
    assertNull(classUnderTest.getReferenceRelease());

    // Set a reference release
    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setId(Integer.valueOf(1));

    InformationSystemInterface expected = new InformationSystemInterface();
    expected.setReferenceRelease(isr);

    classUnderTest.setReferenceRelease(isr);
    InformationSystemRelease actual = classUnderTest.getReferenceRelease();

    assertEquals(expected.getReferenceRelease(), actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#connect(de.iteratec.iteraplan.model.InformationSystemRelease, de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method tests if the connect(InformationSystemRelease a, InformationSystemRelease b)
   * correctly sets the releases and they are ready to connect.
   */
  @Test
  public void testConnectCaseNoneException() {
    classUnderTest = new InformationSystemInterface();
    assertNull(classUnderTest.getInformationSystemReleaseA());
    assertNull(classUnderTest.getInformationSystemReleaseB());

    // set to test the second side of the association
    Set<InformationSystemInterface> expectedISIs = hashSet();
    expectedISIs.add(classUnderTest);

    // Set an Information System Release A
    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(Integer.valueOf(1));

    // Set an Information System Release B
    InformationSystemRelease isrB = new InformationSystemRelease();
    isrB.setId(Integer.valueOf(2));

    classUnderTest.connect(isrA, isrB);

    InformationSystemRelease releaseA = classUnderTest.getInformationSystemReleaseA();
    InformationSystemRelease releaseB = classUnderTest.getInformationSystemReleaseB();
    assertNotNull(releaseA);
    assertNotNull(releaseB);

    // test the first side of the association
    assertEquals(isrA, releaseA);
    assertEquals(isrB, releaseB);

    // test the first side of the association
    assertEquals(expectedISIs, releaseA.getInterfacesReleaseA());
    assertEquals(expectedISIs, releaseB.getInterfacesReleaseB());
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#addTechnicalComponentRelease(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getTechnicalComponentReleases()}.
   */
  @Test
  public void testSetTechnicalComponentReleases() {
    classUnderTest = new InformationSystemInterface();
    assertNotNull(classUnderTest.getTechnicalComponentReleases());

    Set<TechnicalComponentRelease> expectedTCRs = hashSet();
    expectedTCRs.add(new TechnicalComponentRelease());

    classUnderTest.setTechnicalComponentReleases(expectedTCRs);

    Set<TechnicalComponentRelease> actualTCRs = classUnderTest.getTechnicalComponentReleases();

    assertEquals(expectedTCRs, actualTCRs);
  }

  /**
   * Test method for (getter and add)
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#setTechnicalComponentReleases(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getTechnicalComponentReleases(java.util.Set)}
   * The method tests if the addTechnicalComponentReleases() correctly adds set with
   * addTechnicalComponentReleases. The the both sides of the association should be set.
   */
  @Test
  public void testAddTechnicalComponentReleases() {
    classUnderTest = new InformationSystemInterface();
    assertNotNull(classUnderTest.getTechnicalComponentReleases());

    // set to test the second side of the association
    Set<InformationSystemInterface> expectedISIs = hashSet();
    expectedISIs.add(classUnderTest);

    Set<TechnicalComponentRelease> expectedTCRs = hashSet();
    expectedTCRs.add(new TechnicalComponentRelease());
    classUnderTest.addTechnicalComponentReleases(expectedTCRs);

    Set<TechnicalComponentRelease> actualTCRs = classUnderTest.getTechnicalComponentReleases();

    // test the first side of the association
    assertEquals(expectedTCRs, actualTCRs);

    // test the second side of the association
    for (TechnicalComponentRelease tcr : actualTCRs) {
      Set<InformationSystemInterface> actualISIs = tcr.getInformationSystemInterfaces();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedISIs, actualISIs);
    }
  }

  /**
   * Test method for (getter and add)
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#addTechnicalComponentRelease(de.iteratec.iteraplan.model.BuildingBlock)}
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getTechnicalComponentReleases(java.util.Set)}
   * The method tests if the addTechnicalComponentRelease() correctly adds
   * addTechnicalComponentRelease. The the both sides of the association should be set.
   */
  @Test
  public void testAddTechnicalComponentRelease() {
    classUnderTest = new InformationSystemInterface();
    assertNotNull(classUnderTest.getTechnicalComponentReleases());

    // set to test the second side of the association
    Set<InformationSystemInterface> expectedISIs = hashSet();
    expectedISIs.add(classUnderTest);

    Set<TechnicalComponentRelease> expectedTCRs = hashSet();
    TechnicalComponentRelease firstTCR = new TechnicalComponentRelease();
    expectedTCRs.add(firstTCR);
    classUnderTest.addTechnicalComponentRelease(firstTCR);

    Set<TechnicalComponentRelease> actualTCRs = classUnderTest.getTechnicalComponentReleases();

    // test the first side of the association
    assertEquals(expectedTCRs, actualTCRs);

    // test the second sideof the association
    for (TechnicalComponentRelease tcr : actualTCRs) {
      Set<InformationSystemInterface> actualISIs = tcr.getInformationSystemInterfaces();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedISIs, actualISIs);
    }
  }

  /**
   * Test method for (getter and add)
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#addReleaseA (de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method tests if the addReleaseA() correctly adds InformationSystemRelease. The the both
   * sides of the association should be set.
   */
  @Test
  public void testAddReleaseA() {
    classUnderTest = new InformationSystemInterface();
    assertNull(classUnderTest.getInformationSystemReleaseA());

    // set to test the second side of the association
    Set<InformationSystemInterface> expectedISIs = hashSet();
    expectedISIs.add(classUnderTest);

    InformationSystemRelease expectedRelease = new InformationSystemRelease();
    expectedRelease.setId(Integer.valueOf(55));

    classUnderTest.addReleaseA(expectedRelease);

    InformationSystemRelease actualRelease = classUnderTest.getInformationSystemReleaseA();

    // test the first side of the association
    assertEquals(expectedRelease, actualRelease);

    // test the second side of the association
    Set<InformationSystemInterface> actualISIs = actualRelease.getInterfacesReleaseA();
    assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedISIs, actualISIs);
  }

  /**
   * Test method for (getter and add)
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#addReleaseB (de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method tests if the addReleaseB() correctly adds InformationSystemRelease. The the both
   * sides of the association should be set.
   */
  @Test
  public void testAddReleaseB() {
    classUnderTest = new InformationSystemInterface();
    assertNull(classUnderTest.getInformationSystemReleaseA());

    // set to test the second side of the association
    Set<InformationSystemInterface> expectedISIs = hashSet();
    expectedISIs.add(classUnderTest);

    InformationSystemRelease expectedRelease = new InformationSystemRelease();
    expectedRelease.setId(Integer.valueOf(50));

    classUnderTest.addReleaseB(expectedRelease);

    InformationSystemRelease actualRelease = classUnderTest.getInformationSystemReleaseB();

    // test the first side of the association
    assertEquals(expectedRelease, actualRelease);

    // test the second side of the association
    Set<InformationSystemInterface> actualISIs = actualRelease.getInterfacesReleaseB();
    assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedISIs, actualISIs);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#addTransport(de.iteratec.iteraplan.model.Transport)}
   * The method tests if the addTransport() correctly adds transport. The the both sides of the
   * association should be set.
   */
  @Test
  public void testAddTransport() {
    classUnderTest = new InformationSystemInterface();
    assertNotNull(classUnderTest.getTransports());

    Set<Transport> expectedTs = hashSet();
    Transport transport = new Transport();
    expectedTs.add(transport);
    
    classUnderTest.addTransport(transport);
    Set<Transport> actualTs = classUnderTest.getTransports();

    // test the first side of the association
    assertEquals(expectedTs.size(), actualTs.size());
    Iterator<Transport> actuals = actualTs.iterator();
    Iterator<Transport> expected = expectedTs.iterator();
    
    while(expected.hasNext()) {
      Transport exp = expected.next();
      Transport act = actuals.next();
      assertEquals(exp, act);
    }
    
    // test the second side of the association
    for (Transport t : actualTs) {
      InformationSystemInterface actualISI = t.getInformationSystemInterface();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, classUnderTest, actualISI);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#removeTechnicalComponentReleases()}
   */
  @Test
  public void testRemoveTechnicalComponentReleases() {
    classUnderTest = new InformationSystemInterface();
    assertNotNull(classUnderTest.getTechnicalComponentReleases());

    Set<TechnicalComponentRelease> actual = hashSet();
    TechnicalComponentRelease first = new TechnicalComponentRelease();
    actual.add(first);
    TechnicalComponentRelease sec = new TechnicalComponentRelease();
    actual.add(sec);
    TechnicalComponentRelease third = new TechnicalComponentRelease();
    actual.add(third);

    classUnderTest.setTechnicalComponentReleases(actual);
    classUnderTest.removeTechnicalComponentReleases();

    Set<TechnicalComponentRelease> expectedTCRs = classUnderTest.getTechnicalComponentReleases();
    Set<TechnicalComponentRelease> actualTCRs = new InformationSystemInterface().getTechnicalComponentReleases();
    assertEquals(expectedTCRs, actualTCRs);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getTechnicalComponentReleasesSorted()}
   * The method tests if the getTechnicalComponentReleasesSorted() correctly sorted list with
   * TechnicalComponentReleases.
   */
  @Test
  public void testGetTechnicalComponentReleasesSorted() {
    classUnderTest = new InformationSystemInterface();
    assertNotNull(classUnderTest.getTechnicalComponentReleases());

    Set<TechnicalComponentRelease> expectedSet = hashSet();
    List<TechnicalComponentRelease> expectedList = arrayList();

    TechnicalComponent firstTC = new TechnicalComponent();
    firstTC.setId(Integer.valueOf(1));
    firstTC.setName("testTC");
    TechnicalComponentRelease first = new TechnicalComponentRelease();
    first.setId(Integer.valueOf(6));
    first.setVersion("566");
    firstTC.addRelease(first);
    expectedSet.add(first);
    expectedList.add(first);

    TechnicalComponent secTC = new TechnicalComponent();
    secTC.setId(Integer.valueOf(2));
    secTC.setName("Bla Bla");
    TechnicalComponentRelease sec = new TechnicalComponentRelease();
    sec.setId(Integer.valueOf(5));
    sec.setVersion("660");
    secTC.addRelease(sec);
    expectedSet.add(sec);
    expectedList.add(sec);

    TechnicalComponent thirdTC = new TechnicalComponent();
    thirdTC.setId(Integer.valueOf(3));
    thirdTC.setName("Amun");
    TechnicalComponentRelease third = new TechnicalComponentRelease();
    third.setId(Integer.valueOf(4));
    third.setVersion("5");
    thirdTC.addRelease(third);
    expectedSet.add(third);
    expectedList.add(third);

    Collections.sort(expectedList);

    classUnderTest.setTechnicalComponentReleases(expectedSet);
    List<TechnicalComponentRelease> actual = classUnderTest.getTechnicalComponentReleasesSorted();

    assertEquals(actual.toString(), expectedList.toString());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getTransportInformation()} The
   * test method tests if the getTransportInformation() throws correctly an exception if
   * referenceRelease is null.
   */
  @Test(expected = IteraplanTechnicalException.class)
  public void testGetTransportInformationCaseException() {
    classUnderTest = new InformationSystemInterface();
    classUnderTest.getTransportInformation();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getTransportInformation()} The
   * test method tests if the getTransportInformation() returns correct list if the referenceRelease
   * and the InformationSystemReleaseA are equal.
   */
  @Test
  public void testGetTransportInformationFirstCase() {
    classUnderTest = new InformationSystemInterface();

    // InformationSystems and InformationSystemReleases
    // A
    InformationSystem isA = new InformationSystem();
    isA.setId(Integer.valueOf(1001));
    isA.setName(TEST_IS_NAME_A);

    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(Integer.valueOf(150));
    isrA.setVersion("55");
    isA.addRelease(isrA);

    // B
    InformationSystem isB = new InformationSystem();
    isB.setId(Integer.valueOf(1002));
    isB.setName(TEST_IS_NAME_B);

    InformationSystemRelease isrB = new InformationSystemRelease();
    isrB.setId(Integer.valueOf(151));
    isrB.setVersion("56");
    isB.addRelease(isrB);

    classUnderTest.setInformationSystemReleaseA(isrA);
    classUnderTest.setInformationSystemReleaseB(isrB);

    classUnderTest.setTransports(TestDataCreationHelper.createTestTransportsForBusinessObjects(TestDataCreationHelper.createTestBusinessObjects()));
    classUnderTest.setReferenceRelease(isrA);

    String expected = "[Id: 45, Name: first Product, Description: test first Product, transportkey: firstToSecond, Id: 65, Name: fourth Product, Description: test fourth Product, transportkey: noDirection, Id: 50, Name: second Product, Description: test sec Product, transportkey: secondToFirst, Id: 55, Name: third Product, Description: test third Product, transportkey: bothDirections]";
    String actual = classUnderTest.getTransportInformation().toString();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getTransportInformation()} The
   * test method tests if the getTransportInformation() returns correct list if the referenceRelease
   * and the InformationSystemReleaseB are equal.
   */
  @Test
  public void testGetTransportInformationSecCase() {
    classUnderTest = new InformationSystemInterface();

    // InformationSystems and InformationSystemReleases
    // A
    InformationSystem isA = new InformationSystem();
    isA.setId(Integer.valueOf(1001));
    isA.setName(TEST_IS_NAME_A);

    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(Integer.valueOf(150));
    isrA.setVersion("55");
    isA.addRelease(isrA);

    // B
    InformationSystem isB = new InformationSystem();
    isA.setId(Integer.valueOf(1002));
    isB.setName(TEST_IS_NAME_B);

    InformationSystemRelease isrB = new InformationSystemRelease();
    isrB.setId(Integer.valueOf(151));
    isrB.setVersion("56");
    isB.addRelease(isrB);

    classUnderTest.setInformationSystemReleaseA(isrA);
    classUnderTest.setInformationSystemReleaseB(isrB);

    classUnderTest.setTransports(TestDataCreationHelper.createTestTransportsForBusinessObjects(TestDataCreationHelper.createTestBusinessObjects()));
    classUnderTest.setReferenceRelease(isrB);

    String expected = "[Id: 45, Name: first Product, Description: test first Product, transportkey: secondToFirst, Id: 65, Name: fourth Product, Description: test fourth Product, transportkey: noDirection, Id: 50, Name: second Product, Description: test sec Product, transportkey: firstToSecond, Id: 55, Name: third Product, Description: test third Product, transportkey: bothDirections]";
    String actual = classUnderTest.getTransportInformation().toString();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getTransportInformation()} The
   * test method tests if the getTransportInformation() returns new list if the referenceRelease and
   * the InformationSystemReleaseA and referenceRelease and the InformationSystemReleaseB are not
   * equal.
   */
  @Test
  public void testGetTransportInformationThirdCase() {
    classUnderTest = new InformationSystemInterface();

    // InformationSystems and InformationSystemReleases
    // A
    InformationSystem isA = new InformationSystem();
    isA.setName(TEST_IS_NAME_A);

    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(Integer.valueOf(150));
    isrA.setVersion("55");
    isA.addRelease(isrA);

    // B
    InformationSystem isB = new InformationSystem();
    isB.setName(TEST_IS_NAME_B);

    InformationSystemRelease isrB = new InformationSystemRelease();
    isrB.setId(Integer.valueOf(151));
    isrB.setVersion("56");
    isB.addRelease(isrB);

    // referenceRelease
    InformationSystemRelease reference = new InformationSystemRelease();
    reference.setId(Integer.valueOf(15));
    reference.setVersion("550");
    isB.addRelease(reference);

    // BusinessObject
    Set<Transport> ts = TestDataCreationHelper.createTestTransportsForBusinessObjects(TestDataCreationHelper.createTestBusinessObjects());

    classUnderTest.setInformationSystemReleaseA(isrA);
    classUnderTest.setInformationSystemReleaseB(isrB);

    classUnderTest.setTransports(ts);
    classUnderTest.setReferenceRelease(reference);

    List<NamedId> expected = arrayList();
    List<NamedId> actual = classUnderTest.getTransportInformation();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getInterfaceInformation()} The
   * method test if thegetInterfaceInformation() returns correct string.
   */
  @Test
  public void testGetInterfaceInformation() {
    classUnderTest = new InformationSystemInterface();
    classUnderTest.setDirection(Direction.BOTH_DIRECTIONS.getValue());

    // InformationSystems and InformationSystemReleases
    // A
    InformationSystem isA = new InformationSystem();
    isA.setName(TEST_IS_NAME_A);

    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(Integer.valueOf(150));
    isrA.setVersion("55");
    isA.addRelease(isrA);

    // B
    InformationSystem isB = new InformationSystem();
    isB.setName(TEST_IS_NAME_B);

    InformationSystemRelease isrB = new InformationSystemRelease();
    isrB.setId(Integer.valueOf(151));
    isrB.setVersion("56");
    isB.addRelease(isrB);

    classUnderTest.setInformationSystemReleaseA(isrA);
    classUnderTest.setInformationSystemReleaseB(isrB);

    String expected = "testInfoSysA # 55 <-> testInfoSysB # 56";
    String actual = classUnderTest.getInterfaceInformation();
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getOtherRelease()} The method
   * test if getOtherRelease() returns the other release correctly.
   */
  @Test
  public void testGetOtherReleaseFirstCase() {
    classUnderTest = new InformationSystemInterface();

    // InformationSystems and InformationSystemReleases
    // A
    InformationSystem isA = new InformationSystem();
    isA.setName(TEST_IS_NAME_A);

    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(Integer.valueOf(150));
    isrA.setVersion("55");
    isA.addRelease(isrA);

    // B
    InformationSystem isB = new InformationSystem();
    isB.setName(TEST_IS_NAME_B);

    InformationSystemRelease isrB = new InformationSystemRelease();
    isrB.setId(Integer.valueOf(151));
    isrB.setVersion("56");
    isB.addRelease(isrB);

    // referenceRelease
    InformationSystemRelease reference = new InformationSystemRelease();
    reference.setId(Integer.valueOf(151));
    reference.setVersion("56");

    classUnderTest.setInformationSystemReleaseA(isrA);
    classUnderTest.setInformationSystemReleaseB(isrB);
    classUnderTest.setReferenceRelease(reference);
    InformationSystemRelease actual = classUnderTest.getOtherRelease();

    assertEquals(isrA, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getOtherRelease()} The method
   * test if getOtherRelease() returns the other release correctly.
   */
  @Test
  public void testGetOtherReleaseSecCase() {
    classUnderTest = new InformationSystemInterface();

    // InformationSystems and InformationSystemReleases
    // A
    InformationSystem isA = new InformationSystem();
    isA.setName(TEST_IS_NAME_A);

    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(Integer.valueOf(150));
    isrA.setVersion("55");
    isA.addRelease(isrA);

    // B
    InformationSystem isB = new InformationSystem();
    isB.setName(TEST_IS_NAME_B);

    InformationSystemRelease isrB = new InformationSystemRelease();
    isrB.setId(Integer.valueOf(151));
    isrB.setVersion("56");
    isB.addRelease(isrB);

    // referenceRelease
    InformationSystemRelease reference = new InformationSystemRelease();
    reference.setId(Integer.valueOf(150));
    reference.setVersion("55");

    classUnderTest.setInformationSystemReleaseA(isrA);
    classUnderTest.setInformationSystemReleaseB(isrB);
    classUnderTest.setReferenceRelease(reference);
    InformationSystemRelease actual = classUnderTest.getOtherRelease();

    assertEquals(isrB, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getTransportInfos()} The test
   * method tests if the getTransportInfos() returns a correct list.
   */
  @Test
  public void testGetTransportInfos() {
    classUnderTest = new InformationSystemInterface();

    // InformationSystems and InformationSystemReleases
    // A
    InformationSystem isA = new InformationSystem();
    isA.setId(Integer.valueOf(1001));
    isA.setName(TEST_IS_NAME_A);

    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(Integer.valueOf(150));
    isrA.setVersion("55");
    isA.addRelease(isrA);

    // B
    InformationSystem isB = new InformationSystem();
    isB.setId(Integer.valueOf(1002));
    isB.setName(TEST_IS_NAME_B);

    InformationSystemRelease isrB = new InformationSystemRelease();
    isrB.setId(Integer.valueOf(151));
    isrB.setVersion("56");
    isB.addRelease(isrB);

    // referenceRelease
    InformationSystemRelease reference = new InformationSystemRelease();
    reference.setId(Integer.valueOf(15));
    reference.setVersion("550");
    isB.addRelease(reference);

    classUnderTest.setInformationSystemReleaseA(isrA);
    classUnderTest.setInformationSystemReleaseB(isrB);

    classUnderTest.setTransports(TestDataCreationHelper.createTestTransportsForBusinessObjects(TestDataCreationHelper.createTestBusinessObjects()));
    classUnderTest.setReferenceRelease(reference);

    String expected = "[-> first Product ->, - fourth Product -, <- second Product <-, <-> third Product <->]";
    String actual = classUnderTest.getTransportInfos().toString();
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemInterface#getIdentityString()} The test
   * method tests if the getIdentityString() returns correct string with the HierarchicalName of the
   * InformationSystemReleases.
   */
  @Test
  public void testGetIdentityString() {
    classUnderTest = new InformationSystemInterface();

    // InformationSystems and InformationSystemReleases
    // A
    InformationSystem isA = new InformationSystem();
    isA.setName(TEST_IS_NAME_A);

    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(Integer.valueOf(150));
    isrA.setVersion("55");
    isA.addRelease(isrA);

    // B
    InformationSystem isB = new InformationSystem();
    isB.setName(TEST_IS_NAME_B);

    InformationSystemRelease isrB = new InformationSystemRelease();
    isrB.setId(Integer.valueOf(151));
    isrB.setVersion("56");
    isB.addRelease(isrB);

    // referenceRelease
    InformationSystemRelease reference = new InformationSystemRelease();
    reference.setId(Integer.valueOf(15));
    reference.setVersion("550");
    isB.addRelease(reference);

    classUnderTest.setInformationSystemReleaseA(isrA);
    classUnderTest.setInformationSystemReleaseB(isrB);

    String expected = "testInfoSysA # 55 - testInfoSysB # 56";
    String actual = classUnderTest.getIdentityString();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemInterface#validate()} The
   * method tests if the validate method throws exception correctly if a technical component release
   * is assigned to it even though it is not available for interfaces.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateFirstCaseIteraplanBusinessException() {
    classUnderTest = new InformationSystemInterface();

    TechnicalComponent tc = new TechnicalComponent();
    tc.setAvailableForInterfaces(false);

    Set<TechnicalComponentRelease> tcrs = hashSet();
    TechnicalComponentRelease firstTCR = new TechnicalComponentRelease();
    firstTCR.setTechnicalComponent(tc);
    tcrs.add(firstTCR);

    classUnderTest.setTechnicalComponentReleases(tcrs);
    classUnderTest.validate();

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemInterface#validate()} The
   * method tests if the validate () accepts the case that inforationSystemReleaseA and
   * informationSystemReleaseB have the same id.
   */
  @Test
  public void testValidateSecCaseIteraplanBusinessException() {
    classUnderTest = new InformationSystemInterface();

    // InformationSystems and InformationSystemReleases
    InformationSystem is = new InformationSystem();
    is.setName("testInfoSys");

    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setId(Integer.valueOf(150));
    isr.setVersion("55");
    is.addRelease(isr);

    classUnderTest.setInformationSystemReleaseA(isr);
    classUnderTest.setInformationSystemReleaseB(isr);

    classUnderTest.validate();
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemInterface#validate()} The
   * method tests if the validate method validates correctly. For that test case the log4jrootLogger
   * must be set on DEBUG, stdout. The flag is in the
   * /iteraplan/WebContent/Web-Inf/classes/log4j.properties
   */
  @Test
  public void testValidateCaseNoneException() {
    classUnderTest = new InformationSystemInterface();
    InformationSystemRelease releaseA = new InformationSystemRelease();
    releaseA.setId(Integer.valueOf(23));
    classUnderTest.setInformationSystemReleaseA(releaseA);
    InformationSystemRelease releaseB = new InformationSystemRelease();
    releaseB.setId(Integer.valueOf(22));
    classUnderTest.setInformationSystemReleaseB(releaseB);
    classUnderTest.validate();
  }
}

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
package de.iteratec.iteraplan.model.TechnicalLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import de.iteratec.iteraplan.TestHelper;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;


/**
 * @author mma
 */
@SuppressWarnings("PMD.TooManyMethods")
public class TechnicalComponentReleaseTest {

  private static final String       UPDATE_BOTH_SIDES_ERROR_MSG = "Fail to update both sides of the association";

  private static final String       DATE_PATTERN                = "dd.MM.yyyy";
  private static final String       STANDARD_START_DATE_2005    = "1.1.2005";
  private static final String       STANDARD_END_DATE_2008      = "31.12.2008";
  private static final String       STANDARD_END_DATE_2020      = "31.12.2020";

  private TechnicalComponentRelease classUnderTest              = null;

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getTypeOfBuildingBlock()} The
   * method tests the getTypeOfBuildingBlock() method. The test method has only meaning for the code
   * coverage.
   */
  @Test
  public void testGetTypeOfBuildingBlock() {
    assertEquals("technicalComponentRelease.singular", new TechnicalComponentRelease().getTypeOfBuildingBlock().toString());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addArchitecturalDomains(java.util.Set)}
   * The method tests if the addArchitecturalDomains throws IllegalArgumentException if the user tries
   * to add null in the set ArchitecturalDomains.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testArchitecturalDomainsAddNull() {
    classUnderTest = new TechnicalComponentRelease();
    Set<ArchitecturalDomain> setISD = hashSet();
    setISD.add(null);

    classUnderTest.addArchitecturalDomains(setISD);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addArchitecturalDomain(de.iteratec.iteraplan.model.ArchitecturalDomain)}
   * The method tests if the addArchitecturalDomain throws IllegalArgumentException if the user tries to
   * add null as element.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testArchitecturalDomainAddNull() {
    classUnderTest = new TechnicalComponentRelease();

    classUnderTest.addArchitecturalDomain(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addArchitecturalDomain(de.iteratec.iteraplan.model.ArchitecturalDomain)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addArchitecturalDomains(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#setArchitecturalDomains(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getArchitecturalDomains()} The
   * method tests if the addArchitecturalDomains, addArchitecturalDomain and setArchitecturalDomains
   * add correctly a ArchitecturalDomains/ArchitecturalDomain in the set of the
   * architecturalDomains.
   */
  @Test
  public void testArchitecturalDomains() {
    classUnderTest = new TechnicalComponentRelease();
    assertNotNull(classUnderTest.getArchitecturalDomains());

    // set for the second side of the association
    Set<TechnicalComponentRelease> expectedTCRs = hashSet();
    expectedTCRs.add(classUnderTest);

    // initialization set
    Set<ArchitecturalDomain> expected = hashSet();

    // add some elements to test addArchitecturalDomains
    ArchitecturalDomain firstEl = new ArchitecturalDomain();
    expected.add(firstEl);
    ArchitecturalDomain secEl = new ArchitecturalDomain();
    expected.add(secEl);
    ArchitecturalDomain thirdEl = new ArchitecturalDomain();
    expected.add(thirdEl);

    classUnderTest.addArchitecturalDomains(expected);
    Set<ArchitecturalDomain> actual = classUnderTest.getArchitecturalDomains();

    // addArchitecturalDomains : test the first side of the association
    assertEquals(expected, actual);

    // addArchitecturalDomains : test the second side of the association
    for (ArchitecturalDomain ad : actual) {
      Set<TechnicalComponentRelease> actualTCRs = ad.getTechnicalComponentReleases();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedTCRs.size(), actualTCRs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedTCRs, actualTCRs);
    }

    // add fourth element in the set to test addArchitecturalDomain
    ArchitecturalDomain fourthEl = new ArchitecturalDomain();
    classUnderTest.addArchitecturalDomain(fourthEl);
    expected.add(fourthEl);
    actual = classUnderTest.getArchitecturalDomains();

    // addArchitecturalDomain : test the first side of the association
    assertEquals(expected, actual);

    // addArchitecturalDomain : test the second side of the association
    for (ArchitecturalDomain ad : actual) {
      Set<TechnicalComponentRelease> actualTCRs = ad.getTechnicalComponentReleases();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedTCRs.size(), actualTCRs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedTCRs, actualTCRs);
    }

    // add fifth element in the set to test setArchitecturalDomains
    ArchitecturalDomain fifthEl = new ArchitecturalDomain();
    expected.add(fifthEl);
    classUnderTest.setArchitecturalDomains(expected);
    actual = classUnderTest.getArchitecturalDomains();

    // setArchitecturalDomains : test the first side of the association
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addBaseComponents(java.util.Set)}
   * The method tests if the addBaseComponents throws IllegalArgumentException if the user tries to add
   * null in the set baseComponents.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testBaseComponentsAddNull() {
    classUnderTest = new TechnicalComponentRelease();
    Set<TechnicalComponentRelease> setISD = hashSet();
    setISD.add(null);

    classUnderTest.addBaseComponents(setISD);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addBaseComponent(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * The method tests if the addBaseComponent throws IllegalArgumentException if the user tries to add
   * null as element.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testBaseComponentAddNull() {
    classUnderTest = new TechnicalComponentRelease();

    classUnderTest.addBaseComponent(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getBaseComponents()}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#setBaseComponents(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addBaseComponent(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addBaseComponents(java.util.Set)}.
   * The method tests if the addBaseComponents, addBaseComponent and setBaseComponents add correctly
   * a BaseComponents/BaseComponent in the set of the baseComponents.
   */
  @Test
  public void testBaseComponents() {
    classUnderTest = new TechnicalComponentRelease();
    assertNotNull(classUnderTest.getBaseComponents());

    // set for the second side of the association
    Set<TechnicalComponentRelease> expectedPCs = hashSet();
    expectedPCs.add(classUnderTest);

    // initialization set
    Set<TechnicalComponentRelease> expected = hashSet();

    // add some elements to test addBaseComponents
    TechnicalComponentRelease firstEl = new TechnicalComponentRelease();
    expected.add(firstEl);
    TechnicalComponentRelease secEl = new TechnicalComponentRelease();
    expected.add(secEl);
    TechnicalComponentRelease thirdEl = new TechnicalComponentRelease();
    expected.add(thirdEl);

    classUnderTest.addBaseComponents(expected);
    Set<TechnicalComponentRelease> actual = classUnderTest.getBaseComponents();

    // addBasedComponents : test the first side of the association
    assertEquals(expected, actual);

    // addBasedComponents : test the second side of the association
    for (TechnicalComponentRelease bc : actual) {
      Set<TechnicalComponentRelease> actualPCs = bc.getParentComponents();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPCs.size(), actualPCs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPCs, actualPCs);
    }

    // add fourth element in the set to test addBaseComponent
    TechnicalComponentRelease fourthEl = new TechnicalComponentRelease();
    classUnderTest.addBaseComponent(fourthEl);
    expected.add(fourthEl);
    actual = classUnderTest.getBaseComponents();

    // addBasedComponent : test the first side of the association
    assertEquals(expected, actual);

    // addBasedComponent : test the second side of the association
    for (TechnicalComponentRelease bc : actual) {
      Set<TechnicalComponentRelease> actualPCs = bc.getParentComponents();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPCs.size(), actualPCs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPCs, actualPCs);
    }

    // add fifth element in the set to test setBaseComponents
    TechnicalComponentRelease fifthEl = new TechnicalComponentRelease();
    expected.add(fifthEl);
    classUnderTest.setBaseComponents(expected);
    actual = classUnderTest.getBaseComponents();

    // setBaseComponents : test the first side of the association
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addBaseComponent(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * Tests if it is possible to generate a base component cycle with the aforementioned method.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddBaseComponentCycle() {
    classUnderTest = new TechnicalComponentRelease();
    classUnderTest.setId(Integer.valueOf(1));

    TechnicalComponentRelease base = new TechnicalComponentRelease();
    base.setId(Integer.valueOf(2));
    classUnderTest.addBaseComponent(base);

    base.addBaseComponent(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addBaseComponent(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * Tests if it is possible to generate a base component cycle with the aforementioned method.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddBaseComponentTransientCycle() {
    classUnderTest = new TechnicalComponentRelease();
    classUnderTest.setId(Integer.valueOf(1));

    TechnicalComponentRelease base = new TechnicalComponentRelease();
    base.setId(Integer.valueOf(2));

    TechnicalComponentRelease base2 = new TechnicalComponentRelease();
    base2.setId(Integer.valueOf(3));

    // create baseComponentHierarchy
    base.addBaseComponent(base2);
    base2.addBaseComponent(classUnderTest);

    // this will fail
    classUnderTest.addBaseComponent(base);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addInformationSystemReleases(java.util.Set)}
   * The method tests if the addInformationSystemReleases throws IllegalArgumentException if the user
   * tries to add null in the set informationSystemReleases.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInformationSystemReleasesAddNull() {
    classUnderTest = new TechnicalComponentRelease();
    Set<InformationSystemRelease> setISD = hashSet();
    setISD.add(null);

    classUnderTest.addInformationSystemReleases(setISD);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addInformationSystemRelease(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method tests if the addInformationSystemRelease throws IllegalArgumentException if the user
   * tries to add null as element.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInformationSystemReleaseAddNull() {
    classUnderTest = new TechnicalComponentRelease();

    classUnderTest.addInformationSystemRelease(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#setInformationSystemReleases(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getInformationSystemReleases()}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addInformationSystemReleases(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addInformationSystemRelease(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method tests if the addInformationSystemReleases, addInformationSystemRelease and
   * setInformationSystemReleases add correctly a InformationSystemReleases/InformationSystemRelease
   * in the set of the informationSystemReleases.
   */
  @Test
  public void testInformationSystemReleases() {
    classUnderTest = new TechnicalComponentRelease();
    assertNotNull(classUnderTest.getInformationSystemReleases());

    // set for the second side of the association
    Set<TechnicalComponentRelease> expectedISRs = hashSet();
    expectedISRs.add(classUnderTest);

    // initialization set
    Set<InformationSystemRelease> expected = hashSet();

    // add some elements to test addInformationSystemReleases
    InformationSystemRelease firstEl = new InformationSystemRelease();
    expected.add(firstEl);
    InformationSystemRelease secEl = new InformationSystemRelease();
    expected.add(secEl);
    InformationSystemRelease thirdEl = new InformationSystemRelease();
    expected.add(thirdEl);

    // addInformationSystemReleases : test the first side of the association
    classUnderTest.addInformationSystemReleases(expected);

    Set<InformationSystemRelease> actual = classUnderTest.getInformationSystemReleases();
    // addInformationSystemReleases : test the first side of the association
    assertEquals(expected, actual);

    // addInformationSystemReleases : test the second side of the association
    for (InformationSystemRelease isr : actual) {
      Set<TechnicalComponentRelease> actualISRs = isr.getTechnicalComponentReleases();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedISRs.size(), actualISRs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedISRs, actualISRs);
    }

    // add fourth element in the set to test addInformationSystemRelease
    InformationSystemRelease fourthEl = new InformationSystemRelease();
    classUnderTest.addInformationSystemRelease(fourthEl);
    expected.add(fourthEl);
    actual = classUnderTest.getInformationSystemReleases();

    // addInformationSystemRelease : test the first side of the association
    assertEquals(expected, actual);

    // addInformationSystemRelease : test the second side of the association
    for (InformationSystemRelease isr : actual) {
      Set<TechnicalComponentRelease> actualISRs = isr.getTechnicalComponentReleases();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedISRs.size(), actualISRs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedISRs, actualISRs);
    }

    // add fifth element in the set to test setInformationSystemReleases
    InformationSystemRelease fifthEl = new InformationSystemRelease();
    expected.add(fifthEl);
    classUnderTest.setInformationSystemReleases(expected);
    actual = classUnderTest.getInformationSystemReleases();

    // setInformationSystemReleases : test the first side of the association
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#setInfrastructureElements(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getInfrastructureElements()}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addInfrastructureElement(de.iteratec.iteraplan.model.InfrastructureElement)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addInfrastructureElements(java.util.Set)}
   * The method tests if the addInfrastructureElements, addInfrastructureElement and
   * setInfrastructureElements add correctly a InfrastructureElements/InfrastructureElement in the
   * set of the infrastructureElements.
   */
  @Test
  public void testInfrastructureElements() {
    classUnderTest = new TechnicalComponentRelease();
    assertNotNull(classUnderTest.getInfrastructureElements());

    // set for the second side of the association
    Set<TechnicalComponentRelease> expectedTCRs = hashSet();
    expectedTCRs.add(classUnderTest);

    // initialization set
    Set<InfrastructureElement> expected = hashSet();
    // add some elements to test addInfrastructureElements
    InfrastructureElement firstEl = new InfrastructureElement();
    expected.add(firstEl);
    InfrastructureElement secEl = new InfrastructureElement();
    expected.add(secEl);
    InfrastructureElement thirdEl = new InfrastructureElement();
    expected.add(thirdEl);

    // addInfrastructureElements : test the first side of the association
    for (InfrastructureElement expectedIe : expected) {
      new Tcr2IeAssociation(classUnderTest, expectedIe).connect();
    }

    Set<InfrastructureElement> actual = classUnderTest.getInfrastructureElements();

    // addInfrastructureElements : test the first side of the association
    assertEquals(expected, actual);

    // addInfrastructureElements : test the second side of the association
    for (InfrastructureElement ie : actual) {
      Set<TechnicalComponentRelease> actualTCRs = ie.getTechnicalComponentReleases();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedTCRs.size(), actualTCRs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedTCRs, actualTCRs);
    }

    // add fourth element in the set to test addInformationSystemRelease
    InfrastructureElement fourthEl = new InfrastructureElement();
    Tcr2IeAssociation fourthElAssoc = new Tcr2IeAssociation(classUnderTest, fourthEl);
    fourthElAssoc.connect();
    expected.add(fourthEl);
    actual = classUnderTest.getInfrastructureElements();

    // setInfrastructureElements : test the first side of the association
    assertEquals(expected, actual);

    // addInfrastructureElement : test the second side of the association
    for (InfrastructureElement ie : actual) {
      Set<TechnicalComponentRelease> actualTCRs = ie.getTechnicalComponentReleases();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedTCRs.size(), actualTCRs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedTCRs, actualTCRs);
    }

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addParentComponent(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * The method tests if the addParentComponent throws IllegalArgumentException if the user tries to add
   * null as element.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testParentComponentAddNull() {
    classUnderTest = new TechnicalComponentRelease();

    classUnderTest.addParentComponent(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addParentComponent(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#setParentComponents(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getParentComponents()} The method
   * tests if the addParentComponents, addParentComponent and setParentComponents add correctly a
   * TechnicalComponentReleases/TechnicalComponentRelease in the set of the parentComponents.
   */
  @Test
  public void testParentComponents() {
    classUnderTest = new TechnicalComponentRelease();
    assertNotNull(classUnderTest.getParentComponents());

    // set for the second side of the association
    Set<TechnicalComponentRelease> expectedBCs = hashSet();
    expectedBCs.add(classUnderTest);

    // initialization set
    Set<TechnicalComponentRelease> expected = hashSet();

    // add some elements to test addParentComponents
    TechnicalComponentRelease firstEl = new TechnicalComponentRelease();
    expected.add(firstEl);
    TechnicalComponentRelease secEl = new TechnicalComponentRelease();
    expected.add(secEl);
    TechnicalComponentRelease thirdEl = new TechnicalComponentRelease();
    expected.add(thirdEl);

    // addParentComponent : test the first side of the association
    classUnderTest.addParentComponent(firstEl);
    classUnderTest.addParentComponent(secEl);
    classUnderTest.addParentComponent(thirdEl);

    Set<TechnicalComponentRelease> actual = classUnderTest.getParentComponents();

    // addParentComponent : test the first side of the association
    assertEquals(expected, actual);

    // addParentComponent : test the second side of the association
    for (TechnicalComponentRelease tcr : actual) {
      Set<TechnicalComponentRelease> actualBCs = tcr.getBaseComponents();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBCs.size(), actualBCs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBCs, actualBCs);
    }

    // add fourth element in the set to test setParentComponents
    TechnicalComponentRelease fourthEl = new TechnicalComponentRelease();
    expected.add(fourthEl);
    classUnderTest.setParentComponents(expected);
    actual = classUnderTest.getParentComponents();

    // setParentComponents : test the first side of the association
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addParentComponent(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * Tests if it is possible to generate a base component cycle with the aforementioned method.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentComponentCycle() {
    classUnderTest = new TechnicalComponentRelease();
    classUnderTest.setId(Integer.valueOf(1));

    TechnicalComponentRelease parent = new TechnicalComponentRelease();
    parent.setId(Integer.valueOf(2));
    classUnderTest.addParentComponent(parent);

    parent.addParentComponent(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addParentComponent(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * Tests if it is possible to generate a base component cycle with the aforementioned method.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentComponentTransientCycle() {
    classUnderTest = new TechnicalComponentRelease();
    classUnderTest.setId(Integer.valueOf(1));

    TechnicalComponentRelease parent = new TechnicalComponentRelease();
    parent.setId(Integer.valueOf(2));

    TechnicalComponentRelease parent2 = new TechnicalComponentRelease();
    parent2.setId(Integer.valueOf(3));

    // create parentComponentHierarchy
    parent.addParentComponent(parent2);
    parent2.addParentComponent(classUnderTest);

    // this should fail
    classUnderTest.addParentComponent(parent);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addPredecessors(java.util.Set)}
   * The method tests if the addPredecessors throws IllegalArgumentException if the user tries to add
   * null in the set predecessors.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPredecessorsAddNull() {
    classUnderTest = new TechnicalComponentRelease();
    Set<TechnicalComponentRelease> setISD = hashSet();
    setISD.add(null);

    classUnderTest.addPredecessors(setISD);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addPredecessor(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * The method tests if the addPredecessor throws IllegalArgumentException if the user tries to add
   * null as element.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPredecessorAddNull() {
    classUnderTest = new TechnicalComponentRelease();

    classUnderTest.addPredecessor(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addPredecessor(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addPredecessors(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#setPredecessors(java.util.Set)}
   * Test method for {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getPredecessors()}
   * The method tests if the addPredecessors, addPredecessor and setPredecessors add correctly a
   * TechnicalComponentReleases/TechnicalComponentRelease in the set of the predecessors.
   */
  @Test
  public void testPredecessors() {
    classUnderTest = new TechnicalComponentRelease();
    assertNotNull(classUnderTest.getPredecessors());

    // set for the second side of the association
    Set<TechnicalComponentRelease> expectedSs = hashSet();
    expectedSs.add(classUnderTest);

    // initialization set
    Set<TechnicalComponentRelease> expected = hashSet();

    // add some elements to test addParentComponents
    TechnicalComponentRelease firstEl = new TechnicalComponentRelease();
    expected.add(firstEl);
    TechnicalComponentRelease secEl = new TechnicalComponentRelease();
    expected.add(secEl);
    TechnicalComponentRelease thirdEl = new TechnicalComponentRelease();
    expected.add(thirdEl);

    classUnderTest.addPredecessors(expected);
    Set<TechnicalComponentRelease> actual = classUnderTest.getPredecessors();

    // addPredecessors : test the first side of the association
    assertEquals(expected, actual);

    // addPredecessors : test the second side of the association
    for (TechnicalComponentRelease p : actual) {
      Set<TechnicalComponentRelease> actualSs = p.getSuccessors();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedSs.size(), actualSs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedSs, actualSs);
    }

    // add fourth element in the set to test addPredecessor
    TechnicalComponentRelease fourthEl = new TechnicalComponentRelease();
    classUnderTest.addPredecessor(fourthEl);
    expected.add(fourthEl);
    actual = classUnderTest.getPredecessors();

    // addPredecessor : test the first side of the association
    assertEquals(expected, actual);

    // addPredecessor : test the second side of the association
    for (TechnicalComponentRelease p : actual) {
      Set<TechnicalComponentRelease> actualSs = p.getSuccessors();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedSs.size(), actualSs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedSs, actualSs);
    }

    // add fifth element in the set to test setPredecessors
    TechnicalComponentRelease fifthEl = new TechnicalComponentRelease();
    expected.add(fifthEl);
    classUnderTest.setPredecessors(expected);
    actual = classUnderTest.getPredecessors();

    // setPredecessors : test the first side of the association
    assertEquals(expected, actual);
  }

  /**
   * Tests if it is possible to generate an predecessor cycle with the addPredecessor method
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddPredecessorCycle() {
    classUnderTest = new TechnicalComponentRelease();
    classUnderTest.setId(Integer.valueOf(1));

    TechnicalComponentRelease predecessor = new TechnicalComponentRelease();
    predecessor.setId(Integer.valueOf(2));
    classUnderTest.addPredecessor(predecessor);

    predecessor.addPredecessor(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addSuccessors(java.util.Set)} The
   * method tests if the addSuccessors throws IllegalArgumentException if the user tries to add null in
   * the set successors.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSuccessorsAddNull() {
    classUnderTest = new TechnicalComponentRelease();
    Set<TechnicalComponentRelease> setISD = hashSet();
    setISD.add(null);

    classUnderTest.addSuccessors(setISD);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addSuccessor(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * The method tests if the addSuccessor throws IllegalArgumentException if the user tries to add null
   * as element.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSuccessorAddNull() {
    classUnderTest = new TechnicalComponentRelease();

    classUnderTest.addSuccessor(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addSuccessor(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#addSuccessors(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#setSuccessors(java.util.Set)} Test
   * method for {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getSuccessors()} The
   * method tests if the addSuccessors, addSuccessor and setSuccessors add correctly a
   * TechnicalComponentReleases/TechnicalComponentRelease in the set of the successors.
   */
  @Test
  public void testSuccessors() {
    classUnderTest = new TechnicalComponentRelease();
    assertNotNull(classUnderTest.getPredecessors());

    // set for the second side of the association
    Set<TechnicalComponentRelease> expectedPs = hashSet();
    expectedPs.add(classUnderTest);

    // initialization set
    Set<TechnicalComponentRelease> expected = hashSet();

    // add some elements to test addSucessors
    TechnicalComponentRelease firstEl = new TechnicalComponentRelease();
    expected.add(firstEl);
    TechnicalComponentRelease secEl = new TechnicalComponentRelease();
    expected.add(secEl);
    TechnicalComponentRelease thirdEl = new TechnicalComponentRelease();
    expected.add(thirdEl);

    classUnderTest.addSuccessors(expected);
    Set<TechnicalComponentRelease> actual = classUnderTest.getSuccessors();

    // addSucessors : test the first side of the association
    assertEquals(expected, actual);

    // addSucessors : test the second side of the association
    for (TechnicalComponentRelease s : actual) {
      Set<TechnicalComponentRelease> actualPs = s.getPredecessors();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPs.size(), actualPs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPs, actualPs);
    }

    // add fourth element in the set to test addSucessor
    TechnicalComponentRelease fourthEl = new TechnicalComponentRelease();
    classUnderTest.addSuccessor(fourthEl);
    expected.add(fourthEl);
    actual = classUnderTest.getSuccessors();

    // addSucessor : test the first side of the association
    assertEquals(expected, actual);

    // addSuccessor : test the second side of the association
    for (TechnicalComponentRelease s : actual) {
      Set<TechnicalComponentRelease> actualPs = s.getPredecessors();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPs.size(), actualPs.size());
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedPs, actualPs);
    }

    // add fifth element in the set to test setSuccessors
    TechnicalComponentRelease fifthEl = new TechnicalComponentRelease();
    expected.add(fifthEl);
    classUnderTest.setSuccessors(expected);
    actual = classUnderTest.getSuccessors();

    // setSuccessors : test the first side of the association
    assertEquals(expected, actual);
  }

  /**
   * Tests if it is possible to generate an successor cycle with the addSuccessor method
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddSuccessorCycle() {
    classUnderTest = new TechnicalComponentRelease();
    classUnderTest.setId(Integer.valueOf(1));

    TechnicalComponentRelease successor = new TechnicalComponentRelease();
    successor.setId(Integer.valueOf(2));
    classUnderTest.addSuccessor(successor);

    successor.addSuccessor(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getAllConnectionsSorted()}. The
   * method tests if the getAllConnectionsSorted() returns list with all the
   * InformationSystemInterfaces in it.
   */
  @Test
  public void testGetAllConnectionsSorted() {
    classUnderTest = new TechnicalComponentRelease();

    String versionAlpha = "alpha";
    String versionBeta = "beta";
    Integer isrId150 = Integer.valueOf(150);
    Integer isrId151 = Integer.valueOf(151);

    // InformationSystems and InformationSystemReleases
    // A
    InformationSystemRelease isrA = createInformationSystemRelease("testInfoSysA", isrId150, versionAlpha);

    // B
    InformationSystemRelease isrB = createInformationSystemRelease("testInfoSysB", isrId151, versionBeta);

    // C
    InformationSystemRelease isrC = createInformationSystemRelease("testInfoSysC", isrId150, versionAlpha);

    // D
    InformationSystemRelease isrD = createInformationSystemRelease("testInfoSysD", isrId151, versionBeta);

    // E
    InformationSystemRelease isrE = createInformationSystemRelease("testInfoSysE", isrId150, versionAlpha);

    // F
    InformationSystemRelease isrF = createInformationSystemRelease("testInfoSysF", isrId151, versionBeta);

    // BusinessObject
    BusinessObject object = new BusinessObject();
    object.setId(Integer.valueOf(25));
    object.setName("first Product");
    object.setDescription("test first Product");

    // Transport
    Set<Transport> ts = hashSet();

    // <-
    Transport first = new Transport();
    first.setId(Integer.valueOf(80));
    first.setBusinessObject(object);
    first.setDirection(Direction.SECOND_TO_FIRST);

    // ->
    Transport sec = new Transport();
    sec.setId(Integer.valueOf(1));
    sec.setBusinessObject(object);
    sec.setDirection(Direction.FIRST_TO_SECOND);

    ts.add(first);
    ts.add(sec);

    InformationSystemInterface secEl = createInformationSystemInterface(Integer.valueOf(3), isrC, isrD, ts);
    InformationSystemInterface firstEl = createInformationSystemInterface(Integer.valueOf(2), isrA, isrB, ts);
    InformationSystemInterface thirdEl = createInformationSystemInterface(Integer.valueOf(1), isrE, isrF, ts);

    Set<InformationSystemInterface> isis = hashSet();
    isis.add(secEl);
    isis.add(firstEl);
    isis.add(thirdEl);

    List<InformationSystemInterface> expected = arrayList();
    expected.add(thirdEl);
    expected.add(firstEl);
    expected.add(secEl);

    Collections.sort(expected);

    classUnderTest.setInformationSystemInterfaces(isis);

    List<InformationSystemInterface> actual = classUnderTest.getAllConnectionsSorted();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getVersion()}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getName()}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getReleaseName()}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getIdentityString()}. Easy test
   * method. The test has meaning only for the code coverage.
   */
  @Test
  public void testGetIdentityString() {
    classUnderTest = new TechnicalComponentRelease();
    classUnderTest.setVersion("beta");

    TechnicalComponent tc = new TechnicalComponent();
    tc.setName("testTechnicalComponent");

    classUnderTest.setTechnicalComponent(tc);
    tc.addRelease(classUnderTest);

    // the TechnicalComponentRelease has TechnicalComponent
    String firstExpected = "testTechnicalComponent # beta";
    String firstActual = classUnderTest.getIdentityString();
    String secActual = classUnderTest.getName();

    assertEquals(firstExpected, firstActual);
    assertEquals(firstExpected, secActual);

    classUnderTest.setTechnicalComponent(null);

    // the TechnicalComponentRelease has NO TechnicalComponent
    String secExpected = "";
    firstActual = classUnderTest.getIdentityString();
    secActual = classUnderTest.getName();

    assertEquals(secExpected, firstActual);
    assertEquals(secExpected, secActual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getRuntimePeriod()} Easy test
   * method. The test has meaning only for the code coverage.
   */
  @Test
  public void testGetRuntimePeriodNullSafeCaseNull() {
    classUnderTest = new TechnicalComponentRelease();
    RuntimePeriod expected = new RuntimePeriod();

    RuntimePeriod actual = classUnderTest.getRuntimePeriodNullSafe();
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getRuntimePeriod()}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getRuntimePeriodNullSafe()} Easy
   * test method. The test has meaning only for the code coverage.
   */
  @Test
  public void testGetRuntimePeriodNullSafeCaseNotNull() throws ParseException {
    classUnderTest = new TechnicalComponentRelease();

    // date
    RuntimePeriod rtp = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, STANDARD_START_DATE_2005, STANDARD_END_DATE_2008);

    classUnderTest.setRuntimePeriod(rtp);
    RuntimePeriod firstExpected = rtp;
    RuntimePeriod secExpected = classUnderTest.getRuntimePeriod();

    RuntimePeriod actual = classUnderTest.getRuntimePeriodNullSafe();
    assertEquals(firstExpected, secExpected);
    // test for the getter method
    assertEquals(firstExpected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#setInformationSystemInterfaces(java.util.Set)}
   * .
   */
  @Test
  @Ignore
  public void testSetInformationSystemInterfaces() {
    fail("Not yet implemented");
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getInformationSystemInterfaces()}.
   */
  @Test
  @Ignore
  public void testGetInformationSystemInterfaces() {
    fail("Not yet implemented");
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#removeArchitecturalDomains()} The
   * method tests if the testRemoveArchitecturalDomains() successfully removes all the
   * ArchitecturalDomains and their TechnicalComponentRelease.
   */
  @Test
  public void testRemoveArchitecturalDomains() {
    classUnderTest = new TechnicalComponentRelease();

    // expected empty set after the remove operation
    Set<ArchitecturalDomain> expected = hashSet();
    Set<ArchitecturalDomain> ads = hashSet();

    // Set for the TechnicalComponentReleases for the ArchitecturalDomains
    Set<TechnicalComponentRelease> firstELTCRs = hashSet();
    firstELTCRs.add(classUnderTest);

    // create some ArchitecturalDomains and add some TechnicalComponentreleases for them
    ArchitecturalDomain firstEl = new ArchitecturalDomain();
    firstEl.setId(Integer.valueOf(500));
    firstEl.setTechnicalComponentReleases(firstELTCRs);
    ads.add(firstEl);

    // add some ArchitecturalDomains
    classUnderTest.addArchitecturalDomains(ads);

    classUnderTest.removeArchitecturalDomains();
    Set<ArchitecturalDomain> actual = classUnderTest.getArchitecturalDomains();
    assertEquals(expected, actual);

    // Test for the side of the connection
    assertEquals(new HashSet<TechnicalComponentRelease>(), firstEl.getTechnicalComponentReleases());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#removeBaseComponents()} The method
   * tests if the testRemoveBaseComponents() successfully removes all the TechnicalComponentReleases
   * at the baseCoponents and their parentComponents.
   */
  @Test
  public void testRemoveBaseComponents() {
    classUnderTest = new TechnicalComponentRelease();

    // expected empty set after the remove operation
    Set<TechnicalComponentRelease> expected = hashSet();
    Set<TechnicalComponentRelease> bcs = hashSet();

    // Set for the ParentComponents for the BaseComponents
    Set<TechnicalComponentRelease> baseParentComponents = hashSet();
    baseParentComponents.add(classUnderTest);

    // create some baseComponents and add some parents for them
    TechnicalComponentRelease baseComponent = new TechnicalComponentRelease();
    baseComponent.setId(Integer.valueOf(500));
    baseComponent.setParentComponents(baseParentComponents);
    bcs.add(baseComponent);

    // add some baseComponents
    classUnderTest.addBaseComponents(bcs);

    classUnderTest.removeBaseComponents();
    Set<TechnicalComponentRelease> actual = classUnderTest.getBaseComponents();
    assertEquals(expected, actual);

    // Test for the side of the connection
    assertEquals(new HashSet<TechnicalComponentRelease>(), baseComponent.getParentComponents());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#removeInformationSystemInterfaces()}
   * The method tests if the testRemoveInformationSystemInterfaces() successfully removes all the
   * InformationSystemInterfaces and their TechnicalComponentReleases.
   */
  @Test
  public void testRemoveInformationSystemInterfaces() {
    classUnderTest = new TechnicalComponentRelease();

    // expected empty set after the remove operation
    Set<InformationSystemInterface> expected = hashSet();
    Set<InformationSystemInterface> isis = hashSet();

    // Set for the TechnicalComponentReleases for the InformationSystemInterfaces
    Set<TechnicalComponentRelease> firstELTCRs = hashSet();
    firstELTCRs.add(classUnderTest);

    // create some InformationSystemInterfaces and add some TechnicalComponentreleases for them
    InformationSystemInterface firstEl = new InformationSystemInterface();
    firstEl.setId(Integer.valueOf(500));
    firstEl.setTechnicalComponentReleases(firstELTCRs);
    isis.add(firstEl);

    // add some InformationSystemInterfaces
    classUnderTest.setInformationSystemInterfaces(isis);

    classUnderTest.removeInformationSystemInterfaces();
    Set<InformationSystemInterface> actual = classUnderTest.getInformationSystemInterfaces();
    assertEquals(expected, actual);

    // Test for the side of the connection
    assertEquals(new HashSet<TechnicalComponentRelease>(), firstEl.getTechnicalComponentReleases());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#removeInformationSystemReleases()}
   * The method tests if the testRemoveInformationSystemReleases() successfully removes all the
   * InformationSystemReleases and their TechnicalComponentReleases.
   */
  @Test
  public void testRemoveInformationSystemReleases() {
    classUnderTest = new TechnicalComponentRelease();

    // expected empty set after the remove operation
    Set<InformationSystemRelease> expected = hashSet();
    Set<InformationSystemRelease> isrs = hashSet();

    // Set for the TechnicalComponentReleases for the InformationSystemReleases
    Set<TechnicalComponentRelease> firstELTCRs = hashSet();
    firstELTCRs.add(classUnderTest);

    // create some InformationSystemReleases and add some TechnicalComponentreleases for them
    InformationSystemRelease firstEl = new InformationSystemRelease();
    firstEl.setId(Integer.valueOf(500));
    firstEl.setTechnicalComponentReleases(firstELTCRs);
    isrs.add(firstEl);

    // add some InformationSystemReleases
    classUnderTest.setInformationSystemReleases(isrs);

    classUnderTest.removeInformationSystemReleases();
    Set<InformationSystemRelease> actual = classUnderTest.getInformationSystemReleases();
    assertEquals(expected, actual);

    // Test for the side of the connection
    assertEquals(new HashSet<TechnicalComponentRelease>(), firstEl.getTechnicalComponentReleases());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#removeInfrastructureElements()}
   * The method tests if the testRemoveInfrastructureElements() successfully removes all the
   * InfrastructureElements and their TechnicalComponentReleases.
   */
  @SuppressWarnings("boxing")
  @Test
  public void testRemoveInfrastructureElements() {
    classUnderTest = new TechnicalComponentRelease();

    // create some InfrastructureElements and add some TechnicalComponentreleases for them
    InfrastructureElement firstEl = new InfrastructureElement();
    firstEl.setId(500);
    // Set for the TechnicalComponentReleases for the InfrastructureElements
    Set<Tcr2IeAssociation> firstELTCRs = hashSet();
    firstELTCRs.add(new Tcr2IeAssociation(classUnderTest, firstEl));
    firstEl.setTechnicalComponentReleaseAssociations(firstELTCRs);

    // add some InfrastructureElements
    classUnderTest.setInfrastructureElementAssociations(firstELTCRs);
    assertEquals(1, classUnderTest.getInfrastructureElements().size());

    classUnderTest.removeInfrastructureElements();
    Set<InfrastructureElement> actual = classUnderTest.getInfrastructureElements();

    // expected empty set after the remove operation
    Set<InfrastructureElement> expected = hashSet();
    assertEquals(expected, actual);

    // Test for the side of the connection
    assertEquals(new HashSet<TechnicalComponentRelease>(), firstEl.getTechnicalComponentReleases());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#removeParentComponents()} The
   * method tests if the testRemoveParentComponents() successfully removes all the ParentComponents
   * and their BaseComponents.
   */
  @Test
  public void testRemoveParentComponents() {
    classUnderTest = new TechnicalComponentRelease();

    // expected empty set after the remove operation
    Set<TechnicalComponentRelease> expected = hashSet();
    Set<TechnicalComponentRelease> ies = hashSet();

    // Set for the BaseComponents for the ParentElements
    Set<TechnicalComponentRelease> parentBaseComponents = hashSet();
    parentBaseComponents.add(classUnderTest);

    // create some ParentComponents and add some BaseComponents for them
    TechnicalComponentRelease parentComponent = new TechnicalComponentRelease();
    parentComponent.setId(Integer.valueOf(500));
    parentComponent.addBaseComponents(parentBaseComponents);
    ies.add(parentComponent);

    // add some ParentComponents
    classUnderTest.setParentComponents(ies);

    classUnderTest.removeParentComponents();
    Set<TechnicalComponentRelease> actual = classUnderTest.getParentComponents();
    assertEquals(expected, actual);

    // Test for the side of the connection
    assertEquals(new HashSet<TechnicalComponentRelease>(), parentComponent.getBaseComponents());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#removePredecessors()}. The method
   * tests if the testRemovePredecessors() successfully removes all the Predecessors and their
   * Successors.
   */
  @SuppressWarnings("boxing")
  @Test
  public void testRemovePredecessors() {
    classUnderTest = new TechnicalComponentRelease();
    classUnderTest.setId(1);

    // expected empty set after the remove operation
    Set<TechnicalComponentRelease> expected = hashSet();
    Set<TechnicalComponentRelease> predecessors = hashSet();

    // Set for the Successors for the Predecessors
    Set<TechnicalComponentRelease> predecessorSucessors = hashSet();
    predecessorSucessors.add(classUnderTest);

    // create a Predecessor and add a Successor for it
    TechnicalComponentRelease predecessor = new TechnicalComponentRelease();
    predecessor.setId(Integer.valueOf(500));
    predecessor.addSuccessors(predecessorSucessors);

    // add some Predecessors
    predecessors.add(predecessor);
    classUnderTest.addPredecessors(predecessors);

    classUnderTest.removePredecessors();
    Set<TechnicalComponentRelease> actual = classUnderTest.getPredecessors();
    assertEquals(expected, actual);

    // Test for the side of the connection
    assertEquals(new HashSet<TechnicalComponentRelease>(), predecessor.getSuccessors());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#removeSuccessors()} The method
   * tests if the testRemoveSuccessors() successfully removes all the Successors and their
   * Predecessors.
   */
  @SuppressWarnings("boxing")
  @Test
  public void testRemoveSuccessors() {
    classUnderTest = new TechnicalComponentRelease();
    classUnderTest.setId(1);

    // expected empty set after the remove operation
    Set<TechnicalComponentRelease> expected = hashSet();
    Set<TechnicalComponentRelease> successors = hashSet();

    // Set the successor of the successor
    TechnicalComponentRelease successor2 = new TechnicalComponentRelease();
    successor2.setId(2);
    Set<TechnicalComponentRelease> successorSuccessors = hashSet();
    successorSuccessors.add(successor2);

    // create a TCR and add Successors to it
    TechnicalComponentRelease successor = new TechnicalComponentRelease();
    successor.setId(Integer.valueOf(500));
    successor.addSuccessors(successorSuccessors);

    // add the Successors hierarchy to the TCR
    successors.add(successor);
    classUnderTest.addSuccessors(successors);

    classUnderTest.removeSuccessors();
    Set<TechnicalComponentRelease> actual = classUnderTest.getSuccessors();
    assertEquals(expected, actual);

    // Test for the side of the connection
    assertEquals(new HashSet<TechnicalComponentRelease>(), successor.getPredecessors());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#runtimeEndsAt()}
   * The method tests if the runtimeEndsAt() returns the correct end date of the period.
   */
  @Test
  public void testRuntimeEndsAtCaseNull() {
    classUnderTest = new TechnicalComponentRelease();

    // date
    Date expected = null;
    Date actual = classUnderTest.runtimeEndsAt();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#runtimeEndsAt()}
   * The method tests if the runtimeEndsAt() returns the correct end date of the period.
   */
  @Test
  public void testRuntimeEndsAtCaseNotNull() throws ParseException {
    classUnderTest = new TechnicalComponentRelease();

    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date end = format.parse(STANDARD_END_DATE_2008);
    RuntimePeriod rtp = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, STANDARD_START_DATE_2005, STANDARD_END_DATE_2008);

    classUnderTest.setRuntimePeriod(rtp);
    Date actual = classUnderTest.runtimeEndsAt();
    assertEquals(end, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#runtimeOverlapsPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeOverlapsPeriod() correctly returns true if no period is set.
   */
  @Test
  public void testRuntimeOverlapsPeriodCaseNull() {
    classUnderTest = new TechnicalComponentRelease();

    // dates
    RuntimePeriod period = new RuntimePeriod(null, null);

    boolean actual = classUnderTest.runtimeOverlapsPeriod(period);
    Boolean expected = Boolean.TRUE;
    assertEquals(expected, Boolean.valueOf(actual));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#runtimeOverlapsPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeOverlapsPeriod() returns boolean value if the overlap of the
   * periods is different as null.
   */
  @Test
  public void testRuntimeOverlapsPeriodFirstCaseNotNull() throws ParseException {
    classUnderTest = new TechnicalComponentRelease();

    // dates
    RuntimePeriod thisPeriod = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, STANDARD_START_DATE_2005, STANDARD_END_DATE_2008);

    RuntimePeriod otherPeriod = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, "01.01.2008", STANDARD_END_DATE_2020);

    classUnderTest.setRuntimePeriod(thisPeriod);

    boolean actual = classUnderTest.runtimeOverlapsPeriod(otherPeriod);
    Boolean expected = Boolean.TRUE;

    assertEquals(expected, Boolean.valueOf(actual));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#runtimeOverlapsPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeOverlapsPeriod() returns boolean value if the overlap of the
   * periods is different as null.
   */
  @Test
  public void testRuntimeOverlapsPeriodSecCaseNotNull() throws ParseException {
    classUnderTest = new TechnicalComponentRelease();

    // dates
    RuntimePeriod thisPeriod = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, STANDARD_START_DATE_2005, STANDARD_END_DATE_2008);

    RuntimePeriod otherPeriod = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, "01.01.2009", STANDARD_END_DATE_2020);

    classUnderTest.setRuntimePeriod(thisPeriod);

    boolean actual = classUnderTest.runtimeOverlapsPeriod(otherPeriod);
    Boolean expected = Boolean.FALSE;

    assertEquals(expected, Boolean.valueOf(actual));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#runtimeStartsAt()}
   * The method tests if the runtimeStartsAt() returns the correct start date of the period.
   */
  @Test
  public void testRuntimeStartsAtCaseNull() {
    classUnderTest = new TechnicalComponentRelease();

    // date
    Date expected = null;
    Date actual = classUnderTest.runtimeStartsAt();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#runtimeStartsAt()}
   * The method tests if the runtimeStartsAt() returns the correct start date of the period.
   */
  @Test
  public void testRuntimeStartsAtCaseNotNull() throws ParseException {
    classUnderTest = new TechnicalComponentRelease();

    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse(STANDARD_START_DATE_2005);
    RuntimePeriod period = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, STANDARD_START_DATE_2005, STANDARD_END_DATE_2008);

    classUnderTest.setRuntimePeriod(period);
    Date actual = classUnderTest.runtimeStartsAt();
    assertEquals(start, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeWithinPeriod() correctly returns true if the runTimePeriod is
   * null.
   */
  @Test
  public void testRuntimeWithinPeriodCaseRunTimePeriodNull() {
    classUnderTest = new TechnicalComponentRelease();
    assertEquals(Boolean.TRUE, Boolean.valueOf(classUnderTest.runtimeWithinPeriod(null)));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeWithinPeriod() correctly returns true if the period is null.
   */
  @Test
  public void testRuntimeWithinPeriodCasePeriodNull() throws ParseException {
    classUnderTest = new TechnicalComponentRelease();

    // date
    RuntimePeriod period = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, "06.03.2003", "30.10.2004");
    classUnderTest.setRuntimePeriod(period);

    assertEquals(Boolean.TRUE, Boolean.valueOf(classUnderTest.runtimeWithinPeriod(null)));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeWithinPeriod() correctly returns true if the start and the end
   * of the period are not set.
   */
  @Test
  public void testRuntimeWithinPeriodCaseStartNullEndNull() {
    classUnderTest = new TechnicalComponentRelease();

    // dates
    RuntimePeriod period = new RuntimePeriod(null, null);
    classUnderTest.setRuntimePeriod(period);

    assertEquals(Boolean.TRUE, Boolean.valueOf(classUnderTest.runtimeWithinPeriod(null)));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#setRuntimePeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeWithinPeriod() returns boolean value if the period is different
   * as null.
   */
  @Test
  public void testRuntimeWithinPeriodFirstCaseNotNull() throws ParseException {
    classUnderTest = new TechnicalComponentRelease();

    // dates
    RuntimePeriod thisPeriod = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, "06.03.2003", "30.10.2004");
    RuntimePeriod otherPeriod = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, "06.03.2005", "30.10.2015");

    classUnderTest.setRuntimePeriod(thisPeriod);
    assertEquals(Boolean.FALSE, Boolean.valueOf(classUnderTest.runtimeWithinPeriod(otherPeriod)));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#setRuntimePeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeWithinPeriod() returns boolean value if the period is different
   * as null.
   */
  @Test
  public void testRuntimeWithinPeriodSecCaseNotNull() throws ParseException {
    classUnderTest = new TechnicalComponentRelease();

    // dates
    RuntimePeriod thisPeriod = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, STANDARD_START_DATE_2005, STANDARD_END_DATE_2008);

    RuntimePeriod otherPeriod = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, STANDARD_START_DATE_2005, STANDARD_END_DATE_2020);

    classUnderTest.setRuntimePeriod(thisPeriod);

    boolean actual = classUnderTest.runtimeWithinPeriod(otherPeriod);
    Boolean expected = Boolean.TRUE;

    assertEquals(expected, Boolean.valueOf(actual));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeWithinPeriod() returns boolean value if the period is different
   * as null.
   */
  @Test
  public void testRuntimeWithinPeriodThirdCaseNotNull() throws ParseException {
    classUnderTest = new TechnicalComponentRelease();

    // dates
    RuntimePeriod thisPeriod = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, "01.01.2003", STANDARD_END_DATE_2008);

    RuntimePeriod otherPeriod = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, STANDARD_START_DATE_2005, STANDARD_END_DATE_2020);

    classUnderTest.setRuntimePeriod(thisPeriod);

    boolean actual = classUnderTest.runtimeWithinPeriod(otherPeriod);
    Boolean expected = Boolean.FALSE;

    assertEquals(expected, Boolean.valueOf(actual));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#setRuntimePeriodNullSafe(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the setRuntimePeriodNullSafeCase() correctly returns true if the
   * runTimePeriod has start and end date null.
   */
  @Test
  public void testSetRuntimePeriodNullSafeCaseNull() {
    classUnderTest = new TechnicalComponentRelease();

    // dates
    RuntimePeriod period = new RuntimePeriod(null, null);
    classUnderTest.setRuntimePeriod(period);

    classUnderTest.setRuntimePeriodNullSafe(period);
    RuntimePeriod actual = classUnderTest.getRuntimePeriod();
    RuntimePeriod expected = null;
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#setRuntimePeriodNullSafe(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the setRuntimePeriodNullSafeCase() correctly returns true if the
   * runTimePeriod has start and end date different as null.
   */
  @Test
  public void testSetRuntimePeriodNullSafeCaseNotNull() throws ParseException {
    classUnderTest = new TechnicalComponentRelease();

    // dates
    RuntimePeriod period = TestHelper.getInstance().getStandardRuntimePeriod(DATE_PATTERN, "01.01.2003", STANDARD_END_DATE_2008);
    classUnderTest.setRuntimePeriod(period);

    classUnderTest.setRuntimePeriodNullSafe(period);
    RuntimePeriod actual = classUnderTest.getRuntimePeriod();
    RuntimePeriod expected = period;
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#setTypeOfStatus(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus)}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getTypeOfStatus()}
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#getTypeOfStatusAsString()}The
   * method tests the TypeOfStatus class.
   */
  @Test
  public void testSetTypeOfStatus() {
    classUnderTest = new TechnicalComponentRelease();
    classUnderTest.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.CURRENT);

    // test for getTypeOfStatus()
    TechnicalComponentRelease.TypeOfStatus expectedTypeOfStatus = TechnicalComponentRelease.TypeOfStatus.CURRENT;
    TechnicalComponentRelease.TypeOfStatus actualTypeOfStatus = classUnderTest.getTypeOfStatus();
    assertEquals(expectedTypeOfStatus, actualTypeOfStatus);

    // tests for getValue()
    TechnicalComponentRelease.TypeOfStatus firstObject = TechnicalComponentRelease.TypeOfStatus.CURRENT;
    TechnicalComponentRelease.TypeOfStatus secObject = TechnicalComponentRelease.TypeOfStatus.INACTIVE;
    TechnicalComponentRelease.TypeOfStatus thirdObject = TechnicalComponentRelease.TypeOfStatus.PLANNED;
    TechnicalComponentRelease.TypeOfStatus fourthObject = TechnicalComponentRelease.TypeOfStatus.TARGET;

    assertEquals("typeOfStatus_current", firstObject.getValue());
    assertEquals("typeOfStatus_inactive", secObject.getValue());
    assertEquals("typeOfStatus_planned", thirdObject.getValue());
    assertEquals("typeOfStatus_target", fourthObject.getValue());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#validate()} The
   * method tests if the validate method throws exception correctly.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateFirstCaseException() {
    classUnderTest = new TechnicalComponentRelease();

    TechnicalComponent tc = new TechnicalComponent();
    tc.setName("");
    tc.addRelease(classUnderTest);
    classUnderTest.setTechnicalComponent(tc);
    classUnderTest.validate();
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#validate()} The
   * method tests if the validate method throws exception correctly.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateSecCaseException() {
    classUnderTest = new TechnicalComponentRelease();

    TechnicalComponent tc = new TechnicalComponent();
    tc.addRelease(classUnderTest);
    classUnderTest.setTechnicalComponent(tc);
    classUnderTest.validate();
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.TechnicalComponentRelease#validate()}The
   * method tests if the validate method validates correctly. For that test case the log4jrootLogger
   * must be set on DEBUG, stdout. The flag is in the
   * /iteraplan/WebContent/Web-Inf/classes/log4j.properties
   */
  @Test
  public void testValidateCaseNoneException() {
    classUnderTest = new TechnicalComponentRelease();
    classUnderTest.setId(Integer.valueOf(15));

    TechnicalComponent tc = new TechnicalComponent();
    tc.setName("testTechComp");
    tc.addRelease(classUnderTest);
    classUnderTest.setTechnicalComponent(tc);
    classUnderTest.validate();

  }

  private InformationSystemRelease createInformationSystemRelease(String name, Integer isrId, String version) {
    InformationSystem isA = new InformationSystem();
    isA.setName(name);

    InformationSystemRelease isrA = new InformationSystemRelease();
    isrA.setId(isrId);
    isrA.setVersion(version);
    isA.addRelease(isrA);

    return isrA;
  }

  private InformationSystemInterface createInformationSystemInterface(Integer isiId, InformationSystemRelease releaseA,
                                                                      InformationSystemRelease releaseB, Set<Transport> ts) {
    InformationSystemInterface isi = new InformationSystemInterface();
    isi.setId(isiId);
    isi.setInformationSystemReleaseA(releaseA);
    isi.setInformationSystemReleaseB(releaseB);
    isi.setTransports(ts);

    return isi;
  }
}

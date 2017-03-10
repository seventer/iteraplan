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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * @author rfe
 * @author mma
 */
@SuppressWarnings("PMD.TooManyMethods")
public class BusinessObjectTest {

  private static final String UPDATE_BOTH_SIDES_ERROR_MSG = "Fail to update both sides of the association";

  private static final String TEST_BO_NAME                = "first";
  private static final String TEST_DESCRIPTION            = "testDescription";

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    // not yet used
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    // not yet used
  }

  private BusinessObject classUnderTest = null;

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#getTypeOfBuildingBlock()}
   */
  @Test
  public void testGetTypeOfBuildingBlock() {
    assertEquals(TypeOfBuildingBlock.BUSINESSOBJECT, new BusinessObject().getTypeOfBuildingBlock());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#addBusinessDomains(java.util.Set)} The method
   * tests if the addBusinessDomains adds properly an empty list with BusinessDomains.
   */
  @Test
  public void testAddBusinessDomainsCaseEmptyList() {
    classUnderTest = new BusinessObject();
    Set<BusinessDomain> expectedBDs = hashSet();
    classUnderTest.addBusinessDomains(expectedBDs);

    Set<BusinessDomain> actualDBs = classUnderTest.getBusinessDomains();
    assertEquals(expectedBDs, actualDBs);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#addBusinessDomains(java.util.Set)} The method
   * tests if the addBusinessDomains adds properly a list with BusinessDomains. The both sides of
   * the association should be set.
   */
  @Test
  public void testAddBusinessDomainsCaseNotEmptyList() {
    classUnderTest = new BusinessObject();

    // set for the other side of the association
    Set<BusinessObject> expectedBOs = hashSet();
    expectedBOs.add(classUnderTest);

    Set<BusinessDomain> expectedBDomains = hashSet();

    BusinessDomain firstBD = new BusinessDomain();
    BusinessDomain secondBD = new BusinessDomain();
    BusinessDomain thirdBD = new BusinessDomain();

    expectedBDomains.add(firstBD);
    expectedBDomains.add(secondBD);
    expectedBDomains.add(thirdBD);

    classUnderTest.addBusinessDomains(expectedBDomains);

    // test the first side of the association
    Set<BusinessDomain> actualBDomains = classUnderTest.getBusinessDomains();
    assertEquals(expectedBDomains, actualBDomains);

    // test the second side of the association
    for (BusinessDomain bd : actualBDomains) {
      Set<BusinessObject> actualBOs = bd.getBusinessObjects();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBOs, actualBOs);
    }
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessObject#addBusinessFunctions(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.BusinessObject#getBusinessDomains()}. The method tests if
   * the addBusinessFunctions adds properly an empty list with BusinessFunctions.
   */
  @Test
  public void testAddBusinessFunctionsCaseEmptyList() {
    classUnderTest = new BusinessObject();
    Set<BusinessFunction> expectedBFs = hashSet();

    classUnderTest.addBusinessFunctions(expectedBFs);
    Set<BusinessFunction> actualDFs = classUnderTest.getBusinessFunctions();
    assertEquals(expectedBFs, actualDFs);
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessObject#addBusinessFunctions(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.BusinessObject#getBusinessDomains()}. The method tests if
   * the addBusinessFunctions adds properly a list with BusinessFunctions. The both sides of the
   * association should be set.
   */
  @Test
  public void testAddBusinessFunctionsCaseNotEmptyList() {
    classUnderTest = new BusinessObject();

    // set for the other side of the association
    Set<BusinessObject> expectedBOs = hashSet();
    expectedBOs.add(classUnderTest);

    Set<BusinessFunction> expectedBFunctions = hashSet();

    BusinessFunction firstBF = new BusinessFunction();
    BusinessFunction secondBF = new BusinessFunction();
    BusinessFunction thirdBF = new BusinessFunction();

    expectedBFunctions.add(firstBF);
    expectedBFunctions.add(secondBF);
    expectedBFunctions.add(thirdBF);

    // test the second side of the association
    classUnderTest.addBusinessFunctions(expectedBFunctions);
    Set<BusinessFunction> actualBFunctions = classUnderTest.getBusinessFunctions();
    assertEquals(expectedBFunctions, actualBFunctions);

    // test the second side of the association
    for (BusinessFunction bf : actualBFunctions) {
      Set<BusinessObject> actualBOs = bf.getBusinessObjects();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBOs, actualBOs);
    }

  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessObject#addSpecialisations(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.BusinessObject#getSpecialisations()}. The method tests if
   * the addSpecialisations adds properly an empty list with BusinessObject. The both sides of the
   * association should be set.
   */
  @Test
  public void testAddSpecialisationsCaseNotEmptyList() {
    classUnderTest = new BusinessObject();

    Set<BusinessObject> expectedSpecialisations = hashSet();

    BusinessObject firstS = new BusinessObject();
    BusinessObject secondS = new BusinessObject();
    BusinessObject thirdS = new BusinessObject();

    expectedSpecialisations.add(firstS);
    expectedSpecialisations.add(secondS);
    expectedSpecialisations.add(thirdS);

    classUnderTest.addSpecialisations(expectedSpecialisations);

    // test the second side of the association
    Set<BusinessObject> actualSpecialisations = classUnderTest.getSpecialisations();
    assertEquals(expectedSpecialisations, actualSpecialisations);

    // test the second side of the association
    for (BusinessObject asp : actualSpecialisations) {
      BusinessObject actualGeneralisation = asp.getGeneralisation();
      assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, classUnderTest, actualGeneralisation);
    }
  }

  /**
   * Test method for (setter and getter)
   * {@link de.iteratec.iteraplan.model.BusinessObject#addSpecialisations(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.BusinessObject#getSpecialisations()}. The method tests if
   * the addSpecialisations adds properly an empty list with BusinessObject.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddSpecialisationsCaseException() {
    classUnderTest = new BusinessObject();
    Set<BusinessObject> expectedSpecialisations = hashSet();

    BusinessObject spec = new BusinessObject();
    spec.setGeneralisation(new BusinessObject());
    expectedSpecialisations.add(spec);

    classUnderTest.addSpecialisations(expectedSpecialisations);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#addSpecialisations(java.util.Set)} The method
   * test if the tested object can use the addSpecialisations (Set<BusinessObject> set) and can add
   * itself as a specialization. That should not be allowed.
   */
  @SuppressWarnings("boxing")
  @Test(expected = IteraplanBusinessException.class)
  public void testAddSpecialisationsCaseClassUnderTestItSelf() {
    classUnderTest = new BusinessObject();
    classUnderTest.setId(1);
    Set<BusinessObject> expectedSpecialisations = hashSet();

    expectedSpecialisations.add(classUnderTest);

    classUnderTest.addSpecialisations(expectedSpecialisations);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#addParent(de.iteratec.iteraplan.model.BusinessObject)}
   * The method tests if the addParent() really throws IllegalArgumentException when the user tries to
   * set the parent with null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddParentCaseNull() {
    classUnderTest = new BusinessObject();
    classUnderTest.addParent(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#addParent(de.iteratec.iteraplan.model.BusinessObject)}
   * The method tests if the method sets itself as parent. An element can not be parent of itself,
   * that is why we expect IteraplanBusinessException.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testAddParentCaseAddItself() {
    classUnderTest = new BusinessObject();
    classUnderTest.setId(Integer.valueOf(1));
    classUnderTest.addParent(classUnderTest);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#addParent(de.iteratec.iteraplan.model.BusinessObject)}
   * The method tests if the method detects cycles. An element A can not have an element B as its
   * parent if B already has A as its parent.
   */
  @Test
  public void testAddParentCaseCycle() {
    classUnderTest = new BusinessObject();
    classUnderTest.setId(Integer.valueOf(1));
    BusinessObject parent1 = new BusinessObject();
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
   * {@link de.iteratec.iteraplan.model.BusinessObject#addParent(de.iteratec.iteraplan.model.BusinessObject)}
   * The method tests if the AddParent method sets correctly another object as parent.
   */
  @SuppressWarnings("boxing")
  @Test
  public void testAddParentCaseAddOtherBO() {
    BusinessObject parent = new BusinessObject();
    parent.setId(1);
    classUnderTest = new BusinessObject();
    classUnderTest.addParent(parent);

    assertEquals(parent, classUnderTest.getParent());

    // check that object is removed from old parent's child list
    BusinessObject newParent = new BusinessObject();
    newParent.setId(3);
    assertEquals(0, newParent.getChildren().size());
    assertEquals(1, parent.getChildren().size());
    classUnderTest.addParent(newParent);
    assertEquals(1, newParent.getChildren().size());
    assertEquals(0, parent.getChildren().size());

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#addTransport(de.iteratec.iteraplan.model.Transport)}
   * .
   */
  @Test
  public void testAddTransport() {
    Transport t1 = new Transport();
    Transport t2 = new Transport();

    Set<Transport> transports = hashSet();
    transports.add(t1);

    classUnderTest = new BusinessObject();
    classUnderTest.setTransports(transports);
    classUnderTest.addTransport(t2);

    transports.add(t2);

    assertEquals(transports, classUnderTest.getTransports());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#getAllConnectionsSorted()}
   * The method tests if the getAllConnectionsSorted() sorts alphabetically correctly a list with
   * InformationSystemInterface. For the test are created four InformationSystems with Releases. Two
   * releases communicate with an interface.
   */
  @Test
  public void testGetAllConnectionsSorted() {

    classUnderTest = new BusinessObject();
    Set<Transport> setTs = hashSet();
    List<Transport> sortedList = arrayList();

    // infosys 1
    InformationSystem thirdIS = new InformationSystem();
    thirdIS.setId(Integer.valueOf(31));
    thirdIS.setName("infosys");

    InformationSystemRelease fifthISR = new InformationSystemRelease();
    fifthISR.setId(Integer.valueOf(5));
    fifthISR.setVersion("5");
    fifthISR.setInformationSystem(thirdIS);
    InformationSystemRelease sixthISR = new InformationSystemRelease();
    sixthISR.setId(Integer.valueOf(6));
    sixthISR.setVersion("6");
    sixthISR.setInformationSystem(thirdIS);

    Transport thirdT = new Transport();
    thirdT.setId(Integer.valueOf(21));
    InformationSystemInterface interface3 = new InformationSystemInterface();
    interface3.setId(Integer.valueOf(53));
    interface3.setInformationSystemReleaseA(fifthISR);
    interface3.setInformationSystemReleaseB(sixthISR);
    thirdT.setInformationSystemInterface(interface3);

    // infosys 2
    InformationSystem firstIS = new InformationSystem();
    firstIS.setId(Integer.valueOf(37));
    firstIS.setName("BlaBla");

    InformationSystemRelease firstISR = new InformationSystemRelease();
    firstISR.setId(Integer.valueOf(1));
    firstISR.setVersion("1");
    firstISR.setInformationSystem(firstIS);
    InformationSystemRelease secISR = new InformationSystemRelease();
    secISR.setId(Integer.valueOf(2));
    secISR.setVersion("2");
    secISR.setInformationSystem(firstIS);

    Transport firstT = new Transport();
    firstT.setId(Integer.valueOf(23));
    InformationSystemInterface interface1 = new InformationSystemInterface();
    interface1.setId(Integer.valueOf(59));
    interface1.setInformationSystemReleaseA(firstISR);
    interface1.setInformationSystemReleaseB(secISR);
    firstT.setInformationSystemInterface(interface1);

    // infosys 3
    InformationSystem secIS = new InformationSystem();
    secIS.setId(Integer.valueOf(53));
    secIS.setName("Marmalada");

    // infosys 4
    InformationSystem fourthIS = new InformationSystem();
    fourthIS.setId(Integer.valueOf(103));
    fourthIS.setName("Pentagona");

    InformationSystemRelease thirdISR = new InformationSystemRelease();
    thirdISR.setId(Integer.valueOf(3));
    thirdISR.setVersion("3");
    thirdISR.setInformationSystem(secIS);
    InformationSystemRelease fourthISR = new InformationSystemRelease();
    fourthISR.setId(Integer.valueOf(4));
    fourthISR.setVersion("4");
    fourthISR.setInformationSystem(fourthIS);

    Transport secondT = new Transport();
    secondT.setId(Integer.valueOf(29));
    InformationSystemInterface interface2 = new InformationSystemInterface();
    interface2.setId(Integer.valueOf(1001));
    interface2.setInformationSystemReleaseA(thirdISR);
    interface2.setInformationSystemReleaseB(fourthISR);
    secondT.setInformationSystemInterface(interface2);

    setTs.add(secondT);
    setTs.add(thirdT);
    setTs.add(firstT);

    sortedList.add(thirdT);
    sortedList.add(secondT);
    sortedList.add(firstT);

    classUnderTest.setTransports(setTs);
    String actual = classUnderTest.getAllConnectionsSorted().toString();
    String expected = "[BlaBla # 1 - BlaBla # 2, infosys # 5 - infosys # 6, Marmalada # 3 - Pentagona # 4]";

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#getAllConnectedInformationsSystemReleasesToIsiSorted()}
   * . The method tests if the getAllConnectedInformationsSystemReleasesToIsiSorted() sorts
   * alphabetically correctly a list with InformationSystemReleases. For the test are created four
   * InformationSystems with Releases. Two releases communicate with an interface.
   */
  @Test
  public void testGetAllConnectedInformationsSystemReleasesToIsiSorted() {

    classUnderTest = new BusinessObject();
    Set<Transport> setTs = hashSet();
    List<Transport> sortedList = arrayList();

    // infosys 1
    InformationSystem thirdIS = new InformationSystem();
    thirdIS.setId(Integer.valueOf(11));
    thirdIS.setName("infosys");

    InformationSystemRelease fifthISR = new InformationSystemRelease();
    fifthISR.setId(Integer.valueOf(5));
    fifthISR.setVersion("5");
    fifthISR.setInformationSystem(thirdIS);
    InformationSystemRelease sixthISR = new InformationSystemRelease();
    sixthISR.setId(Integer.valueOf(6));
    sixthISR.setVersion("6");
    sixthISR.setInformationSystem(thirdIS);

    Transport thirdT = new Transport();
    thirdT.setId(Integer.valueOf(12));
    InformationSystemInterface interface3 = new InformationSystemInterface();
    interface3.setId(Integer.valueOf(13));
    interface3.setInformationSystemReleaseA(fifthISR);
    interface3.setInformationSystemReleaseB(sixthISR);
    thirdT.setInformationSystemInterface(interface3);

    // infosys 2
    InformationSystem firstIS = new InformationSystem();
    firstIS.setId(Integer.valueOf(14));
    firstIS.setName("BlaBla");

    InformationSystemRelease firstISR = new InformationSystemRelease();
    firstISR.setId(Integer.valueOf(1));
    firstISR.setVersion("1");
    firstISR.setInformationSystem(firstIS);
    InformationSystemRelease secISR = new InformationSystemRelease();
    secISR.setId(Integer.valueOf(2));
    secISR.setVersion("2");
    secISR.setInformationSystem(firstIS);

    Transport firstT = new Transport();
    firstT.setId(Integer.valueOf(14));
    InformationSystemInterface interface1 = new InformationSystemInterface();
    interface1.setId(Integer.valueOf(15));
    interface1.setInformationSystemReleaseA(firstISR);
    interface1.setInformationSystemReleaseB(secISR);
    firstT.setInformationSystemInterface(interface1);

    // infosys 3
    InformationSystem secIS = new InformationSystem();
    secIS.setName("Marmalada");

    // infosys 4
    InformationSystem fourthIS = new InformationSystem();
    fourthIS.setId(Integer.valueOf(16));
    fourthIS.setName("Pentagona");

    InformationSystemRelease thirdISR = new InformationSystemRelease();
    thirdISR.setId(Integer.valueOf(3));
    thirdISR.setVersion("3");
    thirdISR.setInformationSystem(secIS);
    InformationSystemRelease fourthISR = new InformationSystemRelease();
    fourthISR.setId(Integer.valueOf(4));
    fourthISR.setVersion("4");
    fourthISR.setInformationSystem(fourthIS);

    Transport secondT = new Transport();
    secondT.setId(Integer.valueOf(17));
    InformationSystemInterface interface2 = new InformationSystemInterface();
    interface2.setId(Integer.valueOf(18));
    interface2.setInformationSystemReleaseA(thirdISR);
    interface2.setInformationSystemReleaseB(fourthISR);
    secondT.setInformationSystemInterface(interface2);

    setTs.add(secondT);
    setTs.add(thirdT);
    setTs.add(firstT);

    sortedList.add(thirdT);
    sortedList.add(secondT);
    sortedList.add(firstT);

    classUnderTest.setTransports(setTs);
    String actual = classUnderTest.getAllConnectedInformationsSystemReleasesToIsiSorted().toString();
    String expected = "[BlaBla # 1, BlaBla # 2, infosys # 5, infosys # 6, Marmalada # 3, Pentagona # 4]";

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * this object is bigger.
   */
  @Test
  public void testCompareToCaseThisBigger() {
    BusinessObject first = new BusinessObject();
    BusinessObject second = new BusinessObject();
    second.setName("second");
    first.setParent(second);
    assertEquals(1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are empty and equal.
   */
  @Test
  public void testCompareToCaseEmptyObjects() {
    BusinessObject first = new BusinessObject();
    BusinessObject second = new BusinessObject();
    assertEquals(0, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * second object is bigger.
   */
  @Test
  public void testCompareToCaseOtherBigger() {
    BusinessObject first = new BusinessObject();
    first.setName(TEST_BO_NAME);
    BusinessObject second = new BusinessObject();

    assertEquals(-1, first.compareTo(second));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * objects are equal. .
   */
  @Test
  public void testCompareToCaseEqualsObjects() {
    BusinessObject first = new BusinessObject();
    BusinessObject second = new BusinessObject();
    assertEquals(0, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is.
   */
  @Test
  public void testCompareToCaseParentEquals() {
    BusinessObject parent = new BusinessObject();
    parent.setId(Integer.valueOf(55));
    parent.setName("parent");
    parent.setDescription(TEST_DESCRIPTION);

    BusinessObject first = new BusinessObject();
    first.setParent(parent);
    first.setId(Integer.valueOf(1));

    BusinessObject second = new BusinessObject();
    second.setParent(parent);
    second.setId(Integer.valueOf(2));

    List<BusinessObject> children = arrayList();
    children.add(first);
    children.add(second);

    parent.setChildren(children);

    assertEquals(1, second.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsFirst() {
    // Initialize
    // firstParent
    BusinessObject firstParent = new BusinessObject();
    firstParent.setId(Integer.valueOf(55));
    firstParent.setName("firstParent");

    // children
    BusinessObject first = new BusinessObject();
    first.setId(Integer.valueOf(167));
    first.setName(TEST_BO_NAME);
    first.setParent(firstParent);

    // secondParent
    BusinessObject secParent = new BusinessObject();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");

    // children
    BusinessObject second = new BusinessObject();
    second.setName("sec");
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessObject> firstChildren = arrayList();
    firstChildren.add(first);
    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessObject> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    BusinessObject root = new BusinessObject();
    root.setName("root");
    root.setId(Integer.valueOf(1000));
    ArrayList<BusinessObject> rootChildren = new ArrayList<BusinessObject>();
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
   * {@link de.iteratec.iteraplan.model.BusinessObject#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The root will be with the first compared. The
   * result should be -1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsSec() {
    // Initialize
    BusinessObject top = new BusinessObject();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    BusinessObject root = new BusinessObject();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    BusinessObject firstParent = new BusinessObject();
    firstParent.setId(Integer.valueOf(55));

    // children
    BusinessObject first = new BusinessObject();
    first.setName(TEST_BO_NAME);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    BusinessObject secParent = new BusinessObject();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription(TEST_DESCRIPTION);

    // children
    BusinessObject second = new BusinessObject();
    second.setName("sec");
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessObject> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessObject> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<BusinessObject> rootChildren = new ArrayList<BusinessObject>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<BusinessObject> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(-1, root.compareTo(first));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#compareTo(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the compareTo method properly takes decision which object bigger is. The
   * tree structure you can find in structure.doc. The second will be with the root compared. The
   * result should be +1. For more info see structure.doc or the comments for the
   * compareToForOrderedHierarchy(T thisInstance, T otherInstance) in HirachieHelper.java
   */
  @Test
  public void testCompareToCaseParentNotEqualsThird() {
    // Initialize
    BusinessObject top = new BusinessObject();
    top.setName("top");
    top.setId(Integer.valueOf(2000));

    BusinessObject root = new BusinessObject();
    root.setName("root");
    root.setId(Integer.valueOf(1000));

    // firstParent
    BusinessObject firstParent = new BusinessObject();
    firstParent.setId(Integer.valueOf(55));

    // children
    BusinessObject first = new BusinessObject();
    first.setName(TEST_BO_NAME);
    first.setParent(firstParent);
    first.setId(Integer.valueOf(167));

    // secondParent
    BusinessObject secParent = new BusinessObject();
    secParent.setId(Integer.valueOf(60));
    secParent.setName("secParent");
    secParent.setDescription(TEST_DESCRIPTION);

    // children
    BusinessObject second = new BusinessObject();
    second.setName("sec");
    second.setParent(secParent);
    second.setId(Integer.valueOf(2));

    // firstParent knows its children
    List<BusinessObject> firstChildren = arrayList();
    firstChildren.add(first);

    firstParent.setChildren(firstChildren);

    // secParent knows its children
    List<BusinessObject> secChildren = arrayList();
    secChildren.add(second);

    secParent.setChildren(secChildren);

    // root
    ArrayList<BusinessObject> rootChildren = new ArrayList<BusinessObject>();
    rootChildren.add(firstParent);
    rootChildren.add(secParent);
    root.setParent(top);
    root.setChildren(rootChildren);

    // firstParent knows its parent
    firstParent.addParent(root);
    secParent.addParent(root);

    // top
    List<BusinessObject> topChildren = arrayList();
    topChildren.add(root);
    top.setChildren(topChildren);

    assertEquals(1, second.compareTo(root));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#getChildrenAsList()} The
   * method tests if the getChildrenAsList() correctly returns the list with the children as a list.
   */
  @Test
  public void testGetChildrenAsList() {
    classUnderTest = new BusinessObject();
    assertNotNull(classUnderTest.getChildrenAsList());

    // Add some children:
    List<BusinessObject> expected = arrayList();

    // child 1
    BusinessObject firstChild = new BusinessObject();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("child1");
    expected.add(firstChild);

    // child 2 = child 3
    BusinessObject secChild = new BusinessObject();
    expected.add(secChild);
    expected.add(secChild);

    classUnderTest.setChildren(expected);

    List<BusinessObject> actual = classUnderTest.getChildrenAsList();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#getChildrenAsSet()} The
   * method tests if the GetChildrenAsSet() correctly returns the list with the children as a set.
   */
  @Test
  public void testGetChildrenAsSet() {
    classUnderTest = new BusinessObject();
    assertNotNull(classUnderTest.getChildrenAsSet());

    // Add some children:
    List<BusinessObject> expected = arrayList();
    Set<BusinessObject> actual = hashSet();

    // child 1
    BusinessObject firstChild = new BusinessObject();
    firstChild.setId(Integer.valueOf(1));
    firstChild.setName("child1");
    expected.add(firstChild);

    // child 2 = child 3
    BusinessObject secChild = new BusinessObject();
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
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#removeBusinessDomainRelations()} The method
   * test if the removeBusinessDomainRelations() removes correctly all the BusinessDomainRelations.
   */
  @Test
  public void testRemoveBusinessDomainRelations() {
    classUnderTest = new BusinessObject();
    Set<BusinessDomain> initBDs = hashSet();

    BusinessDomain firstBD = new BusinessDomain();
    BusinessDomain secondBD = new BusinessDomain();
    BusinessDomain thirdBD = new BusinessDomain();
    BusinessDomain fourthBD = new BusinessDomain();
    BusinessDomain fifthBD = new BusinessDomain();
    BusinessDomain sixthBD = new BusinessDomain();
    BusinessDomain seventhBD = new BusinessDomain();
    BusinessDomain eightBD = new BusinessDomain();
    BusinessDomain ninthBD = new BusinessDomain();
    BusinessDomain tenthBD = new BusinessDomain();
    BusinessDomain eleventhBD = new BusinessDomain();

    initBDs.add(firstBD);
    initBDs.add(secondBD);
    initBDs.add(thirdBD);

    initBDs.add(fourthBD);
    initBDs.add(fifthBD);
    initBDs.add(sixthBD);

    initBDs.add(seventhBD);
    initBDs.add(eightBD);
    initBDs.add(ninthBD);

    initBDs.add(tenthBD);
    initBDs.add(eleventhBD);

    classUnderTest.addBusinessDomains(initBDs);
    classUnderTest.removeBusinessDomainRelations();

    Set<BusinessDomain> expectedBDs = hashSet();
    Set<BusinessDomain> actualBDs = classUnderTest.getBusinessDomains();

    assertEquals(expectedBDs, actualBDs);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessObject#removeBusinessFunctionRelations()} The method
   * test if the removeBusinessFunctionRelations() removes correctly all the
   * BusinessFunctionRelations.
   */
  @Test
  public void testRemoveBusinessFunctionRelations() {
    classUnderTest = new BusinessObject();
    Set<BusinessFunction> initBFs = hashSet();

    BusinessFunction firstBF = new BusinessFunction();
    BusinessFunction secondBF = new BusinessFunction();
    BusinessFunction thirdBF = new BusinessFunction();
    BusinessFunction fourthBF = new BusinessFunction();
    BusinessFunction fifthBF = new BusinessFunction();
    BusinessFunction sixthBF = new BusinessFunction();
    BusinessFunction seventhBF = new BusinessFunction();
    BusinessFunction eightBF = new BusinessFunction();
    BusinessFunction ninthBF = new BusinessFunction();
    BusinessFunction tenthBF = new BusinessFunction();
    BusinessFunction eleventhBF = new BusinessFunction();

    initBFs.add(firstBF);
    initBFs.add(secondBF);
    initBFs.add(thirdBF);

    initBFs.add(fourthBF);
    initBFs.add(fifthBF);
    initBFs.add(sixthBF);

    initBFs.add(seventhBF);
    initBFs.add(eightBF);
    initBFs.add(ninthBF);

    initBFs.add(tenthBF);
    initBFs.add(eleventhBF);

    classUnderTest.addBusinessFunctions(initBFs);
    classUnderTest.removeBusinessFunctionRelations();

    Set<BusinessFunction> expectedBFs = hashSet();
    Set<BusinessFunction> actualBFs = classUnderTest.getBusinessFunctions();
    assertEquals(expectedBFs, actualBFs);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#removeAllChildren()}
   * {@link de.iteratec.iteraplan.model.BusinessObject#removeSpecialisationRelations()} The method
   * tests the removeAllChildren() if it removes correctly all the Children relations. The method
   * also tests if the removeSpecialisationRelations() removes correctly all the Specializations.
   */
  @Test
  public void testRemoveSpecialisationRelationsRemoveAllChildren() {
    classUnderTest = new BusinessObject();

    // Set the Specialization Relations
    Set<BusinessObject> initBSpecialisations = hashSet();

    BusinessObject firstBO = new BusinessObject();
    BusinessObject secondBO = new BusinessObject();
    BusinessObject thirdBO = new BusinessObject();

    initBSpecialisations.add(firstBO);
    initBSpecialisations.add(secondBO);
    initBSpecialisations.add(thirdBO);

    // set the children relations
    List<BusinessObject> children = arrayList();
    children.add(firstBO);
    children.add(secondBO);
    children.add(thirdBO);

    classUnderTest.addSpecialisations(initBSpecialisations);
    classUnderTest.setChildren(children);
    classUnderTest.removeAllChildren();

    Set<BusinessObject> expectedBSpecialisations = hashSet();
    Set<BusinessObject> actualBSpecialisations = classUnderTest.getSpecialisations();
    List<BusinessObject> expectedChildren = arrayList();
    List<BusinessObject> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedBSpecialisations, actualBSpecialisations);
    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#removeGeneralisation()} The
   * method test if the removeGeneralisation() removes correctly all the Generalisation.
   */
  @Test
  public void testRemoveGeneralisation() {
    classUnderTest = new BusinessObject();

    BusinessObject bo = new BusinessObject();

    classUnderTest.setGeneralisation(bo);
    classUnderTest.removeGeneralisation();

    BusinessObject expected = null;
    BusinessObject actual = classUnderTest.getGeneralisation();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#removeInformationSystemReleaseAssociations()}
   * The method test if the removeInformationSystemReleases() removes correctly all the
   * InformationSystemReleases.
   */
  @Test
  public void testRemoveInformationSystemReleases() {
    classUnderTest = new BusinessObject();

    InformationSystemRelease firstISR = new InformationSystemRelease();
    InformationSystemRelease secondISR = new InformationSystemRelease();
    InformationSystemRelease thirdISR = new InformationSystemRelease();

    new Isr2BoAssociation(firstISR, classUnderTest).connect();
    new Isr2BoAssociation(secondISR, classUnderTest).connect();
    new Isr2BoAssociation(thirdISR, classUnderTest).connect();

    classUnderTest.removeInformationSystemReleaseAssociations();

    Set<InformationSystemRelease> expectedISRs = hashSet();
    Set<InformationSystemRelease> actualISRs = classUnderTest.getInformationSystemReleases();
    assertEquals(expectedISRs, actualISRs);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#removeParent()} The method
   * tests if the removeParent method removes the parent object correctly. The attributes of the
   * parent object save same information.
   */
  @Test
  public void testRemoveParentCaseNotNull() {
    classUnderTest = new BusinessObject();

    BusinessObject parent = new BusinessObject();
    parent.setId(Integer.valueOf(55));

    classUnderTest.addParent(parent);
    classUnderTest.removeParent();

    BusinessObject expectedParent = null;
    BusinessObject actualParent = classUnderTest.getParent();

    List<BusinessObject> expectedChildren = arrayList();
    List<BusinessObject> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedParent, actualParent);
    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#removeParent()} The method
   * tests if the method removes the parent object. The attributes of the parent object saves no
   * information.
   */
  @Test
  public void testRemoveParentCaseNull() {
    classUnderTest = new BusinessObject();

    classUnderTest.removeParent();

    BusinessObject expectedParent = null;
    BusinessObject actualParent = classUnderTest.getParent();

    List<BusinessObject> expectedChildren = arrayList();
    List<BusinessObject> actualChildren = classUnderTest.getChildren();

    assertEquals(expectedParent, actualParent);
    assertEquals(expectedChildren, actualChildren);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#validate()} The method tests
   * if the validate method throws exception correctly.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateFirstCase() {
    //generalization on hierarchy cycle bo
    classUnderTest = new BusinessObject();

    BusinessObject top = new BusinessObject();
    top.setName("-");
    top.setId(Integer.valueOf(0));

    classUnderTest.setName(TEST_BO_NAME);
    classUnderTest.setId(Integer.valueOf(765));

    classUnderTest.setParent(top);
    classUnderTest.setGeneralisation(classUnderTest);

    classUnderTest.validate();

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#validate()} The method tests
   * if the validate method throws exception correctly.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateCaseException() {
    //element of hierarchy cycle
    classUnderTest = new BusinessObject();
    classUnderTest.setId(Integer.valueOf(765));

    classUnderTest.setParent(classUnderTest);

    classUnderTest.validate();

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#validate()} The method tests
   * if the validate method throws exception correctly.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateFirstCaseException() {
    //name cannot be empty
    classUnderTest = new BusinessObject();
    classUnderTest.setName(null);
    classUnderTest.validate();
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#validate()} The method tests
   * if the validate method throws exception correctly.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateSecCaseException() {
    //name cannot be empty
    classUnderTest = new BusinessObject();
    classUnderTest.setName("");
    classUnderTest.validate();

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#validate()} The method tests
   * if the validate method throws exception correctly.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateCaseError() {
    //general technical error
    classUnderTest = new BusinessObject();

    classUnderTest.setParent(null);
    classUnderTest.setName("qwerty");

    classUnderTest.validate();
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#validate()} The method tests
   * if the validate method validates correctly. For that test case the log4jrootLogger must be set
   * on DEBUG, stdout. The flag is in the /iteraplan/WebContent/Web-Inf/classes/log4j.properties
   */
  @Test
  public void testValidateSecCaseNoException() {
    classUnderTest = new BusinessObject();

    BusinessObject top = new BusinessObject();
    top.setName("-");
    top.setId(Integer.valueOf(0));

    classUnderTest.setName("test");
    classUnderTest.setId(Integer.valueOf(765));

    classUnderTest.setParent(top);
    classUnderTest.setGeneralisation(top);

    classUnderTest.validate();

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessObject#getI18NKey()} The method
   * tests the getI18NKey() method. The test method has only meaning for the code coverage.
   */
  @Test
  public void testGetI18NKey() {
    classUnderTest = new BusinessObject();
    assertEquals("businessObject.virtualElement", classUnderTest.getI18NKey());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#setBusinessDomains(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#setBusinessFunctions(java.util.Set)}
   * {@link de.iteratec.iteraplan.model.InfrastructureElement#setInformationSystemReleases(java.util.Set)}
   * The method has only meaning for the code coverage.
   */
  @Test
  public void testSetTCRsAndSetISRs() {
    classUnderTest = new BusinessObject();

    Set<BusinessDomain> expectedBDElements = hashSet();
    expectedBDElements.add(new BusinessDomain());
    expectedBDElements.add(new BusinessDomain());
    expectedBDElements.add(new BusinessDomain());
    expectedBDElements.add(new BusinessDomain());
    expectedBDElements.add(new BusinessDomain());

    classUnderTest.setBusinessDomains(expectedBDElements);
    Set<BusinessDomain> actualBDElements = classUnderTest.getBusinessDomains();
    assertEquals(expectedBDElements, actualBDElements);

    Set<BusinessFunction> expectedBFElements = hashSet();
    expectedBFElements.add(new BusinessFunction());
    expectedBFElements.add(new BusinessFunction());
    expectedBFElements.add(new BusinessFunction());
    expectedBFElements.add(new BusinessFunction());
    expectedBFElements.add(new BusinessFunction());

    classUnderTest.setBusinessFunctions(expectedBFElements);
    Set<BusinessFunction> actualBFElements = classUnderTest.getBusinessFunctions();
    assertEquals(expectedBFElements, actualBFElements);

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

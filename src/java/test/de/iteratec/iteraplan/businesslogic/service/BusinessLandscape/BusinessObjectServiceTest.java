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
package de.iteratec.iteraplan.businesslogic.service.BusinessLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.BusinessObjectService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 * Class for testing the service methods of the {@link BusinessObjectService} interface.
 */
public class BusinessObjectServiceTest extends BaseTransactionalTestSupport {

  private static final String             TEST_BO_NAME_1   = "firstTestBO";
  private static final String             TEST_BO_NAME_2   = "secTestBO";
  private static final String             TEST_BO_NAME_3   = "thirdTestBO";
  private static final String             TEST_BO_NAME_4   = "fourthTestBO";
  private static final String             TEST_BO_NAME_5   = "fifthTestBO";
  private static final String             TEST_DESCRIPTION = "testDescription";
  @Autowired
  private BusinessObjectService           businessObjectService;
  @Autowired
  private InformationSystemReleaseService releaseService;
  @Autowired
  private TestDataHelper2                 testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectServiceImpl#deleteEntity(java.lang.Integer)}
   * The method tests if deleteEntity() deletes correctly businessObject from the database.
   * Here is the Parent / Child - Hierarchy that the test method build:
   * <p>
   * ----A---- | | B ----C---- | | D E
   * <p>
   * Generalisation / Specialisation - Hierarchy:
   * <p>
   * ----F---- | | A ----C | B ----D---- | | G ----H | E
   * <p>
   * Associations to information system releases:
   * <p>
   * release A: A, C, E release B: B, C, D release C: A, D
   */
  @Test
  public void testDeleteEntity() {
    Object[] obj = createTestDeleteEntityData();
    BusinessObject root = (BusinessObject) obj[0];
    BusinessObject objectA = (BusinessObject) obj[1];
    BusinessObject objectB = (BusinessObject) obj[2];
    BusinessObject objectC = (BusinessObject) obj[3];
    BusinessObject objectD = (BusinessObject) obj[4];
    BusinessObject objectE = (BusinessObject) obj[5];
    BusinessObject objectF = (BusinessObject) obj[6];
    BusinessObject objectG = (BusinessObject) obj[7];
    BusinessObject objectH = (BusinessObject) obj[8];
    InformationSystemRelease releaseA = (InformationSystemRelease) obj[9];

    // Delete element A.
    beginTransaction();
    Integer idA = objectA.getId();
    objectA = businessObjectService.loadObjectById(idA);
    businessObjectService.deleteEntity(objectA);

    commit();
    beginTransaction();

    // Elments A, B, C, D, E have been deleted.
    objectA = businessObjectService.loadObjectByIdIfExists(idA);
    objectB = businessObjectService.loadObjectByIdIfExists(objectB.getId());
    objectC = businessObjectService.loadObjectByIdIfExists(objectC.getId());
    objectD = businessObjectService.loadObjectByIdIfExists(objectD.getId());
    objectE = businessObjectService.loadObjectByIdIfExists(objectE.getId());
    assertNull(objectA);
    assertNull(objectB);
    assertNull(objectC);
    assertNull(objectD);
    assertNull(objectE);

    // The root element has got no more children.
    root = businessObjectService.getFirstElement();
    assertEquals(3, root.getChildren().size());

    // The information system release has got no more associated elements.
    releaseA = releaseService.loadObjectById(releaseA.getId());
    assertEquals(0, releaseA.getBusinessObjects().size());

    // Elements F, G, H have not been deleted.
    objectF = businessObjectService.loadObjectByIdIfExists(objectF.getId());
    assertNotNull(objectF);
    objectG = businessObjectService.loadObjectByIdIfExists(objectG.getId());
    assertNotNull(objectG);
    objectH = businessObjectService.loadObjectByIdIfExists(objectH.getId());
    assertNotNull(objectH);

    // Elements F, H have got no specialisations.
    assertEquals(0, objectF.getSpecialisations().size());
    assertEquals(0, objectH.getSpecialisations().size());

    // Elements G, H have got no generalisation.
    assertNull(objectG.getGeneralisation());
    assertNull(objectH.getGeneralisation());

    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectServiceImpl#deleteEntity(de.iteratec.iteraplan.model.BusinessObject)}
   * The method tests if deleteEntity() throws correctly IteraplanBusinessException if the given element is the root
   */@Test
  public void testDeleteEntityCaseIteraplanBusinessException() {
    try {
      BusinessObject root = businessObjectService.getFirstElement();
      businessObjectService.deleteEntity(root);
    } catch (IteraplanBusinessException e) {
      // do noting, it's OK
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectServiceImpl#getAvailableGeneralisations(java.lang.Integer)}
   * The method tests if getAvailableGeneralisations() gets correctly list with available
   * businessObjectGeneralisations from the DataBase with a given id.
   */@Test
  public void testGetAvailableGeneralisationCaseNull() {
    List<BusinessObject> expected = arrayList();
    BusinessObject root = businessObjectService.getFirstElement();

    expected.add(root);
    List<BusinessObject> actual = businessObjectService.getAvailableGeneralisations(null);

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectServiceImpl#getAvailableGeneralisations(java.lang.Integer)}
   * The method tests if getAvailableGeneralisations() gets correctly list with available
   * businessObjectGeneralisations from the DataBase with a given id.
   */@Test
  public void testGetAvailableGeneralisationCaseNotNull() {
    // Create the data.
    BusinessObject testGeneralisation = testDataHelper.createBusinessObject("testGeneralisation", TEST_DESCRIPTION);
    BusinessObject firstTestSpecialisation = testDataHelper.createBusinessObject("firstTestSpecialisation", TEST_DESCRIPTION);
    BusinessObject secTestSpecialisation = testDataHelper.createBusinessObject("secTestSpecialisation", TEST_DESCRIPTION);
    BusinessObject thirdTestSpecialisation = testDataHelper.createBusinessObject("thirdTestSpecialisation", TEST_DESCRIPTION);
    commit();

    Set<BusinessObject> initSpecSet = hashSet();
    initSpecSet.add(firstTestSpecialisation);
    initSpecSet.add(secTestSpecialisation);
    initSpecSet.add(thirdTestSpecialisation);

    Set<BusinessObject> specialisations = hashSet();
    specialisations.add(firstTestSpecialisation);
    specialisations.add(secTestSpecialisation);
    specialisations.add(thirdTestSpecialisation);

    // Create the associations.
    beginTransaction();
    testGeneralisation.addSpecialisations(specialisations);
    firstTestSpecialisation.addGeneralisation(testGeneralisation);
    secTestSpecialisation.addGeneralisation(testGeneralisation);
    thirdTestSpecialisation.addGeneralisation(testGeneralisation);

    businessObjectService.saveOrUpdate(testGeneralisation);
    businessObjectService.saveOrUpdate(firstTestSpecialisation);
    businessObjectService.saveOrUpdate(secTestSpecialisation);
    businessObjectService.saveOrUpdate(thirdTestSpecialisation);
    commit();

    beginTransaction();
    BusinessObject root = businessObjectService.getFirstElement();

    List<BusinessObject> expected = arrayList();
    expected.add(root);
    expected.add(firstTestSpecialisation);
    expected.add(testGeneralisation);
    expected.add(thirdTestSpecialisation);

    Integer id = secTestSpecialisation.getId();
    List<BusinessObject> actual = businessObjectService.getAvailableGeneralisations(id);

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectServiceImpl#getBusinessObjectsWithoutGeneralisation()}
   * The method tests if getBusinessObjectsWithoutGeneralisation() gets correctly list with
   * businessObjects from the DataBase without generalization.
   */@Test
  public void testGetBusinessObjectsWithoutGeneralisation() {
    // Create the data.
    BusinessObject testGeneralisation = testDataHelper.createBusinessObject("testGeneralisation", TEST_DESCRIPTION);
    BusinessObject firstTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_1, TEST_DESCRIPTION);
    BusinessObject secTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_2, TEST_DESCRIPTION);
    BusinessObject thirdTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_3, TEST_DESCRIPTION);

    BusinessObject root = businessObjectService.getFirstElement();

    // Create the associations.
    testGeneralisation.setGeneralisation(root);
    businessObjectService.saveOrUpdate(testGeneralisation);

    commit();

    List<BusinessObject> expected = arrayList();
    expected.add(root);
    expected.add(firstTestBO);
    expected.add(secTestBO);
    expected.add(thirdTestBO);

    List<BusinessObject> actual = businessObjectService.getBusinessObjectsWithoutGeneralisation();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectServiceImpl#getBusinessObjectsBySearch(de.iteratec.iteraplan.model.BusinessObject, int, int)}
   * The method tests if the getBusinessObjectsBySearch() returns correct list with BusinessObjects.
   */@Test
  public void testGetBusinessObjectsBySearch() {
    BusinessObject testBO = testDataHelper.createBusinessObject("testBO", TEST_DESCRIPTION);
    commit();
    beginTransaction();

    testBO = businessObjectService.loadObjectById(testBO.getId());

    BusinessObject root = businessObjectService.getFirstElement();
    testBO.addParent(root);

    businessObjectService.saveOrUpdate(testBO);
    commit();

    // delete rootBD
    beginTransaction();

    // get Object by search
    // search for ObjectA
    List<BusinessObject> actualList = businessObjectService.getEntityResultsBySearch(testBO);
    commit();

    List<BusinessObject> expected = arrayList();
    expected.add(testBO);

    assertEquals(expected.size(), actualList.size());
    assertEquals(expected, actualList);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectServiceImpl#getAvailableSpecialisations(java.lang.Integer , java.util.List,java.lang.Boolean)}
   * The method tests if the getAvailableSpecialisations() returns correct list with all the
   * available BusinessObjectSpecifications.
   */@Test
  public void testGetAvailableSpecialisations() {
    // Create the data.
    BusinessObject testGeneral = testDataHelper.createBusinessObject("general", TEST_DESCRIPTION);

    BusinessObject firstTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_1, TEST_DESCRIPTION);
    BusinessObject secTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_2, TEST_DESCRIPTION);
    BusinessObject thirdTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_3, TEST_DESCRIPTION);
    BusinessObject fourthTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_4, TEST_DESCRIPTION);
    BusinessObject fifthTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_5, TEST_DESCRIPTION);
    commit();

    Set<BusinessObject> specSet = hashSet();
    specSet.add(firstTestBO);
    specSet.add(secTestBO);
    specSet.add(thirdTestBO);
    specSet.add(fourthTestBO);
    specSet.add(fifthTestBO);

    beginTransaction();
    testGeneral.addSpecialisations(specSet);
    businessObjectService.saveOrUpdate(testGeneral);
    commit();

    List<BusinessObject> elementsToExclude = arrayList();
    elementsToExclude.add(firstTestBO);
    elementsToExclude.add(secTestBO);

    List<BusinessObject> expected = arrayList();
    expected.add(thirdTestBO);
    expected.add(fourthTestBO);
    expected.add(fifthTestBO);

    beginTransaction();
    Integer id = testGeneral.getId();
    List<BusinessObject> actual = businessObjectService.getAvailableSpecialisations(id, elementsToExclude, false);
    commit();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectServiceImpl#getAvailableSpecialisations(java.lang.Integer , java.util.List,java.lang.Boolean)}
   * The method tests if the getAvailableSpecialisations() throws correctly IllegalStateException if
   * the elementsToExclude list includes the root element.
   */@Test
  public void testGetAvailableSpecialisationsCaseIllegalStateException() {
    // Create the data.
    try {
      BusinessObject testGeneral = testDataHelper.createBusinessObject("general", TEST_DESCRIPTION);

      BusinessObject firstTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_1, TEST_DESCRIPTION);
      BusinessObject secTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_2, TEST_DESCRIPTION);
      BusinessObject thirdTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_3, TEST_DESCRIPTION);
      BusinessObject fourthTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_4, TEST_DESCRIPTION);
      BusinessObject fifthTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_5, TEST_DESCRIPTION);
      commit();

      Set<BusinessObject> specSet = hashSet();
      specSet.add(firstTestBO);
      specSet.add(secTestBO);
      specSet.add(thirdTestBO);
      specSet.add(fourthTestBO);
      specSet.add(fifthTestBO);

      beginTransaction();
      testGeneral.addSpecialisations(specSet);
      businessObjectService.saveOrUpdate(testGeneral);
      BusinessObject root = businessObjectService.getFirstElement();
      commit();

      List<BusinessObject> elementsToExclude = arrayList();
      elementsToExclude.add(root);
      elementsToExclude.add(firstTestBO);
      elementsToExclude.add(secTestBO);

      beginTransaction();
      Integer id = testGeneral.getId();
      businessObjectService.getAvailableSpecialisations(id, elementsToExclude, false);
      commit();
    } catch (IllegalStateException e) {
      // do noting, it's OK
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectServiceImpl#getAvailableSpecialisations(java.lang.Integer, java.util.List, java.lang.Boolean)}
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectServiceImpl#removeHierarchyViolation(java.util.List, de.iteratec.iteraplan.model.BusinessObject)}
   * The method tests if the getAvailableSpecialisations() calls removeHierarchyViolation () and
   * that one returns a correct result.
   */@Test
  public void testGetAvailableSpecialisationsFirstCaseremoveHierarchyViolation() {
    // Create the data.
    BusinessObject testGeneral = testDataHelper.createBusinessObject("general1", TEST_DESCRIPTION);
    BusinessObject testSuper = testDataHelper.createBusinessObject("super", TEST_DESCRIPTION);

    BusinessObject firstTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_1, TEST_DESCRIPTION);
    BusinessObject secTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_2, TEST_DESCRIPTION);
    BusinessObject thirdTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_3, TEST_DESCRIPTION);
    BusinessObject fourthTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_4, TEST_DESCRIPTION);
    BusinessObject fifthTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_5, TEST_DESCRIPTION);
    commit();

    Set<BusinessObject> specSet = hashSet();
    specSet.add(firstTestBO);
    specSet.add(secTestBO);
    specSet.add(thirdTestBO);
    specSet.add(fourthTestBO);
    specSet.add(fifthTestBO);

    beginTransaction();
    testGeneral.addSpecialisations(specSet);
    testGeneral.setGeneralisation(testSuper);

    firstTestBO.setGeneralisation(testGeneral);
    secTestBO.setGeneralisation(testGeneral);
    thirdTestBO.setGeneralisation(testGeneral);
    fourthTestBO.setGeneralisation(testGeneral);
    fifthTestBO.setGeneralisation(testGeneral);

    businessObjectService.saveOrUpdate(firstTestBO);
    businessObjectService.saveOrUpdate(secTestBO);
    businessObjectService.saveOrUpdate(thirdTestBO);
    businessObjectService.saveOrUpdate(fourthTestBO);
    businessObjectService.saveOrUpdate(fifthTestBO);
    businessObjectService.saveOrUpdate(testGeneral);
    businessObjectService.saveOrUpdate(testSuper);
    commit();

    List<BusinessObject> elementsToExclude = arrayList();
    elementsToExclude.add(testGeneral);
    elementsToExclude.add(firstTestBO);
    elementsToExclude.add(secTestBO);

    List<BusinessObject> expected = arrayList();

    beginTransaction();
    Integer id = testSuper.getId();
    List<BusinessObject> actual = businessObjectService.getAvailableSpecialisations(id, elementsToExclude, false);
    commit();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectServiceImpl#getAvailableSpecialisations(java.lang.Integer, java.util.List, java.lang.Boolean)}
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectServiceImpl#removeHierarchyViolation(java.util.List, de.iteratec.iteraplan.model.BusinessObject)}
   * The method tests if the getAvailableSpecialisations() calls removeHierarchyViolation () and
   * that one returns a correct result.
   */@Test
  public void testGetAvailableSpecialisationsSecCaseremoveHierarchyViolation() {
    // Create the data.
    BusinessObject testGeneral = testDataHelper.createBusinessObject("general", TEST_DESCRIPTION);

    BusinessObject firstTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_1, TEST_DESCRIPTION);
    BusinessObject secTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_2, TEST_DESCRIPTION);
    BusinessObject thirdTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_3, TEST_DESCRIPTION);
    BusinessObject fourthTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_4, TEST_DESCRIPTION);
    BusinessObject fifthTestBO = testDataHelper.createBusinessObject(TEST_BO_NAME_5, TEST_DESCRIPTION);
    commit();

    Set<BusinessObject> specSet = hashSet();
    specSet.add(firstTestBO);
    specSet.add(secTestBO);
    specSet.add(thirdTestBO);
    specSet.add(fourthTestBO);
    specSet.add(fifthTestBO);

    beginTransaction();
    testGeneral.addSpecialisations(specSet);

    firstTestBO.setGeneralisation(testGeneral);
    secTestBO.setGeneralisation(testGeneral);
    thirdTestBO.setGeneralisation(testGeneral);
    fourthTestBO.setGeneralisation(testGeneral);
    fifthTestBO.setGeneralisation(testGeneral);

    businessObjectService.saveOrUpdate(firstTestBO);
    businessObjectService.saveOrUpdate(secTestBO);
    businessObjectService.saveOrUpdate(thirdTestBO);
    businessObjectService.saveOrUpdate(fourthTestBO);
    businessObjectService.saveOrUpdate(fifthTestBO);
    businessObjectService.saveOrUpdate(testGeneral);
    commit();

    List<BusinessObject> elementsToExclude = arrayList();
    elementsToExclude.add(firstTestBO);
    elementsToExclude.add(secTestBO);

    List<BusinessObject> expected = arrayList();
    expected.add(thirdTestBO);
    expected.add(fourthTestBO);
    expected.add(fifthTestBO);

    beginTransaction();
    Integer id = testGeneral.getId();
    List<BusinessObject> actual = businessObjectService.getAvailableSpecialisations(id, elementsToExclude, false);
    commit();

    assertEquals(expected, actual);
  }

  private Object[] createTestDeleteEntityData() {
    InformationSystem informationSystem = testDataHelper.createInformationSystem("System");
    InformationSystemRelease releaseA = testDataHelper.createInformationSystemRelease(informationSystem, "1.0");
    InformationSystemRelease releaseB = testDataHelper.createInformationSystemRelease(informationSystem, "2.0");
    InformationSystemRelease releaseC = testDataHelper.createInformationSystemRelease(informationSystem, "3.0");

    BusinessObject objectA = testDataHelper.createBusinessObject("Object-A", "-");
    BusinessObject objectB = testDataHelper.createBusinessObject("Object-B", "-");
    BusinessObject objectC = testDataHelper.createBusinessObject("Object-C", "-");
    BusinessObject objectD = testDataHelper.createBusinessObject("Object-D", "-");
    BusinessObject objectE = testDataHelper.createBusinessObject("Object-E", "-");
    BusinessObject objectF = testDataHelper.createBusinessObject("Object-F", "-");
    BusinessObject objectG = testDataHelper.createBusinessObject("Object-G", "-");
    BusinessObject objectH = testDataHelper.createBusinessObject("Object-H", "-");
    commit();
    beginTransaction();

    objectA = businessObjectService.loadObjectById(objectA.getId());
    objectB = businessObjectService.loadObjectById(objectB.getId());
    objectC = businessObjectService.loadObjectById(objectC.getId());
    objectD = businessObjectService.loadObjectById(objectD.getId());
    objectE = businessObjectService.loadObjectById(objectE.getId());
    objectF = businessObjectService.loadObjectById(objectF.getId());
    objectG = businessObjectService.loadObjectById(objectG.getId());
    objectH = businessObjectService.loadObjectById(objectH.getId());

    BusinessObject root = businessObjectService.getFirstElement();

    objectA.addParent(root);
    objectB.addParent(objectA);
    objectC.addParent(objectA);
    objectD.addParent(objectC);
    objectE.addParent(objectC);

    objectA.addGeneralisation(objectF);
    objectC.addGeneralisation(objectF);
    objectB.addGeneralisation(objectF);

    objectG.addGeneralisation(objectD);
    objectH.addGeneralisation(objectD);
    objectE.addGeneralisation(objectH);

    businessObjectService.saveOrUpdate(objectA);
    businessObjectService.saveOrUpdate(objectB);
    businessObjectService.saveOrUpdate(objectC);
    businessObjectService.saveOrUpdate(objectD);
    businessObjectService.saveOrUpdate(objectE);
    businessObjectService.saveOrUpdate(objectF);
    businessObjectService.saveOrUpdate(objectG);
    businessObjectService.saveOrUpdate(objectH);

    testDataHelper.addBusinessObjectToInformationSystem(releaseA, objectA);
    testDataHelper.addBusinessObjectToInformationSystem(releaseA, objectC);
    testDataHelper.addBusinessObjectToInformationSystem(releaseA, objectE);
    testDataHelper.addBusinessObjectToInformationSystem(releaseB, objectB);
    testDataHelper.addBusinessObjectToInformationSystem(releaseB, objectC);
    testDataHelper.addBusinessObjectToInformationSystem(releaseB, objectD);
    testDataHelper.addBusinessObjectToInformationSystem(releaseC, objectA);
    testDataHelper.addBusinessObjectToInformationSystem(releaseC, objectD);
    commit();

    return new Object[] { root, objectA, objectB, objectC, objectD, objectE, objectF, objectG, objectH, releaseA };
  }
}

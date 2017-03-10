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
package de.iteratec.iteraplan.businesslogic.service.TechnicalLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.Role2BbtPermission;
import de.iteratec.iteraplan.model.user.Role2BbtPermission.EditPermissionType;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.User;


/**
 * Test class for service interface {@link TechnicalComponentReleaseService}.
 */
public class TechnicalComponentReleaseServiceTest extends BaseTransactionalTestSupport {

  @Autowired
  private TechnicalComponentReleaseService tcrService;
  @Autowired
  private InfrastructureElementService     infrastructureElementService;
  @Autowired
  private InformationSystemReleaseService  informationSystemReleaseService;
  @Autowired
  private TechnicalComponentService        technicalComponentService;
  @Autowired
  private TestDataHelper2                  testDataHelper;

  private static final String              STANDARD_START_DATE = "1.1.2007";
  private static final String              STANDARD_END_DATE   = "10.10.2007";

  private static final String              TEST_TC_NAME        = "testTC";
  private static final String              VERSION_1_0         = "1.0";
  private static final String              VERSION_1_1         = "1.1";
  private static final String              TEST_DESCRIPTION    = "testDescription";

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testSaveEntity() {
    InformationSystem informationSystem = testDataHelper.createInformationSystem("System");
    InformationSystemRelease iSRelease = testDataHelper.createInformationSystemRelease(informationSystem, VERSION_1_0);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement("Infra-A", TEST_DESCRIPTION);
    commit();

    TechnicalComponent catalogItem = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, false);

    TechnicalComponentRelease release = testDataHelper.createTCRelease(catalogItem, VERSION_1_1, TEST_DESCRIPTION, STANDARD_START_DATE,
        STANDARD_END_DATE, TypeOfStatus.CURRENT, false);

    beginTransaction();

    iSRelease = informationSystemReleaseService.loadObjectById(iSRelease.getId());
    ie = infrastructureElementService.loadObjectById(ie.getId());

    release.addInformationSystemRelease(iSRelease);
    Tcr2IeAssociation assoc = BuildingBlockFactory.createTcr2IeAssociation(release, ie);
    assoc.connect();

    tcrService.saveOrUpdate(release);

    commit();

    Integer id = release.getId();

    beginTransaction();
    // check the stored TCR
    release = tcrService.loadObjectById(id);
    assertNotNull(release);
    assertEquals(TEST_TC_NAME + " # " + VERSION_1_1, release.getName());
    assertEquals(VERSION_1_1, release.getVersion());
    // check the corresponding ISR
    Set<InformationSystemRelease> expISRSet = release.getInformationSystemReleases();
    assertNotNull(expISRSet);
    assertEquals(1, expISRSet.size());
    assertEquals("System # " + VERSION_1_0, expISRSet.iterator().next().getName());
    assertEquals(VERSION_1_0, expISRSet.iterator().next().getVersion());
    // check the corresponding IE
    Set<InfrastructureElement> expIESet = release.getInfrastructureElements();
    assertNotNull(expIESet);
    assertEquals(1, expIESet.size());
    assertEquals("Infra-A", expIESet.iterator().next().getName());
    assertEquals(TEST_DESCRIPTION, expIESet.iterator().next().getDescription());

    // The information system release has got an association to the current TCR.
    iSRelease = informationSystemReleaseService.loadObjectByIdIfExists(iSRelease.getId());
    assertNotNull(iSRelease);
    Set<TechnicalComponentRelease> expTCRSet = iSRelease.getTechnicalComponentReleases();
    assertNotNull(expTCRSet);
    assertEquals(1, expTCRSet.size());
    assertEquals(TEST_TC_NAME + " # " + VERSION_1_1, expTCRSet.iterator().next().getName());
    assertEquals(VERSION_1_1, expTCRSet.iterator().next().getVersion());

    // The infrastructure element has got got an association to the current TCR.
    ie = infrastructureElementService.loadObjectByIdIfExists(ie.getId());
    assertNotNull(ie);
    Set<TechnicalComponentRelease> expTCRSet2 = ie.getTechnicalComponentReleases();
    assertNotNull(expTCRSet2);
    assertEquals(1, expTCRSet2.size());
    assertEquals(TEST_TC_NAME + " # " + VERSION_1_1, expTCRSet2.iterator().next().getName());
    assertEquals(VERSION_1_1, expTCRSet2.iterator().next().getVersion());

    commit();
  }

  @Test
  public void testSaveEntityNotAVailableForInterfaces() {

    // set up test data
    InformationSystem informationSystem = testDataHelper.createInformationSystem("System");
    InformationSystemRelease iSRelease = testDataHelper.createInformationSystemRelease(informationSystem, VERSION_1_0);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement("Infra-A", TEST_DESCRIPTION);
    commit();

    TechnicalComponent catalogItem = testDataHelper.createTechnicalComponent(TEST_TC_NAME, false, false);

    TechnicalComponentRelease release = testDataHelper.createTCRelease(catalogItem, VERSION_1_1, TEST_DESCRIPTION, STANDARD_START_DATE,
        STANDARD_END_DATE, TypeOfStatus.CURRENT, false);

    beginTransaction();

    iSRelease = informationSystemReleaseService.loadObjectById(iSRelease.getId());
    ie = infrastructureElementService.loadObjectById(ie.getId());

    release.addInformationSystemRelease(iSRelease);
    Tcr2IeAssociation assoc = BuildingBlockFactory.createTcr2IeAssociation(release, ie);
    assoc.connect();

    tcrService.saveOrUpdate(release);

    commit();

    Integer id = release.getId();

    beginTransaction();
    // check the stored TCR
    release = tcrService.loadObjectById(id);
    assertNotNull(release);
    assertEquals(TEST_TC_NAME + " # " + VERSION_1_1, release.getName());
    assertEquals(VERSION_1_1, release.getVersion());
    // check the corresponding ISR
    Set<InformationSystemRelease> expISRSet = release.getInformationSystemReleases();
    assertNotNull(expISRSet);
    assertEquals(1, expISRSet.size());
    assertEquals("System # " + VERSION_1_0, expISRSet.iterator().next().getName());
    assertEquals(VERSION_1_0, expISRSet.iterator().next().getVersion());
    // check the corresponding IE
    Set<InfrastructureElement> expIESet = release.getInfrastructureElements();
    assertNotNull(expIESet);
    assertEquals(1, expIESet.size());
    assertEquals("Infra-A", expIESet.iterator().next().getName());
    assertEquals(TEST_DESCRIPTION, expIESet.iterator().next().getDescription());

    // The information system release has got an association to the current TCR.
    iSRelease = informationSystemReleaseService.loadObjectByIdIfExists(iSRelease.getId());
    assertNotNull(iSRelease);
    Set<TechnicalComponentRelease> expTCRSet = iSRelease.getTechnicalComponentReleases();
    assertNotNull(expTCRSet);
    assertEquals(1, expTCRSet.size());
    assertEquals(TEST_TC_NAME + " # " + VERSION_1_1, expTCRSet.iterator().next().getName());
    assertEquals(VERSION_1_1, expTCRSet.iterator().next().getVersion());

    // The infrastructure element has got got an association to the current TCR.
    ie = infrastructureElementService.loadObjectByIdIfExists(ie.getId());
    assertNotNull(ie);
    Set<TechnicalComponentRelease> expTCRSet2 = ie.getTechnicalComponentReleases();
    assertNotNull(expTCRSet2);
    assertEquals(1, expTCRSet2.size());
    assertEquals(TEST_TC_NAME + " # " + VERSION_1_1, expTCRSet2.iterator().next().getName());
    assertEquals(VERSION_1_1, expTCRSet2.iterator().next().getVersion());

    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseServiceImpl#deleteEntity(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * The method tests if the deleteEntity() throws correctly an IteraplanBusinessException.
   */
  @Test
  public void testDeleteEntityCaseIteraplanBusinessException() throws Exception {

    try {
      TechnicalComponent testTC = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
      TechnicalComponentRelease testTCR = testDataHelper.createTCRelease(testTC, VERSION_1_0, TEST_DESCRIPTION, STANDARD_START_DATE,
          STANDARD_END_DATE, TypeOfStatus.CURRENT, true);

      InformationSystem testIS = testDataHelper.createInformationSystem("testIS");

      InformationSystemRelease testIsrA = testDataHelper.createInformationSystemRelease(testIS, VERSION_1_0);
      InformationSystemRelease testIsrB = testDataHelper.createInformationSystemRelease(testIS, "2.0");

      testDataHelper.createInformationSystemInterface(testIsrA, testIsrB, testTCR, TEST_DESCRIPTION);
      commit();

      beginTransaction();
      testTC.setAvailableForInterfaces(true);
      technicalComponentService.saveOrUpdate(testTC);
      commit();

      beginTransaction();
      tcrService.deleteEntity(testTCR);
      fail();

    } catch (IteraplanBusinessException e) {
      // do noting, it's OK
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseServiceImpl#doesDuplicateReleaseExist(java.lang.String, java.lang.String)}
   * The method tests if the doesReleaseExist() returns correct value if the Release exists.
   */
  @Test
  public void testDoesReleaseExistCaseTrue() {
    // create Data
    TechnicalComponent testTC = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
    testDataHelper.createTCRelease(testTC, VERSION_1_0, true);
    commit();

    beginTransaction();
    TechnicalComponentRelease duplicateTcr = testDataHelper.createTCRelease(testTC, VERSION_1_0, false);

    assertTrue(tcrService.doesDuplicateReleaseExist(duplicateTcr));
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseServiceImpl#doesDuplicateReleaseExist(java.lang.String, java.lang.String)}
   * The method tests if the doesReleaseExist() returns correct value if the Release does not
   * exists.
   */
  @Test
  public void testDoesReleaseExistCaseFalse() {
    TechnicalComponent testTC = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
    TechnicalComponentRelease tcr1 = testDataHelper.createTCRelease(testTC, VERSION_1_0, true);
    TechnicalComponentRelease tcr2 = testDataHelper.createTCRelease(testTC, VERSION_1_1, false);

    assertFalse(tcrService.doesDuplicateReleaseExist(tcr1));
    assertFalse(tcrService.doesDuplicateReleaseExist(tcr2));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseServiceImpl#eligibleForConnections(java.util.List, java.lang.Boolean)}
   * The method tests if the eligibleForConnections() returns ... exist.
   */
  @Test
  public void testEligibleForConnections() {
    // create Data
    InformationSystem firstTestIS = testDataHelper.createInformationSystem("firstTestIS");
    InformationSystemRelease firstTestISR = testDataHelper.createInformationSystemRelease(firstTestIS, VERSION_1_0);

    InformationSystem secondTestIS = testDataHelper.createInformationSystem("secondTestIS");
    InformationSystemRelease secondTestISR = testDataHelper.createInformationSystemRelease(secondTestIS, "10.0");

    TechnicalComponent testTC = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
    TechnicalComponentRelease firstTestTCR = testDataHelper.createTCRelease(testTC, VERSION_1_0, true);
    TechnicalComponentRelease secondTestTCR = testDataHelper.createTCRelease(testTC, "2.0", true);
    TechnicalComponentRelease thirdTestTCR = testDataHelper.createTCRelease(testTC, "3.0", true);
    TechnicalComponentRelease fourthTestTCR = testDataHelper.createTCRelease(testTC, "4.0", true);
    TechnicalComponentRelease fifthTestTCR = testDataHelper.createTCRelease(testTC, "5.0", true);
    commit();

    beginTransaction();
    firstTestISR.addTechnicalComponentRelease(firstTestTCR);
    firstTestISR.addTechnicalComponentRelease(secondTestTCR);
    firstTestISR.addTechnicalComponentRelease(thirdTestTCR);
    firstTestISR.addTechnicalComponentRelease(fourthTestTCR);
    firstTestISR.addTechnicalComponentRelease(fifthTestTCR);

    secondTestISR.addTechnicalComponentRelease(firstTestTCR);
    secondTestISR.addTechnicalComponentRelease(secondTestTCR);
    secondTestISR.addTechnicalComponentRelease(thirdTestTCR);
    secondTestISR.addTechnicalComponentRelease(fourthTestTCR);
    secondTestISR.addTechnicalComponentRelease(fifthTestTCR);

    informationSystemReleaseService.saveOrUpdate(firstTestISR);
    informationSystemReleaseService.saveOrUpdate(secondTestISR);
    commit();

    beginTransaction();
    List<TechnicalComponentRelease> expected = arrayList();
    expected.add(firstTestTCR);
    expected.add(secondTestTCR);
    expected.add(fifthTestTCR);

    List<TechnicalComponentRelease> toExclude = arrayList();
    toExclude.add(thirdTestTCR);
    toExclude.add(fourthTestTCR);

    List<TechnicalComponentRelease> actual = tcrService.eligibleForConnections(toExclude, false);

    assertEquals(expected, actual);
    commit();
  }

  @Test
  public void testDeleteById() {
    TechnicalComponent catalogItem = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
    TechnicalComponentRelease release = testDataHelper.createTCRelease(catalogItem, VERSION_1_1, TEST_DESCRIPTION, STANDARD_START_DATE,
        STANDARD_END_DATE, TypeOfStatus.CURRENT, true);
    InformationSystem informationSystem = testDataHelper.createInformationSystem("System");
    InformationSystemRelease iSRelease = testDataHelper.createInformationSystemRelease(informationSystem, VERSION_1_0);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement("Infra-A", "-");
    commit();

    beginTransaction();
    technicalComponentService.saveOrUpdate(catalogItem);
    tcrService.saveOrUpdate(release);
    testDataHelper.addTcrToIsr(iSRelease, release);
    testDataHelper.addTcrToIe(ie, release);
    commit();

    // begin new transaction
    beginTransaction();
    Integer id = release.getId();
    release = tcrService.loadObjectById(id);
    tcrService.deleteEntity(release);
    commit();

    // ASSERT.
    beginTransaction();
    try {
      release = tcrService.loadObjectById(id);
      fail("A technical component release has not been deleted.");
    } catch (IteraplanTechnicalException e) {
      // do nothing it is OK
    }

    // The information system release has got no more associated configuration items.
    iSRelease = informationSystemReleaseService.loadObjectById(iSRelease.getId());
    assertEquals(0, iSRelease.getTechnicalComponentReleases().size());

    // The infrastructure element has got no more associated configuration items.
    ie = infrastructureElementService.loadObjectById(ie.getId());
    assertEquals(0, ie.getTechnicalComponentReleases().size());

    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseServiceImpl#filter(java.util.List, java.lang.Boolean)}
   */
  @Test
  public void testFilter() {
    // create test data and save to database
    TechnicalComponent x = testDataHelper.createTechnicalComponent("X", true, true);
    TechnicalComponentRelease x1 = testDataHelper.createTCRelease(x, "x1", true); // X # x1
    TechnicalComponentRelease x2 = testDataHelper.createTCRelease(x, "x2", true); // X # x2

    TechnicalComponent y = testDataHelper.createTechnicalComponent("Y", true, true);
    TechnicalComponentRelease y1 = testDataHelper.createTCRelease(y, "y1", true); // Y # y1
    TechnicalComponentRelease y2 = testDataHelper.createTCRelease(y, "y2", true); // Y # y2
    commit();

    // begin new transaction
    beginTransaction();

    // filter for "x"
    List<TechnicalComponentRelease> connected = new ArrayList<TechnicalComponentRelease>();
    connected.add(x2);
    connected.add(y2);
    List<TechnicalComponentRelease> list = tcrService.filter(connected, true);
    assertTrue(list.get(0).getReleaseName().equals(x1.getReleaseName()));
    assertEquals(2, list.size());

    // no filter string
    list = tcrService.filter(connected, true);
    assertTrue(list.get(0).getReleaseName().equals(x1.getReleaseName()));
    assertTrue(list.get(1).getReleaseName().equals(y1.getReleaseName()));
    assertEquals(2, list.size());

    // end transaction
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseServiceImpl#filter(java.util.List, java.lang.Boolean)}
   */
  @Test
  public void testFilterCaseIdNotNull() {
    // create test data and save to database
    TechnicalComponent x = testDataHelper.createTechnicalComponent("X", true, true);
    TechnicalComponentRelease x1 = testDataHelper.createTCRelease(x, "x1", true); // X # x1
    TechnicalComponentRelease x2 = testDataHelper.createTCRelease(x, "x2", true); // X # x2
    commit();

    beginTransaction();

    // filter for "x"
    List<TechnicalComponentRelease> connected = new ArrayList<TechnicalComponentRelease>();
    connected.add(x2);

    List<TechnicalComponentRelease> list = tcrService.filter(connected, true);
    assertTrue(list.get(0).getReleaseName().equals(x1.getReleaseName()));
    assertEquals(1, list.size());

    // no filter string
    list = tcrService.filter(connected, true);
    assertTrue(list.get(0).getReleaseName().equals(x1.getReleaseName()));
    assertEquals(1, list.size());

    // end transaction
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseServiceImpl#isDuplicateTechnicalComponent(java.lang.String, java.lang.Integer)}
   * The method tests if the isDuplicateTechnicalComponent() returns correct boolean value if the
   * TechnicalComponentRelease is duplicated or not.
   */
  @Test
  public void testIsDuplicateTechnicalComponentCaseFalse() {
    // create data
    TechnicalComponent catalogItem = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
    commit();

    beginTransaction();
    Integer id = catalogItem.getId();
    boolean actual = tcrService.isDuplicateTechnicalComponent(TEST_TC_NAME, id);
    commit();

    assertFalse(actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseServiceImpl#isDuplicateTechnicalComponent(java.lang.String, java.lang.Integer)}
   * The method tests if the isDuplicateTechnicalComponent() returns correct boolean value if the
   * TechnicalComponentRelease is duplicated or not. In this case the method should returns true,
   * because the same element is already in the data base. But the second element could not be
   * written in the data base and we should expect an exception.
   */
  @Test
  public void testIsDuplicateTechnicalComponentCaseTrue() {
    // create data
    try {
      testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
      TechnicalComponent catalogItem = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
      commit();

      beginTransaction();
      Integer id = catalogItem.getId();
      tcrService.isDuplicateTechnicalComponent(TEST_TC_NAME, id);
    } catch (DataIntegrityViolationException e) {
      // do nothing it is OK
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseServiceImpl#validBaseComponents(java.lang.Integer, java.utilList, java.lang.Boolean)}
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseServiceImpl#gatherAllUsedByReleases(de.iteratec.iteraplan.model.TechnicalComponentRelease, java.util.List)}
   * The method tests if the validBaseComponents() returns correct list with all the BaseComponents.
   */
  @Test
  public void testValidBaseComponents() {
    // create data
    TechnicalComponent catalogItem = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
    TechnicalComponentRelease release = testDataHelper.createTCRelease(catalogItem, VERSION_1_0, TEST_DESCRIPTION, STANDARD_START_DATE,
        STANDARD_END_DATE, TypeOfStatus.CURRENT, true);

    TechnicalComponentRelease firstComponent = testDataHelper.createTCRelease(catalogItem, VERSION_1_1, TEST_DESCRIPTION, STANDARD_START_DATE,
        STANDARD_END_DATE, TypeOfStatus.CURRENT, true);

    TechnicalComponentRelease secondComponent = testDataHelper.createTCRelease(catalogItem, "1.2", TEST_DESCRIPTION, "01.01.2005", "10.10.2010",
        TypeOfStatus.INACTIVE, true);

    TechnicalComponentRelease thirdComponent = testDataHelper.createTCRelease(catalogItem, "1.3", TEST_DESCRIPTION, "01.05.2013", "10.10.2015",
        TypeOfStatus.CURRENT, true);

    TechnicalComponentRelease fourthComponent = testDataHelper.createTCRelease(catalogItem, "1.4", TEST_DESCRIPTION, STANDARD_START_DATE,
        "10.10.2008", TypeOfStatus.CURRENT, true);

    TechnicalComponentRelease fifthComponent = testDataHelper.createTCRelease(catalogItem, "1.5", TEST_DESCRIPTION, "12.12.2011", "10.10.2045",
        TypeOfStatus.PLANNED, true);

    TechnicalComponentRelease firstChild = testDataHelper.createTCRelease(catalogItem, "1.1.1", true);
    TechnicalComponentRelease secondChild = testDataHelper.createTCRelease(catalogItem, "1.1.2", true);
    TechnicalComponentRelease thirdChild = testDataHelper.createTCRelease(catalogItem, "1.1.3", true);
    commit();

    // create relations
    beginTransaction();
    release.addBaseComponent(firstComponent);
    release.addBaseComponent(secondComponent);
    release.addBaseComponent(thirdComponent);
    release.addBaseComponent(fourthComponent);
    release.addBaseComponent(fifthComponent);

    firstChild.addParentComponent(firstComponent);
    secondChild.addParentComponent(firstChild);
    thirdChild.addParentComponent(secondChild);
    tcrService.saveOrUpdate(release);
    commit();

    // create list to exclude
    List<TechnicalComponentRelease> toExclude = arrayList();
    toExclude.add(thirdComponent);
    toExclude.add(fifthComponent);

    beginTransaction();
    Integer id = firstComponent.getId();
    List<TechnicalComponentRelease> actual = tcrService.validBaseComponents(id, toExclude, true);

    List<TechnicalComponentRelease> expected = arrayList();
    expected.add(firstChild);
    expected.add(secondChild);
    expected.add(thirdChild);
    expected.add(secondComponent);
    expected.add(fourthComponent);

    assertEquals(expected, actual);
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseServiceImpl#validPredecessors(java.lang.Integer, java.utilList, java.lang.Boolean)}
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseServiceImpl#gatherAllSuccessors(de.iteratec.iteraplan.model.TechnicalComponentRelease, java.util.List)}
   * The method tests if the validPredecessors() returns correct list with all the Predecessors.
   */
  @Test
  public void testValidPredecessors() {
    // create data
    TechnicalComponent catalogItem = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
    TechnicalComponentRelease release = testDataHelper.createTCRelease(catalogItem, VERSION_1_0, TEST_DESCRIPTION, STANDARD_START_DATE,
        STANDARD_END_DATE, TypeOfStatus.CURRENT, true);

    TechnicalComponentRelease firstPredeccessor = testDataHelper.createTCRelease(catalogItem, VERSION_1_1, TEST_DESCRIPTION, STANDARD_START_DATE,
        STANDARD_END_DATE, TypeOfStatus.CURRENT, true);

    TechnicalComponentRelease secondPredeccessor = testDataHelper.createTCRelease(catalogItem, "1.2", TEST_DESCRIPTION, "01.01.2005", "10.10.2010",
        TypeOfStatus.INACTIVE, true);

    TechnicalComponentRelease thirdPredeccessor = testDataHelper.createTCRelease(catalogItem, "1.3", TEST_DESCRIPTION, "01.05.2013", "10.10.2015",
        TypeOfStatus.CURRENT, true);

    TechnicalComponentRelease fourthPredeccessor = testDataHelper.createTCRelease(catalogItem, "1.4", TEST_DESCRIPTION, STANDARD_START_DATE,
        "10.10.2008", TypeOfStatus.CURRENT, true);

    TechnicalComponentRelease fifthPredeccessor = testDataHelper.createTCRelease(catalogItem, "1.5", TEST_DESCRIPTION, "12.12.2011", "10.10.2045",
        TypeOfStatus.PLANNED, true);

    TechnicalComponentRelease firstSucessor = testDataHelper.createTCRelease(catalogItem, "1.1.1", true);
    TechnicalComponentRelease secondSucessor = testDataHelper.createTCRelease(catalogItem, "1.1.2", true);
    TechnicalComponentRelease thirdSucessor = testDataHelper.createTCRelease(catalogItem, "1.1.3", true);
    commit();

    // create relations
    beginTransaction();
    release.addPredecessor(firstPredeccessor);
    release.addPredecessor(secondPredeccessor);
    release.addPredecessor(thirdPredeccessor);
    release.addPredecessor(fourthPredeccessor);
    release.addPredecessor(fifthPredeccessor);

    firstPredeccessor.addSuccessor(firstSucessor);
    firstPredeccessor.addSuccessor(secondSucessor);
    firstPredeccessor.addSuccessor(thirdSucessor);
    tcrService.saveOrUpdate(release);

    List<TechnicalComponentRelease> toExclude = arrayList();
    toExclude.add(thirdPredeccessor);
    toExclude.add(fifthPredeccessor);

    Integer id = firstPredeccessor.getId();
    commit();

    // create list to exclude
    beginTransaction();
    List<TechnicalComponentRelease> actual = tcrService.validPredecessors(id, toExclude, true);

    List<TechnicalComponentRelease> expected = arrayList();
    expected.add(firstSucessor);
    expected.add(secondSucessor);
    expected.add(thirdSucessor);
    expected.add(secondPredeccessor);
    expected.add(fourthPredeccessor);

    assertEquals(expected, actual);
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseServiceImpl#getTechnicalComponentReleasesBySearch(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   * The method tests if the getTechnicalComponentReleaseBySearch() returns correct list with all
   * the TechnicalComponentReleases in association with given TechnicalComponent.
   */
  @Test
  public void testGetTechnicalComponentReleaseBySearch() {
    // create data
    TechnicalComponent testTC = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
    TechnicalComponentRelease testTCR = testDataHelper.createTCRelease(testTC, "Beta", true);
    commit();

    beginTransaction();
    // get Unit by search
    // search for TechnicalComponentRelease
    List<TechnicalComponentRelease> actualList = tcrService.getTechnicalComponentReleasesBySearch(testTC, true);
    commit();

    beginTransaction();
    List<TechnicalComponentRelease> expected = arrayList();
    expected.add(testTCR);

    assertEquals(expected.size(), actualList.size());
    assertEquals(expected, actualList);
    commit();
  }

  /**
   * Tests the method {@link InformationSystemReleaseService#findByNames(Set)}
   */
  @Test
  public void testFindByNames() {
    TechnicalComponent is = testDataHelper.createTechnicalComponent("technicalComponent", true, true);
    testDataHelper.createTCRelease(is, null, true);
    TechnicalComponentRelease release2 = testDataHelper.createTCRelease(is, VERSION_1_0, true);
    TechnicalComponentRelease release3 = testDataHelper.createTCRelease(is, VERSION_1_1, true);
    commit();
    beginTransaction();

    List<TechnicalComponentRelease> foundReleases = tcrService.findByNames(Sets.newHashSet("technicalComponent  #  1.0", "technicalComponent # 1.1",
        "technicalComponent # 1.3"));
    assertEquals(2, foundReleases.size());
    assertEquals(Lists.newArrayList(release2, release3), foundReleases);
  }

  /**
   * Tests the method {@link technicalComponentReleaseService#findByNames(Set)}
   */
  @Test
  public void testFindByNamesWhenVersionNull() {
    TechnicalComponent is = testDataHelper.createTechnicalComponent("technicalComponent", true, true);
    TechnicalComponentRelease release1 = testDataHelper.createTCRelease(is, null, true);
    testDataHelper.createTCRelease(is, VERSION_1_0, true);
    testDataHelper.createTCRelease(is, VERSION_1_1, true);
    commit();
    beginTransaction();

    List<TechnicalComponentRelease> foundReleases = tcrService.findByNames(Sets.newHashSet("technicalComponent"));
    assertEquals(1, foundReleases.size());
    assertEquals(release1, foundReleases.get(0));
  }

  @Test
  public void testSaveOrUpdateTCRCreatePermission() {

    User user = new User();
    user.setDataSource("MASTER");
    user.setLoginName("user");

    Role role = new Role();
    role.setRoleName("testRole");

    Set<Role> roles = Sets.newHashSet();
    roles.add(role);

    Role2BbtPermission role2TC = new Role2BbtPermission();
    role2TC.setBbt(new BuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE));
    role2TC.setType(EditPermissionType.CREATE);

    Set<Role2BbtPermission> roles2Bbt = Sets.newHashSet();
    roles2Bbt.add(role2TC);

    Set<PermissionFunctional> funcPermissions = Sets.newHashSet();
    PermissionFunctional funcPermission = new PermissionFunctional();
    funcPermission.setTypeOfFunctionalPermission(TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES);
    funcPermissions.add(funcPermission);

    role.setPermissionsBbt(roles2Bbt);
    role.setPermissionsFunctional(funcPermissions);

    user.addRoleTwoWay(role);

    UserContext userContext = new UserContext("user", roles, Locale.UK, user);
    UserContext.setCurrentUserContext(userContext);

    commit();
    beginTransaction();

    TechnicalComponent catalogItem = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);

    TechnicalComponentRelease tcr = BuildingBlockFactory.createTechnicalComponentRelease();

    tcr.setTechnicalComponent(catalogItem);

    tcrService.saveOrUpdate(tcr);

  }
}
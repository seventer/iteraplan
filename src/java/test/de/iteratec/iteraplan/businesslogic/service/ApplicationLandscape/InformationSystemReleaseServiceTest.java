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
package de.iteratec.iteraplan.businesslogic.service.ApplicationLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO;


/**
 * Tests the service methods of the {@link InformationSystemReleaseService} interface.
 * 
 * @author mma
 */
public class InformationSystemReleaseServiceTest extends BaseTransactionalTestSupport {
  private static final String             TEST_IS_NAME = "testIS";
  private static final String             VERSION_1_0  = "1.0";
  private static final String             VERSION_2_0  = "2.0";
  private static final String             VERSION_3_0  = "3.0";
  private static final String             VERSION_4_0  = "4.0";
  private static final String             VERSION_5_0  = "5.0";

  @Autowired
  private InformationSystemReleaseService isrService;
  @Autowired
  private InformationSystemReleaseDAO     informationSystemReleaseDAO;
  @Autowired
  private TestDataHelper2                 testDataHelper;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#deleteEntity(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#getOutermostInformationSystemReleases()}
   * The method tests if deleteEntity() deletes correctly an InformationSystemRelease from the database.
   */
  @Test
  public void testDeleteEntity() {
    // Create the data.
    testDataHelper.createInformationSystem("firstTestSystem");
    InformationSystem secondTestIS = testDataHelper.createInformationSystem("secondTestSystem");
    testDataHelper.createInformationSystemRelease(secondTestIS, VERSION_1_0);
    InformationSystemRelease releaseB = testDataHelper.createInformationSystemRelease(secondTestIS, VERSION_2_0);
    InformationSystemRelease releaseC = testDataHelper.createInformationSystemRelease(secondTestIS, VERSION_3_0);
    commit();

    // Create the associations.
    beginTransaction();
    List<InformationSystemRelease> releases = isrService.getOutermostInformationSystemReleases();
    isrService.deleteEntity(releases.get(0));

    List<InformationSystemRelease> expected = arrayList();
    expected.add(releaseB);
    expected.add(releaseC);

    List<InformationSystemRelease> actual = isrService.getOutermostInformationSystemReleases();

    assertEquals(expected, actual);
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#doesDuplicateReleaseExist(de.iteratec.iteraplan.model.Release)}
   * The method tests if doesDuplicateReleaseExist() returns correct values if release exists or it does not.
   */
  @Test
  public void testDoesDuplicateReleaseExist() {
    // Create the data.
    InformationSystem testIS = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(testIS, VERSION_1_0);
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(testIS, VERSION_2_0);

    // isr3 will not be persisted
    InformationSystemRelease isr3 = BuildingBlockFactory.createInformationSystemRelease();
    isr3.setInformationSystem(testIS);
    isr3.setVersion(VERSION_3_0);
    // isr4 will not be persisted
    InformationSystemRelease isr4 = BuildingBlockFactory.createInformationSystemRelease();
    isr4.setInformationSystem(testIS);
    isr4.setVersion(VERSION_2_0);
    commit();

    beginTransaction();
    assertFalse(isrService.doesDuplicateReleaseExist(isr1)); // recognizes the release as itself due to ID => no duplicate
    assertFalse(isrService.doesDuplicateReleaseExist(isr2)); // recognizes the release as itself due to ID => no duplicate
    assertFalse(isrService.doesDuplicateReleaseExist(isr3));
    assertTrue(isrService.doesDuplicateReleaseExist(isr4)); // same IS and version as isr2, but different/no ID => duplicate
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#getAvailableParentReleases(java.lang.Integer, java.lang.Boolean)}
   * The method tests if getAvailableParentReleases() returns correct sorted list with all parent
   * InformationSystemReleases.
   */
  @Test
  public void testGetAvailableParentReleases() {
    // Create the data.
    InformationSystem testIS = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease releaseA = testDataHelper.createInformationSystemRelease(testIS, VERSION_1_0);
    InformationSystemRelease releaseB = testDataHelper.createInformationSystemRelease(testIS, VERSION_2_0);
    InformationSystemRelease releaseC = testDataHelper.createInformationSystemRelease(testIS, VERSION_3_0);
    InformationSystemRelease releaseD = testDataHelper.createInformationSystemRelease(testIS, VERSION_4_0);
    InformationSystemRelease releaseE = testDataHelper.createInformationSystemRelease(testIS, VERSION_5_0);
    commit();

    // create associations
    beginTransaction();
    releaseE.addParent(releaseD);
    releaseD.addParent(releaseC);
    releaseC.addParent(releaseB);
    releaseB.addParent(releaseA);

    informationSystemReleaseDAO.saveOrUpdate(releaseA);
    informationSystemReleaseDAO.saveOrUpdate(releaseB);
    informationSystemReleaseDAO.saveOrUpdate(releaseC);
    informationSystemReleaseDAO.saveOrUpdate(releaseD);
    informationSystemReleaseDAO.saveOrUpdate(releaseE);

    List<InformationSystemRelease> expected = arrayList();
    expected.add(releaseC);
    expected.add(releaseD);
    expected.add(releaseA);
    expected.add(releaseB);

    Collections.sort(expected);

    Integer id = releaseE.getId();
    List<InformationSystemRelease> actual = isrService.getAvailableParentReleases(id, true);
    assertEquals(expected, actual);
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#getInformationSystemsFiltered(java.util.List, java.lang.Boolean)}
   * The method tests if getAvailableParentReleases() returns correct filtered list with all parent
   * InformationSystemReleases.
   */
  @Test
  public void testGetInformationSystemsFiltered() {
    // Create the data.
    InformationSystem testIS = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease testReleaseA = testDataHelper.createInformationSystemRelease(testIS, VERSION_1_0);
    InformationSystemRelease testReleaseB = testDataHelper.createInformationSystemRelease(testIS, VERSION_2_0);
    InformationSystemRelease testReleaseC = testDataHelper.createInformationSystemRelease(testIS, VERSION_3_0);
    InformationSystemRelease testReleaseD = testDataHelper.createInformationSystemRelease(testIS, VERSION_4_0);
    InformationSystemRelease testReleaseE = testDataHelper.createInformationSystemRelease(testIS, VERSION_5_0);
    commit();

    beginTransaction();

    List<InformationSystemRelease> expected = arrayList();
    expected.add(testReleaseA);
    expected.add(testReleaseC);
    expected.add(testReleaseD);
    expected.add(testReleaseE);

    Collections.sort(expected);

    List<InformationSystemRelease> elementsToExclude = arrayList();
    elementsToExclude.add(testReleaseB);

    List<InformationSystemRelease> actual = isrService.getInformationSystemsFiltered(elementsToExclude, true);
    assertEquals(expected, actual);
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#getValidPredecessors(java.lang.Integer, java.util.List, java.lang.Boolean)}
   * The method tests if getAvailableParentReleases() returns correct list with all Predecessors of
   * a InformationSystemReleases with a given id.
   */
  @Test
  public void testGetValidPredecessors() {
    // Create the data.
    InformationSystem testIS = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease testPredecessorA = testDataHelper.createInformationSystemRelease(testIS, VERSION_1_0);
    InformationSystemRelease testPredecessorB = testDataHelper.createInformationSystemRelease(testIS, VERSION_2_0);
    InformationSystemRelease testPredecessorC = testDataHelper.createInformationSystemRelease(testIS, VERSION_3_0);
    InformationSystemRelease testPredecessorD = testDataHelper.createInformationSystemRelease(testIS, VERSION_4_0);
    InformationSystemRelease testPredecessorE = testDataHelper.createInformationSystemRelease(testIS, VERSION_5_0);

    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(testIS, "last One");

    commit();

    // create associations
    beginTransaction();
    release.addPredecessor(testPredecessorA);
    release.addPredecessor(testPredecessorB);
    release.addPredecessor(testPredecessorC);
    release.addPredecessor(testPredecessorD);
    release.addPredecessor(testPredecessorE);

    informationSystemReleaseDAO.saveOrUpdate(release);

    List<InformationSystemRelease> expected = arrayList();
    expected.add(testPredecessorA);
    expected.add(testPredecessorC);
    expected.add(testPredecessorD);
    expected.add(testPredecessorE);

    Collections.sort(expected);

    Integer id = release.getId();
    List<InformationSystemRelease> elementsToExclude = arrayList();
    elementsToExclude.add(testPredecessorB);

    List<InformationSystemRelease> actual = isrService.getValidPredecessors(id, elementsToExclude, false);

    assertEquals(expected, actual);
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#isDuplicateInformationSystem(java.lang.String, java.lang.Integer)}
   * The method tests if doesDeletedReleaseExist() throws Exception if the use tries to add objects
   * in the data base with identical ids.
   */
  @Test
  public void testIsDuplicateInformationSystemCaseException() {
    // Create the data.
    try {
      testDataHelper.createInformationSystem(TEST_IS_NAME);
      InformationSystem secTestIS = testDataHelper.createInformationSystem(TEST_IS_NAME);
      commit();

      beginTransaction();
      Integer identifier = secTestIS.getId();
      isrService.isDuplicateInformationSystem(secTestIS.getName(), identifier);
    } catch (DataIntegrityViolationException e) {
      // do noting, it's ok
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#isDuplicateInformationSystem(java.lang.String, java.lang.Integer)}
   * The method tests if doesDeletedReleaseExist() returns correct values if a informationSystem is
   * duplicated or not.
   */
  @Test
  public void testIsDuplicateInformationSystemCaseFalse() {
    // Create the data.
    testDataHelper.createInformationSystem(TEST_IS_NAME);
    commit();

    beginTransaction();
    assertFalse(isrService.isDuplicateInformationSystem("testISFalse", Integer.valueOf(565)));
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#getAvailableChildren(java.lang.Integer, java.util.List, java.lang.Boolean)}
   * The method tests if getAvailableChildren() returns correct list with all the available children
   * of a given InformationSystemRelease.
   */
  @Test
  public void testGetAvailableChildrenCaseIdNotNull() {
    // Create the data.
    InformationSystem testIS = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(testIS, "testISR");

    InformationSystemRelease parent = testDataHelper.createInformationSystemRelease(testIS, "parent");

    InformationSystemRelease firstChild = testDataHelper.createInformationSystemRelease(testIS, "firstChild");

    InformationSystemRelease secondChild = testDataHelper.createInformationSystemRelease(testIS, "secondChild");

    InformationSystemRelease thirdChild = testDataHelper.createInformationSystemRelease(testIS, "thirdChild");

    InformationSystemRelease fourthChild = testDataHelper.createInformationSystemRelease(testIS, "fourthChild");

    InformationSystemRelease fifthChild = testDataHelper.createInformationSystemRelease(testIS, "fifthChild");
    commit();

    // create associations
    beginTransaction();
    release.addParent(parent);
    release.addChild(firstChild);
    release.addChild(secondChild);
    release.addChild(thirdChild);
    release.addChild(fourthChild);
    release.addChild(fifthChild);

    informationSystemReleaseDAO.saveOrUpdate(release);

    Integer id = release.getId();
    List<InformationSystemRelease> expected = arrayList();
    expected.add(firstChild);
    expected.add(secondChild);
    expected.add(thirdChild);

    List<InformationSystemRelease> elementsToExclude = arrayList();
    elementsToExclude.add(fourthChild);
    elementsToExclude.add(fifthChild);

    List<InformationSystemRelease> actual = isrService.getAvailableChildren(id, elementsToExclude, true);
    assertEquals(expected, actual);
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#getAvailableChildren(java.lang.Integer, java.util.List, java.lang.Boolean)}
   * The method tests if getAvailableChildren() returns correct list with all the available children
   * of a given InformationSystemRelease.
   */
  @Test
  public void testGetAvailableChildrenCaseIdNull() {
    // Create the data.
    InformationSystem testIS = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(testIS, "testISR");

    InformationSystemRelease firstChild = testDataHelper.createInformationSystemRelease(testIS, "firstChild");

    InformationSystemRelease secondChild = testDataHelper.createInformationSystemRelease(testIS, "secondChild");

    InformationSystemRelease thirdChild = testDataHelper.createInformationSystemRelease(testIS, "thirdChild");

    InformationSystemRelease fourthChild = testDataHelper.createInformationSystemRelease(testIS, "fourthChild");

    InformationSystemRelease fifthChild = testDataHelper.createInformationSystemRelease(testIS, "fifthChild");
    commit();

    // create associations
    beginTransaction();
    release.addChild(firstChild);
    release.addChild(secondChild);
    release.addChild(thirdChild);
    release.addChild(fourthChild);
    release.addChild(fifthChild);

    informationSystemReleaseDAO.saveOrUpdate(release);

    List<InformationSystemRelease> expected = arrayList();
    expected.add(release);
    expected.add(firstChild);
    expected.add(secondChild);
    expected.add(thirdChild);

    List<InformationSystemRelease> elementsToExclude = arrayList();
    elementsToExclude.add(fourthChild);
    elementsToExclude.add(fifthChild);

    List<InformationSystemRelease> actual = isrService.getAvailableChildren(null, elementsToExclude, true);

    assertEquals(expected, actual);
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#getInformationSystemReleasesBySearch(de.iteratec.iteraplan.model.InformationSystemRelease, java.lang.Integer, java.lang.Integer, java.lang.Boolean)}
   * The method tests if the getInformationSystemReleasesBySearch() returns correct list with
   * InformationSystemReleases.
   */
  @Test
  public void testGetInformationSystemReleasesBySearch() {
    // create data
    InformationSystem testIS = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(testIS, VERSION_1_0);
    commit();

    beginTransaction();

    // get Unit by search
    // search for UnitA
    List<InformationSystemRelease> actualList = isrService.getInformationSystemReleasesBySearch(testIS, true);
    commit();

    List<InformationSystemRelease> expected = arrayList();
    expected.add(release);

    assertEquals(expected.size(), actualList.size());
    assertEquals(expected, actualList);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#validateDuplicate(de.iteratec.iteraplan.model.InformationSystem, de.iteratec.iteraplan.model.InformationSystemRelease)}
   */
  @Test
  public void testValidateDuplicate() {
    // create data
    InformationSystem is = testDataHelper.createInformationSystem("is");
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(is, VERSION_4_0);
    commit();

    isrService.validateDuplicate(is, release);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#validateDuplicate(de.iteratec.iteraplan.model.InformationSystem, de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The test method tests if validateDuplicate() throws correctly a IteraplanBusinessException.
   * That test does not increase the code coverage, because the exception is thrown from the second
   * validate method.
   */
  @Test
  public void testValidateDuplicateFirstCaseIteraplanBusinessException() {
    try {
      // create data
      InformationSystem is = new InformationSystem();
      is.setId(Integer.valueOf(567));
      is.setName(null);

      InformationSystemRelease release = new InformationSystemRelease();
      release.setId(Integer.valueOf(56));
      release.setVersion(null);

      release.setInformationSystem(is);
      isrService.validateDuplicate(is, release);
    } catch (IteraplanBusinessException e) {
      // do noting it is OK
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#validateNewISR(de.iteratec.iteraplan.model.InformationSystem, de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The test method tests if validateDuplicate() throws correctly a IteraplanBusinessException.
   */
  @Test
  public void testValidateDuplicateThirdCaseIteraplanBusinessException() {
    try {
      // create data
      InformationSystem is = testDataHelper.createInformationSystem("is");
      InformationSystemRelease release = testDataHelper.createInformationSystemRelease(is, VERSION_4_0);
      commit();
      isrService.validateDuplicate(is, release);
    } catch (IteraplanBusinessException e) {
      // do nothing it's okay.
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#validateDuplicate(de.iteratec.iteraplan.model.InformationSystem, de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The test method tests if validateDuplicate() throws correctly a IteraplanBusinessException.
   * That test does not increase the code coverage, because the exception is thrown from the second
   * validate method.
   */
  @Test
  public void testValidateDuplicateSecondCaseIteraplanBusinessException() {
    try {
      // create data
      InformationSystem is = testDataHelper.createInformationSystem("");
      InformationSystemRelease release = testDataHelper.createInformationSystemRelease(is, "");
      commit();

      isrService.validateDuplicate(is, release);
    } catch (IteraplanBusinessException e) {
      // do noting it is OK
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#validateDuplicate(de.iteratec.iteraplan.model.InformationSystem, de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The test method tests if newISR() throws correctly a IteraplanBusinessException. That test does
   * not increase the code coverage, because the exception is thrown from the second validate
   * method.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateNewISRSecondCaseIteraplanBusinessException() {
    // create data
    InformationSystem is = new InformationSystem();
    is.setId(Integer.valueOf(567));
    is.setName(null);

    InformationSystemRelease release = new InformationSystemRelease();
    release.setId(Integer.valueOf(56));
    release.setVersion(null);

    release.setInformationSystem(is);
    isrService.validateDuplicate(is, release);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#validateDuplicate(de.iteratec.iteraplan.model.InformationSystem, de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The test method tests if newISR() throws correctly a IteraplanBusinessException. That test does
   * not increase the code coverage, because the exception is thrown from the second validate
   * method.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateNewISRFirstCaseIteraplanBusinessException() {
    // create data
    InformationSystem is = new InformationSystem();
    is.setId(Integer.valueOf(567));
    is.setName("");

    InformationSystemRelease release = new InformationSystemRelease();
    release.setId(Integer.valueOf(56));
    release.setVersion("");

    release.setInformationSystem(is);
    isrService.validateDuplicate(is, release);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#validateDuplicate(de.iteratec.iteraplan.model.InformationSystem, de.iteratec.iteraplan.model.InformationSystemRelease)}
   */
  @Test
  public void testValidateNewISR() {
    // create data
    InformationSystem is = new InformationSystem();
    is.setId(Integer.valueOf(567));
    is.setName("is");

    InformationSystemRelease release = new InformationSystemRelease();
    release.setId(Integer.valueOf(56));
    release.setVersion(VERSION_4_0);

    release.setInformationSystem(is);
    isrService.validateDuplicate(is, release);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#validateDuplicate(de.iteratec.iteraplan.model.InformationSystem, de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The test method tests if newISR() throws correctly a IteraplanBusinessException.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateNewISRThirdCaseIteraplanBusinessException() {
    // create data
    InformationSystem is = testDataHelper.createInformationSystem("is");
    testDataHelper.createInformationSystemRelease(is, VERSION_4_0);
    commit();

    InformationSystemRelease release2 = BuildingBlockFactory.createInformationSystemRelease();
    release2.setInformationSystem(is);
    release2.setVersion(VERSION_4_0);
    isrService.validateDuplicate(is, release2);
  }

  /**
   * Tests the method {@link InformationSystemReleaseService#findByNames(java.util.Set)}
   */
  @Test
  public void testFindByNames() {
    InformationSystem is = testDataHelper.createInformationSystem("informationSystem");
    testDataHelper.createInformationSystemRelease(is, null);
    InformationSystemRelease release2 = testDataHelper.createInformationSystemRelease(is, VERSION_2_0);
    InformationSystemRelease release3 = testDataHelper.createInformationSystemRelease(is, VERSION_3_0);
    commit();
    beginTransaction();

    List<InformationSystemRelease> foundReleases = isrService.findByNames(Sets.newHashSet("informationSystem  #  2.0", "informationSystem # 3.0",
        "informationSystem # 4.0"));
    assertEquals(2, foundReleases.size());
    assertEquals(Lists.newArrayList(release2, release3), foundReleases);
  }

  /**
   * Tests the method {@link InformationSystemReleaseService#findByNames(java.util.Set)}
   */
  @Test
  public void testFindByNamesWhenVersionNull() {
    InformationSystem is = testDataHelper.createInformationSystem("informationSystem");
    InformationSystemRelease release1 = testDataHelper.createInformationSystemRelease(is, null);
    testDataHelper.createInformationSystemRelease(is, VERSION_2_0);
    testDataHelper.createInformationSystemRelease(is, VERSION_3_0);
    commit();
    beginTransaction();

    List<InformationSystemRelease> foundReleases = isrService.findByNames(Sets.newHashSet("informationSystem"));
    assertEquals(1, foundReleases.size());
    assertEquals(release1, foundReleases.get(0));
  }

  @Test
  public void testRemoveBusinessMapping() {
    InformationSystemRelease isr = setupIsr();
    BusinessMapping bm = setupBusinessMapping("BM1", isr);
    commit();

    removeBusinessMapping(isr.getId(), bm.getId());

    // Now check that the business mapping no longer exists
    assertNull("Business mapping entry still exists in database.", getBusinessMappingFromDb(bm.getId()));
  }

  @Test
  public void testNotRemoveBusinessMapping() {
    InformationSystemRelease isr = setupIsr();
    BusinessMapping bm = setupBusinessMapping("BM1", isr);
    commit();

    removeBusinessMapping(isr.getId(), null);

    // Now check that the business mapping exists
    assertNotNull("Business mapping missing in database.", getBusinessMappingFromDb(bm.getId()));
  }

  @Test
  public void testRemoveOnlyOneBusinessMapping() {
    InformationSystemRelease isr = setupIsr();
    BusinessMapping bm = setupBusinessMapping("BM1", isr);
    commit();

    removeBusinessMapping(isr.getId(), bm.getId());

    assertNull("Business mapping entry still exists in database.", getBusinessMappingFromDb(bm.getId()));
  }

  private InformationSystemRelease setupIsr() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    return testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
  }

  private BusinessMapping setupBusinessMapping(String name, InformationSystemRelease isr) {
    BusinessProcess process = testDataHelper.createBusinessProcess(name + " - Process", "");
    BusinessUnit unit = testDataHelper.createBusinessUnit(name + " - Unit", "");
    Product product = testDataHelper.createProduct(name + " - Product", "");
    return testDataHelper.createBusinessMapping(isr, process, unit, product);
  }

  private void removeBusinessMapping(Integer isrId, Integer bmId) {
    beginTransaction();
    InformationSystemRelease isr = isrService.loadObjectById(isrId);

    if (bmId != null) {
      // Remove the business mapping and issue an update
      removeBusinessMapping2(bmId, isr.getBusinessMappings());
    }

    isrService.saveOrUpdate(isr);
    commit();
  }

  private BusinessMapping getBusinessMappingFromDb(final Integer id) {
    HibernateCallback<BusinessMapping> callback = new HibernateCallback<BusinessMapping>() {
      @SuppressWarnings("unchecked")
      public BusinessMapping doInHibernate(Session session) {
        List<BusinessMapping> mappings = session.createCriteria(BusinessMapping.class).add(Restrictions.eq("id", id)).list();
        return mappings.isEmpty() ? null : mappings.get(0);
      }
    };
    return getHibernateTemplate().execute(callback);
  }

  /**
   * Removes a single {@code BusinessMapping} by its id. Updates both sides of the association.
   * @throws NoSuchElementException
   */
  public void removeBusinessMapping2(Integer id, Set<BusinessMapping> businessMappings) {
    Iterator<BusinessMapping> it = businessMappings.iterator();
    BusinessMapping bm = null;
    while (it.hasNext()) {
      bm = it.next();
      if (bm.getId().equals(id)) {
        break;
      }
    }

    if (bm != null) {
      businessMappings.remove(bm);
      bm.setInformationSystemRelease(null);
    }
    else {
      throw new NoSuchElementException();
    }
  }
}

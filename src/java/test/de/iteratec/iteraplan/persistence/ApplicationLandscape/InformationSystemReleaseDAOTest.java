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
package de.iteratec.iteraplan.persistence.ApplicationLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO;


/**
 * @author mma
 */
public class InformationSystemReleaseDAOTest extends BaseTransactionalTestSupport {
  private static final String         TEST_IS_NAME_1 = "firstIS";
  private static final String         TEST_IS_NAME_2 = "secIS";
  private static final String         VERSION_1_0    = "1.0";
  private static final String         VERSION_2_0    = "2.0";
  private static final String         VERSION_3_0    = "3.0";
  
  @Autowired
  private InformationSystemReleaseDAO informationSystemReleaseDAO;
  @Autowired
  private TestDataHelper2  testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }
  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO#doesReleaseExist(java.lang.String, java.lang.String)}
   */
  @Test
  public void testDoesReleaseExistCaseExist() {
    // Create test data.
    InformationSystem is = testDataHelper.createInformationSystem("A");
    testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    testDataHelper.createInformationSystemRelease(is, VERSION_2_0);
    commit();

    beginTransaction();
    assertFalse(informationSystemReleaseDAO.doesReleaseExist("A", VERSION_3_0));
    assertFalse(informationSystemReleaseDAO.doesReleaseExist("B", VERSION_1_0));
    assertTrue(informationSystemReleaseDAO.doesReleaseExist("A", VERSION_1_0));
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO#doesReleaseExist(java.lang.String, java.lang.String)}
   * The method should throw an exception, because the name parameter is null.
   */@Test
  public void testDoesReleaseExistCaseException() {
    try {
      informationSystemReleaseDAO.doesReleaseExist(null, VERSION_3_0);
    } catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }
   @Test
  public void testDoesReleaseNameExist() {
    // Create test data.
    InformationSystem is = testDataHelper.createInformationSystem("A");
    commit();

    beginTransaction();

    boolean isDuplicate;
    isDuplicate = informationSystemReleaseDAO.isDuplicateInformationSystem("A", is.getId());
    assertFalse(isDuplicate);

    Integer id = Integer.valueOf(is.getId().intValue() + 1);
    isDuplicate = informationSystemReleaseDAO.isDuplicateInformationSystem("A", id);
    assertTrue(isDuplicate);

    isDuplicate = informationSystemReleaseDAO.isDuplicateInformationSystem("B", null);
    assertFalse(isDuplicate);
    commit();
  }
   @Test
  public void testDoesReleaseExist() {
    InformationSystem is = testDataHelper.createInformationSystem("A");
    testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    testDataHelper.createInformationSystemRelease(is, VERSION_2_0);
    commit();

    beginTransaction();

    boolean isDuplicate;
    isDuplicate = informationSystemReleaseDAO.doesReleaseExist("A", VERSION_3_0);
    assertFalse(isDuplicate);

    isDuplicate = informationSystemReleaseDAO.doesReleaseExist("B", VERSION_1_0);
    assertFalse(isDuplicate);

    isDuplicate = informationSystemReleaseDAO.doesReleaseExist("A", VERSION_1_0);
    assertTrue(isDuplicate);

    commit();
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAOImpl#doesObjectWithDifferentIdExist(Integer, String)}
   */@Test
  public void testDoesObjectWithDifferentIdExist() {
    InformationSystem is = testDataHelper.createInformationSystem("A");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is, VERSION_2_0);
    commit();

    beginTransaction();
    assertFalse(informationSystemReleaseDAO.doesObjectWithDifferentIdExist(isr1.getId(), "A # 1.0"));
    assertFalse(informationSystemReleaseDAO.doesObjectWithDifferentIdExist(isr2.getId(), "A # 2.0"));
    assertFalse(informationSystemReleaseDAO.doesObjectWithDifferentIdExist(isr2.getId(), "A # 3.0"));
    assertTrue(informationSystemReleaseDAO.doesObjectWithDifferentIdExist(isr2.getId(), "A # 1.0"));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAOImpl#doesObjectWithDifferentIdExist(Integer, String)}
   */@Test
  public void testDoesObjectExistWhenIdNull() {
    InformationSystem is = testDataHelper.createInformationSystem("A");
    testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    testDataHelper.createInformationSystemRelease(is, VERSION_2_0);
    commit();

    beginTransaction();
    assertTrue(informationSystemReleaseDAO.doesObjectWithDifferentIdExist(null, "A # 1.0"));
    assertTrue(informationSystemReleaseDAO.doesObjectWithDifferentIdExist(null, "A # 2.0"));
    assertFalse(informationSystemReleaseDAO.doesObjectWithDifferentIdExist(null, "A # 3.0"));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAOImpl#getFirstElement()}
   */@Test
  public void testGetFirstElement() {
    UserContext.getCurrentUserContext().setShowInactiveStatus(true);
    InformationSystem is = testDataHelper.createInformationSystem("A");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    testDataHelper.createInformationSystemRelease(is, VERSION_2_0);
    commit();

    InformationSystemRelease actual = informationSystemReleaseDAO.getFirstElement();
    assertEquals(isr1, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAOImpl#getFirstElement()}
   */@Test
  public void testGetFirstElementWithActiveReleases() {
    UserContext.getCurrentUserContext().setShowInactiveStatus(true);
    InformationSystem is = testDataHelper.createInformationSystem("A");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    isr1.setTypeOfStatus(TypeOfStatus.PLANNED);
    testDataHelper.createInformationSystemRelease(is, VERSION_2_0);
    commit();

    InformationSystemRelease actual = informationSystemReleaseDAO.getFirstElement();
    assertEquals(isr1, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAOImpl#getFirstElement()}
   */@Test
  public void testGetFirstElementWithInactiveReleases() {
    UserContext.getCurrentUserContext().setShowInactiveStatus(false);
    InformationSystem is = testDataHelper.createInformationSystem("A");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    isr1.setTypeOfStatus(TypeOfStatus.INACTIVE);
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is, VERSION_2_0);
    commit();

    InformationSystemRelease actual = informationSystemReleaseDAO.getFirstElement();
    assertEquals(isr2, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO#filterWithIds(java.util.Set, boolean)}
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO#filter(java.util.List, boolean)}
   */@Test
  public void testFilter() {
    InformationSystem firstIS = testDataHelper.createInformationSystem(TEST_IS_NAME_1);
    InformationSystemRelease firstISR = testDataHelper.createInformationSystemRelease(firstIS, VERSION_1_0);

    InformationSystem secIS = testDataHelper.createInformationSystem(TEST_IS_NAME_2);
    InformationSystemRelease secISR = testDataHelper.createInformationSystemRelease(secIS, VERSION_1_0);

    InformationSystem thirdIS = testDataHelper.createInformationSystem("thirdIS");
    InformationSystemRelease thirdISR = testDataHelper.createInformationSystemRelease(thirdIS, VERSION_1_0);
    commit();

    List<InformationSystemRelease> toExclude = arrayList();
    toExclude.add(secISR);

    List<InformationSystemRelease> actual = informationSystemReleaseDAO.filter(toExclude, false);
    List<InformationSystemRelease> expected = arrayList();
    expected.add(firstISR);
    expected.add(thirdISR);

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO#getInformationSystemReleasesWithConnections(boolean)}
   */@Test
  public void testGetInformationSystemReleasesWithConnections() {
    InformationSystem firstIS = testDataHelper.createInformationSystem(TEST_IS_NAME_1);
    InformationSystemRelease firstISR = testDataHelper.createInformationSystemRelease(firstIS, VERSION_1_0);
    InformationSystem secIS = testDataHelper.createInformationSystem(TEST_IS_NAME_2);
    InformationSystemRelease secISR = testDataHelper.createInformationSystemRelease(secIS, VERSION_1_0);

    TechnicalComponent tc = testDataHelper.createTechnicalComponent("testTC", true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, VERSION_1_0, true);

    testDataHelper.createInformationSystemInterface(firstISR, secISR, tcr, "desc");
    commit();

    beginTransaction();
    List<InformationSystemRelease> actual = informationSystemReleaseDAO.getInformationSystemReleasesWithConnections(false);
    List<InformationSystemRelease> expected = arrayList();
    expected.add(firstISR);
    expected.add(secISR);

    assertEquals(expected, actual);
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO#getInformationSystemReleasesWithConnections(boolean)}
   */@Test
  public void testGetInformationSystemReleasesWithConnectionsCaseShowInactive() {
    InformationSystem firstIS = testDataHelper.createInformationSystem(TEST_IS_NAME_1);
    InformationSystemRelease firstISR = testDataHelper.createInformationSystemRelease(firstIS, VERSION_1_0);
    InformationSystem secIS = testDataHelper.createInformationSystem(TEST_IS_NAME_2);
    InformationSystemRelease secISR = testDataHelper.createInformationSystemRelease(secIS, VERSION_1_0);

    TechnicalComponent tc = testDataHelper.createTechnicalComponent("testTC", true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, VERSION_1_0, true);

    testDataHelper.createInformationSystemInterface(firstISR, secISR, tcr, "desc");
    commit();

    beginTransaction();
    List<InformationSystemRelease> actual = informationSystemReleaseDAO.getInformationSystemReleasesWithConnections(true);
    List<InformationSystemRelease> expected = arrayList();
    expected.add(firstISR);
    expected.add(secISR);

    assertEquals(expected, actual);
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO#getOutermostInformationSystemReleases()}
   */@Test
  public void testGetOutermostInformationSystemReleases() {
    InformationSystem firstIS = testDataHelper.createInformationSystem(TEST_IS_NAME_1);
    InformationSystemRelease firstISR = testDataHelper.createInformationSystemRelease(firstIS, VERSION_1_0);

    InformationSystem secIS = testDataHelper.createInformationSystem(TEST_IS_NAME_2);
    InformationSystemRelease secISR = testDataHelper.createInformationSystemRelease(secIS, VERSION_1_0);
    commit();

    beginTransaction();
    List<InformationSystemRelease> actual = informationSystemReleaseDAO.getOutermostInformationSystemReleases();
    List<InformationSystemRelease> expected = arrayList();
    expected.add(firstISR);
    expected.add(secISR);

    assertEquals(expected, actual);
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO#isDuplicateInformationSystem(java.lang.String, java.lang.Integer)}
   */@Test
  public void testIsDuplicateTechnicalComponentCaseFalse() {
    InformationSystem firstIS = testDataHelper.createInformationSystem(TEST_IS_NAME_1);
    commit();

    Integer identifier = firstIS.getId();
    assertFalse(informationSystemReleaseDAO.isDuplicateInformationSystem(TEST_IS_NAME_1, identifier));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO#isDuplicateInformationSystem(java.lang.String, java.lang.Integer)}
   */@Test
  public void testIsDuplicateTechnicalComponentCaseException() {
    try {
      informationSystemReleaseDAO.isDuplicateInformationSystem(null, null);
    } catch (IllegalArgumentException e) {
      // Nothing to do. It is okay.
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO#getReleasesWithSuccessors(boolean)}
   */@Test
  public void testGetInformationsystemsWithSuccessors() {
    InformationSystem is = testDataHelper.createInformationSystem("is");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is, VERSION_2_0);
    InformationSystemRelease isr3 = testDataHelper.createInformationSystemRelease(is, VERSION_3_0);

    InformationSystemRelease predecessor = testDataHelper.createInformationSystemRelease(is, "0.0");
    InformationSystemRelease successor = testDataHelper.createInformationSystemRelease(is, "4.0");

    isr1.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.INACTIVE);
    isr2.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.TARGET);
    isr3.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.PLANNED);
    predecessor.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.PLANNED);
    successor.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.PLANNED);
    commit();

    beginTransaction();
    isr1.addPredecessor(predecessor);
    isr2.addPredecessor(predecessor);
    isr3.addSuccessor(successor);
    informationSystemReleaseDAO.saveOrUpdate(isr1);
    informationSystemReleaseDAO.saveOrUpdate(isr3);
    informationSystemReleaseDAO.saveOrUpdate(successor);
    informationSystemReleaseDAO.saveOrUpdate(predecessor);
    commit();

    assertEquals(Lists.newArrayList(predecessor, isr3, successor), informationSystemReleaseDAO.getReleasesWithSuccessors(false));
    assertEquals(Lists.newArrayList(predecessor, isr1, isr3, successor), informationSystemReleaseDAO.getReleasesWithSuccessors(true));
  }
}

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
package de.iteratec.iteraplan.persistence.TechnicalLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAO;


public class TechnicalComponentReleaseDAOTest extends BaseTransactionalTestSupport {

  @Autowired
  private TechnicalComponentReleaseDAO     tcrDao;
  @Autowired
  private TechnicalComponentReleaseService technicalComponentReleaseService;
  @Autowired
  private TestDataHelper2                  testDataHelper;

  private static final String              TEST_TC_NAME = "tc1";
  private static final String              BETA_VERSION = "Beta";
  private static final String              VERSION_1_0  = "1.0";

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAO#doesReleaseExist(java.lang.String, java.lang.String)}
   * There should be true as a return value, because the release exists.
   */
  @Test
  public void testDoesReleaseExistCaseTrue() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent("testTC", true, true);
    testDataHelper.createTCRelease(tc, BETA_VERSION, true);
    commit();
    boolean actual = tcrDao.doesReleaseExist("testTC", BETA_VERSION);

    assertTrue(actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAO#doesReleaseExist(java.lang.String, java.lang.String)}
   * There should be false as a return value, because the release does not exists.
   */
  @Test
  public void testDoesReleaseExistCaseFalse() {
    boolean actual = tcrDao.doesReleaseExist("testTC", BETA_VERSION);

    assertFalse(actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAO#doesReleaseExist(java.lang.String, java.lang.String)}
   * The method should throw an exception, because the name parameter is null.
   */
  @Test
  public void testDoesReleaseExistCaseException() {
    try {
      tcrDao.doesReleaseExist(null, BETA_VERSION);
    } catch (IllegalArgumentException e) {
      // Nothing to do. It is okay.
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAO#eligibleForConnections(java.util.List, boolean)}
   * The result is a List with all releases that are availableForConnection.
   */
  @Test
  public void testEligibleForConnections() {
    TechnicalComponent firstTC = testDataHelper.createTechnicalComponent("firstTC", true, true);
    TechnicalComponentRelease firstTCR = testDataHelper.createTCRelease(firstTC, VERSION_1_0, true);

    TechnicalComponent secTC = testDataHelper.createTechnicalComponent("secTC", false, true);
    testDataHelper.createTCRelease(secTC, VERSION_1_0, true);

    TechnicalComponent thirdTC = testDataHelper.createTechnicalComponent("thirdTC", true, true);
    TechnicalComponentRelease thirdTCR = testDataHelper.createTCRelease(thirdTC, VERSION_1_0, true);
    commit();

    List<TechnicalComponentRelease> toExclude = arrayList();

    List<TechnicalComponentRelease> actual = tcrDao.eligibleForConnections(toExclude, false);
    List<TechnicalComponentRelease> expected = arrayList();
    expected.add(firstTCR);
    expected.add(thirdTCR);

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAO#filter(java.util.List, boolean)}
   */
  @Test
  public void testFilter() {
    TechnicalComponent firstTC = testDataHelper.createTechnicalComponent("firstTC", true, true);
    TechnicalComponentRelease firstTCR = testDataHelper.createTCRelease(firstTC, VERSION_1_0, true);

    TechnicalComponent secTC = testDataHelper.createTechnicalComponent("secTC", true, true);
    TechnicalComponentRelease secTCR = testDataHelper.createTCRelease(secTC, VERSION_1_0, true);

    TechnicalComponent thirdTC = testDataHelper.createTechnicalComponent("thirdTC", true, true);
    TechnicalComponentRelease thirdTCR = testDataHelper.createTCRelease(thirdTC, VERSION_1_0, true);
    commit();

    List<TechnicalComponentRelease> toExclude = arrayList();
    toExclude.add(secTCR);

    List<TechnicalComponentRelease> actual = tcrDao.filter(toExclude, false);
    List<TechnicalComponentRelease> expected = arrayList();
    expected.add(firstTCR);
    expected.add(thirdTCR);

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAOImpl#getFirstElement()}
   */
  @Test
  public void testGetFirstElement() {
    TechnicalComponent secTC = testDataHelper.createTechnicalComponent("firstTC", true, true);
    TechnicalComponentRelease secTCR = testDataHelper.createTCRelease(secTC, VERSION_1_0, true);

    TechnicalComponent firstTC = testDataHelper.createTechnicalComponent("secTC", true, true);
    testDataHelper.createTCRelease(firstTC, VERSION_1_0, true);
    commit();

    TechnicalComponentRelease actual = tcrDao.getFirstElement();
    TechnicalComponentRelease expected = secTCR;

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAO#isDuplicateTechnicalComponent(java.lang.String, java.lang.Integer)}
   * The reurn result should be false because the tc is not duplicated. There CAN NOT be written in
   * the data base another tc with the same name. That is why the method should not return true
   * value.
   */
  @Test
  public void testIsDuplicateTechnicalComponentCaseFalse() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent("tc", true, true);
    commit();
    Integer identifier = tc.getId();
    boolean actual = tcrDao.isDuplicateTechnicalComponent("tc", identifier);

    assertFalse(actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAO#isDuplicateTechnicalComponent(java.lang.String, java.lang.Integer)}
   * The method should throw an exception if the name argument is null
   */
  @Test
  public void testIsDuplicateTechnicalComponentCaseException() {
    try {
      tcrDao.isDuplicateTechnicalComponent(null, null);
    } catch (IllegalArgumentException e) {
      // Nothing to do. It is okay.
    }
  }

  @Test
  public void testUpdateFrom() {

    String startDay = "01.01.2010";
    Date startDate = DateUtils.parseAsDate(startDay, Locale.GERMAN);
    String endDay = "01.01.2011";
    Date endDate = DateUtils.parseAsDate(endDay, Locale.GERMAN);

    TechnicalComponent tc1 = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);

    TechnicalComponentRelease tcr1 = testDataHelper.createTCRelease(tc1, VERSION_1_0, "description tc1 # " + VERSION_1_0, startDay, endDay,
        TechnicalComponentRelease.TypeOfStatus.PLANNED, true);

    TechnicalComponentRelease predecessor = testDataHelper.createTCRelease(tc1, "predecessor", "", null, null,
        TechnicalComponentRelease.TypeOfStatus.CURRENT, true);
    tcr1.addPredecessor(predecessor);

    TechnicalComponentRelease usedTbb = testDataHelper.createTCRelease(tc1, "usedTcr", "", null, null,
        TechnicalComponentRelease.TypeOfStatus.CURRENT, true);
    tcr1.addBaseComponent(usedTbb);

    ArchitecturalDomain ad = testDataHelper.createArchitecturalDomain("A-Dom", "");
    tcr1.addArchitecturalDomain(ad);

    InfrastructureElement ie = testDataHelper.createInfrastructureElement("ieForTCR", "");
    testDataHelper.addTcrToIe(ie, tcr1);

    tcrDao.saveOrUpdate(tcr1);

    commit();

    beginTransaction();

    TechnicalComponentRelease updateTcRel = technicalComponentReleaseService.saveOrUpdate(tcr1);

    commit();

    assertNotNull(updateTcRel);
    assertEquals(TEST_TC_NAME, updateTcRel.getTechnicalComponent().getName());
    assertTrue(updateTcRel.getTechnicalComponent().isAvailableForInterfaces());
    assertEquals(VERSION_1_0, updateTcRel.getVersion());
    assertEquals("description tc1 # " + VERSION_1_0, updateTcRel.getDescription());
    assertEquals(startDate, updateTcRel.runtimeStartsAt());
    assertEquals(endDate, updateTcRel.runtimeEndsAt());
    assertEquals(TechnicalComponentRelease.TypeOfStatus.PLANNED, updateTcRel.getTypeOfStatus());
    assertEquals(1, updateTcRel.getPredecessors().size());
    assertEquals(1, updateTcRel.getArchitecturalDomains().size());
    assertEquals(1, updateTcRel.getInfrastructureElements().size());
    assertEquals(1, updateTcRel.getBaseComponents().size());
    TechnicalComponentRelease pred = updateTcRel.getPredecessors().iterator().next();
    assertEquals(TEST_TC_NAME, pred.getTechnicalComponent().getName());
    assertTrue(pred.getTechnicalComponent().isAvailableForInterfaces());
    assertEquals("predecessor", pred.getVersion());
    TechnicalComponentRelease used = updateTcRel.getBaseComponents().iterator().next();
    assertEquals(TEST_TC_NAME, used.getTechnicalComponent().getName());
    assertTrue(used.getTechnicalComponent().isAvailableForInterfaces());
    assertEquals("usedTcr", used.getVersion());
    ArchitecturalDomain ad2 = updateTcRel.getArchitecturalDomains().iterator().next();
    assertEquals("A-Dom", ad2.getName());
    InfrastructureElement ie2 = updateTcRel.getInfrastructureElements().iterator().next();
    assertEquals("ieForTCR", ie2.getName());
  }

  @Test
  public void testValidateOrphanConnections() {
    TechnicalComponentRelease tcRel1 = prepareData();

    beginTransaction();
    try {
      tcRel1.getTechnicalComponent().setAvailableForInterfaces(false);
      technicalComponentReleaseService.saveOrUpdate(tcRel1);
      fail();
    } catch (IteraplanException e) {
      assertEquals(IteraplanErrorMessages.CONNECTION_ASSOCIATED_WITH_TC, e.getErrorCode());
    }
  }

  @Test
  public void testValidatePredecessorCycle() {
    TechnicalComponentRelease tcRel1 = prepareData();

    // check predecessor cycle
    beginTransaction();
    try {
      tcRel1 = technicalComponentReleaseService.loadObjectById(tcRel1.getId());
      TechnicalComponentRelease successor1 = technicalComponentReleaseService.filter(null, true).get(0);
      tcRel1.addPredecessor(successor1);
      technicalComponentReleaseService.saveOrUpdate(tcRel1);
      fail();
    } catch (IteraplanException e) {
      assertEquals(IteraplanErrorMessages.TECHNICALCOMPONENT_PREDECESSOR_CYCLE, e.getErrorCode());
    }
  }

  @Test
  public void testValidateUsedCycle() {
    TechnicalComponentRelease tcRel1 = prepareData();

    // check used cycle
    beginTransaction();
    try {
      tcRel1 = technicalComponentReleaseService.loadObjectById(tcRel1.getId());
      TechnicalComponentRelease usedByTc1 = technicalComponentReleaseService.filter(null, true).get(0);
      tcRel1.addBaseComponent(usedByTc1);
      technicalComponentReleaseService.saveOrUpdate(tcRel1);
      fail();
    } catch (IteraplanException e) {
      assertEquals(IteraplanErrorMessages.TECHNICALCOMPONENT_BASECOMPONENT_CYCLE, e.getErrorCode());
    }
  }

  private TechnicalComponentRelease prepareData() {
    commit();
    beginTransaction();

    TechnicalComponent tc1 = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);

    TechnicalComponentRelease tcRel1 = testDataHelper.createTCRelease(tc1, VERSION_1_0, "description tc1 # " + VERSION_1_0, "01.02.2005",
        "20.02.2009", TechnicalComponentRelease.TypeOfStatus.PLANNED, true);

    TechnicalComponentRelease predecessor1 = testDataHelper.createTCRelease(tc1, "predecessor1", "", "", "",
        TechnicalComponentRelease.TypeOfStatus.CURRENT, true);
    tcRel1.addPredecessor(predecessor1);

    TechnicalComponentRelease successor1 = testDataHelper.createTCRelease(tc1, "successor1", "", "", "",
        TechnicalComponentRelease.TypeOfStatus.CURRENT, true);
    tcRel1.addSuccessor(successor1);

    TechnicalComponentRelease usedTbb1 = testDataHelper.createTCRelease(tc1, "usedTc", "", "", "", TechnicalComponentRelease.TypeOfStatus.CURRENT,
        true);
    tcRel1.addBaseComponent(usedTbb1);

    TechnicalComponentRelease usedByTc1 = testDataHelper.createTCRelease(tc1, "usedByTc1", "", "", "",
        TechnicalComponentRelease.TypeOfStatus.CURRENT, true);
    tcRel1.addParentComponent(usedByTc1);

    ArchitecturalDomain ad = testDataHelper.createArchitecturalDomain("A-Dom", "");
    tcRel1.addArchitecturalDomain(ad);

    InfrastructureElement ie = testDataHelper.createInfrastructureElement("ieForTCR", "");
    testDataHelper.addTcrToIe(ie, tcRel1);

    //     have an idea for a test with an interface connection? 
    InformationSystem is1 = testDataHelper.createInformationSystem("is1");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is1, VERSION_1_0, "", "", "",
        InformationSystemRelease.TypeOfStatus.CURRENT);

    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is1, "version2", "", "", "",
        InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemInterface conn1 = testDataHelper.createInformationSystemInterface(isr1, isr2, tcRel1, "");
    conn1.toString();

    commit();
    return tcRel1;
  }

}

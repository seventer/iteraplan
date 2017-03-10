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
package de.iteratec.iteraplan.businesslogic.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Iterables;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.Seal;
import de.iteratec.iteraplan.model.SealState;


/**
 * Integration test for the {@link SealServiceImpl} class.
 */
public class SealServiceImplTest extends BaseTransactionalTestSupport {

  @Autowired
  private SealService                     sealService;
  @Autowired
  private InformationSystemReleaseService isrService;
  @Autowired
  private TestDataHelper2                 testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.SealServiceImpl#createSeal(de.iteratec.iteraplan.model.Seal)}.
   */
  @Test
  public void testCreateSeal() {
    testDataHelper.createUserContext();
    Integer isrId = createSimpleISR().getId();
    commit();
    beginTransaction();

    InformationSystemRelease isr = isrService.loadObjectById(isrId);
    assertEquals(SealState.NOT_AVAILABLE, isr.getSealState());

    Seal seal = sealService.createSeal(isr, "The brand new seal");
    assertNotNull(seal.getId());
    assertEquals("system", seal.getUser());
    assertEquals("The brand new seal", seal.getComment());
    assertEquals(isr, seal.getBb());
    assertNotNull(seal.getDate());
    assertEquals(SealState.VALID, isr.getSealState());
  }

  /**
   * Tests if the ISR containing {@link Seal} can be deleted.
   */
  @Test
  public void testDeleteISRWithSeal() {
    testDataHelper.createUserContext();
    Integer isrId = createSimpleISR().getId();
    commit();
    beginTransaction();

    InformationSystemRelease isr = isrService.loadObjectById(isrId);
    assertEquals(SealState.NOT_AVAILABLE, isr.getSealState());

    Seal seal = sealService.createSeal(isr, "The brand new seal");
    assertNotNull(seal.getId());
    commit();
    beginTransaction();

    isrService.deleteEntity(isr);
    assertNull(isrService.loadObjectByIdIfExists(isrId));
  }

  /**
   * Tests if the {@link Seal} state will be updated after ISR update.
   */
  @Test
  public void testIfSealIsResetedAfterSave() {
    Integer isrId = createSimpleISR().getId();
    commit();
    beginTransaction();

    InformationSystemRelease isr = isrService.loadObjectById(isrId);
    assertEquals(SealState.NOT_AVAILABLE, isr.getSealState());

    sealService.createSeal(isr, "The brand new seal");
    assertEquals(SealState.VALID, isr.getSealState());
    isrService.saveOrUpdate(isr);
    assertEquals(SealState.INVALID, isr.getSealState());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.SealServiceImpl#createSeal(de.iteratec.iteraplan.model.Seal)}.
   */
  @Test
  public void testSealMutability() {
    InformationSystemRelease isr = createSimpleISR();

    testDataHelper.createUserContext();
    Seal seal = sealService.createSeal(isr, "The brand new seal");
    seal.setComment("annother comment");
    commit();
    beginTransaction();

    InformationSystemRelease reloadedIsr = isrService.loadObjectById(isr.getId());
    assertEquals(1, reloadedIsr.getSeals().size());
    Seal reloadedSeal = Iterables.get(reloadedIsr.getSeals(), 0);
    assertEquals("The brand new seal", reloadedSeal.getComment());
  }

  /**
   * Creates sample {@link InformationSystemRelease} instance.
   * 
   * @return the newly created and persisted isr
   */
  private InformationSystemRelease createSimpleISR() {
    InformationSystem is = testDataHelper.createInformationSystem("I");
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, "i1", "", "1.1.2005", "31.12.2005", TypeOfStatus.CURRENT);
    isrService.saveOrUpdate(isr);
    commit();
    beginTransaction();

    return isr;
  }

}

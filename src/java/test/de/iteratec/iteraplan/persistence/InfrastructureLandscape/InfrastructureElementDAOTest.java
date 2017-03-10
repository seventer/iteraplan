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
package de.iteratec.iteraplan.persistence.InfrastructureLandscape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.persistence.dao.InfrastructureElementDAO;


public class InfrastructureElementDAOTest extends BaseTransactionalTestSupport {
  private static final String          VERSION_1 = "version1";
  @Autowired
  private InfrastructureElementDAO     infrastructureElementDAO;
  @Autowired
  private InfrastructureElementService infrastructureElementService;
  @Autowired
  private TestDataHelper2              testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testUpdateFrom() {
    String startDay = "01.01.2010";
    String endDay = "01.01.2011";

    InfrastructureElement ie1 = testDataHelper.createInfrastructureElement("ie1", "");
    InfrastructureElement root = infrastructureElementService.getFirstElement();
    ie1.addParent(root);

    TechnicalComponent tc1 = testDataHelper.createTechnicalComponent("tc1", true, true);

    TechnicalComponentRelease tbbRel1 = testDataHelper.createTCRelease(tc1, VERSION_1, "description tc1 # version1", startDay, endDay,
        TechnicalComponentRelease.TypeOfStatus.PLANNED, true);
    testDataHelper.addTcrToIe(ie1, tbbRel1);

    InformationSystem is1 = testDataHelper.createInformationSystem("is1");
    InformationSystemRelease isRel1 = testDataHelper.createInformationSystemRelease(is1, VERSION_1, "description is1 # version1", startDay, endDay,
        InformationSystemRelease.TypeOfStatus.PLANNED);

    HashSet<InformationSystemRelease> isReleases = new HashSet<InformationSystemRelease>();
    isReleases.add(isRel1);
    ie1.setInformationSystemReleases(isReleases);

    ie1 = infrastructureElementDAO.saveOrUpdate(ie1);

    commit();

    // retrieve the stored infrastructure element and for contents
    InfrastructureElement updateIE = null;
    beginTransaction();
    updateIE = infrastructureElementService.saveOrUpdate(ie1);
    commit();

    Date startDate = DateUtils.parseAsDate(startDay, Locale.GERMAN);
    Date endDate = DateUtils.parseAsDate(endDay, Locale.GERMAN);
    RuntimePeriod period = new RuntimePeriod(startDate, endDate);

    assertNotNull(updateIE);
    assertEquals("ie1", updateIE.getName());

    Set<InformationSystemRelease> updateISReleases = updateIE.getInformationSystemReleases();
    assertNotNull(updateISReleases);
    assertEquals(1, updateISReleases.size());
    InformationSystemRelease updateIS = updateISReleases.iterator().next();
    assertNotNull(updateIS);
    assertEquals(VERSION_1, updateIS.getVersion());
    assertEquals("description is1 # version1", updateIS.getDescription());
    assertEquals(period, updateIS.getRuntimePeriod());

    Set<TechnicalComponentRelease> updateTCReleases = updateIE.getTechnicalComponentReleases();
    assertNotNull(updateTCReleases);
    assertEquals(1, updateTCReleases.size());
    TechnicalComponentRelease updateTC = updateTCReleases.iterator().next();
    assertNotNull(updateTC);
    assertEquals(VERSION_1, updateTC.getVersion());
    assertEquals("description tc1 # version1", updateTC.getDescription());
    assertEquals(period, updateTC.getRuntimePeriod());
  }
  @Test
  public void testValidateParentCycle() {
    InfrastructureElement ie1 = prepareData();
    beginTransaction();

    InfrastructureElement successor = ie1.getChildrenAsList().get(0);
    try {
      InfrastructureElement successorReloaded = infrastructureElementDAO.loadObjectById(successor.getId());
      ie1.addParent(successorReloaded);
      infrastructureElementService.saveOrUpdate(ie1);
      fail();
    } catch (IteraplanException e) {
      assertEquals(IteraplanErrorMessages.ELEMENT_OF_HIERARCHY_CYCLE, e.getErrorCode());
    }
  }

  @Test
  public void testValidateParentChildCycle() {
    InfrastructureElement ie1 = prepareData();
    beginTransaction();

    InfrastructureElement parent = ie1.getParent();
    try {
      parent = infrastructureElementDAO.loadObjectById(parent.getId());
      parent.addParent(ie1);
      infrastructureElementService.saveOrUpdate(parent);
      fail();
    } catch (IteraplanException e) {
      assertEquals(IteraplanErrorMessages.ELEMENT_OF_HIERARCHY_CYCLE, e.getErrorCode());
    }
  }

  /**
   * Tests illegal states for {@link InfrastructureElement}
   */
  @Test
  public void testValidateChildrenCycle() {
    InfrastructureElement ie = prepareData();
    beginTransaction();

    InfrastructureElement parent = ie.getParent();
    try {
      parent = infrastructureElementDAO.loadObjectById(parent.getId());
      ie.getChildren().add(parent);
      infrastructureElementService.saveOrUpdate(ie);
      fail();
    } catch (IteraplanException e) {
      assertEquals(IteraplanErrorMessages.PARENT_CAN_NOT_BE_CHILD_ELEMENT, e.getErrorCode());
    }
  }

  private InfrastructureElement prepareData() {
    commit();
    beginTransaction();

    InfrastructureElement ie = testDataHelper.createInfrastructureElement("ie1", "");
    InfrastructureElement parent = testDataHelper.createInfrastructureElement("parent", "");
    InfrastructureElement successor = testDataHelper.createInfrastructureElement("successor", "");

    ie.addParent(parent);
    successor.addParent(ie);

    infrastructureElementDAO.saveOrUpdate(ie);
    commit();

    return ie;
  }
}

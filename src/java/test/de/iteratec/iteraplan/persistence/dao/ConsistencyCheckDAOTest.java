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
package de.iteratec.iteraplan.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.persistence.util.Pair;


/**
 * Tests if all methods of ConsistencyCheckDAOImpl eventually operate with valid HQL/SQL against the database.
 * Therefore, most tests here do simply check if the return value value, and implicitly that no exception was thrown.
 * Only a few test actually perform semantic tests on the return values.   
 */
public class ConsistencyCheckDAOTest extends BaseTransactionalTestSupport {
  @Autowired
  private ConsistencyCheckDAO  consistencyCheckDAO;
  @Autowired
  private BuildingBlockTypeDAO buildingBlockTypeDAO;
  @Autowired
  private TestDataHelper2      testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testGetConnectionsWithUnsynchronizedInformationSystemReleases() {
    List<InformationSystemInterface> list = consistencyCheckDAO.getConnectionsWithUnsynchronizedInformationSystemReleases();
    assertNotNull(list);
  }

  @Test
  public void testGetInformationSystemReleasesActiveWithoutStatusCurrent() {
    List<InformationSystemRelease> list = consistencyCheckDAO.getInformationSystemReleasesActiveWithoutStatusCurrent();
    assertNotNull(list);
  }

  @Test
  public void testGetInformationSystemReleasesInactiveWithoutStatusInactive() {
    List<InformationSystemRelease> list = consistencyCheckDAO.getInformationSystemReleasesInactiveWithoutStatusInactive();
    assertNotNull(list);
  }

  @Test
  public void testGetInformationSystemReleasesPlannedWithoutAssociatedProjects() {
    List<InformationSystemRelease> list = consistencyCheckDAO.getInformationSystemReleasesPlannedWithoutAssociatedProjects();
    assertNotNull(list);
  }

  @Test
  public void testGetInformationSystemReleasesWithoutStatusPlannedButAssociatedToProjects() {
    List<InformationSystemRelease> list = consistencyCheckDAO.getInformationSystemReleasesWithoutStatusPlannedButAssociatedToProjects();
    assertNotNull(list);
  }

  @Test
  public void testGetInformationSystemReleasesWithParents() {
    List<InformationSystemRelease> list = consistencyCheckDAO.getInformationSystemReleasesWithParents();
    assertNotNull(list);
  }

  @Test
  public void testGetInformationSystemReleasesWithStatusCurrentOrInactiveButNotYetLaunched() {
    List<InformationSystemRelease> list = consistencyCheckDAO.getInformationSystemReleasesWithStatusCurrentOrInactiveButNotYetLaunched();
    assertNotNull(list);
  }

  @Test
  public void testGetTcReleasesActiveWithoutStatusCurrent() {
    List<TechnicalComponentRelease> list = consistencyCheckDAO.getTcReleasesActiveWithoutStatusCurrent();
    assertNotNull(list);
  }

  @Test
  public void testGetTcReleasesInactiveWithoutStatusInactive() {
    List<TechnicalComponentRelease> list = consistencyCheckDAO.getTcReleasesInactiveWithoutStatusInactive();
    assertNotNull(list);
  }

  @Test
  public void testGetTcReleasesWithStatusCurrentOrInactiveButNotYetLaunched() {
    List<TechnicalComponentRelease> list = consistencyCheckDAO.getTcReleasesWithStatusCurrentOrInactiveButNotYetLaunched();
    assertNotNull(list);
  }

  @Test
  public void testGetTcReleasesWithStatusUndefined() {
    List<TechnicalComponentRelease> list = consistencyCheckDAO.getTcReleasesWithStatusUndefined();
    assertNotNull(list);
  }

  @Test
  public void testGetTcReleasesUsingOtherTcrNotReleased() {
    List<Map<String, TechnicalComponentRelease>> list = consistencyCheckDAO.getTcReleasesUsingOtherTcrNotReleased();
    assertNotNull(list);
  }

  @Test
  public void testGetUnsynchronizedProjectsWithInformationSystemReleases() {
    List<Object[]> list = consistencyCheckDAO.getUnsynchronizedProjectsWithInformationSystemReleases();
    assertNotNull(list);
  }

  @Test
  public void testGetUnsynchronizedTcAndIsReleases() {
    List<Object[]> list = consistencyCheckDAO.getUnsynchronizedTcAndIsReleases();
    assertNotNull(list);
  }

  @Test
  public void testGetInformationSystemInterfacesConnectingCurrentAndNonCurrentInformationSystemReleases() {
    List<InformationSystemInterface> list = consistencyCheckDAO
        .getInformationSystemInterfacesConnectingCurrentAndNonCurrentInformationSystemReleases();
    assertNotNull(list);
  }

  @Test
  public void testGetInformationSystemInterfacesConnectingCurrentAndNonCurrentInformationSystemReleases2() {
    List<InformationSystemInterface> list = consistencyCheckDAO
        .getInformationSystemInterfacesConnectingCurrentAndNonCurrentInformationSystemReleases2();
    assertNotNull(list);
  }

  @Test
  public void testGetTechnicalComponentReleasesSharingNoArchitecturalDomainWithTheirChildren() {
    List<Object[]> list = consistencyCheckDAO.getTechnicalComponentReleasesSharingNoArchitecturalDomainWithTheirChildren();
    assertNotNull(list);
  }

  @Test
  public void testGetTechnicalComponentReleasesSharingNoArchitecturalDomainWithTheirSuccessors() {
    List<Object[]> list = consistencyCheckDAO.getTechnicalComponentReleasesSharingNoArchitecturalDomainWithTheirSuccessors();
    assertNotNull(list);
  }

  @Test
  public void testGetIsrConnectedToInfrastrElemViaTcrButNotDirectly() {
    // create an open loop isr1 --> tcr1 --> ie1 -|-> isr1
    InformationSystem is = testDataHelper.createInformationSystem("IS1");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is, "1");

    TechnicalComponent tc = testDataHelper.createTechnicalComponent("TC1", true, true);
    TechnicalComponentRelease tcr1 = testDataHelper.createTCRelease(tc, "1", true);
    testDataHelper.addTcrToIsr(isr1, tcr1);

    InfrastructureElement ie1 = testDataHelper.createInfrastructureElement("IE1", "descr");
    testDataHelper.addIeToTcr(tcr1, ie1);

    List<Map<String, BuildingBlock>> list = consistencyCheckDAO.getIsrConnectedToInfrastrElemViaTcrButNotDirectly();
    assertEquals(1, list.size());
    Map<String, BuildingBlock> map = list.get(0);
    assertEquals(isr1, map.get(ConsistencyCheckDAO.ISR_ALIAS));
    assertEquals(tcr1, map.get(ConsistencyCheckDAO.TCR_ALIAS));
    assertEquals(ie1, map.get(ConsistencyCheckDAO.INFRASTR_ELEM_ALIAS));
    // it found an inconsistency -> ok

    // create another IE that has a closed loop
    InfrastructureElement ie2 = testDataHelper.createInfrastructureElement("IE2", "descr");
    testDataHelper.addIeToTcr(tcr1, ie2);
    testDataHelper.addIeToIsr(isr1, ie2);
    list = consistencyCheckDAO.getIsrConnectedToInfrastrElemViaTcrButNotDirectly();
    assertEquals("shouldn't find more inconsistencies than before", 1, list.size());

    // create an open loop isr2 -|-> tcr2 --> ie2 --> isr2
    // this one shouldn't be found
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is, "2");
    testDataHelper.addIeToIsr(isr2, ie2);
    TechnicalComponentRelease tcr2 = testDataHelper.createTCRelease(tc, "2", true);
    testDataHelper.addIeToTcr(tcr2, ie2);
    list = consistencyCheckDAO.getIsrConnectedToInfrastrElemViaTcrButNotDirectly();
    assertEquals("shouldn't find more inconsistencies than before", 1, list.size());

  }

  @Test
  public void testGetIsiConnectedToInformationSystemViaBusinessObjectButNotDirectly() {
    List<Map<String, BuildingBlock>> list = consistencyCheckDAO.getIsiConnectedToInformationSystemViaBusinessObjectButNotDirectly();
    assertNotNull(list);
  }

  @Test
  public void testGetBoUsedByInformationSystemButNotTransported() {
    List<Pair<BusinessObject, InformationSystemRelease>> list = consistencyCheckDAO.getBoUsedByInformationSystemButNotTransported();
    assertNotNull(list);
  }

  @Test
  public void testGetBuildingBlockWithNoAssociations() throws Exception {
    for (TypeOfBuildingBlock tob : TypeOfBuildingBlock.DISPLAY) {
      List<BuildingBlock> list = consistencyCheckDAO.getBuildingBlocksWithNoAssociations(tob);
      assertNotNull(list);
    }
  }

  @Test
  public void testGetBuildingBlocksWithAttributeValueAssigmentsOutOfRange() throws Exception {
    for (TypeOfBuildingBlock tob : TypeOfBuildingBlock.DISPLAY) {
      List<BuildingBlock> list = consistencyCheckDAO.getBuildingBlocksWithAttributeValueAssigmentsOutOfRange(tob);
      assertNotNull(list);
    }
  }

  @Test
  public void testGetBuildingBlocksRecentlyUpdated() throws Exception {
    for (TypeOfBuildingBlock tob : TypeOfBuildingBlock.DISPLAY) {
      List<BuildingBlock> list = consistencyCheckDAO.getBuildingBlocksRecentlyUpdated(tob, 3, true);
      assertNotNull(list);
      list = consistencyCheckDAO.getBuildingBlocksRecentlyUpdated(tob, 3, false);
      assertNotNull(list);
    }
  }

  @Test
  public void testGetNumberAttributeTypeAndValueForBuildingBlockID() throws Exception {
    for (BuildingBlockType bbt : buildingBlockTypeDAO.getBuildingBlockTypesEligibleForAttributes()) {
      List<Object[]> list = consistencyCheckDAO.getNumberAttributeTypeAndValueForBuildingBlockID(bbt.getId());
      assertNotNull(list);
    }
  }

  @Test
  public void testGetReleasesWithStatusCurrentForInformationSystemID() throws Exception {
    InformationSystem is = testDataHelper.createInformationSystem("IS1");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is, "1");
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is, "2");
    isr1.setTypeOfStatus(TypeOfStatus.CURRENT);
    isr2.setTypeOfStatus(TypeOfStatus.CURRENT);
    List<InformationSystemRelease> list = consistencyCheckDAO.getReleasesWithStatusCurrentForInformationSystemID(is.getId());
    assertEquals("Consistency check should find isr1 and isr2", 2, list.size());

    isr2.setTypeOfStatus(TypeOfStatus.INACTIVE);
    list = consistencyCheckDAO.getReleasesWithStatusCurrentForInformationSystemID(is.getId());
    assertEquals("Consistency check should only find isr1", 1, list.size());
  }

  @Test
  public void testGetReleasesWithStatusCurrentForTcID() throws Exception {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent("TC1", true, true);
    TechnicalComponentRelease tcr1 = testDataHelper.createTCRelease(tc, "1", true);
    TechnicalComponentRelease tcr2 = testDataHelper.createTCRelease(tc, "2", true);
    tcr1.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.CURRENT);
    tcr2.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.CURRENT);
    List<TechnicalComponentRelease> list = consistencyCheckDAO.getReleasesWithStatusCurrentForTcID(tc.getId());
    assertEquals("Consistency check should find tcr1 and tcsr2", 2, list.size());

    tcr2.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.INACTIVE);
    list = consistencyCheckDAO.getReleasesWithStatusCurrentForTcID(tc.getId());
    assertEquals("Consistency check should only find tcr1", 1, list.size());

  }
}

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;

import com.google.common.collect.Iterables;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO;


/**
 * Integration test for the {@link InformationSystemInterfaceDAO} class.
 */
public class InformationSystemInterfaceDAOTest extends BaseTransactionalTestSupport {

  private static final String               TEST_TC_NAME     = "testTC";
  private static final String               TEST_IS_NAME_A   = "testAIS";
  private static final String               TEST_IS_NAME_B   = "testBIS";
  private static final String               BETA_VERSION     = "Beta";
  private static final String               TEST_DESCRIPTION = "testDescription";

  @Autowired
  private InformationSystemInterfaceDAO     isiDAO;
  @Autowired
  private InformationSystemInterfaceService informationSystemInterfaceService;
  @Autowired
  private TestDataHelper2                   testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO#getConnectionsImplementedByTechnicalComponent(de.iteratec.iteraplan.model.TechnicalComponent)}
   */
  @Test
  public void testGetConnectionsImplementedByTechnicalComponentCaseOne() {
    InformationSystemInterface isi = createInterfaceWithIsrAndTcr();
    commit();

    beginTransaction();
    TechnicalComponentRelease tcr = Iterables.getOnlyElement(isi.getTechnicalComponentReleases());
    List<InformationSystemInterface> actual = isiDAO.getConnectionsImplementedByTechnicalComponent(tcr.getTechnicalComponent());
    List<InformationSystemInterface> expected = arrayList();
    expected.add(isi);
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO#getConnectionsImplementedByTechnicalComponent(de.iteratec.iteraplan.model.TechnicalComponent)}
   */@Test
  public void testGetConnectionsImplementedByTechnicalComponentCaseEmpty() {
    List<InformationSystemInterface> actual = isiDAO.getConnectionsImplementedByTechnicalComponent(null);
    List<InformationSystemInterface> expected = arrayList();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO#getConnectionsImplementedByTechnicalComponentRelease(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   */@Test
  public void testGetConnectionsImplementedByTechnicalComponentReleaseCaseOne() {
    InformationSystemInterface isi = createInterfaceWithIsrAndTcr();
    commit();

    beginTransaction();
    TechnicalComponentRelease tcr = Iterables.getOnlyElement(isi.getTechnicalComponentReleases());
    List<InformationSystemInterface> actual = isiDAO.getConnectionsImplementedByTechnicalComponentRelease(tcr);
    List<InformationSystemInterface> expected = arrayList();
    expected.add(isi);
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO#getConnectionsImplementedByTechnicalComponentRelease(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   */@Test
  public void testGetConnectionsImplementedByTechnicalComponentReleaseCaseEmpty() {
    List<InformationSystemInterface> actual = isiDAO.getConnectionsImplementedByTechnicalComponentRelease(null);
    List<InformationSystemInterface> expected = arrayList();
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO#getFirstElement()}
   */@Test
  public void testGetFirstElement() {
    UserContext.getCurrentUserContext().setShowInactiveStatus(true);
    InformationSystemInterface isi = createInterfaceWithIsrAndTcr();
    commit();

    InformationSystemInterface actual = isiDAO.getFirstElement();
    assertEquals(isi, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO#getFirstElement()}
   */@Test
  public void testGetFirstElementForInactiveElementsForReleaseA() {
    UserContext.getCurrentUserContext().setShowInactiveStatus(false);
    InformationSystemInterface isi1 = createInterfaceWithIsrAndTcr();
    isi1.getInformationSystemReleaseA().setTypeOfStatus(TypeOfStatus.INACTIVE);
    commit();

    InformationSystemInterface actual = isiDAO.getFirstElement();
    assertNull(actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO#getFirstElement()}
   */@Test
  public void testGetFirstElementForInactiveElementsForReleaseB() {
    UserContext.getCurrentUserContext().setShowInactiveStatus(false);
    InformationSystemInterface isi1 = createInterfaceWithIsrAndTcr();
    isi1.getInformationSystemReleaseB().setTypeOfStatus(TypeOfStatus.INACTIVE);
    commit();

    InformationSystemInterface actual = isiDAO.getFirstElement();
    assertNull(actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO#getFirstElement()}
   */@Test
  public void testGetFirstElementCaseNull() {
    InformationSystemInterface actual = isiDAO.getFirstElement();
    assertNull(actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO#getMatchesAndCountForISI(java.lang.String[], int, int)}
   */@Test
  public void testGetMatchesAndCountForISI() {
    InformationSystemInterface isi = createInterfaceWithIsrAndTcr();
    commit();

    String[] searchTerm = new String[2];
    searchTerm[0] = TEST_IS_NAME_A;
    searchTerm[1] = TEST_IS_NAME_B;

    PagedListHolder<InformationSystemInterface> actualMatchesList = isiDAO.getMatchesAndCountForISI(searchTerm);
    List<InformationSystemInterface> actual = actualMatchesList.getSource();
    List<InformationSystemInterface> expected = arrayList();
    expected.add(isi);

    assertEquals(expected, actual);
  }
   @Test
  public void testUpdateFrom() {
    TechnicalComponent tc1 = testDataHelper.createTechnicalComponent("tc1", true, true);
    InformationSystemInterface c1 = createTestInterface(tc1);

    BusinessObject bo = testDataHelper.createBusinessObject("BO-1", "");
    Transport t1 = BuildingBlockFactory.createTransport();
    t1.setId(Integer.valueOf(-10));
    t1.setBusinessObject(bo);
    t1.setDirection(Direction.FIRST_TO_SECOND);
    c1.addTransport(t1);

    BusinessObject bo2 = testDataHelper.createBusinessObject("BO-2", "");
    Transport t2 = BuildingBlockFactory.createTransport();
    t2.setId(Integer.valueOf(-20));
    t2.setBusinessObject(bo2);
    t2.setDirection(Direction.SECOND_TO_FIRST);
    c1.addTransport(t2);

    commit();
    beginTransaction();

    informationSystemInterfaceService.saveOrUpdate(c1);
    commit();

    beginTransaction();

    assertNotNull(c1);
    assertEquals("X # 1", c1.getInformationSystemReleaseA().getNonHierarchicalName());
    assertEquals("Y # 1", c1.getInformationSystemReleaseB().getNonHierarchicalName());
    assertEquals("new connection", c1.getDescription());
    assertEquals(1, c1.getTechnicalComponentReleases().size());
    assertEquals(2, c1.getTransports().size());
    List<String> info = c1.getTransportInfos();
    assertEquals("-> BO-1 ->", info.get(0));
    assertEquals("<- BO-2 <-", info.get(1));

    rollback();
    beginTransaction();
  }
   @Test
  public void testValidate() {
    TechnicalComponent tc1 = testDataHelper.createTechnicalComponent("tc1", false, true);

    commit();
    beginTransaction();
    try {
      createTestInterface(tc1);
      fail();
    } catch (IteraplanException t) {
      assertEquals(IteraplanErrorMessages.TECHNICALCOMPONENT_NOT_AVAILABLE_FOR_CONNECTION, t.getErrorCode());
    }
  }
   
  private InformationSystemInterface createTestInterface(TechnicalComponent techComponent) {
    InformationSystem is1 = testDataHelper.createInformationSystem("X");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is1, "1");

    InformationSystem is2 = testDataHelper.createInformationSystem("Y");
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is2, "1");

    TechnicalComponentRelease tcr1 = testDataHelper.createTCRelease(techComponent, "version1", true);
    return testDataHelper.createInformationSystemInterface(isr1, isr2, tcr1, "new connection");
  }

  private InformationSystemInterface createInterfaceWithIsrAndTcr() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
    TechnicalComponentRelease release = testDataHelper.createTCRelease(tc, BETA_VERSION, true);

    InformationSystem isA = testDataHelper.createInformationSystem(TEST_IS_NAME_A);
    InformationSystemRelease releaseA = testDataHelper.createInformationSystemRelease(isA, BETA_VERSION);

    InformationSystem isB = testDataHelper.createInformationSystem(TEST_IS_NAME_B);
    InformationSystemRelease releaseB = testDataHelper.createInformationSystemRelease(isB, BETA_VERSION);

    return testDataHelper.createInformationSystemInterface(releaseA, releaseB, release, TEST_DESCRIPTION);
  }

}

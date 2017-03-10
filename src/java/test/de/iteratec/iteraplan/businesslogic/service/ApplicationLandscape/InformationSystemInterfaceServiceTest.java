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
import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.SealState;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO;
import de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO;


/**
 * Tests the service methods of the {@link InformationSystemInterfaceService} interface.
 * 
 * @author mma
 */
public class InformationSystemInterfaceServiceTest extends BaseTransactionalTestSupport {

  private static final String               TEST_IS_NAME_1                = "firstTestIS";
  private static final String               TEST_IS_NAME_2                = "secTestIS";
  private static final String               TEST_TC_NAME                  = "testTC";
  private static final String               ALPHA_VERSION                 = "Alpha";
  private static final String               BETA_VERSION                  = "Beta";
  private static final String               TEST_DESCRIPTION              = "testDescription";

  @Autowired
  private InformationSystemInterfaceService isiService                    = null;
  @Autowired
  private InformationSystemInterfaceDAO     informationSystemInterfaceDAO = null;
  @Autowired
  private InformationSystemReleaseDAO       informationSystemReleaseDAO   = null;
  @Autowired
  private TestDataHelper2                   testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceServiceImpl#deleteEntity(de.iteratec.iteraplan.model.InformationSystemInterface)}
   * The method tests if deleteEntity() deletes correctly an InformationSystemInterface from the database.
   */
  @Test
  public void testDeleteEntity() {
    // create data
    InformationSystem firstTestIS = testDataHelper.createInformationSystem(TEST_IS_NAME_1);
    InformationSystemRelease firstTestISR = testDataHelper.createInformationSystemRelease(firstTestIS, BETA_VERSION);

    InformationSystem secTestIS = testDataHelper.createInformationSystem(TEST_IS_NAME_2);
    InformationSystemRelease secTestISR = testDataHelper.createInformationSystemRelease(secTestIS, ALPHA_VERSION);

    TechnicalComponent testTC = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
    TechnicalComponentRelease testTCR = testDataHelper.createTCRelease(testTC, BETA_VERSION, true);

    InformationSystemInterface testISI = testDataHelper.createInformationSystemInterface(firstTestISR, secTestISR, testTCR, TEST_DESCRIPTION);
    commit();

    // create associations
    beginTransaction();
    Integer id = testISI.getId();
    testISI = informationSystemInterfaceDAO.loadObjectById(id);
    isiService.deleteEntity(testISI);
    PagedListHolder<InformationSystemInterface> real = isiService.getInformationSystemInterfacesBySearch(testISI, 0, 10);
    commit();

    List<InformationSystemInterface> expected = arrayList();
    List<InformationSystemInterface> actual = real.getSource();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceServiceImpl#getInformationSystemReleasesWithConnections(java.lang.Integer, java.lang.Boolean)}
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceServiceImpl#getDao()}
   * The method tests if the getInformationSystemReleasesWithConnections() returns a correct list
   * with all the InformationSystemReleases witch have connections.
   */
  @Test
  public void testGetInformationSystemReleasesWithConnections() {
    // create data
    InformationSystem firstTestIS = testDataHelper.createInformationSystem(TEST_IS_NAME_1);
    InformationSystemRelease firstTestISR = testDataHelper.createInformationSystemRelease(firstTestIS, BETA_VERSION);

    InformationSystem secTestIS = testDataHelper.createInformationSystem(TEST_IS_NAME_2);
    InformationSystemRelease secTestISR = testDataHelper.createInformationSystemRelease(secTestIS, ALPHA_VERSION);

    InformationSystemRelease thirdTestISR = testDataHelper.createInformationSystemRelease(secTestIS, BETA_VERSION);

    TechnicalComponent testTC = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
    TechnicalComponentRelease testTCR = testDataHelper.createTCRelease(testTC, BETA_VERSION, true);

    InformationSystemInterface testISI = testDataHelper.createInformationSystemInterface(firstTestISR, secTestISR, testTCR, TEST_DESCRIPTION);
    commit();

    beginTransaction();
    // need to test getDao()
    InformationSystemInterface root = isiService.getFirstElement();
    testISI.addReleaseB(thirdTestISR);
    Integer actualRootId = root.getId();
    commit();

    beginTransaction();
    Integer id = secTestISR.getId();
    List<InformationSystemRelease> actual = isiService.getInformationSystemReleasesWithConnections(id, true);
    commit();

    List<InformationSystemRelease> expected = arrayList();
    expected.add(firstTestISR);
    expected.add(secTestISR);

    // need to test getDao()
    Integer expectedRootId = Integer.valueOf(18);

    assertEquals(expected, actual);
    assertEquals(expectedRootId, actualRootId);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceServiceImpl#getNamedIdsForConnectionsOfInformationSystemRelease(java.lang.Integer)}
   * The method tests if the getNamedIdsForConnectionsOfInformationSystemRelease()
   */
  @Test
  public void testGetNamedIdsForConnectionsOfInformationSystemRelease() {
    // create data
    InformationSystem firstTestIS = testDataHelper.createInformationSystem(TEST_IS_NAME_1);
    InformationSystemRelease firstTestISR = testDataHelper.createInformationSystemRelease(firstTestIS, ALPHA_VERSION);
    firstTestISR.setDescription(TEST_DESCRIPTION);

    InformationSystemRelease secTestISR = testDataHelper.createInformationSystemRelease(firstTestIS, BETA_VERSION);
    secTestISR.setDescription(TEST_DESCRIPTION);

    InformationSystem secTestIS = testDataHelper.createInformationSystem(TEST_IS_NAME_2);
    InformationSystemRelease thirdTestISR = testDataHelper.createInformationSystemRelease(secTestIS, "Gama");
    thirdTestISR.setDescription(TEST_DESCRIPTION);

    TechnicalComponent testTC = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
    TechnicalComponentRelease testTCR = testDataHelper.createTCRelease(testTC, BETA_VERSION, true);

    InformationSystemInterface firstTestISI = testDataHelper.createInformationSystemInterface(firstTestISR, secTestISR, testTCR,
        "Description maximum 30 signs !");

    InformationSystemInterface secTestISI = testDataHelper.createInformationSystemInterface(thirdTestISR, secTestISR, testTCR, null);

    InformationSystemInterface thirdTestISI = testDataHelper.createInformationSystemInterface(secTestISR, thirdTestISR, testTCR,
        "Description maximum 30 signs ! ");

    BusinessObject testBO = testDataHelper.createBusinessObject("testBO", TEST_DESCRIPTION);
    Transport testTransport = testDataHelper.createTransport(testBO, firstTestISI, Direction.BOTH_DIRECTIONS);
    commit();

    beginTransaction();
    secTestISI.addTransport(testTransport);
    thirdTestISI.addTransport(testTransport);

    informationSystemInterfaceDAO.saveOrUpdate(secTestISI);
    informationSystemInterfaceDAO.saveOrUpdate(thirdTestISI);

    Role r = new Role();
    r.setRoleName("SUPERVISOR_ROLE_NAME");
    Set<Role> rolesSet = hashSet();
    rolesSet.add(r);

    Locale l = new Locale("BG", "Deutschland");
    User u = new User();

    UserContext uc = new UserContext("PenchoMamin", rolesSet, l, u);
    uc.setShowInactiveStatus(false);
    UserContext.setCurrentUserContext(uc);

    firstTestISR.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.INACTIVE);
    informationSystemReleaseDAO.saveOrUpdate(firstTestISR);

    Integer id = secTestISR.getId();
    List<NamedId> actual = isiService.getNamedIdsForConnectionsOfInformationSystemRelease(id);
    commit();

    List<NamedId> expected = arrayList();
    NamedId firstNameId = new NamedId(Integer.valueOf(19), "secTestIS # Gama", TEST_DESCRIPTION);
    expected.add(firstNameId);

    NamedId secNameId = new NamedId(Integer.valueOf(20), "secTestIS # Gama  (Description maximum 30 signs !...)", TEST_DESCRIPTION);
    expected.add(secNameId);

    Collections.sort(expected);

    assertEquals(expected.toString(), actual.toString());

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceServiceImpl#getInformationSystemInterfacesBySearch(de.iteratec.iteraplan.model.InformationSystemInterface, int, int)}
   * The method tests if the getInformationSystemInterfacesBySearch() returns correct list with
   * InformationSystemInterfaces.
   */
  @Test
  public void testGetInformationSystemInterfacesBySearch() {
    // create data
    InformationSystem firstTestIS = testDataHelper.createInformationSystem(TEST_IS_NAME_1);
    InformationSystemRelease firstTestISR = testDataHelper.createInformationSystemRelease(firstTestIS, BETA_VERSION);

    InformationSystem secTestIS = testDataHelper.createInformationSystem(TEST_IS_NAME_2);
    InformationSystemRelease secTestISR = testDataHelper.createInformationSystemRelease(secTestIS, ALPHA_VERSION);

    TechnicalComponent testTC = testDataHelper.createTechnicalComponent(TEST_TC_NAME, true, true);
    TechnicalComponentRelease testTCR = testDataHelper.createTCRelease(testTC, BETA_VERSION, true);

    InformationSystemInterface testISI = testDataHelper.createInformationSystemInterface(firstTestISR, secTestISR, testTCR, TEST_DESCRIPTION);
    commit();

    beginTransaction();
    // get Unit by search
    // search for InformationSystemInterface
    // the first page is 0
    // max number of elements 10
    PagedListHolder<InformationSystemInterface> actual = isiService.getInformationSystemInterfacesBySearch(testISI, 0, 10);
    commit();

    List<InformationSystemInterface> expected = arrayList();
    expected.add(testISI);

    List<InformationSystemInterface> actualList = actual.getSource();
    assertEquals(expected.size(), actualList.size());
    assertEquals(expected, actualList);
  }

  @Test
  public void testSaveOrUpdateEntity() {
    final InformationSystem isr1 = testDataHelper.createInformationSystem("ipu1");
    final InformationSystemRelease firstIsr = testDataHelper.createInformationSystemRelease(isr1, "1.1", TEST_DESCRIPTION, "01.01.2007",
        "10.10.2007", TypeOfStatus.CURRENT);
    firstIsr.setSealState(SealState.VALID);
    final InformationSystem isr2 = testDataHelper.createInformationSystem("ipu2");
    final InformationSystemRelease secondIsr = testDataHelper.createInformationSystemRelease(isr2, "2.2", TEST_DESCRIPTION, "01.01.2007",
        "10.10.2007", TypeOfStatus.CURRENT);
    secondIsr.setSealState(SealState.OUTDATED);
    final TechnicalComponent catalogItem = testDataHelper.createTechnicalComponent("cat1", true, true);
    final TechnicalComponentRelease catalogItemRelease = testDataHelper.createTCRelease(catalogItem, "3.3", TEST_DESCRIPTION, "03.03.2007",
        "04.04.2007", de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.CURRENT, true);
    final InformationSystemInterface conn = testDataHelper
        .createInformationSystemInterface(firstIsr, secondIsr, catalogItemRelease, TEST_DESCRIPTION);

    commit();

    beginTransaction();
    isiService.saveOrUpdate(conn);
    commit();

    assertEquals(SealState.INVALID, firstIsr.getSealState());
    assertEquals(SealState.INVALID, secondIsr.getSealState());
  }

}

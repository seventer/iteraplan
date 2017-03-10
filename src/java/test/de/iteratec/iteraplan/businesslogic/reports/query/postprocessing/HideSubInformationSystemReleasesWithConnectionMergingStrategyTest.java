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
package de.iteratec.iteraplan.businesslogic.reports.query.postprocessing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO;


public class HideSubInformationSystemReleasesWithConnectionMergingStrategyTest extends BaseTransactionalTestSupport {
  private static final Logger         LOGGER  = Logger.getIteraplanLogger(HideSubInformationSystemReleasesWithConnectionMergingStrategyTest.class);
  private static final String         VER_1_1 = "1.1";
  
  @Autowired
  private InformationSystemReleaseDAO informationSystemReleaseDAO;
  @Autowired
  private TestDataHelper2             testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  public Object[] createComplexInformationSystemWithTransport() {
    InformationSystem a = testDataHelper.createInformationSystem("A");
    InformationSystemRelease a1 = testDataHelper
        .createInformationSystemRelease(a, "1", "", null, null, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease a11 = testDataHelper.createInformationSystemRelease(a, VER_1_1, "", null, null,
        InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease a12 = testDataHelper.createInformationSystemRelease(a, "1.2", "", null, null,
        InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease a121 = testDataHelper.createInformationSystemRelease(a, "1.2.1", "", null, null,
        InformationSystemRelease.TypeOfStatus.CURRENT);
    testDataHelper.addChildToIsr(a1, a11);
    testDataHelper.addChildToIsr(a1, a12);
    testDataHelper.addChildToIsr(a12, a121);

    InformationSystem b = testDataHelper.createInformationSystem("B");
    InformationSystemRelease b1 = testDataHelper
        .createInformationSystemRelease(b, "1", "", null, null, InformationSystemRelease.TypeOfStatus.CURRENT);

    InformationSystem c = testDataHelper.createInformationSystem("C");
    InformationSystemRelease c1 = testDataHelper
        .createInformationSystemRelease(c, "1", "", null, null, InformationSystemRelease.TypeOfStatus.CURRENT);

    InformationSystem d = testDataHelper.createInformationSystem("D");
    InformationSystemRelease d1 = testDataHelper
        .createInformationSystemRelease(d, "1", "", null, null, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease d11 = testDataHelper.createInformationSystemRelease(d, VER_1_1, "", null, null,
        InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease d12 = testDataHelper.createInformationSystemRelease(d, "1.2", "", null, null,
        InformationSystemRelease.TypeOfStatus.CURRENT);
    testDataHelper.addChildToIsr(d1, d11);
    testDataHelper.addChildToIsr(d1, d12);

    InformationSystemInterface conn1 = testDataHelper.createInformationSystemInterface(c1, a11, null, null);
    InformationSystemInterface conn2 = testDataHelper.createInformationSystemInterface(d11, a121, null, null);
    InformationSystemInterface conn3 = testDataHelper.createInformationSystemInterface(d1, a11, null, null);
    InformationSystemInterface conn4 = testDataHelper.createInformationSystemInterface(a12, b1, null, null);
    InformationSystemInterface conn5 = testDataHelper.createInformationSystemInterface(d1, a12, null, null);
    InformationSystemInterface conn6 = testDataHelper.createInformationSystemInterface(a12, d12, null, null);
    InformationSystemInterface conn7 = testDataHelper.createInformationSystemInterface(a1, d1, null, null);
    InformationSystemInterface conn8 = testDataHelper.createInformationSystemInterface(a11, d11, null, null);
    testDataHelper.createInformationSystemInterface(a11, a12, null, null);
    testDataHelper.createInformationSystemInterface(d1, a1, null, null);
    testDataHelper.createInformationSystemInterface(d1, c1, null, null);

    BusinessObject u = testDataHelper.createBusinessObject("U", null);
    BusinessObject v = testDataHelper.createBusinessObject("V", null);
    BusinessObject w = testDataHelper.createBusinessObject("W", null);
    BusinessObject x = testDataHelper.createBusinessObject("X", null);
    BusinessObject y = testDataHelper.createBusinessObject("Y", null);
    BusinessObject z = testDataHelper.createBusinessObject("Z", null);

    testDataHelper.createTransport(z, conn1, Direction.FIRST_TO_SECOND);
    testDataHelper.createTransport(x, conn2, Direction.SECOND_TO_FIRST);
    testDataHelper.createTransport(y, conn3, Direction.FIRST_TO_SECOND);
    testDataHelper.createTransport(w, conn4, Direction.FIRST_TO_SECOND);
    testDataHelper.createTransport(x, conn3, Direction.FIRST_TO_SECOND);
    testDataHelper.createTransport(x, conn5, Direction.FIRST_TO_SECOND);
    testDataHelper.createTransport(y, conn5, Direction.FIRST_TO_SECOND);
    testDataHelper.createTransport(y, conn6, Direction.NO_DIRECTION);
    testDataHelper.createTransport(v, conn7, Direction.BOTH_DIRECTIONS);
    testDataHelper.createTransport(u, conn8, Direction.NO_DIRECTION);

    commit();
    return new Object[] { a1, a121, b1, c1, d1, d11, d12, u, v, w, x, y, z };
  }

  @Test
  public void testComplexScenario1WithTransportMerging() throws Exception {
    Object[] obj = createComplexInformationSystemWithTransport();
    InformationSystemRelease a1 = (InformationSystemRelease) obj[0];
    InformationSystemRelease a121 = (InformationSystemRelease) obj[1];
    InformationSystemRelease b1 = (InformationSystemRelease) obj[2];
    InformationSystemRelease c1 = (InformationSystemRelease) obj[3];
    InformationSystemRelease d1 = (InformationSystemRelease) obj[4];
    InformationSystemRelease d11 = (InformationSystemRelease) obj[5];
    BusinessObject u = (BusinessObject) obj[7];
    BusinessObject v = (BusinessObject) obj[8];
    BusinessObject w = (BusinessObject) obj[9];
    BusinessObject x = (BusinessObject) obj[10];
    BusinessObject y = (BusinessObject) obj[11];
    BusinessObject z = (BusinessObject) obj[12];

    beginTransaction();

    List<InformationSystemRelease> newipurResult = loadInformationSystemReleaseList(a121.getId(), c1.getId(), d11.getId());

    InformationSystemRelease ipurA = testInformationSystemReleaseList(newipurResult, a1, 3);

    boolean c1Visited = false;
    boolean c2Visited = false;
    boolean c3Visited = false;

    for (InformationSystemInterface connection : ipurA.getAllConnections()) {
      boolean expectedConnectionFound = false;

      expectedConnectionFound = testSingleTransportConnection(c1Visited, connection, a1.getId(), b1.getId(), w.getName());
      c1Visited = c1Visited || expectedConnectionFound;

      if (!expectedConnectionFound) {
        Integer[] expectedTransportIds = new Integer[] { u.getId(), v.getId(), x.getId(), y.getId() };
        expectedConnectionFound = testMultiTransportConnection(c2Visited, connection, a1.getId(), d1.getId(), expectedTransportIds);
        c2Visited = c2Visited || expectedConnectionFound;
      }

      if (!expectedConnectionFound) {
        expectedConnectionFound = testSingleTransportConnection(c3Visited, connection, c1.getId(), a1.getId(), z.getName());
        c3Visited = c3Visited || expectedConnectionFound;
      }

      if (!expectedConnectionFound) {
        fail("Connections for A1 were not as expected." + "\nfromIpurelesase: " + connection.getInformationSystemReleaseA().getIdentityString()
            + "id from: " + connection.getInformationSystemReleaseA().getId() + "\ntoIpurelease: "
            + connection.getInformationSystemReleaseB().getIdentityString() + "id to: " + connection.getInformationSystemReleaseB().getId()
            + "\na1: " + a1.getIdentityString() + "id a1: " + a1.getId() + "\nb1: " + b1.getIdentityString() + "id b1: " + b1.getId() + "\nc1: "
            + c1.getIdentityString() + "id c1: " + c1.getId() + "\nd1: " + d1.getIdentityString() + "id d1: " + d1.getId());
      }

    }
    assertTrue(c1Visited && c2Visited && c3Visited);

    commit();
  }
  @Test
  public void testComplexScenario2WithTransportMerging() throws Exception {
    Object[] obj = createComplexInformationSystemWithTransport();
    InformationSystemRelease a1 = (InformationSystemRelease) obj[0];
    InformationSystemRelease a121 = (InformationSystemRelease) obj[1];
    InformationSystemRelease c1 = (InformationSystemRelease) obj[3];
    InformationSystemRelease d1 = (InformationSystemRelease) obj[4];
    InformationSystemRelease d11 = (InformationSystemRelease) obj[5];
    BusinessObject z = (BusinessObject) obj[12];

    beginTransaction();

    List<InformationSystemRelease> newipurResult = loadInformationSystemReleaseList(a121.getId(), c1.getId(), d11.getId());
    InformationSystemRelease ipur = testInformationSystemReleaseList(newipurResult, c1, 2);

    boolean c1Visited = false;
    boolean c2Visited = false;
    for (InformationSystemInterface connection : ipur.getAllConnections()) {
      boolean expectedConnectionFound = false;

      expectedConnectionFound = testDeepSingleTransportConnection(c1Visited, connection, c1.getId(), a1.getId(), z.getName());
      c1Visited = c1Visited || expectedConnectionFound;

      if (!expectedConnectionFound) {
        expectedConnectionFound = testEmptyTransportConnection(c2Visited, connection, d1.getId(), c1.getId());
        c2Visited = c2Visited || expectedConnectionFound;
      }

      if (!expectedConnectionFound) {
        fail("Connections for C where not as excepted");
      }

    }
    assertTrue(c1Visited && c2Visited);

    commit();
  }
  @Test
  public void testComplexScenario3WithTransportMerging() throws Exception {
    Object[] obj = createComplexInformationSystemWithTransport();
    InformationSystemRelease a1 = (InformationSystemRelease) obj[0];
    InformationSystemRelease a121 = (InformationSystemRelease) obj[1];
    InformationSystemRelease c1 = (InformationSystemRelease) obj[3];
    InformationSystemRelease d1 = (InformationSystemRelease) obj[4];
    InformationSystemRelease d11 = (InformationSystemRelease) obj[5];
    BusinessObject u = (BusinessObject) obj[7];
    BusinessObject v = (BusinessObject) obj[8];
    BusinessObject x = (BusinessObject) obj[10];
    BusinessObject y = (BusinessObject) obj[11];

    beginTransaction();
    List<InformationSystemRelease> newipurResult = loadInformationSystemReleaseList(a121.getId(), c1.getId(), d11.getId());

    InformationSystemRelease ipur = testInformationSystemReleaseList(newipurResult, d1, 2);

    boolean c1Visited = false;
    boolean c2Visited = false;
    for (InformationSystemInterface connection : ipur.getAllConnections()) {
      boolean expectedConnectionFound = false;

      Integer[] expectedTransportIds = new Integer[] { u.getId(), v.getId(), x.getId(), y.getId() };
      expectedConnectionFound = testMultiTransportConnection(c1Visited, connection, a1.getId(), d1.getId(), expectedTransportIds);
      c1Visited = c1Visited || expectedConnectionFound;

      if (!expectedConnectionFound) {
        expectedConnectionFound = testEmptyTransportConnection(c2Visited, connection, d1.getId(), c1.getId());
        c2Visited = c2Visited || expectedConnectionFound;
      }

      if (!expectedConnectionFound) {
        fail("Connections for D where not as excepted");
      }
    }
    assertTrue(c1Visited && c2Visited);

    commit();
  }

  private InformationSystemRelease[] createTwoSimpleInformationSystemRelease(String releaseAName, String releaseBName, String businessObjectName) {
    InformationSystem a = testDataHelper.createInformationSystem(releaseAName);
    InformationSystemRelease a1 = testDataHelper
        .createInformationSystemRelease(a, "1", "", null, null, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease a11 = testDataHelper.createInformationSystemRelease(a, VER_1_1, "", null, null,
        InformationSystemRelease.TypeOfStatus.CURRENT);
    testDataHelper.addChildToIsr(a1, a11);

    InformationSystem b = testDataHelper.createInformationSystem(releaseBName);
    InformationSystemRelease b1 = testDataHelper
        .createInformationSystemRelease(b, "1", "", null, null, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease b11 = testDataHelper.createInformationSystemRelease(b, VER_1_1, "", null, null,
        InformationSystemRelease.TypeOfStatus.CURRENT);
    testDataHelper.addChildToIsr(b1, b11);

    InformationSystemInterface conn1 = testDataHelper.createInformationSystemInterface(a1, b1, null, null);

    BusinessObject w = testDataHelper.createBusinessObject(businessObjectName, null);
    testDataHelper.createTransport(w, conn1, Direction.NO_DIRECTION);
    testDataHelper.addBusinessObjectToInformationSystem(a1, w);
    testDataHelper.addBusinessObjectToInformationSystem(a11, w);
    testDataHelper.addBusinessObjectToInformationSystem(b1, w);
    testDataHelper.addBusinessObjectToInformationSystem(b11, w);

    commit();

    return new InformationSystemRelease[] { a1, b1, a11, b11 };

  }
  @Test
  public void testFourSimpleScenarios() throws Exception {
    String expectReleaseNameA = "A";
    String expectReleaseNameB = "B";
    String expectBusinessObjectName = "W";
    InformationSystemRelease[] allReleases = createTwoSimpleInformationSystemRelease(expectReleaseNameA, expectReleaseNameB, expectBusinessObjectName);
    InformationSystemRelease a1 = allReleases[0];
    InformationSystemRelease b1 = allReleases[1];
    InformationSystemRelease a11 = allReleases[2];
    InformationSystemRelease b11 = allReleases[3];

    beginTransaction();

    List<InformationSystemRelease> newipurResult = null;
    for (int i = 0; i < 4; i++) {
      newipurResult = loadInformationSystemRelease(a1.getId(), a11.getId(), b1.getId(), b11.getId(), i);
      assertEquals(2, newipurResult.size());
      testSimpleInformationSystemRelease(newipurResult, a1, b1, expectReleaseNameA, expectBusinessObjectName, 0);
      testSimpleInformationSystemRelease(newipurResult, a1, b1, expectReleaseNameB, expectBusinessObjectName, 1);
    }

    commit();
  }

  public static void printResults(Collection<InformationSystemRelease> results, Logger out) {
    out.debug("size: " + results.size());
    for (InformationSystemRelease ipur : results) {
      out.debug("---> " + ipur.getNonHierarchicalName());
      for (InformationSystemInterface conn : ipur.getAllConnections()) {
        out.debug("Connection: ");
        out.debug("    from: " + conn.getInformationSystemReleaseA().getNonHierarchicalName());
        out.debug("    to: " + conn.getInformationSystemReleaseB().getNonHierarchicalName());
        for (Transport transport : conn.getTransports()) {
          out.debug("    Transport: " + transport.getBusinessObject().getName());
          out.debug("        direction: " + transport.getDirection());
        }
      }
    }
  }

  private List<InformationSystemRelease> loadInformationSystemReleaseList(Integer id1, Integer id2, Integer id3) {
    InformationSystemRelease a121 = informationSystemReleaseDAO.loadObjectById(id1);
    InformationSystemRelease c1 = informationSystemReleaseDAO.loadObjectById(id2);
    InformationSystemRelease d11 = informationSystemReleaseDAO.loadObjectById(id3);
    Set<InformationSystemRelease> ipurResult = new HashSet<InformationSystemRelease>();
    ipurResult.add(a121);
    ipurResult.add(c1);
    ipurResult.add(d11);

    HideSubInformationSystemReleasesWithConnectionMergingStrategy hsis = new HideSubInformationSystemReleasesWithConnectionMergingStrategy(null);
    List<InformationSystemRelease> newipurResult = new ArrayList<InformationSystemRelease>(hsis.process(ipurResult, null));
    Collections.sort(newipurResult, new HierarchicalEntityCachingComparator<InformationSystemRelease>());

    printResults(newipurResult, LOGGER);
    return newipurResult;
  }

  private InformationSystemRelease testInformationSystemReleaseList(List<InformationSystemRelease> newipurResult, InformationSystemRelease a1,
                                                                    int expectedConnectionSize) {
    InformationSystemRelease ipurA = null;
    for (InformationSystemRelease rel : newipurResult) {
      if (a1.getHierarchicalName().equals(rel.getHierarchicalName())) {
        ipurA = rel;
        break;
      }
    }
    if (ipurA == null) {
      fail("Ipurelease not found after postprocessing");
    }
    assert ipurA != null;

    assertEquals(expectedConnectionSize, ipurA.getAllConnections().size());
    return ipurA;
  }

  /**
   * Checks, if the given ISI {@code connection} is between the ISRs identified by {@code isrId1} and {@code isrId2}.
   * If yes, tests if each of the expected 4 transports in {@code connection} is as expected
   * @param visited
   *          boolean value indicating if this ISI (including {@code isrId1} and {code isrId2} was already tested
   * @param connection
   *          InformationSystemInterface to test
   * @param isrId1
   *          ID of an InformationSystemRelease
   * @param isrId2
   *          ID of an InformationSystemRelease
   * @param expectedTransportIds
   *          IDs of the expected transports contained in {@code connection}
   * @return true if {@code connection} is between {@code isrId1} and {@code isrId2} and was thus visited, false otherwise
   */
  private boolean testMultiTransportConnection(boolean visited, InformationSystemInterface connection, Integer isrId1, Integer isrId2,
                                               Integer expectedTransportIds[]) {
    // isrId1 -> isrId2
    if (connection.getInformationSystemReleaseA().getId().equals(isrId1) && connection.getInformationSystemReleaseB().getId().equals(isrId2)) {
      testMultiTransportList(connection, expectedTransportIds[0], expectedTransportIds[1], expectedTransportIds[2], expectedTransportIds[3], visited,
          false);
      return true;
    }
    // if not isrId1 -> isrId2 then check isrId2 -> isrId1
    else if (connection.getInformationSystemReleaseA().getId().equals(isrId2) && connection.getInformationSystemReleaseB().getId().equals(isrId1)) {
      testMultiTransportList(connection, expectedTransportIds[0], expectedTransportIds[1], expectedTransportIds[2], expectedTransportIds[3], visited,
          true);
      return true;
    }
    return false;
  }

  /**
   * Checks, if the given ISI {@code connection} is between the ISRs identified by {@code isrId1} and {@code isrId2}.
   * If yes, tests if the expected single transport of {@code connection} is there
   * @param visited
   *          boolean value indicating if this ISI (including {@code isrId1} and {code isrId2} was already tested
   * @param connection
   *          InformationSystemInterface to test
   * @param isrId1
   *          ID of an InformationSystemRelease
   * @param isrId2
   *          ID of an InformationSystemRelease
   * @param expectedTransportName
   *          expected name of the transport contained in {@code connection}
   * @return true if {@code connection} is between {@code isrId1} and {@code isrId2} and was thus visited, false otherwise
   */
  private boolean testSingleTransportConnection(boolean visited, InformationSystemInterface connection, Integer isrId1, Integer isrId2,
                                                String expectedTransportName) {
    // isrId1 -> isrId2
    if (connection.getInformationSystemReleaseA().getId().equals(isrId1) && connection.getInformationSystemReleaseB().getId().equals(isrId2)) {
      testSingelTransportSet(connection, expectedTransportName, visited);
      return true;
    }
    // if not isrId1 -> isrId2 then check isrId2 -> isrId1
    else if (connection.getInformationSystemReleaseA().getId().equals(isrId2) && connection.getInformationSystemReleaseB().getId().equals(isrId1)) {
      testSingelTransportSet(connection, expectedTransportName, visited);
      return true;
    }
    return false;
  }

  /**
   * Checks, if the given ISI {@code connection} is between the ISRs identified by {@code isrId1} and {@code isrId2}.
   * If yes, tests if the expected single transport of {@code connection} is as expected.
   * @param visited
   *          boolean value indicating if this ISI (including {@code isrId1} and {code isrId2} was already tested
   * @param connection
   *          InformationSystemInterface to test
   * @param isrId1
   *          ID of an InformationSystemRelease
   * @param isrId2
   *          ID of an InformationSystemRelease
   * @param expectedTransportName
   *          expected name of the transport contained in {@code connection}
   * @return true if {@code connection} is between {@code isrId1} and {@code isrId2} and was thus visited, false otherwise
   */
  private boolean testDeepSingleTransportConnection(boolean visited, InformationSystemInterface connection, Integer isrId1, Integer isrId2,
                                                    String expectedTransportName) {
    // isrId1 -> isrId2
    if (connection.getInformationSystemReleaseA().getId().equals(isrId1) && connection.getInformationSystemReleaseB().getId().equals(isrId2)) {
      testDeepSingleTransportSet(connection, expectedTransportName, visited, false);
      return true;
    }
    // if not isrId1 -> isrId2 then check isrId2 -> isrId1
    else if (connection.getInformationSystemReleaseA().getId().equals(isrId2) && connection.getInformationSystemReleaseB().getId().equals(isrId1)) {
      testDeepSingleTransportSet(connection, expectedTransportName, visited, true);
      return true;
    }
    return false;
  }

  /**
   * Checks, if the given ISI {@code connection} is between the ISRs identified by {@code isrId1} and {@code isrId2}.
   * If yes, tests if {@code connection} doesn't have transports, as expected
   * @param visited
   *          boolean value indicating if this ISI (including {@code isrId1} and {code isrId2} was already tested
   * @param connection
   *          InformationSystemInterface to test
   * @param isrId1
   *          ID of an InformationSystemRelease
   * @param isrId2
   *          ID of an InformationSystemRelease
   * @return true if {@code connection} is between {@code isrId1} and {@code isrId2} and was thus visited, false otherwise
   */
  private boolean testEmptyTransportConnection(boolean visited, InformationSystemInterface connection, Integer isrId1, Integer isrId2) {
    // isrId1 -> isrId2
    if (connection.getInformationSystemReleaseA().getId().equals(isrId1) && connection.getInformationSystemReleaseB().getId().equals(isrId2)) {
      testEmptyTransportSet(connection, visited);
      return true;
    }
    // if not isrId1 -> isrId2 then check isrId2 -> isrId1
    else if (connection.getInformationSystemReleaseA().getId().equals(isrId2) && connection.getInformationSystemReleaseB().getId().equals(isrId1)) {
      testEmptyTransportSet(connection, visited);
      return true;
    }
    return false;
  }

  private void testEmptyTransportSet(InformationSystemInterface connection, boolean visited) {
    assertTrue(connection.getTransports().isEmpty());
    assertFalse(visited);
  }

  private void testSingelTransportSet(InformationSystemInterface connection, String expectName, boolean visited) {
    Set<Transport> transports = connection.getTransports();
    assertEquals(1, transports.size());
    Transport t = (Transport) transports.toArray()[0];
    assertEquals(expectName, t.getBusinessObject().getName());
    assertFalse(visited);
  }

  private void testDeepSingleTransportSet(InformationSystemInterface connection, String expectName, boolean visited, boolean reverse) {
    testSingelTransportSet(connection, expectName, visited);
    Transport transport = (Transport) connection.getTransports().toArray()[0];
    assertTrue(getDirection(transport, reverse));
    assertFalse(getDirection(transport, !reverse));
  }

  private void testMultiTransportList(InformationSystemInterface connection, Integer expectId1, Integer expectId2, Integer expectId3,
                                      Integer expectId4, boolean visited, boolean reverse) {
    List<Transport> transports = new ArrayList<Transport>(connection.getTransports());
    assertEquals(4, transports.size());
    Collections.sort(transports);

    Transport transport = transports.get(0);
    assertEquals(expectId1, transport.getBusinessObject().getId());
    assertFalse(getDirection(transport, reverse));
    assertFalse(getDirection(transport, !reverse));

    transport = transports.get(1);
    assertEquals(expectId2, transport.getBusinessObject().getId());
    assertTrue(getDirection(transport, reverse));
    assertTrue(getDirection(transport, !reverse));

    transport = transports.get(2);
    assertEquals(expectId3, transport.getBusinessObject().getId());
    assertTrue(getDirection(transport, reverse));
    assertTrue(getDirection(transport, !reverse));

    transport = transports.get(3);
    assertEquals(expectId4, transport.getBusinessObject().getId());
    assertFalse(getDirection(transport, reverse));
    assertTrue(getDirection(transport, !reverse));

    assertFalse(visited);
  }

  private boolean getDirection(Transport transport, boolean reverse) {
    boolean firstToSecond = transport.getDirection().isFirstToSecond();
    boolean secondToFirst = transport.getDirection().isSecondToFirst();
    if (reverse) {
      return secondToFirst;
    }
    else {
      return firstToSecond;
    }
  }

  private void testSimpleInformationSystemRelease(List<InformationSystemRelease> actualSystemReleases, InformationSystemRelease actualReleaseA,
                                                  InformationSystemRelease actualReleaseB, String expectedReleaseName,
                                                  String expectedBusinessObjectName, int variant) {
    InformationSystemRelease ipur = null;
    for (InformationSystemRelease rel : actualSystemReleases) {
      if (rel.getHierarchicalName().startsWith(expectedReleaseName)) {
        ipur = rel;
      }
    }
    if (ipur == null) {
      fail("Ipurelease not found");
    }

    assertTrue(ipur.getHierarchicalName().equals(expectedReleaseName + Constants.VERSIONSEP + "1"));
    assertEquals(InformationSystemRelease.TypeOfStatus.CURRENT, ipur.getTypeOfStatus());
    assertEquals(1, ipur.getBusinessObjects().size());
    BusinessObject bo = (BusinessObject) ipur.getBusinessObjects().toArray()[0];
    assertEquals(expectedBusinessObjectName, bo.getName());
    InformationSystemInterface conn = null;
    if (variant == 0) {
      assertEquals(1, ipur.getInterfacesReleaseB().size());
      conn = (InformationSystemInterface) ipur.getInterfacesReleaseB().toArray()[0];
    }
    else {
      assertEquals(1, ipur.getInterfacesReleaseA().size());
      conn = (InformationSystemInterface) ipur.getInterfacesReleaseA().toArray()[0];
    }
    assertEquals(actualReleaseA.getId(), conn.getInformationSystemReleaseA().getId());
    assertEquals(actualReleaseB.getId(), conn.getInformationSystemReleaseB().getId());
    assertEquals(1, conn.getTransports().size());
    Transport t = (Transport) conn.getTransports().toArray()[0];
    assertEquals(Direction.NO_DIRECTION, t.getDirection());
    assertEquals(expectedBusinessObjectName, t.getBusinessObject().getName());
  }

  private List<InformationSystemRelease> loadInformationSystemRelease(Integer releaseA1Id, Integer releaseA11Id, Integer releaseB1Id,
                                                                      Integer releaseB11Id, int variant) {
    HideSubInformationSystemReleasesWithConnectionMergingStrategy hsis = new HideSubInformationSystemReleasesWithConnectionMergingStrategy(null);
    Set<InformationSystemRelease> ipurResult = new HashSet<InformationSystemRelease>();
    if (variant == 0) {
      ipurResult.add(informationSystemReleaseDAO.loadObjectById(releaseA1Id));
      ipurResult.add(informationSystemReleaseDAO.loadObjectById(releaseB1Id));
    }
    else if (variant == 1) {
      ipurResult.add(informationSystemReleaseDAO.loadObjectById(releaseA11Id));
      ipurResult.add(informationSystemReleaseDAO.loadObjectById(releaseB1Id));
    }
    else if (variant == 2) {
      ipurResult.add(informationSystemReleaseDAO.loadObjectById(releaseA1Id));
      ipurResult.add(informationSystemReleaseDAO.loadObjectById(releaseB11Id));
    }
    else {
      ipurResult.add(informationSystemReleaseDAO.loadObjectById(releaseA11Id));
      ipurResult.add(informationSystemReleaseDAO.loadObjectById(releaseB11Id));
    }
    List<InformationSystemRelease> newipurResult = new ArrayList<InformationSystemRelease>(hsis.process(ipurResult, null));
    Collections.sort(newipurResult, new HierarchicalEntityCachingComparator<InformationSystemRelease>());
    return newipurResult;
  }
}

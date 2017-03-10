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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.MockTestHelper;
import de.iteratec.iteraplan.TestAsSuperUser;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.dto.ReleaseSuccessorDTO;
import de.iteratec.iteraplan.model.dto.ReleaseSuccessorDTO.SuccessionContainer;
import de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO;
import de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAO;

public class ReleaseSuccessorServiceImplTest {

  private ReleaseSuccessorServiceImpl relSuccService;
  private InformationSystemReleaseDAO informationSystemReleaseDaoMock;
  private TechnicalComponentReleaseDAO technicalComponentReleaseDaoMock;
  
  @Before
  public void setUp() throws Exception {
    // run as super user in order to avoid initializing all permissions for the requested data
    TestAsSuperUser.createSuperUserInContext();

    relSuccService = new ReleaseSuccessorServiceImpl();
    informationSystemReleaseDaoMock = MockTestHelper.createMock(InformationSystemReleaseDAO.class);
    technicalComponentReleaseDaoMock = MockTestHelper.createMock(TechnicalComponentReleaseDAO.class);
    relSuccService.setInformationSystemReleaseDAO(informationSystemReleaseDaoMock);
    relSuccService.setTechnicalComponentReleaseDAO(technicalComponentReleaseDaoMock);
    
  }

  @SuppressWarnings("boxing")
  @Test
  public void testGetIsReleaseSuccessorDTO() {
    List<InformationSystemRelease> availableList = new ArrayList<InformationSystemRelease>();
    InformationSystem testIs = new InformationSystem();
    testIs.setId(42);
    testIs.setName("testIS");
    
    // create 4 releases as a successor chain
    for (int i = 0; i <= 3; i++) {
      InformationSystemRelease isr = new InformationSystemRelease();
      isr.setId(i);
      isr.setVersion("ver" + i);
      testIs.addRelease(isr);
      if (i >= 1) {
        InformationSystemRelease pred = availableList.get(i - 1);
        isr.addPredecessor(pred);
      }
      availableList.add(isr);
    }
    // the last in the chain has no successor and should be removed from the list; but it must appear in the results later on!
    InformationSystemRelease latestIsr = availableList.remove(availableList.size() - 1);
    
    // create another 2 separate releases
    InformationSystem independentIs = new InformationSystem();
    independentIs.setId(45);
    independentIs.setName("kslkmvlkmfd");
    InformationSystemRelease noAncestorIsr = new InformationSystemRelease();
    noAncestorIsr.setId(11);
    independentIs.addRelease(noAncestorIsr);
    InformationSystemRelease firstSuccIsr = new InformationSystemRelease();
    firstSuccIsr.setId(12);
    firstSuccIsr.setVersion("blub");
    independentIs.addRelease(firstSuccIsr);
    firstSuccIsr.addPredecessor(noAncestorIsr);
    availableList.add(noAncestorIsr);
    
    // test with null ID parameter
    MockTestHelper.expect(this.informationSystemReleaseDaoMock.getReleasesWithSuccessors(true)).andReturn(availableList);
    MockTestHelper.replay(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    ReleaseSuccessorDTO<InformationSystemRelease> isrSuccDto = relSuccService.getIsReleaseSuccessorDTO(null, true);
    
    MockTestHelper.verify(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    assertEquals("Detected a deviation from the list of expected successors", availableList, isrSuccDto.getAvailableReleases());
    Assert.assertTrue("DTO no properly initiatilized", isrSuccDto.isNoQueryExecuted());
    Assert.assertTrue("Succession should be empty", isrSuccDto.getSuccession().isEmpty());
    
    
    // test successors with a real ID
    MockTestHelper.reset(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    MockTestHelper.expect(this.informationSystemReleaseDaoMock.getReleasesWithSuccessors(true)).andReturn(availableList);
    MockTestHelper.expect(informationSystemReleaseDaoMock.loadObjectById(0)).andReturn(availableList.get(0));
    MockTestHelper.replay(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    isrSuccDto = relSuccService.getIsReleaseSuccessorDTO(0, true);
    
    MockTestHelper.verify(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    assertEquals("Detected a deviation from the list of expected successors", availableList, isrSuccDto.getAvailableReleases());
    List<SuccessionContainer<InformationSystemRelease>> successionList = isrSuccDto.getSuccession();
    assertEquals(availableList.get(0), successionList.get(0).getRelease());
    assertEquals(0, successionList.get(0).getLevel());
    assertEquals(availableList.get(1), successionList.get(1).getRelease());
    assertEquals(1, successionList.get(1).getLevel());
    assertEquals(availableList.get(2), successionList.get(2).getRelease());
    assertEquals(2, successionList.get(2).getLevel());
    assertEquals(latestIsr, successionList.get(3).getRelease());
    assertEquals(3, successionList.get(3).getLevel());
    assertEquals(4, successionList.size());

    
    // test successors with an ID not contained in availableReleases
    Integer latestIsrId = latestIsr.getId();
    MockTestHelper.reset(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    MockTestHelper.expect(this.informationSystemReleaseDaoMock.getReleasesWithSuccessors(true)).andReturn(availableList);
    MockTestHelper.expect(informationSystemReleaseDaoMock.loadObjectById(latestIsrId)).andReturn(latestIsr);
    MockTestHelper.replay(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    isrSuccDto = relSuccService.getIsReleaseSuccessorDTO(latestIsrId, true);
    assertEquals(1, isrSuccDto.getSuccession().size());

  
    // test predecessor with an ID not contained in availableReleases
    MockTestHelper.reset(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    MockTestHelper.expect(this.informationSystemReleaseDaoMock.getReleasesWithSuccessors(true)).andReturn(availableList);
    MockTestHelper.expect(informationSystemReleaseDaoMock.loadObjectById(latestIsrId)).andReturn(latestIsr);
    MockTestHelper.replay(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    ReleaseSuccessorDTO<InformationSystemRelease> isrPredDto = relSuccService.getIsReleaseSuccessorDTO(latestIsrId, false);

    List<SuccessionContainer<InformationSystemRelease>> predecessorSuccList = isrPredDto.getSuccession();
    assertEquals(latestIsr, predecessorSuccList.get(0).getRelease());
    assertEquals(0, predecessorSuccList.get(0).getLevel());
    assertEquals(availableList.get(2), predecessorSuccList.get(1).getRelease());
    assertEquals(1, predecessorSuccList.get(1).getLevel());
    assertEquals(availableList.get(1), predecessorSuccList.get(2).getRelease());
    assertEquals(2, predecessorSuccList.get(2).getLevel());
    assertEquals(availableList.get(0), predecessorSuccList.get(3).getRelease());
    assertEquals(3, predecessorSuccList.get(3).getLevel());
    assertEquals(4, predecessorSuccList.size());
}

  @SuppressWarnings("boxing")
  @Test
  public void testGetTcReleaseSuccessorDTO() {
    List<TechnicalComponentRelease> availableList = new ArrayList<TechnicalComponentRelease>();
    TechnicalComponent testTc = new TechnicalComponent();
    testTc.setId(42);
    testTc.setName("testTC");
    
    // create 4 releases as a successor chain
    for (int i = 0; i <= 3; i++) {
      TechnicalComponentRelease isr = new TechnicalComponentRelease();
      isr.setId(i);
      isr.setVersion("ver" + i);
      testTc.addRelease(isr);
      if (i >= 1) {
        TechnicalComponentRelease pred = availableList.get(i - 1);
        isr.addPredecessor(pred);
      }
      availableList.add(isr);
    }
    // the last in the chain has no successor and should be removed from the list; but it must appear in the results later on!
    TechnicalComponentRelease latestIsr = availableList.remove(availableList.size() - 1);
    
    // create another 2 separate releases
    TechnicalComponent independentTc = new TechnicalComponent();
    independentTc.setId(45);
    independentTc.setName("kslkmvlkmfd");
    TechnicalComponentRelease noAncestorTcr = new TechnicalComponentRelease();
    noAncestorTcr.setId(11);
    independentTc.addRelease(noAncestorTcr);
    TechnicalComponentRelease firstSuccIsr = new TechnicalComponentRelease();
    firstSuccIsr.setId(12);
    firstSuccIsr.setVersion("blub");
    independentTc.addRelease(firstSuccIsr);
    firstSuccIsr.addPredecessor(noAncestorTcr);
    availableList.add(noAncestorTcr);
    
    // test with null ID parameter
    MockTestHelper.expect(technicalComponentReleaseDaoMock.getReleasesWithSuccessors(true)).andReturn(availableList);
    MockTestHelper.replay(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    ReleaseSuccessorDTO<TechnicalComponentRelease> isrSuccDto = relSuccService.getTcReleaseSuccessorDTO(null, true);
    
    MockTestHelper.verify(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    assertEquals("Detected a deviation from the list of expected successors", availableList, isrSuccDto.getAvailableReleases());
    Assert.assertTrue("DTO no properly initiatilized", isrSuccDto.isNoQueryExecuted());
    Assert.assertTrue("Succession should be empty", isrSuccDto.getSuccession().isEmpty());
    
    
    // test successors with a real ID
    MockTestHelper.reset(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    MockTestHelper.expect(technicalComponentReleaseDaoMock.getReleasesWithSuccessors(true)).andReturn(availableList);
    MockTestHelper.expect(technicalComponentReleaseDaoMock.loadObjectById(0)).andReturn(availableList.get(0));
    MockTestHelper.replay(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    isrSuccDto = relSuccService.getTcReleaseSuccessorDTO(0, true);
    
    MockTestHelper.verify(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    assertEquals("Detected a deviation from the list of expected successors", availableList, isrSuccDto.getAvailableReleases());
    List<SuccessionContainer<TechnicalComponentRelease>> successionList = isrSuccDto.getSuccession();
    assertEquals(availableList.get(0), successionList.get(0).getRelease());
    assertEquals(0, successionList.get(0).getLevel());
    assertEquals(availableList.get(1), successionList.get(1).getRelease());
    assertEquals(1, successionList.get(1).getLevel());
    assertEquals(availableList.get(2), successionList.get(2).getRelease());
    assertEquals(2, successionList.get(2).getLevel());
    assertEquals(latestIsr, successionList.get(3).getRelease());
    assertEquals(3, successionList.get(3).getLevel());
    assertEquals(4, successionList.size());

    
    // test successors with an ID not contained in availableReleases
    Integer latestIsrId = latestIsr.getId();
    MockTestHelper.reset(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    MockTestHelper.expect(this.technicalComponentReleaseDaoMock.getReleasesWithSuccessors(true)).andReturn(availableList);
    MockTestHelper.expect(technicalComponentReleaseDaoMock.loadObjectById(latestIsrId)).andReturn(latestIsr);
    MockTestHelper.replay(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    isrSuccDto = relSuccService.getTcReleaseSuccessorDTO(latestIsrId, true);
    assertEquals(1, isrSuccDto.getSuccession().size());

  
    // test predecessor with an ID not contained in availableReleases
    MockTestHelper.reset(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    MockTestHelper.expect(this.technicalComponentReleaseDaoMock.getReleasesWithSuccessors(true)).andReturn(availableList);
    MockTestHelper.expect(technicalComponentReleaseDaoMock.loadObjectById(latestIsrId)).andReturn(latestIsr);
    MockTestHelper.replay(informationSystemReleaseDaoMock, technicalComponentReleaseDaoMock);
    ReleaseSuccessorDTO<TechnicalComponentRelease> isrPredDto = relSuccService.getTcReleaseSuccessorDTO(latestIsrId, false);

    List<SuccessionContainer<TechnicalComponentRelease>> predecessorSuccList = isrPredDto.getSuccession();
    assertEquals(latestIsr, predecessorSuccList.get(0).getRelease());
    assertEquals(0, predecessorSuccList.get(0).getLevel());
    assertEquals(availableList.get(2), predecessorSuccList.get(1).getRelease());
    assertEquals(1, predecessorSuccList.get(1).getLevel());
    assertEquals(availableList.get(1), predecessorSuccList.get(2).getRelease());
    assertEquals(2, predecessorSuccList.get(2).getLevel());
    assertEquals(availableList.get(0), predecessorSuccList.get(3).getRelease());
    assertEquals(3, predecessorSuccList.get(3).getLevel());
    assertEquals(4, predecessorSuccList.size());
  }

}

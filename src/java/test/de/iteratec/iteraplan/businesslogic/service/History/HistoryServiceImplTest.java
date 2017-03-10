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
package de.iteratec.iteraplan.businesslogic.service.History;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.service.HistoryService;
import de.iteratec.iteraplan.businesslogic.service.HistoryServiceImpl;
import de.iteratec.iteraplan.businesslogic.service.UserService;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.persistence.dao.HistoryDAO;
import de.iteratec.iteraplan.persistence.history.BuildingBlockRevision;
import de.iteratec.iteraplan.presentation.dialog.History.HistoryResultsPage;


public class HistoryServiceImplTest {

  private HistoryDAO     historyDAO;
  private HistoryService historyService;

  @Before
  public void setUp() throws Exception {
    historyDAO = EasyMock.createMock(HistoryDAO.class);
    // it's enough if Userservice mock returns null all the time --> nice mock
    UserService userServiceMock = EasyMock.createNiceMock(UserService.class);
    historyService = new HistoryServiceImpl(historyDAO, userServiceMock);
  }

  /**
   * Just tests if the method returns true after set to true
   */
  @SuppressWarnings("boxing")
  @Test
  public void testServiceHistoryEnabled() {
    historyService.setHistoryEnabled(true);

    // set mock expectations
    historyDAO.setHistoryEnabled(true);
    EasyMock.expectLastCall();
    EasyMock.expect(historyDAO.isHistoryEnabled()).andReturn(false);

    assertFalse(historyService.isHistoryEnabled());
  }

  /**
   * Check if call thereto is forwarded to DAO
   */
  @SuppressWarnings("boxing")
  @Test
  public void testServiceLocalHistories() {
    List<BuildingBlockRevision<BusinessUnit>> revisions = Lists.newArrayList();
    EasyMock.expect(historyDAO.getRevisionBounded(BusinessUnit.class, 1, 2, 3, null, null)).andReturn(revisions);
    EasyMock.expect(historyDAO.getHistoryLengthFor(BusinessUnit.class, 1, null, null)).andReturn(1);
    EasyMock.replay(historyDAO);

    HistoryResultsPage hrpReturned = historyService.getLocalHistoryPage(BusinessUnit.class, 1, 2, 3, null, null);
    assertEquals(1, hrpReturned.getNumberResults());
    assertEquals(2, hrpReturned.getCurPage());
    assertEquals(3, hrpReturned.getResultsPerPage());
    assertEquals(Collections.emptyList(), hrpReturned.getBbChangesets());

    EasyMock.verify(historyDAO);
    EasyMock.reset(historyDAO);
  }

}
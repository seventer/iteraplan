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
package de.iteratec.iteraplan.presentation.dialog.History;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import de.iteratec.iteraplan.TestAsSuperUser;
import de.iteratec.iteraplan.businesslogic.service.HistoryService;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.dialog.GuiSearchControllerTestHelper;


/**
 * Test of the history controller
 * 
 * @author rge
 */
public class HistoryControllerTest extends GuiSearchControllerTestHelper {

  private MockHttpServletResponse        response;
  private HistoryController              controller;
  private AnnotationMethodHandlerAdapter adapter;
  private HistoryService                 historyService;

  @Override
  @Before
  public void setUp() {

    super.setUp();
    historyService = EasyMock.createMock(HistoryService.class);
    adapter = new AnnotationMethodHandlerAdapter();
    response = new MockHttpServletResponse();
    controller = new HistoryController(historyService);
    GuiContext guiContext = new GuiContext();
    GuiContext.setCurrentGuiContext(guiContext);

    TestAsSuperUser.createSuperUserInContext();
  }

  @After
  public void tearDown() {
    GuiContext.detachCurrentGuiContext();
    TestAsSuperUser.clearUserContext();
  }

  @Test
  public void testControllerSameInAsOut() throws Exception {
    // Same in as out
    verifyControllerLocalHistory("100", "BusinessUnit", "0", "20", 100, BusinessUnit.class, 0, 20);
    verifyControllerLocalHistory("201", "BusinessFunction", "3", "50", 201, BusinessFunction.class, 3, 50);
  }

  @Test
  public void testControllerChangePageParam() throws Exception {
    // No page param means page=0
    verifyControllerLocalHistory("201", "BusinessFunction", null, "50", 201, BusinessFunction.class, 0, 50);
    // Negative page means page=0
    verifyControllerLocalHistory("201", "BusinessFunction", "-1", "50", 201, BusinessFunction.class, 0, 50);
  }

  @Test
  public void testControllerChangePageSizeParam() throws Exception {
    // No pageSize param means pageSize=-1 (infinite)
    verifyControllerLocalHistory("201", "BusinessFunction", "3", null, 201, BusinessFunction.class, 3, -1);
    // Less than -1 pageSize param means pageSize=-1 (infinite)
    verifyControllerLocalHistory("201", "BusinessFunction", "3", "-99", 201, BusinessFunction.class, 3, -1);
    // ==0 pageSize param means pageSize=-1 (infinite)
    verifyControllerLocalHistory("201", "BusinessFunction", "3", "0", 201, BusinessFunction.class, 3, -1);
    // 1 is a perfectly valid size
    verifyControllerLocalHistory("201", "BusinessFunction", "3", "1", 201, BusinessFunction.class, 3, 1);
  }

  /**
   * Test the controller's LocalHistory method (with the mocked service). URL parameters get passed to the
   * method, and the EXPECTED params are what the controller is expected to pass on to the service.
   */
  @SuppressWarnings("boxing")
  private <T extends BuildingBlock> void verifyControllerLocalHistory(String urlId, String urlBbt, String urlPage, String urlPageSize, int expectId,
                                                                      Class<T> expectClass, int expectPage, int expectPageSize) throws Exception { //NOPMD

    String fromDate = "2010-01-01";
    String toDate = "2011-02-02";

    DateTime dateFrom = DateTime.parse(fromDate);
    // add one day to make the date range inclusive, like the controller does
    DateTime dateTo = DateTime.parse(toDate).plusDays(1);

    // Expected service calls/replies
    HistoryResultsPage resultsPageExpected = new HistoryResultsPage(null, 1, 20, 0);
    EasyMock.expect(
        historyService.getLocalHistoryPage(expectClass, expectId, expectPage, expectPageSize, dateFrom, dateTo))
        .andReturn(resultsPageExpected);
    EasyMock.expect(historyService.isHistoryEnabled()).andReturn(true);
    EasyMock.replay(historyService);

    // call URL (routed to HistoryController)
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/local.do");
    request.addParameter("id", urlId);
    request.addParameter("buildingBlockType", urlBbt);
    request.addParameter("page", urlPage);
    request.addParameter("pageSize", urlPageSize);
    request.addParameter("dateFrom", fromDate);
    request.addParameter("dateTo", toDate);
    request.setMethod("GET");
    ModelAndView mv = adapter.handle(request, response, controller);

    // Check result
    HistoryResultsPage resultsPageRcvd = (HistoryResultsPage) mv.getModelMap().get("resultsPage");
    assertEquals(resultsPageRcvd, resultsPageExpected);

    EasyMock.verify(historyService);
    EasyMock.reset(historyService);
  }

}
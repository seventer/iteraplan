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
package de.iteratec.iteraplan.presentation.dialog.InfrastructureElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.SessionConstants;
import de.iteratec.iteraplan.presentation.dialog.GuiSearchControllerTestHelper;


/**
 * JUnit Test of the InfrastructureElementController
 * 
 * @author dzu
 */
public class InfrastructureElementControllerTest extends GuiSearchControllerTestHelper {

  private MockHttpServletRequest          request;
  private MockHttpServletResponse         response;
  private InfrastructureElementController controller;
  private AnnotationMethodHandlerAdapter  adapter;
  private InfrastructureElementService    infrastructureElementService;

  @Override
  @Before
  public void setUp() {
    GuiContext guiContext;
    User user;
    UserContext userContext;
    super.setUp();
    infrastructureElementService = EasyMock.createNiceMock(InfrastructureElementService.class);
    controller = new InfrastructureElementController(infrastructureElementService);
    controller.setBbtService(EasyMock.createNiceMock(BuildingBlockTypeService.class));
    controller.setInitFormHelperService(super.getInitFormHelperService());
    controller.setRefreshHelperService(super.getRefreshHelperService());
    controller.setAttributeTypeService(super.getAttributeTypeService());
    adapter = new AnnotationMethodHandlerAdapter();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    guiContext = new GuiContext();
    GuiContext.setCurrentGuiContext(guiContext);
    user = new User();
    user.setDataSource("MASTER");
    user.setLoginName("system");
    userContext = new UserContext("system", new HashSet<Role>(), Locale.getDefault(), user);
    UserContext.setCurrentUserContext(userContext);
  }

  @Test
  public void testInit() throws Exception {

    List<InfrastructureElement> matches = new ArrayList<InfrastructureElement>();

    EasyMock.expect(infrastructureElementService.getEntityResultsBySearch(new InfrastructureElement())).andReturn(matches);
    EasyMock.replay(infrastructureElementService);
    EasyMock.expect(getAttributeTypeService().getAttributeTypesForTypeOfBuildingBlock(controller.getTob(), true)).andReturn(
        new ArrayList<AttributeType>());
    EasyMock.replay(getAttributeTypeService());

    request.setRequestURI("/infrastructureelement/init.do");
    request.setMethod("GET");
    ModelAndView mv;

    mv = adapter.handle(request, response, controller);
    // Check if controller can handle init.do request
    assertNotNull("expecting a view and model from controller#init()", mv);

    EasyMock.verify(infrastructureElementService, getAttributeTypeService());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSearch() throws Exception {

    // Creates a result MatchesList with one project element
    List<InfrastructureElement> fakeResultList = new ArrayList<InfrastructureElement>(1);

    InfrastructureElement fakeResultInfrastructureElement = new InfrastructureElement();
    fakeResultInfrastructureElement.setId(Integer.valueOf(123));
    fakeResultInfrastructureElement.setName("MyFakeInfrastructureElement");

    fakeResultList.add(fakeResultInfrastructureElement);

    // Controller is expected to search for an empty infrastructure element when page first loads
    InfrastructureElement emptyInfrastructureElement = new InfrastructureElement();
    EasyMock.expect(infrastructureElementService.getEntityResultsBySearch(emptyInfrastructureElement)).andReturn(fakeResultList);
    EasyMock.replay(infrastructureElementService);
    EasyMock.expect(getAttributeTypeService().getAttributeTypesForTypeOfBuildingBlock(controller.getTob(), true)).andReturn(
        new ArrayList<AttributeType>());
    EasyMock.replay(getAttributeTypeService());

    request.setRequestURI("/infrastructureelement/search.do");
    request.setMethod("GET");
    ModelAndView mv;

    mv = adapter.handle(request, response, controller);

    // Check if controller has handled /search.do request correctly
    assertEquals("redirect:/infrastructureelement/init.do", mv.getViewName());

    request.setRequestURI("/infrastructureelement/init.do");
    request.setMethod("GET");
    mv = adapter.handle(request, response, controller);

    PagedListHolder<InfrastructureElement> returnedMatchesList = (PagedListHolder<InfrastructureElement>) mv.getModel().get(
        SessionConstants.MVC_SEARCH_RESULT_LIST);

    // Check if model has correct MatchesList
    assertEquals(fakeResultList, returnedMatchesList.getSource());
    assertEquals(1, returnedMatchesList.getNrOfElements());
    assertTrue("Controller should find dummy infrastructure element.", returnedMatchesList.getSource().contains(fakeResultInfrastructureElement));

    EasyMock.verify(infrastructureElementService, getAttributeTypeService());
  }
}

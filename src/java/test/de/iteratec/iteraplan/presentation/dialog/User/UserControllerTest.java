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
package de.iteratec.iteraplan.presentation.dialog.User;

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

import de.iteratec.iteraplan.businesslogic.service.UserService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.SessionConstants;


public class UserControllerTest {

  private MockHttpServletRequest         request;
  private MockHttpServletResponse        response;
  private UserController                 controller;
  private AnnotationMethodHandlerAdapter adapter;
  private UserService                    userService;

  @Before
  public void setUp() {
    GuiContext guiContext;
    User user;
    UserContext userContext;
    userService = EasyMock.createNiceMock(UserService.class);
    adapter = new AnnotationMethodHandlerAdapter();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    controller = new UserController(userService);
    guiContext = new GuiContext();
    GuiContext.setCurrentGuiContext(guiContext);

    user = new User();
    user.setDataSource("MASTER");
    user.setLoginName("system");
    userContext = new UserContext("system", new HashSet<Role>(), Locale.getDefault(), user);
    UserContext.setCurrentUserContext(userContext);
  }

  @Test
  public void init() throws Exception { // NOPMD
    List<User> matches = new ArrayList<User>();
    // expect a search with a user object of that type
    EasyMock.expect(userService.getUserBySearch(new User())).andReturn(matches);
    EasyMock.replay(userService);

    request.setRequestURI("/init.do");
    request.setMethod("GET");
    ModelAndView mv;
    mv = adapter.handle(request, response, controller);
    assertNotNull("expecting a view and model from controller#init()", mv);

    EasyMock.verify(userService);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void search() throws Exception { // NOPMD

    List<User> users = new ArrayList<User>(1);
    User testUser = new User();
    testUser.setLoginName("Dummy");
    users.add(testUser);

    User searchUser = new User();
    // expect a search with a user object of that type
    EasyMock.expect(userService.getUserBySearch(searchUser)).andReturn(users);
    // EasyMock.expectLastCall().once();

    EasyMock.replay(userService);
    request.setRequestURI("/search.do");
    request.setMethod("GET");

    ModelAndView mv = adapter.handle(request, response, controller);

    assertEquals("redirect:/user/init.do", mv.getViewName());

    request.setRequestURI("/user/init.do");
    request.setMethod("GET");
    mv = adapter.handle(request, response, controller);

    PagedListHolder<User> returnedUsers = (PagedListHolder<User>) mv.getModel().get(SessionConstants.MVC_SEARCH_RESULT_LIST);
    assertEquals(users, returnedUsers.getSource());
    assertEquals(1, returnedUsers.getNrOfElements());
    assertTrue("controller should find dummy user", returnedUsers.getSource().contains(testUser));

    EasyMock.verify(userService);
  }

}
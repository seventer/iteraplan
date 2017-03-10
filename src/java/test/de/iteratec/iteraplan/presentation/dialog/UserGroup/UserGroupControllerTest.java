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
package de.iteratec.iteraplan.presentation.dialog.UserGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.businesslogic.service.QueryService;
import de.iteratec.iteraplan.businesslogic.service.UserGroupService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.SessionConstants;


public class UserGroupControllerTest {

  private MockHttpServletRequest         request;
  private MockHttpServletResponse        response;
  private UserGroupController            controller;
  private AnnotationMethodHandlerAdapter adapter;
  private UserGroupService               userGroupService;

  @Before
  public void setUp() {
    GuiContext guiContext;
    User user;
    UserContext userContext;
    userGroupService = EasyMock.createNiceMock(UserGroupService.class);
    adapter = new AnnotationMethodHandlerAdapter();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    controller = new UserGroupController(userGroupService);
    controller.setQueryService(EasyMock.createNiceMock(QueryService.class));
    controller.setInitFormHelperService(EasyMock.createNiceMock(InitFormHelperService.class));
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

    List<UserGroup> matches = new ArrayList<UserGroup>();

    EasyMock.expect(userGroupService.getUserGroupsBySearch(new UserGroup())).andReturn(matches);
    EasyMock.replay(userGroupService);

    request.setRequestURI("/init.do");
    request.setMethod("GET");
    ModelAndView mv;

    mv = adapter.handle(request, response, controller);
    Assert.assertNotNull("expecting a view and model from controller#init()", mv);

    EasyMock.verify(userGroupService);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSearch() throws Exception {
    UserGroup group = new UserGroup();
    group.setId(Integer.valueOf(23));
    group.setName("MyGroup");
    List<UserGroup> groupList = new ArrayList<UserGroup>(1);
    groupList.add(group);

    // instruct mock service what to return
    UserGroup criteriaGroup = new UserGroup();
    EasyMock.expect(userGroupService.getUserGroupsBySearch(criteriaGroup)).andReturn(groupList);
    EasyMock.replay(userGroupService);

    request.setRequestURI("/search.do");
    request.setMethod("GET");
    ModelAndView mv;

    mv = adapter.handle(request, response, controller);

    Assert.assertEquals("redirect:/usergroup/init.do", mv.getViewName());

    request.setRequestURI("/usergroup/init.do");
    request.setMethod("GET");
    mv = adapter.handle(request, response, controller);

    PagedListHolder<UserGroup> returnedGroup = (PagedListHolder<UserGroup>) mv.getModel().get(SessionConstants.MVC_SEARCH_RESULT_LIST);
    Assert.assertEquals(groupList, returnedGroup.getSource());
    Assert.assertEquals(1, returnedGroup.getNrOfElements());
    Assert.assertTrue("controller should find dummy user group", returnedGroup.getSource().contains(group));

    EasyMock.verify(userGroupService);
  }

}

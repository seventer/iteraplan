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
package de.iteratec.iteraplan.presentation.dialog.Configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import de.iteratec.iteraplan.businesslogic.service.ElasticeamService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.memory.DialogMemory;


public class ConfigurationControllerTest {

  private static final Logger            LOGGER    = Logger.getIteraplanLogger(ConfigurationControllerTest.class);

  private MockHttpServletRequest         request;
  private MockHttpServletResponse        response;
  private ConfigurationController        controller;
  private AnnotationMethodHandlerAdapter adapter;
  private UserContext                    userContext;
  private User                           user;

  private static final String            USER_NAME = "system";

  @Before
  public void onSetUp() {
    adapter = new AnnotationMethodHandlerAdapter();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    controller = new ConfigurationController();

    // provide the mocked service to the @Autowired field
    ElasticeamService elasticeamService = EasyMock.createNiceMock(ElasticeamService.class);
    ReflectionTestUtils.setField(controller, "elasticService", elasticeamService);

    Role role = new Role();
    role.setRoleName(Role.SUPERVISOR_ROLE_NAME);
    Set<Role> roles = new HashSet<Role>();
    roles.add(role);

    user = new User();
    user.setDataSource("MASTER");
    user.setLoginName(USER_NAME);
    userContext = new UserContext(USER_NAME, roles, Locale.getDefault(), user);
    UserContext.setCurrentUserContext(userContext);

    GuiContext guiContext = new GuiContext();
    GuiContext.setCurrentGuiContext(guiContext);
  }

  @Test
  public void testInit() {
    request.setMethod("GET");
    request.setRequestURI("/init.do");

    ModelAndView mv;
    try {
      mv = adapter.handle(request, response, controller);
      ConfigurationDialogMemory dialogMemory = (ConfigurationDialogMemory) mv.getModel().get("dialogMemory");
      checkDialogMemPresenceInGuiCtx();
      assertNotNull("Controller must put a dialog memory to the model", dialogMemory);
      assertEquals(userContext.getDataSource(), dialogMemory.getSelectedDataSource());
      assertEquals(userContext.isShowInactiveStatus(), dialogMemory.isShowInactive());
    } catch (Exception e) {
      LOGGER.error(e);
    }
  }

  @Test
  public void testSaveConfiguration() {
    boolean expectedState = false;

    request.setParameter("showInactive", String.valueOf(expectedState));
    request.setMethod("GET");
    request.setRequestURI("/saveConfiguration.do");

    try {
      @SuppressWarnings("unused")
      ModelAndView mv = adapter.handle(request, response, controller);
      checkDialogMemPresenceInGuiCtx();
      assertEquals(expectedState, userContext.isShowInactiveStatus());
      // go to standard view. This test currently doesnt work as controller.getResetPage is always
      // null, because the springmvc-servlet.xml is not applied
      // assertNotSame(controller.getResetPage(), mv.getViewName());
    } catch (Exception e) {
      throw new IteraplanBusinessException(e);
    }
  }

  @Test
  public void testSaveDataSource() throws Exception {
    String expectDataSource = "MASTER";

    request.setParameter("routingDatasourceModel.selectedDataSource", expectDataSource);
    request.setMethod("GET");
    request.setRequestURI("/saveDataSource.do");

    ModelAndView mv = adapter.handle(request, response, controller);

    checkDialogMemPresenceInGuiCtx();
    assertEquals(expectDataSource, userContext.getDataSource());
    // assertNotSame(controller.getResetPage(), mv.getViewName());

    request.setParameter("doReset", "true");
    mv = adapter.handle(request, response, controller);
    assertEquals(controller.getResetPage(), mv.getViewName());
  }

  @Test
  public void testSaveConfigurationPermission() throws Exception {
    // as the spring superclass extends TestCase, no JUnit4-Style expected-Exceptionhandling can be
    // used!
    try {
      userContext = new UserContext(USER_NAME, new HashSet<Role>(), Locale.getDefault(), user);
      UserContext.setCurrentUserContext(userContext);
      testSaveConfiguration();
      fail();
    } catch (IteraplanBusinessException e) {
      // succeed
    }
  }

  @Test
  public void testSaveDataSourcePermission() throws Exception {
    // as the spring superclass extends TestCase, no JUnit4-Style expected-Exceptionhandling can be
    // used!
    try {
      userContext = new UserContext(USER_NAME, new HashSet<Role>(), Locale.getDefault(), user);
      UserContext.setCurrentUserContext(userContext);
      testSaveDataSource();
      fail();
    } catch (IteraplanBusinessException e) {
      // succeed
    }
  }

  private void checkDialogMemPresenceInGuiCtx() {
    GuiContext guiCtx = GuiContext.getCurrentGuiContext();
    DialogMemory diaMem = guiCtx.getDialogMemory(controller.getDialogName());
    assertNotNull(diaMem);
    assertEquals(controller.getDialogName(), guiCtx.getActiveDialog());

  }

}

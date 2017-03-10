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
package de.iteratec.iteraplan.presentation.flows;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.impl.FlowExecutionImpl;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import de.iteratec.iteraplan.AbstractFlowTestBase;
import de.iteratec.iteraplan.businesslogic.service.RoleService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService;
import de.iteratec.iteraplan.presentation.dialog.Role.RoleComponentModel;
import de.iteratec.iteraplan.presentation.dialog.Role.RoleMemBean;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


public class RoleFlowTest extends AbstractFlowTestBase {

  private static final Logger                       LOGGER     = Logger.getIteraplanLogger(RoleFlowTest.class);

  private RoleService                               roleService;
  private BuildingBlockFrontendService<RoleMemBean> roleFrontendService;

  private static final String                       SHOW_STATE = "show";

  @Override
  protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
    return resourceFactory.createFileResource("WebContent/WEB-INF/flow/roleFlow.xml");
  }

  @Override
  protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
    super.configureFlowBuilderContext(builderContext);
    builderContext.registerBean("roleService", roleService);
    builderContext.registerBean("roleFrontendService", roleFrontendService);
  }

  public void testStartRoleFlow() {
    roleFrontendService = EasyMock.createMock(BuildingBlockFrontendService.class);
    EasyMock.expect(roleFrontendService.getEntityName()).andReturn("role");
    EasyMock.expect(roleFrontendService.getFlowLabel((String) EasyMock.anyObject())).andReturn("");

    RoleMemBean memBean = new RoleMemBean();
    memBean.setComponentModel(new RoleComponentModel(ComponentMode.READ));
    EasyMock.expect(roleFrontendService.getMemBean(Integer.valueOf(1))).andReturn(memBean);

    roleService = EasyMock.createMock(RoleService.class);
    EasyMock.expect(roleService.getFirstElement()).andReturn(createFirstTestRole());
    EasyMock.expect(roleService.getAllRolesFiltered()).andReturn(new ArrayList<Role>());

    EasyMock.replay(roleService);
    EasyMock.replay(roleFrontendService);
    MutableAttributeMap input = new LocalAttributeMap();
    input.put("id", "1");
    MockExternalContext context = new MockExternalContext();
    context.setCurrentUser("keith");
    context.setEventId(SHOW_STATE);
    startFlow(input, context);
    assertCurrentStateEquals(SHOW_STATE);
  }

  public void testEditRoleProceed() {
    roleFrontendService = EasyMock.createNiceMock(BuildingBlockFrontendService.class);

    EasyMock.replay(roleFrontendService);

    setCurrentState(SHOW_STATE);

    FlowExecution flowExecution2 = getFlowExecutionFactory().createFlowExecution(getFlowDefinition());
    ((FlowExecutionImpl) flowExecution2).setCurrentState(SHOW_STATE);

    getFlowScope().put("id", Integer.valueOf(1));

    MockExternalContext context = new MockExternalContext();
    context.setEventId("edit");
    resumeFlow(context);

    assertCurrentStateEquals("edit");
    LOGGER.info("Context: {0}", context.getMockResponseWriter().getBuffer().toString());
  }

  private Role createFirstTestRole() {
    Role role = new Role();
    role.setId(Integer.valueOf(1));
    role.setRoleName("TestAdmin");
    role.setDescription("TestAdminDescription");
    return role;
  }

}

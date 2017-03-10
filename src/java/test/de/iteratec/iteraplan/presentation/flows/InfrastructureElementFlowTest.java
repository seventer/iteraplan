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

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import de.iteratec.iteraplan.AbstractFlowTestBase;
import de.iteratec.iteraplan.businesslogic.service.HistoryService;
import de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService;
import de.iteratec.iteraplan.presentation.dialog.InfrastructureElement.InfrastructureElementComponentModel;
import de.iteratec.iteraplan.presentation.dialog.InfrastructureElement.InfrastructureElementMemBean;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * JUnit Test of the infrastructureElement flow.
 * 
 * @author dzu
 */
public class InfrastructureElementFlowTest extends AbstractFlowTestBase {

  private BuildingBlockFrontendService<InfrastructureElementMemBean> infrastructureElementFrontendService;
  private HistoryService historyService;

  @Override
  @Before
  protected void setUp() {
    historyService = EasyMock.createNiceMock(HistoryService.class);
    EasyMock.expect(historyService.isHistoryEnabled()).andReturn(true).anyTimes();
    infrastructureElementFrontendService = EasyMock.createNiceMock(BuildingBlockFrontendService.class);
    EasyMock.expect(infrastructureElementFrontendService.getEntityName()).andReturn("infrastructureelement");
    EasyMock.expect(infrastructureElementFrontendService.getFlowLabel((String) EasyMock.anyObject())).andReturn("");

    InfrastructureElementMemBean memBean = new InfrastructureElementMemBean();
    memBean.setComponentModel(new InfrastructureElementComponentModel(ComponentMode.READ));
    EasyMock.expect(infrastructureElementFrontendService.getMemBean(Integer.valueOf(1))).andReturn(memBean);
  }

  @Override
  protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
    return resourceFactory.createFileResource("WebContent/WEB-INF/flow/infrastructureElementFlow.xml");
  }

  @Override
  protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
    super.configureFlowBuilderContext(builderContext);
    builderContext.registerBean("infrastructureElementFrontendService", infrastructureElementFrontendService);
    builderContext.registerBean("historyService", historyService);
  }

  @Test
  public void testStartFlow() {
    EasyMock.replay(infrastructureElementFrontendService, historyService);

    MutableAttributeMap input = new LocalAttributeMap();
    input.put("id", "1");
    MockExternalContext context = new MockExternalContext();
    context.setCurrentUser("keith");
    context.setEventId("show");

    // Tests if flow is started in show state.
    startFlow(input, context);
    assertCurrentStateEquals("show");

    // Tests if transition to edit mode is done.
    context.setEventId("edit");
    resumeFlow(context);
    assertCurrentStateEquals("edit");
  }

  @Test
  public void testCreateFlow() {

    EasyMock.replay(infrastructureElementFrontendService, historyService);

    // Tests if flow goes to "initCreate" state after event "create" is called at flow start.
    MockExternalContext context = new MockExternalContext();
    context.setEventId("create");
    context.setCurrentUser("keith");
    startFlow(context);
    assertCurrentStateEquals("initCreate");

    // Tests if validation for empty name field redirects flow to initCreate state.
    context.setEventId("update");
    resumeFlow(context);
    assertCurrentStateEquals("initCreate");

    // Tests if cancel ends flow
    context.setEventId("cancel");
    resumeFlow(context);
    assertFlowExecutionEnded();
  }
}

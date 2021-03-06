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
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.engine.impl.FlowExecutionImpl;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.MockParameterMap;

import de.iteratec.iteraplan.AbstractFlowTestBase;
import de.iteratec.iteraplan.presentation.dialog.TabularReporting.TabularReportingFrontendService;


public class TabularReportingFlowTest extends AbstractFlowTestBase {

  private TabularReportingFrontendService tabularReportingFrontendService;

  private static final String             SHOW_STATE        = "show";
  private static final String             BD_PLURAL_MSG_KEY = "businessDomain.plural";

  @Override
  protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
    return resourceFactory.createFileResource("WebContent/WEB-INF/flow/tabularReportingFlow.xml");
  }

  @Override
  protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
    super.configureFlowBuilderContext(builderContext);
    builderContext.registerBean("tabularReportingFrontendService", tabularReportingFrontendService);
  }

  public void testStartTabularReportingFlow() {
    tabularReportingFrontendService = EasyMock.createMock(TabularReportingFrontendService.class);
    EasyMock.expect(tabularReportingFrontendService.getInitialMemBean(null)).andReturn(null);
    EasyMock.expect(tabularReportingFrontendService.getFlowLabel((String) EasyMock.anyObject())).andReturn("");

    EasyMock.replay(tabularReportingFrontendService);
    MockExternalContext context = new MockExternalContext();
    context.setCurrentUser("keith");
    context.setEventId(null);
    startFlow(context);
    assertCurrentStateEquals(SHOW_STATE);
  }

  public void testStartWithBBTypeTabularReportingFlow() {
    tabularReportingFrontendService = EasyMock.createMock(TabularReportingFrontendService.class);
    EasyMock.expect(tabularReportingFrontendService.getInitialMemBean(BD_PLURAL_MSG_KEY)).andReturn(null);
    EasyMock.expect(tabularReportingFrontendService.getFlowLabel((String) EasyMock.anyObject())).andReturn("");

    EasyMock.replay(tabularReportingFrontendService);
    MockExternalContext context = new MockExternalContext();
    context.setCurrentUser("keith");
    context.setEventId(null);
    MockParameterMap requestParameterMap = new MockParameterMap();
    requestParameterMap.put("bbType", BD_PLURAL_MSG_KEY);
    context.setRequestParameterMap(requestParameterMap);
    startFlow(context);
    assertCurrentStateEquals(SHOW_STATE);
  }

  public void testChangeBBType() {
    tabularReportingFrontendService = EasyMock.createMock(TabularReportingFrontendService.class);
    EasyMock.expect(tabularReportingFrontendService.getFlowLabel((String) EasyMock.anyObject())).andReturn("");
    EasyMock.expect(tabularReportingFrontendService.getInitialMemBean(BD_PLURAL_MSG_KEY)).andReturn(null);

    EasyMock.replay(tabularReportingFrontendService);
    setCurrentState(SHOW_STATE);

    FlowExecution flowExecution2 = getFlowExecutionFactory().createFlowExecution(getFlowDefinition());
    ((FlowExecutionImpl) flowExecution2).setCurrentState(SHOW_STATE);

    MockExternalContext context = new MockExternalContext();
    MockParameterMap requestParameterMap = new MockParameterMap();
    requestParameterMap.put("bbType", BD_PLURAL_MSG_KEY);
    context.setRequestParameterMap(requestParameterMap);
    context.setEventId("changeBBType");
    resumeFlow(context);

    assertCurrentStateEquals(SHOW_STATE);
  }
}

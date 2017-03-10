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
import org.junit.Before;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.impl.FlowExecutionImpl;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import de.iteratec.iteraplan.AbstractFlowTestBase;
import de.iteratec.iteraplan.businesslogic.service.BusinessProcessService;
import de.iteratec.iteraplan.businesslogic.service.HistoryService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService;
import de.iteratec.iteraplan.presentation.dialog.BusinessProcess.BusinessProcessComponentModel;
import de.iteratec.iteraplan.presentation.dialog.BusinessProcess.BusinessProcessMemBean;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


public class BusinessProcessFlowTest extends AbstractFlowTestBase {

  private static final Logger                                  LOGGER     = Logger.getIteraplanLogger(BusinessProcessFlowTest.class);

  private BusinessProcessService                               businessProcessService;
  private BuildingBlockFrontendService<BusinessProcessMemBean> businessProcessFrontendService;

  private static final String                                  SHOW_STATE = "show";

  private HistoryService historyService;

  @Override
  @Before
  protected void setUp() {
    historyService = EasyMock.createNiceMock(HistoryService.class);
    EasyMock.expect(historyService.isHistoryEnabled()).andReturn(true).anyTimes();
  }

  @Override
  protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
    super.configureFlowBuilderContext(builderContext);
    builderContext.registerBean("businessProcessService", businessProcessService);
    builderContext.registerBean("businessProcessFrontendService", businessProcessFrontendService);
    builderContext.registerBean("historyService", historyService);
  }

  @Override
  protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
    return resourceFactory.createFileResource("WebContent/WEB-INF/flow/businessProcessFlow.xml");
  }

  public void testStartBusinessProcessFlow() {
    businessProcessFrontendService = EasyMock.createMock(BuildingBlockFrontendService.class);
    EasyMock.expect(businessProcessFrontendService.getEntityName()).andReturn("businessprocess");
    EasyMock.expect(businessProcessFrontendService.getFlowLabel((String) EasyMock.anyObject())).andReturn("");

    BusinessProcessMemBean businessProcessMemBean = new BusinessProcessMemBean();
    businessProcessMemBean.setComponentModel(new BusinessProcessComponentModel(ComponentMode.READ));
    EasyMock.expect(businessProcessFrontendService.getMemBean(Integer.valueOf(31))).andReturn(businessProcessMemBean);

    businessProcessService = EasyMock.createNiceMock(BusinessProcessService.class);
    EasyMock.expect(businessProcessService.getFirstElement()).andReturn(createFirstTestBusinessProcess());
    EasyMock.expect(businessProcessService.getEntitiesFiltered(null, false)).andReturn(new ArrayList<BusinessProcess>());

    EasyMock.replay(businessProcessService, businessProcessFrontendService, historyService);

    MutableAttributeMap input = new LocalAttributeMap();
    input.put("id", "31");

    MockExternalContext context = new MockExternalContext();
    context.setCurrentUser("keith");
    context.setEventId(SHOW_STATE);

    startFlow(input, context);
    assertCurrentStateEquals(SHOW_STATE);
  }

  public void testEditBusinessProcessProceed() {
    businessProcessFrontendService = EasyMock.createNiceMock(BuildingBlockFrontendService.class);

    EasyMock.replay(businessProcessFrontendService, historyService);

    setCurrentState(SHOW_STATE);

    FlowExecution flowExecution2 = getFlowExecutionFactory().createFlowExecution(getFlowDefinition());
    ((FlowExecutionImpl) flowExecution2).setCurrentState(SHOW_STATE);

    getFlowScope().put("id", Integer.valueOf(31));

    MockExternalContext context = new MockExternalContext();
    context.setEventId("edit");
    resumeFlow(context);

    assertCurrentStateEquals("edit");
    LOGGER.info("Context: {0}", context.getMockResponseWriter().getBuffer().toString());
  }

  private BusinessProcess createFirstTestBusinessProcess() {
    BusinessProcess businessProcess = new BusinessProcess();

    businessProcess.setName("MyBusinessProcess");
    businessProcess.setId(Integer.valueOf(31));

    return businessProcess;
  }

}

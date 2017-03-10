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
import de.iteratec.iteraplan.businesslogic.service.BusinessFunctionService;
import de.iteratec.iteraplan.businesslogic.service.HistoryService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService;
import de.iteratec.iteraplan.presentation.dialog.BusinessFunction.BusinessFunctionComponentModel;
import de.iteratec.iteraplan.presentation.dialog.BusinessFunction.BusinessFunctionMemBean;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


public class BusinessFunctionFlowTest extends AbstractFlowTestBase {

  private static final Logger                                   LOGGER     = Logger.getIteraplanLogger(BusinessFunctionFlowTest.class);

  private BusinessFunctionService                               businessFunctionService;
  private BuildingBlockFrontendService<BusinessFunctionMemBean> businessFunctionFrontendService;

  private static final String                                   SHOW_STATE = "show";

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
    builderContext.registerBean("businessFunctionService", businessFunctionService);
    builderContext.registerBean("businessFunctionFrontendService", businessFunctionFrontendService);
    builderContext.registerBean("historyService", historyService);
  }

  @Override
  protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
    return resourceFactory.createFileResource("WebContent/WEB-INF/flow/businessFunctionFlow.xml");
  }

  public void testStartBusinessFunctionFlow() {
    businessFunctionFrontendService = EasyMock.createMock(BuildingBlockFrontendService.class);
    EasyMock.expect(businessFunctionFrontendService.getEntityName()).andReturn("businessfunction");
    EasyMock.expect(businessFunctionFrontendService.getFlowLabel((String) EasyMock.anyObject())).andReturn("");

    BusinessFunctionMemBean businessFunctionMemBean = new BusinessFunctionMemBean();
    businessFunctionMemBean.setComponentModel(new BusinessFunctionComponentModel(ComponentMode.READ));
    EasyMock.expect(businessFunctionFrontendService.getMemBean(Integer.valueOf(31))).andReturn(businessFunctionMemBean);

    businessFunctionService = EasyMock.createNiceMock(BusinessFunctionService.class);
    EasyMock.expect(businessFunctionService.getFirstElement()).andReturn(createFirstTestBusinessFunction());
    EasyMock.expect(businessFunctionService.getEntitiesFiltered(null, false)).andReturn(new ArrayList<BusinessFunction>());

    EasyMock.replay(businessFunctionService, businessFunctionFrontendService, historyService);

    MutableAttributeMap input = new LocalAttributeMap();
    input.put("id", "31");

    MockExternalContext context = new MockExternalContext();
    context.setCurrentUser("keith");
    context.setEventId(SHOW_STATE);

    startFlow(input, context);
    assertCurrentStateEquals(SHOW_STATE);
  }

  public void testEditBusinessFunctionProceed() {
    businessFunctionFrontendService = EasyMock.createNiceMock(BuildingBlockFrontendService.class);

    EasyMock.replay(businessFunctionFrontendService, historyService);

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

  private BusinessFunction createFirstTestBusinessFunction() {
    BusinessFunction businessFunction = new BusinessFunction();

    businessFunction.setName("MyBusinessFunction");
    businessFunction.setId(Integer.valueOf(31));

    return businessFunction;
  }

}

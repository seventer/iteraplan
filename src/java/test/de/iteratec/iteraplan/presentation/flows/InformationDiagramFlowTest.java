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
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import de.iteratec.iteraplan.AbstractFlowTestBase;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.InformationFlow.InformationFlowGraphicFrontendServiceImpl;


public class InformationDiagramFlowTest extends AbstractFlowTestBase {

  private InformationFlowGraphicFrontendServiceImpl informationFlowGraphicFrontendService;

  @Override
  protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
    return resourceFactory.createFileResource("WebContent/WEB-INF/flow/graphicalInformationFlowDiagramFlow.xml");
  }

  @Override
  protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
    super.configureFlowBuilderContext(builderContext);
    builderContext.registerBean("informationFlowGraphicFrontendService", informationFlowGraphicFrontendService);
  }

  public void testStartInformationFlow() {
    ManageReportMemoryBean initialMemoryBean = new ManageReportMemoryBean();
    ManageReportMemoryBean step2MemoryBean = new ManageReportMemoryBean();
    informationFlowGraphicFrontendService = EasyMock.createMock(InformationFlowGraphicFrontendServiceImpl.class);
    EasyMock.expect(informationFlowGraphicFrontendService.getInitialMemBean()).andReturn(initialMemoryBean);
    EasyMock.expect(informationFlowGraphicFrontendService.getFlowLabel(null)).andReturn("test");
    EasyMock.expect(informationFlowGraphicFrontendService.getFlowLabel(null)).andReturn("test");

    EasyMock.expect(
        informationFlowGraphicFrontendService.stepOneToStepTwo((ManageReportMemoryBean) EasyMock.anyObject(), (RequestContext) EasyMock.anyObject(),
            (FlowExecutionContext) EasyMock.anyObject())).andReturn(step2MemoryBean);
    informationFlowGraphicFrontendService.refreshRelevantInterfaces((ManageReportMemoryBean) EasyMock.anyObject());
    informationFlowGraphicFrontendService.refreshRelevantBusinessObjects((ManageReportMemoryBean) EasyMock.anyObject());
    EasyMock.replay(informationFlowGraphicFrontendService);

    MockExternalContext externalContext = new MockExternalContext();
    externalContext.setCurrentUser("keith");
    externalContext.setEventId(null);
    startFlow(externalContext);
    assertCurrentStateEquals("firststep");
    assertEquals(initialMemoryBean, getRequiredFlowAttribute("memBean"));

    externalContext.setEventId("goToInformationFlowGraphicExportStep2");
    resumeFlow(externalContext);
    assertEquals(step2MemoryBean, getRequiredFlowAttribute("memBean"));
  }

}

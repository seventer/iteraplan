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
import org.junit.Test;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.impl.FlowExecutionImpl;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import de.iteratec.iteraplan.AbstractFlowTestBase;
import de.iteratec.iteraplan.businesslogic.service.HistoryService;
import de.iteratec.iteraplan.businesslogic.service.ProductService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService;
import de.iteratec.iteraplan.presentation.dialog.Product.ProductComponentModel;
import de.iteratec.iteraplan.presentation.dialog.Product.ProductMemBean;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


public class ProductFlowTest extends AbstractFlowTestBase {

  private static final Logger                          LOGGER     = Logger.getIteraplanLogger(ProductFlowTest.class);

  private ProductService                               productService;
  private BuildingBlockFrontendService<ProductMemBean> productFrontendService;
  private HistoryService historyService;

  private static final String                          SHOW_STATE = "show";

  @Override
  @Before
  protected void setUp() {
    historyService = EasyMock.createNiceMock(HistoryService.class);
    EasyMock.expect(historyService.isHistoryEnabled()).andReturn(true).anyTimes();
  }

  @Override
  protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
    super.configureFlowBuilderContext(builderContext);
    builderContext.registerBean("productService", productService);
    builderContext.registerBean("productFrontendService", productFrontendService);
    builderContext.registerBean("historyService", historyService);
  }

  @Override
  protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
    return resourceFactory.createFileResource("WebContent/WEB-INF/flow/productFlow.xml");
  }

  @Test
  public void testStartProductFlow() {
    productFrontendService = EasyMock.createMock(BuildingBlockFrontendService.class);
    EasyMock.expect(productFrontendService.getEntityName()).andReturn("product");
    EasyMock.expect(productFrontendService.getFlowLabel((String) EasyMock.anyObject())).andReturn("");

    ProductMemBean productMemBean = new ProductMemBean();
    productMemBean.setComponentModel(new ProductComponentModel(ComponentMode.READ));
    EasyMock.expect(productFrontendService.getMemBean(Integer.valueOf(31))).andReturn(productMemBean);

    productService = EasyMock.createNiceMock(ProductService.class);
    EasyMock.expect(productService.getFirstElement()).andReturn(createFirstTestProduct());
    EasyMock.expect(productService.getEntitiesFiltered(null, false)).andReturn(new ArrayList<Product>());

    EasyMock.replay(productService, productFrontendService, historyService);

    MutableAttributeMap input = new LocalAttributeMap();
    input.put("id", "31");

    MockExternalContext context = new MockExternalContext();
    context.setCurrentUser("keith");
    context.setEventId(SHOW_STATE);

    startFlow(input, context);
    assertCurrentStateEquals(SHOW_STATE);
  }

  @Test
  public void testEditProductProceed() {
    productFrontendService = EasyMock.createNiceMock(BuildingBlockFrontendService.class);

    EasyMock.replay(productFrontendService, historyService);

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

  @Test
  public void testCreateProductFlow() {
    productFrontendService = EasyMock.createNiceMock(BuildingBlockFrontendService.class);
    EasyMock.replay(productFrontendService, historyService);

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

  private Product createFirstTestProduct() {
    Product product = new Product();

    product.setName("MyProduct");
    product.setId(Integer.valueOf(31));

    return product;
  }

}

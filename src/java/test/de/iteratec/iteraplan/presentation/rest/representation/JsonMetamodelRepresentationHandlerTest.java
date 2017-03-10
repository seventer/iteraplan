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
package de.iteratec.iteraplan.presentation.rest.representation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.ElasticMiExecution;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTaskFactory;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.permission.User;
import de.iteratec.iteraplan.elasticmi.permission.impl.StakeholderManagerImpl;
import de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication;


/**
 *  Tests whether the returned representation is of the correct type and the response fields are properly set. <br>
 *  Tests of the content of the Representation are omitted since they are performed in the tests of the {@code serialization} package
 *  
 */
public class JsonMetamodelRepresentationHandlerTest {

  private JsonMetamodelRepresentationHandler handler;

  private Request                            request;
  private Response                           response;
  private HashMap<String, Object>            arguments;

  @SuppressWarnings("unchecked")
  @Before
  public void init() throws InterruptedException {
    Set<Object> mocks = Sets.newHashSet();
    handler = new JsonMetamodelRepresentationHandler();
    arguments = Maps.newHashMap();

    //metamodel
    RMetamodel metamodelMock = EasyMock.createMock(RMetamodel.class);
    mocks.add(metamodelMock);
    EasyMock.expect(metamodelMock.getStructuredTypes()).andReturn(Collections.EMPTY_LIST);
    EasyMock.expect(metamodelMock.getEnumerationTypes()).andReturn(Collections.EMPTY_LIST);

    //model
    Model modelMock = EasyMock.createMock(Model.class);
    mocks.add(modelMock);

    StakeholderManagerImpl mgr = new StakeholderManagerImpl();
    User user = mgr.createUser("testUser");
    ElasticMiContext ctx = new ElasticMiContext(user, "MASTER", new ElasticMiExecution(mgr, ElasticMiLoadTaskFactory.EMPTY_TASK_FACTORY));
    ElasticMiContext.setCurrentContext(ctx);

    ElasticMiContext currentContext = ElasticMiContext.getCurrentContext();
    while (!currentContext.isReady()) {
      Thread.sleep(500);
    }

    //request+response
    request = EasyMock.createMock(Request.class);
    mocks.add(request);
    response = new Response(request);

    EasyMock.replay(mocks.toArray());
  }

  @Test
  public void testRepresentationJson() {
    arguments.put(IteraplanRestApplication.KEY_FORMAT, "json");
    Representation rep = handler.process(request, response, arguments);

    Assert.assertTrue(rep instanceof JsonRepresentation);
    Assert.assertTrue(response.getEntity() instanceof JsonRepresentation);
    Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());

  }

  @Test
  public void testRepresentationDefault() {
    arguments.put(IteraplanRestApplication.KEY_RESPONSE_CONTENT, "InformationSystem;");

    Representation rep = handler.process(request, response, arguments);

    Assert.assertTrue(rep instanceof JsonRepresentation);
    Assert.assertTrue(response.getEntity() instanceof JsonRepresentation);
    Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());

  }

}

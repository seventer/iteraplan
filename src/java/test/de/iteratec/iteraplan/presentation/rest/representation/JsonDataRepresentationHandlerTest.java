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

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;

import com.google.common.collect.ImmutableMap;

import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.ElasticMiExecution;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTask;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTaskFactory;
import de.iteratec.iteraplan.elasticmi.m3c.SimpleM3C;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelCopyUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.write.impl.WMetamodelImpl;
import de.iteratec.iteraplan.elasticmi.model.impl.ModelImpl;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiAccessLevel;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiTypePermission;
import de.iteratec.iteraplan.elasticmi.permission.impl.StakeholderManagerImpl;
import de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication;


/**
 *  Tests whether the returned representation is of the correct type and the response fields are properly set. <br>
 *  Tests of the content of the Representation are omitted since they are performed in the tests of the {@code serialization} package
 *  
 */
public class JsonDataRepresentationHandlerTest {

  private JsonQueryRepresentationHandler handler;

  private Request                        request;
  private Response                       response;

  @Before
  public void setUp() throws InterruptedException {
    handler = new JsonQueryRepresentationHandler();

    WMetamodel miWMetamodel = new WMetamodelImpl();
    miWMetamodel.createClassExpression("IS", miWMetamodel.createTypeGroup("testGroup"));
    RMetamodel miRMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(miWMetamodel);

    SimpleM3C m3c = SimpleM3C.of("MASTER", miWMetamodel, miRMetamodel, new ModelImpl());

    ElasticMiLoadTask task = EasyMock.createMock(ElasticMiLoadTask.class);
    try {
      EasyMock.expect(task.call()).andReturn(m3c);
    } catch (Exception e) {
      Assert.fail(e.getMessage());
    }
    EasyMock.replay(task);

    ElasticMiLoadTaskFactory factory = EasyMock.createMock(ElasticMiLoadTaskFactory.class);
    EasyMock.expect(factory.create("MASTER")).andReturn(task);
    EasyMock.replay(factory);

    StakeholderManagerImpl mgr = new StakeholderManagerImpl();
    ElasticMiTypePermission perm = new ElasticMiTypePermission("IS", ElasticMiAccessLevel.CREATE);
    mgr.addTypePermission(perm);
    de.iteratec.iteraplan.elasticmi.permission.Role role = mgr.createRole("testRole");
    role.grantTypePermission(perm);
    de.iteratec.iteraplan.elasticmi.permission.User user = mgr.createUser("testUser");
    user.addRole(role);
    ElasticMiContext ctx = new ElasticMiContext(user, "MASTER", new ElasticMiExecution(mgr, factory));
    ElasticMiContext.setCurrentContext(ctx);

    Reference reference = new Reference("");

    request = EasyMock.createMock(Request.class);
    EasyMock.expect(request.getRootRef()).andReturn(reference);
    EasyMock.replay(request);
    response = new Response(request);

    while (!ElasticMiContext.getCurrentContext().isReady()) {
      Thread.sleep(500);
    }
  }

  @Test
  public void testRepresentationJson() {
    Representation rep = handler.process(request, response,
        ImmutableMap.<String, Object> of(IteraplanRestApplication.KEY_FORMAT, "json", IteraplanRestApplication.KEY_RESPONSE_CONTENT, "IS;"));

    Assert.assertTrue(rep instanceof JsonRepresentation);
    Assert.assertTrue(response.getEntity() instanceof JsonRepresentation);
    Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());

  }

  @Test
  public void testRepresentationDefault() {
    Representation rep = handler.process(request, response, ImmutableMap.<String, Object> of(IteraplanRestApplication.KEY_RESPONSE_CONTENT, "IS;"));

    Assert.assertTrue(rep instanceof JsonRepresentation);
    Assert.assertTrue(response.getEntity() instanceof JsonRepresentation);
    Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());

  }

}

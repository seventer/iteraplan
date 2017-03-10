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

import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.ElasticMiExecution;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTask;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTaskFactory;
import de.iteratec.iteraplan.elasticmi.m3c.SimpleM3C;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelCopyUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.write.impl.WMetamodelImpl;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.impl.ModelImpl;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiAccessLevel;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiTypePermission;
import de.iteratec.iteraplan.elasticmi.permission.impl.StakeholderManagerImpl;
import de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication;


/**
 *
 */
public class ExcelQueryRepresenationHandlerTest {

  private static final String             UTE_INFORMATION_SYSTEM_PN = "InformationSystem";

  private ExcelQueryRepresentationHandler handler;

  private Request                         request;
  private Response                        response;

  @Before
  public void init() throws InterruptedException {

    handler = new ExcelQueryRepresentationHandler();

    WMetamodel miWMetamodel = new WMetamodelImpl();
    miWMetamodel.createClassExpression(UTE_INFORMATION_SYSTEM_PN, miWMetamodel.createTypeGroup("testGroup"));
    RMetamodel miRMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(miWMetamodel);

    Model model = new ModelImpl();

    miRMetamodel.findStructuredTypeByPersistentName(UTE_INFORMATION_SYSTEM_PN).create(model);

    SimpleM3C m3c = SimpleM3C.of("MASTER", miWMetamodel, miRMetamodel, model);

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
    ElasticMiTypePermission perm = new ElasticMiTypePermission(UTE_INFORMATION_SYSTEM_PN, ElasticMiAccessLevel.CREATE);
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
  public void testXLSX() {
    Map<String, Object> arguments = Maps.newHashMap();
    arguments.put(IteraplanRestApplication.KEY_FORMAT, "xlsx");
    arguments.put(IteraplanRestApplication.KEY_RESPONSE_CONTENT, UTE_INFORMATION_SYSTEM_PN + ";");

    Representation rep = handler.process(request, response, arguments);

    Assert.assertTrue(rep instanceof OutputRepresentation);
    Assert.assertTrue(response.getEntity() instanceof OutputRepresentation);
    Assert.assertTrue(response.getEntity().equals(rep));
    Assert.assertEquals(MediaType.APPLICATION_MSOFFICE_XLSX, rep.getMediaType());
    Disposition dis = response.getEntity().getDisposition();
    Assert.assertEquals(Disposition.TYPE_ATTACHMENT, dis.getType());
  }

  @Test
  public void testXLS() {
    Map<String, Object> arguments = Maps.newHashMap();
    arguments.put(IteraplanRestApplication.KEY_FORMAT, "xls");
    arguments.put(IteraplanRestApplication.KEY_RESPONSE_CONTENT, UTE_INFORMATION_SYSTEM_PN + ";");

    handler.process(request, response, arguments);

    Assert.assertEquals(Status.CLIENT_ERROR_NOT_ACCEPTABLE, response.getStatus());
  }

  @Test
  public void testWrongFileFormat() {
    Map<String, Object> arguments = Maps.newHashMap();
    arguments.put(IteraplanRestApplication.KEY_FORMAT, "xml");
    arguments.put(IteraplanRestApplication.KEY_RESPONSE_CONTENT, UTE_INFORMATION_SYSTEM_PN + ";");

    handler.process(request, response, arguments);

    Assert.assertEquals(Status.CLIENT_ERROR_NOT_ACCEPTABLE, response.getStatus());
  }

  @Test
  public void testWrongQueryFormat() {
    Map<String, Object> arguments = Maps.newHashMap();
    arguments.put(IteraplanRestApplication.KEY_FORMAT, "xlsx");
    arguments.put(IteraplanRestApplication.KEY_RESPONSE_CONTENT, "wrongQuery");

    handler.process(request, response, arguments);

    Assert.assertEquals(Status.CLIENT_ERROR_BAD_REQUEST, response.getStatus());
  }
}
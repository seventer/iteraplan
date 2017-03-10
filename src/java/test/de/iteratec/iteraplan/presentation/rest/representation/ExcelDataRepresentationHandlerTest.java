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

import java.util.HashMap;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.WorkbookContext;
import de.iteratec.iteraplan.businesslogic.service.ElasticeamService;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.ElasticMiExecution;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTaskFactory;
import de.iteratec.iteraplan.elasticmi.permission.User;
import de.iteratec.iteraplan.elasticmi.permission.impl.StakeholderManagerImpl;
import de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication;


/**
 * Tests the functionality of the {@link ExcelDataRepresentationHandler}. Ensures that the representation created by this handler is of the proper type, disposition and format, and that the appropriate representation is sent with the response. 
 */
public class ExcelDataRepresentationHandlerTest {
  private ExcelDataRepresentationHandler handler;

  private Request                        request;
  private Response                       response;
  private HashMap<String, Object>        arguments;

  private Set<Object>                    mocks = Sets.newHashSet();

  @Before
  public void init() throws InterruptedException {
    StakeholderManagerImpl mgr = new StakeholderManagerImpl();
    User user = mgr.createUser("testUser");
    ElasticMiContext ctx = new ElasticMiContext(user, "MASTER", new ElasticMiExecution(mgr, ElasticMiLoadTaskFactory.EMPTY_TASK_FACTORY));
    ElasticMiContext.setCurrentContext(ctx);

    ElasticMiContext currentContext = ElasticMiContext.getCurrentContext();
    while (!currentContext.isReady()) {
      Thread.sleep(500);
    }

    handler = new ExcelDataRepresentationHandler();
    arguments = Maps.newHashMap();

    //metamodel
    Metamodel metamodelMock = EasyMock.createMock(Metamodel.class);
    mocks.add(metamodelMock);

    //model
    Model modelMock = EasyMock.createMock(Model.class);
    mocks.add(modelMock);

    //elastic service
    ElasticeamService elasticServiceMock = EasyMock.createMock(ElasticeamService.class);
    mocks.add(elasticServiceMock);
    handler.setElasticeamService(elasticServiceMock);
    EasyMock.expect(elasticServiceMock.getMetamodel()).andReturn(metamodelMock).anyTimes();
    EasyMock.expect(elasticServiceMock.getModel()).andReturn(modelMock).anyTimes();

    //workbook
    Workbook wbmock = EasyMock.createMock(Workbook.class);
    mocks.add(wbmock);

    //workbookcontext
    WorkbookContext wbContextmock = EasyMock.createMock(WorkbookContext.class);
    mocks.add(wbContextmock);
    EasyMock.expect(wbContextmock.getWb()).andReturn(wbmock).anyTimes();

    //request+response
    request = EasyMock.createMock(Request.class);
    mocks.add(request);
    response = new Response(request);

    EasyMock.replay(mocks.toArray());
  }

  @Test
  public void testXLS() {
    arguments.put(IteraplanRestApplication.KEY_FORMAT, "xls");

    Representation rep = handler.process(request, response, arguments);

    Assert.assertTrue(rep instanceof OutputRepresentation);
    Assert.assertTrue(response.getEntity() instanceof OutputRepresentation);
    Assert.assertTrue(response.getEntity().equals(rep));
    Assert.assertEquals(MediaType.APPLICATION_EXCEL, rep.getMediaType());
    Disposition dis = response.getEntity().getDisposition();
    Assert.assertEquals(Disposition.TYPE_ATTACHMENT, dis.getType());
  }

  @Test
  public void testXLSX() {
    arguments.put(IteraplanRestApplication.KEY_FORMAT, "xlsx");

    Representation rep = handler.process(request, response, arguments);

    Assert.assertTrue(rep instanceof OutputRepresentation);
    Assert.assertTrue(response.getEntity() instanceof OutputRepresentation);
    Assert.assertTrue(response.getEntity().equals(rep));
    Assert.assertEquals(MediaType.APPLICATION_MSOFFICE_XLSX, rep.getMediaType());
    Disposition dis = response.getEntity().getDisposition();
    Assert.assertEquals(Disposition.TYPE_ATTACHMENT, dis.getType());
  }

  @Test
  public void testOther() {
    arguments.put(IteraplanRestApplication.KEY_FORMAT, "something");

    try {
      handler.process(request, response, arguments);
    } catch (IteraplanBusinessException ex) {
      if (ex.getErrorCode() == IteraplanErrorMessages.FORMAT_NOT_SUPPORTED) {
        return;
      }
    }

    //should not reach this point; exception with appropriate code should be thrown
    Assert.assertTrue(false);

  }
}

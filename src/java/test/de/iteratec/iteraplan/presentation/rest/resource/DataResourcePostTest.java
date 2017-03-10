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
package de.iteratec.iteraplan.presentation.rest.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.exchange.common.SimpleMessage;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.ImportProcessFactory;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.MiImportProcess;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.MiImportProcessMessages;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticmi.messages.Message;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.presentation.dialog.ExcelImport.ImportStrategy;
import de.iteratec.iteraplan.presentation.rest.IteraplanRestRouter;


@ContextConfiguration({ "/applicationContext.xml", "/applicationContext-dao.xml", "/applicationContext-datasource.xml",
    "/applicationContext-ext.xml", "/applicationContext-gui.xml", "/applicationContext-staticquery.xml", "/applicationContext-test.xml",
    "/applicationContext-rest.xml", "/applicationContext-mockImportProcess.xml" })
@ActiveProfiles("de.iteratec.iteraplan.testing")
public class DataResourcePostTest extends AbstractJUnit4SpringContextTests {

  private static final String  TEST_INFO_MESSAGE    = "Test info message.";
  private static final String  TEST_WARNING_MESSAGE = "Test warning message.";
  private static final String  TEST_ERROR_MESSAGE   = "Test error message.";
  private static final String  URL                  = "/data";
  @Autowired
  private IteraplanRestRouter  router;
  @Autowired
  private ImportProcessFactory importProcessFactoryMock;

  private ByteArrayInputStream emptyInputStream;
  private MiImportProcess      importProcessMock;

  @Before
  public void setUp() {
    importProcessMock = EasyMock.createStrictMock(MiImportProcess.class);

    EasyMock.reset(importProcessMock, importProcessFactoryMock);

    emptyInputStream = new ByteArrayInputStream(new byte[0]);
    EasyMock.expect(importProcessFactoryMock.createMiExcelImportProcess(ImportStrategy.ADDITIVE, emptyInputStream)).andReturn(importProcessMock);
    EasyMock.replay(importProcessFactoryMock);

    Role restRole = new Role();
    restRole.setRoleName("RestRole");

    restRole.setPermissionsFunctional(Collections.singleton(new PermissionFunctional(TypeOfFunctionalPermission.REST)));

    de.iteratec.iteraplan.model.user.User alice = new de.iteratec.iteraplan.model.user.User();
    UserContext context = new UserContext("test", Collections.singleton(restRole), Locale.ENGLISH, alice);
    UserContext.setCurrentUserContext(context);
  }

  @SuppressWarnings("boxing")
  @Test
  public void testPostSuccess() throws Exception {
    EasyMock.expect(importProcessMock.executeAllApplicableSteps()).andReturn(Boolean.TRUE);
    EasyMock.expect(importProcessMock.getImportProcessMessages()).andReturn(createMessagesMock());
    EasyMock.replay(importProcessMock);

    Response response = assertPost(Status.SUCCESS_OK);
    assertEquals(getExpectedMessages(), getActualMessages(response));
  }

  @SuppressWarnings("boxing")
  @Test
  public void testPostFail() throws Exception {
    EasyMock.expect(importProcessMock.executeAllApplicableSteps()).andReturn(Boolean.FALSE);
    EasyMock.expect(importProcessMock.getImportProcessMessages()).andReturn(createMessagesMock());
    EasyMock.replay(importProcessMock);

    Response response = assertPost(Status.SERVER_ERROR_INTERNAL);
    assertEquals(getExpectedMessages(), getActualMessages(response));
  }

  @SuppressWarnings("boxing")
  @Test
  public void testPostException() throws Exception {
    EasyMock.expect(importProcessMock.executeAllApplicableSteps()).andThrow(new IteraplanTechnicalException());
    EasyMock.expect(importProcessMock.getImportProcessMessages()).andReturn(createMessagesMock());
    EasyMock.replay(importProcessMock);

    Response response = assertPost(Status.SERVER_ERROR_INTERNAL);
    String actual = getActualMessages(response);
    String expected = getExpectedMessages() + "ERROR: de.iteratec.iteraplan.common.error.IteraplanTechnicalException";
    assertTrue("Message \n\"" + actual + "\"\n doesn't start with \n\"" + expected + "\"\n.", actual.startsWith(expected));
  }

  private Response assertPost(Status expectedStatus) {
    InputRepresentation fileEntity = new InputRepresentation(emptyInputStream);

    Request request = new Request(Method.POST, URL, fileEntity);
    request.setEntity(fileEntity);

    Response response = new Response(request);
    router.handle(request, response);

    assertEquals(expectedStatus, response.getStatus());
    return response;
  }

  private String getActualMessages(Response response) throws IOException {
    Representation representation = response.getEntity();
    return representation.getText();
  }

  private String getExpectedMessages() {
    return TEST_ERROR_MESSAGE + "\n" + TEST_WARNING_MESSAGE + "\n" + TEST_INFO_MESSAGE + "\n";
  }

  private MiImportProcessMessages createMessagesMock() {
    List<Message> messageList = Lists.newArrayList();
    messageList.add(new SimpleMessage(Severity.ERROR, TEST_ERROR_MESSAGE));
    messageList.add(new SimpleMessage(Severity.WARNING, TEST_WARNING_MESSAGE));
    messageList.add(new SimpleMessage(Severity.INFO, TEST_INFO_MESSAGE));

    MiImportProcessMessages messages = EasyMock.createMock(MiImportProcessMessages.class);
    EasyMock.expect(messages.getMessages()).andReturn(messageList);
    EasyMock.replay(messages);
    return messages;
  }
}

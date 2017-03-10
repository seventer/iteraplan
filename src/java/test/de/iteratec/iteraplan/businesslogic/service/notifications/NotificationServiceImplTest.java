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
package de.iteratec.iteraplan.businesslogic.service.notifications;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.model.notification.Email;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.email.EmailModel;

public class NotificationServiceImplTest {

  //  private static final Logger         LOGGER = Logger.getIteraplanLogger(NotificationServiceImplTest.class);

  private JavaMailSender          senderMock;
  private EmailFactory        emailFactoryMock;

  private NotificationServiceImpl notificationServiceImpl = new NotificationServiceImpl();

  @Before
  public void setUp() {
    senderMock = EasyMock.createNiceMock(JavaMailSender.class);
    emailFactoryMock = EasyMock.createNiceMock(EmailFactory.class);

    notificationServiceImpl.setEmailFactory(emailFactoryMock);
    notificationServiceImpl.setSender(senderMock);

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.notifications.NotificationServiceImpl#sendEmail(java.util.Collection, java.lang.String, java.lang.Object[])}.
   */
  @Test
  public void testSendEmail() {
    String emailTo = "your.mail@yourHost.com";
    String emailFrom = "my.mail@myHost.com";
    notificationServiceImpl.setEmailFrom(emailFrom);

    User userNoMail = new User();
    User withMail = new User();
    withMail.setEmail(emailTo);
    List<User> users = Lists.newArrayList(withMail, userNoMail);

    Map<User, EmailModel> data = new HashMap<User, EmailModel>();
    EmailModel model = new EmailModel();
    data.put(withMail, model);

    String key = "key";
    String subj = "This is the subject";
    String txt = "This is the text";
    Email mail = new Email(subj, txt);
    EasyMock.expect(emailFactoryMock.createEmail(key, model)).andReturn(mail);

    SimpleMailMessage expMessage = new SimpleMailMessage();
    expMessage.setTo(emailTo);
    expMessage.setFrom(emailFrom);
    expMessage.setSubject(subj);
    expMessage.setText(txt);
    senderMock.send(expMessage);
    EasyMock.expectLastCall();

    EasyMock.replay(emailFactoryMock, senderMock);
    notificationServiceImpl.sendEmail(users, key, data);
    EasyMock.verify(emailFactoryMock, senderMock);
  }

}

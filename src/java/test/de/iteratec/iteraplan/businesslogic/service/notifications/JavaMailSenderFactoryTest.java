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
/**
 * 
 */
package de.iteratec.iteraplan.businesslogic.service.notifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * A Test for creating {@link JavaMailSenderImpl} instance using the {@link JavaMailSenderFactory} and
 * then trying to send a test email. All mails will be sent to mailinator. To access the mailbox go to
 * http://asd654fasdf.mailinator.com.
 * 
 * <p>The account passwords can be found in our WIKI.
 * <p>This test class is set to Ignore, because the passwords must be set, and I don't want to spread them
 * across the world :).
 * 
 */
public class JavaMailSenderFactoryTest {

  private static final String GMAIL_USERNAME = "user@gmail.com";
  private static final String GMAIL_PASSWORD = "***";

  private static final String ITERATEC_DE_USERNAME = "iteraplan@iteratec.de";
  private static final String ITERATEC_DE_PASSWORD = "******";

  private static final String ITERAPLAN_DE_MAILADRESS = "iteraplande-0001@iteraplan.de";
  private static final String ITERAPLAN_DE_USERNAME = "iteraplande-0001";
  private static final String ITERAPLAN_DE_PASSWORD = "******";

  private JavaMailSenderFactory mailSenderFactory = new JavaMailSenderFactory();

  @Before
  public void onSetup() {
    mailSenderFactory = new JavaMailSenderFactory();
    mailSenderFactory.setActivated(true);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.notifications.JavaMailSenderFactory#create()}.
   */
  @Test
  @Ignore
  public void testSendUsingIteratecSmtp() {
    mailSenderFactory.setHost("smtp.system-hoster.com");
    mailSenderFactory.setPort("25");
    mailSenderFactory.setStartTls(true);
    mailSenderFactory.setUsername(ITERATEC_DE_USERNAME);
    mailSenderFactory.setPassword(ITERATEC_DE_PASSWORD);

    JavaMailSender javaSender = mailSenderFactory.create();
    send(javaSender, ITERATEC_DE_USERNAME);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.notifications.JavaMailSenderFactory#create()}.
   */
  @Test
  @Ignore
  public void testSendUsingGmailSmtpWithSSL() {
    mailSenderFactory.setHost("smtp.gmail.com");
    mailSenderFactory.setPort("465");
    mailSenderFactory.setSsl(true);
    mailSenderFactory.setUsername(GMAIL_USERNAME);
    mailSenderFactory.setPassword(GMAIL_PASSWORD);

    JavaMailSender javaSender = mailSenderFactory.create();
    send(javaSender, GMAIL_USERNAME);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.notifications.JavaMailSenderFactory#create()}.
   */
  @Test
  @Ignore
  public void testSendUsingIteraplanSmtp() {
    mailSenderFactory.setHost("smtp.udag.de");
    mailSenderFactory.setPort("");
    mailSenderFactory.setStartTls(true);
    mailSenderFactory.setUsername(ITERAPLAN_DE_USERNAME);
    mailSenderFactory.setPassword(ITERAPLAN_DE_PASSWORD);

    JavaMailSender javaSender = mailSenderFactory.create();
    send(javaSender, ITERAPLAN_DE_MAILADRESS);
  }

  private void send(JavaMailSender sender, String from) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo("asd654fasdf@mailinator.com");
    message.setFrom(from);
    message.setSubject("test");
    message.setText("test. Time: " + new LocalDateTime().toString());
    sender.send(message);
  }

  @Test
  public void testSubmitAnonymously() {
    mailSenderFactory.setHost("smtp.udag.de");
    mailSenderFactory.setPort("");
    mailSenderFactory.setStartTls(false);
    mailSenderFactory.setUsername(""); // this should translate to null
    mailSenderFactory.setPassword(""); // this should translate to null

    JavaMailSenderImpl anonymousSender = (JavaMailSenderImpl) mailSenderFactory.create();
    assertNull("empty user name should be treated as null", anonymousSender.getUsername());
    assertNull("empty password should be treated as null if user is empty", anonymousSender.getPassword());

    mailSenderFactory.setUsername("foobar");
    mailSenderFactory.setPassword(""); // this should be treated as empty string, as user name is given
    JavaMailSenderImpl emptyPasswordSender = (JavaMailSenderImpl) mailSenderFactory.create();
    assertNotNull("user name should not be treated as null", emptyPasswordSender.getUsername());
    assertEquals("empty password should be treated as empty if user is given", "", emptyPasswordSender.getPassword());

    mailSenderFactory.setUsername("");
    mailSenderFactory.setPassword("barbaz"); // this should be treated as empty string, as user name is given
    JavaMailSenderImpl bogusPasswordSender = (JavaMailSenderImpl) mailSenderFactory.create();
    assertNull("empty user name should be treated as null", bogusPasswordSender.getUsername());
    assertNull("password should be ignored and treated as null if user is empty", bogusPasswordSender.getPassword());

  }

}

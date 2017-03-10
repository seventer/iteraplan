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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailPreparationException;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.model.notification.Email;
import de.iteratec.iteraplan.presentation.email.EmailModel;


/**
 * Integration test for the {@link FreeMarkerBasedEmailFactory} class.
 */
public class EmailFactoryTest extends BaseTransactionalTestSupport {
  @Autowired
  private FreeMarkerBasedEmailFactory emailFactory;

  @Test
  public void testCreateEmail() throws Exception {
    Date date = new LocalDateTime(2011, 4, 12, 16, 45).toDateTime().toDate();

    assertNotNull(emailFactory);
    String key = "generic.deleted";
    EmailModel data = new EmailModel();
    data.setName("BP1");
    data.setLink("http://localhost:8080/iteraplan/show/businessdomain/728");
    data.setUser("system");
    data.setTime(date);
    data.setType("Business process");

    Email email = emailFactory.createEmail(key, data);
    String actualSubject = email.getSubject();
    String actualContent = email.getText();
    String expectedSubject = "[iteraplan] Business process \"BP1\" deleted";
    String expectedTimeStamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(date);
    String expectedContent = "Business process \"BP1\" deleted (http://localhost:8080/iteraplan/show/businessdomain/728)\r\n\r\nUser: system\r\n\r\nTime: "
        + expectedTimeStamp + "\r\n\r\n-- \r\nEAM Tool iteraplan";

    assertEquals(expectedSubject, actualSubject);
    assertEquals(expectedContent, actualContent);
  }

  /**
   * Tests if the correct exception is thrown after an invalid key.
   */
  @Test(expected=MailPreparationException.class)
  public void testCreateEmailNoSuchMessage() {
    assertNotNull(emailFactory);
    String key = "invalid";
    Map<String, Object> data = Maps.newHashMap();

    emailFactory.createEmail(key, data);
  }

}

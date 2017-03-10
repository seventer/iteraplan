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

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.notification.Email;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.email.EmailModel;

/**
 * A default implementation for the {@link NotificationService}.
 */
public class NotificationServiceImpl implements NotificationService {

  private static final Logger LOGGER = Logger.getIteraplanLogger(NotificationServiceImpl.class);

  private String              emailFrom;
  @Autowired
  private JavaMailSender      sender;
  @Autowired
  private EmailFactory        emailFactory;

  /** {@inheritDoc} */
  public void sendEmail(Collection<User> users, String key, Map<User, EmailModel> emailModels) {
    if (sender == null || users == null) {
      return;
    }

    if (StringUtils.isBlank(emailFrom)) {
      LOGGER.error("No outgoing email specified");
      throw new IteraplanTechnicalException(
          IteraplanErrorMessages.NOTIFICATION_CONFIGURATION_INCOMPLETE);
    }

    for (User user : users) {
      String emailTo = user.getEmail();

      if (StringUtils.isBlank(emailTo)) {
        LOGGER.error("Missing email address for user " + user.getLoginName());
        continue;
      }

      EmailModel model = emailModels.get(user);

      if (key.endsWith(".updated") && model.getChanges().isEmpty()) {
        continue;
      }

      Email email = null;
      try {
        email = emailFactory.createEmail(key, model);
      } catch (MailException e) {
        LOGGER.error("Error generating the email content: ", e);
        continue;
      }

      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(emailTo);
      message.setFrom(emailFrom);
      message.setSubject(email.getSubject());
      message.setText(email.getText());
      try {
        this.sender.send(message);
      } catch (MailException e) {
        LOGGER.error("Mail cannot be sent: ", e);
        continue;
      }
    }
  }

  public void setSender(JavaMailSender sender) {
    this.sender = sender;
  }

  public void setEmailFactory(EmailFactory emailFactory) {
    this.emailFactory = emailFactory;
  }

  public void setEmailFrom(String emailFrom) {
    this.emailFrom = emailFrom;
  }

}

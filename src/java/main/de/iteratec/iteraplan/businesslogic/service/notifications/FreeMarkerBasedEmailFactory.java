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

import java.io.IOException;

import org.springframework.mail.MailPreparationException;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import de.iteratec.iteraplan.model.notification.Email;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;


/**
 * An Email factory for creating {@link Email} messages using FreeMarker template engine.
 * 
 * @author vsh
 *
 */
public class FreeMarkerBasedEmailFactory implements EmailFactory {

  private Configuration freeMarkerConfiguration;

  /**
   * Creates an instance of this class with the specified {@code freeMarkerConfiguration}.
   * 
   * @param freeMarkerConfiguration the message source for resolving localization messages
   */
  public FreeMarkerBasedEmailFactory(Configuration freeMarkerConfiguration) {
    this.freeMarkerConfiguration = freeMarkerConfiguration;
  }

  /**
   * {@inheritDoc}
   */
  public Email createEmail(String key, Object data) {
    String subject;
    String text;
    try {
      Template subjectTemplate = freeMarkerConfiguration.getTemplate(key + ".subject.ftl");
      Template contentTemplate = freeMarkerConfiguration.getTemplate(key + ".content.ftl");

      subject = FreeMarkerTemplateUtils.processTemplateIntoString(subjectTemplate, data);
      text = FreeMarkerTemplateUtils.processTemplateIntoString(contentTemplate, data);
    } catch (TemplateException e) {
      throw new MailPreparationException(e);
    } catch (IOException e) {
      throw new MailPreparationException(e);
    }

    return new Email(subject, text);
  }

}

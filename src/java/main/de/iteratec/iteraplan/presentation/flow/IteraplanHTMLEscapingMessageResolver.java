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
package de.iteratec.iteraplan.presentation.flow;

import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageResolver;
import org.springframework.context.MessageSource;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.presentation.tags.TagUtils;


/**
 * {@link MessageResolver} that resolves messages of {@link IteraplanException} and 
 * escapes the params of the exception if they are not of class {@link String} or {@link Number}
 */
public class IteraplanHTMLEscapingMessageResolver implements MessageResolver {
  private static final Logger LOGGER = Logger.getIteraplanLogger(IteraplanHTMLEscapingMessageResolver.class);
  private IteraplanException  exception;

  /**
   * Default constructor.
   */
  public IteraplanHTMLEscapingMessageResolver(IteraplanException exception) {
    this.exception = exception;
  }

  /**{@inheritDoc}**/
  public Message resolveMessage(MessageSource messageSource, Locale locale) {
    Object[] params = exception.getParams();

    if (params == null) {
      params = new Object[0];
    }

    for (int i = 0; i < params.length; i++) {
      if (params[i] instanceof Number || params[i] instanceof Date) {
        continue;
      }
      if (params[i] instanceof String) {
        params[i] = TagUtils.filter((String) params[i]);
      }
      else {
        params[i] = TagUtils.filter(params[i].toString());
      }
    }

    String errorKey = IteraplanErrorMessages.getErrorMsgKey(exception.getErrorCode());
    if (StringUtils.isEmpty(errorKey)) {
      LOGGER.error("Could not find message for error code " + exception.getErrorCode());
      errorKey = IteraplanErrorMessages.getErrorMsgKey(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }

    return new MessageBuilder().error().code(errorKey).args(params).build().resolveMessage(messageSource, locale);
  }
}

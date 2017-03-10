/*
 * iTURM is a User and Roles Management web application developed by iteratec, GmbH
 * Copyright (C) 2008 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
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
 * You can contact iteratec GmbH headquarters at Inselkammerstra�e 4
 * 82008 M�nchen - Unterhaching, Germany, or at email address info@iteratec.de.
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
package de.iteratec.turm.messages;

import de.iteratec.turm.servlets.TurmServlet;


/**
 * A class that can hold a message.
 * 
 * When a message needs to be displayed to the user, an instance of this class
 * should be created and stored with the {@link TurmServlet#saveMessages} method.
 * It will automatically shown to the user.
 */
public class TurmMessage {

  /** The I18N key of the message that is to be displayed. */
  String   messageKey;

  /** Optional list of parameters that will replace placeholders in the message. */
  String[] parameters;

  public TurmMessage(String messageKey, String... parameters) {
    super();
    this.messageKey = messageKey;
    this.parameters = parameters;
  }

  public String getMessageKey() {
    return messageKey;
  }

  public String[] getParameters() {
    return parameters;
  }

}
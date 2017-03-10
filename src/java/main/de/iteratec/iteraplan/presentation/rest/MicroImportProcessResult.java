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
package de.iteratec.iteraplan.presentation.rest;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcessMessages;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.JsonMicroImportProcess;
import de.iteratec.iteraplan.elasticmi.messages.Message;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;


/**
 * The result of a {@link JsonMicroImportProcess} capable of serializing itself into json.<br>
 * <br>
 * This is directly used for the DELETE verb of the micro REST API. For PUT and POST verbs use the {@link MicroImportProcessElementResult} to ensure the URI of the affected {@link ObjectExpression} being included into the result.
 */
public class MicroImportProcessResult {
  private List<String> messages;

  /**
   * Default constructor.
   */
  public MicroImportProcessResult(ImportProcessMessages messages) {
    this.messages = Lists.transform(messages.getMessages(), new Function<Message, String>() {
      @Override
      public String apply(Message input) {
        return input.getMessage();
      }
    });
  }

  public JsonElement toJson() {
    JsonObject result = new JsonObject();

    preMessages(result);
    JsonArray messagesArray = new JsonArray();
    for (String message : messages) {
      messagesArray.add(new JsonPrimitive(message));
    }
    result.add("messages", messagesArray);

    return result;
  }

  protected void preMessages(JsonObject target) {
    //do nothing by default
  }
}

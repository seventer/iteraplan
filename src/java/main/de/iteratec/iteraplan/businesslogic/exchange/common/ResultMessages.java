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
package de.iteratec.iteraplan.businesslogic.exchange.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;


public class ResultMessages implements Serializable {
  /** Serialization version */
  private static final long                   serialVersionUID = 8164080995867459287L;

  private final Map<ErrorLevel, List<String>> messagesMap;

  public ResultMessages() {
    this.messagesMap = Maps.newHashMap();
    for (ErrorLevel type : ErrorLevel.values()) {
      messagesMap.put(type, new ArrayList<String>());
    }
  }

  public void addMessage(ErrorLevel type, String message) {
    messagesMap.get(type).add(message);
  }

  public void addMessages(ErrorLevel type, List<String> messages) {
    messagesMap.get(type).addAll(messages);
  }

  public void addMessages(ResultMessages messages) {
    for (Map.Entry<ErrorLevel, List<String>> entry : messages.getMessages().entrySet()) {
      messagesMap.get(entry.getKey()).addAll(entry.getValue());
    }
  }

  public Map<ErrorLevel, List<String>> getMessages() {
    return messagesMap;
  }

  public List<String> getErrors() {
    return messagesMap.get(ErrorLevel.ERROR);
  }

  public List<String> getWarnings() {
    return messagesMap.get(ErrorLevel.WARNING);
  }

  public List<String> getInfos() {
    return messagesMap.get(ErrorLevel.INFO);
  }

  public ErrorLevel getErrorLevel() {
    if (!getErrors().isEmpty()) {
      return ErrorLevel.ERROR;
    }
    if (!getWarnings().isEmpty()) {
      return ErrorLevel.WARNING;
    }
    return ErrorLevel.INFO;
  }

  public static enum ErrorLevel {
    INFO("[INFO ]: "), WARNING("[WARN ]: "), ERROR("[ERROR]: ");
    private String prefix;

    private ErrorLevel(String prefix) {
      this.prefix = prefix;
    }

    public String getPrefix() {
      return prefix;
    }
  }

}

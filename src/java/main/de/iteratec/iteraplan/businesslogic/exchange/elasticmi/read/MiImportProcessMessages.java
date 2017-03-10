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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcess.CheckPoint;
import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcessMessages;
import de.iteratec.iteraplan.businesslogic.exchange.common.ResultMessages;
import de.iteratec.iteraplan.businesslogic.exchange.common.ResultMessages.ErrorLevel;
import de.iteratec.iteraplan.elasticmi.messages.LocalizedMiMessage;
import de.iteratec.iteraplan.elasticmi.messages.Message;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.messages.merge.AccumulatedCreateMessage;
import de.iteratec.iteraplan.elasticmi.messages.merge.AccumulatedDeleteMessage;
import de.iteratec.iteraplan.elasticmi.messages.merge.AccumulatedUpdateMessage;
import de.iteratec.iteraplan.elasticmi.messages.merge.CreateDiffMessage;
import de.iteratec.iteraplan.elasticmi.messages.merge.DeleteDiffMessage;
import de.iteratec.iteraplan.elasticmi.messages.merge.UpdateDiffMessage;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;


/**
 * Message listener for elasticMI-based import processes.
 */
public class MiImportProcessMessages extends ImportProcessMessages {

  private static final long                                            serialVersionUID = 7310923775203432281L;

  private final ArrayListMultimap<Severity, Message>                   messages         = ArrayListMultimap.create();

  private final Multimap<RStructuredTypeExpression, CreateDiffMessage> createMessages   = HashMultimap.create();
  private final Multimap<RStructuredTypeExpression, UpdateDiffMessage> updateMessages   = HashMultimap.create();
  private final Multimap<RStructuredTypeExpression, DeleteDiffMessage> deleteMessages   = HashMultimap.create();

  /**{@inheritDoc}**/
  @Override
  public boolean hasErrors() {
    return this.messages.get(Severity.ERROR).size() > 0;
  }

  /**{@inheritDoc}**/
  @Override
  public void onMessage(Message message) {
    this.messages.get(message.getSeverity()).add(message);
  }

  /**{@inheritDoc}**/
  @Override
  public void onMessage(CreateDiffMessage message) {
    createMessages.put(message.getType(), message);
  }

  /**{@inheritDoc}**/
  @Override
  public void onMessage(UpdateDiffMessage message) {
    updateMessages.put(message.getType(), message);
  }

  /**{@inheritDoc}**/
  @Override
  public void onMessage(DeleteDiffMessage message) {
    deleteMessages.put(message.getType(), message);
  }

  @Override
  public void clear() {
    messages.clear();
  }

  /**{@inheritDoc}**/
  @Override
  public List<Message> getMessages() {
    List<Message> result = Lists.newArrayList(messages.get(Severity.ERROR));
    result.addAll(messages.get(Severity.WARNING));
    result.addAll(messages.get(Severity.INFO));
    return Collections.unmodifiableList(result);
  }

  /**{@inheritDoc}**/
  @Override
  public List<Message> getMessages(Severity severity) {
    return Collections.unmodifiableList(messages.get(severity));
  }

  /**{@inheritDoc}**/
  @Override
  public ResultMessages asResultMessages() {
    ResultMessages result = new ResultMessages();
    for (Entry<Severity, Message> entry : messages.entries()) {
      result.addMessage(map(entry.getKey()), entry.getValue().getMessage());
    }
    return result;
  }

  private static ErrorLevel map(Severity severity) {
    if (Severity.ERROR.equals(severity)) {
      return ErrorLevel.ERROR;
    }
    else if (Severity.WARNING.equals(severity)) {
      return ErrorLevel.WARNING;
    }
    return ErrorLevel.INFO;
  }

  /**{@inheritDoc}**/
  @Override
  public void finalizeCheckPoint(CheckPoint checkpoint) {
    if (CheckPoint.MODEL_COMPARE.equals(checkpoint) || CheckPoint.MODEL_WRITE.equals(checkpoint)) {
      if (createMessages.isEmpty() && updateMessages.isEmpty() && deleteMessages.isEmpty()) {
        messages.put(Severity.INFO, new LocalizedMiMessage(Severity.INFO, "de.iteratec.iteraplan.elasticmi.excelimport.noChanges"));
      }
      else {
        for (RStructuredTypeExpression type : createMessages.keySet()) {
          messages.put(Severity.INFO, new AccumulatedCreateMessage(type, ImmutableSet.copyOf(createMessages.get(type))));
        }
        for (RStructuredTypeExpression type : updateMessages.keySet()) {
          messages.put(Severity.INFO, new AccumulatedUpdateMessage(type, ImmutableSet.copyOf(updateMessages.get(type))));
        }
        for (RStructuredTypeExpression type : deleteMessages.keySet()) {
          messages.put(Severity.INFO, new AccumulatedDeleteMessage(type, ImmutableSet.copyOf(deleteMessages.get(type))));
        }
        createMessages.clear();
        updateMessages.clear();
        deleteMessages.clear();
      }
    }
  }
}

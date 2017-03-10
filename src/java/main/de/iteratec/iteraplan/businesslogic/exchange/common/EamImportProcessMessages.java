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

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcess.CheckPoint;
import de.iteratec.iteraplan.businesslogic.exchange.common.ResultMessages.ErrorLevel;
import de.iteratec.iteraplan.elasticmi.messages.Message;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.messages.merge.CreateDiffMessage;
import de.iteratec.iteraplan.elasticmi.messages.merge.DeleteDiffMessage;
import de.iteratec.iteraplan.elasticmi.messages.merge.UpdateDiffMessage;


/**
 * A message listener which wraps the elasticEAM ResultMessages,
 * to enable messaging from the EAM Import Process.
 */
public final class EamImportProcessMessages extends ImportProcessMessages {

  private static final long serialVersionUID = -3192473719864778868L;

  private ResultMessages    resultMessages;

  public EamImportProcessMessages(ResultMessages messages) {
    this.resultMessages = messages;
  }

  /**{@inheritDoc}**/
  @Override
  public void onMessage(Message message) {
    //Not supported in this kind of listener (for now)
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  @Override
  public void onMessage(DeleteDiffMessage message) {
    // Not supported in this kind of listener (for now)
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  @Override
  public void onMessage(CreateDiffMessage message) {
    // Not supported in this kind of listener (for now)
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  @Override
  public void onMessage(UpdateDiffMessage message) {
    // Not supported in this kind of listener (for now)
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  @Override
  public ResultMessages asResultMessages() {
    return this.resultMessages;
  }

  /**{@inheritDoc}**/
  @Override
  public List<Message> getMessages() {
    List<Message> messages = Lists.newArrayList();
    for (Entry<ErrorLevel, List<String>> entry : resultMessages.getMessages().entrySet()) {
      messages.addAll(createMessages(entry.getKey(), entry.getValue()));
    }
    return messages;
  }

  public void setWrappedResultMessages(ResultMessages messages) {
    this.resultMessages = messages;
  }

  /**{@inheritDoc}**/
  @Override
  public List<Message> getMessages(Severity severity) {
    ErrorLevel errorLevel;
    if (Severity.ERROR.equals(severity)) {
      errorLevel = ErrorLevel.ERROR;
    }
    else if (Severity.INFO.equals(severity)) {
      errorLevel = ErrorLevel.INFO;
    }
    else {
      errorLevel = ErrorLevel.WARNING;
    }
    return createMessages(errorLevel, resultMessages.getMessages().get(errorLevel));
  }

  private List<Message> createMessages(final ErrorLevel errorLevel, final Collection<String> messages) {
    List<Message> result = Lists.newArrayList();
    for (String msg : messages) {
      result.add(new SimpleMessage(errorLevel, msg));
    }
    return result;
  }

  /**{@inheritDoc}**/
  @Override
  public void clear() {
    this.resultMessages = new ResultMessages();
  }

  /**{@inheritDoc}**/
  @Override
  public void finalizeCheckPoint(CheckPoint checkpoint) {
    //no finalization required for eam import
  }

  /**{@inheritDoc}**/
  @Override
  public boolean hasErrors() {
    return resultMessages.getErrors().size() > 0;
  }

}

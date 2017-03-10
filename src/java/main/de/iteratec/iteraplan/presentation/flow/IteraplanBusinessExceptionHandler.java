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

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.execution.FlowExecutionException;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.ConcurrentModificationException;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.presentation.problemreports.IteraplanProblemReport;


/**
 * Handles {@link IteraplanBusinessException}s. It extracts the error code from the exception and
 * passes the appropriate error message to the previous state's page. The ongoing flow is not
 * terminated.
 */
public class IteraplanBusinessExceptionHandler implements FlowExecutionExceptionHandler {

  private static final String ERROR_TARGET_STATE = "onError";
  private static final Logger LOGGER             = Logger.getIteraplanLogger(IteraplanBusinessExceptionHandler.class);

  public boolean canHandle(FlowExecutionException exception) {
    return canHandleIteraBizExc(exception);
  }

  public boolean canHandleIteraBizExc(Throwable exception) {
    // though unlikely, check for null
    if (exception.getCause() == null) {
      return false;
    }

    Class<? extends Throwable> causingException = exception.getCause().getClass();

    if (IteraplanBusinessException.class.isAssignableFrom(causingException)
        || ConcurrentModificationException.class.isAssignableFrom(causingException)
        || HibernateOptimisticLockingFailureException.class.isAssignableFrom(causingException)) {
      return true;
    }

    // is this the end of the chain of causes?
    if (!exception.equals(exception.getCause())) {
      return canHandleIteraBizExc(exception.getCause());
    }

    return false;
  }

  public void handle(FlowExecutionException exception, RequestControlContext context) {
    LOGGER.debug(exception);

    IteraplanProblemReport.createFromFlow(exception, context);

    // unwrap the IteraplanBusinessException and get the error code; must be consistent with above canHandle methods
    Throwable ex = exception;

    if (ex.getCause() instanceof HibernateOptimisticLockingFailureException) {
      String errorKey = IteraplanErrorMessages.getErrorMsgKey(IteraplanErrorMessages.CONCURRENT_MODIFICATION_DETECTED);

      MessageContext msgCtx = context.getMessageContext();
      MessageBuilder builder = new MessageBuilder();
      msgCtx.addMessage(builder.error().code(errorKey).build());
    }
    else {
      while (!(ex instanceof IteraplanBusinessException) && (ex.getCause() != ex)) {
        ex = ex.getCause();
      }
      IteraplanBusinessException bizException = (IteraplanBusinessException) ex;

      // put exception message to message context
      MessageContext msgCtx = context.getMessageContext();
      msgCtx.addMessage(new IteraplanHTMLEscapingMessageResolver(bizException));
    }

    String targetState = exception.getStateId();

    // if the exception passed this handler already once, we might have caused it by re-executing
    // the state again, so this time use the default error state
    if (FlowExceptionHandler.isExceptionPossiblyRecursive(ex, this)) {
      targetState = ERROR_TARGET_STATE;
    }

    // if the exception happened in an action-State, then transition to error state, since
    // transitioning again to the action-State would only cause the exception to be thrown again!
    // tricky: if the exception happened in a view-state within on-entry it will happen again.
    if (!(context.getCurrentState() instanceof ViewState)) {
      targetState = ERROR_TARGET_STATE;
    }

    // return to previous (or error) state and have error messages displayed
    TargetStateResolver resolver = new DefaultTargetStateResolver(targetState);
    Transition targetTransition = new Transition(resolver);
    context.execute(targetTransition);
  }

}

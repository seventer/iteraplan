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

import java.util.Arrays;
import java.util.List;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.NoMatchingTransitionException;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.execution.FlowExecutionException;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.presentation.problemreports.IteraplanProblemReport;


/**
 * The default catch-all exception handler.
 */
public class FlowExceptionHandler implements FlowExecutionExceptionHandler {

  private static final String DEFAULT_ERROR_TARGET_STATE = "onError";
  private static final Logger LOGGER                     = Logger.getIteraplanLogger(FlowExceptionHandler.class);

  public boolean canHandle(FlowExecutionException ex) {
    if (isExceptionPossiblyRecursive(ex, this)) {
      return false;
    }
    return true;
  }

  public void handle(FlowExecutionException ex, RequestControlContext ctx) {
    LOGGER.error(ex);

    IteraplanProblemReport.createFromFlow(ex, ctx);

    // put exception message to message context
    MessageContext msgCtx = ctx.getMessageContext();
    MessageBuilder builder = new MessageBuilder();

    String targetState = DEFAULT_ERROR_TARGET_STATE;

    if (ex instanceof NoMatchingTransitionException) {
      msgCtx.addMessage(builder.error().code("errorview.text.helpOne.partThree").build());

    }

    TargetStateResolver resolver = new DefaultTargetStateResolver(targetState);
    Transition targetTransition = new Transition(resolver);
    ctx.execute(targetTransition);
  }

  /**
   * Checks if the exception within its StackTrace contains a class that's name is equal to the
   * given handler-class. <br />
   * If the exception passed this handler already once, we might have caused it ourselves by trying
   * to send a specific errorPage or re-entering a flow-state. To prevent a loop the exception
   * handling should be modified if this method returns true.
   * 
   * @param exception
   *          the exception to check
   * @param handler
   *          the current handler we are in (this)
   * @return true if the Exception is possibly recursive, false otherwise
   */
  public static boolean isExceptionPossiblyRecursive(Throwable exception, FlowExecutionExceptionHandler handler) {
    List<StackTraceElement> stack = Arrays.asList(exception.getStackTrace());
    for (StackTraceElement stackElement : stack) {
      if (stackElement.getClassName().equals(handler.getClass().getName())) {
        return true;
      }
    }
    return false;
  }
}

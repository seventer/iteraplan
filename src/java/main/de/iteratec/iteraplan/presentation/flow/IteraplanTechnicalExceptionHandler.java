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

import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.execution.FlowExecutionException;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.presentation.problemreports.IteraplanProblemReport;


/**
 * Handles {@link IteraplanTechnicalException}s. It extracts the error code from the exception and
 * passes the appropriate error message to a generic error page. The ongoing flow is terminated, as
 * such errors are typically unrecoverable.
 */
public class IteraplanTechnicalExceptionHandler implements FlowExecutionExceptionHandler {

  private static final String ERROR_TARGET_STATE = "onError";
  private static final Logger LOGGER             = Logger.getIteraplanLogger(IteraplanTechnicalExceptionHandler.class);

  public boolean canHandle(FlowExecutionException exception) {
    return canHandleIteraTechExc(exception);
  }

  /**
   * Walks the entire exception cause chain in order to decide whether the top-level exception was
   * caused by an {@link IteraplanTechnicalException}. Aborts the recursion at the end of the chain.
   * 
   * @param exception
   *          An Exception to assess.
   * @return true if the exception chain contains anIteraplanTechnicalException.
   */
  public boolean canHandleIteraTechExc(Throwable exception) {
    // though unlikely, check for null
    if (exception.getCause() == null) {
      return false;
    }
    // if IteraplanTechnicalException is in the chain of causes, support exception handling
    Class<? extends Throwable> causingException = exception.getCause().getClass();
    if (IteraplanTechnicalException.class.isAssignableFrom(causingException)) {
      return true;
    }
    // is this the end of the chain of causes?
    if (!exception.equals(exception.getCause())) {
      return canHandleIteraTechExc(exception.getCause());
    }
    return false;
  }

  public void handle(FlowExecutionException exception, RequestControlContext context) {
    LOGGER.error("A technical exception occurred", exception);

    IteraplanProblemReport.createFromFlow(exception, context);

    // unwrap the IteraplanTechnicalException and get the error code; must be consistent with above
    // canHandle methods
    Throwable ex = exception;
    while (!(ex instanceof IteraplanTechnicalException) && (ex.getCause() != ex)) {
      ex = ex.getCause();
    }
    IteraplanTechnicalException techException = (IteraplanTechnicalException) ex;
    // put exception message to message context
    MessageContext msgCtx = context.getMessageContext();
    msgCtx.addMessage(new IteraplanHTMLEscapingMessageResolver(techException));

    // go to the default error end state, thus terminating the flow
    TargetStateResolver resolver = new DefaultTargetStateResolver(ERROR_TARGET_STATE);
    Transition targetTransition = new Transition(resolver);
    context.execute(targetTransition);
  }

}

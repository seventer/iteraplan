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
package de.iteratec.iteraplan.presentation.dialog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.repository.FlowExecutionAccessException;
import org.springframework.webflow.execution.repository.FlowExecutionLock;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.FlowExecutorImpl;

import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.flow.FlowEntry;


/**
 * Provides common methods for all frontend services.
 */
public abstract class CommonFrontendServiceImpl implements CommonFrontendService {

  /** Flash scoped attribute name for the attribute, which triggers the download (event) after the POST-REDIRECT-GET. */
  public static final String  FLUSHATTRIBUTE_NAME_OF_DOWNLOAD_TRIGGER = "triggerDownloadEvent";

  private static final Logger LOGGER                                  = Logger.getIteraplanLogger(CommonFrontendServiceImpl.class);

  private FlowExecutor        flowExecutor;

  @Autowired
  public void setFlowExecutor(FlowExecutor flowExecutor) {
    this.flowExecutor = flowExecutor;
  }

  /**
   * Must return the flowId for the specific FrontendService.
   * 
   * @return the flowId
   */
  protected abstract String getFlowId();

  /**
   * {@inheritDoc}
   */
  public String getEntityName() {
    return getFlowId().substring(0, getFlowId().indexOf('/'));
  }

  /**
   * {@inheritDoc}
   */
  public String getFlowLabel(String label) {
    if (label == null) {
      return MessageAccess.getStringOrNull("global.name.unnamed", UserContext.getCurrentLocale());
    }
    return label;
  }

  /**
   * {@inheritDoc}
   */
  public void enterEditMode(FlowExecutionContext flowContext) {
    if (flowContext != null) {
      String key = flowContext.getKey() == null ? null : flowContext.getKey().toString(); // may be null if flow is still initializing
      GuiContext.getCurrentGuiContext().updateFlowEntry(getFlowId(), null, key, Boolean.TRUE, null);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void leaveEditMode(FlowExecutionContext flowContext) {
    if (flowContext != null) {
      GuiContext.getCurrentGuiContext().updateFlowEntry(getFlowId(), null, flowContext.getKey().toString(), Boolean.FALSE, null);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void closeAllFlows(RequestContext context) {

    // get the flowRepository for access to the active Flows
    // we need to rely on that the flowExecutor is in Fact a FlowExecutorImpl
    // as this is currently the only implementation provided by Spring, this is safe, but may change
    // in future versions! If another Way to retrieve the executionRepository can be found it should
    // be preferred.
    FlowExecutionRepository flowRepository = ((FlowExecutorImpl) flowExecutor).getExecutionRepository();

    Collection<FlowEntry> flowEntries;

    // if the current Dialog is RESTART, then get ALL Flows
    if (Dialog.RESTART.getFlowId().equals(getFlowId())) {
      flowEntries = GuiContext.getCurrentGuiContext().getAllFlows();
    }
    else {
      // get all the flowEntries for the current Dialog name (e.g. all AttributeType-Flows)
      flowEntries = GuiContext.getCurrentGuiContext().getAllFlowsForDialogName(Dialog.getDialogNameForFlowId(getFlowId()));
    }

    // remove each flowExecution that matches the Key of the FlowEntries from the flowRepository
    for (FlowEntry flowEntry : flowEntries) {
      FlowExecutionKey flowKey = flowRepository.parseFlowExecutionKey(flowEntry.getKey());

      try {
        FlowExecutionLock flowLock = flowRepository.getLock(flowKey);
        flowLock.lock();
        try {
          FlowExecution flowExecution = flowRepository.getFlowExecution(flowKey);
          flowRepository.removeFlowExecution(flowExecution);
        } finally {
          flowLock.unlock();
        }
      } catch (FlowExecutionAccessException e) {
        // if the FlowExecution for an key could not be retrieved, the Flow is definitely broken,
        // the entry will later be removed from the menu which is okay
        LOGGER.error("Flow could not be closed, but will be removed from Menu!", e);
      }
    }

    // remove all flowEntries from the collection, thus removing them from the menu
    flowEntries.clear();
  }

  /**
   * Triggers an Download Event
   * This method can be used to display a label on the screen which indicates that a file download has been requested
   * @param context current request context
   * @param downloadEvent the name of the flow action, which will be executed after reloading the page, e.g. a flow action to trigger a download
   */
  public void triggerDownloadEvent(RequestContext context, String downloadEvent) {
    context.getFlashScope().put(FLUSHATTRIBUTE_NAME_OF_DOWNLOAD_TRIGGER, downloadEvent);
  }

  /**
   * Marks a Download as completed
   * This is necessary to interrupt Spring Webflow Routine to prevent the sendRedirect() error
   * @param requestContext requestcontext
   */
  protected void markFinished(RequestContext requestContext) {
    //see here: http://docs.spring.io/spring-webflow/docs/2.3.3.RELEASE/reference/html/actions.html 
    //chapter: 6.7.8 Streaming actions
    MessageBuilder messageBuilder = new MessageBuilder().info().defaultText("The download will start automatically.");
    requestContext.getMessageContext().addMessage(messageBuilder.build());
    requestContext.getExternalContext().recordResponseComplete();
  }
}

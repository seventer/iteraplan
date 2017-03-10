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

import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.SessionConstants;


public class CustomFlowListener extends FlowExecutionListenerAdapter {

  private static final Logger LOGGER = Logger.getIteraplanLogger(CustomFlowListener.class);

  @Override
  public void sessionEnded(RequestContext context, FlowSession session, String outcome, AttributeMap output) {
    FlowExecutionKey key = ((RequestControlContext) context).assignFlowExecutionKey();
    GuiContext.getCurrentGuiContext().removeFlowEntry(session.getDefinition().getId(), key.toString());
    LOGGER.debug("Flow Session ended. Session key - {0}", key.toString());
  }

  @Override
  public void sessionStarted(RequestContext context, FlowSession session) {
    // only add the FlowEntry, if we are in a view-state!
    // thus preventing NPE
    if (! context.inViewState()) {
      return;
    }

    FlowExecutionKey key = context.getFlowExecutionContext().getKey();
    String label = (String) session.getScope().get(SessionConstants.FLOW_LABEL_KEY);
    // ID of the entity that is display with that flow, i.e. for which the flow was started
    // it may safely be null
    Integer entityId = (Integer) session.getScope().get(SessionConstants.ENTITY_ID_KEY, Integer.class);

    GuiContext.getCurrentGuiContext().addFlowEntry(context.getActiveFlow().getId(), key.toString(), label, entityId);
    LOGGER.debug("Flow session with key {0} started. Session key: {1}; Added to GuiContext!", context.getActiveFlow().getId(), key.toString());
  }

  @Override
  public void viewRendering(RequestContext context, View view, StateDefinition viewState) {
    FlowExecutionKey key = context.getFlowExecutionContext().getKey();
    Integer id = (Integer) context.getFlowScope().get(SessionConstants.ENTITY_ID_KEY);
    GuiContext.getCurrentGuiContext().updateFlowEntry(context.getActiveFlow().getId(),
        context.getFlowScope().get(SessionConstants.FLOW_LABEL_KEY).toString(), key.toString(), null, id);

    LOGGER.debug("Entering flow view. Session key - {0}", key.toString());
  }

}

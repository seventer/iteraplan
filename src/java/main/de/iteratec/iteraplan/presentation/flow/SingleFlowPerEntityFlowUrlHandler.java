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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.webflow.context.servlet.DefaultFlowUrlHandler;
import org.springframework.webflow.core.collection.AttributeMap;

import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.SessionConstants;


/**
 * Specialization of Spring's Default Flow URL Handler. When a new flow is to be opened, this flow
 * URL handler checks whether a flow of the desired entity exists already. If so, it does simply
 * return the flow execution key of that existing flow, thus resuming the existing flow. This
 * prevents one entity to be opened in more than one flow at a time.
 */
public class SingleFlowPerEntityFlowUrlHandler extends DefaultFlowUrlHandler {

  private static final String SHOW_STATE     = "show";
  private static final String COPYBB_STATE   = "copyBB";
  private static final String CREATE_NEW_REL = "createRel";
  private static final String CLOSEALL_STATE = "closeAll";

  /**
   * When using REST-Urls, the requestURI always is a valid flowDefinition URL
   */
  @Override
  public String createFlowDefinitionUrl(String flowId, AttributeMap input, HttpServletRequest request) {
    StringBuffer url = new StringBuffer();
    if (request.getPathInfo() != null) {
      // in our case the requestURI itself is always a valid flowDefinitionUrl
      url.append(request.getRequestURI());
    }
    else {
      return super.createFlowDefinitionUrl(flowId, input, request);
    }
    if ((input != null) && !input.isEmpty()) {
      url.append('?');
      appendQueryParameters(url, input.asMap(), getEncodingScheme(request));
    }
    return url.toString();
  }

  @Override
  public String getFlowExecutionKey(HttpServletRequest request) {
    // if the flow already has an executionKey, return it
    if (super.getFlowExecutionKey(request) != null) {
      return super.getFlowExecutionKey(request);
    }

    // if this is a closeAll request return a new flow to jump to after all the others are closed
    if (isCloseAllRequest(request)) {
      return super.getFlowExecutionKey(request); // New Flow
    }

    // retrieve the entityIdVal from the request
    String entityIdVal = retrieveEntityIdVal(request);
    String flowID = getFlowId(request);

    if ((entityIdVal != null) || isSingleFlow(flowID)) { // Try to stay within a flow if the request
      // has an ID or this is an singleFlow (like tabular reporting or graphicalFlows)
      Integer entityId = entityIdVal == null ? null : Integer.valueOf(entityIdVal);

      GuiContext guiCtx = GuiContext.getCurrentGuiContext();
      List<FlowEntry> flowEntries = guiCtx.getAllFlowsWithIds(flowID, entityId);

      if (flowEntries.size() > 0) {
        return deriveFlowExecutionKey(request, flowEntries);
      }
    }

    return super.getFlowExecutionKey(request); // New Flow
  }

  private boolean isCloseAllRequest(HttpServletRequest request) {
    String[] evtCodes = request.getParameterValues(SessionConstants.FLOW_EVENT_ID);
    if (evtCodes != null) {
      for (String evtCode : evtCodes) {
        if (CLOSEALL_STATE.equalsIgnoreCase(evtCode)) {
          return true;
        }
      }
    }
    return false;
  }

  private String deriveFlowExecutionKey(HttpServletRequest request, List<FlowEntry> flowEntries) {
    String[] eventCodes = request.getParameterValues(SessionConstants.FLOW_EVENT_ID);
    // need to remove the event ID if we jump into the flow from outside (and thus the eventId
    // is "show"), in order to prevent an unexpected state transition
    if (eventCodes != null) {
      for (int i = 0; i < eventCodes.length; i++) {
        // despite having an ID, the copyBB and createRel events must always open a new flow
        if (COPYBB_STATE.equals(eventCodes[i]) || CREATE_NEW_REL.equals(eventCodes[i])) {
          return super.getFlowExecutionKey(request); // New Flow
        }
        else if (SHOW_STATE.equals(eventCodes[i])) {
          eventCodes[i] = null;
        }
      }
    }
    // as webflow only evaluates the eventCode at position 0, make sure that the array
    // contains a value at position 0
    switchNullWithFirstValue(eventCodes);

    return flowEntries.get(0).getKey();
  }

  private String retrieveEntityIdVal(HttpServletRequest request) {
    // does the request specify an entity ID?
    String entityIdVal = request.getParameter(SessionConstants.ENTITY_ID_KEY);

    // if id is not specified as request parameter, try to get REST-style id
    if (entityIdVal == null) {
      entityIdVal = FlowHelper.getId(request.getPathInfo());
    }

    String flowID = getFlowId(request);

    // if this is a singleFlow, set the entityID to null, to get all active flows as search results,
    // regardless of their id
    if (isSingleFlow(flowID)) {
      entityIdVal = null;
    }
    return entityIdVal;
  }

  @Override
  /**
   * When using REST-URIs the flow id is the part between the start of the pathInfo and the 
   * first occurrence of a number suffixed with "/start" i.e. for 
   * http://localhost:80/iteraplan/show/informationsystem/20/ the flowId would be informationsystem/start 
   * assuming a servlet mapping of "/show/*"
   */
  public String getFlowId(HttpServletRequest request) {
    String pathInfo = request.getPathInfo();
    if (pathInfo != null) {
      return FlowHelper.getFlowId(pathInfo);
    }
    else {
      return super.getFlowId(request);
    }
  }

  /**
   * If the Array contains a null value at position 0, the first value in the array that is not null
   * will be switched with it
   * 
   * @param eventCodes
   */
  private void switchNullWithFirstValue(String[] eventCodes) {
    if ((eventCodes != null) && (eventCodes[0] == null)) {
      for (int i = 0; i < eventCodes.length; i++) {
        if (eventCodes[i] != null) {
          eventCodes[0] = eventCodes[i];
          eventCodes[i] = null;
          break;
        }
      }
    }
  }

  private boolean isSingleFlow(String flowID) {
    for (Dialog d : Dialog.getSingleFlows()) {
      if (d.getFlowId().equals(flowID)) {
        return true;
      }
    }
    return false;
  }

}

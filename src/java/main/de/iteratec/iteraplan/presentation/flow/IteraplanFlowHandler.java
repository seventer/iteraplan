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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.mvc.servlet.AbstractFlowHandler;

import de.iteratec.iteraplan.presentation.SessionConstants;
import de.iteratec.iteraplan.presentation.problemreports.IteraplanProblemReport;


public abstract class IteraplanFlowHandler extends AbstractFlowHandler {

  /**
   * <p>
   * To support RESTful URIs this method extracts the ID of the entity that is addressed in this
   * flow from the URI and provides it to SWF as an attribute. This attribute will be received by
   * SWF as if it was a requestParam. This means that <input name="id" type="java.lang.Integer" />
   * will now be set to "100" for either "/entity/start.flow?id=100" or "/entity/100/".
   * </p>
   * <p>
   * Also note that within the flows decision state the default outcome (if _eventId == null) 
   * was set to "show" (or "init" for some flows) thus the _eventId can now be ommited in most 
   * cases.
   * </p>
   */
  @Override
  public MutableAttributeMap createExecutionInputMap(HttpServletRequest request) {
    String pathInfo = request.getPathInfo();
    if (pathInfo != null) {
      LocalAttributeMap map = new LocalAttributeMap();

      String id = FlowHelper.getId(pathInfo);
      if (id != null) {
        map.put(SessionConstants.ENTITY_ID_KEY, id);
        return map;
      }
    }
    return null;
  }

  @Override
  public String getFlowId() {
    // Overriden by subclasses
    return "";
  }

  @Override
  public String handleException(FlowException e, HttpServletRequest request, HttpServletResponse response) {
    IteraplanProblemReport.createFromController(e, request);
    return super.handleException(e, request, response);
  }

  @Override
  public String handleExecutionOutcome(FlowExecutionOutcome outcome, HttpServletRequest request, HttpServletResponse response) {
    return super.handleExecutionOutcome(outcome, request, response);
  }

}

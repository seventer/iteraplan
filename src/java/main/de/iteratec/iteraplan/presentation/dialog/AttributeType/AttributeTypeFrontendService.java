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
package de.iteratec.iteraplan.presentation.dialog.AttributeType;

import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService;


/**
 * Service interface for all attribute type management frontend logic.
 */
public interface AttributeTypeFrontendService extends BuildingBlockFrontendService<AttributeTypeMemBean> {

  AttributeTypeMemBean getChooseMemBean(RequestContext context, FlowExecutionContext flowContext);

  /**
   * Generates a new memBean that is suitable for creating a new attribute type. The memBean is
   * guaranteed to be in CREATE mode. <b>The second method with this name,
   * {@link AttributeTypeFrontendService#getCreateMemBean(FlowExecutionContext)}, does not work in
   * the attribute type context. Use this one here!</b>
   * 
   * @param memBean
   * @param context
   * @param flowContext
   * @return see method description
   */
  AttributeTypeMemBean getCreateMemBean(AttributeTypeMemBean memBean, RequestContext context, FlowExecutionContext flowContext);

  /**
   * Generates a new memBean that is initialized for copying values from an already existing
   * attribute type. <code>templateId</code> is the id of the attribute type to copy from. The
   * memBean is guaranteed to be in CREATE mode.
   * 
   * @param templateId
   *          ID of the attribute type from which values will be initialized.
   * @param context
   *          Spring Webflow context
   * @param flowContext
   *          Spring Webflow context
   * @return an attribute type memBean which holds a component model initialized with values from
   *         the specified existing attribute type
   */
  AttributeTypeMemBean getCopyMemBean(Integer templateId, RequestContext context, FlowExecutionContext flowContext);

}

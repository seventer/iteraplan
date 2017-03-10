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
package de.iteratec.iteraplan.presentation.rest.resource;

import java.util.Map;

import com.google.common.collect.Maps;


/**
 * represents saved reports as a Resource. This class handles both single Reports and the list of Reports. <br> 
 *  
 *  With the URI iteraplan/api/reports/, a list of all saved reports can be retrieved. These are the same reports as defined over the web interface.<br> 
 *  
 *  With URIs following the pattern iteraplan/api/reports/{reportIdentifier}, a certain report can be generated and retrieved.<br> 
 *  
 *  This resource allows only GET requests. The query parameter "format" may specify the exact kind of report to generate when accessing a certain saved report.
 */
public class ReportResource extends AResource {

  /**{@inheritDoc}**/
  @Override
  protected Map<String, Object> getInitialArguments() {
    Map<String, Object> arguments = Maps.newHashMap();
    //TODO initialize the arguments here when implementing this Resource, i.e. extract all relevant elements from the request and store them in the arguments map.
    return arguments;

  }
}

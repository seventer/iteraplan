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
package de.iteratec.iteraplan.presentation.dialog.InformationSystem;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.iteratec.iteraplan.businesslogic.service.BusinessMappingService;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.sorting.BuildingBlockComparator;
import de.iteratec.iteraplan.presentation.ajax.DojoUtils;


/**
 * Alias controller that redirects requests from /businessmapping/init.do to /informationsystem/init.do
 */
@Controller
public class BusinessMappingController {

  @Autowired
  private BusinessMappingService businessMappingService;

  @Autowired
  public BusinessMappingController(BusinessMappingService businessMappingService) {
    super();
    this.businessMappingService = businessMappingService;
  }

  @RequestMapping(value = "/businessmapping_projection_businessunit_informationsystem/list.do", method = RequestMethod.GET, produces = "application/json")
  public void projectOnBUToIS(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //ensure IE is not caching the list
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
    response.setDateHeader("Expires", 0); // Proxies.

    List<BusinessMapping> elements = businessMappingService.loadElementList();
    Collections.sort(elements, new BuildingBlockComparator());
    Object jsonData = DojoUtils.projectBusinessMappingsToBUISRelationship(elements, request);
    DojoUtils.write(jsonData, response);
  }

}
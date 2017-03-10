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
package de.iteratec.iteraplan.presentation.dialog.DateInterval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.service.DateIntervalService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.iteraplan.presentation.SessionConstants;
import de.iteratec.iteraplan.presentation.ajax.DojoUtils;
import de.iteratec.iteraplan.presentation.dialog.GuiController;
import de.iteratec.iteraplan.presentation.memory.DialogMemory;

@Controller
public class DateIntervalController extends GuiController {
  
  @Autowired
  private DateIntervalService dateIntervalService;

  public void setDateIntervalService(DateIntervalService dateIntervalService) {
    this.dateIntervalService = dateIntervalService;
  }

  public DateIntervalDialogMemory getDateIntervalDialogMemory() {
    DateIntervalDialogMemory dialogMemory = new DateIntervalDialogMemory();
    dialogMemory.setIntervals(dateIntervalService.findAllDateIntervals());
    return dialogMemory;
  }
  
//  @Override
  protected String getBaseViewMapping() {
    return "dateinterval";
  }
  
  @RequestMapping
  protected void init(ModelMap model, HttpSession session, HttpServletRequest request) {
    super.init(model, session, request);
    DialogMemory dialogMemory = getDateIntervalDialogMemory();
    this.updateGuiContext(dialogMemory);
    model.addAttribute(SessionConstants.DIALOG_MEMORY_LABEL, dialogMemory);
  }
  
  @RequestMapping
  public void list(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //ensure IE is not caching the list
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
    response.setDateHeader("Expires", 0); // Proxies.

    List<DateInterval> elements = getDateIntervalDialogMemory().getIntervals();
    
    Object jsonData = convertToMap(elements, request);
    
    DojoUtils.write(jsonData, response);
  }
  
  @RequestMapping
  public String search(HttpServletRequest request, HttpServletResponse response) {
    
    return "redirect:/" + "dateinterval" + "/init.do";
  }
  
  private Map<String, Object> convertToMap(Collection<DateInterval> data, HttpServletRequest req) {
    List<Map<String, String>> items = new ArrayList<Map<String, String>>();
    for (DateInterval bb : data) {
      Map<String, String> item = new HashMap<String, String>();
      item.put("id", String.valueOf(bb.getId().intValue()));
      item.put("name", bb.getIdentityString());

      String bbJsonUri = URLBuilder.getEntityURL(bb, req, URLBuilder.EntityRepresentation.JSON);
      item.put("elementUri", bbJsonUri);

      items.add(item);
    }

    Map<String, Object> result = new HashMap<String, Object>();
    result.put("identifier", "id");
    result.put("label", "name");
    result.put("items", items);
    return result;
  }

  /**{@inheritDoc}**/
  @Override
  protected String getDialogName() {
    return Dialog.DATE_INTERVAL.getDialogName();
  }
}

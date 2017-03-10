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
package de.iteratec.iteraplan.presentation.dialog.TechnicalComponent;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.sorting.IdentityStringComparator;
import de.iteratec.iteraplan.presentation.SessionConstants;
import de.iteratec.iteraplan.presentation.ajax.DojoUtils;
import de.iteratec.iteraplan.presentation.dialog.GuiSearchController;
import de.iteratec.iteraplan.presentation.memory.SearchDialogMemory;


@Controller
public class TechnicalComponentController extends GuiSearchController<TechnicalComponentDialogMemory> {

  @Autowired
  private TechnicalComponentReleaseService technicalComponentReleaseService;

  @Autowired
  private BuildingBlockTypeService         bbtService;

  @Autowired
  public TechnicalComponentController(TechnicalComponentReleaseService technicalComponentReleaseService) {
    super();
    this.technicalComponentReleaseService = technicalComponentReleaseService;
  }

  @Override
  protected TechnicalComponentDialogMemory getDialogMemory() {
    return new TechnicalComponentDialogMemory();
  }

  @Override
  protected String getDialogName() {
    return Dialog.TECHNICAL_COMPONENT.getDialogName();
  }

  @Override
  protected String getBaseViewMapping() {
    return "technicalcomponent";
  }

  @Override
  protected TypeOfBuildingBlock getTob() {
    return TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE;
  }

  /**{@inheritDoc}**/
  @Override
  protected void initHierarchicalTopElement(SearchDialogMemory searchDialogMemory) {
    searchDialogMemory.setHierarchicalTopElement(null); // no hierarchy => set top element to null     
  }

  @Override
  public PagedListHolder<?> searchAll(TechnicalComponentDialogMemory dialogMemory, ModelMap model) {

    List<TechnicalComponentRelease> tcrList = technicalComponentReleaseService.getTechnicalComponentReleasesBySearch(
        dialogMemory.toTechnicalComponent(), UserContext.getCurrentUserContext().isShowInactiveStatus());
    PagedListHolder<TechnicalComponentRelease> results = new PagedListHolder<TechnicalComponentRelease>(tcrList);

    return results;
  }

  @Override
  protected void addBuildingBlockTypeToModel(ModelMap model) {
    model.addAttribute(SessionConstants.MVC_BB_TYPE, bbtService.getBuildingBlockTypeByType(getTob()));
  }

  @RequestMapping
  public void list(@RequestParam(value = "showInactive", defaultValue = "true") Boolean showInactive,
                   @RequestParam(value = "escapeHtml", defaultValue = "false") boolean escapeHtml, HttpServletRequest request,
                   HttpServletResponse response) throws IOException {
    //ensure IE is not caching the list
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
    response.setDateHeader("Expires", 0); // Proxies.

    List<TechnicalComponentRelease> elements = technicalComponentReleaseService.loadElementList(showInactive.booleanValue());
    Collections.sort(elements, new IdentityStringComparator());
    Object jsonData = DojoUtils.convertToMap(elements, escapeHtml, request);
    DojoUtils.write(jsonData, response);
  }

  @Override
  protected boolean loadQueryForm(TechnicalComponentDialogMemory dialogMemory, ModelMap model) {

    if (dialogMemory.getQueryForm() == null) {
      super.initQueryForm(dialogMemory);
    }

    // explicitly exclude any start nor end date as part of search criteria
    dialogMemory.getQueryForm().getQueryUserInput().getTimespanQueryData().setEndDateAsString(null);
    dialogMemory.getQueryForm().getQueryUserInput().getTimespanQueryData().setStartDateAsString(null);

    //explicitly include all stati as part of search criteria
    dialogMemory.getQueryForm().getQueryUserInput().getStatusQueryData().setSelectedStatus(TypeOfStatus.stringValues());

    return super.loadQueryForm(dialogMemory, model);
  }

}

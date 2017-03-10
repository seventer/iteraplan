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
package de.iteratec.iteraplan.presentation.dialog.Search;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.iteratec.iteraplan.businesslogic.service.SearchService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.dto.SearchDTO;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.dialog.GuiController;
import de.iteratec.iteraplan.presentation.problemreports.IteraplanProblemReport;


@Controller
public class SearchController extends GuiController {

  private static final Logger LOGGER              = Logger.getIteraplanLogger(SearchController.class);

  private static final String DIALOG_MEMORY_LABEL = "dialogMemory";

  @Override
  protected String getDialogName() {
    return Dialog.SEARCH.getDialogName();
  }

  @Autowired
  private SearchService searchService;

  @Override
  @RequestMapping
  public void init(ModelMap model, HttpSession session, HttpServletRequest request) {

    super.init(model, session, request);

    GuiContext context = GuiContext.getCurrentGuiContext();

    SearchDialogMemory dialogMemory;
    if (context.hasDialogMemory(getDialogName())) {
      // if the page was already accessed once, use the existing dialogMemory
      dialogMemory = (SearchDialogMemory) context.getDialogMemory(getDialogName());
    }
    else {
      dialogMemory = new SearchDialogMemory();
    }

    updateGuiContext(dialogMemory);
    model.addAttribute(DIALOG_MEMORY_LABEL, dialogMemory);
  }

  @RequestMapping
  public String globalsearch(@RequestParam(value = "globalSearchBox", required = false) String q, ModelMap model) {

    SearchDialogMemory dialogMemory = new SearchDialogMemory();

    if (isSearchValid(q)) {
      dialogMemory.setSearchField(q);
      executeSearch(dialogMemory);
    }
    model.addAttribute(DIALOG_MEMORY_LABEL, dialogMemory);

    return "search/search";
  }

  @RequestMapping
  public String filteredSearch(@RequestParam(value = "q", required = false) String q, @RequestParam(value = "f", required = false) String f, /* BuildingBlockTypeFilter */
                               ModelMap model) {

    SearchDialogMemory dialogMemory = new SearchDialogMemory();

    if (isSearchValid(q)) {
      dialogMemory.setSearchField(q);
      dialogMemory.setBuildingBlockTypeFilter(f);
      executeSearch(dialogMemory);
    }
    model.addAttribute(DIALOG_MEMORY_LABEL, dialogMemory);

    return "search/search";
  }

  @RequestMapping
  public void search(@ModelAttribute(DIALOG_MEMORY_LABEL) SearchDialogMemory dialogMemory, HttpSession session, HttpServletRequest request) {

    // reset: Adds a new dto with an empty searchList to the dialogMemory and resets the serachField
    if ("reset".equals(dialogMemory.getRequestType())) {
      dialogMemory.setSearchField("");
      executeSearch(dialogMemory);
    }
    else if (isSearchValid(dialogMemory.getSearchField())) {
      // get results
      executeSearch(dialogMemory);
    }
  }

  private void executeSearch(SearchDialogMemory dialogMemory) {
    // get results
    SearchDTO dto = searchService.getSearchDTO(dialogMemory);

    searchService.getSearchBuildingBlocks(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, dialogMemory.getSearchField());

    dialogMemory.setSearchDTO(dto);
    dialogMemory.setNumberOfResults(dto.getSearchMultiMap().size());

    // always update the guiContext
    updateGuiContext(dialogMemory);
  }

  private boolean isSearchValid(String searchField) {
    if (StringUtils.isBlank((searchField))) {
      LOGGER.info("User tried to search for empty String");
      return false;
    }
    // compares the searchField with a list of illegal characters
    else if ((searchField.compareTo("+") == 0) || (searchField.compareTo("~") == 0 || (searchField.compareTo("!") == 0))
        || (searchField.compareTo("\"") == 0) || (searchField.compareTo("(") == 0) || (searchField.compareTo(")") == 0)
        || (searchField.compareTo("{") == 0) || (searchField.compareTo("[") == 0) || (searchField.compareTo("]") == 0)
        || (searchField.compareTo("}") == 0) || (searchField.compareTo("?") == 0) || (searchField.compareTo("\\") == 0)
        || (searchField.compareTo("-") == 0) || (searchField.compareTo(":") == 0) || (searchField.compareTo("^") == 0)) {
      LOGGER.info("User tried to search for forbidden character");
      return false;
    }
    else {
      return true;
    }
  }

  @ExceptionHandler(IteraplanBusinessException.class)
  public ModelAndView handleIteraplanBusinessException(Throwable ex, HttpServletRequest req, HttpServletResponse resp) {
    ModelAndView mav = new ModelAndView("search/search");

    this.init(mav.getModelMap(), req.getSession(), req);
    mav.getModelMap().addAttribute(Constants.JSP_ATTRIBUTE_EXCEPTION_MESSAGE, ex.getLocalizedMessage());

    IteraplanProblemReport.createFromController(ex, req);

    return mav;
  }
}

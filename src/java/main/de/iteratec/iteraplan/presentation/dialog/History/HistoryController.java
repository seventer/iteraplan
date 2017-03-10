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
package de.iteratec.iteraplan.presentation.dialog.History;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.iteratec.iteraplan.businesslogic.service.HistoryService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.problemreports.IteraplanProblemReport;


/**
 * Controller to retrieve history
 * Example URL: https://localhost:8443/iteraplan/history/local.do?id=148&buildingBlockType=BusinessUnit&dateFrom=&dateTo=
 * @author rge
 */
@Controller
public class HistoryController {

  private static final Logger LOGGER = Logger.getIteraplanLogger(HistoryController.class);

  @Autowired
  private HistoryService      historyService;

  @Autowired
  public HistoryController(HistoryService historyService) {
    this.historyService = historyService;
  }

  /**
   * URL Handler method that returns an HTML view of the history of one building block, narrowed down by provided criteria
   * @param bbId Building Block ID
   * @param bbType Building Block type code, which can be decoded by {@link TypeOfBuildingBlock#fromInitialCapString(String)}
   * @param page The number of the page to return, depending on {@code pageSize}. Zero-based. May be {@code null}, in which case the default value 0 is used.
   * @param pageSize The number of history events on the returned page. -1 is interpreted as "everything". May be {@code null}, in which case the default value -1 is used.
   * @param dateFromStr The start date of the time range to filter history events for. This is expected to be in ISO date format (pattern yyyy-MM-dd)!
   * @param dateToStr The end date (inclusive) of the time range to filter history events for. This is expected to be in ISO date format (pattern yyyy-MM-dd)!
   */
  @SuppressWarnings("boxing")
  @RequestMapping
  public ModelAndView localHistory(@RequestParam(value = "id", required = true) String bbId,
                                   @RequestParam(value = "buildingBlockType", required = true) String bbType,
                                   @RequestParam(value = "page", required = false) String page,
                                   @RequestParam(value = "pageSize", required = false) String pageSize,
                                   @RequestParam(value = "dateFrom", required = false) String dateFromStr,
                                   @RequestParam(value = "dateTo", required = false) String dateToStr) {

    ModelAndView modelAndView = new ModelAndView("history/local");

    // Check if user may see history, and note so JSP can show a nice error. If not, return
    Boolean hasPermission = Boolean.valueOf(UserContext.getCurrentPerms().getUserHasFuncPermViewHistory());
    modelAndView.addObject("isHasViewHistoryPermission", hasPermission);
    if (!hasPermission.booleanValue()) {
      return modelAndView;
    }

    int id = parseInt(bbId, -1);

    // Default to showing page 0 (first page), if unspecified
    int curPage = parseInt(page, 0);
    // current page must not be negative
    curPage = Math.max(curPage, 0);

    // Default -1 means infinite results Per Page
    int pageSizeInt = parseInt(pageSize, -1);
    // make sure it's not 0, /0 error later; and make all values < -1 turn into -1
    if (pageSizeInt <= 0) {
      pageSizeInt = -1;
    }

    DateTimeFormatter isoDateFormatter = ISODateTimeFormat.date();
    DateTime dateFrom = null;
    DateTime dateTo = null;
    if (StringUtils.isNotEmpty(dateFromStr)) {
      try {
        LocalDate date = LocalDate.parse(dateFromStr, isoDateFormatter);
        dateFrom = date.toDateTimeAtStartOfDay();
      } catch (IllegalArgumentException ex) {
        // invalid date format, ignore
      }
    }

    if (StringUtils.isNotEmpty(dateToStr)) {
      try {
        // assumption: we parsed from a date with no time which gave us the beginning of that day, but
        // we want to include the whole day, so add 1 day
        LocalDate date = LocalDate.parse(dateToStr, isoDateFormatter).plusDays(1);
        dateTo = date.toDateTimeAtStartOfDay();
      } catch (IllegalArgumentException ex) {
        // invalid date format, ignore
      }
    }

    HistoryResultsPage resultsPage;
    try {
      resultsPage = historyService.getLocalHistoryPage(getClassFromTypeString(bbType), id, curPage, pageSizeInt, dateFrom, dateTo);
    } catch (Exception e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.HISTORY_RETRIEVAL_GENERAL_ERROR, e);
    }
    assert resultsPage != null;

    modelAndView.addObject("resultsPage", resultsPage);
    modelAndView.addObject("isHistoryEnabled", Boolean.valueOf(historyService.isHistoryEnabled()));

    return modelAndView;
  }

  /**
   * Turn String into Integer, with default value returned if string is null/empty
   * @param strInt string to convert to an Integer
   * @param defaultVal returned if String is null/empty
   */
  private int parseInt(String strInt, int defaultVal) {
    int result = defaultVal;

    if (StringUtils.isNotEmpty(strInt)) {
      try {
        result = Integer.parseInt(strInt);
      } catch (NumberFormatException e) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INVALID_REQUEST_PARAMETER, e);
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private <T extends BuildingBlock> Class<T> getClassFromTypeString(String bbType) {
    if ("BuildingBlock".equals(bbType)) {
      return (Class<T>) BuildingBlock.class;
    }

    TypeOfBuildingBlock tob = TypeOfBuildingBlock.fromInitialCapString(bbType);
    return tob.getAssociatedClass();
  }

  @ExceptionHandler(IteraplanTechnicalException.class)
  public ModelAndView handleException(Throwable ex, HttpServletRequest req, HttpServletResponse resp) {
    ModelAndView mav = new ModelAndView("errorOutsideFlow");
    mav.addObject(Constants.JSP_ATTRIBUTE_EXCEPTION_MESSAGE, ex.getLocalizedMessage());
    LOGGER.error("During history retrieval, an error occurred", ex);
    IteraplanProblemReport.createFromController(ex, req);
    resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // status code 204
    return mav;
  }

}
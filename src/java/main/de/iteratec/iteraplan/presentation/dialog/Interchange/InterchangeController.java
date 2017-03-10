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
package de.iteratec.iteraplan.presentation.dialog.Interchange;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.reports.interchange.InterchangeBean;
import de.iteratec.iteraplan.businesslogic.reports.interchange.InterchangeDestination;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.presentation.problemreports.IteraplanProblemReport;


@Controller
public class InterchangeController {

  private static final Logger LOGGER = Logger.getIteraplanLogger(InterchangeController.class);

  public String interchange(ModelMap model, InterchangeBean interchangeBean, HttpServletRequest request) {

    if (InterchangeDestination.GRAPHIC_INFORMATION_FLOW.equals(interchangeBean.getInterchangeDestination())) {
      return interchangeToInformationFlow(interchangeBean, request);
    }
    else if (InterchangeDestination.GRAPHIC_CLUSTER.equals(interchangeBean.getInterchangeDestination())) {
      return interchangeToCluster(interchangeBean, request);
    }
    else if (InterchangeDestination.GRAPHIC_IS_LANDSCAPE_CONTENT.equals(interchangeBean.getInterchangeDestination())
        || InterchangeDestination.GRAPHIC_IS_LANDSCAPE_X_AXIS.equals(interchangeBean.getInterchangeDestination())
        || InterchangeDestination.GRAPHIC_IS_LANDSCAPE_Y_AXIS.equals(interchangeBean.getInterchangeDestination())
        || InterchangeDestination.GRAPHIC_TC_LANDSCAPE_CONTENT.equals(interchangeBean.getInterchangeDestination())
        || InterchangeDestination.GRAPHIC_TC_LANDSCAPE_X_AXIS.equals(interchangeBean.getInterchangeDestination())
        || InterchangeDestination.GRAPHIC_TC_LANDSCAPE_Y_AXIS.equals(interchangeBean.getInterchangeDestination())) {
      return interchangeToLandscape(interchangeBean, request);
    }
    else if (InterchangeDestination.GRAPHIC_MASTERPLAN.equals(interchangeBean.getInterchangeDestination())) {
      return interchangeToMasterplan(interchangeBean, request);
    }
    else if (InterchangeDestination.GRAPHIC_PORTFOLIO.equals(interchangeBean.getInterchangeDestination())) {
      return interchangeToPortfolio(interchangeBean, request);
    }
    else if (InterchangeDestination.MASSUPDATE.equals(interchangeBean.getInterchangeDestination())) {
      return interchangeToMassUpdate(interchangeBean, request);
    }
    else if (InterchangeDestination.PIE_BAR.equals(interchangeBean.getInterchangeDestination())) {
      return interchangeToPieOrBarChart(interchangeBean, request);
    }
    else if (InterchangeDestination.TABULAR_REPORTS.equals(interchangeBean.getInterchangeDestination())) {
      return interchangeToTabReports(interchangeBean, request);
    }

    return "start/start";
  }

  private String interchangeToInformationFlow(InterchangeBean interchangeBean, HttpServletRequest request) {
    String idList = buildIdString(interchangeBean.getSelectedIds());

    return "redirect:" + getApplicationUrl(request) + "/show/graphicalreporting/informationflowdiagram?_eventId=fromInterchange&idList=" + idList;
  }

  private String interchangeToLandscape(InterchangeBean interchangeBean, HttpServletRequest request) {
    String idList = buildIdString(interchangeBean.getSelectedIds());

    return "redirect:" + getApplicationUrl(request) + "/show/graphicalreporting/landscapediagram?_eventId=fromInterchange&bbType="
        + interchangeBean.getTypeOfBuildingBlock().getPluralValue() + "&idList=" + idList + "&diagramType="
        + interchangeBean.getInterchangeDestination().getValue();
  }

  private String interchangeToCluster(InterchangeBean interchangeBean, HttpServletRequest request) {
    String idList = buildIdString(interchangeBean.getSelectedIds());

    return "redirect:" + getApplicationUrl(request) + "/show/graphicalreporting/clusterdiagram?_eventId=fromInterchange&bbType="
        + interchangeBean.getTypeOfBuildingBlock().getPluralValue() + "&idList=" + idList;
  }

  private String interchangeToPortfolio(InterchangeBean interchangeBean, HttpServletRequest request) {
    String idList = buildIdString(interchangeBean.getSelectedIds());

    return "redirect:" + getApplicationUrl(request) + "/show/graphicalreporting/portfoliodiagram?_eventId=fromInterchange&bbType="
        + interchangeBean.getTypeOfBuildingBlock().getPluralValue() + "&idList=" + idList;
  }

  private String interchangeToMasterplan(InterchangeBean interchangeBean, HttpServletRequest request) {
    String idList = buildIdString(interchangeBean.getSelectedIds());

    return "redirect:" + getApplicationUrl(request) + "/show/graphicalreporting/masterplandiagram?_eventId=fromInterchange&bbType="
        + interchangeBean.getTypeOfBuildingBlock().getPluralValue() + "&idList=" + idList;
  }

  private String interchangeToMassUpdate(InterchangeBean interchangeBean, HttpServletRequest request) {
    String idList = buildIdString(interchangeBean.getSelectedIds());

    return "redirect:" + getApplicationUrl(request) + "/show/massupdate?_eventId=fromInterchange&bbType="
        + interchangeBean.getTypeOfBuildingBlock().getPluralValue() + "&idList=" + idList;
  }

  private String interchangeToPieOrBarChart(InterchangeBean interchangeBean, HttpServletRequest request) {
    String idList = buildIdString(interchangeBean.getSelectedIds());

    return "redirect:" + getApplicationUrl(request) + "/show/graphicalreporting/piebardiagram?_eventId=fromInterchange&bbType="
        + interchangeBean.getTypeOfBuildingBlock().getPluralValue() + "&idList=" + idList;
  }

  private String interchangeToTabReports(InterchangeBean interchangeBean, HttpServletRequest request) {
    String idList = buildIdString(interchangeBean.getSelectedIds());

    return "redirect:" + getApplicationUrl(request) + "/show/tabularreporting?_eventId=fromInterchange&bbType="
        + interchangeBean.getTypeOfBuildingBlock().getPluralValue() + "&idList=" + idList + "&statusSelected="
        + interchangeBean.getIsStatusSelected() + "&statusAV=" + interchangeBean.getSelectedStatusValue() + "&sealSelected="
        + interchangeBean.getIsSealSelected() + "&sealAV=" + interchangeBean.getSelectedSealValue();
  }

  private static String getApplicationUrl(HttpServletRequest request) {
    return URLBuilder.getApplicationURL(request);
  }

  private static String buildIdString(Integer[] ids) {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    for (int i = 0; i < ids.length; i++) {
      builder.append(ids[i]);
      if (i != (ids.length - 1)) {
        builder.append(",");
      }
    }
    builder.append("]");
    return builder.toString();
  }

  @ExceptionHandler(Throwable.class)
  public String handleException(Throwable ex, HttpServletRequest req, HttpServletResponse resp) {
    LOGGER.error("An error occurred while passing query results around", ex);
    IteraplanProblemReport.createFromController(ex, req);
    return "errorOutsideFlow";
  }

}

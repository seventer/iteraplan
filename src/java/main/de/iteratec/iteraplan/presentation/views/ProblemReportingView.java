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
package de.iteratec.iteraplan.presentation.views;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.presentation.problemreports.IteraplanProblemReport;


/**
 * A Spring MVC view for the download of a (zipped) {@link IteraplanProblemReport}.
 */
public class ProblemReportingView extends AbstractView {

  private static final Logger    LOGGER        = Logger.getIteraplanLogger(ProblemReportingView.class);

  private static final String    MIME_TYPE_ZIP = "application/zip";

  private IteraplanProblemReport problemReport;

  public ProblemReportingView(IteraplanProblemReport problemReport) {
    this.problemReport = problemReport;
  }

  /**{@inheritDoc}**/
  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
    OutputStream os;
    try {
      os = response.getOutputStream();

      if (problemReport == null) {
        response.setContentType("text/plain");
        String expiredText = MessageAccess.getString("problemreport.expired.content");
        byte[] textAsBytes = expiredText.getBytes();
        response.setContentLength(textAsBytes.length);
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_GONE);
        os.write(textAsBytes);
      }
      else {
        response.setHeader("Content-disposition", "attachment; filename=" + getFilename());
        response.setContentType(MIME_TYPE_ZIP);
        response.setContentLength(problemReport.getData().length);
        response.setStatus(HttpServletResponse.SC_OK);
        os.write(problemReport.getData());
      }
      response.flushBuffer();
    } catch (IOException e) {
      LOGGER.warn("Unable to write problem report data to HTTP response output stream.");
    }
  }

  private String getFilename() {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HHmmss", UserContext.getCurrentLocale());
    String timestamp = formatter.format(new Date(problemReport.getProblemTime()));
    return "ProblemReport_" + timestamp + ".zip";
  }
}

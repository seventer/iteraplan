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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.Log4jProperties;
import de.iteratec.iteraplan.common.util.ZipUtil;


public class AuditLogFileView extends AbstractView {
  private static final Logger LOGGER        = Logger.getIteraplanLogger(AuditLogFileView.class);
  private static final String MIME_TYPE_ZIP = "application/zip";

  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {

    LOGGER.info("Entering AuditLogFileResponseGenerator:generateResponse()");

    String auditLogString = null;
    try {
      auditLogString = Log4jProperties.getProperties().getProperty(Log4jProperties.PROP_AUDIT_LOG_FILE);
    } catch (IteraplanTechnicalException e1) {
      LOGGER.error("Error reading audit log file settings from log4j property file.", e1);
    }

    if (auditLogString == null) {
      LOGGER.error("Could not retrieve audit log file name from iteraplan properties.");
    }

    File auditLog = new File(auditLogString);
    if (!auditLog.exists()) {
      LOGGER.error("AuditLogfile cannot be found. Path is: " + auditLogString);
    }

    File auditLog2 = new File(auditLogString + ".1");
    File[] filesToZip = new File[] { auditLog, auditLog2 };
    ByteArrayOutputStream out = null;

    try {
      out = new ByteArrayOutputStream();
      ZipUtil.zipFilesToStream(filesToZip, out);

      response.setContentLength(out.size());
      response.setContentType(MIME_TYPE_ZIP);
      response.setHeader("Content-disposition", "attachment; filename=iteraplanAuditLogs.zip");

      try {
        LOGGER.info("Write audit log file output.");
        OutputStream os = response.getOutputStream();
        out.writeTo(response.getOutputStream());
        os.flush();
        //os.close();
      } catch (IOException e) {
        if (e.getCause() instanceof SocketException) {
          // happens if the user cancels the download
          LOGGER.info("Download of audit log file cancelled by user or network error.");
        }
        else {
          throw e;
        }
      }
    } catch (IOException e) {
      throw new IteraplanTechnicalException(e);
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          LOGGER.error("Could not close audit log output stream.", e);
        }
      }
    }
  }
}

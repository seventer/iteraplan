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
package de.iteratec.iteraplan.presentation.responsegenerators;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import javax.servlet.http.HttpServletResponse;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.writer.AbstractProjectWriter;
import de.iteratec.iteraplan.businesslogic.exchange.msproject.MsProjectExport;
import de.iteratec.iteraplan.businesslogic.exchange.msproject.MsProjectExporterBase.ExportType;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


public class MsProjectResponseGenerator {

  private static final Logger LOGGER                = Logger.getIteraplanLogger(MsProjectResponseGenerator.class);

  private static final String ITERAPLAN             = "iteraplan";

  private static final String MSPROJECT_FILENAME    = "MsProject";
  private static final String MSPROJECT_FILETYPE    = "xml";

  private static final String GANTTPROJECT_FILENAME = "GanttProject";
  private static final String GANTTPROJECT_FILETYPE = "mpx";

  /** MIME Content Type for MS-Project */
  private static final String MIME_TYPE_MSPROJECT   = "application/vnd.ms-project";

  public void generateResponse(HttpServletResponse response, MsProjectExport msProjExport, ExportType type) {

    AbstractProjectWriter writer = null;
    StringBuilder filename = new StringBuilder();

    // common prefix
    filename.append(ITERAPLAN);

    switch (type) {
      case MPX_WITHOUT_SUBORDINATED_BLOCKS:
      case MPX_WITH_SUBORDINATED_BLOCKS:
        writer = new MPXWriter();
        filename.append(GANTTPROJECT_FILENAME);
        filename.append(".");
        filename.append(GANTTPROJECT_FILETYPE);
        break;
      case XML_WITHOUT_SUBORDINATED_BLOCKS:
      case XML_WITH_SUBORDINATED_BLOCKS:
        writer = new MSPDIWriter();
        filename.append(MSPROJECT_FILENAME);
        filename.append(".");
        filename.append(MSPROJECT_FILETYPE);
        break;
      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }

    response.setContentType(MIME_TYPE_MSPROJECT);
    response.setHeader("Content-disposition", "attachment; filename=" + filename);

    try {
      OutputStream os = response.getOutputStream();
      ProjectFile file = msProjExport.createMsProjectFile();
      writer.write(file, os);

      os.flush();
    } catch (SocketException e) {
      // happens if the user cancels the download
      LOGGER.info("Download of Ms-Project file cancelled by user or network error.", e);
    } catch (IOException e) {
      LOGGER.info("Download of Ms-Project file cancelled by user or network error.", e);
    }

  }

}

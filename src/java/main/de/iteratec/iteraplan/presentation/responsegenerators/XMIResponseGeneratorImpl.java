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
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.XmiExportService;
import de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.XmiServiceForTabReporting;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;


public class XMIResponseGeneratorImpl implements XMIResponseGenerator {

  private static final Logger             LOGGER           = Logger.getIteraplanLogger(XMIResponseGeneratorImpl.class);

  public static final String              CONTENT_TYPE_XMI = "application/xml";
  public static final String              CONTENT_TYPE_ZIP = "application/zip";

  private final XmiExportService          xmiSerializer;
  private final XmiServiceForTabReporting xmiServiceForTabReporting;

  public XMIResponseGeneratorImpl(XmiExportService xmiSerializer, XmiServiceForTabReporting xmiServiceForTabReporting) {
    this.xmiSerializer = xmiSerializer;
    this.xmiServiceForTabReporting = xmiServiceForTabReporting;
  }

  /** {@inheritDoc} */
  public void generateXmiResponseForExport(HttpServletResponse response, String filename) {
    if (!filename.endsWith(".xmi")) {
      throw new IllegalArgumentException("filename parameter must end in '.xmi'");
    }

    // store current Locale and temporarily remove it from UserContext
    Locale loc = UserContext.getCurrentLocale();
    UserContext.getCurrentUserContext().setLocale(null);

    setContentAndHeader(response, CONTENT_TYPE_XMI, filename);

    try {
      OutputStream httpOutputStream = response.getOutputStream();
      xmiSerializer.serializeModel(httpOutputStream);

    } catch (SocketException e) {
      // happens if the user cancels the download
      LOGGER.info("Download of Excel file cancelled by user or network error.", e);
    } catch (IOException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } finally {
      UserContext.getCurrentUserContext().setLocale(loc);
    }
  }

  /** {@inheritDoc} */
  public void generateEcoreForExport(HttpServletResponse response, String filename) {
    if (!filename.endsWith(".ecore")) {
      throw new IllegalArgumentException("filename parameter must end in '.ecore'");
    }

    setContentAndHeader(response, CONTENT_TYPE_XMI, filename);

    try {
      OutputStream httpOutputStream = response.getOutputStream();
      xmiSerializer.serializeMetamodel(httpOutputStream);

    } catch (IOException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }

  }

  /** {@inheritDoc} */
  public void generateXmiAndEcoreZipBundle(HttpServletResponse response, String filename) {
    if (!filename.endsWith(".zip")) {
      throw new IllegalArgumentException("filename parameter must end in '.zip'");
    }

    // store current Locale and temporarily remove it from UserContext
    Locale loc = UserContext.getCurrentLocale();
    UserContext.getCurrentUserContext().setLocale(null);

    setContentAndHeader(response, CONTENT_TYPE_ZIP, filename);

    try {
      response.getOutputStream().write(xmiSerializer.serializeBundle());

    } catch (IOException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } finally {
      UserContext.getCurrentUserContext().setLocale(loc);
    }
  }

  /** {@inheritDoc} */
  public void generateEcoreResponseForTabularReporting(HttpServletResponse response, String filename) {
    if (!filename.endsWith(".ecore")) {
      throw new IllegalArgumentException("filename parameter must end in '.ecore'");
    }

    setContentAndHeader(response, CONTENT_TYPE_XMI, filename);

    try {
      ServletOutputStream httpOutputStream = response.getOutputStream();
      xmiServiceForTabReporting.saveExtendedEPackge(httpOutputStream);
    } catch (IOException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
  }

  /** {@inheritDoc} */
  public void generateCompleteXmlExport(HttpServletResponse response, String filename) {
    if (!filename.endsWith(".xml")) {
      throw new IllegalArgumentException("filename parameter must end in '.xml'");
    }

    setContentAndHeader(response, CONTENT_TYPE_XMI, filename);

    try {
      ServletOutputStream httpOutputStream = response.getOutputStream();
      xmiServiceForTabReporting.generateTotalXmlExport(httpOutputStream);
    } catch (IOException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
  }

  /** {@inheritDoc} */
  public void generateXmlResponseForTabularReporting(HttpServletResponse response, Collection<? extends BuildingBlock> buildingBlocks, String fileName) {
    if (buildingBlocks == null || buildingBlocks.isEmpty()) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }

    if (!fileName.endsWith(".xml")) {
      throw new IllegalArgumentException("filename parameter must end in '.xml'");
    }

    setContentAndHeader(response, CONTENT_TYPE_XMI, fileName);

    try {
      OutputStream httpOutputStream = response.getOutputStream();
      xmiServiceForTabReporting.generateXmlExportFor(buildingBlocks, httpOutputStream);
    } catch (IOException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }

  }

  private void setContentAndHeader(HttpServletResponse response, String contentType, String filename) {
    response.setContentType(contentType);
    response.setHeader("Content-disposition", "attachment;fileName=" + filename);
  }
}

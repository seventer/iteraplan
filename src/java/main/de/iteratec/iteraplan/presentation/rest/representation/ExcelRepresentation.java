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
package de.iteratec.iteraplan.presentation.rest.representation;

import java.io.IOException;
import java.io.OutputStream;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.io.mapper.xls.XlsModelMapper;
import de.iteratec.iteraplan.elasticmi.io.mapper.xls.XlsModelMapper.ExcelFormat;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.metamodel.partial.BasePartialExportMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;


public class ExcelRepresentation extends OutputRepresentation {

  private final RMetamodel metamodel;

  public ExcelRepresentation(String format, RMetamodel metamodel) {
    super(determineType(format));
    this.metamodel = metamodel;
  }

  /**{@inheritDoc}**/
  @Override
  public void write(OutputStream outputStream) throws IOException {
    ExcelFormat xlsFormat = null;
    if (MediaType.APPLICATION_EXCEL.equals(getMediaType())) {
      xlsFormat = ExcelFormat.XLS;
    }
    else if (MediaType.APPLICATION_MSOFFICE_XLSX.equals(getMediaType())) {
      xlsFormat = ExcelFormat.XLSX;
    }
    XlsModelMapper mapper;
    if (ExcelFormat.XLSX.equals(xlsFormat) && BasePartialExportMetamodel.class.isInstance(metamodel)) {
      //just to ensure that partial export is in XLSX format only
      //Note: using NOOP message listener here
      mapper = new XlsModelMapper((BasePartialExportMetamodel) metamodel, null, MessageListener.NOOP_LISTENER, IteraplanProperties.getProperties()
          .getBuildVersion());
    }
    else {
      //Note: Using NOOP message listener here
      mapper = new XlsModelMapper(metamodel, null, MessageListener.NOOP_LISTENER, xlsFormat, IteraplanProperties.getProperties().getBuildVersion());
    }
    mapper.write(ElasticMiContext.getCurrentContext().getContextModel()).write(outputStream);
  }

  private static MediaType determineType(String format) {
    if ("xls".equals(format)) {
      return MediaType.APPLICATION_EXCEL;
    }
    else if ("xlsx".equals(format)) {
      return MediaType.APPLICATION_MSOFFICE_XLSX;
    }
    else {
      throw new IteraplanBusinessException(IteraplanErrorMessages.FORMAT_NOT_SUPPORTED);
    }
  }

  /**{@inheritDoc}**/
  @Override
  public boolean equals(Object other) {
    return super.equals(other) && ExcelRepresentation.class.isInstance(other) && metamodel.equals(((ExcelRepresentation) other).metamodel);
  }

  /**{@inheritDoc}**/
  @Override
  public int hashCode() {
    return super.hashCode() ^ metamodel.hashCode();
  }
}

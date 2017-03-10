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
package de.iteratec.iteraplan.presentation.rest.resource.data;

import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcess;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticmi.messages.Message;
import de.iteratec.iteraplan.presentation.dialog.ExcelImport.ImportStrategy;
import de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication;
import de.iteratec.iteraplan.presentation.rest.ResourceType;


/**
 * represents the entire model as a Resource. <br>
 * Accessible via iteraplan/api/data <br>
 * Internally handled as a union of IteraQL query results for all Substantial Types, which defines the structure of the output.<br>
 * Excel import is available on this resource only.
 */
public class DataResource extends ADataResource {
  private static final Logger LOGGER = Logger.getIteraplanLogger(DataResource.class);

  @Override
  protected Map<String, Object> getInitialArguments() {
    Map<String, Object> arguments = super.getInitialArguments();
    arguments.put(IteraplanRestApplication.KEY_RESPONSE_CONTENT, IteraplanRestApplication.CONTENT_ENTIRE_MODEL);
    return arguments;
  }

  /**{@inheritDoc}**/
  @Override
  protected Representation getJson() {
    Representation resultRepresentation = null;
    try {
      resultRepresentation = process("json", ResourceType.MODEL);
    } catch (Exception e) {
      LOGGER.error(e);
      return getErrorMessage(e);
    }

    return resultRepresentation;
  }

  @Override
  protected Representation getXLS() {
    Representation resultRepresentation = null;
    try {
      resultRepresentation = process("xls", ResourceType.MODEL);

    } catch (Exception e) {
      LOGGER.error(e);
      return getErrorMessage(e);
    }

    return resultRepresentation;
  }

  @Override
  protected Representation getXLSX() {
    Representation resultRepresentation = null;
    try {
      resultRepresentation = process("xls", ResourceType.MODEL);

    } catch (Exception e) {
      LOGGER.error(e);
      return getErrorMessage(e);
    }

    return resultRepresentation;
  }

  @Override
  protected Representation doPostResource(Representation representation) {
    ImportProcess process = null;
    StringBuilder sb = new StringBuilder();
    try {
      ImportStrategy strategy = (ImportStrategy) getArgument(IteraplanRestApplication.KEY_MODE);

      // TODO once import for different formats is added, change import process factory adding a method to
      // return the right process
      process = getImportProcessFactory().createMiExcelImportProcess(strategy, representation.getStream());
      if (!process.executeAllApplicableSteps()) {
        setStatus(Status.SERVER_ERROR_INTERNAL);
      }
      for (Message message : process.getImportProcessMessages().getMessages()) {
        sb.append(message.getMessage());
        sb.append("\n");
      }
    } catch (Exception e) {
      LOGGER.error(e);
      if (process != null) {
        for (Message message : process.getImportProcessMessages().getMessages()) {
          sb.append(message.getMessage());
          sb.append("\n");
        }
      }
      setStatus(Status.SERVER_ERROR_INTERNAL);
      sb.append("ERROR: ");
      sb.append(ExceptionUtils.getStackTrace(e) + "\n");
    }

    return new StringRepresentation(sb.toString(), MediaType.TEXT_PLAIN);
  }
}

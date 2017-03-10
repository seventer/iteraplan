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

import static de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication.KEY_FORMAT;
import static de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication.KEY_ID;
import static de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication.KEY_MODE;
import static de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication.KEY_RESPONSE_CONTENT;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.restlet.data.Parameter;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.ImportProcessFactory;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.JsonMicroImportProcess;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.presentation.dialog.ExcelImport.ImportStrategy;
import de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication;
import de.iteratec.iteraplan.presentation.rest.resource.AResource;


/**
 * Abstract superclass for all DataResources. Since the GET mechanisms are very similar for each of these resources, all functionality is implemented here and common for the subclasses. They only differ in their implementation of {@code initArguments()}. 
 */
public abstract class ADataResource extends AResource {
  private ImportProcessFactory importProcessFactory;

  /**
   * @return a Representation of the requested type. As of now, supports only json and will return an error code 415 for resources of type xmi, xls and xlsx, but json again for any format that is unknown. (This is for testing purposes, in order to allow the API to be accessed via browser without using a specific tool.)
   */
  @Override
  public Representation doGetResource() {
    String format = (String) getArgument(KEY_FORMAT);
    if (format != null && !format.isEmpty()) {

      if ("json".equals(format)) {
        return getJson();
      }
      else if ("xmi".equals(format)) {
        return getXMI();
      }
      else if ("xls".equals(format)) {
        return getXLS();
      }
      else if ("xlsx".equals(format)) {
        return getXLSX();
      }
      else if (IteraplanRestApplication.VALUE_DEFAULT_FORMAT.equals(format)) {
        return getDefault();
      }
      return getErrorMessage(new IteraplanBusinessException(IteraplanErrorMessages.FORMAT_NOT_SUPPORTED));
    }
    return getDefault();
  }

  protected Representation getDefault() {
    return getJson();
  }

  protected Representation getJson() {
    return getErrorMessage(new IteraplanBusinessException(IteraplanErrorMessages.FORMAT_NOT_SUPPORTED));
  }

  protected Representation getXMI() {
    return getErrorMessage(new IteraplanBusinessException(IteraplanErrorMessages.FORMAT_NOT_SUPPORTED));
  }

  protected Representation getXLS() {
    return getErrorMessage(new IteraplanBusinessException(IteraplanErrorMessages.FORMAT_NOT_SUPPORTED));
  }

  protected Representation getXLSX() {
    return getErrorMessage(new IteraplanBusinessException(IteraplanErrorMessages.FORMAT_NOT_SUPPORTED));
  }

  /**
   * {@inheritDoc}*
   */
  @Override
  protected Map<String, Object> getInitialArguments() {
    Map<String, Object> arguments = Maps.newHashMap();

    arguments.put(KEY_MODE, determineStrategy(getReference().getQueryAsForm().getFirst(KEY_MODE)));

    return arguments;
  }

  private ImportStrategy determineStrategy(Parameter mode) {
    if (mode != null) {
      String name = mode.getValue();
      for (ImportStrategy candidate : ImportStrategy.values()) {
        if (candidate.name().equalsIgnoreCase(name)) {
          return candidate;
        }
      }
      throw new IteraplanBusinessException(IteraplanErrorMessages.MODE_UNKNOWN);
    }
    return ImportStrategy.ADDITIVE;
  }

  /**
   * @return The IteraQL query String, URL-Decoded and double-escaped slashes resolved.
   * @throws UnsupportedEncodingException
   */
  protected String extractQuery() {
    String query = (String) getRequest().getAttributes().get(KEY_RESPONSE_CONTENT);

    query = query.replace("%252F", "/");
    query = query.replace("%252f", "/");

    try {
      query = URLDecoder.decode(query, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.UNSUPPORTED_ENCODING);
    }

    query = query.trim() + ";";

    return query;
  }

  /**
   * @param query a properly formatted IteraQL query string, including the delimiting ;, but nothing else afterwards.
   * @return a properly formatted IteraQL query string representing the input with an additional filter by the id defined in the request's URL. Includes the delimiting ;.
   * @throws NumberFormatException if the id attribute of the request is not a proper Integer.
   */
  protected String getQueryWithIdFilter(String query) {
    String idString = (String) getRequest().getAttributes().get(KEY_ID);

    //assert that the id is actually an id in order to return an appropriate error
    try {
      Integer.parseInt(idString);
    } catch (NumberFormatException ex) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.ID_FORMAT_ERROR);
    }

    String result = query.trim().substring(0, query.length() - 1);

    result += "[" + "@id=" + idString + "];";

    return result;

  }

  /**
   * @param process
   */
  protected void setStatus(JsonMicroImportProcess process) {
    if (process.getImportProcessMessages().hasErrors()) {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    }
  }

  public void setImportProcessFactory(ImportProcessFactory importProcessFactory) {
    this.importProcessFactory = importProcessFactory;
  }

  protected ImportProcessFactory getImportProcessFactory() {
    return importProcessFactory;
  }
}

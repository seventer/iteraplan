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

import static de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication.KEY_RESPONSE_CONTENT;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;

import com.google.gson.JsonElement;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.JsonMicroImportProcess;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQlQuery;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.presentation.rest.MicroImportProcessElementResult;
import de.iteratec.iteraplan.presentation.rest.ResourceType;
import de.iteratec.iteraplan.presentation.rest.RestUtils;


/**
 *  represents a resource which is specified by a certain IteraQL query.<br> 
 *  The most common case is that the expression is simply some Type, such that the resource would represent the Set of all BuildingBlocks of that type.<br>  
 *  
 *  The URI of resources of this type match the following schema: iteraplan/api/data/{iteraQLQuery}<br> 
 *  
 *  This resource allows to retrieve the result set of the query via a GET request in a format specified either in the request header or as query parameter (key= "format").<br> 
 *  
 *  Modifications on elements in the result set can be performed with a POST request containing the updated values. The parameter "mode" specifies whether objects may be created and/or deleted.<br> 
 *  
 */
public class QueryResource extends ADataResource {
  private static final Logger LOGGER = Logger.getIteraplanLogger(QueryResource.class);

  @Override
  protected Map<String, Object> getInitialArguments() {
    Map<String, Object> arguments = super.getInitialArguments();
    arguments.put(KEY_RESPONSE_CONTENT, extractQuery());

    return arguments;
  }

  /**{@inheritDoc}**/
  @Override
  protected Representation getJson() {
    Representation resultRepresentation = null;
    try {
      resultRepresentation = process("json", ResourceType.QUERY);
    } catch (Exception e) {
      LOGGER.error(e);
      return getErrorMessage(e);
    }

    return resultRepresentation;
  }

  /**{@inheritDoc}**/
  @Override
  protected Representation getXLSX() {
    Representation resultRepresentation = null;
    try {
      resultRepresentation = process("xlsx", ResourceType.QUERY);
    } catch (Exception e) {
      LOGGER.error(e);
      return getErrorMessage(e);
    }

    return resultRepresentation;
  }

  /**{@inheritDoc}**/
  @Override
  protected Representation doPostResource(Representation representation) {
    Representation result = new EmptyRepresentation();

    try {
      IteraQlQuery query = extractType();
      if (query != null) {
        if (query.isLeft()) {
          JsonMicroImportProcess process = getImportProcessFactory().createMiJsonMicroImportProcess();
          RStructuredTypeExpression type = query.getLeft();
          BigInteger createdID = process.create(type, representation.getStream());
          setStatus(process);
          JsonElement json = new MicroImportProcessElementResult(getRootRef().toString(), process.getImportProcessMessages(), type, createdID)
              .toJson();
          result = new JsonRepresentation(RestUtils.formatToJson(json));
        }
        else {
          throw new IllegalArgumentException("expected the query \"" + query.serialize(false) + "\" to compile to a structured type!");
        }
      }
    } catch (IOException e) {
      LOGGER.error(e);
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    }

    return result;
  }
}

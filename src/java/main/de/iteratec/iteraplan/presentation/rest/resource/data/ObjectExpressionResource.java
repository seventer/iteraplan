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
import static de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication.KEY_SINGLETON;

import java.io.IOException;
import java.io.InputStream;
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
import de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication;
import de.iteratec.iteraplan.presentation.rest.MicroImportProcessElementResult;
import de.iteratec.iteraplan.presentation.rest.MicroImportProcessResult;
import de.iteratec.iteraplan.presentation.rest.ResourceType;
import de.iteratec.iteraplan.presentation.rest.RestUtils;


/**
 *  represents a single BuildingBlock or Relator as a resource.<br> 
 *  
 *  The URI of resources of this type match the following schema: iteraplan/api/data/{buildingBlockType}/{id}<br> 
 *  
 *  Note that the buildingBlockType is internally handled as iteraQL query and the id is applied as a filter. It is technically possible to replace the type with another, more complicated iteraQL-Statement, but this is discouraged. In typical cases, it does not make sense to further limit the set of BuildingBlocks from which the one with the given ID is picked, since the ID is unique over the entire model anyway. Specifying the type of the BuildingBlock is necessary, however, due to the nature of IteraQL.<br> 
 *  
 *  This resource allows to retrieve the result set of the query via a GET request in a format specified either in the request header or as query parameter (key= "format").<br> 
 *  
 *  Modifications on elements in the result set can be performed with a POST request containing the updated values. The parameter "mode" specifies whether the object may be created and/or deleted, and if empty or missing attributes are updated.<br> 
 *  
 *  The Object may be deleted with a DELETE request. TODO clarify whether relationships to this object will be updated properly or not, and what the intended behaviour is.<br>
 *  
 */
public class ObjectExpressionResource extends ADataResource {
  private static final Logger LOGGER = Logger.getIteraplanLogger(ObjectExpressionResource.class);

  @Override
  protected Map<String, Object> getInitialArguments() {
    Map<String, Object> arguments = super.getInitialArguments();
    arguments.put(KEY_RESPONSE_CONTENT, getQueryWithIdFilter(extractQuery()));
    arguments.put(KEY_SINGLETON, Boolean.TRUE);

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
  protected Representation doPutResource(Representation representation) {
    Representation result = new EmptyRepresentation();

    IteraQlQuery query = extractType();
    try {
      InputStream in = representation.getStream();
      if (query != null) {
        if (query.isLeft()) {
          JsonMicroImportProcess process = getImportProcessFactory().createMiJsonMicroImportProcess();
          RStructuredTypeExpression type = query.getLeft();
          String idString = (String) getRequest().getAttributes().get(IteraplanRestApplication.KEY_ID);
          BigInteger id = BigInteger.valueOf(Long.parseLong(idString));
          process.update(id, type, in);
          setStatus(process);
          JsonElement json = new MicroImportProcessElementResult(getRootRef().toString(), process.getImportProcessMessages(), type, id).toJson();
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

  /**{@inheritDoc}**/
  @Override
  protected Representation doDeleteResource() {
    Representation result = new EmptyRepresentation();

    IteraQlQuery query = extractType();
    if (query != null) {
      if (query.isLeft()) {
        JsonMicroImportProcess process = getImportProcessFactory().createMiJsonMicroImportProcess();
        RStructuredTypeExpression type = query.getLeft();
        String idString = (String) getRequest().getAttributes().get(IteraplanRestApplication.KEY_ID);
        process.delete(BigInteger.valueOf(Long.parseLong(idString)), type);
        setStatus(process);
        JsonElement json = new MicroImportProcessResult(process.getImportProcessMessages()).toJson();
        result = new JsonRepresentation(RestUtils.formatToJson(json));
      }
      else {
        throw new IllegalArgumentException("expected the query \"" + query.serialize(false) + "\" to compile to a structured type!");
      }
    }

    return result;
  }
}

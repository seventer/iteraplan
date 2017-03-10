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

import java.util.Map;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.google.gson.JsonObject;

import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.io.mapper.json.JsonQueryMapper;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQl2Compiler;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQl2Exception;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQlQuery;
import de.iteratec.iteraplan.elasticmi.iteraql2.qt.Query;
import de.iteratec.iteraplan.elasticmi.model.BindingSet;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.util.Either;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication;
import de.iteratec.iteraplan.presentation.rest.ResourceType;
import de.iteratec.iteraplan.presentation.rest.RestUtils;


/**
 *
 */
public class JsonQueryRepresentationHandler implements RepresentationHandler {

  /**{@inheritDoc}**/
  @Override
  public Representation process(Request request, Response response, Map<String, Object> arguments) {
    String queryString = (String) arguments.get(IteraplanRestApplication.KEY_RESPONSE_CONTENT);

    Representation result;
    boolean parsingSucceeded = false;
    try {
      Query parsedQuery = IteraQl2Compiler.parseQuery(queryString);
      parsingSucceeded = true;
      IteraQlQuery query = IteraQl2Compiler.compile(ElasticMiContext.getCurrentContext().getContextMetamodel(), parsedQuery);
      if (arguments.get(IteraplanRestApplication.KEY_SINGLETON) != null
          && ((Boolean) arguments.get(IteraplanRestApplication.KEY_SINGLETON)).booleanValue()) {
        //check if object could be found
        Either<ElasticValue<ObjectExpression>, BindingSet> queryResult = query.apply(ElasticMiContext.getCurrentContext().getContextModel());
        if (queryResult.getLeft().isNone()) {
          //NOT FOUND
          response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
          return new EmptyRepresentation();
        }
      }

      JsonObject json = new JsonQueryMapper(ElasticMiContext.getCurrentContext().getContextMetamodel(), request.getRootRef().toString(), query)
          .write(ElasticMiContext.getCurrentContext().getContextModel());

      result = new JsonRepresentation(RestUtils.formatToJson(json));

      response.setEntity(result);
      response.setStatus(Status.SUCCESS_OK);
    } catch (IteraQl2Exception e) {
      if (parsingSucceeded) {
        //Query was syntactically correct, but Type or feature could not be found within context metamodel
        response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
      }
      else {
        //Query could not be parsed correctly => syntactical error
        response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
      }
      result = new StringRepresentation(e.getLocalizedMessage());
    }

    return result;
  }

  /**{@inheritDoc}**/
  @Override
  public boolean supports(ResourceType resourceType) {
    return ResourceType.QUERY.equals(resourceType);
  }

}

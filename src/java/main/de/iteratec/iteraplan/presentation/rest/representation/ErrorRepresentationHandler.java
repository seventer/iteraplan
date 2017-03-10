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

import static de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication.KEY_ERROR_CAUSE;

import java.io.IOException;
import java.util.Map;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.elasticeam.iteraql2.IteraQl2Exception;
import de.iteratec.iteraplan.elasticmi.exception.ElasticMiException;
import de.iteratec.iteraplan.presentation.rest.ResourceType;


/**
 * Returns a plaintext representation of the error message and sets the status of the response accordingly.
 * The {@code Throwable} in the arguments with the key {@code KEY_ERROR_CAUSE} decides which status is returned.
 * 
 * Handled Error Cases are:
 * <ul>
 * <li>{@code IteraplanBusinessException(ITERAQL_FRAMEWORK_EXCEPTION)} - 503: This happens when the user just logged in or the application was just started. The query framework was not yet initialized.</li>
 * <li>{@code IteraplanBusinessException(FORMAT_NOT_SUPPORTED)} - 415: The parameter "format" in a GET request does not match any supported format types.</li> 
 * <li>{@code IteraplanBusinessException(MODE_UNKNOWN)} - 400: The parameter "mode" in a POST request does not match any supported mode.</li>
 * <li>{@code IteraplanBusinessException(ACCESS_NOT_ALLOWED)} - 401: The user does not have permissions to perform the desired action on the given resource.</li>
 * <li>{@code IteraQl2Exception} - 400: The IteraQLQuery could not be parsed. </li>
 * <li>{@code NotFoundException} - 404: The resource is not available. This usually means that the client tried to access a single element (via ID), but no Object of the given type exists in the Database (or, if the client sent a more complicated query along with the request, this might mean that the object with the given ID is not in the result set of the given query.)</li> 
 * <li>{@code IteraplanBusinessException(UNSUPPORTED_ENCODING)} - 400: The client tried to open a resource that should identify a single BuildingBlock with its id, but the part of the ID was not properly formatted.</li>
 * <li>{@code IteraplanBusinessException(ID_FORMAT_ERROR)} - 400: Something is wrong with the encoding of the URL. More specifically, the IteraQL part of the URL could not be URLDecoded.</li>
 * <li>{@code IOException} - 415: Cannot read the document sent along with the request.</li>
 * </ul>
 * 
 */
public class ErrorRepresentationHandler implements RepresentationHandler {

  public static final String NO_SUCH_FORMAT_MESSAGE      = "Resource format not supported.";
  public static final String INTERNAL_SERVER_ERROR       = "An internal server error has occurred.";
  public static final String REQUEST_MISFORMULATED_ERROR = "Request is not a correct iteraQL statement.";
  public static final String RESOURCE_NOT_FOUND_MESSAGE  = "Resource not found.";
  public static final String ID_MALFORMATTED             = "ID String is not a proper Integer.";

  /**{@inheritDoc}**/
  public Representation process(Request request, Response response, Map<String, Object> arguments) {
    if (arguments.get(KEY_ERROR_CAUSE) == null || !Exception.class.isInstance(arguments.get(KEY_ERROR_CAUSE))) {
      response.setStatus(Status.SERVER_ERROR_INTERNAL);
      return new StringRepresentation(INTERNAL_SERVER_ERROR, MediaType.TEXT_PLAIN);
    }

    Exception exception = (Exception) arguments.get(KEY_ERROR_CAUSE);

    if (exception instanceof IteraplanBusinessException) {

      IteraplanBusinessException iteraplanBusinessException = (IteraplanBusinessException) exception;

      switch (iteraplanBusinessException.getErrorCode()) {
        case IteraplanErrorMessages.ITERAQL_FRAMEWORK_EXCEPTION: {
          response.setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, iteraplanBusinessException, iteraplanBusinessException.getEnglishMessage());
          break;
        }
        case IteraplanErrorMessages.FORMAT_NOT_SUPPORTED: {
          response.setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, iteraplanBusinessException, iteraplanBusinessException.getEnglishMessage());
          break;
        }
        case IteraplanErrorMessages.METHOD_NOT_ALLOWED: {
          response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, iteraplanBusinessException, iteraplanBusinessException.getEnglishMessage());
          break;
        }
        case IteraplanErrorMessages.MODE_UNKNOWN: {
          response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, iteraplanBusinessException, iteraplanBusinessException.getEnglishMessage());
          break;
        }
        case IteraplanErrorMessages.ACCESS_NOT_ALLOWED: {
          response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED, iteraplanBusinessException, iteraplanBusinessException.getEnglishMessage());
          break;
        }
        case IteraplanErrorMessages.UNSUPPORTED_ENCODING: {
          response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, iteraplanBusinessException, iteraplanBusinessException.getEnglishMessage());
          break;
        }
        case IteraplanErrorMessages.ID_FORMAT_ERROR: {
          response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, iteraplanBusinessException, iteraplanBusinessException.getEnglishMessage());
          break;
        }
        default: {
          response.setStatus(Status.SERVER_ERROR_INTERNAL, iteraplanBusinessException, iteraplanBusinessException.getEnglishMessage());
          break;
        }
      }

      return new StringRepresentation(iteraplanBusinessException.getEnglishMessage(), MediaType.TEXT_PLAIN);
    }

    if (exception instanceof IteraQl2Exception) {
      //entered query was invalid
      response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, exception, exception.getMessage());
      return new StringRepresentation(exception.getMessage(), MediaType.TEXT_PLAIN);
    }
    if (exception instanceof ElasticMiException) {
      response.setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, exception, exception.getMessage());
      return new StringRepresentation(exception.getMessage(), MediaType.TEXT_PLAIN);
    }

    if (exception instanceof ResourceException) {
      //Resource was not found
      response.setStatus(((ResourceException) exception).getStatus(), exception, exception.getMessage());
      return new StringRepresentation(exception.getMessage(), MediaType.TEXT_PLAIN);
    }

    if (exception instanceof IOException) {
      response.setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, exception, exception.getMessage());
      return new StringRepresentation(exception.getMessage(), MediaType.TEXT_PLAIN);
    }

    //final case: if an unpredicted error has occurred report a server error
    response.setStatus(Status.SERVER_ERROR_INTERNAL, exception, INTERNAL_SERVER_ERROR);
    return new StringRepresentation(INTERNAL_SERVER_ERROR, MediaType.TEXT_PLAIN);
  }

  /**{@inheritDoc}**/
  public boolean supports(ResourceType resourceType) {
    return true;
  }
}

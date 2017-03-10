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
package de.iteratec.iteraplan.presentation.rest.resource;

import static de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication.KEY_FORMAT;

import java.util.List;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.net.HttpHeaders;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQl2Compiler;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQl2Exception;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQlQuery;
import de.iteratec.iteraplan.elasticmi.iteraql2.qt.Query;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication;
import de.iteratec.iteraplan.presentation.rest.RepresentationHandlers;
import de.iteratec.iteraplan.presentation.rest.ResourceType;


/**
 *  Abstract superclass for all resources. Predefines stub methods to intercept GET, POST, PUT and DELETE requests for resources where they are not explicitly defined. Subclasses should override those methods if they allow access with the respective http verb. <br>
 *  The request is evaluated in a manner such that all relevant parameters, etc. are written to a Map {@code arguments} before actually evaluating the request. This allows serializers to easily access all information they need. <br>
 *  {@code initArguments()} must be implemented by subclasses and called before each request is processed.  
 */
public abstract class AResource extends ServerResource {
  private static final Logger                 LOGGER                  = Logger.getIteraplanLogger(AResource.class);

  private RepresentationHandlers              representationHandlers;

  private static final Map<MediaType, String> MIME_TYPE_TO_FORMAT_MAP = new ImmutableMap.Builder<MediaType, String>() //
                                                                          .put(MediaType.ALL, IteraplanRestApplication.VALUE_DEFAULT_FORMAT) //
                                                                          .put(MediaType.APPLICATION_JSON, "json") //
                                                                          .put(MediaType.APPLICATION_EXCEL, "xls") //
                                                                          .put(MediaType.APPLICATION_MSOFFICE_XLSX, "xlsx") //
                                                                          .put(MediaType.APPLICATION_XMI, "xmi") //
                                                                          .build();

  /**
   * contains the relevant arguments of the request. Typically, these might be:
   * <ol>
   * <li>The Request Object</li>
   * <li>The Response Object</li>
   * <li>Relevant request parameters</li>
   * <li>The IteraQL query String</li>
   * <li>The identifier of a saved visualization (Future Feature!)</li>
   * <li>... and some more to come when write access is implemented. </li> 
   * </ol>
   * 
   * All contents must be accessibly by keys defined in {@link IteraplanRestApplication} as constants. 
   */
  private Map<String, Object>                 arguments               = Maps.newHashMap();

  private void initArguments() {
    arguments = getInitialArguments();
    initFormat();
  }

  /**
   * Initializes the {@code arguments}, i.e. extracts all relevant information from the request object and stores them there. Subclasses must implement this method, since different aspects of the request are relevant for different resources.
   * @return the initial value of the {@code arguments}
   */
  protected abstract Map<String, Object> getInitialArguments();

  protected Representation getErrorMessage(Exception ex) {
    arguments.put(IteraplanRestApplication.KEY_ERROR_CAUSE, ex);
    return representationHandlers.getErrorRepresentationHandler().process(getRequest(), getResponse(), arguments);
  }

  protected Representation process(String format, ResourceType resourceType) {
    return representationHandlers.getHandlerForFormat(format, resourceType).process(getRequest(), getResponse(), arguments);
  }

  /**
   * standard method for processing GET requests. Takes care of all pre-processing, i.e. initializing the arguments and checking permissions. Delegates actual processing to the {@code doGetResource()} method.
   * @return the representation of the result. 
   */
  @Get()
  public final Representation getResource() {
    try {
      initArguments();
    } catch (Exception ex) {
      LOGGER.error(ex);
      return getErrorMessage(ex);
    }
    if (!isUserReadAccessAllowed()) {
      return getErrorMessage(new IteraplanBusinessException(IteraplanErrorMessages.ACCESS_NOT_ALLOWED));
    }
    return doGetResource();
  }

  /**
   * Standard handling method for GET requests. Returns a METHOD_NOT_ALLOWED_ERROR by default. Subclasses must overwrite this method if (and only if) they want GET requests to be handled and place their functionality in this method.
   * @return the representation of the response
   */
  protected Representation doGetResource() {
    return getErrorMessage(new IteraplanBusinessException(IteraplanErrorMessages.METHOD_NOT_ALLOWED));
  }

  /**
   * standard method for processing POST requests. Takes care of all pre-processing, like initializing the arguments and checking permissions. Delegates actual processing to the {@code doPostResource()} method.
   * @param representation the representation of the model part containing the information to change
   * @return the representation of the response. 
   */
  @Post()
  public final Representation postResource(Representation representation) {
    try {
      initArguments();
    } catch (Exception ex) {
      LOGGER.error(ex);
      return getErrorMessage(ex);
    }
    if (!isUserWriteAccessAllowed()) {
      return getErrorMessage(new IteraplanBusinessException(IteraplanErrorMessages.ACCESS_NOT_ALLOWED));
    }
    return doPostResource(representation);
  }

  /**
   * Standard handling method for POST requests. Returns a METHOD_NOT_ALLOWED_ERROR by default. Subclasses must overwrite this method if (and only if) they want POST requests to be handled and place their functionality in this method.
   * @param representation
   * @return the representation of the response
   */
  protected Representation doPostResource(Representation representation) {
    return getErrorMessage(new IteraplanBusinessException(IteraplanErrorMessages.METHOD_NOT_ALLOWED));
  }

  /**
   * standard method for processing OUT requests. Takes care of all pre-processing, like initializing the arguments and checking permissions. Delegates actual processing to the {@code doPutResource()} method.
   * @param representation the representation of the model part to insert
   * @return the representation of the response. 
   */
  @Put()
  public final Representation putResource(Representation representation) {
    initArguments();
    if (!isUserWriteAccessAllowed()) {
      return getErrorMessage(new IteraplanBusinessException(IteraplanErrorMessages.ACCESS_NOT_ALLOWED));
    }
    return doPutResource(representation);
  }

  /**
   * Standard handling method for PUT requests. Returns a METHOD_NOT_ALLOWED_ERROR by default. Subclasses must overwrite this method if (and only if) they want PUT requests to be handled and place their functionality in this method.
   * @param representation
   * @return the representation of the response
   */
  protected Representation doPutResource(Representation representation) {
    return getErrorMessage(new IteraplanBusinessException(IteraplanErrorMessages.METHOD_NOT_ALLOWED));
  }

  /**
   * standard method for processing DELETE requests. Takes care of all pre-processing, like initializing the arguments and checking permissions. Delegates actual processing to the {@code doDeleteResource()} method.
   * @return the representation of the result. 
   */
  @Delete()
  public final Representation deleteResource() {
    initArguments();
    if (!isUserWriteAccessAllowed()) {
      return getErrorMessage(new IteraplanBusinessException(IteraplanErrorMessages.ACCESS_NOT_ALLOWED));
    }
    return doDeleteResource();
  }

  /**
   * Standard handling method for DELETE requests. Returns a METHOD_NOT_ALLOWED_ERROR by default. Subclasses must overwrite this method if (and only if) they want DELETE requests to be handled and place their functionality in this method.
   * @return the representation of the response
   */
  protected Representation doDeleteResource() {
    return getErrorMessage(new IteraplanBusinessException(IteraplanErrorMessages.METHOD_NOT_ALLOWED));
  }

  public void setRepresentationHandlers(RepresentationHandlers representationHandlers) {
    this.representationHandlers = representationHandlers;
  }

  /**
   * Method that checks whether the User has permissions to view a certain resource. MUST be called before each Get request is processed to ensure that users can only see what they are intended to see. Currently supports only functional permissions, i.e. a user is either allowed to access the entire REST API or not.<br>
   * Subclasses are welcome to overwrite this method in order to add specific permissions that need to be granted in order to use it, like the permission for IteraQL for ModelResources.
   * @return if the user has permissions to view the resource.
   */
  private boolean isUserReadAccessAllowed() {
    return UserContext.getCurrentPerms().userHasFunctionalPermission(TypeOfFunctionalPermission.REST);
  }

  /**
   * Method that checks whether the User has permissions to view a certain resource. MUST be called before each POST, PUT and DELETE request is processed to ensure that users can only see what they are intended to see. Currently supports only functional permissions, i.e. a user is either allowed to access the entire REST API or not.  
   * Subclasses are welcome to overwrite this method in order to add specific permissions that need to be granted in order to use it, like the permission for IteraQL for ModelResources.
   * Note that this method is a dummy as of now, only to prepare further expansion of the REST API.
   * @return if the user has permissions to view the resource.
   */
  private boolean isUserWriteAccessAllowed() {
    return UserContext.getCurrentPerms().userHasFunctionalPermission(TypeOfFunctionalPermission.REST);
  }

  /**
   * reads the format from the requests parameters and headers and stores it in the arguments map. Preference is the query parameter "format", but if none is set, the Accept header is evaluated. In order to allow access by browsers, any parameter from the Accept header that contains "html" will be stored as an identifier for the default format.
   */
  private void initFormat() {
    Parameter formatParam = getReference().getQueryAsForm().getFirst(KEY_FORMAT);
    if (formatParam != null) {
      arguments.put(KEY_FORMAT, formatParam.getValue().toLowerCase());
    }
    else {
      if (getRequest() instanceof HttpRequest) {
        List<MediaType> acceptedMediaTypes = getAcceptedMediaTypesOrderedByQuality((HttpRequest) getRequest());

        if (isBrowserRequest((HttpRequest) getRequest()) || acceptedMediaTypes.size() == 0) {
          // workaround for browsers; if html is requested, this is probably a browser
          arguments.put(KEY_FORMAT, IteraplanRestApplication.VALUE_DEFAULT_FORMAT);
        }
        else {
          boolean foundSupportedMediaType = false;
          for (MediaType mt : acceptedMediaTypes) {
            if (MIME_TYPE_TO_FORMAT_MAP.containsKey(mt)) {
              foundSupportedMediaType = true;
              arguments.put(KEY_FORMAT, MIME_TYPE_TO_FORMAT_MAP.get(mt));
              break;
            }
          }

          if (!foundSupportedMediaType) {
            arguments.put(KEY_FORMAT, IteraplanRestApplication.VALUE_UNSUPPORTED_FORMAT);
          }
        }
      }
    }
  }

  private List<MediaType> getAcceptedMediaTypesOrderedByQuality(HttpRequest request) {
    List<Preference<MediaType>> acceptedMediaTypes = request.getClientInfo().getAcceptedMediaTypes();
    Function<Preference<MediaType>, Float> qualityBase = new Function<Preference<MediaType>, Float>() {
      @Override
      public Float apply(Preference<MediaType> input) {
        return Float.valueOf(input.getQuality());
      }
    };
    Ordering<Preference<MediaType>> qualityBasedDescendingOrdering = Ordering.natural().onResultOf(qualityBase).reverse();
    List<Preference<MediaType>> acceptedMediaTypesDescending = qualityBasedDescendingOrdering.sortedCopy(acceptedMediaTypes);
    Function<Preference<MediaType>, MediaType> mediaTypeExtract = new Function<Preference<MediaType>, MediaType>() {
      @Override
      public MediaType apply(Preference<MediaType> input) {
        return input.getMetadata();
      }
    };
    return Lists.newArrayList(Iterables.transform(acceptedMediaTypesDescending, mediaTypeExtract));
  }

  private boolean isBrowserRequest(HttpRequest request) {
    String[] formatStrings = request.getHeaders().getValuesArray(HttpHeaders.ACCEPT, true);
    for (String formatString : formatStrings) {
      if (formatString.toLowerCase().contains("html")) {
        return true;
      }
    }
    return false;
  }

  protected Object getArgument(String key) {
    return arguments.get(key);
  }

  protected IteraQlQuery extractType() {
    boolean parsingSucceeded = false;
    try {
      String queryString = (String) getArgument(IteraplanRestApplication.KEY_RESPONSE_CONTENT);
      Query parsedQuery = IteraQl2Compiler.parseQuery(queryString);
      parsingSucceeded = true;
      return IteraQl2Compiler.compile(ElasticMiContext.getCurrentContext().getContextMetamodel(), parsedQuery);
    } catch (IteraQl2Exception e) {
      LOGGER.error(e);
      if (parsingSucceeded) {
        //Query was syntactically correct, but Type or feature could not be found within context metamodel
        setStatus(Status.CLIENT_ERROR_NOT_FOUND);
      }
      else {
        //Query could not be parsed correctly => syntactical error
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
      }
    }
    return null;
  }
}

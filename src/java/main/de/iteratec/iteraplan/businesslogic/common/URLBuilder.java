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
package de.iteratec.iteraplan.businesslogic.common;

import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.operator.filter.FilteredUniversalType;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;


/**
 * Contains diverse methods to create URL strings to objects in iteraplan.
 */
public final class URLBuilder {

  private static final Logger LOGGER                              = Logger.getIteraplanLogger(URLBuilder.class);

  private static final int    WEB_PORT                            = 80;
  private static final String COLON                               = ":";
  public static final String  FORWARD_SLASH                       = "/";

  private static final int    BUFFER_SIZE                         = 150;

  // MVC specific URL fragment
  public static final String  INIT_DO                             = "/init.do?id=";

  public static final String  FLOW_MAPPING                        = "/show/";

  // Contains the server and application url. Is set in iteraplan.properties when using e.g. a
  // reverse proxy server.
  private static final String APPLICATION_ADDRESS_FROM_PROPERTIES = IteraplanProperties.getProperties().getProperty(
      IteraplanProperties.APPLICATION_ADDRESS_FROM_PROPERTIES);

  private URLBuilder() {
    // no instance needed. Only providing static methods
  }

  public static enum EntityRepresentation {
    FLOW_HTML {
      @Override
      public String getUriComponent() {
        return "";
      }
    },

    JSON {
      @Override
      public String getUriComponent() {
        return "json" + FORWARD_SLASH;
      }
    };

    public abstract String getUriComponent();
  }

  /**
   * Returns the URL of the IdentityEntity object by using the {@link Dialog} enum and extracting
   * the server address out of the flow's RequestContext.
   * 
   * @param entity
   *          An IdentityEntity object.
   * @param context
   *          The current RequestContext of the flow.
   * @return The URL of the BuildingBlock object as String.
   */
  public static String getEntityURL(IdentityEntity entity, RequestContext context) {
    return getEntityURL(entity, (HttpServletRequest) context.getExternalContext().getNativeRequest());
  }

  /**
   * Returns the URL of the IdentityEntity object by using the {@link Dialog} enum.
   * This method links to the HTML/ WebFlow representation of the entity.
   * 
   * @param entity
   *          An IdentityEntity object.
   * @param request
   *          The current HttpServletRequest
   * @return The URL of the BuildingBlock object as String.
   */
  public static String getEntityURL(IdentityEntity entity, HttpServletRequest request) {
    return getEntityURL(entity, request, EntityRepresentation.FLOW_HTML);
  }

  /**
   * Returns the URL of the IdentityEntity object by using the {@link Dialog} enum
   * 
   * @param entity
   *          An IdentityEntity object.
   * @param request
   *          The current HttpServletRequest
   * @param representation
   *          The presentation form of the entity that should be linked to
   * @return The URL of the BuildingBlock object as String.
   */
  public static String getEntityURL(IdentityEntity entity, HttpServletRequest request, EntityRepresentation representation) {
    String serverURL = getApplicationURL(request);

    return getEntityURL(entity, serverURL, representation);
  }

  /**
   * Returns the URL of the IdentityEntity object by using the {@link Dialog} enum in combination
   * with a given server address. This method links to the HTML/ WebFlow representation of the entity.
   * 
   * @param entity
   *          An IdentityEntity object.
   * @param serverURL
   *          The known server + application url. Should look like https://127.0.0.1:8443/iteraplan
   * @return The URL of the BuildingBlock object as String.
   */
  public static String getEntityURL(IdentityEntity entity, String serverURL) {
    return getEntityURL(entity, serverURL, EntityRepresentation.FLOW_HTML);
  }

  /**
   * Returns the URL of the IdentityEntity object by using the {@link Dialog} enum in combination
   * with a given server address.
   * 
   * @param entity
   *          An IdentityEntity object.
   * @param serverURL
   *          The known server + application url. Should look like https://127.0.0.1:8443/iteraplan
   * @param representation
   *          The presentation form of the entity that should be linked to
   * @return The URL of the BuildingBlock object as String.
   */
  public static String getEntityURL(IdentityEntity entity, String serverURL, EntityRepresentation representation) {
    StringBuilder url = new StringBuilder(BUFFER_SIZE);

    // iteraplan properties are checked for server address override
    url.append(StringUtils.defaultIfEmpty(APPLICATION_ADDRESS_FROM_PROPERTIES, serverURL));

    LOGGER.info("The application address from the properties is: " + APPLICATION_ADDRESS_FROM_PROPERTIES);
    LOGGER.info("URL created from the application address: " + url.toString());

    IdEntity linkTarget = deriveLinkTarget(entity);

    String type = Dialog.dialogNameForClass(linkTarget.getClass());
    if (type == null) { // entity class is still unknown --> no link
      return "";
    }
    type = type.toLowerCase(Locale.ENGLISH);
    url.append(FLOW_MAPPING);
    url.append(type);
    url.append(FORWARD_SLASH);
    url.append(representation.getUriComponent());
    url.append(linkTarget.getId());

    return url.toString();
  }

  public static String getEntityURL(UniversalModelExpression instance, UniversalTypeExpression type, String serverURL) {
    TypeOfBuildingBlock tobb = null;

    try {
      tobb = getTOBBForUTE(type);
    } catch (IteraplanTechnicalException e) {
      return ""; //no link for unknown type
    }

    StringBuilder url = new StringBuilder(BUFFER_SIZE);

    // iteraplan properties are checked for server address override
    url.append(StringUtils.defaultIfEmpty(APPLICATION_ADDRESS_FROM_PROPERTIES, serverURL));

    LOGGER.info("The application address from the properties is: " + APPLICATION_ADDRESS_FROM_PROPERTIES);
    LOGGER.info("URL created from the application address: " + url.toString());

    String typeName = Dialog.dialogNameForClass(tobb.getAssociatedClass()).toLowerCase(Locale.ENGLISH);
    url.append(FLOW_MAPPING);
    url.append(typeName);
    url.append(FORWARD_SLASH);
    url.append(instance.getValue(UniversalTypeExpression.ID_PROPERTY));

    return url.toString();
  }

  public static String getEntityURL(ObjectExpression instance, RStructuredTypeExpression type, String serverURL) {
    TypeOfBuildingBlock tobb = null;

    try {
      tobb = getTOBBForStructuredType(type);
    } catch (IteraplanTechnicalException e) {
      return ""; //no link for unknown type
    }

    StringBuilder url = new StringBuilder(BUFFER_SIZE);

    // iteraplan properties are checked for server address override
    url.append(StringUtils.defaultIfEmpty(APPLICATION_ADDRESS_FROM_PROPERTIES, serverURL));

    LOGGER.info("The application address from the properties is: " + APPLICATION_ADDRESS_FROM_PROPERTIES);
    LOGGER.info("URL created from the application address: " + url.toString());

    String typeName = Dialog.dialogNameForClass(tobb.getAssociatedClass()).toLowerCase(Locale.ENGLISH);
    url.append(FLOW_MAPPING);
    url.append(typeName);
    url.append(FORWARD_SLASH);
    RPropertyExpression idProperty = type.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_ID);
    url.append(idProperty.apply(instance).getOne().asInteger());

    return url.toString();
  }

  private static TypeOfBuildingBlock getTOBBForUTE(UniversalTypeExpression type) {
    UniversalTypeExpression baseType = type;

    while (baseType instanceof FilteredUniversalType<?>) {
      baseType = ((FilteredUniversalType<?>) baseType).getBaseType();
    }

    String persistentName = baseType.getPersistentName();

    persistentName = persistentName.replace("InformationFlow", "Transport");

    return TypeOfBuildingBlock.fromInitialCapString(persistentName);
  }

  private static TypeOfBuildingBlock getTOBBForStructuredType(RStructuredTypeExpression type) {
    RStructuredTypeExpression baseType = type.getCanonicBase();

    String persistentName = baseType.getPersistentName();

    persistentName = persistentName.replace("InformationFlow", "Transport");

    return TypeOfBuildingBlock.fromInitialCapString(persistentName);
  }

  /**
   * Checks if the passed entity is suitable for linking to (typically yes). For some exceptional
   * cases, it will find an entity that can be linked.
   * 
   * @param entity
   *          A model entity
   * @return Typically the same object as passed in. For some classes, a related object is returned,
   *         which supports linking
   */
  private static IdEntity deriveLinkTarget(IdEntity entity) {
    // get url element of building block type: consider exceptions from the rule
    IdEntity linkTarget = entity;
    if (entity instanceof BusinessMapping) {
      linkTarget = ((BusinessMapping) entity).getInformationSystemRelease();
    }
    else if (entity instanceof Transport) {
      linkTarget = ((Transport) entity).getInformationSystemInterface();
    }
    else if (entity instanceof Tcr2IeAssociation) {
      linkTarget = ((Tcr2IeAssociation) entity).getTechnicalComponentRelease();
    }
    else if (entity instanceof Isr2BoAssociation) {
      linkTarget = ((Isr2BoAssociation) entity).getInformationSystemRelease();
    }
    return linkTarget;
  }

  /**
   * Retrieves the URL of the application. It should be something like:
   * https://localhost:8443/iteraplan
   * 
   * @param req
   *          The current servlet request providing the necessary information.
   * @return The constructed application URL. Without trailing slash.
   */
  public static String getApplicationURL(HttpServletRequest req) {
    StringBuilder appUrlBuffer = new StringBuilder(BUFFER_SIZE);

    // Iteraplan properties are checked for server address override
    if (StringUtils.isNotEmpty(APPLICATION_ADDRESS_FROM_PROPERTIES)) {
      appUrlBuffer.append(APPLICATION_ADDRESS_FROM_PROPERTIES);
    }
    else {
      appUrlBuffer.append(getServerURL(req));
      appUrlBuffer.append(req.getContextPath());
    }

    return appUrlBuffer.toString();
  }

  /**
   * Retrieves the URL of the server. It is also needed in case of ServletReqeusts, where
   * ContextPath is not available, so URL is without application name. It should be something like:
   * https://localhost:8443 This method is private because the iteraplan.properties are not checked
   * to see if the user has entered an application url by hand as in the other methods. Therefore
   * there could be problems if this method is used directly and the user depends on the property
   * setting.
   * 
   * @param req
   *          The current servlet request providing the necessary information.
   * @return The constructed application URL.
   */
  private static String getServerURL(ServletRequest req) {
    StringBuilder serverUrl = new StringBuilder(BUFFER_SIZE);

    serverUrl.append(req.getScheme());
    serverUrl.append(COLON);
    serverUrl.append(FORWARD_SLASH);
    serverUrl.append(FORWARD_SLASH);
    serverUrl.append(req.getServerName());

    int port = req.getServerPort();
    if (port != WEB_PORT) {
      serverUrl.append(COLON);
      serverUrl.append(Integer.toString(port));
    }

    return serverUrl.toString();
  }

  private static String getRelativeURLforFlow(String context, String dialog, Integer objectId) {
    StringBuilder relativeUrlBuffer = new StringBuilder(BUFFER_SIZE);

    relativeUrlBuffer.append(context);
    relativeUrlBuffer.append(FLOW_MAPPING);
    relativeUrlBuffer.append(StringUtils.lowerCase(dialog));
    relativeUrlBuffer.append(FORWARD_SLASH);
    relativeUrlBuffer.append(objectId);

    return relativeUrlBuffer.toString();
  }

  /**
   * This method returns the relative URL of the object's flow by combining application name, object
   * name and object id. Only use this overloaded method, if
   * {@link #getRelativeURLforFlow(String, IdEntity)} does not work for you.
   * 
   * @param context
   *          The application name. Should be something like /iteraplan.
   * @param entity
   *          The object to link to.
   * @param targetClassHint
   *          The class of the object, if you want to override auto-detection. This class is
   *          determined to derive a dialog that is appropriate for managing <code>entity</code>.
   * @return The relative URL of the flow.
   */
  public static String getRelativeURLforFlow(String context, IdEntity entity, Class<? extends IdEntity> targetClassHint) {
    String dialog = Dialog.dialogNameForClass(targetClassHint);
    return getRelativeURLforFlow(context, dialog, entity.getId());
  }

  /**
   * This method returns the relative URL of the object's flow by combining application name, object
   * name and object id. If the objects cannot be linked to directly, this method determines a
   * related object and will generate a link to that one.
   * <dl>
   * For instance, a business mapping will be replaced by its information system.
   * </dl>
   * 
   * @param context
   *          The application name. Should be something like /iteraplan.
   * @param entity
   *          The object to link to.
   * @return The relative URL of the flow.
   */
  public static String getRelativeURLforFlow(String context, IdEntity entity) {
    IdEntity linkTarget = deriveLinkTarget(entity);
    return getRelativeURLforFlow(context, linkTarget, linkTarget.getClass());
  }

  private static String getRelativeURLforMVC(String context, String dialog, Integer objectId) {
    StringBuilder relativeUrlBuffer = new StringBuilder(BUFFER_SIZE);

    // currently no HTML escaping necessary as no conflicting characters are used
    relativeUrlBuffer.append(context);
    relativeUrlBuffer.append(FORWARD_SLASH);
    relativeUrlBuffer.append(dialog.toLowerCase());
    relativeUrlBuffer.append(INIT_DO);
    relativeUrlBuffer.append(objectId);

    return relativeUrlBuffer.toString();
  }

  /**
   * This method returns the relative URL of the object's MVC page by combining application name,
   * object name and object id. If the objects cannot be linked to directly, this method determines
   * a related object and will generate a link to that one.
   * <dl>
   * For instance, a business mapping will be replaced by its information system.
   * </dl>
   * 
   * @param context
   *          The application name. Should be something like /iteraplan.
   * @param entity
   *          The object to link to.
   * @return The relative URL for the MVC page.
   */
  public static String getRelativeURLforMVC(String context, IdEntity entity) {
    IdEntity linkTarget = deriveLinkTarget(entity);
    String dialog = Dialog.dialogNameForClass(linkTarget.getClass());
    return getRelativeURLforMVC(context, dialog, linkTarget.getId());
  }

  /**
   * Returns the absolute URL of the object's flow page by extracting the server and application url
   * out of the pageContext and adding name, id and flow specific String elements.
   * 
   * @param pageContext
   *          The current PageContext.
   * @param dialog
   *          The object's name.
   * @param objectId
   *          The object's id.
   * @return The absolute URL of the flow.
   */
  public static String getAbsoluteURLforFlow(PageContext pageContext, String dialog, Integer objectId) {
    String applicationUrl = getApplicationURL((HttpServletRequest) pageContext.getRequest());

    return getRelativeURLforFlow(applicationUrl, dialog, objectId);
  }

}

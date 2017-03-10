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
import org.restlet.representation.Representation;

import com.google.gson.JsonArray;

import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.io.metamodel.writer.json.JsonMetamodelWriter;
import de.iteratec.iteraplan.presentation.rest.ResourceType;
import de.iteratec.iteraplan.presentation.rest.RestUtils;


public class JsonMetamodelRepresentationHandler implements RepresentationHandler {
  /**{@inheritDoc}**/
  public Representation process(Request request, Response response, Map<String, Object> arguments) {

    JsonMetamodelWriter writer = new JsonMetamodelWriter();
    JsonArray json = writer.write(ElasticMiContext.getCurrentContext().getContextMetamodel());
    JsonRepresentation resultRepresentation = new JsonRepresentation(RestUtils.formatToJson(json));

    response.setEntity(resultRepresentation);
    response.setStatus(Status.SUCCESS_OK);

    return resultRepresentation;
  }

  /**{@inheritDoc}**/
  public boolean supports(ResourceType resourceType) {
    return ResourceType.METAMODEL.equals(resourceType);
  }

}

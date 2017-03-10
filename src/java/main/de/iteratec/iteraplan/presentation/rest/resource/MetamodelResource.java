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
import static de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication.VALUE_DEFAULT_FORMAT;

import java.util.Map;

import org.restlet.representation.Representation;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.presentation.rest.ResourceType;


/**
 * Resource representing the Metamodel, i.e. all types without Data. Supports GET only and does not react to any additional parameters apart from the requested format.
 */
public class MetamodelResource extends AResource {

  /**
   * @return a Representation of the Metamodel in the requested format. Supports only Json format as of now. If a different format is requested, an according error will be returned and the response will have status code 415. 
   */
  @Override
  public Representation doGetResource() {
    String format = (String) getArgument(KEY_FORMAT);
    if (format == null || format.isEmpty()) {
      return getMetamodelDefault();
    }
    else if ("json".equals(format) || format.equals(VALUE_DEFAULT_FORMAT)) {
      return getMetamodelJson();
    }
    return getErrorMessage(new IteraplanBusinessException(IteraplanErrorMessages.FORMAT_NOT_SUPPORTED));
  }

  public Representation getMetamodelDefault() {
    return getMetamodelJson();
  }

  public Representation getMetamodelJson() {
    Representation resultRepresentation = null;

    try {
      resultRepresentation = process("json", ResourceType.METAMODEL);
    } catch (Exception e) {
      return getErrorMessage(e);
    }

    return resultRepresentation;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Map<String, Object> getInitialArguments() {
    return Maps.newHashMap();
  }
}

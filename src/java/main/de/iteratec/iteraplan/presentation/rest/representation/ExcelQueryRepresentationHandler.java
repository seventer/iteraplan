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
import org.restlet.data.Disposition;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.io.mapper.xls.XlsModelMapper.ExcelFormat;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQl2Compiler;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQl2Exception;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQlQuery;
import de.iteratec.iteraplan.elasticmi.iteraql2.qt.Query;
import de.iteratec.iteraplan.elasticmi.metamodel.partial.BasePartialExportMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.partial.a.APartialExportMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication;
import de.iteratec.iteraplan.presentation.rest.ResourceType;


/**
 *
 */
public class ExcelQueryRepresentationHandler implements RepresentationHandler {

  /**{@inheritDoc}**/
  @Override
  public Representation process(Request request, Response response, Map<String, Object> arguments) {
    String queryString = (String) arguments.get(IteraplanRestApplication.KEY_RESPONSE_CONTENT);
    String format = (String) arguments.get(IteraplanRestApplication.KEY_FORMAT);

    if (!ExcelFormat.XLSX.toString().equalsIgnoreCase(format)) {
      response.setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
      return new EmptyRepresentation();
    }

    Representation result;
    boolean parsingSucceeded = false;
    try {
      Query parsedQuery = IteraQl2Compiler.parseQuery(queryString);
      parsingSucceeded = true;
      IteraQlQuery query = IteraQl2Compiler.compile(ElasticMiContext.getCurrentContext().getContextMetamodel(), parsedQuery);
      if (query.isRight()) {
        response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        return new EmptyRepresentation();
      }

      RStructuredTypeExpression mainType = query.getLeft();

      BasePartialExportMetamodel metamodel = new APartialExportMetamodel(ElasticMiContext.getCurrentContext().getContextMetamodel(), mainType);

      result = new ExcelRepresentation(format, metamodel);

      Disposition attachment = new Disposition(Disposition.TYPE_ATTACHMENT);
      attachment.setFilename("iteraplanExcelData." + format);

      result.setDisposition(attachment);

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

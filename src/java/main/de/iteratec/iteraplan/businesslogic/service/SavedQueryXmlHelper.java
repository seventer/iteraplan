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
package de.iteratec.iteraplan.businesslogic.service;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ReportMemBean;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.xml.LandscapeDiagramXML;
import de.iteratec.iteraplan.model.xml.SerializedQuery;


/**
 * A Helper class for serializing and deserializing the {@link SavedQuery}s XML data.
 *
 */
public final class SavedQueryXmlHelper {
  private static final String SCHEMA_URI = "http://iteratec.iteraplan.com/";
  private static final Logger LOGGER     = Logger.getIteraplanLogger(SavedQueryXmlHelper.class);

  private SavedQueryXmlHelper() {
    //prevents instances of this class
  }

  /**
   * Loads a query from database
   * 
   * @param <T> The XML dto class the XML content reflects
   * @param clazz The XML dto class the XML content reflects
   * @param schemaName The URI of the schema that will be used to validate the XML query
   * @param savedQuery the query object
   * @return The object tree reflecting the XML query. Instance of a JAXB-marshallable class
   */
  @SuppressWarnings("unchecked")
  public static <T extends SerializedQuery<? extends ReportMemBean>> T loadQuery(Class<T> clazz, String schemaName, SavedQuery savedQuery) {
    if (!ReportType.LANDSCAPE.equals(savedQuery.getType()) && clazz.isAssignableFrom(LandscapeDiagramXML.class)) {
      LOGGER.error("requested QueryType ('{0}') does not fit the required QueryType ('{1}')", ReportType.LANDSCAPE,
          savedQuery.getType());
      throw new IteraplanBusinessException(IteraplanErrorMessages.INVALID_REQUEST_PARAMETER, "savedQueryType");
    }

    try {
      String content = savedQuery.getContent();
      if (content == null || content.length() <= 0) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.LOAD_QUERY_EXCEPTION);
      }

      Reader queryDefinitionReader = null;
      try {
        Schema schema = getSchema(schemaName);
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(schema);
        
        queryDefinitionReader = new StringReader(content);
        return (T) unmarshaller.unmarshal(queryDefinitionReader);
      } finally {
        IOUtils.closeQuietly(queryDefinitionReader);
      }
    } catch (SAXException e) {
      LOGGER.error("SAXException in SavedQueryServiceImpl#loadQuery " + e.getLocalizedMessage());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (JAXBException e) {
      LOGGER.error("JAXBException in SavedQueryServiceImpl#loadQuery " + e.getLocalizedMessage());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
  }

  private static Schema getSchema(String schemaName) throws SAXException {
    SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
    URL inStream = SavedQueryServiceImpl.class.getResource(schemaName);
    if (inStream == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.FILE_NOT_FOUND_EXCEPTION);
    }

    return sf.newSchema(inStream);
  }

  private static Marshaller getMarshaller(Class<?> clazz, String schemaName) throws JAXBException, SAXException {
    JAXBContext context = JAXBContext.newInstance(clazz);
    Marshaller m = context.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    m.setSchema(getSchema(schemaName));
    m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SCHEMA_URI + " " + SCHEMA_URI + schemaName);

    return m;
  }

  /**
   * Serializes a JAXB object to an XML string.
   * 
   * @param schema Schema to use.
   * @param xml JAXB object to serialize.
   * @return the XML representation of the passed object
   */
  public static String writeQueryToXMLString(String schema, SerializedQuery<?> xml) {
    try {
      Marshaller marshaller = getMarshaller(xml.getClass(), schema);
      StringWriter sw = new StringWriter();
      marshaller.marshal(xml, sw);

      return sw.toString();
    } catch (JAXBException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (SAXException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
  }
}

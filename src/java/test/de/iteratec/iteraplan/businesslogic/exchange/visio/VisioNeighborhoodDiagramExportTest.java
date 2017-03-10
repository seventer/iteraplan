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
package de.iteratec.iteraplan.businesslogic.exchange.visio;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.common.neighbor.NeighborDiagramDataCreator;
import de.iteratec.iteraplan.businesslogic.exchange.common.neighbor.NeighborhoodDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.neighbor.NeighborhoodDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.visio.neighborhood.VisioNeighborhoodDiagramExport;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.visio.model.Document;


/**
 *
 */
public class VisioNeighborhoodDiagramExportTest extends BaseTransactionalTestSupport {

  @Autowired
  private AttributeTypeService  attributeTypeService;
  @Autowired
  private AttributeValueService attributeValueService;
  @Autowired
  private TestDataHelper2 testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testVisioNeighborhoodDiagramExport() throws IOException, ParserConfigurationException, SAXException {
    
    NeighborhoodDiagram neighborhoodDiagram = new NeighborhoodDiagramCreator(NeighborDiagramDataCreator.createInformationSystems(testDataHelper)[0]).createNeighborhoodDiagram();
    VisioNeighborhoodDiagramExport diagramExport = new VisioNeighborhoodDiagramExport(neighborhoodDiagram, attributeTypeService,
        attributeValueService);
    org.w3c.dom.Document document = createVisioNeighborhoodDiagramExportDocument(diagramExport);

    assertNotNull("Generated Visio XML is null!", document);

    commit();
  }

  private org.w3c.dom.Document createVisioNeighborhoodDiagramExportDocument(VisioNeighborhoodDiagramExport diagramExport) throws IOException,
      ParserConfigurationException,
      SAXException {

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Document exportDoc = diagramExport.createDiagram();
    VisioExportUtils.writeToFile(exportDoc);

    exportDoc.write(out);
    out.close();

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(new ByteArrayInputStream(out.toByteArray()));
  }

}

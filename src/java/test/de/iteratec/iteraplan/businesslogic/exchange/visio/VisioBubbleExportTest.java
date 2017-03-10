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
import static org.junit.Assert.assertNotSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.PortfolioOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;


/**
 * Test class for Visio Exports.
 */
public class VisioBubbleExportTest extends BaseTransactionalTestSupport {
  @Autowired
  private AttributeTypeService  attributeTypeService;
  @Autowired
  private AttributeValueService attributeValueService;
  @Autowired
  private TestDataHelper2       testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testExportEmpty() throws Exception {
    List<InformationSystemRelease> ipuReleases = new ArrayList<InformationSystemRelease>();
    Document document = createVisioBubbleExportDocument(ipuReleases);

    assertNotNull("Generated Visio XML is null!", document);

    commit();
  }

  private Document createVisioBubbleExportDocument(List<InformationSystemRelease> ipuReleases) throws IOException, ParserConfigurationException,
      SAXException {
    PortfolioOptionsBean bob = new PortfolioOptionsBean();
    bob.setPortfolioType(PortfolioOptionsBean.TYPE_XY);

    VisioBubbleExport export = new VisioBubbleExport(ipuReleases, bob, attributeTypeService, attributeValueService);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    de.iteratec.visio.model.Document exportDoc = export.createDiagram();
    VisioExportUtils.writeToFile(exportDoc);

    exportDoc.write(out);
    out.close();

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(new ByteArrayInputStream(out.toByteArray()));
  }

  @SuppressWarnings("boxing")
  @Test
  public void testAllReleasesInDocument() throws Exception {
    List<InformationSystemRelease> ipuReleases = new ArrayList<InformationSystemRelease>();
    for (char chr = 'a'; chr < 'f'; chr++) {
      InformationSystem ipu = new InformationSystem();
      ipu.setName(String.valueOf(chr).toUpperCase());
      InformationSystemRelease rel = new InformationSystemRelease();
      rel.setInformationSystem(ipu);
      rel.setVersion(String.valueOf(chr));
      rel.setAttributeValueAssignments(new HashSet<AttributeValueAssignment>());
      rel.setId(Integer.valueOf(chr));
      ipuReleases.add(rel);
    }

    Document document = createVisioBubbleExportDocument(ipuReleases);

    assertNotNull("Generated Visio XML is null!", document);

    NodeList pages = document.getElementsByTagName("Pages");
    Element page = (Element) (pages.item(0));
    String shapesXML = VisioExportUtils.transformToString(page);

    assertNotNull("Generated Visio XML is null!", document);
    assertNotNull("Generated Visio XML document has no root element!", document.getDocumentElement());

    for (char chr = 'a'; chr < 'f'; chr++) {
      String name = String.valueOf(chr).toUpperCase() + Constants.VERSIONSEP + chr;
      assertNotSame("Element " + name + "does not occur in VisioXML!", shapesXML.indexOf(name), -1);
    }

    commit();
  }
}

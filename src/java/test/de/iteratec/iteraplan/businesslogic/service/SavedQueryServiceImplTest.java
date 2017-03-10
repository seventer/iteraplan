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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.MockTestHelper;
import de.iteratec.iteraplan.TestAsSuperUser;
import de.iteratec.iteraplan.TestHelper;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.xml.LandscapeDiagramXML;
import de.iteratec.iteraplan.model.xml.LandscapeDiagramXML.ContentOption;
import de.iteratec.iteraplan.model.xml.query.QueryFormXML;
import de.iteratec.iteraplan.model.xml.query.TypeXML;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.SavedQueryDAO;
import de.iteratec.iteraplan.setup.SavedDiagramConstants;


public class SavedQueryServiceImplTest {

  private SavedQueryServiceImpl classUnderTest;
  private SavedQueryDAO         savedQueryDAOMock;
  private AttributeTypeDAO      attributeTypeDAOMock;
  private TextAT                color;
  private TextAT                lineType;

  @Before
  public void setUp() {
    // run as super user in order to avoid initializing all permissions for the requested data
    TestAsSuperUser.createSuperUserInContext();

    classUnderTest = new SavedQueryServiceImpl();
    savedQueryDAOMock = MockTestHelper.createMock(SavedQueryDAO.class);
    attributeTypeDAOMock = MockTestHelper.createMock(AttributeTypeDAO.class);
    classUnderTest.setSavedQueryDAO(savedQueryDAOMock);
    classUnderTest.setAttributeTypeDAO(attributeTypeDAOMock);

    TextAT row = new TextAT();
    row.setId(Integer.valueOf(3));
    row.setName("ROW ATTRIBUTE");

    color = new TextAT();
    color.setId(Integer.valueOf(1));
    color.setName(SavedDiagramConstants.LANDSCAPE_QUERY_ONE_COLOR);
    lineType = new TextAT();
    lineType.setId(Integer.valueOf(2));
    lineType.setName(SavedDiagramConstants.LANDSCAPE_QUERY_ONE_LINE_TYPE);
  }

  /**
   * Tests the loading process of a saved landscape diagram based on test data from
   * "src/java/test/landscapeQueryTest.xml"
   */
  @Test
  public void testGetSavedLandscapeDiagram() {
    SavedQuery savedQuery = new SavedQuery();
    String content = null;

    try {
      File file = new File(TestHelper.getInstance().getTestPath() + "/landscapeQueryTest.xml");
      content = FileUtils.readFileToString(file, "UTF-8");
    } catch (FileNotFoundException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.FILE_NOT_FOUND_EXCEPTION, e);
    } catch (IOException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }

    savedQuery.setSchemaFile(SavedQuery.getSchemaMapping().get(ReportType.LANDSCAPE));
    savedQuery.setContent(content);
    savedQuery.setType(ReportType.LANDSCAPE);
    MockTestHelper.expect(attributeTypeDAOMock.loadObjectByIdIfExists(color.getId())).andReturn(color).anyTimes();
    MockTestHelper.expect(attributeTypeDAOMock.loadObjectByIdIfExists(lineType.getId())).andReturn(lineType).anyTimes();
    MockTestHelper.expect(attributeTypeDAOMock.loadObjectByIdIfExists(Integer.valueOf(-1))).andReturn(null).anyTimes();
    MockTestHelper.expect(attributeTypeDAOMock.loadObjectByIdIfExists(Integer.valueOf(4))).andReturn(null).anyTimes();

    MockTestHelper.replay(savedQueryDAOMock, attributeTypeDAOMock);

    LandscapeDiagramXML landscapeDiagram = classUnderTest.getSavedLandscapeDiagram(savedQuery);

    assertEquals(InformationSystemReleaseTypeQu.getInstance(), landscapeDiagram.getContentType().getQueryType());
    assertEquals(4, landscapeDiagram.getDialogStep());
    assertEquals(Integer.valueOf(1), landscapeDiagram.getColor().getAttributeId());
    assertEquals(3, landscapeDiagram.getColor().getAttributeValues().size());
    assertTrue("Erwarteter Wert 'Gut' nicht gefunden", landscapeDiagram.getColor().getAttributeValues().contains("Gut"));
    assertTrue("Erwarteter Wert 'Mittel' nicht gefunden", landscapeDiagram.getColor().getAttributeValues().contains("Mittel"));
    assertTrue("Erwarteter Wert 'Schlecht' nicht gefunden", landscapeDiagram.getColor().getAttributeValues().contains("Schlecht"));
    assertEquals(3, landscapeDiagram.getColor().getSelectedStyles().size());
    assertTrue("Erwarteter Wert 'AFCEA8' nicht gefunden", landscapeDiagram.getColor().getSelectedStyles().contains("AFCEA8"));
    assertTrue("Erwarteter Wert 'F6DF95' nicht gefunden", landscapeDiagram.getColor().getSelectedStyles().contains("F6DF95"));
    assertTrue("Erwarteter Wert 'D79DAD' nicht gefunden", landscapeDiagram.getColor().getSelectedStyles().contains("D79DAD"));
    assertFalse(landscapeDiagram.isColumnAxisScalesWithContent());

    assertEquals(ContentOption.BUILDING_BLOCK, landscapeDiagram.getSelectedColumnOption());
    assertEquals(ContentOption.BUILDING_BLOCK, landscapeDiagram.getSelectedRowOption());
    MockTestHelper.verify(savedQueryDAOMock, attributeTypeDAOMock);
    assertEquals(1, landscapeDiagram.getColumnQuery().getQueryForms().size());
    QueryFormXML form = landscapeDiagram.getColumnQuery().getQueryForms().get(0);
    assertEquals(TypeXML.BUSINESSPROCESS, form.getTypeXML());

    assertEquals(1, landscapeDiagram.getContentQuery().getQueryForms().size());
    form = landscapeDiagram.getContentQuery().getQueryForms().get(0);
    assertEquals(TypeXML.INFORMATIONSYSTEMRELEASE, form.getTypeXML());

    assertEquals(1, landscapeDiagram.getRowQuery().getQueryForms().size());
    form = landscapeDiagram.getRowQuery().getQueryForms().get(0);
    assertEquals(TypeXML.BUSINESSUNIT, form.getTypeXML());

    // If an attribute id in the query doesn't exist anymore in DB, then it has to be substituted by
    // -1 otherwise query can not be loaded; here attribute with id=4 is no longer available,
    // so test checks if it is substituted by blank attribute in order to load query.
    assertEquals("blank_null_-1", landscapeDiagram.getRowQuery().getQueryForms().get(0).getQueryUserInput().getQueryFirstLevels().get(0)
        .getQuerySecondLevels().get(0).getChosenAttributeStringId());

  }
}

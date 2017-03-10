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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.visio.informationflow.VisioInformationFlowExport;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * Test class for Visio Exports.
 */
public class VisioInformationFlowExportTest extends BaseTransactionalTestSupport {

  private static final Logger             LOGGER              = Logger.getIteraplanLogger(VisioInformationFlowExportTest.class);

  private static String                   testTextAV          = "EINDEUTIGERText12";
  private static List<String>             availableColors     = Lists.newArrayList("255255", "666666");
  private static List<String>             availableAttrVals   = Lists.newArrayList("1", "2");

  @Autowired
  private BuildingBlockTypeDAO            buildingBlockTypeDAO;
  @Autowired
  private InformationSystemReleaseService informationSystemReleaseService;
  @Autowired
  private AttributeTypeService            attributeTypeService;
  @Autowired
  private AttributeValueService           attributeValueService;
  @Autowired
  private TestDataHelper2                 testDataHelper;

  private static final String             OCCURRENCE          = "Element %s does occur in VisioXML!";
  private static final String             NO_OCCURRENCE       = "Element %s does not occur in VisioXML!";
  private static final String             STANDARD_START_DATE = "1.1.2005";
  private static final String             STANDARD_END_DATE   = "31.12.2006";
  private static final String             I1_NAME             = "informationSystemI1";
  private static final String             I3_NAME             = "informationSystemI3";
  private static final String             J1_NAME             = "informationSystemJ1";
  private static final String             J3_NAME             = "informationSystemJ3";
  private static final String             BO1_NAME            = "businessObject1";
  private static final String             BO2_NAME            = "businessObject2";
  private static final String             BO3_NAME            = "businessObject3";
  private static final String             BO4_NAME            = "businessObject4";

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @SuppressWarnings("boxing")
  @Test
  public void testExportSimpleCorrect() throws Exception {
    createIpureleaseConfigurationWithTransports();

    beginTransaction();
    List<InformationSystemRelease> ipuReleases = new ArrayList<InformationSystemRelease>();
    ipuReleases.addAll(informationSystemReleaseService.findByNames(Sets.newHashSet("I # informationSystemI1", "J # informationSystemJ1")));

    InformationFlowOptionsBean informationFlowOptions = new InformationFlowOptionsBean();
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("atg1", "desc");
    EnumAT enumAt = testDataHelper.createEnumAttributeType("EnumAT", "desc", Boolean.FALSE, atg);

    ColorDimensionOptionsBean colorOptions = initializeColorOptionsBean(informationFlowOptions, enumAt);
    colorOptions.setDimensionAttributeId(Integer.valueOf(GraphicalExportBaseOptions.STATUS_SELECTED));
    VisioInformationFlowExport export = new VisioInformationFlowExport(ipuReleases, null, null, Locale.GERMAN, informationFlowOptions,
        attributeTypeService, attributeValueService, informationSystemReleaseService);
    de.iteratec.visio.model.Document exportDoc = export.createDiagram();
    VisioExportUtils.writeToFile(exportDoc);

    Document document = VisioExportUtils.visioDocumentToDOM(exportDoc);

    NodeList pages = document.getElementsByTagName("Pages");
    Element page = (Element) (pages.item(0));
    String shapesXML = VisioExportUtils.transformToString(page);

    assertNotNull("Generated Visio XML is null!", document);
    assertNotNull("Generated Visio XML document has no root element!", document.getDocumentElement());
    assertNotSame(String.format(NO_OCCURRENCE, I1_NAME), shapesXML.indexOf(I1_NAME), -1);
    assertNotSame(String.format(NO_OCCURRENCE, I3_NAME), shapesXML.indexOf(I3_NAME), -1);
    assertNotSame(String.format(NO_OCCURRENCE, J1_NAME), shapesXML.indexOf(J1_NAME), -1);
    assertNotSame(String.format(NO_OCCURRENCE, J3_NAME), shapesXML.indexOf(J3_NAME), -1);
    assertNotSame(String.format(NO_OCCURRENCE, BO1_NAME), shapesXML.indexOf(BO1_NAME), -1);
    assertNotSame(String.format(NO_OCCURRENCE, BO2_NAME), shapesXML.indexOf(BO2_NAME), -1);
    assertNotSame(String.format(NO_OCCURRENCE, BO3_NAME), shapesXML.indexOf(BO3_NAME), -1);
    assertNotSame(String.format(NO_OCCURRENCE, BO4_NAME), shapesXML.indexOf(BO4_NAME), -1);

    assertNotSame("Color " + availableColors.get(0) + " does not occur in VisioXML!", shapesXML.indexOf("#" + availableColors.get(0)), -1);
    assertNotSame("Color " + availableColors.get(1) + " does not occur in VisioXML!", shapesXML.indexOf("#" + availableColors.get(1)), -1);

    commit();
  }

  private InformationSystemInterface createIsReleases() {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, I3_NAME, "i # i3", STANDARD_START_DATE, STANDARD_END_DATE,
        TypeOfStatus.CURRENT);
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, I1_NAME, "i # i1", null, null, TypeOfStatus.CURRENT);
    testDataHelper.addChildToIsr(i3, i1);

    InformationSystem j = testDataHelper.createInformationSystem("J");
    InformationSystemRelease j3 = testDataHelper.createInformationSystemRelease(j, J3_NAME, "j # j3", STANDARD_START_DATE, STANDARD_END_DATE,
        TypeOfStatus.CURRENT);
    InformationSystemRelease j1 = testDataHelper.createInformationSystemRelease(j, J1_NAME, "j # j1", null, null, TypeOfStatus.CURRENT);
    testDataHelper.addChildToIsr(j3, j1);

    return testDataHelper.createInformationSystemInterface(i1, j1, null, "Connection i1 j1");
  }

  private void createIpureleaseConfigurationWithTransports() {
    InformationSystemInterface c = createIsReleases();

    BusinessObject bo1 = testDataHelper.createBusinessObject(BO1_NAME, "Business Object one way 1");
    BusinessObject bo2 = testDataHelper.createBusinessObject(BO2_NAME, "Business Object one way 2");
    BusinessObject bo3 = testDataHelper.createBusinessObject(BO3_NAME, "Business Object bidirectional");
    BusinessObject bo4 = testDataHelper.createBusinessObject(BO4_NAME, "Business Object no direction");

    testDataHelper.createTransport(bo1, c, Direction.FIRST_TO_SECOND);
    testDataHelper.createTransport(bo2, c, Direction.SECOND_TO_FIRST);
    testDataHelper.createTransport(bo3, c, Direction.BOTH_DIRECTIONS);
    testDataHelper.createTransport(bo4, c, Direction.NO_DIRECTION);

    commit();
  }

  private ColorDimensionOptionsBean initializeColorOptionsBean(InformationFlowOptionsBean informationFlowOptions, AttributeType at) {
    ColorDimensionOptionsBean colorOptions = informationFlowOptions.getColorOptionsBean();
    colorOptions.setAvailableColors(availableColors);

    // an existing attribute is needed so that the attributeValueService in the bean can retrieve something
    colorOptions.setDimensionAttributeId(at.getId());
    colorOptions.setColorRangeAvailable(false);
    colorOptions.refreshDimensionOptions(availableAttrVals);

    return colorOptions;
  }

  /**
   * Test for an empty IS Visio export using an empty list of Ipureleases.
   */
  @SuppressWarnings({ "boxing" })
  @Test
  public void testExportWithNoResultsCorrect() throws Exception {
    createIpureleaseConfigurationWithTransports();

    // act and assert:
    beginTransaction();
    List<InformationSystemRelease> ipuReleases = new ArrayList<InformationSystemRelease>();
    InformationFlowOptionsBean informationFlowOptions = new InformationFlowOptionsBean();
    informationFlowOptions.getColorOptionsBean().setDimensionAttributeId(Integer.valueOf(GraphicalExportBaseOptions.NOTHING_SELECTED));
    VisioInformationFlowExport export = new VisioInformationFlowExport(ipuReleases, null, null, null, informationFlowOptions, attributeTypeService,
        attributeValueService, informationSystemReleaseService);
    de.iteratec.visio.model.Document exportDoc = export.createDiagram();
    VisioExportUtils.writeToFile(exportDoc);

    Document document = VisioExportUtils.visioDocumentToDOM(exportDoc);

    NodeList pages = document.getElementsByTagName("Pages");
    Element page = (Element) (pages.item(0));
    String shapesXML = VisioExportUtils.transformToString(page);

    assertNotNull("Generated Visio XML is null!", document);
    assertNotNull("Generated Visio XML document has no root element!", document.getDocumentElement());
    assertSame(String.format(OCCURRENCE, I1_NAME), shapesXML.indexOf(I1_NAME), -1);
    assertSame(String.format(OCCURRENCE, I3_NAME), shapesXML.indexOf(I3_NAME), -1);
    assertSame(String.format(OCCURRENCE, J1_NAME), shapesXML.indexOf(J1_NAME), -1);
    assertSame(String.format(OCCURRENCE, J3_NAME), shapesXML.indexOf(J3_NAME), -1);
    assertSame(String.format(OCCURRENCE, BO1_NAME), shapesXML.indexOf(BO1_NAME), -1);
    assertSame(String.format(OCCURRENCE, BO2_NAME), shapesXML.indexOf(BO2_NAME), -1);
    assertSame(String.format(OCCURRENCE, BO3_NAME), shapesXML.indexOf(BO3_NAME), -1);
    assertSame(String.format(OCCURRENCE, BO4_NAME), shapesXML.indexOf(BO4_NAME), -1);

    // No legend if no color attribute is selected
    assertSame("Color " + availableColors.get(0) + " should not occur in VisioXML!", shapesXML.indexOf("#" + availableColors.get(0)), -1);
    commit();

  }

  @SuppressWarnings({ "boxing" })
  @Test
  public void testExportWithoutTransportsCorrect() throws Exception {
    createIsReleases();

    AttributeTypeGroup group = testDataHelper.createAttributeTypeGroup("test", "test");
    TextAT at = testDataHelper.createTextAttributeType("text", "", false, group);
    TextAV av1 = testDataHelper.createTextAV(testTextAV, at);

    BuildingBlockType bbType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    at.addBuildingBlockTypeTwoWay(bbType);
    commit();

    // act and assert:
    beginTransaction();

    List<InformationSystemRelease> isReleases = new ArrayList<InformationSystemRelease>();
    isReleases.addAll(informationSystemReleaseService.findByNames(Sets.newHashSet("I # informationSystemI1", "J # informationSystemJ1")));
    LOGGER.debug("Ipureleases in result list: " + isReleases);

    InformationFlowOptionsBean informationFlowOptions = new InformationFlowOptionsBean();
    initializeColorOptionsBean(informationFlowOptions, at);

    VisioInformationFlowExport export = new VisioInformationFlowExport(isReleases, null, null, UserContext.getCurrentLocale(),
        informationFlowOptions,
        attributeTypeService, attributeValueService, informationSystemReleaseService);

    de.iteratec.visio.model.Document exportDoc = export.createDiagram();
    VisioExportUtils.writeToFile(exportDoc);

    Document document = VisioExportUtils.visioDocumentToDOM(exportDoc);

    NodeList pages = document.getElementsByTagName("Page");
    Element page = (Element) pages.item(0);
    String shapesXML = VisioExportUtils.transformToString(page);

    assertNotNull("Generated Visio XML is null!", document);
    assertNotNull("Generated Visio XML document has no root element!", document.getDocumentElement());
    assertTrue(String.format(NO_OCCURRENCE, I1_NAME), shapesXML.indexOf(I1_NAME) > -1);
    assertTrue(String.format(NO_OCCURRENCE, I3_NAME), shapesXML.indexOf(I3_NAME) > -1);
    assertTrue(String.format(NO_OCCURRENCE, J1_NAME), shapesXML.indexOf(J1_NAME) > -1);
    assertTrue(String.format(NO_OCCURRENCE, J3_NAME), shapesXML.indexOf(J3_NAME) > -1);

    boolean foundArrowShapeNoDirection = containsArrowShapeNoDirection(createMasterMap(document), page);
    assertTrue("Shape of type InformationFlowNoDirection not found!", foundArrowShapeNoDirection);

    assertNotSame("TextAV: " + av1.getIdentityString() + " does not occur in VisioXML!", shapesXML.indexOf(testTextAV), -1);

    commit();
  }

  private boolean containsArrowShapeNoDirection(Map<String, String> masterMap, Element page) {
    boolean foundArrowShapeNoDirection = false;
    NodeList shapes = page.getElementsByTagName("Shape");
    for (int idx = 0; idx < shapes.getLength(); idx++) {
      Element shape = (Element) shapes.item(idx);
      String masterId = shape.getAttribute("Master");
      String masterName = masterMap.get(masterId);
      if ("InformationFlowNoDirection".equals(masterName)) {
        foundArrowShapeNoDirection = true;
        break;
      }
    }
    return foundArrowShapeNoDirection;
  }

  private Map<String, String> createMasterMap(Document document) {
    Map<String, String> masterMap = new HashMap<String, String>();
    NodeList masters = document.getElementsByTagName("Master");
    for (int idx = 0; idx < masters.getLength(); idx++) {
      Element master = (Element) masters.item(idx);
      String masterName = master.getAttribute("NameU");
      String idString = master.getAttribute("ID");
      masterMap.put(idString, masterName);
    }
    return masterMap;
  }
}

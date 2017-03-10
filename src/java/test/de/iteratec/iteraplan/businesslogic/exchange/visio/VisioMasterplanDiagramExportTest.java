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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.svg.DataCreator;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry.COLUMN_TYPE;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.TimelineFeature;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.DateIntervalService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateInterval;


/**
 * Masterplan diagram export test.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@IfProfileValue(name = "junit.build", value = "nightly.only")
public class VisioMasterplanDiagramExportTest extends BaseTransactionalTestSupport {

  /**
   * The variable is responsible for the xPaths and the xPathExpressions. For more information see
   * the java documentation for the package javax.xml.xpath The package replaced the XMLUnit Objects
   */
  private final XPathFactory          factoryXPath        = XPathFactory.newInstance();

  @Autowired
  private AttributeTypeService        attributeTypeService;
  @Autowired
  private AttributeValueService       attributeValueService;
  @Autowired
  private BuildingBlockServiceLocator buildingBlockServiceLocator;
  @Autowired
  private TestDataHelper2             testDataHelper;

  private static final String[]       RELS                = { "Rel1", "Rel2", "Rel3", "Rel4", "Rel5", "Rel6" };
  private static final String[]       MONTHS              = { "Jan", "Feb", "Mrz", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez" };
  private static final String         STANDARD_START_DATE = "01.05.2005";

  private static final String         TIMESPAN_BEGIN      = "15.12.2010";
  private static final String         TIMESPAN_END        = "01.06.2014";
  private static final List<String>   availableColors     = Lists.newArrayList("AFCEA8", "F6DF95", "D79DAD", "88AED9", "ACA1C8", "D3DD93", "E8BB9E");

  private static final List<String>   ALL_ATTRIBUTE_NAMES = Lists
                                                              .newArrayList(DataCreator.AT_COSTS, DataCreator.AT_DECISION_DATE,
                                                                  DataCreator.AT_EVALUATION_DATE, DataCreator.AT_HEALTH,
                                                                  DataCreator.AT_INTRODUCTION_DATE);

  @Autowired
  private DateIntervalService         dateIntervalService;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    (new DataCreator(testDataHelper)).createData();
  }

  @Test
  public void testNonHierarchicalExport() throws Exception {
    List<BuildingBlock> entities = createTestInformationSystemReleases(false);

    String shapesXML = createDiagramAsXML(entities, getNonHierarchicalExportOptions());

    // no bar is displayed, so only one occurence (in table) (+ time-span label)
    assertEquals(RELS[0], 1, countOccurrences(shapesXML, RELS[0]));
    // bar is displayed, so two occurences: one in the table and one in the bar
    assertEquals(RELS[1], 1, countOccurrences(shapesXML, RELS[1]));
    assertEquals(RELS[2], 1, countOccurrences(shapesXML, RELS[2]));
    assertEquals(RELS[3], 1, countOccurrences(shapesXML, RELS[3]));
    assertEquals(RELS[4], 1, countOccurrences(shapesXML, RELS[4]));// (+ label)
    assertEquals(RELS[5], 1, countOccurrences(shapesXML, RELS[5]));// (+ bar)
    // hierarchical names should not be present
    assertEquals(0, countOccurrences(shapesXML, "IpuA # Rel1 : IpuA # Rel2"));

    // check title
    assertEquals("Title not found", 1, countOccurrences(shapesXML, "iteraplan Masterplan-Grafik"));

    // check months
    int[] occurences = { 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1 };
    assertOccurrenceCounts(shapesXML, occurences, MONTHS);
  }

  @Test
  public void testNonHierarchicalExportXPath() throws Exception {
    List<BuildingBlock> entities = createTestInformationSystemReleases(false);

    Document document = createDiagram(entities, getNonHierarchicalExportOptions());
    checkXPath(document, "//Pages/Page/Shapes/Shape/Line/LinePattern[text() = '16']", 1);
  }

  @Test
  public void testOneYearTimeSpan() throws Exception {
    List<BuildingBlock> entities = createTestInformationSystemReleases(false);

    String shapesXML = createDiagramAsXML(entities, getOneYearExportOptions());

    // check months
    int[] occurences = { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 };
    assertOccurrenceCounts(shapesXML, occurences, MONTHS);
  }

  @Test
  public void testOneYearTimeSpanXPath() throws Exception {
    List<BuildingBlock> entities = createTestInformationSystemReleases(false);

    Document document = createDiagram(entities, getOneYearExportOptions());
    checkXPath(document, "//Pages/Page/Shapes/Shape/Prop[@NameU='LineStyle']/Value[text() = 'dottedCoarse']", 0);
  }

  @Test
  public void testTwoYearTimeSpan() throws Exception {
    List<BuildingBlock> entities = createTestInformationSystemReleases(false);

    String shapesXML = createDiagramAsXML(entities, getTwoYearExportOptions());

    // check months
    int[] occurences = { 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1 };
    assertOccurrenceCounts(shapesXML, occurences, MONTHS);
  }

  @Test
  public void testTwoYearTimeSpanXPath() throws Exception {
    List<BuildingBlock> entities = createTestInformationSystemReleases(false);

    Document document = createDiagram(entities, getTwoYearExportOptions());
    checkXPath(document, "//Pages/Page/Shapes/Shape/Line/LinePattern[text() = '16']", 1);
  }

  @Test
  public void testThreeYearTimeSpan() throws Exception {
    List<BuildingBlock> entities = createTestInformationSystemReleases(false);

    String shapesXML = createDiagramAsXML(entities, getThreeYearExportOptions());

    // check months
    int[] occurences = { 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2 };
    assertOccurrenceCounts(shapesXML, occurences, MONTHS);
  }

  @Test
  public void testThreeYearTimeSpanXPath() throws Exception {
    List<BuildingBlock> entities = createTestInformationSystemReleases(false);

    Document document = createDiagram(entities, getThreeYearExportOptions());
    checkXPath(document, "//Pages/Page/Shapes/Shape/Line/LinePattern[text() = '16']", 2);
  }

  @Test
  public void testHierarchicalExport() throws Exception {
    List<BuildingBlock> entities = createTestInformationSystemReleases(true);
    String shapesXML = createDiagramAsXML(entities, getTwoYearExportOptions());

    // this is the result expected for the new MasterplanDiagramCreator
    assertEquals(RELS[0], 5, countOccurrences(shapesXML, RELS[0]));
    assertEquals(RELS[1], 4, countOccurrences(shapesXML, RELS[1]));
    assertEquals(RELS[2], 3, countOccurrences(shapesXML, RELS[2]));
    assertEquals(RELS[3], 1, countOccurrences(shapesXML, RELS[3]));
    assertEquals(RELS[4], 1, countOccurrences(shapesXML, RELS[4]));
    assertEquals(RELS[5], 1, countOccurrences(shapesXML, RELS[5]));

    // check title
    assertEquals("Title not found", 1, countOccurrences(shapesXML, "iteraplan Masterplan-Grafik"));
  }

  @Test
  public void testMasterplanOptionsValidation() throws Exception {
    List<BuildingBlock> entities = createTestInformationSystemReleases(false);
    MasterplanOptionsBean options = new MasterplanOptionsBean();

    configureLevelOptions(options, 0, "", Constants.BB_INFORMATIONSYSTEMRELEASE, false, DataCreator.AT_HEALTH, false, false, new ArrayList<String>(),
        Lists.newArrayList(DataCreator.AT_COSTS));

    VisioMasterplanExport exp;

    // set wrong dates
    options.setEndDateString("01.05.2004");
    try {
      exp = createExport(entities, options);
      fail("Dates are wrong, but no exception has been thrown for: " + exp.toString());
    } catch (IteraplanBusinessException e) {
      assertEquals(IteraplanErrorMessages.INVALID_TIMESPAN, e.getErrorCode());
    }

    // set time span too large
    options.setStartDateString(STANDARD_START_DATE);
    options.setEndDateString("01.06.2030");
    try {
      exp = createExport(entities, options);
      fail("Time span too large, but no exception has been thrown for: " + exp.toString());
    } catch (IteraplanException e) {
      assertEquals(IteraplanErrorMessages.TIMESPAN_TOO_LARGE, e.getErrorCode());
    }

    // leave time span open
    options.setStartDateString("");
    options.setEndDateString("01.06.2030");
    try {
      exp = createExport(entities, options);
      fail("Time span open, but no exception has been thrown for: " + exp.toString());
    } catch (IteraplanException e) {
      assertEquals(IteraplanErrorMessages.TIMESPAN_NOT_CLOSED, e.getErrorCode());
    }
  }

  private void assertOccurrenceCounts(String shapesXML, int[] expectedOccurrences, String[] stringsToLookFor) {
    for (int i = 0; i < expectedOccurrences.length; i++) {
      assertEquals(expectedOccurrences[i], countOccurrences(shapesXML, stringsToLookFor[i]));
    }
  }

  private List<DateInterval> getAvailableDateIntervals(String forBbType) {
    TypeOfBuildingBlock bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(forBbType);
    List<AttributeType> aTypes = attributeTypeService.getAttributeTypesForTypeOfBuildingBlock(bbType, false);
    Set<Integer> dateATids = new HashSet<Integer>();
    for (AttributeType dat : aTypes) {
      if (dat instanceof DateAT) {
        dateATids.add(dat.getId());
      }
    }
    return Lists.newArrayList(dateIntervalService.findDateIntervalsByDateATs(dateATids));
  }

  private void initColorOptions(ColorDimensionOptionsBean colorOptions, String attributeName) {
    colorOptions.setAvailableColors(availableColors);
    Integer atId = attributeTypeService.getAttributeTypeByName(attributeName).getId();
    colorOptions.setDimensionAttributeId(atId);
    colorOptions.refreshDimensionOptions(attributeValueService.getAllAVStrings(atId));
    colorOptions.switchToGenerationMode();
  }

  private TimelineFeature getTimelineFeatureByName(MasterplanRowTypeOptions options, String timelineFeatureName) {
    for (TimelineFeature feature : options.getAvailableTimeLines()) {
      if (feature.getName().equals(timelineFeatureName)) {
        return feature;
      }
    }
    return null;
  }

  private ColumnEntry getColumnEntry(String attributeName) {
    return new ColumnEntry(attributeTypeService.getAttributeTypeByName(attributeName).getId().toString(), COLUMN_TYPE.ATTRIBUTE, attributeName);
  }

  private void configureLevelOptions(MasterplanOptionsBean masterplanOptions, int level, String relationToBb, String selectedBbType,
                                     boolean hierarchicalNames, String colorAtName, boolean closure, boolean disableRuntimePeriod,
                                     List<String> timelineFeatureNames, List<String> customColumnNames) {
    MasterplanRowTypeOptions levelOpts = new MasterplanRowTypeOptions(relationToBb, selectedBbType, level, getAvailableDateIntervals(selectedBbType),
        new ArrayList<BBAttribute>(), getAvailableCustomColumns(ALL_ATTRIBUTE_NAMES));
    levelOpts.setHierarchicalSort(hierarchicalNames);
    levelOpts.setCanBuildClosure(true);
    levelOpts.setBuildClosure(closure);

    if (disableRuntimePeriod) {
      levelOpts.removeTimeLineByPosition(0);
    }

    for (String timelineFeatureName : timelineFeatureNames) {
      levelOpts.addTimeline(getTimelineFeatureByName(levelOpts, timelineFeatureName));
    }

    if (colorAtName != null && !colorAtName.isEmpty()) {
      initColorOptions(levelOpts.getColorOptions(), colorAtName);
    }

    List<ColumnEntry> customCols = levelOpts.getAvailableCustomColumns();
    for (String colName : customColumnNames) {
      customCols.add(getColumnEntry(colName));
    }
    levelOpts.setSelectedCustomColumns(customCols);

    if (level == 0) {
      masterplanOptions.setLevel0Options(levelOpts);
    }
    else if (level == 1) {
      masterplanOptions.setLevel1Options(levelOpts);
    }
    else {
      masterplanOptions.setLevel2Options(levelOpts);
    }
  }

  private List<ColumnEntry> getAvailableCustomColumns(List<String> attributeNames) {
    List<ColumnEntry> result = Lists.newArrayList();
    for (String atName : attributeNames) {
      result.add(getColumnEntry(atName));
    }
    return result;
  }

  private static String getDateString(String sourceString) {
    Date date = DataCreator.parseDate(sourceString);
    return DateUtils.formatAsString(date, UserContext.getCurrentLocale());
  }

  private MasterplanOptionsBean initOptions() {
    MasterplanOptionsBean options = new MasterplanOptionsBean();
    options.setSelectedGraphicFormat(Constants.REPORTS_EXPORT_GRAPHICAL_VISIO);
    options.setStartDateString(getDateString(TIMESPAN_BEGIN));
    options.setEndDateString(getDateString(TIMESPAN_END));
    return options;
  }

  private VisioMasterplanExport createExport(List<BuildingBlock> entities, MasterplanOptionsBean options) {
    MasterplanDiagramCreator masterplanDiagramCreator = new MasterplanDiagramCreator(options, buildingBlockServiceLocator, attributeTypeService,
        entities);

    MasterplanDiagram masterplanDiagram2 = masterplanDiagramCreator.createMasterplanDiagram();

    return new VisioMasterplanExport(masterplanDiagram2, options, attributeTypeService, attributeValueService);
  }

  private Document createDiagram(List<BuildingBlock> entities, MasterplanOptionsBean options) throws IOException, ParserConfigurationException,
      SAXException {
    de.iteratec.visio.model.Document exportDoc = createExport(entities, options).createDiagram();

    VisioExportUtils.writeToFile(exportDoc);

    Document document = VisioExportUtils.visioDocumentToDOM(exportDoc);

    assertNotNull("Generated Visio XML is null!", document);
    assertNotNull("Generated Visio XML document has no root element!", document.getDocumentElement());
    return document;
  }

  private String createDiagramAsXML(List<BuildingBlock> entities, MasterplanOptionsBean options) throws IOException, ParserConfigurationException,
      SAXException {
    Document document = createDiagram(entities, options);

    NodeList pages = document.getElementsByTagName("Pages");
    Element page = (Element) (pages.item(0));
    return VisioExportUtils.transformToString(page);
  }

  private MasterplanOptionsBean getNonHierarchicalExportOptions() {
    MasterplanOptionsBean masterplanOptions = initOptions();
    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_INFORMATIONSYSTEMRELEASE, false, DataCreator.AT_HEALTH, false, false,
        new ArrayList<String>(), Lists.newArrayList(DataCreator.AT_COSTS));

    masterplanOptions.setStartDateString(STANDARD_START_DATE);
    masterplanOptions.setEndDateString("01.05.2006");

    masterplanOptions.getLevel0Options().setHierarchicalSort(false);

    return masterplanOptions;
  }

  private MasterplanOptionsBean getOneYearExportOptions() {
    MasterplanOptionsBean options = new MasterplanOptionsBean();

    configureLevelOptions(options, 0, "", Constants.BB_INFORMATIONSYSTEMRELEASE, false, DataCreator.AT_HEALTH, false, false, new ArrayList<String>(),
        Lists.newArrayList(DataCreator.AT_COSTS));

    options.setStartDateString(STANDARD_START_DATE);
    options.setEndDateString("01.05.2005");

    return options;
  }

  private MasterplanOptionsBean getTwoYearExportOptions() {
    MasterplanOptionsBean masterplanOptions = initOptions();
    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_INFORMATIONSYSTEMRELEASE, true, DataCreator.AT_HEALTH, false, false,
        new ArrayList<String>(), Lists.newArrayList(DataCreator.AT_COSTS));

    masterplanOptions.getLevel0Options().setHierarchicalSort(true);

    masterplanOptions.setStartDateString(STANDARD_START_DATE);
    masterplanOptions.setEndDateString("01.05.2006");

    return masterplanOptions;
  }

  private MasterplanOptionsBean getThreeYearExportOptions() {
    MasterplanOptionsBean options = new MasterplanOptionsBean();

    configureLevelOptions(options, 0, "", Constants.BB_INFORMATIONSYSTEMRELEASE, false, DataCreator.AT_HEALTH, false, false, new ArrayList<String>(),
        Lists.newArrayList(DataCreator.AT_COSTS));

    options.setStartDateString(STANDARD_START_DATE);
    options.setEndDateString("01.05.2007");
    return options;
  }

  private void checkXPath(Document document, String expression, int expectedElements) throws XPathExpressionException {
    // create a new variable for the xPath
    XPath xPathA = factoryXPath.newXPath();

    // Iterator for the xPath with a xPathExpression. The results are saved in a NodeList.
    NodeList coarseElements = (NodeList) xPathA.evaluate(expression, document, XPathConstants.NODESET);

    // The number of the elements saved of the NodeList should be 1.
    assertEquals(expectedElements, coarseElements.getLength());
  }

  private List<BuildingBlock> createTestInformationSystemReleases(boolean hierarchical) throws IteraplanBusinessException {
    List<BuildingBlock> entityList = new ArrayList<BuildingBlock>();

    InformationSystem ipuA = testDataHelper.createInformationSystem("IpuA");

    InformationSystemRelease ipurA1 = createInformationSystemRelease(ipuA, RELS[0], null, "01.03.2005", InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease ipurA2 = createInformationSystemRelease(ipuA, RELS[1], "29.02.2004", "15.12.2005",
        InformationSystemRelease.TypeOfStatus.INACTIVE);
    InformationSystemRelease ipurA3 = createInformationSystemRelease(ipuA, RELS[2], "11.07.2005", "20.04.2006",
        InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease ipurA4 = createInformationSystemRelease(ipuA, RELS[3], "03.02.2006", "20.04.2100",
        InformationSystemRelease.TypeOfStatus.PLANNED);

    if (hierarchical) {
      ipurA2.addParent(ipurA1);
      ipurA3.addParent(ipurA2);
      ipurA4.addParent(ipurA3);
    }

    InformationSystem ipuB = testDataHelper.createInformationSystem("IpuB");

    InformationSystemRelease ipurB1 = createInformationSystemRelease(ipuB, RELS[4], "17.09.2006", "17.09.2007",
        InformationSystemRelease.TypeOfStatus.TARGET);
    InformationSystemRelease ipurB2 = createInformationSystemRelease(ipuB, RELS[5], null, null, InformationSystemRelease.TypeOfStatus.PLANNED);

    entityList.add(ipurA1);
    entityList.add(ipurA2);
    entityList.add(ipurA3);
    entityList.add(ipurA4);
    entityList.add(ipurB1);
    entityList.add(ipurB2);

    return entityList;
  }

  private InformationSystemRelease createInformationSystemRelease(InformationSystem is, String version, String startDate, String endDate,
                                                                  TypeOfStatus status) {

    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, version, "descr", startDate, endDate, status);

    return isr;
  }

  private int countOccurrences(String source, String pattern) {
    int count = 0;
    int index = source.indexOf(pattern, 0);
    while (index > -1) {
      index = source.indexOf(pattern, index + 1);
      count++;
    }
    return count;
  }

}

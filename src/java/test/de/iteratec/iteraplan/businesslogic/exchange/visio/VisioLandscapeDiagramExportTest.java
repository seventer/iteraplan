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
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.LandscapeDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.LandscapeDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.visio.landscape.VisioLandscapeDiagramExport;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ILandscapeOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.LandscapeOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.TechnicalComponentReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.service.ArchitecturalDomainService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.GeneralBuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService;
import de.iteratec.iteraplan.businesslogic.service.ProjectService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.dto.LandscapeDiagramConfigDTO;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;
import de.iteratec.iteraplan.persistence.dao.BusinessProcessDAO;
import de.iteratec.iteraplan.persistence.dao.BusinessUnitDAO;
import de.iteratec.iteraplan.persistence.dao.ProductDAO;


/**
 * Landscape diagram export test.
 */
public class VisioLandscapeDiagramExportTest extends BaseTransactionalTestSupport {

  private static final Logger          LOGGER                     = Logger.getIteraplanLogger(VisioLandscapeDiagramExportTest.class);

  private static final int             BUSINESS_PROCESS_MASTER_ID = 29;
  private static final int             CONTENT_MASTER_ID          = 33;
  private static final int             AXIS_ELEMENT_MASTER_ID     = 39;
  private static final int             LEGEND_TITLE_MASTER_ID     = 40;
  private static final int             LEGEND_ITEM_MASTER_ID      = 41;
  private static final int             COLOR_SAMPLE_MASTER_ID     = 42;
  private static final int             LINE_SAMPLE_MASTER_ID      = 43;

  private EnumAT                       atMv;
  private EnumAT                       atSv;

  private BusinessProcess              bpA;
  private BusinessProcess              bpB;
  private BusinessProcess              bpC;
  private List<BuildingBlock>          businessProcesses;

  private BusinessUnit                 buA;
  private BusinessUnit                 buB;
  private BusinessUnit                 buC;
  private List<BuildingBlock>          businessUnits;

  private InformationSystemRelease     i1;
  private InformationSystemRelease     i2;
  private InformationSystemRelease     i3;
  private InformationSystemRelease     i4;
  private InformationSystemRelease     i5;
  private InformationSystemRelease     i6;
  private InformationSystemRelease     i7;
  private List<BuildingBlock>          isReleases;

  private Product                      prodA;
  private Product                      prodB;
  private Product                      prodC;
  private List<BuildingBlock>          products;

  private TechnicalComponentRelease    tcRel1;
  private TechnicalComponentRelease    tcRel2;
  private TechnicalComponentRelease    tcRel3;
  private TechnicalComponentRelease    tcRel4;
  private TechnicalComponentRelease    tcRel5;
  private TechnicalComponentRelease    tcRel6;
  private List<BuildingBlock>          tcReleases;

  private List<BuildingBlock>          adList;

  private List<BuildingBlock>          ciList;

  private List<BuildingBlock>          projects;

  /**
   * The variable is responsible for the xPaths and the xPathExpressions. For more information see
   * the java documentation for the package javax.xml.xpath The package replaced the XMLUnit Objects
   */
  private final XPathFactory           factoryXPath               = XPathFactory.newInstance();
  @Autowired
  private AttributeTypeGroupDAO        attributeTypeGroupDAO;
  @Autowired
  private BuildingBlockTypeDAO         buildingBlockTypeDAO;
  @Autowired
  private BusinessProcessDAO           businessProcessDAO;
  @Autowired
  private BusinessUnitDAO              businessUnitDAO;
  @Autowired
  private ProductDAO                   productDAO;
  @Autowired
  private ArchitecturalDomainService   architecturalDomainService;
  @Autowired
  private InfrastructureElementService infrastructureElementService;
  @Autowired
  private ProjectService               projectService;
  @Autowired
  private AttributeTypeService         attributeTypeService;
  @Autowired
  private AttributeValueService        attributeValueService;
  @Autowired
  private GeneralBuildingBlockService  generalBuildingBlockService;
  @Autowired
  private AttributeTypeDAO             attributeTypeDAO;
  @Autowired
  private TestDataHelper2              testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    initProcesses();
    initBusinessUnits();
    initISReleases();
    initTcrs();
    initProducts();
    initAttributes();
    commit();
    beginTransaction();
  }

  @Test
  public void testExportOverTcrIsrArchitecturalDomainRelations() {
    LandscapeDiagramConfigDTO config = new LandscapeDiagramConfigDTO();
    config.setTopAxisBbs(isReleases);
    config.setSideAxisBbs(adList);
    config.setContentBbs(tcReleases);

    config.setContentType(TechnicalComponentReleaseTypeQu.getInstance());
    config.setColumnExtension(TechnicalComponentReleaseTypeQu.getInstance().getExtension(
        TechnicalComponentReleaseTypeQu.EXTENSION_INFORMATIONSYSTEMRELEASES));
    config
        .setRowExtension(TechnicalComponentReleaseTypeQu.getInstance().getExtension(TechnicalComponentReleaseTypeQu.EXTENSION_ARCHITECTURALDOMAINS));
    config.setColumnAxisScalesWithContent(false);
    config.setMergeContent(true);

    Document document = createVisioDocument(config, new LandscapeOptionsBean());

    assertNumberOfShapesForMaster(document, AXIS_ELEMENT_MASTER_ID, 10);
    assertNumberOfShapesForMaster(document, CONTENT_MASTER_ID, 9);

    // check that TC 2#2 is two cells wide since it spans two neighbouring Information System
    // releases

    XPath xPath = factoryXPath.newXPath();

    // With the xPathexpression we search an attribute or property value in a xmlFile.
    // Search a Shape-Element, witch has in Node "Field" an Under-Node "Value" with content
    // "TC 2 # 2" and then retrieve the content of the Under-Node XForm and than Under-Node Width.
    // The logic by the next xPathExpressions is the same.

    try {
      XPathExpression xpathA = xPath.compile("//Shape[Text/text() = 'TC 2 # 2']/XForm/Width");
      // The variable contains the needed attribute.
      String widthA = xpathA.evaluate(document);
      // With the xPathexpression we search an attribute or property value in a xmlFile.
      XPathExpression xpathB = xPath.compile("//Shape[Text/text() = 'TC 1 # 1']/XForm/Width");
      // The variable contains the needed attribute.
      String widthB = xpathB.evaluate(document);

      double widthTcr2 = Double.parseDouble(widthA);
      double widthTcr1 = Double.parseDouble(widthB);

      assertTrue(widthTcr2 > 2 * widthTcr1);

      // we don't expect any legend here
      checkLegend(document, 0, 0, 0);
      commit();
    } catch (XPathExpressionException e) {
      LOGGER.error(e);
    }

  }

  private LandscapeOptionsBean getLandscapeOptions() {
    List<String> colors = CollectionUtils.arrayList();
    colors.add(getColorStr(Color.GREEN));
    colors.add(getColorStr(Color.RED));
    colors.add(getColorStr(Color.YELLOW));
    colors.add(getColorStr(Color.BLUE));

    Map<Integer, String> lines = CollectionUtils.hashMap();
    lines.put(Integer.valueOf(1), "-----");
    lines.put(Integer.valueOf(2), "-.-.-");
    lines.put(Integer.valueOf(3), "- - -");
    lines.put(Integer.valueOf(4), ". . .");

    LandscapeOptionsBean landscapeOptionsBean = new LandscapeOptionsBean();
    ColorDimensionOptionsBean colorOptionsBean = landscapeOptionsBean.getColorOptionsBean();
    colorOptionsBean.setAvailableColors(colors);
    colorOptionsBean.setDimensionAttributeId(atMv.getId());
    colorOptionsBean.setColorRangeAvailable(false);
    colorOptionsBean.refreshDimensionOptions(attributeValueService.getAllAVStrings(atMv.getId()));
    LineDimensionOptionsBean lineOptionsBean = landscapeOptionsBean.getLineOptionsBean();
    lineOptionsBean.setAvailableLineStyles(lines);
    lineOptionsBean.setDimensionAttributeId(atSv.getId());
    lineOptionsBean.refreshDimensionOptions(attributeValueService.getAllAVStrings(atSv.getId()));

    return landscapeOptionsBean;
  }

  @Test
  public void testExportOverIsrTcrProjectRelations() throws XPathExpressionException {
    LandscapeDiagramConfigDTO config = new LandscapeDiagramConfigDTO();
    config.setTopAxisBbs(ciList);
    config.setSideAxisBbs(projects);
    config.setContentBbs(isReleases);
    config.setContentType(InformationSystemReleaseTypeQu.getInstance());
    config.setColumnExtension(InformationSystemReleaseTypeQu.getInstance().getExtension(
        InformationSystemReleaseTypeQu.EXTENSION_INFRASTRUCTUREELEMENTS));
    config.setRowExtension(InformationSystemReleaseTypeQu.getInstance().getExtension(InformationSystemReleaseTypeQu.EXTENSION_PROJECTS));
    config.setColumnAxisScalesWithContent(true);
    config.setMergeContent(true);

    Document document = createVisioDocument(config, new LandscapeOptionsBean());

    assertNumberOfShapesForMaster(document, AXIS_ELEMENT_MASTER_ID, 8);
    assertNumberOfShapesForMaster(document, CONTENT_MASTER_ID, 5);

    // check that information system releases # 1 is three cells high
    XPath xPath = factoryXPath.newXPath();

    // With the xPathexpression we search an attribute or property value in a xmlFile.
    XPathExpression xpathA = xPath.compile("//Shape[Text/text() = 'IS Release # 1']/XForm/Height");
    // The variable contains the needed attribute.
    String heightA = xpathA.evaluate(document);

    // With the xPathexpression we search an attribute or property value in a xmlFile.
    XPathExpression xpathB = xPath.compile("//Shape[Text/text() = 'IS Release # 2']/XForm/Height");
    // The variable contains the needed attribute.
    String heightB = xpathB.evaluate(document);

    double heightIsr1 = Double.parseDouble(heightA);
    double heightIsr2 = Double.parseDouble(heightB);

    assertTrue(heightIsr1 > 3 * heightIsr2);
    // assertTrue(heightIsr1 < 4 * heightIsr2); TODO

    // we don't expect any legend here
    checkLegend(document, 0, 0, 0);

    commit();
  }

  @Test
  public void testExportOverIsrAttributes() {
    LandscapeDiagramConfigDTO config = new LandscapeDiagramConfigDTO();
    config.setRowAttributeId(atMv.getId());
    config.setColumnAttributeId(atSv.getId());
    config.setContentBbs(isReleases);
    config.setContentType(InformationSystemReleaseTypeQu.getInstance());
    config.setColumnAxisScalesWithContent(true);
    config.setMergeContent(false);

    Document document = createVisioDocument(config, getLandscapeOptions());

    assertNumberOfShapesForMaster(document, AXIS_ELEMENT_MASTER_ID, 8);
    assertNumberOfShapesForMaster(document, CONTENT_MASTER_ID, 8);

    checkLegend(document, 2, 5, 5);

    assertContentShapeProperties(document, "IS Release # 1", 1, Color.GREEN, 3);
    assertContentShapeProperties(document, "IS Release # 2", 0, Color.LIGHT_GRAY, 3);
    assertContentShapeProperties(document, "IS Release # 4", 0, Color.RED, 9);
    assertContentShapeProperties(document, "IS Release # 6", 1, Color.GREEN, 2);
    assertContentShapeProperties(document, "IS Release # 5", 0, Color.BLUE, 2);
    assertContentShapeProperties(document, "IS Release # 3", 0, Color.YELLOW, 9);
    assertContentShapeProperties(document, "IS Release # 7", 1, Color.GREEN, 1);

    commit();
  }

  @Test
  public void testExportOverTcrAttributes() {
    LandscapeDiagramConfigDTO config = new LandscapeDiagramConfigDTO();
    config.setRowAttributeId(atMv.getId());
    config.setColumnAttributeId(atSv.getId());
    config.setContentBbs(tcReleases);
    config.setContentType(TechnicalComponentReleaseTypeQu.getInstance());
    config.setColumnAxisScalesWithContent(true);
    config.setMergeContent(false);

    Document document = createVisioDocument(config, getLandscapeOptions());

    assertNumberOfShapesForMaster(document, AXIS_ELEMENT_MASTER_ID, 8);
    assertNumberOfShapesForMaster(document, CONTENT_MASTER_ID, 7);

    checkLegend(document, 2, 5, 5);

    assertContentShapeProperties(document, "TC 1 # 1", 1, Color.GREEN, 3);
    // assertContentShapeProperties(document, "TC 1 # 2", 2, Color.LIGHT_GRAY, 1);
    assertContentShapeProperties(document, "TC 1 # 2", 0, Color.LIGHT_GRAY, 3);
    assertContentShapeProperties(document, "TC 1 # 3", 0, Color.YELLOW, 9);
    assertContentShapeProperties(document, "TC 1 # 4", 1, Color.GREEN, 9);
    assertContentShapeProperties(document, "TC 2 # 1", 0, Color.BLUE, 2);
    assertContentShapeProperties(document, "TC 2 # 2", 1, Color.GREEN, 2);

    commit();
  }

  /**
   * Asserts that there are $occurances shapes with some property value of $name, a fill foreground
   * of color $color and a line pattern with id $linePatternId.
   * 
   * @throws Exception
   */
  private void assertContentShapeProperties(Document document, String name, int occurances, Color color, int linePatternId) {

    String hexCode = "#" + this.getColorStr(color);
    // The expression counts how many Shape objects with the needed name color and linePatternId are there.
    String xpath = "count(//Shape[(Text/text() = '" + name + "') and " + "(Fill/FillForegnd/text() = '" + hexCode + "') and "
        + "(Line/LinePattern/text() = '" + linePatternId + "')])";

    assertEqualsXPath(String.valueOf(occurances), xpath, document);
  }

  @Test
  public void testExportOverIsrBizMappingWithProcessAndBusinessUnitRelation() {
    Product prodToplevel = productDAO.getFirstElement();

    testDataHelper.createBusinessMapping(i1, bpA, buA, prodToplevel);
    testDataHelper.createBusinessMapping(i2, bpB, buB, prodToplevel);
    testDataHelper.createBusinessMapping(i2, bpC, buC, prodToplevel);
    commit();

    beginTransaction();

    // remove one business unit to test correct calculation of information system releases to
    // display
    List<BuildingBlock> buList = new ArrayList<BuildingBlock>(businessUnits);
    buList.remove(buA);

    LandscapeDiagramConfigDTO config = new LandscapeDiagramConfigDTO();
    config.setTopAxisBbs(businessProcesses);
    config.setSideAxisBbs(buList);
    config.setContentBbs(isReleases);
    config.setContentType(InformationSystemReleaseTypeQu.getInstance());
    config.setColumnExtension(InformationSystemReleaseTypeQu.getInstance().getRelations()
        .get(InformationSystemReleaseTypeQu.EXTENSION_BM_BUSINESSPROCESS));
    config.setRowExtension(InformationSystemReleaseTypeQu.getInstance().getRelations().get(InformationSystemReleaseTypeQu.EXTENSION_BM_BUSINESSUNIT));
    config.setColumnAxisScalesWithContent(true);
    config.setMergeContent(false);

    Document document = createVisioDocument(config, getLandscapeOptions());

    // check total shape count:
    // 20 grid lines
    // 2 information system releasess
    // 12 Business Processes
    // 12 businessunits
    // 12 color legend shapes
    // 12 line legend shapes
    // 2 axis lables
    // 1 title
    // 2 Logos
    // 1 generatedInformation
    // 17 master shapes
    // 24 names legend shapes
    String xPathStringA = "count(//Shape)";
    assertEqualsXPath("117", xPathStringA, document);

    // specific shape types
    assertNumberOfShapesForMaster(document, BUSINESS_PROCESS_MASTER_ID, 12);
    assertNumberOfShapesForMaster(document, AXIS_ELEMENT_MASTER_ID, 12);

    // one business unit should have line pattern 10 (dashed since not selected)
    String xPathStringB = "count(//Shape[@Master='39']/Line/LinePattern[text()='10'])";
    assertEqualsXPath("1", xPathStringB, document);

    assertIsr(document);

    checkLegend(document, 2, 5, 5);
    commit();
  }

  /**
   * evaluates the given xPathString and assertEquals the result
   * @param toAssert
   *          String to compare with
   * @param xPathString
   *          xPathString to evaluate
   * @param document
   *          document for xPath evaluation
   * @throws XPathExpressionException
   */
  private void assertEqualsXPath(String toAssert, String xPathString, Document document) {
    XPath xPath = factoryXPath.newXPath();
    XPathExpression xPathExpression;
    try {
      xPathExpression = xPath.compile(xPathString);
      String value = xPathExpression.evaluate(document);
      assertEquals(toAssert, value);
    } catch (XPathExpressionException e) {
      LOGGER.error(e);
    }
  }

  @Test
  public void testExportOverIsrBizMappingWithProcessAndProductRelation() {
    BusinessUnit orgToplevel = businessUnitDAO.getFirstElement();

    testDataHelper.createBusinessMapping(i1, bpA, orgToplevel, prodA);
    testDataHelper.createBusinessMapping(i2, bpB, orgToplevel, prodB);
    testDataHelper.createBusinessMapping(i2, bpC, orgToplevel, prodC);
    commit();

    beginTransaction();
    // remove one product to test correct calculation of information system releases to display
    List<BuildingBlock> prodExport = new ArrayList<BuildingBlock>(products);
    prodExport.remove(prodA);

    LandscapeDiagramConfigDTO config = new LandscapeDiagramConfigDTO();
    config.setTopAxisBbs(businessProcesses);
    config.setSideAxisBbs(prodExport);
    config.setContentBbs(isReleases);
    config.setContentType(InformationSystemReleaseTypeQu.getInstance());
    config.setColumnExtension(InformationSystemReleaseTypeQu.getInstance().getRelations()
        .get(InformationSystemReleaseTypeQu.EXTENSION_BM_BUSINESSPROCESS));
    config.setRowExtension(InformationSystemReleaseTypeQu.getInstance().getRelations().get(InformationSystemReleaseTypeQu.EXTENSION_BM_PRODUCT));
    config.setColumnAxisScalesWithContent(true);
    config.setMergeContent(false);

    LandscapeOptionsBean landscapeOptions = getLandscapeOptions();
    landscapeOptions.getLineOptionsBean().setDimensionAttributeId(Integer.valueOf(-1));
    Document document = createVisioDocument(config, landscapeOptions);

    // check total shape count:
    // 18 grid lines
    // 2 information system releases
    // 12 Business Processes
    // 8 products
    // 12 color legend shapes
    // 2 axis labels
    // 2 Logo titles
    // 1 generatedInformation
    // 17 master shapes
    // 21 names legend shapes (incl. container)
    String xPathStringA = "count(//Shape)";
    assertEqualsXPath("95", xPathStringA, document);

    // specific shape types
    assertNumberOfShapesForMaster(document, BUSINESS_PROCESS_MASTER_ID, 12);
    assertNumberOfShapesForMaster(document, AXIS_ELEMENT_MASTER_ID, 8);
    assertNumberOfShapesForMaster(document, CONTENT_MASTER_ID, 2);

    // product A should have line pattern 10 (dashed since not selected)
    String xPathStringB = "count(//Shape[Text/text() = 'Product A']/Line/LinePattern[text()='10'])";
    assertEqualsXPath("1", xPathStringB, document);

    assertIsr(document);

    checkLegend(document, 1, 5, 0);

    commit();
  }

  private void assertIsr(Document document) {
    // a total of two information system releases are in the diagram
    String xPathStringA = "count(//Text[contains(text(),'IS Release')])";
    assertEqualsXPath("2", xPathStringA, document);

    // exactly two information system releases # 2 exist. This is important! We used the business
    // mapping relation, so are information system releases shapes only for the two specified combinations
    String xPathStringB = "count(//Text[contains(text(),'IS Release # 2')])";
    assertEqualsXPath("2", xPathStringB, document);
  }

  private Document createVisioDocument(LandscapeDiagramConfigDTO config, ILandscapeOptions options) {

    LandscapeDiagramCreator mc = new LandscapeDiagramCreator(config, attributeTypeDAO, generalBuildingBlockService);
    LandscapeDiagram landscapeDiagram = mc.createLandscapeDiagram();
    VisioLandscapeDiagramExport me = new VisioLandscapeDiagramExport(landscapeDiagram, options, attributeTypeService, attributeValueService);
    de.iteratec.visio.model.Document exportDoc = me.createDiagram();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      exportDoc.write(out);
    } catch (IOException e) {
      LOGGER.error(e);
    }

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    Document document = null;
    try {
      builder = factory.newDocumentBuilder();
      document = builder.parse(new ByteArrayInputStream(out.toByteArray()));

    } catch (ParserConfigurationException e1) {
      LOGGER.error(e1);
    } catch (SAXException e) {
      LOGGER.error(e);
    } catch (IOException e) {
      LOGGER.error(e);
    }

    try {
      out.close();
    } catch (IOException e) {
      LOGGER.error(e);
    }

    assertNotNull("Generated Visio XML is null!", document);
    assertNotNull("Generated Visio XML document has no root element!", document.getDocumentElement());

    String fileName = Thread.currentThread().getStackTrace()[1].getMethodName() + ".vdx";

    VisioExportUtils.writeToFile(exportDoc, fileName);
    return document;
  }

  private void checkLegend(Document document, int numTitles, int numColor, int numLinePattern) {
    assertNumberOfShapesForMaster(document, LEGEND_TITLE_MASTER_ID, numTitles);
    assertNumberOfShapesForMaster(document, LEGEND_ITEM_MASTER_ID, numColor + numLinePattern);
    assertNumberOfShapesForMaster(document, COLOR_SAMPLE_MASTER_ID, numColor);
    assertNumberOfShapesForMaster(document, LINE_SAMPLE_MASTER_ID, numLinePattern);
  }

  private void assertNumberOfShapesForMaster(Document document, int masterId, int expectedCount) {
    String xpath = "count(//Page//Shape[@Master='" + masterId + "'])";
    assertEqualsXPath(String.valueOf(expectedCount), xpath, document);
  }

  private void initISReleases() throws IteraplanException {
    InformationSystem i = testDataHelper.createInformationSystem("IS Release");
    i1 = testDataHelper.createInformationSystemRelease(i, "1", "", null, null, InformationSystemRelease.TypeOfStatus.CURRENT);
    i2 = testDataHelper.createInformationSystemRelease(i, "2", "", null, null, InformationSystemRelease.TypeOfStatus.CURRENT);
    i3 = testDataHelper.createInformationSystemRelease(i, "3", "", null, null, InformationSystemRelease.TypeOfStatus.CURRENT);
    i4 = testDataHelper.createInformationSystemRelease(i, "4", "", null, null, InformationSystemRelease.TypeOfStatus.CURRENT);
    i5 = testDataHelper.createInformationSystemRelease(i, "5", "", null, null, InformationSystemRelease.TypeOfStatus.CURRENT);
    i6 = testDataHelper.createInformationSystemRelease(i, "6", "", null, null, InformationSystemRelease.TypeOfStatus.CURRENT);
    i7 = testDataHelper.createInformationSystemRelease(i, "7", "", null, null, InformationSystemRelease.TypeOfStatus.CURRENT);
    isReleases = Arrays.asList(new BuildingBlock[] { i1, i2, i3, i4, i5, i6, i7 });

    InfrastructureElement root = infrastructureElementService.getFirstElement();
    InfrastructureElement ci1 = testDataHelper.createInfrastructureElement("ci1", "ie1");
    InfrastructureElement ci2 = testDataHelper.createInfrastructureElement("ci2", "ie2");
    InfrastructureElement ci3 = testDataHelper.createInfrastructureElement("ci3", "ie3");
    InfrastructureElement ci4 = testDataHelper.createInfrastructureElement("ci4", "ie4");
    ci1.addParent(root);
    ci2.addParent(root);
    ci3.addParent(root);
    ci4.addParent(root);
    ciList = Arrays.asList(new BuildingBlock[] { ci1, ci2, ci3, ci4 });

    i1.addInfrastructureElement(ci1);
    i1.addInfrastructureElement(ci2);
    i2.addInfrastructureElement(ci3);
    i3.addInfrastructureElement(ci4);
    i4.addInfrastructureElement(ci1);
    i5.addInfrastructureElement(ci2);
    i6.addInfrastructureElement(ci3);

    Project projectRoot = projectService.getFirstElement();
    Project project1 = testDataHelper.createProject("Project 1", "itt1");
    Project project2 = testDataHelper.createProject("Project 2", "itt2");
    Project project3 = testDataHelper.createProject("Project 3", "itt3");
    Project project4 = testDataHelper.createProject("Project 4", "itt4");
    project1.addParent(projectRoot);
    project2.addParent(projectRoot);
    project3.addParent(projectRoot);
    project4.addParent(projectRoot);
    projects = Arrays.asList(new BuildingBlock[] { project1, project2, project3, project4 });

    i1.addProject(project1);
    i1.addProject(project2);
    i1.addProject(project3);
    i2.addProject(project4);
    i3.addProject(project1);
    i3.addProject(project2);
    i4.addProject(project4);
  }

  private void initTcrs() throws IteraplanException {
    TechnicalComponent tc1 = testDataHelper.createTechnicalComponent("TC 1", true, true);
    TechnicalComponent tc2 = testDataHelper.createTechnicalComponent("TC 2", false, true);

    tcRel1 = testDataHelper.createTCRelease(tc1, "1", "1#1", null, null, TechnicalComponentRelease.TypeOfStatus.CURRENT, true);
    tcRel2 = testDataHelper.createTCRelease(tc1, "2", "1#2", null, null, TechnicalComponentRelease.TypeOfStatus.CURRENT, true);
    tcRel3 = testDataHelper.createTCRelease(tc1, "3", "1#3", null, null, TechnicalComponentRelease.TypeOfStatus.PLANNED, true);
    tcRel4 = testDataHelper.createTCRelease(tc1, "4", "1#4", null, null, TechnicalComponentRelease.TypeOfStatus.TARGET, true);
    tcRel5 = testDataHelper.createTCRelease(tc2, "1", "2#1", null, null, TechnicalComponentRelease.TypeOfStatus.CURRENT, true);
    tcRel6 = testDataHelper.createTCRelease(tc2, "2", "2#2", null, null, TechnicalComponentRelease.TypeOfStatus.PLANNED, true);
    tcReleases = Arrays.asList(new BuildingBlock[] { tcRel1, tcRel2, tcRel3, tcRel4, tcRel5, tcRel6 });

    i1.addTechnicalComponentRelease(tcRel1);
    i1.addTechnicalComponentRelease(tcRel5);
    i2.addTechnicalComponentRelease(tcRel3);
    i3.addTechnicalComponentRelease(tcRel4);
    i4.addTechnicalComponentRelease(tcRel2);
    i5.addTechnicalComponentRelease(tcRel6);
    i6.addTechnicalComponentRelease(tcRel6);
    i7.addTechnicalComponentRelease(tcRel1);

    ArchitecturalDomain root = architecturalDomainService.getFirstElement();
    ArchitecturalDomain ad1 = testDataHelper.createArchitecturalDomain("A-Dom 1", "");
    ArchitecturalDomain ad2 = testDataHelper.createArchitecturalDomain("A-Dom 2", "");
    ArchitecturalDomain ad3 = testDataHelper.createArchitecturalDomain("A-Dom 3", "");
    ad1.addParent(root);
    ad2.addParent(root);
    ad3.addParent(root);
    adList = Arrays.asList(new BuildingBlock[] { ad1, ad2, ad3 });

    tcRel1.addArchitecturalDomain(ad1);
    tcRel1.addArchitecturalDomain(ad2);
    tcRel2.addArchitecturalDomain(ad2);
    tcRel3.addArchitecturalDomain(ad1);
    tcRel4.addArchitecturalDomain(ad1);
    tcRel5.addArchitecturalDomain(ad3);
    tcRel6.addArchitecturalDomain(ad2);
  }

  private void initAttributes() throws IteraplanException {

    AttributeTypeGroup atg = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    this.atMv = testDataHelper.createEnumAttributeType("Multivalue", "Testing", Boolean.TRUE, atg);
    atMv.addBuildingBlockTypeTwoWay(buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    atMv.addBuildingBlockTypeTwoWay(buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE));

    this.atSv = testDataHelper.createEnumAttributeType("Singlevalue", "Testing", Boolean.FALSE, atg);
    atSv.addBuildingBlockTypeTwoWay(buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    atSv.addBuildingBlockTypeTwoWay(buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE));

    createAttributeValueAssignments();

  }

  private void createAttributeValueAssignments() {
    final AttributeValue atMvV1 = testDataHelper.createEnumAV("One", "One", atMv);
    final AttributeValue atMvV2 = testDataHelper.createEnumAV("Two", "Two", atMv);
    final AttributeValue atMvV3 = testDataHelper.createEnumAV("Three", "Three", atMv);
    final AttributeValue atMvV4 = testDataHelper.createEnumAV("Four", "Four", atMv);

    final AttributeValue atSvV1 = testDataHelper.createEnumAV("Eins", "Eins", atSv);
    final AttributeValue atSvV2 = testDataHelper.createEnumAV("Zwei", "Zwei", atSv);
    final AttributeValue atSvV3 = testDataHelper.createEnumAV("Drei", "Drei", atSv);
    final AttributeValue atSvV4 = testDataHelper.createEnumAV("Vier", "Vier", atSv);

    testDataHelper.createAVA(i1, atMvV1);
    testDataHelper.createAVA(i2, atMvV2);
    testDataHelper.createAVA(i3, atMvV3);
    testDataHelper.createAVA(i2, atMvV4);
    testDataHelper.createAVA(i4, atMvV2);
    testDataHelper.createAVA(i7, atMvV3);
    testDataHelper.createAVA(i5, atMvV4);
    testDataHelper.createAVA(i6, atMvV1);
    testDataHelper.createAVA(i1, atSvV1);
    testDataHelper.createAVA(i2, atSvV1);
    testDataHelper.createAVA(i3, atSvV2);
    testDataHelper.createAVA(i4, atSvV2);
    testDataHelper.createAVA(i5, atSvV3);
    testDataHelper.createAVA(i6, atSvV3);
    testDataHelper.createAVA(i7, atSvV4);

    testDataHelper.createAVA(tcRel1, atMvV1);
    testDataHelper.createAVA(tcRel1, atSvV1);
    testDataHelper.createAVA(tcRel2, atMvV2);
    testDataHelper.createAVA(tcRel2, atMvV4);
    testDataHelper.createAVA(tcRel2, atSvV1);
    testDataHelper.createAVA(tcRel3, atMvV3);
    testDataHelper.createAVA(tcRel3, atSvV2);
    testDataHelper.createAVA(tcRel4, atMvV2);
    testDataHelper.createAVA(tcRel4, atSvV2);
    testDataHelper.createAVA(tcRel5, atMvV4);
    testDataHelper.createAVA(tcRel5, atSvV3);
    testDataHelper.createAVA(tcRel6, atMvV1);
    testDataHelper.createAVA(tcRel6, atSvV3);
  }

  private String getColorStr(Color color) {
    String rgb = Integer.toHexString(color.getRGB());
    rgb = rgb.substring(2, rgb.length());
    return rgb;
  }

  private void initBusinessUnits() {
    buA = testDataHelper.createBusinessUnit("BusinessUnitA", "");
    buB = testDataHelper.createBusinessUnit("BusinessUnitB", "");
    buC = testDataHelper.createBusinessUnit("BusinessUnitC", "");
    BusinessUnit buD = testDataHelper.createBusinessUnit("BusinessUnitD", "");
    BusinessUnit buE = testDataHelper.createBusinessUnit("BusinessUnitE", "");
    BusinessUnit buF = testDataHelper.createBusinessUnit("BusinessUnitF", "");
    BusinessUnit buG = testDataHelper.createBusinessUnit("BusinessUnitG", "");
    BusinessUnit buH = testDataHelper.createBusinessUnit("BusinessUnitH", "");
    BusinessUnit buI = testDataHelper.createBusinessUnit("BusinessUnitI", "");
    BusinessUnit buJ = testDataHelper.createBusinessUnit("BusinessUnitJ", "");
    BusinessUnit buK = testDataHelper.createBusinessUnit("BusinessUnitK", "");
    BusinessUnit buL = testDataHelper.createBusinessUnit("BusinessUnitL", "");
    BusinessUnit rootBusinessUnit = businessUnitDAO.getFirstElement();

    buA.addParent(rootBusinessUnit);
    buB.addParent(rootBusinessUnit);
    buC.addParent(rootBusinessUnit);
    buD.addParent(rootBusinessUnit);
    buE.addParent(buA);
    buF.addParent(buA);
    buG.addParent(buC);
    buH.addParent(buC);
    buI.addParent(buF);
    buJ.addParent(buG);
    buK.addParent(rootBusinessUnit);
    buL.addParent(buG);
    businessUnits = Arrays.asList(new BuildingBlock[] { buA, buB, buC, buD, buE, buF, buG, buH, buI, buJ, buK, buL });
  }

  private void initProducts() {
    prodA = testDataHelper.createProduct("Product A", "a");
    prodB = testDataHelper.createProduct("Product B", "b");
    prodC = testDataHelper.createProduct("Product C", "c");
    Product prodD = testDataHelper.createProduct("Product D", "d");
    Product prodE = testDataHelper.createProduct("Product E", "e");
    Product prodF = testDataHelper.createProduct("Product F", "f");
    Product prodG = testDataHelper.createProduct("Product G", "g");
    Product prodH = testDataHelper.createProduct("Product H", "h");
    Product topLevelProduct = productDAO.getFirstElement();
    prodA.addParent(topLevelProduct);
    prodB.addParent(prodA);
    prodC.addParent(prodA);
    prodD.addParent(topLevelProduct);
    prodE.addParent(topLevelProduct);
    prodF.addParent(topLevelProduct);
    prodG.addParent(topLevelProduct);
    prodH.addParent(topLevelProduct);
    products = Arrays.asList(new BuildingBlock[] { prodA, prodB, prodC, prodD, prodE, prodF, prodG, prodH });
  }

  private void initProcesses() {
    bpA = testDataHelper.createBusinessProcess("BpA", "a");
    bpB = testDataHelper.createBusinessProcess("BpB", "b");
    bpC = testDataHelper.createBusinessProcess("BpC", "c");
    BusinessProcess bpD = testDataHelper.createBusinessProcess("BpD", "d");
    BusinessProcess bpE = testDataHelper.createBusinessProcess("BpE", "");
    BusinessProcess bpF = testDataHelper.createBusinessProcess("BpF", "");
    BusinessProcess bpG = testDataHelper.createBusinessProcess("BpG", "");
    BusinessProcess bpH = testDataHelper.createBusinessProcess("BpH", "");
    BusinessProcess bpI = testDataHelper.createBusinessProcess("BpI", "");
    BusinessProcess bpJ = testDataHelper.createBusinessProcess("BpJ", "");
    BusinessProcess bpK = testDataHelper.createBusinessProcess("BpK", "");
    BusinessProcess bpL = testDataHelper.createBusinessProcess("BpL", "");
    BusinessProcess bpTop = businessProcessDAO.getFirstElement();

    bpA.addParent(bpTop);
    bpB.addParent(bpTop);
    bpC.addParent(bpTop);
    bpD.addParent(bpTop);
    bpK.addParent(bpTop);

    bpE.addParent(bpA);
    bpI.addParent(bpF);
    bpF.addParent(bpC);
    bpG.addParent(bpC);
    bpH.addParent(bpC);
    bpJ.addParent(bpG);
    bpL.addParent(bpG);

    businessProcesses = Arrays.asList(new BuildingBlock[] { bpA, bpB, bpC, bpD, bpE, bpF, bpG, bpH, bpI, bpJ, bpK, bpL });
  }
}

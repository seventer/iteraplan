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
package de.iteratec.iteraplan.businesslogic.exchange.svg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagramCreator;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.svg.SvgGraphicWriter;
import de.iteratec.svg.model.SvgExportException;


public class SvgMasterplanExportTest extends BaseTransactionalTestSupport {

  private static final Logger         LOGGER                  = Logger.getIteraplanLogger(SvgMasterplanExportTest.class);

  private int                         idNumber                = 0;
  private static final String[]       LEGEND_SHAPES           = { "LegendBoxRoot", "ColorLegendFieldRoot" };
  private static final String[]       MONTHS                  = { "Jan", "Feb", "Mrz", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez" };
  private static final String[]       LABELS                  = { "uX4XjD", "gr3kJX", "q9Am7j", "GD1v3e", "WFCoKF", "13kmuF", "XPXs7g", "bPTplD" };
  private static final String[]       LABELS_ORDERED          = { "bPTplD", "13kmuF", "WFCoKF", "XPXs7g", "GD1v3e", "gr3kJX", "q9Am7j", "uX4XjD", };
  private static final String         TEST_DESCRIPTION        = "descr";

  private static final String         STANDARD_START_DATE     = "10.05.2002";

  private final List<TestDocument>[]  documents               = new ArrayList[] { new ArrayList<TestDocument>(4), new ArrayList<TestDocument>(4),
      new ArrayList<TestDocument>(3), new ArrayList<TestDocument>(3), new ArrayList<TestDocument>(1) };

  private List<BuildingBlock>         hierarchicalEntities    = new ArrayList<BuildingBlock>();
  private List<BuildingBlock>         nonHierarchicalEntities = new ArrayList<BuildingBlock>();

  @Autowired
  private AttributeTypeService        attributeTypeService;
  @Autowired
  private AttributeValueService       attributeValueService;
  @Autowired
  private BuildingBlockServiceLocator buildingBlockServiceLocator;
  @Autowired
  private TestDataHelper2             testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    setIdNumber(0);
  }

  @Test
  public void testSvgMasterplan() {
    hierarchicalEntities = createTestIpureleasesHierarchical();
    nonHierarchicalEntities = createTestIpureleases();

    createDocumentsStartsWithJan();
    createDocumentsEndsWithDez();
    createNonHierarchicalDocuments();
    createHierarchicalDocuments();
    createDocumentWithLegend();

    checkOrder();
    checkLabels();
    checkMonths();
    checkYears();
    checkLegend();
  }

  /**
   * Checks the order of the labels.
   */

  private void checkOrder() {
    String document = documents[2].get(0).document;
    for (int i = 0; i < (LABELS.length - 1); i++) {
      assertTrue("[FAILED] The label \"" + LABELS_ORDERED[i] + "\" is expected to appear before the label \"" + LABELS_ORDERED[i + 1] + "\".",
          document.indexOf(LABELS_ORDERED[i]) < document.indexOf(LABELS_ORDERED[i + 1]));
    }
  }

  /**
   * Checks if the draw grafik contains the expected count of month labels
   */
  public void checkMonths() {
    assertCounts(documents[0].get(0), new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, MONTHS);
    assertCounts(documents[0].get(1), new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, MONTHS);
    assertCounts(documents[0].get(2), new int[] { 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 }, MONTHS);
    assertCounts(documents[0].get(3), new int[] { 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1 }, MONTHS);

    assertCounts(documents[1].get(0), new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, MONTHS);
    assertCounts(documents[1].get(1), new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, MONTHS);
    assertCounts(documents[1].get(2), new int[] { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1 }, MONTHS);
    assertCounts(documents[1].get(3), new int[] { 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2 }, MONTHS);

    assertCounts(documents[2].get(0), new int[] { 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1 }, MONTHS);
    assertCounts(documents[2].get(1), new int[] { 3, 3, 3, 3, 4, 3, 3, 3, 3, 3, 3, 3 }, MONTHS);
    assertCounts(documents[2].get(2), new int[] { 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5 }, MONTHS);

    assertCounts(documents[3].get(0), new int[] { 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1 }, MONTHS);
    assertCounts(documents[3].get(1), new int[] { 3, 3, 3, 3, 4, 3, 3, 3, 3, 3, 3, 3 }, MONTHS);
    assertCounts(documents[3].get(2), new int[] { 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5 }, MONTHS);
  }

  /**
   * Checks if the draw grafik contains the expected count of year labels
   * 
   * @throws Exception
   */
  public void checkYears() {
    String[] years = { "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008" };

    assertCounts(documents[0].get(0), new int[] { 0, 1, 0 }, years, LABELS_ORDERED[0]);
    assertCounts(documents[0].get(1), new int[] { 0, 1, 0 }, years, LABELS_ORDERED[0]);
    assertCounts(documents[0].get(2), new int[] { 0, 1, 0 }, years, LABELS_ORDERED[0]);
    assertCounts(documents[0].get(3), new int[] { 0, 1, 1, 0 }, years, LABELS_ORDERED[0]);

    assertCounts(documents[1].get(0), new int[] { 0, 1, 0 }, years, LABELS_ORDERED[0]);
    assertCounts(documents[1].get(1), new int[] { 0, 1, 0 }, years, LABELS_ORDERED[0]);
    assertCounts(documents[1].get(2), new int[] { 0, 1, 0 }, years, LABELS_ORDERED[0]);
    assertCounts(documents[1].get(3), new int[] { 0, 1, 1, 0 }, years, LABELS_ORDERED[0]);

    assertCounts(documents[2].get(0), new int[] { 0, 1, 1, 0 }, years, LABELS_ORDERED[0]);
    assertCounts(documents[2].get(1), new int[] { 0, 1, 1, 1, 1, 0 }, years, LABELS_ORDERED[0]);
    assertCounts(documents[2].get(2), new int[] { 0, 1, 1, 1, 1, 1, 1, 0 }, years, LABELS_ORDERED[0]);

    assertCounts(documents[3].get(0), new int[] { 0, 1, 1, 0 }, years, LABELS_ORDERED[0]);
    assertCounts(documents[3].get(1), new int[] { 0, 1, 1, 1, 1, 0 }, years, LABELS_ORDERED[0]);
    assertCounts(documents[3].get(2), new int[] { 0, 1, 1, 1, 1, 1, 1, 0 }, years, LABELS_ORDERED[0]);
  }

  public void checkLegend() {
    for (TestDocument document : documents[4]) {
      assertCounts(document, new int[] { 5, 4 }, LEGEND_SHAPES);
    }
  }

  /**
   * Checks if the draw graphic contains the expected count of the BuildingBlocks' labels.
   * 
   * @throws Exception
   */
  public void checkLabels() {
    assertCounts(documents[0].get(0), new int[] { 1, 1, 1, 1, 1, 1, 4, 2 }, LABELS);
    assertCounts(documents[0].get(1), new int[] { 1, 1, 1, 1, 1, 1, 4, 2 }, LABELS);
    assertCounts(documents[0].get(2), new int[] { 1, 1, 1, 1, 1, 1, 4, 2 }, LABELS);
    assertCounts(documents[0].get(3), new int[] { 1, 1, 1, 1, 1, 1, 4, 2 }, LABELS);

    assertCounts(documents[1].get(0), new int[] { 1, 1, 1, 1, 1, 1, 4, 2 }, LABELS);
    assertCounts(documents[1].get(1), new int[] { 1, 1, 1, 1, 1, 1, 4, 2 }, LABELS);
    assertCounts(documents[1].get(2), new int[] { 1, 1, 1, 1, 1, 1, 4, 2 }, LABELS);
    assertCounts(documents[1].get(3), new int[] { 1, 1, 1, 1, 1, 1, 4, 2 }, LABELS);

    assertCounts(documents[2].get(0), new int[] { 1, 1, 1, 1, 1, 1, 4, 2 }, LABELS);
    assertCounts(documents[2].get(1), new int[] { 1, 1, 1, 1, 1, 1, 4, 2 }, LABELS);
    assertCounts(documents[2].get(2), new int[] { 1, 1, 1, 1, 1, 1, 4, 2 }, LABELS);

    assertCounts(documents[3].get(0), new int[] { 7, 3, 2, 1, 1, 1, 13, 2 }, LABELS);
    assertCounts(documents[3].get(1), new int[] { 7, 3, 2, 1, 1, 1, 13, 2 }, LABELS);
    assertCounts(documents[3].get(2), new int[] { 7, 3, 2, 1, 1, 1, 13, 2 }, LABELS);

    for (TestDocument document : documents[4]) {
      assertCounts(document, new int[] { 1, 1, 1, 1, 1, 1, 4, 2 }, LABELS);
    }
  }

  public void createDocumentsStartsWithJan() {
    documents[0].add(createDocumentWithConditions("10.01.2002", "15.01.2002", -1, false));
    documents[0].add(createDocumentWithConditions("01.01.2002", "31.01.2002", -1, false));
    documents[0].add(createDocumentWithConditions("10.01.2002", "15.05.2002", -1, false));
    documents[0].add(createDocumentWithConditions("10.01.2002", "15.05.2003", -1, false));
  }

  public void createDocumentsEndsWithDez() {
    documents[1].add(createDocumentWithConditions("10.12.2002", "15.12.2002", -1, false));
    documents[1].add(createDocumentWithConditions("01.12.2002", "31.12.2002", -1, false));
    documents[1].add(createDocumentWithConditions("10.07.2002", "15.12.2002", -1, false));
    documents[1].add(createDocumentWithConditions("10.07.2002", "15.12.2003", -1, false));
  }

  public void createNonHierarchicalDocuments() {
    documents[2].add(createDocumentWithConditions(STANDARD_START_DATE, "15.05.2003", -1, false));
    documents[2].add(createDocumentWithConditions(STANDARD_START_DATE, "15.05.2005", -1, false));
    documents[2].add(createDocumentWithConditions(STANDARD_START_DATE, "15.05.2007", -1, false));
  }

  public void createHierarchicalDocuments() {
    documents[3].add(createDocumentWithConditions(STANDARD_START_DATE, "15.05.2003", -1, true));
    documents[3].add(createDocumentWithConditions(STANDARD_START_DATE, "15.05.2005", -1, true));
    documents[3].add(createDocumentWithConditions(STANDARD_START_DATE, "15.05.2007", -1, true));
  }

  public void createDocumentWithLegend() {
    documents[4].add(createDocumentWithConditions("10.05.2004", "15.05.2004", 0, false));
  }

  /**
   * Generates a {@link TestDocument} with its error track.
   * 
   * @param colorId
   *          the Id of the color schema for the Legend (-1 if none). See
   *          {@link de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.IMasterplanOptions IMasterplanOptions}
   * @param hierarchical
   *          tells whether the entities should be hierarchically sorted or not.
   * @return an occurrence of the generated {@link TestDocument}.
   * @throws IOException
   */
  private TestDocument createDocumentWithConditions(String startDate, String endDate, int colorId, boolean hierarchical) {
    MasterplanOptionsBean options = new MasterplanOptionsBean();

    options.setStartDateString(startDate);
    options.setEndDateString(endDate);

    MasterplanRowTypeOptions level0Options = new MasterplanRowTypeOptions("", options.getSelectedBbType(), 0, new ArrayList<DateInterval>(),
        new ArrayList<BBAttribute>(), new ArrayList<ColumnEntry>());
    options.setLevel0Options(level0Options);

    level0Options.setHierarchicalSort(hierarchical);
    level0Options.getColorOptions().setDimensionAttributeId(Integer.valueOf(colorId));

    String errorTrack = "(from: " + startDate + " to: " + endDate + " - " + (colorId == -1 ? "without legend" : "with legend") + " - "
        + (hierarchical ? "hierarchical" : "non hierarchical") + ")";

    String document = getSvgDocumentAsString(hierarchical ? hierarchicalEntities : nonHierarchicalEntities, options);

    assertNotNull("[FAILED] Document-string is null! " + errorTrack, document);
    assertTrue("[FAILED] Document-string is empty! " + errorTrack, document.length() > 0);
    assertTrue("[FAILED] Document-string is too small! " + errorTrack, document.length() > 20);

    return new TestDocument(document, errorTrack);
  }

  /**
   * Checks if the given documents contains the right number of the given terms.
   * 
   * @param document
   *          document to check.
   * @param counts
   *          the expected coccurences' counts of the given terms.
   * @param patterns
   *          the terms which have to be counted.
   */
  private void assertCounts(TestDocument document, int[] counts, String[] patterns) {
    assertCounts(document, counts, patterns, null);
  }

  /**
   * Checks if the given documents contains the right number of the given terms, that appear before
   * the first occurrence of the stop-string.
   * 
   * @param document
   *          document to check.
   * @param counts
   *          the expected coccurences' counts of the given terms.
   * @param patterns
   *          the terms which have to be counted.
   * @param stop
   *          the term where the search has to be stopped
   */
  private void assertCounts(TestDocument document, int[] counts, String[] patterns, String stop) {
    for (int i = 0; i < counts.length; i++) {
      int occurrences = countOccurrences(document.document, patterns[i], stop);
      assertEquals("[FAILED] Incorrect count of the label \"" + patterns[i] + "\" in " + document.document + ". " + document.errorTrack, counts[i],
          occurrences);
    }
  }

  /**
   * Generates a test entities list.
   * 
   * @param hierarchical
   *          tells whether the entities should be sorted hierarchically or not.
   * @return the generated entities list.
   * @throws IteraplanBusinessException
   */
  private List<BuildingBlock> createTestIpureleases() throws IteraplanBusinessException {
    List<BuildingBlock> entityList = new ArrayList<BuildingBlock>();

    InformationSystem ipuA = testDataHelper.createInformationSystem(LABELS[6]);

    InformationSystemRelease ipurA1 = testDataHelper.createInformationSystemRelease(ipuA, LABELS[0], TEST_DESCRIPTION, null, "01.03.2005",
        InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease ipurA2 = testDataHelper.createInformationSystemRelease(ipuA, LABELS[1], TEST_DESCRIPTION, "29.02.2004", "15.12.2005",
        InformationSystemRelease.TypeOfStatus.INACTIVE);
    InformationSystemRelease ipurA3 = testDataHelper.createInformationSystemRelease(ipuA, LABELS[2], TEST_DESCRIPTION, "11.07.2005", "20.04.2006",
        InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease ipurA4 = testDataHelper.createInformationSystemRelease(ipuA, LABELS[3], TEST_DESCRIPTION, "03.02.2006", "20.04.2100",
        InformationSystemRelease.TypeOfStatus.PLANNED);

    InformationSystem ipuB = testDataHelper.createInformationSystem(LABELS[7]);

    InformationSystemRelease ipurB1 = testDataHelper.createInformationSystemRelease(ipuB, LABELS[4], TEST_DESCRIPTION, "17.09.2006", "17.09.2007",
        InformationSystemRelease.TypeOfStatus.TARGET);
    InformationSystemRelease ipurB2 = testDataHelper.createInformationSystemRelease(ipuB, LABELS[5], TEST_DESCRIPTION, null, null,
        InformationSystemRelease.TypeOfStatus.PLANNED);

    entityList.add(ipurA1);
    entityList.add(ipurA2);
    entityList.add(ipurA3);
    entityList.add(ipurA4);
    entityList.add(ipurB1);

    entityList.add(ipurB2);

    return entityList;
  }

  private List<BuildingBlock> createTestIpureleasesHierarchical() throws IteraplanBusinessException {
    List<BuildingBlock> entityList = new ArrayList<BuildingBlock>();

    InformationSystem ipuA = testDataHelper.createInformationSystem(LABELS[6] + "h");

    InformationSystemRelease ipurA1 = testDataHelper.createInformationSystemRelease(ipuA, LABELS[0], TEST_DESCRIPTION, null, "01.03.2005",
        InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease ipurA2 = testDataHelper.createInformationSystemRelease(ipuA, LABELS[1], TEST_DESCRIPTION, "29.02.2004", "15.12.2005",
        InformationSystemRelease.TypeOfStatus.INACTIVE);
    InformationSystemRelease ipurA3 = testDataHelper.createInformationSystemRelease(ipuA, LABELS[2], TEST_DESCRIPTION, "11.07.2005", "20.04.2006",
        InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease ipurA4 = testDataHelper.createInformationSystemRelease(ipuA, LABELS[3], TEST_DESCRIPTION, "03.02.2006", "20.04.2100",
        InformationSystemRelease.TypeOfStatus.PLANNED);

    ipurA2.addParent(ipurA1);
    ipurA3.addParent(ipurA2);
    ipurA4.addParent(ipurA3);

    InformationSystem ipuB = testDataHelper.createInformationSystem(LABELS[7] + "h");

    InformationSystemRelease ipurB1 = testDataHelper.createInformationSystemRelease(ipuB, LABELS[4], TEST_DESCRIPTION, "17.09.2006", "17.09.2007",
        InformationSystemRelease.TypeOfStatus.TARGET);
    InformationSystemRelease ipurB2 = testDataHelper.createInformationSystemRelease(ipuB, LABELS[5], TEST_DESCRIPTION, null, null,
        InformationSystemRelease.TypeOfStatus.PLANNED);

    entityList.add(ipurA1);
    entityList.add(ipurA2);
    entityList.add(ipurA3);
    entityList.add(ipurA4);
    entityList.add(ipurB1);

    entityList.add(ipurB2);

    return entityList;
  }

  /**
   * Counts the occurrences of the pattern in the given source-string, that appears before the first
   * occurrence of the stop-string.
   * 
   * @param source
   *          the string where the search has to be done.
   * @param pattern
   *          the string to search.
   * @param stop
   *          the string where search should stop on.
   * @return the count of the found occurrences.
   */
  private int countOccurrences(String source, String pattern, String stop) {
    int count = 0;
    int index = source.indexOf(pattern, 0);
    String stopFlag = stop;
    int stopIndex = (stopFlag == null) ? 0 : source.indexOf(stopFlag, 0);
    if (stopIndex < 0) {
      stopFlag = null;
    }
    while (index > -1) {
      if (stopFlag == null || index <= stopIndex) {
        count++;
      }
      index = source.indexOf(pattern, index + 1);
    }
    return count;
  }

  /**
   * Generates an SVG-test-document with the given options.
   * 
   * @return the generated SVG-document as String
   * @throws IOException
   */
  private String getSvgDocumentAsString(List<BuildingBlock> entities, MasterplanOptionsBean options) {
    MasterplanDiagramCreator masterplanDiagramCreator = new MasterplanDiagramCreator(options, buildingBlockServiceLocator, attributeTypeService,
        entities);
    MasterplanDiagram masterplanDiagram = masterplanDiagramCreator.createMasterplanDiagram();

    SvgMasterplanExport exp = new SvgMasterplanExport(masterplanDiagram, options, attributeTypeService, attributeValueService);
    de.iteratec.svg.model.Document exportDoc = exp.createDiagram();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      SvgGraphicWriter.writeToSvg(exportDoc, out);
      out.close();
    } catch (SvgExportException e) {
      LOGGER.error(e);
    } catch (IOException e) {
      LOGGER.error(e);
    }
    return out.toString();
  }

  public void setIdNumber(int idNumber) {
    this.idNumber = idNumber;
  }

  public int getIdNumber() {
    return idNumber;
  }

  /**
   * This inner class is used to save an error track with each generated SVG-test-document. The
   * error track contains information about the option this document was drawn with.
   * 
   * @author wja
   */
  private static class TestDocument {
    private final String document;
    private final String errorTrack;

    protected TestDocument(String document, String errorTrack) {
      this.document = document;
      this.errorTrack = errorTrack;
    }
  }

}

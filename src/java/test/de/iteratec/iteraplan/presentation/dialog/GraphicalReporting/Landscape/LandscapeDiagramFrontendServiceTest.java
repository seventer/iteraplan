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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.Landscape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.TestHelper;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.LandscapeOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ManageLandscapeDiagramMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.IQStatusData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QPart;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QTimespanData;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.ExportService;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.businesslogic.service.QueryService;
import de.iteratec.iteraplan.businesslogic.service.RefreshHelperService;
import de.iteratec.iteraplan.businesslogic.service.SavedQueryService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.presentation.dialog.MemBeanSerializationTestHelper;


public class LandscapeDiagramFrontendServiceTest extends BaseTransactionalTestSupport {

  private static final Logger                 LOGGER           = Logger.getIteraplanLogger(LandscapeDiagramFrontendServiceTest.class);
  private static final String                 TEST_DESCRIPTION = "testDescription";

  private LandscapeDiagramFrontendServiceImpl feService;
  @Autowired
  private SavedQueryService                   savedQueryService;
  @Autowired
  private QueryService                        queryService;
  @Autowired
  private AttributeTypeService                attributeTypeService;
  @Autowired
  private InitFormHelperService               initFormHelperService;
  @Autowired
  private ExportService                       exportService;
  @Autowired
  private TestDataHelper2                     testDataHelper;
  @Autowired
  private RefreshHelperService                refreshHelper;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());

    feService = new LandscapeDiagramFrontendServiceImpl();
    feService.setSavedQueryService(savedQueryService);
    feService.setQueryService(queryService);
    feService.setAttributeTypeService(attributeTypeService);
    feService.setInitFormHelperService(initFormHelperService);
    feService.setExportService(exportService);
    feService.setRefreshHelperService(refreshHelper);
  }

  /**
   * This test loads a landscape diagram XML file containing test data and checks, if both the
   * resulting query configuration is correct and the result sets contain the correct data
   */
  @Test
  public void testLoadLandscapeDiagram() {
    SavedQuery savedQuery = new SavedQuery();
    String content = null;

    try {
      File file = new File(TestHelper.getInstance().getTestPath() + "/landscapeQueryTest.xml");
      content = FileUtils.readFileToString(file, "UTF-8");
    } catch (FileNotFoundException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (IOException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }

    savedQuery.setName("landscape");
    savedQuery.setType(ReportType.LANDSCAPE);
    savedQuery.setSchemaFile(SavedQuery.getSchemaMapping().get(ReportType.LANDSCAPE));
    savedQuery.setContent(content);
    getHibernateTemplate().save(savedQuery);

    // setup test data
    InformationSystem ipu = testDataHelper.createInformationSystem("IPU1");
    InformationSystemRelease ipuRel = testDataHelper.createInformationSystemRelease(ipu, "1.0");

    testDataHelper.createBusinessProcess("processname", "desc");
    testDataHelper.createBusinessProcess("processname2", "desc");
    BusinessUnit bu = testDataHelper.createBusinessUnit("BusinessUnit", "");
    BusinessUnit bu2 = testDataHelper.createBusinessUnit("BusinessUnit2", "");
    testDataHelper.createBusinessUnit("BusinessUnit3", "");
    bu2.addParent(bu);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("atg1", TEST_DESCRIPTION);
    EnumAT color = testDataHelper.createEnumAttributeType("Gesundheitszustand", TEST_DESCRIPTION, Boolean.FALSE, atg);
    EnumAT lineType = testDataHelper.createEnumAttributeType("Komplexit�t", TEST_DESCRIPTION, Boolean.FALSE, atg);
    testDataHelper.createEnumAV("Gut", TEST_DESCRIPTION, color);
    testDataHelper.createEnumAV("Mittel", TEST_DESCRIPTION, color);
    testDataHelper.createEnumAV("Schlecht", TEST_DESCRIPTION, color);

    testDataHelper.createEnumAV("Hoch", TEST_DESCRIPTION, lineType);
    testDataHelper.createEnumAV("Mittel", TEST_DESCRIPTION, lineType);
    testDataHelper.createEnumAV("Gering", TEST_DESCRIPTION, lineType);

    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(color);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(lineType);

    // commit the testdata and start testing

    commit();
    beginTransaction();

    ManageLandscapeDiagramMemoryBean memBean = new ManageLandscapeDiagramMemoryBean();
    memBean.setSavedQueryId(savedQuery.getId());

    feService.loadSavedQuery(memBean);
    // the file is now loaded into memBean

    testManageLandscapeDiagramMemoryBean(memBean, ipuRel, lineType, bu);

  }

  @Test
  public void testSerialization() {
    ManageLandscapeDiagramMemoryBean originalMemBean = new ManageLandscapeDiagramMemoryBean();
    MemBeanSerializationTestHelper.testSerializeDeserialize(originalMemBean);
  }

  private void testManageLandscapeDiagramMemoryBean(ManageLandscapeDiagramMemoryBean memBean, InformationSystemRelease ipuRel, EnumAT lineType,
                                                    BusinessUnit bu) {
    DynamicQueryFormData<?> contentForm = memBean.getQueryResult(LandscapeOptionsBean.CONTENT_QUERY).getQueryForms().get(0);
    assertEquals(InformationSystemReleaseTypeQu.getInstance(), contentForm.getType());

    IQStatusData status = contentForm.getQueryUserInput().getStatusQueryData();
    QTimespanData time = contentForm.getQueryUserInput().getTimespanQueryData();
    QPart part = contentForm.getQueryUserInput().getQueryFirstLevels().get(0).getQuerySecondLevels().get(0);

    assertEquals(DateUtils.formatAsString(new Date(), Locale.GERMAN), DateUtils.formatAsString(time.getStartDate(), Locale.GERMAN));
    assertEquals(DateUtils.formatAsString(new Date(new Date().getTime() + 500000000L), Locale.GERMAN),
        DateUtils.formatAsString(time.getEndDate(), Locale.GERMAN));
    assertEquals(4, status.getStatusMap().size());
    assertTrue(status.getStatus("typeOfStatus_current").booleanValue());
    assertFalse(status.getStatus("typeOfStatus_planned").booleanValue());
    assertFalse(status.getStatus("typeOfStatus_target").booleanValue());
    assertFalse(status.getStatus("typeOfStatus_inactive").booleanValue());

    assertEquals("userdefEnum_null_" + lineType.getId(), part.getChosenAttributeStringId());
    assertEquals(Constants.OPERATION_CONTAINSNOT_ID, part.getChosenOperationId());

    LandscapeOptionsBean landscapeOptions = memBean.getGraphicalOptions();

    ColorDimensionOptionsBean colorOptions = landscapeOptions.getColorOptionsBean();
    assertEquals("AFCEA8", colorOptions.getSelectedColors().get(0));
    assertEquals("Gut", colorOptions.getAttributeValues().get(0));
    assertEquals("F6DF95", colorOptions.getSelectedColors().get(1));
    assertEquals("Mittel", colorOptions.getAttributeValues().get(1));
    assertEquals("D79DAD", colorOptions.getSelectedColors().get(2));
    assertEquals("Schlecht", colorOptions.getAttributeValues().get(2));

    LineDimensionOptionsBean lineOptions = landscapeOptions.getLineOptionsBean();
    assertEquals("1", lineOptions.getSelectedLineTypes().get(0));
    assertEquals("Hoch", lineOptions.getAttributeValues().get(0));
    assertEquals("2", lineOptions.getSelectedLineTypes().get(1));
    assertEquals("Mittel", lineOptions.getAttributeValues().get(1));
    assertEquals("3", lineOptions.getSelectedLineTypes().get(2));
    assertEquals("Gering", lineOptions.getAttributeValues().get(2));

    assertEquals("1_1", landscapeOptions.getSelectedLevelRangeColumnAxis());
    assertEquals("1_1", landscapeOptions.getSelectedLevelRangeRowAxis());

    // ID set in selectedResultIds:
    QueryResult contentQuery = memBean.getQueryResult(LandscapeOptionsBean.CONTENT_QUERY);
    assertEquals(1, contentQuery.getSelectedResultIds().length);
    assertEquals(ipuRel.getId(), contentQuery.getSelectedResultIds()[0]);
    assertEquals(Integer.valueOf(12), ipuRel.getId());
    LOGGER.info("Postprocessing: {0}", contentQuery.getSelectedPostProcessingStrategies());

    // No selected result ids
    assertEquals(2, memBean.getQueryResult(LandscapeOptionsBean.COLUMN_QUERY).getSelectedResultIds().length);
    // assertEquals(process.getId(), memBean.getColumnQuery().getSelectedResultIds()[0]);
    // assertEquals(process2.getId(), memBean.getColumnQuery().getSelectedResultIds()[1]);

    // ID set in selectedResultIds:
    // 2 Units where found, but only one is in selected id array.
    QueryResult rowQuery = memBean.getQueryResult(LandscapeOptionsBean.ROW_QUERY);
    assertEquals(1, rowQuery.getSelectedResultIds().length);
    assertEquals(bu.getId(), rowQuery.getSelectedResultIds()[0]);
    assertEquals(Integer.valueOf(15), bu.getId());

    assertFalse(landscapeOptions.isColumnAxisScalesWithContent());

    // If an attribute id in the query doesn't exist anymore in DB, then it has to be substituted by
    // -1 otherwise query can not be loaded; here attribute with id=4 is no longer available,
    // so test checks if it is substituted by blank attribute in order to load query.
    assertEquals("blank_null_-1", rowQuery.getQueryForms().get(0).getQueryUserInput().getQueryFirstLevels().get(0).getQuerySecondLevels().get(0)
        .getChosenAttributeStringId());
  }
}

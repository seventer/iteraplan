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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.TestHelper;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalOptionsGetter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterSecondOrderBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.PortfolioOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.businesslogic.service.QueryService;
import de.iteratec.iteraplan.businesslogic.service.SavedQueryService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.presentation.dialog.MemBeanSerializationTestHelper;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.cluster.ClusterGraphicFrontendServiceImpl;


public class GraphicalReportBaseFrontendServiceTest extends BaseTransactionalTestSupport {
  private static final String                                            TEST_DESCRIPTION    = "testDescription";
  private static final String                                            ATTR_NULL_STRING_ID = "blank_null_-1";

  private GraphicalReportBaseFrontendServiceImpl<ManageReportMemoryBean> feService;
  @Autowired
  private SavedQueryService                                              savedQueryService;
  @Autowired
  private QueryService                                                   queryService;
  @Autowired
  private AttributeTypeService                                           attributeTypeService;
  @Autowired
  private InitFormHelperService                                          initFormHelperService;
  @Autowired
  private TestDataHelper2                                                testDataHelper;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());

    feService = new ClusterGraphicFrontendServiceImpl();
    feService.setSavedQueryService(savedQueryService);
    feService.setQueryService(queryService);
    feService.setAttributeTypeService(attributeTypeService);
    feService.setInitFormHelperService(initFormHelperService);
  }

  @Test
  public void testLoadInformationFlowDiagram() {
    SavedQuery savedQuery = new SavedQuery();
    String content = null;

    try {
      File file = new File(TestHelper.getInstance().getTestPath() + "/informationFlowQueryTest.xml");
      content = FileUtils.readFileToString(file, "UTF-8");
      assertFalse(StringUtils.isEmpty(content));
    } catch (FileNotFoundException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.FILE_NOT_FOUND_EXCEPTION, e);
    } catch (IOException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }

    savedQuery.setSchemaFile(SavedQuery.getSchemaMapping().get(ReportType.INFORMATIONFLOW));
    savedQuery.setName("informationflow");
    savedQuery.setType(ReportType.INFORMATIONFLOW);
    savedQuery.setContent(content);
    getHibernateTemplate().save(savedQuery);

    // create the attribute types which are referenced in the saved query
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("atg1", TEST_DESCRIPTION);
    // Multiassignment has to be FALSE because
    // reports don't support the attribute otherwise:
    EnumAT type = testDataHelper.createEnumAttributeType("Gesundheitszustand", TEST_DESCRIPTION, Boolean.FALSE, atg);
    testDataHelper.createEnumAV("Gut", TEST_DESCRIPTION, type);
    testDataHelper.createEnumAV("Mittel", TEST_DESCRIPTION, type);
    testDataHelper.createEnumAV("Schlecht", TEST_DESCRIPTION, type);

    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(type);
    commit();

    beginTransaction();

    ManageReportMemoryBean memBean = new ManageReportMemoryBean();
    memBean.setSavedQueryId(savedQuery.getId());
    memBean.setReportType(ReportType.INFORMATIONFLOW);

    feService.loadSavedQuery(memBean);
    // the file is now loaded into memBean

    DynamicQueryFormData<?> form = memBean.getQueryResult().getQueryForms().get(0);
    assertEquals(1, form.getQueryUserInput().getQueryFirstLevels().size());
    assertEquals(ATTR_NULL_STRING_ID, form.getQueryUserInput().getQueryFirstLevels().get(0).getQuerySecondLevels().get(0)
        .getChosenAttributeStringId());
    assertEquals(1, form.getQueryUserInput().getStatusQueryData().exportCheckedStatusList().size());
    assertEquals("typeOfStatus_current", form.getQueryUserInput().getStatusQueryData().exportCheckedStatusList().get(0));

    // If an attribute id in the query doesn't exist anymore in DB, then it has to be substituted by
    // -1 otherwise query can not be loaded; here attribute with id=4 is no longer available,
    // so test checks if it is substituted by blank attribute in order to load query.
    assertEquals(ATTR_NULL_STRING_ID, form.getQueryUserInput().getQueryFirstLevels().get(0).getQuerySecondLevels().get(0)
        .getChosenAttributeStringId());

  }

  @Test
  public void testPortfolioDiagram() {
    SavedQuery savedQuery = new SavedQuery();
    String content = null;

    try {
      File file = new File(TestHelper.getInstance().getTestPath() + "/portfolioQueryTest.xml");
      content = FileUtils.readFileToString(file, "UTF-8");
      assertFalse(StringUtils.isEmpty(content));
    } catch (FileNotFoundException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.FILE_NOT_FOUND_EXCEPTION, e);
    } catch (IOException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }

    savedQuery.setSchemaFile(SavedQuery.getSchemaMapping().get(ReportType.PORTFOLIO));
    savedQuery.setName("portfolio");
    savedQuery.setContent(content);
    savedQuery.setType(ReportType.PORTFOLIO);
    getHibernateTemplate().save(savedQuery);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("atg1", TEST_DESCRIPTION);
    // Multiassignment has to be FALSE because
    // reports don't support the attribute otherwise:
    EnumAT type = testDataHelper.createEnumAttributeType("Komplexit‰t", TEST_DESCRIPTION, Boolean.FALSE, atg);
    NumberAT numberType1 = testDataHelper.createNumberAttributeType("Betriebskosten", TEST_DESCRIPTION, atg);
    NumberAT numberType2 = testDataHelper.createNumberAttributeType("Eigenleistung Entwicklung", TEST_DESCRIPTION, atg);
    NumberAT numberType3 = testDataHelper.createNumberAttributeType("Eigenleistung Wartung", TEST_DESCRIPTION, atg);

    testDataHelper.createEnumAV("Hoch", TEST_DESCRIPTION, type);
    testDataHelper.createEnumAV("Mittel", TEST_DESCRIPTION, type);
    testDataHelper.createEnumAV("Gering", TEST_DESCRIPTION, type);

    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(type);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(numberType1);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(numberType2);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(numberType3);
    commit();

    beginTransaction();

    ManageReportMemoryBean memBean = new ManageReportMemoryBean();
    memBean.setSavedQueryId(savedQuery.getId());
    memBean.setReportType(savedQuery.getType());

    feService.loadSavedQuery(memBean);
    // the file is now loaded into memBean
    PortfolioOptionsBean bean = GraphicalOptionsGetter.getPortfolioOptions(memBean);

    DynamicQueryFormData<?> form = memBean.getQueryResult().getQueryForms().get(0);
    assertEquals(1, form.getQueryUserInput().getQueryFirstLevels().size());
    assertEquals(ATTR_NULL_STRING_ID, form.getQueryUserInput().getQueryFirstLevels().get(0).getQuerySecondLevels().get(0)
        .getChosenAttributeStringId());
    assertEquals(1, form.getQueryUserInput().getStatusQueryData().exportCheckedStatusList().size());
    assertEquals("typeOfStatus_current", form.getQueryUserInput().getStatusQueryData().exportCheckedStatusList().get(0));

    ColorDimensionOptionsBean colorOptions = bean.getColorOptionsBean();

    // Check if the non existing id is substituted by -1
    assertEquals(Integer.valueOf(-1), colorOptions.getDimensionAttributeId());

    assertEquals(numberType2.getId(), bean.getXAxisAttributeId());
    assertEquals(numberType3.getId(), bean.getYAxisAttributeId());
    assertEquals(numberType1.getId(), bean.getSizeAttributeId());

  }

  @Test
  public void testClusterDiagram() {
    SavedQuery savedQuery = new SavedQuery();
    String content = null;

    try {
      File file = new File(TestHelper.getInstance().getTestPath() + "/clusterQueryTest.xml");
      content = FileUtils.readFileToString(file, "UTF-8");
      assertFalse(StringUtils.isEmpty(content));
    } catch (FileNotFoundException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.FILE_NOT_FOUND_EXCEPTION, e);
    } catch (IOException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }

    savedQuery.setSchemaFile(SavedQuery.getSchemaMapping().get(ReportType.CLUSTER));
    savedQuery.setName("cluster");
    savedQuery.setType(ReportType.CLUSTER);
    savedQuery.setContent(content);
    getHibernateTemplate().saveOrUpdate(savedQuery);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("atg1", TEST_DESCRIPTION);
    // Multiassignment has to be FALSE because
    // reports don't support the attribute otherwise:
    EnumAT type = testDataHelper.createEnumAttributeType("Komplexit‰t", TEST_DESCRIPTION, Boolean.FALSE, atg);
    String lowComplexity = "Gering";
    String mediumComplexity = "Mittel";
    String highComplexity = "Hoch";
    testDataHelper.createEnumAV(highComplexity, TEST_DESCRIPTION, type);
    testDataHelper.createEnumAV(mediumComplexity, TEST_DESCRIPTION, type);
    testDataHelper.createEnumAV(lowComplexity, TEST_DESCRIPTION, type);

    InformationSystemDomain isd = testDataHelper.createInformationSystemDomain("Test", null);

    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(type);

    commit();

    beginTransaction();

    ManageReportMemoryBean memBean = new ManageReportMemoryBean();
    memBean.setSavedQueryId(savedQuery.getId());
    memBean.setReportType(savedQuery.getType());

    feService.loadSavedQuery(memBean);
    // the file is now loaded into memBean

    DynamicQueryFormData<?> form = memBean.getQueryResult().getQueryForms().get(0);
    assertEquals(1, form.getQueryUserInput().getQueryFirstLevels().size());
    assertEquals(ATTR_NULL_STRING_ID, form.getQueryUserInput().getQueryFirstLevels().get(0).getQuerySecondLevels().get(0)
        .getChosenAttributeStringId());
    assertEquals(1, form.getQueryUserInput().getStatusQueryData().exportCheckedStatusList().size());
    assertEquals("typeOfStatus_current", form.getQueryUserInput().getStatusQueryData().exportCheckedStatusList().get(0));

    ClusterOptionsBean bean = GraphicalOptionsGetter.getClusterOptions(memBean);
    ColorDimensionOptionsBean colorOptions = bean.getColorOptionsBean();
    List<ClusterSecondOrderBean> listOfColorOptions = bean.getSecondOrderBeans();
    List<AttributeType> at = bean.getAvailableAttributeTypes();

    assertEquals(type.getId(), at.get(0).getId());

    assertEquals(isd.getBuildingBlockType().getName(), listOfColorOptions.get(1).getName());

    List<EnumAV> attributeValues = ((EnumAT) at.get(0)).getSortedAttributeValues();
    assertEquals(highComplexity, attributeValues.get(0).getName());
    assertEquals(mediumComplexity, attributeValues.get(1).getName());
    assertEquals(lowComplexity, attributeValues.get(2).getName());

    assertEquals("AFCEA8", colorOptions.getSelectedColors().get(0));
    assertEquals("F6DF95", colorOptions.getSelectedColors().get(1));
    assertEquals("D79DAD", colorOptions.getSelectedColors().get(2));

    assertEquals(Integer.valueOf(0), colorOptions.getDimensionAttributeId());

    assertEquals("Ist", colorOptions.getAttributeValues().get(0));
    assertEquals("Plan", colorOptions.getAttributeValues().get(1));
    assertEquals("Soll", colorOptions.getAttributeValues().get(2));
    assertEquals("Auﬂer Betrieb", colorOptions.getAttributeValues().get(3));

  }

  @Test
  public void testSerialization() {
    ManageReportMemoryBean originalMemBean = new ManageReportMemoryBean();
    MemBeanSerializationTestHelper.testSerializeDeserialize(originalMemBean);
  }
}

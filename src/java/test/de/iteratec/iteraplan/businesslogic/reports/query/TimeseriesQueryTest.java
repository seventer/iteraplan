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
package de.iteratec.iteraplan.businesslogic.reports.query;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.reports.query.node.AssociatedLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Comparator;
import de.iteratec.iteraplan.businesslogic.reports.query.node.EnumAttributeLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.node.NumberAttributeLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Operation;
import de.iteratec.iteraplan.businesslogic.reports.query.node.OperationNode;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QPart;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QTimespanData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery.Quantor;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessUnitQueryType;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.businesslogic.service.QueryService;
import de.iteratec.iteraplan.businesslogic.service.TimeseriesService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;
import de.iteratec.iteraplan.model.attribute.util.TimeseriesHelper;


/**
 *
 */
public class TimeseriesQueryTest extends BaseTransactionalTestSupport {
  private static final List<AbstractPostprocessingStrategy<BusinessUnit>> EMPTY_APS_BU_LIST = Collections.emptyList();
  private NumberAT                                                        numberTsAt;
  private EnumAT                                                          enumTsAt;
  private EnumAV                                                          enumAv1;
  private BusinessUnit                                                    testBUts;
  private BusinessUnit                                                    testBU;

  @Autowired
  private BuildingBlockTypeService                                        buildingBlockTypeService;
  @Autowired
  private TimeseriesService                                               timeseriesService;
  @Autowired
  private TestDataHelper2                                                 testDataHelper;
  @Autowired
  private QueryService                                                    queryService;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    timeseriesService.setTimeseriesEnabled(true);

    // create timeseries number and enum attribute type for BU
    numberTsAt = testDataHelper.createNumberAttributeType("number ts", "number ts", testDataHelper.getDefaultATG());
    numberTsAt.setTimeseries(true);
    enumTsAt = testDataHelper.createEnumAttributeType("enum ts", "enum ts", Boolean.FALSE, testDataHelper.getDefaultATG());
    enumAv1 = createEnumAv("test1", enumTsAt);
    EnumAV enumAv2 = createEnumAv("test2", enumTsAt);
    enumTsAt.setTimeseries(true);

    BuildingBlockType bbt = buildingBlockTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSUNIT);
    bbt.addAttributeTypeTwoWay(numberTsAt);
    bbt.addAttributeTypeTwoWay(enumTsAt);

    testBUts = testDataHelper.createBusinessUnit("testBUts", "BU with timeseries");
    testDataHelper.createTimeseries(testBUts, enumTsAt, createTimeseriesEntry(DateTime.now().minusMonths(1), enumAv2),
        createTimeseriesEntry(DateTime.now(), enumAv1));
    testDataHelper.createTimeseries(testBUts, numberTsAt,
        createTimeseriesEntry(DateTime.now().minusMonths(1), new NumberAV(numberTsAt, BigDecimal.ONE)),
        createTimeseriesEntry(DateTime.now(), new NumberAV(numberTsAt, BigDecimal.TEN)));
    testBU = testDataHelper.createBusinessUnit("testBU", "BU without timeseries");

    commit();
    beginTransaction();
  }

  @Test
  public void testEnumContains() {
    List<BusinessUnit> result = queryService.evaluateQueryTree(createEnumQueryNode(Comparator.LIKE, "*" + enumAv1.getName() + "*"), null,
        EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_CONTAINS_ID, enumAv1.getName()),
        EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService
        .evaluateQueryTree(
            createEmptyQueryNode(),
            createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_CONTAINS_ID,
                enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(0, result.size());
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_CONTAINS_ID,
            enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertTrue(result.contains(testBUts));
  }

  @Test
  public void testEnumContainsNot() {
    List<BusinessUnit> result = queryService.evaluateQueryTree(createEnumQueryNode(Comparator.NOT_LIKE, "*" + enumAv1.getName() + "*"), null,
        EMPTY_APS_BU_LIST);
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result.contains(testBU));
    Assert.assertFalse(result.contains(testBUts));
    result = queryService.evaluateQueryTree(createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_CONTAINSNOT_ID, enumAv1.getName()),
        EMPTY_APS_BU_LIST);
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result.contains(testBU));
    Assert.assertFalse(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_CONTAINSNOT_ID,
            enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(2, result.size());
    Assert.assertFalse(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_CONTAINSNOT_ID,
            enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(3, result.size());
    Assert.assertTrue(result.contains(testBUts));
  }

  @Test
  public void testEnumStartsWith() {
    List<BusinessUnit> result = queryService
        .evaluateQueryTree(createEnumQueryNode(Comparator.LIKE, enumAv1.getName() + "*"), null, EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_STARTSWITH_ID, enumAv1.getName()),
        EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_STARTSWITH_ID,
            enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(0, result.size());
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_STARTSWITH_ID,
            enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertTrue(result.contains(testBUts));
  }

  @Test
  public void testEnumEndsWith() {
    List<BusinessUnit> result = queryService
        .evaluateQueryTree(createEnumQueryNode(Comparator.LIKE, "*" + enumAv1.getName()), null, EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_ENDSWITH_ID, enumAv1.getName()),
        EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService
        .evaluateQueryTree(
            createEmptyQueryNode(),
            createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_ENDSWITH_ID,
                enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(0, result.size());
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_ENDSWITH_ID,
            enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertTrue(result.contains(testBUts));
  }

  @Test
  public void testEnumEquals() {
    List<BusinessUnit> result = queryService.evaluateQueryTree(createEnumQueryNode(Comparator.LIKE, enumAv1.getName()), null, EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_EQUALS_ID, enumAv1.getName()),
        EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_EQUALS_ID, enumAv1.getName()),
        EMPTY_APS_BU_LIST);
    Assert.assertEquals(0, result.size());
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_EQUALS_ID,
            enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertTrue(result.contains(testBUts));
  }

  @Test
  public void testEnumEqualsNot() {
    List<BusinessUnit> result = queryService.evaluateQueryTree(createEnumQueryNode(Comparator.NOT_LIKE, enumAv1.getName()), null, EMPTY_APS_BU_LIST);
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result.contains(testBU));
    Assert.assertFalse(result.contains(testBUts));
    result = queryService.evaluateQueryTree(createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_EQUALSNOT_ID, enumAv1.getName()),
        EMPTY_APS_BU_LIST);
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result.contains(testBU));
    Assert.assertFalse(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_EQUALSNOT_ID,
            enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(2, result.size());
    Assert.assertFalse(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_EQUALSNOT_ID,
            enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(3, result.size());
    Assert.assertTrue(result.contains(testBUts));
  }

  @Test
  public void testEnumNoEntries() {
    List<BusinessUnit> result = queryService.evaluateQueryTree(createEnumQueryNode(Comparator.NO_ASSIGNMENT, enumAv1.getName()), null,
        EMPTY_APS_BU_LIST);
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result.contains(testBU));
    Assert.assertFalse(result.contains(testBUts));
    result = queryService.evaluateQueryTree(createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_NOENTRIES_ID, enumAv1.getName()),
        EMPTY_APS_BU_LIST);
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result.contains(testBU));
    Assert.assertFalse(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_NOENTRIES_ID,
            enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(2, result.size());
    Assert.assertFalse(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_NOENTRIES_ID,
            enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(3, result.size());
    Assert.assertTrue(result.contains(testBUts));
  }

  @Test
  public void testEnumAnyEntries() {
    List<BusinessUnit> result = queryService.evaluateQueryTree(createEnumQueryNode(Comparator.ANY_ASSIGNMENT, enumAv1.getName()), null,
        EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_ANYENTRIES_ID, enumAv1.getName()),
        EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_ANYENTRIES_ID,
            enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(0, result.size());
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createEnumTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_ANYENTRIES_ID,
            enumAv1.getName()), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertTrue(result.contains(testBUts));
  }

  @Test
  public void testEnumInvalidOperator() {
    try {
      queryService.evaluateQueryTree(createEmptyQueryNode(),
          createEnumTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Integer.valueOf(1000), enumAv1.getName()), EMPTY_APS_BU_LIST);
      Assert.fail();
    } catch (IllegalArgumentException e) {
      //expected
    }
  }

  @Test
  public void testNumberGT() {
    List<BusinessUnit> result = queryService.evaluateQueryTree(
        createNumberQueryNode(Comparator.GT, BigDecimalConverter.format(BigDecimal.ONE, Locale.ENGLISH)), null, EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_GT_ID,
            BigDecimalConverter.format(BigDecimal.ONE, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_GT_ID,
            BigDecimalConverter.format(BigDecimal.ZERO, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(0, result.size());
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(1), DateTime.now(), Constants.OPERATION_GT_ID,
            BigDecimalConverter.format(BigDecimal.ZERO, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_GT_ID,
            BigDecimalConverter.format(BigDecimal.ZERO, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
  }

  @Test
  public void testNumberGEQ() {
    List<BusinessUnit> result = queryService.evaluateQueryTree(
        createNumberQueryNode(Comparator.GEQ, BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), null, EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_GEQ_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_GEQ_ID,
            BigDecimalConverter.format(BigDecimal.ONE, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(0, result.size());
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(1), DateTime.now(), Constants.OPERATION_GEQ_ID,
            BigDecimalConverter.format(BigDecimal.ONE, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_GEQ_ID,
            BigDecimalConverter.format(BigDecimal.ONE, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
  }

  @Test
  public void testNumberEQ() {
    List<BusinessUnit> result = queryService.evaluateQueryTree(
        createNumberQueryNode(Comparator.EQ, BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), null, EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_EQ_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_EQ_ID,
            BigDecimalConverter.format(BigDecimal.ONE, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(0, result.size());
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(1), DateTime.now().minusDays(1), Constants.OPERATION_EQ_ID,
            BigDecimalConverter.format(BigDecimal.ONE, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_EQ_ID,
            BigDecimalConverter.format(BigDecimal.ONE, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertTrue(result.contains(testBUts));
  }

  @Test
  public void testNumberLEQ() {
    List<BusinessUnit> result = queryService.evaluateQueryTree(
        createNumberQueryNode(Comparator.LEQ, BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), null, EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_LEQ_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_LEQ_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(0, result.size());
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(1), DateTime.now(), Constants.OPERATION_LEQ_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_LEQ_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
  }

  @Test
  public void testNumberLT() {
    List<BusinessUnit> result = queryService.evaluateQueryTree(
        createNumberQueryNode(Comparator.LT, BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), null, EMPTY_APS_BU_LIST);
    Assert.assertEquals(0, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertFalse(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_LT_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(0, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertFalse(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_LT_ID,
            BigDecimalConverter.format(BigDecimal.TEN.add(BigDecimal.ONE), Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(0, result.size());
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(1), DateTime.now(), Constants.OPERATION_LT_ID,
            BigDecimalConverter.format(BigDecimal.TEN.add(BigDecimal.ONE), Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_LT_ID,
            BigDecimalConverter.format(BigDecimal.TEN.add(BigDecimal.ONE), Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertTrue(result.contains(testBUts));
  }

  @Test
  public void testNumberNoEntries() {
    List<BusinessUnit> result = queryService.evaluateQueryTree(
        createNumberQueryNode(Comparator.NO_ASSIGNMENT, BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), null, EMPTY_APS_BU_LIST);
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result.contains(testBU));
    Assert.assertFalse(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_NOENTRIES_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(2, result.size());
    Assert.assertTrue(result.contains(testBU));
    Assert.assertFalse(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(3), DateTime.now(), Constants.OPERATION_NOENTRIES_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(2, result.size());
    Assert.assertFalse(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(3), DateTime.now().minusMonths(2), Constants.OPERATION_NOENTRIES_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(3, result.size());
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_NOENTRIES_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(3, result.size());
  }

  @Test
  public void testNumberAnyEntries() {
    List<BusinessUnit> result = queryService.evaluateQueryTree(
        createNumberQueryNode(Comparator.ANY_ASSIGNMENT, BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), null, EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Constants.OPERATION_ANYENTRIES_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertFalse(result.contains(testBU));
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_ANYENTRIES_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(0, result.size());
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now().minusMonths(1), DateTime.now(), Constants.OPERATION_ANYENTRIES_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertTrue(result.contains(testBUts));
    result = queryService.evaluateQueryTree(
        createEmptyQueryNode(),
        createNumberTimeseriesQueryToday(Quantor.EXISTS, DateTime.now().minusMonths(2), DateTime.now(), Constants.OPERATION_ANYENTRIES_ID,
            BigDecimalConverter.format(BigDecimal.TEN, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
    Assert.assertEquals(1, result.size());
    Assert.assertTrue(result.contains(testBUts));
  }

  @Test
  public void testNumberInvalidOperator() {
    try {
      queryService.evaluateQueryTree(
          createEmptyQueryNode(),
          createNumberTimeseriesQueryToday(Quantor.ALL, DateTime.now(), DateTime.now(), Integer.valueOf(1000),
              BigDecimalConverter.format(BigDecimal.ZERO, Locale.ENGLISH)), EMPTY_APS_BU_LIST);
      Assert.fail();
    } catch (IllegalArgumentException e) {
      //expected
    }
  }

  private Node createEmptyQueryNode() {
    OperationNode result = new OperationNode(Operation.AND);
    result.addChild(new AssociatedLeafNode(BusinessUnitQueryType.getInstance(), null));
    return result;
  }

  private Node createEnumQueryNode(Comparator comparator, String pattern) {
    OperationNode result = new OperationNode(Operation.AND);
    result.addChild(EnumAttributeLeafNode.createNode(BusinessUnitQueryType.getInstance(), null, enumTsAt.getId().intValue(), comparator, pattern));
    return result;
  }

  private Node createNumberQueryNode(Comparator comparator, String pattern) {
    OperationNode result = new OperationNode(Operation.AND);
    result.addChild(NumberAttributeLeafNode.createNode(BusinessUnitQueryType.getInstance(), null, numberTsAt.getId().intValue(), comparator, pattern,
        Locale.ENGLISH));
    return result;
  }

  private TimeseriesQuery createEnumTimeseriesQueryToday(Quantor quantor, DateTime start, DateTime end, Integer operationId, String criteria) {
    BBAttribute bbAttribute = new BBAttribute(enumTsAt.getId(), BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, enumTsAt.getName(), enumTsAt.getName());
    return createTimeseriesQuery(bbAttribute, quantor, start, end, operationId, criteria);
  }

  private TimeseriesQuery createNumberTimeseriesQueryToday(Quantor quantor, DateTime start, DateTime end, Integer operationId, String criteria) {
    BBAttribute bbAttribute = new BBAttribute(numberTsAt.getId(), BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE, numberTsAt.getName(),
        numberTsAt.getName());
    return createTimeseriesQuery(bbAttribute, quantor, start, end, operationId, criteria);
  }

  private TimeseriesQuery createTimeseriesQuery(BBAttribute bbAttribute, Quantor quantor, DateTime start, DateTime end, Integer operationId,
                                                String criteria) {
    TimeseriesQuery result = new TimeseriesQuery(Lists.newArrayList(bbAttribute));
    QPart qPart = new QPart();
    qPart.setChosenAttributeStringId(bbAttribute.getStringId());
    qPart.setChosenOperationId(operationId);
    qPart.setExistingCriteria(criteria);
    qPart.setFreeTextCriteriaSelected(Boolean.FALSE);
    result.setPart(qPart);
    result.setQuantor(quantor);
    QTimespanData timespan = new QTimespanData(Locale.ENGLISH);
    timespan.setStartDateAsString(start.toString(MessageAccess.getString("calendar.dateFormat", Locale.ENGLISH), Locale.ENGLISH));
    timespan.setEndDateAsString(end.toString(MessageAccess.getString("calendar.dateFormat", Locale.ENGLISH), Locale.ENGLISH));
    result.setTimespan(timespan);
    return result;
  }

  private EnumAV createEnumAv(String name, EnumAT type) {
    EnumAV result = new EnumAV();

    result.setName(name);
    type.addAttribueValueTwoWay(result);

    return result;
  }

  private TimeseriesEntry createTimeseriesEntry(DateTime date, EnumAV value) {
    return new TimeseriesEntry(date.toDate(), TimeseriesHelper.AV_TO_NAME_FUNCTION.apply(value));
  }

  private TimeseriesEntry createTimeseriesEntry(DateTime date, NumberAV value) {
    return new TimeseriesEntry(date.toDate(), BigDecimalConverter.format(value.getValue(), Locale.ENGLISH));
  }
}

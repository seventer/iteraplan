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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.Timeseries;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;


public class TimeseriesServiceTest extends BaseTransactionalTestSupport {
  private static final String             NUM_AT_NAME = "Test Number";
  private static final String             BD_NAME     = "Test BD";
  private static final String             TEST_DESC   = "test desc";

  @Autowired
  private TimeseriesService               timeseriesService;
  @Autowired
  private TestDataHelper2                 testDataHelper;
  @Autowired
  private AttributeValueService           avService;
  @Autowired
  private InfrastructureElementService    ieService;
  @Autowired
  private InformationSystemReleaseService isrService;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    timeseriesService.setTimeseriesEnabled(true);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.TimeseriesService#loadObjectById(java.lang.Integer)}.
   */
  @Test
  public void testLoadObjectById() {
    BusinessDomain bd = testDataHelper.createBusinessDomain(BD_NAME, TEST_DESC);
    NumberAT numAt = testDataHelper.createNumberAttributeType(NUM_AT_NAME, TEST_DESC, testDataHelper.getDefaultATG());
    numAt.setTimeseries(true);
    commit();
    Timeseries testSeries = createTestTimeseries(bd, numAt);
    commit();

    assertEquals(testSeries, timeseriesService.loadObjectById(testSeries.getId()));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.TimeseriesService#loadTimeseriesByBuildingBlockAndAttributeType(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.attribute.AttributeType)}.
   */
  @Test
  public void testLoadTimeseriesByBuildingBlockAndAttributeType() {
    BusinessDomain bd = testDataHelper.createBusinessDomain(BD_NAME, TEST_DESC);
    NumberAT numAt = testDataHelper.createNumberAttributeType(NUM_AT_NAME, TEST_DESC, testDataHelper.getDefaultATG());
    numAt.setTimeseries(true);
    commit();
    Timeseries testSeries = createTestTimeseries(bd, numAt);
    commit();

    assertEquals(testSeries, timeseriesService.loadTimeseriesByBuildingBlockAndAttributeType(bd, numAt));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.TimeseriesService#saveOrUpdateWithBbUpdate(de.iteratec.iteraplan.model.attribute.Timeseries)}.
   * Among other things also tests whether the saving of a timeseries writes the current attribute value
   * to the according building block's attribute value assignments.
   */
  @Test
  public void testSaveOrUpdateWithBbUpdate() {
    BusinessDomain bd = testDataHelper.createBusinessDomain(BD_NAME, TEST_DESC);
    AttributeTypeGroup atg = testDataHelper.getDefaultATG();
    EnumAT enumAt = testDataHelper.createEnumAttributeType("Test Enum", TEST_DESC, Boolean.FALSE, atg);
    enumAt.setTimeseries(true);
    testDataHelper.createEnumAV("1", TEST_DESC, enumAt);
    EnumAV enumAv = testDataHelper.createEnumAV("2", TEST_DESC, enumAt);
    NumberAT numAt = testDataHelper.createNumberAttributeType(NUM_AT_NAME, TEST_DESC, atg);
    numAt.setTimeseries(true);
    commit();

    beginTransaction();
    createTestTimeseries(bd, numAt); // saveOrUpdate executed here
    createTestTimeseries(bd, enumAt); // and here
    commit();

    // assertions
    beginTransaction();
    HashBucketMap<Integer, AttributeValue> numAvMap = avService.getBuildingBlockIdsToConnectedAttributeValues(Lists.newArrayList(bd.getId()),
        numAt.getId());
    List<AttributeValue> actualNumAvList = numAvMap.get(bd.getId());
    assertEquals(1, actualNumAvList.size());
    assertEquals(BigDecimal.valueOf(2.0).setScale(2), actualNumAvList.get(0).getValue());

    HashBucketMap<Integer, AttributeValue> enumAvMap = avService.getBuildingBlockIdsToConnectedAttributeValues(Lists.newArrayList(bd.getId()),
        enumAt.getId());
    List<AttributeValue> actualEnumAvList = enumAvMap.get(bd.getId());
    assertEquals(1, actualEnumAvList.size());
    assertEquals(enumAv, actualEnumAvList.get(0));
  }

  @Test
  public void testSaveOrUpdateWithoutBbUpdate() {
    InfrastructureElement ie = testDataHelper.createInfrastructureElement("Test IE", TEST_DESC);
    NumberAT numAt = testDataHelper.createNumberAttributeType(NUM_AT_NAME, TEST_DESC, testDataHelper.getDefaultATG());
    numAt.setTimeseries(true);
    ie.getBuildingBlockType().addAttributeTypeTwoWay(numAt);
    commit();

    List<TimeseriesEntry> entries = Lists.newArrayList();
    entries.add(new TimeseriesEntry(new LocalDate(2012, 1, 1).toDate(), "1"));
    entries.add(new TimeseriesEntry(new LocalDate(2012, 10, 1).toDate(), "2"));
    entries.add(new TimeseriesEntry(new LocalDate(2012, 5, 11).toDate(), "3"));
    Timeseries timeseries = new Timeseries();
    timeseries.setAttribute(numAt);
    timeseries.setBuildingBlock(ie);
    timeseries.addEntries(entries);

    beginTransaction();
    timeseriesService.saveOrUpdateWithoutBbUpdate(timeseries);
    commit();

    beginTransaction();
    InfrastructureElement reloadedIE = ieService.loadObjectById(ie.getId());
    assertTrue(reloadedIE.getAttributeValueAssignments().isEmpty());

    commit();
  }

  @Test(expected = IteraplanBusinessException.class)
  public void testSaveOrUpdateValidationFailed() {
    BusinessDomain bd = testDataHelper.createBusinessDomain(BD_NAME, TEST_DESC);
    NumberAT numAt = testDataHelper.createNumberAttributeType(NUM_AT_NAME, TEST_DESC, testDataHelper.getDefaultATG());
    numAt.setTimeseries(true);
    commit();

    beginTransaction();
    Timeseries testTimeseries = createTestTimeseries(bd, numAt);
    testTimeseries.addEntry(new TimeseriesEntry(new LocalDate(2052, 1, 1).toDate(), "4"));
    timeseriesService.saveOrUpdateWithoutBbUpdate(testTimeseries);
    commit();
  }

  @Test
  public void testAutomaticDeletionOfTimeseriesCaseHierarchicalEntity() {
    InfrastructureElement ie1 = testDataHelper.createInfrastructureElement("IE 1", "IE 1 desc");
    InfrastructureElement ie1Child = testDataHelper.createInfrastructureElement("IE 1 child", "IE 1 child desc");
    ie1Child.addParent(ie1);

    testAutomaticDeletionOfTimeseries(ie1, ie1Child, ieService);
  }

  @Test
  public void testAutomaticDeletionOfTimeseriesCaseISR() {
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(testDataHelper.createInformationSystem("IS 1"), "test");
    InformationSystemRelease isr1Child = testDataHelper.createInformationSystemRelease(testDataHelper.createInformationSystem("IS 1 child"), "test");
    isr1Child.addParent(isr1);

    testAutomaticDeletionOfTimeseries(isr1, isr1Child, isrService);
  }

  private <B extends BuildingBlock> void testAutomaticDeletionOfTimeseries(B bb, B bbChild, BuildingBlockService<B, Integer> service) {
    NumberAT numAt = testDataHelper.createNumberAttributeType(NUM_AT_NAME, TEST_DESC, testDataHelper.getDefaultATG());
    numAt.setTimeseries(true);

    Timeseries testSeries1 = createTestTimeseries(bb, numAt);
    Timeseries testSeries2 = createTestTimeseries(bbChild, numAt);
    commit();

    beginTransaction();
    service.deleteEntity(bb); // should delete bb and its child, bbChild, as well as the related timeseries
    commit();

    // assertions
    beginTransaction();
    assertNull(timeseriesService.loadObjectByIdIfExists(testSeries1.getId()));
    assertNull(timeseriesService.loadObjectByIdIfExists(testSeries2.getId()));
    commit();
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.TimeseriesServiceImpl#deleteTimeseriesByAttributeType(de.iteratec.iteraplan.model.attribute.AttributeType)}.
   */
  @Test
  public void testDeleteTimeseriesByAttributeType() {
    BusinessDomain bd = testDataHelper.createBusinessDomain(BD_NAME, TEST_DESC);
    AttributeTypeGroup atg = testDataHelper.getDefaultATG();
    EnumAT enumAt = testDataHelper.createEnumAttributeType("Test Enum", TEST_DESC, Boolean.FALSE, atg);
    enumAt.setTimeseries(true);
    testDataHelper.createEnumAV("1", TEST_DESC, enumAt);
    testDataHelper.createEnumAV("2", TEST_DESC, enumAt);
    NumberAT numAt = testDataHelper.createNumberAttributeType(NUM_AT_NAME, TEST_DESC, atg);
    numAt.setTimeseries(true);
    commit();

    Timeseries testSeries = createTestTimeseries(bd, enumAt);
    Timeseries testSeries2 = createTestTimeseries(bd, numAt);
    commit();

    beginTransaction();
    Integer numberOfDeleted = timeseriesService.deleteTimeseriesByAttributeType(enumAt);
    assertEquals(1, numberOfDeleted.intValue());
    commit();

    try {
      Timeseries loadedSeries = timeseriesService.loadObjectById(testSeries.getId());
      fail("Timeseries should have been deleted, but was " + loadedSeries);
    } catch (IteraplanTechnicalException e) {
      assertEquals(IteraplanErrorMessages.ENTITY_NOT_FOUND_READ, e.getErrorCode());
      // expected result, everything fine here
    }
    try {
      Timeseries loadedSeries = timeseriesService.loadObjectById(testSeries2.getId());
      assertEquals(testSeries2, loadedSeries);
    } catch (IteraplanTechnicalException e) {
      fail("Timeseries should not have been deleted.\n" + e);
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.TimeseriesServiceImpl#deleteTimeseriesByBuildingBlock(de.iteratec.iteraplan.model.BuildingBlock)}.
   */
  @Test
  public void testDeleteTimeseriesByBuildingBlocks() {
    BusinessDomain bd1 = testDataHelper.createBusinessDomain(BD_NAME + "1", TEST_DESC);
    BusinessDomain bd2 = testDataHelper.createBusinessDomain(BD_NAME + "2", TEST_DESC);
    AttributeTypeGroup atg = testDataHelper.getDefaultATG();
    NumberAT numAt = testDataHelper.createNumberAttributeType(NUM_AT_NAME, TEST_DESC, atg);
    numAt.setTimeseries(true);
    commit();

    Timeseries testSeries = createTestTimeseries(bd1, numAt);
    Timeseries testSeries2 = createTestTimeseries(bd2, numAt);
    commit();

    beginTransaction();
    Integer numberOfDeleted = timeseriesService.deleteTimeseriesByBuildingBlocks(Collections.singleton(bd1));
    assertEquals(1, numberOfDeleted.intValue());
    commit();

    try {
      Timeseries loadedSeries = timeseriesService.loadObjectById(testSeries.getId());
      fail("Timeseries should have been deleted, but was " + loadedSeries);
    } catch (IteraplanTechnicalException e) {
      assertEquals(IteraplanErrorMessages.ENTITY_NOT_FOUND_READ, e.getErrorCode());
      // expected result, everything fine here
    }
    try {
      Timeseries loadedSeries = timeseriesService.loadObjectById(testSeries2.getId());
      assertEquals(testSeries2, loadedSeries);
    } catch (IteraplanTechnicalException e) {
      fail("Timeseries should not have been deleted.\n" + e);
    }
  }

  private Timeseries createTestTimeseries(BuildingBlock bb, AttributeType at) {
    List<TimeseriesEntry> entries = Lists.newArrayList();
    entries.add(new TimeseriesEntry(new LocalDate(2012, 1, 1).toDate(), "1"));
    entries.add(new TimeseriesEntry(new LocalDate(2012, 10, 1).toDate(), "2"));
    entries.add(new TimeseriesEntry(new LocalDate(2012, 5, 11).toDate(), "3"));
    return testDataHelper.createTimeseries(bb, at, entries.toArray(new TimeseriesEntry[3]));
  }
}

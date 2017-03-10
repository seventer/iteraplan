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
package de.iteratec.iteraplan.model.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesSerializer;


public class TimeseriesTest {

  @Test
  public void testTimeseriesCreation() {
    BusinessDomain bd = new BusinessDomain();
    bd.setId(Integer.valueOf(1));

    EnumAT enumAt = new EnumAT();
    enumAt.setId(Integer.valueOf(10));
    enumAt.setTimeseries(true);

    NumberAT numAt = new NumberAT();
    numAt.setId(Integer.valueOf(11));

    TextAT textAt = new TextAT();
    textAt.setId(Integer.valueOf(12));

    Timeseries testSeries = createTimeseries(bd, enumAt);
    assertEquals(bd, testSeries.getBuildingBlock());
    assertEquals(enumAt, testSeries.getAttribute());

    try {
      createTimeseries(bd, numAt);
      fail("It shouldn't be possible to create a timeseries with an attribute without timeseries flag set.");
    } catch (IteraplanBusinessException e) {
      assertEquals(IteraplanErrorMessages.NO_TIMESERIES_ATTRIBUTE, e.getErrorCode());
      // expected result, everything fine
    }

    try {
      createTimeseries(bd, textAt);
      fail("It shouldn't be possible to create a timeseries with a non-timeseries attribute type like " + textAt);
    } catch (IteraplanBusinessException e) {
      assertEquals(IteraplanErrorMessages.NO_TIMESERIES_ATTRIBUTE, e.getErrorCode());
      // expected result, everything fine
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesSerializer#serialize(java.util.List)}.
   */
  @Test
  public void testSerialize() {
    List<TimeseriesEntry> testList = getTestList();

    assertEquals(getUnsortedSerialization(), TimeseriesSerializer.serialize(testList));

    Collections.sort(testList);
    assertEquals(getSortedSerialization(), TimeseriesSerializer.serialize(testList));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesSerializer#deserialize(java.lang.String)}.
   */
  @Test
  public void testDeserialize() {
    List<TimeseriesEntry> testList = getTestList();

    assertListContentEquality(testList, TimeseriesSerializer.deserialize(getUnsortedSerialization()));

    Collections.sort(testList);
    assertListContentEquality(testList, TimeseriesSerializer.deserialize(getSortedSerialization()));
  }

  @SuppressWarnings("PMD.ExcessiveMethodLength")
  @Test
  public void testTrim() {
    List<TimeseriesEntry> testList = getTestList();

    BusinessDomain bd = new BusinessDomain();
    bd.setId(Integer.valueOf(1));

    EnumAT enumAt = new EnumAT();
    enumAt.setId(Integer.valueOf(10));
    enumAt.setTimeseries(true);

    Timeseries testSeries = createTimeseries(bd, enumAt);
    testSeries.addEntries(testList);

    Collection<String> trimmed = Timeseries.getValuesBetweenTimespan(null, null, null);
    Assert.assertEquals(1, trimmed.size());
    Assert.assertTrue(trimmed.contains(null));
    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2012, 9, 29, 0, 0).toDate(), null, null);
    Assert.assertEquals(1, trimmed.size());
    Assert.assertTrue(trimmed.contains(null));
    trimmed = Timeseries.getValuesBetweenTimespan(null, new LocalDateTime(2012, 9, 29, 0, 0).toDate(), null);
    Assert.assertEquals(1, trimmed.size());
    Assert.assertTrue(trimmed.contains(null));

    trimmed = Timeseries.getValuesBetweenTimespan(null, new LocalDateTime(2012, 9, 30, 0, 0).toDate(), testSeries);
    Assert.assertEquals(1, trimmed.size());
    Assert.assertTrue(trimmed.contains(null));

    trimmed = Timeseries.getValuesBetweenTimespan(null, new LocalDateTime(2012, 10, 1, 0, 0).toDate(), testSeries);
    Assert.assertEquals(2, trimmed.size());
    Assert.assertTrue(trimmed.contains(null));
    Assert.assertTrue(trimmed.contains("value B"));

    trimmed = Timeseries.getValuesBetweenTimespan(null, new LocalDateTime(2013, 2, 10, 0, 0).toDate(), testSeries);
    Assert.assertEquals(4, trimmed.size());
    Assert.assertTrue(trimmed.contains(null));
    Assert.assertTrue(trimmed.contains("value B"));
    Assert.assertTrue(trimmed.contains("value A"));
    Assert.assertTrue(trimmed.contains("value C"));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2012, 9, 30, 0, 0).toDate(), null, testSeries);
    Assert.assertEquals(4, trimmed.size());
    Assert.assertTrue(trimmed.contains(null));
    Assert.assertTrue(trimmed.contains("value B"));
    Assert.assertTrue(trimmed.contains("value A"));
    Assert.assertTrue(trimmed.contains("value C"));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2012, 10, 1, 0, 0).toDate(), null, testSeries);
    Assert.assertEquals(3, trimmed.size());
    Assert.assertTrue(trimmed.contains("value B"));
    Assert.assertTrue(trimmed.contains("value A"));
    Assert.assertTrue(trimmed.contains("value C"));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2013, 2, 10, 0, 0).toDate(), null, testSeries);
    Assert.assertEquals(1, trimmed.size());
    Assert.assertTrue(trimmed.contains("value C"));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2012, 9, 29, 0, 0).toDate(), new LocalDateTime(2012, 9, 30, 0, 0).toDate(), null);
    Assert.assertEquals(1, trimmed.size());
    Assert.assertTrue(trimmed.contains(null));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2012, 9, 29, 0, 0).toDate(), new LocalDateTime(2012, 9, 30, 0, 0).toDate(),
        testSeries);
    Assert.assertEquals(1, trimmed.size());
    Assert.assertTrue(trimmed.contains(null));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2012, 9, 29, 0, 0).toDate(), new LocalDateTime(2012, 10, 1, 0, 0).toDate(),
        testSeries);
    Assert.assertEquals(2, trimmed.size());
    Assert.assertTrue(trimmed.contains(null));
    Assert.assertTrue(trimmed.contains("value B"));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2012, 9, 29, 0, 0).toDate(), new LocalDateTime(2012, 10, 2, 0, 0).toDate(),
        testSeries);
    Assert.assertEquals(2, trimmed.size());
    Assert.assertTrue(trimmed.contains(null));
    Assert.assertTrue(trimmed.contains("value B"));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2012, 10, 1, 0, 0).toDate(), new LocalDateTime(2012, 10, 2, 0, 0).toDate(),
        testSeries);
    Assert.assertEquals(1, trimmed.size());
    Assert.assertTrue(trimmed.contains("value B"));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2012, 10, 2, 0, 0).toDate(), new LocalDateTime(2012, 10, 3, 0, 0).toDate(),
        testSeries);
    Assert.assertEquals(1, trimmed.size());
    Assert.assertTrue(trimmed.contains("value B"));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2012, 10, 1, 0, 0).toDate(), new LocalDateTime(2013, 1, 11, 0, 0).toDate(),
        testSeries);
    Assert.assertEquals(1, trimmed.size());
    Assert.assertTrue(trimmed.contains("value B"));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2012, 10, 1, 0, 0).toDate(), new LocalDateTime(2013, 1, 12, 0, 0).toDate(),
        testSeries);
    Assert.assertEquals(2, trimmed.size());
    Assert.assertTrue(trimmed.contains("value B"));
    Assert.assertTrue(trimmed.contains("value A"));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2012, 10, 1, 0, 0).toDate(), new LocalDateTime(2013, 1, 13, 0, 0).toDate(),
        testSeries);
    Assert.assertEquals(2, trimmed.size());
    Assert.assertTrue(trimmed.contains("value B"));
    Assert.assertTrue(trimmed.contains("value A"));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2013, 2, 9, 0, 0).toDate(), new LocalDateTime(2013, 2, 11, 0, 0).toDate(),
        testSeries);
    Assert.assertEquals(2, trimmed.size());
    Assert.assertTrue(trimmed.contains("value A"));
    Assert.assertTrue(trimmed.contains("value C"));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2013, 2, 10, 0, 0).toDate(), new LocalDateTime(2013, 2, 11, 0, 0).toDate(),
        testSeries);
    Assert.assertEquals(1, trimmed.size());
    Assert.assertTrue(trimmed.contains("value C"));

    trimmed = Timeseries.getValuesBetweenTimespan(new LocalDateTime(2013, 2, 11, 0, 0).toDate(), new LocalDateTime(2013, 2, 12, 0, 0).toDate(),
        testSeries);
    Assert.assertEquals(1, trimmed.size());
    Assert.assertTrue(trimmed.contains("value C"));
  }

  private Timeseries createTimeseries(BuildingBlock bb, AttributeType at) {
    Timeseries testSeries = new Timeseries();
    testSeries.setBuildingBlock(bb);
    testSeries.setAttribute(at);
    return testSeries;
  }

  private void assertListContentEquality(List<TimeseriesEntry> expected, List<TimeseriesEntry> actual) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
      TimeseriesEntry expectedEntry = expected.get(i);
      TimeseriesEntry actualEntry = actual.get(i);
      assertEquals(expectedEntry.getDate(), actualEntry.getDate());
      assertEquals(expectedEntry.getValue(), actualEntry.getValue());
    }
  }

  private List<TimeseriesEntry> getTestList() {
    List<TimeseriesEntry> testList = Lists.newArrayList();
    testList.add(new TimeseriesEntry(new LocalDateTime(2013, 1, 12, 0, 0).toDate(), "value A"));
    testList.add(new TimeseriesEntry(new LocalDateTime(2012, 10, 1, 0, 0).toDate(), "value B"));
    testList.add(new TimeseriesEntry(new LocalDateTime(2013, 2, 10, 0, 0).toDate(), "value C"));
    return testList;
  }

  private String getUnsortedSerialization() {
    return "[{\"date\":\"2013-01-12\",\"value\":\"value A\"},{\"date\":\"2012-10-01\",\"value\":\"value B\"},{\"date\":\"2013-02-10\",\"value\":\"value C\"}]";
  }

  private String getSortedSerialization() {
    return "[{\"date\":\"2012-10-01\",\"value\":\"value B\"},{\"date\":\"2013-01-12\",\"value\":\"value A\"},{\"date\":\"2013-02-10\",\"value\":\"value C\"}]";
  }
}

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
package de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus;


public class QStatusDataTechnicalComponentReleaseTest {

  /**
   * Test method for the initial {@link TypeOfStatus}-mapping for
   * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease TechnicalComponentRelease}s
   */
  @Test
  public void testStatusMapInitValue() {
    QStatusDataTechnicalComponentRelease qStatusDataTcr = new QStatusDataTechnicalComponentRelease();

    boolean expected[] = { true, false, false, false, false };
    Map<TypeOfStatus, Boolean> expectedMap = getExpectedMapping(expected);
    Map<TypeOfStatus, Boolean> actualMap = qStatusDataTcr.getStatusMap();

    assertEquals(expectedMap, actualMap);
  }

  /**
   * Test method (getter and setter)
   * {@link QStatusDataTechnicalComponentRelease#getSelectedStatus()}
   * {@link QStatusDataTechnicalComponentRelease#setSelectedStatus(List)}
   */
  @Test
  public void testSelectedStatus() {
    QStatusDataTechnicalComponentRelease qStatusDataTcr = new QStatusDataTechnicalComponentRelease();

    List<String> statusList = Lists.newArrayList();
    statusList.add("typeOfStatus_planned");
    statusList.add("typeOfStatus_inactive");
    statusList.add("typeOfStatus_undefined");
    qStatusDataTcr.setSelectedStatus(statusList);

    boolean expected[] = { false, true, false, true, true };
    Map<TypeOfStatus, Boolean> expectedMap = getExpectedMapping(expected);
    Map<TypeOfStatus, Boolean> actualMap = qStatusDataTcr.getStatusMap();

    assertEquals(expectedMap, actualMap);

    List<String> actualSelectedStatusList = qStatusDataTcr.getSelectedStatus();

    assertEquals(statusList, actualSelectedStatusList);
  }

  private Map<TypeOfStatus, Boolean> getExpectedMapping(boolean[] mapping) {
    Map<TypeOfStatus, Boolean> expected = Maps.newHashMap();
    expected.put(TypeOfStatus.CURRENT, Boolean.valueOf(mapping[0]));
    expected.put(TypeOfStatus.PLANNED, Boolean.valueOf(mapping[1]));
    expected.put(TypeOfStatus.TARGET, Boolean.valueOf(mapping[2]));
    expected.put(TypeOfStatus.INACTIVE, Boolean.valueOf(mapping[3]));
    expected.put(TypeOfStatus.UNDEFINED, Boolean.valueOf(mapping[4]));
    return expected;
  }

}

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
package de.iteratec.iteraplan.presentation.dialog.InformationSystem;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.presentation.dialog.JsonApiTestHelper;


/**
 *  this class is responsible for testing the BusinessMappingController. It ensures that the JSON/REST API is working properly.
 */
public class BusinessMappingControllerTest {

  private Map<Integer, String>        expectedNamesBU = Maps.newHashMap();
  private Map<Integer, String>        expectedNamesIS = Maps.newHashMap();

  private Map<Integer, Integer>       expectedIdsBU   = Maps.newHashMap();
  private Map<Integer, Integer>       expectedIdsIS   = Maps.newHashMap();

  private Map<Integer, String>        expectedURIsBU  = Maps.newHashMap();
  private Map<Integer, String>        expectedURIsIS  = Maps.newHashMap();

  private Map<Integer, List<Integer>> expectedIds     = Maps.newHashMap();

  private BusinessMappingController   controller;

  private JsonApiTestHelper           testHelper;

  @Before
  public void init() {
    testHelper = new JsonApiTestHelper(false);
    controller = testHelper.getBusinessMappingController();
  }

  @After
  public void cleanup() {
    testHelper.cleanup();
  }

  @Test
  public void testProjectOnISToBU() {

    initExpectedData();

    MockHttpServletRequest req = new MockHttpServletRequest();
    MockHttpServletResponse res = new MockHttpServletResponse();
    try {
      controller.projectOnBUToIS(req, res);
    } catch (IOException ex) {
      //ignore
    }
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> responseContent = testHelper.readJson(res, List.class);

    Collections.sort(responseContent, new Comparator<Map<String, Object>>() {
      //compare on value of ids of InformationSystem (and, if IS is equal, BusinessUnit)
      public int compare(Map<String, Object> o1, Map<String, Object> o2) {
        Integer isId1 = Preconditions.checkNotNull((Integer) o1.get("informationSystemId"));
        Integer isId2 = Preconditions.checkNotNull((Integer) o2.get("informationSystemId"));

        int compare = isId1.compareTo(isId2);
        if (compare != 0) {
          return compare;
        }

        Integer buId1 = Preconditions.checkNotNull((Integer) o1.get("businessUnitId"));
        Integer buId2 = Preconditions.checkNotNull((Integer) o2.get("businessUnitId"));
        return buId1.compareTo(buId2);
      }

    });

    Assert.assertEquals(3, responseContent.size());

    validateItemContent(responseContent.get(0), Integer.valueOf(0));
    validateItemContent(responseContent.get(1), Integer.valueOf(1));
    validateItemContent(responseContent.get(2), Integer.valueOf(2));

  }

  private void initExpectedData() {
    expectedIds.clear();
    expectedIds.put(Integer.valueOf(0), Lists.newArrayList(Integer.valueOf(100), Integer.valueOf(101)));
    expectedIds.put(Integer.valueOf(1), Lists.newArrayList(Integer.valueOf(102)));
    expectedIds.put(Integer.valueOf(2), Lists.newArrayList(Integer.valueOf(103)));

    expectedNamesBU.clear();
    expectedNamesBU.put(Integer.valueOf(0), testHelper.getBusinessUnitName(0));
    expectedNamesBU.put(Integer.valueOf(1), testHelper.getBusinessUnitName(1));
    expectedNamesBU.put(Integer.valueOf(2), testHelper.getBusinessUnitName(1));

    expectedNamesIS.clear();
    expectedNamesIS.put(Integer.valueOf(0), testHelper.getInformationSystemNameWithVersion(0));
    expectedNamesIS.put(Integer.valueOf(1), testHelper.getInformationSystemNameWithVersion(0));
    expectedNamesIS.put(Integer.valueOf(2), testHelper.getInformationSystemNameWithVersion(1));

    expectedIdsBU.clear();
    expectedIdsBU.put(Integer.valueOf(0), testHelper.getBusinessUnitId(0));
    expectedIdsBU.put(Integer.valueOf(1), testHelper.getBusinessUnitId(1));
    expectedIdsBU.put(Integer.valueOf(2), testHelper.getBusinessUnitId(1));

    expectedIdsIS.clear();
    expectedIdsIS.put(Integer.valueOf(0), testHelper.getInformationSystemReleaseId(0));
    expectedIdsIS.put(Integer.valueOf(1), testHelper.getInformationSystemReleaseId(0));
    expectedIdsIS.put(Integer.valueOf(2), testHelper.getInformationSystemReleaseId(1));

    expectedURIsBU.clear();
    expectedURIsBU.put(Integer.valueOf(0), "/businessunit/json/" + testHelper.getBusinessUnitId(0));
    expectedURIsBU.put(Integer.valueOf(1), "/businessunit/json/" + testHelper.getBusinessUnitId(1));
    expectedURIsBU.put(Integer.valueOf(2), "/businessunit/json/" + testHelper.getBusinessUnitId(1));

    expectedURIsIS.clear();
    expectedURIsIS.put(Integer.valueOf(0), "/informationsystem/json/" + testHelper.getInformationSystemReleaseId(0));
    expectedURIsIS.put(Integer.valueOf(1), "/informationsystem/json/" + testHelper.getInformationSystemReleaseId(0));
    expectedURIsIS.put(Integer.valueOf(2), "/informationsystem/json/" + testHelper.getInformationSystemReleaseId(1));
  }

  private void validateItemContent(Map<String, Object> item, Integer number) {
    Assert.assertTrue(item.containsKey("businessMappingIds"));
    @SuppressWarnings("unchecked")
    List<Integer> actual = (List<Integer>) item.get("businessMappingIds");
    List<Integer> expected = expectedIds.get(number);
    Assert.assertEquals(expected, actual);

    Assert.assertTrue(item.containsKey("businessUnit"));
    String buNameExpected = expectedNamesBU.get(number);
    String buNameActual = (String) item.get("businessUnit");
    Assert.assertTrue(buNameActual.endsWith(buNameExpected));

    Assert.assertTrue(item.containsKey("informationSystem"));
    String isNameExpected = expectedNamesIS.get(number);
    String isNameActual = (String) item.get("informationSystem");
    Assert.assertTrue(isNameActual.endsWith(isNameExpected));

    Assert.assertTrue(item.containsKey("businessUnitId"));
    Assert.assertEquals(expectedIdsBU.get(number), item.get("businessUnitId"));

    Assert.assertTrue(item.containsKey("informationSystemURI"));
    Assert.assertTrue(((String) item.get("informationSystemURI")).endsWith(expectedURIsIS.get(number)));

    Assert.assertTrue(item.containsKey("informationSystemId"));
    Assert.assertEquals(expectedIdsIS.get(number), item.get("informationSystemId"));

    Assert.assertTrue(item.containsKey("businessUnitURI"));
    Assert.assertTrue(((String) item.get("businessUnitURI")).endsWith(expectedURIsBU.get(number)));

    Assert.assertTrue(item.keySet().size() == 7);

    //    Assert.assertEquals(expected, actual);
  }
}

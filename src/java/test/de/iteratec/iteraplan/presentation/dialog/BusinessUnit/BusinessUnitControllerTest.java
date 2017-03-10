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
package de.iteratec.iteraplan.presentation.dialog.BusinessUnit;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import de.iteratec.iteraplan.presentation.dialog.JsonApiTestHelper;


/**
 *  This class is responsible for testing the BusinessUnitController. It ensures that the JSON/REST API works properly.
 */
public class BusinessUnitControllerTest {
  private BusinessUnitController controller;
  private JsonApiTestHelper      testHelper;

  @After
  public void cleanup() {
    testHelper.cleanup();
  }

  @Test
  public void testList() {
    testHelper = new JsonApiTestHelper(false);
    controller = testHelper.getBusinessUnitController();

    MockHttpServletRequest req = new MockHttpServletRequest();
    MockHttpServletResponse res = new MockHttpServletResponse();
    try {
      controller.list(false, req, res);
    } catch (IOException ex) {
      //ignore
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> responseContent = testHelper.readJson(res, Map.class);

    Assert.assertTrue(responseContent.containsKey("items"));
    Assert.assertTrue(responseContent.containsKey("label"));
    Assert.assertTrue(responseContent.containsKey("identifier"));

    Assert.assertTrue(responseContent.keySet().size() == 3);

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> items = (List<Map<String, Object>>) responseContent.get("items");

    Collections.sort(items, new Comparator<Map<String, Object>>() {

      public int compare(Map<String, Object> o1, Map<String, Object> o2) {
        Integer id1 = Integer.valueOf((String) o1.get("id"));
        Integer id2 = Integer.valueOf((String) o2.get("id"));

        if (id1 == null && id2 == null) {
          return 0;
        }
        if (id1 == null) {
          return -1;
        }
        if (id2 == null) {
          return 1;
        }

        return id1.compareTo(id2);
      }

    });

    validateListItem(items.get(0), 0);
    validateListItem(items.get(1), 1);
    validateListItem(items.get(2), 2);

  }

  private void validateListItem(Map<String, Object> item, int number) {
    Assert.assertTrue(item.keySet().size() == 4);

    Assert.assertTrue(item.containsKey("id"));
    Integer expectedId = testHelper.getBusinessUnitId(number);
    Integer actualId = Integer.valueOf(Integer.parseInt((String) item.get("id")));
    Assert.assertEquals(expectedId, actualId);

    Assert.assertTrue(item.containsKey("name"));
    String name = testHelper.getBusinessUnitName(number);
    Assert.assertTrue(((String) item.get("name")).endsWith(name));

    Assert.assertTrue(item.containsKey("elementUri"));
    Assert.assertTrue(((String) item.get("elementUri")).endsWith("/businessunit/json/" + item.get("id").toString()));

    Assert.assertTrue(item.containsKey("lastmodified"));

  }

  @Test
  public void testDetailsBM0() {
    testHelper = new JsonApiTestHelper(false);
    controller = testHelper.getBusinessUnitController();

    MockHttpServletRequest req = new MockHttpServletRequest();
    MockHttpServletResponse res = new MockHttpServletResponse();
    try {
      controller.details(req, res, testHelper.getBusinessUnitId(0));
    } catch (IOException ex) {
      //ignore
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> responseContent = testHelper.readJson(res, Map.class);

    validateDetailResponseKeys(responseContent, 0);

    Assert.assertTrue(responseContent.containsKey("parentUri"));
    String parentUri = (String) responseContent.get("parentUri");
    Assert.assertTrue(parentUri.endsWith("/businessunit/json/" + JsonApiTestHelper.BUSINESSUNIT_VIRTUAL_ROOT_ID));

    Assert.assertTrue(responseContent.containsKey("connectedInformationSystems"));

    @SuppressWarnings("unchecked")
    List<Object> cbu = (List<Object>) responseContent.get("connectedInformationSystems");

    Assert.assertTrue(cbu.size() == 1);

    String isr = (String) cbu.get(0);

    Assert.assertTrue(isr.contains(testHelper.getInformationSystemReleaseId(0).toString()));

    Assert.assertTrue(responseContent.keySet().size() == 7);

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> attr = (List<Map<String, Object>>) responseContent.get("attributes");
    Assert.assertEquals(2, attr.size());

    testHelper.validateAttributeGroup1(attr.get(0), testHelper.getATGroup1());
    testHelper.validateAttributeGroup1(attr.get(1), testHelper.getATGroup2());
  }

  @Test
  public void testDetailsBM1() {
    testHelper = new JsonApiTestHelper(false);
    controller = testHelper.getBusinessUnitController();

    MockHttpServletRequest req = new MockHttpServletRequest();
    MockHttpServletResponse res = new MockHttpServletResponse();
    try {
      controller.details(req, res, testHelper.getBusinessUnitId(1));
    } catch (IOException ex) {
      //ignore
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> responseContent = testHelper.readJson(res, Map.class);

    validateDetailResponseKeys(responseContent, 1);
    Assert.assertTrue(responseContent.containsKey("parentUri"));
    String parentUri = (String) responseContent.get("parentUri");
    Assert.assertTrue(parentUri.endsWith("/businessunit/json/" + JsonApiTestHelper.BUSINESSUNIT_VIRTUAL_ROOT_ID));

    Assert.assertTrue(responseContent.containsKey("connectedInformationSystems"));
    @SuppressWarnings("unchecked")
    List<Object> cbu = (List<Object>) responseContent.get("connectedInformationSystems");

    Assert.assertTrue(cbu.size() == 2);

    String isr = (String) cbu.get(0);
    String isr1 = (String) cbu.get(1);

    Assert.assertTrue(responseContent.keySet().size() == 7);

    Assert.assertTrue(isr.contains(testHelper.getInformationSystemReleaseId(0).toString()));
    Assert.assertTrue(isr1.contains(testHelper.getInformationSystemReleaseId(1).toString()));

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> attr = (List<Map<String, Object>>) responseContent.get("attributes");
    Assert.assertEquals(2, attr.size());

    testHelper.validateAttributeGroup1(attr.get(0), testHelper.getATGroup1());
    testHelper.validateAttributeGroup1(attr.get(1), testHelper.getATGroup2());
  }

  @Test
  public void testDetailsBM2() {
    testHelper = new JsonApiTestHelper(false);
    controller = testHelper.getBusinessUnitController();

    MockHttpServletRequest req = new MockHttpServletRequest();
    MockHttpServletResponse res = new MockHttpServletResponse();
    try {
      controller.details(req, res, testHelper.getBusinessUnitId(2));
    } catch (IOException ex) {
      //ignore
    }
    @SuppressWarnings("unchecked")
    Map<String, Object> responseContent = testHelper.readJson(res, Map.class);

    validateDetailResponseKeys(responseContent, 2);

    Assert.assertTrue(responseContent.containsKey("parentUri"));
    String parentUri = (String) responseContent.get("parentUri");
    Assert.assertTrue(parentUri.endsWith("/businessunit/json/" + testHelper.getBusinessUnitId(0).toString()));

    Assert.assertFalse(responseContent.containsKey("connectedInformationSystems"));

    Assert.assertTrue(responseContent.keySet().size() == 6);

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> attr = (List<Map<String, Object>>) responseContent.get("attributes");
    Assert.assertEquals(2, attr.size());

    testHelper.validateAttributeGroup1(attr.get(0), testHelper.getATGroup1());
    testHelper.validateAttributeGroup1(attr.get(1), testHelper.getATGroup2());
  }

  @Test
  public void testPermissionsLimited() {
    testHelper = new JsonApiTestHelper(true);
    controller = testHelper.getBusinessUnitController();
    MockHttpServletRequest req = new MockHttpServletRequest();
    MockHttpServletResponse res = new MockHttpServletResponse();
    try {
      controller.details(req, res, testHelper.getBusinessUnitId(0));
    } catch (IOException ex) {
      //ignore
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> responseContent = testHelper.readJson(res, Map.class);

    validateDetailResponseKeys(responseContent, 0);

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> attr = (List<Map<String, Object>>) responseContent.get("attributes");
    Assert.assertEquals(1, attr.size());

    testHelper.validateAttributeGroup1(attr.get(0), testHelper.getATGroup1());

  }

  /**
   * validates the keys that are equal for all test objects (regarding a certain pattern depending on the object's number)
   * @param responseContent
   * @param number
   */
  private void validateDetailResponseKeys(Map<String, Object> responseContent, int number) {
    Assert.assertTrue(responseContent.containsKey("name"));
    Assert.assertEquals(testHelper.getBusinessUnitName(number), responseContent.get("name"));

    Assert.assertTrue(responseContent.containsKey("id"));
    Assert.assertEquals(testHelper.getBusinessUnitId(number), responseContent.get("id"));

    Assert.assertTrue(responseContent.containsKey("description"));
    Assert.assertEquals(testHelper.getDescription(number), responseContent.get("description"));

    Assert.assertTrue(responseContent.containsKey("attributes"));

    Assert.assertTrue(responseContent.containsKey("lastModified"));

  }
}

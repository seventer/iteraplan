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
package de.iteratec.iteraplan.businesslogic.service.BusinessLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.BusinessFunctionService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 * Class for testing the service methods of the {@link BusinessFunctionService} interface.
 */
public class BusinessFunctionServiceTest extends BaseTransactionalTestSupport {

  private static final String     TEST_DESCRIPTION = "testDescription";
  @Autowired
  private BusinessFunctionService bfService        = null;
  @Autowired
  private TestDataHelper2         testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessFunctionServiceImpl#deleteEntity(de.iteratec.iteraplan.model.BusinessFunction)}
   * The method tests if the deleteEntity() deletes correctly an element from the database.
   */
  @Test
  public void testDeleteEntityCaseNoneException() {
    // create data
    BusinessFunction firstTestBF = testDataHelper.createBusinessFunction("firstTestBF", TEST_DESCRIPTION);
    BusinessFunction secondTestBF = testDataHelper.createBusinessFunction("secondTestBF", TEST_DESCRIPTION);
    BusinessFunction thirdTestBF = testDataHelper.createBusinessFunction("thirdTestBF", TEST_DESCRIPTION);
    commit();
    beginTransaction();

    firstTestBF = bfService.loadObjectById(firstTestBF.getId());
    secondTestBF = bfService.loadObjectById(secondTestBF.getId());
    thirdTestBF = bfService.loadObjectById(thirdTestBF.getId());

    BusinessFunction root = bfService.getFirstElement();
    firstTestBF.addParent(root);
    secondTestBF.addParent(root);
    thirdTestBF.addParent(root);

    bfService.saveOrUpdate(firstTestBF);
    bfService.saveOrUpdate(secondTestBF);
    bfService.saveOrUpdate(thirdTestBF);
    commit();

    // delete firstTestBD
    beginTransaction();
    Integer id = firstTestBF.getId();
    firstTestBF = bfService.loadObjectById(id);
    bfService.deleteEntity(firstTestBF);
    List<BusinessFunction> firstActualList = bfService.getEntityResultsBySearch(firstTestBF);
    List<BusinessFunction> secondActualList = bfService.getEntityResultsBySearch(secondTestBF);
    List<BusinessFunction> thirdActualList = bfService.getEntityResultsBySearch(thirdTestBF);
    commit();

    List<BusinessFunction> firstExpectedList = arrayList();
    List<BusinessFunction> secondExpectedList = arrayList();
    secondExpectedList.add(secondTestBF);
    List<BusinessFunction> thirdExpectedList = arrayList();
    thirdExpectedList.add(thirdTestBF);

    assertEquals(firstExpectedList, firstActualList);
    assertEquals(secondExpectedList, secondActualList);
    assertEquals(thirdExpectedList, thirdActualList);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessFunctionServiceImpl#deleteEntity(de.iteratec.iteraplan.model.BusinessFunction)}
   * The method tests if the deleteEntity() throws correctly an IteraplanBusinessException if the
   * user tries to delete the root element.
   */
  @Test
  public void testDeleteEntityCaseException() throws Exception {
    try {
      // create data
      BusinessFunction firstTestBD = testDataHelper.createBusinessFunction("firstTestBD", TEST_DESCRIPTION);
      BusinessFunction secondTestBD = testDataHelper.createBusinessFunction("secondTestBD", TEST_DESCRIPTION);
      BusinessFunction thirdTestBD = testDataHelper.createBusinessFunction("thirdTestBD", TEST_DESCRIPTION);
      commit();
      beginTransaction();

      firstTestBD = bfService.loadObjectById(firstTestBD.getId());
      secondTestBD = bfService.loadObjectById(secondTestBD.getId());
      thirdTestBD = bfService.loadObjectById(thirdTestBD.getId());

      BusinessFunction root = bfService.getFirstElement();
      firstTestBD.addParent(root);
      secondTestBD.addParent(root);
      thirdTestBD.addParent(root);

      bfService.saveOrUpdate(firstTestBD);
      bfService.saveOrUpdate(secondTestBD);
      bfService.saveOrUpdate(thirdTestBD);
      commit();

      // delete rootBD
      beginTransaction();
      bfService.deleteEntity(bfService.getFirstElement());
      fail("Expected IteraplanBusinessException");
    } catch (IteraplanBusinessException e) {
      // do noting, it's OK
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessFunctionServiceImpl#getFunctionsForInformationSystemRelease(de.iteratec.iteraplan.model.InformationSystemRelease)}
   * The method tests if the getFunctionsForInformationSystemRelease returns correct list with
   * BusinessFunctions which have association with a given InformationSystemRelease.
   */
  @Test
  public void testGetFunctionsForInformationSystemRelease() {
    // Set up the test data.
    InformationSystem systemA = testDataHelper.createInformationSystem("SystemA");
    InformationSystemRelease relA = testDataHelper.createInformationSystemRelease(systemA, "1.0");
    InformationSystemRelease relB = testDataHelper.createInformationSystemRelease(systemA, "2.0");

    BusinessFunction functionA = testDataHelper.createBusinessFunction("FunctionA", null);
    BusinessFunction functionB = testDataHelper.createBusinessFunction("FunctionB", null);
    BusinessFunction functionC = testDataHelper.createBusinessFunction("FunctionC", null);

    testDataHelper.addBfToIsr(relA, functionA);
    testDataHelper.addBfToIsr(relA, functionB);
    testDataHelper.addBfToIsr(relB, functionC);
    commit();

    // Perform test.
    beginTransaction();
    Set<BusinessFunction> list = relA.getBusinessFunctions();

    assertEquals("Expected number of referenced business functions:", 2, list.size());
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessFunctionServiceImpl#getBusinessFunctionsBySearch(de.iteratec.iteraplan.model.BusinessFunction, int, int)}
   * The method tests if the getBusinessDomainsBySearch() returns correct list with BusinessDomains.
   */
  @Test
  public void testGetBusinessFunctionsBySearch() {
    // create data
    BusinessFunction testBF = testDataHelper.createBusinessFunction("firstTestBF", TEST_DESCRIPTION);
    commit();
    beginTransaction();

    testBF = bfService.loadObjectById(testBF.getId());

    BusinessFunction root = bfService.getFirstElement();
    testBF.addParent(root);

    bfService.saveOrUpdate(testBF);
    commit();

    // delete rootBD
    beginTransaction();

    // get domain by search
    // search for domainA
    List<BusinessFunction> actualList = bfService.getEntityResultsBySearch(testBF);
    commit();

    List<BusinessFunction> expected = arrayList();
    expected.add(testBF);

    assertEquals(expected.size(), actualList.size());
    assertEquals(expected, actualList);
  }
}

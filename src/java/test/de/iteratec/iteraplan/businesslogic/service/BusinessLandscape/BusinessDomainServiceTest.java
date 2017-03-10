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

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.BusinessDomainService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessDomain;


/**
 * Class for testing the service methods of the {@link BusinessDomainService} interface.
 * 
 * @author mma
 */
public class BusinessDomainServiceTest extends BaseTransactionalTestSupport {

  private static final String   TEST_DESCRIPTION = "testDescription";
  @Autowired
  private BusinessDomainService businessDomainService;
  @Autowired
  private TestDataHelper2       testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessDomainServiceImpl#deleteEntity(de.iteratec.iteraplan.model.BusinessDomain)}
   * The method tests if the deleteEntity() deletes correctly an element from the database.
   */
  @Test
  public void testDeleteEntityCaseNoneException() {
    // create data
    BusinessDomain firstTestBD = testDataHelper.createBusinessDomain("firstTestBD", TEST_DESCRIPTION);
    BusinessDomain secondTestBD = testDataHelper.createBusinessDomain("secondTestBD", TEST_DESCRIPTION);
    BusinessDomain thirdTestBD = testDataHelper.createBusinessDomain("thirdTestBD", TEST_DESCRIPTION);
    commit();
    beginTransaction();

    firstTestBD = businessDomainService.loadObjectById(firstTestBD.getId());
    secondTestBD = businessDomainService.loadObjectById(secondTestBD.getId());
    thirdTestBD = businessDomainService.loadObjectById(thirdTestBD.getId());

    // get all the BuildingBlockTypes in association with the given id
    BusinessDomain root = businessDomainService.getFirstElement();
    firstTestBD.addParent(root);
    secondTestBD.addParent(root);
    thirdTestBD.addParent(root);

    businessDomainService.saveOrUpdate(firstTestBD);
    businessDomainService.saveOrUpdate(secondTestBD);
    businessDomainService.saveOrUpdate(thirdTestBD);
    commit();

    // delete firstTestBD
    beginTransaction();
    Integer id = firstTestBD.getId();
    firstTestBD = businessDomainService.loadObjectById(id);
    businessDomainService.deleteEntity(firstTestBD);
    List<BusinessDomain> firstActualList = businessDomainService.getEntityResultsBySearch(firstTestBD);
    List<BusinessDomain> secondActualList = businessDomainService.getEntityResultsBySearch(secondTestBD);
    List<BusinessDomain> thirdActualList = businessDomainService.getEntityResultsBySearch(thirdTestBD);
    commit();

    List<BusinessDomain> firstExpectedList = arrayList();
    List<BusinessDomain> secondExpectedList = arrayList();
    secondExpectedList.add(secondTestBD);
    List<BusinessDomain> thirdExpectedList = arrayList();
    thirdExpectedList.add(thirdTestBD);

    assertEquals(firstExpectedList, firstActualList);
    assertEquals(secondExpectedList, secondActualList);
    assertEquals(thirdExpectedList, thirdActualList);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessDomainServiceImpll#deleteEntity(de.iteratec.iteraplan.model.BusinessDomain)}
   * The method tests if the deleteEntity() throws correctly an IteraplanBusinessException if the
   * user tries to delete the root element.
   */
  @Test
  public void testDeleteEntityCaseException() throws Exception {
    try {
      BusinessDomain firstTestBD = testDataHelper.createBusinessDomain("firstTestBD", TEST_DESCRIPTION);
      BusinessDomain secondTestBD = testDataHelper.createBusinessDomain("secondTestBD", TEST_DESCRIPTION);
      BusinessDomain thirdTestBD = testDataHelper.createBusinessDomain("thirdTestBD", TEST_DESCRIPTION);
      commit();
      beginTransaction();

      firstTestBD = businessDomainService.loadObjectById(firstTestBD.getId());
      secondTestBD = businessDomainService.loadObjectById(secondTestBD.getId());
      thirdTestBD = businessDomainService.loadObjectById(thirdTestBD.getId());

      BusinessDomain root = businessDomainService.getFirstElement();
      firstTestBD.addParent(root);
      secondTestBD.addParent(root);
      thirdTestBD.addParent(root);

      businessDomainService.saveOrUpdate(firstTestBD);
      businessDomainService.saveOrUpdate(secondTestBD);
      businessDomainService.saveOrUpdate(thirdTestBD);
      commit();

      // delete rootBD
      beginTransaction();
      businessDomainService.deleteEntity(businessDomainService.getFirstElement());
      fail("Expected IteraplanBusinessException");
    } catch (IteraplanBusinessException e) {
      // do noting, it's OK
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessDomainServiceImpl#getBusinessDomainsBySearch(de.iteratec.iteraplan.model.BusinessDomain, int, int)}
   * The method tests if the getBusinessDomainsBySearch() returns correct list with BusinessDomains.
   */
  @Test
  public void testGetBusinessDomainsBySearch() {
    BusinessDomain testBD = testDataHelper.createBusinessDomain("firstTestBD", TEST_DESCRIPTION);
    commit();
    beginTransaction();

    testBD = businessDomainService.loadObjectById(testBD.getId());
    BusinessDomain root = businessDomainService.getFirstElement();
    testBD.addParent(root);

    businessDomainService.saveOrUpdate(testBD);
    commit();
    beginTransaction();

    // get domain by search
    // search for domainA
    List<BusinessDomain> actualList = businessDomainService.getEntityResultsBySearch(testBD);
    commit();

    List<BusinessDomain> expected = arrayList();
    expected.add(testBD);

    assertEquals(expected.size(), actualList.size());
    assertEquals(expected, actualList);
  }
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.BusinessMappingService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.HashBucketMatrix;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Class for testing the service methods of the {@link BusinessMappingService} interface.
 * 
 * @author mma
 */
public class BusinessMappingServiceTest extends BaseTransactionalTestSupport {

  private static final String    TEST_IS_NAME      = "firstTestIS";
  private static final String    TEST_BU_NAME      = "firstTestBU";
  private static final String    TEST_BP_NAME      = "firstTestBP";
  private static final String    TEST_PRODUCT_NAME = "firstTestProduct";
  private static final String    VERSION_1_0       = "1.0";
  private static final String    TEST_DESCRIPTION  = "testDescription";
  @Autowired
  private BusinessMappingService businessMappingService;
  @Autowired
  private TestDataHelper2        testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessMappingServiceImpl#getBusinessMappingsWithNoFunctions(de.iteratec.iteraplan.model.BuildingBlock)}
   */
  @Test
  public void testGetBusinessMappingsWithNoFunctionsCaseISR() throws Exception {
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(testDataHelper.createInformationSystem(TEST_IS_NAME), VERSION_1_0);
    BusinessUnit bu = testDataHelper.createBusinessUnit(TEST_BU_NAME, TEST_DESCRIPTION);
    BusinessProcess bp = testDataHelper.createBusinessProcess(TEST_BP_NAME, TEST_DESCRIPTION);
    Product product = testDataHelper.createProduct(TEST_PRODUCT_NAME, TEST_DESCRIPTION);
    BusinessMapping expected = testDataHelper.createBusinessMapping(isr, bp, bu, product);

    List<BusinessMapping> list = businessMappingService.getBusinessMappingsWithNoFunctions(isr);
    assertEquals(1, list.size());
    assertEquals(expected, list.get(0));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessMappingServiceImpl#getBusinessMappingsWithNoFunctions(de.iteratec.iteraplan.model.BuildingBlock)}
   */
  @Test
  public void testGetBusinessMappingsWithNoFunctionsCaseBP() throws Exception {
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(testDataHelper.createInformationSystem(TEST_IS_NAME), VERSION_1_0);
    BusinessUnit bu = testDataHelper.createBusinessUnit(TEST_BU_NAME, TEST_DESCRIPTION);
    BusinessProcess bp = testDataHelper.createBusinessProcess(TEST_BP_NAME, TEST_DESCRIPTION);
    Product product = testDataHelper.createProduct(TEST_PRODUCT_NAME, TEST_DESCRIPTION);
    BusinessMapping expected = testDataHelper.createBusinessMapping(isr, bp, bu, product);

    List<BusinessMapping> list = businessMappingService.getBusinessMappingsWithNoFunctions(bp);
    assertEquals(1, list.size());
    assertEquals(expected, list.get(0));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessMappingServiceImpl#getBusinessMappingsWithNoFunctions(de.iteratec.iteraplan.model.BuildingBlock)}
   */
  @Test
  public void testGetBusinessMappingsWithNoFunctionsCaseBU() throws Exception {
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(testDataHelper.createInformationSystem(TEST_IS_NAME), VERSION_1_0);
    BusinessUnit bu = testDataHelper.createBusinessUnit(TEST_BU_NAME, TEST_DESCRIPTION);
    BusinessProcess bp = testDataHelper.createBusinessProcess(TEST_BP_NAME, TEST_DESCRIPTION);
    Product product = testDataHelper.createProduct(TEST_PRODUCT_NAME, TEST_DESCRIPTION);
    BusinessMapping expected = testDataHelper.createBusinessMapping(isr, bp, bu, product);

    List<BusinessMapping> list = businessMappingService.getBusinessMappingsWithNoFunctions(bu);
    assertEquals(1, list.size());
    assertEquals(expected, list.get(0));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessMappingServiceImpl#getBusinessMappingsWithNoFunctions(de.iteratec.iteraplan.model.BuildingBlock)}
   */
  @Test
  public void testGetBusinessMappingsWithNoFunctionsCaseProduct() throws Exception {
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(testDataHelper.createInformationSystem(TEST_IS_NAME), VERSION_1_0);
    BusinessUnit bu = testDataHelper.createBusinessUnit(TEST_BU_NAME, TEST_DESCRIPTION);
    BusinessProcess bp = testDataHelper.createBusinessProcess(TEST_BP_NAME, TEST_DESCRIPTION);
    Product product = testDataHelper.createProduct(TEST_PRODUCT_NAME, TEST_DESCRIPTION);
    BusinessMapping expected = testDataHelper.createBusinessMapping(isr, bp, bu, product);

    List<BusinessMapping> list = businessMappingService.getBusinessMappingsWithNoFunctions(product);
    assertEquals(1, list.size());
    assertEquals(expected, list.get(0));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessMappingServiceImpl#getBusinessMappingByRelatedBuildingBlockIds(Integer, Integer, Integer, Integer)}
   */
  @Test
  public void testGetBusinessMappingByRelatedBuildingBlockIds() throws Exception {
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(testDataHelper.createInformationSystem(TEST_IS_NAME), VERSION_1_0);
    BusinessUnit bu = testDataHelper.createBusinessUnit(TEST_BU_NAME, TEST_DESCRIPTION);
    BusinessProcess bp = testDataHelper.createBusinessProcess(TEST_BP_NAME, TEST_DESCRIPTION);
    Product product = testDataHelper.createProduct(TEST_PRODUCT_NAME, TEST_DESCRIPTION);
    BusinessMapping expected = testDataHelper.createBusinessMapping(isr, bp, bu, product);

    BusinessMapping actual = businessMappingService.getBusinessMappingByRelatedBuildingBlockIds(product.getId(), bu.getId(), bp.getId(), isr.getId());
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessMappingServiceImpl#getBusinessMappingsWithNoFunctions(de.iteratec.iteraplan.model.BuildingBlock)}
   * The method tests if the getBusinessMappingsWithNoFunctions throws IllegalArgumentException
   * correctly if the method is for example with BusinessDomain called.
   */
  @Test
  public void testGetBusinessMappingsWithNoFunctionsCaseException() {
    try {
      BusinessDomain testBD = testDataHelper.createBusinessDomain("testBD", TEST_DESCRIPTION);
      businessMappingService.getBusinessMappingsWithNoFunctions(testBD);
    } catch (IllegalArgumentException e) {
      // do noting, it's OK
    }
  }

  @Test
  public void testSaveOrUpdateCaseBpIsNull() {
    BusinessMapping bm = setUpBusinessMapping();

    beginTransaction();

    // invalid Business Process
    bm = businessMappingService.loadObjectById(bm.getId());
    bm.setBusinessProcess(null);

    testSaveOrUpdateInvalidMapping(bm);

    beginTransaction();
    bm = businessMappingService.loadObjectById(bm.getId());
    assertNotNull("null value for Business Process should not have been saved.", bm.getBusinessProcess());
  }

  @Test
  public void testSaveOrUpdateCaseBuIsNull() {
    BusinessMapping bm = setUpBusinessMapping();

    beginTransaction();

    // invalid Business Process
    bm = businessMappingService.loadObjectById(bm.getId());
    bm.setBusinessUnit(null);

    testSaveOrUpdateInvalidMapping(bm);

    beginTransaction();
    bm = businessMappingService.loadObjectById(bm.getId());
    assertNotNull("null value for Business Unit should not have been saved.", bm.getBusinessUnit());
  }

  @Test
  public void testSaveOrUpdateCaseProdIsNull() {
    BusinessMapping bm = setUpBusinessMapping();

    beginTransaction();

    // invalid Business Process
    bm = businessMappingService.loadObjectById(bm.getId());
    bm.setProduct(null);

    testSaveOrUpdateInvalidMapping(bm);

    beginTransaction();
    bm = businessMappingService.loadObjectById(bm.getId());
    assertNotNull("null value for Product should not have been saved.", bm.getProduct());
  }

  @Test
  public void testSaveOrUpdateCaseIsrIsNull() {
    BusinessMapping bm = setUpBusinessMapping();

    beginTransaction();

    // invalid Business Process
    bm = businessMappingService.loadObjectById(bm.getId());
    bm.setInformationSystemRelease(null);

    testSaveOrUpdateInvalidMapping(bm);

    beginTransaction();
    bm = businessMappingService.loadObjectById(bm.getId());
    assertNotNull("null value for Information System Release should not have been saved.", bm.getInformationSystemRelease());
  }

  private BusinessMapping setUpBusinessMapping() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    Product prod = testDataHelper.createProduct("testProd", TEST_DESCRIPTION);
    BusinessUnit bu = testDataHelper.createBusinessUnit("testBU", TEST_DESCRIPTION);
    BusinessProcess bp = testDataHelper.createBusinessProcess("testBP", TEST_DESCRIPTION);

    BusinessMapping bm = testDataHelper.createBusinessMapping(isr, bp, bu, prod);

    commit();
    return bm;
  }

  private void testSaveOrUpdateInvalidMapping(BusinessMapping bm) {
    try {
      businessMappingService.saveOrUpdate(bm);
      fail("saveOrUpdate should have failed with an exception.");
    } catch (IteraplanBusinessException e) {
      assertEquals("Wrong exception thrown.", e.getErrorCode(), IteraplanErrorMessages.CANNOT_ADD_INVALID_BUSINESS_MAPPINGS);
    }
    rollback();
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.BusinessMappingServiceImpl#findByNames(java.util.Set)}
   */
  @Test
  public void testFindByNames() {
    try {
      businessMappingService.findByNames(Sets.<String> newHashSet());
      fail("expected exception: " + IllegalStateException.class.getSimpleName());
    } catch (IllegalStateException e) {
      // OK
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.BusinessMappingServiceImpl#getTabelData(TypeOfBuildingBlock, Integer, TypeOfBuildingBlock, TypeOfBuildingBlock)}
   */
  @Test
  public void testGetTabelDataEmpty() {
    Product first = testDataHelper.createProduct("TestProduct (first)", "");
    HashBucketMatrix<BuildingBlock, BuildingBlock, BuildingBlock> table = businessMappingService.getTabelData(TypeOfBuildingBlock.PRODUCT,
        first.getId(), TypeOfBuildingBlock.BUSINESSPROCESS, TypeOfBuildingBlock.BUSINESSUNIT);
    assertTrue(table.entrySet().isEmpty());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.BusinessMappingServiceImpl#getTabelData(TypeOfBuildingBlock, Integer, TypeOfBuildingBlock, TypeOfBuildingBlock)}
   */
  @Test
  public void testGetTabelDataWrongBBT() {
    try {
      Product first = testDataHelper.createProduct("TestProduct (first)", "");
      businessMappingService.getTabelData(TypeOfBuildingBlock.PRODUCT, first.getId(), TypeOfBuildingBlock.BUSINESSPROCESS,
          TypeOfBuildingBlock.BUSINESSPROCESS);
      fail("expected exception: " + IteraplanTechnicalException.class.getSimpleName());
    } catch (IteraplanTechnicalException e) {
      // OK
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.BusinessMappingServiceImpl#getTabelData(TypeOfBuildingBlock, Integer, TypeOfBuildingBlock, TypeOfBuildingBlock)}
   */
  @Test
  public void testGetTabelData1() {
    InformationSystemRelease first = testDataHelper.createInformationSystemRelease(testDataHelper.createInformationSystem("Test ISR (first)"), "1.0");
    BusinessProcess bp1 = testDataHelper.createBusinessProcess("bp1", "");
    BusinessProcess bp2 = testDataHelper.createBusinessProcess("bp2", "");
    BusinessProcess bp3 = testDataHelper.createBusinessProcess("bp3", "");
    BusinessUnit bu1 = testDataHelper.createBusinessUnit("bu1", "");
    BusinessUnit bu2 = testDataHelper.createBusinessUnit("bu2", "");
    BusinessUnit bu3 = testDataHelper.createBusinessUnit("bu3", "");
    Product p1 = testDataHelper.createProduct("p1", "");
    Product p2 = testDataHelper.createProduct("p2", "");

    testDataHelper.createBusinessMapping(first, bp1, bu1, p1);
    testDataHelper.createBusinessMapping(first, bp1, bu1, p2);
    testDataHelper.createBusinessMapping(first, bp1, bu2, p1);
    testDataHelper.createBusinessMapping(first, bp1, bu2, p2);
    testDataHelper.createBusinessMapping(first, bp2, bu1, p1);
    testDataHelper.createBusinessMapping(first, bp2, bu2, p1);
    testDataHelper.createBusinessMapping(first, bp2, bu3, p2);

    HashBucketMatrix<BuildingBlock, BuildingBlock, BuildingBlock> table = businessMappingService.getTabelData(
        TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, first.getId(), TypeOfBuildingBlock.BUSINESSPROCESS, TypeOfBuildingBlock.BUSINESSUNIT);

    ArrayList<Product> pList = Lists.newArrayList(p1, p2);
    ArrayList<Product> p1List = Lists.newArrayList(p1);
    ArrayList<Product> p2List = Lists.newArrayList(p2);

    assertEquals(7, table.entrySet().size());
    assertEquals(pList, table.getBucket(bp1, bu1));
    assertEquals(pList, table.getBucket(bp1, bu2));
    assertEquals(p1List, table.getBucket(bp2, bu1));
    assertEquals(p1List, table.getBucket(bp2, bu2));
    assertEquals(p2List, table.getBucket(bp2, bu3));
    assertNull(table.getBucket(bp3, bu1));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.BusinessMappingServiceImpl#getTabelData(TypeOfBuildingBlock, Integer, TypeOfBuildingBlock, TypeOfBuildingBlock)}
   */
  @Test
  public void testGetTabelData2() {
    Product first = testDataHelper.createProduct("TestProduct (first)", "");
    BusinessProcess bp1 = testDataHelper.createBusinessProcess("bp1", "");
    BusinessProcess bp2 = testDataHelper.createBusinessProcess("bp2", "");
    BusinessProcess bp3 = testDataHelper.createBusinessProcess("bp3", "");
    BusinessUnit bu1 = testDataHelper.createBusinessUnit("bu1", "");
    BusinessUnit bu2 = testDataHelper.createBusinessUnit("bu2", "");
    BusinessUnit bu3 = testDataHelper.createBusinessUnit("bu3", "");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(testDataHelper.createInformationSystem("isr1"), "1.0");
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(testDataHelper.createInformationSystem("isr2"), "1.0");

    testDataHelper.createBusinessMapping(isr1, bp1, bu1, first);
    testDataHelper.createBusinessMapping(isr2, bp1, bu1, first);
    testDataHelper.createBusinessMapping(isr1, bp1, bu3, first);
    testDataHelper.createBusinessMapping(isr1, bp2, bu1, first);
    testDataHelper.createBusinessMapping(isr1, bp2, bu2, first);
    testDataHelper.createBusinessMapping(isr1, bp2, bu3, first);
    testDataHelper.createBusinessMapping(isr2, bp1, bu2, first);

    HashBucketMatrix<BuildingBlock, BuildingBlock, BuildingBlock> table = businessMappingService.getTabelData(TypeOfBuildingBlock.PRODUCT,
        first.getId(), TypeOfBuildingBlock.BUSINESSUNIT, TypeOfBuildingBlock.BUSINESSPROCESS);

    ArrayList<InformationSystemRelease> isrList = Lists.newArrayList(isr1, isr2);
    ArrayList<InformationSystemRelease> isr1List = Lists.newArrayList(isr1);
    ArrayList<InformationSystemRelease> isr2List = Lists.newArrayList(isr2);

    assertEquals(7, table.entrySet().size());
    assertEquals(isrList, table.getBucket(bu1, bp1));
    assertEquals(isr1List, table.getBucket(bu3, bp1));
    assertEquals(isr1List, table.getBucket(bu1, bp2));
    assertEquals(isr1List, table.getBucket(bu2, bp2));
    assertEquals(isr1List, table.getBucket(bu3, bp2));
    assertEquals(isr2List, table.getBucket(bu2, bp1));
    assertNull(table.getBucket(bu1, bp3));
  }

}

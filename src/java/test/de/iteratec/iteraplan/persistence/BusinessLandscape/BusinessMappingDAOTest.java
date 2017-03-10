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
package de.iteratec.iteraplan.persistence.BusinessLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.persistence.dao.BusinessMappingDAO;


/**
 * @author mma
 */
public class BusinessMappingDAOTest extends BaseTransactionalTestSupport {

  private static final String TEST_IS_NAME      = "testIS";
  private static final String TEST_BP_NAME      = "testProcess";
  private static final String TEST_BU_NAME      = "testUnit";
  private static final String TEST_PRODUCT_NAME = "testProduct";
  private static final String BETA_VERSION      = "Beta";
  private static final String TEST_DESCRIPTION  = "testDescription";
  @Autowired
  private BusinessMappingDAO  businessMappingDAO;
  @Autowired
  private TestDataHelper2     testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.BusinessMappingDAO#getBusinessMappingsConnectedToISR(java.lang.Integer)}
   * Here is bm expected, because the release is associated with bm.
   */
  @Test
  public void testGetBusinessMappingsConnectedToISR() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(is, BETA_VERSION);
    BusinessProcess process = testDataHelper.createBusinessProcess(TEST_BP_NAME, TEST_DESCRIPTION);
    BusinessUnit unit = testDataHelper.createBusinessUnit(TEST_BU_NAME, TEST_DESCRIPTION);
    Product product = testDataHelper.createProduct(TEST_PRODUCT_NAME, TEST_DESCRIPTION);

    BusinessMapping bm = testDataHelper.createBusinessMapping(release, process, unit, product);
    commit();

    beginTransaction();
    Integer id = release.getId();
    commit();

    List<BusinessMapping> actual = businessMappingDAO.getBusinessMappingsConnectedToISR(id);
    List<BusinessMapping> expected = arrayList();
    expected.add(bm);

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.BusinessMappingDAO#getBusinessMappingsConnectedToBU(java.lang.Integer)}
   * Here is bm expected, because the businessUnit is associated with bm.
   */
  @Test
  public void testGetBusinessMappingsConnectedToBU() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(is, BETA_VERSION);
    BusinessProcess process = testDataHelper.createBusinessProcess(TEST_BP_NAME, TEST_DESCRIPTION);
    BusinessUnit unit = testDataHelper.createBusinessUnit(TEST_BU_NAME, TEST_DESCRIPTION);
    Product product = testDataHelper.createProduct(TEST_PRODUCT_NAME, TEST_DESCRIPTION);

    BusinessMapping bm = testDataHelper.createBusinessMapping(release, process, unit, product);
    commit();

    beginTransaction();
    Integer id = unit.getId();
    commit();

    List<BusinessMapping> actual = businessMappingDAO.getBusinessMappingsConnectedToBU(id);
    List<BusinessMapping> expected = arrayList();
    expected.add(bm);

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.BusinessMappingDAO#getBusinessMappingsConnectedToBP(java.lang.Integer)}
   * Here is bm expected, because the businnessProcess is associated with bm.
   */
  @Test
  public void testGetBusinessMappingsConnectedToBP() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(is, BETA_VERSION);
    BusinessProcess process = testDataHelper.createBusinessProcess(TEST_BP_NAME, TEST_DESCRIPTION);
    BusinessUnit unit = testDataHelper.createBusinessUnit(TEST_BU_NAME, TEST_DESCRIPTION);
    Product product = testDataHelper.createProduct(TEST_PRODUCT_NAME, TEST_DESCRIPTION);

    BusinessMapping bm = testDataHelper.createBusinessMapping(release, process, unit, product);
    commit();

    beginTransaction();
    Integer id = process.getId();
    commit();

    List<BusinessMapping> actual = businessMappingDAO.getBusinessMappingsConnectedToBP(id);
    List<BusinessMapping> expected = arrayList();
    expected.add(bm);

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.BusinessMappingDAO#getBusinessMappingsConnectedToProduct(java.lang.Integer)}
   * Here is bm expected, because the product is associated with bm.
   */
  @Test
  public void testGetBusinessMappingsConnectedToProduct() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(is, BETA_VERSION);
    BusinessProcess process = testDataHelper.createBusinessProcess(TEST_BP_NAME, TEST_DESCRIPTION);
    BusinessUnit unit = testDataHelper.createBusinessUnit(TEST_BU_NAME, TEST_DESCRIPTION);
    Product product = testDataHelper.createProduct(TEST_PRODUCT_NAME, TEST_DESCRIPTION);

    BusinessMapping bm = testDataHelper.createBusinessMapping(release, process, unit, product);
    commit();

    beginTransaction();
    Integer id = product.getId();
    commit();

    List<BusinessMapping> actual = businessMappingDAO.getBusinessMappingsConnectedToProduct(id);
    List<BusinessMapping> expected = arrayList();
    expected.add(bm);

    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.persistence.dao.BusinessMappingDAO#getBusinessMappingConnectedToProductAndBUAndBPAndISR(Integer, Integer, Integer, Integer)}
   */
  @Test
  public void testGetBusinessMappingConnectedToProductAndBUAndBPAndISR() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(is, BETA_VERSION);
    BusinessProcess process = testDataHelper.createBusinessProcess(TEST_BP_NAME, TEST_DESCRIPTION);
    BusinessUnit unit = testDataHelper.createBusinessUnit(TEST_BU_NAME, TEST_DESCRIPTION);
    Product product = testDataHelper.createProduct(TEST_PRODUCT_NAME, TEST_DESCRIPTION);

    testDataHelper.createBusinessMapping(release, process, unit, product);
    commit();

    beginTransaction();
    BusinessMapping bm2 = businessMappingDAO.getBusinessMappingConnectedToProductAndBUAndBPAndISR(product.getId(), unit.getId(), process.getId(),
        release.getId());

    assertNotNull(bm2);
    assertEquals(product, bm2.getProduct());
    assertEquals(release, bm2.getInformationSystemRelease());
    assertEquals(process, bm2.getBusinessProcess());
    assertEquals(unit, bm2.getBusinessUnit());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.persistence.dao.BusinessMappingDAO#getBusinessMappingConnectedToProductAndBUAndBPAndISR(Integer, Integer, Integer, Integer)}
   */
  @Test
  public void testGetBusinessMappingConnectedToProductAndBUAndBPAndISRForDublicateMapping() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(is, BETA_VERSION);
    BusinessProcess process = testDataHelper.createBusinessProcess(TEST_BP_NAME, TEST_DESCRIPTION);
    BusinessUnit unit = testDataHelper.createBusinessUnit(TEST_BU_NAME, TEST_DESCRIPTION);
    Product product = testDataHelper.createProduct(TEST_PRODUCT_NAME, TEST_DESCRIPTION);

    testDataHelper.createBusinessMapping(release, process, unit, product);
    testDataHelper.createBusinessMapping(release, process, unit, product);
    commit();

    beginTransaction();
    try {
      businessMappingDAO.getBusinessMappingConnectedToProductAndBUAndBPAndISR(product.getId(), unit.getId(), process.getId(), release.getId());
      fail();
    } catch (IteraplanTechnicalException e) {
      //ok
    }
  }

}

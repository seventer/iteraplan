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
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.ProductService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.persistence.dao.ProductDAO;


/**
 * Class for testing the service methods of the {@link ProductService} interface.
 */
public class ProductServiceTest extends BaseTransactionalTestSupport {

  private static final String             TEST_DESCRIPTION = "testDescription";
  @Autowired
  private ProductService                  productService;
  @Autowired
  private ProductDAO                      productDAO;
  @Autowired
  private InformationSystemReleaseService informationSystemReleaseService;
  @Autowired
  private TestDataHelper2  testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.ProductServiceImpl#deleteEntity(de.iteratec.iteraplan.model.Product)}
   * The method tests if the deleteEntity() throws correctly an IteraplanBusinessException if the
   * user tries to delete the root element.
   */
  @Test
  public void testDeleteEntityCaseIteraplanBusinessException() throws Exception {
    try {
      // create data
      Product firstTestP = testDataHelper.createProduct("firstTestProduct", TEST_DESCRIPTION);
      Product secondTestP = testDataHelper.createProduct("secondTestProduct", TEST_DESCRIPTION);
      Product thirdTestP = testDataHelper.createProduct("thirdTestProduct", TEST_DESCRIPTION);
      commit();
      beginTransaction();

      firstTestP = productService.loadObjectById(firstTestP.getId());
      secondTestP = productService.loadObjectById(secondTestP.getId());
      thirdTestP = productService.loadObjectById(thirdTestP.getId());

      Product root = productService.getFirstElement();
      firstTestP.addParent(root);
      secondTestP.addParent(root);
      thirdTestP.addParent(root);

      productDAO.saveOrUpdate(firstTestP);
      productDAO.saveOrUpdate(secondTestP);
      productDAO.saveOrUpdate(thirdTestP);
      commit();

      // delete rootISD
      beginTransaction();
      productService.deleteEntity(productService.getFirstElement());
      fail("Expected IteraplanBusinessException");
    } catch (IteraplanBusinessException e) {
      // do noting, it's OK
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemDomainServiceImpll#deleteEntity(de.iteratec.iteraplan.model.Product)}
   * The method tests if the deleteEntity() deletes correctly a Product from the database.
   */
  @Test
  public void testDeleteEntity() {
    InformationSystem is = testDataHelper.createInformationSystem("CI");
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(is, "1.0");

    Product productA = testDataHelper.createProduct("Product-A", "-");
    Product productB = testDataHelper.createProduct("Product-B", "-");
    Product productC = testDataHelper.createProduct("Product-C", "-");
    commit();
    beginTransaction();

    productA = productService.loadObjectById(productA.getId());
    productB = productService.loadObjectById(productB.getId());
    productC = productService.loadObjectById(productC.getId());

    Product root = productService.getFirstElement();
    productA.addParent(root);
    productB.addParent(productA);
    productC.addParent(productB);
    productDAO.saveOrUpdate(productA);
    productDAO.saveOrUpdate(productB);
    productDAO.saveOrUpdate(productC);
    testDataHelper.createBusinessMapping(release, null, null, productA);
    commit();

    // delete organizational units
    beginTransaction();
    Integer id = productA.getId();
    productA = productDAO.loadObjectById(id);
    productService.deleteEntity(productA);
    commit();

    beginTransaction();

    // object doesn't exist anymore
    productA = productDAO.loadObjectByIdIfExists(id);
    if (productA != null) {
      fail();
    }

    // virtual element has no more children
    root = productService.getFirstElement();
    assertEquals(0, root.getChildren().size());

    // information system release has no more associated business supports
    release = informationSystemReleaseService.loadObjectById(release.getId());
    assertEquals(0, release.getBusinessMappings().size());
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.ProductServiceImpl#getProductsBySearch(de.iteratec.iteraplan.model.Product, int, int)}
   * The method tests if the getProductsBySearch() returns correct list with Products.
   */
  @Test
  public void testGetProductsBySearch() {
    Product testISD = testDataHelper.createProduct("testISD", TEST_DESCRIPTION);
    commit();

    beginTransaction();
    // get Unit by search
    // search for Product
    List<Product> actualList = productService.getEntityResultsBySearch(testISD);
    commit();

    List<Product> expected = arrayList();
    expected.add(testISD);

    assertEquals(expected.size(), actualList.size());
    assertEquals(expected, actualList);
  }
}
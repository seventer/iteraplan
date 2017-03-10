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
package de.iteratec.iteraplan.persistence.dao;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.Product;


/**
 * @author mma
 */
public class ProductDAOImplTest extends BaseTransactionalTestSupport {

  private static final String TEST_DESCRIPTION = "testDescription";
  @Autowired
  private ProductDAOImpl      productDao;
  @Autowired
  private TestDataHelper2     testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.ProductDAOImpl#onBeforeDelete(de.iteratec.iteraplan.model.Product)}
   */
  @Test
  public void testOnBeforeDelete() {
    Product entity = testDataHelper.createProduct("testProduct", TEST_DESCRIPTION);
    Product parent = testDataHelper.createProduct("parentProduct", TEST_DESCRIPTION);

    Product firstChild = testDataHelper.createProduct("firstChildProduct", TEST_DESCRIPTION);
    Product secChild = testDataHelper.createProduct("secChildProduct", TEST_DESCRIPTION);
    Product thirdChild = testDataHelper.createProduct("thirdChildProduct", TEST_DESCRIPTION);
    commit();

    beginTransaction();
    thirdChild.addParent(secChild);
    secChild.addParent(firstChild);
    commit();

    List<Product> children = arrayList();
    children.add(firstChild);

    beginTransaction();
    entity.addParent(parent);
    entity.setChildren(children);
    commit();

    productDao.onBeforeDelete(entity);

    Product actualParent = entity.getParent();

    List<Product> expectedChildren = arrayList();
    List<Product> actualChildren = entity.getChildren();

    // after the delete operation the entity should not have a parent.
    assertNull(actualParent);
    // after the delete operation the entity should not have any children.
    assertEquals(expectedChildren, actualChildren);
    // after the delete operation the children should not have association with one another.
    assertNull(thirdChild.getParent());
    assertNull(secChild.getParent());
    assertNull(firstChild.getParent());

  }

}
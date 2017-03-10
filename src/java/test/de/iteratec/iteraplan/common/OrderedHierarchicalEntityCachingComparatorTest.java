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
package de.iteratec.iteraplan.common;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.sorting.OrderedHierarchicalEntityCachingComparator;


/**
 * Class for testing the Comparator {@link OrderedHierarchicalEntityCachingComparator}.
 * 
 * @author sip
 */
public class OrderedHierarchicalEntityCachingComparatorTest extends BaseTransactionalTestSupport {

  private Random          rand = new Random(123456);
  private int             x    = 0;
  @Autowired
  private TestDataHelper2 testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.sorting.OrderedHierarchicalEntityCachingComparator#compare(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.BuildingBlock)}.
   * 
   * This hierarchies will be build:
   * 
   * virtual element
   *    - bo0
   *        - bo5
   *    - bo1
   *        - bo2
   *            - bo3
   *                - bo4
   *                    - bo7
   *                    - bo8
   *                        - bo9
   *                    - bo11
   *                    - bo12
   *                - bo6
   *                    - bo10
   *            - bo13
   *        - bo14
   *        - bo15
   */
  @Test
  public void testCompareHierarchie() {
    BusinessObject bo0 = createBO(null);
    BusinessObject bo1 = createBO(null);
    BusinessObject bo2 = createBO(bo1);
    BusinessObject bo3 = createBO(bo2);
    BusinessObject bo4 = createBO(bo3);
    BusinessObject bo5 = createBO(bo0);
    BusinessObject bo6 = createBO(bo3);
    BusinessObject bo7 = createBO(bo4);
    BusinessObject bo8 = createBO(bo4);
    BusinessObject bo9 = createBO(bo8);
    BusinessObject bo10 = createBO(bo6);
    BusinessObject bo11 = createBO(bo8);
    BusinessObject bo12 = createBO(bo8);
    BusinessObject bo13 = createBO(bo2);
    BusinessObject bo14 = createBO(bo1);
    BusinessObject bo15 = createBO(bo1);

    BusinessObject virtual = bo0.getParent();
    assertOrder(new OrderedHierarchicalEntityCachingComparator<BusinessObject>(), virtual, bo0, bo5, bo1, bo2, bo3, bo4, bo7, bo8, bo9, bo11, bo12,
        bo6, bo10, bo13, bo14, bo15);
    assertOrder(new OrderedHierarchicalEntityCachingComparator<BusinessObject>(), bo0, bo4, bo7, bo8, bo10);
    assertOrder(new OrderedHierarchicalEntityCachingComparator<BusinessObject>(), bo1, bo2, bo3, bo12, bo6);
    assertOrder(new OrderedHierarchicalEntityCachingComparator<BusinessObject>(), bo3, bo14, bo15);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.sorting.OrderedHierarchicalEntityCachingComparator#compare(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.BuildingBlock)}.
   */
  @Test
  public void testCompareNoId() {
    OrderedHierarchicalEntityCachingComparator<BusinessDomain> comp = new OrderedHierarchicalEntityCachingComparator<BusinessDomain>();
    try {
      comp.compare(new BusinessDomain(), new BusinessDomain());
      fail("expected " + IteraplanTechnicalException.class.getSimpleName());
    } catch (IteraplanTechnicalException e) {
      //OK
    }
  }

  private BusinessObject createBO(BusinessObject parent) {
    BusinessObject bo = testDataHelper.createBusinessObject(getRandomString(), "");
    if (parent != null) {
      testDataHelper.addElementOf(parent, bo);
    }
    return bo;
  }

  /**
   * this comparator uses the position, for compare(), the name should be never mind.
   * @return a random string of 3 lower case letters followed by an unique number for one test case.
   */
  private String getRandomString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 3; i++) {
      int r = Math.abs(rand.nextInt() % ('z' - 'a' + 1));
      sb.append((char) ('a' + r));
    }
    sb.append(x++);
    return sb.toString();
  }

  private <T extends HierarchicalEntity<T>> void assertCompBefore(OrderedHierarchicalEntityCachingComparator<T> comp, T t1, T t2) {
    String name1 = t1.getHierarchicalName() + "(id=" + t1.getId() + ")";
    String name2 = t2.getHierarchicalName() + "(id=" + t2.getId() + ")";
    assertTrue(String.format("compare %s with %s should return a negaive number.", name1, name2), comp.compare(t1, t2) < 0);
    assertTrue(String.format("compare %s with %s should return a positive number.", name1, name2), comp.compare(t2, t1) > 0);
  }

  private <T extends HierarchicalEntity<T>> void assertOrder(OrderedHierarchicalEntityCachingComparator<T> comp, T... ts) {
    for (int i = 0; i < ts.length; i++) {
      for (int j = i + 1; j < ts.length; j++) {
        assertCompBefore(comp, ts[i], ts[j]);
      }
    }
  }
}

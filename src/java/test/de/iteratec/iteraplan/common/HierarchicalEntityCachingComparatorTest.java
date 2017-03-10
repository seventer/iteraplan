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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;


/**
 * Class for testing the Comparator {@link HierarchicalEntityCachingComparator}.
 * 
 * @author sip
 */
public class HierarchicalEntityCachingComparatorTest extends BaseTransactionalTestSupport {
  @Autowired
  private TestDataHelper2 testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator#compare(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.BuildingBlock)}.
   */
  @Test
  public void testCompareRelease() {
    HierarchicalEntityCachingComparator<InformationSystemRelease> comp = new HierarchicalEntityCachingComparator<InformationSystemRelease>();

    InformationSystem is = testDataHelper.createInformationSystem("a");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is, "1");
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is, "2");

    assertCompBefore(comp, isr1, isr2);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator#compare(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.BuildingBlock)}.
   */
  @Test
  public void testCompareNonHierarchy() {
    HierarchicalEntityCachingComparator<BusinessObject> comp = new HierarchicalEntityCachingComparator<BusinessObject>();

    BusinessObject bo1 = testDataHelper.createBusinessObject("a", "");
    BusinessObject bo2 = testDataHelper.createBusinessObject("b", "");

    assertCompBefore(comp, bo1, bo2);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator#compare(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.BuildingBlock)}.
   */
  @Test
  public void testCompareHierarchy() {
    HierarchicalEntityCachingComparator<Product> comp = new HierarchicalEntityCachingComparator<Product>();

    Product p1 = testDataHelper.createProduct("a", "");
    Product p11 = testDataHelper.createProduct("c", "");
    p11.addParent(p1);
    Product p12 = testDataHelper.createProduct("d", "");
    p12.addParent(p1);

    Product p2 = testDataHelper.createProduct("b", "");
    Product p21 = testDataHelper.createProduct("e", "");
    p21.addParent(p2);
    Product p22 = testDataHelper.createProduct("f", "");
    p22.addParent(p2);

    Product[] ordered = new Product[] { p1, p11, p12, p2, p21, p22 };
    assertOrder(comp, ordered);

    List<Product> expected = Lists.newArrayList(ordered);
    List<Product> actual = Lists.newArrayList(ordered);
    Collections.shuffle(actual);
    Collections.sort(actual, comp);
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator#compare(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.BuildingBlock)}.
   */
  @Test
  public void testCompareHierarchyWithSeparator() {
    HierarchicalEntityCachingComparator<BusinessObject> comp = new HierarchicalEntityCachingComparator<BusinessObject>();

    BusinessObject bo1 = testDataHelper.createBusinessObject("test1", ""); // test1

    BusinessObject bo = testDataHelper.createBusinessObject("test", "");
    BusinessObject bo2 = testDataHelper.createBusinessObject("2", ""); // test : 2
    testDataHelper.addElementOf(bo, bo2);

    BusinessObject bo3 = testDataHelper.createBusinessObject("test3", ""); // test3

    // test : 2 is before test1 and test2, because the separator, " : " belongs to the hierarchical name
    assertOrder(comp, bo, bo2, bo1, bo3);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator#compare(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.BuildingBlock)}.
   */
  @Test
  public void testCompareNoId() {
    HierarchicalEntityCachingComparator<BusinessDomain> comp = new HierarchicalEntityCachingComparator<BusinessDomain>();
    try {
      comp.compare(new BusinessDomain(), new BusinessDomain());
      fail("expected " + IteraplanTechnicalException.class.getSimpleName());
    } catch (IteraplanTechnicalException e) {
      //OK
    }
  }

  private <T extends HierarchicalEntity<T>> void assertCompBefore(HierarchicalEntityCachingComparator<T> comp, T t1, T t2) {
    String name1 = t1.getHierarchicalName() + "(id=" + t1.getId() + ")";
    String name2 = t2.getHierarchicalName() + "(id=" + t2.getId() + ")";
    assertTrue(String.format("compare %s with %s should return a negaive number.", name1, name2), comp.compare(t1, t2) < 0);
    assertTrue(String.format("compare %s with %s should return a positive number.", name1, name2), comp.compare(t2, t1) > 0);
  }

  private <T extends HierarchicalEntity<T>> void assertOrder(HierarchicalEntityCachingComparator<T> comp, T... ts) {
    for (int i = 0; i < ts.length; i++) {
      for (int j = i + 1; j < ts.length; j++) {
        assertCompBefore(comp, ts[i], ts[j]);
      }
    }
  }
}

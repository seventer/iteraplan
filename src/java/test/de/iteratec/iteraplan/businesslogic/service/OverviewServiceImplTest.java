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
package de.iteratec.iteraplan.businesslogic.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.OverviewElementLists.ElementList;


/**
 * Tests the service methods of the {@link OverviewService} interface.
 * 
 * @author sip
 */
public class OverviewServiceImplTest extends BaseTransactionalTestSupport {

  private static final String TEST_BO_NAME1 = "testBO1";
  private static final String TEST_BO_NAME2 = "testBO2";
  private static final String TEST_BO_NAME3 = "testBO3";
  private static final String TEST_DESC     = "desc";
  private static final String TEST_IS_NAME  = "testIS";
  private static final String TEST_RELEASE1 = "rel1";
  private static final String TEST_RELEASE2 = "rel2";
  private static final String TEST_RELEASE3 = "rel3";
  private static final String TEST_RELEASE4 = "rel4";
  private static final String TEST_RELEASE5 = "rel5";
  private static final String TEST_RELEASE6 = "rel6";
  private static final String TEST_RELEASE7 = "rel7";
  @Autowired
  private OverviewService     overviewService;
  @Autowired
  private TestDataHelper2     testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.OverviewService#getElementLists()}
   */
  @Test
  public void testGetElementListsHtmlIds() {
    Set<String> expected = Sets.newHashSet("businessdomain", "businessprocess", "businessunit", "product", "businessfunction", "businessobject",
        "informationsystemdomain", "informationsystem", "architecturaldomain", "technicalcomponent", "infrastructureelement", "project");

    Set<String> actual = Sets.newHashSet();
    for (ElementList<?> list : overviewService.getElementLists().getLists()) {
      actual.add(list.getHtmlId());
    }

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.OverviewService#getElementLists()}
   */
  @Test
  public void testGetElementListsHierarchical() {
    BusinessObject bd1 = testDataHelper.createBusinessObject(TEST_BO_NAME1, TEST_DESC);
    BusinessObject bd2 = testDataHelper.createBusinessObject(TEST_BO_NAME2, TEST_DESC);
    BusinessObject bd3 = testDataHelper.createBusinessObject(TEST_BO_NAME3, TEST_DESC);
    testDataHelper.addElementOf(bd2, bd3);

    boolean tested = false;
    for (ElementList<?> list : overviewService.getElementLists().getLists()) {
      if (list.getHtmlId().equals("businessobject")) {
        assertEquals(Lists.newArrayList(bd1, bd2), list.getElements());
        assertEquals(4, list.getTotalNumberOfElements());
        tested = true;
      }
    }
    assertTrue(tested);

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.OverviewService#getElementLists()}
   */
  @Test
  public void testGetElementListsInformationSystem() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is, TEST_RELEASE1);
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is, TEST_RELEASE2);
    InformationSystemRelease isr3 = testDataHelper.createInformationSystemRelease(is, TEST_RELEASE3);
    InformationSystemRelease isr4 = testDataHelper.createInformationSystemRelease(is, TEST_RELEASE4);
    InformationSystemRelease isr5 = testDataHelper.createInformationSystemRelease(is, TEST_RELEASE5);
    InformationSystemRelease isr6 = testDataHelper.createInformationSystemRelease(is, TEST_RELEASE6);
    InformationSystemRelease isr7 = testDataHelper.createInformationSystemRelease(is, TEST_RELEASE7);

    testDataHelper.createInformationSystemInterface(isr1, isr2, null, "");
    testDataHelper.createInformationSystemInterface(isr1, isr3, null, "");
    testDataHelper.createInformationSystemInterface(isr1, isr4, null, "");
    testDataHelper.createInformationSystemInterface(isr1, isr5, null, "");
    testDataHelper.createInformationSystemInterface(isr1, isr6, null, "");
    testDataHelper.createInformationSystemInterface(isr1, isr7, null, "");
    testDataHelper.createInformationSystemInterface(isr2, isr3, null, "");
    testDataHelper.createInformationSystemInterface(isr2, isr4, null, "");
    testDataHelper.createInformationSystemInterface(isr3, isr4, null, "");
    testDataHelper.createInformationSystemInterface(isr3, isr5, null, "");
    testDataHelper.createInformationSystemInterface(isr3, isr6, null, "");
    testDataHelper.createInformationSystemInterface(isr4, isr5, null, "");
    testDataHelper.createInformationSystemInterface(isr4, isr6, null, "");
    testDataHelper.createInformationSystemInterface(isr5, isr6, null, "");
    testDataHelper.createInformationSystemInterface(isr6, isr7, null, "");

    assertEquals(6, isr1.getInterfacedInformationSystemReleases().size());
    assertEquals(3, isr2.getInterfacedInformationSystemReleases().size());
    assertEquals(5, isr3.getInterfacedInformationSystemReleases().size());
    assertEquals(5, isr4.getInterfacedInformationSystemReleases().size());
    assertEquals(4, isr5.getInterfacedInformationSystemReleases().size());
    assertEquals(5, isr6.getInterfacedInformationSystemReleases().size());
    assertEquals(2, isr7.getInterfacedInformationSystemReleases().size());

    boolean tested = false;
    for (ElementList<?> list : overviewService.getElementLists().getLists()) {
      if (list.getHtmlId().equals("informationsystem")) {
        assertEquals(Lists.newArrayList(isr1, isr3, isr4, isr6, isr5), list.getElements());
        assertEquals(7, list.getTotalNumberOfElements());
        tested = true;
      }
    }
    assertTrue(tested);
  }

}

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

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;


/**
 * Integration test for various optimistic locking cases.
 */
public class OptimisticLockTest extends BaseTransactionalTestSupport {

  @Autowired
  private InformationSystemReleaseService isrService;
  @Autowired
  private TestDataHelper2  testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Tests if after ISR update the version is increased. 
   */
  @Test
  public void testSaveISR() {
    InformationSystem is = testDataHelper.createInformationSystem("IS");
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, "1.0");
    BusinessMapping bm = createBM(isr);
    BusinessUnit bu = testDataHelper.createBusinessUnit("BU2", "");

    Integer isrVersionBefore = isr.getOlVersion();
    Integer isVersionBefore = is.getOlVersion();
    Integer bmVersionBefore = bm.getOlVersion();

    commit();
    beginTransaction();
    isr.setVersion("2.0");
    bm.setBusinessUnit(bu);
    isrService.saveOrUpdate(isr);

    assertTrue(isr.getOlVersion().intValue() > isrVersionBefore.intValue());
    assertTrue(is.getOlVersion().intValue() > isVersionBefore.intValue());
    assertTrue(bm.getOlVersion().intValue() > bmVersionBefore.intValue());
  }

  private BusinessMapping createBM(InformationSystemRelease isr) {
    BusinessProcess bp = testDataHelper.createBusinessProcess("BP", "");
    BusinessUnit bu = testDataHelper.createBusinessUnit("BU", "");
    Product product = testDataHelper.createProduct("PRODUCT", "");

    return testDataHelper.createBusinessMapping(isr, bp, bu, product);
  }
}
